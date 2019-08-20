package com.walktour.control.config;

import android.content.Context;

import com.walktour.base.util.LogUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/*****************************************
 * 从XML文件中读取配置参数
 * 
 * @param stream
 *          :传入xml文件格式的数据流.
 **************************************/
public class Config {

	private static String sFilePath;
	// private static String str_xml;
	private static File sFile;
	// private static Context context ;

	// 以DOM方式读取XML文件
	DocumentBuilderFactory docBuilderFactory = null;
	DocumentBuilder docBuilder = null;
	Document doc = null;
	Element root, elment;
	NodeList nodeList, items;

	/**
	 * 
	 * @param sFilePath
	 *          文件的绝对路径,配置文件通常放在/data/data/package_name/files目录下
	 */

	public Config(Context context) {
		// this.context = context;
		sFilePath = context.getFilesDir() + "/config/config.xml";
		sFile = new File(sFilePath);
		try {
			FileInputStream fileInStream = new FileInputStream(sFile);

			// 把数据流转换成DOM对象
			try {
				docBuilderFactory = DocumentBuilderFactory.newInstance();
				docBuilder = docBuilderFactory.newDocumentBuilder();
				// 从xml文件生成Document doc,xml file位于 assets目录下
				doc = docBuilder.parse(fileInStream);

				// 生成根节点
				root = doc.getDocumentElement();
			} catch (IOException e) {
			} catch (ParserConfigurationException e) {
			} catch (SAXException e) {
				e.printStackTrace();
				LogUtil.v("XML---------->", e.toString());
			} finally {
				doc = null;
				docBuilder = null;
				docBuilderFactory = null;

			} // end try catch finally
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			LogUtil.v("File------------>", e.toString());
		}
	}

	///////////////////////////////////////////////////////////////
	/**
	 * @param tagName
	 *          XML配置文件中标签名，标签必须是item的父节点
	 * @param position
	 *          指定第几项item
	 * @return 标签下指定item项
	 */////////////////////////////////////////////////////////////
	public String getItemString(String tagName, int position) {
		// 从根结点指定标签生成列表
		this.nodeList = root.getElementsByTagName(tagName);

		// 仅有的一个节点强制转换成Element类型
		this.elment = (Element) nodeList.item(0);

		// 把elment下所有的item生成列表
		this.items = elment.getElementsByTagName("item");
		return items.item(position).getFirstChild().getNodeValue();
	}

	///////////////////////////////////////////////////////////////
	/**
	 * @param tagName
	 *          XML配置文件中标签名，标签必须是item的父节点
	 * @param item_name
	 *          指定item
	 * @return 标签下指定item项
	 */////////////////////////////////////////////////////////////
	public String getItemString(String tagName, String item_name) {
		String result = null;

		// 从根结点指定标签生成列表
		this.nodeList = root.getElementsByTagName(tagName);

		// 仅有的一个节点强制转换成Element类型
		this.elment = (Element) nodeList.item(0);

		// 把elment下所有的item生成列表
		this.items = elment.getElementsByTagName("item");
		Node node;
		for (int i = 0; i < items.getLength(); i++) {
			node = items.item(i);
			if (node.getAttributes().getNamedItem("name").getNodeValue().equals(item_name)) {
				result = node.getFirstChild().getNodeValue();
			}
		}
		return result;
	}

	//////////////////////////////////////////////////////////////////
	/**
	 * 
	 * @param tagName
	 *          XML配置文件中标签名，标签必须是item的父节点
	 * @return 返回所有item项的字符串
	 *////////////////////////////////////////////////////////////////
	public String[] getItemStringArray(String tagName) {
		// 从根结点指定标签生成列表
		this.nodeList = root.getElementsByTagName(tagName);

		// 仅有的一个节点强制转换成Element类型
		this.elment = (Element) nodeList.item(0);

		// 把elment下所有的item生成列表
		this.items = elment.getElementsByTagName("item");

		ArrayList<String> arrayList = new ArrayList<String>();
		for (int i = 0; i < items.getLength(); i++) {
			arrayList.add(items.item(i).getFirstChild().getNodeValue());
		}

		String strArray[] = new String[arrayList.size()];
		arrayList.toArray(strArray);
		return strArray;
	}

	//////////////////////////////////////////////////////////////////
	/**
	 * 
	 * @param tagName
	 *          XML配置文件中标签名，标签必须是item的父节点
	 * @return 返回所有item项的布尔型,item的值非0时返回true
	 *////////////////////////////////////////////////////////////////
	public boolean[] getItemBooleanArray(String tagName) {
		// 从根结点指定标签生成列表
		this.nodeList = root.getElementsByTagName(tagName);

		// 仅有的一个节点强制转换成Element类型
		this.elment = (Element) nodeList.item(0);

		// 把elment下所有的item生成列表
		this.items = elment.getElementsByTagName("item");

		ArrayList<String> arrayList = new ArrayList<String>();
		for (int i = 0; i < items.getLength(); i++) {
			arrayList.add(items.item(i).getFirstChild().getNodeValue());
		}

		String strArray[] = new String[arrayList.size()];
		arrayList.toArray(strArray);

		boolean[] result = new boolean[strArray.length];
		for (int i = 0; i < strArray.length; i++) {
			result[i] = strArray[i].equals("0") ? false : true;
		}
		return result;
	} // end method getItemBooleanArray

