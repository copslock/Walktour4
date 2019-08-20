package com.walktour.control.config;

import android.content.Context;
import android.os.Environment;

import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 该类用于加载放于/sdcard/walktour/info_project_no.xml的配置文件,如果该文件存则加载相关信息
 * 配置文件格式:
<?xml version="1.0" encoding="UTF-8"?>
<root>
	<project value="walktour">
		<No>2.2.10</No>
		<No>2.2.11</No>
		<No>2.2.12</No>
		<No>2.2.13</No>
		<No>2.2.14</No>
	</project>
	
</root>
 * 
 * @author zhihui.lian
 *
 */
public class ConfigProNum {
	
	private static ConfigProNum proNoInfo = null;
	private final String TAG = "ConfigProject";
	private final String proNoFilePath = Environment.getExternalStorageDirectory().getPath() + "/walktour/info_project_no.xml";
	
	private Context mContext = null;
	private ArrayList<String> proNoList = null;
	
	private ConfigProNum(Context context){
		mContext = context;
		proNoList = new ArrayList<String>();
		buildProNoInfo();
	}
	
	public static  synchronized ConfigProNum getInstance(Context context){
		if(proNoInfo == null){
			proNoInfo = new ConfigProNum(context);
		}
		return proNoInfo;
	}
	
	
	public ArrayList<String> getProNoList() {
		return proNoList;
	}

	
	private void buildProNoInfo(){
		File file = new File(proNoFilePath);
		LogUtil.w(TAG, "--proNoFilePath:" + proNoFilePath + "--exist:" + file.exists());
		if(file.exists()){
			try{
				proNoList.clear();
				//添加选择省项
				proNoList.add(mContext.getString(R.string.main_select_proNo));
				
				DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = fac.newDocumentBuilder();
				Document doc = db.parse(file);
				
				NodeList nodeList = doc.getElementsByTagName("project");
				for(int i = 0 ;i < nodeList.getLength(); i++){
					Element node_pro =  (Element)nodeList.item(i);
					NodeList no  = node_pro.getElementsByTagName("No");
					for(int j = 0; j < no.getLength(); j++){
						Node node = no.item(j);
						proNoList.add(node.getFirstChild().getNodeValue());
					}
					
				}
				
			}catch(Exception e){
				LogUtil.w(TAG, "buildProNoInfo",e);
			}
		}
	}
	
}
