package com.walktour.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.walktour.Utils.BuildPower;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.service.app.Killer;

public class CheckLicenseService extends Service {
	private static final String TAG = "CheckLicenseService";
	
	private TelephonyManager telManager;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		LogUtil.w(TAG,"--onCreate--");
		listenPhoneState();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(telManager != null){
			telManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
		LogUtil.w(TAG,"--onDestroy--");
		
	}

	/**
	 * 开始监听，PhoneState实例化后必须运行此方法才能获取手机信号状态变化
	 *            监听手机信号的Activity或者Service
	 * */
	public void listenPhoneState() {
		telManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		telManager.listen(mPhoneStateListener,
				PhoneStateListener.LISTEN_SERVICE_STATE 
				| PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
	}
	
	/** 手机状态监听器 */
	private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
		@Override
		public void onDataConnectionStateChanged(int state) {
			super.onDataConnectionStateChanged(state);
			LogUtil.w(TAG, "--DataState:" + state);
			if(state == TelephonyManager.DATA_CONNECTED){
				new CheckLicenseFileExist().start();
			}
		}
	};
	
	boolean checkFileRun = false;
	class CheckLicenseFileExist extends Thread{
		public void run(){
			LogUtil.w(TAG, "--CheckLicenseFileExist run--" + checkFileRun);
			if(!checkFileRun){
				checkFileRun = true;
				try {
					Thread.sleep(1000 * 3);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				int result = BuildPower.checkFtpFileExist(getApplicationContext());
				
				if(result == WalkCommonPara.POWER_FTP_FILE_NOT_EXISTS){
					mHandler.obtainMessage(CHECK_FILE_NOT_EXISTS).sendToTarget();
				}else if(result == WalkCommonPara.POWER_FTP_FILE_EXISTS){
					mHandler.obtainMessage(CHECK_FILE_EXISTS).sendToTarget();
				}
				
				checkFileRun = false;
			}
		}
	}
	
	private final int CHECK_FILE_EXISTS 	= 1;
	private final int CHECK_FILE_NOT_EXISTS	= 2;
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){ 
    	public void handleMessage(Message msg){
			LogUtil.w(TAG,"--handle:" + msg.what);
			
			switch(msg.what){
			case CHECK_FILE_EXISTS:
				
				stopSelf();
				break;
			case CHECK_FILE_NOT_EXISTS:
				ConfigRoutine.getInstance().setGoOnType(getApplicationContext(), false);
				startService(new Intent(getApplicationContext(),Killer.class));
				break;
			}
		}
	};
}
