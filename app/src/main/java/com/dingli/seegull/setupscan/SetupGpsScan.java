package com.dingli.seegull.setupscan;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;
/*
 * APPLICATION_ID	int	Application Id return from Initialize Service command.
 * DEVICE_ID	int	Scanner device Id.
 * REQUEST_PARAMETERS	String	Request parameters in JSON string.
 * 
 * Request Parameters
 * DATA_REPORT_MODE	int	Data report mode
 * 		Value:
 * 		0 - Once (Data reported only once)
 * 		1 - Auto (Data reported automatically whenever new value is available)
 * 		2 - Stop (Data reporting is stopped)
 */
public class SetupGpsScan {	 
	private int mAppId = -1;
	private int mDeviceId = -1;
	public SetupGpsScan(int appId, int deviceId) {
		this.mAppId = appId;
		this.mDeviceId = deviceId;
	}
	public void genScanRequestBody() throws JSONException {
		// send scanner inquiry request
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("DATA_REPORT_MODE", 1);  // auto
		 
		// create bundle object
		Bundle bundle = new Bundle();

		bundle.putInt("APPLICATION_ID", mAppId);
		bundle.putInt("DEVICE_ID", mDeviceId);
		bundle.putString("REQUEST_PARAMETERS", jsonObject.toString());		 
		
		//sendServiceMessage(SeeGullUtils.MSG_GPS_REQUEST, bundle);
	}
	
	public JSONObject setupScanParameters(int dataReportMode) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("DATA_REPORT_MODE", dataReportMode);
		return jsonObject;
	}
	
	

}
