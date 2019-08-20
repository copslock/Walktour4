package com.dinglicom.dataset;

import android.content.Context;

import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UnifyStruct;
import com.walktour.control.config.ParameterSetting;


/**
 * 
 * @author Administrator
 *	提供查询参数统一类
 */
public class PageQueryParas {

	/**
	 * 串口监控参数
	 * 只要监控的参数值返回有有一个不为无效值，则认为有串口正常
	 */
	public static int[] traceMonitorParam = {
		UnifyParaID.C_RxAGC,UnifyParaID.C_EV_RxAGC0,
		UnifyParaID.G_Ser_RxLevFull,UnifyParaID.TD_Ser_PCCPCHRSCP,
		UnifyParaID.W_Ser_Total_RSCP,UnifyParaID.L_SRV_RSRP
	};
	
	/**
	 * 根据当前网络参数，获得对应的参数ID
	 * 当前网络GSM = 0x01,WCDMA = 0x02,TD-SCDMA = 0x04,CDMA\EVDO = 0x08,LTE = 0x10,Unknown = 0x20,NoService = 0x80
	 * @param nettype
	 * @return
	 */
	public static int[] getParamIdByNet(int nettype){
		switch(nettype){
		case 0x01:
			return new int[]{UnifyParaID.G_Ser_RxLevFull};
		case 0x02:
			return new int[]{UnifyParaID.W_Ser_Total_RSCP,UnifyParaID.W_Ser_RxPower};
		case 0x04:
			return new int[]{UnifyParaID.TD_Ser_PCCPCHRSCP,UnifyParaID.TD_Ser_CarrierRSSI};
		case 0x08:
			return new int[]{UnifyParaID.C_RxAGC,UnifyParaID.C_EV_RxAGC0};
		case 0x10:
			return new int[]{UnifyParaID.L_SRV_RSRP};
		default:
			return traceMonitorParam;
		}
	}
	
