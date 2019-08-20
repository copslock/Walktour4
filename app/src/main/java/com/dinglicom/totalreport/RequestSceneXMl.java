package com.dinglicom.totalreport;

import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 统计场景请求XMl操作类
 * 
 * @author zhihui.lian
 */
public class RequestSceneXMl {

	private static RequestSceneXMl requestSceneXML = null;

	private XmlSerializer serializer;

	private static XmlPullParser xmlParser;

	private File newxmlfile;

	public DLMessageModel dlMessageModel = new DLMessageModel();

	public String configPathName = "/Walktour/TotalConfig"; // 配置包名

	public List<String> filePathList = new ArrayList<String>();

	public List<String> getFilePathList() {
		return filePathList;
	}

	public void setFilePathList(List<String> filePathList) {
		this.filePathList.clear();
		this.filePathList.addAll(filePathList);
	}

	/**
	 * 工厂操作单例
	 * 
	 * @return
	 */
	public static RequestSceneXMl getInstance() {
		if (requestSceneXML == null) {
			requestSceneXML = new RequestSceneXMl();
		}
		return requestSceneXML;
	}

	/**
	 * 生成场景XML
	 * 
	 * @param baseModelList
	 */
	public void xmlFileCreator(DLMessageModel dlMessageModel) {
		String xmlPath = Environment.getExternalStorageDirectory() + configPathName
				+ (!isParser ? "/sceneXmlOne.xml" : "/sceneXmlTwo.xml");
		newxmlfile = new File(xmlPath);
		try {
			if (!newxmlfile.exists()) {
				newxmlfile.createNewFile();
			}
		} catch (Exception e) {
			Log.e("createException", "create xml exception");
		}
		if (dlMessageModel != null) {
			isParser = false;
			Log.d("xmlCreate", "create xml");
			FileOutputStream fileos = null;
			try {
				fileos = new FileOutputStream(newxmlfile);
			} catch (FileNotFoundException e) {
				Log.d("FileNotFoundException", "can't create FileOutputStream");
			}
			serializer = Xml.newSerializer();
			try {
				// UTF-8 encoding
				serializer.setOutput(fileos, "UTF-8");
				// standalone flag (if standalone not null)
				serializer.startDocument(null, Boolean.valueOf(true));
				// set indentation option
				serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

				Log.d("xmlCreate", "create ScanScheme");
				serializer.startTag(null, "DLMessage");
				serializer.startTag(null, "DLMessageHeader");
				serializer.attribute(null, "Device", "Android");
				serializer.attribute(null, "Tag", "1");
				serializer.attribute(null, "SendTime", dlMessageModel.getSendTime());
				serializer.endTag(null, "DLMessageHeader");
				serializer.startTag(null, "DLMessageBody");
				serializer.startTag(null, "DLFilesStatics");
				serializer.attribute(null, "Business", dlMessageModel.getBusiness());
				serializer.startTag(null, "Scenes");
				eachFileNameXml(dlMessageModel.getSubDlMessageItems());
				serializer.endTag(null, "Scenes");
				serializer.endTag(null, "DLFilesStatics");
				serializer.endTag(null, "DLMessageBody");
				serializer.endTag(null, "DLMessage");
				serializer.endDocument();
				// write xml data into the FileOutputStream
				serializer.flush();
				// finally we close the file stream
				if (fileos != null)
					fileos.close();
			} catch (Exception e) {
				Log.e("Exception", "error occurred while creating xml file");
			}
		}
	}

