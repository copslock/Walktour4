package com.walktour.service.test;

import android.content.Intent;

import com.walktour.Utils.TotalStruct.TotalFtp;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;

import java.util.HashMap;

/**
 * FTP业务测试基础类
 * 
 * @author jianchao.wang
 *
 */
public abstract class FtpBaseTest extends TestTaskService {

	@Override
	public void onCreate() {
		super.onCreate();
		tag = this.getClass().getSimpleName();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.i(tag, "onStart");

		int startFlag = super.onStartCommand(intent, flags, startId);

		if (taskModel == null) {
			stopSelf();
		} else {
			if (this.getDataTestHandler())
				dataTestHandler.startTest();
		}

		return startFlag;
	}

	/**
	 * 获取数据测试句柄
	 * 
	 * @return 是否实例化成功
	 */
	protected abstract boolean getDataTestHandler();

	/**
	 * 获得缓冲区大小
	 * 
	 * @param ftpType
	 *          FTP类型1上传，2下载
	 * @param bufferType
	 *          缓冲区类型 1recv,2send
	 * @return
	 */
	// private int getBufferSize(int ftpType, int bufferType) {
	// int result = 8192;
	// NetType netType =
	// MyPhoneState.getInstance().getCurrentNetType(getApplicationContext());
	// if (ftpType == 1) {
	// if (bufferType == 1) {
	// result = 65535;
	// } else {
	// switch (netType) {
	// case GSM:
	// case TDSCDMA:
	// case LTETDD:
	// result = 16384;
	// break;
	// case LTE:
	// result = 1048576;
	// break;
	// case WCDMA:
	// case CDMA:
	// case EVDO:
	// default:
	// result = 65535;
	// break;
	// }
	// }
	// } else {
	// if (bufferType == 1) {
	// result = 1048576;
	// } else {
	// result = 65535;
	// }
	// }
	//
	// LogUtil.w(tag, "--buffer size:" + result);
	// return result;
	// }

	/**
	 * 统计http
	 * 
	 * @param totalType
	 *          统计项
	 * @param value
	 *          值
	 */
	protected void totalFtpResult(DataTestHandler handler, TotalFtp totalType, long value) {
		HashMap<String, Long> map = new HashMap<String, Long>();
		map.put(totalType.name(), value);
		handler.totalResult(map);
	}

	/**
	 * 业务成功时写入ftp相关的参数统计
	 * 
	 * @param isDownload
	 *          是否下发
	 * @param ftpJobTime
	 *          ftp的业务时间(last - sendGetTime)
	 */
	protected void sendTotalFtpPara(boolean isDownload, int ftpJobTime) {
		Intent ftpTotalFull = new Intent(WalkMessage.TotalByFtpIsFull);
		ftpTotalFull.putExtra("IsFtpDown", isDownload);
		ftpTotalFull.putExtra("FtpJobTimes", ftpJobTime);
		sendBroadcast(ftpTotalFull);
	}

	// private String getLoginFailStr(int failCode) {
	// switch (failCode) {
	// case 1:
	// return "Invalid Username or Password";
	// case 2:
	// return "Username Response Timeout";
	// case 3:
	// return "Password Response Timeout";
	// case 15:
	// return "Other";
	// default:
	// return "Other";
	// }
	// }

}
