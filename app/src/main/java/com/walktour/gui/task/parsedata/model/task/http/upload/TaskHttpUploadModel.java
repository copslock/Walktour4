/**
 * 
 */
package com.walktour.gui.task.parsedata.model.task.http.upload;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

/**
 * @author Jihong Xie Http Upload业务实体对象
 */
public class TaskHttpUploadModel extends TaskModel { 

	/**
	 * 
	 */
	private static final long serialVersionUID = -6840059185450554059L;
	/** 本地文件 */
	public static final int LOCAL_FILE = 0;
	/** 自动创建文件 */
	public static final int CREATE_FILE = 1;

	/** 上传模式按文件 */
	public static final int BY_FILE = 0;
	/** 上传模式按时长 */
	public static final int BY_TIME = 1;
	public static final int SERVER_TYPE_HTTPS = 0;
	public static final int SERVER_TYPE_YOUTUBE = 1;
	public static final int SERVER_TYPE_BAIDUYUN = 2;
	public TaskHttpUploadModel() {
		setTaskName("HTTP Upload");
		setRepeat(10);
		setDisConnect(1);
		setInterVal(15);
		setNoDataTimeout(15);
		setFilePath("/");
		setServerType(1);
		setFileSource(CREATE_FILE);
		setFileSize(512);
		setTimeout(1200);
		setTypeProperty(WalkCommonPara.TypeProperty_Net);
		setTaskType(WalkStruct.TaskType.HttpUpload.name());
		setServerPath("/ding-up");
	}

	@SerializedName("httpUpTestConfig")
	private HTTPUpTestConfig httpUpTestConfig = new HTTPUpTestConfig();
	@SerializedName("networkConnectionSetting")
	private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();

