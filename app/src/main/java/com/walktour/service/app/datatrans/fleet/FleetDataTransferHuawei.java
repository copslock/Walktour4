package com.walktour.service.app.datatrans.fleet;

import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.gui.workorder.hw.WorkOrderFactory;
import com.walktour.gui.workorder.hw.model.TestSchema;
import com.walktour.service.app.DataTransService;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

/**
 * Fleet服务器上传数据
 * 
 * @author jianchao.wang
 *
 */
public class FleetDataTransferHuawei extends FleetDataTransferBase {

	/** 华为工单使用的测试计划 */
	private TestSchema mSchema;

	FleetDataTransferHuawei(DataTransService service) {
		super("FleetDataTransferHuawei", service);
	}

	/**
	 * 获取时区
	 * 
	 * @return 时区
	 */
	private String getTimeZone() {
		TimeZone tz = TimeZone.getDefault();
		int timeoffset = tz.getOffset(System.currentTimeMillis()) / 1000 / 60;
		StringBuilder sb = new StringBuilder();
		if (timeoffset > 0)
			sb.append("+");
		else
			sb.append("-");
		timeoffset = Math.abs(timeoffset);
		int hour;
		int min = 0;
		if (timeoffset % 60 == 0) {
			hour = timeoffset / 60;
		} else {
			min = timeoffset % 60;
			hour = (timeoffset - min) / 60;
		}
		sb.append(hour > 9 ? hour : "0" + hour);
		sb.append(min > 9 ? min : "0" + min);
		return sb.toString();
	}

	/**
	 * 格式化服务器端需要的文件名 华为工单专用,对于历史数据文件采用该处理方式
	 * 
	 * @return 文件名
	 */
	private String formatOldFileName() {
		String fileName = super.mCurrentFile.getName();
		System.out.println(TAG + " fileName:" + fileName);
		if (fileName.indexOf("_Port") > 0) {
			return fileName;
		}
		String prefix = "";
		int start = 0;
		if (fileName.indexOf("-IN") > 0) {
			start = fileName.indexOf("-IN") + 3;
			prefix = fileName.substring(0, fileName.indexOf("-IN"));
		} else if (fileName.indexOf("-OUT") > 0) {
			start = fileName.indexOf("-OUT") + 4;
			prefix = fileName.substring(0, fileName.indexOf("-OUT"));
		}
		String data = "";
		if (start > 0)
			data = fileName.substring(start, start + 15);
		StringBuilder name = new StringBuilder();
		String imei = MyPhoneState.getInstance().getDeviceId(mService);
		if (imei.length() > 0)
			name.append(imei).append("_");
		if (prefix.length() > 0)
			name.append(prefix).append("_");
		name.append(super.formatDateString(data));
		System.out.println(TAG + " formatOldFileName:" + name.toString());
		return name.toString();
	}

	@Override
	protected String formatFileName() {
		File file = super.mCurrentFile.getFile(super.mCurrentFileType);
		String fileName = super.mCurrentFile.getName();
		if (file == null)
			return fileName;
		System.out.println(TAG + " fileName:" + fileName);
		if (fileName.contains("IN") || fileName.contains("OUT")) {
			fileName = this.formatOldFileName();
		}
		if (fileName.indexOf("_Port") > 0) {
			return fileName;
		}
		String imei = MyPhoneState.getInstance().getDeviceId(mService);
		if (!fileName.startsWith(imei))
			fileName = imei + "_" + fileName;
		int post = fileName.indexOf("_");
		int post1 = fileName.lastIndexOf("_");
		if (post == post1) {
			return fileName;
		}
		String taskNameID = fileName.substring(post + 1, post1);
		post = taskNameID.lastIndexOf("_");
		String taskID = "";
		String taskName = "";
		if (post > 0) {
			taskID = taskNameID.substring(post + 1);
			taskName = taskNameID.substring(0, post);
		}
		if (!UtilsMethod.isInteger(taskID))
			return fileName;
		else {
			this.mSchema = WorkOrderFactory.getInstance(mService).getTestSchema(Long.parseLong(taskID));
		}
		String datetime = fileName.substring(post1 + 1);
		StringBuilder name = new StringBuilder();
		name.append(imei).append("_");
		name.append(taskName).append("_");
		name.append(datetime);
		System.out.println(TAG + " formatFileName:" + name.toString());
		return name.toString();
	}

	@Override
	protected UploadRcuParams createParams() {
		if (this.mSchema == null)
			return null;
		return super.createParams();
	}

	@Override
	protected String getTag() {
		UploadRcuTag tag = new UploadRcuTag();
		if (super.mCurrentFileType == FileType.ORGRCU) {
			tag.FileType = "ORGRCU";
			tag.TestPlanID = String.valueOf(this.mSchema.getPlan().getId());
			tag.TestSchemaID = String.valueOf(this.mSchema.getId());
			tag.TimeOffset = this.getTimeZone();
		} else if (super.mCurrentFileType == FileType.PCAP) {
			tag.FileType = "PCap";
			tag.TestPlanID = String.valueOf(this.mSchema.getPlan().getId());
			tag.TestSchemaID = String.valueOf(this.mSchema.getId());
			tag.TimeOffset = this.getTimeZone();
		} else
			return null;
		StringBuilder sb = new StringBuilder();
		sb.append("{\"Protocol\":\"").append(tag.Protocol).append("\",");
		sb.append("\"FileType\":\"").append(tag.FileType).append("\",");
		sb.append("\"TestPlanID\":\"").append(tag.TestPlanID).append("\",");
		sb.append("\"TestSchemaID\":\"").append(tag.TestSchemaID).append("\",");
		sb.append("\"TimeOffset\":\"").append(tag.TimeOffset).append("\"}");
		return sb.toString();
	}

	/**
	 * 上传的Rcu的Tag对象
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class UploadRcuTag {
		/** 协议 */
		public String Protocol = "HuaWei";
		/** 文件类型 */
		public String FileType;
		/** 计划ID */
		String TestPlanID;
		/** 任务ID */
		String TestSchemaID;
		/** 当前时区 */
		String TimeOffset;
	}

	@Override
	protected void initCurrentFileTypes() {
		if (super.mCurrentFile.getFileTypes().length == 0) {
			Set<FileType> fileTypes = new HashSet<>();
			fileTypes.add(FileType.ORGRCU);
			fileTypes.add(FileType.PCAP);
			super.mCurrentFile.setFileTypes(fileTypes);
		}
	}

}
