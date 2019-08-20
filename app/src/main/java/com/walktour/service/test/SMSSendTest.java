package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.TotalStruct.TotalAppreciation;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.gui.task.parsedata.TaskTestObject;
import com.walktour.gui.task.parsedata.model.task.sms.send.TaskSmsSendModel;
import com.walktour.service.ICallback;
import com.walktour.service.IService;
import com.walktour.service.TestService;

import java.util.HashMap;
import java.util.Map;

/**
 * @Serivice 短信发送测试
 * @descrition 业务流程参考文档《前台产品测试行为一致性规范－数据分册 V1.1.0.doc》
 *  @author qihang.li@dinglicom.com
 * */
public class SMSSendTest extends TestTaskService{
	//tag 
	private static final String tag = "SMSSendTest";			
	//Action
	private static final String ACTION_SENT = "walktour.sms.sent";				/*发送短信后的结果广播*/
	private static final String ACTION_DELIVERED = "walktour.sms.delivered";	/*对方接收后的结果广播*/
	
	//控制发送的变量
	private int count =0;					/*本次测试中尝试发送的次数*/
	private long sendTime ;					/*记录发送的时间 */
	private boolean hasResponse = false;    /*已经响应*/
	
	//测试模型
//	private ApplicationModel appModel = ApplicationModel.getInstance();
	private TaskSmsSendModel smsSendModel ;
	
	private String simNumber = ConfigRoutine.getInstance().getTelNum();		/*sim卡号码*/
	/**
	 * 注册回调接口
	 */
//	private boolean	hasCallbackRegisted = false;		//是否已经注册完成,用于判断测试完成后方可
	private RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<ICallback>();   
    private IService.Stub mBinder = new IService.Stub() {   
        
        public void unregisterCallback(ICallback cb){   
            if(cb != null) {   
                mCallbacks.unregister(cb);   
            }   
        }   
       
        public void registerCallback(ICallback cb){   
            if(cb != null) {
                mCallbacks.register(cb);   
//                hasCallbackRegisted = true;
            }   
        }
		
		public void stopTask(boolean isTestInterrupt,int dropReason) throws RemoteException {
			LogUtil.w( tag , "===Call Stop Task : InitiativeCallTest===");
			
			if( !isTestInterrupt && dropReason!= RcuEventCommand.DROP_NORMAL ){
				displayEvent( getDropReasonString(dropReason) );
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
    
    /** mHandler: 调用回调函数*/
    @SuppressLint("HandlerLeak")
		private Handler mHandler = new Handler()
    {
    	@Override
    	public void handleMessage(Message msg)
    	{
    		if( msg.what == EVENT_CHANGE ){
    			LogUtil.w(tag, "===" + msg.obj.toString() );
    		}
    		resultCallBack(msg);
    		super.handleMessage(msg);
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
                		mCallbacks.getBroadcastItem(i).OnEventChange( repeatTimes +"-"+msg.obj.toString());
                		break;
                	case CHART_CHANGE:
                		mCallbacks.getBroadcastItem(i).onChartDataChanged((Map)msg.obj);
                		break;
                	case DATA_CHANGE:
                	    Map tempMap = (Map) msg.obj;
                        tempMap.put(TaskTestObject.stopResultName, smsSendModel.getTaskName());
                        mCallbacks.getBroadcastItem(i).OnDataChanged(tempMap);
                		break;
                	case TEST_STOP:
                		Map<String,String> resultMap = TaskTestObject.getStopResultMap(smsSendModel);
            			resultMap.put(TaskTestObject.stopResultState, (String)msg.obj);
                		mCallbacks.getBroadcastItem(i).onCallTestStop(resultMap);
                	}
                }   
            } catch (RemoteException e) {   
                LogUtil.w(tag, "", e);   
            }   
            mCallbacks.finishBroadcast();   
        } 
    };//end mHandler
    

    
    
