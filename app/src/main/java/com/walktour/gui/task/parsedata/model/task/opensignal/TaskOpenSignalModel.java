package com.walktour.gui.task.parsedata.model.task.opensignal;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

public class TaskOpenSignalModel extends TaskModel {
    /**
     * 序列化
     */
    private static final long serialVersionUID = 7790167638610956463L;
    @SerializedName("mOpenSignalConfig")
    private OpenSignalConfig mOpenSignalConfig = new OpenSignalConfig();
    @SerializedName("networkConnectionSetting")
    private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();

    public TaskOpenSignalModel() {
        setTaskName(WalkStruct.TaskType.OpenSignal.toString());
        setTaskType(WalkStruct.TaskType.OpenSignal.toString());
        setRepeat(10);
        setInterVal(30);
        setDisConnect(1);
    }

    /**
     * 获得当前测试计划写入RCU文件的字符串
     */
    public String getTestPlanStr() {
        StringBuffer testTask = new StringBuffer();
        testTask.append(getBaseModelStr());
        testTask.append("down_thread_num =" + mOpenSignalConfig.getDownThreadNum() + "\r\n");
        testTask.append("up_thread_num =" + mOpenSignalConfig.getUpThreadNum() + "\r\n");
        testTask.append("duration_ms =" + mOpenSignalConfig.getTestDuration() + "\r\n");
        testTask.append("nodata_timeout_ms =" + mOpenSignalConfig.getNoDataTimeout() + "\r\n");
        return testTask.toString();
    }

    @Override
    public int getTypeProperty() {
        if(networkConnectionSetting.getConnectionProtocol().equals(NetworkConnectionSetting.ConnectionProtocol_WLAN))
            return WalkCommonPara.TypeProperty_Wlan;
        else if(networkConnectionSetting.getConnectionProtocol().equals(NetworkConnectionSetting.ConnectionProtocol_PPPNB)){
            return WalkCommonPara.TypeProperty_Ppp;
        }
        return WalkCommonPara.TypeProperty_Net;
    }



    @Override
    public void setTypeProperty(int typeProperty) {
        if(typeProperty==WalkCommonPara.TypeProperty_Wlan){
            networkConnectionSetting.setConnectionProtocol(NetworkConnectionSetting.ConnectionProtocol_WLAN);
        } else if(typeProperty==WalkCommonPara.TypeProperty_Ppp){
            networkConnectionSetting.setConnectionProtocol(NetworkConnectionSetting.ConnectionProtocol_PPPNB);
        }else{
            networkConnectionSetting.setConnectionProtocol(NetworkConnectionSetting.ConnectionProtocol_PPP);
        }
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

    public String getDownThreadNum() {
        return mOpenSignalConfig.getDownThreadNum();
    }

    public void setDownThreadNum(String downThreadNum) {
        mOpenSignalConfig.setDownThreadNum(downThreadNum);
    }

    public String getUpThreadNum() {
        return mOpenSignalConfig.getUpThreadNum();
    }

    public void setUpThreadNum(String upThreadNum) {
        mOpenSignalConfig.setUpThreadNum(upThreadNum);
    }

    public String getTestDuration() {
        return mOpenSignalConfig.getTestDuration();
    }

    public void setTestDuration(String testDuration) {
        mOpenSignalConfig.setTestDuration(testDuration);
    }

    public String getNoDataTimeout() {
        return mOpenSignalConfig.getNoDataTimeout();
    }

    public void setNoDataTimeout(String noDataTimeout) {
        mOpenSignalConfig.setNoDataTimeout(noDataTimeout);
    }

    public OpenSignalConfig getOpenSignalConfig() {
        return mOpenSignalConfig;
    }

    public String getCountry() {
        return mOpenSignalConfig.getCountry();
    }

    public void setCountry(String country) {
        mOpenSignalConfig.setCountry(country);
    }

    public String getCity() {
        return mOpenSignalConfig.getCity();
    }

    public void setCity(String city) {
        mOpenSignalConfig.setCity(city);
    }

    public String getSponsor() {
        return mOpenSignalConfig.getSponsor();
    }

    public void setSponsor(String sponsor) {
        mOpenSignalConfig.setSponsor(sponsor);
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
                    if (tagName.equals("OpenSignalConfig")) {
                        mOpenSignalConfig.parseXml(parser);
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

    @Override
    public void writeXml(XmlSerializer serializer) throws Exception {
        super.writeXml(serializer);
        if (null != mOpenSignalConfig) {
            mOpenSignalConfig.writeXml(serializer);
        }
        if (null != networkConnectionSetting) {
            networkConnectionSetting.writeXml(serializer);
        }
    }

    @Override
    public String getServerTaskType() {
        return WalkStruct.TaskType.OpenSignal.getXmlTaskType();
    }

}