/**
 * com.walktour.gui.locknet
 * OnTaskChangeListener.java
 * 类功能：
 * 2013-11-7-上午11:28:39
 * 2013鼎利-版权所有
 * @author qihang.li@dinglicom.com
 */
package com.walktour.gui.locknet;


/**
 * OnTaskChangeListener
 * 任务执行状态变化接口
 * 2013-11-4 下午3:17:56
 * 
 * @version 1.0.0
 */
public class OnTaskChangeListener{
	/**
	 * 任务完成
	 * @param success 是否成功执行 
	 */
	public void onFinished( boolean success){
		//do nothing 
	}
	
	/**
	 * 函数功能：原有希母手机用的回调函数
	 * @param result
	 * @param resultContent
	 * @param opt
	 */
	public void onSimATFinished(Boolean result, String resultContent, int opt) {
		
	}
	
}
