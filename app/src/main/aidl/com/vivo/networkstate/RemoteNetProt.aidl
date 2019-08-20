package com.vivo.networkstate;
 interface RemoteNetProt {	
	String sendMiscInfo(int phoneId, int commandId, String buffer);
	String getSimLTEIMSI(int phoneId);
	boolean setPreferredNetworkType(int phoneId, int networkType);
	String getSimICCID(int phoneId);
	int getDefaultSubId();
	int getPreferredNetworkType(int phoneId);
	void setVolteOnOff(boolean enabled,int phoneId);
	int getDefaultDataPhoneId();
	boolean lockWcdmaVersion(int version);
	int readWcdmaVersion();
	boolean writeWcdmaTestNV5458(int slot,int value);
	String readWcdmaTestNV5458(int slot);
	void airplaneModeOnoff(boolean flag);
	void makecustomphonecall(String number, String suggestionName , boolean isVideo , int slot);
}
