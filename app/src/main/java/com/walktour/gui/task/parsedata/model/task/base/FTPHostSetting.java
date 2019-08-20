package com.walktour.gui.task.parsedata.model.task.base;

import com.google.gson.annotations.SerializedName;
import com.walktour.control.config.ConfigFtp;
import com.walktour.gui.task.parsedata.model.base.TaskBase;
import com.walktour.model.FtpServerModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

/***
 * Ftp 服务器信息配置
 * 
 * @author weirong.fan
 *
 */
public class FTPHostSetting extends TaskBase{ 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -566777098481245648L;
	public static final String CONNECTMODE_PASSIVE="Passive";
	public static final String CONNECTMODE_PORT="Port";

	@SerializedName("isCheck")
	private boolean isCheck = false;
	@SerializedName("password")
	private String password = "";
	@SerializedName("isAnonymous")
	private boolean isAnonymous = false;
	@SerializedName("userName")
	private String userName = "";
	/** 映射为数值型1-被动 2-主动**/
	@SerializedName("connectionMode")
	private String connectionMode = CONNECTMODE_PASSIVE;
	@SerializedName("port")
	private int port = 0;
	@SerializedName("siteName")
	private String siteName = "";
	@SerializedName("address")
	private String address = "";
	@SerializedName("ftpServerID")
	private String ftpServerID = "";
	@SerializedName("transferMode")
	private String transferMode = "";

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getIsAnonymous() {
		return isAnonymous;
	}

	public void setIsAnonymous(boolean isAnonymous) {
		this.isAnonymous = isAnonymous;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getConnectionMode() {
		return connectionMode;
	}

	public void setConnectionMode(String connectionMode) {
		this.connectionMode = connectionMode;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getFtpServerID() {
		return ftpServerID;
	}

	public void setFtpServerID(String ftpServerID) {
		this.ftpServerID = ftpServerID;
	}

	public String getTransferMode() {
		return transferMode;
	}

	public void setTransferMode(String transferMode) {
		this.transferMode = transferMode;
	}

	public void parseXml(XmlPullParser parser) throws Exception {
		for (int i = 0; i < parser.getAttributeCount(); i++) {
			String attName = parser.getAttributeName(i);
			String attValue = parser.getAttributeValue(i);
			if (attName.equals("Password")) {
				this.setPassword(attValue);
			} else if (attName.equals("IsCheck")) {
				this.setCheck(stringToBool(attValue));
			} else if (attName.equals("IsAnonymous")) {
				this.setIsAnonymous(stringToBool(attValue));
			} else if (attName.equals("Username")) {
				this.setUserName(attValue);
			} else if (attName.equals("ConnectionMode")) {
				this.setConnectionMode(attValue);
			} else if (attName.equals("Port")) {
				this.setPort(stringToInt(attValue));
			} else if (attName.equals("SiteName")) {
				this.setSiteName(attValue);
			} else if (attName.equals("Address")) {
				this.setAddress(attValue);
			} else if (attName.equals("FTPServerID")) {
				this.setFtpServerID(attValue);
			} else if (attName.equals("TransferMode")) {
				this.setTransferMode(attValue);
			}
		}
		
		//将FTP信息统一写入FTP配置文件中
		ConfigFtp ftp=new ConfigFtp();
		FtpServerModel ftpServer=ftp.getFtpServerModel(this.getSiteName());
		String[] ips=ftp.getAllFtpIps();
		String[] names=ftp.getAllFtpNames();
		boolean isExist=false;
		for(int i=0;i<ips.length;i++){
			if(ips[i].equals(ftpServer.getIp())&&names[i].equals(ftpServer.getName())){
				isExist=true;
				break;
			}
		}
		if(!isExist){//不存在,同意写入ftp配置文件中
			FtpServerModel model=new FtpServerModel();
			model.setAnonymous(this.isAnonymous);
			model.setConnect_mode(this.getConnectionMode().equals(FTPHostSetting.CONNECTMODE_PASSIVE)?1:2);
			model.setIp(this.getAddress());
			model.setLoginPassword(this.getPassword());
			model.setLoginUser(this.getUserName());
			model.setName(this.getSiteName());
			model.setPort(this.getPort()+"");
			ftp.addFtp(model);
		}
	}

	public void writeXml(XmlSerializer serializer)  throws Exception {	
		serializer.startTag(null,"FTPHostSetting");
		this.writeAttribute(serializer, "IsCheck", this.isCheck);
		this.writeAttribute(serializer, "Password", this.password);
		this.writeAttribute(serializer, "IsAnonymous", this.boolToText(this.isAnonymous));
		this.writeAttribute(serializer, "Username", this.userName);
		this.writeAttribute(serializer, "ConnectionMode", this.connectionMode);
		this.writeAttribute(serializer, "Port", this.port);
		this.writeAttribute(serializer, "SiteName", this.siteName);
		this.writeAttribute(serializer, "Address", this.address);
		this.writeAttribute(serializer, "FTPServerID", this.ftpServerID);
		this.writeAttribute(serializer, "TransferMode", this.transferMode);
		serializer.endTag(null,"FTPHostSetting");
	}

}
