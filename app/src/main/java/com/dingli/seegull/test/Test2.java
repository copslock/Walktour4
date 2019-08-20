package com.dingli.seegull.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

public class Test2 {
	@SuppressWarnings("unused")
	private final static String TAG = "Test";
	
	public static void showIntentInfo(String tag, Intent intent) {
		if (intent == null) {
			Log.i(tag, "intent == null");
			return;
		}
		Bundle b = intent.getExtras();
		if (b == null) {
			Log.i(tag, "intent->action="+intent.getAction()+", Ext=null");
			return;
		}
		Log.i(tag, "intent->action="+intent.getAction());
		
		Set<String> keys = b.keySet();
		for(String key :keys) {
			Object obj = b.get(key);
			String type = obj.getClass().getName();
			
			if (type.equals("java.lang.String")) {
				Log.i(tag, "intent->"+key+"="+b.getString(key)
						+" [As "+obj.getClass().getName()+"]");
			} else if (type.equals("java.lang.Long")) {
				Log.i(tag, "intent->"+key+"="+b.getLong(key)
						+" [As "+type+"]");
			} else if (type.equals("java.lang.Integer")) {
				Log.i(tag, "intent->"+key+"="+b.getInt(key)
						+" [As "+type+"]");
			} else if (type.equals("java.lang.Boolean")) {
				Log.i(tag, "intent->"+key+"="+b.getBoolean(key)
						+" [As "+type+"]");
			} else {
				Log.i(tag, "intent->"+key+"="+b.get(key)
						+" [As "+type+"]");
			}
		}
	}
	public static String getIntentInfo(Intent intent) {
		String retval;
		if (intent == null) {
			retval = "intent == null";
			return retval;
		}
		Bundle b = intent.getExtras();
		if (b == null) {
			retval = "intent->action="+intent.getAction()+", Ext=null";
			return retval;
		}
		retval = "intent->action="+intent.getAction();
		
		Set<String> keys = b.keySet();
		for(String key :keys) {
			Object obj = b.get(key);
			String type = obj.getClass().getName();
			
			if (type.equals("java.lang.String")) {
				retval += "\nintent->"+key+" = "+b.getString(key)
						+" [As "+obj.getClass().getName()+"]";
			} else if (type.equals("java.lang.Long")) {
				retval += "\nintent->"+key+" = "+b.getLong(key)
						+" [As "+type+"]";
			} else if (type.equals("java.lang.Integer")) {
				retval += "\nintent->"+key+" = "+b.getInt(key)
						+" [As "+type+"]";
			} else if (type.equals("java.lang.Boolean")) {
				retval += "\nintent->"+key+" = "+b.getBoolean(key)
						+" [As "+type+"]";
			} else {
				retval += "\nintent->"+key+" = "+b.get(key)
						+" [As "+type+"]";
			}
		}
		
		//将字符打印到文件待查	 
		boolean out = false;
		if (out)
	    try {
	    	String filename = Environment.getExternalStorageDirectory().getAbsolutePath() +"/Scanner.log";
	    	File file = new File(filename);
	    	if (!file.exists())
		    	file.createNewFile();	
		    FileOutputStream fos = new FileOutputStream(file, true);
		    fos.write(retval.getBytes());
		    String s = "\n\n";
		    fos.write(s.getBytes());
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
		return retval;
	}
	
	public static class Hex {

    	/**
    	 * 用于建立十六进制字符的输出的小写字符数组
    	 */
    	private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5',
    			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    	/**
    	 * 用于建立十六进制字符的输出的大写字符数组
    	 */
    	private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5',
    			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    	/**
    	 * 将字节数组转换为十六进制字符数组
    	 * 
    	 * @param data
    	 *            byte[]
    	 * @return 十六进制char[]
    	 */
    	public static char[] encodeHex(byte[] data) {
    		return encodeHex(data, true);
    	}

    	/**
    	 * 将字节数组转换为十六进制字符数组
    	 * 
    	 * @param data
    	 *            byte[]
    	 * @param toLowerCase
    	 *            <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
    	 * @return 十六进制char[]
    	 */
    	public static char[] encodeHex(byte[] data, boolean toLowerCase) {
    		return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    	}

    	/**
    	 * 将字节数组转换为十六进制字符数组
    	 * 
    	 * @param data
    	 *            byte[]
    	 * @param toDigits
    	 *            用于控制输出的char[]
    	 * @return 十六进制char[]
    	 */
    	protected static char[] encodeHex(byte[] data, char[] toDigits) {
    		int l = data.length;
    		char[] out = new char[(l << 1)+l];
    		// two characters form the hex value.
    		for (int i = 0, j = 0; i < l; i++) {
    			out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
    			out[j++] = toDigits[0x0F & data[i]];
    			out[j++] = ' ';
    		}
    		return out;
    	}
    	protected static char[] encodeHex2(byte[] data, char[] toDigits) {
    		int l = data.length;
    		char[] out = new char[(l << 1)];
    		// two characters form the hex value.
    		for (int i = 0, j = 0; i < l; i++) {
    			out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
    			out[j++] = toDigits[0x0F & data[i]];
    		}
    		return out;
    	}

    	/**
    	 * 将字节数组转换为十六进制字符串
    	 * 
    	 * @param data
    	 *            byte[]
    	 * @return 十六进制String
    	 */
    	public static String encodeHexStr(byte[] data) {
    		return encodeHexStr(data, true);
    	}
    	public static String encodeHexStr(byte[] data, int length) {
    		int uselen = (length <data.length)? length : data.length;
    		byte[] usedata = new byte[uselen];
    		System.arraycopy(data, 0, usedata, 0, uselen);
    		return encodeHexStr(usedata, true);
    	}
    	/**
    	 * 将字节数组转换为十六进制字符串
    	 * 
    	 * @param data
    	 *            byte[]
    	 * @param toLowerCase
    	 *            <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
    	 * @return 十六进制String
    	 */
    	public static String encodeHexStr(byte[] data, boolean toLowerCase) {
    		return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    	}

    	/**
    	 * 将字节数组转换为十六进制字符串
    	 * 
    	 * @param data
    	 *            byte[]
    	 * @param toDigits
    	 *            用于控制输出的char[]
    	 * @return 十六进制String
    	 */
    	protected static String encodeHexStr(byte[] data, char[] toDigits) {
    		return new String(encodeHex(data, toDigits));
    	}

    	/**
    	 * 将十六进制字符数组转换为字节数组
    	 * 
    	 * @param data
    	 *            十六进制char[]
    	 * @return byte[]
    	 * @throws RuntimeException
    	 *             如果源十六进制字符数组是一个奇怪的长度，将抛出运行时异常
    	 */
    	public static byte[] decodeHex(char[] data) {

    		int len = data.length;

    		if ((len & 0x01) != 0) {
    			throw new RuntimeException("Odd number of characters.");
    		}

    		byte[] out = new byte[len >> 1];

    		// two characters form the hex value.
    		for (int i = 0, j = 0; j < len; i++) {
    			int f = toDigit(data[j], j) << 4;
    			j++;
    			f = f | toDigit(data[j], j);
    			j++;
    			out[i] = (byte) (f & 0xFF);
    		}

    		return out;
    	}

    	/**
    	 * 将十六进制字符转换成一个整数
    	 * 
    	 * @param ch
    	 *            十六进制char
    	 * @param index
    	 *            十六进制字符在字符数组中的位置
    	 * @return 一个整数
    	 * @throws RuntimeException
    	 *             当ch不是一个合法的十六进制字符时，抛出运行时异常
    	 */
    	protected static int toDigit(char ch, int index) {
    		int digit = Character.digit(ch, 16);
    		if (digit == -1) {
    			throw new RuntimeException("Illegal hexadecimal character " + ch
    					+ " at index " + index);
    		}
    		return digit;
    	}
	}
}
