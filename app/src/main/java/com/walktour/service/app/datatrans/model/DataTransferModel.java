package com.walktour.service.app.datatrans.model;

import com.walktour.Utils.WalkStruct.ServerOperateType;

import java.util.List;

/**
 * 数据交互对象
 * 
 * @author jianchao.wang
 *
 */
public class DataTransferModel {
	/** 操作类型 */
	private ServerOperateType mOperateType;
	/** 要上传的文件列表 */
	private List<UploadFileModel> mUploadFiles;
	/** 操作关联的消息 */
	private String mMessage;

	public DataTransferModel(ServerOperateType operateType) {
		this.mOperateType = operateType;
	}

	public ServerOperateType getOperateType() {
		return mOperateType;
	}

	public List<UploadFileModel> getUploadFiles() {
		return mUploadFiles;
	}

	public void setUploadFiles(List<UploadFileModel> uploadFiles) {
		mUploadFiles = uploadFiles;
	}

	public String getMessage() {
		return mMessage;
	}

	public void setMessage(String message) {
		mMessage = message;
	}
}
