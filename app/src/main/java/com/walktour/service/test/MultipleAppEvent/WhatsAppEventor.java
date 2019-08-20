package com.walktour.service.test.MultipleAppEvent;

import com.dingli.ott.skype.SkypeEvent;
import com.dingli.ott.whatsapp.WhatsAppEvent;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.gui.task.parsedata.model.task.ott.TaskMultipleAppTestModel;
import com.walktour.service.test.OttWhatsAppTest;

import java.util.Map;

/**
 * WhatsApp 测试事件
 *
 * @author zhicheng.chen
 * @date 2018/8/21
 */
public class WhatsAppEventor extends BaseEventor{

	private boolean mSuccess = true;
	private int mFailedCode;
	private String mFailedDesc;
    protected OttWhatsAppTest.MultipleAppTestHandler  mHandler;
    protected TaskMultipleAppTestModel mTaskModel;

	public WhatsAppEventor(OttWhatsAppTest.MultipleAppTestHandler  handler) {
        this.mHandler = handler;
        mTaskModel = handler.getTaskModel();
	}

	@Override
	public void handleStartEvent() {
		int pictureQuality = 0;
		switch (this.mTaskModel.getSendPictureType()) {
			case 0:
				pictureQuality = 1048576;
				break;
			case 1:
				pictureQuality = 3145728;
				break;
		}
		Event(RcuEventCommand.WhatsApp_Test_Start)
				.addStringBuffer(mTaskModel.getSendText())
				.addInteger(pictureQuality)
				.addStringBuffer(mHandler.mIp)
				.writeToRcu(System.currentTimeMillis());
	}

	@Override
	public void handleProcessEvent(Map<String, String> datasMap) {
		String eventType = datasMap.get(SUB_EVENT);
		switch (eventType) {
			case SUB_EVENT_MPACTION_START:
				handleStartEvent(datasMap);
				break;
			case SUB_EVENT_MPACTION_SUCCESS:
				Event(RcuEventCommand.WhatsApp_Action_Success)
						.addInteger(getInt(datasMap, MULTIPLE_ACTION_TYPE))
						.addInteger(getInt(datasMap, INNER_DELAY))
						.addInteger(getInt(datasMap, UP_BYTES))
						.addInteger(getInt(datasMap, DOWN_BYTES))
						.writeToRcu(System.currentTimeMillis());
				break;
			case SUB_EVENT_MPACTION_FAILED:
				mSuccess = false;
				mFailedCode = getInt(datasMap, CODE);
				mFailedDesc = datasMap.get(DESC) == null ? "" : datasMap.get(DESC);
				Event(RcuEventCommand.WhatsApp_Action_Failed)
						.addInteger(getInt(datasMap, MULTIPLE_ACTION_TYPE))
						.addInteger(getInt(datasMap, INNER_DELAY))
						.addInteger(mFailedCode)
						.addStringBuffer(mFailedDesc)
						.writeToRcu(System.currentTimeMillis());
				break;
		}
	}

	private void handleStartEvent(Map<String, String> datasMap) {
		int actionType = getInt(datasMap, MULTIPLE_ACTION_TYPE);
		EventBytes eventBytes = Event(RcuEventCommand.WhatsApp_Action_Start)
				.addInteger(actionType);
		if (WhatsAppEvent.WHATSAPP_ACTION_TYPE_SEND_MSG == actionType) {
			eventBytes.addStringBuffer(mTaskModel.getSendText())
					.addInteger(mTaskModel.getSendText().getBytes().length)
					.writeToRcu(System.currentTimeMillis());
		} else if (WhatsAppEvent.WHATSAPP_ACTION_TYPE_SEND_IMG == actionType) {
			int pictureQuality = 0;
			String imgType = "";
			switch (this.mTaskModel.getSendPictureType()) {
				case 0:
					pictureQuality = 1048576;
					imgType = "1M";
					break;
				case 1:
					pictureQuality = 3145728;
					imgType = "3M";
					break;
			}
			eventBytes.addStringBuffer(imgType)
					.addInteger(pictureQuality)
					.writeToRcu(System.currentTimeMillis());
		} else if (SkypeEvent.SKYPE_ACTION_VOICE == actionType) {
			eventBytes.addStringBuffer("skype voice msg")
					.addInteger(mTaskModel.getVoiceDuration())
					.writeToRcu(System.currentTimeMillis());
		}
	}

	@Override
	public void handleEndEvent() {
		if (mSuccess) {
			Event(RcuEventCommand.WhatsApp_Test_End)
                    .addInteger(0)
                    .addInteger(0)
                    .addStringBuffer("success")
					.writeToRcu(System.currentTimeMillis());
		} else {
			Event(RcuEventCommand.WhatsApp_Test_End)
					.addInteger(1)
					.addInteger(mFailedCode)
					.addStringBuffer(mFailedDesc)
					.writeToRcu(System.currentTimeMillis());
		}
    }
}
