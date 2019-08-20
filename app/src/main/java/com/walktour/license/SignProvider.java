package com.walktour.license;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

public class SignProvider {
	private SignProvider() {
	}

	public static boolean verify(byte[] pubKeyText, String plainText, byte[] signText) {
		try {
			// 解密由base64编码的公钥,并构造X509EncodedKeySpec对象
			X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(Base64.decode(pubKeyText));
			// RSA对称加密算法
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			// 取公钥匙对象
			PublicKey pubKey = keyFactory.generatePublic(bobPubKeySpec);
			// 解密由base64编码的数字签名
			byte[] signed = Base64.decode(signText);
			Signature signatureChecker = Signature.getInstance("MD5withRSA");
			signatureChecker.initVerify(pubKey);
			signatureChecker.update(plainText.getBytes());
			// 验证签名是否正常
			if (signatureChecker.verify(signed))
				return true;
			return false;
		} catch (Throwable e) {
			System.out.println("校验签名失败");
			e.printStackTrace();
			return false;
		}
	}
}
