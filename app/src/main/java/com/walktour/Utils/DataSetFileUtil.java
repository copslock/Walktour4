package com.walktour.Utils;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;

import com.dingli.samsungvolte.SIPInfoModel;
import com.dingli.samsungvolte.VolteKeyModel;
import com.dinglicom.DataSetLib;
import com.dinglicom.data.control.BuildTestRecord;
import com.dinglicom.data.control.DataTableStruct;
import com.dinglicom.data.control.DataTableStruct.RecordDetailEnum;
import com.dinglicom.data.control.DataTableStruct.RecordInfoKey;
import com.dinglicom.data.control.DataTableStruct.TestRecordEnum;
import com.dinglicom.dataset.DatasetManager;
import com.dinglicom.dataset.TotalInterface;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.Utils.WalkStruct.TestType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.VoiceAnalyse;
import com.walktour.control.bean.FileReader;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.control.config.ServerManager;
import com.walktour.control.instance.DataManagerFileList;
import com.walktour.gui.R;
import com.walktour.gui.indoor.TestInfoValue;
import com.walktour.gui.map.MapFactory;
import com.walktour.model.UmpcTestInfo;
import com.walktour.service.phoneinfo.utils.MobileUtil;

import java.io.File;
import java.text.DecimalFormat;

/**
 * 数据集文件工具类，用于整个系统的数据集文件的创建与关闭
 */
public final class DataSetFileUtil {
    /**
     * TAG标记
     */
    public static String TAG="DataSetFileUtil";
    /**
     * 单例
     */
    public static final DataSetFileUtil instance=new DataSetFileUtil();

    private static final int SPECIALDATT_TYPE_ADBSIP = 1;
    //VOLTE密钥
    private static final int SPECIALDATT_TYPE_SIPKEY = 4;
    /**
     * RCU文件全路径
     */
    private String createRcuFilePath = "";
    /**
     * 全局对象
     */
    private ApplicationModel appModel = ApplicationModel.getInstance();
    private ConfigRoutine routineSet =ConfigRoutine.getInstance();
    private DatasetManager mDatasetMgr = null;
    private ServerManager sManager;
    private BuildTestRecord mTestRecord;
    private MyPhoneState myPhoneState=MyPhoneState.getInstance();
    private String newRcuFileName="";
    private String rcuFileName = "";
    private UmpcTestInfo umpcTestinfo = null;
    //当前次测试的唯时间ID,用于多个文件分割时该值唯一分组
    private String task_no = UtilsMethod.sdfhmsss.format(System.currentTimeMillis());
    private Double rcuFileSizeLimit = 0.0;    //RCU文件大小限制，当该值大于0时表示需要对当前测试次数进行文件分割
    private int rcuFileTimeLimit = 0;    //RCU文件大小按时间限制，当该值大于0时表示需要对当前测试进行分割,时间秒
    private String splitFileRcuName = "";   //用户记录除UMPC测试外，需要做文件分割时第一个RCU名字，当为“”时替换为当前文件名
    private boolean isIndoorTest = false;    //是否室内测试
    private int outCircleTimes = 1;    //外循环次数设置
    private boolean isNetsniffer = false;
    private boolean isUmpcTest = true;                    //如果当前为UMPC测试，以此状态修改最终的RCU文件名
    private DecimalFormat fileNameFormat = new DecimalFormat("000000");
    /**
     * 单机加密文件路径信息
     */
    private final String entryptioneFilePath = Environment.getExternalStorageDirectory().getPath() + "/walktour/config/dingli.wskf";
    /**
     * 密钥文件信息
     */
    private String entryptioneKey = null;
    private int currentFileNum = -1;
    private int rcuFileLimitNum = 1;    //RCU文件分割时序号，从1开始
    private String testTagStr = "";        //测试TAG标题
    /**
     * 私有构造器,防止外部构造
     */
    private DataSetFileUtil() {
        entryptioneKey = new FileReader().getFileText(entryptioneFilePath);
    }

    /**
     * 获取单例
     * @return
     */
    public static DataSetFileUtil getInstance(){
        return instance;
    }


