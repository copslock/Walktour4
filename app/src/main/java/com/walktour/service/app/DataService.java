package com.walktour.service.app;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.dinglicom.data.control.SyncFileToTable;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.control.Alarm;
import com.walktour.gui.setting.SysBuildingManager;

/**
 *监听系统数据变化的服务
 * */
public class DataService extends Service{
	private final String tag = "DataService";
	
	private Context mContext;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		mContext = this ;
		LogUtil.i(tag, "---onCreate");
		//注册事件监听
		regeditBroadcast();
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		LogUtil.i(tag, "---onStart" + (intent == null));
		if(intent == null){
			startService(new Intent(getApplicationContext(),Killer.class));
		}else{
			refreshFileList();
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		unregisterReceiver( mBroadcastReceiver );//反注册事件监听
		LogUtil.w(tag, "-------->onDestroy");
	}
	
	
	/**
	 * 检查数据库记录和实际存储是否一致，如果数据不一致，则重新生成模型列表FileList
	 * 业务数据是TaskFileList
	 * 监控数据是MonitorFileList
	 * */
	private void refreshFileList(){
		ApplicationModel appModel = ApplicationModel.getInstance();
		
		//如果当前不是测试状态
		if( !appModel.isTestJobIsRun() && !appModel.isTestStoping() ){
			//如果有测试权限
			if(	appModel.getAppList().contains(WalkStruct.AppType.OperationTest) || 
					appModel.getAppList().contains(WalkStruct.AppType.AutomatismTest)||
					appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)||
					appModel.getAppList().contains(WalkStruct.AppType.ScannerTest)
					){
				LogUtil.w(tag, "----sync start--");
				//检查业务数据一致性
				new Thread( new ThreadData() ).start();
			}
		}
	}
	
	/**
	 * 检查数据，和数据库同步
	 * 
	 * @version 3.3.1
	 * @author Tangwq
	 */
	private class ThreadData implements Runnable{
		@Override
		public void run(){
			SyncFileToTable syncFile = new SyncFileToTable(mContext);
			syncFile.syncFilesToTable();
    		//add by msi同步旧的楼层结构到数据库
    		SysBuildingManager.getInstance(mContext).syncDB(mContext);
		}
	}

	
	/**
	 * 注册广播接收器
	 */
	protected void regeditBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		filter.addAction(Intent.ACTION_MEDIA_SHARED);
		filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		filter.addAction(Intent.ACTION_MEDIA_EJECT);
		/*添加了DataSchem*/
		filter.addDataScheme("file");							/*必须有file标识才能接收*/	
		this.registerReceiver(mBroadcastReceiver, filter);
	}
	
	/**
	 * 广播接收器:接收来广播更新界面
	 * */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			
			/**
			 * SDcard状态改变时重新生成数据并刷新页面,
			 * 为了处理这几种情况：
			 * 1.用户挂载SDcard到PC机后直接删除Sdcard里的文件,
			 * 2.直接复制文件到sdcard
			 * 3.修改文件名
			 */
			if( intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED )
				|| intent.getAction().equals(Intent.ACTION_MEDIA_SHARED )
				|| intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED )
				|| intent.getAction().equals(Intent.ACTION_MEDIA_BAD_REMOVAL)
				|| intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)){
				
				LogUtil.i(tag, "----sdcard state change--");
				//检查数据一致性 ,I/O操作务必在子线程中进行
				refreshFileList();
				new Thread (  new StorgeChecker() ).start();//I/O操作务必在子线程中进行
				//发送广播通知界面
				sendBroadcast( new Intent(WalkMessage.ACTION_SDCARD_STATUS) );
			}
			
		}//end onReceive
		
	};
	
	/**
	 * 用handler处理，避免崩溃
	 */
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 10:
				Alarm alarm = (Alarm)msg.obj;
				alarm.checkSDcardLow();
				alarm.checkSDcardNon();
				alarm.checkStorge();
				break;
			}
		};
	};
	
	private class StorgeChecker implements Runnable{

		@Override
		public void run() {
			/*
			 * 当Sdcard挂载到手机的时候，检查Sdcard的空间大小
			 * 否则检查手机的空间大小
			 */
			Alarm alarm = new Alarm( DataService.this );
			Message msg = handler.obtainMessage(10, alarm);
			handler.sendMessage(msg);
		}
	}
}
