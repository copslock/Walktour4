package com.dingli.seegull.setupscan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * SCAN_ID			int		Scan Id unique to individual scan.
 * 					Value: 0 - 254
 * SCAN_MODE		int	Scan mode value.
 * 				Value: 
 * 				0 - Auto
 * 				3 - Auto with SD recording
 * CHANNEL_LIST		JSON 	Object	Channel List JSONObject

 * DATA_MODE_LIST	JSON Array (type of int)	Array of data modes
 * RSSI_THRESHOLD	double	RSSI threshold
 * MULTIPLE_COLOR_CODE	boolean	Enable Multiple color code flag
 * COLLECTION_MODE	int	Optional. Collection Mode
 * CONTROL_CHANNEL_MESSAGES	int	Optional. Control channel messages
 * DWELLING_TIME	int	Optional.Dwelling time
 * 		0 - default set to 204 frames (4 multiframes)
 * 		x - translate to 12 + x frames
 */
public class SetupColorCodeScan extends SetupScanBase {
	
	private final static int DATA_MODE_COLOR_CODE = 0x0002;
	private final static int DATA_MODE_C_I = 0x0200;	
	private final static int DATA_MODE_L3_MSG = 0x4000;
	
	private double  mRssiThreshold = -120.0f;
	private boolean mMultipleColorCodeEnable = false;
	private boolean mIsColorCode = true;
	private boolean mIsCI = true;
	private boolean mIsL3Msg = false;
	
	public SetupColorCodeScan(int scanId, int scanMode, int protocolCode,
			int bandCode) {
		super(scanId, scanMode, protocolCode, bandCode);
	}
	
	public void setRssiThreshold(double threshold) {
		mRssiThreshold = threshold;
	}
	
	public void setMultipleColorCodeEnable(boolean enanble) {
		mMultipleColorCodeEnable = enanble;
	}
	
	public void setColorCodeMode(boolean isColorCode) {
		mIsColorCode = isColorCode;
	}
	
	public void setCIMode(boolean isCI) {
		mIsCI = isCI;
	}
	
	public void setL3MsgMode(boolean isL3Msg) {
		mIsL3Msg = isL3Msg;
	}
	
	//Optional parameters
	private boolean mCollectionModeEnable = false;
	private int 	mCollectionMode = 0;
	private boolean mControlChannelMessagesEnable = false;
	private int 	mControlChannelMessages = 0;
	private boolean mDwellingTimeEnable = false;
	private int 	mDwellingTime = 0;//0 - default set to 204 frames (4 multiframes), x - translate to 12 + x frames
	
	public void setCollectionMode_Optional(int mode) {
		mCollectionMode = mode;
		mCollectionModeEnable = true;
	}
	public void setControlChannelMessages_Optional(int msgs) {
		mControlChannelMessages = msgs;
		mControlChannelMessagesEnable = true;
	}
	public void setDwellingTime_Optional(int dwellingTime) {
		mDwellingTime = dwellingTime;
		mDwellingTimeEnable = true;
	}		

	/** 
	 * @return the json object contain the request body.
	 * @throws JSONException 
	 */
	
	@Override
	public JSONObject genScanRequestBody()
			throws JSONException {
		
		JSONObject jsonObject = new JSONObject(); 
		jsonObject.put("SCAN_ID", mScanId);
		jsonObject.put("SCAN_MODE", mScanMode); // auto
		jsonObject.put("CHANNEL_LIST",setupChannelList());
		jsonObject.put("RSSI_THRESHOLD", mRssiThreshold);
		jsonObject.put("MULTIPLE_COLOR_CODE", mMultipleColorCodeEnable);
		 
		JSONArray dataModeArray = new JSONArray();
		if (mIsColorCode)
			dataModeArray.put(DATA_MODE_COLOR_CODE);
		if (mIsCI)
			dataModeArray.put(DATA_MODE_C_I);
		if (mIsL3Msg)
			dataModeArray.put(DATA_MODE_L3_MSG);
		jsonObject.put("DATA_MODE_LIST", dataModeArray);
		
		//Optional parameters
		if (mCollectionModeEnable)
			jsonObject.put("COLLECTION_MODE", mCollectionMode);
		if (mControlChannelMessagesEnable)
			jsonObject.put("CONTROL_CHANNEL_MESSAGES", mControlChannelMessages);
		if (mDwellingTimeEnable)
			jsonObject.put("DWELLING_TIME", mDwellingTime);
		
		return jsonObject;
	}
}
