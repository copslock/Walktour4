package com.walktour.base.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by LinYi on 2017/3/4.
 * <p>desc:吐司工具，解决连续弹吐司不好的体验</p>
 */

public class ToastUtil {

    private static Toast TOAST_SHORT;
    private static Toast TOAST_LONG;

    private ToastUtil() {
        //No instances
    }

    /**
     * 弹出短时长土司
     * @param context
     * @param msg 文字内容
     */
    public static void showShort(Context context, String msg) {
        if (TOAST_SHORT == null) {
            TOAST_SHORT = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        } else {
            TOAST_SHORT.setText(msg);
        }
        TOAST_SHORT.show();
    }
    /**
     * 弹出短时长土司
     * @param context
     * @param msgRes 文字内容资源id
     */
    public static void showShort(Context context, int msgRes) {
        showShort(context, context.getString(msgRes));
    }
    /**
     * 弹出长时长土司
     * @param context
     * @param msg 文字内容
     */
    public static void showLong(Context context, String msg) {
        if (TOAST_LONG == null) {
            TOAST_LONG = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG);
        } else {
            TOAST_LONG.setText(msg);
        }
        TOAST_LONG.show();
    }
    /**
     * 弹出长时长土司
     * @param context
     * @param msgRes 文字内容资源id
     */
    public static void showLong(Context context, int msgRes) {
        showLong(context, context.getString(msgRes));
    }

}
