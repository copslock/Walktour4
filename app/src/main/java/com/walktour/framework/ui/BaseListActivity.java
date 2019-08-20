package com.walktour.framework.ui;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import com.walktour.gui.R;

/**
 * Activity基类
 * 
 * @author weirong.fan
 * 
 */
public abstract class BaseListActivity extends ListActivity {
	/** 进度提示 */
	public ProgressDialog progressDialog;
	
	/** 上下文对象 */
	public Context context = null;
	/** 列表显示装配器 */
	protected BaseAdapter adapter = null;
	
	/** 初始化视图控件 */
	public abstract void initViews();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		 ActivityManager.addActivity(this);
	}
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
        this.finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }
	@Override
	public void finish() {
		super.finish();
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

	/**
	 * <p>
	 * Description:打开进度条
	 * </p>
	 * 
	 * @author weirong.fan
	 * @date 2012-5-29 上午10:49:57
	 * @param txt
	 *            提示信息
	 * @return void
	 */
	protected void openDialog(String txt) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(txt);
		progressDialog.show();
	}
	
	/**
	 * <p>
	 * Description:关闭进度条
	 * </p>
	 * 
	 * @author weirong.fan
	 * @date 2012-5-29 上午10:50:59
	 * @return void
	 */
	protected void closeDialog() {
		progressDialog.dismiss();
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
	 * 初始化View
	 * 
	 * @param viewId
	 *            view的ID
	 * @return Button
	 */
	protected Button initButton(int viewId) {
		return (Button) this.findViewById(viewId);
	}
	
	/**
	 * 初始化View
	 * 
	 * @param viewId
	 *            view的ID
	 * @return Button
	 */
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
	 * @return Spinner
	 */
	protected ProgressBar initProgressBar(int viewId) {
		return (ProgressBar) this.findViewById(viewId);
	}
	
	 
	
	/**
	 * 初始化View
	 * 
	 * @param viewId
	 *            view的ID
	 * @return Spinner
	 */
	protected DatePicker initDatePicker(int viewId) {
		return (DatePicker) this.findViewById(viewId);
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

	/**
	 * Activity跳转，Activity保留
	 * 
	 * @param cls
	 *            跳转的类
	 * 
	 */
	protected void jumpActivity(Class<?> cls) {
		Intent i = new Intent(this, cls);
		startActivity(i);
		overridePendingTransition(R.anim.open_next, R.anim.close_main);
	}

	/**
	 * Activity跳转，Activity保留,返回值
	 * 
	 * @param cls
	 *            跳转的类
	 * 
	 */
	protected void jumpActivityForResult(Class<?> cls, int requestCode) {
		Intent i = new Intent(this, cls);
		this.startActivityForResult(i, requestCode);
		overridePendingTransition(R.anim.open_next, R.anim.close_main);
	}

	protected void jumpActivityForResult(Class<?> cls, int requestCode,Bundle bundle) {
		Intent i = new Intent(this, cls);
		i.putExtras(bundle);
		this.startActivityForResult(i, requestCode);
		overridePendingTransition(R.anim.open_next, R.anim.close_main);
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
		Intent i = new Intent(this, cls);
		i.putExtras(bundle);
		startActivity(i);
		overridePendingTransition(R.anim.open_next, R.anim.close_main);
	}
	
	/**
	 * Activity跳转，Activity保留,请传递数据值
	 * 
	 * @param cls
	 *            跳转的类
	 * @param bundle
	 *            数据值
	 */
	protected void jumpActivity(Intent intent) {
		startActivity(intent);
		overridePendingTransition(R.anim.open_next, R.anim.close_main);
	}
}
