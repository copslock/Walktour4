package com.dinglicom.data.model;

import java.util.ArrayList;

public class RecordDetail implements RecordBase{
	public int detail_id;
	public String record_id;
	public int file_type;
	public String file_type_str;
	public String file_name;
	public String file_path;
	public long file_size;
	public String file_guid;
	
	private ArrayList<RecordDetailUpload> detailUploads = new ArrayList<RecordDetailUpload>();
	
	public String getFile_type_str() {
		return file_type_str;
	}

	public ArrayList<RecordDetailUpload> getDetailUploads() {
		return detailUploads;
	}

	public void setDetailUploads(ArrayList<RecordDetailUpload> detailUploads) {
		this.detailUploads = detailUploads;
	}
	
	/**
	 * 添加单行上传状态记录
	 * @param uplad
	 */
	public void addDetailUpload(RecordDetailUpload uplad){
		if(detailUploads == null){
			detailUploads = new ArrayList<RecordDetailUpload>();
		}
		
		detailUploads.add(uplad);
	}
}
