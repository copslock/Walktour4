/**
 * com.walktour.gui.applet
 * FileListView.java
 * 类功能：
 * 2014-5-26-上午11:13:16
 * 2014鼎利-版权所有
 * @author qihang.li@dinglicom.com
 */
package com.walktour.gui.applet;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * FileListView
 * 点击文件管理ListView一个Item展开后嵌套的ListView
 * 2014-5-26 上午11:13:16
 * 
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
public class FileListView extends ListView {
	/**
	 * 创建一个新的实例 FileListView.
	 *
	 * @param context
	 */
	public FileListView(Context context) {
		super(context);
	}
	
	public FileListView(Context context, AttributeSet attrs){
		super(context,attrs);
	}
	
	public FileListView(Context context, AttributeSet attrs,int defStyle){
		super(context,attrs,defStyle);
	}
	
	@Override
	/**
	 * 重载onMesure方法,使展开后的文件类型列表能达到其最高高度
	 * @see android.widget.ListView#onMeasure(int, int)
	 */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
