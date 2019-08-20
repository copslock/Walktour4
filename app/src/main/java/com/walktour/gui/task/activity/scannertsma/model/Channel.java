package com.walktour.gui.task.activity.scannertsma.model;

/**
 * 频点model类,两个参数 bandCode与channel
 *
 * @author zhihui.lian
 */

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * <CWChannel>
 * <CWChannel_0>
 * <ULFlag>0</ULFlag>
 * <NarrowWideRBW>100</NarrowWideRBW>
 * <Band>1318</Band>
 * <Channel>37900</Channel>
 * </CWChannel_0>
 * </CWChannel>
 * <p>
 * CWChannel – ULFlag：上下行标识，0为下行，1为上行，Pioneer界面初始值为0；
 * CWChannel – NarrowWideRBW：带宽，单位KH，Pioneer界面初始值为100KHz;
 * CWChannel – Band：BandID;
 * CWChannel – Channel：逻辑频点；
 */
public class Channel extends ScanSonTaskModel {

    /**
     * 对象ID
     */
    private long id;
    /**
     * 频段编号
     */
    private int channel;

    private int ULFlag;
    private long NarrowWideRBW;
    private boolean TableOfPNOffsetArbitraryLimitation;
    private boolean IsEvdoFrequency;
    private int MidambleCodes;//MeasurementMode：测量模式，0为高速，1为高灵敏，Pioneer界面初始值为1；

    private int Band;
    private int EARFCN;
    private int IsUL;
    private int BandWidth;
    private int FrameStructureType;
    private int MIMOTode;
    private int PCI;
    private int DlaaMode;


    public Channel() {
        this.id = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }


    public void setId(long id) {
        this.id = id;
    }

    public int getULFlag() {
        return ULFlag;
    }

    public void setULFlag(int ULFlag) {
        this.ULFlag = ULFlag;
    }

    public long getNarrowWideRBW() {
        return NarrowWideRBW;
    }

    public void setNarrowWideRBW(long narrowWideRBW) {
        NarrowWideRBW = narrowWideRBW;
    }

    public boolean isTableOfPNOffsetArbitraryLimitation() {
        return TableOfPNOffsetArbitraryLimitation;
    }

    public void setTableOfPNOffsetArbitraryLimitation(boolean tableOfPNOffsetArbitraryLimitation) {
        TableOfPNOffsetArbitraryLimitation = tableOfPNOffsetArbitraryLimitation;
    }

    public boolean isEvdoFrequency() {
        return IsEvdoFrequency;
    }

    public void setEvdoFrequency(boolean evdoFrequency) {
        IsEvdoFrequency = evdoFrequency;
    }

    public int getMidambleCodes() {
        return MidambleCodes;
    }

    public void setMidambleCodes(int midambleCodes) {
        MidambleCodes = midambleCodes;
    }

    public int getBand() {
        return Band;
    }

    public void setBand(int band) {
        Band = band;
    }

    public int getEARFCN() {
        return EARFCN;
    }

    public void setEARFCN(int EARFCN) {
        this.EARFCN = EARFCN;
    }

    public int getIsUL() {
        return IsUL;
    }

    public void setIsUL(int isUL) {
        IsUL = isUL;
    }

    public int getBandWidth() {
        return BandWidth;
    }

    public void setBandWidth(int bandWidth) {
        BandWidth = bandWidth;
    }

    public int getFrameStructureType() {
        return FrameStructureType;
    }

    public void setFrameStructureType(int frameStructureType) {
        FrameStructureType = frameStructureType;
    }

    public int getMIMOTode() {
        return MIMOTode;
    }

    public void setMIMOTode(int MIMOTode) {
        this.MIMOTode = MIMOTode;
    }

    public int getPCI() {
        return PCI;
    }

    public void setPCI(int PCI) {
        this.PCI = PCI;
    }

    public int getDlaaMode() {
        return DlaaMode;
    }

    public void setDlaaMode(int dlaaMode) {
        DlaaMode = dlaaMode;
    }

    @Override
    void writeToXml(XmlSerializer serializer) throws IOException {

        NodeValue(serializer, "ULFlag", this.getULFlag() + "");
        NodeValue(serializer, "NarrowWideRBW", this.getNarrowWideRBW() + "");
        NodeValue(serializer, "Band", this.getBand() + "");
        NodeValue(serializer, "Channel", this.getChannel() + "");
        NodeValue(serializer, "TableOfPNOffsetArbitraryLimitation", this.isTableOfPNOffsetArbitraryLimitation() + "");
        NodeValue(serializer, "IsEvdoFrequency", this.isEvdoFrequency() + "");
    }

