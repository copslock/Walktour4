package com.dingli.wlan.apscan;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.base.util.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/*
 * LFJ 2012/3/23提供Wifi状态的监测，扫描，结果获取等
 */
public final class WifiScanner {
	private final String tag = "WifiScanner";
	private IntentFilter wifiFilter;
	private BroadcastReceiver wifiReceiver;
	private WifiManager wifiManager;
	private Context  context;
	
	private int wifiStatus=1;
	final static int   SCAN_PERIOD = 2000;  //扫描周期
	private List<ScanResult> 	scanResult;
	private Vector<ScannerObserver> observerList;
	private boolean isStop=false;
	private ScanThread  scanThread=null;
	private boolean needScan=false;
	private int 	scanStartTimes = 0;

	//private ScanPlus scanPlus; 
	private long updateTime = System.currentTimeMillis();
	private static WifiScanner scanner = null;
	
	//主要用来保存wifi扫描信息，给些文件使用
	private Map<String,APInfoModel> wifiApMap= new ConcurrentHashMap<String,APInfoModel>();
	
	//给显示使用
	private List<APInfoModel> wifiAPlist = new ArrayList<APInfoModel>();
	
	public static WifiScanner instance(Context context) {
		if (scanner == null) {
			scanner = new WifiScanner(context.getApplicationContext());
			LogUtil.i("WifiScanner", "1Create WifiScanner instance" + scanner );
			return scanner;
		}
		LogUtil.i("WifiScanner", "2Create WifiScanner instance" + scanner );
		return scanner;
	}
	
	private WifiScanner (Context context) {
		
		this.context = context;
		wifiFilter = new IntentFilter();
		wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		wifiFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		wifiFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
		wifiFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		wifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		wifiFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		wifiReceiver = new BroadcastReceiver() {
	            @Override
	            public void onReceive(Context context, Intent intent) {
	            	if((System.currentTimeMillis()-updateTime)>1000){
	            		updateTime = System.currentTimeMillis();
	            		handleEvent(intent);
	            	}
	            }
	        };
	
	    context.registerReceiver(wifiReceiver,wifiFilter); 
	    WifiTools.wifiManager  = wifiManager;
	    WifiTools.mContext = context;
	    //WifiTools.wifiScanner = this;
	    isStop=false;
	   // scanPlus = new ScanPlus();
	    observerList = new Vector<ScannerObserver>();
//	    ScanThread  thread = new ScanThread();
//	    thread.start();
//	    if (wifiManager.getWifiState() == wifiManager.WIFI_STATE_ENABLED) {
//	    	wifiStatus = WIFI_ENABLE;
//	    }
//	    else if (wifiManager.getWifiState() == wifiManager.WIFI_STATE_DISABLED) {
//	    	wifiStatus = WIFI_DISABLE;
//	    }
	    Log.e(tag,"WifiScanner has register");
	}
	
    private static boolean isScannerWifi = false; //标志是否扫描WIFI
    
    public static boolean isScannerWifi() {
		return isScannerWifi;
	}

	public static void setScannerWifi(boolean scanning) {
		isScannerWifi = scanning;
	}

	public List<ScanResult> getScanResult() {
		return scanResult;
	}
	public synchronized void registerObserver(ScannerObserver observer) {
		synchronized(observerList) {
			observerList.add(observer);
		}
	}
	public synchronized void removeObserver(ScannerObserver observer) {
		observerList.remove(observer);
		Log.d("Scan","object remove");
	}
	public boolean isWifiEnable() {
		return wifiStatus == WifiManager.WIFI_STATE_ENABLED;
	}
	public int getWifiState() {
		return wifiStatus;
	}
	
	/*
	 * 提供给外部使用
	 */
	public Map<String,APInfoModel> getApMap() {
		return wifiApMap;
	}
	
	public List<APInfoModel> getApList() {
		return wifiAPlist;
	}
	
