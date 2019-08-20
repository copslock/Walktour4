package com.walktour.service.app.datatrans.fleet;

import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.gui.workorder.ah.WorkOrderFactory;
import com.walktour.gui.workorder.ah.model.WorkOrder;
import com.walktour.gui.workorder.ah.model.WorkOrderPoint;
import com.walktour.service.app.DataTransService;

import java.util.HashSet;
import java.util.Set;

/**
 * Fleet服务器上传数据安徽工单项目用
 * 
 * @author jianchao.wang
 *
 */
public class FleetDataTransferAnhui extends FleetDataTransferBase {

	FleetDataTransferAnhui(DataTransService service) {
		super("FleetDataTransferAnhui", service);
	}

	@Override
	protected String getTag() {
		String buildName = super.mCurrentFile.getStringExtraParam("BuildName");
		if (buildName == null)
			return "";
		WorkOrder order = WorkOrderFactory.getInstance().getOrderByPointNo(buildName);
		if (order == null)
			return "";
		UploadRcuTag tag = new UploadRcuTag();
		tag.WorkItemCode = order.getWorkItemCode();
		WorkOrderPoint point = WorkOrderFactory.getInstance().getPointByPointNo(buildName);
		if (point.isCreate()) {
			tag.Info = point.getName();
			tag.TestPointID = "ExtraCqtPoint";
		} else {
			tag.Info = "";
			tag.TestPointID = point.getPointID();
		}
		tag.SpotDetail = super.mCurrentFile.getStringExtraParam("FloorName");
		StringBuilder sb = new StringBuilder();
		sb.append("{\"Protocol\":\"AHCTU\",");
		sb.append("\"FileType\":\"ORGRCU\",");
		sb.append("\"SourceDataType\":\"ICQT\",");
		sb.append("\"WorkItemCode\":\"").append(tag.WorkItemCode).append("\",");
		sb.append("\"TestPointID\":\"").append(tag.TestPointID).append("\",");
		sb.append("\"SpotDetail\":\"").append(tag.SpotDetail).append("\",");
		sb.append("\"Info\":\"").append(tag.Info).append("\"}");
		return sb.toString();
	}

	/**
	 * 上传的Rcu的Tag对象
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class UploadRcuTag {
		/** 工单号 */
		String WorkItemCode = "";
		/** 建筑物 */
		String TestPointID = "";
		/** 楼层 */
		String SpotDetail = "";
		/** 信息 */
		public String Info = "";
	}

	@Override
	protected void initCurrentFileTypes() {
		if (super.mCurrentFile.getFileTypes().length == 0) {
			Set<FileType> fileTypes = new HashSet<>();
			fileTypes.add(FileType.ORGRCU);
			super.mCurrentFile.setFileTypes(fileTypes);
		}
	}
}
