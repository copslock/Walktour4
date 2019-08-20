package com.walktour.model;


/**CDMA的Finger信息*/
public class CdmaFinger{
	
	private String pn;
	private String ecIo;
	private String offSet;
	public CdmaFinger(){
		pn = "";
		ecIo = "";
		offSet = "";
	}
	public synchronized String getPn() {
		return pn;
	}
	public synchronized void setPn(String pn) {
		this.pn = pn;
	}
	public synchronized String getEcIo() {
		return ecIo;
	}
	public synchronized void setEcIo(String ecIo) {
		this.ecIo = ecIo;
	}
	public synchronized String getOffSet() {
		return offSet;
	}
	public synchronized void setOffSet(String offSet) {
		this.offSet = offSet;
	}
}