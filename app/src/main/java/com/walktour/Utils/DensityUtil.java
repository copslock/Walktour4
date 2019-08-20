package com.walktour.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

public class DensityUtil {
    /** 
     * 镙规嵁镓嬫満镄勫垎杈ㄧ巼浠?dp 镄勫崟浣?杞垚涓?px(镀忕礌) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 镙规嵁镓嬫満镄勫垎杈ㄧ巼浠?px(镀忕礌) 镄勫崟浣?杞垚涓?dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }

	public static int getViewMeasuredHeight(TextView tv) {
		// TODO Auto-generated method stub
		measureView(tv);
		return tv.getMeasuredHeight();
	}  
	
	public static int getViewMeasuredHeight(View view) {
		// TODO Auto-generated method stub
		measureView(view);
		return view.getMeasuredHeight();
	}
	
	public static int getViewMeasuredWidth(View view) {
		// TODO Auto-generated method stub
		measureView(view);
		return view.getMeasuredWidth();
	}
	/**
	 * 娴嬮噺杩欎釜view
	 * 链€鍚庨€氲绷getMeasuredWidth()銮峰彇瀹藉害鍜岄佩锟?
	 * @param view 瑕佹祴閲忕殑view
	 * @return 娴嬮噺杩囩殑view
	 */
	public static void measureView(View view) {
		ViewGroup.LayoutParams p = view.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		view.measure(childWidthSpec, childHeightSpec);
	}

	@SuppressWarnings("deprecation")
	public static int getScreenHeight(Activity context) {
		WindowManager wm = context.getWindowManager();
		 
	     int width = wm.getDefaultDisplay().getWidth();
	     int height = wm.getDefaultDisplay().getHeight();
		return height;	
	}
	
	/**
	 * 璁＄畻鏂囧瓧楂桦害
	 * 
	 * @param fontSize
	 *            浠ュ澶х殑瀛椾綋璁＄畻
	 * @return
	 */
	public static float getFontHeight(Context context, float fontSize) {
		float density = context.getResources().getDisplayMetrics().density;
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		FontMetrics fm = paint.getFontMetrics();
		return (float) Math.ceil(fm.descent - fm.ascent) - 6 * density;
	}
}
