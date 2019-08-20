package com.walktour.gui.data.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.walktour.control.adapter.CustomPagerAdapter;
import com.walktour.gui.R;
import com.walktour.gui.data.FilterKey;

import java.util.ArrayList;
import java.util.List;

/**
 * 筛选view
 * @author MSI
 *
 */
public class ScreeningView extends BaseView {

	private View mView;
	private List<View> contentViews = new ArrayList<View>(); 
	private List<RadioButton> dialogTabButtons = new ArrayList<RadioButton>();
	public TimeView mTimeView;
	public BusinessView mBusinessView;
	public NetworkView mNetworkView;
	private ViewPager mPager;
	public Button btnSummit;
	public Button btnClear;

	public ScreeningView(Context context, String type) {
		super(context, type);
		init();
	}
	
	public View getView() {
		return this.mView;
	}
	
	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.btn_clear:
			clearSettings();
			if (mCallBack != null) {
				mCallBack.onClear();
			}
			break;
		case R.id.btn_filter:
			checkCustomTimeRange();
			saveSettings();
			if (mCallBack != null) {
				mCallBack.onSummit();
			}
			break;
		default:
			break;
		}
		
	}

	/**
	 * 检查筛选是否选择自定义时间但没有选择起始、结束时间,如果没有设置起始结束时间，则默认勾选回今天选项
	 */
	private void checkCustomTimeRange(){
		SharedPreferences sp = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);;
		int checkPosition = sp.getInt(FilterKey.KEY_TIME_POSITION + type, -1);
		if (checkPosition == 3) {
			String timeRangeStr = sp.getString(FilterKey.KEY_TIME_RANGE + type, "");
			if (TextUtils.isEmpty(timeRangeStr)){
				//如果选择自定义时间但是未选择起始结束时间的话，默认勾选当天
				sp.edit().putInt(FilterKey.KEY_TIME_POSITION + type, 0).commit();
				mTimeView.getRadioButton(0).setChecked(true);
			}
		}
	}
	
	private void init() {
		this.mView = inflater.inflate(R.layout.screen_view_root, null);
		initContentView();
		btnClear = (Button)this.mView.findViewById(R.id.btn_clear);//清空
		btnSummit = (Button)this.mView.findViewById(R.id.btn_filter);//筛选
		btnClear.setOnClickListener(this);
		btnSummit.setOnClickListener(this);
		dialogTabButtons.clear();
		dialogTabButtons.add((RadioButton)this.mView.findViewById(R.id.rb1));
		dialogTabButtons.add((RadioButton)this.mView.findViewById(R.id.rb2));
		dialogTabButtons.add((RadioButton)this.mView.findViewById(R.id.rb3));
		dialogTabButtons.add((RadioButton)this.mView.findViewById(R.id.rb4));
		for (int i = 0; i < dialogTabButtons.size(); i++) {
			dialogTabButtons.get(i).setOnClickListener(new MyOnClickListener(i));
		}
		CustomPagerAdapter adapter = new CustomPagerAdapter(contentViews);
        mPager = (ViewPager)this.mView.findViewById(R.id.pager);
        mPager.setAdapter(adapter);
        mPager.setOffscreenPageLimit(4);
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器
        adapter.notifyDataSetChanged();
	}
	
	private void initContentView() {
		mTimeView = new TimeView(mContext, type);
		mBusinessView = new BusinessView(mContext, type);
		mNetworkView = new NetworkView(mContext, type);
		contentViews.add(mTimeView.getView());
		contentViews.add(new StateView(mContext, type).getView());
		contentViews.add(mBusinessView.getView());
		contentViews.add(mNetworkView.getView());
	}
	
	/**
	 * 清除所有设置
	 */
	private void clearSettings() {
		//清除时间设置
		mPreferences.edit().putInt(FilterKey.KEY_TIME_POSITION + type, -1).commit();
		//清除状态
		mPreferences.edit().putInt(FilterKey.KEY_UPLOADED_STATE + type, -100).commit();
		//清除业务
		mBusinessView.selectedList.clear();
		for (int i = 0; i <mBusinessView.adapter.getDatas().length ; i++) {
			mBusinessView.adapter.getDatas()[i].checked = false;
			mBusinessView.adapter.notifyDataSetChanged();
		}
		//清除网络
		mNetworkView.selectedList.clear();
		for (int i = 0; i < mNetworkView.adapter.getDatas().size(); i++) {
			mNetworkView.adapter.getDatas().get(i).isChecked = false;
			mNetworkView.adapter.notifyDataSetChanged();
		}
		mPreferences.edit().putBoolean(FilterKey.KEY_IS_SCREEN_SETTING + type, false).commit();//筛选是否设置过
		mPreferences.edit().putString(FilterKey.KEY_BUSINESS_SELECTED + type, "").commit();
		mPreferences.edit().putString(FilterKey.KEY_NETWORK_TYPE_SELECTED + type, "").commit();
	}
	
	/**
	 * 保存设置
	 */
	private void saveSettings() {
		//保存业务设置
		String keys = "";
		for (int i = 0; i < mBusinessView.selectedList.size(); i++) {
			keys += mBusinessView.selectedList.get(i).typeKey + ",";
		}
		if (keys.contains(",")) {
			keys = keys.substring(0, keys.lastIndexOf(","));
		}
		//保存网络设置
		String networkTypes = "";
		for (int i = 0; i < mNetworkView.selectedList.size(); i++) {
			networkTypes += mNetworkView.selectedList.get(i).getNetType() + ",";
		}
		if (networkTypes.contains(",")) {
			networkTypes = networkTypes.substring(0, networkTypes.lastIndexOf(","));
		}
		mPreferences.edit().putBoolean(FilterKey.KEY_IS_SCREEN_SETTING + type, true).commit();//筛选是否设置过
		mPreferences.edit().putString(FilterKey.KEY_BUSINESS_SELECTED + type, keys).commit();//保存业务
		mPreferences.edit().putString(FilterKey.KEY_NETWORK_TYPE_SELECTED + type, networkTypes).commit();//保存网络
	}
	
	 public class MyOnClickListener implements View.OnClickListener {
	        private int index = 0;

	        public MyOnClickListener(int i) {
	            index = i;
	        }

	        @Override
	        public void onClick(View v) {
	            mPager.setCurrentItem(index);
	        }
	    };
	    
	    public class MyOnPageChangeListener implements OnPageChangeListener{  
	      
	    @Override  
	    public void onPageScrolled(int arg0, float arg1, int arg2) {  
	          
	    }  
	      
	    @Override  
	    public void onPageScrollStateChanged(int arg0) {  
	          
	    }  
	      
	    @Override  
	    public void onPageSelected(int arg0) {   
	    	dialogTabButtons.get(arg0).setChecked(true);
	    }  

	    }
	    
	    
		private ClickListenerCallBack mCallBack;
		public void setClickListenerCallBack(ClickListenerCallBack callback) {
			this.mCallBack = callback;
		}
	
}
