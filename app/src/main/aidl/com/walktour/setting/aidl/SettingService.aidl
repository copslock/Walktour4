package com.walktour.setting.aidl;

/**
 * The process of cross service class 
 * @author Jihong Xie
 *
 */
interface SettingService {

	/**
	 * APN Set up
	 * @param apnName APN name
	 * @return Set the return true, false on failure
	 */
	boolean setSelectApn(String apnName);
	
	/**
	* Close APN
	*/
	boolean closeAPN();
	
	/**
	 * Open APN
	 * @param apnId apnId
	 */
	boolean openAPN(String apnId);
	
	/**
	 *Wireless settings 
	 *@param flag true Open,false Close
	 *@return Set the return true, false on failure
	 */
	boolean setRadio(boolean flag);
	
	/**
	 *Wireless settings
	 *@param flag true Open,false Close
	 *@return Set the return true, false on failure
	 */
	 boolean setMobileDataEnabled(boolean flag);
	 
	 /**
	  * GPS switch, note here is just a switch, when GPS is not available for free, and vice versa
	  **/
	 void gpsProvider();
}
