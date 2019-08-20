package com.walktour.gui.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;


/**
 * 模板历史记录公共存储数据类
 * @author zhihui.lian
 */
public class SaveHistoryShare {
	
	private Context context;
	
	private CustomAutoCompleteAdapter adapter;
	
	private ArrayList<String> mOriginalValues = new ArrayList<String>();    //存储历史队列
	
	private String preference = "";										
	
	private String keyField = "";
	
	public SaveHistoryShare(Context context){
		this.context = context;
	}
	
	
	
	
	
    /**
     * 当搜索完成一次后，对搜索记录进行刷新
     */
    public void refreshAdapter()
    {
    	getHistoryDataFromSP(preference, keyField,mOriginalValues);
        adapter.SetAutoCompleteAdapter(mOriginalValues);
    }
	
	
    /**
     * 初始化AutoCompleteTextView，最多显示5项提示，使 AutoCompleteTextView在一开始获得焦点时自动提示
     *
     * @param field
     *            保存在sharedPreference中的字段名
     * @param auto
     *            要操作的AutoCompleteTextView
     */
    public void initAutoComplete(AutoCompleteTextView auto) {
    	adapter = new CustomAutoCompleteAdapter(context, mOriginalValues);
    	auto.setAdapter(adapter);
        auto.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                CustomAutoCompleteTextView view = (CustomAutoCompleteTextView) v;
                if (hasFocus) {
                    view.showDropDown();
                }
            }
        });
        auto.setOnClickListener(new OnClickListener() {
                
            public void onClick(View v) {
                CustomAutoCompleteTextView view = (CustomAutoCompleteTextView) v;
                view.showDropDown();
            }
        });
    }
	
	
	
	
	
    /**
     * 根据指定的sharePreference 来获取最近的5条搜索的历史记录
     * @param preference
     * @param keyField
     */
    public void getHistoryDataFromSP(String preference, String keyField ,ArrayList<String> mOriginalValues)
    {
    	this.preference = preference;
    	this.keyField = keyField;
        SharedPreferences sp = context.getSharedPreferences(preference, 0);
        String longhistory = sp.getString(keyField, "");
        if (mOriginalValues != null && mOriginalValues.size() > 0) {
    		mOriginalValues.clear();
    	}
        if(longhistory.length()!=0){
        	String[] hisArrays = longhistory.split(",");
        	for (int i = 0; i < hisArrays.length; i++) {
        		if (i < 5) {
        			mOriginalValues.add(hisArrays[i]);
        		}
        	}
        	this.mOriginalValues = mOriginalValues;
        }
    }
	
	
    /**
     * 把指定AutoCompleteTextView中内容保存到sharedPreference中指定的字符段
     *
     * @param field
     *            保存在sharedPreference中的字段名
     * @param auto
     *            要操作的AutoCompleteTextView
     */
    public void saveHistory(AutoCompleteTextView auto) {
            String text = auto.getText().toString();
            SharedPreferences sp = context.getSharedPreferences(preference, 0);
            String longhistory = sp.getString(keyField, "");
            if (!longhistory.contains(text + ",")) {
                StringBuilder sb = new StringBuilder(longhistory);
                sb.insert(0, text + ",");
                sp.edit().putString(keyField, sb.toString()).commit();
            }
        }
    
    /**
     * 清除历史记录
     * @param taskName    任务名
     * @param mOriginalValues
     */
    public void clearData(){
    	mOriginalValues.clear();
    	SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), 0);
        sp.edit().putString(keyField, "").commit();
    	
    }

}
