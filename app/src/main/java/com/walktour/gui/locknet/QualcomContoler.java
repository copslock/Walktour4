package com.walktour.gui.locknet;

import android.annotation.SuppressLint;
import android.content.Context;

import com.dinglicom.dataset.DatasetManager;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.map.LTE;

import java.util.ArrayList;
import java.util.Locale;

import static com.walktour.gui.locknet.ForceControler.Band.Auto;

/**
 *
 * QualcomContoler 高通芯片强制 2014-6-25 下午5:11:43
 *
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
@SuppressLint("SdCardPath")
public class QualcomContoler extends ForceControler {

	public static final String TIP = "tip";
	public static final String DEV = "/dev/socket/qmux_radio/qmux_connect_socket";


	private static final int NETWORK_CDMA = 1; // 00000001
	private static final int NETWORK_EVDO = 2; // 00000010
	private static final int NETWORK_GSM = 4; // 00000100
	private static final int NETWORK_WCDMA = 8; // 00001000
	private static final int NETWORK_LTE = 16; // 00010000
	private static final int NETWORK_TDSCDMA = 32; // 00100000
	private static final int NETWORK_AUTO = 63; // 00111111

	private static final int NETWORK_CDMA_EVDO = 3; // 00000011
//	public static final int NETWORK_CDMA_EVDO_LTE = 19; // 00010011
//
//	public static final int NETWORK_GSM_WCDMA = 12; // 00001100
//	public static final int NETWORK_WCDMA_LTE = 24; // 00011000
//
//	public static final int NETWORK_GSM_TDSCDMA = 36; // 00100100
//	public static final int NETWORK_TDSCDMA_LTE = 48; // 00110000

	@Override
	public boolean init() {
		return true;
	}

	@Override
	public boolean lockNetwork(ForceNet networkType) {
		int type = NETWORK_AUTO;
		switch (networkType) {
			case NET_AUTO:
				type = NETWORK_AUTO;
				break;
			case NET_GSM:
				type = NETWORK_GSM;
				break;
			case NET_WCDMA:
				type = NETWORK_WCDMA;
				break;
			case NET_TDSCDMA:
				type = NETWORK_TDSCDMA;
				break;
			case NET_CDMA:
				type = NETWORK_CDMA;
				break;
			case NET_EVDO:
				type = NETWORK_EVDO;
				break;
			case NET_CDMA_EVDO:
				type = NETWORK_CDMA_EVDO;
				break;
			case NET_FDD_LTE:
			case NET_TDD_LTE:
			case NET_LTE:
				type = NETWORK_LTE;
				break;
			case NET_GSM_WCDMA:
				type = NETWORK_GSM + NETWORK_WCDMA;
				break;
			case NET_GSM_TDSCDMA:
				type = NETWORK_GSM + NETWORK_TDSCDMA;
				break;
			case NET_TDSCDMA_LTE:
				type = NETWORK_TDSCDMA + NETWORK_LTE;
				break;
			case NET_WCDMA_LTE:
				type = NETWORK_WCDMA + NETWORK_LTE;
				break;
			default:
				break;
		}

		// 不要输出命令的log
		String command = String.format(Locale.getDefault(), "%s setnetwork %d", AppFilePathUtil.getInstance().getAppFilesFile(TIP), type);
		UtilsMethod.runRootCommandNoLog(command);

		// 用库来调用tip文件，su管理器会把操作命令完全打印在界面
		// String netType = String.valueOf(type);
		// ForceUtil.setnetwork("","",netType);
		return true;
	}

	@Override
	public boolean unLockAll(ForceNet forceNets) {

		// 解除频段锁定
		switch (forceNets) {
			case NET_GSM:
			case NET_TDSCDMA:
			case NET_WCDMA:
			case NET_LTE:
			case NET_FDD_LTE:
			case NET_TDD_LTE:
				lockBand(ForceNet.NET_LTE, new Band[] { Auto });
				break;
			default:
				break;
		}

		// 解除网络
		lockNetwork(ForceNet.NET_AUTO);
		return true;
	}

	@Override
	public boolean unLockAll(Context context, ForceNet forceNets) {
		return true;
	}
	@Override
	public void release() {

	}

	@Override
	public boolean queryBand(ForceNet netType) {
		return false;
	}

	@Override
	public boolean queryFrequency(ForceNet netType) {
		return false;
	}

	@Override
	public boolean queryCell(ForceNet netType) {
		return false;
	}

	@Override
	public boolean lockBand(ForceNet netType, String arg) {
		return false;
	}

	@Override
	public boolean lockBand(ForceNet netType, Band[] bands) {
		String command = "";
		String bandStr = "";

		bandStr = getBandStrs(bands);

		// 有自动模式时，选择所有频段即可
		if (bandStr.toLowerCase(Locale.getDefault()).contains("auto")) {

			ArrayList<Band> bandList = new ArrayList<Band>();
			if (netType == ForceNet.NET_GSM) {
				bandList.addAll(Band.getGsmBands());
			} else if (netType == ForceNet.NET_WCDMA) {
				bandList.addAll(Band.getWCDMABands());
			} else if (netType == ForceNet.NET_TDSCDMA) {
				bandList.addAll(Band.getTDSCDMABands());
			} else if (netType == ForceNet.NET_LTE) {
				bandList.addAll(Band.getLTEBands());
			} else if (netType == ForceNet.NET_FDD_LTE) {
				bandList.addAll(Band.getFDD_LTEBands());
			} else if (netType == ForceNet.NET_TDD_LTE) {
				bandList.addAll(Band.getTDD_LTEBands());
			}
			bandList.remove(Auto);

			Band[] bandArray = new Band[bandList.size()];
			bandList.toArray(bandArray);
			bandStr = getBandStrs(bandArray);
		}

		// 锁频前先执行锁网
		lockNetwork(netType);

		switch (netType) {
			case NET_GSM:
			case NET_WCDMA:
				command = String.format("%s setwgband %s", AppFilePathUtil.getInstance().getAppFilesFile(TIP), bandStr);
				break;

			case NET_LTE:
			case NET_TDD_LTE:
			case NET_FDD_LTE:
				command = String.format("%s setlteband %s", AppFilePathUtil.getInstance().getAppFilesFile(TIP), bandStr);
				break;
			default:
				break;
		}
		// 不要输出命令的log
		UtilsMethod.runRootCommandNoLog(command);
		return false;
	}

	@Override
	public boolean lockFrequency(Context context, ForceNet netType, String... args) {
		return lockFrequency_Normal(context, netType, args);
	}

	@Override
	public boolean lockCell(Context context, ForceNet netType, String... args) {
		return lockCellNormal(context, netType, args);
	}

	@Override
	public boolean campCell(ForceNet netType, String arg1, String arg2) {
		return false;
	}

	@Override
	public boolean setAirplaneModeSwitch(Context context, boolean flag) {
		return false;
	}

	@Override
	public boolean setVolteSwitch(Context context, boolean flag) {
		return false;
	}

	private String getBandStrs(Band[] bands) {
		String bandStr = "";
		for (int i = 0; i < bands.length; i++) {
			Band band = bands[i];
			bandStr += (i > 0 ? "|" : "") + band.name();
		}
		return bandStr;
	}

	/**
	 * 获得锁频点的打开目录的命令码流
	 *
	 * @param netType
	 *          网络类型
	 * @return
	 */
	private byte[] getLockFreqOpenBytes(ForceNet netType) {
		if (netType == ForceNet.NET_WCDMA) {
			//nv/item_files/wcdma/rrc
			String hexStr = "4B 13 09 00 FF 1F 6E 76 2F 69 74 65 6D 5F 66 69 6C 65 73 2F 77 63 64 6D 61 2F 72 72 63 00";
			return this.hexStringToBytes(hexStr);
		} else if (netType == ForceNet.NET_LTE) {
			//nv/item_files/modem/lte/ML1
			String hexStr = "4B 13 09 00 FF 1F 6E 76 2F 69 74 65 6D 5F 66 69 6C 65 73 2F 6D 6F 64 65 6D 2F 6C 74 65 2F 4D 4C 31 00";
			return this.hexStringToBytes(hexStr);
		}
		return null;
	}

	/**
	 * 获得锁频点的命令码流
	 *
	 * @param netType
	 *          网络类型
	 * @param args
	 *          请求参数
	 * @return
	 */
	private byte[] getLockFreqBytes(ForceNet netType, String... args) {
		if (netType == ForceNet.NET_WCDMA) {
			String frontStr = "4B 13 26 00 02 00 00 00 41 02 00 00 01 00";
			byte[] frontBytes = this.hexStringToBytes(frontStr);
			byte[] freq = UtilsMethod.shortToBytes((short) Integer.parseInt(args[1]));
			String backStr = "6E 76 2F 69 74 65 6D 5F 66 69 6C 65 73 2F 77 63 64 6D 61 2F 72 72 63 2F 77 63 64 6D 61 5F 72 72 63 5F 66 72 65 71 5F 6C 6F 63 6B 5F 69 74 65 6D 00";
			byte[] backBytes = this.hexStringToBytes(backStr);
			return this.mergeBytesArray(frontBytes, freq, backBytes);
		} else if (netType == ForceNet.NET_LTE) {
			String strBand = args[0];
			String strFreq = args[1];
			String frontStr = "4B 13 26 00 04 00 00 00 41 02 00 00 26 13";
			byte[] frontBytes = this.hexStringToBytes(frontStr);
			byte[] freq = UtilsMethod.shortToBytes((short) Integer.parseInt(strFreq));
			//if (strBand.equals("Auto")) {
			{
				String backStr = "/nv/item_files/modem/lte/rrc/csp/earfcn_lock\0";
				byte[] backBytes = backStr.getBytes();
				return this.mergeBytesArray(frontBytes, freq, freq, backBytes);
			} /*else{
				byte[] band = UtilsMethod.shortToBytes((short) Integer.parseInt(strBand));
				String backStr = "6E 76 2F 69 74 65 6D 5F 66 69 6C 65 73 2F 6D 6F 64 65 6D 2F 6C 74 65 2F 4D 4C 31 2F 63 61 6D 70 5F 62 61 6E 64 5F 65 61 72 66 63 6E 00";
				byte[] backBytes = this.hexStringToBytes(backStr);
				return this.mergeBytesArray(frontBytes, band, freq, backBytes);
			}*/
		}
		return null;
	}

	/**
	 * 获得解锁频点的命令码流
	 *
	 * @param netType
	 *          网络类型
	 * @return
	 */
	private byte[] getUnlockFreqBytes(ForceNet netType) {
		if (netType == ForceNet.NET_WCDMA) {
			String hexStr = "4B 13 08 00 6E 76 2F 69 74 65 6D 5F 66 69 6C 65 73 2F 77 63 64 6D 61 2F 72 72 63 2F 77 63 64 6D 61 5F 72 72 63 5F 66 72 65 71 5F 6C 6F 63 6B 5F 69 74 65 6D 00";
			return this.hexStringToBytes(hexStr);
		} else if (netType == ForceNet.NET_LTE) {
			//String backStr = "/nv/item_files/modem/lte/rrc/csp/earfcn_lock\0";
			//byte[] backBytes = backStr.getBytes();
			String hexStr = "4B 13 08 00 2f 6e 76 2f 69 74 65 6d 5f 66 69 6c 65 73 2f 6d 6f 64 65 6d 2f 6c 74 65 2f 72 72 63 2f 63 73 70 2f 65 61 72 66 63 6e 5f 6c 6f 63 6b 00";
			return this.hexStringToBytes(hexStr);
		}
		return null;
	}

	/**
	 * 获得锁小区的打开目录的命令码流
	 *
	 * @param netType
	 *          网络类型
	 * @return
	 */
	private byte[] getLockCellOpenBytes(ForceNet netType) {
		String hexStr = "";
		if (netType == ForceNet.NET_WCDMA) {// WCDMA仅锁定当前小区
			hexStr = "4B 13 09 00 FF 01 6E 76 2F 69 74 65 6D 5F 66 69 6C 65 73 2F 77 63 64 6D 61 2F 72 72 63 00";
		} else {// LTE 网络
			hexStr = "4B 13 09 00 FF 01 6E 76 2F 69 74 65 6D 5F 66 69 6C 65 73 2F 6D 6F 64 65 6D 2F 6C 74 65 2F 72 72 63 2F 63 73 70 00";
		}
		return this.hexStringToBytes(hexStr);
	}

	/**
	 * 获得解锁小区的命令码流
	 *
	 * @param netType
	 *          网络类型
	 * @return
	 */
	private byte[] getUnlockCellBytes(ForceNet netType) {
		String hexStr = "";
		if (netType == ForceNet.NET_WCDMA) {// WCDMA仅锁定当前小区
			hexStr = "4B 13 26 00 01 00 00 00 41 02 00 00 01 00 00 6E 76 2F 69 74 65 6D 5F 66 69 6C 65 73 2F 77 63 64 6D 61 2F 72 72 63 2F 77 63 64 6D 61 5F 72 72 63 5F 65 6E 61 62 6C 65 5F 70 73 63 5F 6C 6F 63 6B 00";
		} else {// LTE 网络
			hexStr = "4B 13 08 00 6E 76 2F 69 74 65 6D 5F 66 69 6C 65 73 2F 6D 6F 64 65 6D 2F 6C 74 65 2F 72 72 63 2F 63 73 70 2F 70 63 69 5F 6C 6F 63 6B 00";
		}
		return this.hexStringToBytes(hexStr);
	}

	/**
	 * 获得锁小区的命令码流
	 *
	 * @param netType
	 *          网络类型
	 * @param args
	 *          小区参数
	 * @return
	 */
	private byte[] getLockCellBytes(ForceNet netType, String... args) {
		if (netType == ForceNet.NET_WCDMA) {// WCDMA仅锁定当前小区
			String command = "4B 13 26 00 01 00 00 00 41 02 00 00 01 00 01 6E 76 2F 69 74 65 6D 5F 66 69 6C 65 73 2F 77 63 64 6D 61 2F 72 72 63 2F 77 63 64 6D 61 5F 72 72 63 5F 65 6E 61 62 6C 65 5F 70 73 63 5F 6C 6F 63 6B 00";
			return this.hexStringToBytes(command);
		}// LTE 网络
		String frontStr = "4B 13 26 00 04 00 00 00 41 02 00 00 02 00";
		byte[] frontBytes = this.hexStringToBytes(frontStr);
		byte[] earfcn = UtilsMethod.shortToBytes((short) Integer.parseInt(args[1]));
		byte[] pci = UtilsMethod.shortToBytes((short) Integer.parseInt(args[2]));
		String backStr = "6E 76 2F 69 74 65 6D 5F 66 69 6C 65 73 2F 6D 6F 64 65 6D 2F 6C 74 65 2F 72 72 63 2F 63 73 70 2F 70 63 69 5F 6C 6F 63 6B 00";
		byte[] backBytes = this.hexStringToBytes(backStr);
		return this.mergeBytesArray(frontBytes, earfcn, pci, backBytes);
	}

	/**
	 * 合并多个byte数组
	 *
	 * @param arrays
	 *          多个byte数组
	 * @return
	 */
	private byte[] mergeBytesArray(byte[]... arrays) {
		int size = 0;
		for (byte[] bytes : arrays) {
			size += bytes.length;
		}
		byte[] result = new byte[size];
		int pos = 0;
		for (byte[] bytes : arrays) {
			System.arraycopy(bytes, 0, result, pos, bytes.length);
			pos += bytes.length;
		}
		return result;
	}

	/**
	 * 转换16进制字符窜为byte数组
	 *
	 * @param hexStr
	 *          16进制字符串
	 * @return byte[]
	 */
	private byte[] hexStringToBytes(String hexStr) {
		if (StringUtil.isNullOrEmpty(hexStr)) {
			return null;
		}
		hexStr = hexStr.replaceAll(" ", "").toUpperCase(Locale.getDefault());
		byte[] bytes = new byte[hexStr.length() / 2];
		for (int i = 0, j = 0; i < hexStr.length(); i = i + 2, j++) {
			bytes[j] = (byte) (charToByte(hexStr.charAt(i)) << 4 | charToByte(hexStr.charAt(i + 1)));
		}
		return bytes;
	}

	/**
	 * 字符转换成byte
	 *
	 * @param c
	 *          字符串
	 * @return
	 */
	private byte charToByte(char c) {
		return Byte.decode("0x" + c).byteValue();
	}

	@Override
	public boolean unlockFrequency(Context context, ForceNet networkType) {
		return unlockFrequency_Normal(context, networkType);
	}

	@Override
	public boolean unlockCell(Context context, ForceNet networkType) {
		return unlockCell_Normal(context, networkType);
	}

	private byte[] lockWriteBandTrace(short iNvCode, long iNvValue) {
			byte[] nvBytes = new byte[133];

			int iPos = 0;
			nvBytes[iPos++] = 0x27;
			nvBytes[iPos++] = (byte) (iNvCode & 0xFF);
			nvBytes[iPos++] = (byte) ((iNvCode >> 8) & 0xFF);
			if ((441 == iNvCode) || (946 == iNvCode) || (2954 == iNvCode))
				nvBytes[iPos++] = 0x00;
			nvBytes[iPos++] = (byte) (iNvValue & 0xFF);
			nvBytes[iPos++] = (byte) ((iNvValue >> 8)& 0xFF);
			nvBytes[iPos++] = (byte) ((iNvValue >> 16)& 0xFF);
			nvBytes[iPos++] = (byte) ((iNvValue >> 24)& 0xFF);
			nvBytes[iPos++] = (byte) ((iNvValue >> 32)& 0xFF);
			nvBytes[iPos++] = (byte) ((iNvValue >> 40)& 0xFF);
			nvBytes[iPos++] = (byte) ((iNvValue >> 48)& 0xFF);
			nvBytes[iPos++] = (byte) ((iNvValue >> 56)& 0xFF);

			return nvBytes;
	}

	@Override
	public boolean lockBand(Context context, ForceNet netType, Band[] band) {
		Deviceinfo deviceinfo = Deviceinfo.getInstance();

		String strDeviceName = deviceinfo.getDevicemodel();
        if(     strDeviceName.equals("SM-A6060") ||
                strDeviceName.equals("SM-G9500") ||
                strDeviceName.equals("SM-G9550") ||
                strDeviceName.equals("SM-G9600") ||
                strDeviceName.equals("SM-G9650")) {
			return lockBand_NoRestart(context, netType, band, 2000);
		}

		return lockBand_Normal(context, netType, band, 2000);
	}


	/*
	 * 高通芯片重启Modem指令;
	 * 部分终端可能会直接重启
	 */
	private boolean rebootModem(Context context, int iTimeOut){
        DatasetManager datasetManager = DatasetManager.getInstance(context);
        if (null == datasetManager)
            return  false;

        byte[] rebootByts = {0x4b, 0x25, 0x03, 0x00}; //0x92 0x3a 0x7e

        boolean bFlag = datasetManager.devWritePort(rebootByts);

        try {
            Thread.sleep(iTimeOut);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return  bFlag;
    }

    private boolean syncLockcmd(Context context, String strPath, int iTimeOut){
	    DatasetManager datasetManager = DatasetManager.getInstance(context);
	    if (null == datasetManager)
	        return false;

	    String strFront = "4b 13 30 00 1F 01";
	    String strBack  = strPath;

	    byte[] frontBytes = this.hexStringToBytes(strFront);
	    byte[] backBytes  = strBack.getBytes();

	    byte[] entryBytes = this.mergeBytesArray(frontBytes, backBytes);

	    boolean bFlag = datasetManager.devWritePort(entryBytes);

        try {
            Thread.sleep(iTimeOut);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return bFlag;

    }

	@Override
	public boolean lockNetwork(Context context, ForceNet networkType) {
		Deviceinfo deviceinfo = Deviceinfo.getInstance();

		String deviceName = deviceinfo.getDevicemodel();
		if(     deviceName.equals("SM-A6060") ||
		        deviceName.equals("SM-G9500") ||
				deviceName.equals("SM-G9550") ||
				deviceName.equals("SM-G9600") ||
				deviceName.equals("SM-G9650")) {
			return lockNetwork_NoRestart(context, networkType, 1000);
		}
		
		return lockNetwork_NoRestart(context, networkType, 1000);
		//return lockNetwork_Normal(context, networkType);
	}

	//@Override
	public boolean setScrambleState(Context context, boolean flag){
		return  false;
	}

	@Override
	public boolean setAPN(Context context, String arg) {
		return false;
	}

	@Override
	public void makeVideoCall(Context context, String number) { return; }

	private boolean lockBand_NoRestart(Context context, ForceNet netType, Band[] band, int iTimeOut){
		DatasetManager datasetManager = DatasetManager.getInstance(context);
		if (null == datasetManager)
			return false;

		boolean bFlag = false;
        String strHex = "";

        if (0 == strHex.length()) {
			switch (netType) {
				case NET_LTE: {
					for (Band b : band) {
						if (b == Auto) {
							byte[] entryBytes = {0x4b, 0x13, 0x26, 0x00, 0x08, 0x00, 0x00, 0x00, 0x41, 0x02, 0x00,
									0x00, 0x1f, 0x66, (byte)0xff, 0x3f, (byte) 0xdf,(byte)0xff, (byte)0xff, (byte)0xff, 0x00, 0x00, 0x2f, 0x6e, 0x76,
									0x2f, 0x69, 0x74, 0x65, 0x6d, 0x5f, 0x66, 0x69, 0x6c, 0x65, 0x73, 0x2f, 0x6d, 0x6f,
									0x64, 0x65, 0x6d, 0x2f, 0x6d, 0x6d, 0x6f, 0x64, 0x65, 0x2f, 0x6c, 0x74, 0x65, 0x5f,
									0x62, 0x61, 0x6e, 0x64, 0x70, 0x72, 0x65, 0x66, 0x00};
							bFlag = datasetManager.devWritePort(entryBytes);

							try {
								Thread.sleep(iTimeOut);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							byte[] sysBytes = {0x4b, 0x13, 0x30, 0x00, 0x28, 0x50, 0x2f, 0x6e, 0x76, 0x2f, 0x69,
									0x74, 0x65, 0x6d, 0x5f, 0x66, 0x69, 0x6c, 0x65, 0x73, 0x2f, 0x6d, 0x6f, 0x64, 0x65, 0x6d,
									0x2f, 0x6d, 0x6d, 0x6f, 0x64, 0x65, 0x2f, 0x6c, 0x74, 0x65, 0x5f, 0x62, 0x61, 0x6e, 0x64, 0x70, 0x72, 0x65, 0x66, 0x00};
							bFlag = datasetManager.devWritePort(sysBytes);

							try {
								Thread.sleep(iTimeOut);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							byte[] ReModemBytes = {0x4b, 0x25, 0x03, 0x00};
							return datasetManager.devWritePort(ReModemBytes);
						}

						int iBandValue_1_32 = 0, iBandValue_33_64 = 0;
						if (b.name().startsWith("L")) {
							String strBand = b.name();
							strBand = strBand.substring(strBand.indexOf("L") + 1);
							int iBv = Integer.parseInt(strBand);
							if ((iBv >= 1) && (iBv <= 32))
								iBandValue_1_32 = iBandValue_1_32 + (1 << (iBv - 1));
							else  if ((iBv >= 33) && (iBv <= 64))
								iBandValue_33_64 = iBandValue_33_64 + (1 << (iBv - 33));
						}

						if (iBandValue_1_32 > 0){
							String strBand_1_32_Front = "4b 0f 28 00 00 00 00 00 10 00 00 00 03 00 00 00 00 00 00 40";
							String strBand_1_32_Back = "00 01 00 00 02 00 00 00 04 00 00 00";


							byte[] frontBytes 	= this.hexStringToBytes(strBand_1_32_Front);
							byte[] midBytes 	= UtilsMethod.intToByteArray1(iBandValue_1_32);
							byte[] backBytes 	= this.hexStringToBytes(strBand_1_32_Back);

							byte[] entryBytes		= this.mergeBytesArray(frontBytes, midBytes, backBytes);
							bFlag = datasetManager.devWritePort(entryBytes);

							try {
								Thread.sleep(iTimeOut);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}

						if (iBandValue_33_64 > 0) {
							//此处参数长度为64bits, 但是前面命令可支持1~32 且不用重启Modem，故前面32个band使用前面的命令实现;
							String strBand_33_64_Front = "4b 13 26 00 08 00 00 00 41 02 00 00 d2 ed 00 00 00 00";
							String strBand_33_64_Back = "2f 6e 76 2f 69 74 65 6d 5f 66 69 6c 65 73 2f 6d 6f 64 65 6d 2f 6d 6d 6f 64 65 2f 6c 74 65 5f 62 61 6e 64 70 72 65 66 00";

							byte[] frontBytes 	= this.hexStringToBytes(strBand_33_64_Front);
							byte[] midBytes 	= UtilsMethod.intToByteArray1(iBandValue_33_64);
							byte[] backBytes 	= this.hexStringToBytes(strBand_33_64_Back);

							byte[] entryBytes		= this.mergeBytesArray(frontBytes, midBytes, backBytes);
							bFlag = datasetManager.devWritePort(entryBytes);

							try {
								Thread.sleep(iTimeOut);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							byte[] sysBytes = {0x4b, 0x13, 0x30, 0x00, 0x28, 0x50, 0x2f, 0x6e, 0x76, 0x2f, 0x69,
									0x74, 0x65, 0x6d, 0x5f, 0x66, 0x69, 0x6c, 0x65, 0x73, 0x2f, 0x6d, 0x6f, 0x64, 0x65, 0x6d,
									0x2f, 0x6d, 0x6d, 0x6f, 0x64, 0x65, 0x2f, 0x6c, 0x74, 0x65, 0x5f, 0x62, 0x61, 0x6e, 0x64, 0x70, 0x72, 0x65, 0x66, 0x00};
							bFlag = datasetManager.devWritePort(sysBytes);

							try {
								Thread.sleep(iTimeOut);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							byte[] ReModemBytes = {0x4b, 0x25, 0x03, 0x00};
							bFlag = datasetManager.devWritePort(ReModemBytes);
						}
					}
					break;
				}
				case NET_GSM: {
					for (Band b : band) {
						switch (b) {
							case G900:
								strHex = "4b 0f 10 00 00 00 00 00 10 00 00 00 03 00 00 00 03 02 c0 04 00 00 02 00 00 01 00 00 02 00 00 00 04 00 00 00";
								break;
							case G1800:
								strHex = "4b 0f 10 00 00 00 00 00 10 00 00 00 03 00 00 00 83 00 c0 04 00 00 02 00 00 01 00 00 02 00 00 00 04 00 00 00";
								break;
							case GE900:
								strHex = "4b 0f 10 00 00 00 00 00 10 00 00 00 03 00 00 00 03 01 c0 04 00 00 02 00 00 01 00 00 02 00 00 00 04 00 00 00";
								break;
							case G850:
								strHex = "4b 0f 10 00 00 00 00 00 10 00 00 00 03 00 00 00 03 00 c8 04 00 00 02 00 00 01 00 00 02 00 00 00 04 00 00 00";
								break;
							case G1900:
								strHex = "4b 0f 10 00 00 00 00 00 10 00 00 00 03 00 00 00 03 00 e0 04 00 00 02 00 00 01 00 00 02 00 00 00 04 00 00 00";
								break;
						}
					}
					break;
				}
				case NET_WCDMA: {
					for (Band b : band) {
						switch (b) {
							case W2100:
								strHex = "4b 0f 10 00 00 00 00 00 10 00 00 00 03 00 00 00 83 03 68 00 00 00 00 00 00 01 00 00 02 00 00 00 04 00 00 00";
								break;
							case W1900:
								strHex = "4b 0f 10 00 00 00 00 00 10 00 00 00 03 00 00 00 83 03 a8 00 00 00 00 00 00 01 00 00 02 00 00 00 04 00 00 00";
								break;
							case W850:
								strHex = "4b 0f 10 00 00 00 00 00 10 00 00 00 03 00 00 00 83 03 28 04 00 00 00 00 00 01 00 00 02 00 00 00 04 00 00 00";
								break;
							case W900:
								strHex = "4b 0f 10 00 00 00 00 00 10 00 00 00 03 00 00 00 83 03 28 00 00 00 02 00 00 01 00 00 02 00 00 00 04 00 00 00";
								break;
						}
					}
					break;
				}
			}
		}

        if (0 == strHex.length())
            return false;


        byte[] bytes = this.hexStringToBytes(strHex);

        if (null != bytes) {
            boolean flag = DatasetManager.getInstance(context).devWritePort(bytes);
            if (!flag)
                return false;
            try {
                Thread.sleep(iTimeOut);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

	    return true;
    }

	private boolean lockBand_Normal(Context context, ForceNet netType, Band[] band, int iTimeOut){
		long iBandClassPref_nv441 = 0, iBandClassPref_nv946 = 0;
		long iBandClassPref_nv2954 = 0, iBandClassPref_NV6828 = 0;

		if (netType == ForceNet.NET_AUTO) {
			iBandClassPref_nv441	= 0xFFFFL;
			iBandClassPref_nv946	= 0xFFFFL;
			iBandClassPref_nv2954	= 0xFFFFFFFFL;
			iBandClassPref_NV6828 	= 0x7FFFFFFFFFFFFFFFL;
		} else {
			if (band.length == 0) {
				return false;
			}

			for (Band b : band) {
				switch (b) {
					case G1800:
						iBandClassPref_nv441 += 0x80;
						break;
					case G900:
						iBandClassPref_nv441 += 0x100; //E-GEM 900
						iBandClassPref_nv441 += 0x200; //P-GSM 900
						break;
					case G450:
						iBandClassPref_nv946 += 0x1;
						break;
					case G480:
						iBandClassPref_nv946 += 0x2;
						break;
					case G750:
						iBandClassPref_nv946 += 0x4;
						break;
					case G850:
						iBandClassPref_nv946 += 0x8;
						break;
					case G9000:
						iBandClassPref_nv946 += 0x10;
						break;
					case G1900:
						iBandClassPref_nv946 += 0x20;
						break;
					case W2100:
						iBandClassPref_nv441 += 0x40;
						break;
					case W1900:
						iBandClassPref_nv946 += 0x80;
						break;
					case W1800:
						iBandClassPref_nv946 += 0x100;
						break;
					case W1700:
						iBandClassPref_nv946 += 0x200;		//WCDMA 1700(U.S.)
						iBandClassPref_nv946 += 0x40000;	//WCDMA 1700(Japan.)
						break;
					case W850:
						iBandClassPref_nv946 += 0x400;
						break;
					case W800:
						iBandClassPref_nv946 += 0x800;
						break;
					case W2600:
						iBandClassPref_nv946 += 0x10000;
						break;
					case W900:
						iBandClassPref_nv946 += 0x20000;
						break;
					default:
						if (b.name().startsWith("L")) {
							String strBand = b.name();
							strBand = strBand.substring(strBand.indexOf("L") + 1);
							int iBv = Integer.parseInt(strBand);
							iBandClassPref_NV6828 += (((long)(1 << (iBv + 3) % 4)) << (((iBv - 1) / 4) * 4));
						}
						break;
				}
			}
		}

		byte[] nv441Bytes = lockWriteBandTrace((short)441, iBandClassPref_nv441);
		if (null != nv441Bytes) {
			boolean flag = DatasetManager.getInstance(context).devWritePort(nv441Bytes);
			if (!flag)
				return false;
			try {
				Thread.sleep(iTimeOut);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		byte[] nv946Bytes = lockWriteBandTrace((short)946, iBandClassPref_nv946);
		if (null != nv946Bytes) {
			boolean flag = DatasetManager.getInstance(context).devWritePort(nv946Bytes);
			if (!flag)
				return false;
			try {
				Thread.sleep(iTimeOut);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		byte[] nv2954Bytes = lockWriteBandTrace((short)2954, iBandClassPref_nv2954);
		if (null != nv2954Bytes) {
			boolean flag = DatasetManager.getInstance(context).devWritePort(nv2954Bytes);
			if (!flag)
				return false;
			try {
				Thread.sleep(iTimeOut);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		byte[] nv6828Bytes = lockWriteBandTrace((short)6828, iBandClassPref_NV6828);
		if (null != nv6828Bytes) {
			boolean flag = DatasetManager.getInstance(context).devWritePort(nv6828Bytes);
			if (!flag)
				return false;
			try {
				Thread.sleep(iTimeOut);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	private boolean lockNetwork_Normal(Context context,ForceNet networkType){
		byte netWorkBytes[] = new byte[133];

		int iPos = 0;
		netWorkBytes[iPos ++] = 0x27;
		netWorkBytes[iPos ++] = 0x0A;
		netWorkBytes[iPos ++] = 0x00;
		netWorkBytes[iPos ++] = 0x00;
		short iNetworkMode = 0;
		switch (networkType) {
			case NET_AUTO:
				iNetworkMode = 4;
				break;
			case NET_GSM:
				iNetworkMode = 13;
				break;
			case NET_WCDMA:
				iNetworkMode = 14;
				break;
			case NET_GSM_WCDMA:
				iNetworkMode = 17;
				break;
			case NET_WCDMA_LTE:
				iNetworkMode = 35;
				break;
			case NET_TDSCDMA:
				iNetworkMode = 53;
				break;
			case NET_GSM_TDSCDMA:
				iNetworkMode = 54;
				break;
			case NET_TDSCDMA_LTE:
				iNetworkMode = 60;
				break;
			case NET_LTE:
				iNetworkMode = 30;
				break;
			case NET_FDD_LTE:
				break;
			case NET_TDD_LTE:
				break;
			case NET_CDMA:
				iNetworkMode = 9;
				break;
			case NET_EVDO:
				iNetworkMode = 10;
				break;
			case NET_CDMA_EVDO:
				iNetworkMode = 19;
				break;
		}

		netWorkBytes[iPos ++] = (byte) (iNetworkMode & 0xFF);
		netWorkBytes[iPos ++] = (byte)((iNetworkMode >> 8) & 0xFF);

		boolean flag = DatasetManager.getInstance(context).devWritePort(netWorkBytes);
		if (!flag)
			return false;
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		rebootModem(context, 1000);

		return true;
	}

	private boolean lockNetwork_ByBand(Context context,ForceNet networkType, int iTimeOut){
		long iBandClassPref_nv441 = 0, iBandClassPref_nv946 = 0;
		long iBandClassPref_nv2954 = 0, iBandClassPref_NV6828 = 0;

		switch (networkType) {
			case NET_AUTO: {
				iBandClassPref_nv441 = 0xFFFFL;
				iBandClassPref_nv946 = 0xFFFFL;
				iBandClassPref_nv2954 = 0xFFFFFFFFL;
				iBandClassPref_NV6828 = 0x7FFFFFFFFFFFFFFFL;

				break;
			}
			case NET_GSM: {
				iBandClassPref_nv441 += 0x80; //GSM 1800  DCS 1800
				iBandClassPref_nv441 += 0x100; //E-GSM 900
				iBandClassPref_nv441 += 0x200; //P-GSM 900
				iBandClassPref_nv946 += 0x1; //GSM 450;
				iBandClassPref_nv946 += 0x2; //GSM 480
				iBandClassPref_nv946 += 0x4; //GSM 750
				iBandClassPref_nv946 += 0x8; //GSM 850
				iBandClassPref_nv946 += 0x10; //R-GSM 9000; Railway
				iBandClassPref_nv946 += 0x20; //GSM PCS 1900
				break;
			}
			case NET_CDMA_EVDO: {
				iBandClassPref_nv441 += 0x1; 	//BC0 System A only&#xd;&#xa;
				iBandClassPref_nv441 += 0x2; 	//BC0 System B only&#xd;&#xa;
				iBandClassPref_nv441 += 0x4; 	//BC1
				iBandClassPref_nv441 += 0x10;	//BC3
				iBandClassPref_nv441 += 0x20;	//BC4
				iBandClassPref_nv441 += 0x40;	//BC5
				iBandClassPref_nv441 += 0x400; 	//BC6
				iBandClassPref_nv441 += 0x800; 	//BC7
				iBandClassPref_nv441 += 0x1000; 	//BC8
				iBandClassPref_nv441 += 0x2000; 	//BC9
				iBandClassPref_nv441 += 0x4000; 	//BC10
				iBandClassPref_nv441 += 0x8000; 	//BC11

				iBandClassPref_nv946 += 0x1000; 	//BC12
				//case 13: //BC13
				iBandClassPref_nv946 += 0x2000; 	//BC14
				iBandClassPref_nv946 += 0x8000; 	//BC15

				iBandClassPref_nv2954 += 0x1000000; 	//BC16
				break;
			}
			case NET_WCDMA: {
				iBandClassPref_nv946 += 0x40; 		//WCDMA 2100
				iBandClassPref_nv946 += 0x80; 		//WCDMA PCS 1900
				iBandClassPref_nv946 += 0x100; 		//WCDMA DCS 1800
				iBandClassPref_nv946 += 0x200; 		//WCDMA 1700 (U.S.)
				iBandClassPref_nv946 += 0x400; 		//WCDMA 850
				iBandClassPref_nv946 += 0x800; 		//WCDMA 800

				iBandClassPref_nv2954 += 0x10000; 		//WCDMA 2600
				iBandClassPref_nv2954 += 0x20000; 		//WCDMA 900
				iBandClassPref_nv2954 += 0x40000; 		//WCDMA 1700 (Japan)
				break;
			}
			case NET_LTE: {
				iBandClassPref_NV6828 = 0x7FFFFFFFFFFFFFFFL;
				break;
			}
		}

		byte[] nv441Bytes = lockWriteBandTrace((short)441, iBandClassPref_nv441);
		if (null != nv441Bytes) {
			boolean flag = DatasetManager.getInstance(context).devWritePort(nv441Bytes);
			if (!flag)
				return false;
			try {
				Thread.sleep(iTimeOut);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		byte[] nv946Bytes = lockWriteBandTrace((short)946, iBandClassPref_nv946);
		if (null != nv946Bytes) {
			boolean flag = DatasetManager.getInstance(context).devWritePort(nv946Bytes);
			if (!flag)
				return false;
			try {
				Thread.sleep(iTimeOut);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		byte[] nv2954Bytes = lockWriteBandTrace((short)2954, iBandClassPref_nv2954);
		if (null != nv2954Bytes) {
			boolean flag = DatasetManager.getInstance(context).devWritePort(nv2954Bytes);
			if (!flag)
				return false;
			try {
				Thread.sleep(iTimeOut);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		byte[] nv6828Bytes = lockWriteBandTrace((short)6828, iBandClassPref_NV6828);
		if (null != nv6828Bytes) {
			boolean flag = DatasetManager.getInstance(context).devWritePort(nv6828Bytes);
			if (!flag)
				return false;
			try {
				Thread.sleep(iTimeOut);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	private boolean lockNetwork_NoRestart(Context context,ForceNet networkType, int iTimeOut){

        String hexStr = "";
        switch (networkType) {
            case NET_AUTO: {
                hexStr = "4b 0f 02 00 00 00 00 00 02 00 00 00 03 00 00 00 00 00 00 80 00 01 00 00 02 00 00 00 04 00 00 00";
                break;
            }
            case NET_GSM:{
                hexStr = "4b 0f 02 00 00 00 00 00 0d 00 00 00 03 00 00 00 00 00 00 80 00 01 00 00 02 00 00 00 04 00 00 00";
                break;
            }
            case NET_CDMA:{
                hexStr = "4b 0f 02 00 00 00 00 00 09 00 00 00 03 00 00 00 00 00 00 80 00 01 00 00 02 00 00 00 04 00 00 00";
                break;
            }
            case NET_EVDO:{
                hexStr = "4b 0f 02 00 00 00 00 00 0a 00 00 00 03 00 00 00 00 00 00 80 00 01 00 00 02 00 00 00 04 00 00 00";
                break;
            }
            case NET_WCDMA:{
                hexStr = "4b 0f 02 00 00 00 00 00 0e 00 00 00 03 00 00 00 00 00 00 80 00 01 00 00 02 00 00 00 04 00 00 00";
                break;
            }
            case NET_TDSCDMA:{
                hexStr = "4b 0f 02 00 00 00 00 00 3b 00 00 00 03 00 00 00 00 00 00 80 00 01 00 00 02 00 00 00 04 00 00 00";
                break;
            }
            case NET_LTE: {
				hexStr = "4b 0f 02 00 00 00 00 00 26 00 00 00 03 00 00 00 00 00 00 80 00 01 00 00 02 00 00 00 04 00 00 00";
				break;
			}case NET_CDMA_EVDO:{
				hexStr = "4b 0f 02 00 00 00 00 00 16 00 00 00 03 00 00 00 00 00 00 80 00 01 00 00 02 00 00 00 04 00 00 00";
				break;
            }
        }

        if (hexStr.length() <= 0)
            return false;

        byte[] bytes = this.hexStringToBytes(hexStr);

        if (null != bytes) {
            boolean flag = DatasetManager.getInstance(context).devWritePort(bytes);
            if (!flag)
                return false;
            try {
                Thread.sleep(iTimeOut);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

	private boolean lockFrequency_Normal(Context context, ForceNet netType, String... args){
		DatasetManager datasetManager = DatasetManager.getInstance(context);
		if (null == datasetManager)
		    return false;

	    byte[] bytes = this.getLockFreqOpenBytes(netType);
		if (bytes == null)
			return false;
		boolean flag = datasetManager.devWritePort(bytes);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		bytes = this.getLockFreqBytes(netType, args);
		if (bytes == null)
			return false;
		flag = datasetManager.devWritePort(bytes);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (netType == ForceNet.NET_LTE) {
            String strPath = "nv/item_files/modem/lte/rrc/csp\0";
            flag = syncLockcmd(context, strPath, 100);
        }

		flag = rebootModem(context, 100);

		return flag;
	}


	private boolean lockCellNormal(Context context, ForceNet netType, String... args){
	    DatasetManager datasetManager = DatasetManager.getInstance(context);
	    if (null == datasetManager)
	        return false;

		byte[] bytes = this.getLockCellOpenBytes(netType);
		boolean flag = DatasetManager.getInstance(context).devWritePort(bytes);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		bytes = this.getLockCellBytes(netType, args);
		flag = DatasetManager.getInstance(context).devWritePort(bytes);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

        if (netType == ForceNet.NET_LTE) {
            String strPath = "nv/item_files/modem/lte/rrc/csp\0";
            flag = syncLockcmd(context, strPath, 1000);
        }

		flag = rebootModem(context, 1000);

		return flag;
	}

	private boolean unlockFrequency_Normal(Context context, ForceNet networkType) {
	    DatasetManager datasetManager = DatasetManager.getInstance(context);
	    if (null == datasetManager)
	        return false;

		byte[] bytes = this.getUnlockFreqBytes(networkType);

		boolean flag = datasetManager.devWritePort(bytes);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (networkType == ForceNet.NET_LTE) {
            String backStr = "nv/item_files/modem/lte/rrc/csp\0";
            flag = syncLockcmd(context, backStr, 1000);
        }

        flag = rebootModem(context, 1000);

		return flag;
	}

	private boolean unlockCell_Normal(Context context, ForceNet networkType) {
        DatasetManager datasetManager = DatasetManager.getInstance(context);
        if (null == datasetManager)
            return false;

		byte[] bytes = this.getUnlockCellBytes(networkType);
		boolean flag = DatasetManager.getInstance(context).devWritePort(bytes);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (networkType == ForceNet.NET_LTE) {
            String backStr = "nv/item_files/modem/lte/rrc/csp\0";
            flag = syncLockcmd(context, backStr, 1000);
        }

        flag = rebootModem(context, 1000);

		return flag;
	}
}
