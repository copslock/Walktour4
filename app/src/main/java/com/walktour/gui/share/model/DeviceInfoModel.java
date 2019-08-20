package com.walktour.gui.share.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhihui.lian
 *  注册的终端信息Model
 */
public class DeviceInfoModel extends BaseResultInfoModel{

	private List<Device> devices = new ArrayList<DeviceInfoModel.Device>();

	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

	public class Device {
		public String device_code;
		public String device_name;
		public String device_type;
		public String request_message;

		public String getDevice_code() {
			return device_code;
		}

		public void setDevice_code(String device_code) {
			this.device_code = device_code;
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

		public String getRequest_message() {
			return request_message;
		}

		public void setRequest_message(String request_message) {
			this.request_message = request_message;
		}

	}

}
