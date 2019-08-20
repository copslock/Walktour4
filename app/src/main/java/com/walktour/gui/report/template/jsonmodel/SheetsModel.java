package com.walktour.gui.report.template.jsonmodel;

import java.util.LinkedList;
import java.util.List;

/**
 * 自定义的样式表
 * @author weirong.fan
 *
 */
public class SheetsModel {
	/**样式表**/
	private List<SheetInfoModel> sheets=new LinkedList<SheetInfoModel>();

	public List<SheetInfoModel> getSheets() {
		return sheets;
	}
	
}
