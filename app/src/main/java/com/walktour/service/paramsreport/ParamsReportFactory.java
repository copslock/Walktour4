package com.walktour.service.paramsreport;

import android.content.Context;
import android.location.Location;

import com.dinglicom.dataset.DatasetManager;
import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.JsonParseUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.model.NetStateModel;
import com.walktour.service.paramsreport.model.ParamsReportModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.walktour.Utils.WalkStruct.CurrentNetState.CDMA;
import static com.walktour.Utils.WalkStruct.CurrentNetState.LTE;

/**
 * 参数上报平台工厂类
 * 
 * @author jianchao.wang
 *
 */
public class ParamsReportFactory implements RefreshEventListener {
	/** 日志标识 */
	private static final String TAG = "ParamsReportFactory";
	/** 唯一实例 */
	private static ParamsReportFactory sInstance;
	/** 要上传的参数列表 */
	private List<ParamsReportModel> mParamsList = new ArrayList<ParamsReportModel>();
	/** 当前是否在监听 */
	private boolean isListener = false;
	/** 数据集管理类 */
	private DatasetManager mDatasetManager;
	/** GPS获取类 */
	private GpsInfo mGPS;
	/** 要获取的参数key值 */
	private int[] mParamKeys = new int[] { UnifyParaID.L_SRV_RSRP, UnifyParaID.L_SRV_RSRQ, UnifyParaID.L_SRV_SINR,
			UnifyParaID.L_SRV_PCI, UnifyParaID.L_SRV_EARFCN, UnifyParaID.C_TotalEcIo, UnifyParaID.C_RxAGC,
			UnifyParaID.C_TotalSINR };
	/** 上次获取的采样点 */
	private int lastIndex;

	private ParamsReportFactory(Context context) {
		this.mDatasetManager = DatasetManager.getInstance(context);
		this.mGPS = GpsInfo.getInstance();
	}

	/**
	 * 返回唯一实例
	 * 
	 * @param context
	 *          上下文
	 * @return
	 */
	public static ParamsReportFactory get(Context context) {
		if (sInstance == null) {
			sInstance = new ParamsReportFactory(context);
		}
		return sInstance;
	}

