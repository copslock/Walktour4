package com.dingli.seegull;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.dingli.seegull.ScanModelAdapter.ScanRequestParams;
import com.dingli.seegull.SeeGullFlags.ServiceStatusCode;
import com.dingli.seegull.test.DataSaveThread;
import com.dingli.seegull.test.ScannerSimulator;
import com.walktour.base.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

/**
 * 扫频仪服务类
 * @author jianchao.wang
 *
 */
public class SeeGullScanner {	
	private static final String TAG = "SeeGullScanner";
	
	public static final String SERVICE_IP_ADDRESS = "127.0.0.1";
	public static final int SERVICE_PORT_NUM_BASE  = 5000;	
	
	public static final String ACTION_SEVICE_SEEGULL = "com.pctel.SeeGullConnect";
	public static final String ACTION_STATUS = "com.pctel.statusBroadcastReceiver";
	public static final String ACTION_DETECT = "com.pctel.detectBroadcastReceiver";
	public static final String ACTION_RESPONSE = "com.pctel.responseBroadcastReceiver";
	public static final String STR_STATUS_BROADCAST_RECEIVER_NAME = "STATUS_BROADCAST_RECEIVER_NAME";
	public static final String STR_DEVICE_DETECTOR_BROADCAST_RECEIVER_NAME = "DEVICE_DETECTOR_BROADCAST_RECEIVER_NAME";
	public static final String STR_RESPONSE_BROADCAST_RECEIVER_NAME = "RESPONSE_BROADCAST_RECEIVER_NAME";
	
	public static final String STR_APPLICATION_ID = "APPLICATION_ID";
	public static final String STR_DEVICE_ID = "DEVICE_ID";
	public static final String STR_DEVICE_NAME = "DEVICE_NAME";
	public static final String STR_DEVICE_TYPE = "DEVICE_TYPE";
	public static final String STR_DEVICE_STATE = "DEVICE_STATE";
	public static final String STR_RESPONSE_TYPE = "RESPONSE_TYPE";
	public static final String STR_SCAN = "SCAN";
	public static final String STR_SCAN_RESPONSE = "SCAN_RESPONSE";
	public static final String STR_SCAN_ID = "SCAN_ID";
	public static final String STR_STATUS_CODE = "STATUS_CODE";
	public static final String STR_STATUS_MESSAGE = "STATUS_MESSAGE";
	public static final String STR_REQUEST_TYPE = "REQUEST_TYPE";
	public static final String STR_REQUEST_PARAMETERS = "REQUEST_PARAMETERS";
	public static final String STR_RESPONSE = "RESPONSE"; 
	public static final String STR_IP_ADDRESS = "IP_ADDRESS";
	public static final String STR_PORT_NUMBER = "PORT_NUMBER";
	public static final String STR_LICENSE_INQUIRY = "LICENSE_INQUIRY";
	public static final String STR_INQUIRY = "INQUIRY";
	public static final String STR_CONNECT_DEVICE = "CONNECT_DEVICE";
	public static final String STR_STOP_SCAN = "STOP_SCAN";
	public static final String STR_CONNECTION_TYPE = "CONNECTION_TYPE";	
	
	private static final int MSG_CLOSE_SERVICE = -1;
	private static final int MSG_INITIALIZE_SERVICE = 0;
	private static final int MSG_DETECT_DEVICE = 1;
	private static final int MSG_CONNECT_DISCONNECT_DEVICE = 2;
	private static final int MSG_CONTROL_REQUEST = 3;
	private static final int MSG_SCAN_REQUEST = 4;
	//private static final int MSG_GPS_REQUEST = 5;	
	
	private static final int STATE_NOT_INIT = 0;
	private static final int STATE_INITIALING = 1;
	private static final int STATE_DETECTING = 2;
	private static final int STATE_CONNECTING = 3;
	private static final int STATE_READY = 4;
	private static final int STATE_SCANNING = 5;
	private static final int STATE_DISCONNECTED = 6;
	
	
	
