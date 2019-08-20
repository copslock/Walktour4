package com.walktour.control.bean;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct.CurrentNetState;
import com.walktour.Utils.WalkStruct.NetType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.gui.R;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.locknet.ForceManager;
import com.walktour.model.NetStateModel;
import com.walktour.model.TotalSpecialModel;
import com.walktour.service.SamsungService;
import com.walktour.service.app.StateService;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;


/**
 * 全局监听手机状态:信号、通话状态、网络状态 提供手机有关参数:手机制式,手机标识号,本地号码
 *
 * */
public class MyPhoneState {
	private final String tag = "MyPhoneState";
	/**数据连接状态为*/
	public final static int DATA_STATE_DISCONNECTED = 0;
	public final static int DATA_STATE_WIFI = 1;
	public final static int DATA_STATE_MOBILE = 2;

	private static TelephonyManager telManager;
	private static int mCallState;				/**通话状态*/
	private final static String KEY_SYNC = "synchronize";
	private int deviceInfoDeviceType ;

	private int mDataState;				/**数据状态*/
	private boolean isServiceOn;

	// Is dumpsys Phone Info Empty
    private boolean mIsDumpsysEmpty;
	private int preferredNetworkType = 0;

	private StateService mStateService ;

	/** 私有构造函数 */
	private MyPhoneState() {
		deviceInfoDeviceType = Deviceinfo.getInstance().getNettype();
	}

	/** PhoneState类的静态值 */
	private static MyPhoneState mPhoneState;

	/** PhoneState类的静态实例化 */
	public synchronized static MyPhoneState getInstance() {
		if (mPhoneState == null) {
			mPhoneState = new MyPhoneState();
		}
		return mPhoneState;
	}

	/**
	 * 是否已经时间同步
	 */
	public static boolean  hasSync(Context context){
		SharedPreferences share = context.getSharedPreferences( context.getPackageName()+"_preferences", Context.MODE_PRIVATE );
		if( !share.contains( KEY_SYNC ) ){
			Editor editor = share.edit();
			editor.putBoolean( KEY_SYNC, false );
			editor.commit();
		}
		return share.getBoolean( KEY_SYNC , false );
	}

	/**
	 *设置是否已经时间同步
	 */
	public static void setSync(Context context,boolean hasSync){
		SharedPreferences share = context.getSharedPreferences( context.getPackageName()+"_preferences", Context.MODE_PRIVATE );
		if( !share.contains( KEY_SYNC ) ){
			Editor editor = share.edit();
			editor.putBoolean( KEY_SYNC, false );
			editor.commit();
		}
		Editor editor = share.edit();
		editor.putBoolean( KEY_SYNC, hasSync );
		editor.commit();
	}

