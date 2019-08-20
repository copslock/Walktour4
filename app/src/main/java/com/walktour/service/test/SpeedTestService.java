package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.dingli.service.test.ipc2jni;
import com.dingli.service.test.ipc2msg;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.RcuEventCommand.DropReason;
import com.walktour.Utils.RcuEventCommand.FailReason;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.DataTaskEvent;
import com.walktour.Utils.WalkStruct.DataTaskValue;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.SpeedTestParamter.ServerInfo;
import com.walktour.control.config.SpeedTestSetting;
import com.walktour.gui.task.parsedata.TaskTestObject;
import com.walktour.gui.task.parsedata.model.task.speedtest.TaskSpeedTestModel;
import com.walktour.service.ICallback;
import com.walktour.service.IService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jihong Xie
 * Speed Test Service类
 */
@SuppressLint("SdCardPath")
public class SpeedTestService extends TestTaskService{
	private static final String TAG = "SpeedTest";
	private final int TIMER_CHANG	= 55;	//时间刷新计时器
	//网速测试业务ID
	private static final int SPEEDTEST_TEST						= 84;

	/* 对外发出事件 */

	//事件参数: result(int) 0:init failed 1:init success
	private static final int SPEEDTEST_INITED					= 1;  //初始化完毕

	//连接SpeedTest服务器
	private static final int SPEEDTEST_CONNECT_START			= 2;  //开始连接服务器(打开SPEEDTEST网站)(RCU:SPEEDTEST_SockConnecting)
	private static final int SPEEDTEST_CONNECT_SUCCESS			= 3;  //连接服务器成功(RCU:SPEEDTEST_ConnectSockSucc)
	//事件参数：reason(int) 
	//-1:normal -2:timeout -3:dns-resolv failed -4:server_url failed
	private static final int SPEEDTEST_CONNECT_FAILED			= 4;  //连接服务器失败(RCU:SPEEDTEST_ConnectSockFaile)

	//PING
	private static final int SPEEDTEST_PING_START				= 5;  //PING开始(RCU:SPEEDTEST_Ping_Start)
	//参数 st_ping_suc_info
	private static final int SPEEDTEST_PING_SUCCESS				= 7;  //PING成功(RCU:SPEEDTEST_Ping_Suc)

	//下载
	private static final int SPEEDTEST_DOWNLOAD_START			= 10;  //下载开始(RCU:SPEEDTEST_DL_Start)
	//参数 st_down_qos
	private static final int SPEEDTEST_DOWNLOAD_QOS_ARRIVED		= 11;  //下载QOS
	//参数 st_down_suc_info
	private static final int SPEEDTEST_DOWNLOAD_SUCCESS			= 12;  //下载成功(RCU:SPEEDTEST_DL_Suc)

	//上传
	private static final int SPEEDTEST_UPLOAD_START				= 15;  //上传开始(RCU:SPEEDTEST_UL_Start)
	//参数 st_up_qos
	private static final int SPEEDTEST_UPLOAD_QOS_ARRIVED		= 16;  //上传QOS
	//参数 st_up_suc_info
	private static final int SPEEDTEST_UPLOAD_SUCCESS			= 17;  //上传成功(RCU:SPEEDTEST_UL_Suc)

	//事件参数:st_fail_info结构体
	private static final int SPEEDTEST_FAILED					= 18;  //各项业务测试失败上传失败

	private static final int SPEEDTEST_FINISH					= 21; //业务完成(RCU:SPEEDTEST_Finish)
	private static final int SPEEDTEST_QUIT						= 22; //业务退出(RCU:SPEEDTEST_SocketDisconnected)

	/*外部发来事件*/
	//事件参数: 
	private static final int SPEEDTEST_START_TEST				= 1001; //开始业务(ID不可变)
//	private static final int SPEEDTEST_STOP_TEST				= 1006; //停止业务(ID不可变)
	
	private boolean isCallbackRegister = false;
	private TaskSpeedTestModel taskModel;
	private ipc2jni aIpc2Jni;	
	private boolean isRunning;//业务是否已经启动
	private int step = -1;
	private boolean isStop = false;
	private boolean testInterrupt = false;
	
	
	//测试模型
		private ApplicationModel appModel = ApplicationModel.getInstance();
		
		/**业务回调显示信息*/
		private String msgStr = "";
		
		//远程回调
		private RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<ICallback>();
		
