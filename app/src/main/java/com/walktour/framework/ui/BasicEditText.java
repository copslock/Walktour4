/*
 * 文件名: BasicEditText.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2012-11-2
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.framework.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.EditText;

import com.walktour.gui.R;

/**
 * 自定义EditText控件<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-11-2] 
 */
public class BasicEditText extends EditText{
	
	/**
	 * [构造简要说明]
	 * @param context
	 * @param attrs
	 */
	public BasicEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray arry = context.obtainStyledAttributes(attrs, R.styleable.BasicEditText);  
		int max = arry.getInt(R.styleable.BasicEditText_max, -9999);
		int min = arry.getInt(R.styleable.BasicEditText_min, -9999);
		this.setFilters(new InputFilter[]{new LengthFilter (max,min)});
	}
	
    public static class LengthFilter implements InputFilter {
    	
    	private int max;
    	
    	private int min;
    	
        public LengthFilter(int max ,int min) {
        	this.max = max;
        	this.min = min;
        }
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
        	System.out.println("source：" + source   +"   dest:"+ dest);
        	if(max != -9999 && !dest.equals("")){
        		if(Integer.valueOf(dest.toString()) > max){
        			return "";
        		}
        	}
        	
        	if(min != -9999 && !dest.equals("")){
        		if(Integer.valueOf(dest.toString()) < min){
        			return "";
        		}
        	}
        	
        	return source;
        }

    }

}
