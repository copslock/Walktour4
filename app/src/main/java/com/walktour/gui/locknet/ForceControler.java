package com.walktour.gui.locknet;

import android.content.Context;

import com.walktour.base.util.LogUtil;
import com.walktour.control.config.Deviceinfo;

import java.util.ArrayList;

/**
 * 强制控制抽象类，强制界面调用，由于每台手机的强制功能 都不一样， 每台手机单独实现
 *
 * @author qihang.li
 */
public abstract class ForceControler {

	public enum ForceNet {
			NET_AUTO("Auto"),
			NET_GSM("GSM Only","GSM"),
			NET_WCDMA("WCDMA Only","WCDMA"), NET_GSM_WCDMA("GSM/WCDMA Auto"),
			NET_TDSCDMA("TD-SCDMA Only","TD-SCDMA"), NET_GSM_TDSCDMA("GSM/TD-SCDMA Auto"),
			NET_WCDMA_LTE("WCDMA/LTE Auto"), NET_TDSCDMA_LTE("TD-SCDMA/LTE Auto"), NET_LTE("LTE Only","LTE"), NET_FDD_LTE("FDD LTE Only"),
				NET_TDD_LTE("TDD_LTE Only"),
			NET_CDMA("CDMA Only","CDMA"), NET_EVDO("EVDO Only","EVDO"), NET_CDMA_EVDO("CDMA/EVDO Auto"),
			NET_NBIot_WB("WB"), NET_NBIot_CatM1("CatM1"), NET_NBIot_NB1("NB1"), NET_NBIot_CatM1_NB1("CatM1/NB1"),
				NET_NBIot_WB_CatM1("WB/CatM1"), NET_NBIot_WB_NB1("WB/NB1"), NET_NBIot_WB_CatM1_NB1("WB/CatM1/NB1");

		final String descrition;
		final String netStr;

		private ForceNet(String descrition) {
			this.descrition = descrition;
			this.netStr = "";
		}

		private ForceNet(String descrition,String netStr) {
			this.descrition = descrition;
			this.netStr = netStr;
		}

		public String getDescrition() {
			return this.descrition;
		}

		public String getNetStr(){
			return this.netStr;
		}

		/**
		 * 根据描述信息返回类型
		 *
		 * @param descrition
		 * @return 不匹配时返回null
		 */
		public static ForceNet getForceNet(String descrition) {
			ForceNet[] values = ForceNet.values();
			for (ForceNet net : values) {
				if (net.getDescrition().equals(descrition)) {
					return net;
				}
			}
			return null;
		}

		/**
		 * 根据传入的网络串,获得指定的要锁定的网络
		 * LTE,WCDMA TD-SCDMA,GSM,CDMA,EVDO
		 * @param netStr
		 * @return
		 */
		public static ForceNet getForceNetByNet(String netStr) {
			ForceNet[] values = ForceNet.values();
			for (ForceNet net : values) {
				if (net.getNetStr().equals(netStr)) {
					return net;
				}
			}
			return null;
		}
	}

	public enum Band {

		Auto("Auto"), G850("GSM 850"), G900("GSM 900"), G1800("GSM 1800"), G1900("GSM 1900"), G450("GSM 450"), G480("GSM 480"),
		G750("GSM 750"), G9000("GSM-R 9000"), GE900("EGSM 900"),

		W800("WCDMA 800"), W850("WCDMA 850"), W900("WCDMA 900"), W1700("WCDMA 1700"), W1800("WCDMA 1800"), W1900("WCDMA 1900"), W2100(
				"WCDMA 2100"), W2600("WCDAM 2600"),

		// TDSCDMA
		TBandA("Band A"), TBandF("Band F"),

		// FDD LTE
		L1("1 (2110-2170 MHz)"), L2("2 (2110-2170 MHz)"), L3("3 (1805-1880 MHz)"), L4("4 (2110-2155 MHz)"), L5(
				"5 (869-894MHz)"), L6("6 (875-885 MHz)"), L7("7 (2620-2690 MHz)"), L8("8 (925-960 MHz)"), L9(
				"9 (1844.9-1879.9 MHz)"), L10("10 (2110-2170 MHz)"), L11("11 (1475.9-1495.9 MHz)"), L12("12 (729-746 MHz)"), L13(
				"13 (746-756 MHz)"), L14("14 (758-768 MHz)"), L17("17 (734-746 MHz)"), L18("18 (860-875 MHz)"), L19(
				"19 (875-890 MHz)"), L20("20 (791-821 MHz)"), L21("21 (1495.9-1510.9 MHz)"), L22("22 (3510-3590MHz)"), L23(
				"23 (2180-2200MHz)"), L24("24 (1525-1559MHz)"), L25("25 (1930-1995MHz)"), L26("26 (859-894MHz)"), L27(
				"27 (852-869MHz)"), L28("28 (758-803MHz)"),

