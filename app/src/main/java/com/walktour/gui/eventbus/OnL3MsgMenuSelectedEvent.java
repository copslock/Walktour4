package com.walktour.gui.eventbus;

/**
 * 点击查看信息-》更多界面信令menu下发的EventBus事件，在SignalActivity接收
 * @author zhicheng.chen
 * @date 2018/7/19
 */
public  class OnL3MsgMenuSelectedEvent {

	public static final int TYPE_SEARCH = 0;
	public static final int TYPE_SETTING = 1;
	public static final int TYPE_REFRESH_SETTING = 2;
	public static final int TYPE_SAVE_MSG_LIST = 3;

	@Override
	public String toString() {
		return "OnL3MsgMenuSelectedEvent{" +
				"type=" + type +
				'}';
	}

	private int type;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public OnL3MsgMenuSelectedEvent(int type) {
		this.type = type;
	}
}
