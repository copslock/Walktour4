package com.walktour.control.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkStruct.ShowInfoType;
import com.walktour.base.util.LogUtil;

import java.util.ArrayList;

/**
 * 显示页面管理
 * 
 * */
public class PageManager{
	public static final String PREFERENCE_NAME = "info";
	private final String tag = "PageManager";
	private Context mContext;
	public PageManager(Context context,boolean reSetPara){
		this.mContext = context;
		initPreference(reSetPara);
	}
	
	/**
	 * @return 查看信息要显示的页面列表
	 * */
	public ArrayList<ShowInfoType> getShowInfoList(){
		ArrayList<ShowInfoType> infoList = new ArrayList<ShowInfoType>(); 
		//页面显示配置
    	SharedPreferences share = mContext.getSharedPreferences( mContext.getPackageName(), Context.MODE_PRIVATE );
    	String defStr = ShowInfoType.Map.toString()+","
    	                    +ShowInfoType.Info.toString()+","
    	                    +ShowInfoType.Param.toString()+","
    						+ShowInfoType.Chart.toString()+","
    						+ShowInfoType.Data.toString()+","
    						+ShowInfoType.Event.toString()+","
    						+ShowInfoType.L3Msg.toString();
    	String[] items = share.getString( PREFERENCE_NAME, defStr ).split(",");
    	for(String item:items ){
    		ShowInfoType type = ShowInfoType.valueOf(item);
    		//如果是网络类型页面
    		if( type.getNetType()!=0 ){
    			if( hasLicense(type) ){
    				infoList.add(type);
    			}
    		}else{
    			infoList.add( type );
    		}
    	}
		return infoList;
	}
	
    /**
     * 按默认初始化SharedPreferences
     * */
    public void initPreference(boolean isResetPara){
    	//页面显示配置
    	SharedPreferences share = mContext.getSharedPreferences( mContext.getPackageName(), Context.MODE_PRIVATE );
    	LogUtil.w(tag,"----contains info:"+share.contains( PageManager.PREFERENCE_NAME )+"---isreset:"+isResetPara);
 		if( !share.contains( PageManager.PREFERENCE_NAME ) || isResetPara){
 			Editor editor = share.edit();
 			StringBuffer infoPages = new StringBuffer();
 			ArrayList<ShowInfoType> defList = getDefaultPageList();
 			//ArrayList<ShowInfoType> defList = ApplicationModel.getInstance().getNetList();
 			for(int i=0;i<defList.size();i++){
 				if( i>0){
 					infoPages.append(",");
 				}
 				infoPages.append( defList.get(i).toString() );
 			}
 			//LogUtil.w("PageManager","---infoPages:"+infoPages.toString());
 			editor.putString( PageManager.PREFERENCE_NAME, infoPages.toString() );
 			editor.commit();
 		}
    }
    
    /**
     * 获取默认的全部页面,其中页面参数页面是根据License获取
     * */
    public ArrayList<ShowInfoType> getDefaultPageList(){
    		ArrayList<ShowInfoType> defList = new ArrayList<ShowInfoType>();
    		ArrayList<ShowInfoType> netList = ApplicationModel.getInstance().getNetList();//有权限的网络类型
			//添加地图和参数界面
    		if(!netList.contains(ShowInfoType.Map))
    			defList.add( ShowInfoType.Map );
            if(!netList.contains(ShowInfoType.Param))
                defList.add( ShowInfoType.Param );
            if(!netList.contains(ShowInfoType.Info))
                defList.add( ShowInfoType.Info );
    		if(!netList.contains(ShowInfoType.Chart))
    			defList.add( ShowInfoType.Chart );
			//添加网络参数页面
			for(int i=0;i<netList.size();i++){
				defList.add( netList.get( i));
			}
			//其它页面
			//defList.add( ShowInfoType.LTE );
			if(!netList.contains(ShowInfoType.Data))
    			defList.add( ShowInfoType.Data );
			if(!netList.contains(ShowInfoType.Event))
    			defList.add( ShowInfoType.Event );
			if(!netList.contains(ShowInfoType.L3Msg))
    			defList.add(ShowInfoType.L3Msg);
			//defList.add( ShowInfoType.AlarmMsg );
			
			return defList;
    }
	
	/**
	 * 该网络类型是否在License权限文件里
	 * */
	private boolean hasLicense(ShowInfoType showInfotype){
		ArrayList<ShowInfoType> netList = ApplicationModel.getInstance().getNetList();//有权限的网络类型
		for(ShowInfoType infoType:netList){
			if( showInfotype == infoType ){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 隐藏页面
	 * @param type 要隐藏的页面对应的类型
	 * */
	public void hidePage(ShowInfoType type){
		SharedPreferences share = mContext.getSharedPreferences( mContext.getPackageName(), Context.MODE_PRIVATE );
		String[] items = share.getString( PREFERENCE_NAME, "" ).split(",");
		
		String infoPages = "";
		for(int i=0;i<items.length;i++){
			if( items[i].trim().toString().length()>0 ){
				if( ! items[i] .equals( type.toString() ) ){
					infoPages += items[i]+",";
				}
			}
		}
		if( infoPages.endsWith(",") ){
			infoPages = infoPages.substring(0, infoPages.lastIndexOf(",") );
		}
		
		Editor editor = share.edit();
		editor.putString( PageManager.PREFERENCE_NAME, infoPages );
		editor.commit();
	}
	
	/**
	 * 设置要显示的界面
	 * */
	public void setPages(ArrayList<ShowInfoType> showTypeList){
		SharedPreferences share = mContext.getSharedPreferences( mContext.getPackageName(), Context.MODE_PRIVATE );
		String infoPages = "";
		//至少显示一个界面
		if( showTypeList.size() ==0 ){
			infoPages =ShowInfoType.Map.toString();
		}else{
			for( int i=0;i<showTypeList.size();i++){
				infoPages += showTypeList.get(i).toString()+",";
			}
		}
		if( infoPages.endsWith(",") ){
			infoPages = infoPages.substring(0, infoPages.lastIndexOf(",") );
		}
		
		Editor editor = share.edit();
		editor.putString( PageManager.PREFERENCE_NAME, infoPages );
		editor.commit();
	}
	
}