package com.walktour.control.config;

import com.walktour.base.util.AppFilePathUtil;
import com.walktour.control.bean.MyXMLWriter;
import com.walktour.model.UmpcEnvModel;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;

public class ConfigUmpc {
	private MyXMLWriter writer ;
	private Document doc;
	//private Element element = reader.getElementRoot("umpcconfig");
	private UmpcEnvModel umpcModel = null;
	private static ConfigUmpc configUmpc = null;
	
	/**
	 * 获取ConfigUmpc单例
	 * @return
	 */
	public synchronized static ConfigUmpc getInstance(){
		if(configUmpc == null){
			configUmpc = new ConfigUmpc();
		}
		return configUmpc;
	}
	
	public UmpcEnvModel getUmpcModel(){
		return umpcModel;
	}
	
	private ConfigUmpc(){
		writer = new MyXMLWriter(AppFilePathUtil.getInstance().getAppConfigFile( "config_umpc.xml"));
		doc = writer.getDocument();
		readUmpcFromXml();
	}
	
	private void readUmpcFromXml(){
		umpcModel = new UmpcEnvModel();
		umpcModel.setAutoStart(doc.getElementsByTagName("autostart").item(0).getAttributes().getNamedItem("value").getNodeValue().equals("0"));
		umpcModel.setServerIp(doc.getElementsByTagName("serverip").item(0).getAttributes().getNamedItem("value").getNodeValue());
		umpcModel.setServerPort(Integer.parseInt(doc.getElementsByTagName("serverport").item(0).getAttributes().getNamedItem("value").getNodeValue()));
		umpcModel.setConnectTime(Integer.parseInt(doc.getElementsByTagName("connecttime").item(0).getAttributes().getNamedItem("value").getNodeValue()));
		
		umpcModel.setMobileName(doc.getElementsByTagName("mobilename").item(0).getAttributes().getNamedItem("value").getNodeValue());
		umpcModel.setMobilePassword(doc.getElementsByTagName("mobilepassword").item(0).getAttributes().getNamedItem("value").getNodeValue());
		
		umpcModel.setWifiName(doc.getElementsByTagName("wifiname").item(0).getAttributes().getNamedItem("value").getNodeValue());
		umpcModel.setWifiPassword(doc.getElementsByTagName("wifipassord").item(0).getAttributes().getNamedItem("value").getNodeValue());
		umpcModel.setWifiCiphermode(Integer.parseInt(doc.getElementsByTagName("wificiphermode").item(0).getAttributes().getNamedItem("value").getNodeValue()));
	}
	
	public void writeToFile(){
		Document doc = writer.getDocument();
		Node node = doc.getDocumentElement().getElementsByTagName("autostart").item(0);
		node.getAttributes().getNamedItem("value").setNodeValue(umpcModel.isAutoStart() ? "0" : "1");
		node = doc.getDocumentElement().getElementsByTagName("serverip").item(0);
		node.getAttributes().getNamedItem("value").setNodeValue(umpcModel.getServerIp());
		node = doc.getDocumentElement().getElementsByTagName("serverport").item(0);
		node.getAttributes().getNamedItem("value").setNodeValue(String.valueOf(umpcModel.getServerPort()));
		node = doc.getDocumentElement().getElementsByTagName("connecttime").item(0);
		node.getAttributes().getNamedItem("value").setNodeValue(String.valueOf(umpcModel.getConnectTime()));
		node = doc.getDocumentElement().getElementsByTagName("mobilename").item(0);
		node.getAttributes().getNamedItem("value").setNodeValue(umpcModel.getMobileName());
		node = doc.getDocumentElement().getElementsByTagName("mobilepassword").item(0);
		node.getAttributes().getNamedItem("value").setNodeValue(umpcModel.getMobilePassword());
		node = doc.getDocumentElement().getElementsByTagName("wifiname").item(0);
		node.getAttributes().getNamedItem("value").setNodeValue(umpcModel.getWifiName());
		node = doc.getDocumentElement().getElementsByTagName("wifipassord").item(0);
		node.getAttributes().getNamedItem("value").setNodeValue(umpcModel.getWifiPassword());
		
		node = doc.getDocumentElement().getElementsByTagName("wificiphermode").item(0);
		node.getAttributes().getNamedItem("value").setNodeValue(String.valueOf(umpcModel.getWifiCiphermode()));
		writer.writeToFile(doc);
	}
	
	/**
	 * 根据加密方式将绑定WIFP的文件写入/data/local目录下面,wpa.conf
	 * @param cipherType
	 */
	public void writeCipherToLocal(int cipherType){
		MyXMLWriter writerWap = new MyXMLWriter(new File("/data/local/wpa.conf"));
		StringBuilder sb = new StringBuilder();
		sb.append("ctrl_interface=/data/local/tmp/wpa_supplicant\n");
		sb.append("update_config=1\n\n");
		sb.append("network={\n");
		
		if(cipherType == 2 || cipherType == 3){	//2:WEP(ASCII);3WEP(HEX)
			sb.append("\tssid=\"").append(umpcModel.getWifiName()).append("\"\n");
			sb.append("\tkey_mgmt=NONE\n");
			sb.append("\twep_key0=").append(cipherType == 2 ? "\"" : "").append(umpcModel.getWifiPassword()).append(cipherType == 2 ? "\"" : "").append("\n");
			sb.append("\twep_tx_keyidx=0\n");
		}else{	//WPA(TKIP)/WPA2(AES)
			sb.append("\tssid=\"").append(umpcModel.getWifiName()).append("\"\n");
			sb.append("\tpsk=\"").append(umpcModel.getWifiPassword()).append("\"\n");
			sb.append("\tpriority=44\n");
		}
		sb.append("}\n");
		writerWap.writeToFile( sb.toString() );
	}
}
