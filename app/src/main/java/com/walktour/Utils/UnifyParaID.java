/*
*文件名:UnifyParaID.java
*版权：CopyrightDingliComTech.Co.Ltd.AllRightsReserved.
*描述:[该类的简要描述]
*创建人:tangwq
*创建时间:2013-2-19
*
*修改人：
*修改时间:
*修改内容：[修改内容]
*/
package com.walktour.Utils;

/**
*[统一解码库参数ID定义]<BR>
*各种网络参数ID定义以[头_参数名]出现，网络类型头如下
*W_(WCDMA),G_(GSM),T_(TDSCDMA),L_(LTE),C_(CDMA),E_(EVDO)
*@authortangwq
*@version[WalkTourClientV100R001C03,2013-2-19]
*/
public class UnifyParaID{
	
	/**
	 * 当前网络类型
	 */
	public static final int CURRENT_NETWORKTYPE 		= 0x7F1D2001;
	public static final int CURRENT_STATE_GSM	 		= 0x7F1D2002;
	public static final int CURRENT_STATE_TDSCDMA	 	= 0x7F1D2003;
	public static final int CURRENT_STATE_WCDMA	 	= 0x7F1D2004;
	public static final int CURRENT_STATE_CDMA	 		= 0x7F1D2005;
	public static final int CURRENT_STATE_EVDO	 		= 0x7F1D2006;
	public static final int CURRENT_STATE_LTE	 		= 0x7F1D2007;
	public static final int CURRENT_STATE_NB_IOT	 	= 0x7F1D2007;
	public static final int CURRENT_STATE_CAT_M	 	= 0x7F1D2007;
	public static final int CURRENT_STATE_ENDC	 	= 0x7F1D2007;
	public static final int LTECA_Capacity_Packet_Capability = 0x7F1D2008;
	public static final int NET_GSM 						= 0x01;
	public static final int NET_WCDMA 					= 0x02;
	public static final int NET_TDSCDMA					= 0x04;
	public static final int NET_CDMA_EVDO				= 0x08;
	public static final int NET_LTE						= 0x10;
	public static final int NET_NB_IoT                  = 0x30;
	public static final int NET_CAT_M                  = 0x100;
	public static final int NET_UNKONWN					= 0x20;
	public static final int NET_NO_SERVICE				= 0x80;
	public static final int NET_ENDC                  = 0x200;
	//PBM业务各网络下侵扰度(PBM给的算法,由数据集集成,计算出来的一个虚拟字段)
	public static final int PBM_RBC						= 0x0A0050B1;		//网络类型:Radio Bearer Configuration
		
	
	/*CDMA显示参数*/
	public static final int C_Frequency					= 0x7F010012;
	public static final int C_SID						= 0x7F010301;
	public static final int C_MCC						= 0x7F010319;
	public static final int C_MNC						= 0x7F010318;
	public static final int C_NID						= 0x7F010302;
	public static final int C_BID						= 0x7F010303;
	public static final int C_ReferencePN				= 0x7F01000A;
	public static final int C_ReferenceEcIo				= 0x7F01000E;
	public static final int C_ReferenceEc				= 0x7F010011;
	public static final int C_TotalEcIo					= 0x7F01000C;
	public static final int C_TotalEc					= 0x7F01000F;
	public static final int C_MaxEcIoPN					= 0x7F01000B;
	public static final int C_MaxEcIo					= 0x7F01000D;
	public static final int C_MaxEc						= 0x7F010010;
	public static final int C_RxAGC						= 0x7F010001;
	public static final int C_TxAGC						= 0x7F010002;
	public static final int C_TxPower					= 0x7F010003;
	public static final int C_FFER						= 0x7F010006;
	public static final int C_PhoneState				= 0x7F1C0001;//cpsIdle= 0,cpsWait= 1,cpsBusy= 2,cpsRelease= 3,cpsPage= 3
	public static final int C_State						= 0x7F1D2005;//cpsIdle= 0,cpsWait= 1,cpsBusy= 2,cpsRelease= 3,cpsPage= 3
	public static final int C_IdleState					= 0x7F018305;
	public static final int C_TxGainAdj					= 0x7F010004;
	public static final int C_LockedNum					= 0x7F010008;
	public static final int C_ActiveSetNum				= 0x7F010009;
	public static final int C_DRC_Index					= 0x7F018009;
	public static final int C_DRC_Value					= 0x7F01800A;
	public static final int C_DRC_Cover					= 0x7F01800B;
	public static final int C_Win_A						= 0x7F010304;
	public static final int C_Win_N						= 0x7F010305;
	public static final int C_Win_R						= 0x7F010306;
	public static final int C_Pilot_Inc					= 0x7F010307;
	public static final int C_T_Comp					= 0x7F010308;
	public static final int C_T_Drop					= 0x7F010309;
	public static final int C_T_TDrop					= 0x7F01030A;
	public static final int C_T_Add						= 0x7F010018;
	public static final int C_Soft_Slope				= 0x7F01030B;
	public static final int C_NeighborMaxAge			= 0x7F01030E;
	public static final int C_Ec_Threshold				= 0x7F01030C;
	public static final int C_ESN						= 0x7F01030F;
	public static final int C_IMSI_Hi					= 0x7F010310;
	public static final int C_IMSI_Lo					= 0x7F010311;
	public static final int C_EV_RxAGC0					= 0x7F018001;
	public static final int C_EV_RxAGC1					= 0x7F018002;
	public static final int C_EV_TxAGC					= 0x7F018003;
	public static final int C_TotalC_I					= 0x7F018007;
	public static final int C_TotalSINR					= 0x7F018008;

	/*EVDO参数ID*/
	public static final int E_MaxT2P					= 0x7F018507;
	public static final int E_Band						= 0x7F01800E;
	public static final int E_EV_Frequenc				= 0x7F01800D;
	public static final int E_ServingSectorPN			= 0x7F01800F;
	public static final int E_UATI						= 0x7F018010;
	public static final int E_EVsectorInfo				= 0x7F0107DC;
	public static final int E_DedicateUserCount			= 0x7F01830B;
	public static final int E_Carrier1_EV_RxAGC0		= 0x7F018001;
	public static final int E_Carrier1_EV_RxAGC1		= 0x7F018002;
	public static final int E_Carrier1_EV_TxAGC			= 0x7F018003;
	public static final int E_Carrier1_TxPilotPower		= 0x7F018004;
	public static final int E_Carrier1_TxOpenLoopPower	= 0x7F018005;
	public static final int E_Carrier1_TxClosedLoopAdjust= 0x7F018006;
	public static final int E_Carrier1_TotalC_I			= 0x7F018007;
	public static final int E_Carrier1_TotalSINR		= 0x7F018008;
	public static final int E_Carrier1_DRC_Index		= 0x7F018009;
	public static final int E_Carrier1_DRC_Value		= 0x7F01800A;
	public static final int E_Carrier1_DRC_Cover		= 0x7F01800B;
	public static final int E_ActiveCount				= 0x7F01800C;
	public static final int E_Carrier1_EV_Frequency		= 0x7F01800D;
	public static final int E_Carrier1_Band				= 0x7F01800E;
	public static final int E_Carrier1_ServingSectorPN	= 0x7F01800F;

	//Rev.AReverseInfo.-2
	public static final int E_DRC2Pilot					= 0x7F018502;
	public static final int E_TxMode					= 0x7F018501;
	public static final int E_RRI2Pilot					= 0x7F018503;
	public static final int E_DSC2Pilot					= 0x7F018504;
	public static final int E_DSC						= 0x7F018508;
	public static final int E_Data2Pilot				= 0x7F018505;
	public static final int E_TxPacketSize				= 0x7F018509;
	public static final int E_Aux2Pilot					= 0x7F018506;
	public static final int E_FRAB						= 0x7F01850F;

	//EVDOState-3
	public static final int E_Session					= 0x7F018301;
	public static final int E_ALMP						= 0x7F018303;
	public static final int E_Idle						= 0x7F018305;
	public static final int E_OverHeadMsg				= 0x7F018306;
	public static final int E_HbridMode					= 0x7F018309;
	public static final int E_Connection_Release		= 0x7F018011;
	public static final int E_AT						= 0x7F018302;
	public static final int E_Init						= 0x7F018304;
	public static final int E_Connected					= 0x7F018307;
	public static final int E_RouteUpdate				= 0x7F018308;
	public static final int E_Session_Release			= 0x7F018012;

	//EVDOThroughput-3
	public static final int E_RxRLP_Thr					= 0x7F018102;
	public static final int E_RLP_Error_Rate			= 0x7F018106;
	public static final int E_RxPacket_Thr				= 0x7F018202;
	public static final int E_RxPER						= 0x7F018206;
	public static final int E_RxSuPacket_Thr_Ist		= 0x7F018402;
	public static final int E_RxSuPacket_Thr			= 0x7F018401;
	public static final int E_RxSuPER					= 0x7F018407;
	public static final int E_TxRLP_Thr					= 0x7F018104;
	public static final int E_RLP_RTX_Rate				= 0x7F018107;
	public static final int E_TxPacket_Thr				= 0x7F018205;
	public static final int E_TxPER						= 0x7F018207;
	public static final int E_RxMuPacket_Thr_Ist		= 0x7F018405;
	public static final int E_RxMuPacket_Thr			= 0x7F018404;
	public static final int E_RxMuPER					= 0x7F018408;

	public static final int E_EV_Revision				= 0x7F01830A;

	/**CDMA结构体ID
	 * neighbor:ActiveSetType,CdmaFreq,CdmaPn,CdmaRssi,CdmaRSCP,CdmaEcIo,CdmaDummy 
	 */
	public static final int C_cdmaServingNeighbor		= 0x7F0107D6;
	/**EVDO结构体ID
	 * neighbor:ActiveSetType,EvdoFreq,EvdoPn,EvdoRssi,EvdoEcIo,EvdoTotalC2I 
	 */
	public static final int E_EVServingNeighbor			= 0x7F0107D8;
	public static final int E_EVPilotActiveSet			= 0x7F0107D9;
	public static final int E_EVPilotCadidateSet		= 0x7F0107DA;
	public static final int E_EVPilotNeighborSet		= 0x7F0107DB;

	/*TDScdmaServingCellInfo*/
	public static final int TD_Ser_MCC					= 0x7F030301;
	public static final int TD_Ser_MNC					= 0x7F030302;
	public static final int TD_Ser_LAC					= 0x7F030303;
	public static final int TD_Ser_UARFCN				= 0x7F030126;
	public static final int TD_Ser_CellID				= 0x7F03030A;
	public static final int TD_Ser_DCHURAFCN			= 0x7F030127;
	public static final int TD_Ser_RNCID				= 0x7F030309;
	public static final int TD_Ser_CPI					= 0x7F030125;
	public static final int TD_Ser_URAID				= 0x7F03030B;
	public static final int TD_Ser_CarrierRSSI			= 0x7F030101;
	public static final int TD_Ser_UpPCHTxPower			= 0x7F030115;
	public static final int TD_Ser_PCCPCHRSCP			= 0x7F03011B;
	public static final int TD_Ser_DPCHRSCP				= 0x7F030112;
	public static final int TD_Ser_PCCPCHISCP			= 0x7F03011D;
	public static final int TD_Ser_DPCHISCP				= 0x7F030113;
	public static final int TD_Ser_PCCPCHC2I			= 0x7F030120;
	public static final int TD_Ser_DPCHC2I				= 0x7F030114;
	public static final int TD_Ser_PCCPCHSIR			= 0x7F030122;
	public static final int TD_Ser_UETxPower			= 0x7F030102;
	public static final int TD_Ser_PCCPCHPathloss		= 0x7F03011E;
	public static final int TD_Ser_TA					= 0x7F030117;
	public static final int TD_Ser_Main_State			= 0x7F030E02;
	public static final int TD_Ser_BLER					= 0x7F030119;
	public static final int TD_Ser_RAC					= 0x7F030304;
	public static final int TD_Ser_Connected_State		= 0x7F030E04;
	
