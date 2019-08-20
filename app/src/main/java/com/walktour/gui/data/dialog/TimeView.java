package com.walktour.gui.data.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.walktour.gui.R;
import com.walktour.gui.data.FilterKey;

import java.util.ArrayList;
import java.util.List;

public class TimeView extends BaseView{

	private LayoutInflater inflater;
	private View mView;
	private List<RadioButton> radioList = new ArrayList<RadioButton>();
	
	public TimeView(Context context, String type) {
		super(context, type);
		init();
	}
	
	public View getView() {
		return this.mView;
	}
	
	private void init() {
		int checkPosition = mPreferences.getInt(FilterKey.KEY_TIME_POSITION + type, -1);
		inflater = LayoutInflater.from(mContext);
		mView = inflater.inflate(R.layout.time_fragment, null);
		RadioGroup rg = (RadioGroup)mView.findViewById(R.id.radioGroup1);
		radioList.add((RadioButton)mView.findViewById(R.id.radio0));
		radioList.add((RadioButton)mView.findViewById(R.id.radio1));
		radioList.add((RadioButton)mView.findViewById(R.id.radio2));
		radioList.add((RadioButton)mView.findViewById(R.id.radio3));
		if(checkPosition != -1) {
			radioList.get(checkPosition).setChecked(true);
		}
		if (checkPosition == 3) {
			String timeRang = mPreferences.getString(FilterKey.KEY_TIME_RANGE + type, "");
			if (!timeRang.equals("")) {
	    		String[] tmps = timeRang.split("~");
	    		String str = tmps[0] + "\n" + tmps[1];
	    		getSelfRadioButton().setText(str);
	    	} 
		}
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int id) {
				switch (id) {
				case R.id.radio0:
					mPreferences.edit().putInt(FilterKey.KEY_TIME_POSITION + type, 0).commit();
					break;
				case R.id.radio1:
					mPreferences.edit().putInt(FilterKey.KEY_TIME_POSITION + type, 1).commit();
					break;
				case R.id.radio2:
					mPreferences.edit().putInt(FilterKey.KEY_TIME_POSITION + type, 2).commit();
					break;
				case R.id.radio3:
					mPreferences.edit().putInt(FilterKey.KEY_TIME_POSITION + type, 3).commit();
					break;
				default:
					break;
				}
			}
		});
		
	}

	/**
	 * 获取RadioButton
	 * @param position 第几个
	 * @return RadioButton
	 */
	public RadioButton getRadioButton(int position){
		if (radioList.size() > position) {
			return radioList.get(position);
		}else{
			return null;
		}
	}
	
	public RadioButton getSelfRadioButton() {
		if (radioList.size() > 0) {
			return radioList.get(3);
		}
		return null;
	}
	
}
