package com.walktour.control.bean;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.dingli.aliyun.AppendFileUpload;
import com.walktour.Utils.APNOperate;
import com.walktour.Utils.BuildPower;
import com.walktour.Utils.FtpOperate;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.license.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class LocalInfoUpload {
	private final String TAG = "LocalInfoUpload";
	
	private LocationClient mLocClient;
	private Context mContext = null;
	
	public LocalInfoUpload(Context context){
		this.mContext = context;
	}
	
	/*如果保存日期与当前日期不一致且当前网络为连接状态,执行上传信息动作*/
	public void UploadLocalInfo(){
		LogUtil.w(TAG, "--Build Local Info Start--");
		if(!ConfigRoutine.getUEnvInfoTime(mContext).equals(UtilsMethod.ymdFormat.format(System.currentTimeMillis()))
				&& APNOperate.getInstance(mContext).checkNetWorkIsAvailable()){
		
			getBaiduLocationByApi();
			//doUploadInfo(null);
		}
	}
	
	//此方法,不能在子线程中被触发,所以调用的位置在主线程界面,后续操作需另启子线程执行
	private void getBaiduLocationByApi(){
		try{
			LogUtil.w(TAG,"--init Baidu Location Client--");
			mLocClient = new LocationClient(mContext.getApplicationContext());
			mLocClient.registerLocationListener(new MyLocationListenner());
			LocationClientOption option = new LocationClientOption();
			option.setCoorType("bd09ll"); // 设置坐标类型
			option.setScanSpan(5000);
			mLocClient.setLocOption(option);
			mLocClient.start();
			// 发起POI查询请求。请求过程是异步的，定位结果在上面的监听函数onReceivePoi中获取。
			if (mLocClient != null && mLocClient.isStarted())
				mLocClient.requestLocation();
			
		}catch(Exception e){
			LogUtil.w(TAG,"getBaiduLocationByApi",e);
		}
	}
	
	/**
	 * 定位SDK监听函数
	 */
	private class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null )
				return;
			try{
				// 此处设置开发者获取到的方向信息，顺时针0-360
				/*MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(100)
						.latitude(location.getLatitude()).longitude(location.getLongitude()).build();
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);*/
				
				MyLatLng ml = new MyLatLng(location.getLatitude(), location.getLongitude());
				LogUtil.w(TAG, "---MyLocationListenner la:" + ml.latitude + "--long:" + ml.longitude);
				
				// 此处设置开发者获取到的方向信息，顺时针0-360
				//MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(100)
				//		.latitude(location.getLatitude()).longitude(location.getLongitude()).build();
				
				mLocClient.stop();
				mLocClient = null;
				
				new Thread(new UploadLocalInfo(ml)).start();
			}catch(Exception e){
				LogUtil.w(TAG,"MyLocationListenner",e);
			}
		}
	}
	
	class UploadLocalInfo implements Runnable{
		
		private MyLatLng myLatLng = null;
		public UploadLocalInfo(MyLatLng ml){
			myLatLng = ml;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			doUploadInfo(myLatLng);
		}
	}
	
	private void doUploadInfo(MyLatLng latlng){
		String info = getLocalInfo(latlng);
		String encodeStr = Base64.encodeToString(info.getBytes());
		
		//LogUtil.w(TAG, "--Build Local Info Success--" + info);
		//上传成功保存上传信息
		if(uploadInfoToSev(encodeStr)){
			LogUtil.w(TAG, "--Local info display--");
			ConfigRoutine.setUEnvInfoTime(mContext, UtilsMethod.ymdFormat.format(System.currentTimeMillis()));
		}
	}
	
	private String getLocalInfo(MyLatLng latlng){
		JSONObject localInfo = new JSONObject(); 
		try{
			PackageManager packageManager= mContext.getPackageManager();
			PackageInfo packageInfo=packageManager.getPackageInfo(mContext.getPackageName(), 0); 
			
			localInfo.put("config version", 1);
			localInfo.put("upload time", UtilsMethod.sdFormat.format(System.currentTimeMillis()));
			localInfo.put("phone model", android.os.Build.MODEL);
			localInfo.put("install time", UtilsMethod.sdFormat.format(packageInfo.firstInstallTime));
			localInfo.put("walktour version", packageInfo.versionName);
			localInfo.put("rom version", android.os.Build.VERSION.RELEASE);
			localInfo.put("adb imei", MyPhoneState.getInstance().getMyDeviceId(mContext));
			localInfo.put("api imei", MyPhoneState.getInstance().getDeviceId(mContext));
			localInfo.put("wifi addr", MyPhoneState.getInstance().getLocalMacAddress(mContext));
			localInfo.put("blue tooth addr", MyPhoneState.getInstance().getBluetoothAddress());
			localInfo.put("run durations", 330);
			
			JSONArray location = new JSONArray(); 
			location.put(latlng == null ? 0 : latlng.latitude).put(latlng == null ? 0 : latlng.longitude);
			localInfo.put("location", location);
			
		}catch(Exception e){
			Log.w("TAG","getLocalInfo",e);
		}
		
		return localInfo.toString();
	}
	
	/*上传当前内容到服务器中*/
	private boolean uploadInfoToSev(String encodeStr){
		boolean isUploadSuc = true;
		String tempName = UtilsMethod.sdfhmsss.format(System.currentTimeMillis()) + ".json";
		String tempFile = Environment.getExternalStorageDirectory().getPath() + "/walktour/temp/" + tempName;
		UtilsMethod.WriteFile(tempFile, encodeStr + "\r\n");
		
		new AppendFileUpload().fileUpload(mContext, tempFile);
		
		try{
			FtpOperate ftpClient = new FtpOperate(mContext);
			boolean isConnect = ftpClient.connect(UtilsMethod.jem(BuildPower.PI), 21,
					UtilsMethod.jem("PWNGWmNtOXBrNVdR"), UtilsMethod.jem("PUVYYm9kOHFNalJZWm1ESnBaMFU="));
			if(isConnect){
				ftpClient.uploadFile(tempFile,String.format("/LocalInfo/%s/%s/%s", 
						android.os.Build.MODEL,MyPhoneState.getInstance().getDeviceId(mContext),tempName));
			}
			ftpClient.disconnect();
			ftpClient = null;
			
			new File(tempFile).delete();
		}catch(Exception e){
			isUploadSuc = false;
			LogUtil.w(TAG,"uploadInfoToSev",e);
		}
		
		return isUploadSuc;
	}
}
