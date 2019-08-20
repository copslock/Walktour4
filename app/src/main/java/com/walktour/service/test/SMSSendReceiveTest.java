package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.TotalStruct.TotalAppreciation;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.gui.task.parsedata.TaskTestObject;
import com.walktour.gui.task.parsedata.model.task.sms.sendreceive.TaskSmsSendReceiveModel;
import com.walktour.service.ICallback;
import com.walktour.service.IService;
import com.walktour.service.TestService;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @ClassName 短信自发自收测试
 * @descrition 业务流程参考文档《前台产品测试行为一致性规范－数据分册 V1.1.0.doc》
 * @date	2010.01.24
 * @author qihang.li@dinglicom.com
 * */
public class SMSSendReceiveTest extends TestTaskService{
	//tag 
	private static final String tag = "SMSSendReceiveTest";			
	
	private static final int ERROR_FORMAT = -9999;		//短信的格式错误
	
	//Action
	private static final String ACTION_SENT = "walktour.sms.sent";				/*发送短信后的结果广播*/
	private static final String ACTION_DELIVERED = "walktour.sms.delivered";	/*对方接收后的结果广播*/
	
	//控制发送的变量
	private int count =0;					/*本次测试中尝试发送的次数*/
	private long sendTime ;					/*发送的时间 */
//	private long sendSuccessTime;			/*发送成功的时间*/
	private boolean hasResponse = false;    /*已经响应*/
	private boolean hasReceived = false;	/*是否成功接收发送的短信*/
	private boolean hasRead = false;		/*是否把接收的短信改为已读*/
	private long receiveTime 	= 0;		/*短信接收的时间*/	
	

	//测试模型
//	private ApplicationModel appModel = ApplicationModel.getInstance();
	private TaskSmsSendReceiveModel testModel ;
	
	private String simNumber = ConfigRoutine.getInstance().getTelNum();	/*sim卡号码*/
	
	private String guid = "";
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
		
