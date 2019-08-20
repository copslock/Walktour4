/*
 * 文件名: BaseDisplayAdapter.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2013-5-30
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.control.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.gui.R;
import com.walktour.gui.setting.SysMap;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [RCS Client V100R001C03, 2013-5-30] 
 */
public class BaseDisplayAdapter extends BaseAdapter {
    
    private Context context;
    
    private String[] params;
    
    public BaseDisplayAdapter(Context context, String[] params) {
        this.context = context;
        this.params = params;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return params.length;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param position
     * @return
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return null;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param position
     * @return
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param position
     * @param convertView
     * @param parent
     * @return
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    @SuppressWarnings("deprecation")
    public View getView(int position, View convertView, ViewGroup parent) {
        CheckedTextView checkedTextView = new CheckedTextView(context);
        checkedTextView.setText(params[position]);
        boolean checked = false;
        switch (position) {
            case 0:
                checked = SharePreferencesUtil.getInstance(context.getApplicationContext()).getBoolean(SysMap.BASE_GSM,false);
                break;
            case 1:
                checked = SharePreferencesUtil.getInstance(context.getApplicationContext()).getBoolean(SysMap.BASE_WCDMA, false);
                break;
            case 2:
                checked = SharePreferencesUtil.getInstance(context.getApplicationContext()).getBoolean(SysMap.BASE_CDMA,false);
                break;
            case 3:
                checked = SharePreferencesUtil.getInstance(context.getApplicationContext()).getBoolean(SysMap.BASE_TDSCDMA,false);
                break;
            case 4:
                checked = SharePreferencesUtil.getInstance(context.getApplicationContext()).getBoolean(SysMap.BASE_LTE,false);
                break;
            case 5:
                checked = SharePreferencesUtil.getInstance(context.getApplicationContext()).getBoolean(SysMap.BASE_NB_IoT,false);
                break;

            default:
                break;
        }
        checkedTextView.setChecked(checked);
        checkedTextView.setCheckMarkDrawable(context.getResources().getDrawable(R.drawable.checkbox));
        checkedTextView.setBackgroundResource(R.drawable.base_list_item_bg);
        checkedTextView.setGravity(Gravity.CENTER_VERTICAL);
        checkedTextView.setPadding(8, 0, 8, 0);
        checkedTextView.setHeight(105);
        checkedTextView.setTextColor(context.getResources().getColor(R.color.app_main_text_color));
        return checkedTextView;
    }
    
}
