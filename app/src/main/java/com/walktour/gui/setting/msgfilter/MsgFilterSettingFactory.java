package com.walktour.gui.setting.msgfilter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Xml;

import com.walktour.base.util.AppFilePathUtil;
import com.walktour.control.bean.AssetsWriter;
import com.walktour.gui.setting.eventfilter.model.EventFilterSetModel;
import com.walktour.gui.setting.msgfilter.model.MsgFilterSetModel;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * 信令过滤设置单例工厂类 用于信令过滤设置的读取和保存
 * 
 * @author jianchao.wang
 * 
 */
@SuppressLint("SdCardPath")
public class MsgFilterSettingFactory {
	/** 文件名称 */
	private static final String FILE_NAME = AppFilePathUtil.getInstance().getAppConfigFile("config_l3msg_filter.xml").getAbsolutePath();
	/** 单例对象 */
	private static MsgFilterSettingFactory sInstance;
	/** 对象编码和对象的映射 */
	private Map<String, MsgFilterSetModel> codeMap = new HashMap<String, MsgFilterSetModel>();
	/** 网络类型类表，一级菜单 */
	private List<MsgFilterSetModel> netTypeList = new ArrayList<MsgFilterSetModel>();
	/** 默认对象，用于返回不存在配置表中的代码 */
	private MsgFilterSetModel defaultModel = new MsgFilterSetModel("");
	/** 是否有编辑 */
	private boolean isEdit = false;
	/** 需要过滤Code */
	private List<String> filterCodeList = new ArrayList<String>();
	/** 所有信令默认显示队列 */
	private List<String> l3ModelStrAll = new ArrayList<String>();

	private MsgFilterSettingFactory() {
	}

	/**
	 * 返回单例对象
	 * 
	 * @return
	 */
	public static MsgFilterSettingFactory getInstance() {
		if (sInstance == null) {
			sInstance = new MsgFilterSettingFactory();
		}
		return sInstance;
	}

	/**
	 * 清理所有对象
	 */
	private void clearAll() {
		this.codeMap.clear();
		this.netTypeList.clear();
		this.filterCodeList.clear();
		this.l3ModelStrAll.clear();
	}

