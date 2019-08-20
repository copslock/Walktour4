package com.walktour.base.gui.fragment;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import butterknife.ButterKnife;

/**
 * 基础列表适配器类
 * Created by wangk on 2017/4/7.
 */

public abstract class BaseListAdapter<T> extends ArrayAdapter<T> {
    /**
     * 视图id
     */

    private int mResourceId;
    /**
     * 得到一个LayoutInfalter对象用来导入布局
     */
    private LayoutInflater mInflater;

    public BaseListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
        this.mResourceId = resource;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseHolder holder = null;
        if (convertView == null) {
            holder = getHolder();
            convertView = this.mInflater.inflate(this.mResourceId, null);
            ButterKnife.bind(holder, convertView);//用butterKnife绑定
            convertView.setTag(holder);
        } else {
            holder = (BaseHolder) convertView.getTag();
        }
        holder.setData(position, this.getItem(position));//将数据传给holder
        return convertView;
    }

    /**
     * 返回对应的holder类
     *
     * @return 返回对应的holder子类，需要继承自BaseHolder
     */
    protected abstract BaseHolder getHolder();
}
