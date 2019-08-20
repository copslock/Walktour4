package com.dingli.seegull.test;

import android.util.Log;

import com.dingli.seegull.SeeGullFlags.ProtocolCodes;
import com.dingli.seegull.SeeGullFlags.ScanTypes;
import com.dingli.seegull.model.ChannelModel;
import com.dingli.seegull.model.ColorCodeModel;
import com.dingli.seegull.model.EtopNModel;
import com.dingli.seegull.model.RssiModel;
import com.dingli.seegull.model.ScanTaskModel;
import com.dingli.seegull.model.TopNModel;

import java.util.ArrayList;
import java.util.List;

/*
 * 网络制式    频段        频点号		中心频率
 * FDD        Band3       1650			1850Mhz
 * TDD        Band38      38100		2605Mhz
 * TDD        Band39      38350		1890Mhz
 * 
 * CDMA 283
 * EVDO 37
 * WCDMA 10688 10663 10713
 * TDSCDMA 10114 10077 10092 10081
 * 
 *                频点  							BandCode   ProtocolCode 
 *1 GSM          30-50							0x0600  		0x0001
 *2 CDMA           283      					0x0100			0x0005 
 *3 EVDO           37 							0x0100		 	0x0006
 *4 WCDMA 		10688 10663 10713				0x0300			0x0004
 *5 TDSCDMA   10114 10077 10092 10081  			0x2703 			0x0008
 *6 FDD 			1650						0x0700			0x000A
 * 
 *7 TDD			38100							0x1C03			0x000B
 * TDD			38350							0x1F03			0x000B
 * 
 * 
 */
public class TestModels {
	public List<ScanTaskModel> getTestModelList() {
		ArrayList<ScanTaskModel> list = new ArrayList<ScanTaskModel>();
		
		list.add(getRssiScanModelForGSM());		//0
		list.add(getColorCodeScanModelForGSM());//1
		
		list.add(getRssiScanModelForCDMA());	//2
		list.add(getTopNPilotScanModelForCDMA());//3
		
		list.add(getRssiScanModelForEVDO());	//4
		list.add(getTopNPilotScanModelForEVDO());//5
		
		list.add(getRssiScanModelForWCDMA());	  //6
		list.add(getTopNPilotScanModelForWCDMA());//7
		
		list.add(getRssiScanModelForTDSCDMA());	//8
		list.add(getTopNPilotScanModelForTDSCDMA());//9
		
		list.add(getRssiScanModelForFDD());		//10
		list.add(getETopNScanModelForFDD());	//11
		
		list.add(getRssiScanModelForTDD());		//13
		list.add(getETopNScanModelForTDD());	//14
		return list;		
	}
	
	
	
