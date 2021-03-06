package com.walktour.service.innsmap;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.dinglicom.data.model.RecordDetail;
import com.dinglicom.data.model.TestRecord;
import com.dinglicom.dataset.DatasetManager;
import com.innsmap.InnsMap.INNSMapLocationClient;
import com.innsmap.InnsMap.location.bean.INNSMapLocation;
import com.innsmap.InnsMap.location.listener.INNSMapLocationListener;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ServerManager;
import com.walktour.control.instance.FileDB;
import com.walktour.gui.data.model.DBManager;
import com.walktour.gui.inns.dao.InnsDaoManager;
import com.walktour.gui.inns.dao.model.InnsFtpParams;
import com.walktour.gui.inns.dao.model.InnsVoLTEParams;
import com.walktour.gui.map.InnsMapView;
import com.walktour.model.LocusParamInfo;
import com.walktour.model.MapEvent;
import com.walktour.model.TotalMeasureModel;
import com.walktour.service.innsmap.model.InnsLocationSetManager;
import com.walktour.service.innsmap.model.LocationWithMeasParameter;
import com.walktour.service.innsmap.model.PingData;
import com.walktour.service.test.PingTest;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ???????????????????????????
 *
 * @author jianchao.wang
 */
public class InnsmapTestService extends Service implements INNSMapLocationListener {

    public static final String EXTRA_KEY_PING_TTL = "ping_ttl";
    public static final String EXTRA_KEY_PING_IP = "ping_ip";
    public static final String EXTRA_KEY_PING_SIZE = "ping_size";
    public static final String EXTRA_KEY_PING_REPEAT = "ping_repeat";
    public static final String EXTRA_KEY_EVENT_ID = "event_id";
    public static final String EXTRA_KEY_EVENT_DATA = "event_data";

    /**
     * ????????????
     */
    private static final String TAG = "InnsmapTestService";

    /**
     * ???????????????????????????
     */
    private boolean hasRegisteredBroadcast = false;

    /**
     * ????????????????????????
     */
    public static final String ACTION_CREATE_LOG_FILE = "create_inns_log_file";
    /**
     * ????????????????????????
     */
    public static final String ACTION_REFLASH_VIEW = "reflash_inns_map_view";
    /**
     * ?????????????????????????????????
     */
    private static final int LOCATION_INTERVAL_TIME = 1000;
    /**
     * ???????????????
     */
    private INNSMapLocationClient mINNSLocationClient;
    /**
     * ???????????????????????????
     */
    private INNSMapLocation mLastLocation;
    /**
     * ?????????????????????
     */
    private boolean isLocationChange = false;
    /**
     * ??????
     */
    private MyHandler mHandler = new MyHandler(this);
    /**
     * ?????????
     */
    private Timer mLocationTimer;
    /**
     * ????????????
     */
    private static ApplicationModel appModel = ApplicationModel.getInstance();
    /**
     * ??????????????????
     */
    private DatasetManager mDatasetManager;
    /**
     * ????????????????????????
     */
    private String mFilePath;
    /**
     * ??????????????????
     */
    private List<String> mLogList = new ArrayList<String>();
    /**
     * ??????????????????
     */
    private MyFileThread mThread;
    /**
     * ???????????????????????????
     */
    private boolean isCreateFile = false;

    /**
     * ????????????????????????????????????????????????????????????
     */
    private INNSMapLocation mPreLocation = new INNSMapLocation();


    /**
     * ??????????????????MapEvent??????Key
     */
    private static final String KEY_MAP_EVENT_RX_LEV_SUB = "RxLevSub";
    private static final String KEY_MAP_EVENT_RSCP = "PCCPCH RSCP";
    private static final String KEY_MAP_EVENT_EC_NO = "EC/NO";// TODO: 2017/11/24
    private static final String KEY_MAP_EVENT_EC_IO = "(C)Total EcIo";
    private static final String KEY_MAP_EVENT_RSRP = "RSRP";
    private static final String KEY_MAP_EVENT_RSRQ = "RSRQ";
    private static final String KEY_MAP_EVENT_SNR = "(L)SINR";

    private int mTestType = TEST_TYPE_COVERAGE;// ???????????? ??? 1  Ping????????? 2
    private static final int TEST_TYPE_COVERAGE = 1, TEST_TYPE_PING = 2;

