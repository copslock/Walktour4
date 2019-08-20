package com.dinglicom;

import com.walktour.Utils.UnifyParaID;
import com.walktour.base.util.LogUtil;

public class DataSetLib {
    /**
     * 0为参数资源
     */
    public static int RESOURCETYPE_PARAM = 0;
    /**
     * 1为事件资源
     */
    public static int RESOURCETYPE_EVENT = 1;
    /**
     * 2为业务过程资源
     */
    public static int RESOURCETYPE_BUSINESS = 2;

    private String[] param;

    /**
     * 消息参数查询注册后,android实时通知回调函数
     *
     * @param callBack 返回格式:"%d@@%d@@%llx@@%llx", APointIndex, AInfoType, AInfoID, AInfoValue
     *                 要回调的消息通过regObserverInfo添加到数据集，然后测试时会实时回调
     */
    public void dsCallBack(String callBack) {
        // LogUtil.d("DataSetLib","--callBack:" + callBack);
        try {
            param = callBack.split("@@");
            if (param.length >= 1) {
                int infoType = Integer.parseInt(param[1]);
                LogUtil.d("DataSetLib", "--callBack:infoType=" + infoType);
                switch (infoType) {
                case Register_Observer_Info_FileSize:
                    currentFileLength = Long.parseLong(param[3], 16);
                    currentFileType = Integer.parseInt(param[2]);
                    LogUtil.d("DataSetLib", "--callBack:--currentFileLength:" + currentFileLength);
                    break;
                case Register_Observer_Info_Param:
                    break;
                case Register_Observer_Info_Signal:
                    break;
                case Register_Observer_Info_Struct:
                    break;
                case Register_Observer_Info_FileName:
                    currentFileName = param[5];// 读取前36个字符长度
                    LogUtil.d("DataSetLib", "--callBack:--currentFileName:" + currentFileName);
                    break;
                }
            }
        } catch (Exception e) {
            LogUtil.w("DataSetLib", "dsCallBack", e);
        }
        param = null;
    }

    public class FileType {
        public static final int CPV_FileType_Unknown = 0;
        public static final int CPV_FileType_RCU = 1;
        public static final int CPV_FileType_RCUS = 2;
        public static final int CPV_FileType_DCF = 3;
        public static final int CPV_FileType_SourceRCU = 4;
        public static final int CPV_FileType_MDM = 10;
        public static final int CPV_FileType_ISF = 11;
        public static final int CPV_FileType_FMT = 12;
        public static final int CPV_FileType_UETR = 13;
        public static final int CPV_FileType_NEMO = 14;
        public static final int CPV_FileType_CTI = 15;
        public static final int CPV_FileType_OTSPARAM = 17;
        public static final int CPV_FileType_CU = 19;
        public static final int CPV_FileType_ECTI = 7;
        public static final int CPV_FileType_LOG = 100;

        // 2014.1.3 目前还没用到以下格式
        // public static final int CPV_FileType_Dtlog_GSM_Text = 50;
        // public static final int CPV_FileType_Dtlog_CDMA_Text = 51;
        // public static final int CPV_FileType_Dtlog_UMTS_Text = 52;
        // public static final int CPV_FileType_Dtlog_TD_Text = 53;
        // public static final int CPV_FileType_Dtlog_GSM_Binary = 54;
        // public static final int CPV_FileType_Dtlog_CDMA_Binary = 55;
        // public static final int CPV_FileType_Dtlog_UMTS_Binary = 56;
        // public static final int CPV_FileType_Dtlog_TD_Binary = 57;
        // public static final int CPV_FileType_Dtlog_EVDO_Text = 58;
        // public static final int CPV_FileType_Dtlog_EVDO_Binary = 59;
        // public static final int CPV_FileType_LTE_Binary = 60;

        // 2013.12.26 DTLog商务终端的格式
        public static final int dkDTLogGSMText_Phone = 61; // kjb20110928 Text
        public static final int dkDTLogCDMAText_Phone = 62;
        public static final int dkDTLogUMTSText_Phone = 63;
        public static final int intdkDTLogTDText_Phone = 64;
        public static final int dkDTLogGSMBin_Phone = 65; // kjb20110928 Bin
        public static final int dkDTLogCDMABin_Phone = 66;
        public static final int dkDTLogUMTSBin_Phone = 67;
        public static final int dkDTLogTDBin_Phone = 68;
        public static final int dkDTLogEVDOText_Phone = 69;
        public static final int dkDTLogEVDOBin_Phone = 70;
        public static final int dkDTLogLTEBin_Phone = 71;// 移动商务终端lte的dtlog

        public static final int dkDTLogWLANBin_Phone = 100;// wifi测试时终端dtlog
    }

    public static final int ConfigPropertyKey_IsCheckGPSDrift = 200;// 设置为GPS漂移过滤
    /**
     * 数据格式默认配置项
     */
    public static final int ConfigPropertyKey_Logic_StatisticMethod = 501;
    public static final int ConfigPropertyKey_Logic_ServiceProvider = 502;
    public static final int ConfigPropertyKey_Logic_MessageLostInterval = 503;
    public static final int ConfigPropertyKey_Logic_FTPDropMode = 504;
    public static final int ConfigPropertyKey_Logic_UserStopMode = 505;
    public static final int ConfigPropertyKey_Logic_TimeOutMode = 506;
    public static final int ConfigPropertyKey_Logic_DeviceLostMode = 507;
    public static final int ConfigPropertyKey_Logic_PPPDropMode = 508;
    public static final int ConfigPropertyKey_Logic_VoiceIgnoreDevoceLost = 509;
    public static final int ConfigPropertyKey_Logic_AbandonNoVoiceDialMOSP = 510;
    public static final int ConfigPropertyKey_Logic_BlockCallTimeout = 511;
    public static final int ConfigPropertyKey_Logic_VoiceDropByExceptionCodeOfDownDisconnectMsg = 500;
    public static final int ConfigPropertyKey_Logic_NoRCUEventVoiceSPMode = 512;
    public static final int ConfigPropertyKey_Logic_AbandonHaveAlertBeforeBlockSP = 513;
    public static final int ConfigPropertyKey_Logic_AbandonSpecialReasonExceptVoiceSP = 514;
    public static final int ConfigPropertyKey_Logic_Email70543DropMode = 515;
    public static final int ConfigPropertyKey_Logic_VoLTECallDropWhenNoEndMessage = 517;
    public static final int ConfigPropertyKey_Logic_VoLTECallEndCheckByeOKDirection = 519;
    public static final int ConfigPropertyKey_Logic_LastCallNotInclude = 520;
    // public static final int ConfigPropertyKey_Logic_InviteBidVersion = 105;
    // 招标特殊项

    /***当前返回的文件大小**/
    public static long currentFileLength = -9999;
    /***当前返回的文件名**/
    public static String currentFileName = "";

    /**
     * 当前的文件类型
     **/
    public static int currentFileType = -1;
    // 2014.5.13 qihang.li以下PropertyKey是数据集定义的Key
    /**
     * 配置源文件的解密文件内容 密钥
     */
    public static final int ConfigPropertyKey_String_SecretKeyContent = 202;
    /**
     * Walktour在Mode端口写文件顺序号的配置信息,顺序号由UtilsMethod.buildRucFileNum(num)生成
     */
    public static final int ConfigPropertyKey_Complex_RCUSecretContent = 9;
    /**
     * DCF加密用户证书内容
     */
    public static final int ConfigPropertyKey_Complex_DCFUserCertContent = 11;
    /**
     * 配置产品类型 enum TProductType {ptUnkonwn = 0, ptPionner, ptWTIphone, ptWTAndroid, ptRCU, ptEnd}
     */
    public static final int ConfigPropertyKey_ProductType = 10003;
    /**
     * 设备ID
     */
    public final static int ConfigPropertyKey_String_DeviceID = 0;
    /**
     * 商务终端DTLog文件的测试计划版本号KEY
     */
    public final static int PropertyKey_TestPlanID = 10006;
    /**
     * 测试开始时刻,到1970年1月1日0：0：0秒的微秒数（本地时间),配置为采集场景，则需要配置本项的值
     */
    public final static int ConfigPropertyKey_StartCurDateTime = 10007;

