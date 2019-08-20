package com.walktour.gui.locknet;

import android.content.Context;
import android.content.SharedPreferences;

import com.walktour.Utils.ApplicationModel;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.locknet.ForceControler.Band;
import com.walktour.gui.locknet.ForceControler.ForceNet;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * ForceManager 强制功能管理，锁定小区，网络，频点 2013-11-4 下午3:20:10
 * 
 * @author qihang.li@dinglicom.com
 * @version 1.0.0
 *
 */
public class ForceManager {
	/** 关键字：锁网 */
	public static final String KEY_LOCK_NET = "lock_net";
	/** 关键字：锁频段 */
	public static final String KEY_LOCK_BAND = "lock_band";
	/** 关键字：锁频点 */
	public static final String KEY_LOCK_FREQUENCY = "lock_Frequency";
	/** 关键字：锁小区 */
	public static final String KEY_LOCK_CELL = "lock_cell";
	/** 关键字：锁解锁频点和小区 */
	public static final String KEY_UNLOCK_FREQUENCY_CELL = "unlock_frequency_cell";

	private static ForceManager instance = null;

	public static ForceManager getInstance() {
		if (instance == null) {
			instance = new ForceManager();
		}
		return instance;
	}

	public ForceManager(){
	    init();
    }

	private ForceControler mControler;

	/**
	 * 初始化
	 * 
	 * @exception
	 * @since 1.0.0
	 */
	public void init() {
		if (mControler == null) {
			String deviceModel = Deviceinfo.getInstance().getDevicemodel();
			if (ApplicationModel.getInstance().isNBTest()){
				mControler = new NBDeviceControler();
			} else if (deviceModel.equals("SIM929")) {
				mControler = new SimControler();
			} else if (deviceModel.equals("L8161")) {
				mControler = new LeadControler();
			} else if (deviceModel.equals("HuaweiMT7")) {
				mControler = new HisiliconContoler();
			} else if (Deviceinfo.getInstance().isVivo()) {
				mControler = new VivoControler();
			} else if (Deviceinfo.getInstance().isS8CustomRomV1()) {
				mControler = new QualcomContoler();
			} else if (Deviceinfo.getInstance().isS8CustomRom() || Deviceinfo.getInstance().isS9CustomRom()) {
				mControler = new SamsungCustomControler();
			} else if (Deviceinfo.getInstance().getLockInfo().hasLock()) {
				mControler = new QualcomContoler();
			}
		}

		if (mControler != null) {
			mControler.init();
		}
	}

	public void release() {
		if (mControler != null) {
			mControler.release();
			mControler = null;
		}
	}

	/**
	 * 函数功能：锁定网络优先
	 * 
	 * @param networkType
	 * @return
	 */
	public void lockNetwork(ForceControler.ForceNet networkType) {
		if (mControler != null && networkType != null) {
			mControler.lockNetwork(networkType);
		}
	}

	/**
	 * 函数功能：锁定网络优先
	 * 
	 * @param networkType
	 * @return
	 */
	public void lockNetwork(Context context, ForceControler.ForceNet networkType) {
		if (mControler != null && networkType != null) {
			mControler.lockNetwork(context, networkType);
		}
	}

	/**
	 * 解除频点锁定
	 * 
	 * @param context
	 *          上下文
	 * @param networkType
	 *          网络类型
	 * @return
	 */
	public boolean unlockFrequency(Context context, ForceNet networkType) {
		if (mControler != null && networkType != null) {
			return mControler.unlockFrequency(context, networkType);
		}
		return false;
	}

	/**
	 * 解除小区锁定
	 * 
	 * @param context
	 *          上下文
	 * @param networkType
	 *          网络类型
	 * @return
	 */
	public boolean unlockCell(Context context, ForceNet networkType) {
		if (mControler != null && networkType != null) {
			return mControler.unlockCell(context, networkType);
		}
		return false;
	}

