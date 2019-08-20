package com.walktour.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.walktour.gui.R;
import com.walktour.gui.task.TaskSelSampleActivity;

import java.util.List;


public class TaskSelectSampleAdapter extends BaseAdapter {
    private List<TaskSelSampleActivity.SampleBean> sampleBeanList;
    private Context context;

    public TaskSelectSampleAdapter(List<TaskSelSampleActivity.SampleBean> taskList, Context context) {
        this.sampleBeanList = taskList;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (sampleBeanList != null) {
            return sampleBeanList.size();
        }
        return 0;
    }

    @Override
    public TaskSelSampleActivity.SampleBean getItem(int position) {
        return sampleBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_select_sample, null);
            holder = new ViewHolder();
            holder.ItemCheckable = (ImageButton) convertView.findViewById(R.id.ItemTestable);
            holder.ItemTitle = (TextView) convertView.findViewById(R.id.ItemTitle);
            holder.ItemDropable = (ImageView) convertView.findViewById(R.id.drag_handle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final TaskSelSampleActivity.SampleBean bean = getItem(position);
        holder.ItemCheckable.setSelected(bean.select);
        holder.ItemTitle.setText(bean.name);
        holder.updataCheckStatus();

        holder.ItemCheckable.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ItemCheckable.setSelected(!holder.ItemCheckable.isSelected());
                bean.select = !bean.select;
                holder.updataCheckStatus();
            }
        });


        return convertView;
    }

    private class ViewHolder {
        ImageButton ItemCheckable;
        TextView ItemTitle;
        ImageView ItemDropable;

        public void updataCheckStatus() {
            if (ItemCheckable.isSelected()) {
                ItemCheckable.setImageResource(R.drawable.btn_check_on);
            } else {
                ItemCheckable.setImageResource(R.drawable.btn_check_off);
            }
        }
    }

}
