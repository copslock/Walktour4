package com.walktour.workorder.bll;

import android.content.Context;
import android.util.Log;

import com.dinglicom.UnicomInterface;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ServerManager;
import com.walktour.gui.R;
import com.walktour.workorder.dal.ServerInfoHelper;
import com.walktour.workorder.model.ServerInfo;
import com.walktour.workorder.model.ServerInfoType;
import com.walktour.workorder.model.XmlFileType;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务器初始化的业务逻辑类，包括下面的step1 ~ step7
 * Author: ZhengLei
 *   Date: 2013-6-19 下午3:44:49
 */
public class InitServer extends BaseManipulate implements Runnable {
	private static final String TAG = "InitServer";
	// 公开的IP和端口，用于首次连接泰和平台，获取其它服务器资源信息
	private String publicIp;
	private int publicPort;
	private ServerManager mServerManager = null;
	private Context context = null;
	private List<OnInitServerListener> initListeners = new ArrayList<OnInitServerListener>();
	private boolean isValid = false;
	private boolean isConnectNetwork = false;
	private static boolean hasInitAll =false;
	// 初始化类型，因为有这样的需求，至初始化Ftp Upload，不需要初始化别的
	private int type;
	public static final int TYPE_ALL = 0;
	public static final int TYPE_FTP_UPLOAD = 1;
	
	public InitServer(Context context) {
		this(context, TYPE_ALL);
	}
	
	public InitServer(Context context, int type) {
		this.context = context;
		this.mServerManager = ServerManager.getInstance(context);
		this.type = type;
	}
	
	@Override
	public void run() {
		if(type==TYPE_ALL && !hasInitAll) {
			init();
			// 初始化结束，回传结束事件给UI
			for(OnInitServerListener listener : initListeners) {
				listener.onInitFinish(hasInitAll);
			}
		} else if(type==TYPE_FTP_UPLOAD) {
			init();
			// 无回调
		}
	}

	public void init() {
		// ********step 1：从配置后保存的Sharereferences中获取获取IP和端口********
		getSettings();
		
		// ********step 2：验证IP和端口是否有效********
		valid();
		// 通知监听者IP或端口验证失败
		for(OnInitServerListener listener : initListeners) {
			listener.onValid(isValid);
		}
		// 如果未设置联通一级平台服务器的用户名和密码，则不再继续向下走
		if(!isValid) {return;}
		
		// ********step 3：判断网络是否连接********
		checkNetwork();
		for(OnInitServerListener listener : initListeners) {
			listener.onCheckNetwork(isConnectNetwork);
		}
		if(!isConnectNetwork) {return;}
		
		// 如果是只获取联通Ftp Upload服务器信息，则只保存这个
		if(type==TYPE_FTP_UPLOAD) {
			ServerInfo ftpUploadServer = getResourceServer(publicIp, publicPort, ServerInfoType.FtpUploadServer);
			if(ftpUploadServer!=null) mServerManager.saveUnicomServer(ftpUploadServer);
			return;
		}
		
		// ********step 4：连接泰和的服务器，获得业务和回传等服务器信息的List********
		List<ServerInfo> serverInfos = this.getAllResourceServer(publicIp, publicPort);
		if (serverInfos == null)
			return;
		// ********step 5：将所有的服务器信息保存到系统Preference中，以便以后读取地图和回传Log时用********
		mServerManager.saveUnicomServer(serverInfos);
		
		// ********step 6：读取业务服务器信息********
		ServerInfo taskServer = mServerManager.readUnicomServer(UnicomInterface.TASK_SERVER);
		
		// ********step 7：初始化业务服务器********
		hasInitAll = this.initTaskServer(taskServer); //李方杰 :全部成功之后，将初始化成功的标记记录到静态变量中。
		
	}

	/**
	 * 是否已经初始化服务器
	 * @return
	 */
	public static boolean hasInit() {
		Log.i(TAG, "has init all?" + hasInitAll);
		return hasInitAll;
	}
	
	public static void setInit(boolean initAll) {
		hasInitAll = initAll;
	}

	/**
	 * 从设置后生成的配置文件中读取
	 */
	private void getSettings() {
		this.publicIp = mServerManager.getUnicomIp();
		this.publicPort = mServerManager.getUnicomPort();
	}
	
	/**
	 * 验证ip和端口是否有效
	 */
	private void valid() {
		isValid = (publicIp!=null 
				&& !"".equals(publicIp.trim()) 
				&& publicPort!=0);
	}
	
	/**
	 * 检查网络连通性
	 */
	private void checkNetwork() {
		isConnectNetwork = MyPhoneState.getInstance().isNetworkAvirable(context);
	}
	
	private List<ServerInfo> getAllResourceServer(String publicIp, int publicPort) {
		List<ServerInfo> serverInfos = new ArrayList<ServerInfo>();
		ServerInfoType[] serverInfoTypes = ServerInfoType.values();
		
		// i从1开始，因为0表示的是接入的服务器信息，不加入
		for(int i=1; i<serverInfoTypes.length; i++) {
			ServerInfo serverInfo = getResourceServer(publicIp, publicPort, serverInfoTypes[i]);
			if(serverInfo != null) {
				serverInfos.add(serverInfo);
			}else { //lifangjie 必须全部成功之后才可以！！
				return null;
			}
		}
		return serverInfos;
		
	}
	
	private ServerInfo getResourceServer(String publicIp, int publicPort, ServerInfoType type) {
		super.fileName = AppFilePathUtil.getInstance().getSDCardBaseFile(context.getString(R.string.work_order_dir),type.name() + ".xml").getAbsolutePath();
		super.xmlType = XmlFileType.TypeServerInfo;
		super.mHelper = new ServerInfoHelper(publicIp, publicPort, type);
		return (ServerInfo)synchronize();
	}
	
	/**
	 * 初始化（相当于登录）业务服务器
	 * @param info 服务器信息模型
	 * @return 是否初始化成功
	 */
	private boolean initTaskServer(ServerInfo info) {
		if(info != null) {
			return UnicomInterface.initTaskServer(info.getIpAddr(), info.getPort(), info.getAccount(), info.getPassword());
		}
		return false;
	}
	
	@Override
	public Object load() {
		// 前后添加回调事件
		return super.load();
	}

	@Override
	public Object synchronize() {
		// 前后添加回调事件
		return super.synchronize();
	}
	
	public void addListener(OnInitServerListener initListener) {
		if(!initListeners.contains(initListener)) {
			initListeners.add(initListener);
		}
	}
	
	public void removeListener(OnInitServerListener initListener) {
		if(initListeners.contains(initListener)) {
			initListeners.remove(initListener);
		}
	}
	
	/**
	 * 事件监听接口
	 * @author ZhengLei
	 *
	 */
	public interface OnInitServerListener {
		public abstract void onValid(boolean isValid);
		public abstract void onCheckNetwork(boolean isConnectNetwork);
		public abstract void onInitFinish(boolean isInit); // 这里的回调的isInit不要用了，因为可以用hasInit的方法，保持一个口来获取是否初始化
	}
}
