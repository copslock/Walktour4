package com.walktour.Utils.excel.model;

/**
 * @author jinfeng.xie
 * @data 2019/3/15
 */

import com.walktour.Utils.excel.jxlhelper.annotations.ExcelContent;
import com.walktour.Utils.excel.jxlhelper.annotations.ExcelContentCellFormat;
import com.walktour.Utils.excel.jxlhelper.annotations.ExcelSheet;
import com.walktour.Utils.excel.jxlhelper.annotations.ExcelTitleCellFormat;

import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WriteException;

/**
 * 记录禁止用户imei号
 */
@ExcelSheet(sheetName = "禁止用户升级表")
public class NoUpgradeExcelBean {

	@ExcelContent(titleName = "id",index = 0)
	private String id;

	@ExcelContent(titleName = "imei",index = 1)
	private String imei;

	@ExcelContent(titleName = "手机型号",index = 2)
	private String phoneName;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getPhoneName() {
		return phoneName;
	}

	public void setPhoneName(String phoneName) {
		this.phoneName = phoneName;
	}
	@ExcelContentCellFormat(titleName = "id")
	private WritableCellFormat f1() {
		WritableCellFormat format = null;
		try {
			format = new WritableCellFormat();
			format.setAlignment(Alignment.CENTRE);
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return format;
	}

	@ExcelContentCellFormat(titleName = "imei")
	private WritableCellFormat f2() {
		WritableCellFormat format = null;
		try {
			format = new WritableCellFormat();
			format.setAlignment(Alignment.CENTRE);
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return format;
	}
	@ExcelContentCellFormat(titleName = "手机型号")
	private WritableCellFormat f3() {
		WritableCellFormat format = null;
		try {
			format = new WritableCellFormat();
			format.setAlignment(Alignment.CENTRE);
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return format;
	}
	@Override
	public String toString() {
		return "NoUpgradeExcelBean{" +
				"id='" + id + '\'' +
				", imei='" + imei + '\'' +
				", phoneName='" + phoneName + '\'' +
				'}';
	}
}