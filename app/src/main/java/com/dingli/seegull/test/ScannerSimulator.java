package com.dingli.seegull.test;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.dingli.seegull.ScanModelAdapter.ScanRequestParams;
import com.dingli.seegull.SeeGullFlags.ProtocolCodes;
import com.dingli.seegull.SeeGullFlags.ScanTypes;
import com.dingli.seegull.SeeGullScanner;
import com.dingli.seegull.SeeGullScanner.OnEventListener;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScannerSimulator extends Thread {	
	private static final String TAG = "ScannerSimulator";	
	private final static String FILE_SAVE_PATH = "/Scanner";		
	/**
	 * contains all the replay data info.
	 */
	private List<RepleyInfo> mReplayInfoList = new ArrayList<RepleyInfo>();
	/**
	 * if no-null then send to uplayer in the while() of run();
	 */
	private Intent mResponseIntent = null;
	/**
	 * set as {@code true} for breaking the while() loop when call cancel() by uplayer.
	 */
	private boolean mNeedStopFlag = false;

	private ScannerSimulator mInstance = null;
	/**
	 * get the single instance;
	 */
	public ScannerSimulator getInstance() {
		if (mInstance == null)
			mInstance = new ScannerSimulator();
		return mInstance;
	}
	
	private OnEventListener mListener;
	/**
	 * set the callback.
	 * @param l
	 */
	public void setEventListener(OnEventListener l) {
		mListener = l;
	}
	
	/**
	 * init the simulator; after calling it, a serial device state information responsed 
	 * will send to uplayer by callback fun onResponse(intent).
	 */
	public void init() {
		initResponse();
		mResponseIndex = 0;
		mMsgHandler.postDelayed(mInitRunnable, 100);
	}	
	/**
	 * simulate sending a scan request
	 * @param scanType
	 * @param protocolCode
	 * @return the scanId;
	 */
	public int sendScanRequest(ScanRequestParams params) {
		Log.i(TAG, "startScan() called.");
		//根据数据返回ScanId;
		RepleyInfo info = new RepleyInfo(params.scanType, params.protocolCode);
		if (info.isInitOK()) {
			synchronized(mReplayInfoList) {
				mReplayInfoList.add(info);
			}
			setResponseIntent(info.scanId, true);					
			return info.scanId;
		}		
		return -1;
	}
	/**
	 * simulate sending a stopscan request
	 * @param scanId
	 * @return
	 */
	public int sendStopScanRequest(int scanId) {
		Log.i(TAG, "stopScan() called, scanId="+scanId);
		synchronized(mReplayInfoList) {
			for (int i=mReplayInfoList.size()-1; i>=0; i--) {
				RepleyInfo info = mReplayInfoList.get(i);
				if (info.scanId == scanId || scanId == -1) {
					try {
						info.inputStream.close();
					} catch (IOException e) {
					}
					setResponseIntent(info.scanId, false);
					mReplayInfoList.remove(i);
					
				}
			}
		}
		return 0;
	}
	
	private void setResponseIntent(int scanId, boolean isStartScan) {
		Intent intent = new Intent(SeeGullScanner.ACTION_RESPONSE);
		String response;
		if (isStartScan) {
			response = "{\"SCAN_RESPONSE\":{\"STATUS_MESSAGE\":\"Success.\",\"SCAN_ID\":"
					+scanId+ ",\"STATUS_CODE\":0},\"RESPONSE_TYPE\":\"SCAN\"}";	
		} else {
			response = "{\"STOP_SCAN_RESPONSE\":{\"REMAINING_SLOTS\":50000," +
					"\"STATUS_MESSAGE\":\"Success.\",\"STATUS_CODE\":0}," +
					"\"CONTROL_RESPONSE_TYPE\":\"STOP_SCAN\",\"RESPONSE_TYPE\":\"CONTROL\"}";
		}
		intent.putExtra(SeeGullScanner.STR_RESPONSE, response);
		
		if (mResponseIntent != null && mListener != null)
				mListener.onResponse(mResponseIntent);
		mResponseIntent = intent;
	}
	
	@Override 
	public void run() {
		while (!mNeedStopFlag) {
			synchronized(mReplayInfoList) {
				if (mResponseIntent != null) {
					if (mListener != null) {
						mListener.onResponse(mResponseIntent);
					}
					mResponseIntent = null;
				}
				for (int i=0; i<mReplayInfoList.size(); i++) {
					RepleyInfo info = mReplayInfoList.get(i);
					if (info.delayClick > info.delayClickMax)
						info.delayClick = info.delayClickMax;
					info.delayClick--;
					if (info.delayClick <= 0) {
						info.delayClick = info.delayClickMax;
						byte[] frame= info.getNextPackage();
						if (mListener != null) {
							mListener.onDataReceive(frame);
						}
					}					
				}				
			}
			try {
				sleep(1);
			} catch (InterruptedException e) {
			}			
		}
		Log.i(TAG, "ScannerSimulator thead-"+this.getId()+".run() finish.");	
	}
	public void cancel() {
		this.interrupt();
		mNeedStopFlag = true;
    	Log.i(TAG, "DataReplayThread.cancel() called, thead="+Thread.currentThread().getId());
	}
	public void release() {
		cancel();
		mInstance = null;
	}

	private Intent[] mResponseIntents = new Intent[6];
	private int mResponseIndex = 0;
	private void initResponse() {
		mResponseIntents[0] = new Intent(SeeGullScanner.ACTION_STATUS);
		mResponseIntents[0].putExtra(SeeGullScanner.STR_RESPONSE,
				"{\"STATUS_MESSAGE\":\"Initialization successful.\",\"APPLICATION_ID\":0,\"STATUS_CODE\":-3}");
		
		mResponseIntents[1] = new Intent(SeeGullScanner.ACTION_DETECT);
		mResponseIntents[1].putExtra(SeeGullScanner.STR_RESPONSE, 
				"[{\"DEVICE_NAME\":\"IBflex-ESN-0000-0000-8140-7045\",\"DEVICE_ID\":1,\"DEVICE_TYPE\":\"Bluetooth\"}]");
		
		mResponseIntents[2] = new Intent(SeeGullScanner.ACTION_STATUS);
		mResponseIntents[2].putExtra(SeeGullScanner.STR_RESPONSE, 
				"{\"STATUS_MESSAGE\":\"Detection process completed.\",\"STATUS_CODE\":-10}");
		
		mResponseIntents[3] = new Intent(SeeGullScanner.ACTION_STATUS);
		mResponseIntents[3].putExtra(SeeGullScanner.STR_RESPONSE, 
				"{\"STATUS_MESSAGE\":\"TCP\\/IP connection established.\",\"STATUS_CODE\":-51}");
		
		mResponseIntents[4] = new Intent(SeeGullScanner.ACTION_STATUS);
		mResponseIntents[4].putExtra(SeeGullScanner.STR_RESPONSE, 
				"{\"STATUS_MESSAGE\":\"Bluetooth socket connection established successfully.\",\"STATUS_CODE\":-18}");
		
		mResponseIntents[5] = new Intent(SeeGullScanner.ACTION_STATUS);
		mResponseIntents[5].putExtra(SeeGullScanner.STR_RESPONSE, 
				"{\"STATUS_MESSAGE\":\"Device connected successful.\",\"STATUS_CODE\":-31}");
	}
	
	private static Handler mMsgHandler = new Handler() {    	
    };
    private final Runnable mInitRunnable = new Runnable() {
		@Override
		public void run() {						
			mMsgHandler.removeCallbacks(mInitRunnable);			
			if (mResponseIndex < mResponseIntents.length) {
				if (mListener != null) {
					mListener.onResponse(mResponseIntents[mResponseIndex]);					
				}
				mResponseIndex++;
				if (mResponseIndex < mResponseIntents.length)
					mMsgHandler.postDelayed(mInitRunnable, 1000);
			}			
		}
	};
		
	private class RepleyInfo {
		public int scanType = -1;
		public int scanId = -1;
		public int protocolCode;
		public FileInputStream inputStream;
		public File dataFile;
		public int delayClick;
		public int delayClickMax;
//		public int packetRate;
		public byte[] headbuf = new byte[8];
		public byte[] payload = new byte[8*1024];

		public boolean isInitOK() {
			return (scanId != -1 && inputStream != null);
		}
		public RepleyInfo(int scanType, int protocol) {
			this.scanType = scanType;
			this.protocolCode = protocol;
//			this.packetRate = packetRate;
			init();
		}	
		public byte[] getNextPackage() {
			try {
				if (inputStream.available() < 8) {
					inputStream.close();
					inputStream = new FileInputStream(dataFile); 
				}
				int bytes = inputStream.read(headbuf, 0, 8);
				int length = headbuf[4]<<24 | (headbuf[5]<<16 & 0x00FF0000) | (headbuf[6]<<8 & 0x0000FF00) | (headbuf[7] & 0x000000FF);
				if (bytes < 8 || length < 0) {
					inputStream.close();
					inputStream = new FileInputStream(dataFile);
					return null;
				}		
				//Log.i(TAG, "bytes="+bytes+",length="+length+","+Test2.Hex.encodeHexStr(headbuf, bytes));

				bytes = inputStream.read(payload, 0, length);
				
				//Log.i(TAG, "bytes="+ bytes+","+Test2.Hex.encodeHexStr(payload, bytes));
				
				byte[] buf = new byte[length+8];
				System.arraycopy(headbuf, 0, buf, 0, 8);
				System.arraycopy(payload, 0, buf, 8, length);
				return buf;				
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		private void init() {
			try {
				checkDataFileConfig(scanType, protocolCode);
				
				inputStream = new FileInputStream(dataFile);		
				int bytes = -1;
//				int seek = 0;
				do {
					bytes = inputStream.read(headbuf, 0, 8);		
//					seek += bytes;
					int type = headbuf[0]<<24 | (headbuf[1]<<16 & 0x00FF0000) | (headbuf[2]<<8 & 0x0000FF00) | (headbuf[3] & 0x000000FF);
					int length = headbuf[4]<<24 | (headbuf[5]<<16 & 0x00FF0000) | (headbuf[6]<<8 & 0x0000FF00) | (headbuf[7] & 0x000000FF);
					if (length > payload.length)
						break;
					bytes = inputStream.read(payload, 0, length);					
					if (type == 1) {
						int scanType_r = payload[0]<<24 | (payload[1]<<16 & 0x00FF0000) | (payload[2]<<8 & 0x0000FF00) | (payload[3] & 0x000000FF);
						int scanId_r = payload[4]<<24 | (payload[5]<<16 & 0x00FF0000) | (payload[6]<<8 & 0x0000FF00) | (payload[7] & 0x000000FF);
						if (scanType == scanType_r) {
							scanId = scanId_r;
							inputStream.close();
							inputStream = new FileInputStream(dataFile); 							
							break;
						}
					}					
				} while(bytes > 0);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void checkDataFileConfig(int scanType, int protocolCode) {
			String filehead = null;
			int rate = 280;
			if (scanType == ScanTypes.eScanType_RssiChannel) {				
				if (protocolCode == ProtocolCodes.PROTOCOL_GSM) {
					filehead = "GSM_RssiData_";		
					rate = (138+145)/2;
				} else if (protocolCode == ProtocolCodes.PROTOCOL_IS_2000_CDMA) {
					filehead = "CDMA_RssiData_";
					rate = (260+280)/2;
				} else if (protocolCode == ProtocolCodes.PROTOCOL_IS_856_EVDO) {
					filehead = "EVDO_RssiData_";	
					rate = (260+280)/2;
				} else if (protocolCode == ProtocolCodes.PROTOCOL_3GPP_WCDMA) {
					filehead = "WCDMA_RssiData_";
					rate = (90+92)/2;
				} else if (protocolCode == ProtocolCodes.PROTOCOL_TDSCDMA) {
					filehead = "TDSCDMA_RssiData_";
					rate = (135+145)/2;
				} else if (protocolCode == ProtocolCodes.PROTOCOL_LTE) {
					filehead = "FDDLTE_RssiData_";
					rate = (55+57)/2;
				} else if (protocolCode == ProtocolCodes.PROTOCOL_TD_LTE) {
					filehead = "TDDLTE_RssiData_";
					rate = (53+55)/2;
				}
				
			} else if(scanType == ScanTypes.eScanType_ColorCode) {
				if (protocolCode == ProtocolCodes.PROTOCOL_GSM) {
					filehead = "GSM_ColorCodeData_";	
					rate = (8+9)/2;
				}
			} else if(scanType == ScanTypes.eScanType_TopNPilot) {
				if (protocolCode == ProtocolCodes.PROTOCOL_IS_2000_CDMA) {
					filehead = "CDMA_TopNPilotData_";
					rate = (25+35)/2;
				} else if (protocolCode == ProtocolCodes.PROTOCOL_IS_856_EVDO) {
					filehead = "EVDO_TopNPilotData_";
					rate = (16+20)/2;
				} else if (protocolCode == ProtocolCodes.PROTOCOL_3GPP_WCDMA) {
					filehead = "WCDMA_TopNPilotData_";
					rate = (35+45)/2;
				} else if (protocolCode == ProtocolCodes.PROTOCOL_TDSCDMA) {
					filehead = "TDSCDMA_TopNPilotData_";
					rate = (35+45)/2;
				}
			} else if(scanType == ScanTypes.eScanType_eTopNSignal) {
				if (protocolCode == ProtocolCodes.PROTOCOL_LTE) {
					filehead = "FDDLTE_ETopNSignalData_";
					rate = (3+5)/2;
				} else if (protocolCode == ProtocolCodes.PROTOCOL_TD_LTE) {
					filehead = "TDDLTE_ETopNSignalData_";
					rate = (7+10)/2;
				}
			}
			
			delayClickMax = 1000 / rate;
			
			if (filehead == null || 
					!Environment.getExternalStorageState().equals(
							android.os.Environment.MEDIA_MOUNTED)) {		
			}
				
			dataFile = getMatchFile(Environment.getExternalStorageDirectory()
					.getAbsolutePath()+"/"+FILE_SAVE_PATH, filehead);
		}
		
		private File getMatchFile(String path, final String filehead) {
			File dir = new File(path);
			File[] files = dir.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return (pathname.isFile() && pathname.getName().startsWith(filehead));
				}
			});
			if (files != null)
				return files[0];		
			return null;
		}
	}
}
