package com.walktour.gui.task.parsedata.xml.btu.model;

import com.walktour.gui.task.parsedata.model.base.TaskModel;

import java.util.ArrayList;

/**
 * TestScheme
 * 对应平台上的测试方案
 * 2014-2-27 下午5:38:10
 * 
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
public class TestScheme {
	private int version=0;
	private String desc = "";
	private boolean enable = true;
	private int moudleNum = 1;
	
	private boolean isUsing = false;
	
	/**
	 * 开始执行日期，毫秒级
	 */
	private long beginDate;
	
	/**
	 * 结束日期(当日是最后一日),毫秒
	 */
	private long endDate ;
	
	/**
	 * 当日的开始时间
	 */
	private long beginTime;
	/**
	 * 当日的结束时间
	 */
	private long endTime;
	/**
	 * 外循环次数
	 */
	private int commandListRepeat = 1;
	
	/**
	 * 平台的command相当于Walktour的{@link TaskModel}
	 */
	private ArrayList<TaskModel> commandList = new ArrayList<>();

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public int getMoudleNum() {
		return moudleNum;
	}

	public void setMoudleNum(int moudleNum) {
		this.moudleNum = moudleNum;
	}

	public long getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(long beginDate) {
		this.beginDate = beginDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public long getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(long beginTime) {
		this.beginTime = beginTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public int getCommandListRepeat() {
		return commandListRepeat;
	}

	public void setCommandListRepeat(int commandListRepeat) {
		this.commandListRepeat = commandListRepeat;
	}

	public ArrayList<TaskModel> getCommandList() {
		return commandList;
	}

	public void setCommandList(ArrayList<TaskModel> commandList) {
		this.commandList = commandList;
	}

	public boolean isUsing() {
		return isUsing;
	}

	public void setUsing(boolean isUsing) {
		this.isUsing = isUsing;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
}
