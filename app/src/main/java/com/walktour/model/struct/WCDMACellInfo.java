package com.walktour.model.struct;

import com.walktour.model.BaseStructParseModel;

public class WCDMACellInfo extends BaseStructParseModel {
	public int ActiveSetType;
	public int Frequency;
	public int PSC;
	public float RSSI;
	public float EcIo;
	public float RSCP;
	
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		this.propMap.put("ActiveSetType", StructType.Int);
		this.propMap.put("Frequency", StructType.Int);
		this.propMap.put("PSC", StructType.Int);
		this.propMap.put("RSSI", StructType.Float);
		this.propMap.put("EcIo", StructType.Float);
		this.propMap.put("RSCP", StructType.Float);
	}
}
