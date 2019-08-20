package com.walktour.gui.newmap.layer.baidu;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.newmap.BaseMapActivity;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.newmap.layer.BaseMapLayer;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap.overlay.BaseMapOverlay;
import com.walktour.service.metro.MetroFactory;
import com.walktour.service.metro.model.MetroStation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 百度地图图层
 * 
 * @author jianchao.wang
 * 
 */
public class BaiduMapLayer extends BaseMapLayer implements BaiduMap.OnMapStatusChangeListener, OnMapClickListener,
		OnMapLongClickListener, OnMapLoadedCallback, OnMarkerClickListener {
	/** 百度地图视图层 */
	private MapView mMapView;
	/** 百度地图 */
	private BaiduMap mMap;
	/** 定位客户端 */
	private LocationClient mLocClient;
	/** 是否首次定位 */
	private boolean isFirstLoc = true;
	/** 上次的缩放等级 */
	private float lastZoom = 0;
	/** 上次的经纬度坐标 */
	private MyLatLng lastLatlng = null;
	/** 应用对象 */
	private ApplicationModel appModel = ApplicationModel.getInstance();
	/** 是否有做暂停操作 */
	private boolean isPause = false;

	public BaiduMapLayer(BaseMapActivity activity) {
		super(activity, "BaiduMapLayer");
		this.isShow = true;
		this.isFirstLoc = true;
	}

	@Override
	public void init() {
		LogUtil.d(TAG, "-------init----------");
		BaiduMapOptions option = new BaiduMapOptions();
//		option.zoomControlsEnabled(false);
		option.overlookingGesturesEnabled(false);
		option.rotateGesturesEnabled(false);
		this.mMapView = new MapView(this.mActivity, option);
		this.mMap = this.mMapView.getMap();
		this.mMap.setMapType(NewMapFactory.getInstance().getMapType());
		this.mMap.setOnMapStatusChangeListener(this);
		this.mMap.setOnMapClickListener(this);
		this.mMap.setOnMapLongClickListener(this);
		this.mMap.setOnMapLoadedCallback(this);
		this.mMap.setOnMarkerClickListener(this);
		this.mMap.setMaxAndMinZoomLevel(NewMapFactory.getInstance().getZoomLevelMax(),
				NewMapFactory.getInstance().getZoomLevelMin());
		this.mMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(NewMapFactory.getInstance().getZoomLevelNow()));
	}

	@Override
	protected void setLocation() {
		SceneType scene = ApplicationModel.getInstance().getSelectScene();
		if (scene != null && scene == SceneType.Metro) {
			return;
		}
		this.isLocation = true;
		LogUtil.d(TAG, "--------setLocation-------------");
		// 定位初始化
		mLocClient = new LocationClient(this.mActivity.getApplicationContext());
		mLocClient.registerLocationListener(new MyLocationListenner());
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(LocationClientOption.MIN_SCAN_SPAN);
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
			// map view 销毁后不再处理新接收的位置
			if (location == null || mMapView == null || !isShow)
				return;
			MyLatLng latlng = new MyLatLng(location.getLatitude(), location.getLongitude());
			LogUtil.d(TAG, "--------onReceiveLocation-----locType:" + location.getLocType() + "-----lat:" + latlng.latitude
					+ "----lng:" + latlng.longitude);
			if (latlng.latitude == 0 && latlng.longitude == 0) {
				return;
			}
			// 此处设置开发者获取到的方向信息，顺时针0-360
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(100)
					.latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mMap.setMyLocationData(locData);
			if (isFirstLoc) {
				mLocClient.stop();
				isLocation = false;
				setCenter(latlng, NewMapFactory.getInstance().getZoomLevelNow());
				saveLocusImage();
				isFirstLoc = false;
			}
		}
	}

	/**
	 * 测试结束时保存地图轨迹图片
	 */
	private void saveLocusImage() {
		if (!TraceInfoInterface.isSaveFileLocus)
			return;
		this.setZoomLevelNow(10);
		new Thread() {
			public void run() {
				try {
					Thread.sleep(1 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!StringUtil.isNullOrEmpty(TraceInfoInterface.saveFileLocusPath)) {
					File file = new File(TraceInfoInterface.saveFileLocusPath);
					String picName = file.getName();
					picName = picName.substring(0, picName.lastIndexOf(".") + 1) + "locus";
					LogUtil.d(TAG, "saveFileLocusPath" + file.getParent() + File.separator + picName);
					mActivity.saveViewToBMP(file.getParent(), picName);
					TraceInfoInterface.saveFileLocusPath = null;
				}
			}
		}.start();
	}

	/**
	 * 设置俯视角度,用于呈现3D效果
	 * 
	 */
	private void setOverlook() {
		int overlook = 0;
		if (NewMapFactory.getInstance().getMapType() == NewMapFactory.MAP_TYPE_NORMAL_3D)
			overlook = -30;
		if (mMap == null || mMap.getMapStatus() == null)
			return;
		LogUtil.d(TAG, "----setOverlook---- overlook:" + overlook);
		MapStatus.Builder builder;
		if (mMap.getMapStatus() != null)
			builder = new MapStatus.Builder(mMap.getMapStatus());
		else
			builder = new MapStatus.Builder();
		MapStatus mapStatus = builder.overlook(overlook).build();
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
		mMap.animateMapStatus(mapStatusUpdate);
	}

	@Override
	public void setCenter(MyLatLng latlng, float zoomLevelNow) {
		if (latlng == null || this.isLocation)
			return;
		LogUtil.d(TAG, "----setCenter---- latitude=" + latlng.latitude + ",longitude=" + latlng.longitude + ",isFirstLoc="
				+ this.isFirstLoc);
		NewMapFactory.getInstance().setNowLatLng(latlng);
		this.mMap.animateMapStatus(
				MapStatusUpdateFactory.newLatLngZoom(new LatLng(latlng.latitude, latlng.longitude), zoomLevelNow));
		for (BaseMapOverlay overlay : this.overlayList) {
			overlay.setCenter();
		}
		if (this.isFirstLoc) {
			new Thread() {
				public void run() {
					try {
						Thread.sleep(1 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					setOverlook();
				}
			}.start();
		}
	}

	@Override
	public void setZoomLevelNow(float zoomLevelNow) {
		if (zoomLevelNow > NewMapFactory.getInstance().getZoomLevelMax()
				|| zoomLevelNow < NewMapFactory.getInstance().getZoomLevelMin())
			return;
		NewMapFactory.getInstance().setZoomLevelNow(zoomLevelNow);
		this.mMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(zoomLevelNow));
	}

	@Override
	public void onPause() {
		LogUtil.d(TAG, "-----onPause-----");
		this.isShow = false;
		this.isPause = true;
		this.mMapView.onPause();
	}

	@Override
	public void onResume() {
		LogUtil.d(TAG, "-----onResume-----");
		this.isShow = true;
		this.mMapView.onResume();
		if (this.isPause) {
			for (BaseMapOverlay overlay : this.overlayList) {
//				overlay.onResume();
				overlay.setCenter();
			}
			this.isPause = false;
		}
	}

	@Override
	public void onDestroy() {
		LogUtil.d(TAG, "-----onDestroy-----");
		this.isShow = false;
		this.mMapView.onDestroy();
		for (BaseMapOverlay overlay : super.overlayList) {
			overlay.onDestroy();
		}
	}

	@Override
	public Point convertLatlngToPoint(double latitude, double longitude) {
		if (this.mMap.getProjection() == null)
			return new Point(-1, -1);
		return this.mMap.getProjection().toScreenLocation(new LatLng(latitude, longitude));
	}

	@Override
	public View getMap() {
		return this.mMapView;
	}

	@Override
	public BaiduMap getMapControl() {
		return this.mMap;
	}

	@Override
	public MyLatLng getMapCenter() {
		if (this.mMap.getProjection() == null)
			return null;
		int width = this.mMapView.getMeasuredWidth();
		int height = this.mMapView.getMeasuredHeight();
		return this.convertPointToLatlng(new Point(width / 2, height / 2));
	}

	/**
	 * 获取地图截图
	 * 
	 * @param callback
	 *          回调类
	 */
	public void snapshot(SnapshotReadyCallback callback) {
		this.mMap.snapshot(callback);
	}

	@Override
	public MyLatLng convertPointToLatlng(Point point) {
		LatLng latlng = this.mMap.getProjection().fromScreenLocation(point);
		return new MyLatLng(latlng.latitude, latlng.longitude);
	}

//	@Override
//	public void onMapDrawFrame(GL10 arg0, MapStatus arg1) {
//		float zoom = arg1.zoom;
//		Log.d(TAG,"onMapDrawFrame:");
//		Log.d(TAG,"zoom:"+zoom+"   lastZoom:"+lastZoom);
//		Log.d(TAG,"latlng:"+arg1.target.latitude+","+arg1.target.longitude);
//
//	}
	@Override
	public void onMapStatusChangeStart(MapStatus mapStatus) {

	}

	@Override
	public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

	}

	@Override
	public void onMapStatusChange(MapStatus mapStatus) {

	}

	@Override
	public void onMapStatusChangeFinish(MapStatus mapStatus) {
		float zoom = mapStatus.zoom;
		MyLatLng latlng = new MyLatLng(mapStatus.target.latitude, mapStatus.target.longitude);
		if (appModel.isTestJobIsRun() || zoom != this.lastZoom || !latlng.equals(this.lastLatlng)) {
			if (this.lastZoom !=zoom) {
				this.zoomOverlays();
				this.lastZoom = zoom;
			}
			this.lastLatlng = latlng;
			NewMapFactory.getInstance().setZoomLevelNow(zoom);
			NewMapFactory.getInstance().setNowLatLng(latlng);
			this.mActivity.handleMapTitle();
		}
	}
	@Override
	public void onMapLongClick(LatLng arg0) {
		Point click = convertLatlngToPoint(arg0.latitude, arg0.longitude);
		LogUtil.d(TAG, "--------onMapLongClick------x:" + click.x + "---y:" + click.y + "---");
		longClickOverlays(click);
	}

	@Override
	public void onMapClick(LatLng arg0) {
		Point click = convertLatlngToPoint(arg0.latitude, arg0.longitude);
		LogUtil.d(TAG, "--------onMapClick------x:" + click.x + "---y:" + click.y + "---");
		clickOverlays(click);
	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		// 无需实现
		return false;
	}

	@Override
	public void onMapLoaded() {
		LogUtil.d(TAG, "---------onMapLoaded-----------");
		SceneType scene = ApplicationModel.getInstance().getSelectScene();
		if (scene != null && scene == SceneType.Metro) {
			MetroFactory factory = MetroFactory.getInstance(mActivity);
			MetroStation station = factory.getCurrentStation();
			if (station != null) {
				if (station.getBaiduLatLng() == null)
					station.setBaiduLatLng(this.adjustFromGPS(station.getLatLng().latitude, station.getLatLng().longitude));
				this.setCenter(station.getBaiduLatLng(), 13);
			}
		} else
			this.setCenter(this.getMapCenter(), NewMapFactory.getInstance().getZoomLevelNow());
	}

	@Override
	public MyLatLng adjustFromGPS(double latitude, double longitude) {
		CoordinateConverter conver = new CoordinateConverter();
		conver.coord(new LatLng(latitude, longitude));
		conver.from(CoordinateConverter.CoordType.GPS);
		LatLng ll = conver.convert();
		return new MyLatLng(ll.latitude, ll.longitude);
	}

	@Override
	public void changeMapType() {
		NewMapFactory.getInstance().changeMapType();
		int mapType = NewMapFactory.getInstance().getMapType();
		switch (mapType) {
		case NewMapFactory.MAP_TYPE_NORMAL_2D:
			this.mMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			this.setOverlook();
			break;
		case NewMapFactory.MAP_TYPE_NORMAL_3D:
			this.mMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			this.setOverlook();
			break;
		case NewMapFactory.MAP_TYPE_SATELLITE:
			this.mMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			this.setOverlook();
			break;
		case NewMapFactory.MAP_TYPE_NONE:
			this.mMap.setMapType(BaiduMap.MAP_TYPE_NONE);
			this.setOverlook();
			break;
		}
	}

	@Override
	public Object drawLine(List<MyLatLng> points, int lineWidth, int lineColor) {
		PolylineOptions options = new PolylineOptions();
		List<LatLng> list = new ArrayList<LatLng>();
		for (MyLatLng latlng : points) {
			list.add(new LatLng(latlng.latitude, latlng.longitude));
		}
		options.points(list);
		options.width(lineWidth);
		options.color(lineColor);
		return this.mMap.addOverlay(options);
	}

	@Override
	public Object drawDot(MyLatLng point, int dotRadius, int dotColor) {
		DotOptions options = new DotOptions();
		options.center(new LatLng(point.latitude, point.longitude));
		options.radius(dotRadius);
		options.color(dotColor);
		return this.mMap.addOverlay(options);
	}

	@Override
	public Object drawText(MyLatLng latlng, String text, int textSize, int textColor) {
		TextOptions options = new TextOptions();
		options.position(new LatLng(latlng.latitude, latlng.longitude));
		options.text(text);
		options.align(TextOptions.ALIGN_RIGHT, TextOptions.ALIGN_CENTER_VERTICAL);
		options.fontSize(textSize);
		options.fontColor(textColor);
		return this.mMap.addOverlay(options);
	}

	@Override
	public void clearAllDraw() {
		this.mMap.clear();
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		clickOverlaysMarker(arg0.getExtraInfo());
		return false;
	}

	@Override
	public Object drawBitmapMarker(MyLatLng latlng, Bitmap icon, float anchorX, float anchorY, Bundle bundle) {
		MarkerOptions options = new MarkerOptions();
		ArrayList<BitmapDescriptor> list = new ArrayList<BitmapDescriptor>();
		list.add(BitmapDescriptorFactory.fromBitmap(icon));
		options.icons(list);
		options.position(new LatLng(latlng.latitude, latlng.longitude));
		options.anchor(anchorX, anchorY);
		options.period(10);
		if (bundle != null)
			options.extraInfo(bundle);
		return this.mMap.addOverlay(options);
	}

	@Override
	public Object drawResourceMarker(MyLatLng latlng, List<Integer> rsIds, float anchorX, float anchorY) {
		MarkerOptions options = new MarkerOptions();
		ArrayList<BitmapDescriptor> list = new ArrayList<BitmapDescriptor>();
		for (int rsId : rsIds) {
			list.add(BitmapDescriptorFactory.fromResource(rsId));
		}
		options.icons(list);
		options.position(new LatLng(latlng.latitude, latlng.longitude));
		options.anchor(anchorX, anchorY);
		return this.mMap.addOverlay(options);
	}

}
