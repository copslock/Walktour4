package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.dingli.service.test.ipc2jni;
import com.dingli.service.test.ipc2msg;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.RcuEventCommand.FailReason;
import com.walktour.Utils.TotalStruct.TotalAppreciation;
import com.walktour.Utils.TotalStruct.TotalHttpType;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.DataTaskValue;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.R;
import com.walktour.gui.about.WebViewActivity;
import com.walktour.gui.task.parsedata.model.task.http.page.TaskHttpPageModel;
import com.walktour.gui.task.parsedata.model.task.wap.TaskWapPageModel;
import com.walktour.model.TotalSpecialModel;
import com.walktour.service.ICallback;
import com.walktour.service.IService;
import com.walktour.service.TestService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


@SuppressLint({ "HandlerLeak", "SdCardPath" })
public class WebPage extends TestTaskService {
	
	//JNI回调
	private static final int HTTP_PAGE_TEST = 74;
	private static final int HTTP_PAGE_INITED = 1; 
	private static final int HTTP_PAGE_CONNECT_START = 2;
	private static final int HTTP_PAGE_CONNECT_SUCCESS = 3;
	private static final int HTTP_PAGE_CONNECT_FAILED = 4;
	private static final int HTTP_PAGE_SENDGETCMD = 5;
	private static final int HTTP_PAGE_SENDGETCMD_SUCCESS = 6;
	private static final int HTTP_PAGE_SENDGETCMD_FAILED = 7;
	private static final int HTTP_PAGE_URL_REDIRECT = 8;
	private static final int HTTP_PAGE_FIRSTDATA = 9;
	private static final int HTTP_PAGE_QOS = 10;
	private static final int HTTP_PAGE_DROP = 11;
	private static final int HTTP_PAGE_FINISH = 12;
	private static final int HTTP_PAGE_QUIT = 13;	
	//新增解析事件,此事件在连接事件之前进行
	private static final int HTTP_PAGE_DNSRESOLVE_START	=	15;  //开始解析
	//事件参数：http_page_dnsresolve_info
	private static final int HTTP_PAGE_DNSRESOLVE_SUCCESS=	16;  //解析成功
	//事件参数：http_page_fail_info
	private static final int HTTP_PAGE_DNSRESOLVE_FAILED=		17;  //解析失败
	//事件参数:http_page_mainpage_info
	private static final int HTTP_PAGE_MAINPAGE_OK	=		18;  //首页加载完成，不包括子资源
	
	private static final int HTTP_PAGE_START_TEST = 1001;	
	private static final int HTTP_PAGE_STOP_TEST = 1006;
	
	//Wap_Page事件类型
	private static final byte WAP_PAGE_TYPE_LOGON = 0;
	private static final byte WAP_PAGE_TYPE_REFRESH = 1;
	private static final byte WAP_PAGE_TYPE_LOGON_BEFORE_FRESH = 2;
	
	//http_page事件类型
	public static final int HTTP_PAGE_TYPE_LOGON = 0;
	public static final int HTTP_PAGE_TYPE_LOGON_BEFORE_FRESH = 1;
	public static final int HTTP_PAGE_TYPE_REFRESH = 2;
	public static final int HTTP_PAGE_TYPE_LOGON_HOMESITE = 3;//门户网站
	
	//业务库回调相关
	private String strQos;	
	private ipc2jni aIpc2Jni;	
	private boolean http_test;
	
	//测试模型
	private PageTestHandler pageHandler;
//	private int logonDrop = 0;						//刷新业务中的登陆失败次数
//	private DecimalFormat df= new DecimalFormat("#0.000");
	private int pdpDelay = 0;						//pdp的时延
	
