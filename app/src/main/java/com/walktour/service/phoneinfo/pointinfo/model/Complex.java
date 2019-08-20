package com.walktour.service.phoneinfo.pointinfo.model;

import com.walktour.Utils.EventBytes;

/***
 * 复合类型
 * 
 * @author weirong.fan
 *
 */
public interface Complex {
	/***
	 * 写入事件信息值
	 * 
	 * @param eventByte
	 */
	public void addEventValue(EventBytes eventByte);
}
