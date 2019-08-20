package com.walktour.gui.locknet;


import android.R.anim;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;

/**
 * 工程机锁定页面
 * 
 * @author XieJihong
 * 
 */
public class LockDevMachine extends BasicActivity{
	private RelativeLayout network;
	private RelativeLayout band;
	private RelativeLayout point;
	private RelativeLayout cell;
	private RelativeLayout camp;
	private RelativeLayout release;
	public static final String LOCK_TYPE = "lock_type";
	public static final int LOCK_BAND_STATE = 0;
	public static final int LOCK_POINT_STATE = 1;
	public static final int LOCK_CELL_STATE = 2;
	LayoutInflater inflater;
	private BasicDialog dialog;
	private TextView diaMessage;
	private ProgressBar pb;
	private ForceManager mForceMgr = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lock_dev);

		inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		
		mForceMgr = ForceManager.getInstance();
		mForceMgr.init();
		
		(initTextView(R.id.title_txt)).setText(R.string.setting);
		findViewById(R.id.pointer).setOnClickListener(this);

		network = initRelativeLayout(R.id.setting_network_layout);
		band = initRelativeLayout(R.id.setting_band_layout);
		point = initRelativeLayout(R.id.setting_point_layout);
		cell = initRelativeLayout(R.id.setting_cell_layout);
		camp = initRelativeLayout(R.id.setting_camp_layout);
		release = initRelativeLayout(R.id.setting_release_layout);
		
		Deviceinfo.LockInfo info = Deviceinfo.getInstance().getLockInfo();
		network.setVisibility( info.hasLockNet()?View.VISIBLE:View.GONE );
		band.setVisibility( info.hasLockBand()?View.VISIBLE:View.GONE );
		point.setVisibility( info.hasLockFreq()?View.VISIBLE:View.GONE );
		cell.setVisibility( info.hasLockCell()?View.VISIBLE:View.GONE );
		camp.setVisibility( info.hasCampCell()?View.VISIBLE:View.GONE );

		network.setOnClickListener(this);
		band.setOnClickListener(this);
		point.setOnClickListener(this);
		cell.setOnClickListener(this);
		camp.setOnClickListener(this);
		release.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.setting_network_layout:
			intent = new Intent(LockDevMachine.this, LockNetWork.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_left, anim.slide_out_right);
			break;
		case R.id.setting_band_layout:
			intent = new Intent(LockDevMachine.this, LockBandwidth.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_left, anim.slide_out_right);
			break;
		case R.id.setting_point_layout:
			intent = new Intent(LockDevMachine.this, LockFrequency.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_left, anim.slide_out_right);
			break;
		case R.id.setting_cell_layout:
			intent = new Intent(LockDevMachine.this, LockCell.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_left, anim.slide_out_right);
			break;
		case R.id.setting_release_layout:
			mForceMgr.setOnTaskChangeListener( onTaskChangeListener );
			showDialog(getString(R.string.exe_info));
			mForceMgr.unlockAll(null);
			break;
		case R.id.pointer:
			this.finish();
		}
	}
	
	@SuppressLint("InflateParams")
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
		mForceMgr.release();
	}
	
	private OnTaskChangeListener onTaskChangeListener = new OnTaskChangeListener(){
		/**
		 * @see com.dinglicom.dataset.ForceManager.OnTaskChangeListener#onFinished(boolean)
		 */
		@Override
		public void onFinished(boolean success) {
			super.onFinished(success);
			if( success ){
				diaMessage.setText(getString(R.string.lock_succ));
			}else{
				diaMessage.setText(getString(R.string.lock_fail));
			}
			pb.setVisibility(View.GONE);
		}
	};

}
