package com.walktour.gui.task.parsedata.model;

import com.walktour.Utils.UtilsMethod;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

/***
 * 任务执行时间间隔
 * 
 * @author weirong.fan
 *
 */
public class TaskExecuteDuration extends TaskBase{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 4483416981130227649L;
	/** 开始时间 */
	private String StartTime = "";
	/** 时间间隔 */
	private String duration = "0";

	public String getStartTime() {
		return StartTime;
	}

	/**获得开始测试时间长整形值*/
	public long getStartTimeByLong(){
		if(!StartTime.equals("")){
			try{
				return UtilsMethod.sdFormatss.parse(UtilsMethod.yyyyMMddFormat.format(System.currentTimeMillis()) + " " + StartTime).getTime();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return System.currentTimeMillis();
	}
	
	public void setStartTime(String startTime) {
		StartTime = startTime;
	}

	/**获得持续时间长整形值*/
	public long getDurationByLong(){
		try{
			return Long.parseLong(duration);
		}catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	
	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

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
				if (tagName.equals("StartTime")) {
					this.setStartTime(parser.nextText());
				} else if (tagName.equals("Duration")) {
					this.setDuration(parser.nextText());
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("TaskExecuteDuration")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null,"TaskExecuteDuration");
		this.writeTag(serializer, "StartTime",this.StartTime);
		this.writeTag(serializer, "Duration",this.duration);
		serializer.endTag(null,"TaskExecuteDuration");
	}
}
