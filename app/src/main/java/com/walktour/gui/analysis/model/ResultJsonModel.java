package com.walktour.gui.analysis.model;

import java.util.LinkedList;
import java.util.List;

/***
 * 历史结果对应的json模型数据
 * 
 * @author weirong.fan
 *
 */
public class ResultJsonModel {
	/**记录文件夹的名字**/
	public String parentName;
	public List<String> files=new LinkedList<String>();
	
	public int allIsDT=0;
	
	public String dataTime="";
	
	List<String> analysis=new LinkedList<String>();

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public int getAllIsDT() {
		return allIsDT;
	}

	public void setAllIsDT(int allIsDT) {
		this.allIsDT = allIsDT;
	}

	public String getDataTime() {
		return dataTime;
	}

	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}

	public List<String> getFiles() {
		return files;
	}

	public List<String> getAnalysis() {
		return analysis;
	}
	
	

}
