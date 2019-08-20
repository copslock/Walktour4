package com.dinglicom;

import com.dinglicom.QMIServerControl.ControlReturn;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;

/**
 * QMI服务控制工厂类
 * 
 * @author jianchao.wang
 *
 */
public class QMIServerFactory {
	private final static String TAG = "QMIServerFactory";
	/** 唯一实例 */
	private static QMIServerFactory sInstance = null;
	/** 句柄号 */
	private int handle = 0;

	private QMIServerFactory() {
		UtilsMethod.runRootCommand("chmod 777 /dev/socket/qmux_radio");
	}

	/**
	 * 返回唯一实例
	 * 
	 * @return
	 */
	public static QMIServerFactory getInstance() {
		if (sInstance == null)
			sInstance = new QMIServerFactory();
		return sInstance;
	}

	/**
	 * 初始化控制类
	 */
	private void initControl() {
		if (this.handle > 0)
			return;
		this.handle = QMIServerControl.CreateSerControlHandle();
		LogUtil.d(TAG, "---------initControl----handle:" + this.handle + "--------");

	}

	/**
	 * 发送请求消息到服务端
	 * 
	 * @param request
	 *          请求消息
	 * @return 是否成功
	 */
	public boolean sendRequestToServer(String request) {
		if (StringUtil.isNullOrEmpty(request))
			return false;
		this.initControl();
		int flag = QMIServerControl.SendRequestToSer(handle, request.length(), request);
		ControlReturn cr = ControlReturn.get(flag);
		if (cr == ControlReturn.success) {
			LogUtil.d(TAG, "sendRequestToServer Success");
			return true;
		}
		LogUtil.d(TAG, "sendRequestToServer Error:" + cr.getName());
		return false;
	}

	/**
	 * 获取服务端响应的数据
	 * 
	 * @return 数据
	 */
	public String getRespMsgFromServer() {
		this.initControl();
		String resp = QMIServerControl.GetResMsgFromSer(handle);
		return resp;
	}

	/**
	 * 释放控制的句柄
	 */
	public void freeControlHandle() {
		if (this.handle > 0) {
			LogUtil.d(TAG, "---------freeControlHandle------------");
			QMIServerControl.FreeSerControlHandle(handle);
			this.handle = 0;
		}
	}

	/**
	 * 测试方法
	 */
	public void test() {
		boolean flag = this.sendRequestToServer("S3546PbhKJ91N4v1ot9uvUVp9MynexorSjx9snS9ddY=");
		if (flag)
			LogUtil.d(TAG, this.getRespMsgFromServer());
		else
			LogUtil.d(TAG, "sendRequest Error!");
	}
}