    @Override  
    public IBinder onBind(Intent intent) {   
        LogUtil.d(tag, "---onBind");   
        return mBinder;   
    }
	//注册回调接口
    private IService.Stub mBinder = new IService.Stub() {   
        public void unregisterCallback(ICallback cb){   
            if(cb != null) {   
            	callbacks.unregister(cb);   
                hasRegeditCallback = false;
            }   
        }   
       
        public void registerCallback(ICallback cb){   
            if(cb != null) {
            	callbacks.register(cb);   
                hasRegeditCallback = true;
            }   
        }
		
		public void stopTask(boolean isTestInterrupt,int dropReason) throws RemoteException {
			LogUtil.i(tag, String.format( "===stopTask,isInterrupt:%b,reason:%d" ,
					isTestInterrupt,dropReason ) );
			long time = System.currentTimeMillis() * 1000;
			if( isTestInterrupt ){
				isInterrupted = true;
				stopTest();
				
				//手工停止的时候，有可能回调drop,也有可能没有回调drop,所以这里也添加Drop事件
				if( pageHandler != null ){
					pageHandler.dropByUserStop();
				}
			}else if( dropReason!= RcuEventCommand.DROP_NORMAL ){
				
				showEvent( getDropReasonString( dropReason ) );
				
				//2013.4.9 添加统一控制的Drop(out of service或者pppdrop)
				if( pageHandler != null ){
					if( pageHandler instanceof WapLoginHandler ){
						WapLoginHandler wapLoginHandler = (WapLoginHandler)pageHandler;
						wapLoginHandler.wapLoginDrop( dropReason,System.currentTimeMillis() * 1000 );//满足drop的条件在函数里判断
					}
					
					else if( pageHandler instanceof WapRefreshHandler ){
						WapRefreshHandler wapRefreHandler = (WapRefreshHandler) pageHandler;
						wapRefreHandler.wapRefreshDrop( (byte) dropReason, time);
					}
					
					else if( pageHandler instanceof HttpLoginHandler ){
						HttpLoginHandler httpLoginHandler = (HttpLoginHandler) pageHandler;
						httpLoginHandler.fail( dropReason,time );
						httpLoginHandler.httpLogonDrop(dropReason,time);
					}
					
					else if( pageHandler instanceof HttpRefreshHandler ){
						HttpRefreshHandler httpRefreshHandler = (HttpRefreshHandler) pageHandler;
						httpRefreshHandler.fail( dropReason,time );
						httpRefreshHandler.httpRefreshDrop( dropReason,time );
					}
				}
			}
			
			if( pageHandler != null ){
				pageHandler.disconnect();
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
    
    
    @Override  
    public void onCreate() {   
        super.onCreate(); 
        tag = "WebPage";
        LogUtil.i(tag,"onCreate");
    }
    
    @Override
	public int onStartCommand(Intent intent,int flags, int startId) {
		LogUtil.i(tag, "onStart");
		useRoot = Deviceinfo.getInstance().isUseRoot();
		int startFlag = super.onStartCommand(intent, flags, startId);
		LogUtil.d(tag, "taskModel="+taskModel.getClass().getSimpleName());		
		//初始化JNI相关服务,这个要和Demo中一样，只能初始化一次
		if( taskModel instanceof TaskHttpPageModel ){
			TaskHttpPageModel httpModel = (TaskHttpPageModel) taskModel;
			switch( WalkStruct.TaskType.valueOf( httpModel.getTaskType() ) ){
			case Http:
				pageHandler = new HttpLoginHandler( httpModel );
				if( httpModel.isShowWeb() ){
					loadUrlWebView( httpModel.getUrl() );
				}
				break;
			case HttpRefurbish:
				pageHandler = new HttpRefreshHandler( httpModel );
				if( httpModel.isShowWeb() ){
					loadUrlWebView( httpModel.getUrl() );
				}
				break;
			default:
				Log.i(tag, "type:"+httpModel.getTaskType() );
				break;
			}
		}else if( taskModel instanceof TaskWapPageModel ){
			TaskWapPageModel wapModel = (TaskWapPageModel) taskModel;
			pdpDelay = intent.getIntExtra( KEY_PDP_DELAY, 0);
			switch( WalkStruct.TaskType.valueOf( wapModel.getTaskType() ) ){
			case WapLogin:
				pageHandler = new WapLoginHandler( wapModel );
				break;
			case WapRefurbish:
				pageHandler = new WapRefreshHandler(  wapModel );
				break;
			default:
				Log.i(tag, "type:"+wapModel.getTaskType() );
				break;
			}
		}
		
		aIpc2Jni = new ipc2jni( pageHandler );
		aIpc2Jni.initServer(this.getLibLogPath());	
		
		new Thread(){
			public void run(){
				while( !hasRegeditCallback );
				startTest();
			}
		}.start();
		
		return startFlag; 
	}
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	LogUtil.i(tag,"---onDestroy-aIpc2JniNull:"+(aIpc2Jni == null));
    	if( aIpc2Jni != null ){
    		aIpc2Jni.uninit_server();
    	}
    	
    	callbackHandler.removeMessages(0);
    	callbacks.kill();
    	android.os.Process.killProcess(android.os.Process.myPid());
    	//因为业务库的原因，此服务关闭后进程未退出，在此直接杀本进程
    	UtilsMethod.killProcessByPname( "com.walktour.service.test.WebPage", false );
    }
    
    /**开始测试,在拨号成功后调用*/
    protected void startTest(){
		strQos = "";
		http_test = false;
		
		pageHandler.reset();
		
		String args = "-m web_http -z "+ AppFilePathUtil.getInstance().getAppConfigDirectory();
		LogUtil.i(tag, args);
		
		//在非root权限下调用
//		if(Deviceinfo.getInstance().isS8() || Deviceinfo.getInstance().isS7()){
			useRoot=false;//特殊处理下，使用exe执行不起来
//		}
		String client_path = AppFilePathUtil.getInstance().getAppLibFile(useRoot?"webbrowser_lynx":"libwebbrowser_lynx_so.so").getAbsolutePath();
        if( useRoot ){
            aIpc2Jni.set_su_file_path("su -c", "export LD_LIBRARY_PATH=" +AppFilePathUtil.getInstance().getAppLibDirectory());
        }
        LogUtil.i(tag, client_path );
        aIpc2Jni.run_client(client_path, args);

		http_test = true;
    }

    /**停止测试，用户中断时调用*/
    protected void stopTest(){
    	if(http_test)
		{
			aIpc2Jni.send_command(HTTP_PAGE_TEST, HTTP_PAGE_STOP_TEST, "", 0);
		}
    }
    
    
    /**停止当次测试*/
    private boolean hasStopCurrentTest = false;
    private class StopCurrentTest implements Runnable{
    	private String result ="";//回调到TestService的结果
    	
    	/**
    	 * @param result 回调到TestService的结果，可以为""
    	 * */
    	public StopCurrentTest (String result ){
    		this.result = result;
    	}
    	
		@Override
		public void run() {
			sendBroadcast(new Intent(WalkMessage.KEY_HTTP_SHOWWEB_QUID));
			
			LogUtil.w(tag, "--hasStop:" + hasStopCurrentTest + "--isInter:" + isInterrupted);
			if( !hasStopCurrentTest){  //手工停止测试不需要发送关闭命令
				hasStopCurrentTest = true;
				
				try {
					Thread.sleep( 1000 );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if(!isInterrupted){
    				//结束测试
    				Message msg = callbackHandler.obtainMessage( TEST_STOP ,result);
    				msg.sendToTarget();
				}
			}
		}
    }
    
	/**
	 * 显示事件
	 * @param event 
	 * */
	private void showEventNT(String event){
		Message msg = callbackHandler.obtainMessage(EVENT_CHANGE, event);
		msg.sendToTarget(); 
	}
	
	/**
	 * 显示事件,前面带有序号
	 * @param event 
	 * */
	protected void showEvent(String event){
		Message msg = callbackHandler.obtainMessage(EVENT_CHANGE, repeatTimes+"-"+event);
		msg.sendToTarget(); 
	}
	
/*    *//**
     * 发送瞬时速率到数据页面
     * @param 速率
     * *//*
    private void sendCurrentRate(long meanRate){
    	Map<String, Object> dataMap =new HashMap<String, Object>();
    	if(){
    		dataMap.put(WalkStruct.DataTaskValue.WapDlThrput.name(), UtilsMethod.decFormat.format(meanRate) );
    	}else{
    		dataMap.put(WalkStruct.DataTaskValue.WapDlThrput.name(), UtilsMethod.decFormat.format(meanRate) );
    	}
    	Message msg = callbackHandler.obtainMessage(DATA_CHANGE,dataMap);
		callbackHandler.sendMessage(msg);
    }*/
    
    /**
     * Page 测试的抽象类
     * */
    private abstract class PageTestHandler extends Handler{
    	
    	protected boolean hasFail = false;
    	
    	protected long dnsStartTime = 0;
    	
    	/**
    	 * 抽象方法：重新设置所有基本类型(JNI初始化只有new一次Handler,所以要用到此函数)
    	 * */
    	public abstract void reset(); 
    	/**
    	 *用户手动停止测试
    	 * */
    	public abstract void dropByUserStop();
    	
    	/**
    	 * 业务失败
    	 * @param reason
    	 */
    	protected abstract void fail(int reason,long time);
    	
    	protected abstract void disconnect();
    	
		/**
		 * 统计WAP业务
		 * @param totalType 统计项
		 * @param value 值
		 */
		protected void totalWapResult(TotalAppreciation totalType,int value){
			HashMap<String,Integer> map = new HashMap<String,Integer>();
			map.put( totalType.name(), value );
			Message msg = callbackHandler.obtainMessage( CHART_CHANGE, map );
			msg.sendToTarget();
		}
		
		/**
		 * 统计HTTP业务
		 * @param httpType 主键1，HTTP登陆或刷新
		 * @param url url 主键2,HTTP的URL
		 * @param totalType 要统计的具体值的KEY,如http刷新时延
		 */
		protected void totalHttpResult( HashMap<String, TotalSpecialModel> map,
				TotalHttpType httpType,String url,TotalAppreciation totalType,int value ){
		    TotalSpecialModel tmpSp = new TotalSpecialModel(
		    		httpType.getHttpType(), 
		    		url, 
		    		totalType.name(), 
		    		value
		    );
			map.put( tmpSp.getKeyName(), tmpSp );
		}
		
		
    }
    
    /**JNI回调*/
	//used to update ui
	private class WapLoginHandler extends PageTestHandler
	{
		
		private long gatewayBegin = 0l;	//连接网关开始时间
		private long gatewayFinish = 0l;//连接网关成功时间
		private long beginRequest = 0l; //发送请求开始时间
		private long firstData = 0l;	//第一个数据的时间
		private int logonDelay = 0;		//登录总时延= PDP激活时间 + 网关连接时间 + 页面响应时间
		private long lastQosTime = 0;	//上次写入QOS的时间
		private boolean hasDrop = false;
		private Timer finishTimer;
		private TimerTask finishTimerTask;
		private boolean hasLastData = false;
		private int finishDealy = 0;
		
		private TaskWapPageModel wapModel;
		public WapLoginHandler(TaskWapPageModel wapModel){
			this.wapModel = wapModel;
		}
		
		@Override
		public void reset(){
			  gatewayBegin = 0l;	//连接网关开始时间
			  gatewayFinish = 0l;//连接网关成功时间
			  beginRequest = 0l; //发送请求开始时间
			  firstData = 0l;	//第一个数据的时间
			  hasDrop = false;
			  toPioneerTransmitByte = 0l;		//收到的数据大小
			  hasLastData = false;
			  lastQosTime = 0;
		}
		
		public void handleMessage(android.os.Message msg)
    	{
			ipc2msg aMsg = (ipc2msg)msg.obj;
			if(aMsg.test_item != HTTP_PAGE_TEST || hasDrop )
			{
				return;
			}
			
			    		
    		switch(aMsg.event_id)
    		{
    		
    		// 如果qos_inv_ms设置成1000ms,而业务可能在1000ms内完成，这里没有返回文件大小
    		case HTTP_PAGE_QOS:
    			strQos = aMsg.data;
    			LogUtil.d(tag,"---HTTP WapLoginid:" + aMsg.event_id + "--" + aMsg.data +"\n\r");
    			
				//发送大小
				String s = strQos.split("\n")[1].split("::")[1];
				int b = Integer.parseInt( s );
				int internalTime =  (int) (aMsg.getRealTime() - lastQosTime) / 1000;
				lastQosTime = aMsg.getRealTime();
				int internalBytes = (int)(b - toPioneerTransmitByte);
				//已发送大小
				toPioneerTransmitByte = b;
				toPioneerInstRate = internalBytes*8;
				//写入Ftp_data数据格式
				if( firstData !=0 ){
					int totalTime = (int) (aMsg.getRealTime() - firstData) / 1000;
					UtilsMethod.sendWriteRcuFtpData(getApplicationContext(),
							WalkCommonPara.MsgDataFlag_B,
							0x00, totalTime, b, internalTime, internalBytes);
					
					sendCurrentRate(strQos);
				}
    			break;
    			
    		case HTTP_PAGE_INITED:
    			LogUtil.i(tag, "HTTP_PAGE_INITED\r\n");
    			LogUtil.i(tag, aMsg.data + "\r\n");
    			//是否要保存文件
    			File file = new File( downloadPath );
    			int do_save = file.exists()? 1:0;
    			//各个参数属性请参考基础库的web_http_jni.h
    			String event_data = "local_if::"+ "" + "\n"//rmnet0
				  + "connect_timeout_ms::" + 15*1000 + "\n"
				  + "login_timeout_ms::" + wapModel.getTimeOut()*1000 + "\n"
				  + "nodata_timeout_ms::" + "180000" + "\n"
				  + "qos_inv_ms::" + 1*1000 + "\n"
				  + "url::" + wapModel.getUrl() + "\n"
				  + "refresh_type::" + "" + "\n"	//1,普通刷新  2,深度刷新
				  + "refresh_times::" + "" + "\n"
				  + "refresh_timeout_ms::" + "" + "\n"
				  + "refresh_delay_ms::" + "" + "\n"
				  + "protol_type::" + PROTOL_TYPE_WAP20 + "\n"
				  + "gateway_ip::" + wapModel.getGateway()+ "\n"
				  + "gateway_port::" + wapModel.getPort() + "\n"
				  + "save_filepath::" + downloadPath + "\n"
				  + "ps_call::" + 0 + "\n"		//ps_call==1时，超时未完成当结束，ps_call==0,超时未完成当drop
				  + "transmit_img::" + 1 + "\n"
				  + "clear_cache::" + 1+ "\n"
				  + "use_gateway::" + 1 + "\n"
				  //2013.4.2 添加新的属性
				  + "gateway_type::" + "" + "\n" //代理类型。 1:HTTP  2:Socks4  3:Socks4A  4:Socks5  5:Socks5Hostname
				  + "gateway_username::" + "" + "\n" //代理用户名
				  + "gateway_password::" + "" + "\n" //代理密码
				  + "user_agent::" + "" + "\n" //模拟手机浏览器（只在WAP下使用）
				  + "parent_processid::" + 0 + "\n"//父进程ID(父进程异常退出，子进程会跟随退出。0：不设定)
				  + "do_save::" + do_save ;
    			LogUtil.i(tag, event_data );
    			aIpc2Jni.send_command(HTTP_PAGE_TEST, HTTP_PAGE_START_TEST, event_data, event_data.length());
    			//2013.5.23添加此事件
    			EventBytes.Builder(WebPage.this, RcuEventCommand.WAP_PageStart )
    			.addStringBuffer( wapModel.getUrl() )
    			.addByte( WAP_PAGE_TYPE_LOGON )
    			.writeToRcu(aMsg.getRealTime());
    			showEventNT("WAP Page Start");
    			//connecting事件
    			writeRcuEvent( RcuEventCommand.WAP_SockConnecting,aMsg.getRealTime() );
				break;
			
				//连接网关开始
    		case HTTP_PAGE_CONNECT_START:
    			LogUtil.i(tag, "recv HTTP_PAGE_CONNECT_START\r\n");
    			gatewayBegin = aMsg.getRealTime();
    			writeRcuEvent( RcuEventCommand.WAP_ConnectGatewayRequest,aMsg.getRealTime() );
    			showEvent("WAP Logon Connect Gateway Request");
    			//网关连接到登陆成功的超时
    			startFinishTimer( wapModel.getTimeOut() );
    			//登录业务的开始时间从Logon开始
    			startPioneerTimer();
    			break;
    			
    			//连接网关成功
    		case HTTP_PAGE_CONNECT_SUCCESS:
    			LogUtil.i(tag, "recv HTTP_PAGE_CONNECT_SUCCESS\r\n");
    			gatewayFinish = aMsg.getRealTime();
    			int delayGateway = (int)( gatewayFinish - gatewayBegin) / 1000;
    			writeRcuEvent( RcuEventCommand.WAP_ConnectGatewayFinished,aMsg.getRealTime(), 
    					RcuEventCommand.WAP_CONNECTGATEWAY_SUCCESS, 
    					delayGateway
    			);
    			showEvent( "WAP Logon Connect Gateway Success:Delay "+delayGateway+"(ms)" );
    			break;
    			
    			//连接网关失败
    		case HTTP_PAGE_CONNECT_FAILED:
    			LogUtil.i(tag, "recv HTTP_PAGE_CONNECT_FAILED\r\n");
    			LogUtil.i(tag, aMsg.data + "\r\n");
    			//连接失败的原因
//    			int failReason = 0;
//    			try{
//    				failReason = Integer.parseInt( aMsg.data.split("::")[1] );
//    			}catch(Exception e){
//    				
//    			}
    			connectFail( aMsg.getRealTime() );
    			break;
    			
    			//发送获取页面请求
    		case HTTP_PAGE_SENDGETCMD:
    			LogUtil.i(tag, "recv HTTP_PAGE_SENDGETCMD\r\n");
    			beginRequest = aMsg.getRealTime();
    			writeRcuEvent( RcuEventCommand.WAP_GetRequest,aMsg.getRealTime() );
    			showEvent( "WAP Logon Get Request" );
    			break;
    			
    			//获取页面成功
    		case HTTP_PAGE_SENDGETCMD_SUCCESS:
    			LogUtil.i(tag, "recv HTTP_PAGE_SENDGETCMD_SUCCESS\r\n");
    			int reqSuccssDelay = (int) ( aMsg.getRealTime() - beginRequest ) / 1000;
    			writeRcuEvent(RcuEventCommand.WAP_GetUrlFinished,aMsg.getRealTime(),
    					RcuEventCommand.WAP_GetUrlFinished_Success,
    					reqSuccssDelay);
    			showEvent("WAP Logon Get URL Success,Delay "+reqSuccssDelay+"(ms)");
    			
    			break;
    			
    			//获取页面失败
    		case HTTP_PAGE_SENDGETCMD_FAILED:
    			LogUtil.i(tag, "recv HTTP_PAGE_SENDGETCMD_FAILED\r\n");
    			LogUtil.i(tag, aMsg.data + "\r\n");
//    			int reason = 0;
//    			try{
//    				//原因码为Fail的原因码1000，不是Drop的原因码
//    				reason = Integer.parseInt( aMsg.data.split("::")[1] );
//    			}catch(Exception e){
//    				
//    			}
    			sendCmdFail(aMsg.getRealTime());
//    			//Drop:WAP登录Reply之后服务器直接返回失败
//    			wapLoginDrop( RcuEventCommand.DROP_NORMAL );
    			//2013.4.15 看回规范文件，这里应该是终止当次测试，但不满足条件drop.
    			//这里不作任何处理直至等到超时Drop
    			break;
    			
    			//URL跳转
    		case HTTP_PAGE_URL_REDIRECT:
    			LogUtil.i(tag, "recv HTTP_PAGE_URL_REDIRECT\r\n");
    			LogUtil.i(tag, aMsg.data + "\r\n");
    			String url = aMsg.data.split("::")[1];
    			EventBytes.Builder( WebPage.this,RcuEventCommand.WAP_UrlRedirect)
    			.addCharArray( url.toCharArray(), 2048 )
    			.writeToRcu(aMsg.getRealTime());
    			
    			break;
    			
    			//响应第1个数据
    		case HTTP_PAGE_FIRSTDATA:
    			LogUtil.i(tag, "recv HTTP_PAGE_FIRSTDATA\r\n");
    			firstData = aMsg.getRealTime();
    			lastQosTime = firstData;
    			int firstDelay = (int ) ( firstData - beginRequest ) / 1000;
    			//writeRcuEvent( RcuEventCommand.WAP_FirstData,firstDelay );
    			delayGateway = (int)( gatewayFinish - gatewayBegin) / 1000;
    			logonDelay = pdpDelay + firstDelay + delayGateway;
    			EventBytes.Builder( WebPage.this,RcuEventCommand.WAP_FirstDataEx)
    			.addInteger( pdpDelay )
    			.addInteger( delayGateway )
    			.addInteger( firstDelay )
    			.addInteger( logonDelay )
    			.writeToRcu(aMsg.getRealTime());
    			Log.i(tag,String.format( "pdpdelay:%d,gateway:%d,firstDelay:%d",
    					pdpDelay,delayGateway,firstDelay ) );
    			showEvent( "WAP Logon Reply:Delay "+logonDelay+"(ms)" );
    			break;
    			
    		case HTTP_PAGE_DROP:
    			LogUtil.i(tag, "recv HTTP_PAGE_DROP\r\n");
    			LogUtil.i(tag, aMsg.data );
    			int r = RcuEventCommand.DROP_NORMAL;
    			try{
    				r = Integer.parseInt( aMsg.data.split("::")[1] );
    			}catch(Exception e){
    				
    			}
				wapLoginDrop( r,aMsg.getRealTime() );
    			break;
    			
    		case HTTP_PAGE_FINISH:
    			LogUtil.i(tag, "recv HTTP_PAGE_FINISH\r\n");
    			hasLastData = true;
    			 finishDealy = (int) ( aMsg.getRealTime() - gatewayFinish ) / 1000;
    			EventBytes.Builder( WebPage.this,RcuEventCommand.WAP_LastData)
    			.addInteger( (int)toPioneerTransmitByte )
    			.addInteger( finishDealy )
    			.addByte( WAP_PAGE_TYPE_LOGON )
    			.writeToRcu(aMsg.getRealTime());
    			showEvent("WAP Logon Finish");
    			//统计 //统计 的时延是从连接网关到当前
    			totalWapResult( TotalAppreciation._wapLoginTrys, 1 );
    			totalWapResult( TotalAppreciation._wapLogingSuccs, 1);
    			totalWapResult( TotalAppreciation._wapLoginDelay, logonDelay );
    			break;
    			
    		case HTTP_PAGE_QUIT:
    			LogUtil.i(tag, "recv HTTP_PAGE_QUIT\r\n");
    			//没有连接网关直接退出时，业务失败
    			if( gatewayBegin ==0 ){
    				fail( FailReason.UNKNOWN.getReasonCode(),aMsg.getRealTime() );
    			}
    			
    			new Thread( new StopCurrentTest( TestService.RESULT_SUCCESS ) ).start();
    			break;
    		
    		}   		
		}

		/**
		 * 以下是文档描述
		 * WAP登录Drop判断：在以下几种情况下，都必须插入WAP登录Drop事件，结束该次WAP登录测试流程；
		 * WAP获取URL成功但收不到WAP登录Reply时，WAP库返回Drop 
		 * 在超时时长内收不到WAP登录最后一个数据包
		 * WAP登录Reply之后服务器直接返回失败 ? 
		 * 
		 * WAP超时时长：从WAP Page Connect Gateway Request开始计时，收不到最后一个数据包，均认为超时。
		 * 
		 * 保证所有重复的drop只有一次
		 * @param reason drop的原因码
		 * */
    	public void wapLoginDrop(int reason,long time){
    		if( !hasDrop && !hasFail && !hasLastData && gatewayBegin!=0 ){
    			hasDrop = true;
    			int delay = (int) (time - gatewayBegin ) / 1000;
    			//存储Drop事件
    			EventBytes.Builder( WebPage.this, RcuEventCommand.WAP_Drop)
    			.addInteger( delay )
    			.addInteger( (int)toPioneerTransmitByte )
    			.addByte( (byte)reason )
    			.addByte( WAP_PAGE_TYPE_LOGON )
    			.writeToRcu(time);
    			
    			showEvent( "WAP Logon Drop:Delay "+delay+"(ms)," +
    					"Transmit Size:"+toPioneerTransmitByte/UtilsMethod.kbyteRage+" KBytes," +
    					getDropReasonString(reason) );
    			
    			//统计
    			if( reason == RcuEventCommand.DROP_USERSTOP  && delay>0 ){
    				//统计 的时延是从连接网关到当前
        			totalWapResult( TotalAppreciation._wapLoginTrys, 1 );
        			totalWapResult( TotalAppreciation._wapLogingSuccs, 1);
        			totalWapResult( TotalAppreciation._wapLoginDelay, logonDelay );
    			}else{
    				totalWapResult( TotalAppreciation._wapLoginTrys, 1 );
    				totalWapResult( TotalAppreciation._wapLogingSuccs, 0 );
    			}
    			
    			//结束本次测试
				new Thread( new StopCurrentTest( TestService.RESULT_SUCCESS ) ).start();
    		}
    	}
    	
    	 /**等待完成计时器*/
        private void startFinishTimer( int timeOut){
    		if( finishTimer!= null && finishTimerTask!= null){
    			finishTimer.cancel();
    			finishTimerTask.cancel();
    			finishTimer = null;
    			finishTimerTask = null;
    		}
    		finishTimer = new Timer();
    		finishTimerTask = new TimerTask(){
    			@Override
    			public void run() {

    				if( !hasLastData ){
    					LogUtil.i(tag, "receive data timout");
    					wapLoginDrop( RcuEventCommand.DROP_TIMEOUT,System.currentTimeMillis() );
    				}
    			}
    			
    		};
    		finishTimer.schedule( finishTimerTask, timeOut * 1000 );
    	}
        
		@Override
		public void dropByUserStop() {
			this.wapLoginDrop( RcuEventCommand.DROP_USERSTOP ,System.currentTimeMillis() * 1000);
		}
		
		private void connectFail(long time ){
			if( !hasFail ){
				hasFail = true;
				
				int delayGF = (int)(time - gatewayBegin) / 1000;
    			writeRcuEvent( RcuEventCommand.WAP_ConnectGatewayFinished,time,
    					RcuEventCommand.WAP_CONNECTGATEWAY_FAIL, 
    					delayGF
    			);
    			showEvent("WAP Logon Connect Gateway Failure:Delay "+delayGF+"(ms)");
    			//停止当前测试
    			new Thread( new StopCurrentTest( TestService.RESULT_FAILD ) ).start();
			}			
		}
		
		
		private void sendCmdFail(long time){
			if( !hasFail ){
				hasFail = true;
				
				int reqFailDealy = (int) (time - beginRequest ) / 1000;
    			writeRcuEvent(RcuEventCommand.WAP_GetUrlFinished,time,
    					RcuEventCommand.WAP_GetUrlFinished_Fail,
    					reqFailDealy);
    			showEvent("WAP Refresh Get URL Failure,Delay "+reqFailDealy+"(ms)");
    			
    			//登录业务从连接网关开始，这里统计为登录失败
    			totalWapResult( TotalAppreciation._wapLoginTrys, 1 );
				totalWapResult( TotalAppreciation._wapLogingSuccs, 0 );
    			
    			//停止当前测试
    			new Thread( new StopCurrentTest( TestService.RESULT_SUCCESS ) ).start();
			}
		}
		
		/**
		 * http登录业务失败
		 * @param reason
		 */
		protected void fail( int reason,long time ){
			if( !hasFail && !hasDrop && !hasLastData){
				hasFail = true;
				
				//RCU事件没有定义，这里只显示
//				EventBytes.Builder( mContext, RcuEventCommand.HTTP_Page_Fail)
//				.addInteger( reason )
//				.writeToRcu();
				
				showEvent( String.format("WAP Logon Failure:%s",
						FailReason.getFailReason(reason).getResonStr() ) );
				
				//结束本次测试
				new Thread( new StopCurrentTest( TestService.RESULT_FAILD ) ).start();
			}
		}

		@Override
		protected void disconnect() {
			
		}
	};
	
    /**JNI回调*/
	//used to update ui
	private class WapRefreshHandler extends PageTestHandler
	{
		private long gatewayBegin = 0l;	//连接网关开始时间
		private long gatewayFinish = 0l;//连接网关成功时间
		private long request = 0l; //发送请求开始时间
		private long firstData = 0l;	//第一个数据的时间
		private long lastQosTime=0l;	//上次写入QOS的时间
		private long lastData = 0l;
		private boolean hasDrop = false;
		private boolean hasLogon = false;
		
		private boolean isInterval = false;//是否在间隔时间内(处理刷新间隔中手工停止会drop的问题)
		
		private TaskWapPageModel wapModel;
		public WapRefreshHandler(TaskWapPageModel wapModel){
			this.wapModel = wapModel;
		}
		
		/**重设相关变量*/
		public void reset(){
			  gatewayBegin = 0l;	//连接网关开始时间
			  gatewayFinish = 0l;//连接网关成功时间
			  request = 0l; //发送请求开始时间
			  firstData = 0l;	//第一个数据的时间
			  lastData = 0l;
			  hasDrop = false;
			  toPioneerTransmitByte = 0l;		//收到的数据大小
			  hasLogon = false;
			  lastQosTime = 0;
		}
		
    	public void handleMessage(android.os.Message msg)
    	{
			ipc2msg aMsg = (ipc2msg)msg.obj;
			if(aMsg.test_item != HTTP_PAGE_TEST   || hasDrop )
			{
				LogUtil.w(tag, "---not testing");
				return;
			}
			    		
			LogUtil.d(tag,"---HTTP WapRefrese eventid:" + aMsg.event_id + "--data:" + aMsg.data +"\n\r");
    		switch(aMsg.event_id)
    		{
    		// 如果qos_inv_ms设置成1000ms,而业务可能在1000ms内完成，这里没有返回文件大小
    		case HTTP_PAGE_QOS:
    			strQos = aMsg.data;
    			
				//接收大小
				String s = strQos.split("\n")[1].split("::")[1];
				int b = Integer.parseInt( s );
				int internalTime = (int) (aMsg.getRealTime() - lastQosTime) / 1000;
				lastQosTime = aMsg.getRealTime();
				int internalBytes = (int) (b - toPioneerTransmitByte);
				toPioneerInstRate = internalBytes * 8;
				//已发送大小
				toPioneerTransmitByte = b;
				//写入Ftp_data数据格式
				if( firstData !=0 && firstData > request){//必须是firstData之后的QOS
					int totalTime = (int) (aMsg.getRealTime() - firstData) / 1000;
					UtilsMethod.sendWriteRcuFtpData(getApplicationContext(),
							WalkCommonPara.MsgDataFlag_B,
							0x00, totalTime, toPioneerTransmitByte, internalTime, internalBytes);
					
					sendCurrentRate(strQos);
				}
				
    			break;
    			
    		case HTTP_PAGE_INITED:
    			LogUtil.i(tag, "HTTP_PAGE_INITED\r\n");
    			LogUtil.i(tag, aMsg.data + "\r\n");
    			//是否要保存文件
    			File file = new File( downloadPath );
    			int do_save = file.exists()? 1:0;
    			//各个参数属性请参考基础库的web_http_jni.h
    			String event_data = "local_if::"+ ""+ "\n"//rmnet0
				  + "connect_timeout_ms::" + 15*1000 + "\n"
				  + "login_timeout_ms::" + wapModel.getTimeOut()*1000 + "\n"
				  + "nodata_timeout_ms::" + "180000" + "\n"
				  + "qos_inv_ms::" + 1*1000 + "\n"
				  + "url::" + wapModel.getUrl() + "\n"
				  + "refresh_type::" + 2 + "\n"	//1,普通刷新  2,深度刷新
				  + "refresh_times::" +  wapModel.getRepeat() + "\n"
				  + "refresh_timeout_ms::" + wapModel.getTimeOut()*1000 + "\n"
				  + "refresh_delay_ms::" + wapModel.getInterVal()*1000 + "\n"
				  + "protol_type::" + PROTOL_TYPE_WAP20 + "\n"
				  + "gateway_ip::" + wapModel.getGateway()+ "\n"
				  + "gateway_port::" + wapModel.getPort() + "\n"
				  + "save_filepath::" + downloadPath + "\n"
				  + "ps_call::" + 0 + "\n"	//ps_call==1时，超时未完成当结束，ps_call==0,超时未完成当drop
				  + "transmit_img::" + 1 + "\n"
				  + "clear_cache::" + 1+ "\n"
				  + "use_gateway::" + 1 + "\n"
				  //2013.4.2 添加新的属性
				  + "gateway_type::" + "" + "\n" //代理类型。 1:HTTP  2:Socks4  3:Socks4A  4:Socks5  5:Socks5Hostname
				  + "gateway_username::" + "" + "\n" //代理用户名
				  + "gateway_password::" + "" + "\n" //代理密码
				  + "user_agent::" + "" + "\n" //模拟手机浏览器（只在WAP下使用）
				  + "refresh_depth::" + wapModel.getRefreshDepth() + "\n" //刷新深度
				  + "parent_processid::" + 0 + "\n"//父进程ID(父进程异常退出，子进程会跟随退出。0：不设定)
				  + "do_save::" + do_save ;
    			LogUtil.i(tag, event_data );
    			aIpc2Jni.send_command(HTTP_PAGE_TEST, HTTP_PAGE_START_TEST, event_data, event_data.length());
    			
    			repeatTimes = 1;
    			
    			//2013.5.23添加此事件 新后台(6.1)能解析此事件了，
    			EventBytes.Builder(WebPage.this, RcuEventCommand.WAP_PageStart )
    			.addStringBuffer( wapModel.getUrl() )
    			.addByte( WAP_PAGE_TYPE_LOGON_BEFORE_FRESH )//刷新前登录，必须写在连接网关前
    			.writeToRcu(aMsg.getRealTime());
    			showEventNT("WAP Page Start");
    			//显示connecting icon_event_1
    			writeRcuEvent( RcuEventCommand.WAP_SockConnecting,aMsg.getRealTime() );
				break;
			
				//连接网关开始
    		case HTTP_PAGE_CONNECT_START:
    			LogUtil.i(tag, "recv HTTP_PAGE_CONNECT_START\r\n");
    			gatewayBegin = aMsg.getRealTime();
    			EventBytes.Builder(WebPage.this,RcuEventCommand.WAP_ConnectGatewayRequest ).writeToRcu(aMsg.getRealTime());
    			showEventNT( "WAP Refresh Connect Gateway Request" );
    			break;
    			
    			//连接网关成功
    		case HTTP_PAGE_CONNECT_SUCCESS:
    			LogUtil.i(tag, "recv HTTP_PAGE_CONNECT_SUCCESS\r\n");
    			gatewayFinish = aMsg.getRealTime();
    			int delayGateway = (int)( gatewayFinish - gatewayBegin) / 1000;
    			EventBytes.Builder( WebPage.this, RcuEventCommand.WAP_ConnectGatewayFinished)
    			.addInteger( RcuEventCommand.WAP_CONNECTGATEWAY_SUCCESS)
    			.addInteger( delayGateway )
    			.writeToRcu(aMsg.getRealTime());
    			showEventNT( "WAP Refresh Connect Gateway Success:Delay "+delayGateway+"(ms)" );
    			break;
    			
    			//连接网关失败
    		case HTTP_PAGE_CONNECT_FAILED:
    			LogUtil.i(tag, "recv HTTP_PAGE_CONNECT_FAILED\r\n");
    			LogUtil.i(tag, aMsg.data + "\r\n");
    			connectFail(aMsg.getRealTime() );
    			break;
    			
    			//发送获取页面请求 ( Request ) 
    		case HTTP_PAGE_SENDGETCMD:
    			LogUtil.i(tag, "recv HTTP_PAGE_SENDGETCMD\r\n" + repeatTimes);
    			setTaskCurrentTimes(repeatTimes ++);
    			
    			//重设定接收数据大小 
    			toPioneerTransmitByte = 0;
    			isInterval = false;
    			//第1次是登陆请求，非第1次是刷新请求
    			if( request==0 ){
    				hasLogon = false;
    				request = aMsg.getRealTime();
    				writeRcuEvent( RcuEventCommand.WAP_GetRequest,aMsg.getRealTime() );
//    				showEventNT( "WAP Get URL Request" );
    				//2013.6.28按文档<RCU测试事件存储和显示标准>修改
    				showEventNT( "WAP Refresh Get Request" );
    			}else{
    				request = aMsg.getRealTime();
    				//2013.5.23添加此事件
        			EventBytes.Builder(WebPage.this, RcuEventCommand.WAP_PageStart )
        			.addStringBuffer( wapModel.getUrl() )
        			.addByte( WAP_PAGE_TYPE_REFRESH )//刷新	
        			.writeToRcu(aMsg.getRealTime());
        			
        			showEventNT("");
        			showEvent("WAP Page Start");
        			
    				writeRcuEvent( RcuEventCommand.WAP_GetRequest,aMsg.getRealTime() );
    				showEvent( "WAP Refresh Get Request" );
    			}
    			break;
    			
    			//发送获取页面请求命令成功
    		case HTTP_PAGE_SENDGETCMD_SUCCESS:
    			LogUtil.i(tag, "recv HTTP_PAGE_SENDGETCMD_SUCCESS\r\n");
    			int getUrlDelay = (int) (aMsg.getRealTime() - request ) / 1000;
    			writeRcuEvent(RcuEventCommand.WAP_GetUrlFinished,aMsg.getRealTime(),
    					RcuEventCommand.WAP_GetUrlFinished_Success,
    					getUrlDelay);
    			if( !hasLogon ){
    				showEventNT("WAP Refresh Get URL Success:Delay "+getUrlDelay+"(ms)");
    			}else{
    				showEvent("WAP Refresh Get URL Success:Delay "+getUrlDelay+"(ms)");
    			}
    			break;
    			
    			//发送获取页面请求命令失败
    		case HTTP_PAGE_SENDGETCMD_FAILED:
    			LogUtil.i(tag, "recv HTTP_PAGE_SENDGETCMD_FAILED\r\n");
    			LogUtil.i(tag, aMsg.data + "\r\n");
    			sendCmdFail(aMsg.getRealTime());
    			break;
    			
    			//URL跳转
    		case HTTP_PAGE_URL_REDIRECT:
    			LogUtil.i(tag, "recv HTTP_PAGE_URL_REDIRECT\r\n");
    			LogUtil.i(tag, aMsg.data + "\r\n");
    			String url = aMsg.data.split("::")[1];
    			EventBytes.Builder( WebPage.this,RcuEventCommand.WAP_UrlRedirect)
    			.addCharArray( url.toCharArray(), 2048 )
    			.writeToRcu(aMsg.getRealTime());
    			
    			break;
    			
    			//响应第1个数据(  Reply )
    		case HTTP_PAGE_FIRSTDATA:
    			LogUtil.i(tag, "recv HTTP_PAGE_FIRSTDATA\r\n");
    			//第一次响应为登陆响应,非第一次为刷新响应
    			if( firstData ==0 ){
    				firstData = aMsg.getRealTime();
    				lastQosTime = firstData;
    				int firstDelay = (int ) ( firstData - request ) / 1000;
    				delayGateway = (int)( gatewayFinish - gatewayBegin) / 1000;
        			int total = pdpDelay + firstDelay + delayGateway;
        			EventBytes.Builder( WebPage.this,RcuEventCommand.WAP_FirstDataEx)
        			.addInteger( pdpDelay )
        			.addInteger( delayGateway )
        			.addInteger( firstDelay )
        			.addInteger( total )
        			.writeToRcu(aMsg.getRealTime());
    			}else {
    				firstData = aMsg.getRealTime();
    				lastQosTime = firstData;
    				int firstDelay = (int ) ( firstData - request ) / 1000;
    				delayGateway = (int)( gatewayFinish - gatewayBegin) / 1000;
        			int total = pdpDelay + firstDelay + delayGateway;
        			EventBytes.Builder( WebPage.this,RcuEventCommand.WAP_FirstDataEx)
        			.addInteger( pdpDelay )
        			.addInteger( delayGateway )
        			.addInteger( firstDelay )
        			.addInteger( total )
        			.writeToRcu(aMsg.getRealTime());
    				showEvent( "WAP Refresh Reply" );
    				
    				startPioneerTimer();
    			}
    			break;
    			
    		case HTTP_PAGE_DROP:
    			LogUtil.i(tag, "recv HTTP_PAGE_DROP\r\n");
    			LogUtil.i(tag, aMsg.data );
				int reason = 0;
				try{
					reason = Integer.parseInt( aMsg.data.trim().split("::")[1] );
				}catch(Exception e){
					LogUtil.w(tag, e.toString() );
				}
					
				wapRefreshDrop( (byte)reason,aMsg.getRealTime() );
				
    			break;
    			
    		case HTTP_PAGE_FINISH:
    			LogUtil.i(tag, "recv HTTP_PAGE_FINISH\r\n");
    			//第一次为登陆成功，非第一次为刷新成功
    			if( !hasLogon ){
    				hasLogon = true;
    				lastData = aMsg.getRealTime();
    				int logonDelay = (int) ( lastData - firstData ) / 1000;//A算法(参照前后台规范文档)
    				EventBytes.Builder( WebPage.this, RcuEventCommand.WAP_LastData)
    				.addInteger( (int)toPioneerTransmitByte )
    				.addInteger( logonDelay )
    				.addByte( WAP_PAGE_TYPE_LOGON_BEFORE_FRESH )
    				.writeToRcu(aMsg.getRealTime());
    				//showEventNT("WAP Get URL Success");
    			}else{
    				/*
    				 * 这个文档描述有问题<RCU事件存储结构-20120705.doc>
    				 * 描述为 从WAP_ ConnectGatewayFinished到本事件的毫秒数
    				 * 但是ConnectGatewayFinished一次后会有多次 刷新，所以不能用RCU文档说的方式
    				 * 
    				 * 以这个文档为准<统计参数和指标定义及算法V1.2.5.doc>
    				 * WAP页面刷新时长：从WAP Page Get Request事件到WAP Page LastData为止的时间（包含页面被重新定向或重传的时间）
    				 * 
    				 */
    				lastData = aMsg.getRealTime();
    				int refreshDelay = (int) ( lastData - request ) / 1000;
    				EventBytes.Builder( WebPage.this,RcuEventCommand.WAP_LastData)
    				.addInteger( (int)toPioneerTransmitByte )
    				.addInteger( refreshDelay )
    				.addByte( WAP_PAGE_TYPE_REFRESH )
    				.writeToRcu(aMsg.getRealTime());
    				showEvent("WAP Refresh Finish:Delay "+refreshDelay+"(ms)");
    				
    				//统计
    				totalWapResult(TotalAppreciation._wapRefreshTrys, 1 );
    				totalWapResult(TotalAppreciation._wapRefreshSuccs, 1 );
    				totalWapResult(TotalAppreciation._wapRefreshDelay, refreshDelay );
    				
    			}
    			isInterval = true; 
    			break;
    			
    		case HTTP_PAGE_QUIT:
    			LogUtil.i(tag, "recv HTTP_PAGE_QUIT\r\n");
    			//没有连接网关直接退出时，业务失败
    			if( gatewayBegin ==0 ){
    				fail( FailReason.UNKNOWN.getReasonCode(),aMsg.getRealTime() );
    			}
    			
    			new Thread( new StopCurrentTest( TestService.RESULT_SUCCESS ) ).start();
    			break;
    		
    		}   		
		}
    	
		/**
		 * 以下是文档描述 ?
		 *  WAP刷新Drop判断：在以下几种情况下，都必须插入WAP刷新Drop事件，结束该次WAP登录测试流程；
		 * －WAP获取URL成功但收不到WAP刷新Reply时	(这个应该在库里实现)
		 * －WAP刷新Reply之后在超时时长内收不到WAP刷新最后一个数据包 (这个应该在库里实现，本Handler加超时控制)
		 * －WAP刷新Reply之后服务器直接返回失败 ? (这个应该是库返回)
		 * 
		 * [超时时长]：从WAP Page Request开始，在该时长内，收不到WAP Page Finished，均认为超时。
		 * 
		 * 注：超时、无数据这两个Drop只要引用库回调的就可以，
		 * 用户停导致的drop库也会返回，但可能未返回本进程就被退出，
		 * 
		 * @param reason drop的原因码
		 * */
    	public void wapRefreshDrop(byte reason,long time){
    		
    		
    		if( !hasDrop  ){
    			hasDrop = true;
    			
    			if( request != 0 && !isInterval ){
    				byte type = hasLogon? WAP_PAGE_TYPE_REFRESH:WAP_PAGE_TYPE_LOGON_BEFORE_FRESH;
    				int delay = (int) (time - request ) / 1000;
    				//存储Drop事件
    				EventBytes.Builder( WebPage.this, RcuEventCommand.WAP_Drop)
    				.addInteger( delay )
    				.addInteger( (int)toPioneerTransmitByte )
    				.addByte( reason )
    				.addByte( type )
    				.writeToRcu(time);
    				
        			String dropString =  "WAP Refresh Drop:Delay "+delay+"(ms)," +
							"Transmit Size:"+toPioneerTransmitByte/UtilsMethod.kbyteRage+" KBytes," +
							getDropReasonString(reason) ;
    				
    				if( type == WAP_PAGE_TYPE_LOGON_BEFORE_FRESH ){
    					showEventNT(dropString);
    				}else  if( type == WAP_PAGE_TYPE_REFRESH ){
    					showEvent(dropString);
    					//统计
    					if( reason == RcuEventCommand.DROP_USERSTOP && delay>0 ){
    						totalWapResult(TotalAppreciation._wapRefreshTrys, 1 );
    						totalWapResult(TotalAppreciation._wapRefreshSuccs, 1 );
    						totalWapResult(TotalAppreciation._wapRefreshDelay, delay );
    					}else{
    						totalWapResult(TotalAppreciation._wapRefreshTrys, 1 );
    						totalWapResult(TotalAppreciation._wapRefreshSuccs, 0 );
    					}
    					
    					//结束本次测试
    					new Thread( new StopCurrentTest(  TestService.RESULT_SUCCESS  ) ).start();
    				}
    			}else{
    				//结束本次测试
					new Thread( new StopCurrentTest(  TestService.RESULT_SUCCESS  ) ).start();
    			}
    			
    		}
    	}
    	
    	/**
    	 * 用户停止的Drop
    	 * */
    	public void dropByUserStop(){
    		wapRefreshDrop( (byte)  RcuEventCommand.DROP_USERSTOP,System.currentTimeMillis() * 1000 );
    	}
    	
    	private void connectFail(long time){
			if( !hasFail ){
				hasFail = true;
				
				int delayGF = (int)(time - gatewayBegin ) / 1000;
    			EventBytes.Builder( WebPage.this, RcuEventCommand.WAP_ConnectGatewayFinished)
    			.addInteger( RcuEventCommand.WAP_CONNECTGATEWAY_FAIL )
    			.addInteger( delayGF )
    			.writeToRcu(time);
    			showEventNT("WAP Refresh Connect Gateway Failure:Delay "+delayGF+"(ms)");
				
				
				//停止当前测试
    			new Thread( new StopCurrentTest( TestService.RESULT_FAIL_REDIAL ) ).start();
			}			
		}
		
		
		private void sendCmdFail(long time){
			if( !hasFail ){
				hasFail = true;
				
				int reqFailDealy = (int) (time - request ) / 1000;
    			writeRcuEvent(RcuEventCommand.WAP_GetUrlFinished,time,
    					RcuEventCommand.WAP_GetUrlFinished_Fail,
    					reqFailDealy);
    			if( !hasLogon ){
    				showEventNT("WAP Refresh Get URL Failure:Delay "+reqFailDealy+"(ms)");
    				
    				//刷新前登录失败，结束本次测试重新拨号
    				new Thread( new StopCurrentTest(TestService.RESULT_FAIL_REDIAL) ).start();
    			}else{
    				
    				showEvent( "WAP Refresh Get URL Failure:Delay "+reqFailDealy+"(ms)" );
    				
    				//刷新失败
    				new Thread( new StopCurrentTest( TestService.RESULT_SUCCESS ) ).start();
    			}
			}
		}
		
		/**
		 * http登录业务失败
		 * @param reason
		 */
		protected void fail( int reason,long time ){
			if( !hasFail && !hasDrop && lastData==0 ){
				hasFail = true;
				
				//RCU事件没有定义，这里只显示
//				EventBytes.Builder( mContext, RcuEventCommand.HTTP_Page_Fail)
//				.addInteger( reason )
//				.writeToRcu();
				
				showEvent( String.format("WAP Refresh Failure:%s",
						FailReason.getFailReason(reason).getResonStr() ) );
				
				//结束本次测试
				new Thread( new StopCurrentTest( TestService.RESULT_FAILD ) ).start();
			}
		}

		@Override
		protected void disconnect() {
			
		}
    	
		
	}
	
	
    /**
     * JNI回调:Http刷新业务
     * */
	//used to update ui
	private class HttpLoginHandler extends PageTestHandler
	{
		private long gatewayBegin = 0l;	//连接网关开始时间
		private long gatewayFinish = 0l;//连接网关成功时间
		private long beginRequest = 0l; //发送请求开始时间
		private long firstData = 0l;	//第一个数据的时间
		private boolean hasDrop = false;
		private Timer finishTimer;
		private TimerTask finishTimerTask;
		private boolean hasLastData = false;
		private long lastQosTime = 0;//上次写入QOS的时间
		
		private boolean hasDisConnect = false;
		
		private TaskHttpPageModel httpModel ;
		public HttpLoginHandler(TaskHttpPageModel httpModel){
			this.httpModel = httpModel;
		}
		
		@Override
		public void reset(){
			  gatewayBegin = 0l;	//连接网关开始时间
			  gatewayFinish = 0l;//连接网关成功时间
			  beginRequest = 0l; //发送请求开始时间
			  firstData = 0l;	//第一个数据的时间
			  hasDrop = false;
			  toPioneerTransmitByte = 0l;		//收到的数据大小
			  hasLastData = false;
			  lastQosTime = 0;
		}
		
		@Override
    	public void handleMessage(android.os.Message msg)
    	{
			ipc2msg aMsg = (ipc2msg)msg.obj;
			if(aMsg.test_item != HTTP_PAGE_TEST || hasDrop )
			{
				return;
			}
			
			    		
    		switch(aMsg.event_id)
    		{
    		
    		// 如果qos_inv_ms设置成1000ms,而业务可能在1000ms内完成，这里没有返回文件大小
    		case HTTP_PAGE_QOS:
    			strQos = aMsg.data;
    			LogUtil.d(tag,"---HTTP Login id:" + aMsg.event_id + "--" + aMsg.data +"\n\r");
    			
				//发送大小
				String s = strQos.split("\n")[1].split("::")[1];
				int b = Integer.parseInt( s );
				int internalTime = (int) (aMsg.getRealTime() - lastQosTime) / 1000;
				lastQosTime = aMsg.getRealTime();
				int internalBytes = (int)(b - toPioneerTransmitByte);
				toPioneerInstRate = internalBytes *8 ;
				//已发送大小
				toPioneerTransmitByte = b;
				//写入Ftp_data数据格式
				if( firstData !=0 ){
					int totalTime = (int) (aMsg.getRealTime() - firstData) / 1000;
					UtilsMethod.sendWriteRcuFtpData(getApplicationContext(),
							WalkCommonPara.MsgDataFlag_X,
							0x00, totalTime, toPioneerTransmitByte, internalTime, internalBytes);
					
					sendCurrentRate(strQos);
				}
    			break;
    			
    		case HTTP_PAGE_INITED:
    			LogUtil.i(tag, "HTTP_PAGE_INITED\r\n");
    			LogUtil.i(tag, aMsg.data + "\r\n");
    			//是否要保存文件
    			File file = new File( downloadPath );
    			int do_save = file.exists()? 1:0;
    			//各个参数属性请参考基础库的web_http_jni.h
    			String event_data = "local_if::"+ "" + "\n"//rmnet0
				  + "connect_timeout_ms::" + 15*1000 + "\n"
				  + "login_timeout_ms::" + httpModel.getTimeOut()*1000 + "\n"
				  + "nodata_timeout_ms::" + "180000" + "\n"
				  + "qos_inv_ms::" + 1*1000 + "\n"
				  + "url::" + httpModel.getUrl() + "\n"
				  + "refresh_type::" + "" + "\n"	//1,普通刷新  2,深度刷新
				  + "refresh_times::" + "" + "\n"
				  + "refresh_timeout_ms::" + "" + "\n"
				  + "refresh_delay_ms::" + "" + "\n"
				  + "protol_type::" + PROTOL_TYPE_HTTP + "\n"
				  + "gateway_ip::" + "" + "\n"
				  + "gateway_port::" + "" + "\n"
				  + "save_filepath::" + downloadPath + "\n"
				  + "ps_call::" + 0 + "\n"		//ps_call==1时，超时未完成当结束，ps_call==0,超时未完成当drop
//				  + "transmit_img::" + (httpModel.isDownPicture() ? 1 : 0) + "\n"
				  + "transmit_img::" + 1 + "\n"
				  + "clear_cache::" + 1+ "\n"
				  + "use_gateway::" + 0 + "\n"
				   //2013.4.2 添加新的属性
				  + "gateway_type::" + "" + "\n" //代理类型。 1:HTTP  2:Socks4  3:Socks4A  4:Socks5  5:Socks5Hostname
				  + "gateway_username::" + "" + "\n" //代理用户名
				  + "gateway_password::" + "" + "\n" //代理密码
				  + "user_agent::" + "" + "\n" //模拟手机浏览器（只在WAP下使用）
				  + "parent_processid::" + 0 + "\n"//父进程ID(父进程异常退出，子进程会跟随退出。0：不设定)
				  + "do_save::" + do_save ;
    			LogUtil.i(tag, event_data );
    			aIpc2Jni.send_command(HTTP_PAGE_TEST, HTTP_PAGE_START_TEST, event_data, event_data.length());
    			
    			
    			showEvent( httpModel.getUrl() );
    			//2013.7.17 RCU测试事件存储和显示标准.xls 规定是在Connect之前显示
      			EventBytes.Builder(WebPage.this, RcuEventCommand.HTTP_PageStart)
      			.addStringBuffer( httpModel.getUrl() )
      			.addInteger( HTTP_PAGE_TYPE_LOGON )
      			.writeToRcu(aMsg.getRealTime());
      			showEvent("HTTP Page Start");
				break;
			
    		case HTTP_PAGE_DNSRESOLVE_START:
    			LogUtil.i(tag, "recv HTTP_PAGE_DNSRESOLVE_START");
    			dnsStartTime = aMsg.getRealTime();
    			//写事件
    			EventBytes.Builder(mContext, RcuEventCommand.DNS_LOOKUP_START )
    			.addInteger( RcuEventCommand.NullityRcuValue )
    			.addInteger( RcuEventCommand.TEST_TYPE_HTTP_PAGE )
    			.writeToRcu(aMsg.getRealTime());
    			//显示事件
    			showEvent( "HTTP Page DNS Lookup Start" );
    			break;
    			
    		case HTTP_PAGE_DNSRESOLVE_SUCCESS:
    			LogUtil.i(tag, "recv HTTP_PAGE_DNSRESOLVE_SUCCESS\r\n");
    			LogUtil.i(tag, aMsg.data );
//    			08-22 11:57:31.494: I/WebPage(4819): recv HTTP_PAGE_DNSRESOLVE_SUCCESS
//    			08-22 11:57:31.494: I/WebPage(4819): host::www.baidu.com
//    			08-22 11:57:31.494: I/WebPage(4819): ip::115.239.210.27
    			int dnsDelay = (int) (aMsg.getRealTime() - dnsStartTime) / 1000;
    			String host = aMsg.data.split("\n")[0].split("::")[1];
    			String ip = aMsg.data.split("\n")[1].split("::")[1];
    			//写事件
    			EventBytes.Builder(mContext, RcuEventCommand.DNS_LOOKUP_SUCCESS )
    			.addCharArray( host.toCharArray(), 256 )
    			.addInteger( UtilsMethod.convertIpString2Int(ip) )
    			.addInteger( dnsDelay )
    			.writeToRcu(aMsg.getRealTime());
    			//显示事件
    			showEvent( 
    					String.format("HTTP Page DNS Lookup Success:%s,Delay %d(ms)",
    							ip,dnsDelay )
    			);
    			break;
    			
    		case HTTP_PAGE_DNSRESOLVE_FAILED:
    			LogUtil.i(tag, "recv HTTP_PAGE_DNSRESOLVE_FAILED\r\n");
    			LogUtil.i(tag, aMsg.data );
//    			08-22 13:46:42.975: I/WebPage(6405): reason::1000
//    			08-22 13:46:42.975: I/WebPage(6405): desc::unknown error
    			dnsDelay = (int) (aMsg.getRealTime() - dnsStartTime) / 1000;
    			int dnsFailReason = 0;
    			try{
    				dnsFailReason = Integer.parseInt( aMsg.data.split("\n")[0].split("::")[1] );
    			}catch(Exception e){
    				
    			}
    			EventBytes.Builder(mContext,  RcuEventCommand.DNS_LOOKUP_FAILURE )
    			.addCharArray( httpModel.getUrl().toCharArray(), 256)
    			.addInteger( dnsDelay )
    			.addInteger( dnsFailReason )
    			.writeToRcu(aMsg.getRealTime());
    			showEvent( 
    					String.format("HTTP Page DNS Lookup Failure:Delay %d(ms),%s",
    							dnsDelay,FailReason.getFailReason(dnsFailReason).getResonStr() )
    			);
    			break; 	
				
				//连接网关开始
    		case HTTP_PAGE_CONNECT_START:
    			LogUtil.i(tag, "recv HTTP_PAGE_CONNECT_START\r\n");
    			gatewayBegin = aMsg.getRealTime();
    			writeRcuEvent( RcuEventCommand.HTTP_SockConnecting,aMsg.getRealTime() );
    			showEvent("HTTP Page Connect" );
    			//网关连接到登陆成功的超时
    			startFinishTimer( httpModel.getTimeOut());
    			//开始pioneer计时
    			startPioneerTimer();
    			break;
    			
    			//连接网关成功
    		case HTTP_PAGE_CONNECT_SUCCESS:
    			LogUtil.i(tag, "recv HTTP_PAGE_CONNECT_SUCCESS\r\n");
    			gatewayFinish = aMsg.getRealTime();
    			int delayGateway = (int)( gatewayFinish - gatewayBegin) / 1000;
    			writeRcuEvent( RcuEventCommand.HTTP_SockSuccess,aMsg.getRealTime(),delayGateway );
    			showEvent( "HTTP Page Connect Success" );
    			//
    			break;
    			
    			//连接网关失败
    		case HTTP_PAGE_CONNECT_FAILED:
    			LogUtil.i(tag, "recv HTTP_PAGE_CONNECT_FAILED\r\n");
    			LogUtil.i(tag, aMsg.data + "\r\n");
    			// 连接失败的原因
    			int failReason = 0;
    			try{
    				failReason = Integer.parseInt( aMsg.data.split("::")[1] );
    			}catch(Exception e){
    				
    			}
    			connectFail( failReason,aMsg.getRealTime() );
    			break;
    			
    			//发送获取页面请求
    		case HTTP_PAGE_SENDGETCMD:
    			LogUtil.i(tag, "recv HTTP_PAGE_SENDGETCMD\r\n");
    			beginRequest = aMsg.getRealTime();
    			writeRcuEvent( RcuEventCommand.HTTP_SendGetCmd,aMsg.getRealTime() );
    			showEvent( "HTTP Page Logon Request" );
    			break;
    			
    			//获取页面成功
    		case HTTP_PAGE_SENDGETCMD_SUCCESS:
    			LogUtil.i(tag, "recv HTTP_PAGE_SENDGETCMD_SUCCESS\r\n");
    			break;
    			
    			//获取页面失败
    		case HTTP_PAGE_SENDGETCMD_FAILED:
    			LogUtil.i(tag, "recv HTTP_PAGE_SENDGETCMD_FAILED\r\n");
    			LogUtil.i(tag, aMsg.data + "\r\n");
    			sendCmdFail(aMsg.getRealTime());
    			break;
    			
    			//URL跳转
    		case HTTP_PAGE_URL_REDIRECT:
    			LogUtil.i(tag, "recv HTTP_PAGE_URL_REDIRECT\r\n");
    			LogUtil.i(tag, aMsg.data + "\r\n");
    			String url = aMsg.data.split("::")[1];
    			EventBytes.Builder( WebPage.this,RcuEventCommand.HTTP_UrlRedirect)
    			.addCharArray( url.toCharArray(), 2048 )
    			.writeToRcu(aMsg.getRealTime());
    			
    			break;
    			
    			//响应第1个数据
    		case HTTP_PAGE_FIRSTDATA:
    			LogUtil.i(tag, "recv HTTP_PAGE_FIRSTDATA\r\n");
    			firstData = aMsg.getRealTime();
    			lastQosTime = firstData;//以firstData之后才写入QOS
    			int firstDelay = (int ) ( firstData - beginRequest ) / 1000;
    			writeRcuEvent( RcuEventCommand.HTTP_FirstData,aMsg.getRealTime(),firstDelay );
    			showEvent( "HTTP Page Logon Reply" );
    			break;
    			
    		case HTTP_PAGE_MAINPAGE_OK:
    			LogUtil.i(tag, "recv HTTP_PAGE_MAINPAGE_OK\r\n");
    			LogUtil.i(tag, aMsg.data);
//    			09-04 16:41:20.055: I/WebPage(13772): recv HTTP_PAGE_MAINPAGE_OK
//    			09-04 16:41:20.055: I/WebPage(13772): title:
//    			09-04 16:41:20.055: I/WebPage(13772): content_type::text/html; charset=gb2312
//    			09-04 16:41:20.055: I/WebPage(13772): size::523
    			//处理中文乱码
    			long size = 0;
    			try{
    				String sizeStr = getResponseResult(aMsg.data,"size" );
    				size = Long.parseLong( sizeStr );
    			}catch(Exception e){
    				LogUtil.e( tag, e.toString() );
    			}
    			LogUtil.e(tag,"main page size:"+size);
    			int delayMainPage = (int) (aMsg.getRealTime() - firstData) / 1000;
    			EventBytes.Builder(mContext, RcuEventCommand.HTTP_MainPageOk )
    			.addInteger( delayMainPage )
    			.addInt64( size )
    			.writeToRcu(aMsg.getRealTime());
    			showEvent("HTTP Page MainPage Ok");
    			break;
    			
    		case HTTP_PAGE_DROP:
    			LogUtil.i(tag, "recv HTTP_PAGE_DROP\r\n");
    			LogUtil.i(tag, aMsg.data );
    			int reason = 0;
    			try{
    				reason = Integer.parseInt( aMsg.data.split("::")[1] );
    			}catch(Exception e){
    				
    			}
				httpLogonDrop( reason ,aMsg.getRealTime());
    			break;
    			
    		case HTTP_PAGE_FINISH:
    			LogUtil.i(tag, "recv HTTP_PAGE_FINISH\r\n");
    			hasLastData = true;
    			int connectTime = (int) ( aMsg.getRealTime() - beginRequest ) / 1000;
    			int transmitTime = (int) (aMsg.getRealTime() - firstData ) / 1000;
    			EventBytes.Builder( WebPage.this,RcuEventCommand.HTTP_LastData )
    			.addInteger( (int)toPioneerTransmitByte )
    			.addInteger( connectTime )
    			.addInteger( transmitTime )
    			.addInteger( HTTP_PAGE_TYPE_LOGON )
    			.addCharArray( httpModel.getUrl().toCharArray() , 256 )
    			.writeToRcu(aMsg.getRealTime());
    			showEvent( String.format( "HTTP Page Logon Success:Delay %.3f(s)",(float)connectTime/1000 ) );
    			
    			//统计
   			 	HashMap<String, TotalSpecialModel> map =new HashMap<String, TotalSpecialModel>();
    			totalHttpResult(map,TotalHttpType.HTTPLogon,httpModel.getUrl(),TotalAppreciation._HttpTry, 1);
    			totalHttpResult(map,TotalHttpType.HTTPLogon,httpModel.getUrl(),TotalAppreciation._HttpSuccess,1);
    			totalHttpResult(map,TotalHttpType.HTTPLogon,httpModel.getUrl(),TotalAppreciation._HttpDelay ,connectTime );
    			LogUtil.i(tag, "delay:"+connectTime );
    			Message msgChart = callbackHandler.obtainMessage( CHART_CHANGE, map );
    			callbackHandler.sendMessage( msgChart );
    			break;
    			
    		case HTTP_PAGE_QUIT:
    			LogUtil.i(tag, "recv HTTP_PAGE_QUIT\r\n");
    			
    			//没有lastdata 和drop 直接退出时报Fail
    			if( !hasDrop && !hasLastData ){
    				fail( FailReason.UNKNOWN.getReasonCode(),aMsg.getRealTime() );
    			}
    			new Thread( new StopCurrentTest( TestService.RESULT_SUCCESS ) ).start();
    			break;
    		
    		}   		
		}

		/**
		 * 以下是文档描述
		 * WAP登录Drop判断：在以下几种情况下，都必须插入WAP登录Drop事件，结束该次WAP登录测试流程；
		 * WAP获取URL成功但收不到WAP登录Reply时，WAP库返回Drop 
		 * 在超时时长内收不到WAP登录最后一个数据包
		 * WAP登录Reply之后服务器直接返回失败 ? 
		 * 
		 * WAP超时时长：从WAP Page Connect Gateway Request开始计时，收不到最后一个数据包，均认为超时。
		 * 
		 * 保证所有重复的drop只有一次
		 * @param reason drop的原因码
		 * */
    	public void httpLogonDrop(int reason,long time){
    		if( !hasDrop && !hasLastData && gatewayBegin!=0 && !hasFail){
    			hasDrop = true;
    			int delay = (int) (time - gatewayBegin) / 1000;
    			//存储Drop事件
    			EventBytes.Builder( WebPage.this, RcuEventCommand.HTTP_Drop)
    			.addInteger( delay )
    			.addInteger( (int)toPioneerTransmitByte )
    			.addInteger( reason )
    			.addInteger( HTTP_PAGE_TYPE_LOGON )
    			.addCharArray(httpModel.getUrl().toCharArray(), 256)
    			.writeToRcu(time);
    			
    			showEvent( "HTTP Page Logon Drop:Delay "+delay+"(ms)," +
    					"Transmit Size:"+toPioneerTransmitByte/UtilsMethod.kbyteRage+" KBytes," +
    					getDropReasonString(reason) );
    			
    			//统计
   			 	HashMap<String, TotalSpecialModel> map =new HashMap<String, TotalSpecialModel>();
   			 	if( reason == RcuEventCommand.DROP_USERSTOP && delay>0){
   			 		totalHttpResult(map,TotalHttpType.HTTPLogon,httpModel.getUrl(),TotalAppreciation._HttpTry, 1 );
   			 		totalHttpResult(map,TotalHttpType.HTTPLogon,httpModel.getUrl(),TotalAppreciation._HttpSuccess,1 );
   	    			totalHttpResult(map,TotalHttpType.HTTPLogon,httpModel.getUrl(),TotalAppreciation._HttpDelay ,delay );
   			 	}else{
   			 		totalHttpResult(map,TotalHttpType.HTTPLogon,httpModel.getUrl(),TotalAppreciation._HttpTry, 1);
   			 		totalHttpResult(map,TotalHttpType.HTTPLogon,httpModel.getUrl(),TotalAppreciation._HttpSuccess,0);
   			 	}
    			Message msgChart = callbackHandler.obtainMessage( CHART_CHANGE, map );
    			callbackHandler.sendMessage( msgChart );
    			
    			//结束本次测试
				new Thread( new StopCurrentTest( TestService.RESULT_SUCCESS ) ).start();
    		}
    	}
    	
    	 /**等待完成计时器*/
        private void startFinishTimer( int timeOut){
    		if( finishTimer!= null && finishTimerTask!= null){
    			finishTimer.cancel();
    			finishTimerTask.cancel();
    			finishTimer = null;
    			finishTimerTask = null;
    		}
    		finishTimer = new Timer();
    		finishTimerTask = new TimerTask(){
    			@Override
    			public void run() {

    				if( !hasLastData ){
    					LogUtil.i(tag, "receive data timout");
    					httpLogonDrop( RcuEventCommand.DROP_TIMEOUT,System.currentTimeMillis() );
    				}
    			}
    			
    		};
    		finishTimer.schedule( finishTimerTask, timeOut * 1000 );
    	}


		@Override
		public void dropByUserStop() {
			this.httpLogonDrop(  RcuEventCommand.DROP_USERSTOP,System.currentTimeMillis() * 1000  );
		}
		
		
		private void connectFail(int reason,long time ){
			if( !hasFail ){
				hasFail = true;
				int delayCF = (int)(time - gatewayBegin ) / 1000;
    			writeRcuEvent( RcuEventCommand.HTTP_SockFailure,time,delayCF,reason );
    			showEvent( String.format( "HTTP Page Connect Failure:%s",
    					FailReason.getFailReason(reason).getResonStr() ) );
    			//停止当前测试
    			new Thread( new StopCurrentTest( TestService.RESULT_FAILD ) ).start();
			}			
		}
		
		
		private void sendCmdFail(long time){
			if( !hasFail ){
				hasFail = true;
				writeRcuEvent(RcuEventCommand.HTTP_SendGetCmdFail,time );
    			showEvent( "HTTP Page Logon CMD Failure" );
    			
    			HashMap<String, TotalSpecialModel> map1 =new HashMap<String, TotalSpecialModel>();
    			totalHttpResult(map1,TotalHttpType.HTTPLogon,httpModel.getUrl(),TotalAppreciation._HttpTry, 1);
    			totalHttpResult(map1,TotalHttpType.HTTPLogon,httpModel.getUrl(),TotalAppreciation._HttpSuccess,0);
    			Message msgChart1 = callbackHandler.obtainMessage( CHART_CHANGE, map1 );
    			callbackHandler.sendMessage( msgChart1 );
    			
    			//停止当前测试
    			new Thread( new StopCurrentTest( TestService.RESULT_SUCCESS ) ).start();
			}
		}
		
		/**
		 * http登录业务失败
		 * @param reason
		 */
		protected void fail( int reason,long time ){
			if( !hasFail && !hasDrop && firstData==0 ){
				hasFail = true;
				
				EventBytes.Builder( mContext, RcuEventCommand.HTTP_Page_Fail)
				.addInteger( reason )
				.writeToRcu(time);
				
				showEvent( String.format("HTTP Page Logon Failure:%s",
						FailReason.getFailReason(reason).getResonStr() ) );
				
				//结束本次测试
				new Thread( new StopCurrentTest( TestService.RESULT_FAILD ) ).start();
			}
		}
		
		/**
		 * 断开连接
		 */
		protected void disconnect(){
			if( !hasDisConnect && gatewayFinish!=0 ){
				hasDisConnect = true;
				
				//存储事件
    			EventBytes.Builder( WebPage.this,
    					RcuEventCommand.HTTP_SocketDisconnected).writeToRcu(System.currentTimeMillis() * 1000);
    			//显示事件
    			showEvent("HTTP Page Disconnect");
			}
		}
		
	};
	
    /**JNI回调*/
	//used to update ui
	private class HttpRefreshHandler extends PageTestHandler
	{
		private long gatewayBegin = 0l;	//连接网关开始时间
		private long gatewayFinish = 0l;//连接网关成功时间
		private long request = 0l; //发送请求开始时间
		private long firstData = 0l;	//第一个数据的时间
		private long lastQosTime = 0;  //上次写入QOS的时间
		private long lastData = 0l;
		private boolean hasDrop = false;
		private boolean hasLogon = false;
		
		private boolean isInterval = false;//是否在间隔时间内(处理刷新间隔中手工停止会drop的问题)
		
		private boolean hasDisConnect = false;
		private String curUrl;//当前刷新的网址
		
		private TaskHttpPageModel httpModel;
		public HttpRefreshHandler(TaskHttpPageModel httpModel){
			this.httpModel = httpModel;
		}
		
		/**重设相关变量*/
		public void reset(){
			  gatewayBegin = 0l;	//连接网关开始时间
			  gatewayFinish = 0l;//连接网关成功时间
			  request = 0l; //发送请求开始时间
			  firstData = 0l;	//第一个数据的时间
			  lastData = 0l;
			  hasDrop = false;
			  toPioneerTransmitByte = 0l;		//收到的数据大小
			  hasLogon = false;
			  lastQosTime = 0;
		}
		
    	public void handleMessage(android.os.Message msg)
    	{
			ipc2msg aMsg = (ipc2msg)msg.obj;
			if(aMsg.test_item != HTTP_PAGE_TEST   || hasDrop )
			{
				LogUtil.w(tag, "---not testing");
				return;
			}
			    		
			LogUtil.d(tag,"---HTTP Refresh id:" + aMsg.event_id + "--" + aMsg.data +"\n\r");
    		switch(aMsg.event_id)
    		{
    		// 如果qos_inv_ms设置成1000ms,而业务可能在1000ms内完成，这里没有返回文件大小
    		case HTTP_PAGE_QOS:
    			strQos = aMsg.data;
    			
				//接收大小
				String s = strQos.split("\n")[1].split("::")[1];
				int b = Integer.parseInt( s );
				int internalTime = (int) (aMsg.getRealTime() - lastQosTime) / 1000;
				lastQosTime = aMsg.getRealTime();
				int internalBytes = (int)(b - toPioneerTransmitByte);
				toPioneerInstRate = internalBytes*8 ;
				//已发送大小
				toPioneerTransmitByte = b;
				
				//写入Ftp_data数据格式
				if( firstData !=0 && firstData > request ){//确保是firstData之后的QOS
					int totalTime = (int) (aMsg.getRealTime() - firstData) / 1000;
					UtilsMethod.sendWriteRcuFtpData(getApplicationContext(),
							WalkCommonPara.MsgDataFlag_X,
							0x00, totalTime, toPioneerTransmitByte, internalTime, internalBytes);
					
					sendCurrentRate(strQos);
				}
    			break;
    			
    		case HTTP_PAGE_INITED:
    			LogUtil.i(tag, "HTTP_PAGE_INITED\r\n");
    			LogUtil.i(tag, aMsg.data + "\r\n");
    			
    			//是否要保存文件
    			File file = new File( downloadPath );
    			int do_save = file.exists()? 1:0;
    			//各个参数属性请参考基础库的web_http_jni.h
    			String event_data = "local_if::" +""+ "\n"//rmnet0
				  + "connect_timeout_ms::" + 15*1000 + "\n"
				  + "login_timeout_ms::" + httpModel.getTimeOut()*1000 + "\n"
				  + "nodata_timeout_ms::" + "180000" + "\n"
				  + "qos_inv_ms::" + 1*1000 + "\n"
				  + "url::" + httpModel.getUrl() + "\n"
				  + "refresh_type::" + httpModel.getHttpRefreshType() + "\n"	//1,普通刷新  2,深度刷新
				  + "refresh_times::" +  (httpModel.getRepeat() * (httpModel.getHttpRefreshType() == TaskHttpPageModel.REFRESH_TYPE_HOME_PAGE ? 1 : httpModel.getHttpRefreshDepth())) + "\n"
				  + "refresh_timeout_ms::" + httpModel.getTimeOut()*1000 + "\n"
				  + "refresh_delay_ms::" + httpModel.getInterVal()*1000 + "\n"
				  + "protol_type::" + PROTOL_TYPE_HTTP + "\n"
				  + "gateway_ip::" + ""+ "\n"
				  + "gateway_port::" + "" + "\n"
				  + "save_filepath::" + downloadPath + "\n"
				  + "ps_call::" + 0 + "\n"	//ps_call==1时，超时未完成当结束，ps_call==0,超时未完成当drop
//				  + "transmit_img::" + (httpModel.isDownPicture() ? 1 : 0) + "\n"
				  + "transmit_img::" + 1 + "\n"
				  + "clear_cache::" + 1+ "\n"
				  + "use_gateway::" + 0 + "\n"
				    //2013.4.2 添加新的属性
				  + "gateway_type::" + "" + "\n" //代理类型。 1:HTTP  2:Socks4  3:Socks4A  4:Socks5  5:Socks5Hostname
				  + "gateway_username::" + "" + "\n" //代理用户名
				  + "gateway_password::" + "" + "\n" //代理密码
				  + "user_agent::" + "" + "\n" //模拟手机浏览器（只在WAP下使用）
				  + "refresh_depth::" + httpModel.getHttpRefreshDepth() + "\n" //刷新深度 //业务模板未有此属性
				  + "parent_processid::" + 0 + "\n"//父进程ID(父进程异常退出，子进程会跟随退出。0：不设定)
				  + "do_save::" + do_save ;
    			LogUtil.i(tag, event_data );
    			aIpc2Jni.send_command(HTTP_PAGE_TEST, HTTP_PAGE_START_TEST, event_data, event_data.length());
    			
    			repeatTimes = 1;
    			
    			//2013.7.17 RCU测试事件存储和显示标准.xls 规定是在Connect之前显示
    			showEventNT( httpModel.getUrl() );
      			EventBytes.Builder(WebPage.this, RcuEventCommand.HTTP_PageStart)
      			.addStringBuffer( httpModel.getUrl() )
      			.addInteger( HTTP_PAGE_TYPE_LOGON_BEFORE_FRESH )
      			.writeToRcu(aMsg.getRealTime());
      			showEventNT("HTTP Page Start");
				break;
				
    		case HTTP_PAGE_DNSRESOLVE_START:
    			LogUtil.i(tag, "recv HTTP_PAGE_DNSRESOLVE_START");
    			dnsStartTime = aMsg.getRealTime();
    			//写事件
    			EventBytes.Builder(mContext, RcuEventCommand.DNS_LOOKUP_START )
    			.addInteger( RcuEventCommand.NullityRcuValue )
    			.addInteger( RcuEventCommand.TEST_TYPE_HTTP_PAGE )
    			.writeToRcu(aMsg.getRealTime());
    			//显示事件
    			showEventNT( "HTTP Page DNS Lookup Start" );
    			break;
    			
    		case HTTP_PAGE_DNSRESOLVE_SUCCESS:
    			LogUtil.i(tag, "recv HTTP_PAGE_DNSRESOLVE_SUCCESS\r\n");
    			LogUtil.i(tag, aMsg.data );
//    			08-22 11:57:31.494: I/WebPage(4819): recv HTTP_PAGE_DNSRESOLVE_SUCCESS
//    			08-22 11:57:31.494: I/WebPage(4819): host::www.baidu.com
//    			08-22 11:57:31.494: I/WebPage(4819): ip::115.239.210.27
    			int dnsDelay = (int) (aMsg.getRealTime() - dnsStartTime) / 1000;
    			String host = aMsg.data.split("\n")[0].split("::")[1];
    			String ip = aMsg.data.split("\n")[1].split("::")[1];
    			//写事件
    			EventBytes.Builder(mContext, RcuEventCommand.DNS_LOOKUP_SUCCESS )
    			.addCharArray( host.toCharArray(), 256 )
    			.addInteger( UtilsMethod.convertIpString2Int(ip) )
    			.addInteger( dnsDelay )
    			.writeToRcu(aMsg.getRealTime());
    			//显示事件
    			showEventNT( 
    					String.format("HTTP Page DNS Lookup Success:%s,Delay %d(ms)",
    							ip,dnsDelay )
    			);
    			break;
    			
    		case HTTP_PAGE_DNSRESOLVE_FAILED:
    			LogUtil.i(tag, "recv HTTP_PAGE_DNSRESOLVE_FAILED\r\n");
    			LogUtil.i(tag, aMsg.data );
//    			08-22 13:46:42.975: I/WebPage(6405): reason::1000
//    			08-22 13:46:42.975: I/WebPage(6405): desc::unknown error
    			dnsDelay = (int) (aMsg.getRealTime() - dnsStartTime) / 1000;
    			int dnsFailReason = 0;
    			try{
    				dnsFailReason = Integer.parseInt( aMsg.data.split("\n")[0].split("::")[1] );
    			}catch(Exception e){
    				
    			}
    			EventBytes.Builder(mContext,  RcuEventCommand.DNS_LOOKUP_FAILURE )
    			.addCharArray( httpModel.getUrl().toCharArray(), 256)
    			.addInteger( dnsDelay )
    			.addInteger( dnsFailReason )
    			.writeToRcu(aMsg.getRealTime());
    			showEventNT( 
    					String.format("HTTP Page DNS Lookup Failure:Delay %d(ms),%s",
    							dnsDelay,FailReason.getFailReason(dnsFailReason).getResonStr() )
    			);
    			break;   			
			
				//连接网关开始
    		case HTTP_PAGE_CONNECT_START:
    			LogUtil.i(tag, "recv HTTP_PAGE_CONNECT_START\r\n");
    			gatewayBegin = aMsg.getRealTime();
    			writeRcuEvent( RcuEventCommand.HTTP_SockConnecting,aMsg.getRealTime() );
    			showEventNT("HTTP Page Connect" );
    			break;
    			
    			//连接网关成功
    		case HTTP_PAGE_CONNECT_SUCCESS:
    			LogUtil.i(tag, "recv HTTP_PAGE_CONNECT_SUCCESS\r\n");
    			gatewayFinish = aMsg.getRealTime();
    			int delayGateway = (int)( gatewayFinish - gatewayBegin) / 1000;
    			writeRcuEvent( RcuEventCommand.HTTP_SockSuccess,aMsg.getRealTime(),delayGateway);
    			showEventNT( "HTTP Page Connect Success" );
    			break;
    			
    			//连接网关失败
    		case HTTP_PAGE_CONNECT_FAILED:
    			LogUtil.i(tag, "recv HTTP_PAGE_CONNECT_FAILED\r\n");
    			LogUtil.i(tag, aMsg.data + "\r\n");
    			// 连接失败的原因
    			int failReason = 0;
    			try{
    				failReason = Integer.parseInt( aMsg.data.split("::")[1] );
    			}catch(Exception e){
    				
    			}
    			connectFail( failReason,aMsg.getRealTime() );
    			break;
    			
    			//发送获取页面请求 ( Request ) 
    		case HTTP_PAGE_SENDGETCMD:
    			LogUtil.i(tag, "recv HTTP_PAGE_SENDGETCMD:" + repeatTimes);
    			setTaskCurrentTimes(repeatTimes ++);
    			
    			//重设定接收数据大小 
    			toPioneerTransmitByte = 0;
    			isInterval = false;
    			//第1次是登陆请求，非第1次是刷新请求
    			if( request==0 ){
    				hasLogon = false;
    				showEventNT( "HTTP Page Logon Request" );
    			}else{
					curUrl = aMsg.data.split("::")[1];
    				EventBytes.Builder(WebPage.this, RcuEventCommand.HTTP_PageStart)
          			.addStringBuffer(curUrl)
          			.addInteger( HTTP_PAGE_TYPE_REFRESH )
          			.writeToRcu(aMsg.getRealTime());
    				
    				showEventNT("");
          			showEvent("HTTP Page Start");
    				
    				showEvent( "HTTP Page Refresh Request" );
    			}
    			
    			request = aMsg.getRealTime();
    			writeRcuEvent( RcuEventCommand.HTTP_SendGetCmd,aMsg.getRealTime() );
    			break;
    			
    			//发送获取页面请求命令成功
    		case HTTP_PAGE_SENDGETCMD_SUCCESS:
    			LogUtil.i(tag, "recv HTTP_PAGE_SENDGETCMD_SUCCESS\r\n");
    			break;
    			
    			//发送获取页面请求命令失败
    		case HTTP_PAGE_SENDGETCMD_FAILED:
    			LogUtil.i(tag, "recv HTTP_PAGE_SENDGETCMD_FAILED\r\n");
    			LogUtil.i(tag, aMsg.data + "\r\n");
    			sendCmdFail(aMsg.getRealTime());
    			break;
    			
    			//URL跳转
    		case HTTP_PAGE_URL_REDIRECT:
    			LogUtil.i(tag, "recv HTTP_PAGE_URL_REDIRECT\r\n");
    			LogUtil.i(tag, aMsg.data + "\r\n");
				curUrl = aMsg.data.split("::")[1];
    			EventBytes.Builder( WebPage.this,RcuEventCommand.HTTP_UrlRedirect)
    			.addCharArray( curUrl.toCharArray(), 2048 )
    			.writeToRcu(aMsg.getRealTime());
    			
    			break;
    			
    			//响应第1个数据(  Reply )
    		case HTTP_PAGE_FIRSTDATA:
    			LogUtil.i(tag, "recv HTTP_PAGE_FIRSTDATA\r\n");
    			//第一次响应为登陆响应,非第一次为刷新响应
    			if( !hasLogon ){
    				firstData = aMsg.getRealTime();
    				lastQosTime = firstData;
    				int firstDelay = (int ) ( firstData - request ) / 1000;
    				writeRcuEvent( RcuEventCommand.HTTP_FirstData,aMsg.getRealTime(),firstDelay );
    				showEventNT("HTTP Page Logon Reply");
    			}else {
    				firstData = aMsg.getRealTime();
    				lastQosTime = firstData;
    				int firstDelay = (int ) ( firstData - request ) / 1000;
    				writeRcuEvent( RcuEventCommand.HTTP_FirstData,aMsg.getRealTime(),firstDelay );
    				showEvent( "HTTP Page Refresh Reply" );
    				
    				//开始pioneer计时
        			startPioneerTimer();
    			}
    			break;
    			
    		case HTTP_PAGE_MAINPAGE_OK:
    			LogUtil.i(tag, "recv HTTP_PAGE_MAINPAGE_OK\r\n");
    			LogUtil.i(tag, aMsg.data );
    			long size = 0;
    			try{
    				String sizeStr = getResponseResult(aMsg.data,"size" );
    				size = Long.parseLong( sizeStr );
    			}catch(Exception e){
    				LogUtil.e( tag, e.toString() );
    			}
    			LogUtil.e(tag,"main page size:"+size);
    			int delayMainPage = (int) (aMsg.getRealTime() - firstData) / 1000;
    			EventBytes.Builder(mContext, RcuEventCommand.HTTP_MainPageOk )
    			.addInteger( delayMainPage )
    			.addInt64( size )
    			.writeToRcu(aMsg.getRealTime());
    			if( !hasLogon ){
    				showEventNT("HTTP Page MainPage Ok");
    			}else{
    				showEvent("HTTP Page MainPage Ok");
    			}
    			break;
    			
    		case HTTP_PAGE_DROP:
    			LogUtil.i(tag, "recv HTTP_PAGE_DROP\r\n");
    			LogUtil.i(tag, aMsg.data );
				int reason = 0;
				try{
					reason = Integer.parseInt( aMsg.data.trim().split("::")[1] );
				}catch(Exception e){
					LogUtil.w(tag, e.toString() );
				}
				//刷新的Drop
				httpRefreshDrop( reason,aMsg.getRealTime());
    			break;
    			
    		case HTTP_PAGE_FINISH:
    			LogUtil.i(tag, "recv HTTP_PAGE_FINISH\r\n");
    			isInterval = true;
    			lastData = aMsg.getRealTime();
				int connectTime = (int) ( lastData - request ) / 1000;
    			int transmitTime = (int) ( lastData - firstData ) / 1000;
    			//第一次为登陆成功，非第一次为刷新成功
    			if( !hasLogon ){
    				hasLogon = true;
    				EventBytes.Builder( WebPage.this,RcuEventCommand.HTTP_LastData )
        			.addInteger( (int)toPioneerTransmitByte )
        			.addInteger( connectTime )
        			.addInteger( transmitTime )
        			.addInteger( HTTP_PAGE_TYPE_LOGON_BEFORE_FRESH )
        			.addCharArray( httpModel.getUrl().toCharArray() , 256 )
        			.writeToRcu(aMsg.getRealTime());
    				showEventNT( String.format( "HTTP Page Logon Success:Delay %.3f(s)"
    						,(float)connectTime/1000  )
    				);
    			}else{
    				/*
    				 * 这个文档描述有问题<RCU事件存储结构-20120705.doc>
    				 * 以这个文档为准<统计参数和指标定义及算法V1.2.5.doc>
    				 * WAP页面刷新时长：从WAP Page Get Request事件到WAP Page LastData为止的时间（包含页面被重新定向或重传的时间）
    				 */
        			EventBytes.Builder( WebPage.this,RcuEventCommand.HTTP_LastData )
        			.addInteger( (int)toPioneerTransmitByte )
        			.addInteger( connectTime )
        			.addInteger( transmitTime )
        			.addInteger( HTTP_PAGE_TYPE_REFRESH )
        			.addCharArray( curUrl.toCharArray() , 256 )
        			.writeToRcu(aMsg.getRealTime());
    				showEvent( String.format( "HTTP Page Refresh Success:Delay %.3f(s)",
    							(float)transmitTime /1000 
    							) 
    				);
    				
    				//统计()
    				HashMap<String, TotalSpecialModel> map =new HashMap<String, TotalSpecialModel>();
    				totalHttpResult(map,TotalHttpType.HTTPRefresh,httpModel.getUrl(),TotalAppreciation._HttpRefreshTry, 1);
    				totalHttpResult(map,TotalHttpType.HTTPRefresh,httpModel.getUrl(),TotalAppreciation._HttpRefreshSuccess,1);
    				totalHttpResult(map,TotalHttpType.HTTPRefresh,httpModel.getUrl(),TotalAppreciation._HttpRefreshDelay ,transmitTime );
    				LogUtil.i(tag, "delay:"+transmitTime );
    				Message msgChart = callbackHandler.obtainMessage( CHART_CHANGE, map );
        			callbackHandler.sendMessage( msgChart );
    			}
    			
    			//重置firstData时间为0
    			firstData = 0;
    			break;
    			
    		case HTTP_PAGE_QUIT:
    			LogUtil.i(tag, "recv HTTP_PAGE_QUIT\r\n");
    			
    			//没有连接网关直接退出时，业务失败
    			if( gatewayBegin == 0 ){
    				fail( FailReason.UNKNOWN.getReasonCode(),aMsg.getRealTime() );
    			}
    			
    			new Thread( new StopCurrentTest( TestService.RESULT_SUCCESS ) ).start();
    			break;
    		
    		}   		
		}

		/**
		 * 以下是文档描述 ?
		 *  WAP刷新Drop判断：在以下几种情况下，都必须插入WAP刷新Drop事件，结束该次WAP登录测试流程；
		 * －WAP获取URL成功但收不到WAP刷新Reply时	(这个应该在库里实现)
		 * －WAP刷新Reply之后在超时时长内收不到WAP刷新最后一个数据包 (这个应该在库里实现，本Handler加超时控制)
		 * －WAP刷新Reply之后服务器直接返回失败 ? (这个应该是库返回)
		 * 
		 * [超时时长]：从WAP Page Request开始，在该时长内，收不到WAP Page Finished，均认为超时。
		 * 
		 * 注：超时、无数据这两个Drop只要引用库回调的就可以，
		 * 用户停导致的drop库也会返回，但可能未返回本进程就被退出，
		 * 
		 * @param reason drop的原因码
		 * */
    	public void httpRefreshDrop(int reason,long time ){
    		if( !hasDrop ){
    			hasDrop = true;
    			
    			if( firstData != 0 && !isInterval && !hasFail ){
    				int type = hasLogon? HTTP_PAGE_TYPE_REFRESH: HTTP_PAGE_TYPE_LOGON_BEFORE_FRESH;
    				int delay = (int) (time - firstData ) / 1000;
    				//存储Drop事件
    				EventBytes.Builder( WebPage.this, RcuEventCommand.HTTP_Drop)
    				.addInteger( delay )
    				.addInteger( (int)toPioneerTransmitByte )
    				.addInteger( reason )
    				.addInteger( type )
    				.addCharArray(httpModel.getUrl().toCharArray(), 256)
    				.writeToRcu(time);
    				
    				if( type == HTTP_PAGE_TYPE_LOGON_BEFORE_FRESH ){
    					showEventNT("HTTP Page Logon Drop:Delay "+delay+"(ms)," +
    							"Transmit Size:"+toPioneerTransmitByte/UtilsMethod.kbyteRage+" KBytes," +
    							getDropReasonString(reason) );
						//结束本次测试
						new Thread( new StopCurrentTest( TestService.RESULT_FAIL_REDIAL ) ).start();
    				}else  if( type == HTTP_PAGE_TYPE_REFRESH ){
    					showEvent( "HTTP Page Refresh Drop:Delay "+delay+"(ms)," +
    							"Transmit Size:"+toPioneerTransmitByte/UtilsMethod.kbyteRage+" KBytes," +
    							getDropReasonString(reason) );
    					//统计
    					HashMap<String, TotalSpecialModel> map =new HashMap<String, TotalSpecialModel>();
    					if( reason == RcuEventCommand.DROP_USERSTOP && delay>0 ){
    						totalHttpResult(map,TotalHttpType.HTTPRefresh,httpModel.getUrl(),TotalAppreciation._HttpRefreshTry, 1);
    	    				totalHttpResult(map,TotalHttpType.HTTPRefresh,httpModel.getUrl(),TotalAppreciation._HttpRefreshSuccess,1);
    	    				totalHttpResult(map,TotalHttpType.HTTPRefresh,httpModel.getUrl(),TotalAppreciation._HttpRefreshDelay ,delay );
    					}else{
    						totalHttpResult(map,TotalHttpType.HTTPRefresh,httpModel.getUrl(),TotalAppreciation._HttpRefreshTry, 1);
    						totalHttpResult(map,TotalHttpType.HTTPRefresh,httpModel.getUrl(),TotalAppreciation._HttpRefreshSuccess,0 );
    					}
    					Message msgChart = callbackHandler.obtainMessage( CHART_CHANGE, map );
            			callbackHandler.sendMessage( msgChart );
            			
    					//结束本次测试
    					new Thread( new StopCurrentTest( TestService.RESULT_SUCCESS ) ).start();
    				}
    			}else{
    				
    				//结束本次测试
					new Thread( new StopCurrentTest( TestService.RESULT_SUCCESS) ).start();
    			}
    			
    		}
    	}
    	
    	/**
    	 * 用户停止的Drop
    	 * */
    	public void dropByUserStop(){
			httpRefreshDrop(  RcuEventCommand.DROP_USERSTOP,System.currentTimeMillis() * 1000 );
    	}
    	
    	private void connectFail(int reason,long time ){
			if( !hasFail ){
				hasFail = true;
				int delayCF = (int)(time - gatewayBegin ) / 1000;
    			writeRcuEvent( RcuEventCommand.HTTP_SockFailure,time,delayCF,reason);
    			showEventNT( String.format( "HTTP Page Connect Failure:%s",
    					FailReason.getFailReason(reason).getResonStr() )  );
    			//停止当前测试
    			new Thread( new StopCurrentTest( TestService.RESULT_FAIL_REDIAL ) ).start();
			}			
		}
		
		
		private void sendCmdFail(long time){
			if( !hasFail ){
				hasFail = true;
				
				writeRcuEvent(RcuEventCommand.HTTP_SendGetCmdFail,time );
    			if( !hasLogon ){
    				showEventNT( "HTTP Page Logon CMD Failure" );
    				//停止当前测试
    				new Thread( new StopCurrentTest( TestService.RESULT_FAILD ) ).start();
    			}else{
    				showEvent( "HTTP Page Refresh CMD Failure" );
    				//停止当前测试
    				new Thread( new StopCurrentTest( TestService.RESULT_SUCCESS ) ).start();
    			}
    			
			}
		}
		
		/**
		 * http业务失败
		 * @param reason
		 */
		protected void fail( int reason,long time ){
			if( !hasFail && !hasDrop && lastData==0 && firstData==0 ){
				hasFail = true;
				
				EventBytes.Builder( mContext, RcuEventCommand.HTTP_Page_Fail)
				.addInteger( reason )
				.writeToRcu(time);
				
				showEvent( String.format("HTTP Page Refresh Failure:%s",
						FailReason.getFailReason(reason).getResonStr() ) );
				
				//结束本次测试
				new Thread( new StopCurrentTest( TestService.RESULT_FAILD ) ).start();
			}
		}
    	
		/**
		 * 断开连接
		 */
		protected void disconnect(){
			if( !hasDisConnect && gatewayFinish!=0 ){
				hasDisConnect = true;
				
				//存储事件
    			EventBytes.Builder( WebPage.this, RcuEventCommand.HTTP_SocketDisconnected).writeToRcu(System.currentTimeMillis() * 1000);
    			
    			//显示事件
    			showEventNT("HTTP Page Disconnect");
			}
		}
		
	}
	
	private void loadUrlWebView(String url){
		Intent i = new Intent(mContext,WebViewActivity.class);
		i.putExtra("path",url);
		i.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		startActivity( i );
	
	}
	
	private void sendCurrentRate(String msg){
		String[] msgs = msg.split("\n");
		
		Map<String, Object> dataMap =new HashMap<String, Object>();
		dataMap.put(DataTaskValue.BordLeftTitle.name(), 
				getString(R.string.info_data_averagerate)
	            + "@" + UtilsMethod.decFormat.format(  Double.parseDouble(msgs[4].split("::")[1]) /UtilsMethod.kbyteRage) 
	            + getString(R.string.info_rate_kbps));
		dataMap.put(DataTaskValue.BordCurrentSpeed.name(), 
				UtilsMethod.decFormat.format(Double.parseDouble(msgs[7].split("::")[1])/UtilsMethod.kbyteRage));
		
		callbackHandler.obtainMessage(DATA_CHANGE,dataMap).sendToTarget();
	}
}
