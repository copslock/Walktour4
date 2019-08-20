package com.walktour.gui.task.parsedata.model.task.pbm;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class Server extends TaskBase{
	private static final long serialVersionUID = 8675460428675258922L;
	@SerializedName("serverIP")
	private String serverIP;

	@SerializedName("isCheck")
	private boolean isCheck=false;

	@SerializedName("serverPort")
	private int serverPort;

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	
	public void parseXml(XmlPullParser parser) throws Exception {
		for(int i=0;i<parser.getAttributeCount();i++){
			String attName=parser.getAttributeName(i);
			if(attName.equals("ServerIP")){
				this.setServerIP(parser.getAttributeValue(i));
			}else if(attName.equals("IsCheck")){
				this.setCheck(stringToBool(parser.getAttributeValue(i)));
			}else if(attName.equals("ServerPort")){
				this.setServerPort(stringToInt(parser.getAttributeValue(i)));
			}
		}
	}
	
	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null,"Server");
		this.writeAttribute(serializer, "ServerIP",this.serverIP);
		this.writeAttribute(serializer, "IsCheck",this.isCheck);
		this.writeAttribute(serializer, "ServerPort",this.serverPort);
		serializer.endTag(null,"Server");
	}
}
