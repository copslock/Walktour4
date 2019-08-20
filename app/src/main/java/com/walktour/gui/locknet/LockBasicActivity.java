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

import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.locknet.ForceControler.ForceNet;

/**
 * LockBasicActivity
 * 锁定小区，锁定频点，锁定频段页面都继承此页面
 * 2013-11-6 下午4:30:12
 * 
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
@SuppressLint("InflateParams")
public abstract class LockBasicActivity extends BasicActivity {
	
	protected RelativeLayout layout2G;
	protected RelativeLayout layout3G;
	protected RelativeLayout layoutLte;
	protected RelativeLayout layoutCurrent;
	protected TextView txt2G;
	protected TextView txt3G;
	protected TextView txtLte;
	protected TextView txtCurrent;
	protected LayoutInflater inflater;
	
	//progress Dialog
	protected BasicDialog dialog;
	protected TextView diaMessage;
	protected ProgressBar pb;
	
	protected ForceManager mForceMgr = null;
	
	protected ForceNet net2G = ForceNet.NET_GSM;
	protected ForceNet net3G = ForceNet.NET_TDSCDMA;
	protected ForceNet net4G = ForceNet.NET_TDD_LTE;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mForceMgr = ForceManager.getInstance();
		mForceMgr.init();
		findView();
	}
	
	protected void findView(){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lock_dev_band);
		
		//title
		(initTextView(R.id.title_txt)).setText(  titleStringId() );
        findViewById(R.id.pointer).setOnClickListener(this);
        
        //layout
        layout2G = initRelativeLayout(R.id.setting_layout_2g);
        layout3G = initRelativeLayout(R.id.setting_layout_3G);
        layoutLte = initRelativeLayout(R.id.setting_layout_lte);
        layoutCurrent = initRelativeLayout(R.id.setting_layout_current);
        Deviceinfo.LockInfo info = Deviceinfo.getInstance().getLockInfo();
        layoutCurrent.setVisibility( info.hasLockCurrentCell()?View.VISIBLE:View.GONE );
        
        layout2G.setOnClickListener( this );
        layout3G.setOnClickListener( this );
        layoutLte.setOnClickListener( this );
        layoutCurrent.setOnClickListener( this );
        
        //TextView
        txt2G = initTextView( R.id.txt_2g );
        txt3G = initTextView( R.id.txt_3g );
        txtLte = initTextView( R.id.txt_lte );
        txtCurrent = initTextView( R.id.txt_current );
        
        int netType = Deviceinfo.getInstance().getNettype();
		if( netType == Deviceinfo.NETTYPE_TDD_LTE ){
			txt2G.setText("GSM");
			txt3G.setText("TD-SCDMA");
			txtLte.setText("TDD-LTE");
			net2G = ForceNet.NET_GSM;
			net3G = ForceNet.NET_TDSCDMA;
			net4G = ForceNet.NET_TDD_LTE;
		}else if( netType == Deviceinfo.NETTYPE_FDD_LTE ){
			txt2G.setText("GSM");
			txt3G.setText("WCDMA");
			txtLte.setText("FDD-LTE");
			net2G = ForceNet.NET_GSM;
			net3G = ForceNet.NET_WCDMA;
			net4G = ForceNet.NET_FDD_LTE;
		}
		
		setViewByDeviceInfo();
	}
	
	protected abstract int titleStringId();
	
	/**
	 * 根据DeviceInfo配置显示部分View可见
	 * setViewByDeviceInfo
	 * 函数功能：
	 */
	protected abstract void setViewByDeviceInfo();
	/**
	 * showLockDialog
	 * 函数功能：
	 */
	protected abstract void showLockDialog( int layoutViewId );
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pointer:
			this.finish();

		default:
			mForceMgr.setOnTaskChangeListener( onTaskChangeListener );
			showLockDialog( v.getId() );
			break;
		}
	}
	
	protected void showProgressDialog(String content){
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
	
	@SuppressLint("HandlerLeak")
	protected Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			//查询的消息也会从这里返回
			if( diaMessage !=null && pb!=null && msg!=null) {
				if( (Boolean) msg.obj ){
					diaMessage.setText(getString(R.string.lock_succ));
				}else{
					diaMessage.setText(getString(R.string.lock_fail));
				}
				pb.setVisibility(View.GONE);
			}
		};
	};
	
	
	private OnTaskChangeListener onTaskChangeListener = new OnTaskChangeListener(){
		/**
		 * @see com.dinglicom.dataset.ForceManager.OnTaskChangeListener#onFinished(boolean)
		 */
		@Override
		public void onFinished(boolean success) {
			super.onFinished(success);
			handler.obtainMessage( 0, success).sendToTarget();
		}
	};
}
