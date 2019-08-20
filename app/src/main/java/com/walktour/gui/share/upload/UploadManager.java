package com.walktour.gui.share.upload;

import android.text.TextUtils;

import com.walktour.gui.share.download.ViewHolder;
import com.walktour.gui.share.logic.ShareHttpRequestUtil;
import com.walktour.gui.weifuwu.business.model.ShareFileModel;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
/**
 * 上传管理器
 * 
 * @author zhihui.lian
 */
public final class UploadManager {
	private static UploadManager instance;
	private final static int MAX_DOWNLOAD_THREAD = 1;
	private final Executor executor = new PriorityExecutor(MAX_DOWNLOAD_THREAD, true);
	private ConcurrentHashMap<Integer, Callback.Cancelable> hashMap = new ConcurrentHashMap<Integer, Callback.Cancelable>();
	public static UploadManager getInstance() {
		if (instance == null) {
			synchronized (UploadManager.class) {
				if (instance == null) {
					instance = new UploadManager();
				}
			}
		}
		return instance;
	}
	private ViewHolder viewHolder;
	private long uploadOffsetSize = 0;
	public synchronized void startUpload(final ShareFileModel shareFileModel) {
		RequestParams paramsSize = new RequestParams(ShareHttpRequestUtil.url + "/file/query_size.do");
		paramsSize.addBodyParameter("device_code", ShareCommons.device_code);
		paramsSize.addBodyParameter("file_id", shareFileModel.getFileID());
		x.http().get(paramsSize, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				try {
					JSONObject jsonObject2 = new JSONObject(result);
					String sizeStr = jsonObject2.getString("file_size");
					if (!TextUtils.isEmpty(sizeStr)) {
						uploadOffsetSize = Long.valueOf(sizeStr);
					}
				} catch (Throwable e) {
					e.printStackTrace();
					uploadOffsetSize = 0;
				}
				System.out.println("----query_size----" + uploadOffsetSize);
				upload(shareFileModel);
			}
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				System.out.println("----onError----" + ex.getMessage());
			}
			@Override
			public void onCancelled(CancelledException cex) {
			}
			@Override
			public void onFinished() {
			}
		});
	}
	/**
	 * 上传文件
	 * 
	 * @param shareFileModel
	 */
	private void upload(ShareFileModel shareFileModel) {
		ShareFileModel upLoadInfo = shareFileModel;
		if (viewHolder == null) {
			viewHolder = new ViewHolder(shareFileModel);
		} else {
			viewHolder.update(shareFileModel);
		}
		UploadCallback callback = new UploadCallback(viewHolder);
		callback.switchViewHolder(viewHolder);
		RequestParams params = new RequestParams(ShareHttpRequestUtil.url + "/file/upload.do?device_code="
				+ upLoadInfo.getFromDeviceCode() + "&file_id=" + upLoadInfo.getFileID());
		params.setCharset("UTF-8");
		params.setUploadOffsetSize(uploadOffsetSize);
		params.setExecutor(executor);
		params.setMultipart(true);
		params.addBodyParameter("file", new File(shareFileModel.getFilePath() + shareFileModel.getFileName()), null);
		params.setCancelFast(true);
		params.setHeader("Cookie", "JSESSIONID=" + ShareCommons.session_id);
		Callback.Cancelable cancelable = x.http().post(params, callback);
		callback.setCancelable(cancelable);
		hashMap.put(shareFileModel.getId(), cancelable);
	}
	/**
	 * 停止上传
	 * 
	 * @param shareFileModel
	 *            依据主键唯一ID
	 */
	public void stopUpload(ShareFileModel shareFileModel) {
		Callback.Cancelable cancelable = hashMap.get(shareFileModel.getId());
		if (cancelable != null) {
			cancelable.cancel();
		}
	}
}
