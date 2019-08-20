package com.walktour.workorder.dal;

import com.walktour.workorder.model.XmlFileType;


/**
 * 数据访问层的助手接口，定义规则
 * Author: ZhengLei
 *   Date: 2013-6-20 上午9:22:02
 */
public interface IHelper {
	public abstract boolean exist(String fileName);
	public abstract String getXmlByLib();
	public abstract boolean saveXmlAsFile(String content, String fileName);
	public abstract Object getContentFromFile(String fileName, XmlFileType type);

}
