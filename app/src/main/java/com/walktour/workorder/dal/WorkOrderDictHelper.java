package com.walktour.workorder.dal;

import com.dinglicom.UnicomInterface;
import com.walktour.workorder.model.XmlFileType;

/**
 * 工单字典的数据访问层类
 * Author: ZhengLei
 *   Date: 2013-6-19 下午5:31:42
 */
public class WorkOrderDictHelper extends BaseHelper {
	public WorkOrderDictHelper() {
		super();
	}
	
	@Override
	public String getXmlByLib() {
		return UnicomInterface.getWorkTypeDict2();
	}

	@Override
	public boolean exist(String fileName) {
		return super.exist(fileName);
	}

	@Override
	public boolean saveXmlAsFile(String content, String fileName) {
		return super.saveXmlAsFile(content, fileName);
	}

	@Override
	public Object getContentFromFile(String fileName, XmlFileType type) {
		return super.getContentFromFile(fileName, type);
	}

}
