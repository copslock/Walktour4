package com.walktour.service.app;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ServerManager;
import com.walktour.gui.R;
import com.walktour.gui.VersionUpgradeActivity;

import java.io.File;


/**
 * 版本更新服务
 * 更新前必须验证是否有测试任务在进行中，如果有则需要停止这些测试任务，以免程序覆盖过程中引起问题；
 * 如果没有测试任务则直接连接服务器确认是否有新版本和新版本下载；
 * 其中{@link VersionUpgradeActivity}以界面提示的方式配合该服务完成更新流程；
 * @author maosen.zhang
 *
 */
public class VersionUpgradeService extends Service{  
	
	private static final String TAG = "VersionUpdateService";
	
	/** 下载最新版本以该名称存储 */
	public static final String APK_NAME = "walktour.apk";
	
    public static final char FLEET_4 = (char)0X00;
    public static final char FLEET_5 = (char)0X01;
    public static final char TASK_TYPE = (char)0X16;//22
    /** 设备类型，见文档 */
    public static final int DEVICE_TYPE = 3;
    /** 连接更新服务器超时时间 */
    public static final int TIMEOUT = 30;
    
    /**
     * 是否手工点开始
     */
    public static final String KEY_MANUAL = "manual";
    /**
     * 是否现在开始检查升级 
     */
    public static final String KEY_CHECK_NOW = "checkNow";
    
    /**
     * 回调进度
     */
    private final int CB_PROGRESS = 1;
    
    /**
     * 回调访问是否正在测试 
     */
    private final int CB_TESING = 2;
    
    //private final IBinder mBinder = new VersionUpgradeBinder();
    
    private String guid = "";
    
    /**
     * 已经下载的大小
     */
    private long dlSize = 0;
    
    /**
     * 是否手工点击升级
     */
    private boolean isUpdateManual = false;
    
    /**
     * 是否现在开始检查
     */
    private boolean checkItNow = false;
    
    private boolean hasDownloadFinish = false;

    /**
     * 是否正在测试，因为是跨进程，所以通过回调获取
     */
    private boolean isTesting = true;
    
