package com.walktour.gui.task.parsedata.model.task.email.sendreceive;

import android.os.Environment;

import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.control.bean.FileOperater;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;
import com.walktour.gui.task.parsedata.model.task.email.receive.ReceiveConfig;
import com.walktour.gui.task.parsedata.model.task.email.send.SendConfig;
import com.walktour.gui.task.parsedata.model.task.email.send.TaskEmailSmtpModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 邮件自发自收测试模型
 * 
 * @author maosen.zhang
 *
 */
public class TaskEmailSmtpPop3Model extends TaskModel { 

	/**
	 * 
	 */
	private static final long serialVersionUID = 7166923197059312126L;

	public TaskEmailSmtpPop3Model() {
		setTypeProperty(WalkCommonPara.TypeProperty_Net);
		setTaskType(WalkStruct.TaskType.EmailSmtpAndPOP.toString());
	}

	/** SMTP服务器身份验证 0：否1：是 */
	private int smtpAuthentication;

	
	private EmailSelfTestConfig emailSelfTestConfig = new EmailSelfTestConfig();
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
		testTask.append("Account =" + this.getAccount() + "\r\n");
		testTask.append("Password =" + this.getPassword() + "\r\n");
		testTask.append("SmtpServer =" + this.getSmtpServer() + "\r\n");
		testTask.append("SmtpPort =" + this.getSmtpPort() + "\r\n");
		testTask.append("SmtpAuthentication =" + smtpAuthentication + "\r\n");
		testTask.append("Pop3Server =" + this.getPop3Server() + "\r\n");
		testTask.append("Pop3Port =" + this.getPop3Port() + "\r\n");
		testTask.append("Subject =" + this.getSubject() + "\r\n");
		testTask.append("Body =" + this.getBody() + "\r\n");
		testTask.append("Adjunct =" + this.getAdjunct() + "\r\n");
		testTask.append("SendTimeOut =" + emailSelfTestConfig.getSendTimeout() + "\r\n");
		testTask.append("ReceiveTimeOut =" + emailSelfTestConfig.getReceiveTimeout() + "\r\n");
		return testTask.toString();
	}
	@Override
	public int getTypeProperty() { 
		if(networkConnectionSetting.getConnectionProtocol().equals(NetworkConnectionSetting.ConnectionProtocol_WLAN))
			return WalkCommonPara.TypeProperty_Wlan;
		return WalkCommonPara.TypeProperty_Net;
	}



	@Override
	public void setTypeProperty(int typeProperty) {
		if(typeProperty==WalkCommonPara.TypeProperty_Wlan){
			networkConnectionSetting.setConnectionProtocol(NetworkConnectionSetting.ConnectionProtocol_WLAN);
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
			file = new File(TaskEmailSmtpModel.LOG_PATH_SDCARD_DIR);
		} else {
			file = new File(installPath + TaskEmailSmtpModel.LOG_PATH_MEMORY_DIR);
		}

		if (!file.exists()) {
			FileOperater.createTempFile(file.getAbsolutePath(), 500 * 1000);
		}
		emailSelfTestConfig.setLocalFile(file.getAbsolutePath());
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

	public String getSmtpServer() {
		List<EmailServerConfig> list = emailSelfTestConfig.getEmailServerList();
		EmailServerConfig select = null;
		for (EmailServerConfig er : list) {
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

	public void setSmtpServer(String smtpServer) {
		List<EmailServerConfig> list = emailSelfTestConfig.getEmailServerList();
		EmailServerConfig select = null;
		for (EmailServerConfig er : list) {
			if (er.isCheck()) {
				select = er;
				break;
			}
			er.setCheck(false);
		}
		boolean isUpdate = false;
		if (null != select) {
			select.getSendConfig().setServerAddress(smtpServer);
			isUpdate = true;
		}
		if (!isUpdate) {// 如果没更新，则直接新增一个
			EmailServerConfig config = new EmailServerConfig();
			config.getSendConfig().setServerAddress(smtpServer);
			config.setCheck(true);
			list.add(config);
		}
	}

	public int getSmtpPort() {
		List<EmailServerConfig> list = emailSelfTestConfig.getEmailServerList();
		EmailServerConfig select = null;
		for (EmailServerConfig er : list) {
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

	public void setSmtpPort(int smtpPort) {
		List<EmailServerConfig> list = emailSelfTestConfig.getEmailServerList();
		String sendServer = this.getSmtpServer();
		String receServer = this.getPop3Server();
		boolean isUpdate = false;
		for (EmailServerConfig er : list) {
			er.setCheck(false);
			if (er.getSendConfig().getServerAddress().equals(sendServer)
					&& er.getReceiveConfig().getServerAddress().equals(receServer)) {
				er.setCheck(true);
				er.getSendConfig().setServerPort(smtpPort);
				isUpdate = true;
				break;
			}
		}
		if (!isUpdate) {// 没更新,则直接新增一个
			EmailServerConfig config = new EmailServerConfig();
			config.getSendConfig().setServerPort(smtpPort);
			config.setCheck(true);
			list.add(config);
		}
	}

	public String getPop3Server() {
		List<EmailServerConfig> list = emailSelfTestConfig.getEmailServerList();
		EmailServerConfig select = null;
		for (EmailServerConfig er : list) {
			if (er.isCheck()) {
				select = er;
				break;
			}
		}
		if (null != select) {
			return select.getReceiveConfig().getServerAddress();
		}
		return "";
	}

	public void setPop3Server(String pop3Server) {
		List<EmailServerConfig> list = emailSelfTestConfig.getEmailServerList();
		EmailServerConfig select = null;
		for (EmailServerConfig er : list) {
			if (er.isCheck()) {
				select = er;
				break;
			}
			er.setCheck(false);
		}
		boolean isUpdate = false;
		if (null != select) {
			select.getReceiveConfig().setServerAddress(pop3Server);
			isUpdate = true;
		}
		if (!isUpdate) {// 如果没更新，则直接新增一个
			EmailServerConfig config = new EmailServerConfig();
			config.getReceiveConfig().setServerAddress(pop3Server);
			config.setCheck(true);
			list.add(config);
		}
	}

	public int getPop3Port() {
		List<EmailServerConfig> list = emailSelfTestConfig.getEmailServerList();
		EmailServerConfig select = null;
		for (EmailServerConfig er : list) {
			if (er.isCheck()) {
				select = er;
				break;
			}
		}
		if (null != select) {
			return select.getReceiveConfig().getServerPort();
		}
		return 0;
	}

	public void setPop3Port(int pop3Port) {
		List<EmailServerConfig> list = emailSelfTestConfig.getEmailServerList();
		String sendServer = this.getSmtpServer();
		String receServer = this.getPop3Server();
		boolean isUpdate = false;
		for (EmailServerConfig er : list) {
			er.setCheck(false);
			if (er.getSendConfig().getServerAddress().equals(sendServer)
					&& er.getReceiveConfig().getServerAddress().equals(receServer)) {
				er.setCheck(true);
				er.getReceiveConfig().setServerPort(pop3Port);
				isUpdate = true;
				break;
			}
		}
		if (!isUpdate) {// 没更新,则直接新增一个
			EmailServerConfig config = new EmailServerConfig();
			config.getReceiveConfig().setServerPort(pop3Port);
			config.setCheck(true);
			list.add(config);
		}
	}

	public String getAccount() {
		List<EmailServerConfig> list = emailSelfTestConfig.getEmailServerList();
		EmailServerConfig select = null;
		for (EmailServerConfig er : list) {
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
		List<EmailServerConfig> list = emailSelfTestConfig.getEmailServerList();
		String sendServer = this.getSmtpServer();
		String receServer = this.getPop3Server();
		boolean isUpdate = false;
		for (EmailServerConfig er : list) {
			er.setCheck(false);
			if (er.getSendConfig().getServerAddress().equals(sendServer)
					&& er.getReceiveConfig().getServerAddress().equals(receServer)) {
				er.setCheck(true);
				er.getAccountConfig().setUserName(account);
				isUpdate = true;
				break;
			}
		}
		if (!isUpdate) {// 没更新,则直接新增一个
			EmailServerConfig config = new EmailServerConfig();
			config.getAccountConfig().setUserName(account);
			config.setCheck(true);
			list.add(config);
		}
	}

	public String getPassword() {
		List<EmailServerConfig> list = emailSelfTestConfig.getEmailServerList();
		EmailServerConfig select = null;
		for (EmailServerConfig er : list) {
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
		List<EmailServerConfig> list = emailSelfTestConfig.getEmailServerList();
		String sendServer = this.getSmtpServer();
		String receServer = this.getPop3Server();
		boolean isUpdate = false;
		for (EmailServerConfig er : list) {
			er.setCheck(false);
			if (er.getSendConfig().getServerAddress().equals(sendServer)
					&& er.getReceiveConfig().getServerAddress().equals(receServer)) {
				er.setCheck(true);
				er.getAccountConfig().setPassword(password);
				isUpdate = true;
				break;
			}
		}
		if (!isUpdate) {// 没更新,则直接新增一个
			EmailServerConfig config = new EmailServerConfig();
			config.getAccountConfig().setPassword(password);
			config.setCheck(true);
			list.add(config);
		}
	}

//	public String getSendServerType() {
//		List<EmailServerConfig> list = emailSelfTestConfig.getEmailServerList();
//		EmailServerConfig select = null;
//		for (EmailServerConfig er : list) {
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
//	public void setSendServerType(String sendServerType) {
//		List<EmailServerConfig> list = emailSelfTestConfig.getEmailServerList();
//		String sendServer = this.getSmtpServer();
//		String receServer = this.getPop3Server();
//		boolean isUpdate = false;
//		for (EmailServerConfig er : list) {
//			er.setCheck(false);
//			if (er.getSendConfig().getServerAddress().equals(sendServer)
//					&& er.getReceiveConfig().getServerAddress().equals(receServer)) {
//				er.setCheck(true);
//				er.getSendConfig().setServerType(sendServerType);
//				isUpdate=true;
//				break;
//			}
//		}
//		if(!isUpdate){//没更新,则直接新增一个
//			EmailServerConfig config=new EmailServerConfig();
//			config.getSendConfig().setServerType(sendServerType);
//			config.setCheck(true);
//			list.add(config);
//		}
//	}

	public String getReceiveServerType() {
		List<EmailServerConfig> list = emailSelfTestConfig.getEmailServerList();
		EmailServerConfig select = null;
		for (EmailServerConfig er : list) {
			if (er.isCheck()) {
				select = er;
				break;
			}
			er.setCheck(false);
		}
		if (null != select) {
			return select.getReceiveConfig().getServerType();
		}
		return ReceiveConfig.TYPE_POP3;
	}

	public void setReceiveServerType(String receiveServerType) {
		List<EmailServerConfig> list = emailSelfTestConfig.getEmailServerList();
		String sendServer = this.getSmtpServer();
		String receServer = this.getPop3Server();
		boolean isUpdate = false;
		for (EmailServerConfig er : list) {
			er.setCheck(false);
			if (er.getSendConfig().getServerAddress().equals(sendServer)
					&& er.getReceiveConfig().getServerAddress().equals(receServer)) {
				er.setCheck(true);
				er.getReceiveConfig().setServerType(receiveServerType);
				isUpdate=true;
				break;
			}
		}
		if(!isUpdate){//没更新,则直接新增一个
			EmailServerConfig config=new EmailServerConfig();
			config.getReceiveConfig().setServerType(receiveServerType);
			config.setCheck(true);
			list.add(config);
		}
	}
	
	public int getUseSSLBySmtp(){
		List<EmailServerConfig> list = emailSelfTestConfig.getEmailServerList();
		for (EmailServerConfig er : list) {
			if(er.isCheck()){
				SendConfig s=er.getSendConfig();
				if(s.getSecurityProtocol().equals(SendConfig.PROTOCOL_SSL))
					return 1;
				else if(s.getSecurityProtocol().equals(SendConfig.PROTOCOL_TLS))
					return 2; 
			}
		}
		return 0;
	}
	
	public void setUseSSLBySmtp(int smtpSSL){
		List<EmailServerConfig> list = emailSelfTestConfig.getEmailServerList();
		for (EmailServerConfig er : list) {
			if(er.isCheck()){
				SendConfig s=er.getSendConfig();
				switch(smtpSSL){
				case 0:
					s.setSecurityProtocol(SendConfig.PROTOCOL_NONE);
					break;
				case 1:
					s.setSecurityProtocol(SendConfig.PROTOCOL_SSL);
					break;
				case 2:
					s.setSecurityProtocol(SendConfig.PROTOCOL_TLS);
					break;
				}
			}
		}
	}
	
	public int getUseSSLByPop3(){
		List<EmailServerConfig> list = emailSelfTestConfig.getEmailServerList();
		for (EmailServerConfig er : list) {
			if(er.isCheck()){
				ReceiveConfig r=er.getReceiveConfig();
				if(r.getSecurityProtocol().equals(ReceiveConfig.PROTOCOL_SSL))
					return 1;  
			}
		}
		return 0; 
	}
	
	public void setUseSSLByPop3(int pop3SSL){
		List<EmailServerConfig> list = emailSelfTestConfig.getEmailServerList();
		for (EmailServerConfig er : list) {
			if(er.isCheck()){
				ReceiveConfig s=er.getReceiveConfig();
				switch(pop3SSL){
				case 0:
					s.setSecurityProtocol(ReceiveConfig.PROTOCOL_NONE);
					break;
				case 1:
					s.setSecurityProtocol(ReceiveConfig.PROTOCOL_SSL);
					break; 
				}
			}
		}
	}

	public int getSmtpAuthentication() {
		return smtpAuthentication;
	}

	public void setSmtpAuthentication(int smtpAuthentication) {
		this.smtpAuthentication = smtpAuthentication;
	}

	public String getSubject() {
		return emailSelfTestConfig.getEmailSubject();
	}

	public void setSubject(String subject) {
		emailSelfTestConfig.setEmailSubject(subject);
	}

	public String getBody() {
		return emailSelfTestConfig.getEmailBody();
	}

	public void setBody(String body) {
		emailSelfTestConfig.setEmailBody(body);
	}

	public String getAdjunct() {
		return emailSelfTestConfig.getLocalFile();
	}

	public void setAdjunct(String adjunct) {
		emailSelfTestConfig.setLocalFile(adjunct);
	}

	public NetworkConnectionSetting getNetworkConnectionSetting() {
		return networkConnectionSetting;
	}

	public EmailSelfTestConfig getEmailSelfTestConfig() {
		return emailSelfTestConfig;
	}

	@Override
	public String toString() {
		return "SendtimeOut=" + emailSelfTestConfig.getSendTimeout() + ",ReceiveTimeOut="
				+ emailSelfTestConfig.getReceiveTimeout() + ",emailServer=" + this.getSmtpServer() + ",port="
				+ this.getSmtpPort() + ",account=" + this.getAccount() + ",password=" + this.getPassword()
				+ ",smtpAuthentication=" + smtpAuthentication + ",subject" + this.getSubject() + ",body="
				+ this.getBody() + ",adjunct=" + this.getAdjunct();

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
				if (tagName.equals("EmailSelfTestConfig")) {
					emailSelfTestConfig.parseXml(parser);
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
		if (null != emailSelfTestConfig)
			emailSelfTestConfig.writeXml(serializer);
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}

	@Override
	public String getServerTaskType() {
		return WalkStruct.TaskType.EmailSmtpAndPOP.getXmlTaskType();
	}
}
