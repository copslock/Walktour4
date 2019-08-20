/*
 * 文件名: FTPGroupModel.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2013-6-5
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.walktour.gui.task.parsedata.model.task.base.FTPHostSetting;
import com.walktour.gui.task.parsedata.model.task.multiftp.download.MFTPDownConfig;
import com.walktour.gui.task.parsedata.model.task.multiftp.upload.MFTPUpConfig;

import java.io.Serializable;

public class FTPGroupModel implements Parcelable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5042754177094365282L;

	/**
	 * 是否开启 0 为 false 1为true
	 */
	private int enable;

	/**
	 * 当前选择的FTP服务器名字
	 */
	private String ftpServer;

	/**
	 * 0:create File 1:local File
	 */
	private int fileSource;

	/**
	 * 文件大小
	 */
	private int fileSize;

	/**
	 * 上传路径
	 */
	private String uploadFilePath;

	/**
	 * 本地文件
	 */
	private String localFile;

	/**
	 * 0 不保存 1 保存
	 */
	private int savaFile;

	private String downloadFile;

	public int getEnable() {
		return enable;
	}

	public void setEnable(int enable) {
		this.enable = enable;
	}

	public String getFtpServerName() {
		if (ftpServer == null) {
			return "";
		}
		return ftpServer;
	}

	public void setFtpServers(String ftpServer) {
		this.ftpServer = ftpServer;
	}

	public int getFileSource() {
		return fileSource;
	}

	public void setFileSource(int fileSource) {
		this.fileSource = fileSource;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public String getUploadFilePath() {
		if (uploadFilePath == null) {
			return "";
		}
		return uploadFilePath;
	}

	public void setUploadFilePath(String uploadFilePath) {
		this.uploadFilePath = uploadFilePath;
	}

	public String getLocalFile() {
		if (localFile == null) {
			return "";
		}
		return localFile;
	}

	public void setLocalFile(String localFile) {
		this.localFile = localFile;
	}

	public int getSavaFile() {
		return savaFile;
	}

	public void setSavaFile(int savaFile) {
		this.savaFile = savaFile;
	}

	public String getDownloadFile() {
		return downloadFile;
	}

	public void setDownloadFile(String downloadFile) {
		this.downloadFile = downloadFile;
	}

	/**
	 * 默认构造器
	 */
	public FTPGroupModel() {
		super();
	}

	/***
	 * 从MFTPDownConfig 转 FTPGroupModel
	 * 
	 * @param setting
	 */
	public FTPGroupModel(MFTPDownConfig config) {
		FTPHostSetting setting = config.getFtpHostSetting();
		this.downloadFile = config.getDownloadFile();
		this.enable = config.isCheck() ? 1 : 0;
		this.ftpServer = setting.getSiteName();
		this.savaFile = config.isSaveFile() ? 1 : 0;
	}

	/***
	 * 从MFTPUpConfig 转 FTPGroupModel
	 * 
	 * @param setting
	 */
	public FTPGroupModel(MFTPUpConfig config) {
		FTPHostSetting setting = config.getFtpHostSetting();
		this.enable = config.isCheck() ? 1 : 0;
		this.fileSize = config.getFileSize();
		this.fileSource = config.getFileSource().equals(MFTPUpConfig.FILESOURCE_CREATE) ? 0 : 1;
		this.ftpServer = setting.getSiteName();
		this.localFile = config.getLocalFile();
		this.uploadFilePath = config.getRemoteDirectory();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(enable);
		dest.writeString(ftpServer);
		dest.writeInt(fileSource);
		dest.writeInt(fileSize);
		dest.writeString(uploadFilePath);
		dest.writeString(localFile);
		dest.writeInt(savaFile);
		dest.writeString(downloadFile);
	}

	public static final Parcelable.Creator<FTPGroupModel> CREATOR = new Parcelable.Creator<FTPGroupModel>() {
		public FTPGroupModel createFromParcel(Parcel source) {
			FTPGroupModel ftpModel = new FTPGroupModel();
			ftpModel.setEnable(source.readInt());
			ftpModel.setFtpServers(source.readString());
			ftpModel.setFileSource(source.readInt());
			ftpModel.setFileSize(source.readInt());
			ftpModel.setUploadFilePath(source.readString());
			ftpModel.setLocalFile(source.readString());
			ftpModel.setSavaFile(source.readInt());
			ftpModel.setDownloadFile(source.readString());
			return ftpModel;
		}

		public FTPGroupModel[] newArray(int size) {
			return new FTPGroupModel[size];
		}
	};

}
