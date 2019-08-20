package com.walktour.control.config;

import com.walktour.base.util.AppFilePathUtil;
import com.walktour.control.bean.MyXMLWriter;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class ConfigAlarm {
	
	private MyXMLWriter writer;
	private Document doc;
	
	private static ConfigAlarm sInstance;
	private ConfigAlarm(){
		writer = new MyXMLWriter(AppFilePathUtil.getInstance().getAppConfigFile( "config_alarm.xml"));
		doc = writer.getDocument();
	}
	public synchronized static ConfigAlarm getInstance(){
		if(sInstance ==null){
			sInstance = new ConfigAlarm();
		}
		return sInstance;
	}
	
	public boolean[] getChoiced(){
		ArrayList<String> arrayList = new ArrayList<String>();
		String [] result;
		NodeList node_list  = doc.getElementsByTagName("item");
		for(int i=0;i<node_list.getLength();i++){
			Node node = node_list.item(i);
			arrayList.add(node.getAttributes().getNamedItem("value").getNodeValue() );
		}
		result = new String[arrayList.size()];
		arrayList.toArray(result);
		boolean [] choiced = new boolean[result.length];
		for(int i=0;i<result.length;i++){
			choiced[i] = result[i].equals("1");
		}
		return choiced;
	}//end method getChoiced
	
	public boolean isAlram(String name){
		try {
			NodeList node_list  = doc.getElementsByTagName("item");
			for(int i=0;i<node_list.getLength();i++){
				Node node = node_list.item(i);
				if( node.getAttributes().getNamedItem("name").getNodeValue().equals(name) ){
					return node.getAttributes().getNamedItem("value").getNodeValue().equals("1");
				}
			}
		} catch (DOMException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	public String getThreshold(String name){
		try {
			NodeList node_list  = doc.getElementsByTagName("item");
			for(int i=0;i<node_list.getLength();i++){
				Node node = node_list.item(i);
				if( node.getAttributes().getNamedItem("name").getNodeValue().equals(name) ){
					return node.getAttributes().getNamedItem("threshold").getNodeValue();
				}
			}
		} catch (DOMException e) {
			e.printStackTrace();
			return "";
		}
		return "";
	}
	
	public void setAll(boolean choiced){
		NodeList node_list  = doc.getElementsByTagName("item");
		for(int i=0;i<node_list.getLength();i++){
			Node node = node_list.item(i);
			node.getAttributes().getNamedItem("value").setNodeValue(choiced?"1":"0");
		}
		writer.writeToFile(doc);
	}//end method setAll
	
	public void setAlarm(String name ,boolean choiced){
		NodeList node_list  = doc.getElementsByTagName("item");
		for(int i=0;i<node_list.getLength();i++){
			Node node = node_list.item(i);
			if( node.getAttributes().getNamedItem("name").getNodeValue().equals(name) ){
				node.getAttributes().getNamedItem("value").setNodeValue(choiced?"1":"0");
				break;
			}
		}
		writer.writeToFile(doc);
	}
	
	public void setThreshold(String name ,String threshold){
		NodeList node_list  = doc.getElementsByTagName("item");
		for(int i=0;i<node_list.getLength();i++){
			Node node = node_list.item(i);
			if( node.getAttributes().getNamedItem("name").getNodeValue().equals(name) ){
				node.getAttributes().getNamedItem("threshold").setNodeValue(threshold);
				break;
			}
		}
		writer.writeToFile(doc);
	}
	
}