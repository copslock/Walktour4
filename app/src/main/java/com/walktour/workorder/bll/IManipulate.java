package com.walktour.workorder.bll;
/**
 * 从数据访问层获取数据的接口，定义逻辑
 * Author: ZhengLei
 *   Date: 2013-6-20 上午10:56:52
 */
public interface IManipulate {
	/**
	 * 从手机SDCard中加载xml，来给UI提供数据
	 * @return
	 */
	public abstract Object load();
	/**
	 * 同步，不管本地有没有，都同步服务器信息到手机本地
	 * @return
	 */
	public abstract Object synchronize();

}