	public static final int TD_Main_Current_State		= 0x7F030E02;
	public static final int TD_Main_Previous_State		= 0x7F030E01;
	public static final int TD_Connected_Current_State	= 0x7F030E04;
	public static final int TD_Connected_Previous_State	= 0x7F030E03;
	
	public static final int TD_Attach_Allowed			= 0x7F03030D;
	public static final int TD_Cell_Barred				= 0x7F03030C;
	public static final int TD_Q_Rxlevmin				= 0x7F030A02;
	public static final int TD_Max_Allowed_TxPower		= 0x7F030313;
	public static final int TD_Served_RS				= 0x7F03012A;
	
	
	
	/*TDScdmaServingNeighbor*/
	public static final int T_NCell_N1_UARFCN			= 0x7F030201;
	public static final int T_NCell_N1_CPI				= 0x7F030202;
	public static final int T_NCell_N1_RSCP				= 0x7F030203;
	public static final int T_NCell_N1_CarrierRSSI		= 0x7F030204;
	public static final int T_NCell_N1_PathLoss			= 0x7F030205;
	public static final int T_NCell_N1_Rn				= 0x7F030206;
	public static final int T_NCell_N2_UARFCN			= 0x7F030207;
	public static final int T_NCell_N2_CPI				= 0x7F030208;
	public static final int T_NCell_N2_RSCP				= 0x7F030209;
	public static final int T_NCell_N2_CarrierRSSI		= 0x7F03020A;
	public static final int T_NCell_N2_PathLoss			= 0x7F03020B;
	public static final int T_NCell_N2_Rn				= 0x7F03020C;
	public static final int T_NCell_N3_UARFCN			= 0x7F03020D;
	public static final int T_NCell_N3_CPI				= 0x7F03020E;
	public static final int T_NCell_N3_RSCP				= 0x7F03020F;
	public static final int T_NCell_N3_CarrierRSSI		= 0x7F030210;
	public static final int T_NCell_N3_PathLoss			= 0x7F030211;
	public static final int T_NCell_N3_Rn				= 0x7F030212;
	public static final int T_NCell_N4_UARFCN			= 0x7F030213;
	public static final int T_NCell_N4_CPI				= 0x7F030214;
	public static final int T_NCell_N4_RSCP				= 0x7F030215;
	public static final int T_NCell_N4_CarrierRSSI		= 0x7F030216;
	public static final int T_NCell_N4_PathLoss			= 0x7F030217;
	public static final int T_NCell_N4_Rn				= 0x7F030218;
	public static final int T_NCell_N5_UARFCN			= 0x7F030219;
	public static final int T_NCell_N5_CPI				= 0x7F03021A;
	public static final int T_NCell_N5_RSCP				= 0x7F03021B;
	public static final int T_NCell_N5_CarrierRSSI		= 0x7F03021C;
	public static final int T_NCell_N5_PathLoss			= 0x7F03021D;
	public static final int T_NCell_N5_Rn				= 0x7F03021E;
	public static final int T_NCell_N6_UARFCN			= 0x7F03021F;
	public static final int T_NCell_N6_CPI				= 0x7F030220;
	public static final int T_NCell_N6_RSCP				= 0x7F030221;
	public static final int T_NCell_N6_CarrierRSSI		= 0x7F030222;
	public static final int T_NCell_N6_PathLoss			= 0x7F030223;
	public static final int T_NCell_N6_Rn				= 0x7F030224;
	public static final int T_NCell_N1_ISCP				= 0x7F030107;
	public static final int T_NCell_N1_Sir				= 0x7F030226;
	public static final int T_NCell_N1_Qoffset			= 0x7F030227;
	public static final int T_NCell_N2_ISCP				= 0x7F03010A;
	public static final int T_NCell_N2_Sir				= 0x7F030229;
	public static final int T_NCell_N2_Qoffset			= 0x7F03022A;
	public static final int T_NCell_N3_ISCP				= 0x7F03010D;
	public static final int T_NCell_N3_Sir				= 0x7F03022C;
	public static final int T_NCell_N3_Qoffset			= 0x7F03022D;
	public static final int T_NCell_N4_ISCP				= 0x7F030110;
	public static final int T_NCell_N4_Sir				= 0x7F03022F;
	public static final int T_NCell_N4_Qoffset			= 0x7F030230;
	public static final int T_NCell_N5_ISCP				= 0x7F030113;
	public static final int T_NCell_N5_Sir				= 0x7F030232;
	public static final int T_NCell_N5_Qoffset			= 0x7F030233;
	public static final int T_NCell_N6_ISCP				= 0x7F030234;
	public static final int T_NCell_N6_Sir				= 0x7F030235;
	public static final int T_NCell_N6_Qoffset			= 0x7F030236;
	public static final int T_NCell_CellCount			= 0x7F030237;
	public static final int T_NCell_N1_C2I				= 0x7F030238;
	public static final int T_NCell_N2_C2I				= 0x7F030239;
	public static final int T_NCell_N3_C2I				= 0x7F03023A;
	public static final int T_NCell_N4_C2I				= 0x7F03023B;
	public static final int T_NCell_N5_C2I				= 0x7F03023C;
	public static final int T_NCell_N6_C2I				= 0x7F03023D;

	/*TDScdmaThroughput*/
	public static final int TD_Thr_DL_RLC_Thr			= 0x7F030601;
	public static final int TD_Thr_UL_RLC_Thr			= 0x7F030602;
	public static final int TD_Thr_DL_PDCP_Thr			= 0x7F030603;
	public static final int TD_Thr_UL_PDCP_Thr			= 0x7F030604;
	public static final int TD_Thr_DL_RLC_Error_Rate	= 0x7F030605;
	public static final int TD_Thr_UL_RLC_RTX_Rate		= 0x7F030606;

	/**TD ts**/
	public static final int TD_TS1_DPCH_ISCP			= 0x7F030110;
	public static final int TD_TS2_DPCH_ISCP			= 0x7F030104;
	public static final int TD_TS3_DPCH_ISCP			= 0x7F030107;
	public static final int TD_TS4_DPCH_ISCP			= 0x7F03010A;
	public static final int TD_TS5_DPCH_ISCP			= 0x7F03010D;
	public static final int TD_TS6_DPCH_ISCP			= 0x7F030110;
	
	public static final int TD_TS2_DPCH_RSCP			= 0x7F030103;
	public static final int TD_TS3_DPCH_RSCP			= 0x7F030106;
	public static final int TD_TS4_DPCH_RSCP			= 0x7F030109;
	public static final int TD_TS5_DPCH_RSCP			= 0x7F03010C;
	public static final int TD_TS6_DPCH_RSCP			= 0x7F03010F;
	
	public static final int TD_TS1_TxPower				= 0x7F030701;
	public static final int TD_TS2_TxPower				= 0x7F030702;
	public static final int TD_TS3_TxPower				= 0x7F030703;
	
	public static final int TD_TS2_DPCH_SIR				= 0x7F030105;
	public static final int TD_TS3_DPCH_SIR				= 0x7F030108;
	public static final int TD_TS4_DPCH_SIR				= 0x7F03010B;
	public static final int TD_TS5_DPCH_SIR				= 0x7F03010E;
	public static final int TD_TS6_DPCH_SIR				= 0x7F030111;

	
	
	
	
	/**TDScdmaHSDPARadio*/
	public static final int TD_DPA_HSDPA_Session		= 0x7F030501;
	public static final int TD_DPA_PDSCH_RSCP			= 0x7F030704;
	public static final int TD_DPA_PDSCH_SIR			= 0x7F030706;
	public static final int TD_DPA_PDSCH_C2I			= 0x7F030705;
	public static final int TD_DPA_PDSCH_ISCP			= 0x7F030707;
	public static final int TD_DPA_SCCH_RSCP			= 0x7F030708;
	public static final int TD_DPA_SCCH_SIR				= 0x7F03070A;
	public static final int TD_DPA_SCCH_C2I				= 0x7F030709;
	public static final int TD_DPA_SCCH_ISCP			= 0x7F03070B;
	public static final int TD_DPA1_Category			= 0x7F030502;
	public static final int TD_DPA1_Work_UARFCN			= 0x7F03073A;
	public static final int TD_DPA1_H_RNTI				= 0x7F030716;
	public static final int TD_DPA1_A_DPCH_RSCP			= 0x7F03070C;
	public static final int TD_DPA1_A_DPCH_SIR			= 0x7F03070E;
	public static final int TD_DPA1_A_DPCH_C2I			= 0x7F03070D;
	public static final int TD_DPA1_A_DPCH_ISCP			= 0x7F03070F;
	public static final int TD_DPA1_A_DPCH_BLER			= 0x7F030710;
	public static final int TD_DPA1_A_SICH_TxPower		= 0x7F030712;
	
	/*TDScdmaHSDPAQoS*/
	public static final int TD_DPA2_16QAM_Rate			= 0x7F03071C;
	public static final int TD_DPA2_QPSK_Rate			= 0x7F03071D;
	public static final int TD_DPA2_SCCH_ScheduleCount	= 0x7F030717;
	public static final int TD_DPA2_SCCH_ScheduleRate	= 0x7F030718;
	public static final int TD_DPA2_SCCH_BLER			= 0x7F03071A;
	public static final int TD_DPA2_SCCH_DecodeSuccRate	= 0x7F030719;
	public static final int TD_DPA2_DSCH_NACK_Rate		= 0x7F030724;
	public static final int TD_DPA2_DSCH_ACK_Rate		= 0x7F030725;
	public static final int TD_DPA2_DSCH_UnUsed_Rate	= 0x7F030736;
	public static final int TD_DPA2_DSCH_Error_Rate		= 0x7F030737;
	public static final int TD_DPA2_DSCH_ErrorBlocks	= 0x7F030738;
	public static final int TD_DPA2_OneTime_Trans_SuccRate= 0x7F030920;
	public static final int TD_DPA2_HS_TransBlock_Size	= 0x7F030739;
	public static final int TD_DPA2_Max_HSDPA_CQI		= 0x7F030721;
	public static final int TD_DPA2_Mean_HSDPA_CQI		= 0x7F030722;
	public static final int TD_DPA2_Min_HSDPA_CQI		= 0x7F030723;
	public static final int TD_DPA2_PDSCH_CodesUsedRate	= 0x7F03071E;
	public static final int TD_DPA2_PDSCH_TimeSlotUsed	= 0x7F03071F;
	public static final int TD_DPA2_PDSCH_AverageSize	= 0x7F030720;
	public static final int TD_DPA2_PDSCH_InitialBLER	= 0x7F030727;
	public static final int TD_DPA2_PDSCH_TotalBLER		= 0x7F030728;
	public static final int TD_DPA2_Phys_Schedule_Thr	= 0x7F03072A;
	public static final int TD_DPA2_Phys_Service_Thr	= 0x7F03072B;
		
	/**TDScdmaHSUPARadio*/	
	public static final int TD_UPA1_HSUPA_Session		= 0x7F030503;
	public static final int TD_UPA1_EPUCH_TxPower		= 0x7F030901;
	public static final int TD_UPA1_ERUCCH_TxPower		= 0x7F030902;
	public static final int TD_UPA1_EHICH_RSCP			= 0x7F030905;
	public static final int TD_UPA1_EHICH_ISCP			= 0x7F030906;
	public static final int TD_UPA1_EHICH_BLER			= 0x7F030907;
	public static final int TD_UPA1_ERNTI				= 0x7F030924;
	public static final int TD_UPA1_Category			= 0x7F030504;
	public static final int TD_UPA1_RUCCH_ENI			= 0x7F030904;
	public static final int TD_UPA1_EDCH_TxPower		= 0x7F030903;
	public static final int TD_UPA1_EAGCH_RSCP			= 0x7F03090C;
	public static final int TD_UPA1_EAGCH_ISCP			= 0x7F03090D;
	public static final int TD_UPA1_EAGCH_BLER			= 0x7F03090E;

