package com.walktour.service.app.datatrans;

import android.content.Intent;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.Utils.WalkStruct.ServerOperateType;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ServerManager;
import com.walktour.framework.ui.dialog.BasicDialogActivity;
import com.walktour.gui.R;
import com.walktour.gui.listener.ServerStatus;
import com.walktour.service.app.DataTransService;
import com.walktour.service.app.datatrans.model.DataTransferModel;
import com.walktour.service.app.datatrans.model.UploadFileModel;
import com.walktour.service.app.datatrans.model.UploadFileModel.UploadState;
import com.walktour.service.app.datatrans.smtp.SendMailReport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 终端和服务器间数据交互抽象类
 *
 * @author jianchao.wang
 *
 */
public abstract class BaseDataTransfer extends Thread {
	/** 交互状态：初始化 */
	private static final int STATE_INIT = 0;
	/** 交互状态：等待执行中 */
	private static final int STATE_WAIT = 1;
	/** 交互状态：上传中 */
	private static final int STATE_UPLOAD_DOING = 2;
	/** 交互状态：上传中断中 */
	private static final int STATE_UPLOAD_INTERRUPT = 3;
	/** 交互状态：运行结束 */
	public static final int STATE_FINISH = 99;
	/** 没有任何操作的最大等待时间 */
	public static final int NOTHING_MAX_TIME = 30 * 1000;
	/** 日志标识 */
	protected static String TAG;
	/** 是否停止文件交互 */
	protected boolean isStopTransfer = false;
	/** 当前正在上传文件的等待上传的文件类型 */
	private List<FileType> mWaitFileTypes = new ArrayList<>();
	/** 当前等待上传的文件列表 */
	private List<UploadFileModel> mWaitFiles = new ArrayList<>();
	/** 当前等待终止上传的文件列表 */
	private List<UploadFileModel> mWaitStopFiles = new ArrayList<>();
	/** 当前等待执行的操作对象，除上传操作以外 */
	private List<DataTransferModel> mWaitOperateModels = new ArrayList<>();
	/** 记录成功上传的文件 */
	private List<UploadFileModel> mSuccessFiles = new ArrayList<>();
	/** 当前正在上传的文件 */
	protected UploadFileModel mCurrentFile;
	/** 当前正在上传的文件类型 */
	protected FileType mCurrentFileType;
	/** 服务器管理类 */
	protected ServerManager mServerMgr;
	/** 连接失败尝试次数 */
	private static final int LOGIN_FAIL_MAX = 3;
	/** 上传服务类 */
	protected DataTransService mService;
	/** 日志文件存放路径 */
	protected String mLogPath = "";
	/** 服务端描述 */
	protected String mServerDescribe = "";
	/** 当前交互状态 */
	private int mTransferState = STATE_INIT;
	/** 上传的服务器类型 */
	private final int mUploadServer;
	/** 最好一次操作的时间 */
	private long mLastOperateTime = 0;

	/**
	 * 创建一个新的实例 DataTransfer. 目前一个操作动作（上传文件或上报事件或同步时间）就对应一个DataTransfer实例。
	 *
	 * @param uploadServer
	 *          上传的服务器类型
	 * @param tag
	 *          日志标识
	 * @param service
	 *          服务类
	 * @param logName
	 *          日志文件名称
	 */
	public BaseDataTransfer(int uploadServer, String tag, DataTransService service, String logName) {
		this.mUploadServer = uploadServer;
		TAG = tag;
		this.mService = service;
		this.mServerMgr = ServerManager.getInstance(this.mService);
		this.mLogPath = AppFilePathUtil.getInstance().getSDCardBaseFile("liblog",logName).getAbsolutePath();
	}

	/**
	 * 新增操作任务
	 *
	 * @param model
	 *          操作对象
	 */
	public void addTask(DataTransferModel model) {
		switch (model.getOperateType()) {
			case uploadIndoorFile:
			case uploadTestFile:
			case uploadAutoTestFile:
				if (model.getUploadFiles() != null && model.getUploadFiles().size() > 0) {
					this.setUploadFiles(model.getUploadFiles());
					this.mWaitFiles.addAll(model.getUploadFiles());
				}
				break;
			default:
				this.mWaitOperateModels.add(model);
				break;
		}
	}

