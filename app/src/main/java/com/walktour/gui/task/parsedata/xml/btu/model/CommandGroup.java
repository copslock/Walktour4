/**
 * com.dinglicom.btu.model
 * CommandGroup.java
 * 类功能：
 * 2014-3-11-下午4:14:46
 * 2014鼎利-版权所有
 * @author qihang.li@dinglicom.com
 */
package com.walktour.gui.task.parsedata.xml.btu.model;

import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;

/**
 * CommandGroup
 * 
 * 并发业务
 * 2014-3-11 下午4:14:46
 * 
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
public class CommandGroup extends TaskRabModel{ 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2872040464905616891L;
	private String startLable ="";
	private String endLable = "";
	
	private int firstServiceEndType = 0;
	private int firstServiceEndDelay = 0;
	private int secondServiceStartType = 0;
	private int secondServiceStartDelay = 10;
	private int secondServiceEndType = 0;
	private int secondServiceEndDelay = 0;
	
	public String getStartLable() {
		return startLable;
	}

	public void setStartLable(String startLable) {
		this.startLable = startLable;
	}

	public String getEndLable() {
		return endLable;
	}

	public void setEndLable(String endLable) {
		this.endLable = endLable;
	}

	public int getFirstServiceEndType() {
		return firstServiceEndType;
	}

	public void setFirstServiceEndType(int firstServiceEndType) {
		this.firstServiceEndType = firstServiceEndType;
	}

	public int getFirstServiceEndDelay() {
		return firstServiceEndDelay;
	}

	public void setFirstServiceEndDelay(int firstServiceEndDelay) {
		this.firstServiceEndDelay = firstServiceEndDelay;
	}

	public int getSecondServiceStartType() {
		return secondServiceStartType;
	}

	public void setSecondServiceStartType(int secondServiceStartType) {
		this.secondServiceStartType = secondServiceStartType;
	}

	public int getSecondServiceStartDelay() {
		return secondServiceStartDelay;
	}

	public void setSecondServiceStartDelay(int secondServiceStartDelay) {
		this.secondServiceStartDelay = secondServiceStartDelay;
	}

	public int getSecondServiceEndType() {
		return secondServiceEndType;
	}

	public void setSecondServiceEndType(int secondServiceEndType) {
		this.secondServiceEndType = secondServiceEndType;
	}

	public int getSecondServiceEndDelay() {
		return secondServiceEndDelay;
	}

	public void setSecondServiceEndDelay(int secondServiceEndDelay) {
		this.secondServiceEndDelay = secondServiceEndDelay;
	}
}