	/*WCDMAServingCellInfo*/
	public static final int W_Ser_MCC					= 0x7F020015;
	public static final int W_Ser_MNC					= 0x7F020016;
	public static final int W_Ser_UL_UARFCN				= 0x7F02002C;
	public static final int W_Ser_DL_UARFCN				= 0x7F020012;
	public static final int W_Ser_Max_PSC				= 0x7F020009;
	public static final int W_Ser_Max_RSCP				= 0x7F02000E;
	public static final int W_Ser_Max_EcIo				= 0x7F02000B;
	public static final int W_Ser_RxPower				= 0x7F020001;
	public static final int W_Ser_TxPower				= 0x7F020002;
	public static final int W_Ser_DL_AMR_Codec			= 0x7F1D1016;
	public static final int W_Ser_LAC					= 0x7F020019;
	public static final int W_Ser_Scr                   = 0x7F020414;//扰码
	/*CellID解码库给出28bit长码值，换算为7位16进制，前三位为RNCID,后4位为CellID短码*/	
	public static final int W_Ser_RNC_ID				= 0x7F020017;
	public static final int W_Ser_Cell_ID				= 0x7F020017;
	public static final int W_Ser_BLER					= 0x7F020005;
	public static final int W_Ser_Total_RSCP			= 0x7F02000D;
	public static final int W_Ser_Total_EcIo			= 0x7F02000A;
	public static final int W_Ser_SIR					= 0x7F020010;
	public static final int W_Ser_RRC_State				= 0x7F020024;
	public static final int W_Ser_UL_AMR_Codec			= 0x7F1D1015;
	public static final int W_Ser_Ref_PSC				= 0x7F020008;

	/*WCDMA小区集合结构体ID*/
	/**
	 * valueStr的值：0,	-10688,			103,		 -68.000000,		  -5.267518,		  -73.267517
	 * ActiveSetType:0  UmtsFreq:10688  UmtsPSC;103  UmtsRssi;-68.000000  UmtsEcIo;-5.267518  UmtsRSCP;-73.267517
	 * 其中ActiveSetType取值：ActiveSet 0，MonitorSet 1，NeighborSet 2，DetectedSet 3，VirtualActiveSet 4
	 */
	public static final int W_TUMTSCellInfoV2			= 0x7F020519;
	public static final int W_UmtsNeighborCell			= 0x7F020502;

	/*WCDMAPowerControl*/
	public static final int W_PC_UL_Power_Up			= 0x7F020035;
	public static final int W_PC_DL_Power_Up			= 0x7F020036;
	
	/*WCDMARACH*/
	public static final int W_RA_Message_Length			= 0x7F020859;
	public static final int W_RA_Preambles_Num			= 0x7F020858;
	public static final int W_RA_Last_Preamble_Signature= 0x7F020856;
	public static final int W_RA_RF_TX_Power			= 0x7F02085A;
	public static final int W_RA_AICH_Status			= 0x7F020852;
	public static final int W_RA_Access_Slot			= 0x7F020853;
	public static final int W_RA_SFN					= 0x7F020855;
	public static final int W_RA_AICH_Timing			= 0x7F020857;
	
	/*WCDMAPRACH*/
	public static final int W_PRA_Max_TxPower			= 0x7F02085B;
	public static final int W_PRA_Min_SF_for_RACH		= 0x7F02085E;
	public static final int W_PRA_SC_Index				= 0x7F02085F;
	public static final int W_PRA_UL_Punctuing_Limit	= 0x7F020860;
	public static final int W_PRA_Transport_Chan_ID		= 0x7F020861;
	public static final int W_PRA_PWR_Ramp_Step			= 0x7F020862;
	public static final int W_PRA_Max_Preamble_Trans	= 0x7F020863;
	public static final int W_PRA_UL_Interference		= 0x7F02001A;
	
	/*WCDMAHSDPA*/
	public static final int W_DPA1_Session				= 0x7F02001D;
	public static final int W_DPA1_CQI_Min				= 0x7F020306;
	public static final int W_DPA1_CQI_Mean				= 0x7F020305;
	public static final int W_DPA1_CQI_Max				= 0x7F020304;
	public static final int W_DPA1_QPSK_Rate			= 0x7F020302;
	public static final int W_DPA1_16QAM_Rate			= 0x7F020301;
	public static final int W_DPA1_64QAM_Rate			= 0x7F020319;
	public static final int W_DPA1_SCCH_DecodeSuccRate	= 0x7F02030A;
	public static final int W_DPA1_DSCH_ACK_Rate		= 0x7F02030B;
	public static final int W_DPA1_DSCH_DTX_Rate		= 0x7F02030C;
	public static final int W_DPA1_HARQ_ResponseRate	= 0x7F02030E;
	public static final int W_DPA1_HARQ_Process_Num		= 0x7F02031A;
	public static final int W_DPA1_DSCH_One_RTX_Rate	= 0x7F02031C;
	public static final int W_DPA1_CQI_Power_Offset		= 0x7F020316;
	public static final int W_DPA1_MIMO_Support			= 0x7F020318;
	public static final int W_DPA1_Class_Category		= 0x7F02001F;
	public static final int W_DPA1_H_RNTI				= 0x7F020313;
	public static final int W_DPA1_16QAM_Config			= 0x7F020317;
	public static final int W_DPA1_SCCH_Code_Num		= 0x7F02031D;
	public static final int W_DPA1_TransBolck_Size		= 0x7F020303;
	public static final int W_DPA1_DSCH_Error_Rate		= 0x7F020307;
	public static final int W_DPA1_DSCH_Error_Blocks	= 0x7F020308;
	public static final int W_DPA1_DSCH_Total_Code_Num	= 0x7F020321;
	public static final int W_DPA1_DSCH_Avg_Code_Num	= 0x7F020324;
	public static final int W_DPA1_DSCH_Schedule_Num	= 0x7F020323;
	public static final int W_DPA1_SCCH_Schedule_Num	= 0x7F020327;
	public static final int W_DPA1_SCCH_Schedule_Ratio	= 0x7F020328;
	public static final int W_DPA1_Phys_Request_Thr		= 0x7F02030F;
	public static final int W_DPA1_Phys_Schedule_Thr	= 0x7F020310;
	public static final int W_DPA1_Phys_Service_Thr		= 0x7F020311;
	public static final int W_DPA1_MAC_Thr				= 0x7F020312;
	
	/*WCDMAThrought*/
	public static final int W_Thr_DL_RLC_Thr			= 0x7F020201;
	public static final int W_Thr_UL_RLC_Thr			= 0x7F020202;
	public static final int W_Thr_RLC_Err_Rate			= 0x7F020205;
	public static final int W_Thr_DL_PDCP_Thr			= 0x7F020203;
	public static final int W_Thr_UL_PDCP_Thr			= 0x7F020204;
	public static final int W_Thr_RLC_RTX_Rate			= 0x7F020206;
	
	/*WCDMAHSUPA*/
	public static final int W_UPA1_Session				= 0x7F02001E;
	public static final int W_UPA1_Class_Category		= 0x7F020020;
	public static final int W_UPA1_HappyBit_Rate		= 0x7F020405;
	public static final int W_UPA1_ETFCI_LTDMP_Rate		= 0x7F02040D;
	public static final int W_UPA1_ETFCI_LTDSG_Rate		= 0x7F02040E;
	public static final int W_UPA1_ETFCI_LTDBo_Rate		= 0x7F02040F;
	public static final int W_UPA1_A_Set_Count			= 0x7F020413;
	public static final int W_UPA1_UE_Frame_Usage		= 0x7F020410;
	public static final int W_UPA1_TB_Size_Max			= 0x7F020411;
	public static final int W_UPA1_E_RNTI				= 0x7F020402;
	public static final int W_UPA1_E_DPCCH_Power		= 0x7F020412;
	public static final int W_UPA1_Serving_TTI			= 0x7F020401;
	public static final int W_UPA1_SGI_Average			= 0x7F020408;
	public static final int W_UPA1_ACK_Rate				= 0x7F020409;
	public static final int W_UPA1_NACK_Rate			= 0x7F02040A;
	public static final int W_UPA1_DTX_Rate				= 0x7F02040C;
	public static final int W_UPA1_One_RTX_Rate			= 0x7F020406;
	public static final int W_UPA1_Two_RTX_Rate			= 0x7F020407;
	public static final int W_UPA1_Phys_Service_Thr		= 0x7F020404;
	public static final int W_UPA1_MAC_Thr				= 0x7F020403;
	
	/*WCDMAHSPA+Sate*/
	public static final int W_PA1_HSPAPlus_Session		= 0x7F020021;
	public static final int W_PA1_Dual_Carrier_Session_	= 0x7F020023;
	public static final int W_PA1_HSPAPlus_Class_Category= 0x7F020022;
	
	/*WCDMAHSPA+DualCell*/
	public static final int W_DC_Dual_Serving_Cell_1	= 0x7F02062F;
	public static final int W_DC_Dual_Serving_UARFCN_1	= 0x7F020630;
	public static final int W_DC_TransBlockCount_1		= 0x7F02062B;
	public static final int W_DC_TransBlockSize_1		= 0x7F02062C;
	public static final int W_DC_CQI_Mean_1				= 0x7F020627;
	public static final int W_DC_DSCH_BLER_Re_Rate_1	= 0x7F020631;
	public static final int W_DC_DSCH_BLER_Fst_Rate_1	= 0x7F020633;
	public static final int W_DC_DSCH_ACK_Rate_1		= 0x7F020635;
	public static final int W_DC_DSCH_DTX_Rate_1		= 0x7F020637;
	public static final int W_DC_DSCH_Retrans_Rate_1	= 0x7F020639;
	public static final int W_DC_64QAM_Rate_1			= 0x7F02063A;
	public static final int W_DC_16QAM_Rate_1			= 0x7F02063B;
	public static final int W_DC_QPSK_Rate_1			= 0x7F02063C;
	public static final int W_DC_Decode_Success_Rate_1	= 0x7F020640;
	public static final int W_DC_Phys_Request_Thr_1		= 0x7F02063D;
	public static final int W_DC_Phys_Schedule_Thr_1	= 0x7F02063E;
	public static final int W_DC_Phys_Service_Thr_1		= 0x7F02063F;
	public static final int W_DC_Dual_Serving_Cell_2	= 0x7F020641;
	public static final int W_DC_Dual_Serving_UARFCN_2	= 0x7F020642;
	public static final int W_DC_TransBlockCount_2		= 0x7F02062D;
	public static final int W_DC_TransBlockSize_2		= 0x7F02062E;
	public static final int W_DC_CQI_Mean_2				= 0x7F02062A;
	public static final int W_DC_DSCH_BLER_Re_Rate_2	= 0x7F020643;
	public static final int W_DC_DSCH_BLER_Fst_Rate_2	= 0x7F020645;
	public static final int W_DC_DSCH_ACK_Rate_2		= 0x7F020647;
	public static final int W_DC_DSCH_DTX_Rate_2		= 0x7F020649;
	public static final int W_DC_DSCH_Retrans_Rate_2	= 0x7F02064B;
	public static final int W_DC_64QAM_Rate_2			= 0x7F02064C;
	public static final int W_DC_16QAM_Rate_2			= 0x7F02064D;
	public static final int W_DC_QPSK_Rate_2			= 0x7F02064E;
	public static final int W_DC_Decode_Success_Rate_2	= 0x7F020652;
	public static final int W_DC_Phys_Request_Thr_2		= 0x7F02064F;
	public static final int W_DC_Phys_Schedule_Thr_2	= 0x7F020650;
	public static final int W_DC_Phys_Service_Thr_2		= 0x7F020651;
	
