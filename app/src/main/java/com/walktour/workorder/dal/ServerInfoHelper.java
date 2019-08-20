package com.walktour.workorder.dal;

import com.dinglicom.UnicomInterface;
import com.walktour.workorder.model.ServerInfoType;
import com.walktour.workorder.model.XmlFileType;

/**
 * 服务器信息数据访问层
 * Author: ZhengLei
 *   Date: 2013-6-21 下午2:51:28
 */
public class ServerInfoHelper extends BaseHelper {
	private String publicIp;
	private int publicPort;
	private ServerInfoType serverType;
	
	public ServerInfoHelper(String publicIp, int publicPort, ServerInfoType serverType) {
		this.publicIp = publicIp;
		this.publicPort = publicPort;
		this.serverType = serverType;
	}

	@Override
	public String getXmlByLib() {
		return UnicomInterface.getResourceServer(publicIp, publicPort, serverType.ordinal());
	}

	@Override
	public boolean exist(String fileName) {
		return super.exist(fileName);
	}

	@Override
	public boolean saveXmlAsFile(String content, String fileName) {
		return super.saveXmlAsFile(content, fileName);
	}

	@Override
	public Object getContentFromFile(String fileName, XmlFileType type) {
		return super.getContentFromFile(fileName, type);
	}

}
