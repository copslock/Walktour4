package com.dingli.seegull;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.dingli.seegull.SeeGullFlags.ProtocolCodes;
import com.dingli.seegull.SeeGullFlags.ScanTypes;
import com.dingli.seegull.model.ChannelModel;
import com.dingli.seegull.model.ColorCodeModel;
import com.dingli.seegull.model.EtopNModel;
import com.dingli.seegull.model.RssiModel;
import com.dingli.seegull.model.ScanTaskModel;
import com.dingli.seegull.model.TopNModel;
import com.dingli.seegull.test.TestModels;
import com.walktour.Utils.StringUtil;
import com.walktour.gui.task.activity.scanner.ui.ScanTaskLtePilotActivity.RefDataMode;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 扫频仪任务操作控制类
 * @author zhihui.lian
 */
public class ScanTaskOperateFactory {

	private  ArrayList<ScanTaskModel> testModelList = new ArrayList<ScanTaskModel>();
	/** 唯一实例*/
	private static ScanTaskOperateFactory sInstance = null;

	private XmlSerializer serializer;

	private static XmlPullParser xmlParser;

	public static final String TESTTYPE = "TestType";
	/** 是否是PILOT编辑*/
	public static final String  IS_PILOT = "IsPilot";
	/** 是否是LTE编辑*/
	public static final String  IS_LTE = "IsLTE";
	/** 接口编码*/
	public static final String  PROTOCOL_CODE = "ProtocolCode";
	/** 通道类型*/
	public static final String  CHANNEL_STYLE = "ChannelStyle";

	public static final String IS_UPLOAD = "isUpload";

	private ArrayList<ChannelModel> channelList = new ArrayList<ChannelModel>();

	private ArrayList<ChannelModel> restoreChannelList = new ArrayList<ChannelModel>();				//还原频点列表

	private boolean isRecoverChannelModels = false;													//是否恢复频点列表

	private File newxmlfile;
	/** 为了远程验证请求json是否正确读取的临时文件 */
	private File mTestJsonFile;
	/** 为了远程验证请求json内容*/
	private String mTestJson = "";
	/**
	 * 工厂操作单例
	 * @return
	 */
	public static ScanTaskOperateFactory getInstance() {
		if(sInstance == null){
			sInstance = new ScanTaskOperateFactory();
		}
		return sInstance;
	}



	/**
	 * 初始化构造
	 */
	private ScanTaskOperateFactory() {
		newxmlfile = new File(Environment.getExternalStorageDirectory()+ "/scantasklist.xml");
		this.mTestJsonFile = new File(Environment.getExternalStorageDirectory()+ "/scantasktest.json");
		if(!newxmlfile.exists()){
			try {
				newxmlfile.createNewFile();
				XmlFileCreator(testModelList);
			} catch (Exception e) {
				Log.e("createException", "create xml exception");
			}
		}
		this.readJsonFile();
	}

