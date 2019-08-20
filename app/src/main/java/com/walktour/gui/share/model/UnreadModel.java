package com.walktour.gui.share.model;
import java.util.ArrayList;
import java.util.List;
/**
 * @author zhihui.lian 未读信息
 */
public class UnreadModel extends BaseResultInfoModel {
	public List<ShareFile> files = new ArrayList<ShareFile>();
	public List<ShareFile> getFiles() {
		return files;
	}
	public void setFiles(List<ShareFile> files) {
		this.files = files;
	}
	public class ShareFile {
		public String file_id;
		public String file_type;
		public String file_name;
		public String file_size;
		public String file_describe;
		public String device_code;
		public String device_name;
		public String device_type;
		public String group_code;
		public String group_name;
		public String getFile_id() {
			return file_id;
		}
		public void setFile_id(String file_id) {
			this.file_id = file_id;
		}
		public String getFile_type() {
			return file_type;
		}
		public void setFile_type(String file_type) {
			this.file_type = file_type;
		}
		public String getFile_name() {
			return file_name;
		}
		public void setFile_name(String file_name) {
			this.file_name = file_name;
		}
		public String getFile_size() {
			return file_size;
		}
		public void setFile_size(String file_size) {
			this.file_size = file_size;
		}
		public String getFile_describe() {
			return file_describe;
		}
		public void setFile_describe(String file_describe) {
			this.file_describe = file_describe;
		}
		public String getDevice_code() {
			return device_code;
		}
		public void setDevice_code(String device_code) {
			this.device_code = device_code;
		}
		public String getGroup_code() {
			return group_code;
		}
		public void setGroup_code(String group_code) {
			this.group_code = group_code;
		}
		public String getGroup_name() {
			return group_name;
		}
		public void setGroup_name(String group_name) {
			this.group_name = group_name;
		}
		public String getDevice_name() {
			return device_name;
		}
		public void setDevice_name(String device_name) {
			this.device_name = device_name;
		}
		public String getDevice_type() {
			return device_type;
		}
		public void setDevice_type(String device_type) {
			this.device_type = device_type;
		}
	}
}
