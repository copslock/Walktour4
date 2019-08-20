/**
 * com.walktour.Utils
 * UnifyStruct.java
 * 类功能：
 * 2013-12-20-下午4:26:59
 * 2013鼎利-版权所有
 * @author qihang.li@dinglicom.com
 */
package com.walktour.Utils;

import com.google.gson.Gson;
import com.walktour.base.util.LogUtil;
import com.walktour.model.LteGsmStructModel;
import com.walktour.model.T5GNRCellBlock;
import com.walktour.model.T5GSingleBeamInfo;

import java.util.ArrayList;

/**
 * UnifyStruct
 * 数据集的特殊参数结构体
 * 2013-12-20 下午4:26:59
 * 
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
public class UnifyStruct {
	
	/**从结构体或取的参数*/
	public static final int FLAG_TD_TDPhysChannelInfoDataV2					= 0x7F037201;
	public static final int FLAG_TD_HSDPAPhysChannelInfoData				= 0x7F037202;
	public static final int FLAG_TD_Activate_PDP_Context_Accept_Win_Data	= 0x7F1D7101;

	public static final int FLAG_WCDMA_TrCH_DL_Configuration				= 0x7F020520;
	public static final int FLAG_WCDMA_RLC_DL_Entities						= 0x7F02051C;
	public static final int FLAG_WCDMA_RLC_UL_Entities						= 0x7F02051B;
	
	private static final String TAG = "UnifyStruct";
	
	public abstract interface ParaStruct{
		
	}
	
	
	public class TDPhysChannelInfoDataV2 implements ParaStruct{
		
		public final static int FLAG = FLAG_TD_TDPhysChannelInfoDataV2;
		
		private int[] integers;
		
		public int channelNum = 2;
		
		/**
		 * chanInfo[0]为下行信道，chanInfo[1]为上行信道
		 */
		public ChannelInfo[] chanInfo = new ChannelInfo[2];

		/**
		 * 请参考<数据集特殊结构存储文件>
		 * byte[]如下
		 * 01 72 03 7f --->flag
		 * 01 00 00 00 --->IntCount
		 * 02 00 00 00 --->TDPhysChannelNum
		 * 00 00 00 00 --->64bitIntCount
		 * 00 00 00 00 --->Double Count
		 * 
		 * --------------->TDPhysChanInfo存储结构[1]
		 * 0a 00 00 00 --->IntCount,值为10
		 * 02 00 00 00 --->timeslot
		 * 01 00 00 00 --->direction
		 * 88 27 00 00 --->workfrequency
		 * 08 00 00 00 --->SF
		 * 01 00 00 00 --->codeNumber
		 * 01 00 00 00 
		 * 00 00 00 00 
		 * 01 00 00 00 
		 * 04 00 00 00 
		 * f1 d8 ff ff 
		 * 00 00 00 00 
		 * 00 00 00 00 
		 * 01 00 00 00 --->CodeNO.
		 * 06 00 00 00 --->CodeNO.1
		 * 
		 * --------------->TDPhysChanInfo存储结构[2]
		 * 0a 00 00 00 --->IntCount,值为10
		 * 06 00 00 00 --->timeslot
		 * 00 00 00 00 --->direction
		 * 88 27 00 00 --->workfrequency
		 * 10 00 00 00 --->SF
		 * 01 00 00 00 --->codeNumber
		 * 01 00 00 00 
		 * 00 00 00 00 
		 * 01 00 00 00 
		 * 04 00 00 00 
		 * f1 d8 ff ff 
		 * 00 00 00 00 
		 * 00 00 00 00 
		 * 01 00 00 00 --->CodeNO.
		 * 0e 00 00 00 --->CodeNO.1
		 */
		public TDPhysChannelInfoDataV2(byte[] byteDataset) {
			
			integers = byteToIntArray(byteDataset);
			
			channelNum = getInt(byteDataset,8);
			
			if( channelNum >0 && integers.length > 5 ){
				
				int startPoint = 5;
				int[] intArray1 = subArray( integers, startPoint, integers.length-1 );
				ChannelInfo info1 = new ChannelInfo( intArray1, 13);
				
				//18+info1.codeNO.length+1
				startPoint = startPoint + 13 + info1.codeNO.length + 1;
				int[] intArray2 = subArray( integers, startPoint, integers.length-1);
				ChannelInfo info2 = new ChannelInfo( intArray2, 13);
				
				chanInfo[0] = info1;
				chanInfo[1] = info2;
			}
		}

	}
	
	
	/**
	 * 
	 * HSDPAPhysChannelInfoData
	 *   	12-21 23:37:58.191: I/DatasetBuilder(21558): ---struct:0x7f037202,byte[] length:292
//    		02 72 03 7f --->flag
//    		03 00 00 00 
//    		02 00 00 00 --->SCCHChannelNum
//    		00 00 00 00 --->PDSCHChannelNum
//    		02 00 00 00 --->SICHChannelNum
//    		00 00 00 00 
//    		00 00 00 00 
							---->SCCHChannelInfo 下行		
//    		0b 00 00 00 
//    		06 00 00 00 
//    		00 00 00 00 
//    		00 00 00 00 
//    		10 00 00 00
//    		02 00 00 00
//    		04 00 00 00
//    		00 00 00 00
//    		00 00 00 00 
//    		f1 d8 ff ff 
//    		04 00 00 00 
//    		00 00 00 00 
//    		00 00 00 00 
//    		00 00 00 00 
//    		02 00 00 00 
//    		0d 00 00 00 
//    		0e 00 00 00 
     						---->SCCHChannelInfo 上行			
//    		0b 00 00 00 
//    		06 00 00 00 
//    		00 00 00 00 
//    		00 00 00 00 
//    		10 00 00 00 
//    		02 00 00 00 
//    		04 00 00 00 
//    		00 00 00 00 
//    		00 00 00 00 
//    		f1 d8 ff ff 
//    		04 00 00 00 
//    		00 00 00 00 
//    		00 00 00 00 
//    		00 00 00 00 
//    		02 00 00 00 
//    		0f 00 00 00 
//    		10 00 00 00
    						---->SICHChannelNum 
//    		0b 00 00 00 
//    		01 00 00 00 
//    		01 00 00 00 
//    		00 00 00 00 
//    		10 00 00 00 
//    		01 00 00 00
//    		05 00 00 00
//    		00 00 00 00
//    		00 00 00 00 
//    		f1 d8 ff ff 
//    		04 00 00 00 
//    		00 00 00 00 
//    		00 00 00 00
//    		00 00 00 00 
//    		01 00 00 00
//    		0e 00 00 00 
//    		
//    		0b 00 00 00 
//    		01 00 00 00
//    		01 00 00 00 
//    		00 00 00 00 
//    		10 00 00 00 
//    		01 00 00 00 
//    		05 00 00 00 
//    		00 00 00 00
//    		00 00 00 00 
//    		f1 d8 ff ff 
//    		04 00 00 00
//    		00 00 00 00 
//    		00 00 00 00
//    		00 00 00 00
//    		01 00 00 00 
//    		0c 00 00 00 
	 */
	
	
	/***
	 * 02 72 03 7f 
	 * 03 00 00 00 
	 * 00 00 00 00 
	 * 03 00 00 00 
	 * 00 00 00 00 
	 * 00 00 00 00 
	 * 00 00 00 00 
	 * 
	 * 0b 00 00 00 
	 * 03 00 00 00 
	 * 00 00 00 00 
	 * 4e 27 00 00 
	 * 01 00 00 00 
	 * 01 00 00 00 
	 * 02 00 00 00 
	 * 00 00 00 00 
	 * 00 00 00 00 
	 * 00 00 00 00 
	 * 04 00 00 00 
	 * 00 00 00 00 
	 * 00 00 00 00 
	 * 00 00 00 00 
	 * 01 00 00 00 
	 * 01 00 00 00 
	 * 
	 * 0b 00 00 00 
	 * 04 00 00 00 
	 * 00 00 00 00 
	 * 4e 27 00 00 
	 * 01 00 00 00 
	 * 01 00 00 00 
	 * 02 00 00 00 
	 * 00 00 00 00 
	 * 00 00 00 00 
	 * 00 00 00 00 
	 * 04 00 00 00 
	 * 00 00 00 00 
	 * 00 00 00 00 
	 * 00 00 00 00 
	 * 01 00 00 00 
	 * 01 00 00 00 
	 * 
	 * 0b 00 00 00 
	 * 05 00 00 00 
	 * 00 00 00 00 
	 * 4e 27 00 00 
	 * 01 00 00 00 
	 * 01 00 00 00 
	 * 02 00 00 00 
	 * 00 00 00 00 
	 * 00 00 00 00 
	 * 00 00 00 00 
	 * 04 00 00 00 
	 * 00 00 00 00 
	 * 00 00 00 00 
	 * 00 00 00 00 
	 * 01 00 00 00 
	 * 01 00 00 00 
	 */
	public class HSDPAPhysChannelInfoData implements ParaStruct{
		public final static int FLAG = FLAG_TD_HSDPAPhysChannelInfoData;
		
		private int[] integers;
		
		public ArrayList<ChannelInfo> chanInfo = new ArrayList<ChannelInfo>();
		
		public HSDPAPhysChannelInfoData(byte[] byteDataset) {
			if(byteDataset.length == 0){
				return;
			}
			
			integers = byteToIntArray(byteDataset);
			
			if( integers.length > 7 ){
				
				int SCCHChannelNum 	= getInt(byteDataset,8);
				int PDSCHChannelNum	= getInt(byteDataset,12);
				int SICHChannelNum	= getInt(byteDataset,16);
				
				int totalChannelCount = SCCHChannelNum + PDSCHChannelNum + SICHChannelNum;
				
				int startPoint = 7 ;		//在integers里的起点
				int lastChannelIntCount = 0;//上一个Channel占用的integer个数
				
				for( int i=0;i<totalChannelCount;i++){
					startPoint = startPoint + lastChannelIntCount;
					int[] subIntArray = subArray( integers, startPoint, integers.length-1);
					ChannelInfo info = new ChannelInfo( subIntArray, 14);
					if( info.channelType!= ChannelInfo.INVALID_TYPE ){
						chanInfo.add( info );
						lastChannelIntCount = info.getIntegerCount();
					}else{
						//假如有无效的Channel出现，不再添加(数据集应该不会出现这个情况)
						break;
					}
				}
			}
		}
	}
	
	public class PDPInfoDataV2 implements ParaStruct{
		public final static int FLAG = FLAG_TD_Activate_PDP_Context_Accept_Win_Data;
		
		 public int Delay_Class;
		 public int Reliability_Class;
		 public int Peak_Throughput;
		 public int Precedence_Class;
		 public int Mean_Throughput;
		 public int Radio_Priority;
		 public int LLC_SAPI;
		 public int UL_Max_bit_Rate;
		 public int DL_Max_bit_Rate;
		 public int UL_Guarante_bit_Rate;
		 public int DL_Guarante_bit_Rate;
		 public int Max_SDU_size;
		 
		 public int IP;

		public int[] integers = null;
		
		
		public PDPInfoDataV2(byte[] byteDataset){
						
			integers = new int[ byteDataset.length / 4 ];
			for(int i = 0,b = 0; b < byteDataset.length; b = b + 4,i++){
				integers[i] = getInt(byteDataset,b);
			}
			
			if( integers.length>19 ){
				int startPoint = 4;
				Delay_Class 			= integers[++startPoint];
				Reliability_Class 		= integers[++startPoint];
				Peak_Throughput 		= integers[++startPoint];
				Precedence_Class 		= integers[++startPoint];
				Mean_Throughput 		= integers[++startPoint];
				Radio_Priority 			= integers[++startPoint];
				LLC_SAPI 				= integers[++startPoint];
				UL_Max_bit_Rate 		= integers[++startPoint];
				DL_Max_bit_Rate 		= integers[++startPoint];
				UL_Guarante_bit_Rate 	= integers[++startPoint];
				DL_Guarante_bit_Rate 	= integers[++startPoint];
				Max_SDU_size 			= integers[++startPoint];
				
				IP 						= integers[18];
			}
			
		}
	}
	
	/**
	 *  信道
	 * @author Kong
	 */
	public class ChannelInfo implements ParaStruct{
		
		/**
		 * 无效的类型
		 */
		public static final int INVALID_TYPE = -9999;
		public static final int TYPE_PDSCH = 2;
		public static final int TYPE_SCCH = 4;
		public static final int TYPE_SICH = 5;
		
		public int timeSlot ;
		public int direction;
		public int workfrequency;
		public int sf;
		public int codeNumber;
		public int channelType = INVALID_TYPE ;
		
		/**
		 * codeNO_count的值处于结构体里位置
		 */
		public int codeNO_point;
		public int codeNO_count;
		public int[] codeNO;
		
		public ChannelInfo(int[] intArray,int codeNoPoint) {
			this.codeNO_point = codeNoPoint;
			try{
				timeSlot = intArray[1];
				direction = intArray[2];
				workfrequency = intArray[3];
				sf = intArray[4];
				codeNumber = intArray[5];
				channelType = intArray[6];
				
				codeNO_count = intArray[codeNoPoint];
				codeNO = new int[ codeNO_count ];
				for (int i = 1; i <= codeNO_count; i++) {
					codeNO[i - 1] = intArray[codeNoPoint + i];
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		/**
		 * @return 此结构体 包含的 整形 int的数量 = codeNO_point + 1 + codeNO_count
		 */
		public int getIntegerCount(){
			return codeNO_point + 1 + codeNO_count;
		}
	}
	
	/**
	 * 	0d 0e 06 7f
		03 00 00 00
		00 00 00 00
		00 00 00 00
		01 00 00 00
		00 00 00 00
		00 00 00 00
		64 00 00 00
		31 30 2e 32
		30 30 2e 31
		36 30 2e 38
		31 00 00 00
		00 00 00 00
		00 00 00 00
		00 00 00 00
		00 00 00 00
		00 00 00 00
		00 00 00 00
		00 00 00 00
		00 00 00 00
		00 00 00 00
		00 00 00 00
		00 00 00 00
		00 00 00 00
		00 00 00 00
		00 00 00 00
		00 00 00 00
		00 00 00 00
		00 00 00 00
		00 00 00 00
		00 00 00 00
		00 00 00 00
		00 00 00 00
	 * @author tangwq
	 *
	 */
	public class LTEEPSBearerContext02C1 implements ParaStruct{
		public final static int FLAG = UnifyParaID.LTE_EPS_BearerContext_02C1;
		
		public String pndAddress = "";
		public LTEEPSBearerContext02C1(byte[] byteDataset){
			if(byteDataset.length > 32){
				pndAddress = new String(subArray(byteDataset, 32, byteDataset.length - 1));
			}
		}
	}
	
	/**
	 * 	14 0e 06 7f		Flag	0x7F060E14
		00 00 00 00		IntCount	
		00 00 00 00		64bitIntCount
		00 00 00 00		Double Count
		16 00 00 00		Count
		33 67 6e 65		由Count个字符组成的字符串
		74 4d 4e 43
		30 30 31 4d
		43 43 34 36
		30 47 50 52
		53 00 
	 * @author tangwq
	 *
	 */
	public class LTEAPN implements ParaStruct{
		public final static int FLAG = UnifyParaID.LTE_APN;
		
		public String lteApn = "";
		public LTEAPN(byte[] byteDataset){
			if(byteDataset.length > 20){
				lteApn = new String(subArray(byteDataset, 20, byteDataset.length - 1));
			}
		}
	}

	public class LTENB implements ParaStruct{
		public final static int FLAG = UnifyParaID.LTE_NB_FORMAT;

		public String lteNb = "";
		public LTENB(byte[] byteDataset){
			if(byteDataset.length > 20){
				lteNb = new String(subArray(byteDataset, 20, byteDataset.length - 1));
			}
		}
	}

	public class LTE_GSM implements ParaStruct{
		public final static int FLAG = UnifyParaID.LTE_GSM_CELL_LIST;
		private ArrayList<LteGsmStructModel> modelList = new ArrayList<>();
		public String lteGsm = "";
		public LTE_GSM(byte[] byteDataset){
			if(byteDataset.length > 4){
				int num = ByteUitils.getInt(byteDataset,0);
				LogUtil.i(TAG,"num: " + num);
				int currIndex = 4;
				for (;currIndex < byteDataset.length;) {
					int arfcn = ByteUitils.getInt(byteDataset,currIndex);
					currIndex += 4;
					byte gsmBand = byteDataset[currIndex];
					currIndex += 1;
					short lnaState = ByteUitils.getShort(byteDataset,currIndex);
					currIndex += 2;
					float rssi = ByteUitils.getFloat(byteDataset,currIndex);
					currIndex += 4;
					float srxLev = ByteUitils.getFloat(byteDataset,currIndex);
					currIndex += 4;
					LteGsmStructModel model = new LteGsmStructModel(arfcn,gsmBand,lnaState,rssi,srxLev);
					modelList.add(model);
				}
				lteGsm = new Gson().toJson(modelList);
			}
		}
	}
	/**
	 * 5G 邻区列表
	 *
	 */
//	public class ENDC_CELL_LIST implements ParaStruct{
//		public final static int FLAG = UnifyParaID.ENDC_CELL_LIST;
//		public int CarrierIndex; //0: PCell 1-16: SCell1 - SCell16
//		public int CellType;  //0: Server_PCell 1 Server_SCell  2: Neighbor 3:Detected
//		public int NRARFCN;
//		public int PCI;
//		public int BeamCount;
//		public ArrayList<T5GSingleBeamInfo> modelList = new ArrayList<>();
//		public ENDC_CELL_LIST(byte[] byteDataset){
//			if(byteDataset.length > 4){
//				int currIndex = 0;
//				CarrierIndex= ByteUitils.getInt(byteDataset,currIndex);
//				currIndex += 4;
//				CellType= ByteUitils.getInt(byteDataset,currIndex);
//				currIndex += 4;
//				NRARFCN= ByteUitils.getInt(byteDataset,currIndex);
//				currIndex += 4;
//				PCI= ByteUitils.getInt(byteDataset,currIndex);
//				currIndex += 4;
//				BeamCount= ByteUitils.getInt(byteDataset,currIndex);
//				currIndex += 4;
//				int size=0;
//				for (;currIndex < byteDataset.length;) {
//					int SSBIndex = ByteUitils.getInt(byteDataset,currIndex);
//					currIndex += 4;
//					boolean servingBeam = byteDataset[currIndex]==0x00?false:true;
//					currIndex += 1;
//					int rxBeamID = ByteUitils.getInt(byteDataset,currIndex);
//					currIndex += 4;
//					int cellTiming = ByteUitils.getInt(byteDataset,currIndex);
//					currIndex += 4;
//					float RSRP = ByteUitils.getFloat(byteDataset,currIndex);
//					currIndex += 4;
//					float RSRQ = ByteUitils.getFloat(byteDataset,currIndex);
//					currIndex += 4;
//					float SINR = ByteUitils.getFloat(byteDataset,currIndex);
//					currIndex += 4;
//					T5GSingleBeamInfo model = new T5GSingleBeamInfo(SSBIndex, servingBeam, rxBeamID, cellTiming, RSRP, RSRQ, SINR);
//					modelList.add(model);
//					size+=1;
//					if(size>=BeamCount)
//						break;
//				}
//			}
//		}
//	}
	/**
	 * 5G 邻区列表
	 *
	 */
	public class ENDC_CELL_LIST implements ParaStruct{
		public final static int FLAG = UnifyParaID.ENDC_CELL_LIST;
		public ArrayList<T5GNRCellBlock> modelList = new ArrayList<>();
		public ENDC_CELL_LIST(byte[] byteDataset){
			if(byteDataset.length > 4){
				int currIndex = 0;
				int num = ByteUitils.getInt(byteDataset,currIndex);
				LogUtil.i(TAG,"num: " + num);
				currIndex += 4;
				for(int i=0;i<num;i++) {
					T5GNRCellBlock t5GNRCellBlock = new T5GNRCellBlock();
					t5GNRCellBlock.CarrierIndex = ByteUitils.getInt(byteDataset, currIndex);
					currIndex += 4;
					t5GNRCellBlock.CellType = ByteUitils.getInt(byteDataset, currIndex);
					currIndex += 4;
					t5GNRCellBlock.NRARFCN = ByteUitils.getInt(byteDataset, currIndex);
					currIndex += 4;
					t5GNRCellBlock.PCI = ByteUitils.getInt(byteDataset, currIndex);
					currIndex += 4;
					t5GNRCellBlock.BeamCount = ByteUitils.getInt(byteDataset, currIndex);
					if(t5GNRCellBlock.BeamCount>8){
						break;
					}
					currIndex += 4;
					int size = 0;
					for (; currIndex < byteDataset.length; ) {
						int SSBIndex = ByteUitils.getInt(byteDataset, currIndex);
						currIndex += 4;
						boolean servingBeam = byteDataset[currIndex] == 0x00 ? false : true;
						currIndex += 1;
						int rxBeamID = ByteUitils.getInt(byteDataset, currIndex);
						currIndex += 4;
						int cellTiming = ByteUitils.getInt(byteDataset, currIndex);
						currIndex += 4;
						float RSRP = ByteUitils.getFloat(byteDataset, currIndex);
						currIndex += 4;
						float RSRQ = ByteUitils.getFloat(byteDataset, currIndex);
						currIndex += 4;
						float SINR = ByteUitils.getFloat(byteDataset, currIndex);
						if(SINR==(int)SINR){
							SINR=(int)SINR;
						}
						currIndex += 4;
						T5GSingleBeamInfo model = new T5GSingleBeamInfo(SSBIndex, servingBeam, rxBeamID, cellTiming, RSRP, RSRQ, SINR);
						t5GNRCellBlock.modelList.add(model);
						size += 1;
						if(size >= t5GNRCellBlock.BeamCount)
							break;
					}
					modelList.add(t5GNRCellBlock);
				}
			}
		}
	}

	public class Channel implements ParaStruct{
		 private int ChannelID;
		 private int ChannelType; 	//0: DCH 1:RACH
		 private int CodingRate;  	//0: No coding 1:1/2 And Convolutional 2; 1/3 And Convolutional 3: 1/3 And Turbo 
		 private int CRCBits;		//bits
		 private int TTIFormat;   	//ms
		public int getChannelID() {
			return ChannelID;
		}
		public void setChannelID(int channelID) {
			ChannelID = channelID;
		}
		public int getChannelType() {
			return ChannelType;
		}
		public void setChannelType(int channelType) {
			ChannelType = channelType;
		}
		public int getCodingRate() {
			return CodingRate;
		}
		public void setCodingRate(int codingRate) {
			CodingRate = codingRate;
		}
		public int getCRCBits() {
			return CRCBits;
		}
		public void setCRCBits(int cRCBits) {
			CRCBits = cRCBits;
		}
		public int getTTIFormat() {
			return TTIFormat;
		}
		public void setTTIFormat(int tTIFormat) {
			TTIFormat = tTIFormat;
		}
		 
		 
	}
	
	public class WCDMATrCHDLConfiguration implements ParaStruct{
		public int channelLength = 0;
		public ArrayList<Channel> channels = new ArrayList<UnifyStruct.Channel>();
		Channel channel = null;
		
		public WCDMATrCHDLConfiguration(byte[] byteDataSet){
			channelLength = byteDataSet[0];
			for(int i = 1 ; i < byteDataSet.length; i = i + 5){
				channel = new Channel();
				channel.ChannelID = byteDataSet[i];
				channel.ChannelType = byteDataSet[i+1];
				channel.CodingRate = byteDataSet[i+2];
				channel.CRCBits = byteDataSet[i+3];
				channel.TTIFormat = byteDataSet[i+4];
				
				channels.add(channel);
			}
		}
	}
	
	/**
	 * 根据数据集返回的字节数组创建TDPhysChanInfo对象
	 * @param byteDataset
	 * @return 生成的结构体，传入值不正确时，返回null
	 */
	public  ParaStruct BuildStruct(byte[] byteDataset){
		ParaStruct result = null;
		
		if( byteDataset.length > 0 ){
			//第一个字节为flag
			int flag = getInt(byteDataset,0);
			
			switch(flag){
			case FLAG_TD_TDPhysChannelInfoDataV2:
				TDPhysChannelInfoDataV2 chanInfoV2 = new TDPhysChannelInfoDataV2(byteDataset);
				result = chanInfoV2;
				break;
			case FLAG_TD_HSDPAPhysChannelInfoData:
				HSDPAPhysChannelInfoData chanInfoHsdpa = new HSDPAPhysChannelInfoData(byteDataset);
				result = chanInfoHsdpa;
				break;
			case FLAG_TD_Activate_PDP_Context_Accept_Win_Data:
				PDPInfoDataV2 pdpInfoV2 = new PDPInfoDataV2(byteDataset);
				result = pdpInfoV2;
				break;
			case UnifyParaID.LTE_EPS_BearerContext_02C1:
				LTEEPSBearerContext02C1 pdnAddress = new LTEEPSBearerContext02C1(byteDataset);
				result = pdnAddress;
				break;
			case UnifyParaID.LTE_APN:
				LTEAPN lteAPn = new LTEAPN(byteDataset);
				result = lteAPn;
				break;
				case UnifyParaID.LTE_NB_FORMAT:
					LTENB ltenb = new LTENB(byteDataset);
					result = ltenb;
					break;
			}
		}
		return result;
	}

	public  ParaStruct BuildGsmStruct(byte[] byteDataset){
		ParaStruct result = null;
		if( byteDataset.length > 0 ){
			LTE_GSM lteGsm = new LTE_GSM(byteDataset);
			result = lteGsm;
		}
		return result;
	}
	public  ParaStruct BuildENDCStruct(byte[] byteDataset){
		ParaStruct result = null;
		if( byteDataset.length > 0 ){
			ENDC_CELL_LIST lteGsm = new ENDC_CELL_LIST(byteDataset);
			result = lteGsm;
		}
		return result;
	}
	public ParaStruct BuildStruct(int id,byte[] byteDateset){
		ParaStruct result = null;
		
		if(byteDateset.length > 0){
			switch(id){
			case FLAG_WCDMA_TrCH_DL_Configuration:
				WCDMATrCHDLConfiguration configGuration = new WCDMATrCHDLConfiguration(byteDateset);
				result = configGuration;
				break;
			case FLAG_WCDMA_RLC_DL_Entities:
				break;
			case FLAG_WCDMA_RLC_UL_Entities:
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * 函数功能：字节数组转为int数组，全部成员是int的结构体才用到
	 * @param byteDataset
	 */
	private int[] byteToIntArray(byte[] byteDataset){
		int[] integers = new int[ byteDataset.length/4 ];
		for(int i=0,b=0;b<byteDataset.length && i<integers.length;b=b+4,i++){
			integers[i] = getInt(byteDataset,b);
		}
		return integers;
	}


	/** 
     * 通过byte数组取到int 
     * @param bb 
     * @param index 第几位开始 
     * @return 
     */  
    private int getInt(byte[] bb, int index) {  
        return (int) ((((bb[index + 3] & 0xff) << 24)  
                | ((bb[index + 2] & 0xff) << 16)  
                | ((bb[index + 1] & 0xff) << 8) | ((bb[index + 0] & 0xff) << 0)));  
    } 
    
    /**
     * 包含from 和to的子数组
     * @param byteArray
     * @param from
     * @param to
     * @return
     */
    private byte[] subArray(byte[] byteArray,int from ,int to){
    	byte[] result = new byte[to-from+1];
    	
    	int r=0;
    	for(int i=from;i<=to;i++){
    		result[r] = byteArray[i];
			r++;
    	}
    	
    	return result;
    }
    
    /**
     * 包含from 和to的子数组
     * @param byteArray
     * @param from
     * @param to
     * @return
     */
    private int[] subArray(int[] byteArray,int from ,int to){
    	int[] result = new int[to-from+1];
    	
    	int r=0;
    	for(int i=from;i<=to;i++){
    		result[r] = byteArray[i];
    		r++;
    	}
    	
    	return result;
    }
    
	private static void printBytes(byte[] showByte){
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < showByte.length; i++){
			
			sb.append(";" + i + ":" + Integer.toHexString(showByte[i]));
		}
		LogUtil.w(TAG, "--Bytes:" + sb.toString());
	}
	
	private static void printInts(int[] ints){
		StringBuffer sb = new StringBuffer();
		for(int i=0; i< ints.length; i++){
			sb.append(";" + i + ":" + Integer.toHexString(ints[i]));
		}
		LogUtil.w(TAG, "--ints:" + sb.toString());
	}
}
