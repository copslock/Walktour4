package com.walktour.service.phoneinfo.pointinfo;

import android.content.Context;

import com.walktour.Utils.EventBytes;
import com.walktour.service.phoneinfo.pointinfo.model.PointInfoHeader;
import com.walktour.service.phoneinfo.pointinfo.model.PointInfoParam;
import com.walktour.service.phoneinfo.pointinfo.model.SingleStructInfo;

import java.util.List;

/**
 * 采样点信息写入RCU文件
 * 
 * 
 * @author weirong.fan
 *
 */
public final class PointInfo {
	/** 采样点头信息 **/
	private PointInfoHeader header;
	/** 采样点中的参数信息 */
	private List<PointInfoParam> params;
	/** 采样点中的临时结构信息 **/
	private List<SingleStructInfo> others;
	/** 采样点信息组合 **/
	private EventBytes pointBytes;

	/***
	 * 构造器
	 * 
	 * @param context
	 * @param header
	 * @param params
	 * @param others
	 */
	public PointInfo(Context context, PointInfoHeader header, List<PointInfoParam> params,
			List<SingleStructInfo> others) {
		super();
		this.header = header;
		this.params = params;
		this.others = others;
		this.pointBytes = EventBytes.Builder(context);
	}

	/**
	 * 生成采样点信息字节流
	 */
	private void createPointBytes() {
		// 写采样点头信息
		this.header.addEventValue(this.pointBytes);
		// 写采样点中的参数信息
		if (null == this.params) {
			this.pointBytes.addInteger(0);
		} else if (null != this.params && this.params.size() >= 0) {
			this.pointBytes.addInteger(this.params.size());
			for (PointInfoParam param : this.params) {
				param.addEventValue(this.pointBytes);
			}
		}
		// 写采样点中的临时结构信息
		if (null == this.others) {
			this.pointBytes.addInteger(0);
		} else if (null != this.others && this.others.size() >= 0) {
			this.pointBytes.addInteger(this.others.size());
			for (SingleStructInfo s : this.others) {
				s.addEventValue(this.pointBytes);
			}
		}
	}

	/***
	 * 采样点信息写入RCU文件
	 */
	public void writeToRcu() {
		this.createPointBytes();
		// 写入到RCU文件中
		this.pointBytes.writePointToRcu(System.currentTimeMillis() * 1000);
		this.pointBytes = null;
		this.header = null;
		this.params = null;
		this.others = null;
	}
}
