package com.walktour.model;

public class Especial {

	public static final int TYPE_ONE 	= 1;	//表格特殊结构,定义了表格头,每行标题及该行表格头下对应的参数ID,如果该行需要对值进行转义,可以定义转义字符
	public static final int TYPE_TWO 	= 2;	//当前值需要转行字符转义,用定义的值替换整形值显示
	public static final int TYPE_THREE 	= 3;	//为特殊结构信息,需要从结构中读取相关信息进行显示
	public static final int TYPE_FOUR 	= 4;	//为特殊结构信息,需要从结构中读取相关信息进行显示
	public static final int TYPE_FIVE	= 5;	//配置参数为5时表示需要转成16进制
	public static final int TYPE_SEVEN	= 7;	//分子列表分母列表参数值和(暂只支持和;如需支持其它运算方法,可加属性calculate="%d" 1:+ 2:- 3:* 4:/)的比,
	
	private String 		tableTitle = "";
	private String[] 		columnTitles;
	private EspecialRow[] 	tableRows;
	private EspecialEnum[] especialEnums;
	private int 			columnWidth = 0;
	
	public int getCol() {
		if (this.columnTitles != null) {
			return this.columnTitles.length;
		}
		return 0;
	}
	public int getRow() {
		if (this.tableRows != null) {
			return this.tableRows.length;
		}
		return 0;
	}
	public String getTableTitle() {
		if (this.tableTitle == null) {
			return "";
		}
		return tableTitle;
	}
	public void setTableTitle(String tableTitle) {
		this.tableTitle = tableTitle;
	}
	public String[] getColumnTitles() {
		if (this.columnTitles == null) {
			return new String[0];
		}
		return columnTitles;
	}
	public void setColumnTitles(String[] columnTitles) {
		this.columnTitles = columnTitles;
	}
	public EspecialRow[] getTableRows() {
		if (this.tableRows == null) {
			return new EspecialRow[0];
		}
		return tableRows;
	}
	public void setTableRows(EspecialRow[] tableRows) {
		this.tableRows = tableRows;
	}
	public EspecialEnum[] getEspecialEnums() {
		if (this.especialEnums == null) {
			return new EspecialEnum[0];
		}
		return especialEnums;
	}
	public void setEspecialEnums(EspecialEnum[] especialEnums) {
		this.especialEnums = especialEnums;
	}
	public int getColumnWidth() {
		return columnWidth;
	}
	public void setColumnWidth(int columnWidth) {
		this.columnWidth = columnWidth;
	}
}
