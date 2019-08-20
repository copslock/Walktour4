package com.walktour.gui.task.activity.scannertsma.model;

import com.dingli.seegull.SeeGullFlags;
import com.walktour.base.util.LogUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Rssi配置model
 *
 * @author jinfeng.xie
 */
/*<CW>
			<Enable>1</Enable>
			<CWSetting IsCustomFreq="False">
				<ReceiverIndex>0</ReceiverIndex>
				<ScanInterval>100000</ScanInterval>
				<Band>0</Band>
				<Protocol>5</Protocol>
				<!-- MaxCount: 512 -->
				<ChannelCount>1</ChannelCount>
				<CWChannel>
					<CWChannel_0>
						<ULFlag>0</ULFlag>
						<NarrowWideRBW>100</NarrowWideRBW>
						<Band>1318</Band>
						<Channel>37900</Channel>
					</CWChannel_0>
				</CWChannel>
				<!-- MaxCount: 4 -->
				<FullBandCount>0</FullBandCount>
				<FullBand>
				</FullBand>
			</CWSetting>
</CW>
节点说明：
ReceiverIndex：暂不使用，固定赋0；
ScanInterval：扫频数，Pioneer界面初始值为100000；
Band：RS不使用，固定赋0；
Protocol：RS不使用，固定赋0；
ChannelCount：配置项个数，最大值为512；
CWChannel – ULFlag：上下行标识，0为下行，1为上行，Pioneer界面初始值为0；
CWChannel – NarrowWideRBW：带宽，单位KH，Pioneer界面初始值为100KHz;
CWChannel – Band：BandID;
CWChannel – Channel：逻辑频点；
FullBandCount：配置项个数，最大值为4；
FullBand – StartFrequency：起始频率，单位KHz
FullBand – StopFrequency：终止频率，单位KHz
FullBand – Spacing：步进，单位KHz
FullBand – BandWidth：测量带宽，单位KHz
*/
public class CW extends ScanTaskModel implements Cloneable {
    private static final String TAG = "CWModel";
        private  long Narrow=0;
    public CW() {
        setScanType(0);
    }

    int ReceiverIndex = 0;
    long ScanInterval = 0l;
    int Band = 0;
    int Protocol = 0;
    int DetectorType = 0;
    private ArrayList<FullBand> fullBands = new ArrayList<>();
    private ArrayList<Channel> channelList = new ArrayList<Channel>();

    public long getNarrow() {
        return Narrow;
    }

    public void setNarrow(long narrow) {
        Narrow = narrow;
    }

    public ArrayList<Channel> getChannelList() {
        return channelList;
    }

    public void setChannelList(ArrayList<Channel> channelList) {
        this.channelList = channelList;
    }

    public int getReceiverIndex() {
        return ReceiverIndex;
    }

    public void setReceiverIndex(int receiverIndex) {
        ReceiverIndex = receiverIndex;
    }

    public long getScanInterval() {
        return ScanInterval;
    }

    public void setScanInterval(long scanInterval) {
        ScanInterval = scanInterval;
    }

    public int getBand() {
        return Band;
    }

    public void setBand(int band) {
        Band = band;
    }

    public int getProtocol() {
        return Protocol;
    }

    public void setProtocol(int protocol) {
        Protocol = protocol;
    }

    public ArrayList<FullBand> getFullBands() {
        return fullBands;
    }

    public void setFullBands(ArrayList<FullBand> fullBands) {
        this.fullBands = fullBands;
    }

    public int getDetectorType() {
        return DetectorType;
    }

    public void setDetectorType(int detectorType) {
        DetectorType = detectorType;
    }

