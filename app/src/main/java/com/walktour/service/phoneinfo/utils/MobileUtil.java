package com.walktour.service.phoneinfo.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.CurrentNetState;
import com.walktour.base.util.LogUtil;

import java.io.File;

/**
 * 获取收集基本信息工具类
 *
 * @author jianchao.wang
 *
 */
public class MobileUtil {
	/** 日志标识 */
	private static final String TAG = "MobileUtil";

	/**
	 * LTE网络,不区分电信和移动
	 *
	 * @param telephonyManager
	 * @return
	 */
	public static boolean isLteNetWork(TelephonyManager telephonyManager) {
		int netType = telephonyManager.getNetworkType();
		return netType == TelephonyManager.NETWORK_TYPE_LTE;
	}

	/**
	 * 电信3G网络
	 *
	 * @param telephonyManager
	 * @return
	 */
	public static boolean isEvdoNetwork(TelephonyManager telephonyManager) {
		int netType = telephonyManager.getNetworkType();
		return netType == TelephonyManager.NETWORK_TYPE_EVDO_0 || netType == TelephonyManager.NETWORK_TYPE_EVDO_A
				|| netType == TelephonyManager.NETWORK_TYPE_EVDO_B || netType == TelephonyManager.NETWORK_TYPE_EHRPD;
	}

