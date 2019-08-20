package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.dingli.service.test.ipc2jni;
import com.google.gson.Gson;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.MobileInfoUtil;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.RcuEventCommand.DropReason;
import com.walktour.Utils.RcuEventCommand.FailReason;
import com.walktour.Utils.TotalStruct.TotalAppreciation;
import com.walktour.Utils.UmpcSwitchMethod;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.UMPCEventType;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskTestObject;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.attach.TaskAttachModel;
import com.walktour.gui.task.parsedata.model.task.dnslookup.TaskDNSLookUpModel;
import com.walktour.gui.task.parsedata.model.task.email.receive.TaskEmailPop3Model;
import com.walktour.gui.task.parsedata.model.task.email.send.TaskEmailSmtpModel;
import com.walktour.gui.task.parsedata.model.task.facebook.TaskFaceBookModel;
import com.walktour.gui.task.parsedata.model.task.ftp.TaskFtpModel;
import com.walktour.gui.task.parsedata.model.task.http.page.TaskHttpPageModel;
import com.walktour.gui.task.parsedata.model.task.http.upload.TaskHttpUploadModel;
import com.walktour.gui.task.parsedata.model.task.iperf.TaskIperfModel;
import com.walktour.gui.task.parsedata.model.task.mms.receive.TaskMmsReceiveModel;
import com.walktour.gui.task.parsedata.model.task.mms.send.TaskMmsSendModel;
import com.walktour.gui.task.parsedata.model.task.mms.sendreceive.TaskMmsSendReceiveModel;
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;
import com.walktour.gui.task.parsedata.model.task.mtc.TaskPassivityCallModel;
import com.walktour.gui.task.parsedata.model.task.multiftp.download.TaskMultiftpDownloadModel;
import com.walktour.gui.task.parsedata.model.task.multiftp.upload.TaskMultiftpUploadModel;
import com.walktour.gui.task.parsedata.model.task.multihttp.download.TaskMultiHttpDownModel;
import com.walktour.gui.task.parsedata.model.task.opensignal.TaskOpenSignalModel;
import com.walktour.gui.task.parsedata.model.task.ott.TaskMultipleAppTestModel;
import com.walktour.gui.task.parsedata.model.task.ott.TaskWeCallModel;
import com.walktour.gui.task.parsedata.model.task.pbm.TaskPBMModel;
import com.walktour.gui.task.parsedata.model.task.pdpactive.TaskPdpModel;
import com.walktour.gui.task.parsedata.model.task.ping.TaskPingModel;
import com.walktour.gui.task.parsedata.model.task.reboot.TaskRebootModel;
import com.walktour.gui.task.parsedata.model.task.sms.receive.TaskSmsReceiveModel;
import com.walktour.gui.task.parsedata.model.task.sms.send.TaskSmsSendModel;
import com.walktour.gui.task.parsedata.model.task.sms.sendreceive.TaskSmsSendReceiveModel;
import com.walktour.gui.task.parsedata.model.task.speedtest.TaskSpeedTestModel;
import com.walktour.gui.task.parsedata.model.task.traceroute.TaskTraceRouteModel;
import com.walktour.gui.task.parsedata.model.task.udp.TaskUDPModel;
import com.walktour.gui.task.parsedata.model.task.videoplay.TaskVideoPlayModel;
import com.walktour.gui.task.parsedata.model.task.videostreaming.TaskStreamModel;
import com.walktour.gui.task.parsedata.model.task.wap.TaskWapPageModel;
import com.walktour.gui.task.parsedata.model.task.wechat.TaskWeChatModel;
import com.walktour.gui.task.parsedata.model.task.weibo.TaskWeiBoModel;
import com.walktour.gui.task.parsedata.model.task.wlan.login.TaskWlanLoginModel;
import com.walktour.model.TotalSpecialModel;
import com.walktour.model.UmpcTestInfo;
import com.walktour.service.ICallback;
import com.walktour.service.IService;
import com.walktour.service.SamsungService;
import com.walktour.service.TestService;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TestTaskService
 * 所有测试业务继承此类
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
@SuppressLint("HandlerLeak")
public abstract class TestTaskService extends Service{
	protected static String tag ="TestTaskService";
	protected Context mContext;
	
	/**
	 * 是否独立进程
	 */
	protected boolean isSingleProcess = true;
	
	/**无效值*/
	protected final static int INVALID_VALUE = 0;
	
	/**
	 * TestService到业务的Intent包中的KEYPDP时延
	 */
	public static final String KEY_PDP_DELAY = "pdp_delay";
	public static final String KEY_PING_DELAY= "ping_delay";
	
	/**外循环同步控制*/
	public final static String MSG_UNIT_SYNC_Circle		= "walktour_sync_Circle:";
	/**并发业务第次开始同步控制*/
	public final static String MSG_UNIT_SYNC_Parallel	= "walktour_sync_Parallel:";
	/**主被叫联合测试主叫准备就绪的消息，由主叫发给被叫*/
	public final static String MSG_MOC_READY ="walktour_sync_ResendMTState";
	/**主被叫联合测试被叫准备就绪的消息，被叫收到MSG_MOC_READY后向主叫发送*/
	public final static String MSG_MTC_READY ="walktour_sync_MtReady";
	/**主叫在写入开始测试事件之前向被叫请求UUID*/
	public final static String MSG_MOC_GETUUIDFROMMTC = "walktour_sync_getuuidfrommtc";
	/**被叫收到主叫的请求后，发送UUID到主要端*/
	public final static String MSG_MTC_SENDUUID2MOC = "walktour_sync_mtcsenduuid2moc:";
	/**主被叫联合测试的主叫完成一次呼叫后挂机的消息，主叫发给被叫*/
	public final static String MSG_UNIT_MOC_HANGUP ="walktour_sync_MocHungup";
	/**主被叫联合测试的被叫收到主叫挂机命令后的响应*/
	public final static String MSG_UNIT_MOC_HANGUP_ACK ="walktour_sync_MocHungupAck";
	/**主被叫联合测试MOC当前任务结束或者当前非MOC任务消息，主叫发给被叫*/
	public final static String MSG_UNIT_NOT_MOC_TASK ="walktour_sync_MocTaskFinished";
	///**主被叫联合测试:被叫接通消息,被叫发给主叫，只有主叫为C网才使用此消息*/
	//public final static String MSG_UNIT_MTC_CONNECTED ="walktour_sync_MtcConnected";
	
	/**
	 * 联合测试：短信彩信发送方告知接收方业务开始(仅第1次开始时使用)
	 */
	public final static String MSG_UNIT_SMS_SEND_START = "walktour_sync_sms_send_start";
	/**
	 * 联合测试：短信彩信接收方告知发送方业务开始(仅第1次开始时使用)
	 */
	public final static String MSG_UNIT_SMS_RECV_START = "walktour_sync_sms_recv_start";
	/**
	 * 联合测试：短信彩信发送方告知接收方业务结束(完成最后一次时使用)
	 */
	public final static String MSG_UNIT_SMS_SEND_END = "walktour_sync_sms_send_end";
	/**
	 * 联合测试：短信彩信接收方告知发送方业务结束(完成最后一次时使用)
	 */
	public final static String MSG_UNIT_SMS_RECV_EDN = "walktour_sync_sms_recv_end";
	
