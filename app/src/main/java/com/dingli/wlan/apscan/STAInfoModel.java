package com.dingli.wlan.apscan;

/*
 *  根据airodump输出text格式定义
 * 
 */
public class STAInfoModel {
	public String macAddress;
	public String apAddress; //关联的MAC地址，或者是not associated，此时probe的值就为正在关联的AP
	public String ipaddress;
	public String rssi;
	public String packtes;
	public String lostPackes;
	public String rate;
	public String probe;
	public int channel;
	public int sendBytes;
	public int recvBytes;
	public int lostBytes;
	public String firstSeen;
	public String lastSeen;
}
