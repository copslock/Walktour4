package com.walktour.service.test;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;

import com.dinglicom.dataset.model.DataSetEvent;
import com.walktour.Utils.APNOperate;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.TotalStruct.TotalPdp;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.model.task.pdpactive.TaskPdpModel;
import com.walktour.model.TotalSpecialModel;
import com.walktour.service.TestService;

import java.util.HashMap;

/**Title: PDP测试类
*Description: 进行PDP测试的服务类，独立进程
* @author qihang.li@dinglicom.com
* @date 2011.11.1
* @version 1.0
*/
public class PdpTest extends TestTaskService{
	private final String tag ="PDPTest";
	
	//测试任务相关
	private TaskPdpModel taskModel;
	private long actRequestTime = 0;	/*开始Actvie的时间*/
	private long actAcceptTime = 0;
	
	private boolean hasDeactAccept = false;//发出Deactivity后是否收到Deactivate PDP Context Accept
	private boolean hasActRequest = false;//是否收到Activate PDP Context Request
	private boolean hasActAccept = false;//发出Activity后是否收到Activate PDP Context Accept
	
	boolean hasStopProcess = false;
	boolean hasFail = false;

    @Override  
    public void onCreate() {   
        super.onCreate(); 
    	LogUtil.d(tag,"---onCreate");
    	regedit();
    }
	
	@Override
	public int onStartCommand(Intent intent,int flags, int startId) {
		LogUtil.d(tag, "---onStart");
		int startFlag = super.onStartCommand(intent, flags, startId);
		taskModel = (TaskPdpModel) super.taskModel;
		new Thread( new  ThreadTest() ).start();
		return startFlag;
	}
	
