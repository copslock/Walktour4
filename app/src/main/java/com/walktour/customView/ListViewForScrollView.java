package com.walktour.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * @author Max
 * @data 2018/11/19
 */
public class ListViewForScrollView extends ListView
{
    public ListViewForScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public ListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ListViewForScrollView(Context context) {
        super(context);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}