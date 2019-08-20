package com.walktour.control.config;

import android.content.Context;

import com.walktour.base.util.AppFilePathUtil;
import com.walktour.control.bean.MyXMLWriter;
import com.walktour.gui.R;
import com.walktour.model.Business;
import com.walktour.model.GoOrNogoParameter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * GoOrNogo设置
 * @author msi
 *
 */
public class GoOrNogoSetting {
	//从文件读取参数
	private MyXMLWriter writer ;
	private Document doc 		=null;//从文件读取到内存
	private List<Business> businessList = new ArrayList<>();
	private String[] businessNames = null;
	private Business business;
	
	/**
	 * MapProperty类的静态值
	 * */
	private static GoOrNogoSetting sInstance;
	
	public synchronized static GoOrNogoSetting getInstance(Context context){
		if(sInstance ==null){
			sInstance = new GoOrNogoSetting(context);
		}
		return sInstance;
	}
	
	private GoOrNogoSetting(Context context) {
		writer 	= new MyXMLWriter(AppFilePathUtil.getInstance().getAppConfigFile("go_or_nogo_setting.xml"));
		init(context);
	}
	
	/**
	 * 初始化
	 */
	private void init(Context context) {
		doc = writer.getDocument();
		//读取业务列表
		readBusinessList();
		businessNames = context.getResources().getStringArray(R.array.array_all_businesses);
	}
	
	public List<Business> getBusinesses() {
		return businessList;
	}
	public void setBusiness(Business business) {
		this.business = business;
	}
	public Business getBusiness() {
		return this.business;
	}
	
	public String[] getBusinessNames() {
		return businessNames;
	}
	
