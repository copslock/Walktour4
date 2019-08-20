package com.walktour.service.bluetoothmos;

import com.walktour.service.bluetoothmos.IBluetoothMOSServiceCallback;

interface IBluetoothMOSServiceBinder {
	
	/**
	 * 执行蓝牙MOS头的放音功能
	 *
	 * @param fileType
	 *          文件类型
	 */
	boolean runPlayback(String fileType);

	/**
	 * 初始化蓝牙MOS头芯片
	 *
	 * @param fileType
	 *          文件类型
	 * @param isPlayer
	 *			是否放音，否则录音
	 */
	boolean initMOS(String fileType,boolean isPlayer);

	/**
	 * 执行蓝牙MOS头的录音功能
	 *
	 * @param fileType
	 *          文件类型
	 */
	boolean runRecord(String fileType);

	/**
	 * 获取蓝牙MOS头的录音文件路径
	 */
	String getRecordFile();

	/**
	 * 查找正确的设备
	 * @param callback 是否需要检查蓝牙是否插入耳机口
	 */
	boolean findCorrectDevice(boolean needHeadsetOn);

	/**
	 * 查找正确的设备
	 * @param callback
	 *          回调对象
	 */
    void registerCallback(IBluetoothMOSServiceCallback callback);

	/**
	 * 查找正确的设备
	 * @param callback
	 *          回调对象
	 */
    void unregisterCallback(IBluetoothMOSServiceCallback callback);

    /**
     * 执行回环检测
     */
    void testPlaybackAndRecord();

    /**
	 * 写文件到MOS头去
	 *
	 * @param fileType
	 *          文件类型
	 */
	void writeFileTo(String fileType);

	/**
	 * 获取PESQ评分
	 *
	 * @param rawId
	 *          资源ID
	 * @param filePath
	 *          要评估文件ID
	 * @return [pesqscore,pesqLq,pesq]
	 */
	double[] getCalculatePESQ(int rawId, String filePath);

	/**
	 * 获取MOS头的设置参数
	 */
	void getDeviceParams();

	/**
	 * 获取MOS头的已有音频文件
	 */
	void getHaveFileIDs();

	/**
	 * 设置MOS头的参数
	 * @param playbackVolume
	 *			放音音量
	 * @param recordVolume
	 *			录音音量
	 */
	void setDeviceParams(int playbackVolume, int recordVolume);

	/**
	 * 断开当前设备连接
	 */
	void disconnect();

	/**
	 * 清除所有文件
	 */
	void clearAudioFiles();

	/**
	 * 设置文件名扩展属性
	 * @param rcuFileName
	 *			rcu文件名称
	 * @param fileNameExtends
	 *			文件名扩展属性
	 */
	void setFileNameExtends(String rcuFileName,String fileNameExtends);

	/**
	 * 终端当前命令执行
	 */
	void interrupt();

	/**
	 * 获取当前终端电量单位%
	 */
	int getDevicePower();


	void connect(String mac);
}