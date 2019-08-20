package com.dinglicom.dataset.logic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.telephony.TelephonyManager;
import android.util.SparseArray;

import com.dinglicom.DataSetLib;
import com.dinglicom.DataSetLib.EnumNetType;
import com.dinglicom.DataSetUtil;
import com.dinglicom.data.control.BuildTestRecord;
import com.dinglicom.data.control.DataTableStruct.RecordAbnormalEnum;
import com.dinglicom.dataset.DatasetManager;
import com.dinglicom.dataset.EventManager;
import com.dinglicom.dataset.PageQueryParas;
import com.dinglicom.dataset.model.DataSetEvent;
import com.dinglicom.dataset.model.ENDCDataModel;
import com.dinglicom.dataset.model.EventModel;
import com.dinglicom.dataset.model.ModuleInfo;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.ConstItems;
import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.ShowInfo;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.StructParseUtil;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalDial;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.TypeConver;
import com.walktour.Utils.UnifyL3Decode;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UnifyStruct;
import com.walktour.Utils.UnifyStruct.ParaStruct;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.Abnormal;
import com.walktour.Utils.WalkStruct.TaskType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ParamInfo;
import com.walktour.control.config.ParamItem;
import com.walktour.control.config.ParamTotalInfo;
import com.walktour.control.config.ParameterSetting;
import com.walktour.control.config.ServerManager;
import com.walktour.control.instance.AlertManager;
import com.walktour.framework.view.L3MsgRefreshEventManager;
import com.walktour.gui.R;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.map.PointStatus;
import com.walktour.gui.map.TraceInfoData;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap.util.Util;
import com.walktour.gui.task.parsedata.xml.btu.model.BtuEvent;
import com.walktour.model.AlarmModel;
import com.walktour.model.GsmStructModel;
import com.walktour.model.HistoryPoint;
import com.walktour.model.LocusParamInfo;
import com.walktour.model.MapEvent;
import com.walktour.model.Parameter;
import com.walktour.model.TdL3Model;
import com.walktour.model.Threshold;

import org.andnav.osm.util.GeoPoint;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 数据集建造对象<BR>
 * [功能详细描述]
 *
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2013-8-29]
 */
@SuppressLint("SimpleDateFormat")
public class DatasetBuilder {

	private String TAG = "DatasetBuilder";

	private final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");

	/**
	 * 句柄
	 */
	private int datasetHandle;

	private Context mContext;

	private ModuleInfo moduleInfo;
	private ParameterSetting mParameterSet;

	private BuildTestRecord		testRecord;
	private ArrayList<Integer> 	abnormalList;

	// 统计相关
	private ApplicationModel appModel;

	private EventManager mEventMgr;
	private AlertManager mAlertMgr;

	// qihang.li 2014.4.11 增加BTU事件的转换
	private ServerManager mServerMgr = null;

	private SparseArray<ParamInfo> paramDrawList;
	private ArrayList<Integer> totalEvents;
	private SparseArray<ParamInfo> allTotalInfo;
	private final int LTE_Cover_Rate_RSRP = -105;
	private final int LTE_Cover_Rate_SINR = -3;
	private final int TD_Cover_Rate_RSCP = -94;
	private final int TD_Cover_Rate_CtoI = -3;
	private final int GSM_Cover_Rate_RxlevSub = -90;
	private final int GSM_Cover_Rate_BcchLev = -90;
	private SparseArray<Integer> mLastTotalPointCount = new SparseArray<>();
    /**
     * 最后一次GPS计算
     */
	private int mLastGPSCount = 0;
	private ENDCDataModel endcDataModel = null;
	/**
	 * [构造简要说明]
	 */
	public DatasetBuilder(int datasetHandle, ModuleInfo moduleInfo, Context context) {
		this.datasetHandle = datasetHandle;
		this.mContext = context;
		this.moduleInfo = moduleInfo;

		mParameterSet = ParameterSetting.getInstance();
		appModel = ApplicationModel.getInstance();
		paramDrawList = ParamTotalInfo.getInstance().getParaIdList();
		totalEvents = ParamTotalInfo.getInstance().getAllTotalEvents();
		allTotalInfo = ParamTotalInfo.getInstance().getAllTotalInfo();

		mEventMgr = EventManager.getInstance();
		mAlertMgr = AlertManager.getInstance(mContext);
		abnormalList = Abnormal.getAbnormalList();

		if (appModel.getNetList().contains(WalkStruct.ShowInfoType.Btu)) {
			mServerMgr = ServerManager.getInstance(mContext);
		}
	}

    /**
	 * 查询参数界面数据统一<BR>
	 * 只需要传入需要查询参数数组
	 *
	 * @param port
	 *          端口,默认1
	 * @param pointIndex
	 *          当前采样点
	 * @param params
	 *          需要查询的参数
	 */
	public void buildDatasetParamQuery(int port, int pointIndex, int[] params) {
		if (pointIndex > 0) {
//            LogUtil.d(TAG, "----buildDatasetParamQuery----start----params:" + Arrays.toString(params));
			String result = DatasetManager.getInstance(mContext).getDatasetLib().batchGetRealParam(datasetHandle, port, params, params.length, pointIndex, false);
			// LogUtil.w(TAG, "--params:" + UtilsMethod.arrayToString(params));
//			LogUtil.w(TAG, "--arrlen4yy:" + params.length + "--V:" + result);
			String[] value = result != null ? result.split("@@") : new String[]{};

			if(params.equals(PageQueryParas.pageNBIoTQueryPublicId)) {
				if(appModel.isTesting()||appModel.isTestJobIsRun()) {
					sendValue(value);
				}
			}

			if(params.equals(PageQueryParas.pageENDCQueryPublicId)){//ENDC
				if(appModel.isTesting()||appModel.isTestJobIsRun()) {
					sendValueENDC(value);
				}
			}
			if (value.length > 0) {
				for (int i = 0; i < value.length; i++) {
					String realValue = getRealValue(value[i]);
					if (params[i] == UnifyParaID.PBM_RBC) {
//						LogUtil.w(TAG, "--PBM_RBC:" + value[i] + "--" + realValue);
					}
					TraceInfoInterface.decodeResultUpdate(params[i], realValue);
				}
			}
//            LogUtil.d(TAG,"----buildDatasetParamQuery----end----params:" + Arrays.toString(params));
		}
	}
	private void sendValueENDC(String[] value)
	{
		if (null == value)
			return;
			try {
				LogUtil.w(TAG, "valuex=x=" + value.length + "," + value.toString());
				if (value.length >= 16) {
					float value1 =
							parseString(TraceInfoInterface.getParaValue(UnifyParaID.L_Thr_DL_PDCP_Thr));
					float value2 =
							parseString(TraceInfoInterface.getParaValue(UnifyParaID.L_Thr_UL_PDCP_Thr));
					float value3 =
							parseString(TraceInfoInterface.getParaValue(UnifyParaID.L_Thr_DL_RLC_Thr));
					float value4 =
							parseString(TraceInfoInterface.getParaValue(UnifyParaID.L_Thr_UL_RLC_Thr));
					float value5 =
							parseString(TraceInfoInterface.getParaValue(UnifyParaID.L_Thr_DL_MAC_Thr));
					float value6 =
							parseString(TraceInfoInterface.getParaValue(UnifyParaID.L_Thr_UL_MAC_Thr));
					float value7 =
							parseString(TraceInfoInterface.getParaValue(UnifyParaID.L_Thr_DL_Phy_Thr));
					float value8 =
							parseString(TraceInfoInterface.getParaValue(UnifyParaID.L_Thr_UL_Phy_Thr));
					float value9 =
							parseString(TraceInfoInterface.getParaValue(UnifyParaID.NR_Thr_DL_PDCP_Thr));
					float value10 =
							parseString(TraceInfoInterface.getParaValue(UnifyParaID.NR_Thr_UL_PDCP_Thr));
					float value11 =
							parseString(TraceInfoInterface.getParaValue(UnifyParaID.NR_Thr_DL_RLC_Thr));
					float value12 =
							parseString(TraceInfoInterface.getParaValue(UnifyParaID.NR_Thr_UL_RLC_Thr));
					float value13 =
							parseString(TraceInfoInterface.getParaValue(UnifyParaID.NR_Thr_DL_MAC_Thr));
					float value14 =
							parseString(TraceInfoInterface.getParaValue(UnifyParaID.NR_Thr_UL_MAC_Thr));
					float value15 =
							parseString(TraceInfoInterface.getParaValue(UnifyParaID.NR_Thr_DL_Phy_Thr));
					float value16 =
							parseString(TraceInfoInterface.getParaValue(UnifyParaID.NR_Thr_UL_Phy_Thr));

//					LogUtil.w(TAG, "value1=" + value1
//							+ "value2=" + value1
//							+ "value3=" + value2
//							+ "value4=" + value3
//							+ "value5=" + value4
//							+ "value6=" + value5
//							+ "value7=" + value6
//							+ "value8=" + value7
//							+ "value9=" + value8
//							+ "value10=" + value9
//							+ "value11=" + value10
//							+ "value12=" + value12
//							+ "value13=" + value13
//							+ "value14=" + value14
//							+ "value15=" + value15
//							+ "value16=" + value16
//					);
					//如果都是-9999则不发送广播刷新数据
					if (value1 == -9999
							&& value2 == -9999
							&& value3 == -9999
							&& value4 == -9999
							&& value5 == -9999
							&& value6 == -9999
							&& value7 == -9999
							&& value8 == -9999
							&& value9 == -9999
							&& value10 == -9999
							&& value11 == -9999
							&& value12 == -9999
							&& value13 == -9999
							&& value14 == -9999
							&& value15 == -9999
							&& value16 == -9999) {
						return;
					}
					endcDataModel = null;
					endcDataModel = new ENDCDataModel();
					endcDataModel.L_Thr_DL_PDCP_Thr = value1;
					endcDataModel.L_Thr_UL_PDCP_Thr = value2;
					endcDataModel.L_Thr_DL_RLC_Thr = value3;
					endcDataModel.L_Thr_UL_RLC_Thr = value4;
					endcDataModel.L_Thr_DL_MAC_Thr = value5;
					endcDataModel.L_Thr_UL_MAC_Thr = value6;
					endcDataModel.L_Thr_DL_Phy_Thr = value7;
					endcDataModel.L_Thr_UL_Phy_Thr = value8;
					endcDataModel.NR_Thr_DL_PDCP_Thr = value9;
					endcDataModel.NR_Thr_UL_PDCP_Thr = value10;
					endcDataModel.NR_Thr_DL_RLC_Thr = value11;
					endcDataModel.NR_Thr_UL_RLC_Thr = value12;
					endcDataModel.NR_Thr_DL_MAC_Thr = value13;
					endcDataModel.NR_Thr_UL_MAC_Thr = value14;
					endcDataModel.NR_Thr_DL_Phy_Thr = value15;
					endcDataModel.NR_Thr_UL_Phy_Thr = value16;
					ShowInfo.getInstance().setEndcDataModel(endcDataModel);
				}
			} catch (Exception ex) {
				LogUtil.w(TAG, ex.getMessage());
			}
	}

	private void sendValue(String[] value) {
		if (null == value)
			return;
		if (ApplicationModel.getInstance().isNBTest()) {//NB模块UDP测试，上行取速率
				if(ApplicationModel.getInstance().isNBTest()) {
					try {
						if (value.length >= 7) {
							if (Float.parseFloat(value[5]) != -9999.00) {
								Intent intent = new Intent(WalkMessage.ACTION_EVENT);
								intent.putExtra(WalkMessage.KEY_EVENT_RCUID, UnifyParaID.L_THR_DL_UDP);
								intent.putExtra("nb_up_value", Float.parseFloat(value[5]));
								mContext.sendBroadcast(intent);
								intent=null;
							}
							if (Float.parseFloat(value[6]) != -9999.00) {
								Intent intent = new Intent(WalkMessage.ACTION_EVENT);
								intent.putExtra(WalkMessage.KEY_EVENT_RCUID, UnifyParaID.L_THR_DL_DDP);
								intent.putExtra("nb_dp_value", Float.parseFloat(value[6]));
								mContext.sendBroadcast(intent);
								intent=null;
							}

							//
							float valuex=parseString(TraceInfoInterface.getParaValue(UnifyParaID.L_Thr_UL_RLC_Thr));
//							LogUtil.w(TAG,"vvvalues1="+valuex);
							if(valuex!=-9999.00){
								Intent intent = new Intent(WalkMessage.ACTION_EVENT);
								intent.putExtra(WalkMessage.KEY_EVENT_RCUID, UnifyParaID.L_Thr_UL_RLC_Thr);
								intent.putExtra("nb_dp_value1", valuex);
								mContext.sendBroadcast(intent);
								intent=null;
							}
							valuex=parseString(TraceInfoInterface.getParaValue(UnifyParaID.L_Thr_DL_RLC_Thr));
//							LogUtil.w(TAG,"vvvalues2="+valuex);
							if(valuex!=-9999.00){
								Intent intent = new Intent(WalkMessage.ACTION_EVENT);
								intent.putExtra(WalkMessage.KEY_EVENT_RCUID, UnifyParaID.L_Thr_DL_RLC_Thr);
								intent.putExtra("nb_dp_value2", valuex);
								mContext.sendBroadcast(intent);
								intent=null;
							}
							valuex=parseString(TraceInfoInterface.getParaValue(UnifyParaID.L_Thr_UL_MAC_Thr));
//							LogUtil.w(TAG,"vvvalues3="+valuex);
							if(valuex!=-9999.00){
								Intent intent = new Intent(WalkMessage.ACTION_EVENT);
								intent.putExtra(WalkMessage.KEY_EVENT_RCUID, UnifyParaID.L_Thr_UL_MAC_Thr);
								intent.putExtra("nb_dp_value3", valuex);
								mContext.sendBroadcast(intent);
								intent=null;
							}
							valuex=parseString(TraceInfoInterface.getParaValue(UnifyParaID.L_Thr_DL_MAC_Thr));
//							LogUtil.w(TAG,"vvvalues4="+valuex);
							if(valuex!=-9999.00){
								Intent intent = new Intent(WalkMessage.ACTION_EVENT);
								intent.putExtra(WalkMessage.KEY_EVENT_RCUID, UnifyParaID.L_Thr_DL_MAC_Thr);
								intent.putExtra("nb_dp_value4", valuex);
								mContext.sendBroadcast(intent);
								intent=null;
							}

							valuex=parseString(TraceInfoInterface.getParaValue(UnifyParaID.L_Thr_UL_Phy_Thr));
//							LogUtil.w(TAG,"vvvalues5="+valuex);
							if(valuex!=-9999.00){
								Intent intent = new Intent(WalkMessage.ACTION_EVENT);
								intent.putExtra(WalkMessage.KEY_EVENT_RCUID, UnifyParaID.L_Thr_UL_Phy_Thr);
								intent.putExtra("nb_dp_value5", valuex);
								mContext.sendBroadcast(intent);
								intent=null;
							}
							valuex=parseString(TraceInfoInterface.getParaValue(UnifyParaID.L_Thr_DL_Phy_Thr));
//							LogUtil.w(TAG,"vvvalues6="+valuex);
							if(valuex!=-9999.00){
								Intent intent = new Intent(WalkMessage.ACTION_EVENT);
								intent.putExtra(WalkMessage.KEY_EVENT_RCUID, UnifyParaID.L_Thr_DL_Phy_Thr);
								intent.putExtra("nb_dp_value6", valuex);
								mContext.sendBroadcast(intent);
								intent=null;
							}

							valuex=parseString(TraceInfoInterface.getParaValue(UnifyParaID.L_Thr_UL_PDCP_Thr));
//							LogUtil.w(TAG,"vvvalues5="+valuex);
							if(valuex!=-9999.00){
								Intent intent = new Intent(WalkMessage.ACTION_EVENT);
								intent.putExtra(WalkMessage.KEY_EVENT_RCUID, UnifyParaID.L_Thr_UL_PDCP_Thr);
								intent.putExtra("nb_dp_value7", valuex);
								mContext.sendBroadcast(intent);
								intent=null;
							}
							valuex=parseString(TraceInfoInterface.getParaValue(UnifyParaID.L_Thr_DL_PDCP_Thr));
//							LogUtil.w(TAG,"vvvalues6="+valuex);
							if(valuex!=-9999.00){
								Intent intent = new Intent(WalkMessage.ACTION_EVENT);
								intent.putExtra(WalkMessage.KEY_EVENT_RCUID, UnifyParaID.L_Thr_DL_PDCP_Thr);
								intent.putExtra("nb_dp_value8", valuex);
								mContext.sendBroadcast(intent);
								intent=null;
							}

						}
					} catch (Exception ex) {
						LogUtil.w(TAG, ex.getMessage());
					}
				}
		}
	}

