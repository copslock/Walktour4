package com.walktour.model;

import com.walktour.Utils.WalkStruct.TraceStructType;

import java.util.HashMap;

/**
 * 解码返回结果类
 * 继承了TDL3Msg类的所有属性
 * @author tangwq
 *
 */
public class DecodeResult extends TdL3Model {
	private TraceStructType traceType = TraceStructType.Default;
	
	public TraceStructType getTraceType() {
		return traceType;
	}
	public void setTraceType(TraceStructType traceType) {
		this.traceType = traceType;
	}
	
	public HashMap<Integer,Integer> totalList = new HashMap<Integer,Integer>();
}
