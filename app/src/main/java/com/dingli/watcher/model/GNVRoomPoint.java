package com.dingli.watcher.model;

import com.walktour.service.automark.glonavin.bean.GlonavinPoint;

public class GNVRoomPoint {

    public int SequenceID;            //序列号
    public float PoxX;              //x坐标
    public float PoxY;              //y坐标
    public float PoxZ;              //z坐标
    public float Pitch;             //俯仰角
    public float Roll;              //横滚角
    public float Bow;               //航向角
    public int ElectricRate;        //电量
    public int Signal;              //信号
    public int Floor;               //楼层
    public int Climb;               //上楼方式

    public GlonavinPoint chancetoGlonavinPointModel(){
        GlonavinPoint glonavinPoint=new GlonavinPoint();
        glonavinPoint.setX(PoxX);
        glonavinPoint.setY(PoxY);
        glonavinPoint.setZ(PoxZ);
        glonavinPoint.setSignalPower(Signal);
        glonavinPoint.setAngleOfPitch(Pitch);
        glonavinPoint.setAngleOfRoll(Roll);
        glonavinPoint.setAngle(Bow);
        glonavinPoint.setPower(ElectricRate);
        glonavinPoint.setCurrentFloor(Floor);
        glonavinPoint.setUpDownFloor(Climb);
        return glonavinPoint;
    }
    public int getSequenceID() {
        return SequenceID;
    }

    public void setSequenceID(int sequenceID) {
        SequenceID = sequenceID;
    }

    public float getPoxX() {
        return PoxX;
    }

    public void setPoxX(float poxX) {
        PoxX = poxX;
    }

    public float getPoxY() {
        return PoxY;
    }

    public void setPoxY(float poxY) {
        PoxY = poxY;
    }

    public float getPoxZ() {
        return PoxZ;
    }

    public void setPoxZ(float poxZ) {
        PoxZ = poxZ;
    }

    public float getPitch() {
        return Pitch;
    }

    public void setPitch(float pitch) {
        Pitch = pitch;
    }

    public float getRoll() {
        return Roll;
    }

    public void setRoll(float roll) {
        Roll = roll;
    }

    public float getBow() {
        return Bow;
    }

    public void setBow(float bow) {
        Bow = bow;
    }

    public int getElectricRate() {
        return ElectricRate;
    }

    public void setElectricRate(int electricRate) {
        ElectricRate = electricRate;
    }

    public int getSignal() {
        return Signal;
    }

    public void setSignal(int signal) {
        Signal = signal;
    }

    public int getFloor() {
        return Floor;
    }

    public void setFloor(int floor) {
        Floor = floor;
    }

    public int getClimb() {
        return Climb;
    }

    public void setClimb(int climb) {
        Climb = climb;
    }

    @Override
    public String toString() {
        return "GNVRoomPoint{" +
                "SequenceID=" + SequenceID +
                ", PoxX=" + PoxX +
                ", PoxY=" + PoxY +
                ", PoxZ=" + PoxZ +
                ", Pitch=" + Pitch +
                ", Roll=" + Roll +
                ", Bow=" + Bow +
                ", ElectricRate=" + ElectricRate +
                ", Signal=" + Signal +
                ", Floor=" + Floor +
                ", Climb=" + Climb +
                '}';
    }
}
