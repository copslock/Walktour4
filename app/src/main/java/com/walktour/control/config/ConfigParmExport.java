package com.walktour.control.config;


import com.walktour.base.util.AppFilePathUtil;
import com.walktour.control.bean.MyXMLWriter;
import com.walktour.model.ExportParmModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * 导出参数配置操作类
 *
 * @author zhihui.lian
 */
public class ConfigParmExport {

    private MyXMLWriter writer;

    private Document doc = null;//从文件读取到内存

    private ExportParmModel exportParmModel = null;

    private ArrayList<ExportParmModel> exportParmModelS = new ArrayList<ExportParmModel>();

    private static ConfigParmExport configExport = null;

    public ConfigParmExport() {
        writer = new MyXMLWriter(AppFilePathUtil.getInstance().getAppConfigFile("config_export_parm.xml"));
        initialParameter();
    }

    /**
     * 初始化参数
     */
    public void initialParameter() {
        doc = writer.getDocument();

        //读取配置文件
        readXmlFile();
    }


    /**
     * 读取XMl文件
     */
    private void readXmlFile() {
        Element e = doc.getDocumentElement();
        NodeList nodelist = e.getElementsByTagName("par");
        for (int i = 0; i < nodelist.getLength(); i++) {
            exportParmModel = new ExportParmModel();
            String id = nodelist.item(i).getAttributes().getNamedItem("name").getNodeValue();
            String shortName = nodelist.item(i).getAttributes().getNamedItem("shortName").getNodeValue();
            String nettype = nodelist.item(i).getAttributes().getNamedItem("nettype").getNodeValue();
            String checked = nodelist.item(i).getAttributes().getNamedItem("checked").getNodeValue();
            exportParmModel.setEnable(Integer.valueOf(checked));
            exportParmModel.setShowNmae(shortName);
            exportParmModel.setId(id);
            exportParmModel.setNetType(nettype.length() == 0 ? 0 : Integer.valueOf(nettype));
            exportParmModelS.add(exportParmModel);
        }

    }


    public void setExporParmEnable(int position, int enable) {
        //修改内存中的值
        for (int i = 0; i < exportParmModelS.size(); i++) {
            if (i == position) {
                exportParmModelS.get(i).setEnable(enable);
            }
        }
        //修改文件中的值
        NodeList nl = doc.getDocumentElement().getElementsByTagName("par");
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (i == position) {
                node.getAttributes().getNamedItem("checked").setNodeValue(String.valueOf(enable));
                writer.writeToFile(doc);//写入文件
            }
        }
    }


    /**
     * 根据网络类型设置组网络勾选参数
     */

    public void setParmByNetWork(int netWork) {
        if (netWork == 1) {
            return;
        }
        int netWorkChannge = 1;
        switch (netWork) {
            case 0:
                netWorkChannge = 1;
                break;
            case 2:
                netWorkChannge = 2;
                break;
            case 3:
                netWorkChannge = 3;
                break;

            default:
                break;
        }
        NodeList nl = doc.getDocumentElement().getElementsByTagName("par");
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            String netType = node.getAttributes().getNamedItem("nettype").getNodeValue();
            if (!netType.isEmpty()) {
                if (Integer.valueOf(netType) == netWorkChannge) {
                    node.getAttributes().getNamedItem("checked").setNodeValue(1 + "");
                    exportParmModelS.get(i).setEnable(1);
                } else {
                    node.getAttributes().getNamedItem("checked").setNodeValue(0 + "");
                    exportParmModelS.get(i).setEnable(0);
                }
            }
        }
        writer.writeToFile(doc);//写入文件

    }


    /**
     * 获取model队列
     */
    public ArrayList<ExportParmModel> getParmModelList() {
        return exportParmModelS;
    }


    /**
     * @return
     */
    public synchronized static ConfigParmExport getSingleInstance() {
        if (configExport == null) {
            configExport = new ConfigParmExport();
        }
        return configExport;
    }

}