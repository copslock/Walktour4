package com.walktour.gui.task.activity.scanner.model;

import com.walktour.model.BaseStructParseModel;



/**
 * Pilot WCMDA CPICH结构返回解析类
 * 
 * @author zhihui.lian
 */
public class WcdmaCpichPilotModel extends BaseStructParseModel {
	
	public int band;
	public int channel;
	public int sfn;
	public int psc;
	public float fRSSI;
	public float fPeakEcIo;
	public float fPeakEc;
	public float fEc2Io;
	public int timeOffset;
	public int delaySpread;
	public int pilotDelay;
	public float faggEcIo;
	public float faggEc;
	public float fSIR;
	public float fRSCP;
	public float fSINR;
	public float fISCP;


	@Override
	protected void init() {
		// 注意此处put队列顺序一定要与文档结构体字段顺序相同
		this.propMap.put("band", StructType.Int);
		this.propMap.put("channel", StructType.Int);
		this.propMap.put("sfn", StructType.Int);
		this.propMap.put("psc", StructType.Int);
		this.propMap.put("fRSSI", StructType.Float);
		this.propMap.put("fPeakEcIo", StructType.Float);
		this.propMap.put("fPeakEc", StructType.Float);
		this.propMap.put("fEc2Io", StructType.Float);
		this.propMap.put("timeOffset", StructType.Int);
		this.propMap.put("delaySpread", StructType.Int);
		this.propMap.put("pilotDelay", StructType.Int);
		this.propMap.put("faggEcIo", StructType.Float);
		this.propMap.put("faggEc", StructType.Float);
		this.propMap.put("fSIR", StructType.Float);
		this.propMap.put("fRSCP", StructType.Float);
		this.propMap.put("fSINR", StructType.Float);
		this.propMap.put("fISCP", StructType.Float);
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


	public int getPsc() {
		return psc;
	}


	public void setPsc(int psc) {
		this.psc = psc;
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


	public int getPilotDelay() {
		return pilotDelay;
	}


	public void setPilotDelay(int pilotDelay) {
		this.pilotDelay = pilotDelay;
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


	public float getfRSCP() {
		return fRSCP;
	}


	public void setfRSCP(float fRSCP) {
		this.fRSCP = fRSCP;
	}


	public float getfSINR() {
		return fSINR;
	}


	public void setfSINR(float fSINR) {
		this.fSINR = fSINR;
	}


	public float getfISCP() {
		return fISCP;
	}


	public void setfISCP(float fISCP) {
		this.fISCP = fISCP;
	}


	

}
