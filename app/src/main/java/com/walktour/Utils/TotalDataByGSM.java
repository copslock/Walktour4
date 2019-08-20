package com.walktour.Utils;

import android.content.Context;
import android.content.Intent;

import com.walktour.Utils.TotalStruct.TotalAppreciation;
import com.walktour.Utils.TotalStruct.TotalPPP;
import com.walktour.gui.task.parsedata.TaskTestObject;
import com.walktour.model.TotalMeasureModel;
import com.walktour.model.TotalSpecialModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TotalDataByGSM extends TotalDataInterface {
//	private static final String TAG ="TotalDataByGSM";
	private static TotalDataByGSM instance = null;

	public synchronized static TotalDataByGSM getInstance(){
		if(instance == null)
			instance = new TotalDataByGSM();
		return instance;
	}
	//初始化统计数据处理方法,读取统计任务,事件,参数已存储数据
	private TotalDataByGSM(){
		initTotalDetail();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTotalEvent(Iterator<?> itData) {
		try{
			while(itData.hasNext()){
				Entry<String,Integer> entry = (Entry<String,Integer>)itData.next();
				String key = entry.getKey();
				updateTotalEvent(key, entry.getValue());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 将指定值更新到事件表中
	 * @param key
	 * @param value
	 */
	public void updateTotalEvent(String key,int value){
		hmEvent.put(key, ((hmEvent.containsKey(key) ? hmEvent.get(key) : 0)) + value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTotalMeasurePara(Iterator<?> itData) {
		try{
			while(itData.hasNext()){
				Entry<String,Integer> entry = (Entry<String,Integer>)itData.next();
				String key = entry.getKey();
				int value = entry.getValue();
				updateTotalMeasurePara(key,value);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void updateTotalMeasurePara(String key, int value) {
		try{
			TotalMeasureModel para;
			if(hmMeasure.containsKey(key)){
				para = hmMeasure.get(key);
				if(value > para.getMaxValue()){
					para.setMaxValue(value);
				}else if(value < para.getMinValue()){
					para.setMinValue(value);
				}
				para.setKeyCounts(para.getKeyCounts() + 1);
				para.setKeySum(para.getKeySum() + value);
			}else{
				para = new TotalMeasureModel(value);
			}
			hmMeasure.put(key, para);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTotalPara(Iterator<?> itData) {
		try{
			while(itData.hasNext()){
				Entry<String,Long> entry = (Entry<String,Long>)itData.next();
				String key = entry.getKey();
				hmPara.put(key, ((hmPara.containsKey(key) ? hmPara.get(key) : 0)) + entry.getValue());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTotalUnifyTimes(Context context,HashMap<?,?> hash) {
		try{
		    List<?> list = new ArrayList<Object>(hash.entrySet());
			if(list.size() > 0){
			    Entry<String,?> model = (Entry<String,?>)list.get(0);
			    if(model.getValue() instanceof TotalSpecialModel){
			        toUpdateSpecial(list);
			    }else{

			    	//2013.5.23修改为可以同时接收Hash<String key,long value>和Hash<String key,Integer value>
			    	Iterator<?> iter = hash.entrySet().iterator();
			    	while( iter.hasNext() ){
			    		Entry<String, ?> entry = (Entry<String, ?>) iter.next();
			    		String key = entry.getKey();
			    		Object value = entry.getValue();
			    		if( value instanceof Long){
			    			Entry<String, Long> entryLong = ((Entry<String, Long>) entry);
			    			hmUnifyTimes.put(key, ((hmUnifyTimes.containsKey(key) ? hmUnifyTimes.get(key) : 0)) + entryLong.getValue() );
			    		}else if (value instanceof Integer){
			    			Entry<String, Integer> entryInt = (Entry<String, Integer>) entry;
			    			hmUnifyTimes.put(key, ((hmUnifyTimes.containsKey(key) ? hmUnifyTimes.get(key) : 0)) + entryInt.getValue() );
			    		}
			    	}

			        //toUpdateUnify(list);
			    }
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		//通知界面更新
		context.sendBroadcast(new Intent(TotalTaskDataChanged) );
	}

//	private void toUpdateUnify(List ls){
//	    //while(itData.hasNext()){
//	    for(int i=0;i<ls.size();i++){
//	        //Entry<String, Integer> entry = (Entry<String, Integer>)itData.next();
//	    	if(ls.get(i) instanceof Long){
//	    		Entry<String, Long> entry = (Entry<String, Long>)ls.get(i);
//	    		String key = entry.getKey();
//	    		//LogUtil.w(tag,"--key:" + key + "--value:" + entry.getValue());
//	    		hmUnifyTimes.put(key, ((hmUnifyTimes.containsKey(key) ? hmUnifyTimes.get(key) : 0)) + entry.getValue());
//	    	}else{
//	    		Entry<String, Integer> entry = (Entry<String, Integer>)ls.get(i);
//	    		String key = entry.getKey();
//	    		//LogUtil.w(tag,"--key:" + key + "--value:" + entry.getValue());
//	    		hmUnifyTimes.put(key, ((hmUnifyTimes.containsKey(key) ? hmUnifyTimes.get(key) : 0)) + entry.getValue());
//	    	}
//        }
//	}

	@SuppressWarnings("unchecked")
	private void toUpdateSpecial(List<?> ls){
		Map<String,Map<String,Long>> main2;
	    Map<String, Long> hMap;

	    TotalSpecialModel special = ((Entry<String, TotalSpecialModel>)ls.get(0)).getValue();
	    String keyMain1 = special.getMainKey1();
	    String keyMain2 = special.getMainKey2();
	    //LogUtil.w(tag,"---keyMain1:"+keyMain1+"--keyMain2:"+keyMain2);

	    if(hmSpecialTimes.containsKey(keyMain1)){
	        main2 = hmSpecialTimes.get(keyMain1);
	        if(main2.containsKey(keyMain2)){
	            hMap = main2.get(keyMain2);
	        }else{
	            hMap = new LinkedHashMap<>();
	        }
	    }else{
	        hMap = new LinkedHashMap<>();
	        main2 = new LinkedHashMap<>();
	    }

	    for(int i=0;i<ls.size();i++){
	        Entry<String, TotalSpecialModel> entry = (Entry<String, TotalSpecialModel>)ls.get(i);
            String key = entry.getKey();
            hMap.put(key, ((hMap.containsKey(key) ? hMap.get(key) : 0)) + entry.getValue().getKeyValue());

            //twq20131130 将ping,http业务的特殊结构，以不区特第一第二关键字的方法，往普通业务hash表中同时存一份结果
            hmUnifyTimes.put(key, ((hmUnifyTimes.containsKey(key) ? hmUnifyTimes.get(key) : 0)) + entry.getValue().getKeyValue());

            //20131116，此处理用于计划ping的最大最小时延,接口改动有点大，此处将就用吧
            if(key.equals(TotalAppreciation._pingDelay.name())){
            	//此处记录上次拨号时延,用于下进ping业务时的抖动计划,将就存这吧
            	TaskTestObject.PING_LAST_DELAY = (int)entry.getValue().getKeyValue();

            	if(hMap.containsKey(TotalAppreciation._pingDelayMin.name())){
            		if(entry.getValue().getKeyValue() < hMap.get(TotalAppreciation._pingDelayMin.name())){
            			hMap.put(TotalAppreciation._pingDelayMin.name(),entry.getValue().getKeyValue());
            		}
            	}else{
            		hMap.put(TotalAppreciation._pingDelayMin.name(),entry.getValue().getKeyValue());
            	}
            	if(hMap.containsKey(TotalAppreciation._pingDelayMax.name())){
            		if(entry.getValue().getKeyValue() > hMap.get(TotalAppreciation._pingDelayMax.name())){
            			hMap.put(TotalAppreciation._pingDelayMax.name(),entry.getValue().getKeyValue());
            		}
            	}else{
            		hMap.put(TotalAppreciation._pingDelayMax.name(),entry.getValue().getKeyValue());
            	}
            }
        }

	    main2.put(keyMain2, hMap);
	    hmSpecialTimes.put(keyMain1, main2);
	}

	/**
	 * 统计PPP
	 */
	void totalPPP(Context context,boolean success,int delay){
		//统计
		HashMap<String,Integer> map = new HashMap<>();
		if( success ){
			map.put( TotalPPP._pppRequest.name(), 1);
			map.put( TotalPPP._pppSuccess.name(), 1);
			map.put( TotalPPP._pppDelay.name(), delay );
		}else{
			map.put( TotalPPP._pppRequest.name(), 1);
		}
		if( !map.isEmpty() ){
			updateTotalUnifyTimes(context,map);
		}
	}
}
