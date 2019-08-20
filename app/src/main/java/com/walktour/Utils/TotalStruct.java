package com.walktour.Utils;

/**
 * 统计过程中相关统计参数定义的枚举类型
 *
 * @author tangwq
 * @version 1.0
 */
public class TotalStruct {
    /**
     * 下面的枚举值与上面的Trace中吐出的参数名指定的位置相同
     *
     * @author tangwq
     */
    public static enum TotaletailPara {
        RxLevSub,        //GSM弱覆盖
        RxLevFull,
        RxQualSub,        //连续质差
        RxQualFull,
        TxPower,
        TA,

        ULRLCThr,
        upRLCCount,
        ULTBFIdentifier,
        ULCS,
        ULTS0,
        upMCAllCount,

        DLRLCThr,
        downRLCCount,        //RLC瞬时速率采样点数
        GPRSBLER,
        downRLCBlerClunt,
        DLTBFIdentifier,
        DLTS0,
        DLCS,
        downMCAllCount,        //总包数量

        Default
    }

    ;

    /**
     * 参数界面测试过程中需要进行统计的参数
     *
     * @author tangwq
     */
    public static enum TotalPara {
        rxQualFullMax,
        rxQualFullMin,
        rxQualFullAverage,
        rxQualSubMax,
        rxQualSubMin,
        rxQualSubAverage,
        rxLevFullMax,
        rxLevFullMin,
        rxLevFullAverage,
        rxLevSubMax,
        rxLevSubMin,
        rxLevSubAverage,
        ferFullMax,
        ferFullMin,
        ferFullAverage,
        ferSubMax,
        ferSubMin,
        ferSubAverage,
        bcchLevMax,
        bcchLevMin,
        bcchLevAverage
    }

    /**
     * 语音通话过程中的相关需要统计的参数
     *
     * @author tangwq
     */
    public static enum TotalDial {
        /**
         * CSFB Request总次数
         */
        _csfb_mo_request,

        /**
         * RRC Release总次数
         */
        _csfb_mo_RrcRelease,
        /**
         * RRC Release总时延
         */
        _csfb_mo_RrcReleaseDelay,
        /**
         * CSFB回落2G次数
         */
        _csfb_mo_proceeding_2G,
        /**
         * CSFB回落2G总时延ms, request到CSFB Call Proceeding的时延
         */
        _csfb_mo_proceedingDelay_2G,
        /**
         * CSFB Proceeding回落2G次数
         */
        _csfb_mo_proceeding_3G,
        /**
         * CSFB回落3G总时延ms, request到CSFB Call Proceeding的时延
         */
        _csfb_mo_proceedingDelay_3G,
        /**
         * Alerting（Setup）次数
         */
        _csfb_mo_alerting,
        /**
         * CSFB呼叫建立时延
         */
        _csfb_mo_SuccessDelay,
        /**
         * CSFB接通次数
         */
        _csfb_mo_Established,

        _csfb_mo_CallEnd,
        /**
         * Call Drop总次数
         */
        _csfb_mo_drop,

        /**
         * 2G返回LTE成功次数
         */
        _csfb_mo_2G_ReturnLTE,

        /**
         * 2G返回LTE成功总时延
         */
        _csfb_mo_2G_ReturnLTE_Delay,

        /**
         * 3G返回LTE成功次数
         */
        _csfb_mo_3G_ReturnLTE,

        /**
         * 3G返回LTE成功总时延
         */
        _csfb_mo_3G_ReturnLTE_Delay,

        /**
         * CSFB Request总次数
         */
        _csfb_mt_request,
        /**
         * RRC Release,有了这个事件之后才会有proceeding
         */
        _csfb_mt_RrcRelease,
        _csfb_mt_RrcReleaseDelay,
        /**
         * CSFB Proceeding次数
         */
        _csfb_mt_proceeding_2G,
        /**
         * CSFB回落总时延ms, request到CSFB Call Proceeding的时延
         */
        _csfb_mt_proceedingDelay_2G,
        /**
         * CSFB Proceeding次数
         */
        _csfb_mt_proceeding_3G,
        /**
         * CSFB回落总时延ms, request到CSFB Call Proceeding的时延
         */
        _csfb_mt_proceedingDelay_3G,
        /**
         * Alerting（Setup）次数
         */
        _csfb_mt_alerting,
        /**
         * Call Established次数
         */
        _csfb_mt_Established,
        _csfb_mt_CallEnd,
        /**
         * 建立时延(注意这个是主叫手机发出第一条rrcConnectionRequest（需同时判原因码＝0）
         * 到主叫Alerting、Connect、Connect Ack中最先到达的一条信令的时间差)
         */
        _csfb_mt_SuccessDelay,
        /**
         * Call Drop总次数
         */
        _csfb_mt_drop,
        /**
         * 2G返回LTE成功次数
         */
        _csfb_mt_2G_ReturnLTE,

        /**
         * 2G返回LTE成功总时延
         */
        _csfb_mt_2G_ReturnLTE_Delay,
        /**
         * 3G返回LTE成功次数
         */
        _csfb_mt_3G_ReturnLTE,

        /**
         * 3G返回LTE成功总时延
         */
        _csfb_mt_3G_ReturnLTE_Delay,

        _pesq_score,
        _pesq_scoreCount,

        _pesq_mos,
        _pesq_mosCount,

        _moTrys,            //主叫偿试
        _moConnects,        //主叫接通次数
        _moDropcalls,        //主叫掉话次数
        _moCalldelay,        //主叫总时延
        _moDelaytimes,        //计算时延的次数

