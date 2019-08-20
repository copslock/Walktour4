package com.walktour.workorder.parser;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;

/**
 * xml文件解析，采用Pull方法解析，防止占用内存
 * 注意1：抽象类，描述Pull解析步骤
 * Author: ZhengLei
 *   Date: 2013-6-7 上午11:53:15
 */
public abstract class XmlParser {
	private String fileName; // xml文件名
	private InputStream inputStream; // 文件的输入流
	private Reader reader; // Reader对象，用于parser.setInput(reader)
	protected XmlPullParser parser; // 解析器
	protected int eventType; // 解析的事件类型，为XmlPullParser的常量。如开始文档、开始标签等
//	protected List<Object> parseResult; // 解析结果
	
	// 三个构造方法
	public XmlParser(InputStream inputStream) {
		this.inputStream = inputStream;
		initParser();
	}
	
	public XmlParser(Reader reader) {
		this.reader = reader;
		initParser();
	}
	
	public XmlParser(String fileName) {
		try {
			this.fileName = fileName;
			this.inputStream = new FileInputStream(this.fileName);
			initParser();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析方法，解析结果放在parseResult中
	 */
	public abstract void parse();
	
	public abstract Object getParseResult();
	
	/**
	 * 初始化XmlPullParser
	 */
	private void initParser() {
		try {
			parser = Xml.newPullParser();
//			parser.setInput(reader);
			parser.setInput(inputStream, "UTF-8");
			this.eventType = parser.getEventType();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 解析xml中的整型，但是可能会为空或者为字符，将会造成异常
	 * @param value 要转化的字符串
	 * @param defaultVal 如果异常，则默认值设置为什么
	 * @return 转化后的整型
	 */
	protected int parseInt(String value, int defaultVal) {
		int result;
		try {
			result = Integer.parseInt(value);
			// 0为无效值，需要改为默认值
			if(result == 0) {
				result = defaultVal;
			}
		} catch (Exception e) {
			result = defaultVal;
		}
		return result;
	}

}