	//////////////////////////////////////////////////////////////////
	/**
	 * 
	 * @param tagName
	 *          XML配置文件中标签名，标签必须是item的父节点
	 * @return 返回所有item的数目
	 *////////////////////////////////////////////////////////////////
	public int getItemsCount(String tagName) {
		// 从根结点指定标签生成列表
		this.nodeList = root.getElementsByTagName(tagName);

		// 仅有的一个节点强制转换成Element类型
		this.elment = (Element) nodeList.item(0);

		// 把elment下所有的item生成列表
		this.items = elment.getElementsByTagName("item");

		return items.getLength();
	} // end method getItemsCount

	//////////////////////////////////////////////////////////////
	/**
	 * @param tagName
	 *          XML配置文件中标签名，标签必须是item的父节点
	 * @param attriName
	 *          指定要修改的item的name
	 * @param value
	 */
	//////////////////////////////////////////////////////////////
	public boolean setItemValue(String tagName, String attriName, String value) {

		// 读取文件并逐行扫描并修改
		try {
			FileInputStream fileStream = new FileInputStream(sFile);
			BufferedReader bufreader = new BufferedReader(new InputStreamReader(fileStream));
			StringBuffer sb = new StringBuffer();

			String line = null;
			try {
				while ((line = bufreader.readLine()) != null) {
					if (line.contains("<" + tagName + ">")) {
						sb.append(line + "\n");
						while ((line = bufreader.readLine()) != null) {
							if (line.contains(attriName)) {
								line = "<item name=\"" + attriName + "\">" + value + "</item>";
								sb.append(line + "\n");
								break;
							}
							sb.append(line + "\n");
						}
						break;
					}
					sb.append(line + "\n");
				}
				while ((line = bufreader.readLine()) != null) {
					sb.append(line + "\n");

				}
				// 读取完后关闭bufreader
				bufreader.close();
			} catch (IOException e) {
				LogUtil.v("reader-------->", e.toString());
				e.printStackTrace();
			}

			// 把修改后的XML写入文件
			try {
				FileWriter fw = new FileWriter(sFile);
				BufferedWriter writer = new BufferedWriter(fw);
				writer.write(sb.toString());
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			return true;

		} catch (FileNotFoundException e) {
			LogUtil.v("File-------------->", e.toString());
			e.printStackTrace();
			return false;
		} // end try&catch

	}// end method setItemByPostion

	///////////////////////////////////////////////////////////
	/**
	 * @param tagName
	 *          XML配置文件中标签名，标签必须是item的父节点
	 * @param values
	 *          对应各个item的值，true｜false
	 */
	//////////////////////////////////////////////////////////////
	public boolean setItemsByTag(String tagName, boolean[] values) {

		String[] str_value = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			str_value[i] = values[i] ? "1" : "0";
		}

		// 读取文件并逐行扫描并修改
		try {
			FileInputStream fileStream = new FileInputStream(sFile);
			BufferedReader bufreader = new BufferedReader(new InputStreamReader(fileStream));
			StringBuffer sb = new StringBuffer();

			String line = null;
			try {
				while ((line = bufreader.readLine()) != null) {
					if (line.contains("<" + tagName + ">")) {
						sb.append(line + "\n");
						for (int i = 0; (line = bufreader.readLine()) != null; i++) {
							if (line.contains("<item")) {
								line = line.substring(0, line.indexOf("</") - 1) + str_value[i] + "</item>";
								sb.append(line + "\n");
							} else {
								sb.append(line + "\n");
								break;
							}
						}
						break;
					}
					sb.append(line + "\n");
				}
				while ((line = bufreader.readLine()) != null) {
					sb.append(line + "\n");

				}
				// 读取完后关闭bufreader
				bufreader.close();
			} catch (IOException e) {
				LogUtil.v("reader-------->", e.toString());
				e.printStackTrace();
			}

			// 把修改后的XML写入文件
			try {
				FileWriter fw = new FileWriter(sFile);
				BufferedWriter writer = new BufferedWriter(fw);
				writer.write(sb.toString());
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			return true;

		} catch (FileNotFoundException e) {
			LogUtil.v("File-------------->", e.toString());
			e.printStackTrace();
			return false;
		} // end try&catch

	}// end method setItemByPostion

}// end inner class Config