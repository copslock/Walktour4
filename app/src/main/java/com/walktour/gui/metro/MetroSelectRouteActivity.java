package com.walktour.gui.metro;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.walktour.gui.R;
import com.walktour.gui.metro.fragment.MetroSelectRouteFragment;
import com.walktour.gui.metro.fragment.MetroSelectStationFragment;
import com.walktour.service.metro.MetroFactory;
import com.walktour.service.metro.model.MetroCity;
import com.walktour.service.metro.model.MetroRoute;

/**
 * 地铁线路选择界面
 * 
 * @author jianchao.wang
 *
 */
public class MetroSelectRouteActivity extends FragmentActivity {
	/** 返回结果ID */
	public static final int RESULT_CODE = 1001;
	/** 地铁线路工厂类 */
	private MetroFactory mFactory;
	/** 当前选择的城市名称 */
	private String mCityName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_metro_select_main);
		this.mFactory = MetroFactory.getInstance(this);
		MetroCity city = this.mFactory.getCurrentCity(false);
		if (city != null)
			this.mCityName = city.getName();
		this.setRouteFragment();
	}

	/**
	 * 设置路线视图
	 */
	public void setRouteFragment() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		MetroSelectRouteFragment routeFragment = new MetroSelectRouteFragment();
		Bundle args = new Bundle();
		args.putString("city_name", this.mCityName);
		routeFragment.setArguments(args);
		transaction.replace(R.id.id_content, routeFragment);
		transaction.commit();
	}

	/**
	 * 设置站点视图
	 * 
	 * @param route
	 *          路线
	 */
	public void setStationFragment(MetroRoute route) {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		MetroSelectStationFragment stationFragment = new MetroSelectStationFragment();
		Bundle args = new Bundle();
		args.putString("city_name", this.mCityName);
		args.putLong("route_id", route.getId());
		stationFragment.setArguments(args);
		transaction.replace(R.id.id_content, stationFragment);
		transaction.commit();
	}

}
