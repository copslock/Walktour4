package com.walktour.gui.task.parsedata.model.task.idle;

import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

/**
 * Task IDLE 测试,即空测试
 * 
 * @author weirong.fan
 *
 */
public class TaskEmptyModel extends TaskModel { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 278772067039026089L;
	/** Idle 测试配置 */
	private IdleTestConfig idleTestConfig=new IdleTestConfig();

	public TaskEmptyModel() {
		setTaskType(WalkStruct.TaskType.EmptyTask.toString());
		setTypeProperty(WalkCommonPara.TypeProperty_None);
		this.setRepeat(1);
	} 
	
	

	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 * 
	 * @return
	 */
	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());
		testTask.append("KeepTime =" + idleTestConfig.getKeepTime() + "\r\n");
		return testTask.toString();
	}
 
	public IdleTestConfig getIdleTestConfig() {
		return idleTestConfig;
	}
 

	/**
	 * 解析IDLE 测试
	 * 
	 * @param parser
	 * @param model
	 * @throws Exception
	 */
	public void parseXml(XmlPullParser parser,List<TaskModel> tasks,Map<String,String> map) throws Exception { 
		int eventType = parser.getEventType();
		String tagName = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				tagName = parser.getName();
				break;
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if (tagName.equals("IdleTestConfig")) { 
					idleTestConfig.parseXmlIdleTest(parser);
				} else {// 解析公共属性
					parsrXmlPublic(parser,map);
				}
				break;
			case XmlPullParser.END_TAG: 
				tagName = parser.getName();
				if (tagName.equals("TaskConfig")) {
					tasks.add(this);
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	@Override
	public void writeXml(XmlSerializer serializer)  throws Exception {
		super.writeXml(serializer);
		if(null!=idleTestConfig)
			idleTestConfig.writeXml(serializer);
	}
	
	@Override
	public String getServerTaskType() { 
		return WalkStruct.TaskType.EmptyTask.getXmlTaskType();
	}
}
