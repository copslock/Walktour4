package com.walktour.model;

/**
 *  License信息model
 * @author Administrator
 *
 */
public class LicenseInfoModel {

	private String userName;
	private String status;
	private String contract;
	private String expirationDate;
	private String checkoutDate;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getContract() {
		return contract;
	}
	public void setContract(String contract) {
		this.contract = contract;
	}
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	public String getCheckoutDate() {
		return checkoutDate;
	}
	public void setCheckoutDate(String checkoutDate) {
		this.checkoutDate = checkoutDate;
	}
}
