package com.walktour.framework.view;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.walktour.framework.ui.BasicActivityGroup;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-6-26] 
 */
public abstract class ScroollActivity extends BasicActivityGroup implements LayoutChangeListener,
        OnClickListener {
    
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
            scrollLayout.snapToScreen(0);
        } else if (v == tab2) {
            scrollLayout.snapToScreen(1);
        } else if (v == tab3) {
            scrollLayout.snapToScreen(2);
        } else if (v == tab4) {
            scrollLayout.snapToScreen(3);
        } else if (v == tab5) {
            scrollLayout.snapToScreen(4);
        } else if (v == tab6) {
            scrollLayout.snapToScreen(5);
        }
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param lastIndex
     * @param currentIndex
     * @see com.walktour.framework.view.LayoutChangeListener#doChange(int, int)
     */
    @Override
    public void doChange(int lastIndex, int currentIndex) {
        if (lastIndex != currentIndex) {
            TranslateAnimation animation = null;
            LinearLayout lastTab = null;
            LinearLayout currentTab = null;
            switch (lastIndex) {
                case 0:
                    lastTab = (LinearLayout) tab1.getParent();
                    break;
                case 1:
                    lastTab = (LinearLayout) tab2.getParent();
                    break;
                case 2:
                    lastTab = (LinearLayout) tab3.getParent();
                    break;
                case 3:
                    lastTab = (LinearLayout) tab4.getParent();
                    break;
                case 4:
                    lastTab = (LinearLayout) tab5.getParent();
                    break;
                case 5:
                    lastTab = (LinearLayout) tab6.getParent();
                    break;
                default:
                    lastTab = (LinearLayout) tab6.getParent();
                    break;
            }
            switch (currentIndex) {
                case 0:
                    currentTab = (LinearLayout) tab1.getParent();
                    animation = new TranslateAnimation(lastTab.getLeft(), currentTab.getLeft(),
                            0, 0);
                    break;
                case 1:
                    currentTab = (LinearLayout) tab2.getParent();
                    animation = new TranslateAnimation(lastTab.getLeft(),
                            currentTab.getLeft(), 0, 0);
                    
                    break;
                case 2:
                    currentTab = (LinearLayout) tab3.getParent();
                    animation = new TranslateAnimation(lastTab.getLeft(),
                            currentTab.getLeft(), 0, 0);
                    
                    break;
                case 3:
                    currentTab = (LinearLayout) tab4.getParent();
                    animation = new TranslateAnimation(lastTab.getLeft(),
                            currentTab.getLeft(), 0, 0);
                    break;
                case 4:
                    currentTab = (LinearLayout) tab5.getParent();
                    animation = new TranslateAnimation(lastTab.getLeft(),
                            currentTab.getLeft(), 0, 0);
                    break;
                case 5:
                    currentTab = (LinearLayout) tab6.getParent();
                    animation = new TranslateAnimation(lastTab.getLeft(),
                            currentTab.getLeft(), 0, 0);
                    break;
               default:
                   currentTab = (LinearLayout) tab6.getParent();
                   animation = new TranslateAnimation(lastTab.getLeft(),
                           currentTab.getLeft(), 0, 0);
                   break;
            }
            animation.setDuration(300);
            animation.setFillAfter(true);
            scrollTag.startAnimation(animation);
        }
        
    }
    
}
