package com.walktour.gui.task.activity.scannertsma.model;

import java.util.ArrayList;

/**
 * @author jinfeng.xie
 * @data 2019/2/19
 */
public class ScanSettingModel {
    private int ResultBufferDepth;//ResultBufferDepth：缓存，范围为1 -1024，Pioneer默认为1024；
    private int ReceiverIndex;//ReceiverIndex：扫频仪索引，暂不使用，固定赋0；
    private int FrontEndSelectionMask;//FrontEndSelectionMask：物理接收天线（天线1、天线2），Pioneer界面初始值为1；
    private int ValuePerSec; //ValuePerSec：扫频速率，单位Hz，Pioneer界面初始值为10Hz，因为配置项是每1000秒，所以传进来的配置需要乘上1000；
    private int DecodeOutputMode;//DecodeOutputMode：0 为实时显示，1 为缓存显示，Pioneer界面初始值为0；
    private int MeasurementMode;//MeasurementMode：测量模式，0为高速，1为定点，Pioneer界面初始值为0；
    private ArrayList<Channel> channels=new ArrayList<>();//ChannelCount：信道配置项个数，最大值为1024；
    private  int Band;//Band：暂不使用，固定赋0；
    private com.walktour.gui.task.activity.scannertsma.model.Demodulation Demodulation=new Demodulation();// 检波
    public ScanSettingModel(){

    };
    public ScanSettingModel(int resultBufferDepth, int receiverIndex, int frontEndSelectionMask, int valuePerSec,
                            int decodeOutputMode, int measurementMode, ArrayList<Channel> channels,
                            int band, com.walktour.gui.task.activity.scannertsma.model.Demodulation demodulation) {
        ResultBufferDepth = resultBufferDepth;
        ReceiverIndex = receiverIndex;
        FrontEndSelectionMask = frontEndSelectionMask;
        ValuePerSec = valuePerSec;
        DecodeOutputMode = decodeOutputMode;
        MeasurementMode = measurementMode;
        this.channels = channels;
        Band = band;
        Demodulation = demodulation;
    }

    public int getResultBufferDepth() {
        return ResultBufferDepth;
    }

    public void setResultBufferDepth(int resultBufferDepth) {
        ResultBufferDepth = resultBufferDepth;
    }

    public int getReceiverIndex() {
        return ReceiverIndex;
    }

    public void setReceiverIndex(int receiverIndex) {
        ReceiverIndex = receiverIndex;
    }

    public int getFrontEndSelectionMask() {
        return FrontEndSelectionMask;
    }

    public void setFrontEndSelectionMask(int frontEndSelectionMask) {
        FrontEndSelectionMask = frontEndSelectionMask;
    }

    public int getValuePerSec() {
        return ValuePerSec;
    }

    public void setValuePerSec(int valuePerSec) {
        ValuePerSec = valuePerSec;
    }

    public int getDecodeOutputMode() {
        return DecodeOutputMode;
    }

    public void setDecodeOutputMode(int decodeOutputMode) {
        DecodeOutputMode = decodeOutputMode;
    }

    public int getMeasurementMode() {
        return MeasurementMode;
    }

    public void setMeasurementMode(int measurementMode) {
        MeasurementMode = measurementMode;
    }

    public ArrayList<Channel> getChannels() {
        return channels;
    }

    public void setChannels(ArrayList<Channel> channels) {
        this.channels = channels;
    }

    public int getBand() {
        return Band;
    }

    public void setBand(int band) {
        Band = band;
    }

    public com.walktour.gui.task.activity.scannertsma.model.Demodulation getDemodulation() {
        return Demodulation;
    }

    public void setDemodulation(com.walktour.gui.task.activity.scannertsma.model.Demodulation demodulation) {
        Demodulation = demodulation;
    }

    @Override
    public String toString() {
        return "ScanSettingModel{" +
                "ResultBufferDepth=" + ResultBufferDepth +
                ", ReceiverIndex=" + ReceiverIndex +
                ", FrontEndSelectionMask=" + FrontEndSelectionMask +
                ", ValuePerSec=" + ValuePerSec +
                ", DecodeOutputMode=" + DecodeOutputMode +
                ", MeasurementMode=" + MeasurementMode +
                ", channels=" + channels +
                ", Band=" + Band +
                ", Demodulation=" + Demodulation +
                '}';
    }
}
