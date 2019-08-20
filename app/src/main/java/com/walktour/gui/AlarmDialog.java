package com.walktour.gui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.walktour.Utils.UtilsMethod;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.model.AlarmModel;

public class AlarmDialog extends BasicActivity{
	private String tag = "AlarmDialog";
	private Context mContext = null;
	private String content ="";	//告警内容 
	private BasicDialog dialog = null;
	private boolean hasFinish = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		addAlarmDialog( getIntent() );
	}
	
	@Override
	public void onNewIntent(Intent intent){
		super.onNewIntent(intent);
		addAlarmDialog( intent );
	}
	
	@Override
	public void onDestroy(){
		Log.e(tag, "---onDestroy");
		if( dialog!=null ){
			dialog.cancel();
		}
		hasFinish = true;
		super.onDestroy();
	}
	
	private void closeActicity(){
		hasFinish = true;
		finish();
	}
	
	private void addAlarmDialog(Intent intent){
		
		Bundle  bundle = getIntent().getExtras();
		
		AlarmModel alarmModel = null;
		if( bundle!=null ){
			Object obj = bundle.getSerializable("alarm");
			if( obj!=null ){
				alarmModel = (AlarmModel) obj;
			}
		}
		
		if( alarmModel !=null ){
			content = String.format("%s %s", 
					UtilsMethod.getSimpleDateFormat1( alarmModel.getTime() )
					,alarmModel.getDescription( mContext) );
			
			if( dialog!=null ){
				dialog.dismiss();
			}
			dialog = new BasicDialog.Builder( this )
			.setTitle(R.string.sys_tab_alert )
			.setMessage( content )
			.setPositiveButton( R.string.str_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					closeActicity();
				}
			}).setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					dialog.dismiss();
					closeActicity();
				}
			}).create();
			
			//如果Activity未被前一个窗口关闭时才显示
			if( !hasFinish ){
				dialog.show();
			}
		}
		
	}
}
