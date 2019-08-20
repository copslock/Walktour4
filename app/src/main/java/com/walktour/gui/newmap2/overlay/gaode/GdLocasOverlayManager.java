package com.walktour.gui.newmap2.overlay.gaode;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.dinglicom.dataset.DatasetManager;
import com.walktour.Utils.DensityUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ParameterSetting;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.gui.R;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap2.NewMapActivity;
import com.walktour.gui.newmap2.bean.MarkClickBean;
import com.walktour.gui.newmap2.bean.MarkLocasBean;
import com.walktour.gui.newmap2.overlay.BaseOverlayManager;
import com.walktour.gui.newmap2.overlay.OverlayType;
import com.walktour.gui.newmap2.ui.GpsLocasView;
import com.walktour.gui.newmap2.util.GaodeMapUtil;
import com.walktour.model.LocusParamInfo;
import com.walktour.model.MapEvent;
import com.walktour.model.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jinfeng.xie
 * @version 1.0.0
 * @date on 2018/6/15
 * @describe 定点打点
 */
public class GdLocasOverlayManager extends BaseOverlayManager implements RefreshEventManager.RefreshEventListener{

	private static final String TAG = GdLocasOverlayManager.class.getSimpleName();

	private static final int Z_INDEX = 9;

	private DatasetManager mDatasetMgr;//数据集管理对象
	private SharedPreferences mPreferences;//存储设置
	private ParameterSetting mParameterSet;//参数设置
	private List<Parameter> parameterms;//参数列表
	private long mLastCheckParamsTime = 0;//最后一次检测网络参数时间

	private List<Marker> markers = new ArrayList<>();
	private Marker mCurrentMarker;//当前的Marker
	private ArrayList<MapEvent> locasList;

	public GdLocasOverlayManager(Context context) {
		super(context);
		mDatasetMgr = DatasetManager.getInstance(mContext);
		mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		mParameterSet = ParameterSetting.getInstance();
		mParameterSet.initMapLocusShape(mContext);
		locasList = new ArrayList<>();
		RefreshEventManager.addRefreshListener(this);
	}

	@Override
	public void onDestory() {
		super.onDestory();
		RefreshEventManager.removeRefreshListener(this);
	}

	@Override
	public OverlayType getOverlayType() {
		return OverlayType.LocasPoint;
	}

	@Override
	public boolean onMapClick(Object... obj) {
		return super.onMapClick(obj);
	}

	@Override
	public void onMapLoaded() {
		super.onMapLoaded();
//		if (getMapControllor() != null && getMapControllor().getMyLocation() != null) {
//			addGpsOverlay(new MyLatLng(getMapControllor().getMyLocation().getLatitude(), getMapControllor().getMyLocation().getLongitude()));
//		} else {
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
		return false;
	}

	private void addGpsOverlay(MyLatLng myLatLng) {
		checkParamerChange();
		addCurrentLocationOverlay(myLatLng);
		addGpsLineOverlay();
	}

