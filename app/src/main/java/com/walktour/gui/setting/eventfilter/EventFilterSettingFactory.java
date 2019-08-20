package com.walktour.gui.setting.eventfilter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Xml;

import com.dinglicom.dataset.EventManager;
import com.walktour.Utils.StringUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.control.bean.AssetsWriter;
import com.walktour.gui.setting.eventfilter.model.EventFilterSetModel;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * 事件过滤设置单例工厂类 用于事件过滤设置的读取和保存
 * 
 * @author jianchao.wang
 * 
 */
@SuppressLint("SdCardPath")
public class EventFilterSettingFactory {
	/** 文件名称 */
	private static final String FILE_NAME = AppFilePathUtil.getInstance().getAppConfigFile("config_event_filter.xml").getAbsolutePath();
	/** 单例对象 */
	private static EventFilterSettingFactory sInstance;
	/** 对象编码和对象的映射 */
	private Map<String, EventFilterSetModel> codeMap = new LinkedHashMap<String, EventFilterSetModel>();
	/** 网络类型类表，一级菜单 */
	private List<EventFilterSetModel> firstTypeList = new ArrayList<EventFilterSetModel>();
	/** 是否有编辑 */
	private boolean isEdit = false;

	private EventFilterSettingFactory() {
	}

	/**
	 * 返回单例对象
	 * 
	 * @return
	 */
	public static EventFilterSettingFactory getInstance() {
		if (sInstance == null) {
			sInstance = new EventFilterSettingFactory();
		}
		return sInstance;
	}