	/***
	 * 如果是空值，返回无效值-9999，不纳入统计
	 * @param str
	 * @return
	 */
	private float parseString(String str){
		if(null==str||str.length()==0){
			return -9999;
		}
		try{
			return Float.valueOf(str);
		}catch(Exception ex){
			LogUtil.w(TAG,ex.getMessage());
		}
		return 0;
	}
	/**
	 * 函数功能：查询特殊结构体
	 *
	 * @param port
	 *          端口
	 * @param pointIndex
	 *          当前采样点
	 * @param iSpecialKey
	 *          特殊结构体的ID
	 */
	public void buildSpecialStruct(int port, int pointIndex, int iSpecialKey) {
		if (pointIndex > 0) {
			byte[] byteDataset = DatasetManager.getInstance(mContext).getDatasetLib().getSpecialStructInfo(datasetHandle, port, pointIndex, iSpecialKey);

			if (byteDataset.length > 0) {
				// 把字节数组转换为对应的结构体
				UnifyStruct struct = new UnifyStruct();
				ParaStruct p1 = struct.BuildStruct(byteDataset);
				TraceInfoInterface.decodeStructUpdate(iSpecialKey, p1);
			} else {
				TraceInfoInterface.decodeStructUpdate(iSpecialKey, null);
			}

		}
	}

	/**
	 * 构建LTE网络GSM邻区参数特殊结构体
	 * @param port
	 * @param pointIndex
	 * @param iSpecialKey
	 */
	public void buildGSMSpecialStruct(int port, int pointIndex, int iSpecialKey) {
		if (pointIndex > 0) {
			byte[] byteDataset = DatasetManager.getInstance(mContext).getDatasetLib().getOriginalStructInfo(datasetHandle, port, pointIndex, iSpecialKey);
			if (byteDataset.length > 0) {
				// 把字节数组转换为对应的结构体
				UnifyStruct struct = new UnifyStruct();
				ParaStruct p1 = struct.BuildGsmStruct(byteDataset);
				TraceInfoInterface.decodeStructUpdate(iSpecialKey, p1);
			} else {
				TraceInfoInterface.decodeStructUpdate(iSpecialKey, null);
			}

		}
	}

	/**
	 * 构建ENDC邻区参数特殊结构体
	 * @param port
	 * @param pointIndex
	 * @param iSpecialKey
	 */
	public void buildENDCSpecialStruct(int port, int pointIndex, int iSpecialKey) {
		if (pointIndex > 0) {
			byte[] byteDataset = DatasetManager.getInstance(mContext).getDatasetLib().getOriginalStructInfo(datasetHandle, port, pointIndex, iSpecialKey);
			if (byteDataset.length > 0) {
				// 把字节数组转换为对应的结构体
				UnifyStruct struct = new UnifyStruct();
				ParaStruct p1 = struct.BuildENDCStruct(byteDataset);
				TraceInfoInterface.decodeStructUpdate(iSpecialKey, p1);
			} else {
				TraceInfoInterface.decodeStructUpdate(iSpecialKey, null);
			}

		}
	}

