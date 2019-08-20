package com.walktour.gui.task.parsedata.xml.common;

import android.util.Xml;

import com.walktour.Utils.FileUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.task.parsedata.model.TaskGroupConfig;
import com.walktour.gui.task.parsedata.model.TestPlanConfig;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 通用版测试任务的XML数据解析与生成
 *
 * @author weirong.fan
 */
public class TaskXmlTools {
    /**
     * 日志标识
     */
    private static final String TAG = "TaskXmlTools";
    /**
     * 导入测试任务加载模式 0-覆盖加载
     **/
    public static final int LOADMODEL_REPLACE = 0;
    /**
     * 导入测试任务加载模式 1-追加加载
     **/
    public static final int LOADMODEL_APPEND = 1;
    /**
     * walktour整个测试任务对象
     */
    protected TestPlanConfig mTestPlanConfig = null;
    /**
     * 默认组名称,当没有组设置时,设一个默认的组ID.
     **/
    public static final String sDefaultGroupID = "G1";
    /**
     * 默认组名称,当没有组设置时,设一个默认的组名.
     **/
    public static final String sDefaultGroupName = "default";
    /**
     * 当前任务模板文件绝对路径
     */
    private String mFileName;

    /***
     * 获取整个测试计划
     *
     * @return 测试计划
     */
    public TestPlanConfig getTestPlanConfig() {
        return mTestPlanConfig;
    }

    /***
     * 初始化TestPlanConfig
     */
    private void initTestPlanConfig() {
        mTestPlanConfig = null;//回收
        mTestPlanConfig = new TestPlanConfig();
        mTestPlanConfig.getTestPlanInfo().setTestPlanFormatVersion("20141209");
        mTestPlanConfig.getTestPlanInfo().setLastUpdateTime("2015-12-24 13:54:49");
        this.writeXml();
    }