	public  synchronized ArrayList<String> getAPNamesClone(){
		ArrayList<String> apList = new ArrayList<String>();
		if(wifiAPlist == null){
			return apList;
		}
		for (APInfoModel sr : wifiAPlist) {
			String ssid = sr.ssid;
			String bssid = sr.bssid;
			apList.add(ssid+"("+bssid+")");
		}
		return apList;
	} 
//	/*
//	 * 程序退出时候调用
//	 */
//	public void stopScan () {
//		isStop=true;
//		context.unregisterReceiver(wifiReceiver);
//	}
	synchronized private void fillApList() {
		try {
		wifiApMap.clear();
		wifiAPlist.clear();
		if (scanResult == null)
			return;
		for (int i=0;i<scanResult.size();i++) {
			APInfoModel model = new APInfoModel();
			ScanResult result = scanResult.get(i);
			model.bssid = result.BSSID;
			model.ssid = result.SSID;
			model.encryptionType = result.capabilities;
			model.frequency = result.frequency;
			model.rssi = result.level;
			//model.mode = result.
			model.beaconPeriod = "100";
			model.noise = "-92";
			model.supportedProtocol = "802.11 b/g/n";
			model.rates ="1M,2M,5.5M,6M,9M,11M,18M,24M,36M,48M,54Mbps";
			int snr = model.rssi-Integer.parseInt(model.noise);
			model.snr = ""+snr;
			wifiApMap.put(model.bssid,model);
			WifiTools.insertFirstAndLastTime(result.BSSID, System.currentTimeMillis());
			
			//add rssi to indoor map parameters
			if (null != model.bssid && model.bssid.equalsIgnoreCase(WifiTools.getCurrentBssid())) {
				TraceInfoInterface.traceData.setMapParamInfo(Integer.toHexString(UnifyParaID.WLAN_RSSI), ""+model.rssi);
			}
		}
		
			// 这个地方必须做Freeze处理，因为UI中直接使用WIFI_AP_LIST_CLONE的应用
			// 会造成用户滑动屏幕的时候，listview刷新，导致数据发生变化或者出错。
			// 2012.10.25 LFJ
			// if (ApplicationModel.getInstance().isFreezeScreen())
			// return;
			// synchronized (WifiTools.WIFI_AP_LIST_CLONE) {

			for (int i = 0; i < scanResult.size(); i++) {
				APInfoModel model = new APInfoModel();
				ScanResult result = scanResult.get(i);
				model.bssid = result.BSSID;
				model.ssid = result.SSID;
				model.encryptionType = result.capabilities;
				model.frequency = result.frequency;
				model.rssi = result.level;
				// model.mode = scanPlus.getMode(model.bssid.toLowerCase());
				model.supportedProtocol = "802.11 b/g/n";
				model.rates = "1M,2M,5.5M,6M,9M,11M,18M,24M,36M,48M,54Mbps";
				model.beaconPeriod = "100";
				model.noise = "-92";
				int snr = model.rssi - Integer.parseInt(model.noise);
				model.snr = "" + snr;

				// 如果 需要过滤，传给UI的就是已经过滤好的东西
				// 2012.12.24 lfj
				// 如果想过滤全部就什么都不设置，如果什么都不过滤就全部打钩
				if (WifiTools.needFiltered(model))
					wifiAPlist.add(model);
			}
		} catch (Exception e) {
			Log.e(tag, "fill ap list exception  " + e.getMessage());
		}
		// }
		// WifiTools.WIFI_AP_LIST = scanResult;
	}
	@SuppressLint("WrongConstant")
	private void handleEvent(Intent intent) {
	    String action = intent.getAction();
		if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
			// int currWifiState =
			// intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
			// WifiManager.WIFI_STATE_DISABLED);
			// Log.d("Scan", "WIFI_STATE_CHANGED_ACTION state=="+currWifiState);
			wifiStatus = wifiManager.getWifiState();
			if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
				scanResult = null;
				// 下面的代码是在网络断开的情况下通知接收者更新结果
				fillApList();
				Log.w(tag, "receive wifi_state_disabled msg");
				// WifiTools.WIFI_AP_LIST = scanResult;
				for (int i = 0; i < observerList.size(); i++) {
					observerList.get(i).onGetScanResult();
				}
			} else {
				if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
				}
			}
			for (int i = 0; i < observerList.size(); i++) {
				observerList.get(i).onGetWifiStatus(wifiStatus);
			}
		} else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {

			// 使用系统API
			scanResult = wifiManager.getScanResults();
			if (scanResult != null) {
				// Log.d("Scan","wifi API get scan result = " +
				// scanResult.size());
				Collections.sort(scanResult, new ScanResultComparator(
						ScanResultComparator.LEVEL,
						ScanResultComparator.SORT_DESC));
			}
			// with iwlist to scan again...
//			String model = Build.MODEL;
			// if (!model.equals("XT910") && !model.equals("GT-I9308"))
			// //如果是XT910暂时不执行iwlist的操作，程序会崩溃，原因待查
			// scanPlus.scan(2);
			fillApList();

			// WifiTools.WIFI_AP_LIST = scanResult;
			for (int i = 0; i < observerList.size(); i++) {
				observerList.get(i).onGetScanResult();
			}
			// check scanplus
			// Log.d("Scan" , " ap type = " +
			// scanPlus.getApType("00:25:9C:DA:B9:14".toLowerCase()));

		} else if (WifiManager.NETWORK_IDS_CHANGED_ACTION.equals(action)) {

		} else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {

		} else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {

		} else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {

		}
	}
	
	//可以被调用多次
	public void startScan() {
		needScan=true;
		scanStartTimes += 1;
		if (scanThread == null) {
			scanThread = new ScanThread();
			scanThread.start();
		}
		
		LogUtil.w(tag, "--startScan:" + scanStartTimes);
	}
	
	//可以被调用多次，临时性暂定扫描
	public void stopScan() {
		scanStartTimes -= 1;
		if(scanStartTimes <= 0 ){
			needScan = false;
		}
		
		LogUtil.w(tag, "--stopScan:" + scanStartTimes + "--state:" + needScan);
	}
	
	//程序退出的时候执行
	public void quitScan() {
		isStop=true;
		try {
		context.unregisterReceiver(wifiReceiver);
		}catch(Exception e) {
			Log.e(tag,"unregister receiver exception");
		}
	}
	
	class ScanThread extends Thread {
		public void run() {
			while(!isStop) {
				LogUtil.w(tag, "--ScanThread:" + needScan);
				if (WifiTools.isWifiOpen() && needScan) {
					wifiManager.startScan();
				}
		        try {
					Thread.sleep(SCAN_PERIOD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		        
		}
	}
	/*
	 * 这是从其他地方得到的AP列表，在这里进行转化，并推送给观察者，这是个临时的解决方案
	 */
//	public void getAplistFromGrab(ArrayList<APInfoModel> lst){
//		WifiTools.WIFI_AP_LIST = (ArrayList<APInfoModel>)lst.clone();
////		WifiTools.WIFI_AP_LIST.clear();
////		for (int i=0;i<lst.size();i++) {
////			APInfoModel model = new APInfoModel();
////			APInfoModel result = lst.get(i);
////			model.bssid = result.bssid;
////			model.ssid = result.ssid;
////			model.encryptionType = result.encryptionType;
////			model.frequency = result.frequency;
////			model.rssi = result.rssi;
////			WifiTools.WIFI_AP_LIST.add(model);
////		}
//		if (System.currentTimeMillis()-updateTime > 2000){
//			for (int i=0;i<observerList.size();i++) {
//	    		observerList.get(i).onGetScanResult();
//	    	}
//			updateTime = System.currentTimeMillis();
//		}

    	
//	}
	

}
