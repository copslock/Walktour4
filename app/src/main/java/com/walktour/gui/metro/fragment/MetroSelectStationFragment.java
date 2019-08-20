package com.walktour.gui.metro.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.gui.R;
import com.walktour.gui.StartDialog;
import com.walktour.gui.metro.MetroSelectRouteActivity;
import com.walktour.service.metro.MetroFactory;
import com.walktour.service.metro.model.MetroCity;
import com.walktour.service.metro.model.MetroRoute;
import com.walktour.service.metro.model.MetroStation;

import java.util.ArrayList;
import java.util.List;

/**
 * 地铁线路站点选择列表界面
 * 
 * @author jianchao.wang
 *
 */
public class MetroSelectStationFragment extends Fragment implements OnItemClickListener, OnClickListener {
	/** 返回结果ID */
	public static final int RESULT_CODE = 1001;
	/** 地铁线路工厂类 */
	private MetroFactory mFactory;
	/** 当前选择的路线 */
	private MetroRoute mRoute;
	/** 路线描述 */
	private TextView mRouteDesc;
	/** 站点列表 */
	private List<MetroStation> mStations = new ArrayList<MetroStation>();
	/** 列表适配器 */
	private MetroStationAdapter mAdapter;
	/** 起始站点 */
	private MetroStation mStartStation = null;
	/** 到达站点 */
	private MetroStation mEndStation = null;
	/** 确定按钮 */
	private Button mOkBtn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.mFactory = MetroFactory.getInstance(this.getActivity());
		View view = inflater.inflate(R.layout.activity_metro_select_station, container, false);
		String cityName = this.getArguments().getString("city_name");
		MetroCity city = this.mFactory.getCityByName(cityName);
		long routeId = this.getArguments().getLong("route_id");
		this.mRoute = city.getRoute(routeId);
		this.mRoute.initStationState();
		this.mStartStation = this.mRoute.getStartStation();
		this.mEndStation = this.mRoute.getEndStation();
		this.findView(view);
		this.setOkButtonState();
		return view;
	}

	/**
	 * 视图设置
	 */
	private void findView(View view) {
		TextView title = (TextView) view.findViewById(R.id.title_txt);
		title.setText(R.string.metro_select_station);
		TextView routeName = (TextView) view.findViewById(R.id.route_name);
		routeName.setText(this.mRoute.getName());
		mRouteDesc = (TextView) view.findViewById(R.id.route_desc);
		ImageButton pointer = (ImageButton) view.findViewById(R.id.pointer);
		pointer.setOnClickListener(this);
		Button clearBtn = (Button) view.findViewById(R.id.btn_clear);
		clearBtn.setOnClickListener(this);
		mOkBtn = (Button) view.findViewById(R.id.btn_ok);
		mOkBtn.setOnClickListener(this);
		TextView orderBtn = (TextView) view.findViewById(R.id.order_btn);
		orderBtn.setOnClickListener(this);
		ListView list = (ListView) view.findViewById(R.id.metro_station_list);
		this.setStations();
		this.mAdapter = new MetroStationAdapter(this.getActivity(), R.layout.activity_metro_select_station_row,
				this.mStations);
		list.setAdapter(this.mAdapter);
		list.setOnItemClickListener(this);
	}

	/**
	 * 根据选择的方向显示当前线路的站点
	 */
	private void setStations() {
		this.mStations.clear();
		this.mStations.addAll(this.mRoute.getStations());
		mRouteDesc.setText(this.mRoute.getRouteDesc());
	}

	/**
	 * 路线列表适配器类
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class MetroStationAdapter extends ArrayAdapter<MetroStation> {
		private int mResourceId;

		public MetroStationAdapter(Context context, int textViewResourceId, List<MetroStation> objects) {
			super(context, textViewResourceId, objects);
			this.mResourceId = textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = LayoutInflater.from(this.getContext()).inflate(this.mResourceId, null);
			}
			TextView stationName = (TextView) view.findViewById(R.id.station_name);
			TextView stationState = (TextView) view.findViewById(R.id.station_state);
			MetroStation station = this.getItem(position);
			stationName.setText(station.getName());
			stationName.setTextColor(this.getContext().getResources().getColor(R.color.app_main_text_color));
			switch (station.getState()) {
			case MetroStation.STATE_CAN_SELECT:
				stationState.setText("");
				break;
			case MetroStation.STATE_START:
				stationState.setText(R.string.metro_station_start);
				stationState.setTextColor(this.getContext().getResources().getColor(R.color.light_blue));
				stationName.setTextColor(this.getContext().getResources().getColor(R.color.light_blue));
				break;
			case MetroStation.STATE_END:
				stationState.setText(R.string.metro_station_end);
				stationState.setTextColor(this.getContext().getResources().getColor(R.color.light_blue));
				stationName.setTextColor(this.getContext().getResources().getColor(R.color.light_blue));
				break;
			case MetroStation.STATE_CANT_SELECT:
				stationName.setTextColor(this.getContext().getResources().getColor(R.color.app_grey_color));
				stationState.setText("");
				break;
			}
			return view;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getId() == R.id.metro_station_list) {
			this.setStationState(position);
		}
	}

	/**
	 * 设置站点状态
	 * 
	 * @param position
	 *          站点序号
	 */
	private void setStationState(int position) {
		MetroStation station = this.mStations.get(position);
		if (station.getState() == MetroStation.STATE_CANT_SELECT)
			return;
		if (station.getState() == MetroStation.STATE_START) {
			for (int i = 0; i < this.mStations.size(); i++) {
				if (i <= position)
					this.mStations.get(i).setState(MetroStation.STATE_CAN_SELECT);
			}
			this.mStartStation = null;
		} else if (station.getState() == MetroStation.STATE_END) {
			for (int i = 0; i < this.mStations.size(); i++) {
				if (i >= position)
					this.mStations.get(i).setState(MetroStation.STATE_CAN_SELECT);
			}
			this.mEndStation = null;
		} else {
			int startIndex = -1;
			int endIndex = -1;
			for (int i = 0; i < this.mStations.size(); i++) {
				switch (this.mStations.get(i).getState()) {
				case MetroStation.STATE_START:
					startIndex = i;
					break;
				case MetroStation.STATE_END:
					endIndex = i;
					break;
				}
			}
			// 如果已选择了起始站点和结束站点，则不操作直接返回
			if (startIndex >= 0 && endIndex >= 0)
				return;
			// 如果当前线路未设置起始站点和结束站点，则把当前站点设置成起始站点，然后把之前的设置成不可点击，把之后的设置成可点击
			else if (startIndex < 0 && endIndex < 0) {
				this.mStartStation = station;
				station.setState(MetroStation.STATE_START);
				for (int i = 0; i < this.mStations.size(); i++) {
					if (i < position)
						this.mStations.get(i).setState(MetroStation.STATE_CANT_SELECT);
					else if (i > position)
						this.mStations.get(i).setState(MetroStation.STATE_CAN_SELECT);
				}
				// 如果当前线路已设置起始站点，则把当前站点设置成结束站点，然后把之后的设置成不可点击
			} else if (startIndex >= 0) {
				this.mEndStation = station;
				station.setState(MetroStation.STATE_END);
				for (int i = 0; i < this.mStations.size(); i++) {
					if (i > position)
						this.mStations.get(i).setState(MetroStation.STATE_CANT_SELECT);
				}
				// 如果当前线路已设置结束站点，则把当前站点设置成起始站点，然后把之前的设置成不可点击
			} else if (endIndex >= 0) {
				this.mStartStation = station;
				station.setState(MetroStation.STATE_START);
				for (int i = 0; i < this.mStations.size(); i++) {
					if (i < position)
						this.mStations.get(i).setState(MetroStation.STATE_CANT_SELECT);
				}
			}
		}
		this.mAdapter.notifyDataSetChanged();
		this.setOkButtonState();
	}

	/**
	 * 设置确定按钮的状态
	 */
	private void setOkButtonState() {
		if (this.mStartStation == null || this.mEndStation == null) {
			this.mOkBtn.setEnabled(false);
			this.mOkBtn.setTextColor(this.getResources().getColor(R.color.app_grey_color));
		} else {
			this.mOkBtn.setEnabled(true);
			this.mOkBtn.setTextColor(this.getResources().getColor(R.color.app_main_text_color));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pointer:
			MetroSelectRouteActivity activity = (MetroSelectRouteActivity) this.getActivity();
			activity.setRouteFragment();
			break;
		case R.id.btn_clear:
			this.clear();
			break;
		case R.id.btn_ok:
			if (this.mStartStation != null && this.mEndStation != null) {
				this.saveStationSet();
			}
			break;
		case R.id.order_btn:
			this.setDirect();
			break;
		}
	}

	/**
	 * 清除当前的站点选择
	 */
	private void clear() {
		for (int i = 0; i < this.mStations.size(); i++) {
			this.mStations.get(i).setState(MetroStation.STATE_CAN_SELECT);
		}
		this.mAdapter.notifyDataSetChanged();
	}

	/**
	 * 保存站点设置
	 */
	private void saveStationSet() {
		this.mRoute.setStartStation(this.mStartStation);
		this.mRoute.setEndStation(this.mEndStation);
		this.mFactory.setCurrentRoute(this.getActivity(), this.mRoute, true);
		Intent data = new Intent();
		data.putExtra("result", this.mRoute.getName() + "," + this.mRoute.getRouteSelectDesc());
		this.getActivity().setResult(StartDialog.requestMetroRouteCode, data);
		this.getActivity().finish();
	}

	/**
	 * 设置路线方向
	 */
	private void setDirect() {
		this.mRoute.setForward(!this.mRoute.isForward());
		this.setStations();
		for (MetroStation station : this.mStations) {
			station.setState(MetroStation.STATE_CAN_SELECT);
		}
		this.mAdapter.notifyDataSetChanged();
	}

}
