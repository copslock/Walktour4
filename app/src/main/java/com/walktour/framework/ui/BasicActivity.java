package com.walktour.framework.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import com.walktour.base.util.LogUtil;
import com.walktour.base.util.SystemBarUtil;
import com.walktour.gui.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
/***
 * Activity基类,提供Activity基本方法操作
 */
public abstract class BasicActivity extends Activity implements android.view.View.OnClickListener {

	public ImageButton backBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initSystemBarTint();
		ActivityManager.addActivity(this);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.open_main, R.anim.close_next);
	}

	public void finishWithoutAnim() {
		super.finish();
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
        this.finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }
    
	@Override
	public void onBackPressed() { 
		super.onBackPressed();
		ActivityManager.removeActivity(this);
		this.finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
	}	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			ActivityManager.removeActivity(this);
			finish();  
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void dismissDialog(DialogInterface dialog, boolean isShow) {
		try {
			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, isShow);
			dialog.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			BasicActivity.this.finish();
			overridePendingTransition(R.anim.open_main, R.anim.close_next);
			break;

		default:
			break;
		}
	}

	/**
	 * 初始化View
	 * 
	 * @param viewId
	 *            view的ID
	 * @return View
	 */
	protected ViewFlipper initViewFlipper(int viewId) {
		return (ViewFlipper) this.findViewById(viewId);
	}

	/**
	 * 初始化View
	 * 
	 * @param viewId
	 *            view的ID
	 * @return View
	 */
	protected View initView(int viewId) {
		return this.findViewById(viewId);
	}

	/**
	 * 初始化View
	 * 
	 * @param viewId
	 *            view的ID
	 * @return Button
	 */
	protected Button initButton(int viewId) {
		return (Button) this.findViewById(viewId);
	}

	protected ImageButton initImageButton(int viewId) {
		return (ImageButton) this.findViewById(viewId);
	}

	/**
	 * 初始化View
	 * 
	 * @param viewId
	 *            view的ID
	 * @return Button
	 */
	protected CheckBox initCheckBox(int viewId) {
		return (CheckBox) this.findViewById(viewId);
	}

	/**
	 * 初始化View
	 * 
	 * @param viewId
	 *            view的ID
	 * @return TextView
	 */
	protected TextView initTextView(int viewId) {
		return (TextView) this.findViewById(viewId);
	}

	/**
	 * 初始化View
	 * 
	 * @param viewId
	 *            view的ID
	 * @return RadioButton
	 */
	protected RadioButton initRadioButton(int viewId) {
		return (RadioButton) this.findViewById(viewId);
	}

	/**
	 * 初始化View
	 * 
	 * @param viewId
	 *            view的ID
	 * @return AutoCompleteTextView
	 */
	protected AutoCompleteTextView initAutoCompleteTextView(int viewId) {
		return (AutoCompleteTextView) this.findViewById(viewId);
	}

	/**
	 * 初始化View
	 * 
	 * @param viewId
	 *            view的ID
	 * @return ToggleButton
	 */
	protected ToggleButton initToggleButton(int viewId) {
		return (ToggleButton) this.findViewById(viewId);
	}

	/**
	 * 初始化View
	 * 
	 * @param viewId
	 *            view的ID
	 * @return EditText
	 */
	protected EditText initEditText(int viewId) {
		return (EditText) this.findViewById(viewId);
	}

	/**
	 * 初始化View
	 * 
	 * @param viewId
	 *            view的ID
	 * @return ImageView
	 */
	protected ImageView initImageView(int viewId) {
		return (ImageView) this.findViewById(viewId);
	}

	/**
	 * 初始化View
	 * 
	 * @param viewId
	 *            view的ID
	 * @return Spinner
	 */
	protected Spinner initSpinner(int viewId) {
		return (Spinner) this.findViewById(viewId);
	}

	/**
	 * 初始化View
	 * 
	 * @param viewId
	 *            view的ID
	 * @return ProgressBar
	 */
	protected ProgressBar initProgressBar(int viewId) {
		return (ProgressBar) this.findViewById(viewId);
	}

	/**
	 * 初始化LineLayout
	 * 
	 * @param viewId
	 *            view的ID
	 * @return LinearLayout
	 */
	protected LinearLayout initLinearLayout(int viewId) {
		return (LinearLayout) this.findViewById(viewId);
	}

	/***
	 * 初始化RelativeLayout
	 * 
	 * @param viewId
	 *            view的ID
	 * @return RelativeLayout
	 */
	protected RelativeLayout initRelativeLayout(int viewId) {
		return (RelativeLayout) this.findViewById(viewId);
	}

	/***
	 * 初始化ListView
	 * 
	 * @param viewId
	 *            view的ID
	 * @return ListView
	 */
	protected ListView initListView(int viewId) {
		return (ListView) this.findViewById(viewId);
	}

	/**
	 * Activity跳转，Activity保留
	 * 
	 * @param cls
	 *            跳转的类
	 * 
	 */
	protected void jumpActivity(Class<?> cls) {
		this.jumpActivity(cls, null);
	}

	/**
	 * Activity跳转，Activity保留,返回值
	 * 
	 * @param cls
	 *            跳转的类
	 * 
	 */
	protected void jumpActivityForResult(Class<?> cls, int requestCode) {
		this.jumpActivityForResult(cls, null, requestCode);
	}

	/**
	 * Activity跳转，Activity保留,请传递数据值
	 * 
	 * @param cls
	 *            跳转的类
	 * @param bundle
	 *            数据值
	 */
	protected void jumpActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent(this, cls);
		if (bundle != null)
			intent.putExtras(bundle);
		startActivity(intent);
		overridePendingTransition(R.anim.open_next, R.anim.close_main);
		intent = null;
	}

	/**
	 * Activity跳转，Activity保留,返回值
	 * 
	 * @param cls
	 *            跳转的类
	 * 
	 */
	protected void jumpActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
		Intent intent = new Intent(this, cls);
		if (bundle != null)
			intent.putExtras(bundle);
		this.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.open_next, R.anim.close_main);
		intent = null;
	}

	/**
	 * Activity跳转，Activity保留,请传递数据值
 	 */
	protected void jumpActivity(Intent intent) {
		startActivity(intent);
		overridePendingTransition(R.anim.open_next, R.anim.close_main);
		intent=null;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		setIconEnable(menu, true);
		return true;
	}

	// enable为true时，菜单添加图标有效，enable为false时无效。4.0系统默认无效
	private void setIconEnable(Menu menu, boolean enable) {
		try {
			Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
			Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
			m.setAccessible(true);

			// MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
			m.invoke(menu, enable);

		} catch (Exception e) {
			e.printStackTrace();
		}
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
		return false;
	}
	/** 子类可以重写决定是否使用透明状态栏 */
	protected boolean translucentStatusBar() {
		return false;
	}
	/** 子类可以重写改变状态栏颜色 */
	protected int setStatusBarColor() {
		return R.color.app_main_color;
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
		getTheme().resolveAttribute(com.walktour.base.R.attr.colorPrimaryDark, typedValue, true);
		return typedValue.data;
	}

	/**
	 * 获取主题色
	 * */
	public int getColorPrimary() {
		TypedValue typedValue = new TypedValue();
		getTheme().resolveAttribute(com.walktour.base.R.attr.colorPrimary, typedValue, true);
		return typedValue.data;
	}
}
