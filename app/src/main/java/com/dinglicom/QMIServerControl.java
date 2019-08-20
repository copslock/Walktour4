package com.dinglicom;

/**
 * 
 * @author jianchao.wang
 *
 */
public class QMIServerControl {

	/**
	 * 执行返回值
	 * 
	 * @author jianchao.wang
	 *
	 */
	public static enum ControlReturn {
		error(-1, "Error"), success(0, "Success"), param_error(1, "ParamError"), reg_callback_failed(2,
				"RegCallbackFailed"), open_ser_error(3, "OpenSerError"), get_clientId_failed(4,
						"GetClientIdFailed"), ser_verEx_failed(5, "SerVerExFailed"), buffer_flowing(6,
								"BufferFlowing"), register_failed(7, "RegisterFailed"), response_not_complete(8,
										"ResponseNotComplete"), aka_sync_failure(9, "AkaSyncFailure"), aka_failure(10,
												"AkaFailure"), create_socket_error(11, "CreateSocketError"), bind_socket_error(12,
														"BindSocketError"), connect_socket_error(13, "ConnectSocketError");
		private int mId;
		private String mName;

		private ControlReturn(int id, String name) {
			mId = id;
			mName = name;
		}

		/**
		 * 根据ID获取对象
		 * 
		 * @param id
		 *          对象Id
		 * @return
		 */
		public static ControlReturn get(int id) {
			for (ControlReturn cr : values()) {
				if (cr.mId == id)
					return cr;
			}
			return null;
		}

		public String getName() {
			return mName;
		}

	}

	static {
		System.loadLibrary("QMISerControl");
	}

	/**
	 * 生成控制句柄
	 * 
	 * @return 句柄号
	 */
	public native static int CreateSerControlHandle();

	/**
	 * 发送请求到控制端
	 * 
	 * @param handle
	 *          句柄号
	 * @param iNonceLen
	 *          请求消息长度
	 * @param szNonce
	 *          请求消息内容
	 * @return
	 */
	public native static int SendRequestToSer(int handle, int iNonceLen, String szNonce);

	/**
	 * 获取请求响应的数据
	 * 
	 * @param handle
	 *          句柄号
	 * @return
	 */
	public native static String GetResMsgFromSer(int handle);

	/**
	 * 释放控制句柄
	 * 
	 * @param handle
	 *          句柄号
	 * @return
	 */
	public native static int FreeSerControlHandle(int handle);
}
