package com.walktour.gui.task.parsedata;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.TaskType;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.task.parsedata.model.TaskGroupConfig;
import com.walktour.gui.task.parsedata.model.TestPlanConfig;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.ftp.TaskFtpModel;
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;
import com.walktour.gui.task.parsedata.model.task.ott.TaskMultipleAppTestModel;
import com.walktour.gui.task.parsedata.model.task.wap.TaskWapPageModel;
import com.walktour.gui.task.parsedata.txt.TestPlan;
import com.walktour.gui.task.parsedata.xml.common.TaskXmlTools;
import com.walktour.model.TaskSetModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 任务列表解析和维护 匹配通用测试计划
 *
 * @author zhihui.lian
 */
public class TaskListDispose {
    /**
     * 日志标识
     */
    private static final String TAG = "TaskListDispose";
    /**
     * 文件存放路径
     */
    private String mFilePath = AppFilePathUtil.getInstance().getAppFilesDirectory();
    /**
     * 文件在手机/data/data/com.walktour.gui/files/目录下,这个文件是测试时的公共专用文件,保存时需自动更新到各自场景的文件中
     */
    private String mFileName = "tasklistx.xml";
    /**
     * 业务测试-测试任务文件
     */
    public static final String FILENAME_MANUAL = "tasklistx.xml";
    /**
     * 自动测试-测试任务文件
     */
//    public static final String FILENAME_AUTOTEST = "tasklistx_autotest.xml";
    /**
     * 多网测试-测试任务文件
     */
//    public static final String FILENAME_MULTITEST = "tasklistx_multitest.xml";
    /**
     * 地铁测试-测试任务文件
     */
//    public static final String FILENAME_METRO = "tasklistx_metro.xml";
    /**
     * 高铁测试-测试任务文件
     */
//    public static final String FILENAME_HIGHSPEEDRAIL = "tasklistx_highspeedrail.xml";
    /**
     * 单站验证测试-测试任务文件
     */
    public static final String FILENAME_SINGLESITE = "tasklistx_singlesite.xml";
    /**
     * 任务类型：历史任务
     */
    public static final int TASK_HISTORY = 0;
    /**
     * 任务类型：当前任务
     */
    public static final int TASK_CURRENT = 1;
    /**
     * 任务类型：所有任务
     */
    public static final int TASK_DISPLAY = 2;
    /**
     * 当前任务模版版本号
     */
    public static final int TASK_MODEL_VERSION = 3;
    /**
     * 通用测试计划对应字符串常量
     */
    public static final String PSCALLMODE = "By File";
    /**
     * 包含所有任务,已删除和未删除的
     **/
    private List<TaskModel> mTaskList = new ArrayList<>();
    /**
     * 任务设置对象
     */
    private TaskSetModel mTaskSetModel = null;
    /**
     * 唯一实例
     */
    private static TaskListDispose sInstance;
    /**
     * 测试任务模板文件解析类
     */
    private TaskXmlTools mTaskXmlTools;
    /**
     * 操作的当前组,无组时为默认组ID,所有的以ID为区分
     */
    private String mGroupID = TaskXmlTools.sDefaultGroupID;

    /**
     * 获取当前操作类实例
     *
     * @return 唯一实例
     */
    public static TaskListDispose getInstance() {
        if (sInstance == null) {
            sInstance = new TaskListDispose();
        }
        return sInstance;
    }

    /**
     * 根据任务组名称获得任务组
     *
     * @param groupName 任务组名称
     * @return 任务组
     */
    public TaskGroupConfig getGroup(String groupName) {
        for (TaskGroupConfig group : this.mTaskXmlTools.getCurrenGroups()) {
            if (group.getGroupName().equals(groupName))
                return group;
        }
        return null;
    }

    public String getGroupID() {
        return mGroupID;
    }

    public void setGroupID(String groupID) {
        this.mGroupID = groupID;
    }

    public void addTask(TaskModel taskModel) {
        taskModel.setTaskSequence(this.mTaskXmlTools.getCurrentTaskSequence(this.mGroupID));
        for (TaskGroupConfig group : this.mTaskXmlTools.getCurrenGroups()) {
            if (group.getGroupID().equals(this.mGroupID)) {
                this.mTaskXmlTools.addTask(group.getGroupName(), taskModel);
                break;
            }
        }
        this.mTaskXmlTools.writeXml();
    }

    /**
     * 默认构造器
     */
    private TaskListDispose() {
        this.mTaskXmlTools = new TaskXmlTools();
        this.mTaskXmlTools.parseXml(this.getFileName(), TaskXmlTools.LOADMODEL_REPLACE);
    }

