package com.dingli.seegull;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dingli.seegull.ScanModelAdapter.ScanRequestParams;
import com.dingli.seegull.SeeGullFlags.ScannerStatusCode;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 扫频仪工具类
 * @author jianchao.wang
 *
 */
public class SeeGullUtils {	
	
	private static final String TAG = "SeeGullUtils";	
	
	public static final String STR_STATE_DISCONNECTED = "DISCONNECTED";
	public static final String STR_STATE_CONNECTING = "CONNECTING";
	public static final String STR_STATE_CONNECTED = "CONNECTED";	

	@SuppressWarnings("unused")
	private Context mContext;
	private SeeGullScanner mScanner;
	private OnEventListener mListener;	
	private List<ScanRequestInfo> mScanRequestInfoList = new ArrayList<ScanRequestInfo>();	
	
	/**
	 * The interface for callback to activity or other binded caller
	 */
	public abstract interface OnEventListener {
		public abstract void onDataReceive(byte[] data);
		public abstract void onUpdateDeviceInfo(Map<String, Object> info);
		public abstract void showDebugInfo(String info);
	}	
		
	public SeeGullUtils(Context ctx) {
		mContext = ctx;		
		mScanner = new SeeGullScanner(ctx);
		mScanner.setResponseListener(mScannerListener);
	}
	
	private SeeGullScanner.OnEventListener mScannerListener = new SeeGullScanner.OnEventListener() {
		@Override
		public void onResponse(Intent intent) {
			String jsonString = intent.getStringExtra(SeeGullScanner.STR_RESPONSE);
			Map<String, Object> map = Utils.parseJSONToMap(jsonString);
			parseResponseInfo(map);	
			mListener.onUpdateDeviceInfo(getDeviceInfo());
			String s = Utils.getIntentInfo(intent);
			mListener.showDebugInfo(s);
		}

		@Override
		public void onDataReceive(byte[] data) {
			//byte[] frame = Utils.createDataFrame(data, data.length);
			mListener.onDataReceive(data);
		}		
	};
	
	/**
	 * set the callback listener.
	 * @param listener
	 */
	public void setOnEventListener(OnEventListener listener) {
		mListener = listener;
	}
	/**
	 * call the initial function for connect scanner.
	 */
	public void init(boolean simulate) {
		release();		
		mScanner.init(simulate);
	}
	/**
	 * release the resource and the connection of scanner. 
	 */
	public void release() {	
		mScanner.release();		
		mScanRequestInfoList = new ArrayList<ScanRequestInfo>();
	}
	
	public Map<String, Object> getDeviceInfo() {
		Map<String, Object> infos = new HashMap<String, Object>();
		infos.put(SeeGullScanner.STR_DEVICE_NAME, new String(mScanner.getDeviceName()));
		infos.put(SeeGullScanner.STR_DEVICE_TYPE, new String(mScanner.getDeviceType()));
		
		if (mScanner.isConnected()) {
			infos.put(SeeGullScanner.STR_DEVICE_STATE, STR_STATE_CONNECTED);
		} else if (mScanner.isConnecting()) {
			infos.put(SeeGullScanner.STR_DEVICE_STATE, STR_STATE_CONNECTING);
		} else {
			infos.put(SeeGullScanner.STR_DEVICE_STATE, STR_STATE_DISCONNECTED);
		}
		
		if (mScanRequestInfoList.size() > 0) {
			List<Map<String, Integer>> list = new ArrayList<Map<String, Integer>>();
			for (int i=0; i<mScanRequestInfoList.size(); i++) {
				ScanRequestInfo info = mScanRequestInfoList.get(i);
				Map<String, Integer> map = new HashMap<String, Integer>();
				map.put("SCAN_ID", info.params.scanId);
				map.put("SCAN_TYPE", info.params.scanType);
				map.put("SCAN_STATUS", info.scanStatus);
				list.add(map);
			}
			infos.put("SCAN_INFO_LIST", list);
		}		
		return infos;
	}
	
