package com.walktour.Utils;

/**
 * 地图窗口显示事件类型
 * @author Administrator
 *
 */
public class CallType {
	public static final int GpsPoint		= -1;	//GPS默认点
	public static final int CallEstablished	= 1;	//接通
	public static final int CallEnd 		= 2;	//通话结束
	public static final int BlockCall		= 3;	//未接通
	public static final int DroppedCall 	= 4;	//掉话
	public static final int CallAttempt 	= 5;	//起呼
	public static final int HandOff 		= 6;	//切换
}
