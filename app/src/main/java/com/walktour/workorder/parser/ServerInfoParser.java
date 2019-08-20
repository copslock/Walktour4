package com.walktour.workorder.parser;

import com.walktour.workorder.model.ServerInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;


/**
 * 服务器信息解析器
 * Author: ZhengLei
 *   Date: 2013-6-9 上午10:00:49
 */
public class ServerInfoParser extends XmlParser {
	private ServerInfo serverInfo;

	public ServerInfoParser(Reader reader) {
		super(reader);
	}

	public ServerInfoParser(String fileName) {
		super(fileName);
	}

	@Override
	public void parse() {
		try {
			while(eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
					case XmlPullParser.START_DOCUMENT: // 判断当前事件是否是文档开始事件
						
						break;
					case XmlPullParser.START_TAG: // 判断当前事件是否是标签元素开始事件
						if("svrinfo".equals(parser.getName())) { // 判断开始标签元素是否是svrinfo
							serverInfo = new ServerInfo();
						}
						if(serverInfo != null) {
							if("ipaddr".equals(parser.getName())) {
								serverInfo.setIpAddr(parser.nextText());
							} else if("port".equals(parser.getName())) {
								serverInfo.setPort(Integer.parseInt(parser.nextText()));
							} else if("account".equals(parser.getName())) {
								serverInfo.setAccount(parser.nextText());
							} else if("password".equals(parser.getName())) {
								serverInfo.setPassword(parser.nextText());
							} else if("svctype".equals(parser.getName())) {
								serverInfo.setSvrType(Integer.parseInt(parser.nextText()));
							}
						}
						break;
					case XmlPullParser.END_TAG: // 判断当前事件是否是标签元素结束事件
						
						break;
					default:
						break;
				} // end switch
				
				// 进入下一个元素并触发相应事件
				eventType = parser.next();
				
			} // end while
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 获取解析完的工单字典
	 * @return 工单字典
	 */
	@Override
	public ServerInfo getParseResult() {
		return serverInfo;
	}

}