	/*WCDMAHSPA+MIMO*/
	public static final int W_MM_RateMatchCode			= 0x7F020602;
	public static final int W_MM_TypeA_CQI_Rate			= 0x7F020603;
	public static final int W_MM_TypeB_CQI_Rate			= 0x7F020604;
	public static final int W_MM_Secondary_E_RNTI		= 0x7F020601;
	public static final int W_MM_TypeA_CQI_Mean			= 0x7F020607;
	public static final int W_MM_TypeB_CQI_Mean			= 0x7F02060A;
	public static final int W_MM_Pre_TB_Rate_1			= 0x7F02060B;
	public static final int W_MM_TransBlock_Size_Max_1	= 0x7F020617;
	public static final int W_MM_DSCH_BLER_Re_Rate_1	= 0x7F02060C;
	public static final int W_MM_DSCH_BLER_Fst_Rate_1	= 0x7F02060E;
	public static final int W_MM_DSCH_ACK_Rate_1		= 0x7F020610;
	public static final int W_MM_16QAM_Rate_1			= 0x7F020612;
	public static final int W_MM_64QAM_Rate_1			= 0x7F020613;
	public static final int W_MM_QPSK_Rate_1			= 0x7F020614;
	public static final int W_MM_Phys_Schedule_Thr_1	= 0x7F020615;
	public static final int W_MM_Phys_Service_Thr_1		= 0x7F020616;
	public static final int W_MM_Pre_TB_Rate_2			= 0x7F020618;
	public static final int W_MM_TransBlock_Size_Max_2	= 0x7F020624;
	public static final int W_MM_DSCH_BLER_Re_Rate_2	= 0x7F020619;
	public static final int W_MM_DSCH_BLER_Fst_Rate_2	= 0x7F02061B;
	public static final int W_MM_DSCH_ACK_Rate_2		= 0x7F02061D;
	public static final int W_MM_16QAM_Rate_2			= 0x7F02061F;
	public static final int W_MM_64QAM_Rate_2			= 0x7F020620;
	public static final int W_MM_QPSK_Rate_2			= 0x7F020621;
	public static final int W_MM_Phys_Schedule_Thr_2	= 0x7F020622;
	public static final int W_MM_Phys_Service_Thr_2		= 0x7F020623;
	
	//WCDMA AMR
	public static final int W_DL_AMR_NB_475k_Count		= 0x7F1D1001;
	public static final int W_DL_AMR_NB_515k_Count		= 0x7F1D1002;
	public static final int W_DL_AMR_NB_590k_Count		= 0x7F1D1003;
	public static final int W_DL_AMR_NB_670k_Count		= 0x7F1D1004;
	public static final int W_DL_AMR_NB_740k_Count		= 0x7F1D1005;
	public static final int W_DL_AMR_NB_795k_Count		= 0x7F1D1006;
	public static final int W_DL_AMR_NB_102k_Count		= 0x7F1D1007;
	public static final int W_DL_AMR_NB_122k_Count		= 0x7F1D1008;
	
	public static final int W_UL_AMR_NB_475k_Count		= 0x7F1D100B;
	public static final int W_UL_AMR_NB_515k_Count		= 0x7F1D100C;
	public static final int W_UL_AMR_NB_590k_Count		= 0x7F1D100D;
	public static final int W_UL_AMR_NB_670k_Count		= 0x7F1D100E;
	public static final int W_UL_AMR_NB_740k_Count		= 0x7F1D100F;
	public static final int W_UL_AMR_NB_795k_Count		= 0x7F1D1010;
	public static final int W_UL_AMR_NB_102k_Count		= 0x7F1D1011;
	public static final int W_UL_AMR_NB_122k_Count		= 0x7F1D1012;
	
	public static final int W_DL_AMR_WB_66k_Count		= 0x7F1D1020;
	public static final int W_DL_AMR_WB_885k_Count		= 0x7F1D1021;
	public static final int W_DL_AMR_WB_1265k_Count		= 0x7F1D1022;
	public static final int W_DL_AMR_WB_1425k_Count		= 0x7F1D1023;
	public static final int W_DL_AMR_WB_1585k_Count		= 0x7F1D1024;
	public static final int W_DL_AMR_WB_1825k_Count		= 0x7F1D1025;
	public static final int W_DL_AMR_WB_1985k_Count		= 0x7F1D1026;
	public static final int W_DL_AMR_WB_2325k_Count		= 0x7F1D1027;
	public static final int W_DL_AMR_WB_2385k_Count		= 0x7F1D1028;
	
	public static final int W_UL_AMR_WB_66k_Count		= 0x7F1D1017;
	public static final int W_UL_AMR_WB_885k_Count		= 0x7F1D1018;
	public static final int W_UL_AMR_WB_1265k_Count		= 0x7F1D1019;
	public static final int W_UL_AMR_WB_1425k_Count		= 0x7F1D101A;
	public static final int W_UL_AMR_WB_1585k_Count		= 0x7F1D101B;
	public static final int W_UL_AMR_WB_1825k_Count		= 0x7F1D101C;
	public static final int W_UL_AMR_WB_1985k_Count		= 0x7F1D101D;
	public static final int W_UL_AMR_WB_2325k_Count		= 0x7F1D101E;
	public static final int W_UL_AMR_WB_2385k_Count		= 0x7F1D101F;
	
	/*GSMServingCellInfo*/
	public static final int G_Ser_MCC					= 0x7F000301;
	public static final int G_Ser_MNC					= 0x7F000302;
	public static final int G_Ser_BCCH					= 0x7F000101;
	public static final int G_Ser_BSIC					= 0x7F000102;
	public static final int G_Ser_BCCHLev				= 0x7F000109;
	public static final int G_Ser_TxPower				= 0x7F000114;
	public static final int G_Ser_TA					= 0x7F000113;
	public static final int G_Ser_RxLevFull				= 0x7F000103;
	public static final int G_Ser_RxLevSub				= 0x7F000104;
	public static final int G_Ser_DTX					= 0x7F000117;
	public static final int G_Ser_LAC					= 0x7F000303;
	public static final int G_Ser_RAC					= 0x7F000304;
	public static final int G_Ser_Cell_ID				= 0x7F000305;
	public static final int G_Ser_V_Codec				= 0x7F000314;
	public static final int G_Ser_TS					= 0x7F00030A;
	public static final int G_Ser_RLT					= 0x7F000311;
	public static final int G_Ser_TCH_C2I				= 0x7F000112;
	public static final int G_Ser_RxQualFull			= 0x7F000105;
	public static final int G_Ser_RxQualSub				= 0x7F000106;
	public static final int G_Ser_State					= 0x7F000B02;
	public static final int G_Ser_FerFull				= 0x7F000107;
	public static final int G_Ser_FerSub				= 0x7F000108;
	public static final int G_Ser_C1				= 0x7F000115;
	public static final int G_Ser_C2				= 0x7F000116;
	
	/*GSMServingNeighborCell,可以直接从字段获取*/		
	//public static final int GSMNCellInfoData			= 0x7F007101；
	public static final int G_NCell_N1_RxLevel			= 0x7F000201;
	public static final int G_NCell_N2_RxLevel			= 0x7F000202;
	public static final int G_NCell_N3_RxLevel			= 0x7F000203;
	public static final int G_NCell_N4_RxLevel			= 0x7F000204;
	public static final int G_NCell_N5_RxLevel			= 0x7F000205;
	public static final int G_NCell_N6_RxLevel			= 0x7F000206;
	public static final int G_NCell_N1_BCCH				= 0x7F000207;
	public static final int G_NCell_N2_BCCH				= 0x7F000208;
	public static final int G_NCell_N3_BCCH				= 0x7F000209;
	public static final int G_NCell_N4_BCCH				= 0x7F00020A;
	public static final int G_NCell_N5_BCCH				= 0x7F00020B;
	public static final int G_NCell_N6_BCCH				= 0x7F00020C;
	public static final int G_NCell_N1_BSIC				= 0x7F00020D;
	public static final int G_NCell_N2_BSIC				= 0x7F00020E;
	public static final int G_NCell_N3_BSIC				= 0x7F00020F;
	public static final int G_NCell_N4_BSIC				= 0x7F000210;
	public static final int G_NCell_N5_BSIC				= 0x7F000211;
	public static final int G_NCell_N6_BSIC				= 0x7F000212;
	public static final int G_NCell_N1_C1				= 0x7F000213;
	public static final int G_NCell_N2_C1				= 0x7F000214;
	public static final int G_NCell_N3_C1				= 0x7F000215;
	public static final int G_NCell_N4_C1				= 0x7F000216;
	public static final int G_NCell_N5_C1				= 0x7F000217;
	public static final int G_NCell_N6_C1				= 0x7F000218;
	public static final int G_NCell_N1_C2				= 0x7F000219;
	public static final int G_NCell_N2_C2				= 0x7F00021A;
	public static final int G_NCell_N3_C2				= 0x7F00021B;
	public static final int G_NCell_N4_C2				= 0x7F00021C;
	public static final int G_NCell_N5_C2				= 0x7F00021D;
	public static final int G_NCell_N6_C2				= 0x7F00021E;
	public static final int G_NCell_N1_LAC				= 0x7F000232;
	public static final int G_NCell_N2_LAC				= 0x7F000233;
	public static final int G_NCell_N3_LAC				= 0x7F000234;
	public static final int G_NCell_N4_LAC				= 0x7F000235;
	public static final int G_NCell_N5_LAC				= 0x7F000236;
	public static final int G_NCell_N6_LAC				= 0x7F000237;
	public static final int G_NCell_N1_CI				= 0x7F000238;
	public static final int G_NCell_N2_CI				= 0x7F000239;
	public static final int G_NCell_N3_CI				= 0x7F00023A;
	public static final int G_NCell_N4_CI				= 0x7F00023B;
	public static final int G_NCell_N5_CI				= 0x7F00023C;
	public static final int G_NCell_N6_CI				= 0x7F00023D;

	/*GSMSystemParameter*/
	public static final int G_SYS_HSN					= 0x7F000308;
	public static final int G_SYS_CR_Hysteresis			= 0x7F00031A;
	public static final int G_SYS_CR_offset				= 0x7F00031B;
	public static final int G_SYS_TO					= 0x7F00031D;
	public static final int G_SYS_PT					= 0x7F00031C;
	public static final int G_SYS_Cell_Bar				= 0x7F000A06;
	public static final int G_SYS_BS_PA_MFRMS			= 0x7F000A04;
	public static final int G_SYS_MAIO					= 0x7F000309;
	public static final int G_SYS_T3212					= 0x7F000306;
	public static final int G_SYS_Max_Retransmitted		= 0x7F000A05;
	public static final int G_SYS_RX_Level_Access_Min	= 0x7F00031E;
	public static final int G_SYS_MS_TX_Power_Max_CCH	= 0x7F00031F;
	
	//system state
	public static final int G_GRR_State					= 0x7F00062C;
	public static final int G_GPRS_Support				= 0x7F000313;   //此处两个ID值一样，显示不一样
	public static final int G_EGPRS_Support				= 0x7F000313;
	public static final int G_Attach_State				= 0x7F000627;
	public static final int G_GMM_State					= 0x7F000608;
	public static final int G_NMO						= 0x7F00061E;
	public static final int G_Channel_Type				= 0x7F000307;
	public static final int G_Channel_Mode				= 0x7F00030B;
	public static final int G_RLC_Mode					= 0x7F000620;
	public static final int G_Service_State				= 0x7F1D2002;
	
	public static final int G_Dedicated_ARFCN			= 0x7F000101;
	public static final int G_Channels_Num				= 0x0A0050A5;
	public static final int G_CCCH_CONF					= 0x7F000A08;
	public static final int G_CCCH_Combined				= 0x7F000A02;
	public static final int G_Channel_Count				= 0x7F000A09;
	
	
	
	//public static final int G_SYS_Reestablish		= 
	public static final int G_SYS_ATT_Allowed			= 0x7F000A01;
	