	/**
	 * 判断Mobile网络是否开启
	 *
	 * @param context
	 * @return
	 */
	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 判断Wifi网络是否开启
	 *
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable()&&mWiFiNetworkInfo.getState()==NetworkInfo.State.CONNECTED;
			}
		}
		return false;
	}

	/***
	 * 获取信号强度值
	 *
	 * @param context
	 * @param signal
	 * @return
	 */
	public static int getSignalStrengthDbm(Context context, SignalStrength signal) {
		int signalStrenghInt = -1;
		if (signal.isGsm()) {// GSM
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			try {
				String[] sarray = signal.toString().split(" ");

				if (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE) {// LTE网络
					if (signal.toString().contains("gw|lte")) {
						signalStrenghInt = Integer.parseInt(sarray[11]);
					} else {
						signalStrenghInt = Integer.parseInt(sarray[9]);
					}
				} else {

					if (!signal.toString().contains("gw|lte") && !signal.toString().contains("gsm|lte")
							&& signal.getGsmSignalStrength() == 99) {
						return signal.getGsmSignalStrength();
					}
					signalStrenghInt = -113 + 2 * (signal.getGsmSignalStrength());
					if (signalStrenghInt < -1 && signalStrenghInt > -113) {// 信号强度正确

					} else {
						if (sarray[sarray.length - 2].equals("gsm|lte") && Integer.parseInt(sarray[sarray.length - 3]) < -1
								&& (Integer.parseInt(sarray[sarray.length - 3]) > -113)) {// ??????
							signalStrenghInt = Integer.parseInt(sarray[sarray.length - 3]);
						} else if (sarray[sarray.length - 1].equals("gsm|lte")) { // sony
							if (StringUtil.isInteger(sarray[sarray.length - 2])) {
								int value = Integer.parseInt(sarray[sarray.length - 2]);
								if (value < -1 && value > -113)
									signalStrenghInt = value;
							} else if (StringUtil.isInteger(sarray[sarray.length - 3])) {
								int value = Integer.parseInt(sarray[sarray.length - 3]);
								if (value < -1 && value > -113)
									signalStrenghInt = value;
							}
						} else if (signal.toString().contains("gw|lte")) {// ???D2????????
							int i = 1;
							while (i < sarray.length - 1) {
								if (!StringUtil.isInteger(sarray[i]))
									continue;
								signalStrenghInt = Integer.parseInt(sarray[i]);
								if (signalStrenghInt < -1 && signalStrenghInt > -113) {
									break;
								}
								i += 1;
							}
						} else {

						}
					}

				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			signalStrenghInt = signalStrenghInt == 99 || (signalStrenghInt >= -113 && signalStrenghInt <= -1)
					? signalStrenghInt : -1;
			return signalStrenghInt;
		} else if (signal.getCdmaDbm() != -1) {// CDMA
			// CDMA
			signalStrenghInt = signal.getCdmaDbm();
			signalStrenghInt = signalStrenghInt == 99 || (signalStrenghInt >= -113 && signalStrenghInt <= -1)
					? signalStrenghInt : -1;
			return signalStrenghInt;
		} else if (signal.getEvdoDbm() != -1) {// EVDO
			// EVDO
			signalStrenghInt = signal.getEvdoDbm();
			signalStrenghInt = signalStrenghInt == 99 || (signalStrenghInt >= -113 && signalStrenghInt <= -1)
					? signalStrenghInt : -1;
			return signalStrenghInt;
		} else {
			return -1;
		}
	}

	/**
	 * 判断sd卡是否存在
	 *
	 * @return
	 */
	public static boolean isSDCardAvailable() {
		boolean flag = false;
		try {
			flag = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		} catch (Exception ex) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 获取sd卡路径
	 *
	 * @param context
	 *          上下文
	 * @return
	 */
	public static String getSDPath(Context context) {
		File sdDir = null;
		if (isSDCardAvailable()) {
			sdDir = Environment.getExternalStorageDirectory();// 获取根目录
			return sdDir.toString();
		}
		return null;

	}

	/***
	 * 获取网络制式
	 *
	 * @param telephonyManager
	 * @return
	 */
	public static WalkStruct.CurrentNetState getNetworkType(TelephonyManager telephonyManager) {
		String operator = telephonyManager.getSimOperator();
		switch (telephonyManager.getNetworkType()) {
			case TelephonyManager.NETWORK_TYPE_EDGE:
			case TelephonyManager.NETWORK_TYPE_GPRS:
				return CurrentNetState.GSM;
			case TelephonyManager.NETWORK_TYPE_HSDPA:
			case TelephonyManager.NETWORK_TYPE_HSUPA:
			case TelephonyManager.NETWORK_TYPE_HSPA:
				if (operator != null && (operator.equals("46000") || operator.equals("46002") || operator.equals("46007"))) {
					return CurrentNetState.TDSCDMA;
				}
				return CurrentNetState.WCDMA;
			case TelephonyManager.NETWORK_TYPE_UMTS:
			case TelephonyManager.NETWORK_TYPE_HSPAP:
				return CurrentNetState.WCDMA;
			case TelephonyManager.NETWORK_TYPE_CDMA:
			case TelephonyManager.NETWORK_TYPE_1xRTT:
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
			case TelephonyManager.NETWORK_TYPE_EVDO_B:
				return CurrentNetState.CDMA;
			case TelephonyManager.NETWORK_TYPE_LTE:
				return CurrentNetState.LTE;
			case TelephonyManager.NETWORK_TYPE_EHRPD:
				return CurrentNetState.CDMA;
			case 16:
			case 17:
				return CurrentNetState.TDSCDMA;
			default:
				return CurrentNetState.Unknown;
		}
	}

	/***
	 * 获取手机的IMEI号
	 *
	 * @param context
	 *          上下文
	 *
	 * @return
	 */
	public static synchronized String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	/***
	 * 获取手机的IMSI号
	 *
	 * @param context
	 *          上下文
	 *
	 * @return
	 */
	public static String getIMSI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSubscriberId();

	}

	/**
	 * 获取SIM MCC MNC
	 *
	 * @param context
	 *          上下文
	 * @return
	 */
	public static String getSIM_MCCMNC(Context context) {

		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String mccmnc = "";
		if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY)
			mccmnc = telephonyManager.getSimOperator();
		if (mccmnc != null && !mccmnc.equals(""))
			return mccmnc;

		return "";
	}

	/**
	 * 获取SIM MCC
	 *
	 * @param context
	 *          上下文
	 * @return
	 */
	public static int getSIM_MCC(Context context) {
		String mccmnc = getSIM_MCCMNC(context);
		if (!mccmnc.equals(""))
			return Integer.parseInt(mccmnc.substring(0, 3));
		return -1;
	}

	/**
	 * 获取SIM MNC
	 *
	 * @param context
	 *          上下文
	 * @return
	 */
	public static int getSIM_MNC(Context context) {
		String mccmnc = getSIM_MCCMNC(context);
		if (!mccmnc.equals(""))
			return Integer.parseInt(mccmnc.substring(3));
		return -1;
	}

	/***
	 * 获取LTE 小区信息
	 *
	 * @param context
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static CellIdentityLte getCellIdentityLte(Context context) {
		CellIdentityLte lte = null;
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		if (currentVersion >= 17) {
			try {
				final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				if (null != tm.getAllCellInfo()) {
					for (final CellInfo info : tm.getAllCellInfo()) {
						if (info instanceof CellInfoLte) {
							lte = ((CellInfoLte) info).getCellIdentity();
							break;
						} else if (info instanceof CellInfoGsm) {

						}
					}
				}
			} catch (Exception e) {
				LogUtil.e(TAG, "Unable to obtain LTE cell signal information", e);
			}
		}
		return lte;
	}


	/** SIM卡是中国移动 */
	public static boolean isChinaMobile(Context context) {
		String imsi = getSIM_MCCMNC(context);
		if (imsi == null) return false;
		return imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007");
	}

	/** SIM卡是中国联通 */
	public static boolean isChinaUnicom(Context context) {
		String imsi = getSIM_MCCMNC(context);
		if (imsi == null) return false;
		return imsi.startsWith("46001");
	}

	/** SIM卡是中国电信 */
	public static boolean isChinaTelecom(Context context) {
		String imsi = getSIM_MCCMNC(context);
		if (imsi == null) return false;
		return imsi.startsWith("46003") || imsi.startsWith("46011") || imsi.startsWith("46005");
	}

}
