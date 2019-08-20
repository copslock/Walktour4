package com.walktour.service.phoneinfo.model;

public class LTESignalStrength {
	public int strength = -1;
	public int level = -1;
	public int timingadvance = -1;
	public int sinr = -1;
	public int cqi = -1;
	public int rsrq = -1;
	public int rsrp = -1;
	public int rsrp_2g = -1;
	public int strength_2g = -1;

	@Override
	public String toString() {
		return "LteSignalStrength [strength=" + strength + ", level=" + level + ", timingadvance=" + timingadvance
				+ ", snr=" + sinr + ", cqi=" + cqi + ", rsrq=" + rsrq + ", rsrp=" + rsrp + ", rsrp_2g=" + rsrp_2g
				+ ", strength_2g=" + strength_2g + "]";
	}

}
