package com.walktour.Utils;

import android.content.Context;
import android.content.Intent;

import com.walktour.gui.task.activity.scannertsma.model.TestSchemaType;
import com.dingli.seegull.SeeGullFlags.ScanIDShow;
import com.dinglicom.dataset.DatasetManager;
import com.walktour.Utils.WalkStruct.NetType;
import com.walktour.Utils.WalkStruct.UMPCEventType;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.task.activity.scanner.model.CdmaCpichPilotModel;
import com.walktour.gui.task.activity.scanner.model.ColorCodeParseModel;
import com.walktour.gui.task.activity.scanner.model.LteCellDataPilotModel;
import com.walktour.gui.task.activity.scanner.model.LtePssPilotModel;
import com.walktour.gui.task.activity.scanner.model.LteRsPilotModel;
import com.walktour.gui.task.activity.scanner.model.LteSssPilotModel;
import com.walktour.gui.task.activity.scanner.model.RssiParseModel;
import com.walktour.gui.task.activity.scanner.model.TdScdmaPilotModel;
import com.walktour.gui.task.activity.scanner.model.WcdmaCpichPilotModel;
import com.walktour.gui.task.activity.scanner.model.WcdmaPschPilotModel;
import com.walktour.gui.task.activity.scanner.model.WcdmaSschPilotModel;
import com.walktour.model.UmpcTestInfo;
import com.walktour.model.YwDataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 获得测试过程中，手机端返回给服务端测试过程中的相关参数信息
 * @author tangwq
 *
 */
public class UmpcSwitchMethod {
    private final static String tag = "UmpcSwitchMethod";
	private final static String Null= "-9999"; 	//上传参数无效值
	
