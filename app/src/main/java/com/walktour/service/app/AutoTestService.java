package com.walktour.service.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.dinglicom.dataset.EventManager;
import com.walktour.Utils.AlertWakeLock;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.ServerMessage;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.Utils.WalkStruct.TestType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.AssetsWriter;
import com.walktour.control.bean.MyPhone;
import com.walktour.control.bean.Verify;
import com.walktour.control.config.ConfigAutoTest;
import com.walktour.control.instance.DataManagerFileList;
import com.walktour.gui.R;
import com.walktour.gui.data.model.DataModel;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.txt.TestPlan;
import com.walktour.gui.task.parsedata.txt.TestPlan.TimeRange;
import com.walktour.service.FleetService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 *控制自动测试的服务,
 *此服务随界面中的自动测试开启和关闭
 * */
public class AutoTestService extends Service{
	private final String tag = "AutoTestService";
	
	/**切换自动测试开关*/
	public static final String ACTION_FLEET_TRIGGLE_AUTOTEST = "walktour.fleet.autotest.triggle";
	/**切换定位开关*/
	public static final String ACTION_FLEET_TRIGGLE_GPS = "walktour.fleet.gps.triggle";
	/**开始连续上传GPS*/
	public static final String ACTION_FLEET_GPS_CONSTANTLY_START = "walktour.fleet.gps.constantly.start";
	/**停止连续上传GPS*/
	public static final String ACTION_FLEET_GPS_CONSTANTLY_STOP = "walktour.fleet.gps.constantly.stop";
	/**上传自动测试文件*/
	public static final String ACTION_FLEET_UPLOAD_AUTOTEST= "walktour.fleet.upload.autotest";
	/**停止上传自动测试文件*/
	public static final String ACTION_FLEET_UPLOAD_AUTOTEST_STOP= "walktour.fleet.upload.autotest.stop";
	/**上传自动测试文件完毕*/
	public static final String ACTION_FLEET_UPLOAD_AUTOTEST_DONE= "walktour.fleet.upload.autotest.done";
	/**重启后更新测试计划*/
	public static final String ACTION_DOWNLOAD_AFTER_BOOT= "walktour.fleet.download.afterboot";
	/**界面手工下载测试计划*/
	public static final String ACTION_DOWNLOAD_MANUALY= "walktour.fleet.download.manualy";
	/**重启Walktour*/
	public static final String ACTION_RESTART= "walktour.autotest.restart";
	
	public static String Action_LOCATION_START = "com.walktour.location.start";			
	public static String Action_LOCATION_STOP = "com.walktour.location.stop";
	public static final String ACTION_START_TEST= "walktour.autotest.starttest";
	public static final String ACTION_STOP_TEST= "walktour.autotest.stoptest";
	
    //自动测试相关
    private AlarmManager startAlarm = null;
	private PendingIntent pendingTestStart = null;
	private PendingIntent pendingTestStop = null;
	private PendingIntent pendingLocationStart = null;
	private PendingIntent pendingLocationStop = null;
	private TestPlan mTestPlan = TestPlan.getInstance();
	
	private boolean waitingForTest = false;
	
	private Context mContext;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		mContext = this;
		LogUtil.i(tag, "---onCreate");
		startAlarm = (AlarmManager)getSystemService(ALARM_SERVICE);
		
		//注册广播
		regeditBroadcast();
		
		//上传GPS定时器
		setLocationAlarmer();
		
