package com.walktour.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import com.walktour.gui.R;

import java.util.List;

public class TabHostUtil {
    private static final String TAG = "TabHostUtil";

    /**
     * 生成tab页面
     *
     * @param tabTag  tab标识
     * @param textId  文本ID
     * @param content 内容
     */
    public static View createTab(Context context, TabHost myTabhost, String tabTag, int textId, Intent content) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabmini, null);
        TextView tvTab = (TextView) view.findViewById(R.id.tv_title);
        tvTab.setText(textId);
        if (content == null)
            myTabhost.addTab(myTabhost.newTabSpec(tabTag).setIndicator(view));
        else
            myTabhost.addTab(myTabhost.newTabSpec(tabTag).setIndicator(view).setContent(content));

        return view;
    }

    public static void updateTab(Context context, TabHost tabHost) {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            View view = (View) tabHost.getTabWidget().getChildTabViewAt(i);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
            View line = view.findViewById(R.id.line);
            if (tabHost.getCurrentTab() == i) {
                line.setVisibility(View.VISIBLE);
                tvTitle.setTextColor(Color.WHITE);
            } else {
                line.setVisibility(View.GONE);
                tvTitle.setTextColor(context.getResources().getColor(R.color.app_tag_text));
            }
        }

    }

    /**
     * 动态更新按钮颜色
     */

    public static void updateBtnColor(Context context, List<Button> tabs, TabHost mTabHost, int mCurScreen) {
        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
            if (tabs.get(i) != null) {
                if (mCurScreen == i) {
                    tabs.get(i).setTextColor(context.getResources().getColor(R.color.app_tag_bg));
                    tabs.get(i).setBackgroundResource(R.drawable.bg_second_title);
                } else {
                    tabs.get(i).setTextColor(context.getResources().getColor(R.color.app_tag_text));
                    tabs.get(i).setBackgroundResource(R.color.app_tag_bg);
                }
            }
        }
    }
}
