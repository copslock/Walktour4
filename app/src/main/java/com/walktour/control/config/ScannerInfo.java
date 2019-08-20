package com.walktour.control.config;

import com.walktour.Utils.WalkStruct.NetType;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.model.ScannerInfoModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ScannerInfo {

	private static final String tag      = "ScannerInfo";
	private static ScannerInfo instance = null;
	private ArrayList<ScannerInfoModel> scannerLists = new ArrayList<>();
	
	private ScannerInfo(){
		initScannerInfo();
	}
	
	/**
	 * 获得扫频仪配置列表信息
	 * @return 唯一实例
	 */
	public static ScannerInfo getInstance(){
		if(instance == null){
			instance = new ScannerInfo();
		}
		
		return instance;
	}
	
	private void initScannerInfo(){
		try{
			File deviceFile  = AppFilePathUtil.getInstance().getAppConfigFile("config_scanner_info.xml");
			if(deviceFile.exists()){

				DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = fac.newDocumentBuilder();
				Document doc = db.parse(deviceFile);
				Element e = doc.getDocumentElement();
				NodeList nodelist = e.getChildNodes();
				if(nodelist != null){
					for(int i = 0;i<nodelist.getLength();i++){
						Node node = nodelist.item(i);
						if(node.getNodeType() == Node.ELEMENT_NODE){
							ScannerInfoModel scannerInfo = new ScannerInfoModel();
							NamedNodeMap nodemap = node.getAttributes();
							
							
							scannerInfo.setDeviceName(nodemap.getNamedItem("devicename").getNodeValue());
							
							String[] nettypes    = nodemap.getNamedItem("nettypes").getNodeValue().split(",");
							for(String netType : nettypes){
								try{
									NetType net = NetType.valueOf(netType);
									scannerInfo.addNetTypes(net);
								}catch(Exception ee){
									LogUtil.w(tag, "NetType",ee);
								}
							}
							scannerInfo.setModulName(nodemap.getNamedItem("modulename").getTextContent());
							scannerInfo.setChipvendor(Integer.valueOf(nodemap.getNamedItem("chipvendor").getNodeValue(),16));
							scannerInfo.setExpend1(nodemap.getNamedItem("expend1").getTextContent());
							scannerInfo.setExpend2(nodemap.getNamedItem("expend2").getTextContent());
							scannerInfo.setExpend3(nodemap.getNamedItem("expend3").getTextContent());
							
							scannerLists.add(scannerInfo);
						}
					}
				}
			}
		}catch(Exception e){
			LogUtil.w(tag,"",e);
		}
	}
	
	/**
	 * 返回当前支持的所有扫频仪列表
	 * @return 列表
	 */
	public ArrayList<ScannerInfoModel> getScannerList(){
		return scannerLists;
	}
}
