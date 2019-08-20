package com.walktour.control.config;


import com.walktour.base.util.AppFilePathUtil;
import com.walktour.control.bean.MyXMLWriter;
import com.walktour.model.KpiSettingModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * kpi配置文件操作类
 * @author zhihui.lian
 *
 */
public  class ConfigKpi {
	
	private MyXMLWriter writer;

	private Document doc 		=null;//从文件读取到内存

	private ArrayList<KpiSettingModel> kpiSettingModels = new ArrayList<>();
	
	private static ConfigKpi configKpi = null;
	
	private ConfigKpi() {
		writer = new MyXMLWriter(AppFilePathUtil.getInstance().getAppConfigFile( "config_totalkey.xml"));
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
	
	
	/**
	 * 读取XMl文件
	 */
	private void readXmlFile() {
		Element e = doc.getDocumentElement();
		NodeList nodelist = e.getElementsByTagName("kpi");
		for (int i = 0; i < nodelist.getLength(); i++) {
			KpiSettingModel kpiSettingModel = new KpiSettingModel();
			String enable = nodelist.item(i).getAttributes().getNamedItem("enable").getNodeValue();
			String kpiKey = nodelist.item(i).getAttributes().getNamedItem("kpiKey").getNodeValue();
			String kpiShowName = nodelist.item(i).getAttributes().getNamedItem("kpiShowName").getNodeValue();
			String operator = nodelist.item(i).getAttributes().getNamedItem("operator").getNodeValue();
			String value = nodelist.item(i).getAttributes().getNamedItem("value").getNodeValue();
			String scale = nodelist.item(i).getAttributes().getNamedItem("scale").getNodeValue();
			String molecule = nodelist.item(i).getAttributes().getNamedItem("molecule").getNodeValue();
			String denominator = nodelist.item(i).getAttributes().getNamedItem("denominator").getNodeValue();
			String units = nodelist.item(i).getAttributes().getNamedItem("units").getNodeValue();
			String groupby = nodelist.item(i).getAttributes().getNamedItem("groupby").getNodeValue();
			kpiSettingModel.setEnable(Integer.valueOf(enable));
			kpiSettingModel.setKpiKey(kpiKey);
			kpiSettingModel.setKpiShowName(kpiShowName);
			kpiSettingModel.setOperator(operator);
			kpiSettingModel.setValue(value.equals("") ? 0 : Float.valueOf(value));
			kpiSettingModel.setScale(Float.valueOf(scale));
			kpiSettingModel.setMolecule(molecule);
			kpiSettingModel.setDenominator(denominator);
			kpiSettingModel.setUnits(units);
			kpiSettingModel.setGroupby(groupby);
			kpiSettingModels.add(kpiSettingModel);
		}
		
	}
	
	
	
	/**
	 * 设置kpi是否有效
	 * */
	public void setKpiEnable(int position,int enable){
		//修改内存中的值
		for (int i = 0; i < kpiSettingModels.size(); i++){
			if (i == position){
				kpiSettingModels.get(i).setEnable(enable);
			} 
		}
		//修改文件中的值
		NodeList nl = doc.getDocumentElement().getElementsByTagName("kpi");
		for(int i=0;i<nl.getLength();i++){
			Node node = nl.item(i);
			KpiSettingModel kpiSettingModel = kpiSettingModels.get(position);
			if( (kpiSettingModel.getKpiShowName() + kpiSettingModel.getGroupby())
					.equals( node.getAttributes().getNamedItem("kpiShowName").getNodeValue() + node.getAttributes().getNamedItem("groupby").getNodeValue() ) ){
				node.getAttributes().getNamedItem("enable").setNodeValue(  String.valueOf(enable));
				writer.writeToFile(doc);//写入文件
				//doc = writer.getDocument();
			}
		}
	}
	
	
	/**
	 * 编辑Item值
	 * */
	public void setKpiEdit(int position ,String formula,String threshold){
		KpiSettingModel kpiSettingModelN;
		for (int i = 0; i < kpiSettingModels.size(); i++){
			if (i == position){
				kpiSettingModelN = kpiSettingModels.get(i);
				kpiSettingModelN.setValue(Float.valueOf(threshold));
				kpiSettingModelN.setOperator(formula);
			} 
		}
		//修改文件中的值
		NodeList nl = doc.getDocumentElement().getElementsByTagName("kpi");
		for(int i=0;i<nl.getLength();i++){
			Node node = nl.item(i);
			KpiSettingModel kpiSettingModel = kpiSettingModels.get(position);
			if( (kpiSettingModel.getKpiShowName() + kpiSettingModel.getGroupby())
					.equals( node.getAttributes().getNamedItem("kpiShowName").getNodeValue() + node.getAttributes().getNamedItem("groupby").getNodeValue() ) ){
				
				node.getAttributes().getNamedItem("operator").setNodeValue(formula);
				node.getAttributes().getNamedItem("value").setNodeValue(threshold);
				writer.writeToFile(doc);//写入文件
				//doc = writer.getDocument();
			}
		}
	}
	
	
	
	
	/**
	 * 获取model队列
	 */
	public ArrayList<KpiSettingModel> getKpiModelList(){
		return kpiSettingModels;
	}
	
	
	
	/**
	 * 获取ConfigKpi单例
	 * @return 唯一实例
	 */
	public synchronized static ConfigKpi getInstance(){
		if(configKpi == null){
			configKpi = new ConfigKpi();
		}
		return configKpi;
	}
		
}