	/**
	 * 获得程序安装时间
	 * @return
	 */
	public static long getFisrtInstallTime(Context mContexnt){
		try {
			PackageManager packageManager = mContexnt.getPackageManager();
			return packageManager.getPackageInfo(mContexnt.getPackageName(), 0).firstInstallTime;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return -9999;
	}

	/**
	 * 开始监听，PhoneState实例化后必须运行此方法才能获取手机信号状态变化
	 *
	 * @param context
	 *            监听手机信号的Activity或者Service
	 * */
	public void listenPhoneState(Context context) {
		if( context instanceof StateService){
			this.mStateService = (StateService) context;
		}
		telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		telManager.listen(mPhoneStateListener,
				PhoneStateListener.LISTEN_SERVICE_STATE
				| PhoneStateListener.LISTEN_CALL_STATE
				| PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
	}

	/**
	 * 是否有手机信号
	 *
	 * 在独立进程中要运行监听方法后方可读取
	 * */
	public boolean isServiceAlive() {
		return this.isServiceOn;
	}

	/**
	 * 通过解码库设置当前的脱网状态
	 * @param isOn
	 */
	public void setServiceAlive(boolean isOn){
		isServiceOn = isOn;
	}

	/**
	 * 是否有可用互连网
	 **/
	public boolean isNetworkAvirable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		return (activeNetInfo != null && activeNetInfo.isConnected());
	}

	/**
	 * 判断wifi与数据模式
	 */
	 public int getDataConnectState(Context context){
	        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(
	                Context.CONNECTIVITY_SERVICE);
	        State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
	        State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
	        //如果3G、wifi、2G等网络状态是连接的，则退出，否则显示提示信息进入网络设置界面
	        if(mobile == State.CONNECTED)
	        {
	        	return DATA_STATE_MOBILE;
	        }
	        if(wifi == State.CONNECTED)
	        {
	        	return DATA_STATE_WIFI;
	        }

	        return DATA_STATE_DISCONNECTED;
	  }

    public boolean hasWifiConnect(Context context){
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        State wifiState = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if( wifiState == State.CONNECTED || wifiState==State.CONNECTING ){
            return true;
        }

        return false;
    }



	/** 手机状态监听器 */
	private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
		/**
		 * 正在等待信号开启
		 */
		private boolean isTurnningPowerOn = false;
		@Override
		/**手机信号改变回调函数*/
		public void onServiceStateChanged(ServiceState serviceState) {
			if (serviceState.getState() == ServiceState.STATE_IN_SERVICE) {
				// 更改当前信号状态为on
				LogUtil.w(tag, "Service is on");
				isServiceOn = true;
				RefreshEventManager.notifyRefreshEvent(RefreshType.REFRESH_PARAM_TAB,null);
			} else {
				// 更改当前信号状态为off
				LogUtil.w(tag, "Service is off");
				isServiceOn = ConfigRoutine.getInstance().checkOutOfService() ? false : true;
			}

			//无线状态关闭后自动恢复(Attach测试会导致的异常)
			if( serviceState.getState()== ServiceState.STATE_POWER_OFF ){
				new Thread(){
					@Override
					public void run(){
						if( !isTurnningPowerOn ){
							isTurnningPowerOn = true;

							while( !isServiceOn ){
								LogUtil.w(tag, "power will be trun on in 10 seconds");
								try {
									Thread.sleep(10*1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								if( isServiceOn ){
									break;
								}
								new MyPhone( mStateService ).setRadioPower( true );
							}

							isTurnningPowerOn = false;
						}
					}
				}.start();
			}

		}

		@Override
		public void onCallStateChanged(int state, String incomingNumber){
			LogUtil.i(tag, "---CallState:"+state);
			mCallState = state;

			if( mStateService != null ){
				if( state == TelephonyManager.CALL_STATE_RINGING ){
					if( ConfigRoutine.getInstance().isAcceptCall(mStateService) ){
						MyPhone phone = new MyPhone( mStateService );
						phone.aceeptCall();
					}
				}
			}
		}

		@Override
		public void onDataConnectionStateChanged(int state) {
			super.onDataConnectionStateChanged(state);
			mDataState = state;
			LogUtil.w(tag, "--onDataConnectionStateChanged:" + mDataState);
		}
	};

	/** 获取手机管理器 */
	public TelephonyManager getTelephoneManager(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm;
	}

	/**
	 * 获取手机类型(GSM,CDMA,NONE)
	 * */
	public int getPhoneType(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getPhoneType();
	}

	/**
	 * 获取手机网络类型
	 *
	 * @see TelephonyManager;
	 * */
	public int getNetWorkType(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getNetworkType();
	}

	/**
	 * 返回移动网络运营商的名字(SPN)
	 * @param context
	 * @return
	 */
	public String getNetworkOperateName(Context context){
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String networkOperator = tm.getNetworkOperator();//46000等
		if(!TextUtils.isEmpty(networkOperator)){
			switch (networkOperator) {
				case "46003":
				case "46005":
				case "46011":
					return context.getString(R.string.china_telecom);
				case "46001":
				case "46006":
				case "46009":
					return context.getString(R.string.china_unicom);
				case "46000":
				case "46002":
				case "46007":
				case "46004":
				case "46020":
					return context.getString(R.string.china_mobile);
			}
		}
		return tm.getNetworkOperatorName();
	}

	/**
	 * 返回移动网络运营商的名字(MCC+MNC)
	 * @param context
	 * @return
	 */
	public String getNetworkOperate(Context context){
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getNetworkOperator();
	}


	/**
	 * 根据当前的使用的网络类型判断当前归属网络
	 * @param context
	 * @return
	 */
	public NetType getCurrentNetType(Context context){
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String operator = telephonyManager.getSimOperator();
		LogUtil.w("getCurrentNetType","getCurrentNetType=="+getNetWorkType(context));
		switch(getNetWorkType(context)){
		case TelephonyManager.NETWORK_TYPE_EDGE:
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return NetType.GSM;
		case TelephonyManager.NETWORK_TYPE_HSDPA:
		case TelephonyManager.NETWORK_TYPE_HSUPA:
		case TelephonyManager.NETWORK_TYPE_HSPA:
			if (operator != null && (operator.equals("46000") || operator.equals("46002") || operator.equals("46007"))) {
				return NetType.TDSCDMA;
			}
			return NetType.WCDMA;
		case TelephonyManager.NETWORK_TYPE_UMTS:
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return NetType.WCDMA;
		case TelephonyManager.NETWORK_TYPE_CDMA:
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return NetType.CDMA;
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			return NetType.EVDO;
		case TelephonyManager.NETWORK_TYPE_LTE:
			return NetType.LTE;
			case TelephonyManager.NETWORK_TYPE_NR:
				return NetType.ENDC;
		case 16:
		case 17:
			return NetType.TDSCDMA;
		default:
			//return NetType.UnKnown;
			return NetType.getNetTypeByID(deviceInfoDeviceType);
		}
	}

	/**
	 * 屏蔽此查询方式
	 * @param context
	 * @return
	 */
	public int getCurrentNetForParam1(Context context){
	    NetType netType = getCurrentNetType(context);
	    int net = 3;
	    switch (netType) {
            case GSM:
                net = 2;
                break;
            case WCDMA:
            case TDSCDMA:
                net = 3;
                break;
            case LTE:
                net = 4;
                break;
            case CDMA:
            case EVDO:
            	net = NetStateModel.getInstance().isEvdoIdle() ? 2 : 3;
            	break;
            default:
                break;
        }
	    return net;
	}

    /**
     * save current nettype string to sharedpreferences
     * @param netTypeName
     */
	public static void saveNetTypeToSpf(String netTypeName){
        if (!netTypeName.equals(getNetTypeFromSpf())) {
            SharedPreferences spf = WalktourApplication.getAppContext().getSharedPreferences("NetType",Context.MODE_MULTI_PROCESS);
            spf.edit().putString("CURRENT_NET_TYPE",netTypeName).commit();
        }
    }

    /**
     * get current nettype string from sharedpreferences
     * @return
     */
    public static String getNetTypeFromSpf(){
        SharedPreferences spf = WalktourApplication.getAppContext().getSharedPreferences("NetType",Context.MODE_MULTI_PROCESS);
        return spf.getString("CURRENT_NET_TYPE","UnKnown");
    }

	/**
	 * 获取网络类型 (only for 电信招标）
	 * @param context
     * @param ipadWantLockNet ipad 下发的网络类型
	 * @return 2G、3G、4G
	 */
	public String parseNetType(Context context,String ipadWantLockNet){
		// if is oppo custom phone && nettype is 4G
        if (Deviceinfo.getInstance().isOppoCustom() && "4G".equals(ipadWantLockNet)){
            String currentNetType = getNetTypeFromSpf();
            LogUtil.i("MyPhoneState", currentNetType);
            return currentNetType.replace("LTE","4G");
        }
        String netStr = "UnKnown";
		int net = getCurrentNetForParam1(context);
		switch (net){
			case 2:
				netStr = "2G";
				break;
			case 3:
				netStr = "3G";
				break;
			case 4:
				netStr = "4G";
				break;
		}
		return netStr;
	}

	/**
	 * 根据数据集查询网络状态
	 * @param context
	 * @return
	 */
	public int getCurrentNetForParam(Context context){
		int net = 3;
		CurrentNetState netType = !ApplicationModel.getInstance().isFreezeScreen() ? TraceInfoInterface.currentNetType
				: TraceInfoInterface.decodeFreezeNetType;
			switch (netType) {
				case GSM:
					net = 2;
					break;
				case LTE:
				case NBIoT:
				case CatM:
				case ENDC:
					net = 4;
					break;
//				case NBIoT:
//					net = 5;
//					break;
				case WCDMA:
				case TDSCDMA:
					net = 3;
					break;
				case CDMA:
					net = NetStateModel.getInstance().isEvdoIdle() ? 2 : 3;
					break;
				case NoService:
				case Unknown:
				default:
					break;
			}
	    return net;
	}



	/**获取基带版本信息*/
	public String getBaseband(){
		try {
    		Class<?> cl = Class.forName("android.os.SystemProperties");
    		Object invoker = cl.newInstance();
    		Method m = cl.getMethod("get", new Class[] { String.class,String.class });
    		Object result = m.invoke(invoker, new Object[]{"gsm.version.baseband", "no message"});
    		return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "no message";
		}
	}


	/**
	 * [获得当前网络类型名称]<BR>
	 * [功能详细描述]
	 * @param context
	 * @return
	 */
	public String getNetworkName(Context context){
	    int netWorkType = getNetWorkType(context);
	    String networkName = "Network Unavailable";
	    switch (netWorkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                networkName = "GPRS";
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                networkName = "EDGE";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                networkName = "UMTS";
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                networkName = "HSDPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                networkName = "HSUPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                networkName = "HSPA";
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                networkName = "CDMA";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                networkName = "EvDo rev. 0";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                networkName = "EvDo rev. A";
                break;
            case 12 : //TelephonyManager.NETWORK_TYPE_EVDO_B:
                networkName = "EvDo rev. B";
                break;
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                networkName = "CDMA - 1xRTT";
                break;
            case 13:
                networkName = "LTE";
                break;
            case 14:
                networkName = "EHRPD";
                break;
            case 15:
                networkName = "HSPA+";
                break;
            case 16:
            case 17:
            	networkName = "TD-SCDMA";
            	break;
            default:
                networkName = "Network Unavailable";
                break;
        }
	    //LogUtil.w(tag,"---netWorkType:"+netWorkType+"--networkName:"+networkName);
	    return networkName;
	}

	/**
	 * [获得网络名称及当前网络状态，用于PING等业务的统计区分]<BR>
	 * [功能详细描述]
	 * @param netType
	 * @return
	 */
	public static TotalSpecialModel getNetworkNameForTotal(int netWorkType,int netType){
	    TotalSpecialModel spModel = new TotalSpecialModel("Other","Other");
        switch (netWorkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
                spModel.setMainKey1("GSM");
                if(TraceInfoInterface.getParaValue(UnifyParaID.G_GPRS_UL_TBF_State).equals("1")){
                    spModel.setMainKey2("TBF Open");
                }else{
                    spModel.setMainKey2("TBF Close");
                }
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case 15:    //HSPA+
                /*spModel.setMainKey1("WCDMA");
                if(TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_RRC_State).equals("2")){
                    spModel.setMainKey2("Cell FACH");
                }else{
                    spModel.setMainKey2("Cell DCH");
                }
                break;*/
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case 17:
                if(netType == NetType.TDSCDMA.getNetType()
                		|| netType == NetType.LTETDD.getNetType()){
                    spModel.setMainKey1("TDSCDMA");
                }else if(netType == NetType.WCDMA.getNetType()
                		|| netType == NetType.LTE.getNetType()){
                    spModel.setMainKey1("WCDMA");
                    if(TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_RRC_State).equals("2")){
                        spModel.setMainKey2("Cell FACH");
                    }else{
                        spModel.setMainKey2("Cell DCH");
                    }
                }else{
                	spModel.setMainKey1("Other");
                }
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                spModel.setMainKey1("CDMA");
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case 12 : //TelephonyManager.NETWORK_TYPE_EVDO_B:
                spModel.setMainKey1("EVDO");
                break;
            case 13:
                spModel.setMainKey1("LTE");
                break;
            case 14:
                spModel.setMainKey1("EHRPD");
                break;
            default:
                spModel.setMainKey1("Other");
                break;
        }
	    return spModel;
	}

	/** 获取手机串号 */
	public String getDeviceId(Context context) {
//		return "865407010000009";
		TelephonyManager apiTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String apiImei = apiTm.getDeviceId();

        String dumpStr = "";
        if (!mIsDumpsysEmpty) {
            // 该方法仅针对 4.4以下有效 ！！！
            dumpStr = UtilsMethod.getLinuxCommandResult("dumpsys iphonesubinfo");
            if (StringUtil.isNullOrEmpty(dumpStr)){
                mIsDumpsysEmpty = true;
            }
        }
        String deviceInfo = "";
		if(dumpStr != null && dumpStr.indexOf("Device") > 1 ){
			deviceInfo = dumpStr.substring(dumpStr.indexOf("=", dumpStr.indexOf("Device")) + 1).trim();
			LogUtil.w(tag,"--getDeviceId deviceInfo:" + deviceInfo);
		}

		if (StringUtil.isNullOrEmpty(deviceInfo) && apiImei != null) {
			deviceInfo = apiImei;
			LogUtil.w(tag,"--getDeviceId no dumpsys:" + deviceInfo);
		}else if(!deviceInfo.equals(apiImei)){
			if(deviceInfo.startsWith("*") && apiImei!=null && apiImei.endsWith(deviceInfo.substring(deviceInfo.length() - 7))){
				deviceInfo = apiImei;

				LogUtil.w(tag,"--getDeviceId dumpsys End Widit API:" + deviceInfo);
			}else{
				LogUtil.w(tag,"--getDeviceId Api no Equals dumpsys:" + deviceInfo);
			}
		}
		if(deviceInfo.startsWith("IMEI:")) {
			deviceInfo = deviceInfo.substring(5).trim();
		}
		return deviceInfo;
	}

	/**写序号时获得设备的IMEI，从最后第二位往前数8位当成狗号*/
	public int getDogId(Context context){
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		String d = deviceId.substring(deviceId.length() - 9,deviceId.length() - 1);
		int result = 0;
		try{
			result = Integer.parseInt( Deviceinfo.getInstance().getPhoneLogType()+d );
		}catch(Exception e){

		}
		return result;
	}

	/** 获取手机串号,CDMA为MEID，GSM为IMEI，xt800用下面的方法获得到的为MEID */
	public String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		// return new ConfigFleet().getIMEI();
		return tm.getDeviceId().length() == 14 ? tm.getDeviceId() + "0" : tm
				.getDeviceId();
	}

