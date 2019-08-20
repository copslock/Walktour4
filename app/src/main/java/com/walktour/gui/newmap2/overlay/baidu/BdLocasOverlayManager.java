package com.walktour.gui.newmap2.overlay.baidu;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.dinglicom.dataset.DatasetManager;
import com.walktour.Utils.DensityUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ParameterSetting;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.gui.R;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.map.PointStatus;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap2.overlay.BaseOverlayManager;
import com.walktour.gui.newmap2.overlay.OverlayType;
import com.walktour.gui.newmap2.ui.GpsLocasView;
import com.walktour.gui.newmap2.util.BaiduMapUtil;
import com.walktour.model.LocusParamInfo;
import com.walktour.model.MapEvent;
import com.walktour.model.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * gps 轨迹点
 *
 * @author zhicheng.chen
 * @date 2018/6/11
 */
public class BdLocasOverlayManager extends BaseOverlayManager implements RefreshEventManager.RefreshEventListener {

	private static final String TAG = BdLocasOverlayManager.class.getSimpleName();

	private static final int Z_INDEX = 9;
	private final String EXTRA_MAPEVENT_INFO = "EXTRA_MAPEVENT_INFO";
	private final String EXTRA_LOCUSPARAM_INFO = "EXTRA_LOCUSPARAM_INFO";
	private final String EXTRA_MARKER_TYPE = "EXTRA_MARKER_TYPE"; //marker 类型
	private final String EXTRA_IS_MARKER_CURRENT_POSITION = "EXTRA_IS_MARKER_CURRENT_POSITION"; // 是否是当前位置的 marker

	private DatasetManager mDatasetMgr;//数据集管理对象
	private ParameterSetting mParameterSet;//参数设置
	private List<Parameter> parameterms;//参数列表
	private long mLastCheckParamsTime = 0;//最后一次检测网络参数时间

	private View mPopLayout;
	private TextView mtitle;
	private TextView mDescr;
	private TextView mCoord;

	private List<Overlay> mOverlayList = new ArrayList<>();
	private ArrayList<MapEvent> locasList;//记录的是轨迹点的MapEvent
	private List<Overlay> mInDoorOverlayList = new ArrayList<>();
	private ArrayList<MapEvent> inDoorList;//记录的是室内打点点的MapEvent
	private Overlay mCurrentMarker;

	public BdLocasOverlayManager(Context context) {
		super(context);
		mDatasetMgr = DatasetManager.getInstance(mContext);
		mParameterSet = ParameterSetting.getInstance();
		mParameterSet.initMapLocusShape(mContext);
		locasList = new ArrayList<>();
		inDoorList = new ArrayList<>();
		RefreshEventManager.addRefreshListener(this);
	}

	@Override
	public OverlayType getOverlayType() {
		return OverlayType.LocasPoint;
	}

	@Override
	public boolean onMapClick(Object... obj) {
		if (mPopLayout != null) {
			MapView mapView = (MapView) mMapSdk.getMapView();
			mapView.removeView(mPopLayout);
		}
		return super.onMapClick(obj);
	}

	@Override
	public void onMapLoaded() {
//		if (DatasetManager.isPlayback) {
			addGpsOverlay(null);
//		}
	}

	@Override
	public synchronized boolean addOverlay(Object... obj) {
		if (obj.length==0||obj==null){
			addGpsOverlay(null);
			return false;
		}
		addGpsOverlay((MyLatLng) obj[0]);
		return true;
	}

	private void addGpsOverlay(MyLatLng ll) {
//        clearOverlay();
//        mOverlayList.clear();
		checkParamerChange();
//        getOverlayCoord();
		List<OverlayOptions> overlayOptions = new ArrayList<>();
		List<OverlayOptions> inDooroverlayOptions = new ArrayList<>();
//		addCurrentLocationOverlay(ll);
		addGpsLineOverlay(overlayOptions);
		addInDoorLineOverlay(inDooroverlayOptions);
		if (getMapControllor() != null) {
			mOverlayList.addAll(getMapControllor().addOverlays(overlayOptions));
			mInDoorOverlayList.addAll(getMapControllor().addOverlays(inDooroverlayOptions));
		}
	}

