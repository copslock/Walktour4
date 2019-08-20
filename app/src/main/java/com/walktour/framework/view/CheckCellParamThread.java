package com.walktour.framework.view;

import android.content.Context;
import android.location.Location;

import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.database.BaseStationDBHelper;
import com.walktour.framework.database.model.BaseStationDetail;
import com.walktour.model.CellInfo;

import java.util.List;

/**
 * 获取小区参数线程
 * 
 * @author jianchao.wang
 *
 */
public class CheckCellParamThread extends Thread {
	private static final String TAG = "CheckCellParamThread";
	/** 查询参数 */
	private String[] queryParams;
	/** 查询条件 */
	private String condition;
	/** 网络类型 */
	private int networkType;
	/** 上下文 */
	private Context mContext;

	public CheckCellParamThread(Context context, String[] queryParams, String condition, int networkType) {
		this.queryParams = queryParams;
		this.condition = condition;
		this.networkType = networkType;
		this.mContext = context;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Location location = GpsInfo.getInstance().getLocation();
		if (location == null) {
			TraceInfoInterface.traceData.cleanCellIDHmKey();
			return;
		}
		List<BaseStationDetail> list = BaseStationDBHelper.getInstance(this.mContext).queryCellIDByFields(condition);
		for (BaseStationDetail detail : list) {
			if (detail.main.netType != this.networkType)
				continue;
			StringBuffer buffer = new StringBuffer();
			buffer.append(networkType);
			buffer.append("_").append(queryParams[0]).append("_").append(detail.getParamValue(queryParams[0]));
			buffer.append("_").append(queryParams[1]).append("_").append(detail.getParamValue(queryParams[1]));
			CellInfo cell = TraceInfoInterface.traceData.getNetworkCellInfo(buffer.toString());
			double distance = UtilsMethod.getDistance(detail.main.latitude, detail.main.longitude, location.getLatitude(),
					location.getLongitude());
			if (cell == null || cell.getDistance() > distance || cell.getDistance() == -1
					|| TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_Cell_ID).equals(detail.cellId)) {
				CellInfo cellInfo = new CellInfo(detail.cellName, detail.cellId, distance);
				TraceInfoInterface.traceData.setNetworkCellInfo(buffer.toString(), cellInfo);
				LogUtil.i(TAG, cell != null ? cell.toString() : "cell null " + "_____distance:" + distance);
				LogUtil.i(TAG, "cellKey:" + buffer.toString() + cellInfo.getCellName());
			}
		}
	}

}
