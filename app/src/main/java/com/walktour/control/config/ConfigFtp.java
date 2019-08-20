package com.walktour.control.config;

import android.annotation.SuppressLint;
import android.content.Context;

import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyXMLWriter;
import com.walktour.gui.R;
import com.walktour.model.FtpServerModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/***
 * 配置的FTP信息
 * 
 * @author weirong.fan 修改
 *
 */
@SuppressLint("SdCardPath")
public class ConfigFtp {
	private MyXMLWriter writer = null;
	private Document doc = null;

	public ConfigFtp() {
		writer = new MyXMLWriter(AppFilePathUtil.getInstance().getAppConfigFile("config_ftp.xml"));
		doc = writer.getDocument();
	}

	public boolean contains(String ftp_name) {
		boolean result = false;
		String[] ftp_names = getAllFtpNames();
		for (int i = 0; i < ftp_names.length; i++) {
			if (ftp_name.equals(ftp_names[i])) {
				result = true;
				break;
			}
		}
		return result;
	}

	public synchronized void addFtp(FtpServerModel model) {
		Document doc = writer.getDocument();

		String[] names = this.getAllFtpNames();
		boolean exit = false;
		for (String x : names) {
			if (x.equals(model.getName())) {
				setFtpIp(model.getName(), model.getIp());
				setFtpPort(model.getName(), model.getPort());
				setFtpUser(model.getName(), model.getLoginUser());
				setFtpPass(model.getName(), model.getLoginPassword());
				setAnonymous(model.getName(), model.isAnonymous());
				setConnectMode(model.getName(), model.getConnect_mode());
				exit = true;
			}
		}

		if (!exit) {
			Element elment = doc.createElement("ftp");
			elment.setAttribute("name", model.getName());
			elment.setAttribute("ip", model.getIp());
			elment.setAttribute("port", model.getPort());
			elment.setAttribute("user", model.getLoginUser());
			elment.setAttribute("pass", model.getLoginPassword());
			elment.setAttribute("anonymous", model.isAnonymous() ? "1" : "0");
			elment.setAttribute("connectMode", String.valueOf(model.getConnect_mode()));
			doc.getDocumentElement().appendChild(elment);
			writer.writeToFile(doc);
		}
	}

	/**
	 * 编辑FTP服务器
	 * 
	 * @param model
	 * @param position
	 */
	public void editFtp(FtpServerModel model, Integer position) {
		Document doc = writer.getDocument();

		NodeList list = doc.getElementsByTagName("ftp");

		for (int i = 0; i < list.getLength(); i++) {
			Element e = (Element) list.item(i);
			if (i == position) {
				e.getAttributes().getNamedItem("name").setNodeValue(model.getName());
				e.getAttributes().getNamedItem("ip").setNodeValue(model.getIp());
				e.getAttributes().getNamedItem("port").setNodeValue(model.getPort());
				e.getAttributes().getNamedItem("user").setNodeValue(model.getLoginUser());
				e.getAttributes().getNamedItem("pass").setNodeValue(model.getLoginPassword());
				e.getAttributes().getNamedItem("anonymous").setNodeValue(model.isAnonymous() ? "1" : "0");
				e.getAttributes().getNamedItem("connectMode").setNodeValue(String.valueOf(model.getConnect_mode()));
				break;
			}

		}
		writer.writeToFile(doc);

	}

	public FtpServerModel getFtpServerModel(String name) {
		FtpServerModel model = new FtpServerModel(name, getFtpIp(name), getFtpPort(name), getFtpUser(name),
				getFtpPass(name));

		model.setAnonymous(getAnonymous(name));
		model.setConnect_mode(getConnectMode(name));

		return model;
	}

