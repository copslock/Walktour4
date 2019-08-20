package com.walktour.gui.setting.customevent;

import android.os.Handler;
import android.util.Xml;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalCustomEvent;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.setting.customevent.model.CustomEvent;
import com.walktour.gui.setting.customevent.model.CustomEventMsg;
import com.walktour.gui.setting.customevent.model.CustomEventParam;
import com.walktour.model.TotalCustomModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 自定义参数工厂类
 * 
 * @author jianchao.wang
 *
 */
public class CustomEventFactory {
	private static final String TAG = "CustomEventFactory";
	/** 编辑了自定义事件:编辑 */
	public static final int MSG_CUSTOM_EVENT_EDIT = 0x01;
	/** 编辑了自定义事件:删除 */
	public static final int MSG_CUSTOM_EVENT_DELETE = 0x02;
	/** 编辑了自定义事件:新增 */
	public static final int MSG_CUSTOM_EVENT_ADD = 0x03;
	/** 编辑了自定义事件:改变 */
	public static final int MSG_CUSTOM_EVENT_CHANGE = 0x04;
	/** 文件名称 */
	public static final String CUSTOM_FILE_NAME = "CustomEvent.txt";
	/** 文件名称XML格式 */
	public static final String CUSTOM_FILE_XML_NAME = "CustomEvent.xml";
	/** 唯一实例 */
	private static CustomEventFactory sInstance = null;
	/** 自定义事件里最大的时延 */
	public int customMaxDelay = 0;
	/** 所有自定义信令事件 */
	private List<CustomEventMsg> customMsgList = new ArrayList<CustomEventMsg>();
	/** 所有自定义参数事件 */
	private List<CustomEventParam> customParamList = new ArrayList<CustomEventParam>();
	/** 所有自定义事件映射<事件名称,事件对象> */
	private Map<String, CustomEvent> customMap = new HashMap<String, CustomEvent>();
	/** 消息处理句柄集合 */
	private Set<Handler> handlerSet = new HashSet<Handler>();
	/** 自定义事件统计的列表 */
	private List<TotalCustomModel> totalEventList = new ArrayList<TotalCustomModel>();
	/** 删除的对象集合 */
	private Set<CustomEvent> removeSet = new HashSet<CustomEvent>();
	/** xml解析类 */
	private XmlPullParser xmlParser;
	/** xml编辑类 */
	private XmlSerializer serializer;



	@Override
	public String toString() {
		return "CustomEventFactory{" +
				"customMaxDelay=" + customMaxDelay +
				", customMsgList=" + customMsgList +
				", customParamList=" + customParamList +
				", customMap=" + customMap +
				", handlerSet=" + handlerSet +
				", totalEventList=" + totalEventList +
				", removeSet=" + removeSet +
				", xmlParser=" + xmlParser +
				", serializer=" + serializer +
				'}';
	}
	private CustomEventFactory() {

	}
	/**
	 * 返回唯一实例
	 * 
	 * @return
	 */
	public static CustomEventFactory getInstance() {
		if (sInstance == null) {
			sInstance = new CustomEventFactory();
		}
		return sInstance;
	}

	/**
	 * 开始测试时清除 几个事件列表
	 * 
	 */
	public synchronized void clearEvents() {
		// 统计结果列表
		this.totalEventList.clear();
		this.obtainMessage(MSG_CUSTOM_EVENT_CHANGE);
	}

	/**
	 * 初始化自定义参数和自定义事件列表
	 * 
	 */
	public void initCustomEvent() {
		LogUtil.d(TAG, "-----initCustomEvent-----");
		boolean flag = this.readTextCustomEvent();
		this.readXmlCustomEvent();
		if (flag) {
			AppFilePathUtil.getInstance().deleteSDCardBaseFile(CUSTOM_FILE_NAME);
			this.writeXmlToFile();
		}
	}

