package com.walktour.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.SparseArray;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalFtp;
import com.walktour.Utils.TotalStruct.TotalMeasurePara;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct.TaskType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ParamInfo;
import com.walktour.control.config.ParamItem;
import com.walktour.control.config.ParamTotalInfo;
import com.walktour.model.TotalFtpModel;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TotalDetailByCDMA extends Service {
	private final String tag = "TotalDetailByCDMA";
	private ApplicationModel appModel = null;
	private TotalFtpModel tempFtp = null;
	private ParamTotalInfo totalDrawInfo = null;	//统计中图表画图信息
	private SparseArray<ParamInfo> 	paraIDList = null;
	private List<ParamInfo> paraInfoList = null;
	private HashMap<String,Number> paraMap;
	private HashMap<String,Number> measureMap;
	private boolean isDownTBFOpen = false;
	private boolean isUpTBFOpen	  = false;
	
	private long tbfFirstTime	= 0;	//TBF OPEN计算开始时间
	private long tbfLastTime	= 0;	//TBF OPEN计算结束时间
	private long tsFirstTime 	= 0;	//TS记时开始时间
	private long tsLastTime		= 0;	//TS结束时间
	private int  tsCurrentNum	= 0;	//当前TS值
	private long subTime	= 0;	//开始结束时间相差	毫秒
	
	Timer 			timer 			= new Timer();
	TimerTask 		timerTask 		= null;
	private int 	timerDelay		= 1000 * 1;	//延时1秒开始计时
	private int 	timerIntervalue	= 1000 * 5;	//间隔5秒钟刷新页面一次
	private Intent 	eventIntent 	= new Intent(TotalDataByGSM.TotalParaDataChanged);
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	class ParaChangeTimerTask extends TimerTask{
		public void run(){
			sendBroadcast(eventIntent);
		}
	}
	/**
     * 清楚工作时间计时器
     */
    private void cleanTimerTask(){
    	if(timerTask!=null){
			timerTask.cancel();
			timerTask=null;
		}
		if(timer !=null) timer=null;
    }
    
	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.w(tag,"----onCreate----");
		appModel = ApplicationModel.getInstance();
		tempFtp = new TotalFtpModel();
		totalDrawInfo = ParamTotalInfo.getInstance();
		paraIDList = totalDrawInfo.getParaIdList();
		paraInfoList = totalDrawInfo.getParamList();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(WalkMessage.TotalDetailParaChange);
		filter.addAction(WalkMessage.TotalByFtpIsFull);
		//filter.addAction(WalkMessage.FtpTest_Upload_Start_Logmask);
		//filter.addAction(WalkMessage.FtpTest_Download_Start_Logmask);
		filter.addAction(WalkMessage.FtpTest_RETR_OR_STOR);
		this.registerReceiver(decodeParaReceiver, filter);
		
		timerTask = new ParaChangeTimerTask();
		timer.schedule(timerTask,timerDelay,timerIntervalue);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.w(tag,"----onDestroy----");
		cleanTimerTask();
		this.unregisterReceiver(decodeParaReceiver);
	}
	
	/**
	 * 计算FTP下载时TBF Open时长
	 * 如果传进来的状态为开，之前为关时将状态置为开，开始时间为当前时间；如果当前是开的状态不作处理
	 * 如果传进的状态为关，如果之前开的状态则将状态置关，并将当前时间减开始时间作为OPEN时长记录OPEN时间临时对象
	 * @param state
	 */
	private void totalUlTBFTime(int state){
		if(state == 1){
			if(!isUpTBFOpen){
				tbfFirstTime = System.currentTimeMillis();
				isUpTBFOpen = true;
			}
		}else{
			if(isUpTBFOpen){
				isUpTBFOpen = false;
				tbfLastTime = System.currentTimeMillis();
				subTime = tbfLastTime - tbfFirstTime;
				tempFtp.setUpTBFOpenTimes(tempFtp.getUpTBFOpenTimes() + subTime );
				tbfFirstTime = tbfLastTime;
			}
		}
	}
	
	/**
	 * 计算FTP下载时TBF Open时长
	 * 如果传进来的状态为开，之前为关时将状态置为开，开始时间为当前时间；如果当前是开的状态不作处理
	 * 如果传进的状态为关，如果之前开的状态则将状态置关，并将当前时间减开始时间作为OPEN时长记录OPEN时间临时对象
	 * @param state
	 */
	private void totalDownTBFTime(int state){
		if(state == 1){
			if(!isDownTBFOpen){
				tbfFirstTime = System.currentTimeMillis();
				isDownTBFOpen = true;
			}
		}else{
			if(isDownTBFOpen){
				isDownTBFOpen = false;
				tbfLastTime = System.currentTimeMillis();
				subTime = tbfLastTime - tbfFirstTime;
				tempFtp.setDownTBFOpenTimes(tempFtp.getDownTBFOpenTimes() + subTime );
				tbfFirstTime = tbfLastTime;
			}
		}
	}
	
	/**
	 * 计算当前TS时长
	 * @param tsNum
	 */
	private void totalDownTSTime(int tsNum){
		//LogUtil.w(tag,"--tsCurrentNum:"+tsCurrentNum+"---tsNum:"+tsNum+"--"+UtilsMethod.sdFormat.format(System.currentTimeMillis()));
		if(tsCurrentNum != tsNum){
			if(tsCurrentNum == 0 || tsFirstTime == 0){
				tsCurrentNum = tsNum;
				tsFirstTime = System.currentTimeMillis();
				//LogUtil.w(tag,"--tsCurrentNum == "+tsCurrentNum+":nextNum:"+tsNum+"--tsFirstTime:"+UtilsMethod.sdFormat.format(tsFirstTime));
			}else{
				tsLastTime = System.currentTimeMillis();
				subTime = tsLastTime - tsFirstTime;
				tempFtp.setDownTSCounts(tempFtp.getDownTSCounts() + (subTime * tsCurrentNum));
				/*LogUtil.w(tag,"--tsNum Change-subTime:"+((subTime*tsCurrentNum)/1000)+"--" +tempFtp.getDownTSCounts()+"--first"
						+UtilsMethod.sdFormat.format(tsFirstTime)+"--end:"+UtilsMethod.sdFormat.format(tsLastTime));*/
				tsCurrentNum = tsNum;
				tsFirstTime = tsLastTime;
			}
		}
	}
	/**
	 * 将FTP上传过程中的参数更新到统计明细表中
	 */
	private void buildUpPara(int jobTimes){
		totalUlTBFTime(0);
		paraMap = new HashMap<String, Number>();
		paraMap.put(TotalFtp._upRLCThrs.name(), tempFtp.getUpRLCThrs());
		paraMap.put(TotalFtp._upRLCCount.name(), tempFtp.getUpRLCCount());
		
		paraMap.put(TotalFtp._upTBFOpenTimes.name(), (int)(tempFtp.getUpTBFOpenTimes()/1000));
		paraMap.put(TotalFtp._upAllTimes.name(), jobTimes);
		paraMap.put(TotalFtp._upTSCounts.name(),tempFtp.getUpTSCounts());
		paraMap.put(TotalFtp._upTSAllcounts.name(), tempFtp.getUpTSAllCounts());
		
		paraMap.put(TotalFtp._upMCCounts.name(), tempFtp.getUpMCCounts());
		paraMap.put(TotalFtp._upMCAllCount.name(), tempFtp.getUpMCAllCount());
		TotalDataByGSM.getInstance().updateTotalPara(paraMap.entrySet().iterator());
		
		LogUtil.w(tag,"---buildUpPara---");
		tempFtp = new TotalFtpModel();
	}
	
	/**
	 * 将FTP下载过程中的参数更新到统计明细表中
	 */
	private void buildDownPara(int jobTimes){
		totalDownTBFTime(0);	//
		totalDownTSTime(0);		//将当前TS时间置0，如果前一值不为0则累加其计时值
		paraMap = new HashMap<String, Number>();
		paraMap.put(TotalFtp._downRLCThrs.name(), tempFtp.getDownRLCThrs());
		paraMap.put(TotalFtp._downRLCCount.name(), tempFtp.getDownRLCCount());
		
		paraMap.put(TotalFtp._downRLCBlers.name(), (int)tempFtp.getDownRLCBlers());
		paraMap.put(TotalFtp._downRLCBlerClunt.name(), tempFtp.getDownRLCBlerClunt());
		
		paraMap.put(TotalFtp._downTBFOpenTimes.name(), (int)(tempFtp.getDownTBFOpenTimes()/1000));
		paraMap.put(TotalFtp._downAllTimes.name(), jobTimes);
		paraMap.put(TotalFtp._downTSCounts.name(),(int)(tempFtp.getDownTSCounts()/1000));
		paraMap.put(TotalFtp._downTSAllCount.name(), tempFtp.getDownTSAllCounts());
		
		paraMap.put(TotalFtp._downMCCounts.name(), tempFtp.getDownMCCounts());
		paraMap.put(TotalFtp._downMCAllCount.name(), tempFtp.getDownMCAllCount());
		TotalDataByGSM.getInstance().updateTotalPara(paraMap.entrySet().iterator());
		
		LogUtil.w(tag,"---buildDownPara---");
		tempFtp = new TotalFtpModel();
	}
	
	/**
	 * 如果当前ID在参数画图队列中
	 * 取该队列的阀值设定信息
	 * @param id
	 * @param value
	 */
	private void setDrawPara(int id,int value){
		for(int i = 0;i<paraInfoList.size();i++){
			if(paraInfoList.get(i).id == id){
				paraMap = new HashMap<String, Number>();
				List<ParamItem> itemList = paraInfoList.get(i).paramItemList;
				
				for(int j=0;j<itemList.size();j++){
					if(j==0){
						if(value <= itemList.get(j).value){
							paraMap.put(itemList.get(j).itemname, 1);
							//LogUtil.w(tag,"----"+value+"<="+itemList.get(j).value);
							break;
						}
					}else if(j == itemList.size()-1){
						if(value >itemList.get(j-1).value){
							paraMap.put(itemList.get(j).itemname, 1);
							//LogUtil.w(tag,"----"+value+">"+itemList.get(j).value);
							break;
						}
					}else{
						if(value>itemList.get(j-1).value && value <=itemList.get(j).value){
							paraMap.put(itemList.get(j).itemname, 1);
							//LogUtil.w(tag,"----"+value+">"+itemList.get(j).value+" && "+value+"<="+itemList.get(j+1).value);
							break;
						}
					}
				}
				//LogUtil.w(tag,"----value:"+value);
				paraMap.put(paraInfoList.get(i).paramName, 1);
				TotalDataByGSM.getInstance().updateTotalPara(paraMap.entrySet().iterator());
				break;
			}
		}
	}
	
	/**
	 * 接收处理需要统计的相关参数
	 */
	private final BroadcastReceiver decodeParaReceiver  = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(WalkMessage.TotalDetailParaChange)){
				String paraValues = intent.getStringExtra(WalkMessage.TotalParaValue);
				//LogUtil.w(tag,"--TotalCDMA:"+paraValues);
				String[] paras = paraValues.split(";");
				measureMap = new HashMap<String, Number>();
				for(int i=0;i<paras.length;i++){
					try{
						String[] values = paras[i].split(",");
						if(values.length != 2)
							continue;
						int id = Integer.valueOf(values[0], 16);
						float valuef = Float.parseFloat(values[1]);
						int value = (int) valuef;
						
						switch(id){
						    //CDMA
					    case 0x7F01000C:
					        measureMap.put(TotalMeasurePara._cTotalECIO.name(),value);
					        break;
					    case 0x7F01000F:
					        measureMap.put(TotalMeasurePara._cTotalEC.name(),value);
					        break;
						case 0x7F010006:
							measureMap.put(TotalMeasurePara._cFFER.name(), value);
							break;
						case 0x7F010001:
							measureMap.put(TotalMeasurePara._cRxAGC.name(), value);
							break;
						case 0x7F010002:
							measureMap.put(TotalMeasurePara._cTxAGC.name(), value);
							break;
						case 0x7F010003:
						    measureMap.put(TotalMeasurePara._cTxPower.name(), value);
						    break;
						    
						    //CDMA 1x
						case 0x7F010205:
						    measureMap.put(TotalMeasurePara._cRXPhysThr.name(), value);
						    break;
						case 0x7F010206:
                            measureMap.put(TotalMeasurePara._cTPhysThr.name(), value);
                            break;
						case 0x7F010201:
                            measureMap.put(TotalMeasurePara._cRXRLPThr.name(), value);
                            break;
						case 0x7F010202:
                            measureMap.put(TotalMeasurePara._cTXRLPThr.name(), value);
                            break;
						case 0x7F010203:
                            measureMap.put(TotalMeasurePara._cRLPErrRate.name(), value);
                            break;
						case 0x7F010204:
                            measureMap.put(TotalMeasurePara._cRLPRTXRate.name(), value);
                            break;
                            
                            //EVDO
						case 0x7F018001:
						    measureMap.put(TotalMeasurePara._eRXAGC0.name(), value);
						    break;
						case 0x7F018002:
                            measureMap.put(TotalMeasurePara._eRXAGC1.name(), value);
                            break;
						case 0x7F018003:
                            measureMap.put(TotalMeasurePara._eTxAGC.name(), value);
                            break;
						case 0x7F018008:
                            measureMap.put(TotalMeasurePara._eTotalSINR.name(), value);
                            break;
						}
						
						//当当前参数存在于画图参数队列中时，需根据队列的设置信息进行参数统计
						if(paraIDList.get(id) != null){
							setDrawPara(id,value);
						}
					}catch(Exception e){
						e.printStackTrace();
						return;
					}
				}
				
				//将统GSM测量参数统计写入测量统计对象中
				TotalDataByGSM.getInstance().updateTotalMeasurePara(measureMap.entrySet().iterator());
			}else if(intent.getAction().equals(WalkMessage.TotalByFtpIsFull)){
				boolean isDown = intent.getBooleanExtra("IsFtpDown", false);
				int ftpJobTimes = intent.getIntExtra("FtpJobTimes",0);
				if(isDown)
					buildDownPara(ftpJobTimes);
				else
					buildUpPara(ftpJobTimes);
			}/*else if(intent.getAction().equals(WalkMessage.FtpTest_Upload_Start_Logmask)
					|| intent.getAction().equals(WalkMessage.FtpTest_Download_Start_Logmask)){
			}*/else if(intent.getAction().equals(WalkMessage.FtpTest_RETR_OR_STOR)){
				/**
				 * 按照B算法，当FTP测试收到文件大小时，开始记录统计参数的相关信息到统计参数临时对象中
				 * 当FTP测试满足完整测试后，将临时参数对象的数据记录测试结果对象中。
				 * 收到文件大小时，初始化相关的临时参数记录对象
				 * 因为收到数据大小时，会将临时对象等都初始，所以测试过程中不需要根据当前是否在做FTP测试再将相关参数值存入相关对象
				 * 因为测试结果时，只将上传或者下载相对应的临时对象值写入统计参数对象，故测试过程中也不需要判断当前类型再记入临时对象
				 */
				tempFtp = new TotalFtpModel();	//清空FTP未满完整性时的临时参数记录对象
				isDownTBFOpen = false;			//TBFDOWN 为CLOSE
				isUpTBFOpen = false;			//TBFUP 为CLOSE
				tsCurrentNum= 0;				//当前TS值为0
				tbfFirstTime = System.currentTimeMillis();
				tbfLastTime = tbfFirstTime;
				tsFirstTime = System.currentTimeMillis();
				try{
					if(appModel.getCurrentTask() == TaskType.FTPUpload){
						totalDownTBFTime(Integer.parseInt(TraceInfoInterface.getParaValue(UnifyParaID.G_GPRS_UL_TBF_State) != null
							? TraceInfoInterface.getParaValue(UnifyParaID.G_GPRS_UL_TBF_State) : "0"));
					}else if(appModel.getCurrentTask() == TaskType.FTPDownload){
						totalDownTSTime(Integer.parseInt(TraceInfoInterface.getParaValue(UnifyParaID.G_GPRS_DL_TS_Num) != null
							? TraceInfoInterface.getParaValue(UnifyParaID.G_GPRS_DL_TS_Num) : "0"));	//TS统计时长从0开始
						totalUlTBFTime(Integer.parseInt(TraceInfoInterface.getParaValue(UnifyParaID.G_GPRS_DL_TBF_State) != null
							? TraceInfoInterface.getParaValue(UnifyParaID.G_GPRS_DL_TBF_State) : "0"));
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	};
}