		// TDD LTE
		L33("33 (1900-1920 MHz)"), L34("34 (2010-2025 MHz)"), L35("35 (1850-1910 MHz)"), L36("36 (1930-1990 MHz)"), L37(
				"37 (1910-1930 MHz)"), L38("38 (2570-2620 MHz)"), L39("39 (1880-1920 MHz)"), L40("40 (2300-2400 MHz)"), L41(
				"41 (2496-2690 MHz)"), L42("42 (3400-3600 MHz)"), L43("43 (3600-3800 MHz)"), L44("44 (703-803 MHz)"),

		NB_Band1("Band 1"), NB_Band2("Band 2"), NB_Band3("Band 3"), NB_Band4("Band 4"), NB_Band5("Band 5"), NB_Band6("Band 6"), NB_Band7("Band 7"), NB_Band8("Band 8"),NB_Band9("Band 9"),
		NB_Band10("Band 10"), NB_Band11("Band 11"), NB_Band12("Band 12"), NB_Band13("Band 13"), NB_Band14("Band 14"), NB_Band17("Band 17"), NB_Band18("Band 18"),
		NB_Band19("Band 19"), NB_Band20("Band 20"), NB_Band21("Band 21"), NB_Band23("Band 23"), NB_Band24("Band 24"), NB_Band25("Band 25"), NB_Band26("Band 26"),
		NB_Band27("Band 27"), NB_Band28("Band 28"), NB_Band29("Band 29"), NB_Band30("Band 30"), NB_Band33("Band 33"), NB_Band34("Band 34"), NB_Band35("Band 35"),
		NB_Band36("Band 36"), NB_Band37("Band 37"), NB_Band38("Band 38"), NB_Band39("Band 39"), NB_Band40("Band 40"), NB_Band41("Band 41"), NB_Band42("Band 42"),
		NB_Band43("Band 43"),

		Band_End("End");
		final String des;

		private Band(String des) {
			this.des = des;
		}

		public static ArrayList<Band> getGsmBands() {
			ArrayList<Band> list = new ArrayList<>();
			Deviceinfo deviceinfo = Deviceinfo.getInstance();
			if (deviceinfo.isSamsungCustomRom()) {
				list.add(Band.Auto);
				list.add(Band.GE900);
				list.add(Band.G900);
				list.add(Band.G1900);
				list.add(Band.G1800);
				list.add(Band.G850);
			} else {
				list.add(Band.Auto);
				for (Band b : Band.values()) {
					if (b.name().startsWith("G")) {
						list.add(b);
					}
				}
			}
			return list;
		}

		public static ArrayList<Band> getWCDMABands() {
			ArrayList<Band> list = new ArrayList<>();
			Deviceinfo deviceinfo = Deviceinfo.getInstance();
			if (deviceinfo.isSamsungCustomRom()) {
				list.add(Band.Auto);

				list.add(Band.W2100);
				list.add(Band.W1900);
				list.add(Band.W1700);
				list.add(Band.W850);
				list.add(Band.W900);
			} else {
				list.add(Band.Auto);

				for (Band b : Band.values()) {
					if (b.name().startsWith("W")) {
						list.add(b);
					}
				}
			}
			return list;
		}

		public static ArrayList<Band> getTDSCDMABands() {
			ArrayList<Band> list = new ArrayList<Band>();
			list.add(Band.Auto);
			for (Band b : Band.values()) {
				if (b.name().startsWith("T")) {
					list.add(b);
				}
			}
			return list;
		}

		public static ArrayList<Band> getLTEBands() {
			ArrayList<Band> list = new ArrayList<>();
			Deviceinfo deviceinfo = Deviceinfo.getInstance();
			if (deviceinfo.isSamsungCustomRom()) {
				list.add(Band.Auto);
				list.add(Band.L1);
				list.add(Band.L2);
				list.add(Band.L3);
				list.add(Band.L5);
				list.add(Band.L7);
				list.add(Band.L8);
				list.add(Band.L12);
				list.add(Band.L13);
				list.add(Band.L17);
				list.add(Band.L20);
				list.add(Band.L25);
				list.add(Band.L26);
				list.add(Band.L28);
				list.add(Band.L38);
				list.add(Band.L39);
				list.add(Band.L40);
				list.add(Band.L41);
			} else {
				list.add(Band.Auto);
				for (Band b : Band.values()) {
					if (b.name().startsWith("L")) {
						list.add(b);
					}
				}
			}
			return list;
		}

