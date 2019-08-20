package com.dingli.wlan.model;

import android.util.Log;

import com.dingli.wlan.apscan.WifiTools;
import com.walktour.Utils.NArchive;

public class WlanModel2 {

	public WifiServiceState m9001;
	public ServiceAP    m9002;
	public APRadioMeasurement m9003;
	public TWiFi_BSSIDList_Param_Ex_Info m9005;
	
	public final static int WifiEnable=0;
	public final static int WifiDisable=1;
	//wifi state
	public final static int WifiState_Idle=0;
	public final static int WifiState_Connected=1;
	public final static int WifiState_NoService=2;
	public final static int WifiState_Unknown=3;
	
	//eEncryptionType
	public final static int EncryptionType_Unknow=0;
	public final static int EncryptionType_Open=1;  //不需要鉴权 
	public final static int EncryptionType_WEP=2;
	public final static int EncryptionType_WPA_1x=3;
	public final static int EncryptionType_WPA_PSK=4;
	public final static int EncryptionType_WPA_PSK_1x=5;
	public final static int EncryptionType_WPA2_1x=6;
	public final static int EncryptionType_WPA2_PSK=7;
	public final static int EncryptionType_WPA2_PSK_1x=8;
	public final static int EncryptionType_WAPI_Cert=9;
	public final static int EncryptionType_WAPI_PSK=10;
	public final static int EncryptionType_WAPI_Cert_PSK=11;
	
//	public static enum eEncryptionType
//	{
//		EncryptionType_Unknow ,
//		EncryptionType_Open,		//不需要鉴权  
//
//		EncryptionType_WEP,				//WEP的鉴权方式
//
//		//WPA的鉴权方式
//		EncryptionType_WPA_1x ,			//802.1x鉴权
//		EncryptionType_WPA_PSK,			//PSK鉴权
//		EncryptionType_WPA_PSK_1x,		//802.1x和PSK两种皆有
//
//		//WPA2的鉴权方式
//		EncryptionType_WPA2_1x,			//802.1x鉴权
//		EncryptionType_WPA2_PSK,		//PSK鉴权
//		EncryptionType_WPA2_PSK_1x,		//802.1x和PSK两种皆有
//
//		//WAPI的鉴权方式
//		EncryptionType_WAPI_Cert,		//证书鉴权
//		EncryptionType_WAPI_PSK,		//共享密钥鉴权
//		EncryptionType_WAPI_Cert_PSK    //证书 共享密钥鉴权
//	};
	//将指定byte数组以16进制的形式打印到控制台
	public  void printHexString( byte[] b,int len) {  
		   StringBuilder builder = new StringBuilder();
		   for (int i = 0; i < len; i++) { 
		     String hex = Integer.toHexString(b[i] & 0xFF); 
		     if (hex.length() == 1) { 
		       hex = '0' + hex; 
		     } 
		     hex+=" ";
		     builder.append(hex);
		   
		   } 
		   Log.d("wlanmodel",builder.toString().toUpperCase() ); 
		 	
		   System.out.println(""); 

		}
	public WlanModel2 () {
		m9001 = new WifiServiceState();
		m9002 = new ServiceAP();
		m9003 = new APRadioMeasurement();
		m9005 = new TWiFi_BSSIDList_Param_Ex_Info();
	}
	private class Header {
		int msglen; //including header and data
		short msgcode;
		long createtime; //utc time
		byte[] getValue() {
			byte[] buffer = new byte[14];
			NArchive ar = NArchive.createFromBuffer(buffer,false);
			ar.writeInt(msglen);
			ar.writeShort(msgcode);
			ar.writeLong(createtime);
			Log.d("wlanmodel","time = " + createtime);
			printHexString(buffer,buffer.length);
			return buffer;
		}
	}
//	public static enum WifiState {
//		Enable,
//		Disable
//	};
//	public static enum ServiceState {
//		idle,
//		connected,
//		no_service,
//		unknown
//		0：Idle（无AP连接）
//		1：Connected（已连接）
//		2:：No Service （无有效信号AP）
//		3：Unknown （未知）

//	}

	//static final String[] SECURITY_MODES = { WEP, WPA, WPA_EAP, IEEE8021X,WPA2};
	public static int getAuthorMode(String capability) {
		String cap = WifiTools.getScanResultSecurity(capability);
		if (cap.equals("WEP")) {
			return  EncryptionType_WEP;
		}
		else if (cap.equals("WPA")) {
			return EncryptionType_WPA_1x;
		}
		else if (cap.equals("WPA_EAP")) {
			return EncryptionType_WPA_PSK;
		}
		else if (cap.equals("IEEE8021X")) {
			return EncryptionType_WPA_1x;
		}
		else if (cap.equals("WPA2")) {
			return EncryptionType_WPA2_1x;
		}
		else 
			return EncryptionType_Open;
	
	}
	class WifiServiceState {
		public Header header;
		public int wlanState; //0：Enable,1：Disable
		public int serviceState;
		public WifiServiceState () {
			header = new Header();
			header.msgcode = (short)0x9001;
			header.msglen = 14+8;
			header.createtime = getCurrentTime();
		}
		public byte[] getValue() {
			byte[] buffer = new byte[header.msglen];
			NArchive ar = NArchive.createFromBuffer(buffer,false);
			ar.writeBytes(header.getValue());
			ar.writeInt(wlanState);
			ar.writeInt(serviceState);
			Log.d("wlan","state="+wlanState + " serviceState=" + serviceState);
			return buffer;
		}
	};
	
