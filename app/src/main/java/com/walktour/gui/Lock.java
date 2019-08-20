package com.walktour.gui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.BasicActivity;


/**
 * 锁定网络，频段的界面　
 * 此Activity发送广播到WalktourPhone进程，通知其实现具体功能
 * */
public class Lock extends BasicActivity{
	
	private final static String tag ="Walktour.Lock";
	private final static String GSM_WCDMA = "GSM,WCDMA";
	private final static String CDMA = "CDMA";
	
	
	//view 
	private Spinner spinnerNT;
	private Spinner spinnerBM;
	private TextView textPT;
	private TextView textNT;
/*	private TextView textBM;
	private TextView textZone;*/
	
	//TelePhone
	 private TelephonyManager telManager;
	 private static int  networkType ;
	 private static int bandMode ;
	 
	 
	
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.walktour_lock);
        findView();
        
        //此线程为了防止spinnerNT和spinnerBM初始化时触发OnItemSelectedListener
        Thread thread = new Thread( new ThreadPauser());
        thread.start();
	}
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	/**
	 * 生成界面
	 */
	private void findView()
	{
		spinnerNT = initSpinner(R.id.Spinner01);
		spinnerBM = initSpinner(R.id.Spinner02);
		
		
		textPT = initTextView(R.id.TextView06);
		textNT = initTextView(R.id.TextView03);
/*		textBM = initTextView(R.id.TextView04);
		textZone = initTextView(R.id.TextView05);*/
		
		//读取手机无线和网络信息
		initPhoneInfo();
		
		//Spinner networkType
		String[] netWorkType =
			GSM_WCDMA.equals( getPhoneTypeToString( ) )?
			getResources().getStringArray(R.array.lock_netWorkType_GSM):
			getResources().getStringArray(R.array.lock_netWorkType_CDMA);
		
		ArrayAdapter<String> adptNT = new ArrayAdapter<String>(Lock.this,
	            android.R.layout.simple_spinner_item, netWorkType );
		adptNT.setDropDownViewResource(R.layout.spinner_dropdown_item);
		spinnerNT.setEnabled(false);
		spinnerNT.setAdapter(adptNT);
		spinnerNT.setOnItemSelectedListener(netWorkSelcetListener);
		spinnerNT.setSelection(networkType);
		
		//Spinner networkBandMode
		String[] netWorkBM = 
			GSM_WCDMA.equals( getPhoneTypeToString( ) )?
			getResources().getStringArray(R.array.lock_bandMode_gsm):
			getResources().getStringArray(R.array.lock_bandMode_cdma)	;
		ArrayAdapter<String> adptBM = new ArrayAdapter<String>(Lock.this,
	            android.R.layout.simple_spinner_item, netWorkBM );
		adptBM.setDropDownViewResource(R.layout.spinner_dropdown_item);
		spinnerBM.setEnabled(false);
		spinnerBM.setAdapter(adptBM);
		spinnerBM.setOnItemSelectedListener( bandModeSelectedListener );
		spinnerBM.setSelection(bandMode);
	}
	
	//netWorkSelcetListener
	private OnItemSelectedListener netWorkSelcetListener = new OnItemSelectedListener()
	{
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			
			if(spinnerNT.isEnabled())
			{
				networkType = arg2;
				
				//发送广播通知WalktourPhone程序切换网络
				Intent intent = new Intent();
				intent.setAction(WalkMessage.SET_NETWORKTYPE_ACTION);
				
				int t =0;
				//如果是GSM_WCDMA手机
				if(GSM_WCDMA.equals( getPhoneTypeToString()) ){
					t =  arg2;
				}
				//如果是CDMA手机
				if(CDMA.equals( getPhoneTypeToString() )){
					t = arg2+4;
				}
				intent.putExtra(WalkMessage.NETWORKTYPE_KEY, t );
				LogUtil.w(tag, "send broadcast to PhoneService , network type:"+t);
				sendBroadcast(intent);
				
			}
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			
		}
	};
	
	/**
	 * bandModeSelectListener
	 * 请参考源代码：hardware/ril/include/telephony/ril.h　搜索RIL_REQUEST_SET_BAND_MODE　
	 * 并结合http://wireless.agilent.com/rfcomms/refdocs/1xevdo/1xevdo_gen_bse_cell_band.php
	 */
	private OnItemSelectedListener bandModeSelectedListener = new OnItemSelectedListener()
	{
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			
			if(arg2<0 || arg2>4){
				return;
			}
			
			int networkType =0; ;
			switch(arg2)
			{
			case 0:networkType=0;break;
			case 1:networkType=14;break;
			case 2:networkType=13;break;
			case 3:networkType=7;break;
			}
			
			if(spinnerBM.isEnabled())
			{
				
				//发送广播通知WalktourPhone程序切换网络
				Intent intent = new Intent();
				intent.setAction(WalkMessage.BANDMODE_ACTION);
				intent.putExtra(WalkMessage.BANDMODE_KEY, networkType);
				LogUtil.w(tag, "send broadcast to PhoneService, band mode:"+networkType );
				sendBroadcast(intent);
				
				bandMode= arg2;
			}
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};
	
	//读取手机无线和网络信息
	private void initPhoneInfo()
	{
        telManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        telManager.listen(new ServiceStateListener(), PhoneStateListener.LISTEN_SERVICE_STATE );
        textPT.setText(this.getPhoneTypeToString() );
        textNT.setText(this.getNetWorkType() );
	}
	
	/**
	 * 获取网络类型:
	 * 　  NETWORK_TYPE_UNKNOWN
		NETWORK_TYPE_GPRS
		NETWORK_TYPE_EDGE
		NETWORK_TYPE_UMTS
		NETWORK_TYPE_HSDPA
		NETWORK_TYPE_HSUPA
		NETWORK_TYPE_HSPA
		NETWORK_TYPE_CDMA
		NETWORK_TYPE_EVDO_0
		NETWORK_TYPE_EVDO_A
		NETWORK_TYPE_1xRTT
	 * */
	private String getNetWorkType()
	{
		switch(telManager.getNetworkType())
		{
			case TelephonyManager.NETWORK_TYPE_UNKNOWN:return "UNKONWN";
			case TelephonyManager.NETWORK_TYPE_CDMA:return "CMMA";
			case TelephonyManager.NETWORK_TYPE_EDGE:return "EDGE";
			case TelephonyManager.NETWORK_TYPE_EVDO_0:return "EVDO_O";
			case TelephonyManager.NETWORK_TYPE_EVDO_A:return "EVDO_A";
			case TelephonyManager.NETWORK_TYPE_GPRS:return "GPRS";
			case TelephonyManager.NETWORK_TYPE_HSDPA:return "HSDPA";
			case TelephonyManager.NETWORK_TYPE_HSPA:return "HSPA";
			case TelephonyManager.NETWORK_TYPE_HSUPA:return "HSUPA";
			case TelephonyManager.NETWORK_TYPE_UMTS:return "UMTS";
			default :return "null";
		}
	}
	
	/**
	 * 获取网络制式：GSM/CMDA/WCDMA
	 * */
	private String getPhoneTypeToString(){
		switch(telManager.getPhoneType() )
		{
		case TelephonyManager.PHONE_TYPE_NONE:
			return "UNKONWN";
		case TelephonyManager.PHONE_TYPE_GSM:
			return GSM_WCDMA;
		case TelephonyManager.PHONE_TYPE_CDMA:
			return CDMA;
		default :return "null";
		}
	}
	
	/**
	 * 网络状态监听器
	 * */
	private class ServiceStateListener extends PhoneStateListener{
		@Override
		/**手机信号改变回调函数*/
		public void onServiceStateChanged(ServiceState serviceState){
			textNT.setText( getNetWorkType() );
		}
	}
	
	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			spinnerNT.setEnabled(true);
			spinnerBM.setEnabled(true);
		}
	};
	
	private class ThreadPauser implements Runnable
	{
		@Override
		public void run() {
			try {
				Thread.sleep(1000);
				Message msg = handler.obtainMessage();
				msg.sendToTarget();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**hardware/ril/include/telephony/ril.h
	 * RIL_REQUEST_SET_PREFERRED_NETWORK_TYPE
	 *
	 * Requests to set the preferred network type for searching and registering
	 * (CS/PS domain, RAT, and operation mode)
	 *
	 * "data" is int *
	 *
	 * ((int *)data)[0] is == 0 for GSM/WCDMA (WCDMA preferred)
	 * ((int *)data)[0] is == 1 for GSM only
	 * ((int *)data)[0] is == 2 for WCDMA only
	 * ((int *)data)[0] is == 3 for GSM/WCDMA (auto mode, according to PRL)
	 * ((int *)data)[0] is == 4 for CDMA and EvDo (auto mode, according to PRL)
	 * ((int *)data)[0] is == 5 for CDMA only
	 * ((int *)data)[0] is == 6 for EvDo only
	 * ((int *)data)[0] is == 7 for GSM/WCDMA, CDMA, and EvDo (auto mode, according to PRL)
	 *
	 * "response" is NULL
	 *
	 * Valid errors:
	 *  SUCCESS
	 *  RADIO_NOT_AVAILABLE (radio resetting)
	 *  GENERIC_FAILURE
	 *  MODE_NOT_SUPPORTED
	 */
	
	/**hardware/ril/include/telephony/ril.h
	 * RIL_REQUEST_SET_BAND_MODE
	 *
	 * Assign a specified band for RF configuration.
	 *
	 * "data" is int *
	 * ((int *)data)[0] is == 0 for "unspecified" (selected by baseband automatically)
	 * ((int *)data)[0] is == 1 for "EURO band" (GSM-900 / DCS-1800 / WCDMA-IMT-2000)
	 * ((int *)data)[0] is == 2 for "US band" (GSM-850 / PCS-1900 / WCDMA-850 / WCDMA-PCS-1900)
	 * ((int *)data)[0] is == 3 for "JPN band" (WCDMA-800 / WCDMA-IMT-2000)
	 * ((int *)data)[0] is == 4 for "AUS band" (GSM-900 / DCS-1800 / WCDMA-850 / WCDMA-IMT-2000)
	 * ((int *)data)[0] is == 5 for "AUS band 2" (GSM-900 / DCS-1800 / WCDMA-850)
	 * ((int *)data)[0] is == 6 for "Cellular (800-MHz Band)"
	 * ((int *)data)[0] is == 7 for "PCS (1900-MHz Band)"
	 * ((int *)data)[0] is == 8 for "Band Class 3 (JTACS Band)"
	 * ((int *)data)[0] is == 9 for "Band Class 4 (Korean PCS Band)"
	 * ((int *)data)[0] is == 10 for "Band Class 5 (450-MHz Band)"
	 * ((int *)data)[0] is == 11 for "Band Class 6 (2-GMHz IMT2000 Band)"
	 * ((int *)data)[0] is == 12 for "Band Class 7 (Upper 700-MHz Band)"
	 * ((int *)data)[0] is == 13 for "Band Class 8 (1800-MHz Band)"
	 * ((int *)data)[0] is == 14 for "Band Class 9 (900-MHz Band)"
	 * ((int *)data)[0] is == 15 for "Band Class 10 (Secondary 800-MHz Band)"
	 * ((int *)data)[0] is == 16 for "Band Class 11 (400-MHz European PAMR Band)"
	 * ((int *)data)[0] is == 17 for "Band Class 15 (AWS Band)"
	 * ((int *)data)[0] is == 18 for "Band Class 16 (US 2.5-GHz Band)"
	 * 
	 *更多关于频率请参考
	 *http://wireless.agilent.com/rfcomms/refdocs/1xevdo/1xevdo_gen_bse_cell_band.php
	 *
	 * "response" is NULL
	 *
	 * Valid errors:
	 *  SUCCESS
	 *  RADIO_NOT_AVAILABLE
	 *  GENERIC_FAILURE
	 */
}