	/**
	 * 根据当前网络类型，获得该网络下的参数显示信息，不区分测试业务
	 * @param netType 传入当前配置的网络类型
	 * @return
	 */
	public static String getCallBackPara(int netType){
		String callBack = "";
		if(netType == NetType.CDMA.getNetType()){
			callBack = "Frequency="  + handleParameter(getParaValue(UnifyParaID.C_Frequency))
			        + ",Refer_PN="   + handleParameter(getParaValue(UnifyParaID.C_ReferencePN))
			        + ",Refer_EcIo=" + handleParameter(getParaValue(UnifyParaID.C_ReferenceEcIo))
			        + ",Refer_Ec="	 + handleParameter(getParaValue(UnifyParaID.C_ReferenceEc))
			        + ",Total_EcIo=" + handleParameter(getParaValue(UnifyParaID.C_TotalEcIo))
			        + ",Total_Ec="   + handleParameter(getParaValue(UnifyParaID.C_TotalEc))
			        + ",RxAGC="      + handleParameter(getParaValue(UnifyParaID.C_RxAGC))
			        + ",TxAGC="      + handleParameter(getParaValue(UnifyParaID.C_TxAGC))
			        + ",TxPower="    + handleParameter(getParaValue(UnifyParaID.C_TxPower))
			        + ",State="      + handleParameter(UtilsMethodPara.byValue2Enum(Long.toHexString(UnifyParaID.C_State)))
			        + ",SID="        + handleParameter(getParaValue(UnifyParaID.C_SID))
			        + ",NID="        + handleParameter(getParaValue(UnifyParaID.C_NID))
			        + ",BID="        + handleParameter(getParaValue(UnifyParaID.C_BID))
			        + ",Max_PN="     + handleParameter(getParaValue(UnifyParaID.C_MaxEcIoPN))
			        + ",Max_EcIo="   + handleParameter(getParaValue(UnifyParaID.C_MaxEcIo))
			        + ",Max_Ec="     + handleParameter(getParaValue(UnifyParaID.C_MaxEc))
			        + ",FFER="		 + handleParameter(getParaValue(UnifyParaID.C_FFER))
			        + ",TxGainAdj="  + handleParameter(getParaValue(UnifyParaID.C_TxGainAdj))
			        + ",A-Set_Num="  + handleParameter(getParaValue(UnifyParaID.C_ActiveSetNum))
			        + getCdmaNeighborStr();
			
		}else if(netType == NetType.WCDMA.getNetType()){
			//UmtsServingCell umtsServing = TraceInfoInterface.traceData.getUmtsServingCell();
			callBack = "MCC-MNC=" 		+ handleParameter(getParaValue(UnifyParaID.W_Ser_MCC)) + "/" 
										+ handleParameter(getParaValue(UnifyParaID.W_Ser_MNC)) +
			        ",UL-DL_UARFCN="	+ handleParameter(getParaValue(UnifyParaID.W_Ser_UL_UARFCN)) + "/" 
			        					+ handleParameter(getParaValue(UnifyParaID.W_Ser_DL_UARFCN)) +
			        ",Max_PSC=" 		+ handleParameter(getParaValue(UnifyParaID.W_Ser_Max_PSC))+
			        ",Max_RSCP=" 		+ handleParameter(getParaValue(UnifyParaID.W_Ser_Max_RSCP))+
			        ",Max_EcIo=" 		+ handleParameter(getParaValue(UnifyParaID.W_Ser_Max_EcIo))+
			        ",RxPower=" 		+ handleParameter(getParaValue(UnifyParaID.W_Ser_RxPower))+
			        ",TxPower=" 		+ handleParameter(getParaValue(UnifyParaID.W_Ser_TxPower))+
			        ",Cell_Name=" 		+ handleParameter("") +
			        ",LAC=" 			+ handleParameter(getParaValue(UnifyParaID.W_Ser_LAC))+
			        ",RNC_ID-Cell_ID=" 	+ handleParameter(UtilsMethod.getLongCellIdToRNCId(getParaValue(UnifyParaID.W_Ser_RNC_ID))+ "/" 
		                    			+ UtilsMethod.getLongTosShortCellID(getParaValue(UnifyParaID.W_Ser_Cell_ID))) +
			        ",BLER=" 			+ handleParameter(getParaValue(UnifyParaID.W_Ser_BLER))+
			        ",Total_RSCP=" 		+ handleParameter(getParaValue(UnifyParaID.W_Ser_Total_RSCP))+
			        ",Total_EcIo=" 		+ handleParameter(getParaValue(UnifyParaID.W_Ser_Total_EcIo))+
			        ",SIR=" 			+ handleParameter(getParaValue(UnifyParaID.W_Ser_SIR))+
			        ",RRC_State=" 		+ handleParameter(UtilsMethodPara.getWcdmaRRCState(getParaValue(UnifyParaID.W_Ser_RRC_State))) + 
			        getWcdmaNeighborStr();
			
		}else if(netType == NetType.TDSCDMA.getNetType()){
			//TdScdmaModel tdscdmaModel = TraceInfoInterface.traceData.getTdScdmaInfo();
			callBack = "MCC-MNC=" 		+ handleParameter(getParaValue(UnifyParaID.TD_Ser_MCC)) 
										+ "/" + handleParameter(getParaValue(UnifyParaID.TD_Ser_MNC)) +
			        ",UARFCN=" 			+ handleParameter(getParaValue(UnifyParaID.TD_Ser_UARFCN)) + 
			        ",DCH_UARFCN=" 		+ handleParameter(getParaValue(UnifyParaID.TD_Ser_DCHURAFCN)) + 
			        ",CPI=" 			+ handleParameter(getParaValue(UnifyParaID.TD_Ser_CPI)) + 
			        ",CarrierRSSI=" 	+ handleParameter(getParaValue(UnifyParaID.TD_Ser_CarrierRSSI)) + 
			        ",PCCPCH_RSCP=" 	+ handleParameter(getParaValue(UnifyParaID.TD_Ser_PCCPCHRSCP)) + 
			        ",PCCPCH_ISCP=" 	+ handleParameter(getParaValue(UnifyParaID.TD_Ser_PCCPCHISCP)) + 
			        ",PCCPCH_C-I=" 		+ handleParameter(getParaValue(UnifyParaID.TD_Ser_PCCPCHC2I)) + 
			        ",PCCPCH_SIR=" 		+ handleParameter(getParaValue(UnifyParaID.TD_Ser_PCCPCHSIR)) + 
			        ",PCCPCH_Pathloss=" + handleParameter(getParaValue(UnifyParaID.TD_Ser_PCCPCHPathloss)) + 
			        ",Main_State=" 		+ handleParameter(UtilsMethodPara.byValue2Enum(Long.toHexString(UnifyParaID.TD_Ser_Main_State))) +
			        ",Connected_State=" + handleParameter(UtilsMethodPara.byValue2Enum(Long.toHexString(UnifyParaID.TD_Ser_Connected_State))) +
			        ",LAC=" 			+ handleParameter(getParaValue(UnifyParaID.TD_Ser_LAC)) + 
			        ",Cell_ID=" 		+ handleParameter(getParaValue(UnifyParaID.TD_Ser_CellID)) + 
			        ",RNC_ID=" 			+ handleParameter(getParaValue(UnifyParaID.TD_Ser_RNCID)) + 
			        ",URA_ID=" 			+ handleParameter(getParaValue(UnifyParaID.TD_Ser_URAID)) + 
			        ",UpPCH_TxPower=" 	+ handleParameter(getParaValue(UnifyParaID.TD_Ser_UpPCHTxPower)) + 
			        ",DPCH_RSCP=" 		+ handleParameter(getParaValue(UnifyParaID.TD_Ser_DPCHRSCP)) + 
			        ",DPCH_ISCP=" 		+ handleParameter(getParaValue(UnifyParaID.TD_Ser_DPCHISCP)) + 
			        ",DPCH_C-I=" 		+ handleParameter(getParaValue(UnifyParaID.TD_Ser_DPCHC2I)) + 
			        ",UE_TxPower=" 		+ handleParameter(getParaValue(UnifyParaID.TD_Ser_UETxPower)) + 
			        ",TA=" 				+ handleParameter(getParaValue(UnifyParaID.TD_Ser_TA)) + 
			        ",BLER=" 			+ handleParameter(getParaValue(UnifyParaID.TD_Ser_BLER)) 
			        + getTdscdmaNeighborStr();
			
		}else if(netType == NetType.GSM.getNetType()){
			//GsmServingCellMeas gsmServing = TraceInfoInterface.traceData.getGsmServingCellMeas();
			callBack = "MCC-MNC=" 	+handleParameter(getParaValue(UnifyParaID.G_Ser_MCC)) 
									+ "/" + handleParameter(getParaValue(UnifyParaID.G_Ser_MNC)) +
			          ",BCCH=" 		+ handleParameter(getParaValue(UnifyParaID.G_Ser_BCCH))+
			          ",BSIC=" 		+ handleParameter(getParaValue(UnifyParaID.G_Ser_BSIC))+
			          ",BCCHLev=" 	+ handleParameter(getParaValue(UnifyParaID.G_Ser_BCCHLev))+
			          ",TxPower=" 	+ handleParameter(getParaValue(UnifyParaID.G_Ser_TxPower))+
			          ",TA=" 		+ handleParameter(getParaValue(UnifyParaID.G_Ser_TA))+
			          ",RxLevFull=" + handleParameter(getParaValue(UnifyParaID.G_Ser_RxLevFull))+
			          ",RxLevSub=" 	+ handleParameter(getParaValue(UnifyParaID.G_Ser_RxLevSub))+
			          ",DTX=" 		+ handleParameter(getParaValue(UnifyParaID.G_Ser_DTX))+
			          ",LAC=" 		+ handleParameter(getParaValue(UnifyParaID.G_Ser_LAC))+
			          ",Cell_ID=" 	+ handleParameter(getParaValue(UnifyParaID.G_Ser_Cell_ID))+
			           ",V-Codec=" 	+ handleParameter(UtilsMethodPara.byValue2Enum(Long.toHexString(UnifyParaID.G_Ser_V_Codec))) +
			          ",TS=" 		+ handleParameter(getParaValue(UnifyParaID.G_Ser_TS))+
			          ",RLT=" 		+ handleParameter(getParaValue(UnifyParaID.G_Ser_RLT))+
			          ",TCH_C/I=" 	+ handleParameter(getParaValue(UnifyParaID.G_Ser_TCH_C2I))+
			          ",RxQualFull="+ handleParameter(getParaValue(UnifyParaID.G_Ser_RxQualFull))+
			          ",RxQualSub=" + handleParameter(getParaValue(UnifyParaID.G_Ser_RxQualSub))+
			          ",State=" 	+ handleParameter(UtilsMethodPara.getGsmRRStateStr(getParaValue(UnifyParaID.G_Ser_State)))+
			          //",Cell_ID=" 	+ handleParameter(getParaValue(UnifyParaID.G_Ser_Cell_ID))+ 
			          getGsmNeighborStr();
			
		}else if(netType == NetType.LTE.getNetType()){
			//LteServingCellMeas lteModel = TraceInfoInterface.traceData.getLteServingCellMeas();
			callBack = "MCC-MNC=" 		+ handleParameter(getParaValue(UnifyParaID.L_SRV_MCC)) + "/"
										+ handleParameter(getParaValue(UnifyParaID.L_SRV_MNC)) +
			        ",UL-DL_Freq=" 		+ handleParameter(getParaValue(UnifyParaID.L_SRV_UL_Freq)) + "/" 
										+ handleParameter(getParaValue(UnifyParaID.L_SRV_DL_Freq)) +
			        ",Work_Mode=" 		+ handleParameter(UtilsMethodPara.byValue2Enum(Long.toHexString(UnifyParaID.L_SRV_Work_Mode))) +
			        ",EARFCN_DL=" 		+ handleParameter(getParaValue(UnifyParaID.L_SRV_EARFCN))+
			        ",EARFCN_UL=" 		+ handleParameter(getParaValue(UnifyParaID.L_EARFCN_UL))+
			        ",RSRP=" 			+ handleParameter(getParaValue(UnifyParaID.L_SRV_RSRP))+
			        ",RSRQ=" 			+ handleParameter(getParaValue(UnifyParaID.L_SRV_RSRQ))+
			        ",SINR=" 			+ handleParameter(getParaValue(UnifyParaID.L_SRV_SINR))+
			        ",CRS_SINR=" 		+ handleParameter(getParaValue(UnifyParaID.L_SRV_CRS_SINR))+
			        ",DRS_SINR=" 		+ handleParameter(getParaValue(UnifyParaID.L_SRV_DRS_SINR))+
			        ",SRS_Power=" 		+ handleParameter(getParaValue(UnifyParaID.L_SRV_SRS_Power))+
			        ",EMM_State=" 		+ handleParameter(UtilsMethodPara.byValue2Enum(Long.toHexString(UnifyParaID.L_SRV_EMM_State)))+
			        ",TM=" 				+ handleParameter(UtilsMethodPara.byValue2Enum(Long.toHexString(UnifyParaID.L_SRV_TM))) +
			        ",UL-DL_Bandwidth=" + handleParameter(getParaValue(UnifyParaID.LTECA_UL_BandWidth)) + "/" 
			        					+ handleParameter(getParaValue(UnifyParaID.L_SRV_DL_BandWidth)) +
			        ",Band=" 			+ handleParameter(getParaValue(UnifyParaID.L_SRV_Band))+
			        ",PCI=" 			+ handleParameter(getParaValue(UnifyParaID.L_SRV_PCI))+
			        ",RSSI=" 			+ handleParameter(getParaValue(UnifyParaID.L_SRV_RSSI))+
			        ",Pathloss=" 		+ handleParameter(getParaValue(UnifyParaID.L_SRV_Pathloss))+
			        ",SRS_RB_Num=" 		+ handleParameter(getParaValue(UnifyParaID.L_SRV_SRS_RB_Num))+
			        ",TAC=" 			+ handleParameter(getParaValue(UnifyParaID.L_SRV_TAC))+
			        //",ECGI=" 			+ handleParameter(getParaValue(UnifyParaID.L_SRV_ECGI))+
			        ",RRC_State=" 		+ handleParameter(UtilsMethodPara.byValue2Enum(Long.toHexString(UnifyParaID.CURRENT_STATE_LTE)))+
			        ",EMM_Substate="	+ handleParameter(UtilsMethodPara.byValue2Enum(Long.toHexString(UnifyParaID.L_SRV_EMM_Substate)))+
			        ",QCI="	+ handleParameter(getParaValue(UnifyParaID.L_SYS_QCI))+
			        ",PUSCH_TxPower="	+ handleParameter(getParaValue(UnifyParaID.L_CH1_PUSCH_TxPower))+
			        ",PUCCH_TxPower="	+ handleParameter(getParaValue(UnifyParaID.L_CH1_PUCCH_TxPower))+
			        ",ECGI=" 			+ handleParameter(UtilsMethodPara.byValue2Enum(Long.toHexString(UnifyParaID.L_SRV_ECGI)))+
			        ",PUSCH_rank_Indicator="+handleParameter(getParaValue(UnifyParaID.LTE_RANK_INDICATOR))     	+
					",Uu_TA=" 				+handleParameter(getParaValue(UnifyParaID.L_SYS_Uu_TA))                  	+
					",RACH_Type=" 			+handleParameter(getParaValue(UnifyParaID.L_RACH_Type))                  	+
					",RACH_Result=" 		+handleParameter(getParaValue(UnifyParaID.L_RACH_Result))                	+
					",Max_Preamble_Number=" +handleParameter(getParaValue(UnifyParaID.L_Max_Preamble_Number))       	+
					",CP_Type=" 			+handleParameter(getParaValue(UnifyParaID.L_CP_Type))                     	+
					",PDSCH_serving=" 		+handleParameter(getParaValue(UnifyParaID.L_PDSCH_serving_RB_count_slot)) +
					",PDSCH_Schedule=" 		+handleParameter(getParaValue(UnifyParaID.L_PDSCH_Schedule_RB_count_slot))+
					",PUSCH_serving=" 		+handleParameter(getParaValue(UnifyParaID.L_PUSCH_serving_RB_count_slot)) +
					",PUSCH_Schedule=" 		+handleParameter(getParaValue(UnifyParaID.L_PUSCH_Schedule_RB_count_slot))+
					",PDSCH_utilization=" 	+handleParameter(UtilsMethodPara.byValue2Enum(Long.toHexString(UnifyParaID.L_PDSCH_RB_utilization)))+
					",PUSCH_utilization=" 	+handleParameter(UtilsMethodPara.byValue2Enum(Long.toHexString(UnifyParaID.L_PUSCH_RB_utilization)))+ 

			        getLteNeighborStr() + getLteCaStr() + getLteQCIConfig() + getCAwideband() + getAppThr();
			        
		}else if(netType == NetType.EVDO.getNetType()){
		    callBack = "Frequency="           + handleParameter(getParaValue(UnifyParaID.E_Carrier1_EV_Frequency)) 
		             + ",PN="                 + handleParameter(getParaValue(UnifyParaID.E_Carrier1_ServingSectorPN))
		             + ",SectorID24="         + (getParaValue(UnifyParaID.E_EVsectorInfo).split(",").length > 1 ? getParaValue(UnifyParaID.E_EVsectorInfo).split(",")[0] : "")
		             + ",RxAGC0="             + handleParameter(getParaValue(UnifyParaID.E_Carrier1_EV_RxAGC0))
		             + ",Total_SINR="         + handleParameter(getParaValue(UnifyParaID.E_Carrier1_TotalSINR))
		             + ",DRC_Rate="           + handleParameter(getParaValue(UnifyParaID.E_Carrier1_DRC_Value))
		             + ",DRC_Cover="          + handleParameter(getParaValue(UnifyParaID.E_Carrier1_DRC_Cover))
		             + ",A-Set_Count="        + handleParameter(getParaValue(UnifyParaID.E_ActiveCount))
		             + ",Band="               + handleParameter(UtilsMethodPara.getEvdoBand(getParaValue(UnifyParaID.E_Band)))
		             + ",UATI="               + handleParameter(getParaValue(UnifyParaID.E_UATI))
		             + ",ServUserNum="        + handleParameter(getParaValue(UnifyParaID.E_DedicateUserCount))
		             + ",RxAGC1="             + handleParameter(getParaValue(UnifyParaID.E_Carrier1_EV_RxAGC1))
		             + ",TxAGC="              + handleParameter(getParaValue(UnifyParaID.E_Carrier1_EV_TxAGC))
		             + ",TxPilotPower="       + handleParameter(getParaValue(UnifyParaID.E_Carrier1_TxPilotPower))
		             + ",TxOpenLoopPower="    + handleParameter(getParaValue(UnifyParaID.E_Carrier1_TxOpenLoopPower))
		             + ",TxCloseLoopAdjust="  + handleParameter(getParaValue(UnifyParaID.E_Carrier1_TxClosedLoopAdjust))
		             + getEvdoNeighborStr();
		    
		}
		//2018/8/14 上报参数添加采样点和时间
		int currentIndex = DatasetManager.getInstance(WalktourApplication.getAppContext()).getCurrentIndex();
		callBack += ",Point_Index=" + currentIndex
				+ ",Point_Time=" + System.currentTimeMillis();
		
		//LogUtil.w(tag, "--callBack:" + callBack);
		return callBack;
	}
	
	
	
	
	
