package com.walktour.service.test.MultipleAppEvent;

import com.walktour.Utils.RcuEventCommand;
import com.walktour.gui.task.parsedata.model.task.ott.TaskMultipleAppTestModel;
import com.walktour.service.test.OttSinaWeiboTest;

import java.util.Map;

/**
 * SinaWeibo 测试事件
 *
 * @author zhicheng.chen
 * @date 2018/8/21
 */
public class SinaWeiboEventor extends BaseEventor {

	private boolean mSuccess = true;
	private int mFailedCode;
	private String mFailedDesc;
    protected OttSinaWeiboTest.MultipleAppTestHandler mHandler;
    protected TaskMultipleAppTestModel mTaskModel;

	public SinaWeiboEventor(OttSinaWeiboTest.MultipleAppTestHandler handler) {
        this.mHandler = handler;
        mTaskModel = handler.getTaskModel();
	}

	@Override
	public void handleStartEvent() {
		Event(RcuEventCommand.Multiple_App_SinaWeBo_Test_Start)
				.addStringBuffer(mHandler.mIp)
				.writeToRcu(System.currentTimeMillis());
	}

	@Override
	public void handleProcessEvent(Map<String, String> datasMap) {
		String eventType = datasMap.get(SUB_EVENT);
		switch (eventType) {
			case SUB_EVENT_MPACTION_START:
                 Event(RcuEventCommand.Multiple_App_SinaWeBo_Action_Start)
                        .addInteger(15003);
				break;
			case SUB_EVENT_MPACTION_SUCCESS:
				Event(RcuEventCommand.Multiple_App_SinaWeBo_Action_Success)
						.addInteger(15001)
						.addInteger(getInt(datasMap, INNER_DELAY))
						.addInteger(getInt(datasMap, UP_BYTES))
						.addInteger(getInt(datasMap, DOWN_BYTES))
						.writeToRcu(System.currentTimeMillis());
				break;
			case SUB_EVENT_MPACTION_FAILED:
				mSuccess = false;
				mFailedCode = getInt(datasMap, CODE);
				mFailedDesc = datasMap.get(DESC) == null ? "" : datasMap.get(DESC);
				Event(RcuEventCommand.Multiple_App_SinaWeBo_Action_Failure)
						.addInteger(15001)
						.addInteger(getInt(datasMap, INNER_DELAY))
						.addInteger(mFailedCode)
						.addStringBuffer(mFailedDesc)
						.writeToRcu(System.currentTimeMillis());
				break;
		}
	}

	@Override
	public void handleEndEvent() {
		if (mSuccess) {
			Event(RcuEventCommand.Multiple_App_SinaWeBo_Test_END)
					.writeToRcu(System.currentTimeMillis());
		} else {
			Event(RcuEventCommand.Multiple_App_SinaWeBo_Test_END)
					.addInteger(mFailedCode)
					.addStringBuffer(mFailedDesc)
					.writeToRcu(System.currentTimeMillis());
		}
	}
}
