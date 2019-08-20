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
 * CHANNEL_OR_FREQUENCY_LIST	JSON Object	Channel or Frequency List JSON Object

 * ETOPN_SIGNAL_REQUEST_ELEMENT	JSON Object	Enhanced Top N Signal Request Element JSON Object
 * ETOPN_SIGNAL_SYNC_REQUEST_ELEMENT	JSON Object	Enhanced Top N Signal Sync Request Element JSON Object
 * ETOPN_SIGNAL_REF_REQUEST_ELEMENT	JSON Object	Enhanced Top N Signal Ref Request Element JSON Object
 */

public class SetupEnhanceTopNSignalScan extends SetupScanBase {

	public SetupEnhanceTopNSignalScan(int scanId, int scanMode, int protocolCode,
									  int bandCode) {
		super(scanId, scanMode, protocolCode, bandCode);
	}
	/** IBFlex限制的最大通道数*/
	public static final int MAX_CHANNEL_COUNT = 24;
	//for ETopN Signal Request Element
	private double	mCarrierRssiThreshold = -120.0f;
	private int 	mNumberOfSignals = 16;
	//Optional
	private boolean mNumberOfTxAntennaPortsEnable = true;
	private int		mNumberOfTxAntennaPorts = 0; // 0 - auto detect
	private boolean mNumberOfRxAntennaPortsEnable = true;
	private int		mNumberOfRxAntennaPorts = 1;//isMIMO true 为2  否为1
	private boolean mCyclicPrefixEnable = true;
	private int		mCyclicPrefix = 0; // 0 - auto detect
	/** 0 - Standard 1 - High Speed Train*/
	private int mSignalMode = 0;
	//for ETopN Signal Sync Request Element
	private double	mSyncMeasurementThreshold = -150.0f;
	private int 	mSyncWideBand  = 13;//13--有层三SCH DB  14--无层三SCH_MIB_SIB DB
	//private int[] 	mSyncDataModes = {mSyncWideBand};
	//Optional
	private boolean mSyncOperationalModeEnable = true;
	private int 	mSyncOperationalMode = 9;
	private boolean	mIBlockTypeListEnable = true;
	private int[]	mIBlockTypeList = {0,14};//0 , master block; 1-14, system block 0-13
	private boolean	mDwellingTimeEnable = true;
	private int		mDwellingTime = 0;	//multiple of 40 ms
	// for ETopN Signal Ref Request element
	private double	mRefMeasurementThreshold = -150.0f;//db
	private int		mRefOperationalMode = 9;
	private int		mRefWideBand = 18;// 要根据protocol来判断 TDD25起 FDD18起
	//private int[]	mRefDataModes = {mRefWideBand};
	private int 	mSubBandStart = 0;
	private int 	mNumberOfSubBands = 4;
	private int 	mSubBandSize = 4;
	//for Scan Sampling
	/** 1 - Distance,2 - Count,3 - Time*/
//	private int mScanSamplingTriggerType = 3;
	/** based on trigger type,
	 * Time sampling in multiple of 100ms*/
//	private int mScanSamplingTriggerValue = 2000;
	/**
	 * Array of sampling mode
	 *	Values:
	 *	0 - Minimum Reporting
	 *	1 - Average
	 *	2 - Minimum
	 *	3 - Maximum
	 *	4 - Standard Deviation
	 *	5 - Median
	 */
//	private int mScanSamplingMode = 0;


	public void setCarrierRssiThreshold(double threshold) {
		mCarrierRssiThreshold = threshold;
	}
	public void setNumberOfSignals(int number) {
		mNumberOfSignals = number;
	}
	//Optional
	public void setNumberOfTxAntennaPorts(int number) {
		mNumberOfTxAntennaPorts = number;
		mNumberOfTxAntennaPortsEnable = true;
	}
	public void setNumberOfRxAntennaPorts(int number) {
		mNumberOfRxAntennaPorts = number;
		mNumberOfRxAntennaPortsEnable = true;
	}
	public void setCyclicPrefix(int cyclicPrefix) {
		mCyclicPrefix = cyclicPrefix;
		mCyclicPrefixEnable = true;
	}