	public static int [] PageQueryTDScdma = {
			UnifyParaID.TD_Ser_MCC,UnifyParaID.TD_Ser_MNC,UnifyParaID.TD_Ser_LAC,
			UnifyParaID.TD_Ser_UARFCN,UnifyParaID.TD_Ser_CellID,
			UnifyParaID.TD_Ser_DCHURAFCN,UnifyParaID.TD_Ser_RNCID,
			UnifyParaID.TD_Ser_CPI,UnifyParaID.TD_Ser_URAID,
			UnifyParaID.TD_Ser_CarrierRSSI,UnifyParaID.TD_Ser_UpPCHTxPower,
			UnifyParaID.TD_Ser_PCCPCHRSCP,UnifyParaID.TD_Ser_DPCHRSCP,
			UnifyParaID.TD_Ser_PCCPCHISCP,UnifyParaID.TD_Ser_DPCHISCP,
			UnifyParaID.TD_Ser_PCCPCHC2I,UnifyParaID.TD_Ser_DPCHC2I,
			UnifyParaID.TD_Ser_PCCPCHSIR,UnifyParaID.TD_Ser_UETxPower,
			UnifyParaID.TD_Ser_PCCPCHPathloss,UnifyParaID.TD_Ser_TA,
			UnifyParaID.TD_Ser_BLER,UnifyParaID.TD_Ser_RAC,
		////////////////////////////System Parameter////////////////////////////
			UnifyParaID.TD_Thr_DL_RLC_Thr,UnifyParaID.TD_Thr_UL_RLC_Thr,           
			UnifyParaID.TD_Thr_DL_PDCP_Thr,UnifyParaID.TD_Thr_UL_PDCP_Thr,         
			UnifyParaID.TD_Thr_DL_RLC_Error_Rate,UnifyParaID.TD_Thr_UL_RLC_RTX_Rate,
		////////////////////////////邻区获取////////////////////////////	
			UnifyParaID.T_NCell_N1_RSCP,                       
			UnifyParaID.T_NCell_N2_RSCP,
			UnifyParaID.T_NCell_N3_RSCP,                
			UnifyParaID.T_NCell_N4_RSCP,
			UnifyParaID.T_NCell_N5_RSCP,
			UnifyParaID.T_NCell_N6_RSCP,
			
			UnifyParaID.T_NCell_N1_UARFCN,
			UnifyParaID.T_NCell_N2_UARFCN,
			UnifyParaID.T_NCell_N3_UARFCN,
			UnifyParaID.T_NCell_N4_UARFCN,
			UnifyParaID.T_NCell_N5_UARFCN,
			UnifyParaID.T_NCell_N6_UARFCN,
			
			UnifyParaID.T_NCell_N1_CPI,
			UnifyParaID.T_NCell_N2_CPI,
			UnifyParaID.T_NCell_N3_CPI,
			UnifyParaID.T_NCell_N4_CPI,
			UnifyParaID.T_NCell_N5_CPI,
			UnifyParaID.T_NCell_N6_CPI,
			
			UnifyParaID.T_NCell_N1_ISCP,
			UnifyParaID.T_NCell_N2_ISCP,
			UnifyParaID.T_NCell_N3_ISCP,
			UnifyParaID.T_NCell_N4_ISCP,
			UnifyParaID.T_NCell_N5_ISCP,
			UnifyParaID.T_NCell_N6_ISCP,
			
			UnifyParaID.T_NCell_N1_CPI,   
			UnifyParaID.T_NCell_N2_CPI,   
			UnifyParaID.T_NCell_N3_CPI,   
			UnifyParaID.T_NCell_N4_CPI,   
			UnifyParaID.T_NCell_N5_CPI,   
			UnifyParaID.T_NCell_N6_CPI,   

			UnifyParaID.T_NCell_N1_CarrierRSSI,
			UnifyParaID.T_NCell_N2_CarrierRSSI,
			UnifyParaID.T_NCell_N3_CarrierRSSI,
			UnifyParaID.T_NCell_N4_CarrierRSSI,
			UnifyParaID.T_NCell_N5_CarrierRSSI,
			UnifyParaID.T_NCell_N6_CarrierRSSI,
			
			UnifyParaID.T_NCell_N1_PathLoss,
			UnifyParaID.T_NCell_N2_PathLoss,
			UnifyParaID.T_NCell_N3_PathLoss,
			UnifyParaID.T_NCell_N4_PathLoss,
			UnifyParaID.T_NCell_N5_PathLoss,
			UnifyParaID.T_NCell_N6_PathLoss,
			
			UnifyParaID.T_NCell_N1_Rn,
			UnifyParaID.T_NCell_N2_Rn,
			UnifyParaID.T_NCell_N3_Rn,
			UnifyParaID.T_NCell_N4_Rn,
			UnifyParaID.T_NCell_N5_Rn,
			UnifyParaID.T_NCell_N6_Rn,
////////////////////////////System state////////////////////////////
			UnifyParaID.TD_Main_Current_State,
			UnifyParaID.TD_Main_Previous_State,
			UnifyParaID.TD_Connected_Current_State,
			UnifyParaID.TD_Connected_Previous_State,
////////////////////////////System state////////////////////////////			
			UnifyParaID.TD_Attach_Allowed,
			UnifyParaID.TD_Cell_Barred,
			UnifyParaID.TD_Q_Rxlevmin,
			UnifyParaID.TD_Max_Allowed_TxPower,
			UnifyParaID.TD_Served_RS,
//////////////////////////// Ts ////////////////////////////			
			UnifyParaID.TD_TS1_DPCH_ISCP,
			UnifyParaID.TD_TS2_DPCH_ISCP,
			UnifyParaID.TD_TS3_DPCH_ISCP,
			UnifyParaID.TD_TS4_DPCH_ISCP,
			UnifyParaID.TD_TS5_DPCH_ISCP,
			UnifyParaID.TD_TS6_DPCH_ISCP,
			
			UnifyParaID.TD_TS2_DPCH_RSCP,
			UnifyParaID.TD_TS3_DPCH_RSCP,
			UnifyParaID.TD_TS4_DPCH_RSCP,
			UnifyParaID.TD_TS5_DPCH_RSCP,
			UnifyParaID.TD_TS6_DPCH_RSCP,
			
			UnifyParaID.TD_TS1_TxPower,
			UnifyParaID.TD_TS2_TxPower,
			UnifyParaID.TD_TS3_TxPower,
			
			UnifyParaID.TD_TS2_DPCH_SIR,
			UnifyParaID.TD_TS3_DPCH_SIR,
			UnifyParaID.TD_TS4_DPCH_SIR,
			UnifyParaID.TD_TS5_DPCH_SIR,
			UnifyParaID.TD_TS6_DPCH_SIR
    };
	
	
	public static int [] PageQueryCdma = {
			UnifyParaID.C_Frequency,UnifyParaID.C_SID,
            UnifyParaID.C_ReferencePN,UnifyParaID.C_NID,
            UnifyParaID.C_ReferenceEcIo,UnifyParaID.C_BID,
            UnifyParaID.C_ReferenceEc,UnifyParaID.C_MaxEcIoPN,
            UnifyParaID.C_TotalEcIo,UnifyParaID.C_MaxEcIo,
            UnifyParaID.C_TotalEc,UnifyParaID.C_MaxEc,
            UnifyParaID.C_RxAGC,UnifyParaID.C_FFER,
            UnifyParaID.C_TxAGC,UnifyParaID.C_TxGainAdj,
            UnifyParaID.C_TxPower,UnifyParaID.C_ActiveSetNum,
            UnifyParaID.C_State,
        ////////////////////////////System Parameter////////////////////////////
            UnifyParaID.C_Win_A,UnifyParaID.C_Pilot_Inc,
            UnifyParaID.C_Win_N,UnifyParaID.C_T_Add,
            UnifyParaID.C_Win_R,UnifyParaID.C_T_Comp,
            UnifyParaID.C_Soft_Slope,UnifyParaID.C_T_Drop,
            UnifyParaID.C_Ec_Threshold,UnifyParaID.C_NeighborMaxAge,
            UnifyParaID.E_Band,UnifyParaID.C_ESN
            
    };
	
	
	public static int [] PageQueryWcdma = {
			UnifyParaID.W_Ser_MCC,				    	
			UnifyParaID.W_Ser_MNC,					  
			UnifyParaID.W_Ser_UL_UARFCN,			
			UnifyParaID.W_Ser_DL_UARFCN,			
			UnifyParaID.W_Ser_Max_PSC,				
			UnifyParaID.W_Ser_Max_RSCP,			
			UnifyParaID.W_Ser_Max_EcIo,			
			UnifyParaID.W_Ser_RxPower,				
			UnifyParaID.W_Ser_TxPower,				
			UnifyParaID.W_Ser_DL_AMR_Codec,	
			UnifyParaID.W_Ser_LAC,					  
			UnifyParaID.W_Ser_RNC_ID,				
			UnifyParaID.W_Ser_Cell_ID,				
			UnifyParaID.W_Ser_BLER,					
			UnifyParaID.W_Ser_Total_RSCP,	
			UnifyParaID.W_Ser_Total_EcIo,	
			UnifyParaID.W_Ser_SIR,					
			UnifyParaID.W_Ser_RRC_State,		
			UnifyParaID.W_Ser_UL_AMR_Codec,
		////////////////////////////WCDMA RACH////////////////////////////
			UnifyParaID.W_RA_Message_Length,UnifyParaID.W_RA_AICH_Status,
			UnifyParaID.W_RA_Preambles_Num,UnifyParaID.W_RA_Access_Slot,
			UnifyParaID.W_RA_Last_Preamble_Signature,UnifyParaID.W_RA_SFN,
			UnifyParaID.W_RA_RF_TX_Power,UnifyParaID.W_RA_AICH_Timing,
		////////////////////////////WCDMA PRACH////////////////////////////	
			UnifyParaID.W_PRA_Max_TxPower,UnifyParaID.W_PRA_Transport_Chan_ID,
			UnifyParaID.W_PRA_Min_SF_for_RACH,UnifyParaID.W_PRA_PWR_Ramp_Step,
			UnifyParaID.W_PRA_SC_Index,UnifyParaID.W_PRA_Max_Preamble_Trans,
			UnifyParaID.W_PRA_UL_Punctuing_Limit,UnifyParaID.W_PRA_UL_Interference,
		////////////////////////////Power Control////////////////////////////	
			UnifyParaID.W_PC_DL_Power_Up,UnifyParaID.W_PC_UL_Power_Up,
			////////////////////////////AMR INFO////////////////////////////	
			UnifyParaID.W_DL_AMR_NB_475k_Count,UnifyParaID.W_DL_AMR_NB_515k_Count,	
			UnifyParaID.W_DL_AMR_NB_590k_Count,UnifyParaID.W_DL_AMR_NB_670k_Count,	
			UnifyParaID.W_DL_AMR_NB_740k_Count,UnifyParaID.W_DL_AMR_NB_795k_Count,	
			UnifyParaID.W_DL_AMR_NB_102k_Count,UnifyParaID.W_DL_AMR_NB_122k_Count,	
			UnifyParaID.W_UL_AMR_NB_475k_Count,UnifyParaID.W_UL_AMR_NB_515k_Count,	
			UnifyParaID.W_UL_AMR_NB_590k_Count,UnifyParaID.W_UL_AMR_NB_670k_Count,	
			UnifyParaID.W_UL_AMR_NB_740k_Count,UnifyParaID.W_UL_AMR_NB_795k_Count,	
			UnifyParaID.W_UL_AMR_NB_102k_Count,UnifyParaID.W_UL_AMR_NB_122k_Count,
			
			UnifyParaID.W_DL_AMR_WB_66k_Count,UnifyParaID.W_DL_AMR_WB_885k_Count,
			UnifyParaID.W_DL_AMR_WB_1265k_Count,UnifyParaID.W_DL_AMR_WB_1425k_Count,
			UnifyParaID.W_DL_AMR_WB_1585k_Count,UnifyParaID.W_DL_AMR_WB_1825k_Count,
			UnifyParaID.W_DL_AMR_WB_1985k_Count,UnifyParaID.W_DL_AMR_WB_2325k_Count,
			UnifyParaID.W_DL_AMR_WB_2385k_Count,
			UnifyParaID.W_UL_AMR_WB_66k_Count,UnifyParaID.W_UL_AMR_WB_885k_Count,
			UnifyParaID.W_UL_AMR_WB_1265k_Count,UnifyParaID.W_UL_AMR_WB_1425k_Count,
			UnifyParaID.W_UL_AMR_WB_1585k_Count,UnifyParaID.W_UL_AMR_WB_1825k_Count,
			UnifyParaID.W_UL_AMR_WB_1985k_Count,UnifyParaID.W_UL_AMR_WB_2325k_Count,
			UnifyParaID.W_UL_AMR_WB_2385k_Count
	};
	
	
	public static int [] PageQueryGsm = {
			UnifyParaID.G_Ser_MCC,UnifyParaID.G_Ser_MNC,UnifyParaID.G_Ser_LAC,
			UnifyParaID.G_Ser_BCCH,UnifyParaID.G_Ser_Cell_ID,
			UnifyParaID.G_Ser_BSIC,UnifyParaID.G_Ser_V_Codec,
			UnifyParaID.G_Ser_BCCHLev,UnifyParaID.G_Ser_TS,UnifyParaID.G_Ser_TS,
			UnifyParaID.G_Ser_TxPower,UnifyParaID.G_Ser_RLT,
			UnifyParaID.G_Ser_TA,UnifyParaID.G_Ser_TCH_C2I,
			UnifyParaID.G_Ser_RxLevFull,UnifyParaID.G_Ser_RxQualFull,
			UnifyParaID.G_Ser_RxLevSub,UnifyParaID.G_Ser_RxQualSub,
			UnifyParaID.G_Ser_DTX,UnifyParaID.G_Ser_State,
			UnifyParaID.G_Ser_FerFull,UnifyParaID.G_Ser_FerSub,
			
			////////////////////////////System Parameter////////////////////////////	
			UnifyParaID.G_SYS_HSN,UnifyParaID.G_SYS_MAIO,
			UnifyParaID.G_SYS_CR_Hysteresis,UnifyParaID.G_SYS_T3212,
			UnifyParaID.G_SYS_CR_offset,UnifyParaID.G_SYS_Max_Retransmitted,
			UnifyParaID.G_SYS_TO,UnifyParaID.G_SYS_RX_Level_Access_Min,
			UnifyParaID.G_SYS_PT,UnifyParaID.G_SYS_MS_TX_Power_Max_CCH,
			
			////////////////////////////System State////////////////////////////	
			UnifyParaID.G_GRR_State,UnifyParaID.G_GPRS_Support,
			UnifyParaID.G_EGPRS_Support,UnifyParaID.G_Attach_State,
			UnifyParaID.G_GMM_State,UnifyParaID.G_NMO,
			UnifyParaID.G_Channel_Type,UnifyParaID.G_Channel_Mode,
			UnifyParaID.G_Dedicated_ARFCN,UnifyParaID.G_Channels_Num,	
			UnifyParaID.G_CCCH_CONF,UnifyParaID.G_CCCH_Combined,	
			UnifyParaID.G_Channel_Count,UnifyParaID.G_gsm_Struct,
			////////////////////////////邻区获取////////////////////////////		
			UnifyParaID.G_NCell_N1_BCCH,    
			UnifyParaID.G_NCell_N2_BCCH,    
			UnifyParaID.G_NCell_N3_BCCH,    
			UnifyParaID.G_NCell_N4_BCCH,    
			UnifyParaID.G_NCell_N5_BCCH,    
			UnifyParaID.G_NCell_N6_BCCH,    
			                                
			UnifyParaID.G_NCell_N1_BSIC,    
			UnifyParaID.G_NCell_N2_BSIC,    
			UnifyParaID.G_NCell_N3_BSIC,    
			UnifyParaID.G_NCell_N4_BSIC,    
			UnifyParaID.G_NCell_N5_BSIC,    
			UnifyParaID.G_NCell_N6_BSIC,    
			                                
			UnifyParaID.G_NCell_N1_BCCH,    
			UnifyParaID.G_NCell_N2_BCCH,    
			UnifyParaID.G_NCell_N3_BCCH,    
			UnifyParaID.G_NCell_N4_BCCH,    
			UnifyParaID.G_NCell_N5_BCCH,    
			UnifyParaID.G_NCell_N6_BCCH,    
			                                
			UnifyParaID.G_NCell_N1_BSIC,    
			UnifyParaID.G_NCell_N2_BSIC,    
			UnifyParaID.G_NCell_N3_BSIC,    
			UnifyParaID.G_NCell_N4_BSIC,    
			UnifyParaID.G_NCell_N5_BSIC,    
			UnifyParaID.G_NCell_N6_BSIC,    
			                                
			UnifyParaID.G_NCell_N1_RxLevel, 
			UnifyParaID.G_NCell_N2_RxLevel, 
			UnifyParaID.G_NCell_N3_RxLevel, 
			UnifyParaID.G_NCell_N4_RxLevel, 
			UnifyParaID.G_NCell_N5_RxLevel, 
			UnifyParaID.G_NCell_N6_RxLevel, 
			                                
			UnifyParaID.G_NCell_N1_C1,      
			UnifyParaID.G_NCell_N2_C1,      
			UnifyParaID.G_NCell_N3_C1,      
			UnifyParaID.G_NCell_N4_C1,      
			UnifyParaID.G_NCell_N5_C1,      
			UnifyParaID.G_NCell_N6_C1,      
			                                
			UnifyParaID.G_NCell_N1_C2,      
			UnifyParaID.G_NCell_N2_C2,      
			UnifyParaID.G_NCell_N3_C2,      
			UnifyParaID.G_NCell_N4_C2,      
			UnifyParaID.G_NCell_N5_C2,      
			UnifyParaID.G_NCell_N6_C2       
	};
	
	public static int [] PageQueryEdge = {
			UnifyParaID.G_GPRS_DL_TBF_State,
			UnifyParaID.G_GPRS_UL_TBF_State, UnifyParaID.G_GPRS_DL_CS,
			UnifyParaID.G_GPRS_DL_MCS, UnifyParaID.G_GPRS_UL_CS,
			UnifyParaID.G_GPRS_UL_MCS, UnifyParaID.G_GPRS_DL_RLC_Thr,
			UnifyParaID.G_GPRS_UL_RLC_Thr, UnifyParaID.G_GPRS_DL_RLC_RTX,
			UnifyParaID.G_GPRS_UL_RLC_RTX, UnifyParaID.G_GPRS_DL_TFI,
			UnifyParaID.G_GPRS_UL_TFI, UnifyParaID.G_GPRS_GMSK_CV_BEP,
			UnifyParaID.G_GPRS_GMSK_MEAN_BEP, UnifyParaID.G_GPRS_8PSK_CV_BEP,
			UnifyParaID.G_GPRS_8PSK_MEAN_BEP, UnifyParaID.G_GPRS_GPRS_BLER,
			UnifyParaID.G_GPRS_CValue, UnifyParaID.G_GPRS_RxQual,
			UnifyParaID.G_GPRS_SignVar,
			
			UnifyParaID.G_TS_UL_TS , UnifyParaID.G_TS_DL_TS,
			UnifyParaID.G_GPRS_SM_STATE
	};
	
