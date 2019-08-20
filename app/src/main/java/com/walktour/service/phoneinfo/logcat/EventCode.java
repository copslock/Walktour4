package com.walktour.service.phoneinfo.logcat;

/**
 * 事件编码
 * 
 * @author jianchao.wang
 *
 */
public class EventCode {

	public static final int FLAG_CALL_NORMAL = 0;
	/**
	 * reason unkown
	 */
	public static final int FLAG_CALL_DIS_UNKOWN = 1;
	/**
	 * opposite side busy
	 */
	public static final int FLAG_CALL_DIS_BUSY = 2;
	/**
	 * local hangup (normal)
	 */
	public static final int FLAG_CALL_DIS_LOCAL = 3;
	/**
	 * opposite side handup (normal)
	 */
	public static final int FLAG_CALL_DIS_REMOTE = 4;
	/**
	 * drop
	 */
	public static final int FLAG_CALL_DIS_DROP = 5;
	/**
	 * opposite side ring
	 */
	public static final int FLAG_CALL_ALERTING = 6;

	/**
	 * call active
	 * 
	 */
	public static final int FLAG_CALL_ACTIVE = 7;

	/**
	 * noresponse
	 * 
	 */
	public static final int FLAG_CALL_NORESPONSE = 8;
	/**
	 * call block
	 * 
	 */
	public static final int FLAG_CALL_SETUP_FAILURE = 9;

	/**
	 * ringing
	 */
	public static final int FLAG_CALL_RINGING = 10;

	/**
	 * missed
	 */
	public static final int FLAG_CALL_MISSED = 11;

	/**
	 * rejected
	 */
	public static final int FLAG_CALL_REJECTED = 12;
}
