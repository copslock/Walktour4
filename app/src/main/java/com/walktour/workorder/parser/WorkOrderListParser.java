package com.walktour.workorder.parser;

import com.walktour.workorder.model.WorkOrderList;
import com.walktour.workorder.model.WorkOrderList.WorkOrderInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;


/**
 * 工单列表解析器
 * Author: ZhengLei
 *   Date: 2013-6-7 下午3:12:54
 */
public class WorkOrderListParser extends XmlParser {
	private WorkOrderList workOrderList;
	
	public WorkOrderListParser(Reader reader) {
		super(reader);
	}

	public WorkOrderListParser(String fileName) {
		super(fileName);
	}

	@Override
	public void parse() {
		WorkOrderInfo workOrderInfo = null;
		try {
			while(eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
					case XmlPullParser.START_DOCUMENT: // 判断当前事件是否是文档开始事件
						workOrderList = new WorkOrderList();
						break;
					case XmlPullParser.START_TAG: // 判断当前事件是否是标签元素开始事件
						if("WorkOrderInfo".equals(parser.getName())) { // 判断开始标签元素是否是WorkOrderInfo
							workOrderInfo = workOrderList.new WorkOrderInfo(); // new 内部类
						}
						if(workOrderInfo != null) {
							if("WorkID".equals(parser.getName())) {
								workOrderInfo.setWorkId(Integer.parseInt(parser.nextText()));
							} else if("WorkName".equals(parser.getName())) {
								workOrderInfo.setWorkName(parser.nextText());
							} else if("WorkArea".equals(parser.getName())) {
								workOrderInfo.setWorkArea(Integer.parseInt(parser.nextText()));
							} else if("WorkType".equals(parser.getName())) {
								workOrderInfo.setWorkType(Integer.parseInt(parser.nextText()));
							} else if("ProjectID".equals(parser.getName())) {
								workOrderInfo.setProjectId(Integer.parseInt(parser.nextText()));
							} else if("ProjectName".equals(parser.getName())) {
								workOrderInfo.setProjectName(parser.nextText());
							} else if("PlanEndTime".equals(parser.getName())) {
								workOrderInfo.setPlanEndTime(parser.nextText());
							} else if("SenderAccount".equals(parser.getName())) {
								workOrderInfo.setSenderAccount(parser.nextText());
							} else if("ProvinceID".equals(parser.getName())) {
								workOrderInfo.setProvinceId(Integer.parseInt(parser.nextText()));
							} else if("CityID".equals(parser.getName())) {
								workOrderInfo.setCityId(Integer.parseInt(parser.nextText()));
							} else if("AreaID".equals(parser.getName())) {
								workOrderInfo.setAreaId(Integer.parseInt(parser.nextText()));
							} else if("KmSum".equals(parser.getName())) {
								workOrderInfo.setKmSum(Integer.parseInt(parser.nextText()));
							} else if("NetType".equals(parser.getName())) {
								workOrderInfo.setNetType(Integer.parseInt(parser.nextText()));
							} else if("isrecived".equals(parser.getName())) {
								workOrderInfo.setIsReceived(Integer.parseInt(parser.nextText()));
							}
						}
						break;
					case XmlPullParser.END_TAG: // 判断当前事件是否是标签元素结束事件
						if("WorkOrderInfo".equals(parser.getName())) { // 判断结束标签元素是否是WorkOrderInfo
							workOrderList.getWorkOrderInfos().add(workOrderInfo);
							workOrderInfo = null;
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

	@Override
	public WorkOrderList getParseResult() {
		return workOrderList;
	}

}