	/**
	 * 获得GSM邻区字符串值
	 * @return
	 */
	private static String getGsmNeighborStr(){
		StringBuffer n_BCCH 	= new StringBuffer(",N_BCCH=");
		StringBuffer n_BSIC 	= new StringBuffer(",N_BSIC=");
		StringBuffer n_Cell_ID 	= new StringBuffer(",N_Cell_ID=");
		StringBuffer n_RxLev 	= new StringBuffer(",N_RxLev=");
		StringBuffer n_C1 		= new StringBuffer(",N_C1=");
		StringBuffer n_C2 		= new StringBuffer(",N_C2=");
		/*for(int i = 0 ; i<TraceInfoInterface.traceData.getGsmNeighborCell().size();i++){
		    GsmNeighborCell gsmN  = TraceInfoInterface.traceData.getGsmNeighborCell().get(i);
		    n_BCCH.append(handleParameter(gsmN.getbCCHARFCN()));
		    n_BSIC.append(handleParameter(gsmN.getbSIC()));
		    n_Cell_ID.append(handleParameter(gsmN.getCellID()));
		    n_RxLev.append(handleParameter(gsmN.getRxLev()));
		    n_C1.append(handleParameter(gsmN.getC1()));
		    n_C2.append(handleParameter(gsmN.getC2()));
		    if(i != (TraceInfoInterface.traceData.getGsmNeighborCell().size() - 1)){
		        n_BCCH.append("/");
		        n_BSIC.append("/");
		        n_Cell_ID.append("/");
		        n_RxLev.append("/");
		        n_C1.append("/");
		        n_C2.append("/");
		    }
		}*/
		n_BCCH.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N1_BCCH)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N2_BCCH)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N3_BCCH)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N4_BCCH)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N5_BCCH)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N6_BCCH)));
		n_BSIC.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N1_BSIC)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N2_BSIC)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N3_BSIC)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N4_BSIC)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N5_BSIC)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N6_BSIC)));
		n_Cell_ID.append(handleParameter(""))
				.append("/")
				.append(handleParameter(""))
				.append("/")
				.append(handleParameter(""))
				.append("/")
				.append(handleParameter(""))
				.append("/")
				.append(handleParameter(""))
				.append("/")
				.append(handleParameter(""));
		n_RxLev.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N1_RxLevel)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N2_RxLevel)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N3_RxLevel)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N4_RxLevel)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N5_RxLevel)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N6_RxLevel)));
		n_C1.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N1_C1)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N2_C1)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N3_C1)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N4_C1)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N5_C1)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N6_C1)));
		n_C2.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N1_C2)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N2_C2)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N3_C2)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N4_C2)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N5_C2)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.G_NCell_N6_C2)));
		
		return n_BCCH.toString() + n_BSIC.toString() + n_Cell_ID.toString()
                + n_RxLev.toString()  + n_C1.toString() + n_C2.toString();
	}
	
	/**
	 * 获得WCDMA邻区列表字符串<BR>
	 * [功能详细描述]
	 * @return
	 */
	public static String getWcdmaNeighborStr(){
        StringBuffer n_Freq 	= new StringBuffer(",N_Freq=");
        StringBuffer n_PSC 		= new StringBuffer(",N_PSC=");
        StringBuffer n_Cell_ID 	= new StringBuffer(",N_Cell_ID=");
        StringBuffer n_RSCP 	= new StringBuffer(",N_RSCP=");
        StringBuffer n_EcIo 	= new StringBuffer(",N_EcIo=");
        StringBuffer n_Set 		= new StringBuffer(",N_Set=");
        String[] servingNeighbor = getParaValue(UnifyParaID.W_TUMTSCellInfoV2).split(";");
        for (int i = 0; servingNeighbor != null && i < servingNeighbor.length - 1; i++) {
        	String[] neighbor = servingNeighbor[i+1].split(",");
        	
            n_Freq.append(handleParameter(neighbor[1]));
            n_PSC.append(handleParameter(neighbor[2]));
            n_Cell_ID.append(handleParameter(""));
            n_RSCP.append(UtilsMethod.numToShowDecimal2(handleParameter(neighbor[5])));
            n_EcIo.append(UtilsMethod.numToShowDecimal2(handleParameter(neighbor[4])));
            n_Set.append(handleParameter(UtilsMethodPara.getWcdmaSetType(neighbor[0])));
            if(i != (servingNeighbor.length - 2)){
                n_Freq.append("/");
                n_PSC.append("/");
                n_Cell_ID.append("/");
                n_RSCP.append("/");
                n_EcIo.append("/");
                n_Set.append("/");
            }
        }
	    return n_Freq.toString() + n_PSC.toString() + n_Cell_ID.toString() + n_RSCP.toString() 
	            + n_EcIo.toString() + n_Set;
	    
	}
	
	
	/**
	 * 拼接LteCa参数串
	 * @return
	 */
	private static String getLteCaStr() {
		StringBuffer lteCaStr = new StringBuffer(",");
		lteCaStr.append("LTE_UE_Category=" + getParaValue(UnifyParaID.L_SYS_UE_Category)+",")
		.append("CA_Info_PCell="+ handleParameter(UtilsMethodPara.getLteWorkModel(getParaValue(UnifyParaID.LTECA_WorkMode_PCell))) + "/"
								+ getParaValue(UnifyParaID.LTECA_TAC_PCell) + "/" 
								+ getParaValue(UnifyParaID.LTECA_CellID_PCell) + "/" 
								+ getParaValue(UnifyParaID.LTECA_Band_PCell) + "/" 
								+ getParaValue(UnifyParaID.LTECA_DLEARFCN_PCell) + "/" 
								+ getParaValue(UnifyParaID.LTECA_ULEARFCN_PCell) + "/" 
								+ getParaValue(UnifyParaID.LTECA_PCI_PCell) + "/" 
								+ getParaValue(UnifyParaID.LTECA_BandWidth_PCell) + "/" 
								+ getParaValue(UnifyParaID.LTECA_UL_BandWidth) + "/" 
								+ handleParameter(UtilsMethodPara.getLteTM(getParaValue(UnifyParaID.LTECA_TM_PCell))) + "/"
								+ getParaValue(UnifyParaID.LTECA_Freq_PCell) + "/" 
								+ getParaValue(UnifyParaID.L_SRV_UL_Freq) + "/" 
								+ getParaValue(UnifyParaID.LTECA_CodeWordNum_PCell) + ",")
								
		.append("CA_Info_SCell1=" 	+ handleParameter(UtilsMethodPara.getLteWorkModel(getParaValue(UnifyParaID.LTECA_WorkMode_SCell1))) + "/"
									+ getParaValue(UnifyParaID.LTECA_TAC_SCell1) + "/" 
									+ getParaValue(UnifyParaID.LTECA_CellID_SCell1) + "/" 
									+ getParaValue(UnifyParaID.LTECA_Band_SCell1) + "/" 
									+ getParaValue(UnifyParaID.LTECA_DLEARFCN_SCell1) + "/" 
									+ "-/" 
									+ getParaValue(UnifyParaID.LTECA_PCI_SCell1) + "/" 
									+ getParaValue(UnifyParaID.LTECA_BandWidth_SCell1) + "/"
									+ "-/" 
									+ handleParameter(UtilsMethodPara.getLteTM(getParaValue(UnifyParaID.LTECA_TM_SCell1))) + "/"
									+ getParaValue(UnifyParaID.LTECA_Freq_SCell1) + "/" 
									+ "-/" 
									+ getParaValue(UnifyParaID.LTECA_CodeWordNum_SCell1) + ",")
									
		.append("CA_Info_SCell2=" 	+ handleParameter(UtilsMethodPara.getLteWorkModel(getParaValue(UnifyParaID.LTECA_WorkMode_SCell2))) + "/"
									+ getParaValue(UnifyParaID.LTECA_TAC_SCell2) + "/" 
									+ getParaValue(UnifyParaID.LTECA_CellID_SCell2) + "/" 
									+ getParaValue(UnifyParaID.LTECA_Band_SCell2) + "/" 
									+ getParaValue(UnifyParaID.LTECA_DLEARFCN_SCell2) + "/" 
									+ "-/" 
									+ getParaValue(UnifyParaID.LTECA_PCI_SCell2) + "/" 
									+ getParaValue(UnifyParaID.LTECA_BandWidth_SCell2) + "/" 
									+  "-/" 
									+ handleParameter(UtilsMethodPara.getLteTM(getParaValue(UnifyParaID.LTECA_TM_SCell2))) + "/"
									+ getParaValue(UnifyParaID.LTECA_Freq_SCell2) + "/" 
									+  "-/" 
									+ getParaValue(UnifyParaID.LTECA_CodeWordNum_SCell2) + ",")
									
		.append("CA_Meas_PCell=" 	+ getParaValue(UnifyParaID.LTECA_RSSI_PCell) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSSIRx0_PCell) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSSIRx1_PCell) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSRP_PCell) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSRPRx0_PCell) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSRPRx1_PCell) + "/" 
									+ getParaValue(UnifyParaID.LTECA_CRSRP_PCell) + "/" 
									+ getParaValue(UnifyParaID.LTECA_SINR_PCell) + "/" 
									+ getParaValue(UnifyParaID.LTECA_SINRRx0_PCell) + "/" 
									+ getParaValue(UnifyParaID.LTECA_SINRRx1_PCell) + "/" 
									+ getParaValue(UnifyParaID.LTECA_CRSSINR_PCell) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSRQ_PCell) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSRQRx0_PCell) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSRQRx1_PCell) + ",")
									
		.append("CA_Meas_SCell1=" 	+ getParaValue(UnifyParaID.LTECA_RSSI_SCell1) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSSIRx0_SCell1) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSSIRx1_SCell1) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSRP_SCell1) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSRPRx0_SCell1) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSRPRx1_SCell1) + "/" 
									+ getParaValue(UnifyParaID.LTECA_CRSRP_SCell1) + "/" 
									+ getParaValue(UnifyParaID.LTECA_SINR_SCell1) + "/" 
									+ getParaValue(UnifyParaID.LTECA_SINRRx0_SCell1) + "/" 
									+ getParaValue(UnifyParaID.LTECA_SINRRx1_SCell1) + "/" 
									+ getParaValue(UnifyParaID.LTECA_CRSSINR_SCell1) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSRQ_SCell1) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSRQRx0_SCell1) + "/"
									+ getParaValue(UnifyParaID.LTECA_RSRQRx1_SCell1) + ",")
									
		.append("CA_Meas_SCell2=" 	+ getParaValue(UnifyParaID.LTECA_RSSI_SCell2) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSSIRx0_SCell2) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSSIRx1_SCell2) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSRP_SCell2) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSRPRx0_SCell2) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSRPRx1_SCell2) + "/" 
									+ getParaValue(UnifyParaID.LTECA_CRSRP_SCell2) + "/" 
									+ getParaValue(UnifyParaID.LTECA_SINR_SCell2) + "/" 
									+ getParaValue(UnifyParaID.LTECA_SINRRx0_SCell2) + "/" 
									+ getParaValue(UnifyParaID.LTECA_SINRRx1_SCell2) + "/" 
									+ getParaValue(UnifyParaID.LTECA_CRSSINR_SCell2) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSRQ_SCell2) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSRQRx0_SCell2) + "/" 
									+ getParaValue(UnifyParaID.LTECA_RSRQRx1_SCell2) + ",")
									
		.append("CA_Thr_Total=" + changeScaleValue(UnifyParaID.L_Thr_DL_FTP,1000) + "/"
								+ changeScaleValue(UnifyParaID.L_Thr_DL_PDCP_Thr,1000) + "/"
								+ changeScaleValue(UnifyParaID.L_Thr_DL_RLC_Thr,1000) + "/"
								+ changeScaleValue(UnifyParaID.LTECA_MACThrDL_Total,1000) + "/" 
								+ changeScaleValue(UnifyParaID.LTECA_PhyThrDL_Total,1000) 
								+ ",")
								
		.append("CA_Thr_PCell=" + "-/-/-/"
								+ changeScaleValue(UnifyParaID.L_Thr_DL_MAC_Thr,1000) + "/" 
								+ changeScaleValue(UnifyParaID.L_Thr_DL_Phy_Thr,1000) 
								+ ",")
		.append("CA_Thr_SCell1=" + "-/-/-/"
								 + changeScaleValue(UnifyParaID.LTECA_MACThrDL_SCell1,1000) + "/" 
								 + changeScaleValue(UnifyParaID.LTECA_PhyThrDL_SCell1,1000) 
								 + ",")
		.append("CA_Thr_SCell2=" + "-/-/-/"
								 + changeScaleValue(UnifyParaID.LTECA_MACThrDL_SCell2,1000) + "/" 
								 + changeScaleValue(UnifyParaID.LTECA_PhyThrDL_SCell2,1000) 
								 + ",")
								 
		.append("CA_RB_Total=" 	+ getParaValue(UnifyParaID.LTECA_RBCount_Total) + "/" 
								+ getParaValue(UnifyParaID.LTECA_RBCountSl_Total) + "/" 
								+ getParaValue(UnifyParaID.LTECA_SlotCount_Total)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_SubFNCount_Total)+ "/" 
								+ "-/" 
								+ getParaValue(UnifyParaID.LTECA_TBSizeCode0_Total)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_TBSizeCode1_Total) + ",")
								
		.append("CA_RB_PCell=" 	+ getParaValue(UnifyParaID.LTECA_RBCount_PCell) + "/" 
								+ getParaValue(UnifyParaID.LTECA_RBCountSl_PCell) + "/" 
								+ getParaValue(UnifyParaID.LTECA_SlotCount_PCell)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_SubFNCount_PCell)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_ScheduleRate_PCell)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_TBSizeCode0_PCell)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_TBSizeCode1_PCell) + ",")
								
		.append("CA_RB_SCell1=" + getParaValue(UnifyParaID.LTECA_RBCount_SCell1) + "/" 
								+ getParaValue(UnifyParaID.LTECA_RBCountSl_SCell1) + "/" 
								+ getParaValue(UnifyParaID.LTECA_SlotCount_SCell1)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_SubFNCount_SCell1)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_ScheduleRate_SCell1)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_TBSizeCode0_SCell1)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_TBSizeCode1_SCell1) + ",")
								
		.append("CA_RB_SCell2=" + getParaValue(UnifyParaID.LTECA_RBCountSl_SCell2) + "/" 
								+ getParaValue(UnifyParaID.LTECA_RBCount_SCell2) + "/" 
								+ getParaValue(UnifyParaID.LTECA_SlotCount_SCell2)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_SubFNCount_SCell2)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_ScheduleRate_SCell2)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_TBSizeCode0_SCell2)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_TBSizeCode1_SCell2) + ",")
								
		.append("CA_Grant_PCell=" 	+ getParaValue(UnifyParaID.LTECA_PDCCHDLGrant_PCell) + "/" 
									+ getParaValue(UnifyParaID.LTECA_PDCCHULGrant_PCell) + ",")
		.append("CA_Grant_SCell1=" 	+ getParaValue(UnifyParaID.LTECA_PDCCHDLGrant_SCell1) + "/" 
									+ getParaValue(UnifyParaID.LTECA_PDCCHDLGrant_SCell2) + ",")
		.append("CA_Grant_SCell2=" 	+ getParaValue(UnifyParaID.LTECA_PDCCHULGrant_SCell2) + "/" 
									+ getParaValue(UnifyParaID.LTECA_PDCCHULGrant_SCell2) + ",")
									
		.append("CA_BLER_Total="+ getParaValue(UnifyParaID.LTECA_BLER_Total) + "/" 
								+ getParaValue(UnifyParaID.LTECA_BLERCode0_Total) + "/" 
								+ getParaValue(UnifyParaID.LTECA_BLERCode1_Total)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_InitialBLER_Total)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_InitialBLERCode0_Total)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_InitialBLERCode1_Total) + "/" 
								+ getParaValue(UnifyParaID.LTECA_ResidualBLER_Total) + "/" 
								+ getParaValue(UnifyParaID.LTECA_ResidualBLERCode0_Total)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_ResidualBLERCode1_Total) + ",")
								
		.append("CA_BLER_PCell=" + getParaValue(UnifyParaID.LTECA_BLER_PCell) + "/" 
								 + getParaValue(UnifyParaID.LTECA_BLERCode0__PCell) + "/" 
								 + getParaValue(UnifyParaID.LTECA_BLERCode1_PCell)+ "/" 
								 + getParaValue(UnifyParaID.LTECA_InitialBLER_PCell)+ "/" 
								 + getParaValue(UnifyParaID.LTECA_InitialBLERCode0_PCell)+ "/" 
								 + getParaValue(UnifyParaID.LTECA_InitialBLERCode1_PCell) + "/" 
								 + getParaValue(UnifyParaID.LTECA_ResidualBLER_PCell) + "/" 
								 + getParaValue(UnifyParaID.LTECA_ResidualBLERCode0_PCell)+ "/" 
								 + getParaValue(UnifyParaID.LTECA_ResidualBLERCode1_PCell) + ",")
								 
		.append("CA_BLER_SCell1=" + getParaValue(UnifyParaID.LTECA_BLER_SCell1) + "/" 
								  + getParaValue(UnifyParaID.LTECA_BLERCode0__SCell1) + "/" 
								  + getParaValue(UnifyParaID.LTECA_BLERCode1_SCell1)+ "/" 
								  + getParaValue(UnifyParaID.LTECA_InitialBLER_SCell1)+ "/" 
								  + getParaValue(UnifyParaID.LTECA_InitialBLERCode0_SCell1)+ "/" 
								  + getParaValue(UnifyParaID.LTECA_InitialBLERCode1_SCell1) + "/" 
								  + getParaValue(UnifyParaID.LTECA_ResidualBLER_SCell1) + "/" 
								  + getParaValue(UnifyParaID.LTECA_ResidualBLERCode0_SCell1)+ "/" 
								  + getParaValue(UnifyParaID.LTECA_ResidualBLERCode1_SCell1) + ",")
								  
		.append("CA_BLER_SCell2=" + getParaValue(UnifyParaID.LTECA_BLER_SCell2) + "/" 
								  + getParaValue(UnifyParaID.LTECA_BLERCode0__SCell2) + "/" 
								  + getParaValue(UnifyParaID.LTECA_BLERCode1_SCell2)+ "/" 
								  + getParaValue(UnifyParaID.LTECA_InitialBLER_SCell2)+ "/" 
								  + getParaValue(UnifyParaID.LTECA_InitialBLERCode0_SCell2)+ "/" 
								  + getParaValue(UnifyParaID.LTECA_InitialBLERCode1_SCell2) + "/" 
								  + getParaValue(UnifyParaID.LTECA_ResidualBLER_SCell2) + "/" 
								  + getParaValue(UnifyParaID.LTECA_ResidualBLERCode0_SCell2)+ "/" 
								  + getParaValue(UnifyParaID.LTECA_ResidualBLERCode1_SCell2) + ",")
		
		.append("CA_MCS_Total=" + getParaValue(UnifyParaID.LTECA_QPSK_Total) + "/" 
								+ getParaValue(UnifyParaID.LTECA_16QAM_Total) + "/" 
								+ getParaValue(UnifyParaID.LTECA_64QAM_Total)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_QPSKCode0_Total)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_16QAMCode0_Total)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_64QAMCode0_Total) + "/" 
								+ getParaValue(UnifyParaID.LTECA_QPSKCode1_Total) + "/" 
								+ getParaValue(UnifyParaID.LTECA_16QAMCode1_Total) + "/" 
								+ getParaValue(UnifyParaID.LTECA_64QAMCode1_Total) + "/" 
								+ getParaValue(UnifyParaID.LTECA_MCSAvg_Total) + ",")
								
		.append("CA_MCS_PCell=" + getParaValue(UnifyParaID.LTECA_QPSK_PCell) + "/" 
								+ getParaValue(UnifyParaID.LTECA_16QAM_PCell) + "/" 
								+ getParaValue(UnifyParaID.LTECA_64QAM_PCell)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_QPSKCode0_PCell)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_16QAMCode0_PCell)+ "/" 
								+ getParaValue(UnifyParaID.LTECA_64QAMCode0_PCell) + "/" 
								+ getParaValue(UnifyParaID.LTECA_QPSKCode1_PCell) + "/" 
								+ getParaValue(UnifyParaID.LTECA_16QAMCode1_PCell) + "/" 
								+ getParaValue(UnifyParaID.LTECA_64QAMCode1_PCell) + "/" 
								+ getParaValue(UnifyParaID.LTECA_MCSAvg_PCell) + ",")
								
		.append("CA_MCS_SCell1=" + getParaValue(UnifyParaID.LTECA_QPSK_SCell1) + "/" 
								 + getParaValue(UnifyParaID.LTECA_16QAM_SCell1) + "/" 
								 + getParaValue(UnifyParaID.LTECA_64QAM_SCell1)+ "/" 
								 + getParaValue(UnifyParaID.LTECA_QPSKCode0_SCell1)+ "/" 
								 + getParaValue(UnifyParaID.LTECA_16QAMCode0_SCell1)+ "/" 
								 + getParaValue(UnifyParaID.LTECA_64QAMCode0_SCell1) + "/" 
								 + getParaValue(UnifyParaID.LTECA_QPSKCode1_SCell1) + "/" 
								 + getParaValue(UnifyParaID.LTECA_16QAMCode1_SCell1) + "/" 
								 + getParaValue(UnifyParaID.LTECA_64QAMCode1_SCell1) + "/" 
								 + getParaValue(UnifyParaID.LTECA_MCSAvg_SCell1) + ",")
								 
		.append("CA_MCS_SCell2=" + getParaValue(UnifyParaID.LTECA_QPSK_SCell2) + "/" 
								 + getParaValue(UnifyParaID.LTECA_16QAM_SCell2) + "/" 
								 + getParaValue(UnifyParaID.LTECA_64QAM_SCell2)+ "/" 
								 + getParaValue(UnifyParaID.LTECA_QPSKCode0_SCell2)+ "/" 
								 + getParaValue(UnifyParaID.LTECA_16QAMCode0_SCell2)+ "/" 
								 + getParaValue(UnifyParaID.LTECA_64QAMCode0_SCell2) + "/" 
								 + getParaValue(UnifyParaID.LTECA_QPSKCode1_SCell2) + "/" 
								 + getParaValue(UnifyParaID.LTECA_16QAMCode1_SCell2) + "/" 
								 + getParaValue(UnifyParaID.LTECA_64QAMCode1_SCell2) + "/" 
								 + getParaValue(UnifyParaID.LTECA_MCSAvg_SCell2));
		
		return lteCaStr.toString();
	}
	
	private static String getLteQCIConfig(){
		StringBuffer qciConfig = new StringBuffer();
		qciConfig.append(",QCI_UL=");
		qciConfig.append(handleParameter(getParaValue(UnifyParaID.L_QCI_1_Config_ul))+ "/" +
						handleParameter(getParaValue(UnifyParaID.L_QCI_2_Config_ul))+ "/" +
						handleParameter(getParaValue(UnifyParaID.L_QCI_3_Config_ul))+ "/" +
						handleParameter(getParaValue(UnifyParaID.L_QCI_4_Config_ul))+ "/" +
						handleParameter(getParaValue(UnifyParaID.L_QCI_5_Config_ul))+ "/" +
						handleParameter(getParaValue(UnifyParaID.L_QCI_6_Config_ul))+ "/" +
						handleParameter(getParaValue(UnifyParaID.L_QCI_7_Config_ul))+ "/" +
						handleParameter(getParaValue(UnifyParaID.L_QCI_8_Config_ul))+ "/" +
						handleParameter(getParaValue(UnifyParaID.L_QCI_9_Config_ul)));
		qciConfig.append(",QCI_DL=");
		qciConfig.append(handleParameter(getParaValue(UnifyParaID.L_QCI_1_Config_dl))+ "/" +
						handleParameter(getParaValue(UnifyParaID.L_QCI_2_Config_dl))+ "/" +
						handleParameter(getParaValue(UnifyParaID.L_QCI_3_Config_dl))+ "/" +
						handleParameter(getParaValue(UnifyParaID.L_QCI_4_Config_dl))+ "/" +
						handleParameter(getParaValue(UnifyParaID.L_QCI_5_Config_dl))+ "/" +
						handleParameter(getParaValue(UnifyParaID.L_QCI_6_Config_dl))+ "/" +
						handleParameter(getParaValue(UnifyParaID.L_QCI_7_Config_dl))+ "/" +
						handleParameter(getParaValue(UnifyParaID.L_QCI_8_Config_dl))+ "/" +
						handleParameter(getParaValue(UnifyParaID.L_QCI_9_Config_dl)));
		return qciConfig.toString();
	}
	
	private static String getCAwideband(){
		StringBuffer wideband = new StringBuffer();
		wideband.append(",CQI_PCell=");
		wideband.append(handleParameter(getParaValue(UnifyParaID.CA_Wideband_CQI_code0_pc))+ "/" +
						handleParameter(getParaValue(UnifyParaID.CA_Wideband_CQI_code1_pc)));
		wideband.append(",CQI_PCell1=");
		wideband.append(handleParameter(getParaValue(UnifyParaID.CA_Wideband_CQI_code0_pc1))+ "/" +
						handleParameter(getParaValue(UnifyParaID.CA_Wideband_CQI_code1_pc1)));
		wideband.append(",CQI_PCell2=");
		wideband.append(handleParameter(getParaValue(UnifyParaID.CA_Wideband_CQI_code0_pc2))+ "/" +
						handleParameter(getParaValue(UnifyParaID.CA_Wideband_CQI_code1_pc2)));
		wideband.append(",CQI_PCell3=");
		wideband.append(handleParameter(getParaValue(UnifyParaID.CA_Wideband_CQI_code0_pc3))+ "/" +
						handleParameter(getParaValue(UnifyParaID.CA_Wideband_CQI_code1_pc3)));
		wideband.append(",CQI_PCell4=");
		wideband.append(handleParameter(getParaValue(UnifyParaID.CA_Wideband_CQI_code0_pc4))+ "/" +
						handleParameter(getParaValue(UnifyParaID.CA_Wideband_CQI_code1_pc4)));
		return wideband.toString();
	}
	
	private static String getAppThr(){
		StringBuffer appThr = new StringBuffer();
		appThr.append(",APP_Thr_DL=");
		appThr.append(	changeScaleValue(UnifyParaID.L_Thr_DL_FTP,1000) + "/" +
						changeScaleValue(UnifyParaID.L_Thr_DL_HTTP,1000) + "/" +
						changeScaleValue(UnifyParaID.L_Thr_DL_EMAIL,1000) + "/" +
						changeScaleValue(UnifyParaID.L_Thr_DL_HTTPPAGE,1000) + "/" +
						changeScaleValue(UnifyParaID.L_Thr_DL_VIDEOPLA,1000));
		
		appThr.append(",APP_Thr_UL=");
		appThr.append(	changeScaleValue(UnifyParaID.L_Thr_UL_FTP,1000) + "/" +
						changeScaleValue(UnifyParaID.L_Thr_UL_HTTP,1000) + "/" +
						changeScaleValue(UnifyParaID.L_Thr_UL_EMAIL,1000) 
						+ "/-/-");
		return appThr.toString();
	}
	
	/**
	 * 计算缩放比例
	 */
	public static String changeScaleValue(int id,int scale){
		String value = "";
		try {
			value = TraceInfoInterface.getParaValue(id) + "";
			value = UtilsMethod.narrowMultiple(value, scale);
		} catch (Exception e) {
			e.printStackTrace();
			value =  "-";
		}
		return value;
	}
	
	
	
	/**
	 * 获得LTE邻区列表字符串<BR>
	 * [功能详细描述]
	 * @return
	 */
	public static String getLteNeighborStr(){
        StringBuffer n_EARFCN 	= new StringBuffer(",N_EARFCN=");
        StringBuffer n_PCI 		= new StringBuffer(",N_PCI=");
        StringBuffer n_RSRP 	= new StringBuffer(",N_RSRP=");
        StringBuffer n_RSRQ 	= new StringBuffer(",N_RSRQ=");
        StringBuffer n_RSSI 	= new StringBuffer(",N_RSSI=");
        String[] servingNeighbor = getParaValue(UnifyParaID.LTE_CELL_LIST).split(";");
        //Vector<LteNeighborCell> lteCells = TraceInfoInterface.traceData.getLteNeighborCell();
        for (int i = 0; servingNeighbor != null && i < servingNeighbor.length - 1; i++) {
            //LteNeighborCell lteCell = lteCells.get(i);
        	String[] neighbor = servingNeighbor[i+1].split(",");
        	if(neighbor[0].equals(getParaValue(UnifyParaID.L_SRV_EARFCN)) 
            		&& neighbor[1].equals(getParaValue(UnifyParaID.L_SRV_PCI))){
            	continue;
            }
            n_EARFCN.append(handleParameter(neighbor[0]));
            n_PCI.append(handleParameter(neighbor[1]));
            n_RSRP.append(handleParameter(UtilsMethod.transferByScale(neighbor[2],100)));
            n_RSRQ.append(handleParameter(UtilsMethod.transferByScale(neighbor[3],100)));
            n_RSSI.append(handleParameter(UtilsMethod.transferByScale(neighbor[4],100)));
            if(i != (servingNeighbor.length - 2)){
                n_EARFCN.append("/");
                n_PCI.append("/");
                n_RSRP.append("/");
                n_RSRQ.append("/");
                n_RSSI.append("/");
            }
        }
        
	    return n_EARFCN.toString() + n_PCI.toString() + n_RSRP.toString() +
	            n_RSRQ.toString() + n_RSSI.toString();
	}
	
	/**
	 * 获得CDMA邻区字符串值
	 * @return
	 */
	private static String getCdmaNeighborStr(){
	    StringBuffer n_Freq = new StringBuffer(",N_Freq=");
        StringBuffer n_PN   = new StringBuffer(",N_PN=");
        StringBuffer n_EcIo = new StringBuffer(",N_Ec-Io=");
        StringBuffer n_Ec   = new StringBuffer(",N_Ec=");
        String[] servingNeighbor = getParaValue(UnifyParaID.C_cdmaServingNeighbor).split(";");
		for (int i = 0; i < servingNeighbor.length -1; i++) {
		    /**neighbor:ActiveSetType,CdmaFreq,CdmaPn,CdmaRssi,CdmaRSCP,CdmaEcIo,CdmaDummy*/
            String[] neighbor = servingNeighbor[i+1].split(",");
            
		    n_Freq.append(handleParameter(neighbor[1]));
		    n_PN.append(handleParameter(neighbor[2]));
		    n_EcIo.append(UtilsMethod.numToShowDecimal2(handleParameter(neighbor[5])));
		    n_Ec.append(handleParameter(""));
		    if(i != (servingNeighbor.length - 2)){
		        n_Freq.append("/");
		        n_PN.append("/");
		        n_EcIo.append("/");
		        n_Ec.append("/");
		    }
        }
		return n_Freq.toString() + n_PN.toString() + n_EcIo.toString() + n_Ec.toString();
	}
	
	
	/**
	 * 得到EVDO邻区列表回传字段<BR>
	 * [功能详细描述]
	 * @return
	 */
	public static String getEvdoNeighborStr(){
        StringBuffer n_Freq         = new StringBuffer(",N_Freq=");
        StringBuffer n_PN           = new StringBuffer(",N_PN=");
        StringBuffer n_EcIo         = new StringBuffer(",N_Ec-Io=");
        StringBuffer n_DRC_Cover    = new StringBuffer(",N_DRC_Cover=");
        StringBuffer n_Set          = new StringBuffer(",N_Set=");
        
        String[] servingNeighbor = getParaValue(UnifyParaID.E_EVServingNeighbor).split(";");
        for (int i = 0; i < servingNeighbor.length - 1; i++) {
            String[] neighbor = servingNeighbor[i+1].split(",");
            n_Freq.append(handleParameter(neighbor[1]));
            n_PN.append(handleParameter(neighbor[2]));
            n_EcIo.append(UtilsMethod.numToShowDecimal2(handleParameter(neighbor[4])));
            n_DRC_Cover.append(handleParameter(neighbor[6]));
            n_Set.append(UtilsMethodPara.getEvdoSetType(neighbor[0]));
            
            if(i != (servingNeighbor.length - 2)){
                n_Freq.append("/");
                n_PN.append("/");
                n_EcIo.append("/");
                n_DRC_Cover.append("/");
                n_Set.append("/");
            }
        }
        return n_Freq.toString() + n_PN.toString() + n_EcIo.toString() + n_DRC_Cover.toString() + n_Set.toString();
	}
	
	/**
	 * 得到TDSCDMA 邻区列表回传字段<BR>
	 * [功能详细描述]
	 * @return
	 */
	public static String getTdscdmaNeighborStr(){
        StringBuffer n_UARFCN 		= new StringBuffer(",N_UARFCN=");
        StringBuffer n_CPI 			= new StringBuffer(",N_CPI=");
        StringBuffer n_RSCP 		= new StringBuffer(",N_RSCP=");
        StringBuffer n_CarrierRSSI 	= new StringBuffer(",N_CarrierRSSI=");
        StringBuffer n_Pathloss 	= new StringBuffer(",N_Pathloss=");
        StringBuffer n_Rn 			= new StringBuffer(",N_Rn=");
        n_UARFCN.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N1_UARFCN)))
        		.append("/")
        		.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N2_UARFCN)))
        		.append("/")
        		.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N3_UARFCN)))
        		.append("/")
        		.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N4_UARFCN)))
        		.append("/")
        		.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N5_UARFCN)))
        		.append("/")
        		.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N6_UARFCN)));
        n_CPI.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N1_CPI)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N2_CPI)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N3_CPI)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N4_CPI)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N5_CPI)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N6_CPI)));
        n_RSCP.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N1_RSCP)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N2_RSCP)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N3_RSCP)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N4_RSCP)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N5_RSCP)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N6_RSCP)));
        n_CarrierRSSI.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N1_CarrierRSSI)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N2_CarrierRSSI)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N3_CarrierRSSI)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N4_CarrierRSSI)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N5_CarrierRSSI)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N6_CarrierRSSI)));
        n_Pathloss.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N1_PathLoss)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N2_PathLoss)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N3_PathLoss)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N4_PathLoss)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N5_PathLoss)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N6_PathLoss)));
        n_Rn.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N1_Rn)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N2_Rn)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N3_Rn)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N4_Rn)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N5_Rn)))
				.append("/")
				.append(handleParameter(getParaValue(UnifyParaID.T_NCell_N6_Rn)));
        
        return n_UARFCN.toString() + n_CPI.toString() + n_RSCP.toString() + n_CarrierRSSI.toString()
                + n_Pathloss.toString() + n_Rn.toString();
	}
	
	/**
	 * 根据网络类型返回FTP测试过程中的相关网络参数信息
	 * @param netType
	 * @return
	 */
	public static String getFtpCallBackStr(int netType){
		String callBack = "";
		YwDataModel ywData = ShowInfo.getInstance().getYwDataModel();
		if(netType == NetType.CDMA.getNetType()){
			callBack =	  "Evdo_RxAGC="           + getParaValue(UnifyParaID.E_Carrier1_EV_RxAGC1)
						+",Evdo_TotalC/I="        + getParaValue(UnifyParaID.E_Carrier1_TotalC_I)
						+",Evdo_TxAGC="           + getParaValue(UnifyParaID.E_Carrier1_EV_TxAGC)
						+",Evdo_DRCRate="         + getParaValue(UnifyParaID.E_Carrier1_DRC_Value)
						+",EVSectorUserServed="   + getParaValue(UnifyParaID.E_DedicateUserCount)
						+",FTPDLAllSize="         + (ywData.getFtpDlAllSize().equals("") ? Null : ywData.getFtpDlAllSize())
						+",FTPULAllSize="         + (ywData.getFtpUlAllSize().equals("") ? Null : ywData.getFtpUlAllSize())
						+",FTPDLCurSize="         + (ywData.getFtpDlCurrentSize().equals("") ? Null : ywData.getFtpDlCurrentSize())
						+",FTPULCurSize="         + (ywData.getFtpUlCurrentSize().equals("") ? Null : ywData.getFtpUlCurrentSize())
						+", Evdo_Freq="           + getParaValue(UnifyParaID.E_Carrier1_EV_Frequency)
						+", Evdo_PN="             + getParaValue(UnifyParaID.E_ServingSectorPN);
		}else if(netType == NetType.WCDMA.getNetType()){
			//UmtsServingCell umtsServing = TraceInfoInterface.traceData.getUmtsServingCell();
			callBack =	"TotalRSCP=" + getParaValue(UnifyParaID.W_Ser_Total_RSCP)
						+",TotalEc/IO=" + getParaValue(UnifyParaID.W_Ser_Total_EcIo)
						+",TxPower=" + getParaValue(UnifyParaID.W_Ser_TxPower)
						+",BLER=" + getParaValue(UnifyParaID.W_Ser_BLER)
						+",FTPDLAllSize="+(ywData.getFtpDlAllSize().equals("") ? Null : ywData.getFtpDlAllSize())
						+",FTPULAllSize="+(ywData.getFtpUlAllSize().equals("") ? Null : ywData.getFtpUlAllSize())
						+",FTPDLCurSize="+(ywData.getFtpDlCurrentSize().equals("") ? Null : ywData.getFtpDlCurrentSize())
						+",FTPULCurSize="+(ywData.getFtpUlCurrentSize().equals("") ? Null : ywData.getFtpUlCurrentSize())
						+",Freq=" + ""
						+",PSC=" + getParaValue(UnifyParaID.W_Ser_Max_PSC)
						+",CellID=" + getParaValue(UnifyParaID.W_Ser_Cell_ID);
			
		}else if(netType == NetType.TDSCDMA.getNetType()){
			//TdScdmaModel tdScdma = TraceInfoInterface.traceData.getTdScdmaInfo();
			callBack =	"PCCPCHRSCP=" + getParaValue(UnifyParaID.TD_Ser_PCCPCHRSCP)
						+",PCCPCHCtoI=" + getParaValue(UnifyParaID.TD_Ser_PCCPCHC2I)
						+",TxPower=" + getParaValue(UnifyParaID.TD_Ser_UpPCHTxPower)
						+",BLER=" + getParaValue(UnifyParaID.TD_Ser_BLER)
						+",FTPDLAllSize="+(ywData.getFtpDlAllSize().equals("") ? Null : ywData.getFtpDlAllSize())
						+",FTPULAllSize="+(ywData.getFtpUlAllSize().equals("") ? Null : ywData.getFtpUlAllSize())
						+",FTPDLCurSize="+(ywData.getFtpDlCurrentSize().equals("") ? Null : ywData.getFtpDlCurrentSize())
						+",FTPULCurSize="+(ywData.getFtpUlCurrentSize().equals("") ? Null : ywData.getFtpUlCurrentSize())
						+",Freq="+""
						+",CPI=" + getParaValue(UnifyParaID.TD_Ser_CPI)
						+",CellID=" + getParaValue(UnifyParaID.TD_Ser_CellID);
			
		}else{
			callBack = "FTPDLAllSize="+(ywData.getFtpDlAllSize().equals("") ? Null : ywData.getFtpDlAllSize())
			+",FTPULAllSize="+(ywData.getFtpUlAllSize().equals("") ? Null : ywData.getFtpUlAllSize())
			+",FTPDLCurSize="+(ywData.getFtpDlCurrentSize().equals("") ? Null : ywData.getFtpDlCurrentSize())
			+",FTPULCurSize="+(ywData.getFtpUlCurrentSize().equals("") ? Null : ywData.getFtpUlCurrentSize());
		}
		return callBack;
	}
	/**
	 * 根据网络类型返回给服务端标志位
	 * @param netType
	 * @return
	 */
	public static char getNetTypeFlag(int netType){
		if(netType == NetType.CDMA.getNetType()){
			return UMPCEventType.NetCDMA.getUMPCEvnetType();
		}else if(netType == NetType.WCDMA.getNetType()){
			return UMPCEventType.NetWCDMA.getUMPCEvnetType();
		}else if(netType == NetType.TDSCDMA.getNetType()
				|| netType == NetType.LTETDD.getNetType()){
			return UMPCEventType.NetTDSCDMA.getUMPCEvnetType();
		}else if(netType == NetType.EVDO.getNetType()){
            return UMPCEventType.NetEVDO.getUMPCEvnetType();
        }else if(netType == NetType.LTE.getNetType()){
            return UMPCEventType.NetLTE.getUMPCEvnetType();
        }else {//if(netType == NetType.GSM.getNetType()){
			return UMPCEventType.NetGSM.getUMPCEvnetType();
		}
	}
	
	/**
	 * [往IPAD端传实时事件]<BR>
     * [功能详细描述]
     * @param context 当前上下文
     * @param type 事件类型
     * @param eventMsg 事件内容
	 */
    public static void sendEventToController(Context context,char type,String eventMsg,int controller){
        LogUtil.w(tag,"--control:"+controller + "--msg:"+eventMsg);
        if(controller == UmpcTestInfo.ControlForPioneer || controller == UmpcTestInfo.ControlForIpad){
            sendEventToUmpc(context,type,eventMsg,false);
        }
    }
    
	/**
	 * [往IPAD端传实时事件]<BR>
	 * [功能详细描述]
	 * @param context 当前上下文
	 * @param type 事件类型
	 * @param eventMsg 事件内容
	 * @param needTime 是否添加时间戳 HH:mm:ss
	 */
	public static void sendEventToUmpc(Context context,char type,String eventMsg,boolean needTime){
	    String dateStr = UtilsMethod.sdfhms.format(System.currentTimeMillis());
        
        Intent intent = new Intent(WalkMessage.UMPC_WriteRealTimeEvent);
        intent.putExtra(WalkMessage.UMPC_WriteRealTimeType, type);
        intent.putExtra(WalkMessage.UMPC_WriteRealTimeInfo, (needTime ? dateStr + " " : "") + eventMsg);
        context.sendBroadcast(intent);
	}
	
	/**
     * 参数处理<BR>
     * 若参数为空则返回-9999，否则则返回参数本身
     * @param parameter 参数
     * @return
     */
    public static String handleParameter(String parameter){
        if(parameter.equals("")){
            return Null;
        }
        return parameter;
    }
    
	/**
     * 参数处理<BR>
     * 若参数为-9999则返回-9999，否则则返回参数本身
     * @param parameter 参数
     * @return
     */
    public static String handleParameterNull(String parameter){
        if(parameter.equals(Null)){
            return "";
        }
        return parameter;
    }
    
    
    
    /**获得得参数队列中指定ID的值*/
    private static String getParaValue(int paraId){
        return TraceInfoInterface.getParaValue(paraId);
    }
    
    
    /**
     * Scanner实时回传参数
     */
   public synchronized static String getScannerCallBackParm(TestSchemaType schemaType){
    	String callBackStr = "";
    	switch (schemaType) {
		case GSMCW:
		case CDMACW:
		case EVDOCW:
		case LTECW:
		case WCDMACW:
			ArrayList<RssiParseModel> cwModel = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.ScanID_RSSI);
			if (cwModel != null && cwModel.size() != 0) {
				String netTypeStr = "";
				String n_Channel= "";
				String n_RSSI = "";
				for (int i = 0; i < cwModel.size(); i++) {
						netTypeStr = UtilsMethodPara.getNetWorkStr(cwModel.get(i).getNetType()); 
						n_Channel +=cwModel.get(i).getChannel() + "/";
						n_RSSI += cwModel.get(i).getRssi()+"/";
				}

				callBackStr = "ScannerType=CW,Network=" + netTypeStr + ",N_Channel=" + n_Channel + ",N_RSSI=" + n_RSSI;
			}
			
			break;
		
		case GSMCOLORCODE:
			ArrayList<ColorCodeParseModel> colorCodeModel = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_COLORCODE);
				
			if (colorCodeModel != null && colorCodeModel.size() != 0){
				String n_BCCH = "";
				String n_BSIC = "";
				String n_RxLev = "";
				for (int i = 0; i < colorCodeModel.size(); i++) {
					n_BCCH += colorCodeModel.get(i).getChannel() + "/" ;
					n_BSIC += colorCodeModel.get(i).getBsic() + "/";
					n_RxLev += colorCodeModel.get(i).getRssi() + "/";
				}
				callBackStr = "ScannerType=ColorCode,N_BCCH=" + n_BCCH + ",N_BSIC=" + n_BSIC + ",N_RxLev=" + n_RxLev;
			}
			
			break;
		
		case CDMAPILOT:
		case EVDOPILOT:
			ArrayList<CdmaCpichPilotModel>  topNModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_CDMA_CPICH);
			if(topNModelResult != null && topNModelResult.size() != 0){
				String n_Freq = "";
				String n_PN = "";
				String n_Ec = "";
				String n_Io = "";
				String n_EcIo = "";
				for (int i = 0; i < topNModelResult.size(); i++) {
					n_Freq += topNModelResult.get(i).getChannel() + "/";
					n_PN += topNModelResult.get(i).getPn() + "/";
					n_Ec += topNModelResult.get(i).getFaggEc() + "/";
					n_Io += topNModelResult.get(i).getfRSSI() + "/";
					n_EcIo += topNModelResult.get(i).getFaggEcIo() + "/";
				}
				callBackStr = "ScannerType=PilotCDMA,N_Freq=" + n_Freq + ",N_PN=" + n_PN 
												+ ",N_Ec=" + n_Ec + ",N_Io=" + n_Io + ",N_EcIo=" + n_EcIo;
			}
			break;
		case WCDMAPILOT:
			ArrayList<WcdmaPschPilotModel> pschModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_WCDMA_PSCH);
			ArrayList<WcdmaCpichPilotModel> cpichModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_WCDMA_CPICH);
			ArrayList<WcdmaSschPilotModel> sschModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_WCDMA_SSCH);
			if(pschModelResult != null && pschModelResult.size() != 0){
				String n_UARFCN = "";
				String n_PSC = "";
				String n_RSCP = "";
				String n_Io = "";
				String n_EcIo = "";

				String n_PSCH_UARFCN = "";
				String n_PSCH_PSC = "";
				String n_PSCH_RSCP = "";
				String n_PSCH_Io = "";
				String n_PSCH_EcIo = "";

				String n_SSCH_UARFCN = "";
				String n_SSCH_PSC = "";
				String n_SSCH_RSCP = "";
				String n_SSCH_Io = "";
				String n_SSCH_EcIo = "";
				for (int i = 0; i < pschModelResult.size(); i++) {
					n_UARFCN += cpichModelResult.get(i).getChannel() + "/";
					n_PSC += cpichModelResult.get(i).getPsc() + "/";
					n_RSCP += cpichModelResult.get(i).getfRSCP() + "/";
					n_Io += cpichModelResult.get(i).getfRSSI() + "/";
					n_EcIo += cpichModelResult.get(i).getFaggEcIo() + "/";
					
					n_PSCH_UARFCN += pschModelResult.get(i).getChannel() + "/";
					n_PSCH_PSC += pschModelResult.get(i).getPsc() + "/";
					n_PSCH_RSCP += pschModelResult.get(i).getRscp() +"/";
					n_PSCH_Io += "" +"/";
					n_PSCH_EcIo += pschModelResult.get(i).getEcio() + "/";
					
					n_SSCH_UARFCN += sschModelResult.get(i).getChannel() + "/";
					n_SSCH_PSC +=sschModelResult.get(i).getPsc() + "/";
					n_SSCH_RSCP += "/";
					n_SSCH_Io += sschModelResult.get(i).getRscp() + "/";
					n_SSCH_EcIo += sschModelResult.get(i).getEcio() + "/";
				}
				callBackStr = "ScannerType=PilotWCDMA,N_UARFCN=" + n_UARFCN + ",N_PSC=" + n_PSC + ",N_RSCP=" + n_RSCP + ",N_Io=" + n_Io + ",N_EcIo=" + n_EcIo
								+ ",N_PSCH_UARFCN=" + n_PSCH_UARFCN + ",N_PSCH_PSC=" + n_PSCH_PSC + ",N_PSCH_RSCP=" + n_PSCH_RSCP + ",N_PSCH_Io=" + n_PSCH_Io + ",N_PSCH_EcIo="+n_PSCH_EcIo
								+ ",N_SSCH_UARFCN=" + n_SSCH_UARFCN + ",N_SSCH_PSC=" + n_SSCH_PSC + ",N_SSCH_RSCP=" + n_SSCH_RSCP + ",N_SSCH_Io=" + n_SSCH_Io + ",N_SSCH_EcIo=" + n_SSCH_EcIo;
												
			}
			break;
			
		case TDSCDMAPILOT:
			ArrayList<TdScdmaPilotModel> tdModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_TDSCDMA_PCCPCH);
			if(tdModelResult != null && tdModelResult.size() != 0){
				String n_UARFCN = "";
				String n_CPI = "";
				String n_RSCP = "";
				String n_ISCP = "";
				String n_CI ="";
				String n_SyncID ="";
				String n_SIR = "";
				for (int i = 0; i < tdModelResult.size(); i++) {
					n_UARFCN += tdModelResult.get(i).getChannel() + "/";
					n_CPI += tdModelResult.get(i).getMidambleCode() + "/";
					n_RSCP += tdModelResult.get(i).getfPCCPCHRSCP() + "/";
					n_ISCP += tdModelResult.get(i).getfPCCPCHISCP() + "/";
					n_CI += tdModelResult.get(i).getfPCCPCHEcIo() + "/";
					n_SyncID += tdModelResult.get(i).getSyncID() + "/";
					n_SIR += tdModelResult.get(i).getfPCCPCHSIR() + "/";
				}
				callBackStr = "ScannerType=PilotTDSCDMA,N_UARFCN=" + n_UARFCN + ",N_CPI=" + n_CPI + ",N_RSCP=" + n_RSCP
								+ ",N_ISCP=" + n_ISCP + ",N_CI=" + n_CI + ",N_SyncID=" + n_SyncID + ",N_SIR=" + n_SIR;
						
			}
			
			break;
			
		case LTEPILOT:
			List<LteCellDataPilotModel> cellInfoModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_LTE_CellInfo); //查小区信息
			List<LteRsPilotModel> rsModelResult		 = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_LTE_RS);  //1
			List<LtePssPilotModel> pssModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_LTE_PSS);		  //2	
			List<LteSssPilotModel> sssModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_LTE_SSS);		  //3
			if(cellInfoModelResult != null && cellInfoModelResult.size() != 0){
				String N_EARFCN = "";
				String N_PCI = "";
				String N_RSCP = "";
				String N_RSRQ = "";
				String N_CINR = "";
				String N_Band = "";
				String N_BW = "";
				String N_CellID = "";
				String N_TO = "";
				String N_PSCH_EARFCN = "";
				String N_PSCH_PCI = "";
				String N_PSCH_RSCP = "";
				String N_PSCH_RSRQ = "";
				String N_PSCH_CINR = "";
				String N_PSCH_Band = "";
				String N_PSCH_BW = "";
				String N_PSCH_CellID = "";
				String N_PSCH_TO= "";
				String N_SSCH_EARFCN= "";
				String N_SSCH_PCI= "";
				String N_SSCH_RSCP= "";
				String N_SSCH_RSRQ= "";
				String N_SSCH_CINR= "";
				String N_SSCH_Band= "";
				String N_SSCH_BW= "";
				String N_SSCH_CellID= "";
				String N_SSCH_TO= "";
				for (int i = 0; i < cellInfoModelResult.size(); i++) {
					N_EARFCN += rsModelResult.get(i).getEarfcn() + "/";
					N_PCI += rsModelResult.get(i).getPci() + "/";
					N_RSCP += rsModelResult.get(i).getRp() + "/";
					N_RSRQ += rsModelResult.get(i).getRq() + "/";
					N_CINR += rsModelResult.get(i).getCinr() + "/";
					N_Band += cellInfoModelResult.get(i).getBand() + "/";
					N_BW += cellInfoModelResult.get(i).getBandWidth() + "/";
					N_CellID += cellInfoModelResult.get(i).getNumOfRB()+ "/";
					N_TO += rsModelResult.get(i).getTimeOffset()+ "/";
					
					N_PSCH_EARFCN += pssModelResult.get(i).getEarfcn() + "/";
					N_PSCH_PCI += pssModelResult.get(i).getPci() + "/";
					N_PSCH_RSCP += pssModelResult.get(i).getRp() + "/";
					N_PSCH_RSRQ += pssModelResult.get(i).getRq() + "/";
					N_PSCH_CINR += pssModelResult.get(i).getCinr() + "/";
					N_PSCH_Band += cellInfoModelResult.get(i).getBand() + "/";
					N_PSCH_BW += cellInfoModelResult.get(i).getBandWidth() + "/";
					N_PSCH_CellID += cellInfoModelResult.get(i).getNumOfRB()+ "/";
					N_PSCH_TO += pssModelResult.get(i).getTimeOffset()+ "/";
					
					N_SSCH_EARFCN += sssModelResult.get(i).getEarfcn() + "/";
					N_SSCH_PCI += sssModelResult.get(i).getPci() + "/";
					N_SSCH_RSCP += sssModelResult.get(i).getRp() + "/";
					N_SSCH_RSRQ += sssModelResult.get(i).getRq() + "/";
					N_SSCH_CINR += sssModelResult.get(i).getCinr() + "/";
					N_SSCH_Band += cellInfoModelResult.get(i).getBand() + "/";
					N_SSCH_BW += cellInfoModelResult.get(i).getBandWidth() + "/";
					N_SSCH_CellID += cellInfoModelResult.get(i).getNumOfRB()+ "/";
					N_SSCH_TO += sssModelResult.get(i).getTimeOffset()+ "/";
					
				}
					callBackStr = "ScannerType=PilotLTE,N_EARFCN=" + N_EARFCN + ",N_PCI=" + N_PCI + ",N_RSCP=" + N_RSCP + ",N_RSRQ=" + N_RSRQ + ",N_CINR=" + N_CINR + ",N_Band=" + N_Band + ",N_BW=" + N_BW + ",N_CellID=" + N_CellID + ",,N_TO=" + N_TO + 
							",N_PSCH_EARFCN=" + N_PSCH_EARFCN + ",N_PSCH_PCI=" + N_PSCH_PCI + ",N_PSCH_RSCP=" + N_PSCH_RSCP + ",N_PSCH_RSRQ=" + N_PSCH_RSRQ + ",N_PSCH_CINR=" + N_PSCH_CINR + ",N_PSCH_Band=" + N_PSCH_Band + ",N_PSCH_BW=" + N_PSCH_BW + ",N_PSCH_CellID=" + N_PSCH_CellID + ",N_PSCH_TO=" + N_PSCH_TO +
							",N_SSCH_EARFCN=" + N_SSCH_EARFCN + ",N_SSCH_PCI=" + N_SSCH_PCI + ",N_SSCH_RSCP=" + N_SSCH_RSCP + ",N_SSCH_RSRQ=" + N_SSCH_RSRQ + ",N_SSCH_CINR=" + N_SSCH_CINR + ",N_SSCH_Band=" + N_SSCH_Band + ",N_SSCH_BW=" + N_SSCH_BW + ",N_SSCH_CellID=" + N_SSCH_CellID + ",N_SSCH_TO=" + N_SSCH_TO;
							
			}
			
			break;
			
		default:
			break;
		}
    	return callBackStr;
    }
    
    
}