	/**
	 * 清理所有对象
	 */
	private void clearAll() {
		this.codeMap.clear();
		this.firstTypeList.clear();
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
		private EventFilterSetModel filter;
		/** 二级过滤 */
		private EventFilterSetModel secondFilter;

		// 接收元素开始的通知。当读到一个开始标签的时候，会触发这个方法。其中namespaceURI表示元素的命名空间；
		// localName表示元素的本地名称（不带前缀）；qName表示元素的限定名（带前缀）；atts 表示元素的属性集合
		@Override
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
			if (localName.equals("firstType")) {
				this.filter = readFirstTypes(atts);
			} else if (localName.equals("secondType")) {
				this.secondFilter = readSecondTypes(atts, this.filter);
			} else if (localName.equals("event")) {
				readEventDetails(atts, this.secondFilter);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (localName.equals("firstType"))
				this.filter = null;
			else if (localName.equals("secondType"))
				this.secondFilter = null;
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
		new AssetsWriter(context, "config/config_event_filter.xml", "config_event_filter.xml", isReplace).writeToConfigDir();
		this.initFilters();
	}

	/**
	 * 读取一级分类
	 * 
	 * @param atts
	 *          属性值
	 */
	private EventFilterSetModel readFirstTypes(Attributes atts) {
		EventFilterSetModel filter = this.getModelValue(atts);
		filter.setParent(null);
		filter.setType(EventFilterSetModel.TYPE_FIRST_TYPE);
		this.codeMap.put(filter.getCode(), filter);
		this.firstTypeList.add(filter);
		return filter;
	}

	/**
	 * 读取二级分类
	 * 
	 * @param atts
	 *          属性值
	 * @param parent
	 *          父对象
	 * @param
	 */
	private EventFilterSetModel readSecondTypes(Attributes atts, EventFilterSetModel parent) {
		EventFilterSetModel filter = this.getModelValue(atts);
		filter.setCode(parent.getCode() + filter.getCode());
		filter.setParent(parent);
		filter.setType(EventFilterSetModel.TYPE_SECOND_TYPE);
		this.codeMap.put(filter.getCode(), filter);
		parent.getChildList().add(filter);
		return filter;
	}

	/**
	 * 读取事件明细列表，三级菜单
	 * 
	 * @param atts
	 *          属性值
	 * @param parent
	 *          父对象
	 */
	private void readEventDetails(Attributes atts, EventFilterSetModel parent) {
		EventFilterSetModel filter = this.getModelValue(atts);
		filter.setParent(parent);
		filter.setType(EventFilterSetModel.TYPE_EVENT_DETAIL);
		String[] codes = filter.getCode().split(",");
		for (String code : codes) {
			this.codeMap.put(code, filter);
		}
		parent.getChildList().add(filter);
	}

	/**
	 * 把读取到的值设置到模型中
	 * 
	 * @param atts
	 *          读取的元素
	 */
	private EventFilterSetModel getModelValue(Attributes atts) {
		String code = atts.getValue("code");
		EventFilterSetModel filter = new EventFilterSetModel(code);
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
		String isShowChart = atts.getValue("isShowChart");
		filter.setShowChart(isShowChart != null && isShowChart.equals("1"));
		filter.setImagePath(atts.getValue("imagePath"));
		return filter;
	}

	/**
	 * 获得一级事件过滤列表
	 * 
	 * @return
	 */
	public List<EventFilterSetModel> getFirstTypeList() {
		List<EventFilterSetModel> list = new ArrayList<EventFilterSetModel>();
		list.addAll(firstTypeList);
		return list;
	}

	/**
	 * 获取所有的过滤事件列表
	 * 
	 * @return
	 */
	public List<EventFilterSetModel> getEventList() {
		List<EventFilterSetModel> list = new ArrayList<EventFilterSetModel>();
		list.addAll(this.codeMap.values());
		return list;
	}

	/**
	 * 获得指定ID的事件对象
	 * 
	 * @param eventId
	 *          事件ID
	 * @return
	 */
	public EventFilterSetModel getModel(int eventId) {
		if (eventId == 0)
			return null;
		if (EventManager.getInstance().getEventMap().containsKey(eventId)) {
			return this.getModel(EventManager.getInstance().getEventKeyMap().get(eventId));
		}
		return null;
	}

	/**
	 * 获得指定ID的事件对象
	 * 
	 * @param code
	 *          对象ID
	 * @return
	 */
	public EventFilterSetModel getModel(String code) {
		if (this.codeMap.containsKey(code))
			return this.codeMap.get(code);
		return null;
	}

	/**
	 * 设置编辑的过滤设置
	 * 
	 * @param filter
	 *          过滤设置
	 */
	public void setFilter(EventFilterSetModel filter) {
		switch (filter.getType()) {
		case EventFilterSetModel.TYPE_FIRST_TYPE:
			this.setFirstTypeFilter(filter);
			break;
		case EventFilterSetModel.TYPE_SECOND_TYPE:
			this.setSecondTypeFilter(filter, true);
			break;
		default:
			this.setEventFilter(filter, true);
			break;
		}
	}

	/**
	 * 设置一级过滤
	 * 
	 * @param filter
	 *          过滤设置
	 */
	private void setFirstTypeFilter(EventFilterSetModel filter) {
		if (filter == null)
			return;
		this.isEdit = true;
		// 如果是勾选状态和颜色有设置，则相应的把下级的所有对象都设置成相应的选择
		if (filter.isChangeChecked() || filter.isChangeColor() || filter.isChangeShowList() || filter.isChangeShowMap()
				|| filter.isChangeShowChart()) {
			for (EventFilterSetModel sub : filter.getChildList()) {
				if (filter.isChangeChecked() && filter.getChecked() != EventFilterSetModel.CHECKED_HALF) {
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
				if (filter.isChangeShowChart()) {
					sub.setShowChart(filter.isShowChart());
				}
				this.setSecondTypeFilter(sub, false);
			}
			filter.setChangeChecked(false);
			filter.setChangeColor(false);
		}
	}

	/**
	 * 设置二级过滤
	 * 
	 * @param filter
	 *          过滤设置
	 * @param updateParent
	 *          是否同时设置父节点
	 */
	private void setSecondTypeFilter(EventFilterSetModel filter, boolean updateParent) {
		if (filter == null)
			return;
		this.isEdit = true;
		// 如果是勾选状态和颜色有设置，则相应的把下级的所有对象都设置成相应的选择
		if (filter.isChangeChecked() || filter.isChangeColor() || filter.isChangeShowList() || filter.isChangeShowMap()
				|| filter.isChangeShowChart()) {
			for (EventFilterSetModel msg : filter.getChildList()) {
				if (filter.isChangeChecked() && filter.getChecked() != EventFilterSetModel.CHECKED_HALF) {
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
				if (filter.isChangeShowChart()) {
					msg.setShowChart(filter.isShowChart());
				}
				if (filter.getChecked() == EventFilterSetModel.CHECKED_YES)
					msg.setShowList(true);
				this.setEventFilter(msg, false);
			}
			if (updateParent) {
				this.setParentChange(filter, false);
			}
			filter.setChangeChecked(false);
			filter.setChangeColor(false);
		}

	}

	/**
	 * 设置事件过滤
	 * 
	 * @param filter
	 *          过滤设置
	 * @param updateParent
	 *          是否同时设置父节点
	 */
	private void setEventFilter(EventFilterSetModel filter, boolean updateParent) {
		if (filter == null)
			return;
		this.isEdit = true;
		if (updateParent && (filter.isChangeChecked() || filter.isChangeColor() || filter.isChangeShowList()
				|| filter.isChangeShowMap() || filter.isChangeShowChart())) {
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
	private void setParentChange(EventFilterSetModel filter, boolean updateParent) {
		EventFilterSetModel parent = filter.getParent();
		if (filter.isChangeChecked()) {
			// 是否当前级别的类型为全选或全不选
			int yesCount = 0;
			int noCount = 0;
			for (EventFilterSetModel obj : parent.getChildList()) {
				switch (obj.getChecked()) {
				case EventFilterSetModel.CHECKED_YES:
					yesCount++;
					break;
				case EventFilterSetModel.CHECKED_NO:
					noCount++;
					break;
				}
			}
			if (yesCount == parent.getChildList().size()) {
				parent.setChecked(EventFilterSetModel.CHECKED_YES);
			} else if (noCount == parent.getChildList().size()) {
				parent.setChecked(EventFilterSetModel.CHECKED_NO);
			} else {
				parent.setChecked(EventFilterSetModel.CHECKED_HALF);
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
					for (EventFilterSetModel netType : firstTypeList) {
						xmlWrite.startTag(null, "firstType");
						writeXMLValue(netType, xmlWrite);
						for (EventFilterSetModel subType : netType.getChildList()) {
							xmlWrite.startTag(null, "secondType");
							writeXMLValue(subType, xmlWrite);
							for (EventFilterSetModel msg : subType.getChildList()) {
								xmlWrite.startTag(null, "event");
								writeXMLValue(msg, xmlWrite);
								xmlWrite.endTag(null, "event");
							}
							xmlWrite.endTag(null, "secondType");
						}
						xmlWrite.endTag(null, "firstType");
					}
					xmlWrite.endTag(null, "filters");
					xmlWrite.endDocument();
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
	private void writeXMLValue(EventFilterSetModel filter, XmlSerializer xmlWrite) throws Exception {
		xmlWrite.attribute(null, "code", filter.getCode());
		xmlWrite.attribute(null, "name", filter.getName());
		xmlWrite.attribute(null, "isShowList", filter.isShowList() ? "1" : "0");
		xmlWrite.attribute(null, "isShowMap", filter.isShowMap() ? "1" : "0");
		xmlWrite.attribute(null, "isShowChart", filter.isShowChart() ? "1" : "0");
		xmlWrite.attribute(null, "color", String.valueOf(filter.getColor()));
		xmlWrite.attribute(null, "checked", String.valueOf(filter.getChecked()));
		xmlWrite.attribute(null, "imagePath", StringUtil.isNullOrEmpty(filter.getImagePath()) ? "" : filter.getImagePath());
	}
}
