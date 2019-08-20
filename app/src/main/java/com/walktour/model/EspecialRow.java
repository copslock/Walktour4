package com.walktour.model;

import android.util.SparseArray;

public class EspecialRow {

	private String name;
	
	private String unit;
	
	private int decimal;
	
	private int scale;
	
	private int enums;
	
	private String[] keys;
	
	private SparseArray<String> enumSet;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getDecimal() {
		return decimal;
	}

	public void setDecimal(int decimal) {
		this.decimal = decimal;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	/**
	 * @return the enums
	 */
	public int getEnums() {
		return enums;
	}

	/**
	 * @param enums the enums to set
	 */
	public void setEnums(int enums) {
		this.enums = enums;
	}

	/**
	 * @return the enumSet
	 */
	public SparseArray<String> getEnumSet() {
		if(enumSet != null){
			return enumSet;
		}
		return new SparseArray<String>();
	}

	/**
	 * @param enumSet the enumSet to set
	 */
	public void setEnumSet(SparseArray<String> enumSet) {
		this.enumSet = enumSet;
	}
}
