package com.walktour.gui.task.activity.scanner.model;

import com.walktour.model.BaseStructParseModel;



/**
 * Pilot TDSCDMA结构返回解析类
 * 
 * @author zhihui.lian
 */
public class TdScdmaPilotModel extends BaseStructParseModel {
	
	public int channel;
	public int syncID;
	public int midambleCode;
	public float fPCCPCHRSCP;
	public float fPCCPCHISCP;
	public float fPCCPCHSIR;
	public float fPCCPCHCI;
	public int timing;
	public float fPCCPCHEcIo;
	public float fFrame_rssi_5ms;
	public int delaySpread;
	public float fBLER;
	public float pCCPCH_RSSI;
	public int band;


	@Override
	protected void init() {
		// 注意此处put队列顺序一定要与文档结构体字段顺序相同
		this.propMap.put("channel", StructType.Int);
		this.propMap.put("syncID", StructType.Int);
		this.propMap.put("midambleCode", StructType.Int);
		this.propMap.put("fPCCPCHRSCP", StructType.Float);
		this.propMap.put("fPCCPCHISCP", StructType.Float);
		this.propMap.put("fPCCPCHSIR", StructType.Float);
		this.propMap.put("fPCCPCHCI", StructType.Float);
		this.propMap.put("timing", StructType.Int);
		this.propMap.put("fPCCPCHEcIo", StructType.Float);
		this.propMap.put("fFrame_rssi_5ms", StructType.Float);
		this.propMap.put("delaySpread", StructType.Int);
		this.propMap.put("fBLER", StructType.Float);
		this.propMap.put("pCCPCH_RSSI", StructType.Float);
		this.propMap.put("band", StructType.Int);
	}


	public int getChannel() {
		return channel;
	}


	public void setChannel(int channel) {
		this.channel = channel;
	}


	public int getSyncID() {
		return syncID;
	}


	public void setSyncID(int syncID) {
		this.syncID = syncID;
	}


	public int getMidambleCode() {
		return midambleCode;
	}


	public void setMidambleCode(int midambleCode) {
		this.midambleCode = midambleCode;
	}


	public float getfPCCPCHRSCP() {
		return fPCCPCHRSCP;
	}


	public void setfPCCPCHRSCP(float fPCCPCHRSCP) {
		this.fPCCPCHRSCP = fPCCPCHRSCP;
	}


	public float getfPCCPCHISCP() {
		return fPCCPCHISCP;
	}


	public void setfPCCPCHISCP(float fPCCPCHISCP) {
		this.fPCCPCHISCP = fPCCPCHISCP;
	}


	public float getfPCCPCHSIR() {
		return fPCCPCHSIR;
	}


	public void setfPCCPCHSIR(float fPCCPCHSIR) {
		this.fPCCPCHSIR = fPCCPCHSIR;
	}


	public float getfPCCPCHCI() {
		return fPCCPCHCI;
	}


	public void setfPCCPCHCI(float fPCCPCHCI) {
		this.fPCCPCHCI = fPCCPCHCI;
	}


	public int getTiming() {
		return timing;
	}


	public void setTiming(int timing) {
		this.timing = timing;
	}


	public float getfPCCPCHEcIo() {
		return fPCCPCHEcIo;
	}


	public void setfPCCPCHEcIo(float fPCCPCHEcIo) {
		this.fPCCPCHEcIo = fPCCPCHEcIo;
	}


	public float getfFrame_rssi_5ms() {
		return fFrame_rssi_5ms;
	}


	public void setfFrame_rssi_5ms(float fFrame_rssi_5ms) {
		this.fFrame_rssi_5ms = fFrame_rssi_5ms;
	}


	public int getDelaySpread() {
		return delaySpread;
	}


	public void setDelaySpread(int delaySpread) {
		this.delaySpread = delaySpread;
	}


	public float getfBLER() {
		return fBLER;
	}


	public void setfBLER(float fBLER) {
		this.fBLER = fBLER;
	}


	public float getpCCPCH_RSSI() {
		return pCCPCH_RSSI;
	}


	public void setpCCPCH_RSSI(float pCCPCH_RSSI) {
		this.pCCPCH_RSSI = pCCPCH_RSSI;
	}


	public int getBand() {
		return band;
	}


	public void setBand(int band) {
		this.band = band;
	}



}