		public static ArrayList<Band> getFDD_LTEBands() {
			ArrayList<Band> list = new ArrayList<Band>();
			for (Band b : Band.values()) {
				if (b.name().startsWith("L")) {
					int lteNum = 0;
					try {
						lteNum = Integer.parseInt(b.name().replace("L", ""));
					} catch (Exception e) {

					}
					if (lteNum >= 1 && lteNum <= 28) {
						list.add(b);
					}
				}
			}
			return list;
		}

		public static ArrayList<Band> getTDD_LTEBands() {
			ArrayList<Band> list = new ArrayList<Band>();
			for (Band b : Band.values()) {
				if (b.name().startsWith("L")) {
					int lteNum = 0;
					try {
						lteNum = Integer.parseInt(b.name().replace("L", ""));
					} catch (Exception e) {

					}
					if (lteNum >= 33 && lteNum <= 44) {
						list.add(b);
					}
				}
			}
			return list;
		}

		public static String[] bandArrayToNames(ArrayList<Band> bands) {
			String[] bandNames = new String[bands.size()];
			for (int i = 0; i < bands.size(); i++) {
				bandNames[i] = (bands.get(i).toString());
			}
			return bandNames;
		}

		public static ArrayList<Band> getBandsByNetType(String netType) {
			if (netType.equals(Deviceinfo.NET_TYPES_LTE)) {
				return Band.getLTEBands();
			} else if (netType.equals(Deviceinfo.NET_TYPES_WCDMA)) {
				return Band.getWCDMABands();
			} else if (netType.equals(Deviceinfo.NET_TYPES_TDSCDMA)) {
				return Band.getTDSCDMABands();
			} else if (netType.equals(Deviceinfo.NET_TYPES_GSM)) {
				return Band.getGsmBands();
			} else {
				return null;
			}
		}

		@Override
		public String toString() {
			return this.des;
		}
	}

	protected OnTaskChangeListener iTaskListener;

	public void setOnTaskChangeListener(OnTaskChangeListener listener) {
		this.iTaskListener = listener;
	}

	/**
	 * 初始化
	 *
	 * @return
	 */
	public abstract boolean init();

	/**
	 * 函数功能：锁定网络优先
	 *
	 * @param networkType
	 */
	public abstract boolean lockNetwork(ForceNet networkType);

	/**
	 * 函数功能：锁定网络优先
	 *
	 * @param networkType
	 */
	public abstract boolean lockNetwork(Context context, ForceNet networkType);

	/**
	 * 解除所有锁定
	 *
	 * @param forceNets
	 *          当前的网络制式
	 * @return
	 * @exception
	 * @since 1.0.0
	 */
	public abstract boolean unLockAll(ForceNet forceNets);

	/**
	 * 解除所有锁定
	 *
	 * @param context
	 * 			上下文
	 * @param forceNets
	 *          当前的网络制式
	 * @return
	 * @exception
	 * @since 1.0.0
	 * @note:
	 */
	public boolean unLockAll(Context context, ForceNet forceNets) {
		return false;
	}

	/**
	 * 解除频点锁定
	 *
	 * @param networkType
	 *          网络类型
	 * @return
	 */
	public abstract boolean unlockFrequency(Context context, ForceNet networkType);

	/**
	 * 解除小区锁定
	 *
	 * @param context
	 * 			上下文
	 * @param networkType
	 *          网络类型
	 * @return
	 */
	public abstract boolean unlockCell(Context context, ForceNet networkType);

	/**
	 * 释放资源
	 *
	 * @exception
	 * @since 1.0.0
	 */
	public abstract void release();

	/**
	 * 函数功能：查询已经锁定的频段
	 *
	 * @param
	 * @return
	 */
	public abstract boolean queryBand(ForceNet netType);

	/**
	 * 函数功能：查询频点
	 *
	 * @param netType
	 * @return
	 */
	public abstract boolean queryFrequency(ForceNet netType);

	/**
	 * 函数功能：查询小区
	 *
	 * @param netType
	 * @return
	 */
	public abstract boolean queryCell(ForceNet netType);

