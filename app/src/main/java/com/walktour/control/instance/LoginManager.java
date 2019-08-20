package com.walktour.control.instance;


/**
 * 
 * */
public class LoginManager{
	public final static String WLAN_NAME = "CMCC";
	public final static String KEY_USER ="KEY_USER";
	public final static String KEY_PASS ="KEY_PASS";
	public final static String KEY_SAVE_USER ="KEY_SAVE_USER";
	public final static String KEY_SAVE_PASS ="KEY_SAVE_PASS";
	public final static String KEY_AUTO_LOGIN ="KEY_AUTO_LOGIN";
	public final static String ACTION_RECEIVE_SMS ="android.provider.Telephony.SMS_RECEIVED";//接受短信
	
	//静态实例
	private LoginManager(){
		
	}
	private  boolean isWifiEnable = false;//wifi是否可用
	private  boolean isWifiEnabling = false;//是否正在启动
	private  boolean hasCMCC = false;//是否在覆盖范围内
	private  boolean hasConnectToCMCC = false;//是否已经关联到CMCC
	private  boolean hasIP = false;//是否已经有IP地址
	private  boolean hasLogin = false;//是否已经登录(注意，不在覆盖范围内也可能是登录状态)
	private  boolean hasLoadPage = false;
	private  boolean outWhenOnline = false;
	private  int linkSpeed;//连接速率
	private  int level;//场强
	private  int ip;//ip地址
	private  long loginTime;
	private static LoginManager sInstance;
	public synchronized static LoginManager getInstance(){
		if(sInstance ==null){
			sInstance =new LoginManager();
		}
		return sInstance;
	}
	public boolean isHasCMCC() {
		return hasCMCC;
	}

	public boolean isWifiEnable() {
		return isWifiEnable;
	}
	public void setWifiEnable(boolean isWifiEnable) {
		sInstance.isWifiEnable = isWifiEnable;
	}
	public boolean isWifiEnabling() {
		return isWifiEnabling;
	}
	public void setWifiEnabling(boolean isWifiEnabling) {
		this.isWifiEnabling = isWifiEnabling;
	}
	public void setHasCMCC(boolean hasCMCC) {
		sInstance.hasCMCC = hasCMCC;
	}
	public boolean isHasConnectToCMCC() {
		return hasConnectToCMCC;
	}
	public void setHasConnectToCMCC(boolean hasConnectToCMCC) {
		sInstance.hasConnectToCMCC = hasConnectToCMCC;
	}
	public boolean isHasIP() {
		return hasIP;
	}
	public void setHasIP(boolean hasIP) {
		sInstance.hasIP = hasIP;
	}
	public boolean isHasLogin() {
		return hasLogin;
	}
	public  void setHasLogin(boolean hasLogin) {
		sInstance.hasLogin = hasLogin;
	}
	public  boolean isHasLoadPage() {
		return hasLoadPage;
	}
	public  void setHasLoadPage(boolean hasLoadPage) {
		sInstance.hasLoadPage = hasLoadPage;
	}
	public long getLoginTime() {
		return loginTime;
	}
	
	public void setLoginTime(long loginTime) {
		sInstance.loginTime = loginTime;
	}
	public int getIp() {
		return ip;
	}
	public void setIp(int ip) {
		sInstance.ip = ip;
	}
	public int getLinkSpeed() {
		return linkSpeed;
	}
	public void setLinkSpeed(int linkSpeed) {
		this.linkSpeed = linkSpeed;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public boolean isOutWhenOnline() {
		return outWhenOnline;
	}
	public void setOutWhenOnline(boolean outWhenOnline) {
		this.outWhenOnline = outWhenOnline;
	}
}