	/**
	 * 获取要上传到平台的GPS和实时参数生成的json
	 * 
	 * @return
	 */
	public String getReportJson() {
		if (!this.isListener) {
			RefreshEventManager.addRefreshListener(this);
			this.isListener = true;
			return null;
		}
		try {
			JSONObject gps = this.createGPSJson();
			JSONArray params = this.createParamsJson();
			if (gps == null && params == null)
				return null;
			JSONObject json = new JSONObject();
			if (gps != null)
				json.put("GPS", gps);
			if (params != null)
				json.put("ParamList", params);
			JsonParseUtil util = new JsonParseUtil();
			util.parse(json);
//			LogUtil.w(TAG,"getReportJson="+util.getJson());
			return util.getJson();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 生成GPS数据的JSON对象
	 * 
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createGPSJson() throws JSONException {
		Location location = this.mGPS.getLocation();
		if (location == null)
			return null;
		JSONObject json = new JSONObject();
		json.put("Time", location.getTime());
		json.put("Longitude", location.getLongitude());
		json.put("Latitude", location.getLatitude());
		json.put("Altitude", location.getAltitude());
		return json;
	}

	/**
	 * 生成实时参数的json数组
	 * 
	 * @return
	 * @throws JSONException
	 */
	private JSONArray createParamsJson() throws JSONException {
		if (this.mParamsList.isEmpty())
			return null;
		JSONArray params = new JSONArray();
		while (!this.mParamsList.isEmpty()) {
			ParamsReportModel model = this.mParamsList.remove(0);
			JSONObject obj = new JSONObject();
			obj.put("Port", model.getPort());
			JSONObject gps = new JSONObject();
			gps.put("Time", model.getTime());
			gps.put("Longitude", model.getLongitude());
			gps.put("Latitude", model.getLatitude());
			gps.put("Altitude", model.getAltitude());
			obj.put("Position", gps);
			JSONArray inheritValues = new JSONArray();
			for (int i = 0; i < this.mParamKeys.length; i++) {
				if (model.getInheritValues()[i] == -9999)
					continue;
				JSONArray array = new JSONArray();
				array.put(this.mParamKeys[i]);
				array.put(model.getInheritValues()[i]);
				inheritValues.put(array);
			}
			if (inheritValues.length() > 0) {
				NetStateModel state = NetStateModel.getInstance();
				WalkStruct.CurrentNetState status = state.getCurrentNetTypeSync();
				if (status == LTE) {//LTE网络增加一个参数
					JSONArray array = new JSONArray();
					array.put(UnifyParaID.CURRENT_NETWORKTYPE);
					array.put(LTE.getNetType());
					inheritValues.put(array);
				}else if (status == CDMA) {//CDMA网络增加一个参数
					JSONArray array = new JSONArray();
					array.put(UnifyParaID.CURRENT_NETWORKTYPE);
					array.put(CDMA.getNetType());
					inheritValues.put(array);

					JSONArray array2 = new JSONArray();
					array2.put(UnifyParaID.CURRENT_STATE_CDMA);
					int val=TraceInfoInterface.getIntParaValue(UnifyParaID.CURRENT_STATE_CDMA);
					if(val!=-9999) {
						array2.put(val);
						inheritValues.put(array2);
					}
					JSONArray array3 = new JSONArray();
					array3.put(UnifyParaID.CURRENT_STATE_EVDO);
					val=TraceInfoInterface.getIntParaValue(UnifyParaID.CURRENT_STATE_EVDO);
					if(val!=-9999) {
						array3.put(val);
						inheritValues.put(array3);
					}
				}
				obj.put("ParamVList_S", inheritValues);
			}
			JSONArray realValues = new JSONArray();
			for (int i = 0; i < this.mParamKeys.length; i++) {
				if (model.getRealValues()[i] == -9999)
					continue;
				JSONArray array = new JSONArray();
				array.put(this.mParamKeys[i]);
				array.put(model.getRealValues()[i]);
				realValues.put(array);
			}
			if (realValues.length() > 0)
				obj.put("ParamVList_D", realValues);
			if (obj.has("ParamVList_S") || obj.has("ParamVList_D"))
				params.put(obj);
		}
		if (params.length() > 0)
			return params;
		return null;
	}

	/**
	 * 拼装要发送的参数对象
	 *
	 *          参数对象
	 */
	private void addParams() {
		int index = this.mDatasetManager.currentIndex;
		if (index < 0 || index == this.lastIndex)
			return;
		Location location = this.mGPS.getLocation();
//		if (location == null)
//			return;
		try {
			this.lastIndex = index;
			int port = DatasetManager.isPlayback ? DatasetManager.PORT_4 : DatasetManager.PORT_2;
			LogUtil.d(TAG, "--------index:" + index + ",port:" + port + "-----------");
			String params = this.mDatasetManager.batchGetRealParam(this.mParamKeys, this.mParamKeys.length, index, false);
			LogUtil.d(TAG, "--------inheritValues:" + params + "-----------");
			if (params == null)
				return;
			String[] inheritValues = params.split("@@");
			params = this.mDatasetManager.batchGetRealParam(this.mParamKeys, this.mParamKeys.length, index, true);
			LogUtil.d(TAG, "--------realValues:" + params + "-----------");
			if (params == null)
				return;
			String[] realValues = params.split("@@");
			ParamsReportModel model = new ParamsReportModel();
			model.setPort(port);
			model.setParamKeys(this.mParamKeys);
			model.setTime(null == location ? System.currentTimeMillis() : location.getTime());
			model.setLongitude(null == location ? -9999 : location.getLongitude());
			model.setLatitude(null == location ? -9999 : location.getLatitude());
			model.setAltitude(null == location ? -9999 : location.getAltitude());
			double[] values1 = new double[this.mParamKeys.length];
			double[] values2 = new double[this.mParamKeys.length];
			for (int i = 0; i < this.mParamKeys.length; i++) {
				values1[i] = Double.parseDouble(inheritValues[i]);
				values2[i] = Double.parseDouble(realValues[i]);
			}
			model.setInheritValues(values1);
			model.setRealValues(values2);
			this.mParamsList.add(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 移除事件监听
	 */
	public void removeListener() {
		if (this.isListener) {
			RefreshEventManager.removeRefreshListener(this);
			this.isListener = false;
		}
	}

	@Override
	public void onRefreshed(RefreshType refreshType, Object object) {
		switch (refreshType) {
		case REFRESH_PARAM_TAB:
		case ACTION_WALKTOUR_TIMER_CHANGED:
			this.addParams();
			break;
		default:
			break;
		}
	}

}
