package com.walktour.gui.task.parsedata.model.task.udp;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

/**
 * pioneer测试专用
 */
public class CustomPioneer extends TaskBase {

    private static final long serialVersionUID = -2559973516152265592L;
    /**
     * 本地ip
     */
    @SerializedName("localIp")
    private  String localIp;

    /**
     * 本地port
     */
    @SerializedName("localPort")
    private int localPort;

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }
    public void parseXml(XmlPullParser parser) throws Exception {
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            String attName = parser.getAttributeName(i);
            if (attName.equals("LocalIP")) {
                this.setLocalIp(parser.getAttributeValue(i));
            } else if (attName.equals("LocalPort")) {
                this.setLocalPort(stringToInt(parser.getAttributeValue(i)));
            }
        }
    }

    public void writeXml(XmlSerializer serializer) throws Exception {
        serializer.startTag(null, "CustomPioneer");
        this.writeAttribute(serializer, "LocalIP", this.localIp);
        this.writeAttribute(serializer, "LocalPort", this.localPort);
        serializer.endTag(null, "CustomPioneer");

    }

}
