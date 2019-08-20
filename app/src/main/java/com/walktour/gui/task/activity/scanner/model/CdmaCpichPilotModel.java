package com.walktour.gui.task.activity.scanner.model;

import com.walktour.model.BaseStructParseModel;



/**
 * Pilot CMDA CPICH结构返回解析类
 * 
 * @author zhihui.lian
 */
public class CdmaCpichPilotModel extends BaseStructParseModel {
	
	public int byFrequencyType;						//0:CDMA  1:EVDO
	
	public int band;								//频段
	
	public int channel;								//频点
	
	public int sfn;									//System frame Number
	
	public int pn;									//扰码
	
	public float fRSSI;
	
	public float fPeakEcIo;
	
	public float fPeakEc;
	
	public float fEc2Io;							//信干比
	
	public int timeOffset;							//GPS偏移
	
	public int delaySpread;							//传输时延 chip
	
	public float faggEcIo;
	
	public float faggEc;
	
	public float fSIR;
	
	public float fEc;
	
	public float fCINR;
	
	public float fCI;
	

	@Override
	protected void init() {
		// 注意此处put队列顺序一定要与文档结构体字段顺序相同
		this.propMap.put("byFrequencyType", StructType.Int);
		this.propMap.put("band", StructType.Int);
		this.propMap.put("channel", StructType.Int);
		this.propMap.put("sfn", StructType.Int);
		this.propMap.put("pn", StructType.Int);
		this.propMap.put("fRSSI", StructType.Float);
		this.propMap.put("fPeakEcIo", StructType.Float);
		this.propMap.put("fPeakEc", StructType.Float);
		this.propMap.put("fEc2Io", StructType.Float);
		this.propMap.put("timeOffset", StructType.Int);
		this.propMap.put("delaySpread", StructType.Int);
		this.propMap.put("faggEcIo", StructType.Float);
		this.propMap.put("faggEc", StructType.Float);
		this.propMap.put("fSIR", StructType.Float);
		this.propMap.put("fEc", StructType.Float);
		this.propMap.put("fCINR", StructType.Float);
		this.propMap.put("fCI", StructType.Float);
	}


	public int getByFrequencyType() {
		return byFrequencyType;
	}


	public void setByFrequencyType(int byFrequencyType) {
		this.byFrequencyType = byFrequencyType;
	}


	public int getBand() {
		return band;
	}


	public void setBand(int band) {
		this.band = band;
	}


	public int getChannel() {
		return channel;
	}


	public void setChannel(int channel) {
		this.channel = channel;
	}


	public int getSfn() {
		return sfn;
	}


	public void setSfn(int sfn) {
		this.sfn = sfn;
	}


	public int getPn() {
		return pn;
	}


	public void setPn(int pn) {
		this.pn = pn;
	}


	public float getfRSSI() {
		return fRSSI;
	}


	public void setfRSSI(float fRSSI) {
		this.fRSSI = fRSSI;
	}


	public float getfPeakEcIo() {
		return fPeakEcIo;
	}


	public void setfPeakEcIo(float fPeakEcIo) {
		this.fPeakEcIo = fPeakEcIo;
	}


	public float getfPeakEc() {
		return fPeakEc;
	}


	public void setfPeakEc(float fPeakEc) {
		this.fPeakEc = fPeakEc;
	}


	public float getfEc2Io() {
		return fEc2Io;
	}


	public void setfEc2Io(float fEc2Io) {
		this.fEc2Io = fEc2Io;
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


	public float getFaggEcIo() {
		return faggEcIo;
	}


	public void setFaggEcIo(float faggEcIo) {
		this.faggEcIo = faggEcIo;
	}


	public float getFaggEc() {
		return faggEc;
	}


	public void setFaggEc(float faggEc) {
		this.faggEc = faggEc;
	}


	public float getfSIR() {
		return fSIR;
	}


	public void setfSIR(float fSIR) {
		this.fSIR = fSIR;
	}


	public float getfEc() {
		return fEc;
	}


	public void setfEc(float fEc) {
		this.fEc = fEc;
	}


	public float getfCINR() {
		return fCINR;
	}


	public void setfCINR(float fCINR) {
		this.fCINR = fCINR;
	}


	public float getfCI() {
		return fCI;
	}


	public void setfCI(float fCI) {
		this.fCI = fCI;
	}
	
	
	

}
