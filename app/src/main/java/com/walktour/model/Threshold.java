package com.walktour.model;

import android.graphics.Color;

import com.walktour.base.util.LogUtil;

/**
 * @class　阈值类
 * @descrition 一个实例表示一个阈值，
 * 
 * */
public class Threshold {
		int color;
		
		private final String TAG = "Threshold";
		private final String NoRangeFormat		= "%s%s";		//非区间格式化
		private final String RangeFormat 		= "%s%s,%s%s";	//区间格式化
		private final String NoRangeFormatShow	= "X %s %s";		//非区间格式化
		private final String RangeFormatShow	= "%s %s X %s %s";	//区间格式化
		
		private int scale = 1;//数据放到倍数
		
		public static enum SignType{
			LESSEQUALA	("<=","<=",">="),
			LESS		("<","<",">"),
			GREATEEQUALS(">=",">=","<="),
			GREATE		(">",">","<"),
			LEFTCLOSE	("[",">=","<="),
			LEFTOPEN	("(",">","<"),
			RIGHTCLOSE	("]","<=",">="),
			RIGHTOPEN	(")","<",">"),
			UNKNOW		("","","");
			
			private String signStr;
			private String sign;
			private String signChange2Left;
			private SignType(String str,String sign,String change2Left){
				this.signStr		= str;
				this.sign = sign;
				this.signChange2Left = change2Left;
			}
			
			public String getSignStr(){
				return signStr;
			}
			
			public String getSign(){
				return sign;
			}
			
			/**
			 * 当前符号放到左边去显示时的符号
			 * @return
			 */
			public String getSignChange2Left(){
				return signChange2Left;
			}
			
			public static SignType getSignWithStr(String str){
				SignType[] signS = SignType.values();
				for(SignType sign : signS){
					if(str.startsWith(sign.getSignStr()) || str.endsWith(sign.getSignStr())){
						return sign;
					}
				}
				
				return UNKNOW;
			}
		}
		
		/**当前段为范围值，表示在两个数据之间*/
		private boolean isRange = false;
		
		private SignType leftSign = SignType.UNKNOW;	//左阀值比较符号
		private int leftValue;	//左阀值大小
		private SignType rightSign= SignType.UNKNOW;	//右阀值比较符号
		private int rightValue;	//右阀值大小
		private String valueStr;
		
		/**
		 * @param value  阈值
		 * @param color  阈值对应的颜色
		 * */
		public Threshold(String valueStr,String colors){
			try{
				this.valueStr = valueStr;
				distributValues(this.valueStr);
				this.color = Color.parseColor(colors);
			}catch(Exception e){
				LogUtil.w(TAG, "new Threshold:" + valueStr,e);
			}
		}
		
		/**分解配置文件中的阀值信息*/
		private void distributValues(String valueStr){
			String[] values = valueStr.split(",");
			isRange = (values.length == 2);
			
			leftSign = SignType.getSignWithStr(values[0]);
			leftValue= Integer.parseInt(values[0].replace(leftSign.getSignStr(),""));
			
			if(isRange){
				rightSign = SignType.getSignWithStr(values[1]);
				rightValue= Integer.parseInt(values[1].replace(rightSign.getSignStr(), ""));
			}
			//LogUtil.w(TAG, "--toShow:" + getValue2Show());
		}
		
		/**
		 * 运算结果
		 * @param operationType	运算符类型
		 * @param basisValue	基准比较值
		 * @param value			传入进行运行的比较值
		 * @return
		 */
		private boolean operationResult(SignType operationType,double basisValue,double value){
			boolean result = false;
			switch(operationType){
			case LESS:
			case RIGHTOPEN:
				result = (value < basisValue);
				break;
			case LESSEQUALA:
			case RIGHTCLOSE:
				result = (value <= basisValue);
				break;
			case GREATE:
			case LEFTOPEN:
				result = (value > basisValue);
				break;
			case GREATEEQUALS:
			case LEFTCLOSE:
				result = (value >= basisValue);
				break;
				default:
					result = false;
					break;
			}
			
			return result;
		}
		
		/**
		 * 获得当前参数当前阀值的比较结果
		 * @param value	当前参数值
		 * @return
		 */
		public boolean getValueResult(double value){
			boolean result = false;
			
			result = operationResult(leftSign,leftValue,value);
			if(isRange){
				result = result && operationResult(rightSign,rightValue,value);
			}
			
			return result;
		}
		
