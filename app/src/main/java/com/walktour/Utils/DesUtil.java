package com.walktour.Utils;

import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
/**
 * DES加解密工具类
 * 
 * @author jianchao.wang
 *
 */
public class DesUtil {
	/** HTTP 请求加解密使用的KEY **/
	private static final String PASSWORD_CRYPT_KEY = "WalkTour";
	/**
	 * 解密数据
	 * 
	 * @param data
	 *            要解密的数据
	 * @return
	 */
	public final static byte[] decrypt(byte[] data) {
		try {
			byte[] key = PASSWORD_CRYPT_KEY.getBytes();
			DESKeySpec dks = new DESKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(key);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
			return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	/**
	 * 加密数据
	 * 
	 * @param data
	 *            要加密的数据
	 * @return
	 */
	public final static byte[] encrypt(byte[] data) {
		try {
			byte[] key = PASSWORD_CRYPT_KEY.getBytes();
			DESKeySpec dks = new DESKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(key);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	/***
	 * base 64 加密
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] base64Encrypt(byte[] bytes) {
		return Base64.encode(bytes, Base64.DEFAULT);
	}
	/***
	 * base 64 解密
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] base64Dcrypt(byte[] bytes) {
		return Base64.decode(bytes, Base64.DEFAULT);
	}
}