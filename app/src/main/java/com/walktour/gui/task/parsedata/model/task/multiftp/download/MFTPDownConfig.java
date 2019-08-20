package com.walktour.gui.task.parsedata.model.task.multiftp.download;

import com.google.gson.annotations.SerializedName;
import com.walktour.control.config.ConfigFtp;
import com.walktour.gui.task.parsedata.model.base.TaskBase;
import com.walktour.gui.task.parsedata.model.task.base.FTPHostSetting;
import com.walktour.model.FTPGroupModel;
import com.walktour.model.FtpServerModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class MFTPDownConfig extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 6196405703767876413L;
	@SerializedName("isCheck")
	private boolean isCheck;
	@SerializedName("downloadFile")
	private String downloadFile;
	@SerializedName("isSaveFile")
	private boolean isSaveFile;
	@SerializedName("threadCount")
	private int threadCount;
	@SerializedName("ftpHostSetting")
	private FTPHostSetting ftpHostSetting=new FTPHostSetting();

	/***
	 * 默认构造器
	 */
	public MFTPDownConfig() {
		super();
	}

	/***
	 * 从FTPGroupModel 转为 MFTPUpConfig
	 * 
	 * @param model
	 */
	public MFTPDownConfig(FTPGroupModel model) {
		this.isCheck = model.getEnable() == 1 ? true : false;
		this.downloadFile = model.getDownloadFile();
		this.isSaveFile =model.getSavaFile()==1?true:false;
		// 获取server的名字
		String serverName = model.getFtpServerName();
		// 获取配置了的所有的ftp
		ConfigFtp ftp = new ConfigFtp();
		String[] ftpNams = ftp.getAllFtpNames();
		if (null == ftpNams || ftpNams.length <= 0)
			return;
		for (String str : ftpNams) {
			if (str.equals(serverName)) {
				ftpHostSetting.setCheck(true);
				ftpHostSetting.setSiteName(str);
				ftpHostSetting.setAddress(ftp.getFtpIp(serverName));
				ftpHostSetting.setPort(Integer.parseInt(ftp.getFtpPort(serverName)));
				ftpHostSetting.setUserName(ftp.getFtpUser(serverName));
				ftpHostSetting.setPassword(ftp.getFtpPass(serverName));
				ftpHostSetting.setIsAnonymous(ftp.getAnonymous(serverName));
				ftpHostSetting.setConnectionMode(ftp.getConnectMode(serverName) == FtpServerModel.CONNECT_MODE_PASSIVE
						? FTPHostSetting.CONNECTMODE_PASSIVE : FTPHostSetting.CONNECTMODE_PORT);
			}
		}
	}
	
	public String getDownloadFile() {
		return downloadFile;
	}

	public void setDownloadFile(String downloadFile) {
		this.downloadFile = downloadFile;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public FTPHostSetting getFtpHostSetting() {
		return ftpHostSetting;
	}

 

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public boolean isSaveFile() {
		return isSaveFile;
	}

	public void setSaveFile(boolean isSaveFile) {
		this.isSaveFile = isSaveFile;
	}

	public void parseXml(XmlPullParser parser) throws Exception {
		int eventType = parser.getEventType();
		String tagName = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				tagName = parser.getName();
				break;
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if (tagName.equals("DownloadFile")) {
					this.setDownloadFile(parser.nextText());
				} else if (tagName.equals("ThreadCount")) {
					this.setThreadCount(stringToInt(parser.nextText()));
				} else if (tagName.equals("IsSaveFile")) {
					this.setSaveFile(stringToBool(parser.nextText()));
				} else if (tagName.equals("FTPHostSetting")) { 
					ftpHostSetting.parseXml(parser);
				}

				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("MFTPDownConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {

		serializer.startTag(null, "MFTPDownConfig");
		this.writeAttribute(serializer, "IsCheck", this.isCheck);
		this.writeTag(serializer, "DownloadFile", this.downloadFile);
		this.writeTag(serializer, "IsSaveFile", this.isSaveFile);
		this.writeTag(serializer, "ThreadCount", this.threadCount);
		if (null != ftpHostSetting) {
			ftpHostSetting.writeXml(serializer);
		}

		serializer.endTag(null, "MFTPDownConfig");
	}
}
