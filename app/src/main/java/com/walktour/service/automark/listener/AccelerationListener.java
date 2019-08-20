package com.walktour.service.automark.listener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import com.walktour.service.automark.AutoMarkManager;
import com.walktour.service.automark.kalman.Kalman;
import com.walktour.service.automark.kalman.KalmanUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 加速度传感器监听类
 *
 * @author jianchao.wang
 */
public class AccelerationListener extends BaseListener {
    /**
     * 判断脚步波峰和波谷的间隔时间阈值
     */
    private final static int THRESHOLD_TOP_BOTTOM_TIME_INTERAL = 100;
    /**
     * 判断脚步波峰和波谷的差值阈值
     */
    private final static float THRESHOLD_TOP_BOTTOM_VALUE_INTERAL = 0.7f;
    /**
     * 计算间隔时间（纳秒）
     */
    private final static int INTERAL_TIME = 50 * 1000 * 1000;
    /**
     * 将纳秒转化为秒
     */
    private final static float NS2S = 1.0f / 1000000000;
    /**
     * 加速度传感器
     */
    private Sensor mAccelerationSensor;
    /**
     * 加速度卡尔曼滤波算法计算类
     */
    private Kalman mAccelerationKalman;
    /**
     * 步长卡尔曼滤波算法类
     */
    private Kalman mStepLengthKalman;
    /**
     * 上一次获取传感器数据的时间
     */
    private long mLastSensorTime = 0;
    /**
     * 用于平滑加速度的数组
     */
    private List<Float> mAccelerations = new ArrayList<>();
    /**
     * 用于判断是否波峰波谷的数组,数组中是平滑值
     */
    private List<Float> mJudgeValues = new ArrayList<>();
    /**
     * 用于判断是否波峰波谷的数组,数组中是采样时间
     */
    private List<Long> mJudgeTimes = new ArrayList<>();
    /**
     * 积分值，用于计算当前步伐的移动速度
     */
    private float mIntegralValue;
    /**
     * 上一次波谷时间
     */
    private long mLastBottomTime;
    /**
     * 上一次波谷值
     */
    private float mLastBottomValue;
    /**
     * 是否发现波谷
     */
    private boolean isHasBottom;
    /**
     * 上一次波峰时间
     */
    private long mLastTopTime;
    /**
     * 上一次波峰值
     */
    private float mLastTopValue;
    /**
     * 是否发现波峰
     */
    private boolean isHasTop;
    /**
     * 上一次的加速度
     */
    private float mLastAcceleration;

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                if (this.mLastCalcTime == 0 || event.timestamp - this.mLastCalcTime > INTERAL_TIME) {
                    this.calculateAcceleration(event);
                    this.mLastCalcTime = event.timestamp;
                }
                break;
        }
    }

    /**
     * 计算加速度
     *
     * @param event 事件
     */
    private void calculateAcceleration(SensorEvent event) {
        long time = System.currentTimeMillis();
        float acceleration = this.calculateAcceleration(event.values[1], time);
        if (this.mLastSensorTime == 0) {
            this.mAccelerationKalman = KalmanUtil.createAccelerationKalman();
        } else {
            acceleration = (float) this.mAccelerationKalman.calculate(acceleration);
            // 得到两次检测到手机旋转的时间差（纳秒），并将其转化为秒
            final float dT = (event.timestamp - this.mLastSensorTime) * NS2S;
            this.mIntegralValue += Math.abs(this.mLastAcceleration) * dT
                    + Math.abs(Math.abs(acceleration) - Math.abs(this.mLastAcceleration)) * dT / 2;
        }
        if (this.mJudgeValues.size() == 3) {
            boolean isTop = this.judgeIsTop();
            boolean isBottom = this.judgeIsBottom();
            int isStep = this.judgeIsStep(acceleration);
            time = this.mJudgeTimes.get(1);
            acceleration = this.mJudgeValues.get(1);
            AutoMarkManager.getInstance().saveValuesToFile(time,
                    new double[]{acceleration, AutoMarkManager.getInstance().getCurrentOrientation(),
                            isStep == 1 ? AutoMarkManager.getInstance().getStepLength() : 0, isTop ? 1 : (isBottom ? -1 : 0), isStep,
                            isStep == 1 ? this.mLastTopValue : 0, isStep == 1 ? this.mLastBottomValue : 0});
        }
        this.mLastSensorTime = event.timestamp;
    }

    /**
     * 判断是否行走了一步
     *
     * @param acceleration 当前Y轴加速度
     */
    private int judgeIsStep(float acceleration) {
        int flag = 0;
        if (this.isHasBottom && this.isHasTop && acceleration > (this.mLastBottomValue + this.mLastTopValue) / 2) {
            if (this.mLastTopValue - this.mLastBottomValue > THRESHOLD_TOP_BOTTOM_VALUE_INTERAL) {
                if (this.mLastBottomTime - this.mLastTopTime > THRESHOLD_TOP_BOTTOM_TIME_INTERAL) {
                    flag = 1;
                    AutoMarkManager.getInstance().addNewStep(this.calculteStepLength());
                    AutoMarkManager.getInstance().setSpeed(this.mIntegralValue);
                } else
                    flag = -1;
            } else
                flag = -2;
            this.isHasBottom = false;
            this.isHasTop = false;
            this.mIntegralValue = 0;
        }
        this.mLastAcceleration = acceleration;
        return flag;
    }

    /**
     * 计算当前一步的步长
     */
    private double calculteStepLength() {
        // 计算当前步长
        double K = 0.5;
        double stepLength = K * Math.pow(this.mLastTopValue - this.mLastBottomValue, 1.0 / 4);
        if (this.mStepLengthKalman == null) {
            this.mStepLengthKalman = KalmanUtil.createStepLengthKalman();
            return stepLength;
        }
        return this.mStepLengthKalman.calculate(stepLength);
    }

    /**
     * 计算平滑加速度值
     *
     * @param acceleration 当前Y轴加速度
     * @param timestamp    当前时间（毫秒）
     * @return 加速度值
     */
    private float calculateAcceleration(float acceleration, long timestamp) {
        if (this.mAccelerations.size() == 3)
            this.mAccelerations.remove(0);
        this.mAccelerations.add(acceleration);
        if (this.mAccelerations.size() == 3) {
            float sum = 0;
            for (int i = 0; i < 3; i++) {
                sum += this.mAccelerations.get(i);
            }
            acceleration = sum / 3;
        }
        if (this.mJudgeValues.size() == 3) {
            this.mJudgeValues.remove(0);
            this.mJudgeTimes.remove(0);
        }
        this.mJudgeValues.add(acceleration);
        this.mJudgeTimes.add(timestamp);
        return acceleration;
    }

    /**
     * 判断是否波峰
     */
    private boolean judgeIsTop() {
        if (this.mJudgeValues.size() != 3)
            return false;
        float left = this.mJudgeValues.get(0);
        float center = this.mJudgeValues.get(1);
        float right = this.mJudgeValues.get(2);
        if (center > 0.5 && left < center && right < center) {
            if (!this.isHasTop || center > this.mLastTopValue) {
                this.mLastTopTime = this.mJudgeTimes.get(1);
                this.mLastTopValue = this.mJudgeValues.get(1);
                this.isHasTop = true;
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否波谷
     */
    private boolean judgeIsBottom() {
        if (this.mJudgeValues.size() != 3)
            return false;
        float left = this.mJudgeValues.get(0);
        float center = this.mJudgeValues.get(1);
        float right = this.mJudgeValues.get(2);
        if (left > center && right > center) {
            if (!this.isHasBottom || center < this.mLastBottomValue) {
                this.mLastBottomTime = this.mJudgeTimes.get(1);
                this.mLastBottomValue = this.mJudgeValues.get(1);
                this.isHasBottom = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public void registerListener(SensorManager manager) {
        if (this.isRegister)
            return;
        this.init();
        this.mAccelerationSensor = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        manager.registerListener(this, this.mAccelerationSensor, SensorManager.SENSOR_DELAY_GAME);
        this.isRegister = true;
    }

    /**
     * 初始化值
     */
    protected void init() {
        this.mAccelerationKalman = null;
        this.mStepLengthKalman = null;
        this.mAccelerations.clear();
        this.mJudgeValues.clear();
        this.mIntegralValue = 0;
        this.mLastAcceleration = 0;
        this.mLastBottomTime = 0;
        this.mLastBottomValue = 0;
        this.isHasBottom = false;
        this.mLastSensorTime = 0;
        this.mLastTopTime = 0;
        this.mLastTopValue = 0;
        this.isHasTop = false;
        this.mStepLengthKalman = null;
    }

    @Override
    public void unregisterListener(SensorManager manager) {
        if (!this.isRegister)
            return;
        manager.unregisterListener(this, this.mAccelerationSensor);
        this.mAccelerationSensor = null;
        this.isRegister = false;
    }

}
