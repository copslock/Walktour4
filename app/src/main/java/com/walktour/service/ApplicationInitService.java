package com.walktour.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.dingli.samsungvolte.SIPInfoModel;
import com.dingli.samsungvolte.SplitSIPInfo;
import com.dingli.samsungvolte.SplitVoLTEKey;
import com.dingli.samsungvolte.VolteKeyModel;
import com.dingli.wlan.apscan.WifiScanner;
import com.dinglicom.ResourceCategory;
import com.dinglicom.dataset.EventManager;
import com.innsmap.InnsMap.INNSMapSDK;
import com.innsmap.InnsMap.net.http.listener.forout.SDKInitListener;
import com.walktour.Utils.APNOperate;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.BuildPower;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.HttpServer;
import com.walktour.Utils.ImageUtil;
import com.walktour.Utils.ServerMessage;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalktourConst;
import com.walktour.Utils.ZipUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.AssetsUtils;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.AssetsWriter;
import com.walktour.control.bean.FileOperater;
import com.walktour.control.bean.LocalInfoUpload;
import com.walktour.control.bean.MyFileWriter;
import com.walktour.control.bean.MyPhone;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigAPN;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.ConfigUmpc;
import com.walktour.control.config.Deviceinfo;
import com.walktour.control.config.ParameterSetting;
import com.walktour.control.config.ServerManager;
import com.walktour.gui.R;
import com.walktour.gui.analysis.commons.AnalysisCommons;
import com.walktour.gui.highspeedrail.HighSpeedRailCommons;
import com.walktour.gui.locknet.QualcomContoler;
import com.walktour.gui.setting.customevent.CustomEventFactory;
import com.walktour.gui.setting.eventfilter.EventFilterSettingFactory;
import com.walktour.gui.setting.msgfilter.MsgFilterSettingFactory;
import com.walktour.gui.share.logic.RegisterDeviceLogic;
import com.walktour.gui.task.parsedata.model.task.videoplay.StringSpecialInit;
import com.walktour.gui.upgrade.NoUpgradeService;
import com.walktour.gui.upgrade.UpgradeService;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;
import com.walktour.model.APNModel;
import com.walktour.service.app.AutoTestService;
import com.walktour.service.app.Killer;
import com.walktour.service.phoneinfo.TelephonyManagerService;

import java.io.File;
import java.lang.ref.WeakReference;
import java.sql.Date;
import java.util.List;
import java.util.Locale;


/**
 * 1.初始化所有数据 注意较大的I/O操作必须在独立线程中进行 2.启动和管理所有后台服务
 */
@SuppressLint("SdCardPath")
public class  ApplicationInitService extends Service {
	/** 日志标识*/
	private static final String TAG = "ApplicationInitService";
	private static final int NOSDCARD = 1;
	private static final int InitDefaultApn = 2;
	private ApplicationModel appModel = ApplicationModel.getInstance();
	private AppFilePathUtil mAppFilePathUtil;
	private Context mContext;
	private MyPhoneState myPhoneState = null;
	private final String KEY_ENV_FILE_MAKE_DIR = "env_file_make_dir";
	/**
	 * 单机加密文件路径信息
	 */
	private String entryptioneFilePath;
	/** 消息处理句柄 */
	private Handler mHandler = new MyHandler(ApplicationInitService.this);
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.setDebug(this.isApkInDebug());
		this.mContext = this.getApplicationContext();
		this.mAppFilePathUtil = AppFilePathUtil.getInstance();

        File cacertFile = this.mAppFilePathUtil.getSDCardBaseFile("config", "cacert.der");
        // if cacertFile is null , it means AppfilePathUtil not init
        if (cacertFile == null){
            // init
            mAppFilePathUtil.init(mContext);
            cacertFile = this.mAppFilePathUtil.getSDCardBaseFile("config", "cacert.der");
            this.entryptioneFilePath = cacertFile.getAbsolutePath();
        }else {
            this.entryptioneFilePath = cacertFile.getAbsolutePath();
        }

        if (appModel.getNetList().contains(WalkStruct.ShowInfoType.WLAN)) {
			WifiScanner.instance(getApplicationContext());
		}
		if (appModel.hasInnsmapTest()) {
			INNSMapSDK.init(mContext, new SDKInitListener() {
				@Override
				public void onSuccess() {
					LogUtil.d(TAG, "---INNSMapSDK Success---");
				}

				@Override
				public void onFail(String s) {
					LogUtil.d(TAG, "---INNSMapSDK Fail---:" + s);
				}
			});
		}
		LogUtil.w(TAG, "--onCreate--EnvInit:" + appModel.isEnvironmentInit());
		myPhoneState = MyPhoneState.getInstance();

		new LocalInfoUpload(mContext).UploadLocalInfo();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.w(TAG, "--onStart--intent Null:" + (intent == null));
		if (intent == null) {
			startService(new Intent(getApplicationContext(), Killer.class));
		}
		LogUtil.w(TAG, "--onStart--intent EnvironmentInit:" + appModel.isEnvironmentInit());
		if (!appModel.isEnvironmentInit()) {

			new InitDataThread().start();
			appModel.setEnvironmentInit(true);
		}

