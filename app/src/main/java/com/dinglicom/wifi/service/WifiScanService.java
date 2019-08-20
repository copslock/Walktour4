package com.dinglicom.wifi.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;

import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.WifiTools;
import com.walktour.base.util.LogUtil;

import java.util.List;

/**
 * Wifi定期扫描Service
 * 
 * @author weirong.fan
 *
 */
public class WifiScanService extends Service {

	/** 日志标识 */
	private static final String TAG = "WifiScanService";
	/** 上下文 **/
	private Context mContext;
	// 休眠10秒
	private int sleepTime = 2 * 1000;
	private Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			try {
//				LogUtil.d(TAG, "-----------Wifi Scan---------------");
				initWifiData(mContext);
				getData();
				if (null != handler)
					handler.postDelayed(this, sleepTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void onCreate() {
		LogUtil.d(TAG, "-----------onCreate---------------");
		super.onCreate();
		mContext = this.getApplicationContext();
		if (null != handler)
			handler.postDelayed(runnable, sleepTime);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 上报GPS和实时参数给平台
	 */
	private void getData() {
		WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		if (null != wifiManager&& WifiManager.WIFI_STATE_ENABLED == wifiManager.getWifiState()) {
			List<ScanResult> list = wifiManager.getScanResults();
			if (null != list && list.size() > 0) {
				for (ScanResult sr : list) {
					EventBytes eventBytes =EventBytes.Builder(mContext, RcuEventCommand.WLAN_WIFI_AP_SCANNED_INFO);
					eventBytes.addInteger(list.size());
					eventBytes.addStringBuffer(sr.SSID + "");
					eventBytes.addStringBuffer(sr.BSSID + "");
					eventBytes.addCharArray((WifiTools.getChannelByFrequency(sr.frequency) + "\0").toCharArray(), 24);
					eventBytes.addInteger(sr.level);
					eventBytes.addInteger(0);
					eventBytes.addInteger(0);
					eventBytes.addInteger(0);
					eventBytes.writeToRcu(System.currentTimeMillis() * 1000);
					eventBytes=null;
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		LogUtil.d(TAG, "-----onDestroy-----");
		super.onDestroy();
		if (null != handler)
			if (null != runnable)
				handler.removeCallbacks(runnable);
	}
	
	/**
	 * 初始化Wifi数据
	 * @param context
	 */
	private void initWifiData(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (null != wifiManager && WifiManager.WIFI_STATE_ENABLED == wifiManager.getWifiState()) {
			WifiInfo info = wifiManager.getConnectionInfo();
			if (null != info) {
				TraceInfoInterface.decodeResultUpdate(UnifyParaID.WIFI_SSID, info.getSSID() + "");
				TraceInfoInterface.decodeResultUpdate(UnifyParaID.WIFI_IP_Address,
						WifiTools.getIPAddress(info.getIpAddress()) + "");
				TraceInfoInterface.decodeResultUpdate(UnifyParaID.WIFI_MAC_Address, info.getMacAddress() + "");
				List<ScanResult> listSR=wifiManager.getScanResults();
				TraceInfoInterface.decodeResultUpdate(UnifyParaID.WIFI_Channel, "");
				for(ScanResult sr:listSR){
					if(sr.BSSID.equals(info.getBSSID())){
						TraceInfoInterface.decodeResultUpdate(UnifyParaID.WIFI_Channel, WifiTools.getChannelByFrequency(sr.frequency));		
					}
				}
				
				TraceInfoInterface.decodeResultUpdate(UnifyParaID.WIFI_Signal_Strength, info.getRssi() + "");
				TraceInfoInterface.decodeResultUpdate(UnifyParaID.WIFI_SNR, "");
				TraceInfoInterface.decodeResultUpdate(UnifyParaID.WIFI_SFI, "");
				TraceInfoInterface.decodeResultUpdate(UnifyParaID.WIFI_AFI, "");
				TraceInfoInterface.decodeResultUpdate(UnifyParaID.WIFI_FrameThroughput, "");
				TraceInfoInterface.decodeResultUpdate(UnifyParaID.WIFI_FrameRetransRate, "");
			}
		} else {
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.WIFI_SSID, "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.WIFI_IP_Address, "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.WIFI_MAC_Address, "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.WIFI_Channel, "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.WIFI_Signal_Strength, "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.WIFI_SNR, "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.WIFI_SFI, "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.WIFI_AFI, "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.WIFI_FrameThroughput, "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.WIFI_FrameRetransRate, "");
		}
	}

}
