package com.dingli.service.test;

import android.util.Log;

public class ipc2msg {
	public int test_item;
	public int event_id;
	public int tv_sec;
	public int tv_usec;
	public String data;
	public int data_len;
	public String dev;

	/**
	 * 获得实际时间(微秒)
	 * 
	 * @return
	 */
	public long getRealTime() {
		if (this.tv_sec > 0 && this.tv_usec > 0) {
			long tvLong = Long.valueOf(Integer.valueOf(this.tv_sec)) * 1000000 + this.tv_usec; // 处理毫秒运算,去掉以前字符串处理方法
			Log.i("RealTime>>", tvLong + "");
			return tvLong;
		}
		return System.currentTimeMillis() * 1000;
	}

	@Override
	public String toString() {
		return "ipc2msg{" + "test_item=" + test_item + ", event_id=" + event_id + ", tv_sec=" + tv_sec + ", tv_usec="
				+ tv_usec + ", data='" + data + '\'' + ", data_len=" + data_len + ", dev='" + dev + '\'' + '}';
	}
}
