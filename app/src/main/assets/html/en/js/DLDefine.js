var DLByLogSum = 'DLSum';

//key : display
var DLNetEnum = {
	'GSM':'GSM',
	'CDMA':'CDMA',
	'EVDO':'EVDO',
	'WCDMA':'WCDMA',
	'TDSCDMA':'TDSCDMA',
	'LTE':'LTE'
};

// key : display
var DLBusinessEnum = {
	'CS':'Voice 2/3G',
	'CSFB':'Voice CSFB',
	'Network_Connect':'Network Connect',
	'FTPDownload':'FTPDownload',
	'FTPUpload':'FTPUpload',
	'MultiFTPDownload':'MultiFTPDownload',
	'MultiFTPUpload':'MultiFTPUpload',
	'Ping' : 'Ping',
	'HTTPPage_Login':'HTTPPage Login',
	'HTTPPage_Refresh':'HTTPPage Refresh',
	'HTTPDownload':'HTTPDownload',
	'HTTPUpload':'HTTPUpload',
	'EmailRecv':'EmailRecv',
	'EmailSend':'EmailSend',
	'VideoPlay':'VideoPlay',
	'Speedtest':'Speedtest',
	'DNSLookup':'DNSLookup',
	'PBM':'PBM',
	'SMSSend':'SMSSend',
	'SMSRecv':'SMSRecv',
	'MMSSend':'MMSSend',
	'MMSRecv':'MMSRecv',
	'TraceRoute':'TraceRoute',
};

