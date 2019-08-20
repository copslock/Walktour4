/**
 * 
 */
package com.walktour.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkStruct.Alarm;
import com.walktour.gui.R;

import java.io.Serializable;

/**
 * 告警模型
 * 
 * @author qihang.li
 */
public class AlarmModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Alarm alarm;
	protected long time = 0;

	private String descrtion = "";
	/**
	 * 地图页面点击后弹出的描述信息 包含告警名、位置、网络参数值
	 */
	private String mapPopInfo = "";

	/**
	 * 要在地图上显示的告警点
	 */
	private MapEvent mapEvent = null;

	/**
	 * 采样点序号
	 */
	private int msgIndex = -1;

	private int drawableId = 0;
	private String drawablePath = "";
	/** 参数序号*/
	private int paramIndex = -1;
	/** */
	private int number=0;
	public AlarmModel() {
	}

	@Override
	public String toString() {
		return "AlarmModel{" +
				"alarm=" + alarm +
				", time=" + time +
				", descrtion='" + descrtion + '\'' +
				", mapPopInfo='" + mapPopInfo + '\'' +
				", mapEvent=" + mapEvent +
				", msgIndex=" + msgIndex +
				", drawableId=" + drawableId +
				", drawablePath='" + drawablePath + '\'' +
				", paramIndex=" + paramIndex +
				", number=" + number +
				'}';
	}

	/**
	 * 包含时间的告警类
	 * 
	 * @param time
	 *          时间(1970到现在的毫秒)
	 * @param type
	 *          告警类型
	 */
	public AlarmModel(long time, Alarm type) {
		this.time = time;
		this.alarm = type;
	}

	public Alarm getAlarm() {
		return alarm;
	}

	/**
	 * 告警的描述（标题）
	 */
	public String getDescription(Context context) {
		if (alarm != null) {
			if (alarm.getType() != Alarm.TYPE_CUSTOM && alarm.getType() != Alarm.TYPE_FILTER_EVENT) {
				this.descrtion=alarm.getDescription(context);
			}
		}
		return this.descrtion;
	}

	/**
	 * 告警的描述（标题）
	 */
	public void setDescrition(String des) {
		this.descrtion = des;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setDrawableId(int drawable) {
		this.drawableId = drawable;
	}

	/**
	 * 自定义事件引起的告警，图标为文件 setDrawableFile 函数功能：
	 * 
	 * @param filePath
	 */
	public void setDrawableFile(String filePath) {
		this.drawablePath = filePath;
	}

	/**
	 * @return 告警事件的图标的Drawable, RCU事件生成的告警取于R.drawable.*, 对于自定义事件，取的是文件
	 */
	@SuppressWarnings("deprecation")
	public Drawable getIconDrawable(Context context) {
		if (drawableId > 0) {
			return context.getResources().getDrawable(drawableId);
		}
		if (alarm != null) {
			if (alarm.getType() == Alarm.TYPE_CUSTOM || alarm.getType() == Alarm.TYPE_FILTER_EVENT) {
				Bitmap b = BitmapFactory.decodeFile(drawablePath);
				BitmapDrawable bmd = new BitmapDrawable(context.getResources(), b);
				bmd.setTargetDensity(context.getResources().getDisplayMetrics());
				return bmd;
			}
			return context.getResources().getDrawable(alarm.getDrawable());
		}
		return context.getResources().getDrawable(R.drawable.empty);
	}

	/**
	 * @return 告警事件的图标的Drawable, RCU事件生成的告警取于R.drawable.*, 对于自定义事件，取的是文件
	 */
	public Bitmap getIconBitmap(Context context) {
		if (drawableId > 0) {
			return BitmapFactory.decodeResource(context.getResources(), drawableId);
		}
		if (alarm != null) {
			if (alarm.getType() == Alarm.TYPE_CUSTOM || alarm.getType() == Alarm.TYPE_FILTER_EVENT) {
				return BitmapFactory.decodeFile(drawablePath);
			}
			return BitmapFactory.decodeResource(context.getResources(), alarm.getDrawable());
		}
		return null;
	}

	/**
	 * 图标的Drawable id,为0时表示没有引用R.drawable.*
	 * 
	 * @return
	 */
	public int getDrawableId() {
		return this.drawableId;
	}

	/**
	 * 设置地图点POP显示描述信息
	 * 
	 * @param netInfo
	 *          网络信息
	 */
	public void setMapPopInfo(String netInfo) {
		if (StringUtil.isNullOrEmpty(netInfo))
			this.mapPopInfo = "";
		else
			this.mapPopInfo = netInfo.replaceAll("-9999", "");
	}

	/**
	 * 设置图标弹出框的内容，带具体网络参数
	 * 
	 * @param netType
	 *          当前网络类型
	 * @param paramValues
	 *          参数值
	 */

	/**
	 * @return 告警事件的描述，在地图或列表中显示
	 */
	public String getMapPopInfo() {
		return this.mapPopInfo;
	}

	public MapEvent getMapEvent() {
		return mapEvent;
	}

	public void setMapEvent(MapEvent mapEvent) {
		this.mapEvent = mapEvent;
	}

	/**
	 * 采样点序号
	 */
	public int getMsgIndex() {
		return msgIndex;
	}

	public void setMsgIndex(int msgIndex) {
		this.msgIndex = msgIndex;
	}

	public int getParamIndex() {
		return paramIndex;
	}

	public void setParamIndex(int paramIndex) {
		this.paramIndex = paramIndex;
	}

	public void setAlarm(Alarm alarm) {
		this.alarm = alarm;
	}

	public String getDescrtion() {
		return descrtion;
	}

	public void setDescrtion(String descrtion) {
		this.descrtion = descrtion;
	}

	public String getDrawablePath() {
		return drawablePath;
	}

	public void setDrawablePath(String drawablePath) {
		this.drawablePath = drawablePath;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}
