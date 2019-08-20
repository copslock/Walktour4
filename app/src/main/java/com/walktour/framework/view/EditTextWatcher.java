/*
 * 文件名: EditTextTextWatcher.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2012-8-3
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.framework.view;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-8-3] 
 */
public class EditTextWatcher implements TextWatcher{

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param s
     * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
     */
    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub
        
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param s
     * @param start
     * @param count
     * @param after
     * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
     */
    
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
        
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param s
     * @param start
     * @param before
     * @param count
     * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int, int, int)
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub
        
    }
    
}
