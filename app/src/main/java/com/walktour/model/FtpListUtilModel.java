package com.walktour.model;


/**
 * ftp浏览器工具类
 * @author lzh
 *
 */
public class FtpListUtilModel {
	
	
	/**
	 * 保存服务器选中的位置
	 */
	private int serverPosition;
	
	/**
	 * 区别ftp浏览器是上传还是下载
	 */
	private int dlOrUl;

	
	private FtpListUtilModel(){}
	
	private static FtpListUtilModel sInstance;
	
	public synchronized static FtpListUtilModel getInstance(){
		if(sInstance ==null){
			sInstance =new FtpListUtilModel();
		}
		return sInstance;
	}
	
	public int getServerPosition() {
		return serverPosition;
	}

	public void setServerPosition(int serverPosition) {
		this.serverPosition = serverPosition;
	}

	public int getDlOrUl() {
		return dlOrUl;
	}

	public void setDlOrUl(int dlOrUl) {
		this.dlOrUl = dlOrUl;
	}
}
