package com.walktour.gui.setting.customevent.model;

/**
 * 自定义信令事件
 * 
 * @author jianchao.wang
 *
 */
public class CustomEventMsg extends CustomEvent {
	/** 信令事件用到:信令ID1 */
	private long mL3MsgID1 = 0;
	/** 信令事件用到:信令ID2 */
	private long mL3MsgID2 = 0;
	/** 两个信令之间的对比 */
	private int mCompare = COMPARE_L;
	/** 两个信令之间的间隔 */
	private int mInterval = 5000;

	public CustomEventMsg() {
		super(TYPE_MSG);
	}

	public int getCompare() {
		return this.mCompare;
	}

	public String getComapreStr() {
		switch (this.mCompare) {
		case COMPARE_L:
			return ">";
		case COMPARE_L_EQ:
			return ">=";
		case COMPARE_S:
			return "<";
		case COMPARE_S_EQ:
			return "<=";
		default:
			return "";
		}
	}

	public void setCompare(int compare) {
		this.mCompare = compare;
	}

	/**
	 * 根据信令ID和时延判断是否属于此自定义事件
	 * 
	 * @param l3MsgID1
	 *          信令ID1
	 * @param l3MsgID2
	 *          信令ID2
	 * @param delay
	 *          时延
	 * @return
	 */
	public boolean hasGenerateEvent(long l3MsgID1, long l3MsgID2, int delay) {
		if (isCompare()){  //是否需要比较
			if (l3MsgID1 == this.mL3MsgID1 && l3MsgID2 == this.mL3MsgID2 && delay > 0) {
				switch (this.mCompare) {
					case COMPARE_L:
					case COMPARE_L_EQ:
						if (delay >= this.mInterval) {
							return true;
						}
						break;
					case COMPARE_S:
					case COMPARE_S_EQ:
						if (delay <= this.mInterval) {
							return true;
						}
						break;
				}
			}
		}else { //如果不需要比较，只需要满足一个信令要求就可以
			if (l3MsgID1 == this.mL3MsgID1){
				return true;
			}
			return false;
		}



		return false;
	}

	public long getL3MsgID1() {
		return mL3MsgID1;
	}

	public void setL3MsgID1(long l3MsgID1) {
		mL3MsgID1 = l3MsgID1;
	}

	public int getInterval() {
		return mInterval;
	}

	public void setInterval(int interval) {
		mInterval = interval;
	}

	public long getL3MsgID2() {
		return mL3MsgID2;
	}

	public void setL3MsgID2(long l3MsgID2) {
		mL3MsgID2 = l3MsgID2;
	}

}