	/**
	 * 联合测试：短信彩信发送方告知接收方业务开始(仅第1次开始时使用)
	 */
	public final static String MSG_UNIT_MMS_SEND_START = "walktour_sync_MMS_send_start";
	/**
	 * 联合测试：短信彩信接收方告知发送方业务开始(仅第1次开始时使用)
	 */
	public final static String MSG_UNIT_MMS_RECV_START = "walktour_sync_MMS_recv_start";
	/**
	 * 联合测试：短信彩信发送方告知接收方业务结束(完成最后一次时使用)
	 */
	public final static String MSG_UNIT_MMS_SEND_END = "walktour_sync_MMS_send_end";
	/**
	 * 联合测试：短信彩信接收方告知发送方业务结束(完成最后一次时使用)
	 */
	public final static String MSG_UNIT_MMS_RECV_EDN = "walktour_sync_MMS_recv_end";
	
	
	//以下两个在iPhone里没有用到
	/**主被叫联合测试的主叫未接通消息,主叫向被叫发送*/
	public final static String MSG_UNIT_CALL_BLOCK ="walktour.united.call.block";
	/**主被叫联合测试的主叫掉话消息,主叫被叫发送*/
	public final static String MSG_UNIT_CALL_DROP ="walktour.united.call.drop";
	
	
	//回放同步控制消息关键字
	/**回放过程中的位置同步消息,冒号后面跟着的是主控端的当前采样点时间,需要用该时间转换为当前设备中对应时间的采样点*/
	public final static String MSG_PLAYBACK_INDEXTIMES_SYNC	= "walktour_sync_playback_indextime:";
	/**回放开始时的文件同步时间，冒号后面跟的为当前选中文件的创建时间，接受端如果当前文件为空则去列表中比对创建时间最近的文件做为选中文件，然后响应开始，暂停回放*/
	public final static String MSG_PLAYBACK_FILE_START		= "walktour_sync_playback_file:";
	/**回放过程中文件重选择*/
	public final static String MSG_PLAYBACK_RESELECT_FILE	= "walktour_sync_playback_reselect_file";
	/**回放方向,冒号后面跟的是改变方向*/
	public final static String MSG_PLAYBACK_INORDOR			= "walktour_sync_playback_inorder:";
	/**设置当前播放速度*/
	public final static String MSG_PLAYBACK_SPEED			= "walktour_sync_playback_speed:";
	/**退出回放*/
	public final static String MSG_PLAYBACK_CLOSE			= "walktour_sync_playback_close";
	
	/**主被叫联合测试的MOS同步消息,由被叫向主叫发送*/
	public final static String MSG_UNIT_MOS_START ="walktour.united.test.mos.start";
	/**主被叫联合测试的MOS同步消息,由被叫向主叫发送后，主叫收到时向被叫回应*/
	public final static String MSG_UNIT_MOS_START_ACK ="walktour.united.test.mos.start.ack";
	/**算分完成后,主叫向被叫发送*/
	public final static String MSG_UNIT_MOS_STOP ="walktour.united.test.mos.stop";
	/**算分完成后,主叫向被叫发送，被叫收到后的回应*/
	public final static String MSG_UNIT_MOS_STOP_ACK ="walktour.united.test.mos.stop.ack";
	/**录音的间隔时间*/
	public static final int MOS_TIME_INTERVAL	= 10 * 1000;
	
	protected static final int EVENT_CHANGE 	= 1;
	protected static final int CHART_CHANGE 	= 2;
	protected static final int DATA_CHANGE 		= 3;
	protected static final int TEST_STOP 		= 4;
	protected static final int REAL_PARA 		= 5;
	protected static final int GET_NETWORK_TYPE = 6;
	/**访问主进程消息，目前用于开始时获得主进程的网络参数*/
	protected static final int CALL_MAINPROCESS	= 7;
	protected static final int MONITOR_NET_TYPE	= 8;

	//协议类型   1:WAP1.0	2:WAP2.0	3:HTTP	（注WAP一般使用WAP2.0）
	public  static final int PROTOL_TYPE_WAP10 = 1; 
	public  static final int PROTOL_TYPE_WAP20 = 2; 
	public  static final int PROTOL_TYPE_HTTP = 3; 
	
	//业务开始后的定时器
	protected Timer timer;
	protected TimerTask timerTask;
	
	//数据业务相关的参数
	protected TaskModel taskModel;
	
	/**收到的数据大小(byte)*/
	protected long toPioneerTransmitByte = INVALID_VALUE;		
	/**当前实时速率 (bps)*/
	protected int toPioneerInstRate = INVALID_VALUE;
	
	/**当前是第几次测试*/
	protected int repeatTimes = 0;	
	/** 当前是否小背包 */
//	protected boolean isUmpcTest = false;
	/**是否被人工终止*/
	protected boolean isInterrupted = false;
	
	/**
	 * 是否已经成功注册回调
	 */
	protected boolean hasRegeditCallback = false;
	
	/**下载测试用到的临时目录*/
	protected String downloadPath = "";
	
	protected String forPioneerStr = "";

	/**是否使用root启动业务库*/
    protected boolean useRoot = false;
    
    /**s6业务绑定时,有不执行startCommond的情况,此为业务调度状态控制,执行了为true*/
    protected boolean startCommondRun = false;
    
    /**G9300 出现绑定不上后业务不能正常退出,即无onDestroy动作,添加监控该动作线程``*/
    protected boolean hasOnDestroy 		= false;
    protected int waitDestroyTime 			= 0;
    /**监控间隔 50 ms*/
	protected final int MonitoryInterval 	= 50;
	/**监控超时3000 ms*/
	protected final int MonitoryTimeout	= 3000;
	
	
    protected String guid = null;
    
    /**开始测试时，存放当前网络名字，用于统计区分,GSM，WCDMA
	 * 根据当前网类型，存放当前网络状态,如TBF-OPEN/CLOSE
	 */
    protected TotalSpecialModel totalStateModel;
	

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	@Override
	public void onCreate(){
        super.onCreate();
        LogUtil.i(tag, "onCreate");
        mContext = this;
        downloadPath = AppFilePathUtil.getInstance().getSDCardBaseDirectory("temp","test_down");

		if(Deviceinfo.getInstance().isCustomS9()){
			SamsungService.getInStance(mContext);
		}

	}
	