	/**
	 * 读取业务列表
	 */
	private void readBusinessList() {
		businessList.clear();
		
		NodeList nodeList = doc.getElementsByTagName("Business");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node_bus = nodeList.item(i);
			Element el = (Element) node_bus;
			String name = el.getAttributes().getNamedItem("Name").getNodeValue();
			String key = el.getAttributes().getNamedItem("Key").getNodeValue();
			System.out.println("Name:" + name);
			Business business = new Business();
			business.setName(name);
			business.setKey(key);
			NodeList avaliableList = el.getElementsByTagName("AvaliableSettings");
			NodeList defaultList = el.getElementsByTagName("DefaultSettings");
			//AvaliableSettings
			Node node_avaliable = avaliableList.item(0);
			Element el_avaliable = (Element) node_avaliable;
			NodeList avaliablePar = el_avaliable.getElementsByTagName("Parameter");
			System.out.println("avaliablePar:" + avaliablePar.getLength());
			for (int k = 0; k < avaliablePar.getLength(); k++) {
				GoOrNogoParameter avaliableItem = new GoOrNogoParameter();
				avaliableItem.setName(avaliablePar.item(k).getAttributes().getNamedItem("Name").getNodeValue());
				avaliableItem.setAlias(avaliablePar.item(k).getAttributes().getNamedItem("Alias").getNodeValue());
				avaliableItem.setCondiction(avaliablePar.item(k).getAttributes().getNamedItem("Condiction").getNodeValue());
				business.getAvaliableSettings().add(avaliableItem);
			}
			//DefaultSettings
			Node node_default = defaultList.item(0);
			Element el_default = (Element)node_default;
			NodeList defaultPar = el_default.getElementsByTagName("Parameter");
			System.out.println("defaultPar:" + defaultPar.getLength());
			for (int k = 0; k < defaultPar.getLength(); k++) {
				GoOrNogoParameter defaultItem = new GoOrNogoParameter();
				defaultItem.setName(defaultPar.item(k).getAttributes().getNamedItem("Name").getNodeValue());
				defaultItem.setAlias(defaultPar.item(k).getAttributes().getNamedItem("Alias").getNodeValue());
				defaultItem.setCondiction(defaultPar.item(k).getAttributes().getNamedItem("Condiction").getNodeValue());
				business.getDefaultSettings().add(defaultItem);
			}
			
//			for (int j = 0; j < avaliableList.getLength(); j++) {
//				Node node_avaliable = avaliableList.item(j);
//				Element el_avaliable = (Element) node_avaliable;
//				NodeList avaliablePar = el_avaliable.getElementsByTagName("Parameter");
//				System.out.println("avaliablePar:" + avaliablePar.getLength());
//				for (int k = 0; k < avaliablePar.getLength(); k++) {
//					GoOrNogoParameter avaliableItem = new GoOrNogoParameter();
//					avaliableItem.setName(avaliablePar.item(k).getAttributes().getNamedItem("Name").getNodeValue());
//					avaliableItem.setAlias(avaliablePar.item(k).getAttributes().getNamedItem("Alias").getNodeValue());
//					avaliableItem.setCondiction(avaliablePar.item(k).getAttributes().getNamedItem("Condiction").getNodeValue());
//					business.getAvaliableSettings().add(avaliableItem);
//				}
//			}
			
//			for (int j = 0; j < defaultList.getLength(); j++) {
//				Node node_default = defaultList.item(j);
//				Element el_default = (Element)node_default;
//				NodeList defaultPar = el_default.getElementsByTagName("Parameter");
//				System.out.println("defaultPar:" + defaultPar.getLength());
//				for (int k = 0; k < defaultPar.getLength(); k++) {
//					GoOrNogoParameter defaultItem = new GoOrNogoParameter();
//					defaultItem.setName(defaultPar.item(k).getAttributes().getNamedItem("Name").getNodeValue());
//					defaultItem.setAlias(defaultPar.item(k).getAttributes().getNamedItem("Alias").getNodeValue());
//					defaultItem.setCondiction(defaultPar.item(k).getAttributes().getNamedItem("Condiction").getNodeValue());
//					business.getDefaultSettings().add(defaultItem);
//				}
//			}
			businessList.add(business);
		}
	}
	
	public void saveBusiness() {
		NodeList nodeList = doc.getElementsByTagName("Business");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node_bus = nodeList.item(i);
			if(this.business.getName().equals(node_bus.getAttributes().getNamedItem("Name").getNodeValue())) {
				Element el = (Element) node_bus;
				NodeList defaultList = el.getElementsByTagName("DefaultSettings");
				Node node_default = defaultList.item(0);
				Element el_default = (Element)node_default;
				NodeList defaultPar = el_default.getElementsByTagName("Parameter");
				
//				int newItemCount = this.business.getDefaultSettings().size() - defaultPar.getLength();
//				for (int j = 0; j < newItemCount; j++) {
//					Node newParamsNode = defaultPar.item(0).cloneNode(true);
//					newParamsNode.getAttributes().getNamedItem("Name").setNodeValue(this.business.getDefaultSettings().get(j).getName());
//					newParamsNode.getAttributes().getNamedItem("Alias").setNodeValue(this.business.getDefaultSettings().get(j).getAlias());
//					newParamsNode.getAttributes().getNamedItem("Condiction").setNodeValue(this.business.getDefaultSettings().get(j).getCondiction());
//				}
//				
//				for (int k = 0; k < defaultPar.getLength(); k++) {
//					Node defaultItem = defaultPar.item(k);
//					System.out.println("value:" + this.businessList.get(i).getDefaultSettings().get(k).getCondiction());
//					defaultItem.getAttributes().getNamedItem("Condiction").setNodeValue(this.businessList.get(i).getDefaultSettings().get(k).getCondiction() + "");
//				}
				NodeList avaliableList = el.getElementsByTagName("AvaliableSettings");
				//AvaliableSettings
				Node node_avaliable = avaliableList.item(0);
				Element el_avaliable = (Element) node_avaliable;
				NodeList avaliablePar = el_avaliable.getElementsByTagName("Parameter");
				Node template = avaliablePar.item(0);
				removeAllNode(el_default, defaultPar);
				for (int j = 0; j < this.business.getDefaultSettings().size(); j++) {
					Node newParamsNode = template.cloneNode(true);
					newParamsNode.getAttributes().getNamedItem("Name").setNodeValue(this.business.getDefaultSettings().get(j).getName());
					newParamsNode.getAttributes().getNamedItem("Alias").setNodeValue(this.business.getDefaultSettings().get(j).getAlias());
					newParamsNode.getAttributes().getNamedItem("Condiction").setNodeValue(this.business.getDefaultSettings().get(j).getCondiction());
					el_default.appendChild(newParamsNode);
				}
				writer.writeToFile(doc);//写入文件
			}
		}
		
	}
	
	/**
	 * 删除所有节点
	 * @param element 元素
	 * @param nodeList 节点列表
	 */
	private void removeAllNode(Element element, NodeList nodeList) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			element.removeChild(nodeList.item(i));
		}
	}
	
}
