package com.walktour.base.gui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.walktour.base.R;
import com.walktour.base.R2;
import com.walktour.base.gui.fragment.BaseDialogFragment;
import com.walktour.base.gui.fragment.BaseFragment;
import com.walktour.base.gui.model.ServiceMessage;
import com.walktour.base.gui.presenter.BaseActivityPresenter;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.SystemBarUtil;
import com.walktour.base.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 不带分页的界面基础类
 * Created by wangk on 2017/6/20.
 */

public abstract class BaseActivity extends AppCompatActivity {
    /**
     * 工具栏
     */
    @BindView(R2.id.toolbar)
    Toolbar mToolbar;
    /**
     * 工具栏标题
     */
    @BindView(R2.id.toolbar_title)
    TextView mToolbarTitle;
    /**
     * 当前界面显示的视图
     */
    private BaseFragment mCurrentFragment;
    /**
     * 界面包含的视图列表
     */
    protected List<BaseFragment> mFragmentList = new ArrayList<>();
    /**
     * 进度对话框
     */
    private ProgressDialog mProgressDialog;
    /**
     * 解绑对象
     */
    private Unbinder mUnbinder;
    /**
     * 点击非编辑框区域是否自动隐藏键盘标志
     */
    private boolean isAutoHideKeyboard = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSystemBarTint();
        LogUtil.d(this.getLogTAG(), "----onCreate----");
        setContentView(R.layout.activity_base);
        this.setupActivityComponent();
        this.mUnbinder = ButterKnife.bind(this);
        setSupportActionBar(this.mToolbar);
        this.onCreate();
        this.initFragments();
        EventBus.getDefault().register(this);
    }

    /**
     * Activity在onCreate方法中要初始化的操作
     */
    protected abstract void onCreate();

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(this.getLogTAG(), "----onResume----");
    }
    /**
     * 设置状态栏颜色
     * */
    protected void initSystemBarTint() {
        if (translucentStatusBar()) {
            // 设置状态栏全透明
            SystemBarUtil.transparencyStatusBar(this);
        } else {
            // 设置状态栏字体颜色为深色
            if (isStatusBarTextDark()) {
                if (SystemBarUtil.isSupportStatusBarDarkFont()) {
                    // 设置状态栏颜色
                    SystemBarUtil.setStatusBarColor(this, setStatusBarColor(), false, isPaddingStatus());
                    SystemBarUtil.setStatusBarLightMode(this, true);
                } else {
                    LogUtil.e("baseActivity","当前设备不支持状态栏字体变色");
                    // 设置状态栏颜色为主题颜色
                    SystemBarUtil.setStatusBarColor(this, getDarkColorPrimary(), false, isPaddingStatus());
                }
            } else {
                // 设置状态栏颜色
                SystemBarUtil.setStatusBarColor(this, setStatusBarColor(), false, isPaddingStatus());
            }
        }
    }
    /** 子类可以重写决定是否使用状态栏深色字体 */
    protected boolean isStatusBarTextDark() {
        return true;
    }
    /** 子类可以重写决定是否使用透明状态栏 */
    protected boolean translucentStatusBar() {
        return false;
    }
    /** 子类可以重写改变状态栏颜色 */
    protected int setStatusBarColor() {
        return getColorPrimary();
    }
    /** 子类可以重写决定是否解决状态栏与标题栏重叠问题 */
    protected boolean isPaddingStatus() {
        return true;
    }
    /**
     * 获取深主题色
     * */
    public int getDarkColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.data;
    }
    /**
     * 处理弹出的对话框返回的值
     *
     * @param bundle 返回的值
     */
    public void dealDialogCallBackValues(Bundle bundle) {
        if (this.mCurrentFragment != null)
            this.mCurrentFragment.getPresenter().dealDialogCallBackValues(bundle);
    }

    /**
     * 获取主题色
     * */
    public int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }
    @Override
    public Intent getIntent() {
        if (super.getIntent() == null) {
            super.setIntent(new Intent());
        }
        return super.getIntent();
    }

    /**
     * 显示对话框
     *
     * @param dialogFragment 对话框对象
     */
    public void showDialog(BaseDialogFragment dialogFragment) {
        dialogFragment.show(this.getSupportFragmentManager(), dialogFragment.getLogTAG());
    }

    /**
     * 设置当前要显示的视图
     *
     * @param fragment 要设置的视图
     */
    private void setFragment(BaseFragment fragment) {
        if (fragment.equals(this.mCurrentFragment))
            return;
        LogUtil.d(this.getLogTAG(), "---setFragment----index:" + fragment.getIndex());
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        transaction.setCustomAnimations(R.anim.open_next, R.anim.close_main);
        if (this.mCurrentFragment == null) {
            transaction.add(R.id.content_fragment, fragment);
        } else if (!fragment.isAdded()) {    // 先判断是否被add过
            // 隐藏当前的fragment，add下一个到Activity中
            transaction.hide(this.mCurrentFragment).add(R.id.content_fragment, fragment);
        } else {// 隐藏当前的fragment，显示下一个
            transaction.hide(this.mCurrentFragment).show(fragment);
        }
        transaction.commitAllowingStateLoss();
        this.mCurrentFragment = fragment;
    }

    /**
     * 生成顶部菜单栏
     *
     * @param menu   菜单对象
     * @param menuId 菜单资源文件ID
     * @return 是否有生成
     */
    public boolean onCreateOptionsMenu(Menu menu, @MenuRes int menuId) {
        LogUtil.d(this.getLogTAG(), "----onCreateOptionsMenu----");
        getMenuInflater().inflate(menuId, menu);
        this.showFragmentMenuItems();
        return true;
    }

    /**
     * 新增界面包含的视图
     *
     * @param fragment 视图
     */
    protected int addFragment(BaseFragment fragment) {
        if (fragment == null)
            return -1;
        this.mFragmentList.add(fragment);
        fragment.setIndex(this.mFragmentList.size() - 1);
        LogUtil.d(getLogTAG(), "----addFragment----index:" + fragment.getIndex());
        if (this.mFragmentList.size() == 1) {
            this.setFragment(fragment);
        }
        return fragment.getIndex();
    }

    /**
     * 显示指定的视图
     *
     * @param fragmentIndex 视图序号
     */
    public void showFragment(int fragmentIndex) {
        if (fragmentIndex < 0)
            return;
        LogUtil.d(this.getLogTAG(), "----showFragment----index:" + fragmentIndex);
        if (fragmentIndex < this.mFragmentList.size()) {
            this.setFragment(this.mFragmentList.get(fragmentIndex));
        }
        this.loadCurrentFragmentData();
    }

    /**
     * 处理服务类反馈的消息
     *
     * @param message 消息对象
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealMessageFromService(ServiceMessage message) {
        LogUtil.d(this.getLogTAG(), "----dealMessageFromService----action:" + message.getAction());
        if (this.mCurrentFragment != null) {
            this.mCurrentFragment.dealMessageFromService(message);
        }
    }

    /**
     * 重新加载当前视图的数据
     */
    public void loadCurrentFragmentData() {
        if (this.mCurrentFragment.isInitData()) {
            this.mCurrentFragment.getPresenter().loadData();
        }
        this.showFragmentMenuItems();
    }

    /**
     * 获得当前显示的视图
     *
     * @return 视图
     */
    protected BaseFragment getCurrentFragment() {
        return this.mCurrentFragment;
    }

    /**
     * 显示视图关联的菜单
     */
    private void showFragmentMenuItems() {
        if (null != this.mCurrentFragment) {
            int[] menuItemIds = this.mCurrentFragment.showActivityMenuItemIds();
            if (menuItemIds == null)
                return;
            LogUtil.d(this.getLogTAG(), "----showFragmentMenuItems----:" + Arrays.toString(menuItemIds));
            for (int i = 0; i < this.mToolbar.getMenu().size(); i++) {
                MenuItem item = this.mToolbar.getMenu().getItem(i);
                boolean isFind = false;
                for (int itemId : menuItemIds) {
                    if (item.getItemId() == itemId) {
                        isFind = true;
                        break;
                    }
                }
                item.setVisible(isFind);
            }
        }
    }

    /**
     * 初始化视图
     */
    protected abstract void initFragments();

    /**
     * 获取日志标识
     *
     * @return 日志标识
     */
    protected abstract String getLogTAG();

    /**
     * 获得界面交互类
     *
     * @return 交互类
     */
    protected abstract BaseActivityPresenter getPresenter();

    /**
     * 设置界面组件
     */
    protected abstract void setupActivityComponent();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LogUtil.d(this.getLogTAG(), "----onOptionsItemSelected----");
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        } else {
            this.getCurrentFragment().getPresenter().onOptionsItemSelected(item);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.d(this.getLogTAG(), "----onActivityResult----requestCode:" + requestCode + "----resultCode:" + resultCode);
        this.getCurrentFragment().getPresenter().onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 显示进度对话框
     *
     * @param title         标题
     * @param message       消息
     * @param indeterminate 进度是否不确定性
     * @param cancelable    是否可以取消
     */
    public void showProgressDialog(String title, String message, boolean indeterminate, boolean cancelable) {
        if (this.mProgressDialog != null)
            return;
        this.mProgressDialog = ProgressDialog.show(this, title, message, indeterminate, cancelable);
    }

    /**
     * 关闭显示的进度对话框
     */
    public void dismissProgressDialog() {
        if (this.mProgressDialog != null) {
            this.mProgressDialog.dismiss();
            this.mProgressDialog = null;
        }
    }

    /**
     * 设置标题栏
     *
     * @param titleId 标题ID
     */
    protected void setToolbarTitle(@StringRes int titleId) {
        this.setToolbarTitle(this.getString(titleId));
    }

    /**
     * 设置标题栏
     *
     * @param title 标题
     */
    protected void setToolbarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            return;
        //为标题栏设置标题，即给ActionBar设置标题。
        this.mToolbarTitle.setText(title);
        actionBar.setTitle("");
        //ActionBar加一个返回图标
        actionBar.setDisplayHomeAsUpEnabled(true);
        //不显示当前程序的图标。
        actionBar.setDisplayShowHomeEnabled(false);
    }

    /**
     * 显示提示信息
     *
     * @param messageId 信息资源ID
     */
    public void showToast(int messageId) {
        ToastUtil.showShort(this, messageId);
    }

    /**
     * 显示提示信息
     *
     * @param message 信息内容
     */
    public void showToast(String message) {
        ToastUtil.showShort(this, message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(this.getLogTAG(), "----onDestroy----");
        this.mFragmentList.clear();
        if (this.mUnbinder != null)
            this.mUnbinder.unbind();
        EventBus.getDefault().unregister(this);
    }


    /**
     * 设置时候启用点击关闭键盘
     *
     * @param autoHide 是否自动隐藏键盘
     */
    public void setAutoHideKeyboard(boolean autoHide) {
        isAutoHideKeyboard = autoHide;
    }

    /**
     * 点击EditText外的地方就收起软键盘
     *
     * @param motionEvent 监听事件
     */
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (!isAutoHideKeyboard) return super.dispatchTouchEvent(motionEvent);
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isShouldHideInput(view, motionEvent)) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null && view != null) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(motionEvent);
        }
        return (getWindow().superDispatchTouchEvent(motionEvent) || onTouchEvent(motionEvent));
    }

    /**
     * 判断EditText的位置
     *
     * @param view        视图类
     * @param motionEvent 监听事件
     * @return 是否显示隐藏
     */
    public boolean isShouldHideInput(View view, MotionEvent motionEvent) {
        if (view != null && (view instanceof EditText)) {
            int[] leftTop = {0, 0};
            // 获取输入框当前的location位置
            view.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + view.getHeight();
            int right = left + view.getWidth();
            // 点击的是输入框区域，则保留点击EditText的事件
            return (motionEvent.getX() < left || motionEvent.getX() > right || motionEvent.getY() < top || motionEvent.getY() > bottom);
        }
        return false;
    }

}
