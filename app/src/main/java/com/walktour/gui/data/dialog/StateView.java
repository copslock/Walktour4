package com.walktour.gui.data.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.walktour.gui.R;
import com.walktour.gui.data.FilterKey;

import java.util.ArrayList;

public class StateView extends BaseView{

	private View mView;
	private ArrayList<CheckBox> cbList = new ArrayList<CheckBox>();
	
	public StateView(Context context, String type) {
		super(context, type);
		init();
	}
	
	public View getView() {
		return this.mView;
	}
	
	private void init() {
		int uploadState = mPreferences.getInt(FilterKey.KEY_UPLOADED_STATE + type, -100);
		mView = LayoutInflater.from(mContext).inflate(R.layout.state_fragment, null);
		final CheckBox cb_unselect_upload = (CheckBox)this.mView.findViewById(R.id.radio0);
		final CheckBox cb_upload_fail = (CheckBox)this.mView.findViewById(R.id.radio1);
		final CheckBox cb_upload_waiting = (CheckBox)this.mView.findViewById(R.id.radio2);
		final CheckBox cb_uploaded = (CheckBox)this.mView.findViewById(R.id.radio3);
		cbList.add(cb_unselect_upload);
		cbList.add(cb_upload_fail);
		cbList.add(cb_upload_waiting);
		cbList.add(cb_uploaded);
		
		for (int i = 0; i < cbList.size(); i++) {
			cbList.get(i).setTag(i);
			cbList.get(i).setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
					int position = Integer.parseInt(arg0.getTag() + "");
					if (isChecked) {
						changeCheckBoxState(position);
						mPreferences.edit().putInt(FilterKey.KEY_UPLOADED_STATE + type, getUploadState(position)).commit();
					}else {
						mPreferences.edit().putInt(FilterKey.KEY_UPLOADED_STATE + type, -100).commit();

					}
				}
			});
		}
		if (uploadState != -100) {
			cbList.get(getUploadStatePosition(uploadState)).setChecked(true);
		}
//		if (uploadState == 0) {
//			cb_uploaded.setChecked(false);
//			cb_unUpload.setChecked(false);
//		} else if (uploadState == 1) {
//			cb_uploaded.setChecked(true);
//			cb_unUpload.setChecked(false);
//		} else if (uploadState == 2) {
//			cb_uploaded.setChecked(false);
//			cb_unUpload.setChecked(true);
//		}
//		cb_uploaded.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
//				if (isChecked) {
//					cb_unUpload.setChecked(false);
//					mPreferences.edit().putInt(FilterKey.KEY_UPLOADED_STATE + type, 1).commit();
//				}else {
//					if (!cb_unUpload.isChecked()) {
//						mPreferences.edit().putInt(FilterKey.KEY_UPLOADED_STATE + type, 0).commit();
//					}
//				}
//			}
//		});
//		
//		cb_unUpload.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
//				if (isChecked) {
//					cb_uploaded.setChecked(false);
//					mPreferences.edit().putInt(FilterKey.KEY_UPLOADED_STATE + type, 2).commit();
//				} else {
//					if (!cb_uploaded.isChecked()) {
//						mPreferences.edit().putInt(FilterKey.KEY_UPLOADED_STATE + type, 0).commit();
//					}
//				}
//			}
//		});
		
	}
	
	private void changeCheckBoxState(int currentCheckBox) {
		for (int i = 0; i < cbList.size(); i++) {
			if (i != currentCheckBox) {
				cbList.get(i).setChecked(false);
			} 
		}
	}
	
	private int getUploadState(int position) {
		int result = -1;
		if (position == 0) {
			result = -1;
		} else if (position == 1) {
			result = -2;
		} else if (position == 2) {
			result = 0;
		} else if (position == 3) {
			result = 100;
		}
		return result;
	}
	
	private int getUploadStatePosition(int state) {
		int result = -1;
		if (state == -1) {
			result = 0;
		} else if (state == -2) {
			result = 1;
		} else if (state == 0) {
			result = 2;
		} else if (state == 100) {
			result = 3;
		}
		return result;
	}
}