		//同步网络时间
		syncNetWorkTime();
		//如果是S8定制rom，则在启动app时关闭自动接听，防止在进行互拨测试时打开自动接听后测试崩溃后未关闭自动接听操作,导致手机状态还处于自动接听状态
		if(Deviceinfo.getInstance().isSamsungCustomRom()){
            MyPhone.stopS8AutoAnswer(this);
		}

		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 当前apk是否在debug模式
	 * @return 是否
	 */
	private boolean isApkInDebug() {
		try {
			ApplicationInfo info = this.getApplicationInfo();
			return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 获取网络时间
	 */
	private void syncNetWorkTime() {
		new Thread() {

			@Override
			public void run() {
				if (!MyPhoneState.getInstance().isNetworkAvirable(getApplicationContext())) {
					setDataEnabled(true);
				}
				long time = UtilsMethod.getNetTimeByBjtime();
				long timeOffset  = System.currentTimeMillis() - time;
				ApplicationModel.getInstance().setServerTimeOffset(timeOffset);
				// 分别取得时间中的小时，分钟和秒，并输出
				LogUtil.d(TAG, "sync net work time:" + UtilsMethod.sdFormat.format(new Date(time)));
				LogUtil.d(TAG, "server time offset:" + timeOffset);
			}

		}.start();
	}
	/**
	 * 关闭可用接入点
	 */
	private synchronized void setDataEnabled(boolean enabled) {
		try {
			APNOperate apnOperate = APNOperate.getInstance(this);
			if (enabled || (!enabled && apnOperate.checkNetWorkIsConnected())) {
				apnOperate.setMobileDataEnabled(enabled, "", true, 1000 * 15);
			}
		} catch (Exception e) {
			LogUtil.e(TAG, e.getMessage());
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.w(TAG, "----" + TAG + " stop111!");
	}

	/**
	 * 初始化所有配置文件，数据目录
	 */
	private class InitDataThread extends Thread {
		@Override
		public void run() {
			Deviceinfo deviceinfo=Deviceinfo.getInstance();
			initResourceCategory();
			if(deviceinfo.isVivo()){
				deviceinfo.setAir(true);
			}
			// 从create中将权限检查放到线程中去执行
			if (!appModel.isCheckPowerSuccess()) {
				// 修改获取手机标识为使用自定义方法，CDMA下取蓝牙加密16位，其它取IMEI
				String deviceId = myPhoneState
						.getMyDeviceId(getApplicationContext());
				int checkPower = new BuildPower().checkUserPower(
						getApplicationContext(), deviceId);
				if (checkPower == WalkCommonPara.POWER_LINCESE_SUCCESS) {
					appModel.setCheckPowerSuccess(true);
					// 重置共享变量
				}
			}

			Looper.prepare();
			if (!appModel.isGeneralMode()) {
				File file = new File(AppFilePathUtil.getInstance().createSDCardBaseDirectory("lib"));

				File[] fileList = file.listFiles();
                if (fileList != null) {
                    for (File f : fileList) {
                        if (f.isFile()) {
                            copyLibFromSdcard(f.getAbsolutePath());
                        }
                    }
                }
            }

			initData();
			ConfigRoutine.getInstance().setContext(mContext);

			deleteTotalTempFile(); // 新增
			if (!appModel.isGeneralMode()) {
                UtilsMethod.runRootCommand("chmod 777 "
                        + deviceinfo.getTracepath());
            }


			initService();
			appModel.setInit(true);
			if(Deviceinfo.getInstance().isVivo()){
				try {
					while(Deviceinfo.getInstance().isAir()){
						Thread.sleep(1000);
						LogUtil.w(TAG,"Vivo is airing,please wait.");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			ApplicationInitService.this.sendBroadcast(new Intent(
					WalkMessage.ACTION_MAIN_INIT_SUCCESS));
			if (ConfigRoutine.getInstance().toRunHttpServer(
					getApplicationContext())) {
				HttpServer.getInstance(getApplicationContext())
						.startHttpServer(
								PreferenceManager.getDefaultSharedPreferences(
										getApplicationContext()).getInt(
										WalktourConst.SYS_SETTING_OTS_PORT,
										HttpServer.HttpServerPort));
			}

			// 如果当前设置默认上报状态为真，则启动自动上报服务
			if (PreferenceManager.getDefaultSharedPreferences(
					getApplicationContext()).getBoolean(
					WalktourConst.SYS_SETTING_OTS_TEST_ACTIVE, false)) {
				startService(new Intent(getApplicationContext(),
						OtsHttpUploadService.class));
			}

			if (!ApplicationModel.getInstance().isNBTest()&&appModel.hasSamsungVoLTE()) {
				LogUtil.w(TAG,"start===");
				SplitVoLTEKey splitVoLTEKey = SplitVoLTEKey.getInstance(mHandler, android.os.Build.MODEL,android.os.Build.TAGS);
				int times = 1;
				splitVoLTEKey.startSIPInfo(AppFilePathUtil.getInstance().getAppLibFile("whatsnew").getAbsolutePath(),times);
				long time = System.currentTimeMillis();
				while (System.currentTimeMillis() - time < 20 * times * 1000) {
					try {
						Thread.sleep(1000);
						if(appModel.getVolteKeyModel() != null) {
                            break;
                        }
					} catch (InterruptedException e) {
						break;
					}
				}
				splitVoLTEKey.stopSIPInfo();
				LogUtil.w(TAG,"start===end");
			}

			// 平台交互监控-->测试计划扫描
			if (PreferenceManager.getDefaultSharedPreferences(
					getApplicationContext()).getBoolean(
					WalktourConst.SYS_SETTING_platform_test, false)) {
				Intent intent = new Intent();
				intent.setAction(ServerMessage.ACTION_PLATFORM_CONTROL_TESTPLAN_START);
				sendOrderedBroadcast(intent, null);
			}
			StringSpecialInit.getInstance().setmContext(mContext);
			RegisterDeviceLogic.getInstance(mContext).shareRegister();
			stopSelf();
		}
	}
	/***
	 * 初始化资源信息
	 */
	private void initResourceCategory(){
		int fg=openResourceCategory(ResourceCategory.RESOURCETYPE_PARAM,appModel.getHandler_param());
		if(fg==1) {
			fg=openResourceCategory(ResourceCategory.RESOURCETYPE_EVENT, appModel.getHandler_event());
			if(fg==1){
				fg=openResourceCategory(ResourceCategory.RESOURCETYPE_BUSINESS,appModel.getHandler_business());
				if(fg!=1){
					LogUtil.d(TAG,"openResourceCategory business is failure");
				}
			}else{
				LogUtil.d(TAG,"openResourceCategory event is failure");
			}
		}else{
			LogUtil.d(TAG,"openResourceCategory param is failure");
		}

//		LogUtil.d(TAG,"POPOPO=1="+ResourceCategory.getInstance().GetParamStandardInfo(appModel.getHandler_param(),Integer.valueOf("7F06001B")));
//		LogUtil.d(TAG,"POPOPO=2="+ResourceCategory.getInstance().GetParamStandardName(appModel.getHandler_param(), UnifyParaID.L_SRV_Work_Mode));
	}
	/***
	 * 打开资源库—事件资源
	 * @param eResourceHandleType 资源类型
	 * @param handler 资源句柄
	 * @return  1=表示成功，
	 */
	private int openResourceCategory(int eResourceHandleType,Integer handler){
		int flag=-1;
		try {
			//初始化资源库
			LogUtil.d(TAG, "flag=0x=eResourceHandleType=" + eResourceHandleType + ",DataSetResourceDir=" + AppFilePathUtil.getInstance().getAppLibDirectory() + ",handler=" + handler);
			flag = ResourceCategory.getInstance().CreateResourceHandle(eResourceHandleType, AppFilePathUtil.getInstance().getAppLibDirectory(), handler);
			LogUtil.d(TAG, "flag=1x=" + flag + ",handler=" + handler);
			if(flag==1) {
				//打开资源库
				File file = AppFilePathUtil.getInstance().getAppConfigFile("ResourceCategory.db");
				flag = ResourceCategory.getInstance().Open(handler, file.getAbsolutePath(), "");
				if(flag==1){
					LogUtil.d(TAG, "flag=2x=" + flag);
				}
			}


		}catch (Exception ex){
			ex.printStackTrace();
		}
		return flag;
	}
	/**
	 * 从sd卡上拷贝库文件到库目录
	 *
	 * @param filePath 文件路径
	 */
	private static void copyLibFromSdcard(String filePath) {
		File file = new File(filePath);
		if (file.isFile()) {
			FileOperater fileOP = new FileOperater();
			File desFile = AppFilePathUtil.getInstance().getAppLibFile(file.getName());
			if (desFile.exists() && !desFile.delete()) {
				return;
			}
			fileOP.copy(filePath, desFile.getAbsolutePath());
			UtilsMethod.runRootCommand("chmod 777 " + desFile.getAbsolutePath());

			if(!file.delete()) {
				LogUtil.d(TAG, "---copyLibFromSdcard delete false---");
			}
		}
	}

	/**
	 * 自定义消息处理句柄
	 *
	 * @author jianchao.wang
	 *
	 */
	private static class MyHandler extends Handler {
		private WeakReference<ApplicationInitService> reference;

		public MyHandler(ApplicationInitService service) {
			this.reference = new WeakReference<>(service);
		}

		@Override
		public void handleMessage(Message msg) {
			ApplicationInitService service = this.reference.get();
			switch (msg.what) {
				case NOSDCARD:
					Toast.makeText(service.getApplicationContext(),	service.getString(R.string.sdcard_unmount),Toast.LENGTH_LONG).show();
					break;
				case InitDefaultApn:
					service.setDefaultApn();
					break;
				case SplitSIPInfo.HANDLE_SPLITSIPINFO:
					SIPInfoModel sipModel = (SIPInfoModel) msg.obj;
					ApplicationModel.getInstance().setSIPInfoModel(sipModel);
					LogUtil.w(TAG, "--SIPInfo:" + (sipModel != null
							? sipModel.getName() + ":" + sipModel.getContent() : "SIPInfo isnull"));
					break;
				case SplitVoLTEKey.HANDLE_SPLITVOLTEKEY:
					VolteKeyModel volteKeyModel = (VolteKeyModel) msg.obj;
					ApplicationModel.getInstance().setVolteKeyModel(volteKeyModel);
					LogUtil.w(TAG, "--volteKey:" + volteKeyModel.getEncryptionKey() + "--" + volteKeyModel.getAuthenticationKey());
					break;
			}
		}

	}

	/**
	 * 生成应用程序工作目录
	 */
	private void mdkWorkDirectory() {
		if (SharePreferencesUtil.getInstance(mContext).getBoolean(KEY_ENV_FILE_MAKE_DIR, false)) {
			LogUtil.d(TAG, "---mdkWorkDirectory finish--");
			return;
		}
		/* 在手机创建数据文件目录 */
		 /* 测试任务组数据 */
		this.mAppFilePathUtil.createAppFilesDirectory(getString(R.string.path_data),getString(R.string.path_group));
		/* 测试任务数据 */
		this.mAppFilePathUtil.createAppFilesDirectory(getString(R.string.path_data),getString(R.string.path_task));
		/* 数据集回放数据 */
		this.mAppFilePathUtil.createAppFilesDirectory(getString(R.string.path_data),getString(R.string.path_ddib));
		/* 扫频仪数据 */
		this.mAppFilePathUtil.createAppFilesDirectory(getString(R.string.path_data),getString(R.string.path_scanner));
		/* 监控数据 */
		this.mAppFilePathUtil.createAppFilesDirectory(getString(R.string.path_data),getString(R.string.path_monitor));
		/* 室内专项数据 */
		this.mAppFilePathUtil.createAppFilesDirectory(getString(R.string.path_data),getString(R.string.path_indoor));
		/* 室内测试数据 */
		this.mAppFilePathUtil.createAppFilesDirectory(getString(R.string.path_data),getString(R.string.path_indoortest));
		 /* 测数据时生成临时bin文件 */
		this.mAppFilePathUtil.createAppFilesDirectory(getString(R.string.path_data),getString(R.string.path_bin));
		/* 导出Excel报表生成目录 */
		this.mAppFilePathUtil.createAppFilesDirectory(getString(R.string.path_data),getString(R.string.path_report));
		/* 临时目录,用于下载测试计划 */
		this.mAppFilePathUtil.createAppFilesDirectory(getString(R.string.path_temp));
		/* 自动测试日志目录 */
		this.mAppFilePathUtil.createAppFilesDirectory(getString(R.string.path_event),getString(R.string.path_event_autotest));
		/* 业务测试日志目录 */
		this.mAppFilePathUtil.createAppFilesDirectory(getString(R.string.path_event),getString(R.string.path_event_test));
		/* 截图目录 */
		this.mAppFilePathUtil.createAppFilesDirectory(getString(R.string.path_snapshot));
		/* project工程 */
		this.mAppFilePathUtil.createSDCardBaseDirectory("project");
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			/* 测试任务数据 */
			this.mAppFilePathUtil.createSDCardBaseDirectory(getString(R.string.path_data),getString(R.string.path_task));
			/* 测试任务数据 */
			this.mAppFilePathUtil.createSDCardBaseDirectory(getString(R.string.path_data),getString(R.string.path_ddib));
			/* 扫频仪数据 */
			this.mAppFilePathUtil.createSDCardBaseDirectory(getString(R.string.path_data),getString(R.string.path_scanner));
			/* 监控数据 */
			this.mAppFilePathUtil.createSDCardBaseDirectory(getString(R.string.path_data),getString(R.string.path_monitor));
			/* 室内专项数据 */
			this.mAppFilePathUtil.createSDCardBaseDirectory(getString(R.string.path_data),getString(R.string.path_indoor));
			/* 室内测试数据 */
			this.mAppFilePathUtil.createSDCardBaseDirectory(getString(R.string.path_data),getString(R.string.path_indoortest));
			 /* 测数据时生成临时bin文件 */
			this.mAppFilePathUtil.createSDCardBaseDirectory(getString(R.string.path_data),getString(R.string.path_bin));
			/* 导出Excel报表生成目录 */
			this.mAppFilePathUtil.createSDCardBaseDirectory(getString(R.string.path_data),getString(R.string.path_report));
 			/* 自动测试日志目录 */
			this.mAppFilePathUtil.createSDCardBaseDirectory(getString(R.string.path_event),getString(R.string.path_event_autotest));
			/* 业务测试日志目录 */
			this.mAppFilePathUtil.createSDCardBaseDirectory(getString(R.string.path_event),getString(R.string.path_event_test));
			/* 截图目录 */
			this.mAppFilePathUtil.createSDCardBaseDirectory(getString(R.string.path_snapshot));
			/* 共享数据目录 */
			this.mAppFilePathUtil.createSDCardBaseDirectory(ShareCommons.SHARE_PATH_BASE);
			/* 共享数据目录 共享工程 */
			this.mAppFilePathUtil.createSDCardBaseDirectory(ShareCommons.SHARE_PATH_BASE,ShareCommons.SHARE_PATH_PROJECT);
			/* 共享数据目录 任务组 */
            this.mAppFilePathUtil.createSDCardBaseDirectory(ShareCommons.SHARE_PATH_BASE,ShareCommons.SHARE_PATH_TASKGROUP);
             /* 共享数据目录 任务 */
            this.mAppFilePathUtil.createSDCardBaseDirectory(ShareCommons.SHARE_PATH_BASE,ShareCommons.SHARE_PATH_TASK);
            /* 共享数据目录 报表 */
            this.mAppFilePathUtil.createSDCardBaseDirectory(ShareCommons.SHARE_PATH_BASE,ShareCommons.SHARE_PATH_REPORT);
            /* 共享数据目录 CQT */
            this.mAppFilePathUtil.createSDCardBaseDirectory(ShareCommons.SHARE_PATH_BASE,ShareCommons.SHARE_PATH_CQT);
            /* 共享数据目录 CQTPIC */
            this.mAppFilePathUtil.createSDCardBaseDirectory(ShareCommons.SHARE_PATH_BASE,ShareCommons.SHARE_PATH_CQT_PIC);
            /* 共享数据目录 DATA */
            this.mAppFilePathUtil.createSDCardBaseDirectory(ShareCommons.SHARE_PATH_BASE,ShareCommons.SHARE_PATH_DATA);
            /* 共享数据目录 DATA */
            this.mAppFilePathUtil.createSDCardBaseDirectory(ShareCommons.SHARE_PATH_BASE,ShareCommons.SHARE_PATH_STATION);
            /* 高铁测试目录 */
            this.mAppFilePathUtil.createSDCardBaseDirectory(HighSpeedRailCommons.High_SPEED_RAIL_PATH);
            /* 高铁测试接口库日志目录 */
            this.mAppFilePathUtil.createSDCardBaseDirectory(HighSpeedRailCommons.High_SPEED_RAIL_PATH,HighSpeedRailCommons.High_SPEED_RAIL_PATH_LOG);
            /* 录音目录 */
            this.mAppFilePathUtil.createSDCardBaseDirectory(getString(R.string.path_voice));
            /* 视频目录 */
            this.mAppFilePathUtil.createSDCardBaseDirectory(getString(R.string.path_video));
            /* 低层库日志 */
            this.mAppFilePathUtil.createSDCardBaseDirectory(getString(R.string.path_liblog));
            /* 低层库日志 */
            this.mAppFilePathUtil.createSDCardBaseDirectory(getString(R.string.path_liblog),getString(R.string.path_ipc2));
            /* 自定义目录 */
            this.mAppFilePathUtil.createSDCardBaseDirectory(getString(R.string.path_data),getString(R.string.path_custom));
            /* 配置文件 */
            this.mAppFilePathUtil.createSDCardBaseDirectory(getString(R.string.path_config));
            /* 智能分析根目录 */
            this.mAppFilePathUtil.createSDCardBaseDirectory(AnalysisCommons.ANALYSIS_PATH_ROOT);
			/* 智能分析目录 */
            this.mAppFilePathUtil.createSDCardBaseDirectory(AnalysisCommons.ANALYSIS_PATH_ROOT,AnalysisCommons.ANALYSIS_PATH_ROOT_ANALYSIS);
            /* 智能分析历史文件目录 */
            this.mAppFilePathUtil.createSDCardBaseDirectory(AnalysisCommons.ANALYSIS_PATH_ROOT,AnalysisCommons.ANALYSIS_PATH_ROOT_ANALYSIS,AnalysisCommons.ANALYSIS_PATH_ROOT_ANALYSIS_HISTORY);
            /* 单站验证模板文件目录 */
            this.mAppFilePathUtil.createSDCardBaseDirectory( getString(R.string.path_singlestation),getString(R.string.path_singlestation_template_report),getString(R.string.path_singlestation_template));
            /* 单站验证室内报告路径 */
            this.mAppFilePathUtil.createSDCardBaseDirectory( getString(R.string.path_singlestation),getString(R.string.path_singlestation_template_report),getString(R.string.path_singlestation_template_report_indoor));
			/* 单站验证室外报告路径 */
            this.mAppFilePathUtil.createSDCardBaseDirectory( getString(R.string.path_singlestation),getString(R.string.path_singlestation_template_report),getString(R.string.path_singlestation_template_report_outdoor));
			// 删除临时配置文件
			this.mAppFilePathUtil.deleteSDCardBaseFile(Deviceinfo.DEBUG_FILE_NAME);
			/**SD fleet report 保存下载的报表**/
			this.mAppFilePathUtil.createSDCardBaseDirectory(getString(R.string.fleet_report));

			/** 配置文件目录,方便改变程序执行流程 */
			this.mAppFilePathUtil.createSDCardBaseDirectory(getString(R.string.path_setting));
			ShareDataBase.getInstance(mContext);
			SharePreferencesUtil.getInstance(mContext).saveBoolean(KEY_ENV_FILE_MAKE_DIR, true);
		} else {
			Message msg = mHandler.obtainMessage(NOSDCARD);
			msg.sendToTarget();
		}
	}

	/**
	 * 初始化所有文件
	 */
	private void initData() {
		LogUtil.d(TAG, "----------initData--------start");
		/* 获取手机屏幕信息 */
		DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
		appModel.setScreenWidth(dm.widthPixels);
		appModel.setScreenHeigth(dm.heightPixels);

		mdkWorkDirectory();
		AppFilePathUtil util = AppFilePathUtil.getInstance();
		/*
		 * 写入配置文件
		 * 用Assets_Writer写配置文件到手机目录
		 * 如果文件已经存在，Assets_Writer不会写入
		 */
		// 测试业务库JNI的配置文件
		new AssetsWriter(mContext, "traceroute", "traceroute", false).writeToConfigDir();
		UtilsMethod.runRootCommand("chmod 777 " + AppFilePathUtil.getInstance().getAppConfigDirectory()+"traceroute");
		//s8 NB ppp拨号
		FileUtil.createFileDir("/data/local/tmp");
		File file = new File("/data/local/chat");
		UtilsMethod.writeRawResource(getApplicationContext(), R.raw.chat, file);
		file = new File("/data/local/chat1");
		UtilsMethod.writeRawResource(getApplicationContext(), R.raw.chat1, file);



		//S7,创建专门存放Rom的目录，用于刷机，此目录下的文件名默认为boot_nb.img或boot_normal.img
		FileUtil.createFileDir(AppFilePathUtil.getInstance().getSDCardBaseDirectory()+"rom");

		//室内基站模板
		file = util.getSDCardBaseFile(getString(R.string.path_singlestation), getString(R.string.path_singlestation_template_report), getString(R.string.path_singlestation_template), "cmcc_indoor.xls");
		UtilsMethod.writeRawResource(getApplicationContext(), R.raw.cmcc_indoor, file);
		//室外基站模板
		file = util.getSDCardBaseFile(getString(R.string.path_singlestation), getString(R.string.path_singlestation_template_report), getString(R.string.path_singlestation_template), "cmcc_outdoor.xls");
		UtilsMethod.writeRawResource(getApplicationContext(), R.raw.cmcc_outdoor, file);
//		//端口的json文件
//		file=util.getSDCardBaseFile("setting","port_json.txt");
//		UtilsMethod.writeRawResource(getApplicationContext(),R.raw.port_json,file);
		Deviceinfo deviceInfo = Deviceinfo.getInstance();
		if (!appModel.isGeneralMode()) {
			File libFile = new File(util.getAppLibDirectory());
			UtilsMethod.runRootCommand("chmod 777 " + libFile.getAbsolutePath());
//			libFile = util.getAppLibFile("datatests_android");
//			UtilsMethod.writeRawResource(getApplicationContext(), R.raw.datatests_android, libFile);
//
//			libFile = util.getAppLibFile("datatests_android_devmodem");
//			UtilsMethod.writeRawResource(getApplicationContext(), R.raw.datatests_android_devmodem, libFile);

			libFile = util.getAppLibFile("DMPlayerAndroid");
			UtilsMethod.writeRawResource(getApplicationContext(), R.raw.dmplayerandroid, libFile);
			libFile = util.getAppLibFile("webbrowser_lynx");
			UtilsMethod.writeRawResource(getApplicationContext(), R.raw.webbrowser_lynx, libFile);
			libFile = util.getAppLibFile("tcpipmonitorprocess");
			UtilsMethod.writeRawResource(getApplicationContext(), R.raw.tcpipmonitorprocess, libFile);
			libFile = util.getAppLibFile("iperf");
			UtilsMethod.writeRawResource(getApplicationContext(), R.raw.iperf, libFile);
			libFile = util.getAppLibFile("appanalysis");
			UtilsMethod.writeRawResource(getApplicationContext(), R.raw.appanalysis, libFile);
			libFile = util.getAppLibFile("traceroute");
			UtilsMethod.writeRawResource(getApplicationContext(), R.raw.traceroute, libFile);
			libFile = util.getAppLibFile("whatsnew");
			UtilsMethod.writeRawResource(getApplicationContext(), R.raw.whatsnew, libFile);
			libFile = util.getAppLibFile("cacert.der");
			MyFileWriter.write(libFile,entryptioneFilePath);
			UtilsMethod.runRootCommand("chmod 777 "+libFile.getAbsolutePath());
			libFile = util.getAppLibFile("multipleappanalysis");
			UtilsMethod.writeRawResource(getApplicationContext(), R.raw.multipleappanalysis, libFile);

			// 如果当前手机是华为的海思芯手机，需要往sdcard卡拷贝一cfg文件，并且为dev下的ttyUSB*赋权限
			if (deviceInfo.getDevicemodel().equals("HuaweiD2-5000")) {
				String hua = util.createDirectory(util.getSDCardBaseDirectory(), "hua");
				new AssetsWriter(mContext, "logmask/hua.cfg", util.getFile(hua, "hua.cfg"), false)
						.writeBinFile();

				UtilsMethod.runRootCommand("chmod 777 /dev/ttyUSB*");
			}

			// 高通芯片有强制功能
			if (deviceInfo.getLockInfo().hasLock()) {
				File tipFile = util.getAppFilesFile(QualcomContoler.TIP);
				UtilsMethod.writeRawResource(mContext, R.raw.tip, tipFile);
				UtilsMethod.runRootCommand("chmod 666 " + QualcomContoler.DEV);
			}


		}
		// 微信
		if (!appModel.isGeneralMode()) {

            this.createWeChatPic(1);
            this.createWeChatPic(3);

			file = util.getFile(util.getSDCardBaseDirectory(), "DCIM", "AppTest", "Video", "video.mp4");
			UtilsMethod.writeRawResource(getApplicationContext(), R.raw.video, file, true);
//			UtilsMethod.runRootCommand("chmod 777 " + file.getAbsolutePath());//写进去的时候已经有读写权限了

			file = new File("/data/local/tmp/libgnustl_shared.so");
			UtilsMethod.writeRawResource(getApplicationContext(), R.raw.libgnustl_shared, file, true);
//			UtilsMethod.runRootCommand("chmod 777 " + file.getAbsolutePath());//写进去的时候已经有读写权限了

			file = new File("/data/local/tmp/libipc2tool_jni_64.so");
			UtilsMethod.writeRawResource(getApplicationContext(), R.raw.libipc2tool_jni_64, file, true);
//			UtilsMethod.runRootCommand("chmod 777 " + file.getAbsolutePath());//写进去的时候已经有读写权限了

			file = new File("/data/local/tmp/libipc2tool64.so");
			UtilsMethod.writeRawResource(getApplicationContext(), R.raw.libipc2tool64, file, true);
//			UtilsMethod.runRootCommand("chmod 777 " + file.getAbsolutePath());//写进去的时候已经有读写权限了

			file = new File("/data/local/tmp/libipc2tool_jni_64_new.so");
			UtilsMethod.writeRawResource(getApplicationContext(), R.raw.libipc2tool_jni_64_new, file, true);
//			UtilsMethod.runRootCommand("chmod 777 " + file.getAbsolutePath());//写进去的时候已经有读写权限了

			file = new File("/data/local/tmp/apptestjar.jar");
			UtilsMethod.writeRawResource(getApplicationContext(), R.raw.apptestjar, file, true);
//			UtilsMethod.runRootCommand("chmod 777 " + file.getAbsolutePath());//写进去的时候已经有读写权限了
		}

		// 把图表设置恢复为默认值(重新写入配置文件)
		ParameterSetting.resetToDefaultFromFile(mContext, false);

		// 把信令过滤设置恢复为默认值(重新写入配置文件)
		MsgFilterSettingFactory.getInstance().resetToDefaultFromFile(mContext, false);

		// 把事件过滤设置恢复为默认值(重新写入配置文件)
		EventFilterSettingFactory.getInstance().resetToDefaultFromFile(mContext, false);

		chooseLogmask(deviceInfo);

		/* 往Pionner传数据的时候，需要通过当前临时文件传输数据，此处自动创建该文件，并赋权限 */
		try {
			File pionnerTemp = util.createFile(util.getAppBaseDirectory(), "TRACE.RCU");
			UtilsMethod.runCommand("chmod 777 " + pionnerTemp.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		file = util.getFile(util.getAppBaseDirectory(), "http_test");
		UtilsMethod.writeRawResource(getApplicationContext(), R.raw.http_test, file);

        //没有拷贝,则需要拷贝
        if (!SharePreferencesUtil.getInstance(mContext).getBoolean(WalktourConst.STATISTICS_INFO, false)) {
            try {
                // 报表统计相关配置文件
                File totalConfigFile = util.getFile(util.getSDCardBaseDirectory(), "TotalConfig.zip");
				AssetsUtils.copyFilesFromAssets(mContext,"statistics",util.getSDCardBaseDirectory());
                ZipUtil.unzip(totalConfigFile.getAbsolutePath(), util.getSDCardBaseDirectory());
                if (!totalConfigFile.delete()) {
                    LogUtil.d(TAG, "---delete file false---");
                }
                totalConfigFile=null;
                SharePreferencesUtil.getInstance(mContext).saveBoolean(WalktourConst.STATISTICS_INFO, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

		// 该可执行文件用于接听电话等动作
		file = util.getFile(util.getAppBaseDirectory(), "radiooptions");
		new AssetsWriter(mContext, "radiooptions", file).writeBinFile();
		file = util.getFile(util.getAppBaseDirectory(), "ipcDevDiag");
		new AssetsWriter(mContext, "ipc/ipcDevDiag", file).writeBinFile();
		file = util.getFile(util.getAppBaseDirectory(), "ipcDevDiagNB");
		new AssetsWriter(mContext, "ipc/ipcDevDiagNB", file).writeBinFile();

		//需要拷贝到lib目录下,特殊处理
		file = util.getFile(util.getAppBaseDirectory(), "datatests_android_devmodem");
		new AssetsWriter(mContext, "ipc/datatests_android_devmodem", file).writeBinFile();
		File  fx2=util.getFile(util.getAppBaseDirectory(), "lib/datatests_android_devmodem");
        UtilsMethod.runRootCommand("cp "+file.getAbsolutePath()+" "+fx2.getAbsolutePath());
		UtilsMethod.runRootCommand("chmod 777 "+fx2.getAbsolutePath());
		UtilsMethod.runRootCommand("rm "+file.getAbsolutePath());
		//需要拷贝到lib目录下,特殊处理
		file = util.getFile(util.getAppBaseDirectory(), "datatests_android");
		new AssetsWriter(mContext, "ipc/datatests_android", file).writeBinFile();
		fx2=util.getFile(util.getAppBaseDirectory(), "lib/datatests_android");
        UtilsMethod.runRootCommand("cp "+file.getAbsolutePath()+" "+fx2.getAbsolutePath());
		UtilsMethod.runRootCommand("chmod 777 "+fx2.getAbsolutePath());
		UtilsMethod.runRootCommand("rm "+file.getAbsolutePath());

		/* 当用户权限中拥有自动测试时，复制二进制文件reboot */
		// if( appModel.getAppList().contains(WalkStruct.AppType.AutomatismTest
		// ) ){
		file = util.getAppFilesFile("reboot");
		new AssetsWriter(mContext, "reboot", file).writeBinFile();
		// }
		/* 应用中部分命令不存在时使用该项 */
		file = util.getFile(util.getAppBaseDirectory(), "busybox");
		new AssetsWriter(mContext, "busybox", file).writeBinFile();

		/*
		 * 生成全局静态变量，测试任务图表显示参数
		 */
		try {
			if (appModel.getAppList().contains(WalkStruct.AppType.OperationTest)
					|| appModel.getAppList().contains(WalkStruct.AppType.AutomatismTest)) {
				// ChartProperty.getInstance();
				ParameterSetting.getInstance().initialParameter();
			}
			if (appModel.getAppList().contains(WalkStruct.AppType.MutilyTester)) {
				ConfigUmpc.getInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// new Thread(new RunTestThread()).start();
		// 初始化参数线程,预加载系统XML设置内容到内存中,当然亦可在用到时再加载,不过第一次感觉有点卡
		ApplicationModel.getInstance();
		TotalDataByGSM.getInstance();

		// 初始化事件的定义
		EventManager.getInstance().initEevnts(mContext);
		// 初始化自定义事件的定义
		CustomEventFactory.getInstance().initCustomEvent();

		//没有拷贝帮助文档,则需要拷贝
		if (!SharePreferencesUtil.getInstance(mContext).getBoolean(WalktourConst.HELP_INFO, false)) {
			// 帮助文档
			try {
				File zipFileDoc = util.getAppFilesFile("help_doc.zip");
				File zipFileGuide = util.getAppFilesFile("quick_guide.zip");

				/**将assets dataset目录下的文件全部拷贝到/data/data/com.walktour.gui/files/config/目录下**/
				AssetsUtils.copyFilesFromAssets(mContext, "help", mContext.getFilesDir().getAbsolutePath());

				ZipUtil.unzip(zipFileDoc.getAbsolutePath(), getFilesDir().getAbsolutePath());
				ZipUtil.unzip(zipFileGuide.getAbsolutePath(), getFilesDir().getAbsolutePath());
				if (!zipFileDoc.delete()) {
					LogUtil.d(TAG, "---delete file false---");
				}
				if (!zipFileGuide.delete()) {
					LogUtil.d(TAG, "---delete file false---");
				}
                zipFileDoc=null;
                zipFileGuide=null;
				SharePreferencesUtil.getInstance(mContext).saveBoolean(WalktourConst.HELP_INFO, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		LogUtil.d(TAG, "----------initData--------end");
	}

    	/**
    	 * 已经优化，如果存在，则不需要创建
    	 * 生成微信图片
    	 * @param size 图片大小(单位：M)
    	 */
    	private void createWeChatPic(int size){
    		String fileName = size + "M";
    		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
    		path += "/DCIM/AppTest/" + fileName;
    		File file=new File(path);
    		if (file==null||file.exists()){
    			return;
    		}
    		int width = 592 * size;
    		int height = 592;
    		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    		Canvas canvas = new Canvas(bitmap);
    		// 2、设置画笔
    		Paint paint = new Paint();
    		paint.setColor(Color.RED);// 设置画笔颜色
    		paint.setStrokeWidth(20.0f);// 设置画笔粗细
    		paint.setTextSize(592 / 3);// 设置文字大小
    		canvas.drawColor(Color.WHITE);// 默认背景是黑色的
    		canvas.drawText(fileName, width / 2 - paint.measureText(fileName), height / 2, paint);
    		ImageUtil.saveBitmapToFile(path, bitmap, fileName, ImageUtil.FileType.BMP, false);
    	}

	/**
	 * 根据moduletype指定不同的logmask文件
	 * 1:Module_MC5210,2:Module_MC5218,3:Huawei_ec360;
	 * 4:Module_me860;5:Module_motot3g
	 * G72;6:Module_huawei8500s;7:GAOTONG-LTE
	 */
	private void chooseLogmask(Deviceinfo deviceInfo)
	{
		AppFilePathUtil util = AppFilePathUtil.getInstance();
		//默认使用Diag_Lte.cfg logmask,当有配置时使用配置
		if (ApplicationModel.getInstance().getNetList().contains(WalkStruct.ShowInfoType.LTE)) {
			new AssetsWriter(mContext, "logmask/" + deviceInfo.getLogmask(), util.getAppConfigFile("Diag.cfg"), false).writeBinFile();
			if (appModel.hasSamsungVoLTE()) {
				new AssetsWriter(mContext, "logmask/Diag_VoLTE.cfg", util.getAppConfigFile("Diag_VoLTE.cfg"), false).writeBinFile();

				new AssetsWriter(mContext, "logmask/Diag_Null.cfg", util.getAppConfigFile("Diag_Null.cfg"), false).writeBinFile();
			}

		}else{
			if(deviceInfo.getLogmask().equals("Diag_Lte.cfg")) {
				new AssetsWriter(mContext, "logmask/Diag_NotLte.cfg", util.getAppConfigFile("Diag.cfg"), false).writeBinFile();
			}else{
				new AssetsWriter(mContext, "logmask/" + deviceInfo.getLogmask(), util.getAppConfigFile("Diag.cfg"), false).writeBinFile();
			}
		}

	}

	/**
	 * 每次初始化删除统计临时文件
	 */
	private void deleteTotalTempFile() {
		UtilsMethod.runRootCommand("rm -r " + AppFilePathUtil.getInstance().getSDCardBaseDirectory() + "Totalconfig/ukdir/Temp/2*");
	}

	/**
	 * 设置默认APN接入点 当取值为none,如果APN命名中有wap且类型不是mms（GSM），或者用户名为ctwap@mycdma.cn(CDMA）
	 * 设定为默认WAP业务APN
	 * 当取值为none,如果APN命名中有net(GSM），或者用户名为ctnet@mycdma.cn(CDMA），设定为默认FTP数据业务APN
	 */
	private void setDefaultApn() {
		APNOperate apnOperate = APNOperate.getInstance(mContext);
		ConfigAPN configApn = ConfigAPN.getInstance();
		if (configApn.getWapAPN().equals("none")) {
			List<APNModel> apnList = apnOperate.getAPNList();
			for (APNModel apnModel : apnList) {
				if ((apnModel.getApn().toLowerCase(Locale.getDefault()).contains("wap") && (apnModel.getType() == null
						|| !apnModel.getType().toLowerCase(Locale.getDefault()).equals("mms")))
						|| (apnModel.getUser() != null && apnModel.getUser().toLowerCase(Locale.getDefault()).equals("ctwap@mycdma.cn"))) {
					configApn.setWapAPN(apnModel.getName());
				}
			}
		}
		if (configApn.getDataAPN().equals("none")) {
			List<APNModel> apnList = apnOperate.getAPNList();
			for (APNModel apnModel : apnList) {
				if ((apnModel.getApn().toLowerCase(Locale.getDefault()).contains("net")
						&& apnModel.getType() != null && apnModel.getType().toLowerCase(Locale.getDefault()).equals("default"))
						|| (apnModel.getUser() != null && apnModel.getUser().toLowerCase(Locale.getDefault()).equals("ctnet@mycdma.cn"))) {
					configApn.setDataAPN(apnModel.getName());
				}
			}
		}
	}

	/**
	 * 启动所有后台服务
	 */
	private void initService() {
		LogUtil.i(TAG, "----------initService--------");
		// 启动串口信息服务
		if (!this.appModel.isGeneralMode()) {
			if(!Deviceinfo.getInstance().isUseRoot()||Deviceinfo.getInstance().isRunStartDatasetService()) {
				if((!Deviceinfo.getInstance().isVivo() && !Deviceinfo.getInstance().isOppoCustom())||Deviceinfo.getInstance().isRunStartDatasetService()) {
					Intent startIntent = new Intent(this, StartDatasetService.class);
					startIntent.putExtra("devdiagtype", Deviceinfo.getInstance().getDevDiagType());
					startService(startIntent);
				}
			}
			startService(TraceService.class);
		} else {
			startService(TelephonyManagerService.class);
		}
		// 启动fleet服务器交互服务
		startService(FleetService.class);

		// 启动数据管理服务
		startService(com.walktour.service.app.DataService.class);

		// 开始下载禁止升级用户Excel
//		startService(NoUpgradeService.class);

		// 启动监控手机状态的服务
		startService(com.walktour.service.app.StateService.class);
		// 启动日志服务
		startService(com.walktour.service.app.LogService.class);

		// 当用户权限中拥有业务测试启动测试接口服务
		if (appModel.getAppList().contains(WalkStruct.AppType.OperationTest)
				|| appModel.getAppList().contains(
				WalkStruct.AppType.AutomatismTest)
				|| appModel.getAppList().contains(
				WalkStruct.AppType.MutilyTester)) {
			// 测试接口服务
			startService(TestInterfaceService.class);
		}

		// 当用户权限中拥有自动测试时，启动测试接口服务、自动测试控制服务
		if (appModel.getAppList().contains(WalkStruct.AppType.AutomatismTest)) {
			// 测试接口服务
			startService(TestInterfaceService.class);
			// 自动测试控制服务
			startService(AutoTestService.class);
		}

		// 当用户权限中拥有多路测试或业务测试权限时，
		if (appModel.getAppList().contains(WalkStruct.AppType.MutilyTester)) {

			// 当前如果有多网并发权限,USB连小背包服务自启动
			startService(iPackTerminal.class);
		}

		if (ServerManager.getInstance(mContext).hasAutoUpgrade()) {
			Intent intentUpgrade = new Intent(this, UpgradeService.class);
			intentUpgrade.putExtra(UpgradeService.KEY_CHECK_NOW, true);
			startService(intentUpgrade);
		}

		//S9机型绑定服务
		if(Deviceinfo.getInstance().isCustomS9()){
			SamsungService.getInStance(mContext).getService().getGpsLocationState();
		}
	}


	/***
	 * 启动服务
	 * @param cls 启动的服务类
	 */
	private void startService(Class<?> cls){
		Intent intent = new Intent(mContext, cls);
		startService(intent);
		intent=null;
	}
}
