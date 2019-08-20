package com.walktour.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.setting.Sys;


/**
 * 版本更新提示界面</br>
 * 透明界面，只为显示对话框，提示用户下载的流程和错误的信息
 * 配置文件中 android:theme="@android:style/Theme.Translucent"
 * @author maosen.zhang
 *
 */
public class ServiceDialog extends BasicActivity{
	private static String tag = "ServiceDialog";
	
	/** 对话框的ID */				
	public static final String DIALOG_ID = "ServiceDialog.DIALOG_ID";
	/** 连续三次拨号失败，是否结束以便检测网络设置是否有问题 */
	public static final int PPPFaildToInterrupt = 1;
	/**蓝牙连接接开中*/
	public static final int BLUETOOTH_INTERRUPT	= 2;
	
	/** 标记当前正在显示的对话框 */
	private static int currentShowDialogId = -1;
	
	private final int DIALOG_ON 	= 1;
	private final int DIALOG_OFF 	= 2;
	private BasicDialog mDialog =null;
	
        
	/**
	 * 创建界面：
	 * 根据传入的DIALOG_ID,显示相应的对话框
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		registerRece();
		
		Intent intent = getIntent();
		int dialogId = intent.getIntExtra(DIALOG_ID, PPPFaildToInterrupt);
		
		LogUtil.w(tag,"--onCreate--dialogId:"+dialogId);
		//showDialog(dialogId);
		
		Message msg = mHandler.obtainMessage(DIALOG_ON);
		msg.arg1 = dialogId;
		msg.sendToTarget();
		//mHandler.obtainMessage(DIALOG_ON, getString(R.string.main_pppfaild_tointerrupt)).sendToTarget();
		//new waitDismissDialog().start();
	}
	
	private void registerRece(){
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(WalkMessage.ACTION_BLUETOOTH_SOCKET_CHANGE);
		registerReceiver(receiver, iFilter);
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(WalkMessage.ACTION_BLUETOOTH_SOCKET_CHANGE)){
				if(ApplicationModel.getInstance().isBluetoothConnected()){
					mHandler.obtainMessage(DIALOG_OFF).sendToTarget();
					//ServiceDialog.this.finish();
				}
			}
		}
	};
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(currentShowDialogId != -1){
			mHandler.obtainMessage(DIALOG_OFF);
		}
		int dialogId = intent.getIntExtra(DIALOG_ID, PPPFaildToInterrupt);
		LogUtil.w(tag,"--onNewIntent--dialogId:"+dialogId);
		//showDialog(dialogId);
		
		Message msg = mHandler.obtainMessage(DIALOG_ON);
		msg.arg1 = dialogId;
		msg.sendToTarget();
		//mHandler.obtainMessage(DIALOG_ON, getString(R.string.main_pppfaild_tointerrupt)).sendToTarget();
		//new waitDismissDialog().start();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	/**
	 * 倒数多长时间关闭dialog窗口
	 * @author tangwq
	 *
	 */
	class waitDismissDialog extends Thread{
		public void run(){
			if(currentShowDialogId == -1){
				currentShowDialogId = 1;
				
				for(int i=10;i>0 && currentShowDialogId == 1;i--){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					LogUtil.w(tag,"---waitDimess:"+i);
					mHandler.obtainMessage(DIALOG_ON, getString(R.string.main_pppfaild_tointerrupt)+" "+i).sendToTarget();
				}
				
				mHandler.obtainMessage(DIALOG_OFF).sendToTarget();
			}
		}
	}
	
	private Handler mHandler =new Handler(){
	    	@Override
	    	public void handleMessage(Message msg){
	    		try{
		    		switch (msg.what){
		    		case DIALOG_ON:
		    			currentShowDialogId = 1;
		    			if(mDialog == null){
		    				final int DiagType = msg.arg1;
		    				String showMsg = (DiagType == BLUETOOTH_INTERRUPT ? 
		    						 getString(R.string.sys_setting_bluetooth_disconnected)
		    						:getString(R.string.main_pppfaild_tointerrupt));
		    				
		    				mDialog = new BasicDialog.Builder(ServiceDialog.this)
		    				.setTitle(R.string.str_tip)
		    				.setIcon(R.drawable.walktour38)
		    				.setMessage(showMsg)
		    				.setPositiveButton(R.string.str_ok, 			
		    						new DialogInterface.OnClickListener() {
		    							public void onClick(DialogInterface dialog,int whichButton) {
		    								if(DiagType == BLUETOOTH_INTERRUPT){
		    									Intent sys = new Intent(getApplicationContext(),Sys.class);
		    									sys.putExtra(Sys.CURRENTTAB, 0);
		    									startActivity(sys);
		    								}else{
		    									sendBroadcast(new Intent(WalkMessage.Action_Walktour_Test_Interrupt));
		    								}
		    								ServiceDialog.this.finish();
		    							}
		    						}
		    				)
		    				.setNegativeButton(R.string.str_cancle, 			
		    						new DialogInterface.OnClickListener() {
		    							public void onClick(DialogInterface dialog,int whichButton) {
		    								ServiceDialog.this.finish();
		    							}
		    						}
		    				)
		    				.create();
		    			}
		    			mDialog.show();
		    			break;
		    		case DIALOG_OFF:
		    			if(mDialog != null){
		    				mDialog.dismiss();
		    			}
		    			currentShowDialogId = -1;
		    			mDialog = null;
		    			finish();
		    			break;
		    		}
	    		}catch(Exception e){
	    			e.printStackTrace();
	    		}
		}
	};
	
	/*protected Dialog onCreateDialog(int id) {
		LogUtil.w(tag, "--onCreateDialog--");
		switch (id) {
		case PPPFaildToInterrupt:	//当前版本不是最新版本，请更新...
			currentShowDialogId = PPPFaildToInterrupt;
			return new AlertDialog.Builder(ServiceDialog.this)
			.setTitle(getString(R.string.str_tip))
			.setIcon(R.drawable.walktour)
			.setMessage(getString(R.string.main_pppfaild_tointerrupt))
			.setCancelable(false)
			.setPositiveButton(getString(R.string.str_ok), 			
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int whichButton) {
							sendBroadcast(new Intent(WalkMessage.Action_Walktour_Test_Interrupt));
							ServiceDialog.this.finish();
						}
					}
			)
			.setNegativeButton(getString(R.string.str_cancle), 			
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int whichButton) {
							ServiceDialog.this.finish();
						}
					}
			)
			.create();
		default:
			return null;
		}
	}*/
	
    @Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		LogUtil.w(tag,"--onDestroy--");
	}
}
