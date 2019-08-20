package com.walktour.gui.share.logic;

import com.google.gson.Gson;
import com.walktour.gui.share.model.BaseResultInfoModel;
import com.walktour.gui.share.model.DeviceInfoModel;
import com.walktour.gui.share.model.GroupInfoModel;
import com.walktour.gui.share.model.UnreadModel;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;

import org.xutils.http.RequestParams;
import org.xutils.x;
/**
 * 共享Http请求接口实现
 * 
 * @author zhihui.lian
 */
public class ShareHttpRequestUtil {
	private static final Object lock = new Object();
	private static ShareHttpRequestUtil instance;
	public final static String url = "http://61.143.60.84:63997";// 公司外网
	private ShareHttpRequestUtil() {
	}
	public static ShareHttpRequestUtil getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new ShareHttpRequestUtil();
				}
			}
		}
		return instance;
	}
	/***
	 * 注册设备
	 * 
	 * @param device_imei
	 *            设备的IMEI
	 * @param device_language
	 *            设备当前语言信息
	 * @return
	 */
	public BaseResultInfoModel registerDevice(String device_imei, String device_language) {
		BaseResultInfoModel baseResultInfoModel = new BaseResultInfoModel();
		try {
			RequestParams params = new RequestParams(url + "/device/register.do");
			params.addBodyParameter("device_key", device_imei);
			params.addBodyParameter("device_language", device_language);
			params.addBodyParameter("device_type", "0");
			System.out.println("registerDevice=" + params.toString());
			String result = x.http().getSync(params, String.class);
			Gson gson = new Gson();
			baseResultInfoModel = gson.fromJson(result, BaseResultInfoModel.class);
		} catch (Throwable e) {
			e.printStackTrace();
			baseResultInfoModel.setReasonCode(-1);
		}
		return baseResultInfoModel;
	}

	/**
	 * 查询指定终端名称是否已经存在
	 * 
	 * @param query_name
	 * @throws Throwable
	 */
	public BaseResultInfoModel queryDeviceName(String deviceCode, String device_name, String session_id) {
		BaseResultInfoModel baseResultInfoModel = new BaseResultInfoModel();
		try {
			RequestParams params = new RequestParams(url + "/device/query_name.do");
			params.setCharset("UTF-8");
			params.addBodyParameter("device_code", deviceCode);
			params.addBodyParameter("device_name", device_name);
			params.addHeader("Cookie", "JSESSIONID=" + session_id);
			String result = x.http().getSync(params, String.class);
			Gson gson = new Gson();
			baseResultInfoModel = gson.fromJson(result, BaseResultInfoModel.class);
		} catch (Throwable e) {
			e.printStackTrace();
			baseResultInfoModel.setReasonCode(-1);
		}
		return baseResultInfoModel;
	}
	public BaseResultInfoModel editDevice(String device_name, String token_id, String session_id) {
		BaseResultInfoModel baseResultInfoModel = new BaseResultInfoModel();
		try {
			RequestParams params = new RequestParams(url + "/device/edit.do");
			params.setCharset("UTF-8");
			params.addBodyParameter("device_name", device_name);
			params.addBodyParameter("token_id", token_id);
			params.addHeader("Cookie", "JSESSIONID=" + session_id);
			String result = x.http().getSync(params, String.class);
			Gson gson = new Gson();
			baseResultInfoModel = gson.fromJson(result, BaseResultInfoModel.class);
		} catch (Throwable e) {
			e.printStackTrace();
			baseResultInfoModel.setReasonCode(-1);
		}
		return baseResultInfoModel;
	}
	
	/***
	 * 终端编号查询
	 * @param devie_code 终端编号
	 * @param session_id
	 * @return
	 */
	public BaseResultInfoModel queryDevice(String devie_code,String session_id) {
		BaseResultInfoModel baseResultInfoModel = new BaseResultInfoModel();
		try {
			RequestParams params = new RequestParams(url + "/device/query_code.do");
			params.setCharset("UTF-8");
			params.addBodyParameter("devie_code", devie_code); 
			params.addHeader("Cookie", "JSESSIONID=" + session_id);
			String result = x.http().getSync(params, String.class);
			Gson gson = new Gson();
			baseResultInfoModel = gson.fromJson(result, BaseResultInfoModel.class);
		} catch (Throwable e) {
			e.printStackTrace();
			baseResultInfoModel.setReasonCode(-1);
		}
		return baseResultInfoModel;
	}
	/**
	 * 查询在平台中注册的终端信息，且过滤掉已添加为好友的终端
	 * 
	 * @param httpCallBackI
	 * @param query_name
	 *            支持模糊匹配
	 * @param page_no
	 * @param page_size
	 * @param is_filter_type
	 *            0-表示不过滤
	 * 
	 */
	public DeviceInfoModel queryNewDevice(String query_name, int start_row, int page_size, String session_id) {
		DeviceInfoModel deviceInfoModel = new DeviceInfoModel();
		try {
			RequestParams params = new RequestParams(url + "/device/query.do");
			params.setCharset("UTF-8");
			params.addBodyParameter("query_name", null == query_name ? "" : query_name);
			params.addBodyParameter("start_row", start_row + "");
			params.addBodyParameter("page_size", page_size + "");
			params.addHeader("Cookie", "JSESSIONID=" + session_id);
			String result = x.http().getSync(params, String.class);
			Gson gson = new Gson();
			deviceInfoModel = gson.fromJson(result, DeviceInfoModel.class);
		} catch (Throwable e) {
			e.printStackTrace();
			deviceInfoModel.setReasonCode(-1);
		}
		return deviceInfoModel;
	}
	/****
	 * 终端申请加好友查询
	 * 
	 * @param query_flag
	 *            0、申请加自己为好友，1、自己申请加别人好友
	 * @param session_id
	 * @return
	 */
	public DeviceInfoModel queryConfirmDevice(String query_flag, String session_id) {
		DeviceInfoModel deviceInfoModel = new DeviceInfoModel();
		try {
			RequestParams params = new RequestParams(url + "/device/query_relation_requests.do");
			params.setCharset("UTF-8");
			params.addBodyParameter("query_flag", query_flag + "");
			params.addHeader("Cookie", "JSESSIONID=" + session_id);
			String result = x.http().getSync(params, String.class);
			Gson gson = new Gson();
			deviceInfoModel = gson.fromJson(result, DeviceInfoModel.class);
		} catch (Throwable e) {
			e.printStackTrace();
			deviceInfoModel.setReasonCode(-1);
		}
		return deviceInfoModel;
	}
	/***
	 * 查询指定终端的好友终端信息列表
	 * 
	 * @param session_id
	 *            session信息
	 * @return
	 */
	public DeviceInfoModel queryFriendDevice(String session_id) {
		DeviceInfoModel deviceInfoModel = new DeviceInfoModel();
		try {
			RequestParams params = new RequestParams(url + "/device/query_relations.do");
			params.setCharset("UTF-8");
			params.addHeader("Cookie", "JSESSIONID=" + session_id);
			String result = x.http().getSync(params, String.class);
			Gson gson = new Gson();
			deviceInfoModel = gson.fromJson(result, DeviceInfoModel.class);
		} catch (Throwable e) {
			e.printStackTrace();
			deviceInfoModel.setReasonCode(-1);
		}
		return deviceInfoModel;
	}
	/***
	 * 新增设备请求
	 * 
	 * @param relation_code
	 *            终端编号
	 * @param message
	 *            消息
	 * @param session_id
	 * @return
	 */
	public DeviceInfoModel requestAddNewDevice(String relation_code, String message, String session_id) {
		DeviceInfoModel deviceInfoModel = new DeviceInfoModel();
		try {
			RequestParams params = new RequestParams(url + "/device/add_relation.do?");
			params.setCharset("UTF-8");
			params.addBodyParameter("relation_code", relation_code + "");
			params.addBodyParameter("message", message + "");
			params.addHeader("Cookie", "JSESSIONID=" + session_id);
			String result = x.http().getSync(params, String.class);
			Gson gson = new Gson();
			deviceInfoModel = gson.fromJson(result, DeviceInfoModel.class);
		} catch (Throwable e) {
			e.printStackTrace();
			deviceInfoModel.setReasonCode(-1);
		}
		return deviceInfoModel;
	}
	/**
	 * 发送要共享的文件消息
	 * 
	 * @param from_device_code
	 * @param to_device_codes
	 *            逗号拼接 100000,100001
	 * @param to_group_codes
	 *            逗号拼接 100000,100001
	 * @param file_type
	 * @param file_name
	 * @param file_size
	 * @param file_describe
	 * @return BaseResultInfoModel
	 */
	public BaseResultInfoModel send(String device_code, String to_device_codes, String to_group_codes, String file_type,
			String file_name, String file_size, String file_describe, String session_id) {
		BaseResultInfoModel baseResultInfoModel = new BaseResultInfoModel();
		try {
			RequestParams params = new RequestParams(url + "/file/send.do");
			params.setCharset("UTF-8");
			params.addBodyParameter("device_code", device_code);
			params.addBodyParameter("to_device_codes", to_device_codes);
			params.addBodyParameter("to_group_codes", to_group_codes);
			params.addBodyParameter("file_type", file_type);
			params.addBodyParameter("file_name", file_name);
			params.addBodyParameter("file_size", file_size);
			params.addBodyParameter("file_describe", file_describe);
			params.addHeader("Cookie", "JSESSIONID=" + session_id);
			String result = x.http().getSync(params, String.class);
			Gson gson = new Gson();
			baseResultInfoModel = gson.fromJson(result, BaseResultInfoModel.class);
		} catch (Throwable e) {
			e.printStackTrace();
			baseResultInfoModel.setReasonCode(-1);
		}
		return baseResultInfoModel;
	}
	/**
	 * 转发共享文件信息
	 * 
	 * @param file_id
	 * @param to_device_codes
	 *            逗号拼接 100000,100001
	 * @param to_group_codes
	 *            逗号拼接 100000,100001
	 * @return BaseResultInfoModel
	 */
	public BaseResultInfoModel share(String deviceCode, String file_id, String to_device_codes, String to_group_codes,
			String session_id) {
		BaseResultInfoModel baseResultInfoModel = new BaseResultInfoModel();
		RequestParams params = new RequestParams(url + "/file/share.do");
		params.setCharset("UTF-8");
		params.addBodyParameter("device_code", deviceCode);
		params.addBodyParameter("file_id", file_id);
		params.addBodyParameter("to_device_codes", to_device_codes);
		params.addBodyParameter("to_group_codes", to_group_codes);
		params.addHeader("Cookie", "JSESSIONID=" + session_id);
		try {
			String result = x.http().getSync(params, String.class);
			Gson gson = new Gson();
			baseResultInfoModel = gson.fromJson(result, BaseResultInfoModel.class);
		} catch (Throwable e) {
			e.printStackTrace();
			baseResultInfoModel.setReasonCode(-1);
		}
		return baseResultInfoModel;
	}
	/**
	 * 获取指定终端未获取的共享信息
	 * 
	 * @param device_code
	 * @return UnreadModel
	 */
	public UnreadModel queryUnreadInfo(String device_code, String session_id) {
		UnreadModel unreadModel = new UnreadModel();
		RequestParams params = new RequestParams(url + "/file/query.do");
		params.setCharset("UTF-8");
		params.addBodyParameter("device_code", device_code);
		params.addHeader("Cookie", "JSESSIONID=" + session_id);
		try {
			String result = x.http().getSync(params, String.class);
			Gson gson = new Gson();
			unreadModel = gson.fromJson(result, UnreadModel.class);
		} catch (Throwable e) {
			e.printStackTrace();
			unreadModel.setReasonCode(-1);
		}
		return unreadModel;
	}
	/**
	 * 在平台注册指定终端的群组
	 * 
	 * @param device_code
	 * @param group_name
	 * @return
	 */
	public BaseResultInfoModel registerGroup(String device_code, String group_name, String session_id) {
		BaseResultInfoModel baseResultInfoModel = new BaseResultInfoModel();
		try {
			RequestParams params = new RequestParams(url + "/group/register.do");
			params.setCharset("UTF-8");
			params.addBodyParameter("device_code", device_code);
			params.addBodyParameter("group_name", group_name);
			params.addHeader("Cookie", "JSESSIONID=" + session_id);
			String result = x.http().getSync(params, String.class);
			Gson gson = new Gson();
			baseResultInfoModel = gson.fromJson(result, BaseResultInfoModel.class);
		} catch (Throwable e) {
			e.printStackTrace();
			baseResultInfoModel.setReasonCode(-1);
		}
		return baseResultInfoModel;
	}
	/**
	 * 查询指定群组名称是否已经存在
	 * 
	 * @param group_name
	 * @return
	 */
	public BaseResultInfoModel queryGroupName(String group_name, String deviceCode, String session_id) {
		BaseResultInfoModel baseResultInfoModel = new BaseResultInfoModel();
		try {
			RequestParams params = new RequestParams(url + "/group/query_name.do");
			params.setCharset("UTF-8");
			params.addBodyParameter("group_name", group_name);
			params.addBodyParameter("device_code", deviceCode);
			params.addHeader("Cookie", "JSESSIONID=" + session_id);
			String result = x.http().getSync(params, String.class);
			Gson gson = new Gson();
			baseResultInfoModel = gson.fromJson(result, BaseResultInfoModel.class);
		} catch (Throwable e) {
			e.printStackTrace();
			baseResultInfoModel.setReasonCode(-1);
		}
		return baseResultInfoModel;
	}

	/**
	 * 编辑指定群组的名称
	 * 
	 * @param group_code
	 * @param group_name
	 * @return
	 */
	public BaseResultInfoModel editGroup(String group_code, String group_name, String deviceCode, String session_id) {
		BaseResultInfoModel baseResultInfoModel = new BaseResultInfoModel();
		try {
			RequestParams params = new RequestParams(url + "/group/edit.do");
			params.setCharset("UTF-8");
			params.addBodyParameter("group_code", group_code);
			params.addBodyParameter("group_name", group_name);
			params.addBodyParameter("device_code", deviceCode);
			params.addHeader("Cookie", "JSESSIONID=" + session_id);
			String result = x.http().getSync(params, String.class);
			Gson gson = new Gson();
			baseResultInfoModel = gson.fromJson(result, BaseResultInfoModel.class);
		} catch (Throwable e) {
			e.printStackTrace();
			baseResultInfoModel.setReasonCode(-1);
		}
		return baseResultInfoModel;
	}
	/**
	 * 查询指定群组下面的终端
	 * 
	 * @param group_code
	 * @return
	 */
	public DeviceInfoModel query_members(String group_code, String session_id) {
		DeviceInfoModel deviceInfoModel = new DeviceInfoModel();
		RequestParams params = new RequestParams(url + "/group/query_members.do");
		params.setCharset("UTF-8");
		params.addBodyParameter("group_code", group_code);
		params.addHeader("Cookie", "JSESSIONID=" + session_id);
		try {
			String result = x.http().getSync(params, String.class);
			Gson gson = new Gson();
			deviceInfoModel = gson.fromJson(result, DeviceInfoModel.class);
		} catch (Throwable e) {
			e.printStackTrace();
			deviceInfoModel.setReasonCode(-1);
		}
		return deviceInfoModel;
	}
	/**
	 * 添加指定群组下面的终端
	 * 
	 * @param group_code
	 *            群组code
	 * @param device_code
	 *            终端code
	 * @param device_codes
	 *            逗号拼接 100000,100001
	 * @return
	 */
	public BaseResultInfoModel add_members(String group_code, String device_codes, String session_id) {
		BaseResultInfoModel baseResultInfoModel = new BaseResultInfoModel();
		RequestParams params = new RequestParams(url + "/group/add_members.do");
		params.setCharset("UTF-8");
		params.addBodyParameter("group_code", group_code);
		params.addBodyParameter("device_codes", device_codes);
		params.addHeader("Cookie", "JSESSIONID=" + session_id);
		try {
			String result = x.http().getSync(params, String.class);
			Gson gson = new Gson();
			baseResultInfoModel = gson.fromJson(result, BaseResultInfoModel.class);
		} catch (Throwable e) {
			e.printStackTrace();
			baseResultInfoModel.setReasonCode(-1);
		}
		return baseResultInfoModel;
	}
	/***
	 * 删除设备关系
	 * 
	 * @param relation_code
	 *            关联的设备code
	 * @param session_id
	 *            session
	 * @return
	 */
	public BaseResultInfoModel deleteDeviceRelation(String relation_code, String session_id) {
		BaseResultInfoModel baseResultInfoModel = new BaseResultInfoModel();
		RequestParams params = new RequestParams(url + "/device/delete_relation.do");
		params.setCharset("UTF-8");
		params.addBodyParameter("relation_code", relation_code);
		params.addHeader("Cookie", "JSESSIONID=" + session_id);
		try {
			String result = x.http().getSync(params, String.class);
			Gson gson = new Gson();
			baseResultInfoModel = gson.fromJson(result, BaseResultInfoModel.class);
		} catch (Throwable e) {
			e.printStackTrace();
			baseResultInfoModel.setReasonCode(-1);
		}
		return baseResultInfoModel;
	}
	/***
	 * 退出指定的群组
	 * 
	 * @param group_code
	 *            群组编号
	 * @param deviceCode
	 *            终端编号
	 * @return
	 */
	public BaseResultInfoModel exitGroup(String group_code, String session_id) {
		BaseResultInfoModel baseResultInfoModel = new BaseResultInfoModel();
		RequestParams params = new RequestParams(url + "/group/exit.do");
		params.setCharset("UTF-8");
		params.addBodyParameter("group_code", group_code); 
		params.addHeader("Cookie", "JSESSIONID=" + session_id);
		try {
			String result = x.http().getSync(params, String.class);
			Gson gson = new Gson();
			baseResultInfoModel = gson.fromJson(result, BaseResultInfoModel.class);
		} catch (Throwable e) {
			e.printStackTrace();
			baseResultInfoModel.setReasonCode(-1);
		}
		return baseResultInfoModel;
	}
	/**
	 * 查询指定终端参与的所有群组列表
	 * 
	 * @param group_code
	 *            支持模糊匹配
	 * @param device_codes
	 *            逗号拼接 100000,100001
	 * @return
	 */
	public GroupInfoModel queryGrouprelations(String deviceCode, String session_id) {
		GroupInfoModel groupInfoModel = new GroupInfoModel();
		RequestParams params = new RequestParams(url + "/group/query_relations.do");
		params.setCharset("UTF-8");
		params.addBodyParameter("device_code", deviceCode);
		params.addHeader("Cookie", "JSESSIONID=" + session_id);
		try {
			String result = x.http().getSync(params, String.class);
			Gson gson = new Gson();
			groupInfoModel = gson.fromJson(result, GroupInfoModel.class);
		} catch (Throwable e) {
			e.printStackTrace();
			groupInfoModel.setReasonCode(-1);
		}
		return groupInfoModel;
	}
	/***
	 * 确认还是拒绝设备关联
	 * 
	 * @param relation_code
	 * @param check_flag
	 * @param session_id
	 * @return
	 */
	public BaseResultInfoModel checkDevicerelations(String relation_code, String check_flag, String session_id) {
		BaseResultInfoModel groupInfoModel = new BaseResultInfoModel();
		RequestParams params = new RequestParams(url + "/device/check_relation.do");
		params.setCharset("UTF-8");
		params.addBodyParameter("relation_code", relation_code + "");
		params.addBodyParameter("check_flag", check_flag + "");
		params.addHeader("Cookie", "JSESSIONID=" + session_id);
		try {
			String result = x.http().getSync(params, String.class);
			Gson gson = new Gson();
			groupInfoModel = gson.fromJson(result, BaseResultInfoModel.class);
		} catch (Throwable e) {
			e.printStackTrace();
			groupInfoModel.setReasonCode(-1);
		}
		return groupInfoModel;
	}
	/**
	 * 共享文件下载链接
	 * 
	 * @param file_id
	 * @return url
	 */
	public String getDownUrl(String file_id) {
		return url + "/file/download.do?device_code=" + ShareCommons.device_code + "&file_id=" + file_id;
	}
}
