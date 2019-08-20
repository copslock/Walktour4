package com.walktour.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.SparseArray;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalDial;
import com.walktour.Utils.TotalStruct.TotalEvent;
import com.walktour.Utils.TotalStruct.TotalFtp;
import com.walktour.Utils.TotalStruct.TotalMeasurePara;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyL3Decode;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.TaskType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ParamInfo;
import com.walktour.control.config.ParamItem;
import com.walktour.control.config.ParamTotalInfo;
import com.walktour.model.TotalFtpModel;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TotalDetailByGSM extends Service {
	private final String tag = "TotalDetailByGSM";
	private ApplicationModel appModel = null;
	private TotalFtpModel tempFtp = null;
	private ParamTotalInfo totalDrawInfo = null;	//统计中图表画图信息
	private SparseArray<ParamInfo> 	paraIDList = null;
	private List<ParamInfo> paraInfoList = null;
	private HashMap<String,Number> paraMap;
	private HashMap<String,Number> measureMap;
	private HashMap<String,Number> eventMap;
	private boolean isDownTBFOpen = false;
	private boolean isUpTBFOpen	  = false;
	private boolean hasHandOverReq= false;
	private boolean hasLAUReuqest = false;
	private boolean hasRAUReuqest = false;
	
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
	 * 发送往UMPC回传的实时参数信息
	 * @param type 参数类型
	 * @param info	参数内容
	 */
	private void sendRealTimeEvent(char type,String info){
		Intent sendRealTimeInfo = new Intent(WalkMessage.UMPC_WriteRealTimeEvent);
		sendRealTimeInfo.putExtra(WalkMessage.UMPC_WriteRealTimeType, type);
		sendRealTimeInfo.putExtra(WalkMessage.UMPC_WriteRealTimeInfo, info);
		sendBroadcast(sendRealTimeInfo);
	}
	
	/**
	 * 接收处理需要统计的相关参数
	 */
	private final BroadcastReceiver decodeParaReceiver  = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(WalkMessage.TotalDetailParaChange)){
				String paraValues = intent.getStringExtra(WalkMessage.TotalParaValue);
				String[] paras = paraValues.split(";");
				measureMap = new HashMap<String, Number>();
				for(int i=0;i<paras.length;i++){
					try{
						String[] values = paras[i].split(",");
						if(values.length != 2)
							continue;
						int id = Integer.valueOf(values[0], 16);
						float valuef = (values[1] == null ||values[1].equals("") ? 0 : Float.parseFloat(values[1]));
						int value = (int) valuef;
						
						switch(id){
						//用于GSM语音统计指标
						case 0x02000109:
						case 0x03000109:
						case 0x7F000109:
							paraMap = new HashMap<String, Number>();
							if(value >= -94){
								if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
										&& appModel.getCurrentTask() == TaskType.PassivityCall){
									paraMap.put(TotalDial._mtRxLev1s.name(), 1);
								}else if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
										&& appModel.getCurrentTask() == TaskType.InitiativeCall){
									paraMap.put(TotalDial._moRxLev1s.name(), 1);
								}
							}
							if(value >= -90){
								if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
										&& appModel.getCurrentTask() == TaskType.PassivityCall){
									paraMap.put(TotalDial._mtRxLev2s.name(), 1);
								}else if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
										&& appModel.getCurrentTask() == TaskType.InitiativeCall){
									paraMap.put(TotalDial._moRxLev2s.name(), 1);
								}
							}
							if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
									&& appModel.getCurrentTask() == TaskType.PassivityCall){
								paraMap.put(TotalDial._mtTotalRxLevs.name(), 1);
							}else if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
									&& appModel.getCurrentTask() == TaskType.InitiativeCall){
								paraMap.put(TotalDial._moTotalRxLevs.name(), 1);
							}
							
							TotalDataByGSM.getInstance().updateTotalPara(paraMap.entrySet().iterator());
							break;
						case 0x02000106:
						case 0x03000106:
						case 0x7F000106:
							paraMap = new HashMap<String, Number>();
							if(value >= 0 && value <=2){
								if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
										&& appModel.getCurrentTask() == TaskType.PassivityCall){
									paraMap.put(TotalDial._mtRxQual1s.name(), 1);
								}else if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
										&& appModel.getCurrentTask() == TaskType.InitiativeCall){
									paraMap.put(TotalDial._moRxQual1s.name(), 1);
								}
							}else if(value >= 3 && value <= 5){
								if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
										&& appModel.getCurrentTask() == TaskType.PassivityCall){
									paraMap.put(TotalDial._mtRxQual2s.name(), 1);
								}else if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
										&& appModel.getCurrentTask() == TaskType.InitiativeCall){
									paraMap.put(TotalDial._moRxQual2s.name(), 1);
								}
							}
							
							if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
									&& appModel.getCurrentTask() == TaskType.PassivityCall){
								paraMap.put(TotalDial._mtTotalRxQuals.name(), 1);
							}else if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
									&& appModel.getCurrentTask() == TaskType.InitiativeCall){
								paraMap.put(TotalDial._moTotalRxQuals.name(), 1);
							}
							TotalDataByGSM.getInstance().updateTotalPara(paraMap.entrySet().iterator());
							
							//用于GSM测量参数,语音的测量参数都需要做业务过虑，只有当前在通话过程中才记录测量值
							if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE){
								measureMap.put(TotalMeasurePara._rxQualSub.name(), value);
							}
							break;
						case 0x02000105:
						case 0x03000105:
						case 0x7F000105:
							if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE)
								measureMap.put(TotalMeasurePara._rxQualFull.name(), value);
							break;
						case 0x02000103:
						case 0x03000103:
						case 0x7F000103:
							if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE)
								measureMap.put(TotalMeasurePara._rxLevFull.name(), value);
							break;
						case 0x02000104:
						case 0x03000104:
						case 0x7F000104:
							if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE)
								measureMap.put(TotalMeasurePara._rxLevSub.name(), value);
							break;
						case 0x02000113:
						case 0x03000113:
						case 0x7F000113:
							if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE)
								measureMap.put(TotalMeasurePara._ta.name(), value);
							break;
						case 0x02000114:
						case 0x03000114:
						case 0x7F000114:
							if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE){
								measureMap.put(TotalMeasurePara._txPower.name(), value);
							}
							break;
							
						case 0x02000609:
						case 0x03000609:
						case 0x7F000609:
							//此处需要起线程计算当前状态，如果状态改则清除并记录
							if(appModel.getCurrentTask() == TaskType.FTPDownload){
								totalDownTBFTime(value);
							}
							break;
						//case 0x02000601:	//DLCS
						case 0x02000617:	//DLMCS
						case 0x03000617:	//DLMCS
						case 0x7F000617:    //DLMCS
							tempFtp.setDownMCCounts(tempFtp.getDownMCCounts() + (value+1));
							tempFtp.setDownMCAllCount(tempFtp.getDownMCAllCount() + 1);
							
							break;
						case 0x02000603:	//eGSM_DL_TS_Num 数据业务下行时隙个数
						case 0x03000603:	//eGSM_DL_TS_Num 数据业务下行时隙个数
						case 0x7F000603:    //eGSM_DL_TS_Num 数据业务下行时隙个数
							/*tempFtp.setDownTSAllCounts(tempFtp.getDownTSAllCounts() + 1);
							if(value <=4 )
								tempFtp.setDownTSCounts(tempFtp.getDownTSCounts() + value);*/
							totalDownTSTime(value<=4 ? value : 0);
							break;
						case 0x02000604:	//eGSM_UL_TS_Num 数据业务上行时隙个数
						case 0x03000604:	//eGSM_UL_TS_Num 数据业务上行时隙个数
						case 0x7F000604:    //eGSM_UL_TS_Num 数据业务上行时隙个数
							/*tempFtp.setUpTSAllCounts(tempFtp.getUpTSAllCounts() + 1);
							if(value <=4 )
								tempFtp.setUpTSCounts(tempFtp.getUpTSCounts() + value);*/
							break;
						case 0x0200060a:
						case 0x0300060a:
						case 0x7F00060a:
							//此处需要起线程计算当前状态，如果状态改则清除并记录
							if(appModel.getCurrentTask() == TaskType.FTPUpload){
								totalUlTBFTime(value);
							}
							break;
						//case 0x02000602:	//ULCS
						case 0x02000616:	//UL MCS
						case 0x03000616:	//UL MCS
						case 0x7F000616:    //UL MCS
							tempFtp.setUpMCCounts(tempFtp.getUpMCCounts() + (value+1));
							tempFtp.setUpMCAllCount(tempFtp.getUpMCAllCount() + 1);
							
							break;
                            
                            //WCDMA Cell Info
                        case 0x7F02000D:
                            measureMap.put(TotalMeasurePara._wTotalRSCP.name(), value);
                            break;
                        case 0x7F02000A:
                            measureMap.put(TotalMeasurePara._wTotalEclo.name(), value);
                            break;
                        case 0x7F020001:
                            measureMap.put(TotalMeasurePara._wRxPower.name(), value);
                            break;
                        case 0x7F020002:
                            measureMap.put(TotalMeasurePara._wTxPower.name(), value);
                            break;
                        case 0x7F020005:
                            measureMap.put(TotalMeasurePara._wBLER.name(), value);
                            break;
                        case 0x7F020010:
                            measureMap.put(TotalMeasurePara._wSIR.name(), value);
                            break;
                            
                        //TD SCDMA
                        case 0x7F03011B:
                            measureMap.put(TotalMeasurePara._tPCCPCH_RSCP.name(), value);
                            break;
                        case 0x7F030120:
                            measureMap.put(TotalMeasurePara._tPCCPCH_C_I.name(), value);
                            break;
                        case 0x7F030102:
                            measureMap.put(TotalMeasurePara._tUE_TxPower.name(), value);
                            break;
                        case 0x7F030119:
                            measureMap.put(TotalMeasurePara._tBLER.name(), value);
                            break;
                            
                          //LTE
                        case 0x7F06000E:
                            measureMap.put(TotalMeasurePara._lRSRP.name(), value);
                            break;
                        case 0x7F06000F:
                            measureMap.put(TotalMeasurePara._lRSRQ.name(), value);
                            break;
                        case 0x7F060001:
                            measureMap.put(TotalMeasurePara._lSINR.name(), value);
                            break;
                        case 0x7F060011:
                            measureMap.put(TotalMeasurePara._lSRS_Power.name(), value);
                            break;
                            
							//测试过程中事件统计
						case 0x02000101:	//BCCH
						case 0x03000101:	//BCCH
						case 0x7F000101:    //BCCH
						case 0x02000102:	//BSIC
						case 0x03000102:	//BSIC
						case 0x7F000102:    //BSIC
							//idle状态下，如果当前值发生改变，则计为一次小区重选
							if(MyPhoneState.getCallState() == TelephonyManager.CALL_STATE_IDLE){
								LogUtil.w(tag,"---"+UtilsMethod.sdFormat.format(System.currentTimeMillis())+"--"+Integer.toHexString(id));
								eventMap = new HashMap<String, Number>();
								eventMap.put(TotalEvent._sectionReChooses.name(), 1);
								
								TotalDataByGSM.getInstance().updateTotalEvent(eventMap.entrySet().iterator());
							}
							break;
						case 0x512F062b:	//切换请求
							if(!hasHandOverReq){
								hasHandOverReq = true;
								eventMap = new HashMap<String, Number>();
								eventMap.put(TotalEvent._switchTimes.name(), 1);
								eventMap.put(TotalEvent._switchSuccs.name(), 0);	//用于显示未收到成功次数时显示为0
								
								TotalDataByGSM.getInstance().updateTotalEvent(eventMap.entrySet().iterator());
								sendRealTimeEvent(WalkStruct.UMPCEventType.Event.getUMPCEvnetType(),
										UnifyL3Decode.disposeL3Info(0x512F062b).getL3Msg());
							}
							break;
						case 0x512F062c:	//切换成功
							if(hasHandOverReq){
								eventMap = new HashMap<String, Number>();
								eventMap.put(TotalEvent._switchSuccs.name(), 1);
								
								TotalDataByGSM.getInstance().updateTotalEvent(eventMap.entrySet().iterator());
							}
							sendRealTimeEvent(WalkStruct.UMPCEventType.Event.getUMPCEvnetType(),
									UnifyL3Decode.disposeL3Info(0x512F062c).getL3Msg());
							hasHandOverReq = false;
							break;
						case 0x512F0628:	//切换失败
							if(hasHandOverReq){
								eventMap = new HashMap<String, Number>();
								eventMap.put(TotalEvent._switchFaild.name(), 1);
								
								TotalDataByGSM.getInstance().updateTotalEvent(eventMap.entrySet().iterator());
							}
							sendRealTimeEvent(WalkStruct.UMPCEventType.Event.getUMPCEvnetType(),
									UnifyL3Decode.disposeL3Info(0x512F0628).getL3Msg());
							hasHandOverReq = false;
							break;
						case 0x713A0508:	//Location Updating Request LAU位置更新请求
							if(!hasLAUReuqest){
								hasLAUReuqest = true;
								eventMap = new HashMap<String, Number>();
								eventMap.put(TotalEvent._lauTrys.name(), 1);
								eventMap.put(TotalEvent._lauSuccs.name(),0);	//用于显示未收到成功次数时显示为0
								
								TotalDataByGSM.getInstance().updateTotalEvent(eventMap.entrySet().iterator());
							}
							break;
						case 0x713A0502:	//Location Updating Accept LAU位置更新成功
							if(hasLAUReuqest){
								eventMap = new HashMap<String, Number>();
								eventMap.put(TotalEvent._lauSuccs.name(), 1);
								
								TotalDataByGSM.getInstance().updateTotalEvent(eventMap.entrySet().iterator());
							}
							hasLAUReuqest = false;
							break;
						case 0x713A0504:	//Location Updating Reject LAU位置更新失败
							if(hasLAUReuqest){
								eventMap = new HashMap<String, Number>();
								eventMap.put(TotalEvent._lauFaild.name(), 1);
								
								TotalDataByGSM.getInstance().updateTotalEvent(eventMap.entrySet().iterator());
							}
							hasLAUReuqest = false;
							break;
						case 0x713A0808:	//Routing Area Update Request RAU路由更新请求
							if(!hasRAUReuqest){
								hasRAUReuqest = true;
								eventMap = new HashMap<String, Number>();
								eventMap.put(TotalEvent._rauTrys.name(), 1);
								eventMap.put(TotalEvent._rauSuccs.name(), 0);	//用于显示未收到成功次数时显示为0
								
								TotalDataByGSM.getInstance().updateTotalEvent(eventMap.entrySet().iterator());
							}
							break;
						case 0x713A0809:	//Routing Area Update Accept RAU路由更新成功
							if(hasRAUReuqest){
								eventMap = new HashMap<String, Number>();
								eventMap.put(TotalEvent._rauSuccs.name(), 1);
								
								TotalDataByGSM.getInstance().updateTotalEvent(eventMap.entrySet().iterator());
							}
							hasRAUReuqest = false;
							break;
						case 0x713A080A:	//Routing Area Update Complete
							if(hasRAUReuqest){
								eventMap = new HashMap<String, Number>();
								eventMap.put(TotalEvent._rauSuccs.name(), 1);
								
								TotalDataByGSM.getInstance().updateTotalEvent(eventMap.entrySet().iterator());
							}
							hasRAUReuqest = false;
							break;
						case 0x713A080B:	//Routing Area Update Reject RAU路由更新失败
							if(hasRAUReuqest){
								eventMap = new HashMap<String, Number>();
								eventMap.put(TotalEvent._rauFaild.name(), 1);
								
								TotalDataByGSM.getInstance().updateTotalEvent(eventMap.entrySet().iterator());
							}
							hasRAUReuqest = false;
							break;
						//20130401沈清建议其它的去掉，剩硬切换的两事件
						//case 0x412F0301:	//activeSetUpdate(DL_DCCH)
						//case 0x412F0101:	//activeSetUpdateComplete(UL_DCCH)
						//case 0x412F0102:	//activeSetUpdateFailure(UL_DCCH)
						case 0x412F0307:	//handoverFromUTRANCommand-GSM(DL_DCCH)
						case 0x412F0107:	//handoverFromUTRANFailure(UL_DCCH)
						//case 0x512F0663:	//RR INTER SYSTEM TO UTRAN HANDOVER COMMAND
						//case 0x412F0105:	//handoverToUTRANComplete(UL_DCCH)
							sendRealTimeEvent(WalkStruct.UMPCEventType.Event.getUMPCEvnetType(),
									UnifyL3Decode.disposeL3Info(id).getL3Msg());
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
