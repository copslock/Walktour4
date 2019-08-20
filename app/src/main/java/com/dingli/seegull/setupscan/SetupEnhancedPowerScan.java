package com.dingli.seegull.setupscan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SetupEnhancedPowerScan extends SetupScanBase {
	
	public SetupEnhancedPowerScan(int scanId, int scanMode, int protocolCode,
			int bandCode) {
		super(scanId, scanMode, protocolCode, bandCode);
	}

	private int mEpsFlag = 0;// EPS flags,	0x000 - center,	0x100 - left, 0x200 - right
	private int mTimingPeriodMark = 20; //20 = 1 ms, Value must be divisor of 20000
	private int mTimingPeriodMarkOffset = 0;
	private int mNumberOfBins = 51;//(1 - 2560)
	private int mResolutionBandwidth = 2;//in unit of 2.5 kHz, between 2 and 7200.
	private int mFrequencyStepSize = 2; //in unit of 2.5 kHz, between 2 and 7200.
	private int mMeasurementWindow = 0;
	public boolean setConfiguration(int epsFlag, 
			int timingPeriodMark, int timingPeriodMarkOffset, 
			int numberOfBins, int resolutionBandwidth, 
			int frequencyStepSize, int measurementWindow) {
		if ((epsFlag != 0x000 && epsFlag != 0x100 && epsFlag != 0x200) 
		  || (timingPeriodMark < 0 || timingPeriodMark > 20000 || (20000 % timingPeriodMark) != 0)
		  || (timingPeriodMarkOffset < 0 || timingPeriodMarkOffset > timingPeriodMark)
		  || (numberOfBins < 1 || numberOfBins > 2560)
		  || (resolutionBandwidth < 2 || resolutionBandwidth > 7200)
		  || (frequencyStepSize < 2 || frequencyStepSize > 7200)
		  || (measurementWindow != 0 &&  (measurementWindow <100 || measurementWindow > 400)) ) {
			return false;
		}
		
		mEpsFlag = epsFlag;
		mTimingPeriodMark = timingPeriodMark;
		mTimingPeriodMarkOffset = timingPeriodMarkOffset;
		mNumberOfBins = numberOfBins;		
		mResolutionBandwidth = resolutionBandwidth;
		mFrequencyStepSize = frequencyStepSize;
		mMeasurementWindow = measurementWindow;
		return true;
		
	}
	/**
	 * SCAN_ID	int	Scan Id unique to individual scan.
	 * 		Value: 0 - 254
	 * SCAN_MODE	int	Scan mode value.
	 * 		Value: 
	 * 		0 - Auto
	 * 		3 - Auto with SD recording
	 * CHANNEL_LIST	JSON Object	Channel List JSON Object
	 * POWER_ANALYSIS_ELEMENT	JSON Object	Power Analysis Element JSON Object
	 * 
	 * @return
	 * @throws JSONException 
	 */
	// setup Power Analysis scan
	public JSONObject setupPowerAnalysis(int scanId, int scanMode, int dataMode) throws JSONException {
		JSONObject jsonObject = new JSONObject(); 
		 
		jsonObject.put("SCAN_ID", scanId);
		jsonObject.put("SCAN_MODE", scanMode); // auto
		jsonObject.put("CHANNEL_LIST", setupChannelList());
		jsonObject.put("POWER_ANALYSIS_ELEMENT", setupPowerAnalysisElement(dataMode));
		 
		return jsonObject;	  
	}
	/**
	 * DATA_MODE_LIST	JSON Array (type of int)	Array of data modes
	 * FREQUENCY_OFFSET	int	Optional Frequency Offset
	 * FREQUENCY_RESOLUTION	int	Optional Frequency resolution
	 * FREQUENCY_RANGE	int	Optional Frequency Range
	 * TIME_OFFSET	int	Optional Time Offset
	 * TIME_RESOLUTION	int	Optional Time Resolution
	 * TIME_RANGE	int	Optional Time Range

	 * @return
	 * @throws JSONException 
	 */
	// setup power analysis element
	public JSONObject setupPowerAnalysisElement(int dataMode) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		 
		  JSONArray dataModeArray = new JSONArray();
		  dataModeArray.put(dataMode); // data mode 1 - Resource Block Analysis
		  jsonObject.put("DATA_MODE_LIST", dataModeArray);
		  
		  return jsonObject;	  
	}
	
	/**
	 * SCAN_ID	int	Scan Id unique to individual scan.
	 * 		Value: 0 - 254
	 * SCAN_MODE	int	Scan mode value.
	 * 		Value: 
	 * 		0 - Auto
	 * 		3 - Auto with SD recording
	 * CHANNEL_OR_FREQUENCY	JSON Object	Channel or Frequency JSON Object
	 * EPS_FLAGS	int	EPS flags
	 * 		0x000 - center
	 * 		0x100 - left
	 * 		0x200 - right
	 * TIMING_PERIOD_MARK	int	Timing period mark, in unit of 50 us, scan will start on the nearest timing period mark with timing period mark offset specified below.
	 * 		Value must be divisor of 20000
	 * 		200 = 10 ms
	 * TIMING_PERIOD_MARK_OFFSET	int	Timing period mark offset, in unit of 50 us.
	 * 		Value must be from 0 to (timingPeriodMark - 1)
	 * NUMBER_OF_BINS	int	Number of bins (1 - 2560) For Center Mode value must be odd.
	 * RESOLUTION_BANDWIDTH	int	Resolution bandwidth, in unit of 2.5 kHz.
	 * 		Value must be between 2 and 7200.
	 * FREQUENCY_STEP_SIZE	int	Frequency step size, in unit of 2.5 kHz.
	 * 		Value must be between 2 and 7200.
	 * NUMBER_OF_SWEEPS	int	Optional. Number of sweeps, 0 for auto configuration
	 * MEASUREMENT_WINDOW	int	Optional.Measurement window, in unit of 50 us. 0 for auto configuration, other must be between 100 and 400.

	 * 
	 * @return
	 * @throws JSONException 
	 */
	
	@Override
	public JSONObject genScanRequestBody() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("SCAN_ID", mScanId);
		jsonObject.put("SCAN_MODE", mScanMode); // auto
		jsonObject.put("CHANNEL_OR_FREQUENCY", setupChannelOrFrequency());
		 
		jsonObject.put("EPS_FLAGS", mEpsFlag);
		jsonObject.put("TIMING_PERIOD_MARK", mTimingPeriodMark);
		jsonObject.put("TIMING_PERIOD_MARK_OFFSET", mTimingPeriodMarkOffset);
		jsonObject.put("NUMBER_OF_BINS", mNumberOfBins); // 51 bins
		jsonObject.put("RESOLUTION_BANDWIDTH", mResolutionBandwidth); // 2 as 5.0kHz
		jsonObject.put("FREQUENCY_STEP_SIZE", mFrequencyStepSize); // 2 as 5.0kHz
		 
		jsonObject.put("MEASUREMENT_WINDOW", mMeasurementWindow);//x50us, 100 and 400, 0 auto
		return jsonObject;
	}
}