	/**
	 * start a scan request
	 * @param scanType
	 * @param params
	 * @return the new scanId if >= 0, others fail.
	 */
	public int startScan(Object paramsobj) {		
		if (!mScanner.isConnected()) {
			Log.w(TAG, "Device not connected.");
			return -1;
		}
		Log.d(TAG, "startScan");
		
		try {
			ScanModelAdapter setup = new ScanModelAdapter(paramsobj);
			List<ScanRequestParams> paramsList = setup.createAllScanParameters();
			//判断重置scanId
			for (int i=0; i<paramsList.size(); i++) {
				ScanRequestParams params = paramsList.get(i);
				if (isScanIdUsed(params.scanId)) {
					params.scanId = getNextScanId();
					params.paramsAsJsonObject.put(SeeGullScanner.STR_SCAN_ID, params.scanId);
				}				
				ScanRequestInfo info = new ScanRequestInfo(params);
				mScanRequestInfoList.add(info);	
				
				mScanner.sendScanRequest(params);
			}
			mListener.onUpdateDeviceInfo(getDeviceInfo());
			return 0;
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		return -1;
	}
	/**
	 * setup stop scan request
	 * @param scanId
	 * @return 0 succeed, others fail.
	 */
	public int stopScan(int scanId) {	
		int retval = 0;
		
		if (mScanRequestInfoList == null || mScanRequestInfoList.isEmpty())
			return retval;
		Log.d(TAG, "stopScan:"+scanId);
		
		for (int i=mScanRequestInfoList.size()-1; i>=0; i--) {
			ScanRequestInfo info = mScanRequestInfoList.get(i);
			int id = info.params.scanId;
			if (scanId == -1 || scanId == id) {
				try {
					retval = mScanner.sendStopScanRequest(id);
				} catch (JSONException e) {
					retval = -2;
				}				
				mScanRequestInfoList.remove(i);
				if (scanId != -1)
					break;
			}
		}	
		mListener.onUpdateDeviceInfo(getDeviceInfo());
		return retval;
	}	
	
	
	/**
	 * The struct defined for saving scan request info
	 * and a list for all scanrequest, and then keep all 
	 * scanrequest info by add && remove element with the list; 
	 */
	public class ScanRequestInfo {
		ScanRequestParams params;	
		int scanStatus = -1;
		String scanStatusMsg = null;
		
		public ScanRequestInfo(ScanRequestParams params) {
			this.params = params;
		}		
	}
	
	private int getNextScanId() {
		if (mScanRequestInfoList.size() > 254) {
			for (int i=0; i<mScanRequestInfoList.size(); i++) {
				ScanRequestInfo info = mScanRequestInfoList.get(i);
				if (info.scanStatus != -1 && info.scanStatus != ScannerStatusCode.NORMAL_OK) {
					mScanRequestInfoList.remove(i);
					break;
				}					
			}
		}
		if (mScanRequestInfoList.size() > 254)
			return -1;		
		
		for (int id=0; id<255; id++) {
			boolean found = false;			
			for (int i=0; i<mScanRequestInfoList.size(); i++) {
				ScanRequestInfo info = mScanRequestInfoList.get(i);
				if (id == info.params.scanId) {
					found = true;
					break;
				}
			}	
			if (!found)
				return id;	
		}
		return -1;		
	}
	private boolean isScanIdUsed(int scanId) {
		for (int i=0; i<mScanRequestInfoList.size(); i++) {
			ScanRequestInfo info = mScanRequestInfoList.get(i);
			if (scanId == info.params.scanId) {
				return true;
			}
		}
		return false;
	}
		
	@SuppressWarnings("unchecked")
	private void parseResponseInfo(Map<String, Object> info) {
		if (info == null)
			return;
		String responseType = null;
		try {
			responseType = (String)info.get(SeeGullScanner.STR_RESPONSE_TYPE);
			
			if (responseType != null  && responseType.equals(SeeGullScanner.STR_SCAN)) {
				Map<String, Object> scan_response = (Map<String, Object>)info.get(SeeGullScanner.STR_SCAN_RESPONSE);
				if (scan_response != null) {						
					int scanId = (Integer) scan_response.get(SeeGullScanner.STR_SCAN_ID);
					int status = (Integer) scan_response.get(SeeGullScanner.STR_STATUS_CODE);
					String statusmsg = (String)scan_response.get(SeeGullScanner.STR_STATUS_MESSAGE);
					
					for (int i=0; i<mScanRequestInfoList.size(); i++) {
						ScanRequestInfo scaninfo = mScanRequestInfoList.get(i);
						if (scanId == scaninfo.params.scanId) {
							scaninfo.scanStatus = status;
							scaninfo.scanStatusMsg = statusmsg;
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	
}