	@Override
	public int onStartCommand(Intent intent,int flags, int startId) {
		LogUtil.i(tag,"onStartCommand");
		if(intent != null){
			taskModel = getTaskModel(intent);
			//正常业务测试
			repeatTimes = intent.getIntExtra(WalkCommonPara.testRepeatTimes, 0);
			if (!ApplicationModel.getInstance().isGeneralMode()
					&& (ConfigRoutine.getInstance().getNetInterface(this) == ConfigRoutine.NET_INTERFACE_BIND)) {
				useRoot = Deviceinfo.getInstance().isUseRoot();
			}

			if(Deviceinfo.getInstance().isS8()) {
			    if(MobileInfoUtil.isDeviceRooted()) {
			    	LogUtil.d(tag,"getTypeProperty() ="+taskModel.getTypeProperty());
                    if (taskModel.getTypeProperty() == WalkCommonPara.TypeProperty_Ppp) {//NB拨号
                       useRoot = true;
                    }
                }
            }
            LogUtil.w(tag,"useRoot ="+useRoot);
			forPioneerStr = intent.getStringExtra(WalkMessage.TestInfoForPioneer);
			forPioneerStr = (forPioneerStr==null) ? "" : forPioneerStr; 
			
			startCommondRun = true;
		}else{
			//事实上下面先
			Message msg = callbackHandler.obtainMessage( TEST_STOP ,TestService.RESULT_BUNDLE_NULL );
			msg.sendToTarget();
		}
		LogUtil.i(tag, "--"+forPioneerStr );
		//服务异常退出后也不重启
		return START_NOT_STICKY; 
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		hasOnDestroy = true;
		if(Deviceinfo.getInstance().isCustomS9()){
			SamsungService.getInStance(mContext).release();
		}
		LogUtil.i(tag,"---onDestroy handle:" + (dataTestHandler!=null) + "--pid:" + android.os.Process.myPid());
		if( timer!= null && timerTask!= null){
    		timer.cancel();
    		timerTask.cancel();
    		timer = null;
    		timerTask = null;
    	}
		stopPioneerTimer();
		
		if( dataTestHandler!=null ){
			dataTestHandler.uninitJniServer();
		}
		
		if (isSingleProcess){
			//此命令只会杀死符合本用户号的进程
			android.os.Process.sendSignal( android.os.Process.myPid(),android.os.Process.SIGNAL_KILL );
		}
		
	}
	
	protected IService.Stub binder = new IService.Stub() {   
	    
	    public void unregisterCallback(ICallback cb){
	    	if(cb != null) {   
	        	callbacks.unregister(cb);  
	            hasRegeditCallback = false;
	        }   
	    }   
	
	    public void registerCallback(ICallback cb){
	    	LogUtil.w(tag, "--registerCallback--");
	    	if(cb != null) {
	        	callbacks.register(cb);   
	            hasRegeditCallback = true;
	            LogUtil.i(tag, "regeditCllback");
	        }   
	    }
		
		public void stopTask(boolean isTestInterrupt,int dropReason) throws RemoteException {
			LogUtil.i(tag, String.format( "===stopTask,isInterrupt:%b,reason:%d" ,isTestInterrupt,dropReason ) );
			if( isTestInterrupt ){
				isInterrupted = true;
			}
			
			if( dataTestHandler !=null ){
				if( isInterrupted ){
					dataTestHandler.sendStopCommand();
				}else if( dropReason== DropReason.PPP_DROP.getReasonCode()
						|| dropReason == DropReason.OutOfService.getReasonCode() ){
					showEvent( DropReason.getDropReason(dropReason).getResonStr() );
				}
				dataTestHandler.fail( isInterrupted ?
						FailReason.USER_STOP.getReasonCode():
							dropReason == RcuEventCommand.DROP_NETWORK_NO_MATCH  ? 
									FailReason.NETWORK_NO_MATCH.getReasonCode() : FailReason.UNKNOWN.getReasonCode(),
							System.currentTimeMillis() * 1000 );
				dataTestHandler.drop( dropReason,System.currentTimeMillis() * 1000 );
				//dataTestHandler.disConnect();
			}
			
			new Thread(new MonitorOnDestroy()).start();
		}
		
		/**
		 * 返回是否执行startCommand状态,
		 * 如果改状态需要在业务中出现某种情况时才为真,
		 * 可以在继承的业务中改写该状态*/
		public boolean getRunState(){
			return startCommondRun;
		}
	};

	/**
	 * 当测试结束之后,监控是否释放当前服务,如无强行释放
	 * @author Tangwq
	 *
	 */
	class MonitorOnDestroy implements Runnable{
		@Override
		public void run() {
			LogUtil.w(tag, "----MonitorOnDestroy----start----");
			while(!hasOnDestroy && waitDestroyTime < MonitoryTimeout){
				waitDestroyTime += MonitoryInterval;
				UtilsMethod.ThreadSleep(MonitoryInterval);
			}
			LogUtil.w(tag, "----MonitorOnDestroy----end----");
			if(!hasOnDestroy){
				stopSelf();
			}
		}
	}
	    
	//注册回调接口
	protected RemoteCallbackList<ICallback> callbacks = new RemoteCallbackList<ICallback>();
	/** mHandler: 调用回调函数*/
	protected MyHandler callbackHandler = new MyHandler(this);
	public static class MyHandler extends Handler{
		private WeakReference<TestTaskService> reference;
		public MyHandler(TestTaskService service){
			this.reference = new WeakReference<>(service);
		}
		@Override
		public void handleMessage(Message msg){
			TestTaskService service = this.reference.get();
			super.handleMessage(msg);
			
			while( !service.hasRegeditCallback );
			resultCallBack(msg);
		}
		
