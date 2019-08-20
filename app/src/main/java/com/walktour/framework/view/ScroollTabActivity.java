package com.walktour.framework.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.walktour.Utils.TabHostUtil;
import com.walktour.framework.ui.BasicActivityGroup;
import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-6-26] 
 */
public abstract class ScroollTabActivity extends BasicActivityGroup implements LayoutChangeListener,
        OnClickListener {
	
	public int mCurScreen;
	
	public TabHost mTabHost;
    
    /**
     * 滑动View对象
     */
    public ScrollLayout scrollLayout;
    
    public Button tab1;
    
    public Button tab2;
    
    public Button tab3;
    
    public Button tab4;
    
    public Button tab5;
    
    public Button tab6;
    
    public Button tab7;
    
    /**
     * 滑动位置标识图片
     */
    protected ImageView scrollTag;

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param savedInstanceState
     * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
     */
    @SuppressWarnings("deprecation")
		@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        if(mCurScreen == 0){
			if (tab1 != null){
				tab1.setTextColor(Color.WHITE);
        	}
        }
        if (mTabHost!=null){
            updateBtnColor(0);
        }
    }
    
    /**
     * 初始View抽象方法<BR>
     * 初始化控件，所有类需实现此方法
     */
    public abstract void initView();

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param v
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        if (v == tab1) {
            snapToScreen(0);
            mTabHost.setCurrentTab(0);
        } else if (v == tab2) {
            snapToScreen(1);
            mTabHost.setCurrentTab(1);
        } else if (v == tab3) {
        	 mTabHost.setCurrentTab(2);
            snapToScreen(2);
        } else if (v == tab4) {
        	 mTabHost.setCurrentTab(3);
            snapToScreen(3);
        } else if (v == tab5) {
        	 mTabHost.setCurrentTab(4);
            snapToScreen(4);
        } else if (v == tab6) {
        	 mTabHost.setCurrentTab(5);
            snapToScreen(5);
        } else if (v == tab7) {
        	mTabHost.setCurrentTab(6);
            snapToScreen(6);
        }
    }
    
    public void snapToScreen(int curScreen){
//    	doChange(mCurScreen, curScreen);
//    	Button[] tabs = { tab1, tab2, tab3, tab4, tab5, tab6, tab7 };
//    	if(mCurScreen != curScreen){
//    		if(tabs[mCurScreen] != null){
////    			tabs[mCurScreen].setTextColor(getResources().getColor(R.color.csfb_delay_color));
//    		}
//    	}
    	mCurScreen = curScreen;
        updateBtnColor(mCurScreen);
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param lastIndex
     * @param currentIndex
     * @see com.walktour.framework.view.LayoutChangeListener#doChange(int, int)
     */
    @Override
    public void doChange(int lastIndex, final int currentIndex) {

    }
    
    
    /**
     * 动态更新按钮颜色
     */

    private void updateBtnColor(int mCurScreen){
        List<Button> tabs=new ArrayList<>();
        tabs.add(tab1);
        tabs.add(tab2);
        tabs.add(tab3);
        tabs.add(tab4);
        tabs.add(tab5);
        tabs.add(tab6);
        tabs.add(tab7);
        TabHostUtil.updateBtnColor(this,tabs,mTabHost,mCurScreen);
    }
    
    
    /**
     * 移动动画
     * @param lastTab
     * @param currentTab
     * @return
     */
	private TranslateAnimation moveAnimation(LinearLayout lastTab,
			LinearLayout currentTab) {
		TranslateAnimation animation;
		animation = new TranslateAnimation(lastTab.getLeft()-getResources().getDimension(R.dimen.parm_move_width), currentTab.getLeft()-getResources().getDimension(R.dimen.parm_move_width),
		        0, 0);
		return animation;
	}
	 
}
