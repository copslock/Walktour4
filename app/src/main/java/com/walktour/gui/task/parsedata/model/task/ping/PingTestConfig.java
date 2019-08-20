package com.walktour.gui.task.parsedata.model.task.ping;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class PingTestConfig extends TaskBase {
    private static final long serialVersionUID = 4091240942665960176L;
    @SerializedName("pingAddress")
    private String pingAddress;
    @SerializedName("packetSize_B")
    private int packetSize_B;
    @SerializedName("timeout")
    private int timeout;
    @SerializedName("ttl")
    private int ttl;
    /**
     * 是否启用HSPING:true/false,默认为true
     */
    @SerializedName("hsPing")
    private boolean hsPing=true;
    /**
     * 终端状态:None/Idle,默认为none
     */
    @SerializedName("ueState")
    private String ueState;
    /**
     * 使用AT+PING业务方式:true/false,默认为false
     */
    @SerializedName("isATPing")
    private boolean isATPing = false;
    /**
     * 是否使用CMD+PING业务方式
     */
    @SerializedName("isCMDPing")
    private boolean isCMDPing=false;

    public String getUeState() {
        return ueState;
    }

    public void setUeState(String ueState) {
        this.ueState = ueState;
    }

    public boolean isCMDPing() {
        return isCMDPing;
    }

    public void setCMDPing(boolean CMDPing) {
        isCMDPing = CMDPing;
    }

    public boolean isATPing() {
        return isATPing;
    }

    public void setATPing(boolean ATPing) {
        isATPing = ATPing;
    }

    public String getPingAddress() {
        return pingAddress;
    }

    public void setPingAddress(String pingAddress) {
        this.pingAddress = pingAddress;
    }

    public int getPacketSize_B() {
        return packetSize_B;
    }

    public void setPacketSize_B(int packetSize_B) {
        this.packetSize_B = packetSize_B;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public boolean isHsPing() {
        return hsPing;
    }

    public void setHsPing(boolean hsPing) {
        this.hsPing = hsPing;
    }

    public void parseXml(XmlPullParser parser) throws Exception {
        int eventType = parser.getEventType();
        String tagName = "";
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    tagName = parser.getName();
                    break;
                case XmlPullParser.START_TAG:
                    tagName = parser.getName();
                    if (tagName.equals("PingAddress")) {
                        this.setPingAddress(parser.nextText());
                    } else if (tagName.equals("PacketSize_B")) {
                        this.setPacketSize_B(stringToInt(parser.nextText()));
                    } else if (tagName.equals("Timeout")) {
                        this.setTimeout(stringToInt(parser.nextText()) / 1000);
                    } else if (tagName.equals("TTL")) {
                        this.setTtl(stringToInt(parser.nextText()));
                    } else if (tagName.equals("HSPing")) {
                        this.setHsPing(stringToBool(parser.nextText()));
                    }else if (tagName.equals("UEState")) {
                        this.setUeState(parser.nextText());
                    } else if (tagName.equals("ATPing")) {
                        this.setATPing(stringToBool(parser.nextText()));
                    }else if (tagName.equals("CMDPing")) {
                        this.setCMDPing(stringToBool(parser.nextText()));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    tagName = parser.getName();
                    if (tagName.equals("PingTestConfig")) {
                        return;
                    }
                    break;
            }
            eventType = parser.next();
        }
    }

    public void writeXml(XmlSerializer serializer) throws Exception {
        serializer.startTag(null, "PingTestConfig");

        this.writeTag(serializer, "PingAddress", this.pingAddress);
        this.writeTag(serializer, "PacketSize_B", this.packetSize_B);
        this.writeTag(serializer, "Timeout", this.timeout * 1000);
        this.writeTag(serializer, "TTL", this.ttl);
        this.writeTag(serializer, "HSPing", this.hsPing);
        this.writeTag(serializer, "UEState", this.ueState);
        this.writeTag(serializer, "ATPing", this.isATPing);
        this.writeTag(serializer, "CMDPing", this.isCMDPing);

        serializer.endTag(null, "PingTestConfig");
    }
}
