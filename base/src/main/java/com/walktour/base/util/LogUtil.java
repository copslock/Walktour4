package com.walktour.base.util;

import android.os.Environment;
import android.util.Log;

/**
 * 系统Log类的中转实现
 * 此处用于代码中日志输出的控制，正版本中哪些类型的日志不输出到控制台时在不前类中处理
 * @author tangwq
 * @version 2.0
 * @author Max  统一一个LogUtil
 */
public final class LogUtil {
    //应用Log tag
    public static final String APPTAG = "Walktour";
    /**
     * 是否仅保存当前应用log
     */
    public static boolean ONLYAPPLOG = true;
    /**
     * 当前是否是调试状态下，只有在调试模式下显示代码跳转
     */
    private static boolean isDebug = true;

    /**
     * 保存LOG日志的目录
     */
    public static final String SAVE_LOG_DIR_PATH = Environment.getExternalStorageDirectory().getPath() + "/Walktour";

    /**
     * 保存崩溃日志的路径
     */
    public static final String SAVE_EXCEPTION_PATH = Environment.getExternalStorageDirectory().getPath() + "/Walktour/log/exception.txt";

    private static StringBuffer sb1=new StringBuffer();
    private static StringBuffer sb2=new StringBuffer();
    public static int v(String tag, String msg) {
        if (ONLYAPPLOG) {
            return Log.v(APPTAG, wrapLog(tag, msg));
        }
        return Log.v(tag, wrapLog(tag, msg));
    }


    public static int v(String tag, String msg, Throwable tr) {
        if (ONLYAPPLOG) {
            return Log.v(APPTAG, wrapLog(tag, msg), tr);
        }
        return Log.v(tag, wrapLog(tag, msg), tr);
    }

    public static int d(String tag, String msg) {
        if (ONLYAPPLOG) {
            return Log.d(APPTAG, wrapLog(tag, msg));
        }
        return Log.d(tag, wrapLog(tag, msg));
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (ONLYAPPLOG) {
            return Log.d(APPTAG, wrapLog(tag, msg), tr);
        }
        return Log.d(tag, wrapLog(tag, msg), tr);
    }

    public static int i(String tag, String msg) {
        if (ONLYAPPLOG) {
            return Log.i(APPTAG, wrapLog(tag, msg));
        }
        return Log.i(tag, wrapLog(tag, msg));
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (ONLYAPPLOG) {
            return Log.i(APPTAG, wrapLog(tag, msg), tr);
        }
        return Log.i(tag, wrapLog(tag, msg), tr);
    }

    public static int w(String tag, String msg) {
        if (ONLYAPPLOG) {
            return Log.w(APPTAG, wrapLog(tag, msg));
        }
        return Log.w(tag, wrapLog(tag, msg));
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (ONLYAPPLOG) {
            return Log.w(APPTAG, wrapLog(tag, msg), tr);
        }
        return Log.w(tag, wrapLog(tag, msg), tr);
    }

    public static int w(String tag, Throwable tr) {
        if (ONLYAPPLOG) {
            return Log.w(APPTAG, tag + ":" + tr);
        }
        return Log.w(tag, tr);
    }

    public static int e(String tag, String msg) {
        if (ONLYAPPLOG) {
            return Log.e(APPTAG, wrapLog(tag, msg));
        }
        return Log.e(tag, wrapLog(tag, msg));
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (ONLYAPPLOG) {
            return Log.e(APPTAG, wrapLog(tag, msg), tr);
        }
        return Log.e(tag, wrapLog(tag, msg), tr);
    }

    public static void setDebug(boolean debug) {
        isDebug = debug;
    }

    /**
     * 为打印内容加上打印日志的位置信息
     *
     * @param tag
     * @param msg
     * @return
     */
    private static String wrapLog(String tag, String msg) {
        sb2.setLength(0);
        sb2.append(isDebug ? getLogWrapper() : "");
        sb2.append(ONLYAPPLOG ? tag + ":" + msg : msg);
        return  sb2.toString();
    }

    /**
     * 获取日志打印位置信息（类名行数方法）并支持点击跳转到该日志位置
     *
     * @return
     */
    private static String getLogWrapper() {
        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
        StackTraceElement targetElement = stackTraceElement[5];
        String fullClassName = targetElement.getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        if (className.contains("$")) {
            className = className.split("\\$")[0];
        }
        String methodName = targetElement.getMethodName();
        String lineNumber = String.valueOf(targetElement.getLineNumber());
        sb1.setLength(0);
        sb1.append("[ (");
        sb1.append(className);
        sb1.append(".java:");
        sb1.append(lineNumber);
        sb1.append(") # ");
        sb1.append(methodName);
        sb1.append("() ] ");
        return sb1.toString();
    }

}