	/**
	 * 初始化自定义参数和自定义事件列表,新格式采用xml
	 */
	private void readXmlCustomEvent() {
		File file = AppFilePathUtil.getInstance().getSDCardBaseFile(CUSTOM_FILE_XML_NAME);
		if (!file.exists()) {
			return;
		}
		FileInputStream fis = null;
		try {
			if (xmlParser == null) {
				xmlParser = Xml.newPullParser();
			}
			fis = new FileInputStream(file);
			xmlParser.setInput(fis, "UTF-8");
			int eventType = xmlParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if ("CustomEventList".equals(xmlParser.getName())) {
						this.readCustomEvents();
					}
					break;
				}
				eventType = xmlParser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
					fis = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 读取xml文件自定义参数事件对象
	 * 
	 * @throws Exception
	 */
	private void readCustomEventParam() throws Exception {
		CustomEventParam define = new CustomEventParam();
		define.setName(this.getStringAttributeValue("Name"));
		define.setShowAlarm(this.getBooleanAttributeValue("ShowAlarm"));
		define.setShowChart(this.getBooleanAttributeValue("ShowChart"));
		define.setShowMap(this.getBooleanAttributeValue("ShowMap"));
		define.setShowTotal(this.getBooleanAttributeValue("ShowTotal"));
		define.setIconFilePath(this.getStringAttributeValue("IconFilePath"));
		define.setParams(this.getStringAttributeValue("Params"));
		define.setDuration(this.getIntAttributeValue("Duration"));
		this.customParamList.add(define);
		this.customMap.put(define.getName(), define);
	}

	/**
	 * 获取属性值
	 * 
	 * @param name
	 *          属性名
	 * @return
	 */
	private String getStringAttributeValue(String name) {
		String value = xmlParser.getAttributeValue(null, name);
		if (value == null)
			value = "";
		return value;
	}

	/**
	 * 获取属性值
	 * 
	 * @param name
	 *          属性名
	 * @return
	 */
	private int getIntAttributeValue(String name) {
		String value = this.getStringAttributeValue(name);
		if (StringUtil.isNullOrEmpty(value))
			value = "0";
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			return 0;
		}
	}
	/**
	 * 获取属性值
	 *
	 * @param name
	 *          属性名
	 * @return
	 */
	private long getLongAttributeValue(String name) {
		String value = this.getStringAttributeValue(name);
		if (StringUtil.isNullOrEmpty(value))
			value = "0";
		try {
			return Long.parseLong(value);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 获取属性值
	 * 
	 * @param name
	 *          属性名
	 * @return
	 */
	private boolean getBooleanAttributeValue(String name) {
		String value = this.getStringAttributeValue(name);
		return Boolean.parseBoolean(value);
	}

	/**
	 * 读取xml文件自定义信令事件对象
	 * 
	 * @throws Exception
	 */
	private void readCustomEventMsg() throws Exception {
		CustomEventMsg define = new CustomEventMsg();
		define.setName(this.getStringAttributeValue("Name"));
		define.setShowAlarm(this.getBooleanAttributeValue("ShowAlarm"));
		define.setShowChart(this.getBooleanAttributeValue("ShowChart"));
		define.setShowMap(this.getBooleanAttributeValue("ShowMap"));
		define.setShowTotal(this.getBooleanAttributeValue("ShowTotal"));
		define.setIconFilePath(this.getStringAttributeValue("IconFilePath"));
		define.setCompare(this.getIntAttributeValue("Compare"));
		define.setInterval(this.getIntAttributeValue("Interval"));
		if (define.getInterval() > customMaxDelay) {
			customMaxDelay = define.getInterval();
		}
		define.setL3MsgID1(this.getLongAttributeValue("L3MsgID1"));
		define.setL3MsgID2(this.getLongAttributeValue("L3MsgID2"));
		this.customMsgList.add(define);
		this.customMap.put(define.getName(), define);
	}

	/**
	 * 读取xml文件自定义事件对象
	 * 
	 * @throws Exception
	 */
	private void readCustomEvents() throws Exception {
		int eventType = xmlParser.next();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				if ("CustomEventMsg".equals(xmlParser.getName())) {
					this.readCustomEventMsg();
				} else if ("CustomEventParam".equals(xmlParser.getName())) {
					this.readCustomEventParam();
				}
				break;
			case XmlPullParser.END_TAG:
				if ("CustomEventList".equals(xmlParser.getName())) {
					return;
				}
				break;
			}
			eventType = xmlParser.next();
		}
	}