	private Context mContext;
	private int 	mAppId = -1;
	private int 	mDeviceId = -1;
	private String 	mDeviceName = "-";
	private String 	mDeviceType = "-";	
	private int 	mDeviceState = STATE_NOT_INIT;
	
	private boolean mUseSimulator = false;
	private boolean mNeedInit = false;		
	
	
	private Messenger mServiceMessenger;
	private ScannerBroadcastReceiver mScannerBReceiver;
	private MyServiceConnection myServiceConnection;	
	private DataSaveThread mSaveDataThread = null;
	private ReadSocketThread mReadSocketThread = null;	
	private ScannerSimulator mDataReplayThread = null;
	/**
	 * identify the debug mode, 0 - Normal, 1 - Save, 2 - Replay 
	 */
	
	public SeeGullScanner(Context ctx) {
		mContext = ctx;
	}
	
	private OnEventListener mParentListener;
	public abstract interface OnEventListener {
		abstract void onResponse(Intent intent); 
		abstract void onDataReceive(byte[] data);
	};
	
	
	/**
	 * @param listener which implement the interface {@link ResponseListener}
	 */
	public void setResponseListener(OnEventListener listener) {
		mParentListener = listener;
	}
	
	/**
	 * Init the device and try to connect it to the remote scanner.
	 * @param simulate {@code true} for use simulator as replaying.
	 */
	public void init(boolean simulate) {
		mUseSimulator = simulate;
		createDataThread();
//		release();       //释放移除
		bindService();
		registerBroadcastReceiver();
		mNeedInit = true;
		
	}
	/**
	 * release the the connection with remote scanner.
	 */
	public void release() {	
		releaseDataThread();
		disconnectDevice();			
		closeService();				
		unbindService();
		killService();
		unregisterBroadcastReceiver();		

		mAppId = -1;
		mDeviceId = -1;		
		mDeviceName = "-";
		mDeviceType = "-";
		mDeviceState = STATE_NOT_INIT;	
		mNeedInit = false;
	}

	/**
	 * Get the device id as String.
	 * @return the available device id, -1 as not available.
	 */
	public String getDeviceId() {
		return Integer.toString(mDeviceId);
	}
	/**
	 * Get the device name.
	 * @return the available device name, null as not available.
	 */
	public String getDeviceName() {
		return mDeviceName;
	}
	/**
	 * Get the device name.
	 * @return as "Bluetooth", or null for not available.
	 */
	public String getDeviceType() {
		return mDeviceType;
	}
	
	/**
	 * Check device state   
	 * @return {@code true} is connected. 
	 */
	public boolean isConnected() {
		return (mDeviceState == STATE_READY 
				|| mDeviceState == STATE_SCANNING);
	}	
	/**
	 * Check device state   
	 * @return {@code true} is connecting. 
	 */
	public boolean isConnecting() {
		return (mDeviceState == STATE_INITIALING 
				|| mDeviceState == STATE_DETECTING	 
				|| mDeviceState == STATE_CONNECTING);
	}
	
	
	private void bindService() {
		Intent intent = new Intent();
		intent.setAction(ACTION_SEVICE_SEEGULL);
		myServiceConnection = new MyServiceConnection();
		mContext.bindService(intent, myServiceConnection, Context.BIND_AUTO_CREATE);
	}
	private void unbindService() {
		if (myServiceConnection != null) {
			mContext.unbindService(myServiceConnection);
			myServiceConnection = null;
		}
	}
	private void killService() {
		//mContext.stopService(service);
	}
	private void InitService()	{
    	Bundle bundle = new Bundle();
    	bundle.putString(STR_STATUS_BROADCAST_RECEIVER_NAME, ACTION_STATUS );
    	bundle.putString(STR_DEVICE_DETECTOR_BROADCAST_RECEIVER_NAME, ACTION_DETECT );
    	bundle.putString(STR_RESPONSE_BROADCAST_RECEIVER_NAME, ACTION_RESPONSE);
    	bundle.putString(STR_IP_ADDRESS, SERVICE_IP_ADDRESS);
    	bundle.putInt(STR_PORT_NUMBER, SERVICE_PORT_NUM_BASE);

    	sendServiceMessage(MSG_INITIALIZE_SERVICE, bundle);    	
	}
	private void closeService() {		
		if (mAppId != -1) {
			Bundle bundle = new Bundle();
			bundle.putInt(STR_APPLICATION_ID, mAppId); 
			sendServiceMessage(MSG_CLOSE_SERVICE, bundle);
		}			
	}	
	