	public static int [] PageQueryHspa = {
			UnifyParaID.W_DPA1_Session,UnifyParaID.W_DPA1_MIMO_Support,
			UnifyParaID.W_DPA1_CQI_Min, UnifyParaID.W_DPA1_Class_Category,
			UnifyParaID.W_DPA1_CQI_Mean, UnifyParaID.W_DPA1_H_RNTI,
			UnifyParaID.W_DPA1_CQI_Max, UnifyParaID.W_DPA1_16QAM_Config,
			UnifyParaID.W_DPA1_QPSK_Rate, UnifyParaID.W_DPA1_SCCH_Code_Num,
			UnifyParaID.W_DPA1_16QAM_Rate, UnifyParaID.W_DPA1_TransBolck_Size,
			UnifyParaID.W_DPA1_64QAM_Rate, UnifyParaID.W_DPA1_DSCH_Error_Rate,
			UnifyParaID.W_DPA1_SCCH_DecodeSuccRate,UnifyParaID.W_DPA1_DSCH_Error_Blocks,
			UnifyParaID.W_DPA1_DSCH_ACK_Rate,UnifyParaID.W_DPA1_DSCH_Total_Code_Num,
			UnifyParaID.W_DPA1_DSCH_DTX_Rate,UnifyParaID.W_DPA1_DSCH_Avg_Code_Num,
			UnifyParaID.W_DPA1_HARQ_ResponseRate,UnifyParaID.W_DPA1_DSCH_Schedule_Num,
			UnifyParaID.W_DPA1_HARQ_Process_Num,UnifyParaID.W_DPA1_SCCH_Schedule_Num,
			UnifyParaID.W_DPA1_CQI_Power_Offset,UnifyParaID.W_DPA1_SCCH_Schedule_Ratio,
			UnifyParaID.W_DPA1_Phys_Request_Thr,
			UnifyParaID.W_DPA1_Phys_Schedule_Thr,
			UnifyParaID.W_DPA1_Phys_Service_Thr,
			UnifyParaID.W_DPA1_MAC_Thr,
			
			UnifyParaID.W_Thr_DL_RLC_Thr,UnifyParaID.W_Thr_DL_PDCP_Thr,
			UnifyParaID.W_Thr_UL_RLC_Thr,UnifyParaID.W_Thr_UL_PDCP_Thr,
			UnifyParaID.W_Thr_RLC_Err_Rate,UnifyParaID.W_Thr_RLC_RTX_Rate,
			
			UnifyParaID.W_UPA1_Session,UnifyParaID.W_UPA1_E_RNTI,
			UnifyParaID.W_UPA1_Class_Category,UnifyParaID.W_UPA1_E_DPCCH_Power,
			UnifyParaID.W_UPA1_HappyBit_Rate,UnifyParaID.W_UPA1_Serving_TTI,
			UnifyParaID.W_UPA1_ETFCI_LTDMP_Rate,UnifyParaID.W_UPA1_SGI_Average,
			UnifyParaID.W_UPA1_ETFCI_LTDSG_Rate,UnifyParaID.W_UPA1_ACK_Rate,
			UnifyParaID.W_UPA1_ETFCI_LTDBo_Rate,UnifyParaID.W_UPA1_NACK_Rate,
			UnifyParaID.W_UPA1_A_Set_Count,	UnifyParaID.W_UPA1_DTX_Rate,
			UnifyParaID.W_UPA1_UE_Frame_Usage,UnifyParaID.W_UPA1_One_RTX_Rate,
			UnifyParaID.W_UPA1_TB_Size_Max,UnifyParaID.W_UPA1_Two_RTX_Rate,
			
			UnifyParaID.W_UPA1_Phys_Service_Thr,
			UnifyParaID.W_UPA1_MAC_Thr
	};
	
	public static int [] PageQueryHspaPlus = {
			UnifyParaID.W_PA1_HSPAPlus_Session,
			UnifyParaID.W_PA1_HSPAPlus_Class_Category,UnifyParaID.W_PA1_Dual_Carrier_Session_,
			UnifyParaID.W_MM_RateMatchCode, UnifyParaID.W_MM_Secondary_E_RNTI,
			UnifyParaID.W_MM_TypeA_CQI_Rate, UnifyParaID.W_MM_TypeA_CQI_Mean,
			UnifyParaID.W_MM_TypeB_CQI_Rate, UnifyParaID.W_MM_TypeB_CQI_Mean,
			
			UnifyParaID.W_MM_Pre_TB_Rate_1,UnifyParaID.W_MM_Pre_TB_Rate_2,
			UnifyParaID.W_MM_TransBlock_Size_Max_1,UnifyParaID.W_MM_TransBlock_Size_Max_2,
			UnifyParaID.W_MM_DSCH_BLER_Re_Rate_1,UnifyParaID.W_MM_DSCH_BLER_Re_Rate_2,
			UnifyParaID.W_MM_DSCH_BLER_Fst_Rate_1,UnifyParaID.W_MM_DSCH_BLER_Fst_Rate_2,
			UnifyParaID.W_MM_DSCH_ACK_Rate_1,UnifyParaID.W_MM_DSCH_ACK_Rate_2,
			UnifyParaID.W_MM_16QAM_Rate_1,UnifyParaID.W_MM_16QAM_Rate_2,
			UnifyParaID.W_MM_64QAM_Rate_1,UnifyParaID.W_MM_64QAM_Rate_2,
			UnifyParaID.W_MM_QPSK_Rate_1,UnifyParaID.W_MM_QPSK_Rate_2,
			UnifyParaID.W_MM_Phys_Schedule_Thr_1,UnifyParaID.W_MM_Phys_Schedule_Thr_2,
			UnifyParaID.W_MM_Phys_Service_Thr_1,UnifyParaID.W_MM_Phys_Service_Thr_2,
			
			UnifyParaID.W_DC_Dual_Serving_Cell_1,UnifyParaID.W_DC_Dual_Serving_Cell_2,
			UnifyParaID.W_DC_Dual_Serving_UARFCN_1,UnifyParaID.W_DC_Dual_Serving_UARFCN_2,
			UnifyParaID.W_DC_TransBlockCount_1,UnifyParaID.W_DC_TransBlockCount_2,
			UnifyParaID.W_DC_TransBlockSize_1,UnifyParaID.W_DC_TransBlockSize_2,
			UnifyParaID.W_DC_CQI_Mean_1,UnifyParaID.W_DC_CQI_Mean_2,
			UnifyParaID.W_DC_DSCH_BLER_Re_Rate_1,	UnifyParaID.W_DC_DSCH_BLER_Re_Rate_2,
			UnifyParaID.W_DC_DSCH_BLER_Fst_Rate_1,UnifyParaID.W_DC_DSCH_BLER_Fst_Rate_2,
			UnifyParaID.W_DC_DSCH_ACK_Rate_1,UnifyParaID.W_DC_DSCH_ACK_Rate_2,
			UnifyParaID.W_DC_DSCH_DTX_Rate_1,UnifyParaID.W_DC_DSCH_DTX_Rate_2,
			UnifyParaID.W_DC_DSCH_Retrans_Rate_1,UnifyParaID.W_DC_DSCH_Retrans_Rate_2,
			UnifyParaID.W_DC_64QAM_Rate_1,UnifyParaID.W_DC_64QAM_Rate_2,
			UnifyParaID.W_DC_16QAM_Rate_1,UnifyParaID.W_DC_16QAM_Rate_2,
			UnifyParaID.W_DC_Decode_Success_Rate_1,UnifyParaID.W_DC_Decode_Success_Rate_2,
			UnifyParaID.W_DC_Phys_Request_Thr_1,	UnifyParaID.W_DC_Phys_Request_Thr_2,
			UnifyParaID.W_DC_Phys_Schedule_Thr_1,UnifyParaID.W_DC_Phys_Schedule_Thr_2,
			UnifyParaID.W_DC_Phys_Service_Thr_1,	UnifyParaID.W_DC_Phys_Service_Thr_2
			
	};

