package com.walktour.gui.task.parsedata.model.task.ftp;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.control.config.ConfigFtp;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.FTPHostSetting;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;
import com.walktour.gui.task.parsedata.model.task.ftp.download.FTPDownloadTestConfig;
import com.walktour.gui.task.parsedata.model.task.ftp.upload.FTPUploadTestConfig;
import com.walktour.model.FtpServerModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

public class TaskFtpModel extends TaskModel { 

	/**
	 * 
	 */
	private static final long serialVersionUID = 4622229185910143551L;

	/**
	 * 解析或者构造此类必须先确认任务类型
	 * 
	 * @param taskType
	 */
	public TaskFtpModel(String taskType) {
		this.setTaskType(taskType);
		setTypeProperty(WalkCommonPara.TypeProperty_Net);
	}

	/**
	 * 按文件测试模式
	 */
	public final static int MODE_FILE = 0;
	/**
	 * 按时间测试模式
	 */
	public final static int MODE_TIME = 1;

	@SerializedName("timeOut")
	private int timeOut; // 超时时间
	@SerializedName("noAnswer")
	private int noAnswer; // 无响应时间(秒)
//	private String ftpServer; // FTP服务器
	@SerializedName("remoteFile")
	private String remoteFile; // FTP文件下载时，远程文件地址,上传时,生成远程文件名称
	@SerializedName("localFile")
	private String localFile = ""; // FTP上传时指定本地文件路径，如果为空上传FTP库自动生成的临时文件
	@SerializedName("user")
	private String user;
	@SerializedName("pass")
	private String pass;
	@SerializedName("psCall")
	private int psCall; // PS Call方式 0：否1：是
	@SerializedName("accessPoint")
	private int accessPoint; // 接入点 0,网络 1,wifi
//	private int threadNumber = 3; // 线程数 大于等于1的整数
	@SerializedName("port")
	private int port;
	@SerializedName("fileSize")
	private long fileSize; // FTP文件上传时，前端随机生成的上传文件的大小单位KB
//	private int pppfaildtimes = 3; // PPP拨号失败次数
	@SerializedName("pppInterval")
	private int pppInterval = 15; // PPP拨号间隔
	@SerializedName("loginTimes")
	private int loginTimes = 10; // 登陆失败次数
	@SerializedName("loginInterval")
	private int loginInterval = 5; // 登陆失败间隔
	@SerializedName("loginTimeOut")
	private int loginTimeOut = 60; // 登陆超时
	@SerializedName("tcpipCapture")
	private int tcpipCapture; // tcpip抓包 0：否，1：是
	@SerializedName("protocol")
	private int protocol; // 协议0，IPV4，1，IPV6
	@SerializedName("cache")
	private int cache; // 缓存大小
//	private int fileSource; // 选择文件上传方式 0为本地 1为自动
	@SerializedName("isPassiveMode")
	private int isPassiveMode; // 主被动模式
	/** 是否匿名 */
	@SerializedName("anonymous")
	private boolean anonymous = false;
	@SerializedName("maxThr")
	private int maxThr = 0; //

	private final static String FILESOURCE_LOCAL = "Local File";
	private final static String FILESOURCE_CREAT = "Creat File";

	private final static String PSCALL_FILE = "By File";
	private final static String PSCALL_TIME = "By Time";
	public static final String FTP_TRANSPORT_PROTOCOL_FTP="FTP";
	public static final String FTP_TRANSPORT_PROTOCOL_SFTP="SFTP";
	/**传输协议：默认选择0-FTP,1-SFTP***/
	private int transportProtocal=0;
	/** FTP DOWN **/
	@SerializedName("ftpDownloadTestConfig")
	private FTPDownloadTestConfig ftpDownloadTestConfig = new FTPDownloadTestConfig();
	/** FTP UP **/
	@SerializedName("ftpUploadTestConfig")
	private FTPUploadTestConfig ftpUploadTestConfig = new FTPUploadTestConfig();
	/** 网络参数设置 **/
	@SerializedName("networkConnectionSetting")
	private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();

	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 * 
	 * @author tangwq
	 * @return
	 */
	public String getTestPlanStr() {
		String ftpServer=getFtpServerName();
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());
		ConfigFtp ftp = new ConfigFtp();
		FtpServerModel serverModel = ftp.getFtpServerModel(ftpServer);

