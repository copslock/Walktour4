package com.walktour.gui.workorder.ah;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.walktour.control.bean.Verify;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog.Builder;
import com.walktour.gui.R;
import com.walktour.gui.applet.ControlBar;
import com.walktour.gui.workorder.ah.model.WorkOrder;
import com.walktour.gui.workorder.ah.model.WorkOrderPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * 安徽电信工单项目主界面->信息点（建筑物）列表
 * 
 * @author jianchao.wang
 * 
 */
public class WorkOrderPointActivity extends BasicActivity implements OnClickListener, OnCheckedChangeListener {
	/** 日志标识 */
	public final static String TAG = "WorkOrderPointActivity";
	/** 信息点（建筑物）列表 */
	private ListView workOrderPointList;
	/** 过滤的工单列表 */
	private List<WorkOrderPoint> filterList = new ArrayList<WorkOrderPoint>();
	/** 检索文本 */
	private EditText searchText;
	/** 所属工单 */
	private WorkOrder order;
	/** 工单列表适配类 */
	private PointArrayAdapter pointsAdapter;
	/** 上下文 */
	private WorkOrderPointActivity mContext;
	/** 选中的信息点 */
	private WorkOrderPoint selectPoint;
	/** 定位的信息点 */
	private WorkOrderPoint locationPoint;
	/** 底部左边按钮 */
	private Button buttonLeft;
	/** 底部右边按钮 */
	private Button buttonRight;
	/** 地图布局 */
	private FrameLayout mapFrame;
	/** 百度地图控件 */
	private BaiduMap mMap;
	/** 定位客户端 */
	private LocationClient mLocClient;
	/** 是否首次定位 */
	private boolean isFirstLoc = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SDKInitializer.initialize(this.getApplicationContext());
		super.onCreate(savedInstanceState);
		this.mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent intent = this.getIntent();
		String orderNo = intent.getStringExtra(WorkOrderMainActivity.EXTRA_ORDRE_NO);
		this.order = WorkOrderFactory.getInstance().getOrderByNo(orderNo);
		setContentView(R.layout.work_order_ah_point_list);
		findView();
		genToolBar();
		initMap();
		initValue();
		this.setLocation();
	}

	/**
	 * 生成底部工具栏
	 */
	private void genToolBar() {
		ControlBar bar = (ControlBar) findViewById(R.id.ControlBar);
		Button btnNew = bar.getButton(0);
		Button btnEdit = bar.getButton(1);
		btnNew.setText(R.string.work_order_add_point);
		btnEdit.setText(R.string.work_order_show_detail);
		// set icon
		btnNew.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_new), null,
				null);
		btnEdit.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_edit), null,
				null);
		btnNew.setOnClickListener(this);
		btnEdit.setOnClickListener(this);
	}

	/**
	 * 初始化地图界面
	 */
	private void initMap() {
		BaiduMapOptions option = new BaiduMapOptions();
		option.zoomControlsEnabled(false);
		MapView mapView = new MapView(this, option);
		this.mMap = mapView.getMap();
		this.mMap.setMaxAndMinZoomLevel(19, 3);
		this.mMap.setMapStatus(MapStatusUpdateFactory.zoomTo(16));
		this.mapFrame.addView(mapView);
	}

	/**
	 * 定位当前位置
	 */
	private void setLocation() {
		// 开启定位图层
		mMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this.getApplicationContext());
		mLocClient.registerLocationListener(new MyLocationListenner());
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		// 发起POI查询请求。请求过程是异步的，定位结果在上面的监听函数onReceivePoi中获取。
		if (mLocClient != null && mLocClient.isStarted())
			mLocClient.requestLocation();
	}

	/**
	 * 定位SDK监听函数
	 */
	private class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMap == null)
				return;
			// 此处设置开发者获取到的方向信息，顺时针0-360
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(100)
					.latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mMap.animateMapStatus(u);
				// 当不需要定位图层时关闭定位图层
				mMap.setMyLocationEnabled(false);
				mLocClient.stop();
			}
		}

	}

	/**
	 * 视图关联设置
	 */
	private void findView() {
		this.workOrderPointList = (ListView) this.findViewById(R.id.workOrderPointList);
		this.searchText = (EditText) this.findViewById(R.id.search_content_edit);
		((TextView) this.findViewById(R.id.order_no)).setText(this.order.getWorkItemCode());
		this.buttonLeft = (Button) this.findViewById(R.id.Button01);
		this.buttonLeft.setOnClickListener(this);
		this.buttonRight = (Button) this.findViewById(R.id.Button02);
		this.buttonRight.setOnClickListener(this);
		this.findViewById(R.id.pointer).setOnClickListener(this);
		this.mapFrame = (FrameLayout) this.findViewById(R.id.map);
		this.searchText.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				filterPoint();
			}

		});
	}

	/**
	 * 初始化数据
	 */
	private void initValue() {
		this.filterList.addAll(this.order.getPointList());
		this.pointsAdapter = new PointArrayAdapter(this.getApplicationContext(), R.layout.work_order_ah_point_row,
				this.filterList);
		this.workOrderPointList.setAdapter(pointsAdapter);
		this.filterPoint();
	}

	/**
	 * 过滤信息点
	 */
	private void filterPoint() {
		String keyword = this.searchText.getText().toString().trim();
		this.filterList.clear();
		if (keyword.length() > 0) {
			for (WorkOrderPoint point : this.order.getPointList()) {
				if (point.getName().indexOf(keyword) >= 0 || point.getAddress().indexOf(keyword) >= 0)
					this.filterList.add(point);
			}
		} else {
			this.filterList.addAll(this.order.getPointList());
		}
		this.pointsAdapter.notifyDataSetChanged();
		this.setMapMarker();
	}

	/**
	 * 设置地图标识
	 */
	private void setMapMarker() {
		this.mMap.clear();
		// 构建Marker图标
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
		BitmapDescriptor locationMap = BitmapDescriptorFactory.fromResource(R.drawable.iconmarker);
		for (WorkOrderPoint point : this.filterList) {
			double[] latlng = this.adjustLatLng(point.getLatitude(), point.getLongitude());
			// 构建MarkerOption，用于在地图上添加Marker
			OverlayOptions option = new MarkerOptions().position(new LatLng(latlng[0], latlng[1])).icon(
					point.equals(this.locationPoint) ? locationMap : bitmap);
			// 在地图上添加Marker，并显示
			this.mMap.addOverlay(option);
		}
	}

	/**
	 * 把GPS经纬度转换成百度坐标
	 * 
	 * @param latitude
	 *          纬度
	 * @param longitude
	 *          经度
	 * @return
	 */
	private double[] adjustLatLng(double latitude, double longitude) {
		CoordinateConverter conver = new CoordinateConverter();
		conver.coord(new LatLng(latitude, longitude));
		conver.from(CoordinateConverter.CoordType.GPS);
		LatLng ll = conver.convert();
		return new double[] { ll.latitude, ll.longitude };
	}

	/**
	 * 信息点（建筑物）列表适配类
	 * 
	 * @author jianchao.wang 2014年6月13日
	 */
	private class PointArrayAdapter extends ArrayAdapter<WorkOrderPoint> implements OnClickListener {
		/** 资源ID */
		private int resourceId;

		public PointArrayAdapter(Context context, int textViewResourceId, List<WorkOrderPoint> objectList) {
			super(context, textViewResourceId, objectList);
			this.resourceId = textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (convertView == null) {
				String inflater = Context.LAYOUT_INFLATER_SERVICE;
				LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
				view = vi.inflate(resourceId, null, true);
			}
			View layout = view.findViewById(R.id.text_show);
			layout.setTag(this.getItem(position));
			layout.setOnClickListener(this);
			String keyword = searchText.getText().toString().trim();
			String name = this.getItem(position).getName();
			SpannableStringBuilder noStyle = new SpannableStringBuilder(name);
			int start = name.indexOf(keyword);
			if (keyword.length() > 0 && start >= 0) {
				noStyle.setSpan(new ForegroundColorSpan(Color.RED), start, start + keyword.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			((TextView) view.findViewById(R.id.order_name)).setText(noStyle);
			String addr = this.getItem(position).getAddress();
			SpannableStringBuilder nameStyle = new SpannableStringBuilder(addr);
			start = addr.indexOf(keyword);
			if (keyword.length() > 0 && start >= 0) {
				nameStyle.setSpan(new ForegroundColorSpan(Color.RED), start, start + keyword.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			((TextView) view.findViewById(R.id.order_address)).setText(nameStyle);
			ImageButton location = (ImageButton) view.findViewById(R.id.location_btn);
			location.setTag(this.getItem(position));
			location.setOnClickListener(mContext);
			location.setVisibility(View.VISIBLE);
			return view;
		}

		@Override
		public void onClick(View v) {
			showFloor((WorkOrderPoint) v.getTag());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Button01:
			this.showCreatePointDialog();
			break;
		case R.id.Button02:
			this.showDetail();
			break;
		case R.id.location_btn:
			locationPoint((WorkOrderPoint) v.getTag());
			break;
		case R.id.pointer:
			this.finish();
			break;
		}
	}

	/**
	 * 显示创建信息点的对话框
	 */
	@SuppressLint("InflateParams")
	private void showCreatePointDialog() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View view = factory.inflate(R.layout.alert_dialog_edittext, null);
		EditText alert_EditText = (EditText) view.findViewById(R.id.alert_textEditText);
		alert_EditText.setSelectAllOnFocus(true);
		Builder builder = new Builder(this);
		builder.setIcon(android.R.drawable.ic_menu_edit).setTitle(R.string.work_order_add_point)
				.setView(view).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						EditText alert_EditText = (EditText) view.findViewById(R.id.alert_textEditText);
						String name = alert_EditText.getText().toString().trim();
						if (name.length() > 0 && Verify.checkChar(name)) {
							createPoint(name);
						} else {
							Toast.makeText(mContext, getString(R.string.monitor_inputPosition), Toast.LENGTH_LONG).show();
						}
					}
				}).setNegativeButton(R.string.str_cancle);
		builder.show();
	}

	/**
	 * 创建新的信息点
	 * 
	 * @param name
	 *          信息点名称
	 */
	private void createPoint(String name) {
		WorkOrderPoint point = new WorkOrderPoint();
		point.setPointID(String.valueOf(System.currentTimeMillis()));
		point.setName(name);
		point.setCreate(true);
		point.setOrder(order);
		order.getPointList().add(point);
		WorkOrderFactory.getInstance().createPoint(point);
		this.filterPoint();
	}

	/**
	 * 定位信息点位置
	 * 
	 * @param point
	 *          信息点
	 */
	private void locationPoint(WorkOrderPoint point) {
		this.mapFrame.setVisibility(View.VISIBLE);
		this.locationPoint = point;
		this.setMapMarker();
		double[] latlng = this.adjustLatLng(point.getLatitude(), point.getLongitude());
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(new LatLng(latlng[0], latlng[1]));
		mMap.animateMapStatus(u);
	}

	/**
	 * 显示工单明细
	 */
	private void showDetail() {
		Intent intent = new Intent(this, WorkOrderDetailActivity.class);
		intent.putExtra(WorkOrderMainActivity.EXTRA_ORDRE_NO, this.order.getWorkItemCode());
		this.startActivity(intent);
	}

	/**
	 * 显示楼层信息
	 * 
	 * @param point
	 *          信息点
	 */
	private void showFloor(WorkOrderPoint point) {
		Intent intent = new Intent(this, WorkOrderFloorActivity.class);
		intent.putExtra(WorkOrderMainActivity.EXTRA_ORDRE_NO, this.order.getWorkItemCode());
		intent.putExtra(WorkOrderMainActivity.EXTRA_POINT_NO, point.getPointID());
		this.startActivity(intent);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			this.selectPoint = (WorkOrderPoint) buttonView.getTag();
		} else if (buttonView.getTag().equals(this.selectPoint)) {
			this.selectPoint = null;
		}
		this.pointsAdapter.notifyDataSetChanged();
	}

}
