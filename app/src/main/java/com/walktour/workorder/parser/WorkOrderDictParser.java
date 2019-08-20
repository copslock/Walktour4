package com.walktour.workorder.parser;

import com.walktour.workorder.model.WorkOrderDict;
import com.walktour.workorder.model.WorkOrderDict.WorkType;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;


/**
 * 工单字典解析器
 * Author: ZhengLei
 *   Date: 2013-6-7 下午1:12:47
 */
public class WorkOrderDictParser extends XmlParser {
	private WorkOrderDict workOrderDict = null;
	
	public WorkOrderDictParser(Reader reader) {
		super(reader);
	}

	public WorkOrderDictParser(String fileName) {
		super(fileName);
	}

	@Override
	public void parse() {
		WorkType workType = null;
		
		try {
			while(eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
					case XmlPullParser.START_DOCUMENT: // 判断当前事件是否是文档开始事件
						workOrderDict = new WorkOrderDict();
						break;
					case XmlPullParser.START_TAG: // 判断当前事件是否是标签元素开始事件
						if("WorkType".equals(parser.getName())) { // 判断开始标签元素是否是WorkType
							workType = workOrderDict.new WorkType(); // new 内部类
						}
						if(workType != null) {
							if("CodeId".equals(parser.getName())) {
								workType.setCodeId(Integer.parseInt(parser.nextText()));
							} else if("EnName".equals(parser.getName())) {
								workType.setEnName(parser.nextText());
							} else if("CnName".equals(parser.getName())) {
								workType.setCnName(parser.nextText());
							} else if("Count".equals(parser.getName())) {
								workType.setCount(parseInt(parser.nextText(), 0));
							}
						}
						break;
					case XmlPullParser.END_TAG: // 判断当前事件是否是标签元素结束事件
						if("WorkType".equals(parser.getName())) { // 判断结束标签元素是否是WorkType
							workOrderDict.getWorkTypes().add(workType);
							workType = null;
						}
						break;
					default:
						break;
				} // end switch
				
				// 进入下一个元素并触发相应事件
				eventType = parser.next();
				
			} // end while
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 获取解析完的工单字典
	 * @return 工单字典
	 */
	@Override
	public WorkOrderDict getParseResult() {
		return workOrderDict;
	}

}