        _video_moTrys,            //主叫偿试
        _video_moConnects,        //主叫接通次数
        _video_moDropcalls,        //主叫掉话次数
        _video_moCalldelay,        //主叫总时延
        _video_moDelaytimes,    //计算时延的次数

        _volte_moTrys,            //主叫偿试
        _volte_moConnects,        //主叫接通次数
        _volte_moDropcalls,        //主叫掉话次数
        _volte_moCalldelay,        //主叫总时延
        _volte_moDelaytimes,    //计算时延的次数

        _moRxLev1s,            //RxLev >= -94的采样点数
        _moRxLev2s,            //RxLev >= -90的采样点数
        _moTotalRxLevs,        //RxLev 总的采样点数
        _moRxQual1s,        //通话质量一级采样点RxQual中值为0,1,2
        _moRxQual2s,        //通话质量二级采样点RxQual中值为3,4,5
        _moTotalRxQuals,    //通话质量总采样点RxQual中值为0,1,2, 3,4,5
        _moMileageCover,    //里程覆盖采样点
        _moMileageCount,    //里程总采样点

        _mtTrys,
        _mtConnects,
        _mtDropcalls,
        _mtCalldelay,
        _mtDelaytimes,        //计算时延的次数

        _volte_mtTrys,
        _volte_mtConnects,
        _volte_mtDropcalls,
        _volte_mtCalldelay,
        _volte_mtDelaytimes,//计算时延的次数

        _video_mtTrys,
        _video_mtConnects,
        _video_mtDropcalls,
        _video_mtCalldelay,
        _video_mtDelaytimes,        //计算时延的次数

        _mtRxLev1s,
        _mtRxLev2s,
        _mtTotalRxLevs,
        _mtRxQual1s,
        _mtRxQual2s,
        _mtTotalRxQuals,
        _mtMileageCovere,
        _mtMileageCount,

        _moTDBler03s,        //MO状态下，
        _moTDBlerCount,
        _mtTDBler03s,
        _mtTDBlerCount,
        /**
         * 当前网络GSM = 0x01,WCDMA = 0x02,TD-SCDMA = 0x04,CDMA\EVDO = 0x08,LTE = 0x10,Unknown = 0x20,NoService = 0x80
         */
        _moTimeLongGSM,
        _mtTimeLongGSM,
        _moTimeLongWCDMA,
        _mtTimeLongWCDMA,
        _moTimeLongTD,
        _mtTimeLongTD,
        _moTimeLongCDMA,
        _mtTimeLongCDMA,
        _TimeLongLTE,
        _moTimeLongUnknown,
        _mtTimeLongUnknown,

        /**
         * TD SCDMA通话覆盖率
         */
        _moPccpchRscp1,
        _moPccpchRscp2,
        _moPccpchRscpCount,
        _mtPccpchRscp1,
        _mtPccpchRscp2,
        _mtPccpchRscpCount,

        /**
         * LTE通话覆盖率
         */
        _LTErsrp,
        _LTErsrpCount,

        /**
         * 里程覆盖率计划
         */
        _moGsmCoverMileage,        //GSM下MO的覆盖里程
        _moGsmTotalMileage,    //GSM下MO总里程
        _mtGsmCoverMileage,        //GSM下MT的覆盖里程
        _mtGsmTotalMileage,    //GSM下MT总里程
        _GsmCoverMileage,        //Gsm下非话音覆盖里程
        _GsmTotalMileage,        //Gsm下非话音下总里程
        _moWCoverMileage,        //WCDMA下MO的覆盖里程
        _moWTotalMileage,        //WCDMA下MO总里程
        _mtWCoverMileage,        //WCDMA下MT的覆盖里程
        _mtWTotalMileage,        //WCDMA下MT总里程
        _WcdmaCoverMileage,        //Wcdma下非话音覆盖里程
        _WcdmaTotalMileage,        //Wcdma下非话音下总里程
        _moTdCoverMileage,        //TD下MO的覆盖里程
        _moTdTotalMileage,        //TD下MO总里程
        _mtTdCoverMileage,        //TD下MT的覆盖里程
        _mtTdTotalMileage,        //TD下MT总里程
        _TdCoverMileage,        //TD下非话音覆盖里程
        _TdTotalMileage,        //TD下非话音下总里程
        _moCdmaCoverMileage,    //CDMA下MO的覆盖里程
        _moCdmaTotalMileage,    //CDMA下MO总里程
        _mtCdmaCoverMileage,    //CDMA下MT的覆盖里程
        _mtCdmaTotalMileage,    //CDMA下MT总里程
        _LteCoverMileage,        //LTE覆盖里程
        _LteTotalMileage,        //LTE下总里程


    }

    /**
     * FTP 测试过程中相关统计参数
     *
     * @author tangwq
     */
    public static enum TotalFtp {
        _downtrys,            //下载偿试次数
        _downSuccs,            //下载成功次数
        _downDrops,            //下载掉线次数
        /**
         * 当前次下载大小(bit)
         */
        _downCurrentSize,
        /**
         * 当前次下载时长(ms)
         */
        _downCurrentTimes,
        //多路FTP
        m_downtrys,            //下载偿试次数
        m_downSuccs,        //下载成功次数
        m_downDrops,            //下载掉线次数
        m_downCurrentSize,
        /**
         * 当前次下载时长(ms)
         */
        m_downCurrentTimes,
        /**
         * 最高下载速率
         */
        m_down_max_value,
        /**
         * 最高下载速率
         */
        _down_max_value,