// key : display
var DLParamterEnum = {
	'GSM':{
		'RxLevel_Full':'RxLevelFull',
		'RxLevel_Sub':'RxLevelSub',
		'RxQual_Sub':'RxQualSub',
		'BCCH_Level':'BCCHLevel',
		'GSM_TA':'TA',
		'GSM_TxPower':'TxPower',
		'GSM_VoiceCodec':'VoiceCode',
		'RLC_DL_Throughput':'RLC DL Throughput',
		'RLC_UL_Throughput':'RLC UL Throughput',
		'BLER':'BLER',
		'DL_MCS':'DL MCS/CS',
		'UL_MCS':'UL MCS/CS',
		'DL_TBF_Open' : 'DL TBF Open',
		'UL_TBF_Open':'UL TBF Open'
	},
	'CDMA':{
		'CDMA_Total_EcIo':'TotalEcIo',
		'CDMA_Total_Ec':'TotalEc',
		'CDMA_FFER':'FFER',
		'CDMA_RxAGC':'RxAGC',
		'CDMA_TxPower':'TxPower'
	},
	'EVDO':{
		'EVDO_RxAGC0':'RxAGC0',
		'EVDO_RxAGC1':'RxAGC1',
		'EVDO_TxAGC':'TxAGC',
		'EVDO_TotalSINR':'TotalSINR',
		'EVDO_RxPER':'RxPER',
		'EVDO_TxPER':'TxPER',
		'EVDO_RxSuPER':'RxSuPER'
	},
	'WCDMA':{
		'WCDMA_Total_EcIo':'TotalEcIo',
		'WCDMA_Total_RSCP':'TotalRSCP',
		'WCDMA_RxPower':'RxPower',
		'WCDMA_TxPower':'TxPower',
		'WCDMA_BLER':'BLER',
		'WCDMA_UL_Interference':'UL Interference',
		'WCDMA_DL_Interference':'DL Interference',
		'WCDMA_DL_AMR_Rate':'DL AMR Rate',
		'WCDMA_UL_AMR_Rate':'UL AMR Rate',
		'WCDMA_DL_RLC_Thr':'DL RLC Thr',
		'WCDMA_UL_RLC_Thr':'UL RLC Thr',
		'WCDMA_CQI_Mean':'CQI Mean',
		'WCDMA_Phys_Request_Thr':'Phys Request Thr',
		'WCDMA_Phys_Schedule_Thr':'Phys Schedule Thr',
		'WCDMA_Phys_Service_Thr':'Phys Service Thr',
		'MAC Thr':'WCDMA_MAC_Thr',
		'HPSA_DC_CQI_Mean_1st':'DC CQI Mean 1st',
		'HPSA_DC_CQI_Mean_2nd':'DC CQI Mean 2nd',
		'HSPA_Phys_Schedule_Thr_1st':'Phys Schedule Thr 1st',
		'HSPA_Phys_Service_Thr_1st':'Phys Service Thr 1st',
		'HSPA_Phys_Request_Thr_2st':'Phys Request Thr 2st',
		'HSPA_Phys_Schedule_Thr_2st':'Phys Schedule Thr 2st',
		'HSPA_Phys_Service_Thr_2st':'Phys Service Thr 2st'
	},
	'TDSCDMA':{
		'TDS_UTRA_CarrierRSSI':'UTRA CarrierRSSI',
		'TDS_UE_TxPower':'UE TxPower',
		'TDS_DPCH_RSCP':'DPCH RSCP',
		'TDS_DPCH_CI':'DPCH C/I',
		'TDS_BLER':'BLER',
		'TDS_PCCPCH_RSCP':'PCCPCH RSCP',
		'TDS_PCCPCH_CI':'PCCPCH C/I',
		'TDS_DL_RLC_Thr':'DL RLC Thr',
		'TDS_UL_RLC_Thr':'UL RLC Thr',
		'TDS_Phys_Schedule_Thr':'Phys Schedule Thr',
		'TDS_Phys_Service_Thr':'Phys Service Thr'
	},
	'LTE':{
		'LTE_RSRP':'RSRP',
		'LTE_RSRQ':'RSRQ',
		'LTE_RSSI':'RSSI',
		'LTE_SINR':'SINR',
		'LTE_PUSCH_TxPower':'PUSCH TxPower',
		'LTE_UL_PDCP_Thr':'UL PDCP Thr',
		'LTE_DL_PDCP_Thr':'DL PDCP Thr',
		'LTE_UL_RLC_Thr':'UL RLC Thr',
		'LTE_DL_RLC_Thr':'DL RLC Thr',
		'LTE_UL_MAC_Thr':'UL MAC Thr',
		'LTE_DL_MAC_Thr':'DL MAC Thr',
		'LTE_UL_Phy_Thr':'UL Phy Thr',
		'LTE_DL_Phy_Thr_Code0':'DL PhyThrCode0',
		'LTE_DL_Phy_Thr_Code1':'DL PhyThrCode1',
		'LTE_PDSCH_RB_Count':'PDSCH RB Count',
		'LTE_PUSCH_RB_Count':'PUSCH RB Count',
		'LTE_WideBand_CQI':'WideBand CQI',
		'LTE_WideBand_CQI_Code0':'WideBand CQI Code0',
		'LTE_WideBand_CQI_Code1':'WideBand CQI Code1',
		'LTE_PDSCH_BLER':'PDSCH BLER',
		'LTE_PDCCH_BLER':'PDCCH BLER',
		'LTE_PUSCH_BLER':'PUSCH BLER'
	},
	'PUBLIC':{
		'UL_Current_AMRCodec':'UL Current AMRCodec',
		'DL_Current_AMRCodec':'DL Current AMRCodec',
		'DL_App_Layer_Thr_Mean':'Application Layer DL Speed',
		'UL_App_Layer_Thr_Mean':'Application Layer UL Speed',
		'DL_FTP_Thr_Inst':'DL FTP Thr Inst',
		'UL_FTP_Thr_Inst':'UL FTP Thr Inst'
	}
};

