/*
 * 文件名: TaskMultiftpDownloadModel.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2013-6-4
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.gui.task.parsedata.model.task.multiftp.download;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;
import com.walktour.model.FTPGroupModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author weirong.fan
 *
 */
public class TaskMultiftpDownloadModel extends TaskModel{ 

	/**
	 * 
	 */
	private static final long serialVersionUID = -8555754312331348969L;

	public TaskMultiftpDownloadModel() {
		setTypeProperty(WalkCommonPara.TypeProperty_Net);
		setTaskType(WalkStruct.TaskType.MultiftpDownload.toString());
	}

	@SerializedName("networkConnectionSetting")
	private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();
	@SerializedName("mftpDownloadTestConfig")
	private MFTPDownloadTestConfig mftpDownloadTestConfig = new MFTPDownloadTestConfig();

	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());

		testTask.append("TestMode =" + this.getTestMode() + "\r\n");
		testTask.append("KeepTime =" + mftpDownloadTestConfig.getWaitTime() + "\r\n");
		testTask.append("NoData =" + this.getNoData() + "\r\n");
		testTask.append("WaitTime =" + this.getWaitTime() + "\r\n");
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
	public int getThreadCount(){
		return mftpDownloadTestConfig.getThreadCount();
	}

	public void setThreadCount(int threadCount){
		mftpDownloadTestConfig.setThreadCount(threadCount);
	}
	public String getThreadMode(){
		return mftpDownloadTestConfig.getThreadMode();
	}

	public void setThreadMode(String threadMode){
		mftpDownloadTestConfig.setThreadMode(threadMode);
	}
	public int getTestMode() {
		if (mftpDownloadTestConfig.getPsCallMode().equals(MFTPDownloadTestConfig.PSCALLMODE_FILE)) {
			return 0;
		}
		return 1;
	}

	public void setTestMode(int testMode) {
		if (testMode == 0) {
			mftpDownloadTestConfig.setPsCallMode(MFTPDownloadTestConfig.PSCALLMODE_FILE);
		} else {
			mftpDownloadTestConfig.setPsCallMode(MFTPDownloadTestConfig.PSCALLMODE_TIME);
		}
	}

	public int getKeepTime() {
		return mftpDownloadTestConfig.getDownloadDuration();
	}

	public void setKeepTime(int keepTime) {
		mftpDownloadTestConfig.setDownloadDuration(keepTime);
	}

	public int getNoData() {
		return mftpDownloadTestConfig.getNoDataTimeout();
	}

	public void setNoData(int noData) {
		mftpDownloadTestConfig.setNoDataTimeout(noData);
	}

	public int getWaitTime() {
		return mftpDownloadTestConfig.getWaitTime();
	}

	public void setWaitTime(int waitTime) {
		mftpDownloadTestConfig.setWaitTime(waitTime);
	}

	/**
	 * @return the ftpServers
	 */
	public ArrayList<FTPGroupModel> getFtpServers() {
		ArrayList<FTPGroupModel> ftpServers = new ArrayList<FTPGroupModel>();
		List<MFTPDownConfig> list = mftpDownloadTestConfig.getMftpDownList();
		for (MFTPDownConfig config : list) {
			FTPGroupModel model = new FTPGroupModel(config);
			ftpServers.add(model);
		}

		return ftpServers;
	}

	/**
	 * @param ftpServers
	 *            the ftpServers to set
	 */
	public void setFtpServers(ArrayList<FTPGroupModel> ftpServers) {
		if (null == ftpServers)
			return;
		List<MFTPDownConfig> list = mftpDownloadTestConfig.getMftpDownList();
		list.clear();
		for (FTPGroupModel ftpGroupModel : ftpServers) {
			MFTPDownConfig config = new MFTPDownConfig(ftpGroupModel);
			list.add(config);
		}
	}

	public int getEndCodition() {
		if (mftpDownloadTestConfig.getEndCondition().equals(MFTPDownloadTestConfig.ENDCONDITION_ONE)) {
			return 0;
		}
		return 1;
		}

	public void setEndCodition(int endCodition) {
		if (endCodition == 0) {
			mftpDownloadTestConfig.setEndCondition(MFTPDownloadTestConfig.ENDCONDITION_ONE);
		} else {
			mftpDownloadTestConfig.setEndCondition(MFTPDownloadTestConfig.ENDCONDITION_ALL);
		}
	}

	public MFTPDownloadTestConfig getMftpDownloadTestConfig() {
		return mftpDownloadTestConfig;
	}

	public NetworkConnectionSetting getNetworkConnectionSetting() {
		return networkConnectionSetting;
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
				if (tagName.equals("MFTPDownloadTestConfig")) {
					mftpDownloadTestConfig.parseXml(parser);
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
		if (null != mftpDownloadTestConfig)
			mftpDownloadTestConfig.writeXml(serializer);
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}
	
	
	@Override
	public String getServerTaskType() {
		return WalkStruct.TaskType.MultiftpDownload.getXmlTaskType();
	}
}
