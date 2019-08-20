package com.walktour.base.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.ColorUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 类说明：系统状态栏工具类
 * Author: jinfeng.xie
 * Date: 2018/9/19
 */
public class SystemBarUtil {

    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    private static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";
    private static final String NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME = "navigation_bar_height_landscape";
    private static final String NAV_BAR_WIDTH_RES_NAME = "navigation_bar_width";

    /**
     * 修改状态栏为全透明
     *
     * @param activity
     */
    public static void transparencyStatusBar(Activity activity) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;  //防止系统栏隐藏时内容区域大小发生变化
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !OSUtils.isEMUI3_1()) {
                uiFlags |= initStatusBarAboveLOLLIPOP(uiFlags, window); //初始化5.0以上，包含5.0
            } else {
                initStatusBarBelowLOLLIPOP(activity); //初始化5.0以下，4.4以上沉浸式
            }
            window.getDecorView().setSystemUiVisibility(uiFlags);
        }
    }

    /**
     * 初始化5.0以上，包含5.0
     * */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static int initStatusBarAboveLOLLIPOP(int uiFlags, Window window) {
        // Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态栏遮住。
        uiFlags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ColorUtils.blendARGB(Color.TRANSPARENT, Color.TRANSPARENT, 0.0f));  //设置状态栏颜色

        return uiFlags;
    }

    /**
     * 初始化android 4.4和emui3.1状态栏
     */
    private static void initStatusBarBelowLOLLIPOP(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//透明状态栏

        createStatusBarView(activity); //创建一个假的状态栏
    }

    /**
     * 设置一个可以自定义颜色的状态栏
     */
    private static void createStatusBarView(Activity activity) {
        View statusBarView = new View(activity);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                getStatusBarHeight(activity));
        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);

        statusBarView.setBackgroundColor(ColorUtils.blendARGB(Color.TRANSPARENT, Color.TRANSPARENT, 0.0f));

        statusBarView.setVisibility(View.VISIBLE);
        ViewGroup viewGroup = (ViewGroup) statusBarView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(statusBarView);
        }
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        decorView.addView(statusBarView);
    }

    /**
     *  重新绘制标题栏高度，解决状态栏与顶部重叠问题
     *
     *  @param activity
     *  @param titleBarView 标题栏
     * */
    public static void remeasureTitleBar(Activity activity, View titleBarView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && titleBarView != null) {
            int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            titleBarView.measure(width, height);

            ViewGroup.LayoutParams layoutParams = titleBarView.getLayoutParams();
            int titleBarHeight = titleBarView.getMeasuredHeight() + getStatusBarHeight(activity);
            int titleBarPaddingTopHeight = titleBarView.getPaddingTop() + getStatusBarHeight(activity);

            layoutParams.height = titleBarHeight;
            titleBarView.setPadding(titleBarView.getPaddingLeft(), titleBarPaddingTopHeight,
                    titleBarView.getPaddingRight(), titleBarView.getPaddingBottom());

            titleBarView.setLayoutParams(layoutParams);
        }
    }

    /**
     * 修改底部导航栏为全透明
     *
     * @param activity
     */
    public static void transparencyNavBar(Activity activity) {
        if (hasNavBar(activity)) {
            Window window = activity.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;  //防止系统栏隐藏时内容区域大小发生变化
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !OSUtils.isEMUI3_1()) {
                    uiFlags |= initNavBarAboveLOLLIPOP(uiFlags, activity); //初始化5.0以上，包含5.0
                } else {
                    initNavBarBelowLOLLIPOP(activity); //初始化5.0以下，4.4以上沉浸式
                }
                window.getDecorView().setSystemUiVisibility(uiFlags);
            }
        } else {
            Log.e("TAG", "当前设备没有导航栏或者低于4.4系统");
        }
    }

    /**
     * 初始化5.0以上，包含5.0
     * */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static int initNavBarAboveLOLLIPOP(int uiFlags, Activity activity) {
        // Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态栏遮住。
        uiFlags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        //Activity全屏显示，但导航栏不会被隐藏覆盖，导航栏依然可见，Activity底部布局部分会被导航栏遮住。
        uiFlags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        Window window = activity.getWindow();
        if (hasNavBar(activity)) {  //判断是否存在导航栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        window.setNavigationBarColor(ColorUtils.blendARGB(Color.TRANSPARENT, Color.TRANSPARENT, 0.0f));  //设置导航栏颜色为透明

        return uiFlags;
    }

    /**
     * 初始化android 4.4和emui3.1导航栏
     */
    private static void initNavBarBelowLOLLIPOP(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//透明状态栏

        if (hasNavBar(activity)) {  //判断是否存在导航栏，是否禁止设置导航栏
            //透明导航栏，设置这个，如果有导航栏，底部布局会被导航栏遮住
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            createNavBarView(activity);   //创建一个假的导航栏
        }
    }

    /**
     * 设置一个可以自定义颜色的导航栏
     */
    private static void createNavBarView(Activity activity) {
        View navigationBarView = new View(activity);

        FrameLayout.LayoutParams params;
        if (isNavAtBottom(activity)) {
            params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, getNavBarHeight(activity));
            params.gravity = Gravity.BOTTOM;
        } else {
            params = new FrameLayout.LayoutParams(getNavBarWidth(activity), FrameLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.END;
        }
        navigationBarView.setLayoutParams(params);
        navigationBarView.setBackgroundColor(ColorUtils.blendARGB(Color.TRANSPARENT, Color.TRANSPARENT, 0.0f));

        navigationBarView.setVisibility(View.VISIBLE);
        ViewGroup viewGroup = (ViewGroup) navigationBarView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(navigationBarView);
        }
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        decorView.addView(navigationBarView);
    }

    /**
     * 隐藏状态栏
     *
     * @param activity
     */
    public static void hideStatusBar(Activity activity) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;  //防止系统栏隐藏时内容区域大小发生变化
            uiFlags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.INVISIBLE;
            uiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            window.getDecorView().setSystemUiVisibility(uiFlags);
        }
    }

    /**
     * 显示状态栏
     *
     * @param activity
     */
    public static void showStatusBar(Activity activity) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;  //防止系统栏隐藏时内容区域大小发生变化
            uiFlags |= View.SYSTEM_UI_FLAG_VISIBLE;
            uiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            window.getDecorView().setSystemUiVisibility(uiFlags);
        }
    }

    /**
     * 隐藏底部导航栏
     *
     * @param activity
     */
    public static void hideNavBar(Activity activity) {
        if (hasNavBar(activity)) {
            Window window = activity.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;  //防止系统栏隐藏时内容区域大小发生变化
                uiFlags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;  //隐藏状态栏或者导航栏
                uiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                window.getDecorView().setSystemUiVisibility(uiFlags);
            }
        } else {
            Log.e("TAG", "当前设备没有导航栏或者低于4.4系统");
        }
    }

    /**
     * 显示底部导航栏
     *
     * @param activity
     */
    public static void showNavBar(Activity activity) {
        if (hasNavBar(activity)) {
            Window window = activity.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;  //防止系统栏隐藏时内容区域大小发生变化
                uiFlags |= View.SYSTEM_UI_FLAG_VISIBLE;
                uiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                window.getDecorView().setSystemUiVisibility(uiFlags);
            }
        } else {
            Log.e("TAG", "当前设备没有导航栏或者低于4.4系统");
        }
    }

    /**
     * 获取导航栏高度
     * */
    @TargetApi(14)
    private static int getNavBarWidth(Context context) {
        Resources res = context.getResources();
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (hasNavBar((Activity) context)) {
                return getInternalDimensionSize(res, NAV_BAR_WIDTH_RES_NAME);
            }
        }
        return result;
    }

    /**
     * 获取导航栏高度
     * */
    @TargetApi(14)
    private static int getNavBarHeight(Activity activity) {
        Resources res = activity.getResources();
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (hasNavBar(activity)) {
                boolean mInPortrait = (res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
                String key;
                if (mInPortrait) {
                    key = NAV_BAR_HEIGHT_RES_NAME;
                } else {
                    key = NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME;
                }
                return getInternalDimensionSize(res, key);
            }
        }
        return result;
    }

    /**
     * 是否有导航栏
     * */
    @TargetApi(14)
    private static boolean hasNavBar(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    /**
     * 获取内部尺寸
     * */
    private static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private static boolean isNavAtBottom(Activity activity) {
        Resources res = activity.getResources();
        boolean mInPortrait = (res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
        return (getSmallestWidthDp(activity) >= 600 || mInPortrait);
    }

    @SuppressLint("NewApi")
    private static float getSmallestWidthDp(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        } else {
            // TODO this is not correct, but we don't really care pre-kitkat
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        }
        float widthDp = metrics.widthPixels / metrics.density;
        float heightDp = metrics.heightPixels / metrics.density;
        return Math.min(widthDp, heightDp);
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本,保持沉浸式状态
     *
     * @param activity
     * @param colorId
     */
    public static void setStatusBarColor(Activity activity, int colorId) {
        setStatusBarColor(activity, colorId, true, false);
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本
     *
     * @param activity
     * @param colorId  直接使用资源ID，即R.color.xxx
     * @param isFollow 是否保持沉浸式状态
     * @param isPadding 是否需要解决状态栏与标题栏重叠问题，主要用来解决小米系统
     */
    public static void setStatusBarColor(Activity activity, int colorId, boolean isFollow, boolean isPadding) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            if (!isFollow) {
                window.setStatusBarColor(colorId);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //使用SystemBarTint库使4.4版本状态栏变色，需要先将状态栏设置为透明
            transparencyStatusBar(activity);
            ViewGroup contentFrameLayout = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
            View parentView = contentFrameLayout.getChildAt(0);
            if (parentView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                parentView.setFitsSystemWindows(true);
            }
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(colorId);

            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            ViewGroup contentView = (ViewGroup) decorView.findViewById(android.R.id.content);
            if (isPadding) {
                contentView.setPadding(0, getStatusBarHeight(activity), 0, 0);
            } else {
                contentView.setPadding(0, 0, 0, 0);
            }

//            // 小米系统
//            if (isMIUI()) {
//                ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
//                ViewGroup contentView = (ViewGroup) decorView.findViewById(android.R.id.content);
//                if (isPadding) {
//                    contentView.setPadding(0, getStatusBarHeight(activity), 0, 0);
//                } else {
//                    contentView.setPadding(0, 0, 0, 0);
//                }
//            }
        }
    }

    /**
     * 设置状态栏黑色字体图标，
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @param activity
     * @param dark     是否把状态栏字体及图标颜色设置为深色
     * @return 1:MIUUI 2:Flyme 3:android6.0
     */
    public static void setStatusBarLightMode(Activity activity, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isMIUI()) {
                MIUISetStatusBarLightMode(activity.getWindow(), dark);
            } else if (isFlyme()) {
                FlymeSetStatusBarLightMode(activity.getWindow(), dark);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // android 6.0以上
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            }
        }
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * Sets status bar dark font.
     * 设置状态栏字体颜色，android6.0以上
     */
    public static int setStatusBarDarkFont(int uiFlags, boolean isDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isDark) {
            return uiFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            return uiFlags;
        }
    }

    /**
     * 已知系统类型时，设置状态栏黑色字体图标。
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @param activity
     * @param type     1:MIUUI 2:Flyme 3:android6.0
     */
    public static void setStatusBarDarkMode(Activity activity, int type) {
        if (type == 1) {
            MIUISetStatusBarLightMode(activity.getWindow(), true);
        } else if (type == 2) {
            FlymeSetStatusBarLightMode(activity.getWindow(), true);
        } else if (type == 3) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }

    /**
     * 清除MIUI或flyme或6.0以上版本状态栏黑色字体，即白色字体
     */
    public static void setStatusBarLightMode(Activity activity, int type) {
        if (type == 1) {
            MIUISetStatusBarLightMode(activity.getWindow(), false);
        } else if (type == 2) {
            FlymeSetStatusBarLightMode(activity.getWindow(), false);
        } else if (type == 3) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    /**
     * 通过反射的方式获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取底部导航栏高度
     *
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        //获取NavigationBar的高度
        int navigationHeight = resources.getDimensionPixelSize(resourceId);
        return navigationHeight;
    }

    /**
     * 获取是否存在NavigationBar
     *
     * @return
     * */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }

    /**
     * 设置状态栏
     *
     * @param activity
     * @param useThemeStatusBarColor   是否要状态栏的颜色，不设置则为透明色
     * @param withoutUseStatusBarColor 是否不需要使用状态栏为暗色调
     */
    public static void setStatusBar(Activity activity, boolean useThemeStatusBarColor, boolean withoutUseStatusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            if (useThemeStatusBarColor) {
                activity.getWindow().setStatusBarColor(Color.WHITE);
            } else {
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !withoutUseStatusBarColor) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    /**
     * 设置状态栏文字色值为深色调
     *
     * @param useDart 是否使用深色调
     *
     * @param activity
     */
    public static void setStatusTextColor(Activity activity, boolean useDart) {
        if (isFlyme()) {
            processFlyMe(useDart, activity);
        } else if (isMIUI()) {
            processMIUI(useDart, activity);
        } else {
            if (useDart) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }
            activity.getWindow().getDecorView().findViewById(android.R.id.content).setPadding(0, 0, 0, getNavigationBarHeight(activity));
        }
    }

    /**
     * 判断手机是否是魅族
     *
     * @return
     */
    public static boolean isFlyme() {
        try {
            // Invoke Build.hasSmartBar()
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * 判断手机是否是小米
     *
     * @return
     */
    public static boolean isMIUI() {
        try {
            final BuildProperties prop = BuildProperties.newInstance();
            return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        } catch (final IOException e) {
            return false;
        }
    }

    /**
     * 改变魅族的状态栏字体为黑色，要求FlyMe4以上
     */
    private static void processFlyMe(boolean isLightStatusBar, Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        try {
            Class<?> instance = Class.forName("android.view.WindowManager$LayoutParams");
            int value = instance.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON").getInt(lp);
            Field field = instance.getDeclaredField("meizuFlags");
            field.setAccessible(true);
            int origin = field.getInt(lp);
            if (isLightStatusBar) {
                field.set(lp, origin | value);
            } else {
                field.set(lp, (~value) & origin);
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    /**
     * 改变小米的状态栏字体颜色为黑色, 要求MIUI6以上  lightStatusBar为真时表示黑色字体
     */
    private static void processMIUI(boolean lightStatusBar, Activity activity) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags",int.class,int.class);
            extraFlagField.invoke(activity.getWindow(), lightStatusBar? darkModeFlag : 0, darkModeFlag);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    /**
     * 判断手机支不支持状态栏字体变色
     *
     * @return the boolean
     */
    public static boolean isSupportStatusBarDarkFont() {
        if (isMIUI()) {
            return OSUtils.isMIUI6Later();
        } else if (isFlyme()) {
            return OSUtils.isFlymeOS4Later();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true;
        } else {
            return false;
        }
    }
}
