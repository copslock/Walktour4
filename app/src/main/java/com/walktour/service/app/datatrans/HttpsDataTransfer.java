package com.walktour.service.app.datatrans;

import com.dingli.service.test.fd_init_params;
import com.dingli.service.test.filetransjni;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.control.config.ServerManager;
import com.walktour.service.app.DataTransService;
import com.walktour.service.app.datatrans.model.UploadFileModel.UploadState;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Http服务器上传数据
 * 
 * @author jianchao.wang
 *
 */
public class HttpsDataTransfer extends BaseDataTransfer {
	/** 文件传输JNI */
	private filetransjni mTransJNI;
	/** 进度监控线程 */
	private ProgressThread mThread;

	public HttpsDataTransfer(DataTransService service) {
		super(ServerManager.SERVER_HTTPS, "HttpsDataTransfer", service, "https_upload.log");
		mTransJNI = new filetransjni();
	}

	@Override
	public boolean init() {
		super.mServerDescribe = mServerMgr.getHttpsUrl();
		fd_init_params params = new fd_init_params();
		params.protocol_type = 2;
		params.user_name = mServerMgr.getHttpsUsername();
		params.password = mServerMgr.getHttpsPass();
		params.Market = mServerMgr.getHttpsMarket();
		params.TestScope = mServerMgr.getHttpsScope();
		params.EventName = mServerMgr.getHttpsEvent();
		params.Description = mServerMgr.getHttpsDescription();
		params.DriveSource = mServerMgr.getHttpsDrive();
		return mTransJNI.init_server(params);
	}

	@Override
	public boolean uninit() {
		mTransJNI.fd_stopflag = 1;
		mTransJNI.trans_cancel();
		mTransJNI.uninit_server();
		return true;
	}

	/**
	 * 进度监听线程
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class ProgressThread extends Thread {

		/** 是否停止当前线程 */
		private boolean isStop = false;

		@Override
		public void run() {
			while (!isStop) {
				int progress = mTransJNI.upload_progress();
				mCurrentFile.setUploadProgress(FileType.ORGRCU, progress);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * 停止线程
		 */
		public void stopThread() {
			this.isStop = true;
			this.interrupt();
		}
	}

	@Override
	public boolean downloadTestTask(boolean force) {
		return false;
	}

	@Override
	public boolean sendEvent() {
		return false;
	}

	@Override
	public boolean syncTime() {
		return false;
	}

	@Override
	protected void uploadCurrentFileType() {
		this.mThread = new ProgressThread();
		this.mThread.start();
		File file = super.mCurrentFile.getFile(super.mCurrentFileType);
		boolean result = mTransJNI.upload_file(file.getAbsolutePath(), mServerMgr.getHttpsUrl(), mTransJNI.fd_stopflag,
				mTransJNI.fd_error);
		this.mThread.stopThread();
		this.mThread = null;
		if (result) {
			super.setFileTypeUploadState(UploadState.SUCCESS);
		} else {
			super.setFileTypeUploadState(UploadState.FAILURE);
		}
	}

	@Override
	protected void initCurrentFileTypes() {
		// 目前Https平台仅上传RCU
		if (super.mCurrentFile.getFileTypes().length == 0) {
			Set<FileType> fileTypes = new HashSet<FileType>();
			fileTypes.add(FileType.ORGRCU);
			this.mCurrentFile.setFileTypes(fileTypes);
		}
	}

	@Override
	protected void interruptUploading() {
		this.mThread.stopThread();
		super.setFileTypeUploadState(UploadState.INTERRUPT);
		this.mThread = null;
		this.mTransJNI.trans_cancel();
	}

	@Override
	protected boolean uploadParamsReport(String msg) {
		// 无需实现
		return true;
	}

}
