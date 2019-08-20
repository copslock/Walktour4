package com.walktour.gui.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.control.config.ParameterSetting;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.gui.R;
import com.walktour.model.Parameter;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class DynamicParamterOtherFragment  extends Fragment implements RefreshEventListener {
	
	private Context mContext;
	private DynamicParamView dynamicView;
	List<Parameter> parameters = new ArrayList<Parameter>();
	private ParameterSetting mParameterSet;
	private Parameter[] parameterArray;		//根据网络获取参数
	private String netTypeStr;
	private int page = 3;
	private ScrollView csv;
	private ApplicationModel appModel = ApplicationModel.getInstance();
	
	public DynamicParamterOtherFragment(Context context, int page) {
		this.mContext = context;
		this.page = page;
		mParameterSet = ParameterSetting.getInstance();
		
	}
	
	public int getPage() {
		return this.page;
	}
	
	public void setPage(int page) {
		this.page = page;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.dynamic_param_layout, null);
		initView(rootView);
		return rootView;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		getParamData();
	}

	/**
	 * 初始化view
	 */
	private void initView(View view) {
		view.findViewById(R.id.layout_info).setVisibility(View.GONE);
		csv = (ScrollView) view.findViewById(R.id.scrollView1);
		csv.setVerticalScrollBarEnabled(false);
		LinearLayout customView = (LinearLayout) view.findViewById(R.id.custom_view);
		csv.setFillViewport(true);
		dynamicView = new DynamicParamView(mContext);
		dynamicView.setParamData(parameters);
		customView.addView(dynamicView);
		RefreshEventManager.addRefreshListener(this);
	}

	@Override
	public void onRefreshed(RefreshType refreshType, Object object) {
		switch (refreshType) {
		case ACTION_WALKTOUR_TIMER_CHANGED:
			getParamData();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 获取参数数据信息
	 */
	private void getParamData() {
		if(appModel.isParmDragBack() || !TraceInfoInterface.currentNetType.name().equals(netTypeStr)){
			 netTypeStr = !ApplicationModel.getInstance().isFreezeScreen() ? TraceInfoInterface.currentNetType.name()
					: TraceInfoInterface.decodeFreezeNetType.name();
			 parameters.clear();
			parameterArray = mParameterSet.getTableParametersByNetworkType(netTypeStr);
			Log.i("----", "----parameterArray--" + parameterArray.length);
			Log.i("----", "----other--" + page);
			for (int i = 0; i < parameterArray.length; i++) {
				if (parameterArray[i].getTabIndex() == page) {
					parameters.add(parameterArray[i]);
				}
			}
			Log.i("----", "----" + parameters.size());
			if(appModel.isParmDragBack()){
				appModel.setParmDragBack(false);
			}
		}
		if (dynamicView != null){
			dynamicView.setParamData(parameters);
			csv.measure(dynamicView.getWidth(), dynamicView.calculateViewHeight());
			dynamicView.invalidate();
		}
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		RefreshEventManager.removeRefreshListener(this);
	}

}
