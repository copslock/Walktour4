package com.dingli.seegull.setupscan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class SetupScanBase {

	protected int mScanId = 0;
	protected int mScanMode = 0;
	protected int mProtocolCode = 0x001; //1 as GSM
	protected int mBandCode = 0; // not available

	protected boolean mEnableChannelConfig = true;

	protected long[][] mChannels 		= {{1,0}, {2, 0}};
	protected int[]    mDataModes 		= {0x001, 0x200}; //0x001  Ec/Io, 0x200 SIR 


	public SetupScanBase(int scanId, int scanMode, int protocolCode, int bandCode) {
		mScanId = scanId;
		mScanMode = scanMode;
		mProtocolCode = protocolCode;
		mBandCode = bandCode;
	}


	public void enableChannelConfig(boolean enable) {
		mEnableChannelConfig = enable;
	}
	public boolean setScanChannels(long[][] chns) {
		if (chns == null || chns.length <=0 || chns[0].length != 2) {
			return false;
		}
		mChannels = chns;
		return true;
	}

	public boolean setDataModeList(int[] modes) {
		if (modes == null)
			return false;
		mDataModes = modes;
		return true;
	}


	public abstract JSONObject genScanRequestBody() throws JSONException;
		
	
	/*
	 * CHANNEL_NUMBER	int	Channel number
	 * CHANNEL_STYLE	int	Channel Style Code
	 */
	/**
	 * Setup the Channel JSONObject
	 * @param channel
	 * @param style
	 * @return
	 * @throws JSONException
	 */
	public JSONObject setupChannel(int channel, int style) throws JSONException {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("CHANNEL_NUMBER", channel);
		jsonObject.put("CHANNEL_STYLE", style);

		return jsonObject;
	}
	/**
	 * setup the ChannelList JSONObject
	 * @return JSONObject
	 * @throws JSONException
	 */
	public JSONObject setupChannelList() throws JSONException	{

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("NETTYPE", mProtocolCode);
		jsonObject.put("BAND_CODE", mBandCode);
		JSONArray channelArray = new JSONArray();

		for(int i=0; i<mChannels.length; i++) {
			JSONObject channelJson = setupChannel((int)mChannels[i][0], (int)mChannels[i][1]);
			channelArray.put(channelJson);
		}
		jsonObject.put("CHANNEL_ARRAY", channelArray);
		return jsonObject;
	}
	
	
	/*
	 * FREQUENCY	long	Frequency value in Hz
	 * BAND_WIDTH	int	Bandwidth code
	 */
	/**
	 * Setup frequency JSONObject
	 * @param frequency
	 * @param bandwidth
	 * @return
	 * @throws JSONException
	 */
	public JSONObject setupFrequency(long frequency, int bandwidth) throws JSONException	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("FREQUENCY", frequency);
		jsonObject.put("BAND_WIDTH", bandwidth);
		return jsonObject;
	}


	/**
	 * BAND_CODE	int	Band Code (refer to Band Codes)
	 * FREQUENCY_ARRAY	JSON Array (type of Frequency)
	 * Array of Frequency JSON Object
	 * @return JSONObject
	 * @throws JSONException
	 */
	// setup frequency list
	public JSONObject setupFrequencyList() throws JSONException {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("BAND_CODE", mBandCode);
		JSONArray frequencyArray = new JSONArray();

		for (int i=0; i<mChannels.length; i++) {
			JSONObject frequencyJson = setupFrequency(mChannels[i][0], (int)mChannels[i][1]);
			frequencyArray.put(frequencyJson);
		}


		jsonObject.put("FREQUENCY_ARRAY", frequencyArray);

		return jsonObject;
	}



	/**
	 * CHANNEL_SPECIFIIED	boolean	Channel specified flag.
	 * 		true - use channel list
	 * 		false - use frequency list
	 * CHANNEL_LIST	JSON Object	Channel List JSON Object
	 * FREQUENCY_LIST	JSON Object	Frequency List JSON Object


	 * @return
	 * @throws JSONException
	 */
	public JSONObject setupChannelOrFrequency() throws JSONException	{
		JSONObject jsonObject = new JSONObject();

		if (mEnableChannelConfig) {// setup for channel
			// setup for channel
			jsonObject.put("CHANNEL_SPECIFIED", true);
			jsonObject.put("CHANNEL_LIST", setupChannelList());
		} else {
			// setup for frequency
			jsonObject.put("CHANNEL_SPECIFIC", false);
			jsonObject.put("FREQUENCY_LIST", setupFrequencyList());
		}
		return jsonObject;
	}


	/**
	 * CHANNEL_SPECIFIIED	boolean	Channel specified flag.
	 * 		true - use channel list
	 * 		false - use frequency list
	 * CHANNEL_LIST	JSON Object	Channel List JSON Object
	 * FREQUENCY_LIST	JSON Object	Frequency List JSON Object


	 * @return
	 * @throws JSONException
	 */
	public JSONObject setupChannelOrFrequencyList() throws JSONException	{
		JSONObject jsonObject = new JSONObject();

		if (mEnableChannelConfig) {// setup for channel
			// setup for channel
			jsonObject.put("CHANNEL_SPECIFIED", true);
			jsonObject.put("CHANNEL_LIST", setupChannelList());
		} else {
			// setup for frequency
			jsonObject.put("CHANNEL_SPECIFIC", false);
			jsonObject.put("FREQUENCY_LIST", setupFrequencyList());
		}
		return jsonObject;
	}


	/**
	 * THRESHOLD_TYPE	int	Type of threshold
	 * 		0 - RSSI Power Level
	 * 		1 - PN Threshold
	 * 		3 - Start Frequency (MHz)
	 * 		4 - End Frequency (MHz)
	 * THRESHOLD_VALUE	double	Threshold value
	 *
	 * @param type
	 * @param value
	 * @return JSONObject
	 * @throws JSONException
	 */
	// setup threshold
	public JSONObject setupThreshold(int type, double value) throws JSONException {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("THRESHOLD_TYPE", type);
		jsonObject.put("THRESHOLD_VALUE", value);

		return jsonObject;
	}

}