		ConfigAutoTest config = new ConfigAutoTest();
		if( config.isAutoTestOn() ){
			//下载测试计划
			new DownloadWaiter().start();
		}else{
			//sendEvent("Auto test is off");
			LogUtil.w(tag,"Auto test is off");
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		LogUtil.i(tag, "---onStart:" + (intent == null));
		if(intent == null){
			startService(new Intent(getApplicationContext(),Killer.class));
		}
		
		//监听短信数据库变化
		SMSObserver observer = new SMSObserver( new Handler() );
		this.getContentResolver().registerContentObserver(
				Uri.parse( "content://sms/" ),//注意这里是"content://sms/"而不是"content://sms/inbox/"
				true,
				observer);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		LogUtil.w(tag, "-------->onDestroy");
		this.cancleAutoTestAlarmer();
		this.unregisterReceiver( mBroadcastReceiver );
	}
	
	
	/**
	 * 注册广播接收器
	 */
	protected void regeditBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ServerMessage.ACTION_FLEET_DOWNLOAD_DONE);
		filter.addAction(WalkMessage.NOTIFY_TESTJOBDONE);
		filter.addAction(WalkMessage.NOTIFY_INTERRUPTJOBDONE);
		filter.addAction(AutoTestService.Action_LOCATION_START);
		filter.addAction(AutoTestService.Action_LOCATION_STOP);
		filter.addAction(AutoTestService.ACTION_FLEET_TRIGGLE_AUTOTEST );
		filter.addAction(AutoTestService.ACTION_FLEET_TRIGGLE_GPS);
		filter.addAction(AutoTestService.ACTION_START_TEST);
		filter.addAction(AutoTestService.ACTION_STOP_TEST);
		filter.addAction(AutoTestService.ACTION_FLEET_UPLOAD_AUTOTEST_DONE);
		filter.addAction(AutoTestService.ACTION_DOWNLOAD_AFTER_BOOT);
		filter.addAction(AutoTestService.ACTION_DOWNLOAD_MANUALY);
		filter.addAction(AutoTestService.ACTION_RESTART);
		this.registerReceiver( mBroadcastReceiver, filter );
	}
	
	/**
	 * 广播接收器:接收来广播更新界面
	 * */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.w(tag, "---receive broadcast:"+intent.getAction() );
			
			/*//重启Walktour
			if( intent.getAction().equals(ACTION_RESTART) ){
				//new Thread( new Restarter() ).start();
				new DownloadWaiter().start();
			}*/
			
			//手工下载测试计划
			if( intent.getAction().equals( ACTION_DOWNLOAD_MANUALY) ){
				new DownloadWaiter().start();
			}
			
			//串口重启后重新下载测试计划
			if( intent.getAction().equals( ACTION_DOWNLOAD_AFTER_BOOT ) ){
				if( new ConfigAutoTest().isAutoTestOn() ){
					new DownloadWaiter().start();
				}
			}
			
			//下载测试计划完成后,重新制定下一个测试时间,检查当前是否需要测试
			if( intent.getAction().equals( ServerMessage.ACTION_FLEET_DOWNLOAD_DONE )){
				//通知测试服务停止
				if( ApplicationModel.getInstance().isTestJobIsRun() ){
					LogUtil.w(tag, "---stop test");
					sendBroadcast( new Intent( WalkMessage.Action_Walktour_Test_Interrupt ) );
				}
				
				//制定下一个测试时间段
				boolean isEffect = TestPlan.getInstance().getTestPlanFromFile();
				LogUtil.w(tag, "---is test plan effect?"+isEffect);
				if( isEffect ){
					
					TimeRange timeRange = mTestPlan.getNearestTimeRange();
					
					setTestAlarmer( timeRange );
					
					checkCurrentInTest();
					
					Toast.makeText(getApplicationContext(), 
							getString(R.string.fleet_downloadFinished), 
							Toast.LENGTH_LONG).show();
				}else{
					sendEvent("No Task in Test Plan");
					Toast.makeText(getApplicationContext(), 
							getString(R.string.fleet_download_notask), 
							Toast.LENGTH_LONG).show();
					
					
					//重新制定空闲上传GPS时间段
					setLocationAlarmer();
				}
					
			}
			
			//测试完成，制定下一个测试时段
			else if( intent.getAction().equals(WalkMessage.NOTIFY_TESTJOBDONE) 
					|| intent.getAction().equals(WalkMessage.NOTIFY_INTERRUPTJOBDONE)){
				
				TimeRange timeRange = mTestPlan.getLastTimeRange();
				
				if( timeRange!=null ){
					//先判断该时间段内有没有上传Walktour数据任务
					boolean hasUpload = timeRange.hasUpload();
					LogUtil.w(tag, "has upload task in this timerange?"+hasUpload);
					if( hasUpload ){
						if( !waitingForTest && ApplicationModel.getInstance().isTesting() ){
							//当前在时间段内才进行上传
							long now = Calendar.getInstance().getTimeInMillis();
							long start = timeRange.getStartTimeInMillis();
							long end = timeRange.getStartTimeInMillis() +  timeRange.getContinuousTimeInMillis();
							if(  start<= now && now < end ){
								
								//上传文件
								new Thread(  new FileChecker() ).start();
							}
						}
					}else{
						
						if( !waitingForTest ){
							setTestAlarmer( mTestPlan.getNextStartTimeRange()  );
						}
					}
					
				}
				
				ApplicationModel.getInstance().setAutoTesting( false );
				
				//重新制定空闲上传GPS时间段
				setLocationAlarmer();
			}
			
			//文件上传完毕,制定下一个测试时间段和上传GPS时间段
			else if( intent.getAction().equals( ACTION_FLEET_UPLOAD_AUTOTEST_DONE ) ){
				TimeRange fTimeRange = mTestPlan.getNextStartTimeRange();
				
				if( fTimeRange != null ){
					setTestAlarmer( fTimeRange );
				}
				
				//重新制定空闲上传GPS时间段
				setLocationAlarmer(); 
			}
			
			
			/*****************以下仅是闹铃的广播*************************************/
			
			//开始一个时间段
			else if( intent.getAction().equals( ACTION_START_TEST ) ){
				//下一个测试时间段
				TimeRange nTimeRange = mTestPlan.getNearestTimeRange();
				
				if( nTimeRange != null){
					
					//如果该时间段有测试任务,进行测试,否则上传文件
					String startTime = UtilsMethod.getSimpleDateFormat1( nTimeRange.getStartTimeInMillis() );
					String stopTime = UtilsMethod.getSimpleDateFormat1( nTimeRange.getStartTimeInMillis()
							+nTimeRange.getContinuousTimeInMillis() );
					LogUtil.w(tag, "Start new time:"+startTime+"--->"+stopTime );
					sendEvent( "Start new time:"+startTime+"--->"+stopTime  );
					LogUtil.w(tag,"taskList size:"+nTimeRange.getTaskList().size() );
					if( nTimeRange.hasTestTask() ){
						//
						new TestWaiter().start();
					}else{
						//上传文件任务
						new Thread(  new FileChecker() ).start();
					}
					
				}
				
			}
			
			//停止一个时间段
			else if( intent.getAction().equals( ACTION_STOP_TEST ) ){
				
				TimeRange sTimeRange = mTestPlan.getLastTimeRange();
				
				if( sTimeRange!=null  ){
					
					String startTime = UtilsMethod.getSimpleDateFormat1( sTimeRange.getStartTimeInMillis() );
					String stopTime = UtilsMethod.getSimpleDateFormat1( sTimeRange.getStartTimeInMillis()
							+sTimeRange.getContinuousTimeInMillis() );
					LogUtil.w(tag, "stop time range:"+startTime+"--->"+stopTime );
					sendEvent( "Stop range:"+startTime+"--->"+stopTime  );
					LogUtil.w(tag,"taskList size:"+sTimeRange.getTaskList().size() );
									
				}
				
				//通知测试服务停止
				if( ApplicationModel.getInstance().isTestJobIsRun() ){
					LogUtil.w(tag, "---stop test");
					sendBroadcast( new Intent( WalkMessage.Action_Walktour_Test_Interrupt ) );
					sendBroadcast( new Intent( WalkMessage.ACTION_FLEET_UPLOADGPS_ONECE_STOP ) );
				}
				
				//通知FleetService停止未完成的上传
				sendBroadcast( new Intent( ACTION_FLEET_UPLOAD_AUTOTEST_STOP ) );
				
				//重新制定空闲上传GPS时间段
				setLocationAlarmer();
				
			}
			
			//切换自动测试开关
			else if( intent.getAction().equals( ACTION_FLEET_TRIGGLE_AUTOTEST ) ){
				boolean isEffect = TestPlan.getInstance().getTestPlanFromFile();
				if( isEffect ){
					setTestAlarmer( mTestPlan.getNearestTimeRange() );
					checkCurrentInTest();
				}else{
					sendEvent("Test plan is empty");
					//重新制定空闲上传GPS时间段
					setLocationAlarmer();
				}
			}
			
			//切换连续上传GPS开关
			else if( intent.getAction().equals( ACTION_FLEET_TRIGGLE_GPS) ){
				setLocationAlarmer();
			}
			
			//连续上传GPS开始
			else if( intent.getAction().equals( Action_LOCATION_START )){
				//先强制打开GPS
				new MyPhone( AutoTestService.this ).openGps();
				
				TestPlan.getInstance().setUploadConstantly( true );
				
				if( ! ApplicationModel.getInstance().isTestJobIsRun() ){
					//启动GPS服务
					GpsInfo.getInstance().openGps(AutoTestService.this,
							WalkCommonPara.OPEN_GPS_TYPE_AUTOTEST);
					//通知FleetService连续上传GPS
					sendBroadcast( new Intent(AutoTestService.ACTION_FLEET_GPS_CONSTANTLY_START) );
				}
			}
			
			//连续上传GPS结束
			else if( intent.getAction().equals( Action_LOCATION_STOP )){
				TestPlan.getInstance().setUploadConstantly( false );
				
				//如果当前没有正在测试
				if ( ! ApplicationModel.getInstance().isTestJobIsRun()  ){
					//关闭GPS服务
					GpsInfo.getInstance().releaseGps(AutoTestService.this,
							WalkCommonPara.OPEN_GPS_TYPE_AUTOTEST);
				}
				//通知FleetService停止连续上传GPS
				sendBroadcast( new Intent( ACTION_FLEET_GPS_CONSTANTLY_STOP ) );
			}
				
		}//end onReceive
		
	};
	
	/**检测当前时间是否需要测试*/
	private void checkCurrentInTest(){
		//判断当前是否在最新的测试时间段内
		boolean isOpen = new ConfigAutoTest().isAutoTestOn();
		LogUtil.w(tag, "--->is auto test open?"+isOpen);
		if( isOpen ){
			
			if( mTestPlan.isEffcet() ){
				TimeRange timeRange = mTestPlan.getNearestTimeRange();
				if( timeRange!=null ){
					long now = Calendar.getInstance().getTimeInMillis();
					long start = timeRange.getStartTimeInMillis();
					long end = timeRange.getStartTimeInMillis() +  timeRange.getContinuousTimeInMillis();
					if(  start<= now && now < end ){
						LogUtil.w(tag,"---current is in timerange");
						sendBroadcast( new Intent( ACTION_START_TEST ) );
						
						//制定当前时间段的停止闹钟,因为在上面setTestAlarmer中的停止闹钟是第二天的
						if( startAlarm!=null && pendingTestStop!=null ){
							startAlarm.cancel(pendingTestStop) ;
						}
						pendingTestStop = PendingIntent.getBroadcast(AutoTestService.this, 0, 
								new Intent( ACTION_STOP_TEST ), 0);
						startAlarm.set(AlarmManager.RTC_WAKEUP, end,pendingTestStop);
						
						sendEvent( "Current time range:" );
						sendEvent( "From "+UtilsMethod.getSimpleDateFormat0( start ));
						sendEvent( "-->To "+UtilsMethod.getSimpleDateFormat0( end ));
					}else{
						LogUtil.w(tag,"---current is not in timerange");
						//重新制定空闲上传GPS时间段
						setLocationAlarmer();
					}
					
				}else{
					//重新制定空闲上传GPS时间段
					setLocationAlarmer();
				}
			}
		}else{
			//重新制定空闲上传GPS时间段
			LogUtil.w(tag, "---->is test:"+isOpen);
			setLocationAlarmer();
		}
	}

	/**
     * @param timeRange 根据时间段设置自动测试的闹铃
     * */
    private void setTestAlarmer(TimeRange timeRange){
    	
    	/*闹钟设定*/
		Calendar calendar = Calendar.getInstance();
		long now = calendar.getTimeInMillis();
		
    	//先清理之前的闹钟
		cancleAutoTestAlarmer();
    	
		if( ! new ConfigAutoTest().isAutoTestOn() ){
			LogUtil.w(tag, "---auto test is off");
			//sendEvent("Auto test is off");
			
			//通知测试服务停止
			if( ApplicationModel.getInstance().isTestJobIsRun() ){
				LogUtil.w(tag, "---stop test");
				sendBroadcast( new Intent( WalkMessage.Action_Walktour_Test_Interrupt ) );
				sendBroadcast( new Intent( WalkMessage.ACTION_FLEET_UPLOADGPS_ONECE_STOP ) );
			}
			return;
		}
		
		if( mTestPlan.isEffcet() ){
			if( timeRange!=null ){
				/*任务设定*/
				ArrayList<TaskModel> modelList = timeRange.getTaskList();
				LogUtil.w( tag, "--->conver taskList,size:"+modelList.size() );
				
				//如果测试任务模型列表不为空
				if(modelList.size()>0){
					//添加测试任务模型列表到任务列表中
					TaskListDispose taskList= TaskListDispose.getInstance();
					taskList.replaceTaskList( modelList );
					TaskListDispose.getInstance();
				}
				
				long begin = timeRange.getStartTimeInMillis();
				long end = begin+timeRange.getContinuousTimeInMillis();
				
				
				//这里必须判断是否早于当前时间，否则过时的闹钟会启动
				begin = ( begin < now )? (begin+24*UtilsMethod.Hour) : begin;
				end = begin+timeRange.getContinuousTimeInMillis();
				LogUtil.w(tag, "--->auto test start time:"
						+UtilsMethod.getSimpleDateFormat0( begin ) );
				LogUtil.w(tag, "--->auto test stop time:"
						+UtilsMethod.getSimpleDateFormat0( end ) );
				
				//如果当前不在时间段内
				sendEvent( "New time range:" );
				sendEvent( "From "+UtilsMethod.getSimpleDateFormat0( begin ));
				sendEvent( "-->To "+UtilsMethod.getSimpleDateFormat0( end ));
				
				//测试开始和结束广播
				pendingTestStart = PendingIntent.getBroadcast(AutoTestService.this, 0, 
						new Intent( ACTION_START_TEST ), 0);
				pendingTestStop = PendingIntent.getBroadcast(AutoTestService.this, 0, 
						new Intent( ACTION_STOP_TEST ), 0);
				
				//测试开始和结束定时器
				startAlarm.set(AlarmManager.RTC_WAKEUP, begin,pendingTestStart);
				startAlarm.set(AlarmManager.RTC_WAKEUP, end,pendingTestStop);
			}else{
				LogUtil.w(tag,"--->Time Range is null");
				sendEvent("Time Range is null");
			}
			
    			
		}else{
			LogUtil.w(tag,"--->test plan is empty");
			sendEvent("Test plan is empty");
		}
    		
    }
    
    /**
     * 取消每天自动测试的定时器
     * */
    private void cancleAutoTestAlarmer(){
    	if( startAlarm !=null 
    			&& pendingTestStart!=null 
    			&& pendingTestStop!=null ){
    		startAlarm.cancel(pendingTestStart) ;
    		startAlarm.cancel(pendingTestStop) ;
    	}
    }
    
    /**
     * 设置空闲时间上传GPS的定时器
     * */
    private void setLocationAlarmer(){
    	cancleLocationAlarmer();
    	
    	ConfigAutoTest config = new ConfigAutoTest();
    	
    	LogUtil.w(tag, "--->is location on?"+config.isLocationOn() );
    	if(  config.isLocationOn() ){
    		
			/*闹钟设定*/
			Calendar calendar = Calendar.getInstance();
			long now = calendar.getTimeInMillis();
			long begin = config.getLocationStartTimeMill();
			long end = config.getLocationEndTimeMill();
			
			//如果当前在测试时间内,就启动定位
			boolean isCurrentInLocationTime = begin<now && now<end;
			LogUtil.w(tag, "--->is current in location time?"+isCurrentInLocationTime );
			if( isCurrentInLocationTime  ){
				new LocationWaiter().start();
			}else{
				mTestPlan.setUploadConstantly( false );
				
				//如果当前没有正在测试
				if ( ! ApplicationModel.getInstance().isTestJobIsRun()  ){
					//关闭GPS服务
					GpsInfo.getInstance().releaseGps(AutoTestService.this,
							WalkCommonPara.OPEN_GPS_TYPE_AUTOTEST);
				}
				
				//通知FleetService停止连续上传GPS
				sendBroadcast( new Intent(AutoTestService.ACTION_FLEET_GPS_CONSTANTLY_STOP ) );
			}
			
			begin = ( begin < now )? (begin+24*UtilsMethod.Hour) : begin;
			end = ( end < now )? (end+24*UtilsMethod.Hour) : end;
			LogUtil.w(tag, "--->next location start time:"
					+UtilsMethod.getSimpleDateFormat0( begin ) );
			LogUtil.w(tag, "--->next location stop time:"
					+UtilsMethod.getSimpleDateFormat0( end ) );

			//测试开始和结束广播
			pendingLocationStart = PendingIntent.getBroadcast(AutoTestService.this, 0, 
					new Intent(Action_LOCATION_START), 0);
			pendingLocationStop = PendingIntent.getBroadcast(AutoTestService.this, 0, 
					new Intent(Action_LOCATION_STOP), 0);
			
			//测试开始和结束定时器
			startAlarm.setRepeating(AlarmManager.RTC_WAKEUP, begin
					,24*UtilsMethod.Hour,pendingLocationStart);
			startAlarm.setRepeating(AlarmManager.RTC_WAKEUP, end
					,24*UtilsMethod.Hour,pendingLocationStop);
		}else{
			
			mTestPlan.setUploadConstantly( false );
			
			//如果当前没有正在测试
			if ( ! ApplicationModel.getInstance().isTestJobIsRun()  ){
				//关闭GPS服务
				GpsInfo.getInstance().releaseGps(AutoTestService.this,
						WalkCommonPara.OPEN_GPS_TYPE_AUTOTEST);
				//通知FleetService停止连续上传GPS
				sendBroadcast( new Intent(AutoTestService.ACTION_FLEET_GPS_CONSTANTLY_STOP ) );
			}
			LogUtil.w(tag,"--->lcoation is off");
		}
    }
    
