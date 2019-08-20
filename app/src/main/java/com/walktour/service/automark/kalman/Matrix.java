package com.walktour.service.automark.kalman;

/**
 * 矩阵对象
 */

public class Matrix {
    /**
     * 矩阵行数
     */
    private int m;
    /**
     * 矩阵列数
     */
    private int n;
    /**
     * 矩阵数据
     */
    private double[][] mValues;

    /**
     * @param m         行数
     * @param n         列数
     * @param initValue 初始值
     */
    public Matrix(int m, int n, double initValue) {
        this.m = m;
        this.n = n;
        this.mValues = new double[this.m][this.n];
        for (int i = 0; i < this.m; i++) {
            for (int j = 0; j < this.n; j++) {
                this.mValues[i][j] = initValue;
            }
        }
    }

    public int getN() {
        return n;
    }

    public int getM() {

        return m;
    }

//	public double[][] getmValues() {
//
//		return mValues;
//	}

    /**
     * 矩阵相加
     *
     * @param b 被加矩阵
     * @return 矩阵
     */
    Matrix addition(Matrix b) {
        if (this.m != b.m || this.n != b.n) {
            System.out.println("matrix can't addition");
            return this;
        }
        Matrix c = new Matrix(this.m, this.n, 0);
        for (int i = 0; i < this.m; i++) {
            for (int j = 0; j < this.n; j++) {
                c.mValues[i][j] = this.mValues[i][j] + b.mValues[i][j];
            }
        }
        return c;
    }

    /**
     * 矩阵相减
     *
     * @param b 被减矩阵
     * @return 矩阵
     */
    Matrix subtraction(Matrix b) {
        if (this.m != b.m || this.n != b.n) {
            System.out.println("matrix can't subtraction");
            return this;
        }
        Matrix c = new Matrix(this.m, this.n, 0);
        for (int i = 0; i < this.m; i++) {
            for (int j = 0; j < this.n; j++) {
                c.mValues[i][j] = this.mValues[i][j] - b.mValues[i][j];
            }
        }
        return c;
    }

    /**
     * 设置矩阵指定位置的值
     *
     * @param i     行号
     * @param j     列号
     * @param value 值
     */
    public void setValue(int i, int j, double value) {
        if (i >= 0 && j >= 0 && i < this.m && j < this.n) {
            this.mValues[i][j] = value;
        }
    }

    /**
     * 获取矩阵指定位置的值
     *
     * @param i 行号
     * @param j 列号
     * @return 值
     */
    public double getValue(int i, int j) {
        if (i >= 0 && j >= 0 && i < this.m && j < this.n) {
            return this.mValues[i][j];
        }
        return 0;
    }

    /**
     * 矩阵被减
     *
     * @param value 用来减去当前矩阵的值
     * @return 矩阵
     */
    Matrix beSubtraction(double value) {
        Matrix c = new Matrix(this.m, this.n, 0);
        for (int i = 0; i < this.m; i++) {
            for (int j = 0; j < this.n; j++) {
                c.mValues[i][j] = value - this.mValues[i][j];
            }
        }
        return c;
    }

//    /**
//     * 矩阵数乘
//     *
//     * @param value 被乘数
//     * @return 矩阵
//     */
//    public Matrix multiplication(double value) {
//        Matrix c = new Matrix(this.m, this.n, 0);
//        for (int i = 0; i < this.m; i++) {
//            for (int j = 0; j < this.n; j++) {
//                c.mValues[i][j] = value * this.mValues[i][j];
//            }
//        }
//        return c;
//    }

    /**
     * 矩阵转置
     *
     * @return 矩阵
     */
    Matrix transposition() {
        Matrix c = new Matrix(this.n, this.m, 0);
        for (int i = 0; i < this.n; i++) {
            for (int j = 0; j < this.m; j++) {
                c.mValues[i][j] = this.mValues[j][i];
            }
        }
        return c;

    }

    /**
     * 矩阵相乘
     *
     * @param b 被乘矩阵
     * @return 矩阵
     */
    Matrix multiplication(Matrix b) {
        if (this.n != b.m && this.m != b.n) {
            System.out.println("matrix can't multiplication");
            return this;
        }
        Matrix c;
        if (this.n == b.m) {
            c = new Matrix(this.m, b.n, 0);
            for (int i = 0; i < c.m; i++) {
                for (int j = 0; j < c.n; j++) {
                    for (int k = 0; k < this.n; k++) {
                        c.mValues[i][j] += this.mValues[i][k] * b.mValues[k][j];
                    }
                }
            }
        } else {// this.m == b.n
            c = new Matrix(b.m, this.n, 0);
            for (int i = 0; i < c.m; i++) {
                for (int j = 0; j < c.n; j++) {
                    for (int k = 0; k < this.m; k++) {
                        c.mValues[i][j] += b.mValues[i][k] * this.mValues[k][j];
                    }
                }
            }
        }
        return c;
    }

    /**
     * 矩阵相除
     *
     * @param b 被除矩阵
     * @return 矩阵
     */
    public Matrix division(Matrix b) {
        Matrix c = b.transposition();
        return this.multiplication(c);
    }

}
