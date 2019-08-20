package com.walktour.gui.task.parsedata.model;

import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

/***
 * 任务执行时间间隔
 * 
 * 
 * @author weirong.fan
 *
 */
public class TimeDuration extends TaskBase{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 3460577585885898317L;
	/** 是否是否指定开始时间 */
	private boolean isCheck = false;
	/** 任务执行时间间隔 */
	private TaskExecuteDuration taskExecuteDuration = new TaskExecuteDuration();

	/**获得是否指定开始时间*/
	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public TaskExecuteDuration getTaskExecuteDuration() {
		return taskExecuteDuration;
	}

	/**
	 * 解析 TimeDuration
	 * 
	 * @param parser
	 * @param timeDuration
	 * @throws Exception
	 */
	public void parseXml(XmlPullParser parser) throws Exception {
		int eventType = parser.getEventType();
		String tagName = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				tagName = parser.getName();
				break;
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if (tagName.equals("TaskExecuteDuration")) {
					taskExecuteDuration.parseXml(parser);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("TimeDuration")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "TimeDuration");
		this.writeAttribute(serializer, "IsCheck", isCheck);
		if (null != taskExecuteDuration)
			taskExecuteDuration.writeXml(serializer);
		serializer.endTag(null, "TimeDuration");
	}
}