	/*GPRSandEDGEInfo*/
	public static final int G_GPRS_DL_TBF_State			= 0x7F000609;
	public static final int G_GPRS_DL_TS_Num			= 0x7F000603;
	public static final int G_GPRS_DL_MCS				= 0x7F000617;
	public static final int G_GPRS_DL_CS				= 0x7F000601;
	public static final int G_GPRS_DL_RLC_Thr			= 0x7F000402;
	public static final int G_GPRS_DL_RLC_RTX			= 0x7F000404;
	public static final int G_GPRS_DL_LLC_Thr			= 0x7F000502;
	public static final int G_GPRS_DL_LLC_RTX			= 0x7F000504;
	public static final int G_GPRS_DL_TFI				= 0x7F00060B;
	public static final int G_GPRS_GMSK_CV_BEP			= 0x7F000618;
	public static final int G_GPRS_8PSK_CV_BEP			= 0x7F000619;
	public static final int G_GPRS_GPRS_BLER			= 0x7F000607;
	public static final int G_GPRS_RxQual				= 0x7F00060E;
	public static final int G_GPRS_UL_TBF_State			= 0x7F00060A;
	public static final int G_GPRS_UL_TS_Num			= 0x7F000604;
	public static final int G_GPRS_UL_MCS				= 0x7F000616;
	public static final int G_GPRS_UL_CS				= 0x7F000602;
	public static final int G_GPRS_UL_RLC_Thr			= 0x7F000401;
	public static final int G_GPRS_UL_RLC_RTX			= 0x7F000403;
	public static final int G_GPRS_UL_LLC_Thr			= 0x7F000501;
	public static final int G_GPRS_UL_LLC_RTX			= 0x7F000503;
	public static final int G_GPRS_UL_TFI				= 0x7F00060C;
	public static final int G_GPRS_GMSK_MEAN_BEP		= 0x7F00061A;
	public static final int G_GPRS_8PSK_MEAN_BEP		= 0x7F00061B;
	public static final int G_GPRS_CValue				= 0x7F000610;
	public static final int G_GPRS_SignVar				= 0x7F00060D;
	public static final int G_GPRS_SM_STATE				= 0x7F000C01;
	
	/*GPRSandEDGEInfo*/
	public static final int G_TS_DL_TS					= 0x7F000605;
	public static final int G_TS_UL_TS					= 0x7F000606;
	
	/**gsm结构体*/
	public static final int G_gsm_Struct				= 0x7F1D7101;
	
	/*LTEServingCellInfo*/
	public static final int L_SRV_MCC					= 0x7F060D14;
	public static final int L_SRV_MNC					= 0x7F060D15;
	public static final int L_SRV_UL_Freq				= 0x7F06001A;
	public static final int L_SRV_DL_Freq				= 0x7F060015;
	public static final int L_SRV_Work_Mode			= 0x7F06001B;
	public static final int L_SRV_EARFCN				= 0x7F060016;
	public static final int L_SRV_RSRP					= 0x7F06000E;
	public static final int L_SRV_RSRQ					= 0x7F06000F;
	public static final int L_SRV_SINR					= 0x7F060001;
	public static final int L_SRV_CRS_SINR				= 0x7F060036;
	public static final int L_SRV_DRS_SINR				= 0x7F060037;
	public static final int L_SRV_SRS_Power			= 0x7F060011;
	public static final int L_SRV_EMM_State			= 0x7F060D07;
	public static final int L_SRV_TM					= 0x7F06001F;
	public static final int L_SRV_DL_BandWidth			= 0x7F060012;
	public static final int L_SRV_Band					= 0x7F06001C;
	public static final int L_SRV_PCI					= 0x7F060002;
	public static final int L_SRV_RSSI					= 0x7F060010;
	public static final int L_SRV_Pathloss				= 0x7F060305;
	public static final int L_SRV_SRS_RB_Num			= 0x7F060047;
	public static final int L_SRV_TAC					= 0x7F060018;
	public static final int L_SRV_ECGI					= 0x7F06002A;
	public static final int L_SRV_eNodeBID				= 0x0A0050A2;
	public static final int L_SRV_CellID				= 0x0A0050A4;
	public static final int L_SRV_ECIP1					= 0x7F06002A;
	public static final int L_SRV_ECIP2					= 0x0A0050A2;
	public static final int L_SRV_ECIP3					= 0x0A0050A3;
	public static final int L_SRV_RRC_State			= 0x7F060D10;
	public static final int L_SRV_EMM_Substate			= 0x7F060D08;


	public static final int NB_SRV_RSRP					= 0x7F06000E;
	public static final int NB_SRV_SINR					= 0x7F060001;
	public static final int NB_SRV_TECHNOLOGY			= 0x7F1D2008;
	public static final int NB_SRV_EARFCN				= 0x7F060016;
	public static final int NB_SRV_PCI					= 0x7F060002;
	public static final int NB_IMSI					= 0x0A005400;


	public static final int NR_SS_RSRP=0x7F070003;
	public static final int NR_PCI=0x7F070000;
	public static final int NR_SS_SINR=0x7F070006;
	public static final int NR_Band=0x7F07000A;
	public static final int NR_PointA_ARFCN=0x7F070009;
	public static final int NR_SSB_ARFCN=0x7F070008;
	/**LTENeighborCellList结构体ID,主服务小区包含在内,为首行小区，显示时注意区分主服务和邻区
	 * valueStr的值：0,	-10688,			103,		 -68.000000,		  -5.267518,		  -73.267517
	 *					 0: Earfcn;		1:Pci;		2:Rsrp(/100);		3:Rsrq(/100);		4:Rssi(/100)
	 */
	public static final int LTE_CELL_LIST				= 0x7F060E38;
	public static final int LTE_WCDMA_CELL_LIST		= 0x7F060E90;
	public static final int LTE_GSM_CELL_LIST			= 0x7F060E91;
	public static final int ENDC_CELL_LIST			= 0x7F07E002;
	/*LTEModulation结构体ID*/
	public static final int LTE_UL_MCS_Statistic		= 0x7F060E09;
	public static final int LTE_DL_MCS_Statistic_Code0	= 0x7F060E0A;
	public static final int LTE_DL_MCS_Statistic_Code1	= 0x7F060E0B;
	public static final int LTE_DL_PDSCH_BLER_Code0		= 0x7F06004A;
	public static final int LTE_DL_PDSCH_BLER_Code1		= 0x7F06004B;
	
	/*LTE Modulation*/
	public static final int LTE_QPSK_Ratio_UL			= 0x7F06080E;
	public static final int LTE_QPSK_Ratio_code0_DL		= 0x7F060814;
	public static final int LTE_QPSK_Ratio_code1_DL		= 0x7F06081A;
	public static final int LTE_16QAM_Ratio_UL			= 0x7F060810;
	public static final int LTE_16QAM_Ratio_code0_DL	= 0x7F060816;
	public static final int	LTE_16QAM_Ratio_code1_DL	= 0x7F06081C;
	public static final int LTE_64QAM_Ratio_UL 			= 0x7F060812;
	public static final int LTE_64QAM_Ratio_code0_DL 	= 0x7F060818;
	public static final int LTE_64QAM_Ratio_code1_DL	= 0x7F06081E;
	
	//LTE 速率
	public static final int L_Thr_UL_PDCP_Thr			= 0x7F060B03;
	public static final int L_Thr_UL_RLC_Thr			= 0x7F060B07;
	public static final int L_Thr_UL_MAC_Thr			= 0x7F060B01;
	public static final int L_Thr_UL_Phy_Thr			= 0x7F060B05;
	public static final int L_Thr_DL_PDCP_Thr			= 0x7F060B02;
	public static final int L_Thr_DL_RLC_Thr			= 0x7F060B06;
	public static final int L_Thr_DL_MAC_Thr			= 0x7F060B00;
	public static final int L_Thr_DL_Phy_Thr			= 0x7F060B04;

	//NR 速率
	public static final int NR_Thr_UL_PDCP_Thr			= 0xA00541D;
	public static final int NR_Thr_UL_RLC_Thr			= 0xA00541C;
	public static final int NR_Thr_UL_MAC_Thr			= 0xA00541B;
	public static final int NR_Thr_UL_Phy_Thr			= 0xA00541A;
	public static final int NR_Thr_DL_PDCP_Thr			= 0xA005419;
	public static final int NR_Thr_DL_RLC_Thr			= 0xA005418;
	public static final int NR_Thr_DL_MAC_Thr			= 0xA005417;
	public static final int NR_Thr_DL_Phy_Thr			= 0xA005416;

	public static final int L_Thr_DL_PhyThrCode0		= 0x7F060b08;
	public static final int L_Thr_DL_PhyThrCode1		= 0x7F060b09;
	
	/* APP Thr*/
	public static final int L_Thr_DL_FTP				= 0x0A002151;
	public static final int L_Thr_DL_HTTP				= 0x0A002310;
	public static final int L_Thr_DL_EMAIL				= 0x0A002330;
	public static final int L_Thr_DL_HTTPPAGE			= 0x0A002321;
	public static final int L_Thr_DL_VIDEOPLA			= 0x0A002647;
	public static final int L_Thr_UL_FTP				= 0x0A002153;
	public static final int L_Thr_UL_HTTP				= 0x0A0024A0;
	public static final int L_Thr_UL_EMAIL				= 0x0A002331;

	/**UDP业务测试下行速率**/
	public static final int L_THR_DL_DDP                = 0x07F060B0A;
	/**UDP业务测试上行速率**/
	public static final int L_THR_DL_UDP                = 0x07F060B0B;

	/*LTEChannelMeasurement3*/
	public static final int L_CH1_PUSCH_Fi				= 0x7F060008;
	public static final int L_CH1_PUSCH_P0				= 0x7F060048;
	public static final int L_CH1_PUSCH_Alpha			= 0x7F060049;
	public static final int L_CH1_PUSCH_TB_Size			= 0x7F06003F;
	public static final int L_CH1_PUSCH_ACK				= 0x7F060041;
	public static final int L_CH1_PUSCH_NACK			= 0x7F060042;
	public static final int L_CH1_PUSCH_TxPower			= 0x7F060009;
	public static final int L_CH1_PUSCH_Pathloss		= 0x7F060070;
	public static final int L_CH1_PUSCH_RB_Num			= 0x7F060040;
	public static final int L_CH1_PDSCH_TB_Size_Code0	= 0x7F06003C;
	public static final int L_CH1_PDSCH_TB_Size_Code1	= 0x7F06003D;
	public static final int L_CH1_PDSCH_SINR			= 0x7F060035;
	public static final int L_CH1_PDSCH_Pathloss		= 0x7F06006F;
	public static final int L_CH1_PDSCH_RB_Num			= 0x7F06003E;
	public static final int L_CH1_PDCCH_BLER			= 0x7F060068;
	public static final int L_CH1_PUCCH_Gi				= 0x7F060006;
	public static final int L_CH1_PUCCH_TxPower			= 0x7F060007;
	public static final int L_CH1_PUCCH_Pathloss		= 0x7F06006A;
	public static final int L_CH1_PUCCH_ACK				= 0x7F060043;
	public static final int L_CH1_PUCCH_NACK			= 0x7F060044;
	//public static final int L_CH1_PDCCH_Format		= 0x7F060E10（结构体）;
	public static final int L_CH1_PDCCH_CCE_Start		= 0x7F060030;
	public static final int L_CH1_PDCCH_CCEs_Num		= 0x7F060031;
	//public static final int L_CH1_PDCCH_REGs_Num		= 0x7F060E10（结构体）;
	//public static final int L_CH1_PDCCH_DCI_Format	= 0x7F060E10（结构体）;
	public static final int L_CH1_PDCCH_DL_Grant_Count	= 0x7F060003;
	public static final int L_CH1_PDCCH_UL_Grant_Count	= 0x7F060004;
	public static final int L_CH1_PDCCH_SINR			= 0x7F060034;
	public static final int L_CH1_PDCCH_Pathloss		= 0x7F060071;
	public static final int L_UL_PUSCH_RB_Count			= 0x7F060206;		
	public static final int L_DL_PDSCH_RB_Count			= 0x7F060207;
	
