package com.walktour.base.gui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.walktour.base.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * 基础列表视图类
 * Created by wangk on 2017/4/8.
 */

public abstract class BaseListFragment<T> extends BaseFragment {
    /**
     * 数据列表
     */
    private List<T> mDataList = new ArrayList<>();
    /**
     * 列表适配器
     */
    private ListAdapter mListAdapter;
    /**
     * 行布局资源ID
     */
    private int mRowLayoutId;

    /**
     * @param titleId          标题ID
     * @param fragmentLayoutId 视图关联的布局资源ID
     * @param rowLayoutId      行布局资源ID
     */
    public BaseListFragment(@StringRes int titleId, @LayoutRes int fragmentLayoutId, @LayoutRes int rowLayoutId) {
        super(titleId, fragmentLayoutId);
        this.mRowLayoutId = rowLayoutId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtil.d(this.getLogTAG(), "----createView----");
        View view = inflater.inflate(super.mFragmentLayoutId, container, false);
        ButterKnife.bind(this, view);
        this.setupFragmentComponent();
        this.mListAdapter = new ListAdapter(this.getContext(), this.mRowLayoutId, this.mDataList);
        this.getListView().setAdapter(this.mListAdapter);
        this.onCreateView();
        return view;
    }

    /**
     * 获得要关联的列表对象
     *
     * @return 列表对象
     */
    protected abstract AbsListView getListView();

    /**
     * 显示视图
     *
     * @param dataList 数据列表
     */
    public void showFragment(List<T> dataList) {
        if (dataList == null)
            return;
        LogUtil.d(this.getLogTAG(), "----showFragment----size:" + dataList.size());
        this.mDataList.clear();
        this.mDataList.addAll(dataList);
        this.mListAdapter.notifyDataSetChanged();
    }

    /**
     * 列表适配器
     */
    private class ListAdapter extends BaseListAdapter<T> {


        public ListAdapter(Context context, int resource, List<T> objects) {
            super(context, resource, objects);
        }

        @Override
        protected BaseHolder getHolder() {
            return createViewHolder();
        }

    }

    /**
     * 获取指定位置的数据
     *
     * @param position 位置
     * @return 数据
     */
    protected T getItem(int position) {
        return this.mListAdapter.getItem(position);
    }

    /**
     * 刷新列表
     */
    protected void notifyDataSetChanged() {
        this.mListAdapter.notifyDataSetChanged();
    }

    /**
     * 生成视图控件
     *
     * @return 视图控件
     */
    protected abstract BaseHolder createViewHolder();

    /**
     * 获取数据列表
     *
     * @return 数据列表
     */
    public List<T> getDataList() {
        return this.mDataList;
    }
}
