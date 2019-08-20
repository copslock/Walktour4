package com.walktour.Utils;

import java.util.Map;

/**
 * PioneerInfo
 *
 * @author weirong.fan
 */
public class ForPioneerInfo {
    /**
     * 测试状态：测试中
     */
    public static final int TESTSTATUS_TESTTING = 1;
    /**
     * 测试状态：停止
     */
    public static final int TESTSTATUS_TEST_STOP = 0;
    /**
     * 唯一实例
     */
    private static ForPioneerInfo sInstance = null;
    /**
     * 统计数据
     */
    private TotalDataByGSM mTotalData = null;
    /**
     * 默认空值
     */
    private static final int sNoneValue = -9999;
    /**
     * 测试状态，1测试中，0停止测试
     */
    private int mTestStatus = sNoneValue;
    /**
     * 当前网络类型“G”:GSM参数;“T”:TD参数;“W”:WCDMA参数;“C”:CDMA2000参数;“L”:LTE参数;“V”:EVDO参数
     */
    private String mCurrentNet = "";
    /**
     * （业务类型）：如FTP_DL, FTP_UL, Dial
     */
    private WalkStruct.TestKind mTestKind = null;
    /**
     * （当前大循环次数--即当前业务是第几次大循环--在每次大循环开始时报）
     */
    private int mCurrentOuterLoop = sNoneValue;
    /**
     * 为外循环总次数
     */
    private int mOuterLoop = sNoneValue;
    /**
     * （当前小循环次数--即当前业务是第几次小循环--在每次小循环开始时报）
     */
    private int mCurrentLoop = sNoneValue;
    /**
     * 为小循环总次数
     */
    private int mLoop = sNoneValue;
    /**
     * （业务测试是否成功，0否，1是--每次小循环结束（正常结束或者异常结束）时报一次）
     */
    private boolean isSuccess = false;
    /**
     * （是否并行，0否，1是）
     */
    private boolean isMutil = false;
    /**
     * 任务序号
     */
    private int mTemplateIndex = sNoneValue;

    private ForPioneerInfo() {
        this.mTotalData = TotalDataByGSM.getInstance();
    }

    public static ForPioneerInfo getInstance() {
        if (sInstance == null) {
            sInstance = new ForPioneerInfo();
        }
        return sInstance;
    }

    /**
     * 设置当前测试状态,开始测试时设置为1,结束时设置状态为0
     *
     * @param status 测试状态
     */
    public void setTestStatus(int status) {
        this.mTestStatus = status;
    }

    /**
     * 网络变化时设置当前网络类型,类型值: “G”:GSM参数 “T”:TD参数 “W”:WCDMA参数 “C”:CDMA2000参数
     * “L”:LTE参数 “V”:EVDO参数
     *
     * @param net 当前网络
     */
    public void setCurrentNet(String net) {
        this.mCurrentNet = net;
    }

    /**
     * 设置外循环总次数
     *
     * @param outerLoop 外循环次数
     */
    public void setOuterLoop(int outerLoop) {
        this.mOuterLoop = outerLoop;
    }

    /**
     * 设置小循环总次数
     *
     * @param loop 小循环次数
     */
    public void setLoop(int loop) {
        this.mLoop = loop;
    }

    /**
     * （业务类型）：如FTP_DL, FTP_UL, Dial，定义请看后面的TestKind说明
     *
     * @param kind the mTestKind to set
     */
    public void setTestKind(int kind) {
        this.mTestKind = WalkStruct.TestKind.getTestKind(kind);
    }

    /**
     * 当前大循环次数--即当前业务是第几次大循环--在每次大循环开始时报
     *
     * @param currentOuterLoop the mCurrentOuterLoop to set
     */
    public void setCurrentOuterLoop(int currentOuterLoop) {
        this.mCurrentOuterLoop = currentOuterLoop;
    }

    /**
     * @param currentLoop the mCurrentLoop to set
     */
    public void setCurrentLoop(int currentLoop) {
        this.mCurrentLoop = currentLoop;
    }

    /**
     * 业务测试是否成功，0否，1是--每次小循环结束（正常结束或者异常结束）时报一次
     *
     * @return the bSuccess
     */
    public boolean isSuccess() {
        return this.isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public void setMutil(boolean isMutil) {
        this.isMutil = isMutil;
    }

    public void setTemplateIndex(int templateIndex) {
        this.mTemplateIndex = templateIndex;
    }

    /**
     * 返回外循环开始节点参数
     */
    public String getOuterLoopStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("CurrOuterLoop=");
        sb.append(mCurrentOuterLoop);
        sb.append(",OuterLoop=");
        sb.append(mOuterLoop);
        sb.append(getNormalStr());
        sb.append(getStaticStr());
        return sb.toString();
    }

