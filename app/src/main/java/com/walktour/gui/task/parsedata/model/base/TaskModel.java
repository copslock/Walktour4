package com.walktour.gui.task.parsedata.model.base;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.task.multirab.ParallelServiceTestConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Comparator;

/**
 * 测试任务基类
 *
 * @author weirong.fan
 */
public class TaskModel extends TaskConfig implements Cloneable, Comparator<TaskModel> {

    private static final long serialVersionUID = 5577494294957019886L;
    public static final int TYPE_PROPERTY_NOCALL = 0;
    public static final int TYPE_PROPERTY_CALL = 1;

    /**
     * 开启MOS测试模式
     */
    public static final int MOS_ON = 1;
    /**
     * 关闭MOS测试模式
     */
    public static final int MOS_OFF = 0;
    /**
     * 算分方法：PESQ
     */
    public static final int MOS_PESQ = 0;
    /**
     * 算分方法：MOS_POLQA
     */
    public static final int MOS_POLQA = 1;
    /**
     * POLQA 放音样本文件 8k
     */
    public static final int POLQA_8K = 0;
    /**
     * POLQA 放音样本文件 16k
     */
    public static final int POLQA_16K = 1;
    /**
     * POLQA 放音样本文件 48k
     */
    public static final int POLQA_48K = 2;
    /**
     * MOS类型:Mobile To Mobile
     */
    public static final int MOS_M2M = 0;
    /**
     * MOS类型:Mobile To Land
     */
    public static final int MOS_M2L = 1;

    public static final int EDIT_TYPE_ENABLE = 0;
    public static final int EDIT_TYPE_BTU = 1;

    protected String UNKNOWN_VALUE = "000";
    @SerializedName("id")
    protected long id; // id，btu平台用到,华为工单用到
    @SerializedName("sequence")
    protected long sequence; // 序号，华为工单用到
    @SerializedName("datadevice")
    protected String datadevice = ""; // 数据业务时指定访问网络端口

    @SerializedName("nettype")
    protected int nettype = -1; // 锁网类型,0:FREE,1:GSM,2:TD_SCDMA,3:CDMA2000,4:WCDMA
    @SerializedName("isTest")
    protected int isTest = 0; // 是否不测试，默认为测试。0 不测试 1测试
    @SerializedName("isUnitTest")
    protected boolean isUnitTest; // 是否主被叫联合测试
    @SerializedName("interTaskInterval")
    protected int interTaskInterval = 10; // 任务间间隔恪，默认值为10s
    @SerializedName("editType")
    private int editType = 0; // 模版编辑类型,默认为0可编辑,值为1的时候表示atu下发,此时任务模版不可编辑
    @SerializedName("isRab")
    private int isRab; // 是否并发
    @SerializedName("rabName")
    private String rabName; // 并发名字
    @SerializedName("TypeProperty")
    private int TypeProperty = 0; // 0，非数据类型，1 语音，2 Net,3 Wap
    // 备用的字段，来自Fleet下载的测试计划中
    @SerializedName("tag")
    private String tag;

    /**
     * 测试任务xml描述文件,
     **/
    @SerializedName("xmlDescription")
    private String xmlDescription = "";

    public String getRabRelTime() {
        return this.getParallelStartAfterDelay() + "";
    }

    public void setRabRelTime(String rabRelTime) {
        try {
            this.setParallelStartAfterDelay(Integer.parseInt(rabRelTime));
        } catch (Exception ex) {
            ex.printStackTrace();
            this.setParallelStartAfterDelay(50);
        }

    }

    public String getRabRuelTime() {
        return this.getParallelStartAtTime();
    }

    public void setRabRuelTime(String rabRuelTime) {
        this.setParallelStartAtTime(rabRuelTime);
    }


    public void setXmlDescription(String xmlDescription) {
        this.xmlDescription = xmlDescription;
    }

    @Override
    public String toString() {
        return "TaskModel [TaskID=" + getTaskID() + ",TaskName="+getTaskName()+",TaskSequence="+getTaskSequence()+"]";
    }

    /**
     * 获得基本类型是测试属性参数
     *
     * @return
     * @author tangwq
     */
    public String getBaseModelStr() {
        StringBuffer testTask = new StringBuffer();
        testTask.append("TaskName =" + getTaskName() + "\r\n");
        testTask.append("TaskType =" + getTaskType() + "\r\n");
        testTask.append("Repeat =" + this.getRepeat() + "\r\n");
        testTask.append("InterVal =" + this.getInterVal() + "\r\n");
        testTask.append("TypeProperty =" + TypeProperty + "\r\n");
        testTask.append("DisConnect =" + this.getDisConnect() + "\r\n");
        return testTask.toString();
    }