	/*LTEChannelMeasurement4*/
	public static final int L_CH2_PUSCH_Initial_BLER	= 0x7F06070A;
	public static final int L_CH2_PUSCH_Residual_BLER	= 0x7F06070B;
	public static final int L_CH2_Total_BLER			= 0x7F060069;
	public static final int L_CH2_P_SCH_Power			= 0x7F060026;
	public static final int L_CH2_Wideband_CQI			= 0x7F060506;
	public static final int L_CH2_Wideband_CQI_for_CW0	= 0x7F060507;
	public static final int L_CH2_CQI_Report_Mode		= 0x7F060505;
	public static final int L_CH2_PDSCH_BLER			= 0x7F06006E;
	public static final int L_CH2_PDCCH_Estimated_BLER	= 0x7F060077;
	public static final int L_CH2_S_SCH_Power			= 0x7F060027;
	public static final int L_CH2_Wideband_CQI_for_CW1	= 0x7F060508;
	public static final int L_CH2_Periodicity			= 0x7F060501;
	public static final int L_CH2_Frame_Number          = 0x7F06006B;
	
	/*LTESystemParameter*/
	public static final int L_SYS_UE_Category					= 0x7F060D12;
	public static final int L_SYS_Q_Offset_Cell				= 0x7F060D30;
	public static final int L_SYS_Q_Offset_Freq				= 0x7F060D33;
	public static final int L_SYS_Q_Rxlevmin					= 0x7F060D35;
	public static final int L_SYS_Q_RxlevminOffset			= 0x7F060D3A;
	public static final int L_SYS_IntraFreq_Reselection		= 0x7F060D37;
	public static final int L_SYS_Allowed_Meas_Bandwidth		= 0x7F060D3D;
	public static final int L_SYS_ThresholdX_High				= 0x7F060D3B;
	public static final int L_SYS_ThresholdX_Low				= 0x7F060D3C;
	public static final int L_SYS_Threshold_Serving_Low		= 0x7F060D38;
	public static final int L_SYS_Treselection_EUTRA			= 0x7F060D39;
	public static final int L_SYS_Cell_Reselect_Priority		= 0x7F060D31;
	public static final int L_SYS_Q_Hyst						= 0x7F060D36;
	public static final int L_SYS_E_UTRA_Carrier_Freq			= 0x7F060D32;
	public static final int L_SYS_GERAN_Carrier_Freq			= 0x7F060D34;
	public static final int L_SYS_Roaming_State				= 0x7F060021;
	public static final int L_SYS_Max_UE_TxPower				= 0x7F060D50;
	public static final int L_SYS_DRX_State					= 0x7F060D06;
	public static final int L_SYS_NAS_State					= 0x7F060020;
	public static final int L_SYS_Power_Headroom				= 0x7F060022;
	public static final int L_SYS_Uu_TA							= 0x7F060046;
	public static final int L_SYS_MMEC							= 0x7F060D0C;
	public static final int L_SYS_MMEGI							= 0x7F060D0D;
	public static final int L_SYS_IMSI							= 0x7F060D16;
	public static final int L_SYS_M_TMSI						= 0x7F060D0E;
	public static final int L_SYS_C_RNTI						= 0x7F060039;
	public static final int L_SYS_T_CRNTI						= 0x7F060D52;
	public static final int L_SYS_SPS_CRNTI					= 0x7F060D53;
	public static final int L_SYS_TPC_PUSCH_RNTI				= 0x7F060D54;
	public static final int	L_SYS_QCI							= 0x7F060D18;
	
	public static final int	L_SYS_Rx0RSRP						= 0x7F06001D;
	public static final int	L_SYS_Rx1RSRP						= 0x7F06001E;
	public static final int	L_MIMO_Mode						= 0x7F06090F;
	public static final int	L_RANK1_SINR						= 0x7F06000B;
	public static final int	L_RANK2_SINR1						= 0x7F06000C;
	public static final int	L_RANK2_SINR2						= 0x7F06000D;
	public static final int	L_FreqInfo							= 0x7F060015;
	public static final int	L_SubFrame_AssignmentType		= 0x7F060014;
	public static final int	L_Special_SubFramePatterns		= 0x7F060013;
	public static final int	L_UL_RB_Num						= 0x7F060206;
	public static final int	L_DL_RB_Num						= 0x7F060207;
	public static final int	L_eNodeB_ID						= 0x0A0050A2;
	public static final int	L_EARFCN_UL						= 0x7F06002C;
	
	public static final int	L_RACH_Type						= 0x7F060097;
	public static final int	L_RACH_Result						= 0x7F06009F;
	public static final int	L_Max_Preamble_Number			= 0x7F060084;
	public static final int	L_CP_Type							= 0x7F060028;
	public static final int	L_PDSCH_serving_RB_count_slot	= 0x0A0050AA; 
	public static final int	L_PDSCH_Schedule_RB_count_slot	= 0x0A0050AC;
	public static final int	L_PUSCH_serving_RB_count_slot	= 0x0A0050A6; 
	public static final int	L_PUSCH_Schedule_RB_count_slot	= 0x0A0050A8;
	public static final int	L_PDSCH_RB_utilization			= 0x7FFF100D;
	public static final int	L_PUSCH_RB_utilization			= 0x7FFF100E;
	
	/*LTE 结构信息*/
	public static final int LTE_EPS_BearerContext_02C1		= 0x7F060E0D;
	public static final int LTE_APN								= 0x7f060E14;
	public static final int LTE_UL_Schedul						= 0x7f060e03;
	public static final int LTE_DL_Schedul						= 0x7F060E04;
	public static final int LTE_DCI_FORMAT						= 0x7F060E02;
	/**
	 * NB 特殊结构体信息
	 */
	public static final int LTE_NB_FORMAT						= 0x7F1D7004;


	/*QCI Config*/
	public static final int	L_QCI_1_Config_ul = 0x7F060DA9;
	public static final int	L_QCI_2_Config_ul = 0x7F060DAA;
	public static final int	L_QCI_3_Config_ul = 0x7F060DAB;
	public static final int	L_QCI_4_Config_ul = 0x7F060DAC;
	public static final int	L_QCI_5_Config_ul = 0x7F060DAD;
	public static final int	L_QCI_6_Config_ul = 0x7F060DAE;
	public static final int	L_QCI_7_Config_ul = 0x7F060DAF;
	public static final int	L_QCI_8_Config_ul = 0x7F060DB0;
	public static final int	L_QCI_9_Config_ul = 0x7F060DB1;
	public static final int	L_QCI_1_Config_dl = 0x7F060DA0;
	public static final int	L_QCI_2_Config_dl = 0x7F060DA1;
	public static final int	L_QCI_3_Config_dl = 0x7F060DA2;
	public static final int	L_QCI_4_Config_dl = 0x7F060DA3;
	public static final int	L_QCI_5_Config_dl = 0x7F060DA4;
	public static final int	L_QCI_6_Config_dl = 0x7F060DA5;
	public static final int	L_QCI_7_Config_dl = 0x7F060DA6;
	public static final int	L_QCI_8_Config_dl = 0x7F060DA7;
	public static final int	L_QCI_9_Config_dl = 0x7F060DA8;

	/*CA LTE Wideband CQI */
	public static final int	CA_Wideband_CQI_code0_pc  = 0x7F060507;
	public static final int	CA_Wideband_CQI_code0_pc1 = 0x7F0613B1;
	public static final int	CA_Wideband_CQI_code0_pc2 = 0x7F0613B2;
	public static final int	CA_Wideband_CQI_code0_pc3 = 0x7F0613B3;
	public static final int	CA_Wideband_CQI_code0_pc4 = 0x7F0613B4;
	public static final int	CA_Wideband_CQI_code1_pc  = 0x7F060508;
	public static final int	CA_Wideband_CQI_code1_pc1 = 0x7F0613B9;
	public static final int	CA_Wideband_CQI_code1_pc2 = 0x7F0613BA;
	public static final int	CA_Wideband_CQI_code1_pc3 = 0x7F0613BB;
	public static final int	CA_Wideband_CQI_code1_pc4 = 0x7F0613BC;

	
	/*WLAN参数*/
	public static final int WLAN_RSSI					= 0x7F04A501;
	/*FTP相关参数*/
	public static final int QOS_FTP_DlThr				= 0x0A004001;
	public static final int QOS_FTP_UlThr				= 0x0A004002;
	
	//集合集数据
	public static final int  Set_Rssi 					= 0x0A005500;		//缩放比例：1000	RSSI：1X、WCDMA、LTE共用
	public static final int  Set_EcIo 					= 0x0A005501;		//缩放比例：1000	EcIo：1X、WCDMA共用
	public static final int  Set_Rscp 					= 0x0A005502;		//缩放比例：1000	RSCP：1X、WCDMA共用
	public static final int  Set_SetType 				= 0x0A005503;		//缩放比例：   1	SetType集合类型，参考Enum ActiveType定义；所有网共用
	public static final int  Set_Rsrp 					= 0x0A005504;		//缩放比例： 100	RSRP：LTE
	public static final int  Set_Rsrq 					= 0x0A005505;		//缩放比例： 100	RSRQ：LTE
	public static final int  Set_Frequency 				= 0x0A005506;		//缩放比例：   1	Frequency/UARFCN/EARFCN：所有网共用
	public static final int  Set_PN 						= 0x0A005507;		//缩放比例：   1	PN /PSC /PCI：所有网共用

	//以下为EVDO特有
	public static final int  Set_EVRssi 				= 0x0A005510;		//缩放比例：1000	EVDO RSSI
	public static final int  Set_EVC2I 					= 0x0A005511;		//缩放比例：1000	EVDO TotalC/I
	public static final int  Set_EVEcio 				= 0x0A005512;		//缩放比例：1000	EVDO EcIo
	public static final int  Set_EVLinkID 				= 0x0A005513;		//缩放比例：   1	EVDO LinkID
	public static final int  Set_EVPilotGroupID 		= 0x0A005514;		//缩放比例：   1	EVDO PilotGroupID
	public static final int  Set_EVSchedTag 			= 0x0A005515;		//缩放比例：   1	EVDO SchedTag
	public static final int  Set_EVDRCCover 			= 0x0A005516;		//缩放比例：   1	EVDO DRC Cover
	
	public static final int  Disconnect					= 0x713A0325;		//Disconnect
	public static final int  Disconnect_Reasion		= 0x7F1D0004;		//Disconnect失败原因
	
	//VoLTE参数
	public static final int VOLTE_Invite_Request_Cause						= 0x7F1D401F;	//1:audio;2:video + audio
	public static final int VOLTE_CM_Call_ID									= 0x7F1D4002;		//SIP会话的ID
	public static final int VOLTE_SIP_Codec_Type_in_Summary					= 0x7F1D400F;		
	public static final int VOLTE_Max_Frame_Delay_in_Summary					= 0x7F1D400D;		
	public static final int VOLTE_Avg_Frame_Delay_in_Summary					= 0x7F1D400E;
	public static final int VOLTE_Max_RFC1889_Jitter_in_Summary				= 0x7F1D400B;
	public static final int VOLTE_Avg_RFC1889_Jitter_Avg_in_Summary			= 0x7F1D400C;	
	public static final int VOLTE_RFC1889_Jitter_Inst							= 0x7F1D4005;
	public static final int VOLTE_Packet_Loss_Rate_in_Summary				= 0x7F1D400A;	
	public static final int VOLTE_Packet_Loss_Rate_Inst						= 0x7F1D4003;
	public static final int VOLTE_RTP_Packet_Delay_Max						= 0x7F1D4006;
	public static final int VOLTE_RTP_Packet_Delay_Avg						= 0x7F1D4007;
	public static final int VOLTE_Tx_RTP_Packet_Num_in_Summary				= 0x7F1D4008;
	public static final int VOLTE_Rx_RTP_Packet_Num_in_Summary				= 0x7F1D4009;
	
	//**********************LTE_CA*********************//
	public static final int LTECA_Capacity_CACarrierCount            		= 0x7F060F00;  
	public static final int LTECA_Capacity_EMMSubstate               			= 0x7F060D08;  
	public static final int LTECA_Capacity_LTEUCategory              			= 0x7F060D12;  
	public static final int LTECA_Capacity_Network_State             			= 0x7F060D10;
	public static final int LTECA_Active_SCell_Count							= 0x0A001026;
	public static final int LTECA_UL_Active_SCell_Count						= 0x0A001029;
	
