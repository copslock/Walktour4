package com.walktour.model;

import com.dinglicom.data.model.RecordDetail;
import com.dinglicom.data.model.TestRecord;
import com.dinglicom.ipack.FileRealTimeUpload;
import com.dinglicom.ipack.IpackControl;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.base.util.LogUtil;
import com.walktour.service.iPackTerminal;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * UMPC开始测试时相关测试信息存储
 *
 * @author tangwq
 */
public class UmpcTestInfo {
    private String tag = "UmpcTestInfo";
    public static final int ControlForNone = -1;
    public static final int ControlForIpad = 0;
    public static final int ControlForPioneer = 1;
    public static final int ControlForPioneerTools = 2;//与Pioneer的通信

    public static final int eTransMode_ReTrans = 1;
    public static final int eTransMode_ResumeTrans = 2;

    //定义IPACK下发文件类型所在位置
    //00001111|? ? ? ? cu dtlog orgrcu dcf
    private final DecimalFormat fTypeDcf = new DecimalFormat("00000000");
    public final int GEN_FILE_DCF = 7;
    public final int GEN_FILE_ORGRCU = 6;
    public final int GEN_FILE_DTLOG = 5;
    public final int GEN_FILE_CU = 4;
    public final int GEN_FILE_RCU = 3;
    public final int GEN_FILE_ECTI = 2;


    private String umpcStartInfo = "";    //UMPC开始测试时的所有信息记录
    private int indoor;                //0代表室内测试，1代表室外测试
    private int catchcap;                //是否生成抓包文件,0不生成，1生成抓包文件
    private String testlocale = "";    //测试结果文件名
    private String testmode = "";    //测试模式DT/CQT
    private String taskno = "";    //测试序号
    private String testgroupinfo = "";    //测试组信息
    private String testername = "";    //测试人员姓名
    private String phoneName = "";    //调备名称
    private long longitude;            //.000000经度
    private long latitude;            //0.000000	纬度
    private int valsamplecdma;        //CDMA采样频率（单位：毫秒）
    private int valsampletd;            //TD采样频率（单位：毫秒）
    private int valsamplewcdma;        //CDMA采样频率（单位：毫秒）
    private int datasize;                //自定义文件大小（单位：M）
    private int limittime;                //按时间分割文件大小(单位：分钟)
    private long guid;                //1258261422	UMPC生成一串唯一码用于后台匹配RCU文件，写入RCU文件头的RCUID区
    private int repeats = 1;     //外循环次数，为0时表示不循环
    private String encryptCode = "";    //文件加密密钥
    private int autoupload = 0;    //是否测试完成自动上传
    private int syncmode = 0;    //0代表组内同步，1代表全手机同步
    private int controller = 0;    //0:ipack,1:oldpioneer,2:newpioneer,...
    private int autosync = 0;    //是否上传实时文件，如果否，则不调实时所有接口  1表示自动上传
    private int atuPort = -1;    //IPACK下发的ATU问口
    private char[] genFileTypes = null;    //测试生成数据文件类型 1111|cu dtlog orgrcu dcf

    private float cuScale = 0;//单位米,表示1个像素是多少米
    private int cuPicType = 0;//图片类型,0-BMP,1-JPG,2-PNG
    private String cuPicName = "";//图片名称,UTF-8编码格式
    private boolean isRecordCall = false;//是否开启起呼录音功能
    private int callNet;//电信专项测试语音业务发起网络0---无限制；1---4G Only；2---3G Only；
    private int dataNet;//电信专项测试数据业务测试网络0---无限制；1---4G Only；2---3G Only；
    private String mDxSyncNetType = "NONE"; //电信主被叫网络同步

    private String[] syncfile = null;    //需要同步的文件，格式syncfile=rcu;dtlog;ddib;pcap;dcf文件后缀以分号间隔

