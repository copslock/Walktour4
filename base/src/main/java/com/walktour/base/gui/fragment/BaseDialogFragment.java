package com.walktour.base.gui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.walktour.base.R;
import com.walktour.base.gui.activity.BaseActivity;
import com.walktour.base.util.LogUtil;

import butterknife.ButterKnife;

/**
 * 对话框基础类
 * Created by wangk on 2017/8/10.
 */

public abstract class BaseDialogFragment extends DialogFragment {
    /**
     * 视图界面标题资源ID
     */
    private int mTitleId;
    /**
     * 视图关联的布局资源ID
     */
    protected int mFragmentLayoutId;

    /**
     * @param titleId          标题ID
     * @param fragmentLayoutId 视图关联的布局资源ID
     */
    public BaseDialogFragment(@StringRes int titleId, @LayoutRes int fragmentLayoutId) {
        LogUtil.d(this.getLogTAG(), "----onCreate----");
        this.mTitleId = titleId;
        this.mFragmentLayoutId = fragmentLayoutId;
    }

    /**
     * 获取日志标识
     *
     * @return 日志标识
     */
    public abstract String getLogTAG();

    /**
     * 设置绑定的序列化参数对象
     *
     * @param key 参数标识
     * @param obj 对象
     */
    public void putBundle(String key, Parcelable obj) {
        Bundle bundle = this.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
            this.setArguments(bundle);
        }
        bundle.putParcelable(key, obj);
    }

    /**
     * 设置绑定的整数参数对象
     *
     * @param key 参数标识
     * @param obj 对象
     */
    public void putBundle(String key, int obj) {
        Bundle bundle = this.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
            this.setArguments(bundle);
        }
        bundle.putInt(key, obj);
    }

    /**
     * 设置绑定的字符串参数对象
     *
     * @param key 参数标识
     * @param obj 对象
     */
    public void putBundle(String key, String obj) {
        Bundle bundle = this.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
            this.setArguments(bundle);
        }
        bundle.putString(key, obj);
    }

    /**
     * 获得绑定的序列化参数对象
     *
     * @param key 参数标识
     * @return 对象
     */
    public Parcelable getParcelableBundle(String key) {
        if (this.getArguments() != null) {
            return this.getArguments().getParcelable(key);
        }
        return null;
    }

    /**
     * 获得绑定的整数参数对象
     *
     * @param key 参数标识
     * @return 对象
     */
    public String getStringBundle(String key) {
        if (this.getArguments() != null) {
            return this.getArguments().getString(key);
        }
        return null;
    }

    /**
     * 获得绑定的整数参数对象
     *
     * @param key 参数标识
     * @return 对象
     */
    public int getIntBundle(String key) {
        if (this.getArguments() != null) {
            return this.getArguments().getInt(key);
        }
        return 0;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(this.mFragmentLayoutId, null);
        ButterKnife.bind(this, view);
        this.setShowValues();
        builder.setTitle(this.mTitleId).setView(view).setPositiveButton(R.string.control_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ((BaseActivity) getActivity()).dealDialogCallBackValues(setCallBackValues());
                    }
                }).setNegativeButton(R.string.control_cancel, null);
        return builder.create();
    }

    /**
     * 设置界面显示的值
     */
    protected abstract void setShowValues();

    /**
     * 设置要回传的值
     *
     * @return 回传的值
     */
    protected abstract Bundle setCallBackValues();
}