    /**
     * 解码库参数过滤间隔
     */
    public final static int PropertyKey_Filter_Interval = 1003;
    /**
     * 不存储的特殊结构分组,没使用此配置，默认为全存储；有配置，则不存储所属分组的结构体
     */
    public final static int PropertyKey_Non_Storge = 5;
    /**
     * 设定指定类型文件的Buffer大小
     */
    public final static int PropertyKey_File_Buffer = 7;
    /**
     * 生成其他格式的文件时，写文件使用的技术类型,0：内存映射，1：标准IO库
     */
    public final static int ConfigPropertyKey_OutputWriterType = 10;
    /**
     * 存储ddib时，是否在每次写文件之后，都强制刷新，0表示不需要强制刷新，1表示需要强制刷新，默认为需要强制刷新
     **/
    public final static int ConfigPropertyKey_StorageModuleFlushAfterWriteFile = 58;
    /**
     * 数据集调试模式设置项
     */
    public final static int PropertyKey_Debug_Model = 1000;
    /**
     * 数据集调试日志的路径
     */
    public final static int PropertyKey_Log_Path = 1002;
    public final static int CPV_DecodeMode_OnlyParse = 7; // 只做分帧
    public final static int CPV_DecodeMode_OnlyDecode = 2; // 只调用解码库的函数，不做逻辑判断，不存储
    public final static int CPV_DecodeMode_DecodeAndReadFromDecoder = 3;
    // 调用解码器的解码函数，并调用解码器的访问函数，得到解码后信息
    public final static int CPV_DecodeMode_DoLogic = 5;
    // 调用解码库解码函数及访问函数，并做业务事件逻辑判断，及记录解码后信息的元数
    public final static int CPV_DecodeMode_Normal = 1; // 正常解码
    /**
     * 设置是否对ddib文件进行加密,值为1表示需要对ddib进行加密,0表示不需要,不设置默认为不加密
     */
    public final static int Property_Encry_DDIBFile_Key = 59;
    public final static int Property_Encry_DDIBFile_Yes = 1; // 该值需要对ddib文件进行加密
    public final static int Property_Encry_DDIBFile_No = 0; // 该值表示不对ddib文件进行加密
    public final static int ConfigPropertyKey_DatasetSceneValue = 11; // 应用场景,0采集数据,1非采集（比如重解码)
    /**
     * 输出文件是否为Dgz压缩格式  高四字节为输出文件类型，取值同上；低四字节为1或0，分别表示压缩或不压缩，默认不压缩。
     * 例如dkDTLogLTEBin= 60格式要压缩，配置值为：0x0000003C00000001
     */
    public final static int ConfigPropertyKey_OutputFileIsDgzCompressed = 300;
    /**
     * 压缩缓存大小
     */
    public final static int ConfigPropertyKey_OutputBeforeCompressDataBufferSize = 12;

    /**
     * 设置实时输出source文件 1为实时
     */
    public final static int ConfigPropertyKey_OutputDataBufferRealTime = 14;

    /**
     * 实时通知道消息类型	1实时参数通知
     */
    public final static int Register_Observer_Info_Param = 1;
    /**
     * 实时通知道消息类型	2 实时信令通知
     */
    public final static int Register_Observer_Info_Signal = 2;
    /**
     * 实时通知道消息类型	3 实时结构通知
     */
    public final static int Register_Observer_Info_Struct = 3;
    /**
     * 实时通知道消息类型	4  实时文件大小
     */
    public final static int Register_Observer_Info_FileSize = 4;
    /**
     * 实时通知道消息类型	8  目标文件的实际文件名（目前只有CTI、CU）
     */
    public final static int Register_Observer_Info_FileName = 8;
    // 联通CU格式格式配置项
    /**
     * 需要配置的数据格式  1:联通,2:移动,3:电信
     */
    public final static int ConfigPropertyKey_CUSP_OperatorID = 110;
    /**
     * 需要配置的数据格式  1:DT,2:CQT
     */
    public final static int ConfigPropertyKey_CUSP_TestType = 111;
    /**
     * 配置导出DCF格式文件是否需要加密 1:需要，0：不需要
     */
    public final static int ConfigPropertyKey_DecodeOutputDCFEncryption = 1005;
    /**
     * 是否处于综合测试仪的环境（信令流程可能会与现网不同）配置为1，就独立输出,配置不为1，维持现状,不配置就是维持现状（信令流程可能会与现网不同）
     */
    public final static int ConfigPropertyKey_Decode_IsComprehensiveTestInstrument = 1008;
    /**
     * 配置是否输出手机信令详解Buffer
     */
    public final static int ConfigPropertyKey_DecodeOutputMS_MsgDetailBuffer = 1004;
    /**
     * 产品信息
     */
    public final static int ConfigPropertyKey_String_ProductName = 5;
    /**
     * 测试网络类型
     */
    public final static int ConfigPropertyKey_String_NetType = 6;
    /**
     * 国际ISDN号码(电话号码)
     */
    public final static int ConfigPropertyKey_String_MSIISDN = 29;
    /**
     * 生成其他格式的文件时，若文件名有格式定义，是否由数据集按格式定义生成文件名,
     * 目前对文件名有格式定义的数据格式有：*.cti；*.cu;
     * 1表示由数据集生成文件名,0表示由调用者自行生成文件名;默认值：由数据集生成文件名
     */
    public final static int ConfigPropertyKey_OutputGenerateRealFileName = 15;
    /**
     * 城市名称
     */
    public final static int ConfigPropertyKey_String_TestCityName = 24;
    /**
     * 测试范围
     */
    public final static int ConfigPropertyKey_String_TestRange = 25;
    /**
     * 测试厂家
     */
    public final static int ConfigPropertyKey_String_TestFactory = 26;
    /**
     * 扩展信息
     */
    public final static int ConfigPropertyKey_String_TestExtendInfo = 27;

    /**
     * C网语音业务判起呼时，是否需要判断原因码，1表示需要，0表示不需要，默认为不需要
     */
    public final static int ConfigPropertyKey_Logic_VoiceCDMAAttemptNeedOption = 518;

    /**
     * 生成rcu数据时，是否将GPS信息保存为鼎利通用GPS格式（RCU文件的0端口的ModelName为“DingLi GPS”），0表示不生效，1表示生效
     */
    public final static int ConfigPropertyKey_OutputRCUFileUseDLGPSDevice = 61;

    /***
     * 暂停是否存储ddib信息,设为0-暂停仍然会存储数据,1-暂停不会存储数据
     */
    public final static int ConfigPropertyKey_OutputDdibFileInfo = 73;

    /***
     * 是否存储5G信令，1表示存储，0表示不存储，默认为0
     */
    public final static int ConfigPropertyKey_Storage5GInfo = 74;
    static {
        //注意:----lib_3.9.x_latest加载方式
//        System.loadLibrary("CryptJni");
//        System.loadLibrary("crystax_shared");
//        System.loadLibrary("gnustl_shared");
//        System.loadLibrary("CommonDataSetBase");
//        System.loadLibrary("iconv");
//        System.loadLibrary("myglib");
//        System.loadLibrary("CustomWireshark");
//        System.loadLibrary("CommonIPDecoder");
//        System.loadLibrary("miniSDL");
//        System.loadLibrary("mypcap");
//        System.loadLibrary("DataSetFileOperator");
//        System.loadLibrary("DataSetResource");
//        System.loadLibrary("PilotDevice");
//        System.loadLibrary("DataSetOrignalFileReader");
//        System.loadLibrary("DataSetOrignalFileWriter");
//        System.loadLibrary("CommonDecodeDataSet");
//        System.loadLibrary("tdscdmaRRC");
//        System.loadLibrary("wcdmarrc");
//        System.loadLibrary("lte_rrc");
//        System.loadLibrary("GNWDataProtocolParse");
//        System.loadLibrary("nas");
//        System.loadLibrary("GPRSPacketDecoder");
//        System.loadLibrary("LteL3Decoder");
//        System.loadLibrary("sip");
//        System.loadLibrary("Decode_Messages_Qualcomm");
//        System.loadLibrary("DataSet");
//        System.loadLibrary("DecoderScannerMessage");
//        System.loadLibrary("Pctel_Android_Parser");
//        System.loadLibrary("CommonDataSetScannerStructInfoManager");
//        System.loadLibrary("CommonDataSetStructInfoManager");
//        System.loadLibrary("CommonDataSetSPInfoManager");
//        System.loadLibrary("CommonDataSetSPSimpleInfoManager");
//        System.loadLibrary("ECTIParser");
//        System.loadLibrary("CommonTCPIPMsgTranslater");
//        System.loadLibrary("CommonVolteDecoder");
//        System.loadLibrary("QualcommSIPMsgFormater");
//        System.loadLibrary("HisiliconMifiParser");
//        System.loadLibrary("HuaWeiCSVParser");
//注意:----lib_4.0.x_latest加载方式
        System.loadLibrary("CryptJni");
        System.loadLibrary("crystax_shared");
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("CommonDataSetBase");
        System.loadLibrary("iconv");
        System.loadLibrary("myglib");
        System.loadLibrary("CustomWireshark");
        System.loadLibrary("CommonIPDecoder");
        System.loadLibrary("miniSDL");
        System.loadLibrary("mypcap");
        System.loadLibrary("DataSetFileOperator");
        System.loadLibrary("DataSetResource");
        System.loadLibrary("PilotDevice");
        System.loadLibrary("DataSetOrignalFileReader");
        System.loadLibrary("DataSetOrignalFileWriter");
        System.loadLibrary("CommonDecodeDataSet");
        System.loadLibrary("tdscdmaRRC");
        System.loadLibrary("wcdmarrc");
//        System.loadLibrary("lte_rrc");
        System.loadLibrary("GNWDataProtocolParse");
        System.loadLibrary("LTERRCF30");
        System.loadLibrary("LTERRCF40");
        System.loadLibrary("LTERRCF50");
        System.loadLibrary("Layer3MsgParser");
        System.loadLibrary("NRRRCF21");
        System.loadLibrary("NRRRCF30");
        System.loadLibrary("NRRRCF40");
        System.loadLibrary("NRRRCF50");
        System.loadLibrary("nas");
        System.loadLibrary("GPRSPacketDecoder");
        System.loadLibrary("LteL3Decoder");
        System.loadLibrary("sip");
        System.loadLibrary("Decode_Messages_Qualcomm");
        System.loadLibrary("DataSet");
        System.loadLibrary("DecoderScannerMessage");
        // 加入Scanner解码库
        System.loadLibrary("Pctel_Android_Parser");
        System.loadLibrary("CommonDataSetScannerStructInfoManager");
        System.loadLibrary("CommonDataSetStructInfoManager");
        System.loadLibrary("CommonDataSetSPInfoManager");
        System.loadLibrary("CommonDataSetSPSimpleInfoManager");
        System.loadLibrary("ECTIParser");
        System.loadLibrary("CommonTCPIPMsgTranslater");
        System.loadLibrary("CommonVolteDecoder");
        System.loadLibrary("QualcommSIPMsgFormater");
        System.loadLibrary("HisiliconMifiParser");
        System.loadLibrary("HuaWeiCSVParser");

    }