	/**
	 * 将测试任务数据拼接成字符
	 * 
	 * @return
	 */
	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());

		testTask.append("TimeOut =" + getTimeout() + "\r\n");
		testTask.append("TestMode =" + getTestMode() + "\r\n");
		testTask.append("ServerType =" + getServerType() + "\r\n");
		testTask.append("Username =" + getUsername() + "\r\n");
		testTask.append("password =" + getPassword() + "\r\n");
		testTask.append("Url =" + getUrl() + "\r\n");
		testTask.append("FileSource =" + getFileSource() + "\r\n");
		testTask.append("FilePath =" + getFilePath() + "\r\n");
		testTask.append("NoDataTimeout =" + getNoDataTimeout() + "\r\n");
		testTask.append("accountKey =" + getAccountKey() + "\r\n");
		testTask.append("secretKey =" + getSecretKey() + "\r\n");
		testTask.append("serverPath =" + getServerPath() + "\r\n");
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

	public int getTimeout() {
		if(this.getTestMode()==0) {
			return httpUpTestConfig.getUploadTimeout();
		}else{
			return httpUpTestConfig.getUploadDuration();
		}
	}

	public void setTimeout(int timeout) {
		if(this.getTestMode()==0){
			httpUpTestConfig.setUploadTimeout(timeout);
		}else{
			httpUpTestConfig.setUploadDuration(timeout);
		}

	}

	public int getServerType() {
		if(httpUpTestConfig.getWebsiteType().equalsIgnoreCase(HTTPUpTestConfig.WEBSITETYPE_HTTPS)){
			return 0;
		}else if(httpUpTestConfig.getWebsiteType().equalsIgnoreCase(HTTPUpTestConfig.WEBSITETYPE_YOUTUBE)){
			return 1;
		}
		return 2;
	}

	public void setServerType(int serverType) {
		switch (serverType) {
		case 0:
			httpUpTestConfig.setWebsiteType(HTTPUpTestConfig.WEBSITETYPE_HTTPS);
			break;
		case 0+1:
			httpUpTestConfig.setWebsiteType(HTTPUpTestConfig.WEBSITETYPE_YOUTUBE);
			break;
		case 1+1:
			httpUpTestConfig.setWebsiteType(HTTPUpTestConfig.WEBSITETYPE_BAIDU);
			break;
		default:
			break;
		}
	}

	public String getUsername() {
		return httpUpTestConfig.getYoutubeAccount().getUserName();
	}

	public void setUsername(String username) {
		httpUpTestConfig.getYoutubeAccount().setUserName(username);
	}

	public String getPassword() {
		return httpUpTestConfig.getYoutubeAccount().getPassword();
	}

	public void setPassword(String password) {
		httpUpTestConfig.getYoutubeAccount().setPassword(password);
	}

	public String getUrl() {
		return httpUpTestConfig.getUrl();
	}

	public void setUrl(String url) {
		httpUpTestConfig.setUrl(url);
	}

	public int getFileSource() {
		if(httpUpTestConfig.getFileSource().equalsIgnoreCase(HTTPUpTestConfig.FileSource_Local)){
			return 0;
		}
		return 1;
	}

	public void setFileSource(int fileSource) {
		switch (fileSource) {
		case 0:
			httpUpTestConfig.setFileSource(HTTPUpTestConfig.FileSource_Local);
			break;
		case 1:
			httpUpTestConfig.setFileSource(HTTPUpTestConfig.FileSource_Creat);
			break;
		default:
			break;
		}
	}

	public String getFilePath() {
		return httpUpTestConfig.getLocalFile();
	}

	public void setFilePath(String filePath) {
		httpUpTestConfig.setLocalFile(filePath);
	}

	public int getNoDataTimeout() {
		return httpUpTestConfig.getNoDataTimeout();
	}

	public void setNoDataTimeout(int noDataTimeout) {
		httpUpTestConfig.setNoDataTimeout(noDataTimeout);
	}

	public int getTestMode() {
		if(httpUpTestConfig.getPsCallMode().equalsIgnoreCase(HTTPUpTestConfig.PSCallMode_File)){
			return 0;
		}
		return 1;
	}

	public void setTestMode(int testMode) {
		switch (testMode) {
		case 0:
			httpUpTestConfig.setPsCallMode(HTTPUpTestConfig.PSCallMode_File);
			break;
		case 1:
			httpUpTestConfig.setPsCallMode(HTTPUpTestConfig.PSCallMode_Time);
			break;
		default:
			break;
		}
	}

	/**
	 * @return the fileSizw
	 */
	public int getFileSize() {
		return httpUpTestConfig.getFileSize();
	}

	/**
	 * @param fileSizw
	 *            the fileSizw to set
	 */
	public void setFileSize(int fileSize) {
		httpUpTestConfig.setFileSize(fileSize);
	}

	/**
	 * 帐号类型 0,开发者账号 1专用帐号
	 * 
	 * @return
	 */
	public int getAccountType() {
		if(httpUpTestConfig.getBaiduYunAccount().getIsDeveloperAccount()){
			return 0;
		}
		return 1;
	}

	public void setAccountType(int accountType) {
		switch (accountType) {
		case 0:
			httpUpTestConfig.getBaiduYunAccount().setIsDeveloperAccount(true);
			break;
		case 1:
			httpUpTestConfig.getBaiduYunAccount().setIsDeveloperAccount(false);
			break;
		default:
			break;
		}
	}

	public String getAccountKey() {
		return httpUpTestConfig.getBaiduYunAccount().getApiKey();
	}

	public void setAccountKey(String accountKey) {
		httpUpTestConfig.getBaiduYunAccount().setApiKey(accountKey);
	}

	public String getSecretKey() {
		return httpUpTestConfig.getBaiduYunAccount().getSecretKey();
	}

	public void setSecretKey(String secretKey) {
		httpUpTestConfig.getBaiduYunAccount().setSecretKey(secretKey);
	}

	public String getServerPath() {
		return httpUpTestConfig.getBaiduYunAccount().getUploadPath();
	}

	public void setServerPath(String serverPath) {
		httpUpTestConfig.getBaiduYunAccount().setUploadPath(serverPath);
	}

	public HTTPUpTestConfig getHttpUpTestConfig() {
		return httpUpTestConfig;
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
				if (tagName.equals("HTTPUpTestConfig")) { 
					httpUpTestConfig.parseXml(parser);
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
		if (null != httpUpTestConfig)
			httpUpTestConfig.writeXml(serializer);
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}
	
	@Override
	public String getServerTaskType() { 
		return WalkStruct.TaskType.HttpUpload.getXmlTaskType();
	}
}
