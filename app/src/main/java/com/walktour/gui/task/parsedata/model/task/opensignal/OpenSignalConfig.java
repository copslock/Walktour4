package com.walktour.gui.task.parsedata.model.task.opensignal;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class OpenSignalConfig  extends TaskBase {
    /**序列化*/
    private static final long serialVersionUID = -5756958194782156062L;

    @SerializedName("downThreadNum")
    private String downThreadNum="4";//测试的线程数
    @SerializedName("upThreadNum")
    private String upThreadNum="1";//测试的线程数
    @SerializedName("country")
    private String country;//国家名
    @SerializedName("city")
    private String city;//城市名
    @SerializedName("sponsor")
    private String sponsor;//服务器赞助商
    @SerializedName("testDuration")
    private String testDuration="180";//业务时长
    @SerializedName("noDataTimeout")
    private String noDataTimeout="30";//无流量超时

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
                    if(tagName.equals("DownThreadNum")){
                        this.setDownThreadNum(parser.nextText());
                    }else if(tagName.equals("UpThreadNum")){
                        this.setUpThreadNum(parser.nextText());
                    }else if(tagName.equals("Country")){
                        this.setCountry(parser.nextText());
                    }else if(tagName.equals("City")){
                        this.setCity(parser.nextText());
                    }else if(tagName.equals("Sponsor")){
                        this.setSponsor(parser.nextText());
                    }else if(tagName.equals("TestDuration")){
                        this.setTestDuration(parser.nextText());
                    }else if(tagName.equals("NoDataTimeout")){
                        this.setNoDataTimeout(parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    tagName = parser.getName();
                    if (tagName.equals("OpenSignalConfig")) {
                        return;
                    }
                    break;
            }
            eventType = parser.next();
        }
    }

    public void writeXml(XmlSerializer serializer) throws Exception {
        serializer.startTag(null, "OpenSignalConfig");
        this.writeTag(serializer, "DownThreadNum", this.downThreadNum);
        this.writeTag(serializer, "UpThreadNum", this.upThreadNum);
        this.writeTag(serializer, "Country", this.country);
        this.writeTag(serializer, "City", this.city);
        this.writeTag(serializer, "Sponsor", this.sponsor);
        this.writeTag(serializer, "TestDuration", this.testDuration);
        this.writeTag(serializer, "NoDataTimeout", this.noDataTimeout);
        serializer.endTag(null, "OpenSignalConfig");
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public String getDownThreadNum() {
        return downThreadNum;
    }

    public void setDownThreadNum(String downThreadNum) {
        this.downThreadNum = downThreadNum;
    }

    public String getUpThreadNum() {
        return upThreadNum;
    }

    public void setUpThreadNum(String upThreadNum) {
        this.upThreadNum = upThreadNum;
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
