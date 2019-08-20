package com.dinglicom.btu;

/**
 * 统一的服务端交互jni类，包括atu、btu、fleet平台
 */
public class comlib {
	/**单例**/
	private final static comlib instance = new comlib();
	/** 函数调用返回值：函数调用成功 */
	public final static int DL_RET_OK = 0;
	/** 函数调用返回值：函数调用失败 */
	public final static int DL_RET_ERR = -2;
	/** 函数调用返回值：套接字初始化值 */
	public final static int DL_SOCKET_INIT_VALUE = -1;
	/** 函数调用返回值：login错误 */
	public final static int DL_RET_ERR_LOGIN = -3;
	/** 函数调用返回值：网络模块初始化失败 */
	public final static int DL_RET_ERR_INIT = 100;
	/** 函数调用返回值：登录密码错误 */
	public final static int DL_RET_LOGIN_ERR_PW = 101;
	/** 函数调用返回值：登录密码为空 */
	public final static int DL_RET_LOGIN_ERR_PW_NULL = 102;
	/** 函数调用返回值：ip为空 */
	public final static int DL_RET_LOGIN_ERR_SERVER_NULL = 103;
	/** 函数调用返回值：初始化失败 */
	public final static int DL_RET_LOGIN_ERR_INIT = 104;
	/** 函数调用返回值：保存config数据失败 */
	public final static int DL_RET_CONFIG_ERR_SAVE = 105;
	/** 函数调用返回值：请求应答失败，服务器应答NAC */
	public final static int DL_RET_CONFIG_ERR_REQUEST = 106;
	/** 函数调用返回值：未登陆 */
	public final static int DL_RET_CONFIG_ERR_NOT_LOGIN = 107;
	/** 函数调用返回值：log文件名长度不对 */
	public final static int DL_RET_UPLOAD_ERR_FILENAME_LEN = 108;
	/** 函数调用返回值：当前config不需要更新 */
	public final static int DL_RET_CONFIG_NOT_NEED_UPDATA = 109;
	/** 函数调用返回值：打开上传文件失败 */
	public final static int DL_RET_UPLOAD_ERR_OPEN_FILE = 110;
	/** 函数调用返回值：上传数据完成请求应答失败，服务器应答NAC */
	public final static int DL_RET_UPLOAD_ERR_REQUEST_END_NAC = 111;
	/** 函数调用返回值：读取上传数据失败 */
	public final static int DL_RET_UPLOAD_ERR_READ = 112;
	/** 函数调用返回值：加密上传数据失败 */
	public final static int DL_RET_UPLOAD_ERR_ENCRYPT = 113;
	/** 函数调用返回值：未找到待发送的事件 */
	public final static int DL_RET_EVENT_ERR_NOT_FIND = 114;
	/** 函数调用返回值：分配内存失败 */
	public final static int DL_RET_ERR_MALLOC_FAIL = 115;
	/** 函数调用返回值：中止上传 */
	public final static int DL_RET_UPLOAD_INTERRUPT = 116;
	/** 函数调用返回值：压缩文件失败 */
	public final static int DL_RET_COMPRESS_ERROR = 117;
	/** 函数调用返回值：登录失败，已经登录 */
	public final static int DL_RET_LOGIN_ERR_ALREADY = 118;
	/** 函数调用返回值：外部分配的内存不足 */
	public final static int DL_RET_SYNTIME_BUFFER_ERROR = 119;
	/** 函数调用返回值：同步时间失败 */
	public final static int DL_RET_SYNTIME_FAILUER = 120;
	/** 函数调用返回值：接口调用错误，非该模式的接口 */
	public final static int DL_RET_INTERFACE_ERROR = 121;
	/** 函数调用返回值：文件不存在 */
	public final static int DL_RET_UPLOAD_FILE_NOT_EXIST = 122;
	/** 函数调用返回值：文件已上传 */
	public final static int DL_RET_UPLOAD_FILE_HADUPLOAD = 123;
	/** 函数回调类型值： 输出上传信息结构，data为TRANS_INFO **/
	public final static int CMD_DATA_UPLOAD_TransInfo = 1;
	/** 函数回调类型值： 连接服务器返回，data为int*，1为成功，0为失败 */
	public final static int CMD_ClientConnect = 2;
	/** 函数回调类型值：客户端断开连接，data为NULL */
	public final static int CMD_ClientDisconnect = 3;
	/** 函数回调类型值：客户端请求登陆结果，data为int*，1为成功，0为失败 */
	public final static int CMD_ClientLogin = 4;
	/** 函数回调类型值：当前文件开始上传，data为char*，内容为文件名（不包含路径) */
	public final static int CMD_DATA_UploadStart = 5;
	/** 函数回调类型值：当前文件上传成功，data为char*，内容为文件名（不包含路径） */
	public final static int CMD_DATA_UploadSuccess = 6;
	/** 函数回调类型值：当前文件上传失败，data为char*，内容为文件名（不包含路径） */
	public final static int CMD_DATA_UploadFailure = 7;
	/** 函数回调类型值：所有文件上传完成，data为int*，1为上传成功，0为失败 */
	public final static int CMD_DATA_UploadFinish = 8;
	/** 函数回调类型值：获取测试计划返回，data为int*，1为成功，0为失败 */
	public final static int CMD_GetTestPlan = 9;
	/** 函数回调类型值：重置 */
	public final static int CMD_BACK_DIAL_RESET = 100;
	/** 函数回调类型值：配置 */
	public final static int CMD_BACK_FLAG_CONFIG = 101;
	/** 函数回调类型值：重启 */
	public final static int CMD_BACK_FLAG_RESTART = 102;
	/** 函数回调类型值：文件分割 */
	public final static int CMD_BACK_FLAG_FILECUT = 103;
	/** 函数回调类型值：时间同步成功 */
	public final static int CMD_SYNC_SUCCESS = 200;
	/** 函数回调类型值：时间同步失败 */
	public final static int CMD_SYNC_FAIL = 201;
	/** 登录类型：ATU */
	public final static int LOGIN_TYPE_ATU = 0;
	/** 登录类型：BTU */
	public final static int LOGIN_TYPE_BTU = 1;
	/** 登录类型：FLEET */
	public final static int LOGIN_TYPE_FLEET = 2;
	/** 编码格式：gbk */
	public final static int ENCODING_GBK = 0;
	/** 编码格式：utf-8 */
	public final static int ENCODING_UTF8 = 1;
	/** 回调监听类 */
	private static OnCallbackListener mListener = null;

