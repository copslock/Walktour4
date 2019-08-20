package com.walktour.gui.task.parsedata.model.base;
import com.google.gson.annotations.SerializedName;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.Map;
/**
 * 测试任务配置基类
 * 
 * @author weirong.fan
 *
 */
public abstract class TaskConfig extends TaskBase {
	private static final long serialVersionUID = 867471615318263127L;
	/** 0=已删除的历史任务 */
	public static final int TASKSTATUS_0 = 0;
	/** 1=当前已勾选测试任务 **/
	public static final int TASKSTATUS_1 = 1;
	/** 2=当前未勾选测试任务 **/
	public static final int TASKSTATUS_2 = 2;
	/** 任务来源：自建 */
	public static final int FROM_TYPE_SELF = 0;
	/** 任务来源：下载 */
	public static final int FROM_TYPE_DOWNLOAD = 1;
	/** 是否选择 */
	@SerializedName("isCheck")
	private boolean isCheck = false;
	/** 测试任务类型 */
	@SerializedName("taskType")
	private String taskType;
	/** 测试任务ID */
	@SerializedName("taskID")
	private String taskID;
	/** 测试任务序列号 */
	@SerializedName("taskSequence")
	private int taskSequence = 0;
	/** 测试任务名称 */
	@SerializedName("taskName")
	private String taskName = "";
	/** 测试任务内循环测试 */
	@SerializedName("repeat")
	private int repeat = 0;
	/** 是否无限循环 **/
	@SerializedName("infinite")
	private boolean infinite = false;
	/** 测试任务时间间隔 */
	@SerializedName("interVal")
	private int interVal = 0;
	/** 0=已删除的历史任务 1=当前已勾选测试任务 2=当前未勾选测试任务 **/
	@SerializedName("taskStatus")
	private int taskStatus = TASKSTATUS_2;
	/** 并发业务使用,表明并发是否可用 ***/
	@SerializedName("isAvailable")
	private boolean isAvailable = false;
	/** 并发业务使用,表明相对时间 ***/
	@SerializedName("parallelStartAfterDelay")
	private int parallelStartAfterDelay = 50;
	/** 并发业务使用,表明绝对时间 ***/
	@SerializedName("parallelStartAtTime")
	private String parallelStartAtTime = "";
	/** 任务来源 0 自建任务 1 下发任务 */
	@SerializedName("fromType")
	private int fromType = FROM_TYPE_SELF;
	public boolean isCheck() {
		return isCheck;
	}
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	public String getTaskID() {
		return taskID;
	}
	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}
	public int getTaskSequence() {
		return taskSequence;
	}
	public void setTaskSequence(int taskSequence) {
		this.taskSequence = taskSequence;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public int getRepeat() {
		return repeat;
	}
	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}
	public int getInterVal() {
		return interVal;
	}
	public void setInterVal(int interVal) {
		this.interVal = interVal;
	}
	public int getTaskStatus() {
		return taskStatus;
	}
	public void setTaskStatus(int taskStatus) {
		this.taskStatus = taskStatus;
	}
	public boolean isAvailable() {
		return isAvailable;
	}
	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
	public int getParallelStartAfterDelay() {
		return parallelStartAfterDelay;
	}
	public void setParallelStartAfterDelay(int parallelStartAfterDelay) {
		this.parallelStartAfterDelay = parallelStartAfterDelay;
	}
	public String getParallelStartAtTime() {
		return parallelStartAtTime;
	}
	public void setParallelStartAtTime(String parallelStartAtTime) {
		this.parallelStartAtTime = parallelStartAtTime;
	}
	public boolean isInfinite() {
		return infinite;
	}
	public void setInfinite(boolean infinite) {
		this.infinite = infinite;
	}
	/**
	 * 解析各测试任务公共属性
	 * 
	 * @param parser
	 * @param map
	 * @throws Exception
	 */
	public void parsrXmlPublic(XmlPullParser parser, Map<String, String> map) throws Exception {
		int eventType = parser.getEventType();
		String tagName = ""; 
		if(eventType != XmlPullParser.END_DOCUMENT){ 
			switch (eventType) {
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if (tagName.equals("TaskID")) {
					this.setTaskID(parser.nextText());
				} else if (tagName.equals("TaskSequence")) {
					this.setTaskSequence(stringToInt(parser.nextText()));
				} else if (tagName.equals("TaskName")) {
					this.setTaskName(parser.nextText());
				} else if (tagName.equals("TaskRepeatCount")) {
					this.setRepeat(stringToInt(parser.nextText()));
				} else if (tagName.equals("TaskStatus")) {
					this.setTaskStatus(stringToInt(parser.nextText()));
				} else if (tagName.equals("Interval")) {
					this.setInterVal(stringToInt(parser.nextText()) / 1000);
				} else if (tagName.equals("Infinite")) {
					this.setInfinite(stringToBool(parser.nextText()));
				} else if (tagName.equals("ParallelStartConditon")) {
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						String attName = parser.getAttributeName(i);
						if (attName.equals("IsAvailable")) {
							this.setAvailable(stringToBool(parser.getAttributeValue(i)));
						}
					}
				} else if (tagName.equals("ParallelStartAfterDelay")) {
					this.setParallelStartAfterDelay(stringToInt(parser.nextText()) / 1000);
				} else if (tagName.equals("ParallelStartAtTime")) {
					this.setParallelStartAtTime(parser.nextText());
				} else if (tagName.equals("FromType")) {
					this.setFromType(stringToInt(parser.nextText()));
				}
				break;
 
			} 
		}
		for (Map.Entry<String, String> entry : map.entrySet()) {
			tagName = entry.getKey();
			if (tagName.equals("TaskID")) {
				this.setTaskID(entry.getValue());
			} else if (tagName.equals("TaskSequence")) {
				this.setTaskSequence(stringToInt(entry.getValue()));
			} else if (tagName.equals("TaskName")) {
				this.setTaskName(entry.getValue());
			} else if (tagName.equals("TaskRepeatCount")) {
				this.setRepeat(stringToInt(entry.getValue()));
			} else if (tagName.equals("Interval")) {
				this.setInterVal(stringToInt(entry.getValue()));
			} else if (tagName.equals("IsAvailable")) {
				this.setAvailable(stringToBool(entry.getValue()));
			} else if (tagName.equals("ParallelStartAfterDelay")) {
				this.setParallelStartAfterDelay(stringToInt(entry.getValue()));
			} else if (tagName.equals("ParallelStartAtTime")) {
				this.setParallelStartAtTime(entry.getValue());
			}else if (tagName.equals("Infinite")) {
				this.setInfinite(stringToBool(entry.getValue()));
			} else if (tagName.equals("TaskStatus")) {
				// 此字段只是对于android端有用,为Android端单独新增字段
				this.setTaskStatus(stringToInt(entry.getValue()));
			} else if (tagName.equals("FromType")) {
				this.setFromType(stringToInt(entry.getValue()));
			}
		}
	}
	/**
	 * 生成xml实际的测试任务类型
	 * 
	 * @return
	 */
	public abstract String getServerTaskType();
	/**
	 * 生成各测试任务公共属性
	 * 
	 * @param serializer
	 * @throws Exception
	 */
	public void writeXml(XmlSerializer serializer) throws Exception {
		writeTag(serializer, "TaskType", getServerTaskType());
		writeTag(serializer, "TaskID", this.taskID);
		writeTag(serializer, "TaskSequence", this.taskSequence);
		writeTag(serializer, "TaskName", this.taskName);
		writeTag(serializer, "TaskRepeatCount", this.repeat);
		writeTag(serializer, "TaskStatus", this.taskStatus);
		writeTag(serializer, "Interval", this.interVal * 1000);
		writeTag(serializer, "Infinite", this.infinite);
		writeTag(serializer, "FromType", this.fromType);
		serializer.startTag(null, "ParallelStartConditon");
		this.writeAttribute(serializer, "IsAvailable", this.isAvailable);
		writeTag(serializer, "ParallelStartAfterDelay", this.parallelStartAfterDelay * 1000);
		writeTag(serializer, "ParallelStartAtTime", this.parallelStartAtTime);
		serializer.endTag(null, "ParallelStartConditon");
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + interVal;
		result = prime * result + (isCheck ? 1231 : 1237);
		result = prime * result + repeat;
		result = prime * result + ((taskID == null) ? 0 : taskID.hashCode());
		result = prime * result + ((taskName == null) ? 0 : taskName.hashCode());
		result = prime * result + taskSequence;
		result = prime * result + taskStatus;
		result = prime * result + ((taskType == null) ? 0 : taskType.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaskConfig other = (TaskConfig) obj;
		if (interVal != other.interVal)
			return false;
		if (isCheck != other.isCheck)
			return false;
		if (repeat != other.repeat)
			return false;
		if (taskID == null) {
			if (other.taskID != null)
				return false;
		} else if (!taskID.equals(other.taskID))
			return false;
		if (taskName == null) {
			if (other.taskName != null)
				return false;
		} else if (!taskName.equals(other.taskName))
			return false;
		if (taskSequence != other.taskSequence)
			return false;
		if (taskStatus != other.taskStatus)
			return false;
		if (taskType == null) {
			if (other.taskType != null)
				return false;
		} else if (!taskType.equals(other.taskType))
			return false;
		return true;
	}

	public int getFromType() {
		return fromType;
	}

	public void setFromType(int fromType) {
		this.fromType = fromType;
	}


}
