package com.dingli.https;

import android.content.Context;

import com.walktour.Utils.DateUtils;
import com.walktour.base.util.DateUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * https权限请求工具类
 */
public class HttpsUtil {

    private static final String TAG = "HttpsUtil";

    private HttpsUtil() {

    }

    /**
     * HttpUrlConnection 方式，支持指定load-der.crt证书验证，此种方式Android官方建议
     *
     * @param context 传入上下文
     * @param path    获取结果地址
     */
    public static String getSSLResult(Context context, String path) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        InputStream input = null;
        BufferedReader reader = null;
        HttpsURLConnection urlConnection = null;
        try {
            LogUtil.d(TAG, "start==" + DateUtils.getCurrentDateTime3());
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream in = context.getResources().openRawResource(R.raw.my_ca);
            Certificate ca = cf.generateCertificate(in);
            in.close();
            in = null;
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(null, null);
            keystore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keystore);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());
            URL url = new URL(path);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(500);
            urlConnection.setReadTimeout(2000);
            urlConnection.setUseCaches(false);
            urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
            final HostnameVerifier verifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            urlConnection.setHostnameVerifier(verifier);
            LogUtil.d(TAG, "start=1=" + DateUtils.getCurrentDateTime3());
            urlConnection.connect();
            LogUtil.d(TAG, "start=2=" + DateUtils.getCurrentDateTime3());
            // if(urlConnection.getResponseCode()== HttpURLConnection.HTTP_OK) {//不要写入
            LogUtil.d(TAG, "doing==" + DateUtils.getCurrentDateTime3());
            input = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            LogUtil.d(TAG, "doing=right=" + DateUtils.getCurrentDateTime3());
            return result.toString();
            // }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            LogUtil.d(TAG, "end==" + DateUtils.getCurrentDateTime3());
            if (null != urlConnection) {
                try {
                    urlConnection.disconnect();
                } catch (Exception e1) {
                    e1.printStackTrace();
                } finally {
                    urlConnection = null;
                }

            }
            if (null != reader) {
                try {
                    reader.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                } finally {
                    reader = null;
                }

            }
            if (null != input) {
                try {
                    input.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                } finally {
                    input = null;
                }
            }
        }
        return null;
    }

    /**
     * 指定文件是否存在
     *
     * @param context 上下文
     * @param path    文件路径
     * @return 是否存在
     */
    public static boolean fileExist(Context context, String path) {
        boolean exists;
        try {
            if (null == getSSLResult(context, path)) {
                exists = false;
            } else {
                exists = true;
            }
        } catch (Exception e) {
            exists = false;
        }

        return exists;
    }
}
