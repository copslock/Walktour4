package com.walktour.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.walktour.Utils.UtilsWalktour;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class OtsHttpUploadService extends Service {
	private final String TAG = "OtsHttpUpload";
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		LogUtil.w(TAG,"--onCreate--");
		stopHttpUpload = false;
		
		new TodoOtsHttpUpload().start();
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LogUtil.w(TAG,"--onDestroy--");
		stopHttpUpload = true;
	}

	private boolean stopHttpUpload = false;
	private String paramsUrl = "";
	private String deviceUrl = "";
	private int intervalDelay= 5000;
	
	/**
	 * 通过共享结构获得rul相关信息
	 */
	private void getSharedUrlInfo(){
		paramsUrl = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
				.getString(WalktourConst.SYS_SETTING_OTS_URL_PARAMETER, "");
		
		deviceUrl = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
				.getString(WalktourConst.SYS_SETTING_OTS_URL_DEVICE, "");
		
		intervalDelay = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
				.getInt(WalktourConst.SYS_SETTING_OTS_REPORT_INTERVAL, 10000);
		
		if(!"".equals(paramsUrl) && paramsUrl.equalsIgnoreCase("http://")){
			paramsUrl = "http:" + paramsUrl;
		}
		if("".equals(deviceUrl) && deviceUrl.equalsIgnoreCase("http://")){
			deviceUrl = "http:" + deviceUrl;
		}
		
		
		//LogUtil.w(TAG,"--paramsUrl:" + paramsUrl);
		//LogUtil.w(TAG,"--deviceUrl:" + deviceUrl);
		//LogUtil.w(TAG,"--intervalDelay:" + intervalDelay);
	}
	
	class TodoOtsHttpUpload extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			LogUtil.w(TAG,"--TodoOtsHttpUpload Start--" + intervalDelay);
			while(!stopHttpUpload){
				try{
					getSharedUrlInfo();
					Thread.sleep(intervalDelay);
					
					postHttpContext(paramsUrl,UtilsWalktour.getParamsInfo(getApplicationContext()));
					postHttpContext(deviceUrl,UtilsWalktour.getDeviceInfo(getApplicationContext()));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			LogUtil.w(TAG,"--TodoOtsHttpUpload Stop--");
		}
	}
	
	private void postHttpContext(String url,String body){
		try {
			LogUtil.w(TAG, "--send:" + body);
			//内容或url地址为空时直接返回
			if("".equals(url) || "".equals(body)){
				return;
			}
			
			URL request = new URL(url);
			HttpURLConnection connection;
			connection = (HttpURLConnection)request.openConnection();
		
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(10000);
			connection.setRequestMethod("POST");
			connection.addRequestProperty("Content-type", "application/xml");
			connection.addRequestProperty("Accept", "application/xml");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.connect();
	
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "utf8");
			out.write(body);
			out.flush();
			out.close();
	
			InputStream in = connection.getInputStream();
			String sTotalString = "";
			
			byte[] datas = new byte[in.available()];
			in.read(datas);
			sTotalString = new String(datas);
			
			//int code = Integer.parseInt(findResponseCode(sTotalString));

			LogUtil.w(TAG,"--httpPost result:" + sTotalString);
		
			in.close();
			connection.disconnect();
			request = null;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