    /**
     * 对象浅克隆
     */
    @Override
    public CW clone() {
        CW CWModel = null;
        try {
            CWModel = (CW) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return CWModel;
    }

    @Override
    public void writeToXml(XmlSerializer serializer, TestSchemaType taskType) throws IOException {
        LogUtil.d(TAG, "cwModelToXml");
        serializer.startTag(null, "CW");
        NodeValue(serializer, "Enable", this == null ? "0" : this.getEnable());
        NodeValue(serializer, "TaskName",  "Cw" );
        NodeValue(serializer, "TaskType", taskType.name());
        serializer.startTag(null, "CWSetting");
        serializer.attribute(null, "IsCustomFreq", "" + false);
        NodeValue(serializer, "ReceiverIndex", this == null ? 0 : this.getReceiverIndex());
        NodeValue(serializer, "ScanInterval", this == null ? 0 : this.getScanInterval());
        NodeValue(serializer, "Band", this == null ? 0 : this.getBand());
        NodeValue(serializer, "Protocol", this == null ? 0 : this.getProtocol());
        NodeValue(serializer, "DetectorType", this == null ? 0 : this.getDetectorType());

        NodeValue(serializer, "ChannelCount", channelList == null ? "" + 0 : "" + channelList.size());
        serializer.startTag("", "Channels");
        for (int i = 0; i < channelList.size(); i++) {
            serializer.startTag("", "Channel_" + i);
            channelList.get(i).setNarrowWideRBW(this.getNarrow());
            channelList.get(i).writeToXml(serializer);
            serializer.endTag("", "Channel_" + i);
        }
        serializer.endTag("", "Channels");

        NodeValue(serializer, "FullBandCount", fullBands == null ? 0 : fullBands.size());
        serializer.startTag("", "FullBand");
        for (int i = 0; i < fullBands.size(); i++) {
            serializer.startTag("", "FullBand_" + i);
            fullBands.get(i).writeToXml(serializer);
            serializer.endTag("", "FullBand_" + i);
        }
        serializer.endTag("", "FullBand");
        serializer.endTag(null, "CWSetting");
        serializer.endTag(null, "CW");
    }

    @Override
    public void parserXml(XmlPullParser xmlParser, List<ScanTaskModel> testModelList) throws Exception {
            int eventType = xmlParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:

                        if ("CW".equals(xmlParser.getName())) {
                            this.setTaskName("CW");
                        } else if ("TaskName".equals(xmlParser.getName())) {
                            this.setTaskName(xmlParser.nextText());
                        } else if ("TaskType".equals(xmlParser.getName())) {
                            TestSchemaType testType = TestSchemaType.valueOf(xmlParser.nextText());
                            this.setTaskType(testType.name());
                            this.setGroupName(testType.getGroupName());
                            switch (testType) {
                                case GSMCW:
                                    this.setProtocolCode(SeeGullFlags.ProtocolCodes.PROTOCOL_GSM);
                                    break;

                                case WCDMACW:
                                    this.setProtocolCode(SeeGullFlags.ProtocolCodes.PROTOCOL_3GPP_WCDMA);
                                    break;
                                case CDMACW:
                                    this.setProtocolCode(SeeGullFlags.ProtocolCodes.PROTOCOL_IS_2000_CDMA);
                                    break;
                                case TDSCDMACW:
                                    this.setProtocolCode(SeeGullFlags.ProtocolCodes.PROTOCOL_TDSCDMA);
                                    break;
                                case LTECW:
                                    this.setProtocolCode(SeeGullFlags.ProtocolCodes.PROTOCOL_LTE);
                                    break;
                                default:
                                    break;
                            }
                        } else if ("Enable".equals(xmlParser.getName())) {
                            this.setEnable(Integer.valueOf(xmlParser.nextText()));
                        } else if ("ReceiverIndex".equals(xmlParser.getName())) {
                            this.setReceiverIndex(Integer.valueOf(xmlParser.nextText()));
                        } else if ("ScanInterval".equals(xmlParser.getName())) {
                            this.setScanInterval(Integer.valueOf(xmlParser.nextText()));
                        } else if ("Band".equals(xmlParser.getName())) {
                            this.setBand(Integer.valueOf(xmlParser.nextText()));
                        } else if ("Protocol".equals(xmlParser.getName())) {
                            this.setProtocol(Integer.valueOf(xmlParser.nextText()));
                        } else if ("Channels".equals(xmlParser.getName())) {
                            this.setChannelList(Channel.parserXml(xmlParser));
                        } else if ("FullBand".equals(xmlParser.getName())) {
                            this.setFullBands(FullBand.parserFullBand(xmlParser));
                        } else if ("DetectorType".equals(xmlParser.getName())) {
                            this.setDetectorType(Integer.valueOf(xmlParser.nextText()));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("CW".equals(xmlParser.getName())) {
                            testModelList.add(this);
                            return;
                        }
                }
                eventType = xmlParser.next();
            }
    }

    @Override
    public String toString() {
        return "CW{" +
                "Narrow=" + Narrow +
                ", ReceiverIndex=" + ReceiverIndex +
                ", ScanInterval=" + ScanInterval +
                ", Band=" + Band +
                ", Protocol=" + Protocol +
                ", DetectorType=" + DetectorType +
                ", fullBands=" + fullBands +
                ", channelList=" + channelList +
                '}';
    }


}