    /***
     * 解析XML数据到当前工程,返回当前XML任务列表
     *
     * @param fileName
     *            测试任务模板文件绝对路径
     * @param loadModel
     *            加载模式
     * @return 测试任务计划
     */
    public TestPlanConfig parseXml(String fileName, int loadModel) {
        if (StringUtil.isEmpty(fileName)) {
            return this.mTestPlanConfig;
        }
        this.mFileName = fileName;
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                initTestPlanConfig();
                return mTestPlanConfig;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            byte[] bytes = FileUtil.getBytesFromFile(file);
            String tagName = "";
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new ByteArrayInputStream(bytes), "UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();
                        if (tagName.equals("TestPlanConfig")) {
                            if (mTestPlanConfig == null || loadModel == LOADMODEL_REPLACE) {
                                // 覆盖导入或当前为空
                                mTestPlanConfig = null;
                                mTestPlanConfig = new TestPlanConfig();
                                mTestPlanConfig.parseXml(parser);
                            } else {
                                // 追加导入,以ID为准
                                TestPlanConfig newConfig = new TestPlanConfig();
                                newConfig.parseXml(parser);
                                List<TaskGroupConfig> newTaskGroups = newConfig.getTestSchemas().getTestSchemaConfig().getTaskGroups();
                                List<TaskGroupConfig> currentTaskGroups = this.getCurrenGroups();
                                for (TaskGroupConfig newGroup : newTaskGroups) {
                                    boolean isExistGroup = false;
                                    for (TaskGroupConfig currentGroup : currentTaskGroups) {
                                        if (newGroup.getGroupID().equals(currentGroup.getGroupID())) {
                                            isExistGroup = true;
                                            for (TaskModel newTask : newGroup.getTasks()) {
                                                boolean isExistTask = false;
                                                for (TaskModel currentTask : currentGroup.getTasks()) {
                                                    if (newTask.getTaskID().equals(currentTask.getTaskID())) {
                                                        isExistTask = true;
                                                        break;
                                                    }
                                                }
                                                if (!isExistTask) {
                                                    newTask.setTaskSequence(this.getCurrentTaskSequence(newGroup.getGroupID()));
                                                    currentGroup.getTasks().add(newTask);
                                                }
                                            }
                                        }
                                    }
                                    if (!isExistGroup) {
                                        currentTaskGroups.addAll(newTaskGroups);
                                    }
                                }
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception ex) {
            mTestPlanConfig = null;
            LogUtil.w(TAG, ex.getMessage());
        }
        return mTestPlanConfig;
    }

    /**
     * 保存测试计划到文件中
     */
    public void writeXml() {
        this.writeXml(this.mTestPlanConfig, this.mFileName);
    }

    /***
     * 供导出测试任务使用,将文件导出到制定目录下.
     *
     * @param filePath 保存的文件路径
     */
    public void writeXml(String filePath) {
        this.writeXml(this.mTestPlanConfig, filePath);
    }

    /***
     * 供导出测试任务使用,将制定对象写到制定目录下
     * @param testPlanConfig  测试对象
     * @param filePath 文件路径
     */
    public void writeXml(final TestPlanConfig testPlanConfig, String filePath) {
        try {
            XmlSerializer serializer = Xml.newSerializer();
            File file = new File(filePath);
            OutputStream out = new FileOutputStream(file);
            serializer.setOutput(out, "UTF-8");
            serializer.startDocument("UTF-8", true);
            serializer.startTag(null, "TestPlanConfig");
            if (null != testPlanConfig) {
                testPlanConfig.writeXml(serializer);
            }
            serializer.endTag(null, "TestPlanConfig");
            serializer.endDocument();
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /***
     * 添加组
     *
     * @param taskGroupConfig 任务组对象
     */
    public void addGroup(TaskGroupConfig taskGroupConfig) {
        taskGroupConfig.setGroupID(this.getCurrentGroupID(this.getCurrentSchemasID()));
        taskGroupConfig.setGroupSequence(this.getCurrentGroupSequence());
        for (int i = 0; i < taskGroupConfig.getTasks().size(); i++) {
            TaskModel taskModel = taskGroupConfig.getTasks().get(i);
            taskModel.setTaskID(taskGroupConfig.getGroupID() + "_T" + (i + 1));
            taskModel.setTaskSequence(i + 1);
        }
        this.getCurrenGroups().add(taskGroupConfig);
        this.writeXml(this.mFileName);
    }

    /***
     * 删除组
     *
     */
    public void deleteGroup(String groupID) {
        List<TaskGroupConfig> groupList = this.getCurrenGroups();
        for (TaskGroupConfig group : groupList) {
            if (group.getGroupID().equals(groupID)) {
                // 变为已删除
                group.setGroupStatus(TaskGroupConfig.GROUPSTATUS_0);
                // it.remove();
            }
        }
        this.writeXml();
    }

    /***
     * 删除组
     *
     */
    public void deleteAllGroup() {
        List<TaskGroupConfig> groupList = this.getCurrenGroups();
        for (TaskGroupConfig group : groupList) {
            // if (task.getGroupID().equals(groupID)) {
            // 变为已删除
            group.setGroupStatus(TaskGroupConfig.GROUPSTATUS_0);
            // it.remove();
            // }
        }
        this.writeXml();
    }
    /***
     * 删除组里的所有任务
     *@param  groupID 组ID
     * @param fromType
     *            任务来源 0:自建1:下发
     */
    public void deleteGroupTasks(String groupID, int fromType) {
        List<TaskGroupConfig> groupList = this.getTestPlanConfig().getTestSchemas().getTestSchemaConfig().getTaskGroups();
        for (TaskGroupConfig group : groupList) {
            if (group.getGroupID().equals(groupID)) {
                for (int i = 0; i < group.getTasks().size(); i++) {
                    TaskModel task = group.getTasks().get(i);
                    if (task.getFromType() == fromType) {
                        group.getTasks().remove(i);
                        i--;
                    }
                }
            }
        }
    }
    /***
     * 通过组ID获取组信息
     *
     * @param groupID 组ID
     * @return 组信息
     */
    public TaskGroupConfig fetchGroup(String groupID) {
        List<TaskGroupConfig> groupList = this.getCurrenGroups();
        for (TaskGroupConfig group : groupList) {
            if (group.getGroupID().equals(groupID)) {
                return group;
            }
        }
        return null;
    }

    /***
     * 通过组名称获取组信息
     *
     * @param groupName 组名称
     * @return 组信息
     */
    public TaskGroupConfig fetchGroupByName(String groupName) {
        List<TaskGroupConfig> groupList = this.getCurrenGroups();
        for (TaskGroupConfig group : groupList) {
            if (group.getGroupName().equals(groupName)) {
                return group;
            }
        }
        return null;
    }

    /***
     * 通过组名获取组内所有测试任务信息
     *
     * @param fromType
     *            任务来源 0:自建1:下发
     * @return 任务列表
     */
    public List<TaskModel> fetchTasks(String groupID, int fromType) {
        List<TaskModel> listTask = new ArrayList<TaskModel>();
        List<TaskGroupConfig> groupList = this.getCurrenGroups();
        for (TaskGroupConfig group : groupList) {
            if (StringUtil.isEmpty(groupID) || group.getGroupID().equals(groupID)) {
                List<TaskModel> tasks = group.getTasks();
                // 如果组名为空,直接返回第一组的信息，其他组忽略.
                for (TaskModel task : tasks) {
                    if (task.getFromType() == fromType) {
                        listTask.add(task);
                    }
                }
                break;
            }
        }
        return listTask;
    }

    /**
     * 获取当前的任务组列表
     *
     * @return 任务组列表
     */
    public List<TaskGroupConfig> getCurrenGroups() {
        return this.mTestPlanConfig.getTestSchemas().getTestSchemaConfig().getTaskGroups();
    }

    /**
     * 把所有任务组和组里的任务都设置成未勾选状态
     */
    public boolean uncheckedAllGroups() {
        boolean isFlag = false;
        List<TaskGroupConfig> groupList = this.getCurrenGroups();
        for (TaskGroupConfig group : groupList) {
            group.setCheck(false);
            for (TaskModel task : group.getTasks()) {
                task.setCheck(false);
            }
            isFlag = true;
        }
        this.writeXml();
        return isFlag;
    }

    /***
     * 检索是否存在相同的组信息,不能新建default组
     *
     * @param groupName
     *            组名
     * @return 是否存在相同组名
     */
    public boolean existGroup(String groupName) {
        boolean isFlag = false;
        List<TaskGroupConfig> groupList = this.getCurrenGroups();
        for (TaskGroupConfig group : groupList) {
            if (group.getGroupName().equals(groupName) || groupName.equals(sDefaultGroupName)) {
                isFlag = true;
                break;
            }
        }
        return isFlag;
    }

    /**
     * 添加测试任务
     *
     * @param groupName  祖名
     * @param taskConfig 测试任务
     * @return 是否添加成功
     */
    public boolean addTask(String groupName, TaskModel taskConfig) {
        boolean isFlag = false;
        List<TaskGroupConfig> groupList = this.getCurrenGroups();
        for (TaskGroupConfig group : groupList) {
            if (group.getGroupName().equals(groupName)) {
                group.getTasks().add(taskConfig);
                this.writeXml();
                isFlag = true;
                break;
            }
        }
        return isFlag;
    }

    /**
     * 删除测试任务
     *
     * @param groupName  祖名
     * @param taskConfig 测试任务
     * @return 是否添加成功
     */
    public boolean deleteTask(String groupName, TaskModel taskConfig) {
        boolean isFlag = false;
        List<TaskGroupConfig> groupList = this.getCurrenGroups();
        for (TaskGroupConfig group : groupList) {
            if (group.getGroupName().equals(groupName)) {
                group.getTasks().remove(taskConfig);
                this.writeXml();
                isFlag = true;
                break;
            }
        }
        return isFlag;
    }

    /**
     * 删除测试任务
     *
     * @param groupName 祖名
     * @return 是否添加成功
     */
    public boolean deleteTask(String groupName, List<TaskModel> taskModels) {
        boolean isFlag = false;
        List<TaskGroupConfig> groupList = this.getCurrenGroups();
        for (TaskGroupConfig group : groupList) {
            if (group.getGroupName().equals(groupName)) {
                for (TaskModel taskConfig : taskModels) {
                    group.getTasks().remove(taskConfig);
                }
                isFlag = true;
                this.writeXml();
                break;
            }
        }
        return isFlag;
    }

    /**
     * 更新测试任务
     *
     * @param groupName 组名
     * @return 是否更新成功
     */
    public boolean updateTask(String groupName, TaskModel oldT, TaskModel newT) {
        boolean isFlag = false;
        List<TaskGroupConfig> groupList = this.getCurrenGroups();
        for (TaskGroupConfig group : groupList) {
            if (group.getGroupName().equals(groupName)) {
                for (TaskModel taskM : group.getTasks()) {
                    if (taskM.equals(oldT)) {
                        // 这行代码需要检查,是否能真更新集合中的对象
                        taskM = newT;
                        isFlag = true;
                        break;
                    }
                }
                this.writeXml();
                break;
            }
        }
        return isFlag;
    }

    /**
     * 检索组内是否存在相同任务名的任务
     *
     * @param groupName 组名
     * @param taskName  测试任务名
     * @return 任务是否存在
     */
    public boolean existTask(String groupName, String taskName) {
        boolean isFlag = false;
        List<TaskGroupConfig> groupList = this.getCurrenGroups();
        for (TaskGroupConfig group : groupList) {
            if (group.getGroupName().equals(groupName)) {
                for (TaskModel task : group.getTasks()) {
                    if (task.getTaskName().equals(taskName)) {
                        isFlag = true;
                        break;
                    }
                }
                break;
            }
        }
        return isFlag;
    }

    /**
     * 获取所有已选择的分组信息,在未分组的情况下，无论选择已否默认返回default分组
     * <p>
     * 组内的测试任务都为已选择的非历史任务
     * <p>
     * 同时删除组内无任务的组
     *
     * @return 任务组
     */
    public List<TaskGroupConfig> getAllSelectGroup() {
        List<TaskGroupConfig> groupsSelect = new LinkedList<TaskGroupConfig>();
        boolean isGroup = WalktourApplication.isExitGroup();
        List<TaskGroupConfig> groups = this.getTestPlanConfig().getTestSchemas().getTestSchemaConfig().getTaskGroups();
        if (isGroup) {//存在组
            for (TaskGroupConfig group : groups) {
                //已选择的非历史组
                if (group.isCheck() && group.getGroupStatus() != TaskGroupConfig.GROUPSTATUS_0) {
                    for (TaskModel t : group.getTasks()) {
                        if (t.isCheck()) {//存在勾选任务就
                            groupsSelect.add(group);
                            break;
                        }
                    }
                }
            }
        } else {//不存在组,直接获取default组
            for (TaskGroupConfig group : groups) {
                //直接返回default组，且必须为非历史组
                if (group.getGroupName().equals(sDefaultGroupName)) {
                    for (TaskModel t : group.getTasks()) {
                        if (t.isCheck()) {//存在勾选任务就
                            groupsSelect.add(group);
                            break;
                        }
                    }
                }
            }
        }
        return groupsSelect;
    }

    /***
     * 获取当前最新的Schemas ID. SchemasID为：S9876543210
     *
     * @return 当前的计划ID
     */
    public String getCurrentSchemasID() {
        String schemasID = mTestPlanConfig.getTestSchemas().getTestSchemaConfig().getSchemaID();
        if (schemasID.equals("")) {
            schemasID = "S" + WalktourApplication.deviceIMEI;
            mTestPlanConfig.getTestSchemas().getTestSchemaConfig().setSchemaID(schemasID);
        }
        return schemasID;
    }

    /***
     * 获取当前最新的Group ID. 分组ID为：AS9876543210_G3122
     *
     * @return 当前的任务组ID
     */
    private String getCurrentGroupID(String schemasID) {
        List<TaskGroupConfig> groupList = this.getCurrenGroups();
        String groupID = "";
        if (null == groupList || groupList.isEmpty()) {
            // 从2开始编号,有个默认组
            groupID = schemasID + "_" + "G2";
        } else {
            int maxID = 0;
            for (TaskGroupConfig group : groupList) {
                groupID = group.getGroupID().split("_")[1];
                int curID = stringToInt(groupID.substring(1));
                if (curID > maxID) {
                    maxID = curID;// 获取最大的ID
                }
            }
            groupID = schemasID + "_G" + (maxID + 1);
        }
        return groupID;
    }

    /**
     * 获取当前的最新的SequenceID
     *
     * @return 任务组序列号
     */
    private int getCurrentGroupSequence() {
        List<TaskGroupConfig> groupList = this.getCurrenGroups();
        // 默认组ID为0,所以从1开始
        int maxID = 1;
        for (TaskGroupConfig group : groupList) {
            if (group.getGroupSequence() > maxID) {
                maxID = group.getGroupSequence();
            }
        }
        return maxID + 1;
    }

    /***
     * 获取当前最新的Task Sequence
     *
     * @param groupID 任务组ID
     * @return 任务序号
     */
    public int getCurrentTaskSequence(String groupID) {
        List<TaskGroupConfig> groupList = this.getCurrenGroups();
        int maxID = 0;
        for (TaskGroupConfig group : groupList) {
            if (group.getGroupID().equals(groupID)) {
                List<TaskModel> taskList = group.getTasks();
                if (taskList != null && !taskList.isEmpty()) {
                    for (TaskModel task : taskList) {
                        if (task.getTaskSequence() > maxID) {
                            maxID = task.getTaskSequence();
                        }
                    }
                }
            }
        }
        return maxID + 1;
    }

    /***
     * 获取当前最新的Task ID. 测试任务ID为：AS9876543210_G3122_T4544
     *
     * @return 任务ID
     */
    public String getCurrentTaskID(String groupID) {
        List<TaskGroupConfig> groupList = this.getCurrenGroups();
        String taskID = "";
        for (TaskGroupConfig group : groupList) {
            if (group.getGroupID().equals(groupID)) {
                List<TaskModel> taskList = group.getTasks();
                List<TaskModel> taskListAll = new ArrayList<>();
                if (null == taskList || taskList.isEmpty()) {
                    taskID = groupID + "_T1";
                } else {
                    int maxID = 0;
                    for (TaskModel task : taskList) {
                        taskListAll.add(task);
                        if (task.getTaskType().equals(WalkStruct.TaskType.MultiRAB.name())) { // 需要将并发子业务的ID加进去
                            ArrayList<TaskModel> rabTaskList = ((TaskRabModel) task).getTaskModel();
                            if (rabTaskList != null && rabTaskList.size() != 0) {
                                taskListAll.addAll(rabTaskList);
                            }
                        }
                    }
                    for (TaskModel taskModel : taskListAll) {
                        taskID = taskModel.getTaskID().split("_")[2];
                        int curID = stringToInt(taskID.substring(1));
                        if (curID > maxID) {
                            maxID = curID;// 获取最大的ID
                        }
                    }
                    taskID = groupID + "_T" + (maxID + 1);
                }
            }
        }
        return taskID;
    }

    /**
     * 字符串转整数
     *
     * @param value 字符串
     * @return 整数
     */
    private int stringToInt(String value) {
        if (null == value)
            return 0;
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

}
