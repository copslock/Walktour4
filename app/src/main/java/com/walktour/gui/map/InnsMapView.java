package com.walktour.gui.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dinglicom.dataset.DatasetManager;
import com.dinglicom.dataset.logic.PointIndexChangeLinstener;
import com.innsmap.InnsMap.INNSMapView;
import com.innsmap.InnsMap.location.bean.INNSMapLocation;
import com.innsmap.InnsMap.map.sdk.domain.overlay.BubbleWindow;
import com.innsmap.InnsMap.map.sdk.domain.overlay.Overlayer;
import com.innsmap.InnsMap.map.sdk.domain.overlay.PointOverlayer;
import com.innsmap.InnsMap.map.sdk.listeners.OnINNSMapClickListener;
import com.innsmap.InnsMap.map.sdk.listeners.OverlayerClickListener;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.base.util.DateUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ParameterSetting;
import com.walktour.gui.R;
import com.walktour.model.LocusParamInfo;
import com.walktour.model.MapEvent;
import com.walktour.model.Parameter;
import com.walktour.service.innsmap.InnsmapTestService;
import com.walktour.service.innsmap.OnInnsLocationChangedEvent;
import com.walktour.service.innsmap.OnReceiveInnsLocationEvent;
import com.walktour.service.innsmap.model.InnsLocationSetManager;
import com.walktour.service.innsmap.model.LocationWithMeasParameter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 寅时室内测试地图显示
 *
 * @author jianchao.wang
 */
public class InnsMapView extends INNSMapView implements PointIndexChangeLinstener, OnINNSMapClickListener, OverlayerClickListener {
    /**
     * 日志标识
     */
    private static final String TAG = "InnsMapView";

    /**
     * SharedPreferences记录是否手动打点标志的KEY
     */
    public static final String KEY_INNS_IS_MANUAL_MARK = "inns_is_manual_mark";
    /**
     * 数据管理对象
     */
    private DatasetManager mDatasetManager;
    /**
     * 设置管理对象
     */
    private ParameterSetting mParameterSet;
    /**
     * 参数列表
     */
    private List<Parameter> parameterms;
    /**
     * 显示轨迹,且表示当前轨迹的Index
     */
    private int showIndex = 0;
    /**
     * 上次绘制的GPS点的位置
     */
    private int mPos = -1;
    /**
     * 采样点描述
     */
    private LinearLayout mLocasDesc;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 密度
     */
    private float mDensity;
    /**
     * 弹出的泡泡
     */
    private BubbleWindow mBubbleWindow;

    /**
     * 上次打点位置
     */
    private int mPrePointIndex = -1;
//    /**
//     * 点击的轨迹点
//     */
//    private MapEvent clickItem;
    /**
     * 广播接收器
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            onPointIndexChange(0, false);
        }
    };

    public InnsMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public InnsMapView(Context context) {
        super(context);
        this.init(context);
    }

    public InnsMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    private void init(Context context) {
        this.mContext = context;
        this.mBubbleWindow = new BubbleWindow();
        this.mDatasetManager = DatasetManager.getInstance(context.getApplicationContext());
        mParameterSet = ParameterSetting.getInstance();
        mParameterSet.initMapLocusShape(context.getApplicationContext());
        this.mLocasDesc = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.poi_descr, null);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(3);
        // 添加外圈
        mPaint.setAntiAlias(true);
        this.setOnINNSMapClickListener(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveInnsLocationEvent(OnReceiveInnsLocationEvent event) {
        List<LocationWithMeasParameter> locationList = InnsLocationSetManager.getInstance().getLocationList();
        if (null == locationList || locationList.isEmpty()) {
            return;
        }
        if (this.mPrePointIndex > locationList.size() - 1) {
            this.mPrePointIndex = -1;
            this.removeAllOverlayer();
        }
        for (int i = (this.mPrePointIndex + 1); i < locationList.size(); i++) {
            float radius = this.getLocusRadius();
            LocationWithMeasParameter locationParameter = locationList.get(i);
            INNSMapLocation location = locationParameter.getInnsMapLocation();
            PointOverlayer overlay = new PointOverlayer();
            overlay.setColor(Color.BLACK);
            overlay.setRadius(radius);
            overlay.setPointF(new PointF(location.getX(), location.getY()));
            overlay.setId(String.valueOf(locationParameter.getTime()));
//            overlay.setResponseListener(true); // 设置当前覆盖物响应点击事件
//            overlay.setOnClickListener();
            this.addOverlayer(overlay);
        }
        mPrePointIndex = locationList.size() - 1;
    }

    /**
     * 用于展示当前定位点（箭头标志）
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveInnsLocationChangedEvent(OnInnsLocationChangedEvent event){
        INNSMapLocation location = event.getLocation();
        PointF currLoc = new PointF(location.getX(),location.getY());
        setPositionPoint(currLoc);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPrePointIndex = -1;
    }

    @Override
    public void onPointIndexChange(int pointIndex, boolean isProgressSkip) {
        this.checkParameterChange();
        float radius = this.getLocusRadius();
        List<MapEvent> gpsLocal = new ArrayList<>(TraceInfoInterface.traceData.getGpsLocas());
        if (gpsLocal.isEmpty()) {
            LogUtil.d(TAG, "pointLocus is Empty!");
            return;
        }
        LogUtil.d(TAG, "drawPointLocus start!");
        if (this.mPos > gpsLocal.size() - 1) {
            this.mPos = -1;
            this.removeAllOverlayer();
        }
        for (int i = this.mPos + 1; i < gpsLocal.size(); i++) {
            MapEvent event = gpsLocal.get(i);
            LogUtil.d(TAG, "drawPointLocus x:" + event.getLongitude() + ";y:" + event.getLatitude() + " , printTimeMillis:" + DateUtil.formatDate("HH:mm:ss.SSS", new Date(System.currentTimeMillis())));
            PointOverlayer overlayer = new PointOverlayer();
            overlayer.setColor(event.getColor());
            overlayer.setRadius(radius);
            overlayer.setPointF(new PointF((float) event.getLongitude(), (float) event.getLatitude()));
            overlayer.setId(String.valueOf(event.getId()));
            overlayer.setResponseListener(true); // 设置当前覆盖物响应点击事件

            overlayer.setOnClickListener(this);
            this.addOverlayer(overlayer);
        }

        this.mPos = gpsLocal.size() - 1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    /**
     * 注册广播接收器
     */
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(InnsmapTestService.ACTION_REFLASH_VIEW);
        super.getContext().registerReceiver(this.mReceiver, filter);
    }

    /**
     * 绘制选中的轨迹点
     *
     * @param canvas 画布
     */
