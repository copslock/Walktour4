package com.walktour.gui.task.activity.scannertsma.model;

import com.dingli.seegull.SeeGullFlags;
import com.walktour.base.util.LogUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TopN配置model
 *
 * @author zhihui.lian
 */
public class Pilot extends ScanTaskModel {
    private static final String TAG = "PilotModel";
    private int ResultBufferDepth;//ResultBufferDepth：缓存，范围为1 -1024，Pioneer默认为1024；
    private int ReceiverIndex;//ReceiverIndex：扫频仪索引，暂不使用，固定赋0；
    private int FrontEndSelectionMask;//FrontEndSelectionMask：物理接收天线（天线1、天线2），Pioneer界面初始值为1；
    private int ValuePerSec; //ValuePerSec：扫频速率，单位Hz，Pioneer界面初始值为10Hz，因为配置项是每1000秒，所以传进来的配置需要乘上1000；
    private int DecodeOutputMode;//DecodeOutputMode：0 为实时显示，1 为缓存显示，Pioneer界面初始值为0；
    private int MeasurementMode;//MeasurementMode：测量模式，0为高速，1为定点，Pioneer界面初始值为0；
    private ArrayList<Channel> channels = new ArrayList<>();//ChannelCount：信道配置项个数，最大值为1024；
    private int Band;//Band：暂不使用，固定赋0；
    private com.walktour.gui.task.activity.scannertsma.model.Demodulation Demodulation = new Demodulation();// 检波

    public Pilot() {

    }

    ;

    public Pilot(int resultBufferDepth, int receiverIndex, int frontEndSelectionMask, int valuePerSec,
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
   public void writeToXml(XmlSerializer serializer, TestSchemaType taskType) throws IOException {
        LogUtil.d(TAG, "PilotModelToXml");
        serializer.startTag(null, "Pilot");
        NodeValue(serializer,"Enable", this == null ? "0" : this.getEnable());
        NodeValue(serializer,"TaskName", "Pilot");
        NodeValue(serializer,"TaskType", taskType.name());
        serializer.startTag(null, "Setting");
        NodeValue(serializer,"ResultBufferDepth", this == null ? "0" : this.getResultBufferDepth());
        NodeValue(serializer,"FrontEndSelectionMask", this == null ? "0" : this.getFrontEndSelectionMask());
        NodeValue(serializer,"Band", this == null ? "0" : this.getBand());
        NodeValue(serializer,"ValuePerSec", this == null ? "0" : this.getValuePerSec());
        NodeValue(serializer,"DecodeOutputMode", this == null ? "0" : this.getDecodeOutputMode());
        NodeValue(serializer, "ChannelCount", channels == null ? "" + 0 : "" + channels.size());
        serializer.startTag("", "Channels");
        for (int i = 0; i < channels.size(); i++) {
            serializer.startTag("", "Channel_" + i);
            channels.get(i).writeToXml(serializer);
            serializer.endTag("", "Channel_" + i);
        }
        serializer.endTag("", "Channels");
        getDemodulation().writeToXml(serializer);
        serializer.endTag(null, "Setting");
        serializer.endTag(null, "Pilot");
    }

    @Override
    public void parserXml(XmlPullParser xmlParser, List<ScanTaskModel> testModelList) throws Exception {

            this.setScanType(SeeGullFlags.ScanTypes.eScanType_TopNPilot);
            this.setScanMode(0);
            int eventType = xmlParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("Pilot".equals(xmlParser.getName())) {
                            this.setTaskName("Pilot");
                        } else if ("TaskType".equals(xmlParser.getName())) {
                            TestSchemaType taskType = TestSchemaType.valueOf(xmlParser.nextText());

                            this.setTaskType(taskType.name());
                            this.setGroupName(taskType.getGroupName());
                            switch (taskType) {
                                case WCDMAPILOT:
                                    this.setProtocolCode(SeeGullFlags.ProtocolCodes.PROTOCOL_3GPP_WCDMA);
                                    this.setResultBufferDepth(1);
                                    this.setReceiverIndex(1);
                                    this.setFrontEndSelectionMask(1);
                                    this.setValuePerSec(1);
                                    this.setDecodeOutputMode(1);
                                    this.setMeasurementMode(1);
                                    this.setBand(0);
                                    break;
                                case CDMAPILOT:
                                    this.setProtocolCode(SeeGullFlags.ProtocolCodes.PROTOCOL_IS_2000_CDMA);
                                    this.setResultBufferDepth(1);
                                    this.setReceiverIndex(1);
                                    this.setFrontEndSelectionMask(1);
                                    this.setValuePerSec(1);
                                    this.setDecodeOutputMode(1);
                                    this.setMeasurementMode(1);
                                    this.setBand(0);
                                    break;
                                case TDSCDMAPILOT:
                                    this.setProtocolCode(SeeGullFlags.ProtocolCodes.PROTOCOL_TDSCDMA);
                                    this.setResultBufferDepth(1);
                                    this.setReceiverIndex(1);
                                    this.setFrontEndSelectionMask(1);
                                    this.setValuePerSec(1);
                                    this.setDecodeOutputMode(1);
                                    this.setMeasurementMode(1);
                                    this.setBand(0);
                                    break;
                                default:
                                    break;
                            }
                        } else if ("Enable".equals(xmlParser.getName())) {
                            this.setEnable(Integer.valueOf(xmlParser.nextText()));
                        } else if ("ResultBufferDepth".equals(xmlParser.getName())) {
                            this.setResultBufferDepth(Integer.valueOf(xmlParser.nextText()));
                        } else if ("FrontEndSelectionMask".equals(xmlParser.getName())) {
                            this.setFrontEndSelectionMask(Integer.valueOf(xmlParser.nextText()));
                        } else if ("ReceiverIndex".equals(xmlParser.getName())) {
                            this.setReceiverIndex(Integer.valueOf(xmlParser.nextText()));
                        } else if ("ValuePerSec".equals(xmlParser.getName())) {
                            this.setValuePerSec(Integer.valueOf(xmlParser.nextText()));
                        } else if ("DecodeOutputMode".equals(xmlParser.getName())) {
                            this.setDecodeOutputMode(Integer.valueOf(xmlParser.nextText()));
                        } else if ("MeasurementMode".equals(xmlParser.getName())) {
                            this.setMeasurementMode(Integer.valueOf(xmlParser.nextText()));
                        } else if ("Band".equals(xmlParser.getName())) {
                            this.setBand(Integer.valueOf(xmlParser.nextText()));
                        } else if ("Demodulation".equals(xmlParser.getName())) {
                            this.setDemodulation(com.walktour.gui.task.activity.scannertsma.model.Demodulation.parserXml(xmlParser));
                        } else if ("Channels".equals(xmlParser.getName())) {
                            this.setChannels(Channel.parserXml(xmlParser));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("Pilot".equals(xmlParser.getName())) {
                            testModelList.add(this);
                            return;
                        }
                        break;
                }
                eventType = xmlParser.next();
            }
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
