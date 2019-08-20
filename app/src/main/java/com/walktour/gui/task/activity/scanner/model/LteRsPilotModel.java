package com.walktour.gui.task.activity.scanner.model;

import com.walktour.model.BaseStructParseModel;



/**
 * Pilot LTE RS 结构返回解析类
 * 
 * @author zhihui.lian
 */
public class LteRsPilotModel extends BaseStructParseModel {
	
	public int earfcn;					
	
	public int pci;								
	
	public float rssi;
	
	public float rp;
	
	public float rq;
	
	public float cinr;							
	
	public int timeOffset;						
	
	public int delaySpread;						
	
	

	@Override
	protected void init() {
		// 注意此处put队列顺序一定要与文档结构体字段顺序相同
		this.propMap.put("earfcn", StructType.Int);
		this.propMap.put("pci", StructType.Int);
		this.propMap.put("rssi", StructType.Float);
		this.propMap.put("rp", StructType.Float);
		this.propMap.put("rq", StructType.Float);
		this.propMap.put("cinr", StructType.Float);
		this.propMap.put("timeOffset", StructType.Int);
		this.propMap.put("delaySpread", StructType.Int);
	}



	public int getEarfcn() {
		return earfcn;
	}



	public void setEarfcn(int earfcn) {
		this.earfcn = earfcn;
	}



	public int getPci() {
		return pci;
	}



	public void setPci(int pci) {
		this.pci = pci;
	}



	public float getRssi() {
		return rssi;
	}



	public void setRssi(float rssi) {
		this.rssi = rssi;
	}



	public float getRp() {
		return rp;
	}



	public void setRp(float rp) {
		this.rp = rp;
	}



	public float getRq() {
		return rq;
	}



	public void setRq(float rq) {
		this.rq = rq;
	}



	public float getCinr() {
		return cinr;
	}



	public void setCinr(float cinr) {
		this.cinr = cinr;
	}



	public int getTimeOffset() {
		return timeOffset;
	}



	public void setTimeOffset(int timeOffset) {
		this.timeOffset = timeOffset;
	}



	public int getDelaySpread() {
		return delaySpread;
	}



	public void setDelaySpread(int delaySpread) {
		this.delaySpread = delaySpread;
	}



}