	public static int[] PageQueryLte = {
			UnifyParaID.L_SRV_MCC, UnifyParaID.L_SRV_MNC,
			UnifyParaID.L_SRV_TM, UnifyParaID.L_SRV_UL_Freq,
			UnifyParaID.L_SRV_DL_Freq, UnifyParaID.LTECA_UL_BandWidth,
			UnifyParaID.L_SRV_DL_BandWidth, UnifyParaID.L_SRV_Work_Mode,
			UnifyParaID.L_SRV_Band, UnifyParaID.L_SRV_EARFCN,
			UnifyParaID.L_SRV_PCI, UnifyParaID.L_SRV_RSRP,
			UnifyParaID.L_SRV_RSSI, UnifyParaID.L_SRV_RSRQ,
			UnifyParaID.L_SRV_SINR, UnifyParaID.L_SRV_CRS_SINR,
			UnifyParaID.L_SRV_TAC, UnifyParaID.L_SRV_ECGI,
			UnifyParaID.L_SRV_ECIP1,
			UnifyParaID.L_SRV_ECIP2,
			UnifyParaID.L_SRV_ECIP3,
			UnifyParaID.L_SRV_RRC_State, UnifyParaID.L_SRV_EMM_State,
			UnifyParaID.L_SRV_EMM_Substate,UnifyParaID.CURRENT_STATE_LTE,
			UnifyParaID.L_SubFrame_AssignmentType,
			UnifyParaID.L_Special_SubFramePatterns,
			UnifyParaID.L_CH1_PUSCH_TxPower,
			UnifyParaID.LTE_QPSK_Ratio_UL,
			UnifyParaID.LTE_QPSK_Ratio_code0_DL,
			UnifyParaID.LTE_QPSK_Ratio_code1_DL,
			UnifyParaID.LTE_16QAM_Ratio_UL,
			UnifyParaID.LTE_16QAM_Ratio_code0_DL,
			UnifyParaID.LTE_16QAM_Ratio_code1_DL,
			UnifyParaID.LTE_64QAM_Ratio_UL,
			UnifyParaID.LTE_64QAM_Ratio_code0_DL,
			UnifyParaID.LTE_64QAM_Ratio_code1_DL,
			UnifyParaID.LTE_DL_PDSCH_BLER_Code0,
			UnifyParaID.LTE_DL_PDSCH_BLER_Code1,
			UnifyParaID.LTECA_InitialBLERCode0_PCell,
			UnifyParaID.LTECA_InitialBLERCode1_PCell,
			UnifyParaID.LTECA_ResidualBLERCode0_PCell,
			UnifyParaID.LTECA_ResidualBLERCode1_PCell,
			UnifyParaID.LTECA_BLERCode0__PCell,
			UnifyParaID.LTECA_BLERCode1_PCell,
			UnifyParaID.LTECA_BLER_PCell,
			UnifyParaID.L_CH2_Total_BLER,
			UnifyParaID.LTECA_QPSK_PCell,
			UnifyParaID.LTECA_16QAM_PCell,
			UnifyParaID.LTECA_64QAM_PCell,
			UnifyParaID.LTECA_QPSKCode0_PCell,
			UnifyParaID.LTECA_16QAMCode0_PCell,
			UnifyParaID.LTECA_64QAMCode0_PCell,
			UnifyParaID.LTECA_QPSKCode1_PCell,
			UnifyParaID.LTECA_16QAMCode1_PCell,
			UnifyParaID.LTECA_64QAMCode1_PCell,
			UnifyParaID.L_MIMO_Mode,

			UnifyParaID.L_Thr_UL_PDCP_Thr, UnifyParaID.L_Thr_DL_PDCP_Thr,
			UnifyParaID.L_Thr_UL_RLC_Thr, UnifyParaID.L_Thr_DL_RLC_Thr,
			UnifyParaID.L_Thr_UL_MAC_Thr, UnifyParaID.L_Thr_DL_MAC_Thr,
			UnifyParaID.L_Thr_UL_Phy_Thr, UnifyParaID.L_Thr_DL_Phy_Thr,
			UnifyParaID.L_Thr_DL_PhyThrCode0, UnifyParaID.L_Thr_DL_PhyThrCode1,
			
			UnifyParaID.L_CH2_PUSCH_Initial_BLER,UnifyParaID.L_CH2_PDSCH_BLER,
			UnifyParaID.L_CH2_PUSCH_Residual_BLER,UnifyParaID.L_CH2_PDCCH_Estimated_BLER,
			UnifyParaID.L_CH1_PUSCH_TxPower,UnifyParaID.L_CH1_PUCCH_TxPower,
			UnifyParaID.L_CH2_Wideband_CQI,UnifyParaID.LTECA_ResidualBLER_PCell,
			UnifyParaID.LTECA_InitialBLER_PCell,
			UnifyParaID.L_CH2_Wideband_CQI_for_CW0,UnifyParaID.L_CH2_Wideband_CQI_for_CW1,
			UnifyParaID.L_CH2_CQI_Report_Mode, UnifyParaID.L_CH2_Frame_Number,
			UnifyParaID.L_CH1_PDCCH_DL_Grant_Count,UnifyParaID.L_UL_PUSCH_RB_Count,
			UnifyParaID.L_CH1_PDCCH_UL_Grant_Count,UnifyParaID.L_DL_PDSCH_RB_Count,
			
			
			UnifyParaID.L_SYS_UE_Category, UnifyParaID.L_SYS_Max_UE_TxPower,
			UnifyParaID.L_SYS_Q_Offset_Cell, UnifyParaID.L_SYS_DRX_State,
			UnifyParaID.L_SYS_Q_Offset_Freq, UnifyParaID.L_SYS_NAS_State,
			UnifyParaID.L_SYS_Q_Rxlevmin, UnifyParaID.L_SYS_Power_Headroom,
			UnifyParaID.L_SYS_Q_RxlevminOffset, UnifyParaID.L_SYS_Uu_TA,
			UnifyParaID.L_SYS_IntraFreq_Reselection, UnifyParaID.L_SYS_MMEC,
			UnifyParaID.L_SYS_E_UTRA_Carrier_Freq, UnifyParaID.L_SYS_MMEGI,
			UnifyParaID.L_SYS_ThresholdX_High,UnifyParaID.L_SYS_ThresholdX_Low,
			UnifyParaID.L_SYS_M_TMSI,UnifyParaID.L_SYS_Threshold_Serving_Low,
			UnifyParaID.L_SYS_C_RNTI,UnifyParaID.L_SYS_Treselection_EUTRA, 
			UnifyParaID.L_SYS_T_CRNTI,UnifyParaID.L_SYS_Cell_Reselect_Priority,
			UnifyParaID.L_SYS_Q_Hyst,UnifyParaID.L_SYS_QCI,
			UnifyParaID.L_SYS_Allowed_Meas_Bandwidth,
			////////////////////////////////////////////////邻区ID，备用//////////////////////////////////////////////
			UnifyParaID.L_SRV_RSRQ,//,UnifyParaID.LTE_CELL_LIST,
			//通过特殊结构查询UnifyParaID.LTE_EPS_BearerContext_02C1,UnifyParaID.LTE_APN,
			UnifyParaID.LTE_RANK_INDICATOR, UnifyParaID.LTE_Code_Word,
			UnifyParaID.LTE_Special_SubFrame_Patterns,
			UnifyParaID.LTE_SubFrame_Assignment_Type, UnifyParaID.LTE_RSRP_Rx0,
			UnifyParaID.LTE_RSRP_Rx1, UnifyParaID.LTE_SINR_Rx0,
			UnifyParaID.LTE_SINR_Rx1, UnifyParaID.LTE_RSRQ_Rx0,
			UnifyParaID.LTE_RSRQ_Rx1, UnifyParaID.LTE_RSSI_Rx0,
			UnifyParaID.LTE_RSSI_Rx1, UnifyParaID.LTE_Rank1_SINR,
			UnifyParaID.LTE_Rank2_SINR_code0, UnifyParaID.LTE_Rank2_SINR_code1,
			UnifyParaID.LTE_CRS_RP, UnifyParaID.LTE_PDSCH_TB_Size_code0,
			UnifyParaID.LTE_PDSCH_TB_Size_code1, UnifyParaID.LTE_RANK1_CQI,
			UnifyParaID.LTE_RANK2_CQI_code0, UnifyParaID.LTE_RANK2_CQI_code1,
			UnifyParaID.LTE_PRACH_TxPower, UnifyParaID.LTE_PUCCH_G,
			UnifyParaID.LTE_PUSCH_F, UnifyParaID.LTE_RSPower,
			UnifyParaID.LTE_Pa, UnifyParaID.LTE_Pb,
			UnifyParaID.LTE_PDCCH_Format, UnifyParaID.LTE_PDCCH_DCI_Format,
			UnifyParaID.LTE_CCE1_Count, UnifyParaID.LTE_CCE2_Count,
			UnifyParaID.LTE_CCE4_Count, UnifyParaID.LTE_CCE8_Count,
			UnifyParaID.LTE_Rank1_Indicator_Number,
			UnifyParaID.LTE_Rank2_Indicator_Number,
			UnifyParaID.LTE_Access_Class_Bar,
			UnifyParaID.LTE_T_Reselection_UTRA,
			UnifyParaID.LTE_T_Reselection_GERAN,
			
			//新加海外需求参数
			UnifyParaID.L_Thr_DL_FTP,
			UnifyParaID.L_Thr_DL_HTTP,
			UnifyParaID.L_Thr_DL_EMAIL,
			UnifyParaID.L_Thr_DL_HTTPPAGE,
			UnifyParaID.L_Thr_DL_VIDEOPLA,
			UnifyParaID.L_Thr_UL_FTP,
			UnifyParaID.L_Thr_UL_HTTP,
			UnifyParaID.L_Thr_UL_EMAIL,


			
			UnifyParaID.L_RACH_Type,
			UnifyParaID.L_RACH_Result,
			UnifyParaID.L_Max_Preamble_Number,
			UnifyParaID.L_CP_Type,
			UnifyParaID.L_PDSCH_serving_RB_count_slot,
			UnifyParaID.L_PDSCH_Schedule_RB_count_slot,
			UnifyParaID.L_PUSCH_serving_RB_count_slot,
			UnifyParaID.L_PUSCH_Schedule_RB_count_slot,
			UnifyParaID.L_QCI_3_Config_ul,
			UnifyParaID.L_QCI_4_Config_ul,
			UnifyParaID.L_QCI_5_Config_ul,
			UnifyParaID.L_QCI_6_Config_ul,
			UnifyParaID.L_QCI_7_Config_ul,
			UnifyParaID.L_QCI_8_Config_ul,
			UnifyParaID.L_QCI_9_Config_ul,
			UnifyParaID.L_QCI_1_Config_dl,
			UnifyParaID.L_QCI_2_Config_dl,
			UnifyParaID.L_QCI_3_Config_dl,
			UnifyParaID.L_QCI_4_Config_dl,
			UnifyParaID.L_QCI_5_Config_dl,
			UnifyParaID.L_QCI_6_Config_dl,
			UnifyParaID.L_QCI_7_Config_dl,
			UnifyParaID.L_QCI_8_Config_dl,
			UnifyParaID.L_QCI_9_Config_dl,
			UnifyParaID.CA_Wideband_CQI_code0_pc,
			UnifyParaID.CA_Wideband_CQI_code0_pc1,
			UnifyParaID.CA_Wideband_CQI_code0_pc2,
			UnifyParaID.CA_Wideband_CQI_code0_pc3,
			UnifyParaID.CA_Wideband_CQI_code0_pc4,
			UnifyParaID.CA_Wideband_CQI_code1_pc,
			UnifyParaID.CA_Wideband_CQI_code1_pc1,
			UnifyParaID.CA_Wideband_CQI_code1_pc2,
			UnifyParaID.CA_Wideband_CQI_code1_pc3,
			UnifyParaID.CA_Wideband_CQI_code1_pc4
	};
	