	/**
	 * 添加gps轨迹的点
	 *
	 * @param overlayOptions
	 */
	private void addGpsLineOverlay(List<OverlayOptions> overlayOptions) {
		/**
		 * 2018/12/20
		 * 因为跟随模式按钮改动。没有导航模式，去掉导航这个选项，只能通过导航按钮去到导航模式
		 * @anthor jinfeng.xie
		 *
		 */
//		boolean isNavigation = mPreferences.getInt(NewMapActivity.AUTO_FOLLOW_MODE, 0) == 2;
		BaseStation selectBaseStation = factory.getSelectBaseStation();
		GpsLocasView view = new GpsLocasView(mContext);
		List<MapEvent> gosLocas = TraceInfoInterface.traceData.getGpsLocas();
		LogUtil.e(TAG, "gps點的长度:" + gosLocas.size());
		LogUtil.e(TAG, "locasList的长度:" + locasList.size());
		if (gosLocas.size() <= locasList.size()) {
			return;
		}

		int start = locasList.size();
		for (int i = start; i < gosLocas.size(); i++) {
			locasList.add(gosLocas.get(i));
			factory.getLocasList().add(gosLocas.get(i));
			if (gosLocas.size()>0&&gosLocas.get(gosLocas.size()-1)!=null){
				addCurrentLocationOverlay(gosLocas.get(gosLocas.size()-1).getLatLng());
			}
			MapEvent mapEvent = gosLocas.get(i);
			mapEvent = getOverlayCoord(mapEvent);
			Bundle bundle = new Bundle();
			bundle.putSerializable(EXTRA_MAPEVENT_INFO, mapEvent);
			bundle.putInt(EXTRA_IS_MARKER_CURRENT_POSITION, 0);
			bundle.putInt(EXTRA_MARKER_TYPE, getOverlayType().getId());
			// 如果是导航模式,或者当前选择了基站且当前点和基站没有关联
			if ((selectBaseStation != null && !this.isConnectStation(mapEvent)) ) {
				view.setRadius(getLocusRadius());
				view.setColor(Color.GRAY);
				view.setType(mParameterSet.getLocusShape() == 0 ? GpsLocasView.CIRCLE : GpsLocasView.RECT);
				BitmapDescriptor icon = BitmapDescriptorFactory.fromView(view);
				OverlayOptions option = new MarkerOptions()
						.position(new LatLng(mapEvent.getAdjustLatitude(), mapEvent.getAdjustLongitude()))
						.icon(icon)
						.zIndex(Z_INDEX)
						.extraInfo(bundle);
				overlayOptions.add(option);
			} else {
				if (parameterms == null || parameterms.isEmpty()) {
					view.setRadius(getLocusRadius());
					view.setColor(Color.GRAY);
					view.setType(mParameterSet.getLocusShape() == 0 ? GpsLocasView.CIRCLE : GpsLocasView.RECT);
					BitmapDescriptor icon = BitmapDescriptorFactory.fromView(view);
					OverlayOptions option = new MarkerOptions()
							.position(new LatLng(mapEvent.getAdjustLatitude(), mapEvent.getAdjustLongitude()))
							.icon(icon)
							.zIndex(Z_INDEX)
							.extraInfo(bundle);
					overlayOptions.add(option);
				} else {
//					for (int k = 0; k < parameterms.size(); k++) {
					if(parameterms.size()==0){
						continue;
					}
					int k=0;
						LocusParamInfo info = mapEvent.getParamInfoMap().get(parameterms.get(k).getShowName());
						bundle.putSerializable(EXTRA_LOCUSPARAM_INFO, info);
						int color = (info == null ? Color.GRAY : info.color);
						view.setRadius(getLocusRadius());
						view.setColor(color);
						view.setType(mParameterSet.getLocusShape() == 0 ? GpsLocasView.CIRCLE : GpsLocasView.RECT);
						BitmapDescriptor icon = BitmapDescriptorFactory.fromView(view);
						LatLng position = new LatLng(mapEvent.getAdjustLatitude(), mapEvent.getAdjustLongitude() + k * (float) getLocusRadius() * 0.0000005);
						OverlayOptions option = new MarkerOptions()
								.position(position)
								.icon(icon)
								.zIndex(Z_INDEX)
								.extraInfo(bundle);
						overlayOptions.add(option);
//					}
				}
			}
		}
	}
	/**
	 * 添加室内轨迹的点
	 *
	 * @param overlayOptions
	 */
	private void addInDoorLineOverlay(List<OverlayOptions> overlayOptions) {
		/**
		 * 2018/12/20
		 * 因为跟随模式按钮改动。没有导航模式，去掉导航这个选项，只能通过导航按钮去到导航模式
		 * @anthor jinfeng.xie
		 *
		 */
//		boolean isNavigation = mPreferences.getInt(NewMapActivity.AUTO_FOLLOW_MODE, 0) == 2;
		BaseStation selectBaseStation = factory.getSelectBaseStation();
		GpsLocasView view = new GpsLocasView(mContext);
		List<PointStatus> pointList = new ArrayList<>(MapFactory.getMapData().getPointStatusStack());
		LogUtil.e(TAG, "inDoor點的长度:" + pointList.size());
		LogUtil.e(TAG, "inDoorList的长度:" + inDoorList.size());
		if (pointList.size() <= inDoorList.size()) {
			return;
		}

		int start = inDoorList.size();
		for (int i = start; i < pointList.size(); i++) {
			MapEvent mapEvent = new MapEvent();
			if (pointList.get(i).getLatLng()!=null){
				mapEvent.setLatitude(pointList.get(i).getLatLng().latitude);
				mapEvent.setLongitude(pointList.get(i).getLatLng().longitude);
//				mapEvent.setAdjustLatitude(pointList.get(i).getLatLng().latitude);
//				mapEvent.setAdjustLongitude(pointList.get(i).getLatLng().longitude);
			}
			inDoorList.add(mapEvent);
			factory.getLocasList().add(mapEvent);
			mapEvent = getOverlayCoord(mapEvent);
			Bundle bundle = new Bundle();
			bundle.putSerializable(EXTRA_MAPEVENT_INFO, mapEvent);
			bundle.putInt(EXTRA_IS_MARKER_CURRENT_POSITION, 0);
			bundle.putInt(EXTRA_MARKER_TYPE, getOverlayType().getId());
			// 如果是导航模式,或者当前选择了基站且当前点和基站没有关联
			if ((selectBaseStation != null && !this.isConnectStation(mapEvent)) ) {
				view.setRadius(getLocusRadius());
				view.setColor(Color.GRAY);
				view.setType(mParameterSet.getLocusShape() == 0 ? GpsLocasView.CIRCLE : GpsLocasView.RECT);
				BitmapDescriptor icon = BitmapDescriptorFactory.fromView(view);
				OverlayOptions option = new MarkerOptions()
						.position(new LatLng(mapEvent.getAdjustLatitude(), mapEvent.getAdjustLongitude()))
						.icon(icon)
						.zIndex(Z_INDEX)
						.extraInfo(bundle);
				overlayOptions.add(option);
			} else {
				if (parameterms == null || parameterms.isEmpty()) {
					view.setRadius(getLocusRadius());
					view.setColor(Color.GRAY);
					view.setType(mParameterSet.getLocusShape() == 0 ? GpsLocasView.CIRCLE : GpsLocasView.RECT);
					BitmapDescriptor icon = BitmapDescriptorFactory.fromView(view);
					OverlayOptions option = new MarkerOptions()
							.position(new LatLng(mapEvent.getAdjustLatitude(), mapEvent.getAdjustLongitude()))
							.icon(icon)
							.zIndex(Z_INDEX)
							.extraInfo(bundle);
					overlayOptions.add(option);
				} else {
//					for (int k = 0; k < parameterms.size(); k++) {
                    if(parameterms.size()==0){
                        continue;
                    }
                    int k=0;
						LocusParamInfo info = mapEvent.getParamInfoMap().get(parameterms.get(k).getShowName());
						bundle.putSerializable(EXTRA_LOCUSPARAM_INFO, info);
						int color = (info == null ? Color.GRAY : info.color);
						view.setRadius(getLocusRadius());
						view.setColor(color);
						view.setType(mParameterSet.getLocusShape() == 0 ? GpsLocasView.CIRCLE : GpsLocasView.RECT);
						BitmapDescriptor icon = BitmapDescriptorFactory.fromView(view);
						LatLng position = new LatLng(mapEvent.getAdjustLatitude(), mapEvent.getAdjustLongitude() + k * (float) getLocusRadius() * 0.0000005);
						OverlayOptions option = new MarkerOptions()
								.position(position)
								.icon(icon)
								.zIndex(Z_INDEX)
								.extraInfo(bundle);
						overlayOptions.add(option);
//					}
				}
			}
		}
	}

