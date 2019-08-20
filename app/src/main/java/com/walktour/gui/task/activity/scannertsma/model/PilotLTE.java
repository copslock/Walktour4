package com.walktour.gui.task.activity.scannertsma.model;

import com.walktour.base.util.LogUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * EtopNModel任务配置model
 *
 * @author zhihui.lian
 */

/**
 * <LTE_Pilot>
 <Enable>1</Enable>
 <LTESetting>
 <ResultBufferDepth>1024</ResultBufferDepth>
 <FrontEndSelectionMask>1</FrontEndSelectionMask>
 <Band>0</Band>
 <MeasurementMode>0</MeasurementMode>
 <ValuePerSec>10000</ValuePerSec>
 <DecodeOutputMode>1</DecodeOutputMode>

 <RSSISetting>
 <FrontEndSelectionMask>1</FrontEndSelectionMask>
 <RssiMeasTimeInMs>1</RssiMeasTimeInMs>
 <RssiMeasMode>4</RssiMeasMode>
 </RSSISetting>

 <WBSetting>
 <FrontEndSelectionMask>1</FrontEndSelectionMask>
 <MeaRate>10000</MeaRate>
 <Mode>1</Mode>
 <ShowMode>2</ShowMode>
 </WBSetting>

 <!-- MaxCount: 1024 -->
 <ChannelCount>1</ChannelCount>
 <LTEChannel>
 <LTEChannel_0>
 <Band>1318</Band>
 <EARFCN>37900</EARFCN>
 <IsUL>0</IsUL>
 <BandWidth>0</BandWidth>
 <FrameStructureType>2</FrameStructureType>
 <MIMOTode>0</MIMOTode>
 <PCI>0</PCI>
 <DlaaMode>0</DlaaMode>
 </LTEChannel_0>
 </LTEChannel>

 <!-- MaxCount: 32 -->
 <L3Count>3</L3Count>
 <Demodulation>
 <Demodulation_0>
 <FrontEndSelectionMask>1</FrontEndSelectionMask>
 <SINRtoThreshold>0</SINRtoThreshold>
 <L3Message>10</L3Message>
 </Demodulation_0>
 <Demodulation_1>
 <FrontEndSelectionMask>1</FrontEndSelectionMask>
 <SINRtoThreshold>0</SINRtoThreshold>
 <L3Message>11</L3Message>
 </Demodulation_1>
 <Demodulation_2>
 <FrontEndSelectionMask>1</FrontEndSelectionMask>
 <SINRtoThreshold>0</SINRtoThreshold>
 <L3Message>14</L3Message>
 </Demodulation_2>
 </Demodulation>
 </LTESetting>
 </LTE_Pilot>
 */
public class PilotLTE extends ScanTaskModel implements Cloneable {
    private static final String TAG = "PilotLTEModelModel";
    private int ResultBufferDepth=1024;//ResultBufferDepth：缓存，范围为1 -1024，Pioneer默认为1024；
    private int FrontEndSelectionMask=1;//FrontEndSelectionMask：物理接收天线（天线1、天线2），Pioneer界面初始值为1；
    private int Band=0;//Band：暂不使用，固定赋0；
    private int MeasurementMode=0;//MeasurementMode：测量模式，0为高速，1为定点，Pioneer界面初始值为0；暂不使用，固定赋0；
    private int ValuePerSec; //ValuePerSec：扫频速率，单位Hz，Pioneer界面初始值为10Hz，因为配置项是每1000秒，所以传进来的配置需要乘上1000；
    private int DecodeOutputMode;//DecodeOutputMode：0 为实时显示，1 为缓存显示，Pioneer界面初始值为0；
    private ArrayList<Channel> channels = new ArrayList<>();//ChannelCount：信道配置项个数，最大值为1024；
    private RSSISetting rssiSetting = new RSSISetting();
    private WBSetting wBSetting = new WBSetting();
    private Demodulation Demodulation = new Demodulation();// 检波

