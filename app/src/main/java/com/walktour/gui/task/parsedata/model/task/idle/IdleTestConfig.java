package com.walktour.gui.task.parsedata.model.task.idle;

import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

/***
 * IDLE 测试配置
 * 
 * @author weirong.fan
 *
 */
public class IdleTestConfig extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -8868942903352655486L;
	/** IDLE测试是否采集数据 */
	private boolean collectData=false;
	/** IDLE测试持续时间,默认值100秒 **/
	private int keepTime = 100;

	public boolean isCollectData() {
		return collectData;
	}

	public void setCollectData(boolean collectData) {
		this.collectData = collectData;
	} 

	public int getKeepTime() {
		return keepTime;
	}

	public void setKeepTime(int keepTime) {
		this.keepTime = keepTime;
	}

	public void parseXmlIdleTest(XmlPullParser parser) throws Exception {
		int eventType = parser.getEventType();
		String tagName = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				tagName = parser.getName();
				break;
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if (tagName.equals("CollectData")) {
					this.setCollectData(stringToBool(parser.nextText()));
				} else if (tagName.equals("Duration")) {
					this.setKeepTime(stringToInt(parser.nextText())/1000);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("IdleTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "IdleTestConfig");
		this.writeTag(serializer, "CollectData", collectData);
		this.writeTag(serializer, "Duration", keepTime*1000);
		serializer.endTag(null, "IdleTestConfig");
	}
}
