package com.walktour.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 把Json对象转换成字符串，由于系统代码生成的带有过多转义符，所以自己实现
 * 
 * @author jianchao.wang
 *
 */
public class JsonParseUtil {
	/** 生成的json字符串 */
	private StringBuilder mJson = new StringBuilder();

	/**
	 * 解析json对象
	 * 
	 * @param jsonObj
	 *          json对象
	 * @return
	 */
	public void parse(JSONObject jsonObj) throws JSONException {
		if (jsonObj == null)
			return;
		mJson.append("{");
		JSONArray names = jsonObj.names();
		for (int i = 0; i < names.length(); i++) {
			if (i > 0)
				mJson.append(",");
			Object key = names.get(i);
			if (key instanceof String) {
				mJson.append("\"").append(key).append("\"");
			} else {
				mJson.append(key);
			}
			mJson.append(":");
			Object value = jsonObj.get(String.valueOf(key));
			if (value == null) {
				continue;
			} else if (value instanceof JSONArray) {
				parse((JSONArray) value);
			} else if (value instanceof JSONObject) {
				parse((JSONObject) value);
			} else if (value instanceof String) {
				mJson.append("\"").append(value).append("\"");
			} else {
				mJson.append(value);
			}
		}
		mJson.append("}");
	}

	/**
	 * 解析json数组对象
	 * 
	 * @param jsonArray
	 *          数组对象
	 * @return
	 * @throws JSONException
	 */
	public void parse(JSONArray jsonArray) throws JSONException {
		if (jsonArray == null)
			return;
		mJson.append("[");
		for (int i = 0; i < jsonArray.length(); i++) {
			if (i > 0)
				mJson.append(",");
			Object value = jsonArray.opt(i);
			if (value == null) {
				continue;
			} else if (value instanceof JSONArray) {
				parse((JSONArray) value);
			} else if (value instanceof JSONObject) {
				parse((JSONObject) value);
			} else if (value instanceof String) {
				mJson.append("\"").append(value).append("\"");
			} else {
				mJson.append(value);
			}
		}
		mJson.append("]");
	}

	public String getJson() {
		return this.mJson.toString();
	}
}
