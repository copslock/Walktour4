package com.walktour.service.phoneinfo.pointinfo.model;

import com.walktour.Utils.EventBytes;

/***
 * 采样点中的参数信息
 * 
 * @author weirong.fan
 *
 */
public final class PointInfoParam implements Complex {
	/** 参数ID，解码组定义 */
	private int paramID;
	/** 参数值，带有缩放比例的值 **/
	private int paramValue;
	/** 参数值的标记，解码组定义 **/
	private int paramValueFlag = 1;

	/***
	 * 构造函数
	 * 
	 * @param paramID
	 * @param paramValue
	 * @param paramValueFlag
	 */
	public PointInfoParam(int paramID, int paramValue) {
		super();
		this.paramID = paramID;
		this.paramValue = paramValue;
	}

	@Override
	public void addEventValue(EventBytes eventByte) {
		eventByte.addInteger(paramID);
		eventByte.addInteger(paramValue);
		eventByte.addInteger(paramValueFlag);
	}

}
