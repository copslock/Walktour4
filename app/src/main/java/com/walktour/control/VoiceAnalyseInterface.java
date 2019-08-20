package com.walktour.control;

import android.content.Context;

import com.dinglicom.dataset.DatasetManager;
import com.walktour.model.VoiceFaildModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 语音测试异常分析接口
 * 
 * 对测试结果中的CSFB,VOLTE等语音流程作二次异常分析
 * @author Tangwq
 *
 */
public abstract class VoiceAnalyseInterface {
	//---定义当前是事件节点的显示属性----
	/**当前节点无效且不显示名称*/
	public static final int NODE_SHOW_GONE		= 0;
	/**当前节点无效，但需要显示事件名称*/
	public static final int NODE_SHOW_DISABLE	= 1;
	/**当前节点有效，需要显示详细*/
	public static final int NODE_SHOW_ENABLE	= 2;
	
	/**普通事件*/
	public static final int EVENT_TYPE_NORMAL	= 1;
	/**成功事件*/
	public static final int EVENT_TYPE_SUCCESS	= 2;
	/**失败事件*/
	public static final int EVENT_TYPE_FAILD	= 3;
	/**其它事件*/
	public static final int EVENT_TYPE_OTHER	= 4;
	
	//---业务类型定义---
	/**主叫*/
	protected final int SP_ID_MO	= 0x1001;
	/**被叫*/
	protected final int SP_ID_MT	= 0x1002;
		

	protected DatasetManager dataSetManager = null;
	protected Context mContext = null;
	

	
    
	public abstract HashMap<String, ArrayList<VoiceFaildModel>> getFaildAnalyseResult(String ddibFile,boolean reDecoder);
}
