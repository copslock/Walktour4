package com.walktour.base.util;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * 字符串工具类，字符串工具类 判断字符串<中文字符、邮箱、手机号、空字符、整数、浮点数>
 * 
 * @author weirong.fan
 *
 */
public class StringUtil {
	public static final String TAG = "StringUtil";
	public static final String EMPTY_STRING = ""; // 空字符串
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	/**小数格式化,保留2位**/
	private static DecimalFormat decimalFormat = new DecimalFormat("0.00");

	public static boolean isShowView(String str) {
		try {
			return str != null && Integer.valueOf(str) > 0;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean equals(String str1, String str2) {
		return str1 != null && str2 != null && str1.equals(str2);
	}

	public static boolean equalsIgnoreCase(String str1, String str2) {
		return str1 != null && str2 != null && str1.equalsIgnoreCase(str2);
	}

	public static boolean isContains(String str1, String str2) {
		return str1.contains(str2);
	}

	public static String getString(String str) {
		return str == null ? "" : str;
	}

	public static String unquote(String s, String quote) {
		if (!isEmpty(s) && !isEmpty(quote)) {
			if (s.startsWith(quote) && s.endsWith(quote)) {
				return s.substring(1, s.length() - quote.length());
			}
		}
		return s;
	}

	public static boolean equals(String contentType1, String contentType2, boolean ignoreCase) {
		if (contentType1 != null && contentType2 != null) {
			if (ignoreCase) {
				return contentType1.equalsIgnoreCase(contentType2);
			}
			return contentType1.equals(contentType2);
		}
		return ((contentType1 == null && contentType2 == null) ? true : false);
	}

	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + LINE_SEPARATOR);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 判断字符串是否整数值
	 * 
	 * @param str
	 *          字符串
	 * @return
	 */
	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// 解析短信推送内容
	public static Map<String, Object> getParameterMap(String data) {
		Map<String, Object> map = null;
		if (data != null) {
			map = new HashMap<String, Object>();
			String[] params = data.split("&");
			for (int i = 0; i < params.length; i++) {
				int idx = params[i].indexOf("=");
				if (idx >= 0) {
					map.put(params[i].substring(0, idx), params[i].substring(idx + 1));
				}
			}
		}
		return map;
	}

	/**
	 * 检测字符串是否符合用户名
	 * 
	 * @param len
	 * @return
	 */
	public static boolean checkingMsg(int len) {
		boolean isValid = true;
		if (5 < len && len < 21) {
			isValid = false;
		} else {
			isValid = true;
		}
		return isValid;
	}

	/**
	 * 检测长度
	 * 
	 * @param len
	 * @return
	 */
	public static boolean isVaild(int len) {
		boolean isValid = true;
		if (1 < len && len < 17) {
			isValid = false;
		}
		return isValid;
	}

	/**
	 * 检测字符串是否含有中文字符
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isChineseChar(String str) {
		if (str.length() < str.getBytes().length) {
			return true;
		}
		return false;
	}

	/**
	 * 判断字符串是否为邮箱
	 * 
	 * @param aEmail
	 * @return
	 */
	public static boolean isEmailVaild(String aEmail) {
		boolean isValid = true;
		Pattern pattern = Pattern.compile(
				"^([a-zA-Z0-9]+[_|-|.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|-|.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,3}$",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(aEmail);
		if (matcher.matches()) {
			isValid = false;
		}
		return isValid;
	}

	/**
	 * 判断字符串是否为手机号码
	 * 
	 * @param aTelNumber
	 * @return
	 */
	public static boolean isMobileNumber(String aTelNumber) {
		Pattern p = Pattern.compile(
				"(^1((((3[5-9])|(47)|(5[0-2])|(5[7-9])|(82)|(8[7-8]))\\d{8})|((34[0-8])\\d{7}))$)|(^1((3[0-2])|(5[5-6])|(8[0-6]))\\d{8}$)|(^1((33[0-9])|(349)|(53[0-9])|(80[0-9])|(89[0-9]))\\d{7}$)");
		Matcher m = p.matcher(aTelNumber);
		return m.matches();
	}

	// 判断字符串是否为空字符串。
	public static boolean isEmpty(String aString) {
		return aString == null || aString.trim().length() == 0;
	}

    // 判断字符串是否为空字符串。
    public static boolean isEmpty(CharSequence aString) {
        return aString == null || TextUtils.isEmpty(aString);
    }

	/**
	 * 判断是否大于0的整数
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isLessThanZeroInteger(String str) {
		int zero = 0;
		try {
			zero = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			LogUtil.w(TAG, str + " is not a Integer number");
		}
		return zero <= 0;
	}

	/**
	 * 判断字符串是否是浮点数
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isDouble(String value) {
		try {
			Double.parseDouble(value);
			if (value.contains("."))
				return true;
			return false;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	// 格式化手机号码
	public static String formatPhoneNum(String aPhoneNum) {
		String first = aPhoneNum.substring(0, 3);
		String end = aPhoneNum.substring(7, 11);
		String phoneNumber = first + "****" + end;
		return phoneNumber;
	}

	/**
	 * 将 带格式的日期时间字符串dt转换为不带格式的日期时间字符串
	 * 
	 * @param dt
	 * @return
	 */
	public static String formatDateStrToShortDateStr(String dt) {
		try {
			return new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
					.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dt));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 检查字符串是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		if(isEmpty(str))
			return false;
		// 表达式的功能：验证必须为数字（整数或小数）
		Pattern p = Pattern.compile("[0-9]+(.[0-9]+)?");
		Matcher m = p.matcher(str);
		return m.matches();
	}

	public static boolean isLetter(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (!(s.charAt(i) >= 'A' && s.charAt(i) <= 'Z') && !(s.charAt(i) >= 'a' && s.charAt(i) <= 'z')) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 去除字符串中空格
	 * 
	 * @param aString
	 * @return
	 */
	public static String clearSpaces(String aString) {
		StringTokenizer aStringTok = new StringTokenizer(aString, " ", false);
		String aResult = "";
		while (aStringTok.hasMoreElements()) {
			aResult += aStringTok.nextElement();
		}
		return aResult;
	}

	/**
	 * 
	 * 限制数字输入范围
	 * 
	 * @param start
	 *          最小数
	 * @param end
	 *          最大数
	 * @param str
	 *          输入的数
	 * @return
	 */
	public static boolean isWuToSAN(int start, int end, String str) {
		if (str.length() != 0 && !str.trim().equals("")) {
			if (start <= Integer.valueOf(str) && end >= Integer.valueOf(str)) {
				return true;
			}
		}
		return false;
	}

	/***
	 * 原16进制字符串取反,返回16进制数据
	 * 
	 * 专用于ATU密码的自动生成
	 * 
	 * @param Str
	 *          原字符串
	 * @return 16进制数据
	 */
	public static String getHEX(String Str) {
		String tempStr = Str.toUpperCase(Locale.getDefault()).replace("0X", "").replace("0x", "");
		StringBuilder OutStr = new StringBuilder();
		for (int i = 0; i < tempStr.length(); i++) {
			int tmpInt = 0;
			tmpInt = Str.charAt(i) - 48;
			tmpInt = tmpInt > 9 ? tmpInt - 7 : tmpInt;
			tmpInt = 15 - tmpInt;
			tmpInt += '0';
			tmpInt = tmpInt > '9' ? tmpInt + 7 : tmpInt;
			OutStr.append((char) (tmpInt));
		}
		tempStr = null;
		return OutStr.toString();
	}

	/**
	 * 格式化获取的经纬度数值，取到小数点后8位
	 * 
	 * @param s
	 * @return
	 */
	public static String formatStr(String s) {
		String para = "";
		if (s.indexOf(".") > 0) {
			String st = s.substring(s.indexOf(".") + 1, s.length());
			if (st.length() >= 8) {// 如果小数点后足够8位
				para = s.substring(0, s.indexOf(".") + 9);
			} else {// 如果小数点后不足8位
				para = s;
			}
		} else {
			para = s + ".00000000";
		}
		return para;
	}

	/***
	 * 字符串编码
	 * 
	 * @param str
	 *          源字符串
	 * @param newCharset
	 *          新的编码
	 * @return 编码后的字符串
	 * @throws Exception
	 */
	public static String changeCharset(String str, String newCharset) throws Exception {
		if (str != null) {
			// 用默认字符编码解码字符串。
			byte[] bs = str.getBytes("GBK");
			// 用新的字符编码生成字符串
			return new String(bs, newCharset);
		}
		return "";
	}

	/***
	 * 判断字符串是否乱码
	 * 
	 * @param str
	 *            字符串
	 * @return
	 */
	public static boolean isMessyCode(String str) {
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == 0xfffd) {
				return true;
			}
		}
		return false;
	}

