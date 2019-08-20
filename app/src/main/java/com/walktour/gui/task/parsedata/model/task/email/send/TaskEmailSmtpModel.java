package com.walktour.gui.task.parsedata.model.task.email.send;

import android.os.Environment;

import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.control.bean.FileOperater;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.util.List;
import java.util.Map;

public class TaskEmailSmtpModel extends TaskModel { 

	/**
	 * 
	 */
	private static final long serialVersionUID = -7758103085498297351L;

	public TaskEmailSmtpModel() {
		setTypeProperty(WalkCommonPara.TypeProperty_Net);
		setTaskType(WalkStruct.TaskType.EmailSmtp.toString());
	}

	// 附件大小限制,界面上如此限制
	public static final long FILE_SIZE_LIMIT = 11 * 1000 * 1000;
	public static String LOG_PATH_MEMORY_DIR = File.separator + "MailSendTempFile";
	public static String LOG_PATH_SDCARD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator + "walktour" + File.separator + "MailSendTempFile";
	private EmailSendTestConfig emailSendTestConfig = new EmailSendTestConfig();
	private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();

	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 * 
	 * @author tangwq
	 * @return
	 */
	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());

		testTask.append("TimeOut =" + this.getTimeOut() + "\r\n");
		testTask.append("EmailServer =" + this.getEmailServer() + "\r\n");
		testTask.append("Port =" + this.getPort() + "\r\n");
		testTask.append("Account =" + this.getAccount() + "\r\n");
		testTask.append("Password =" + this.getPassword() + "\r\n");
		testTask.append("SmtpAuthentication =" +this.getSmtpAuthentication() + "\r\n");
		testTask.append("To =" + this.getTo() + "\r\n");
		testTask.append("Subject =" + this.getSubject() + "\r\n");
		testTask.append("Body =" + this.getBody() + "\r\n");
		testTask.append("Adjunct =" + this.getAdjunct() + "\r\n");
		testTask.append("FileSize =" + this.getFileSize() + "\r\n");
		testTask.append("UseSSL =" + this.getUseSSL() + "\r\n");

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
	/**
	 * 创建一个临时的附件文件 如果SD卡挂载则在SD卡中生成该文件 反之在安装目录中生成该文件
	 * 
	 * @param installPath
	 *            安装目录
	 * 
	 */
	public void createSmtpAttachFile(String installPath) {
		File file;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			file = new File(LOG_PATH_SDCARD_DIR);
		} else {
			file = new File(installPath + LOG_PATH_MEMORY_DIR);
		}

		if (!file.exists()) {
			FileOperater.createTempFile(file.getAbsolutePath(), 500 * 1000);
		}
		emailSendTestConfig.setLocalFile(file.getAbsolutePath());
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

	public int getTimeOut() {
		return emailSendTestConfig.getSendTimeout();
	}

	public void setTimeOut(int timeOut) {
		emailSendTestConfig.setSendTimeout(timeOut);
	}

	public String getEmailServer() {
		List<EmailServerSendConfig> list = emailSendTestConfig.getEmailServerList();
		EmailServerSendConfig select = null;
		for (EmailServerSendConfig er : list) {
			if (er.isCheck()) {
				select = er;
				break;
			}
		}
		if (null != select) {
			return select.getSendConfig().getServerAddress();
		}
		return "";

	}

	public void setEmailServer(String emailServer) {
		List<EmailServerSendConfig> list = emailSendTestConfig.getEmailServerList();
		EmailServerSendConfig select = null;
		for (EmailServerSendConfig er : list) {
			if (er.isCheck()) {
				select = er;
				break;
			}
			er.setCheck(false);
		}
		boolean isUpdate = false;
		if (null != select) {
			select.getSendConfig().setServerAddress(emailServer);
			isUpdate = true;
		}
		if (!isUpdate) {// 如果没更新，则直接新增一个
			EmailServerSendConfig config = new EmailServerSendConfig();
			config.getSendConfig().setServerAddress(emailServer);
			config.setCheck(true);
			list.add(config);
		}
	}

	public int getPort() {
		List<EmailServerSendConfig> list = emailSendTestConfig.getEmailServerList();
		EmailServerSendConfig select = null;
		for (EmailServerSendConfig er : list) {
			if (er.isCheck()) {
				select = er;
				break;
			}
		}
		if (null != select) {
			return select.getSendConfig().getServerPort();
		}
		return 0;
	}

	public void setPort(int port) {
		List<EmailServerSendConfig> list = emailSendTestConfig.getEmailServerList();
		String serverAdd = this.getEmailServer();
		boolean isUpdate = false;
		for (EmailServerSendConfig er : list) {
			er.setCheck(false);
			if (er.getSendConfig().getServerAddress().equals(serverAdd)) {
				er.setCheck(true);
				er.getSendConfig().setServerPort(port);
				isUpdate = true;
				break;
			}
		}
		if (!isUpdate) {// 没更新,则直接新增一个
			EmailServerSendConfig config = new EmailServerSendConfig();
			config.setServerName(this.getEmailServer());
			config.getSendConfig().setServerPort(port);
			config.setCheck(true);
			list.add(config);
		}
	}

	public String getAccount() {
		List<EmailServerSendConfig> list = emailSendTestConfig.getEmailServerList();
		EmailServerSendConfig select = null;
		for (EmailServerSendConfig er : list) {
			if (er.isCheck()) {
				select = er;
				break;
			}
		}
		if (null != select) {
			return select.getAccountConfig().getUserName();
		}
		return "";
	}

	public void setAccount(String account) {
		List<EmailServerSendConfig> list = emailSendTestConfig.getEmailServerList();
		String serverAdd = this.getEmailServer();
		boolean isUpdate = false;
		for (EmailServerSendConfig er : list) {
			er.setCheck(false);
			if (er.getSendConfig().getServerAddress().equals(serverAdd)) {
				er.setCheck(true);
				er.getAccountConfig().setUserName(account);
				isUpdate = true;
				break;
			}
		}
		if (!isUpdate) {// 没更新,则直接新增一个
			EmailServerSendConfig config = new EmailServerSendConfig();
			config.setServerName(this.getEmailServer());
			config.getAccountConfig().setUserName(account);
			config.setCheck(true);
			list.add(config);
		}
	}

	public String getPassword() {
		List<EmailServerSendConfig> list = emailSendTestConfig.getEmailServerList();
		EmailServerSendConfig select = null;
		for (EmailServerSendConfig er : list) {
			if (er.isCheck()) {
				select = er;
				break;
			}
		}
		if (null != select) {
			return select.getAccountConfig().getPassword();
		}
		return "";
	}

	public void setPassword(String password) {
		List<EmailServerSendConfig> list = emailSendTestConfig.getEmailServerList();
		String serverAdd = this.getEmailServer();
		boolean isUpdate = false;
		for (EmailServerSendConfig er : list) {
			er.setCheck(false);
			if (er.getSendConfig().getServerAddress().equals(serverAdd)) {
				er.setCheck(true);
				er.getAccountConfig().setPassword(password);
				isUpdate = true;
				break;
			}
		}
		if (!isUpdate) {// 没更新,则直接新增一个
			EmailServerSendConfig config = new EmailServerSendConfig();
			config.setServerName(this.getEmailServer());
			config.getAccountConfig().setPassword(password);
			config.setCheck(true);
			list.add(config);
		}
	}

	public int getSmtpAuthentication() {
		return emailSendTestConfig.getIsAuthentcation();
	}

	public void setSmtpAuthentication(int smtpAuthentication) {
		emailSendTestConfig.setIsAuthentcation(smtpAuthentication);
	}

	public String getTo() {
		return emailSendTestConfig.getReceiverAddress();
	}

	public void setTo(String to) {
		emailSendTestConfig.setReceiverAddress(to);
	}

	public String getSubject() {
		return emailSendTestConfig.getEmailSubject();
	}

	public void setSubject(String subject) {
		emailSendTestConfig.setEmailSubject(subject);
	}

	public String getBody() {
		return emailSendTestConfig.getEmailBody();
	}

	public void setBody(String body) {
		emailSendTestConfig.setEmailBody(body);
	}

	public String getAdjunct() {
		return emailSendTestConfig.getLocalFile();
	}

	public void setAdjunct(String adjunct) {
		emailSendTestConfig.setLocalFile(adjunct);
	}

	public int getFileSize() {
		return emailSendTestConfig.getFileSize();
	}

	public void setFileSize(int fileSize) {
		emailSendTestConfig.setFileSize(fileSize);
	}

	/**
	 * @return the fileSource
	 */
	public int getFileSource() {
		if (emailSendTestConfig.getFileSource().equals(EmailSendTestConfig.FILESOURCE_CREATE)) {
			return 1;
		}
		return 0;
	}

	/**
	 * @param fileSource
	 *            the fileSource to set
	 */
	public void setFileSource(int fileSource) {
		if (fileSource == 1) {
			emailSendTestConfig.setFileSource(EmailSendTestConfig.FILESOURCE_CREATE);
		} else {
			emailSendTestConfig.setFileSource(EmailSendTestConfig.FILESOURCE_LOCAL);
		}

	}

	@Override
	public String toString() {
		return "timeOut=" + this.getTimeOut() + ",emailServer=" + this.getEmailServer() + ",port=" + this.getPort()
				+ ",account=" + this.getAccount() + ",password=" + this.getPassword() + ",smtpAuthentication="
				+ this.getSmtpAuthentication() + ",to=" +this.getTo() + ",subject" + this.getSubject() + ",body=" + this.getBody()
				+ ",adjunct=" + this.getAdjunct();

	}

	public int getPsCall() {
		if (emailSendTestConfig.getPsCallMode().equals(EmailSendTestConfig.PSCALLMODEL_BYFILE))
			return 0;
		return 1;
	}

	public void setPsCall(int psCall) {
		if (psCall == 0) {
			emailSendTestConfig.setPsCallMode(EmailSendTestConfig.PSCALLMODEL_BYFILE);
		} else {
			emailSendTestConfig.setPsCallMode(EmailSendTestConfig.PSCALLMODEL_BYTIME);
		}
	}

	public int getUseSSL() {
		List<EmailServerSendConfig> list = emailSendTestConfig.getEmailServerList();
		EmailServerSendConfig select = null;
		for (EmailServerSendConfig er : list) {
			if (er.isCheck()) {
				select = er;
				break;
			}
		}
		if (null != select) {
			if (select.getSendConfig().getSecurityProtocol().equals(SendConfig.PROTOCOL_SSL)) {
				return 1;
			}else if (select.getSendConfig().getSecurityProtocol().equals(SendConfig.PROTOCOL_NONE)) {
				return 0;
			}else 
				return 2;
		}
		return 0;
	}

	public void setUseSSL(int useSSL) {
		List<EmailServerSendConfig> list = emailSendTestConfig.getEmailServerList();
		String serverAdd = this.getEmailServer();
		boolean isUpdate = false;
		for (EmailServerSendConfig config : list) {
			config.setCheck(false);
			if (config.getSendConfig().getServerAddress().equals(serverAdd)) {
				config.setCheck(true);
				if (useSSL == 1)
					config.getSendConfig().setSecurityProtocol(SendConfig.PROTOCOL_SSL);
				else if(useSSL == 2)
					config.getSendConfig().setSecurityProtocol(SendConfig.PROTOCOL_TLS);
				else
					config.getSendConfig().setSecurityProtocol(SendConfig.PROTOCOL_NONE);
				isUpdate = true;
				break;
			}
		}
		if (!isUpdate) {// 没更新,则直接新增一个
			EmailServerSendConfig config = new EmailServerSendConfig();
			config.setServerName(this.getEmailServer());
			if (useSSL == 1)
				config.getSendConfig().setSecurityProtocol(SendConfig.PROTOCOL_SSL);
			else if(useSSL == 2)
				config.getSendConfig().setSecurityProtocol(SendConfig.PROTOCOL_TLS);
			else
				config.getSendConfig().setSecurityProtocol(SendConfig.PROTOCOL_NONE);
			config.setCheck(true);
			list.add(config);
		}
	}

