package com.walktour.gui.locknet;

import java.util.HashMap;
import java.util.Map;

public class LockDataMatch {
	private static Map<String, String> dataMaps = new HashMap<String, String>(); 
	static {
		dataMaps.put("GSM 850", "24.GSM_850");
		dataMaps.put("E-GSM 900", "9.GSM_EGSM_900");
		dataMaps.put("P-GSM 900", "10.GSM_PGSM_900");
		dataMaps.put("GSM 1800", "8.GSM_DCS_1800");
		dataMaps.put("GSM 1900", "26.GSM_PCS_1900");
		dataMaps.put("Band A", "1.TDS_BANDA");
		dataMaps.put("Band F", "6.TDS_BANDF");
		dataMaps.put("Band1", "1.EUTRAN_BAND1");
		dataMaps.put("Band38", "28.EUTRAN_BAND38");
		dataMaps.put("Band39", "29.EUTRAN_BAND39");
		dataMaps.put("Band40", "30.EUTRAN_BAND40");
	}
	
	public static String getKey(String value){
		if(dataMaps.containsKey(value))
			return dataMaps.get(value);
		return "";
	}
}
