package com.walktour.service.phoneinfo.logcat;

/**
 * 小区信息类
 * 
 * @author jianchao.wang
 *
 */
public class CellBean {
	public static final String UNKNOWN = "no get cellinfo";
	private int id;
	// mobile country code
	private int mcc = 0;
	// mobile network code
	private int mnc = 0;
	// cell id
	private int cid = 0;
	// location area code
	private int lac = 0;
	// insert timestamp
	private long timestamp;

	public CellBean() {
		mcc = 0;
		mnc = 0;
		cid = 0;
		lac = 0;
		timestamp = System.currentTimeMillis();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMcc() {
		return mcc;
	}

	public void setMcc(int mcc) {
		this.mcc = mcc;
	}

	public int getMnc() {
		return mnc;
	}

	public void setMnc(int mnc) {
		this.mnc = mnc;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public int getLac() {
		return lac;
	}

	public void setLac(int lac) {
		this.lac = lac;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		if (mcc == -1) {
			return UNKNOWN;
		}
		return mcc + "-0" + mnc + "-" + lac + '-' + cid;
	}

	public boolean equal(CellBean bean) {
		if (bean.cid == this.cid && bean.id == this.id && bean.lac == this.lac && bean.mcc == this.mcc
				&& bean.mnc == this.mnc) {
			return true;
		}

		return false;
	}
}