	public static final int LTECA_WorkMode_PCell                              =0x7F06001B;
	public static final int LTECA_WorkMode_SCell1                             =0x7F060F71;
	public static final int LTECA_WorkMode_SCell2                             =0x7F060F72;
	                                                                                      
	public static final int LTECA_TAC_PCell                                   =0x7F060018;
	public static final int LTECA_TAC_SCell1                                  =0x7F060FB9;
	public static final int LTECA_TAC_SCell2                                  =0x7F060FBA;
	                                                                                      
	public static final int LTECA_CellID_PCell                                =0x0A0050A2;
	public static final int LTECA_CellID_SCell1                               =0x7F061011;
	public static final int LTECA_CellID_SCell2                               =0x7F061012;
	                                                                                      
	public static final int LTECA_Band_PCell                                  =0x7F06001C;
	public static final int LTECA_Band_SCell1                                 =0x7F060F81;
	public static final int LTECA_Band_SCell2                                 =0x7F060F82;
	                                                                                      
	public static final int LTECA_DLEARFCN_PCell                              =0x7F060016;
	public static final int LTECA_DLEARFCN_SCell1                             =0x7F060F51;
	public static final int LTECA_DLEARFCN_SCell2                             =0x7F060F52;
	                                                                                      
	public static final int LTECA_ULEARFCN_PCell                              =0x7F06002C;
	public static final int LTECA_ULEARFCN_SCell1                             =0x7F060F61;
	public static final int LTECA_ULEARFCN_SCell2                             =0x7F060F62;
	                                                                                      
	public static final int LTECA_PCI_PCell                                   =0x7F060002;
	public static final int LTECA_PCI_SCell1                                  =0x7F060F01;
	public static final int LTECA_PCI_SCell2                                  =0x7F060F02;
	                                                                                      
	public static final int LTECA_BandWidth_PCell                             =0x7F060012;
	public static final int LTECA_BandWidth_SCell1                            =0x7F060F11;
	public static final int LTECA_BandWidth_SCell2                            =0x7F060F12;
	                                                                                      
	                                                                                      
	public static final int LTECA_TM_PCell                                    =0x7F06001F;
	public static final int LTECA_TM_SCell1                                   =0x7F061001;
	public static final int LTECA_TM_SCell2                                   =0x7F061002;
	                                                                                      
	public static final int LTECA_Freq_PCell                                  =0x7F060015;
	public static final int LTECA_Freq_SCell1                                 =0x7F060F21;
	public static final int LTECA_Freq_SCell2                                 =0x7F060F22;
	                                                                                      
	public static final int LTECA_CodeWordNum_PCell                           =0x7F06002B;
	public static final int LTECA_CodeWordNum_SCell1                          =0x7F061019;
	public static final int LTECA_CodeWordNum_SCell2                          =0x7F06101A;
	
	public static final int LTECA_RSSI_PCell                                   =0x7F060010;   
	public static final int LTECA_RSSI_SCell1                                  =0x7F060F89;   
	public static final int LTECA_RSSI_SCell2                                  =0x7F060F8A;   
	                                                                                          
	public static final int LTECA_RSSIRx0_PCell                                =0x7F060093;   
	public static final int LTECA_RSSIRx0_SCell1                               =0x7F060FE1;   
	public static final int LTECA_RSSIRx0_SCell2                               =0x7F060FE2;   
	                                                                                          
	public static final int LTECA_RSSIRx1_PCell                                =0x7F060094;   
	public static final int LTECA_RSSIRx1_SCell1                               =0x7F060FE9;   
	public static final int LTECA_RSSIRx1_SCell2                               =0x7F060FEA;   
	                                                                                          
	public static final int LTECA_RSRP_PCell                                   =0x7F06000E;   
	public static final int LTECA_RSRP_SCell1                                  =0x7F060F69;   
	public static final int LTECA_RSRP_SCell2                                  =0x7F060F6A;   
	                                                                                          
	public static final int LTECA_RSRPRx0_PCell                                =0x7F06001D;   
	public static final int LTECA_RSRPRx0_SCell1                               =0x7F060FF1;   
	public static final int LTECA_RSRPRx0_SCell2                               =0x7F060FF2;   
	                                                                                          
	public static final int LTECA_RSRPRx1_PCell                                =0x7F06001E;   
	public static final int LTECA_RSRPRx1_SCell1                               =0x7F060FF9;   
	public static final int LTECA_RSRPRx1_SCell2                               =0x7F060FFA;   
	                                                                                          
	public static final int LTECA_CRSRP_PCell                                  =0x7F060061;   
	public static final int LTECA_CRSRP_SCell1                                 =0x7F061061;   
	public static final int LTECA_CRSRP_SCell2                                 =0x7F061062;   
	                                                                                          
	public static final int LTECA_DRSRP_PCell                                  =0x7F060062;   
	public static final int LTECA_DRSRP_SCell1                                 =0x7F061069;   
	public static final int LTECA_DRSRP_SCell2                                 =0x7F06106A;   
	                                                                                          
	public static final int LTECA_SINR_PCell                                   =0x7F060001;   
	public static final int LTECA_SINR_SCell1                                  =0x7F060F09;   
	public static final int LTECA_SINR_SCell2                                  =0x7F060F0A;   
	                                                                                          
	public static final int LTECA_SINRRx0_PCell                                =0x7F06008F;   
	public static final int LTECA_SINRRx0_SCell1                               =0x7F060FC1;   
	public static final int LTECA_SINRRx0_SCell2                               =0x7F060FC2;   
	                                                                                          
	public static final int LTECA_SINRRx1_PCell                                =0x7F060090;   
	public static final int LTECA_SINRRx1_SCell1                               =0x7F060FC9;   
	public static final int LTECA_SINRRx1_SCell2                               =0x7F060FCA;   
	                                                                                          
	public static final int LTECA_CRSSINR_PCell                                =0x7F060036;   
	public static final int LTECA_CRSSINR_SCell1                               =0x7F061031;   
	public static final int LTECA_CRSSINR_SCell2                               =0x7F061032;   
	                                                                                          
	public static final int LTECA_DRSSINR_PCell                                =0x7F060037;   
	public static final int LTECA_DRSSINR_SCell1                               =0x7F061039;   
	public static final int LTECA_DRSSINR_SCell2                               =0x7F06103A;   
	                                                                                          
	public static final int LTECA_RSRQ_PCell                                   =0x7F06000F;   
	public static final int LTECA_RSRQ_SCell1                                  =0x7F060F79;   
	public static final int LTECA_RSRQ_SCell2                                  =0x7F060F7A;   
	                                                                                          
	public static final int LTECA_RSRQRx0_PCell                                =0x7F060091;   
	public static final int LTECA_RSRQRx0_SCell1                               =0x7F060FD1;   
	public static final int LTECA_RSRQRx0_SCell2                               =0x7F060FD2;   
	                                                                                          
	public static final int LTECA_RSRQRx1_PCell                                =0x7F060092;   
	public static final int LTECA_RSRQRx1_SCell1                               =0x7F060FD9;   
	public static final int LTECA_RSRQRx1_SCell2                               =0x7F060FDA;  
	
	public static final int LTECA_UL_BandWidth                                 =0x7F060019;   
	public static final int LTECA_UL_BandWidth_SCell1                         =0x7F060F31;   
	public static final int LTECA_UL_BandWidth_SCell2                         =0x7F060F32;
	
	public static final int LTECA_UL_Freq_SCell1                          		=0x7F060F41;   
	public static final int LTECA_UL_Freq_SCell2                          		=0x7F060F42;
	
	
	
	
	/***************超级华丽分隔线******************/
	 public static final int LTECA_MACThrDL_Total                                         =0x0A007064;
	 public static final int LTECA_MACThrDL_PCell                                         =0x7F060FA0;
	 public static final int LTECA_MACThrDL_SCell1                                        =0x7F060FA1;
	 public static final int LTECA_MACThrDL_SCell2                                        =0x7F060FA2;
	                                                                                                  
	 public static final int LTECA_PhyThrDL_Total                                         =0x0A007065;
	 public static final int LTECA_PhyThrDL_PCell                                         =0x7F060FB0;
	 public static final int LTECA_PhyThrDL_SCell1                                        =0x7F060FB1;
	 public static final int LTECA_PhyThrDL_SCell2                                        =0x7F060FB2;
	                                                                                                  
	 public static final int LTECA_RBCount_Total                                         	=0x0A007012;
	 public static final int LTECA_RBCount_PCell                                          =0x7F060207;
	 public static final int LTECA_RBCount_SCell1                                         =0x7F0610F9;
	 public static final int LTECA_RBCount_SCell2                                         =0x7F0610FA;
	                                                                                                  
	 public static final int LTECA_RBCountSl_Total                                        =0x0A007010;
	 public static final int LTECA_RBCountSl_PCell                                        =0x0A0050AA;
	 public static final int LTECA_RBCountSl_SCell1                                       =0x7F0610E1;
	 public static final int LTECA_RBCountSl_SCell2                                       =0x7F0610E2;
	                                                                                                  
	 public static final int LTECA_SlotCount_Total                                        =0x0A007015;
	 public static final int LTECA_SlotCount_PCell                                        =0x7F06020D;
	 public static final int LTECA_SlotCount_SCell1                                       =0x7F061111;
	 public static final int LTECA_SlotCount_SCell2                                       =0x7F061112;
	                                                                                                  
	                                                                                                  
	 public static final int LTECA_SubFNCount_Total                                       =0x0A007016;
	 public static final int LTECA_SubFNCount_PCell                                       =0x7F06020F;
	 public static final int LTECA_SubFNCount_SCell1                                      =0x7F061119;
	 public static final int LTECA_SubFNCount_SCell2                                      =0x7F06111A;
	                                                                                                  
	                                                                                                  
	 public static final int LTECA_ScheduleRate_Total                                     =0x0A007047;
	 public static final int LTECA_ScheduleRate_PCell                                     =0x7F060753;
	 public static final int LTECA_ScheduleRate_SCell1                                    =0x7F0612B9;
	 public static final int LTECA_ScheduleRate_SCell2                                    =0x7F0612BA;
	                                                                                                  
	 public static final int LTECA_TBSizeCode0_Total                                      =0x0A007000;
	 public static final int LTECA_TBSizeCode0_PCell                                      =0x7F06003C;
	 public static final int LTECA_TBSizeCode0_SCell1                                     =0x7F061049;
	 public static final int LTECA_TBSizeCode0_SCell2                                     =0x7F06104A;
	                                                                                                  
	 public static final int LTECA_TBSizeCode1_Total                                      =0x0A007001;
	 public static final int LTECA_TBSizeCode1_PCell                                      =0x7F06003D;
	 public static final int LTECA_TBSizeCode1_SCell1                                     =0x7F061051;
	 public static final int LTECA_TBSizeCode1_SCell2                                     =0x7F061052;
	                                                                                                  
	                                                                                                  
	 public static final int LTECA_PDCCHDLGrant_Total                                             =0;
	 public static final int LTECA_PDCCHDLGrant_PCell                                     =0x7F060003;
	 public static final int LTECA_PDCCHDLGrant_SCell1                                    =0x7F060F19;
	 public static final int LTECA_PDCCHDLGrant_SCell2                                    =0x7F060F1A;
	                                                                                                  
	 public static final int LTECA_PDCCHULGrant_Total                                              =0;
	 public static final int LTECA_PDCCHULGrant_PCell                                     =0x7F060004;
	 public static final int LTECA_PDCCHULGrant_SCell1                                    =0x7F060F29;
	 public static final int LTECA_PDCCHULGrant_SCell2                                    =0x7F060F2A;
	 
	 /*************真心受不了的华丽******************/
	 public static final int LTECA_BLER_Total                                                =0x0A007008;
	 public static final int LTECA_BLER_PCell                                                =0x7F06006E;
	 public static final int LTECA_BLER_SCell1                                               =0x7F0610A1;
	 public static final int LTECA_BLER_SCell2                                               =0x7F0610A2;
	                                                                                                     
