package com.dingli.watcher.model;

public class GNVAcceleration {

    public int SequenceID;            //序列号
    public float Acceleration;         //加速度
    public int StatusFlag;            //运行状态标志(0:机车静止, 1:减速, 2:匀速, 3:加速, 4:启动)
    public int ElectricRate;            //电量
    public int ValidFlag;      //0为无效，1就是有效

    public int getSequenceID() {
        return SequenceID;
    }

    public void setSequenceID(int sequenceID) {
        SequenceID = sequenceID;
    }

    public float getAcceleration() {
        return Acceleration;
    }

    public void setAcceleration(float acceleration) {
        Acceleration = acceleration;
    }

    public int getStatusFlag() {
        return StatusFlag;
    }

    public void setStatusFlag(int statusFlag) {
        StatusFlag = statusFlag;
    }

    public int getElectricRate() {
        return ElectricRate;
    }

    public void setElectricRate(int electricRate) {
        ElectricRate = electricRate;
    }

    public int getValidFlag() {
        return ValidFlag;
    }

    public void setValidFlag(int validFlag) {
        ValidFlag = validFlag;
    }

    @Override
    public String toString() {
        return "GNVAcceleration{" +
                "SequenceID=" + SequenceID +
                ", Acceleration=" + Acceleration +
                ", StatusFlag=" + StatusFlag +
                ", ElectricRate=" + ElectricRate +
                ", ValidFlag=" + ValidFlag +
                '}';
    }
}
