package com.walktour.Utils;

import android.net.Uri;

/**
 * 视频网站类型工具类
 * 
 * @author jianchao.wang
 *
 */
public class WebSiteTypeUtil {
	public static final int WEBSITE_TYPE_OTHER = 0;
	public static final int WEBSITE_TYPE_YOUTUBE = 1;
	public static final int WEBSITE_TYPE_YOUKU = 2;
	public static final int WEBSITE_TYPE_FACEBOOK = 3;
	public static final int WEBSITE_TYPE_IFENG = 4;
	public static final int WEBSITE_TYPE_SOHU = 5;
	public static final int WEBSITE_TYPE_IQIYI = 6;
	public static final int WEBSITE_TYPE_TENCENT = 7;

	/**
	 * 获得视频网站类型
	 * 
	 * @param url
	 *          网站URL
	 * @return
	 */
	public static int GetType(String url) {
		int type = WEBSITE_TYPE_OTHER;

		String host = Uri.parse(url).getHost();
		if ((host == null) || (host.isEmpty()))
			return WEBSITE_TYPE_OTHER;

		if ((host.indexOf("youtube") >= 0) || (host.indexOf("youtu.be") >= 0)) {
			type = WEBSITE_TYPE_YOUTUBE;
		} else if (host.indexOf("youku") >= 0) {
			type = WEBSITE_TYPE_YOUKU;
		} else if (host.indexOf("facebook") >= 0) {
			type = WEBSITE_TYPE_FACEBOOK;
		} else if (host.indexOf("ifeng") >= 0) {
			type = WEBSITE_TYPE_IFENG;
		} else if (host.indexOf("sohu") >= 0) {
			type = WEBSITE_TYPE_SOHU;
		} else if (host.indexOf("iqiyi") >= 0) {
			type = WEBSITE_TYPE_IQIYI;
		} else if (host.indexOf(".qq.") >= 0) {
			type = WEBSITE_TYPE_TENCENT;
		}

		return type;
	}

	/**
	 * 获取视频网站类型名称
	 * 
	 * @param url
	 *          网站URL
	 * @return
	 */
	public static String getTypeName(String url) {
		int type = GetType(url);
		switch (type) {
		case WEBSITE_TYPE_FACEBOOK:
			return "facebook";
		case WEBSITE_TYPE_IFENG:
			return "ifend";
		case WEBSITE_TYPE_IQIYI:
			return "iqiyi";
		case WEBSITE_TYPE_SOHU:
			return "sohu";
		case WEBSITE_TYPE_TENCENT:
			return "qq";
		case WEBSITE_TYPE_YOUKU:
			return "youku";
		case WEBSITE_TYPE_YOUTUBE:
			return "youtube";
		default:
			return "other";
		}
	}
}
