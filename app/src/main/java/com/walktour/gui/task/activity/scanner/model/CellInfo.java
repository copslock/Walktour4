package com.walktour.gui.task.activity.scanner.model;

/**
 * LTE邻区，显示取值用
 */
public class CellInfo {
	private float bandWidth;
	private int band;
	private int timeOffset;
	private int rbNum;

	public CellInfo(float bandWidth, int band, int rbNum, int timeOffset) {
		super();
		this.bandWidth = bandWidth;
		this.band = band;
		this.rbNum = rbNum;
		this.timeOffset = timeOffset;
	}

	/**
	 * 除以缩放比例
	 * @return
	 */
	public float getBandWidth() {
		if (bandWidth >= 1000) {
			return bandWidth / 1000;
		} else {
			return bandWidth;
		}
	}

	public void setBandWidth(float bandWidth) {
		this.bandWidth = bandWidth;
	}

	public int getBand() {
		return band;
	}

	public void setBand(int band) {
		this.band = band;
	}

	public int getRbNum() {
		return rbNum;
	}

	public void setRbNum(int rbNum) {
		this.rbNum = rbNum;
	}

	public int getTimeOffset() {
		return timeOffset;
	}

	public void setTimeOffset(int timeOffset) {
		this.timeOffset = timeOffset;
	}
}