    public String getXmlDescription() {
        return xmlDescription;
    }

    /**
     * 是否主被叫联合测试
     */
    public boolean isUnitTest() {
        return isUnitTest;
    }

    /**
     * 设置是否主被叫联合测试
     */
    public void setUnitTest(boolean isUnitTest) {
        this.isUnitTest = isUnitTest;
    }

    public String getTag() {
        return this.tag;
    }

    public int getEnable() {
        return this.getTaskStatus();
    }

    public void setEnable(int enable) {
        this.setTaskStatus(enable);
        switch (enable) {
            case TASKSTATUS_0:
                break;
            case TASKSTATUS_1:
                this.setCheck(true);
                TaskListDispose.getInstance().checkTask();
                break;
            case TASKSTATUS_2:
                this.setCheck(false);
                break;
        }
    }

    public String getDatadevice() {
        return datadevice;
    }

    public void setDatadevice(String datadevice) {
        this.datadevice = datadevice;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }


    public Object deepClone() throws IOException, ClassNotFoundException {
        // 将对象写到流里
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        // 从流里读回来
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        return ois.readObject();
    }


    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getNettype() {
        return nettype;
    }

    public void setNettype(int nettype) {
        this.nettype = nettype;
    }

    public int getIsTest() {
        return isTest;
    }

    public void setIsTest(int isTest) {
        this.isTest = isTest;
    }

    /**
     * 属性类型 0，非数据类型，1 语音，2 Net,3 Wap,4 wlan
     *
     * @return
     * @author tangwq
     */
    public int getTypeProperty() {
        return TypeProperty;
    }

    public void setTypeProperty(int typeProperty) {
        TypeProperty = typeProperty;
    }

    /**
     * 注意:数据业务类需要复写这两个方法,获取实际的拨号规则
     * 拨号规则 0不断开，1每次断开，2任务完成断开,非数据业务该值无效，为-1
     */
    public int getDisConnect() {
        //断开拨号规则,子类需要覆盖
        //每个测试任务都有不同的连接方式
        return -1;
    }

    /**
     * 当前 任务是否是并发任务的一个子任务
     *
     * @return the isRab
     */
    public int getIsRab() {
        return isRab;
    }

    /**
     * 设置当前 任务是否是并发任务的一个子任务
     *
     * @param isRab the isRab to set
     */
    public void setIsRab(int isRab) {
        this.isRab = isRab;
    }

    public String getRabName() {
        return rabName;
    }

    public void setRabName(String rabName) {
        this.rabName = rabName;
    }

    /**
     * 当前任务模版是否可编辑 默认0为可编辑 1为ATU下发,不可编辑
     *
     * @return
     */
    public boolean cantEdit() {
        return editType != 0;
    }

    public int getEditType() {
        return editType;
    }

    public void setEditType(int editType) {
        this.editType = editType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public int getInterTaskInterval() {
        return interTaskInterval;
    }

    public void setInterTaskInterval(int interTaskInterval) {
        this.interTaskInterval = interTaskInterval;
    }

    /**
     * 重写这个方法为
     */
    @Override
    public void setCheck(boolean isCheck) {
        super.setCheck(isCheck);
        this.setTaskStatus(isCheck ? TASKSTATUS_1 : TASKSTATUS_2);
    }

    /**
     * 并发业务延时启动时间, 如果当前为相对时间,则相对于启动时延时几秒启动, 如果为绝对时间,则为HHmmss转换为秒数,须与启动时间进行转换
     * 在TaskRabModel中有绝对于时间与秒数的转换
     *
     * @param rebModel
     */
    public int getRabDelayTimes(int rebModel) {
        try {
            // 如果当前设置为绝对时间
            if (rebModel == ParallelServiceTestConfig.RAB_STATE_MODEL_ABSOLUTELY_TIME) {
                return UtilsMethod.convertHHmmToSecond(this.getRabRuelTime());
            }
            return stringToInt(this.getRabRelTime());
        } catch (Exception e) {
            LogUtil.w(tag, "getRabDelayTimes", e);
            return 0;
        }
    }

    @Override
    public String getServerTaskType() {
        return "";
    }

    /**
     * 对象自动排序
     */
    @Override
    public int compare(TaskModel lhs, TaskModel rhs) {
        if (lhs.getTaskSequence() > rhs.getTaskSequence())
            return 1;
        else if (lhs.getTaskSequence() < rhs.getTaskSequence())
            return -1;
        return 0;
    }
}