	//1.GSM ===================================================================
	//ArrayList<ChannelModel> mGSMChannelList = getGSMChannelList();
	private ArrayList<ChannelModel> getGSMChannelList() {
		ChannelModel channelModel = new ChannelModel();
		channelModel.setBandCode(0x0600);
		channelModel.setStartChannel(30);
		channelModel.setEndChannel(50);
		ArrayList<ChannelModel> list = new ArrayList<ChannelModel>();
		list.add(channelModel);
		Log.i("getGSMChannelList", "ChannelList="+list);
		return list;
	}	
	public ScanTaskModel getRssiScanModelForGSM() {				
		RssiModel model = new RssiModel();
		model.setChannelList(getGSMChannelList());
		model.setEnable(1);
		model.setProtocolCode(ProtocolCodes.PROTOCOL_GSM);
		model.setScanMode(0);
		model.setScanType(ScanTypes.eScanType_RssiChannel);
		model.setStyle(0);
		model.setTaskName("RssiScanForGSM");
		model.setTaskType("Normal");
		model.setUpload(false);// true 上行， false 下行		
		Log.i("getRssiScanModelForGSM", "ChannelList="+model.getChannelList());
		return model;		
	}
	public ScanTaskModel getColorCodeScanModelForGSM() {
		ColorCodeModel model = new ColorCodeModel();
		model.setChannelList(getGSMChannelList());
		model.setCI(true);
		model.setColorCode(true);
		model.setEnable(1);
		model.setL3Msg(false);
		model.setMultipleColorCode(false);
		model.setProtocolCode(ProtocolCodes.PROTOCOL_GSM);
		model.setRssiThreshold(-120.0F);
		model.setScanType(ScanTypes.eScanType_ColorCode);
		model.setScanMode(0);
		model.setStyle(0);
		model.setTaskName("ColorCodeScanForGSM");
		model.setTaskType("Normal");
		model.setUpload(false);				
		return model;
	}	
	
	
	//2.CDMA============283,0x0100,0x0005===========================
	ArrayList<ChannelModel> mCDMAChannelList = getCDMAChannelList();
	private ArrayList<ChannelModel> getCDMAChannelList() {
		ArrayList<ChannelModel> list = new ArrayList<ChannelModel>();
		ChannelModel channelModel = new ChannelModel();
		channelModel.setBandCode(0x0100);
		channelModel.setStartChannel(283);
		channelModel.setEndChannel(283);			
		list.add(channelModel);			
		return list;
	}
	public ScanTaskModel getRssiScanModelForCDMA() {				
		RssiModel model = new RssiModel();
		model.setChannelList(getCDMAChannelList());
		model.setEnable(1);
		model.setProtocolCode(ProtocolCodes.PROTOCOL_IS_2000_CDMA);
		model.setScanMode(0);
		model.setScanType(ScanTypes.eScanType_RssiChannel);
		model.setStyle(0);
		model.setTaskName("RssiScanForCDMA");
		model.setTaskType("Normal");
		model.setUpload(false);// true 上行， false 下行		
		return model;		
	}	
	public ScanTaskModel getTopNPilotScanModelForCDMA() {
		TopNModel model = new TopNModel();
		model.setChannelList(getCDMAChannelList());
		model.setEnable(1);
		model.setNumberOfPilots(32);
		model.setPilotMode(1);
		model.setPilotThreshold(-30.0f);
		model.setProtocolCode(ProtocolCodes.PROTOCOL_IS_2000_CDMA);
		model.setScanMode(0);
		model.setScanType(ScanTypes.eScanType_TopNPilot);
		model.setStyle(0);
		model.setTaskName("TopNPilotScanForCDMA");
		model.setTaskType("Normal");
		model.setUpload(false);		
		
		//DATA_MODE_LIST = {0x0001,0x0004,0x008,0x0010,0x0400};
		model.setAggregateEcIoEnable(1);
		model.setDelaySpreadEnable(1);
		model.setEcEnable(1);
		model.setEcioEnable(1);
		model.setEpsIoEnable(0);
		model.setEssIoEnable(0);
		model.setBchLayer3MessageDecodingEnable(0);
		model.setRakeFingerCountEnable(0);
		model.setSirEnable(0);
		model.setTimeOffsetEnable(1);
		
		return model;
	}
	
	
	//3.EVDO============37,0x0100,0x0006===========================
	//ArrayList<ChannelModel> mEVDOChannelList = getEVDOChannelList();
	private ArrayList<ChannelModel> getEVDOChannelList() {
		ArrayList<ChannelModel> list = new ArrayList<ChannelModel>();
		ChannelModel channelModel = new ChannelModel();
		channelModel.setBandCode(0x0100);
		channelModel.setStartChannel(37);
		channelModel.setEndChannel(37);			
		list.add(channelModel);			
		return list;
	}
	public ScanTaskModel getRssiScanModelForEVDO() {				
		RssiModel model = new RssiModel();
		model.setChannelList(getEVDOChannelList());
		model.setEnable(1);
		model.setProtocolCode(ProtocolCodes.PROTOCOL_IS_856_EVDO);
		model.setScanMode(0);
		model.setScanType(ScanTypes.eScanType_RssiChannel);
		model.setStyle(0);
		model.setTaskName("RssiScanForEVDO");
		model.setTaskType("Normal");
		model.setUpload(false);// true 上行， false 下行		
		return model;		
	}	
	public ScanTaskModel getTopNPilotScanModelForEVDO() {
		TopNModel model = new TopNModel();
		model.setChannelList(getEVDOChannelList());
		//model.setEcio(0x0001);
		model.setEnable(1);
		model.setNumberOfPilots(32);
		model.setPilotMode(1);
		model.setPilotThreshold(-30.0f);
		model.setProtocolCode(ProtocolCodes.PROTOCOL_IS_856_EVDO);
		model.setScanMode(0);
		model.setScanType(ScanTypes.eScanType_TopNPilot);
		//model.setSir(0x0200);
		model.setStyle(0);
		model.setTaskName("TopNPilotScanForEVDO");
		model.setTaskType("Normal");
		model.setUpload(false);	

		//DATA_MODE_LIST = {0x0001,0x0004,0x008,0x0010,0x0400};
		model.setAggregateEcIoEnable(1);
		model.setDelaySpreadEnable(1);
		model.setEcEnable(1);
		model.setEcioEnable(1);
		model.setEpsIoEnable(0);
		model.setEssIoEnable(0);
		model.setBchLayer3MessageDecodingEnable(0);
		model.setRakeFingerCountEnable(0);
		model.setSirEnable(0);
		model.setTimeOffsetEnable(1);
		return model;
	}	
	