    public static EnumSetCellType[] cellWcdmaArray = { EnumSetCellType.SCTumtsActive, EnumSetCellType.SCTumtsCardinate, EnumSetCellType.SCTumtsNeighbor, EnumSetCellType.SCTumtsDetected, EnumSetCellType.SCTumtsVirtualActiveSet };
    /*
     * ActiveSetType:0 UmtsFreq:10688 UmtsPSC;103 UmtsRssi;-68.000000 UmtsEcIo;-5.267518 UmtsRSCP;-73.267517
     */
    public static int[] cellWcdmaParam = { UnifyParaID.Set_SetType, UnifyParaID.Set_Frequency, UnifyParaID.Set_PN, UnifyParaID.Set_Rssi, UnifyParaID.Set_EcIo, UnifyParaID.Set_Rscp };

    public static EnumSetCellType[] cellLteArray = { EnumSetCellType.SCTlteActive, EnumSetCellType.SCTlteNeighbor };
    /* 0: Earfcn; 1:Pci; 2:Rsrp(/100); 3:Rsrq(/100); 4:Rssi(/100) */
    public static int[] cellLteParam = { UnifyParaID.Set_Frequency, UnifyParaID.Set_PN, UnifyParaID.Set_Rsrp, UnifyParaID.Set_Rsrq, UnifyParaID.Set_Rssi, UnifyParaID.Set_SetType };

    public static EnumSetCellType[] cellCdmaArray = { EnumSetCellType.SCTcdmaActive, EnumSetCellType.SCTcdmaCardinate, EnumSetCellType.SCTcdmaNeighbor, EnumSetCellType.SCTcdmaDetected };
    /* neighbor:ActiveSetType,CdmaFreq,CdmaPn,CdmaRssi,CdmaRSCP,CdmaEcIo,CdmaDummy */
    public static int[] cellCdmaParam = { UnifyParaID.Set_SetType, UnifyParaID.Set_Frequency, UnifyParaID.Set_PN, UnifyParaID.Set_Rssi, UnifyParaID.Set_Rscp, UnifyParaID.Set_EcIo };

    public static EnumSetCellType[] cellEvdoArray = { EnumSetCellType.SCTevdoActive, EnumSetCellType.SCTevdoCardinate, EnumSetCellType.SCTevdoNeighbor, EnumSetCellType.SCTevdoDetected };
    public static int[] cellEvdoParam = { UnifyParaID.Set_SetType, UnifyParaID.Set_Frequency, UnifyParaID.Set_PN, UnifyParaID.Set_EVRssi, UnifyParaID.Set_EVEcio, UnifyParaID.Set_EVC2I, UnifyParaID.Set_EVDRCCover };

    public enum EnumNetType {
        GSM, CDMA(UnifyParaID.C_cdmaServingNeighbor, cellCdmaParam, cellCdmaArray), WCDMA(UnifyParaID.W_TUMTSCellInfoV2, cellWcdmaParam, cellWcdmaArray), TSCDMA, WiMax, WiFi, CMMB, LTE(UnifyParaID.LTE_CELL_LIST, cellLteParam, cellLteArray), EVDO(UnifyParaID.E_EVServingNeighbor, cellEvdoParam, cellEvdoArray);

        private final int netSetID;
        private final int[] cellParas;
        private final EnumSetCellType[] cellArray;

        private EnumNetType(int netSetID, int[] cellParas, EnumSetCellType[] cellArray) {
            this.netSetID = netSetID;
            this.cellParas = cellParas;
            this.cellArray = cellArray;
        }

        private EnumNetType() {
            this.netSetID = -9999;
            cellArray = new EnumSetCellType[] {};
            cellParas = new int[] {};
        }

        /**
         * 返回当前网络类型的邻集参数ID
         */
        public int getNetSetID() {
            return this.netSetID;
        }

        public EnumSetCellType[] getNetCellArray() {
            return cellArray;
        }

        public int[] getNetCellParas() {
            return cellParas;
        }
    }

    ;

    /**
     * 邻区列表激活集类型
     */
    public enum EnumSetCellType {
        SCTcdmaActive(0), // CDMA 1X
        SCTcdmaCardinate(1), SCTcdmaNeighbor(2), SCTcdmaDetected(3), SCTevdoActive(0), // CDMA EVDO
        SCTevdoCardinate(1), SCTevdoNeighbor(2), SCTevdoDetected(3), SCTumtsActive(0), // WCDMA
        SCTumtsCardinate(1), SCTumtsNeighbor(2), SCTumtsDetected(3), SCTumtsVirtualActiveSet(4), SCTlteActive(0), // LTE
        SCTlteNeighbor(1);

        private final int setCellType;

        private EnumSetCellType(int setType) {
            setCellType = setType;
        }

        /**
         * 获得当前数据集的类型
         */
        public int getSetCellType() {
            return setCellType;
        }
    }

    /**
     * 函数功能： 获取版本信息
     *
     * @return 返回字符串，如“Compile Date: Mar 15 2013, Time=11:14:48”
     */
    public native String getVersion();

    /**
     * 函数功能： 初始化句柄
     * 说明    ： 该函数需要在第一个被调用
     *
     * @param szConfigPath szConfigPath：Pilot.Chp和DataSet_SP.xml所在目录。这2个文件需要随安装包打包下去，所以该路径最好是软件的安装路径
     * @param szLibPath    动态库所在目录，目前用到的动态库有libDevDiag.so、libCommonDecodeDataSet.so
     * @return 成功返回大于0的句柄值，失败返回0
     */
    public native int initHandle(String szConfigPath, String szLibPath);

    /**
     * 函数功能： 释放句柄
     * 说明    ： 该函数需要在程序退出时调用，并且调用此接口后，建议在外部将iDslHandle的值赋为0
     *
     * @param iDslHandle initHandle返回的句柄 引用
     */
    public native void freeHandle(int iDslHandle);

    /**
     * 函数功能： 获取最后一次错误
     * 说明    ： 在调用其他的接口函数出错时调用
     *
     * @param iDslHandle initHandle返回的句柄
     * @return 返回错误原因码
     */
    public native int getLastError(int iDslHandle);

    /**
     * 函数功能： 打开Trace口
     * 说明    ： 程序启动时打开，或开始采集数据前打开
     *
     * @param iDslHandle    initHandle返回的句柄
     * @param iPortID       端口号
     * @param iModuleType   设备芯片类型定义，如“0x9107”，参见Pilot.Chip文件
     * @param szDevicePort  设备端口名，默认值“/dev/ttyUSB0”
     * @param iBaudrate     波特率，默认值152000
     * @param iRxInterval   获取数据周期，默认值20ms
     * @param szLogmaskFile logmask文件全路径，没有此文件时填空串
     * @param szBinFilePath bin文件保存路径。没有此文件时填空串
     * @param bPadSpyer     是否为平板监控.
     * @param iTraceOffset  config_deviceinfo.xml中的TraceOffset项
     * @param iNetLicense   iphone 按bit位置1    TDS LTE CDMA WCDMA GSM;此值为IPHONE用的，此处传0即可
     * @return 1=成功；0=失败，此时可以通过getLastError获取错误原因码
     */
    public native int openTrace(int iDslHandle, int iPortID, int iModuleType, String szDevicePort, int iBaudrate, int iRxInterval, String szLogmaskFile, String szBinFilePath, boolean bPadSpyer, int iTraceOffset, int iNetLicense);

    /**
     * 函数功能： 向端口写入数据，例如logmask
     *
     * @param iDslHandle
     * @param iPortID     模块端口号
     * @param pBuffer     buffer内容，
     * @param iBufferSize buffer长度,须 < 8000
     * @return
     */
    public native int devWritePort(int iDslHandle, int iPortID, byte[] pBuffer, int iBufferSize);

