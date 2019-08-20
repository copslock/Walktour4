package com.walktour.gui.task.parsedata.model.task.multihttp.download;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;
import com.walktour.gui.task.parsedata.model.task.base.URLInfo;
import com.walktour.gui.task.parsedata.model.task.multiftp.download.MFTPDownloadTestConfig;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

public class TaskMultiHttpDownModel extends TaskModel {
    /**
     * 序列化
     */
    private static final long serialVersionUID = -2515355257379763917L;
    @SerializedName("mMHttpDownConfig")
    private MHttpDownConfig mMHttpDownConfig = new MHttpDownConfig();
    @SerializedName("networkConnectionSetting")
    private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();

    public TaskMultiHttpDownModel()
    {
        setTaskName(WalkStruct.TaskType.MultiHttpDownload.getXmlTaskType());
        setTaskType(WalkStruct.TaskType.MultiHttpDownload.getXmlTaskType());
    }

    /**
     * 获得当前测试计划写入RCU文件的字符串
     *
     * @return
     * @author tangwq
     */
    public String getTestPlanStr()
    {
        StringBuffer testTask = new StringBuffer();
        testTask.append(getBaseModelStr());
        testTask.append("down timeout =" + mMHttpDownConfig.getDownloadTimeout() + "\r\n");
        testTask.append("urls =" + mMHttpDownConfig.getUrlList().toString() + "\r\n");
        testTask.append("thread_num =" + mMHttpDownConfig.getThreadCount() + "\r\n");
        testTask.append("duration_ms =" + mMHttpDownConfig.getDownloadDuration() + "\r\n");
        testTask.append("nodata_timeout_ms =" + mMHttpDownConfig.getNoDataTimeout() + "\r\n");
        return testTask.toString();
    }

    @Override
    public int getTypeProperty()
    {
        if (networkConnectionSetting.getConnectionProtocol().equals(NetworkConnectionSetting.ConnectionProtocol_WLAN))
            return WalkCommonPara.TypeProperty_Wlan;
        else if (networkConnectionSetting.getConnectionProtocol().equals(NetworkConnectionSetting.ConnectionProtocol_PPPNB)) {
            return WalkCommonPara.TypeProperty_Ppp;
        }
        return WalkCommonPara.TypeProperty_Net;
    }


    @Override
    public void setTypeProperty(int typeProperty)
    {
        if (typeProperty == WalkCommonPara.TypeProperty_Wlan) {
            networkConnectionSetting.setConnectionProtocol(NetworkConnectionSetting.ConnectionProtocol_WLAN);
        } else if (typeProperty == WalkCommonPara.TypeProperty_Ppp) {
            networkConnectionSetting.setConnectionProtocol(NetworkConnectionSetting.ConnectionProtocol_PPPNB);
        } else {
            networkConnectionSetting.setConnectionProtocol(NetworkConnectionSetting.ConnectionProtocol_PPP);
        }
    }

    public int getEndCodition()
    {
        if (mMHttpDownConfig.getEndCondition().equals(MHttpDownConfig.ENDCONDITION_ONE)) {
            return 0;
        }
        return 1;
    }

    public void setEndCodition(int endCodition)
    {
        if (endCodition == 0) {
            mMHttpDownConfig.setEndCondition(MHttpDownConfig.ENDCONDITION_ONE);
        } else {
            mMHttpDownConfig.setEndCondition(MHttpDownConfig.ENDCONDITION_ALL);
        }
    }

    /***
     * 数据业务复写这两个方法，以适配历史的业务
     */
    @Override
    public int getDisConnect()
    {
        return networkConnectionSetting.getDisConnect();
    }

    public void setDisConnect(int disConnect)
    {
        networkConnectionSetting.setDisConnect(disConnect);
    }

    public NetworkConnectionSetting getNetworkConnectionSetting()
    {
        return networkConnectionSetting;
    }

    public int getDownloadTimeout()
    {
        return mMHttpDownConfig.getDownloadTimeout();
    }

    public void setDownloadTimeout(int downloadTimeout)
    {
        mMHttpDownConfig.setDownloadTimeout(downloadTimeout);
    }


    public int getNoDataTimeOut()
    {
        return mMHttpDownConfig.getNoDataTimeout();
    }

    public void setNoDataTimeOut(int noDataTimeout)
    {
        mMHttpDownConfig.setNoDataTimeout(noDataTimeout);
    }

    public int getThreadCount()
    {
        return mMHttpDownConfig.getThreadCount();
    }

    public void setThreadCount(int threadCount)
    {
        mMHttpDownConfig.setThreadCount(threadCount);
    }

    public MHttpDownConfig getMHttpDownConfig()
    {
        return mMHttpDownConfig;
    }

    public List<URLInfo> getUrlList()
    {
        return mMHttpDownConfig.getUrlList();
    }

    public void parseXml(XmlPullParser parser, List<TaskModel> tasks, Map<String, String> map) throws Exception
    {
        int eventType = parser.getEventType();
        String tagName = "";
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    tagName = parser.getName();
                    break;
                case XmlPullParser.START_TAG:
                    tagName = parser.getName();
                    if (tagName.equals("MultiHttpDownloadConfig")) {
                        mMHttpDownConfig.parseXml(parser);
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
    public void writeXml(XmlSerializer serializer) throws Exception
    {
        super.writeXml(serializer);
        if (null != mMHttpDownConfig) {
            mMHttpDownConfig.writeXml(serializer);
        }
        if (null != networkConnectionSetting) {
            networkConnectionSetting.writeXml(serializer);
        }
    }

    @Override
    public String getServerTaskType()
    {
        return WalkStruct.TaskType.MultiHttpDownload.getXmlTaskType();
    }

}