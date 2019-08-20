package com.walktour.framework.view;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.walktour.framework.ui.BasicActivityGroup;
import com.walktour.gui.R;

import java.lang.reflect.Method;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2013-1-29]
 */
public class TotalScrollTabActivity extends BasicActivityGroup implements LayoutChangeListener {

	/**
	 * 父Layout
	 */
	public RelativeLayout parnetLay;

	/**
	 * 滑动Layout
	 */
	private ScrollLayout scrollLay;

	/**
	 * 切换标记Layout
	 */
	private LinearLayout swicthLay;

	private DisplayMetrics metrics;

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param savedInstanceState
	 * @see com.walktour.framework.ui.BasicActivity#onCreate(android.os.Bundle)
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		metrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		parnetLay = new RelativeLayout(this);
		RelativeLayout.LayoutParams parnetParam = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.MATCH_PARENT);
		// parnetParam.setMargins(0, (int) (8 * metrics.density), 0, 0);
		parnetLay.setLayoutParams(parnetParam);
		parnetLay.setBackgroundColor(getResources().getColor(R.color.param_view_bg_color));

		scrollLay = new ScrollLayout(this);
		RelativeLayout.LayoutParams scrollParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.MATCH_PARENT);
		scrollParams.setMargins(0, 0, 0, (int) (4 * metrics.density));
		parnetLay.addView(scrollLay, scrollParams);

		RelativeLayout.LayoutParams swicthParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		swicthLay = new LinearLayout(this);
		swicthLay.setOrientation(LinearLayout.HORIZONTAL);
		swicthParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		swicthParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		swicthLay.setLayoutParams(swicthParams);
		scrollLay.addChangeListener(this);
		setContentView(parnetLay);
	}

	/**
	 * 添加可滑动View<BR>
	 * [功能详细描述]
	 * 
	 * @param view
	 *          子view
	 */
	public void addChildView(View view) {
		scrollLay.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	/**
	 * 刷新当前页标记图片<BR>
	 * 用于创建切换标记
	 * 
	 * @param currentPage
	 */
	public void refreshSwicthTag(int currentPage) {
		swicthLay.removeAllViews();
		parnetLay.removeView(swicthLay);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.setMargins(4, 0, 0, 0);
		for (int i = 0; i < scrollLay.getChildCount(); i++) {
			ImageView swicthTag = new ImageView(this);
			swicthTag.setLayoutParams(params);
			swicthTag.setBackgroundResource(i == currentPage ? R.drawable.lightdot : R.drawable.darkdot);
			swicthLay.addView(swicthTag);
		}
		parnetLay.addView(swicthLay);
	}

	/**
	 * 滑动Layout切换事件<BR>
	 * [功能详细描述]
	 * 
	 * @param lastIndex
	 *          上页
	 * @param currentIndex
	 *          当前页
	 * @see com.walktour.framework.view.LayoutChangeListener#doChange(int, int)
	 */
	@Override
	public void doChange(int lastIndex, int currentIndex) {
		refreshSwicthTag(currentIndex);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		setIconEnable(menu, true);
		return true;
	}

	// enable为true时，菜单添加图标有效，enable为false时无效。4.0系统默认无效
	private void setIconEnable(Menu menu, boolean enable) {
		try {
			Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
			Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
			m.setAccessible(true);

			// MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
			m.invoke(menu, enable);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
