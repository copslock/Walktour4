package com.walktour.gui.task.activity.scanner.model;

import com.walktour.model.BaseStructParseModel;



/**
 * Pilot Wcdma psch结构返回解析类
 * 
 * @author zhihui.lian
 */
public class WcdmaPschPilotModel extends BaseStructParseModel {
	
	public int band;								//0:CDMA  1:EVDO
	
	public int channel;								//频段
	
	public int sfn;									//System frame Number
	
	public int psc;									//主扰码
	
	public float rscp;								//同步接收信号码功率
	
	public float ecio;								//信道质量	
	
	public float ec;
	
	public float io;							//信干比
	
	

	@Override
	protected void init() {
		// 注意此处put队列顺序一定要与文档结构体字段顺序相同
		this.propMap.put("band", StructType.Int);
		this.propMap.put("channel", StructType.Int);
		this.propMap.put("sfn", StructType.Int);
		this.propMap.put("psc", StructType.Int);
		this.propMap.put("rscp", StructType.Float);
		this.propMap.put("ecio", StructType.Float);
		this.propMap.put("ec", StructType.Float);
		this.propMap.put("io", StructType.Float);
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




	public float getRscp() {
		return rscp;
	}




	public void setRscp(float rscp) {
		this.rscp = rscp;
	}




	public float getEcio() {
		return ecio;
	}




	public void setEcio(float ecio) {
		this.ecio = ecio;
	}




	public float getEc() {
		return ec;
	}




	public void setEc(float ec) {
		this.ec = ec;
	}




	public float getIo() {
		return io;
	}




	public void setIo(float io) {
		this.io = io;
	}

	
	
	

}
