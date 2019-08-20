package com.walktour.model;

import android.content.Context;

import com.dinglicom.DataSetLib;
import com.dinglicom.dataset.DatasetManager;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class YwDataModel {
    private final String tag = "YwDataModel";
        
	private String ftpDlAllSize;	//下载总大小	
	private String ftpUlAllSize;	//上传总大小
	private String ftpDlCurrentSize;//当前上传大小
	private String ftpUlCurrentSize;//当前下载大小
	private String ftpDlThrput;		//上传速度
	private String ftpUlThrput; 	//下载速度
	private String ftpDlMeanRate; 	//下载平均速率
	private String ftpUlMeanRate; 	//上传平均速率
	private String ftpDlProgress; 	//下传进度
	private String ftpUlProgress; 	//上传进度
	private String ftpActivityThread;//FTP当前活动线程数
	private String pingDelay; 		//PING 时延
	private String httpDlThrput; 	//HTTP速率
	private String wapDlThrput; 	//WAP速率
	private String pop3Thrput; 		//POP3速率
	private String smtpThrput; 		//SMTP速率
	private String peakValue; 		//峰值
	private String useTimes; 		//测试时长
	
	//数据仪表盘公用字段
	private String bordLeftTitle = "";
	private String bordRightTile = "";
	private float bordCurrentSpeed;
	private Queue<Float> bordPoints = null;
	private Float[] tempBordPoints  = null ;
	private boolean isWlanTest		= false;
	
	private int bordProgress = -999;
	private final int pointLength = 41;    //图表折线图点数
	
	private int palybackTaskType = -1;
	
	public YwDataModel(){
	    LogUtil.w(tag,"--YwDataModel--");
		ftpDlAllSize= "";
		ftpUlAllSize= "";
		ftpDlCurrentSize="";
		ftpUlCurrentSize="";
		ftpDlThrput = "" ;
		ftpUlThrput = "" ; 
		ftpDlMeanRate = "" ; 
		ftpUlMeanRate = "" ; 
		ftpDlProgress = "" ; 
		ftpUlProgress = "" ; 
		ftpActivityThread = "";
		pingDelay = "" ; 
		httpDlThrput = "" ; 
		wapDlThrput = "" ; 
		pop3Thrput = "" ; 
		smtpThrput = "" ; 
		peakValue = "" ; 
		useTimes = "" ; 
	}
	
	public synchronized void clearData(){
	    LogUtil.w(tag,"--clearData--");
		ftpDlAllSize= "";
		ftpUlAllSize= "";
		ftpDlCurrentSize="";
		ftpUlCurrentSize="";
		ftpDlThrput = "" ;
		ftpUlThrput = "" ; 
		ftpDlMeanRate = "" ; 
		ftpUlMeanRate = "" ; 
		ftpDlProgress = "" ; 
		ftpUlProgress = "" ; 
		ftpActivityThread = "";
		pingDelay = "" ; 
		httpDlThrput = "" ; 
		wapDlThrput = "" ; 
		pop3Thrput = "" ; 
		smtpThrput = "" ; 
		peakValue = "" ; 
		useTimes = "" ; 
		
		bordLeftTitle = "";
		bordRightTile = "";
		bordCurrentSpeed = 0;
		bordPoints     = null;
		tempBordPoints = null;
		bordProgress   = -999;
	}
	
	private Queue<Float> initQueue(){
	    Queue<Float> temp = new LinkedBlockingQueue<Float>();
	    tempBordPoints = new Float[pointLength];
	    
	    for(int i=0;i<pointLength;i++){
            temp.add(0f);
            tempBordPoints[i] = 0f;
        }
	    return temp;
	}
	
	public synchronized String getFtpDlThrput() {
		return ftpDlThrput;
	}
	public synchronized void setFtpDlThrput(String ftpDlThrput) {
		this.ftpDlThrput = ftpDlThrput;
	}
	public synchronized String getFtpUlThrput() {
		return ftpUlThrput;
	}
	public synchronized void setFtpUlThrput(String ftpUlThrput) {
		this.ftpUlThrput = ftpUlThrput;
	}
	public synchronized String getFtpDlMeanRate() {
		return ftpDlMeanRate;
	}
	public synchronized void setFtpDlMeanRate(String ftpDlMeanRate) {
		this.ftpDlMeanRate = ftpDlMeanRate;
	}
	public synchronized String getFtpUlMeanRate() {
		return ftpUlMeanRate;
	}
	public synchronized void setFtpUlMeanRate(String ftpUlMeanRate) {
		this.ftpUlMeanRate = ftpUlMeanRate;
	}
	public synchronized String getFtpDlProgress() {
		return ftpDlProgress;
	}
	public synchronized void setFtpDlProgress(String ftpDlProgress) {
		this.ftpDlProgress = ftpDlProgress;
	}
	public synchronized String getFtpUlProgress() {
		return ftpUlProgress;
	}
	public synchronized void setFtpUlProgress(String ftpUlProgress) {
		this.ftpUlProgress = ftpUlProgress;
	}
	
	public String getFtpActivityThread() {
		return ftpActivityThread;
	}

	public void setFtpActivityThread(String ftpActivityThread) {
		this.ftpActivityThread = ftpActivityThread;
	}

	public synchronized String getPingDelay() {
		return pingDelay;
	}
	public synchronized void setPingDelay(String pingDelay) {
		this.pingDelay = pingDelay;
	}
	public synchronized String getHttpDlThrput() {
		return httpDlThrput;
	}
	public synchronized void setHttpDlThrput(String httpDlThrput) {
		this.httpDlThrput = httpDlThrput;
	}
	public synchronized String getWapDlThrput() {
		return wapDlThrput;
	}
	public synchronized void setWapDlThrput(String wapDlThrput) {
		this.wapDlThrput = wapDlThrput;
	}
	public synchronized String getPop3Thrput() {
		return pop3Thrput;
	}
	public synchronized void setPop3Thrput(String pop3Thrput) {
		this.pop3Thrput = pop3Thrput;
	}
	public synchronized String getSmtpThrput() {
		return smtpThrput;
	}
	public synchronized void setSmtpThrput(String smtpThrput) {
		this.smtpThrput = smtpThrput;
	}
	public synchronized String getPeakValue() {
		return peakValue;
	}
	public synchronized void setPeakValue(String peakValue) {
		this.peakValue = peakValue;
	}
	public synchronized String getUseTimes() {
		return useTimes;
	}
	public synchronized void setUseTimes(String useTimes) {
		this.useTimes = useTimes;
	}
	public String getFtpDlAllSize() {
		return ftpDlAllSize;
	}
	public void setFtpDlAllSize(String ftpDlAllSize) {
		this.ftpDlAllSize = ftpDlAllSize;
	}
	public String getFtpUlAllSize() {
		return ftpUlAllSize;
	}
	public void setFtpUlAllSize(String ftpUlAllSize) {
		this.ftpUlAllSize = ftpUlAllSize;
	}
	public String getFtpDlCurrentSize() {
		return ftpDlCurrentSize;
	}
	public void setFtpDlCurrentSize(String ftpDlCurrentSize) {
		this.ftpDlCurrentSize = ftpDlCurrentSize;
	}
	public String getFtpUlCurrentSize() {
		return ftpUlCurrentSize;
	}
	public void setFtpUlCurrentSize(String ftpUlCurrentSize) {
		this.ftpUlCurrentSize = ftpUlCurrentSize;
	}
	public String getBordLeftTitle() {
		return bordLeftTitle;
	}
	public void setBordLeftTitle(String bordLeftTitle) {
		this.bordLeftTitle = bordLeftTitle;
	}
	public String getBordRightTile() {
		return bordRightTile;
	}
	public void setBordRightTile(String bordRightTile) {
		this.bordRightTile = bordRightTile;
	}
	public float getBordCurrentSpeed() {
		return bordCurrentSpeed;
	}
	public void setBordCurrentSpeed(float bordCurrentSpeed) {
		this.bordCurrentSpeed = bordCurrentSpeed;
	}
	public Float[] getBordPoints(){
	    if(bordPoints != null && tempBordPoints != null){
	        bordPoints.toArray(tempBordPoints);
	        
	        /*for(int i=0;i<tempBordPoints.length;i++){
	        LogUtil.w(tag,"--i:"+i+"--v:"+tempBordPoints[i]);
	        }*/
	    }
	    return tempBordPoints;
	}
	
	public void addBordPoint(float bordPoint){
	    if(bordPoints == null){
	        bordPoints = initQueue();
	    }
	    
	    bordPoints.add(bordPoint);
	    bordPoints.remove();
	}
	
	/**
	 * 回放流程添加仪表盘瞬时速度<BR>
	 * [功能详细描述]
	 */
    public void addPlayBackBordPoint(Context mContext, int handler, int port,
									 int pointIndex, String[] params) {
//        WalkStruct.TaskType taskType = null;
        int index = -1;
        for (int i = 0; i < params.length; i++) {
            if(Double.valueOf(params[i]) != -9999.00){
                index = i;
                break;
            }
        }
        double value = -9999.00;
        switch (index) {
            /*case 0:
                taskType = TaskType.Stream;
                break;*/
            case 1:
//                taskType = TaskType.FTPUpload;
                value = DatasetManager.getInstance(mContext).getDatasetLib().getParamRealValue(handler, port, pointIndex, UnifyParaID.QOS_FTP_UlThr, true);
                break;
            case 2:
//                taskType = TaskType.FTPDownload;
                value = DatasetManager.getInstance(mContext).getDatasetLib().getParamRealValue(handler, port, pointIndex, UnifyParaID.QOS_FTP_DlThr, false);
                break;
            case 3:
//                taskType = TaskType.MultiftpUpload;
                value = DatasetManager.getInstance(mContext).getDatasetLib().getParamRealValue(handler, port, pointIndex, UnifyParaID.QOS_FTP_UlThr, true);
                break;
            case 4:
//                taskType = TaskType.MultiftpDownload;
                value = DatasetManager.getInstance(mContext).getDatasetLib().getParamRealValue(handler, port, pointIndex, UnifyParaID.QOS_FTP_DlThr, true);
                break;
            /*case 5:
                taskType = TaskType.EmailSmtp;
                value = DatasetManager.getInstance(mContext).getDatasetLib().getParamRealValue(handler, port, pointIndex, 0x0A002154, true);
                break;
            case 6:
                taskType = TaskType.EmailPop3;
                value = DatasetManager.getInstance(mContext).getDatasetLib().getParamRealValue(handler, port, pointIndex, 0x0A002154, true);
                break;
            case 7:
                taskType = TaskType.HttpDownload;
                value = DatasetManager.getInstance(mContext).getDatasetLib().getParamRealValue(handler, port, pointIndex, 0x0A002310, true);
                break;
            case 8:
                taskType = TaskType.Http; //HTTPPage
                value = DatasetManager.getInstance(mContext).getDatasetLib().getParamRealValue(handler, port, pointIndex, 0x0A002321, true);
                break;
            case 9:
                taskType = TaskType.WapLogin; //WAPPage
                value = DatasetManager.getInstance(mContext).getDatasetLib().getParamRealValue(handler, port, pointIndex, 0x0A002360, true);
                break;
            case 10:
                taskType = TaskType.WapDownload;
                value = DatasetManager.getInstance(mContext).getDatasetLib().getParamRealValue(handler, port, pointIndex, 0x0A002370, true);
                break;
            case 11:
                taskType = TaskType.HttpUpload;
                value = DatasetManager.getInstance(mContext).getDatasetLib().getParamRealValue(handler, port, pointIndex, 0x0A0024A0, true);
                break;
            case 12:
                taskType = TaskType.SpeedTest;
                value = DatasetManager.getInstance(mContext).getDatasetLib().getParamRealValue(handler, port, pointIndex, 0x0A002601, true);
                break;
            case 13:
                taskType = TaskType.SpeedTest;
                value = DatasetManager.getInstance(mContext).getDatasetLib().getParamRealValue(handler, port, pointIndex, 0x0A002605, true);
                break;
            case 14:
                taskType = TaskType.HTTPVS;
                break;*/
            default:
//                taskType = TaskType.Default;
                break;
        }
        if(palybackTaskType == index && index != -1){
            if(value == -9999.00){
                value = 0;
            }
            if (bordPoints == null) {
                bordPoints = initQueue();
            }
            bordPoints.add((float) value / 1000);
            bordPoints.remove();
            setBordCurrentSpeed(Float.parseFloat(UtilsMethod.decFormat.format(value / 1000)));
        }else if(index != -1){
            if(value == -9999.00){
                value = 0;
            }
            bordPoints = initQueue();
            bordPoints.add((float) value / 1000);
            bordPoints.remove();
            setBordCurrentSpeed(Float.parseFloat(UtilsMethod.decFormat.format( value / 1000)));
        }else if(palybackTaskType != index && index == -1){
            bordPoints = initQueue();
        }
        palybackTaskType = index;
    }
	
	public int getBordProgress() {
		return bordProgress;
	}
	public void setBordProgress(int bordProgress) {
		this.bordProgress = bordProgress;
	}

	public boolean isWlanTest() {
		return isWlanTest;
	}

	public void setWlanTest(boolean isWlanTest) {
		this.isWlanTest = isWlanTest;
	}

	@Override
	public String toString() {
		return "YwDataModel{" +
				"tag='" + tag + '\'' +
				", ftpDlAllSize='" + ftpDlAllSize + '\'' +
				", ftpUlAllSize='" + ftpUlAllSize + '\'' +
				", ftpDlCurrentSize='" + ftpDlCurrentSize + '\'' +
				", ftpUlCurrentSize='" + ftpUlCurrentSize + '\'' +
				", ftpDlThrput='" + ftpDlThrput + '\'' +
				", ftpUlThrput='" + ftpUlThrput + '\'' +
				", ftpDlMeanRate='" + ftpDlMeanRate + '\'' +
				", ftpUlMeanRate='" + ftpUlMeanRate + '\'' +
				", ftpDlProgress='" + ftpDlProgress + '\'' +
				", ftpUlProgress='" + ftpUlProgress + '\'' +
				", ftpActivityThread='" + ftpActivityThread + '\'' +
				", pingDelay='" + pingDelay + '\'' +
				", httpDlThrput='" + httpDlThrput + '\'' +
				", wapDlThrput='" + wapDlThrput + '\'' +
				", pop3Thrput='" + pop3Thrput + '\'' +
				", smtpThrput='" + smtpThrput + '\'' +
				", peakValue='" + peakValue + '\'' +
				", useTimes='" + useTimes + '\'' +
				", bordLeftTitle='" + bordLeftTitle + '\'' +
				", bordRightTile='" + bordRightTile + '\'' +
				", bordCurrentSpeed=" + bordCurrentSpeed +
				", bordPoints=" + bordPoints +
				", tempBordPoints=" + Arrays.toString(tempBordPoints) +
				", isWlanTest=" + isWlanTest +
				", bordProgress=" + bordProgress +
				", pointLength=" + pointLength +
				", palybackTaskType=" + palybackTaskType +
				'}';
	}
}
