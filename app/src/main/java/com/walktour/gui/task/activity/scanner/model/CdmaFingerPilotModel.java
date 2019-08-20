package com.walktour.gui.task.activity.scanner.model;

import com.walktour.model.BaseStructParseModel;


/**
 * Pilot CMDA Finger结构返回解析类
 * 
 * @author zhihui.lian
 */
public class CdmaFingerPilotModel extends BaseStructParseModel {

	public int byFrequencyType;						//0: CDMA   1:EVDO
	
	public int band;								//频段
	
	public int channel;								//频点
	
	public int sfn;									//System Frame Number
	
	public int pn;									//主扰码
	
	public float fIo;								//信号强度		
	
	public float  fMaxEcIo;							//接收到的所有信号的强度
	
	public float FMaxEc;							//信道质量
	
	public float fMaxSINR;							//信号与干扰加噪声比	
	
	
	@Override
	protected void init() {
		// 注意此处put队列顺序一定要与文档结构体字段顺序相同
		this.propMap.put("byFrequencyType", StructType.Int);
		this.propMap.put("band", StructType.Int);
		this.propMap.put("channel", StructType.Int);
		this.propMap.put("sfn", StructType.Int);
		this.propMap.put("pn", StructType.Int);
		this.propMap.put("fIo", StructType.Float);
		this.propMap.put("fMaxEcIo", StructType.Float);
		this.propMap.put("FMaxEc", StructType.Float);
		this.propMap.put("fMaxSINR", StructType.Float);
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


	public float getfIo() {
		return fIo;
	}


	public void setfIo(float fIo) {
		this.fIo = fIo;
	}


	public float getfMaxEcIo() {
		return fMaxEcIo;
	}


	public void setfMaxEcIo(float fMaxEcIo) {
		this.fMaxEcIo = fMaxEcIo;
	}


	public float getFMaxEc() {
		return FMaxEc;
	}


	public void setFMaxEc(float fMaxEc) {
		FMaxEc = fMaxEc;
	}


	public float getfMaxSINR() {
		return fMaxSINR;
	}


	public void setfMaxSINR(float fMaxSINR) {
		this.fMaxSINR = fMaxSINR;
	}
	
	

}