        /**
         * 最低下载速率
         */
        _down_min_value,
        _downRLCThrs,        //RLC瞬时速率累加
        _downRLCCount,        //RLC瞬时速率采样点数
        _downRLCBlers,        //下载时间内的总误块率
        _downRLCBlerClunt,    //时间内误块率采样点
        _downAllTimes,        //下载总时长
        _downTBFOpenTimes,    //下载时间内TBFOpen总时长
        _downTSCounts,        //时间内时隙总数1，2，3个时隙的累加
        _downTSAllCount,    //下行时间总采样点
        _downMCCounts,        //下载时间内总包数MC1*1+..MC9*9
        _downMCAllCount,    //总包数量
        _downMileageCover,    //总包数量
        _downMileageCount,    //总包数量

        _uptrys,
        _upSuccs,
        _upDrops,
        /**
         * 当前次下载大小(bit)
         */
        _upCurrentSize,
        /**
         * 当前次下载时长(ms)
         */
        _upCurrentTimes,
        /**
         * 当前次下载大小(bit)
         */
        _upTotalBytes,
        /**
         * 当前次下载时长(ms)
         */
        _upTotalTime,
        //多路FTP
        m_uptrys,
        m_upSuccs,
        m_upDrops,
        m_upAverageThr,
        /**
         * 当前次下载大小(bit)
         */
        m_upCurrentSize,
        /**
         * 当前次下载时长(ms)
         */
        m_upCurrentTimes,
        /**
         * 最高上传速率
         */
        m_up_max_value,
        /**
         * 最高上传速率
         */
        _up_max_value,
        _upRLCThrs,
        _upRLCCount,
        _upAllTimes,
        _upTBFOpenTimes,
        _upTSCounts,
        _upTSAllcounts,
        _upMCCounts,
        _upMileageCover,    //总包数量
        _upMileageCount,    //总包数量
        _upMCAllCount
    }

//	/**
//	 * WAP测试过程中的相关统计信息
//	 * @author tangwq
//	 *
//	 */
//	public static enum TotalWap{
//	
//	}

    /**
     * 数据增值统计项
     *
     * @author tangwq
     */
    public static enum TotalAppreciation {
        _wapLoginTrys,        //登陆尝试次数
        _wapLogingSuccs,    //登陆成功次数
        _wapLoginDelay,        //登陆时延
        _wapRefreshTrys,    //刷新尝试次数
        _wapRefreshSuccs,    //刷新成功次数
        _wapRefreshDelay,    //刷新时延
        _wapDownTrys,        //下载尝试次数
        _wapDownSuccs,        //下载成功次数
        _wapDownTotalBytes, //下载总大小
        _wapDownTotalTime,    //下载总次数
        _MMSSendTry,        //MMS尝试次数
        _MMSSendSuccs,        //MMS发送成功次数
        _MMSSendDelay,        //发送时间延
        _MMSReceiveTry,        //MMS尝试次数
        _MMSReceiveSuccs,    //MMS发送成功次数
        _MMSReceiveFaild,    //MMS发送成功次数
        _MMSReceiveDelay,    //发送时间延
        _MMSPushTry,        //MMS尝试次数
        _MMSPushSuccs,        //MMS发送成功次数
        _MMSPushDelay,        //发送时间延
        _MMSPtoPDelay,        //端到端平均时延
        _MMSP2PCount,        //端到端次数
        _SMSSendTry,        //SMS尝试次数
        _SMSSendSuccs,        //SMS发送成功次数
        _SMSSendFaild,        //SMS发送成功次数
        _SMSSendDelay,        //发送时间延
        _SMSReceiveTry,        //SMS尝试次数
        _SMSReceiveSuccs,    //SMS发送成功次数
        _SMSReceiveFaild,    //SMS发送成功次数
        _SMSReceiveDelay,    //发送时间延
        _SMSPtoPDelay,        //端到端平均时延
        _SMSPtoPCount,        //端到端次数
        _HttpTry,
        _HttpRefreshTry,
        _HttpDownloadTry,
        _HttpSuccess,
        _HttpRefreshSuccess,
        _HttpDownloadSuccess,
        _HttpDelay,
        _HttpRefreshDelay,
        _HttpDownloadTotalBytes,
        _HttpDownloadTotalTime,
        _pingTry,
        _pingTotalTrys,
        _pingSuccess,
        _pingTotalSuccs,
        _pingDelay,
        _pingTotalDelay,
        _pingDelayMin,
        _pingDelayMax,
        _EmailSendTry,
        _EmailSendSuccess,
        _EmailSendSumSize,
        _EmailSendAllTime,
        _EmailReceiveTry,
        _EmailReceiveSuccess,
        _EmailReceSumSize,
        _EmailReceAllTime,
        _HttpUploadTry,        //上传尝试次数
        _HttpUploadSuccess,    //上传成功次数
        _HttpUploadDelay,      //上传时延
        _HttpUploadMeanRate,   //上传速率
        _HttpUploadMeanRateTimes,
        _HttpUploadTotalBytes,   //上传速率
        _HttpUploadTotalTime,
        _OttTryTimes,           //ott尝试测试
        _OttSuccessTimes,       //ott成功次数
        _OttDelayTimes,         //ott时延
    }

    /**
     * 测试过程中相关测试参数统计
     * 最大值，最小值，采样点累加，采样点次数
     *
     * @author tangwq
     */
    public static enum TotalMeasurePara {
        //GSM相关测量参数统计
        _rxLevFull("7F000103"),
        _rxLevSub("7F000104"),
        _rxQualFull("7F000105"),
        _rxQualSub("7F000106"),
        _txPower("7F000114"),
        _ta("7F000113"),
        //no show
        _vCodec("7F000314"),
        _bcchLev("7F000109"),


