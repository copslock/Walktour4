package com.walktour.gui.task.activity.scannertsma.model;

import com.walktour.base.util.LogUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jinfeng.xie
 * @data 2019/2/12
 */
/*
* <!-- Spectrum -->
<RFSetting>
	<Protocol>0</Protocol>
	<BlockCount>1</BlockCunt>
	<RFDataBlock>
		<RFDataBlock_0>
			<Band>0</Band>
			<StartFrequency>2110000</StartFrequency>
			<StopFrequency>2170000</StopFrequency>
			<FFTBandwidth>11130430</FFTBandwidth>
			<Spacing>1024</Spacing>
			<RBW>100000</RBW>
			<FFTSize>128</FFTSize>
			<MeasRate>10</MeasRate>
			<MeasTimeInNs>1</MeasTimeInNs>
		</RFDataBlock_0>
	</RFDataBlock>
</RFSetting>
* */
public class Spectrum extends ScanTaskModel {
    private static final String TAG = "Spectrum";
    int Protocol;//Protocol：RS不使用，固定赋0；
    int BlockCount;//块配置数量，最大值为64。
    ArrayList<RFDataBlock> rfDataBlocks=new ArrayList<>();
    int DetectorType;//
    long MeasTimeInNs;
    public int getProtocol() {
        return Protocol;
    }

    public void setProtocol(int protocol) {
        Protocol = protocol;
    }

    public int getDetectorType() {
        return DetectorType;
    }

    public void setDetectorType(int detectorType) {
        DetectorType = detectorType;
    }

    public long getMeasTimeInNs() {
        return MeasTimeInNs;
    }

    public void setMeasTimeInNs(long measTimeInNs) {
        MeasTimeInNs = measTimeInNs;
    }

    public int getBlockCount() {
        return BlockCount;
    }

    public void setBlockCount(int blockCount) {
        BlockCount = blockCount;
    }

    public ArrayList<RFDataBlock> getRfDataBlocks() {
        return rfDataBlocks;
    }

    public void setRfDataBlocks(ArrayList<RFDataBlock> rfDataBlocks) {
        this.rfDataBlocks = rfDataBlocks;
    }

    @Override
    public void writeToXml(XmlSerializer serializer, TestSchemaType taskType) throws IOException {
            LogUtil.d(TAG, "spectrumModelToXml");
        serializer.startTag(null, "Spectrum");
            NodeValue(serializer,"Enable",this==null?0:this.getEnable());
            NodeValue(serializer,"TaskName","Spectrum");
            NodeValue(serializer,"TaskType",taskType.name());
            serializer.startTag(null,"SpectrumSetting");
            NodeValue(serializer,"Protocol", this == null ? 0 : this.getProtocol());
            NodeValue(serializer,"DetectorType", this == null ? 0 : this.getDetectorType());
            NodeValue(serializer,"MeasTimeInNs", this == null ? 0 : this.getMeasTimeInNs());
        NodeValue(serializer,"BlockCount", rfDataBlocks == null ? 0 : rfDataBlocks.size());
        serializer.startTag("", "RFDataBlock");
        for (int i = 0; i < rfDataBlocks.size(); i++) {
            serializer.startTag("", "RFDataBlock_" + i);
           rfDataBlocks.get(i).writeToXml(serializer);
            serializer.endTag("", "RFDataBlock_" + i);
        }
        serializer.endTag("", "RFDataBlock");
            serializer.endTag(null,"SpectrumSetting");
        serializer.endTag(null, "Spectrum");
    }