		testTask.append("timeOut =" + getTimeOut() + "\r\n");
		testTask.append("noAnswer =" + getNoAnswer() + "\r\n");
		testTask.append("remoteFile =" + getRemoteFile() + "\r\n");
		testTask.append("localFile =" + getLocalFile() + "\r\n");
		testTask.append("ftpServer =" + ftpServer + "\r\n");
		testTask.append("ftpIP =" + serverModel.getIp() + "\r\n");
		testTask.append("user =" + serverModel.getLoginUser() + "\r\n");
		testTask.append("pass =" + serverModel.getLoginPassword() + "\r\n");
		testTask.append("connectMode =" + (serverModel.getConnect_mode() == 1 ? "PASSIVE" : "PORT") + "\r\n");
		testTask.append("psCall =" + getPsCall() + "\r\n");
		testTask.append("accessPoint =" + accessPoint + "\r\n");
		testTask.append("threadNumber =" + getThreadNumber() + "\r\n");
		testTask.append("port =" + port + "\r\n");
		testTask.append("fileSize =" + getFileSize() * 1000 + "\r\n");
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

	/**
	 * @return the fileSource
	 */
	public int getFileSource() {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case FTPUpload:
			if (ftpUploadTestConfig.getFileSource().equalsIgnoreCase(FILESOURCE_LOCAL)) {
				return 0;
			}
			return 1;
		default:
			break;
		}
		return 1;
	}

	/**
	 * @param fileSource
	 *            the fileSource to set
	 */
	public void setFileSource(int fileSource) {

		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case FTPUpload:
			switch (fileSource) {
			case 0:
				ftpUploadTestConfig.setFileSource(FILESOURCE_LOCAL);
				break;
			case 1:
				ftpUploadTestConfig.setFileSource(FILESOURCE_CREAT);
				break;

			default:
				break;
			}
		default:
			break;
		}
	}

	public int getAccessPoint() {
		return accessPoint;
	}

	public void setAccessPoint(int accessPoint) {
		this.accessPoint = accessPoint;
	}

	/**
	 * 文件大小 (KByte)
	 * 
	 * @return
	 */
	public long getFileSize() {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case FTPUpload:
			return ftpUploadTestConfig.getFileSize();
		default:
			break;
		}
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case FTPUpload:
			ftpUploadTestConfig.setFileSize((int) fileSize);
		default:
			break;
		}
	}
	public int getTransportProtocal()
	{
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
			case FTPUpload:
				return ftpUploadTestConfig.getTransportProtocol().equals(FTP_TRANSPORT_PROTOCOL_FTP)?0:1;
			case FTPDownload:
				return ftpDownloadTestConfig.getTransportProtocol().equals(FTP_TRANSPORT_PROTOCOL_FTP)?0:1;
			default:
				break;
		}

		return transportProtocal;
	}

	public void setTransportProtocal(int transportProtocal)
	{
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
			case FTPUpload:
				if(transportProtocal==0) {
					ftpUploadTestConfig.setTransportProtocol(FTP_TRANSPORT_PROTOCOL_FTP);
				}else{
					ftpUploadTestConfig.setTransportProtocol(FTP_TRANSPORT_PROTOCOL_SFTP);
				}
				break;
			case FTPDownload:
				if(transportProtocal==0) {
					ftpDownloadTestConfig.setTransportProtocol(FTP_TRANSPORT_PROTOCOL_FTP);
				}else{
					ftpDownloadTestConfig.setTransportProtocol(FTP_TRANSPORT_PROTOCOL_SFTP);
				}
				break;
			default:
				break;
		}
	}
	public int getTimeOut() {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case FTPUpload:
			return ftpUploadTestConfig.getUploadDuration();
		case FTPDownload:
			return ftpDownloadTestConfig.getDownloadDuration();
		default:
			break;
		}
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case FTPUpload:
			ftpUploadTestConfig.setUploadDuration(timeOut);
			break;
		case FTPDownload:
			ftpDownloadTestConfig.setDownloadDuration(timeOut);
			break;
		default:
			break;
		}
	}
	public String getSendBuffer() {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
			case FTPUpload:
				return ftpUploadTestConfig.getSendBuffer();
			case FTPDownload:
				return ftpDownloadTestConfig.getSendBuffer();
			default:
				break;
		}
		return "";
	}

	public void setSendBuffer(String buffer) {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
			case FTPUpload:
				ftpUploadTestConfig.setSendBuffer(buffer);
				break;
			case FTPDownload:
				ftpDownloadTestConfig.setSendBuffer(buffer);
				break;
			default:
				break;
		}
	}

	public String getReceiveBuffer() {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
			case FTPUpload:
				return ftpUploadTestConfig.getReceBuffer();
			case FTPDownload:
				return ftpDownloadTestConfig.getReceBuffer();
			default:
				break;
		}
		return "";
	}

	public void setReceiveBuffer(String buffer) {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
			case FTPUpload:
				ftpUploadTestConfig.setReceBuffer(buffer);
				break;
			case FTPDownload:
				ftpDownloadTestConfig.setReceBuffer(buffer);
				break;
			default:
				break;
		}
	}
	public int getNoAnswer() {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case FTPUpload:
			return ftpUploadTestConfig.getNoDataTimeout();
		case FTPDownload:
			return ftpDownloadTestConfig.getNoDataTimeout();
		default:
			break;
		}
		return noAnswer;
	}

	public void setNoAnswer(int noAnswer) {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case FTPUpload:
			ftpUploadTestConfig.setNoDataTimeout(noAnswer);
			break;
		case FTPDownload:
			ftpDownloadTestConfig.setNoDataTimeout(noAnswer);
			break;
		default:
			break;
		}
	}

	public String getFtpServerName() {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case FTPUpload:
			return ftpUploadTestConfig.getFtpHostSetting().getSiteName();
		case FTPDownload:
			return ftpDownloadTestConfig.getFtpHostSetting().getSiteName();
		default:
			break;
		}
		return "";
	}

	public void setFtpServer(String ftpServer) {
		ConfigFtp ftp = new ConfigFtp();
		FtpServerModel serverModel = ftp.getFtpServerModel(ftpServer);
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case FTPUpload:
			ftpUploadTestConfig.getFtpHostSetting().setSiteName(ftpServer);
			ftpUploadTestConfig.getFtpHostSetting().setAddress(serverModel.getIp());
			ftpUploadTestConfig.getFtpHostSetting().setUserName(serverModel.getLoginUser());
			ftpUploadTestConfig.getFtpHostSetting().setPassword(serverModel.getLoginPassword());
			if (serverModel.getPort() != null && !serverModel.getPort().equals("")) {
				ftpUploadTestConfig.getFtpHostSetting().setPort(Integer.valueOf(serverModel.getPort()));
			}
			ftpUploadTestConfig.getFtpHostSetting().setCheck(true);
			ftpUploadTestConfig.getFtpHostSetting().setIsAnonymous(serverModel.isAnonymous());
			ftpUploadTestConfig.getFtpHostSetting()
					.setConnectionMode(serverModel.getConnect_mode() == FtpServerModel.CONNECT_MODE_PASSIVE
							? FTPHostSetting.CONNECTMODE_PASSIVE : FTPHostSetting.CONNECTMODE_PORT);
			break;
		case FTPDownload:
			ftpDownloadTestConfig.getFtpHostSetting().setSiteName(ftpServer);
			ftpDownloadTestConfig.getFtpHostSetting().setAddress(serverModel.getIp());
			ftpDownloadTestConfig.getFtpHostSetting().setUserName(serverModel.getLoginUser());
			ftpDownloadTestConfig.getFtpHostSetting().setPassword(serverModel.getLoginPassword());
			if (serverModel.getPort() != null && !serverModel.getPort().equals("")) {
				ftpDownloadTestConfig.getFtpHostSetting().setPort(Integer.valueOf(serverModel.getPort()));
			}
			ftpDownloadTestConfig.getFtpHostSetting().setCheck(true);
			ftpDownloadTestConfig.getFtpHostSetting().setIsAnonymous(serverModel.isAnonymous());
			ftpDownloadTestConfig.getFtpHostSetting()
					.setConnectionMode(serverModel.getConnect_mode() == FtpServerModel.CONNECT_MODE_PASSIVE
							? FTPHostSetting.CONNECTMODE_PASSIVE : FTPHostSetting.CONNECTMODE_PORT);
			break;
		default:
			break;
		}
 
	}

	public String getRemoteFile() {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case FTPUpload:
			return ftpUploadTestConfig.getRemoteDirectory();
		case FTPDownload:
			return ftpDownloadTestConfig.getDownloadFile();
		default:
			break;
		}

		return remoteFile;
	}

	public String getLocalFile() {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case FTPUpload:
			return ftpUploadTestConfig.getLocalFile();
		default:
			break;
		}
		return localFile;
	}

	public void setLocalFile(String localFile) {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case FTPUpload:
			ftpUploadTestConfig.setLocalFile(localFile);
		default:
			break;
		}
	}

	public void setRemoteFile(String remoteFile) {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case FTPUpload:
			ftpUploadTestConfig.setRemoteDirectory(remoteFile);
		case FTPDownload:
			ftpDownloadTestConfig.setDownloadFile(remoteFile);
		default:
			break;
		}
	}

	public String getThreadMode() {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
			case FTPUpload:
				return ftpUploadTestConfig.getThreadMode();
			case FTPDownload:
				return ftpDownloadTestConfig.getThreadMode();
			default:
				break;
		}

		return remoteFile;
	}
	public void setThreadMode(String threadMode) {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
			case FTPUpload:
				ftpUploadTestConfig.setThreadMode(threadMode);
			case FTPDownload:
				ftpDownloadTestConfig.setThreadMode(threadMode);
			default:
				break;
		}
	}


	public int getPsCall() {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case FTPDownload:
			if (ftpDownloadTestConfig.getPsCallMode().equalsIgnoreCase(PSCALL_FILE)) {
				return 0;
			}
			return 1;
		case FTPUpload:
			if (ftpUploadTestConfig.getPsCallMode().equalsIgnoreCase(PSCALL_FILE)) {
				return 0;
			}
			return 1;
		default:
			break;
		}

		return psCall;
	}

	public void setPsCall(int psCall) {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case FTPDownload:
			switch (psCall) {
			case 0:
				ftpDownloadTestConfig.setPsCallMode(PSCALL_FILE);
				break;
			case 1:
				ftpDownloadTestConfig.setPsCallMode(PSCALL_TIME);
				break;
			default:
				break;
			}
			break;
		case FTPUpload:
			switch (psCall) {
			case 0:
				ftpUploadTestConfig.setPsCallMode(PSCALL_FILE);
				break;
			case 1:
				ftpUploadTestConfig.setPsCallMode(PSCALL_TIME);
				break;

			default:
				break;
			}
			break;
		default:
			break;
		}
	}

	public int getThreadNumber() {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case FTPDownload:
			return ftpDownloadTestConfig.getThreadCount();
		case FTPUpload:
			return ftpUploadTestConfig.getThreadCount();
		default:
			break;
		}
		return 3;
	}

	public void setThreadNumber(int threadNumber) {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case FTPDownload:
			ftpDownloadTestConfig.setThreadCount(threadNumber);
			break;
		case FTPUpload:
			ftpUploadTestConfig.setThreadCount(threadNumber);
			break;
		default:
			break;
		}

	}

	public int getPort() {
		return this.port;
	}

	public String getUser() {
		return this.user;
	}

	public String getPass() {
		return this.pass;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	/**
	 * @return the pppfaildtimes
	 */
	public int getPppfaildtimes() {
		return networkConnectionSetting.getReconnectCount();
	}

	/**
	 * @param pppfaildtimes
	 *            the pppfaildtimes to set
	 */
	public void setPppfaildtimes(int pppfaildtimes) {
		networkConnectionSetting.setReconnectCount(pppfaildtimes);
	}

	/**
	 * @return the pppInterval
	 */
	public int getPppInterval() {
		return pppInterval;
	}

	/**
	 * @param pppInterval
	 *            the pppInterval to set
	 */
	public void setPppInterval(int pppInterval) {
		this.pppInterval = pppInterval;
	}

	/**
	 * @return the loginTimes
	 */
	public int getLoginTimes() {
		return loginTimes;
	}

	/**
	 * @param loginTimes
	 *            the loginTimes to set
	 */
	public void setLoginTimes(int loginTimes) {
		this.loginTimes = loginTimes;
	}

	/**
	 * @return the loginTimeOut
	 */
	public int getLoginTimeOut() {
		return loginTimeOut;
	}

	/**
	 * @param loginTimeOut
	 *            the loginTimeOut to set
	 */
	public void setLoginTimeOut(int loginTimeOut) {
		this.loginTimeOut = loginTimeOut;
	}

	/**
	 * @return the loginInterval
	 */
	public int getLoginInterval() {
		return loginInterval;
	}

	/**
	 * @param loginInterval
	 *            the loginInterval to set
	 */
	public void setLoginInterval(int loginInterval) {
		this.loginInterval = loginInterval;
	}

	/**
	 * @return the tcpipCapture
	 */
	public int getTcpipCapture() {
		return tcpipCapture;
	}

	/**
	 * @param tcpipCapture
	 *            the tcpipCapture to set
	 */
	public void setTcpipCapture(int tcpipCapture) {
		this.tcpipCapture = tcpipCapture;
	}

	/**
	 * @return the protocol
	 */
	public int getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol
	 *            the protocol to set
	 */
	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	/**
	 * @return the cache
	 */
	public int getCache() {
		return cache;
	}

	/**
	 * @param cache
	 *            the cache to set
	 */
	public void setCache(int cache) {
		this.cache = cache;
	}

	public int getIsPassiveMode() {
		return isPassiveMode;
	}

	public void setIsPassiveMode(int isPassiveMode) {
		this.isPassiveMode = isPassiveMode;
	}

	public boolean isAnonymous() {
		return anonymous;
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}

	public int getMaxThr() {
		return maxThr;
	}

	public void setMaxThr(int maxThr) {
		this.maxThr = maxThr;
	}

	public FTPDownloadTestConfig getFtpDownloadTestConfig() {
		return ftpDownloadTestConfig;
	}

	public void setFtpDownloadTestConfig(FTPDownloadTestConfig ftpDownloadTestConfig) {
		this.ftpDownloadTestConfig = ftpDownloadTestConfig;
	}

	public FTPUploadTestConfig getFtpUploadTestConfig() {
		return ftpUploadTestConfig;
	}

	public void setFtpUploadTestConfig(FTPUploadTestConfig ftpUploadTestConfig) {
		this.ftpUploadTestConfig = ftpUploadTestConfig;
	}

	public NetworkConnectionSetting getNetworkConnectionSetting() {
		return networkConnectionSetting;
	}

	public void setNetworkConnectionSetting(NetworkConnectionSetting networkConnectionSetting) {
		this.networkConnectionSetting = networkConnectionSetting;
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
				if (tagName.equals("FTPDownloadTestConfig")) {
					ftpDownloadTestConfig.parseXml(parser);
				} else if (tagName.equals("FTPUploadTestConfig")) {
					ftpUploadTestConfig.parseXml(parser);
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

		if (this.getTaskType().equals(WalkStruct.TaskType.FTPDownload.name())) {
			if (null != ftpDownloadTestConfig) {
				ftpDownloadTestConfig.writeXml(serializer);
			}
		} else {
			if (null != ftpUploadTestConfig) {
				ftpUploadTestConfig.writeXml(serializer);
			}
		}

		if (null != networkConnectionSetting) {
			networkConnectionSetting.writeXml(serializer);
		}
	}

	@Override
	public String getServerTaskType() {
		return WalkStruct.TaskType.valueOf(this.getTaskType()).getXmlTaskType();
	}

}
