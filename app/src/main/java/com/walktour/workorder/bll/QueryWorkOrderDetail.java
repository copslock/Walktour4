package com.walktour.workorder.bll;

import android.content.Context;

import com.dinglicom.UnicomInterface;
import com.walktour.control.config.ServerManager;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.workorder.dal.IQueryHelper;
import com.walktour.workorder.dal.WorkOrderDetailHelper;
import com.walktour.workorder.model.ServerInfo;
import com.walktour.workorder.model.WorkOrderDetail;

import java.util.List;

/**
 * 工单详情的查询业务逻辑类
 * Author: ZhengLei
 *   Date: 2013-6-21 上午10:52:54
 */
public class QueryWorkOrderDetail {
	private Context context = null;
	private IQueryHelper qHelper = null;

	
	public QueryWorkOrderDetail(Context context) {
		this.context = context;
		qHelper = new WorkOrderDetailHelper();
	}
	
	/*start***********************************提供各种查询接口***********************************start*/
	public int getSubId(WorkOrderDetail detail, int position) {
		return detail.getWorkSubItems().get(position).getItemId();
	}
	
	public List<TaskModel> getTask(WorkOrderDetail detail, int subId) {
		return qHelper.getTask(detail, subId);
	}
	
	public int getLoopTime(WorkOrderDetail detail, int subId) {
		return qHelper.getLoopTime(detail, subId);
	}

	public int getLoopInterval(WorkOrderDetail detail, int subId) {
		return qHelper.getLoopInterval(detail, subId);
	}

	public String getBuilding(WorkOrderDetail detail) {
		return qHelper.getBuilding(detail);
	}

	public String getFloor(WorkOrderDetail detail, int subId) {
		return qHelper.getFloor(detail, subId);
	}

	public String getFloorMap(WorkOrderDetail detail, int subId) {
		return qHelper.getFloorMap(detail, subId);
	}

	public String getFloorMapUrl(WorkOrderDetail detail, int subId) {
		String baseUrl = getBaseUrl();
		if(baseUrl == null) return null;
		return baseUrl + qHelper.getFloorMap(detail, subId);
	}
	/*end***********************************提供各种查询接口***********************************end*/
	
	
	/**
	 * 从Sharereferences中获取Ftp的基地址
	 * @return 基地址，如ftp://219.216.103.8:21/
	 */
	private String getBaseUrl() {
		// 从Sharereferences中获取Ftp的地址
		ServerInfo info = ServerManager.getInstance(context).readUnicomServer(UnicomInterface.FTP_DOWNLOAD_SERVER);
		String url = null;
		if(info != null) {
			url = "ftp://" + info.getIpAddr() + ":" + info.getPort() + "/";
		}
		return url;
	}
}