        //GPRS/EDGE 测量参数统计
        _ulRLCThr("7F000401"),
        _dlRLCThr("7F000402"),
        _ulRLCRTX("7F000403"),
        _dlRLCRTX("7F000404"),
        _ulLLCThr("7F000501"),
        _dlLLCThr("7F000502"),
        _bepGMSK("7F00061A"),
        _cvBepGMSK("7F000618"),
        _bep8PSK("7F00061B"),
        _cvBep8PSK("7F000619"),
        _cvalue("7F000610"),
        _gprsBLER("7F000607"),
        _rlcBLER("7F000408"),
        //no show
        _dlTBFState("7F000609"),
        _mcsDown("7F000617"),
        _dlTSNum("7F000603"),
        _ulTSNum("7F000604"),
        _ulTBFState("7F00060A"),
        _mcsUp("7F000616"),


        //WCDMA Cell Info
        _wTotalRSCP("7F02000D"),
        _wTotalEclo("7F02000A"),
        _wRxPower("7F020001"),
        _wTxPower("7F020002"),
        _wBLER("7F020005"),
        _wSIR("7F020010"),


        //TDSCDMA Cell Info
        _tPCCPCH_RSCP("7F03011B"),
        _tPCCPCH_C_I("7F030120"),
        _tUE_TxPower("7F030102"),
        _tBLER("7F030119"),
        //no show
        _tdlRLCThr("7F030601"),
        _tulRLCThr("7F030602"),
        _tdlRLCBLER("7F030605"),
        _tulRLCRTXRate("7F030606"),
        _pdschTotalLER("7F030728"),

        //LTE Cell Info
        _lRSRP("7F06000E"),
        _lRSRQ("7F06000F"),
        _lSINR("7F060001"),
        _lSRS_Power("7F060011"),
        _lPRACH_Power("7F060005"),
        _lPUCCH_Power("7F060007"),
        _lPUSCH_Power("7F060009"),
        _rlc_BLER_UL("7F060738"),
        _rlc_BLER_DL("7F060739"),

        //VoLTE
        _RFC1889_Jitter("7F1D4005"),
        _Jitter_Buffer_Delay("7F1D4010"),
        _Packet_LossCount("7F1D401A"),
        _SIP_Rx_RTP_Packet_Num("7F1D4033"),
        _Packet_Delay("7F1D400E"),


        //no show
        _lupPDCPThr("7F060B03"),
        _ldownPDCPThr("7F060B02"),
        _lupRLCThr("7F060B07"),
        _ldownRLCThr("7F060B06"),
        _lupMACThr("7F060B01"),
        _ldownMACThr("7F060B00"),
        _lupPhysThr("7F060B05"),
        _ldownPhysThr("7F060B04"),
        _lupMCSMean("0A004106"),
        _ldownMCSMean("0A004107"),
        _lupRBCount("7F060206"),
        _ldownRBCount("7F060207"),
        _lpdSCHBLER("7F06006E"),
        _lpuSCHBLER("7F060069"),


        //HSDPA
        _hsdpaPhysRequestThr("7F02030F"),
        _hsdpaPhysScheduledThr("7F020310"),
        _hsdpaPhysServiceThr("7F020311"),
        _hsdpaMACThr("7F020312"),

        //HSUPA
        _hsupaPhysServiceThr("7F020404"),
        _hsupaMACThr("7F020403"),

        //MIMO
        _mimoPhysScheduledThr("70F20615"),
        _mimoPhysServiceThr("70F20616"),

        //Dual Cell
        _dualPhysRequestThr("7F02063D"),
        _dualPhysScheduledThr("7F02063E"),
        _dualPhysServiceThr("7F02063F"),

        //CDMA相关测量参数
        _cTotalECIO("7F01000C"),
        _cTotalEC("7F01000F"),
        _cFFER("7F010006"),
        _cRxAGC("7F010001"),
        _cTxAGC("7F010002"),
        _cTxPower("7F010003"),

        //CDMA 1X
        _cRXPhysThr("7F010205"),
        _cTPhysThr("7F010206"),
        _cRXRLPThr("7F010201"),
        _cTXRLPThr("7F010202"),
        _cRLPErrRate("7F010203"),
        _cRLPRTXRate("7F010204"),

        //EVDO相关测试参数
        _eRXAGC0("7F018001"),
        _eRXAGC1("7F018002"),
        _eTxAGC("7F018003"),
        _eTotalSINR("7F018008");

        //TDSCDMA相关测量参数
        private String paramId;

        private TotalMeasurePara(String id) {
            this.paramId = id;
        }

        public String getParamId() {
            return this.paramId;
        }
    }