	//4.WCDMA============{10688,10663,10713},0x0300,0x0004===========================
	//ArrayList<ChannelModel> mWCDMAChannelList = getWCDMAChannelList();
	private ArrayList<ChannelModel> getWCDMAChannelList() {
		ArrayList<ChannelModel> list = new ArrayList<ChannelModel>();
		ChannelModel channelModel = new ChannelModel();
		channelModel.setBandCode(0x0300);
		channelModel.setStartChannel(10688);
		channelModel.setEndChannel(10688);			
		list.add(channelModel);
		channelModel = new ChannelModel();
		channelModel.setBandCode(0x0300);
		channelModel.setStartChannel(10663);
		channelModel.setEndChannel(10663);
		list.add(channelModel);
		channelModel = new ChannelModel();
		channelModel.setBandCode(0x0300);
		channelModel.setStartChannel(10713);
		channelModel.setEndChannel(10713);		
		list.add(channelModel);
		return list;
	}
	public ScanTaskModel getRssiScanModelForWCDMA() {				
		RssiModel model = new RssiModel();
		model.setChannelList(getWCDMAChannelList());
		model.setEnable(1);
		model.setProtocolCode(ProtocolCodes.PROTOCOL_3GPP_WCDMA);
		model.setScanMode(0);
		model.setScanType(ScanTypes.eScanType_RssiChannel);
		model.setStyle(0);
		model.setTaskName("RssiScanForWCDMA");
		model.setTaskType("Normal");
		model.setUpload(false);// true 上行， false 下行		
		return model;		
	}	
	public ScanTaskModel getTopNPilotScanModelForWCDMA() {
		TopNModel model = new TopNModel();
		model.setChannelList(getWCDMAChannelList());
		model.setEnable(1);
		model.setNumberOfPilots(32);
		model.setPilotMode(1);
		model.setPilotThreshold(-30.0f);
		model.setProtocolCode(ProtocolCodes.PROTOCOL_3GPP_WCDMA);
		model.setScanMode(0);
		model.setScanType(ScanTypes.eScanType_TopNPilot);
		model.setStyle(0);
		model.setTaskName("TopNPilotScanForWCDMA");
		model.setTaskType("Normal");
		model.setUpload(false);		

		//DATA_MODE_LIST = {0x0001,0x0004,0x008,0x0010,0x0020,0x0040,0x0100,0x0200,0x0400};
		model.setAggregateEcIoEnable(1);
		model.setDelaySpreadEnable(1);
		model.setEcEnable(1);
		model.setEcioEnable(1);
		model.setEpsIoEnable(1);
		model.setEssIoEnable(1);
		model.setBchLayer3MessageDecodingEnable(0);
		model.setRakeFingerCountEnable(1);
		model.setSirEnable(1);
		model.setTimeOffsetEnable(1);
		return model;
	}
	
