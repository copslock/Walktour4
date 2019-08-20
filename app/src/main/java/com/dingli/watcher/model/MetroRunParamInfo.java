package com.dingli.watcher.model;

public class MetroRunParamInfo {
	/** 加速度 */
	public double Acceleration;

	/**
	 * 0=Initial(初始状态)<BR>
	 * 1=Reduce(减速状态)<BR>
	 * 2=Stop(停止状态)<BR>
	 * 3=Start(起动状态)<BR>
	 * 4=Station(到站状态)<BR>
	 * 5=Unknown(未知状态)
	 */
	public int RunStatus;
	public int GNWStatus;
	public int Mode;

}
