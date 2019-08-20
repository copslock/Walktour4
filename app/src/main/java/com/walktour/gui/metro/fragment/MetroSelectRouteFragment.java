package com.walktour.gui.metro.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.gui.R;
import com.walktour.gui.metro.MetroSelectRouteActivity;
import com.walktour.service.metro.MetroFactory;
import com.walktour.service.metro.model.MetroCity;
import com.walktour.service.metro.model.MetroRoute;

import java.util.ArrayList;
import java.util.List;

/**
 * 地铁线路选择列表界面
 * 
 * @author jianchao.wang
 *
 */
public class MetroSelectRouteFragment extends Fragment implements OnItemClickListener, OnClickListener {
	/** 地铁线路工厂类 */
	private MetroFactory mFactory;
	/** 当前选择的城市 */
	private MetroCity mCity;
	/** 线路列表 */
	private List<MetroRoute> mRoutes = new ArrayList<MetroRoute>();
	/** 列表适配器 */
	private MetroRouteAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.mFactory = MetroFactory.getInstance(this.getActivity());
		View view = inflater.inflate(R.layout.activity_metro_select_route, container, false);
		String cityName = this.getArguments().getString("city_name");
		this.mCity = this.mFactory.getCityByName(cityName);
		this.findView(view);
		return view;
	}

	/**
	 * 视图设置
	 */
	private void findView(View view) {
		TextView title = (TextView) view.findViewById(R.id.title_txt);
		title.setText(R.string.metro_select_route);
		ImageButton pointer = (ImageButton) view.findViewById(R.id.pointer);
		pointer.setOnClickListener(this);
		ListView list = (ListView) view.findViewById(R.id.metro_route_list);
		this.mRoutes.addAll(this.mCity.getRoutes());
		this.mAdapter = new MetroRouteAdapter(this.getActivity(), R.layout.activity_metro_select_route_row, this.mRoutes);
		list.setAdapter(this.mAdapter);
		list.setOnItemClickListener(this);
	}

	/**
	 * 路线列表适配器类
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class MetroRouteAdapter extends ArrayAdapter<MetroRoute> {
		private int mResourceId;

		public MetroRouteAdapter(Context context, int textViewResourceId, List<MetroRoute> objects) {
			super(context, textViewResourceId, objects);
			this.mResourceId = textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = LayoutInflater.from(this.getContext()).inflate(this.mResourceId, null);
			}
			TextView routeName = (TextView) view.findViewById(R.id.route_name);
			TextView routeDesc = (TextView) view.findViewById(R.id.route_desc);
			MetroRoute route = this.getItem(position);
			routeName.setText(route.getName());
			routeDesc.setText(route.getRouteDesc());
			if (route.equals(mFactory.getCurrentRoute(false))) {
				routeName.setTextColor(this.getContext().getResources().getColor(R.color.light_blue));
				routeDesc.setTextColor(this.getContext().getResources().getColor(R.color.light_blue));
			}
			return view;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getId() == R.id.metro_route_list) {
			MetroRoute route = this.mRoutes.get(position);
			MetroSelectRouteActivity activity = (MetroSelectRouteActivity) this.getActivity();
			activity.setStationFragment(route);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pointer:
			this.getActivity().finish();
			break;
		}
	}

}
