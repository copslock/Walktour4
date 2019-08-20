package com.walktour.gui.weifuwu.business.model;

import com.walktour.base.util.AppFilePathUtil;
import com.walktour.gui.R;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;

public class ShareFileModel {
	/** 文件类型：1-工程文件 */
	public static final int FILETYPE_PROJECT = 1;
	/** 文件类型：2-任务组文件 */
	public static final int FILETYPE_GROUP = 2;
	/** 文件类型：3-任务文件 */
	public static final int FILETYPE_TASK = 3;
	/** 文件类型：4-CQT文件 */
	public static final int FILETYPE_CQT = 4;
	/** 文件类型：5-室内地图文件 */
	public static final int FILETYPE_CQI_PIC = 5;
	/** 文件类型：6-基站信息文件 */
	public static final int FILETYPE_STATION = 6;
	/** 文件类型：7-截屏图片文件 */
	public static final int FILETYPE_PIC_SCREENSHOT = 7;
	/** 文件类型：8-数据文件 */
	public static final int FILETYPE_DATA = 8;
	/** 文件类型：9-报表文件 */
	public static final int FILETYPE_REPORT = 9;

	/** 数据共享的模式:1-发送 **/
	public static final int SEND_OR_RECEIVE_SEND = 1;
	/** 数据共享的模式:2-接收 **/
	public static final int SEND_OR_RECEIVE_RECEIVE = 2;

	/** 处理文件的状态：0-初始状态，表示新入库，接收时表示未点击 **/
	public static final int FILE_STATUS_INIT = 0;
	/** 处理文件的状态：1-开始处理，发送时直接填1 **/
	public static final int FILE_STATUS_START = 1;
	/** 处理文件的状态：2-上传，等待 **/
	public static final int FILE_STATUS_WAITING = 2;
	/** 处理文件的状态：2-上传，进行中 **/
	public static final int FILE_STATUS_ONGOING = 3;
	/** 处理文件的状态：2-上传，下载已经开始 **/
	public static final int FILE_STATUS_ERROR = 4;
	/** 处理文件的状态：3-完成状态 **/
	public static final int FILE_STATUS_FINISH = 5;

	/** 处理文件的状态：99-正在压缩中 **/
	public static final int FILE_STATUS_ZIP = 99;
	/** 处理文件的状态：99-正在解压中 **/
	public static final int FILE_STATUS_UNZIP = 100;
	
	/** 主键唯一 **/
	private int id;
	/** 文件类型 **/
	private int fileType = -1;
	/** 文件的路径 **/
	private String filePath;
	/** 文件名，包含文件的绝对路径和文件名 **/
	private String fileName;
	/** 远程服务器的文件ID **/
	private String fileID;
	/** 文件的总大小 **/
	private long fileTotalSize;
	/** 文件描述 **/
	private String fileDescribe;
	/** 文件的共享模式：发送还是接收，默认是发送 **/
	private int sendOrReceive = SEND_OR_RECEIVE_SEND;
	/** 发送时只有发送的终端 ID，接收时不仅有发送终端ID,还有发送的群组ID,表示从哪个组发过来的 **/
	private String fromDeviceCode;
	private String fromGroupCode;
	/** 目标device code,多个以分号隔开 **/
	private String targetDeviceCodes;
	/** 目标group code,多个以分号隔开 **/
	private String targetGroupCodes;
	/** 文件状态 **/
	private int fileStatus = FILE_STATUS_INIT;
	/** 文件上传或接收的实际大小 **/
	private long fileRealSize = 0;
	/** 创建时间 */
	private String createTime = "";

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFileType() {
		return fileType;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileID() {
		return fileID;
	}

	public void setFileID(String fileID) {
		this.fileID = fileID;
	}

	public long getFileTotalSize() {
		return fileTotalSize;
	}

	public void setFileTotalSize(long fileTotalSize) {
		this.fileTotalSize = fileTotalSize;
	}

	public String getFileDescribe() {
		return fileDescribe;
	}

	public void setFileDescribe(String fileDescribe) {
		this.fileDescribe = fileDescribe;
	}

	public int getSendOrReceive() {
		return sendOrReceive;
	}

	public void setSendOrReceive(int sendOrReceive) {
		this.sendOrReceive = sendOrReceive;
	}

	public String getFromDeviceCode() {
		return fromDeviceCode;
	}

	public void setFromDeviceCode(String fromDeviceCode) {
		this.fromDeviceCode = fromDeviceCode;
	}

	public String getFromGroupCode() {
		return fromGroupCode;
	}

	public void setFromGroupCode(String fromGroupCode) {
		this.fromGroupCode = fromGroupCode;
	}

	public String getTargetDeviceCodes() {
		return targetDeviceCodes;
	}

	public void setTargetDeviceCodes(String targetDeviceCodes) {
		this.targetDeviceCodes = targetDeviceCodes;
	}

	public String getTargetGroupCodes() {
		return targetGroupCodes;
	}

	public void setTargetGroupCodes(String targetGroupCodes) {
		this.targetGroupCodes = targetGroupCodes;
	}

	public int getFileStatus() {
		return fileStatus;
	}

	public void setFileStatus(int fileStatus) {
		this.fileStatus = fileStatus;
	}

	public long getFileRealSize() {
		return fileRealSize;
	}

	public void setFileRealSize(long fileRealSize) {
		this.fileRealSize = fileRealSize;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
		if (null == filePath || filePath.equalsIgnoreCase("")) {
			this.filePath = this.getFileSDPath();
		}
	}

	/**
	 * 获取实际的文件类型图标
	 * 
	 * @return
	 */
	public int getFileTypeDrawable() {
		switch (this.fileType) {
		case FILETYPE_PROJECT:
			return R.drawable.share_type_project;
		case FILETYPE_GROUP:
			return R.drawable.share_type_task;
		case FILETYPE_TASK:
			return R.drawable.share_type_task;
		case FILETYPE_CQT:
			return R.drawable.share_type_cqt;
		case FILETYPE_CQI_PIC:
			return R.drawable.share_type_map;
		case FILETYPE_STATION:
			return R.drawable.share_type_station;
		case FILETYPE_PIC_SCREENSHOT:
			return R.drawable.share_type_pic;
		case FILETYPE_DATA:
			return R.drawable.share_type_data;
		case FILETYPE_REPORT:
			return R.drawable.share_type_report;
		}
		return R.drawable.share_type_project;
	}

	public String getFileSDPath() {
		String filePath = AppFilePathUtil.getInstance().getSDCardBaseDirectory();
		switch (this.fileType) {
			case FILETYPE_GROUP:
				filePath += ShareCommons.SHARE_PATH_TASKGROUP;
				break;
			case FILETYPE_TASK:
				filePath += ShareCommons.SHARE_PATH_TASK;
				break;
			case FILETYPE_CQT:
				filePath += ShareCommons.SHARE_PATH_CQT;
				break;
			case FILETYPE_CQI_PIC:
				filePath += ShareCommons.SHARE_PATH_CQT_PIC;
				break;
			case FILETYPE_STATION:
				filePath += ShareCommons.SHARE_PATH_STATION;
				break;
			case FILETYPE_PIC_SCREENSHOT:
				filePath += ShareCommons.SHARE_PATH_SCREENSHOT_PIC;
				break;
			case FILETYPE_DATA:
				filePath += ShareCommons.SHARE_PATH_DATA;
				break;
			case FILETYPE_REPORT:
				filePath += ShareCommons.SHARE_PATH_REPORT;
				break;
			default:
				filePath += ShareCommons.SHARE_PATH_PROJECT;
				break;
		}
		return filePath;
	}
}