	/**
	 * 函数功能：锁频段
	 *
	 * @param netType
	 *          网络类型
	 * @param arg
	 *          预留参数 各网络接需引用
	 * @return
	 */
	public abstract boolean lockBand(ForceNet netType, String arg);

	/**
	 * 函数功能：锁频段
	 *
	 * @param netType
	 *          网络类型
	 * @param band
	 *          频段
	 * @return
	 */
	public abstract boolean lockBand(ForceNet netType, Band[] band);

	/**
	 * 函数功能：锁频段
	 *
	 * @param netType
	 *          网络类型
	 * @param band
	 *          预留参数 各网络接需引用
	 * @return
	 */
	public abstract boolean lockBand(Context context, ForceNet netType, Band[] band);

	/**
	 * 函数功能：锁频点
	 *
	 * @param context
	 *          上下文
	 * @param netType
	 *          网络类型
	 * @param args
	 *          频点参数
	 * @return
	 */
	public abstract boolean lockFrequency(Context context, ForceNet netType, String... args);

	/**
	 * 函数功能：锁小区
	 *
	 * @param context
	 *          上下文
	 * @param netType
	 *          网络类型
	 * @param args
	 *
	 * @return
	 */
	public abstract boolean lockCell(Context context, ForceNet netType, String... args);

	/**
	 * 函数功能：强制驻留小区，但不锁定
	 *
	 * @param netType
	 *          网络类型
	 * @param arg1
	 *          预留参数1 各网络接需引用
	 * @param arg2
	 *          预留参数2 各网络接需引用
	 * @return
	 */
	public abstract boolean campCell(ForceNet netType, String arg1, String arg2);


	/**
	 * 函数功能：飞行模式的开关
	 *
	 * @param context
	 *          上下文
	 * @param flag
	 *          true: 开启飞行模式
	 *          false: 关闭飞行模式
	 * @return
	 */
	public abstract boolean setAirplaneModeSwitch(Context context, boolean flag);

	/**
	 * 函数功能：设置volte功能的开启与关闭
	 *
	 * @param context
	 *          上下文
	 * @param flag
	 *          true: 开启volte功能
	 *          false: 关闭volte功能
	 * @return
	 */
	public abstract boolean setVolteSwitch(Context context, boolean flag);

	/**
	 * 函数功能：设置扰码状态
	 *
	 * @param context
	 *          上下文
	 * @param flag
	 *          true: 开启扰码状态
	 *          false: 关闭扰码状态
	 * @return
	 */
	public abstract boolean setScrambleState(Context context, boolean flag);


	/**
	 * 函数功能：设置接入点名称
	 *
	 * @param context
	 *          上下文
	 * @param arg
	 *          apnc参数
	 * @return
	 */
	public abstract boolean setAPN(Context context, String arg);

	/**
	 * 函数功能：S8定制机的视频电话
	 *
	 * @param context
	 *          上下文
	 * @param number
	 *          电话号码
	 * @return
	 */
	public abstract void makeVideoCall(Context context, String number);

	/**
	 ** 函数功能：NB终端的PSM设置功能
	 *
	 * @param context
	 *          上下文
	 * @param iFlag
	 *          NB的开关处理:
	 *          0: State=close\r\n
	 *          1: State=open\r\n
	 *          2: PSM resume, 该流程需参数Pioneer
	 * @param strArgs
	 *         若开启PSM, 需要传递部分参数,参数顺序如下：
	 *         strArgs[0]: T3412_Unit=***\r\n
	 *         strArgs[1]: T3412_Value=***\r\n
	 *         strArgs[2]: T3324_Unit=***\r\n
	 *         strArgs[3]: T3324_Value=***\r\n
	 * @return 顺利发送则返回OK，否则返回false
	 */
	public boolean setPSMState(Context context, String strArg) {
		return  false;
	}

	/**
	 ** 函数功能：NB终端的eDRX设置功能
	 *
	 * @param context
	 *          上下文
	 * @param iFlag
	 *          NB的开关处理:
	 *          0: State=close\r\n
	 *          1: State=open\r\n
	 * @param strArgs
	 *         若开启eDRX, 需要传递部分参数,参数顺序如下：
	 *		   strArgs[0]: RAT=***\r\n （NOTE: NB1, GSM）
	 *		   strArgs[1]: eDRXCycleLength=***\r\n
	 * @return
	 */
	public boolean setEDRXState(Context context, String strArgs) {
		LogUtil.w("ForceContorler", "setEDRXSetting");
		return false;
	}
}
