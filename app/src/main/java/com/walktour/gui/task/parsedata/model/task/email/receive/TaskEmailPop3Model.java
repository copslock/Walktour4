package com.walktour.gui.task.parsedata.model.task.email.receive;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

public class TaskEmailPop3Model extends TaskModel { 

	/**
	 * 
	 */
	private static final long serialVersionUID = -4616652983898200655L;
	public TaskEmailPop3Model() {
		setTypeProperty(WalkCommonPara.TypeProperty_Net);
		setTaskType(WalkStruct.TaskType.EmailPop3.toString());
	}
	@SerializedName("emailReceiveTestConfig")
	private EmailReceiveTestConfig emailReceiveTestConfig = new EmailReceiveTestConfig();
	@SerializedName("networkConnectionSetting")
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

		testTask.append("EmailServer =" + this.getEmailServer() + "\r\n");
		testTask.append("Port =" + this.getPort() + "\r\n");
		testTask.append("Account =" + this.getAccount() + "\r\n");
		testTask.append("Password =" + this.getPassword() + "\r\n");
		testTask.append("UseSSL =" + this.getUseSSL() + "\r\n");
		testTask.append("TimeOut =" + this.getTimeOut() + "\r\n");

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

	/***
	 * 数据业务复写这两个方法，以适配历史的业务
	 */
	public void setDisConnect(int disConnect) {
		networkConnectionSetting.setDisConnect(disConnect);
	}

	public int getTimeOut() {
		return emailReceiveTestConfig.getReceiveTimeout();
	}

	public void setTimeOut(int timeOut) {
		emailReceiveTestConfig.setReceiveTimeout(timeOut);
	}

