package com.walktour.model;

import java.util.ArrayList;
import java.util.List;

/**
 * GoOrNogo相关实体
 * @author msi
 *
 */
public class Business {

	private String name = "";
	private String key = "";
	
	private List<GoOrNogoParameter> avaliableSettings = new ArrayList<GoOrNogoParameter>();
	private List<GoOrNogoParameter> defaultSettings = new ArrayList<GoOrNogoParameter>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public List<GoOrNogoParameter> getAvaliableSettings() {
		return avaliableSettings;
	}
	public void setAvaliableSettings(List<GoOrNogoParameter> avaliableSettings) {
		this.avaliableSettings = avaliableSettings;
	}
	public List<GoOrNogoParameter> getDefaultSettings() {
		return defaultSettings;
	}
	public void setDefaultSettings(List<GoOrNogoParameter> defaultSettings) {
		this.defaultSettings = defaultSettings;
	}
	
}