	//注册广播接收器
	private void regedit() {
		IntentFilter filter = new IntentFilter();
		filter.addAction( ACTION_SENT );
		filter.addAction( ACTION_DELIVERED );
		this.registerReceiver(mEventReceiver, filter);
	}
	
	
    @Override  
    public IBinder onBind(Intent intent) {   
        LogUtil.d(tag, "onBind");   
        return mBinder;   
    }
	
    @Override  
    public void onCreate() {   
        super.onCreate(); 
    	LogUtil.d(tag,"---onCreate");
    	
    	simNumber = getSimNumber();
		
    	regedit();
    }
    
    /**
	 * 获取当前的测试对象
	 * @param intent	启动Intent
	 * @return	当前的测试对象
	 */
//	private TaskSmsSendModel getMyModel(Intent intent) {
//		Bundle bundle = intent.getExtras();
//		Object obj = bundle.getSerializable(WalkCommonPara.testModelKey);
//		if(obj == null){
//			LogUtil.w(tag, "data_key is null");
//			return null;
//		}else{
//			return (TaskSmsSendModel)obj;
//		}
//	}
	
	@Override
	public int onStartCommand(Intent intent,int flags, int startId) {
		
		LogUtil.d(tag, "---onStart");
		
		int startFlag = super.onStartCommand(intent, flags, startId);
		
		smsSendModel = (TaskSmsSendModel) super.taskModel;
		
		guid = genGUID();
		
		//验证参数正确性
		if( smsSendModel.getDesNumber().trim().length()==0 ){
			//结束测试
			Message msgFinish = mHandler.obtainMessage( TEST_STOP,TestService.RESULT_SUCCESS );
			msgFinish.sendToTarget();
			return startFlag;
		}
		
		new Thread(  new Tester() ).start();
		
		return startFlag;
	}
	
	@Override  
    public void onDestroy() {   
		this.unregisterReceiver(mEventReceiver);
    	LogUtil.d(tag, "---onDestroy");   
        mHandler.removeMessages(0);   
        mCallbacks.kill();
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();   
    }
	
	/**
	 * 启动新的线程正式开始测试
	 * @SMS发送成功判断：如果发出SMS发送指令之后，在设定超时时长内，AT口返回OK，认为SMS发送成功；
	 * @发送失败判断：如果发出SMS发送指令之后，在设定超时时长内，AT口返回OK，认为SMS发送成功；
	 * 如果发出SMS发送指令之后，在设定超时时长内，返回端口error，认为SMS发送失败；
	 * 如果在设定超时时长内，端口无返回，则不计入SMS测试次数，不插入SMS Send Failure事件，
	 * 并重新发送AT指令，如连续3次如此，则弹出“端口无响应”对话框，提示测试人员检查设置和手机状况，
	 * 并记录SMS端口无响应事件；
	 * */
	private class Tester implements Runnable{
		@Override
		public void run() {
			
			//显示事件
			displayEvent( "SMS Send Start" );
			
			//存储事件
			EventBytes.Builder(mContext, RcuEventCommand.SMS_Access_Request)
			.addCharArray(simNumber.replace("+86","").toCharArray(), 12)
			.addCharArray(smsSendModel.getDesNumber().toCharArray(), 12)
			.writeToRcu(System.currentTimeMillis() * 1000);
			
			//不在循环里进行重新发送，防止流程错误导致不停的发送短信
			sendSMS();			/*第一次尝试发送*/
			sleepForSecond( 15 );
			if( !hasResponse ){
				sendSMS();		//第二次尝试发送
				sleepForSecond( 15 );
				if( !hasResponse ){
					sendSMS();	//第三次尝试发送
					sleepForSecond( 15 );
					if( !hasResponse ){
						//弹出对话框提示
						Toast.makeText(
								SMSSendTest.this, "No Response After Sending SMS", Toast.LENGTH_LONG).show();
						displayEvent("No Response After Sending SMS");
						//计入一次测试次数
						Message msg_count = mHandler.obtainMessage(TEST_STOP);
						msg_count.sendToTarget();
					}
				}
			}
			/*
			 * 2012.11.12修改重发超时,避免 出现这样的问题：当发送超时设置为1秒的时候，
			 * 事件窗口显示短信发送成功，但是，接受短信的手机会接受到三条同样的信息
			 */
			
		}
		