	 public static final int LTECA_BLERCode0_Total                                         =0x0A007006;
	 public static final int LTECA_BLERCode0__PCell                                        =0x7F06004A;
	 public static final int LTECA_BLERCode0__SCell1                                       =0x7F061091;
	 public static final int LTECA_BLERCode0__SCell2                                       =0x7F061092;
	                                                                                                   
	 public static final int LTECA_BLERCode1_Total                                         =0x0A007007;
	 public static final int LTECA_BLERCode1_PCell                                         =0x7F06004B;
	 public static final int LTECA_BLERCode1_SCell1                                        =0x7F061099;
	 public static final int LTECA_BLERCode1_SCell2                                        =0x7F06109A;
	                                                                                                   
	 public static final int LTECA_InitialBLER_Total                                       =0x0A00700F;
	 public static final int LTECA_InitialBLER_PCell                                       =0x7F060105;
	 public static final int LTECA_InitialBLER_SCell1                                      =0x7F0610D9;
	 public static final int LTECA_InitialBLER_SCell2                                      =0x7F0610DA;
	                                                                                                   
	 public static final int LTECA_InitialBLERCode0_Total                                  =0x0A00700A;
	 public static final int LTECA_InitialBLERCode0_PCell                                  =0x7F060100;
	 public static final int LTECA_InitialBLERCode0_SCell1                                 =0x7F0610B1;
	 public static final int LTECA_InitialBLERCode0_SCell2                                 =0x7F0610B2;
	                                                                                                   
	                                                                                                   
	 public static final int LTECA_InitialBLERCode1_Total                                 =0x0A00700B ;
	 public static final int LTECA_InitialBLERCode1_PCell                                  =0x7F060101;                                                                                      
	 public static final int LTECA_InitialBLERCode1_SCell1                                 =0x7F0610B9;                                                                                      
	 public static final int LTECA_InitialBLERCode1_SCell2                                 =0x7F0610BA;
	                                                                                                   
	 public static final int LTECA_ResidualBLER_Total                                      =0x0A00700E;
	 public static final int LTECA_ResidualBLER_PCell                                      =0x7F060104;                                        
	 public static final int LTECA_ResidualBLER_SCell1                                     =0x7F0610D1;                                                
	 public static final int LTECA_ResidualBLER_SCell2                                     =0x7F0610D2;                                        
	                                                                                                                                                         
	 public static final int LTECA_ResidualBLERCode0_Total                                 =0x0A00700C;                                                        
	 public static final int LTECA_ResidualBLERCode0_PCell                                 =0x7F060102;                                                        
	 public static final int LTECA_ResidualBLERCode0_SCell1                                =0x7F0610C1;                                                        
	 public static final int LTECA_ResidualBLERCode0_SCell2                                =0x7F0610C2;                                                        
	                                                                                                                                                         
	                                                                                                                                                         
	 public static final int LTECA_ResidualBLERCode1_Total                                =0x0A00700D;                                                        
	 public static final int LTECA_ResidualBLERCode1_PCell                                 =0x7F060103;                                                        
	 public static final int LTECA_ResidualBLERCode1_SCell1                                =0x7F0610C9;                                                        
	 public static final int LTECA_ResidualBLERCode1_SCell2                                =0x7F0610CA;
	                                                                                                                                                                 
	                                                                                                                                                                                                                                                                                                                                                  
	 public static final int LTECA_QPSK_Total                                             =0x0A00705F;                                            
	 public static final int LTECA_QPSK_PCell                                              =0x7F060827;                                         
	 public static final int LTECA_QPSK_SCell1                                             =0x7F061381;                                         
	 public static final int LTECA_QPSK_SCell2                                             =0x7F061382;
	                                                                                                   
	 public static final int LTECA_16QAM_Total                                            =0x0A007060;                                     
	 public static final int LTECA_16QAM_PCell                                             =0x7F060828;                                        
	 public static final int LTECA_16QAM_SCell1                                            =0x7F061389;                                        
	 public static final int LTECA_16QAM_SCell2                                            =0x7F06138A;
	                                                                                                   
	 public static final int LTECA_64QAM_Total                                            =0x0A007061;                                            
	 public static final int LTECA_64QAM_PCell                                             =0x7F060829;                                              
	 public static final int LTECA_64QAM_SCell1                                            =0x7F061391;                                              
	 public static final int LTECA_64QAM_SCell2                                            =0x7F061392;                                              
	                                                                                                   
	 public static final int LTECA_QPSKCode0_Total                                          =0x0A00704E;                                                      
	 public static final int LTECA_QPSKCode0_PCell                                         =0x7F060814;                                                      
	 public static final int LTECA_QPSKCode0_SCell1                                        =0x7F0612F1;                                                      
	 public static final int LTECA_QPSKCode0_SCell2                                        =0x7F0612F2;                                                      
	                                                                                                   
	 public static final int LTECA_16QAMCode0_Total                                         =0x0A007050;                                                         
	 public static final int LTECA_16QAMCode0_PCell                                        =0x7F060816;                                                          
	 public static final int LTECA_16QAMCode0_SCell1                                       =0x7F061309;                                                          
	 public static final int LTECA_16QAMCode0_SCell2                                       =0x7F06130A;              
	                                                                                                   
	                                                                                                    	 	  	 	 	 	   	                                             
	 public static final int LTECA_64QAMCode0_Total                                         =0x0A007052; 	 	                                                              
	 public static final int LTECA_64QAMCode0_PCell                                        =0x7F060818; 	 	                                                              
	 public static final int LTECA_64QAMCode0_SCell1                                       =0x7F061319; 	 	                                                              
	 public static final int LTECA_64QAMCode0_SCell2                                       =0x7F06131A; 	 	                                                              
	                                                                                                   
	 public static final int LTECA_QPSKCode1_Total                                          =0x0A007054;                                                              
	 public static final int LTECA_QPSKCode1_PCell                                         =0x7F06081A; 
	 public static final int LTECA_QPSKCode1_SCell1                                        =0x7F061329; 
	 public static final int LTECA_QPSKCode1_SCell2                                        =0x7F06132A; 
	                                                                                                   
	                                                                                                   
	 public static final int LTECA_16QAMCode1_Total                                         =0x0A007056;
	 public static final int LTECA_16QAMCode1_PCell                                        =0x7F06081C;
	 public static final int LTECA_16QAMCode1_SCell1                                       =0x7F061339;
	 public static final int LTECA_16QAMCode1_SCell2                                       =0x7F06133A;
	                                                                                                   
	                                                                                                   
	 public static final int LTECA_64QAMCode1_Total                                         =0x0A007058;
	 public static final int LTECA_64QAMCode1_PCell                                        =0x7F06081E;
	 public static final int LTECA_64QAMCode1_SCell1                                       =0x7F061349;
	 public static final int LTECA_64QAMCode1_SCell2                                       =0x7F06134A;
	                                                                                                   
	 public static final int LTECA_MCSAvg_Total                                             =0x0A00704C;
	 public static final int LTECA_MCSAvg_PCell                                            =0x7F06080A;
	 public static final int LTECA_MCSAvg_SCell1                                           =0x7F0612E1;
	 public static final int LTECA_MCSAvg_SCell2                                          =0x7F0612E2;
	 
	 public static final int LTE_RANK_INDICATOR                                           =0x7F06000A;
	 public static final int LTE_Code_Word                                          	  =0x7F06002B;
	 public static final int LTE_Special_SubFrame_Patterns                                =0x7F060013;
	 public static final int LTE_SubFrame_Assignment_Type                                 =0x7F060014;
	 public static final int LTE_RSRP_Rx0                                                 =0x7F06001D;
	 public static final int LTE_RSRP_Rx1												  =0x7F06001E;
	 public static final int LTE_SINR_Rx0												  =0x7F06008F;	
	 public static final int LTE_SINR_Rx1												  =0x7F060090;
	 public static final int LTE_RSRQ_Rx0											      =0x7F060091;
	 public static final int LTE_RSRQ_Rx1												  =0x7F060092;			
	 public static final int LTE_RSSI_Rx0												  =0x7F060093;
	 public static final int LTE_RSSI_Rx1											      =0x7F060094;
	 public static final int LTE_Rank1_SINR												  =0x7F06000B;	
	 public static final int LTE_Rank2_SINR_code0										  =0x7F06000C;
	 public static final int LTE_Rank2_SINR_code1										  =0x7F06000D;
	 public static final int LTE_CRS_RP													  =0x7F060061;
	 public static final int LTE_PDSCH_TB_Size_code0                                      =0x7F06003C;
	 public static final int LTE_PDSCH_TB_Size_code1									  =0x7F06003D;
	 public static final int LTE_RANK1_CQI												  =0x7F060502;		
	 public static final int LTE_RANK2_CQI_code0										  =0x7F060503;
	 public static final int LTE_RANK2_CQI_code1										  =0x7F060504;			
	 public static final int LTE_PRACH_TxPower                                            =0x7F060005;
	 public static final int LTE_PUCCH_G 												  =0x7F060006;
	 public static final int LTE_PUSCH_F                                                  =0x7F060008;
	 public static final int LTE_RSPower                                                  =0x7F060D8C;
	 public static final int LTE_Pa                                                       =0x7F060D8E;
	 public static final int LTE_Pb                                                       =0x7F060D8D;
	 public static final int LTE_PDCCH_Format                                             =0x7F060032;
	 public static final int LTE_PDCCH_DCI_Format                                         =0x7F060033;
	 public static final int LTE_CCE1_Count                                               =0x7F06007C;
	 public static final int LTE_CCE2_Count                                               =0x7F06007D;
	 public static final int LTE_CCE4_Count                                               =0x7F06007E;
	 public static final int LTE_CCE8_Count                                               =0x7F06007F;
	 public static final int LTE_Rank1_Indicator_Number                                   =0x7F06008C;
	 public static final int LTE_Rank2_Indicator_Number 								  =0x7F06008D;
	 public static final int LTE_Access_Class_Bar										  =0x7F060086;		
	 public static final int LTE_T_Reselection_UTRA                                       =0x7F060D3F;
	 public static final int LTE_T_Reselection_GERAN                                      =0x7F060D40; 

	 public static final int PESQ_MOS					=  0x0A001055;
	 public static final int POLQA_Score_NB 	=  0x0A00105F;
	 public static final int POLQA_Score_SWB  =  0x0A001060;
	 
	 public static final int WIFI_SSID=0x7F04A002;
	 public static final int WIFI_IP_Address=0x7F044002;
	 public static final int WIFI_MAC_Address=0x7F04A001;
	 public static final int WIFI_Channel=0x7F044005;
	 public static final int WIFI_Signal_Strength=0x7F044100;
	 public static final int WIFI_SNR=0x7F044102;
	 public static final int WIFI_SFI=0x7F044103;
	 public static final int WIFI_AFI=0x7F044104;
	 public static final int WIFI_FrameThroughput=0x7F044200;
	 public static final int WIFI_FrameRetransRate=0x7F044201;

	 //应用层瞬时速率
	 public static final int DATA_FTP_DOWNLOAD_RATE=0x0A002151;
	 public static final int DATA_FTP_UPLOAD_RATE=0x0A002153;
	 public static final int DATA_PBM_DOWNLOAD_RATE=0x0A002682;
	 public static final int DATA_PBM_UPLOAD_RATE=0x0A002702;
	 public static final int DATA_HTTP_DOWNLOAD_RATE=0x0A002310;
	 public static final int DATA_HTTP_PAGE_RATE=0x0A002321;
	 public static final int DATA_HTTP_UP_RATE=0x0A0024A0;

	 public static final int DATA_MULTIFTP_DOWN=0x0A002201;
	 public static final int DATA_MULTIFTP_UP=0x0A002200;
	 public static final int DATA_SPEEDTEST_DOWN=0x0A002601;
	 public static final int DATA_SPEEDTEST_UP=0x0A002605;
	 public static final int DATA_IPERF_DOWN=0x0A002435;
	 public static final int DATA_IPERF_UP=0x0A002438;
	 public static final int DATA_UDP_UL=0x0A0027C1;
	 public static final int DATA_UDP_DL=0x0A0027B1;
}