	static {
		System.loadLibrary("communicate_btu");
	}

	/**
	 * 私有构造器
	 */
	private comlib() {
		super();
	}

	/***
	 * 获取单例
	 * @return
	 */
	public static comlib getInstance() {
		return instance;
	}

	/**
	 * 回调方法
	 * 
	 * @param buffer
	 *            消息内容
	 * @param type
	 *            消息类型
	 */
	public static void msg_callback(String buffer, int type) {
		// Log.i("comlib", String.format("type:%d,buffer:%s", type, buffer));
		if (mListener != null){
			mListener.onCallback(buffer, type);
		}
		// System.out.println("buffer="+buffer);
	}

	public void setOnCallbackListener(OnCallbackListener listener) {
		mListener = listener;
	}

	/**
	 * 回调接口类
	 * 
	 * @author jianchao.wang
	 *
	 */
	public interface OnCallbackListener {
		/**
		 * 回调方法
		 * 
		 * @param buffer
		 *            消息内容
		 * @param type
		 *            消息类型
		 */
		void onCallback(String buffer, int type);
	}

	/**
	 * 
	 * 初始化客户端连接
	 * 
	 * @param pwPath
	 *            密码文件路径
	 * @param serverIp
	 *            服务端IP
	 * @param password
	 *            服务端密码
	 * @param port
	 *            服务端端口
	 * @param userId
	 *            用户id
	 * @param logPath
	 *            日志文件绝对路径
	 * @param loginType
	 *            登录类型 0：ATU，1 = BTU，2=Fleet
	 * @param cVer
	 * @param sVer
	 * @param syncTime
	 *            是否进行时间同步
	 * @return
	 * 
	 * 		0： DL_RET_OK， -2： DL_RET_ERR -1：DL_SOCKET_INIT_VALUE
	 *         -3：DL_RET_ERR_LOGIN
	 * 
	 */
	public native int initclient(String pwPath, String serverIp, String password, String userId, String logPath,
			int timeout,int loginType, int port, int cVer, String sVer, int syncTime);

