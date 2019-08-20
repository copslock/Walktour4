package com.walktour.control.config;

import android.annotation.SuppressLint;
import android.content.Context;

import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyXMLWriter;
import com.walktour.gui.R;
import com.walktour.model.UrlModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

@SuppressLint("SdCardPath")
public class ConfigUrl {
	// private MyXMLReader reader =null;
	private MyXMLWriter writer = null;
	private Document doc = null;
	// private Element element_root =null;

	public ConfigUrl() {
		writer = new MyXMLWriter(AppFilePathUtil.getInstance().getAppConfigFile("config_url.xml"));
		doc = writer.getDocument();
	}

	/**
	 * 判断url名是否重复
	 */
	public boolean contains(String url_name) {
		boolean result = false;
		String[] url_names = getAllUrlNames();
		for (int i = 0; i < url_names.length; i++) {
			if (url_name.equals(url_names[i])) {
				result = true;
				break;
			}
		}
		return result;
	}

	public void addUrl(UrlModel model) {
		Document doc = writer.getDocument();

		Element elment = doc.createElement("url");
		elment.setAttribute("name", model.getName());
		elment.setAttribute("enble", model.getEnable());

		String[] names = this.getAllUrlNames();
		boolean exit = false;
		// int index = 0;
		for (String x : names) {
			if (x.equals(model.getName())) {
				// if(!ips[index].equals(model.getIp())){
				setUrlName(model.getName(), model.getName());
				setEnble(model.getName(), model.getEnable());
				// }
				exit = true;
			}
			// index++;
		}

		if (!exit) {
			doc.getDocumentElement().appendChild(elment);
			writer.writeToFile(doc);
		}
	}

	public void removeUrl(UrlModel model) {
		Document doc = writer.getDocument();

		Element elment = doc.createElement("url");
		elment.setAttribute("name", model.getName());
		elment.setAttribute("enble", model.getEnable());

		String[] names = this.getAllUrlNames();
		boolean exit = false;
		// int index = 0;
		for (String x : names) {
			if (x.equals(model.getName())) {
				// if(!ips[index].equals(model.getIp())){
				setUrlName(model.getName(), model.getName());
				setEnble(model.getName(), model.getEnable());
				// }
				exit = true;
			}
			// index++;
		}

		if (!exit) {
			doc.getDocumentElement().removeChild(elment);
			writer.writeToFile(doc);
		}
	}

	// public FtpServerModel getFtpServerModel( String name ){
	// return new FtpServerModel(
	// name,
	// getFtpIp( name ),
	// getFtpPort( name ),
	// getFtpUser( name ),
	// getFtpPass( name )
	// );
	// }

	public void addFtp(String name, String enble) {
		Document doc = writer.getDocument();

		Element elment = doc.createElement("url");
		elment.setAttribute("name", name);
		elment.setAttribute("ip", enble);

		doc.getDocumentElement().appendChild(elment);

		writer.writeToFile(doc);
	}

