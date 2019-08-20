/*
 * 文件名: MutilChoiceArrayAdapter.java
 * 版    权：  Copyright Dingli Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2013-9-9
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
import android.widget.CheckedTextView;

import com.walktour.model.Parameter;

import java.util.List;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2013-9-9] 
 */
public class MutilChoiceArrayAdapter extends BaseAdapter{
    
    private Context mContext;
    
    private int textViewResourceId;
    
    private List<Parameter> parameterList;
    
    /**
     * [构造简要说明]
     * @param context
     * @param textViewResourceId
     * @param objects
     */
    public MutilChoiceArrayAdapter(Context context, int textViewResourceId,
            List<Parameter> parameterList) {
        this.mContext = context;
        this.textViewResourceId = textViewResourceId;
        this.parameterList = parameterList;
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param position
     * @param convertView
     * @param parent
     * @return
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(textViewResourceId, null);
            holder.checkedTextView = (CheckedTextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder= (ViewHolder) convertView.getTag();
        }
        Parameter parameter = parameterList.get(position);
        holder.checkedTextView.setText(parameter.getShowName());
//        holder.checkedTextView.setChecked(parameter.isChartView());
        holder.checkedTextView.setChecked(parameter.isMapChecked());
/*        CheckedTextView checkedTextView = new CheckedTextView(mContext);
        checkedTextView.setText(objects[position]);
        checkedTextView.setChecked(checkItems[position]);
        checkedTextView.setCheckMarkDrawable(mContext.getResources().getDrawable(R.drawable.checkbox));
        checkedTextView.setBackgroundResource(R.drawable.base_list_item_bg);
        checkedTextView.setGravity(Gravity.CENTER_VERTICAL);
        checkedTextView.setPadding(8, 0, 8, 0);
        checkedTextView.setHeight((int) (40 * mContext.getResources().getDisplayMetrics().density));*/
        return convertView;
    }
    
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        if(parameterList == null){
            return 0;
        }
        return parameterList.size();
    }
    
    class ViewHolder{
        
        CheckedTextView checkedTextView;
        
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
    
    
}