	/*
	 * BundleKey		 Type		 Description
	 * APPLICATION_ID		 int		 Application Id return from Initialize Service command.
	 * CONNECTION_TYPE		 byte		 Connection Type, Value:
	 *	  0 - Reserved for Bluetooth and USB 1 - Bluetooth 2 - Reserved USB
	 * Note: Detected Bluetooth and USB devices will be reported through separate broadcast receiver messages.
	 */
	private boolean detectDevice() {	
		Bundle bundle = new Bundle();
		bundle.putInt(STR_APPLICATION_ID, mAppId);
		bundle.putByte(STR_CONNECTION_TYPE, (byte)1); // must be 1 for Bluetooth	 
 
		return sendServiceMessage(MSG_DETECT_DEVICE, bundle);
	}
	
	private boolean connectDevice() {
		Bundle bundle = new Bundle();
		bundle.putInt(STR_APPLICATION_ID, mAppId);
		bundle.putInt(STR_DEVICE_ID, mDeviceId);
		bundle.putBoolean(STR_CONNECT_DEVICE, true); // true to connect to device, false to disconnect

    	return sendServiceMessage(MSG_CONNECT_DISCONNECT_DEVICE, bundle);    	
	}
	
	private boolean disconnectDevice() {
		if (mAppId != -1 && mDeviceId != -1) {
			Bundle bundle = new Bundle();
			bundle.putInt(STR_APPLICATION_ID, mAppId);
			bundle.putInt(STR_DEVICE_ID, mDeviceId);
			bundle.putBoolean(STR_CONNECT_DEVICE, false); // true to connect to device, false to disconnect

			return sendServiceMessage(MSG_CONNECT_DISCONNECT_DEVICE, bundle);    
		}
		return false;			
	}
	
