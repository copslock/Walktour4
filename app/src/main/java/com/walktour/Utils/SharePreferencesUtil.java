package com.walktour.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/***
 * 保存数据
 *
 * @author weirong.fan
 *
 */
public class SharePreferencesUtil {

    /**
     * SharedPreferences文件名
     */
    private SharedPreferences mSharedPreferences;

    private static SharePreferencesUtil instance;

    @SuppressWarnings("deprecation")
    private SharePreferencesUtil(Context context) {
        mSharedPreferences = context.getSharedPreferences("com.walktour.gui",
                Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE | Context.MODE_MULTI_PROCESS);
    }

    public static SharePreferencesUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (SharePreferencesUtil.class) {
                if (instance == null) {
                    instance = new SharePreferencesUtil(context);
                }
            }
        }
        return instance;
    }

    /**
     * 保存字符串
     *
     * @param key
     * @param value
     */
    public void saveString(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 获取字符串
     *
     * @param key
     * @param defValue
     * @return
     */
    public String getString(String key, String... defValue) {
        if (defValue.length > 0)
            return mSharedPreferences.getString(key, defValue[0]);
        return mSharedPreferences.getString(key, "");

    }

    /**
     * 更新浮点数
     *
     * @param key
     * @param value
     * @return
     */
    public void putFloat(String key, float value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putFloat(key, value);
        editor.commit();
    }


    /**
     * 获取浮点数
     *
     * @param key
     * @param defValue
     * @return
     */
    public float getFloat(String key, float... defValue) {
        if (defValue.length > 0)
            return mSharedPreferences.getFloat(key, defValue[0]);
        return mSharedPreferences.getFloat(key, 0);

    }

    /**
     * 保存布尔值
     *
     * @param key
     * @param value
     */
    public void saveBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 获取布尔值
     *
     * @param key
     * @param defValue
     * @return
     */
    public Boolean getBoolean(String key, Boolean... defValue) {
        if (defValue.length > 0)
            return mSharedPreferences.getBoolean(key, defValue[0]);
        return mSharedPreferences.getBoolean(key, false);
    }

    /**
     * 保存整形
     *
     * @param key
     * @param value
     */
    public void saveInteger(String key, Integer value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 保存整形
     *
     * @param key
     * @param value
     */
    public boolean saveIntegerWithResult(String key, Integer value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    /**
     * 获取整形
     *
     * @param key
     * @param defValue
     * @return
     */
    public Integer getInteger(String key, Integer... defValue) {
        if (defValue.length > 0)
            return mSharedPreferences.getInt(key, defValue[0]);
        return mSharedPreferences.getInt(key, 0);

    }

    /**
     * 保存长整形
     *
     * @param key
     * @param value
     */
    public void saveLong(String key, Long value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }
    public void saveFloat(String key, Float i) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putFloat(key, i);
        editor.commit();
    }
    /**
     * 保存对象
     */
    public <T> boolean saveObjectToShare(
            String key, Object object, Class<T> classes) {
        if (object == null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit().remove(key);
            return editor.commit();
        }
        try {
            String jsonStr = new Gson().toJson(object);
            saveString(key, jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
//
//        if (object == null) {
//            SharedPreferences.Editor editor = mSharedPreferences.edit().remove(key);
//            return editor.commit();
//        }
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ObjectOutputStream oos = null;
//        try {
//            oos = new ObjectOutputStream(baos);
//            oos.writeObject(object);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//// 将对象放到OutputStream中
//// 将对象转换成byte数组，并将其进行base64编码
//        String objectStr = new String(Base64.encode(baos.toByteArray(),
//                Base64.DEFAULT));
//        try {
//            baos.close();
//            oos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        SharedPreferences.Editor editor = mSharedPreferences.edit();
//// 将编码后的字符串写到base64.xml文件中
//        editor.putString(key, objectStr);
        return true;
    }


    /**
     * 得到对象
     */
    public  <T>T getObjectFromShare(String key, Class<T> tClass) {
        try {
            String jsonStr = getString(key, "");
            T obj = new Gson().fromJson(jsonStr, tClass);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取长整形
     *
     * @param key
     * @param defValue
     * @return
     */
    public Long getLong(String key, Long... defValue) {
        if (defValue.length > 0)
            return mSharedPreferences.getLong(key, defValue[0]);
        return mSharedPreferences.getLong(key, 0);

    }


}