    /**
     * 测试过程中事件统计相关参数
     *
     * @author tangwq
     */
    public static enum TotalEvent {
        _switchTimes,                //切换次数	RR Handover Command
        _switchSuccs,                //切换成功次数	RR Handover Complete
        _switchFaild,                //切换失败	RR Handover Failure
        _lauTrys,                    //LAU位置更新尝试次数	Location Updating Request
        _lauSuccs,                    //LAU位置更新成功次数	Location Updating Accept LAU
        _lauFaild,                    //LAU位置更新失败次数	Location Updating Reject LAU
        _rauTrys,                    //RAU路由更新尝试次数	Routing Area Update Request RAU
        _rauSuccs,                    //RAU路由更新成功次数	Routing Area Update Accept RAU/Routing Area Update Complete
        _rauFaild,                    //RAU路由更新失败		Routing Area Update Reject RAU
        _sectionReChooses,            //小区重选次数	idle状态下，如果当前BCCH值发生改变，则计为一次小区重选
        _testTimeLong,                //测试时长	用于计算RAU间隔，小区重选间隔
        _testMileage,                //测试里程
        _moGsmSwitch,
        _moGsmSwitchSuccs,
        _mtGsmSwitch,
        _mtGsmSwitchSuccs,
        _lacTry,
        _lacSuccs,
        _lteHandOverReq,
        _lteHandOverSucss,
        _moTDBatonHandoverRequest,
        _mtTDBatonHandoverRequest,
        _moTDBatonHandoverSuccess,
        _mtTDBatonHandoverSuccess,
        _moTDHardHandoverRequest,
        _mtTDHardHandoverRequest,
        _moTDHardHandoverSuccess,
        _mtTDHardHandoverSuccess,
        _moReSetGSM2TD,
        _mtReSetGSM2TD,
        _moReSetTD2GSM,
        _mtReSetTD2GSM,
        _moHandOverGSM2TD,
        _mtHandOverGSM2TD,
        _moHandOverTD2GSM,
        _mtHandOverTD2GSM,
        _lteHandoverToTDRequest,
        _lteHandoverToTDSuccess,
        _lteHandoverToTDFailure,
        _redirectionLTEToGSMRequest,
        _redirectionLTEToGSMSuccess,
        _redirectionLTEToGSMFailure,
        _lteToGsmCellComplete,
        _lteToGsmCellRequest,
        _gsmToLteCellComplete,
        _gsmToLteCellRequest,
        _lteToTdCellComplete,
        _lteToTdCellRequest,
        _tdscdmaToLteCellComplete,
        _tdscdmaToLteCellRequest,
        _gsmTotdHandoverRequest,
        _gsmTotdHandoverSuccess,
        _tdTogsmHandoverRequest,
        _tdTogsmHandoverSuccess,
        _gsmTotdCellReselectRequest,
        _gsmTotdCellReselectComplete,
        _tdTogsmCellResRequest,
        _tdTogsmCellResSuccess,
        _lteTogsmHandoverRequest,
        _lteTogsmHandoverSuccess
    }

    /**
     * 自定义事件的属性
     *
     * @author qihang.li
     */
    public static enum TotalCustomEvent {
        _delay,
        _longitude,
        _latitude
    }

    /**
     * Video Play
     *
     * @author jone
     */
    public static enum TotalVideoPlay {
        _videoAvgRecvSpeedKbps,
        _videoAV_DeSync,
        _videoAv_Vmos,
        _videoAv_VmosTimes,
        _videoTrys,
        _videoSuccs,
        _videoConnectTime,
        _videoReproductionStart,
        _videoReproductionFailure,
        _videoReproductionDaily,
        _videoPlayEnd,
        _videoDrop,
        _videoReBuffers,
        _videoReBufferSuccess,
        _videoReBufferFail,
        _videoRebufferTime,
        _videoPlayDuration,
        _vpTotalBytes,
        _vpCurRecvSpeed,
        _vpCurRecvSpeedTimes,
        _vpTotalTime,
        _vpTotalSample
    }

    /**
     * WAP测试过程中的相关统计信息
     *
     * @author tangwq
     */
    public static enum TotalVS {
        _vsTrys,        //流媒体尝试次数
        _vsSuccs,        //流媒体成功次数
        _vsConnectTime,//接入时长
        _vsReproductionStart,//复制开始次数
        _vsReproductionFailure,
        _vsReproductionDaily,//接入时延
        _vsReproductionEnd,//复制成功次数
        _vsPlayEnd,//播放完成次数
        _vsDrop,//掉线次数
        _vsReBufferFailure,//缓冲导制的失败次数
        _vsReBuffers,//缓冲次数
        _vsReBufferSuccess,//缓冲成功次数
        _vsRebufferTime,//缓冲时长

        _vsTotalQosTimes,//QOS报告次数
        _vsTotalReceivedPackage,//接收保总数
        _vsTotalPackgeLoss,//丢包总数
        _vsMeanPackedInterval,//平均慢包间隔时间
        _vsMeanJitter,//平均包抖动
        _vsTotalReceivedSize,//接收数据总大小
        _vsMeanRate,//平均接收速率
        _vsTotalBufferCount,//缓冲总次数
        //_vsTotalBufferTime,//缓冲总时长
        _vsMeanAVDelay,//平均A-V时延
        _vsMeanAVCorrection,//平均A-V纠错帧率
        _vsMeanAVSync,//平均A-V丢帧率
        _vsMeanFPS,//平均帧率
        _vs_decode_frames,//解码过的帧数

        _vsVideoTotalRecv,
        _vsAudio_Pkg_Recv,//_vsAudioTotalRecv,
        _vsVideoTotalLost,
        _vsAudio_Pkg_Lost,//_vsAudioTotalLost,
        _vsVideo_Interval,//_vsAvgVideoPacketGap,
        _vsAudio_Interval,//_vsAvgAudioPacketGap,
        _vsVideo_Jitter,//_vsAvgVideoPacketJitter,
        _vsAudio_Jitter,//_vsAvgAudioPacketJitter,
        _vsTotalBytes,//_vsTotalRecvByte,
        _vsAvgRecvSpeedKbps,
        _vsA_v_async,//_vsAV_DeSync,
        _vsAv_Vmos,
        _vsAv_VmosTimes,
        _vsTotalTime,
        _vsTotalSample
    }