	public static int[] PageQueryEvdo = {
			UnifyParaID.E_EVsectorInfo,UnifyParaID.E_EV_Frequenc,
			UnifyParaID.E_Band,UnifyParaID.E_ServingSectorPN,
			UnifyParaID.E_UATI,UnifyParaID.E_DedicateUserCount,
			UnifyParaID.E_Carrier1_EV_RxAGC0,UnifyParaID.E_Carrier1_EV_RxAGC1,
			UnifyParaID.E_Carrier1_TotalSINR,UnifyParaID.E_Carrier1_EV_TxAGC,
			UnifyParaID.E_Carrier1_DRC_Value,UnifyParaID.E_Carrier1_TxPilotPower,
			UnifyParaID.E_Carrier1_DRC_Cover,UnifyParaID.E_Carrier1_TxOpenLoopPower, 
			UnifyParaID.E_ActiveCount,UnifyParaID.E_Carrier1_TxClosedLoopAdjust,
			
			UnifyParaID.E_DRC2Pilot,UnifyParaID.E_TxMode,
    		UnifyParaID.E_RRI2Pilot,UnifyParaID.E_MaxT2P,
    		UnifyParaID.E_DSC2Pilot,UnifyParaID.E_DSC,
    		UnifyParaID.E_Data2Pilot,UnifyParaID.E_TxPacketSize,
    		UnifyParaID.E_Aux2Pilot,UnifyParaID.E_FRAB,
    		
    		UnifyParaID.E_Session,UnifyParaID.E_AT,
    		UnifyParaID.E_ALMP,UnifyParaID.E_Init,
    		UnifyParaID.E_Idle,UnifyParaID.E_Connected,
    		UnifyParaID.E_OverHeadMsg,UnifyParaID.E_RouteUpdate,
    		UnifyParaID.E_HbridMode,UnifyParaID.E_Session_Release,
    		UnifyParaID.E_Connection_Release,
    		////////////////////////邻区ID/////////////////////////////////
    		UnifyParaID.E_EVServingNeighbor,
    		
			UnifyParaID.E_RxRLP_Thr, UnifyParaID.E_TxRLP_Thr,
			UnifyParaID.E_RLP_Error_Rate, UnifyParaID.E_RLP_RTX_Rate,
			UnifyParaID.E_RxPacket_Thr, UnifyParaID.E_TxPacket_Thr,
			UnifyParaID.E_RxPER, UnifyParaID.E_TxPER,
			UnifyParaID.E_RxSuPacket_Thr_Ist, UnifyParaID.E_RxMuPacket_Thr_Ist,
			UnifyParaID.E_RxSuPacket_Thr, UnifyParaID.E_RxMuPacket_Thr,
			UnifyParaID.E_RxSuPER, UnifyParaID.E_RxMuPER,
			
			UnifyParaID.E_EVPilotActiveSet, 
			UnifyParaID.E_EV_Revision,
			UnifyParaID.E_EVPilotCadidateSet, 
			UnifyParaID.E_EVPilotNeighborSet,
			UnifyParaID.E_EVServingNeighbor
	};
	
	public static int[] PageQueryTDHspaPlus = {
			UnifyParaID.TD_DPA_HSDPA_Session,UnifyParaID.TD_DPA1_Category,
			UnifyParaID.TD_DPA1_Work_UARFCN,UnifyParaID.TD_DPA1_H_RNTI,
			UnifyParaID.TD_DPA1_A_DPCH_RSCP,UnifyParaID.TD_DPA1_A_DPCH_C2I,
			UnifyParaID.TD_DPA_SCCH_SIR,UnifyParaID.TD_DPA1_A_SICH_TxPower,
			
			UnifyParaID.TD_DPA2_16QAM_Rate,UnifyParaID.TD_DPA2_HS_TransBlock_Size,
			UnifyParaID.TD_DPA2_QPSK_Rate,UnifyParaID.TD_DPA2_Max_HSDPA_CQI,
			UnifyParaID.TD_DPA2_SCCH_ScheduleCount,UnifyParaID.TD_DPA2_Mean_HSDPA_CQI,
			UnifyParaID.TD_DPA2_SCCH_ScheduleRate,UnifyParaID.TD_DPA2_Min_HSDPA_CQI,
			UnifyParaID.TD_DPA2_SCCH_BLER,UnifyParaID.TD_DPA2_PDSCH_CodesUsedRate,
			UnifyParaID.TD_DPA2_SCCH_DecodeSuccRate,UnifyParaID.TD_DPA2_PDSCH_TimeSlotUsed,
			UnifyParaID.TD_DPA2_DSCH_NACK_Rate,UnifyParaID.TD_DPA2_PDSCH_AverageSize,
			UnifyParaID.TD_DPA2_DSCH_ACK_Rate,UnifyParaID.TD_DPA2_PDSCH_InitialBLER,
			UnifyParaID.TD_DPA2_DSCH_UnUsed_Rate,UnifyParaID.TD_DPA2_PDSCH_TotalBLER,
			UnifyParaID.TD_DPA2_DSCH_Error_Rate,UnifyParaID.TD_DPA2_Phys_Schedule_Thr,
			UnifyParaID.TD_DPA2_DSCH_ErrorBlocks,UnifyParaID.TD_DPA2_Phys_Service_Thr,
			UnifyParaID.TD_DPA2_OneTime_Trans_SuccRate
	};
	
	public int[] getPageQueryChart(Context context){
		return ParameterSetting.getInstance().getChartParameterIds(context);
	}
	
	/**
	 * 仪表盘查询参数ID定义
	 */
	public int PageQueryDataBrabord[] = new int[]{/*0x0A002154,0x0A002155,0x0A002200,0x0A002201,0x0A002310,0x0A002321,0x0A002330,0x0A002331,0x0A002360,
	        0x0A002370,0x0A0024A0,0x0A002601,0x0A002605*/0x0B001003,0x0B001004,0x0B001005,0x0B001006,0x0B001007,0x0B00100A,0x0B00100B,
	        0x0B001011,0x0B001012,0x0B001013,0x0B001014,0x0B00101F,0x0B001020,0x0B001021,0x0B001022};
	
	/**
	 * VideoStream参数ID定义<BR>
	 */
	public int PageQueryVideoStream[] = new int[]{0x0A00207B,0x0A002075,0x0A00207E,0x0A002072,0x0A002074,0x0A00207D,
			0x0A002076,0x0A002057,0x0A00206C,0x0A002079,0x0A00206E,0x0A00206F,0x0A00205C,0x0A00205E};
	
	
	/**其它实时参数查询*/
	public static int[] OtherParamQuery = {
		UnifyParaID.CURRENT_NETWORKTYPE,UnifyParaID.CURRENT_STATE_GSM,
		UnifyParaID.CURRENT_STATE_TDSCDMA,UnifyParaID.CURRENT_STATE_WCDMA,
		UnifyParaID.CURRENT_STATE_CDMA,UnifyParaID.CURRENT_STATE_EVDO,
		UnifyParaID.CURRENT_STATE_LTE,UnifyParaID.L_SRV_RRC_State
	};
	
	/**PBM业务使用到的实时参数查询 */
	public static int[] PBMParamQuery = { 
		UnifyParaID.PBM_RBC
	};
	