var DLParamterWithUnitEnum = {
	'GSM':{
		'RxLevel_Full':'RxLevelFull(dBm)',
		'RxLevel_Sub':'RxLevelSub(dBm)',
		'RxQual_Sub':'RxQualSub',
		'BCCH_Level':'BCCHLevel(dBm)',
		'GSM_TA':'TA',
		'GSM_TxPower':'TxPower(dBm)',
		'GSM_VoiceCodec':'VoiceCode',
		'RLC_DL_Throughput':'RLC DL Throughput',
		'RLC_UL_Throughput':'RLC UL Throughput',
		'BLER':'BLER(%)',
		'DL_MCS':'DL MCS/CS',
		'UL_MCS':'UL MCS/CS',
		'DL_TBF_Open' : 'DL TBF Open',
		'UL_TBF_Open':'UL TBF Open'
	},
	'CDMA':{
		'CDMA_Total_EcIo':'TotalEcIo(dB)',
		'CDMA_Total_Ec':'TotalEc(dBm)',
		'CDMA_FFER':'FFER(%)',
		'CDMA_RxAGC':'RxAGC(dBm)',
		'CDMA_TxPower':'TxPower(dBm)'
	},
	'EVDO':{
		'EVDO_RxAGC0':'RxAGC0(dBm)',
		'EVDO_RxAGC1':'RxAGC1(dBm)',
		'EVDO_TxAGC':'TxAGC(dBm)',
		'EVDO_TotalSINR':'TotalSINR(dB)',
		'EVDO_RxPER':'RxPER(%)',
		'EVDO_TxPER':'TxPER(%)',
		'EVDO_RxSuPER':'RxSuPER(%)'
	},
	'WCDMA':{
		'WCDMA_Total_EcIo':'TotalEcIo(dB)',
		'WCDMA_Total_RSCP':'TotalRSCP(dBm)',
		'WCDMA_RxPower':'RxPower(dBm)',
		'WCDMA_TxPower':'TxPower(dBm)',
		'WCDMA_BLER':'BLER(%)',
		'WCDMA_UL_Interference':'UL Interference(dBm)',
		'WCDMA_DL_Interference':'DL Interference(dBm)',
		'WCDMA_DL_AMR_Rate':'DL AMR Rate',
		'WCDMA_UL_AMR_Rate':'UL AMR Rate',
		'WCDMA_DL_RLC_Thr':'DL RLC Thr(KB/s)',
		'WCDMA_UL_RLC_Thr':'UL RLC Thr(KB/s)',
		'WCDMA_CQI_Mean':'CQI Mean',
		'WCDMA_Phys_Request_Thr':'Phys Request Thr(KB/s)',
		'WCDMA_Phys_Schedule_Thr':'Phys Schedule Thr(KB/s)',
		'WCDMA_Phys_Service_Thr':'Phys Service Thr(KB/s)',
		'WCDMA_MAC_Thr':'MAC Thr(KB/s)',
		'HPSA_DC_CQI_Mean_1st':'DC CQI Mean 1st',
		'HPSA_DC_CQI_Mean_2nd':'DC CQI Mean 2nd',
		'HSPA_Phys_Schedule_Thr_1st':'Phys Schedule Thr 1st',
		'HSPA_Phys_Service_Thr_1st':'Phys Service Thr 1st',
		'HSPA_Phys_Request_Thr_2st':'Phys Request Thr 2st',
		'HSPA_Phys_Schedule_Thr_2st':'Phys Schedule Thr 2st',
		'HSPA_Phys_Service_Thr_2st':'Phys Service Thr 2st'
	},
	'TDSCDMA':{
		'TDS_UTRA_CarrierRSSI':'UTRA CarrierRSSI(dBm)',
		'TDS_UE_TxPower':'UE TxPower(dBm)',
		'TDS_DPCH_RSCP':'DPCH RSCP(dBm)',
		'TDS_DPCH_CI':'DPCH C/I(dB)',
		'TDS_BLER':'BLER(%)',
		'TDS_PCCPCH_RSCP':'PCCPCH RSCP(dBm)',
		'TDS_PCCPCH_CI':'PCCPCH C/I(dB)',
		'TDS_DL_RLC_Thr':'DL RLC Thr(KB/s)',
		'TDS_UL_RLC_Thr':'UL RLC Thr(KB/s)',
		'TDS_Phys_Schedule_Thr':'Phys Schedule Thr(KB/s)',
		'TDS_Phys_Service_Thr':'Phys Service Thr(KB/s)'
	},
	'LTE':{
		'LTE_RSRP':'RSRP(dBm)',
		'LTE_RSRQ':'RSRQ(dB)',
		'LTE_RSSI':'RSSI(dB)',
		'LTE_SINR':'SINR(dB)',
		'LTE_PUSCH_TxPower':'PUSCH TxPower(dBm)',
		'LTE_UL_PDCP_Thr':'UL PDCP Thr(KB/s)',
		'LTE_DL_PDCP_Thr':'DL PDCP Thr(KB/s)',
		'LTE_UL_RLC_Thr':'UL RLC Thr(KB/s)',
		'LTE_DL_RLC_Thr':'DL RLC Thr(KB/s)',
		'LTE_UL_MAC_Thr':'UL MAC Thr(KB/s)',
		'LTE_DL_MAC_Thr':'DL MAC Thr(KB/s)',
		'LTE_UL_Phy_Thr':'UL Phy Thr(KB/s)',
		'LTE_DL_Phy_Thr_Code0':'DL PhyThrCode0',
		'LTE_DL_Phy_Thr_Code1':'DL PhyThrCode1',
		'LTE_PDSCH_RB_Count':'PDSCH RB Count',
		'LTE_PUSCH_RB_Count':'PUSCH RB Count',
		'LTE_WideBand_CQI':'WideBand CQI',
		'LTE_WideBand_CQI_Code0':'WideBand CQI Code0',
		'LTE_WideBand_CQI_Code1':'WideBand CQI Code1',
		'LTE_PDSCH_BLER':'PDSCH BLER',
		'LTE_PDCCH_BLER':'PDCCH BLER',
		'LTE_PUSCH_BLER':'PUSCH BLER'
	},
	'PUBLIC':{
		'UL_Current_AMRCodec':'UL Current AMRCodec',
		'DL_Current_AMRCodec':'DL Current AMRCodec',
		'DL_App_Layer_Thr_Mean':'Application Layer DL Speed',
		'UL_App_Layer_Thr_Mean':'Application Layer UL Speed',
		'DL_FTP_Thr_Inst':'DL FTP Thr Inst',
		'UL_FTP_Thr_Inst':'UL FTP Thr Inst'
	}
};


