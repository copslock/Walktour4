package com.walktour.gui.report.template;

import org.xmlpull.v1.XmlPullParser;

/***
 * 业务模板描述文件基类
 * 
 * @author weirong.fan
 *
 */
public abstract class BaseTemplateModel {
	/** 层级 **/
	public int level = -1;
	/** 编码 **/
	public String code = "";

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	/***
	 * 解析XML
	 * @param parser
	 * @throws Exception
	 */
	public abstract void parseXml(XmlPullParser parser) throws Exception;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + level;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseTemplateModel other = (BaseTemplateModel) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (level != other.level)
			return false;
		return true;
	}
	
	
}