		private void sleepForSecond( int second ){
			try {
				Thread.sleep(1000* second );
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 根据时间和短信内容组合成要发送的短信
	 * */
	private String makeSMS(){
		String fmt = "Index=%d\n" +
					"GUID=%s\n" +
					"%s";
		return String.format(fmt,repeatTimes,guid,smsSendModel.getContent() );
	}
	
	/**
	 * 发送短信
	 * */
	private void sendSMS(){
		count ++;
		
		//超过3次不再发送, 
		if( count <= 3 ){
			LogUtil.w(tag, "---send Message to " +smsSendModel.getDesNumber() );
			
			//发送动作
			SmsManager smsManager = SmsManager.getDefault();
			PendingIntent sentIntent = PendingIntent.getBroadcast(
					this, 0, new Intent( ACTION_SENT  ), 0);
			PendingIntent deliIntent = PendingIntent.getBroadcast(
					this, 0, new Intent( ACTION_DELIVERED ), 0);
			LogUtil.w( tag, makeSMS() );
			smsManager.sendTextMessage(
					smsSendModel.getDesNumber(),		//目标号码
					//smsSendModel.getServerNumber() , 	//服务中心
					null,//ME860写入短信中心号码会无法发送
					makeSMS(), 							//内容	
					sentIntent,				 			//发送方发送后发的广播
					deliIntent							//接收方收到时发的广播
			);
			sendTime = System.currentTimeMillis();
		}
		
	}
	
	/**
	 * 统计数据
	 * @param tatalWap TotalWap其中之一
	 * @param value 数值
	 * */
	private void totalData(TotalStruct.TotalAppreciation totalMms, int value){
		HashMap<String,Integer> totalMap =new HashMap<String,Integer>();
		totalMap.put( totalMms.name(), value);
		Message msg = mHandler.obtainMessage(CHART_CHANGE , totalMap);
		mHandler.sendMessage(msg);
	}
	
	
	/**
	 * 广播接收器:接收通信过程中的信令
	 * */
	private BroadcastReceiver mEventReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			long time = System.currentTimeMillis() * 1000;
			//发送结果
			if( intent.getAction().equals( ACTION_SENT ) ){
				hasResponse = true;
				long delay = System.currentTimeMillis() - sendTime ;
				long timeout = 1000 * ( (long) smsSendModel.getTimeOut()  );
				LogUtil.w(tag, "---Receive send result,delay:"+delay+"\ttime out:"+smsSendModel.getTimeOut() );
				/*
				 * 端口响应设定的超时时间内返回
				 */
				if(  delay < timeout ) {
					Message msg;
					switch( getResultCode() ){
					
					//发送成功
					case Activity.RESULT_OK:
							//存储事件:1表示发送成功，其它值表示失败的ErrorNO
							EventBytes.Builder(mContext, RcuEventCommand.SMS_Send_Finished)
							.addInteger( 1 )
							.addInteger( (int) delay ) 
							.addInteger( repeatTimes )
							.addTguid(guid)
							.writeToRcu(time);
							//显示事件
							displayEvent( "SMS Send Success:Delay " + delay +"ms" );
							//统计
							totalData(TotalAppreciation._SMSSendTry,1);
							totalData(TotalAppreciation._SMSSendSuccs,1);
							totalData(TotalAppreciation._SMSSendDelay,(int)delay);
							
							//计入一次测试次数
							msg = mHandler.obtainMessage(TEST_STOP);
							msg.sendToTarget();
						break;
						
					//发送失败:
					case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
						//存储事件:1表示发送成功，其它值表示失败的ErrorNO
						EventBytes.Builder(mContext, RcuEventCommand.SMS_Send_Finished)
						.addInteger( -1 )
						.addInteger( (int) delay ) 
						.addInteger( repeatTimes )
						.addTguid(guid)
						.writeToRcu(time);
						//显示事件
						displayEvent( "SMS Send Failure" );
						//统计
						totalData(TotalAppreciation._SMSSendTry,1);
						totalData(TotalAppreciation._SMSSendSuccs,0);
						//计入一次测试次数
						msg = mHandler.obtainMessage(TEST_STOP);
						msg.sendToTarget();
						break;
					case SmsManager.RESULT_ERROR_NULL_PDU:
						//存储事件:1表示发送成功，其它值表示失败的ErrorNO
						EventBytes.Builder(mContext, RcuEventCommand.SMS_Send_Finished)
						.addInteger( -1 )
						.addInteger( (int) delay ) 
						.addInteger( repeatTimes )
						.addTguid(guid)
						.writeToRcu(time);
						//显示事件
						displayEvent( "SMS Send Failure" );
						//统计
						totalData(TotalAppreciation._SMSSendTry,1);
						totalData(TotalAppreciation._SMSSendSuccs,0);
						//计入一次测试次数
						msg = mHandler.obtainMessage(TEST_STOP);
						msg.sendToTarget();
						break;
					case SmsManager.RESULT_ERROR_RADIO_OFF:
						//存储事件:1表示发送成功，其它值表示失败的ErrorNO
						EventBytes.Builder(mContext, RcuEventCommand.SMS_Send_Finished)
						.addInteger( -1 )
						.addInteger( (int) delay ) 
						.addInteger( repeatTimes )
						.addTguid(guid)
						.writeToRcu(time);
						//显示事件
						displayEvent( "SMS Send Failure" );
						//统计
						totalData(TotalAppreciation._SMSSendTry,1);
						totalData(TotalAppreciation._SMSSendSuccs,0);
						//计入一次测试次数
						msg = mHandler.obtainMessage(TEST_STOP);
						msg.sendToTarget();
						break;
						
					case SmsManager.RESULT_ERROR_NO_SERVICE:
						//存储事件:1表示发送成功，其它值表示失败的ErrorNO
						EventBytes.Builder(mContext, RcuEventCommand.SMS_Send_Finished)
						.addInteger( -1 )
						.addInteger( (int) delay ) 
						.addInteger( repeatTimes )
						.addTguid(guid)
						.writeToRcu(time);
						//统计
						totalData(TotalAppreciation._SMSSendTry,1);
						totalData(TotalAppreciation._SMSSendSuccs,0);
						//显示事件
						displayEvent( "SMS Send Failure" );
						//计入一次测试次数
						msg = mHandler.obtainMessage(TEST_STOP);
						msg.sendToTarget();
						break;
						
					}
					
				}else{
					/*
					 * 发送超时
					 * */
					
					//存储事件:1表示发送成功，其它值表示失败的ErrorNO
					writeRcuEvent( RcuEventCommand.SMS_Send_Finished,System.currentTimeMillis() * 1000 , 0 ,(int)delay,repeatTimes);
					//显示事件
					displayEvent( "SMS Send Failure" );
					//统计
					totalData(TotalAppreciation._SMSSendTry,1);
					totalData(TotalAppreciation._SMSSendSuccs,0);
					//计入一次测试次数
					Message msg = mHandler.obtainMessage(TEST_STOP);
					msg.sendToTarget();
				}
			}//end ouside if
			
		}//end onReceiver
	};
	
	
	/** 
	 * 写入RCU事件
	 * @param eventFlag 事件标识
	 * @param numbers 电话号码，格式为   发送方号码@@接收方号码
	 * */
//	private void writeRcuEvent(int eventFlag,String numbers){
//		LogUtil.w(tag, "---write event to RCU file,Flag="+String.valueOf(eventFlag) );
//		//UtilsMethod.sendWriteRcuEvent(SMSSendReceiveTest.this,eventFlag,numbers);
//		EventBytes.Builder( this ,eventFlag)
//    	//.addInteger( flag )
//		.addCharArray( numbers.toCharArray(), 12)
//		.writeToRcu(System.currentTimeMillis() * 1000);
//	}
	
	/**
	 * 显示事件
	 * */
	private void displayEvent(String event){
		Message msg = mHandler.obtainMessage(EVENT_CHANGE, event);
		msg.sendToTarget(); 
	}
	
}