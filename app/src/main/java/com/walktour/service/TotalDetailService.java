package com.walktour.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalDial;
import com.walktour.Utils.TotalStruct.TotalFtp;
import com.walktour.Utils.TotalStruct.TotaletailPara;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.TypeConver;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct.TaskType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.model.TotalFtpModel;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class TotalDetailService extends Service {
	private final String tag = "TotalDetailService";
	private ApplicationModel appModel = null;
	private TotalFtpModel tempFtp = null;
	private HashMap<String,Number> map;
	private HashMap<String,Number> paraMap;
	private boolean isDownTBFOpen = false;
	private boolean isUpTBFOpen	  = false;
	private boolean getFirstData  = false;
	private long firstTime	= 0;	//计算开始时间
	private long lastTime	= 0;	//计算结束时间
	private long subTime	= 0;	//开始结束时间相差	毫秒
	
	Timer 			timer 			= new Timer();
	TimerTask 		timerTask 		= null;
	private int 	timerDelay		= 1000 * 1;	//延时1秒开始计时
	private int 	timerIntervalue	= 1000 * 5;	//间隔5秒钟刷新页面一次
	private Intent 	eventIntent 	= new Intent(TotalDataByGSM.TotalParaDataChanged);
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		super.onCreate();
		LogUtil.w(tag,"----onCreate----");
		appModel = ApplicationModel.getInstance();
		tempFtp = new TotalFtpModel();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(WalkMessage.TotalDetailParaChange);
		filter.addAction(WalkMessage.TotalByFtpIsFull);
		filter.addAction(WalkMessage.FtpTest_Upload_Start_Logmask);
		filter.addAction(WalkMessage.FtpTest_Download_Start_Logmask);
		filter.addAction(WalkMessage.FtpTest_Get_FirstData);
		this.registerReceiver(decodeParaReceiver, filter);
		
		timerTask = new ParaChangeTimerTask();
		timer.schedule(timerTask,timerDelay,timerIntervalue);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LogUtil.w(tag,"----onDestroy----");
		cleanTimerTask();
		this.unregisterReceiver(decodeParaReceiver);
	}
	
	/**
	 * 计算FTP上行时TBF Open时长
	 * @param state
	 */
	private void totalUlTBFTime(int state){
		if(state == 1){
			if(!isUpTBFOpen){
				firstTime = System.currentTimeMillis();
				isUpTBFOpen = true;
			}
		}else{
			if(isUpTBFOpen){
				isUpTBFOpen = false;
				lastTime = System.currentTimeMillis();
				subTime = lastTime - firstTime;
				tempFtp.setUpTBFOpenTimes(tempFtp.getUpTBFOpenTimes() + (int)(subTime / 1000));
				firstTime = lastTime;
			}
		}
	}
	
	/**
	 * 计算FTP下载时TBF Open时长
	 * @param state
	 */
	private void totalDownTBFTime(int state){
		if(state == 1){
			if(!isDownTBFOpen){
				firstTime = System.currentTimeMillis();
				isDownTBFOpen = true;
			}
		}else{
			if(isDownTBFOpen){
				isDownTBFOpen = false;
				lastTime = System.currentTimeMillis();
				subTime = lastTime - firstTime;
				tempFtp.setDownTBFOpenTimes(tempFtp.getDownTBFOpenTimes() + (int)(subTime / 1000));
				firstTime = lastTime;
			}
		}
	}
	
	/**
	 * 将FTP上传过程中的参数更新到统计明细表中
	 */
	private void buildUpPara(){
		totalUlTBFTime(0);
		map = new HashMap<String, Number>();
		map.put(TotalFtp._upRLCThrs.name(), tempFtp.getUpRLCThrs());
		map.put(TotalFtp._upRLCCount.name(), tempFtp.getUpRLCCount());
		
		map.put(TotalFtp._upTBFOpenTimes.name(), tempFtp.getUpTBFOpenTimes());
		map.put(TotalFtp._upTSCounts.name(),tempFtp.getUpTSCounts());
		
		map.put(TotalFtp._upMCCounts.name(), tempFtp.getUpMCCounts());
		map.put(TotalFtp._upMCAllCount.name(), tempFtp.getUpMCAllCount());
		//TotalDataByGSM.getInstance().updateTotalUnifyTimes(map.entrySet().iterator());
		TotalDataByGSM.getInstance().updateTotalUnifyTimes(TotalDetailService.this,map);
        
		LogUtil.w(tag,"---buildUpPara---");
		tempFtp = new TotalFtpModel();
	}
	
	/**
	 * 将FTP下载过程中的参数更新到统计明细表中
	 */
	private void buildDownPara(){
		totalDownTBFTime(0);
		map = new HashMap<String, Number>();
		map.put(TotalFtp._downRLCThrs.name(), tempFtp.getDownRLCThrs());
		map.put(TotalFtp._downRLCCount.name(), tempFtp.getDownRLCCount());
		
		map.put(TotalFtp._downRLCBlers.name(), tempFtp.getDownRLCBlers());
		map.put(TotalFtp._downRLCBlerClunt.name(), tempFtp.getDownRLCBlerClunt());
		
		map.put(TotalFtp._downTBFOpenTimes.name(), tempFtp.getDownTBFOpenTimes());
		map.put(TotalFtp._downTSCounts.name(),tempFtp.getDownTSCounts());
		
		map.put(TotalFtp._downMCCounts.name(), tempFtp.getDownMCCounts());
		map.put(TotalFtp._downMCAllCount.name(), tempFtp.getDownMCAllCount());
		TotalDataByGSM.getInstance().updateTotalPara(map.entrySet().iterator());
		
		LogUtil.w(tag,"---buildDownPara---");
		tempFtp = new TotalFtpModel();
	}
	
	private final BroadcastReceiver decodeParaReceiver  = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(WalkMessage.TotalDetailParaChange)){
				String paraValues = intent.getStringExtra(WalkMessage.TotalParaValue);
				//LogUtil.w(tag,"---paraValues:"+paraValues);
				String[] paras = paraValues.split(";");
				paraMap = new HashMap<String, Number>();
				for(int i=0;i<paras.length;i++){
					try{
						TotaletailPara eventMsg;
						String para = paras[i].substring(0,paras[i].indexOf("="));
						String value = paras[i].substring(paras[i].indexOf("=")+1);
						if(value.equals(""))
							continue;
						try{
							eventMsg = TotaletailPara.valueOf(para);
						}catch(Exception e){
							eventMsg = TotaletailPara.Default;
						}
						switch(eventMsg){
						case RxLevSub:
							map = new HashMap<String, Number>();
							int rxLevSub = TypeConver.StringToInt(value);
							if(rxLevSub >= -94){
								if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
										&& appModel.getCurrentTask() == TaskType.PassivityCall){
									map.put(TotalDial._mtRxLev1s.name(), 1);
								}else if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
										&& appModel.getCurrentTask() == TaskType.InitiativeCall){
									map.put(TotalDial._moRxLev1s.name(), 1);
								}
							}
							if(rxLevSub >= -90){
								if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
										&& appModel.getCurrentTask() == TaskType.PassivityCall){
									map.put(TotalDial._mtRxLev2s.name(), 1);
								}else if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
										&& appModel.getCurrentTask() == TaskType.InitiativeCall){
									map.put(TotalDial._moRxLev2s.name(), 1);
								}
							}
							if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
									&& appModel.getCurrentTask() == TaskType.PassivityCall){
								map.put(TotalDial._mtTotalRxLevs.name(), 1);
							}else if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
									&& appModel.getCurrentTask() == TaskType.InitiativeCall){
								map.put(TotalDial._moTotalRxLevs.name(), 1);
							}
							
							//用于GSM参数统计
							paraMap.put(TotaletailPara.RxLevSub.name(), rxLevSub);
							TotalDataByGSM.getInstance().updateTotalPara(map.entrySet().iterator());
							break;
						case RxQualSub:
							map = new HashMap<String, Number>();
							int rxQualSub = Integer.parseInt(value);
							if(rxQualSub >= 0 && rxQualSub <=2){
								if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
										&& appModel.getCurrentTask() == TaskType.PassivityCall){
									map.put(TotalDial._mtRxQual1s.name(), 1);
								}else if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
										&& appModel.getCurrentTask() == TaskType.InitiativeCall){
									map.put(TotalDial._moRxQual1s.name(), 1);
								}
							}else if(rxQualSub >= 3 && rxQualSub <= 5){
								if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
										&& appModel.getCurrentTask() == TaskType.PassivityCall){
									map.put(TotalDial._mtRxQual2s.name(), 1);
								}else if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
										&& appModel.getCurrentTask() == TaskType.InitiativeCall){
									map.put(TotalDial._moRxQual2s.name(), 1);
								}
							}
							
							if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
									&& appModel.getCurrentTask() == TaskType.PassivityCall){
								map.put(TotalDial._mtTotalRxQuals.name(), 1);
							}else if(MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE 
									&& appModel.getCurrentTask() == TaskType.InitiativeCall){
								map.put(TotalDial._moTotalRxQuals.name(), 1);
							}
							
							//用于GSM参数统计
							paraMap.put(TotaletailPara.RxQualSub.name(), rxQualSub);
							TotalDataByGSM.getInstance().updateTotalPara(map.entrySet().iterator());
							break;
						case RxLevFull:
							int rxLevFull = Integer.parseInt(value);
							paraMap.put(TotaletailPara.RxLevFull.name(), rxLevFull);
							break;
						case RxQualFull:
							int rxQualFull = Integer.parseInt(value);
							paraMap.put(TotaletailPara.RxQualFull.name(), rxQualFull);
							break;
						case TA:
							int ta = Integer.parseInt(value);
							paraMap.put(TotaletailPara.TA.name(), ta);
							break;
						case TxPower:
							int txPower = Integer.parseInt(value);
							paraMap.put(TotaletailPara.TxPower.name(), txPower);
							break;
						case DLRLCThr:
							tempFtp.setDownRLCThrs(tempFtp.getDownRLCThrs() + Integer.parseInt(value));
							tempFtp.setDownRLCCount(tempFtp.getDownRLCCount() + 1);
							break;
						case GPRSBLER:
							tempFtp.setDownRLCBlers(tempFtp.getDownRLCBlers() + Integer.parseInt(value));
							tempFtp.setDownRLCBlerClunt(tempFtp.getDownRLCBlerClunt() + 1);
							break;
						case DLTBFIdentifier:
							//TODO 此处需要起线程计算当前状态，如果状态改则清除并记录
							if(getFirstData && appModel.getCurrentTask() == TaskType.FTPDownload){
								totalDownTBFTime(Integer.parseInt(value));
							}
							break;
						case DLTS0:
							LogUtil.w(tag,"---DLTS0:"+value);
							if(Integer.parseInt(value) <=3 ){
								tempFtp.setDownTSCounts(tempFtp.getDownTSCounts() + Integer.parseInt(value));
								LogUtil.w(tag,"----tempFtp.getDownTSCounts():"+tempFtp.getDownTSCounts());
							}
							break;
						case DLCS:
							tempFtp.setDownMCCounts(tempFtp.getDownMCCounts() + Integer.parseInt(value));
							tempFtp.setDownMCAllCount(tempFtp.getDownMCAllCount() + 1);
							break;
							
						case ULRLCThr:
							tempFtp.setUpRLCThrs(tempFtp.getUpRLCThrs() + Integer.parseInt(value));
							tempFtp.setUpRLCCount(tempFtp.getUpRLCCount() + 1);
							break;
						case ULTBFIdentifier:
							//TODO 此处需要起线程计算当前状态，如果状态改则清除并记录
							if(getFirstData && appModel.getCurrentTask() == TaskType.FTPUpload){
								totalUlTBFTime(Integer.parseInt(value));
							}
							break;
						case ULTS0:
							if(Integer.parseInt(value) <=3 )
								tempFtp.setUpTSCounts(tempFtp.getUpTSCounts() + Integer.parseInt(value));
							break;
						case ULCS:
							tempFtp.setUpMCCounts(tempFtp.getUpMCCounts() + Integer.parseInt(value));
							tempFtp.setUpMCAllCount(tempFtp.getUpMCAllCount() + 1);
							break;
						}
					}catch(Exception e){
						e.printStackTrace();
						return;
					}
				}
				
				//将统GSM统计参数写入统计对象中
				TotalDataByGSM.getInstance().updateTotalPara(paraMap.entrySet().iterator());
			}else if(intent.getAction().equals(WalkMessage.TotalByFtpIsFull)){
				boolean isDown = intent.getBooleanExtra("IsFtpDown", false);
				if(isDown)
					buildDownPara();
				else
					buildUpPara();
			}else if(intent.getAction().equals(WalkMessage.FtpTest_Upload_Start_Logmask)
					|| intent.getAction().equals(WalkMessage.FtpTest_Download_Start_Logmask)){
				//FTP测试开始时，将当前环境信息初始化
				tempFtp = new TotalFtpModel();
				isDownTBFOpen = false;
				isUpTBFOpen = false;
				getFirstData = false;
				firstTime = System.currentTimeMillis();
				lastTime = firstTime;
			}else if(intent.getAction().equals(WalkMessage.FtpTest_Get_FirstData)){
				getFirstData = true;
				try{
					if(appModel.getCurrentTask() == TaskType.FTPDownload){
						totalDownTBFTime(Integer.parseInt(TraceInfoInterface.getParaValue(UnifyParaID.G_GPRS_DL_TBF_State) != null
							? TraceInfoInterface.getParaValue(UnifyParaID.G_GPRS_DL_TBF_State) : "0"));
					}else if(appModel.getCurrentTask() == TaskType.FTPUpload){
						totalUlTBFTime(Integer.parseInt(TraceInfoInterface.getParaValue(UnifyParaID.G_GPRS_UL_TBF_State) != null
							? TraceInfoInterface.getParaValue(UnifyParaID.G_GPRS_UL_TBF_State) : "0"));
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
	};
}
