package com.dinglicom.data.model;

public class RecordImg implements RecordBase {
	protected int img_id;
	public String node_id;
	public String img_path;
	public String img_name;
	
	public int getImgId(){
		return img_id;
	}
	
	public void setImgId(int id){
		img_id = id;
	}
}