	/**
	 * 读取json文件
	 */
	private void readJsonFile() {
		if (this.mTestJsonFile.exists()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(this.mTestJsonFile));
				String tempString = null;
				StringBuilder sb = new StringBuilder();
				// 一次读入一行，直到读入null为文件结束
				while ((tempString = reader.readLine()) != null) {
					sb.append(tempString.trim());
				}
				this.mTestJson = sb.toString();
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e1) {
					}
				}
			}
		}
	}

	/**
	 * 业务测试模板类型，控制界面模板
	 *  **加入分组类型，方便界面操作
	 * @author zhihui.lian
	 */
	public enum TestSchemaType {



		GSMCOLORCODE("GSM",1,2,"G-ColorCode"),
		GSMCW("GSM",1,1,"G-Cw"),
		WCDMACW("WCDMA",2,1,"W-Cw"),
		WCDMAPILOT("WCDMA",2,3,"W-Pilot"),
		CDMACW("CDMA",3,1,"C-Cw"),
		CDMAPILOT("CDMA",3,3,"C-Pilot"),
		EVDOCW("EVDO",4,1,"E-Cw"),
		EVDOPILOT("EVDO",4,3,"E-Pilot"),
		TDSCDMACW("TDSCDMA",5,1,"T-Cw"),
		TDSCDMAPILOT("TDSCDMA",5,3,"T-Pilot"),
		LTECW("LTE",6,1,"L-Cw"),
		LTEPILOT("LTE",6,4,"L-Pilot"),
		LTESPECTURM("LTE",6,5,"L-Specturm"),
		LTEBLIND("LTE",6,6,"L-Specturm");

		private String groupName;
		private int netWorkType;
		private int schemaTaskType;
		private String showFileName;

		public static final int GSM = 1;
		public static final int WCDMA = 2;
		public static final int CDMA = 3;
		public static final int EVDO = 4;
		public static final int TDSCDMA = 5;
		public static final int LTE = 6;


		public static final int CWTEST = 1;
		public static final int COLORCODETEST = 2;
		public static final int PILOTTEST = 3;
		public static final int LTEPILOTTEST = 4;
		public static final int SPECTURM=5;
		public static final int BLIND=6;



		private TestSchemaType(String groupName,int netWorkType,int schemaTaskType,String showFileName){
			this.groupName = groupName;
			this.netWorkType = netWorkType;
			this.schemaTaskType = schemaTaskType;
			this.showFileName = showFileName;
		}
		public String getGroupName() {
			return groupName;
		}
		public int getNetWorkType() {
			return netWorkType;
		}
		public int getSchemaTaskType() {
			return schemaTaskType;
		}
		public String getShowFileName() {
			return showFileName;
		}

	}


	/**
	 * 频点范围对应值
	 * 利用枚举操作
	 */
	public enum ChannelRange{


		G850(ProtocolCodes.PROTOCOL_GSM,"850",0x0100,0x0101,"127-252;127-252",""),
		G900(ProtocolCodes.PROTOCOL_GSM,"E-GSM900",0x0600,0x0601,"0-125,974-1023;0-125,974-1023",""),
		G1800(ProtocolCodes.PROTOCOL_GSM,"1800",0x0700,0x0701,"511-886;511-886",""),
		G1900(ProtocolCodes.PROTOCOL_GSM,"1900",0x0200,0x0201,"511-811;511-811",""),

		C450(ProtocolCodes.PROTOCOL_IS_2000_CDMA,"450MHz NMT",0x2F00,0x2F01,"27-374,1069-1443,2017,2018;1-400,1039-1473,2017,2018",""),
		C800(ProtocolCodes.PROTOCOL_IS_2000_CDMA,"800MHz cellular",0x0100,0x0101,"990-1023,1-780,1044-1323;990-1023,1-800,1024-1323",""),
		C1900(ProtocolCodes.PROTOCOL_IS_2000_CDMA,"1900",0x0200,0x0201,"13-1187;0-1200",""),

		E450(ProtocolCodes.PROTOCOL_IS_856_EVDO,"450MHz NMT",0x2F00,0x2F01,"27-374,1069-1443,2017,2018;1-400,1039-1473,2017,2018",""),
		E800(ProtocolCodes.PROTOCOL_IS_856_EVDO,"800MHz cellular",0x0100,0x0101,"990-1023,1-780,1044-1323;990-1023,1-800,1024-1323",""),
		E1900(ProtocolCodes.PROTOCOL_IS_856_EVDO,"1900",0x0200,0x0201,"13-1187;0-1200",""),

		W850(ProtocolCodes.PROTOCOL_3GPP_WCDMA,"V_850",0x0100,0x0101,"4355-4460,1005-1109;4345-4470,994-1120","4130-4235,780-884;4120-4245,770-894"),
		W900(ProtocolCodes.PROTOCOL_3GPP_WCDMA,"VIII_900",0x0600,0x0601,"2935-3090;2925-3100","2710-2865;2700-2875"),
		W1900(ProtocolCodes.PROTOCOL_3GPP_WCDMA,"II_1900",0x0200,0x0202,"9660-9940,410-689;9650-9950,399-700","9260-9540,10-289;9250-9550,0-299"),
		W1700(ProtocolCodes.PROTOCOL_3GPP_WCDMA,"IX_1700",0x07F8,0x07F9,"9235-9389;9225-9399","8760-8914;8750-8924"),
		W2100(ProtocolCodes.PROTOCOL_3GPP_WCDMA,"I_2100",0x0300,0x0301,"10560-10840;10550-10850","9610-9890;9600-9900"),

		T2000(ProtocolCodes.PROTOCOL_TDSCDMA,"Chinese 2000 (A)",0x2703,0x2703,"10054-10121;10050-10125;;;;;;10050-10125",""),
		T18TD(ProtocolCodes.PROTOCOL_TDSCDMA,"1.8TD(F)",0x1F03,0x1F03,"9404-9596;9400-9600;;;;;;9400-9600",""),

		LBand1(ProtocolCodes.PROTOCOL_LTE,"Band1",0x0300,0x0301,";0-599;;22-578;45-555;67-533;90-510",";18000-18599;;18022-18578;18045-18555;18067-18533;18090-18510"),
		LBand2(ProtocolCodes.PROTOCOL_LTE,"Band2",0x0200,0x0201,"605-1195;600-1199;613-1187;622-1178;645-1155;667-1133;690-1110","18605-19195;18600-19199;18613-19187;18622-19178;18645-19155;18667-19133;18690-19110"),
		LBand3(ProtocolCodes.PROTOCOL_LTE,"Band3",0x0700,0x0701,"1205-1945;1200-1949;1213-1937;1222-1928;1245-1905;1267-1883;1290-1860","19205-19945;19200-19949;19213-19937;19222-19928;19245-19905;19267-19883;19290-19860"),
		LBand5(ProtocolCodes.PROTOCOL_LTE,"Band5",0x0100,0x0101,"2405-2645;2400-2649;2413-2637;2422-2628;2445-2605","20405-20645;20400-20649;20413-20637;20422-20628;20445-20605"),
		LBand7(ProtocolCodes.PROTOCOL_LTE,"Band7",0x1200,0x1201,";2750-3449;;2772-3428;2795-3405;2817-3383;2840-3360",";20750-21449;;20772-21428;20795-21405;20817-21383;20840-21360"),
		LBand9(ProtocolCodes.PROTOCOL_LTE,"Band9",0x07F8,0x07F9,";3800-4149;;3822-4128;3845-4105;3867-4083;3890-4060",";21800-22149;;21822-22128;21845-22105;21867-22083;21890-22060"),
		LBand12(ProtocolCodes.PROTOCOL_LTE,"Band12",0x1100,0x1101,"5005-5175;5000-5179;5013-5167;5032-5158;5045-5135","23005-23175;23010-23179;23013-23167;23022-23158;23045-23135"),
		LBand17(ProtocolCodes.PROTOCOL_LTE,"Band17",0x11F8,0x11F9,"5735-5845;5730-5849;5743-5837;5752-5828;5775-5805","23735-23845;23730-23849;23743-23837;23752-23828;23775-23805"),
		LBand20(ProtocolCodes.PROTOCOL_LTE,"Band20",0x1A00,0x1A01,";6150-6449;;6172-6428;6195-6405;6217-6383;6240-6360",";24150-24449;;24172-24428;24195-24405;24217-24383;24240-24360"),

		LBand35(ProtocolCodes.PROTOCOL_TD_LTE,"Band35",0x2803,0x2803,"36355-36945;36350-36949;36363-36937;36372-36928;36395-36905;36417-36883;36440-36860",""),
		LBand38(ProtocolCodes.PROTOCOL_TD_LTE,"Band38",0x1C03,0x1C03,"37755-38245;37750-38249;37763-38237;37772-38228;37795-38205;37817-38183;37840-38160",""),
		LBand39(ProtocolCodes.PROTOCOL_TD_LTE,"Band39",0x1F03,0x1F03,"38255-38645;38250-38649;38263-38637;38272-38628;38295-38605;38317-38583;38340-38560",""),
		LBand40(ProtocolCodes.PROTOCOL_TD_LTE,"Band40",0x1D03,0x1D03,"38655-39645;38650-39649;38663-39637;38672-39628;38695-39605;38717-39583;38740-39560",""),
		LBand41(ProtocolCodes.PROTOCOL_TD_LTE,"Band41",0x1E03,0x1E03,"39655-41585;39650-41589;39663-41577;39672-41568;39695-41545;39717-41523;39740-41500",""),
		LBand42(ProtocolCodes.PROTOCOL_TD_LTE,"Band42",0x2303,0x2303,"41595-43585;41590-43589;41603-43577;41612-43568;41635-43545;41657-43523;41680-43500",""),
		LBand43(ProtocolCodes.PROTOCOL_TD_LTE,"Band43",0x2403,0x2403,"43595-45585;43590-45589;43603-45577;43612-45568;43635-45545;43657-45523;43680-45500",""),
		LBand44(ProtocolCodes.PROTOCOL_TD_LTE,"Band44",0x2503,0x2503,"45595-46585;45590-46589;45603-46577;45612-46568;45635-46545;45657-46523;45680-46500","");

		private int mNetType;				//标识区分网络
		private String mBandTypeName;		//频段类型名字
		private String[] mChannelRangeDL;		//下行频点支持范围
		private String[] mChannelRangeUL;		//上行频点支持范围
		private int mBandCodeDL;		//下行频段代号
		private int mBandCodeUL;		//上行频段代号


		ChannelRange(int netType,String bandTypeName,int bandCodeDL,int bandCodeUL,String channelRangeDL,String channelRangeUL){
			this.mNetType = netType;
			this.mBandTypeName = bandTypeName;
			this.mChannelRangeDL = channelRangeDL.split(";");
			if(StringUtil.isNullOrEmpty(channelRangeUL))
				channelRangeUL = channelRangeDL;
			this.mChannelRangeUL = channelRangeUL.split(";");
			this.mBandCodeDL = bandCodeDL;
			this.mBandCodeUL = bandCodeUL;
		}

		/**
		 * 获取频段名字数组
		 * @param channelStyle 带宽
		 * @param netType
		 * @param isUL 是否上行链路
		 * @param firstRow 第一行显示的选项
		 * @return
		 */
		public static String[] getBandStrArray(int channelStyle,int netType, boolean isUL, String firstRow) {
			Set<Integer> netTypeSet = new HashSet<Integer>();
			netTypeSet.add(netType);
			if (netType == ProtocolCodes.PROTOCOL_LTE) { // 组合Lte FDD与TDD
				netTypeSet.add(ProtocolCodes.PROTOCOL_TD_LTE);
			}
			List<String> bandStrList = new ArrayList<String>();
			bandStrList.add("--" + firstRow + "--");
			for (ChannelRange channel : ChannelRange.values()) {
				if (netTypeSet.contains(channel.mNetType)) {
					String[] channelRanges = isUL ? channel.mChannelRangeUL : channel.mChannelRangeDL;
					if(channelRanges.length > channelStyle){
						String channelRange = channelRanges[channelStyle];
						if(!TextUtils.isEmpty(channelRange)){
							bandStrList.add(channel.mBandTypeName);
						}
					}
				}
			}
			return bandStrList.toArray(new String[bandStrList.size()]);
		}

		/**
		 * 获取bandcode数组
		 * @param channelStyle 带宽
		 * @param netType 网络类型
		 * @param isUL 是否上行链路
		 * @return
		 */
		public static int[] getBandCodeArray(int channelStyle,int netType, boolean isUL) {
			Set<Integer> netTypeSet = new HashSet<Integer>();
			netTypeSet.add(netType);
			if (netType == ProtocolCodes.PROTOCOL_LTE) { // 组合Lte FDD与TDD
				netTypeSet.add(ProtocolCodes.PROTOCOL_TD_LTE);
			}
			List<Integer> bandCodeList = new ArrayList<Integer>();

			for (ChannelRange channel : ChannelRange.values()) {
				if (netTypeSet.contains(channel.mNetType)) {
					String[] channelRanges = isUL ? channel.mChannelRangeUL : channel.mChannelRangeDL;
					if(channelRanges.length > channelStyle){
						String channelRange = channelRanges[channelStyle];
						if(!TextUtils.isEmpty(channelRange)){
							bandCodeList.add(isUL ? channel.mBandCodeUL : channel.mBandCodeDL);
						}
					}
				}
			}
			int[] bandCodes = new int[bandCodeList.size()];
			for (int i = 0; i < bandCodes.length; i++) {
				bandCodes[i] = bandCodeList.get(i);
			}
			return bandCodes;
		}

		/**
		 * 获取频点范围数组集合
		 * @param netType 网络类型
		 * @param style 带宽类型
		 * @param isUL 是否上行链路
		 * @return
		 */
		public static List<String> getBandRangeList(int netType,int style, boolean isUL) {
			Set<Integer> netTypeSet = new HashSet<Integer>();
			netTypeSet.add(netType);
			if (netType == ProtocolCodes.PROTOCOL_LTE) { // 组合Lte FDD与TDD
				netTypeSet.add(ProtocolCodes.PROTOCOL_TD_LTE);
			}
			List<String> bandRangeList = new ArrayList<String>();
			for (ChannelRange channel : ChannelRange.values()) {
				if (netTypeSet.contains(channel.mNetType)) {
					String[] channels = isUL ? channel.mChannelRangeUL : channel.mChannelRangeDL;
					if (style < channels.length){
						String channel1 = channels[style];
						if(!TextUtils.isEmpty(channel1)){
							bandRangeList.add(channels[style]);
						}
					}
				}
			}
			return bandRangeList;
		}
	}

	/**
	 * 生成任务XML
	 * @param baseModelList
	 */
	private void XmlFileCreator(List<ScanTaskModel> baseModelList) {
		// we have to bind the new file with a FileOutputStream
		Log.d("xmlCreate", "create xml");
		FileOutputStream fileos = null;
		try {
			fileos = new FileOutputStream(newxmlfile);
		} catch (FileNotFoundException e) {
			Log.d("FileNotFoundException", "can't create FileOutputStream");
		}
		// we create a XmlSerializer in order to write xml data
		serializer = Xml.newSerializer();
		try {
			// we set the FileOutputStream as output for the serializer, using
			// UTF-8 encoding
			serializer.setOutput(fileos, "UTF-8");
			// Write <?xml declaration with encoding (if encoding not null) and
			// standalone flag (if standalone not null)
			serializer.startDocument(null, Boolean.valueOf(true));
			// set indentation option
			serializer.setFeature(
					"http://xmlpull.org/v1/doc/features.html#indent-output",
					true);
			ColorCodeModel gsmColorCodeModel = null;
			RssiModel gsmRssiModel = null;
			RssiModel wcdmaRssiModel = null;
			RssiModel evdoRssiModel = null;
			RssiModel tdscdmaRssiModel = null;
			RssiModel lteRssiModel = null;
			RssiModel cdmaRssiModel = null;

			TopNModel wcdmaPilotModel = null;
			TopNModel tdscdmaPilotModel = null;
			TopNModel evdoPilotModel = null;
			TopNModel cdmaPilotModel = null;
			EtopNModel ltePilotModel = null;

			for (int i = 0; i < baseModelList.size(); i++) {
				switch (TestSchemaType.valueOf(baseModelList.get(i)
						.getTaskType())) {
					case GSMCOLORCODE:
						gsmColorCodeModel = (ColorCodeModel) baseModelList.get(i);
						break;
					case GSMCW:
						gsmRssiModel = (RssiModel) baseModelList.get(i);
						break;
					case WCDMACW:
						wcdmaRssiModel = (RssiModel) baseModelList.get(i);
						break;
					case WCDMAPILOT:
						wcdmaPilotModel = (TopNModel) baseModelList.get(i);
						break;
					case TDSCDMACW:
						tdscdmaRssiModel = (RssiModel) baseModelList.get(i);
						break;
					case TDSCDMAPILOT:
						tdscdmaPilotModel = (TopNModel) baseModelList.get(i);
						break;
					case CDMACW:
						cdmaRssiModel = (RssiModel) baseModelList.get(i);
						break;
					case CDMAPILOT:
						cdmaPilotModel = (TopNModel) baseModelList.get(i);
						break;
					case EVDOCW:
						evdoRssiModel = (RssiModel) baseModelList.get(i);
						break;
					case EVDOPILOT:
						evdoPilotModel = (TopNModel) baseModelList.get(i);
						break;
					case LTECW:
						lteRssiModel = (RssiModel) baseModelList.get(i);
						break;
					case LTEPILOT:
						ltePilotModel = (EtopNModel) baseModelList.get(i);
						break;
					default:
						break;
				}
			}
			Log.d("xmlCreate", "create ScanScheme");
			serializer.startTag(null, "ScanScheme");
			// ///////////////////GSM//////////////////////
			serializer.startTag(null, "GsmGroup");
			serializer.startTag(null, "ColorCode");
			gsmColorCodeModelToXml(gsmColorCodeModel);
			serializer.endTag(null, "ColorCode");
			serializer.startTag(null, "Cw");
			rssiModelToXml(gsmRssiModel, TestSchemaType.GSMCW);
			serializer.endTag(null, "Cw");
			serializer.endTag(null, "GsmGroup");
			// ///////////////////WCDMA//////////////////////
			serializer.startTag(null, "WcdmaGroup");
			serializer.startTag(null, "Cw");
			rssiModelToXml(wcdmaRssiModel, TestSchemaType.WCDMACW);
			serializer.endTag(null, "Cw");
			serializer.startTag(null, "Pilot");
			topNModelToXml(wcdmaPilotModel, TestSchemaType.WCDMAPILOT);
			serializer.endTag(null, "Pilot");
			serializer.endTag(null, "WcdmaGroup");
			// ///////////////////Tdscdma//////////////////////
			serializer.startTag(null, "TdscdmaGroup");
			serializer.startTag(null, "Cw");
			rssiModelToXml(tdscdmaRssiModel, TestSchemaType.TDSCDMACW);
			serializer.endTag(null, "Cw");
			serializer.startTag(null, "Pilot");
			topNModelToXml(tdscdmaPilotModel, TestSchemaType.TDSCDMAPILOT);
			serializer.endTag(null, "Pilot");
			serializer.endTag(null, "TdscdmaGroup");
			// ///////////////////LTE//////////////////////
			serializer.startTag(null, "LteGroup");
			serializer.startTag(null, "Cw");
			rssiModelToXml(lteRssiModel, TestSchemaType.LTECW);
			serializer.endTag(null, "Cw");
			serializer.startTag(null, "Pilot");
			eTopNModelToXml(ltePilotModel);
			serializer.endTag(null, "Pilot");
			serializer.endTag(null, "LteGroup");

			// ///////////////////Cdma//////////////////////
			serializer.startTag(null, "CdmaGroup");
			serializer.startTag(null, "Cw");
			rssiModelToXml(cdmaRssiModel, TestSchemaType.CDMACW);
			serializer.endTag(null, "Cw");
			serializer.startTag(null, "Pilot");
			topNModelToXml(cdmaPilotModel, TestSchemaType.CDMAPILOT);
			serializer.endTag(null, "Pilot");
			serializer.endTag(null, "CdmaGroup");

			// ///////////////////evdo//////////////////////
			serializer.startTag(null, "EvdoGroup");
			serializer.startTag(null, "Cw");
			rssiModelToXml(evdoRssiModel, TestSchemaType.EVDOCW);
			serializer.endTag(null, "Cw");
			serializer.startTag(null, "Pilot");
			topNModelToXml(evdoPilotModel, TestSchemaType.EVDOPILOT);
			serializer.endTag(null, "Pilot");
			serializer.endTag(null, "EvdoGroup");

			serializer.endTag(null, "ScanScheme");

			serializer.endDocument();
			// write xml data into the FileOutputStream
			serializer.flush();
			// finally we close the file stream
			if(fileos != null)
				fileos.close();
		} catch (Exception e) {
			Log.e("Exception", "error occurred while creating xml file");
		}
	}


	/**
	 * 解析子节点
	 * @param nodeName
	 * @param nodeValue
	 */
	private  void NodeValue(String nodeName, Object nodeValue) {

		try {
			serializer.startTag("", nodeName);
			serializer.text(String.valueOf(nodeValue));
			serializer.endTag("", nodeName);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成频点XML
	 *
	 * @param channelModels
	 */
	private  void ChannleNodeXml(List<ChannelModel> channelModels) {

		// ChannelModel channelModel = new ChannelModel();
		// channelModel.setChannel(0x0101);
		// channelModel.setStartChannel(10688);
		// channelModel.setEndChannel(10688);

		if (channelModels == null) {
			channelModels = new ArrayList<ChannelModel>();
		}
		// for (int i = 0; i < 5; i++) {
		// channelModels.add(channelModel);
		// }

		try {
			serializer.startTag("", "Channels");
			for (int i = 0; i < channelModels.size(); i++) {
				serializer.startTag("", "Channel");
				serializer.attribute("", "BandCode", channelModels.get(i).getBandCode() + "");
				serializer.attribute("", "StartChannel", channelModels.get(i).getStartChannel() + "");
				serializer.attribute("", "EndChannel", channelModels.get(i).getEndChannel() + "");
				serializer.endTag("", "Channel");
			}
			serializer.endTag("", "Channels");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * ColorCodeModel Model转为XML
	 *
	 * @param colorCodeModel
	 */
	private  void gsmColorCodeModelToXml(ColorCodeModel colorCodeModel) {
		Log.d("xmlCreate", "colorCodeModel");
		NodeValue("Enable",colorCodeModel == null ? "0" : colorCodeModel.getEnable());
		NodeValue("TaskName", "ColorCode");
		NodeValue("TaskType", TestSchemaType.GSMCOLORCODE.name());
		NodeValue("IsUlorDl",
				colorCodeModel == null ? "false" : colorCodeModel.isUpload());
		NodeValue("RssiThreshold", colorCodeModel == null ? "-110"
				: colorCodeModel.getRssiThreshold());
		NodeValue("IsL3Msg",
				colorCodeModel == null ? "false" : colorCodeModel.isL3Msg());
		NodeValue("IsBsic",
				colorCodeModel == null ? "false" : colorCodeModel.isColorCode());
		NodeValue("IsCI",
				colorCodeModel == null ? "false" : colorCodeModel.isCI());
		NodeValue("BandWidth",
				colorCodeModel == null ? "0" : colorCodeModel.getStyle());
		ChannleNodeXml(colorCodeModel == null ? null : colorCodeModel
				.getChannelList());
	}

	/**
	 * CW也是RSSI RssiModel Model转为XML
	 *
	 * @param rssiModel
	 */
	private  void rssiModelToXml(RssiModel rssiModel,TestSchemaType taskType) {
		Log.d("xmlCreate", "rssiModel");
		NodeValue("Enable", rssiModel == null ? "0" : rssiModel.getEnable());
		NodeValue("TaskName", rssiModel == null ? "Cw" : rssiModel.getTaskName());
		NodeValue("TaskType", taskType.name());
		NodeValue("IsUlorDl",
				rssiModel == null ? "false" : rssiModel.isUpload());
		NodeValue("BandWidth",
				rssiModel == null ? "0" : rssiModel.getStyle());
		ChannleNodeXml(rssiModel == null ? null : rssiModel.getChannelList());
	}

	/**
	 * TopN 对应 pilot RssiModel Model转为XML
	 *
	 * @param topNModel
	 */
	private  void topNModelToXml(TopNModel topNModel,
								 TestSchemaType taskType) {
		Log.d("xmlCreate", "topNModel");
		NodeValue("Enable", topNModel == null ? "0" : topNModel.getEnable());
		NodeValue("TaskName", "Pilot");
		NodeValue("TaskType", taskType.name());
		NodeValue("IsUlorDl",
				topNModel == null ? "false" : topNModel.isUpload());
		NodeValue("IsPscOrPn",
				topNModel == null ? "false" : topNModel.isPscOrPn());
		NodeValue("NumberOfPilots",
				topNModel == null ? "10" : topNModel.getNumberOfPilots());
		NodeValue("PscStr", topNModel == null ? "" : topNModel.getPscStr());
		NodeValue("BandWidth",
				topNModel == null ? "0" : topNModel.getStyle());
		ChannleNodeXml(topNModel == null ? null : topNModel.getChannelList());
	}

	/**
	 * ETopN 对应 LTE Pilot EtopNModel转为XML
	 */

	private  void eTopNModelToXml(EtopNModel topNModel) {
		Log.d("xmlCreate", "eTopNModelToXml");
		NodeValue("Enable", topNModel == null ? "0" : topNModel.getEnable());
		NodeValue("TaskName", "Pilot");
		NodeValue("TaskType", TestSchemaType.LTEPILOT);
		NodeValue("IsUlorDl",
				topNModel == null ? "false" : topNModel.isUpload());
		NodeValue("NumberOfPilots",
				topNModel == null ? "16" : topNModel.getNumberOfSignals());
		NodeValue("CARRIER_RSSI_THRESHOLD", topNModel == null ? "-120"
				: topNModel.getCarrierRssiThreshold());
		NodeValue("WideBand", topNModel == null ? "6" : topNModel.getStyle());
		NodeValue("DataWideBand",
				topNModel == null ? "0" : topNModel.getRefWideBand()); // 对应界面参考数据模式
		NodeValue("PscStr", topNModel == null ? "" : topNModel.getPscStr());
		ChannleNodeXml(topNModel == null ? null : topNModel.getChannelList());
	}

	/**
	 * xml解析器
	 *
	 * @throws Exception
	 */
	private  void xmlParser() throws Exception {
		testModelList.clear();
		try {
			FileInputStream fs = new FileInputStream(newxmlfile);
			xmlParser = Xml.newPullParser();
			xmlParser.setInput(fs, "UTF-8");
			int eventType = xmlParser.getEventType();
			while ((eventType = xmlParser.next()) != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
					case XmlPullParser.START_TAG:
						if ("GsmGroup".equalsIgnoreCase(xmlParser.getName())) {
							parserGsmColorCodeXmlToModel();
							parserRssiXmlToModel();
						}else if("WcdmaGroup".equalsIgnoreCase(xmlParser.getName())){
							parserRssiXmlToModel();
							parserPilotXmlToModel();
						}
						else if("TdscdmaGroup".equalsIgnoreCase(xmlParser.getName())){
							parserRssiXmlToModel();
							parserPilotXmlToModel();
						}
						else if("EvdoGroup".equalsIgnoreCase(xmlParser.getName())){
							parserRssiXmlToModel();
							parserPilotXmlToModel();
						}
						else if("CdmaGroup".equalsIgnoreCase(xmlParser.getName())){
							parserRssiXmlToModel();
							parserPilotXmlToModel();
						}
						else if("LteGroup".equalsIgnoreCase(xmlParser.getName())){
							parserRssiXmlToModel();
							parserLtePilotXmlToModel();
						}
						break;

					case XmlPullParser.END_TAG:

						break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private  void parserRssiXmlToModel() throws Exception {

		RssiModel rssiModel = new RssiModel();
		rssiModel.setScanMode(0);
		rssiModel.setScanType(ScanTypes.eScanType_RssiChannel);
		rssiModel.setStyle(0);
		int eventType = xmlParser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_TAG:

					if ("Cw".equals(xmlParser.getName())) {

					}else if ("TaskName".equals(xmlParser.getName())) {
						rssiModel.setTaskName(xmlParser.nextText());
					}
					else if ("TaskType".equals(xmlParser.getName())) {
						TestSchemaType testType = TestSchemaType.valueOf(xmlParser.nextText());
						rssiModel.setTaskType(testType.name());
						rssiModel.setGroupName(testType.getGroupName());
						switch (testType) {
							case GSMCW:
								rssiModel.setProtocolCode(ProtocolCodes.PROTOCOL_GSM);
								break;

							case WCDMACW:
								rssiModel.setProtocolCode(ProtocolCodes.PROTOCOL_3GPP_WCDMA);
								break;
							case CDMACW:
								rssiModel.setProtocolCode(ProtocolCodes.PROTOCOL_IS_2000_CDMA);
								break;
							case EVDOCW:
								rssiModel.setProtocolCode(ProtocolCodes.PROTOCOL_IS_856_EVDO);
								break;
							case TDSCDMACW:
								rssiModel.setProtocolCode(ProtocolCodes.PROTOCOL_TDSCDMA);
								break;
							case LTECW:
								rssiModel.setProtocolCode(ProtocolCodes.PROTOCOL_LTE);
								break;
							default:
								break;
						}
					} else if ("Enable".equals(xmlParser.getName())) {
						rssiModel.setEnable(Integer.valueOf(xmlParser.nextText()));
					} else if ("IsUlorDl".equals(xmlParser.getName())) {
						rssiModel.setUpload(Boolean.valueOf(xmlParser.nextText()));
					}else if ("BandWidth".equals(xmlParser.getName())) {
						rssiModel.setStyle(Integer.valueOf(xmlParser.nextText()));
					} else if ("Channels".equals(xmlParser.getName())) {
						rssiModel.setChannelList(parserChannelModel());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("Cw".equals(xmlParser.getName())) {
						testModelList.add(rssiModel);
						return;
					}
			}
			eventType = xmlParser.next();
		}

	}

	/**
	 * GSM ColorCode ColorCode转Model
	 *
	 * @return
	 */
	public  void parserGsmColorCodeXmlToModel() throws Exception {
		ColorCodeModel colorCodeModel = new ColorCodeModel();
		colorCodeModel.setTaskType(TestSchemaType.GSMCOLORCODE.name());
		colorCodeModel.setStyle(0);
		colorCodeModel.setMultipleColorCode(false);
		colorCodeModel.setProtocolCode(ProtocolCodes.PROTOCOL_GSM);
		colorCodeModel.setScanType(ScanTypes.eScanType_ColorCode);
		colorCodeModel.setScanMode(0);

		int eventType = xmlParser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_TAG:

					if ("ColorCode".equals(xmlParser.getName())) {
						colorCodeModel.setTaskName("ColorCode");
						colorCodeModel.setGroupName(TestSchemaType.valueOf(colorCodeModel.getTaskType()).getGroupName());
					} else if ("Enable".equals(xmlParser.getName())) {
						colorCodeModel.setEnable(Integer.valueOf(xmlParser.nextText()));

					} else if ("IsUlorDl".equals(xmlParser.getName())) {
						colorCodeModel.setUpload(Boolean.valueOf(xmlParser.nextText()));

					} else if ("RssiThreshold".equals(xmlParser.getName())) {
						colorCodeModel.setRssiThreshold(Double.valueOf(xmlParser.nextText()));

					} else if ("IsL3Msg".equals(xmlParser.getName())) {
						colorCodeModel.setL3Msg(Boolean.valueOf(xmlParser.nextText()));

					} else if ("IsBsic".equals(xmlParser.getName())) {
						colorCodeModel.setColorCode(Boolean.valueOf(xmlParser.nextText()));
					} else if ("IsCI".equals(xmlParser.getName())) {
						colorCodeModel.setCI(Boolean.valueOf(xmlParser.nextText()));
					}else if ("BandWidth".equals(xmlParser.getName())) {
						colorCodeModel.setStyle(Integer.valueOf(xmlParser.nextText()));
					} else if ("Channels".equals(xmlParser.getName())) {
						colorCodeModel.setChannelList(parserChannelModel());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("ColorCode".equals(xmlParser.getName())) {
						testModelList.add(colorCodeModel);
						return;
					}
					break;
			}
			eventType = xmlParser.next();
		}
	}




	/**
	 * 临时用
	 */

	private ArrayList<ChannelModel> getFDDChannelList() {
		ArrayList<ChannelModel> list = new ArrayList<ChannelModel>();
		ChannelModel channelModel = new ChannelModel();
		channelModel.setBandCode(0x0700);
		channelModel.setStartChannel(1650);
		channelModel.setEndChannel(1650);
		list.add(channelModel);
		return list;
	}


	public  ScanTaskModel getETopNScanModelForFDD() {
		EtopNModel model = new EtopNModel();
		model.setCarrierRssiThreshold(-120.0f);
		model.setChannelList(getFDDChannelList());
		model.setCyclicPrefix(0);
		model.setEnable(1);
		model.setMeasurementThreshold(-150.0f);
		model.setNumberOfRxAntennaPorts(1);
		model.setNumberOfSignals(16);
		model.setNumberOfSubBands(4);
		model.setNumberOfTxAntennaPorts(0);
		model.setOperationalMode(9);
		model.setProtocolCode(ProtocolCodes.PROTOCOL_LTE);
		model.setRefMeasurementThreshold(-150.0f);
		model.setRefOperationalMode(9);
		model.setRefWideBand(21);//要根据protocol来判断 TDD25起 FDD18起
		model.setScanMode(0);
		model.setScanType(ScanTypes.eScanType_eTopNSignal);
		model.setStyle(6);
		model.setSubBandSize(4);
		model.setSubBandStart(0);
		model.setSyncMeasurementThreshold(-150.0f);
		model.setSyncOperationalMode(9);
		model.setSyncWideBand(14);
		model.setTaskName("ETopNScanForFDD");
		model.setTaskType("Normal");
		model.setUpload(false);
		model.setWideBand(13);
		return model;
	}



	/**
	 * Lte pilot 由XML转model
	 */
	private  void parserLtePilotXmlToModel() throws Exception {

//		EtopNModel eTopModel = new EtopNModel();
//		eTopModel.setTaskType(TestSchemaType.LTEPILOT.name());
//		eTopModel.setProtocolCode(ProtocolCodes.PROTOCOL_LTE);
//		eTopModel.setScanType(ScanTypes.eScanType_eTopNSignal);
//		eTopModel.setScanMode(0);
//		eTopModel.setCyclicPrefix(0);
//		eTopModel.setNumberOfRxAntennaPorts(1);
//		eTopModel.setNumberOfSubBands(4);
//		eTopModel.setNumberOfTxAntennaPorts(0);
//		eTopModel.setOperationalMode(9);
//		eTopModel.setRefMeasurementThreshold(-150.0f);
//		eTopModel.setRefOperationalMode(9);
//		eTopModel.setSubBandSize(4);
//		eTopModel.setSubBandStart(0);
//		eTopModel.setSyncMeasurementThreshold(-150.0f);
//		eTopModel.setSyncOperationalMode(9);
//		eTopModel.setSyncWideBand(14); // 同步数据模式
//		eTopModel.setMeasurementThreshold(-150.0f);
//		eTopModel.setWideBand(13);


		EtopNModel eTopModel = new EtopNModel();
		eTopModel.setTaskType(TestSchemaType.LTEPILOT.name());
		eTopModel.setCyclicPrefix(0);
		eTopModel.setMeasurementThreshold(-150.0f);
		eTopModel.setNumberOfRxAntennaPorts(1);
		eTopModel.setNumberOfSubBands(4);
		eTopModel.setNumberOfTxAntennaPorts(0);
		eTopModel.setOperationalMode(9);
		eTopModel.setProtocolCode(ProtocolCodes.PROTOCOL_LTE);
		eTopModel.setRefMeasurementThreshold(-150.0f);
		eTopModel.setRefOperationalMode(9);
		eTopModel.setScanMode(0);
		eTopModel.setScanType(ScanTypes.eScanType_eTopNSignal);
		eTopModel.setSubBandSize(4);
		eTopModel.setSubBandStart(0);
		eTopModel.setSyncMeasurementThreshold(-150.0f);
		eTopModel.setSyncOperationalMode(9);
		eTopModel.setSyncWideBand(14);
		eTopModel.setTaskName("ETopNScanForFDD");
		eTopModel.setWideBand(13);


		int eventType = xmlParser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_TAG:

					if ("Pilot".equals(xmlParser.getName())) {
						eTopModel.setTaskName("Pilot");
						eTopModel.setGroupName(TestSchemaType.valueOf(eTopModel.getTaskType()).getGroupName());
					} else if ("Enable".equals(xmlParser.getName())) {
//					xmlParser.nextText();
						eTopModel.setEnable(Integer.valueOf(xmlParser.nextText()));
					}

					else if ("IsUlorDl".equals(xmlParser.getName())) {
						eTopModel.setUpload(Boolean.valueOf(xmlParser.nextText()));
					} else if ("NumberOfPilots".equals(xmlParser.getName())) {
						eTopModel.setNumberOfSignals(Integer.valueOf(xmlParser.nextText()));
					} else if ("CARRIER_RSSI_THRESHOLD".equals(xmlParser.getName())) {
						eTopModel.setCarrierRssiThreshold(Double.valueOf(xmlParser.nextText()));

					} else if ("WideBand".equals(xmlParser.getName())) {
						eTopModel.setStyle(Integer.valueOf(xmlParser.nextText()));
					} else if ("DataWideBand".equals(xmlParser.getName())) {
						eTopModel.setRefWideBand(Integer.valueOf(xmlParser.nextText())); // //参考数据模式
//					eTopModel.setRefWideBand(21);//要根据protocol来判断 TDD25起 FDD18起
					} else if ("PscStr".equals(xmlParser.getName())) {
						eTopModel.setPscStr(xmlParser.nextText());
					}

					else if ("Channels".equals(xmlParser.getName())) {
						eTopModel.setChannelList(parserChannelModel());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("Pilot".equals(xmlParser.getName())) {
						testModelList.add(eTopModel);
						return;
					}
					break;
			}
			eventType = xmlParser.next();
		}

	}

	/**
	 * 普通 pilot 由XML转model
	 */
	private  void parserPilotXmlToModel() throws Exception {

		TopNModel topNModel = new TopNModel();
		topNModel.setScanType(ScanTypes.eScanType_TopNPilot);
		topNModel.setScanMode(0);
		topNModel.setPilotThreshold(-30.0f);

		int eventType = xmlParser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("Pilot".equals(xmlParser.getName())) {
						topNModel.setTaskName("Pilot");
					} else if ("TaskType".equals(xmlParser.getName())) {
						TestSchemaType taskType = TestSchemaType.valueOf(xmlParser.nextText());

						topNModel.setTaskType(taskType.name());
						topNModel.setGroupName(taskType.getGroupName());
						switch (taskType) {
							case WCDMAPILOT:
								topNModel.setProtocolCode(ProtocolCodes.PROTOCOL_3GPP_WCDMA);
								topNModel.setAggregateEcIoEnable(1);
								topNModel.setDelaySpreadEnable(1);
								topNModel.setEcEnable(1);
								topNModel.setEcioEnable(1);
								topNModel.setEpsIoEnable(1);
								topNModel.setEssIoEnable(1);
								topNModel.setBchLayer3MessageDecodingEnable(0);
								topNModel.setRakeFingerCountEnable(1);
								topNModel.setSirEnable(1);
								topNModel.setTimeOffsetEnable(1);
								topNModel.setPilotMode(1);
								break;
							case CDMAPILOT:
								topNModel.setProtocolCode(ProtocolCodes.PROTOCOL_IS_2000_CDMA);
								topNModel.setAggregateEcIoEnable(1);
								topNModel.setDelaySpreadEnable(1);
								topNModel.setEcEnable(1);
								topNModel.setEcioEnable(1);
								topNModel.setEpsIoEnable(0);
								topNModel.setEssIoEnable(0);
								topNModel.setBchLayer3MessageDecodingEnable(0);
								topNModel.setRakeFingerCountEnable(0);
								topNModel.setSirEnable(0);
								topNModel.setTimeOffsetEnable(1);
								topNModel.setPilotMode(1);
								break;
							case EVDOPILOT:
								topNModel.setProtocolCode(ProtocolCodes.PROTOCOL_IS_856_EVDO);
								topNModel.setAggregateEcIoEnable(1);
								topNModel.setDelaySpreadEnable(1);
								topNModel.setEcEnable(1);
								topNModel.setEcioEnable(1);
								topNModel.setEpsIoEnable(0);
								topNModel.setEssIoEnable(0);
								topNModel.setBchLayer3MessageDecodingEnable(0);
								topNModel.setRakeFingerCountEnable(0);
								topNModel.setSirEnable(0);
								topNModel.setTimeOffsetEnable(1);
								topNModel.setPilotMode(1);
								break;
							case TDSCDMAPILOT:
								topNModel.setProtocolCode(ProtocolCodes.PROTOCOL_TDSCDMA);
								topNModel.setAggregateEcIoEnable(0);
								topNModel.setDelaySpreadEnable(0);
								topNModel.setEcEnable(1);
								topNModel.setEcioEnable(1);
								topNModel.setEpsIoEnable(1);
								topNModel.setEssIoEnable(0);
								topNModel.setBchLayer3MessageDecodingEnable(0);						//层三信息值必须为0，否则没法测试，因为扫频仪不够权限
								topNModel.setRakeFingerCountEnable(0);
								topNModel.setSirEnable(1);
								topNModel.setTimeOffsetEnable(1);
								topNModel.setPilotMode(4);//3-syncDlPilotMode  4- midamblePilotMode
								break;
							default:
								break;
						}
					} else if ("Enable".equals(xmlParser.getName())) {
						topNModel.setEnable(Integer.valueOf(xmlParser.nextText()));
					} else if ("IsUlorDl".equals(xmlParser.getName())) {
						topNModel.setUpload(Boolean.valueOf(xmlParser.nextText()));
					} else if ("NumberOfPilots".equals(xmlParser.getName())) {
						topNModel.setNumberOfPilots(Integer.valueOf(xmlParser.nextText()));
					} else if ("IsPscOrPn".equals(xmlParser.getName())) {
						topNModel.setPscOrPn(Boolean.valueOf(xmlParser.nextText()));
					} else if ("PscStr".equals(xmlParser.getName())) {
						topNModel.setPscStr(xmlParser.nextText());
					}else if ("BandWidth".equals(xmlParser.getName())) {
						topNModel.setStyle(Integer.valueOf(xmlParser.nextText()));
					} else if ("Channels".equals(xmlParser.getName())) {
						topNModel.setChannelList(parserChannelModel());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("Pilot".equals(xmlParser.getName())) {
						testModelList.add(topNModel);
						return;
					}
					break;
			}
			eventType = xmlParser.next();
		}
	}

	/**
	 * 解析频点model
	 *
	 * @return
	 * @throws Exception
	 */
	private  ArrayList<ChannelModel> parserChannelModel()throws Exception {

		ArrayList<ChannelModel> channelList = new ArrayList<ChannelModel>();
		ChannelModel channelModel = null;

		int eventType = xmlParser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("Channel".equals(xmlParser.getName())) {
						channelModel = new ChannelModel();
						channelModel.setBandCode(Integer.valueOf(xmlParser.getAttributeValue(null, "BandCode")));
						channelModel.setStartChannel(Integer.valueOf(xmlParser.getAttributeValue(null, "StartChannel")));
						channelModel.setEndChannel(Integer.valueOf(xmlParser.getAttributeValue(null, "EndChannel")));
					}
					break;
				case XmlPullParser.END_TAG:
					if ("Channel".equals(xmlParser.getName())) {
						channelList.add(channelModel);
					}
					if ("Channels".equals(xmlParser.getName())) {
						return channelList;
					}
			}
			eventType = xmlParser.next();
		}
		return channelList;
	}


	public ArrayList<ChannelModel> getChannelList() {
		return channelList;
	}

	public void setChannelList(ArrayList<ChannelModel> channelList) {
		this.channelList = channelList;

	}


	public ArrayList<ChannelModel> getRestoreChannelList() {
		return restoreChannelList;
	}

	public void setRestoreChannelList(ArrayList<ChannelModel> restoreChannelList) {
		this.restoreChannelList = restoreChannelList;
	}



	public boolean isRecoverChannelModels() {
		return isRecoverChannelModels;
	}

	public void setRecoverChannelModels(boolean isRecoverChannelModels) {
		this.isRecoverChannelModels = isRecoverChannelModels;
	}



	/**
	 * 设置任务列表勾选框
	 */
	public void setEnable(ScanTaskModel taskModel, int position) {
		for (int i = 0; i < testModelList.size(); i++) {
			if (i == position){
				testModelList.remove(position);
				testModelList.add(position, taskModel);
				break;
			}
		}
		setTaskModelToFile(testModelList);
	}



	/**
	 * 将内存中的值，写入文件
	 */

	public void setTaskModelToFile(ArrayList<ScanTaskModel> taskModels){
		System.out.println(taskModels.toString());
		XmlFileCreator(taskModels);
	}

	/**
	 * 任务获取接口，能获取整个配置任务列表
	 * @return
	 */
	public  ArrayList<ScanTaskModel> getTestModelList() {
		try {
			xmlParser();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return testModelList;
	}



	/**
	 * 最终获取列表
	 * @return
	 */
	public ArrayList<ScanTaskModel> enableTasks(){
		ArrayList<ScanTaskModel> models = new ArrayList<ScanTaskModel>();
		models.clear();
		for (ScanTaskModel model : getTestModelList()) {
			if(model.getEnable() == 1){
				if(model.getProtocolCode() == ProtocolCodes.PROTOCOL_LTE){
					models.addAll(splitLteModel(model));
				}else{
					models.add(model);
				}
			}
		}
		return models;
	}


	/**
	 * 获取最终文件名
	 * Out2014-12020212_Scanner_LTE-Pilot_W-Pilot
	 */
	public String getScanFileName(){
		String scanFileName = "_Scanner";
		List<ScanTaskModel> bestModels = enableTasks();
		for (int i = 0; i < bestModels.size(); i++) {
			TestSchemaType taskType = TestSchemaType.valueOf(bestModels.get(i).getTaskType());
			if(i < 3 && scanFileName.lastIndexOf(taskType.getShowFileName()) == -1){
				scanFileName = scanFileName + "_" + taskType.getShowFileName();
			}

		}
		return scanFileName;
	}



	/**
	 * 将LTE Model 跟BandCode范围拆分为TDD Model与 FDD Model，给扫频仪分批发送命令
	 * @return lteModels
	 * @param lteModel
	 * ********bandCode范围**********
	 *  **FDD**        **TDD**
	 *    512		    7171
	 *	  768			7427
	 *	  1792			7683
	 *	  4352			7939
	 *	  4600			8963
	 *	  4608			9219
	 *					9475
	 *					10243
	 */

	ArrayList<ChannelModel> fddChannelModels = new ArrayList<ChannelModel>();

	ArrayList<ChannelModel> tddChannelModels = new ArrayList<ChannelModel>();

	private ArrayList<ScanTaskModel> splitLteModel(ScanTaskModel lteModel){
		fddChannelModels.clear();
		tddChannelModels.clear();
		ArrayList<ScanTaskModel> lteModels = new ArrayList<ScanTaskModel>();
		switch (TestSchemaType.valueOf(lteModel.getTaskType())) {
			case LTECW:
				RssiModel rssiModel = (RssiModel)lteModel;
				ArrayList<ChannelModel> channelModels = rssiModel.getChannelList();
				for (int i = 0; i < channelModels.size(); i++) {
					int bandCode = channelModels.get(i).getBandCode();
					if( bandCode >= 512 && bandCode <= 4609){
						//////FDD
						fddChannelModels.add(channelModels.get(i));
					}else{
						//////TDD
						tddChannelModels.add(channelModels.get(i));
					}
				}
				if(fddChannelModels.size() != 0){
					RssiModel fddModel = rssiModel.clone();
					fddModel.setProtocolCode(ProtocolCodes.PROTOCOL_LTE);
					fddModel.setChannelList(fddChannelModels);
					lteModels.add(fddModel);
				}
				if(tddChannelModels.size() != 0){
					RssiModel tddModel = rssiModel.clone();
					tddModel.setProtocolCode(ProtocolCodes.PROTOCOL_TD_LTE);
					tddModel.setChannelList(tddChannelModels);
					lteModels.add(tddModel);
				}
				break;

			case LTEPILOT:
				EtopNModel eTopNModel = (EtopNModel)lteModel;
				ArrayList<ChannelModel> eChannelModels = eTopNModel.getChannelList();
				for (int i = 0; i < eChannelModels.size(); i++) {
					int bandCode = eChannelModels.get(i).getBandCode();
					if( bandCode >= 512 && bandCode <= 4609){
						//////FDD
						fddChannelModels.add(eChannelModels.get(i));
					}else{
						//////TDD
						tddChannelModels.add(eChannelModels.get(i));
					}
				}
				if(fddChannelModels.size() != 0){
					EtopNModel fddModel = eTopNModel.clone();
					fddModel.setProtocolCode(ProtocolCodes.PROTOCOL_LTE);
					fddModel.setRefWideBand(RefDataMode.DB.getDateCode(eTopNModel.getRefWideBand()).getFdModeCode());
					fddModel.setChannelList(fddChannelModels);
					lteModels.add(fddModel);
				}
				if(tddChannelModels.size() != 0){
					EtopNModel tddModel = eTopNModel.clone();
					tddModel.setProtocolCode(ProtocolCodes.PROTOCOL_TD_LTE);
					tddModel.setRefWideBand(RefDataMode.DB.getDateCode(eTopNModel.getRefWideBand()).getTdModeCode());
					tddModel.setChannelList(tddChannelModels);
					lteModels.add(tddModel);
				}

				break;
			default:
				break;
		}
		return lteModels;
	}



	/**
	 * 判断任务配置是否有勾选项
	 */
	public boolean hasEnableTask(){
		boolean hasTask = false;
		for(ScanTaskModel model :  getTestModelList()){
			if(model.getEnable() == 1){
				hasTask = true;
				break;
			}
		}
		return hasTask;
	}

	public ArrayList<ScanTaskModel> addDefault(){
		if(testModelList.size() <= 0){
			List<ScanTaskModel> mModelList = (new TestModels()).getTestModelList();
			testModelList.addAll(mModelList);
		}
		return testModelList;
	}



	public String getTestJson() {
		return mTestJson;
	}

}
