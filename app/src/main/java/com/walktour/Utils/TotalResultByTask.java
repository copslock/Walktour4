package com.walktour.Utils;

import com.walktour.Utils.TotalStruct.TotalAppreciation;
import com.walktour.Utils.TotalStruct.TotalAttach;
import com.walktour.Utils.TotalStruct.TotalDNS;
import com.walktour.Utils.TotalStruct.TotalDial;
import com.walktour.Utils.TotalStruct.TotalFaceBook;
import com.walktour.Utils.TotalStruct.TotalFtp;
import com.walktour.Utils.TotalStruct.TotalPdp;
import com.walktour.Utils.TotalStruct.TotalSpeed;
import com.walktour.Utils.TotalStruct.TotalTraceRoute;
import com.walktour.Utils.TotalStruct.TotalVideoPlay;
import com.walktour.base.util.LogUtil;
import com.walktour.model.StateInfoModel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 当前类实现通过指定的任务，获得当前任务的测试次数，成功率等结果
 * 
 * @author tangwq
 *
 */
public class TotalResultByTask {

	public static void totalTargeTask(WalkStruct.TaskType taskType, StateInfoModel infoModel) {
		switch (taskType) {
		case InitiativeCall:
			buildInitiativeCall(infoModel);
			break;
		case PassivityCall:
			buildPassivityCall(infoModel);
			break;
		case Ping:
			buildPing(infoModel);
			break;
		case Attach:
			buildAttach(infoModel);
			break;
		case PDP:
			buildPdp(infoModel);
			break;
		case FTPUpload:
			buildFTPUpload(infoModel);
			break;
		case FTPDownload:
			buildFTPDownload(infoModel);
			break;
		case HttpRefurbish:
			buildHttpRefurbish(infoModel);
			break;
		case HttpDownload:
			buildHttpDownload(infoModel);
			break;
		case HttpUpload:
			buildHttpUpload(infoModel);
			break;
		case Http:
			buildHttpPage(infoModel);
			break;
		case EmailPop3:
			buildEmailPop3(infoModel);
			break;
		case EmailSmtp:
			buildEmailSmtp(infoModel);
			break;
		case SMSIncept:
			buildSMSIncept(infoModel);
			break;
		case SMSSend:
			buildSMSSend(infoModel);
			break;
		case MMSIncept:
			buildMMSIncept(infoModel);
			break;
		case MMSSend:
			buildMMSSend(infoModel);
			break;
		case WapLogin:
			buildWapLogin(infoModel);
			break;
		case WapRefurbish:
			buildWapRefurbish(infoModel);
			break;
		case WapDownload:
			buildWapDownload(infoModel);
			break;
		case SpeedTest:
			buildSpeedTest(infoModel);
			break;
		case DNSLookUp:
			buildDNSLookUp(infoModel);
			break;
		case HTTPVS:
			buildHTTPVS(infoModel);
			break;
		case MultiftpUpload:
			buildMultiftpUpload(infoModel);
			break;
		case MultiftpDownload:
			buildMultiftpDownload(infoModel);
			break;
		case Facebook:
			buildFacebook(infoModel);
			break;
		case TraceRoute:
			buildTraceRoute(infoModel);
			break;
		default:
			buildDefault(infoModel);
			break;

		}
	}