	/**
	 * 设置要上传的文件列表
	 *
	 * @param uploadFiles
	 *          文件列表
	 */
	private void setUploadFiles(List<UploadFileModel> uploadFiles) {
		if (uploadFiles == null)
			return;
		Map<String, Boolean> map = mServerMgr.getUploadFileTypes(this.mService);
		Set<FileType> fileTypes = new HashSet<FileType>();
		for (String fileType : map.keySet()) {
			if (map.get(fileType) && FileType.getFileTypeByName(fileType) != null) {
				fileTypes.add(FileType.getFileTypeByName(fileType));
			}
		}
		for (int i = 0; i < uploadFiles.size(); i++) {
			if (!uploadFiles.get(i).hasFileTypes() && !fileTypes.isEmpty())
				uploadFiles.get(i).setFileTypes(fileTypes);
		}
	}

	@Override
	public void run() {
		if (this.mTransferState != STATE_INIT)
			return;
		LogUtil.d(TAG, "-----run-----");
		if (!init()) {
			mServerMgr.OnServerStatusChange(ServerStatus.loginFail, "请检查服务器配置.");
			this.mService.handlerMessage(R.string.alert_connect_fail);
			this.finishTransfer();
			return;
		}
		this.isStopTransfer = false;
		this.mTransferState = STATE_WAIT;
		while (!isStopTransfer) {
			if (!this.mWaitOperateModels.isEmpty()) {
				if (this.mTransferState == STATE_UPLOAD_DOING) {
					this.mTransferState = STATE_UPLOAD_INTERRUPT;
					this.interruptUploading();
					this.mWaitFiles.add(0, this.mCurrentFile);
					this.mCurrentFile = null;
					this.mCurrentFileType = null;
					this.mWaitFileTypes.clear();
				} else if (this.mTransferState == STATE_WAIT) {
					this.mLastOperateTime = 0;
					DataTransferModel model = this.mWaitOperateModels.remove(0);
					switch (model.getOperateType()) {
						case downManual:// 下载测试计划
							downloadTestTask(true);
							this.mTransferState = STATE_WAIT;
							break;
						case downManualForce:// 覆盖下载测试计划
							downloadTestTask(true);
							this.mTransferState = STATE_WAIT;
							break;
						case sendEvent:
							reportEvent();
							this.mTransferState = STATE_WAIT;
							break;
						case syncTime:
							// 关闭系统设置里的自动同步
							UtilsMethod.setTimeAuto(mService, false);
							// 同步时间
							syncTime();
							this.mTransferState = STATE_WAIT;
							break;
						case uploadParamsReport:
							if (!StringUtil.isNullOrEmpty(model.getMessage()))
								if (!this.uploadParamsReport(model.getMessage())) {
									if (this.uninit())
										this.init();
								}
							break;
						default:
							break;
					}
				}
			} else if (!this.mWaitFiles.isEmpty() && this.mTransferState == STATE_WAIT) {
				this.mLastOperateTime = 0;
				this.uploadNextFile();
			} else if (this.mTransferState == STATE_WAIT) {
				if (this.mLastOperateTime == 0)
					this.mLastOperateTime = System.currentTimeMillis();
				if (System.currentTimeMillis() - this.mLastOperateTime > NOTHING_MAX_TIME) {
					LogUtil.d(TAG, "----connect is time out " + NOTHING_MAX_TIME + " seconds----");
					this.finishTransfer();
				}
			}
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 上传实时参数到平台
	 *
	 * @param msg
	 *          参数json字符串
	 */
	protected abstract boolean uploadParamsReport(String msg);

	/**
	 * 停止指定操作
	 *
	 * @param model
	 *          操作类型
	 */
	public void stopOperate(DataTransferModel model) {
		if (model.getUploadFiles() != null && model.getUploadFiles().size() > 0) {
			this.mWaitStopFiles.addAll(model.getUploadFiles());
			if (this.mCurrentFile != null && model.getUploadFiles().contains(this.mCurrentFile)) {
				this.interruptUploading();
			}
		} else if (model.getOperateType() != null) {
			for (int i = 0; i < this.mWaitOperateModels.size(); i++) {
				if (this.mWaitOperateModels.get(i).getOperateType() == model.getOperateType()) {
					this.mWaitOperateModels.remove(i);
					i--;
				}
			}
		}
	}

	/**
	 * 中断当前的上传操作
	 */
	protected abstract void interruptUploading();

	/**
	 * 上报事件
	 */
	private void reportEvent() {
		// 上报事件提示
		Intent intentDialog = new Intent(this.mService, BasicDialogActivity.class);
		intentDialog.putExtra("title", this.mService.getString(R.string.str_tip));
		intentDialog.putExtra("message", this.mService.getString(R.string.server_upload_event));
		intentDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.mService.startActivity(intentDialog);
		// 上报事件
		sendEvent();
		// 上报事件完成提示
		intentDialog.putExtra("title", this.mService.getString(R.string.str_tip));
		intentDialog.putExtra("message", this.mService.getString(R.string.server_upload_event_finish));
		intentDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.mService.startActivity(intentDialog);
	}

	/**
	 * 上传文件结束
	 *
	 */
	private void onUploadFinish() {
		LogUtil.d(TAG, "-------onUploadFinish-------");
		// 发邮件通知
		if (!this.mSuccessFiles.isEmpty()) {
			SendMailReport mailReport = new SendMailReport();
			String[] fileNames = new String[this.mSuccessFiles.size()];
			for (int i = 0; i < fileNames.length; i++) {
				fileNames[i] = this.mSuccessFiles.get(i).getName();
			}
			mailReport.sendReportMail(fileNames, this.mService);
			this.mSuccessFiles.clear();
		}
		this.mService.handlerTransferEnd();
	}

	/**
	 * 等待设置的上传数据网络可用,可用之后重新登录
	 */
	private boolean waitForUploadNetwork() {
		LogUtil.d(TAG, "----------waitForUploadNetwork-------------");
		boolean initSuccess = false;
		int tryCount = 0;
		while (!initSuccess && !isStopTransfer && tryCount < LOGIN_FAIL_MAX) {
			uninit();
			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			LogUtil.d(TAG, "relogin");
			tryCount++;
			initSuccess = init();
			LogUtil.d(TAG, "relogin " + Boolean.toString(initSuccess));
		}
		return initSuccess;
	}

	/**
	 * 等待正在压缩的DTLog文件
	 *
	 */
	private void waitForZipDTLog() {
		while (mCurrentFile.isConverting() || mCurrentFile.isZiping()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			File file = mCurrentFile.getFile(FileType.DTLOG);
			if (file != null) {
				LogUtil.d(TAG, "zipping dtlog file:" + file);
				mServerMgr.sendTipBroadcast(mService.getString(R.string.str_compressing) + file);
			}
		}
	}

	/**
	 * 初始化
	 *
	 * @return
	 */
	protected abstract boolean init();

	/**
	 * 同步上传当前的文件
	 *
	 * @return
	 */
	private void uploadCurrentFile() {
		LogUtil.d(TAG, "------uploadCurrentFile------");
		mService.handleTransferFileStart(mCurrentFile);
		this.initCurrentFileTypes();
		this.mWaitFileTypes.clear();
		FileType[] fileTypes = this.mCurrentFile.getFileTypes();
		for (FileType fileType : fileTypes) {
			this.mWaitFileTypes.add(fileType);
		}
		if (this.isStopCurrentFile())
			return;
		this.uploadNextFileType();
	}

	/**
	 * 是否要停止当前正在上传的文件
	 *
	 * @return
	 */
	private boolean isStopCurrentFile() {
		if (this.mWaitStopFiles.contains(this.mCurrentFile)) {
			LogUtil.d(TAG, "-----isStopCurrentFile-----");
			this.mWaitStopFiles.remove(this.mCurrentFile);
			this.finishCurrentFile();
			return true;
		}
		return false;
	}

	/**
	 * 上传当前文件下一个文件类型
	 */
	private void uploadNextFileType() {
		if (isStopTransfer || this.mWaitFileTypes.isEmpty() || !this.queryCurrentFileCanOperateFileType()) {
			this.finishCurrentFile();
			return;
		}
		if (this.isStopCurrentFile())
			return;
		LogUtil.d(TAG, "-------------uploadNextFileType:" + this.mCurrentFileType.getFileTypeName() + "---------------");
		File file = this.mCurrentFile.getFile(this.mCurrentFileType);
		if (file == null) {
			if(this.mCurrentFileType == WalkStruct.FileType.FloorPlan){
				this.setFileTypeUploadState(UploadState.SUCCESS);
			}else{
				this.setFileTypeUploadState(UploadState.FILE_NO_FOUND);
			}
		} else {
			this.setFileTypeUploadState(UploadState.DOING);
		}
	}

	/**
	 * 查找当前可以操作的文件类型
	 *
	 * @return
	 */
	private boolean queryCurrentFileCanOperateFileType() {
		while (!this.mWaitFileTypes.isEmpty()) {
			this.mCurrentFileType = this.mWaitFileTypes.remove(0);
			UploadState uploadState = this.getUploadState();
			if (uploadState == UploadState.WAIT) {
				return true;
			} else {
				this.mCurrentFileType = null;
			}
		}
		return false;
	}

	/**
	 * 上传当前文件的指定文件类型的文件
	 *
	 * @return
	 */
	protected abstract void uploadCurrentFileType();

	/**
	 * 设置当前正在上传的文件类型的上传状态
	 *
	 * @param uploadState
	 *          上传状态
	 */
	protected void setFileTypeUploadState(UploadState uploadState) {
		if (this.mCurrentFile == null || this.mCurrentFileType == null) {
			return;
		}
		LogUtil.d(TAG, "------setFileTypeUploadState------state:" + uploadState.getName() + "---------");
		this.mCurrentFile.setUploadState(mCurrentFileType, uploadState);
		switch (uploadState) {
			case DOING:
				LogUtil.d(TAG, "----start----file:" + this.mCurrentFile.getName(this.mCurrentFileType));
				this.mService.handleTransferFileStart(mCurrentFile);
				this.uploadCurrentFileType();
				break;
			case SUCCESS:
			case FAILURE:
			case FILE_NO_FOUND:
				this.mCurrentFile.setUploadProgress(mCurrentFileType, 100);
				String remoteInfo = this.mCurrentFile.getName(this.mCurrentFileType)
						+ this.mService.getString(R.string.server_file_notfound);
				mServerMgr.sendTipBroadcast(remoteInfo);
			case INTERRUPT:
				this.mService.updateDBUploadState(mCurrentFile, mCurrentFileType, mServerDescribe);
				this.finishCurrentFileType();
				break;
			default:
				break;
		}
	}

	/**
	 * 设置当前正在上传的文件类型的上传进度
	 *
	 * @param progress
	 */
	protected void setUploadProgress(int progress) {
		if (this.mCurrentFile != null && this.mCurrentFileType != null) {
			this.mCurrentFile.setUploadProgress(mCurrentFileType, progress);
			if (progress == 100) {
				this.mCurrentFile.setUploadState(mCurrentFileType, UploadState.SUCCESS);
			}
			this.mService.handleTransferFileProgress(this.mCurrentFile);
		}
	}

	/**
	 * 获取当前上传的文件类型的上传进度
	 *
	 * @return
	 */
	protected int getUploadProgress() {
		if (this.mCurrentFile != null && this.mCurrentFileType != null) {
			return this.mCurrentFile.getProgress(mCurrentFileType);
		}
		return 0;
	}

	/**
	 * 获取当前上传的文件类型的上传状态
	 *
	 * @return
	 */
	protected UploadState getUploadState() {
		if (this.mCurrentFile != null && this.mCurrentFileType != null) {
			return this.mCurrentFile.getUploadState(mCurrentFileType);
		}
		return null;
	}

	/**
	 * 上传下一个文件
	 *
	 */
	private void uploadNextFile() {
		if (isStopTransfer || this.mWaitFiles.isEmpty())
			return;
		LogUtil.d(TAG, "-------------uploadNextFile----------");
		this.mTransferState = STATE_UPLOAD_DOING;
		mCurrentFile = this.mWaitFiles.remove(0);
		LogUtil.d(TAG, "------upload file:" + mCurrentFile.getParentPath() + mCurrentFile.getName());
		if (mCurrentFile.isZiping()) {
			waitForZipDTLog();
		}
		uploadCurrentFile();
	}

	/**
	 * 结束当前上传的文件
	 */
	private void finishCurrentFile() {
		if (this.mCurrentFile == null)
			return;
		LogUtil.d(TAG, "-------------finishCurrentFile----------");
		mService.handleTransferFileEnd(mCurrentFile);
		if (this.mCurrentFile.isUploadSuccess())
			mSuccessFiles.add(mCurrentFile);
		if (this.mCurrentFile.isUploadFailure()) {
			if (this.waitForUploadNetwork()) {
				this.mCurrentFile = null;
				this.mCurrentFileType = null;
				this.mTransferState = STATE_WAIT;
			} else
				this.finishTransfer();
		} else {
			if (this.mWaitFiles.isEmpty()) {
				onUploadFinish();
			}
			this.mCurrentFile = null;
			this.mCurrentFileType = null;
			this.mTransferState = STATE_WAIT;
		}
	}

	/**
	 * 结束当前上传的文件类型
	 */
	private void finishCurrentFileType() {
		LogUtil.d(TAG, "----finishCurrentFileType----isLastSuccess:" + this.mCurrentFile.isLastSuccess());
		if (this.mTransferState == STATE_UPLOAD_INTERRUPT) {
			this.mTransferState = STATE_WAIT;
			return;
		}
		if(this.mCurrentFile.isLastSuccess() && this.mCurrentFile.getUploadState(this.mCurrentFileType) != UploadState.SUCCESS){
			this.mTransferState = STATE_WAIT;
			this.finishCurrentFile();
			return;
		}
		this.uploadNextFileType();
	}

	/**
	 * 同步时间
	 *
	 * @return
	 */
	protected abstract boolean syncTime();

	/**
	 * 下载测试计划
	 *
	 * @param forceReplace
	 *          是否覆盖当前计划
	 * @return
	 */
	public abstract boolean downloadTestTask(boolean forceReplace);

	/**
	 * 发送事件到服务器
	 *
	 * @return
	 */
	protected abstract boolean sendEvent();

	/**
	 * 反初始化
	 *
	 * @return
	 */
	protected abstract boolean uninit();

	/**
	 * 初始化当前文件的上传文件类型
	 */
	protected abstract void initCurrentFileTypes();

	/**
	 * 获取最后一次断开网络时间
	 *
	 * @return
	 */
	protected long getLastDisconnectTime() {
		return this.mService.getLastDisconnectTime();
	}

	/**
	 * 结束下载测试任务
	 */
	protected void finishDownloadTestTask() {
		LogUtil.d(TAG, "----------finishDownloadTestTask----------");
		this.mWaitOperateModels.add(new DataTransferModel(ServerOperateType.syncTime));
		this.mTransferState = STATE_WAIT;
	}

	/**
	 * 完成当前的交互操作
	 */
	public synchronized void finishTransfer() {
		LogUtil.d(TAG, "---------finishTransfer-----------");
		try {
			this.isStopTransfer = true;
			if (this.mTransferState == STATE_UPLOAD_DOING)
				this.interruptUploading();
			this.mTransferState = STATE_FINISH;
			this.uninit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public int getTransferState() {
		return mTransferState;
	}

	public void setTransferState(int transferState) {
		mTransferState = transferState;
	}

	/**
	 * 当前交互是否已完成
	 *
	 * @return
	 */
	public boolean isFinish() {
		return this.mTransferState == STATE_FINISH;
	}

	public int getUploadServer() {
		return mUploadServer;
	}

	/**
	 * 函数功能：例如 0500000F20080210193040ms1.log，表示ID号为0500000F的测试盒、
	 * 测试文件的起始时间为2008年2月10日19点30分40秒、第一个测试模块采集的原始文件，后缀为log
	 *
	 * @param boxId
	 * @param moudleNum
	 *          模块号,ms1,ms2...
	 * @param dtlogFile
	 *          DTLog文件
	 * @return
	 */
	public String getRemoteFileName(String boxId, int moudleNum, File dtlogFile) {

		String fileInfo = getFileInfoFromDTLog(dtlogFile);

		try {

			String netType = fileInfo.split("\t")[4];
			String time = fileInfo.split("\t")[5];
			if (time.length() > 14) {
				time = time.substring(0, 14);
			}

			String fileType = "lte";
			if (netType.equalsIgnoreCase("GSM")) {
				fileType = "log";
			} else if (netType.equalsIgnoreCase("TD")) {
				fileType = "lot";
			} else if (netType.equalsIgnoreCase("LTE")) {
				fileType = "lte";
			} else if (netType.equalsIgnoreCase("WLAN")) {
				fileType = "lol";
			} else
				fileType = "log";
			String remoteFileName = String.format(Locale.getDefault(), "%s%sms%d.%s", boxId, time, moudleNum, fileType);
			return remoteFileName;
		} catch (Exception e) {
			return "FILE_ERROR";
		}

	}

	/**
	 * 获取文件信息从DTlog
	 *
	 * @param dtlogFile
	 *          dtlog文件
	 * @return
	 */
	public String getFileInfoFromDTLog(File dtlogFile) {
		String fileInfo = "";
		try {
			FileInputStream inStream = new FileInputStream(dtlogFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
			if (br != null) {
				fileInfo = br.readLine();
			}

			br.close();
			inStream.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return fileInfo;
	}
}