	/***
	 * 判断给定值是否在指定范围内
	 * 
	 * @param value
	 *          指定值
	 * @param min
	 *          最小值
	 * @param max
	 *          最大值
	 * @return
	 */
	public static boolean isRange(int value, int min, int max) {
		if (value >= min && value <= max)
			return true;
		return false;
	}

	/**
	 * 获取中英文环境
	 * 
	 * @return
	 */
	public static String getLanguage() {
		String language = Locale.getDefault().getLanguage();
		if (language.endsWith("zh"))// 中文
			return "cn";
		return "en";// 英文
	}
	
	/**
	 * 字符串格式化为百分比,如果为空,则返回-,如果是整数,不保留小数位，否则保留2位小数 注意：此函数不包含%符号
	 * 
	 * @param str 原字符串
	 * @return 格式化后的字符串
	 */
	public static String formatPercent(String str) {
		if (null == str || str.trim().length() <= 0 || str.trim().equalsIgnoreCase("null")) {
			return "-";
		}
		try {
			double val = Double.parseDouble(str);
			int val2 = (int) (val * 100);
			if (val2 == val * 100) {// 说明是整数
				return val2 + "";
			}
			return decimalFormat.format(val * 100) + "";
		} catch (Exception ex) { 
		}
		return "-";
	}

	/**
	 * 字符串格式化， 1.如果为空则返回- 2.如果为数值：浮点数保留2为,整数取整 否则返回原字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String formatString(String str) {
		if (null == str || str.trim().length() <= 0 || str.equalsIgnoreCase("null")) {
			return "-";
		}
		try {
			Double val = Double.parseDouble(str);
			int val2 = val.intValue();
			if (val2 == val) {// 说明是整数
				return val2 + "";
			}
			return decimalFormat.format(val) + "";
		} catch (Exception ex) {  
		}
		return str;
	}
	
	/***
	 * 格式化字符串
	 * 
	 * @param str
	 * @return 去掉时间字符串最后的.987数字
	 */
	public static String formatDate(String str){
		if (null == str || str.trim().length() <= 0 || str.equalsIgnoreCase("null")) {
			return "-";
		}
		if(str.contains(".")){
			return str.substring(0,str.lastIndexOf("."));
		}
		return str;
	}
	public static String gbkToUtf8(String str) {
		String result = "";
		try {
			result = new String(str.getBytes("GBK"), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
