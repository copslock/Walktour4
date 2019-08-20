package com.walktour.model;

import com.dinglicom.dataset.EventManager;
import com.walktour.control.VoiceAnalyseInterface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * CSFB通话异常时记录这次通话的相关信息
 * 包含相关网络信息，所有通话事件，通话信令，参数等
 * 
 * @author tangwq
 *
 */
public class VoiceFaildModel {

	//CSFB失败时间，从第一个事件节点得到
	private String 	faildTime 		= "--:--:--";
	//CSFB失败原因描述符
	private String	csfbFaildStr	= "";
	//CSFB失败原因详细描述符
	private String 	csfbFaildStrDec	= "";
	//CSFB失败第一个节点网络类型
	private String 	faildNetType	= "";
	//当前失败第一个事件对应采样点的当前网络相关参数结果,如:BCCH:38350@@BSIC:7@@LAC:-6.12dBm;显示时以@@符分割
	private String 	faildParamValues= "";
	//呼叫类型主叫（MO），被叫（MT）
	private String 	callType		= "";
	
	private Map<Integer, CsfbFaildEventModel> csfbEventMap = null;
	private List<CsfbFaildEventModel> csfbEventArray	= null;
	
	public VoiceFaildModel(int[][] csfbFixedEvents){
		//此处初始化固定存在的异常列表
		initCsfbEventList(csfbFixedEvents);
	}
	
	//按照设定好的事件列表初始化数据
	private void initCsfbEventList(int[][] csfbFixedEvents){
		csfbEventMap = new LinkedHashMap<Integer, CsfbFaildEventModel>();
		for(int i=0; i < csfbFixedEvents.length; i++){
			csfbEventMap.put(csfbFixedEvents[i][0], 
					new CsfbFaildEventModel(csfbFixedEvents[i][1],csfbFixedEvents[i][2], csfbFixedEvents[i][0],
							EventManager.getInstance().getEventStr(csfbFixedEvents[i][0])));
		}
	}

	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append("faildTime:").append(faildTime).append(";csfbFaildSt:").append(csfbFaildStr);
		str.append(";faildNetType:").append(faildNetType).append(";faildParamValues:").append(faildParamValues);
		str.append(";csfbFaildStrDec:").append(csfbFaildStrDec);
		int rows = 1;
		for (int key : csfbEventMap.keySet()) {
			str.append(";").append(rows++).append(":").append(csfbEventMap.get(key).toString());
		}
		return str.toString();
	}
	
	/**
	 * 移除事件列表中不显示的事件项，因界面处理隐藏时不好弄，故使用前调此方法清掉
	 */
	public void removeGoneItem(){
		Set<Integer> set = new HashSet<Integer>();
		for (int key : csfbEventMap.keySet()) {
			CsfbFaildEventModel model = csfbEventMap.get(key);
			if (model.getNodeShowType() == VoiceAnalyseInterface.NODE_SHOW_GONE) {
				set.add(key);
			}
		}
		for (int key : set) {
			csfbEventMap.remove(key);
		}
	}
	
	/**
	 * 构建CSFB ArrayList对象列表
	 * @return
	 */
	public void buildCSFBEventArray(){
		csfbEventArray = new ArrayList<CsfbFaildEventModel>();
		for(CsfbFaildEventModel model:csfbEventMap.values()){
			if (model.getNodeShowType() != VoiceAnalyseInterface.NODE_SHOW_GONE) {
				csfbEventArray.add(model);
			}
		}
	}
	
	/**
	 * 获得当前通话过程各事件节点的对象列表
	 * @return
	 */
	public List<CsfbFaildEventModel> getCsfbFaildEventArray(){
		return csfbEventArray;
	}
	
	/**CSFB失败时间，从第一个事件节点得到*/
	public String getFaildTime() {
		return faildTime;
	}

	public void setFaildTime(String faildTime) {
		this.faildTime = faildTime;
	}

	/**CSFB失败原因描述符*/
	public String getCsfbFaildStr() {
		return csfbFaildStr;
	}

	public void setCsfbFaildStr(String csfbFaildStr) {
		this.csfbFaildStr = csfbFaildStr;
	}
	
	/**CSFB失败原因详细描述符*/
	public String getCsfbFaildStrDec() {
		return csfbFaildStrDec;
	}

	public void setCsfbFaildStrDec(String csfbFaildStrDec) {
		this.csfbFaildStrDec = csfbFaildStrDec;
	}

	/**CSFB失败第一个节点网络类型*/
	public String getFaildNetType() {
		return faildNetType;
	}

	public void setFaildNetType(String faildNetType) {
		this.faildNetType = faildNetType;
	}

	/**当前失败第一个事件对应采样点的当前网络相关参数结果,如:BCCH:38350@@BSIC:7@@LAC:-6.12dBm;显示时以@@符分割*/
	public String getFaildParamValues() {
		return faildParamValues;
	}

	public void setFaildParamValues(String faildParamValues) {
		this.faildParamValues = faildParamValues;
	}

	/**异常产生时当前次任务相关的所有事件列表*/
	public Map<Integer, CsfbFaildEventModel> getCsfbEventList() {
		return csfbEventMap;
	}

	public void setCsfbEventList(
			LinkedHashMap<Integer, CsfbFaildEventModel> csfbEventList) {
		this.csfbEventMap = csfbEventList;
	}

	/**获得主被叫类型串*/
	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
	}
}
