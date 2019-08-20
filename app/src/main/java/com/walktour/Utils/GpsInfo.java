package com.walktour.Utils;

import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.Location;
import android.location.LocationListener;

import com.innsmap.InnsMap.net.http.okutil.utils.L;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.service.GpsService;

import org.andnav.osm.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * @功能 存储GPS相关信息的单例类，保存单前GPS搜索模块是否打开，
 * 由谁打开，关闭GPS时，判断当前的GPS搜索模块是否需要关闭
 * 控制GPS搜索模块的真正开启，关闭
 * @author tangwq
 *
 */
public class GpsInfo {
	private String tag = "GpsInfo";
	private GpsInfo(){}
	private static GpsInfo sInstance;
	public synchronized static GpsInfo getInstance(){
		if(sInstance ==null){
			sInstance =new GpsInfo();
		}
		return sInstance;
	}
	/**
	 * 存储Walktour中Gps的是否打开,并保存Gps打开时监听
	 */
	private boolean jobTestGpsOpen 	= false;	//业务测试界面的GPS选择开启
	private boolean monitorGpsOpen 	= false;	//用户感知时GPS开启
	private boolean indoorGpsOpen	= false;	//室内专项的GPS开启
	private boolean autoTestGpsOpen	= false;	//自动测试时需间隔上传GPS开启
	private boolean umpcGpsOpen 	= false;	//多路测试中是否设置GPS打开
	public LocationListener gpsListener=null;
	public static String gpsLocationChanged="com.walktour.gpsLocationChanged";
	public static String gpsOpenStateChanged = "com.walktour.gpsOpenStateChanged";
	//public static String gpsProviderDisabled = "com.walktour.gpsProviderDisabled";
	public MyLatLng ipackLatLng;
	private Location location=null;
	/** 最后一个生成的GPS点*/
	private GeoPoint lastGeoPoint;
	private List<GpsSatellite> numSatelliteList = new ArrayList<GpsSatellite>();
	private int usedStatellite = 0;
	/**
	 * 记录GPS最后一次更新时间
	 */
	private long gpsLastChangeTime;
	
	/**
	 * @return the gpsLastChangeTime
	 */
	public long getGpsLastChangeTime() {
		return gpsLastChangeTime;
	}
	/**
	 * @param gpsLastChangeTime the gpsLastChangeTime to set
	 */
	public void setGpsLastChangeTime(long gpsLastChangeTime) {
		this.gpsLastChangeTime = gpsLastChangeTime;
	}
	public  boolean isJobTestGpsOpen() {
		return jobTestGpsOpen;
	}
	public  void setJobTestGpsOpen(boolean gpsOpen) {
		this.jobTestGpsOpen = gpsOpen;
	}
	public synchronized boolean isIndoorGpsOpen() {
		return indoorGpsOpen;
	}
	private synchronized void setIndoorGpsOpen(boolean indoorGpsOpen) {
		this.indoorGpsOpen = indoorGpsOpen;
	}
	public synchronized boolean isAutoTestGpsOpen() {
		return autoTestGpsOpen;
	}
	private synchronized void setAutoTestGpsOpen(boolean autoTestGpsOpen) {
		this.autoTestGpsOpen = autoTestGpsOpen;
	}
	public synchronized Location getLocation() {
		return location;
	}

	public MyLatLng getIpackLatLng() {
		return ipackLatLng;
	}

	public void setIpackLatLng(MyLatLng ipackLatLng) {
		this.ipackLatLng = ipackLatLng;
	}

	/**
     * @return the numSatelliteList
     */
    public List<GpsSatellite> getNumSatelliteList() {
        return numSatelliteList;
    }
    /**
     * 设置当前可见卫星列表
     * @param numSatelliteList the numSatelliteList to set
     */
    public void setNumSatelliteList(List<GpsSatellite> numSatelliteList) {
        this.numSatelliteList = numSatelliteList;
    }


    /**
     * [清除已有的卫星状态列表]<BR>
     * [功能详细描述]
     */
    public void cleanNumStatelliteList(){
        numSatelliteList.clear();
    }
    
    /**
     * 返回已连接卫星个数
     * @return the usedStatellite
     */
    public int getUsedStatellite() {
        return usedStatellite;
    }
    /**
     * 设置已连接卫星个数
     * @param usedStatellite the usedStatellite to set
     */
    public void setUsedStatellite(int usedStatellite) {
        this.usedStatellite = usedStatellite;
    }
	
	public synchronized void setLocation(Location location) {
		this.location = location;
	}

	public synchronized boolean isMonitorGpsOpen() {
		return monitorGpsOpen;
	}
	private synchronized void setMonitorGpsOpen(boolean monitorGpsOpen) {
		this.monitorGpsOpen = monitorGpsOpen;
	}
	public synchronized boolean isUmpcGpsOpen() {
		return umpcGpsOpen;
	}
	public synchronized void setUmpcGpsOpen(boolean umpcGpsOpen) {
		this.umpcGpsOpen = umpcGpsOpen;
	}
	
    public synchronized void openGps(Context context,int gpsType){
		if(!(jobTestGpsOpen || autoTestGpsOpen || indoorGpsOpen || monitorGpsOpen || umpcGpsOpen)){
			context.startService(new Intent(context,GpsService.class));
		}
		if(gpsType == WalkCommonPara.OPEN_GPS_TYPE_MONITOR){
			setMonitorGpsOpen(true);
		}else if(gpsType == WalkCommonPara.OPEN_GPS_TYPE_JOBTEST){	
			setJobTestGpsOpen(true);
		}else if(gpsType == WalkCommonPara.OPEN_GPS_TYPE_AUTOTEST){	
			setAutoTestGpsOpen(true);
		}else if(gpsType == WalkCommonPara.OPEN_GPS_TYPE_INDOORTEST){	
			setIndoorGpsOpen(true);
		}else if(gpsType == WalkCommonPara.OPEN_GPS_TYPE_UMPC){
			setUmpcGpsOpen(true);
		}
		LogUtil.w(tag,"--openGps -gpsType:"+gpsType);
	}
	public synchronized void releaseGps(Context context,int gpsType){
		LogUtil.w(tag,"--relesaeGps -gpsType:"+gpsType);
		if(gpsType == WalkCommonPara.OPEN_GPS_TYPE_MONITOR){
			setMonitorGpsOpen(false);
		}else if(gpsType == WalkCommonPara.OPEN_GPS_TYPE_JOBTEST){
			setJobTestGpsOpen(false);
		}else if(gpsType == WalkCommonPara.OPEN_GPS_TYPE_AUTOTEST){
			setAutoTestGpsOpen(false);
		}else if(gpsType == WalkCommonPara.OPEN_GPS_TYPE_INDOORTEST){
			setIndoorGpsOpen(false);
		}else if(gpsType == WalkCommonPara.OPEN_GPS_TYPE_UMPC){
			setUmpcGpsOpen(false);
		}
		if(!(jobTestGpsOpen || autoTestGpsOpen || indoorGpsOpen || monitorGpsOpen || umpcGpsOpen)){
			context.stopService(new Intent(context,GpsService.class));
		}
	}
	public GeoPoint getLastGeoPoint() {
		return lastGeoPoint;
	}
	public void setLastGeoPoint(GeoPoint lastGeoPoint) {
		this.lastGeoPoint = lastGeoPoint;
	}
}
