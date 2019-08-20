package com.dingli.seegull.model;

/**
 * 频点model类,两个参数 bandCode与channel
 *
 * @author zhihui.lian
 *
 */
public class ChannelModel {
	/** 对象ID */
	private long id;
	/** 频段编号 */
	private int bandCode;
	/** 起始频点，如果起始频点与终支频点相等则只有一个频点 */
	private int startChannel;
	/** 终止频点 */
	private int endChannel;

	public ChannelModel(){
		this.id = System.currentTimeMillis();
	}
	public long getId() {
		return id;
	}

	public int getBandCode() {
		return bandCode;
	}

	public void setBandCode(int bandCode) {
		this.bandCode = bandCode;
	}

	public int getStartChannel() {
		return startChannel;
	}

	public void setStartChannel(int startChannel) {
		this.startChannel = startChannel;
	}

	public int getEndChannel() {
		return endChannel;
	}

	public void setEndChannel(int endChannel) {
		this.endChannel = endChannel;
	}

	@Override
	public String toString() {
		return "ChannelModel [bandCode=" + bandCode + ", startChannel=" + startChannel + ", endChannel=" + endChannel + "]";
	}


}