		//call back
		@SuppressLint("HandlerLeak") @SuppressWarnings({ "unchecked", "rawtypes" })
		private void resultCallBack(Message msg) {
			TestTaskService service = this.reference.get();
			
	    	int N = service.callbacks.beginBroadcast();
	        try {   
	            for (int i = 0; i < N; i++) {
	            	switch(msg.what){
	            	case EVENT_CHANGE:
	            		service.callbacks.getBroadcastItem(i).OnEventChange(  msg.obj.toString() );
	            		break;
	            	case CHART_CHANGE:
	            		service.callbacks.getBroadcastItem(i).onChartDataChanged((Map)msg.obj);
	            		break;
	            	case DATA_CHANGE:
            	    Map tempMap = (Map) msg.obj;
                  tempMap.put(TaskTestObject.stopResultName, service.taskModel.getTaskName());
                  service.callbacks.getBroadcastItem(i).OnDataChanged(tempMap);
	            		break;
	            	case TEST_STOP:
	            		Map<String,String> resultMap = TaskTestObject.getStopResultMap(service.taskModel);
	            		LogUtil.d(tag, "resultMap="+resultMap.toString());
  	        			resultMap.put(TaskTestObject.stopResultState, (String)msg.obj);
              		LogUtil.d(tag, "resultMap="+resultMap.toString());
  	        			service.callbacks.getBroadcastItem(i).onCallTestStop(resultMap);
	            		break;
	            	case REAL_PARA:
	            		try{
	            			Map paraMap = (Map)msg.obj;
	                		if(paraMap.get(WalkCommonPara.CALL_BACK_TYPE_KEY) != null){
	                			int paraType = (Integer) paraMap.get(WalkCommonPara.CALL_BACK_TYPE_KEY);
	                			service.callbacks.getBroadcastItem(i).onParaChanged(paraType, paraMap);
	                		}
                		}catch(Exception e){
                			e.printStackTrace();
                		}
	            		break;
	            	case GET_NETWORK_TYPE:
						try {
							boolean isPBM =  msg.obj==null?false:(Boolean)msg.obj ;
							int netWorkType = service.callbacks.getBroadcastItem(i).getNetWorkType(isPBM);
							if (netWorkType > 0)
								service.dataTestHandler.netWorkType = netWorkType;
						} catch (Exception e) {
							e.printStackTrace();
						}
	            		break;
	            	case CALL_MAINPROCESS:
	            		if(msg.arg1 == WalkCommonPara.CallMainType_GetNetStat_ByPingStart){
		            		HashMap<String, TotalSpecialModel> totalState = (HashMap<String, TotalSpecialModel>)
		            				service.callbacks.getBroadcastItem(i).callMainProcess(WalkCommonPara.CallMainType_GetNetStat_ByPingStart);
		            	    if(totalState != null && totalState.get(WalkCommonPara.CallMainResultKey) != null){
		            	    	service.totalStateModel = totalState.get(WalkCommonPara.CallMainResultKey);
		                    }else{
		                    	service.totalStateModel = new TotalSpecialModel("Other", "Other");
		                    }
	            		}else if(msg.arg1 == WalkCommonPara.CallMainType_Do_Attach){
	            			service.callbacks.getBroadcastItem(i).callMainProcess(WalkCommonPara.CallMainType_Do_Attach);
	            		}else if(msg.arg1 == WalkCommonPara.CallMainType_Do_Detach){
	            			service.callbacks.getBroadcastItem(i).callMainProcess(WalkCommonPara.CallMainType_Do_Detach);
	            		}
	            		break;
	            	}
	            }   
	        } catch (RemoteException e) {   
	            LogUtil.w(tag, "callbackHandler", e);
	        }   
	        service.callbacks.finishBroadcast();   
	    } 
	};//end mHandler

	/***
	 * 显示通知
	 * @param tickerText 通知显示的内容
	 * @param strBroadcast 点通知后要发的广播
	 */
    @SuppressWarnings("deprecation")
		protected void showNotification(String tickerText,String strBroadcast) {  
    	//生成通知管理器
    	NotificationManager  mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // notification //通知图标, 状态栏显示的通知文本提示,通知产生的时间
		Notification.Builder notification = new Notification.Builder(this);
		notification.setTicker(tickerText);
		notification.setSmallIcon(R.mipmap.walktour);
		notification.setWhen(System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getBroadcast(TestTaskService.this,
        		0,new Intent( (strBroadcast==null)?"":strBroadcast ), 0); 
        // must set this for content view, or will throw a exception
        //如果想要更新一个通知，只需要在设置好notification之后，再次调用 setLatestEventInfo(),然后重新发送一次通知即可，即再次调用notify()。
		notification.setAutoCancel(true);
		notification.setContentIntent(contentIntent);
		notification.setContentTitle(getString(R.string.sys_alarm));
		notification.setContentText(tickerText);
        mNotificationManager.notify(R.string.service_started, notification.build());
    }
    
	/**显示通知并跳到指定的地方*/
    @SuppressWarnings("deprecation")
		protected void showNotification(String tickerText,Class<?> cls) {  
    	//生成通知管理器
    	NotificationManager  mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // notification //通知图标, 状态栏显示的通知文本提示,通知产生的时间
		Notification.Builder notification = new Notification.Builder(this);
		notification.setTicker(tickerText);
		notification.setSmallIcon(R.mipmap.walktour);
		notification.setWhen(System.currentTimeMillis());
        //Intent 点击该通知后要跳转的Activity
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,new Intent(this,(cls != null ? cls : TestService.class)),0);  
        // must set this for content view, or will throw a exception
        //如果想要更新一个通知，只需要在设置好notification之后，再次调用 setLatestEventInfo(),然后重新发送一次通知即可，即再次调用notify()。
		notification.setAutoCancel(true);
		notification.setContentIntent(contentIntent);
		notification.setContentTitle(getString(R.string.sys_alarm));
		notification.setContentText(tickerText);
        mNotificationManager.notify(R.string.service_started, notification.build());
    }