    /**
     * DNS统计字段
     *
     * @author Xie Jihong
     */
    public static enum TotalDNS {
        _dnsTotalTrys,//尝试次数
        _dnsSuccs,//成功次数
        _dnsDelay//时延 ms
    }

    /**
     * SpeedTest统计字段
     *
     * @author Xie Jihong
     */
    public static enum TotalSpeed {
        _speedTotalTrys,//尝试次数
        _speedPingSuccs,//Ping成功次数
        _speedDownloadSuccs,//DownLoad成功次数
        _speedUploadSuccs,//Upload成功次数
        _speedDelay,//时延 ms
        _speedUpKbps,//上传速率 kbps
        _speedUpKbpsCounts,
        _speedDownKbps,//下载速率 kbps
        _speedDownKbpsCounts,
        _speedSuccs,
        _speedULTotalBytes,
        _speedULTotalTime,
        _speedDLTotalBytes,
        _speedDLTotalTime
    }

    /**
     * Facebook统计字段
     *
     * @author XieJihong
     */
    public static enum TotalFaceBook {
        _faceBookAttempts,            //收到Facebook Test Start事件的总次数
        _faceBookSuccesses,            //收到Facebook Test Success事件的总次数，或者Failure事件（原因码User Stop）
        _faceBookLoginAttempts,        //收到Facebook Action Start(ActionType=1)事件的总次数
        _faceBookLoginSuccesses,        //收到Facebook Action Success(ActionType=1)事件的总次数
        _faceBookLoginMeanDelay,        //收到Facebook Action Success(ActionType=1)事件中的delay均值
        _faceBookGetWallAttempts,        //收到Facebook Action Start(ActionType=2)事件的总次数
        _faceBookGetWallSuccesses,        //收到Facebook Action Success(ActionType=2)事件的总次数
        _faceBookGetWallDownBytes,        //下载大小：收到Facebook Action Success(ActionType=2)事件中的down_bytes
        _faceBookGetWallMeanDelay,        //收到Facebook Action Success(ActionType=2)事件中的delay
        _faceBookGetFriendListAttempts,        //收到Facebook Action Start(ActionType=3)事件的总次数
        _faceBookGetFriendListSuccesses,        //收到Facebook Action Success(ActionType=3)事件的总次数
        _faceBookGetFriendListDownBytes,        //下载大小：收到Facebook Action Success(ActionType=3)事件中的down_bytes
        _faceBookGetFriendListMeanDelay,        //时延：收到Facebook Action Success(ActionType=3)事件中的delay
        _faceBookPostStatusAttempts,        //收到Facebook Action Start(ActionType=4)事件的总次数
        _faceBookPostStatusSuccesses,        //收到Facebook Action Success(ActionType=4)事件的总次数
        _faceBookPostStatusUpBytes,        //上传大小：收到Facebook Action Success(ActionType=4)事件中的up_bytes
        _faceBookPostStatusMeanDelay,        //时延：收到Facebook Action Success(ActionType=4)事件中的delay
        _faceBookPostPhotoAttempts,        //收到Facebook Action Start(ActionType=5)事件的总次数
        _faceBookPostPhotoSuccesses,        //收到Facebook Action Success(ActionType=5)事件的总次数
        _faceBookPostPhotoUpBytes,        //上传大小：收到Facebook Action Success(ActionType=5)事件中的up_bytes
        _faceBookPostPhotoMeanDelay,        //时延：收到Facebook Action Success(ActionType=5)事件中的delay
        _faceBookPostCommentAttempts,        //收到Facebook Action Start(ActionType=6)事件的总次数
        _faceBookPostCommentSuccesses,        //收到Facebook Action Success(ActionType=6)事件的总次数
        _faceBookPostCommentUpBytes,        //上传大小：收到Facebook Action Success(ActionType=6)事件中的up_bytes
        _faceBookPostCommentMeanDelay,        //时延：收到Facebook Action Success(ActionType=6)事件中的delay
        _faceBookLogoutAttempts,        //收到Facebook Action Start(ActionType=7)事件的总次数
        _faceBookLogoutSuccesses,    //收到Facebook Action Success(ActionType=7)事件的总次数
        _faceBookLogoutMeanDelay            //收到Facebook Action Success(ActionType=7)事件中的delay均值
    }

    public static enum TotalTraceRoute {
        _traceRouteTrys,
        _traceRouteSucc,
        _traceRouteDelay,
        _traceRouteHopCounts
    }

    public static enum TotalHttpType {
        HTTPLogon("HTTP Logon"),
        HTTPRefresh("HTTP Refresh"),
        HTTPDownload("HTTP Download"),
        HTTPUpload("HTTP Upload");

        private String httpType = "";

        private TotalHttpType(String type) {
            httpType = type;
        }

        public String getHttpType() {
            return httpType;
        }
    }

    public static enum TotalAttach {
        _attachRequest,
        _attachSuccess,
        _attachDelay,

        _lteSearchDelay,//搜网时延
        _lteAttachRequest,
        _lteAttachSuccess,
        _lteAttachDelay,

    }

    public static enum TotalPdp {
        _pdpRequest,
        _pdpSuccess,
        _pdpDelay,
    }

    /**
     * 所有业务的PPP统计
     */
    public static enum TotalPPP {
        _pppRequest,
        _pppSuccess,
        _pppDelay,
    }

