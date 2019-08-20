package com.walktour.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.walktour.Utils.DensityUtil;
import com.walktour.Utils.DesUtil;

/**
 * @author Max
 * @data 2018/11/19
 */
public class ListViewForStartDialog extends ListView
{
    public ListViewForStartDialog(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public ListViewForStartDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ListViewForStartDialog(Context context) {
        super(context);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(DensityUtil.dip2px(getContext(),320),MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}