	public String getEmailServer() {
		List<EmailServerReceivConfig> list = emailReceiveTestConfig.getEmailServerList();
		EmailServerReceivConfig select = null;
		for (EmailServerReceivConfig er : list) {
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
	public int getEmailServerTye() {
		List<EmailServerReceivConfig> list = emailReceiveTestConfig.getEmailServerList();
		EmailServerReceivConfig select = null;
		for (EmailServerReceivConfig er : list) {
			if (er.isCheck()) {
				select = er;
				break;
			}
		}
		if (null != select) {
			if(select.getReceiveConfig().getServerType().equals(ReceiveConfig.TYPE_POP3))
				return 0;
			else if(select.getReceiveConfig().getServerType().equals(ReceiveConfig.TYPE_IMAP))
				return 2;
		}
		return 0;
		
	}
	public void setEmailServer(String emailServer) {
		List<EmailServerReceivConfig> list = emailReceiveTestConfig.getEmailServerList();
		EmailServerReceivConfig select = null;
		for (EmailServerReceivConfig er : list) {
			if (er.isCheck()) {
				select = er;
				break;
			}
			er.setCheck(false);
		}
		boolean isUpdate=false;
		if (null != select) {
			select.getReceiveConfig().setServerAddress(emailServer);
			isUpdate=true;
		}
		if(!isUpdate){//如果没更新，则直接新增一个
			EmailServerReceivConfig config=new EmailServerReceivConfig();
			config.getReceiveConfig().setServerAddress(emailServer);
			config.setCheck(true);
			list.add(config);
		}
	}

	public int getPort() {
		List<EmailServerReceivConfig> list = emailReceiveTestConfig.getEmailServerList();
		EmailServerReceivConfig select = null;
		for (EmailServerReceivConfig er : list) {
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

	public void setPort(int port) {
		List<EmailServerReceivConfig> list = emailReceiveTestConfig.getEmailServerList();
		String serverAdd = this.getEmailServer();
		boolean isUpdate = false;
		for (EmailServerReceivConfig er : list) {
			er.setCheck(false);
			if (er.getReceiveConfig().getServerAddress().equals(serverAdd)) {
				er.setCheck(true);
				er.getReceiveConfig().setServerPort(port);
				isUpdate=true;
				break;
			}
		}
		if(!isUpdate){//没更新,则直接新增一个
			EmailServerReceivConfig config=new EmailServerReceivConfig();
			config.setServerName(this.getEmailServer());
			config.getReceiveConfig().setServerPort(port);
			config.setCheck(true);
			list.add(config);
		}
	}

	public String getAccount() {
		List<EmailServerReceivConfig> list = emailReceiveTestConfig.getEmailServerList();
		EmailServerReceivConfig select = null;
		for (EmailServerReceivConfig er : list) {
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
		List<EmailServerReceivConfig> list = emailReceiveTestConfig.getEmailServerList();
		String serverAdd = this.getEmailServer();
		boolean isUpdate = false;
		for (EmailServerReceivConfig er : list) {
			er.setCheck(false);
			if (er.getReceiveConfig().getServerAddress().equals(serverAdd)) {
				er.setCheck(true);
				er.getAccountConfig().setUserName(account);
				isUpdate=true;
				break;
			}
		}
		if(!isUpdate){//没更新,则直接新增一个
			EmailServerReceivConfig config=new EmailServerReceivConfig();
			config.setServerName(this.getEmailServer());
			config.getAccountConfig().setUserName(account);
			config.setCheck(true);
			list.add(config);
		}
	}

	public String getPassword() {
		List<EmailServerReceivConfig> list = emailReceiveTestConfig.getEmailServerList();
		EmailServerReceivConfig select = null;
		for (EmailServerReceivConfig er : list) {
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
		List<EmailServerReceivConfig> list = emailReceiveTestConfig.getEmailServerList();
		String serverAdd = this.getEmailServer();
		boolean isUpdate = false;
		for (EmailServerReceivConfig er : list) {
			er.setCheck(false);
			if (er.getReceiveConfig().getServerAddress().equals(serverAdd)) {
				er.setCheck(true);
				er.getAccountConfig().setPassword(password);
				isUpdate=true;
				break;
			}
		}
		if(!isUpdate){//没更新,则直接新增一个
			EmailServerReceivConfig config=new EmailServerReceivConfig();
			config.setServerName(this.getEmailServer());
			config.getAccountConfig().setPassword(password);
			config.setCheck(true);
			list.add(config);
		}
	}

	public String getServerType() {
		List<EmailServerReceivConfig> list = emailReceiveTestConfig.getEmailServerList();
		EmailServerReceivConfig select = null;
		for (EmailServerReceivConfig er : list) {
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

	public void setServerType(String serverType) {
		List<EmailServerReceivConfig> list = emailReceiveTestConfig.getEmailServerList();
		String serverAdd = this.getEmailServer();
		boolean isUpdate = false;
		for (EmailServerReceivConfig er : list) {
			er.setCheck(false);
			if (er.getReceiveConfig().getServerAddress().equals(serverAdd)) {
				er.setCheck(true);
				er.getReceiveConfig().setServerType(serverType);
				isUpdate=true;
				break;
			}
		}
		if(!isUpdate){//没更新,则直接新增一个
			EmailServerReceivConfig config=new EmailServerReceivConfig();
			config.setServerName(this.getEmailServer());
			config.getReceiveConfig().setServerType(serverType);
			config.setCheck(true);
			list.add(config);
		}
	}

	public int getUseSSL() {
		List<EmailServerReceivConfig> list = emailReceiveTestConfig.getEmailServerList();
		EmailServerReceivConfig select = null;
		for (EmailServerReceivConfig er : list) {
			if (er.isCheck()) {
				select = er;
				break;
			}
		}
		if (null != select) {
			if (select.getReceiveConfig().getSecurityProtocol().equals(ReceiveConfig.PROTOCOL_SSL)) {
				return 1;
			}
		}
		return 0;
	}

	public void setUseSSL(int useSSL) {
		List<EmailServerReceivConfig> list = emailReceiveTestConfig.getEmailServerList();
		String serverAdd = this.getEmailServer();
		boolean isUpdate = false;
		for (EmailServerReceivConfig er : list) {
			er.setCheck(false);
			if (er.getReceiveConfig().getServerAddress().equals(serverAdd)) {
				er.setCheck(true);
				if (useSSL == 1)
					er.getReceiveConfig().setSecurityProtocol(ReceiveConfig.PROTOCOL_SSL);
				else
					er.getReceiveConfig().setSecurityProtocol(ReceiveConfig.PROTOCOL_NONE);
				isUpdate=true;
				break;
			}
		}
		if(!isUpdate){//没更新,则直接新增一个
			EmailServerReceivConfig config=new EmailServerReceivConfig();
			config.setServerName(this.getEmailServer());
			if (useSSL == 1)
				config.getReceiveConfig().setSecurityProtocol(ReceiveConfig.PROTOCOL_SSL);
			else
				config.getReceiveConfig().setSecurityProtocol(ReceiveConfig.PROTOCOL_NONE);
			config.setCheck(true);
			list.add(config);
		}
	}

	public int getPsCall() {
		if (emailReceiveTestConfig.getPsCallMode().equals(EmailReceiveTestConfig.PSCALLMODEL_BYFILE))
			return 0;
		return 1;
	}

	public void setPsCall(int psCall) {
		if (psCall == 0) {
			emailReceiveTestConfig.setPsCallMode(EmailReceiveTestConfig.PSCALLMODEL_BYFILE);
		} else {
			emailReceiveTestConfig.setPsCallMode(EmailReceiveTestConfig.PSCALLMODEL_BYTIME);
		}
	}

	public EmailReceiveTestConfig getEmailReceiveTestConfig() {
		return emailReceiveTestConfig;
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
				if (tagName.equals("EmailReceiveTestConfig")) {
					emailReceiveTestConfig.parseXml(parser);
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
		if (null != emailReceiveTestConfig)
			emailReceiveTestConfig.writeXml(serializer);
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}

	@Override
	public String getServerTaskType() {
		return WalkStruct.TaskType.EmailPop3.getXmlTaskType();
	}
}
