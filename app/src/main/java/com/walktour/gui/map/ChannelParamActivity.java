package com.walktour.gui.map;

import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;

import com.dingli.wlan.apscan.ScannerObserver;
import com.dingli.wlan.apscan.WifiScanner;
import com.walktour.Utils.ApplicationModel;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;


/**
 * AP信道参数界面
 * 修改记录：添加Timer定时器，定时访问WifiTools类，获取信道质量参数。郑磊修改于2012年04月11日
 */
public class ChannelParamActivity extends BasicActivity implements ScannerObserver {
	private static final String TAG = "ChannelParamActivity";
	private float DENSITY;
	private View channelView;
	private ChannelParamView paramView;
	
	private Handler mHandler=new Handler();
	private ApplicationModel appModel = ApplicationModel.getInstance();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//获取屏幕密度，并传递给View
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
		DENSITY = localDisplayMetrics.density;
		ChannelParamView.setDensity(DENSITY);
		channelView = View.inflate(this, R.layout.apscan_channelparam_ui, null);
		setContentView(channelView);
		WifiScanner.instance(this).registerObserver(this);

		//具体负责画图的View
		paramView = (ChannelParamView)findViewById(R.id.channelContentView);
		
	}
	
	
	@Override
	public void onGetScanResult() {
//		android.util.Log.i(TAG, "onGetScanResult in ChannelParamActivity");
		
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				if(!appModel.isFreezeScreen()) {
//					WifiTools.WIFI_MANAGER.startScan();
//					WifiTools.WIFI_AP_LIST = WifiTools.WIFI_MANAGER.getScanResults();
//					if(WifiTools.WIFI_AP_LIST != null){
//						Collections.sort(WifiTools.WIFI_AP_LIST, new ScanResultComparator(ScanResultComparator.LEVEL,ScanResultComparator.SORT_DESC));
//					}
					channelView.invalidate();
				}
			}});
	
	}
	@Override
	public void onGetWifiStatus(int status) {
		
	}
	
	/**
	 * 从其它TAB页返回此页时，重新注册Broadcast Receiver
	 * **/
	@Override
	protected void onResume() {
		super.onResume();
		paramView.invalidate();

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		WifiTools.removeScannerObsrver(this);
	}

}
