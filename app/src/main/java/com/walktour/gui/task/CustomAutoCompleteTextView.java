package com.walktour.gui.task;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * 自定义搜索TextView
 * @author zhihui.lian
 *
 */
public class CustomAutoCompleteTextView extends AutoCompleteTextView {
    

	public CustomAutoCompleteTextView(Context context) {
        super(context);
    }
    
    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        super.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.normal_txt_size));
    }
    
    public CustomAutoCompleteTextView(Context context, AttributeSet attrs,int defStyle) {
        super(context, attrs, defStyle);
//        super.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.normal_txt_size));
    }
    
    @Override
    public boolean enoughToFilter() {
        return true;
    }
    
	@Override
	protected void onFocusChanged(boolean focused, int direction,Rect previouslyFocusedRect) {
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
//		performFiltering(getText(), KeyEvent.KEYCODE_UNKNOWN);
		
	}
	
	@Override
	public void setText(CharSequence text, BufferType type) {
		// TODO Auto-generated method stub
		super.setText(text, type);
	}

}
