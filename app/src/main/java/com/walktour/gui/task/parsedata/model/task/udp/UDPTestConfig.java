package com.walktour.gui.task.parsedata.model.task.udp;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

/**
 * Created by yi.lin on 2017/8/16.
 */

public class UDPTestConfig extends TaskBase {
    private static final long serialVersionUID = 6434746664978476826L;
    /**上行测试**/
    public static final String TEST_MODE_UP="0";
    /**下行测试**/
    public static final String TEST_MODE_DOWN="1";
    /**上下同测**/
    public static final String TEST_MODE_UPDOWN="2";
    @SerializedName("serverIP")
    private String serverIP;//服务器IP
    @SerializedName("serverPort")
    private String serverPort;//服务器端口
    @SerializedName("upPacketSize")
    private String upPacketSize="500";//上行包大小
    @SerializedName("downPacketSize")
    private String downPacketSize="500";//下行包大小
    @SerializedName("testMode")
    private String testMode=TEST_MODE_UP;//0:上行，1:下行，2:上下行同测
    @SerializedName("sendPacketInterval")
    private String sendPacketInterval="1";//发包间隔
    @SerializedName("sendPacketDuration")
    private String sendPacketDuration="20";//持续发包时长
    @SerializedName("testDuration")
    private String testDuration="180";//业务时长
    @SerializedName("noDataTimeout")
    private String noDataTimeout="30";//无流量超时
    @SerializedName("customPioneer")
    private CustomPioneer customPioneer=null;

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
                    if (tagName.equals("ServerIP")) {
                        this.setServerIP(parser.nextText());
                    }else if (tagName.equals("ServerPort")) {
                        this.setServerPort(parser.nextText());
                    }else if (tagName.equals("UpPacketSize")) {
                        this.setUpPacketSize(parser.nextText());
                    }else if (tagName.equals("DownPacketSize")) {
                        this.setDownPacketSize(parser.nextText());
                    }else if(tagName.equals("TestMode")){
                        this.setTestMode(parser.nextText());
                    }else if(tagName.equals("SendPacketInterval")){
                        this.setSendPacketInterval(parser.nextText());
                    }else if(tagName.equals("SendPacketDuration")){
                        this.setSendPacketDuration(parser.nextText());
                    }else if(tagName.equals("TestDuration")){
                        this.setTestDuration(parser.nextText());
                    }else if(tagName.equals("NoDataTimeout")){
                        this.setNoDataTimeout(parser.nextText());
                    }else if(tagName.equals("CustomPioneer")){
                        customPioneer=new CustomPioneer();
                        customPioneer.parseXml(parser);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    tagName = parser.getName();
                    if (tagName.equals("UDPTestConfig")) {
                        return;
                    }
                    break;
            }
            eventType = parser.next();
        }
    }

    public void writeXml(XmlSerializer serializer) throws Exception {
        serializer.startTag(null, "UDPTestConfig");
        this.writeTag(serializer, "ServerIP", this.serverIP);
        this.writeTag(serializer, "ServerPort", this.serverPort);
        this.writeTag(serializer, "UpPacketSize", this.upPacketSize);
        this.writeTag(serializer, "DownPacketSize", this.downPacketSize);
        this.writeTag(serializer, "TestMode", this.testMode);
        this.writeTag(serializer, "SendPacketInterval", this.sendPacketInterval);
        this.writeTag(serializer, "SendPacketDuration", this.sendPacketDuration);
        this.writeTag(serializer, "TestDuration", this.testDuration);
        this.writeTag(serializer, "NoDataTimeout", this.noDataTimeout);
        if(null!=customPioneer){
            customPioneer.writeXml(serializer);
        }
        serializer.endTag(null, "UDPTestConfig");
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }


    public String getSendPacketInterval() {
        return sendPacketInterval;
    }

    public void setSendPacketInterval(String sendPacketInterval) {
        this.sendPacketInterval = sendPacketInterval;
    }

    public String getSendPacketDuration() {
        return sendPacketDuration;
    }

    public void setSendPacketDuration(String sendPacketDuration) {
        this.sendPacketDuration = sendPacketDuration;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getTestMode() {
        return testMode;
    }

    public void setTestMode(String testMode) {
        this.testMode = testMode;
    }


    public String getUpPacketSize() {
        return upPacketSize;
    }

    public void setUpPacketSize(String upPacketSize) {
        this.upPacketSize = upPacketSize;
    }

    public String getDownPacketSize() {
        return downPacketSize;
    }

    public void setDownPacketSize(String downPacketSize) {
        this.downPacketSize = downPacketSize;
    }

    public String getTestDuration() {
        return testDuration;
    }

    public void setTestDuration(String testDuration) {
        this.testDuration = testDuration;
    }

    public String getNoDataTimeout() {
        return noDataTimeout;
    }

    public void setNoDataTimeout(String noDataTimeout) {
        this.noDataTimeout = noDataTimeout;
    }
}
