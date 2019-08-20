package com.walktour.model.struct;

import com.walktour.model.BaseStructParseModel;

public class CDMA1xCellInfo extends BaseStructParseModel {
	public int ActiveSetType;
	public int Frequency;
	public int PN;
	public float RSSI;
	public float RSCP;
	public float EcIo;
	public int Dummy;
	
	
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		this.propMap.put("ActiveSetType", StructType.Int);
		this.propMap.put("Frequency", StructType.Int);
		this.propMap.put("PN", StructType.Int);
		this.propMap.put("RSSI", StructType.Float);
		this.propMap.put("RSCP", StructType.Float);
		this.propMap.put("EcIo", StructType.Float);
		this.propMap.put("Dummy", StructType.Int);
	}
}
