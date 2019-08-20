package com.walktour.workorder.bll;

import com.walktour.workorder.dal.IHelper;
import com.walktour.workorder.model.XmlFileType;

/**
 * 从数据访问层获取数据的基类
 * Author: ZhengLei
 *   Date: 2013-6-20 上午10:58:19
 */
public abstract class BaseManipulate implements IManipulate {
	protected IHelper mHelper = null;
	protected XmlFileType xmlType;
	protected String fileName;

	public BaseManipulate() {
		super();
	}

	@Override
	public Object load() {
		boolean isExist = mHelper.exist(fileName);
		// 先判断手机是否存在改xml文件，存在则加载
		if(!isExist) {
			return null;
		}
		return mHelper.getContentFromFile(fileName, xmlType);
	}

	@Override
	public Object synchronize() {
		String xmlContent = mHelper.getXmlByLib();
		if(xmlContent==null || "".equals(xmlContent)) {
			return null;
		}
		mHelper.saveXmlAsFile(xmlContent, fileName);
		return mHelper.getContentFromFile(fileName, xmlType);
	}

}