	/**
	 * 查询特殊结构体,用新工具类解析
	 */
	public void buildSpecialStructNew(int port, int pointIndex, int iSpecialKey) {
		if (pointIndex > 0) {
			byte[] byteDataset = DatasetManager.getInstance(mContext).getDatasetLib().getOriginalStructInfo(datasetHandle, port, pointIndex, iSpecialKey);
			try {
				if (byteDataset.length > 0) {
					// 把字节数组转换为对应的结构体
					GsmStructModel struct = new GsmStructModel();
					List<GsmStructModel> gsmStructModels = StructParseUtil.parse(struct, byteDataset);
					if (gsmStructModels != null) {
						GsmStructModel gsmModel = gsmStructModels.get(0);
						TraceInfoInterface.setGsmStructModel(gsmModel);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 通过源始数据接口获询特殊结构信息
	 *
	 * @param port
	 * @param pointIndex
	 * @param iSpecialKey
	 */
	public void buildSpecialStruct2(int port, int pointIndex, int iSpecialKey) {
		byte[] byteDataset = DatasetManager.getInstance(mContext).getDatasetLib().getOriginalStructInfo(datasetHandle, port, pointIndex, iSpecialKey);
		if (byteDataset.length > 0) {
			UnifyStruct struct = new UnifyStruct();
			ParaStruct p1 = struct.BuildStruct(iSpecialKey, byteDataset);
			TraceInfoInterface.decodeStructUpdate(iSpecialKey, p1);
		} else {
			TraceInfoInterface.decodeStructUpdate(iSpecialKey, null);
		}
	}

	/**
	 * 从层3信息生成自定义事件
	 *
	 * @param l3
	 */
	private void l3Msg2CustomEvent(TdL3Model l3, boolean isReplay) {
		// 判断自定义事件流程
		List<EventModel> newEventList = mEventMgr.l3Msg2CustomEvent(mContext, l3, isReplay);
		for (EventModel event : newEventList) {
			// 告警
			mAlertMgr.addAlarmFromEvent(event);

			// 测试时统计自定义事件
			if (event.isShowOnTotal()) {
				GeoPoint geoPoint = GpsInfo.getInstance().getLastGeoPoint();
				mEventMgr.addCutstomEventTotal(mContext, event, geoPoint);
			}
		}
	}

	/**
	 * 函数功能： 2014.5.21用于处理数据集不准确的Block_Call之后没有Connect事件的问题 这里直接生成事件发送到主被叫进程
	 *
	 * @param code
	 */
	private void l3Msg2CallEvent(long code) {
		if (code == 0x713A0307 // CDMA以外的网络
				|| code == 0x10050E00 || code == 0x10081400) {

			Intent callEvent = new Intent(WalkMessage.ACTION_EVENT);
			callEvent.putExtra(WalkMessage.KEY_EVENT_RCUID, DataSetEvent.ET_MO_Connect);
			callEvent.putExtra(WalkMessage.KEY_EVENT_TIME, System.currentTimeMillis());
			callEvent.putExtra(WalkMessage.KEY_EVENT_STRING, "Outgoing Call Established");
			mContext.sendBroadcast(callEvent);
		}
	}

	/**
	 * 查询层三信令<BR>
	 * [功能详细描述]
	 *
	 * @param port
	 *          端口
	 * @param from
	 *          起始采样点
	 * @param to
	 *          结束采样点
	 * @param isReplay
	 *          是否回放
	 */
	public void buildL3MsgQuery(int port, int from, int to, boolean isReplay) {
		if (from <= to && from >= 0) {
//			LogUtil.d("wxckssgala", "----buildL3MsgQuery----start----from:" + from + "----to:" + to + ",currentIndex="+DatasetManager.getInstance(mContext).getCurrentIndex()+"----isReplay:" + isReplay);
			// ArrayList<TdL3Model> l3Array = new ArrayList<TdL3Model>();
//			LogUtil.d("ykykyk","from="+from+",to="+to);
			String result = DatasetManager.getInstance(mContext).getDatasetLib().getMsgCode(datasetHandle, port, from, to, true, false);
			if (result != null && result.length() > 0) {
				String[] paras = result.split("##");

				for (int i = 0; paras != null && i < paras.length; i++) {
					String ids[] = paras[i].split("@@");
//					LogUtil.w("wxckssgala",paras[i]+"----====---index="+ids[0]+",diretion="+ids[3]);
					if (ids != null && ids.length == 4) {
						String pointIndex = ids[0];
						String time = ids[1];
						String code = ids[2];
						String direction = ids[3];
						//不显示手机信令.
						if(!ServerManager.getInstance(mContext).hasShowL1L2Command()&&direction.trim().equals(TdL3Model.TdL3Model_MMS+"")){
							continue;
						}

						long msgCode = 0;
						try {
							msgCode = Long.valueOf(code, 16);
						} catch (NumberFormatException e) {

						}
						long msgHead = msgCode >> 16;
						msgHead = msgHead & 0x0000FFFF;
						// LogUtil.d("L3msgCode:", Long.toHexString(msgCode) +
						// "");
						if (msgHead != 0xd090 && msgHead != 0xfff0 && msgHead != 0xee03) {
							TdL3Model l3 = UnifyL3Decode.disposeL3Info( msgCode);
							l3.setId(msgCode);
							l3.setDirection(direction);

							l3Msg2CallEvent((int) msgCode);
							/*
							 * if (msgHead == 0x413f) {
							 * //新的定义，将W的RRC和TD的RRC分开，但是目前系统只定义了412fxxxx，故要减掉这个值
							 * msgCode-=0x100000; }
							 */
							if (!l3.getL3Msg().equals("")) {

								if (!time.startsWith("f")) {
									try {
										l3.setTime(Long.valueOf(time, 16) / 1000);
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									// 2013.11.21
									l3.setTime(0);
								}

								String msg = UtilsMethod.sdfhms.format(l3.getTime()) + " " + l3.getL3Msg();
//								LogUtil.d(TAG, "----buildL3MsgQuery----L3Msg:" + UtilsMethod.sdFormat.format(new Date(l3.getTime())) + l3.getL3Msg());
								l3.setL3Msg(msg);
								l3.setPointIndex(Integer.valueOf(pointIndex));
								/*
								 * LogUtil.d(TAG,"buildL3MsgQuery>>>> msg = " + " id = 0x" +
								 * Integer.toHexString(l3.getId()) + "  index = pointIndex" +
								 * l3.getPointIndex()+ "  msg = " + l3.getL3Msg());
								 */

								TraceInfoInterface.traceData.l3MsgList.add(l3);
								//这是个临时调用的方法，由于会导致异常，暂时从当前方法中隐藏，以后要正式使用，需要找业务组提供新的调用方法
//								if (!isReplay)
//									this.dealSIPRegisterUnauthorized(l3);
								while (!isReplay && TraceInfoInterface.traceData.l3MsgList.size() > 200) {
									TraceInfoInterface.traceData.l3MsgList.remove(0);

									// 添加完得马上通知界面更新
									/*
									 * Intent intent = new Intent( WalkMessage.traceL3MsgChanged);
									 * mContext.sendBroadcast(intent);
									 */
								}

								L3MsgRefreshEventManager.getInstance().notifyL3MsgRefreshed(WalkMessage.traceL3MsgChanged, l3.getL3Msg());

								// 添加完得马上通知界面更新
								Intent intent = new Intent(WalkMessage.traceL3MsgChanged);
								intent.putExtra(WalkMessage.traceL3MsgInfo, l3.getL3Msg());
								intent.putExtra(WalkMessage.traceIndexPoint, pointIndex);
								intent.putExtra("L3Str",
										l3.getTime() + "," + l3.getL3Msg() + "," + l3.getDirection() + "," + pointIndex + "," + l3.getId());
								mContext.sendBroadcast(intent);

								l3Msg2CustomEvent(l3, isReplay);
							}
						}

					}

				}
			}
//			LogUtil.d(TAG,"----buildL3MsgQuery----end----");
		}
		// lastPointIndex = count;
		return;
	}

//	/**
//	 * 对于sip-register-unauthorized指令做特殊处理
//	 * 这是个临时调用的方法，由于会导致异常，暂时从当前方法中隐藏，以后要正式使用，需要找业务组提供新的调用方法
//	 * @param l3
//	 *          层三信令
//	 */
//	private void dealSIPRegisterUnauthorized(TdL3Model l3) {
//		if (l3.getId() != 0x1500F006)
//			return;
//		int port = DatasetManager.PORT_2;
//		if (DatasetManager.isPlayback) {
//			port = DatasetManager.PORT_4;
//		} else {
//			port = DatasetManager.PORT_2;
//		}
//		String detail = this.getL3Detail(port, l3.getPointIndex());
//		String flag = "nonce=&quot;";
//		int pos = detail.indexOf(flag);
//		if (pos > 0) {
//			pos += flag.length();
//			detail = detail.substring(pos);
//			pos = detail.indexOf("&quot;");
//			if (pos > 0) {
//				String nonce = detail.substring(0, pos);
//				LogUtil.d(TAG, "nonce:" + nonce);
//				QMIServerFactory.getInstance().sendRequestToServer(nonce);
//				String msg = QMIServerFactory.getInstance().getRespMsgFromServer();
//				LogUtil.d(TAG, "QMIServerRespMsg:" + msg);
//			}
//		}
//	}

	/**
	 * 查询层三详细解码
	 *
	 * @param index
	 * @return
	 */

	public String getL3Detail(int port, int index) {
		String l3DetailStr = "";
		try {
			l3DetailStr = DatasetManager.getInstance(mContext).getDatasetLib().getDetailMsg(datasetHandle, port, index);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return l3DetailStr;
	}

	/**
	 * 获得统一邻区集合 （WCDMA、LTE、CDMA、EVDO）
	 *
	 * @param port
	 * @param index
	 * @param netType
	 * @return 结果集字符串
	 */
	public String getCellSetQuery(int port, int index, DataSetLib.EnumNetType netType) {
		StringBuffer allStr = new StringBuffer();
		int count = getTotalPointCount(port);
		int cells = 0;
		if (count > 0 && index < count) {
			for (int cellIndex = 0; cellIndex < netType.getNetCellArray().length; cellIndex++) {
				int itemNum = DatasetManager.getInstance(mContext).getDatasetLib().getSetCellCount(datasetHandle, port,
						(netType == EnumNetType.EVDO ? EnumNetType.CDMA.ordinal() : netType.ordinal()),
						netType.getNetCellArray()[cellIndex].ordinal(), index, netType == EnumNetType.EVDO, false);
				for (int i = 0; i < itemNum && (i + cells) < 9; i++) { // 控制邻区列表最多只能取6个
					String result = DatasetManager.getInstance(mContext).getDatasetLib().getSetCellParam(datasetHandle, port, netType.getNetCellParas(),
							netType.getNetCellParas().length,
							(netType == EnumNetType.EVDO ? EnumNetType.CDMA.ordinal() : netType.ordinal()),
                            netType.getNetCellArray()[cellIndex].ordinal(), index, i, netType == EnumNetType.EVDO, false);
					if (result != null) {
						String[] value = result.split("@@");
						if (value.length > 0 && value.length == netType.getNetCellParas().length) {
							StringBuffer resultStr = new StringBuffer();

							for (int resultI = 0; resultI < value.length; resultI++) {
								if (netType.getNetCellParas()[resultI] == UnifyParaID.Set_SetType) {
									resultStr.append(netType.getNetCellArray()[cellIndex].getSetCellType());
								} else {
									resultStr.append(getRealValue(value[resultI]));
								}
								if (resultI != value.length - 1) {
									resultStr.append(",");
								}
							}
							allStr.append(";");
							allStr.append(resultStr);
						}
					}
				} // end for
				cells += itemNum;
				if (cells >= 9) {
					break;
				}
			} // end if (count > 0)

		}
		return allStr.toString();
	}

	/**
	 * 统一查询邻区集合 （WCDMA、LTE、CDMA、EVDO）
	 *
	 * @param index
	 * @return
	 */
	public void buildCellSetQuery(int port, int index, DataSetLib.EnumNetType netType) {
		String str = this.getCellSetQuery(port, index, netType);
		int count = getTotalPointCount(port);
		if (count > 0 && index < count) {
			TraceInfoInterface.decodeResultUpdate(netType.getNetSetID(), str);
		}
	}
	/**
	 * 获得采样点总数<BR>
	 * [功能详细描述]
	 *
	 * @param port 端口
	 * @return 总数
	 */
	public int getTotalPointCount(int port) {
		return this.getTotalPointCount(port,false);
	}
    /**
     * 获得采样点总数<BR>
     * [功能详细描述]
     *
     * @param port 端口
     * @param isRefresh 是否刷新采样点数
     * @return 总数
     */
    public int getTotalPointCount(int port,boolean isRefresh) {
    	if(!isRefresh && this.mLastTotalPointCount.get(port) != null)
    		return this.mLastTotalPointCount.get(port);
    	int count = DatasetManager.getInstance(mContext).getDatasetLib().getTotalPointCount(datasetHandle, port);
        this.mLastTotalPointCount.put(port,count);
        return count;
    }

	/**
	 * 开始测试启动查询事件线程前,设置当前文件记录对象,后面异常事件查询时直接对访对象赋值
	 * @param buildTestRecord
	 */
	public void setBuildTestRecord(BuildTestRecord buildTestRecord){
		this.testRecord = buildTestRecord;
	}

	/**
	 * 查询新产生的若干个事件,每个事件都用dataChangeListener返回 在查询结果添加统计流程
	 *
	 * @param isReplay
	 *          是否回放
	 * @param moduleInfo
	 * @return 查询到新增加事件数
	 */
	public ArrayList<EventModel> buildEvents(int port, boolean isReplay, ModuleInfo moduleInfo) {
		return buildEvents(port,isReplay,moduleInfo,-1,-1);
	}

	/**
	 * 查询新产生的若干个事件,每个事件都用dataChangeListener返回 在查询结果添加统计流程
	 *
	 * @param isReplay
	 *          是否回放
	 * @param moduleInfo
	 * @param startPoint	如果有指定开始采样点,那么加载事件从指定采样点开始	未指定为-1,只对回放有用
	 * @param endPoint 		如果有结束采样点,加载事件到指定位置结束,未指定为-1,只对回放有用
	 * @return 查询到新增加事件数
	 */
	public ArrayList<EventModel> buildEvents(int port, boolean isReplay, ModuleInfo moduleInfo,int startPoint,int endPoint) {
		LogUtil.d(TAG,"----buildEvents----start----");
		boolean queryByPointIndex = false;
		int maxIndex = -1;

		if (isReplay) {
			//-1;twq20161020如果未指定开始值,进来的值为-1,此处直接替换为默认传的值即可
			moduleInfo.lastEventIndex = startPoint;
		}

		if(startPoint != -1 && endPoint != -1){
			queryByPointIndex = true;
			maxIndex = getTotalPointCount(port);
			//小于最大采样点值
			if(endPoint < maxIndex){
				maxIndex = endPoint;
			}
		}else{
			maxIndex = DatasetManager.getInstance(mContext).getDatasetLib().getEventTotalCount(datasetHandle, port) -1;	// 最后一个事件的序号为count-1;
		}

		// 2013.10.22事件过多时只能分多次查询，每次最多查100个事件
		final int STEP = 100;
		ArrayList<EventModel> result = new ArrayList<>();

		do {
			// 查询一次:要从上次最后一个事件序号+1开始读(先不管有没有新的)
			int from = moduleInfo.lastEventIndex + 1;
			int to = from + STEP;
			to = to <= maxIndex ? to : maxIndex;// to 不能超过最大值

			ArrayList<EventModel> newEvents = queryEvents(port, from, to,queryByPointIndex);
			result.addAll(newEvents);
			moduleInfo.lastEventIndex = to;
		} while (moduleInfo.lastEventIndex < maxIndex);// 还有未查事件时继续

		// 回放时一次全部添加
		if (isReplay) {
			mEventMgr.addReplayEvents(result);
			mAlertMgr.addReplayEvents(result);
		} else {
			// 测试时
			for (EventModel eventModel : result) {
				mEventMgr.addEvent(mContext, eventModel, false);
				mEventMgr.sentEventBroadcast(mContext, eventModel);
				mAlertMgr.addAlarmFromEvent(eventModel);

				if (mServerMgr != null) {
					if (mServerMgr.genDTLogEvent()) {
						BtuEvent btuEvent = BtuEvent.convertFormRcu(eventModel.getTime(), eventModel.getRcuId(),
								mServerMgr.getDTLogMoudleNum());
						if (btuEvent != null) {
							mServerMgr.pushDTLogEvent(btuEvent);
						}
					}
				}
			}
		}
        LogUtil.d(TAG,"----buildEvents----end----");
		return result;
	}

	/**
	 * 查询参数界面数据统一<BR>
	 * 只需要传入需要查询参数数组
	 *
	 * @param port
	 *          端口
	 * @param pointIndex
	 *          当前采样点
	 * @param params
	 *          需要查询的参数
	 */
	public void buildDataBroardQuery(Context context,int port, int pointIndex, int[] params) {
		if (pointIndex > 0) {
			String result = DatasetManager.getInstance(mContext).getDatasetLib().batchGetRealParam(datasetHandle, port, params, params.length, pointIndex, false);
			// LogUtil.w(TAG, "--arrlen3:" + params.length + "--V:" + result);
			String[] values = !StringUtil.isNullOrEmpty(result) ? result.split("@@") : new String[] {};
			ShowInfo.getInstance().getYwDataModel().addPlayBackBordPoint(context,datasetHandle, port, pointIndex, values);
		}
	}

	/**
	 * 查询参数界面数据统一<BR>
	 * 只需要传入需要查询参数数组
	 *
	 * @param port
	 *          端口
	 * @param pointIndex
	 *          当前采样点
	 * @param params
	 *          需要查询的参数
	 */
	public void buildVideoStreamQuery(int port, int pointIndex, int[] params) {
		if (pointIndex > 0) {
			String result = DatasetManager.getInstance(mContext).getDatasetLib().batchGetRealParam(datasetHandle, port, params, params.length, pointIndex, false);
			// LogUtil.w(TAG, "--arrlen5:" + params.length + "--V:" + result);
			String[] value = !StringUtil.isNullOrEmpty(result) ? result.split("@@") : new String[] {};
			if (value.length > 0) {
				Map<String, Object> valuesHM = TraceInfoInterface.traceData.getVideoRealPrar();
				for (int i = 0; i < value.length; i++) {
					switch (params[i]) {
					case 0x0A00207B:
						valuesHM.put(ConstItems.TOTAL_RECV_SIZE, getRealValue(value[i]));
						break;
					case 0x0A002075:
						valuesHM.put(ConstItems.DOWN_PROCESS, getRealValue(value[i]));
						break;
					case 0x0A00207E:
						valuesHM.put(ConstItems.MEAN_DL_RATE, getRealValue(value[i]));
						break;
					case 0x0A002072:
						valuesHM.put(ConstItems.REBUF_COUNTS, getRealValue(value[i]));
						break;
					case 0x0A002074:
						valuesHM.put(ConstItems.REBUF_TIMES, getRealValue(value[i]));
						break;
					case 0x0A00207D:
						valuesHM.put(ConstItems.AV_SYNC, getRealValue(value[i]));
						break;
					case 0x0A002076:
						valuesHM.put(ConstItems.TOTAL_BIT_RATE, getRealValue(value[i]));
						break;
					case 0x0A002057:
						valuesHM.put(ConstItems.VIDEO_FPS, getRealValue(value[i]));
						break;
					case 0x0A00206C:
						valuesHM.put(ConstItems.VMOS, getRealValue(value[i]));
						break;
					case 0x0A002079:
						valuesHM.put(ConstItems.CurFps, getRealValue(value[i]));
						break;
					case 0x0A00206E:
						valuesHM.put(ConstItems.VideoJitter, getRealValue(value[i]));
						break;
					case 0x0A00206F:
						valuesHM.put(ConstItems.AudioJitter, getRealValue(value[i]));
						break;
					case 0x0A00205C:
						valuesHM.put(ConstItems.RecvVideoPacket, getRealValue(value[i]));
						break;
					case 0x0A00205E:
						valuesHM.put(ConstItems.LostVideoPacket, getRealValue(value[i]));
						break;
					default:
						break;
					}
				}
				mContext.sendBroadcast(new Intent(WalkCommonPara.VIDEO_REAL_PARA_CHANGE));
			}

		}
	}

	/**
	 * 根据传入的需要统计的ID列表，将实时查询的参数结果用dataChangeListener返回
	 *
	 * @param port
	 * @param moduleInfo
	 * @return
	 */
    public int buildParamTotalValue(int port, ModuleInfo moduleInfo) {
        int count = getTotalPointCount(port) - 1;
        int from = moduleInfo.lastTotalIndex + 1;
//        LogUtil.d(TAG, "----buildParamTotalValue----start----from:" + from + "----to:" + count);
        if (count > from) {
            if (count - from > 100)
                from = count - 100;
            int[] ids = ParamTotalInfo.getInstance().getCurrentPointFilterTotalParas();
            String params = DatasetManager.getInstance(mContext).getDatasetLib().batchGetRealParam(datasetHandle, port, ids, ids.length, count, true);
            if(!StringUtil.isNullOrEmpty(params)) {
                String[] values = params.split("@@");
                for (int i = 0; i < ids.length; i++) {
                    changeTotalParam(ids[i], values[i], port);
                }
            }
            ids = ParamTotalInfo.getInstance().getCurrentPointNoFilterTotalParas();
            params = DatasetManager.getInstance(mContext).getDatasetLib().batchGetRealParam(datasetHandle, port, ids, ids.length, count, false);
            if(!StringUtil.isNullOrEmpty(params)) {
                String[] values = params.split("@@");
                for (int i = 0; i < ids.length; i++) {
                    changeTotalParam(ids[i], values[i], port);
                }
            }
            ids = ParamTotalInfo.getInstance().getFilterTotalParas();
            for (int id : ids) {
                params = DatasetManager.getInstance(mContext).getDatasetLib().getRealParam2(datasetHandle, port, id, from, count, true, true);
                changeTotalParam(id, params, port);
            }
            ids = ParamTotalInfo.getInstance().getNoFilterTotalParas();
            for (int id : ids) {
                params = DatasetManager.getInstance(mContext).getDatasetLib().getRealParam2(datasetHandle, port, id, from, count, false, true);
                changeTotalParam(id, params, port);
            }
            moduleInfo.lastTotalIndex = count - 1;
        }
//        LogUtil.d(TAG, "----buildParamTotalValue----end----");
        return 0;
    }

	/**
	 * 根据传进来的事件列表，判断当前是否有参与统计的值
	 *
	 * @param events
	 */
	public void buildEventTotalValue(ArrayList<EventModel> events) {
		for (int i = 0; i < events.size(); i++) {
			if (totalEvents.contains(events.get(i).getRcuId())) {
				updateTotalKeyValue(events.get(i).getRcuId(), 1);
			}
		}
	}

	/**
	 * 根据统计参数的参数ID，及当前区间的参数变化值进将指定值填入到对应的统计结果中
	 *
	 * @param paramValue
	 * @param port
	 *
	 */
	private void changeTotalParam(int id, String paramValue, int port) {
		try {
			if (paramValue != null && !paramValue.equals("")) {
				String[][] values = DataSetUtil.splitValues(paramValue);
//                LogUtil.d(TAG, "----changeTotalParam----id:" + id + "----values:" + Arrays.deepToString(values));
				/*
				 * if(id == 0x7F000106){ LogUtil.w(TAG, "--RxQualSub:" + paramValue +
				 * "--vs:" + values.length); }
				 */
				for (int i = 0; i < values.length; i++) {
					// 当前参数统计是否为带有附属属性的参数ID，如果是根据主参数对应采样点的ID，获得列表中辅助参数ID对应采样点的继承值
					if (allTotalInfo.get(id).totalType == ParamTotalInfo.TotalType_Quality_Main) {
						// 当前当前参数有效值所对应的采样点中，附属结构的继承值
						// 数组1存放的是当前主参数有几个辅助参数，对应的数组2存放的是对应辅助参数对应序号的继承值
						LinkedHashMap<Integer, Long> assistParamValues = new LinkedHashMap<>();

						for (int as = 0; as < allTotalInfo.get(id).assistParamList.size(); as++) {

							// 获得指定ID的指定采样点继承值
							long assistValue = (long) DatasetManager.getInstance(mContext).getDatasetLib().getParamRealValue(datasetHandle, port,
									Integer.parseInt(values[i][1]), allTotalInfo.get(id).assistParamList.get(as),
									allTotalInfo.get(id).isFilter);

							assistParamValues.put(allTotalInfo.get(id).assistParamList.get(as), assistValue);

							/*
							 * if(id == UnifyParaID.L_SRV_RSRP){ LogUtil.w(TAG,"--mi:" +
							 * Integer.toHexString(id) + ":" + values[count][0] + "--ai:" +
							 * Integer.toHexString
							 * (allTotalInfo.get(id).assistParamList.get(as)) + ":" +
							 * assistValue); }
							 */
						}

						updateTotalKeyValue(id, values[i][0], assistParamValues);
					}

					// 不管当前参数是否为需特处理的覆盖参数，都参数基本参数的最大最小统计
					int vs = (int) (Float.parseFloat(values[i][0]) * allTotalInfo.get(id).scale);
					updateTotalKeyValue(id, vs);

					// 当前参数是否需要画术状态图
					if (paramDrawList.get(id) != null) {
						setDrawPara(id, vs);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新指定统计ID，相应值到对应的统计表中 当前方法根据ID区分是参数统计还是事件统计 参数统计如果为语音业务，还执行质量统计
	 *
	 * @param keyId
	 * @param keyValue
	 */
	private void updateTotalKeyValue(int keyId, int keyValue) {
		ParamInfo paramInfo = allTotalInfo.get(keyId);
		// 当前统计参数是否需要限制业务
		if (paramInfo.limitTask != null && paramInfo.limitTask.size() > 0) {
			// 当前任务包含于参数限制业务列表中
			if (paramInfo.limitTask.contains(appModel.getCurrentTask())) {
				// 当前统计非数据业务限制，或者为数据业务限制且当前业务状态为收到FirstData，当前参数才参与统计
				// LogUtil.w(TAG, "--dataLimit:" + paramInfo.isDataLimit +
				// "--isFirst:" + appModel.isFirstData());
				if (!paramInfo.isDataLimit || (paramInfo.isDataLimit && appModel.isFirstData())) {
					updateToTotalResult(paramInfo, keyValue);
				}
			}
		} else {
			updateToTotalResult(paramInfo, keyValue);
		}

		// 当前非数据业务即语音业务，需要通计语音质量
		if (paramInfo.totalType == ParamTotalInfo.TotalType_Other) {
			totalCallQuality(keyId, keyValue);
		}
	}

	/**
	 * 将统计结调用具体的方法存储到相应的位置中
	 *
	 * @param paramInfo
	 * @param keyValue
	 */
	private void updateToTotalResult(ParamInfo paramInfo, int keyValue) {
		if (paramInfo.totalType == ParamTotalInfo.TotalType_Event) {
			TotalDataByGSM.getInstance().updateTotalEvent(paramInfo.getKeyNameByTask(appModel.getCurrentTask()), keyValue);
		} else {
			TotalDataByGSM.getInstance().updateTotalMeasurePara(paramInfo.getKeyNameByTask(appModel.getCurrentTask()),
					keyValue);
		}
	}

	/**
	 * 设置事件对象的相关信息
	 *
	 * @param alarmList
	 *          事件列表
	 * @param startX
	 *          起始点
	 * @param endX
	 *          结束点
	 * @param parameter
	 *          参数对象
	 */
	private void setMapAlarmEvent(List<AlarmModel> alarmList, int port, double startX, double startY, double endX,
			double endY, Parameter parameter) {
		int count = alarmList.size();
		double xOffset = (endX - startX) / (count + 1);
		double yOffset = (endY - startY) / (count + 1);
		for (int i = 0; i < count; i++) {
			AlarmModel alarmModel = alarmList.get(i);
			MapEvent event = new MapEvent();
			event.setLongitude(startX + (i + 1) * xOffset);
			event.setLatitude(startY + (i + 1) * yOffset);
			event.setParamName(parameter.getShowName());
			if (event.getStatus() == 0) {
				event.setColor(ParameterSetting.getInstance().getGpsEventColor(mContext, parameter, event.getValue()));
			} else {
				event.setColor(ParameterSetting.getInstance().getGpsColor());
			}

			int netType = Double.valueOf(getRealParam(port, UnifyParaID.CURRENT_NETWORKTYPE, alarmModel.getMsgIndex(), alarmModel.getMsgIndex(), false, true)).intValue();
			int paramKeys[] = AlertManager.getCurrentParams(netType);
			String paramValues = DatasetManager.getInstance(mContext).getDatasetLib().batchGetRealParam(datasetHandle, port, paramKeys, paramKeys.length,
					alarmModel.getMsgIndex(), false);

			// LogUtil.w(TAG, "--arrlen2:" + paramKeys.length + "--V:" +
			// paramValues + "--Net:" + netType);
			alarmModel.setMapPopInfo(AlertManager.genMapPopInfo(netType, paramValues));

			alarmModel.setMapEvent(event);
			MapFactory.getMapData().getEventQueue().add(alarmModel);
		}
	}

	/**
	 * 按指定采样点查询事件
	 *
	 * @param port
	 * @param from
	 *          起始采样点
	 * @param to
	 *          结束采样点
	 * @return
	 */
//	private ArrayList<EventModel> queryEvents(int port, int from, int to) {
//		return queryEvents(port,from,to,false);
//	}

	/**
	 * 按指定采样点查询事件
	 *
	 * @param port
	 * @param from
	 *          起始采样点
	 * @param to
	 *          结束采样点
	 * @return
	 */
	private ArrayList<EventModel> queryEvents(int port, int from, int to,boolean byPointIndex) {
		ArrayList<EventModel> result = new ArrayList<>();
		// 当前最后一个事件序号满足大于等于要读点from的序号时，才是有新的
		if (to >= from) {
			LogUtil.d(TAG,"----queryEvents----start----");
			String eventsStr = null;
			if(byPointIndex){
				eventsStr = DatasetManager.getInstance(mContext).getDatasetLib().getPointsEvent(datasetHandle,port,from,to);
			}else{
				eventsStr = DatasetManager.getInstance(mContext).getDatasetLib().getEvent(datasetHandle, port, from, to, true, 0);
			}
			if (eventsStr != null) {
				// 解析一次查询返回来的多个事件,各个事件是用##格开的.
				String[] events = eventsStr.split("##");
				 LogUtil.w(TAG, "----event result from:" + from + "to:" + to + "--event.size():" + events.length);
				for (String e : events) {
					EventModel eventModel = genOneEvent(e, port);
					if (eventModel != null) {

                        int rcuId = eventModel.getRcuId();

                        if ((rcuId == RcuEventCommand.LTE_SWITCH_REQUEST)) {
							long source_freq = DatasetManager.getInstance(mContext).getDatasetLib().getEventPropertyValue(datasetHandle, port, eventModel.getEventIndex(),
									RcuEventCommand.LTE_SWITCH_SOURCE_FREQ);
							long source_pci = DatasetManager.getInstance(mContext).getDatasetLib().getEventPropertyValue(datasetHandle, port, eventModel.getEventIndex(),
									RcuEventCommand.LTE_SWITCH_SOURCE_PCI);
							long targe_freq = DatasetManager.getInstance(mContext).getDatasetLib().getEventPropertyValue(datasetHandle, port, eventModel.getEventIndex(),
									RcuEventCommand.LTE_SWITCH_TARGE_FREQ);
							long targe_pci = DatasetManager.getInstance(mContext).getDatasetLib().getEventPropertyValue(datasetHandle, port, eventModel.getEventIndex(),
									RcuEventCommand.LTE_SWITCH_TARGE_PCI);

							LogUtil.w(TAG,
									"-------getEventPropertyValue:" + "--:" + eventModel.getPointIndex() + "--:"
											+ eventModel.getEventIndex() + "--:" + source_freq + "--:" + source_pci + "--:" + targe_freq
											+ "--:" + targe_pci);

							Intent intent = new Intent();
							intent.setAction(WalkMessage.LTE_SWITCH_BROADCAST);
							intent.putExtra(RcuEventCommand.LTE_SWITCH_SOURCE_FREQ + "", source_freq);
							intent.putExtra(RcuEventCommand.LTE_SWITCH_SOURCE_PCI + "", source_pci);
							intent.putExtra(RcuEventCommand.LTE_SWITCH_TARGE_FREQ + "", targe_freq);
							intent.putExtra(RcuEventCommand.LTE_SWITCH_TARGE_PCI + "", targe_pci);
							mContext.sendBroadcast(intent);
						}else if ((rcuId == DataSetEvent.ET_MO_Attempt) || (rcuId == DataSetEvent.ET_MT_Attempt)){
                            long attemptValue = DatasetManager.getInstance(mContext).getDatasetLib().getEventPropertyValue(datasetHandle, port, eventModel.getEventIndex(),
                                    RcuEventCommand.PROPERTY_ID_VOICE_BASE);

                            Intent intent = new Intent();
                            intent.setAction(WalkMessage.ACTION_PROPERTY);
                            intent.putExtra(WalkMessage.KEY_EVENT_RCUID , rcuId);
                            intent.putExtra(WalkMessage.KEY_PROPERTY , attemptValue);
                            mContext.sendBroadcast(intent);
                        }

						if(testRecord != null && abnormalList.contains(rcuId)){
							//Abnormal abnormal = Abnormal.getAbnormalByEventId(eventModel.getRcuId());
							int abnormalId = 0x40000000 + rcuId;

							testRecord.setRecordAbnormalMsg(String.valueOf(eventModel.getTime()), RecordAbnormalEnum.abnormal_time.name(), eventModel.getTime());
							testRecord.setRecordAbnormalMsg(String.valueOf(eventModel.getTime()), RecordAbnormalEnum.abnormal_point.name(), eventModel.getPointIndex());
							testRecord.setRecordAbnormalMsg(String.valueOf(eventModel.getTime()), RecordAbnormalEnum.abnormal_type.name(), abnormalId);
						}

						result.add(eventModel);
					}
				}
                LogUtil.d(TAG,"----queryEvents----end----");
			} else {
				// Log.i(TAG, "DatasetManager.getInstance(mContext).getDatasetLib().getEvent null:" + from + "-->" + to);
			}
		}
		return result;
	}

	/**
	 * 生成一个事件
	 *
	 * @param oneEventStr
	 *          一个时间的字串如4e385dab19310@@c7@@20051@@2388
	 * @param port
	 *          端口，这里应该是2
	 * @return 输入不正确或在EventManager里没有对应事件ID时返回null
	 */
	private EventModel genOneEvent(String oneEventStr, int port) {
		try {
//			LogUtil.d(TAG, "--event:" + oneEventStr);
			String[] params = oneEventStr.split("@@");
			long time = 0;
			if (params.length < 3) {
				return null;
			}

			if (!params[0].equals("") && !params[0].startsWith("f")) {
				time = Long.parseLong(params[0], 16) / 1000; // 时间(微秒转毫秒)
			} else {
				time = 0;
			}
			int eventIndex = Integer.parseInt(params[1], 16); // 序号
			int rcuId = Integer.parseInt(params[2], 16); // RCU ID
			int msgIndex = Integer.parseInt(params[3], 16); // 采样点序号
			String eventStr = EventManager.getInstance().getEventStr(rcuId);
			int totalPoint = getTotalPointCount(port) - 1;// 总采样点
			if (eventStr != null) {
//				LogUtil.i(TAG, String.format("--->%d,%d/%d,%s,0x%s,%s", eventIndex, msgIndex, totalPoint, df.format(time),
//						params[2], eventStr));
				// 生成事件
				EventModel event = new EventModel(time, eventStr,
						eventStr.startsWith("Tag Event") || eventStr.equals("DataService Msg") ? EventModel.TYPE_TAG
								: EventModel.TYPE_RCU);
				event.setEventIndex(eventIndex);
				event.setPointIndex(msgIndex);
				event.setRcuId(rcuId);
				// 查询一个事件的附属结构
				String descs = DatasetManager.getInstance(mContext).getDatasetLib().getEventDesc(datasetHandle, port, event.getEventIndex(), event.getRcuId());
				if (descs != null) {
					LogUtil.d(TAG, descs);
					// descs = descs.replaceAll("-9999.000",
					// "-").replaceAll("-9999", "-");
					event.setDescStr(descs.replaceAll("-9999.000", "-").replaceAll("-9999", "-"));
				}
				return event;
			}
		} catch (Exception e) {
			LogUtil.w(TAG, "genOneEvent", e);
		}

		return null;
	}

	private String getRealValue(String value) {
		if (value == null)
			return "";
		String realValue = "";
		if (value.endsWith("-9999.00") || value.endsWith("-9999")) {
			realValue = "";
		} else {
			if (value.endsWith(".00")) {
				realValue = value.substring(0, value.length() - 3);
			} else {
				realValue = value;
			}
		}
		return realValue;
	}

	/**
	 * Build GPS 数据<BR>
	 * 相应接口getRealGPSPointCount,getRealGPSPointInfo,getPointIndexZoneByGPSIndex
	 * 逻辑： 1、先获取GPS总数getRealGPSPointCount
	 * 2、再根据GPS序号,调用getRealGPSPointInfo或者GPS位置信息,返回格式格式：
	 * “DateTime@@longitude@@latitude@@Altiutde@@Speed@@SatelliteNum@@GroundDegree
	 * ” 3、通过序号获得采样点getPointIndexZoneByGPSIndex
	 * 4、通过采样点获得详细参数信息getRealParam,组织轨迹点MapEvent Model 5、添加到轨迹队列
	 *
	 * @param port
	 * @return
	 */
	public int buildAllGPSInfoQuery(int port) {
		int count = DatasetManager.getInstance(mContext).getDatasetLib().getRealGPSPointCount(datasetHandle, port);
		// 判断是否是Mark点
		boolean isIndoorMarked = DatasetManager.getInstance(mContext).getDatasetLib().haveIndoorMarked(datasetHandle, port);
		int showMap = 0;
		String[] dtmap = this.mContext.getResources().getStringArray(R.array.sys_dtmap_default);
		if (dtmap.length > 2 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[2])) {
			showMap = 1;
		}else if(dtmap.length >3 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[3])) {
			showMap = 1;
		}else if(dtmap.length > 4 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[4])) {
			showMap = 1;
		}

		int gpsStep = 1 + count / TraceInfoData.GPSLIMITSIZE;
		LogUtil.d(TAG, "buildAllGPSInfoQuery>>>" + "__Count:" + count + "IsIndoorMarked:" + isIndoorMarked + "--gpsStep:"
				+ gpsStep + "--show:" + showMap);
		/**
		 * @anthor jinfeng.xie
		 * 室内打点的时候:
		 * 1.有经纬度的时候，取第一个点x,y（经纬度）作参照并设为屏幕左下角(0,0)
		 * 2.之后取到的点都减去第一个点的经纬度，
		 * 3.转换成像素显示在屏幕上MapFactory.getMapData().getPointStatusStack().add(pointStatus);
		 */
		double    PI =  3.14159265;
		boolean addFirstPoint=false;//是否已经设置第一个参考点
		double firstPointX=0;//第一个点的精度
		double firstPointY=0;//第一个点的维度
		//室内测试新结构的写点第一个点是插入的gps点，不参与显示
//		int startIndex = isIndoorMarked ? 1 : 0;
		int startIndex =  0;
		for (int i = startIndex; i < count; i++) {
			if (i % gpsStep == 0) {
				String gpsPointInfo = DatasetManager.getInstance(mContext).getDatasetLib().getRealGPSPointInfo(datasetHandle, port, i);
				LogUtil.d(TAG, "GetRealGPSPointInfo>>>" + gpsPointInfo);
				if (!StringUtil.isNullOrEmpty(gpsPointInfo)) {
					String gpsInfo[] = gpsPointInfo.split("@@");
					if (isIndoorMarked && !MapFactory.isLoadTAB()) {
						double xPoint=0;
						double yPoint=0;
						//经纬度转换成米
						if (!addFirstPoint){//如果是第一个点
							firstPointX =  Double.valueOf(gpsInfo[1]);
							firstPointY =  Double.valueOf(gpsInfo[2]);
							xPoint=0;
							yPoint=0;
							addFirstPoint=true;
						}else {//减去第一个参考点
							xPoint=Double.valueOf(gpsInfo[1])-firstPointX;
							yPoint=Double.valueOf(gpsInfo[2])-firstPointY;
						}
						double dTemp =  Double.valueOf(gpsInfo[2]) * PI / 180;
						double	xM =  (xPoint) * 111 * 1000*Math.cos(dTemp); // 经度1度 = 等于111km乘纬度的余弦
						double	yM =  (yPoint) * 111 * 1000 ;// 纬度1度 = 大约111km
						//再转换成像素
						float plottingScale = MapFactory.getMapData().getPlottingScale();//像素/米
						float xPx = (float) (xM * plottingScale);
						float yPx = (float) (yM * plottingScale);
						LogUtil.d(TAG,"gpsPointInfo:" + gpsPointInfo + "xM:" + xM + " , yM:" + yM + "xPx:" + xPx + " , yPx:" + yPx + " , plottingScale:" + plottingScale);
						PointF point = new PointF(xPx , yPx);
						PointStatus pointStatus = new PointStatus();
						pointStatus.setPoint(point);
						int sampleSize = MapFactory.getMapData().getSampleSize();
						pointStatus.getPoint().y = (MapFactory.getMapData().getMap() == null ? mContext.getResources().getDisplayMetrics().heightPixels
								: MapFactory.getMapData().getMapHeight() * sampleSize) - pointStatus.getPoint().y;
						pointStatus.getPoint().x /= sampleSize;
						pointStatus.getPoint().y /= sampleSize;
						pointStatus.setStatus(PointStatus.POINT_STATUS_EFFECTIVE);
						pointStatus.setLatLng(new MyLatLng(Double.valueOf(gpsInfo[2]),Double.valueOf(gpsInfo[1])));
						LogUtil.d(TAG,"pointStatus:x"+pointStatus.getPoint().x);
						LogUtil.d(TAG,"pointStatus:y"+pointStatus.getPoint().y);
						MapFactory.getMapData().getPointStatusStack().add(pointStatus);
						addEventBetweenMark(i, port);
					} else {
						addGpsInfo(gpsInfo, i, port, showMap, false, true);
						this.addEventBetweenGPS(i, port);
					}
				}
			}
		}
		return count;
	}

	/**
	 * 建造新加入GPS数据<BR>
	 * [功能详细描述]
	 *
	 * @param port
	 *          端口
	 * @param IsIndoorMarked
	 *          是否为室内打点,否1为 GPS打点
	 * @return
	 */

	public int buildNewGPSInfo(int port, boolean IsIndoorMarked) {
		int count = DatasetManager.getInstance(mContext).getDatasetLib().getRealGPSPointCount(datasetHandle, port);
		if(count == mLastGPSCount)
		    return count;
		LogUtil.d(TAG, "buildNewGPSInfo>>>" + "____Count:" + count + "IsIndoorMarked:" + IsIndoorMarked);
		for (int i=mLastGPSCount;i<count;i++){
			String gpsPointInfo = DatasetManager.getInstance(mContext).getDatasetLib().getRealGPSPointInfo(datasetHandle, port, i - 1);
			LogUtil.d(TAG, "buildNewGPSInfo>>>" + gpsPointInfo);
			if (!StringUtil.isNullOrEmpty(gpsPointInfo)) {
				int showMap = 0;
				String[] dtmap = this.mContext.getResources().getStringArray(R.array.sys_dtmap_default);
				if (dtmap.length > 2 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[2])) {
					showMap = 1;
				}else if(dtmap.length >3 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[3])) {
					showMap = 1;
				}else if(dtmap.length > 4 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[4])) {
					showMap = 1;
				}
				if (IsIndoorMarked) {
					addEventBetweenMark(i, port);//室内的话，传实际下标
//					addEventBetweenMark(i - 1, port);
				} else {
					String gpsInfo[] = gpsPointInfo.split("@@");
					addGpsInfo(gpsInfo, i - 1, port, showMap, false, false);
					this.addEventBetweenGPS(i - 1, port);
				}
			}
		}
        mLastGPSCount = count;
		return count;

	}

	/**
	 * 查询参数值信息（实际值）<BR>
	 * [功能详细描述]
	 *
	 * @param port
	 * @param param      参数ID
	 * @param fromIndex  起始采样点索引
	 * @param toIndex    终止采样点索引
	 * @param filter     非继承值（即实际解码值），false=继承值
	 * @param statistics 是否过滤无效值
	 * @return 字符串，格式如“value1@@value2@@value3@@value4...”
	 */
	private String getRealParam(int port, int param, int fromIndex, int toIndex, boolean filter, boolean statistics) {
		//防止因采样点数据过多导致应用无响应特设置最大采样点范围
		if (toIndex - fromIndex > 100)
			fromIndex = toIndex - 100;
		return DatasetManager.getInstance(mContext).getDatasetLib().getRealParam(datasetHandle, port, param, fromIndex, toIndex, filter, statistics);
	}

	/**
	 * 添加GPS点间的事件
	 *
	 * @param index
	 * @param port
	 */
	private void addEventBetweenGPS(int index, int port) {
		MapEvent beginEvent = null;
		MapEvent endEvent = null;
		LogUtil.d(TAG, "---addEventBetweenGPS getGpsLocasSize:" + TraceInfoInterface.traceData.getGpsLocas().size());
		for (int i = TraceInfoInterface.traceData.getGpsLocas().size() - 1, t = 0; i >= 0; i--, t++) {
			MapEvent event = TraceInfoInterface.traceData.getGpsLocas().get(i);
			if (t == 0) {
				endEvent = event;
			} else {
				beginEvent = event;
				break;
			}
		}

		LogUtil.d(TAG, "---addEventBetweenGPS begin:" + (beginEvent == null) + "--end:" + (endEvent == null));
		if (beginEvent != null && endEvent != null) {
			int beginPointIndex = endEvent.getBeginPointIndex();
			int endPointIndex = endEvent.getEndPointIndex();
			Queue<AlarmModel> queue = new LinkedBlockingQueue<AlarmModel>();
			List<Parameter> parameterList = mParameterSet
					.getCheckedParamertersByNet(MyPhoneState.getInstance().getCurrentNetForParam(mContext));
			for (int j = 0; j < parameterList.size(); j++) {
				String result = getRealParam( port, Integer.valueOf(parameterList.get(j).getId(), 16),
						beginPointIndex, endPointIndex, true, true);
				// LogUtil.d(TAG,"result:" + result);
				if (!StringUtil.isNullOrEmpty(result)) {
					String radomParams[] = result.split("@@");
					Queue<AlarmModel> q = getPoint(radomParams, beginEvent.getLongitude() + j * 0.00001, beginEvent.getLatitude(),
							endEvent.getLongitude() + j * 0.00001, endEvent.getLatitude(), parameterList.get(j), beginPointIndex,
							endPointIndex, j);
					// 为了顺序显示当前点，如果中间有插值点，则把最后一个GPS点的起始序号修正为插值点的最后一个点的结束点
					if (!q.isEmpty()) {
						AlarmModel alarm = (AlarmModel) q.toArray()[q.size() - 1];
						endEvent.setBeginPointIndex(alarm.getMapEvent().getEndPointIndex() + 1);
					}
					MapFactory.getMapData().getEventQueue().addAll(q);
					queue.addAll(q);
				}
			}
			List<AlarmModel> alarmList = AlertManager.getInstance(mContext).getMapAlarmByIndex(beginPointIndex,
					endPointIndex);
			setMapAlarmEvent(alarmList, port, beginEvent.getLongitude(), beginEvent.getLatitude(), endEvent.getLongitude(),
					endEvent.getLatitude(), ParameterSetting.getInstance().getMapParameter());
			queue.addAll(alarmList);
			MapFactory.getMapData().getQueueStack().push(queue);
		}
		LogUtil.d(TAG,"--addEventBetweenGPS break---");
	}

	/**
	 * 添加记录点间的事件点 [功能详细描述]
	 *
	 * @param index
	 * @param port
	 */
	private void addEventBetweenMark(int index, int port) {
		PointStatus endPoint = null;
		PointStatus beginPoint = null;
		for (int i = MapFactory.getMapData().getPointStatusStack().size() - 1, t = 0; i >= 0; i--) {
			PointStatus point = MapFactory.getMapData().getPointStatusStack().elementAt(i);
			if (t == 0) {
				if (point.getStatus() == PointStatus.POINT_STATUS_EFFECTIVE
						|| point.getStatus() == PointStatus.POINT_STATUS_PREVIOUS_EFFECTIVE) {
					endPoint = point;
					t++;
				}
			} else {
				if (point.getStatus() == PointStatus.POINT_STATUS_EFFECTIVE
						|| point.getStatus() == PointStatus.POINT_STATUS_PREVIOUS_EFFECTIVE) {
					beginPoint = point;
					break;
				}
			}
		}
		if (endPoint != null && beginPoint == null) {
			setMarkDescription(endPoint, port);
		}

		if (endPoint == null || beginPoint == null) {
			return;
		}
		String pointInterregional = DatasetManager.getInstance(mContext).getDatasetLib().getPointIndexZoneByGPSIndex(datasetHandle, port, index);
		if (!StringUtil.isNullOrEmpty(pointInterregional)) {
			String pointsIndex[] = pointInterregional.split("@@");
			int beginPointIndex = Integer.valueOf(pointsIndex[0]);
			int endPointIndex = Integer.valueOf(pointsIndex[1]);
			endPoint.setBeginPointIndex(beginPointIndex);
			endPoint.setEndPointIndex(endPointIndex);
			if (DatasetManager.isPlayback) {
				endPoint.setStatus(PointStatus.POINT_STATUS_EFFECTIVE);
			}
			LogUtil.d(TAG, "---addEventBetweenMark beginPointIndex:" + beginPointIndex + "--endPointIndex:" + endPointIndex);
			setMarkDescription(endPoint, port);
			Queue<AlarmModel> queue = new LinkedBlockingQueue<>();
			List<Parameter> parameterList = mParameterSet.getCheckedParamertersByNet(MyPhoneState.getInstance().getCurrentNetForParam(mContext));
			for (int j = 0; j < parameterList.size(); j++) {
				String result = getRealParam(port, Integer.valueOf(parameterList.get(j).getId(), 16), beginPointIndex, endPointIndex, true, true);
				LogUtil.d(TAG, "param:" + parameterList.get(j).getShowName() + ",result:" + result);
				if (!StringUtil.isNullOrEmpty(result)) {
					String radomParams[] = result.split("@@");
					Queue<AlarmModel> q = getPoint(radomParams, beginPoint.getPoint().x + j * 10, beginPoint.getPoint().y,
							endPoint.getPoint().x + j * 10, endPoint.getPoint().y, parameterList.get(j), beginPointIndex,
							endPointIndex, j);
					// 为了顺序显示当前点，如果中间有插值点，则把最后一个GPS点的起始序号修正为插值点的最后一个点的结束点
					if (!q.isEmpty()) {
						AlarmModel alarm = (AlarmModel) q.toArray()[q.size() - 1];
						endPoint.setBeginPointIndex(alarm.getMapEvent().getEndPointIndex() + 1);
					}
					MapFactory.getMapData().getEventQueue().addAll(q);
					queue.addAll(q);
					/*
					 * endPoint.description = parameterList.get(j) .getShortName() + ":"
					 * + (Double.valueOf(radomParams[radomParams.length - 1]) != -9999 ?
					 * Double.valueOf(radomParams[radomParams.length - 1]) :
					 * mContext.getResources() .getString(R.string.map_invalid_value));
					 */
				}
			}
			List<AlarmModel> alarmList = AlertManager.getInstance(mContext).getMapAlarmByIndex(beginPointIndex,
					endPointIndex);
			setMapAlarmEvent(alarmList, port, beginPoint.getPoint().x, beginPoint.getPoint().y, endPoint.getPoint().x,
					endPoint.getPoint().y, ParameterSetting.getInstance().getMapParameter());
			queue.addAll(alarmList);
			if (MapFactory.getMapData().getPointStatusStack().size() > 1) {
				MapFactory.getMapData().getQueueStack().push(queue);
			}
			LinkedList<HistoryPoint> histories = MapFactory.getMapData().getHistoryList();
			if (!histories.isEmpty() && histories.getLast().getPointStatus().equals(endPoint)) {
				histories.removeLast();
				for (HistoryPoint hp : histories) {
					if (hp.getPointStatus().equals(endPoint)) {
						hp.setAlarmQueue(queue);
					}
				}
			} else {
				HistoryPoint hp = new HistoryPoint(endPoint, queue, HistoryPoint.HISTORY_ADD);
				histories.add(hp);
			}
		}
	}

	/**
	 * 设置标记点描述
	 *
	 * @param pointStatus
	 *          标记点
	 * @param port
	 *          端口号
	 */
	private void setMarkDescription(PointStatus pointStatus, int port) {
		if (pointStatus != null) {
			List<Parameter> parameterList = mParameterSet
					.getCheckedParamertersByNet(MyPhoneState.getInstance().getCurrentNetForParam(mContext));
			int pParamKeyList[] = new int[parameterList.size()];
			for (int i = 0; i < parameterList.size(); i++) {
				pParamKeyList[i] = Integer.valueOf(parameterList.get(i).getId(), 16);
			}

			String result = DatasetManager.getInstance(mContext).getDatasetLib().batchGetRealParam(datasetHandle, port, pParamKeyList, pParamKeyList.length,
					pointStatus.getEndPointIndex(), false);
			// LogUtil.w(TAG, "--arrlen7:" + pParamKeyList.length + "--V:" +
			// result);
			StringBuffer buffer = new StringBuffer("");
			if (!StringUtil.isNullOrEmpty(result)) {
				String radomParams[] = result.split("@@");
				for (int i = 0; i < radomParams.length; i++) {
					buffer.append(parameterList.get(i).getShowName() + ":" + ((Double.valueOf(radomParams[i]) != -9999)
							? Double.valueOf(radomParams[i]) : mContext.getResources().getString(R.string.map_invalid_value)));
					if (i != radomParams.length - 1) {
						buffer.append("\n");
					}
				}
			}
			pointStatus.setDescription(buffer.toString());
		}
	}

	/**
	 * 构造GPS信息对象,添加到队列<BR>
	 * [功能详细描述]
	 *
	 * @param gpsInfo
	 *          DateTime@@longitude@@latitude@@Altiutde@@Speed@@SatelliteNum@@GroundDegree
	 * @param index
	 *          序号
	 * @param port
	 *          端口号
	 * @param showMap
	 *          0、google地图 1、百度地图
	 * @param isFillGps
	 *          是否GPS补点操作
	 * @param isReplay
	 *          是否回放
	 */
	private void addGpsInfo(String[] gpsInfo, int index, int port, int showMap, boolean isFillGps, boolean isReplay) {
		MapEvent mapEvent = new MapEvent();

		try{
			double lng = Double.valueOf(gpsInfo[1]);
			double lat = Double.valueOf(gpsInfo[2]);
			if(ApplicationModel.getInstance().isInnsmapTest()){
				mapEvent.setEventTime(System.currentTimeMillis());
			}else if (gpsInfo[0].length() < 16) {
				long gpstime = new BigInteger(gpsInfo[0], 16).longValue() / 1000;
				mapEvent.setEventTime(gpstime);
			}
			// LogUtil.d(TAG, "--addGpsInfo --lat:" + gpsInfo[2] + "--lon:" +
			// gpsInfo[1]);

			if (!isFillGps) { // 当前非GPS补充点时,需要通过数据集获得开始结果采样点
				GeoPoint geopoint = new GeoPoint((int) (lat * 1e6), (int) (lng * 1e6));
				GpsInfo.getInstance().setLastGeoPoint(geopoint);

				mapEvent.setLatitude((float) lat);
				mapEvent.setLongitude((float) lng);

				String pointInterregional = DatasetManager.getInstance(mContext).getDatasetLib().getPointIndexZoneByGPSIndex(datasetHandle, port, index);
				if (!StringUtil.isNullOrEmpty(pointInterregional)) {
					String pointsIndex[] = pointInterregional.split("@@");
					mapEvent.setBeginPointIndex(Integer.valueOf(pointsIndex[0]));
					mapEvent.setEndPointIndex(Integer.valueOf(pointsIndex[1]));
				}
			} else {
				mapEvent.setLatitude((float) lat);
				mapEvent.setLongitude((float) lng);
				mapEvent.setBeginPointIndex(Integer.valueOf(gpsInfo[7]));
				mapEvent.setEndPointIndex(Integer.valueOf(gpsInfo[7]));
			}
			// LogUtil.d(TAG, "--addGpsInfo --lat:" + lat + "--lng:" + lng +
			// "--beginIndex:" + mapEvent.getBeginPointIndex()
			// + "--fill:" + isFillGps);

			List<Parameter> parameterList = mParameterSet.getCheckedParamerters();
			int paramKeyList[] = new int[parameterList.size()];
			for (int j = 0; j < parameterList.size(); j++) {
				paramKeyList[j] = Integer.valueOf(parameterList.get(j).getId(), 16);
			}
			String valueString = DatasetManager.getInstance(mContext).getDatasetLib().batchGetRealParam(datasetHandle, port, paramKeyList, paramKeyList.length,
					mapEvent.getBeginPointIndex(), false);
			// LogUtil.d(TAG, "--addGpsInfo --valueString:" + valueString);

			if (!StringUtil.isNullOrEmpty(valueString)) {
				String values[] = valueString.split("@@");
				for (int j = 0; j < parameterList.size(); j++) {
					LocusParamInfo locusParamInfo = new LocusParamInfo();
					locusParamInfo.paramName = parameterList.get(j).getShowName();
					locusParamInfo.value = Double.valueOf(values[j]);
					locusParamInfo.color = mParameterSet.getGpsEventColor(mContext, parameterList.get(j), locusParamInfo.value);
					mapEvent.getParamInfoMap().put(parameterList.get(j).getShowName(), locusParamInfo);
				}
				//当是高铁，地铁的时候，不需要补点
				if (isReplay||ApplicationModel.getInstance().getSelectScene()== WalkStruct.SceneType.Metro||ApplicationModel.getInstance().getSelectScene()== WalkStruct.SceneType.HighSpeedRail)
				{ // 当非GPS补充点时需要判断当前是否需要补点

				}else {
					fillingGpsInfo(mapEvent, index, port, showMap, isReplay);
				}
				setGPSMapEvent(mapEvent, port);
				setAlarmModelMapEvent(mapEvent, port);
				// this.setBaseStationCoverParam(mapEvent, port,
				// mapEvent.getBeginPointIndex());
				TraceInfoInterface.traceData.addGpsLocas(mapEvent);
			}

			if (!isFillGps && !isReplay) { // 非GPS补充点需要做覆盖率统计,当回放时不做统计处理
				totalConverageQuality(gpsInfo, port);
			}

		}catch(Exception e){
			LogUtil.w(TAG, "addGpsInfo",e);
		}
		// LogUtil.d(TAG, "--addGpsInfo --End");
	}

	/**
	 * 超过几秒,在且在两分钟之前,两个GPS点之前需要补充点 1.计算两个时间之间差的秒数为需要补的点数
	 * 2.两个经度相减,两个纬度相减,得出的值按点数均分,计算完作为每个点的经纬度
	 * 3.两个点之前的初始采样点相减,得出的值除以补点个数,作为步长,用于取得每个补充GSP点对应的参数值 将当前点ADD添加GPS列表队列中
	 */
	private void fillingGpsInfo(MapEvent mapEvent, int index, int port, int showMap, boolean isReplay) {
		if (moduleInfo.lastMapEvent == null) {
			moduleInfo.lastMapEvent = mapEvent;
		} else {
			LogUtil.d(TAG, "-----fillingGpsInfo-----");
			long interval = mapEvent.getEventTime() - moduleInfo.lastMapEvent.getEventTime();
			/*
			* !(appModel.isGpsTest()&&appModel.isInOutSwitchMode())
			* 如果是CQT与DT之间转换时，就不要补点了,否则室外最后一个点与GPS第一个点就连在一起的。
			* */
			if (interval > moduleInfo.fillGpsInterval && interval < moduleInfo.fillGpsLimitTime && !(appModel.isGpsTest()&&appModel.isInOutSwitchMode())) {
				int intervalTimes = (int) interval / 1000; // 两点之前间隔秒数

				double intervalLong = (mapEvent.getLongitude() - moduleInfo.lastMapEvent.getLongitude()) / intervalTimes;
				double intervalLat = (mapEvent.getLatitude() - moduleInfo.lastMapEvent.getLatitude()) / intervalTimes;
				int intervalIndex = (mapEvent.getEndPointIndex() - moduleInfo.lastMapEvent.getBeginPointIndex())
						/ intervalTimes;

				for (int i = 1; i <= intervalTimes; i++) {
					String[] gpsInfo = new String[8];
					gpsInfo[0] = String.valueOf(moduleInfo.lastMapEvent.getEventTime() + 1000 * i);
					gpsInfo[1] = String.valueOf(moduleInfo.lastMapEvent.getLongitude() + intervalLong * i);
					gpsInfo[2] = String.valueOf(moduleInfo.lastMapEvent.getLatitude() + intervalLat * i);
					gpsInfo[7] = String.valueOf(moduleInfo.lastMapEvent.getBeginPointIndex() + intervalIndex * i);

					addGpsInfo(gpsInfo, index, port, showMap, true, isReplay);
				}
			}

			moduleInfo.lastMapEvent = mapEvent;
		}
	}

	/**
	 * 设置基站参数值
	 *
	 * @param value
	 *          参数值
	 * @param paramName
	 *          参数名
	 * @param mapEvent
	 *          事件点
	 */
	private void setStationParamMap(String value, String paramName, MapEvent mapEvent) {
		if (value.length() == 0)
			return;
		mapEvent.getStationParamMap().put(paramName, value.toString());
	}

	/**
	 * 统计里程覆盖质量
	 *
	 * [功能详细描述]
	 *
	 * @param gpsInfo
	 *          DateTime@@longitude@@latitude@@Altiutde@@Speed@@SatelliteNum@@GroundDegree
	 * @param port
	 */
	private void totalConverageQuality(String[] gpsInfo, int port) {
		LogUtil.d(TAG, "----totalConverageQuality--start--");
		int formIndex = moduleInfo.lastGPSIndex;
		int toIndex = getTotalPointCount(port) - 1;
		long gpstime = new BigInteger(gpsInfo[0], 16).longValue() / 1000;
		LogUtil.d(TAG, "----totalConverageQuality lastGpsIndex:" + formIndex + "--lastTime:" + moduleInfo.lastGpsTimes + "--TotalPoint:" + toIndex);
		try{
			// 1.获得两经纬度之前的距离，单位KM
			double midTestMileage = TraceInfoInterface.traceData.theLocationChange(Double.valueOf(gpsInfo[1]),
					Double.valueOf(gpsInfo[2]));

			//只有上个GPS采样点存在,时间存在,且与当前时间的比小于五分钟,否则不参与覆盖率运算
			if(moduleInfo.lastGPSIndex != -1 && moduleInfo.lastGpsTimes != -1
					&& (gpstime - moduleInfo.lastGpsTimes < 300 * 1000)){
				// 2.获得当前网络类型
				String currentNet = TraceInfoInterface.getRealParaValue(UnifyParaID.CURRENT_NETWORKTYPE);
				int current = 0x20;
				if (!currentNet.equals("")) {
					try {
						current = Double.valueOf(currentNet).intValue();
					} catch (Exception e) {
						e.printStackTrace();
						current = 0x20;
					}
				}
				LogUtil.d(TAG,"----totalConverageQuality currentNet:" + current);

				// 3.根据当前网络类型，查询当前网络类型下指定参数指定采样点区域的所有继承值,不过滤无效值的结果
				paraMap = new HashMap<>();
				switch (current) {
				case UnifyParaID.NET_TDSCDMA:
					String tdRscpValue = getRealParam(port, UnifyParaID.TD_Ser_PCCPCHRSCP, formIndex, toIndex, false, false);
					String tdc2iValue = getRealParam(port, UnifyParaID.TD_Ser_PCCPCHC2I, formIndex, toIndex, false, false);
					LogUtil.d(TAG,"----totalConverageQuality TD V1:" + tdRscpValue + "--V2:" + tdc2iValue);
					float tdfulfil = getFulfilRate(tdRscpValue, TD_Cover_Rate_RSCP, tdc2iValue, TD_Cover_Rate_CtoI);
					if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
							&& appModel.getCurrentTask() == TaskType.InitiativeCall) {
						paraMap.put(TotalDial._moTdCoverMileage.name(), (long) (midTestMileage * 10000 * tdfulfil));
						paraMap.put(TotalDial._moTdTotalMileage.name(), (long) (midTestMileage * 10000));
					} else if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
							&& appModel.getCurrentTask() == TaskType.PassivityCall) {
						paraMap.put(TotalDial._mtTdCoverMileage.name(), (long) (midTestMileage * 10000 * tdfulfil));
						paraMap.put(TotalDial._mtTdTotalMileage.name(), (long) (midTestMileage * 10000));
					} else {
						paraMap.put(TotalDial._TdCoverMileage.name(), (long) (midTestMileage * 10000 * tdfulfil));
						paraMap.put(TotalDial._TdTotalMileage.name(), (long) (midTestMileage * 10000));
					}

					break;
				case UnifyParaID.NET_LTE:
					String lrsrp = getRealParam(port, UnifyParaID.L_SRV_RSRP, formIndex, toIndex, false, false);
					String lsinr = getRealParam(port, UnifyParaID.L_SRV_SINR, formIndex, toIndex, false, false);
					LogUtil.d(TAG,"----totalConverageQuality LTE V1:" + lrsrp + "--V2:" + lsinr);
					float lfulfil = getFulfilRate(lrsrp, LTE_Cover_Rate_RSRP, lsinr, LTE_Cover_Rate_SINR);
					paraMap.put(TotalDial._LteCoverMileage.name(), (long) (midTestMileage * 10000 * lfulfil));
					paraMap.put(TotalDial._LteTotalMileage.name(), (long) (midTestMileage * 10000));

					break;
				case UnifyParaID.NET_GSM:
					String grxlevSub = getRealParam(port, UnifyParaID.G_Ser_RxLevSub, formIndex, toIndex, false, false);
					String gbcchlev = getRealParam(port, UnifyParaID.G_Ser_BCCHLev, formIndex, toIndex, false, false);
					LogUtil.d(TAG,"----totalConverageQuality GSM V1:" + grxlevSub + "--V2:" + gbcchlev);
					float gfulfil = getGfulfilRate(grxlevSub, GSM_Cover_Rate_RxlevSub, gbcchlev, GSM_Cover_Rate_BcchLev);
					if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
							&& appModel.getCurrentTask() == TaskType.InitiativeCall) {
						paraMap.put(TotalDial._moGsmCoverMileage.name(), (long) (midTestMileage * 10000 * gfulfil));
						paraMap.put(TotalDial._moGsmTotalMileage.name(), (long) (midTestMileage * 10000));
					} else if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
							&& appModel.getCurrentTask() == TaskType.PassivityCall) {
						paraMap.put(TotalDial._mtGsmCoverMileage.name(), (long) (midTestMileage * 10000 * gfulfil));
						paraMap.put(TotalDial._mtGsmTotalMileage.name(), (long) (midTestMileage * 10000));
					} else {
						paraMap.put(TotalDial._GsmCoverMileage.name(), (long) (midTestMileage * 10000 * gfulfil));
						paraMap.put(TotalDial._GsmTotalMileage.name(), (long) (midTestMileage * 10000));
					}
					break;
				/*
				 * case UnifyParaID.NET_WCDMA: break;
				 */
				default:
					break;
				}
				TotalDataByGSM.getInstance().updateTotalPara(paraMap.entrySet().iterator());
			}else{
				LogUtil.d(TAG, "----totalConverageQuality the Data Disable--");
			}

			moduleInfo.lastGPSIndex = toIndex;
			moduleInfo.lastGpsTimes = gpstime;
		}catch(Exception e){
			LogUtil.w(TAG, "lastGpsTimes",e);
			moduleInfo.lastGpsTimes = -1;
		}
		LogUtil.d(TAG, "----totalConverageQuality--end--");
	}

	/**
	 * 获得GSM的满足指定参数个数比 GSM的比较特殊，第一个参数指定位置的值无效时，取第二个参数相应位置的值
	 *
	 * @param values1
	 *          RxLevSub
	 * @param fulfil1
	 *          >=-90
	 * @param values2
	 *          BCCH LEV
	 * @param fulfil2
	 *          >=-90
	 * @return
	 */
	private float getGfulfilRate(String values1, float fulfil1, String values2, float fulfil2) {
		float fulfilRate = 0.0f;
		try {
			if (values1 != null && values2 != null) {
				String[] v1 = values1.split("@@");
				String[] v2 = values2.split("@@");
				if (v1.length == v2.length) {
					float fulfilCount = 0;
					for (int i = 0; i < v1.length; i++) {
						if (Float.parseFloat(v1[i]) != -9999) {
							if (Float.parseFloat(v1[i]) >= fulfil1) {
								fulfilCount++;
							}
						} else if (Float.parseFloat(v2[i]) >= fulfil2) {
							fulfilCount++;
						}
					}

					fulfilRate = fulfilCount / (v1.length * 1f);
					LogUtil.w(TAG, "--getGfulfilRate:" + fulfilRate);
				} else {
					LogUtil.w(TAG, "--gv1:" + values1);
					LogUtil.w(TAG, "--gv2:" + values2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.w(TAG, "--gv1:" + values1);
			LogUtil.w(TAG, "--gv2:" + values2);
			fulfilRate = 0.0f;
		}
		return fulfilRate;
	}

	/**
	 * 获回查询结果列表中，满足指定大小的个数比 当仅有一个参数进行比较时，第二个参数允许为空
	 *
	 * @param values1
	 *          第一个参数的结果列表
	 * @param fulfil1
	 *          第一个参数满足的大小值
	 * @param values2
	 *          第二个参数的结果列表
	 * @param fulfil2
	 *          第二个参数满足的大小值
	 * @return
	 */
	private float getFulfilRate(String values1, float fulfil1, String values2, float fulfil2) {
		float fulfilRate = 0.0f;
		try {
			String[] v1 = StringUtil.isNullOrEmpty(values1) ? new String[] {} : values1.split("@@");
			String[] v2 = StringUtil.isNullOrEmpty(values2) ? new String[] {} : values2.split("@@");
			if (v1.length == v2.length) {
				float fulfilCount = 0;
				for (int i = 0; i < v1.length; i++) {
					if (Float.parseFloat(v1[i]) >= fulfil1 && Float.parseFloat(v2[i]) >= fulfil2) {
						fulfilCount += 1;
					}
				}

				fulfilRate = fulfilCount / (v1.length * 1f);
				LogUtil.w(TAG, "--getFulfilRate:" + fulfilRate);
			} else {
				LogUtil.w(TAG, "--v1:" + values1);
				LogUtil.w(TAG, "--v2:" + values2);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.w(TAG, "--v1:" + values1);
			LogUtil.w(TAG, "--v2:" + values2);

			fulfilRate = 0.0f;
		}
		return fulfilRate;
	}

	/**
	 * 重建地图GPS点Event信息<BR>
	 * [功能详细描述]
	 *
	 * @param parameter
	 * @param port
	 */
	public void reBuildGpsInfo(Parameter parameter, int port) {
		List<MapEvent> mapEvents = TraceInfoInterface.traceData.getGpsLocas();
		for (int i = mapEvents.size() - 1; i >= 0; i--) {
			MapEvent mapEvent = mapEvents.get(i);
			String valueString = DatasetManager.getInstance(mContext).getDatasetLib().batchGetRealParam(datasetHandle, port,
					new int[] { Integer.valueOf(parameter.getId(), 16) }, 1, mapEvent.getBeginPointIndex(), false);
			if (!StringUtil.isNullOrEmpty(valueString)) {
				String values[] = valueString.split("@@");
				LocusParamInfo locusParamInfo = new LocusParamInfo();
				locusParamInfo.paramName = parameter.getShowName();
				locusParamInfo.value = Double.valueOf(values[0]);
				locusParamInfo.color = mParameterSet.getGpsEventColor(mContext, parameter, locusParamInfo.value);
				mapEvent.getParamInfoMap().put(parameter.getShowName(), locusParamInfo);
			}
		}
	}

	/**
	 * 设置GPS点的MapEvent<BR>
	 * 地图绘制需要使用MapEvent对象
	 *
	 * @param mapEvent
	 * @param port
	 */
	private void setGPSMapEvent(MapEvent mapEvent, int port) {
		String netTypeS = getRealParam(port, UnifyParaID.CURRENT_NETWORKTYPE, mapEvent.getBeginPointIndex(), mapEvent.getBeginPointIndex(), false, true);
		if (netTypeS.length() == 0 || !UtilsMethod.isNumeric(netTypeS))
			return;
		int netType = Double.valueOf(netTypeS).intValue();
		int paramKeys[] = AlertManager.getCurrentParams(netType);
		String paramValues = DatasetManager.getInstance(mContext).getDatasetLib().batchGetRealParam(datasetHandle, port, paramKeys, paramKeys.length,
				mapEvent.getBeginPointIndex(), false);
		if (paramValues == null)
			return;
		String info = Util
				.formatGeoPoint(new GeoPoint((int) (mapEvent.getLatitude() * 1e6), (int) (mapEvent.getLongitude() * 1e6)));
		mapEvent.setMapPopInfo(info + "\n" + AlertManager.genMapPopInfo(netType, paramValues));
		String[] values = paramValues.split("@@");
		switch (netType) {
		case UnifyParaID.NET_GSM:
			this.setStationParamMap(String.valueOf(Double.valueOf(values[1]).intValue()), "BS_BCCH", mapEvent);
			this.setStationParamMap(String.valueOf(Double.valueOf(values[2]).intValue()), "BS_BSIC", mapEvent);
			break;
		case UnifyParaID.NET_WCDMA:
			this.setStationParamMap(String.valueOf(Double.valueOf(values[1]).intValue()), "BS_UARFCN", mapEvent);
			this.setStationParamMap(String.valueOf(Double.valueOf(values[2]).intValue()), "BS_PSC", mapEvent);
			break;
		case UnifyParaID.NET_LTE:
			this.setStationParamMap(String.valueOf(Double.valueOf(values[0]).intValue()), "BS_EARFCN", mapEvent);
			this.setStationParamMap(String.valueOf(Double.valueOf(values[1]).intValue()), "BS_PCI", mapEvent);
			break;
		case UnifyParaID.NET_CDMA_EVDO:
			this.setStationParamMap(String.valueOf(Double.valueOf(values[0]).intValue()), "BS_FREQ", mapEvent);
			this.setStationParamMap(String.valueOf(Double.valueOf(values[1]).intValue()), "BS_PN", mapEvent);
			break;
		case UnifyParaID.NET_TDSCDMA:
			this.setStationParamMap(String.valueOf(Double.valueOf(values[0]).intValue()), "BS_UARFCN", mapEvent);
			this.setStationParamMap(String.valueOf(Double.valueOf(values[1]).intValue()), "BS_CPI", mapEvent);
			break;
		}
	}

	/**
	 * 设置告警对象的MapEvent<BR>
	 * 地图绘制需要使用MapEvent对象
	 *
	 * @param mapEvent
	 * @param port
	 */
	private void setAlarmModelMapEvent(MapEvent mapEvent, int port) {
		List<AlarmModel> alarmList = AlertManager.getInstance(mContext).getMapAlarmByIndex(mapEvent.getBeginPointIndex(),
				mapEvent.getEndPointIndex());
		for (AlarmModel aModel : alarmList) {
			aModel.setMapEvent(mapEvent);
			int netType = Double.valueOf(getRealParam(port, UnifyParaID.CURRENT_NETWORKTYPE, aModel.getMsgIndex(), aModel.getMsgIndex(), false, true)).intValue();
			int paramKeys[] = AlertManager.getCurrentParams(netType);
			String paramValues = DatasetManager.getInstance(mContext).getDatasetLib().batchGetRealParam(datasetHandle, port, paramKeys, paramKeys.length, aModel.getMsgIndex(), false);

			// LogUtil.w(TAG, "--arrlen9:" + paramKeys.length + "--V:" +
			// paramValues);
			aModel.setMapPopInfo(AlertManager.genMapPopInfo(netType, paramValues));
		}
	}

	/**
	 * 获取两次打点间的事件点<BR>
	 * [功能详细描述]
	 *
	 * @param randomParams
	 *          参数数组
	 * @param startX
	 *          开始点X坐标
	 * @param startY
	 *          开始点Y坐标
	 * @param endX
	 *          结束点X坐标
	 * @param endY
	 *          结束点Y坐标
	 * @param parameter
	 *          参数
	 * @param startIndex
	 *          上一个GPS点或者Mark点的起始序号
	 * @param endIndex
	 *          上一个GPS点或者Mark点的结束序号
	 * @param paramIndex
	 *          参数序号
	 * @return 事件点队列
	 */
	private Queue<AlarmModel> getPoint(String[] randomParams, double startX, double startY, double endX, double endY,
			Parameter parameter, int startIndex, int endIndex, int paramIndex) {
		Queue<AlarmModel> eventQueue = new LinkedBlockingQueue<AlarmModel>();
		int scale = 10;
		// int step = randomParams.length/20 > 1 ? randomParams.length/20 : 1;
		// twq20131205广府建议把这此调少一点
		int step = randomParams.length / scale > 1 ? randomParams.length / scale : 1;
		for (int i = 0; i < randomParams.length; i += step) {
			if (randomParams[i] != null) {
				AlarmModel alarmModel = new AlarmModel();
				alarmModel.setParamIndex(paramIndex);
				MapEvent mapEvent = new MapEvent();
				mapEvent.setValue(Double.parseDouble(randomParams[i]));
				alarmModel.setMapEvent(mapEvent);
				eventQueue.add(alarmModel);
			}
		}

		int count = eventQueue.size();
		float xOffset = (float) (endX - startX) / (count + 1);
		float yOffset = (float) (endY - startY) / (count + 1);
		int indexOffset = (endIndex - startIndex) / (count + 1);
		float addx = 0;
		float addy = 0;
		int addIndex = 0;
		for (AlarmModel alarmModel : eventQueue) {
			MapEvent event = alarmModel.getMapEvent();
			addx += xOffset;
			addy += yOffset;
			addIndex += indexOffset;
			event.setLongitude(startX + addx);
			event.setLatitude(startY + addy);
			event.setBeginPointIndex(startIndex + addIndex - indexOffset);
			event.setEndPointIndex(startIndex + addIndex);
			// LogUtil.d(TAG, "----- e.longitude=" + event.getLongitude()
			// + " e.latitude=" + event.getLatitude());
			event.setParamName(parameter.getShowName());
			if (event.getStatus() == 0) {
				event.setColor(ParameterSetting.getInstance().getGpsEventColor(mContext, parameter, event.getValue()));
			} else {
				event.setColor(ParameterSetting.getInstance().getGpsColor());
			}
		}
		return eventQueue;
	}

	/**
	 * 如果当前ID在参数画图队列中 取该队列的阀值设定信息
	 *
	 * @param id
	 * @param value
	 */
	private void setDrawPara(int id, int value) {
		List<ParamItem> itemList = paramDrawList.get(id).paramItemList;
		HashMap<String, Long> paraMap = new HashMap<String, Long>();

		for (int j = 0; j < itemList.size(); j++) {
			if (j == 0) {
				if (value <= itemList.get(j).value) {
					paraMap.put(itemList.get(j).itemname, 1l);
					break;
				}
			} else if (j == itemList.size() - 1) {
				if (value > itemList.get(j - 1).value) {
					paraMap.put(itemList.get(j).itemname, 1l);
					break;
				}
			} else {
				if (value > itemList.get(j - 1).value && value <= itemList.get(j).value) {
					paraMap.put(itemList.get(j).itemname, 1l);
					break;
				}
			}
		}
		paraMap.put(paramDrawList.get(id).paramName, 1l);
		TotalDataByGSM.getInstance().updateTotalPara(paraMap.entrySet().iterator());
	}

	private HashMap<String, Long> paraMap = null;

	/** 当前参数用于记录语音测试开始时的时间，用于后面的时长分网络统计 */
	private long netTimeLongStart = 0;

	/**
	 * 统计语音通话质量
	 *
	 * @param id
	 * @param value
	 */
	private void totalCallQuality(int id, int value) {
		switch (id) {
		case UnifyParaID.G_Ser_BCCHLev:
			// 用于GSM语音统计指标
			paraMap = new HashMap<String, Long>();
			if (value >= -94) {
				if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
						&& appModel.getCurrentTask() == TaskType.PassivityCall) {
					paraMap.put(TotalDial._mtRxLev1s.name(), 1l);
				} else if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
						&& appModel.getCurrentTask() == TaskType.InitiativeCall) {
					paraMap.put(TotalDial._moRxLev1s.name(), 1l);
				}
			}
			if (value >= -90) {
				if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
						&& appModel.getCurrentTask() == TaskType.PassivityCall) {
					paraMap.put(TotalDial._mtRxLev2s.name(), 1l);
				} else if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
						&& appModel.getCurrentTask() == TaskType.InitiativeCall) {
					paraMap.put(TotalDial._moRxLev2s.name(), 1l);
				}
			}
			if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
					&& appModel.getCurrentTask() == TaskType.PassivityCall) {
				paraMap.put(TotalDial._mtTotalRxLevs.name(), 1l);
			} else if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
					&& appModel.getCurrentTask() == TaskType.InitiativeCall) {
				paraMap.put(TotalDial._moTotalRxLevs.name(), 1l);
			}

			TotalDataByGSM.getInstance().updateTotalPara(paraMap.entrySet().iterator());
			break;
		case UnifyParaID.G_Ser_RxQualSub:
			// 用于GSM语音统计指标
			paraMap = new HashMap<String, Long>();
			if (value >= 0 && value <= 2) {
				if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
						&& appModel.getCurrentTask() == TaskType.PassivityCall) {
					paraMap.put(TotalDial._mtRxQual1s.name(), 1l);
				} else if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
						&& appModel.getCurrentTask() == TaskType.InitiativeCall) {
					paraMap.put(TotalDial._moRxQual1s.name(), 1l);
				}
			} else if (value >= 3 && value <= 5) {
				if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
						&& appModel.getCurrentTask() == TaskType.PassivityCall) {
					paraMap.put(TotalDial._mtRxQual2s.name(), 1l);
				} else if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
						&& appModel.getCurrentTask() == TaskType.InitiativeCall) {
					paraMap.put(TotalDial._moRxQual2s.name(), 1l);
				}
			}

			if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
					&& appModel.getCurrentTask() == TaskType.PassivityCall) {
				paraMap.put(TotalDial._mtTotalRxQuals.name(), 1l);
			} else if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
					&& appModel.getCurrentTask() == TaskType.InitiativeCall) {
				paraMap.put(TotalDial._moTotalRxQuals.name(), 1l);
			}
			TotalDataByGSM.getInstance().updateTotalPara(paraMap.entrySet().iterator());
			break;
		case UnifyParaID.TD_Ser_BLER:
			// TD网络下的话音覆盖率
			paraMap = new HashMap<String, Long>();
			if (value >= 0 && value <= 3) {
				if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
						&& appModel.getCurrentTask() == TaskType.PassivityCall) {
					paraMap.put(TotalDial._mtTDBler03s.name(), 1l);
				} else if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
						&& appModel.getCurrentTask() == TaskType.InitiativeCall) {
					paraMap.put(TotalDial._moTDBler03s.name(), 1l);
				}
			}

			if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
					&& appModel.getCurrentTask() == TaskType.PassivityCall) {
				paraMap.put(TotalDial._mtTDBlerCount.name(), 1l);
			} else if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
					&& appModel.getCurrentTask() == TaskType.InitiativeCall) {
				paraMap.put(TotalDial._moTDBlerCount.name(), 1l);
			}
			TotalDataByGSM.getInstance().updateTotalPara(paraMap.entrySet().iterator());
			break;
		case UnifyParaID.CURRENT_NETWORKTYPE:
			// 统计统计状态下，各网络所所占用的时长
			if (netTimeLongStart == 0) {
				netTimeLongStart = System.currentTimeMillis();
			} else {
				long subLong = (int) (System.currentTimeMillis() - netTimeLongStart);
				netTimeLongStart = System.currentTimeMillis();

				paraMap = new HashMap<String, Long>();

				// 此流程为每秒刷新一次，如果当前相隔时间大于5秒，说明为上一次通话结束到此次的通话开始时间，此段时间需要在此处简单丢掉，不参与累计
				if (subLong >= 5000) {
					LogUtil.w(TAG, "--call task internal times--" + subLong);
				} else {
					/**
					 * 当前网络GSM = 0x01,WCDMA = 0x02,TD-SCDMA = 0x04,CDMA\EVDO = 0x08,LTE =
					 * 0x10,Unknown = 0x20,NoService = 0x80
					 */
					paraMap = new HashMap<String, Long>();
					if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
							&& appModel.getCurrentTask() == TaskType.PassivityCall) {
						switch (value) {
						case UnifyParaID.NET_GSM:
							paraMap.put(TotalDial._mtTimeLongGSM.name(), subLong);
							break;
						case UnifyParaID.NET_WCDMA:
							paraMap.put(TotalDial._mtTimeLongWCDMA.name(), subLong);
							break;
						case UnifyParaID.NET_TDSCDMA:
							paraMap.put(TotalDial._mtTimeLongTD.name(), subLong);
							break;
						case UnifyParaID.NET_CDMA_EVDO:
							paraMap.put(TotalDial._mtTimeLongCDMA.name(), subLong);
							break;
						/*
						 * case 0x10: paraMap.put(TotalDial._mtTimeLongLTE.name(), subLong);
						 * break;
						 */
						default:
							paraMap.put(TotalDial._mtTimeLongUnknown.name(), subLong);
							break;
						}
					} else if (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
							&& appModel.getCurrentTask() == TaskType.InitiativeCall) {
						switch (value) {
						case UnifyParaID.NET_GSM:
							paraMap.put(TotalDial._moTimeLongGSM.name(), subLong);
							break;
						case UnifyParaID.NET_WCDMA:
							paraMap.put(TotalDial._moTimeLongWCDMA.name(), subLong);
							break;
						case UnifyParaID.NET_TDSCDMA:
							paraMap.put(TotalDial._moTimeLongTD.name(), subLong);
							break;
						case UnifyParaID.NET_CDMA_EVDO:
							paraMap.put(TotalDial._moTimeLongCDMA.name(), subLong);
							break;
						/*
						 * case 0x10: paraMap.put(TotalDial._moTimeLongLTE.name(), subLong);
						 * break;
						 */
						default:
							paraMap.put(TotalDial._moTimeLongUnknown.name(), subLong);
							break;
						}
					}

					/*
					 * //LTE下不支持语音通话 if(value == UnifyParaID.NET_LTE){
					 * paraMap.put(TotalDial._TimeLongLTE.name(), subLong); }
					 *
					 * TotalDataByGSM.getInstance().updateTotalPara(paraMap.entrySet
					 * ().iterator());
					 */
				}

				// LTE下不支持语音通话，故此处的有可能有超时五秒不计入LTE的情况，放if外面统计LTE的总时长
				if (value == UnifyParaID.NET_LTE) {
					paraMap.put(TotalDial._TimeLongLTE.name(), subLong);
				}

				TotalDataByGSM.getInstance().updateTotalPara(paraMap.entrySet().iterator());
			}
			break;
		case 0x7F03011B:

			break;
		}
	}

	/**
	 * 多参数联合统计处理类,该类用于处理TD，LTE的覆盖率
	 *
	 * @param keyId
	 * @param mainValues
	 * @param assistValues
	 */
	private void updateTotalKeyValue(int keyId, String mainValues, LinkedHashMap<Integer, Long> assistValues) {
		// for(int i=0; i < mainValues.length; i++){
		int mainValue = (int) Float.parseFloat(mainValues);
		long assistValue = 0;
		boolean isCall = (MyPhoneState.getCallState() != TelephonyManager.CALL_STATE_IDLE
				&& (appModel.getCurrentTask() == TaskType.InitiativeCall
						|| (appModel.getCurrentTask() == TaskType.PassivityCall)));
		boolean isMoCall = (appModel.getCurrentTask() == TaskType.InitiativeCall);

		switch (keyId) {
		case UnifyParaID.TD_Ser_PCCPCHRSCP:
			if (isCall) {
				paraMap = new HashMap<String, Long>();
				if (mainValue >= TD_Cover_Rate_RSCP) {
					assistValue = getAssistValues(assistValues, UnifyParaID.TD_Ser_PCCPCHC2I);
					if (assistValue != -9999 && assistValue >= TD_Cover_Rate_CtoI) {
						paraMap.put(isMoCall ? TotalDial._moPccpchRscp1.name() : TotalDial._mtPccpchRscp1.name(), 1l);
					}
				}
				if (mainValue >= -85) {
					assistValue = getAssistValues(assistValues, UnifyParaID.TD_Ser_PCCPCHC2I);
					if (assistValue != -9999 && assistValue >= 0) {
						paraMap.put(isMoCall ? TotalDial._moPccpchRscp2.name() : TotalDial._mtPccpchRscp2.name(), 1l);
					}
				}
				paraMap.put(isMoCall ? TotalDial._moPccpchRscpCount.name() : TotalDial._mtPccpchRscpCount.name(), 1l);
				TotalDataByGSM.getInstance().updateTotalPara(paraMap.entrySet().iterator());
			}
			break;
		case UnifyParaID.L_SRV_RSRP:
			paraMap = new HashMap<String, Long>();
			if (mainValue >= LTE_Cover_Rate_RSRP) {
				assistValue = getAssistValues(assistValues, UnifyParaID.L_SRV_SINR);
				if (assistValue != -9999 && assistValue >= LTE_Cover_Rate_SINR) {
					paraMap.put(TotalDial._LTErsrp.name(), 1l);
				}
			}

			paraMap.put(TotalDial._LTErsrpCount.name(), 1l);
			TotalDataByGSM.getInstance().updateTotalPara(paraMap.entrySet().iterator());
			break;
		}
		// }
	}

	/**
	 * 获得辅助结果列表中，指定ID的辅助参数，对应序号的值 无效值返回 -9999
	 *
	 * @param assistValues
	 * @param assistKey
	 * @return
	 */
	private long getAssistValues(LinkedHashMap<Integer, Long> assistValues, int assistKey) {
		try {
			if (assistValues.containsKey(assistKey)) {
				return assistValues.get(assistKey);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -9999;
	}

	/**
	 * 自定义导出，多处使用
	 *
	 * @param port
	 * @param exportFileName
	 * @param headMsg
	 * @param iSplitSize
	 * @param config
	 * @param from
	 * @param to
	 */
	public void customExportFile(int port, ArrayList<String> exportFileName, ArrayList<String> headMsg, int iSplitSize,
			ArrayList<Integer> config, int from, int to, long[] jMsgIDs, int MsgIDCount) {

		// long ss = DatasetManager.getInstance(mContext).getDatasetLib().getPointTime(datasetHandle, port, 20, false,
		// true);
		// int timeToPoint = DatasetManager.getInstance(mContext).getDatasetLib().getPointIndexFromTime(datasetHandle,
		// port, 1378452937355000l);
		// int totalPointCount = DatasetManager.getInstance(mContext).getDatasetLib().getTotalPointCount(datasetHandle,
		// port);
		for (int i = 0; i < headMsg.size(); i++) {
			LogUtil.i(TAG, "custom" + "Come In");
			DatasetManager.getInstance(mContext).getDatasetLib().exportTable(datasetHandle, port, exportFileName.get(i), config.get(i), from, to, headMsg.get(i),
					iSplitSize, UtilsMethod.getExtensionName(exportFileName.get(i)).equals("csv") ? "," : "\t", jMsgIDs,
					MsgIDCount);
		}
	}

	/**
	 * 参数配置导出，为招标项避免影响OTS功能正常导出，新加接口与朱工库对应 zhihui.ian
	 */

	public void parmCustomExportFile(int port, String exportFilePath, int config, int fromPointIndex, int toPointIndex,
			int splitSize, long[] msgIDs, int msgIDCount, long[] paramIDs) {
		DatasetManager.getInstance(mContext).getDatasetLib().exportTableExt(datasetHandle, port, exportFilePath, config, fromPointIndex, toPointIndex, "", splitSize,
				UtilsMethod.getExtensionName(exportFilePath).equals("csv") ? "," : "\t", msgIDs, msgIDCount, paramIDs);
	}

	/**
	 * 构建参数分布页当前指定参数相关信息
	 *
	 * @param port
	 */
	public void buildDistributionParams(int port) {
		try {
            LogUtil.d(TAG,"----buildDistributionParams----start----");
			int fromIndex = moduleInfo.lastDistributionIndex;
			int toIndex = getTotalPointCount(port) - 1;
			moduleInfo.lastDistributionIndex = toIndex;

			Parameter[] params = mParameterSet.getDistributionParams();

			// 循环需要获取参数的列表
			for (int i = 0; i < params.length; i++) {
				Parameter param = params[i];
				buildDistributionByParam(param, port, fromIndex, toIndex);
			}
            LogUtil.d(TAG,"----buildDistributionParams----end----");
		} catch (Exception e) {
			LogUtil.w(TAG, "buildDistributionParams", e);
		}
	}

	/**
	 * 当分布参数切换时,传入当前切换的Parameter对象
	 *
	 * @param port
	 * @param param
	 */
	public void rebuildDistributionParams(int port, Parameter param) {
		int fromIndex = -1;
		int toIndex = moduleInfo.lastDistributionIndex;

		try {
			TraceInfoInterface.traceData.reSetDistributionData();
			buildDistributionByParam(param, port, fromIndex, toIndex);
		} catch (Exception e) {
			LogUtil.w(TAG, "rebuildDistributionParams", e);
		}
	}

	/** 重置统计参数信息 */
	public void resetTotalInfo() {
		netTimeLongStart = 0;
	}

	/**
	 * 根据传进来的参数信息,查询相对应的参数分布信息
	 *
	 * @param param
	 * @param port
	 * @param fromIndex
	 * @param toIndex
	 */
	private void buildDistributionByParam(Parameter param, int port, int fromIndex, int toIndex) {
		if (param != null) {
			ArrayList<Threshold> thresholdList = param.getThresholdList();

			int paramid = Integer.parseInt(param.getId(), 16);
			String distribuValud = getRealParam(port, paramid, fromIndex, toIndex, true, true);
			String[] distribus = (distribuValud != null ? distribuValud.split("@@") : new String[] {});

			// 循环取得当前参数的当前值进行相应处理
			for (int j = 0; j < distribus.length; j++) {
				String values = getRealValue(distribus[j]);
				if (!values.equals("")) {
					boolean bThres = false;
					int value = TypeConver.StringToInt(values);
					// 比较当前值所属的阀值范围,并做相关处理
					for (int t = 0; t < thresholdList.size(); t++) {
						// if (value <= thresholdList.get(t).getValue()) {
						if (thresholdList.get(t).getValueResult(value)) {
							TraceInfoInterface.traceData.addDistributionData(param.getId(), t + 1, 1);
							bThres = true;
							break;
						}
					}

					if (!bThres) {
						TraceInfoInterface.traceData.addDistributionData(param.getId(), thresholdList.size(), 1);
					}
				}
			}
		}
	}

	/**
	 * 获取scanner采样点上对应业务数据块数
	 *
	 * @return 块数
	 * @param iPointIndex
	 */
	public int getStructItemCount(int iPointIndex, int scanID, int port) {
		int itemCount = DatasetManager.getInstance(mContext).getDatasetLib().getStructItemCount(datasetHandle, port, iPointIndex, false, scanID);
		LogUtil.i(TAG, "Scanner Item Count  " + itemCount);
		return itemCount;
	}

	/**
	 * 获取scanner采样点上对应业务数据
	 *
	 * @return 块数
	 * @param iPointIndex
	 */
	public byte[] getStructItem(int iPointIndex, int scanID, int iItemIndex, int port) {
		byte[] structItem = DatasetManager.getInstance(mContext).getDatasetLib().getStructItem(datasetHandle, port, iPointIndex, false, scanID, iItemIndex);
		LogUtil.i(TAG, "Scanner Item byte  ");
		return structItem;
	}

}
