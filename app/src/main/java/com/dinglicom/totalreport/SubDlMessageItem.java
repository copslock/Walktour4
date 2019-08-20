package com.dinglicom.totalreport;

import java.util.ArrayList;
import java.util.List;

/**
 * 请求场景子列表model
 * @author zhihui.lian
 */
public class SubDlMessageItem {
	private String sceneName;
	private List<String>  dataFileName = new ArrayList<String>();   //文件名列表

	public String getSceneName() {
		return sceneName;
	}
	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}
	public List<String> getDataFileName() {
		return dataFileName;
	}
	public void setDataFileName(List<String> dataFileName) {
		this.dataFileName.addAll(dataFileName);
	}
	
	
}
