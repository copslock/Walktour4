package com.walktour.gui.share.download;

import android.annotation.SuppressLint;
import android.content.Intent;

import com.dinglicom.data.control.BuildTestRecord;
import com.dinglicom.data.model.RecordAbnormal;
import com.dinglicom.data.model.RecordDetail;
import com.dinglicom.data.model.RecordNetType;
import com.dinglicom.data.model.RecordTaskType;
import com.dinglicom.data.model.RecordTestInfo;
import com.dinglicom.data.model.TestRecord;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.ZipUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.control.instance.FileDB;
import com.walktour.gui.setting.SysBuildingManager;
import com.walktour.gui.share.upload.UploadCallback;
import com.walktour.gui.weifuwu.business.model.ShareFileModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;

import org.xutils.common.Callback;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
/**
 * 下载回调接口类
 * 
 * @author zhihui.lian
 */
@SuppressLint("SdCardPath")
public class DownloadCallback
		implements Callback.CommonCallback<File>, Callback.ProgressCallback<File>, Callback.Cancelable {
	private ShareFileModel shareFileModel;
	private DownloadManager downloadManager;
	private boolean cancelled = false;
	private Cancelable cancelable;
	private long fileUpdateTime = 2000;
	private long startTime = 0;
	private long fileTotalSize = 0;
	private long fileCurrentSize = 0;
	public DownloadCallback(ViewHolder viewHolder) {
		this.switchViewHolder(viewHolder);
	}
	public boolean switchViewHolder(ViewHolder viewHolder) {
		if (viewHolder == null)
			return false;
		synchronized (DownloadCallback.class) {
			this.shareFileModel = viewHolder.getShareFileModel();
			fileTotalSize = this.shareFileModel.getFileTotalSize();
			fileCurrentSize = this.shareFileModel.getFileRealSize();
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
		startTime = System.currentTimeMillis();
		fileTotalSize = shareFileModel.getFileTotalSize();
		fileCurrentSize = shareFileModel.getFileRealSize();
		shareFileModel.setFileStatus(ShareFileModel.FILE_STATUS_ONGOING);
		updateFileStatus();
	}
	@Override
	public void onLoading(long total, long current, boolean isDownloading) {
		synchronized (DownloadCallback.class) {
			if (isDownloading) {
				fileTotalSize = total;
				fileCurrentSize = current;
				shareFileModel.setFileRealSize(current);
				shareFileModel.setFileTotalSize(total);
				if (current == total)// 传输完成
					shareFileModel.setFileStatus(ShareFileModel.FILE_STATUS_FINISH);
				else// 在传输过程中
					shareFileModel.setFileStatus(ShareFileModel.FILE_STATUS_ONGOING);
				if (total == current)
					updateFileStatus();
				else if (System.currentTimeMillis() - startTime > fileUpdateTime) {
					startTime = System.currentTimeMillis();
					updateFileStatus();
				}
			}
		}
	}
	@Override
	public void onSuccess(File result) {
		synchronized (DownloadCallback.class) {
			System.out.println("--fileId--" + shareFileModel.getFileID() + "--success--" + result.getName());
			
			shareFileModel.setFileStatus(ShareFileModel.FILE_STATUS_FINISH);
			updateFileStatus();
			Intent intent;
			// 下载成功后将文件解压到实际目录，依据文件类型做相应处理
			try {
				boolean flag = false;
				String unzipDirectory = "";
				String fileName = shareFileModel.getFilePath() + shareFileModel.getFileName();
				switch (shareFileModel.getFileType()) {
				case ShareFileModel.FILETYPE_PROJECT:// 工程
					unzipDirectory = AppFilePathUtil.getInstance().getSDCardBaseDirectory("project");
					ZipUtil.unsharezip(fileName, unzipDirectory);
					// 下载完成后通知工程管理界面更新
					intent = new Intent(ShareCommons.SHARE_ACTION_REFRESH_PROJECT);
					x.app().sendBroadcast(intent);
					break;
				case ShareFileModel.FILETYPE_GROUP:// 任务组
					unzipDirectory = AppFilePathUtil.getInstance().getSDCardBaseDirectory("group");
					ZipUtil.unsharezip(fileName, unzipDirectory);
					break;
				case ShareFileModel.FILETYPE_TASK:// 任务
					unzipDirectory = AppFilePathUtil.getInstance().getSDCardBaseDirectory("task");
					ZipUtil.unsharezip(fileName, unzipDirectory);
					break;
				case ShareFileModel.FILETYPE_REPORT:// 报表
					unzipDirectory = AppFilePathUtil.getInstance().getSDCardBaseDirectory("report");
					ZipUtil.unsharezip(fileName, unzipDirectory);
					break;
				case ShareFileModel.FILETYPE_CQT:// CQT
					unzipDirectory = AppFilePathUtil.getInstance().getSDCardBaseDirectory("indoortest");
					ZipUtil.unsharezip(fileName, unzipDirectory);
					// 同步楼层关系入库
					SysBuildingManager sd = SysBuildingManager.getInstance(x.app());
					sd.syncDB(x.app());
					break;
				case ShareFileModel.FILETYPE_STATION:// STATION
					unzipDirectory = AppFilePathUtil.getInstance().getSDCardBaseDirectory("basestation");
					ZipUtil.unsharezip(fileName, unzipDirectory);
					break;
				case ShareFileModel.FILETYPE_DATA:// DATA
					// 特殊考虑
					unzipDirectory = AppFilePathUtil.getInstance().getSDCardBaseDirectory("sharepush","data",ShareCommons.device_code+"_"+System.currentTimeMillis());
					File dir=new File(unzipDirectory);
					if(!dir.exists())
						dir.mkdir();
					shareFileModel.setFileStatus(ShareFileModel.FILE_STATUS_UNZIP);
					updateFileStatus();
					ZipUtil.unsharezip(fileName, unzipDirectory);
					File jsonF = new File(unzipDirectory + ShareCommons.DATA_DESCRIBE_JSON);
					// 解析json文件入库
					ArrayList<TestRecord> listR = ShareCommons
							.changeJsonToArray(new String(FileUtil.getBytesFromFile(jsonF), "UTF-8"));
					if (null != listR && listR.size() > 0) {
						for (TestRecord tr : listR) {// json中的数据不全,因此需要重新构造下
							TestRecord record = new BuildTestRecord().getTestRecord();
							record.record_id = tr.record_id;
							record.test_type = tr.test_type;
							record.type_scene = tr.type_scene;
							record.file_name = tr.file_name;
							record.time_create = tr.time_create;
							record.time_end = tr.time_end;
							record.file_split_id = tr.file_split_id;
							record.node_id = tr.node_id;
							record.task_no = tr.task_no;
							record.port_id = tr.port_id;
							record.test_index = tr.test_index;
							record.group_info = tr.group_info;
							record.go_or_nogo = tr.go_or_nogo;
							// 网络类型
							ArrayList<RecordNetType> nettypes = tr.getRecordNetTypes();
							for (RecordNetType nt : nettypes) {
								record.getRecordNetTypes().add(nt);
							}
							// 任务类型
							ArrayList<RecordTaskType> tasktypes = tr.getRecordTaskTypes();
							for (RecordTaskType nt : tasktypes) {
								record.getRecordTaskTypes().add(nt);
							}

							// 任务类型
							ArrayList<RecordDetail> recordDetails = tr.getRecordDetails();
							for (RecordDetail nt : recordDetails) {
								nt.file_path=unzipDirectory;
								record.getRecordDetails().add(nt);
							}
							// 任务类型
							ArrayList<RecordAbnormal> recordAbnormals = tr.getRecordAbnormals();
							for (RecordAbnormal nt : recordAbnormals) {
								record.getRecordAbnormals().add(nt);
							}
							// 任务类型
							ArrayList<RecordTestInfo> recordTestInfo = tr.getRecordTestInfo();
							for (RecordTestInfo nt : recordTestInfo) {
								record.getRecordTestInfo().add(nt);
							}
							FileDB.getInstance(x.app().getApplicationContext()).syncTestRecord(record);
						}
					}
					jsonF.delete();
					shareFileModel.setFileStatus(ShareFileModel.FILE_STATUS_FINISH);
					updateFileStatus();
					// 下载完成后通知数据管理界面更新
					intent = new Intent(ShareCommons.SHARE_ACTION_REFRESH_DATA);
					x.app().sendBroadcast(intent);
					break;
				}
				while (!flag && fileName.endsWith(".zip")) {// 如果是zip文件才需要删除，其他文件不需要
					flag = FileUtil.deleteFile(fileName);
				}
			} catch (Exception e) {
				e.printStackTrace();
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
		synchronized (DownloadCallback.class) {
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
			shareFileModel.setFileTotalSize(fileTotalSize);
			shareFileModel.setFileRealSize(fileCurrentSize);
			ShareDataBase.getInstance(x.app()).updateFile(shareFileModel);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// 异常错误，也要通知界面刷新
		Intent intent = new Intent(UploadCallback.ACTION);
		x.app().sendBroadcast(intent);
	}
}
