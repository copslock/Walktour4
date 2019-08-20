package com.walktour.Utils;

//import java.text.DecimalFormat;

/**
 * GPS经纬度值与手机屏幕坐标值的转换
 * @author tangwq
 *
 */
public class GpsAdaptChange {
	private final String tag = "GpsAdaptChange";
	private final float extendRate 	= 3000;	//扩大系数
	private final float maxLongitude= 180;	//最大经度
	private final float maxLatitude = 90;	//最大纬度
	private float xCenterPoint		= 0;	//x轴中心点
	private float yCenterPoint		= 0;	//Y轴中心点
	private double firstLongitude	= 0;	//第一经度
	private double firstLatitude	= 0;	//第一纬度
	//private float currentLongitude	= 0;	//当前经度
    //private float currentLatitude	= 0;	//当前纬度
    
	private float xAxesRate = 0;	//x轴系数
	private float yAxesRate = 0;	//y轴系数
	
	private static GpsAdaptChange sInstance;
	private GpsAdaptChange(int width,int height){
		xCenterPoint = width / 2;
		yCenterPoint = height/ 2;
		
		xAxesRate = (width / maxLongitude ) * extendRate;
		yAxesRate = (height / maxLatitude ) * extendRate;
	}
	public synchronized static GpsAdaptChange getInstance(int width, int height){
		if(sInstance == null){
			sInstance = new GpsAdaptChange(width,height);
		}
		return sInstance;
	}
	
	/**
	 * 根据传入的经度获得X轴位置信息
	 * @param longitude
	 * @return
	 */
	public float getXByLongitude(double longitude){
		//LogUtil.w(tag,"---longitude:"+longitude);
		float xAxes = 0;
		if(firstLongitude != 0){
			xAxes = (float)(xCenterPoint + (longitude - firstLongitude) * xAxesRate);
		}else{
			firstLongitude = longitude;
			xAxes = xCenterPoint;
		}
		return xAxes;
	}

	/**
	 * 根据传入的纬度获得X轴位置信息
	 * @param latitude
	 * @return
	 */
	public float getYByLatitude(double latitude){
		//LogUtil.w(tag,"--latitude:"+latitude);
		float yAxes = 0;
		if(firstLatitude != 0){
			yAxes = (float)(yCenterPoint - (latitude - firstLatitude) *  yAxesRate);
		}else{
			firstLatitude = latitude;
			yAxes = yCenterPoint;
		}
		return yAxes;
	}
	/**
	 * 根据传入的经纬度与当前的经纬度比较是否有改变，如改变则将传入经纬度替换当前经纬，返回真
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	/*public boolean isChangedLongLat(double longitude,double latitude){
		boolean isChanged = false;
		float fblot = RoundDouble2Float(longitude);
		float fblat = RoundDouble2Float(latitude);
		if(fblot != currentLongitude || fblat != currentLatitude){
			currentLongitude = fblot;
			currentLatitude  = fblat;
			
			isChanged = true;
		}
		return isChanged;
	}
	
	private DecimalFormat floatFormat = new DecimalFormat("#.00000");
	private float RoundDouble2Float(double value){
		return Float.valueOf(floatFormat.format(value));
	}
	private float RoundDouble2Float(double value,int scale,int roundingMode){
		return Float.valueOf(floatFormat.format(value));
		
		BigDecimal bd = new BigDecimal(value);
		bd.setScale(scale,roundingMode);
		float fv = bd.floatValue();
		bd = null;
		return fv;
	}*/
}
