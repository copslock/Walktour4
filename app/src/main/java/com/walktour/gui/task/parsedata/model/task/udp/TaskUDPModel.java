package com.walktour.gui.task.parsedata.model.task.udp;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

import static com.walktour.gui.task.parsedata.model.task.udp.UDPTestConfig.TEST_MODE_DOWN;
import static com.walktour.gui.task.parsedata.model.task.udp.UDPTestConfig.TEST_MODE_UP;
import static com.walktour.gui.task.parsedata.model.task.udp.UDPTestConfig.TEST_MODE_UPDOWN;

/**
 * Created by yi.lin on 2017/8/11.
 */

public class TaskUDPModel extends TaskModel {
    private static final long serialVersionUID = 2555515323650909653L;
    @SerializedName("mUDPTestConfig")
    private UDPTestConfig mUDPTestConfig = new UDPTestConfig();
    @SerializedName("networkConnectionSetting")
    private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();
    @SerializedName("isUnlimited")
    private boolean isUnlimited = false;

    public TaskUDPModel() {
        setTaskType(WalkStruct.TaskType.UDP.toString());
    }

    /**
     * 获得当前测试计划写入RCU文件的字符串
     *
     * @return
     * @author tangwq
     */
    public String getTestPlanStr() {
        StringBuffer testTask = new StringBuffer();
        testTask.append(getBaseModelStr());
        testTask.append("Repeat =" + (isUnlimited ? 9999 : getRepeat()) + "\r\n");
        testTask.append("server_ip =" + mUDPTestConfig.getServerIP() + "\r\n");
        testTask.append("server_port =" + mUDPTestConfig.getServerPort() + "\r\n");
        testTask.append("test_mode =" + mUDPTestConfig.getTestMode() + "\r\n");
        testTask.append("up_packet_size =" + mUDPTestConfig.getUpPacketSize() + "\r\n");
        testTask.append("down_packet_size =" + mUDPTestConfig.getDownPacketSize() + "\r\n");
        try {
            int inter = (int) (Integer.parseInt(getPacketSize()) * 8 / Integer.parseInt(getSendPacketInterval()));
            testTask.append("send_packet_interval_ms =" + inter + "\r\n");
        }catch (Exception ex){
            LogUtil.w("TaskUDPModel",ex.getMessage());
        }
        testTask.append("send_packet_duration_ms =" + mUDPTestConfig.getSendPacketDuration() + "\r\n");
        testTask.append("duration_ms =" + mUDPTestConfig.getTestDuration() + "\r\n");
        testTask.append("nodata_timeout_ms =" + mUDPTestConfig.getNoDataTimeout() + "\r\n");
        return testTask.toString();
    }

    @Override
    public int getTypeProperty() {
        if (networkConnectionSetting.getConnectionProtocol().equals(NetworkConnectionSetting.ConnectionProtocol_WLAN))
            return WalkCommonPara.TypeProperty_Wlan;
        return WalkCommonPara.TypeProperty_Net;
    }

    /***
     * 数据业务复写这两个方法，以适配历史的业务
     */
    @Override
    public int getDisConnect() {
        return networkConnectionSetting.getDisConnect();
    }

    public void setDisConnect(int disConnect) {
        networkConnectionSetting.setDisConnect(disConnect);
    }

    public NetworkConnectionSetting getNetworkConnectionSetting() {
        return networkConnectionSetting;
    }

    public String getServerIP() {
        return mUDPTestConfig.getServerIP();
    }

    public void setServerIP(String serverIP) {
        mUDPTestConfig.setServerIP(serverIP);
    }

    public String getServerPort() {
        return mUDPTestConfig.getServerPort();
    }

    public void setServerPort(String serverPort) {
        mUDPTestConfig.setServerPort(serverPort);
    }

    public String getSendPacketInterval() {
        return mUDPTestConfig.getSendPacketInterval();
    }

    public void setSendPacketInterval(String sendPacketInterval) {
        mUDPTestConfig.setSendPacketInterval(sendPacketInterval);
    }

    public String getSendPacketDuration() {
        return mUDPTestConfig.getSendPacketDuration();
    }

    public void setSendPacketDuration(String sendPacketDuration) {
        mUDPTestConfig.setSendPacketDuration(sendPacketDuration);
    }

    public String getTestMode() {
        return mUDPTestConfig.getTestMode();
    }

    public void setTestMode(String testMode) {
        mUDPTestConfig.setTestMode(testMode);
    }

    public String getPacketSize() {
        if(mUDPTestConfig.getTestMode().equals(TEST_MODE_UP)){
            return mUDPTestConfig.getUpPacketSize();
        }else  if(mUDPTestConfig.getTestMode().equals(TEST_MODE_DOWN)){
            return mUDPTestConfig.getDownPacketSize();
        }else if(mUDPTestConfig.getTestMode().equals(TEST_MODE_UPDOWN)){
            return mUDPTestConfig.getDownPacketSize();
        }
        return mUDPTestConfig.getUpPacketSize();
    }

    public void setPacketSize(String packetSize) {
        if(mUDPTestConfig.getTestMode().equals(TEST_MODE_UP)){
            mUDPTestConfig.setUpPacketSize(packetSize);
        }else  if(mUDPTestConfig.getTestMode().equals(TEST_MODE_DOWN)){
            mUDPTestConfig.setDownPacketSize(packetSize);
        }else if(mUDPTestConfig.getTestMode().equals(TEST_MODE_UPDOWN)){
            mUDPTestConfig.setUpPacketSize(packetSize);
            mUDPTestConfig.setDownPacketSize(packetSize);
        }
    }

    public String getTestDuration() {
        return mUDPTestConfig.getTestDuration();
    }

    public void setTestDuration(String testDuration) {
        mUDPTestConfig.setTestDuration(testDuration);
    }

    public String getNoDataTimeout() {
        return mUDPTestConfig.getNoDataTimeout();
    }

    public void setNoDataTimeout(String noDataTimeout) {
        mUDPTestConfig.setNoDataTimeout(noDataTimeout);
    }

    public UDPTestConfig getUDPTestConfig() {
        return mUDPTestConfig;
    }

    public void parseXml(XmlPullParser parser, List<TaskModel> tasks, Map<String, String> map) throws Exception {
        int eventType = parser.getEventType();
        String tagName = "";
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    tagName = parser.getName();
                    break;
                case XmlPullParser.START_TAG:
                    tagName = parser.getName();
                    if (tagName.equals("UDPTestConfig")) {
                        mUDPTestConfig.parseXml(parser);
                    } else if (tagName.equals("NetworkConnectionSetting")) {
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            String attName = parser.getAttributeName(i);
                            String attValue = parser.getAttributeValue(i);
                            if (attName.equals("IsAvailable")) {
                                networkConnectionSetting.setAvailable(stringToBool(attValue));
                            }
                        }
                        networkConnectionSetting.parseXml(parser);
                    } else {// 解析公共属性
                        parsrXmlPublic(parser, map);
                    }

                    break;
                case XmlPullParser.END_TAG:
                    tagName = parser.getName();
                    if (tagName.equals("TaskConfig")) {
                        tasks.add(this);
                        return;
                    }
                    break;
            }
            eventType = parser.next();
        }
    }

    public void writeXml(XmlSerializer serializer) throws Exception {
        super.writeXml(serializer);
        if (null != mUDPTestConfig)
            mUDPTestConfig.writeXml(serializer);
        if (null != networkConnectionSetting)
            networkConnectionSetting.writeXml(serializer);
    }

    @Override
    public String getServerTaskType() {
        return WalkStruct.TaskType.UDP.getXmlTaskType();
    }

}