    /**
     * 函数功能： 关闭Trace口
     * 说明    ： 程序退出或者省电时候关闭，关闭后需要重新调用OpenTrace打开设备
     *
     * @param iDslHandle initHandle返回的句柄
     * @param iPortID    端口号
     * @return 1=成功；0=失败，此时可以通过getLastError获取错误原因码
     */
    public native int closeTrace(int iDslHandle, int iPortID);

    /**
     * 函数功能： 打开解码器
     * 说明    ： TRACE口打开以后，调用此函数初始化数据集，开始解码数据
     *
     * @param iDslHandle     initHandle返回的句柄
     * @param szStoragePath  数据集生成的临时文件所在目录，建议在sdcard上 ,此目录后续存储开始测试时生成的ddib文件路径
     * @param szGPSModelName GPS设备名，空结尾字符串，如“NMEA0183”
     * @return >=0 成功；<0 失败，此时可以通过getLastError获取错误原因码
     */
    public native int openDecoder(int iDslHandle, String szStoragePath, String szGPSModelName);

    /**
     * 函数功能： 增加模块
     *
     * @param iDslHandle        Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @param iPortID           模块端口号
     * @param szDeviceModelName
     * @return 成功>=0 ; 失败<0
     */
    public native int addMoudle(int iDslHandle, int iPortID, String szDeviceModelName);

    /**
     * 函数功能： 删除模块
     *
     * @param iDslHandle Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @param iPortID    模块端口号
     * @return 成功>=0 ; 失败<0
     */
    public native int delMoudle(int iDslHandle, int iPortID);

    /**
     * 函数功能： 配置数据集解码库
     *
     * @param iDslHandle        Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @param bIsFilterBinary   解码库是否过滤
     * @param bIsSampleBinary   解码库是否精简
     * @param OriginalFileType  数据集数据输入类型，RCU = 1， DCF = 3
     * @param OutputFileType    数据集数据输出类型，RCU = 1， DCF = 3
     * @param DataTimeIsUTCTime 是否UTC时间
     * @return 成功>=0 ; 失败<0
     */
    public native int configDecoder(int iDslHandle, boolean bIsFilterBinary, boolean bIsSampleBinary, int OriginalFileType, int OutputFileType, boolean DataTimeIsUTCTime);

    /**
     * 函数功能： 通知数据集无新数据再压入数据集
     *
     * @param iDslHandle Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @return 成功>=0 ; 失败<0
     */
    public native int finishDataToDecoder(int iDslHandle);

    /**
     * 函数功能： RCU转共享格式DCF
     *
     * @param iDslHandle       Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @param szSrcPath        需要转换的RCU文件路径名
     * @param szDstPath        转换后dcf格式文件路径名
     * @param szDecompressPath 解码缩需要的缓存路径
     * @param szStoragePath    数据集生成的临时文件所在目录，建议在sdcard上
     * @return 返回值 ： 成功>=0 ; 失败<0 参数 ： iDslHandle
     */
    public native int DSLRcuToDcf(int iDslHandle, String szSrcPath, String szDstPath, String szDecompressPath, String szStoragePath);

    /**
     * 函数功能： 开始测试之前添加要保存的文件类型
     * 说明    ： 每次StartTest之前调用添加，若要生成多个文件类型则调用多次
     *
     * @param iDslHandle    initHandle返回的句柄
     * @param iSaveFileType 要生成的文件类型，参考数据集文档
     * @return 成功>=0 ; 失败<0
     */
    public native int addSaveFileType(int iDslHandle, int iSaveFileType);

    /**
     * 函数功能： 开始测试
     * (实际上是创建文件，但此时采样点序号等会归零，测试显示参数事件处需要处理)
     * 说明    ： 开始执行业务测试时，调用此函数，开始采集有效数据
     *
     * @param iDslHandle initHandle返回的句柄
     * @param szFileName 测试期间生成的ddib&rcu文件名（无后缀），如“20130309112233”
     * @return 1=成功；0=失败，此时可以通过getLastError获取错误原因码
     */
    public native int startTest(int iDslHandle, String szFileName);

    /**
     * 函数功能： 配置本次业务对某些字段的过滤周期或结构是否存储等
     *
     * @param iDslHandle
     * @param iPortID       iPortID 端口号
     * @param PropertyKey   PropertyKey   = 1003 解码库参数过滤间隔
     *                      PropertyKey   = 5    不存储的特殊结构分组,  没使用此配置，默认为全存储；有配置，则不存储所属分组的结构体
     * @param PropertyValue PropertyValue 配置项值 //说明    ：
     * @return 成功>=0 ; 失败<0
     */
    public native int configDecodeOrStorageProperty(int iDslHandle, int iPortID, int PropertyKey, long PropertyValue);

    /**
     * configDecodeOrStorageStringProperty
     * 函数功能：指定生成的文件的设备信息，如RCU里的GUID或者DTLog文件的BoxId
     *
     * @param iDslHandle
     * @param iPortID
     * @param PropertyKey   数据集协商过指定为0
     * @param PropertyValue BoxId或者是GUID
     * @return
     */
    public native int configDecodeOrStorageStringProperty(int iDslHandle, int iPortID, int PropertyKey, String PropertyValue);

    /**
     * configDecodeOrStorageComplexProperty
     * 函数功能:特殊函数,往model口写入byte[]流信息
     *
     * @param iDslHandle
     * @param iPortID
     * @param PropertyKey
     * @param PropertyValue
     * @return
     */
    public native int configDecodeOrStorageComplexProperty(int iDslHandle, int iPortID, int PropertyKey, byte[] PropertyValue);

    /**
     * 函数功能： 获取解码后文件(ddib)全路径
     *
     * @param iDslHandle DSL_InitHandle返回的句柄
     * @param iPortID    模块端口号 如果是0，代表GPS
     * @return 路径存放首地址, 实际pPath长度
     */
    public native String getDecodedIndexFileName(int iDslHandle, int iPortID);

    /**
     * 函数功能： 停止业务
     * 说明    ： 测试完成之后，调用此函数，停止记录解码数据，并生成文件
     *
     * @param iDslHandle Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @return 成功>=0 ; 失败<0	,此时可以通过getLastError获取错误原因码
     */
    public native int stopTest(int iDslHandle);

    /**
     * 函数功能： 业务中暂停保存文件//业务开始时默认是记录文件的
     *
     * @param iDslHandle
     * @param iPortID    端口号
     * @param bSave      False: 暂停记录文件；True: 记录文件
     * @return 成功>=0 ; 失败<0
     */
    public native int setSaveFile(int iDslHandle, int iPortID, boolean bSave);

    /**
     * 函数功能： 压数据给数据集,一般压事件
     * 说明    ： 业务测试期间调用
     *
     * @param iDslHandle  Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @param iPortID     模块端口号 如果是0，代表GPS
     * @param iFlag       类型标识，比如'D','E'等
     * @param i64Time     数据包时间，到1970年1月1日0：0：0秒的微秒数
     * @param pBuffer     数据包缓存
     * @param iBufferSize 数据包缓存大小（buffer的实际长度，不包括flag，module，time）
     * @param iPointIndex 生成事件要往前插到的采样点索引，默认为-9999时插到最后一个采样点索引
     * @return 1=成功；0=失败，此时可以通过Java_com_dinglicom_DataSetLib_getLastError获取错误原因码
     */
    public native int pushData(int iDslHandle, int iPortID, int iFlag, long i64Time, byte[] pBuffer, int iBufferSize, int iPointIndex);

    /**
     * 外部压入Trace数据
     * 主要用于平板监控等，测试数据从外部压入
     *
     * @param iDslHandle  Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @param iPortID     模块端口号
     * @param i64Time     时间，从1970开始的微秒数
     * @param buffer      压入的数据
     * @param iBufferSize 压入的数据长度
     * @return >=0 sucess, <0 error
     */
    public native int pushExternalTraceData(int iDslHandle, int iPortID, long i64Time, byte[] buffer, int iBufferSize);

    /**
     * 函数功能： 添加用户自定义事件
     *
     * @param iDslHandle      DSL_InitHandle返回的句柄
     * @param iPortID         模块端口号 如果是0，代表GPS
     * @param iEventFlag      自定义事件ID（0x0010 0000至0x00FF FFFF之间，否则返回值为-1）；
     * @param iPointIndex     事件对应的采样点序号；
     * @param iEvent64Time    事件时间，到1970年1月1日0：0：0秒的微秒数；
     * @param szEventDescribe 事件的描述，调用GetEventDetailsByEventFlagAndIndex时的返回信息；
     * @return 成功返回1，失败返回-1
     */
    public native int pushCustomEventData(int iDslHandle, int iPortID, int iEventFlag, int iPointIndex, long iEvent64Time, String szEventDescribe);

    /**
     * 函数功能： 获取采样点总数
     *
     * @param iDslHandle initHandle返回的句柄
     * @param iPort      端口号
     * @return 当前实际采样点的个数
     */
    public native int getTotalPointCount(int iDslHandle, int iPort);

