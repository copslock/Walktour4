package com.walktour.base.gui.fragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.walktour.base.gui.model.ServiceMessage;
import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.base.util.LogUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 基础视图类
 * Created by wangk on 2017/3/27.
 */

public abstract class BaseFragment extends Fragment {
    /**
     * 视图界面标题资源ID
     */
    private int mTitleId;
    /**
     * 视图界面标题
     */
    private String mTitleName;
    /**
     * 视图关联的布局资源ID
     */
    protected int mFragmentLayoutId;
    /**
     * 是否初始化
     */
    private boolean isInitData = false;
    /**
     * 解绑对象
     */
    private Unbinder mUnbinder;
    /**
     * 如果一个界面有多个同类页签时，需要设置改属性来区分
     */
    private int mIndex;

    /**
     * @param titleId          标题ID
     * @param fragmentLayoutId 视图关联的布局资源ID
     */
    public BaseFragment(@StringRes int titleId, @LayoutRes int fragmentLayoutId) {
        LogUtil.d(this.getLogTAG(), "----onCreate----");
        this.mTitleId = titleId;
        this.mFragmentLayoutId = fragmentLayoutId;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public int getIndex() {
        return mIndex;
    }

    /**
     * 获取日志标识
     *
     * @return 日志标识
     */
    protected abstract String getLogTAG();

    public int getTitleId() {
        return this.mTitleId;
    }

    public String getTitleName() {
        return mTitleName;
    }

    public void setTitleName(String titleName) {
        mTitleName = titleName;
    }

    /**
     * 获得视图交互类
     *
     * @return 视图交互类
     */
    public abstract BaseFragmentPresenter getPresenter();

    /**
     * 设置视图组件
     */
    protected abstract void setupFragmentComponent();

    /**
     * 处理服务类反馈的消息
     *
     * @param message 消息
     */
    public void dealMessageFromService(ServiceMessage message) {
        //默认实现
    }

    /**
     * 设置文本编辑框的值
     *
     * @param editText 文本编辑框
     * @param value    值
     */
    protected void setValue(EditText editText, String value) {
        if (editText != null) {
            editText.setText(value);
        }
    }

    /**
     * 设置下拉框的值
     *
     * @param spinner  下拉框
     * @param position 选中行
     */
    protected void setValue(Spinner spinner, int position) {
        if (spinner != null) {
            spinner.setSelection(position);
        }
    }

    /**
     * 设置勾选框的值
     *
     * @param checkBox 勾选框
     * @param value    值
     */
    protected void setValue(CheckBox checkBox, boolean value) {
        if (checkBox != null) {
            checkBox.setChecked(value);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtil.d(this.getLogTAG(), "----onCreateView----");
        View view = inflater.inflate(this.mFragmentLayoutId, container, false);
        this.mUnbinder = ButterKnife.bind(this, view);
        this.setupFragmentComponent();
        this.onCreateView();
        return view;
    }

    /**
     * 在创建视图时要设置
     */
    protected abstract void onCreateView();

    @Override
    public void onDestroyView() {
        LogUtil.d(this.getLogTAG(), "----onDestroyView----");
        super.onDestroyView();
        if (this.mUnbinder != null)
            this.mUnbinder.unbind();
    }

    public boolean isInitData() {
        return isInitData;
    }

    /**
     * 获取当前视图关联的界面要显示的子菜单ID数组，默认不显示
     *
     * @return 当前视图关联的界面要显示的子菜单ID数组
     */
    public int[] showActivityMenuItemIds() {
        return new int[0];
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d(this.getLogTAG(), "----onResume----");
        if (!isInitData) {
            this.getPresenter().loadData();
            this.isInitData = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(this.getLogTAG(), "----onDestroy----");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.d(this.getLogTAG(), "----onPause----");
        this.isInitData = false;
    }
}