	/**
	 * 函数功能：锁频段
	 * 
	 * @return
	 */
	public boolean lockBand(ForceNet netType, String arg) {
		if (mControler != null && arg != null) {
			return mControler.lockBand(netType, arg);
		}
		return false;
	}

	/**
	 * 函数功能：锁频段
	 * 
	 * @return
	 */
	public boolean lockBand(ForceNet netType, Band[] band) {
		if (mControler != null && band != null && band.length > 0) {
			return mControler.lockBand(netType, band);
		}
		return false;
	}

	/**
	 * 函数功能：锁频段
	 * 
	 * @return
	 */
	public boolean lockBand(Context context, ForceNet netType, Band[] band) {
		if (mControler != null && band != null && band.length > 0) {
			return mControler.lockBand(context, netType, band);
		}
		return false;
	}

	/**
	 * 函数功能：锁频点
	 * 
	 * @param context
	 *          上下文
	 * @param netType
	 *          网络类型
	 * @param args
	 *          频点参数
	 * @return
	 */
	public boolean lockFrequency(Context context, ForceNet netType, String... args) {
		if (mControler != null) {
			return mControler.lockFrequency(context, netType, args);
		}
		return false;
	}

	/**
	 * 函数功能：锁小区
	 * 
	 * @param context
	 *          上下文
	 * @param netType
	 *          网络类型
	 * @param args
	 *          小区参数
	 * @return
	 */
	public boolean lockCell(Context context, ForceNet netType, String... args) {
		if (mControler != null) {
			return mControler.lockCell(context, netType, args);
		}
		return false;
	}

	/**
	 * 解除所有强制
	 * 
	 * @exception
	 * @since 1.0.0
	 */
	public void unlockAll(ForceNet net) {
		if (mControler != null) {
			mControler.unLockAll(net);
		}
	}

	public void unlockAll(Context context, ForceNet net) {
		if (null != mControler) {
			mControler.unLockAll(context, net);
		}
	}

	/**
	 * 函数功能：查询频段
	 * 
	 * @param queryType
	 * @return
	 */
	public boolean queryBand(ForceNet queryType) {
		if (mControler != null) {
			return mControler.queryBand(queryType);
		}
		return false;
	}

	/**
	 * 函数功能：查询频点
	 * 
	 * @param queryType
	 * @return
	 */
	public boolean queryFrequency(ForceNet queryType) {
		if (mControler != null) {
			return mControler.queryBand(queryType);
		}
		return false;
	}

	/**
	 * 函数功能：查询小区
	 * 
	 * @param queryType
	 * @return
	 */
	public boolean queryCell(ForceNet queryType) {
		if (mControler != null) {
			return mControler.queryBand(queryType);
		}
		return false;
	}

	/**
	 * 函数功能：飞行模式的开关
	 *
	 * @param context
	 *          上下文
	 * @param flag
	 *          true: 开启飞行模式
	 *          false: 关闭飞行模式
	 */
	public boolean setAirplaneModeSwitch(Context context, boolean flag) {
		if (null == mControler)
			return false;

		return mControler.setAirplaneModeSwitch(context, flag);
	}

	/**
	 * 函数功能：设置volte功能的开启与关闭
	 *
	 * @param context
	 *          上下文
	 * @param flag
	 *          true: 开启volte功能
	 *          false: 关闭volte功能
	 * @return
	 */
	public boolean setVolteSwitch(Context context, boolean flag) {
		if (null == mControler)
			return false;

		return mControler.setVolteSwitch(context, flag);
	}

	/**
	 * 函数功能：设置volte功能的开启与关闭
	 *
	 * @param context
	 *          上下文
	 * @param number
	 *          电话号码
	 * @return
	 */
	public void makeVideoCall(Context context, String number) {
		if (null == mControler)
			return;

		mControler.makeVideoCall(context, number);
	}

