package com.walktour.Utils;

import android.util.FloatMath;

import com.dinglicom.dataset.model.ENDCDataModel;
import com.github.mikephil.charting.data.Entry;
import com.walktour.gui.map.GenericData;
import com.walktour.model.YwDataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class ShowInfo {
    /**
     * 业务类型,下行业务
     **/
    public static final int DIRECT_TYPE_DOWN = 0;
    /**
     * 业务类型,上行业务
     **/
    public static final int DIRECT_TYPE_UP = 1;
    public static GenericData data = new GenericData();
    public static final String defaultChatName = "defaultTaskName";
    private static ShowInfo sInstance = new ShowInfo();
    private YwDataModel defaultData = new YwDataModel();
    private HashMap<String, YwDataModel> ywDataList = new HashMap<String, YwDataModel>();
    private ENDCDataModel endcDataModel = new ENDCDataModel();

    private float totaLtelAll;//LtE总值
    private float maxLte;
    private int dataLteNum;//LtE个数
    private ArrayList<Entry> ltePoint=new ArrayList<>();//lte的点，用来绘制图表

    private float totaNRlAll;//NR总值
    private float maxNR;
    private int dataNRNum;//NR个数
    private ArrayList<Entry> nrPoint=new ArrayList<>();//nr的点，用来绘制图表

    /**
     * 默认是下行业务
     **/
    private int type = DIRECT_TYPE_DOWN;

    /***
     * 私有构造器,防止外部构造
     */
    private ShowInfo() {
    }

    public static ShowInfo getInstance() {
        return sInstance;
    }

    public void SetChartProperty() {
        WalkStruct.TaskType taskType = ApplicationModel.getInstance().getCurrentTask();
        if (taskType == null)
            return;
    }

    /**
     * 获得图表显示对象
     *
     * @return
     */
    public YwDataModel getYwDataModel() {
        // 如果业务数据Hash表中未有数据,则返回默认对象,否则,返回Hash表的第一个对象
        if (ywDataList.size() <= 0) {
            return defaultData;
        } else {
            return ywDataList.entrySet().iterator().next().getValue();
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * 返回指定名称图表显示对象
     *
     * @param name
     * @return
     */
    public YwDataModel getYwDataModel(String name) {
        if (ywDataList.containsKey(name)) {
            return ywDataList.get(name);
        } else {
            return defaultData;
        }
    }

    /**
     * 获得图表对象列表信息
     *
     * @return
     */
    public HashMap<String, YwDataModel> getYwDataModelList() {
        return ywDataList;
    }

    public ENDCDataModel getEndcDataModel() {
        return endcDataModel;
    }

    public void setEndcDataModel(ENDCDataModel endcDataModel) {
        this.endcDataModel = endcDataModel;
        totalMean(endcDataModel);
    }

    /*统计平均数,最大值*/
    private void totalMean(ENDCDataModel endcDataModel) {
        /*TODO 获取数据并显示*/
        WalkStruct.CurrentNetState netType = TraceInfoInterface.currentNetType;
        boolean is5G = (netType == WalkStruct.CurrentNetState.ENDC);
        if (is5G) {
            if (ShowInfo.getInstance().getType() == ShowInfo.DIRECT_TYPE_DOWN) {
                totaLtelAll += endcDataModel.getL_Thr_DL_PDCP_Thr();//    LTE速度，单位是bps         1kbps=1024bps;
                totaNRlAll += endcDataModel.getNR_Thr_DL_PDCP_Thr();//    NR速度，单位是bps           1kbps=1024bps;
                ltePoint.add(new Entry(dataLteNum,endcDataModel.getL_Thr_DL_PDCP_Thr()/1024/1024));  //1Mbps =1024*1024 bps
                nrPoint.add(new Entry(dataLteNum,endcDataModel.getNR_Thr_DL_PDCP_Thr()/1024/1024));  //1Mbps =1024*1024 bps
                dataLteNum += 1;
                dataNRNum += 1;
                if (maxLte < endcDataModel.getL_Thr_DL_PDCP_Thr()) {
                    maxLte = endcDataModel.getL_Thr_DL_PDCP_Thr();
                }
                if (maxNR < endcDataModel.getNR_Thr_DL_PDCP_Thr()) {
                    maxNR = endcDataModel.getNR_Thr_DL_PDCP_Thr();
                }
            } else if (ShowInfo.getInstance().getType() == ShowInfo.DIRECT_TYPE_UP) {
                totaLtelAll += endcDataModel.getL_Thr_UL_PDCP_Thr();//    LTE速度，单位是bps       1kbps=1024bps;
                totaNRlAll += endcDataModel.getNR_Thr_UL_PDCP_Thr();//    NR速度，单位是bps          1kbps=1024bps;
                ltePoint.add(new Entry(dataLteNum,endcDataModel.getL_Thr_UL_PDCP_Thr()/1024/1024));  //1Mbps =1024*1024 bps
                nrPoint.add(new Entry(dataLteNum,endcDataModel.getNR_Thr_UL_PDCP_Thr()/1024/1024));  //1Mbps =1024*1024 bps
                dataLteNum += 1;
                dataNRNum += 1;
                if (maxLte < endcDataModel.getL_Thr_UL_PDCP_Thr()) {
                    maxLte = endcDataModel.getL_Thr_UL_PDCP_Thr();
                }
                if (maxNR < endcDataModel.getNR_Thr_UL_PDCP_Thr()) {
                    maxNR = endcDataModel.getNR_Thr_UL_PDCP_Thr();
                }
            }
        }
    }

    public ArrayList<Entry> getLtePoint() {
        return ltePoint;
    }


    public ArrayList<Entry> getNrPoint() {
        return nrPoint;
    }


    public void clearData() {
        type=DIRECT_TYPE_DOWN;
        totaLtelAll = 0;//LtE总值
        maxLte = 0;
        dataLteNum = 0;//LtE个数
        ltePoint.clear();//lte的点，用来绘制图表

        totaNRlAll = 0;//NR总值
        maxNR = 0;
        dataNRNum = 0;//NR个数
        nrPoint.clear();//nr的点，用来绘制图表
    }


    /*得到NR速度的平均值*/
    public float getMeanNR() {
        if (dataNRNum == 0) {
            return 0;
        }
        return totaNRlAll / dataNRNum;
    }

    /*得到Lte速度的平均值*/
    public float getMeanLte() {
        if (dataLteNum == 0) {
            return 0;
        }
        return totaLtelAll / dataLteNum;
    }

    public float getMaxLte() {
        return maxLte;
    }


    public float getMaxNR() {
        return maxNR;
    }

    /**
     * FTP测试过程中将当前速率，平均速率，测试时间等信息显示在数据页面及图表中
     *
     * @param itData
     */
    public synchronized void SetChartData(String taskName, Iterator<?> itData) {
        try {
            ClearCurrentPara();

            if (itData == null || !itData.hasNext()) {
                setChartYWDataByNull();
            }

            YwDataModel ywData = null;
            if (ywDataList.containsKey(taskName)) {
                ywData = ywDataList.get(taskName);
            } else {
                ywData = new YwDataModel();
            }

            while (itData != null && itData.hasNext()) {
                Map.Entry<?, ?> ent = (Map.Entry<?, ?>) itData.next();
                SetApplicationInfo(ywData, ent);
            }

            ywDataList.put(taskName, ywData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setChartYWDataByNull() {
        TraceInfoInterface.traceData.setMapParamInfo(Integer.toHexString(UnifyParaID.QOS_FTP_UlThr), "");
        TraceInfoInterface.traceData.setMapParamInfo(Integer.toHexString(UnifyParaID.QOS_FTP_DlThr), "");

        TraceInfoInterface.decodeResultUpdate(UnifyParaID.QOS_FTP_UlThr, "");
        TraceInfoInterface.decodeResultUpdate(UnifyParaID.QOS_FTP_DlThr, "");
    }

    private void SetApplicationInfo(YwDataModel ywData, Map.Entry<?, ?> ent) {
        WalkStruct.DataTaskValue value = WalkStruct.DataTaskValue.valueOf(ent.getKey().toString());
        switch (value) {
            case FtpDlThrput:
                ywData.setFtpDlThrput(ent.getValue().toString());
                break;
            case FtpUlThrput:
                ywData.setFtpUlThrput(ent.getValue().toString());
                break;
            case FtpDlMeanRate:
                ywData.setFtpDlMeanRate(ent.getValue().toString());
                break;
            case FtpUlMeanRate:
                ywData.setFtpUlMeanRate(ent.getValue().toString());
                break;
            case FtpDlProgress:
                ywData.setFtpDlProgress(ent.getValue().toString());
                break;
            case FtpUlProgress:
                ywData.setFtpUlProgress(ent.getValue().toString());
                break;
            case ActiveThreadNum:
                ywData.setFtpActivityThread(ent.getValue().toString());
                break;
            case PingDelay:
                ywData.setPingDelay(ent.getValue().toString());
                break;
            case HttpDlThrput:
                ywData.setHttpDlThrput(ent.getValue().toString());
                break;
            case WapDlThrput:
                ywData.setWapDlThrput(ent.getValue().toString());
                break;
            case Pop3Thrput:
                ywData.setPop3Thrput(ent.getValue().toString());
                break;
            case SmtpThrput:
                ywData.setSmtpThrput(ent.getValue().toString());
                break;
            case PeakValue:
                ywData.setPeakValue(ent.getValue().toString());
                break;
            case useTimes:
                ywData.setUseTimes(ent.getValue().toString());
                break;
            case FtpDlAllSize:
                ywData.setFtpDlAllSize(ent.getValue().toString());
                break;
            case FtpUlAllSize:
                ywData.setFtpUlAllSize(ent.getValue().toString());
                break;
            case FtpDlCurrentSize:
                ywData.setFtpDlCurrentSize(ent.getValue().toString());
                break;
            case FtpUlCurrentSize:
                ywData.setFtpUlCurrentSize(ent.getValue().toString());
                break;

            // 仪表盘数据设置
            case BordLeftTitle: // 左标题
                ywData.setBordLeftTitle(ent.getValue().toString());
                break;
            case BordRightTile: // 右标题
                ywData.setBordRightTile(ent.getValue().toString());
                break;
            case BordCurrentSpeed: // 当前速率
                ywData.setBordCurrentSpeed(Float.parseFloat(ent.getValue().toString()));
                ywData.addBordPoint(ywData.getBordCurrentSpeed());
                break;
            // case BordPoints: //当前速率数组
            // ywData.setBordPoints((float[])ent.getValue());
            // break;
            case BordProgress: // 当前进度
                ywData.setBordProgress((int) Float.parseFloat(ent.getValue().toString()));
                break;
            case IsWlanTest:
                ywData.setWlanTest(Boolean.parseBoolean(ent.getValue().toString()));
                break;
            default:
                break;
        }
    }

    /**
     * [任务结束时清除相关的显示信息]<BR>
     * [当前数据任务结束时，清除图表，折线图及数据界面的数据业务信息]
     */
    public void ClearQueueInfo(String taskName) {
        if (ywDataList.containsKey(taskName)) {
            ywDataList.remove(taskName);
        } else {
            ywDataList.clear();
            // defaultData = new YwDataModel();
        }
        setChartYWDataByNull();

        if (data != null) {
            data.clearPara_Queue1();
            data.clearPara_Queue2();
            data.clearPara_Queue3();
            data.clearPara_Queue4();
        }
    }

    private void ClearCurrentPara() {
        data.ClearCurrentPara();
    }
}
