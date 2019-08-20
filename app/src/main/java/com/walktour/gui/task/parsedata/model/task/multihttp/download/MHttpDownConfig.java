package com.walktour.gui.task.parsedata.model.task.multihttp.download;

import com.google.gson.annotations.SerializedName;
import com.walktour.control.config.ConfigUrl;
import com.walktour.gui.task.parsedata.model.base.TaskBase;
import com.walktour.gui.task.parsedata.model.task.base.URLInfo;
import com.walktour.model.UrlModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.LinkedList;
import java.util.List;

public class MHttpDownConfig extends TaskBase {

    private static final long serialVersionUID = 7599607139037207614L;

    public static final String ENDCONDITION_ONE="One Session End";
    public static final String ENDCONDITION_ALL="All Session End";

    //下载超时
    @SerializedName("downloadTimeout")
    private int downloadTimeout;
    //下载间隔时长
    @SerializedName("downloadDuration")
    private int downloadDuration;
    //下载线程数
    @SerializedName("threadCount")
    private int threadCount;
    //无流量超时
    @SerializedName("noDataTimeout")
    private int noDataTimeout;
    /**结束条件: 0为One 1为All**/
    @SerializedName("endCondition")
    private String endCondition=ENDCONDITION_ONE;
    @SerializedName("urlList")
    private List<URLInfo> urlList = new LinkedList<URLInfo>();


    public int getDownloadTimeout() {
        return downloadTimeout;
    }

    public void setDownloadTimeout(int downloadTimeout) {
        this.downloadTimeout = downloadTimeout;
    }

    public int getDownloadDuration() {
        return downloadDuration;
    }

    public void setDownloadDuration(int downloadDuration) {
        this.downloadDuration = downloadDuration;
    }


    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getNoDataTimeout() {
        return noDataTimeout;
    }

    public void setNoDataTimeout(int noDataTimeout) {
        this.noDataTimeout = noDataTimeout;
    }

    public String getEndCondition() {
        return endCondition;
    }

    public void setEndCondition(String endCondition) {
        this.endCondition = endCondition;
    }

    public List<URLInfo> getUrlList() {
        return urlList;
    }

    public void parseXml(XmlPullParser parser) throws Exception {
        int eventType = parser.getEventType();
        String tagName = "";
        ConfigUrl config=new ConfigUrl();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    tagName = parser.getName();
                    break;
                case XmlPullParser.START_TAG:
                    tagName = parser.getName();
                    if (tagName.equals("DownloadTimeout")) {
                        this.setDownloadTimeout(stringToInt(parser.nextText())/1000);
                    } else if (tagName.equals("DownloadDuration")) {
                        this.setDownloadDuration(stringToInt(parser.nextText())/1000);
                    } else if (tagName.equals("NoDataTimeout")) {
                        this.setNoDataTimeout(stringToInt(parser.nextText())/1000);
                    } else if (tagName.equals("ThreadCount")) {
                        this.setThreadCount(stringToInt(parser.nextText()));
                    } else if (tagName.equals("URLInfo")) {
                        URLInfo urlInfo = new URLInfo();
                        urlInfo.parseXml(parser);

                        //载入到配置里面去
                        UrlModel url=new UrlModel();
                        url.setName(urlInfo.getUrl());
                        url.setEnable(urlInfo.isCheck()?"1":"0");
                        config.addUrl(url);

                        this.getUrlList().add(urlInfo);
                    }else if (tagName.equals("EndCondition")) {
                        this.setEndCondition(parser.nextText());
                    }

                    break;
                case XmlPullParser.END_TAG:
                    tagName = parser.getName();
                    if (tagName.equals("MultiHttpDownloadConfig")) {
                        return;
                    }
                    break;
            }
            eventType = parser.next();
        }
    }

    public void writeXml(XmlSerializer serializer) throws Exception {

        serializer.startTag(null, "MultiHttpDownloadConfig");

        this.writeTag(serializer, "DownloadTimeout", this.downloadTimeout*1000);
        this.writeTag(serializer, "DownloadDuration", this.downloadDuration*1000);
        this.writeTag(serializer, "ThreadCount", this.threadCount);
        this.writeTag(serializer, "NoDataTimeout", this.noDataTimeout*1000);
        this.writeTag(serializer, "EndCondition", this.endCondition);
        serializer.startTag(null, "URLList");
        for (URLInfo urlInfo : urlList) {
            if (null != urlInfo) {
                urlInfo.writeXml(serializer);
            }
        }

        serializer.endTag(null, "URLList");

        serializer.endTag(null, "MultiHttpDownloadConfig");

    }
}
