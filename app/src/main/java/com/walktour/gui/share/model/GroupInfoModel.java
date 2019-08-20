package com.walktour.gui.share.model;
import java.util.ArrayList;
import java.util.List;
/***
 * 群组信息
 * 
 * @author weirong.fan
 *
 */
public class GroupInfoModel extends BaseResultInfoModel {
	public List<Group> groups = new ArrayList<Group>();
	public class Group {
		public String group_code;
		public String group_name;
		public String device_code;
		public List<DeviceInfoModel.Device> devices = new ArrayList<DeviceInfoModel.Device>();
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
		public String getDevice_code() {
			return device_code;
		}
		public void setDevice_code(String device_code) {
			this.device_code = device_code;
		}
		public List<DeviceInfoModel.Device> getDevices() {
			return devices;
		}
		public void setDevices(List<DeviceInfoModel.Device> devices) {
			this.devices = devices;
		}
	}
	public List<Group> getGroups() {
		return groups;
	}
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
}
