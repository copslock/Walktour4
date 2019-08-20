package com.walktour.gui.setting.bluetoothmos.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.walktour.gui.R;


/**
 * Created by ZengZeHong on 2017/5/15.
 */

public class ToastHolder extends RecyclerView.ViewHolder{
    private TextView tvToast;
    public ToastHolder(View itemView) {
        super(itemView);
        tvToast = (TextView)itemView.findViewById(R.id.tv_toast);
    }

    public TextView getTvToast() {
        return tvToast;
    }

    public void setTvToast(TextView tvToast) {
        this.tvToast = tvToast;
    }
}