/*    *//**设置重启闹钟*//*
    private void setRestartAlarmer(){
    	long restartTime = TestPlan.getInstance().getRestartTimeInMillis();
    	sendEvent( "Restart RCU at "+UtilsMethod.getSimpleDateFormat0(restartTime) );
    	LogUtil.w(tag, "Restart RCU at "+UtilsMethod.getSimpleDateFormat0(restartTime) );
		pendingRestart = PendingIntent.getBroadcast(AutoTestService.this, 0, 
				new Intent( ACTION_RESTART ), 0);
		startAlarm.set(AlarmManager.RTC_WAKEUP, restartTime,pendingRestart);
    }*/
    
    /**
     * 取消空闲时间上传GPS的定时器
     * */
    private void cancleLocationAlarmer(){
    	if( startAlarm !=null && pendingLocationStart!=null && pendingLocationStop!=null ){
    		startAlarm.cancel(pendingLocationStart) ;
    		startAlarm.cancel(pendingLocationStop) ;
    	}
    }
    
    /**
	 * 开始操作文作，上传 删除等(I/O操作用单独线程防止界面卡死)
	 */
	private class FileChecker implements Runnable{
		
		private final int KEEPDAYS = 3;
		
		@Override
		public void run() {
			
			//唤醒CPU
			AlertWakeLock.acquire( AutoTestService.this );
			
			//第一步:通知FleetService停止连续上传GPS
			TestPlan.getInstance().setUploadConstantly( false );
			sendBroadcast( new Intent(ACTION_FLEET_GPS_CONSTANTLY_STOP ) );
			
			//第二步：先删除3天前的数据,把未上传的文件标志为需要上传
			ArrayList<DataModel> modelList = DataManagerFileList.getInstance(getApplicationContext())
					.getAllFileList(TestType.DT);
			long now = System.currentTimeMillis();
			long keepDays = 1000*60*60*24*KEEPDAYS;
			boolean hasFileNeedUpload = false;
			for( int i=modelList.size()-1;i>=0;i--){
				DataModel model = modelList.get(i);
				LogUtil.w(tag,"--->"+model.testRecord.file_name+" is uploaded?"
						+ (model.getState()==DataModel.STATUS_UPLOAD_FINISH ) );
				//删除指定天数前的数据
				if( ( now-model.getCreateTime() ) >= keepDays ){
					LogUtil.w(tag, "delete file:"+model.testRecord.file_name);
					DataManagerFileList.getInstance(getApplicationContext()).deleteFile( model );
				}else if( model.getState() !=DataModel.STATUS_UPLOAD_FINISH){
					hasFileNeedUpload = true;
					model.setState( DataModel.STATUS_UPLOAD_WAITING );
				}
			}
			
			//第三步：通知FleetService上传文件
			if(hasFileNeedUpload){
				sendBroadcast( new Intent ( AutoTestService.ACTION_FLEET_UPLOAD_AUTOTEST ) );
			}else{
				sendEvent("No file is need to upload");
				sendBroadcast( new Intent ( ACTION_FLEET_UPLOAD_AUTOTEST_DONE ) );
			}
		}
	}
	
	/**等待测试完成开始新的时间段*/
	private class TestWaiter extends Thread{
		@Override
		public void run(){
			waitingForTest = true;
			ApplicationModel app = ApplicationModel.getInstance();
			
			//发送广播通知测试停止
			if(app.isTestJobIsRun()){
				sendBroadcast( new Intent( WalkMessage.Action_Walktour_Test_Interrupt ) );
				sendBroadcast( new Intent( WalkMessage.ACTION_FLEET_UPLOADGPS_ONECE_STOP ) );
			}
			
			//通知FleetService停止未完成的上传
			sendBroadcast( new Intent( ACTION_FLEET_UPLOAD_AUTOTEST_STOP ) );
			
			//通知FleetService停止连续上传GPS
			mTestPlan.setUploadConstantly( false );
			sendBroadcast( new Intent(ACTION_FLEET_GPS_CONSTANTLY_STOP ) );
			
			//等待停止完成，60秒超时
			int i =0;
			while( app.isTestJobIsRun() || app.isTestStoping() ){
				LogUtil.w(tag,"Wait for last test to finish");
				sendEvent("Wait for last test to finish");
				i++;
				if( i>=60 ){
					break;
				}
				try {
					Thread.sleep(1*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			//这里不能休眠，否则，勾选自动测试后马上点下载，下载线程DownloadWaiter不会判断到正在测试状态
			/*try {
				Thread.sleep(2*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
			
			//唤醒CPU
			AlertWakeLock.acquire( AutoTestService.this );
			
			//开始测试
			if( new ConfigAutoTest().isAutoTestOn() ){
				Intent autoTest = new Intent(WalkMessage.ACTION_WALKTOUR_START_TEST);
				autoTest.putExtra(WalkMessage.ACTION_ISAUTOTEST_PARA, true);
				autoTest.putExtra(WalkMessage.KEY_FROM_SCENE, SceneType.Auto.getSceneTypeId());
				sendBroadcast( autoTest);
				app.setAutoTesting( true );
			}
			waitingForTest = false;
		}
	}
    
	
	private class LocationWaiter extends Thread{
		@Override
		public void run(){
			//先强制打开GPS
			new MyPhone( AutoTestService.this ).openGps();
			
			int i=0;
			while( ApplicationModel.getInstance().isTestJobIsRun()  ||
					ApplicationModel.getInstance().isTestStoping() ){
				i++;
				if( i>60 ){
					break;
				}
				try {
					Thread.sleep( 1*1000 );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			ConfigAutoTest config = new ConfigAutoTest();
			if( config.isLocationOn() ){
				mTestPlan.setUploadConstantly( true );
				//启动GPS服务
				GpsInfo.getInstance().openGps(AutoTestService.this,
						WalkCommonPara.OPEN_GPS_TYPE_AUTOTEST);
				//通知FleetService连续上传GPS
				sendBroadcast( new Intent(AutoTestService.ACTION_FLEET_GPS_CONSTANTLY_START) );
			}else{
				mTestPlan.setUploadConstantly( false );
				//关闭GPS服务
				GpsInfo.getInstance().releaseGps(AutoTestService.this,
						WalkCommonPara.OPEN_GPS_TYPE_AUTOTEST);
				//通知FleetService停止连续上传GPS
				sendBroadcast( new Intent(AutoTestService.ACTION_FLEET_GPS_CONSTANTLY_STOP ) );
			}
		}
	}
	
	private class DownloadWaiter extends Thread{
		@Override
		public void run(){
			
			//唤醒CPU
			AlertWakeLock.acquire( AutoTestService.this );
			
			//清理所有测试闹钟
			cancleAutoTestAlarmer();
			
			//清理本地文件
			new AssetsWriter(AutoTestService.this,
					"config/test_task.txt","test_task.txt",true).writeToConfigDir();
			
			//重设TestPlan
			TestPlan.getInstance().getTestPlanFromFile();
			sendEvent("Clear local test plan");
			LogUtil.w(tag,"Clear local test plan");
			
			//停止当前时间段
			sendBroadcast( new Intent(ACTION_STOP_TEST) );
			
			//等待停止完成
			if( ApplicationModel.getInstance().isTestJobIsRun()  ||
					ApplicationModel.getInstance().isTestStoping() ){
				sendEvent("Test is stopping");
				int i=0;
				while( ApplicationModel.getInstance().isTestJobIsRun()  ||
						ApplicationModel.getInstance().isTestStoping() ){
					LogUtil.w(tag, "Test is stopping");
					i++;
					if( i>60 ){
						break;
					}
					try {
						Thread.sleep( 1*1000 );
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			
			while( !FleetService.hasRegistedBroadcast() ){
				try {
					Thread.sleep( 1*1000 );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			//发送广播通知FleetService下载测试计划
			sendBroadcast( new Intent( ServerMessage.ACTION_FLEET_DOWNLOAD_AUTOTEST ) );
		}
	}
	
	/**
	 * 短信数据库监控者
	 * 当收件箱变化时读取短信,把短信置为已读
	 * 
	 * */
	private class SMSObserver extends ContentObserver{
		
		/**在指定时间TIME内是否在收到同样的短信*/
		private final int TIME = 60;
		private boolean hasReceiveBefor = false;
		
		public SMSObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean isSelfChange){
			super.onChange( isSelfChange );
			LogUtil.w(tag, "---SMS's DB is changeed, isSelfChange:"+isSelfChange );
			
			//删除短信也会导致SMS数据库变化,这里要确定是收到短信引起的变化
			if( !ApplicationModel.getInstance().isHasReceiveSMS() ){
				return ;
			}
			
			//短信状态改为已读取后会再次触发onChange，加hasReadSMS标志防止死循环
			//收件箱地址
			Uri uriSMS = Uri.parse( "content://sms" );
			Cursor c = AutoTestService.this.getContentResolver().query(uriSMS,
					null,
					//new String[]{"_id","address","date","read","body"},
					null, 
					null, 
					"date desc");
			
			//找到第一条记录
			c.moveToFirst();
			//twq20110908当获昨的行数小于等于0时不再继续
			if(c.getCount()<=0){
				LogUtil.w(tag,"---SMS get msg rows zero---");
				return;
			}
			for( int i=0;i<c.getColumnCount();i++){
				LogUtil.w(tag,c.getColumnName(i)+":"+c.getString( c.getColumnIndex( c.getColumnName(i) ) ) );
			}
			
			//读取短信内容
			String strMsg = c.getString( c.getColumnIndex( "body" ) );
			
//			if( strMsg.contains("PHONE") && strMsg.contains("USER") && strMsg.contains("PASS") 
//					&& strMsg.contains("APN") && strMsg.contains("IP:") && strMsg.contains("PORT:") ){
//				//
//			}else{
//				strMsg = getHexToString(strMsg);
//			}
			
			/* 2013.1.15新规定由明文
			 * 为了区分开这种控制短信与普通短信，防止出现错误的操作，
			 * 每条短信开头需要加一个字 符串“To_Rcu”，区分大小写
			 * 每一个参数之间以空格或回车分开，flag字 符区分大 小写
			 */
			if( strMsg.trim().toLowerCase(Locale.getDefault()).startsWith("to_rcu")
					&& strMsg.contains("IP:") 
					&& strMsg.contains("PORT:") ){
				
				if( !hasReceiveBefor ){
					try{
						String ip="";
						String port="";
						
						//直接解析，不用正则式
						char[] chars = strMsg.toCharArray();
						//IP
						for(int i= strMsg.indexOf("IP:")+3;i<strMsg.length();i++ ){
							char ch = chars[i];
							if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n'){
								break;
							}else{
								ip += chars[i];
							}
						}
						//PORT
						for(int i= strMsg.indexOf("PORT:")+5;i<strMsg.length();i++ ){
							char ch = chars[i];
							if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n'){
								break;
							}else{
								port += chars[i];
							}
						}
						
//						String[] paras = strMsg.split("\n");
//						for(String p : paras){
//							if( p.contains("IP:") ){
//								ip = p.trim().split(":")[1];
//							}
//							
//							if(p.contains("PORT:")){
//								port =  p.trim().split(":")[1];
//							}
//						}
						
						if( Verify.isIp(ip) && Verify.isPort(port) ){
							ConfigAutoTest config =  new ConfigAutoTest();
							config.setIp( ip );
							config.setPort( port );
							
							String msg = "SMS:IP="+ip+",PORT="+port;
							LogUtil.w(tag,msg);
							sendEvent( msg );
							//下载测试计划,下载完成后开始测试
							if( new ConfigAutoTest().isAutoTestOn() ){
								//提示界面更新
								sendBroadcast( new Intent(WalkMessage.ACTION_FLEET_RECEIVE_SMS) );
								
								new DownloadWaiter().start();
							}
						}
						
					}catch(Exception e){
						e.printStackTrace();
					}
					
				}
				
				new Rester().start();
			}
		}
		
		/**重新设定收到短信的状态*/
		private class  Rester extends Thread{
			@Override 
			public void run(){
				hasReceiveBefor = true;
				try {
					Thread.sleep( TIME * 1000 );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				hasReceiveBefor = false;
			}
		}
	}

	/**
	 * 发送显示字符到事件页面
	 * */
	private void sendEvent(String event){
		EventManager.getInstance().addEvent( mContext,event);
	}
	
	/**重启Walktour*/
//	private class Restarter implements Runnable{
//
//		@Override
//		public void run() {
//			//停止当前时间段
//			sendBroadcast( new Intent(ACTION_STOP_TEST) );
//			
//			//等待停止完成
//			int i=0;
//			while( ApplicationModel.getInstance().isTestJobIsRun()  ||
//					ApplicationModel.getInstance().isTestStoping() ){
//				sendEvent("Test is stopping");
//				LogUtil.w(tag, "Test is stopping");
//				i++;
//				if( i>60 ){
//					break;
//				}
//				try {
//					Thread.sleep( 1*1000 );
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//			
//			sendEvent("Restart Walktour now...");
//			LogUtil.w(tag, "Restart Walktour now...");
//			android.os.Process.killProcess(android.os.Process.myPid());
//		}
//		
//	}
	
	/**
	 * 16进制转换到字符显示
	 * */
//	 private String getHexToString(String strValue) {
//		int intCounts = strValue.length() / 2;
//		String strReturn = "";
//		String strHex = "";
//		int intHex = 0;
//		byte byteData[] = new byte[intCounts];
//		try {
//			for (int intI = 0; intI < intCounts; intI++) {
//				strHex = strValue.substring(0, 2);
//				strValue = strValue.substring(2);
//				intHex = Integer.parseInt(strHex, 16);
//				if (intHex > 128)
//					intHex = intHex - 256;
//				byteData[intI] = (byte) intHex;
//			}
//			strReturn = new String(byteData, "ISO8859-1");
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return strReturn;
//	}

}