    /***
     * 解析XML数据到当前工程,返回当前XML任务列表
     *
     * @param fileName
     *            测试任务模板文件绝对路径
     * @param loadModel
     *            加载模式
     */
    public TestPlanConfig parseXml(String fileName, int loadModel) {
        return this.mTaskXmlTools.parseXml(fileName, loadModel);
    }

    /**
     * 从XML重新加载
     */
    public void reloadFromXML() {
        try {
            this.mTaskXmlTools.parseXml(this.getFileName(), TaskXmlTools.LOADMODEL_REPLACE);
        } catch (Exception e) {
            LogUtil.w(TAG, "reloadFromXML() is error!\n" + e.getMessage() + "");
            e.printStackTrace();
        }
    }

    public TaskSetModel getTaskSetModel() {
        return mTaskSetModel;
    }

    public void setTaskSetModel(TaskSetModel model) {
        mTaskSetModel = model;
    }

    /**
     * 勾选指定的任务
     */
    public void checkTask() {
        //任务选择，组必须选择，在此统一处理
        List<TaskGroupConfig> groups = this.mTaskXmlTools.getCurrenGroups();
        for (TaskGroupConfig group : groups) {
            if (group.getGroupID().equals(this.mGroupID)) {
                boolean isAllNocheck = true;
                for (TaskModel task : group.getTasks()) {
                    if (task.getEnable() == TaskModel.TASKSTATUS_1) {
                        isAllNocheck = false;
                        break;
                    }
                }
                if (!isAllNocheck) {
                    group.setCheck(true);
                } else {
                    group.setCheck(false);
                }
            }
        }
    }

    /**
     * 获得当前的任务组列表
     *
     * @return 任务组列表
     */
    public List<TaskGroupConfig> getCurrentGroups() {
        return this.mTaskXmlTools.getCurrenGroups();
    }

    /**
     * 返回任务列表对象:包括正在使用的任务和历史任务
     *
     * @return 任务列表
     */
    public List<TaskModel> getTaskListArray() {
        return this.mTaskList;
    }

    /***
     * 获取未删除,可用的测试任务
     *
     * @return 任务列表
     */
    public List<TaskModel> getTaskListEnable() {
        List<TaskModel> taskList = new LinkedList<>();
        for (TaskModel model : this.mTaskList) {
            if (model.getEnable() != TaskModel.TASKSTATUS_0) {
                taskList.add(model);
            }
        }
        return taskList;
    }

    /**
     * 检查任务列表是否为空,非空返回true 判断是否存在已勾选的测试任务
     *
     * @return 是否为空
     */
    public boolean hasEnabledTask() {
        boolean hasEnabled = false;
        List<TaskGroupConfig> allSelectGroup = getAllSelectGroup();
        //当选中任务组长度大于0时表示当前有选中需要测试的任务
        if (allSelectGroup.size() > 0) {
            hasEnabled = true;
        }
        return hasEnabled;
    }

    /***
     * 获取所有已选择的分组信息,在未分组的情况下，无论选择已否默认返回default分组
     *
     * 组内的测试任务都为已选择的非历史任务
     *
     * 同时删除组内无任务的组
     *
     * @return 任务组列表
     */
    public List<TaskGroupConfig> getAllSelectGroup() {
        return this.mTaskXmlTools.getAllSelectGroup();
    }

    /***
     * 获取所有未删除组的所有已勾选的测试任务
     *
     * @return 任务列表
     */
    public List<TaskModel> getAllSelectedTask(int index) {
        List<TaskGroupConfig> groups = getAllSelectGroup();
        if (groups.size() > index) {
            List<TaskModel> allTasks=groups.get(index).getTasks();
            List<TaskModel> tasks=new LinkedList<TaskModel>();
            for(TaskModel taskModel:allTasks){
                if(taskModel.isCheck()&&taskModel.getTaskStatus()!=TaskModel.TASKSTATUS_0)
                    tasks.add(taskModel);
            }
            return tasks;
        }
        return null;
    }

    /**
     * 获取当前默认群组ID
     *
     * @return 群组ID
     */
    public String getDefaultGroupId() {
        return this.mTaskXmlTools.getCurrentSchemasID() + "_" + TaskXmlTools.sDefaultGroupID;
    }

    /**
     * 检查任务列表是否同是存在语音与数据业务
     */
    public synchronized boolean checkSerialCallData() {
        boolean hasCall = false;
        boolean hasData = false;
        for (TaskModel task : this.mTaskList) {
            if (task.getEnable() == TASK_CURRENT) {
                if (task.getTypeProperty() == WalkCommonPara.TypeProperty_Voice) {
                    hasCall = true;
                } else if (task.getTypeProperty() == WalkCommonPara.TypeProperty_Net || task.getTypeProperty() == WalkCommonPara.TypeProperty_Wap) {
                    hasData = true;
                }
                // 如果语音与数据同时存在，退出判断
                if (hasCall && hasData) {
                    break;
                }
            }
        }
        return hasCall && hasData;
    }

