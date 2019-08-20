package com.walktour.Utils;

import android.content.Context;
import android.content.Intent;

import com.walktour.Utils.TotalStruct.TotalEvent;
import com.walktour.base.util.SDCardUtil;
import com.walktour.control.config.ConfigKpi;
import com.walktour.control.instance.FileDB;
import com.walktour.model.KpiResultModel;
import com.walktour.model.KpiSettingModel;
import com.walktour.model.TotalMeasureModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 统计数据统一接口，初始化业务统计结果，参数统计结果，测试参数，事件参数等
 * 将测试结果写入数据库，从数据库中获得指定的数据存入相应的存储结果中
 * @author tangwq
 *
 */
public abstract class TotalDataInterface {
//private static final String TAG = "TotalDataInterface";
	public static String TotalTaskDataChanged = "com.walktour.TotalTaskDataChanged";
	public static String TotalEventDataChanged = "com.walktour.TotalEventDataChanged";
	public static String TotalParaDataChanged = "com.walktour.TotalParaDataChanged";
	public static String TotalResultToPicture = "com.walktour.TotalResultToPicture";
	public static String TotalSaveFilePath = "TotalSaveFilePath";
	public static WalkStruct.ShowTotalType currentTotal = WalkStruct.ShowTotalType.Default;

	protected final int invalidValue = -9999;
	protected HashMap<String,Long> hmUnifyTimes;
	protected Map<String, Map<String, Map<String, Long>>> hmSpecialTimes;
	protected HashMap<String,Long> hmPara;
	protected HashMap<String,TotalMeasureModel> hmMeasure;
	protected HashMap<String, Long> hmEvent;
	protected ArrayList<KpiResultModel> kpiResultModels;
	/**
	 * 测试开始时，初始化统计信息，以该时间为测试开始时间，用于计划小区重选间隔时间整个测试过程的时间
	 */
	protected long testStartTimes = 0;

	/***
	 * 将测试过程中与网络无关的业务测试通过该方法存放到次数测试表示
	 * @param hash
	 */
	public abstract void updateTotalUnifyTimes(Context context,HashMap<?,?> hash);
	/**
	 * 将测试过程中网络质量相关的参数信息
	 * @param itData
	 */
	public abstract void updateTotalPara(Iterator<?> itData);
	/**
	 * 测试过程中的相关测量参数存储对象
	 * @param itData
	 */
	public abstract void updateTotalMeasurePara(Iterator<?> itData);
	public abstract void updateTotalMeasurePara(String key,int value);
	
	/**
	 * 测试地程中的相关参数存储
	 * @param itData
	 */
	public abstract void updateTotalEvent(Iterator<?> itData);
	
	/**
	 * 根据查询条件在数据库中获取各相关的统计信息
	 * @param context
	 * @param wheres
	 */
	public void buildTotalDetailByHistory(Context context,String wheres){
		testStartTimes = 0;	//当从数据库中查询统计结果时，将开始测试时间置为0
		
		hmUnifyTimes = FileDB.getInstance(context).getTotalUnifyTimes(wheres);
		hmSpecialTimes = FileDB.getInstance(context).getTotalSpecialTimes(wheres);
		hmPara = FileDB.getInstance(context).getTotalPara(wheres);
		hmMeasure = FileDB.getInstance(context).getTotalMeasurePara(wheres);
		hmEvent = FileDB.getInstance(context).getTotalEvent(wheres);
		
		Intent totalDataChange = new Intent(TotalDataByGSM.TotalTaskDataChanged);
		context.sendBroadcast(totalDataChange);
	}
	