		// 远程回调方法绑定
		private IService.Stub mBind = new IService.Stub() {
			@Override
			public void unregisterCallback(ICallback cb) throws RemoteException {
				LogUtil.i(TAG, "unregisterCallback");
				if (cb != null) {
					mCallbacks.unregister(cb);
				}
			}
			@Override
			public void stopTask(boolean isTestInterrupt,int dropResion) throws RemoteException {
				testInterrupt = isTestInterrupt;
				appModel.setCurrentTask(null);
				LogUtil.w(TAG, isTestInterrupt + ", " + 
						(dropResion != DropReason.NORMAL.getReasonCode()) + ", " + !isStop);
				if((isTestInterrupt || dropResion != DropReason.NORMAL.getReasonCode()) && !isStop){
					isStop = true;
					//LogUtil.d("TTTT", isTestInterrupt + "--->" + dropResion);
					if(step>1 && step < 3){//在连接阶段
		    			displayEvent(WalkStruct.DataTaskEvent.SPEEDTEST_ConnectSockFailed.toString());
		    			writeRcuEvent(RcuEventCommand.SPEEDTEST_CONNECTSOCKFAILED ,System.currentTimeMillis() * 1000);
					}else if( step == 5){//在Ping阶段
						displayEvent(
								String.format(DataTaskEvent.SPEEDTEST_PING_FAILURE.toString(),
										isTestInterrupt?FailReason.USER_STOP.getResonStr() : 
											FailReason.getFailReason(dropResion).getResonStr()));
						EventBytes.Builder(getBaseContext(), RcuEventCommand.SPEEDTEST_FAIL)
						  .addInteger(1)
						  .addInteger(isTestInterrupt?FailReason.USER_STOP.getReasonCode():dropResion)
						  .addInt64(0)
						  .writeToRcu(System.currentTimeMillis() * 1000);
						
						if(isTestInterrupt)
							totalMap.put(TotalStruct.TotalSpeed._speedPingSuccs.name(), 1);
					}else if(step>=10 && step <=12){
						displayEvent(
								String.format(DataTaskEvent.SPEEDTEST_DOWNLOAD_FAILURE.toString(),
										isTestInterrupt?FailReason.USER_STOP.getResonStr() : 
											FailReason.getFailReason(dropResion).getResonStr()));
						EventBytes.Builder(getBaseContext(), RcuEventCommand.SPEEDTEST_FAIL)
						  .addInteger(2)
						  .addInteger(isTestInterrupt?FailReason.USER_STOP.getReasonCode():dropResion)
						  .addInt64((long) (down_index * down_speed /(8 * UtilsMethod.kbyteRage)))
						  .writeToRcu(System.currentTimeMillis() * 1000);
						
						if(isTestInterrupt)
							totalMap.put(TotalStruct.TotalSpeed._speedDownloadSuccs.name(), 1);
					}else if(step>=15 && step <=17){
						displayEvent(
								String.format(DataTaskEvent.SPEEDTEST_UPLOAD_FAILURE.toString(),
										isTestInterrupt?FailReason.USER_STOP.getResonStr() : 
											FailReason.getFailReason(dropResion).getResonStr()));
						EventBytes.Builder(getBaseContext(), RcuEventCommand.SPEEDTEST_FAIL)
						  .addInteger(3)
						  .addInteger(isTestInterrupt?FailReason.USER_STOP.getReasonCode():dropResion)
						  .addInt64((long) ((up_index * up_speed)/(8 * UtilsMethod.kbyteRage)))
						  .writeToRcu(System.currentTimeMillis() * 1000);
						
						if(isTestInterrupt)
							totalMap.put(TotalStruct.TotalSpeed._speedUploadSuccs.name(), 1);
					}
					
					if(isTestInterrupt)
						mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
					
	    			sendMsgToPioneer(",bSuccess=1");
				}
				//stopTest(isTestInterrupt);
			}
			@Override
			public void registerCallback(ICallback cb) throws RemoteException {
				LogUtil.i(TAG, "registerCallback");
				if (cb != null) {
					mCallbacks.register(cb);
					isCallbackRegister = true;
				}
			}
			
			/**
			 * 返回是否执行startCommand状态,
			 * 如果改状态需要在业务中出现某种情况时才为真,
			 * 可以在继承的业务中改写该状态*/
			public boolean getRunState(){
				return startCommondRun;
			}
		};
		
		public IBinder onBind(Intent intent) {
			return mBind;
		};
		
		@Override
		public int onStartCommand(Intent intent,int flags, int startId) {
			LogUtil.d(tag, "---onStart");

			int startFlag = super.onStartCommand(intent, flags, startId);
			taskModel = (TaskSpeedTestModel) super.taskModel;
			
			timerChange();
			Thread thread = new Thread(new RunTest());
			thread.start();
			return startFlag; 
		}
		
		/**
		 * 测试开始时钟消息，每秒钟刷新一次
		 */
		private void timerChange() {
			mHandler.sendEmptyMessageDelayed(TIMER_CHANG, 1000);
			// 如果当前参数传递正确进入计算环节，否则退出消息重发机制
			if (isDLStart && !isUPStart) {
				LogUtil.d(TAG, "down_index=" + down_index +
						"down_speed=" + down_speed + 
						"down_bytes=" + down_bytes + 
						"down_progress=" + down_progress);
				EventBytes.Builder(getBaseContext())
				  .addInteger(WalkCommonPara.SpeedTestDownloadQos)
				  .addInteger(down_index)
				  .addInt64((long)down_speed)
				  .addInt64((long)down_bytes)
				  .addInteger(down_progress)
				  .writeToRcu(WalkCommonPara.MsgDataFlag_A);
			}else if(!isDLStart && isUPStart){
				LogUtil.d(TAG, "up_index=" + up_index +
						"up_speed=" + up_speed + 
						"up_bytes=" + up_bytes + 
						"up_progress=" + up_progress);
				EventBytes.Builder(getBaseContext())
				  .addInteger(WalkCommonPara.SpeedTestUploadQos)
				  .addInteger(up_index)
				  .addInt64((long)up_speed)
				  .addInt64((long)up_bytes)
				  .addInteger(up_progress)
				  .writeToRcu(WalkCommonPara.MsgDataFlag_A);
			}
		}
		
