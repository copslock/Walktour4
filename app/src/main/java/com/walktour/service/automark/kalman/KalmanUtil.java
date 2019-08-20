package com.walktour.service.automark.kalman;

/**
 * 卡尔曼滤波算法工具类
 */
public class KalmanUtil {

    /**
     * 生成陀螺仪角速度算法对象
     *
     * @return 计算结果
     */
    public static Kalman createGyroscopeKalman() {
        Matrix a = new Matrix(1, 1, 1);
        Matrix b = new Matrix(1, 1, 1);
        Matrix h = new Matrix(1, 1, 1);
        Matrix u = new Matrix(1, 1, 0);
        Kalman kalman = new Kalman(a, b, h, u);
        Matrix x = new Matrix(1, 1, 0);
        Matrix p = new Matrix(1, 1, 0);
        Matrix q = new Matrix(1, 1, 0.1);
        Matrix r = new Matrix(1, 1, 0.1);
        kalman.init(x, p, q, r);
        return kalman;
    }

    /**
     * 生成加速度算法对象
     *
     * @return 计算结果
     */
    public static Kalman createAccelerationKalman() {
        Matrix a = new Matrix(1, 1, 1);
        Matrix b = new Matrix(1, 1, 1);
        Matrix h = new Matrix(1, 1, 1);
        Matrix u = new Matrix(1, 1, 0);
        Kalman kalman = new Kalman(a, b, h, u);
        Matrix x = new Matrix(1, 1, 0);
        Matrix p = new Matrix(1, 1, 0);
        Matrix q = new Matrix(1, 1, 0.1);
        Matrix r = new Matrix(1, 1, 0.1);
        kalman.init(x, p, q, r);
        return kalman;
    }

    /**
     * 生成步长算法对象
     *
     * @return 计算对象
     */
    public static Kalman createStepLengthKalman() {
        Matrix a = new Matrix(1, 1, 1);
        Matrix b = new Matrix(1, 1, 1);
        Matrix h = new Matrix(1, 1, 1);
        Matrix u = new Matrix(1, 1, 0);
        Kalman kalman = new Kalman(a, b, h, u);
        Matrix x = new Matrix(1, 1, 0.65);
        Matrix p = new Matrix(1, 1, 0);
        Matrix q = new Matrix(1, 1, 0.1);
        Matrix r = new Matrix(1, 1, 0.1);
        kalman.init(x, p, q, r);
        return kalman;
    }
}
