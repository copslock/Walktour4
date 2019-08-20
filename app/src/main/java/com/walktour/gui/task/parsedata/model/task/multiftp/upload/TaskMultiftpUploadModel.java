/*
 * 文件名: TaskMultiftpUploadModel.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2013-6-4
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.gui.task.parsedata.model.task.multiftp.upload;

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
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2013-6-4]
 */
public class TaskMultiftpUploadModel extends TaskModel { 

	/**
	 * 
	 */
	private static final long serialVersionUID = 7951360306275000930L;

	public TaskMultiftpUploadModel() {
		setTypeProperty(WalkCommonPara.TypeProperty_Net);
		setTaskType(WalkStruct.TaskType.MultiftpUpload.toString());
	}

	/**
	 * 服务组
	 */
	// private ArrayList<FTPGroupModel> ftpServers;
	@SerializedName("mftpUploadTestConfig")
	private MFTPUploadTestConfig mftpUploadTestConfig = new MFTPUploadTestConfig();
	@SerializedName("networkConnectionSetting")
	private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();

	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());

		testTask.append("TestMode =" + this.getTestMode() + "\r\n");
		testTask.append("KeepTime =" + mftpUploadTestConfig.getWaitTime() + "\r\n");
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

	public int getTestMode() {
		if (mftpUploadTestConfig.getPsCallMode().equals(MFTPUploadTestConfig.PSCALLMODE_FILE)) {
			return 0;
		}
		return 1;
	}

	public void setTestMode(int testMode) {
		if (testMode == 0) {
			mftpUploadTestConfig.setPsCallMode(MFTPUploadTestConfig.PSCALLMODE_FILE);
		} else {
			mftpUploadTestConfig.setPsCallMode(MFTPUploadTestConfig.PSCALLMODE_TIME);
		}
	}

	public int getThreadCount(){
		return mftpUploadTestConfig.getThreadCount();
	}

	public void setThreadCount(int threadCount){
		mftpUploadTestConfig.setThreadCount(threadCount);
	}
	public String getThreadMode(){
		return mftpUploadTestConfig.getThreadMode();
	}

	public void setThreadMode(String threadMode){
		mftpUploadTestConfig.setThreadMode(threadMode);
	}
	/**
	 * @return the keepTime
	 */
	public int getKeepTime() {
		return mftpUploadTestConfig.getUploadDuration();
	}

	/**
	 * @param keepTime
	 *            the keepTime to set
	 */
	public void setKeepTime(int keepTime) {
		mftpUploadTestConfig.setUploadDuration(keepTime);
	}

	public int getNoData() {
		return mftpUploadTestConfig.getNoDataTimeout();
	}

	public void setNoData(int noData) {
		mftpUploadTestConfig.setNoDataTimeout(noData);
	}

	/**
	 * @return the waitTime
	 */
	public int getWaitTime() {
		return mftpUploadTestConfig.getWaitTime();
	}

	/**
	 * @param waitTime
	 *            the waitTime to set
	 */
	public void setWaitTime(int waitTime) {
		mftpUploadTestConfig.setWaitTime(waitTime);
	}

	/**
	 * @return the ftpServers
	 */
	public ArrayList<FTPGroupModel> getFtpServers() {
		ArrayList<FTPGroupModel> ftpServers = new ArrayList<FTPGroupModel>();
		List<MFTPUpConfig> list = mftpUploadTestConfig.getMftpUpList();
		for (MFTPUpConfig config : list) {
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
		List<MFTPUpConfig> mftpUpList = mftpUploadTestConfig.getMftpUpList();
		mftpUpList.clear();
		for (FTPGroupModel ftpGroupModel : ftpServers) {
			MFTPUpConfig config = new MFTPUpConfig(ftpGroupModel);
			mftpUpList.add(config);
		}
	}

	public int getEndCodition() {
		if (mftpUploadTestConfig.getEndCondition().equals(MFTPUploadTestConfig.ENDCONDITION_ONE)) {
			return 0;
		}
		return 1;
	}

	public void setEndCodition(int endCodition) {
		if (endCodition == 0) {
			mftpUploadTestConfig.setEndCondition(MFTPUploadTestConfig.ENDCONDITION_ONE);
		} else {
			mftpUploadTestConfig.setEndCondition(MFTPUploadTestConfig.ENDCONDITION_ALL);
		}
	}

	public MFTPUploadTestConfig getMftpUploadTestConfig() {
		return mftpUploadTestConfig;
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
				if (tagName.equals("MFTPUploadTestConfig")) {
					mftpUploadTestConfig.parseXml(parser);
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
		if (null != mftpUploadTestConfig)
			mftpUploadTestConfig.writeXml(serializer);
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}
	
	@Override
	public String getServerTaskType() {
		return WalkStruct.TaskType.MultiftpUpload.getXmlTaskType();
	}
}