    /**
     * 函数功能： 是否为室内打点
     *
     * @param iDslHandle DSL_InitHandle返回的句柄
     * @param iPortID    端口号
     * @return true:室内打点 false: gps点
     */
    public native boolean haveIndoorMarked(int iDslHandle, int iPortID);

    /**
     * 函数功能： 关闭解码器
     * 说明    ： 解码过程结束时调用此接口
     *
     * @param iDslHandle initHandle返回的句柄
     * @return 1=成功；0=失败，此时可以通过getLastError获取错误原因码
     */
    public native int closeDecoder(int iDslHandle);

    /**
     * 函数功能： 打开文件回放,开始回放时，调用此函数
     *
     * @param iDslHandle     initHandle返回的句柄
     * @param portID         模块端口号
     * @param szFileFullPath 目标ddib文件全路径，如“/data/local/ddib/20130309.ddib”
     * @return 1=成功；0=失败，此时可以通过getLastError获取错误原因码
     */
    public native int openPlayback(int iDslHandle, int portID, String szFileFullPath);

    /**
     * 函数功能： 结束回放
     * 说明    ： 回放过程结束时调用此接口
     *
     * @param iDslHandle Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @param portID     模块端口号
     * @return 1=成功；0=失败，此时可以通过Java_com_dinglicom_DataSetLib_getLastError获取错误原因码
     */
    public native int closePlayback(int iDslHandle, int portID);

    /**
     * 函数功能： 通过时间定位采样点序号
     * 按信令时间二分查找定位采样点序号
     *
     * @param iDslHandle Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @param iPortID    端口号
     * @param iTime      电脑本地时间
     * @return 返回值 ： 采样点序号
     */
    public native int getPointIndexFromTime(int iDslHandle, int iPortID, long i64Time);

    /**
     * 函数功能： 获取信令编码
     *
     * @param iDslHandle      initHandle返回的句柄
     * @param iPort           端口号
     * @param iFromPointIndex 起始采样点索引
     * @param iToPointIndex   终止采样点索引
     * @param bIsUTCTime      true=UTC时间，false=非UTC时间
     * @param bIsHandsetTime  true=信令时间，false=电脑时间
     * @return 字符串，格式如“PointIndex1@@time1@@code1@@direction1##PointIndex2@@time2@@code2
     * @@direction2...”
     */
    public native String getMsgCode(int iDslHandle, int iPort, int iFromPointIndex, int iToPointIndex, boolean bIsUTCTime, boolean bIsHandsetTime);

    /**
     * 函数功能： 获取一个参数的非继承点的总个数
     * 说明    :  测试用不能使用
     *
     * @param iDslHandle    DSL_InitHandle返回的句柄
     * @param iPortID        端口号
     * @param AParamKey    参数ID
     * @return 返回个数
     */
    // public native int getParamRealValueTotalCount(int iDslHandle, int iPortID, int
    // AParamKey);

    /**
     * 函数功能： 获取一个参数的第n个非继承点的采样点序号
     * 说明    :  测试用不能使用
     *
     * @param iDslHandle    DSL_InitHandle返回的句柄
     * @param iPortID        端口号
     * @param AParamKey    参数ID
     * @param AIndex        参数非继承点的秩（第n个非继承点）
     * @return 返回采样点序号
     */
    // public native int getPointIndexByParamRealValueIndex(int iDslHandle, int iPortID, int
    // AParamKey, int AIndex);

    /**
     * 函数功能： 获取实际参数值信息
     *
     * @param iDslHandle DSL_InitHandle返回的句柄
     * @param iPortID    端口号
     * @param PointIndex 采样点序号；
     * @param ParamKey   参数ID；
     * @param Filter     非继承值，即实际解码值，False： 继承值；
     * @return 参数实际值，无效值：-9999
     */
    public native double getParamRealValue(int iDslHandle, int iPortID, int PointIndex, int ParamKey, boolean Filter);

    /**
     * 函数功能： 查询参数值信息（实际值）
     *
     * @param iDslHandle      Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @param iPort           模块端口号
     * @param iParamKey       参数ID
     * @param iFromPointIndex 起始采样点索引
     * @param iToPointIndex   终止采样点索引
     * @param bFilter         true=非继承值（即实际解码值），false=继承值
     * @param bIsStatistics   是否过滤无效值
     * @return 字符串，格式如“value1@@value2@@value3@@value4...”
     */
    public native String getRealParam(int iDslHandle, int iPort, int iParamKey, int iFromPointIndex, int iToPointIndex, boolean bFilter, boolean bIsStatistics);

    /**
     * 函数功能： 查询参数值信息（实际值）
     * 说明：此方法的返回值中带着采样点序号
     *
     * @param iDslHandle      Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @param iPort           模块端口号
     * @param iParamKey       参数ID
     * @param iFromPointIndex 起始采样点索引
     * @param iToPointIndex   终止采样点索引
     * @param bFilter         true=非继承值（即实际解码值），false=继承值
     * @param bIsStatistics   是否过滤无效值
     * @return 返回值 ： 字符串，格式如“pointIndex,value1@@pointIndex,value2@@pointIndex,value3@@pointIndex,
     * value4...”
     */
    public native String getRealParam2(int iDslHandle, int iPort, int iParamKey, int iFromPointIndex, int iToPointIndex, boolean bFilter, boolean bIsStatistics);

    /**
     * 批量查询参数值信息（实际值）
     * <p>
     * 说明 ：这个函数也适合GSM邻小区和TD邻小区的数据请求（每个小区的值都有key）
     *
     * @param iDslHandle    Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @param iPort         模块端口号
     * @param pParamKeyList 参数ID列表
     * @param iListCount    参数个数
     * @param iPointIndex   采样点索引
     * @param bFilter       true=非继承值（即实际解码值），false=继承值
     * @return 字符串，格式如“value1@@value2@@value3@@value4...”
     */
    public native String batchGetRealParam(int iDslHandle, int iPort, int[] pParamKeyList, int iListCount, int iPointIndex, boolean bFilter);

    /**
     * 函数功能： 获取集合小区个数
     *
     * @param iDslHandle   Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @param port         模块端口号
     * @param eNetType     网络类型，参见EnumNetType定义
     * @param eSetCellType 小区类型，参见EnumSetCellType定义
     * @param iPointIndex  采样点索引
     * @param bEVDO        是否为EVDO，只在CDMA网络生效
     * @param bFilter      true=非继承值（即实际解码值），false=继承值
     * @return 返回值  ： 集合小区个数
     */
    public native int getSetCellCount(int iDslHandle, int port, int eNetType, int eSetCellType, int iPointIndex, boolean bEVDO, boolean bFilter);

    /**
     * 函数功能： 获得集合小区参数
     *
     * @param iDslHandle    Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @param port          模块端口号
     * @param pParamKeyList 参数列表
     * @param iListCount    参数个数
     * @param eNetType      网络类型，参见EnumNetType定义
     * @param eSetCellType  小区类型，参见EnumSetCellType定义
     * @param iPointIndex   采样点索引
     * @param iItemIndex    请求的参数的索引
     * @param bEVDO         是否为EVDO，只在CDMA网络生效
     * @param bFilter       true=非继承值（即实际解码值），false=继承值
     * @return 返回值  ： 字符串，格式如“value1@@value2@@value3@@value4...”
     */
    public native String getSetCellParam(int iDslHandle, int port, int[] pParamKeyList, int iListCount, int eNetType, int eSetCellType, int iPointIndex, int iItemIndex, boolean bEVDO, boolean bFilter);

    /**
     * 函数功能： 获取事件总数
     *
     * @param iDslHandle Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @param iPort      模块端口号
     * @return 当前事件的个数
     */
    public native int getEventCount(int iDslHandle, int iPort);

    /**
     * 函数功能： 获取事件总数
     *
     * @param iDslHandle Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @param iPort      模块端口号
     * @return 返回值 ： 当前事件的个数
     */
    public native int getEventTotalCount(int iDslHandle, int iPort);

    /**
     * 函数功能： 				获取事件
     * 说明    ： 在特殊情况下，比如MOS的时候，算分的事件会滞后
     * 返回的eventFlag是只有低4位的，比如 Ping_Start对应的值为 0x0190
     * 所以在和本地进行比较的时候，需要将本地的eventFlag取低4位进行比较
     *
     * @param iDslHandle      Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @param iPort           模块端口号
     * @param iFromEventIndex 起始事件索引
     * @param iToEventIndex   终止事件索引
     * @param bIsUTCTime      true=UTC时间，false=非UTC时间
     * @param iTimeType       0=电脑时间，1=信令时间，2=事件时间
     * @return 字符串，返回值，格式如“time1@@eventIndex1@@eventFlag1@@pointindex1##time2@@eventIndex2
     * @@eventFlag2@@pointindex2...”
     */
    public native String getEvent(int iDslHandle, int iPort, int iFromEventIndex, int iToEventIndex, boolean bIsUTCTime, int iTimeType);

