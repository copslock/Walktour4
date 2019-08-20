package com.walktour.gui.share;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.walktour.gui.share.download.DownloadManager;
import com.walktour.gui.share.upload.UploadManager;

import org.xutils.x;

import java.util.List;

/**
 * 上传下载service
 * @author zhihui.lian
 */
public class UpDownService extends Service {

	public static DownloadManager getDownloadManager() {
		if (!UpDownService.isServiceRunning(x.app())) {
			Intent downloadSvr = new Intent("UpDownService.action");
			x.app().startService(downloadSvr);
		}
		return DownloadManager.getInstance();
	}

	public static UploadManager getUploadManager() {
		if (!UpDownService.isServiceRunning(x.app())) {
			Intent upLoadSvr = new Intent("UpDownService.action");
			x.app().startService(upLoadSvr);
		}
		return UploadManager.getInstance();
	}
	
	
	

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public static boolean isServiceRunning(Context context) {
		boolean isRunning = false;

		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);

		if (serviceList == null || serviceList.size() == 0) {
			return false;
		}

		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(UpDownService.class.getName())) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

}
