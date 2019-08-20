package com.walktour.gui.task.activity.scanner.model;

import com.walktour.model.BaseStructParseModel;


/**
 * RSSI返回结果对应类
 * @author zhihui.lian
 *
 */
public class RssiParseModel extends BaseStructParseModel {
	
	
	public long rbw;        //带宽 Hz
	public int bandCode;	//频段标志
	public int channel;		//频点
	public float rssi;		//信号响度
	public int netType;		//网络类型	1---GSM 2---CDMA 3---EVDO 4---WCDMA 5---TDSCDMA 6---FDD_LTE 7---TDD_LTE

	
	
	@Override
	protected void init() {
		//注意此处put队列顺序一定要与文档结构体字段顺序相同
		this.propMap.put("rbw", StructType.Int64);
		this.propMap.put("bandCode", StructType.Int);
		this.propMap.put("channel", StructType.Int);
		this.propMap.put("rssi", StructType.Float);
		this.propMap.put("netType", StructType.Int);
	}

	public long getRbw() {
		return rbw;
	}

	public void setRbw(long rbw) {
		this.rbw = rbw;
	}

	public int getBandCode() {
		return bandCode;
	}

	public void setBandCode(int bandCode) {
		this.bandCode = bandCode;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}


	public float getRssi() {
		return rssi;
	}


	public void setRssi(float rssi) {
		this.rssi = rssi;
	}

	public int getNetType() {
		return netType;
	}

	public void setNetType(int netType) {
		this.netType = netType;
	}

}
