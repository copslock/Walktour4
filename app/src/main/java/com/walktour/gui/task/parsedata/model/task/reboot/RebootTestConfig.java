package com.walktour.gui.task.parsedata.model.task.reboot;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

/**
 * 重启测试配置
 */
public class RebootTestConfig extends TaskBase {
    /** 重启时间 **/
    @SerializedName("rebootTime")
    private String rebootTime = "";

    public String getRebootTime() {
        return rebootTime;
    }

    public void setRebootTime(String rebootTime) {
        this.rebootTime = rebootTime;
    }

    public void parseXmlIdleTest(XmlPullParser parser) throws Exception {
        int eventType = parser.getEventType();
        String tagName = "";
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    tagName = parser.getName();
                    break;
                case XmlPullParser.START_TAG:
                    tagName = parser.getName();
                    if (tagName.equals("RebootTime")) {
                        this.setRebootTime(parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    tagName = parser.getName();
                    if (tagName.equals("RebootTestConfig")) {
                        return;
                    }
                    break;
            }
            eventType = parser.next();
        }
    }

    public void writeXml(XmlSerializer serializer) throws Exception {
        serializer.startTag(null, "RebootTestConfig");
        this.writeTag(serializer, "RebootTime", this.rebootTime);
        serializer.endTag(null, "RebootTestConfig");
    }
}