	/**
	 * 函数功能：设置扰码状态
	 *
	 * @param context
	 *          上下文
	 * @param flag
	 *          true: 开启扰码状态
	 *          false: 关闭扰码状态
	 * @return
	 */
	public boolean setScrambleState(Context context, boolean flag) {
		if (null == mControler)
			return false;

		return mControler.setScrambleState(context, flag);
	}

	public boolean setAPN(Context context, String arg) {
		if (null == mControler)
			return false;

		return mControler.setAPN(context, arg);
	}

	public boolean setPSMState(Context context, String strArg) {
		if (null == mControler)
			return false;

		return  mControler.setPSMState(context, strArg);
	}

	public boolean setEDRXState(Context context, String strArg) {
		LogUtil.w("ForceManageer", "setEDRXSetting");
		if (null == mControler)
			return false;
		LogUtil.w("ForceManageer", "setEDRXSetting - 1");

		return  mControler.setEDRXState(context, strArg);
	}

	/**
	 * 设置任务的状态变化，如界面的进度变化 setOnTaskChangeListener
	 * 
	 * @param listener
	 */
	public void setOnTaskChangeListener(OnTaskChangeListener listener) {
		if (mControler != null) {
			mControler.setOnTaskChangeListener(listener);
		}
	}

	public void saveLockNet(Context context, ForceNet net) {
		SharedPreferences preference = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		preference.edit().putString(KEY_LOCK_NET, net.name()).commit();
	}

	public ForceNet getLockNet(Context context) {
		SharedPreferences preference = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		return ForceNet.valueOf(preference.getString(KEY_LOCK_NET, ForceNet.NET_AUTO.name()));
	}

	public void saveLockFrequency(Context context, String[] frequencyParams) {
		SharedPreferences preference = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		StringBuilder str = new StringBuilder();
		if (frequencyParams != null) {
			for (String param : frequencyParams) {
				str.append(param).append(",");
			}
			if (str.length() > 0)
				str.deleteCharAt(str.length() - 1);
		}
		preference.edit().putString(KEY_LOCK_FREQUENCY, str.toString()).commit();
	}

	public String getLockFrequency(Context context) {
		SharedPreferences preference = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		return preference.getString(KEY_LOCK_FREQUENCY, "");
	}

	public void saveLockCell(Context context, String[] cellParams) {
		SharedPreferences preference = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		StringBuilder str = new StringBuilder();
		if (cellParams != null) {
			for (String param : cellParams) {
				str.append(param).append(",");
			}
			if (str.length() > 0)
				str.deleteCharAt(str.length() - 1);
		}
		preference.edit().putString(KEY_LOCK_CELL, str.toString()).commit();
	}

	public String getLockCell(Context context) {
		SharedPreferences preference = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		return preference.getString(KEY_LOCK_CELL, "");
	}

	public void saveLockBand(Context context, Band[] band) {
		SharedPreferences preference = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		String str = "";
		for (Band b : band) {
			str += b.name() + ",";
		}
		preference.edit().putString(KEY_LOCK_BAND, str).commit();
	}

	public String getLockBands(Context context) {
		SharedPreferences preference = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		String[] bands = preference.getString(KEY_LOCK_BAND, "").split(",");
		String bandStr = "";
		for (int i = 0; i < bands.length; i++) {
			String b = bands[i];
			if (b.trim().length() > 0) {
				Band band = Band.valueOf(b);
				bandStr += (i > 0 ? "," : "") + band.toString();
			}
		}
		return bandStr;
	}

	public Band[] getLockBandArray(Context context) {
		Set<Band> set = this.getLockBand(context);
		if (!set.isEmpty()) {
			return (Band[]) set.toArray();
		}
		return new Band[0];
	}

	public Set<Band> getLockBand(Context context) {
		SharedPreferences preference = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		String[] bands = preference.getString(KEY_LOCK_BAND, "").split(",");
		Set<Band> set = new HashSet<Band>();
		for (String b : bands) {
			if (b.trim().length() > 0) {
				set.add(Band.valueOf(b));
			}
		}
		return set;
	}

}
