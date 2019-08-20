package com.walktour.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.walktour.model.ExportParmModel;

import java.util.List;

/**
 * zhihui.lian
 * 导出多选框
 */
public class ExportMutilChoiceAdapter extends BaseAdapter{
    
    private Context mContext;
    
    private int textViewResourceId;
    
    private List<ExportParmModel> exportModels;
    
    public ExportMutilChoiceAdapter(Context context, int textViewResourceId,
            List<ExportParmModel> parameterList) {
        this.mContext = context;
        this.textViewResourceId = textViewResourceId;
        this.exportModels = parameterList;
    }

    
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
        ExportParmModel parameter = exportModels.get(position);
        holder.checkedTextView.setText(parameter.getShowNmae());
        holder.checkedTextView.setChecked(parameter.getEnable() == 0 ? false : true);
        return convertView;
    }
    
    
    @Override
    public int getCount() {
        if(exportModels == null){
            return 0;
        }
        return exportModels.size();
    }
    
    class ViewHolder{
        
        CheckedTextView checkedTextView;
        
    }

    
    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    
    
}