    public static enum TotalPBM {
        _pbmUpLostFraction,//PBM上行丢包率
        _pbmUpPkgGap,//PBM上行包间隔(ms)
        _pbmUpBandwidth,//PBM上行带宽
        _pbmDownLostFraction,//PBM下行丢包率
        _pbmDownPkgGap,//PBM下行包间隔(ms)
        _pbmDownBandwidth,//PBM下行带宽
        _pbmCurrentTimes,//当前执行次数
    }
    public static enum TotalUDP {
        _upTryTimes,//尝试次数-上行
        _upSuccessTimes,//成功次数-上行
        _upFailTimes,//失败次数-上行
        _upAverageSpeed,//应用层平均速率-上行
        _upAverageSpeedRLC,
        _upAverageSpeedMAC,
        _upAverageSpeedPHY,
        _upAverageSpeedRLCTotal,
        _upAverageSpeedMACTotal,
        _upAverageSpeedPHYTotal,
        _upAverageSpeedPDCPTotal,
        _downTryTimes,//尝试次数-下行
        _downSuccessTimes,//成功次数-下行
        _downFailTimes,//失败次数-下行
        _downAverageSpeed,//应用层平均速率-下行
        _downAverageSpeedRLC,
        _downAverageSpeedMAC,
        _downAverageSpeedPHY,
        _downAverageSpeedRLCTotal,
        _downAverageSpeedMACTotal,
        _downAverageSpeedPHYTotal,
        _downAverageSpeedPDCPTotal
    }

    public static enum TotalMultiHttpDownload{
        _TryTimes,//尝试次数
        _SuccessTimes,//成功次数
        _FalureTimes,//成功次数
    }

    public static enum TotalWeiBo {
        _weiboLoginTimes,//登录次数
        _weiboLoginSuccessTimes,//登录成功次数
        _weiboSentTextTimes,//发送微博次数
        _weiboSentTextSuccessTimes,//发生微博成功次数
        _weiboSentTextTotalDelay,//发生微博总时延
        _weiboSentPicTimes,//发送图片次数
        _weiboSentPicSuccessTimes,//发送图片成功次数
        _weiboSentPicTotalDelay,//发送图片总时延
        _weiboRefreshTimes,//粉丝刷新微博次数
        _weiboRefreshSuccessTimes,//粉丝刷新微博成功次数
        _weiboRefreshTotalDelay,//粉丝刷新微博总时延
        _weiboCommentTimes,//粉丝评论次数
        _weiboCommentSuccessTimes,//粉丝评论成功次数
        _weiboCommentTotalDelay,//粉丝评论总时延
        _weiboRelayTimes,//粉丝转发次数
        _weiboRelaySuccessTimes,//粉丝转发成功次数
        _weiboRelayTotalDelay,//粉丝转发总时延
        _weiboReadPicTimes,//粉丝查看原图次数
        _weiboReadPicSuccessTimes,//粉丝查看原图次数
        _weiboReadPicTotalDelay,//粉丝查看原图总时延
    }

    public static enum TotalWeChat {
        _sendMsgCount,//发送消息次数
        _sendMsgSuccessCount,//发送消息成功次数
        _sendMsgTotalDelay,//发送消息总时延
        _sendMsgTotalUpbytes,//发送消息总上行字节数
        _sendMsgTotalDownbytes,//发送消息总下行字节数
        _sendImgCount,//发送图片次数
        _sendImgSuccessCount,//发送图片成功次数
        _sendImgTotalDelay,//发送图片总时延
        _sendImgTotalUpbytes,//发送图片总上行字节数
        _sendImgTotalDownbytes,//发送图片总下行字节数
        _sendVoiceCount,//发送语音次数
        _sendVoiceSuccessCount,//发送语音成功次数
        _sendVoiceTotalDelay,//发送语音总时延
        _sendVoiceTotalUpbytes,//发送语音总上行字节数
        _sendVoiceTotalDownbytes//发送语音总下行字节数
    }

	public static enum TotalWeChatAudiAppTest {
		_call_dial_audio_start_count,//语音拨打开始次数
		_call_dial_audio_ring_count,//语音拨打振铃次数
		_call_dial_audio_success_count,//次数
		_call_dial_audio_failed_count,//语音接通失败次数
		_call_session_audio_start_count,//语音会话开始次数
		_call_session_audio_success_count,//语音会话成功次数
		_call_session_audio_failed_count//语音会话失败次数
	}

	public static enum TotalMultipleWeChatAppTest {
		_startAppCount,//首页加载次数
		_startAppSuccessCount,//首页加载成功次数
		_startAppTotalDelay,//首页加载总时延
		_sendMsgCount,//发送消息次数
		_sendMsgSuccessCount,//发送消息成功次数
		_sendMsgTotalDelay,//发送消息总时延
		_sendMsgTotalUpbytes,//发送消息总上行字节数
		_sendMsgTotalDownbytes,//发送消息总下行字节数
		_sendImgCount,//发送图片次数
		_sendImgSuccessCount,//发送图片成功次数
		_sendImgTotalDelay,//发送图片总时延
		_sendImgTotalUpbytes,//发送图片总上行字节数
		_sendImgTotalDownbytes,//发送图片总下行字节数
		_sendVoiceCount,//发送语音次数
		_sendVoiceSuccessCount,//发送语音成功次数
		_sendVoiceTotalDelay,//发送语音总时延
		_sendVoiceTotalUpbytes,//发送语音总上行字节数
		_sendVoiceTotalDownbytes//发送语音总下行字节数
	}