	/** 获取登录Fleet服务器的GUID
	 *  12345678-9012-3456-0000-000000000000
	 * */
	public String getGUID(Context context) {
		return getGUID(context,false);
	}

	/**
	 * 获取登录Fleet服务器的GUID
	 * 12345678-9012-3456-0000-000000000000
	 *
	 * @param context
	 * @param isEncry	是否加密,如果加密第二三位为固定值 D2 = 0x68BD;D3 = 0x859B;
	 * @return
	 */
	public String getGUID(Context context,boolean isEncry) {
		String deviceId = getMyDeviceId(context);

		if(isEncry && deviceId.length() > 8){
			return String.format("%s-68BD-859B-0000-000000000000",deviceId.substring(0, 8));
		}
		//IMEI
		else if( deviceId.length() == 14 ){
			return String.format("%s-%s-%s00-0000-000000000000",
					deviceId.substring(0, 8),deviceId.substring(8, 12),deviceId.substring(12, 14) );
		}
		//MEID
		else if( deviceId.length() == 15){
			return String.format("%s-%s-%s0-0000-000000000000",
					deviceId.substring(0, 8),deviceId.substring(8, 12),deviceId.substring(12, 15) );
		}
		//Bluetooth MAC
		else if( deviceId.length() == 16 ){
			return String.format("%s-%s-%s-0000-000000000000",
					deviceId.substring(0, 8),deviceId.substring(8, 12),deviceId.substring(12, 16) );
		}
		return "";
	}