    /**
     * 实时文件上传调用类
     */
    private FileRealTimeUpload fileUpload = null;
    private int sceneType = 0;//测试场景
    /**业务测试不同业务间的时间间隔,最小是10***/
    private int taskInterval=10;
    /**业务测试外循环间隔时长,最小是5**/
    private int repeatInterval=5;
    /**
     * 传入开始测试时的信息分解完存到相对应用值中。
     *
     * @param startInfo
     */
    public UmpcTestInfo(String startInfo) {
        try {
            LogUtil.w(tag, "---" + startInfo);
            umpcStartInfo = startInfo;

            String[] isUmpcTestInfo = startInfo.split(",");
            for (int i = 0; i < isUmpcTestInfo.length; i++) {
                String name = isUmpcTestInfo[i].substring(0, isUmpcTestInfo[i].indexOf("="));
				/*if(name.equals("indoor")){
				    //0 DT测试，1 CQT测试
					if(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]).equals("1")){
					    ApplicationModel.getInstance().setIndoorTest(true);
					    ApplicationModel.getInstance().setGpsTest(false);
					}else{
					    ApplicationModel.getInstance().setGpsTest(true);
					    ApplicationModel.getInstance().setIndoorTest(false);
					}
				}else*/
                if (name.equals("catchcap")) {
                    catchcap = Integer.parseInt(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]));
                } else if (name.equals("testlocale")) {
                    testlocale = UtilsMethod.getEqualsValue(isUmpcTestInfo[i]);
                } else if (name.equals("testername")) {
                    testername = UtilsMethod.getEqualsValue(isUmpcTestInfo[i]);
                }/*else if(name.equals("longitude")){
					longitude = Long.parseLong(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]));
				}else if(name.equals("latitude")){
					latitude = Long.parseLong(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]));
				}else if(name.equals("valsamplecdma")){
					valsamplecdma = Integer.parseInt(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]));
				}else if(name.equals("valsampletd")){
					valsampletd = Integer.parseInt(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]));
				}else if(name.equals("valsamplewcdma")){
					valsamplewcdma = Integer.parseInt(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]));
				}*/ else if (name.equals("datasize")) {
                    datasize = Integer.parseInt(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]));
                    //大于60认为是按时间分割,这是神一般的设计
                    if (datasize >= 60) {
                        limittime = datasize;
                        datasize = 0;
                    } else {
                        limittime = 0;
                    }
                } else if (name.equals("guid")) {
                    guid = Long.parseLong(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]));
                } else if (name.equals("repeats")) {
                    repeats = Integer.parseInt(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]));
                } else if (name.equals("encryptCode")) {
                    encryptCode = UtilsMethod.getEqualsValue(isUmpcTestInfo[i]);
                } else if (name.equals("autoupload")) {
                    autoupload = Integer.parseInt(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]));
                } else if (name.equals("phonename")) {
                    phoneName = UtilsMethod.getEqualsValue(isUmpcTestInfo[i]);
                } else if (name.equals("syncmode")) {
                    syncmode = Integer.parseInt(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]));
                }/*else if(name.equals("limittime")){
					limittime = Integer.parseInt(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]));
				}*/ else if (name.equals("Controller")) {
                    controller = Integer.parseInt(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]));
                } else if (name.equals("testmode")) {
                    testmode = UtilsMethod.getEqualsValue(isUmpcTestInfo[i]);
                } else if (name.equals("taskno")) {
                    taskno = UtilsMethod.getEqualsValue(isUmpcTestInfo[i]);
                } else if (name.equals("testgroupinfo")) {
                    testgroupinfo = UtilsMethod.getEqualsValue(isUmpcTestInfo[i]);
                } else if (name.equals("autosync")) {
                    autosync = Integer.parseInt(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]));
                } else if (name.equals("syncfile")) {
                    //下发的关键字为dtlog,此次需要转换为dtlog文件类型的扩展名 lte.dgz
                    String syncStr = UtilsMethod.getEqualsValue(isUmpcTestInfo[i]);
                    syncStr = syncStr.replaceAll("dtlog", FileType.DTLOG.getFileTypeName());
                    syncfile = syncStr.split(";");
                } else if (name.equals("ATUPort")) {
                    atuPort = Integer.parseInt(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]));
                } else if (name.equals("genfiletype")) {
                    //传过来的值为十进制,先传成二进制串,再当成十进制整形格式化为8位长度默认值为0的串,最后转换成char数组
                    genFileTypes = fTypeDcf.format(Integer.parseInt(Integer.toBinaryString(Integer.parseInt(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]))))).toCharArray();
                } else if (name.equals("cu_scale")) {
                    cuScale = Float.parseFloat(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]));
                } else if (name.equals("cu_pictype")) {
                    cuPicType = Integer.parseInt(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]));
                } else if (name.equals("cu_picname")) {
                    cuPicName = UtilsMethod.getEqualsValue(isUmpcTestInfo[i]);
                } else if (name.equals("recordcall")) {
                    String recordCall = UtilsMethod.getEqualsValue(isUmpcTestInfo[i]);
                    if (null != recordCall && recordCall.equals("1")) {
                        isRecordCall = true;
                    }
                } else if (name.equals("callnet")) {
					/*String callNetStr = UtilsMethod.getEqualsValue(isUmpcTestInfo[i]);
					if(!TextUtils.isEmpty(callNetStr)){
						try {
							callNet = Integer.parseInt(callNetStr);
							ConfigRoutine.getInstance().setTelecomVoiceNetSetting(WalktourApplication.getAppContext(), callNet);
							LogUtil.d(tag,"setTelecomVoiceSetting " + callNet);
						}catch (Exception e){
							LogUtil.e(tag,"Parse String to Int exception on callNet" + e.getMessage());
						}
					}*/
                } else if (name.equals("datanet")) {
					/*String dataNetStr = UtilsMethod.getEqualsValue(isUmpcTestInfo[i]);
					if(!TextUtils.isEmpty(dataNetStr)){
						try {
							dataNet = Integer.parseInt(dataNetStr);
							ConfigRoutine.getInstance().setTelecomDataNetSetting(WalktourApplication.getAppContext(), dataNet);
							LogUtil.d(tag,"setTelecomDataNetSetting " + dataNet);
						}catch (Exception e){
							LogUtil.e(tag,"Parse String to Int exception on dataNet" + e.getMessage());
						}
					}*/
                } else if (name.equals("DxSyncNetType")) {
                    mDxSyncNetType = UtilsMethod.getEqualsValue(isUmpcTestInfo[i]);
                } else if (name.equals("sceneMode")) {
                    sceneType = Integer.parseInt(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]));
                } else if (name.equals("taskInterval")){//业务测试业务间间隔
                    taskInterval=Integer.parseInt(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]));
                    if(taskInterval<10){
                        taskInterval=10;
                    }
                }else if (name.equals("repeatInterval")){//业务测试大循环间隔
                    repeatInterval=Integer.parseInt(UtilsMethod.getEqualsValue(isUmpcTestInfo[i]));
                    if(repeatInterval<5){
                        repeatInterval=5;
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.w(tag,"iPackterminal参数传递错误,请核查.\n"+e.getMessage());
            e.printStackTrace();
        }
    }

    public String getUmpcStartInfo() {
        return umpcStartInfo;
    }

    /**
     * 是否生成抓包文件
     *
     * @return
     */
    public boolean isCatchcap() {
        return catchcap == 1;
    }

    public int getSceneType() {
        return sceneType;
    }

    public void setSceneType(int sceneType) {
        this.sceneType = sceneType;
    }

    public String getTestlocale() {
        return testlocale;
    }

    public String getTestername() {
        return testername;
    }

    public long getLongitude() {
        return longitude;
    }

    public long getLatitude() {
        return latitude;
    }

    public int getValsamplecdma() {
        return valsamplecdma;
    }

    public int getValsampletd() {
        return valsampletd;
    }

    public int getValsamplewcdma() {
        return valsamplewcdma;
    }

    public int getDatasize() {
        return datasize;
    }

    public long getGuid() {
        return guid;
    }

    public int getRepeats() {
        return repeats;
    }

    public String getEncryptCode() {
        return encryptCode;
    }

    public boolean isAutoupload() {
        return autoupload == 1;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public int getLimitTime() {
        return limittime;
    }

    public float getCuScale() {
        return cuScale;
    }

    public int getCuPicType() {
        return cuPicType;
    }

    public String getCuPicName() {
        return cuPicName;
    }

    /**
     * 获得当连接控制端状态
     * 0:ipack,1:oldpioneer,2:newpioneer,...
     */
    public int getController() {
        return controller;
    }

    /**
     * 外循环同步类型 0代表组内同步，1代表全手机同步
     */
    public int getSyncModel() {
        return syncmode;
    }

    public String getTag() {
        return tag;
    }

    public int getIndoor() {
        return indoor;
    }

    public String getTestmode() {
        return testmode;
    }

    public String getTaskno() {
        return taskno;
    }

    public String getTestgroupinfo() {
        return testgroupinfo;
    }

    public int getLimittime() {
        return limittime;
    }

    public int getSyncmode() {
        return syncmode;
    }

    public boolean getAutosync() {
        return autosync == 1;
    }

    public String[] getSyncfile() {
        return syncfile;
    }

    public int getAtuPort() {
        return atuPort;
    }

    public boolean isRecordCall() {
        return isRecordCall;
    }

    public void setRecordCall(boolean recordCall) {
        isRecordCall = recordCall;
    }

    public int getCallNet() {
        return callNet;
    }

    public void setCallNet(int callNet) {
        this.callNet = callNet;
    }

    public int getDataNet() {
        return dataNet;
    }

    public void setDataNet(int dataNet) {
        this.dataNet = dataNet;
    }

    public String getDxSyncNetType() {
        return mDxSyncNetType;
    }

    public void setDxSyncNetType(String dxSyncNetType) {
        this.mDxSyncNetType = dxSyncNetType;
    }

    public int getTaskInterval()
    {
        return taskInterval;
    }

    public int getRepeatInterval()
    {
        return repeatInterval;
    }

    /**
     * 获得生成文件类型
     * 如果不为null,结果为char[] 数组,相应位置为1时表示生成该类型文件
     * 从最后一位往前类型以次表示 dcf org.rcu dtlog cu
     *
     * @return
     */
    public char[] getGenFileTypes() {
        return genFileTypes;
    }


    /**
     * 测试文件重命名
     * 将当前所有的文件修改为除路径扩展名外,其它一致的名字
     *
     * @param reName
     */
    public void filesRename(String reName, String deviceId, TestRecord testRecord) {
        if (getAutosync() && testRecord != null) {
            for (RecordDetail detail : testRecord.getRecordDetails()) {

                iPackTerminal.SendRTFileUpdate(String.format(IpackControl.getInstance().getSendRTFileUpdateFormat(), detail.file_guid, deviceId,
                        reName.substring(0, reName.lastIndexOf(".")) + FileType.getFileType(detail.file_type).getExtendName()));
            }
        }
    }

    /**
     * 如果当前为上传实时文件，由当前方法触发上传实时文件动作
     */
    public void setRTUploadStart(HashMap<String, RecordDetail> details) {
        if (getAutosync()) {
            fileUpload = new FileRealTimeUpload();
            fileUpload.startRTFileUpload(this, details);
        }
    }

    /**
     * 如果当前为上传实时文件，测试结果由此方法设置退出上传回调
     */
    public void setRTUploadEnd() {
        if (getAutosync() && fileUpload != null) {
            fileUpload.stopRTFileUplad();
        }
    }

    /**
     * 用于等待发送文件关闭完成状态
     *
     * @return
     */
    public boolean isUploadEnd() {
        if (getAutosync() && fileUpload != null) {
            return fileUpload.isFileEnd();
        }

        return true;
    }

    /**
     * 当收到登陆成功后，重设文件实时上传接口
     */
    public void reRTUploadStart() {
        if (getAutosync() && fileUpload != null) {
            fileUpload.reStartRTFileUpload();
        }
    }
}