	public String[] getAllFtpNames() {
		// 动态数组
		ArrayList<String> arrayList = new ArrayList<String>();
		NodeList node_list = doc.getElementsByTagName("ftp");
		for (int i = 0; i < node_list.getLength(); i++) {
			Node node = node_list.item(i);
			if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
				arrayList.add(node.getAttributes().getNamedItem("name").getNodeValue());
			}
		}
		// 动态数组转换成字符串数组后返回
		String strArray[] = new String[arrayList.size()];
		arrayList.toArray(strArray);
		return strArray;
	}

	public String[] getAllFtpIps() {
		// 动态数组
		ArrayList<String> arrayList = new ArrayList<String>();

		NodeList node_list = doc.getElementsByTagName("ftp");
		for (int i = 0; i < node_list.getLength(); i++) {
			Node node = node_list.item(i);
			if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
				arrayList.add(node.getAttributes().getNamedItem("ip").getNodeValue());
			}
		}
		// 动态数组转换成字符串数组后返回
		String strArray[] = new String[arrayList.size()];
		arrayList.toArray(strArray);
		return strArray;
	}

	public String[] getAllFtpPorts() {
		// 动态数组
		ArrayList<String> arrayList = new ArrayList<String>();

		NodeList node_list = doc.getElementsByTagName("ftp");
		for (int i = 0; i < node_list.getLength(); i++) {
			Node node = node_list.item(i);
			if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
				arrayList.add(node.getAttributes().getNamedItem("port").getNodeValue());
			}
		}
		// 动态数组转换成字符串数组后返回
		String strArray[] = new String[arrayList.size()];
		arrayList.toArray(strArray);
		return strArray;
	}

	public String getFtpIp(String name) {
		StringBuffer sb = new StringBuffer();

		NodeList node_list = doc.getElementsByTagName("ftp");
		for (int i = 0; i < node_list.getLength(); i++) {
			Node node = node_list.item(i);
			if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getAttributes().getNamedItem("name").getNodeValue().equals(name)) {
					sb.append(node.getAttributes().getNamedItem("ip").getNodeValue());
					break;
				}
			}
		}

		return sb.toString();
	}

	public String getFtpPort(String name) {
		StringBuffer sb = new StringBuffer();

		NodeList node_list = doc.getElementsByTagName("ftp");
		for (int i = 0; i < node_list.getLength(); i++) {
			Node node = node_list.item(i);
			if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getAttributes().getNamedItem("name").getNodeValue().equals(name)) {
					sb.append(node.getAttributes().getNamedItem("port").getNodeValue());
					break;
				}
			}
		}

		return sb.toString();
	}

	public String getFtpUser(String name) {
		StringBuffer sb = new StringBuffer();

		NodeList node_list = doc.getElementsByTagName("ftp");
		for (int i = 0; i < node_list.getLength(); i++) {
			Node node = node_list.item(i);
			if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getAttributes().getNamedItem("name").getNodeValue().equals(name)) {
					sb.append(node.getAttributes().getNamedItem("user").getNodeValue());
					break;
				}
			}
		}

		return sb.toString();
	}

	public String getFtpPass(String name) {
		StringBuffer sb = new StringBuffer();

		NodeList node_list = doc.getElementsByTagName("ftp");
		for (int i = 0; i < node_list.getLength(); i++) {
			Node node = node_list.item(i);
			if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getAttributes().getNamedItem("name").getNodeValue().equals(name)) {
					sb.append(node.getAttributes().getNamedItem("pass").getNodeValue());
					break;
				}
			}
		}

		return sb.toString();
	}

	public boolean getAnonymous(String name) {

		NodeList node_list = doc.getElementsByTagName("ftp");
		for (int i = 0; i < node_list.getLength(); i++) {
			Node node = node_list.item(i);
			if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getAttributes().getNamedItem("name").getNodeValue().equals(name)) {
					String s = node.getAttributes().getNamedItem("anonymous").getNodeValue();
					return s.equals("1");
				}
			}
		}

		return false;
	}

	public int getConnectMode(String name) {
		int mode = FtpServerModel.CONNECT_MODE_PASSIVE;
		NodeList node_list = doc.getElementsByTagName("ftp");
		for (int i = 0; i < node_list.getLength(); i++) {
			Node node = node_list.item(i);
			if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getAttributes().getNamedItem("name").getNodeValue().equals(name)) {
					String s = node.getAttributes().getNamedItem("connectMode").getNodeValue();
					try {
						return Integer.parseInt(s);
					} catch (Exception e) {

					}
				}
			}
		}

		return mode;
	}

	public String[] getFtp_Attrs(String name) {
		String[] sb = new String[5];

		NodeList node_list = doc.getElementsByTagName("ftp");
		for (int i = 0; i < node_list.getLength(); i++) {
			Node node = node_list.item(i);
			if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getAttributes().getNamedItem("name").getNodeValue().equals(name)) {
					sb[0] = (node.getAttributes().getNamedItem("name").getNodeValue());
					sb[1] = (node.getAttributes().getNamedItem("ip").getNodeValue());
					sb[2] = (node.getAttributes().getNamedItem("port").getNodeValue());
					sb[3] = (node.getAttributes().getNamedItem("user").getNodeValue());
					sb[4] = ("******");
				}
			}
		}

		return sb;
	}

	public int getPositon(String name) {
		int result = -1;
		NodeList node_list = doc.getElementsByTagName("ftp");
		for (int i = 0; i < node_list.getLength(); i++) {
			Node node = node_list.item(i);
			if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getAttributes().getNamedItem("name").getNodeValue().equals(name)) {
					result = i;
					break;
				}
			}
		}
		return result;
	}

	public String getName(int position) {
		StringBuffer sb = new StringBuffer();
		NodeList node_list = doc.getElementsByTagName("ftp");
		Node node = node_list.item(position);
		if (node != null) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				sb.append(node.getAttributes().getNamedItem("name").getNodeValue());
			}
		}
		return sb.toString();
	}

	public void removeFtp(String name) {
		Document doc = writer.getDocument();

		NodeList nodelist_ftps = doc.getDocumentElement().getElementsByTagName("ftp");
		writer.removeNodeByAttrValue(doc,nodelist_ftps, "name", name);
		LogUtil.v("After RemoveFtp--->", writer.docToStr(doc));
		writer.writeToFile(doc);
	}

	public void removeFtps(String[] ftpNames) {
		Document doc = writer.getDocument();
		NodeList nodelist_ftps = doc.getDocumentElement().getElementsByTagName("ftp");
		for (int i = 0; i < ftpNames.length; i++) {
			writer.removeNodeByAttrValue(doc,nodelist_ftps, "name", ftpNames[i]);
		}
		writer.writeToFile(doc);
	}

	public void setFtpName(String ftp_name, String value) {
		Document doc = writer.getDocument();
		NodeList nodelist_ftps = doc.getDocumentElement().getElementsByTagName("ftp");

		Node node = writer.getItemNodeByAttrValue(nodelist_ftps, "name", ftp_name);
		node.getAttributes().getNamedItem("name").setNodeValue(value);

		writer.writeToFile(doc);
	}

	public void setFtpIp(String ftp_name, String value) {
		Document doc = writer.getDocument();
		NodeList nodelist_ftps = doc.getDocumentElement().getElementsByTagName("ftp");

		Node node = writer.getItemNodeByAttrValue(nodelist_ftps, "name", ftp_name);
		node.getAttributes().getNamedItem("ip").setNodeValue(value);

		writer.writeToFile(doc);
	}

	public void setFtpPort(String ftp_name, String value) {
		Document doc = writer.getDocument();
		NodeList nodelist_ftps = doc.getDocumentElement().getElementsByTagName("ftp");

		Node node = writer.getItemNodeByAttrValue(nodelist_ftps, "name", ftp_name);
		node.getAttributes().getNamedItem("port").setNodeValue(value);

		writer.writeToFile(doc);
	}

	public void setFtpUser(String ftp_name, String value) {
		Document doc = writer.getDocument();
		NodeList nodelist_ftps = doc.getDocumentElement().getElementsByTagName("ftp");

		Node node = writer.getItemNodeByAttrValue(nodelist_ftps, "name", ftp_name);
		node.getAttributes().getNamedItem("user").setNodeValue(value);

		writer.writeToFile(doc);
	}

	public void setFtpPass(String ftp_name, String value) {
		Document doc = writer.getDocument();
		NodeList nodelist_ftps = doc.getDocumentElement().getElementsByTagName("ftp");

		Node node = writer.getItemNodeByAttrValue(nodelist_ftps, "name", ftp_name);
		node.getAttributes().getNamedItem("pass").setNodeValue(value);

		writer.writeToFile(doc);
	}

	public void setAnonymous(String ftp_name, boolean anonymous) {
		Document doc = writer.getDocument();
		NodeList nodelist_ftps = doc.getDocumentElement().getElementsByTagName("ftp");

		Node node = writer.getItemNodeByAttrValue(nodelist_ftps, "name", ftp_name);
		node.getAttributes().getNamedItem("anonymous").setNodeValue(anonymous ? "1" : "0");

		writer.writeToFile(doc);
	}

	public void setConnectMode(String ftp_name, int mode) {
		Document doc = writer.getDocument();
		NodeList nodelist_ftps = doc.getDocumentElement().getElementsByTagName("ftp");

		Node node = writer.getItemNodeByAttrValue(nodelist_ftps, "name", ftp_name);
		node.getAttributes().getNamedItem("connectMode").setNodeValue(String.valueOf(mode));

		writer.writeToFile(doc);
	}

	/**
	 * @author wuqing.tang@dinglicom.com
	 */
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

	/**
	 * 通过dom对象查找出节点并保存到xml,供导入任务用
	 * 
	 * @param docc
	 */
	public void addFtpToXMl(Document docc) {
		String[] sb = null;

		NodeList node_list = docc.getElementsByTagName("ftp");
		for (int i = 0; i < node_list.getLength(); i++) {
			Node node = node_list.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			sb = new String[7];
			sb[0] = (node.getAttributes().getNamedItem("name").getNodeValue());
			if (contains(sb[0])) { // 如果ftp名字相同，则不替换ftp服务器
				continue;
			}
			sb[1] = (node.getAttributes().getNamedItem("ip").getNodeValue());
			sb[2] = (node.getAttributes().getNamedItem("port").getNodeValue());
			sb[3] = (node.getAttributes().getNamedItem("user").getNodeValue());
			sb[4] = (node.getAttributes().getNamedItem("pass").getNodeValue());
			// 2013.8.26增加两个字段
			try {
				sb[5] = (node.getAttributes().getNamedItem("anonymous").getNodeValue());
				sb[6] = (node.getAttributes().getNamedItem("connectMode").getNodeValue());
			} catch (Exception e) {
				sb[5] = "0";
				sb[6] = "0";
			}
			FtpServerModel ftp = new FtpServerModel();
			ftp.setName(sb[0]);
			ftp.setIp(sb[1]);
			ftp.setPort(sb[2]);
			ftp.setLoginUser(sb[3]);
			ftp.setLoginPassword(sb[4]);
			ftp.setAnonymous(sb[5].equals("1"));
			ftp.setConnect_mode(Integer.parseInt(sb[6]));
			addFtp(ftp);
		}
	}

	public String[] getAllFtpNamesFirstEmpty(Context context) {
		// 动态数组
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add(context.getString(R.string.none));

		Document doc = writer.getDocument();
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

}