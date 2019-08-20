package com.walktour.gui.report.template.jsonmodel;

import com.walktour.gui.report.template.GroupTemplateModel;

import java.util.LinkedList;
import java.util.List;

/***
 * 报表信息
 * 
 * @author weirong.fan
 *
 */
public class SheetInfoModel {

	/** 报表名称 **/
	private String sheetName = "";

	/** 每一个sheet选择的报表样式信息 **/
	private List<GroupTemplateModel> groups = new LinkedList<GroupTemplateModel>();

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public List<GroupTemplateModel> getGroups() {
		return groups;
	}

}