	/**
	 * 邮件发送
	 * 
	 * @param infoModel
	 */
	private static void buildPdp(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalPdp._pdpRequest.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalPdp._pdpSuccess.name()));
		infoModel.setSuccessRate(
				TotalDataByGSM.getHashMapMultiple(hMap, TotalPdp._pdpSuccess.name(), TotalPdp._pdpRequest.name(), 100, "%"));
		infoModel.setDelay(
				TotalDataByGSM.getHashMapMultiple(hMap, TotalPdp._pdpDelay.name(), TotalPdp._pdpSuccess.name(), 1, ""));
		infoModel.setAvgThrRate("");
	}

	/**
	 * 邮件发送
	 * 
	 * @param infoModel
	 */
	private static void buildAttach(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalAttach._attachRequest.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalAttach._attachSuccess.name()));
		infoModel.setSuccessRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalAttach._attachSuccess.name(),
				TotalAttach._attachRequest.name(), 100, "%"));
		infoModel.setDelay(TotalDataByGSM.getHashMapMultiple(hMap, TotalAttach._lteAttachDelay.name(),
				TotalAttach._attachSuccess.name(), 1, ""));
		infoModel.setAvgThrRate("");
	}

	/**
	 * 邮件发送
	 * 
	 * @param infoModel
	 */
	private static void buildTraceRoute(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalTraceRoute._traceRouteTrys.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalTraceRoute._traceRouteSucc.name()));
		infoModel.setSuccessRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalTraceRoute._traceRouteSucc.name(),
				TotalTraceRoute._traceRouteTrys.name(), 100, "%"));
		infoModel.setDelay(TotalDataByGSM.getHashMapMultiple(hMap, TotalTraceRoute._traceRouteDelay.name(),
				TotalTraceRoute._traceRouteSucc.name(), 1, ""));
		infoModel.setAvgThrRate("");
	}

	/**
	 * 邮件发送
	 * 
	 * @param infoModel
	 */
	private static void buildFacebook(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalFaceBook._faceBookAttempts.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalFaceBook._faceBookSuccesses.name()));
		infoModel.setSuccessRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalFaceBook._faceBookSuccesses.name(),
				TotalFaceBook._faceBookAttempts.name(), 100, "%"));
		infoModel.setDelay("");
		infoModel.setAvgThrRate("");
	}

	/**
	 * FTPUp统计
	 * 
	 * @param infoModel
	 */
	private static void buildMultiftpUpload(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalFtp.m_uptrys.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalFtp.m_upSuccs.name()));
		infoModel.setSuccessRate(
				TotalDataByGSM.getHashMapMultiple(hMap, TotalFtp.m_upSuccs.name(), TotalFtp.m_uptrys.name(), 100, "%"));
		infoModel.setDelay("");
		infoModel.setAvgThrRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalStruct.TotalFtp.m_upCurrentSize.name(), // bit
				TotalStruct.TotalFtp.m_upCurrentTimes.name(), // ms
				8f*1000/1024, // 界面显示为kbps<------bit/ms转换成 1000f/1000
				""));
	}

	/**
	 * FTPUp统计
	 * 
	 * @param infoModel
	 */
	private static void buildMultiftpDownload(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalFtp.m_downtrys.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalFtp.m_downSuccs.name()));
		infoModel.setSuccessRate(
				TotalDataByGSM.getHashMapMultiple(hMap, TotalFtp.m_downSuccs.name(), TotalFtp.m_downtrys.name(), 100, "%"));
		infoModel.setDelay("");
		infoModel.setAvgThrRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalStruct.TotalFtp.m_downCurrentSize.name(), // bit
				TotalStruct.TotalFtp.m_downCurrentTimes.name(), // ms
				8f*1000/1024, // 界面显示为kbps<------bit/ms转换成 1000f/1000
				""));
	}

	/**
	 * 邮件发送
	 * 
	 * @param infoModel
	 */
	private static void buildHTTPVS(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalVideoPlay._videoTrys.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalVideoPlay._videoSuccs.name()));
		infoModel.setSuccessRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalVideoPlay._videoSuccs.name(),
				TotalVideoPlay._videoTrys.name(), 100, "%"));
		infoModel.setDelay(TotalDataByGSM.getHashMapMultiple(hMap, TotalVideoPlay._videoReproductionDaily.name(),
				TotalVideoPlay._videoTrys.name(), 1, ""));
		infoModel.setAvgThrRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalVideoPlay._vpTotalBytes.name(),
				TotalVideoPlay._vpTotalTime.name(), 8f, "kbps")); // 1000/1000
	}

	/**
	 * 邮件发送
	 * 
	 * @param infoModel
	 */
	private static void buildDNSLookUp(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalDNS._dnsTotalTrys.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalDNS._dnsSuccs.name()));
		infoModel.setSuccessRate(
				TotalDataByGSM.getHashMapMultiple(hMap, TotalDNS._dnsSuccs.name(), TotalDNS._dnsTotalTrys.name(), 100, "%"));
		infoModel
				.setDelay(TotalDataByGSM.getHashMapMultiple(hMap, TotalDNS._dnsDelay.name(), TotalDNS._dnsSuccs.name(), 1, ""));
		infoModel.setAvgThrRate("");
	}

	/**
	 * 邮件发送
	 * 
	 * @param infoModel
	 */
	private static void buildSpeedTest(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalSpeed._speedTotalTrys.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalSpeed._speedSuccs.name()));
		infoModel.setSuccessRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalSpeed._speedSuccs.name(),
				TotalSpeed._speedTotalTrys.name(), 100, "%"));
		infoModel.setDelay(TotalDataByGSM.getHashMapMultiple(hMap, TotalSpeed._speedDelay.name(),
				TotalSpeed._speedPingSuccs.name(), 1, ""));
		infoModel.setAvgThrRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalSpeed._speedDLTotalBytes.name(),
				TotalSpeed._speedDLTotalTime.name(), 1f, "")); // 1000/1000
	}

	/**
	 * 邮件发送
	 * 
	 * @param infoModel
	 */
	private static void buildWapDownload(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalAppreciation._wapDownTrys.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalAppreciation._wapDownSuccs.name()));
		infoModel.setSuccessRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalAppreciation._wapDownSuccs.name(),
				TotalAppreciation._wapDownTrys.name(), 100, "%"));
		infoModel.setDelay("");
		infoModel.setAvgThrRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalAppreciation._wapDownTotalBytes.name(),
				TotalAppreciation._wapDownTotalTime.name(), 8f, "")); // 1000f/1000
	}

	/**
	 * 邮件发送
	 * 
	 * @param infoModel
	 */
	private static void buildWapRefurbish(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalAppreciation._wapRefreshTrys.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalAppreciation._wapRefreshSuccs.name()));
		infoModel.setSuccessRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalAppreciation._wapRefreshSuccs.name(),
				TotalAppreciation._wapRefreshTrys.name(), 100, "%"));
		infoModel.setDelay(TotalDataByGSM.getHashMapMultiple(hMap, TotalAppreciation._wapRefreshDelay.name(),
				TotalAppreciation._wapRefreshSuccs.name(), 1, ""));
		infoModel.setAvgThrRate("");
	}

	/**
	 * 邮件发送
	 * 
	 * @param infoModel
	 */
	private static void buildWapLogin(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalAppreciation._wapLoginTrys.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalAppreciation._wapLogingSuccs.name()));
		infoModel.setSuccessRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalAppreciation._wapLogingSuccs.name(),
				TotalAppreciation._wapLoginTrys.name(), 100, "%"));
		infoModel.setDelay(TotalDataByGSM.getHashMapMultiple(hMap, TotalAppreciation._wapLoginDelay.name(),
				TotalAppreciation._wapLogingSuccs.name(), 1, ""));
		infoModel.setAvgThrRate("");
	}

	/**
	 * 邮件发送
	 * 
	 * @param infoModel
	 */
	private static void buildMMSSend(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalAppreciation._MMSSendTry.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalAppreciation._MMSSendSuccs.name()));
		infoModel.setSuccessRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalAppreciation._MMSSendSuccs.name(),
				TotalAppreciation._MMSSendTry.name(), 100, "%"));
		infoModel.setDelay(TotalDataByGSM.getHashMapMultiple(hMap, TotalAppreciation._MMSSendDelay.name(),
				TotalAppreciation._MMSSendSuccs.name(), 1, ""));
		infoModel.setAvgThrRate("");
	}

	/**
	 * 邮件发送
	 * 
	 * @param infoModel
	 */
	private static void buildMMSIncept(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalAppreciation._MMSReceiveTry.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalAppreciation._MMSReceiveSuccs.name()));
		infoModel.setSuccessRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalAppreciation._MMSReceiveSuccs.name(),
				TotalAppreciation._MMSReceiveTry.name(), 100, "%"));
		infoModel.setDelay(TotalDataByGSM.getHashMapMultiple(hMap, TotalAppreciation._MMSReceiveDelay.name(),
				TotalAppreciation._MMSReceiveSuccs.name(), 1, ""));
		infoModel.setAvgThrRate("");
	}

	/**
	 * 邮件发送
	 * 
	 * @param infoModel
	 */
	private static void buildSMSSend(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalAppreciation._SMSSendDelay.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalAppreciation._SMSSendSuccs.name()));
		infoModel.setSuccessRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalAppreciation._SMSSendSuccs.name(),
				TotalAppreciation._SMSSendTry.name(), 100, "%"));
		infoModel.setDelay(TotalDataByGSM.getHashMapMultiple(hMap, TotalAppreciation._SMSSendDelay.name(),
				TotalAppreciation._SMSSendSuccs.name(), 1, ""));
		infoModel.setAvgThrRate("");
	}

	/**
	 * 邮件发送
	 * 
	 * @param infoModel
	 */
	private static void buildSMSIncept(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalAppreciation._SMSReceiveTry.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalAppreciation._SMSReceiveSuccs.name()));
		infoModel.setSuccessRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalAppreciation._SMSReceiveSuccs.name(),
				TotalAppreciation._SMSReceiveTry.name(), 100, "%"));
		infoModel.setDelay(TotalDataByGSM.getHashMapMultiple(hMap, TotalAppreciation._SMSReceiveDelay.name(),
				TotalAppreciation._SMSReceiveSuccs.name(), 1, ""));
		infoModel.setAvgThrRate("");
	}

	/**
	 * 邮件发送
	 * 
	 * @param infoModel
	 */
	private static void buildEmailSmtp(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalAppreciation._EmailSendTry.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalAppreciation._EmailSendSuccess.name()));
		infoModel.setSuccessRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalAppreciation._EmailSendSuccess.name(),
				TotalAppreciation._EmailSendTry.name(), 100, "%"));
		infoModel.setDelay("");
		infoModel.setAvgThrRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalAppreciation._EmailSendSumSize.name(), // bit
				TotalAppreciation._EmailSendAllTime.name(), // ms
				8f, // 界面显示为kbps<------bit/ms转换成 //*1000f/1000
				""));
	}

	/**
	 * 邮件接受
	 * 
	 * @param infoModel
	 */
	private static void buildEmailPop3(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalAppreciation._EmailReceiveTry.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalAppreciation._EmailReceiveSuccess.name()));
		infoModel.setSuccessRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalAppreciation._EmailReceiveSuccess.name(),
				TotalAppreciation._EmailReceiveTry.name(), 100, "%"));
		infoModel.setDelay("");
		infoModel.setAvgThrRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalAppreciation._EmailReceSumSize.name(), // bit
				TotalAppreciation._EmailReceAllTime.name(), // ms
				8f, // 界面显示为kbps<------bit/ms转换成 //*1000f/1000
				""));
	}

	/**
	 * Http统计
	 * 
	 * @param infoModel
	 */
	@SuppressWarnings("unchecked")
	private static void buildHttpPage(StateInfoModel infoModel) {
		Map<String, Map<String,Long>> httpRefreshHM = TotalDataByGSM.getInstance().getSpecialTimes().get(TotalStruct.TotalHttpType.HTTPLogon.getHttpType());
		if (httpRefreshHM != null) {
			Iterator<Entry<String, Map<String,Long>>> iter = httpRefreshHM.entrySet().iterator();
			long trys = 0;
			long succes = 0;
			long delay = 0;
			while (iter.hasNext()) {
				Map.Entry<String, Map<String,Long>> entry = iter.next();
				HashMap<String, Long> val = (HashMap<String, Long>) entry.getValue();
				trys += TotalDataByGSM.getHashMapVal(val, TotalAppreciation._HttpTry.name());
				succes += TotalDataByGSM.getHashMapVal(val, TotalAppreciation._HttpSuccess.name());
				delay += TotalDataByGSM.getHashMapVal(val, TotalAppreciation._HttpDelay.name());
			}
			infoModel.setTestTimes(String.valueOf(trys));
			infoModel.setSuccessTimes(String.valueOf(succes));
			infoModel.setSuccessRate(TotalDataByGSM.getIntMultiple(succes, trys, 100, "%"));
			infoModel.setDelay(TotalDataByGSM.getIntMultiple(delay, succes, 1, ""));
			infoModel.setAvgThrRate("");
		} else {
			buildDefault(infoModel);
		}
	}

	/**
	 * Http统计
	 * 
	 * @param infoModel
	 */
	@SuppressWarnings("unchecked")
	private static void buildHttpUpload(StateInfoModel infoModel) {
		Map<String, Map<String,Long>> httpUpHM = TotalDataByGSM.getInstance().getSpecialTimes().get(TotalStruct.TotalHttpType.HTTPUpload.getHttpType());
		if (httpUpHM != null) {
			Iterator<Entry<String, Map<String,Long>>> iter = httpUpHM.entrySet().iterator();
			long trys = 0;
			long succes = 0;
			long bytes = 0;
			long times = 0;
			long delay = 0;
			while (iter.hasNext()) {
				Map.Entry<String, Map<String,Long>> entry = iter.next();
				HashMap<String, Long> val = (HashMap<String, Long>) entry.getValue();
				trys += TotalDataByGSM.getHashMapVal(val, TotalAppreciation._HttpUploadTry.name());
				succes += TotalDataByGSM.getHashMapVal(val, TotalAppreciation._HttpUploadSuccess.name());
				bytes += TotalDataByGSM.getHashMapVal(val, TotalAppreciation._HttpUploadMeanRate.name());
				times += TotalDataByGSM.getHashMapVal(val, TotalAppreciation._HttpUploadMeanRateTimes.name());
				delay += TotalDataByGSM.getHashMapVal(val, TotalAppreciation._HttpUploadDelay.name());
			}
			infoModel.setTestTimes(String.valueOf(trys));
			infoModel.setSuccessTimes(String.valueOf(succes));
			infoModel.setSuccessRate(TotalDataByGSM.getIntMultiple(succes, trys, 100, "%"));
			infoModel.setDelay(TotalDataByGSM.getIntMultiple(delay, succes, 1, ""));
			infoModel.setAvgThrRate(TotalDataByGSM.getIntMultiple(bytes, times, 0.001f, ""));
		}
	}

	/**
	 * Http统计
	 * 
	 * @param infoModel
	 */
	@SuppressWarnings("unchecked")
	private static void buildHttpDownload(StateInfoModel infoModel) {
		Map<String, Map<String,Long>> httpDownHM = TotalDataByGSM.getInstance().getSpecialTimes().get(TotalStruct.TotalHttpType.HTTPDownload.getHttpType());
		if (httpDownHM != null) {
			Iterator<Entry<String, Map<String,Long>>> iter = httpDownHM.entrySet().iterator();
			long trys = 0;
			long succes = 0;
			long bytes = 0;
			long times = 0;
			while (iter.hasNext()) {
				Map.Entry<String, Map<String,Long>> entry = iter.next();
				HashMap<String, Long> val = (HashMap<String, Long>) entry.getValue();
				trys += TotalDataByGSM.getHashMapVal(val, TotalAppreciation._HttpDownloadTry.name());
				succes += TotalDataByGSM.getHashMapVal(val, TotalAppreciation._HttpDownloadSuccess.name());
				bytes += TotalDataByGSM.getHashMapVal(val, TotalAppreciation._HttpDownloadTotalBytes.name());
				times += TotalDataByGSM.getHashMapVal(val, TotalAppreciation._HttpDownloadTotalTime.name());
			}
			infoModel.setTestTimes(String.valueOf(trys));
			infoModel.setSuccessTimes(String.valueOf(succes));
			infoModel.setSuccessRate(TotalDataByGSM.getIntMultiple(succes, trys, 100, "%"));
			infoModel.setDelay("");
			infoModel.setAvgThrRate(TotalDataByGSM.getIntMultiple(bytes, times, 8f, "")); // *1000f/1000
		}
	}

	/**
	 * Http统计
	 * 
	 * @param infoModel
	 */
	@SuppressWarnings("unchecked")
	private static void buildHttpRefurbish(StateInfoModel infoModel) {
		Map<String, Map<String,Long>> httpRefreshHM = TotalDataByGSM.getInstance().getSpecialTimes().get(TotalStruct.TotalHttpType.HTTPRefresh.getHttpType());
		if (httpRefreshHM != null) {
			Iterator<Entry<String, Map<String,Long>>> iter = httpRefreshHM.entrySet().iterator();
			long trys = 0;
			long succes = 0;
			long delay = 0;
			while (iter.hasNext()) {
				Map.Entry<String, Map<String,Long>> entry = iter.next();
				HashMap<String, Long> val = (HashMap<String, Long>) entry.getValue();
				trys += TotalDataByGSM.getHashMapVal(val, TotalAppreciation._HttpRefreshTry.name());
				succes += TotalDataByGSM.getHashMapVal(val, TotalAppreciation._HttpRefreshSuccess.name());
				delay += TotalDataByGSM.getHashMapVal(val, TotalAppreciation._HttpRefreshDelay.name());
			}
			infoModel.setTestTimes(String.valueOf(trys));
			infoModel.setSuccessTimes(String.valueOf(succes));
			infoModel.setSuccessRate(TotalDataByGSM.getIntMultiple(succes, trys, 100, "%"));
			infoModel.setDelay(TotalDataByGSM.getIntMultiple(delay, succes, 1, ""));
			infoModel.setAvgThrRate("");
		}
	}

	/**
	 * FTPUp统计
	 * 
	 * @param infoModel
	 */
	private static void buildFTPUpload(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalFtp._uptrys.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalFtp._upSuccs.name()));
		infoModel.setSuccessRate(
				TotalDataByGSM.getHashMapMultiple(hMap, TotalFtp._upSuccs.name(), TotalFtp._uptrys.name(), 100, "%"));
		infoModel.setDelay("");
		infoModel.setAvgThrRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalStruct.TotalFtp._upCurrentSize.name(), // bit
				TotalStruct.TotalFtp._upCurrentTimes.name(), // ms
				8f*1000/1024, // 界面显示为kbps<------bit/ms转换成 1000f/1000
				""));
	}

	/**
	 * FTPUp统计
	 * 
	 * @param infoModel
	 */
	private static void buildFTPDownload(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalFtp._downtrys.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalFtp._downSuccs.name()));
		infoModel.setSuccessRate(
				TotalDataByGSM.getHashMapMultiple(hMap, TotalFtp._downSuccs.name(), TotalFtp._downtrys.name(), 100, "%"));
		infoModel.setDelay("");
		infoModel.setAvgThrRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalStruct.TotalFtp._downCurrentSize.name(), // bit
				TotalStruct.TotalFtp._downCurrentTimes.name(), // ms
				8f*1000/1024, // 界面显示为kbps<------bit/ms转换成 1000f/1000
				""));
	}

	/**
	 * Ping统计
	 * 
	 * @param infoModel
	 */
	@SuppressWarnings("unchecked")
	private static void buildPing(StateInfoModel infoModel) {
		Map<String, Map<String, Map<String,Long>>> specialTimesHM = TotalDataByGSM.getInstance().getSpecialTimes();
		Iterator<Entry<String, Map<String, Map<String,Long>>>> iter = specialTimesHM.entrySet().iterator();

		long trys = 0;
		long succes = 0;
		long delay = 0;
		while (iter.hasNext()) {
			Map.Entry<String, Map<String, Map<String,Long>>> entry = iter.next();
			String key = entry.getKey();
			LogUtil.w("TR", "--key:" + key);
			if (key.contains("GSM") || key.equals("WCDMA") || key.equals("TDSCDMA") || key.contains("LTE")
					|| key.contains("Other") || key.contains("EVDO") || key.contains("CDMA")) {
				Map<String, Map<String,Long>> pingDataHM = entry.getValue();
				if (pingDataHM != null) {
					Iterator<Entry<String, Map<String,Long>>> iter2 = pingDataHM.entrySet().iterator();
					int k = 0;
					while (iter2.hasNext() && k < 2) {
						Map.Entry<String, Map<String,Long>> entry2 = iter2.next();
						HashMap<String, Long> val = (HashMap<String, Long>) entry2.getValue();

						trys += TotalDataByGSM.getHashMapVal(val, TotalAppreciation._pingTry.name());
						succes += TotalDataByGSM.getHashMapVal(val, TotalAppreciation._pingSuccess.name());
						delay += TotalDataByGSM.getHashMapVal(val, TotalAppreciation._pingDelay.name());

						k++;
					}
				}
			}
		}
		infoModel.setTestTimes(String.valueOf(trys));
		infoModel.setSuccessTimes(String.valueOf(succes));
		infoModel.setSuccessRate(TotalDataByGSM.getIntMultiple(succes, trys, 100, "%"));
		infoModel.setDelay(TotalDataByGSM.getIntMultiple(delay, succes, 1, ""));
		infoModel.setAvgThrRate("");
	}

	/**
	 * 被叫统计
	 * 
	 * @param infoModel
	 */
	private static void buildPassivityCall(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalDial._mtTrys.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalDial._mtConnects.name()));
		infoModel.setSuccessRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalStruct.TotalDial._mtConnects.name(),
				TotalStruct.TotalDial._mtTrys.name(), 100, "%"));
		infoModel.setDelay(TotalDataByGSM.getHashMapMultiple(hMap, TotalStruct.TotalDial._mtCalldelay.name(),
				TotalStruct.TotalDial._mtDelaytimes.name(), 0.001f, ""));
		infoModel.setAvgThrRate("");
	}

	/**
	 * 主叫统计
	 * 
	 * @param infoModel
	 */
	private static void buildInitiativeCall(StateInfoModel infoModel) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		infoModel.setTestTimes(TotalDataByGSM.getHashMapValue(hMap, TotalDial._moTrys.name()));
		infoModel.setSuccessTimes(TotalDataByGSM.getHashMapValue(hMap, TotalDial._moConnects.name()));
		infoModel.setSuccessRate(TotalDataByGSM.getHashMapMultiple(hMap, TotalStruct.TotalDial._moConnects.name(),
				TotalStruct.TotalDial._moTrys.name(), 100, "%"));
		infoModel.setDelay(TotalDataByGSM.getHashMapMultiple(hMap, TotalStruct.TotalDial._moCalldelay.name(),
				TotalStruct.TotalDial._moDelaytimes.name(), 0.001f, ""));
		infoModel.setAvgThrRate("");
	}

	/**
	 * 当前任务不统计或不存在时，置空
	 * 
	 * @param infoModel
	 */
	private static void buildDefault(StateInfoModel infoModel) {
		infoModel.setTestTimes("");
		infoModel.setSuccessTimes("");
		infoModel.setSuccessRate("");
		infoModel.setDelay("");
		infoModel.setAvgThrRate("");
	}
}
