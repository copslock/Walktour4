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
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.RcuEventCommand.DropReason;
import com.walktour.Utils.RcuEventCommand.FailReason;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct.DataTaskEvent;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.task.parsedata.TaskTestObject;
import com.walktour.gui.task.parsedata.model.task.traceroute.TaskTraceRouteModel;
import com.walktour.service.ICallback;
import com.walktour.service.IService;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("SdCardPath")
public class TraceRouteService extends TestTaskService {
	private static final String TAG = "TraceRoute";

	private static final int TRACERT_TEST = 81;
	private static final int TRACERT_INITED = 1; 
	private static final int TRACERT_RESOLV_START = 2;
	private static final int TRACERT_RESOLV_SUCCESS = 3;
	private static final int TRACERT_RESOLV_FAILED = 4;
	private static final int TRACERT_ROUTE_START = 5;
	private static final int TRACERT_POINT_REPLAY = 6;
	private static final int TRACERT_POINT_TIMEOUT = 7;
	private static final int TRACERT_ERROR = 9;
	private static final int TRACERT_DROP = 10;
	private static final int TRACERT_FINISH = 11;
	private static final int TRACERT_QUIT = 12;
	private static final int TRACERT_START_TEST = 1001;	
//	private static final int TRACERT_STOP_TEST = 1006;
	
	
	private final int TIMER_CHANG	= 55;	
	private TaskTraceRouteModel taskModel;
	private ipc2jni aIpc2Jni;
	private boolean isCallbackRegister = false;
	private boolean isFinish = false;
	
	private RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<ICallback>();
	
	private IService.Stub mBind = new IService.Stub() {
		@Override
		public void unregisterCallback(ICallback cb) throws RemoteException {
			if(cb != null){
				mCallbacks.unregister(cb);
			}
		}
		
		@Override
		public void stopTask(boolean isTestInterrupt,int dropResion) throws RemoteException{
			if((isTestInterrupt || (dropResion != DropReason.NORMAL.getReasonCode())) && !isFinish){
				isFinish = true;
				displayEvent(DataTaskEvent.Route_Fail.toString());
    			writeRcuEvent(RcuEventCommand.Route_Fail,System.currentTimeMillis() * 1000, isTestInterrupt?
    					FailReason.USER_STOP.getReasonCode():dropResion);
			}
		}

		@Override
		public void registerCallback(ICallback cb) throws RemoteException {
			if(cb != null){
				mCallbacks.register(cb);
				isCallbackRegister = true;
			}
		}
		
		/**
		 * ??????????????????startCommand??????,
		 * ???????????????????????????????????????????????????????????????,
		 * ??????????????????????????????????????????*/
		public boolean getRunState(){
			return startCommondRun;
		}
	};
	
	@Override
	public IBinder onBind(Intent arg0) {
		return mBind;
	}
	
