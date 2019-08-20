package com.walktour.framework.database.model;

import java.io.Serializable;

/**
 * 基站信息详细表
 * 
 * @author jianchao.wang
 * 
 */
public class BaseStationDetail implements Serializable, Cloneable {
	/**	 */
	private static final long serialVersionUID = 1L;
	/** 集合类型：active */
	public final static int SETTYPE_ACTIVESET = 0;
	/** 集合类型：monitor */
	public final static int SETTYPE_MONITORSET = 1;
	/** 集合类型：neighbor */
	public final static int SETTYPE_NEIGHBORSET = 2;
	/** 集合类型：serving */
	public final static int SETTYPE_SERVINGSET = 3;
	/** 数据表ID */
	public long id;
	/** 主表ID */
	public long mainId;
	/** 主表 */
	public BaseStation main;
	/** 距离,只在绘制连线的时候使用 */
	public double distance;
	/** 集合类型, 用于连线使用 */
	public int setType = SETTYPE_ACTIVESET;
	/** lac */
	public String lac;
	/** GSM */
	public String bsic = "";
	/** GSM */
	public String bcch = "";
	/** WCDMA */
	public String psc = "";
	/** TDSCDMA */
	public String cpi = "";
	/** TDSCDMA */
	public String uarfcn = "";
	/** LTE */
	public String pci = "";
	/** LTE */
	public String earfcn = "";
	/** LTE */
	public String enodebIp = "";
	/** CDMA */
	public String pn = "";
	/** CDMA */
	public String evPn = "";
	/** CDMA */
	public String nid = "";
	/** CDMA */
	public String sid = "";
	/** CDMA */
	public String bid = "";
	/** 方向角 */
	public int bearing = 0;
	/** CDMA */
	public String frequency = "";
	/** CDMA */
	public String evFreq = "";
	/** LTE 扇区ID */
	public String sectorId = "";
	/** 小区名称 */
	public String cellName = "";
	/** 小区ID */
	public String cellId = "";
	/** 屏幕X点坐标 */
	public float xScreen = 0;
	/** 屏幕Y点坐标 */
	public float yScreen = 0;
	/** 天线高度 */
	public int antennaHeight = 0;
	/** 是否勾选,编辑时用 */
	public boolean checked = true;

	@Override
	public BaseStationDetail clone() {
		BaseStationDetail o = null;
		try {
			o = (BaseStationDetail) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}

	/**
	 * 获取指定属性名的值
	 * 
	 * @param paramName
	 *          属性名
	 * @return
	 */
	public Object getParamValue(String paramName) {
		if ("lac".equals(paramName))
			return this.lac;
		else if ("cellName".equals(paramName))
			return this.cellName;
		else if ("cellId".equals(paramName))
			return this.cellId;
		else if ("bearing".equals(paramName))
			return this.bearing;
		else if ("psc".equals(paramName))
			return this.psc;
		else if ("uarfcn".equals(paramName))
			return this.uarfcn;
		else if ("bcch".equals(paramName))
			return this.bcch;
		else if ("bsic".equals(paramName))
			return this.bsic;
		else if ("cpi".equals(paramName))
			return this.cpi;
		else if ("pci".equals(paramName))
			return this.pci;
		else if ("eafrcn".equals(paramName))
			return this.earfcn;
		else if ("sid".equals(paramName))
			return this.sid;
		else if ("bid".equals(paramName))
			return this.bid;
		else if ("nid".equals(paramName))
			return this.nid;
		else if ("pn".equals(paramName))
			return this.pn;
		else if ("ev_pn".equals(paramName))
			return this.evPn;
		else if ("enodeb_ip".equals(paramName))
			return this.enodebIp;
		else if ("frequency".equals(paramName))
			return this.frequency;
		else if ("ev_freq".equals(paramName))
			return this.evFreq;
		else if ("sectorId".equals(paramName))
			return this.sectorId;
		else
			return null;
	}
}