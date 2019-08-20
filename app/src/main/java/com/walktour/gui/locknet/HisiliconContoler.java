package com.walktour.gui.locknet;

import android.content.Context;

import com.dinglicom.dataset.DatasetManager;
import com.walktour.base.util.LogUtil;

/**
 * 
 * HisiliconContoler 海思芯片强制 2015-1-14
 * 
 * @version 1.0.0
 * @author wuqing.tang@dinglicom.com
 */
public class HisiliconContoler extends ForceControler {

	public static final int NETWORK_AUTO 	= -1;
	public static final int NETWORK_GSM 	= 0;
	public static final int NETWORK_WCDMA 	= 1;
	public static final int NETWORK_TDSCDMA = 2;
	public static final int NETWORK_LTE 	= 3;

	public static final int eATNULL 			= 0;	//
	public static final int eATLTE_PowerON 		= 1;	//不带参数
	public static final int eATLTE_PowerOFF 	= 2;	//不带参数
	public static final int eATLTE_Attach 		= 3;	//不带参数
	public static final int eATLTE_Detach 		= 4;	//不带参数
	public static final int eATLTE_GetIP 		= 5;	//不带参数
	public static final int eATLTE_ResetDev 	= 6;	//不带参数
	public static final int eATLTE_ReleaseIP 	= 7;	//不带参数
	public static final int eATLTE_SetAPN 		= 8;	//暂不做
	public static final int eATLTE_SetIMSAPN 	= 9;	//暂不做
	public static final int eATLTE_SetDefualtAPN= 10;	//暂不做
	public static final int eAT_LockNetwork 	= 11; 	//带参数: Network Type=xx\r\n
	public static final int eATLTE_LockBand 	= 12;	//带参数：Band=xx\r\n
	public static final int eTSLTE_LockFreq 	= 13;	//带参数：Band=xx\r\nEARFCN=xx\r\n
	public static final int eTSLTE_LockCell 	= 14;	//带参数：Band=xx\r\nEARFCN=xx\r\nPCI=xx\r\n
	public static final int eTSLTE_Unlock 		= 15;	//不带参数
	public static final int eTSLTE_HandoverReq 	= 16;	//暂不做
	public static final int eTSLTE_CselReq 		= 17;	//暂不做
	public static final int eTSLTE_BarCellAccessReq = 18;//暂不做
	public static final int eAGSM_LockBand 		= 19;	//带参数：Band=xx\r\n
	public static final int eAGSM_LockFreq 		= 20;	//带参数：Band=xx\r\nARFCN=xx\r\n
	public static final int eAGSM_UnLock 		= 21;	//不带参数
	public static final int eAWcdma_LockFreq 	= 22;	//暂不做
	public static final int eAWcdma_LockPSC 	= 23;	//暂不做
	public static final int eAWcdma_Unlock 		= 24;	//暂不做
	public static final int eUEMoodifyNVParam 	= 25;	//暂不做
	

	@Override
	public boolean init() {
		return true;
	}

	@Override
	public boolean lockNetwork(ForceNet networkType) {
		return false;
	}

	@Override
	public boolean lockNetwork(Context context,ForceNet networkType) {
		int type = NETWORK_AUTO;
		switch (networkType) {
		case NET_AUTO:
			type = NETWORK_AUTO;
			break;
		case NET_GSM:
			type = NETWORK_GSM;
			break;
		case NET_WCDMA:
			type = NETWORK_WCDMA;
			break;
		case NET_TDSCDMA:
			type = NETWORK_TDSCDMA;
			break;
		case NET_FDD_LTE:
		case NET_TDD_LTE:
		case NET_LTE:
			type = NETWORK_LTE;
			break;
		default:
			break;
		}

		String netWork = String.format("Network Type=%s\r\n", type);
		return DatasetManager.getInstance(context).devWritePortExt(eAT_LockNetwork, netWork.getBytes()) == 1;
	}
	
	@Override
	public boolean unLockAll(ForceNet forceNets) {
		return true;
	}

	@Override
	public void release() {

	}

	@Override
	public boolean queryBand(ForceNet netType) {
		return false;
	}

	@Override
	public boolean queryFrequency(ForceNet netType) {
		return false;
	}

	@Override
	public boolean queryCell(ForceNet netType) {
		return false;
	}

	@Override
	public boolean lockBand(ForceNet netType, String arg) {
		return false;
	}

	@Override
	public boolean lockBand(ForceNet netType, Band[] bands) {
		return false;
	}

	@Override
	public boolean lockBand(Context context, ForceNet netType, Band[] bands) {
		if(netType == ForceNet.NET_LTE){
			String arg = bands[0].toString();
			String buffer =String.format("Band=%s\r\n", arg.substring(0,arg.indexOf("(")).trim()) ;
			
			return DatasetManager.getInstance(context).devWritePortExt(eATLTE_LockBand, buffer.getBytes()) == 1;
		}
		return false;
	}
	
	@Override
	public boolean lockFrequency(Context context, ForceNet netType, String... args) {
		if(netType == ForceNet.NET_LTE){
			String buffer =String.format("Band=%s\r\nEARFCN=%s\r\n", args[0],args[1]) ;
			return DatasetManager.getInstance(context).devWritePortExt(eTSLTE_LockFreq, buffer.getBytes()) == 1;
		}
		return false;
	}

	@Override
	public boolean lockCell(Context context, ForceNet netType, String... args) {
		if(netType == ForceNet.NET_LTE){
			try{
			String buffer =String.format("Band=%s\r\nEARFCN=%s\r\nPCI=%s\r\n", args[0],args[1],args[2]) ;
			DatasetManager.getInstance(context).devWritePortExt(eTSLTE_LockCell, buffer.getBytes());
			Thread.sleep(1000);
			DatasetManager.getInstance(context).devWritePortExt(eATLTE_PowerOFF, "".getBytes());
			Thread.sleep(1000);
			DatasetManager.getInstance(context).devWritePortExt(eATLTE_PowerON, "".getBytes());
			}catch(Exception e){
				LogUtil.w("", "");
			}
			return  true;
		}
		return false;
	}

	@Override
	public boolean campCell(ForceNet netType, String arg1, String arg2) {
		return false;
	}

	@Override
	public boolean setAirplaneModeSwitch(Context context, boolean flag) {
		return false;
	}

	@Override
	public boolean setVolteSwitch(Context context, boolean flag) {
		return false;
	}

	@Override
	public boolean unlockFrequency(Context context, ForceNet networkType) {
		return DatasetManager.getInstance(context).devWritePortExt(eTSLTE_Unlock, "".getBytes()) == 1;
	}

	@Override
	public boolean unlockCell(Context context, ForceNet networkType) {
		return DatasetManager.getInstance(context).devWritePortExt(eTSLTE_Unlock, "".getBytes()) == 1;
	}

	//@Override
	public boolean setScrambleState(Context context, boolean flag){
		return  false;
	}

	@Override
	public boolean setAPN(Context context, String arg) {
		return false;
	}

	@Override
	public void makeVideoCall(Context context, String number) { return; }
}
