package com.walktour.service.test.MultipleAppEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhicheng.chen
 * @date 2018/12/14
 */
public class StringSpilt {

    public static Map<String, String> spiltKeyValues(String script_str) {
        Map<String, String> key_values = new HashMap();
        key_values.clear();
        if (script_str != null && script_str.trim().length() > 0) {
            String[] key_value = script_str.split("\r\n");
            String[] var3 = key_value;
            int var4 = key_value.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                String kv = var3[var5];
                String[] ss = kv.split("=");
                if (ss.length == 2) {
                    key_values.put(ss[0], ss[1]);
                }
            }
        }

        return key_values;
    }
}
