package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.task.parsedata.TaskTestObject;
import com.walktour.gui.task.parsedata.model.task.wlan.login.TaskWlanLoginModel;
import com.walktour.service.ICallback;
import com.walktour.service.IService;

import java.util.Map;

public class WlanTest extends TestTaskService{
	public final String tag ="WlanTest";
	
	public final static String ACTION_WLAN_EVENT = "com.walktour.wlantest.event";
	public final static String ACTION_TEST_STOP = "com.walktour.wlantest.teststop";
	public final static String KEY_EVENT ="event";
	public final static String KEY_USER ="user";
	public final static String KEY_PASS ="pass";
	public final static String KEY_TIMEOUT ="timeout";
	
	private final static int RESON_TIMEOUT = 1;	//失败原因码
	
	//Handler message's what
	private static final int EVENT_CHANGE = 1;	/*显示事件变化*/
	private static final int CHART_CHANGE =2;	/*图表变化*/
	private static final int DATA_CHANGE = 3;	/*数据变化*/
	private static final int TEST_STOP = 4;		/*停止当次测试*/
	
	//测试任务相关
	private int repeatTimes = 0;				/*当前测试是第几次*/
	private TaskWlanLoginModel testModel;		
	
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
    }
    
    /**
	 * 获取当前的测试对象
	 * @param intent	启动Intent
	 * @return	当前的测试对象
	 */
	private TaskWlanLoginModel getMyModel(Intent intent) {
		Bundle bundle = intent.getExtras();
		Object obj = bundle.getSerializable(WalkCommonPara.testModelKey);
		if(obj == null){
			LogUtil.w(tag, "data_key is null");
			return null;
		}
		return (TaskWlanLoginModel)obj;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onStart(Intent intent,int startid){
		LogUtil.d(tag,"---onStart");
		try{
			testModel = getMyModel(intent);
			if(testModel == null){
				LogUtil.w(tag,"--onStart Intent Object null Stop!--");
				Message msg = mHandler.obtainMessage(TEST_STOP, "TestStop");
				msg.sendToTarget(); 
				return;
			}
			repeatTimes = intent.getExtras().getInt(WalkCommonPara.testRepeatTimes,0);
        	
			//启动登录页面
        	Intent wlanLogin = new Intent( WlanTest.this,WlanLogin.class);
			wlanLogin.putExtra(WlanTest.KEY_USER, testModel.getUser());
			wlanLogin.putExtra(WlanTest.KEY_PASS, testModel.getPass() );
			wlanLogin.putExtra(WlanTest.KEY_TIMEOUT, testModel.getTimeOut() );
			wlanLogin.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
			startActivity( wlanLogin );
		}catch( Exception e){
			LogUtil.w(tag, e.toString() );
			//停止当前测试
			mHandler.obtainMessage( TEST_STOP, "Test Stop" ).sendToTarget();
		}
	}
	
	//注册回调接口
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
            }   
        }
		
		public void stopTask(boolean isTestInterrupt,int dropReasion) throws RemoteException {
			LogUtil.w( tag , "===Call Stop Task===");
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
										tempMap.put(TaskTestObject.stopResultName, testModel.getTaskName());
										mCallbacks.getBroadcastItem(i).OnDataChanged(tempMap);
                		break;
                	case TEST_STOP:
                		Map<String,String> resultMap = TaskTestObject.getStopResultMap(testModel);
            			resultMap.put(TaskTestObject.stopResultState, (String)msg.obj);
                		mCallbacks.getBroadcastItem(i).onCallTestStop(resultMap);
                		break;
                	}
                }   
            } catch (RemoteException e) {   
                LogUtil.w(tag, "", e);   
            }   
            mCallbacks.finishBroadcast();
        } 
    };//end mHandler
    
    /**
	 * 广播接收器:接收通信过程中的信令
	 * */
	private BroadcastReceiver mEventReceiver = new BroadcastReceiver() {
		private long pageStartTime =0;
		private long longinStartTime =0;
		private long logoutStartTime = 0;
		private int delay =0;
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.i(tag, intent.getAction() );
			if( intent.getAction().equals( ACTION_WLAN_EVENT ) ){
				int event = intent.getIntExtra( KEY_EVENT ,-1 );
				long time = System.currentTimeMillis() * 1000;
				switch(event){
				case  RcuEventCommand.WLAN_WIFI_CONNECT_START:					//开始连接Wifi
					writeRcuEvent( event,time );
					displayEvent( "Wifi Connect Start" );
					break;			
				case  RcuEventCommand.WLAN_WIFI_CONNECT_SUCCESS:				//连接wifi成功
					writeRcuEvent( event,time );
					displayEvent( "Wifi Connect Success" );
					break;
				case  RcuEventCommand.WLAN_WIFI_CONNECT_FAILURE:					//连接wifi失败
					writeRcuEvent( event,time );
					displayEvent("Wifi Connect Failure" );
					stopCurrentTest();
					break;
				case  RcuEventCommand.WLAN_OPEN_PORTAL_PAGE_START:				//打开登录主页
					writeRcuEvent( event,time );
					displayEvent( "Open Portl Page Start" );
					pageStartTime = System.currentTimeMillis();
					break;
				case  RcuEventCommand.WLAN_OPEN_PORTAL_PAGE_FIRST_DATA:			//登录主页第一个数据包到达
					delay = (int)(System.currentTimeMillis()-pageStartTime);
					writeRcuEvent( event,time, delay);
					displayEvent( "Open Portl Page First Data: Delay "+ delay+"(ms)");
					break;
				case  RcuEventCommand.WLAN_OPEN_PORTAL_PAGE_SUCCESS:			//打开登录主页成功
					delay = (int)(System.currentTimeMillis()-pageStartTime);
					writeRcuEvent( event,time,delay);
					displayEvent( "Open Portal Page Success：Delay +"+delay+"(ms)" );
					break;
				case  RcuEventCommand.WLAN_OPEN_PORTAL_PAGE_FAILURE:			//打开登录主页失败
					writeRcuEvent( event,time,RESON_TIMEOUT );
					displayEvent("Open Portal Page Failure");
					break;
				case  RcuEventCommand.WLAN_LOGIN_START:							//开始登录
					longinStartTime = System.currentTimeMillis();
					writeRcuEvent( event,time );
					displayEvent("Wifi Login Start");
					break;
				case  RcuEventCommand.WLAN_LOGIN_SUCCESS:						//登录成功
					delay = (int)(System.currentTimeMillis()-longinStartTime);
					writeRcuEvent( event,time,delay );
					displayEvent("Wifi Login Success:Delay "+delay+"(ms)");
					break;
				case  RcuEventCommand.WLAN_LOGIN_FAILURE:						//登录失败
					writeRcuEvent( event ,time,1 );
					displayEvent( "Wifi Login Failure" );
					break;
				case  RcuEventCommand.WLAN_LOGOUT_START:						//注销开始
					writeRcuEvent( event,time );
					displayEvent("Wifi Logout Start");
					logoutStartTime = System.currentTimeMillis();
					break;
				case  RcuEventCommand.WLAN_LOGOUT_SUCCESS:						//注销成功
					delay = (int)( System.currentTimeMillis()-logoutStartTime );
					writeRcuEvent( event ,time,delay);
					displayEvent( "Wifi Logout Success: Delay "+delay+"(ms)" );
					break;
				case  RcuEventCommand.WLAN_LOGOUT_FAILURE:						//注销失败
					writeRcuEvent( event,time,RESON_TIMEOUT);
					displayEvent("Wifi Logout Failure");
					break;
				}
			}else if( intent.getAction().equals(ACTION_TEST_STOP ) ){
				mHandler.obtainMessage(TEST_STOP, "TEST STOP").sendToTarget();
			}
		}
	};
    
	//注册广播接收器
	private void regedit() {
		IntentFilter filter = new IntentFilter();
		filter.addAction( ACTION_WLAN_EVENT );
		filter.addAction( ACTION_TEST_STOP );
		this.registerReceiver(mEventReceiver, filter);
	}
	
	/**
	 * 显示事件
	 * @param event 要显示在事件页面的事件
	 * */
	private void displayEvent(String event){
		Message msg = mHandler.obtainMessage(EVENT_CHANGE, event);
		msg.sendToTarget(); 
	}
	
	/**
	 * 停止当次测试*
	 */
	private void stopCurrentTest(){
		mHandler.obtainMessage( TEST_STOP, "Test Stop" ).sendToTarget();
	}

}