	/**
	 * 初始化自定义参数和自定义事件列表,旧格式，新格式采用xml
	 */
	private boolean readTextCustomEvent() {
		File file = AppFilePathUtil.getInstance().getSDCardBaseFile(CUSTOM_FILE_NAME);
		if (!file.exists()) {
			return false;
		}
		FileInputStream inStream = null;
		String line;
		BufferedReader reader = null;
		try {
			inStream = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
			while (true) {
				line = reader.readLine();
				if (line == null || line.indexOf("</custom>") == 0)
					break;
				line = line.trim();
				System.out.println("line1 = " + line);
				if (line.length() == 0 || line.indexOf("<custom>") == 0)
					continue;
				System.out.println("line2 = " + line);
				String[] param = line.split(",");
				try {
					int compare = Integer.parseInt(param[3]);
					int interval = Integer.parseInt(param[4]);
					if (interval > customMaxDelay) {
						customMaxDelay = interval;
					}
					long l3MsgID1 = Long.parseLong(param[1], 16);
					long l3MsgID2 = Long.parseLong(param[2], 16);
					String name = param[0].trim();
					boolean isShowAlarm = param[5].equals("1");
					boolean isShowChart = param[6].equals("1");
					boolean isShowMap = param[7].equals("1");
					boolean isShowTotal = param[8].equals("1");
					String iconFilePath = param[9];
					String params = param[10];
					int duration = Integer.parseInt(param[11]);
					int type = Integer.parseInt(param[12]);
					CustomEvent event = null;
					if (type == CustomEvent.TYPE_MSG) {
						CustomEventMsg define = new CustomEventMsg();
						define.setCompare(compare);
						define.setInterval(interval);
						define.setL3MsgID1(l3MsgID1);
						define.setL3MsgID2(l3MsgID2);
						event = define;
						this.customMsgList.add(define);
					} else {
						CustomEventParam define = new CustomEventParam();
						define.setParams(params);
						define.setDuration(duration);
						event = define;
						this.customParamList.add(define);
					}
					event.setName(name);
					event.setShowAlarm(isShowAlarm);
					event.setShowChart(isShowChart);
					event.setShowMap(isShowMap);
					event.setShowTotal(isShowTotal);
					event.setIconFilePath(iconFilePath);
					this.customMap.put(event.getName(), event);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();
				if (inStream != null)
					inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * 统计自定义事件列表
	 * 
	 * @return
	 */
	public List<TotalCustomModel> getTotalEventList() {
		return this.totalEventList;
	}

	/**
	 * 注册信息处理句柄
	 * 
	 * @param handler
	 *          信息处理句柄
	 */
	public void regeditHandler(Handler handler) {
		this.handlerSet.add(handler);
	}

	/**
	 * 注销信息处理句柄
	 * 
	 * @param handler
	 *          信息处理句柄
	 */
	public void UnregeditHandler(Handler handler) {
		this.handlerSet.remove(handler);
	}

	/**
	 * 添加一个自定义信令事件
	 * 
	 * @param custom
	 *          自定义信令事件对象
	 */
	public boolean addCustomEventMsg(CustomEventMsg custom) {
		this.customMsgList.add(custom);
		this.customMap.put(custom.getName(), custom);
		this.obtainMessage(MSG_CUSTOM_EVENT_ADD);
		this.writeXmlToFile();
		return false;
	}

	/**
	 * 添加一个自定义参数事件
	 * 
	 * @param custom
	 *          自定义参数事件对象
	 */
	public boolean addCustomEventParams(CustomEventParam custom) {
		this.customParamList.add(custom);
		this.customMap.put(custom.getName(), custom);
		this.obtainMessage(MSG_CUSTOM_EVENT_ADD);
		this.writeXmlToFile();
		return false;
	}

	/**
	 * 发送信息
	 * 
	 * @param what
	 *          信息ID
	 */
	private void obtainMessage(int what) {
		if (!this.handlerSet.isEmpty()) {
			for (Handler handler : this.handlerSet) {
				handler.obtainMessage(what).sendToTarget();
			}
		}
	}

	/**
	 * 删除选中的自定义信令事件
	 * 
	 */
	public void removeCustomEventMsgs() {
		for (int i = 0; i < this.customMsgList.size(); i++) {
			CustomEventMsg define = this.customMsgList.get(i);
			if (this.removeSet.contains(define)) {
				this.customMsgList.remove(i);
				this.customMap.remove(define.getName());
				i--;
			}
		}
		this.removeSet.clear();
		this.obtainMessage(MSG_CUSTOM_EVENT_DELETE);
		this.writeXmlToFile();
	}

	/**
	 * 删除选中的自定义参数事件
	 * 
	 */
	void removeCustomEventParams() {
		for (int i = 0; i < this.customParamList.size(); i++) {
			CustomEventParam define = this.customParamList.get(i);
			if (this.removeSet.contains(define)) {
				this.customParamList.remove(i);
				this.customMap.remove(define.getName());
				i--;

			}
		}
		this.removeSet.clear();
		this.obtainMessage(MSG_CUSTOM_EVENT_DELETE);
		this.writeXmlToFile();
	}

	/**
	 * 编辑自定义事件
	 * 
	 * @param define
	 *          自定义信令事件对象
	 */
	public void editCustomEvent( CustomEvent define) {
		if (!this.customMap.containsKey(define.getOldName()))
			return;
		if (!define.getName().equals(define.getOldName())) {
			this.customMap.remove(define.getOldName());
			this.customMap.put(define.getName(), define);
		}
		this.obtainMessage(MSG_CUSTOM_EVENT_EDIT);
		this.writeXmlToFile();
	}

	/**
	 * 根据名字判断是否存在该自定义
	 * 
	 * @param eventName
	 *          事件名称
	 * @return
	 */
	public boolean hasCustomEvent(String eventName) {
		if (this.customMap.containsKey(eventName))
			return true;
		return false;
	}

	/**
	 * 获得自定义信令事件列表
	 * 
	 */
	public List<CustomEventMsg> getCustomEventMsgList() {
		return this.customMsgList;
	}

	/**
	 * 获得自定义参数事件列表
	 */
	public List<CustomEventParam> getCustomEventParamList() {
		return this.customParamList;
	}

	/**
	 * 获得所有的自定义事件列表
	 * 
	 * @return
	 */
	public List<CustomEvent> getCustomEventList() {
		List<CustomEvent> list = new ArrayList<CustomEvent>();
		list.addAll(this.customMsgList);
		list.addAll(this.customParamList);
		return list;
	}

	/**
	 * 根据名字返回自定义事件
	 * 
	 * @param eventName
	 *          事件名称
	 * @return 没有时返回null
	 */
	public CustomEvent getCustomDefine(String eventName) {
		if (this.customMap.containsKey(eventName))
			return this.customMap.get(eventName);
		return null;
	}

	@SuppressWarnings("unchecked")
	public void queryCustomTotal() {
		totalEventList.clear();
		// 事件名作为KEY的HashMap
		Map<String, Map<String, Map<String, Long>>> specialTimesHM = TotalDataByGSM.getInstance().getSpecialTimes();
		// specialTime的内容如下
		// {接通时间长={1382799844530={_latitude=0, _longitude=0, _delay=7706},
		// 1382800132075={_latitude=0, _longitude=0, _delay=7443},
		// 1382799959405={_latitude=0, _longitude=0, _delay=7399},
		// 1382799786891={_latitude=0, _longitude=0, _delay=7473},
		// 1382799671888={_latitude=0, _longitude=0, _delay=7496},
		// 1382799902189={_latitude=0, _longitude=0, _delay=7617},
		// 1382800075015={_latitude=0, _longitude=0, _delay=7956},
		// 1382800189950={_latitude=0, _longitude=0, _delay=7696},
		// 1382800017211={_latitude=0, _longitude=0, _delay=7448},
		// 1382799729448={_latitude=0, _longitude=0, _delay=7489}},
		// Cm service Req={1382800008903={_latitude=0, _longitude=0, _delay=0},
		// 1382799951395={_latitude=0, _longitude=0, _delay=0},
		// 1382800123994={_latitude=0, _longitude=0, _delay=0},
		// 1382799778769={_latitude=0, _longitude=0, _delay=0},
		// 1382800181595={_latitude=0, _longitude=0, _delay=0},
		// 1382800066410={_latitude=0, _longitude=0, _delay=0},
		// 1382799663751={_latitude=0, _longitude=0, _delay=0},
		// 1382799721339={_latitude=0, _longitude=0, _delay=0},
		// 1382799836196={_latitude=0, _longitude=0, _delay=0},
		// 1382799893914={_latitude=0, _longitude=0, _delay=0}}}
		Set<String> keySet = specialTimesHM.keySet();
		for (String key : keySet) {
			// 找出Key为自定义事件的Map
			CustomEvent define = getCustomDefine(key);
			if (define != null) {
				TotalCustomModel total = new TotalCustomModel();
				total.setName(key);
				Map<String, Map<String, Long>> eventHM = (HashMap<String, Map<String, Long>>) specialTimesHM.get(key);
				setCusomTotalInfoList(total, eventHM);
				this.totalEventList.add(total);
			}
		}
	}

	/**
	 * 设置自定义事件统计信息列表
	 * 
	 * @param totalModel
	 *          统计对象
	 * @param eventHM 事件值映射
	 */
	private void setCusomTotalInfoList(TotalCustomModel totalModel, Map<String, Map<String, Long>> eventHM) {
		List<Map.Entry<String, Map<String, Long>>> listEvent = new ArrayList<Map.Entry<String, Map<String, Long>>>(
				eventHM.entrySet());

		/**
		 * 按时间键值排序
		 */
		Collections.sort(listEvent, new Comparator<Map.Entry<String, Map<String, Long>>>() {
			public int compare(Map.Entry<String, Map<String, Long>> o1, Map.Entry<String, Map<String, Long>> o2) {
				// return (o2.getValue() - o1.getValue());
				return (o1.getKey()).toString().compareTo(o2.getKey());
			}
		});

		// while (iter.hasNext()) {
		for (Map.Entry<String, Map<String, Long>> entry : listEvent) {
			String time = (String) entry.getKey();
			TotalCustomModel.OneEvent info = totalModel.InfoBuilder();
			info.time = Long.parseLong(time);
			Map<String, Long> valHM = entry.getValue();
			info.delay = Integer.parseInt(valHM.get(TotalCustomEvent._delay.name()) + "");
			info.latitude = valHM.get(TotalCustomEvent._latitude.name());
			info.logitude = valHM.get(TotalCustomEvent._longitude.name());
			totalModel.addEventSortTime(info);
		}
	}

	/**
	 * 根据名字返回统计详情
	 * 
	 * @param name
	 *          事件名称
	 * @return
	 */
	public TotalCustomModel getCustomTotalByName(String name) {
		for (TotalCustomModel total : totalEventList) {
			if (total.getName().equals(name)) {
				return total;
			}
		}
		return null;
	}

	/**
	 * 保存自定义参数和信令到文件中
	 * 
	 */
	private void writeXmlToFile() {
		/*List<CustomEvent> list = this.getCustomEventList();
		if (list.isEmpty())
			return;*/
		FileOutputStream os = null;
		try {
			File file = AppFilePathUtil.getInstance().createSDCardBaseFile(CUSTOM_FILE_XML_NAME);
			os = new FileOutputStream(file);
			this.serializer = Xml.newSerializer();
			this.serializer.setOutput(os, "UTF-8");
			this.serializer.startDocument("UTF-8", true);
			this.serializer.startTag(null, "CustomEventList");
			for (CustomEventMsg define : this.customMsgList) {
				this.writeXmlCustomEventMsg(define);
			}
			for (CustomEventParam define : this.customParamList) {
				this.writeXmlCustomEventParam(define);
			}
			this.serializer.endTag(null, "CustomEventList");
			this.serializer.endDocument();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
					os = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 写入自定义信令事件到文件中
	 * 
	 * @param define
	 *          自定义信令事件
	 */
	private void writeXmlCustomEventMsg(CustomEventMsg define) throws Exception {
		this.serializer.startTag(null, "CustomEventMsg");
		this.writeAttributeValue("Compare", String.valueOf(define.getCompare()));
		this.writeAttributeValue("Interval", String.valueOf(define.getInterval()));
		this.writeAttributeValue("L3MsgID1", String.valueOf(define.getL3MsgID1()));
		this.writeAttributeValue("L3MsgID2", String.valueOf(define.getL3MsgID2()));
		this.writeAttributeValue("Name", define.getName());
		this.writeAttributeValue("ShowChart", Boolean.toString(define.isShowChart()));
		this.writeAttributeValue("ShowMap", Boolean.toString(define.isShowMap()));
		this.writeAttributeValue("ShowAlarm", Boolean.toString(define.isShowAlarm()));
		this.writeAttributeValue("ShowTotal", Boolean.toString(define.isShowTotal()));
		this.writeAttributeValue("IconFilePath", define.getIconFilePath());
		this.serializer.endTag(null, "CustomEventMsg");
		if (define.getInterval() > customMaxDelay) {
			customMaxDelay = define.getInterval();
		}
	}

	/**
	 * 写入自定义参数事件到文件中
	 * 
	 * @param define
	 *          自定义参数事件
	 */
	private void writeXmlCustomEventParam(CustomEventParam define) throws Exception {
		this.serializer.startTag(null, "CustomEventParam");
		this.writeAttributeValue("Name", define.getName());
		this.writeAttributeValue("ShowChart", Boolean.toString(define.isShowChart()));
		this.writeAttributeValue("ShowMap", Boolean.toString(define.isShowMap()));
		this.writeAttributeValue("ShowAlarm", Boolean.toString(define.isShowAlarm()));
		this.writeAttributeValue("ShowTotal", Boolean.toString(define.isShowTotal()));
		this.writeAttributeValue("IconFilePath", define.getIconFilePath());
		this.writeAttributeValue("Params", define.getParamString());
		this.writeAttributeValue("Duration", String.valueOf(define.getDuration()));
		this.serializer.endTag(null, "CustomEventParam");
	}

	/**
	 * 写属性值
	 * 
	 * @param name
	 *          属性名
	 * @param value
	 *          属性值
	 * @throws Exception
	 */
	private void writeAttributeValue(String name, String value) throws Exception {
		this.serializer.attribute(null, name, value);
	}

	/**
	 * 指定类型的事件是否有勾选项
	 * 
	 * @param type
	 *          类型
	 * @return
	 */
	public boolean hasChecked(int type) {
		return !this.removeSet.isEmpty();
	}

	public Set<CustomEvent> getRemoveSet() {
		return removeSet;
	}
}
