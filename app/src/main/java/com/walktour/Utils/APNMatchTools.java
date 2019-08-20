package com.walktour.Utils;

public final class APNMatchTools {
	public static class APNNet{
		/**
		 * 中国移动cmwap
		 */
		public static String CMWAP = "cmwap";
		/**
		 * 中国移动cmnet
		 */
		public static String CMNET = "cmnet";
		//中国联通3GWAP设置        中国联通3G因特网设置        中国联通WAP设置        中国联通因特网设置
		//3gwap                 3gnet                uniwap            uninet
		/**
		 * 3G wap 中国联通3gwap APN 
	 	*/
		public static String GWAP_3 = "3gwap";
		/**
		 * 3G net 中国联通3gnet APN 
		 */
		public static String GNET_3="3gnet";
		/**
		 * uni wap 中国联通uni wap APN 
		 */
		public static String UNIWAP="uniwap";
		/**
		* uni net 中国联通uni net APN 
		*/
		public static String UNINET="uninet";
	}
	public static final String enabledStr = "mdev";
	/**
	 * 
	 * 设置为有效的APN接入点信息<BR>
	 * [将传入的字串，截取到指定特殊字符部分的内容]
	 * @param currentName
	 * @return
	 */
	public static String matchAPN(String currentName) {        
		if("".equals(currentName) || null==currentName){
			return "";
		}
		/*currentName = currentName.toLowerCase();
		if(currentName.startsWith(APNNet.CMNET)){
			return APNNet.CMNET;
		}else if(currentName.startsWith(APNNet.CMWAP)){
			return APNNet.CMWAP;
		}else if(currentName.startsWith(APNNet.GNET_3)){
			return APNNet.GNET_3;
		}else if(currentName.startsWith(APNNet.GWAP_3)){
			return APNNet.GWAP_3;
		}else if(currentName.startsWith(APNNet.UNINET)){
			return APNNet.UNINET;
		}else if(currentName.startsWith(APNNet.UNIWAP)){
			return APNNet.UNIWAP;
		}else if(currentName.startsWith("default")){
			return "default";
		}else if(currentName.indexOf(enabledStr) > 0){
		    return currentName.substring(0,currentName.indexOf(enabledStr));
		}else{
		    return "";
		}*/
		// return currentName.substring(0, currentName.length() - SUFFIX.length());
		if(currentName.indexOf(enabledStr) > 0){
		    return currentName.substring(0,currentName.indexOf(enabledStr));
		}else{
		    return currentName;
		}
	}
}
