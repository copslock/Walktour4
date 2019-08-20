package com.walktour.gui.locknet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.locknet.ForceControler.ForceNet;

@SuppressLint("InflateParams")
public class LockNetWork extends BasicActivity{
	private RelativeLayout auto;
	private RelativeLayout gsm;
	private RelativeLayout tds;
	private RelativeLayout lte;
	LayoutInflater inflater;
	private ForceManager mForceMgr = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lock_dev_network);
		
		mForceMgr = ForceManager.getInstance();
		mForceMgr.init();
		mForceMgr.setOnTaskChangeListener( onTaskChangeListener );
		
		inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		(initTextView(R.id.title_txt)).setText(R.string.lock_locknet);
        findViewById(R.id.pointer).setOnClickListener(this);
        
        auto = (RelativeLayout)findViewById(R.id.setting_auto);
        gsm = (RelativeLayout)findViewById(R.id.setting_gsm);
        tds = (RelativeLayout)findViewById(R.id.setting_td);
        lte = (RelativeLayout)findViewById(R.id.setting_lte);
        
        auto.setOnClickListener(this);
        gsm.setOnClickListener(this);
        tds.setOnClickListener(this);
        lte.setOnClickListener(this);
	}

	private BasicDialog dialog;
	private TextView diaMessage;
	private ProgressBar pb;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pointer:
			this.finish();
			break;
		case R.id.setting_auto:
			showDialog(getString(R.string.exe_info));
			mForceMgr.lockNetwork(ForceNet.NET_AUTO);
			break;
		case R.id.setting_gsm:
			showDialog(getString(R.string.exe_info));
			mForceMgr.lockNetwork(ForceNet.NET_GSM);
			break;
		case R.id.setting_td:
			showDialog(getString(R.string.exe_info));
			mForceMgr.lockNetwork(ForceNet.NET_TDSCDMA);
			break;
		case R.id.setting_lte:
			showDialog(getString(R.string.exe_info));
			mForceMgr.lockNetwork(ForceNet.NET_TDD_LTE);
			break;
		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			if( (Boolean) msg.obj ){
				diaMessage.setText(getString(R.string.lock_succ));
			}else{
				diaMessage.setText(getString(R.string.lock_fail));
			}
			pb.setVisibility(View.GONE);
		};
	};
	
	private void showDialog(String content){
		BasicDialog.Builder builder = new BasicDialog.Builder(this);
		View view = inflater.inflate(R.layout.progress_dialog, null);
		diaMessage = (TextView)view.findViewById(R.id.msg);
		pb = (ProgressBar)view.findViewById(R.id.pb);
		diaMessage.setText(content);
		
		builder.setTitle(R.string.str_tip).setView(view);
    	builder.setPositiveButton(R.string.str_ok, null);
		
    	dialog = builder.create();
    	dialog.show();
	}
	
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private OnTaskChangeListener onTaskChangeListener = new OnTaskChangeListener(){
		/**
		 * @see com.dinglicom.dataset.ForceManager.OnTaskChangeListener#onFinished(boolean)
		 */
		@Override
		public void onFinished(boolean success) {
			handler.obtainMessage( 0, success).sendToTarget();
		};
	};

}