	/**
	 * 将测试结果信息添加到相应的数据库中
	 * @param context
	 * @param mainId
	 * @param testType
	 */
	public void InsertTotalDetailToDB(Context context, int mainId,int testType){
		FileDB.getInstance(context).insertTotalUnifyTimes(mainId, testType,hmUnifyTimes.entrySet().iterator());
		FileDB.getInstance(context).insertTotalSpecialTimes(mainId, testType, hmSpecialTimes.entrySet().iterator());
		FileDB.getInstance(context).insertTotalPara(mainId, testType, hmPara.entrySet().iterator());
		FileDB.getInstance(context).insertTotalMeasurePara(mainId, testType, hmMeasure.entrySet().iterator());
		
		//测试结束将相关统计参数添加到数据库中时，将当前时间-开始测试时间 /1000作为测试总时长
		long testLongTimes = ((testStartTimes == 0 ? 0 : System.currentTimeMillis() - testStartTimes) /1000);
		hmEvent.put(TotalEvent._testTimeLong.name(), testLongTimes);
		FileDB.getInstance(context).insertTotalEvent(mainId, testType,hmEvent.entrySet().iterator());
	}
	/**
	 * 初始化统计表信息
	 */
	public void initTotalDetail(){
		testStartTimes = System.currentTimeMillis();
		hmUnifyTimes = new HashMap<>();
		hmSpecialTimes = new LinkedHashMap<>();
		hmPara = new HashMap<>();
		hmMeasure = new HashMap<>();
		hmEvent = new HashMap<>();
	}
	
	/**
	 * 返回业务测试次数的相关信息
	 * @return
	 */
	public HashMap<String, Long> getUnifyTimes(){
		return hmUnifyTimes == null ? new HashMap<String, Long>() : hmUnifyTimes;
	}
	
	/**
	 * 
	 * [获得HTTP，PING,自定义事件等特别统计业务的统计对象]<BR>
	 * [功能详细描述]
	 * @return
	 */
	public Map<String, Map<String, Map<String, Long>>> getSpecialTimes(){
	    return hmSpecialTimes == null ? new LinkedHashMap<String, Map<String, Map<String, Long>>>() : hmSpecialTimes;
	}
	/**
	 * 返回网络质量的相关参数信息
	 * @return
	 */
	public HashMap<String, Long> getPara(){
		return hmPara == null ? new HashMap<String, Long>() : hmPara;
	}
	
	/**
	 * 返回网络测量参数信息
	 * @return
	 */
	public HashMap<String, TotalMeasureModel> getMeasuePara(){
		return hmMeasure == null ? new HashMap<String, TotalMeasureModel>() : hmMeasure;
	}
	
	/**
	 * 返回事件统计结果
	 * @return
	 */
	public HashMap<String, Long> getEvent(){
		//当前为测试过程中获取事件值，此时需更新小区重选时间为测试开始到当前的时间差
		if((ApplicationModel.getInstance().isTestJobIsRun() || ApplicationModel.getInstance().isTesting())
				&& testStartTimes != 0 && hmEvent != null ){
			long testLongTimes = ((System.currentTimeMillis() - testStartTimes) /1000);
			hmEvent.put(TotalEvent._testTimeLong.name(), testLongTimes);
		}
		return hmEvent == null ? new HashMap<String, Long>() : hmEvent;
	}
	
	/**
	 * 从两个Hash表中获得同一个关键字的值，并返回值的和
	 * 
	 * @param hMap1
	 * @param hMap2
	 * @param key
	 * @return
	 */
	public static long getHashMapValueSum(HashMap<String, Long> hMap1,HashMap<String, Long> hMap2,String key){
       long value1 = getHashMapValue(hMap1, key).equals("")?0:Long.valueOf(getHashMapValue(hMap1, key));
       long value2 = getHashMapValue(hMap2, key).equals("")?0:Long.valueOf(getHashMapValue(hMap2, key));
       
       return (value1 + value2);
    }
   
	/**
	 * 返回HashMap中指定分子，分母的百分比
	 * @param hMap
	 * @param molecule 分子
	 * @param denominator 分母
	 * @param multiple 放大倍数
	 * @param units 单位
	 * @return
	 */
	public static String getHashMapMultiple(HashMap<String, Long> hMap,String molecule,String denominator,float multiple,String units){
		return getHashMapMultiple(hMap,molecule,denominator,multiple,units,-9999);
	}
	
