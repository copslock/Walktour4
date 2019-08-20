package com.dingli.seegull;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.dingli.seegull.SeeGullFlags.ScanTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * The service implements all the seegull function.
 * Client will get a interface {@link ISeeGullService} which user called.
 * After {@link ISeeGullService#setMsgHandler(Handler)}, user can receive Message as 
 * {@link #MSG_UPDATE_DEVICE_INFO}, {@link #MSG_TEST_DATA_RECEIVED}
 */
public final class SeeGullService extends Service implements SeeGullUtils.OnEventListener {
	private final static String TAG = "SeeGullService";	
	
	/**
	 *  RSSI Channel Scan -- Scan Type
	 */
	public static final int eScanType_RssiChannel	= ScanTypes.eScanType_RssiChannel; 
	/**
	 *  Color Code Scan -- Scan Type
	 */
	public static final int eScanType_ColorCode		= ScanTypes.eScanType_ColorCode; 
	/**
	 *  TopN Pilot Scan -- Scan Type
	 */
	public static final int eScanType_TopNPilot		= ScanTypes.eScanType_TopNPilot; 
	/**
	 *  Enhance TopN Signal Scan -- Scan Type
	 */
	public static final int eScanType_eTopNSignal	= ScanTypes.eScanType_eTopNSignal;
	
	
	/**
	 * Msg defined for update device info, such as device name, device type, device state.
	 */
	public static final int MSG_UPDATE_DEVICE_INFO = 0x8003;	
	/**
	 * Msg defined for received socket packet.
	 */
	public static final int MSG_TEST_DATA_RECEIVED = 0x8004;	
	/**
	 * Msg defined for debug.
	 */
	public static final int MSG_SHOWLOG = 0x8888;	
	
	/**
	 * Retval as succeed for calling ibinder func.
	 */	
	public final static int RUN_CMD_SUCCEED = 0;	
	/**
	 * Retval as unknown error for calling ibinder func.
	 */
	public final static int RUN_CMD_UNNKNOWN_ERROR = -1;	
	
	/**
	 * Instance of SeeGullUtils, only one; 
	 */
	private SeeGullUtils mUtils;
	/**
	 * Client handler for receive msg;
	 */
	private Handler mClientMsgHandler;

	@Override
    public void onCreate() {
        super.onCreate();   
        mUtils = new SeeGullUtils(this);	
		mUtils.setOnEventListener(this);
		//mUtils.init();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        int retval = super.onStartCommand(intent, flags, startId);
        return retval;
	}	
	
	@Override
	public void onDestroy() {		
		mUtils.release();
		super.onDestroy();
	}
	
	
	@Override
    public IBinder onBind(Intent intent) {
        return mServiceBinder;
    }
	
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	
	/**
	 * Define the interface for user calling after bind {@link SeeGullService}
	 * If wanted to receive the msg {@link SeeGullService#MSG_UPDATE_DEVICE_INFO} 
	 * & {@link SeeGullService#MSG_TEST_DATA_RECEIVED}, 
	 * please call {@link #setMsgHandler(Handler)} after binded;
	 * Check the device state before calling 
	 * {@link #init()}, 
	 * {@link #startScan(int, Object)},
	 * {@link #stopScan(int)} & 
	 * {@link #releaseSeeGull()}
	 */
	public interface ISeeGullService {
		/**
		 * Set Handler for handling msg {@link SeeGullService#MSG_UPDATE_DEVICE_INFO} 
		 * & {@link SeeGullService#MSG_TEST_DATA_RECEIVED}
		 * @param handler
		 */
		public abstract void setMsgHandler(Handler handler);	
		/**
		 * Init the SeeGull service and try to connect the scanner.
		 * @return  0   succeed, others fail;
		 */
		public abstract int init(boolean simulate);
		/**
		 * Send a scan request to scanner.
		 * @param scanType the type which enumlate as {@link SeeGullFlags#ScanTypes}
		 * @param scanParams the parameters needed by scan reques.
		 * @return  scanId, succeed as >= 0, others fail;
		 */
		public abstract int startScan(Object scanParams);
		/**
		 * Stop a scan request to scanner.
		 * @param scanId the retval of {@link #startScan(Object params)}, 
		 * -1 for stop all running scan requests.
		 * @return  succeed as 0, others fail;
		 */
		public abstract int stopScan(int scanId);
		/**
		 * release the SeeGull scanner.
		 * @return  0   succeed, others fail;
		 */
		public abstract int releaseSeeGull();
    }
	
	private ServiceBinder mServiceBinder = new ServiceBinder();
	
    public class ServiceBinder extends Binder implements ISeeGullService {  
    	
		@Override
		public int init(boolean simulate) {
			mUtils.init(simulate);
			return RUN_CMD_SUCCEED;
		}

		@Override
		public int startScan(Object params) {			
			return mUtils.startScan(params);
		}

		@Override
		public int stopScan(int scanId) {
			mUtils.stopScan(scanId);
			return RUN_CMD_SUCCEED;
		}

		@Override
		public void setMsgHandler(Handler handler) {
			mClientMsgHandler = handler;
		}

		@Override
		public int releaseSeeGull() {
			mUtils.release();
			return RUN_CMD_SUCCEED;
		}		
    }	
    
    @Override
	public void onDataReceive(byte[] data) {
    	if (mClientMsgHandler != null) {
			Message msg = mClientMsgHandler.obtainMessage(MSG_TEST_DATA_RECEIVED);
			msg.obj = data;
			mClientMsgHandler.sendMessage(msg);
		}
    	
    	Log.w(TAG,"--data:" + data.length);
	}
	
	@Override
	public void showDebugInfo(String info) {
		if (mClientMsgHandler != null) {
			Message msg = mClientMsgHandler.obtainMessage(MSG_SHOWLOG);
			msg.obj = info;
			mClientMsgHandler.sendMessage(msg);
		}			
	}

	@Override
	public void onUpdateDeviceInfo(Map<String, Object> info) {
		if (mClientMsgHandler != null) {
			Message msg = mClientMsgHandler.obtainMessage(MSG_UPDATE_DEVICE_INFO);
			msg.obj = info;		
			mClientMsgHandler.sendMessage(msg);			
		}
	}
	
	/**
	 * Device information shared for user, when received the {@link #MSG_UPDATE_DEVICE_INFO}, 
	 * read this field to update device information.
	 * @see The Information of Map as KEY-TYPE-VALUE:
	 * @see DEVICE_NAME-String-"IBflex-ESN-0000-0000-8140-7045";
	 * @see DEVICE_TYPE-String-"Bluetooth";
	 * @see DEVICE_STATE-String-(as "DISCONNECTED", "CONNECTING", "CONNECTED");
	 * @see SCAN_INFO_LIST-List<\Map<\String, Integer>>-(Optional. Contains SCAN_ID, SCAN_TYPE, SCAN_STATUS)
	 */
	public Map<String, Object> mScannerDeviceInfo = new HashMap<String, Object>();
	
}
