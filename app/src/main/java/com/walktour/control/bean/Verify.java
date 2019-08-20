package com.walktour.control.bean;

import com.walktour.Utils.StringUtil;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验类
 * 
 * @author jianchao.wang
 *
 */
public class Verify {

	private Verify() {

	}

	/**
	 * 是否IP地址
	 * 
	 * @param str
	 *          字符串
	 * @return
	 */
	public static boolean isIp(String str) {
		if (StringUtil.isNullOrEmpty(str))
			return false;
		String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 是否合法的端口号
	 * 
	 * @param str
	 *          字符串
	 * @return
	 */
	public static boolean isPort(String str) {
		if (StringUtil.isNullOrEmpty(str) || !isInteger(str))
			return false;
		int port = Integer.valueOf(str);
		if (port > 0 && port <= 65535) {
			return true;
		}
		return false;
	}

	/**
	 * 判断字符串是IP或者URL
	 * 
	 * @param str
	 *          字符串
	 * @return
	 */
	public static boolean isIpOrUrl(String str) {
		return isIp(str) || isUrl(str);
	}

	/**
	 * 是否URL字符串
	 * 
	 * @param str
	 *          字符串
	 * @return
	 */
	public static boolean isUrl(String str) {
		if (StringUtil.isNullOrEmpty(str))
			return false;
		String ip = "(^((http|ftp|https|file)://)?)([a-zA-Z0-9_-]*\\.[a-zA-Z0-9_-]*(\\.[a-zA-Z0-9_-]*)?)(:[0-9]{1,4})*([a-zA-Z0-9\\&%_\\./-~-]*)?";
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(str);
		return matcher.find();
	}

	/**
	 * 是否字符串是IP地址而不是URL
	 * 
	 * @param str
	 *          字符串
	 * @return
	 */
	public static boolean isIpOrNotUrl(String str) {
		return isIp(str) || isNotUrl(str);
	}

	/**
	 * 是否字符串非URL
	 * 
	 * @param str
	 *          字符串
	 * @return
	 */
	public static boolean isNotUrl(String str) {
		if (StringUtil.isNullOrEmpty(str))
			return true;
		
		String ip = "^(((?((http|ftp|https|file)://))www\\.))";
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(str);
		return matcher.find();
	}

	/**
	 * 是否整数
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		if (StringUtil.isNullOrEmpty(str))
			return false;
		String value = "^-?\\d+$";
		Pattern pattern = Pattern.compile(value);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 是否是数值字符串
	 * 
	 * @param str
	 *          字符串
	 * @return
	 */
	public static boolean checknum(String str) {
		if (StringUtil.isNullOrEmpty(str))
			return false;
		return str.matches("-?\\d+\\.?\\d*");
	}

	/***
	 * 验证文件名是否合法.
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isValidFileName(String fileName) {
		if (fileName == null || fileName.length() > 255)
			return false;
		return fileName.matches(
				"[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$");
	}

	/**
	 * 验证天数是否合法
	 * 
	 * @param str
	 *          字符串
	 * @return 合法ture，非法false，正整数为合法
	 */
	public static boolean checktime(String str) {
		if (StringUtil.isNullOrEmpty(str))
			return false;
		boolean flag = false;
		try {
			int a = Integer.parseInt(str);
			if (str.length() > 1 && str.startsWith("0")) {
				flag = false;
			} else {
				if (a < 0) {
					flag = false;
				} else {
					flag = true;
				}

			}
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 验证输入的数字是否合法
	 * 
	 * @param str
	 *          字符串
	 * @return 合法ture，非法false
	 */
	public static boolean check(String str) {
		if (StringUtil.isNullOrEmpty(str))
			return false;
		return str.matches("(\\+|\\-)?(([1-9]\\d*)|0)(\\.\\d+)?") || str.matches("0{1}");
	}

	/**
	 * 验证输入的字符串是否只包含中文，英文或者数字 \\w 由数字、26个英文字母或者下划线 \\u4e00-\\u9fa5汉字
	 * 
	 * @param str
	 *          字符串
	 * @return
	 */
	public static boolean checkChar(String str) {
		if (StringUtil.isNullOrEmpty(str))
			return false;
		return str.matches("^(?![_+-])(?!.*?_+-$)[-+\\w\\u4e00-\\u9fa5]+");
	}

	/**
	 * 判断是不是一个合法的电子邮件地址
	 * 
	 * @param email
	 *          字符串
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (StringUtil.isNullOrEmpty(email))
			return false;
		Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		email = email.toLowerCase(Locale.getDefault());
		return emailer.matcher(email).matches();
	}

	/**
	 * 判断是否是SMTP服务器地址
	 * 
	 * @param stmpaddress
	 *          字符串
	 * @return
	 */
	public static boolean isSmtpAddress(String stmpaddress) {
		if (StringUtil.isNullOrEmpty(stmpaddress))
			return false;
		Pattern pattern = Pattern.compile("\\w+([-+.]\\w+)*\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		return pattern.matcher(stmpaddress).matches();
	}

}