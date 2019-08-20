package com.walktour.gui.share.download;

import com.walktour.gui.share.logic.ShareHttpRequestUtil;
import com.walktour.gui.weifuwu.business.model.ShareFileModel;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;

import org.xutils.common.Callback;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * 下载管理器
 * 
 * @author zhihui.lian
 */
public final class DownloadManager {

	private static DownloadManager instance;

	private final static int MAX_DOWNLOAD_THREAD = 3;

	private final Executor executor = new PriorityExecutor(MAX_DOWNLOAD_THREAD, true);
	
	private ConcurrentHashMap<Integer, Callback.Cancelable> hashMap = new ConcurrentHashMap<Integer, Callback.Cancelable>();

	public static DownloadManager getInstance() {
		if (instance == null) {
			synchronized (DownloadManager.class) {
				if (instance == null) {
					instance = new DownloadManager();
				}
			}
		}
		return instance;
	}

	private ViewHolder viewHolder;

	
	
	/**
	 * 下载文件
	 * @param shareFileModel
	 */
	public synchronized void startDownload(ShareFileModel shareFileModel) {

		String fileSavePath = shareFileModel.getFilePath()+shareFileModel.getFileName();
		//特殊处理室内地图的path
//		if(shareFileModel.getFileType()==ShareFileModel.FILETYPE_PIC){
//			fileSavePath="/sdcard/Walktour/sharepush/"+shareFileModel.getFileName();
//		}
		if (viewHolder == null) {
			viewHolder = new ViewHolder(shareFileModel);
		} else {
			viewHolder.update(shareFileModel);
		}
		DownloadCallback callback = new DownloadCallback(viewHolder);
		callback.setDownloadManager(this);
		callback.switchViewHolder(viewHolder);
		RequestParams params = new RequestParams(ShareHttpRequestUtil.getInstance().getDownUrl(shareFileModel.getFileID()));
		params.setAutoResume(true);
		params.setAutoRename(false);
		params.setSaveFilePath(fileSavePath);
		params.setExecutor(executor);
		params.setCancelFast(true);
		params.setHeader("Cookie", "JSESSIONID=" + ShareCommons.session_id);
//		params.addHeader("Cookie", "JSESSIONID=" + ShareCommons.session_id);
		Callback.Cancelable cancelable = x.http().get(params, callback);
		callback.setCancelable(cancelable);
		hashMap.put(shareFileModel.getId(), cancelable);
	}
	

	
	/**
	 * 停止下载
	 * @param shareFileModel 依据主键唯一ID
 	 */
	public void stopDownload(ShareFileModel shareFileModel){
		Callback.Cancelable cancelable = hashMap.get(shareFileModel.getId());
        if (cancelable != null) {
            cancelable.cancel();
        }
	}

	
	
	
}
