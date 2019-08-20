package com.dingli.seegull;

import com.dingli.seegull.SeeGullFlags.BandCodes;
import com.dingli.seegull.SeeGullFlags.ProtocolCodes;
import com.dingli.seegull.SeeGullFlags.ScanTypes;
import com.dingli.seegull.model.ChannelModel;
import com.dingli.seegull.model.ColorCodeModel;
import com.dingli.seegull.model.EtopNModel;
import com.dingli.seegull.model.RssiModel;
import com.dingli.seegull.model.ScanTaskModel;
import com.dingli.seegull.model.TopNModel;
import com.dingli.seegull.setupscan.SetupBlindScan;
import com.dingli.seegull.setupscan.SetupColorCodeScan;
import com.dingli.seegull.setupscan.SetupEnhanceTopNSignalScan;
import com.dingli.seegull.setupscan.SetupEnhancedPowerScan;
import com.dingli.seegull.setupscan.SetupRssiScan;
import com.dingli.seegull.setupscan.SetupTopNPilotScan;
import com.dingli.seegull.setupscan.SetupWifiScan;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Load the ScanTaskModel and generate the scan request parameters(please reset
 * ScanId). call {@link #createAllScanParameters()} and
 *
 * @author enwan.lu
 *
 */
public class ScanModelAdapter {
	@SuppressWarnings("unused")
	private final static String TAG = "SetupScanParameters";

	private static int mNextScanId = 0;

	private List<ScanTaskModel> mTaskModelList;
	private ScanTaskModel mTaskModel;
	private Integer mScanType = -1;

	@SuppressWarnings("unchecked")
	public ScanModelAdapter(Object params) {
		if (params instanceof ArrayList) {
			mTaskModelList = (List<ScanTaskModel>) params;
		} else if (params instanceof ScanTaskModel) {
			mTaskModel = (ScanTaskModel) params;
		} else if (params instanceof Integer) {
			mScanType = (Integer) params;
		}
	}

	private int getNextScanId() {
		mNextScanId++;
		if (mNextScanId > 254)
			mNextScanId = 0;
		return mNextScanId;
	}

	/**
	 * contains all the scan request information.
	 *
	 * @author enwan.lu
	 *
	 */
	public class ScanRequestParams {
		public int scanType = -1;
		public int scanId = -1;
		public int scanMode = 0;// auto
		public int protocolCode = ProtocolCodes.PROTOCOL_GSM; // GSM;
		public int bandCode = BandCodes.EUROPEAN_900; // 0x0600;GSM900
		public int configAmount = 5;
		JSONObject paramsAsJsonObject;
	}

	/**
	 * Create all the parameters with the ScanTaskModel.
	 *
	 * @return An ScanRequestParams object array of all scan request parameters.
	 * @throws JSONException
	 */
	public List<ScanRequestParams> createAllScanParameters() throws JSONException {
		List<ScanRequestParams> scanParamsList = new ArrayList<ScanRequestParams>();
		if (mScanType != -1) {
			scanParamsList.add(genParamsFromType(mScanType));
		} else {
			if (mTaskModel != null) {
				scanParamsList.addAll(genParamsArrayFromModel(mTaskModel));
			} else {
				if (mTaskModelList != null && mTaskModelList.size() > 0) {
					for (int i = 0; i < mTaskModelList.size(); i++) {
						ScanTaskModel model = mTaskModelList.get(i);
						if (model.getEnable() == 1)
							scanParamsList.addAll(genParamsArrayFromModel(model));
					}
				}
			}
		}
		return scanParamsList;
	}

	private List<ScanRequestParams> genParamsArrayFromModel(ScanTaskModel model) throws JSONException {
		switch (model.getScanType()) {
			case ScanTypes.eScanType_RssiChannel:
				return genRssiScanList((RssiModel) model);
			case ScanTypes.eScanType_ColorCode:
				return genColorCodeScanList((ColorCodeModel) model);
			case ScanTypes.eScanType_TopNPilot:
				return genTopNScanList((TopNModel) model);
			case ScanTypes.eScanType_eTopNSignal:
				return genEtopNScanList((EtopNModel) model);
		}
		return null;
	}

	// map KEY-TYPE:BAND_CODE-INTEGER; CHANNEL_ARRAY-LONG[][];
	private List<Map<String, Object>> translateChannelModeList(List<ChannelModel> channelModelList, int channelStyle) {
		if (channelModelList == null || channelModelList.size() == 0)
			return null;

		List<ChannelModel> templist = new ArrayList<ChannelModel>();
		templist.addAll(channelModelList);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		while (templist.size() > 0) {
			ChannelModel chnm = templist.get(0);
			int bandCode = chnm.getBandCode();
			List<Integer> channelList = new ArrayList<Integer>();
			for (int i = templist.size() - 1; i >= 0; i--) {
				chnm = templist.get(i);
				if (bandCode == chnm.getBandCode()) {
					for (int chn = chnm.getStartChannel(); chn <= chnm.getEndChannel(); chn++) {
						if (!channelList.contains(chn))
							channelList.add(chn);
					}
					templist.remove(i);
				}
			}
			long[][] channelarray = new long[channelList.size()][2];
			for (int i = 0; i < channelList.size(); i++) {
				channelarray[i][0] = channelList.get(i);
				channelarray[i][1] = channelStyle;
			}
			// if (ulOrDl && (protocolCode != ProtocolCodes.PROTOCOL_TDSCDMA &&
			// protocolCode != ProtocolCodes.PROTOCOL_TDSCDMA))
			// bandCode += 1;
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("BAND_CODE", Integer.valueOf(bandCode));
			map.put("CHANNEL_ARRAY", channelarray);
			list.add(map);
		}
		return list;
	}

	// RSSI
	private List<ScanRequestParams> genRssiScanList(RssiModel model) throws JSONException {
		List<ScanRequestParams> scanParamsList = new ArrayList<ScanRequestParams>();

		List<Map<String, Object>> channelInfoList = translateChannelModeList(model.getChannelList(), model.getStyle());

		for (int i = 0; i < channelInfoList.size(); i++) {
			Map<String, Object> map = channelInfoList.get(i);
			ScanRequestParams params = new ScanRequestParams();
			params.scanId = getNextScanId();
			params.scanType = model.getScanType();
			params.scanMode = model.getScanMode();// auto
			params.protocolCode = model.getProtocolCode();
			params.bandCode = (Integer) map.get("BAND_CODE");
			params.configAmount = 5; // 修改压包方式

			SetupRssiScan rssiScan = new SetupRssiScan(params.scanId, params.scanMode, params.protocolCode, params.bandCode);
			rssiScan.setScanChannels((long[][]) map.get("CHANNEL_ARRAY"));

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("SCAN_TYPE", "RSSI");
			jsonObject.put("SCAN_REQUEST_BODY", rssiScan.genScanRequestBody());

			params.paramsAsJsonObject = jsonObject;
			scanParamsList.add(params);
		}
		return scanParamsList;
	}

	// ColorCode
	private List<ScanRequestParams> genColorCodeScanList(ColorCodeModel model) throws JSONException {
		List<ScanRequestParams> scanParamsList = new ArrayList<ScanRequestParams>();

		List<Map<String, Object>> channelInfoList = translateChannelModeList(model.getChannelList(), model.getStyle());
		for (int i = 0; i < channelInfoList.size(); i++) {
			Map<String, Object> map = channelInfoList.get(i);

			ScanRequestParams params = new ScanRequestParams();
			params.scanId = getNextScanId();
			params.scanType = model.getScanType();
			params.scanMode = model.getScanMode();// auto
			params.protocolCode = model.getProtocolCode();
			params.bandCode = (Integer) map.get("BAND_CODE");
			params.configAmount = 3;

			SetupColorCodeScan colorCodeScan = new SetupColorCodeScan(params.scanId, params.scanMode, params.protocolCode,
					params.bandCode);
			colorCodeScan.setScanChannels((long[][]) map.get("CHANNEL_ARRAY"));
			colorCodeScan.setRssiThreshold(model.getRssiThreshold());
			colorCodeScan.setCIMode(model.isCI());
			colorCodeScan.setColorCodeMode(model.isColorCode());
			colorCodeScan.setL3MsgMode(model.isL3Msg());
			colorCodeScan.setMultipleColorCodeEnable(model.isMultipleColorCode());
			// model.isUlorDl();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("SCAN_TYPE", "COLOR_CODE");
			jsonObject.put("SCAN_REQUEST_BODY", colorCodeScan.genScanRequestBody());

			params.paramsAsJsonObject = jsonObject;
			scanParamsList.add(params);
		}
		return scanParamsList;
	}

	// TopN
	private List<ScanRequestParams> genTopNScanList(TopNModel model) throws JSONException {
		List<ScanRequestParams> scanParamsList = new ArrayList<ScanRequestParams>();

		List<Map<String, Object>> channelInfoList = translateChannelModeList(model.getChannelList(), model.getStyle());
		for (int i = 0; i < channelInfoList.size(); i++) {
			Map<String, Object> map = channelInfoList.get(i);

			ScanRequestParams params = new ScanRequestParams();
			params.scanId = getNextScanId();
			params.scanType = model.getScanType();
			params.scanMode = model.getScanMode();// auto
			params.protocolCode = model.getProtocolCode();
			params.bandCode = (Integer) map.get("BAND_CODE");
			params.configAmount = 5;

			SetupTopNPilotScan topNScan = new SetupTopNPilotScan(params.scanId, params.scanMode, params.protocolCode,
					params.bandCode);
			topNScan.setScanChannels((long[][]) map.get("CHANNEL_ARRAY"));
			topNScan.setPilotMode(model.getPilotMode());
			topNScan.setNumberOfPilots(model.getNumberOfPilots());
			topNScan.setPilotThreshold(model.getPilotThreshold());
			topNScan.enableChannelConfig(true);
			topNScan.enableDataOfAggregateEcOrIo(model.getAggregateEcIoEnable() == 1);
			topNScan.enableDataOfDelaySpread(model.getDelaySpreadEnable() == 1);
			topNScan.enableDataOfEc(model.getEcEnable() == 1);
			topNScan.enableDataOfEcOrIo(model.getEcioEnable() == 1);
			topNScan.enableDataOfEpsOrIo(model.getEpsIoEnable() == 1);
			topNScan.enableDataOfEssOrIo(model.getEssIoEnable() == 1);
			topNScan.enableDataOfLayer3Msg(model.getBchLayer3MessageDecodingEnable() == 1);
			topNScan.enableDataOfRakeFingerCount(model.getRakeFingerCountEnable() == 1);
			topNScan.enableDataOfSir(model.getSirEnable() == 1);
			topNScan.enableDataOfTimeOffset(model.getTimeOffsetEnable() == 1);

			if (params.protocolCode == ProtocolCodes.PROTOCOL_IS_856_EVDO) {
				topNScan.setPilotWindowMode_Optional(1);// CDMA/EVDO
				// only.0-offset,1-center,2-edge
				topNScan.setPilotWindowSearch_Optional(0);
				topNScan.setPilotWindowLength_Optional(0);
			}
			if (params.protocolCode == ProtocolCodes.PROTOCOL_IS_2000_CDMA) {
				topNScan.setPilotWindowMode_Optional(1);
			}

			if (params.protocolCode == ProtocolCodes.PROTOCOL_TDSCDMA) {
				topNScan.setDwellingTime_Optional(0);
				// int[] l3list = {1};
				// topNScan.setLayer3TypeList_Optional(l3list);
			}
			if (params.protocolCode == ProtocolCodes.PROTOCOL_3GPP_WCDMA) {
				topNScan.setDwellingTime_Optional(5);// 1-12
			}
			// topNScan.setDwellingTime_Optional(5);//WCDMA1-12//TDSCDMA 1-18, other
			// ...

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("SCAN_TYPE", "TOP_N_PILOT");
			jsonObject.put("SCAN_REQUEST_BODY", topNScan.genScanRequestBody());

			params.paramsAsJsonObject = jsonObject;
			scanParamsList.add(params);
		}
		return scanParamsList;
	}

	// ETopN
	private List<ScanRequestParams> genEtopNScanList(EtopNModel model) throws JSONException {
		List<ScanRequestParams> scanParamsList = new ArrayList<ScanRequestParams>();

		List<Map<String, Object>> channelInfoList = translateChannelModeList(model.getChannelList(), model.getStyle());
		for (int i = 0; i < channelInfoList.size(); i++) {
			Map<String, Object> map = channelInfoList.get(i);

			ScanRequestParams params = new ScanRequestParams();
			params.scanId = getNextScanId();
			params.scanType = model.getScanType();
			params.scanMode = model.getScanMode();// auto
			params.protocolCode = model.getProtocolCode();
			params.bandCode = (Integer) map.get("BAND_CODE");
			params.configAmount = 3;

			SetupEnhanceTopNSignalScan eTopNScan = new SetupEnhanceTopNSignalScan(params.scanId, params.scanMode,
					params.protocolCode, params.bandCode);
			eTopNScan.setScanChannels((long[][]) map.get("CHANNEL_ARRAY"));
			eTopNScan.setCarrierRssiThreshold(model.getCarrierRssiThreshold());
			eTopNScan.setCyclicPrefix(model.getCyclicPrefix());
			// model.getMeasurementThreshold();
			eTopNScan.setNumberOfRxAntennaPorts(model.getNumberOfRxAntennaPorts());
			eTopNScan.setNumberOfSignals(model.getNumberOfSignals());
			eTopNScan.setNumberOfTxAntennaPorts(model.getNumberOfTxAntennaPorts());
			eTopNScan.setNumberOfSubBands(model.getNumberOfSubBands());
			// model.getOperationalMode();
			eTopNScan.setRefMeasurementThreshold(model.getRefMeasurementThreshold());
			eTopNScan.setRefOperationalMode(model.getRefOperationalMode());
			eTopNScan.setRefWideBand(model.getRefWideBand());
			eTopNScan.setSubBandSize(model.getSubBandSize());
			eTopNScan.setSubBandStart(model.getSubBandStart());
			eTopNScan.setSyncMeasurementThreshold(model.getSyncMeasurementThreshold());
			eTopNScan.setSyncOperationalMode(model.getSyncOperationalMode());
			eTopNScan.setSyncWideBand(model.getSyncWideBand());
			// model.getWideBand();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("SCAN_TYPE", "ENHANCED_TOP_N_SIGNAL");
			jsonObject.put("SCAN_REQUEST_BODY", eTopNScan.genScanRequestBody());

			params.paramsAsJsonObject = jsonObject;
			scanParamsList.add(params);
		}
		return scanParamsList;
	}

	// 早期默认参数的方法，测试用
	private ScanRequestParams genParamsFromType(int scanType) throws JSONException {

		JSONObject jsonObject = new JSONObject();
		ScanRequestParams params = new ScanRequestParams();

		params.scanType = scanType;
		params.scanId = getNextScanId();
		params.scanMode = 0;// auto
		params.protocolCode = ProtocolCodes.PROTOCOL_GSM; // GSM
		params.bandCode = BandCodes.EUROPEAN_900; // 0x0600;GSM900
		params.configAmount = 5;

		boolean isImplement = false;

		switch (scanType) {

			case ScanTypes.eScanType_RssiChannel:// 0
				SetupRssiScan rssiChannelScan = new SetupRssiScan(params.scanId, params.scanMode, params.protocolCode,
						params.bandCode);
				// add config
				rssiChannelScan.enableChannelConfig(true);

				jsonObject.put("SCAN_TYPE", "RSSI");
				jsonObject.put("SCAN_REQUEST_BODY", rssiChannelScan.genScanRequestBody());

				params.configAmount = 4;
				isImplement = true;
				break;

			case ScanTypes.eScanType_RssiFrequency:// 1
				SetupRssiScan rssiFrequencyScan = new SetupRssiScan(params.scanId, params.scanMode, params.protocolCode,
						params.bandCode);
				// add config
				rssiFrequencyScan.enableChannelConfig(false);
				// 925 – 960 MHz
				long[][] frequencys = { { 9250000000L, 0 }, { 9350000000L, 0 }, { 9450000000L, 0 }, { 9550000000L, 0 } };
				rssiFrequencyScan.setScanChannels(frequencys);

				jsonObject.put("SCAN_TYPE", "RSSI");
				jsonObject.put("SCAN_REQUEST_BODY", rssiFrequencyScan.genScanRequestBody());
				isImplement = true;
				params.configAmount = 4;
				break;

			case ScanTypes.eScanType_EnhancedPowerScan:// 2
				params.protocolCode = ProtocolCodes.PROTOCOL_3GPP_WCDMA;
				params.bandCode = BandCodes.REV_IMT_2000;

				SetupEnhancedPowerScan epscan = new SetupEnhancedPowerScan(params.scanId, params.scanMode, params.protocolCode,
						params.bandCode);
				// add config
				int epsFlag = 0;
				int timingPeriodMark = 1;
				int timingPeriodMarkOffset = 0;
				int numberOfBins = 10;
				int resolutionBandwidth = 100;
				int frequencyStepSize = 100;
				int measurementWindow = 0;
				epscan.setConfiguration(epsFlag, timingPeriodMark, timingPeriodMarkOffset, numberOfBins, resolutionBandwidth,
						frequencyStepSize, measurementWindow);
				long[][] epschannels = { { 10688, 1 } };
				epscan.setScanChannels(epschannels);

				// add config
				jsonObject.put("SCAN_TYPE", "ENHANCED_POWER_SCAN");
				jsonObject.put("SCAN_REQUEST_BODY", epscan.genScanRequestBody());
				isImplement = true;

				// struct
				int RBW = -1;
				params.protocolCode = RBW;
				params.configAmount = 4;
				break;

			case ScanTypes.eScanType_TopNSignal: // 3
				break;

			case ScanTypes.eScanType_SpectrumAnalysis: // 4
				break;

			case ScanTypes.eScanType_TopNPilot: // 5
				params.protocolCode = ProtocolCodes.PROTOCOL_3GPP_WCDMA;
				long[][] channels = { { 10688, 1 }, { 10663, 1 }, { 10713, 1 } };
				params.bandCode = BandCodes.IMT_2000;

				SetupTopNPilotScan topnscan = new SetupTopNPilotScan(params.scanId, params.scanMode, params.protocolCode,
						params.bandCode);
				// add config

				topnscan.setScanChannels(channels);
				jsonObject.put("SCAN_TYPE", "TOP_N_PILOT");
				jsonObject.put("SCAN_REQUEST_BODY", topnscan.genScanRequestBody());
				params.configAmount = 5;
				isImplement = true;
				break;

			case ScanTypes.eScanType_TopNPilotBch:// 6
				break;

			case ScanTypes.eScanType_ColorCode: // 7
				SetupColorCodeScan colorCodeScan = new SetupColorCodeScan(params.scanId, params.scanMode, params.protocolCode,
						params.bandCode);
				// add config
				jsonObject.put("SCAN_TYPE", "COLOR_CODE");
				jsonObject.put("SCAN_REQUEST_BODY", colorCodeScan.genScanRequestBody());
				isImplement = true;
				params.configAmount = 3;
				break;

			case ScanTypes.eScanType_CodeDomain: // 8
				break;

			case ScanTypes.eScanType_TimeSlot: // 9
				break;

			case ScanTypes.eScanType_PilotZoom:// 10
				break;

			case ScanTypes.eScanType_PilotScan:// 11
				break;

			case ScanTypes.eScanType_eTopNSignal: // 12
				long[][] tdchannels = { { 38350, 1 } };

				int mRefWideBandMode = 18;
				params.protocolCode = ProtocolCodes.PROTOCOL_LTE;
				params.bandCode = BandCodes.DL_OR_UL_1_8_TDD; // BTDD Band39 38350 1890Mhz
				// FDD Band3 1650 1850Mhz
				params.bandCode = BandCodes.B_1800MHZ;// 0x0700 对应LTE 1650, 1800 MHz
				// Region (1805 – 1880 MHz), DCS
				// Forward
				tdchannels[0][0] = 1650;
				tdchannels[0][1] = 0;

				SetupEnhanceTopNSignalScan etnpsscan = new SetupEnhanceTopNSignalScan(params.scanId, params.scanMode,
						params.protocolCode, params.bandCode);
				etnpsscan.setScanChannels(tdchannels);
				etnpsscan.setRefWideBand(mRefWideBandMode);
				// add config
				jsonObject.put("SCAN_TYPE", "ENHANCED_TOP_N_SIGNAL");
				jsonObject.put("SCAN_REQUEST_BODY", etnpsscan.genScanRequestBody());
				params.configAmount = 3;
				isImplement = true;
				break;

			case ScanTypes.eScanType_TopNPilotPCH:// 13
				break;

			case ScanTypes.eScanType_BlindScan: // 14
				SetupBlindScan blindscan = new SetupBlindScan(params.scanId, params.scanMode, params.protocolCode,
						params.bandCode);
				// add config
				jsonObject.put("SCAN_TYPE", "BLIND_SCAN");
				jsonObject.put("SCAN_REQUEST_BODY", blindscan.genScanRequestBody());
				isImplement = true;
				break;

			case ScanTypes.eScanType_PowerAnalysis:// 15
				break;

			case ScanTypes.eScanType_ClarifyBCCH:// 16
				break;

			case ScanTypes.eScanType_MxBlindScan:// 17
				break;

			case ScanTypes.eScanType_ClarifyPilot:// 18
				break;

			case ScanTypes.eScanType_TimeSlotAnalysis:// 19
				break;

			case ScanTypes.eScanType_WiFiThroughput:// 20
				SetupWifiScan wifiscan = new SetupWifiScan(params.scanId, params.scanMode, params.protocolCode, params.bandCode);
				// add config
				jsonObject.put("SCAN_TYPE", "WIFI_THROUGHPUT");
				jsonObject.put("SCAN_REQUEST_BODY", wifiscan.genScanRequestBody());
				isImplement = true;
				break;

			default:
				break;
		}

		if (isImplement) {
			params.paramsAsJsonObject = jsonObject;
			return params;
		}
		return null;
	}

}
