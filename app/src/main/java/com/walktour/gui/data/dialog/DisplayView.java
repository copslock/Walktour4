package com.walktour.gui.data.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.Utils.WalkStruct.TestType;
import com.walktour.gui.R;
import com.walktour.gui.data.FilterKey;

public class DisplayView extends BaseView {

	private View mView;
	private CheckBox cbException;
	private CheckBox cbGoOrNogo;
	private CheckBox cbWorkOrder;
//	private TextView tvRouteName;
	private RelativeLayout layoutWorkorder;
	
	private boolean checkException = false;
	private boolean checkGoOrNogo = false;
	private boolean checkWorkOrder = false;
	private ApplicationModel appModel=ApplicationModel.getInstance();
	public DisplayView(Context context, String type) {
		super(context, type);
		init();
	}
	
	public View getView() {
		return this.mView;
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_clear:
			reset();
			mPreferences.edit().putBoolean(FilterKey.KEY_IS_DISPLAY_SETTING + type, false).commit();
			if (mCallBack != null) {
				mCallBack.onClear();
			}
			break;
		
		case R.id.btn_filter:
			save();
			
			if (mCallBack != null) {
				mCallBack.onSummit();
			}
			break;

		default:
			break;
		}
		
	}
	
	private void init() {
		boolean goOrNogo = false;
		boolean exception = false;
		boolean workorder = false;
		if (type.equals(TestType.DT.name()) || type.equals(TestType.CQT.name())) {
			goOrNogo = mPreferences.getBoolean(FilterKey.KEY_GO_OR_NOGO + type, false);
			exception = mPreferences.getBoolean(FilterKey.KEY_EXCEPTION + type, true);
		} else {
			goOrNogo = mPreferences.getBoolean(FilterKey.KEY_GO_OR_NOGO + type, true);
			exception = mPreferences.getBoolean(FilterKey.KEY_EXCEPTION + type, false);
		}
		workorder = mPreferences.getBoolean(FilterKey.KEY_WORK_ORDER + type, false);
		mView = LayoutInflater.from(mContext).inflate(R.layout.display_view_root, null);
		this.mView.findViewById(R.id.btn_clear).setOnClickListener(this);
		this.mView.findViewById(R.id.btn_filter).setOnClickListener(this);
		cbException = (CheckBox)mView.findViewById(R.id.check_exception);
		cbGoOrNogo = (CheckBox)mView.findViewById(R.id.check_go_nogo);
		layoutWorkorder = (RelativeLayout)mView.findViewById(R.id.layout_work_order);
		cbWorkOrder = (CheckBox)mView.findViewById(R.id.check_work_order);
//		tvRouteName = (TextView)mView.findViewById(R.id.str_route_name);
		cbException.setChecked(exception);
		cbGoOrNogo.setChecked(goOrNogo);
		cbWorkOrder.setChecked(workorder);
		layoutWorkorder.setVisibility((type.equals(TestType.DT.name() )|| type.equals(TestType.CQT.name())) ? View.GONE : View.VISIBLE);
		
		
		if(appModel.getSelectScene()==SceneType.Metro||appModel.getSelectScene()==SceneType.HighSpeedRail){
			cbWorkOrder.setText(R.string.str_title_work_route);
		}
		
		//自动测试和多网测试也要隐藏
		if(appModel.getSelectScene()==SceneType.Auto||appModel.getSelectScene()==SceneType.MultiTest){
			layoutWorkorder.setVisibility(View.GONE);
		}
		
		cbException.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton button, boolean isChecked) {
//				mPreferences.edit().putBoolean(FilterKey.KEY_EXCEPTION + type, isChecked).commit();
				checkException = isChecked;
				cbGoOrNogo.setChecked(!isChecked);
			}
		});
		
		cbGoOrNogo.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton button, boolean isChecked) {
//				mPreferences.edit().putBoolean(FilterKey.KEY_GO_OR_NOGO + type, isChecked).commit();
				checkGoOrNogo = isChecked;
				cbException.setChecked(!isChecked);
			}
		});
		
		cbWorkOrder.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton button, boolean isChecked) {
				checkWorkOrder = isChecked;
//				mPreferences.edit().putBoolean(FilterKey.KEY_WORK_ORDER + type, isChecked).commit();

			}
		});
	}
	
	private void reset() {
		cbException.setChecked(true);
		cbGoOrNogo.setChecked(false);
	}
	
	private void save() {
		mPreferences.edit().putBoolean(FilterKey.KEY_EXCEPTION + type, checkException).commit();
		mPreferences.edit().putBoolean(FilterKey.KEY_GO_OR_NOGO + type, checkGoOrNogo).commit();
		mPreferences.edit().putBoolean(FilterKey.KEY_WORK_ORDER + type, checkWorkOrder).commit();
		if (isDefaultSetting()) {
			mPreferences.edit().putBoolean(FilterKey.KEY_IS_DISPLAY_SETTING + type, false).commit();
		} else {
			mPreferences.edit().putBoolean(FilterKey.KEY_IS_DISPLAY_SETTING + type, true).commit();
		}
	}
	
	public void updateState() {
		boolean goOrNogo = false;
		boolean exception = false;
		boolean workorder = false;
		if (type.equals(TestType.DT.name()) || type.equals(TestType.CQT.name())) {
			goOrNogo = mPreferences.getBoolean(FilterKey.KEY_GO_OR_NOGO + type, false);
			exception = mPreferences.getBoolean(FilterKey.KEY_EXCEPTION + type, true);
		} else {
			goOrNogo = mPreferences.getBoolean(FilterKey.KEY_GO_OR_NOGO + type, true);
			exception = mPreferences.getBoolean(FilterKey.KEY_EXCEPTION + type, false);
		}
		workorder = mPreferences.getBoolean(FilterKey.KEY_WORK_ORDER + type, false);
		cbException.setChecked(exception);
		cbGoOrNogo.setChecked(goOrNogo);
		cbWorkOrder.setChecked(workorder);
	}
	
	private boolean isDefaultSetting() {
		boolean result = false;
		boolean goOrNogo = mPreferences.getBoolean(FilterKey.KEY_GO_OR_NOGO + type, false);
		boolean exception = mPreferences.getBoolean(FilterKey.KEY_EXCEPTION + type, false);
		boolean workorder = mPreferences.getBoolean(FilterKey.KEY_WORK_ORDER + type, false);

		if (type.equals(TestType.DT.name()) || type.equals(TestType.CQT.name())) {
			if (exception) result = true;
			result = exception;
		} else {
			if (goOrNogo && !workorder) {
				result = true;
			} else {
				result = false;
			}
		}
		return result;
	}
	
	private ClickListenerCallBack mCallBack;
	public void setClickListenerCallBack(ClickListenerCallBack callback) {
		this.mCallBack = callback;
	}
}
