package com.walktour.model;

import java.util.LinkedHashMap;

/**
 * 基础结构体对象类，自带解析功能
 * 
 * @author jianchao.wang
 *
 */
public abstract class BaseStructParseModel {
	
	/** 结构体使用的结构类型 */
	public static enum StructType {
		Int(4), Int64(8), Float(4),Double(4);
		private final int len;

		private StructType(int len) {
			this.len = len;
		}

		public int getLen() {
			return len;
		}

	};

	/** 属性映射<属性名，结构体字段类型> */
	public LinkedHashMap<String, StructType> propMap = new LinkedHashMap<String, StructType>();

	public BaseStructParseModel() {
		this.init();
	}

	/**
	 * 初始化结构体映射 propMap
	 */
	protected abstract void init();

	/**
	 * 获得对象的bytes长度
	 * 
	 * @return
	 */
	public int getModelLen() {
		if (this.propMap.isEmpty())
			return 0;
		int len = 0;
		for (StructType type : this.propMap.values()) {
			len += type.getLen();
		}
		return len;
	}
}
