package com.walktour.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class OldFtp extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//2013.7.5 注释所有代码并改名字备份 
	/*
	private final String tag = "FtpTest";
	private final int EVENT_CHANGE 	= 1;	//事件变化
	private final int CHART_CHANGE 	= 2;	//图表变化
	private final int DATA_CHANGE 	= 3;	//数据变化
	private final int TEST_STOP 	= 4;	//停止当次测试
	private final int TIMER_CHANG	= 55;	//时间刷新计时器
	
	*//**登陆FTP服务器时无响应超时获登陆成功后到获得文件大小超时，或者获得文件大小后到收到第一个数据包之间超时*//*
	private long 			serverNotResponseTime = 60;
	private TaskFtpModel 	ftpModel;
	private String 		systemFilePath ="/data/data/com.walktour.gui/";
	private int 		jniHandle 	= 0;
	private static int 	testProcId	= 0;
	private String 		ftpTestPath = "";
	private boolean		isCallbackRegister = false;
	private boolean 	isTotal	= false;	//满足完整性原则并已统计过
	private ConfigFtp 	ftp = null;
	private String[] 	ftpInfo = null;
	private String 		pingStr = "";
	Message 			msg;
	WalkStruct.TaskType taskType	= null;
	
	private float 	kByte 			= 10000f;	//
	private float	process			= 0f;		//进度
	private float 	meanRate		= 0f;		//速率
	private float	averageRage 	= 0f;		//平均速度
	private float 	peakValue 		= 0f;		//峰值
	private long 	fileSize		= 0;		//文件大小
	private long 	currentSize		= 0;		//当前已获取大小 
	private long 	totalTransSize 	= 0;		//总的传输大小，该值为写入RCU事件时的最后一个时间点的大小
	private int 	rate			= 0;		//速率 byte
	private long    testStartTime   = 0;        //测试开始时间保存，用于后面接受计时器时时间不装的微调
	private int 	testRunTimes	= 0;		//测试从进入服务起的时间
	//private int 	linkNetTimes	= 0;		//网络连接时间
	private int 	getFileSizeTimes= 0;		//获得文件大小时间
	private int 	doJobTimes 		= 0;		//工作时间,单位秒
	private int 	noDataTimes 	= 0;		//下载无数据时长
	private int 	repeatTimes 	= 0;		//存放测试服务传过来的当前第几次测试
	private String 	ftpTypeStr 		= "";		//FTP 类型字符串显示 FTP Upload/FTP Download
	*//**是否被人工终止*//*
    protected boolean isInterrupted = false;
	*//**FTP测试参数传递正确*//*
	private boolean hasFtpParas 	= false;
	private boolean isLogonSuccess	= false;	//是否登陆成功
	private boolean isLogonFailure	= false;	//是否有登陆失败事件返回，处理多次返回登陆失败的情况
	private boolean isGetFileSize	= false;	//是否获得文件大小
	private boolean isGetFirstData	= false;	//是否获得第一个数据包
	private boolean isGetLastData   = false;    //是否获得最后一个数据包
	private boolean isDownload 		= true;		//当前测试如果是下载值为true，否则为false
	private Map<String, Integer> totalMap;		//用于业务统计存存结构
	private String 	msgStr			= "";		//
	private long   ftpStartMsTime   = 0;        //FTP测试开始时间
	private long   ftpLastDataMsTime= 0;		//最后一个数据包时间
	private long   previousTotalByte= 0;        //上一次传输总字节数
	private long   previousUseTime  = 0;        //上一次传输时间
	private long 	connectingTime	= 0;		//FTP测试连接开始时间
	//private int currentTimeRepeat	= 0;		//当前次测试重做的次数，如果不为0表示上一次没有测试成功
	private String testStopToTotal	= "1";		//测试结束时是否计入一次有效测试，值为1有效，其它无效
	private String forPioneerStr = "";
	
	//private boolean lastTimeIsSuccess=true;		//上一次是否正常结束，非业务测试才有用，业务测试如果三次FTP都失败的话做中断操作的
	//告警提示相关
    private NotificationManager mNotificationManager;//通知管理器 
    private Notification mNotification;//通知
    private AlarmManager   alarmManager    = null;
    private PendingIntent   pendingTestInt = null;
    
	private RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<ICallback>();
	*//**
	 * 当启动当前服务的地方，需要与本服务绑定时，返回本业务测试的可绑定对象的实现
	 * 包括与本服务的注入注册，供本服务回设使用，反注册
	 * 停止本服务的中断控制
	 *//*
    private IService.Stub mBinder = new IService.Stub() {
        public void unregisterCallback(ICallback cb){   
            if(cb != null) {
                mCallbacks.unregister(cb);   
            }   
        }   
        public void registerCallback(ICallback cb){   
            if(cb != null) {   
                mCallbacks.register(cb);
                isCallbackRegister = true;
            }   
        }
		*//**使止本服务的控制,参数为真表示中断，其它正常结束*//*
		public synchronized void stopTask(boolean isTestInterrupt,int dropReason) throws RemoteException {
			// TODO Auto-generated method stub
			LogUtil.i(tag, "----call stop FtpTest---isLogonSuccess:" + isLogonSuccess 
			+ "--resion:" + RcuEventCommand.DropReason.getDropReason(dropReason).getResonStr());
			isInterrupted = true;
			//如果当前中断状态为是,表示人为中断,需添加DROP 事件,状态为User Stop,且计为成功下载
			if(dropReason != DropReason.NORMAL.getReasonCode()){
				if((isGetFileSize || isGetFirstData) && !isGetLastData){
					ftpLastDataMsTime = System.currentTimeMillis();
					msgStr = repeatTimes +"-"+ftpTypeStr+" "+DataTaskEvent.FTP_DROP.toString()
							+":Delay "+doJobTimes+"(s)"+",Mean Rate:"+UtilsMethod.decFormat.format(getFtpFinishAverageThr())
							+"kbps,Transmit Size:"+UtilsMethod.decFormat.format(totalTransSize*1.0/kByte)+"KBytes"
							+"," + RcuEventCommand.DropReason.getDropReason(dropReason).getResonStr();
					msg = mHandler.obtainMessage(EVENT_CHANGE,msgStr);
					//mHandler.sendMessage(msg);
					resultCallBack(msg);
					
					UtilsMethod.sendWriteRcuEvent(FtpTest.this, isDownload ? RcuEventCommand.FTP_DL_Drop 
																			:RcuEventCommand.FTP_UL_Drop, doJobTimes,(int)totalTransSize
																			,dropReason);
					
					//同时记录统计数据，应该是偿试次数，成功次数都加1
					updateTotalInfo(isTestInterrupt,true);
				}else{
					msgStr = "" + RcuEventCommand.DropReason.getDropReason(dropReason).getResonStr();
					msg = mHandler.obtainMessage(EVENT_CHANGE,msgStr);
					//mHandler.sendMessage(msg);
					resultCallBack(msg);
				}
			}
			
			//调用停止FTP测试动态库
			if(isLogonSuccess){
			    //当登陆成功，FTP任务结束后数据Socket断开完成的时刻，表示一次FTP下载结束,应该有一定的条件
			    UtilsMethod.sendWriteRcuEvent(FtpTest.this,isDownload ? RcuEventCommand.FTP_DL_SocketDisconnected 
			    														: RcuEventCommand.FTP_UL_SocketDisconnected);
    			
			    //发送当前FTP测试连接断开消息
    			msg = mHandler.obtainMessage(EVENT_CHANGE,repeatTimes +"-"+ftpTypeStr+" "+ DataTaskEvent.FTP_SocketDisconnected.toString());
    			LogUtil.w(tag, "--showDisconnected--");
    			if(dropReason != DropReason.NORMAL.getReasonCode()){
    				resultCallBack(msg);
    			}else{
    				mHandler.sendMessage(msg);
    			}
			}
			//uninitserver(jniHandle);
			
			shutdown(jniHandle);
		}
    };  
    
	@Override  
    public IBinder onBind(Intent intent) {   
        return mBinder;   
    }   
  
    @Override  
    public void onCreate() {   
        super.onCreate();
        LogUtil.w(tag,"--onCreate--");
        testStartTime = System.currentTimeMillis();
        //timerChange();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WalkMessage.ACTION_WALKTOUR_TIMER_FTPCHANGED);
        this.registerReceiver(mIntentReceiver, filter, null, null);
        
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        pendingTestInt = PendingIntent.getBroadcast(getApplicationContext(), 0, 
                     new Intent(WalkMessage.ACTION_WALKTOUR_TIMER_FTPCHANGED), PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 
                    1000, pendingTestInt);
        
        ftp = new ConfigFtp();
        //生成通知管理器
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }   
    
    *//**
	 * 测试开始时钟消息，每秒钟刷新一次
	 *//*
	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(WalkMessage.ACTION_WALKTOUR_TIMER_FTPCHANGED)){
    			//本服务收到的消息仅有timerscude,此处节少判断intent时间
    			//如果当前参数传递正确进入计算环节，否则退出消息重发机制
    			if(hasFtpParas){
    				timerScheduleDo();
    			}
			}
		}
	};
	
    private void cleanTimerTask(){
        try{
            LogUtil.w(tag,"--clean cleanTimerTask--");
            if(alarmManager == null){
                alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            }
            if(pendingTestInt == null ){
                pendingTestInt = PendingIntent.getBroadcast(getApplicationContext(), 0, 
                        new Intent(WalkMessage.ACTION_WALKTOUR_TIMER_FTPCHANGED), PendingIntent.FLAG_UPDATE_CURRENT);
            }
            alarmManager.cancel(pendingTestInt);
            pendingTestInt =  null;
            alarmManager = null;
        }catch(Exception e){
            e.printStackTrace();
            LogUtil.w(tag,"--cleanTimerTask faild--");
        }
    }
    
    *//**
	 * 获取当前的测试对象
	 * @param intent	启动Intent
	 * @return	当前的测试对象
	 *//*
	private TaskFtpModel getMyModel(Intent intent) {
	    LogUtil.w(tag,"--intent isnull:"+(intent == null));
	    if(intent != null){
	    	forPioneerStr = intent.getStringExtra(WalkMessage.TestInfoForPioneer);
	    	
    		Bundle bundle = intent.getExtras();
    		Object obj = bundle.getSerializable(WalkCommonPara.testModelKey);
    		if(obj == null){
    			LogUtil.w(tag, "data_key is null");
    			return null;
    		}else{
    			return (TaskFtpModel)obj;
    		}
	    }else{
	        return null;
	    }
	}
	
    @Override
	public void onStart(Intent intent,int startid){
		LogUtil.w(tag,"--onStart--");
		hasFtpParas = false;
		try{
			//接收测试服务传过来的FTP测试相关参数
			ftpModel = getMyModel(intent);
			if(ftpModel == null){
				LogUtil.w(tag,"--onStart Intent Object null Stop!--");
				sendCallBackStop("TestStop");
				return;
			}
			
			repeatTimes = intent.getIntExtra(WalkCommonPara.testRepeatTimes, 0);
			//currentTimeRepeat = intent.getIntExtra("currentTimeRepeat", 0);
			serverNotResponseTime= (long)ftpModel.getLoginTimeOut() * (long)ftpModel.getLoginTimes() +
								(long)ftpModel.getLoginInterval() * (long)ftpModel.getLoginTimes();
			//lastTimeIsSuccess = intent.getBooleanExtra("lastTimeIsSuccess", true);
			LogUtil.w(tag,"---repeatTimes:"+repeatTimes
					+"--noAnswer:"+ftpModel.getNoAnswer()
					+"--timeOut:"+ftpModel.getTimeOut());
			hasFtpParas = true;
		}catch(Exception e){
			e.printStackTrace();
			LogUtil.w(tag,"---Ftp Test getPara null---");
			hasFtpParas = false;
		}
		
		try{
			if(hasFtpParas){
				ftpInfo = ftp.getFtp_All(ftpModel.getFtpServerName());
			}
			//如果当前测试参数获得不成功，中断当前测试
			if(!hasFtpParas ){
				sendCallBackStop("ftp para is null");
				return;
			}else if(ftpInfo == null){
				LogUtil.w(tag,"---ftp server is null---");
				msg = mHandler.obtainMessage(7,getString(R.string.sys_setting_used_isnull));
	            mHandler.sendMessage(msg);
	            
				sendCallBackStop("ftp para is null");
				return;
			}
			pingStr = "ping -c 1 -w 3 "+ ftpInfo[1]+" ";
			
			//根据参数判断类型上传/下载，并处理各自特殊参数
			taskType = WalkStruct.TaskType.valueOf(ftpModel.getTaskType());
			if(taskType.equals(WalkStruct.TaskType.FTPDownload)){
				ftpTypeStr = DataTaskEvent.FTP_TYPE_DOWNLOAD.toString();
				
				//服务开始时修改串口Logmask为FTP模式下
		        Intent ftpStart = new Intent(WalkMessage.FtpTest_Download_Start_Logmask);
		        sendBroadcast(ftpStart);
		        
				isDownload = true;
			}else if(taskType.equals(WalkStruct.TaskType.FTPUpload)){
				ftpTypeStr = DataTaskEvent.FTP_TYPE_UPLOAD.toString();
				fileSize = intent.getLongExtra("fileSize", 1000);
				LogUtil.w(tag,"--getintent size:"+fileSize);
				ftpModel.setFileSize(fileSize);
				
				//服务开始时修改串口Logmask为FTP模式下
		        Intent ftpStart = new Intent(WalkMessage.FtpTest_Upload_Start_Logmask);
		        sendBroadcast(ftpStart);
		        
				isDownload = false;
			}
			
			Thread thread = new Thread(new changeNetwork());
			thread.start();
		}catch(Exception e){
			e.printStackTrace();
			sendCallBackStop("Exception onStart");
		}
	}
    
    *//**按Pioneer需求，在相关时间点往Pioneer发送相关信息*//*
    private void sendPioneerStr(String detailStr){
        if(forPioneerStr != null && !forPioneerStr.equals("")){
            UmpcSwitchMethod.sendEventToController(getApplicationContext(), UMPCEventType.RealTimeData.getUMPCEvnetType(),
                    forPioneerStr + detailStr,UmpcTestInfo.ControlForPioneer);
        }
    }
    
    *//**
     * [调用停止当前业务接口]<BR>
     * [如果当前为手工停止状态不能调用当前停止接口]
     * @param msg
     *//*
    private void sendCallBackStop(String msg){
    	LogUtil.w(tag, "---isInterrupted:" + isInterrupted);
        if(!isInterrupted){
            Message StopMsg = mHandler.obtainMessage(TEST_STOP, msg);
            mHandler.sendMessage(StopMsg);
        }
    }
    
    private float getFtpFinishAverageThr(){
        return (totalTransSize * 8 / ((ftpLastDataMsTime - ftpStartMsTime) / 1000f)) / kByte;
    }
    
    *//**
     * 
     * @param isSuccess 是为下载成功，否为掉线
     * 满足FTP统计完整性，统一更新FTP统计信息
     * 完整性原则：一次FTP测试中，在FTP Download Send RETR之后出现FTP Download Last Data或FTP Download Drop事件则认为该次FTP数据完整，参与统计
     * 根据该完整性，只有在最后收到Last Data或Drop时才需要进行统计，且只需要做一次统计即可
     *//*
    private synchronized void updateTotalInfo(boolean isSuccess,boolean systemErrCall){
    	if(!isTotal){
    		isTotal = true;
    		try{
    		LogUtil.w(tag,"--update total---" + isSuccess + "--isDownload:" + isDownload
    				+"--sub:" + (ftpLastDataMsTime - ftpStartMsTime));
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
    		
    		*//**
    		 * 发送FTP测试完整消息，在当前次FTP测试过程中收集的相关参数可计算统计中
    		 * User Stop、Timeout原因码的FTP Drop当做正常结束处理，统计所有指标
    		 * PPP Drop、No Data、Out of Service原因码的FTP Drop作为FTP掉线处理，仅统计掉线次数，不统计其它相关指标，测量参数不在此限制
    		 *//*
    		if(isSuccess){
	    		Intent ftpTotalFull = new Intent(WalkMessage.TotalByFtpIsFull);
	    		ftpTotalFull.putExtra("IsFtpDown", isDownload);
	    		ftpTotalFull.putExtra("FtpJobTimes", (int)(ftpLastDataMsTime - ftpStartMsTime));
	    		sendBroadcast(ftpTotalFull);
    		}
    		
			//同时记录统计数据，应该是偿试次数，成功次数都加1
			totalMap =new HashMap<String, Integer>();
			totalMap.put(isDownload ? TotalStruct.TotalFtp._downtrys.name():TotalStruct.TotalFtp._uptrys.name(), 1);
			if(isSuccess){
				totalMap.put(isDownload ? TotalStruct.TotalFtp._downSuccs.name():TotalStruct.TotalFtp._upSuccs.name(), 1);
				totalMap.put(isDownload ? TotalStruct.TotalFtp._downDrops.name():TotalStruct.TotalFtp._upDrops.name(), 0);
				//只有成功的时候计累加当前次的平均速率
				float avgeThr = (totalTransSize * 8 / ((ftpLastDataMsTime - ftpStartMsTime) / 1000f)) / kByte;
				totalMap.put(isDownload ? TotalStruct.TotalFtp._downAverageThr.name():TotalStruct.TotalFtp._upAverageThr.name(), 
						(int)(avgeThr * 1000));
				totalMap.put(isDownload ? TotalStruct.TotalFtp._downCurrentSize.name():TotalStruct.TotalFtp._upCurrentSize.name(),
						(int)(totalTransSize * 8 / kByte));
				totalMap.put(isDownload ? TotalStruct.TotalFtp._downCurrentTimes.name():TotalStruct.TotalFtp._upCurrentTimes.name(),
						(int)(ftpLastDataMsTime - ftpStartMsTime));
			}else{
				totalMap.put(isDownload ? TotalStruct.TotalFtp._downDrops.name():TotalStruct.TotalFtp._upDrops.name(), 1);
				totalMap.put(isDownload ? TotalStruct.TotalFtp._downSuccs.name():TotalStruct.TotalFtp._upSuccs.name(), 0);
			}
			//totalMap.put(isDownload ? TotalStruct.TotalFtp._downAllTimes.name():TotalStruct.TotalFtp._upAllTimes.name(),doJobTimes);
			msg = mHandler.obtainMessage(CHART_CHANGE, totalMap);
			if(systemErrCall){
				resultCallBack(msg);
			}else{
				mHandler.sendMessage(msg);
			}
			
			LogUtil.w(tag, "--end updateTotalInfo--");
    	}
    }
    
    *//**统计当前传输大小，时长，瞬时速率，平均速率，最大速率等相关信息*//*
	private void turnTransfersRate(){
		//计算当前每秒传输速度
		totalTransSize = currentSize;								//计算前将当前获得的总大小赋给临时总大小变量，以防计算过程中当前次总大小发生变化
		long currentUseMsTime = System.currentTimeMillis();
		int useMsTime = (int)(currentUseMsTime - ftpStartMsTime);	//当前时间减去第一个数据包时间，得总的任务时间
		int interlTime= (int)(currentUseMsTime - previousUseTime);	//当前时间减去上一个计时时间，得两次计算的间隔时间
		int interBytes = (int)(totalTransSize - previousTotalByte);		//间隔获得数据包大小
		
		doJobTimes = (int)(Math.ceil(useMsTime / 1000f));	//仅用于显示，该值不准确，不参与计算
		Map<String, Object> dataMap =new HashMap<String, Object>();
		
		meanRate = (interBytes * 8 / (interlTime / 1000f))/kByte;
		dataMap.put(isDownload ? WalkStruct.DataTaskValue.FtpDlThrput.name():WalkStruct.DataTaskValue.FtpUlThrput.name()
						, UtilsMethod.decFormat.format(meanRate));
		dataMap.put(DataTaskValue.BordCurrentSpeed.name(), UtilsMethod.decFormat.format(meanRate));
		
		//当前速率大于峰值时替换峰值
		if(meanRate > peakValue) peakValue = meanRate;
		dataMap.put(WalkStruct.DataTaskValue.PeakValue.name(), UtilsMethod.decFormat.format(peakValue));
		//计算当前传输大小
		//dataMap.put(isDownload ? WalkStruct.DataTaskValue.FtpDlThrput.name():WalkStruct.DataTaskValue.FtpUlThrput.name(), UtilsMethod.decFormat.format(totalTransSize*1.0/kByte));

		//当前平均值
		averageRage = (totalTransSize * 8 / (useMsTime / 1000f)) /kByte ;
		dataMap.put(isDownload ? WalkStruct.DataTaskValue.FtpDlMeanRate.name():WalkStruct.DataTaskValue.FtpUlMeanRate.name(), 
						UtilsMethod.decFormat.format(averageRage));
		//时长
		//int times = doJobTimes / timerTimeSwitch + doJobTimes % timerTimeSwitch;
		dataMap.put(WalkStruct.DataTaskValue.useTimes.name(), doJobTimes);
		
		//当前进度FtpDlProgress 如果是PS CALL 按时间比例计算
		process =((ftpModel.getPsCall() == 1) ? 
					(doJobTimes * 1.0f /(ftpModel.getTimeOut() > 0 ? ftpModel.getTimeOut() : 1))
						 : (((totalTransSize * 1.0f) / (fileSize > 0 ? fileSize : 1)))
				) * 100;
		if(process>100)process=100;
		dataMap.put(isDownload ? WalkStruct.DataTaskValue.FtpDlProgress.name():WalkStruct.DataTaskValue.FtpUlProgress.name(), 
						UtilsMethod.decFormat.format(process));
		dataMap.put(DataTaskValue.BordProgress.name(),UtilsMethod.decFormat.format(process));
		        
		dataMap.put(isDownload ? WalkStruct.DataTaskValue.FtpDlCurrentSize.name() : WalkStruct.DataTaskValue.FtpUlCurrentSize.name(), 
				UtilsMethod.decFormat.format(totalTransSize * 8f / kByte));
		dataMap.put(isDownload ? WalkStruct.DataTaskValue.FtpDlAllSize.name() : WalkStruct.DataTaskValue.FtpUlAllSize.name(), 
				UtilsMethod.decFormat.format(fileSize * 8f / kByte));
		
		dataMap.put(DataTaskValue.BordLeftTitle.name(), getString(R.string.info_data_averagerate)
		        + "@" + UtilsMethod.decFormat.format(averageRage) 
		        + getString(R.string.info_rate_kbps));
        
		msg = mHandler.obtainMessage(DATA_CHANGE,dataMap);
		mHandler.sendMessage(msg);
		
		*//**发送瞬时速率*//*
		Intent ftpRate = new Intent(WalkMessage.FtpTest_Times_Rate);
		ftpRate.putExtra("Flag", 'F');
		ftpRate.putExtra("udFlag", (isDownload ? (char)0x00 : (char)0x01));
		ftpRate.putExtra("MSec", useMsTime);
		ftpRate.putExtra("TotalBytes",(int)totalTransSize);
		ftpRate.putExtra("InterlTime", interlTime);
		ftpRate.putExtra("interlbytes", interBytes);
		sendBroadcast(ftpRate);
		UtilsMethod.sendWriteRcuFtpData(getApplicationContext(),WalkCommonPara.MsgDataFlag_F,
				(isDownload ? 0x00 : 0x01), useMsTime, totalTransSize, interlTime, interBytes);
		
		LogUtil.w(tag, "---useMsTime="+useMsTime+" totalbytes="+totalTransSize+" InterlTime="+interlTime
				+" interlbytes="+(totalTransSize - previousTotalByte)+"--meanRate:"+meanRate+"--averageRage:"+averageRage);
		sendPioneerStr(",DurationTime=" + useMsTime + ",FileSize=" + fileSize 
		        + ",TransmitSize=" + totalTransSize + ",InstRate=" + (interBytes * 8));
		previousTotalByte = totalTransSize;
		previousUseTime = currentUseMsTime;
		rate=0;
    }

	*//**
	 * [时间微调方法]<BR>
	 * [根据传进来的时间，对于所有的有效计算时间都+相差时间]
	 * @param addTime
	 *//*
	private void checkTotalTime(int addTime){
	    LogUtil.w(tag,"--addTime:"+addTime);
	    testRunTimes += addTime;
	    if(getFileSizeTimes != 0) getFileSizeTimes += addTime;
	    if(noDataTimes != 0) noDataTimes += addTime;
	    //if(doJobTimes != 0) doJobTimes += doJobTimes;
	}
	
	*//**工作时间计时器，用于产生每秒速率，登陆超时，下载超时等处理*//*
	private void timerScheduleDo(){
    	try{
    	    testRunTimes ++;
            if(isLogonSuccess && !isGetFileSize) getFileSizeTimes++;    //记录登成功后等待获得文件大小时间
            if(isGetFileSize)   noDataTimes++;                          //当获得文件大小时开始记录无数据时间,当有收到数据包时置0
            if(isGetFirstData && !isGetLastData)  turnTransfersRate();  //当收到第一个数据包后且当前非最后一个数据包状态，计算传输速率
            
    	    if(testRunTimes % 5 == 0){
    	        //每5秒校验一次，如果开始时间到当前时间减计算运行时间大于2S,则所有计算时间都加上相差时间
    	        if(Math.abs((System.currentTimeMillis() - testStartTime) /1000 - testRunTimes) > 2){
    	            checkTotalTime((int)((System.currentTimeMillis() - testStartTime) /1000 - testRunTimes));
    	        }
    	        if(testRunTimes % 10 == 0){
        	        LogUtil.w(tag,"---Ftp testing Timer Run:"+testRunTimes
        	                +"--getFileSizeTimes:"+getFileSizeTimes+"--noDataTimes:"+noDataTimes
        	                +"--isGetFirstData:"+isGetFirstData+"getTimeOut():"+ftpModel.getTimeOut()+"--doJobTimes:"+doJobTimes
        	                +"--noAnswer:"+ftpModel.getNoAnswer()+"--isNoDataOut:"+(ftpModel.getNoAnswer()< noDataTimes));
    	        }
    	    }
    	    
			// 连续超过指定时间没有数据传输且尝试ping
			if(noDataTimes != 0 && noDataTimes % 5 == 0 && noDataTimes <ftpModel.getNoAnswer()){
				LogUtil.w(tag,"---noDataTimes:"+noDataTimes+"--to Ping--"+pingStr
						+"--ping result:"+UtilsMethod.getLinuxCommandResult(pingStr));
			}
			
			if(ftpModel.getNoAnswer()< noDataTimes){
				//当收到文件大小后开始计时,如超过无显应时间Drop当前测试,报超时事件  -103 No Data
				LogUtil.w(tag,"--noanswer timer 1--isGetFileSize:"+isGetFileSize+"--isGetFirstData:"+isGetFirstData);
				if(isGetFileSize || isGetFirstData && !isGetLastData){
					isGetLastData = true;
					ftpLastDataMsTime = System.currentTimeMillis();
					
					UtilsMethod.sendWriteRcuEvent(FtpTest.this,isDownload ? RcuEventCommand.FTP_DL_Drop
																			:RcuEventCommand.FTP_UL_Drop, doJobTimes, (int)totalTransSize
																			, RcuEventCommand.DROP_NODATA);
					msgStr = repeatTimes +"-"+ftpTypeStr+" "+DataTaskEvent.FTP_DROP.toString()
							+":Delay "+doJobTimes+"(s)"+",Mean Rate:"+UtilsMethod.decFormat.format(getFtpFinishAverageThr())
							+"kbps,Transmit Size:"+UtilsMethod.decFormat.format(totalTransSize*1.0/kByte)+"KBytes"
							+",NoDataTimeout";
					msg = mHandler.obtainMessage(EVENT_CHANGE,msgStr);
					mHandler.sendMessage(msg);
					
					updateTotalInfo(false,false);
				}
				
				sendCallBackStop((isGetFileSize || isGetFirstData) ? "1" : "noAnswer");
			}else if(ftpModel.getTimeOut() <= doJobTimes){
			    if(!isGetLastData){
			        isGetLastData = true;
			        
    			    //当FTP传输时间大于超时时间按照PASS模式报最后一个数据事件或超时事件	 doJobTimes为开始下载时间
    				long finishDelay = ftpModel.getTimeOut() * 1000 - (System.currentTimeMillis() - ftpStartMsTime);
    				LogUtil.w(tag,"--job timeout 2--finishDelay:"+finishDelay);
    				//如果当前时间大于0，表示业务过程未达到超时时间，需要等待时到完整再结束测试
    				//如果该值超过1000表示大于1秒，需要启线程等待，否则会让程序挂住,后面如果有问题加
    				if(finishDelay > 0 && finishDelay < 4000){	//误差在4秒内才休眠
    					try {
    						Thread.sleep(finishDelay);
    					} catch (InterruptedException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
    				}
    				ftpLastDataMsTime = System.currentTimeMillis();
    				if(ftpModel.getPsCall() == 1){
    					//当为PsCall测试方式时,超过超时时间时,报的是LastData事件
    					UtilsMethod.sendWriteRcuEvent(FtpTest.this,isDownload ? RcuEventCommand.FTP_DL_LastData : RcuEventCommand.FTP_UL_LastData,
    															(int)(totalTransSize > fileSize ? fileSize : totalTransSize));	//20110128当前下载大于文件大小时
    					
    					msgStr = repeatTimes +"-"+ftpTypeStr+" "+DataTaskEvent.FTP_LAST_DATA.toString()
    							+":Delay "+doJobTimes+"(s)"
    							+",Mean Rate:"+UtilsMethod.decFormat.format(getFtpFinishAverageThr())
    							+"kbps,Transmit Size:"
    							+UtilsMethod.decFormat.format((totalTransSize > fileSize ? fileSize : totalTransSize)*1.0/kByte)+"KBytes";
    					msg = mHandler.obtainMessage(EVENT_CHANGE,msgStr);
    					mHandler.sendMessage(msg);
    					
    				}else{
    					//当下载时间超时后,Drop当前测试,报下载超时事件
    					UtilsMethod.sendWriteRcuEvent(FtpTest.this,isDownload ? RcuEventCommand.FTP_DL_Drop : RcuEventCommand.FTP_UL_Drop,
    																doJobTimes, (int)(totalTransSize > fileSize ? fileSize : totalTransSize)
    																,RcuEventCommand.DROP_TIMEOUT);	//20110128当前下载大于文件大小时
    					//LogUtil.w(tag,"Rcu:"+(isDownload ? "FTP_DL_Drop":"FTP_UL_Drop")+" TimeOut");
    					
    					msgStr = repeatTimes +"-"+ftpTypeStr+" "+DataTaskEvent.FTP_DROP.toString()
    							+":Delay "+doJobTimes+"(s)"+",Mean Rate:"+UtilsMethod.decFormat.format(getFtpFinishAverageThr())
    							+"kbps,Transmit Size:"
    							+UtilsMethod.decFormat.format((totalTransSize > fileSize ? fileSize : totalTransSize)*1.0/kByte)+"KBytes"
    							+",Timeout";
    					msg = mHandler.obtainMessage(EVENT_CHANGE,msgStr);
    					mHandler.sendMessage(msg);
    					
    				}
    				//不区分passcall模式，所有超时都认为是成功的 
    				updateTotalInfo(true,false);
    				
    				sendCallBackStop("1");
			    }
			}else if(ftpModel.getTimeOut() + ( testRunTimes - doJobTimes) < testRunTimes){
				LogUtil.w(tag,"--all testtime out 3--");
				if(isGetFileSize || isGetFirstData && !isGetLastData){
					isGetLastData = true;
				    ftpLastDataMsTime = System.currentTimeMillis();
					
					UtilsMethod.sendWriteRcuEvent(FtpTest.this,isDownload ? RcuEventCommand.FTP_DL_Drop
																			:RcuEventCommand.FTP_UL_Drop, doJobTimes, (int)totalTransSize
																			,RcuEventCommand.DROP_NODATA);
					msgStr = repeatTimes +"-"+ftpTypeStr+" "+DataTaskEvent.FTP_DROP.toString()
							+":Delay "+doJobTimes+"(s)"+",Mean Rate:"+UtilsMethod.decFormat.format(getFtpFinishAverageThr())
							+"kbps,Transmit Size:"+UtilsMethod.decFormat.format(totalTransSize*1.0/kByte)+"KBytes"
							+",NoDataTimeout";
					msg = mHandler.obtainMessage(EVENT_CHANGE,msgStr);
					mHandler.sendMessage(msg);
					
					updateTotalInfo(false,false);
				}

				sendCallBackStop((isGetFileSize || isGetFirstData) ? "1" : "timeOut Not Total");
			}else if(!isLogonSuccess && (serverNotResponseTime ) <testRunTimes){	//(ftpModel.getTimeOut() ++ linkNetTimes)
			    //如果当前登陆FTP服务器不成功且登陆时间大于超时时间则报连接FTP失败,中断测试
				LogUtil.w(tag,"--not login time out 4--"+isLogonFailure);
			    if(!isLogonFailure){
			    	isLogonFailure = true;
			    	//FTP下载控制，登录FTP服务器是否成功 0失败,1成
					//UtilsMethod.sendWriteRcuEvent(FtpTest.this,isDownload ? RcuEventCommand.FTP_DL_CtrlSock_Finished
					//		:RcuEventCommand.FTP_UL_CtrlSock_Finished,0);
			    	int delay = (connectingTime == 0 ? 0 : (int)(System.currentTimeMillis() - connectingTime));
					UtilsMethod.sendWriteRcuEvent(FtpTest.this,isDownload ? RcuEventCommand.FTP_DL_ConnectSockFailed
							:RcuEventCommand.FTP_UL_ConnectSockFailed,delay);
					
			    	msgStr = repeatTimes +"-"+ftpTypeStr+" "+DataTaskEvent.FTP_CONNECT_FAILED.toString();
		            msg = mHandler.obtainMessage(EVENT_CHANGE,msgStr);
		            mHandler.sendMessage(msg);
		            
		            msg = mHandler.obtainMessage(7,getString(R.string.Sys_Check_FtpNet_Set));
	                mHandler.sendMessage(msg);
	                
		            sendCallBackStop(getString(R.string.Sys_Check_FtpNet_Set));
			    }
			}else if(isLogonSuccess && !isGetFileSize && (ftpModel.getLoginTimeOut()) < getFileSizeTimes){
				//(ftpModel.getNoAnswer()+ linkNetTimes)
				//当登陆成功后突然脱网，等待数据文件大小超过超时间后，停止当前测试
				LogUtil.w(tag,"-----login ftp is success bu not get first data 5----");
				
				UtilsMethod.sendWriteRcuEvent(FtpTest.this,isDownload ? RcuEventCommand.FTP_DL_Drop
						:RcuEventCommand.FTP_UL_Drop, doJobTimes, (int)totalTransSize, RcuEventCommand.DROP_NODATA);
				
				msgStr = repeatTimes +"-"+ftpTypeStr+" "+DataTaskEvent.FTP_DROP.toString()
						+":Delay(s)"+doJobTimes+",Mean Rate:"+UtilsMethod.decFormat.format(getFtpFinishAverageThr())
						+"kbps,Transmit Size:"+UtilsMethod.decFormat.format(totalTransSize*1.0/kByte)+"KBytes"
						+",NoDataTimeout";
				msg = mHandler.obtainMessage(EVENT_CHANGE,msgStr);
				mHandler.sendMessage(msg);
				
				msg = mHandler.obtainMessage(EVENT_CHANGE,repeatTimes + "-" + DataTaskEvent.FTP_SERVER_NORESPONSE.toString());
				mHandler.sendMessage(msg);
				
				msg = mHandler.obtainMessage(7,getString(R.string.Sys_Check_FtpNet_Set));
                mHandler.sendMessage(msg);
                
	            sendCallBackStop(getString(R.string.Sys_Check_FtpNet_Set));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
    
    *//**
     * 启动时由于等待网络切换原因,可能会阻塞一段时间,所以需另启线程执行
     * 在此处写入模拟拨号RCU事件
     * @author tangwq
     *//*
    class changeNetwork implements Runnable{
    	public void run(){
    		while(!isCallbackRegister);

			callFtpServerJniBy();
    	}
    }
    private int getTcpPort(int faildPort){
		if(faildPort != 0 ) return faildPort + 1;
		else return 8201;
	}
    
    private String getTargetNameAddNum(String name){
        if(name.endsWith("/")){
            return name;
        }else if(name.lastIndexOf(".") >0){
            return name.substring(0,name.lastIndexOf(".")) + (repeatTimes % 20) + name.substring(name.lastIndexOf("."));
        }else{
            return name + (repeatTimes % 20);
        }
    }
    FTP上传时，在FTP服务器上的文件名为FTP+手机的IMEI号减前8位+当前重复次数模5，即从1到5变化
    private String getImeiName(){
    	TelephonyManager tm = (TelephonyManager) getApplicationContext()
		.getSystemService(Context.TELEPHONY_SERVICE);
    	String imei = tm.getDeviceId();
    	return imei.substring((imei.length() > 8 ? 8 : 0),imei.length()) + "-" + (repeatTimes % 20);
    }
    *//**
     * 通过JNI调用FTP测试过程
     *//*
    private void callFtpServerJniBy() {
		if(ftpModel==null) return ;
		jniHandle = inithandle();						//1，初始化JNI全局句柄
		JNICallbackInit(new ResultModel(),jniHandle);	//2，初始化FTP测试库的回调信息
		
		ftpTestPath = systemFilePath+"ftp_test";
		
		if(ftpInfo == null){
			msg = mHandler.obtainMessage(8,getString(R.string.Sys_Check_APN_Set));
            mHandler.sendMessage(msg);
            
		    sendCallBackStop(getString(R.string.Sys_Check_APN_Set));
		    return;
		}
		
		TcpModel model = new TcpModel();
		model.tcpPath = "";//"tcp://:8201";//+appModel.getTcpPort();
		model.device = Deviceinfo.getInstance().getPppName();
		String ftppath = "ftp://"+ftpInfo[3]+":"+ftpInfo[4]+"@"+ftpInfo[1]+(ftpInfo[2].trim().equals("")? "" : ":"+ftpInfo[2]);
		model.item = OperaType.Type_FTP;
		model.ftptestPath=ftpTestPath;//systemFilePath+"ftp_test";
		model.mode=(ftpModel.getPassive() == 0 ? 1 : 0);	//标题为是否被动模式，下拉项 0 为否，1为是；传给底层库时 0是被动模式，1是主动模式,故此处需将值反过来
		model.connect_timeout=ftpModel.getTimeOut();		//超时时间
		model.nodata_timeout=ftpModel.getNoAnswer();		//无响应时间
		model.connect_count = ftpModel.getLoginTimes();		//重新登陆次数
		model.connect_inteval=ftpModel.getLoginInterval();	//重新登陆间隔
		
		switch(taskType){
		case FTPUpload:
			model.ftpMsg = ftppath+(ftpModel.getRemoteFile().trim().equals("") ? "/FTPU-"+getImeiName() : 
                        			    (ftpModel.getRemoteFile().startsWith("/")?"":"/") 
                        			    + getTargetNameAddNum(ftpModel.getRemoteFile())
                        			    + (ftpModel.getRemoteFile().endsWith("/") ? "FTPU-"+getImeiName() : ""));
			model.command = OperaType.Command_UPLOAD;
			model.stfile=systemFilePath+"ftpST";
			model.localfile=ftpModel.getLocalFile().trim();
			model.upload_size =ftpModel.getFileSize();   //传入的单位为kbyte,此处要求传入的单位为byte
			model.pth_num = 1;
			LogUtil.w(tag,"---local:"+model.localfile+"--size:"+model.upload_size);
			break;
		case FTPDownload:
			model.ftpMsg = ftppath+(ftpModel.getRemoteFile().startsWith("/")?"":"/")+ftpModel.getRemoteFile();
			model.command = OperaType.Command_DOWNLOAD;
			model.pth_num=ftpModel.getThreadNumber();
			model.stfile="";
			model.localfile=systemFilePath+"ftpST";
			model.upload_size = 0;
			break;
		} 
		File file = new File(systemFilePath+"ftpST.st");
		if(file.exists()){
			file.delete();
		}
		
		getpars(model,jniHandle);							//3，传递FTP测试相关的参数信息
		
		int tcpPort =0;
		int initServerType = 0;
		int initTimes = 0;
		while(initServerType == 0 && initTimes < 10){
			if(initTimes != 0){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			tcpPort = getTcpPort(tcpPort);
			String tcpPath = "tcp://:"+tcpPort;
			initServerType = initserver(tcpPath,jniHandle);	//4，初始化启动TCP服务端
			initTimes ++ ;
		}
		//如果初始化TCP服务端失败，任务终止返回
		if(initServerType == 0){
			msgStr = repeatTimes +"-"+ftpTypeStr+" "+DataTaskEvent.FTP_DEFAULT_EVENT.toString();
			msg = mHandler.obtainMessage(EVENT_CHANGE,msgStr);
			mHandler.sendMessage(msg);
			
			sendCallBackStop(DataTaskEvent.FTP_DEFAULT_EVENT.toString());
			LogUtil.w(tag,"--Init TCP Server Faild--");
			return;
		}
		LogUtil.d(tag, "begin FTP TEST");
		testProcId = runtest(ftpTestPath,jniHandle);		//5，启动FTP测试客户端进程
		//UtilsMethod.runRootCommand(getFilesDir().getParent()+"/ftp_test -d "+ model.device+" -t tcp://:" +tcpPort);
		//UtilsMethod.runRootCommand(getFilesDir().getParent()+"/radiooptions 8 10010");
		LogUtil.d(tag, "End FTP TEST");
		
		//twq20120414连接开始事件写到调用FTP库后面，防止此处超时无响应后无FTP CONNECT事件
		UtilsMethod.sendWriteRcuEvent(FtpTest.this,isDownload ? RcuEventCommand.FTP_DL_CtrlSockConnecting
				:RcuEventCommand.FTP_UL_CtrlSockConnecting);
		connectingTime	= System.currentTimeMillis();
		
		msgStr = repeatTimes +"-"+ftpTypeStr+" "+DataTaskEvent.FTP_CONNECT_BEGIN.toString();
		msg = mHandler.obtainMessage(EVENT_CHANGE,msgStr);
		mHandler.sendMessage(msg);
		
		LogUtil.w(tag,"----to do runtest 1:"+testProcId+"--write ftp connect begin event--");
		if(testProcId == 0 ) return ;
		//Thread checkProc = new Thread(new CheckTestProcess());//监控测试客户端进程是否存在，如果不存在重启的线程
		//checkProc.start();
	}
    *//**
	 * 启动检测测试进程是否存在
	 * @author tangwq
	 *
	 *//*
	class CheckTestProcess implements Runnable{
		public void run(){
			try {
				Thread.sleep(1000 * 3);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(CheckLive(testProcId)==0){
				LogUtil.w(tag,"----to do runtest 2:"+testProcId);
				testProcId = runtest(ftpTestPath,jniHandle);
			}
		}
	}
	
	@Override  
    public void onDestroy() {   
		super.onDestroy();   
    	LogUtil.d(tag, "onDestroy");
    	cleanTimerTask();
    	
    	//当前FTP测试结束后，将串口Logmask改为测试模式下的相关设置
    	Intent ftpEnd = new Intent(WalkMessage.FtpTest_End_Logmask);
        sendBroadcast(ftpEnd);
        
        //退出前清除由于其它原因无法关闭的FTP进程
        //runtest("killall ftp_test",jniHandle);
        //android.os.Process.killProcess(testProcId);
        if(testProcId != 0){
    		android.os.Process.killProcess(testProcId);
    		ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);     
    		am.restartPackage(ftpTestPath);
    		am = null;
    	}
    	
        mHandler.removeMessages(0);   
        mCallbacks.kill();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
	
	//显示通知
    private void showNotification(String tickerText,Class<?> cls) {  
        // notification //通知图标, 状态栏显示的通知文本提示,通知产生的时间
        mNotification = new Notification(R.drawable.walktour,tickerText, System.currentTimeMillis());  
        //Intent 点击该通知后要跳转的Activity
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,new Intent(this,(cls != null ? cls : TestService.class)),0);  
        mNotification.contentIntent = contentIntent;
        // must set this for content view, or will throw a exception
        //如果想要更新一个通知，只需要在设置好notification之后，再次调用 setLatestEventInfo(),然后重新发送一次通知即可，即再次调用notify()。
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotification.setLatestEventInfo(this, getString(R.string.sys_alarm),tickerText,contentIntent);
        mNotificationManager.notify(R.string.service_started, mNotification);  
    }
    
    *//**
	 * 测试开始时钟消息，每秒钟刷新一次
	 *//*
    private void timerChange(){
    	mHandler.sendEmptyMessageDelayed(TIMER_CHANG, 1000);
    	//如果当前参数传递正确进入计算环节，否则退出消息重发机制
		if(hasFtpParas){
			timerScheduleDo();
		}
    }
    
    private void resultCallBack(Message msg) {
    	int N = mCallbacks.beginBroadcast();
    	try {   
            for (int i = 0; i < N; i++) {
            	if(mCallbacks.getBroadcastItem(i) != null){
	            	switch(msg.what){
	            	case EVENT_CHANGE:    //发送业务事件改变消息
	            		mCallbacks.getBroadcastItem(i).OnEventChange(msg.obj.toString());
	            		break;
	            	case CHART_CHANGE:    //发送图表业务数据改变消息
	            		if(msg.obj != null){
	            			mCallbacks.getBroadcastItem(i).onChartDataChanged((Map<?, ?>)msg.obj);
	            		}
	            		break;
	            	case DATA_CHANGE:    //发送业务数据改变消息
	            		if(msg.obj != null){
	            		    Map tempMap = (Map) msg.obj;
	                        tempMap.put(TaskTestObject.stopResultName, ftpModel.getTaskName());
	            			mCallbacks.getBroadcastItem(i).OnDataChanged(tempMap);
	            		}
	            		break;
	            	case TEST_STOP:    //设用停止当前业务测试
	            		//测试停止时清除工作时间计时器
	        			testStopToTotal = msg.obj.toString();
	        			Map<String,String> resultMap = TaskTestObject.getStopResultMap(ftpModel);
	        			resultMap.put(TaskTestObject.stopResultState, testStopToTotal);
	        			
	            		mCallbacks.getBroadcastItem(i).onCallTestStop(resultMap);
	            		break;
	            	case 7:    //发送通知提示，点击不跳转到相应Intent
	            	    showNotification(msg.obj.toString(),TestService.class);
	            	    break;
	            	case 8:    //发送通知提示，点击跳转到APN设置Intent
	            	    showNotification(msg.obj.toString(),SysSettingAPN.class);
	            	    break;
	            	case 9:	//提示业务测试失败及发送停止业务测试
	            		//mCallbacks.getBroadcastItem(i).onCallTestStop(msg.obj.toString());
	            		
	            		Intent intent = new Intent(WalkMessage.Action_Walktour_Test_Interrupt);
	            		sendBroadcast(intent);
	            		break;
	            	case TIMER_CHANG:
	            		timerChange();
	            		break;
	            	}
            	}
            }   
        } catch (RemoteException e) {   
            LogUtil.w(tag, "", e);   
        } finally{
        	try{
        		mCallbacks.finishBroadcast();
        	}catch(IllegalArgumentException ie){
        		LogUtil.w(tag, "",ie);
        	}
        }
    }   
  
    private Handler mHandler = new Handler() {   
        @Override  
        public void handleMessage(Message msg) {   
        	super.handleMessage(msg);
        	resultCallBack(msg);
    	}   
    }; 
    
	static {
		System.loadLibrary("ipc");
	}
	public native int inithandle();	//返回值: 0失败，成功返回句柄
	public native int JNICallbackInit(ResultModel model,int handle);//入参:  类 全局句柄;返回值: 1成功 0失败
	public native void getpars(TcpModel model,int handle); 
	public native int initserver(String tcppath,int handle);	//返回值: 0失败，1成功
	public native int runtest(String testPath,int handle);	//入参: 需要运行的可执行文件路径;返回值: 0失败，其他为进程id
	public native int CheckLive(int handle);
	public native void shutdown(int handle);
	public native void uninitserver(int handle);
	public native int  msggetinteger(int handle);
	public native int msggetstring(int handle);
	
	*//**
	 * 登陆服务器失败次数，FTP库会根据设置的次数去登陆，并每失败一次都会往回传一次，需在业务层计记失败的次数并做控制
	 *//*
	private int loginFaildTimes	= 0;
	*//**
	 * JNI回调函数
	 * 
	 * @param reuslt
	 *//*
	public void callback(String reuslt){
		String[] results = reuslt.split("@@");
		
		int event = Integer.parseInt(results[0]);
		switch(event){
		case OperaType.FTP_CONNECT_BEGIN:
			//FTP下载控制连接Socket开始建立
			//twq20120414 FTP真正的连接动作事件放到调用FTP库动作之后去，因调用之前可能因无响应会有超时处理，此时可能FTP连接事件不完整
			LogUtil.w(tag,"---FTP CONNECT BEGIN---");
			UtilsMethod.sendWriteRcuEvent(FtpTest.this,isDownload ? RcuEventCommand.FTP_DL_CtrlSockConnecting
					:RcuEventCommand.FTP_UL_CtrlSockConnecting);
			
			msgStr = repeatTimes +"-"+ftpTypeStr+" "+DataTaskEvent.FTP_CONNECT_BEGIN.toString();
			msg = mHandler.obtainMessage(EVENT_CHANGE,msgStr);
			mHandler.sendMessage(msg);
			break;
		case OperaType.FTP_CONNECT_SUCCESS:
			loginFaildTimes = 0;
			msgStr = repeatTimes +"-"+ftpTypeStr+" "+DataTaskEvent.FTP_CONNECT_SUCCESS.toString();
			msg = mHandler.obtainMessage(EVENT_CHANGE,msgStr);
			mHandler.sendMessage(msg);
			break;
		case OperaType.FTP_CONNECT_FAILED:
			loginFaildTimes ++;
			LogUtil.w(tag,"---FTP_CONNECT_FAILED---"+isLogonFailure +"--FaildTimes:"+loginFaildTimes);
			if(!isLogonFailure && loginFaildTimes>=ftpModel.getLoginTimes()){
				isLogonFailure = true;
				
				//FTP下载控制，登录FTP服务器是否成功 0失败,1成功
				int delay = (connectingTime == 0 ? 0 : (int)(System.currentTimeMillis() - connectingTime));
				//UtilsMethod.sendWriteRcuEvent(FtpTest.this,isDownload ? RcuEventCommand.FTP_DL_CtrlSock_Finished
				//		:RcuEventCommand.FTP_UL_CtrlSock_Finished,0);
				UtilsMethod.sendWriteRcuEvent(FtpTest.this,isDownload ? RcuEventCommand.FTP_DL_ConnectSockFailed
						:RcuEventCommand.FTP_UL_ConnectSockFailed,delay);
				
				msgStr = repeatTimes +"-"+ftpTypeStr+" "+DataTaskEvent.FTP_CONNECT_FAILED.toString();
				msg = mHandler.obtainMessage(EVENT_CHANGE,msgStr);
				mHandler.sendMessage(msg);
				
				sendCallBackStop(DataTaskEvent.FTP_CONNECT_FAILED.toString());
			}
			break;
		case OperaType.FTP_LOGIN_SUCCESS:
			isLogonSuccess = true;
			//FTP下载控制，登录FTP服务器是否成功 0失败,1成功
			int delays = (connectingTime == 0 ? 0 : (int)(System.currentTimeMillis() - connectingTime));
			//UtilsMethod.sendWriteRcuEvent(FtpTest.this,isDownload ? RcuEventCommand.FTP_DL_CtrlSock_Finished
			//		:RcuEventCommand.FTP_UL_CtrlSock_Finished,1);
			UtilsMethod.sendWriteRcuEvent(FtpTest.this,isDownload ? RcuEventCommand.FTP_DL_ConnectSockSucc
					:RcuEventCommand.FTP_UL_ConnectSockSucc,delays);
			
			msgStr = repeatTimes +"-"+ftpTypeStr+" "+DataTaskEvent.FTP_LOGIN_SUCCESS.toString();
			msg = mHandler.obtainMessage(EVENT_CHANGE,msgStr);
			mHandler.sendMessage(msg);
			break;		
		case OperaType.FTP_LOGIN_FAILED:
			LogUtil.w(tag,"---FTP_LOGIN_FAILED---");
			if(!isLogonFailure){
				isLogonFailure = true;
				//FTP下载控制，登录FTP服务器是否成功 0失败,1成功
				int delayf = (connectingTime == 0 ? 0 : (int)(System.currentTimeMillis() - connectingTime));
				//UtilsMethod.sendWriteRcuEvent(FtpTest.this,isDownload ? RcuEventCommand.FTP_DL_CtrlSock_Finished
				//		:RcuEventCommand.FTP_UL_CtrlSock_Finished,0);
				UtilsMethod.sendWriteRcuEvent(FtpTest.this,isDownload ? RcuEventCommand.FTP_DL_ConnectSockFailed
						:RcuEventCommand.FTP_UL_ConnectSockFailed,delayf);
				
				msgStr = repeatTimes +"-"+ftpTypeStr+" "+DataTaskEvent.FTP_LOGIN_FAILED.toString();
				msg = mHandler.obtainMessage(EVENT_CHANGE,msgStr);
				mHandler.sendMessage(msg);
	
				sendCallBackStop(DataTaskEvent.FTP_LOGIN_FAILED.toString());
			}
			break;
		case OperaType.FTP_FILE_NOT_FOUND:
			msg = mHandler.obtainMessage(EVENT_CHANGE,DataTaskEvent.FTP_FILE_NOT_FOUND.toString());
			mHandler.sendMessage(msg);
			
			sendCallBackStop(DataTaskEvent.FTP_FILE_NOT_FOUND.toString());
			break;
		case OperaType.FTP_CWD_ERROR:
			//进入指定目录出错，如果当前为FTP下载则发送文件名或目录下存在结束当前次，如果当前为上传的话，会创建指定目录此处不做处理
			LogUtil.w(tag,"---FTP_CWD_ERROR---");
			if(isDownload){
				msg = mHandler.obtainMessage(EVENT_CHANGE,DataTaskEvent.FTP_CWD_ERROR.toString());
				mHandler.sendMessage(msg);
				
				sendCallBackStop(DataTaskEvent.FTP_CWD_ERROR.toString());
			}
			break;
		case OperaType.FTP_MKD_ERROR:
			msg = mHandler.obtainMessage(EVENT_CHANGE,DataTaskEvent.FTP_MKD_ERROR.toString());
			mHandler.sendMessage(msg);
			
			sendCallBackStop(DataTaskEvent.FTP_MKD_ERROR.toString());
			break;
		case OperaType.FTP_FILE_SIZE:
			//此处如果为文件大小，那么需要显示获得文件大小事件
			//isGetFileSize = true;
			//data = results[2];
			fileSize =msggetinteger(jniHandle);
			LogUtil.w(tag,"-----FTP_FILE_SIZE:"+fileSize);
			break;
		case OperaType.FTP_RETR_OR_STOR:
			isGetFileSize = true;
			ftpStartMsTime = System.currentTimeMillis();
			previousUseTime = ftpStartMsTime;
			
			//向FTP服务端发送RETR命令的时刻,记录需下载文件大小
			UtilsMethod.sendWriteRcuEvent(FtpTest.this,isDownload ? RcuEventCommand.FTP_DL_SendRetrCmd 
																	: RcuEventCommand.FTP_UL_SendStorCmd,(int)fileSize);
			//LogUtil.w(tag,"Rcu:"+(isDownload ? "FTP_DL_SendRetrCmd ": "FTP_UL_SendStorCmd")+" sileSize");
			
			String eventTypeStr = (isDownload ?DataTaskEvent.FTP_DL_RETR.toString()
												:DataTaskEvent.FTP_UL_STOR.toString());
			msgStr = repeatTimes +"-"+ftpTypeStr+" "+eventTypeStr+":"+UtilsMethod.decFormat.format(fileSize * 1.0/kByte)+"(KByte)";
			msg = mHandler.obtainMessage(EVENT_CHANGE,msgStr);
			mHandler.sendMessage(msg);
			
			//twq20111012B算法根据收到文件大小开始统计FTP测试过程中的参数信息
			Intent getretrOstor = new Intent(WalkMessage.FtpTest_RETR_OR_STOR);
			sendBroadcast(getretrOstor);
			LogUtil.w(tag,"------ftp start------");
			break;
		case OperaType.FTP_FIRST_DATA:
			isGetFirstData = true;
			//下载计时从获得文件大小开始
			//ftpStartMsTime = System.currentTimeMillis();
			//previousUseTime = ftpStartMsTime;
			//FTP下载收到第一个应用层数据包的时刻
			UtilsMethod.sendWriteRcuEvent(FtpTest.this,isDownload ? RcuEventCommand.FTP_DL_FirstData 
																	: RcuEventCommand.FTP_UL_FirstData);
			
			msgStr = repeatTimes +"-"+ftpTypeStr+" "+DataTaskEvent.FTP_FIRST_DATA.toString();
			msg = mHandler.obtainMessage(EVENT_CHANGE,msgStr);
			mHandler.sendMessage(msg);
			
			//twq20111012A算法根据收到first data开始统计FTP测试过程中的参数信息
			//Intent getFirstData = new Intent(WalkMessage.FtpTest_Get_FirstData);
			//sendBroadcast(getFirstData);
			break;
		case OperaType.FTP_CUR_DATA:
			//data = results[2];
			int packSize = msggetinteger(jniHandle);
			if(packSize != 0)
				noDataTimes = 0;
			rate +=packSize;
			currentSize+=packSize;
			break;
		case OperaType.FTP_LAST_DATA:
		    if(!isGetLastData){
		        isGetLastData = true;
    			if(rate !=0){
    				turnTransfersRate();
    			}
    			ftpLastDataMsTime = System.currentTimeMillis();
    			
    			UtilsMethod.sendWriteRcuEvent(FtpTest.this,isDownload ? RcuEventCommand.FTP_DL_LastData : 
    																	RcuEventCommand.FTP_UL_LastData, (int)fileSize);	//20110128currentSize大小不一至
    			
    			msgStr = repeatTimes +"-"+ftpTypeStr+" "+DataTaskEvent.FTP_LAST_DATA.toString()
    					+":Delay "+doJobTimes+"(s)"+",Mean Rate:"+UtilsMethod.decFormat.format(getFtpFinishAverageThr())
    					+"kbps,Transmit Size:"+UtilsMethod.decFormat.format(fileSize*1.0/kByte)+"KBytes";
    			msg = mHandler.obtainMessage(EVENT_CHANGE,msgStr);
    			mHandler.sendMessage(msg);
    			
    			//收到最后一个数据包时,同时记录统计数据
    			updateTotalInfo(true,false);
    			
    			sendCallBackStop(TestService.RESULT_SUCCESS);
		    }
			break;		
		case OperaType.FTP_DROP:
			LogUtil.w(tag,"--FTP_DROP--");
			msg = mHandler.obtainMessage(EVENT_CHANGE,DataTaskEvent.FTP_DROP.toString());
			mHandler.sendMessage(msg);
			break;
		case OperaType.FTP_REQUEST:
			//LogUtil.w(tag,"--FTP_REQUEST--");
			msg = mHandler.obtainMessage(EVENT_CHANGE,DataTaskEvent.FTP_REQUEST.toString());
			mHandler.sendMessage(msg);
			break;
		case OperaType.FTP_RESPONSE:
			//LogUtil.w(tag,"--FTP_RESPONSE--");
			msg = mHandler.obtainMessage(EVENT_CHANGE,DataTaskEvent.FTP_RESPONSE.toString());
			mHandler.sendMessage(msg);
			break;
		case OperaType.FTP_MALLOC_NULL:
			LogUtil.w(tag,"--FTP_MALLOC_NULL--");
			msg = mHandler.obtainMessage(EVENT_CHANGE,DataTaskEvent.FTP_MALLOC_NULL.toString());
			mHandler.sendMessage(msg);
			
			sendCallBackStop(DataTaskEvent.FTP_MALLOC_NULL.toString());
			break;
		case OperaType.FTP_SERVER_NORESPONSE:
			LogUtil.w(tag,"--FTP_SERVER_NORESPONSE--");
			//listenPhoneState(FtpTest.this);
			if(!isLogonFailure){
				//如未获得文件大小报连接失败
				isLogonFailure = true;
				
				//如果当前已获得文件大小则需要报Drop事件
				if(isGetFileSize || isGetFirstData){
					UtilsMethod.sendWriteRcuEvent(FtpTest.this,isDownload ? RcuEventCommand.FTP_DL_Drop
							:RcuEventCommand.FTP_UL_Drop, doJobTimes, (int)totalTransSize, RcuEventCommand.DROP_NODATA);
					
					msgStr = repeatTimes +"-"+ftpTypeStr+" "+DataTaskEvent.FTP_DROP.toString()
					+":Delay(s)"+doJobTimes+",Mean Rate:"+UtilsMethod.decFormat.format(getFtpFinishAverageThr())
					+"kbps,Transmit Size:"+UtilsMethod.decFormat.format(totalTransSize*1.0/kByte)+"KBytes"
					+",NoDataTimeout";
					msg = mHandler.obtainMessage(EVENT_CHANGE,msgStr);
					mHandler.sendMessage(msg);
					
					//当前如果收到文件大小。因库报无响应时，当成DROP处理
					updateTotalInfo(false);
				}else if(!isLogonSuccess){
					//FTP下载控制，登录FTP服务器是否成功 0失败,1成功
				    int delay = (connectingTime == 0 ? 0 : (int)(System.currentTimeMillis() - connectingTime));
					UtilsMethod.sendWriteRcuEvent(FtpTest.this,isDownload ? RcuEventCommand.FTP_DL_ConnectSockFailed
																			:RcuEventCommand.FTP_UL_ConnectSockFailed,delay);
					msgStr = repeatTimes +"-"+ftpTypeStr+" "+DataTaskEvent.FTP_LOGIN_FAILED.toString();
					msg = mHandler.obtainMessage(EVENT_CHANGE,msgStr);
					mHandler.sendMessage(msg);
				}
				
				//显示无响应消息
				msg = mHandler.obtainMessage(EVENT_CHANGE,repeatTimes +"-"+DataTaskEvent.FTP_SERVER_NORESPONSE.toString());
				mHandler.sendMessage(msg);
				//停止当前次测试
				sendCallBackStop(isGetFileSize || isGetFirstData ? "1" : "FTP_SERVER_NORESPONSE");
			}
			break;
		case OperaType.FTP_FILE_SIZE_ZERO:
			LogUtil.w(tag,"--FTP_FILE_SIZE_ZERO--");
			msg = mHandler.obtainMessage(EVENT_CHANGE,DataTaskEvent.FTP_FILE_SIZE_ZERO.toString());
			mHandler.sendMessage(msg);
			
			sendCallBackStop(DataTaskEvent.FTP_FILE_SIZE_ZERO.toString());
			break;
		default:
			msg = mHandler.obtainMessage(EVENT_CHANGE,DataTaskEvent.FTP_DEFAULT_EVENT.toString());
			mHandler.sendMessage(msg);
			break;	
		}
	}
*/}
