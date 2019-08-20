package com.walktour.gui.setting.msgfilter.model;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * 信令过滤设置对象
 * 
 * @author jianchao.wang
 * 
 */
public class MsgFilterSetModel {
	/** 对象类型：网络类型 一级菜单 */
	public static final int TYPE_NET_TYPE = 0;
	/** 对象类型：网络子类型 二级菜单 */
	public static final int TYPE_NET_SUB_TYPE = 1;
	/** 对象类型：信令明细 三级菜单 */
	public static final int TYPE_MSG_DETAIL = 2;
	/** 勾选状态：无勾选 */
	public static final int CHECKED_NO = 0;
	/** 勾选状态：半勾选 */
	public static final int CHECKED_HALF = 2;
	/** 勾选状态：已勾选 */
	public static final int CHECKED_YES = 1;
	/** 对象代码 */
	private String code = "";
	/** 对象名称 */
	private String name = "";
	/** 对象类型 */
	private int type;
	/** 是否在列表生效 */
	private boolean isShowList = true;
	/** 是否在地图生效 */
	private boolean isShowMap = true;
	/** 显示颜色 */
	private int color = Color.LTGRAY;
	/** 勾选状态 */
	private int checked = CHECKED_YES;
	/** 是否更改勾选状态 */
	private boolean isChangeChecked = false;
	/** 是否更改颜色 */
	private boolean isChangeColor = false;
	/** 是否更改列表显示 */
	private boolean isChangeShowList = false;
	/** 是否更改地图显示 */
	private boolean isChangeShowMap = false;
	/** 父对象 */
	private MsgFilterSetModel parent;
	/** 子对象 */
	private List<MsgFilterSetModel> childList = new ArrayList<MsgFilterSetModel>();

	public MsgFilterSetModel(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isShowList() {
		return isShowList;
	}

	public void setShowList(boolean isShowList) {
		if (this.isShowList != isShowList) {
			this.isChangeShowList = true;
		}
		this.isShowList = isShowList;
	}

	public boolean isShowMap() {
		return isShowMap;
	}

	public void setShowMap(boolean isShowMap) {
		if (this.isShowMap != isShowMap) {
			this.isChangeShowMap = true;
		}
		this.isShowMap = isShowMap;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		if (color == 0)
			color = Color.parseColor("#333333");
		if (this.color != color) {
			this.color = color;
			this.isChangeColor = true;
		}
	}

	public int getChecked() {
		return checked;
	}

	public void setChecked(int checked) {
		if (this.checked != checked) {
			this.checked = checked;
			this.isChangeChecked = true;
		}
	}

	public boolean isChangeChecked() {
		return isChangeChecked;
	}

	public void setChangeChecked(boolean isChangeChecked) {
		this.isChangeChecked = isChangeChecked;
	}

	public boolean isChangeColor() {
		return isChangeColor;
	}

	public void setChangeColor(boolean isChangeColor) {
		this.isChangeColor = isChangeColor;
	}

	public MsgFilterSetModel getParent() {
		return parent;
	}

	public void setParent(MsgFilterSetModel parent) {
		this.parent = parent;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<MsgFilterSetModel> getChildList() {
		return childList;
	}

	public boolean isChangeShowList() {
		return isChangeShowList;
	}

	public void setChangeShowList(boolean isChangeShowList) {
		this.isChangeShowList = isChangeShowList;
	}

	public boolean isChangeShowMap() {
		return isChangeShowMap;
	}

	public void setChangeShowMap(boolean isChangeShowMap) {
		this.isChangeShowMap = isChangeShowMap;
	}

	@Override
	public boolean equals(Object o) {
		if (super.equals(o))
			return true;
		if (o != null && o instanceof MsgFilterSetModel) {
			MsgFilterSetModel obj = (MsgFilterSetModel) o;
			return this.code.equals(obj.code);
		}
		return false;
	}

}
