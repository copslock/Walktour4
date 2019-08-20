package com.walktour.service.phoneinfo.pointinfo.model;

import com.walktour.Utils.EventBytes;

/***
 * 采样点头信息
 * 
 * @author weirong.fan
 *
 */
public final class PointInfoHeader implements Complex {
	/** 网络编码：LTE */
	public static int MSG_CODE_LTE = 0xF0400001;
	/** 网络编码：WCDMA */
	public static int MSG_CODE_WCDMA = 0xF0400002;
	/** 网络编码：TDSCDMA */
	public static int MSG_CODE_TDSCDMA = 0xF0400003;
	/** 网络编码：EVDO */
	public static int MSG_CODE_EVDO = 0xF0400004;
	/** 网络编码：CDMA */
	public static int MSG_CODE_CDMA = 0xF0400005;
	/** 网络编码：GSM */
	public static int MSG_CODE_GSM = 0xF0400006;
	/** 网络编码 */
	private long msgCode;
	/** 0表示下行，1表示上行，2表示手机信令 */
	private int msgDirection = 2;
	/** 信令的类型 */
	private int protocalType = 0xFF;
	/** 信道的类型 */
	private int channelType = 0xFF;
	/** 信令时间 */
	private long handSetTime;
	/** 相邻采样点的信令时间的间隔，单位：微秒 */
	private int handSetInterval;
	/** 信令内容 */
	private String msgBinary = "";

	/***
	 * 构造函数
	 * 
	 * @param msgCode
	 *          信令编码
	 * @param handSetInterval
	 *          相邻采样点的信令时间的间隔
	 */
	public PointInfoHeader(long msgCode) {
		this.msgCode = msgCode;
		this.handSetTime = System.currentTimeMillis() * 1000;
	}

	@Override
	public void addEventValue(EventBytes eventByte) {
		eventByte.addInt64(msgCode);
		eventByte.addInteger(msgDirection);
		eventByte.addInteger(protocalType);
		eventByte.addInteger(channelType);
		eventByte.addInt64(this.handSetTime);
		eventByte.addInteger(handSetInterval);
		eventByte.addStringBuffer(msgBinary);
	}

	public long getHandSetTime() {
		return handSetTime;
	}

	public void setHandSetInterval(int handSetInterval) {
		this.handSetInterval = handSetInterval;
	}

}
