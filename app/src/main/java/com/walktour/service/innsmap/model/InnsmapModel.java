package com.walktour.service.innsmap.model;

/**
 * 寅时室内测试对象
 * 
 * @author jianchao.wang
 *
 */
public class InnsmapModel {
	/** 对象ID */
	private String mId;
	/** 对象名称 */
	private String mName;
	/** 对象类型 */
//	private Type mType;

	/**
	 * 对象类型
	 * 
	 * @author jianchao.wang
	 *
	 */
	public static enum Type {
		City(0), Building(1), Floor(2);
		private int mId;

		private Type(int id) {
			this.mId = id;
		}

		public static Type valueOf(int id) {
			for (Type type : values()) {
				if (type.mId == id)
					return type;
			}
			return null;
		}

		public int getId() {
			return mId;
		}
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		mId = id;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

//	public Type getType() {
//		return mType;
//	}
//
//	public void setType(Type type) {
//		mType = type;
//	}
	@Override
	public String toString() {
		return "InnsmapModel{" + "mId='" + mId + '\'' + ", mName='" + mName + '\'' + '}';
	}
}
