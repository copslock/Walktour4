package com.walktour.Utils;

import com.walktour.Utils.dataset.DataSetNode;
import com.walktour.Utils.dataset.DataSetSignalXmlTools;
import com.walktour.model.TdL3Model;

import java.util.Iterator;
import java.util.Map;

/**
 * 根据L3 Code信息生成相关的层3信息
 * @author tangwq
 *
 */
public class UnifyL3Decode{
	public static TdL3Model disposeL3Info(long id){

		Map<Long,DataSetNode> map= DataSetSignalXmlTools.getInstance().getMap();

		DataSetNode node=map.get((long)id);
		TdL3Model model=new TdL3Model();
		model.setId(id);
		if(null!=node&&node.getName().length()>0){
			model.setL3Msg(node.getName());
		}else{
			boolean isFlag=false;
			Iterator<Map.Entry<Long,DataSetNode>> entries = map.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry<Long,DataSetNode> entry = entries.next();


				Long lx=entry.getKey();
				DataSetNode nox=entry.getValue();

				if(lx.intValue()==id){
					isFlag=true;
					model.setL3Msg(nox.getName());
				}
//				if(Integer.toHexString(id).toUpperCase().equals(Integer.toHexString(lx.intValue()).toUpperCase())){
//					isFlag=true;
//					model.setL3Msg(nox.getName());
//				}
			}
			if(!isFlag) {
				model.setL3Msg("");
			}
		}
		return model;
	}
}
