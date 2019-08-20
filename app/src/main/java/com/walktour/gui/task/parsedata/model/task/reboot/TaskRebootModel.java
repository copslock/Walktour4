package com.walktour.gui.task.parsedata.model.task.reboot;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

/**
 * 重启测试任务Model
 */
public class TaskRebootModel extends TaskModel {

    /** 重启 测试配置 */
    @SerializedName("rebootTestConfig")
    private RebootTestConfig rebootTestConfig=new RebootTestConfig();

    public TaskRebootModel() {
        setTaskType(WalkStruct.TaskType.REBOOT.toString());
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
        testTask.append("RebootTime =" + rebootTestConfig.getRebootTime() + "\r\n");
        return testTask.toString();
    }

    public RebootTestConfig getRebootTestConfig() {
        return rebootTestConfig;
    }

    /**
     * 解析IDLE 测试
     *
     * @param parser
     * @param model
     * @throws Exception
     */
    public void parseXml(XmlPullParser parser, List<TaskModel> tasks, Map<String,String> map) throws Exception {
        int eventType = parser.getEventType();
        String tagName = "";
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    tagName = parser.getName();
                    break;
                case XmlPullParser.START_TAG:
                    tagName = parser.getName();
                    if (tagName.equals("RebootTestConfig")) {
                        rebootTestConfig.parseXmlIdleTest(parser);
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
        if(null!=rebootTestConfig)
            rebootTestConfig.writeXml(serializer);
    }

    @Override
    public String getServerTaskType() {
        return WalkStruct.TaskType.REBOOT.getXmlTaskType();
    }
}
