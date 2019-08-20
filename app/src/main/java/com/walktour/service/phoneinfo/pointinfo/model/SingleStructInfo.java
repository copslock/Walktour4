package com.walktour.service.phoneinfo.pointinfo.model;

import com.walktour.Utils.EventBytes;

/***
 * 单个特殊结构
 * 
 * @author weirong.fan
 *
 */
public final class SingleStructInfo implements Complex {
	/** 特殊结构ID，解码组定义 */
	private int structID;
	/** 特殊结构的标记 **/
	private int structFlag;
	/** 特殊结构内容，每种结构不同，参考解码组文档 **/
	private StructContent structContent;

	/***
	 * 构造函数
	 * 
	 * @param structID
	 * @param structFlag
	 * @param structContent
	 */
	public SingleStructInfo(int structID, int structFlag, StructContent structContent) {
		super();
		this.structID = structID;
		this.structFlag = structFlag;
		this.structContent = structContent;
	}

	@Override
	public void addEventValue(EventBytes eventByte) {
		eventByte.addInteger(structID);
		eventByte.addInteger(structFlag);
		if (null != structContent) {// 没有特殊结构体
			eventByte.addInteger(structContent.getPackageSize());
			structContent.addEventValue(eventByte);
		} else {
			eventByte.addInteger(0);
		}

	}

}
