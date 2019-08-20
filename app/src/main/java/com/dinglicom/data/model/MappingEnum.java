package com.dinglicom.data.model;

public interface MappingEnum {
	/**当前字段类型类串*/
	public String getFiledType();
	/**当字字段默认值*/
	public String getDefaultStr();
	/**当前字段是否不允许为空*/
	public String getNotNull();
	/**当前字段是否为自增主键*/
	public String getPrimarykey();
	/**获取属性名*/
	public String name();
}
