package com.walktour.workorder.dal;

import com.dinglicom.UnicomInterface;
import com.walktour.workorder.model.XmlFileType;

/**
 * 工单列表的数据访问层类
 * Author: ZhengLei
 *   Date: 2013-6-20 下午3:29:03
 */
public class WorkOrderListHelper extends BaseHelper {
	// 指明是哪个工单字典对应的工单列表
	private int workOrderType;
	
	/**
	 * 构造方法
	 * @param workOrderType
	 */
	public WorkOrderListHelper(int workOrderType) {
		this.workOrderType = workOrderType;
	}
	
	@Override
	public String getXmlByLib() {
		return UnicomInterface.getWorkOrderList(workOrderType);
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