	public void setSyncMeasurementThreshold(double threshold) {
		mSyncMeasurementThreshold = threshold;
	}
	public void setSyncWideBand(int wideBand) {
		mSyncWideBand = wideBand;
	}
	//Optional
	public void setSyncOperationalMode(int mode) {
		mSyncOperationalMode = mode;
		mSyncOperationalModeEnable = true;
	}
	public void setIBlockTypeList(int[] typelist) {
		mIBlockTypeList = typelist;
		mIBlockTypeListEnable = true;
	}
	public void setDwellingTime(int time) {
		mDwellingTime = time;
	}

	public void setRefMeasurementThreshold(double threshold) {
		mRefMeasurementThreshold = threshold;
	}
	public void setRefOperationalMode(int mode) {
		mRefOperationalMode = mode;
	}
	public void setRefWideBand(int wideBand) {
		mRefWideBand = wideBand;
	}
	public void setSubBandStart(int start) {
		mSubBandStart = start;
	}
	public void setNumberOfSubBands(int number) {
		mNumberOfSubBands = number;
	}
	public void setSubBandSize(int size) {
		mSubBandSize = size;
	}


	/*
	 * CARRIER_RSSI_THRESHOLD	double	Carrier RSSI Threshold
	 * NUMBER_OF_SIGNALS	int	Number of Signals
	 * NUMBER_OF_TX_ANTENNA_PORTS	int	Optional. Number of TX antenna ports
	 * 		0 - auto detect
	 * NUMBER_OF_RX_ANTENNA_PORTS	int	Optional. Number of RX antenna ports
	 * CYCLIC_PREFIX	int	Optional. Cyclic prefix
	 * 		0 - auto detect
	 */
	/**
	 *  Setup ETopN request element
	 * @return
	 * @throws JSONException
	 */
	private JSONObject setupETopNRequestElement() throws JSONException {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("CARRIER_RSSI_THRESHOLD", mCarrierRssiThreshold);
		jsonObject.put("NUMBER_OF_SIGNALS", mNumberOfSignals);
		if (mNumberOfTxAntennaPortsEnable)
			jsonObject.put("NUMBER_OF_TX_ANTENNA_PORTS", mNumberOfTxAntennaPorts); //0 auto
		if (mNumberOfRxAntennaPortsEnable)
			jsonObject.put("NUMBER_OF_RX_ANTENNA_PORTS", mNumberOfRxAntennaPorts);
		if (mCyclicPrefixEnable)
			jsonObject.put("CYCLIC_PREFIX", mCyclicPrefix); // 0 auto
		jsonObject.put("SIGNAL_MODE", this.mSignalMode);
		return jsonObject;
	}

	/*
	 * DATA_MODE_LIST	JSON Array (type of int)	Array of data modes
	 * MEASUREMENT_THRESHOLD	double	Measurement threshold
	 * OPERATIONAL_MODE	int	Operational mode
	 * IBLOCK_TYPE_LIST	JSON Array (type of int)	Optional. Array of Information Block Type
	 * 		Values:
	 * 		0 - Master Information Block
	 * 		1 - System Information Block
	 * 		2 - System Information Block 1
	 * 		3 - System Information Block 2
	 * 		4 - System Information Block 4
	 * 		5 - System Information Block 4
	 * 		6 - System Information Block 5
	 * 		7 - System Information Block 6
	 * 		8 - System Information Block 7
	 * 		9 - System Information Block 8
	 * 		10 - System Information Block 9
	 * 		11 - System Information Block 10
	 * 		12 - System Information Block 11
	 * 		13 - System Information Block 12
	 * 		14 - System Information Block 13
	 * DWELLING_TIME	int	Optional. Dwelling time (multiple of 40 ms)
	 */
	/**
	 * Setup ETopN Sync request element
	 * @return
	 * @throws JSONException
	 */
	private JSONObject setupETopNSyncRequestElement() throws JSONException {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("MEASUREMENT_THRESHOLD", mSyncMeasurementThreshold);//-150.0

		JSONArray dataModeArray = new JSONArray();
		dataModeArray.put(mSyncWideBand);//13 wide banddataModes
		jsonObject.put("DATA_MODE_LIST", dataModeArray);

		//Optional
		if (mSyncOperationalModeEnable)
			jsonObject.put("OPERATIONAL_MODE", mSyncOperationalMode); //9 RP
		JSONArray iBlockTypeList = new JSONArray();
		if (mIBlockTypeListEnable && mIBlockTypeList != null && mIBlockTypeList.length > 0) {
			for(int i=0; i<mIBlockTypeList.length; i++)
				iBlockTypeList.put(mIBlockTypeList[i]);
			jsonObject.put("IBLOCK_TYPE_LIST", iBlockTypeList);
		}else{
			jsonObject.put("IBLOCK_TYPE_LIST", iBlockTypeList);
		}
		if(mDwellingTimeEnable)
			jsonObject.put("DWELLING_TIME", mDwellingTime);
		return jsonObject;
	}

