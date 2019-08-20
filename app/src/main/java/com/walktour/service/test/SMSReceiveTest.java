package com.walktour.service.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.telephony.SmsMessage;

import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.TotalStruct.TotalAppreciation;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.task.parsedata.model.task.sms.receive.TaskSmsReceiveModel;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

/**
 * @Serivice 短信接收测试
 * @descrition 业务流程参考文档《前台产品测试行为一致性规范－数据分册 V1.1.0.doc》
 *  @author qihang.li@dinglicom.com
 * */
public class SMSReceiveTest extends TestTaskService{
	private static final String tag = "SMSReceiveTest";			
	
	private static final int ERROR_FORMAT = -9999;		//短信的格式错误
	private boolean isInterrupted = false;
	private boolean isWaitingForFinish = false;
	
	//测试模型
	private TaskSmsReceiveModel smsRecModel ;
	private long timeout = 60;//秒
	private int recvCount = 0;
	
	//注册广播接收器
	private void regedit() {
		IntentFilter filter = new IntentFilter();
		filter.addAction( WalkMessage.telephonySMSReceived );
		filter.addAction( WalkMessage.ACTION_UNIT_NORMAL_RECEIVE );
		this.registerReceiver(mEventReceiver, filter);
	}
	
    @Override  
    public void onCreate() {   
        super.onCreate(); 
        isSingleProcess = false;
    	LogUtil.d(tag,"---onCreate");
    	regedit();
    }
    
	
	@Override
	public int onStartCommand(Intent intent,int flags, int startId) {
		LogUtil.i(tag, "onStart");
		
		int startFlag = super.onStartCommand(intent, flags, startId);
		smsRecModel = (TaskSmsReceiveModel) super.taskModel;
		
		timeout=smsRecModel.getTimeOut();
		
		if( smsRecModel.isUnitTest() ){
			sendNormalMessage( MSG_UNIT_SMS_RECV_START );
    	}
		
		//监听短信数据库变化
//		SMSObserver observer = new SMSObserver( new Handler() );
//		this.getContentResolver().registerContentObserver(
//				Uri.parse( "content://sms/" ),//注意这里是"content://sms/"而不是"content://sms/inbox/"
//				true,
//				observer);
		
		return startFlag;
	}
	
	
	@Override  
    public void onDestroy() {   
		this.unregisterReceiver(mEventReceiver);
    	LogUtil.d(tag, "---onDestroy");   
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();   
    }
	
	/**
	 * 统计数据
	 * @param tatalWap TotalWap其中之一
	 * @param value 数值
	 * */
	private void totalData(TotalStruct.TotalAppreciation totalMms, int value){
		HashMap<String,Integer> totalMap =new HashMap<String,Integer>();
		totalMap.put( totalMms.name(), value);
		Message msg = callbackHandler.obtainMessage(CHART_CHANGE , totalMap);
		callbackHandler.sendMessage(msg);
	}
	