	public static int[] OtsParamLte = new int[]{
		UnifyParaID.L_SRV_RSRP,UnifyParaID.L_SRV_SINR,
		UnifyParaID.L_SRV_PCI,UnifyParaID.L_SRV_TAC,
		UnifyParaID.L_CH1_PUSCH_TxPower,
		UnifyParaID.L_Thr_DL_PhyThrCode0, UnifyParaID.L_Thr_DL_PhyThrCode1,
		UnifyParaID.L_Thr_UL_Phy_Thr,UnifyParaID.L_Thr_DL_PDCP_Thr,
		UnifyParaID.L_Thr_UL_PDCP_Thr,UnifyParaID.L_SRV_CRS_SINR,
		UnifyParaID.L_SRV_PCI,UnifyParaID.LTECA_UL_BandWidth,
		UnifyParaID.L_SRV_TAC,
		UnifyParaID.L_SRV_TM,UnifyParaID.LTE_CELL_LIST,	//lte邻区结构是由特殊接口指定令区ID后所得 ,UnifyParaID.LTE_CELL_LIST,
		UnifyParaID.L_SYS_Rx0RSRP,UnifyParaID.L_SYS_Rx1RSRP,
		UnifyParaID.L_MIMO_Mode,UnifyParaID.L_RANK1_SINR,
		UnifyParaID.L_RANK2_SINR1,UnifyParaID.L_RANK2_SINR2,
		UnifyParaID.L_FreqInfo,UnifyParaID.L_SubFrame_AssignmentType,
		UnifyParaID.L_Special_SubFramePatterns,UnifyParaID.L_UL_RB_Num,
		UnifyParaID.L_DL_RB_Num,UnifyParaID.L_eNodeB_ID,
		UnifyParaID.L_EARFCN_UL
};
public static int[] OtsParamTdScdma = new int[]{
		UnifyParaID.TD_Ser_PCCPCHRSCP,
		UnifyParaID.TD_Ser_PCCPCHISCP,
		UnifyParaID.TD_Ser_UETxPower,
		UnifyParaID.TD_Ser_CarrierRSSI,
		UnifyParaID.TD_Ser_DPCHISCP,
		UnifyParaID.TD_Ser_DPCHRSCP,
		UnifyParaID.TD_Ser_PCCPCHSIR,
		UnifyParaID.TD_Ser_PCCPCHC2I,
		UnifyParaID.TD_Ser_TA,
		UnifyParaID.TD_Ser_LAC,
		UnifyParaID.TD_Ser_CPI,
		UnifyParaID.TD_Ser_UARFCN,
		UnifyParaID.TD_Ser_RAC,
		UnifyParaID.TD_Ser_CellID,
		
		//查邻区
		UnifyParaID.T_NCell_N1_RSCP,                       
		UnifyParaID.T_NCell_N2_RSCP,
		UnifyParaID.T_NCell_N3_RSCP,                
		UnifyParaID.T_NCell_N4_RSCP,
		UnifyParaID.T_NCell_N5_RSCP,
		UnifyParaID.T_NCell_N6_RSCP,
		
		UnifyParaID.T_NCell_N1_UARFCN,
		UnifyParaID.T_NCell_N2_UARFCN,
		UnifyParaID.T_NCell_N3_UARFCN,
		UnifyParaID.T_NCell_N4_UARFCN,
		UnifyParaID.T_NCell_N5_UARFCN,
		UnifyParaID.T_NCell_N6_UARFCN,
		
		UnifyParaID.T_NCell_N1_CPI,
		UnifyParaID.T_NCell_N2_CPI,
		UnifyParaID.T_NCell_N3_CPI,
		UnifyParaID.T_NCell_N4_CPI,
		UnifyParaID.T_NCell_N5_CPI,
		UnifyParaID.T_NCell_N6_CPI
};
public static int[] OtsParamGsm = new int[] {
		UnifyParaID.G_Ser_RxLevFull,
		UnifyParaID.G_Ser_RxQualFull,
		UnifyParaID.G_Ser_TA,
		UnifyParaID.G_Ser_LAC,
		UnifyParaID.G_Ser_RAC,
		UnifyParaID.G_Ser_Cell_ID,
		UnifyParaID.G_NCell_N1_RxLevel,
		UnifyParaID.G_Ser_BCCH,
		UnifyParaID.G_Ser_BSIC,
		UnifyParaID.G_Ser_TxPower,
		UnifyParaID.G_Ser_TCH_C2I,
		// //////////////////////////邻区获取////////////////////////////
		UnifyParaID.G_NCell_N1_BCCH, UnifyParaID.G_NCell_N2_BCCH,
		UnifyParaID.G_NCell_N3_BCCH, UnifyParaID.G_NCell_N4_BCCH,
		UnifyParaID.G_NCell_N5_BCCH, UnifyParaID.G_NCell_N6_BCCH,

		UnifyParaID.G_NCell_N1_BSIC, UnifyParaID.G_NCell_N2_BSIC,
		UnifyParaID.G_NCell_N3_BSIC, UnifyParaID.G_NCell_N4_BSIC,
		UnifyParaID.G_NCell_N5_BSIC, UnifyParaID.G_NCell_N6_BSIC,

		UnifyParaID.G_NCell_N1_BCCH, UnifyParaID.G_NCell_N2_BCCH,
		UnifyParaID.G_NCell_N3_BCCH, UnifyParaID.G_NCell_N4_BCCH,
		UnifyParaID.G_NCell_N5_BCCH, UnifyParaID.G_NCell_N6_BCCH,

		UnifyParaID.G_NCell_N1_BSIC, UnifyParaID.G_NCell_N2_BSIC,
		UnifyParaID.G_NCell_N3_BSIC, UnifyParaID.G_NCell_N4_BSIC,
		UnifyParaID.G_NCell_N5_BSIC, UnifyParaID.G_NCell_N6_BSIC,

		UnifyParaID.G_NCell_N1_RxLevel, UnifyParaID.G_NCell_N2_RxLevel,
		UnifyParaID.G_NCell_N3_RxLevel, UnifyParaID.G_NCell_N4_RxLevel,
		UnifyParaID.G_NCell_N5_RxLevel, UnifyParaID.G_NCell_N6_RxLevel,

		UnifyParaID.G_NCell_N1_C1, UnifyParaID.G_NCell_N2_C1,
		UnifyParaID.G_NCell_N3_C1, UnifyParaID.G_NCell_N4_C1,
		UnifyParaID.G_NCell_N5_C1, UnifyParaID.G_NCell_N6_C1,

		UnifyParaID.G_NCell_N1_C2, UnifyParaID.G_NCell_N2_C2,
		UnifyParaID.G_NCell_N3_C2, UnifyParaID.G_NCell_N4_C2,
		UnifyParaID.G_NCell_N5_C2, UnifyParaID.G_NCell_N6_C2 };

/**
 * volte参数查询ID
 */
	public static int[] pramVolte = new int[] {
			UnifyParaID.VOLTE_CM_Call_ID,
			UnifyParaID.VOLTE_SIP_Codec_Type_in_Summary,
			UnifyParaID.VOLTE_Max_Frame_Delay_in_Summary,
			UnifyParaID.VOLTE_Avg_Frame_Delay_in_Summary,
			UnifyParaID.VOLTE_Max_RFC1889_Jitter_in_Summary,
			UnifyParaID.VOLTE_Avg_RFC1889_Jitter_Avg_in_Summary,
			UnifyParaID.VOLTE_RFC1889_Jitter_Inst,
			UnifyParaID.VOLTE_Packet_Loss_Rate_in_Summary,
			UnifyParaID.VOLTE_Packet_Loss_Rate_Inst,
			UnifyParaID.VOLTE_RTP_Packet_Delay_Max,
			UnifyParaID.VOLTE_RTP_Packet_Delay_Avg,
			UnifyParaID.VOLTE_Tx_RTP_Packet_Num_in_Summary,
			UnifyParaID.VOLTE_Rx_RTP_Packet_Num_in_Summary };
	
