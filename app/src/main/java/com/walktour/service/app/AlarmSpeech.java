package com.walktour.service.app;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.walktour.base.util.LogUtil;

/**
 * @语音服务
 * 播放指定音频文件,通过接收广播来播放文件,广播的内容 是音频文件的绝对 路径 
 * */
public class AlarmSpeech extends Service{
	private static final String TAG = "Walktour.service.AlarmSpeech";
	public static final String ACTION = "Walktour.service.AlarmSpeech";
	public static final String KEY_FILEPATH = "file_path";
	//广播接收器
	private MyBroadcastReceiver mReceiver;
	//
	
	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.i(TAG, "-------->OnCreate");
		
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		LogUtil.i(TAG, "-------->OnStart");
		regeditMyReceiver();
	}
	
	@Override
	public void onDestroy(){
		this.unregisterReceiver(mReceiver);
		super.onDestroy();
		LogUtil.w(TAG, "-------->onDestroy");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private void regeditMyReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION);
		if(mReceiver==null){
			mReceiver = new MyBroadcastReceiver();
			
		}
		this.registerReceiver(mReceiver, filter);
	}
	
	/**
	 * 广播接收器
	 * */
	private class MyBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if( intent.getAction().equals(AlarmSpeech.ACTION) ){
				String filePath = intent.getExtras().getString( KEY_FILEPATH );
				LogUtil.w(TAG, "paly audio "+filePath);
				
				//播放声音
				//playwave(filePath);
				
			}
		} 
		
	}
	
    // JNI调用登录Fleet服务器的库
	static {
		System.loadLibrary("decoder");
	}
	
	//播放音频文件
	public native int playwave(String filePath);
	
}