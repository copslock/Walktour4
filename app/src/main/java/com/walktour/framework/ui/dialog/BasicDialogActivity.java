/**
 * com.walktour.framework.ui.dialog
 * DialogFragmentExampleActivity.java
 * 类功能：
 * 2014-4-22-上午10:33:02
 * 2014鼎利-版权所有
 * @author qihang.li@dinglicom.com
 */
package com.walktour.framework.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.walktour.framework.ui.ActivityManager;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;

/**
 * DialogFragmentExampleActivity
 * 
 * 2014-4-22 上午10:33:02
 * 
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
public class BasicDialogActivity extends Activity {

	public BasicDialog mDialog = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		ActivityManager.addActivity(this);
		showDialog( getIntent() );
    }
    
    @Override
	public void onNewIntent(Intent intent){
		super.onNewIntent(intent);
		showDialog( intent );
	}

	public void showDialog(Intent intent) {
    	String title = intent.getStringExtra("title");
    	String message = intent.getStringExtra("message");
    	if (message==null||title==null){
    		finish();
    		return;
		}
    	if( mDialog!=null ){
    		mDialog.dismiss();
    	}
    	
    	mDialog = new BasicDialog.Builder( this )  
        .setTitle(title)  
        .setMessage( message )
        .setPositiveButton( R.string.str_ok,  
                new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog,   
                    int whichButton) {
            	finish();
            }  
        }).create();
    	mDialog.setCancelable(false);
    	mDialog.show();
    }
	@Override
	public void onDestroy() {
		super.onDestroy();
		ActivityManager.removeActivity(this);
		this.finish();
	}
}
