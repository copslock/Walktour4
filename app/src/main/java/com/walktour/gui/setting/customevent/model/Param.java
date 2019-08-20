package com.walktour.gui.setting.customevent.model;

/**
 * 参数对象
 * 
 * @author jianchao.wang
 *
 */
public class Param {
	/** 参数ID */
	public String id = "";
	/** 比较大小 */
	public int compare;
	/** 参数值 */
	public float value;
	/** 持续时间 */
	public int duration = 0;

	public Param() {

	}

	public Param(String id, int compare, float value) {
		this.id = id;
		this.compare = compare;
		this.value = value;
	}

	/**
	 * 函数功能：每秒执行一次此函数，以更新值和持续时间
	 * 
	 * @param value
	 */
	public void refreshValue(float currentValue) {
		switch (compare) {

		case CustomEvent.COMPARE_L:
			if (currentValue > value) {
				duration++;
			} else {
				duration = 0;
			}
			break;

		case CustomEvent.COMPARE_L_EQ:
			if (currentValue >= value) {
				duration++;
			} else {
				duration = 0;
			}
			break;

		case CustomEvent.COMPARE_S:
			if (currentValue < value) {
				duration++;
			} else {
				duration = 0;
			}
			break;

		case CustomEvent.COMPARE_S_EQ:
			if (currentValue <= value) {
				duration++;
			} else {
				duration = 0;
			}
			break;

		default:
			break;
		}
	}

	public String getComapreStr() {
		switch (compare) {
		case CustomEvent.COMPARE_L:
			return ">";
		case CustomEvent.COMPARE_L_EQ:
			return ">=";
		case CustomEvent.COMPARE_S:
			return "<";
		case CustomEvent.COMPARE_S_EQ:
			return "<=";
		default:
			return "";
		}
	}
}