//    private void drawSelectItem(Canvas canvas) {
//        int pointX = (int) clickItem.getX();
//        int pointY = (int) clickItem.getY();
//        if (pointX < 0 || pointY <= 0)
//            return;
//        float radius = this.getLocusRadius();
//        mPaint.setColor(this.clickItem.getColor());
//        mPaint.setStyle(Paint.Style.FILL);
//        canvas.drawCircle(pointX + showIndex * radius * 3, pointY, radius, mPaint);
//        final TextView title = (TextView) this.mLocasDesc.findViewById(R.id.poi_title);
//        final TextView descr = (TextView) this.mLocasDesc.findViewById(R.id.descr);
//        final TextView coord = (TextView) this.mLocasDesc.findViewById(R.id.coord);
//
//        title.setText("Location");
//        descr.setText(this.clickItem.getMapPopInfo());
//        if (parameterms == null || parameterms.isNullOrEmpty()) {
//            coord.setVisibility(View.GONE);
//        } else {
//            LocusParamInfo locusParamInfo = null;
//            locusParamInfo = this.clickItem.getParamInfoMap().get(parameterms.get(showIndex).getShowName());
//            if (locusParamInfo == null || locusParamInfo.paramName == null || locusParamInfo.value == -9999) {
//                coord.setVisibility(View.GONE);
//            } else {
//                coord.setVisibility(View.VISIBLE);
//                coord.setText(locusParamInfo.paramName + ":" + locusParamInfo.value);
//            }
//        }
//        this.mLocasDesc.measure(0, 0);
//        this.mLocasDesc.layout(0, 0, this.mLocasDesc.getMeasuredWidth(), this.mLocasDesc.getMeasuredHeight());
//        canvas.save();
//        canvas.translate(pointX - this.mLocasDesc.getMeasuredWidth() / 2 + showIndex * radius * 3,
//                pointY - 4 * mDensity - this.mLocasDesc.getMeasuredHeight());
//        this.mLocasDesc.draw(canvas);
//        canvas.restore();
//    }

    /**
     * 判断当前参数是否有更改
     *
     * @return
     */
    private void checkParameterChange() {
        List<Parameter> params = mParameterSet
                .getCheckedParamertersByNet(MyPhoneState.getInstance().getCurrentNetForParam(this.mContext));
        if (params == null || params.size() == 0 || this.parameterms == null || this.parameterms.size() != params.size()) {
//            this.clickItem = null;
            this.showIndex = 0;
            this.parameterms = params;
            return;
        }
        int count = 0;
        for (Parameter param : params) {
            for (Parameter param1 : this.parameterms) {
                if (param.getId().equals(param1.getId())) {
                    count++;
                }
            }
        }
        if (params.size() != count) {
//            this.clickItem = null;
            this.showIndex = 0;
            this.parameterms = params;
            return;
        }
    }

    /**
     * 获取轨迹的半径大小<BR>
     * [功能详细描述]
     */
    private int getLocusRadius() {
        float radius = 8 * mDensity;
        switch (mParameterSet.getLocusSize()) {
            case 0:
                radius = 12 * mDensity;
                break;
            case 1:
                radius = 8 * mDensity;
                break;
            case 2:
                radius = 4 * mDensity;
                break;
            default:
                break;
        }
        return (int) radius;
    }

    /**
     * 获取点中的轨迹点
     *
     * @param click 点击坐标
     * @return
     */
    private MapEvent getClickPoint(Point click) {
        float radius = this.getLocusRadius();
        if (this.parameterms == null || this.parameterms.isEmpty())
            return null;
        final Rect rect = new Rect();
        int left, right, top, bottom;
        int pointX, pointY;
        List<MapEvent> list = new ArrayList<>(TraceInfoInterface.traceData.getGpsLocas());
        if (list.isEmpty())
            return null;
        for (int i = list.size() - 1; i >= 0; i--) {
            MapEvent event = list.get(i);
            pointX = (int) event.getX();
            pointY = (int) event.getY();
            for (int j = 0; j < parameterms.size(); j++) {
                left = (int) (pointX - radius + j * radius * 3);
                right = (int) (pointX + radius + j * radius * 3);
                top = (int) (pointY - radius);
                bottom = (int) (pointY + radius);
                rect.set(left, top, right, bottom);
                if (rect.contains(click.x, click.y)) {
                    showIndex = j;
                    if (DatasetManager.isPlayback) {
                        this.mDatasetManager.getPlaybackManager().setSkipIndex(event.getBeginPointIndex());
                    }
                    return event;
                }
            }
        }
        return null;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // this.mDatasetManager =
        // DatasetManager.getInstance(this.getContext().getApplicationContext());
        // this.mDatasetManager.addPointIndexChangeListener(this);
        this.mParameterSet = ParameterSetting.getInstance();
        this.mParameterSet.initMapLocusShape(this.getContext());
        mDensity = this.getContext().getResources().getDisplayMetrics().density;
        this.registerReceiver();
        EventBus.getDefault().register(this);
        this.mPos = -1;
        this.mPrePointIndex = -1;
        //通知view绘制点集合里的点
        onReceiveInnsLocationEvent(null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // this.mDatasetManager.removePointIndexChangeListener(this);
        this.getContext().unregisterReceiver(this.mReceiver);
    }

    @Override
    public void onMapClick(float x, float y) {
        LogUtil.d(TAG, "---onMapClick--- x = " + x + " , y = " + y);
        if (ApplicationModel.getInstance().isTestJobIsRun() && MapFactory.getMapData().isSetPoint()) {
            /*int flag = 0x30002;
            EventBytes.Builder(getContext()).addInteger(1).addInteger(0)
                    .addInteger(0)
                    .addDouble(y).addDouble(x)
                    .addSingle(0).addSingle(0).addSingle(0)
                    .addInteger(3).addSingle(0).writeGPSToRcu(flag);
            this.getContext().sendBroadcast(new Intent(InnsmapTestService.ACTION_REFLASH_VIEW));*/
            INNSMapLocation location = new INNSMapLocation();
            location.setX(x);
            location.setY(y);
            InnsLocationSetManager.getInstance().addLocation(new LocationWithMeasParameter(location, InnsmapTestService.getCellMeasMapEventParameters(), System.currentTimeMillis()));
            EventBus.getDefault().post(new OnReceiveInnsLocationEvent());
            SharePreferencesUtil.getInstance(getContext()).saveBoolean(KEY_INNS_IS_MANUAL_MARK, true);
        }
    }

    @Override
    public void onClick(Overlayer overlayer) {
        List<MapEvent> list = new ArrayList<>(TraceInfoInterface.traceData.getGpsLocas());
        if (list.isEmpty())
            return;
        for (MapEvent event : list) {
            if (String.valueOf(event.getId()).equals(overlayer.getId())) {
                this.showSelectEvent(event);
                break;
            }
        }
    }

    /**
     * 显示点击的轨迹点
     *
     * @param event 轨迹点对象
     */
    private void showSelectEvent(MapEvent event) {
        final TextView title = (TextView) this.mLocasDesc.findViewById(R.id.poi_title);
        final TextView descr = (TextView) this.mLocasDesc.findViewById(R.id.descr);
        final TextView coord = (TextView) this.mLocasDesc.findViewById(R.id.coord);

        title.setText("Location");
        descr.setText(event.getMapPopInfo());
        if (parameterms == null || parameterms.isEmpty()) {
            coord.setVisibility(View.GONE);
        } else {
            LocusParamInfo locusParamInfo;
            locusParamInfo = event.getParamInfoMap().get(parameterms.get(showIndex).getShowName());
            if (locusParamInfo == null || locusParamInfo.paramName == null || locusParamInfo.value == -9999) {
                coord.setVisibility(View.GONE);
            } else {
                coord.setVisibility(View.VISIBLE);
                coord.setText(locusParamInfo.paramName + ":" + locusParamInfo.value);
            }
        }
        this.mLocasDesc.measure(0, 0);
        this.mLocasDesc.layout(0, 0, this.mLocasDesc.getMeasuredWidth(), this.mLocasDesc.getMeasuredHeight());
        mBubbleWindow.setContext("文字内容");   // 使用默认样式并设置文字内容
        mBubbleWindow.setPointF(new PointF((float) event.getLongitude(), (float) event.getLatitude()));  // 单位: m
        mBubbleWindow.setView(this.mLocasDesc);    // 使用自定义样式
        this.addOverlayer(mBubbleWindow);
    }
}

