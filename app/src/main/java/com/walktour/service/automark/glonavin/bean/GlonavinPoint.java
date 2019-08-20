package com.walktour.service.automark.glonavin.bean;

/**
 * Created by Yi.Lin on 2018/4/23.
 * <p>
 * 格纳微打点对象
 */

public class GlonavinPoint {

    /**
     * x坐标
     */
    private float x;
    /**
     * y坐标
     */
    private float y;
    /**
     * z坐标
     */
    private float z;


    /**
     * 俯仰角 (4B float) (范围 为-π～π)
     */
    private float angleOfPitch ;
    /**
     *  横滚角(4B float) (范围 为-π～π)
     */
    private float angleOfRoll;
    /**
     *   航向角 (4B float) (范围 为-π～π)
     */
    private float angle;
    /**
     *  模块电量 (1B) (范围 为-π～π)
     */
    private int power;
    /**
     *  模块信号强 度(1B)
     */
    private int signalPower;
    /**
     * 当前楼层
     */
    private int currentFloor;


    /**
     *   上下楼状  0 表示无上 下楼， 1 表示上 手扶楼梯， 2 表 示下手扶楼 梯， 3 表示电梯 上行， 4 表示电 梯下行.
     */
    private int upDownFloor;
    /**
     *   功能位(1B) CRC 。功能 位定义：正常 状态下为 0，1 表示开始模块 自校准， 2 表示 模块自校准结 束。
     */
    private int CRC;
    private int color;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public float getAngleOfPitch() {
        return angleOfPitch;
    }

    public void setAngleOfPitch(float angleOfPitch) {
        this.angleOfPitch = angleOfPitch;
    }

    public float getAngleOfRoll() {
        return angleOfRoll;
    }

    public void setAngleOfRoll(float angleOfRoll) {
        this.angleOfRoll = angleOfRoll;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getSignalPower() {
        return signalPower;
    }

    public void setSignalPower(int signalPower) {
        this.signalPower = signalPower;
    }

    public int getUpDownFloor() {
        return upDownFloor;
    }

    public void setUpDownFloor(int upDownFloor) {
        this.upDownFloor = upDownFloor;
    }

    public int getCRC() {
        return CRC;
    }

    public void setCRC(int CRC) {
        this.CRC = CRC;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public GlonavinPoint(float x, float y, float angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public GlonavinPoint() {
    }

    @Override
    public String toString() {
        return "GlonavinPoint{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", angleOfPitch=" + angleOfPitch +
                ", angleOfRoll=" + angleOfRoll +
                ", angle=" + angle +
                ", power=" + power +
                ", signalPower=" + signalPower +
                ", currentFloor=" + currentFloor +
                ", upDownFloor=" + upDownFloor +
                ", CRC=" + CRC +
                ", color=" + color +
                '}';
    }
}