	//5.TDSCDMA============{10114,10077,10092,10081},0x2703,0x0008===========================
	 //ArrayList<ChannelModel> mTDSCDMAChannelList = getTDSCDMAChannelList();
	private ArrayList<ChannelModel> getTDSCDMAChannelList() {
		ArrayList<ChannelModel> list = new ArrayList<ChannelModel>();
		ChannelModel channelModel = new ChannelModel();
		channelModel.setBandCode(0x2703);
		channelModel.setStartChannel(10114);
		channelModel.setEndChannel(10114);			
		list.add(channelModel);
		channelModel = new ChannelModel();
		channelModel.setBandCode(0x2703);
		channelModel.setStartChannel(10077);
		channelModel.setEndChannel(10077);
		list.add(channelModel);
		channelModel = new ChannelModel();
		channelModel.setBandCode(0x2703);
		channelModel.setStartChannel(10092);
		channelModel.setEndChannel(10092);
		list.add(channelModel);
		channelModel = new ChannelModel();
		channelModel.setBandCode(0x2703);
		channelModel.setStartChannel(10081);
		channelModel.setEndChannel(10081);
		list.add(channelModel);		
		return list;
	}
	public ScanTaskModel getRssiScanModelForTDSCDMA() {				
		RssiModel model = new RssiModel();
		model.setChannelList(getTDSCDMAChannelList());
		model.setEnable(1);
		model.setProtocolCode(ProtocolCodes.PROTOCOL_TDSCDMA);
		model.setScanMode(0);
		model.setScanType(ScanTypes.eScanType_RssiChannel);
		model.setStyle(0);
		model.setTaskName("RssiScanForTDSCDMA");
		model.setTaskType("Normal");
		model.setUpload(false);// true 上行， false 下行		
		return model;		
	}	
	public ScanTaskModel getTopNPilotScanModelForTDSCDMA() {
		TopNModel model = new TopNModel();
		model.setEnable(1);
		model.setTaskName("TopNPilotScanForTDSCDMA");
		model.setTaskType("Normal");
		model.setProtocolCode(ProtocolCodes.PROTOCOL_TDSCDMA);
		model.setScanType(ScanTypes.eScanType_TopNPilot);
		model.setScanMode(0);
		model.setStyle(0);	
		model.setUpload(false);	
		model.setChannelList(getTDSCDMAChannelList());		
		model.setPilotMode(4);//3-syncDlPilotMode  4- midamblePilotMode
		model.setNumberOfPilots(16);
		model.setPilotThreshold(-30.0f);		
	
		//DATA_MODE_LIST = {0x0001/*,0x0004,0x0020,0x0200,0x0400,0x4000*/};
		model.setAggregateEcIoEnable(0);
		model.setDelaySpreadEnable(0);
		model.setEcEnable(1);
		model.setEcioEnable(1);
		model.setEpsIoEnable(1);
		model.setEssIoEnable(0);
		model.setBchLayer3MessageDecodingEnable(0);						//层三信息值必须为0，否则没法测试，因为扫频仪不够权限
		model.setRakeFingerCountEnable(0);
		model.setSirEnable(1);
		model.setTimeOffsetEnable(1);
		
		return model;
	}		
	
