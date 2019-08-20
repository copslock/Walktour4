package com.dingli.seegull.setupscan;

import com.dingli.seegull.SeeGullFlags;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * JSON_Key 	Type	 Description
 * SCAN_ID		int		 Scan Id unique to individual scan.
 * 						 Value: 0 - 254
 * SCAN_MODE	int		 Scan mode value.
 * 						 Value:0 - Auto;3 - Auto with SD recording
 * CHANNEL_LIST JSONObject Channel List JSON Object
 */
public class SetupRssiScan extends SetupScanBase {		
	/**
	 * @param scanId 0-254, unique
	 * @param scanMode 0--auto; 3--auto with sdcard recording
	 * @param protocolCode define as {@link SeeGullFlags#ProtocolCodes}
	 * @param bandCode define as {@link SeeGullFlags#BandCodes}
	 */
	public SetupRssiScan(int scanId, int scanMode, int protocolCode, int bandCode) {
		super(scanId, scanMode, protocolCode, bandCode);
	}

	@Override
	public JSONObject genScanRequestBody() throws JSONException {		
		  JSONObject jsonObject = new JSONObject();
		  
		  jsonObject.put("SCAN_ID", mScanId);
		  jsonObject.put("SCAN_MODE", mScanMode); // auto  
		  jsonObject.put("CHANNEL_LIST", setupChannelList());
		  
		  return jsonObject;
	}
}