	/**
	 * 从两个Hash表中，获得同一个关键的的分子和，分母和，并返回和的比例
	 * 
	 * @param hMap1
	 * @param hMap2
	 * @param molecule
	 * @param denominator
	 * @param multiple
	 * @param units
	 * @return
	 */
	public static String getHashMapMultipleSum(HashMap<String, Long> hMap1,HashMap<String, Long> hMap2,String molecule,String denominator,float multiple,String units){
		long iMolecule = getHashMapValueSum(hMap1,hMap2,molecule);
        long iDenominator = getHashMapValueSum(hMap1,hMap2,denominator);
        
        return getIntMultiple(iMolecule,iDenominator,multiple,units);
	}
	
	/**
	 * 返回HashMap中指定分子，分母的百分比
	 * @param hMap
	 * @param molecule 分子
	 * @param denominator 分母
	 * @param multiple 放大倍数
	 * @param units 单位
	 * @param maxResult 最大返回值
	 * 最大返回值与单位不能同时有效，否则...
	 * @return
	 */
	public static String getHashMapMultiple(HashMap<String, Long> hMap,String molecule,String denominator,float multiple,String units,float maxResult){
		String result="";
		try{
			if(hMap.get(molecule) == null && hMap.get(denominator) == null){
				return result;
			}
			//if(hMap.get(molecule) != null && hMap.get(denominator) != null){
			result = getIntMultiple(hMap.get(molecule) == null ? 0 : hMap.get(molecule) * 1f,
									hMap.get(denominator) == null ? 1 : hMap.get(denominator),
					multiple,units);
			//LogUtil.w(tag,"--molecule:" + hMap.get(molecule) + "--denominator:" + hMap.get(denominator) + "--result:" + result);
			if(maxResult != -9999 && units.equals("") && !result.equals("")
					&& Float.parseFloat(result) > maxResult) {
				//LogUtil.w(tag,"---getHashMapMultiple the result:"+result+" > maxResult:"+maxResult);
				result = String.valueOf(maxResult);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	/**加法操作*/
	public static final String OPT_ADD = "+";
	/**减法操作*/
	public static final String OPT_SUB = "-";
	/**乘法操作*/
	public static final String OPT_MUL = "*";
	/**除法操作*/
	public static final String OPT_DIV = "%";
	
	/**
	 * 获得HashMap中指定的测量参数模型
	 * @param hMap
	 * @param key
	 * @return
	 */
	public static TotalMeasureModel getHashMapMeasure(HashMap<String, TotalMeasureModel> hMap,String key){
		try{
			return hMap.get(key)==null ? new TotalMeasureModel() : hMap.get(key);
		}catch(Exception e){
			e.printStackTrace();
			return new TotalMeasureModel();
		}
	}
	/**
	 * 返回指定分子分母的百分比占有率
	 * @param molecule 分子
	 * @param denominator 分母
	 * @param multiple 放大倍数
	 * @param units 单位
	 * @return
	 */
	public static String getIntMultiple(float molecule,float denominator,float multiple,String units){
		try{
			if(molecule == -9999){
				return "";
			}
			float result = molecule / (denominator == 0 ? 1 : denominator) * multiple;
			//如果当前运算为求%比运算且运算值大于100时将运算结果置为100
			if(units.equals("%") && result > 100 ){
				//LogUtil.w(tag,"---molecule:"+molecule+"--denominator:"+denominator+"--result:"+result+"--"+units);
				result = 100;
			}
			return UtilsMethod.decFormat.format(result) +units;
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 从Hash表中获得指参数的结果，并处理放大倍数
	 * 返回结果保留两位小数
	 * 
	 * @param hMap
	 * @param key
	 * @param multiple 放大倍数
	 * @return
	 */
	public static String getHashMapValue(HashMap<String, Long> hMap,String key,float multiple){
		if(hMap != null && hMap.containsKey(key)){
			return UtilsMethod.decFormat.format(hMap.get(key) / multiple);
		}
		
		return "";
	}
	/**
	 * 返回HashMap中指定关键字的值，如果指定关键不存在返回空
	 * @param hMap
	 * @param key
	 * @return
	 */
	public static String getHashMapValue(Map<String, Long> hMap,String key){
		if(hMap != null && hMap.containsKey(key)){
			return "" + hMap.get(key);
		}
		return "";
	}
	
	/**
	 * 从Hashmap中取值，返回Long形结果
	 * 
	 * @param hMap
	 * @param key
	 * @return
	 */
	public static long getHashMapVal(HashMap<String, Long> hMap, String key){
		if(hMap != null && hMap.containsKey(key)){
			return hMap.get(key);
		}
		return 0;
	}
	
	/**
	 * 返回指定参数缩小指定倍数后的值
	 * 
	 * @param value
	 * @param multiple
	 * @return
	 */
	public static String getValueByMultiple(long value,float multiple){
		if(value == -9999){
			return "";
		}
		return UtilsMethod.decFormat.format(value / multiple);
	}
	
	/**
	 * 返回指定参数放大指定倍数后的值
	 * 
	 * @param value
	 * @param scale
	 * @return
	 */
	public static String getValueByScale(long value,float scale){
		if(value == -9999){
			return "";
		}
		return UtilsMethod.decFormat.format(value * scale);
	}
	
	/**
	 * 计算内存中的统计结果是否符合设置中的Go or Nogo指标,并将结果存储供界面使用
	 * 
	 * @param context
	 * @return
	 */
	public boolean getGoOrNogoReport(Context context){
		boolean isAllGo = true;
		kpiResultModels = new ArrayList<KpiResultModel>();
		
		ArrayList<KpiSettingModel> models = ConfigKpi.getInstance().getKpiModelList();
		for(int i=0; i < models.size(); i++){
			if(models.get(i).getEnable() == 1){
				KpiResultModel kpiModel = isTestKpiScratch(models.get(i),context);
				if(kpiModel != null){
					kpiResultModels.add(kpiModel);
					if(!kpiModel.isScratch()){
						isAllGo = false;
					}
				}
			}
		}
		
		return isAllGo;
	}
	
	/**
	 * 返回Go or Nogo结果保存信息
	 * @return
	 */
	public ArrayList<KpiResultModel> getGoOrNogoResult(Context context){
		if(kpiResultModels == null){
			getGoOrNogoReport(context);
		}
		
		return kpiResultModels;
	}
	
	/**
	 * 判断传进来的指定关键字运行算结果，是否达到指定的指标值
	 *
	 * @param kpiModel
	 * @param context
	 * @return
	 */
	public static KpiResultModel isTestKpiScratch(KpiSettingModel kpiModel ,Context context){
		KpiResultModel kpiResult = new KpiResultModel();
		
		kpiResult.setActualValue(kpiModel.getOperator() + kpiModel.getValue()+ kpiModel.getUnits());
		kpiResult.setShowKpiName( kpiModel.getGroupby()+ "\n" +
				UtilsMethod.getStringsByFieldName(context, kpiModel.getKpiShowName()));
		
		String kpiValue = getHashMapMultiple(TotalDataByGSM.getInstance().getUnifyTimes(),
				kpiModel.getMolecule(),kpiModel.getDenominator(),kpiModel.getScale(),"");
		kpiResult.setKpiRealValue(kpiValue);
		float  kpiVs = -9999;
		try{
			if(!kpiValue.equals("")){
				kpiVs = Float.parseFloat(kpiValue);
			}else{
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			return kpiResult;
		}
		
		kpiResult.setScratch(compareResult(kpiVs,kpiModel.getValue(),kpiModel.getOperator()));
		
		//LogUtil.w(tag,"--name:" + kpiResult.getShowKpiName() + "--" + kpiResult.getActualValue() + "--" + kpiResult.getKpiRealValue());
		return kpiResult;
	}
	
	/**
	 * 比较源值与目标值的大小情况
	 * 
	 * @param source
	 * @param target
	 * @param operator
	 * @return
	 */
	private static boolean compareResult(float source,float target,String operator){
		//LogUtil.w(tag,"--soruce:" + source + "--targe:" + target + "--oper:" + operator);
		if(source == -9999){
			return false;
		}else if(operator.equals("&gt;=") || operator.equals(">=")){
			return source >= target;
		}else if(operator.equals("&lt;=") || operator.equals("<=")){
			return source <= target;
		}else if(operator.equals("&gt;") || operator.equals(">")){
			return source > target;
		}else if(operator.equals("&lt;") || operator.equals("<")){
			return source < target;
		}else if(operator.equals("=")){
			return source == target;
		}else{
			return false;
		}
	}
}
