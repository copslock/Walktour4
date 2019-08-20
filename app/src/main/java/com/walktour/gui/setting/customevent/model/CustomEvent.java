package com.walktour.gui.setting.customevent.model;

/**
 * 自定义事件对象
 * 
 * @author jianchao.wang
 *
 */
public abstract class CustomEvent implements Cloneable {
	/** 自定义事件类型：信令事件 */
	public final static int TYPE_MSG = 0;
	/** 自定义事件类型：参数事件 */
	public final static int TYPE_PARAMS = 1;
	/** 对比结果：大于 */
	public static final int COMPARE_L = 0;
	/** 对比结果：大于等于 */
	public static final int COMPARE_L_EQ = 1;
	/** 对比结果：小于 */
	public static final int COMPARE_S = 2;
	/** 对比结果：小于等于 */
	public static final int COMPARE_S_EQ = 3;
	/** 事件名，单独唯一 */
	private String mName = "";
	/** 事件名，编辑之前的名字 */
	private String mOldName = "";
	/** 图标路径 */
	private String mIconFilePath = "";
	/** 事件类型 */
	private final int mType;
	/** 是否告警 */
	private boolean isShowAlarm;
	/** 是否在地图显示 */
	private boolean isShowMap;
	/** 是否在图表显示 */
	private boolean isShowChart;
	/** 是否统计 */
	private boolean isShowTotal;
	/** 是否信令对比 */
	private boolean isCompare;

	public CustomEvent(int type) {
		this.mType = type;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getOldName() {
		return mOldName;
	}

	public void setOldName(String oldName) {
		mOldName = oldName;
	}

	public String getIconFilePath() {
		return mIconFilePath;
	}

	public void setIconFilePath(String iconFilePath) {
		mIconFilePath = iconFilePath;
	}

	public int getType() {
		return mType;
	}

	public boolean isShowAlarm() {
		return isShowAlarm;
	}

	public void setShowAlarm(boolean isShowAlarm) {
		this.isShowAlarm = isShowAlarm;
	}

	public boolean isShowMap() {
		return isShowMap;
	}

	public void setShowMap(boolean isShowMap) {
		this.isShowMap = isShowMap;
	}

	public boolean isShowChart() {
		return isShowChart;
	}

	public void setShowChart(boolean isShowChart) {
		this.isShowChart = isShowChart;
	}

	public boolean isShowTotal() {
		return isShowTotal;
	}

	public void setShowTotal(boolean isShowTotal) {
		this.isShowTotal = isShowTotal;
	}

	public boolean isCompare() {
		return isCompare;
	}

	public void setCompare(boolean compare) {
		isCompare = compare;
	}
}