	@Override
	public int onStartCommand(Intent intent,int flags, int startId) {
		LogUtil.d(tag, "---onStart");
		useRoot = Deviceinfo.getInstance().isUseRoot();
		int startFlag = super.onStartCommand(intent, flags, startId);
		
		taskModel = (TaskTraceRouteModel) super.taskModel;
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (!isCallbackRegister) {
						Thread.sleep(100);
					}
				} catch (Exception e) {}
				startTest();				
			}
		});
		thread.start();
		return startFlag; 
	}
	
	public void onDestroy() {
		LogUtil.v(TAG, "--onDestroy--");
    	mHandler.removeMessages(0);
		mCallbacks.kill();
		
		
		//???????????????????????????????????????????????????????????????????????????????????????
	    UtilsMethod.killProcessByPname( "com.walktour.service.test.TraceRouteService", false );
	    UtilsMethod.killProcessByPname( "/data/data/com.dingli.service.test/lib/datatests_android", false );
		super.onDestroy();
    };
	
    private long time = 0;
    private int dealy = 0;
    TraceRouteInfo routeInfo = new TraceRouteInfo();
    private Map<String, Integer> totalMap;
	@SuppressLint("HandlerLeak")
	private Handler mEventHandler = new Handler(){
    	public void handleMessage(android.os.Message msg){
			ipc2msg aMsg = (ipc2msg)msg.obj;
			if(aMsg.test_item != TRACERT_TEST || isFinish){
				return;
			}
			    		
    		switch(aMsg.event_id){
    		case TRACERT_INITED:
    			LogUtil.w(TAG, "recv TRACERT_INITED");
    			LogUtil.w(TAG, aMsg.data + "-->");
				String event_data = "local_if::rmnet_data1\n"
						  + "host_name::" + taskModel.getHost() + "\n"
						  + "packet_size::" + taskModel.getIpPacket() + "\n"
						  + "per_timeout_ms::" + taskModel.getHopTimeout() + "\n"
						  + "per_interval_ms::" + taskModel.getHopInterval() + "\n"
    					  + "nprobes::" + taskModel.getHopProbeNum() ;
    			LogUtil.w(TAG, event_data);
    			aIpc2Jni.send_command(TRACERT_TEST, TRACERT_START_TEST, event_data, event_data.length());
				break;
    		case TRACERT_RESOLV_START:
    			LogUtil.w(TAG, "recv TRACERT_RESOLV_START");
    			analyseMsg(aMsg.data);
    			displayEvent(String.format(DataTaskEvent.Route_DNS_Lookup_Start.toString(), 
    					routeInfo.ip, (aMsg.getRealTime() - time) / 1000));
    			time = aMsg.getRealTime();
    			writeRcuEvent(RcuEventCommand.DNS_LOOKUP_START,aMsg.getRealTime(), -9999, RcuEventCommand.TEST_TYPE_TraceRoute);
    			break;
    		case TRACERT_RESOLV_SUCCESS:
    			LogUtil.w(TAG, "recv TRACERT_RESOLV_SUCCESS");
    			analyseMsg(aMsg.data);
    			
    			dealy = (int) (aMsg.getRealTime() - time) / 1000;
    			displayEvent(String.format(DataTaskEvent.Route_DNS_Lookup_Success.toString(), 
    					routeInfo.ip, dealy));
    			time = aMsg.getRealTime();
    			
    			EventBytes.Builder(getApplicationContext(), RcuEventCommand.DNS_LOOKUP_SUCCESS)
				  .addCharArray(routeInfo.host.toCharArray(), 256)
				  .addInteger(UtilsMethod.convertIpString2Int(routeInfo.ip))
				  .addInteger(dealy)
				  .writeToRcu(aMsg.getRealTime());
    			
    			break;
    		case TRACERT_RESOLV_FAILED:
    			LogUtil.w(TAG, "recv TRACERT_RESOLV_FAILED");
    			analyseMsg(aMsg.data);
    			dealy = (int) (aMsg.getRealTime() - time) / 1000;
    			displayEvent(String.format(DataTaskEvent.Route_DNS_Lookup_Failure.toString(), 
    					dealy, FailReason.getFailReason(routeInfo.reason)));
    			
    			EventBytes.Builder(getApplicationContext(), RcuEventCommand.DNS_LOOKUP_FAILURE)
				  .addCharArray(taskModel.getHost().toCharArray(), 256)
				  .addInteger(dealy)
				  .addInteger(routeInfo.reason)
				  .writeToRcu(aMsg.getRealTime());
    			
    			sendCallBackStop("TEST STOP");
    			time = aMsg.getRealTime();
    			break;
    		case TRACERT_ROUTE_START:
    			LogUtil.w(TAG, "recv TRACERT_ROUTE_START");
    			
    			analyseMsg(aMsg.data);
    			
    			displayEvent(DataTaskEvent.Route_Start.toString());
    			//writeRcuEvent(RcuEventCommand.Route_Start, RcuEventCommand.TEST_TYPE_TraceRoute);
    			LogUtil.w(TAG, routeInfo.srcIp + "-->srcIp");
    			LogUtil.w(TAG, routeInfo.destIp + "-->destIp");
    			EventBytes.Builder(getApplicationContext(), RcuEventCommand.Route_Start)
    					  .addCharArray(routeInfo.srcIp == null? "".toCharArray() : routeInfo.srcIp.toCharArray(), 24)
    					  .addCharArray(routeInfo.destIp == null? "".toCharArray() : routeInfo.destIp.toCharArray(), 24)
    					  .addStringBuffer(taskModel.getHost())
    					  .writeToRcu(aMsg.getRealTime());
    			
    			time = aMsg.getRealTime();
    			totalMap = new HashMap<String, Integer>();
    			totalMap.put(TotalStruct.TotalTraceRoute._traceRouteTrys.name(), 1);
    			mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
    			break;
    		case TRACERT_POINT_REPLAY:
    			LogUtil.w(TAG, "recv TRACERT_POINT_REPLAY");
    			
    			dealy = (int) (aMsg.getRealTime() - time) / 1000;
    			
    			analyseMsg(aMsg.data);
    			displayEvent(String.format(DataTaskEvent.Route_PointReply.toString(), 
    					 routeInfo.id, routeInfo.point_ip ,dealy));
    			
    			LogUtil.w(TAG, routeInfo.point_ip + "-->point_ip");
    			LogUtil.w(TAG, routeInfo.id + "-->id");
    			EventBytes.Builder(getApplicationContext(), RcuEventCommand.Route_PointReply)
    						.addCharArray(routeInfo.point_ip==null?"".toCharArray():routeInfo.point_ip.toCharArray(), 24)
    						.addInteger(routeInfo.id)
    						.addInteger(dealy).writeToRcu(aMsg.getRealTime());
    			
    			time = aMsg.getRealTime();
    			totalMap = new HashMap<String, Integer>();
    			totalMap.put(TotalStruct.TotalTraceRoute._traceRouteHopCounts.name(), 1);
    			mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
    			LogUtil.w(TAG, aMsg.data + "-->");
    			break;
    		case TRACERT_POINT_TIMEOUT:
    			LogUtil.w(TAG, "recv TRACERT_POINT_TIMEOUT");
    			
    			analyseMsg( aMsg.data);
    			displayEvent(String.format(DataTaskEvent.Route_PointTimeOut.toString(), 
    					routeInfo.id, routeInfo.point_ip));
    			LogUtil.w(TAG, routeInfo.point_ip + "-->point_ip");
    			LogUtil.w(TAG, routeInfo.id + "-->id");
    			EventBytes.Builder(getApplicationContext(), RcuEventCommand.Route_PointTimeOut)
				.addCharArray(routeInfo.point_ip==null?"".toCharArray():routeInfo.point_ip.toCharArray(), 24)
				.addInteger(routeInfo.id).writeToRcu(aMsg.getRealTime());
    			
    			time = aMsg.getRealTime();
    			totalMap = new HashMap<String, Integer>();
    			totalMap.put(TotalStruct.TotalTraceRoute._traceRouteHopCounts.name(), 1);
    			mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
    			LogUtil.w(TAG, aMsg.data + "-->");
    			break;
    		case TRACERT_DROP:
    			LogUtil.w(TAG, "recv TRACERT_DROP");
    			analyseMsg(aMsg.data);
    			displayEvent(DataTaskEvent.Route_Fail.toString());
    			writeRcuEvent(RcuEventCommand.Route_Fail,aMsg.getRealTime(), routeInfo.reason);
    			LogUtil.w(TAG, aMsg.data + "-->");
    			break;
    		case TRACERT_ERROR:
    			LogUtil.w(TAG, "recv TRACERT_ERROR");
    			
    			writeRcuEvent(RcuEventCommand.Route_ReslvError,aMsg.getRealTime());
    			LogUtil.w(TAG, aMsg.data + "-->");
    			break;    		
    		case TRACERT_FINISH:
    			LogUtil.w(TAG, "recv TRACERT_FINISH");
    			
    			analyseMsg(aMsg.data);
    			displayEvent(String.format(DataTaskEvent.Route_Success.toString(), 
    						routeInfo.point_all_count, routeInfo.point_all_delay, 
    						routeInfo.point_timeout_count, routeInfo.point_unknown_count));
    			
    			EventBytes.Builder(getApplicationContext(), RcuEventCommand.Route_Success)
				.addInteger(routeInfo.point_all_count)
				.addInteger((int) routeInfo.point_all_delay)
				.addInteger(routeInfo.point_timeout_count)
				.addInteger((int) routeInfo.point_timeout_delay)
				.addInteger(routeInfo.point_unknown_count)
				.addInteger((int) routeInfo.point_unknown_delay).writeToRcu(aMsg.getRealTime());
    			
    			totalMap = new HashMap<String, Integer>();
    			totalMap.put(TotalStruct.TotalTraceRoute._traceRouteSucc.name(), 1);
    			totalMap.put(TotalStruct.TotalTraceRoute._traceRouteDelay.name(), (int) routeInfo.point_all_delay);
    			mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
    			LogUtil.w(TAG, aMsg.data + "-->");
    			break;
    		case TRACERT_QUIT:
    			LogUtil.w(TAG, "recv TRACERT_QUIT");
    			sendCallBackStop("1");
    			aIpc2Jni.uninit_server();
    			break;
    		
    		} 
    		totalMap = null;
		}
	};
	
	private void sendCallBackStop(String msg){
		isFinish = true;
    	Message StopMsg = mHandler.obtainMessage(TEST_STOP, msg);
        StopMsg.sendToTarget();
	}
	
	private class TraceRouteInfo{
		public String destIp;
		public String srcIp;
		public String host;
		public String ip;
		public int reason;
//		public String reasonDesc;
		public int id;
//		String point_name;
		String point_ip;
//		double delay_ms;
		
		int point_all_count;		//????????????
		double point_all_delay;		//?????????, ??????ms
		int point_timeout_count;	//???????????????
		double point_timeout_delay; //????????????, ??????ms
		int point_unknown_count;	//???????????????
		double point_unknown_delay; //????????????, ??????ms
	}
	
	private void analyseMsg(String msg){
		if(msg != null && !"".equals(msg)){
			String[] key_value = msg.split("\n");
			for(String v : key_value){
				String[] res = v.split("::");
				if(res.length < 2) continue;
				
				if(res[0].equals("reason")) routeInfo.reason = Integer.parseInt(res[1]);
//				if(res[0].equals("desc")) routeInfo.reasonDesc = res[1];
				if(res[0].equals("host")) routeInfo.host = res[1];
				if(res[0].equals("ip")) routeInfo.ip = res[1];
				
				if(res[0].equals("id")) routeInfo.id = Integer.parseInt(res[1]);
//				if(res[0].equals("point_name")) routeInfo.point_name = res[1];
				if(res[0].equals("point_ip")) routeInfo.point_ip = res[1];
//				if(res[0].equals("delay_ms")) routeInfo.delay_ms = Double.parseDouble(res[1]);
				
				if(res[0].equals("point_all_count")) routeInfo.point_all_count = Integer.parseInt(res[1]);
				if(res[0].equals("point_all_delay")) routeInfo.point_all_delay = Double.parseDouble(res[1]);
				if(res[0].equals("point_timeout_count")) routeInfo.point_timeout_count = Integer.parseInt(res[1]);
				if(res[0].equals("point_timeout_delay")) routeInfo.point_timeout_delay = Double.parseDouble(res[1]);
				if(res[0].equals("point_unknown_count")) routeInfo.point_unknown_count = Integer.parseInt(res[1]);
				if(res[0].equals("point_unknown_delay")) routeInfo.point_unknown_delay = Double.parseDouble(res[1]);
				if(res[0].equals("src_ip")) routeInfo.srcIp = res[1];
				if(res[0].equals("dest_ip")) routeInfo.destIp = res[1];
			}
		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
    	@Override
    	public void handleMessage(Message msg){
    		super.handleMessage(msg);
			resultCallBack(msg);
    	}
    	
    	//call back
    	@SuppressWarnings({ "rawtypes", "unchecked" })
		private void resultCallBack(Message msg){
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
                		Map<String,String> resultMap = TaskTestObject.getStopResultMap(taskModel);
            			resultMap.put(TaskTestObject.stopResultState, (String)msg.obj);
                		mCallbacks.getBroadcastItem(i).onCallTestStop(resultMap);
                		break;
                	case REAL_PARA:
                		mCallbacks.getBroadcastItem(i).onParaChanged(WalkCommonPara.CALL_BACK_VIDEO_PLAY_REAL_PARA, (Map)msg.obj);
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
    
    private void timerChange() {
		mHandler.sendEmptyMessageDelayed(TIMER_CHANG, 1000);
    }
    
   protected void startTest() {
		if(aIpc2Jni == null){
			aIpc2Jni = new ipc2jni(mEventHandler);
		}
		
		aIpc2Jni.initServer(this.getLibLogPath());	
		
		String args = "-m traceroute -z "+ AppFilePathUtil.getInstance().getAppConfigDirectory();
		String client_path = AppFilePathUtil.getInstance().getAppLibFile(useRoot?"datatests_android":"libdatatests_so.so").getAbsolutePath();
		if( useRoot ){
			aIpc2Jni.set_su_file_path("su -c", "export LD_LIBRARY_PATH=" + AppFilePathUtil.getInstance().getAppLibDirectory());
		}
		
		//String get_root = "chmod 777 " + client_path;
		//aIpc2Jni.set_su_file_path("su -c", "export LD_LIBRARY_PATH=" + getFilesDir().getParent() + "/lib/");
		//ipc2jni.runCommand(get_root);
		LogUtil.w(TAG, client_path);
		LogUtil.w(TAG, args);
		aIpc2Jni.run_client(client_path, args);
		LogUtil.w(TAG, "TraceRoute Runed");
	}
    
    private void displayEvent(String event){
		Message msg = mHandler.obtainMessage(EVENT_CHANGE, event);
		msg.sendToTarget(); 
	}
}