    public int getResultBufferDepth() {
        return ResultBufferDepth;
    }

    public void setResultBufferDepth(int resultBufferDepth) {
        ResultBufferDepth = resultBufferDepth;
    }

    public int getFrontEndSelectionMask() {
        return FrontEndSelectionMask;
    }

    public void setFrontEndSelectionMask(int frontEndSelectionMask) {
        FrontEndSelectionMask = frontEndSelectionMask;
    }

    public int getBand() {
        return Band;
    }

    public void setBand(int band) {
        Band = band;
    }

    public int getMeasurementMode() {
        return MeasurementMode;
    }

    public void setMeasurementMode(int measurementMode) {
        MeasurementMode = measurementMode;
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

    public ArrayList<Channel> getChannels() {
        return channels;
    }

    public void setChannels(ArrayList<Channel> channels) {
        this.channels = channels;
    }

    public RSSISetting getRssiSetting() {
        return rssiSetting;
    }

    public void setRssiSetting(RSSISetting rssiSetting) {
        this.rssiSetting = rssiSetting;
    }

    public WBSetting getwBSetting() {
        return wBSetting;
    }

    public void setwBSetting(WBSetting wBSetting) {
        this.wBSetting = wBSetting;
    }

    public Demodulation getDemodulation() {
        return Demodulation;
    }

    public void setDemodulation(Demodulation demodulation) {
        Demodulation = demodulation;
    }

    @Override
    public void writeToXml(XmlSerializer serializer, TestSchemaType taskType) throws IOException {
        LogUtil.d(TAG, "LTEPilotModelToXml");
        serializer.startTag(null, "LTE_Pilot");
        NodeValue(serializer, "Enable", this == null ? "0" : this.getEnable());
        NodeValue(serializer, "TaskName", "Pilot");
        NodeValue(serializer, "TaskType", TestSchemaType.LTEPILOT);
        serializer.startTag(null, "LTESetting");
        NodeValue(serializer, "ResultBufferDepth", this == null ? "0" : this.getResultBufferDepth());
        NodeValue(serializer, "FrontEndSelectionMask", this == null ? "0" : this.getFrontEndSelectionMask());
        NodeValue(serializer, "Band", this == null ? "0" : this.getBand());
        NodeValue(serializer, "MeasurementMode", this == null ? "0" : this.getMeasurementMode());
        NodeValue(serializer, "ValuePerSec", this == null ? "0" : this.getValuePerSec()*1000);
        NodeValue(serializer, "DecodeOutputMode", this == null ? "0" : this.getDecodeOutputMode());
        serializer.startTag(null, "RSSISetting");
        NodeValue(serializer, "FrontEndSelectionMask", this == null ? "0" : this.getRssiSetting().getFrontEndSelectionMask());
        NodeValue(serializer, "RssiMeasTimeInMs", this == null ? "0" : this.getRssiSetting().getRssiMeasTimeInMs());
        NodeValue(serializer, "RssiMeasMode", this == null ? "0" : this.getRssiSetting().getRssiMeasMode());
        serializer.endTag(null, "RSSISetting");
        serializer.startTag(null, "WBSetting");
        NodeValue(serializer, "FrontEndSelectionMask", this == null ? "0" : this.getwBSetting().getFrontEndSelectionMask());
        NodeValue(serializer, "MeaRate", this == null ? "0" : this.getwBSetting().getMeaRate()*1000);
        NodeValue(serializer, "Mode", this == null ? "0" : this.getwBSetting().getMode());
        NodeValue(serializer, "ShowMode", this == null ? "0" : this.getwBSetting().getShowMode());
        serializer.endTag(null, "WBSetting");
        NodeValue(serializer, "ChannelCount", channels == null ? 0 : channels.size());
        serializer.startTag("", "Channel");
        for (int i = 0; i < channels.size(); i++) {
            serializer.startTag("", "Channel_" + i);
            channels.get(i).ChannleLTENodeXml(serializer);
            serializer.endTag("", "Channel_" + i);
        }
        serializer.endTag("", "Channel");

        Demodulation.LteDemodulationNodeXml(serializer);
        serializer.endTag(null, "LTESetting");
        serializer.endTag(null, "LTE_Pilot");
    }

    @Override
    public void parserXml(XmlPullParser xmlParser, List<ScanTaskModel> testModelList) throws Exception {
            this.setTaskType(TestSchemaType.LTEPILOT.name());
            this.setTaskName("Pilot");


            int eventType = xmlParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("LTE_Pilot".equals(xmlParser.getName())) {
                            this.setTaskName("Pilot");
                            this.setGroupName(TestSchemaType.valueOf(this.getTaskType()).getGroupName());
                        } else if ("Enable".equals(xmlParser.getName())) {
                            this.setEnable(Integer.valueOf(xmlParser.nextText()));
                        } else if ("ResultBufferDepth".equals(xmlParser.getName())) {
                            this.setResultBufferDepth(Integer.valueOf(xmlParser.nextText()));
                        } else if ("FrontEndSelectionMask".equals(xmlParser.getName())) {
                            this.setFrontEndSelectionMask(Integer.valueOf(xmlParser.nextText()));
                        } else if ("Band".equals(xmlParser.getName())) {
                            this.setBand(Integer.valueOf(xmlParser.nextText()));
                        } else if ("MeasurementMode".equals(xmlParser.getName())) {
                            this.setMeasurementMode(Integer.valueOf(xmlParser.nextText()));
                        } else if ("ValuePerSec".equals(xmlParser.getName())) {
                            this.setValuePerSec(Integer.valueOf(xmlParser.nextText())/1000);
                        } else if ("DecodeOutputMode".equals(xmlParser.getName())) {
                            this.setDecodeOutputMode(Integer.parseInt(xmlParser.nextText()));
                        } else if ("Channel".equals(xmlParser.getName())) {
                            this.setChannels(Channel.parserLTEChannelModel(xmlParser));
                        } else if ("RSSISetting".equals(xmlParser.getName())) {
                            this.setRssiSetting(RSSISetting.parserRSSI(xmlParser));
                        } else if ("WBSetting".equals(xmlParser.getName())) {
                            this.setwBSetting(WBSetting.parserWB(xmlParser));
                        } else if ("Demodulation".equals(xmlParser.getName())) {
                            this.setDemodulation(com.walktour.gui.task.activity.scannertsma.model.Demodulation.parserLTEDemodulationModel(xmlParser));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("LTE_Pilot".equals(xmlParser.getName())) {
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
        return "PilotLTEModelModel{" +
                "ResultBufferDepth=" + ResultBufferDepth +
                ", FrontEndSelectionMask=" + FrontEndSelectionMask +
                ", Band=" + Band +
                ", MeasurementMode=" + MeasurementMode +
                ", ValuePerSec=" + ValuePerSec +
                ", DecodeOutputMode=" + DecodeOutputMode +
                ", channels=" + channels +
                ", rssiSetting=" + rssiSetting +
                ", wBSetting=" + wBSetting +
                ", Demodulation=" + Demodulation +
                ", channelLists=" + channelLists +
                '}';
    }

    public static class RSSISetting {
        private int FrontEndSelectionMask;
        private int RssiMeasTimeInMs=1; //RSSISetting – RssiMeasTimeInMs：暂不使用，固定赋1；
        private int RssiMeasMode;

        public int getFrontEndSelectionMask() {
            return FrontEndSelectionMask;
        }

        public void setFrontEndSelectionMask(int frontEndSelectionMask) {
            FrontEndSelectionMask = frontEndSelectionMask;
        }

        public int getRssiMeasTimeInMs() {
            return RssiMeasTimeInMs;
        }

        public void setRssiMeasTimeInMs(int rssiMeasTimeInMs) {
            RssiMeasTimeInMs = rssiMeasTimeInMs;
        }

        public int getRssiMeasMode() {
            return RssiMeasMode;
        }

        public void setRssiMeasMode(int rssiMeasMode) {
            RssiMeasMode = rssiMeasMode;
        }
        /**
         * @return
         * @throws Exception
         */
        public static PilotLTE.RSSISetting parserRSSI(XmlPullParser xmlParser) throws Exception {

            PilotLTE.RSSISetting rssiSetting = new PilotLTE.RSSISetting();
            int eventType = xmlParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("FrontEndSelectionMask".equals(xmlParser.getName())) {
                            rssiSetting.setFrontEndSelectionMask(Integer.valueOf(xmlParser.nextText()));
                        } else if ("RssiMeasTimeInMs".equals(xmlParser.getName())) {
                            rssiSetting.setRssiMeasTimeInMs(Integer.valueOf(xmlParser.nextText()));
                        } else if ("RssiMeasMode".equals(xmlParser.getName())) {
                            rssiSetting.setRssiMeasMode(Integer.valueOf(xmlParser.nextText()));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("RSSISetting".equals(xmlParser.getName())) {
                            return rssiSetting;
                        }
                        break;
                }
                eventType = xmlParser.next();
            }
            return rssiSetting;
        }
        @Override
        public String toString() {
            return "RSSISetting{" +
                    "FrontEndSelectionMask=" + FrontEndSelectionMask +
                    ", RssiMeasTimeInMs=" + RssiMeasTimeInMs +
                    ", RssiMeasMode=" + RssiMeasMode +
                    '}';
        }
    }

    public static class WBSetting {
        private int FrontEndSelectionMask=1;
        private int MeaRate;
        private int Mode=1;
        private int ShowMode=2;//显示模式（1：Max Power Cell；2:All Cell），Pioneer界面初始值为2；

        public int getFrontEndSelectionMask() {
            return FrontEndSelectionMask;
        }

        public void setFrontEndSelectionMask(int frontEndSelectionMask) {
            FrontEndSelectionMask = frontEndSelectionMask;
        }

        public int getMeaRate() {
            return MeaRate;
        }

        public void setMeaRate(int meaRate) {
            MeaRate = meaRate;
        }

        public int getMode() {
            return Mode;
        }

        public void setMode(int mode) {
            Mode = mode;
        }

        public int getShowMode() {
            return ShowMode;
        }

        public void setShowMode(int showMode) {
            ShowMode = showMode;
        }
        /**
         * @return
         * @throws Exception
         */
        public  static PilotLTE.WBSetting parserWB(XmlPullParser xmlParser) throws Exception {
            PilotLTE.WBSetting wbSetting = new PilotLTE.WBSetting();
            int eventType = xmlParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("FrontEndSelectionMask".equals(xmlParser.getName())) {
                            wbSetting.setFrontEndSelectionMask(Integer.valueOf(xmlParser.nextText()));
                        } else if ("MeaRate".equals(xmlParser.getName())) {
                            wbSetting.setMeaRate(Integer.valueOf(xmlParser.nextText())/1000);
                        } else if ("Mode".equals(xmlParser.getName())) {
                            wbSetting.setMode(Integer.valueOf(xmlParser.nextText()));
                        } else if ("ShowMode".equals(xmlParser.getName())) {
                            wbSetting.setShowMode(Integer.valueOf(xmlParser.nextText()));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("WBSetting".equals(xmlParser.getName())) {
                            return wbSetting;
                        }
                        break;
                }
                eventType = xmlParser.next();
            }
            return wbSetting;
        }
        @Override
        public String toString() {
            return "FrontEndSelectionMask{" +
                    "FrontEndSelectionMask=" + FrontEndSelectionMask +
                    ", MeaRate=" + MeaRate +
                    ", Mode=" + Mode +
                    ", ShowMode=" + ShowMode +
                    '}';
        }
    }

}
