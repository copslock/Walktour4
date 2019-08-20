package com.walktour.control.bean;

import android.util.Xml;

import com.walktour.base.util.LogUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 操作XML文件的类
 */

public class MyXMLWriter {
    /**
     * 日志标识
     */
    private static final String TAG = "MyXMLWriter";
    /**
     * 关联文件
     */
    private File mFile;

    /**
     * @param file xml文件
     */
    public MyXMLWriter(File file) {
        this.mFile = file;
    }

    /**
     * @return xml文件转化成doc
     */
    public Document getDocument() {
        Document document = null;
        InputStream in = null;
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            if (this.mFile.exists()) {
                in = new FileInputStream(this.mFile);
                document = docBuilder.parse(in);
            } else {
                LogUtil.w(TAG, "--newDocument--" + this.mFile.getName());
                document = docBuilder.newDocument();
            }
        } catch (Exception e) {
            LogUtil.w(TAG, "getDocument", e);
        }finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return document;
    }

    /**
     * 把文本内容转成字符串
     *
     * @param doc 文本内容
     * @return 字符串
     */
    public String docToStr(Document doc) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            Element root = doc.getDocumentElement();
            if (root != null) {
                serializer.setOutput(writer);
                serializer.startDocument("UTF-8", true);
                writeNode(root, serializer);
                serializer.endDocument();
                return writer.toString() + "\n";
            }
        } catch (Exception e) {
            LogUtil.w(TAG, "docToStr", e);
        }

        return null;
    }

    /**
     * 保存成文件
     *
     * @param str 字符串
     */
    public void writeToFile(String str) {
        // 把修改后的XML写入文件
        FileWriter fw = null;
        BufferedWriter writer = null;
        try {
            fw = new FileWriter(this.mFile);
            writer = new BufferedWriter(fw);
            writer.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存成文件
     *
     * @param doc 文档
     */
    public void writeToFile(Document doc) {
        // 把修改后的XML写入文件
        String docStr = docToStr(doc);
        if (docStr != null) {
            this.writeToFile(docStr);
        }
    }

    /**
     * 删除子节点
     * @param document 文档对象
     * @param nodeList  以"节点深度为２级的节点"为子节点的NodeList
     * @param attr      nodeList的item[i]的第1级的标签属性
     * @param attrValue 指定的属性值
     */
    public void removeNodeByAttrValue(Document document,NodeList nodeList, String attr, String attrValue) {
        Node itemNode = this.getItemNodeByAttrValue(nodeList, attr, attrValue);
        if (itemNode != null) {
            document.getDocumentElement().removeChild(itemNode);
        }
    }

    /**
     * 获取指定节点
     *
     * @param nodeList  以"节点深度为２级的节点"为子节点的NodeList
     * @param attr      nodeList的item[i]的第1级的标签属性
     * @param attrValue 指定的属性值
     * @return 从nodeList中指定的属性和指定属性值的Node
     */
    public Node getItemNodeByAttrValue(NodeList nodeList, String attr, String attrValue) {
        Node itemNode = null;
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node_item = nodeList.item(i);
                if (node_item.getNodeType() == Node.ELEMENT_NODE) {
                    if (node_item.getAttributes().getNamedItem(attr).getNodeValue().equals(attrValue)) {
                        itemNode = node_item;
                        break;
                    }
                }
            }

        } // end if
        return itemNode;
    }

    /**
     * 写入节点
     *
     * @param node       节点
     * @param serializer 文件流
     */
    private void writeNode(Node node, XmlSerializer serializer) {
        try {
            if (node.getNodeType() == 3) {
                serializer.text(node.getNodeValue());
                return;
            }

            serializer.startTag("", node.getNodeName());
            // 把本node的text写入seriali
            String text = node.getNodeValue();
            if (text != null)
                serializer.text(text);
            // 先把node属性写入
            // =====================================
            NamedNodeMap map = node.getAttributes();
            if (map != null) {
                int attrSize = map.getLength();
                for (int i = 0; i < attrSize; i++) {
                    // 得到的是node的属性节点
                    Node Attrnode = map.item(i);
                    // 写出名字和
                    String name = Attrnode.getNodeName();
                    String value = Attrnode.getNodeValue();
                    serializer.attribute("", name, value);
                }
            }
            // =====================================

            // 再写node的各个子childnode
            // =====================================
            NodeList childs = node.getChildNodes();
            if (childs != null) {
                int nodeSize = childs.getLength();
                for (int i = 0; i < nodeSize; i++) {
                    // 得到的是node的属性节点
                    Node Childnode = childs.item(i);
                    writeNode(Childnode, serializer);
                }
            }
            serializer.endTag("", node.getNodeName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
