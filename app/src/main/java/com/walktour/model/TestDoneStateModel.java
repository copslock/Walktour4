/*
 * 文件名: TestDoneStateModel.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: tangwq
 * 创建时间:2012-11-2
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.model;

import com.walktour.service.TestService;

/**
 * [测试任务完成情况]<BR>
 * [主要记录当前任务完成情况的计数，及失败次数的控制等]
 * @author tangwq
 * @version [WalkTour Client V100R001C03, 2012-11-2] 
 */
public class TestDoneStateModel {
    private int doneStateNum = 0;
    private int testTotalCount = 0;
    private String testResult = TestService.RESULT_SUCCESS;
    
    public TestDoneStateModel(int doneStateNum){
        this.doneStateNum = doneStateNum;
    }

    /**
     * @return the doneStateNum
     */
    public int getDoneStateNum() {
        return doneStateNum;
    }

    /**
     * @param doneStateNum the doneStateNum to set
     */
    public void setDoneStateNum(int doneStateNum) {
        this.doneStateNum = doneStateNum;
    }
    
    /**
     * [减去任务完成状态的次数]<BR>
     * [功能详细描述]
     * @param doneStateNum
     */
    public void subDoneStateNum(int doneStateNum){
        this.doneStateNum -= doneStateNum;
    }

    /**
     * @return the testTotalCount
     */
    public int getTestTotalCount() {
        return testTotalCount;
    }

    /**
     * @param testTotalCount the testTotalCount to set
     */
    public void setTestTotalCount(int testTotalCount) {
        this.testTotalCount = testTotalCount;
    }
    
    /**
     * [在现有的控制次数上增加相应的失败次数]<BR>
     * [功能详细描述]
     * @param testTotalCount
     */
    public void addTestTotalCount(int testTotalCount){
        this.testTotalCount += testTotalCount;
    }

    /**获得当前任务的测结果*/
	public String getTestResult() {
		return testResult;
	}

	public void setTestResult(String testResult) {
		this.testResult = testResult;
	}
}
