package com.walktour.gui.workorder.hw.model;

import com.walktour.gui.task.parsedata.model.base.TaskModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试任务组
 * 
 * @author jianchao.wang
 *
 */
public class TestTaskGroup {
	/** 任务组ID */
	private long id;
	/** 任务组序号 */
	private int sequence;
	/** 任务组重复次数 */
	private int repeatCount;
	/** 任务组间间隔时长(秒) */
	private int interval;
	/** 任务组间是否断开网络 */
	private boolean disconnetNetwork = false;
	/** 任务列表 */
	private List<TaskModel> taskList = new ArrayList<TaskModel>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	public List<TaskModel> getTaskList() {
		return taskList;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public boolean isDisconnetNetwork() {
		return disconnetNetwork;
	}

	public void setDisconnetNetwork(boolean disconnetNetwork) {
		this.disconnetNetwork = disconnetNetwork;
	}
}