    private void initUmpcTestInfo(Context mContext,String data){
        umpcTestinfo=null;
        umpcTestinfo = new UmpcTestInfo(data);
        rcuFileLimitNum = 1;

        //响应UMPC下发下来是否生成抓包文件
        task_no = umpcTestinfo.getTaskno();
        isNetsniffer = umpcTestinfo.isCatchcap();
        appModel.setControllerType(umpcTestinfo.getController());
        outCircleTimes = umpcTestinfo.getRepeats();
        rcuFileSizeLimit = (double) umpcTestinfo.getDatasize();
        rcuFileTimeLimit = umpcTestinfo.getLimitTime();
        isIndoorTest=umpcTestinfo.getTestmode().equals("CQT")?true:false;
        if (outCircleTimes < 1) {
            outCircleTimes = 1;
        }
        updateFileUploadTypes(mContext,umpcTestinfo.getGenFileTypes());
    }

    /**
     * 更新iPack下发测试任务,上传的日志的格式
     *
     * @param types 类型,每一位代表一个权限
     */
    private void updateFileUploadTypes(Context mContext,char[] types) {

        if (null != types && types.length >= 0) {
            ConfigRoutine rount = ConfigRoutine.getInstance();
            rount.resetFileUploadTypes(mContext);
            if (types.length >= 1 && types[types.length - 1] == '1') {
                rount.setGenDCF(mContext, true);
            }
            if (types.length >= 2 && types[types.length - 2] == '1') {
                rount.setGenOrgRcu(mContext, true);
            }
            if (types.length >= 3 && types[types.length - 3] == '1') {
                rount.setGenDtLog(mContext, true);
            }
            if (types.length >= 4 && types[types.length - 4] == '1') {
                rount.setGenCU(mContext, true);
            }
            if (types.length >= 5 && types[types.length - 5] == '1') {
                rount.setGenRCU(mContext, true);
            }
            if (types.length >= 6 && types[types.length - 6] == '1') {
                rount.setGenECTI(mContext, true);
            }
        }
    }
    public void createFile(Context mContext, int fromScene,String data,boolean hasCall){
        if (isEncrypt()) {
            //系统顺序分配测试记录序号
            currentFileNum = routineSet.getFileNum();
            routineSet.setFileNum(currentFileNum + 1);
        }
        initUmpcTestInfo(mContext,data);
        sManager=ServerManager.getInstance(mContext);
        mDatasetMgr = DatasetManager.getInstance(mContext);
        String rcuFilePath = routineSet.getStorgePathTask();
        appModel.setRcuFileCreated(false);
        TotalDataByGSM.getInstance().initTotalDetail();
        mTestRecord = new BuildTestRecord();
        mTestRecord.setTestRecordMsg(TestRecordEnum.record_id.name(), UtilsMethod.getUUID());
        mTestRecord.setTestRecordMsg(TestRecordEnum.type_scene.name(), fromScene);
        int testTypeId=-1;
        if(isUmpcTest){
            if(isIndoorTest){
                testTypeId=TestType.CQT.getTestTypeId();
            }else{
                testTypeId=TestType.DT.getTestTypeId();
            }
        }else{
            testTypeId=(GpsInfo.getInstance().isJobTestGpsOpen() || GpsInfo.getInstance().isAutoTestGpsOpen()) ? TestType.DT.getTestTypeId() : TestType.CQT.getTestTypeId();
        }
        mTestRecord.setTestRecordMsg(TestRecordEnum.test_type.name(),testTypeId);
        long startRcuTime = System.currentTimeMillis();
        mTestRecord.setTestRecordMsg(TestRecordEnum.time_create.name(), startRcuTime);

        String nameTag;
        //当前是否开启测试的GPS，如果开启，则认为是室内测试加OUT标志，否则默认为室内测试IN
        if (GpsInfo.getInstance().isJobTestGpsOpen() || GpsInfo.getInstance().isAutoTestGpsOpen()) {
            nameTag = mContext.getString(R.string.path_outindoortip);
        } else {
            //默认为室内测试时，看当前是否有室内转项的权限，如果有室内测试为室内专项的室内测试，不改头标志名
            nameTag = appModel.getAppList().contains(WalkStruct.AppType.IndoorTest) && appModel.isIndoorTest() ?
                    (appModel.getFloorModel().getBuildingName() + "_" + appModel.getFloorModel().getName())
                    : mContext.getString(R.string.path_indoortip);
        }
        rcuFilePath = routineSet.getStorgePathTask();
        String times = UtilsMethod.getSimpleDateFormat7(startRcuTime);
        if (routineSet.isGenCU(mContext)
                && ApplicationModel.getInstance().showInfoTypeCu()) {

            rcuFileName = String.format(DataTableStruct.CU_FILE_NAME_FORMAT,
                    GpsInfo.getInstance().isJobTestGpsOpen() ? "DT" : "CQT",        //测试方式:DT/CQT
                    appModel.getExtendInfoStr(RecordInfoKey.city.name()),            //城市名称
                    appModel.getExtendInfoStr(RecordInfoKey.cu_Scope.name()),        //测试范围
                    (AppVersionControl.getInstance().isPerceptionTest()?mContext.getString(R.string.device_Factory_perception):mContext.getString(R.string.device_Factory)),                            //仪表厂家
                    appModel.getExtendInfoStr(RecordInfoKey.cu_Company.name()),    //测试厂家
                    appModel.getExtendInfoStr(RecordInfoKey.tester.name()),            //测试人员
                    UtilsMethod.sdfyMdhms.format(System.currentTimeMillis()),        //测试时间
                    appModel.getExtendInfoStr(RecordInfoKey.extendsInfo.name()),    //扩展信息
                    DatasetManager.PORT_2,
                    FileType.ORGRCU.getFileTypeName()
            );
        } else {
            rcuFileName = String.format("%s%s." + FileType.ORGRCU.getFileTypeName(), nameTag, times);
        }

//		String jniRealName = String.format("%s%s%s." + MyFileModel.FILE_RCU, nameTag,times,DatasetManager.Port2Name);
//		if(appModel.isScannerTest()){
//			String jniScanRealName = String.format("%s%s%s." + FileType.RCU.getFileTypeName(), nameTag,times,"_Port3");
//			createScanRcuFilePath = String.format("%s%s", rcuFilePath,jniScanRealName);
//		}


        createRcuFilePath = String.format("%s%s", rcuFilePath, rcuFileName);
        buildNewFileName(mContext);
        createRcuFilePath = String.format("%s%s", rcuFilePath, newRcuFileName);
        mTestRecord.setTestRecordMsg(TestRecordEnum.file_name.name(), newRcuFileName.substring(0, newRcuFileName.lastIndexOf(".")));

        //2013.12.7 　不再用原来的广播方式通知生成文件
        mDatasetMgr.configDecodeProperty(false);   //配置采样配置周期  false为不是OTS采集

        boolean genDtLog = routineSet.isGenDTLog(mContext)                                //当前打开DTLog开关
                || (umpcTestinfo != null && umpcTestinfo.getAtuPort() != -1)    //当前为小背包测试且ATU端口设置的有效值
                || sManager.getDTLogCVersion() > 0;    //当前测试计划为ATU关联测试

        String deviceId =  /*genDtLog && !(umpcTestinfo != null && umpcTestinfo.getAtuPort() != -1) ? sManager.getDTLogBoxId() :*/
                ("{" + myPhoneState.getGUID(mContext, isEncrypt()) + "}");

        mDatasetMgr.createFile(createRcuFilePath, false, genDtLog, deviceId,
                sManager.getDTLogCVersion(), umpcTestinfo, mTestRecord, currentFileNum, entryptioneKey, hasCall);
        //20131115,调接口创建文件后，朱燕的库会给文件名拼上端口号，此处把记录的文件名标为真实生成的文件名
        //createRcuFilePath = String.format("%s%s", rcuFilePath,jniRealName);
        createRcuFilePath = createRcuFilePath.substring(0, createRcuFilePath.lastIndexOf("."))
                + DatasetManager.Port2Name + FileType.ORGRCU.getExtendName();
        //rcuFileName = jniRealName;
        rcuFileName = createRcuFilePath.substring(createRcuFilePath.lastIndexOf("/") + 1);
//        if(appModel.isInnsmapTest()){
//        	Intent intent = new Intent(InnsmapTestService.ACTION_CREATE_LOG_FILE);
//        	intent.putExtra("filePath", createRcuFilePath);
//        	this.sendBroadcast(intent);
//        }
        if (isNetsniffer) {
            String pcapFileName = createRcuFilePath.replace(FileType.ORGRCU.getExtendName(), FileType.PCAP.getExtendName());
            mTestRecord.setRecordDetailMsg(FileType.PCAP.name(), RecordDetailEnum.file_type.name(), FileType.PCAP.getFileTypeId());
            mTestRecord.setRecordDetailMsg(FileType.PCAP.name(), RecordDetailEnum.file_path.name(), ConfigRoutine.getInstance().getStorgePathTask());
            mTestRecord.setRecordDetailMsg(FileType.PCAP.name(), RecordDetailEnum.file_name.name(), pcapFileName.substring(pcapFileName.lastIndexOf("/") + 1));
            mTestRecord.setRecordDetailMsg(FileType.PCAP.name(), RecordDetailEnum.file_guid.name(), UtilsMethod.getUUID());
        }

        if (appModel.getFloorModel() != null && !appModel.getFloorModel().getTestMapPath().equals("")) {
            mTestRecord.setRecordDetailMsg(FileType.FloorPlan.name(), RecordDetailEnum.file_type.name(), FileType.FloorPlan.getFileTypeId());
            mTestRecord.setRecordDetailMsg(FileType.FloorPlan.name(), RecordDetailEnum.file_path.name(),
                    appModel.getFloorModel().getTestMapPath().substring(0, appModel.getFloorModel().getTestMapPath().lastIndexOf("/") + 1));
            mTestRecord.setRecordDetailMsg(FileType.FloorPlan.name(), RecordDetailEnum.file_name.name(),
                    appModel.getFloorModel().getTestMapPath().substring(appModel.getFloorModel().getTestMapPath().lastIndexOf("/") + 1));
            mTestRecord.setRecordDetailMsg(FileType.FloorPlan.name(), RecordDetailEnum.file_guid.name(), UtilsMethod.getUUID());
        }



        new WriteTestInfoThread(mContext).start();

        if (umpcTestinfo != null) {
            umpcTestinfo.setRTUploadStart(mTestRecord.getRecordDetails());
        }

        TraceInfoInterface.traceData.setTestLogFile(createRcuFilePath);

    }
    public BuildTestRecord getmTestRecord() {
        return mTestRecord;
    }
    /**
     * 等待RCU文件创建完成后，写入测试人员的相关信息
     */
    private class WriteTestInfoThread extends Thread {
        private Context mContext;

