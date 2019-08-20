package com.walktour.gui.task.parsedata.model.base;

import org.xmlpull.v1.XmlSerializer;

import java.io.Serializable;

/***
 * 任务基类
 * 
 * @author weirong.fan
 *
 */
public abstract class TaskBase  implements Serializable{
	private static final long serialVersionUID = -3363795286608691102L;

	/**
	 * boolean值转字符串、
	 * 
	 * @param value
	 * @return
	 */
	public String boolToText(boolean value) {
		return value ? "True" : "False";
	}

	/***
	 * String值转化为bool值
	 * 
	 * @param value
	 * @return
	 */
	public boolean stringToBool(String value) {
		if (null == value || value.equalsIgnoreCase("false") || value.equalsIgnoreCase("0")) {
			return false;
		}
		return true;
	}

	/**
	 * 字符串或空值转化为整形
	 * @param value
	 * @return
	 */
	public int stringToInt(String value) {
		if (null == value)
			return 0;
		try {
			Integer i = Integer.parseInt(value);
			return i;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return 0;
	}

	/**
	 * 写入单行Xml tag数据
	 * 
	 * @param serializer
	 *            写入器
	 * @param name
	 *            名称
	 * @param value
	 *            字符串
	 * @throws Exception
	 */
	public void writeTag(XmlSerializer serializer, String name, String value) throws Exception {
		serializer.startTag(null, name).text(value == null ? "" : value).endTag(null, name);
	}

	/**
	 * 写入单行Xml tag数据
	 * 
	 * @param serializer
	 *            写入器
	 * @param name
	 *            名称
	 * @param value
	 *            Boolean值
	 * @throws Exception
	 */
	public void writeTag(XmlSerializer serializer, String name, Boolean value) throws Exception {
		serializer.startTag(null, name).text(boolToText(value)).endTag(null, name);
	}

	/**
	 * 写入单行Xml tag数据
	 * 
	 * @param serializer
	 *            写入器
	 * @param name
	 *            名称
	 * @param value
	 *            数值
	 * @throws Exception
	 */
	public void writeTag(XmlSerializer serializer, String name, int value) throws Exception {
		serializer.startTag(null, name).text(value + "").endTag(null, name);
	}

	/**
	 * 写入单行Xml attribute数据
	 * 
	 * @param serializer
	 *            写入器
	 * @param name
	 *            名称
	 * @param value
	 *            字符串
	 * 
	 * @throws Exception
	 */
	public void writeAttribute(XmlSerializer serializer, String name, String value) throws Exception {
		serializer.attribute(null, name, value == null ? "" : value);
	}

	/**
	 * 写入单行Xml attribute数据
	 * 
	 * @param serializer
	 *            写入器
	 * @param name
	 *            名称
	 * @param value
	 *            字符串
	 * 
	 * @throws Exception
	 */
	public void writeAttribute(XmlSerializer serializer, String name, int value) throws Exception {
		serializer.attribute(null, name, value + "");
	}

	/**
	 * 写入单行Xml attribute数据
	 * 
	 * @param serializer
	 *            写入器
	 * @param name
	 *            名称
	 * @param value
	 *            字符串
	 * 
	 * @throws Exception
	 */
	public void writeAttribute(XmlSerializer serializer, String name, boolean value) throws Exception {
		serializer.attribute(null, name, boolToText(value) + "");
	}
}
