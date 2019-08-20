/**
 * com.walktour.gui.locknet
 * LeadControler.java
 * 类功能：
 * 2014-6-19-下午4:33:12
 * 2014鼎利-版权所有
 * @author qihang.li@dinglicom.com
 */
package com.walktour.gui.locknet;

import android.content.Context;

/**
 * LeadControler
 * 
 * 2014-6-19 下午4:33:12
 * 
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
public class LeadControler extends ForceControler {

	// 以下是联芯的强制功能类型定义
	/**
	 * 锁定当前小区
	 */
	public static final byte CELL_LOCK_CURRENT = 0x00;
	/**
	 * 切换并锁定下面定义的TDS小区
	 */
	public static final byte CELL_CAMP_LOCK_TDS = 0x01;
	/**
	 * 切换到下面定义的TDS小区但不锁定
	 */
	public static final byte CELL_CAMP_TDS = 0x02;
	/**
	 * 解除当前小区锁定
	 */
	public static final byte CELL_UNLOCK_CURRENT = 0x03;
	/**
	 * 查询是否锁定小区
	 */
	public static final byte CELL_QUERY_LOCK = 0x04;
	/**
	 * 切换并锁定到下面定义的GSM小区
	 */
	public static final byte CELL_CMAP_LOCK_GSM = 0x05;
	/**
	 * 切换但不锁定到下面定义的GSM小区
	 */
	public static final byte CELL_CAMP_GSM = 0x06;
	/**
	 * 切换并锁定到下面定义的TDL小区
	 */
	public static final byte CELL_CAMP_LOCK_TDL = 0x07;
	/**
	 * 切换但不锁定到下面定义的TDL小区
	 */
	public static final byte CELL_CAMP_TDL = 0x08;
	/**
	 * 锁定当前频点
	 */
	public static final byte FREQ_LOCK_CURRENT = 0x09;
	/**
	 * 切换并锁定到下面定义的TDS频点
	 */
	public static final byte FREQ_CAMP_LOCK_TDS = 0x0A;
	/**
	 * 切换并锁定到下面定义的TDL频点
	 */
	public static final byte FREQ_CAMP_LOCK_TDL = 0x0B;
	/**
	 * 查询当前频点
	 */
	public static final byte FREQ_QUERY_CURRENT = 0x0C;
	/**
	 * 查询当前是否有锁定频点
	 */
	public static final byte FREQ_QUERY_LOCK = 0x0D;

	/**
	 * @see com.walktour.gui.locknet.ForceControler#init()
	 */
	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see com.walktour.gui.locknet.ForceControler#lockNetwork(com.walktour.gui.locknet.ForceControler.ForceNet)
	 */
	@Override
	public boolean lockNetwork(ForceNet networkType) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see com.walktour.gui.locknet.ForceControler#unLockAll()
	 */
	@Override
	public boolean unLockAll(ForceNet forceNets) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see com.walktour.gui.locknet.ForceControler#release()
	 */
	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

	/**
	 * @see com.walktour.gui.locknet.ForceControler#queryBand(int)
	 */
	@Override
	public boolean queryBand(ForceNet netType) {
		return false;
	}

	/**
	 * @see com.walktour.gui.locknet.ForceControler#queryFrequency(com.walktour.gui.locknet.ForceControler.ForceNet)
	 */
	@Override
	public boolean queryFrequency(ForceNet netType) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see com.walktour.gui.locknet.ForceControler#queryCell(com.walktour.gui.locknet.ForceControler.ForceNet)
	 */
	@Override
	public boolean queryCell(ForceNet netType) {
		return false;
	}

	/**
	 * @see com.walktour.gui.locknet.ForceControler#lockCell(java.lang.String,
	 *      int)
	 */
	@Override
	public boolean lockCell(Context context, ForceNet netType, String... args) {
		return false;
	}

	/**
	 * @see com.walktour.gui.locknet.ForceControler#campCell(com.walktour.gui.locknet.ForceControler.ForceNet,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public boolean campCell(ForceNet netType, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setAirplaneModeSwitch(Context context, boolean flag) {
		return false;
	}

	@Override
	public boolean setVolteSwitch(Context context, boolean flag) {
		return false;
	}

	@Override
	public boolean lockFrequency(Context context, ForceNet netType, String... args) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see com.walktour.gui.locknet.ForceControler#lockBand(com.walktour.gui.locknet.ForceControler.ForceNet,
	 *      java.lang.String)
	 */
	@Override
	public boolean lockBand(ForceNet netType, String arg) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see com.walktour.gui.locknet.ForceControler#lockBand(com.walktour.gui.locknet.ForceControler.ForceNet,
	 *      com.walktour.gui.locknet.ForceControler.Band)
	 */
	@Override
	public boolean lockBand(ForceNet netType, Band[] band) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unlockFrequency(Context context, ForceNet networkType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unlockCell(Context context, ForceNet networkType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean lockBand(Context context, ForceNet netType, Band[] band) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean lockNetwork(Context context, ForceNet networkType) {
		// TODO Auto-generated method stub
		return false;
	}

	//@Override
	public boolean setScrambleState(Context context, boolean flag){
		return  false;
	}

	@Override
	public boolean setAPN(Context context, String arg) {
		return false;
	}

	@Override
	public void makeVideoCall(Context context, String number) { return; }
}
