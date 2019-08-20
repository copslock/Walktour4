package com.walktour.service.phoneinfo.utils;

import android.util.Log;

import com.walktour.base.util.LogUtil;
import com.walktour.service.phoneinfo.model.LTESignalStrength;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/***
 * 反射机制获取LTE参数数据
 * 
 * @author weirong.fan
 * 
 */
public final class ReflectionUtils {
	public static final String TAG = "ReflectionUtils";

	public static final LTESignalStrength dumpClassToLteSignalStrength(Class<?> mClass, Object mInstance) {
		if (mClass == null || mInstance == null)
			return null;

		LTESignalStrength lte = new LTESignalStrength();

		final Method[] mMethods = mClass.getMethods();
		final Field[] mFields = mClass.getDeclaredFields();
		String fieldValue = null;
		Object mRet = null;
		for (final Method mMethod : mMethods) {
			mMethod.setAccessible(true);
			try {
				if (mMethod.getName().contains("get")) {
					if (mMethod.getParameterTypes().length > 0)
						continue;
					mRet = mMethod.invoke(mInstance);
					fieldValue = (mRet == null) ? "-1" : mRet.toString();
					if ("getDbm".equals(mMethod.getName())) {
						lte.strength = Integer.valueOf(fieldValue);
					} else if ("getLevel".equals(mMethod.getName())) {
						lte.level = Integer.valueOf(fieldValue);
					} else if ("getLteRssnr".equals(mMethod.getName())) {
						lte.sinr = Integer.valueOf(fieldValue);
					} else if ("getLteCqi".equals(mMethod.getName())) {
						lte.cqi = Integer.valueOf(fieldValue);
					} else if ("getLteRsrq".equals(mMethod.getName())) {
						lte.rsrq = Integer.valueOf(fieldValue);
					} else if ("getTimingAdvance".equals(mMethod.getName())) {
						lte.timingadvance = Integer.valueOf(fieldValue);
					} else if ("getLteRsrp".equals(mMethod.getName())) {
						lte.rsrp = Integer.valueOf(fieldValue);
					} else if ("getAsuLevel".equals(mMethod.getName())) {
						if (Integer.valueOf(fieldValue) >= 0)
							lte.rsrp_2g = -113 + Integer.valueOf(fieldValue) * 2;
						else
							lte.rsrp_2g = Integer.valueOf(fieldValue);
					} else if ("getGsmDbm".equals(mMethod.getName())) {
						lte.strength_2g = Integer.valueOf(fieldValue);
					}
				}

			} catch (Exception e) {
				LogUtil.e(TAG, e.getMessage(), e);
			}
		}

		for (final Field mField : mFields) {
			mField.setAccessible(true);
			try {
				if ((lte.strength == -1 || lte.strength == 0) && "mLteSignalStrength".equals(mField.getName())) {
					if (Integer.valueOf(fieldValue) >= 0)
						lte.strength = -113 + mField.getInt(mInstance) * 2;
					else
						lte.strength = mField.getInt(mInstance);
				} else if ((lte.sinr == -1 || lte.sinr == 0) && "mLteRssnr".equals(mField.getName())) {
					lte.sinr = (mField.getInt(mInstance));
				} else if ((lte.cqi == -1 || lte.cqi == 0) && "mLteCqi".equals(mField.getName())) {
					lte.cqi = (mField.getInt(mInstance));
				} else if ((lte.rsrq == -1 || lte.rsrq == 0) && "mLteRsrq".equals(mField.getName())) {
					lte.rsrq = (mField.getInt(mInstance));
				} else if ((lte.rsrp == -1 || lte.rsrp == 0) && "mLteRsrp".equals(mField.getName())) {
					lte.rsrp = mField.getInt(mInstance);
				}
			} catch (Exception e) {
				LogUtil.e(TAG, e.getMessage(), e);
			}
		}

		if (lte.strength > 0) {
			lte.strength = lte.rsrp;
		}
		return lte;
	}

	/**
	 * Dumps a {@link Class}'s {@link Method}s and {@link Field}s as a String.
	 */
	public static final String dumpClass(Class<?> clazz, Object mInstance) {
		if (clazz == null || mInstance == null)
			return null;

		String mStr = clazz.getSimpleName() + "\n\n";

		mStr += "FIELDS\n\n";

		final Field[] mFields = clazz.getDeclaredFields();

		for (final Field mField : mFields) {
			mField.setAccessible(true);

			mStr += mField.getName() + " (" + mField.getType() + ") = ";

			try {
				mStr += mField.get(mInstance).toString();
			} catch (Exception e) {
				mStr += "null";
				Log.e(TAG, "Could not get Field `" + mField.getName() + "`.", e);
			}

			mStr += "\n";
		}

		mStr += "METHODS\\nn";

		// Dump all methods.

		final Method[] mMethods = clazz.getMethods();

		for (final Method mMethod : mMethods) {
			mMethod.setAccessible(true);

			mStr += mMethod.getReturnType() + " " + mMethod.getName() + "() = ";

			try {
				final Object mRet = mMethod.invoke(mInstance);
				mStr += (mRet == null) ? "null" : mMethod.invoke(mInstance).toString();
			} catch (Exception e) {
				mStr += "null";
				Log.e(TAG, "Could not get Method `" + mMethod.getName() + "`.", e);
			}

			mStr += "\n";
		}

		return mStr;
	}

	/**
	 * @return A string containing the values of all static {@link Field}s.
	 */
	public static final String dumpStaticFields(Class<?> mClass, Object mInstance) {
		if (mClass == null || mInstance == null)
			return null;

		String mStr = mClass.getSimpleName() + "\n\n";

		mStr += "STATIC FIELDS\n\n";

		final Field[] mFields = mClass.getDeclaredFields();

		for (final Field mField : mFields) {
			if (ReflectionUtils.isStatic(mField)) {
				mField.setAccessible(true);

				mStr += mField.getName() + " (" + mField.getType() + ") = ";

				try {
					mStr += mField.get(mInstance).toString();
				} catch (Exception e) {
					mStr += "null";
					Log.e(TAG, "Could not get Field `" + mField.getName() + "`.", e);
				}

				mStr += "\n";
			}
		}

		return mStr;
	}

	/**
	 * @return True if the {@link Field} is static.
	 */
	public final static boolean isStatic(Field field) {
		final int modifiers = field.getModifiers();
		return (Modifier.isStatic(modifiers));
	}
}