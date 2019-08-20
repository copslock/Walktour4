package com.walktour.workorder.dal;

import com.walktour.Utils.UtilsMethod;
import com.walktour.workorder.model.XmlFileType;
import com.walktour.workorder.parser.ServerInfoParser;
import com.walktour.workorder.parser.WorkOrderDetailParser;
import com.walktour.workorder.parser.WorkOrderDictParser;
import com.walktour.workorder.parser.WorkOrderListParser;
import com.walktour.workorder.parser.XmlParser;

/**
 * 数据访问层的助手基类
 * Author: ZhengLei
 *   Date: 2013-6-20 上午10:05:06
 */
public abstract class BaseHelper implements IHelper {
	private XmlParser xmlParser;

	@Override
	public boolean exist(String fileName) {
		return UtilsMethod.existFile(fileName);
	}

//	public abstract String getXmlByLib();

	@Override
	public boolean saveXmlAsFile(String content, String fileName) {
		return UtilsMethod.WriteFile(fileName, content);
	}

	@Override
	public Object getContentFromFile(String fileName, XmlFileType type) {
		if(fileName==null || "".equals(fileName)) {return null;}
		
		switch (type) {
			case TypeServerInfo:
				xmlParser = new ServerInfoParser(fileName);
				break;
			case TypeWorkOrderDict:
				xmlParser = new WorkOrderDictParser(fileName);
				break;
			case TypeWorkOrderList:
				xmlParser = new WorkOrderListParser(fileName);
				break;
			case TypeWorkOrderDetail:
				xmlParser = new WorkOrderDetailParser(fileName);
				break;
	
			default:
				break;
		}
		xmlParser.parse();
		return xmlParser.getParseResult();
	}

}