    private ArrayList<PingData> mPingDataList = new ArrayList<>();
    private PingData mPingData;
    /**
     * ???????????????
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_CREATE_LOG_FILE.equalsIgnoreCase(action)) {
                String filePath = intent.getStringExtra("filePath");
                createFile(filePath);
            } else if (PingTest.NOTIFY_PING_DATA_CHANGED.equals(action)) {
                int eventId = intent.getIntExtra(EXTRA_KEY_EVENT_ID, -1);
                String eventData = intent.getStringExtra(EXTRA_KEY_EVENT_DATA);
                if (eventId == PingTest.PING_INITED) {
                    mTestType = TEST_TYPE_PING;
                    mPingData = new PingData();
                    int size = intent.getIntExtra(EXTRA_KEY_PING_SIZE, -1);
                    int repeat = intent.getIntExtra(EXTRA_KEY_PING_REPEAT, -1);
                    int ttl = intent.getIntExtra(EXTRA_KEY_PING_TTL, -1);
                    String ip = intent.getStringExtra(EXTRA_KEY_PING_IP);
                    mPingData.setIP(ip);
                    mPingData.setRepeat(repeat);
                    mPingData.setSize(size);
                    mPingData.setTTL(ttl);
                    mPingData.setTime(System.currentTimeMillis());
                } else if (eventId == PingTest.PING_RESOLV_FAILED) {
                    mPingData.setResult(PingData.RESULT_FAILED);
                    mPingData.setCause("DNS Resolve Failed");
                    mPingDataList.add(mPingData);
                } else if (eventId == PingTest.PING_ITEM_SUCCESS) {
                    if (!TextUtils.isEmpty(eventData)) {
                        try {
                            if (eventData.contains("host_ip::")) {
                                String realIp = eventData.substring(eventData.indexOf("host_ip::"), eventData.length() - 1).replace("host_ip::", "").trim();
                                mPingData.setIP(realIp);
                            }
                            if (eventData.contains("ttl::")) {
                                String realTTL = eventData.substring(eventData.indexOf("ttl::"), eventData.indexOf("time_ms::")).replace("ttl::", "").trim();
                                mPingData.setTTL(Integer.parseInt(realTTL));
                            }
                            if(eventData.contains("time_ms::")){
                                String realDelay = eventData.substring(eventData.indexOf("time_ms::"), eventData.indexOf("host_ip::")).replace("time_ms::", "").trim();
                                mPingData.setDelay((int)Double.parseDouble(realDelay));
                            }
                        } catch (Exception e) {
                            LogUtil.e(TAG, e.getMessage());
                        }
                    }
                    mPingData.setResult(PingData.RESULT_SUCCEED);
                    mPingDataList.add(mPingData);
                } else if (eventId == PingTest.PING_ITEM_FAILED) {
                    mPingData.setResult(PingData.RESULT_FAILED);
                    if (TextUtils.isEmpty(mPingData.getCause())) {
                        mPingData.setCause("Ping Failed");
                    }
                    mPingDataList.add(mPingData);
                } else if (eventId == PingTest.PING_ERROR) {
                    mPingData.setResult(PingData.RESULT_FAILED);
                    mPingData.setCause("Invalid Ip");
                    mPingDataList.add(mPingData);
                }
            } else if (WalkMessage.NOTIFY_TEST_FILE_CREATED.equals(action)) {
                String recordId = intent.getStringExtra("record_id");
                assembleData(recordId);
            }

        }
    };

    /**
     * ??????????????????????????????
     */
    private void assembleData(String recordId) {
        LogUtil.d(TAG, "recordId = " + recordId);
        TestRecord record = DBManager.getInstance(this).getTestRecord(recordId);
        String userId = ServerManager.getInstance(this).getInnsServerUserId();
        String filePath = record.getRecordDetails().get(0).file_path;
        String fileName = record.file_name;
        String fileNameDetail = filePath + fileName + ".txt";
        String fileUUID = record.task_no;
        StringBuilder sb = new StringBuilder();
        //File format
        sb.append("#FF,")
                .append("1.0.0,")//File format version
                .append(mTestType).append(",")//???????????????????????????1, ???????????? ??? 1  Ping????????? 2
                .append(record == null ? "," : record.time_create + ",")//??????????????????,ms
                .append(TextUtils.isEmpty(userId) ? "-1" : userId).append(",")//????????????,????????????????????????id?????????????????????????????????????????????-1???
                .append(record == null ? "," : record.task_no + ",")//??????????????????
                .append(record == null ? "," : record.file_name + ",")//????????????
                .append(SharePreferencesUtil.getInstance(this).getBoolean(InnsMapView.KEY_INNS_IS_MANUAL_MARK, false) ? "1" : "0")//???????????? 0 ???????????? 1 ????????????
                .append("\n");

        //Phone information
        String phoneName = android.os.Build.MODEL;//???????????????eg:HUAWEI TAGTL00
        String phoneProducer = android.os.Build.BRAND;//??????????????? eg:HUAWEI
        String phoneOSVersion = android.os.Build.VERSION.RELEASE;//?????????????????? eg:4.4.4
        String phoneOs = "1";//1 = android; 2 = iOS;  3 = other
        String phoneNumber = MyPhoneState.getInstance().getSimNumber(this);//???????????? eg:1398888888
        String imei = MyPhoneState.getInstance().getIMEI(this);//eg: 866947022144781
        String imsi = MyPhoneState.getInstance().getIMSI(this);//eg: 464164216545445
        sb.append("#PI,")//Phone information (#PI)
                .append(phoneName).append(",")
                .append(phoneProducer).append(",")
                .append(phoneOSVersion).append(",")
                .append(phoneNumber).append(",")
                .append(phoneOs).append(",")
                .append(imei).append(",")
                .append(imsi)
                .append("\n");

        //Cell scan
        sb.append("CELLSCAN,")
                .append(getCellScanSystem()).append(",")
                .append(getCellScanNetworkType2()).append(",")
                .append(getCellScanParameters())
                .append("\n");

        //Cell measurement (CELLMEAS)
        String selectedBuildingId = InnsmapFactory.getInstance(this).getSelectedBuildingId();
        String selectedFloorId = InnsmapFactory.getInstance(this).getSelectedFloorId();

        ArrayList<LocationWithMeasParameter> locationList = InnsLocationSetManager.getInstance().getLocationList();
        if (null != locationList && !locationList.isEmpty()) {
            for (LocationWithMeasParameter lmp : locationList) {
                INNSMapLocation location = lmp.getInnsMapLocation();
                sb.append("CELLMEAS,")
                        .append(lmp.getTime()).append(",")
                        .append("0.0").append(",")//Longitude
                        .append("0.0").append(",")//Latitude
                        .append(selectedBuildingId).append(",")//buildingID ????????????id
                        .append(selectedFloorId).append(",")//floorID ????????????id
                        .append(location.getX()).append(",")
                        .append(location.getY()).append(",")
                        .append(lmp.getMeasParameter())
                        .append("\n");
            }
        }

        //PING(?????????Ping????????????????????????)
        if (mTestType == TEST_TYPE_PING && !mPingDataList.isEmpty()) {
            for (PingData pingData : mPingDataList) {
                sb.append("PING,").append(pingData.getResult()).append(",");
                if (pingData.getResult() == PingData.RESULT_SUCCEED) {
                    //Parameters for Success: No/Time/IP/Size/Delay/TTL
                    sb.append(pingData.getRepeat()).append(",")
                            .append(pingData.getTime()).append(",")
                            .append(pingData.getIP()).append(",")
                            .append(pingData.getSize()).append(",")
                            .append(pingData.getDelay()).append(",")
                            .append(pingData.getTTL())
                            .append("\n");
                } else {
                    //Parameters for Failed: No/Time/IP/cause.
                    sb.append(pingData.getRepeat()).append(",")
                            .append(pingData.getTime()).append(",")
                            .append(pingData.getIP()).append(",")
                            .append(TextUtils.isEmpty(pingData.getCause()) ? "Ping Failed" : pingData.getCause())
                            .append("\n");
                }
            }
        }

        /*List<MapEvent> eventList = new ArrayList<>(TraceInfoInterface.traceData.getGpsLocas());
        DecimalFormat decimalFormat = new DecimalFormat("#.####");//xy??????????????????????????????
        if (!eventList.isEmpty()) {
            for (MapEvent mapEvent : eventList) {
                sb.append("CELLMEAS,")
                        .append(mapEvent.getEventTime()).append(",")
                        .append("0.0").append(",")//Longitude
                        .append("0.0").append(",")//Latitude
                        .append(selectedBuildingId).append(",")//buildingID ????????????id
                        .append(selectedFloorId).append(",")//floorID ????????????id
                        .append(decimalFormat.format(mapEvent.getLongitude())).append(",")//xy??????????????????????????????
                        .append(decimalFormat.format(mapEvent.getLatitude())).append(",")//xy??????????????????????????????
                        .append(getCellMeasMapEventParameters(mapEvent))
                        .append("\n");
            }
        }*/

        HashMap<String, Long> unifyTimes = TotalDataByGSM.getInstance().getUnifyTimes();

        //FTP????????????
        //FTP ??????????????????
        String ftpDownAve = TotalDataByGSM.getHashMapMultiple(unifyTimes,
                TotalStruct.TotalFtp._downCurrentSize.name(),    //bit
                TotalStruct.TotalFtp._downCurrentTimes.name(),  //ms
                1f,
                "");
        double ftpDownAveDouble = 0;
        if (!TextUtils.isEmpty(ftpDownAve)) {
            try {
                ftpDownAveDouble = Double.parseDouble(ftpDownAve);
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage());
                ftpDownAveDouble = 0;
            }
        }
        //FTP ??????????????????
        String ftpUpAve = TotalDataByGSM.getHashMapMultiple(unifyTimes,
                TotalStruct.TotalFtp._upCurrentSize.name(),//bit
                TotalStruct.TotalFtp._upCurrentTimes.name(), //ms
                1f,
                "");
        double ftpUpAveDouble = 0;
        if (!TextUtils.isEmpty(ftpUpAve)) {
            try {
                ftpUpAveDouble = Double.parseDouble(ftpUpAve);
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage());
                ftpUpAveDouble = 0;
            }
        }
        //??????????????????
        float ftpDownMax = getTotalValue(TotalStruct.TotalFtp._down_max_value.name()) / UtilsMethod.kbyteRage;
        //??????????????????
        float ftpDownMin = getTotalValue(TotalStruct.TotalFtp._down_min_value.name()) / UtilsMethod.kbyteRage;

        InnsFtpParams innsFtpParams = new InnsFtpParams();
        innsFtpParams.setFileNameDetail(fileNameDetail);
        innsFtpParams.setLogFileUUID(fileUUID);
        innsFtpParams.setFtpDownAve(ftpDownAveDouble);
        innsFtpParams.setFtpUpAve(ftpUpAveDouble);
        innsFtpParams.setFtpDownMax(ftpDownMax);
        innsFtpParams.setFtpDownMin(ftpDownMin);
        InnsDaoManager.getInstance(this).save(innsFtpParams);

        //VoLTE????????????
        //????????? ????????????????????????/??????????????????
        String volteSuccessRatio = TotalDataByGSM.getHashMapMultiple(unifyTimes,
                TotalStruct.TotalDial._volte_moConnects.name(),
                TotalStruct.TotalDial._volte_moTrys.name(), 1, "");
        double volteSuccessRatioDouble = 0;
        if (!TextUtils.isEmpty(volteSuccessRatio)) {
            try {
                volteSuccessRatioDouble = Double.parseDouble(volteSuccessRatio);
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage());
                volteSuccessRatioDouble = 0;
            }
        }
        //?????????
        float volteDropRatio = 0;
        float volteMoDropCalls = getTotalValue(TotalStruct.TotalDial._volte_moDropcalls.name());//??????????????????
        float volteMtDropCalls = getTotalValue(TotalStruct.TotalDial._volte_mtDropcalls.name());//??????????????????
        float volteMtConnects = getTotalValue(TotalStruct.TotalDial._volte_moConnects.name());//??????????????????
        float volteMoConnects = getTotalValue(TotalStruct.TotalDial._volte_mtConnects.name());//??????????????????
        float totalConnects = volteMtConnects + volteMoConnects;
        if (totalConnects != 0) {
            volteDropRatio = (volteMoDropCalls + volteMtDropCalls) / totalConnects;
        }
        //??????????????????
        String volteDelay = TotalDataByGSM.getHashMapMultiple(unifyTimes,
                TotalStruct.TotalDial._volte_moCalldelay.name(),
                TotalStruct.TotalDial._volte_moDelaytimes.name(), 0.001f, "");
        int volteDelayInt = 0;
        if (!TextUtils.isEmpty(volteDelay)) {
            try {
                volteDelayInt = Integer.parseInt(volteDelay);
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage());
                volteDelayInt = 0;
            }
        }
        //MOS 3.0 ???????????? TODO ???????????????
        double mosMore3_0Ratio = 0;
        //MOS 3.5 ???????????? TODO ???????????????
        double mosMore3_5Ratio = 0;
        //IMS ??????????????????%??? TODO
        double imsRegisterSucRatio = 0;
        //eSRVCC ????????????%???
        HashMap<String, Long> eventMap = TotalDataByGSM.getInstance().getEvent();
        String eSRVCCSucRatio = TotalDataByGSM.getHashMapMultiple(eventMap,
                TotalStruct.TotalEvent._lteTogsmHandoverSuccess.name(),
                TotalStruct.TotalEvent._lteTogsmHandoverRequest.name(), 1, "");
        double eSRVCCSucRatioDouble = 0;
        if (!TextUtils.isEmpty(eSRVCCSucRatio)) {
            try {
                eSRVCCSucRatioDouble = Double.parseDouble(eSRVCCSucRatio);
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage());
                eSRVCCSucRatioDouble = 0;
            }
        }
        //eSRVCC ????????????-????????????ms??? TODO
        int eSRVCCHandoverDelay = 0;
        //RTP ?????????:????????????Packet LossCount/???????????????Packet LossCount+???????????????SIP Rx RTP Packet Num???
        HashMap<String, TotalMeasureModel> measureMap = TotalDataByGSM.getInstance().getMeasuePara();
        TotalMeasureModel dataLossCount = TotalDataByGSM.getHashMapMeasure(measureMap,
                TotalStruct.TotalMeasurePara._Packet_LossCount.name());
        TotalMeasureModel dataPacketNum = TotalDataByGSM.getHashMapMeasure(measureMap,
                TotalStruct.TotalMeasurePara._SIP_Rx_RTP_Packet_Num.name());
        String rtpLossRatio = (dataLossCount.getKeySum() == -9999 ? "" : UtilsMethod.decFormat
                .format(dataLossCount.getKeySum()
                        * 1f
                        / (dataLossCount.getKeyCounts() != 0 ? dataLossCount
                        .getKeyCounts() : 1)));
        double rtpLossRatioDouble = 0;
        if (!TextUtils.isEmpty(rtpLossRatio)) {
            try {
                rtpLossRatioDouble = Double.parseDouble(rtpLossRatio);
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage());
                rtpLossRatioDouble = 0;
            }
        }
        //RTP ??????(ms)
        TotalMeasureModel dataJitter = TotalDataByGSM.getHashMapMeasure(measureMap,
                TotalStruct.TotalMeasurePara._RFC1889_Jitter.name());
        double rtpJitter = dataJitter.getKeySum() == -9999 ? 0 : dataJitter.getKeySum();
        InnsVoLTEParams innsVoLTEParams = new InnsVoLTEParams();
        innsVoLTEParams.setFileNameDetail(fileNameDetail);
        innsVoLTEParams.setLogFileUUID(fileUUID);
        innsVoLTEParams.setConnRate(volteSuccessRatioDouble);
        innsVoLTEParams.setDropRate(volteDropRatio);
        innsVoLTEParams.setCallDelay(volteDelayInt);
        innsVoLTEParams.setMos3Rate(mosMore3_0Ratio);
        innsVoLTEParams.setMos35Rate(mosMore3_5Ratio);
        innsVoLTEParams.setImsSuccessRate(imsRegisterSucRatio);
        innsVoLTEParams.setEsrvccSuccessRate(eSRVCCSucRatioDouble);
        innsVoLTEParams.setEsrvccDelay(eSRVCCHandoverDelay);
        innsVoLTEParams.setRtpLostRate(rtpLossRatioDouble);
        innsVoLTEParams.setRtpShakeRate(rtpJitter);
        InnsDaoManager.getInstance(this).save(innsVoLTEParams);
        //??????????????????TXT??????
        transString2Txt(record, sb.toString(), filePath, fileName);
        //??????????????????????????????
        SharePreferencesUtil.getInstance(this).saveBoolean(InnsMapView.KEY_INNS_IS_MANUAL_MARK, false);

    }

    private void transString2Txt(TestRecord record, String data, String filePath, String fileName) {
        File file = new File(filePath);
        if (!file.exists() || !file.isDirectory())
            if (!file.mkdirs()) {
                LogUtil.e(TAG, "Directory create Error!");
            }
        try {
            File originFile = FileUtil.getFileFromBytes(data.getBytes("UTF-8"), filePath + fileName + ".txt");
            RecordDetail recordDetail = new RecordDetail();
            recordDetail.record_id = record.record_id;
            recordDetail.file_name = fileName + ".txt";
            recordDetail.file_guid = record.record_id + "_" + WalkStruct.FileType.TXT.getFileTypeId();
            recordDetail.file_size = originFile.length();
            recordDetail.file_path = filePath;
            recordDetail.file_type_str = WalkStruct.FileType.TXT.getFileTypeName();
            recordDetail.file_type = WalkStruct.FileType.TXT.getFileTypeId();
            record.getRecordDetails().add(recordDetail);
            FileDB.getInstance(this).syncTestRecord(record);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     * ???????????????????????????
     *
     * @param name ????????????
     * @return ?????????
     */
    private float getTotalValue(String name) {
        Map<String, Long> paras = TotalDataByGSM.getInstance().getUnifyTimes();
        float value = 0;
        try {
            value = Float.parseFloat(TotalDataByGSM.getHashMapValue(paras, name));
        } catch (Exception e) {
            //
        }
        return value;
    }

    /**
     * ???????????????????????????????????????System??????
     * 0 = UNKNOWN???1 = GPRS???2 = EDGE???3 = UMTS???4 = CDMA???5 = EVDO_0???6 = EVDO_A???7 = 1xRTT???8 = HSDPA???9 = HSUPA
     * 10 = HSPA???11 = IDEN???12 = EVDO_B???13 = LTE???14 = EHRPD???15 = HSPAP???16 = GSM???17 = TD_SCDMA???18 = IWLAN
     *
     * @return ???????????????System??????
     */
    private String getCellScanSystem() {
        String netTypeName = TraceInfoInterface.currentNetType.getNetTypeName();
        if (netTypeName.equalsIgnoreCase("UNKNOWN")) {
            return "0";
        } else if (netTypeName.equalsIgnoreCase("GPRS")) {
            return "1";
        } else if (netTypeName.equalsIgnoreCase("EDGE")) {
            return "2";
        } else if (netTypeName.equalsIgnoreCase("UMTS")) {
            return "3";
        } else if (netTypeName.equalsIgnoreCase("CDMA")) {
            return "4";
        } else if (netTypeName.equalsIgnoreCase("EVDO_0")) {
            return "5";
        } else if (netTypeName.equalsIgnoreCase("EVDO_A")) {
            return "6";
        } else if (netTypeName.equalsIgnoreCase("1xRTT")) {
            return "7";
        } else if (netTypeName.equalsIgnoreCase("HSDPA")) {
            return "8";
        } else if (netTypeName.equalsIgnoreCase("HSUPA")) {
            return "9";
        } else if (netTypeName.equalsIgnoreCase("HSPA")) {
            return "10";
        } else if (netTypeName.equalsIgnoreCase("IDEN")) {
            return "11";
        } else if (netTypeName.equalsIgnoreCase("EVDO_B")) {
            return "12";
        } else if (netTypeName.equalsIgnoreCase("LTE")) {
            return "13";
        } else if (netTypeName.equalsIgnoreCase("EHRPD")) {
            return "14";
        } else if (netTypeName.equalsIgnoreCase("HSPAP")) {
            return "15";
        } else if (netTypeName.equalsIgnoreCase("GSM")) {
            return "16";
        } else if (netTypeName.equalsIgnoreCase("TD_SCDMA")) {
            return "17";
        } else if (netTypeName.equalsIgnoreCase("IWLAN")) {
            return "18";
        } else {
            return "0";
        }
    }

    /**
     * ???????????????????????????????????????NetworkType2??????
     * 0 = ????????????1 = GSM???2 = WCDMA???3 = CDMA2000???4 = EVDO???5 = ?????????????????????6 = LTE???7 = TD-SCDMA???8 = ?????????????????????
     *
     * @return ?????????NetworkType2??????
     */
    private String getCellScanNetworkType2() {
        WalkStruct.CurrentNetState currentNetState = TraceInfoInterface.currentNetType;
        switch (currentNetState) {
            case GSM:
                return "1";
            case WCDMA:
                return "2";
            case CDMA:
                return "3";
            case LTE:
                return "6";
            case TDSCDMA:
                return "7";
            case Unknown:
                return "0";
            case NoService:
                return "0";
            default:
                return "0";
        }
    }

    /**
     * ??????CellMeas?????????????????????value
     *
     * @return ??????value
     */
    public static String getCellMeasMapEventParameters() {
        StringBuilder result = new StringBuilder();
        WalkStruct.CurrentNetState currentNetState = TraceInfoInterface.currentNetType;
        switch (currentNetState) {
            case GSM:
                String rxLevSub = TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_RxLevSub);
                result.append(TextUtils.isEmpty(rxLevSub) ? "0" : rxLevSub.equals("-9999") ? "0" : rxLevSub).append(",");
                break;
            case CDMA:
                //CDMA2000???EVDO
                String lpiRscp = "";//TODO
                String lpiEcio = TraceInfoInterface.getParaValue(UnifyParaID.C_TotalEcIo);
                result.append(TextUtils.isEmpty(lpiRscp) ? "0" : lpiRscp.equals("-9999") ? "0" : lpiRscp).append(",")
                        .append(TextUtils.isEmpty(lpiEcio) ? "0" : lpiEcio.equals("-9999") ? "0" : lpiEcio).append(",");
                break;
            case WCDMA:
                String lpiRscpW = TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_Total_RSCP);
                String lpiEcnoW = "";//TODO
                result.append(TextUtils.isEmpty(lpiRscpW) ? "0" : lpiRscpW.equals("-9999") ? "0" : lpiRscpW).append(",")
                        .append(TextUtils.isEmpty(lpiEcnoW) ? "0" : lpiEcnoW.equals("-9999") ? "0" : lpiEcnoW).append(",");
                break;
            case TDSCDMA:
                String lpiRscpT = TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_PCCPCHRSCP);
                String lpiEcnoT = "";//TODO
                result.append(TextUtils.isEmpty(lpiRscpT) ? "0" : lpiRscpT.equals("-9999") ? "0" : lpiRscpT).append(",")
                        .append(TextUtils.isEmpty(lpiEcnoT) ? "0" : lpiEcnoT.equals("-9999") ? "0" : lpiEcnoT).append(",");
                break;
            case LTE:
                String lpiRsrp = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_RSRP);
                String lpiRsrq = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_RSRQ);
                String lpiSnr = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_SINR);
                result.append(TextUtils.isEmpty(lpiRsrp) ? "0" : lpiRsrp.equals("-9999") ? "0" : lpiRsrp).append(",")
                        .append(TextUtils.isEmpty(lpiRsrq) ? "0" : lpiRsrq.equals("-9999") ? "0" : lpiRsrq).append(",")
                        .append(TextUtils.isEmpty(lpiSnr) ? "0" : lpiSnr.equals("-9999") ? "0" : lpiSnr).append(",");
                break;
            default:

        }
        return result.toString();
    }


    /**
     * ??????CellMeas?????????????????????value
     *
     * @return ??????value
     */
    private String getCellMeasMapEventParameters(MapEvent mapEvent) {
        StringBuilder result = new StringBuilder();
        WalkStruct.CurrentNetState currentNetState = TraceInfoInterface.currentNetType;
        switch (currentNetState) {
            case GSM:
                LocusParamInfo locusParamInfo = mapEvent.getParamInfoMap().get(KEY_MAP_EVENT_RX_LEV_SUB);
                result.append(locusParamInfo == null ? "0" : locusParamInfo.value == -9999 ? "0" : locusParamInfo.value).append(",");
                break;
            case CDMA:
                //CDMA2000???EVDO
                LocusParamInfo lpiRscp = mapEvent.getParamInfoMap().get(KEY_MAP_EVENT_RSCP);
                LocusParamInfo lpiEcio = mapEvent.getParamInfoMap().get(KEY_MAP_EVENT_EC_IO);
                result.append(lpiRscp == null ? "0" : lpiRscp.value == -9999 ? "0" : lpiRscp.value).append(",")
                        .append(lpiEcio == null ? "0" : lpiEcio.value == -9999 ? "0" : lpiEcio.value).append(",");
                break;
            case WCDMA:
            case TDSCDMA:
                LocusParamInfo lpiRscpT = mapEvent.getParamInfoMap().get(KEY_MAP_EVENT_RSCP);
                LocusParamInfo lpiEcno = mapEvent.getParamInfoMap().get(KEY_MAP_EVENT_EC_NO);
                result.append(lpiRscpT == null ? "0" : lpiRscpT.value == -9999 ? "0" : lpiRscpT.value).append(",")
                        .append(lpiEcno == null ? "0" : lpiEcno.value == -9999 ? "0" : lpiEcno.value).append(",");
                break;
            case LTE:
                LocusParamInfo lpiRsrp = mapEvent.getParamInfoMap().get(KEY_MAP_EVENT_RSRP);
                LocusParamInfo lpiRsrq = mapEvent.getParamInfoMap().get(KEY_MAP_EVENT_RSRQ);
                LocusParamInfo lpiSnr = mapEvent.getParamInfoMap().get(KEY_MAP_EVENT_SNR);
                result.append(lpiRsrp == null ? "0" : lpiRsrp.value == -9999 ? "0" : lpiRsrp.value).append(",")
                        .append(lpiRsrq == null ? "0" : lpiRsrq.value == -9999 ? "0" : lpiRsrq.value).append(",")
                        .append(lpiSnr == null ? "0" : lpiSnr.value == -9999 ? "0" : lpiSnr.value).append(",");
                break;
            default:

        }
        return result.toString();
    }

    /**
     * ??????CellScan????????????????????????
     *
     * @return ????????????
     */
    private String getCellScanParameters() {
        StringBuilder result = new StringBuilder();
        WalkStruct.CurrentNetState currentNetState = TraceInfoInterface.currentNetType;
        switch (currentNetState) {
            case GSM:
                String gsmMcc = TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_MCC);//???????????????
                String gsmMnc = TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_MNC);//???????????????
                String gsmLac = TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_LAC);//?????????
                String gsmCid = TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_Cell_ID);// ??????id
                String gsmArfcn = TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_BCCH);//???????????????
                String gsmBsic = TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_BSIC);//???????????????
                result.append(TextUtils.isEmpty(gsmMcc) ? 0 : gsmMcc).append(",")
                        .append(TextUtils.isEmpty(gsmMnc) ? 0 : gsmMnc).append(",")
                        .append(TextUtils.isEmpty(gsmLac) ? 0 : gsmLac).append(",")
                        .append(TextUtils.isEmpty(gsmCid) ? 0 : gsmCid).append(",")
                        .append(TextUtils.isEmpty(gsmArfcn) ? 0 : gsmArfcn).append(",")
                        .append(TextUtils.isEmpty(gsmBsic) ? 0 : gsmBsic);
                break;
            case WCDMA:
                String wcdmaMcc = TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_MCC);
                String wcdmaMnc = TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_MNC);
                String wcdmaLac = TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_LAC);
                String wcdmaCid = TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_Cell_ID);
                String wcdmaArfcn = "0";// ?????????
                String wcdmaScr = TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_Scr);//??????
                result.append(TextUtils.isEmpty(wcdmaMcc) ? 0 : wcdmaMcc).append(",")
                        .append(TextUtils.isEmpty(wcdmaMnc) ? 0 : wcdmaMnc).append(",")
                        .append(TextUtils.isEmpty(wcdmaLac) ? 0 : wcdmaLac).append(",")
                        .append(TextUtils.isEmpty(wcdmaCid) ? 0 : wcdmaCid).append(",")
                        .append(TextUtils.isEmpty(wcdmaArfcn) ? 0 : wcdmaArfcn).append(",")
                        .append(TextUtils.isEmpty(wcdmaScr) ? 0 : wcdmaScr);
                break;
            case CDMA:
                //CDMA2000???EVDO??????EVDO??????
                String evdoMcc = TraceInfoInterface.getParaValue(UnifyParaID.C_MCC);
                String evdoMnc = TraceInfoInterface.getParaValue(UnifyParaID.C_MNC);
                String evdoSid = TraceInfoInterface.getParaValue(UnifyParaID.C_SID);//??????????????? CDMA-systemId
                String evdoNid = TraceInfoInterface.getParaValue(UnifyParaID.C_NID);//??????????????? CDMA-networkId
                String evdoBid = TraceInfoInterface.getParaValue(UnifyParaID.C_BID);//??????????????? CDMAbasestationId
                String evdoArfcn = TraceInfoInterface.getParaValue(UnifyParaID.E_EV_Frequenc);
                String evdoPN = TraceInfoInterface.getParaValue(UnifyParaID.E_ServingSectorPN);//Pilot number
                result.append(TextUtils.isEmpty(evdoMcc) ? 0 : evdoMcc).append(",")
                        .append(TextUtils.isEmpty(evdoMnc) ? 0 : evdoMnc).append(",")
                        .append(TextUtils.isEmpty(evdoSid) ? 0 : evdoSid).append(",")
                        .append(TextUtils.isEmpty(evdoNid) ? 0 : evdoNid).append(",")
                        .append(TextUtils.isEmpty(evdoBid) ? 0 : evdoBid).append(",")
                        .append(TextUtils.isEmpty(evdoArfcn) ? 0 : evdoArfcn).append(",")
                        .append(TextUtils.isEmpty(evdoPN) ? 0 : evdoPN);
                break;
            case TDSCDMA:
                String tdScdmaMcc = TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_MCC);
                String tdScdmaMnc = TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_MNC);
                String tdScdmaLac = TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_LAC);//?????????
                String tdScdmaCid = TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_CellID);
                String tdScdmaArfcn = TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_UARFCN);
                String tdScdmaCPI = TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_CPI);//Cell params ID
                result.append(TextUtils.isEmpty(tdScdmaMcc) ? 0 : tdScdmaMcc).append(",")
                        .append(TextUtils.isEmpty(tdScdmaMnc) ? 0 : tdScdmaMnc).append(",")
                        .append(TextUtils.isEmpty(tdScdmaLac) ? 0 : tdScdmaLac).append(",")
                        .append(TextUtils.isEmpty(tdScdmaCid) ? 0 : tdScdmaCid).append(",")
                        .append(TextUtils.isEmpty(tdScdmaArfcn) ? 0 : tdScdmaArfcn).append(",")
                        .append(TextUtils.isEmpty(tdScdmaCPI) ? 0 : tdScdmaCPI);
                break;
            case LTE:
                String lteMcc = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_MCC);
                String lteMnc = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_MNC);
                String lteTac = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_TAC);
                String lteCid = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_CellID);
                String lteArfcn = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_EARFCN);
                String ltePCI = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_PCI);
                result.append(TextUtils.isEmpty(lteMcc) ? 0 : lteMcc).append(",")
                        .append(TextUtils.isEmpty(lteMnc) ? 0 : lteMnc).append(",")
                        .append(TextUtils.isEmpty(lteTac) ? 0 : lteTac).append(",")
                        .append(TextUtils.isEmpty(lteCid) ? 0 : lteCid).append(",")
                        .append(TextUtils.isEmpty(lteArfcn) ? 0 : lteArfcn).append(",")
                        .append(TextUtils.isEmpty(ltePCI) ? 0 : ltePCI);
                break;
            default:

        }
        return result.toString();
    }


    /**
     * ??????????????????
     *
     * @param filePath rcu????????????
     */
    private void createFile(String filePath) {
        if (filePath == null || this.mFilePath != null)
            return;
        if (!StringUtil.isNullOrEmpty(filePath) && filePath.endsWith(WalkStruct.FileType.ORGRCU.getExtendName())) {
            this.mFilePath = filePath.substring(0, filePath.length() - 5) + ".txt";
        }
        if (StringUtil.isNullOrEmpty(this.mFilePath))
            return;
        this.mThread = new MyFileThread();
        this.mThread.start();
    }

    /**
     * ????????????????????????
     */
    private TimerTask mLocationTask = new TimerTask() {

        @Override
        public void run() {
            mHandler.sendEmptyMessage(1);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "-----onCreate-----");
        this.mINNSLocationClient = INNSMapLocationClient.getInstance(this);
        this.mINNSLocationClient.setINNSMapLocationListener(this);
        this.mDatasetManager = DatasetManager.getInstance(this.getApplicationContext());
        this.mFilePath = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "-----onStartCommand-----");
        //???????????????????????????????????????????????????
        InnsLocationSetManager.getInstance().clear();
        this.mINNSLocationClient.start();
        this.registerReceiver();
        this.mLocationTimer = new Timer();
        this.mLocationTimer.schedule(this.mLocationTask, LOCATION_INTERVAL_TIME, LOCATION_INTERVAL_TIME);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * ?????????????????????
     */
    private void registerReceiver() {
        if(!hasRegisteredBroadcast){
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_CREATE_LOG_FILE);
            filter.addAction(WalkMessage.NOTIFY_TEST_FILE_CREATED);
            filter.addAction(PingTest.NOTIFY_PING_DATA_CHANGED);
            this.registerReceiver(this.mReceiver, filter);
            hasRegisteredBroadcast = true;
        }
    }

    @Override
    public void onFail(String arg0) {
        LogUtil.d(TAG, "-----onFail-----:" + arg0);
    }

    @Override
    public void onReceiveLocation(INNSMapLocation location) {
        EventBus.getDefault().post(new OnInnsLocationChangedEvent(location));
        if (location != null && (location.getX() != mPreLocation.getX() || location.getY() != mPreLocation.getY())) {
            INNSMapLocation iml = new INNSMapLocation();
            iml.setX(location.getX());
            iml.setY(location.getY());
            InnsLocationSetManager.getInstance().addLocation(new LocationWithMeasParameter(iml, getCellMeasMapEventParameters(), System.currentTimeMillis()));
            mPreLocation.setX(location.getX());
            mPreLocation.setY(location.getY());
            EventBus.getDefault().post(new OnReceiveInnsLocationEvent());
        }
        /*if (location != null) {
            LogUtil.d(TAG, "---onReceiveLocation---X:" + location.getX() + "---Y:" + location.getY());
            this.isLocationChange = true;
            this.mLastLocation = location;
            int flag = 0x30002;
            EventBytes.Builder(this).addInteger(1).addInteger(0)
                    .addInteger(0)
                    .addDouble(mLastLocation.getY()).addDouble(mLastLocation.getX())
                    .addSingle(0).addSingle(0).addSingle(0)
                    .addInteger(3).addSingle(0).writeGPSToRcu(flag);
            this.sendBroadcast(new Intent(ACTION_REFLASH_VIEW));
        }*/
    }


    @Override
    public void onDestroy() {
        if(hasRegisteredBroadcast){
            this.unregisterReceiver(this.mReceiver);
            hasRegisteredBroadcast = false;
        }
        this.mINNSLocationClient.stop();
        LogUtil.d(TAG, "-----onDestroy-----");
        if (this.mLocationTask != null) {
            this.mLocationTask.cancel();
            this.mLocationTask = null;
        }
        if (this.mLocationTimer != null) {
            this.mLocationTimer.cancel();
            this.mLocationTimer = null;
        }
        if (this.mThread != null) {
            this.mThread.stopThread();
            this.mThread = null;
            this.isCreateFile = false;
        }
        super.onDestroy();
    }

    /**
     * ?????????????????????????????????
     */
    private void writeLocation() {
        if (!this.isLocationChange || this.mLastLocation == null)
            return;
        // ?????????????????????????????????????????????????????????????????????
        if (!ApplicationModel.getInstance().isRcuFileCreated()) {
            return;
        }
        long time = System.currentTimeMillis() * 1000;
        StringBuffer mark = UtilsMethod.buildMarkStr(UtilsMethod.MARKSTATE_ADD, this.mLastLocation.getX(),
                this.mLastLocation.getY());
        LogUtil.w(TAG,
                "---write Location---X:" + this.mLastLocation.getX() + "---Y:" + this.mLastLocation.getY() + "--mark:" + mark);
        mDatasetManager.pushData(WalkCommonPara.MsgDataFlag_D, 0, 0, time, mark.toString().getBytes(), mark.length());
        this.writeLog();
        this.isLocationChange = false;
    }

    /**
     * ??????????????????
     */
    private void writeLog() {
        if (!this.isCreateFile)
            return;
    }

    /**
     * ??????????????????
     *
     * @author jianchao.wang
     */
    private static class MyHandler extends Handler {
        private static WeakReference<InnsmapTestService> reference;

        public MyHandler(InnsmapTestService service) {
            reference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            InnsmapTestService service = reference.get();
            if (msg.what == 1)
                service.writeLocation();
        }

    }

    /**
     * ????????????????????????
     *
     * @author jianchao.wang
     */
    private class MyFileThread extends Thread {
        /**
         * ??????????????????
         */
        private boolean isStop = false;
        /**
         * ??????????????????
         */
        private int INTERVAL_TIME = 10000;

        @Override
        public void run() {
            File file = new File(mFilePath);
            if (file.exists())
                return;

            try {
                file.createNewFile();
            } catch (IOException e) {
            }
            isCreateFile = true;
            BufferedWriter writer = null;
            FileWriter fileWriter = null;

            try {
                fileWriter = new FileWriter(file, true);
                writer = new BufferedWriter(fileWriter);

                while (isStop) {
                    try {
                        sleep(INTERVAL_TIME);
                    } catch (InterruptedException e) {
                    }
                    while (mLogList.size() > 0) {
                        String log = mLogList.remove(0);
                        writer.write(log);
                        writer.newLine();
                        writer.flush();
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                try {
                    if (fileWriter != null) {
                        fileWriter.close();
                    }
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                }
            }

        }

        public void stopThread() {
            this.isStop = true;
            this.interrupt();
        }

    }

}
