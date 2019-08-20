package com.walktour.gui.task.activity.scanner.model;

import com.walktour.model.BaseStructParseModel;

/**
 * Pilot LTE CellInfo 结构返回解析类
 * 
 * @author zhihui.lian
 */
public class LteCellDataPilotModel extends BaseStructParseModel {

	public int frametype;

	public int earfcn;

	public int pci;

	public int band;

	public float bandWidth;

	public int numOfRB;
	
	public int rxAntennaCount;
	
	public int txAntennaCount;
	
	public int timeOffset;
	
	public int dlUlFrameConfig;
	
	public int specialSubFrameConfig;
	
	public int cpType;
	
	public int numOfSymbolsPerSlot;
	
	
	

	@Override
	protected void init() {
		// 注意此处put队列顺序一定要与文档结构体字段顺序相同
		this.propMap.put("frametype", StructType.Int);
		this.propMap.put("earfcn", StructType.Int);
		this.propMap.put("pci", StructType.Int);
		this.propMap.put("band", StructType.Int);
		this.propMap.put("bandWidth", StructType.Float);
		this.propMap.put("numOfRB", StructType.Int);
		this.propMap.put("rxAntennaCount", StructType.Int);
		this.propMap.put("txAntennaCount", StructType.Int);
		this.propMap.put("dlUlFrameConfig", StructType.Int);
		this.propMap.put("specialSubFrameConfig", StructType.Int);
		this.propMap.put("cpType", StructType.Int);
		this.propMap.put("numOfSymbolsPerSlot", StructType.Int);
	}

	public int getFrametype() {
		return frametype;
	}

	public void setFrametype(int frametype) {
		this.frametype = frametype;
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

	public int getBand() {
		return band;
	}

	public void setBand(int band) {
		this.band = band;
	}

	public float getBandWidth() {
		System.out.println("--bandwidth--" + bandWidth);
		return bandWidth;
	}

	public void setBandWidth(float bandWidth) {
		this.bandWidth = bandWidth;
	}

	public int getNumOfRB() {
		return numOfRB;
	}

	public void setNumOfRB(int numOfRB) {
		this.numOfRB = numOfRB;
	}

	public int getRxAntennaCount() {
		return rxAntennaCount;
	}

	public void setRxAntennaCount(int rxAntennaCount) {
		this.rxAntennaCount = rxAntennaCount;
	}

	public int getTxAntennaCount() {
		return txAntennaCount;
	}

	public void setTxAntennaCount(int txAntennaCount) {
		this.txAntennaCount = txAntennaCount;
	}


	public int getDlUlFrameConfig() {
		return dlUlFrameConfig;
	}

	public void setDlUlFrameConfig(int dlUlFrameConfig) {
		this.dlUlFrameConfig = dlUlFrameConfig;
	}

	public int getSpecialSubFrameConfig() {
		return specialSubFrameConfig;
	}

	public void setSpecialSubFrameConfig(int specialSubFrameConfig) {
		this.specialSubFrameConfig = specialSubFrameConfig;
	}

	public int getCpType() {
		return cpType;
	}

	public void setCpType(int cpType) {
		this.cpType = cpType;
	}

	public int getNumOfSymbolsPerSlot() {
		return numOfSymbolsPerSlot;
	}

	public void setNumOfSymbolsPerSlot(int numOfSymbolsPerSlot) {
		this.numOfSymbolsPerSlot = numOfSymbolsPerSlot;
	}

}