	/**
	 * LTEPA参数查询ID
	 */
	public static int[] PageQueryLteCa = new int[] {
			UnifyParaID.LTECA_Capacity_CACarrierCount,
			UnifyParaID.LTECA_Capacity_EMMSubstate,
			UnifyParaID.LTECA_Capacity_LTEUCategory,
			UnifyParaID.LTECA_Capacity_Network_State,
			UnifyParaID.CURRENT_STATE_LTE,
			
			UnifyParaID.LTECA_Capacity_Packet_Capability,
			UnifyParaID.LTECA_WorkMode_PCell,   
			UnifyParaID.LTECA_WorkMode_SCell1,  
			UnifyParaID.LTECA_WorkMode_SCell2,  
			                                    
			UnifyParaID.LTECA_TAC_PCell,        
			UnifyParaID.LTECA_TAC_SCell1,       
			UnifyParaID.LTECA_TAC_SCell2,       
			                                    
			UnifyParaID.LTECA_CellID_PCell,     
			UnifyParaID.LTECA_CellID_SCell1,    
			UnifyParaID.LTECA_CellID_SCell2,    
			                                    
			UnifyParaID.LTECA_Band_PCell,       
			UnifyParaID.LTECA_Band_SCell1,      
			UnifyParaID.LTECA_Band_SCell2,      
			                                    
			UnifyParaID.LTECA_DLEARFCN_PCell,   
			UnifyParaID.LTECA_DLEARFCN_SCell1,  
			UnifyParaID.LTECA_DLEARFCN_SCell2,  
			                                    
			UnifyParaID.LTECA_ULEARFCN_PCell,   
			UnifyParaID.LTECA_ULEARFCN_SCell1,  
			UnifyParaID.LTECA_ULEARFCN_SCell2,  
			                                    
			UnifyParaID.LTECA_PCI_PCell,        
			UnifyParaID.LTECA_PCI_SCell1,       
			UnifyParaID.LTECA_PCI_SCell2,       
			                                    
			UnifyParaID.LTECA_BandWidth_PCell,  
			UnifyParaID.LTECA_BandWidth_SCell1, 
			UnifyParaID.LTECA_BandWidth_SCell2, 
			                                    
			                                    
			UnifyParaID.LTECA_TM_PCell,         
			UnifyParaID.LTECA_TM_SCell1,        
			UnifyParaID.LTECA_TM_SCell2,        
			                                    
			UnifyParaID.LTECA_Freq_PCell,       
			UnifyParaID.LTECA_Freq_SCell1,      
			UnifyParaID.LTECA_Freq_SCell2,      
			                                    
			UnifyParaID.LTECA_CodeWordNum_PCell,
			UnifyParaID.LTECA_CodeWordNum_SCell1,
			UnifyParaID.LTECA_CodeWordNum_SCell2,
			/********华丽分割线*********/
			UnifyParaID.LTECA_RSSI_PCell,           
			UnifyParaID.LTECA_RSSI_SCell1,          
			UnifyParaID.LTECA_RSSI_SCell2,          
			                                        
			UnifyParaID.LTECA_RSSIRx0_PCell,        
			UnifyParaID.LTECA_RSSIRx0_SCell1,       
			UnifyParaID.LTECA_RSSIRx0_SCell2,       
			                                        
			UnifyParaID.LTECA_RSSIRx1_PCell,        
			UnifyParaID.LTECA_RSSIRx1_SCell1,       
			UnifyParaID.LTECA_RSSIRx1_SCell2,       
			                                        
			UnifyParaID.LTECA_RSRP_PCell,           
			UnifyParaID.LTECA_RSRP_SCell1,          
			UnifyParaID.LTECA_RSRP_SCell2,          
			                                        
			UnifyParaID.LTECA_RSRPRx0_PCell,        
			UnifyParaID.LTECA_RSRPRx0_SCell1,       
			UnifyParaID.LTECA_RSRPRx0_SCell2,       
			                                        
			UnifyParaID.LTECA_RSRPRx1_PCell,        
			UnifyParaID.LTECA_RSRPRx1_SCell1,       
			UnifyParaID.LTECA_RSRPRx1_SCell2,       
			                                        
			UnifyParaID.LTECA_CRSRP_PCell,          
			UnifyParaID.LTECA_CRSRP_SCell1,         
			UnifyParaID.LTECA_CRSRP_SCell2,         
			                                        
			UnifyParaID.LTECA_DRSRP_PCell,          
			UnifyParaID.LTECA_DRSRP_SCell1,         
			UnifyParaID.LTECA_DRSRP_SCell2,         
			                                        
			UnifyParaID.LTECA_SINR_PCell,           
			UnifyParaID.LTECA_SINR_SCell1,          
			UnifyParaID.LTECA_SINR_SCell2,          
			                                        
			UnifyParaID.LTECA_SINRRx0_PCell,        
			UnifyParaID.LTECA_SINRRx0_SCell1,       
			UnifyParaID.LTECA_SINRRx0_SCell2,       
			                                        
			UnifyParaID.LTECA_SINRRx1_PCell,        
			UnifyParaID.LTECA_SINRRx1_SCell1,       
			UnifyParaID.LTECA_SINRRx1_SCell2,       
			                                        
			UnifyParaID.LTECA_CRSSINR_PCell,        
			UnifyParaID.LTECA_CRSSINR_SCell1,       
			UnifyParaID.LTECA_CRSSINR_SCell2,       
			                                        
			UnifyParaID.LTECA_DRSSINR_PCell,        
			UnifyParaID.LTECA_DRSSINR_SCell1,       
			UnifyParaID.LTECA_DRSSINR_SCell2,       
			                                        
			UnifyParaID.LTECA_RSRQ_PCell,           
			UnifyParaID.LTECA_RSRQ_SCell1,          
			UnifyParaID.LTECA_RSRQ_SCell2,          
			                                        
			UnifyParaID.LTECA_RSRQRx0_PCell,        
			UnifyParaID.LTECA_RSRQRx0_SCell1,       
			UnifyParaID.LTECA_RSRQRx0_SCell2,       
			                                        
			UnifyParaID.LTECA_RSRQRx1_PCell,        
			UnifyParaID.LTECA_RSRQRx1_SCell1,       
			UnifyParaID.LTECA_RSRQRx1_SCell2,        
             /**********真华丽**************/           
			UnifyParaID.LTECA_MACThrDL_Total,                                                         
			UnifyParaID.LTECA_MACThrDL_PCell,          
			UnifyParaID.LTECA_MACThrDL_SCell1,         
			UnifyParaID.LTECA_MACThrDL_SCell2,         
			                                           
			UnifyParaID.LTECA_PhyThrDL_Total,          
			UnifyParaID.LTECA_PhyThrDL_PCell,          
			UnifyParaID.LTECA_PhyThrDL_SCell1,         
			UnifyParaID.LTECA_PhyThrDL_SCell2,         
			                                           
			UnifyParaID.LTECA_RBCount_Total,          
			UnifyParaID.LTECA_RBCount_PCell,           
			UnifyParaID.LTECA_RBCount_SCell1,          
			UnifyParaID.LTECA_RBCount_SCell2,          
			                                           
			UnifyParaID.LTECA_RBCountSl_Total,         
			UnifyParaID.LTECA_RBCountSl_PCell,         
			UnifyParaID.LTECA_RBCountSl_SCell1,        
			UnifyParaID.LTECA_RBCountSl_SCell2,        
			                                           
			UnifyParaID.LTECA_SlotCount_Total,         
			UnifyParaID.LTECA_SlotCount_PCell,         
			UnifyParaID.LTECA_SlotCount_SCell1,        
			UnifyParaID.LTECA_SlotCount_SCell2,        
			                                           
			                                           
			UnifyParaID.LTECA_SubFNCount_Total,        
			UnifyParaID.LTECA_SubFNCount_PCell,        
			UnifyParaID.LTECA_SubFNCount_SCell1,       
			UnifyParaID.LTECA_SubFNCount_SCell2,       
			                                           
			                                           
			UnifyParaID.LTECA_ScheduleRate_Total,      
			UnifyParaID.LTECA_ScheduleRate_PCell,      
			UnifyParaID.LTECA_ScheduleRate_SCell1,     
			UnifyParaID.LTECA_ScheduleRate_SCell2,     
			                                           
			UnifyParaID.LTECA_TBSizeCode0_Total,       
			UnifyParaID.LTECA_TBSizeCode0_PCell,       
			UnifyParaID.LTECA_TBSizeCode0_SCell1,      
			UnifyParaID.LTECA_TBSizeCode0_SCell2,      
			                                           
			UnifyParaID.LTECA_TBSizeCode1_Total,       
			UnifyParaID.LTECA_TBSizeCode1_PCell,       
			UnifyParaID.LTECA_TBSizeCode1_SCell1,      
			UnifyParaID.LTECA_TBSizeCode1_SCell2,      
			                                           
			                                           
			UnifyParaID.LTECA_PDCCHDLGrant_Total,                   
			UnifyParaID.LTECA_PDCCHDLGrant_PCell,                           
			UnifyParaID.LTECA_PDCCHDLGrant_SCell1,     
			UnifyParaID.LTECA_PDCCHDLGrant_SCell2,     
			                                           
			UnifyParaID.LTECA_PDCCHULGrant_Total,      
			UnifyParaID.LTECA_PDCCHULGrant_PCell,      
			UnifyParaID.LTECA_PDCCHULGrant_SCell1,     
			UnifyParaID.LTECA_PDCCHULGrant_SCell2,
			
			/******************受不了太华丽了************************/
			UnifyParaID.LTECA_BLER_Total,                                                  
			UnifyParaID.LTECA_BLER_PCell,                                                  
			UnifyParaID.LTECA_BLER_SCell1,                                                 
			UnifyParaID.LTECA_BLER_SCell2,                                                 
			                                                                                       
			UnifyParaID.LTECA_BLERCode0_Total,                                             
			UnifyParaID.LTECA_BLERCode0__PCell,                                            
			UnifyParaID.LTECA_BLERCode0__SCell1,                                           
			UnifyParaID.LTECA_BLERCode0__SCell2,                                           
			                                                                               
			UnifyParaID.LTECA_BLERCode1_Total,                                             
			UnifyParaID.LTECA_BLERCode1_PCell,                                             
			UnifyParaID.LTECA_BLERCode1_SCell1,                                            
			UnifyParaID.LTECA_BLERCode1_SCell2,                                            
			                                                                               
			UnifyParaID.LTECA_InitialBLER_Total,                                           
			UnifyParaID.LTECA_InitialBLER_PCell,                                           
			UnifyParaID.LTECA_InitialBLER_SCell1,                                          
			UnifyParaID.LTECA_InitialBLER_SCell2,                                          
			                                                                               
			UnifyParaID.LTECA_InitialBLERCode0_Total,                                      
			UnifyParaID.LTECA_InitialBLERCode0_PCell,                                      
			UnifyParaID.LTECA_InitialBLERCode0_SCell1,                                     
			UnifyParaID.LTECA_InitialBLERCode0_SCell2,                                     
			                                                                               
			                                                                               
			UnifyParaID.LTECA_InitialBLERCode1_Total,                                      
			UnifyParaID.LTECA_InitialBLERCode1_PCell,                                                                                                                      
			UnifyParaID.LTECA_InitialBLERCode1_SCell1,                                                                                                                     
			UnifyParaID.LTECA_InitialBLERCode1_SCell2,                                     
			                                                                               
			UnifyParaID.LTECA_ResidualBLER_Total,                                          
			UnifyParaID.LTECA_ResidualBLER_PCell,                                                                            
			UnifyParaID.LTECA_ResidualBLER_SCell1,                                                                                   
			UnifyParaID.LTECA_ResidualBLER_SCell2,                                                                           
			                                                                                                                   
			UnifyParaID.LTECA_ResidualBLERCode0_Total,                                                                                       
			UnifyParaID.LTECA_ResidualBLERCode0_PCell,                                                                                       
			UnifyParaID.LTECA_ResidualBLERCode0_SCell1,                                                                                      
			UnifyParaID.LTECA_ResidualBLERCode0_SCell2,                                                                                      
			                                                                                                                   
			                                                                                                                   
			UnifyParaID.LTECA_ResidualBLERCode1_Total,                                                                                       
			UnifyParaID.LTECA_ResidualBLERCode1_PCell,                                                                                       
			UnifyParaID.LTECA_ResidualBLERCode1_SCell1,                                                                                      
			UnifyParaID.LTECA_ResidualBLERCode1_SCell2,                                    
			                                                                                                                           
			                                                                                                                                                                                                                                                                                                            
			UnifyParaID.LTECA_QPSK_Total,                                                                                        
			UnifyParaID.LTECA_QPSK_PCell,                                                                                     
			UnifyParaID.LTECA_QPSK_SCell1,                                                                                    
			UnifyParaID.LTECA_QPSK_SCell2,                                                 
			                                                                               
			UnifyParaID.LTECA_16QAM_Total,                                                                                
			UnifyParaID.LTECA_16QAM_PCell,                                                                                   
			UnifyParaID.LTECA_16QAM_SCell1,                                                                                  
			UnifyParaID.LTECA_16QAM_SCell2,                                                
			                                                                               
			UnifyParaID.LTECA_64QAM_Total,                                                                                       
			UnifyParaID.LTECA_64QAM_PCell,                                                                                         
			UnifyParaID.LTECA_64QAM_SCell1,                                                                                        
			UnifyParaID.LTECA_64QAM_SCell2,                                                                                        
			                                                                               
			UnifyParaID.LTECA_QPSKCode0_Total,                                                                                             
			UnifyParaID.LTECA_QPSKCode0_PCell,                                                                                             
			UnifyParaID.LTECA_QPSKCode0_SCell1,                                                                                            
			UnifyParaID.LTECA_QPSKCode0_SCell2,                                                                                            
			                                                                               
			UnifyParaID.LTECA_16QAMCode0_Total,                                                                                                
			UnifyParaID.LTECA_16QAMCode0_PCell,                                                                                                
			UnifyParaID.LTECA_16QAMCode0_SCell1,                                                                                               
			UnifyParaID.LTECA_16QAMCode0_SCell2,                                                   
			                                                                               
			                                                                                                                             
			UnifyParaID.LTECA_64QAMCode0_Total,                                                                                                         
			UnifyParaID.LTECA_64QAMCode0_PCell,                                                                                                         
			UnifyParaID.LTECA_64QAMCode0_SCell1,                                                                                                        
			UnifyParaID.LTECA_64QAMCode0_SCell2,                                                                                                        
			                                                                               
			UnifyParaID.LTECA_QPSKCode1_Total,                                                                                                     
			UnifyParaID.LTECA_QPSKCode1_PCell,                                             
			UnifyParaID.LTECA_QPSKCode1_SCell1,                                            
			UnifyParaID.LTECA_QPSKCode1_SCell2,                                            
			                                                                               
			                                                                               
			UnifyParaID.LTECA_16QAMCode1_Total,                                            
			UnifyParaID.LTECA_16QAMCode1_PCell,                                            
			UnifyParaID.LTECA_16QAMCode1_SCell1,                                           
			UnifyParaID.LTECA_16QAMCode1_SCell2,                                           
			                                                                               
			                                                                               
			UnifyParaID.LTECA_64QAMCode1_Total,                                            
			UnifyParaID.LTECA_64QAMCode1_PCell,                                            
			UnifyParaID.LTECA_64QAMCode1_SCell1,                                           
			UnifyParaID.LTECA_64QAMCode1_SCell2,                                           
			                                                                               
			UnifyParaID.LTECA_MCSAvg_Total,                                               
			UnifyParaID.LTECA_MCSAvg_PCell,                                                
			UnifyParaID.LTECA_MCSAvg_SCell1,                                               
			UnifyParaID.LTECA_MCSAvg_SCell2,  
			
			UnifyParaID.LTECA_UL_BandWidth,                                                
			UnifyParaID.LTECA_UL_BandWidth_SCell1,                                               
			UnifyParaID.LTECA_UL_BandWidth_SCell2, 
			
			UnifyParaID.LTECA_UL_Freq_SCell1,  
			UnifyParaID.LTECA_UL_Freq_SCell2
			
	};
	
	
	
	/**
	 * 特殊结构体 ---> TD页面
	 */
	public int[] PageQueryStructTD = new int[]{
		UnifyStruct.TDPhysChannelInfoDataV2.FLAG,
		UnifyStruct.FLAG_TD_HSDPAPhysChannelInfoData,
	};
	
	/**
	 * 特殊结构体 ---> EDGE页面
	 */
	public int[] PageQueryStructEDGE = new int[]{
		UnifyStruct.FLAG_TD_Activate_PDP_Context_Accept_Win_Data
	};
	
	
	/**
	 * 公共参数及部分网络参数邻区查询
	 * ******************************************华丽分割*******************************************
	 */
	
