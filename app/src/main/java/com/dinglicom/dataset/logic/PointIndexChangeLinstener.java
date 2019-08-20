/*
 * 文件名: PointIndexChangeLinstener.java
 * 版    权：  Copyright Dingli Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2013-9-14
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.dinglicom.dataset.logic;


/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2013-9-14] 
 */
public interface PointIndexChangeLinstener {
    
    /**
     * 采样点变化回调接口<BR>
     * 所有需要关注采样点变化的界面均可使用该接口
     * 具体方法：
     *      1、实现在接口方法
     *      2、调用DatasetManager.getInstance(context).addPointIndexChangeListener(PointIndexChangeLinstener) 添加回调时间
     * @param pointIndex 当前采样点
     * @param isProgressSkip 是否进度条触发跳转
     */
    void onPointIndexChange(int pointIndex,boolean isProgressSkip );
    
}
