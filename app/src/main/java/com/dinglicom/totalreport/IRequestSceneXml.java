package com.dinglicom.totalreport;

/**
 * 操作统计请求场景XML接口类
 * @author zhihui.lian
 */

public interface IRequestSceneXml {
	
	//创建场景XML
	public abstract boolean createXml(DLMessageModel dlMessageModel);
	//解析场景XML
	public abstract boolean parseXml(String name);

}
