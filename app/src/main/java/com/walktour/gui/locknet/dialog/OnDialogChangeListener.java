/**
 * com.walktour.gui.locknet.dialog
 * OnDialogChangeListener.java
 * 类功能：
 * 2014-7-3-下午4:04:36
 * 2014鼎利-版权所有
 * @author qihang.li@dinglicom.com
 */
package com.walktour.gui.locknet.dialog;

/**
 * OnDialogChangeListener
 * 
 * 2014-7-3 下午4:04:36
 * 
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
public interface OnDialogChangeListener {
	public void onPositive();

	/**
	 * 锁定操作完后的相应
	 * 
	 * @param lockType
	 *          锁定类型
	 */
	public void onLockPositive(String lockType);
}
