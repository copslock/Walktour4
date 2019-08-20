/*
 * 文件名: MutilyTesterSetAdapter.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2012-12-26
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.walktour.gui.R;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-12-26] 
 */
public class MutilyTesterSetAdapter extends BaseAdapter {
    
    private Context context;
    
    /**
     * 适配器数组
     */
    private String[] titles;
    
    private String[] texts;
    
    public MutilyTesterSetAdapter(Context context, String[] titles,
            String[] texts) {
        super();
        this.context = context;
        this.titles = titles;
        this.texts = texts;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        if (titles == null) {
            return 0;
        }
        return titles.length;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.listview_item_style1, null);
            holder.ItemTitle = (TextView) convertView.findViewById(R.id.ItemTitle);
            holder.ItemText = (TextView) convertView.findViewById(R.id.ItemText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(titles[position].contains("密码") || titles[position].contains("Password")){
            holder.ItemTitle.setText("******"); 
        }else{
            holder.ItemTitle.setText(titles[position]); 
        }
        
        holder.ItemText.setText(texts[position]);
        return convertView;
    }
    
    class ViewHolder {
        
        TextView ItemTitle;
        
        TextView ItemText;
    }
    
}