	/**
	 * 生成报表excel导出场景XML
	 */
	public void xmlXlsCreator(ReportXlsConfigModel xlsConfigModel) {
		String xmlPath = Environment.getExternalStorageDirectory() + configPathName + "/sceneXls.xml";
		newxmlfile = new File(xmlPath);
		try {
			if (!newxmlfile.exists()) {
				newxmlfile.createNewFile();
			}
		} catch (Exception e) {
			Log.e("createException", "create xml exception");
		}
		if (xlsConfigModel != null) {
			Log.d("xmlCreate", "create xml");
			FileOutputStream fileos = null;
			try {
				fileos = new FileOutputStream(newxmlfile);
			} catch (FileNotFoundException e) {
				Log.d("FileNotFoundException", "can't create FileOutputStream");
			}
			serializer = Xml.newSerializer();
			try {
				// UTF-8 encoding
				serializer.setOutput(fileos, "UTF-8");
				// standalone flag (if standalone not null)
				serializer.startDocument(null, Boolean.valueOf(true));
				// set indentation option
				serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

				Log.d("xmlCreate", "create ScanScheme");
				serializer.startTag(null, "DLMessage");
				serializer.startTag(null, "DLMessageHeader");
				serializer.attribute(null, "Device", "Android");
				serializer.attribute(null, "Tag", "1");
				serializer.attribute(null, "SendTime", xlsConfigModel.getSendTime());
				serializer.endTag(null, "DLMessageHeader");
				serializer.startTag(null, "DLMessageBody");
				serializer.startTag(null, "DLExportReportRequest");
				serializer.attribute(null, "TemplateFile", xlsConfigModel.getTemplateFile());
				serializer.startTag(null, "Scenes");
				eachFileNameXml(xlsConfigModel.getSubDlMessageItems());
				serializer.endTag(null, "Scenes");
				serializer.endTag(null, "DLExportReportRequest");
				serializer.endTag(null, "DLMessageBody");
				serializer.endTag(null, "DLMessage");
				serializer.endDocument();
				serializer.flush();
				if (fileos != null)
					fileos.close();
			} catch (Exception e) {
				Log.e("Exception", "error occurred while creating xml file");
			}
		}
	}

	/**
	 * 迭代文件场景列表
	 */
	private void eachFileNameXml(List<SubDlMessageItem> fileNameList) {
		try {
			for (int i = 0; i < fileNameList.size(); i++) {
				SubDlMessageItem subItem = fileNameList.get(i);
				serializer.startTag(null, "Scene");
				serializer.attribute(null, "Name", subItem.getSceneName());
				serializer.startTag(null, "DataSource");
				for (int j = 0; j < subItem.getDataFileName().size(); j++) {
					serializer.startTag(null, "Data");
					serializer.startTag(null, "Item");
					serializer.attribute(null, "FileName", subItem.getDataFileName().get(j).toString());
					serializer.endTag(null, "Item");
					serializer.endTag(null, "Data");
				}
				serializer.endTag(null, "DataSource");
				serializer.endTag(null, "Scene");
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析XML文件转换成对象
	 */

	private boolean isParser = false; // 利用此变量来区分场景一请求与场景二请求

	public DLMessageModel xmlParser() {
		isParser = true;
		DLMessageModel dlMessageModel = null;
		List<SubDlMessageItem> subDlMessageItems = null;
		SubDlMessageItem suDlMessageItem = null;
		List<String> filePathName = new ArrayList<String>();
		try {
			FileInputStream fs = new FileInputStream(
					new File(Environment.getExternalStorageDirectory() + configPathName + "/sceneXmlOne.xml"));
			xmlParser = Xml.newPullParser();
			xmlParser.setInput(fs, "UTF-8");
			int eventType = xmlParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					dlMessageModel = new DLMessageModel();
					break;
				case XmlPullParser.START_TAG:
					if (dlMessageModel != null) {
						if ("DLMessageHeader".equalsIgnoreCase(xmlParser.getName())) {
							dlMessageModel.setTag(xmlParser.getAttributeValue(null, "Tag"));
							dlMessageModel.setDevice(xmlParser.getAttributeValue(null, "Device"));
							dlMessageModel.setSendTime(xmlParser.getAttributeValue(null, "SendTime"));
						}
						if ("DLFilesStatics".equalsIgnoreCase(xmlParser.getName())) {
							dlMessageModel.setBusiness(xmlParser.getAttributeValue(null, "Business"));
						}
					}
					if ("Scenes".equalsIgnoreCase(xmlParser.getName())) {
						subDlMessageItems = new ArrayList<SubDlMessageItem>();

					}
					if ("Scene".equalsIgnoreCase(xmlParser.getName())) {
						suDlMessageItem = new SubDlMessageItem();
						filePathName.clear();
						suDlMessageItem.setSceneName(xmlParser.getAttributeValue(null, "Name"));
					}
					if ("Item".equalsIgnoreCase(xmlParser.getName())) {
						filePathName.add(xmlParser.getAttributeValue(null, "FileName"));

					}
					break;
				case XmlPullParser.END_TAG:
					if (suDlMessageItem != null && subDlMessageItems != null && "Scene".equalsIgnoreCase(xmlParser.getName())) {
						suDlMessageItem.setDataFileName(filePathName);
						subDlMessageItems.add(suDlMessageItem);
					}
					if (dlMessageModel != null && "Scenes".equalsIgnoreCase(xmlParser.getName())) {
						dlMessageModel.setSubDlMessageItems(subDlMessageItems);
					}
					break;
				}
				eventType = xmlParser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dlMessageModel;
	}

}
