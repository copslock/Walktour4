package com.dingli.seegull.setupscan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * SCAN_ID	int	Scan Id unique to individual scan.
 * 		Value: 0 - 254
 * SCAN_MODE	int	Scan mode value.
 * 		Value:
 * 		0 - Auto
 * 		3 - Auto with SD recording
 * CHANNEL_LIST	JSON Object	Channel List JSON Object

 * DATA_MODE_LIST	JSON Array (type of int)	Array of data modes
 * NUMBER_OF_PILOTS	int	Number of pilots
 * PILOT_MODE	int	Pilot Mode
 * 		Values:
 * 		0 - High Speed
 * 		1 - High Dynamic
 * 		3 - Sync DL
 * 		4 - Midamble
 * PILOT_THRESHOLD	double	Pilot Threshold in dB
 * PILOT_WINDOW_SEARCH	int	Optional. Pilot Window search
 * PILOT_WINDOW_LENGTH	int	Optional. Pilot window length
 * PILOT_WINDOW_MODE	int	Optional.Pilot window mode
 * TIMING_MODE	int	Optional.Timing mode
 * TIME_OFFSET	int	Optional. Time offset
 * DWELLING_TIME	int	Optional. Dwelling Time (multiple of 40 ms)
 * LAYER3_TYPE_LIST	JSON Array (type of int)	Optional. Layer 3 Message Type
 */

public class SetupTopNPilotScan extends SetupScanBase {
	/** IBFlex限制的最大通道数*/
	public static final int MAX_CHANNEL_COUNT = 24;
	/** IBFlex限制的TDCDMA最大通道数*/
	public static final int MAX_TD_CHANNEL_COUNT = 34;

	private final static int DATA_MODE_EC_OR_IO		=	0x0001	;//	Ec/Io, Pilot Ec/Io, in dB * 100. (e.g., -16.34 = 0xF99E).Valid Range:0..-30. stdDeviation: 0..30. If pilot is not detected, -30 dB is returned.
	private final static int DATA_MODE_TIME_OFFSET	=	0x0004	;//	Time Offset, Time Offset in chips. Valid Range: stdDeviation: 0..38399. If pilot is not detected 38400 is returned.
	private final static int DATA_MODE_AGGREGATE_EC_OR_IO=0x0008;//	Aggregate Ec/Io, Aggregate Pilot Ec/Io, in dB * 100. (e.g., -56.30 = 0xEA02). Valid Range:  0..-30. stdDeviation: 0..30. If there are no peaks above the PN threshold, -30 dB is returned.
	private final static int DATA_MODE_DELAY_SPREAD	=	0x0010	;//	Delay Spread, Delay Spread in Chips Valid Range: stdDeviation: 0..49. If there are no peaks above the threshold, 255 is returned.
	private final static int DATA_MODE_EPS_OR_IO	=	0x0020	;//	Eps/Io, PSCH_Ec/Io, in dB * 100. (e.g., -16.34 = 0xF99E). Valid Range:  0..-30 stdDeviation: 0..30
	private final static int DATA_MODE_ESS_OR_IO	=	0x0040	;//	Ess/Io, SSCH_Ec/Io, in dB * 100. (e.g., -16.34 = 0xF99E). Valid Range: 0..-30, stdDeviation: 0..30. If pilot is not detected, -30 dB is returned. Scrambling Code Group (SCG) can be easily determined from the Pilot Number by finding the integer part of (Pilot Number / 8). (e.g., Pilot Number 12 is belongs to SCG 1).
	private final static int DATA_MODE_RAKE_FINGER_COUNT=0x0100	;//	Rake Finger Count, Rake Finger Count Valid range: 1..50, 255, stdDeviation: 1..50., If pilot is not detected, 255 is returned.
	private final static int DATA_MODE_SIR			=	0x0200	;//	SIR, Pilot Signal-to-Interference Ratio value, in dB * 100. (e.g., -16.34 = 0xF99E). Valid range: 25..-30, stdDeviation: 0..30. For WCDMA Base Station, Antenna 1 radiation is used. If no interference is detected +25 dB is reported. If pilot is not detected, -30 dB is returned.
	private final static int DATA_MODE_EC			=	0x0400	;//	Ec,	Pilot Ec,  in dBm * 100. (e.g., -56.30 = 0xEA02). Valid Range: 0..-120, stdDeviation: 0..120
	private final static int DATA_MODE_BCH_LAYER_3_MESSAGE_DECODING	=0x4000	;//	BCH_Layer_3_Message_Decoding	,	Scan Sampling N/A

