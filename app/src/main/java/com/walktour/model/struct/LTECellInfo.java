package com.walktour.model.struct;

import com.walktour.model.BaseStructParseModel;

public class LTECellInfo extends BaseStructParseModel {
	
	public int CarrierIndex;
	public int CellType;
	public int EARFCN;
	public int PCI;
	public float RSRP;
	public float RSRP_RX0;
	public float RSRP_RX1;
	public float RSRQ;
	public float RSRQ_RX0;
	public float RSRQ_RX1;
	public float RSSI;
	public float RSSI_RX0;
	public float RSSI_RX1;
	public float SINR;
	public float SINR_RX0;
	public float SINR_RX1;
	
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		this.propMap.put("CarrierIndex", StructType.Int);
		this.propMap.put("CellType", StructType.Int);
		this.propMap.put("EARFCN", StructType.Int);
		this.propMap.put("PCI", StructType.Int);
		this.propMap.put("RSRP", StructType.Float);
		this.propMap.put("RSRP_RX0", StructType.Float);
		this.propMap.put("RSRP_RX1", StructType.Float);
		this.propMap.put("RSRQ", StructType.Float);
		this.propMap.put("RSRQ_RX0", StructType.Float);
		this.propMap.put("RSRQ_RX1", StructType.Float);
		this.propMap.put("RSSI", StructType.Float);
		this.propMap.put("RSSI_RX0", StructType.Float);
		this.propMap.put("RSSI_RX1", StructType.Float);
		this.propMap.put("SINR", StructType.Float);
		this.propMap.put("SINR_RX0", StructType.Float);
		this.propMap.put("SINR_RX1", StructType.Float);
	}
}