	/** 国际移动用户识别码 */
	public String getIMSI(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSubscriberId();
	}

	/** 获取Sim的电话号码,部分Sim卡无法获取 */
	public String getSimNumber(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm == null || tm.getLine1Number() == null ? "" : tm.getLine1Number();
	}

	/**
	 * SDCard是否挂载
	 * */
	public boolean isSDCardMounted() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取优先网络类型(GSM/WCDMA/CDMA/EVDO) 在启动时由后台程序WalktourPhone查询,发送广播
	 * WalkBroadcastReceiver接收此广播后改变preferredNetworkType
	 * */
	public int getPreferedNetWorkType() {
		return preferredNetworkType;
	}

	public void setPreferredNetworkType(int type) {
		preferredNetworkType = type;
	}

	/**
	 * 判断一个服务是否已经启动
	 *
	 * @param context 调用此方法的Activity或者Service
	 * @param nameOfService
	 *            要判断是否启动的服务名
	 * */
	public static boolean isWorked(Context context, String nameOfService) {
		ActivityManager myManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
				.getRunningServices(100);
		for (int i = 0; i < runningService.size(); i++) {
			if (runningService.get(i).service.getClassName().toString().equals(
					nameOfService)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 在独立进程中要运行监听方法后方可读取
	 * @return 通话状态
	 */
	public static int getCallState() {
		return mCallState;
	}

	public int getDataState(){
		return mDataState;
	}

	private String myDeviceId = null;
	/**
	 * 设备标识，如果是非CDMA制式，取手机IMEI号，CDMA制式则取蓝牙加密成15位
	 * @param mContext
	 * @return 返回标识
	 */
	public String getMyDeviceId(Context mContext) {

			TelephonyManager tm = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			//非全网通手机且当前为电信网络
			if (!Deviceinfo.getInstance().isAllNet() && tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
				if (null == myDeviceId || "".equals(myDeviceId)) {
					BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
					if (!adapter.isEnabled()) {
						adapter.enable();
						while (!adapter.isEnabled()) {
							try{Thread.sleep(500);}catch(Exception e){}
						}
					}
					String address = adapter.getAddress();
					adapter.disable();
					myDeviceId = encode(address);

					if(StringUtil.isNullOrEmpty(myDeviceId)){
						//myDeviceId = tm.getDeviceId();
						myDeviceId = getDeviceId(mContext);
					}
				}
			} else {
				//myDeviceId = tm.getDeviceId();
				myDeviceId = getDeviceId(mContext);
			}
//		return "867464034545418";
		return myDeviceId;
	}

	/**
	 * 获得蓝牙地址信息
	 * @return
	 */
	public String getBluetoothAddress(){
		BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		return m_BluetoothAdapter.getAddress();
	}

	/**
	 * MD5 位加密 并抽取16位
	 *
	 * @param value
	 * @return
	 */
	private String encode(String value) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		if(md == null)
			return null;
		md.update(value.getBytes());
		byte b[] = md.digest();
		int i;
		StringBuffer buf = new StringBuffer("");
		for (int offset = 0; offset < b.length; offset++) {
			i = b[offset];
			if (i < 0)
				i += 256;
			if (i < 16)
				buf.append("0");
			buf.append(Integer.toHexString(i));
		}
		return buf.toString().substring(8, 24).toUpperCase(Locale.getDefault());
	}


	/**
	 * 获取基础版本号
	 * @return
	 */
	public  String getBaseBandVersion(){
		String baseVersion = "";
		try {
			baseVersion = Build.BOOTLOADER;
		} catch (Exception e) {
			Log.i("MyPhone", "getBaseBandVersion err");
			return "";
		}
		if (baseVersion != null){
			return  baseVersion;
		}
		return "";
	}

	/**
	 * 获得Android版本
	 * @return
	 */
	public  String getAndroidVersion() {
		String androidVersion = "";
		try {
			androidVersion = Build.VERSION.RELEASE;
		} catch (Exception e) {
			Log.i("MyPhone", "androidVersion err");
			return "";
		}
		if (androidVersion != null){
			return  androidVersion;
		}
		return "";
	}

	public  String getLocalIpv6Address() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e(tag,ex.getMessage(),ex);
		}
		return null;
	}