//	public String getServerType() {
//		List<EmailServerSendConfig> list = emailSendTestConfig.getEmailServerList();
//		EmailServerSendConfig select = null;
//		for (EmailServerSendConfig er : list) {
//			if (er.isCheck()) {
//				select = er;
//				break;
//			}
//			er.setCheck(false);
//		}
//		if (null != select) {
//			return select.getSendConfig().getServerType();
//		}
//		return SendConfig.TYPE_POP3;
//	}
//
//	public void setServerType(String serverType) {
//		List<EmailServerSendConfig> list = emailSendTestConfig.getEmailServerList();
//		String serverAdd = this.getEmailServer();
//		boolean isUpdate = false;
//		for (EmailServerSendConfig er : list) {
//			er.setCheck(false);
//			if (er.getSendConfig().getServerAddress().equals(serverAdd)) {
//				er.setCheck(true);
//				er.getSendConfig().setServerType(serverType);
//				isUpdate = true;
//				break;
//			}
//		}
//		if (!isUpdate) {// 没更新,则直接新增一个
//			EmailServerSendConfig config = new EmailServerSendConfig();
//			config.setServerName(this.getEmailServer());
//			config.getSendConfig().setServerType(serverType);
//			config.setCheck(true);
//			list.add(config);
//		}
//	}

	public EmailSendTestConfig getEmailSendTestConfig() {
		return emailSendTestConfig;
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
				if (tagName.equals("EmailSendTestConfig")) {
					emailSendTestConfig.parseXml(parser);
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
		if (null != emailSendTestConfig)
			emailSendTestConfig.writeXml(serializer);
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}

	@Override
	public String getServerTaskType() {
		return WalkStruct.TaskType.EmailSmtp.getXmlTaskType();
	}
}