	/**
	 * 显示当前位置的marker
	 *
	 * @param ll
	 */
	private void addCurrentLocationOverlay(MyLatLng ll) {
        if (mCurrentMarker != null&&!DatasetManager.isPlayback) {
            //移除当前位置的marker
            mCurrentMarker.remove();
        }
		Bundle bundle = new Bundle();
		bundle.putInt(EXTRA_MARKER_TYPE, getOverlayType().getId());
		bundle.putInt(EXTRA_IS_MARKER_CURRENT_POSITION, 1);
		BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.iconmarker2);
		if (DatasetManager.isPlayback) {
            final int currentIndex = mDatasetMgr.currentIndex;
            for (int i = locasList.size()-1; i>=0; i--) {
				MapEvent mapEvent = locasList.get(i);
				mapEvent = getOverlayCoord(mapEvent);
				if ((currentIndex >= mapEvent.getBeginPointIndex()
						&& currentIndex <= mapEvent.getEndPointIndex())
					|| currentIndex >= mapEvent.getEndPointIndex()) {
					LatLng bdLL = new LatLng(mapEvent.getAdjustLatitude(), mapEvent.getAdjustLongitude());
					if (mCurrentMarker == null) {
						OverlayOptions option = new MarkerOptions()
								.position(bdLL)
								.icon(icon)
								.zIndex(Z_INDEX * 2)
								.extraInfo(bundle);
						mCurrentMarker = getMapControllor().addOverlay(option);
					} else {
						((Marker) mCurrentMarker).setPosition(bdLL);
					}
					break;
				}
			}
		} else {
			if (ll != null) {
				LatLng bdLL = BaiduMapUtil.convert(ll.latitude, ll.longitude);
				OverlayOptions option = new MarkerOptions()
						.position(bdLL)
						.icon(icon)
						.zIndex(Z_INDEX * 2)
						.extraInfo(bundle);
				mCurrentMarker = getMapControllor().addOverlay(option);
			}
		}
	}


	@Override
	public boolean onMarkerClick(Object... obj) {
		Marker marker = (Marker) obj[0];
		Bundle bundle = marker.getExtraInfo();
		if (bundle != null) {
			if (bundle.getInt(EXTRA_MARKER_TYPE) == getOverlayType().getId()
					&& bundle.getInt(EXTRA_IS_MARKER_CURRENT_POSITION) == 0) {
				showInfoPop(marker, false);
			} else if (bundle.getInt(EXTRA_MARKER_TYPE) == getOverlayType().getId()) {
				showInfoPop(marker, true);
			}
		}
		return true;
	}

	private void showInfoPop(Marker marker, boolean isCurrentPos) {
		if (mPopLayout == null) {
			mPopLayout = LayoutInflater.from(mContext).inflate(R.layout.poi_descr, null);
			mtitle = (TextView) mPopLayout.findViewById(R.id.poi_title);
			mDescr = (TextView) mPopLayout.findViewById(R.id.descr);
			mCoord = (TextView) mPopLayout.findViewById(R.id.coord);
		}
		Bundle bundle = marker.getExtraInfo();
		if (bundle != null) {
			MapEvent event = (MapEvent) bundle.getSerializable(EXTRA_MAPEVENT_INFO);
			if (event != null) {
				mDescr.setText(event.getMapPopInfo());
				if (DatasetManager.isPlayback) {
					mDatasetMgr.getPlaybackManager().setSkipIndex(event.getBeginPointIndex());
					addCurrentLocationOverlay(null);
				}
			}
			LocusParamInfo locusParamInfo = (LocusParamInfo) bundle.getSerializable(EXTRA_LOCUSPARAM_INFO);
			mtitle.setText(R.string.position);
			if (locusParamInfo == null || locusParamInfo.paramName == null || locusParamInfo.value == -9999) {
				mCoord.setVisibility(View.GONE);
			} else {
				mCoord.setVisibility(View.VISIBLE);
				mCoord.setText(locusParamInfo.paramName + ":" + locusParamInfo.value);
			}

			if (isCurrentPos) {
				mCoord.setVisibility(View.INVISIBLE);
				mDescr.setText(marker.getPosition().latitude + "," + marker.getPosition().longitude);
			} else {
				mCoord.setVisibility(View.VISIBLE);
			}
			MapView mapView = (MapView) mMapSdk.getMapView();
			mapView.removeView(mPopLayout);
			ViewGroup.LayoutParams params = new MapViewLayoutParams.Builder()
					.layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)// 按照经纬度设置位置
					.position(marker.getPosition())
					.width(MapViewLayoutParams.WRAP_CONTENT)
					.height(MapViewLayoutParams.WRAP_CONTENT)
					.yOffset(-25)
					.build();
			mapView.addView(mPopLayout, params);
		}
	}

	@Override
	public synchronized boolean clearOverlay() {
		if (!mOverlayList.isEmpty()) {
			for (Overlay overlay : mOverlayList) {
				overlay.remove();
			}
			mOverlayList.clear();
		}
		if (!mInDoorOverlayList.isEmpty()) {
			for (Overlay overlay : mInDoorOverlayList) {
				overlay.remove();
			}
			mInDoorOverlayList.clear();
		}
		if (mCurrentMarker!=null){
			mCurrentMarker.remove();
		}
		locasList.clear();
		inDoorList.clear();
		return false;
	}

	private BaiduMap getMapControllor() {
		return (BaiduMap) mMapSdk.getMapControllor();
	}

	private int getLocusRadius() {
		float radius = DensityUtil.dip2px(mContext, 10);
		switch (mParameterSet.getLocusSize()) {
			case 0:
				radius = DensityUtil.dip2px(mContext, 12);
				break;
			case 1:
				radius = DensityUtil.dip2px(mContext, 10);
				break;
			case 2:
				radius = DensityUtil.dip2px(mContext, 8);
				break;
			default:
				break;
		}
		return (int) radius;
	}

	@Override
	public void onDestory() {
		super.onDestory();
		RefreshEventManager.removeRefreshListener(this);
	}

	/**
	 * 判断当前参数是否有更改
	 */
	private void checkParamerChange() {
		if (this.mLastCheckParamsTime > 0 && System.currentTimeMillis() - this.mLastCheckParamsTime < 3000)
			return;
		List<Parameter> params = mParameterSet
				.getCheckedParamertersByNet(MyPhoneState.getInstance().getCurrentNetForParam(mContext));
		if (params == null || params.size() == 0 || this.parameterms == null || this.parameterms.size() != params.size()) {
			this.parameterms = params;
			return;
		}
		int count = 0;
		for (Parameter param : params) {
			for (Parameter param1 : this.parameterms) {
				if (param.getId().equals(param1.getId())) {
					count++;
				}
			}
		}
		if (params.size() != count) {
			this.parameterms = params;
			return;
		}
		this.mLastCheckParamsTime = System.currentTimeMillis();
	}

	/**
	 * 更新轨迹点坐标
	 */
	protected MapEvent getOverlayCoord(MapEvent mapEvent) {
		if (mapEvent.getAdjustLatitude() == 0
				&& mapEvent.getAdjustLongitude() == 0) {
			LatLng latlng = BaiduMapUtil.convert(mapEvent.getLatitude(), mapEvent.getLongitude());
			mapEvent.setAdjustLatitude(latlng.latitude);
			mapEvent.setAdjustLongitude(latlng.longitude);
		}
		return mapEvent;
	}

	/**
	 * 判断当前的轨迹点是否和选择的基站相关联
	 *
	 * @param mapEvent 轨迹点
	 * @return 是否相连
	 */
	private boolean isConnectStation(MapEvent mapEvent) {
		BaseStation selectBaseStation = factory.getSelectBaseStation();
		if (selectBaseStation == null)
			return false;
		int index = selectBaseStation.detailIndex;
		if (index == -1)
			return false;
		BaseStationDetail detail = selectBaseStation.details.get(index);
		switch (selectBaseStation.netType) {
			case BaseStation.NETTYPE_GSM:
				if (mapEvent.getStationParamMap().containsKey("BS_BCCH")
						&& mapEvent.getStationParamMap().containsKey("BS_BSIC")) {
					String bcch = mapEvent.getStationParamMap().get("BS_BCCH");
					String bsic = mapEvent.getStationParamMap().get("BS_BSIC");
					if (detail.bcch.equals(bcch) && detail.bsic.equals(bsic))
						return true;
				}
				break;
			case BaseStation.NETTYPE_WCDMA:
				if (mapEvent.getStationParamMap().containsKey("BS_PSC")
						&& mapEvent.getStationParamMap().containsKey("BS_UARFCN")) {
					String psc = mapEvent.getStationParamMap().get("BS_PSC");
					String uarfcn = mapEvent.getStationParamMap().get("BS_UARFCN");
					if (detail.psc.equals(psc) && detail.uarfcn.equals(uarfcn))
						return true;
				}
				break;
			case BaseStation.NETTYPE_CDMA:
				if (mapEvent.getStationParamMap().containsKey("BS_PN") && mapEvent.getStationParamMap().containsKey("BS_FREQ")) {
					String pn = mapEvent.getStationParamMap().get("BS_PN");
					String freq = mapEvent.getStationParamMap().get("BS_FREQ");
					if (detail.pn.equals(pn) && detail.frequency.equals(freq))
						return true;
				}
				break;
			case BaseStation.NETTYPE_TDSCDMA:
				if (mapEvent.getStationParamMap().containsKey("BS_UARFCN")
						&& mapEvent.getStationParamMap().containsKey("BS_CPI")) {
					String uarfcn = mapEvent.getStationParamMap().get("BS_UARFCN");
					String cpi = mapEvent.getStationParamMap().get("BS_CPI");
					if (detail.uarfcn.equals(uarfcn) && detail.cpi.equals(cpi))
						return true;
				}
				break;
			case BaseStation.NETTYPE_LTE:
				if (mapEvent.getStationParamMap().containsKey("BS_PCI")
						&& mapEvent.getStationParamMap().containsKey("BS_EARFCN")) {
					String pci = mapEvent.getStationParamMap().get("BS_PCI");
					String earfcn = mapEvent.getStationParamMap().get("BS_EARFCN");
					if (detail.pci.equals(pci) && detail.earfcn.equals(earfcn))
						return true;
				}
				break;
		}
		return false;
	}

	@Override
	public void onRefreshed(RefreshEventManager.RefreshType refreshType, Object object) {
		if (DatasetManager.isPlayback) {
            addCurrentLocationOverlay(null);
		}
	}
}