	public  String getLocalIpv4Address() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();

                    String ip = inetAddress.getHostAddress().toString();
                    if (!inetAddress.isLoopbackAddress()
							&& !inetAddress.isLinkLocalAddress()
                            && Verify.isIp(ip) ) {
						return ip;
					}
				}
			}
		} catch (SocketException ex) {
			Log.e(tag,ex.getMessage(),ex);
		}

		return "";
	}

	public  String getLocalMacAddress(Context ctx) {
		WifiManager wifi = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}


	/**
	 * 打开飞行模式
	 *
	 */
	public void startAirplane(Context context) {
		if(ApplicationModel.getInstance().isNBTest()) {
			//如果是NB模块,则不用开取飞行模式
			return;
		}
		LogUtil.d(tag, "开启飞行模式");
		if (Deviceinfo.getInstance().isSamsungCustomRom()){
			if(Deviceinfo.getInstance().isCustomS9()){
				SamsungService.getInStance(context).getService().setRadioPower(0,false);
			}else if (Deviceinfo.getInstance().isA60Custom()) {
				UtilsMethod.runRootCommand("settings put global airplane_mode_on 1&");
				UtilsMethod.runRootCommand("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true&");
			} else {
				airplaneCustomS8(context, true);
			}
		}else if(Deviceinfo.getInstance().isVivo()){
			ForceManager forceManager=ForceManager.getInstance();
			forceManager.init();
			forceManager.setAirplaneModeSwitch(context,true);
		}else {
			UtilsMethod.runRootCommand("settings put global airplane_mode_on 1&");
			UtilsMethod.runRootCommand("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true&");
		}
		LogUtil.d(tag, "开启飞行执行完毕");
	}

	/**
	 * 关闭飞行模式
	 */
	public void closeAirplane(Context context) {
		if (ApplicationModel.getInstance().isNBTest()) {
			//如果是NB模块,则不用关闭飞行模式
			return;
		}
		LogUtil.d(tag, "关闭飞行模式 ");
		if (Deviceinfo.getInstance().isSamsungCustomRom()) {
			if(Deviceinfo.getInstance().isCustomS9()){
				SamsungService.getInStance(context).getService().setRadioPower(0,true);
			}else if (Deviceinfo.getInstance().isA60Custom()) {
				UtilsMethod.runRootCommand("settings put global airplane_mode_on 0&");
				UtilsMethod.runRootCommand("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false&");
			}else{
				airplaneCustomS8(context, false);
			}
		} else if (Deviceinfo.getInstance().isVivo()) {
			ForceManager forceManager = ForceManager.getInstance();
			forceManager.init();
			forceManager.setAirplaneModeSwitch(context, false);
		} else {
			UtilsMethod.runRootCommand("settings put global airplane_mode_on 0&");
			UtilsMethod.runRootCommand("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false&");
		}
		LogUtil.d(tag, "关闭飞行模式方法 执行完毕");
	}

	/**
	 * s8定制机的开关飞行模式
	 *
	 * @param context
	 * @param mode    true为开，false为关
	 */
	public void airplaneCustomS8(Context context, boolean mode) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.B2B.AIRPLANE_MODE_SET");
		intent.putExtra("AirplaneMode", mode);
		context.sendBroadcast(intent);
	}


	/**
	 * 当前是否中文环境
	 * 非中文环境都返回false
	 * @param context
	 * @return
	 */
	public static boolean isZhForLanguage(Context context){
		Locale locale = context.getResources().getConfiguration().locale;
		return locale.getLanguage().toLowerCase().endsWith("zh");
	}
}