    /**
     *
     * 初始化客户端连接
     *
     * @param pwPath
     *            密码文件路径
     * @param serverIp
     *            服务端IP
     * @param password
     *            服务端密码
     * @param port
     *            服务端端口
     * @param userId
     *            用户id
     * @param logPath
     *            日志文件绝对路径
     * @param loginType
     *            登录类型 0：ATU，1 = BTU，2=Fleet
     * @param cVer
     * @param sVer
     * @param syncTime
     *            是否进行时间同步
     * @param mode
     *            模式 0 默认设备登录模式; 1 采用User和Pass认证模式
     * @return
     *
     * 		0： DL_RET_OK， -2： DL_RET_ERR -1：DL_SOCKET_INIT_VALUE
     *         -3：DL_RET_ERR_LOGIN
     *
     */
    public native int initclientmode(String pwPath, String serverIp, String password, String userId, String logPath,
                                 int timeout,int loginType, int port, int cVer, String sVer, int syncTime, int mode);


	/**
	 * 获取配置
	 * 
	 * @param cVer
	 * @param fileName
	 * @return 1表示成功,0表示失败
	 */
	public native int getconfig(int cVer, String fileName);

	/**
	 * 销毁客户端
	 */
	public native void destroyclient();

	/**
	 * 上传数据
	 * 
	 * @param pPath
	 *            本地DTLog文件（未压缩的文件）全路径
	 * @param pFileName
	 *            远程文件名
	 * @param bNewFile
	 *            是否新文件
	 * @return
	 */
	public native int uploaddata(String pPath, String pFileName, int bNewFile);

	/**
	 * 上传压缩数据
	 * 
	 * @param pPath
	 *            本地DTLog文件（压缩过的文件）全路径
	 * @param pFileName
	 *            远程文件名
	 * @param bNewFile
	 *            否新文件
	 * @param bDataSet
	 *            0：非数据集（老格式） 1：数据集（新集成数据集71的格式）
	 * @return
	 */
	public native int uploadzipdata(String pPath, String pFileName, int bNewFile, int bDataSet);

	/**
	 * 压缩DTLog文本文件
	 * 
	 * @param pRefName
	 *            DTLog文本文件
	 * @param pDegname
	 *            压缩后的目标文件
	 * @return
	 */
	public native int compressfile(String pRefName, String pDegname);

	/**
	 * 向平台上报事件
	 * 
	 * @param code
	 *            事件CODE如0x2000就写2000
	 * @param pEmodule
	 *            模块号，来自测试计划
	 * @param pSec
	 * @param pUsec
	 * @return
	 */
	public native int sendevent(String code, String pEmodule, String pSec, String pUsec);

	/**
	 * 获取已上传的文件大小
	 * 
	 * @return
	 */
	public native int getuploadedsize();

	/**
	 * 停止上传
	 * 
	 * @return
	 */
	public native int stopupload();

	/**
	 * fleet新协议上传接口（安徽、华为、福建等）
	 * 
	 * @param localFilePath
	 *            上传的文件绝对路径
	 * @param serverFileName
	 *            服务器端需要的文件名
	 * @param tag
	 *            协议中规定的jason串
	 * @param encoding
	 *            编码格式 0:gbk,1:utf-8
	 * @return
	 */
	public native int uploaddatafleet(String localFilePath, String serverFileName, String tag, int encoding);

	/**
	 * 实时上传参数接口
	 * 
	 * @param msg
	 *            参数json字符串
	 * @param encoding
	 *            编码格式 0:gbk,1:utf-8
	 * @return
	 */
	public native int uploadrealtimemsg(String msg, int encoding);

}
