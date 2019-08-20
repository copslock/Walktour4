package com.walktour.framework.ui;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.walktour.gui.R;

/**
 * 自定义置灰Spiner，特殊处理达到效果
 * zhihui.lian  
 */
public class GraySpinnerAdapter implements SpinnerAdapter {
    
    private Context mContext;
    
    private String[] arrayStr;
    
    private boolean isEnabled = true;
    
    public GraySpinnerAdapter(Context context,String[] arrayStr) {
        super();
        this.mContext = context;
        this.arrayStr = arrayStr;
    }
    
    public void setEnabled(boolean isEnabled){
    	this.isEnabled = isEnabled;
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        
    }
    
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        
    }
    
    @Override
    public boolean isEmpty() {
        return false;
    }
    
    @Override
    public boolean hasStableIds() {
        return false;
    }
    
    @Override
    public int getViewTypeCount() {
        return 1;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext)
                .inflate(R.layout.simple_spinner_custom_layout, null);
        TextView  checkTextView = (TextView) convertView.findViewById(android.R.id.text1);
        if(!isEnabled){
        	checkTextView.setTextColor(mContext.getResources().getColor(R.color.app_click_disable_grey_color));
        }
        checkTextView.setText(arrayStr[position]);
        return convertView;
}
    
    @Override
    public int getItemViewType(int position) {
        return 0;
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public Object getItem(int position) {
        return arrayStr[position];
    }
    
    @Override
    public int getCount() {
        return arrayStr.length;
    }
    
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.spinner_dropdown_item, null);
            CheckedTextView  checkTextView = (CheckedTextView) convertView.findViewById(android.R.id.text1);
            checkTextView.setText(arrayStr[position]);
            checkTextView.setBackgroundResource(R.drawable.task_test_main_bg);
            return convertView;
    }
}