var DLTestStaticsFields = {
	'TestTotalTime':'TestTotalTime',
	'TestTotalMileage':'TestTotalMileage',
	'OutOfServiceTime':'OutOfServiceTime',
	'OutOfServiceMileage':'OutOfServiceMileage',
	'StartTime':'StartTime',
	'EndTime':'EndTime',
	'Coverage':'Coverage'
}

var DLStaticBusinessFields = {
	'CS':'Call',
	'CSFB':'Call',
	'Ping_TBFClose':'Ping',
	'Ping_TBFOpen':'Ping',
	'Ping_GSMTotal':'Ping',
	'Ping_CellFach':'Ping',
	'Ping_CellDch':'Ping',
	'Ping_Other':'Ping',
	'Ping_WCDMATotal':'Ping',
	'HTTPPage_Login':'HTTPPage',
	'HTTPPage_Refresh':'HTTPPage',
}

//go nogo 设置业务对应表
var DLGoNoGoMatchBusinessNames = {
	'Call':['MO_CS','MT_CS','MO_CSFB','MT_CSFB'],
	'Ping':['Ping_TBFClose','Ping_TBFOpen','Ping_GSMTotal','Ping_CellFach','Ping_CellDch','Ping_WCDMATotal','Ping_Other'],
	'HTTPPage':['HTTPPage_Login','HTTPPage_Refresh'],
}

var DLVoiceNameEnum = {
	'MO_CS' : 'MOC',
	'MT_CS' : 'MTC',
	'MO_CSFB' : 'MOC',
	'MT_CSFB' : 'MTC',
}

var DLVoiceNameEnumDetail = {
	'MO_CS' : 'MO CS',
	'MT_CS' : 'MT CS',
	'MO_CSFB' : 'MO CSFB',
	'MT_CSFB' : 'MT CSFB',
}

