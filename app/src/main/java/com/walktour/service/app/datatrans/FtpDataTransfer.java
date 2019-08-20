package com.walktour.service.app.datatrans;

import com.walktour.Utils.FtpOperate;
import com.walktour.Utils.FtpOperate.OnProgressChangeListener;
import com.walktour.Utils.FtpTranserStatus.UploadStatus;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigFtp;
import com.walktour.control.config.ServerManager;
import com.walktour.gui.R;
import com.walktour.model.FtpServerModel;
import com.walktour.service.app.DataTransService;
import com.walktour.service.app.datatrans.model.UploadFileModel.UploadState;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * FTP服务器上传数据
 * 
 * @author jianchao.wang
 *
 */
public class FtpDataTransfer extends BaseDataTransfer implements OnProgressChangeListener {
	/** FTP上传工具类 */
	private FtpOperate mFtpUtils = null;
	/** FTP服务类 */
	private FtpServerModel mFtpServer;

	public FtpDataTransfer(DataTransService service) {
		super(ServerManager.SERVER_FTP, "FtpDataTransfer", service, "ftp_upload.log");
	}

	@Override
	public boolean init() {
		ConfigFtp configFtp = new ConfigFtp();
		mFtpServer = configFtp.getFtpServerModel(mServerMgr.getFtpName());
		super.mServerDescribe = this.mFtpServer.getIp() + "_" + this.mFtpServer.getPort();
		mFtpUtils = new FtpOperate(mService, this);
		return this.loginServer();
	}

	@Override
	protected void uploadCurrentFileType() {
		// 远程目录
		String remoteFile = mServerMgr.getFtpPath() + MyPhoneState.getInstance().getIMEI(mService);
		File file = super.mCurrentFile.getFile(super.mCurrentFileType);
		if (!super.mCurrentFile.getBooleanExtraParam("IndoorTest"))
			remoteFile += "/task/" + file.getName();
		else
			remoteFile += "/indoor/" + super.mCurrentFile.getStringExtraParam("BuildName") + "/"
					+ super.mCurrentFile.getStringExtraParam("FloorName") + "/" + file.getName();

		UploadStatus uploadStatus = UploadStatus.Upload_New_File_Failed;
		try {
			uploadStatus = mFtpUtils.uploadFile(file.getAbsolutePath(), remoteFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// 下面3个值表示上传成功
		switch (uploadStatus) {
		case Upload_New_File_Success:
		case Upload_From_Break_Success:
		case File_Exits:
			super.setFileTypeUploadState(UploadState.SUCCESS);
			break;
		case Upload_Interrupted:
			super.setFileTypeUploadState(UploadState.INTERRUPT);
			break;
		default:
			super.setFileTypeUploadState(UploadState.FAILURE);
			break;
		}
	}

	@Override
	public boolean uninit() {
		if (this.mFtpUtils != null)
			this.mFtpUtils.interrupt();
		try {
			this.mFtpUtils.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	protected boolean syncTime() {
		return false;
	}

	@Override
	protected boolean sendEvent() {
		return false;
	}

	@Override
	public void onProgressChange(long currentSize, long totalSize) {
		int progress = (int) (currentSize * 100 / totalSize);
		super.setUploadProgress(progress);
	}

	@Override
	public boolean downloadTestTask(boolean forceReplace) {
		return false;
	}

	/**
	 * 登陆服务器
	 * 
	 * @return
	 */
	private boolean loginServer() {
		boolean connectSuccess = false;
		try {
			LogUtil.d(TAG, "--ftp ip = " + mFtpServer.getIp() + " ftp port = " + mFtpServer.getPort() + " ftp user="
					+ mFtpServer.getLoginUser() + " ftp password = " + mFtpServer.getLoginPassword());
			mServerMgr.sendTipBroadcast(mService.getString(R.string.server_connect_stat));

			connectSuccess = mFtpUtils.connect(mFtpServer.getIp(), Integer.parseInt(mFtpServer.getPort()),
					mFtpServer.getLoginUser(), mFtpServer.getLoginPassword());
		} catch (Exception e) {
			LogUtil.e(TAG, "----->Login fail", e.fillInStackTrace());
		}

		LogUtil.d(TAG, "ftp connect success?" + connectSuccess);
		mServerMgr.sendTipBroadcast(connectSuccess ? mService.getString(R.string.server_connect_success)
				: mService.getString(R.string.server_connect_fail));

		return connectSuccess;
	}

	@Override
	protected void initCurrentFileTypes() {
		if (this.mCurrentFile.getFileTypes().length == 0) {
			Set<FileType> fileTypes = new HashSet<FileType>();
			fileTypes.add(FileType.ORGRCU);
			this.mCurrentFile.setFileTypes(fileTypes);
		}
	}

	@Override
	protected void interruptUploading() {
		this.mFtpUtils.interrupt();
	}

	@Override
	protected boolean uploadParamsReport(String msg) {
		// 无需实现
		return true;
	}

}