	/**测试*/
	private class ThreadTest implements Runnable{
		//MyPhone phone = new MyPhone( PdpTest.this );
		APNOperate apnOperate = APNOperate.getInstance(getApplicationContext());
		@Override
		public void run() {
			boolean needToDeact = true;
			//按测试次数进行测试
			for( int i =0;i<taskModel.getRepeat();i++){
				repeatTimes = i+1;
				if( needToDeact ){
					//发送PDP Deactive指令,，测试之前先确保手机处于未激活状态
					deactivity();
				}
				//间隔T/2时间，发送PDP Active指令
				try {
					Thread.sleep( taskModel.getInterVal() /2 *1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//发送PDP Active指令
				activate();
				//等待超时时间内返回成功指令
				boolean hasSuccess = waitforActivity();
				if( hasSuccess ){
					try {
						Thread.sleep( taskModel.getInterVal() /2 *1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					needToDeact = true;
				}else{
					needToDeact = false;
				}
			}
			
			stopProcess(  TestService.RESULT_SUCCESS  );
		}
		
		/**
		 * 发送PDP Deactive指令
		 * */
		private void deactivity(){
			hasDeactAccept = false;
			//发送Deactivity指令，连续3次直到成功返回Deactivate PDP Context Accept
			for(int i=0;i<3 && !hasDeactAccept ;i++){
				//phone.disableDataConnectivity();
				apnOperate.setMobileDataEnabled(false,false, "",false,1000 * 3);
				//等待5秒内收到
				for( int j=0;j<5*10 && !hasDeactAccept;j++ ){
					try {
						Thread.sleep( 100 );
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		/**
		 * 发送Activate PDP Context Request
		 * */
		private void activate(){
			hasActRequest = false;
			hasActAccept = false;
			//存储PDP_Start事件　
			writeRcuEvent(RcuEventCommand.PDP_Start ,System.currentTimeMillis() * 1000,RcuEventCommand.TEST_TYPE_PDP);
			
			for(int j=0;j<3 && !hasActRequest; j++){
				//发送Active指令
				//phone.enableDataConnectivity();
				apnOperate.setMobileDataEnabled(false,true, "",false,1000 * 3);
				//等待5秒内直到Active_Request信令返回
				for( int i=0;i<5*10 && !hasActRequest;i++){
					try {
						Thread.sleep( 100 );
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			if( !hasActRequest ){
				showNotification( getString(R.string.test_pdp_no_request_msg), "" );
				callbackHandler.obtainMessage(TEST_STOP, "Test Stop").sendToTarget();
			}
		}
		
		/**
		 * 等待Activate PDP Context Request
		 * @return 是否成功
		 * */
		private boolean waitforActivity(){
			for( int i=0;i<taskModel.getKeepTime() * 10 && !hasActAccept ;i++){
				try {
					Thread.sleep( 100 );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			//统计一次结果
			totalResult();
			
			if( hasActAccept ){
				return true;
			}
			fail();
			return false;
		}
		
		
	}

	/**
	 * 广播接收器:接收通信过程中的信令
	 * */
	private BroadcastReceiver mEventReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.i(tag, intent.getAction() );
			if( intent.getAction().equals(WalkMessage.ACTION_EVENT)) {
				int rcuId = intent.getIntExtra( WalkMessage.KEY_EVENT_RCUID, 0);
				switch( rcuId ){
				case DataSetEvent.ET_PDPDeActive_Accept:
					hasDeactAccept = true;
					break;
					
				case DataSetEvent.ET_PDPActive_Request:
					if( !hasActRequest ){
						hasActRequest = true;
						actRequestTime = System.currentTimeMillis();
						//存储PDP Activate请求事件　
						writeRcuEvent( RcuEventCommand.PDP_Request ,System.currentTimeMillis() * 1000);
						//显示PDP Activate请求事件　
						showEvent( "PDP Active Request" );
					}
					break;
					
				case DataSetEvent.ET_PDPActive_Accept:
					if(!hasActAccept){
						hasActAccept = true;
						actAcceptTime = System.currentTimeMillis();
						long delay = actAcceptTime -actRequestTime;
						//存储PDP Activate成功事件　
						writeRcuEvent(RcuEventCommand.PDP_Activate_Success ,System.currentTimeMillis() * 1000,(int) delay );
						//显示PDP Activate成功事件
						showEvent("PDP Active Success: Delay"+delay+"(ms)");
					}
					break;
					
				case DataSetEvent.ET_PDPActive_Reject:
					fail();
					break;
				}
			}
		}
	};
	
	//注册广播接收器
	private void regedit() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(WalkMessage.ACTION_EVENT);
		this.registerReceiver(mEventReceiver, filter);
	}
	
	/**
	 * Attach失败
	 */
	private void fail(){
		if( !hasFail ){
			hasFail = true;
			int deley = (int) (System.currentTimeMillis()-actRequestTime);
			//存储PDP Activate失败事件　
			writeRcuEvent( RcuEventCommand.PDP_Activate_Fail ,System.currentTimeMillis() * 1000 ,deley);
			//显示PDP Activate失败事件
			showEvent("PDP Active Failure：Delay"+deley+"(ms)");
		}
	}
	
	/**
	 * 统计结果
	 */
	private void totalResult(){
		if( hasActRequest ){
			HashMap<String, TotalSpecialModel> totalMap =new HashMap<String, TotalSpecialModel>();
			
			TotalSpecialModel totalRequest = new TotalSpecialModel(
		    		"TotalPdp",String.format("%s/%s",taskModel.getRateUL(),taskModel.getRateDL()),
		    		TotalPdp._pdpRequest.name(),1 );
			totalMap.put( totalRequest.getKeyName(),totalRequest );
			
			if( hasActAccept ){
				TotalSpecialModel totalSuccess = new TotalSpecialModel(
			    		"TotalPdp",
			    		String.format("%s/%s",taskModel.getRateUL(),taskModel.getRateDL()),
			    		TotalPdp._pdpSuccess.name(),1);
				totalMap.put( totalSuccess.getKeyName(),totalSuccess );
				
				int delay = (int) ( actAcceptTime - actRequestTime);
				TotalSpecialModel totalDelay = new TotalSpecialModel(
			    		"TotalPdp",
			    		String.format("%s/%s",taskModel.getRateUL(),taskModel.getRateDL()),
			    		TotalPdp._pdpDelay.name(),delay );
				totalMap.put( totalDelay.getKeyName(),totalDelay );
			}
			
			if ( !totalMap.isEmpty() ) {
				Message msg = callbackHandler.obtainMessage(CHART_CHANGE, totalMap);
				msg.sendToTarget();
			}
		}
	}
	
	/**
	 * 停止当次测试(退出当前测试服务)
	 * @param result
	 */
	private void stopProcess(String result){
		if( !hasStopProcess){
			hasStopProcess = true;
			if(!isInterrupted){
				Message msg = callbackHandler.obtainMessage( TEST_STOP ,result);
				msg.sendToTarget();
			}
		}
	}
	
}