	class ServiceAP {
		public Header header;
		//public byte[] macAddr=new byte[6];
		public byte[] bssid = new byte[6];
		public byte[] ssid = new byte[32];
		public byte[] ipaddr = new byte[4];
		public int bandType;
		public double frequency;
		public int channel;
		public int infraMode;
		public double  authMode;
		public ServiceAP() {
			header = new Header();
			header.msglen = 14+6+32+4+4+8+4+4+8;
			header.msgcode = (short)0x9002;
			header.createtime = getCurrentTime();
		}
		public byte[] getValue() {
			byte[] buffer = new byte[header.msglen];
			NArchive ar = NArchive.createFromBuffer(buffer,false);
			ar.writeBytes(header.getValue());
			//ar.writeBytes(macAddr);
			ar.writeBytes(bssid);
			ar.writeBytes(ssid);
			ar.writeBytes(ipaddr);
			ar.writeInt(bandType);
			ar.writeDouble(frequency);
			ar.writeInt(channel);
			ar.writeInt(infraMode);
			ar.writeDouble(authMode);
			Log.d("wlan","mac addr ="+WifiTools.macAddrToString(bssid) + " ssid=" + new String(ssid) 
			+ " ip=" + WifiTools.ipAddrToString(ipaddr) + "freq = " + frequency + " channel = " +channel +
			"inframode = " + infraMode + " authMode = " + authMode);
			return buffer;
		}
	}
	private long getCurrentTime() {
		return System.currentTimeMillis();
//		Date now = new Date();
//		double time = now.getTime() ;
//		return time;
	}
	//service ap radio measurement
	class APRadioMeasurement{
		public Header header;
		public int rssi;
		public int noise;
		public int snr;
		public int sfi;
		public int afi;
		public int linkspeed;
		public APRadioMeasurement() {
			header = new Header();
			header.msgcode = (short)0x9003;
			header.msglen = 14+24;
			header.createtime = getCurrentTime();
		}
		public byte[] getValue() {
			byte[] buffer = new byte[header.msglen];
			NArchive ar = NArchive.createFromBuffer(buffer,false);
			ar.writeBytes(header.getValue());
			ar.writeInt(rssi);
			ar.writeInt(noise);
			ar.writeInt(snr);
			ar.writeInt(sfi);
			ar.writeInt(afi);
			ar.writeInt(linkspeed);
			Log.d("wlan","rssi ="+rssi + " noise=" + noise 
			+ " snr=" + snr + "sfi = " + sfi + " afi = " +afi +
			"linkspeed = " + linkspeed);
			return buffer;
			
		}

	}
//	typedef struct struBSSIDList_Parame_Info
//	{
//	    char   MacAddr[6];
//	char   iSSID[32];
//	int    iPrivacy;
//	int    iRSSI; 
//	int    iNoise;
//	int    iNetWorkTypeInUse;
//	int    iFrequency;     
//	int    iChannel;  
//	char   arrayProtocol[32];
//	int    iRetrys; 
//	}struBSSIDList_Parame_Info, *pStruBSSIDList_Parame_Info;

	public class TWiFi_BSSIDList_Param_Info {
		public byte[] bssid=new byte[6];
		public byte[] SSID = new byte[32];
		public int Privacy;
		public int RSSI;
		public int noise;
		public int NetworkTypeInUse;
		public int FrequencyBand;
		public byte[] arrayProtocol=new byte[32];
		public int channel;
		public int retrys;
		public byte[] getValue() {
			byte[] buffer = new byte[98];
			NArchive ar = NArchive.createFromBuffer(buffer,false);
			ar.writeBytes(bssid);
			ar.writeBytes(SSID);
			ar.writeInt(Privacy);
			ar.writeInt(RSSI);
			ar.writeInt(noise);
			ar.writeInt(NetworkTypeInUse);
			ar.writeInt(FrequencyBand);
			ar.writeInt(channel);
			ar.writeBytes(arrayProtocol);
			ar.writeInt(retrys);
			Log.d("wlan","bssid ="+WifiTools.macAddrToString(bssid) + " SSID=" + new String(SSID) 
					+ " Privacy=" + Privacy + "RSSI = " + RSSI + " noise = " +noise +
					"channel = " + channel + " FrequencyBand =" + FrequencyBand);
			return buffer;
			
		}
	
	};
	//0x9005附属
	public class TWiFi_BSSIDList_Param_Ex_Info {
		public Header header;
		public int  itemsNumber;
		public TWiFi_BSSIDList_Param_Info[] bssidList = new TWiFi_BSSIDList_Param_Info[100];
		public TWiFi_BSSIDList_Param_Ex_Info() {
			header = new Header();
			for (int i=0;i<100;i++) {
				bssidList[i]=new TWiFi_BSSIDList_Param_Info();
			}
					//NumberOfItems = new LongWord(0);
			//BSSIDList = new TWiFi_BSSIDList_Param_Info[NumberOfItems];
			header.msgcode = (short)0x9005; 
		}
		public byte[] getValue() {
			
			header.msglen = 14+4+98*itemsNumber;// bssidList.length;
			Log.d("wlan","bssid list ,length = " + header.msglen);
			header.createtime = getCurrentTime();
			byte[] buffer = new byte[header.msglen];
			NArchive ar = NArchive.createFromBuffer(buffer,false);
			ar.writeBytes(header.getValue());
			ar.writeInt(itemsNumber);
			for (int i=0;i<itemsNumber;i++) {
				ar.writeBytes(bssidList[i].getValue());
			}
			Log.d("wlan","itemsNumber ="+itemsNumber );
			return buffer;
			
		}
	};
}

