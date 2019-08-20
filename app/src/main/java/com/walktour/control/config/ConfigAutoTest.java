package com.walktour.control.config;

import android.annotation.SuppressLint;

import com.walktour.base.util.AppFilePathUtil;
import com.walktour.control.bean.MyXMLWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Calendar;

@SuppressLint("SdCardPath")
public class ConfigAutoTest {
	
	private MyXMLWriter writer ;
	//private Element element = reader.getElementRoot("fleet");
	private Document doc;
//	private Context mContext;
	
	public ConfigAutoTest(){
		writer = new MyXMLWriter(AppFilePathUtil.getInstance().getAppConfigFile( "config_fleet.xml"));
		doc = writer.getDocument();
	}
	
	
	public boolean isAutoTestOn(){
		//return reader.getElementNodeValue(element, "test_switch").equals("1")?true:false ;	
		return doc.getElementsByTagName("test_switch").item(0).getAttributes().getNamedItem("value")
						.getNodeValue().equals("1")?true:false;
	}
	
	public void setAutoOpen(boolean isOpen){
		Node node = doc.getDocumentElement().getElementsByTagName("test_switch").item(0);
		node.getAttributes().getNamedItem("value").setNodeValue(isOpen?"1":"0");
		writer.writeToFile(doc);
	}
	
	public String getIp(){
		return doc.getElementsByTagName("ip").item(0).getAttributes().getNamedItem("value").getNodeValue();
	}
	
	public void setIp(String ip){
		Node node = doc.getDocumentElement().getElementsByTagName("ip").item(0);
		node.getAttributes().getNamedItem("value").setNodeValue(ip);
		writer.writeToFile(doc);
	}
	
	public String getPort(){
		return 	doc.getElementsByTagName("port").item(0).getAttributes().getNamedItem("value").getNodeValue();
	}
	
	public void setPort(String port){
		Node node = doc.getDocumentElement().getElementsByTagName("port").item(0);
		node.getAttributes().getNamedItem("value").setNodeValue(port);
		writer.writeToFile(doc);
	}
	
//	public String getAutoTime(){
//		return doc.getElementsByTagName("auto_time").item(0).getAttributes().getNamedItem("value").getNodeValue();
//	}
	
	/**
	 * 获取自动测试的Linux时间(1970年开始)
	 * 如果当前时间超过了设定的
	 * */
//	public long getAutoTimeInMillon(){
//		final long Hour = 1000*60*60;
//		
//		String time = getAutoTime();
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf( time.split(":")[0] ) );
//		calendar.set(Calendar.MINUTE, Integer.valueOf( time.split(":")[1] ) );
//		calendar.set(Calendar.SECOND, 0);
//		calendar.set(Calendar.MILLISECOND, 000 );
//		long begin = calendar.getTimeInMillis();
//		long now = Calendar.getInstance().getTimeInMillis();
//		begin =  ( begin < now )? (begin+24*Hour) : begin;
//		
//		return begin;
//	}
	
//	public Date getAutoTestDate(){
//		String time =  doc.getElementsByTagName("auto_time").item(0).getAttributes().getNamedItem("value").getNodeValue();
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt( time.split(":")[0]));
//		calendar.set(Calendar.MINUTE, Integer.parseInt( time.split(":")[1]) );
//		return calendar.getTime();
//	}
	
//	public void setAutoTime(String time){
//		Node node = doc.getDocumentElement().getElementsByTagName("auto_time").item(0);
//		node.getAttributes().getNamedItem("value").setNodeValue(time);
//		writer.writeToFile(doc);
//	}
	
	
//	public boolean isAutoUpload(){
//		return doc.getElementsByTagName("ul_switch").item(0).getAttributes().getNamedItem("value")
//						.getNodeValue().equals("1")?true:false;
//	}
	
//	public void setAutoUpload(boolean isOpen){
//		Node node = doc.getDocumentElement().getElementsByTagName("ul_switch").item(0);
//		node.getAttributes().getNamedItem("value").setNodeValue(isOpen?"1":"0");
//		writer.writeToFile(doc);
//	}
	
//	public void setIMEI(String IMEI){
//		Node node = doc.getDocumentElement().getElementsByTagName("imei").item(0);
//		node.getAttributes().getNamedItem("value").setNodeValue(IMEI);
//		writer.writeToFile(doc);
//	}
	
//	public String getIMEI(){
//		return doc.getElementsByTagName("imei").item(0).getAttributes().getNamedItem("value")
//		.getNodeValue();
//	}
	
	/**
	 * 是否在空闲时间定位
	 * */
	public boolean isLocationOn(){
		return doc.getElementsByTagName("location").item(0).getAttributes().getNamedItem("value")
		.getNodeValue().equals("1")?true:false;
	}
	
	/**
	 * 设置是否在空闲时间定位
	 * */
	public void setLocationOn(boolean locationOn ){
		Node node = doc.getDocumentElement().getElementsByTagName("location").item(0);
		node.getAttributes().getNamedItem("value").setNodeValue(locationOn?"1":"0");
		writer.writeToFile(doc);
	}
	
	/**
	 * 定位开始时间(HH:mm)
	 * */
//	public void setLocationStartTime(String time){
//		Node node = doc.getDocumentElement().getElementsByTagName("location_start").item(0);
//		node.getAttributes().getNamedItem("value").setNodeValue( time );
//		writer.writeToFile(doc);
//	}
	
	/**
	 * 定位开始时间
	 * */
	private String getLocationStartTime(){
		return doc.getElementsByTagName("location_start").item(0).getAttributes().getNamedItem("value")
		.getNodeValue();
	}
	
	/**
	 * 定位开始时间
	 * */
	public long getLocationStartTimeMill(){
		String startTime = getLocationStartTime();
		Calendar calendar = Calendar.getInstance();
		String hour = startTime.split(":")[0];
		String minute = startTime.split(":")[1];
		calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf( hour )  );
		calendar.set(Calendar.MINUTE, Integer.valueOf(minute) );
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 000 );
		return calendar.getTimeInMillis() ;
	}
	
	/**
	 * 定位结束时间(HH:mm)
	 * */
//	public void setLocationEndTime(String time){
//		Node node = doc.getDocumentElement().getElementsByTagName("location_end").item(0);
//		node.getAttributes().getNamedItem("value").setNodeValue( time );
//		writer.writeToFile(doc);
//	}
	
	/**
	 * 定位结束时间
	 * */
	public long getLocationEndTimeMill(){
		String endTime = getLocationEndTime();
		Calendar calendar = Calendar.getInstance();
		String hour = endTime.split(":")[0];
		String minute = endTime.split(":")[1];
		calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf( hour )  );
		calendar.set(Calendar.MINUTE, Integer.valueOf(minute) );
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 000 );
		return calendar.getTimeInMillis() ;
	}
	
	/**
	 * 定位结束时间
	 * */
	private String getLocationEndTime(){
		return doc.getElementsByTagName("location_end").item(0).getAttributes().getNamedItem("value")
		.getNodeValue();
	}
	
}