	private void checkStatusCode(int statusCode) {
		switch (statusCode) {
		case ServiceStatusCode.DEVICE_CONNECTED_SUCCEED:
			mDeviceState = STATE_READY;
			break;
		case ServiceStatusCode.SERVICE_NOT_INITIALIZED:
			if (mNeedInit) {
				InitService();
				mDeviceState = STATE_INITIALING;
			}							
			break;
		case ServiceStatusCode.INITIALIZATION_FAILED:
			if (mNeedInit) {
				InitService();	
				mDeviceState = STATE_INITIALING;
			}
			break;
		case ServiceStatusCode.INITIALIZATION_SUCCEED:
			if (mNeedInit) {
				detectDevice();
				mDeviceState = STATE_DETECTING;
			}
			break;
		case ServiceStatusCode.DETECTION_PROCESS_COMPLETED:
			if (mNeedInit) {
				if (mDeviceId != -1) {//device find
					connectDevice();
					mDeviceState = STATE_CONNECTING;
				} else {// no device
					detectDevice();
					mDeviceState = STATE_DETECTING;
				}
			}			
			break;
		case ServiceStatusCode.DEVICE_CONNECTION_LOST:
			mDeviceState = STATE_DISCONNECTED;
			if (mNeedInit) {
				connectDevice();
				mDeviceState = STATE_CONNECTING;
			}
			break;
		case ServiceStatusCode.DEVICE_DISCONNECTED_FAIL:
			//mDeviceState = STATE_DISCONNECTED;
			break;
		case ServiceStatusCode.DEVICE_DISCONNECTED_SUCCEED:
			//mDeviceState = STATE_DISCONNECTED;//在通信过程中有出现，未知原因
			break;
		}					
	}	
	/**
	 * send a license inquiry request.
	 * @throws JSONException
	 */
	public void sendScannerLicenseInquiryRequest() throws JSONException {
		JSONObject jsonObject = new JSONObject();		
		jsonObject.put(STR_REQUEST_TYPE, STR_LICENSE_INQUIRY);//REQUEST_TYPE Must be "LICENSE_INQUIRY"
		LogUtil.d(TAG, "sendScannerLicenseInquiryRequest:" + jsonObject.toString());
		Bundle bundle = new Bundle();
		bundle.putInt(STR_APPLICATION_ID, mAppId);
		bundle.putInt(STR_DEVICE_ID, mDeviceId);
		bundle.putString(STR_REQUEST_PARAMETERS, jsonObject.toString());	
		 
		sendServiceMessage(MSG_CONTROL_REQUEST, bundle);
	}
	private class MyServiceConnection implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			//Log.i(TAG, "onServiceConnected() called.");
			if(mServiceMessenger == null) {
				mServiceMessenger = new Messenger(arg1);		
			}		
			InitService();		
		}
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			//Log.i(TAG, "onServiceDisconnected() called.");
			mServiceMessenger = null;
		}
	}	
	
	private void registerBroadcastReceiver() {
		IntentFilter filter = new IntentFilter();		
		filter.addAction(ACTION_STATUS);
		filter.addAction(ACTION_DETECT);
		filter.addAction(ACTION_RESPONSE);
		mScannerBReceiver = new ScannerBroadcastReceiver();
		mContext.registerReceiver(mScannerBReceiver, filter);
	}
	
	private void unregisterBroadcastReceiver() {
		if (mScannerBReceiver != null) {
			mContext.unregisterReceiver(mScannerBReceiver);
			mScannerBReceiver = null;
		}
	}
	
	private boolean sendServiceMessage(int what, Bundle data) {
		//for debug
		if (true && mParentListener != null) {
			Intent intent = new Intent("SEND_SERVICE_MSG("+what+")");
			intent.putExtras(data);
			mParentListener.onResponse(intent);
		}
		
		if (mServiceMessenger == null)
			return false;	
		
		try {			
			Message msg = Message.obtain(null, what, 0, 0);		
			msg.setData(data);	
			mServiceMessenger.send(msg);
			return true;			
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private class ScannerBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!mUseSimulator) {
				//Log.i(TAG, "ScannerBroadcastReceiver.onReceive() called.");
				parseScannerBroadcastIntent(intent);
			} else {
				//Log.i(TAG, "ScannerBroadcastReceiver.onReceive() called but abort msg.");
			}
		}			
	}
	private void parseScannerBroadcastIntent(Intent intent) {
		boolean actionMatch = false;
		String action = intent.getAction();
		
		String jsonString = intent.getStringExtra(STR_RESPONSE);			
		Map<String, Object> map = Utils.parseJSONToMap(jsonString);
		
		if (action.equals(ACTION_STATUS)) {
			actionMatch = true;
			if (map != null && map.containsKey(STR_APPLICATION_ID))
				mAppId = (Integer)map.get(STR_APPLICATION_ID);
			if (map != null && map.containsKey(STR_STATUS_CODE)) {					
				int statusCode = (Integer)map.get(STR_STATUS_CODE);
				checkStatusCode(statusCode);					
			}			
		} else if (action.equals(ACTION_DETECT)) {
			actionMatch = true;
			if (map != null && map.containsKey(STR_DEVICE_ID)) {
				mDeviceId = (Integer)(map.get(STR_DEVICE_ID));
			}
			if (map != null && map.containsKey(STR_DEVICE_NAME)) {
				mDeviceName = (String)map.get(STR_DEVICE_NAME);
			}
			if (map != null && map.containsKey(STR_DEVICE_TYPE)) {
				mDeviceType = (String)map.get(STR_DEVICE_TYPE);
			}	
		} else if (action.equals(ACTION_RESPONSE)) {
			actionMatch = true;
		}		
		
		if (actionMatch && mParentListener != null) {
			mParentListener.onResponse(intent);
		}
	}	
	
	/*
	 * BundleKey		 Type		 Description		 
	 * APPLICATION_ID	 int		 Application Id return from Initialize Service command.		 
	 * DEVICE_ID		 int		 Scanner device Id.		 
	 * REQUEST_PARAMETERS	String	 Request parameters in JSON string.
     *
	 * Request Parameters
	 * JSON Key		 Type		 Description		 
	 * REQUEST_TYPE		 String		 Must be "INQUIRY"
	 */
	
	/**
	 * Send an inquiry request to scanner.
	 * @throws JSONException 
	 */
	public void sendScannerInquiryRequest() throws JSONException {
		// setup scanner inquiry request
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(STR_REQUEST_TYPE, STR_INQUIRY);//REQUEST_TYPE Must be "INQUIRY"
		
		Bundle bundle = new Bundle();
		bundle.putInt(STR_APPLICATION_ID, mAppId);
		bundle.putInt(STR_DEVICE_ID, mDeviceId);
		bundle.putString(STR_REQUEST_PARAMETERS, jsonObject.toString());		 
		
		sendServiceMessage(MSG_CONTROL_REQUEST, bundle);
	}
	
	/*
	 * APPLICATION_ID	int	Application Id return from Initialize Service command.
	 * DEVICE_ID		int	Scanner device Id.
	 * REQUEST_PARAMETERS	String	Request parameters in JSON string.
	 * 
	 * Request parameters
	 * SCAN_TYPE	String	Scan type. Values:
	 * 		RSSI
	 * 		ENHANCED_POWER_SCAN
	 * 		POWER_ANALYSIS
	 * 		COLOR_CODE
	 * 		TOP_N_PILOT
	 * 		ENHANCED_TOP_N_SIGNAL
	 * 		BLIND_SCAN
	 * 		WIFI_THROUGHPUT
	 *SCAN_REQUEST_BODY	JSONObject	Scan specific parameters in JSONObject
	 * 		RSSI Scan Request Parameters
	 * 		Enhanced Power Scan Request Parameters
	 * 		Power Analysis Scan Request Parameters
	 * 		Color Code Request Parameters
	 * 		Top N Pilot Scan Request Parameters
	 * 		Enhanced Top N Signal Scan Request Parameters
	 * 		Blind Scan Request Parameters
	 * 		WiFi Throughput Scan Request Parameters
	 * 
	 */
	/**
	 * send a scan request
	 * @param scanType
	 * @param parameters 
	 * @return >= 0 as succeed
	 * @throws JSONException
	 */		
	private int sendScanRequest(JSONObject requestParameters) throws JSONException {
		LogUtil.d(TAG, "sendScanRequest:" + requestParameters.toString());
		// create bundle object
		Bundle bundle = new Bundle();
		bundle.putInt(STR_APPLICATION_ID, mAppId);
		bundle.putInt(STR_DEVICE_ID, mDeviceId);
		bundle.putString(STR_REQUEST_PARAMETERS, requestParameters.toString());		
		sendServiceMessage(MSG_SCAN_REQUEST, bundle);
		return 0;
	}
	/**
	 * send a scan request.
	 * @param params contains all the params, include the jsonstring param.
	 * @throws JSONException
	 */
	public int sendScanRequest(ScanRequestParams params) throws JSONException {		
		//for debug
		if (mUseSimulator) {
			int scanId = mDataReplayThread.sendScanRequest(params);
			params.scanId = scanId;
		} else {
			sendScanRequest(params.paramsAsJsonObject);			
			byte[] frame = Utils.createConfigFrame(params);
			mParentListener.onDataReceive(frame);				
		}
		return 0;	
		
	}
	/**
	 * Send a stop scan request to scanner.
	 * @param scanId to Identify the device.
	 * @return 0 as succeed
	 * @throws JSONException
	 */
	public int sendStopScanRequest(int scanId) throws JSONException {
		if (mUseSimulator)
			return mDataReplayThread.sendStopScanRequest(scanId);
		
		JSONObject jsonObject = new JSONObject();		
		jsonObject.put(STR_REQUEST_TYPE, STR_STOP_SCAN);
		jsonObject.put(STR_SCAN_ID, scanId); // optional	
		LogUtil.d(TAG, "sendStopScanRequest:" + jsonObject.toString());
		
		Bundle bundle = new Bundle();
		bundle.putInt(STR_APPLICATION_ID, mAppId);
		bundle.putInt(STR_DEVICE_ID, mDeviceId);
		bundle.putString(STR_REQUEST_PARAMETERS, jsonObject.toString());		
		sendServiceMessage(MSG_CONTROL_REQUEST, bundle);
		return 0;
	}
	
	/**
	 * sub thread for dealing socket data
	 */	
	private class  ReadSocketThread extends Thread {
		private Socket 	mDataSocket;
		private ServerSocket mServerSocket;
		private boolean mBreakFlag = false;
		
		public ReadSocketThread() {
		}
		
		private void readSocket() {
			try {	
				if (mServerSocket != null) {
					mServerSocket.close();
				}
				mServerSocket = new ServerSocket(SERVICE_PORT_NUM_BASE);
				mDataSocket = mServerSocket.accept(); //hold thread

				InputStream fis = mDataSocket.getInputStream();					
	
				byte[] buf = new byte[65536];					 
				while(!mBreakFlag) {
					int bytes = fis.read(buf, 0, 65536);
					if (bytes < 0 || mBreakFlag)
						break;					
					if (mParentListener != null) {
						byte[] frame = Utils.createDataFrame(buf, bytes);
						mParentListener.onDataReceive(frame);
						
						//debug
						if (mSaveDataThread != null && mSaveDataThread.isAlive()) {
							mSaveDataThread.addData(frame);
						}
					}
				}				
			} catch (IOException e) {    			
			} finally {					
				try {
					if (mDataSocket != null && !mDataSocket.isClosed()) 							
						mDataSocket.close();
					mDataSocket = null;
				} catch (Exception e) {
				}
				try {
					if (mServerSocket != null && !mServerSocket.isClosed()) 							
						mServerSocket.close();
					mServerSocket = null;
				} catch (Exception e) {
				}			
			}
		}
		
		@Override
		public void run() {				
			while(!mBreakFlag) {
				readSocket();
				try {
					sleep(1000);
				} catch (InterruptedException e) {
				}
			}				
			
		}
		public void cancel() {
			mBreakFlag = true;
			if (mServerSocket != null && !mServerSocket.isClosed()) {
				try {
					mServerSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
			this.interrupt();			
		}
	}	
	
	private void createDataThread() {
		if (mReadSocketThread != null) {
			mReadSocketThread.cancel();
			mReadSocketThread = null;
		}
		if (mSaveDataThread != null) {
			mSaveDataThread.cancel();
			mSaveDataThread = null;
		}		
		
		mReadSocketThread = new ReadSocketThread();
		mReadSocketThread.start();	
		
		//for debug
		mSaveDataThread = new DataSaveThread();
		mSaveDataThread.start();
		//////////////////////////////////////////////////////////////
		////for replay
		if (mUseSimulator)
			initDataReplay();
		
	}
	private void releaseDataThread() {
		if (mSaveDataThread != null) {
			mSaveDataThread.cancel();
			mSaveDataThread = null;
		}
		if (mReadSocketThread != null) {
			mReadSocketThread.cancel();
			mReadSocketThread = null;
		}
	}
	
	////////////////////////////////////////////////////////////////////////
	//////--for replay
	private void initDataReplay() {
		OnEventListener listener = new OnEventListener() {
			@Override
			public void onResponse(Intent intent) {		
				parseScannerBroadcastIntent(intent);
			}
			@Override
			public void onDataReceive(byte[] data) {
				if (mParentListener != null) {
					mParentListener.onDataReceive(data);
				}
			}		
		};
		if (mDataReplayThread != null) {
			mDataReplayThread.cancel();
			mDataReplayThread = null;
		}
		mDataReplayThread = new ScannerSimulator();
		mDataReplayThread.setEventListener(listener);		
		mDataReplayThread.init();
		mDataReplayThread.start();
	}
}
