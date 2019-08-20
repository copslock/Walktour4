package com.dingli.seegull.setupscan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SetupBlindScan extends SetupScanBase {	

	private int mNumberOfIds = 1 ;
	
	public SetupBlindScan(int scanId, int scanMode, int protocolCode, int bandCode) {
		super(scanId, scanMode, protocolCode, bandCode);		
	}
	
	public boolean setNumberOfIds(int number) {
		//if (protocolCode == PROTOCOL_GSM) mNumberOfIds = 1;
		mNumberOfIds = number;
		return true;
	}

	/**
	 * NETTYPE	int	Protocol Code (refer to Protocol Codes)
	 * NUMBER_OF_IDS	int	Number of IDs, WCDMA/CDMA/EVDO - Number of Pilots, LTE/TD-LTE - Number of Cell Ids, GSM - Must be 1
	 * BLIND_SCAN_REQUEST_BAND_ELEMENT_LIST	JSON Array (type of Blind Scan Request Band Element), Optional, Array of Blind Scan Request Band Element
	 * DATA_MODE_LIST	JSON Array (type of int)	Optional, Array of data modes
	 * THRESHOLD_LIST	JSON Array ( type of Threshold), Optional, Array of Threshold
	 * ACQUISITION_PERIOD	int	Optional, Acquisition period in minutes
	 * RESPONSE_MODE	int	Optional, Response mode
	 * 		0 - return channels
	 * 		1 - return frequencies
	 * @return
	 * @throws JSONException 
	 */
	// setup blind scan request element
	public JSONObject setupBlindScanRequestElement() throws JSONException
	{
	  JSONObject jsonObject = new JSONObject();
	 
	  jsonObject.put("NETTYPE", mProtocolCode); //1 GSM
	  jsonObject.put("NUMBER_OF_IDS", mNumberOfIds);
	  
	  JSONArray elementArray = new JSONArray();
	  elementArray.put(setupBlindScanRequestBandElement(mBandCode));
	  jsonObject.put("BLIND_SCAN_REQUEST_BAND_ELEMENT_LIST", elementArray);
	 
	  JSONArray dataModeArray = new JSONArray();
	  dataModeArray.put(0x002); //0x002 color code
	  jsonObject.put("DATA_MODE_LIST", dataModeArray);
	 
	  return jsonObject;
	}
	/**
	 * 
	 * BAND_CODE	int	Band Code (refer to Band Codes)
	 * CHANNELIZATION	int	Optional, Channelization mode, Future enhancement to support all channelization
	 * CHANNEL_STYLE	int	Optional, Channel style code,Future enhancement for LTE channel style auto detection

	 * @return
	 * @throws JSONException 
	 */
	// setup blind scan request band element
	public JSONObject setupBlindScanRequestBandElement(int bandCode) throws JSONException {
	  JSONObject jsonObject = new JSONObject();
	 
	  jsonObject.put("BAND_CODE", bandCode); // 512 PCS
	 
	  return jsonObject;
	}

	/**
	 * SCAN_ID	int	Scan Id unique to individual scan.
	 * 		Value: 0 - 254
	 * SCAN_MODE	int	Scan mode value.
	 * 		Value:
	 * 		0 - Auto
	 * 		3 - Auto with SD recording
	 * BLIND_SCAN_REQUEST_ELEMENT_LIST	JSON Array (type of Blind Scan Request Element) Array of Blind Scan Request Element
	 * 
	 * @return
	 * @throws JSONException 
	 */
	
	@Override
	public JSONObject genScanRequestBody()
			throws JSONException {
		// TODO Auto-generated method stub
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("SCAN_ID", mScanId);
		jsonObject.put("SCAN_MODE", mScanMode); // 0 auto
		 
		JSONArray elementArray = new JSONArray();
		elementArray.put(setupBlindScanRequestElement());
		jsonObject.put("BLIND_SCAN_REQUEST_ELEMENT_LIST", elementArray);
		 
		return jsonObject;
	}
}