    /**
     * 返回任务次数序号 包含外循环节点信息
     */
    public String getTemplateIndexStr() {
        StringBuilder sb = new StringBuilder();
        sb.append(getOuterLoopStr());
        sb.append(",TemplateIndex=");
        sb.append(mTemplateIndex);
        return sb.toString();
    }

    /**
     * 返回小循环次数相关字串 包含任务次数序号,外循环信息
     */
    public String getCurrentLoopStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("TestKind=");
        if (this.mTestKind != null)
            sb.append(mTestKind.getKind());
        else
            sb.append(sNoneValue);
        sb.append(",Loop=");
        sb.append(mLoop);
        sb.append(",CurrLoop=");
        sb.append(mCurrentLoop);
        sb.append(",");
        sb.append(getTemplateIndexStr());
        sb.append(",Mutil=");
        sb.append(isMutil ? 1 : 0);

        return sb.toString();
    }

    /**
     * 返回测试结果字符串
     */
    public String getTestSuccessStr(String startInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append(startInfo);
        sb.append(",Success=");
        sb.append(isSuccess ? 1 : 0);
        sb.append(getStaticStr());
        return sb.toString();
    }

    /**
     * 返回往IPAD发送的通用串
     *
     * @return 通用串
     */
    private String getNormalStr() {
        StringBuilder sb = new StringBuilder();
        sb.append(",TestStatus=");
        sb.append(mTestStatus);
        sb.append(",CurrentNet=");
        sb.append(mCurrentNet);

        return sb.toString();
    }

    /**
     * 往IPAD端返回实时统计信息 该返回值以逗号,开头,故后续使用不需要在前面加,
     *
     * @return 实时统计信息
     */
    private String getStaticStr() {
        Map<String, Long> unify = mTotalData.getUnifyTimes();
        Map<String, Long> param = mTotalData.getPara();
        Map<String, Long> event = mTotalData.getEvent();
        Map<String, Map<String, Map<String, Long>>> special = mTotalData.getSpecialTimes();

        StringBuilder realStatic = new StringBuilder(",StaticInfo=[");
        realStatic.append(getMapKeyValue(unify));
        realStatic.append(getMapKeyValue(param));
        realStatic.append(getMapKeyValue(event));
        realStatic.append(getSpecialMapKeyValue(special));

        realStatic.append("]");

        return realStatic.toString();
    }

    /**
     * 迭代返回传入HashMap对象的值对
     *
     * @param map 统计值映射
     * @return 字符串
     */
    private String getSpecialMapKeyValue(Map<String, Map<String, Map<String, Long>>> map) {
        if (this.mTestKind == null)
            return "";
        switch (this.mTestKind) {
            case tkPing:
                return this.getPingTotalValue(map);
        }
        return "";
    }

    /**
     * 获取系统使用的网络类型
     * G”:GSM参数 “T”:TD参数 “W”:WCDMA参数 “C”:CDMA2000参数
     * “L”:LTE参数 “V”:EVDO参数
     */
    private String getNetType() {
        String net;
        switch (this.mCurrentNet) {
            case "G":
                net = "GSM";
                break;
            case "T":
                net = "TDSCDMA";
                break;
            case "W":
                net = "WCDMA";
                break;
            case "C":
                net = "CDMA";
                break;
            case "L":
                net = "LTE";
                break;
            case "V":
                net = "EVDO";
                break;
            default:
                net = "Other";
                break;
        }
        return net;
    }

    /**
     * 获取ping业务统计
     *
     * @param map 统计值
     */
    private String getPingTotalValue(Map<String, Map<String, Map<String, Long>>> map) {
        String net = this.getNetType();
        StringBuilder sb = new StringBuilder();
        if (map.keySet().contains(net)) {
            Map<String, Map<String, Long>> value = map.get(net);
            if (value == null)
                return sb.toString();
            for (String key : value.keySet()) {
                Map<String, Long> map1 = value.get(key);
                if (!map1.isEmpty()) {
                    sb.append(this.getMapKeyValue(map1));
                }

            }
        }
        return sb.toString();
    }

    /**
     * 迭代返回传入HashMap对象的值对
     *
     * @param map 统计值映射
     * @return 字符串
     */
    private String getMapKeyValue(Map<String, Long> map) {
        StringBuilder buffer = new StringBuilder();
        for (String key : map.keySet()) {
            buffer.append(key);
            buffer.append("=");
            buffer.append(map.get(key));
            buffer.append(";");
        }
        return buffer.toString();
    }
}