		public void stopTask(boolean isTestInterrupt,int dropReason ) throws RemoteException {
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
    	@SuppressWarnings({ "unchecked", "rawtypes" })
			private void resultCallBack(Message msg) 
    	{
        	int N = mCallbacks.beginBroadcast();
            try {   
                for (int i = 0; i < N; i++) {
                	switch(msg.what){
                	case EVENT_CHANGE:
                		mCallbacks.getBroadcastItem(i).OnEventChange(repeatTimes +"-"+msg.obj.toString());
                		break;
                	case CHART_CHANGE:
                		mCallbacks.getBroadcastItem(i).onChartDataChanged((Map)msg.obj);
                		break;
                	case DATA_CHANGE:
                	    Map tempMap = (Map) msg.obj;
                        tempMap.put(TaskTestObject.stopResultName, testModel.getTaskName());
                        mCallbacks.getBroadcastItem(i).OnDataChanged(tempMap);
                		break;
                	case TEST_STOP:
                		Map<String,String> resultMap = TaskTestObject.getStopResultMap(testModel);
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
		filter.addAction( WalkMessage.telephonySMSReceived );
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
    	regedit();
    	
    	simNumber = getSimNumber();
    }
    
	@Override
	public int onStartCommand(Intent intent,int flags, int startId) {
		LogUtil.d(tag, "---onStart");
		
		int startFlag = super.onStartCommand(intent, flags, startId);
		
		testModel = (TaskSmsSendReceiveModel) super.taskModel;
		
		guid = genGUID();
		
		//验证参数正确性
		if( testModel.getDesNumber().trim().length()==0 ){
			//结束测试
			Message msgFinish = mHandler.obtainMessage( TEST_STOP , TestService.RESULT_SUCCESS  );
			msgFinish.sendToTarget();
			return super.onStartCommand(intent, flags, startId);
		}
		
		//监听短信数据库变化
		SMSObserver observer = new SMSObserver( new Handler() );
		this.getContentResolver().registerContentObserver(
				Uri.parse( "content://sms/" ),//注意这里是"content://sms/"而不是"content://sms/inbox/"
				true,
				observer);
		
		//启动发送线程
		new Thread(  new Sender() ).start();
		
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
	private class Sender implements Runnable{
		@Override
		public void run() {
			
			//显示事件
			displayEvent( "SMS Send Start" );
			
			//存储事件
			EventBytes.Builder(mContext, RcuEventCommand.SMS_Access_Request)
			.addCharArray(simNumber.replace("+86", "").toCharArray(), 12)//+86超出了12无法存到RCU
			.addCharArray(testModel.getDesNumber().toCharArray(), 12)
			.writeToRcu(System.currentTimeMillis() * 1000);
			
			//不在循环里进行重新发送，防止流程错误导致不停的发送短信
			sendSMS();			/*第一次尝试发送*/
			sleepForSecond( 15 );
			if( !hasResponse ){
				sendSMS();		/*第二次尝试发送*/
				sleepForSecond( 15 );
				if( !hasResponse ){
					sendSMS();	/*第三次尝试发送*/
					sleepForSecond( 15 );
					if( !hasResponse ){
						displayEvent("No Response After Sending SMS");
						//不计入测试次数,而是等接收线程确实确定是否成功
					}
				}
			}
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
	 * 接收短信线程：
	 * 超出设定的时长还未收到短信则报接收失败 
	 * */
	private class Receiver implements Runnable{
		@Override
		public void run() {
			
			//等待设定的超时时长
			int time = testModel.getReceiveTimeOut();
			while( !hasReceived && time > 0 ){
				time --;
				sleepForSecond(1);
			}
			
			//仍未收到短信则判断为接收失败
			if( !hasReceived ){
				// 显示接收失败
				displayEvent("SMS Receive Failure");
				// 存储接收失败事件
				writeRcuEvent( RcuEventCommand.SMS_Received,System.currentTimeMillis() * 1000,0,testModel.getReceiveTimeOut()*1000,repeatTimes );
				
				// 计入一次测试次数
				Message msg = mHandler.obtainMessage(TEST_STOP);
				msg.sendToTarget();
			}
			
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
		return String.format(fmt,repeatTimes,guid,testModel.getContent() );
	}
	
	/**
	 * 发送短信
	 * */
	private void sendSMS(){
		count ++;
		
		//超过3次不再发送, 
		if( count <= 3 ){
			LogUtil.w(tag, "---send Message to " +testModel.getDesNumber() );
			
			//发送动作
			SmsManager smsManager = SmsManager.getDefault();
			PendingIntent sentIntent = PendingIntent.getBroadcast(
					this, 0, new Intent( ACTION_SENT  ), 0);
			PendingIntent deliIntent = PendingIntent.getBroadcast(
					this, 0, new Intent( ACTION_DELIVERED ), 0);
			LogUtil.w( tag, makeSMS() );
			sendTime = System.currentTimeMillis();
			smsManager.sendTextMessage(
					testModel.getDesNumber(),		//目标号码
					//testModel.getServerNumber() , 	//服务中心
					null,//ME860手机填写服务中心号码时无法发送
					makeSMS(), 							//内容	
					sentIntent,				 			//发送方发送后发的广播
					deliIntent							//接收方收到时发的广播
			);
			
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
				//端口响应标志为true
				hasResponse = true;
				//计算时延
				long delay = System.currentTimeMillis() - sendTime ;
				long sendTimeout = 1000 * ( (long ) testModel.getSendTimeOut()  );
				LogUtil.w(tag, "---Receive send result,delay:"+delay+"\ttime out:"+testModel.getSendTimeOut() );
				//如结果是在设定的超时时间内返回
				if(  delay < sendTimeout ) {
					Message msg;
					
					switch( getResultCode() ){
					
					//发送成功
					case Activity.RESULT_OK:

							//成功接收时间
//							sendSuccessTime = System.currentTimeMillis();
							
							//存储事件:1表示发送成功，其它值表示失败的ErrorNO
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
							totalData(TotalAppreciation._SMSReceiveTry,1);
							//启动接收线程
							new Thread(  new Receiver() ).start();
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
						//显示事件
						displayEvent( "SMS Send Failure" );
						//统计
						totalData(TotalAppreciation._SMSSendTry,1);
						totalData(TotalAppreciation._SMSSendSuccs,0);
						//计入一次测试次数
						msg = mHandler.obtainMessage(TEST_STOP, "SMS Send Failure:No Service");
						msg.sendToTarget();
						break;
						
					}
					
				}else{
					/*
					 * 发送超时
					 * */
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
					Message msg = mHandler.obtainMessage(TEST_STOP);
					msg.sendToTarget();
					
				}
			}//end ouside if
			
			/*短信接收广播*/
			if(intent.getAction().equals(WalkMessage.telephonySMSReceived)){
	        	LogUtil.w(tag,"-----telephonySMSReceived----");
	        	//此广播先于SMSObserver监控到变化
	        	if( !hasReceived ){
	        		receiveTime = System.currentTimeMillis();
	        		
	        		//获取短信
	        		Bundle bundle = intent.getExtras();
	        		Object[] pdus = (Object[]) bundle.get("pdus");
	        		SmsMessage[] msgs = new SmsMessage[pdus.length];//事实上这里通常是一条短信
	        		for (int i = 0; i < pdus.length; i++) {
	        			msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
	        		}

	        		//解析短信内容,如果是测试短信并没有超时，则报告成功收取
					long receiveTimeout = 1000 * ( (long) testModel.getReceiveTimeOut() ); 
					String body =  msgs[0].getDisplayMessageBody();
					int delay = getDelay(body) ;
					int index = getIndex(body);
					if( 	0 < delay && delay<=receiveTimeout 
							&& body.startsWith("Index=") && body.contains("GUID=") ){
						
						//接收标志位
						hasReceived = true;
						
						//统计
						totalData(TotalAppreciation._SMSReceiveSuccs,1);
						totalData(TotalAppreciation._SMSReceiveDelay,delay);
						totalData(TotalAppreciation._SMSPtoPDelay,(int)(System.currentTimeMillis()-sendTime) );
						totalData(TotalAppreciation._SMSPtoPCount,1);
						
						//存储短信接收成功事件
						EventBytes.Builder(mContext, RcuEventCommand.SMS_Received)
						.addInteger(1)
						.addInteger(delay)
						.addInteger(index)
						.addTguid(guid)
						.writeToRcu(time);
						
						//显示短信接收成功
						displayEvent("SMS Receive Success:Delay "+delay+"ms");
						
						//计入一次测试次数
						Message msg = mHandler.obtainMessage(TEST_STOP);
						msg.sendToTarget();
					}
	        	
	        	}
	        }
			
		}//end onReceiver
	};
	
	/**
	 * 分析短信是否测试短信
	 * @param sms 短信
	 * @return 接收的时延
	 * */
	private int getDelay( String  smsBody ){
		try{
			LogUtil.e(tag, smsBody );
			guid = smsBody.split("\n")[1].split("=")[1];
			String time = guid.substring(0, 17);
			
			SimpleDateFormat dateFm = new SimpleDateFormat("yyyyMMddHHmmssSSS",Locale.getDefault()); //格式化当前系统日期
			long sendTime = dateFm.parse(time).getTime();
			long delay =  receiveTime - sendTime ;
			LogUtil.w(tag, "---delay:"+delay);
			return (int) delay;
		}catch(Exception e){
			e.printStackTrace();
			return ERROR_FORMAT;
		}
	}
	
	/**
	 * 取到短信内容中的index
	 * */
	private int getIndex(String  smsBody ){
		try{
			String i = smsBody.split("\n")[0].split("=")[1];
			LogUtil.w(tag, "index:"+i);
			return Integer.parseInt( i );
		}catch(Exception e){
			return ERROR_FORMAT;
		}
	}
	
	/**
	 * 短信数据库监控者
	 * 当收件箱变化时读取短信,把短信置为已读
	 * 
	 * */
	private class SMSObserver extends ContentObserver{
		
		public SMSObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean isSelfChange){
			super.onChange( isSelfChange );
			LogUtil.w(tag, "---SMS's DB is changeed" );
			
			if( ! hasRead ){
				//收件箱地址
				Uri uriSMS = Uri.parse( "content://sms/inbox" );
				Cursor c = SMSSendReceiveTest.this.getContentResolver().query(uriSMS,
						new String[]{"_id","address","date","read","body"},
						null, 
						null, 
						"date desc");
				//找到第一条记录
				c.moveToFirst();
				
				//短信状态改为已读取
				ContentValues values = new ContentValues();
				values.put("read", 1);
				SMSSendReceiveTest.this.getContentResolver()
				.update(uriSMS, values, "_id=?", new String[]{""+c.getInt(0 ) } );
				
				hasRead = true;
			}
			
		}
	}
	
	/**
	 * 显示事件
	 * */
	private void displayEvent(String event){
		Message msg = mHandler.obtainMessage(EVENT_CHANGE, event);
		msg.sendToTarget(); 
	}
	
}