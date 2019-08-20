package com.walktour.gui.mos;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.walktour.gui.R;

import java.util.List;

/**
 * @author zhicheng.chen
 * @date 2019/3/21
 */
public class MosDialogAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mData;

    public MosDialogAdapter(Context context, List<String> data) {
        mContext = context;
        mData = data;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mData.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.adapter_mos_cacaulator, null);
            vh = new ViewHolder();
            vh.tvResult = (TextView) convertView.findViewById(R.id.tv_cal_result);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        if (getItem(position).startsWith(">>")) {
            vh.tvResult.setTextSize(16);
        } else {
            vh.tvResult.setTextSize(14);

        }
        vh.tvResult.setText(getItem(position));
        return convertView;
    }

    class ViewHolder {
        TextView tvResult;
    }
}
