package com.walktour.model;

import java.io.Serializable;
import java.util.ArrayList;

public class LicenseModel implements Serializable{
	
	private static final long serialVersionUID = -7060210544600464481L; 
	private String project;
	private String region;
	private String country;
	private String contractcode;
	private String contractname;
	private String createdate;
	private String expiredate;
	
	private int  licenseQuantity;
	private int licenseused;
	private int property;
	
	private ArrayList<String> featureList = new ArrayList<String>();
	
	
	public int getLicenseQuantity() {
		return licenseQuantity;
	}
	public void setLicenseQuantity(int licenseQuantity) {
		this.licenseQuantity = licenseQuantity;
	}
	public int getLicenseused() {
		return licenseused;
	}
	public void setLicenseused(int licenseused) {
		this.licenseused = licenseused;
	}
	public ArrayList<String> getFeatureList() {
		return featureList;
	}
	public void setFeatureList(ArrayList<String> featureList) {
		this.featureList = featureList;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getContractcode() {
		return contractcode;
	}
	public void setContractcode(String contractcode) {
		this.contractcode = contractcode;
	}
	public String getContractname() {
		return contractname;
	}
	public void setContractname(String contractname) {
		this.contractname = contractname;
	}
	public String getCreatedate() {
		return createdate;
	}
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}
	public String getExpiredate() {
		return expiredate;
	}
	public void setExpiredate(String expiredate) {
		this.expiredate = expiredate;
	}
	public int getProperty() {
		return property;
	}
	public void setProperty(int property) {
		this.property = property;
	}
	
	
	
	
}
