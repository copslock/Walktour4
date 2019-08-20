package com.walktour.model.struct;

import com.walktour.model.BaseStructParseModel;

public class EVDOCellInfo extends BaseStructParseModel{
	public int ActiveSetType;
	public int Frequency;
	public int PN;
	public float RSSI;
	public float EcIo;
	public float TotalC2I;
	public int DRCCover;
	public int LinkID;
	public int PilotGroupID;
	public int SchedTag;
	public int Dummy;
	
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		this.propMap.put("ActiveSetType", StructType.Int);
		this.propMap.put("Frequency", StructType.Int);
		this.propMap.put("PN", StructType.Int);
		this.propMap.put("RSSI", StructType.Float);
		this.propMap.put("EcIo", StructType.Float);
		this.propMap.put("TotalC2I", StructType.Float);
		this.propMap.put("DRCCover", StructType.Int);
		this.propMap.put("LinkID", StructType.Int);
		this.propMap.put("PilotGroupID", StructType.Int);
		this.propMap.put("SchedTag", StructType.Int);
		this.propMap.put("Dummy", StructType.Int);
	}
}
