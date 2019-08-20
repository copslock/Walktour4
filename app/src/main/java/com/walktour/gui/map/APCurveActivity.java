package com.walktour.gui.map;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;

import com.dingli.wlan.apscan.ScannerObserver;
import com.dingli.wlan.apscan.WifiScanner;
import com.walktour.Utils.ApplicationModel;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.gui.R;

import java.util.List;
 
/**
 * AP信号强度-时间曲线的Activity
 * 
 */
public class APCurveActivity extends BasicActivity implements ScannerObserver {
	private static final String TAG = "APCurveActivity";
	private float DENSITY;
	private View apView;
	private BasicSpinner spinner;
	private ArrayAdapter<String> adapter;
	private List<String> apName; 
	
	private int peroidCount = 1;//下拉菜单计时用
	private final int TIMEPERIOD = 3;//下拉菜单更新周期
	public static String spinnerSelectName = "";//记录spinner控件选择项目的名称
	public static int spinnerSelectId = 0; 
	private Handler mHandler=new Handler();
	private ApplicationModel appModel = ApplicationModel.getInstance();
	private boolean isRefreshRightNow = true;
	private WifiScanner scanner= WifiScanner.instance(this);
	
	@Override 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("CurveActivity", "onCreate");
		//获取屏幕密度，并传递给View
		APCurveView.allname = scanner.getAPNamesClone();
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
		APCurveView.setDensity(localDisplayMetrics.density);
		//设置主View
		apView = View.inflate(this.getParent().getParent(), R.layout.apscan_curve_ui, null);
		setContentView(apView);
		scanner.registerObserver(this);
		
		//为Spinner设置数据源BasicSpinner
		spinner = (BasicSpinner)findViewById(R.id.aplist_spinner);
		apName = scanner.getAPNamesClone();
		adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,apName);
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(
				new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						//指定要加粗显示的AP的名称
								APCurveView.setSelectAP(apName.get(arg2));
								spinnerSelectName = apName.get(arg2);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});
		
	}
	
	@Override
	public void onGetScanResult() {
//		Log.i(TAG, "onGetScanResult in APCurveActivity");
		
		mHandler.post(new Runnable(){
			@Override
			public void run() {

				
				//计时超过周期，归一
				if(peroidCount > TIMEPERIOD) {
					peroidCount = 1;
				}
				
				//每隔TIMEPERIOD时间更新一次下拉列表
				if(peroidCount % TIMEPERIOD == 0 || isRefreshRightNow) {
					apName = scanner.getAPNamesClone();
					APCurveView.allname = apName;
					adapter = new ArrayAdapter<String>(APCurveActivity.this,R.layout.simple_spinner_custom_layout,apName);
					adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
					spinner.setAdapter(adapter);
					boolean stillExsited = false;
					if(!spinnerSelectName.equals("")) {
						for(int i = 0;i < apName.size();i++) {
							if(apName.get(i).equals(spinnerSelectName)) {
								spinner.setSelection(i);
								stillExsited = true;
							}
						}
					}
					if(!stillExsited) {
						spinner.setSelection(0);
					}
					Log.e("CurveActivity", "CurveSpinner Update");
					isRefreshRightNow = false;
				}
				//APCurvView在画图时需要获得Spinner实际的高度，在此传入
				APCurveView.setSpinnerHeight(spinner.getHeight());
				if(!appModel.isFreezeScreen()) {
					apView.invalidate();
				}
				peroidCount ++;	
			}});
		
		
	
	}
	@Override
	public void onGetWifiStatus(int status) {
		final int wifiStatus = status;
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				if (wifiStatus == WifiManager.WIFI_STATE_DISABLED) {
					Log.i(TAG, "wifi disable");
					if(apName != null) {
						apName.clear();
						adapter.notifyDataSetChanged();
					}
				}
			}});
	}
	

	/**
	 * 从另一个TAB页切换到本页的时候重新注册Broadcast Receiver
	 * **/
	@Override
	protected void onResume() {
		super.onResume();
		Log.e("CurveActivity", "onResume");
		
		apName = scanner.getAPNamesClone();
		APCurveView.allname = apName;
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,apName);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
		APCurveView.setSpinnerHeight(spinner.getHeight());
		apView.invalidate();
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		WifiTools.removeScannerObsrver(this);
	}
}
