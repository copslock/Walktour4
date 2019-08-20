package com.walktour.gui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;


/**
 * 版本更新提示界面</br>
 * 透明界面，只为显示对话框，提示用户下载的流程和错误的信息
 * 配置文件中 android:theme="@android:style/Theme.Translucent"
 * @author maosen.zhang
 *
 */
public class RebootDialog extends BasicActivity{
	private static String tag = "RebootDialog";
	
	/** 对话框的ID */				
	public static final String DIALOG_ID = "RebootDialog.DIALOG_ID";
	/** 连续三次拨号失败，是否结束以便检测网络设置是否有问题 */
	public static final int traceInitFaileToRboot = 1;		
	
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
		Intent intent = getIntent();
		int dialogId = intent.getIntExtra(DIALOG_ID, traceInitFaileToRboot);
		
		LogUtil.w(tag,"--onCreate--dialogId:"+dialogId);
		//showDialog(dialogId);
		
		mHandler.obtainMessage(DIALOG_ON, getString(R.string.main_rebootDevice)).sendToTarget();
		//new waitDismissDialog().start();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(currentShowDialogId != -1){
			mHandler.obtainMessage(DIALOG_OFF);
		}
		int dialogId = intent.getIntExtra(DIALOG_ID, traceInitFaileToRboot);
		LogUtil.w(tag,"--onNewIntent--dialogId:"+dialogId);
		//showDialog(dialogId);
		
		mHandler.obtainMessage(DIALOG_ON, getString(R.string.main_rebootDevice)).sendToTarget();
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
					mHandler.obtainMessage(DIALOG_ON, getString(R.string.main_rebootDevice)+" "+i).sendToTarget();
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
		    				mDialog = new BasicDialog.Builder(RebootDialog.this)
		    				.setTitle(R.string.str_tip)
		    				.setIcon(R.drawable.walktour38)
		    				.setMessage((String)msg.obj)
		    				.setPositiveButton(R.string.str_ok, 			
		    						new DialogInterface.OnClickListener() {
		    							public void onClick(DialogInterface dialog,int whichButton) {
		    								RebootDialog.this.finish();
		    								sendBroadcast(new Intent(WalkMessage.InteruptTestAndRebootDevice));
											//UtilsMethod.runRootCommand("reboot");
		    							}
		    						}
		    				)
		    				.setNegativeButton(R.string.str_cancle, 			
		    						new DialogInterface.OnClickListener() {
		    							public void onClick(DialogInterface dialog,int whichButton) {
		    								RebootDialog.this.finish();
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
		    			//finish();
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
		case traceInitFaileToRboot:	//当前版本不是最新版本，请更新...
			currentShowDialogId = traceInitFaileToRboot;
			return new AlertDialog.Builder(RebootDialog.this)
			.setTitle(getString(R.string.str_tip))
			.setIcon(R.drawable.walktour)
			.setMessage(getString(R.string.main_pppfaild_tointerrupt))
			.setCancelable(false)
			.setPositiveButton(getString(R.string.str_ok), 			
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int whichButton) {
							sendBroadcast(new Intent(WalkMessage.Action_Walktour_Test_Interrupt));
							RebootDialog.this.finish();
						}
					}
			)
			.setNegativeButton(getString(R.string.str_cancle), 			
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int whichButton) {
							RebootDialog.this.finish();
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
		LogUtil.w(tag,"--onDestroy--");
	}
}