	/**
	 * 写入TAG事件
	 * @param lableTime
	 * @param eventStr
	 */
	protected void addTagEvent(long lableTime, String eventStr) {
		short len = 0;
		try {
			len = (short) eventStr.getBytes(UtilsMethod.CharSet_UTF_8).length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		EventBytes.Builder(this, RcuEventCommand.TAG_EVENT).addShort(len).addCharArray(eventStr)
				.writeToRcu(lableTime * 1000);
	}
    
    /** 写入RCU事件
     * @param time 事件时间(微秒)
     * @param flags 原因码
     */
	protected void writeRcuEvent(int eventFlag, long time,int... flags) {
		EventBytes eb = EventBytes.Builder(this, eventFlag);
		for(int flag:flags){
			eb.addInteger(flag);
		}
		eb.writeToRcu(time);
	}
	
    /**
     * 以自由组合字节的方式 写入RCU事件
     * @event 事件flag
     * @arg 参数
     * @arg2 参数2
	 * @param time 事件时间(微秒)
     * */
    protected void writeRcuEventWithByte(int event,long time ,short arg1,int... flags){
    	EventBytes eb = EventBytes.Builder(this, event).addShort(arg1);
  		for(int flag:flags){
  			eb.addInteger(flag);
  		}
    	eb.writeToRcu(time);
	}
    
    /**
     * 获得当前SIM卡的手机号码
     * @return
     */
    protected String getSimNumber() {
		TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getLine1Number() == null ? "" : tm.getLine1Number();
	} 
    
    /**发送常规消息到服务端（ipad/umpc）*/
    protected void sendNormalMessage(String message){
    	LogUtil.w(tag, "send normal msg:"+message);
    	Intent intent = new Intent();
    	intent.setAction( WalkMessage.ACTION_UNIT_NORMAL_SEND);
    	intent.putExtra(WalkMessage.KEY_UNIT_MSG,message);
    	sendBroadcast( intent );
    }
    
    /**
     * 小背包MOS盒测试开始结束信息
     * @param type		MOMT 主叫被叫（MO/MT）
     * @param state		CallStatus 接通状态（0：挂机；1：接通）
     * @param moType	MosType mos类型（POLQA/PESQ）
     * @param simple	Sample 采样率(8000/16000/48000)
     */
    protected void sendMosBoxTest(String action,String type,int state,String moType,int simple){
    	Intent intent = new Intent(action);
    	intent.putExtra(WalkMessage.KEY_MOS_BOX_TEST_TYPE,type);
    	intent.putExtra(WalkMessage.KEY_MOS_BOX_TEST_STATE, state);
    	intent.putExtra(WalkMessage.KEY_MOS_BOX_TEST_MOSTYPE, moType);
    	intent.putExtra(WalkMessage.KEY_MOS_BOX_TEST_SAMPLE, simple);
    	
    	sendBroadcast(intent);
    }
    
    protected String getDropReasonString(int dropReason){
		switch( dropReason ){
		case RcuEventCommand.DROP_NORMAL:return "Normal";
		case RcuEventCommand.DROP_TIMEOUT:return "Timeout";
		case RcuEventCommand.DROP_USERSTOP:return "User Stop";
		case RcuEventCommand.DROP_PPPDROP:return "PPP Drop";
		case RcuEventCommand.DROP_NODATA:return "No Data";
		case RcuEventCommand.DROP_OUT_OF_SERVICE:return "Out of Service";
		default :return "Unkown";
		}
    }
    
    /**
	 * 显示事件,前面带有序号
	 * @param event 
	 * */
	protected void showEvent(String event){
		Message msg = callbackHandler.obtainMessage(EVENT_CHANGE, repeatTimes+"-"+event);
		msg.sendToTarget();
		event=null;
	}
    
	/**
	 * twq20131028
	 * 当收到firstdata状态时，回调主进程，设置当前为firstdata状态，用于有数据业务类型的统计
	 * 当时lastdata及测试结束时，调用该方法，将firstdata状态置为false
	 * @param state
	 */
	protected void setMainFirstDataState(boolean state){
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put(WalkCommonPara.CALL_BACK_TYPE_KEY, WalkCommonPara.CALL_BACK_SET_FIRSTDATA_STATE);
		paramMap.put(WalkCommonPara.CALL_BACK_FIRSTDATE_STATE_KEY,state);
		callbackHandler.obtainMessage(REAL_PARA,paramMap).sendToTarget();
	}

	/***
	 * 设置主进程中测试业务是上行还是下行,0--下行 1--上行
	 * @param direct
	 */
	protected  void setMainBussinessDirectType(int direct){
			Intent normal = new Intent(WalkMessage.ACTION_VALUE_BUSINESS_DIRECT_TYPE);
			normal.putExtra("directType", direct);
			sendBroadcast(normal);
	}
	/**
	 * 当前任务仅启动一次,执行多次测试任务时
	 * 调用当前方法通知主进程当前测试次数
	 * @param times
	 */
	protected void setTaskCurrentTimes(int times){
		try{
			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put(WalkCommonPara.CALL_BACK_TYPE_KEY, WalkCommonPara.CALL_BACK_SET_TESTTIMES);
			paramMap.put(WalkCommonPara.CALL_BACK_SET_TEST_TIMES_KEY,times);
			callbackHandler.obtainMessage(REAL_PARA,paramMap).sendToTarget();
		}catch(Exception e){
			LogUtil.w(tag,"setTaskCurrentTimes",e);
		}
	}
	
    /**
	 * 获取当前的测试对象
	 * @param intent	启动Intent
	 * @return	当前的测试对象
	 */
	protected TaskModel getTaskModel(Intent intent) {
		LogUtil.i(tag,"-----------getTaskModel------------");
		Bundle bundle = intent.getExtras();
		Object obj = null;
		if(bundle.containsKey(WalkCommonPara.testModelKey)){
			obj = bundle.getSerializable(WalkCommonPara.testModelKey);
		}
		if(obj == null){
			LogUtil.w(tag, "data_key is null");
			if(bundle.containsKey(WalkCommonPara.testModelJsonKey) && bundle.containsKey(WalkCommonPara.testModelJsonTypeKey)){
				String jsonStr = bundle.getString(WalkCommonPara.testModelJsonKey);
				int testType = bundle.getInt(WalkCommonPara.testModelJsonTypeKey);
				LogUtil.d(tag,"---jsonStr:" + jsonStr + "---");
				LogUtil.d(tag,"---testType:" + testType + "---");
				TaskModel taskModel = null;
				if(!TextUtils.isEmpty(jsonStr)){
					if(testType == WalkStruct.TaskTypeIDs.InitiativeCall){
						taskModel = new Gson().fromJson(jsonStr,TaskInitiativeCallModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.PassivityCall){
						taskModel = new Gson().fromJson(jsonStr,TaskPassivityCallModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.PBM){
						taskModel = new Gson().fromJson(jsonStr,TaskPBMModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.Ping){
						taskModel = new Gson().fromJson(jsonStr,TaskPingModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.Attach){
						taskModel = new Gson().fromJson(jsonStr,TaskAttachModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.PDP){
						taskModel = new Gson().fromJson(jsonStr,TaskPdpModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.FTPUpload){
						taskModel = new Gson().fromJson(jsonStr, TaskFtpModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.FTPDownload){
						taskModel = new Gson().fromJson(jsonStr, TaskFtpModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.MultiftpUpload){
						taskModel = new Gson().fromJson(jsonStr, TaskMultiftpUploadModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.MultiftpDownload){
						taskModel = new Gson().fromJson(jsonStr, TaskMultiftpDownloadModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.EmailPop3){
						taskModel = new Gson().fromJson(jsonStr, TaskEmailPop3Model.class);
					}else if(testType == WalkStruct.TaskTypeIDs.EmailSmtp){
						taskModel = new Gson().fromJson(jsonStr, TaskEmailSmtpModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.SMSIncept){
						taskModel = new Gson().fromJson(jsonStr, TaskSmsReceiveModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.SMSSend){
						taskModel = new Gson().fromJson(jsonStr, TaskSmsSendModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.SMSSendReceive){
						taskModel = new Gson().fromJson(jsonStr, TaskSmsSendReceiveModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.MMSIncept){
						taskModel = new Gson().fromJson(jsonStr, TaskMmsReceiveModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.MMSSend){
						taskModel = new Gson().fromJson(jsonStr, TaskMmsSendModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.MMSSendReceive){
						taskModel = new Gson().fromJson(jsonStr, TaskMmsSendReceiveModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.WapRefurbish){
						taskModel = new Gson().fromJson(jsonStr, TaskWapPageModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.WapLogin){
						taskModel = new Gson().fromJson(jsonStr, TaskWapPageModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.WapDownload){
						taskModel = new Gson().fromJson(jsonStr, TaskWapPageModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.Http){
						taskModel = new Gson().fromJson(jsonStr, TaskHttpPageModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.HttpRefurbish){
						taskModel = new Gson().fromJson(jsonStr, TaskHttpPageModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.HttpDownload){
						taskModel = new Gson().fromJson(jsonStr, TaskHttpPageModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.WlanLogin){
						taskModel = new Gson().fromJson(jsonStr, TaskWlanLoginModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.Stream){
						taskModel = new Gson().fromJson(jsonStr, TaskStreamModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.DNSLookUp){
						taskModel = new Gson().fromJson(jsonStr, TaskDNSLookUpModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.SpeedTest){
						taskModel = new Gson().fromJson(jsonStr, TaskSpeedTestModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.HttpUpload){
						taskModel = new Gson().fromJson(jsonStr, TaskHttpUploadModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.HTTPVS){
						taskModel = new Gson().fromJson(jsonStr, TaskVideoPlayModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.Facebook){
						taskModel = new Gson().fromJson(jsonStr, TaskFaceBookModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.TraceRoute){
						taskModel = new Gson().fromJson(jsonStr, TaskTraceRouteModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.Iperf){
						taskModel = new Gson().fromJson(jsonStr, TaskIperfModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.WeiBo){
						taskModel = new Gson().fromJson(jsonStr, TaskWeiBoModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.UDP){
						taskModel = new Gson().fromJson(jsonStr, TaskUDPModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.REBOOT){
						taskModel = new Gson().fromJson(jsonStr, TaskRebootModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.OPENSIGNAL){
						taskModel = new Gson().fromJson(jsonStr, TaskOpenSignalModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.MULTIHTTPDOWNLOAD){
						taskModel = new Gson().fromJson(jsonStr, TaskMultiHttpDownModel.class);
					}else if(testType == WalkStruct.TaskTypeIDs.QQ
                            || testType == WalkStruct.TaskTypeIDs.WeChat
                            || testType == WalkStruct.TaskTypeIDs.SkypeChat
                            || testType == WalkStruct.TaskTypeIDs.WhatsAppChat
                            ||testType == WalkStruct.TaskTypeIDs.WhatsAppMoc
                            || testType == WalkStruct.TaskTypeIDs.WhatsAppMtc
                            || testType == WalkStruct.TaskTypeIDs.SinaWeibo
                            || testType == WalkStruct.TaskTypeIDs.Facebook_Ott
                            || testType == WalkStruct.TaskTypeIDs.Instagram_Ott){
						taskModel = new Gson().fromJson(jsonStr, TaskMultipleAppTestModel.class);
					}else if (testType == WalkStruct.TaskTypeIDs.WeCallMoc
                            || testType == WalkStruct.TaskTypeIDs.WeCallMtc){
                        taskModel = new Gson().fromJson(jsonStr, TaskWeCallModel.class);
                    }
				}
				obj = taskModel;
			}else{
				return null;
			}
		}
		return (TaskModel)obj;
	}
    
	protected DataTestHandler dataTestHandler = null  ;
	
    /**按Pioneer需求，在相关时间点往Pioneer发送相关信息*/
    protected void sendMsgToPioneer(String detailStr){
        if(forPioneerStr != null && !forPioneerStr.equals("")){
        	LogUtil.d(tag, forPioneerStr + detailStr);
            UmpcSwitchMethod.sendEventToController(getApplicationContext(), UMPCEventType.RealTimeData.getUMPCEvnetType(),
                    forPioneerStr + detailStr,UmpcTestInfo.ControlForPioneer);
        }
    }
    
    /**
     * 按Pioneer需求，在相关时间点往Pioneer发送相关信息
     * @param durationTime 测试时长（毫秒），-999为不写入该值
     * @param fileSize 文件大小(byte)，-999为不写入该值
     * @param transmitSize 传输大小（byte） ,-999为不写入该值
     * @param instRate 当前速率 (bps)，-999为不写入该值
     */
    private void sendMsgToPioneer(int durationTime,long fileSize,long transmitSize,int instRate){
    	if(forPioneerStr != null && !forPioneerStr.equals("")){
    		
    		String append = "";
    		append += (durationTime==INVALID_VALUE) ? "":String.format( ",DurationTime=%d",durationTime);
    		append += (fileSize==INVALID_VALUE) ? "":String.format( ",FileSize=%d",fileSize);
    		append += (transmitSize==INVALID_VALUE) ? "":String.format( ",transmitSize=%d",transmitSize);
    		append += (instRate==INVALID_VALUE) ? "":String.format( ",InstRate=%d",instRate);
    		
    		String result = forPioneerStr+append;
    		LogUtil.i(tag, result);
    		
    		UmpcSwitchMethod.sendEventToController(getApplicationContext(), 
    				UMPCEventType.RealTimeData.getUMPCEvnetType(),
    				 result,UmpcTestInfo.ControlForPioneer
    		);
    	}
    }
    
    /**
     * 业务开始时，
     * 定时每秒向Pioneer发送测试实时信息(数据业务通常是从first_data开始)
     * @param fileSize 文件大小，部分业务开始请求时就会或得此大小，如Email，
     * 部分业务未下载完成时无法知道,如wap 刷新
     * */
    protected void startPioneerTimer(final long fileSize){
    	if(forPioneerStr != null && !forPioneerStr.equals("")){
    		stopPioneerTimer();
    		
    		timer = new Timer();
    		timerTask = new TimerTask(){
    			private int durationTime = 0 ;
    			@Override
    			public void run() {
    				durationTime ++;
    				sendMsgToPioneer(durationTime*1000,fileSize,toPioneerTransmitByte,toPioneerInstRate);
    			}
    		};
    		timer.schedule(timerTask, 1000, 1000 );
    	}
    }
    
    /**
     * 业务开始时，
     * 定时每秒向Pioneer发送测试实时信息(数据业务通常是从first_data开始)
     */
    protected void startPioneerTimer(){
    	startPioneerTimer( INVALID_VALUE );
    }
    
    protected void stopPioneerTimer(){
    	if( timer!= null && timerTask!= null){
    		timer.cancel();
    		timerTask.cancel();
    		timer = null;
    		timerTask = null;
    	}
    	
    	//重置要传送的相关值
    	toPioneerTransmitByte = INVALID_VALUE;
    	toPioneerInstRate = INVALID_VALUE;
    }

    /**
     * 得到当前手机绑定的网卡
     * @return
     */
    protected String getNetInterface() {
    	String result = "";
		if(Deviceinfo.getInstance().isVivo() || Deviceinfo.getInstance().isSamsungCustomRom()){
			int type = taskModel.getTypeProperty();
			if (type == WalkCommonPara.TypeProperty_Wap || type == WalkCommonPara.TypeProperty_Net) {
				result = Deviceinfo.getInstance().getPppName();
			} else if (type == WalkCommonPara.TypeProperty_Wlan) {
				result = Deviceinfo.getInstance().getWifiDevice();
				Log.d(tag, "wifi device = " + result);
			}
		}else {
			//如果是小背包测试，或设置项里设置要绑定时，进行绑定
			if ( /*isUmpcTest ||*/useRoot || ConfigRoutine.getInstance().getNetInterface(this) == ConfigRoutine.NET_INTERFACE_BIND) {
				//再由模板决定绑定到哪个网卡
				int type = taskModel.getTypeProperty();
				if (type == WalkCommonPara.TypeProperty_Wap || type == WalkCommonPara.TypeProperty_Net) {
					result = Deviceinfo.getInstance().getPppName();
				} else if (type == WalkCommonPara.TypeProperty_Wlan) {
					result = Deviceinfo.getInstance().getWifiDevice();
					Log.d(tag, "wifi device = " + result);
				}
			}
		}
		LogUtil.w(tag,"getNetInterface()="+result);
		return result;
	}

	/**
	 * 数据业务测试的抽象类
	 * */
	protected abstract class DataTestHandler extends Handler{
		//JNI
		private ipc2jni aIpc2Jni = null ;	
		private boolean jni_running = false;
		private String args ="";
		private String client_path = "";
		private int test_item = 0;
		private int cmdStart = 0;
		private int cmdStop = 0;
		
		/**
		 * 创建一个新的实例 DataTestHandler.
		 * 这个实例必须运行construct方法初始化JNI
		 */
		public DataTestHandler(){
			
		}
		
		/**
		 * 数据业务的业务库JNI接口回调所用，下面几个参数请参考业务库的Demo
		 * @param args 业务的启动参数
		 * @param client_path 业务库的目录
		 * @param testItem 具体业务类型
		 * @param startCommand JNI初始化成功后发送的开始业务命令 
		 * @param stopCommand 停止业务测试的命令
		 */
		public DataTestHandler(String args,String client_path,int testItem,
				int startCommand,int stopCommand){
			construct(args, client_path, testItem, startCommand, stopCommand);
		}
		
		public void construct(String args,String client_path,int testItem,
				int startCommand,int stopCommand){
			this.args = args;
			this.client_path = client_path;
			this.test_item = testItem;
			this.cmdStart = startCommand;
			this.cmdStop = stopCommand;
		}
		
		// 关键节点时间
		protected long connectTime = 0;
		protected long connectedTime = 0;
		protected long sendGetTime = 0;
		protected long firstDataTime = 0;
		protected long lastDataTime = 0;
		protected long lastQosTime =0;
		protected long fileSize = 0;
		
		//统一解码库中获得的网络类型
		protected int netWorkType = 15;
		
		// 以下三个事件互斥
		protected boolean hasFail = false;
		protected boolean hasDrop = false;
		protected boolean hasLastData = false;
		
		//启动业务库调试之后,启动线程监控业务库是否有事件回调,当有回调后,该值为true
		protected boolean hasEventCallBack = false;
		
		// Qos
		/**结束测试*/
		protected boolean hasStopProcess = false;
		/**
		 * 传输大小 (Byte)
		 */
		protected long transByte = 0;
		/**
		 * 业务持续时间(ms)
		 */
		protected long delayTime = 0;
		/**
		 * 当前速率(bps)
		 */
		protected long avgRate = 0;	
		/**
		 * 峰值(bps)
		 */
		protected long peakValue = 0;
		/**
		 *当前速率 (bps)
		 */
		protected long currentSpeed = 0;
		/**
		 * 当前FTP活跃线程数
		 * */
		protected int activeThreadNum = 0;
		/**
		 * 当次回调的大小(Byte)
		 */
		protected long currentBytes = 0;
		/**
		 * 进度
		 */
		protected long progress = 0;
		
		/**
		 * 测试之前做的操作，可以是IO读写操作
		 */
		protected abstract void prepareTest();
		protected abstract void fail(int failReason,long time);
		protected abstract void drop(int dropReason,long time);
		protected abstract void sendCurrentRate();
		protected abstract void lastData(long time);
		//protected abstract void disConnect();
		
		private boolean initJniServer(){
			//初始化JNI
			aIpc2Jni = new ipc2jni( this );
			return aIpc2Jni.initServer(getLibLogPath());	
		}
		
		private void startJni(String cmdTAG){
			boolean initSuccess = initJniServer();
			LogUtil.i(tag, "initSuccess = "+initSuccess);
			if( initSuccess ){
				if( args.length()!=0 && client_path.length()!=0 
						&& test_item != 0 && cmdStart !=0 && cmdStop!=0  ){
                    jni_running = true;
                    if( useRoot ){
                        aIpc2Jni.set_su_file_path("su -c", "export LD_LIBRARY_PATH=" + getFilesDir().getParent() + "/lib/");
                    }
        			LogUtil.i(tag, "client_path = "+client_path);
        			LogUtil.i(tag, "args = "+args);
					LogUtil.i(tag, "cmdTAG = "+cmdTAG);
					boolean vx=false;
					if(null==cmdTAG||cmdTAG.trim().equals("")) {
						vx = aIpc2Jni.run_client(client_path, args);
					}else{
						vx = aIpc2Jni.run_clientCMD(client_path, args,cmdTAG);
					}
					LogUtil.i(tag, vx?"aIpc2Jni.run_client is success!":"aIpc2Jni.run_client is failure!");
                }
			}else{
				stopProcess("faild");
			}
		}

		/**
		 * 启动开始测试的线程
		 */
		public void startTest(){
			hasEventCallBack = false;
			
			//监控业务启动线程是否有事件回调.如果五秒内未有回调则报失败
			new Thread(){
				public void run(){
					int timeOut = 0;
					while(timeOut  < 5000 && !hasEventCallBack){
						timeOut += 50;
						UtilsMethod.ThreadSleep(50);
                        if (timeOut % 1000 == 0) {
                            LogUtil.w(tag, "--monitor event call back:" + timeOut);
                        }
                    }
					
					if(!hasEventCallBack){
						LogUtil.w(tag, "--monitor event call back faild---" );
						stopProcess("faild");
					}
				}
			}.start();
			
			new Thread(){
				public void run(){
					prepareTest();
					startJni(null);
				}
			}.start();
		}

		/**
		 * 启动开始测试的线程
		 */
		public void startTestCMD(final String cmdTAG){
			hasEventCallBack = false;

			//监控业务启动线程是否有事件回调.如果五秒内未有回调则报失败
			new Thread(){
				public void run(){
					int timeOut = 0;
					while(timeOut  < 5000 && !hasEventCallBack){
						timeOut += 50;
						UtilsMethod.ThreadSleep(50);
						LogUtil.w(tag, "--monitor event call back:" + timeOut);
					}

					if(!hasEventCallBack){
						LogUtil.w(tag, "--monitor event call back faild---" );
						stopProcess("faild");
					}
				}
			}.start();

			new Thread(){
				public void run(){
					prepareTest();
					startJni(cmdTAG);
				}
			}.start();
		}

		/**
		 * 启动JNI后回调初始化成功后开始运行测试
		 * @param initData
		 */
		protected void sendStartCommand(String initData){
			if( jni_running ){
				sendCommand( cmdStart, initData );
			}
		}
		protected void sendStartCommandTAG(String initData,String cmdTAG){
			if( jni_running ){
				sendCommandTAG( cmdStart, initData ,cmdTAG);
			}
		}
		/**
		 * 手工停止测试
		 */
		protected void sendStopCommand(){
			if( jni_running ){
				sendCommand( cmdStop, "");
			}
		}
		
		/**
		 * 
		 * @param event_id
		 * @param data
		 */
		protected void sendCommand(int event_id,String data){
			if( jni_running ){
				LogUtil.i(tag, "---start command,"+event_id+","+data );
				aIpc2Jni.send_command( test_item, event_id, data,data.length() );
			}
		}
		protected void sendCommandTAG(int event_id,String data,String cmdTAG){
			if( jni_running ){
				LogUtil.i(tag, "---start command,"+event_id+","+data );
				aIpc2Jni.send_commandTAG( test_item, event_id, data,data.length(),cmdTAG );
			}
		}
		protected void sendCommand(int test_item,int event_id,String data){
			if( jni_running ){
				LogUtil.i(tag, "---start command,"+event_id+","+data );
				aIpc2Jni.send_command( test_item, event_id, data,data.length() );
			}
		}
		
		public void uninitJniServer(){
			if( aIpc2Jni!=null ){
				jni_running = false;
				aIpc2Jni.uninit_server();
			}
		}
		
		/**
		 * 统计结果
		 * @param map
		 */
		protected void totalResult(HashMap<String,Long> map){
			if( map !=null ){
				callbackHandler.obtainMessage(CHART_CHANGE, map).sendToTarget();
			}
		}
		
		/**
		 * 获得当前网络类型
		 * @param isPBM 是否PBM
		 */
		protected void getNetWorkType(boolean isPBM){
			callbackHandler.obtainMessage(GET_NETWORK_TYPE, isPBM).sendToTarget();
		}

		/**
		 * 统计结果
		 * @param value
		 */
		protected void totalResult( TotalAppreciation totalType,long value){
			HashMap<String,Long> map = new HashMap<String,Long>();
			map.put( totalType.name(), value );
			totalResult( map );
		}
	
		
		/**
		 * 停止当次测试(退出当前测试服务)
		 * @param result
		 */
		public void stopProcess(String result){
			LogUtil.w(tag, "--result:" + result + "--suName:" + super.getClass().getName()
					+ "--hasStop:" + hasStopProcess + "--Interr:" + isInterrupted);
			if( !hasStopProcess){
				hasStopProcess = true;
				if(!isInterrupted){
					Message msg = callbackHandler.obtainMessage( TEST_STOP ,result);
					msg.sendToTarget();
				}
			}
		}
		
		/**
		 * 删除下载目录 中的文件，没有下载目录时创建目录 
		 * @param fileDir
		 */
		private void deleteDownFiles( String fileDir){
			try{
				File downDir = new File( fileDir );
				if( downDir.exists() && downDir.isDirectory() ){
					File[] files = downDir.listFiles();
					for(File file : files){
						if( file.isFile() ){
							Log.e( tag, "delete "+file.getAbsolutePath() );
							file.delete();
						}else if( file.isDirectory() ){
							deleteDownFiles( file.getAbsolutePath() );
						}
					}
				}
			}catch(Exception e){
				LogUtil.w(tag, "deleteDownFiles",e);
			}
		}
		
		protected void resetDownloadDir(){
			File downDir = new File( downloadPath );
			if( downDir.exists() && downDir.isDirectory() ){
				deleteDownFiles( downDir.getAbsolutePath() );
			}else{
				downDir.mkdirs();
			}
		}
		
		
		/**
		 * 根据回调的内容获取原因码
		 * @param aMsgData 回调的数据，必须是reason::-3这种形式(中间隔着::)
		 * @return
		 */
		protected int getReason(String aMsgData){
			String[] lines = aMsgData.split("\n");
			int result = 0;
			try{
				for( String s:lines){
					if( s.toLowerCase(Locale.getDefault()).startsWith( "reason::" ) ){
						result = Integer.parseInt( s.split("::")[1].trim() );
						break;
					}
				}
			}catch(Exception e){
				
			}
			return result;
		}
		
	} 
	
	/**
	 * 获得指定网的结果
	 * @param responseMsg
	 * @param key
	 * @return
	 */
	protected String getResponseResult(String responseMsg,String key){
		return getResponseResult(responseMsg,key,false);
	}
	
	/**
	 * 获得指定键值内容
	 * @param responseMsg	返回结果
	 * @param key			键值
	 * @param isInt			结果是否整形
	 * @return
	 */
	protected String getResponseResult(String responseMsg,String key,boolean isInt){
		String[] lines = responseMsg.split("\n");
		String result ="";
		
		try{
			for( String s:lines){
				if( s.toLowerCase(Locale.getDefault()).trim().startsWith( key.toLowerCase(Locale.getDefault())+"::" ) ){
					result = s.split("::")[1].trim();
					break;
				}
			}
		}catch(Exception e){
			LogUtil.w(tag,"-err:" + key,e);
		}
		
		if(result.equals("") && isInt){
			result = "-1";
		}
		return result;
	}
	
	/**
	 * 函数功能：生成彩信收发双方用来统计的唯一标记，这里定义为手机IMEI号+时间戳
	 * @return
	 */
	protected String genGUID(){
		String imei = "123456789ABCDEF";
		String timeStr = new SimpleDateFormat("yyyyMMddHHmmssSSS",Locale.getDefault()).format( System.currentTimeMillis() );
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		if( tm!=null ){
			imei = tm.getDeviceId();
		}
		return timeStr + imei;
	}
	
	/**
	 * 返回统计时的无效UUID
	 * @return
	 */
	protected String genGUIDDefault(){
		return "0000000000000000000000000000";
	}
	
	/**
	  * 取传入数组中指定位置的值
	 * 根据返回的 key::value来获得值
	 * 
	 * @param str
	 *          字符串
	 * @param isString
	 *          返回值是否字符串
	 * 
	 * @return
	 */
	protected String getValueByArray(String[] str, int index, boolean isString) {
		String value = "";
		if (index < str.length) {
			String[] ls = str[index].split("::");
			if (ls.length == 1) {
				if (!isString)
					value = "0";
			}
			value = ls[1];
		} else {
			if (!isString)
				value = "0";
		}
		return value;
	}
	
	/**
	 * 获得传入HashMap表的键值,如果该值不存在返回默认值
	 * @param hashMap
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	protected String getHashMapValue(HashMap<String, String> hashMap,String key,String defaultValue){
		if(hashMap.containsKey(key)){
			return hashMap.get(key);
		}
		return defaultValue;
	}
	
	/**
	 * 获取库的日志路径
	 * 
	 * @return
	 */
	protected String getLibLogPath() {
		return AppFilePathUtil.getInstance().getSDCardBaseDirectory("liblog");
	}


}
