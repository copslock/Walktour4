package com.walktour.control.bean;

import com.walktour.base.util.LogUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 适用的XML结构如config_map.xml
 */

public class MyXMLReader {
	/** 文件对象 */
	private File file;
	/** 文档内容 */
	private Document doc = null;

	public MyXMLReader(String filePath, String fileName) {
		this.file = new File(filePath + fileName);
		this.getDocument();
	}

	public MyXMLReader(InputStream in) {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(in);
			doc.normalize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Document getDocument() {
		if (this.doc != null)
			return this.doc;
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			InputStream in = new FileInputStream(this.file);

			doc = docBuilder.parse(in);
			doc.normalize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 获取根节点
	 * 
	 * @param rootName
	 *          指定XML结构中的根节点名
	 */
	private Element getRootElement(String rootName) {
		try {
			this.getDocument();
			Element root = doc.getDocumentElement();
			NodeList nodeList = root.getElementsByTagName(rootName);
			Element el = (Element) nodeList.item(0);
			return el;
		} catch (Exception e) {
			LogUtil.v("MyXMLReader", e.toString());
			return null;
		}
	}

	/**
	 * @param element
	 *          节点深度为３级的Element
	 * @param tagName
	 * @return 返回element中以tag_name为节点名的NodeList
	 */
	public NodeList getNodeListFromElementByTagName(Element element, String tagName) {
		return element.getElementsByTagName(tagName);
	}

	/**
	 * 获取指定节点的指定名称的下级节点
	 * 
	 * @param parent
	 *          父节点
	 * @param tagName
	 *          节点名称
	 * @return
	 */
	public List<Node> getChildNodesFromNodeByTagName(Node parent, String tagName) {
		NodeList childs = parent.getChildNodes();
		List<Node> list = new ArrayList<Node>();
		for (int i = 0; i < childs.getLength(); i++) {
			Node node = childs.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getNodeName().equals(tagName)) {
					list.add(node);
				}
			}
		}
		return list;
	}

	/**
	 * @param nodeList
	 *          以"节点深度为２级的节点"为子节点的NodeList
	 * @param attr
	 *          node_list的item[i]的第1级的标签属性
	 * @param attrValue
	 *          指定的属性值
	 * @return 从node_list中指定的属性和指定属性值的Node
	 */
	public Node getItemNodeByAttrValue(NodeList nodeList, String attr, String attrValue) {
		Node item_node = null;
		if (nodeList != null) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					if (node.getAttributes().getNamedItem(attr).getNodeValue().equals(attrValue)) {
						item_node = node;
						break;
					}
				}
			}

		} // end if
		return item_node;
	}

	/**
	 * @param itemNode
	 * @param attr
	 *          item_node第1级的标签属性
	 * @return attr属性的属性值
	 */
	public String getItemAttriValue(Node itemNode, String attr) {
		StringBuffer sb = new StringBuffer();
		if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
			sb.append(itemNode.getAttributes().getNamedItem(attr).getNodeValue());
		}
		return sb.toString();
	}

	/**
	 * @param itemNode
	 *          节点深度为２级的节点
	 * @param elment_name
	 *          子节点的名字
	 * @return 子节点值
	 */
	public String getElementNodeValue(Node itemNode, String elementName) {
		StringBuffer sb = new StringBuffer();
		if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
			for (Node el_node = itemNode.getFirstChild(); el_node != null; el_node = el_node.getNextSibling()) {
				if (el_node.getNodeType() == Node.ELEMENT_NODE) {
					if (el_node.getNodeName().equals(elementName)) {
						sb.append(el_node.getFirstChild().getNodeValue());
					}
				}
			}
		}
		return sb.toString();
	}

	/** 调试使用 */
	public String getTestValue() {
		StringBuffer sb = new StringBuffer();

		Element el = this.getRootElement("parameters");
		NodeList node_list = this.getNodeListFromElementByTagName(el, "par");

		Node item_node = this.getItemNodeByAttrValue(node_list, "name", "par1");

		sb.append(this.getItemAttriValue(item_node, "name"));
		sb.append(this.getElementNodeValue(item_node, "th1"));

		return sb.toString();
	}

}
