package com.walktour.gui.eventbus;

/**
 * 点击查看信息-》更多界面事件menu下发的EventBus事件，在Event接收
 * @author zhicheng.chen
 * @date 2018/7/19
 */
public  class OnEventMenuSelectedEvent {

	public static final int TYPE_SEARCH = 0;
	public static final int TYPE_CLEAR_TEXT = 1;
	public static final int TYPE_SAVE = 2;
	public static final int TYPE_ADD_LABEL = 3;
	public static final int TYPE_FLEET_COMPLAIN = 4;
	public static final int TYPE_SETTING = 5;

	@Override
	public String toString() {
		return "OnEventMenuSelectedEvent{" +
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

	public OnEventMenuSelectedEvent(int type) {
		this.type = type;
	}
}