		/**
		 * 获得当前参数的符号类型
		 * @return
		 */
		public SignType getSignType(){
			if(isRange){
				return rightSign;
			}
			return leftSign;
		}
		
		/**
		 * 获得当前分段阀值的整开明形值
		 * @return
		 */
		public int getValue(boolean isAscending){
			if(isRange && isAscending){
				return rightValue;
			}
			return leftValue;
		}
		
		/**返回写入xml的字串*/
		public String getValue2Write(){
			if(isRange){
				return String.format(RangeFormat, leftSign.getSignStr(),leftValue,rightValue,rightSign.getSignStr());
			}
			return String.format(NoRangeFormat, leftSign.getSignStr(),leftValue);
		}
		
		/**返回显示的值*/
		public String getValue2Show(){
			if(isRange){
				return String.format(RangeFormatShow, leftValue/this.scale,leftSign.getSignChange2Left(),rightSign.getSign(),rightValue/this.scale);
			}
			return String.format(NoRangeFormatShow,leftSign.getSign(),leftValue/this.scale);
		}
		
		/**
		 * 返回显示的值，地图时用
		 * @return
		 */
		public String getValue2ShowForMap() {
			if(isRange){
//				return String.format(RangeFormat, leftValue/this.scale,leftSign.getSignChange2Left(),rightSign.getSign(),rightValue/this.scale);
				return String.format(RangeFormat, leftSign.getSignStr(),leftValue/this.scale,rightValue/this.scale,rightSign.getSignStr());
			}
//				return String.format(NoRangeFormat,leftSign.getSign(),leftValue/this.scale);
			return String.format(NoRangeFormat, leftSign.getSignStr(),leftValue/this.scale);
		}
		
		public int getColor(){
			return this.color;
		}
		
		/**拼成写入xml的颜色串*/
		public String getColor2Write(){
			String colorStr = Integer.toHexString(color);
			colorStr = colorStr.substring(2);
			return "#" + colorStr;
		}
		
		/**
		 * 当分段阀值发生改变的时候，如果当前为区间段，修改
		 * @param value
		 */
		public void setValue(int value,boolean isAscending){
			if(isRange && isAscending){
				this.rightValue = value;
			}else{
				this.leftValue = value;
			}
		}
		
		/**
		 * 获得交换等于符号>与>=互换，<与<=互换
		 * @param signType
		 * @return
		 */
		private SignType getSwapsEquateSign(SignType signType){
			switch(signType){
			case LESS:
				return SignType.LESSEQUALA;
			case LESSEQUALA:
				return SignType.LESS;
			case LEFTOPEN:
				return SignType.LEFTCLOSE;
			case LEFTCLOSE:
				return SignType.LEFTOPEN;
			case GREATE:
				return SignType.GREATEEQUALS;
			case GREATEEQUALS:
				return SignType.GREATE;
			case RIGHTOPEN:
				return SignType.RIGHTCLOSE;
			case RIGHTCLOSE:
				return SignType.RIGHTOPEN;
			default:
				return SignType.UNKNOW;
			}
		}
		
		/**
		 * 改变当前阀值符号为>=或<=
		 * @param signType
		 */
		public void changeSign(){
			leftSign = getSwapsEquateSign(leftSign);
			if(isRange){
				rightSign = getSwapsEquateSign(rightSign);
			}
			changeRangeStr();
		}
		
		/**
		 * 改变阀值区间符合
		 * @author msi
		 */
		public void changeRangeStr() {
			valueStr = leftSign.getSignStr() + leftValue;
			if (isRange) {
				valueStr = leftSign.getSignStr() + leftValue + "," + rightValue + rightSign.getSignStr();
			}
		}
		
		/**
		 * 当阀值改变时，修改下段阀值显示值
		 * @param value
		 */
		public void setThreshold2Value(int value,boolean isAscending){
			if(!isRange || isAscending){
				this.leftValue = value;
			}else{
				this.rightValue = value;
			}
		}
		
		public void setColor(int value){
			this.color = value;
		}

		public String getValueStr() {
			return valueStr;
		}

		public void setValueStr(String valueStr) {
			this.valueStr = valueStr;
			distributValues(this.valueStr);
		}

		public int getScale() {
			return scale;
		}

		public void setScale(int scale) {
			this.scale = scale;
		}
	}