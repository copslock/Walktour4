package com.walktour.control.config;

import com.datatests.common.LoggerUtils;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.control.bean.MyXMLWriter;
import com.walktour.model.DataAcquisitionModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;


/**
 *  数据采集配置操作XMl文件
 * @author zhihui.lian
 *
 */
public class ConfigDataAcquisition{
	

	private ArrayList<DataAcquisitionModel> dataAcquisitionModels = new ArrayList<>();

	//XML reader and writer
	private MyXMLWriter writer ;
	private Document doc ;
	
	private ConfigDataAcquisition(){
		writer = new MyXMLWriter(AppFilePathUtil.getInstance().getAppConfigFile("config_acquisition.xml" ));
		doc = writer.getDocument();
		initialParameter();
	}
	

	/**
	 * 初始化参数
	 */
	public void initialParameter(){
		doc = writer.getDocument();
		
		//读取配置文件
		readXmlFile();
	}
	
	
	private static ConfigDataAcquisition sInstance = null;
	public synchronized static ConfigDataAcquisition getInstance(){
		if(sInstance == null){
			sInstance = new ConfigDataAcquisition();
		}
		return sInstance;
	}
	
	private String getValueFromXML(String item_name){
		Element el = (Element) doc.getElementsByTagName("setting").item(0);
		NodeList nl = el.getElementsByTagName("item");
		String result = "N/A";
		for(int i=0;i<nl.getLength();i++){
			Node node = nl.item(i);
			if(node.getAttributes().getNamedItem("name").getNodeValue().equals(item_name) ){
				result = node.getAttributes().getNamedItem("value").getNodeValue();
				break;
			}
		}
		return result;
	}
	
	private void setValueIntoXML(String item_name,String value){
		Element el = (Element) doc.getElementsByTagName("setting").item(0);
		NodeList nl = el.getElementsByTagName("item");
		for(int i=0;i<nl.getLength();i++){
			Node node = nl.item(i);
			if(node.getAttributes().getNamedItem("name").getNodeValue().equals(item_name) ){
				 node.getAttributes().getNamedItem("value").setNodeValue(value);
				 break;
			}
		}
		writer.writeToFile(doc);
	}
	
	
	/**
	 * 读取XMl文件
	 */
	private void readXmlFile() {
		Element e = doc.getDocumentElement();
		NodeList nodelist = e.getElementsByTagName("AcquisitionItem");
		for (int i = 0; i < nodelist.getLength(); i++) {
			DataAcquisitionModel dataAcquisitionModel = new DataAcquisitionModel();
//			System.out.println("==================================");
			String showName = nodelist.item(i).getAttributes().getNamedItem("ShowName").getNodeValue();
			String id = nodelist.item(i).getAttributes().getNamedItem("id").getNodeValue();
			String timeInterval = nodelist.item(i).getAttributes().getNamedItem("timeInterval").getNodeValue();
			LoggerUtils.Log_i("ConfigDataAcquisition","-"+showName+"-"+"-"+id+"-"+"-"+timeInterval+"-");
			dataAcquisitionModel.setId(id);
			dataAcquisitionModel.setShowName(showName);
			dataAcquisitionModel.setTimeInterval(timeInterval);
			dataAcquisitionModels.add(dataAcquisitionModel);
		}
		
	}
	
	
	
	/**
	 * 编辑Item值
	 * */
	public void setTimeEdit(int position ,String time){
		DataAcquisitionModel dataAcquisitionModelN ;
		for (int i = 0; i < dataAcquisitionModels.size(); i++){
			if (i == position){
				dataAcquisitionModelN = dataAcquisitionModels.get(i);
				dataAcquisitionModelN.setTimeInterval(time);
			} 
		}
		//修改文件中的值
		NodeList nl = doc.getDocumentElement().getElementsByTagName("AcquisitionItem");
		for(int i=0;i<nl.getLength();i++){
			Node node = nl.item(i);
			DataAcquisitionModel dataAcquisitionModel = dataAcquisitionModels.get(position);
			if( (dataAcquisitionModel.getId())
					.equals( node.getAttributes().getNamedItem("id").getNodeValue())){
				
				node.getAttributes().getNamedItem("timeInterval").setNodeValue(time);
				writer.writeToFile(doc);//写入文件
			}
		}
	}
	
	
	
	
	
	/**
	 * 获取model队列
	 */
	public ArrayList<DataAcquisitionModel> getDataModelList(){
		return dataAcquisitionModels;
	}
	
	

	public boolean getschInfo(){
		return getValueFromXML("schInfo").equals("1");
	}
	public boolean getRLCPDUInfo(){
		return getValueFromXML("rlcpduinfo").equals("1");
	}
	public boolean getMACPDUInfo(){
		return getValueFromXML("macpduinfo").equals("1");
	}
	public boolean getML1Info(){
		return getValueFromXML("ml1info").equals("1");
	}
	public boolean getPDCPPDUInfo(){
		return getValueFromXML("pdcppduinfo").equals("1");
	}
	public boolean getgrantInfo(){
		return getValueFromXML("grantList").equals("1");
		
	}
	public boolean getmcsStatis(){
		return getValueFromXML("mcsStatis").equals("1");
		
	}
	public boolean getl3msg(){
		return getValueFromXML("l3msg").equals("1");
		
	}
	public boolean getdciInfo(){
		return getValueFromXML("dciInfo").equals("1");
		
	}
	public boolean getrrbInfo(){
		return getValueFromXML("rrbInfo").equals("1");
		
	}
	public boolean getisPara(){
		return getValueFromXML("isParaAcquisition").equals("1");
		
	}
	
	public void setschInfo(String value){
		setValueIntoXML("schInfo", value);
	}
	public void setRLCPDUInfo(String value){
		setValueIntoXML("rlcpduinfo", value);
	}
	public void setMACPDUInfo(String value){
		setValueIntoXML("macpduinfo", value);
	}
	public void setML1Info(String value){
		setValueIntoXML("ml1info", value);
	}
	public void setPDCPPDUInfo(String value){
		setValueIntoXML("pdcppduinfo", value);
	}
	public void setgrantList(String value){
		setValueIntoXML("grantList", value);
	}
	public void setmcsStatis(String value){
		setValueIntoXML("mcsStatis", value);
	}
	public void setl3msg(String value){
		setValueIntoXML("l3msg", value);
	}
	public void setdciInfo(String value){
		setValueIntoXML("dciInfo", value);
	}
	public void setrrbInfo(String value){
		setValueIntoXML("rrbInfo", value);
	}
	public void setisPara(String value){
		setValueIntoXML("isParaAcquisition", value);
	}
}