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
 * 记录某天员工的工作时间
 */
@ExcelSheet(sheetName = "统计表")
public class TotalExcelBean {

	@ExcelContent(titleName = "key",index = 0)
	private String key;

	@ExcelContent(titleName = "value",index = 1)
	private String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

//	@ExcelTitleCellFormat(titleName = "key")
//	private static WritableCellFormat getTitleFormat() {
//		WritableCellFormat format = new WritableCellFormat();
//		try {
//			// 单元格格式
//			// 背景颜色
//			// format.setBackground(Colour.PINK);
//			// 边框线
//			format.setBorder(Border.BOTTOM, BorderLineStyle.THIN, Colour.RED);
//			// 设置文字居中对齐方式;
//			format.setAlignment(Alignment.CENTRE);
//			// 设置垂直居中;
//			format.setVerticalAlignment(VerticalAlignment.CENTRE);
//			// 设置自动换行
//			format.setWrap(false);
//
//			// 字体格式
//			WritableFont font = new WritableFont(WritableFont.ARIAL);
//			// 字体颜色
//			font.setColour(Colour.BLUE2);
//			// 字体加粗
//			font.setBoldStyle(WritableFont.BOLD);
//			// 字体加下划线
//			font.setUnderlineStyle(UnderlineStyle.SINGLE);
//			// 字体大小
////			font.setPointSize(20);
//			format.setFont(font);
//
//		} catch (WriteException e) {
//			e.printStackTrace();
//		}
//		return format;
//	}

	@ExcelContentCellFormat(titleName = "key")
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
	@ExcelContentCellFormat(titleName = "value")
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

	@Override
	public String toString() {
		return "TotalExcelBean{" +
				"key='" + key + '\'' +
				", value='" + value + '\'' +
				'}';
	}
}