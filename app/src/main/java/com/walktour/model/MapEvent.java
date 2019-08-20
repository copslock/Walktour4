package com.walktour.model;

import com.walktour.Utils.EventType;
import com.walktour.gui.newmap.model.MyLatLng;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 地图事件对象
 * 
 * @author jianchao.wang
 * 
 */
public class MapEvent implements Serializable{
	/** 该参数点产生时间 */
	private long eventTime = 0;
	/** 参数值 */
	private double value = 0;
	/** 原始经度 */
	private double longitude = 0;
	/** 原始纬度 */
	private double latitude = 0;
	/** 地图校准后的经度 */
	private double adjustLongitude = 0;
	/** 地图校准后的纬度 */
	private double adjustLatitude = 0;
	/** 无MIF时经度转化为屏幕坐标 */
	private float x = 0;
	/** 无MIF时纬度转化为屏幕坐标 */
	private float y = 0;
	/** 有加载MIF时经度参照MIF转化 */
	private float xMIF = 0;
	/** 有加载MIF时纬度参照MIF转化 */
	private float yMIF = 0;
	/** 有加载基站地图时经度参照基站地图转化 */
	private float xBase = 0;
	/** 有加载基站地图时纬度参照基站地图转化 */
	private float yBase = 0;
	/** 参数颜色 */
	private int color = 0xFF000000;
	/** 参数值状态,0表示当前参数,1表示历史参数 */
	private int status = 0;
	/** -1表示默认事件类型，具体事件类型可参照CallType赋值 */
	private int eventType = EventType.GpsPoint;
	/** 参数名称 */
	private String paramName;
	/** 事件点的选中状态，默认0表示无选中，,1表示点击选中 */
	private int selectType = 0;
	/** 参数信息映射 */
	private HashMap<String, LocusParamInfo> paramInfoMap = new HashMap<String, LocusParamInfo>();
	/** 基站参数映射 */
	private HashMap<String, String> stationParamMap = new HashMap<String, String>();
	/** 开始采样点 */
	private int beginPointIndex;
	/** 结束采样点 */
	private int endPointIndex;
	/** 地图上弹出显示的信息 */
	private String mapPopInfo;
	/** 对象ID */
	private long id;

	public MapEvent() {
		this.id = System.currentTimeMillis();
	}

	@Override
	public String toString() {
		return "MapEvent{" +
				"eventTime=" + eventTime +
				", value=" + value +
				", longitude=" + longitude +
				", latitude=" + latitude +
				", adjustLongitude=" + adjustLongitude +
				", adjustLatitude=" + adjustLatitude +
				", x=" + x +
				", y=" + y +
				", xMIF=" + xMIF +
				", yMIF=" + yMIF +
				", xBase=" + xBase +
				", yBase=" + yBase +
				", color=" + color +
				", status=" + status +
				", eventType=" + eventType +
				", paramName='" + paramName + '\'' +
				", selectType=" + selectType +
				", paramInfoMap=" + paramInfoMap +
				", stationParamMap=" + stationParamMap +
				", beginPointIndex=" + beginPointIndex +
				", endPointIndex=" + endPointIndex +
				", mapPopInfo='" + mapPopInfo + '\'' +
				", id=" + id +
				'}';
	}

	public long getEventTime() {
		return eventTime;
	}

	public void setEventTime(long eventTime) {
		this.eventTime = eventTime;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getEventType() {
		return eventType;
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public int getSelectType() {
		return selectType;
	}

	public void setSelectType(int selectType) {
		this.selectType = selectType;
	}

	public HashMap<String, LocusParamInfo> getParamInfoMap() {
		return paramInfoMap;
	}

	public int getBeginPointIndex() {
		return beginPointIndex;
	}

	public void setBeginPointIndex(int beginPointIndex) {
		this.beginPointIndex = beginPointIndex;
	}

	public int getEndPointIndex() {
		return endPointIndex;
	}

	public void setEndPointIndex(int endPointIndex) {
		this.endPointIndex = endPointIndex;
	}

	public float getxMIF() {
		return xMIF;
	}

	public void setxMIF(float xMIF) {
		this.xMIF = xMIF;
	}

	public float getyMIF() {
		return yMIF;
	}

	public void setyMIF(float yMIF) {
		this.yMIF = yMIF;
	}

	public float getxBase() {
		return xBase;
	}

	public void setxBase(float xBase) {
		this.xBase = xBase;
	}

	public float getyBase() {
		return yBase;
	}

	public void setyBase(float yBase) {
		this.yBase = yBase;
	}

	public HashMap<String, String> getStationParamMap() {
		return stationParamMap;
	}

	public String getMapPopInfo() {
		return mapPopInfo;
	}

	public void setMapPopInfo(String mapPopInfo) {
		this.mapPopInfo = mapPopInfo;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof MapEvent))
			return false;
		MapEvent obj = (MapEvent) o;
		if (this.beginPointIndex > 0 && this.endPointIndex > 0 && obj.beginPointIndex == this.beginPointIndex
				&& obj.endPointIndex == this.endPointIndex) {
			return true;
		}
		return false;
	}
	public MyLatLng getLatLng(){
		return new MyLatLng(latitude,longitude);
	}
	public double getAdjustLongitude() {
		return adjustLongitude;
	}

	public void setAdjustLongitude(double adjustLongitude) {
		this.adjustLongitude = adjustLongitude;
	}

	public double getAdjustLatitude() {
		return adjustLatitude;
	}

	public void setAdjustLatitude(double adjustLatitude) {
		this.adjustLatitude = adjustLatitude;
	}

	public long getId() {
		return this.id;
	}

}
