/**
 * @author tangwq
 */
package com.walktour.control.config;

import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.model.MapSetModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author tangwq
 *
 */
public class MapSetByATTved {
	private String tag = "MapSetByATTved";
	private ArrayList<MapSetModel> layoutPlan;
	
	public MapSetByATTved(String filePath){
		try {
			layoutPlan = new ArrayList<MapSetModel>();
			
			DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = fac.newDocumentBuilder();
			Document doc = db.parse(new File(filePath));
			NodeList buildings = doc.getElementsByTagName("Building");
			if(buildings  != null){
				for(int j = 0;j<buildings.getLength();j++){
					Node buildNode = buildings.item(j);
					if(buildNode.getNodeType() == Node.ELEMENT_NODE){
						String buildingName = buildNode.getAttributes().getNamedItem("Name").getNodeValue();
						LogUtil.w(tag,"---building name:"+buildingName);
						Element buildEle = (Element)buildNode;
						
						String dir = filePath.substring(0, filePath.lastIndexOf("/")) + "/" + buildingName;
						File buildingDir = new File(dir);
						if(!buildingDir.exists()){
						    buildingDir.mkdir();
						}
                        UtilsMethod.copyFile(new File(filePath), new File(dir + "/mapset.xml"));
						
						NodeList nodelist = buildEle.getElementsByTagName("LayoutPlan");
						if(nodelist != null){
							for(int i = 0;i<nodelist.getLength();i++){
								Node node = nodelist.item(i);
								if(node.getNodeType() == Node.ELEMENT_NODE){
									NamedNodeMap nodemap = node.getAttributes();
									MapSetModel model =  new MapSetModel();
									
									model.setBuildingName(buildingName);
									model.setName(nodemap.getNamedItem("Name").getNodeValue());
									model.setGuid(nodemap.getNamedItem("GUID").getNodeValue());
									
									Element el = (Element)node;
									model.setImage(el.getElementsByTagName("Image").item(0).getFirstChild() != null
											? el.getElementsByTagName("Image").item(0).getFirstChild().getNodeValue().trim() : "");
									model.setTabFile(el.getElementsByTagName("TabFile").item(0).getFirstChild() != null
											? el.getElementsByTagName("TabFile").item(0).getFirstChild().getNodeValue().trim() : "");
									model.setTransmitterFile(el.getElementsByTagName("TransmitterFile").item(0).getFirstChild() != null 
											? el.getElementsByTagName("TransmitterFile").item(0).getFirstChild().getNodeValue().trim() : "");
									model.setHeight(Float.parseFloat(el.getElementsByTagName("Height").item(0).getFirstChild() != null
											? el.getElementsByTagName("Height").item(0).getFirstChild().getNodeValue().trim() : "0"));
									LogUtil.w(tag,"---name:"+model.getName()+"---guid:"+model.getGuid());
									layoutPlan.add(model);
								}
							}
						}
					}
				}
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LogUtil.w(tag,"----"+e.getMessage());
		}
	}
	
	public void renameGuidToName(String rootDirected){
		if(layoutPlan != null && !layoutPlan.isEmpty()){
			try{
				for(int i = 0;i< layoutPlan.size();i++){
					String imagePath = layoutPlan.get(i).getImage();
					if(imagePath.trim().length() > 0){
						imagePath = imagePath.replaceAll("\\\\", "/");
						String oldDirName = imagePath.substring(0, imagePath.lastIndexOf("/"));
						/*LogUtil.w(tag,"----"+rootDirected + "--imaget:"+imagePath+"---sub:"+subName+"---"+subName
								+"---sub2:"+subName.substring(0,subName.lastIndexOf("/")+1)+layoutPlan.get(i).getName());*/
						String newDirName = layoutPlan.get(i).getBuildingName()
								+ oldDirName.substring(oldDirName.indexOf("/"),oldDirName.lastIndexOf("/")+1)+layoutPlan.get(i).getName();
						UtilsMethod.FileReName(rootDirected, oldDirName,newDirName);
						
						LogUtil.w(tag,"---new:"+newDirName+"---old:"+oldDirName);
						
						//UtilsMethod.FileReName(rootDirected, newDirName, new2DirName);
					}
				}
				File mapdata = new File(rootDirected+"mapdata");
				mapdata.delete();
			}catch(Exception e){
				e.printStackTrace();
				LogUtil.w(tag,"----"+e.getMessage());
			}
		}
	}
}
