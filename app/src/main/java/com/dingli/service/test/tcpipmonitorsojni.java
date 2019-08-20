package com.dingli.service.test;

import android.os.Handler;
import android.util.Log;

import com.walktour.control.bean.packet_dissect_info;
import com.walktour.control.bean.tcpipmonitorso_start_params;

public class tcpipmonitorsojni {

	private int mTcpipHandle = 0;
	private int ret = 0;
	private Handler mEventHandler;
	private String packet_data;

	// C functions we call
	public native int tcpipmonitorso_init(String process_path, String log_pkg_path);

	public native void tcpipmonitorso_uninit(int handle);

	public native int tcpipmonitorso_start(String param_class, String packet_class, String netd_class, String jni_class, int handle, tcpipmonitorso_start_params t_para);

	public native void tcpipmonitorso_stop(int handle);

	public native int tcpipmonitorso_get_status(int handle);

	public native int tcpipmonitorso_get_packet_count(int handle);
	/**
	 * 获取网络诊断信息总数
	 * handle: init时返回的句柄。
	 * return: 网络诊断信息总数。
	 */
	public native int tcpipmonitorso_get_diagnose_count(int handle);
	/**
	 * 获取网络诊断信息树（json），与tcpipmonitorso_get_diagnose_count配合使用。
	 * handle: init时返回的句柄。
	 * diagnose_tree: 返回网络诊断信息树。
	 * return: 成功 1， 失败 -1。
	 */
	public native int tcpipmonitorso_read_diagnose_tree_info(int handle,network_diagnose_tree diagnose_tree);

	public native int tcpipmonitorso_read_packet_simple_info(int handle,
			int packet_idx, packet_dissect_info packet_info);

	public native int tcpipmonitorso_read_packet_detail_info(int handle,
			int packet_idx, packet_dissect_info packet_info, String packet_data);
	
	static {
		Log.i("tcpipmonitorso", "tcpipmonitorsojni");
		System.loadLibrary("crystax_shared");
		System.loadLibrary("gnustl_shared");	
		System.loadLibrary("miniSDL");	
		System.loadLibrary("ipc2");
		System.loadLibrary("mysock");
		System.loadLibrary("iconv");
		System.loadLibrary("myglib");
		System.loadLibrary("mypcap");
		System.loadLibrary("CustomWireshark");
		System.loadLibrary("CustomIPDecoder");
		System.loadLibrary("tcpipmonitorso");
		System.loadLibrary("tcpipmonitorsojni");
	}

	public tcpipmonitorsojni(Handler eventHandler) {
		mEventHandler = eventHandler;
	}

	public boolean init_tcpip(String logPath,String processPath) {
		mTcpipHandle = tcpipmonitorso_init(processPath,logPath);

		if (mTcpipHandle == 0) {
			Log.e("wireshark", "init_tcpip failed");
			return false;
		}
		Log.i("wireshark", "init ok" + "-------mTcpipHandle:[" + mTcpipHandle + "]");
		return true;
	}

	public void uninit_tcpip() {
		tcpipmonitorso_uninit(mTcpipHandle);
	}

	public boolean tcpip_start(tcpipmonitorso_start_params t_para) {
		ret = tcpipmonitorso_start(
				"com/walktour/control/bean/tcpipmonitorso_start_params",
				"com/walktour/control/bean/packet_dissect_info",
				"com/dingli/service/test/network_diagnose_tree",
				"com/dingli/service/test/tcpipmonitorsojni", mTcpipHandle,
				t_para);

		if (ret == -1)
			return false;

		return true;
	}

	public void tcpip_stop() {
		tcpipmonitorso_stop(mTcpipHandle);
	}

	public int tcpip_get_status() {
		return tcpipmonitorso_get_status(mTcpipHandle);
	}

	public int tcpip_get_packet_count() {
		return tcpipmonitorso_get_packet_count(mTcpipHandle);
	}

	public int tcpip_read_packet_simple_info(int packet_idx,
			packet_dissect_info packet_info) {
		ret = tcpipmonitorso_read_packet_simple_info(mTcpipHandle, packet_idx,
				packet_info);

		if (ret == -1) {
			Log.e("wireshark", "read packet simple info failed");
		}

		return ret;
	}

	public int tcpip_read_packet_detail_info(int packet_idx,
			packet_dissect_info packet_info) {
		ret = tcpipmonitorso_read_packet_detail_info(mTcpipHandle, packet_idx,
				packet_info, packet_data);

		if (ret == -1) {
			Log.e("wireshark", "read packet detail info failed");
		}

		return ret;
	}
}