	//6.FDD_LTE===================1650,0x0700,0x000A==============================
	//ArrayList<ChannelModel> mFDDChannelList = getFDDChannelList();
	private ArrayList<ChannelModel> getFDDChannelList() {
		ArrayList<ChannelModel> list = new ArrayList<ChannelModel>();
		ChannelModel channelModel = new ChannelModel();
		channelModel.setBandCode(0x0700);
		channelModel.setStartChannel(1650);
		channelModel.setEndChannel(1650);			
		list.add(channelModel);
		return list;
	}	
	public ScanTaskModel getRssiScanModelForFDD() {				
		RssiModel model = new RssiModel();
		model.setChannelList(getFDDChannelList());
		model.setEnable(1);
		model.setProtocolCode(ProtocolCodes.PROTOCOL_LTE);
		model.setScanMode(0);
		model.setScanType(ScanTypes.eScanType_RssiChannel);
		model.setStyle(6);
		model.setTaskName("RssiScanForFDD");
		model.setTaskType("Normal");
		model.setUpload(false);// true 上行， false 下行		
		return model;		
	}		
	public  ScanTaskModel getETopNScanModelForFDD() {
		EtopNModel model = new EtopNModel();
		model.setCarrierRssiThreshold(-120.0f);
		model.setChannelList(getFDDChannelList());
		model.setCyclicPrefix(0);
		model.setEnable(1);
		model.setMeasurementThreshold(-150.0f);
		model.setNumberOfRxAntennaPorts(1);
		model.setNumberOfSignals(16);
		model.setNumberOfSubBands(4);
		model.setNumberOfTxAntennaPorts(0);
		model.setOperationalMode(9);
		model.setProtocolCode(ProtocolCodes.PROTOCOL_LTE);
		model.setRefMeasurementThreshold(-150.0f);
		model.setRefOperationalMode(9);
		model.setRefWideBand(21);//要根据protocol来判断 TDD25起 FDD18起
		model.setScanMode(0);
		model.setScanType(ScanTypes.eScanType_eTopNSignal);
		model.setStyle(6);
		model.setSubBandSize(4);
		model.setSubBandStart(0);
		model.setSyncMeasurementThreshold(-150.0f);
		model.setSyncOperationalMode(9);
		model.setSyncWideBand(14);
		model.setTaskName("ETopNScanForFDD");
		model.setTaskType("Normal");
		model.setUpload(false);
		model.setWideBand(13);
		return model;
	}	
	
	
	//7.TDD_LTE==============={38100,0x1C03},{38350,0x1F03},0x000B=================================
	 //ArrayList<ChannelModel> mTDDChannelList = getTDDChannelList();
	private  ArrayList<ChannelModel> getTDDChannelList() {
		ArrayList<ChannelModel> list = new ArrayList<ChannelModel>();
		ChannelModel channelModel = new ChannelModel();
		channelModel.setBandCode(0x1C03);
		channelModel.setStartChannel(38100);
		channelModel.setEndChannel(38100);			
		list.add(channelModel);
		channelModel = new ChannelModel();
		channelModel.setBandCode(0x1F03);
		channelModel.setStartChannel(38350);
		channelModel.setEndChannel(38350);
		list.add(channelModel);
		return list;
	}
	public ScanTaskModel getRssiScanModelForTDD() {				
		RssiModel model = new RssiModel();
		model.setChannelList(getTDDChannelList());
		model.setEnable(1);
		model.setProtocolCode(ProtocolCodes.PROTOCOL_TD_LTE);
		model.setScanMode(0);
		model.setScanType(ScanTypes.eScanType_RssiChannel);
		model.setStyle(6);
		model.setTaskName("RssiScanForTDD");
		model.setTaskType("Normal");
		model.setUpload(false);// true 上行， false 下行		
		return model;		
	}		
	public ScanTaskModel getETopNScanModelForTDD() {
		EtopNModel model = new EtopNModel();
		model.setCarrierRssiThreshold(-120.0f);
		model.setChannelList(getTDDChannelList());
		model.setCyclicPrefix(0);
		model.setEnable(1);
		model.setMeasurementThreshold(-150.0f);
		model.setNumberOfRxAntennaPorts(1);
		model.setNumberOfSignals(16);
		model.setNumberOfSubBands(4);
		model.setNumberOfTxAntennaPorts(0);
		model.setOperationalMode(9);
		model.setProtocolCode(ProtocolCodes.PROTOCOL_TD_LTE);
		model.setRefMeasurementThreshold(-150.0f);
		model.setRefOperationalMode(9);
		model.setRefWideBand(28);//要根据protocol来判断 TDD25起 FDD18起
		model.setScanMode(0);
		model.setScanType(ScanTypes.eScanType_eTopNSignal);
		model.setStyle(6);
		model.setSubBandSize(4);
		model.setSubBandStart(0);
		model.setSyncMeasurementThreshold(-150.0f);
		model.setSyncOperationalMode(9);
		model.setSyncWideBand(14);
		model.setTaskName("ETopNScanForTDD");
		model.setTaskType("Normal");
		model.setUpload(false);
		model.setWideBand(13);
		return model;
	}	
	
}