	public static enum TotalMultipleQQAppTest {
		_startAppCount,//首页加载次数
		_startAppSuccessCount,//首页加载成功次数
		_startAppTotalDelay,//首页加载总时延
		_sendMsgCount,//发送消息次数
		_sendMsgSuccessCount,//发送消息成功次数
		_sendMsgTotalDelay,//发送消息总时延
		_sendMsgTotalUpbytes,//发送消息总上行字节数
		_sendMsgTotalDownbytes,//发送消息总下行字节数
		_sendImgCount,//发送图片次数
		_sendImgSuccessCount,//发送图片成功次数
		_sendImgTotalDelay,//发送图片总时延
		_sendImgTotalUpbytes,//发送图片总上行字节数
		_sendImgTotalDownbytes,//发送图片总下行字节数
		_sendVoiceCount,//发送语音次数
		_sendVoiceSuccessCount,//发送语音成功次数
		_sendVoiceTotalDelay,//发送语音总时延
		_sendVoiceTotalUpbytes,//发送语音总上行字节数
		_sendVoiceTotalDownbytes//发送语音总下行字节数
	}

	public static enum TotalMultipleTaoBaoAppTest {
		_startAppCount,//首页加载次数
		_startAppSuccessCount,//首页加载成功次数
		_startAppTotalDelay,//首页加载总时延
		_channelCount,//频道加载次数
		_channelSuccessCount,//频道加载成功次数
		_channelTotalDelay,//频道加载总时延
		_contentCount,//内容加载次数
		_contentSuccessCount,//内容加载成功次数
		_contentTotalDelay,//内容加载总时延
	}

	public static enum TotalMultipleSinaWeBoAppTest {
		_startAppCount,//首页加载次数
		_startAppSuccessCount,//首页加载成功次数
		_startAppTotalDelay,//首页加载总时延
		_channelCount,//频道加载次数
		_channelSuccessCount,//频道加载成功次数
		_channelTotalDelay,//频道加载总时延
		_contentCount,//内容加载次数
		_contentSuccessCount,//内容加载成功次数
		_contentTotalDelay,//内容加载总时延
	}

	public static enum TotalMultipleWangYiNewAppTest {
		_startAppCount,//首页加载次数
		_startAppSuccessCount,//首页加载成功次数
		_startAppTotalDelay,//首页加载总时延
		_channelCount,//频道加载次数
		_channelSuccessCount,//频道加载成功次数
		_channelTotalDelay,//频道加载总时延
		_contentCount,//内容加载次数
		_contentSuccessCount,//内容加载成功次数
		_contentTotalDelay,//内容加载总时延
	}

	public static enum TotalMultipleiQiYiAppTest {
		_startAppCount,//首页加载次数
		_startAppSuccessCount,//首页加载成功次数
		_startAppTotalDelay,//首页加载总时延
	}

	public static enum TotalMultipleTencentVideoAppTest {
		_startAppCount,//首页加载次数
		_startAppSuccessCount,//首页加载成功次数
		_startAppTotalDelay,//首页加载总时延
	}

	public static enum TotalMultipleYouKuAppTest {
		_startAppCount,//首页加载次数
		_startAppSuccessCount,//首页加载成功次数
		_startAppTotalDelay,//首页加载总时延
	}

	public static enum TotalMultipleMiGuAppTest {
		_startAppCount,//首页加载次数
		_startAppSuccessCount,//首页加载成功次数
		_startAppTotalDelay,//首页加载总时延
	}

	public static enum TotalMultipleDouYinAppTest {
		_startAppCount,//首页加载次数
		_startAppSuccessCount,//首页加载成功次数
		_startAppTotalDelay,//首页加载总时延
	}

	public static enum TotalMultipleSkypeAppTest {
		_sendMsgCount,//发送消息次数
		_sendMsgSuccessCount,//发送消息成功次数
		_sendMsgTotalDelay,//发送消息总时延
		_sendMsgTotalUpbytes,//发送消息总上行字节数
		_sendMsgTotalDownbytes,//发送消息总下行字节数
		_sendImgCount,//发送图片次数
		_sendImgSuccessCount,//发送图片成功次数
		_sendImgTotalDelay,//发送图片总时延
		_sendImgTotalUpbytes,//发送图片总上行字节数
		_sendImgTotalDownbytes,//发送图片总下行字节数
		_sendVoiceCount,//发送语音次数
		_sendVoiceSuccessCount,//发送语音成功次数
		_sendVoiceTotalDelay,//发送语音总时延
		_sendVoiceTotalUpbytes,//发送语音总上行字节数
		_sendVoiceTotalDownbytes//发送语音总下行字节数
	}

    public enum TotalSingleStation {
        _RSCoverMileage,//RS覆盖率=RS条件采样点数（RSRP≥-95dBm & RS SINR≥9dB）/总采样点×100%
        _FTPCoverMileage,//覆盖测试的覆盖率RSRP > -110dBm且RS-SINR ≥ -3dB
        _PCISample,//PCI采样点
        _SpeedAverage,//平均速率
        _RSRPAverage,//RSRP的平均值
        _tryTimes,//尝试次数
        _successRate,//成功率(%)
        _csfb_tryTimes,//CSFB尝试次数
        _csfb_establishedRate,//CSFB连接成功率(%)
        _csfb_successRate,//CSFB成功率(%)
        _volte_eSRVCC,//eSRVCC切换成功次数
        _volte_successRate,//VOLTE成功率(%)
        _FTPAttemptHandoverTimes,//FTP 尝试切换次数
        _FTPHandoverSuccessRate, //FTP 切换成功次数
        _csfb_returnDelay,//返回4G时延
        _csfb_connectDelay,//CSFB接通时延
        _csfb_returnSuccessRate,//返回4G成功率
    }
}
