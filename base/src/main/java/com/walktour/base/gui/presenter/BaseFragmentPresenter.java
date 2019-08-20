package com.walktour.base.gui.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.walktour.base.gui.activity.BaseActivity;
import com.walktour.base.gui.fragment.BaseFragment;
import com.walktour.base.util.Base64Util;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.StringUtil;

/**
 * 视图交互类
 * Created by wangk on 2017/6/15.
 */

public abstract class BaseFragmentPresenter {
    /**
     * 关联的视图
     */
    private BaseFragment mFragment;

    public BaseFragmentPresenter(BaseFragment fragment) {
        LogUtil.d(this.getLogTAG(), "-----onCreate----");
        this.mFragment = fragment;
    }


    /**
     * 获取日志标识
     *
     * @return 日志标识
     */
    protected abstract String getLogTAG();

    /**
     * 处理弹出的对话框返回的值
     *
     * @param bundle 返回的值
     */
    public void dealDialogCallBackValues(Bundle bundle) {
        //有需要处理的覆盖实现该方法
    }

    /**
     * 设置共享属性值
     *
     * @param fileName 属性文件名称
     * @param key      属性key
     * @param value    属性值
     */
    public void setSharedPreference(String fileName, String key, String value) {
        if (StringUtil.isEmpty(fileName) || StringUtil.isEmpty(key) || StringUtil.isEmpty(value))
            return;
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, Base64Util.encodeToString(value));
        editor.apply();// 提交修改
    }

    /**
     * 删除共享属性值
     *
     * @param fileName 属性文件名称
     * @param key      属性key
     */
    public void removeSharedPreference(String fileName, String key) {
        if (StringUtil.isEmpty(fileName) || StringUtil.isEmpty(key))
            return;
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();// 提交修改
    }

    /**
     * 获取所属Activity的Intent
     *
     * @return 所属Activity的Intent
     */
    protected Intent getIntent() {
        return this.getActivity().getIntent();
    }

    /**
     * 获取共享属性值
     *
     * @param fileName 属性文件名称
     * @param key      属性key
     */
    public String getSharedPreference(String fileName, String key) {
        if (StringUtil.isEmpty(fileName) || StringUtil.isEmpty(key))
            return "";
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, "");
        if (StringUtil.isEmpty(value))
            return "";
        return Base64Util.decodeToString(value);
    }


    /**
     * 显示进度对话框
     *
     * @param title         标题
     * @param message       消息
     * @param indeterminate 进度是否不确定性
     * @param cancelable    是否可以取消
     */
    protected void showProgressDialog(String title, String message, boolean indeterminate, boolean cancelable) {
        this.getActivity().showProgressDialog(title, message, indeterminate, cancelable);
    }

    /**
     * 关闭显示的进度对话框
     */
    protected void dismissProgressDialog() {
        this.getActivity().dismissProgressDialog();
    }

    /**
     * 获取关联的页面
     *
     * @return 页面
     */
    protected BaseActivity getActivity() {
        return (BaseActivity) this.mFragment.getActivity();
    }

    /**
     * 启动需要返回的界面
     *
     * @param intent      启动参数
     * @param requestCode 请求标识
     */
    protected void startActivityForResult(Intent intent, int requestCode) {
        this.getActivity().startActivityForResult(intent, requestCode);
    }

    /**
     * 启动服务类
     *
     * @param service 服务类参数
     */
    public void startService(Intent service) {
        this.getActivity().startService(service);
    }

    /**
     * 终止服务类
     *
     * @param name 服务类名称
     */
    public void stopService(Intent name) {
        this.getActivity().stopService(name);
    }

    /**
     * 界面菜单选择操作处理
     *
     * @param item 菜单
     */
    public void onOptionsItemSelected(MenuItem item) {
        //默认不处理
    }

    /**
     * 处理调用activity返回的数据
     *
     * @param requestCode 调用标识
     * @param resultCode  结果标识
     * @param data        数据对象
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //默认不处理
    }

    /**
     * 处理Activity点击返回按钮
     */
    public void onBackPressed() {
        //默认不处理
    }

    /**
     * 显示当前视图
     */
    public abstract void loadData();

    /**
     * 显示提示信息
     *
     * @param messageId 信息资源ID
     */
    protected void showToast(int messageId) {
        this.getActivity().showToast(messageId);
    }

    /**
     * 显示提示信息
     *
     * @param message 信息内容
     */
    protected void showToast(String message) {
        this.getActivity().showToast(message);
    }

}