	/**
	 * 初次运行时读取所有的过滤参数
	 */
	private void initFilters() {
		this.clearAll();
		InputStream is = null;
		try {
			is = new FileInputStream(FILE_NAME);
			// 创建解析器
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser saxParser = spf.newSAXParser();

			// 设置解析器的相关特性，true表示开启命名空间特性
			XMLContentHandler handler = new XMLContentHandler();
			saxParser.parse(is, handler);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * SAX类：DefaultHandler，它实现了ContentHandler接口。在实现的时候，只需要继承该类，重载相应的方法即可。
	 * 
	 * @author jianchao.wang
	 * 
	 */
	private class XMLContentHandler extends DefaultHandler {
		/** 一级过滤 */
		private MsgFilterSetModel filter;
		/** 二级过滤 */
		private MsgFilterSetModel subFilter;

		// 接收元素开始的通知。当读到一个开始标签的时候，会触发这个方法。其中namespaceURI表示元素的命名空间；
		// localName表示元素的本地名称（不带前缀）；qName表示元素的限定名（带前缀）；atts 表示元素的属性集合
		@Override
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
			if (localName.equals("nettype")) {
				this.filter = readNetTypes(atts);
			} else if (localName.equals("subtype")) {
				this.subFilter = readSubTypes(atts, this.filter);
			} else if (localName.equals("msg")) {
				readMsgDetails(atts, this.subFilter);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (localName.equals("nettype"))
				this.filter = null;
			else if (localName.equals("subtype"))
				this.subFilter = null;
		}

	}

	/**
	 * 把过滤设置恢复为默认值(重新写入配置文件)
	 * 
	 * @param context
	 *          上下文
	 * @param isReplace
	 *          是否回复
	 */
	public void resetToDefaultFromFile(Context context, boolean isReplace) {
		new AssetsWriter(context, "config/config_l3msg_filter.xml", "config_l3msg_filter.xml", isReplace).writeToConfigDir();
		this.initFilters();
	}

	/**
	 * 读取网络类型，一级菜单
	 * 
	 * @param atts
	 *          属性值
	 */
	private MsgFilterSetModel readNetTypes(Attributes atts) {
		MsgFilterSetModel filter = this.getModelValue(atts);
		filter.setParent(null);
		filter.setType(MsgFilterSetModel.TYPE_NET_TYPE);
		this.codeMap.put(filter.getCode(), filter);
		this.netTypeList.add(filter);
		return filter;
	}

	/**
	 * 读取网络子类型,二级菜单
	 * 
	 * @param atts
	 *          属性值
	 * @param parent
	 *          父对象
	 * @param
	 */
	private MsgFilterSetModel readSubTypes(Attributes atts, MsgFilterSetModel parent) {
		MsgFilterSetModel filter = this.getModelValue(atts);
		filter.setCode(parent.getCode() + filter.getCode());
		filter.setParent(parent);
		filter.setType(MsgFilterSetModel.TYPE_NET_SUB_TYPE);
		this.codeMap.put(filter.getCode(), filter);
		parent.getChildList().add(filter);
		return filter;
	}

	/**
	 * 读取信令明细列表，三级菜单
	 * 
	 * @param atts
	 *          属性值
	 * @param parent
	 *          父对象
	 */
	private void readMsgDetails(Attributes atts, MsgFilterSetModel parent) {
		MsgFilterSetModel filter = this.getModelValue(atts);
		if (parent.getChecked() != MsgFilterSetModel.CHECKED_NO && filter.getChecked() == MsgFilterSetModel.CHECKED_YES) {
			filterCodeList.add(filter.getCode());
		}
		l3ModelStrAll.add(filter.getName());
		filter.setParent(parent);
		filter.setType(MsgFilterSetModel.TYPE_MSG_DETAIL);
		this.codeMap.put(filter.getCode().toUpperCase(Locale.getDefault()), filter);
		parent.getChildList().add(filter);
	}

	/**
	 * 把读取到的值设置到模型中
	 * 
	 *          读取的元素
	 */
	private MsgFilterSetModel getModelValue(Attributes atts) {
		String code = atts.getValue("code");
		MsgFilterSetModel filter = new MsgFilterSetModel(code);
		filter.setName(atts.getValue("name"));
		String checked = atts.getValue("checked");
		filter.setChecked(Integer.parseInt(checked));
		String color = atts.getValue("color");
		if (color == null || color.trim().length() == 0)
			color = "0";
		filter.setColor(Integer.parseInt(color));
		String isShowList = atts.getValue("isShowList");
		filter.setShowList(isShowList != null && isShowList.equals("1"));
		String isShowMap = atts.getValue("isShowMap");
		filter.setShowMap(isShowMap != null && isShowMap.equals("1"));
		return filter;
	}

	/**
	 * 获得一级网络类型列表
	 * 
	 * @return
	 */
	public List<MsgFilterSetModel> getNetTypeList() {
		List<MsgFilterSetModel> list = new ArrayList<MsgFilterSetModel>();
		list.addAll(this.netTypeList);
		return list;
	}

	/**
	 * 获得指定ID的信令对象
	 * 
	 * @param msgId
	 *          信令ID
	 * @return
	 */
	public MsgFilterSetModel getModel(long msgId) {
		String code = "0X" + Long.toHexString(msgId).toUpperCase(Locale.getDefault());
		return this.getModel(code);
	}

	/**
	 * 获得指定ID的信令对象
	 * 
	 *          对象ID
	 * @return
	 */
	public MsgFilterSetModel getModel(String code) {
		if (this.codeMap.containsKey(code))
			return this.codeMap.get(code);
		return this.defaultModel;
	}

	/**
	 * 设置编辑的过滤设置
	 * 
	 * @param filter
	 *          过滤设置
	 */
	public void setFilter(MsgFilterSetModel filter) {
		switch (filter.getType()) {
		case MsgFilterSetModel.TYPE_NET_TYPE:
			this.setNetTypeFilter(filter);
			break;
		case MsgFilterSetModel.TYPE_NET_SUB_TYPE:
			this.setNetSubTypeFilter(filter, true);
			break;
		default:
			this.setMsgFilter(filter, true);
			break;
		}
	}

	/**
	 * 设置网络类型过滤
	 * 
	 * @param filter
	 *          过滤设置
	 */
	private void setNetTypeFilter(MsgFilterSetModel filter) {
		if (filter == null)
			return;
		this.isEdit = true;
		// 如果是勾选状态、颜色、列表显示、地图显示有设置，则相应的把下级的所有对象都设置成相应的选择
		if (filter.isChangeChecked() || filter.isChangeColor() || filter.isChangeShowList() || filter.isChangeShowMap()) {
			for (MsgFilterSetModel sub : filter.getChildList()) {
				if (filter.isChangeChecked() && filter.getChecked() != MsgFilterSetModel.CHECKED_HALF) {
					sub.setChecked(filter.getChecked());
				}
				if (filter.isChangeColor()) {
					sub.setColor(filter.getColor());
				}
				if (filter.isChangeShowList()) {
					sub.setShowList(filter.isShowList());
				}
				if (filter.isChangeShowMap()) {
					sub.setShowMap(filter.isShowMap());
				}
				if (filter.getChecked() == EventFilterSetModel.CHECKED_YES)
					sub.setShowList(true);
				this.setNetSubTypeFilter(sub, false);
			}
			filter.setChangeChecked(false);
			filter.setChangeColor(false);
			filter.setChangeShowList(false);
			filter.setChangeShowMap(false);
		}
	}

	/**
	 * 设置网络子类型过滤
	 * 
	 * @param filter
	 *          过滤设置
	 * @param updateParent
	 *          是否同时设置父节点
	 */
	private void setNetSubTypeFilter(MsgFilterSetModel filter, boolean updateParent) {
		if (filter == null)
			return;
		this.isEdit = true;
		// 如果是勾选状态、颜色、列表显示、地图显示有设置，则相应的把下级的所有对象都设置成相应的选择
		if (filter.isChangeChecked() || filter.isChangeColor() || filter.isChangeShowList() || filter.isChangeShowMap()) {
			for (MsgFilterSetModel msg : filter.getChildList()) {
				if (filter.isChangeChecked() && filter.getChecked() != MsgFilterSetModel.CHECKED_HALF) {
					msg.setChecked(filter.getChecked());
				}
				if (filter.isChangeColor()) {
					msg.setColor(filter.getColor());
				}
				if (filter.isChangeShowList()) {
					msg.setShowList(filter.isShowList());
				}
				if (filter.isChangeShowMap()) {
					msg.setShowMap(filter.isShowMap());
				}
				this.setMsgFilter(msg, false);
			}
			if (updateParent) {
				this.setParentChange(filter, false);
			}
			filter.setChangeChecked(false);
			filter.setChangeColor(false);
			filter.setChangeShowList(false);
			filter.setChangeShowMap(false);
		}
	}

	/**
	 * 设置信令过滤
	 * 
	 * @param filter
	 *          过滤设置
	 * @param updateParent
	 *          是否同时设置父节点
	 */
	private void setMsgFilter(MsgFilterSetModel filter, boolean updateParent) {
		if (filter == null)
			return;
		this.isEdit = true;
		if (updateParent && (filter.isChangeChecked() || filter.isChangeColor() || filter.isChangeShowList()
				|| filter.isChangeShowMap())) {
			this.setParentChange(filter, updateParent);
		}
	}

	/**
	 * 设置父节点的勾选和颜色属性 ,如果是勾选状态和颜色有设置，则相应的把下级的所有对象都设置成相应的选择
	 * 
	 * @param filter
	 *          当前节点
	 * @param updateParent
	 *          是否设置顶级节点的勾选和颜色属性
	 */
	private void setParentChange(MsgFilterSetModel filter, boolean updateParent) {
		MsgFilterSetModel parent = filter.getParent();
		if (filter.isChangeChecked()) {
			// 是否当前级别的类型为全选或全不选
			int yesCount = 0;
			int noCount = 0;
			for (MsgFilterSetModel obj : parent.getChildList()) {
				switch (obj.getChecked()) {
				case MsgFilterSetModel.CHECKED_YES:
					yesCount++;
					break;
				case MsgFilterSetModel.CHECKED_NO:
					noCount++;
					break;
				}
			}
			if (yesCount == parent.getChildList().size()) {
				parent.setChecked(MsgFilterSetModel.CHECKED_YES);
			} else if (noCount == parent.getChildList().size()) {
				parent.setChecked(MsgFilterSetModel.CHECKED_NO);
			} else {
				parent.setChecked(MsgFilterSetModel.CHECKED_HALF);
			}
			this.isEdit = true;
			if (!updateParent)
				parent.setChangeChecked(false);
		}
		if (filter.isChangeColor()) {
			parent.setColor(Color.LTGRAY);
			this.isEdit = true;
			if (!updateParent)
				parent.setChangeColor(false);
		}

		if (updateParent) {
			this.setParentChange(parent, false);
		}

	}

	/**
	 * 保存编辑的内容到文件中
	 */
	public void writeToFile() {
		if (!this.isEdit)
			return;
		new Thread() {

			public void run() {
				FileOutputStream outputStream = null;
				try {
					outputStream = new FileOutputStream(new File(FILE_NAME));
					XmlSerializer xmlWrite = Xml.newSerializer();
					xmlWrite.setOutput(outputStream, "UTF-8");
					xmlWrite.startDocument("UTF-8", true);
					xmlWrite.startTag(null, "filters");
					for (MsgFilterSetModel netType : netTypeList) {
						xmlWrite.startTag(null, "nettype");
						writeXMLValue(netType, xmlWrite);
						for (MsgFilterSetModel subType : netType.getChildList()) {
							xmlWrite.startTag(null, "subtype");
							writeXMLValue(subType, xmlWrite);
							for (MsgFilterSetModel msg : subType.getChildList()) {
								xmlWrite.startTag(null, "msg");
								writeXMLValue(msg, xmlWrite);
								xmlWrite.endTag(null, "msg");
							}
							xmlWrite.endTag(null, "subtype");
						}
						xmlWrite.endTag(null, "nettype");
					}
					xmlWrite.endTag(null, "filters");
					xmlWrite.endDocument();
					outputStream.close();
					isEdit = false;
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					try {
						if (outputStream != null)
							outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	/**
	 * 写入xml数据
	 * 
	 * @param filter
	 *          过滤对象
	 * @param xmlWrite
	 *          写入类
	 * @throws Exception
	 */
	private void writeXMLValue(MsgFilterSetModel filter, XmlSerializer xmlWrite) throws Exception {
		xmlWrite.attribute(null, "code", filter.getCode());
		xmlWrite.attribute(null, "name", filter.getName());
		xmlWrite.attribute(null, "isShowList", filter.isShowList() ? "1" : "0");
		xmlWrite.attribute(null, "isShowMap", filter.isShowMap() ? "1" : "0");
		xmlWrite.attribute(null, "color", String.valueOf(filter.getColor()));
		xmlWrite.attribute(null, "checked", String.valueOf(filter.getChecked()));
	}

	public List<String> getFilterCodeList() {
		initFilters();
		return filterCodeList;
	}

	public List<String> getl3ModelStrAll() {
		initFilters();
		return l3ModelStrAll;
	}

}
