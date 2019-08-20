package com.walktour.gui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Window;

import com.walktour.Utils.ApplicationModel;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.service.app.IVersionCallBack;
import com.walktour.service.app.IVersionService;
import com.walktour.service.app.VersionUpgradeService;

/**
 * 版本更新提示界面</br>
 * 透明界面，只为显示对话框，提示用户下载的流程和错误的信息
 * 配置文件中 android:theme="@android:style/Theme.Translucent"
 * @author maosen.zhang
 *
 */
public class VersionUpgradeActivity extends BasicActivity{
	
	private static String TAG = "VersionUpgradeActivity";
	
	private IVersionService mService;
	
	/** 该界面是否是和后台服务绑定着，如果在绑定应在 {@link#onDestroy} 时解除绑定*/
	boolean isBound = false;
	
	
	/** 对话框的ID */				
	public static final String DIALOG_ID = "VersionUpgradeActivity.DIALOG_ID";

	/** 提示用户“检测到新版本请更新”对话框 */
	public static final int UPDATE_DIALOG = 1;		
	/** 下载失败对话框 */
	public static final int EXCEPTION_DIALOG = 2;
	/** 最新版本无需更新 */
	public static final int NO_UPGRADE_DIALOG = 3;
	
	/** 连接服务器中 */
	public static final int CONNECTING_SERVER = 4;
	
	/** 开始下载 */
	public static final int START_DOWNLOAD = 5;
	
	/** 下载完成 */
	public static final int DOWNLOAD_FINISH = 55;
	
	/** 业务测试正在进行 */
	public static final int TESTING = 6;
	
	/** 业务测试正在停止 */
	public static final int TEST_STOP = 7;
	
	/** 获取服务器版本信息 */
	public static final int FETCH_VERSION_INFO = 8;
	
	/** 版本需更新 */
	public static final int OLD_VERSION = 9;
	
	/** 下载无响应 */
	public static final int NO_RESPONSE = 10;
	
	/**当前不是WIFI连接*/
	public static final int WIFI_NOT_CONNECT = 11;
	
	/** 标记当前正在显示的对话框 */
	private int currentShowDialogId = -1;
	
	
	private ProgressDialog mProgressDialog ;
	
	private boolean hasWifiPage = false;
	
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
        	//获取远程服务
            mService = IVersionService.Stub.asInterface(service);
            
            //注册远程服务回调
            try {
				mService.registerCallback(versionCallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
        	try {
				mService.unregisterCallback(versionCallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
        	
        	mService = null;
            isBound = false;
        }
    };
    
    /**
     * 从{@link VersionUpgradeService}调用的回调函数;
     */
    private IVersionCallBack versionCallback = new IVersionCallBack.Stub() {
		
		@Override
		public void onResultChange(int result) throws RemoteException {
			
		}
		
		@Override
		public void onProgressChange(int dlSize,int totalSize) throws RemoteException {
			mProgressDialog.setMax( totalSize/1000 );
			mProgressDialog.setProgress( dlSize/1000 );
		}

		@Override
		public boolean isTesting() throws RemoteException {
			return ApplicationModel.getInstance().isTestJobIsRun()
					|| ApplicationModel.getInstance().isTestStoping() ;
		}
	};
    	
	/**
	 * 创建界面：
	 * 根据传入的DIALOG_ID,显示相应的对话框
	 */
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent intent = getIntent();
		int dialogId = intent.getIntExtra(DIALOG_ID, CONNECTING_SERVER);
		
		showDialog(dialogId);
		Log.e(TAG, "onCreate>>>>>>>>>>>");
	}
	
	@Override
	@SuppressWarnings("deprecation")
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(currentShowDialogId != -1){
			dismissDialog(currentShowDialogId);
		}
		int dialogId = intent.getIntExtra(DIALOG_ID, CONNECTING_SERVER);
		showDialog(dialogId);
		Log.e(TAG, "onNewIntent>>>>>>>>>>>");
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.e(TAG, "onStart>>>>>>>>>>>");
		bindService();
	}
	
