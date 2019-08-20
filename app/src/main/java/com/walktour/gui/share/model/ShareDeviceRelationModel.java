package com.walktour.gui.share.model;

import com.walktour.gui.share.model.DeviceInfoModel.Device;

import java.util.ArrayList;
import java.util.List;
/***
 * 指定终端曾经发送过和接收过的终端信息列表
 * 
 * @author weirong.fan
 *
 */
public class ShareDeviceRelationModel extends BaseResultInfoModel {
	private List<Device> devices = new ArrayList<DeviceInfoModel.Device>();
	public List<Device> getDevices() {
		return devices;
	}
	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}
}
