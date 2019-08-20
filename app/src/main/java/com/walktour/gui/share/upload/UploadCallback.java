package com.walktour.gui.share.upload;

import android.content.Intent;

import com.walktour.Utils.FileUtil;
import com.walktour.gui.share.download.DownloadCallback;
import com.walktour.gui.share.download.DownloadManager;
import com.walktour.gui.share.download.ViewHolder;
import com.walktour.gui.weifuwu.business.model.ShareFileModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;

import org.xutils.common.Callback;
import org.xutils.x;
/**
 * 上传回调接口类
 * 
 * @author zhihui.lian
 */
public class UploadCallback
		implements Callback.CommonCallback<String>, Callback.ProgressCallback<String>, Callback.Cancelable {
	public static final String ACTION = "com.walktour.gui.share.upload.UploadCallback";
	private ShareFileModel shareFileModel;
	private DownloadManager downloadManager;
	private boolean cancelled = false;
	private Cancelable cancelable;
	public UploadCallback(ViewHolder viewHolder) {
		this.switchViewHolder(viewHolder);
	}
	public boolean switchViewHolder(ViewHolder viewHolder) {
		if (viewHolder == null)
			return false;
		synchronized (UploadCallback.class) {
			this.shareFileModel = viewHolder.getShareFileModel();
		}
		return true;
	}
	public void setDownloadManager(DownloadManager downloadManager) {
		this.downloadManager = downloadManager;
	}
	public void setCancelable(Cancelable cancelable) {
		this.cancelable = cancelable;
	}
	@Override
	public void onWaiting() {
		// 文件等待下载
		shareFileModel.setFileStatus(ShareFileModel.FILE_STATUS_WAITING);
		updateFileStatus();
	}
	@Override
	public void onStarted() {
		// 文件开始下载
		shareFileModel.setFileStatus(ShareFileModel.FILE_STATUS_ONGOING);
		updateFileStatus();
	}
	@Override
	public void onLoading(long total, long current, boolean isDownloading) {
		synchronized (UploadCallback.class) {
			shareFileModel.setFileRealSize(current);
			shareFileModel.setFileTotalSize(total);
			if (current == total)// 传输完成
				shareFileModel.setFileStatus(ShareFileModel.FILE_STATUS_FINISH);
			else// 在传输过程中
				shareFileModel.setFileStatus(ShareFileModel.FILE_STATUS_ONGOING);
			updateFileStatus();
		}
	}
	@Override
	public void onSuccess(String result) {
		synchronized (UploadCallback.class) {
			System.out.println("--fileId--" + shareFileModel.getFileID() + "--success--" + result);
			shareFileModel.setFileStatus(ShareFileModel.FILE_STATUS_FINISH);
			updateFileStatus();
			// 文件上传成功,删除本地文件
			boolean flag = false;
			while (!flag) {
				flag = FileUtil.deleteFile(shareFileModel.getFilePath() + shareFileModel.getFileName());
			}
		}
	}
	@Override
	public void onError(Throwable ex, boolean isOnCallback) {
		synchronized (DownloadCallback.class) {
			// 出现错误异常
			shareFileModel.setFileStatus(ShareFileModel.FILE_STATUS_ERROR);
			updateFileStatus();
		}
	}
	@Override
	public void onCancelled(CancelledException cex) {
		synchronized (UploadCallback.class) {
			// 取消和错误使用同一个状态
			shareFileModel.setFileStatus(ShareFileModel.FILE_STATUS_ERROR);
			updateFileStatus();
		}
	}
	@Override
	public void onFinished() {
		cancelled = false;
	}
	@Override
	public void cancel() {
		cancelled = true;
		if (cancelable != null) {
			cancelable.cancel();
		}
	}
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	/***
	 * 修改文件状态,发送更新广播
	 */
	private void updateFileStatus() {
		try {
			// 更新文件状态
			ShareDataBase.getInstance(x.app()).updateFile(shareFileModel);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// 异常错误，也要通知界面刷新
		Intent intent = new Intent(UploadCallback.ACTION);
		x.app().sendBroadcast(intent);
	}
}
