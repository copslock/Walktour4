package com.walktour.gui.task.parsedata.model.task.multiftp.upload;

import com.google.gson.annotations.SerializedName;
import com.walktour.control.config.ConfigFtp;
import com.walktour.gui.task.parsedata.model.base.TaskBase;
import com.walktour.gui.task.parsedata.model.task.base.FTPHostSetting;
import com.walktour.model.FTPGroupModel;
import com.walktour.model.FtpServerModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class MFTPUpConfig extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -4158727280445819036L;
	public static final String FILESOURCE_LOCAL = "Local File";
	public static final String FILESOURCE_CREATE = "Creat File";
	@SerializedName("isCheck")
	private boolean isCheck;
	@SerializedName("localFile")
	private String localFile;
	@SerializedName("fileSize")
	private int fileSize;
	@SerializedName("threadCount")
	private int threadCount;
	@SerializedName("remoteDirectory")
	private String remoteDirectory;
	@SerializedName("fileSource")
	private String fileSource = FILESOURCE_CREATE;
	@SerializedName("ftpHostSetting")
	private FTPHostSetting ftpHostSetting = new FTPHostSetting();

	/***
	 * 默认构造器
	 */
	public MFTPUpConfig() {
		super();
	}

	/***
	 * 从FTPGroupModel 转为 MFTPUpConfig
	 * 
	 * @param model
	 */
	public MFTPUpConfig(FTPGroupModel model) {
		this.isCheck = model.getEnable() == 1 ? true : false;
		this.localFile = model.getLocalFile();
		this.fileSize = model.getFileSize();
		this.remoteDirectory = model.getUploadFilePath();
		this.fileSource = model.getFileSource() == 0 ? FILESOURCE_CREATE : FILESOURCE_LOCAL;
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

	public String getLocalFile() {
		return localFile;
	}

	public void setLocalFile(String localFile) {
		this.localFile = localFile;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
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

	public String getRemoteDirectory() {
		return remoteDirectory;
	}

	public void setRemoteDirectory(String remoteDirectory) {
		this.remoteDirectory = remoteDirectory;
	}

	public String getFileSource() {
		return fileSource;
	}

	public void setFileSource(String fileSource) {
		this.fileSource = fileSource;
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
				if (tagName.equals("LocalFile")) {
					this.setLocalFile(parser.nextText());
				} else if (tagName.equals("RemoteDirectory")) {
					this.setRemoteDirectory(parser.nextText());
				} else if (tagName.equals("FileSource")) {
					this.setFileSource(parser.nextText());
				} else if (tagName.equals("FileSize")) {
					this.setFileSize(stringToInt(parser.nextText()));
				} else if (tagName.equals("ThreadCount")) {
					this.setThreadCount(stringToInt(parser.nextText()));
				} else if (tagName.equals("FTPHostSetting")) {
					ftpHostSetting.parseXml(parser);
				}

				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("MFTPUpConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {

		serializer.startTag(null, "MFTPUpConfig");
		this.writeAttribute(serializer, "IsCheck", this.isCheck);
		this.writeTag(serializer, "LocalFile", this.localFile);
		this.writeTag(serializer, "FileSize", this.fileSize);
		this.writeTag(serializer, "RemoteDirectory", this.remoteDirectory);
		this.writeTag(serializer, "FileSource", this.fileSource);
		this.writeTag(serializer, "ThreadCount", this.threadCount);
		if (null != ftpHostSetting) {
			ftpHostSetting.writeXml(serializer);
		}

		serializer.endTag(null, "MFTPUpConfig");
	}
}
