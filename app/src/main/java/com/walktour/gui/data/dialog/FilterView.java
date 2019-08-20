package com.walktour.gui.data.dialog;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.walktour.gui.R;
import com.walktour.gui.data.FilterKey;

public class FilterView extends BaseView{

	private View mView;
	private EditText contentEdit;
	
	public FilterView(Context context, String type) {
		super(context, type);
		init();
	}
	
	public View getView() {
		return this.mView;
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_c:
			contentEdit.setText("3");
			mPreferences.edit().putString(FilterKey.KEY_TIME_LIMIT + type, "3").commit();
			mPreferences.edit().putBoolean(FilterKey.KEY_IS_FILTER_SETTING + type, false).commit();
			if (mCallBack != null) {
				mCallBack.onClear();
			}
			break;
		case R.id.btn_s:
			String timeLimit = contentEdit.getText().toString();
			timeLimit = timeLimit.trim().equals("") ? "3" : timeLimit;
			mPreferences.edit().putString(FilterKey.KEY_TIME_LIMIT + type, timeLimit).commit();
			mPreferences.edit().putBoolean(FilterKey.KEY_IS_FILTER_SETTING + type, true).commit();
			if (mCallBack != null) {
				mCallBack.onSummit();
			}
			break;

		default:
			break;
		}
	}
	
	private void init() {
		mView = inflater.inflate(R.layout.filter_view_root, null);
		contentEdit = (EditText)this.mView.findViewById(R.id.edit_start_time);
		this.mView.findViewById(R.id.btn_c).setOnClickListener(this);
		this.mView.findViewById(R.id.btn_s).setOnClickListener(this);
		initData();
	}
	private void initData() {
		String timeLimit = mPreferences.getString(FilterKey.KEY_TIME_LIMIT + type, "3");
		contentEdit.setText(timeLimit);
	}
	
	private ClickListenerCallBack mCallBack;
	public void setClickListenerCallBack(ClickListenerCallBack callback) {
		this.mCallBack = callback;
	}
}
