package com.dinglicom.data.control;

import com.dinglicom.data.model.Dictionary;
import com.walktour.base.util.LogUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DataDictoryInit {
	private static final String TAG = "DataDictoryInit";
	
	public ArrayList<ArrayList<Dictionary>> initDictory(File file){
		ArrayList<ArrayList<Dictionary>> dataDictory = new ArrayList<>();

		if(file.exists()){
			try{
				DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = fac.newDocumentBuilder();
				Document doc = db.parse(file);
				
				NodeList nodeList = doc.getElementsByTagName("DictionaryType");
				for(int i = 0 ;i < nodeList.getLength(); i++){
					ArrayList<Dictionary> dirArrays = new ArrayList<>();

					Element node_pro =  (Element)nodeList.item(i);
					String dictionary_type = node_pro.getAttributes().getNamedItem("value").getNodeValue();
					
					NodeList list_citys  = node_pro.getElementsByTagName("TypeValues");
					for(int j = 0; j < list_citys.getLength(); j++){
						Dictionary dictor = new Dictionary();
						Element dictoryE = (Element)list_citys.item(j);
						
						dictor.dictionary_type = Integer.parseInt(dictionary_type);
						dictor.type_key = Integer.parseInt(dictoryE.getAttributes().getNamedItem("value").getNodeValue());
						dictor.type_name_en = dictoryE.getAttributes().getNamedItem("enName").getNodeValue();
						dictor.type_name_zh = dictoryE.getAttributes().getNamedItem("zhName").getNodeValue();
						
						LogUtil.w(TAG,"--type:" + dictor.dictionary_type + "--key:" + dictor.type_key + "--zh:" + dictor.type_name_zh);
						dirArrays.add(dictor);
					}
					
					dataDictory.add(dirArrays);
				}
			}catch(Exception e){
				LogUtil.w(TAG,"DataDictoryInit",e);
			}
		}
		
		return dataDictory;
	}
}
