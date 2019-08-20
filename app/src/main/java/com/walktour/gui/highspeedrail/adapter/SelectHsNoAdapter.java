package com.walktour.gui.highspeedrail.adapter;

import android.content.Context;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.walktour.base.gui.adapter.recyclerview.BaseViewHolder;
import com.walktour.base.gui.adapter.recyclerview.CommonRecyclerAdapter;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.highspeedrail.model.HighSpeedNoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @date on 2018/9/4
 * @describe 高铁选择班次
 * @author jinfeng.xie
 * @version 1.0.0
 *
 */
public class SelectHsNoAdapter extends CommonRecyclerAdapter<HighSpeedNoModel>  implements Filterable {
    private static final String TAG = "SelectHsNoAdapter";
    private MyFilter myFilter;
    private ArrayList<HighSpeedNoModel> mOriginalValues;
    private final Object mLock = new Object();

    public SelectHsNoAdapter(Context context) {
        super(context);
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.adapter_select_hs_no;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        HighSpeedNoModel bean = mList.get(position);
        TextView tvName = holder.get(R.id.tv_name);
        tvName.setText(bean.noName);
    }

    @Override
    public Filter getFilter() {
        if (myFilter == null) {
            myFilter = new MyFilter();
        }
        return myFilter;
    }
    class MyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            // 持有过滤操作完成之后的数据。该数据包括过滤操作之后的数据的值以及数量。 count:数量 values包含过滤操作之后的数据的值
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    // 将list的用户 集合转换给这个原始数据的ArrayList
                    mOriginalValues = new ArrayList<HighSpeedNoModel>(mList);
                }
            }
            if (prefix == null || prefix.length() == 0) {
                synchronized (mLock) {
                    ArrayList<HighSpeedNoModel> list = new ArrayList<HighSpeedNoModel>(
                            mOriginalValues);
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                // 做正式的筛选
                String prefixString = prefix.toString().toLowerCase();

                // 声明一个临时的集合对象 将原始数据赋给这个临时变量
                final ArrayList<HighSpeedNoModel> values = mOriginalValues;

                final int count = values.size();

                // 新的集合对象
                final ArrayList<HighSpeedNoModel> newValues = new ArrayList<HighSpeedNoModel>(
                        count);

                for (int i = 0; i < count; i++) {
                    // 如果姓名的前缀相符或者电话相符就添加到新的集合
                    final HighSpeedNoModel value = (HighSpeedNoModel) values.get(i);

                    LogUtil.e(TAG, "HighSpeedNoModel:"
                            + value);
                    if (value.getNoName().toLowerCase().contains(
                            prefixString)) {
                        newValues.add(value);
                    }
                }
                // 然后将这个新的集合数据赋给FilterResults对象
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            // 重新将与适配器相关联的List重赋值一下
            mList = (List<HighSpeedNoModel>) results.values;

            if (results.count > 0) {
                notifyDataSetChanged();
            }
        }

    }
}
