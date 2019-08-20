package com.walktour.Utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 手机信息工具类
 * 
 * @author weirong.fan
 * 
 */
public class MobileInfoUtil {
	
	/**
	 * 私有构造器，防止外部构造
	 */
	private MobileInfoUtil() {
		super();
	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @param context
	 *            上下文
	 * @return 屏幕宽度
	 */
	public static int getScreenWidth(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
	
	/**
	 * 获取屏幕高度
	 * 
	 * @param context
	 *            上下文
	 * @return 屏幕高度
	 */
	public static int getScreenHeight(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}
	
	/**
	 * <p>
	 * Description:判断sd卡是否存在
	 * </p>
	 * 
	 * @author weirong.fan
	 * @date 2012-3-2 上午11:39:51
	 * @return boolean
	 */
	public static boolean isSDCardAvailable() {
		boolean flag = false;
		try {
			flag = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		} catch (Exception ex) {
			flag = false;
		}
		return flag;
	}

	/**
	 * <p>
	 * Description:获取sd卡路径
	 * </p>
	 * 
	 * @author weirong.fan
	 * @date 2012-3-2 上午11:42:21
	 * @param context
	 * @return String
	 */
	public static String getSDPath(Context context) {
		File sdDir = null;
		if (isSDCardAvailable()) {
			sdDir = Environment.getExternalStorageDirectory();// 获取根目录
			return sdDir.toString();
		}
		return null;

	}

	/**
	 * 判断手机本身是否具有root权限
	 * @return
	 */
	public static boolean isDeviceRooted() {
		boolean flag = checkRootMethod1() || checkRootMethod2() || checkRootMethod3() || checkRootMethod4()||checkRootExecutable();
		if(flag&&checkRootExecutable()){//有些手机能检测到前面几项，但不能执行su命令,将其设置为非root手机.
			return true;
		}
		return false;
	}


	private static boolean checkRootMethod1() {
		String buildTags = android.os.Build.TAGS;
		return buildTags != null && buildTags.contains("test-keys");
	}


	private static boolean checkRootMethod2() {
		return new File("/system/app/Superuser.apk").exists();
	}


	private static boolean checkRootMethod3() {
		String[] paths = { "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
				"/system/bin/failsafe/su", "/data/local/su" };
		for (String path : paths) {
			if (new File(path).exists()) {
				return true;
			}
		}
		return false;
	}


	private static boolean checkRootMethod4() {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			if (in.readLine() != null) {
				return true;
			}
			return false;
		} catch (Throwable t) {
			return false;
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
	}

	public static boolean checkRootExecutable() {

		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("exit\n");
			os.flush();
			int exitValue = process.waitFor();
			if (exitValue == 0) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
                if (process!=null) {
                    process.destroy();
                }
            } catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * 判断是否是Volte
	 * @return
	 */
	public static boolean isVolte(Context context){
		try {
			if (Build.VERSION.SDK_INT >= 23) {
				TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				Class<? extends TelephonyManager> teleclass = telephonyManager.getClass();
				Method[] methods = teleclass.getMethods();
				for (Method m : methods) {
					System.out.println(m.getName());
				}
				Method method = teleclass.getDeclaredMethod("isVolteAvailable");
				method.setAccessible(true);
				boolean isvoLte = (boolean) method.invoke(telephonyManager);
				return isvoLte;
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}
		return false;
	}

	/***
	 * 开关VOLTE开关,此方法只有在系统权限目录下才可起作用
	 * @param context
	 * @param enable
	 */
	public static void setVolteEnable(Context context,boolean enable) {
		String volteStr="";
		try {
			Settings.Global Global = new Settings.Global();
			Class<?> GlobalClass = Global.getClass();

			Field field = GlobalClass.getDeclaredField("ENHANCED_4G_MODE_ENABLED");
			field.setAccessible(true);
			volteStr = (String) field.get(Global);
			if(null!=volteStr&&volteStr.length()>0){
				android.provider.Settings.Global.putInt(
						context.getContentResolver(), volteStr, enable ? 1 : 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


	}


	public static void SetVolteEnable2(Context context,boolean enable) {
		int value = enable ? 1 : 0;
		String volteStr = null;
		Settings.Global Global = new Settings.Global();
		Class<?> GlobalClass = Global.getClass();
		try {
			Field field = GlobalClass.getDeclaredField("ENHANCED_4G_MODE_ENABLED");
			field.setAccessible(true);
			volteStr = (String) field.get(Global);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//如果是你的App是预制为系统App的，那么直接改变即可
		if (!isDeviceRooted()) {
			android.provider.Settings.Global.putInt(context.getContentResolver(), volteStr, value);
		}

		if (isNonTtyOrTtyOnVolteEnabled(context)) {
			try {
				SubscriptionManager subManager = (SubscriptionManager) context
						.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

				Class<? extends SubscriptionManager> sunClass = subManager.getClass();
				Method method1 = sunClass.getDeclaredMethod("getDefaultVoicePhoneId");
				method1.setAccessible(true);
				int phoneid = (Integer) method1.invoke(subManager);

				Class<?> clazz = (Class<?>) Class.forName("com.android.ims.ImsManager");
				Constructor ct = clazz.getDeclaredConstructor(Context.class,int.class);
				ct.setAccessible(true);
				Object obj = ct.newInstance(context,phoneid);
				//setAdvanced4GMode
				Method method = clazz.getDeclaredMethod("setAdvanced4GMode", new Class[]{boolean.class});
				method.setAccessible(true);
				method.invoke(obj, enable);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	public static boolean isNonTtyOrTtyOnVolteEnabled(Context context) {
		if (getBooleanCarrierConfig(context, CarrierConfigManager.KEY_CARRIER_VOLTE_TTY_SUPPORTED_BOOL)) {
			return true;
		}

		String preferred = null;
		int mode = 0;
		try {
			Settings.Secure secure = new Settings.Secure();
			Class<?> secureClass = secure.getClass();
			Field field = secureClass.getDeclaredField("PREFERRED_TTY_MODE");
			field.setAccessible(true);
			preferred = (String) field.get(secure);

			TelecomManager telcom = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
			Class telcomClass = telcom.getClass();
			Field field1 = telcomClass.getDeclaredField("TTY_MODE_OFF");
			field1.setAccessible(true);
			mode = (Integer) field1.get(telcom);
		} catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		return Settings.Secure.getInt(context.getContentResolver(), preferred, mode) == mode;
	}

	private static boolean getBooleanCarrierConfig(Context context, String key) {
		CarrierConfigManager configManager = (CarrierConfigManager) context.getSystemService(Context.CARRIER_CONFIG_SERVICE);
		Class configManagerClass = configManager.getClass();
		PersistableBundle b = null;
		if (configManager != null) {
			try {
				b = configManager.getConfig();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		if (b != null) {
			return b.getBoolean(key);
		} else {
			try {
				Method method = configManagerClass.getDeclaredMethod("getDefaultConfig");
				method.setAccessible(true);
				PersistableBundle persistableBundle = (PersistableBundle) method.invoke(configManager);
				return persistableBundle.getBoolean(key);
			} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
}
