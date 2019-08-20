package com.walktour.gui.task.parsedata.model.task;

import com.walktour.gui.task.parsedata.model.base.TaskModel;

/**
 * VOIP数据模型对象
 * 
 * 此业务已经作废
 */
public class TaskVoIPModel extends TaskModel {
	
	private static final long serialVersionUID = 2998946109692572204L;

	
	public TaskVoIPModel() {
		super();
		setTaskName("VoIP");
		setRepeat(1);
		setInterVal(15);
	}

	/**
	 * 呼叫类型
	 */
	public int callType;
	
	/**
	 * 通话时长
	 */
	public String duration;
	
	/**
	 * 连接超时
	 */
	public String timeout = "30";
	
	/**
	 * 无数据超时
	 */
	public String nodataTimeout = "30";
	
	/**
	 * 注册类型
	 */
	public int registerType = 0;
	
	/**
	 * 用户名
	 */
	public String username;
	
	/**
	 * 密码
	 */
	public String password;
	
	/**
	 * 服务器IP
	 */
	public String serverIP;
	
	/**
	 * 服务器端口
	 */
	public String serverPort;
	
	/**
	 * IMS鉴权信息
	 */
	public String imsInfo;
	
	/**
	 * IMS Service IP
	 */
	public String imsServiceIP;
	
	/**
	 * 对方用户名
	 */
	public String dialUser;
	
	/**
	 * VOIP类型
	 */
	public int voipType;
	
	/**
	 * 数据类型
	 */
	public int useSample = 1;
	
	/**
	 * 音频文件
	 */
	public String audioFile;
	
	/**
	 * 视频文件
	 */
	public String videoFile;
	
	/**
	 * MOS计算，0为不计算，1为计算
	 */
	public int calMOS = 0;
	
	/**
	 * 是否保存样本
	 * 0：不保存，1：保存
	 */
	public int doSave = 0;
	
	/**
	 * 文件保存路径
	 */
	public String savePath;
}