    @Override
    public void parserXml(XmlPullParser xmlParser, List<ScanTaskModel> testModelList) throws Exception {
            int eventType = xmlParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("TaskName".equals(xmlParser.getName())) {
                            this.setTaskName(xmlParser.nextText());
                        } else if ("TaskType".equals(xmlParser.getName())) {
                            TestSchemaType testType = TestSchemaType.valueOf(xmlParser.nextText());
                            this.setTaskType(testType.name());
                            this.setGroupName(testType.getGroupName());
                        } else if ("Enable".equals(xmlParser.getName())) {
                            this.setEnable(Integer.valueOf(xmlParser.nextText()));
                        } else if ("Protocol".equals(xmlParser.getName())) {
                            this.setProtocol(Integer.valueOf(xmlParser.nextText()));
                        } else if ("BlockCount".equals(xmlParser.getName())) {
                            this.setBlockCount(Integer.valueOf(xmlParser.nextText()));
                        } else if ("MeasTimeInNs".equals(xmlParser.getName())) {
                            this.setMeasTimeInNs(Long.valueOf(xmlParser.nextText()));
                        } else if ("RFDataBlock".equals(xmlParser.getName())) {
                            this.setRfDataBlocks(RFDataBlock.parserRFDataBlockModel(xmlParser));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("SpectrumSetting".equals(xmlParser.getName())) {
                            testModelList.add(this);
                            return;
                        }
                        break;
                }
                eventType = xmlParser.next();
            }

    }
    /**
     * Lte Specturm 由XML转model
     */
    public  void parserLteSpecturmXmlToModel(XmlPullParser xmlParser, List<ScanTaskModel> testModelList) throws Exception {


        this.setTaskType(TestSchemaType.LTESPECTRUM.name());
        this.setScanMode(0);
        this.setTaskName("Specturm");


        int eventType = xmlParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:

                    if ("SpectrumSetting".equals(xmlParser.getName())) {
                        this.setGroupName(TestSchemaType.valueOf(this.getTaskType()).getGroupName());
                    } else if ("Enable".equals(xmlParser.getName())) {
                        this.setEnable(Integer.valueOf(xmlParser.nextText()));
                    } else if ("Protocol".equals(xmlParser.getName())) {
                        this.setProtocol(Integer.valueOf(xmlParser.nextText()));
                    } else if ("BlockCount".equals(xmlParser.getName())) {
                        this.setBlockCount(Integer.valueOf(xmlParser.nextText()));
                    } else if ("RFDataBlock".equals(xmlParser.getName())) {
                        this.setRfDataBlocks(RFDataBlock.parserRFDataBlockModel(xmlParser));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("SpectrumSetting".equals(xmlParser.getName())) {
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
        return "Spectrum{" +
                "Protocol=" + Protocol +
                ", BlockCount=" + BlockCount +
                ", rfDataBlocks=" + rfDataBlocks +
                ", DetectorType=" + DetectorType +
                ", MeasTimeInNs=" + MeasTimeInNs +
                '}';
    }

    public static class RFDataBlock extends ScanSonTaskModel {
        private int Band;//RS不使用，固定赋0；
        private long StartFrequency;//起始频率，单位KHz，Pioneer界面初始值为2110000KHz；
        private long StopFrequency;//结束频率，单位KHz，Pioneer界面初始值为2170000KHz；
        private long FFTBandwidth;//测量带宽，单位Hz，Pioneer界面初始值为11130430Hz；
        private int Spacing;//步进，单位KHz，Pioneer界面初始值为1024；
        private long RBW;//分辨率带宽，单位Hz，Pioneer界面初始值为100；
        private int FFTSize;//FFT大小，枚举值：2^4 – 2^13，Pioneer界面初始值为128;
        private int MeasRate;//测量速率，单位Hz，Pioneer界面初始值为10Hz；
        private long MeasTimeInNs;//测量时间，单位纳秒。只有5G下才开放设置，Pioneer界面初始值为100000000；

        public int getBand() {
            return Band;
        }

        public void setBand(int band) {
            Band = band;
        }

        public long getStartFrequency() {
            return StartFrequency;
        }

        public void setStartFrequency(long startFrequency) {
            StartFrequency = startFrequency;
        }

        public long getStopFrequency() {
            return StopFrequency;
        }

        public void setStopFrequency(long stopFrequency) {
            StopFrequency = stopFrequency;
        }

        public long getFFTBandwidth() {
            return FFTBandwidth;
        }

        public void setFFTBandwidth(long FFTBandwidth) {
            this.FFTBandwidth = FFTBandwidth;
        }

        public int getSpacing() {
            return Spacing;
        }

        public void setSpacing(int spacing) {
            Spacing = spacing;
        }

        public long getRBW() {
            return RBW;
        }

        public void setRBW(long RBW) {
            this.RBW = RBW;
        }

        public int getFFTSize() {
            return FFTSize;
        }

        public void setFFTSize(int FFTSize) {
            this.FFTSize = FFTSize;
        }

        public int getMeasRate() {
            return MeasRate;
        }

        public void setMeasRate(int measRate) {
            MeasRate = measRate;
        }

        public long getMeasTimeInNs() {
            return MeasTimeInNs;
        }

        public void setMeasTimeInNs(long measTimeInNs) {
            MeasTimeInNs = measTimeInNs;
        }
        /**
         * 解析频点model
         *
         * @return
         * @throws Exception
         */
        public static ArrayList<Spectrum.RFDataBlock> parserRFDataBlockModel(XmlPullParser xmlParser) throws Exception {

            ArrayList<Spectrum.RFDataBlock> rfDataBlocks = new ArrayList<Spectrum.RFDataBlock>();
            Spectrum.RFDataBlock rfDataBlock = null;
            int eventType = xmlParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (xmlParser.getName().contains("RFDataBlock_")) {
                            rfDataBlock = new Spectrum.RFDataBlock();
                        } else if ("Band".equals(xmlParser.getName())) {
                            rfDataBlock.setBand(Integer.valueOf(xmlParser.nextText()));
                        } else if ("StartFrequency".equals(xmlParser.getName())) {
                            rfDataBlock.setStartFrequency(Long.valueOf(xmlParser.nextText()));
                        } else if ("StopFrequency".equals(xmlParser.getName())) {
                            rfDataBlock.setStopFrequency(Long.valueOf(xmlParser.nextText()));
                        } else if ("FFTBandwidth".equals(xmlParser.getName())) {
                            rfDataBlock.setFFTBandwidth(Long.valueOf(xmlParser.nextText()));
                        } else if ("Spacing".equals(xmlParser.getName())) {
                            rfDataBlock.setSpacing(Integer.valueOf(xmlParser.nextText()));
                        } else if ("RBW".equals(xmlParser.getName())) {
                            rfDataBlock.setRBW(Long.valueOf(xmlParser.nextText()));
                        } else if ("FFTSize".equals(xmlParser.getName())) {
                            rfDataBlock.setFFTSize(Integer.valueOf(xmlParser.nextText()));
                        } else if ("MeasRate".equals(xmlParser.getName())) {
                            rfDataBlock.setMeasRate(Integer.valueOf(xmlParser.nextText()));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (xmlParser.getName().contains("RFDataBlock_")) {
                            rfDataBlocks.add(rfDataBlock);
                        }
                        if ("RFDataBlock".equals(xmlParser.getName())) {
                            return rfDataBlocks;
                        }
                }
                eventType = xmlParser.next();
            }
            return rfDataBlocks;
        }
        @Override
        public String toString() {
            return "RFDataBlock{" +
                    "Band=" + Band +
                    ", StartFrequency=" + StartFrequency +
                    ", StopFrequency=" + StopFrequency +
                    ", FFTBandwidth=" + FFTBandwidth +
                    ", Spacing=" + Spacing +
                    ", RBW=" + RBW +
                    ", FFTSize=" + FFTSize +
                    ", MeasRate=" + MeasRate +
                    ", MeasTimeInNs=" + MeasTimeInNs +
                    '}';
        }

        @Override
        void writeToXml(XmlSerializer serializer) throws IOException {

                        NodeValue( serializer,"Band", this.getBand() + "");
                        NodeValue(serializer,"StartFrequency", this.getStartFrequency() + "");
                        NodeValue( serializer,"StopFrequency", this.getStopFrequency() + "");
                        NodeValue( serializer,"FFTBandwidth", this.getFFTBandwidth() + "");
                        NodeValue( serializer,"Spacing", this.getSpacing() + "");
                        NodeValue(serializer,"RBW", this.getRBW() + "");
                        NodeValue(serializer,"FFTSize", this.getFFTSize() + "");
                        NodeValue( serializer,"MeasRate", this.getMeasRate() + "");

        }
    }

}
