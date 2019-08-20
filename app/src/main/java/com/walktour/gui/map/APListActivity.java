package com.walktour.gui.map;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.dingli.wlan.apscan.APInfoModel;
import com.dingli.wlan.apscan.ScannerObserver;
import com.dingli.wlan.apscan.WifiScanner;
import com.dingli.wlan.apscan.WifiTools;
import com.walktour.Utils.ApplicationModel;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;

/**
 * AP扫描页
 */
public class APListActivity extends BasicActivity implements OnItemClickListener,ScannerObserver{
	private final String TAG = "APListActivity";
	private ListView listView;
	private TextView wifiStateAler;
	private APListAdapter localAdapter;
	private TextView apNumbers;//显示AP、adHoc、STA数量
	private Handler mHandler=new Handler();
	private WifiScanner scanner= WifiScanner.instance(this);
	private ApplicationModel appModel = ApplicationModel.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apscan_list_ui);
		//为listview分配Adapter
		localAdapter = new APListAdapter(this);
		listView = (ListView)findViewById(R.id.ap_listview);
		listView.setAdapter(localAdapter);

		listView.setOnItemClickListener(this);
		wifiStateAler = initTextView(R.id.wifi_state_alert);
//		Log.i(TAG, "WifiTools.registerScannerObsrver in APListActivity");
		scanner.registerObserver(this);
		if (scanner.getWifiState() == WifiManager.WIFI_STATE_DISABLED && !WifiTools.is9002()) {
			onGetWifiStatus( WifiManager.WIFI_STATE_DISABLED);
		}
		apNumbers = initTextView(R.id.apscan_ui_number_txt);
		apNumbers.setText("AP:0 "/* + "adHoc:0 " + "STA:0 "*/);
	}
	
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 900:
				updateView();
				break;

			default:
				break;
			}
		};
	};
	
	
	
	private synchronized  void updateView() {
		if(!appModel.isFreezeScreen()) {
//			Log.i(TAG, "updateView...");
			//设置listview数据源
			//localAdapter.setAPList(scanner.getApList());

			//更新ListView时，加入的特殊处理
			localAdapter.notifyDataSetChanged();//通知ListView数据源改变
			Log.e("APlistActivity", "listView update");
			showAPNumber();
		}
	}
	
	private void showAPNumber() {
		int apSize = scanner.getApList()!=null?scanner.getApList().size():0;
		int adhocSize = 0;
		if (scanner.getApList() != null) {
			for (int i=0;i<scanner.getApList().size();i++) {
				APInfoModel model = scanner.getApList().get(i);
				if (model.mode == "Adhoc") {
					apSize --;
					adhocSize ++;
				}
			}
		}
		int staSize = 0;//WifiTools.staInfolist!=null?WifiTools.staInfolist.size():0;
		apNumbers.setText("AP:" + apSize/* +  "   adHoc:" + adhocSize+  "  STA:" + staSize*/);
	}
	@Override
	public void onGetScanResult() {
//		Log.i(TAG, "onGetScanResult in APListActivity");
		if (appModel.isFreezeScreen())
			return;
		Log.i(TAG, "listview  sssssssssssssssssssssssssssssss");
//		handler.sendEmptyMessage(900);
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateView();
			}
		});
	}
	
	@Override
	public void onGetWifiStatus(int status) {
		final int wifiStatus = status;
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				if (wifiStatus == WifiManager.WIFI_STATE_ENABLED) {
					wifiStateAler.setText("");
					wifiStateAler.setOnClickListener(null);
				}
				else if (wifiStatus == WifiManager.WIFI_STATE_DISABLED) {
					//Log.d("Scan","Show Prompt");
					wifiStateAler.setText(getResources().getString(R.string.wifi_disabled));
					wifiStateAler.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
//							Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
//							startActivity(intent);
							WifiTools.wifiManager.setWifiEnabled(true);
							wifiStateAler.setText(getResources().getString(R.string.wifi_enable_alert));
						}
					});
				}
			}});
	
	}
	
//	@Override
//	protected void onDestroy() {
//		Log.i(TAG, "onDestroy in APListActivity");
//		super.onDestroy();
//		//第一个页在销毁的时候，恢复其它也的初始参数
//		scanner.removeObserver(this);
//	}
//	
	
	
	
	
	
	
	

	@Override
	public void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		//第一个页在销毁的时候，恢复其它也的初始参数
				scanner.removeObserver(this);
	}

	/**
	 * ListView Item被点击后的处理事件
	 * **/
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		APInfoModel localResult = (APInfoModel)arg0.getItemAtPosition(arg2);
		Intent intent = new Intent(this,APListDetailActivity.class);
		intent.putExtra("SSID", localResult.ssid);
		intent.putExtra("BSSID", localResult.bssid);
		intent.putExtra("RSSI", String.valueOf(localResult.rssi));
		intent.putExtra("AUTHMODE",WifiTools.getScanResultSecurity(localResult.encryptionType));
		intent.putExtra("CHANNEL", String.valueOf(WifiTools.getChannel(localResult.frequency)));
		startActivity(intent);
	}

}
