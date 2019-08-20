package com.walktour.gui.setting.customevent.model;

import com.walktour.Utils.StringUtil;

/**
 * 自定义参数事件
 * 
 * @author jianchao.wang
 *
 */
public class CustomEventParam extends CustomEvent {
	/** 参数定义中的3个参数,参数事件用到 */
	private Param[] mParams = new Param[3];
	/** 参数定义中的持续时间 */
	private int mDuration = 10;

	public CustomEventParam() {
		super(TYPE_PARAMS);
	}

	public Param[] getParams() {
		return this.mParams;
	}

	public void setParams(Param[] params) {
		this.mParams = params;
	}

	public String getParamString() {
		String result = "";
		if (mParams != null) {
			try {
				for (int i = 0; i < mParams.length; i++) {
					if (mParams[i] != null) {
						Param p = mParams[i];
						result += ((i > 0) ? "##" : "") + p.id + "@" + p.compare + "@" + p.value;
					}
				}
			} catch (Exception e) {

			}
		}
		return result;
	}

	/***
	 * 函数功能：设置参数
	 * 
	 * @param paramString
	 *          来自文件的串，与getParamString()对应
	 */
	public void setParams(String paramString) {
		if (StringUtil.isNullOrEmpty(paramString))
			return;
		try {
			String[] pStringArray = paramString.split("##");
			Param[] paramArray = new Param[pStringArray.length];
			for (int i = 0; i < pStringArray.length; i++) {
				String[] oneParam = pStringArray[i].split("@");
				Param param = new Param(oneParam[0], Integer.parseInt(oneParam[1]), Float.parseFloat(oneParam[2]));
				paramArray[i] = param;
			}
			this.mParams = paramArray;
		} catch (Exception e) {

		}
	}

	public int getDuration() {
		return mDuration;
	}

	public void setDuration(int duration) {
		mDuration = duration;
	}

}