	/**
	 * 
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void onResume(){
		super.onResume();
		Log.e(TAG, "onResume>>>>>>>>>>>");
		if( currentShowDialogId == DOWNLOAD_FINISH ){
			dismissDialog(currentShowDialogId);
			showDialog( DOWNLOAD_FINISH );
		}
		
		else if(  hasWifiPage ){
			dismissDialog( currentShowDialogId );
			showDialog( OLD_VERSION );
			hasWifiPage = false;
		}
	}

	@SuppressWarnings("deprecation")
	protected Dialog onCreateDialog(int id) {
		Log.e(TAG, "onCreateDialog>>>>>>>>>>>");
		switch (id) {
		case CONNECTING_SERVER:		//正在连接服务器请稍后...
			currentShowDialogId = CONNECTING_SERVER;
			ProgressDialog connectingDialog = new ProgressDialog(this);
			connectingDialog.setTitle(getString(R.string.update_ver_update));
			connectingDialog.setIcon(R.mipmap.walktour);
			connectingDialog.setIndeterminate(true);
			connectingDialog.setMessage(getString(R.string.connete_server));
			connectingDialog.setCancelable(false);
			return connectingDialog;
		case EXCEPTION_DIALOG:		//下载失败提示窗口
			currentShowDialogId = EXCEPTION_DIALOG;
			return new AlertDialog.Builder(VersionUpgradeActivity.this)
			.setTitle(getString(R.string.update_ver_update))
			.setIcon(R.mipmap.walktour)
			.setMessage(getString(R.string.update_download_fail))
			.setCancelable(false)
			.setNegativeButton(getString(R.string.str_ok), 	
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int whichButton) {
							VersionUpgradeActivity.this.finish();
						}
					}
			)
			.create();
		case NO_UPGRADE_DIALOG: 	//已是最新的版本无需更新
			currentShowDialogId = NO_UPGRADE_DIALOG;
			return new AlertDialog.Builder(VersionUpgradeActivity.this)
			.setTitle(getString(R.string.update_ver_update))
			.setIcon(R.mipmap.walktour)
			.setMessage(getString(R.string.no_update))
			.setCancelable(false)
			.setNegativeButton(getString(R.string.str_ok),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int whichButton) {
							VersionUpgradeActivity.this.finish();
						}
					}
			)
			.create();
		case START_DOWNLOAD:	//开始下载，并提示下载进度
			currentShowDialogId = START_DOWNLOAD;
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setTitle(getString(R.string.update_ver_update));
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setButton(DialogInterface.BUTTON_NEUTRAL,	//取消下载
					getString(R.string.str_cancle),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int whichButton) {
							Log.e(TAG, "---BUTTON_NEUTRAL" );
							
							mProgressDialog.setCancelable(true);
							mProgressDialog.cancel();
							
							try {
								mService.stopDownload();
							} catch (RemoteException e) {
								e.printStackTrace();
							}
							VersionUpgradeActivity.this.finish();
						}
					});
//			mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//				@Override
//				public void onDismiss(DialogInterface dialog) {
//					Log.e(TAG, "onDismiss" );
//					mService.stopDownload();
//					VersionUpgradeActivity.this.finish();
//				}
//			});
			return mProgressDialog;
		case TESTING:		//正在测试中，点击确定停止测试，并继续升级
			currentShowDialogId = TESTING;
			return new AlertDialog.Builder(VersionUpgradeActivity.this)
			.setTitle(getString(R.string.update_ver_update))
			.setIcon(R.mipmap.walktour)
			.setMessage(getString(R.string.update_testing))
			.setCancelable(false)
			.setPositiveButton(getString(R.string.str_ok), 			
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int whichButton) {
							try {
								mService.stopTest();
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
					}
			)
			.setNegativeButton(getString(R.string.str_cancle), 			
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int whichButton) {
							VersionUpgradeActivity.this.finish();
						}
					}
			)
			.create();
		case TEST_STOP:		//正在停止业务测试请稍后...
			currentShowDialogId = TEST_STOP;
			ProgressDialog stopTestDialog = new ProgressDialog(this);
			stopTestDialog.setTitle(getString(R.string.update_ver_update));
			stopTestDialog.setIcon(R.mipmap.walktour);
			stopTestDialog.setIndeterminate(true);
			stopTestDialog.setMessage(getString(R.string.stop_testing));
			stopTestDialog.setCancelable(false);
			return stopTestDialog;
			
		case FETCH_VERSION_INFO://正在获取服务器版本信息...
			currentShowDialogId = FETCH_VERSION_INFO;
			ProgressDialog fetchVersionDialog = new ProgressDialog(this);
			fetchVersionDialog.setTitle(getString(R.string.update_ver_update));
			fetchVersionDialog.setIcon(R.mipmap.walktour);
			fetchVersionDialog.setIndeterminate(true);
			fetchVersionDialog.setMessage(getString(R.string.fetch_version_info));
			fetchVersionDialog.setCancelable(false);
			return fetchVersionDialog;
			
		case OLD_VERSION:	//当前版本不是最新版本，请更新...
			currentShowDialogId = OLD_VERSION;
			return new AlertDialog.Builder(VersionUpgradeActivity.this)
			.setTitle(getString(R.string.update_ver_update))
			.setIcon(R.mipmap.walktour)
			.setMessage(getString(R.string.version_old))
			.setCancelable(false)
			.setPositiveButton(getString(R.string.str_ok), 			
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int whichButton) {
							dialog.dismiss();
							
							ConnectivityManager conMan = 
									(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
							State state = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
							 if( state == State.CONNECTED ){
								 try {
									mService.startDownload();
								} catch (RemoteException e) {
									e.printStackTrace();
								}
							 }else{
								 showDialog( WIFI_NOT_CONNECT );
							 }
							 
						}
					}
			)
			.setNegativeButton(getString(R.string.str_cancle), 			
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int whichButton) {
							dialog.dismiss();
							VersionUpgradeActivity.this.finish();
						}
					}
			)
			.create();
			
		case WIFI_NOT_CONNECT:	//当前没有使用WIFI连接，是否下载
			currentShowDialogId = WIFI_NOT_CONNECT;
			return new AlertDialog.Builder(VersionUpgradeActivity.this)
			.setTitle(getString(R.string.update_ver_update))
			.setIcon(R.mipmap.walktour)
			.setMessage( getString( R.string.download_wifi_off) )
			.setCancelable(false)
			.setPositiveButton(getString(R.string.download_set_wifi ), 			
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int whichButton) {
					wifiSetting();
				}
				}
		    )
		    .setNeutralButton( getString( R.string.download_on_gprs), 
		    		new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						mService.startDownload();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			})
			.setNegativeButton(getString(R.string.str_cancle), 			
					new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int whichButton) {
						VersionUpgradeActivity.this.finish();
					}
				}
			).create();
			
		case NO_RESPONSE:
			currentShowDialogId = NO_RESPONSE;
			//EDGE网下发时，无响应之后库会崩溃，尝试重新启动
			stopAndUnBindService();
			bindService();
			return new AlertDialog.Builder(VersionUpgradeActivity.this)
			.setTitle(getString(R.string.update_ver_update))
			.setIcon(R.mipmap.walktour)
			.setMessage(getString(R.string.download_no_response))
			.setCancelable(false)
			.setPositiveButton(getString( R.string.setting ), 			
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int whichButton) {
							wifiSetting();
						}
					}
			)
			.setNegativeButton(getString(R.string.str_cancle), 			
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int whichButton) {
							VersionUpgradeActivity.this.finish();
						}
					}
			)
			.create();
			
		case DOWNLOAD_FINISH:
			currentShowDialogId = DOWNLOAD_FINISH;
			mProgressDialog.cancel();
			return new AlertDialog.Builder(VersionUpgradeActivity.this)
			.setTitle(getString(R.string.update_ver_update))
			.setIcon(R.mipmap.walktour)
			.setMessage(getString(R.string.download_finish))
			.setCancelable(false)
			.setPositiveButton(getString( R.string.download_finish_install ), 			
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int whichButton) {
					try {
						mService.installApk();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
					)
			.setNegativeButton(getString(R.string.str_cancle), 			
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int whichButton) {
					VersionUpgradeActivity.this.finish();
				}
			}
			).create();
			
		default:
			return null;
		}
	}
	
	private void wifiSetting(){
		hasWifiPage = true;
		Intent intentActivity = new Intent( "android.intent.action.MAIN"  );
		ComponentName componentName = new ComponentName(
				"com.android.settings",
				"com.android.settings.wifi.WifiSettings"
		);  
		intentActivity.setComponent(componentName);  
		startActivity( intentActivity );
	}
   
    @Override
	protected void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "onDestroy");
		stopAndUnBindService();
	}
    
    private void bindService(){
    	Intent intent = new Intent(this, VersionUpgradeService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    
    private void stopAndUnBindService(){
    	
    	stopService(new Intent(VersionUpgradeActivity.this, VersionUpgradeService.class));
    	
    	if (isBound) {
			unbindService(mConnection);
			isBound = false;
		}
    }
    
//   public class DownLoadProgressListener{
//	   
//	   public DownLoadProgressListener(){
//		   if(mProgressDialog != null){
//			   mProgressDialog.setProgress( 0 );
//		   }
//	   }
//	 
//		/**
//	     * 下载进度变化时候回调函数
//	     * @param tSize	总大小
//	     * @param dSize 该次下载大小
//	     */
//    	public void onProgressChanged(int tSize,int dSize){
//    		if(mProgressDialog != null){
//    			mProgressDialog.setMax(tSize);
//    			mProgressDialog.setProgress(mProgressDialog.getProgress()+dSize);
//    		}
//    	}
//    	
//    	public void reset(){
//    		if( mProgressDialog!=null ){
//    			mProgressDialog.setProgress( 0 );
//    		}
//    	}
//    }
}
