/**
 * @author tangwq
 */
package com.walktour.model;

import android.content.Intent;
import android.content.ServiceConnection;

import com.walktour.Utils.WalkStruct.TaskType;
import com.walktour.service.IService;

/**
 * 测试对象模型
 * @author tangwq
 *
 */
public class TestObjectModel {
	private IService mService;
	private Intent testIntent;
	private TaskType taskType = TaskType.Default;
	private ServiceConnection serviceConnection;
	private boolean serviceHasBind = false;
	private boolean testStoping = false;
	/**当前测试任务异常结束状态*/
	private boolean isAbnormalStop = false; 
	
	public TestObjectModel(Intent intent){
		this.testIntent = intent;
	}
	/**
	 * @return the mService
	 */
	public IService getmService() {
		return mService;
	}
	/**
	 * @param mService the mService to set
	 */
	public void setmService(IService mService) {
		this.mService = mService;
	}
	/**
	 * @return the testIntent
	 */
	public Intent getTestIntent() {
		return testIntent;
	}
	/**
	 * @param testIntent the testIntent to set
	 */
	public void setTestIntent(Intent testIntent) {
		this.testIntent = testIntent;
	}
	/**
	 * @return the testStoping
	 */
	public boolean isTestStoping() {
		return testStoping;
	}
	/**
	 * @param testStoping the testStoping to set
	 */
	public void setTestStoping(boolean testStoping) {
		this.testStoping = testStoping;
	}
	/**
	 * @return the serviceConnection
	 */
	public ServiceConnection getServiceConnection() {
		return serviceConnection;
	}
	/**
	 * @param serviceConnection the serviceConnection to set
	 */
	public void setServiceConnection(ServiceConnection serviceConnection) {
		this.serviceConnection = serviceConnection;
	}
    /**
     * @return the isAbnormalStop
     */
    public boolean isAbnormalStop() {
        return isAbnormalStop;
    }
    /**
     * @param isAbnormalStop the isAbnormalStop to set
     */
    public void setAbnormalStop(boolean isAbnormalStop) {
        this.isAbnormalStop = isAbnormalStop;
    }
    /**
     * @return the serviceHasBind
     */
    public boolean isServiceHasBind() {
        return serviceHasBind;
    }
    /**
     * @param serviceHasBind the serviceHasBind to set
     */
    public void setServiceHasBind(boolean serviceHasBind) {
        this.serviceHasBind = serviceHasBind;
    }
    
	public TaskType getTaskType() {
		return taskType;
	}
	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}
}
