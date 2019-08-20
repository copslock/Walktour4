/*
 * 文件名: OnTabActivityResultListener.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2012-10-19
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.framework.view;

import android.content.Intent;

/**
 * 解决子Activity无法接收Activity回调的问题 <BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-10-19] 
 */
public interface OnTabActivityResultListener {
    
    public void onTabActivityResult(int requestCode, int resultCode, Intent data);
}