    /**
     * 函数功能：			 获取事件的属性信息
     *
     * @param iDslHandle  Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @param iPort       模块端口号
     * @param iEventIndex 输入eventIndex事件索引
     * @param iEventFlag  事件码
     * @return 字符串，事件的属性，如果得不到值则为空，比如 请求FTP_DL_Drop的属性则返回“时间=200s\r\n实际下载大小=1200Byte\r\n原因：超时”
     */
    public native String getEventDesc(int iDslHandle, int iPort, int iEventIndex, int iEventFlag);

    /**
     * 函数功能：				 获取采样点区间的事件
     *
     * @param iDslHandle      DSL_InitHandle返回的句柄
     * @param iPortID         端口号
     * @param iFromPointIndex 起始采样点索引
     * @param iToPointIndex   终止采样点索引
     * @return 返回个数 ,格式如“time1@@EventIndex1@@pointindex1@@EventFlag1##time1@@EventIndex2
     * @@pointindex2@@EventFlag2...”
     */
    public native String getPointsEvent(int iDslHandle, int iPortID, int iFromPointIndex, int iToPointIndex);

    /**
     * 函数功能：			 获取信令详细解码信息
     *
     * @param iDslHandle  DSL_InitHandle返回的句柄
     * @param iPortID     端口号
     * @param iPointIndex 采样点索引
     * @return 返回值 ： 字符串，信令详细解码信息，内容格式为XML，如 "<Message><nodetail></nodetail></Message>"，如果得不到值则为空
     */
    public native String getDetailMsg(int iDslHandle, int iPortID, int iPointIndex);

    /**
     * 函数功能： 获取特殊结构体系列化内存字节数组
     *
     * @param iDslHandle  Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @param iPortID     模块端口号
     * @param iPointIndex 采样点索引
     * @param iSpecialKey 特殊结构ID 可参考解码组文档定义，或 数据集特殊结构存储文档.doc
     * @return 数据集存储特殊结构字节数组
     */
    public native byte[] getSpecialStructInfo(int iDslHandle, int iPortID, int iPointIndex, int iSpecialKey);

    /**
     * 获得从串信出来的源始信令流（2018.07.19:解析特殊结构体应调用此方法）
     *
     * @param iDslHandle
     * @param iPortID
     * @param iPointIndex
     * @param iSpecialKey
     * @return
     */
    public native byte[] getOriginalStructInfo(int iDslHandle, int iPortID, int iPointIndex, int iSpecialKey);

    /**
     * 函数功能： 获取指定采样点GPS经纬度
     *
     * @param iDslHandle  DSL_InitHandle返回的句柄
     * @param iPortID     端口号
     * @param iPointIndex 采样点索引
     * @return 字符串，返回值，格式如"longitude@@latitude"
     */
    public native String getGPSValue(int iDslHandle, int iPortID, int iPointIndex);

    /**
     * 函数功能： 获取GPS实际点总数
     *
     * @param iDslHandle DSL_InitHandle返回的句柄
     * @param iPortID    端口号
     * @return GPS实际点的个数
     */
    public native int getRealGPSPointCount(int iDslHandle, int iPortID);

    /**
     * 函数功能： 获取GPS实际点信息
     * 说明    ： 为了提高效率C接口不采用字符串给出
     *
     * @param iDslHandle DSL_InitHandle返回的句柄
     * @param iPortID    端口号
     * @param iGPSIndex  GPS点序号
     * @return 格式： “DateTime@@longitude@@latitude@@Altiutde@@Speed@@SatelliteNum@@GroundDegree”
     */
    public native String getRealGPSPointInfo(int iDslHandle, int iPortID, int iGPSIndex);

    /**
     * 函数功能： 根据GPS点序号获取采样点区间
     * 说明    ： 为了提高效率C接口不采用字符串给出
     *
     * @param iDslHandle DSL_InitHandle返回的句柄
     * @param iPortID    端口号
     * @param iGPSIndex  GPS点序号
     * @return 格式：“BeginPointIndex@@EndPointIndex”
     */
    public native String getPointIndexZoneByGPSIndex(int iDslHandle, int iPortID, int iGPSIndex);

    /**
     * 函数功能： 根据GPS点序号获取采样点
     *
     * @param iDslHandle DSL_InitHandle返回的句柄
     * @param iPortID    端口号
     * @param iGPSIndex  GPS点序号
     * @return 返回值 ： >=0的采样点序号。-9999为无效值
     */
    public native int getPointIndexByGPSIndex(int iDslHandle, int iPortID, int iGPSIndex);

    /**
     * 函数功能：转换RCU文件到
     *
     * @param iDslHandle
     * @param srcFilePath      rcu文件
     * @param desFilePath      目前文件
     * @param iDstFileType     文件类型
     * @param szDecompressPath 解码缩需要的缓存路径
     * @param szStoragePath    数据集生成的临时文件所在目录，建议在sdcard上
     * @return
     */
    public native int rcuConverter(int iDslHandle, String srcFilePath, String desFilePath, int iDstFileType, String szDecompressPath, String szStoragePath);

    /**
     * 函数功能： 获取采样点时间
     *
     * @param iDslHandle     DSL_InitHandle返回的句柄
     * @param iPortID        端口号
     * @param iPointIndex    采样点索引
     * @param bIsUTCTime     true=UTC时间，false=非UTC时间
     * @param bIsHandsetTime true=信令时间，false=电脑时间
     * @return I64时间
     */
    public native long getPointTime(int iDslHandle, int iPortID, int iPointIndex, boolean bIsUTCTime, boolean bIsHandsetTime);

    /**
     * 函数功能： 导出文件
     *
     * @param iDslHandle   DSL_InitHandle返回的句柄
     * @param iPortID      端口号
     * @param szDstPath    导出文件路径名
     * @param config       //bit位标识：bool bGPS, bool bGsmPara, bool bWcdmaPara, bool bTDSDMAPara,
     *                     bool bLTEPara, bool bEvent,bool bL3
     *                     6		5				4					3			2
     *                     1		0
     * @param szFileHeader 要写入的文件头
     * @param iSplitSize   文件分割大小
     * @param szSeparator  分隔符
     * @param jlongArray   jMsgIDs,       //
     *                     jint * @param MsgIDCount //MsgIDCount == 0时，层三不过滤
     * @return 成功>=0 ; 失败<0
     */
    public native boolean exportTable(int iDslHandle, int iPortID, String szDstPath, int config, int iFromPointIndex, int iToPointIndex, String szFileHeader, int iSplitSize, String szSeparator, long[] jMsgIDs, int MsgIDCount);

    /**
     * 函数功能： 导出子文件
     * 说明    ： 仅支持ddib
     *
     * @param iDslHandle      DSL_InitHandle返回的句柄
     * @param iPortID         端口号
     * @param szDstPath       导出文件路径名
     * @param iFromPointIndex 起始采样点索引
     * @param iToPointIndex   终止采样点索引
     * @return 成功>=0 ; 失败<0
     */
    public native int exportSubDdibFile(int iDslHandle, int iPortID, String szDstPath, int iFromPointIndex, int iToPointIndex);

    /**
     * 强制小区和频点功能 ，非数据集函数
     *
     * @param iDslHandle DSL_InitHandle返回的句柄
     * @param iPortID    端口号
     * @param moduleType
     * @param pBuffer
     * @param bufferSize
     * @return
     */
    public native int forceCommand(int iDslHandle, int iPortID, int moduleType, byte[] pBuffer, int bufferSize);

    // ################################统计分析项##################################

    /**
     * 函数功能： 获取存在业务过程类型标识
     *
     * @param iDslHandle 数据集句柄
     *                   pDestList：	预分配整数数组首址
     *                   DestCount：	分配整数数组个数
     * @param iPortID
     * @param SPType     0所有业务，1只包含应用层业务，2只包含信令层业务
     * @return 返回字符串  "4097@@4098"
     * Common_SP_ID_MO             = 0x1001, //主叫
     * Common_SP_ID_MT             = 0x1002,  //被叫
     */
    public native String getExistSPList(int iDslHandle, int iPortID, int SPType);

    /**
     *
     */

    /**
     * 函数功能： 获取指定类型业务个数
     *
     * @param iDslHandle
     * @param iPortID
     * @param SPID
     * @return 返回值 ： 成功返回1
     */
    public native int getAppointSPCount(int iDslHandle, int iPortID, int SPID);

    /**
     * 函数功能： 计算业务过程属性值主
     * 此方法没有反回值,但需要执行后方可调后面的方法
     *
     * @param iDslHandle
     * @param iPortID
     * @param SPID           业务ID
     * @param ConditionKey   APP_SP_Sta_t为前缀定义的常量,参考《业务过程属性(按业务过程分类).xlsx》，必须指定(csfb: 84)
     *                       APP_SP_Stat_CalcCSFBInfo(84)
     * @param ConditionValue 有些APP_SP_Stat_需要指定值，一般情况下为-9999
     * @return 成功返回1
     */
    public native int calcSPSinglePropertyValue(int iDslHandle, int iPortID, int SPID, int ConditionKey, long ConditionValue);