    private UpgradeThread upgradeThread;
    
	
    /** 加载JNI库 */
	static{
		try {
			System.loadLibrary("version_upgrade_lib");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(),e);
		}
	}
	
	/**
	 * @see {@link VersionUpgradeActivity#onStart}
	 */
	public IBinder onBind(Intent intent) {
		Log.e(TAG, "onBind>>>>>>>>>>>>>>>>>>");
		return mBinder;  
	}
	
	private RemoteCallbackList<IVersionCallBack> mCallbacks = 
			new RemoteCallbackList<IVersionCallBack>();
	
	/**
	 * {@link VersionUpgradeActivity} 对此服务的远程调用接口
	 */
	private final IVersionService.Stub  mBinder = new IVersionService.Stub(){
		
		@Override
		/**
		 * 注册回调函数
		 */
		public void registerCallback(IVersionCallBack cb)
				throws RemoteException {
			if( cb!=null ){
				mCallbacks.register(cb);
			}
		}

		@Override
		public void unregisterCallback(IVersionCallBack cb)
				throws RemoteException {
			if( cb!=null ){
				mCallbacks.unregister(cb);
			}
		}
		
		@Override
		/**
		 * 开始下载
		 */
		public void startDownload() throws RemoteException {
			VersionUpgradeService.this.startDownload();
		}

		@Override
		/**
		 * 停止测试
		 */
		public void stopTest() throws RemoteException {
			VersionUpgradeService.this.stopTest();
		}

		@Override
		/**
		 * 安装
		 */
		public void installApk() throws RemoteException {
			VersionUpgradeService.this.installApk();
		}

		@Override
		public void stopDownload() throws RemoteException {
			VersionUpgradeService.this.stopDownload();
		}
		
	};
	
    @SuppressLint("HandlerLeak")
		private Handler mHandler = new Handler(){
    	@Override
    	public void handleMessage(Message msg){
    		resultCallBack(msg);
    		super.handleMessage(msg);
    	}
    };
    
    /**
     * 到{@link VersionUpgradeActivity}的回调函数
     * @param msg
     */
    private void resultCallBack(Message msg) {
    	int N = mCallbacks.beginBroadcast();
        try {   
            for (int i = 0; i < N; i++) {
            	switch(msg.what){
            	case CB_PROGRESS:
            		mCallbacks.getBroadcastItem(i).onProgressChange( msg.arg1 ,msg.arg2);
            		break;
            	case CB_TESING:
            		isTesting = mCallbacks.getBroadcastItem(i).isTesting();
            		Log.i( TAG, "isTesting:"+isTesting );
            		break;
            	}
            }   
        } catch (RemoteException e) {   
            Log.e(TAG, "", e);   
        }   
        mCallbacks.finishBroadcast();   
    } 
	
	/**
	 * 创建服务：
	 * 如果有测试任务正在进行则提示用户关闭测试任务；
	 * 反之则直接开始下载新版本；
	 */
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "onCreate>>>>>>>>>>>>>>>>>>");
		
		guid = MyPhoneState.getInstance().getGUID(
				VersionUpgradeService.this);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent,int startId){
		super.onStart(intent, startId);
		Log.e(TAG, "onStart>>>>>>>>>>>>>>>>>>");
		isUpdateManual = intent.getBooleanExtra( KEY_MANUAL, false);
		checkItNow = intent.getBooleanExtra( KEY_CHECK_NOW, false);
		
		if( checkItNow ){
			checkVersion();
		}
	}
	
	/**
	 * 检测是否有新版本，如果有新版本提示用户升级
	 * @author maosen.zhang
	 */
	class CheckUpgradeThread extends Thread {
		/**
		 * @param disPlayCheckInfo 是否要显示获取更新过程中的提示,手工点升级时要提示，
		 * 软件在后在检测版本时不提示
		 */
		public CheckUpgradeThread() {
			super("CheckUpgradeThread");  
		}

		public void run() {
			if( isUpdateManual ){
				showUpgradeDialog(VersionUpgradeActivity.FETCH_VERSION_INFO);
			}
			int handler = 0;
			try {
				handler = connectServer();
				
				Log.e(TAG, "initServer =="+handler);
				if (handler == 0) {
					if(isUpdateManual){
						showUpgradeDialog(VersionUpgradeActivity.EXCEPTION_DIALOG);
					}else{
						stopSelf();
					}
					return;
				}
				
				int result = CheckPageVersion(handler, DEVICE_TYPE,
						getCurrentVersionNo() + "");
				Log.e(TAG, "CheckPageVersion==" + result);

				if(result == 0){
						showUpgradeDialog(VersionUpgradeActivity.OLD_VERSION);
				}else if(result == 1){
					if( isUpdateManual ){
						showUpgradeDialog(VersionUpgradeActivity.NO_UPGRADE_DIALOG);
					}else{
						stopSelf();
					}
				}else{
					if( isUpdateManual ){
						showUpgradeDialog(VersionUpgradeActivity.EXCEPTION_DIALOG);
					}else{
						stopSelf();
					}
				}
			} catch (Exception e) {
				if( isUpdateManual ){
					showUpgradeDialog(VersionUpgradeActivity.EXCEPTION_DIALOG);
				}else{
					stopSelf();
				}
			}finally{
				freeServer(handler);
			}
		}
	}
	

	/**
	 * 开始检测新版本
	 */
	private void checkVersion(){
		new CheckUpgradeThread().start();
	}
	
	
	/**
	 * 开始下载新版本
	 */
	private void startDownload(){
		dlSize = 0;
		upgradeThread = new UpgradeThread();
		upgradeThread.start();
	}
	
	/**
	 * 取消下载
	 */
	private void stopDownload(){
		upgradeThread.setStop();
	}
	
	/**
	 * 停止业务测试
	 */
	private void stopTest(){
		Intent interruptIntent = new Intent(WalkMessage.Action_Walktour_Test_Interrupt);
		sendBroadcast(interruptIntent);
		showUpgradeDialog(VersionUpgradeActivity.TEST_STOP);
		isUpdateManual = true;
		new MonitorTestThread().start();
	}
	
	/**
	 * 监控测试任务是否停止线程
	 * 如果测试任务停止则开始下载新版本
	 */
	class MonitorTestThread extends Thread{
		public MonitorTestThread() {
			super("MonitorTestThread");  
			
		}
		
		public void run() {
			
			do{
				mHandler.obtainMessage( CB_TESING ).sendToTarget();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Log.e(TAG, e.getMessage(),e);
					break;
				}
			}while( isTesting);
			
			checkVersion();
		}
	}
	
	/**
	 * 监听进度的线程，当长时间没有进度变化时，重新下载
	 * @author qihang.li
	 */
	class MonitorProgreThread extends Thread{
		
		private long lastSize = 0;
		
	    /**
	     * 下载无响应时间
	     */
	    private int noRespontime = 0;
	    
		@Override
		public void run(){
			while( !hasDownloadFinish ){
				if( lastSize == dlSize ){
					noRespontime ++;
				}else{
					noRespontime = 0;
				}
				lastSize = dlSize;
				
				//3无数据超时
				if( noRespontime > 60 ){
					showUpgradeDialog( VersionUpgradeActivity.NO_RESPONSE );
					break;
				}
				
				Log.i(TAG, String.format("dlSize:%s,noResponse:%s",dlSize,noRespontime) );
				
				try {
					Thread.sleep( 1000 );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/** 下载新版本进程 */
	class UpgradeThread extends Thread{
		private boolean isStop = false;
	    /** 连接服务器的JNI调用句柄 */
	    private int serverHandle = 0;
	    
		public UpgradeThread() {
			super("UpgradeThread");  
			
		}
		
		public void run() {
			upgrade();
		}
		
		/**
		 * 开始下载新版本
		 */
		private void upgrade(){
			showUpgradeDialog(VersionUpgradeActivity.CONNECTING_SERVER);
			serverHandle = 0;
			try {
				serverHandle = connectServer();
				
				
				Log.e(TAG, "initServer =="+serverHandle);
				if (serverHandle == 0) {
					showUpgradeDialog(VersionUpgradeActivity.EXCEPTION_DIALOG);
					return;
				}
				
				int result = CheckPageVersion(serverHandle, DEVICE_TYPE,
						getCurrentVersionNo() + "");
				Log.e(TAG, "CheckPageVersion==" + result);

				if (result != 0) {
					if (result == 1) {
						showUpgradeDialog(VersionUpgradeActivity.NO_UPGRADE_DIALOG);
					} else {
						showUpgradeDialog(VersionUpgradeActivity.EXCEPTION_DIALOG);
					}
					return;
				}

				showUpgradeDialog(VersionUpgradeActivity.START_DOWNLOAD);
				deleteApk();

				new MonitorProgreThread().start();
				
				hasDownloadFinish = false;
				result = fleetrenew(serverHandle, getAPKPath());
				hasDownloadFinish = true;

				Log.e(TAG, "fleetrenew ==" + result);
				
				if (result == 0) {
					
					showUpgradeDialog( VersionUpgradeActivity.DOWNLOAD_FINISH );
					
					installApk();
				} else if(result == -3){//停止下载，不做处理
					
				}else {
					if(!isStop){
						showUpgradeDialog(VersionUpgradeActivity.EXCEPTION_DIALOG);
					}
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
				if(!isStop){
					 showUpgradeDialog(VersionUpgradeActivity.EXCEPTION_DIALOG);
				}
			} finally {
				
				Log.e(TAG, "freeServer");
				freeServer(serverHandle);
			}
		}
		
		public void setStop() {
			isStop = true;
			stoprenew(serverHandle);
		}
		
	}
	

	/**
	 * 连接版本更新服务器
	 * @return	0  失败；非0 成功
	 */
	private int connectServer(){
		int handle = 0;
		boolean initOk = true;
		try {
//			ServerSetParam serverParamItem = SystemSetControl
//					.getInstance(VersionUpgradeService.this)
//					.getServerSetItem();
//			String ip = serverParamItem.getIpaddress();
//			int port = Integer.parseInt(serverParamItem.getPort());
			
			
			ServerManager server = ServerManager.getInstance( VersionUpgradeService.this );
			String ip = server.getUploadFleetIp();
			int port = server.getUploadFleetPort();
			
			//TODO
			//String ip = "61.143.60.84";  
			//int port = 60378;
			Log.e(TAG, "TASK_TYPE=="+(int)TASK_TYPE+" guid=="+guid+" ip=="+ip+" port=="+port);
			handle = initfleethandle(ip, port, guid, TIMEOUT, FLEET_5, TASK_TYPE, android.os.Build.MODEL);
			Log.e(TAG, "initfleethandle>>>>>" + handle);

			if (handle == 0) {
				initOk = false;
				return 0;
			}
			int result = fleetconnect(handle);
			Log.e(TAG, "fleetconnect>>>>>" + result);
			if (result != 0) {
				initOk = false;
				return 0;
			}

			result = Fleetlogin(handle);
			Log.e(TAG, "Fleetlogin>>>>>" + result);
			if (result != 0) {
				initOk = false;
				return 0;
			}
			return handle;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			initOk = false;
			return 0;
		} finally {
			if (!initOk) {
				Log.e(TAG, "initServer error >> freefleethandle");
				freeServer(handle);
			}
		}
	}
	
	/**
	 * 释放句柄
	 * @param handle
	 */
	private void freeServer(int handle){
		if (handle != 0) {
			freefleethandle(handle);
		}
	}
	
	/**
	 * 显示下载提示对话框
	 * @param dialogId 待显示对话框的ID
	 */
	private void showUpgradeDialog(int dialogId){
		Intent intent = new Intent(this, VersionUpgradeActivity.class);
		intent.putExtra(VersionUpgradeActivity.DIALOG_ID,dialogId);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	 
	/**
	 * 获取当前的版本号
	 * 
	 * @return
	 */
	private String getCurrentVersionNo() {
		try {
//			int curVersion = getPackageManager().getPackageInfo(
//					getPackageName(), 0).versionCode;
			String curVersion = getPackageManager().getPackageInfo(
					getPackageName(), 0).versionName;
			Log.i(TAG, "curVersion == "+curVersion);
			return curVersion;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage(),e);
			return Integer.MAX_VALUE+"";
		}

	}

	/**
	 * 删除安装包
	 */
	private void deleteApk() {
		File file = new File(getAPKPath());
		if (file.exists()) {
			file.delete();
		}
	}
	
	/**
	 * 获取新版本下载后的存放路径和名称
	 * @return 新版本存放路径
	 */
	private String getAPKPath(){
		return this.getFilesDir().getAbsolutePath() + File.separator
		+ APK_NAME;
	}
    
	/**
	 * 安装程序
	 */
	private void installApk() {
		String filePath = getFilesDir().getAbsolutePath()
				+ File.separator + APK_NAME;
		boolean succ = UtilsMethod.runRootCommand("chmod 777 " + filePath);
		if (succ) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.setDataAndType(Uri.parse("file://" + filePath),
					"application/vnd.android.package-archive");
			startActivity(i);
		} else {
			Toast.makeText(this, R.string.update_install_fail, Toast.LENGTH_LONG).show();
		}
	}
	
//	/**
//	 * 检查是否允许安装非市场应用
//	 */
//	public void checkInstallNonMarket(){
//		int result = Settings.Secure.getInt(getContentResolver(),
//				Settings.Secure.INSTALL_NON_MARKET_APPS, 0 );     
//		
//		Log.e(TAG, "result:"+result );
//		if(result == 0) {     
//			showUpgradeDialog(VersionUpgradeActivity.NON_MARKET_INSTALL);
//		}else{
//			
//		}
//	}
//	
	/**
	 * JNI回调函数，用于计算下载进度
	 * @param tSize	总大小
	 * @param dSize 该次下载大小
	 */
	public void fleetcallback(int tSize,int dSize){
		dlSize += dSize ;
		mHandler.obtainMessage(CB_PROGRESS, (int)dlSize, tSize).sendToTarget();;
	}
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	Log.e(TAG, "onDestroy>>>>>>>>>>>>>>");
    	hasDownloadFinish = true;
    	UtilsMethod.killProcessByPname("com.walktour.service.VersionUpgradeService",true);
    }
	
	/**
           初始化fleet信息 获取句柄
	@param	serverip fleet服务器ip
	@param 	port 服务器端口
	@param	id 设别id号 根据协议区分 可查看文档 
	@param	timeout 连接服务器超时设置
	@param 	fleettype fleet类型 目前有fleet4 和 fleet5 值可查看头文件中定义
	@param	tasktype 任务类型 查看fleet文档 比如上传监控数据是20 更新是22 定义按照头文件中定义传入
	@param 	devmsg 设备信息 比如XT800 G7
	@return 	0失败，非0成功
	*/
	public native int initfleethandle(String serverip,int port,String id,int timeout,char fleettype,char tasktype,String devmsg);
	/**
	    释放句柄
	@return 	非0失败，0成功
	*/
	public native int freefleethandle(int handle);
	/**
	   连接fleet服务器
	@return  非0失败，0成功
	*/
	public native int fleetconnect(int handle);
	/**
	    登陆fleet服务器
	@return	非0失败，0成功
	*/
	public native int Fleetlogin(int handle);
	
	/**
	* 验证是否需要更新
	* @param handle		handle句柄
	* @param device_type	deviceType 设备类型  1为RCU或WalkTour；2为智能感知一键投诉 
	* @param version		version 目前本地的软件版本号 比如1.0.0.1
	* @return	 0 需要更新 1 不需要更新 -1网络错误 
	*/
	public native int CheckPageVersion(int handle,int device_type, String version );
	/**
	* 实现fleet5 flag=22 升级协议 具体可参看文件 特别是下面需要的参数的意义
	*@param	filepath 更新软件存的路径信息
	*
	*@return 0 成功 其它失败 -1网络出错 -2本地文件出错 -3停止下载
	*/
	public native int fleetrenew(int handle,String filepath);
	/**
	    停止更新软件包
	@return非0失败，0成功
	*/   
	public native int stoprenew(int handle);	
}
