package com.walktour.gui.task.activity.scanner.model;

import com.walktour.model.BaseStructParseModel;


/**
 * Pilot CMDA FingerScan结构返回解析类
 * 
 * @author zhihui.lian
 */
public class CdmaFingerScanPilotModel extends BaseStructParseModel {

	public int timeOffSet;
	
	public int delaySpread;
	
	public float fSINR;
	
	public float fEcIo;
	
	public float fEc;
	
	public float fCI;

	
	@Override
	protected void init() {
		// 注意此处put队列顺序一定要与文档结构体字段顺序相同
		this.propMap.put("timeOffSet", StructType.Int);
		this.propMap.put("delaySpread", StructType.Int);
		this.propMap.put("fSINR", StructType.Float);
		this.propMap.put("fEcIo", StructType.Float);
		this.propMap.put("fEc", StructType.Float);
		this.propMap.put("fCI", StructType.Float);
	}


	public int getTimeOffSet() {
		return timeOffSet;
	}


	public void setTimeOffSet(int timeOffSet) {
		this.timeOffSet = timeOffSet;
	}


	public int getDelaySpread() {
		return delaySpread;
	}


	public void setDelaySpread(int delaySpread) {
		this.delaySpread = delaySpread;
	}


	public float getfSINR() {
		return fSINR;
	}


	public void setfSINR(float fSINR) {
		this.fSINR = fSINR;
	}


	public float getfEcIo() {
		return fEcIo;
	}


	public void setfEcIo(float fEcIo) {
		this.fEcIo = fEcIo;
	}


	public float getfEc() {
		return fEc;
	}


	public void setfEc(float fEc) {
		this.fEc = fEc;
	}


	public float getfCI() {
		return fCI;
	}


	public void setfCI(float fCI) {
		this.fCI = fCI;
	}


	
	

}
