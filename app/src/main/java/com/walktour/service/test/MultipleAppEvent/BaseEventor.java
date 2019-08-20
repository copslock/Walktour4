package com.walktour.service.test.MultipleAppEvent;

import com.walktour.Utils.EventBytes;
import com.walktour.gui.WalktourApplication;

import java.util.Map;

/**
 * 事件处理者基类
 *
 * @author zhicheng.chen
 * @date 2018/8/16
 */
public abstract class BaseEventor implements IEvent {


    public static final String CODE = "code";//错误码
    public static final String DESC = "desc"; // 错误描述
    public static final String SUB_EVENT = "sub_event";
    public static final String INNER_DELAY = "inner_delay";//时延
    public static final String UP_BYTES = "up_bytes";// 上行字节
    public static final String DOWN_BYTES = "down_bytes";// 下行字节
    public static final String MULTIPLE_ACTION_TYPE = "multiple_action_type";
    public static final String SUB_EVENT_MPACTION_SUCCESS = "SUB_EVENT_MPACTION_SUCCESS";
    public static final String SUB_EVENT_MPACTION_FAILED = "SUB_EVENT_MPACTION_FAILED";
    public static final String SUB_EVENT_MPACTION_STATUS = "SUB_EVENT_MPACTION_STATUS";
    public static final String SUB_EVENT_MPACTION_START = "SUB_EVENT_MPACTION_START";
    public static final String SUB_EVENT_MPACTION_QOS = "SUB_EVENT_MPACTION_QOS";

    /**
     * error code for ott task
     */
    public enum Error {
        UNKNOWN(1000),
        USER_STOP(1002),
        TIMEOUT(1003),
        PPP_DROP(1006),
        SCRIPT_TIMEOUT(2001),
        PARAMS_FAIL(2002),
        ANALYSIS_TIMEOUT(2003),
        APP_NOT_INSTALL(2004),
        APPTEST_INIT_FAIL(2005);

        private int value;

        Error(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public EventBytes Event(int flag) {
        return EventBytes.Builder(WalktourApplication.getAppContext(), flag);
    }

    public int getInt(Map<String, String> map, String key) {
        try {
            if (map.containsKey(key)) {
                return Integer.parseInt(map.get(key));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getDouble(Map<String, String> map, String key) {
        try {
            if (map.containsKey(key)) {
                return Double.parseDouble(map.get(key));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