    /**
     * 函数功能： 获取业务过程属性值（非字符串）
     *
     * @param iDslHandle
     * @param iPortID
     * @param SPID       业务ID
     * @param Index
     * @param PropertyID SP_Property_ID_CSFBExceptionReason(173)
     *                   返回:CSFB异常分析，0：未配置CSFB，1：超时，2：配置网络与起呼网络不一致
     *                   SP_Property_ID_ReturnToLTEFailureReason(174)返回:Return Back To
     *                   LTE失败原因，0：TAUReject，1：TAUTimeout
     *                   当前方法要根据错误类类多次调用
     * @return -1获取失败
     */
    public native double getSPPropertyDoubleValue(int iDslHandle, int iPortID, int SPID, int Index, int PropertyID);

    /**
     * 函数功能： 获取业务过程属性值（字符串）
     *
     * @param iDslHandle
     * @param iPortID
     * @param SPID       业务ID
     * @param Index
     * @param PropertyID
     * @return 成功返回1
     */
    public native String getSPPropertyStringValue(int iDslHandle, int iPortID, int SPID, int Index, int PropertyID);

    /**
     * 函数功能： 业务详情类区间信息
     *
     * @param iDslHandle
     * @param iPortID
     * @param SPID       业务ID
     * @param Index      SPID的第几个实例（下标从0开始）
     * @param bStat      标示StartPoint和EndPoint是否是按统计规则下的采样点
     *                   StartPoint：按bStat的值，返出相应的采样点
     *                   EndPoint：	按bStat的值，返出相应的采样点
     * @return 返回字符串 "StartPoint@@EndPoint"
     */
    public native String getSPRangeInfo(int iDslHandle, int iPortID, int SPID, int Index, boolean bStat);

    /**
     * 函数功能： 一个业务详情类实例存在多少个事件
     *
     * @param iDslHandle 数据集句柄
     * @param iPortID    业务ID
     * @param SPID       SPID的第几个实例（下标从0开始）
     * @param Index      事件个数
     * @return 返回 COUNT
     */
    public native int getSPEventCount(int iDslHandle, int iPortID, int SPID, int Index);

    /**
     * 函数功能： 一个业务详情类实例所有事件的EventIndex
     *
     * @param iDslHandle 数据集句柄
     * @param iPortID
     * @param SPID       业务ID
     * @param Index      SPID的第几个实例（下标从0开始）
     *                   EventIndexs：数组首地址，数组长度由GetSPEventCount()得到，调用者负责创建释放
     *                   获取每个事件的EvnetIndex后，通过调用事件相关函数，可获取更多的事件信息
     * @return 成功返回1
     */
    public native String getSPEventIndexList(int iDslHandle, int iPortID, int SPID, int Index);

    /**
     * genOtherFormatRealFileFromTempFile
     * 函数功能： 将第三方数据格式的临时文件生成正式文件，这个临时文件是文件结束时转换文件生成的，
     * 转换过程中软件崩溃了就会生成
     *
     * @param iDslHandle
     * @param iPortID
     * @param tempFileName 临时文件
     * @return
     */
    public native int genOtherFormatRealFileFromTempFile(int iDslHandle, int iPortID, String tempFileName);

    /**
     * 配置从设备库获取的数据是否压入到数据集
     *
     * @param iDslHandle
     * @param bPushToDataSet
     * @return 成功>=0 ; 失败<0
     */
    public native int configPushToDataSet(int iDslHandle, boolean bPushToDataSet);

    /**
     * 函数功能： 配置是否以进程方式获取Trace，不配置则默认以非进程方式
     * root权限不够时可使用此方式
     *
     * @param iDslHandle Java_com_dinglicom_DataSetLib_configIPCDiag
     * @param bIPCDiag   bIPCDiag : 是否以进程方式获取trace
     * @return 返回值 ： 成功>=0 ; 失败<0
     */
    public native int configIPCDiag(int iDslHandle, boolean bIPCDiag);

    /**
     * 函数功能： 获取指定采样点所处的业务类型列表
     * 仅回放中使用
     *
     * @param iDslHandle
     * @param iPortID
     * @param APointIndex APointIndex 采样点号
     * @return 返回值 ： 业务类型格式串，例：3@@01##02##03
     */
    public native String getAppointPointIndexOwnedSPList(int iDslHandle, int iPortID, int APointIndex);

    /**
     * RCU文件格式转换前初始化
     * 该方法为做文件设换时设能设置过滤频率添而添加
     * 过滤频率设置在此方法之前,转换之前添加
     *
     * @param iDslHandle       Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @param szSrcPath        需要转换的RCU文件路径名, 转换后文件名为转换前文件名_Portx.xxx
     * @param iSrcFileType     输入szSrcPath的文件格式
     * @param iDstFileType     转换后文件格式
     * @param szDecompressPath 解码缩需要的缓存路径
     * @param szStoragePath    数据集生成的临时文件所在目录，建议在sdcard上
     * @return 无
     */
    public native int fileConverterInit(int iDslHandle, String szSrcPath, int iSrcFileType, int iDstFileType, String szDecompressPath, String szStoragePath);

    /**
     * FMT函数功能： 文件格式转换
     *
     * @param iDslHandle
     * @param szSrcPath        需要转换的RCU文件路径名, 转换后文件名为转换前文件名_Portx.xxx
     * @param iSrcFileType     输入szSrcPath的文件格式
     * @param iDstFileType     转换后文件格式
     * @param szDecompressPath 解码缩需要的缓存路径
     * @param szStoragePath    数据集生成的临时文件所在目录，建议在sdcard上
     * @return
     */
    public native int fileConverter(int iDslHandle, String szSrcPath, int iSrcFileType, int iDstFileType, String szDecompressPath, String szStoragePath);

    /**
     * 函数功能： 注册实时通知
     * 这个方法目前库没有开放,如需用于需朱公子开放
     *
     * @param iDslHandle Java_com_dinglicom_DataSetLib_initHandle返回的句柄
     * @param iPortID    端口号
     * @param iInfoType  1->代表参数 2->代表信令 3 -->结构 4 --> 文件大小(后在的参数传文件类型)
     * @param lInfoID    参数ID或者信令ID
     * @return 返回值 ： 成功>=0 ; 失败<0
     */
    public native int regObserverInfo(int iDslHandle, int iPortID, int iInfoType, long lInfoID);

    /**
     * 函数功能： scanner访问获取采样点上对应业务数据块数
     *
     * @param iDslHandle initHandle返回的句柄
     * @param iPortID    模块端口号
     * @param ScanID     scan业务ID
     * @return Item个数
     */
    public native int getStructItemCount(int iDslHandle, int iPortID, int iPointIndex, boolean bFilter, int ScanID);

    /**
     * 函数功能： 获取采样点上对应业务数据
     *
     * @param iDslHandle  initHandle返回的句柄
     * @param iPortID     模块端口号
     * @param iPointIndex 采样点序号
     * @param bool        bFilter,//True: 非继承值，即实际解码值，False：继承值
     * @param ScanID      scan业务ID
     * @param iItemIndex  GetStructItemCount索引
     * @return 业务数据
     */
    public native byte[] getStructItem(int iDslHandle, int iPortID, int iPointIndex, boolean bFilter, int ScanID, int iItemIndex);

    /**
     * 函数功能： 获取item对应subitem块个数
     *
     * @param iDslHandle  initHandle返回的句柄
     * @param iPortID     模块端口号
     * @param iPointIndex 采样点序号
     * @param bool        bFilter,//True: 非继承值，即实际解码值，False：继承值
     * @param ScanID      scan业务ID
     * @param iItemIndex  GetStructItemCount索引
     * @return subitem个数
     */
    public native int getStructSubItemCount(int iDslHandle, int iPortID, int iPointIndex, boolean bFilter, int ScanID, int iItemIndex);

    /**
     * 函数功能： 获取subitem块
     *
     * @param iDslHandle    initHandle返回的句柄
     * @param iPortID       模块端口号
     * @param iPointIndex   采样点序号
     * @param bool          bFilter,//True: 非继承值，即实际解码值，False：继承值
     * @param ScanID        scan业务ID
     * @param iItemIndex    GetStructItemCount索引
     * @param iSubItemIndex getStructSubItemCount索引
     * @return 业务数据
     */
    public native byte[] getStructSubItem(int iDslHandle, int iPortID, int iPointIndex, boolean bFilter, int ScanID, int iItemIndex, int iSubItemIndex);

    /**
     * 函数功能： 设置scanner返回结构体内排序, 要在其他业务访问接口函数调用前调用，排序函数才有效
     *
     * @param iDslHandle initHandle返回的句柄
     * @param iPortID    模块端口号
     * @param ScanID     scan业务ID
     * @param ParamID    指定按哪个参数排序
     * @return 成功返回1
     */
    public native int SetScannerItemSort(int iDslHandle, int iPortID, int ScanID, int ParamID);