	/*
	 * DATA_MODE_LIST	JSON Array (type of int)	Array of data modes
	 * MEASUREMENT_THRESHOLD	double	Measurement threshold
	 * OPERATIONAL_MODE	int	Operational mode
	 * SUB_BAND_CONFIG	JSON Object	Optional Sub Band Configuration JSON Object
	 */
	/**
	 * Setup ETopN Ref request element
	 * @return
	 * @throws JSONException
	 */
	private JSONObject setupETopNRefRequestElement() throws JSONException	{
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("MEASUREMENT_THRESHOLD", mRefMeasurementThreshold); //-150.0
		jsonObject.put("OPERATIONAL_MODE", mRefOperationalMode); //9 as RP

		JSONArray dataModeArray = new JSONArray();
		dataModeArray.put(mRefWideBand);
		//for (int i=0; i < mRefDataModes.length; i++) {
		//	dataModeArray.put(mRefDataModes[i]);
		//}
		jsonObject.put("DATA_MODE_LIST", dataModeArray);

		jsonObject.put("SUB_BAND_CONFIG", setupSubBandConfig());

		return jsonObject;
	}

	/*
	 * SUB_BAND_START	int	Starting index of the sub band
	 * NUMBER_OF_SUB_BANDS	int	Number of sub band block
	 * SUB_BAND_SIZE	int	Size of the a sub band block
	 */
	/**
	 * Setup sub band configuration
	 * @return
	 * @throws JSONException
	 */
	private JSONObject setupSubBandConfig() throws JSONException {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("SUB_BAND_START", mSubBandStart);
		jsonObject.put("NUMBER_OF_SUB_BANDS", mNumberOfSubBands);
		jsonObject.put("SUB_BAND_SIZE", mSubBandSize);

		return jsonObject;
	}

	@Override
	public JSONObject genScanRequestBody() throws JSONException {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("SCAN_ID", mScanId);
		jsonObject.put("SCAN_MODE", mScanMode); // auto
		jsonObject.put("CHANNEL_OR_FREQUENCY_LIST", setupChannelOrFrequencyList());
		jsonObject.put("ETOPN_SIGNAL_REQUEST_ELEMENT", setupETopNRequestElement());
		jsonObject.put("ETOPN_SIGNAL_SYNC_REQUEST_ELEMENT", setupETopNSyncRequestElement());
		jsonObject.put("ETOPN_SIGNAL_REF_REQUEST_ELEMENT", setupETopNRefRequestElement());
//		jsonObject.put("SCAN_SAMPLING", setupScanSampling());

		return jsonObject;
	}

	/**
	 * 设置扫描样本
	 * @return
	 */
//	private JSONObject setupScanSampling() throws JSONException {
//		JSONObject jsonObject = new JSONObject();
//
//		jsonObject.put("SCAN_SAMPLING_TRIGGER_TYPE", mScanSamplingTriggerType);
//		jsonObject.put("SCAN_SAMPLING_TRIGGER_VALUE", mScanSamplingTriggerValue);
//		JSONArray modeArray = new JSONArray();
//		modeArray.put(mScanSamplingMode);
//		jsonObject.put("SCAN_SAMPLING_MODE_ARRAY", modeArray);
//
//		return jsonObject;
//	}

	public void setSignalMode(int signalMode) {
		mSignalMode = signalMode;
	}

}
