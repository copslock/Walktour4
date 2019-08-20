/*
 * 文件名: LocusParamInfo.java
 * 版    权：  Copyright Dingli Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2013-9-15
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.model;

import java.io.Serializable;

/**
 * 轨迹相关参数信息<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2013-9-15] 
 */
public class LocusParamInfo implements Serializable{
    
    public String paramName;
    
    public int color = 0xFF000000;
    
    public double value = 0;
    
}
