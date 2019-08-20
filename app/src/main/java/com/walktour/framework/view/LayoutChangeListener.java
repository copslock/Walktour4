/*
 * 文件名: LayoutChangeListener.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2012-6-20
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.framework.view;

public interface LayoutChangeListener {
    
    /**
     * 更换屏幕实现接口类<BR>
     * 实现屏幕切换
     * @param lastIndex 将来移动的屏幕
     * @param currentIndex 当前所在的屏幕
     */
	public void doChange(int lastIndex, int currentIndex);
}