	//private int[] mDataModes = {0x0001,0x0004,0x008,0x0010,0x0400};
	//private int[] mDataModesForCDMAorEVDO = {0x0001,0x0004,0x008,0x0010,0x0400};
	//private int[] mDataModesForWCDMA	  = {0x0001,0x0004,0x008,0x0010,0x0020,0x0040,0x0100,0x0200,0x0400};
	//private int[] mDataModesForTDSCDMA	  = {0x0001/*,0x0004,0x0020,0x0200,0x0400,0x4000*/};
	//if (protocolCode == ProtocolCodes.PROTOCOL_3GPP_WCDMA)
	//	mDataModes = mDataModesForWCDMA;
	//else if (protocolCode == ProtocolCodes.PROTOCOL_TDSCDMA)
	//	mDataModes = mDataModesForTDSCDMA;
	//else if (protocolCode == ProtocolCodes.PROTOCOL_IS_856_EVDO 
	//		|| protocolCode == ProtocolCodes.PROTOCOL_IS_2000_CDMA)
	//	mDataModes = mDataModesForCDMAorEVDO;

	public SetupTopNPilotScan(int scanId, int scanMode, int protocolCode, int bandCode) {
		super(scanId, scanMode, protocolCode, bandCode);
	}

	private int 	mNumberOfPilots = 32;
	private int 	mPilotMode 		= 1; //0 - High Speed;1 - High Dynamic;	3 - Sync DL; 4 - Midamble
	private double 	mPilotThreshold = -30.0f;//in dB

	private boolean mEcorIoEnable = false;
	private boolean mTimeOffsetEnable = false;
	private boolean mAggregateEcOrIoEnable = false;
	private boolean mDelaySpreadEnable = false;
	private boolean mEpsOrIoEnable = false;
	private boolean mEssOrIoEnable = false;
	private boolean mRakeFingerCountEnable = false;
	private boolean mSirEnable = false;
	private boolean mEcEnable = false;
	private boolean mLayer3MsgEnable = false;


	public void setNumberOfPilots(int number) {
		mNumberOfPilots = number;
	}
	public void setPilotMode(int mode) {
		mPilotMode = mode;
	}
	public void setPilotThreshold(double threshold) {
		mPilotThreshold = threshold;
	}

	//data modes
	public void enableDataOfEcOrIo(boolean enable) {
		mEcorIoEnable = enable;
	}
	public void enableDataOfTimeOffset(boolean enable) {
		mTimeOffsetEnable = enable;
	}
	public void enableDataOfAggregateEcOrIo(boolean enable) {
		mAggregateEcOrIoEnable = enable;
	}
	public void enableDataOfDelaySpread(boolean enable) {
		mDelaySpreadEnable = enable;
	}
	public void enableDataOfEpsOrIo(boolean enable) {
		mEpsOrIoEnable = enable;
	}
	public void enableDataOfEssOrIo(boolean enable) {
		mEssOrIoEnable = enable;
	}
	public void enableDataOfRakeFingerCount(boolean enable) {
		mRakeFingerCountEnable = enable;
	}
	public void enableDataOfSir(boolean enable) {
		mSirEnable = enable;
	}
	public void enableDataOfEc(boolean enable) {
		mEcEnable = enable;
	}
	public void enableDataOfLayer3Msg(boolean enable) {
		mLayer3MsgEnable = enable;
	}



	public boolean setPilotsConfiguration(int numberOfPilots, int pilotMode, double pilotThreshold) {
		if (pilotMode < 0 || pilotMode > 4 && numberOfPilots <= 0)
			return false;
		mNumberOfPilots = numberOfPilots;
		mPilotMode = pilotMode;
		mPilotThreshold = pilotThreshold;
		return true;
	}

