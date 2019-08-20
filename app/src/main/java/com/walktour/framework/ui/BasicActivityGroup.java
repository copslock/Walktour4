package com.walktour.framework.ui;

import android.app.ActivityGroup;
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

@SuppressWarnings("deprecation")
public class BasicActivityGroup extends ActivityGroup {
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
}