	/**
	 * 添加gps轨迹的点
	 */
	private void addGpsLineOverlay() {
		/**
		 * 2018/12/20
		 * 因为跟随模式按钮改动。没有导航模式，去掉导航这个选项，只能通过导航按钮去到导航模式
		 * @anthor jinfeng.xie
		 *
		 */
//		boolean isNavigation = mPreferences.getInt(NewMapActivity.AUTO_FOLLOW_MODE, 0) == 2;
		BaseStation selectBaseStation = factory.getSelectBaseStation();
		GpsLocasView view;
		List<MapEvent> gosLocas = TraceInfoInterface.traceData.getGpsLocas();
		Log.e(TAG, "locasList的长度:" + locasList.size());
		Log.e(TAG, "gosLocas的长度:" + gosLocas.size());
		if (gosLocas.size() <= locasList.size()) {
			return;
		}
		int start = locasList.size();
		for (int i = start; i < gosLocas.size(); i++) {
			locasList.add(gosLocas.get(i));
			factory.getLocasList().add(gosLocas.get(i));
			MapEvent mapEvent = gosLocas.get(i);
			MarkClickBean bean = new MarkClickBean();
			MarkLocasBean locasBean = new MarkLocasBean();
			locasBean.setMapEvent(mapEvent);
			bean.setCurrentPoint(false);
			bean.setObj(locasBean);
			bean.setOverlayType(OverlayType.LocasPoint);
			// 如果是导航模式,或者当前选择了基站且当前点和基站没有关联
			if ((selectBaseStation != null && !this.isConnectStation(mapEvent)) ) {
				view = new GpsLocasView(mContext);
				view.setRadius(getLocusRadius());
				view.setColor(Color.GRAY);
				view.setType(mParameterSet.getLocusShape() == 0 ? GpsLocasView.CIRCLE : GpsLocasView.RECT);
				BitmapDescriptor icon = BitmapDescriptorFactory.fromView(view);
				MarkerOptions option = new MarkerOptions()
						.position(new LatLng(mapEvent.getAdjustLatitude(), mapEvent.getAdjustLongitude()))
						.icon(icon)
						.zIndex(Z_INDEX);
				Marker marker = getMapControllor().addMarker(option);
				marker.setObject(bean);
				markers.add(marker);
			} else {
				if (parameterms == null || parameterms.isEmpty()) {
					view = new GpsLocasView(mContext);
					view.setRadius(getLocusRadius());
					view.setColor(Color.GRAY);
					view.setType(mParameterSet.getLocusShape() == 0 ? GpsLocasView.CIRCLE : GpsLocasView.RECT);
					BitmapDescriptor icon = BitmapDescriptorFactory.fromView(view);
					MarkerOptions option = new MarkerOptions()
							.position(new LatLng(mapEvent.getAdjustLatitude(), mapEvent.getAdjustLongitude()))
							.icon(icon)
							.zIndex(Z_INDEX);
					Marker marker = getMapControllor().addMarker(option);
					marker.setObject(bean);
					markers.add(marker);
				} else {
					for (int k = 0; k < parameterms.size(); k++) {
						LocusParamInfo info = mapEvent.getParamInfoMap().get(parameterms.get(k).getShowName());
						locasBean.setLocusParamInfo(info);
						bean.setObj(locasBean);
						int color = (info == null ? Color.GRAY : info.color);
						view = new GpsLocasView(mContext);
						view.setRadius(getLocusRadius());
						view.setColor(color);
						view.setType(mParameterSet.getLocusShape() == 0 ? GpsLocasView.CIRCLE : GpsLocasView.RECT);
						BitmapDescriptor icon = BitmapDescriptorFactory.fromView(view);
//                      这里获取屏幕对应的经纬度偏移量
						LatLng position = new LatLng(mapEvent.getLatitude(), mapEvent.getLongitude() + k * (float) getLocusRadius() * 0.0000005);
						position = GaodeMapUtil.convertToGaode(mContext, position, CoordinateConverter.CoordType.GPS);
						MarkerOptions option = new MarkerOptions()
								.position(position)
								.icon(icon)
								.zIndex(Z_INDEX);
						Marker marker = getMapControllor().addMarker(option);
						marker.setObject(bean);
						marker.setAlpha(0.8f);
						markers.add(marker);
					}
				}
			}
		}
		Log.e(TAG, "加载完之后：locasList的长度:" + locasList.size());
		Log.e(TAG, "加载完之后：markers:" + markers.size());
	}

	/**
	 * 显示当前位置的marker
	 *
	 * @param ll
	 */
	private void addCurrentLocationOverlay(MyLatLng ll) {
		if (mCurrentMarker != null) {
			mCurrentMarker.remove();
			markers.remove(mCurrentMarker);
		}
		MarkClickBean bean = new MarkClickBean();
		bean.setOverlayType(OverlayType.LocasPoint);
		bean.setObj(null);
		bean.setCurrentPoint(true);
		BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.iconmarker);
		if (DatasetManager.isPlayback) {
			for (int i = locasList.size() - 1; i >= 0; i--) {
				MapEvent mapEvent = locasList.get(i);
				if ((mDatasetMgr.currentIndex >= mapEvent.getBeginPointIndex()
						&& mDatasetMgr.currentIndex <= mapEvent.getEndPointIndex())
					|| mDatasetMgr.currentIndex >= mapEvent.getEndPointIndex()) {
					LatLng bdLL = GaodeMapUtil.convertToGaode(mContext, GaodeMapUtil.convertToLatLng(mapEvent.getLatLng()), CoordinateConverter.CoordType.GPS);
					MarkerOptions option = new MarkerOptions()
							.position(bdLL)
							.icon(icon)
							.zIndex(Z_INDEX*2);
					mCurrentMarker = getMapControllor().addMarker(option);
					mCurrentMarker.setObject(bean);
					markers.add(mCurrentMarker);
					break;
				}
			}
		} else {
			if (ll != null) {
				LatLng bdLL = GaodeMapUtil.convertToGaode(mContext, GaodeMapUtil.convertToLatLng(ll), CoordinateConverter.CoordType.GPS);
				MarkerOptions option = new MarkerOptions()
						.position(bdLL)
						.icon(icon)
						.zIndex(Z_INDEX + 1);
				mCurrentMarker = getMapControllor().addMarker(option);
				mCurrentMarker.setObject(bean);
				markers.add(mCurrentMarker);
			}
		}
	}

	@Override
	public boolean onMarkerClick(Object... obj) {
		return super.onMarkerClick(obj);
	}


	@Override
	public synchronized boolean clearOverlay() {
		if (!markers.isEmpty()) {
			for (Marker overlay : markers) {
				overlay.remove();
			}
			markers.clear();
		}
		if (mCurrentMarker!=null){
			mCurrentMarker.remove();
		}
		locasList.clear();
		return false;
	}

	private AMap getMapControllor() {
		return (AMap) mMapSdk.getMapControllor();
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

	/**
	 * 判断当前参数是否有更改
	 *
	 * @return
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