	//Optional
	private boolean mPilotWindowSearchEnable = false;
	private int 	mPilotWindowSearch 		 = 0;
	private boolean mPilotWindowLengthEnable = false;
	private int 	mPilotWindowLength 		 = 0;
	private boolean mPilotWindowModeEnable	 = false;
	private int 	mPilotWindowMode		 = 0;
	private boolean mTimingModeEnable		 = false;
	private int 	mTimingMode	 			 = 0;
	private boolean mTimingOffsetEnable	 	 = false;
	private int 	mTimingOffset	 		 = 0;
	private boolean mDwellingTimeEnable	 	 = false;
	private int 	mDwellingTime	 		 = 0;
	private boolean mLayer3TypeListEnable	 = false;
	private int[]   mLayer3TypeList	 		 = {0};
	public void setPilotWindowSearch_Optional(int search) {
		mPilotWindowSearch = search;
		mPilotWindowSearchEnable = true;
	}
	public void setPilotWindowLength_Optional(int length) {
		mPilotWindowLength = length;
		mPilotWindowLengthEnable = true;
	}
	public void setPilotWindowMode_Optional(int mode) {
		mPilotWindowMode = mode;
		mPilotWindowModeEnable = true;
	}
	public void setTimingMode_Optional(int mode) {
		mTimingMode = mode;
		mTimingModeEnable = true;
	}
	public void setTimingOffset_Optional(int offset) {
		mTimingOffset = offset;
		mTimingOffsetEnable = true;
	}
	public void setDwellingTime_Optional(int time) {
		mDwellingTime = time;
		mDwellingTimeEnable = true;
	}
	public void setLayer3TypeList_Optional(int[] types) {
		mLayer3TypeList = types;
		mLayer3TypeListEnable = true;
	}

	/**
	 * @return the request body as JSONObject
	 * @throws JSONException
	 */

	@Override
	public JSONObject genScanRequestBody() throws JSONException {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("SCAN_ID", mScanId);
		jsonObject.put("SCAN_MODE", mScanMode); // auto
		jsonObject.put("CHANNEL_LIST", setupChannelList());
		jsonObject.put("NUMBER_OF_PILOTS", mNumberOfPilots);
		jsonObject.put("PILOT_MODE", mPilotMode); // high dynamic
		jsonObject.put("PILOT_THRESHOLD", mPilotThreshold);

		JSONArray dataModeArray = new JSONArray();
		//for (int mode : mDataModes) {
		//	dataModeArray.put(mode);
		//}
		if (mEcorIoEnable)
			dataModeArray.put(DATA_MODE_EC_OR_IO);
		if (mTimeOffsetEnable)
			dataModeArray.put(DATA_MODE_TIME_OFFSET);
		if (mAggregateEcOrIoEnable)
			dataModeArray.put(DATA_MODE_AGGREGATE_EC_OR_IO);
		if (mDelaySpreadEnable)
			dataModeArray.put(DATA_MODE_DELAY_SPREAD);
		if (mEpsOrIoEnable)
			dataModeArray.put(DATA_MODE_EPS_OR_IO);
		if (mEssOrIoEnable)
			dataModeArray.put(DATA_MODE_ESS_OR_IO);
		if (mRakeFingerCountEnable)
			dataModeArray.put(DATA_MODE_RAKE_FINGER_COUNT);
		if (mSirEnable)
			dataModeArray.put(DATA_MODE_SIR);
		if (mEcEnable)
			dataModeArray.put(DATA_MODE_EC);
		if (mLayer3MsgEnable)
			dataModeArray.put(DATA_MODE_BCH_LAYER_3_MESSAGE_DECODING);

		jsonObject.put("DATA_MODE_LIST", dataModeArray);

		//Optional
		if (mPilotWindowSearchEnable)
			jsonObject.put("PILOT_WINDOW_SEARCH", mPilotWindowSearch);
		if (mPilotWindowLengthEnable)
			jsonObject.put("PILOT_WINDOW_LENGTH", mPilotWindowLength);
		if (mPilotWindowModeEnable)
			jsonObject.put("PILOT_WINDOW_MODE", mPilotWindowMode);
		if (mTimingModeEnable)
			jsonObject.put("TIMING_MODE", mTimingMode);
		if (mTimingOffsetEnable)
			jsonObject.put("TIME_OFFSET", mTimingOffset);
		if (mDwellingTimeEnable)
			jsonObject.put("DWELLING_TIME", mDwellingTime);
		if (mLayer3TypeListEnable && mLayer3TypeList != null && mLayer3TypeList.length > 0) {
			JSONArray layer3TypeList = new JSONArray();
			for(int i=0; i<mLayer3TypeList.length; i++)
				layer3TypeList.put(mLayer3TypeList[i]);
			jsonObject.put("LAYER3_TYPE_LIST", layer3TypeList);
		}

		return jsonObject;
	}
}