	/**
	 * 广播接收器:接收通信过程中的信令
	 * */
	private BroadcastReceiver mEventReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(WalkMessage.telephonySMSReceived)){
	        	LogUtil.w(tag,"-----telephonySMSReceived----");
	        	//此广播先于SMSObserver监控到变化
        		
        		//获取短信
        		Bundle bundle = intent.getExtras();
        		Object[] pdus = (Object[]) bundle.get("pdus");
        		SmsMessage[] msgs = new SmsMessage[pdus.length];//事实上这里通常是一条短信
        		for (int i = 0; i < pdus.length; i++) {
        			msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        		}
        		
        		//解析短信内容,如果是测试短信并没有超时，则报告成功收取
        		String body =  msgs[0].getDisplayMessageBody();
        		
        		//短信有效（格式和发送的一致）
        		if( isBodyEffect(body) ){
        			
        			//短信有效时才算是一次尝试
        			totalData( TotalAppreciation._SMSReceiveTry,1);
        			
        			int index = getIndex( body );
        			int delay =  getDelay( body ) ;
        			LogUtil.w(tag, "---index:"+index);
        			LogUtil.w(tag, "---delay:"+delay);
        			
        			if( delay <= 1000*smsRecModel.getTimeOut() ){//只要判断未超时即可(收和发的关联性要降低)
						//接收标志位
						
						//存储短信接收成功事件(前后台会根据rcu中的index来统计时延 )
						//writeRcuEvent( ,1,(int)delay,index>0?index:repeatTimes );
						EventBytes.Builder(mContext, RcuEventCommand.SMS_Received)
						.addInteger(1)
						.addInteger(delay)
						.addInteger( index )
						.addTguid(guid)
						.writeToRcu(System.currentTimeMillis() * 1000);
						
						//显示短信接收成功
						displayEvent("SMS Receive Success:Delay "+delay+"ms");
						
						//统计
						totalData( TotalAppreciation._SMSReceiveSuccs,1);
						totalData( TotalAppreciation._SMSReceiveDelay,delay);
						
					}else{
						
						//存储接收失败事件
						EventBytes.Builder(mContext, RcuEventCommand.SMS_Received)
						.addInteger(-1)
						.addInteger(delay)
						.addInteger(index)
						.addTguid(guid)
						.writeToRcu(System.currentTimeMillis() * 1000);
						
						//显示接收失败
						displayEvent("SMS Receive Failure");
						
						//统计
						totalData( TotalAppreciation._SMSReceiveSuccs,0);
						totalData( TotalAppreciation._SMSReceiveDelay,delay);
					}
        			
        			recvCount++;
        			if( recvCount==smsRecModel.getRepeat() ){
        				sendCallBackStop();
        			}else{
        				setTaskCurrentTimes( recvCount+1 );
        			}
        			
        		}else{
        			LogUtil.w(tag, "sms body invalid ,is not from SMSSendTest");
        		}
	        }
			
			//来自服务端的常规消息
			else if( intent.getAction().equals(WalkMessage.ACTION_UNIT_NORMAL_RECEIVE) ){
				String msg = intent.getExtras().getString( WalkMessage.KEY_UNIT_MSG );
				LogUtil.i(tag,"receive normal msg:"+msg );
				
				//响应发送方的开始
				if( msg.equals( MSG_UNIT_SMS_SEND_START ) ){
					sendNormalMessage( MSG_UNIT_SMS_RECV_START );
				}
				
				//发送方测试完成,这里不能再无限时等下去
				else if( msg.equals(MSG_UNIT_SMS_SEND_END) ){
					
					new Thread(){
						@Override
						public void run(){
							if( !isWaitingForFinish ){
								isWaitingForFinish = true;
								
								long startTime = System.currentTimeMillis();
								
								while( !isInterrupted && recvCount<smsRecModel.getRepeat() ){
									try {
										Thread.sleep( 1000 );
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									
									//2014.4.9 接收方收到发送完成后等待 超时时间的两倍，
									//timout时间过后还可能有收到delay超出超时的SMS
									LogUtil.e(tag, " sender has stop,wait for timeout(s):"+timeout);
									if( System.currentTimeMillis() - startTime > 2*timeout*1000 ){
										break;
									}
								}
								
								sendCallBackStop();
							}
						}
					}.start();
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
			guid = smsBody.split("\n")[1].split("=")[1];;
			String time = guid.substring(0, 17);
			
			SimpleDateFormat dateFm = new SimpleDateFormat("yyyyMMddHHmmssSSS",Locale.getDefault()); //格式化当前系统日期
			long sendTime = dateFm.parse(time).getTime();
			long delay =  System.currentTimeMillis() - sendTime ;
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
			return Integer.parseInt( i );
		}catch(Exception e){
			return ERROR_FORMAT;
		}
	}
	
	/**
	 * @return 短信内容是否有效
	 */
	private boolean isBodyEffect(String body){
		return body.startsWith("Index=") &&
				body.contains("GUID=")
				&& getDelay( body )!= ERROR_FORMAT
				&& getIndex( body )!= ERROR_FORMAT;
	}
	
	
	/**
	 * 短信数据库监控者
	 * 当收件箱变化时读取短信,把短信置为已读
	 * 
	 * */
//	private class SMSObserver extends ContentObserver{
//		
//		public SMSObserver(Handler handler) {
//			super(handler);
//		}
//
//		@Override
//		public void onChange(boolean isSelfChange){
//			super.onChange( isSelfChange );
//			
//			LogUtil.w(tag, "---SMS's DB is changeed:"+isSelfChange );
//			
////			if( !isSelfChange ){
////				//收件箱地址
////				Uri uriSMS = Uri.parse( "content://sms/inbox" );
////				Cursor c = SMSReceiveTest.this.getContentResolver().query(uriSMS,
////						new String[]{"_id","address","date","read","body"},
////						null, 
////						null, 
////						"date desc");
////				//找到第一条记录
////				c.moveToFirst();
////				
////				//短信状态改为已读取
////				ContentValues values = new ContentValues();
////				values.put("read", 1);
////				SMSReceiveTest.this.getContentResolver()
////				.update(uriSMS, values, "_id=?", new String[]{""+c.getInt(0 ) } );
////			}
//		}
//	}
	
	
	/**
	 * 显示事件
	 * */
	private void displayEvent(String event){
		Message msg = callbackHandler.obtainMessage(EVENT_CHANGE, event);
		msg.sendToTarget(); 
	}
	
    /**
     * [调用停止当前业务接口]<BR>
     * [如果当前为手工停止状态不能调用当前停止接口]
     * @param msg
     */
    private void sendCallBackStop(){
        if(!isInterrupted){
            Message StopMsg = callbackHandler.obtainMessage(TEST_STOP);
            StopMsg.sendToTarget();
        }
    }
	
}