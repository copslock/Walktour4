package com.walktour.gui.task.activity.scanner.model;

import com.walktour.model.BaseStructParseModel;

/**
 * ColorCode结果返回解析类
 * 
 * @author zhihui.lian
 */
public class ColorCodeParseModel extends BaseStructParseModel {

	public int channel;				//频点

	public int bsic;				//基站识别码

	public int tfn;					//

	public int band;				//频段标志

	public int lac;					//位置区域码

	public int cellId;				//小区ID

	public int mnc;					//移动网号

	public int mcc;					//移动国家码

	public float ci;				//载干比

	public float rssi;				//信号强度

	@Override
	protected void init() {
		// 注意此处put队列顺序一定要与文档结构体字段顺序相同
		this.propMap.put("channel", StructType.Int);
		this.propMap.put("bsic", StructType.Int);
		this.propMap.put("tfn", StructType.Int);
		this.propMap.put("band", StructType.Int);
		this.propMap.put("lac", StructType.Int);
		this.propMap.put("cellId", StructType.Int);
		this.propMap.put("mnc", StructType.Int);
		this.propMap.put("mcc", StructType.Int);
		this.propMap.put("ci", StructType.Float);
		this.propMap.put("rssi", StructType.Float);
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getBsic() {
		return bsic;
	}

	public void setBsic(int bsic) {
		this.bsic = bsic;
	}

	public int getTfn() {
		return tfn;
	}

	public void setTfn(int tfn) {
		this.tfn = tfn;
	}

	public int getBand() {
		return band;
	}

	public void setBand(int band) {
		this.band = band;
	}

	public int getLac() {
		return lac;
	}

	public void setLac(int lac) {
		this.lac = lac;
	}

	public int getCellId() {
		return cellId;
	}

	public void setCellId(int cellId) {
		this.cellId = cellId;
	}

	public int getMnc() {
		return mnc;
	}

	public void setMnc(int mnc) {
		this.mnc = mnc;
	}

	public int getMcc() {
		return mcc;
	}

	public void setMcc(int mcc) {
		this.mcc = mcc;
	}

	public float getCi() {
		return ci;
	}

	public void setCi(float ci) {
		this.ci = ci;
	}

	public float getRssi() {
		return rssi;
	}

	public void setRssi(float rssi) {
		this.rssi = rssi;
	}
	
	
	
	

}
