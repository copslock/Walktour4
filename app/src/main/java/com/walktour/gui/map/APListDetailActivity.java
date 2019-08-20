package com.walktour.gui.map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.dingli.wlan.apscan.APInfoModel;
import com.dingli.wlan.apscan.ScannerObserver;
import com.dingli.wlan.apscan.WifiScanner;
import com.dingli.wlan.apscan.WifiTools;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * AP扫描结果具体项显示界面
 *
 */
public class APListDetailActivity extends BasicActivity implements ScannerObserver {
	public static final String TAG = "APListDetailActivity";
//	private View contentView;
//	public static String ssid = "";
//	public static String bssid = "";
//	public static String rssi = "";
//	public static String authMode = "";
//	public static String channel = "";
//	public static String mode = "";
////	private CastReceiver castReceiver;
	public static int screen_width = 0;
	public static int screen_height = 0;
	public static APInfoModel apInfo;
//	private ScanPlus scan;
	private TextView txt_bssid;
	private TextView txt_ssid;
	private TextView txt_rssi;
//	private TextView txt_auth;
	private TextView txt_encrypt;
	private TextView txt_snr;
	private TextView txt_supportProtocol;
	private TextView txt_noise;
	private TextView txt_beacon;
	private TextView txt_supportRate;
//	private TextView txt_staList;
	private TextView txt_signalSchwankung;
	private TextView txt_mode;
//	private TextView txt_maxRate;
//	private TextView txt_activeTimeFirst;
//	private TextView txt_activeTimeLast;
	private TextView txt_activeTime;
	
	// 添加TableLayout
//	private TableLayout tableLayoutSta;
	private TimerTask timerTask;
	private Timer timer;
	private static final int UPDATE_TEXTVIEW = 0x01;
	private int m_lastRssi = -76;
	private String m_bssid;
	public static final String EXTRA_BSSID = "BSSID";
	// 是否显示当前已经连接的AP的信息
	private boolean isConnectedApInfo = false;
	public static final String EXTRA_IS_CONNECTED_AP_INFO = "IsConnectedApInfo";
	private WifiScanner scanner= WifiScanner.instance(this);
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_TEXTVIEW:
				setContent();
				break;

			default:
				break;
			}
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apinfo_detail_ui);
		
		Intent intent = getIntent();
		isConnectedApInfo = intent.getBooleanExtra(EXTRA_IS_CONNECTED_AP_INFO, false);
		Log.e(TAG, "isConnectedApInfo:" + isConnectedApInfo);
		String bssid = intent.getStringExtra(EXTRA_BSSID);
		m_bssid=bssid;
		
		findView();
		setContent();
		
		timerTask = new TimerTask() {
			
			@Override
			public void run() {
				// 如果是显示当前已经连接的AP的详细信息，那么就是要查看做任务时AP的属性，就要定时获取当前连接的AP的bssid
				if(isConnectedApInfo) {
					m_bssid = WifiTools.getCurrentBssid();
				}
				Message msg = mHandler.obtainMessage(UPDATE_TEXTVIEW);
				mHandler.sendMessage(msg);
				
			}
		};
		timer = new Timer();
		timer.schedule(timerTask, 0, 2000);
		scanner.registerObserver(this);
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		timer.cancel();
//		unregisterReceiver(castReceiver);
	}
	
	private void findView() {
		 txt_bssid = initTextView(R.id.txt_bssid);
		 txt_ssid = initTextView(R.id.txt_ssid);
		 txt_rssi = initTextView(R.id.txt_rssi);
		 txt_encrypt = initTextView(R.id.txt_encrypt);
		 txt_signalSchwankung = initTextView(R.id.txt_signalSchwankung);
		// txt_auth = initTextView(R.id.txt_auth);
		 txt_snr = initTextView(R.id.txt_snr);
		 txt_supportProtocol = initTextView(R.id.txt_supportprotocol);
		 txt_noise = initTextView(R.id.txt_noise);
		 txt_beacon = initTextView(R.id.txt_beacon);
		 txt_supportRate = initTextView(R.id.txt_supportrate);
		 txt_mode= initTextView(R.id.txt_network_mode);
//		 txt_activeTimeFirst = initTextView(R.id.txt_active_time_first);
//		 txt_activeTimeLast = initTextView(R.id.txt_active_time_last);
		 txt_activeTime= initTextView(R.id.txt_active_time);
//		 txt_staList = initTextView(R.id.txt_stalist);
//		 tableLayoutSta = (TableLayout)findViewById(R.id.table_sta);
	}
	private void setContent() {
		List<APInfoModel> aplist = WifiScanner.instance(this).getApList();
		//APInfoModel ap =null;
		boolean founded=false;
		for (int i=0;i< aplist.size();i++) {
			apInfo = aplist.get(i);
			if (apInfo.bssid.equals(m_bssid)){
				founded=true;
				break;
			}
		}
		if(apInfo != null && founded) {
			apInfo.signalSchwankung = apInfo.rssi - m_lastRssi;
			m_lastRssi = apInfo.rssi;
			txt_bssid.setText(apInfo.bssid);
			txt_ssid.setText(apInfo.ssid); 
			txt_rssi.setText(String.valueOf(apInfo.rssi)+"dbm");
			txt_signalSchwankung.setText(""+apInfo.signalSchwankung);
			//txt_auth.setText(apInfo.auth);
			txt_encrypt.setText(apInfo.encryptionType);
			txt_snr.setText(apInfo.snr);
			txt_supportProtocol.setText(apInfo.supportedProtocol);
			txt_noise.setText(apInfo.noise + "dbm");
			txt_beacon.setText(apInfo.beaconPeriod + "ms");
			txt_mode.setText(apInfo.mode);
			txt_supportRate.setText(apInfo.rates);
			Long activeTimeFirst = WifiTools.m_firstDetectedTimes.get(apInfo.bssid);
			Long activeTimeLast = WifiTools.m_lastDetectedTimes.get(apInfo.bssid);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm:ss",Locale.getDefault());
			long first = System.currentTimeMillis();
			long last = first;
			if(activeTimeFirst != null) {
				first = activeTimeFirst.longValue();
			}
			if(activeTimeLast != null) {
				last = activeTimeLast.longValue();
			}
//			txt_activeTimeFirst.setText("First: " + sdf.format(new Date(first)));
//			txt_activeTimeLast.setText("Last: " + sdf.format(new Date(last)));
			txt_activeTime.setText("First: " + sdf.format(new Date(first)) + "\n" + "Last: " + sdf.format(new Date(last)));
			//txt_staList.setText(stalist);
		}
		
	}

	@Override
	public void onGetScanResult() {
		
	}

	@Override
	public void onGetWifiStatus(int status) {
		final int wifiStatus = status;
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				if (wifiStatus == WifiManager.WIFI_STATE_ENABLED) {
					setContent();
				}
				else if (wifiStatus == WifiManager.WIFI_STATE_DISABLED) {
					Log.i(TAG, "wifi disable");
					reset();
				}
			}});
	}
	
	private void reset() {
		txt_bssid.setText("");
		txt_ssid.setText(""); 
		txt_rssi.setText("");
		txt_signalSchwankung.setText("");
		//txt_auth.setText("");
		txt_encrypt.setText("");
		txt_snr.setText("");
		txt_supportProtocol.setText("");
		txt_noise.setText("");
		txt_beacon.setText("");
		txt_mode.setText("");
		txt_supportRate.setText("");
		txt_activeTime.setText("");
		//txt_staList.setText("");
	}
	
}