	public static int[] pageGSMQueryPublicId = new int[] {
		UnifyParaID.G_NCell_N1_BCCH,    
		UnifyParaID.G_NCell_N2_BCCH,    
		UnifyParaID.G_NCell_N3_BCCH,    
		UnifyParaID.G_NCell_N4_BCCH,    
		UnifyParaID.G_NCell_N5_BCCH,    
		UnifyParaID.G_NCell_N6_BCCH,    
		                                
		UnifyParaID.G_NCell_N1_BSIC,    
		UnifyParaID.G_NCell_N2_BSIC,    
		UnifyParaID.G_NCell_N3_BSIC,    
		UnifyParaID.G_NCell_N4_BSIC,    
		UnifyParaID.G_NCell_N5_BSIC,    
		UnifyParaID.G_NCell_N6_BSIC,    
		                                
		UnifyParaID.G_NCell_N1_BCCH,    
		UnifyParaID.G_NCell_N2_BCCH,    
		UnifyParaID.G_NCell_N3_BCCH,    
		UnifyParaID.G_NCell_N4_BCCH,    
		UnifyParaID.G_NCell_N5_BCCH,    
		UnifyParaID.G_NCell_N6_BCCH,    
		                                
		UnifyParaID.G_NCell_N1_BSIC,    
		UnifyParaID.G_NCell_N2_BSIC,    
		UnifyParaID.G_NCell_N3_BSIC,    
		UnifyParaID.G_NCell_N4_BSIC,    
		UnifyParaID.G_NCell_N5_BSIC,    
		UnifyParaID.G_NCell_N6_BSIC,    
		                                
		UnifyParaID.G_NCell_N1_RxLevel, 
		UnifyParaID.G_NCell_N2_RxLevel, 
		UnifyParaID.G_NCell_N3_RxLevel, 
		UnifyParaID.G_NCell_N4_RxLevel, 
		UnifyParaID.G_NCell_N5_RxLevel, 
		UnifyParaID.G_NCell_N6_RxLevel, 
		                                
		UnifyParaID.G_NCell_N1_C1,      
		UnifyParaID.G_NCell_N2_C1,      
		UnifyParaID.G_NCell_N3_C1,      
		UnifyParaID.G_NCell_N4_C1,      
		UnifyParaID.G_NCell_N5_C1,      
		UnifyParaID.G_NCell_N6_C1,      
		                                
		UnifyParaID.G_NCell_N1_C2,      
		UnifyParaID.G_NCell_N2_C2,      
		UnifyParaID.G_NCell_N3_C2,      
		UnifyParaID.G_NCell_N4_C2,      
		UnifyParaID.G_NCell_N5_C2,      
		UnifyParaID.G_NCell_N6_C2,
		

		UnifyParaID.G_Ser_Cell_ID,
		UnifyParaID.G_Ser_BCCHLev,
		UnifyParaID.G_Ser_MCC,
		UnifyParaID.G_Ser_MNC,
		UnifyParaID.G_Ser_BCCH,
		UnifyParaID.G_Ser_BSIC 
	};
	
	public static int[] pageWCDMAQueryPublicId = new int[] {
		

		UnifyParaID.W_Ser_Total_RSCP,
		UnifyParaID.W_Ser_Total_EcIo,
		UnifyParaID.W_Ser_MCC,
		UnifyParaID.W_Ser_MNC,
		UnifyParaID.W_Ser_DL_UARFCN,
		UnifyParaID.W_Ser_Max_PSC 
	};
	
	public static int[] pageTdQueryPublicId = new int[] {
		UnifyParaID.T_NCell_N1_RSCP,                       
		UnifyParaID.T_NCell_N2_RSCP,
		UnifyParaID.T_NCell_N3_RSCP,                
		UnifyParaID.T_NCell_N4_RSCP,
		UnifyParaID.T_NCell_N5_RSCP,
		UnifyParaID.T_NCell_N6_RSCP,
		
		UnifyParaID.T_NCell_N1_UARFCN,
		UnifyParaID.T_NCell_N2_UARFCN,
		UnifyParaID.T_NCell_N3_UARFCN,
		UnifyParaID.T_NCell_N4_UARFCN,
		UnifyParaID.T_NCell_N5_UARFCN,
		UnifyParaID.T_NCell_N6_UARFCN,
		
		UnifyParaID.T_NCell_N1_CPI,
		UnifyParaID.T_NCell_N2_CPI,
		UnifyParaID.T_NCell_N3_CPI,
		UnifyParaID.T_NCell_N4_CPI,
		UnifyParaID.T_NCell_N5_CPI,
		UnifyParaID.T_NCell_N6_CPI,
		
		UnifyParaID.T_NCell_N1_ISCP,
		UnifyParaID.T_NCell_N2_ISCP,
		UnifyParaID.T_NCell_N3_ISCP,
		UnifyParaID.T_NCell_N4_ISCP,
		UnifyParaID.T_NCell_N5_ISCP,
		UnifyParaID.T_NCell_N6_ISCP,
		
		UnifyParaID.T_NCell_N1_CPI,   
		UnifyParaID.T_NCell_N2_CPI,   
		UnifyParaID.T_NCell_N3_CPI,   
		UnifyParaID.T_NCell_N4_CPI,   
		UnifyParaID.T_NCell_N5_CPI,   
		UnifyParaID.T_NCell_N6_CPI,   

		UnifyParaID.T_NCell_N1_CarrierRSSI,
		UnifyParaID.T_NCell_N2_CarrierRSSI,
		UnifyParaID.T_NCell_N3_CarrierRSSI,
		UnifyParaID.T_NCell_N4_CarrierRSSI,
		UnifyParaID.T_NCell_N5_CarrierRSSI,
		UnifyParaID.T_NCell_N6_CarrierRSSI,
		
		UnifyParaID.T_NCell_N1_PathLoss,
		UnifyParaID.T_NCell_N2_PathLoss,
		UnifyParaID.T_NCell_N3_PathLoss,
		UnifyParaID.T_NCell_N4_PathLoss,
		UnifyParaID.T_NCell_N5_PathLoss,
		UnifyParaID.T_NCell_N6_PathLoss,
		
		UnifyParaID.T_NCell_N1_Rn,
		UnifyParaID.T_NCell_N2_Rn,
		UnifyParaID.T_NCell_N3_Rn,
		UnifyParaID.T_NCell_N4_Rn,
		UnifyParaID.T_NCell_N5_Rn,
		UnifyParaID.T_NCell_N6_Rn,
		

		UnifyParaID.TD_Ser_PCCPCHRSCP,
		UnifyParaID.TD_Ser_PCCPCHC2I,
		UnifyParaID.TD_Ser_MCC,
		UnifyParaID.TD_Ser_MNC,
		UnifyParaID.TD_Ser_UARFCN,
		UnifyParaID.TD_Ser_CPI
	};
	
	public static int[] pageLteQueryPublicId = new int[] {
		
		UnifyParaID.L_SRV_ECIP2,
		UnifyParaID.L_SRV_ECIP3,

		UnifyParaID.L_SRV_RSRP,
		UnifyParaID.L_SRV_SINR,
		UnifyParaID.L_SRV_MCC,
		UnifyParaID.L_SRV_MNC,
		UnifyParaID.L_SRV_EARFCN,
		UnifyParaID.L_SRV_PCI 
		
	};
	
	public static int[] pageCDMAQueryPublicId = new int[] {
		
		UnifyParaID.C_NID,
		UnifyParaID.C_Frequency, 
		UnifyParaID.C_TotalEcIo,
		UnifyParaID.C_SID, 
		UnifyParaID.C_ReferencePN
	};

	public static int[] pageNBIoTQueryPublicId = new int[] {
			UnifyParaID.NB_SRV_RSRP,
			UnifyParaID.NB_SRV_SINR,
			UnifyParaID.NB_SRV_TECHNOLOGY,
			UnifyParaID.NB_SRV_EARFCN,
			UnifyParaID.NB_SRV_PCI,
			UnifyParaID.L_THR_DL_UDP,
			UnifyParaID.L_THR_DL_DDP,
			UnifyParaID.NB_IMSI,
			UnifyParaID.L_Thr_DL_PDCP_Thr,
			UnifyParaID.L_Thr_UL_PDCP_Thr
	};

	public static int[] pageENDCQueryPublicId = new int[] {
			UnifyParaID.L_Thr_DL_PDCP_Thr,
			UnifyParaID.L_Thr_UL_PDCP_Thr,
			UnifyParaID.L_Thr_DL_RLC_Thr,
			UnifyParaID.L_Thr_UL_RLC_Thr,
			UnifyParaID.L_Thr_DL_MAC_Thr,
			UnifyParaID.L_Thr_UL_MAC_Thr,
			UnifyParaID.L_Thr_DL_Phy_Thr,
			UnifyParaID.L_Thr_UL_Phy_Thr,
			UnifyParaID.NR_Thr_DL_PDCP_Thr,
			UnifyParaID.NR_Thr_UL_PDCP_Thr,
			UnifyParaID.NR_Thr_DL_RLC_Thr,
			UnifyParaID.NR_Thr_UL_RLC_Thr,
			UnifyParaID.NR_Thr_DL_MAC_Thr,
			UnifyParaID.NR_Thr_UL_MAC_Thr,
			UnifyParaID.NR_Thr_DL_Phy_Thr,
			UnifyParaID.NR_Thr_UL_Phy_Thr
	};

	public static int[] pageDataService = new int[] {
	UnifyParaID.DATA_FTP_DOWNLOAD_RATE,
	UnifyParaID.DATA_FTP_UPLOAD_RATE,
	UnifyParaID.DATA_PBM_DOWNLOAD_RATE,
	UnifyParaID.DATA_PBM_UPLOAD_RATE,
	UnifyParaID.DATA_HTTP_DOWNLOAD_RATE,
	UnifyParaID.DATA_HTTP_PAGE_RATE,
	UnifyParaID.DATA_HTTP_UP_RATE,
	UnifyParaID.DATA_MULTIFTP_DOWN,
	UnifyParaID.DATA_MULTIFTP_UP,
	UnifyParaID.DATA_SPEEDTEST_DOWN,
	UnifyParaID.DATA_SPEEDTEST_UP,
	UnifyParaID.DATA_IPERF_DOWN,
	UnifyParaID.DATA_IPERF_UP,
	UnifyParaID.DATA_UDP_UL,
	UnifyParaID.DATA_UDP_DL
	};
}
