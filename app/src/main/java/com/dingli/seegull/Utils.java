package com.dingli.seegull;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import com.dingli.seegull.ScanModelAdapter.ScanRequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Utils {
//	private final static String TAG = Utils.class.getSimpleName();
	
	public static final int APK_TOOL_VERSION = 1000;
	
	public static byte[] createDataFrame(byte[] buf, int length) {
		if (buf == null || buf.length <= 0 || length <= 0) 
			return null;
		if (length > buf.length)
			length = buf.length;
		byte[] tempbuf = new byte[length+8];
		tempbuf[0]= 0;
		tempbuf[1]= 0;
		tempbuf[2]= 0;
		tempbuf[3]= 0;						
		tempbuf[4]=(byte)(length>>>24);
		tempbuf[5]=(byte)(length>>>16);
		tempbuf[6]=(byte)(length>>>8);
		tempbuf[7]=(byte)(length>>>0);
		System.arraycopy(buf, 0, tempbuf, 8, length);
		return tempbuf;
	}
	public static byte[]  createConfigFrame(ScanRequestParams params) {
		if (params.configAmount < 3) {
			return null;
		}
		int buflen = params.configAmount*4;
		byte[] buf = new byte[buflen+8];
		int buftype = 1;

		buf[0]=(byte)(buftype>>>24);
		buf[1]=(byte)(buftype>>>16);
		buf[2]=(byte)(buftype>>>8);
		buf[3]=(byte)(buftype>>>0);
		buf[4]=(byte)(buflen>>>24);
		buf[5]=(byte)(buflen>>>16);
		buf[6]=(byte)(buflen>>>8);
		buf[7]=(byte)(buflen>>>0);
		buf[8]=(byte)(params.scanType>>>24);
		buf[9]=(byte)(params.scanType>>>16);
		buf[10]=(byte)(params.scanType>>>8);
		buf[11]=(byte)(params.scanType>>>0);
		buf[12]=(byte)(params.scanId>>>24);
		buf[13]=(byte)(params.scanId>>>16);
		buf[14]=(byte)(params.scanId>>>8);
		buf[15]=(byte)(params.scanId>>>0);
		buf[16]=(byte)(APK_TOOL_VERSION>>>24);
		buf[17]=(byte)(APK_TOOL_VERSION>>>16);
		buf[18]=(byte)(APK_TOOL_VERSION>>>8);
		buf[19]=(byte)(APK_TOOL_VERSION>>>0);
		if (params.configAmount >= 4) {
			buf[20]=(byte)(params.bandCode>>>24);
			buf[21]=(byte)(params.bandCode>>>16);
			buf[22]=(byte)(params.bandCode>>>8);
			buf[23]=(byte)(params.bandCode>>>0);
		}
		if (params.configAmount >= 5) {
			buf[24]=(byte)(params.protocolCode>>>24);
			buf[25]=(byte)(params.protocolCode>>>16);
			buf[26]=(byte)(params.protocolCode>>>8);
			buf[27]=(byte)(params.protocolCode>>>0);
		}
		return buf;
	}
//	public String getScanTypeString(int scanType) {
//		String retval;
//		if (scanType == ScanTypes.eScanType_RssiChannel)
//			retval = "RSSI";
//		else if (scanType == ScanTypes.eScanType_RssiFrequency)		
//			retval = "RSSI";			
//		else if (scanType == ScanTypes.eScanType_EnhancedPowerScan)		
//			retval = "ENHANCED_POWER_SCAN";
//		else if (scanType == ScanTypes.eScanType_TopNPilot)		
//			retval = "TOP_N_PILOT";
//		else if (scanType == ScanTypes.eScanType_ColorCode)		
//			retval = "COLOR_CODE";
//		else if (scanType == ScanTypes.eScanType_eTopNSignal)		
//			retval = "ENHANCED_TOP_N_SIGNAL";
//		else if (scanType == ScanTypes.eScanType_BlindScan)		
//			retval = "BLIND_SCAN";
//		else if (scanType == ScanTypes.eScanType_WiFiThroughput)		
//			retval = "WIFI_THROUGHPUT";
//		else 
//			retval = null;
//		return retval;
//	}
	
	public static String getIntentInfo(Intent intent) {
		String retval;
		if (intent == null) {
			retval = "intent == null";
			return retval;
		}
		Bundle b = intent.getExtras();
		if (b == null) {
			retval = "intent->action="+intent.getAction()+", Ext=null";
			return retval;
		}
		retval = "intent->action="+intent.getAction();
		
		Set<String> keys = b.keySet();
		for(String key :keys) {
			Object obj = b.get(key);
			String type = obj.getClass().getName();
			
			if (type.equals("java.lang.String")) {
				retval += "\nintent->"+key+" = "+b.getString(key)
						+" [As "+obj.getClass().getName()+"]";
			} else if (type.equals("java.lang.Long")) {
				retval += "\nintent->"+key+" = "+b.getLong(key)
						+" [As "+type+"]";
			} else if (type.equals("java.lang.Integer")) {
				retval += "\nintent->"+key+" = "+b.getInt(key)
						+" [As "+type+"]";
			} else if (type.equals("java.lang.Boolean")) {
				retval += "\nintent->"+key+" = "+b.getBoolean(key)
						+" [As "+type+"]";
			} else {
				retval += "\nintent->"+key+" = "+b.get(key)
						+" [As "+type+"]";
			}
		}
		
		//将字符打印到文件待查	 
		boolean out = false;
		if (out)
	    try {
	    	String filename = Environment.getExternalStorageDirectory().getAbsolutePath() +"/Scanner.log";
	    	File file = new File(filename);
	    	if (!file.exists())
		    	file.createNewFile();	
		    FileOutputStream fos = new FileOutputStream(file, true);
		    fos.write(retval.getBytes());
		    String s = "\n\n";
		    fos.write(s.getBytes());
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
		return retval;
	}
	private static List<Object> parseJSONOArrayToList(JSONArray array) {
		if (array == null || array.length() <= 0)
			return null;
		List<Object> list = new ArrayList<Object>();		
		for (int i=0; i<array.length(); i++) {
			try {
				Object obj = array.get(i);
				if (obj instanceof JSONObject) {
					Map<String, Object> map = parseJSONObjectToMap((JSONObject)obj);
					list.add(map);
				} else {
					list.add(obj);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}		
		return list;
	}
	private static Map<String, Object> parseJSONObjectToMap(JSONObject obj) {
		if (obj == null)
			return null;
		JSONArray names = obj.names();
		if (names == null || names.length() <= 0)
			return null;
		
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i=0; i<names.length();i++) {
			try {
				String name = (String) names.get(i);
				Object subobj = obj.get(name);
				if (subobj instanceof JSONObject) {
					map.put(name, parseJSONObjectToMap((JSONObject)subobj));
				} else if (subobj instanceof JSONArray) {
					map.put(name, parseJSONOArrayToList((JSONArray) subobj));
				} else {
					map.put(name, subobj);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}			
		return map;		
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> parseJSONToMap(String str) {
		if (str == null || str.trim().equals(""))
			return null;
		
		JSONTokener jsonTokener = new JSONTokener(str);  
		
		Map<String, Object> map = null;//new HashMap<String, Object>();
		while (jsonTokener.more()) {			
			Object obj = null;
			try {
				obj = jsonTokener.nextValue();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			if (obj != null) {
				if (obj instanceof JSONObject) {
					Map<String, Object> temp_map = parseJSONObjectToMap((JSONObject)obj);
					return temp_map;
				} else if (obj instanceof JSONArray) {
					List<Object> list = parseJSONOArrayToList((JSONArray)obj);
					if (list != null && list.size() > 0) {
						if (list.size() == 1) {
							Object objc = list.get(0);
							if (objc instanceof Map<?,?>) {
								return (Map<String, Object>)objc;
							}
							map = new HashMap<String, Object>();
							map.put("DEFAULT", objc);
							return map;
						}
						map = new HashMap<String, Object>();
						map.put("DEFAULT", list);
						return map;
					}
				}
			}
		}
		return null;
	}	
	
//	@SuppressWarnings("unchecked")
//	public static void printfJsonMap(Map<String, Object> map) {
//		if (map == null || map.size() <= 0) {
//			Log.i(TAG, "map == null");
//			return ;
//		}
//		
//		Object[] keys = (map.keySet().toArray());
//		for (Object key : keys) {
//			Object obj = map.get(key);
//			if (obj instanceof Map<?,?>) {
//				printfJsonMap((Map<String, Object>)obj);
//			} else if (obj instanceof List<?>) {
//				List<Object> list = (List<Object>)obj;
//				String s = "";
//				for (int i=0; i<list.size(); i++)
//					s += "'"+(String)list.get(i)+"',";
//				
//				Log.i(TAG, "map("+key+")=["+ s+"]");
//			} else {
//				Log.i(TAG, "map("+key+")="+ obj);
//			}
//		}
//		
//	}
	
	/*
	 * RESPONSE_TYPE	String	Response type.
	 * Value MUST be CONTROL for inquiry control response
	 * CONTROL_RESPONSE_TYPE	String	Control response type
	 * Value MUST be LlCENSE_INQUIRY for inquiry response
	 * LICENSE_INQUIRY_RESPONSE	JSON Object	License inquiry parameters information
	 * 
	 * License inquiry parameters information
	 * STATUS_CODE	int	Scanner status code (refer to Scanner Status Codes)
	 * STATUS_MESSAGE	String	Status Message
	 * ESN	long	Electronic Serial Number
	 * CURRENT_TIME 	int	Current time in UTC time in seconds
	 * WARRANTY_EXP_TIME 	int	Warranty expiration time in UTC time in seconds
	 * LICENSE_NUMBER 	int	License number of latest license
	 * LICENSE_MODEL 	int	License model
	 * 		0 - Rental
	 * 		1 - Pay-Per-Use
	 * 		3 - Own
	 * LICENSE_STATE 	int	license state
	 * 		0 - Active Normal
	 * 		3 - Expired
	 * EXPIRATION_TIME 	int	Latest license expiration time in UTC time in seconds
	 * LICENSE_BALANCE 	JSON Object	License Balance JSON Object
	 * CALIBRATION_INFORMATION 	JSON Object	Calibration information JSON Object
	 */
	/**
	 * 	 
	 * @param jsonString
	 * @throws JSONException 
	 */	
	
//	private void parseScannerLicenseInquiryRequestResponse(String jsonString) throws JSONException {
//		 
//		JSONObject jsonObject = new JSONObject(jsonString );
//		String responseType = jsonObject.getString("RESPONSE_TYPE");
//		 
//		// validate is control response
//		if (responseType.equals("CONTROL"))
//		{
//		  // validate is license inquiry response
//		  String ctr_responseType = jsonObject.getString("CONTROL_RESPONSE_TYPE");
//		  if(ctr_responseType.equals("LICENCE_INQUIRY"))
//		  {
//		    JSONObject licenseInqResoJson= jsonObject.getJSONObject("LICENSE_INQUIRY_RESPONSE");
//		 
//		    // extract license inquiry information
//		    long status  = licenseInqResoJson.getLong("STATUS");
//		    long esn  = licenseInqResoJson.getLong("ESN");
//		    long currentTime  = licenseInqResoJson.getLong("CURRENT_TIME"); 
//		    long warrentyExpTime  = licenseInqResoJson.getLong("WARRENTY_EXP_TIME"); 
//		    long licenseNumber  = licenseInqResoJson.getLong("LICENSE_NUMBER"); 
//		    long licenseModel  = licenseInqResoJson.getLong("LICENSE_MODEL"); 
//		    long licenseState  = licenseInqResoJson.getLong("LICENSE_STATE"); 
//		    long expirationTime  = licenseInqResoJson.getLong("EXPIRATION_TIME"); 
//		 
//		    JSONObject licenseBalanceJSONObject  =  licenseInqResoJson.getJSONObject("LICENSE_BALANCE"); 
//		    getLicenseBalance(licenseBalanceJSONObject); 
//		    
//		    JSONObject calibrationInfoJSONObject  =  licenseInqResoJson.getJSONObject("CALIBRATION_INFORMATION"); 
//		    getCalibrationInfo(calibrationInfoJSONObject); 
//		   }
//		}
//	}
	
	/*
	 * DAYS	int	Number of days
	 * HOURS	int	Number of hours
	 * MINUTES	int	Number of minutes
	 */
	/**
	 * @param jsonObject
	 * @throws JSONException 
	 */
	
//	private void getLicenseBalance(JSONObject jsonObject) throws JSONException 	{ 
//		int days = jsonObject.getInt("DAYS"); 
//		int hours= jsonObject.getInt("HOURS"); 
//		int minutes = jsonObject.getInt("MINUTES"); 
//	}
	
	// get calibration information 
//	private void getCalibrationInfo(JSONObject jsonObject) throws JSONException {
//		int version = jsonObject.getInt("VERSION");  
//		int year = jsonObject.getInt("YEAR"); 
//		int month = jsonObject.getInt("MONTH"); 
//		int day = jsonObject.getInt("DAY"); 
//	}
	
	
	/*
	 * JSONKEY
	 * RESPONSE_TYPE	String	Response type.Value MUST be CONTROL for inquiry control response
	 * CONTROL_RESPONSE_TYPE	String	Control response type Value MUST be INQUIRY for inquiry response
	 * INQUIRY_RESPONSE	JSONObject	Inquiry parameters information
	 * 
	 * Inquiry parameters
	 * STATUS_CODE	int	Scanner status code (refer to Scanner Status Codes)
	 * STATUS_MESSAGE	String	Status MessageUNIT_INFORMATION	JSON Object	Unit Information JSON Object
	 * PROTO_BAND_CONFIG_LIST	JSONArray (type of Protocol Band Configuration) Array of Protocol Band Configuration
	 * OPTION_LIST	JSONArray (type of Option)Array of Option
	 * USE_HEADERS	Boolean	Optional.Use headers flag
	 */
	/**
	 * @param 
	 * @throws JSONException 
	 */	
//	private void parseScannerInquiryRequestResponse(String jsonString) throws JSONException {
//		JSONObject jsonObject = new JSONObject(jsonString );
//		String responseType = jsonObject.getString("RESPONSE_TYPE");
//		 
//		// validate is control response
//		if (responseType.equals("CONTROL"))
//		{
//		  // validate is inquiry response
//		  String ctr_responseType = jsonObject.getString("CONTROL_RESPONSE_TYPE");
//		  if(ctr_responseType.equals("INQUIRY"))
//		  {
//		    JSONObject inquiryRespJson = jsonObject.getJSONObject("INQUIRY_RESPONSE");
//		 
//		    // extract inquiry information
//		    int status = inquiryRespJson.getInt("STATUS_CODE");
//		    boolean useHeaders = inquiryRespJson.optBoolean("USE_HEADERS");
//		 
//		    JSONObject unitInfoJson = inquiryRespJson.getJSONObject("UNIT_INFORMATION");
//		    getUnitInformation(unitInfoJson);
//		 
//		    JSONArray protocolBandConfigJsonArray = inquiryRespJson.getJSONArray("PROTO_BAND_CONFIG_LIST");
//		    getProtocolBandConfig(protocolBandConfigJsonArray);
//		 
//		    JSONArray optionJsonArray = inquiryRespJson.getJSONArray("OPTION_LIST");
//		    getOption(optionJsonArray);
//		  }
//		}
//	}
	/*
	 * STATUS_CODE	int	Scanner Status code (refer to Scanner Status Codes)

	 * STATUS_MESSAGE	String	Status Message
	 * DEVICE_TYPE	int	Device Type
	 * ESN	long	Electronic Serial Number
	 * COM_INTERFACE	JSON Array (type of int)	Array of communication interfaces
	 *		Values:
	 *		0 - USB
	 *		1 - RS232
	 *		2 - Ethernet
	 *		3 - Bluetooth
	 * PRODUCT_REFERENCE_VERSION	String	Product Reference version number
	 * API_VERSION	String	API version number
	 * SOFTWARE_RELEASE_VERSION	String	Software Release version number
	 * ASN1_VERSION	String	ASN.1 version number
	 * CALIBRATION_DATE	String	Calibration date Format: YYYY/MM/DD
	 */
	/**
	 * @param jsonObject
	 */
//	private void getUnitInformation(JSONObject jsonObject) {
//		try {
//			int status = jsonObject.getInt("STATUS_CODE");
//			  int deviceType = jsonObject.getInt("DEVICE_TYPE");
//			  long esn = jsonObject.getLong("ESN");
//			  String productReferenceVersion = jsonObject.getString("PRODUCT_REFERENCE_VERSION");
//			  String apiVersion = jsonObject.getString("API_VERSION");
//			  String softwareReleaseVersion = jsonObject.getString("SOFTWARE_RELEASE_VERSION");
//			  String asn1Version = jsonObject.getString("ASN1_VERSION");
//			  String calibrationDate= jsonObject.getString("CALIBRATION_DATE");
//			 
//			  JSONArray comInterfaceJsonArray = jsonObject.getJSONArray("COM_INTERFACE");
//			  int count = comInterfaceJsonArray.length();
//			  int[] comInterfaces = new int[count];
//			  for (int i = 0; i < count; i++)
//			  {
//			    comInterfaces[i] = comInterfaceJsonArray.getInt(i);
//			  }
//		} catch (JSONException e) {
//			
//		}	  
//	}
	/*
	 * PROTOCOL_CODE	int	Protocol Code (refer to Protocol Codes)
	 * BAND_CODE	int	Band Code (refer to Band Codes)
	 * ANTENNA	int	Optional.
	 * Antenna port number
	 */
	/**
	 * get protocol band configuration
	 * @param jsonArray
	 */
	// 
//	private void getProtocolBandConfig(JSONArray jsonArray)	{
//		int count = jsonArray.length();
//		for (int i = 0; i < count; i++) {
//			  try {
//				  JSONObject jsonObject = jsonArray.getJSONObject(i);
//					 
//				    int protocol= jsonObject.getInt("PROTOCOL_CODE");
//				    int band= jsonObject.getInt("BAND_CODE");
//				 
//				    if (jsonObject.has("ANTENNA"))  {
//				      int antenna= jsonObject.getInt("ANTENNA");
//				    }
//		    
//			  } catch (JSONException e) {
//				  
//			  }
//		  }  
//	}

	// get option list
//	private void getOption(JSONArray jsonArray) {
//		int count = jsonArray.length();
//		for (int i = 0; i < count; i++) {
//			try {
//				JSONObject jsonObject = jsonArray.getJSONObject(i);
//				
//			    int option = jsonObject.getInt("OPTION_CODE");
//			 
//			    if (jsonObject.has("PROTOCOL_CODE")) {
//			      int protocol= jsonObject.getInt("PROTOCOL_CODE");
//			    }
//			 
//			    if (jsonObject.has("BAND_CODE")) {
//			      int band= jsonObject.getInt("BAND_CODE");
//			    }			
//			} catch (JSONException e) {				  
//			}
//		 }	 	  
//	}
	
	/*
	 * RESPONSE_TYPE	String	Response type.
	 * Value MUST be CONTROL for stop scan control response
	 * CONTROL_RESPONSE_TYPE	String	Control response type
	 * Value MUST be STOP_SCAN for stop scan response
	 * STOP_SCAN_RESPONSE	JSONObject	Stop scan parameters information
	 * 
	 * Stop scan parameters information
	 * STATUS_CODE	int	Scanner status code (refer to Scanner Status Codes)
	 * STATUS_MESSAGE	String	Status Message
	 * REMAINING_SLOTS	int	Remaining slots
	 */
	/**
	 * @param jsonString
	 */
//	private void parseStopScanResponse(String jsonString) {	
//		try {
//			JSONObject jsonObject = new JSONObject(jsonString );
//			String responseType = jsonObject.getString("RESPONSE_TYPE");
//			// validate is control response
//			if (responseType.equals("CONTROL"))
//			{
//			  // validate is stop scan response
//			  String ctr_responseType = jsonObject.getString("CONTROL_RESPONSE_TYPE");
//			  if(ctr_responseType.equals("STOP_SCAN")) {
//			    JSONObject stopScanRespJson = jsonObject.getJSONObject("STOP_SCAN_RESPONSE");
//			 
//			    // validate response
//			    int status = stopScanRespJson.getInt("STATUS_CODE");
//			  }
//			}		 
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}	
//	}	
	
	/*
	 * RESPONSE_TYPE	String	Response type.
	 * Value MUST be SCAN for scan response
	 * SCAN_RESPONSE	JSONObject	Scan parameters information
	 * 
	 * Scan parameters information
	 * STATUS_CODE	int	Scanner status code (refer to Scanner Status Codes)
	 * STATUS_MESSAGE	String	Status Message
	 * SCAN_ID	int	Scan Id
	 */
	/** 
	 * @param jsonString
	 * @throws JSONException 
	 */
//	private void parseScanResponse(String jsonString) throws JSONException {
//		 
//		JSONObject jsonObject = new JSONObject(jsonString );
//		String responseType = jsonObject.getString("RESPONSE_TYPE");
//		 
//		// validate is scan response
//		if (responseType.equals("SCAN")) {
//		  JSONObject scanRespJson = jsonObject.getJSONObject("SCAN_RESPONSE");
//		 
//		  // extract inquiry information
//		  int status = scanRespJson.getInt("STATUS_CODE");
//		  int scanId = scanRespJson.getInt("SCAN_ID");
//		}	
//	}	
	
	/**
	 * 
	 * RESPONSE_TYPE	String	Response type.
	 * 		Value MUST be "GPS" for GPS response
	 * GPS_RESPONSE	JSON Object	GPS response data
	 * 
	 * Inquiry Parameters
	 * STATUS_CODE	int	Scanner status code (refer to Scanner Status Codes)
	 * STATUS_MESSAGE	String	Status Message
	 * 
	 * @param jsonString
	 * @throws JSONException 
	 */
//	public void parseGpsScanResponse(String jsonString) throws JSONException {		 
//
//		JSONObject jsonObject = new JSONObject(jsonString );
//		String responseType = jsonObject.getString("RESPONSE_TYPE");
//		// validate is control response
//		if (responseType.equals("GPS"))
//		{
//		  JSONObject gpsRespJson = jsonObject.getJSONObject("GPS_RESPONSE");
//		 
//		  int statusCode = gpsRespJson.getInt("STATUS_CODE");
//		  String statusMessage = gpsRespJson.getString("STATUS_MESSAGE"); 
//		  int dataReportMode = gpsRespJson.getInt("DATA_REPORT_MODE");
//		  double longitude = gpsRespJson.getDouble("LONGITUDE");
//		  double latitude = gpsRespJson.getDouble("LATITUDE");
//		  int altitude = gpsRespJson.getInt("ALTITUDE");
//		  int leapSeconds = gpsRespJson.getInt("LEAP_SECONDS");
//		  double velocity = gpsRespJson.getDouble("VELOCITY");
//		  double heading = gpsRespJson.getDouble("HEADING");
//		  int gpsModeCode = gpsRespJson.getInt("GPS_MODE_CODE");
//		  int gpsStatusCode = gpsRespJson.getInt("GPS_STATUS_CODE");
//		  int numberOfSatellites = gpsRespJson.getInt("NUMBER_OF_SATELLITES");
//		  int utcYear = gpsRespJson.getInt("UTC_YEAR");
//		  int utcMonth = gpsRespJson.getInt("UTC_MONTH");
//		  int utcDay = gpsRespJson.getInt("UTC_DAY");
//		  int utcHour = gpsRespJson.getInt("UTC_HOUR");
//		  int utcMinute = gpsRespJson.getInt("UTC_MINUTE");
//		  int utcSecond = gpsRespJson.getInt("UTC_SECOND");
//		  long gpsSeconds = gpsRespJson.getLong("GPS_SECONDS");
//		}
//	}
}