    /**
     * 海思锁频频网络接口
     *
     * @param iDslHandle
     * @param iPortID
     * @param type       eATNULL = 0,
     *                   eATLTE_PowerON,		//不带参数
     *                   eATLTE_PowerOFF,	//不带参数
     *                   eATLTE_Attach,		//不带参数
     *                   eATLTE_Detach,		//不带参数
     *                   eATLTE_GetIP,		//不带参数
     *                   eATLTE_ResetDev,	//不带参数
     *                   eATLTE_ReleaseIP,	//不带参数
     *                   eATLTE_SetAPN,		//暂不做
     *                   eATLTE_SetIMSAPN,	//暂不做
     *                   eATLTE_SetDefualtAPN,//暂不做
     *                   eAT_LockNetwork, 	//带参数: Network Type=xx\r\n
     *                   eATLTE_LockBand,	//带参数：Band=xx\r\n
     *                   eTSLTE_LockFreq,	//带参数：Band=xx\r\nEARFCN=xx\r\n
     *                   eTSLTE_LockCell,	//带参数：Band=xx\r\nEARFCN=xx\r\nPCI=xx\r\n
     *                   eTSLTE_Unlock,		//不带参数
     *                   eTSLTE_HandoverReq,	//暂不做
     *                   eTSLTE_CselReq,		//暂不做
     *                   eTSLTE_BarCellAccessReq,//暂不做
     *                   eAGSM_LockBand,		//带参数：Band=xx\r\n
     *                   eAGSM_LockFreq,		//带参数：Band=xx\r\nARFCN=xx\r\n
     *                   eAGSM_UnLock,		//不带参数
     *                   eAWcdma_LockFreq,	//暂不做
     *                   eAWcdma_LockPSC,	//暂不做
     *                   eAWcdma_Unlock,		//暂不做
     *                   eUEMoodifyNVParam,	//暂不做
     * @param pBuffer
     * @param bufferSize
     * @return
     */
    public native int devWritePortExt(int iDslHandle, int iPortID, int type, byte[] pBuffer, int bufferSize);

    /**
     * 事件属性值EventProperty
     *
     * @param iDslHandle DataSetHandle：数据集句柄
     * @param iPortID    EventIndex：一个事件实例的全局索引
     * @param EventIndex PropertyID：事件属性ID
     * @param PropertyID Value：PropertyID对应的值
     * @return
     */
    public native long getEventPropertyValue(int iDslHandle, int iPortID, int EventIndex, int PropertyID);

    /**
     * 为移动招标自定义导出设置参数过滤，增加接口
     *
     * @param iDslHandle
     * @param iPortID
     * @param szDstPath
     * @param config
     * @param iFromPointIndex
     * @param iToPointIndex
     * @param szFileHeader
     * @param iSplitSize
     * @param szSeparator
     * @param jMsgIDs
     * @param MsgIDCount
     * @param paramIDs
     * @return
     */
    public native boolean exportTableExt(int iDslHandle, int iPortID, String szDstPath, int config, int iFromPointIndex, int iToPointIndex, String szFileHeader, int iSplitSize, String szSeparator, long[] jMsgIDs, int MsgIDCount, long[] paramIDs);

    /**
     * 函数功能： 恢复崩溃文件
     *
     * @param iDslHandle
     * @param szFileFullPath 崩溃的ddib文件全路径，如“/data/local/ddib/20130309.ddib”,
     *                       如果ddib文件(ddi+ddb格式还没添加备份功能)在生成时异常中断，且在FileName同目录下有索引备份文件存在(*
     *                       .Indexbak)文件存在,会尝试恢复
     * @return True成功，如果是*.ddi与*.ddb，则必须两个文件同时存在;False失败，如果是*.ddib，则有可能是测试或重解码过程中中断了
     */
    public native boolean CheckDecodeFileValid(int iDslHandle, String szFileFullPath);

    /**
     * @param iDslHandle 函数功能： 获取业务详情类基本信息
     *                   返回值  ： 返回InfoID对应的值
     *                   说明    ：
     *                   参数    :  InfoID 参考EnumSPBaseInfo
     */
    public native long getSPBaseInfoValue(int iDslHandle, int iPortID, int SPID, int index, int InfoID);

    /**
     * 重新配置logmask
     *
     * @param iDslHandle
     * @param iPortID
     * @param logmaskFilePath logmask文件路径
     * @return
     */
    public native int configLogmask(int iDslHandle, int iPortID, String logmaskFilePath);

    /**
     * 配置保存原始rcu数据
     *
     * @param iDslHandle
     * @param saveOrgSource 是否保存原始RCU
     * @param filePath      原始RCU存放跟径
     * @return
     */
    public native int configSaveOrgSource(int iDslHandle, int iPort, boolean saveOrgSource, String filePath);

    /**
     * 设置数据集的日志路径
     *
     * @param iDslHandle 句柄
     * @param logPath    日志路径
     * @return
     */
    public native int setEnvironmentProperty(int iDslHandle, String logPath);

    /**
     * 配置设备库类型
     * eDevDiag_Comm 	= 0
     * eDevDiag_S7 		= 1
     *
     * @param iDslHandle
     * @param iType
     * @return
     */
    public native int configDevDiagType(int iDslHandle, int iType);

    /**
     * 当调用该方法时不管啥值表示当前无效
     *
     * @param iDslHandle
     * @param value
     * @return
     */
    public native int setEnvironmentProperty2(int iDslHandle, int value);

    /**
     * 校验第三方文本动态内容
     *
     * @param iDslHandle
     * @param str
     * @param strLen
     * @return
     */
    public native int setThirdPropertyValue(int iDslHandle, String str, String imei);

    /**
     * 设备信息配置
     *
     * @param iDslHandle
     * @param port
     * @param type       其中0-common,1-s7,2-NB
     * @param ipc
     * @param ipcPath
     * @return
     */
    public native int configIpcValue(int iDslHandle, int port, int type, boolean ipc, String ipcPath);

    /**
     * 函数功能： 设置缓存间隔时长
     *
     * @param iDslHandle 数据集句柄
     * @param interval   间隔时长 微妙，默认是120000000
     * @return 可以不调用
     */
    public native int setCacheTime(int iDslHandle, int interval);

    /**
     * 函数功能： 在DecodeFinish之后，FreeHandle之前调用
     * 返回值  ： 成功返回1,其他返回值的含义，参考《数据集动态库返回值原因码说明文档》文档
     * 获取需要继承的参数信息
     *
     * @param iDslHandle
     * @param portID
     * @return
     */
    public native byte[] getNeedInheritedParamInfo(int iDslHandle, int portID);

    /**
     * 函数功能： 获取事件名称
     * 返回值  ： 成功返回事件名称
     *
     * @param iDslHandle 数据集句柄
     * @param nEventFlag 事件code
     * @return
     */
    public native String getEventName(int iDslHandle, int nEventFlag);

    /**
     * 函数功能： 获取参数信息
     * 返回值  ： 成功返回字符串，szInfo信息："%d::%d::%s::%d::%d::%d", nParamID, nNameSize, szName, nParamScale,
     * nMaxValue, nMinValue
     * 说明    ： nParamId: 参数ID
     *
     * @param iDslHandle 数据集句柄
     * @param nParamId   参数ID
     * @return
     */
    public native String getParamInfo(int iDslHandle, int nParamId);

    /**
     * 函数功能： 获取参数单位
     * 返回值  ： 返出参数单位
     *
     * @param iDslHandle 数据集句柄
     * @param nParamId   参数ID
     * @return
     */
    public native String getUnitName(int iDslHandle, int nParamId);

    /**
     * 函数功能： 判断参数的值是否要转义
     * 返回值  ： 返出是否要转义
     *
     * @param iDslHandle 数据集句柄
     * @param nParamId   参数ID
     * @return
     */
    public native boolean getIsParamValueParseMeaning(int iDslHandle, int nParamId);

    /**
     * 函数功能： 获取参数信息
     * 返回值  ： 成功返回字符串转义内容
     *
     * @param iDslHandle 数据集句柄
     * @param nParamId   参数ID
     * @param value      未除过Scale的参数值
     * @return
     */
    public native String getParamValueParseMeaning(int iDslHandle, int nParamId, long value);

    /**
     * 函数功能：初始化资源库
     * 返回值  ： 成功返回1
     *
     * @param iDslHandle    数据集句柄
     * @param eResourceType 0为参数资源，1为事件资源，2为业务过程资源
     * @param pDbPath       带路径sqlite3资源数据库文件名，空结尾
     * @param pPassword     资源数据库打开密码，没有传空指针，有则以空结尾
     * @return
     */
    public native int initResourceCategory(int iDslHandle, int eResourceType, String pDbPath, String pPassword);



    /**
     * 暂停导出、切换文件
     *
     * 当设置为1是，参数和信令都不刷新，设置为3时恢复刷新
     *
     * @param iDslHandle 数据集句柄
     * @param CommandType 1--暂停导出 2--截断文件 3--恢复导出
     * @param  arg 文件的全路径
     * @param  argLength 文件的全路径的长度
     * @return
     */
    public native int setCommand(int iDslHandle, int CommandType,String arg,int argLength);

}
