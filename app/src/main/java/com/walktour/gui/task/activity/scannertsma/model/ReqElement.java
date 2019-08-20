package com.walktour.gui.task.activity.scannertsma.model;

import com.walktour.gui.task.activity.scannertsma.ennnnum.NetType;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author jinfeng.xie
 * @data 2019/2/1
 */
public class ReqElement extends ScanSonTaskModel {
    NetType netType=NetType.LTE;
    int BandldMask=8;//Band值，Pioneer界面为Band1-Band44。Band1就传1，Band2就传2；

    public NetType getNetType() {
        return netType;
    }

    public void setNetType(NetType netType) {
        this.netType = netType;
    }

    public int getBandldMask() {
        return BandldMask;
    }

    public void setBandldMask(int bandldMask) {
        BandldMask = bandldMask;
    }

    @Override
    public String toString() {
        return "ReqElement{" +
                "netType=" + netType +
                ", BandldMask=" + BandldMask +
                '}';
    }
    /**
     * 解析频点model
     *
     * @return
     * @throws Exception
     */
    public static ArrayList<ReqElement> parserRReqElementModel(XmlPullParser xmlParser) throws Exception {

        ArrayList<ReqElement> rfDataBlocks = new ArrayList<>();
        ReqElement reqElement = null;

        int eventType = xmlParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (xmlParser.getName().contains("ReqElement_")) {
                        reqElement = new ReqElement();
                    } else if ("BandIdMask".equals(xmlParser.getName())) {
                        reqElement.setBandldMask(Integer.parseInt(xmlParser.nextText()));
                    } else if ("NetType".equals(xmlParser.getName())) {
                        reqElement.setNetType(NetType.getNetTypeByCode(Integer.parseInt(xmlParser.nextText())));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (xmlParser.getName().contains("ReqElement_")) {
                        rfDataBlocks.add(reqElement);
                    }
                    if ("ReqElements".equals(xmlParser.getName())) {
                        return rfDataBlocks;
                    }
            }
            eventType = xmlParser.next();
        }
        return rfDataBlocks;
    }
    @Override
    void writeToXml(XmlSerializer serializer) throws IOException {
                    NodeValue(serializer,"NetType", this.getNetType().getNetType() + "");
                    NodeValue(serializer,"BandIdMask", this.getBandldMask() + "");
    }
}