		/**
		 * 开启一个新线程启动任务
		 * @author Jihong Xie
		 *
		 */
	class RunTest implements Runnable {
		@Override
		public void run() {
			LogUtil.i(TAG, "wait for stard test");
			try {
				while (!isCallbackRegister) {
					LogUtil.i(TAG, "test is started");
					Thread.sleep(1000);
				}
				startTest();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
		/** mHandler: 调用回调函数*/
	    @SuppressLint("HandlerLeak")
			private Handler mHandler = new Handler()
	    {
	    	@Override
	    	public void handleMessage(Message msg)
	    	{
	    		super.handleMessage(msg);
				resultCallBack(msg);
	    	}
	    	
	    	//call back
	    	@SuppressWarnings({ "rawtypes", "unchecked" })
			private void resultCallBack(Message msg) 
	    	{
	        	int N = mCallbacks.beginBroadcast();
	            try {   
	                for (int i = 0; i < N; i++) {
	                	switch(msg.what){
	                	case EVENT_CHANGE:
	                		mCallbacks.getBroadcastItem(i).OnEventChange(repeatTimes +"-"+ msg.obj.toString());
	                		break;
	                	case CHART_CHANGE:
	                		mCallbacks.getBroadcastItem(i).onChartDataChanged((Map)msg.obj);
	                		break;
	                	case DATA_CHANGE:
	                	    Map tempMap = (Map) msg.obj;
	                        tempMap.put(TaskTestObject.stopResultName, taskModel.getTaskName());
	                		mCallbacks.getBroadcastItem(i).OnDataChanged(tempMap);
	                		break;
	                	case TEST_STOP:
	                		LogUtil.w(TAG, "--TEST_STOP--");
	                		if(taskModel != null){
	                			Map<String,String> resultMap = TaskTestObject.getStopResultMap(taskModel);
	                			resultMap.put(TaskTestObject.stopResultState, (String)msg.obj);
	                			mCallbacks.getBroadcastItem(i).onCallTestStop(resultMap);
	                		}else{
	                			SpeedTestService.this.onDestroy();
	                		}
	                		break;
	                	case TIMER_CHANG:
	                		timerChange();
	                		break;
	                	}
	                }   
	            } catch (RemoteException e) {   
	                LogUtil.w(TAG, "---", e);   
	            }   
	            mCallbacks.finishBroadcast();   
	        } 
	    };
	    
	    private static final int PING = 1;
	    private static final int DOWNLOAD = 2;
	    private static final int UPLOAD = 3;
	    private long startTime = 0;
	    float speed = 0;
	    long prexSpeeTime = 0;
	    int count = 0;
	    int times = 0;
	    long speedTime = 0;
	    float lastBytes = 0;
	    long lastTimes	= 0;
	    private Map<String, Integer> totalMap;//用于业务统计存存结构
	    private String tmpPionner = ",CurrentOperater=%d,DurationTime=%d,TransmitSize=%d,InstRate=%d";
		protected Boolean isDLStart = Boolean.FALSE;
		protected Boolean isUPStart = Boolean.FALSE;
		AvgSpeed avgSpeed = new AvgSpeed();
	    @SuppressLint("HandlerLeak")
			private Handler mEventHandler = new Handler()
		{
	    	float time;
	    	String strQos;
	    	public void handleMessage(android.os.Message msg){
				ipc2msg aMsg = (ipc2msg)msg.obj;
				if(aMsg.test_item != SPEEDTEST_TEST){
					return;
				}
				
				//如果已经手动停止，或断网，则不再往下执行，
				//因为在断网或手动停止时已经报出任务失败或结束事件，如果再往下报会有出有任务完了后还在打印的情况
				if(testInterrupt || isStop) return;
				count ++;
				totalMap = new HashMap<String, Integer>();
				step = aMsg.event_id;
				switch(aMsg.event_id)
	    		{
	    		case SPEEDTEST_INITED:
	    			LogUtil.w(TAG, "recv SPEEDTEST_INITED");
	    			String fileName = taskModel.getRemoteFile();
	    			fileName = fileName.length()>0? fileName:"random4000x4000.jpg";
	    			String event_data = "local_if::" + /*getNetInterface()+ */ "\n"
							  + "server_url::" + taskModel.getUrl() +"\n"
	    					  + "download_thread::"+ 5 +"\n"
	    					  + "upload_thread::" + 5 +"\n"
	    					  + "download_file::"+ fileName +"\n"//所有服务器都是这个
	    					  ;
	    			aIpc2Jni.send_command(SPEEDTEST_TEST, SPEEDTEST_START_TEST, event_data, event_data.length());

	    			LogUtil.w(TAG, event_data);
					break;
	    		case SPEEDTEST_CONNECT_START:
	    			LogUtil.w(TAG, "recv SPEEDTEST_CONNECT_START");
	    			strQos = aMsg.data;
	    			msgStr = WalkStruct.DataTaskEvent.SPEEDTEST_SockConnecting.toString();
	    			displayEvent(msgStr);
	    			ServerInfo serverInfo = SpeedTestSetting.getInstance().getByUrl(taskModel.getUrl());
	    			if(serverInfo != null){
	    				taskModel.setSponsor(serverInfo.getSponsor());
	    				LogUtil.w(TAG, "----" + taskModel.getSponsor());
	    			}
	    			EventBytes.Builder(getBaseContext(), RcuEventCommand.SPEEDTEST_SOCKCONNECTING)
	    						  .addCharArray(taskModel.getCountry().split("@@")[0].toCharArray(), 64)
	    						  .addCharArray(taskModel.getName().split("@@")[0].toCharArray(), 64)
	    						  .addCharArray( taskModel.getSponsor().toCharArray(), 128)
	    						  .writeToRcu(aMsg.getRealTime());
	    			break;
	    		case SPEEDTEST_CONNECT_SUCCESS:
	    			LogUtil.w(TAG, "recv SPEEDTEST_CONNECT_SUCCESS");
	    			msgStr = WalkStruct.DataTaskEvent.SPEEDTEST_ConnectSockSucc.toString();
	    			displayEvent(msgStr);
	    			writeRcuEvent(RcuEventCommand.SPEEDTEST_CONNECTSOCKSUCC,aMsg.getRealTime());
	    			
	    			totalMap.put(TotalStruct.TotalSpeed._speedPingSuccs.name(), 0);
	    			totalMap.put(TotalStruct.TotalSpeed._speedDelay.name(), 0);
	    			totalMap.put(TotalStruct.TotalSpeed._speedSuccs.name(), 0);
	    			LogUtil.w(TAG, "init trys");
	    			totalMap.put(TotalStruct.TotalSpeed._speedTotalTrys.name(), 0);
	    			totalMap.put(TotalStruct.TotalSpeed._speedDownloadSuccs.name(), 0);
	    			totalMap.put(TotalStruct.TotalSpeed._speedDownKbps.name(), 0);
	    			totalMap.put(TotalStruct.TotalSpeed._speedDownKbpsCounts.name(), 0);
	    			totalMap.put(TotalStruct.TotalSpeed._speedUploadSuccs.name(), 0);
	    			totalMap.put(TotalStruct.TotalSpeed._speedUpKbps.name(), 0);
	    			totalMap.put(TotalStruct.TotalSpeed._speedUpKbpsCounts.name(), 0);
	    			totalMap.put(TotalStruct.TotalSpeed._speedULTotalBytes.name(), 0);
	    			totalMap.put(TotalStruct.TotalSpeed._speedDLTotalBytes.name(), 0);
	    			
	    			mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
	    			break;
	    		case SPEEDTEST_CONNECT_FAILED:
	    			LogUtil.w(TAG, "recv SPEEDTEST_CONNECT_FAILED");
	    			msgStr = WalkStruct.DataTaskEvent.SPEEDTEST_ConnectSockFailed.toString();
	    			displayEvent(msgStr);
	    			writeRcuEvent(RcuEventCommand.SPEEDTEST_CONNECTSOCKFAILED,aMsg.getRealTime());
	    			
	    			//报告Pioneer，任务失败
	    			sendMsgToPioneer(",bSuccess=0");
	    			//LogUtil.w(TAG, aMsg.data);
	    			break;
	    		case SPEEDTEST_PING_START:
	    			LogUtil.w(TAG, "recv SPEEDTEST_PING_START");
	    			isRunning = true;
	    			msgStr = WalkStruct.DataTaskEvent.SPEEDTEST_Ping_Strart.toString();
	    			displayEvent(msgStr);
	    			writeRcuEvent(RcuEventCommand.SPEEDTEST_PING_STRART,aMsg.getRealTime());
	    			LogUtil.w(TAG, "add trys +1");
	    			totalMap.put(TotalStruct.TotalSpeed._speedTotalTrys.name(), 1);
	    			mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
	    			break;
	    		case SPEEDTEST_PING_SUCCESS:
	    			LogUtil.w(TAG, "recv SPEEDTEST_PING_SUCCESS");
	    			strQos = aMsg.data;
	    			//LogUtil.w(TAG, strQos);
	    			getQos(strQos);
	    			
	    			writeRcuEvent(RcuEventCommand.SPEEDTEST_PING_SUC,aMsg.getRealTime(), delay);
	    			msgStr = String.format(WalkStruct.DataTaskEvent.SPEEDTEST_Ping_Suc.toString(), delay);
	    			displayEvent(msgStr);
	    			
	    			totalMap.put(TotalStruct.TotalSpeed._speedPingSuccs.name(), 1);
	    			totalMap.put(TotalStruct.TotalSpeed._speedDelay.name(), delay);
	    			mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
	    			
	    			sendMsgToDashboard(0, 0, "Ping@" + delay + " ms");
	    			avgSpeed.clear();
	    			break;
	    		case SPEEDTEST_DOWNLOAD_START:
	    			LogUtil.w(TAG, "recv SPEEDTEST_DOWNLOAD_START");
					setMainBussinessDirectType(0);
	    			isDLStart = Boolean.TRUE;
	    			msgStr = DataTaskEvent.SPEEDTEST_DL_Strart.toString();
	    			displayEvent(msgStr);
	    			startTime = aMsg.getRealTime();
	    			prexSpeeTime = startTime;
	    			writeRcuEvent(RcuEventCommand.SPEEDTEST_DL_STRART,aMsg.getRealTime());
	    			speedTime = aMsg.getRealTime();
	    			break;
	    		case SPEEDTEST_DOWNLOAD_QOS_ARRIVED:
	    			strQos = aMsg.data;
	    			getQos(strQos);
	    			LogUtil.w(TAG, strQos);
	    			if(down_speed > 0){
	    				avgSpeed.add(down_speed);
	    				speed = avgSpeed.getAvg();
	    				LogUtil.w("Speed", "down_speed=" + down_speed + ",avgSpeed=" + speed);
			    			sendMsgToDashboard(down_speed/1000, down_progress, 
			    			                    "DownLoad@"+ UtilsMethod.decFormat.format(speed) +"kbps");
	    			}
	    			
	    			
	    			//统计下载速率
	    			//totalMap.put(TotalStruct.TotalSpeed._speedDownKbps.name(), (int)(down_speed));
	    			//totalMap.put(TotalStruct.TotalSpeed._speedDownKbpsCounts.name(), 1);
	    			totalMap.put(TotalStruct.TotalSpeed._speedDLTotalBytes.name(), (int)(down_bytes - lastBytes));
	    			lastBytes = down_bytes;
	    			lastTimes = aMsg.getRealTime();
	    			totalMap.put(TotalStruct.TotalSpeed._speedDLTotalTime.name(), (int)(lastTimes - speedTime) / 1000);
	    			speedTime = lastTimes;
	    			
	    			mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
	    			if(count % 4 == 0 ){
	    				strQos = String.format(tmpPionner, 1, 
	    					(int)(aMsg.getRealTime() - startTime) / 1000, 
	    					(int)down_bytes, (int)speed);
	    				sendMsgToPioneer(strQos);
	    			}
	    			//LogUtil.w(TAG, strQos);
	    			break;
	    		case SPEEDTEST_DOWNLOAD_SUCCESS:
	    			LogUtil.w(TAG, "recv SPEEDTEST_DOWNLOAD_SUCCESS");
	    			strQos = aMsg.data;
	    			getQos(strQos);
	    			time = (aMsg.getRealTime() - startTime)/1000000.0f;
	    			msgStr = String.format(DataTaskEvent.SPEEDTEST_DL_Suc.toString(),  time, down_speed/(8*UtilsMethod.kbyteRage) ,
	    					down_transSize/(UtilsMethod.kbyteRage));
	    			displayEvent(msgStr);
	    			
	    			totalMap.put(TotalStruct.TotalSpeed._speedDownloadSuccs.name(), 1);
	    			totalMap.put(TotalStruct.TotalSpeed._speedDownKbps.name(), (int)(down_speed * 100));
	    			totalMap.put(TotalStruct.TotalSpeed._speedDownKbpsCounts.name(), 1);
	    			mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
	    			
	    			EventBytes.Builder(getBaseContext(), RcuEventCommand.SPEEDTEST_DL_SUC)
							    				.addDouble(down_speed)
							    				.addInt64((long) down_transSize)
							    				.writeToRcu(aMsg.getRealTime());
	    			
	    			//LogUtil.w(TAG, "--msgStr:" + msgStr + "--ds:" + down_speed + "--dt:" + down_transSize);
	    			break;
	    		case SPEEDTEST_UPLOAD_START:
	    			LogUtil.w(TAG, "recv SPEEDTEST_UPLOAD_START");
					setMainBussinessDirectType(1);
	    			isDLStart = Boolean.FALSE;
	    			isUPStart = Boolean.TRUE;
	    			startTime = aMsg.getRealTime();
	    			msgStr = DataTaskEvent.SPEEDTEST_UL_Start.toString();
	    			displayEvent(msgStr);
	    			
	    			writeRcuEvent(RcuEventCommand.SPEEDTEST_UL_START,aMsg.getRealTime());
	    			avgSpeed.clear();
	    			speedTime = startTime;
	    			lastBytes = 0;
	    			break;
	    		case SPEEDTEST_UPLOAD_QOS_ARRIVED:
	    			strQos = aMsg.data;
	    			getQos(strQos);
	    			if(up_speed > 0){
	    				avgSpeed.add(up_speed);
	    				speed = avgSpeed.getAvg();
	    				LogUtil.w("Speed", "up_speed=" + up_speed + ",avgSpeed=" + speed);
		    			
		    			sendMsgToDashboard(up_speed/1000, up_progress, 
		    			                    "Upload@" + UtilsMethod.decFormat.format(speed) +"kbps");
	    			}
	    			
	    			//统计上传速率
	    			//totalMap.put(TotalStruct.TotalSpeed._speedUpKbps.name(), (int)(up_speed));
	    			//totalMap.put(TotalStruct.TotalSpeed._speedUpKbpsCounts.name(), 1);
	    			
	    			totalMap.put(TotalStruct.TotalSpeed._speedULTotalBytes.name(), (int)(up_bytes - lastBytes));
	    			lastBytes = up_bytes;
	    			totalMap.put(TotalStruct.TotalSpeed._speedULTotalTime.name(), (int)(aMsg.getRealTime() - speedTime) / 1000);
	    			speedTime = aMsg.getRealTime();
	    			
	    			mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
	    			
	    			if(count % 4 == 0 ){
	    				strQos = String.format(tmpPionner, 2, 
	    					(int)(aMsg.getRealTime() - startTime) / 1000, 
	    					(int)up_bytes, (int)speed);
	    				sendMsgToPioneer(strQos);
	    			}
	    			//LogUtil.w(TAG, strQos);
	    			break;
	    		case SPEEDTEST_UPLOAD_SUCCESS:
	    			LogUtil.w(TAG, "recv SPEEDTEST_UPLOAD_SUCCESS");
	    			strQos = aMsg.data;
	    			getQos(strQos);
	    			time = (aMsg.getRealTime() - startTime)/1000000.0f;
	    			msgStr = String.format(DataTaskEvent.SPEEDTEST_UL_Suc.toString(), time, up_speed/(8 * UtilsMethod.kbyteRage) ,
	    					up_transSize/UtilsMethod.kbyteRage);
	    			displayEvent(msgStr);
	    			
	    			totalMap.put(TotalStruct.TotalSpeed._speedUploadSuccs.name(), 1);
	    			totalMap.put(TotalStruct.TotalSpeed._speedUpKbps.name(), (int)(up_speed * 100));
	    			totalMap.put(TotalStruct.TotalSpeed._speedUpKbpsCounts.name(), 1);
	    			mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
	    			
	    			EventBytes.Builder(getBaseContext(),RcuEventCommand.SPEEDTEST_UL_SUC)
	    					  .addDouble(up_speed)
	    					  .addInt64((long) up_transSize)
	    					  .writeToRcu(aMsg.getRealTime());
	    			
	    			break;
	    		case SPEEDTEST_FAILED:
	    			LogUtil.w(TAG, "recv SPEEDTEST_FAILED");
	    			strQos = aMsg.data;
	    			LogUtil.w(TAG, strQos);
	    			int type = -1;
	    			int reason = -1;
	    			String[] arr_info = strQos.split("\n");
	    			try {
	    				for(String str: arr_info){
	    					String[] key_value = str.split("::");
	    					if(key_value.length < 2) continue;
	    					if(key_value[0].equals("type")){
	    						type = Integer.parseInt(key_value[1]);
	    					}else if(key_value[0].equals("reason")){
	    						reason = Integer.parseInt(key_value[1]);
	    					}
	    				}
	    			}catch (Exception e) {
						e.printStackTrace();
					}
	    			
	    			long actualSize = 0;
	    			switch (type) {
	    			case PING:
	    				msgStr = String.format(DataTaskEvent.SPEEDTEST_PING_FAILURE.toString(), 
	    						FailReason.getFailReason(reason).getResonStr());
	    				break;
					case DOWNLOAD:
						msgStr = String.format(DataTaskEvent.SPEEDTEST_DOWNLOAD_FAILURE.toString(),
								FailReason.getFailReason(reason).getResonStr());
						actualSize = (long) ((down_index * down_speed)/(8*1000));
						break;
					case UPLOAD:
						msgStr = String.format(DataTaskEvent.SPEEDTEST_UPLOAD_FAILURE.toString() ,
								FailReason.getFailReason(reason).getResonStr());
						actualSize = (long) (up_index * up_speed)/(8*1000);
						break;
					default:
	    				msgStr = DataTaskEvent.SPEEDTEST_FAILED.toString();
	    				break;
					}
	    			
	    			
	    			displayEvent(msgStr);
	    			//writeRcuEvent(RcuEventCommand.SPEEDTEST_FAIL, type, reason, actualSize);
	    			LogUtil.w("failuer", reason + "");
	    			EventBytes.Builder(getBaseContext(), RcuEventCommand.SPEEDTEST_FAIL)
	    						  .addInteger(type)
	    						  .addInteger(reason)
	    						  .addInt64(actualSize)
	    						  .writeToRcu(aMsg.getRealTime());
	    			//报告Pioneer，任务失败
		    		sendMsgToPioneer(",bSuccess=0");
	    			break;
	    		case SPEEDTEST_FINISH:
	    			LogUtil.w(TAG, "recv SPEEDTEST_FINISH");
	    			msgStr = DataTaskEvent.SPEEDTEST_SocketDisconnected.toString();
	    			displayEvent(msgStr);
	    			
	    			totalMap.put(TotalStruct.TotalSpeed._speedSuccs.name(), 1);
	    			mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
	    			
	    			writeRcuEvent(RcuEventCommand.SPEEDTEST_FINISH,aMsg.getRealTime());
	    			//报告Pioneer，任务成功
	    			sendMsgToPioneer(",bSuccess=1");
	    			break;
	    		case SPEEDTEST_QUIT:
	    			LogUtil.w(TAG, "recv SPEEDTEST_QUIT");
	    			writeRcuEvent(RcuEventCommand.SPEEDTEST_SOCKETDISCONNECTED,aMsg.getRealTime());
	    			sendMsgToDashboard(0, 0, "");
	    			sendCallBackStop(isRunning?"1":"TEST STOP");
					if(aIpc2Jni != null){
						aIpc2Jni.uninit_server();
					}
	    			isStop = true;
					stopSelf();
	    			break;
	    		} 
	    	}
		};
		
		//private float[] values = new float[15];
		private Map<String, Object> dataMap =new HashMap<String, Object>();
		
		/**
         * 发送瞬时速率到数据页面
         * float[] values,
         * , String rightValue
         * @param currendSpeed 速率
         * */
        private void sendMsgToDashboard(float currendSpeed,int progress, String leftTitle){
        	//LogUtil.w("TTTT", "currendSpeed=" + currendSpeed + ", progress=" + progress + ",leftTitle=" + leftTitle);
        	 dataMap.clear();
        	 dataMap.put(DataTaskValue.BordCurrentSpeed.name(), UtilsMethod.decFormat.format(currendSpeed));
        	 //dataMap.put(DataTaskValue.BordPoints.name(), values);
        	 dataMap.put(DataTaskValue.BordProgress.name(), progress);
        	 dataMap.put(DataTaskValue.BordLeftTitle.name(), leftTitle);
        	 //dataMap.put(DataTaskValue.BordRightTile.name(), rightValue);
        	 
        	 Message msg = mHandler.obtainMessage(DATA_CHANGE,dataMap);
     		 mHandler.sendMessage(msg);
        }
		
    	
	protected void startTest() {
		LogUtil.w(TAG, "start to prep args");
		
		isRunning = true;
		if(aIpc2Jni == null){
			aIpc2Jni = new ipc2jni(mEventHandler);
		}

		aIpc2Jni.initServer(this.getLibLogPath());
		
		String args = "-m speedtest -z "+ AppFilePathUtil.getInstance().getAppConfigDirectory();
		String client_path = AppFilePathUtil.getInstance().getAppLibFile(useRoot?"datatests_android":"libdatatests_so.so").getAbsolutePath();
		//String get_root = "chmod 777 " + client_path;
		if( useRoot ){
			aIpc2Jni.set_su_file_path("su -c", "export LD_LIBRARY_PATH=" + AppFilePathUtil.getInstance().getAppLibDirectory());
		}
		//ipc2jni.runCommand(get_root);
		aIpc2Jni.run_client(client_path, args);
	}
	
	class AvgSpeed extends ArrayList<Float>{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public float getAvg(){
			float total = 0;
			for(float v : this){
				total += v;
			}
			return total/this.size()/1000;
		}
	}
	
	/**
	 * 停止测试
	 * @param isTestInterrupt 是否是人工停止
	 */
//	private void stopTest(boolean isTestInterrupt){
//		if(isRunning){
//			if(isTestInterrupt){
//				aIpc2Jni.send_command(SPEEDTEST_TEST, SPEEDTEST_STOP_TEST, "", 0);
//			}else{
//				sendCallBackStop("TEST STOP");
//			}
//		}
//
//		aIpc2Jni.uninit_server();
//		isRunning = false;
//	}
	
	/**
	 * 显示事件
	 * */
	private void displayEvent(String event){
		Message msg = mHandler.obtainMessage(EVENT_CHANGE, event);
		msg.sendToTarget(); 
	}
	
	/**
     * [调用停止当前业务接口]<BR>
     * [如果当前为手工停止状态不能调用当前停止接口]
     * @param msg
     */
	private void sendCallBackStop(String msg){
	    if(!isInterrupted){
    	    Message StopMsg = mHandler.obtainMessage(TEST_STOP, msg);
            StopMsg.sendToTarget();
	    }
	}
	
	private void getQos(String qos){
		//android.os.Debug.waitForDebugger();
		String[] arr_info = qos.split("\n");
		try {
			for(String str: arr_info){
				String[] key_value = str.split("::");
				if(key_value.length < 2) continue;
				if(key_value[0].equals("up_speed")){
					up_speed = Float.parseFloat(key_value[1]);
					//LogUtil.w(TAG, "up_speed=" + up_speed);
				}else if(key_value[0].equals("up_bytes_total")){
					up_transSize = Float.parseFloat(key_value[1]);
					//LogUtil.w(TAG, "up_transSize=" + up_transSize);
				}else if(key_value[0].equals("down_speed")){
					down_speed = Float.parseFloat(key_value[1]);
					//LogUtil.w(TAG, "down_speed=" + down_speed);
				}else if(key_value[0].equals("down_bytes_total")){
					down_transSize = Float.parseFloat(key_value[1]);
				}else if(key_value[0].equals("rtt")){
					delay = Integer.parseInt(key_value[1]);
				}else if(key_value[0].equals("down_progress")){
					down_progress = Integer.parseInt(key_value[1]);
				}else if(key_value[0].equals("up_progress")){
					up_progress = Integer.parseInt(key_value[1]);
				}else if(key_value[0].equals("up_index")){
					up_index = Integer.parseInt(key_value[1]);
				}else if(key_value[0].equals("down_index")){
					down_index = Integer.parseInt(key_value[1]);
				}else if(key_value[0].equals("down_bytes")){
					down_bytes = Float.parseFloat(key_value[1]);
				}else if(key_value[0].equals("up_bytes")){
					up_bytes = Float.parseFloat(key_value[1]);
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

//		if( telManager != null){
//    		telManager.listen(phStateListener, PhoneStateListener.LISTEN_NONE);
//    	}
		
		mHandler.removeMessages(0);   
        mCallbacks.kill();
        android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	/*private PhoneStateListener phStateListener = new PhoneStateListener(){

		@Override
		public void onDataConnectionStateChanged(int state) {
			super.onDataConnectionStateChanged(state);
			if(!isStop){
				 if(state == TelephonyManager.DATA_DISCONNECTED) {//网络断开
					 LogUtil.w(TAG, "network data sdisconnected");
					 isStop = true;
					 if(step>1 && step < 3){//在连接阶段
		    			displayEvent(WalkStruct.DataTaskEvent.SPEEDTEST_ConnectSockFailed.toString());
		    			writeRcuEvent(RcuEventCommand.SPEEDTEST_CONNECTSOCKFAILED);
					}else if( step == 5){//在Ping阶段
						displayEvent(DataTaskEvent.SPEEDTEST_PING_FAILURE.toString());
						//writeRcuEvent(RcuEventCommand.SPEEDTEST_FAIL, 1, 2, 0);
						EventBytes.Builder(getBaseContext(), RcuEventCommand.SPEEDTEST_FAIL)
						  .addInteger(1)
						  .addInteger(2)
						  .addInt64(0)
						  .writeToRcu();
					}else if(step>=10 && step <=12){
						displayEvent(DataTaskEvent.SPEEDTEST_DOWNLOAD_FAILURE.toString());
						//writeRcuEvent(RcuEventCommand.SPEEDTEST_FAIL, 2, 2, (int)(down_index * down_speed)/(8*UtilsMethod.kbyteRage));
						EventBytes.Builder(getBaseContext(), RcuEventCommand.SPEEDTEST_FAIL)
						  .addInteger(2)
						  .addInteger(2)
						  .addInt64((long)(down_index * down_speed)/(8*UtilsMethod.kbyteRage))
						  .writeToRcu();
					}else if(step>=15 && step <=17){
						displayEvent(DataTaskEvent.SPEEDTEST_UPLOAD_FAILURE.toString());
						//writeRcuEvent(RcuEventCommand.SPEEDTEST_FAIL, 3, 2, (int)(up_index * up_speed)/(8*UtilsMethod.kbyteRage));
						EventBytes.Builder(getBaseContext(), RcuEventCommand.SPEEDTEST_FAIL)
						  .addInteger(3)
						  .addInteger(2)
						  .addInt64((long)(up_index * up_speed)/(8*UtilsMethod.kbyteRage))
						  .writeToRcu();
					}
					sendCallBackStop(isRunning?"1":"TEST STOP");
				 }
			 }
		}

		@Override
		public void onServiceStateChanged(ServiceState serviceState) {
			super.onServiceStateChanged(serviceState);
			if(!isStop){
				if(serviceState.getState() == ServiceState.STATE_OUT_OF_SERVICE){//脱网
					LogUtil.w(TAG, "state out of service");
					isStop = true;
					if(step>1 && step < 3){//在连接阶段
		    			displayEvent(WalkStruct.DataTaskEvent.SPEEDTEST_ConnectSockFailed.toString());
		    			writeRcuEvent(RcuEventCommand.SPEEDTEST_CONNECTSOCKFAILED);
					}else if( step == 5){//在Ping阶段
						displayEvent(DataTaskEvent.SPEEDTEST_PING_FAILURE.toString());
						//writeRcuEvent(RcuEventCommand.SPEEDTEST_FAIL, 1, 3, 0);
						EventBytes.Builder(getBaseContext(), RcuEventCommand.SPEEDTEST_FAIL)
						  .addInteger(1)
						  .addInteger(3)
						  .addInt64(0)
						  .writeToRcu();
					}else if(step>=10 && step <=12){
						displayEvent(DataTaskEvent.SPEEDTEST_DOWNLOAD_FAILURE.toString());
						//writeRcuEvent(RcuEventCommand.SPEEDTEST_FAIL, 2, 3, (int)(down_index * down_speed)/(8*1000));
						EventBytes.Builder(getBaseContext(), RcuEventCommand.SPEEDTEST_FAIL)
						  .addInteger(2)
						  .addInteger(3)
						  .addInt64((long)(down_index * down_speed)/(8*1000))
						  .writeToRcu();
					}else if(step>=15 && step <=17){
						displayEvent(DataTaskEvent.SPEEDTEST_UPLOAD_FAILURE.toString());
						//writeRcuEvent(RcuEventCommand.SPEEDTEST_FAIL, 3, 3, (int)(up_index * up_speed)/(8*1000));
						EventBytes.Builder(getBaseContext(), RcuEventCommand.SPEEDTEST_FAIL)
						  .addInteger(2)
						  .addInteger(3)
						  .addInt64((long)(up_index * up_speed)/(8*1000))
						  .writeToRcu();
					}
					
					sendCallBackStop(isRunning?"1":"TEST STOP");
				}
			}
		}
	};*/
	
	private float up_speed, down_speed, up_bytes, down_bytes;
	private float up_transSize, down_transSize;
	private int delay, down_progress, up_progress, up_index, down_index;

}