        public WriteTestInfoThread(Context mContext) {
            this.mContext = mContext;
        }

        public void run() {
            try {
                Thread.sleep(200);
                appModel.setRcuFileCreated(true);

                String testInfo = "Device Model=" + Deviceinfo.getInstance().getDevicemodel() + "\r\n"
                        + "Version=" + UtilsMethod.getCurrentVersionName(mContext);
                if (!appModel.isGeneralMode() && isUmpcTest) {
                    //如果为UMPC开始的测试，调用写入测试人员相关信息的RCU事件
                    testInfo += "\r\nCeShiRenYua="
                            + (umpcTestinfo != null && !umpcTestinfo.getTestername().equals("") ? umpcTestinfo.getTestername() : "")
                            + "\r\nCeShiShengFen=\r\nCeShiChengShi=\r\nCeShiCheLiang="
                            + "\r\nHuJiaoXuHao=\r\nXiangXiXinXi=\r\nCeShiDian=\r\nCaiYangDian=";
                    //2013.5.17增加
                    testInfo += "\r\n" + String.format("LogNumber=%s", currentFileNum);
                    testInfo += "\r\n" + String.format("LogName=%s", umpcTestinfo.getTestlocale());
                } else {
                    testInfo += "\r\n" + "Indoor Test="
                            + (isIndoorTest ? "Yes" : "No");

                }
                testInfo += "\r\nOuterLoop=" + outCircleTimes;
                if (!appModel.isGeneralMode() && appModel.getNetList().contains(WalkStruct.ShowInfoType.TelecomSwitch)) {
                    testInfo += "\r\nSpecialInfoData=" + currentFileNum;
                }

                LogUtil.w(TAG, "--Test Information:" + testInfo);

                EventBytes.Builder(mContext)
                        .addCharArray(testInfo)
                        .writeToRcu(WalkCommonPara.MsgDataFlag_I);
                writeTestInfoEventToRcu(mContext);
                //写入VOLTE 密钥
                writeVolteKeyToRcu(mContext);

                if (appModel.isIndoorTest()) {
                    if (appModel.isWoneTest()) {     //如果是Wone插入gpsinfo
                        Location location = GpsInfo.getInstance().getLocation();
                        EventBytes.Builder(mContext, RcuEventCommand.IndoorTestGpsFlag)
                                .addDouble(location.getLongitude())
                                .addDouble(location.getLatitude())
                                .writeToRcu(System.currentTimeMillis());
                    } else {
                        indoorTestGpsFlag(mContext);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void indoorTestGpsFlag(Context mContext) {
        EventBytes.Builder(mContext, RcuEventCommand.IndoorTestGpsFlag)
                .addDouble(TestInfoValue.latitude)
                .addDouble(TestInfoValue.longtitude)
                .writeToRcu(System.currentTimeMillis() * 1000);
    }
    /**
     * 填写测试信息事件到每一个分隔文件中
     */
    private void writeTestInfoEventToRcu(Context mContext) {
        StringBuilder sb = new StringBuilder();
        sb.append("<TestInfo>");
        sb.append("<DeviceID>{").append(myPhoneState.getGUID(mContext)).append("}</DeviceID>");
        sb.append("<Vendor>0</Vendor>");
        sb.append("<DeviceType>2</DeviceType>");
        sb.append("<DeviceModel>").append(Deviceinfo.getInstance().getInstance().getDevicemodel()).append("</DeviceModel>");
        if (this.umpcTestinfo == null) {
            sb.append("<TestPlanID>-9999</TestPlanID>");
            sb.append("<TestPlanName>Null</TestPlanName >");
        } else {
            sb.append("<TestPlanID>").append(this.umpcTestinfo.getTaskno()).append("</TestPlanID>");
            sb.append("<TestPlanName>").append(this.umpcTestinfo.getTestername()).append("</TestPlanName>");
        }
        if (GpsInfo.getInstance().isJobTestGpsOpen()) {
            sb.append("<TestType>0</TestType>");
        } else {
            sb.append("<TestType>1</TestType>");
        }
        sb.append("<TestLevel>-9999</TestLevel>");
        sb.append("<Testscenario>-9999</Testscenario>");
            sb.append("<Address>Null</Address>");
        sb.append("<SecondAddress>Null</SecondAddress>");
        if (isUmpcTest) {//小背包测试写入无效值
            sb.append("<Longitude>-9999</Longitude>");
            sb.append("<Latitude>-9999</Latitude>");
        }else {
            Location location = GpsInfo.getInstance().getLocation();
            if (location != null) {
                sb.append("<Longitude>").append(location.getLongitude()).append("</Longitude>");
                sb.append("<Latitude>").append(location.getLatitude()).append("</Latitude>");
            } else {
                sb.append("<Longitude>-9999</Longitude>");
                sb.append("<Latitude>-9999</Latitude>");
            }
        }
        sb.append("<TestPoint>0</TestPoint>");
            sb.append("<Tester>Null</Tester>");

            sb.append("<MOT>-9999</MOT>");
            sb.append("<MOT_GUID>Null</MOT_GUID>");

        sb.append("</TestInfo>");
        LogUtil.d(TAG, sb.toString());
        EventBytes eventBytes = EventBytes.Builder(mContext, RcuEventCommand.TEST_PLAN_INFO);
        eventBytes.addInteger(0);
        eventBytes.addStringBuffer(sb.toString());
        eventBytes.writeToRcu(System.currentTimeMillis() * 1000);
        if(umpcTestinfo != null) {
            //写入CU数据格式
            EventBytes eventBytes2 = EventBytes.Builder(mContext);
            eventBytes2.addInteger(1);
            eventBytes2.addSingle(umpcTestinfo.getCuScale());
            eventBytes2.addInteger(umpcTestinfo.getCuPicType());
            eventBytes2.addStringBuffer(umpcTestinfo.getCuPicName());
            eventBytes2.addInteger(0);
            eventBytes2.addCharArray("");
            eventBytes2.writeToRcu(WalkCommonPara.MsgDataFlag_N);
        }
    }
    /**
     * 如果volteKeyModel对象不为空
     * 将volteKeyModel中的密钥信息写入RCU中
     */
    private void writeVolteKeyToRcu(Context mContext) {
        VolteKeyModel volteKeyModel = ApplicationModel.getInstance().getVolteKeyModel();
        LogUtil.w(TAG, "--volteKeyWrite:" + (volteKeyModel != null ? volteKeyModel.getAuthenticationKey() + ":" + volteKeyModel.getEncryptionKey() : "key isnull"));

        if (volteKeyModel != null) {
            //LogUtil.w(TAG, "--EncryptionKey:" + volteKeyModel.getEncryptionKey()
            //		+ "--AuthenticationKey:" + volteKeyModel.getAuthenticationKey()
            //		+ "--isEnable:" + volteKeyModel.isEnable());

            long currentTime = System.currentTimeMillis();
            EventBytes.Builder(mContext)
                    .addInteger(SPECIALDATT_TYPE_SIPKEY)    //Special Data Type
                    .addInteger(1)                            //Version 版本号，当版本为0
                    .addInteger((int) currentTime / 1000)
                    .addInteger((int) currentTime % 1000)
                    .addByte((byte) 0xff)
                    .addInteger(volteKeyModel.getEncryptionAlgo())            //加密算法	如果原始值中百11,12,那么当前值为22
                    .addInteger(volteKeyModel.getAuthenticationAlgo())        //鉴权算法	如果原始值为非2,那么当前值为0
                    .addInteger(volteKeyModel.getEncryptionAlgoOrg())        //加密算法原始值
                    .addInteger(volteKeyModel.getAuthenticationAlgoOrg())    //鉴权算法原始值
                    .addStringBuffer(volteKeyModel.getEncryptionKey())
                    .addStringBuffer(volteKeyModel.getAuthenticationKey())
                    .writeToRcu(WalkCommonPara.MsgDataFlag_C);

			/*byte[] bt = sipEvent.getByteArray();
            String s1 = "";
			String s0 = "";
			for (int i = 0; i < bt.length; i++)
	        {
				s0 = s0 + bt[i] + " ";

	            String tempStr = Integer.toHexString(bt[i]);
	            if (tempStr.length() > 2)
	                tempStr = tempStr.substring(tempStr.length() - 2);
	            s1 = s1 + (tempStr.length() == 1 ? "0" : "") + tempStr;
	        }

			LogUtil.w(TAG,"--TestInfo volteKeyByte:" + s0);
			LogUtil.w(TAG,"--TestInfo volteKeyEvent:" + s1);*/

            //BuildRCUSignal.buildVoLTEKeySignal(mContext, keyModel)
            //.writeToRcu(WalkCommonPara.MsgDataFlag_D);
        }
        SIPInfoModel sipModel = ApplicationModel.getInstance().getSIPInfoModel();
        LogUtil.w(TAG, "--SIPInfoWrite:" + (sipModel != null
                ? sipModel.getName() + ":" + sipModel.getContent() : "SIPInfo isnull"));
        if(sipModel != null){
            EventBytes.Builder(mContext).addInteger(SPECIALDATT_TYPE_ADBSIP) // Special
                    // Data
                    // Type
                    .addInteger(0) // Version 版本号，当版本为0
                    .addInteger((int) sipModel.getTime()).addInteger(sipModel.getuTime())
                    .addByte((byte) sipModel.getDirection()).addStringBuffer(sipModel.getName())
                    .addStringBuffer(sipModel.getContent()).writeToRcu(WalkCommonPara.MsgDataFlag_C);
        }
    }
    private String getUmpcTagDeviceName(String localName) {
        if (localName != null && !localName.equals("")) {
            return localName;
        } else if (routineSet.getDeviceTag() != null && !routineSet.getDeviceTag().equals("")) {
            return routineSet.getDeviceTag();
        } else {
            return UtilsMethod.sdfyMdhms.format(System.currentTimeMillis());
        }
    }
    /**
     * 在联通招标中根据网络类型获得文件名的类型标识
     *
     * @return 类型标识
     */
    private String getUmpcDeviceName(Context mContext) {

        String name = MobileUtil.getSIM_MCCMNC(mContext);
        try {
            int iMmcMnc = Integer.parseInt(name);
            //如果当前MNC非国内的,直接文件名中加MNC信息
            switch (iMmcMnc) {
                case 46000:
                case 46002:
                case 46007:
                    name = mContext.getString(R.string.str_chinamobile);
                    break;
                case 46001:
                case 46006:
                    name = mContext.getString(R.string.str_chinaunicom);
                    break;
                case 46003:
                case 46005:
                case 46011:
                    name = mContext.getString(R.string.str_chinatelecom);
                    break;
            }
        } catch (Exception e) {
            LogUtil.w(TAG, "getUmpcDeviceName", e);
        }

        return name;
    }
    /**
     * 获得当前是否需要添加文件分割序号
     *
     * @return 是否需要添加文件分割序号
     */
    private String genSplitFileIndex() {
        return (rcuFileSizeLimit > 0 || rcuFileTimeLimit > 0 ? "(" + (rcuFileLimitNum++) + ")" : "");
    }

    /**
     * 获得需要重命名的名字
     */
    private void buildNewFileName(Context mContext) {
        //如果当前为UMPC测试，则根据UMPC测试时传过来的名称替换现在的RCU文件名
        newRcuFileName = "";
        String doneJobName="";
        if (!appModel.isGeneralMode() && isUmpcTest) {
            if (umpcTestinfo != null /*&& !umpcTestinfo.getTestlocale().equals("")*/) {

                newRcuFileName = getUmpcTagDeviceName(umpcTestinfo.getTestgroupinfo())        //Ipack下发的文件名串
                        + "@"                                                                    //以@分隔之前的内容紧跟着自增序号000001..
                        + (currentFileNum > -1 ? fileNameFormat.format(currentFileNum) + "_" : "")    //如果加密序号大于-1则添加加密序号
                        + getUmpcDeviceName(mContext) + "_"                                                //加上“移动”“联通”“电信”分组;如果非这些运行商显示MMC号
                        + (doneJobName.equals("") ? "" : doneJobName + "_")                        //业务名称(最多三项)
                        + myPhoneState.getMyDeviceId(mContext)                                    //IMEI
                        + genSplitFileIndex()
                        + FileType.ORGRCU.getExtendName();

                mTestRecord.setTestRecordMsg(TestRecordEnum.port_id.name(), String.valueOf(umpcTestinfo.getAtuPort()));
                mTestRecord.setTestRecordMsg(TestRecordEnum.group_info.name(), umpcTestinfo.getTestgroupinfo());
                mTestRecord.setTestRecordMsg(TestRecordEnum.test_index.name(), currentFileNum);
            } else {
                newRcuFileName = testTagStr + addDonenNameToRcuName(rcuFileName, doneJobName);
            }

        } else if (routineSet.isGenCU(mContext) && ApplicationModel.getInstance().showInfoTypeCu()) {

            newRcuFileName = rcuFileName;
//            newRcuFileName = String.format(ExtendsInfo.CU_FILE_NAME_FORMAT,
//                    GpsInfo.getInstance().isJobTestGpsOpen() ? "DT" : "CQT",
//					appModel.getExtendsInfo().cityName,
//					appModel.getExtendsInfo().testRange,
//					getString(R.string.device_Factory),
//					appModel.getExtendsInfo().testFactory,
//					"Testor",
//					UtilsMethod.sdfyMdhms.format(System.currentTimeMillis()),
//					appModel.getExtendsInfo().TestExtendInfo,
//					DatasetManager.PORT_2,
//					MyFileModel.FILE_RCU
//				);
        } else {
            //根据当前已测试的任务修改最终的文件名，并将文件名写入数据库中
            String fleetName = "";
            if (rcuFileSizeLimit > 0 || rcuFileTimeLimit > 0) {
                if (splitFileRcuName.equals("")) {
                    splitFileRcuName = testTagStr + addDonenNameToRcuName(rcuFileName, doneJobName);
                    splitFileRcuName = splitFileRcuName.substring(0, splitFileRcuName.length() - 4);
                }
                newRcuFileName = String.format("%s(%s).%s", splitFileRcuName, (rcuFileLimitNum++), FileType.ORGRCU.getFileTypeName());
            } else {
                newRcuFileName = testTagStr + addDonenNameToRcuName(rcuFileName, doneJobName);
            }

            if (!fleetName.equals("")) {
                newRcuFileName = String.format("%s-%s", fleetName, newRcuFileName);
            }

            newRcuFileName = newRcuFileName.replaceAll(DatasetManager.Port2Name, "");


        }

        mTestRecord.setTestRecordMsg(TestRecordEnum.task_no.name(), task_no);
        mTestRecord.setTestRecordMsg(TestRecordEnum.file_split_id.name(), (rcuFileLimitNum - 1));


    }
    /**
     * 将测试过程中的任务名称添加到已存在的RCU名称后面
     *
     * @param rcuName  rcu文件
     * @param doneName 任务名称
     * @return 生成的名称
     */
    private String addDonenNameToRcuName(String rcuName, String doneName) {
        if (doneName == null || doneName.equals("")) {
            return rcuName;
        }
        return rcuName.substring(0, rcuName.lastIndexOf(".")) + "-" + doneName + FileType.ORGRCU.getExtendName();
    }
    /**
     * 当前测试是否需要加密
     */
    private boolean isEncrypt() {
        return (umpcTestinfo != null && !umpcTestinfo.getEncryptCode().equals("")
                || entryptioneKey != null && !entryptioneKey.equals(""));
    }
    /**
     * 发送关闭RCU文件命令并将当前关闭文件的记录写入数据库中
     */
    public void sendCloseRcuFileAddToDB(Context mContext) {
        appModel.setRcuFileCreated(false);
        LogUtil.w(TAG, "--befer toDB the rcuname:" + createRcuFilePath);
        //测试结束设置不显示当前文件信息
        TraceInfoInterface.traceData.setTestLogFile(null);

//        String ddibFilePath = mDatasetMgr.getDecodedIndexFileName(DatasetManager.PORT_2);
        int cResult = mDatasetMgr.closeFile();//此函数阻塞

        LogUtil.w(TAG, "--befer toDB close file result:" + cResult);
        MapFactory.getMapData().getHistoryList().clear();

        if (umpcTestinfo != null) {
            umpcTestinfo.setRTUploadEnd();

            LogUtil.w(TAG, "--wait upload start");
            while (!umpcTestinfo.isUploadEnd()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            LogUtil.w(TAG, "--wait upload end");
        }


        insertFileInfoToDB(mContext);
    }

    /**
     * 将当前记录的RCU文件信息写入数据库中
     */
    private void insertFileInfoToDB(Context mContext) {
        try {
            if (createRcuFilePath != null && !createRcuFilePath.equals("")) {
                //构建最终要重命名的文件名,存于类全局的newRcuFileName变量中
                newRcuFileName = newRcuFileName.replaceAll(appModel.isScannerTest() ? DatasetManager.Port3Name : DatasetManager.Port2Name, "");
                String initName = newRcuFileName.replace(FileType.ORGRCU.getExtendName(), "");

                long stopRcuTime = System.currentTimeMillis();
                mTestRecord.setTestRecordMsg(TestRecordEnum.time_end.name(), stopRcuTime);
                mTestRecord.setTestRecordMsg(TestRecordEnum.go_or_nogo.name(), TotalDataByGSM.getInstance().getGoOrNogoReport(mContext) ? 1 : 0);

                mTestRecord.setRecordTestInfoMsg(appModel.getExtendInfo());
                String ddibFilePath = mTestRecord.moveRelativeFiles(false, routineSet.getStorgePathTask(), initName);


                if (umpcTestinfo != null ?
                        umpcTestinfo.getGenFileTypes() != null && umpcTestinfo.getGenFileTypes()[umpcTestinfo.GEN_FILE_ECTI] == '1'
                        : ConfigRoutine.getInstance().isGenECTI(mContext)) {
                    mTestRecord.setRecordDetailMsg(FileType.ECTI.getFileTypeName(), RecordDetailEnum.file_type.name(), FileType.ECTI.getFileTypeId());
                    mTestRecord.setRecordDetailMsg(FileType.ECTI.getFileTypeName(), RecordDetailEnum.file_path.name(), ddibFilePath.substring(0, ddibFilePath.lastIndexOf("/") + 1));
                    mTestRecord.setRecordDetailMsg(FileType.ECTI.getFileTypeName(), RecordDetailEnum.file_name.name(), DataSetLib.currentFileName);
                    mTestRecord.setRecordDetailMsg(FileType.ECTI.getFileTypeName(), RecordDetailEnum.file_guid.name(), UtilsMethod.getUUID());
                    if (null != DataSetLib.currentFileName && DataSetLib.currentFileName.length() > 0) {
                        File fx = new File(ddibFilePath.substring(0, ddibFilePath.lastIndexOf("/") + 1) + DataSetLib.currentFileName);
                        if (fx.exists()) {
                            mTestRecord.setRecordDetailMsg(FileType.ECTI.getFileTypeName(), RecordDetailEnum.file_size.name(), fx.length());
                        }
                    }
                }

                if (!appModel.isGeneralMode())
                    TotalInterface.getInstance(mContext).addStatisticDDIBFile(ddibFilePath);


                if (umpcTestinfo != null) {
                    umpcTestinfo.filesRename(newRcuFileName, MyPhoneState.getInstance().getDeviceId(mContext), mTestRecord.getTestRecord());
                }


                int mainId = (int) DataManagerFileList.getInstance(mContext).insertFile(mTestRecord.getTestRecord());
                VoiceAnalyse.setTotalFileId(String.valueOf(mainId));
//                if(appModel.isSingleStationTest()) {
                //生成文件广播
                Intent i = new Intent(WalkMessage.NOTIFY_TEST_FILE_CREATED);
                i.putExtra("record_id", this.mTestRecord.getTestRecord().record_id);
                mContext.sendBroadcast(i);
//                }
                //写入统计数据
                LogUtil.w(TAG, "---insert to DB Id:" + mainId + "--" + createRcuFilePath);
                TotalDataByGSM.getInstance().InsertTotalDetailToDB(mContext, mainId, appModel.isIndoorTest() ? 2 : 1);

                //发送统计文件
                if (isUmpcTest) {
                    if (umpcTestinfo != null) {
                        if (umpcTestinfo.isAutoupload()) {
                            Intent intent = new Intent(WalkMessage.ACTION_SEND_STATIC2PAD);
                            intent.putExtra("file", initName);
                            mContext.sendBroadcast(intent);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.w(TAG, "insertFileInfoToDB", e);
        }
    }
}