	/**
	 * 
	 * 获取所有的url名字
	 */
	public String[] getAllUrlNames() {
		// 动态数组
		ArrayList<String> arrayList = new ArrayList<String>();
		NodeList node_list = doc.getElementsByTagName("url");
		for (int i = 0; i < node_list.getLength(); i++) {
			Node node = node_list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				arrayList.add(node.getAttributes().getNamedItem("name").getNodeValue());
			}
		}
		// 动态数组转换成字符串数组后返回
		String strArray[] = new String[arrayList.size()];
		arrayList.toArray(strArray);
		return strArray;
	}

	/**
	 * 
	 * 获取所有的url可选属性
	 */
	public String[] getAllUrlEnable() {
		// 动态数组
		ArrayList<String> arrayList = new ArrayList<String>();

		NodeList node_list = doc.getElementsByTagName("url");
		for (int i = 0; i < node_list.getLength(); i++) {
			Node node = node_list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				arrayList.add(node.getAttributes().getNamedItem("enable").getNodeValue());
			}
		}
		// 动态数组转换成字符串数组后返回
		String strArray[] = new String[arrayList.size()];
		arrayList.toArray(strArray);
		return strArray;
	}

	/*
	 * 获取单个url可选属性
	 */
	public String getUrlEnble(String name) {
		StringBuffer sb = new StringBuffer();

		NodeList node_list = doc.getElementsByTagName("url");
		for (int i = 0; i < node_list.getLength(); i++) {
			Node node = node_list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getAttributes().getNamedItem("name").getNodeValue().equals(name)) {
					sb.append(node.getAttributes().getNamedItem("enable").getNodeValue());
				}
			}
		}

		return sb.toString();
	}

	public String[] getUrl_Attrs(String name) {
		String[] sb = new String[2];

		NodeList node_list = doc.getElementsByTagName("url");
		for (int i = 0; i < node_list.getLength(); i++) {
			Node node = node_list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getAttributes().getNamedItem("name").getNodeValue().equals(name)) {
					sb[0] = (node.getAttributes().getNamedItem("name").getNodeValue());
					sb[1] = (node.getAttributes().getNamedItem("enable").getNodeValue());
				}
			}
		}

		return sb;
	}

	/**
	 * 0 url 1 enable
	 * 
	 * @return
	 */
	public String[] getUrl_All(String name) {
		String[] sb = null;

		NodeList node_list = doc.getElementsByTagName("url");
		for (int i = 0; i < node_list.getLength(); i++) {
			Node node = node_list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getAttributes().getNamedItem("name").getNodeValue().equals(name)) {
					sb = new String[2];
					sb[0] = (node.getAttributes().getNamedItem("name").getNodeValue());
					sb[1] = (node.getAttributes().getNamedItem("enable").getNodeValue());
				}
			}
		}

		return sb;
	}

	public int getPositon(String name) {
		int result = -1;
		NodeList node_list = doc.getElementsByTagName("url");
		for (int i = 0; i < node_list.getLength(); i++) {
			Node node = node_list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getAttributes().getNamedItem("name").getNodeValue().equals(name)) {
					result = i;
				}
			}
		}
		return result;
	}

	public String getName(int position) {
		StringBuffer sb = new StringBuffer();
		NodeList node_list = doc.getElementsByTagName("url");
		Node node = node_list.item(position);
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			sb.append(node.getAttributes().getNamedItem("name").getNodeValue());
		}
		return sb.toString();
	}

	public void removeUrl(String name) {
		Document doc = writer.getDocument();

		NodeList nodelist_ftps = doc.getDocumentElement().getElementsByTagName("url");
		writer.removeNodeByAttrValue(doc,nodelist_ftps, "name", name);
		LogUtil.v("After RemoveFtp--->", writer.docToStr(doc));
		writer.writeToFile(doc);
	}

	public void removeUrls(String[] ftpNames) {
		Document doc = writer.getDocument();
		NodeList nodelist_ftps = doc.getDocumentElement().getElementsByTagName("url");
		for (int i = 0; i < ftpNames.length; i++) {
			writer.removeNodeByAttrValue(doc,nodelist_ftps, "name", ftpNames[i]);
		}
		writer.writeToFile(doc);
	}

	public void setUrlName(String ftp_name, String value) {
		Document doc = writer.getDocument();
		NodeList nodelist_ftps = doc.getDocumentElement().getElementsByTagName("url");

		Node node = writer.getItemNodeByAttrValue(nodelist_ftps, "name", ftp_name);
		node.getAttributes().getNamedItem("name").setNodeValue(value);

		writer.writeToFile(doc);
	}

	public void setEnble(String ftp_name, String value) {
		Document doc = writer.getDocument();
		NodeList nodelist_ftps = doc.getDocumentElement().getElementsByTagName("url");

		Node node = writer.getItemNodeByAttrValue(nodelist_ftps, "name", ftp_name);
		node.getAttributes().getNamedItem("enble").setNodeValue(value);

		writer.writeToFile(doc);
	}

	public int getPositonFirstEmpty(String name) {
		int result = getPositon(name);
		if (result >= 0)
			return result + 1;
		return 0;
	}

	public String getNameFirstEmpty(int position, Context context) {
		if (position > 0)
			return getName(position - 1);
		return context.getString(R.string.none);
	}

	public String[] getAllFtpNamesFirstEmpty(Context context) {
		// 动态数组
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add(context.getString(R.string.none));

		NodeList node_list = doc.getElementsByTagName("ftp");
		for (int i = 0; i < node_list.getLength(); i++) {
			Node node = node_list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				arrayList.add(node.getAttributes().getNamedItem("name").getNodeValue());
			}
		}
		// li.bie在arrayList最后添加一个子项"配置"
		arrayList.add(context.getString(R.string.main_indoor_config));
		// 动态数组转换成字符串数组后返回
		String strArray[] = new String[arrayList.size()];
		arrayList.toArray(strArray);
		return strArray;
	}

	public ArrayList<UrlModel> getAllUrl() {
		ArrayList<UrlModel> allUrlModel = new ArrayList<UrlModel>();
		NodeList node_list = doc.getElementsByTagName("url");
		for (int i = 0; i < node_list.getLength(); i++) {
			UrlModel urlModel = new UrlModel();
			Node node = node_list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				urlModel.setName(node.getAttributes().getNamedItem("name").getNodeValue());
				urlModel.setEnable(node.getAttributes().getNamedItem("enble").getNodeValue());
			}
			allUrlModel.add(urlModel);
		}
		return allUrlModel;
	}
}