    public static ArrayList<Channel> parserXml(XmlPullParser xmlParser) throws Exception {

        ArrayList<Channel> channelList = new ArrayList<Channel>();
        Channel channelModel = null;
        int eventType = xmlParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (xmlParser.getName().contains("Channel_")) {
                        channelModel = new Channel();
                    } else if ("ULFlag".equals(xmlParser.getName())) {
                        channelModel.setULFlag(Integer.valueOf(xmlParser.nextText()));
                    } else if ("MidambleCodes".equals(xmlParser.getName())) {
                        channelModel.setMidambleCodes(Integer.valueOf(xmlParser.nextText()));
                    } else if ("TableOfPNOffsetArbitraryLimitation".equals(xmlParser.getName())) {
                        channelModel.setTableOfPNOffsetArbitraryLimitation(Boolean.valueOf(xmlParser.nextText()));
                    } else if ("IsEvdoFrequency".equals(xmlParser.getName())) {
                        channelModel.setEvdoFrequency(Boolean.valueOf(xmlParser.nextText()));
                    } else if ("NarrowWideRBW".equals(xmlParser.getName())) {
                        channelModel.setNarrowWideRBW(Integer.valueOf(xmlParser.nextText()));
                    } else if ("Band".equals(xmlParser.getName())) {
                        channelModel.setBand(Integer.valueOf(xmlParser.nextText()));
                    } else if ("Channel".equals(xmlParser.getName())) {
                        channelModel.setChannel(Integer.valueOf(xmlParser.nextText()));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (xmlParser.getName().contains("Channel_")) {
                        channelList.add(channelModel);
                    }
                    if ("Channels".equals(xmlParser.getName())) {
                        return channelList;
                    }
            }
            eventType = xmlParser.next();
        }
        return channelList;
    }

    /**
     * 生成频点XML
     */
    public void ChannleLTENodeXml(XmlSerializer serializer) throws IOException {

        NodeValue(serializer, "Band", this.getBand() + "");
        NodeValue(serializer, "EARFCN", this.getEARFCN() + "");
        NodeValue(serializer, "IsUL", this.getIsUL() + "");
        NodeValue(serializer, "BandWidth", this.getBandWidth() + "");
        NodeValue(serializer, "FrameStructureType", this.getFrameStructureType() + "");
        NodeValue(serializer, "MIMOTode", this.getMIMOTode() + "");
        NodeValue(serializer, "PCI", this.getPCI() + "");
        NodeValue(serializer, "DlaaMode", this.getDlaaMode() + "");
    }

    /**
     * 解析频点model
     *
     * @return
     * @throws Exception
     */
    public static ArrayList<Channel> parserLTEChannelModel(XmlPullParser xmlParser) throws Exception {

        ArrayList<Channel> channelList = new ArrayList<>();
        Channel channelModel = null;
        int eventType = xmlParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (xmlParser.getName().contains("Channel_")) {
                        channelModel = new Channel();
                    } else if ("EARFCN".equals(xmlParser.getName())) {
                        int earfcn = Integer.valueOf(xmlParser.nextText());
                        channelModel.setChannel(earfcn);
                        channelModel.setEARFCN(earfcn);
                    } else if ("IsUL".equals(xmlParser.getName())) {
                        channelModel.setIsUL(Integer.valueOf(xmlParser.nextText()));
                    } else if ("BandWidth".equals(xmlParser.getName())) {
                        channelModel.setBandWidth(Integer.valueOf(xmlParser.nextText()));
                    } else if ("FrameStructureType".equals(xmlParser.getName())) {
                        channelModel.setFrameStructureType(Integer.valueOf(xmlParser.nextText()));
                    } else if ("NarrowWideRBW".equals(xmlParser.getName())) {
                        channelModel.setNarrowWideRBW(Integer.valueOf(xmlParser.nextText()));
                    } else if ("Band".equals(xmlParser.getName())) {
                        channelModel.setBand(Integer.valueOf(xmlParser.nextText()));
                    } else if ("MIMOTode".equals(xmlParser.getName())) {
                        channelModel.setMIMOTode(Integer.valueOf(xmlParser.nextText()));
                    } else if ("PCI".equals(xmlParser.getName())) {
                        channelModel.setPCI(Integer.valueOf(xmlParser.nextText()));
                    } else if ("DlaaMode".equals(xmlParser.getName())) {
                        channelModel.setDlaaMode(Integer.valueOf(xmlParser.nextText()));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (xmlParser.getName().contains("Channel_")) {
                        channelList.add(channelModel);
                    }
                    if ("Channel".equals(xmlParser.getName())) {
                        return channelList;
                    }
            }
            eventType = xmlParser.next();
        }
        return channelList;
    }

    @Override
    public String toString() {
        return "ChannelModel{" +
                "id=" + id +
                ", channel=" + channel +
                ", ULFlag=" + ULFlag +
                ", NarrowWideRBW=" + NarrowWideRBW +
                ", TableOfPNOffsetArbitraryLimitation=" + TableOfPNOffsetArbitraryLimitation +
                ", IsEvdoFrequency=" + IsEvdoFrequency +
                ", MidambleCodes=" + MidambleCodes +
                ", Band=" + Band +
                ", EARFCN=" + EARFCN +
                ", IsUL=" + IsUL +
                ", BandWidth=" + BandWidth +
                ", FrameStructureType=" + FrameStructureType +
                ", MIMOTode=" + MIMOTode +
                ", PCI=" + PCI +
                ", DlaaMode=" + DlaaMode +
                '}';
    }
}