    /**
     * 检查当前要执行的任务列表是否数据与Wlan测试同时存在
     */
    public synchronized boolean checkSerialDataWlan() {
        boolean hasWlan = false;
        boolean hasData = false;
        for (TaskModel task : mTaskList) {
            if (task.getEnable() == TASK_CURRENT) {
                if (task.getTypeProperty() == WalkCommonPara.TypeProperty_Wlan) {
                    hasWlan = true;
                } else if (task.getTypeProperty() == WalkCommonPara.TypeProperty_Net || task.getTypeProperty() == WalkCommonPara.TypeProperty_Wap) {
                    hasData = true;
                }
                // 如果语音与数据同时存在，退出判断
                if (hasWlan && hasData) {
                    break;
                }
            }
        }
        return hasWlan && hasData;
    }

    /**
     * 是否是wlan测试，在保存任务的时候，需要进行判定，如果是WIFI测试，必须所有的业务都是才可以
     */
    public synchronized boolean isWlanTest() {
        if (this.mTaskList.isEmpty()) {
            List<TaskGroupConfig> groups = getAllSelectGroup();
            for (TaskGroupConfig group : groups) {
                List<TaskModel> tasks = group.getTasks();
                for (TaskModel task : tasks) {
                    if (task.isCheck()) {
                        this.mTaskList.add(task);
                    }
                }
            }
        }
        boolean flag = false;
        for (TaskModel task : mTaskList) {
            if (task.getEnable() == TASK_CURRENT) {
                if (task.getTypeProperty() == WalkCommonPara.TypeProperty_Wlan) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * 根据当前设置的任务对象列表实时转换成XML文件
     *
     * @param taskList 任务列表
     */
    public void setTaskListArray(List<TaskModel> taskList) {
        this.mTaskList = taskList;
        updateXML();
    }

    /**
     * 编辑或者修改XML,具体到每组
     */
    private void updateXML() {
        List<TaskGroupConfig> groupList = this.mTaskXmlTools.getCurrenGroups();
        if (StringUtil.isEmpty(this.mGroupID)) {
            // 如果祖名为空,直接更新第一组的信息
            if (!groupList.isEmpty()) {
                groupList.get(0).getTasks().clear();
                for (TaskModel taskModel : mTaskList) {
                    groupList.get(0).getTasks().add(taskModel);
                }
            }
            return;
        }
        for (int i = 0; i < groupList.size(); i++) {
            if (groupList.get(i).getGroupID().equals(this.mGroupID)) {
                groupList.get(i).getTasks().clear();
                for (TaskModel taskModel : mTaskList) {
                    groupList.get(i).getTasks().add(taskModel);
                }
                break;
            }
        }
        this.mTaskXmlTools.writeXml();
    }

    /**
     * 获得任务列表名称
     *
     * @param enable enable 0:历史任务,1:当前任务 ,23:所有任务
     * @return 任务列表名称
     */
    public String[] getTaskNames(int enable) {
        List<String> tasksNameList = new ArrayList<>();
        for (int i = 0; i < this.mTaskList.size(); i++) {
            TaskModel model = this.mTaskList.get(i);
            if (enable == 3 || model.getEnable() == enable || (enable == 1 && model.getEnable() == 2)) {
                tasksNameList.add(model.getTaskName());
            }
        }
        return tasksNameList.toArray(new String[tasksNameList.size()]);
    }

    /***
     * 获取历史组信息
     *
     * @return 任务组名
     */
    public String[] getHistoryGroup() {
        List<String> groupNames = new LinkedList<>();
        List<TaskGroupConfig> groups = this.mTaskXmlTools.getCurrenGroups();
        for (TaskGroupConfig group : groups) {
            if (group.getGroupStatus() == TaskGroupConfig.GROUPSTATUS_0) {
                groupNames.add(group.getGroupName());
            }
        }
        return groupNames.toArray(new String[groupNames.size()]);
    }

    /**
     * @return 当前正在使用的任务列表
     */
    public List<TaskModel> getCurrentTaskList() {
        return this.getCurrentTaskList(TaskModel.FROM_TYPE_SELF);
    }

    /**
     * @param fromType 任务来源 0:自建1:下发
     * @return 当前正在使用的任务列表
     */
    public List<TaskModel> getCurrentTaskList(int fromType) {
        List<TaskModel> taskModelList = new ArrayList<>();
//        if (null == mTaskList || mTaskList.isEmpty()) {
            mTaskList = this.mTaskXmlTools.fetchTasks(this.mGroupID, fromType);
//        }
        for (TaskModel task : mTaskList) {
            if (task.getEnable() == TASK_CURRENT || task.getEnable() == TASK_DISPLAY) {
                if (task.getFromType() == fromType) {
                    taskModelList.add(task);
                }
            }
        }
        return taskModelList;
    }

	/**
	 * 2018/11/13 czc: 当前测试任务是否需要辅助权限（eg.微信自动化测试）
	 */
	public boolean isCurrentTaskNeedAssitPermission(){
        if (this.mTaskList.isEmpty()) {
            List<TaskGroupConfig> groups = getAllSelectGroup();
            for (TaskGroupConfig group : groups) {
                List<TaskModel> tasks = group.getTasks();
                for (TaskModel task : tasks) {
                    if (task.isCheck()) {
                        this.mTaskList.add(task);
                    }
                }
            }
        }
		for (TaskModel taskModel : this.mTaskList) {
			if (taskModel.isCheck() &&
                    taskModel instanceof TaskMultipleAppTestModel){
				return true;
			}
		}
		return false;
	}
    /**
     * 2018/11/13 czc: 当前测试任务是否需要辅助权限（eg.微信自动化测试）
     */
    public boolean isOttTest(){
        if (this.mTaskList.isEmpty()) {
            List<TaskGroupConfig> groups = getAllSelectGroup();
            for (TaskGroupConfig group : groups) {
                List<TaskModel> tasks = group.getTasks();
                for (TaskModel task : tasks) {
                    if (task.isCheck()) {
                        this.mTaskList.add(task);
                    }
                }
            }
        }
        for (TaskModel taskModel : this.mTaskList) {
            if (taskModel instanceof TaskMultipleAppTestModel){
                return true;
            }
        }
        return false;
    }

    /**
     * 2019/1/9   当前测试任务是否需要辅助权限（小米8的视频通话）
     */
    public boolean isXiaoMi8VideoTest(){
        if (!Deviceinfo.getInstance().isXiaomi()){
            return false;
        }
        if (this.mTaskList.isEmpty()) {
            List<TaskGroupConfig> groups = getAllSelectGroup();
            for (TaskGroupConfig group : groups) {
                List<TaskModel> tasks = group.getTasks();
                for (TaskModel task : tasks) {
                    if (task.isCheck()) {
                        this.mTaskList.add(task);
                    }
                }
            }
        }
        for (TaskModel taskModel : this.mTaskList) {
            if (taskModel instanceof TaskInitiativeCallModel){
                if(((TaskInitiativeCallModel)taskModel).getCallMode()==1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 添加一个测试任务模型到任务列表顶端， : 判断任务名是否存在于任务列表中(包括当前任务和历史任务),是则删除原来的
     *
     * @param taskModel 一个任务模型
     */
    private void addTaskToTop(TaskModel taskModel) {
        for (int i = 0; i < mTaskList.size(); i++) {
            TaskModel model = mTaskList.get(i);
            if (model.getTaskName().equals(taskModel.getTaskName())) {
                mTaskList.remove(i);
                break;
            }
        }
        mTaskList.add(0, taskModel);
    }

    /**
     * 替换测试任务模型到任务列表中: 判断任务名是否存在于任务列表中(包括当前任务和历史任务),是则替换原来的
     *
     * @param taskModelList 多个任务模型
     */
    public void replaceTaskList(List<TaskModel> taskModelList) {
        this.replaceTaskList(taskModelList, TaskModel.FROM_TYPE_SELF);
    }

    /**
     * 替换测试任务模型到任务列表中: 判断任务名是否存在于任务列表中(包括当前任务和历史任务),是则替换原来的
     *
     * @param taskModelList 多个任务模型
     * @param fromType      0=自建，1=下发
     */
    public void replaceTaskList(List<TaskModel> taskModelList, int fromType) {
        if (fromType == TaskModel.FROM_TYPE_SELF) {
            // 把当前列表放入历史列表
            for (TaskModel task : mTaskList) {
                if (task.getFromType() == TaskModel.FROM_TYPE_SELF) {
                    task.setEnable(0);
                }
            }
        } else {
            for (int i = 0; i < mTaskList.size(); i++) {
                TaskModel model = mTaskList.get(i);
                if (model.getFromType() == TaskModel.FROM_TYPE_DOWNLOAD) {
                    mTaskList.remove(i);
                    i--;
                }
            }
        }
        if (taskModelList != null && !taskModelList.isEmpty()) {
            // 替换当前列表
            for (int l = taskModelList.size() - 1; l >= 0; l--) {
                TaskModel model = taskModelList.get(l);
                model.setEnable(1);
                addTaskToTop(model);
            }
        }
        // 更新到实际对象里去
        List<TaskGroupConfig> groups = this.mTaskXmlTools.getCurrenGroups();
        for (TaskGroupConfig group : groups) {
            if (group.getGroupID().equals(this.mGroupID)) {
                group.getTasks().clear();
                group.getTasks().addAll(mTaskList);
            }
        }
        // createTaskListXmlFile();
    }

    /**
     * 移动任务列表中某指定任务的位置 如果同时删除多个任务要从列表倒序删除
     *
     * @param typeId 1:上移,2:下移,3移除
     */
    public void move(int typeId, int position) {
        switch (typeId) {
            case 1:// 当前上移
                int from = getRealIndex(position);
                int to = getRealIndex(position - 1);
                if (from == -1 || to == -1)
                    return;
                TaskModel objFrom = this.mTaskList.get(from);
                TaskModel objTo = this.mTaskList.get(to);
                int index = objTo.getTaskSequence();
                objTo.setTaskSequence(objFrom.getTaskSequence());
                objFrom.setTaskSequence(index);
                this.mTaskList.set(from, objTo);
                this.mTaskList.set(to, objFrom);
                break;
            case 2: // 当前下移
                from = getRealIndex(position);
                to = getRealIndex(position + 1);
                if (from == -1 || to == -1)
                    return;
                objFrom = this.mTaskList.get(from);
                objTo = this.mTaskList.get(to);
                index = objTo.getTaskSequence();
                objTo.setTaskSequence(objFrom.getTaskSequence());
                objFrom.setTaskSequence(index);
                this.mTaskList.set(from, objTo);
                this.mTaskList.set(to, objFrom);
                break;
            case 3: // 当前删除
                this.mTaskList.get(getRealIndex(position)).setEnable(0);
                break;
            case 4: // 全部移除
                for (TaskModel task : this.mTaskList) {
                    task.setEnable(0);
                }
                break;
        }

        // 更新到实际对象里去
        List<TaskGroupConfig> groupList = this.mTaskXmlTools.getCurrenGroups();
        for (TaskGroupConfig group : groupList) {
            if (group.getGroupID().equals(this.mGroupID)) {
                group.getTasks().clear();
                group.getTasks().addAll(mTaskList);
            }
        }
        this.mTaskXmlTools.writeXml();

    }

    /**
     * 获取实际索引
     *
     * @param position 位置
     * @return 索引
     */
    private int getRealIndex(int position) {
        try {
            int indexFrom = 0;//实际的顺序
            for (int i = 0; i < this.mTaskList.size(); i++) {
                if (this.mTaskList.get(i).getEnable() != TaskModel.TASKSTATUS_0) {
                    if (indexFrom == position) {
                        return i;
                    }
                    indexFrom++;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取当前的文件名
     *
     * @return 文件名
     */
    public String getFileName() {
        return this.mFilePath + this.mFileName;
    }

    /***
     * 更新当前的场景的文件名
     * @param selectScene 选择的场景
     */
    public void updateCurrentFileName(WalkStruct.SceneType selectScene) {
        this.mFileName = FILENAME_MANUAL;//默认赋值业务测试
//        if (appModel.getSelectScene().equals(WalkStruct.SceneType.Auto)) {//自动测试
//            this.mFileName = this.FILENAME_AUTOTEST;
//        } else if (appModel.getSelectScene().equals(WalkStruct.SceneType.MultiTest)) {//多网测试
//            this.mFileName = this.FILENAME_MULTITEST;
//        } else if (appModel.getSelectScene().equals(WalkStruct.SceneType.Metro)) {//地铁测试
//			this.mFileName = this.FILENAME_METRO;
//        } else if (appModel.getSelectScene().equals(WalkStruct.SceneType.HighSpeedRail)) {//高铁测试
//			this.mFileName = this.FILENAME_HIGHSPEEDRAIL;
//        } else
        if (selectScene.equals(WalkStruct.SceneType.SingleSite)) {//高铁测试
            this.mFileName = FILENAME_SINGLESITE;
        }
    }

    /**
     * 移动任务列表中某指定任务的位置 如果同时删除多个任务要从列表倒序删除
     *
     * @param typeId 1:上移,2:下移,3移除
     * @param taskId 所在任务列表中的位置
     */
    public void rabMove(int typeId, int taskId, ArrayList<TaskModel> rabModel) {
        switch (typeId) { // 当前上移
            case 1:
                if (taskId == 0)
                    return;
                TaskModel obj2 = rabModel.get(taskId);
                TaskModel obj1 = rabModel.get(taskId - 1);
                int index = obj1.getTaskSequence();
                obj1.setTaskSequence(obj2.getTaskSequence());
                obj2.setTaskSequence(index);
                break;
            case 2: // 当前下移
                if (taskId == rabModel.size() - 1)
                    return;
                obj1 = rabModel.get(taskId);
                obj2 = rabModel.get(taskId + 1);
                index = obj1.getTaskSequence();
                obj1.setTaskSequence(obj2.getTaskSequence());
                obj2.setTaskSequence(index);
                break;
            case 3: // 当前删除
                rabModel.remove(taskId);
                break;
            case 4: // 全部移除
                for (int i = 0; i < rabModel.size(); i++) {
                    rabModel.remove(i);
                    i--;
                }
                break;
        }
        // createTaskListXmlFile();
    }

    /**
     * 删除多个任务
     *
     * @param taskIDList 要删除的任务ID列表
     */
    public void removeTasks(List<String> taskIDList) {
        for (int j = 0; j < taskIDList.size(); j++) {
            for (TaskModel task : mTaskList) {
                if (task.getTaskID().equals(taskIDList.get(j))) {
                    task.setEnable(0);
                }
            }
        }
        updateXML();
    }

    /**
     * 更新多个任务状态 当勾选的状态改变的时候，实时更新xml
     */
    public void isTestTasks(String taskID) {
        for (int i = 0; i < mTaskList.size(); i++) {
            TaskModel model = mTaskList.get(i);
            if (model.getTaskID().equals(taskID)) {
                this.mTaskList.remove(i);
                this.mTaskList.add(i, model);
            }
        }
        updateXML();
    }

    public void updateTaskListXmlFile(List<TaskModel> list) {
        mTaskList.clear();
        mTaskList.addAll(list);
        // 更新到实际对象里去
        List<TaskGroupConfig> groupList = this.mTaskXmlTools.getCurrenGroups();
        for (TaskGroupConfig group : groupList) {
            if (group.getGroupID().equals(this.mGroupID)) {
                group.getTasks().clear();
                group.getTasks().addAll(mTaskList);
            }
        }
        this.mTaskXmlTools.writeXml();
    }

    /**
     * 获取任务的描述
     *
     * @param taskModel 测试任务
     * @return 测试任务的描述, 例如语音测试中的"呼叫号码"
     */
    public String getDescrition(TaskModel taskModel) {
        String taskType = taskModel.getTaskType();
        if (taskType.equals(TaskType.InitiativeCall.toString())) {
            TaskInitiativeCallModel task = (TaskInitiativeCallModel) taskModel;
            return task.getCallNumber();
        } else if (taskType.equals(TaskType.PassivityCall.toString())) {
            return "";
        } else if (taskType.equals(TaskType.FTPDownload.toString()) || taskType.equals(TaskType.FTPUpload.toString())) {
            TaskFtpModel task = (TaskFtpModel) taskModel;
            return task.getRemoteFile();
        } else if (taskType.equals(TaskType.WapDownload.toString()) || taskType.equals(TaskType.WapLogin.toString()) || taskType.equals(TaskType.WapRefurbish.toString())) {
            TaskWapPageModel task = (TaskWapPageModel) taskModel;
            return task.getUrl();
        }
        return "";
    }

    /**
     * 写文件
     *
     * @param fileName 文件名称
     */
    public void writeXml(String fileName) {
        this.mTaskXmlTools.writeXml(fileName);
    }

    /**
     * 写文件
     *
     * @param testPlanConfig 测试计划
     * @param fileName       文件名称
     */
    public void writeXml(TestPlanConfig testPlanConfig, String fileName) {
        this.mTaskXmlTools.writeXml(testPlanConfig, fileName);
    }

    /**
     * 写文件
     */
    public void writeXml() {
        this.mTaskXmlTools.writeXml();
    }

    public void removeAllTask(){
        for (TaskModel task : this.mTaskList) {
            task.setEnable(0);
        }
        updateXML();
    }

    /**
     * 删除所有任务组
     */
    public void deleteAllGroup() {
        this.mTaskXmlTools.deleteAllGroup();
    }

    /**
     * 取消所有任务组的勾选
     */
    public boolean uncheckedAllGroups() {
        return this.mTaskXmlTools.uncheckedAllGroups();
    }

    /**
     * 是否存在任务组
     *
     * @param name 任务组名称
     * @return 是否存在
     */
    public boolean existGroup(String name) {
        return this.mTaskXmlTools.existGroup(name);
    }

    /**
     * 新增任务组
     *
     * @param taskGroupConfig 任务组
     */
    public void addGroup(TaskGroupConfig taskGroupConfig) {
        this.mTaskXmlTools.addGroup(taskGroupConfig);
    }

    /**
     * 删除任务组
     *
     * @param groupID 任务组ID
     */
    public void deleteGroup(String groupID) {
        this.mTaskXmlTools.deleteGroup(groupID);
    }

    public TestPlanConfig getTestPlanConfig() {
        return this.mTaskXmlTools.getTestPlanConfig();
    }

    /**
     * 设置当前的任务ID和任务序号
     *
     * @param task 任务对象
     */
    public void setCurrentTaskIdAndSequence(TaskModel task) {
        task.setTaskID(this.mTaskXmlTools.getCurrentTaskID(this.mGroupID));
        task.setTaskSequence(this.mTaskXmlTools.getCurrentTaskSequence(this.mGroupID));
    }

    /***
     * 获取从ipad下载下来的任务,默认为只有一个组
     *
     * @param taskList
     *            测试任务
     * @return 测试计划
     */
    public TestPlanConfig getTestPlanConfigFromAtu(List<TaskModel> taskList) {
        String defautID = this.mTaskXmlTools.getCurrentSchemasID() + "_" + TaskXmlTools.sDefaultGroupID;
        List<TaskGroupConfig> groups = this.mTaskXmlTools.getCurrenGroups();
        TaskGroupConfig currGroup = null;
        for (TaskGroupConfig group : groups) {
            if (group.getGroupID().equals(defautID)) {
                group.getTasks().clear();
                currGroup = group;
            }
        }
        if (taskList != null && !taskList.isEmpty()) {
            if (null == currGroup) {
                TaskGroupConfig groupConfig = new TaskGroupConfig();
                groupConfig.setGroupID(defautID);
                groupConfig.setGroupSequence(1);
                groupConfig.setCheck(true);
                groupConfig.setGroupName(TaskXmlTools.sDefaultGroupName);
                this.mTaskXmlTools.getCurrenGroups().add(groupConfig);
                int i = 0;
                for (TaskModel task : taskList) {
                    task.setTaskID(this.mTaskXmlTools.getCurrentTaskID(defautID));
                    task.setTaskSequence(++i);
                    groupConfig.getTasks().add(task);
                }
            } else {
                int i = 0;
                for (TaskModel task : taskList) {
                    task.setTaskID(this.mTaskXmlTools.getCurrentTaskID(defautID));
                    task.setTaskSequence(++i);
                    currGroup.getTasks().add(task);
                }
            }
        }
        // default组为选中,其他组及组内任务为非选中
        groups = this.mTaskXmlTools.getCurrenGroups();
        for (TaskGroupConfig group : groups) {
            if (!group.getGroupID().equals(defautID)) {
                group.setCheck(false);
                List<TaskModel> taskModelList = group.getTasks();
                for (TaskModel task : taskModelList) {
                    task.setCheck(false);
                }
            }
        }
        this.writeXml();
        return this.mTaskXmlTools.getTestPlanConfig();
    }

    /***
     * 获取从Fleet下载下来的任务
     *
     * @param rangeList 范围类别
     * @return 测试计划
     */
    public TestPlanConfig getTestPlanConfigFromFleet(List<TestPlan.TimeRange> rangeList) {
        this.mTaskXmlTools.getCurrenGroups().clear();
        if (rangeList != null && !rangeList.isEmpty()) {
            String defautID = this.mTaskXmlTools.getCurrentSchemasID() + "_" + TaskXmlTools.sDefaultGroupID;
            if (rangeList.size() == 1) {
                TaskGroupConfig groupConfig = new TaskGroupConfig();
                groupConfig.setGroupID(defautID);
                groupConfig.setGroupSequence(1);
                groupConfig.setCheck(true);
                groupConfig.setGroupName(TaskXmlTools.sDefaultGroupName);
                String startTime = rangeList.get(0).getStartTime();
                String cotinueTime = rangeList.get(0).getContinuousTime();
                groupConfig.getTimeDuration().setCheck(true);
                groupConfig.getTimeDuration().getTaskExecuteDuration().setStartTime(startTime);
                groupConfig.getTimeDuration().getTaskExecuteDuration().setDuration(cotinueTime);
                this.mTaskXmlTools.getCurrenGroups().add(groupConfig);
                List<TaskModel> taskList = rangeList.get(0).getTaskList();
                int i = 0;
                for (TaskModel task : taskList) {
                    task.setTaskID(this.mTaskXmlTools.getCurrentTaskID(defautID));
                    task.setTaskSequence(++i);
                    groupConfig.getTasks().add(task);
                }
            } else {
                int index = 0;
                for (TestPlan.TimeRange range : rangeList) {
                    index += 1;
                    TaskGroupConfig groupConfig = new TaskGroupConfig();
                    groupConfig.setGroupID("S" + WalktourApplication.deviceIMEI + "_G" + index + "");
                    groupConfig.setGroupSequence(index);
                    groupConfig.setCheck(true);
                    groupConfig.setGroupName(TaskXmlTools.sDefaultGroupName);
                    String startTime = range.getStartTime();
                    String cotinueTime = range.getContinuousTime();
                    groupConfig.getTimeDuration().setCheck(true);
                    groupConfig.getTimeDuration().getTaskExecuteDuration().setStartTime(startTime);
                    groupConfig.getTimeDuration().getTaskExecuteDuration().setDuration(cotinueTime);
                    this.mTaskXmlTools.getCurrenGroups().add(groupConfig);
                    List<TaskModel> taskList = range.getTaskList();
                    int i = 0;
                    for (TaskModel task : taskList) {
                        task.setTaskID(this.mTaskXmlTools.getCurrentTaskID(defautID));
                        task.setTaskSequence(++i);
                        groupConfig.getTasks().add(task);
                    }
                }
            }
        }
        this.writeXml();
        return this.mTaskXmlTools.getTestPlanConfig();
    }

    /***
     * 获取从ipad下载下来的任务,默认为只有一个组
     *
     * @param taskList
     *            测试任务
     * @return 测试计划
     */
    public TestPlanConfig getTestPlanConfigFromIpad(List<TaskModel> taskList) {
        String defautID = this.mTaskXmlTools.getCurrentSchemasID() + "_" + TaskXmlTools.sDefaultGroupID;
        List<TaskGroupConfig> groups = this.mTaskXmlTools.getCurrenGroups();
        TaskGroupConfig currGroup = null;
        for (TaskGroupConfig group : groups) {
            if (group.getGroupID().equals(defautID)) {
                group.getTasks().clear();
                currGroup = group;
            }
        }
        if (taskList != null && !taskList.isEmpty()) {
            if (null == currGroup) {
                TaskGroupConfig groupConfig = new TaskGroupConfig();
                groupConfig.setGroupID(defautID);
                groupConfig.setGroupSequence(1);
                groupConfig.setCheck(true);
                groupConfig.setGroupName(TaskXmlTools.sDefaultGroupName);
                this.mTaskXmlTools.getCurrenGroups().add(groupConfig);
                int i = 0;
                for (TaskModel task : taskList) {
                    task.setTaskID(this.mTaskXmlTools.getCurrentTaskID(defautID));
                    task.setTaskSequence(++i);
                    groupConfig.getTasks().add(task);
                }
            } else {
                int i = 0;
                for (TaskModel task : taskList) {
                    task.setTaskID(this.mTaskXmlTools.getCurrentTaskID(defautID));
                    task.setTaskSequence(++i);
                    currGroup.getTasks().add(task);
                }
            }
        }
        //default组为选中,其他组及组内任务为非选中
        groups = this.mTaskXmlTools.getCurrenGroups();
        for (TaskGroupConfig group : groups) {
            if (!group.getGroupID().equals(defautID)) {
                group.setCheck(false);
                List<TaskModel> taskModelList = group.getTasks();
                for (TaskModel task : taskModelList) {
                    task.setCheck(false);
                }
            }
        }
        this.writeXml();
        return this.mTaskXmlTools.getTestPlanConfig();
    }

    /**
     * 生成默认任务组
     */
    public void createDefaultGroup() {
        String defautID = this.mTaskXmlTools.getCurrentSchemasID() + "_"
                + TaskXmlTools.sDefaultGroupID;
        TestPlanConfig config = TaskListDispose.getInstance().getTestPlanConfig();
        config.getTestSchemas().getTestSchemaConfig()
                .setSchemaID(this.mTaskXmlTools.getCurrentSchemasID());
        List<TaskGroupConfig> groups = config.getTestSchemas().getTestSchemaConfig().getTaskGroups();
        boolean isExist = false;
        for (TaskGroupConfig group : groups) {
            if (group.getGroupID().equals(defautID)) {
                isExist = true;
                break;
            }
        }
        if (!isExist) {// 不存在这样的组,需要新建这样的组
            TaskGroupConfig group = new TaskGroupConfig();
            group.setGroupID(defautID);
            group.setGroupName(TaskXmlTools.sDefaultGroupName);
            group.setGroupSequence(1);
            groups.add(group);
        }
        TaskListDispose.getInstance().setGroupID(defautID);
    }
    public TaskXmlTools getmTaskXmlTools() {
        return mTaskXmlTools;
    }
}
