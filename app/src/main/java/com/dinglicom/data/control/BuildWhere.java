package com.dinglicom.data.control;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 构建查询条件类
 * @author Tangwq
 *
 */
public class BuildWhere {

	private HashMap<String, ArrayList<String>> wheres = null;
	
	public BuildWhere(){
		wheres = new HashMap<String, ArrayList<String>>();
	}
	
	public void addWhere(String simpleName,String where){
		ArrayList<String> whereList = null;
		if(wheres.containsKey(simpleName)){
			whereList = wheres.get(simpleName);
		}else{
			whereList = new ArrayList<String>();
		}
		
		//TODO 此处是否需要添加where条件判再定
		
		whereList.add(where);
		wheres.put(simpleName, whereList);
	}
	
	public HashMap<String, ArrayList<String>> getWhere(){
		return wheres;
	}
}
