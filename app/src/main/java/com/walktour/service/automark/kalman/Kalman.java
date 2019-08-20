package com.walktour.service.automark.kalman;

/**
 * 卡尔曼滤波算法实现
 */

public class Kalman {
    /**
     * 系统参数
     */
    private Matrix A;
    /**
     * 系统参数
     */
    private Matrix B;
    /**
     * 测量系统的参数
     */
    private Matrix H;
    /**
     * 系统噪音
     */
    private Matrix Q;
    /**
     * 测量噪音
     */
    private Matrix R;
    /**
     * 最优预测值
     */
    public Matrix X;
    /**
     * 系统的控制量
     */
    private Matrix U;
    /**
     * 预测值协方差
     */
    private Matrix P;

    /**
     * @param a 系统参数
     * @param b 系统参数
     * @param h 测量系统的参数
     * @param u 系统的控制量
     */
    Kalman(Matrix a, Matrix b, Matrix h, Matrix u) {
        this.A = a;
        this.B = b;
        this.H = h;
        this.U = u;
    }

    /**
     * 初始化值
     *
     * @param x 初始预测值
     * @param p 初始协方差
     * @param q 系统噪音
     * @param r 测量噪音
     */
    public void init(Matrix x, Matrix p, Matrix q, Matrix r) {
        this.X = x;
        this.P = p;
        this.Q = q;
        this.R = r;
    }

    /**
     * 进行计算
     *
     * @param z 当前的测量值
     */
    public double calculate(double z) {
        Matrix x = this.calculate(new Matrix(1, 1, z));
        if (x == null)
            return 0;
        return x.getValue(0, 0);
    }

    /**
     * 进行计算
     *
     * @param Z 当前的测量值
     */
    public Matrix calculate(Matrix Z) {
        if (this.X == null)
            return null;
        // X(k|k-1) = A * X(k-1|k-1) + B * U(k)
        Matrix X_k_k1 = A.multiplication(this.X).addition(B.multiplication(this.U));
        // P(k|k-1) = A * P(k-1|k-1) * A' + Q
        Matrix P_k_k1 = A.multiplication(this.P).multiplication(A.transposition()).addition(this.Q);
        // Kg(k) = P(k|k-1) * H' / (H * P(k|k-1) * H' + R)
        // 卡尔曼增益
        Matrix Kg = P_k_k1.multiplication(this.H.transposition())
                .division(this.H.multiplication(P_k_k1).multiplication(this.H.transposition()).addition(this.R));
        // P(k|k) = (1 - Kg(k) * H) * P(k|k-1)
        this.P = Kg.multiplication(this.H).beSubtraction(1).multiplication(P_k_k1);
        // X(k|k) = X(k|k-1) + Kg(k) * (Z(k) - H * X(k|k-1))
        this.X = X_k_k1.addition(Kg.multiplication(Z.subtraction(this.H.multiplication(X_k_k1))));
        return this.X;
    }

}
