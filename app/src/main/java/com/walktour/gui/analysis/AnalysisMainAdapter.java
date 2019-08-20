package com.walktour.gui.analysis;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.R;
import com.walktour.gui.analysis.commons.AnalysisCommons;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/***
 * 智能分析选项
 * 
 * @author weirong.fan
 *
 */
public class AnalysisMainAdapter extends BaseAdapter {
	private ApplicationModel appModel = ApplicationModel.getInstance();
	private Context context;

	int showNum = 0; // 功能显示个数
	int[] icons; 
	int iconi = 0;
	int stri = 0;

	private List<WalkStruct.ShowInfoType> showTypes=new LinkedList<WalkStruct.ShowInfoType>();
	/***
	 * 配置文件工具类
	 */
	SharePreferencesUtil sharePreferencesUtil=null;
	/**
	 * [构造简要说明]
	 */
	public AnalysisMainAdapter(Context context) {
		super();
		this.context = context;
		sharePreferencesUtil=SharePreferencesUtil.getInstance(context);
		if (appModel.getNetList().contains(WalkStruct.ShowInfoType.CSFBAnalysis)) {
			showNum++; 
			showTypes.add(WalkStruct.ShowInfoType.CSFBAnalysis);
		}

		if (appModel.getNetList().contains(WalkStruct.ShowInfoType.VoLTEAnalyse)) {
			showNum++;
			showTypes.add(WalkStruct.ShowInfoType.VoLTEAnalyse);
		}
		
		if (appModel.isLTEData()) {
			showNum++;
			showTypes.add(WalkStruct.ShowInfoType.LTEDataAnalysis);
		}
		// 生成每个Item的图标
		icons = new int[showNum]; 
		iconi = 0;
		stri = 0;

		//按选择先后顺序显示按钮
		String str_select=sharePreferencesUtil.getString(AnalysisCommons.ANALYSIS_SELECT_SCENE).trim();
		String[] selects=str_select.length()>0?str_select.split(","):null;
		if(null!=selects){
			for(int index=0;index<selects.length;index++){
				if(selects[index].equals(AnalysisCommons.ANALYSIS_CSFB)){
					addCsfbBtn();
				}else if(selects[index].equals(AnalysisCommons.ANALYSIS_VOLTE)){
					addVoLTEBtn();		
				}else if(selects[index].equals(AnalysisCommons.ANALYSIS_LTEDATA)){
					addLTEBtn();		
				}
			} 
			Iterator<WalkStruct.ShowInfoType> iter = showTypes.iterator();  
			while(iter.hasNext()){  
				WalkStruct.ShowInfoType type=iter.next();
				if(type.equals(WalkStruct.ShowInfoType.CSFBAnalysis)){
					icons[iconi++] = R.drawable.csfbanalysis;
					if(sharePreferencesUtil.getString(AnalysisCommons.ANALYSIS_SELECT_SCENE).contains(AnalysisCommons.ANALYSIS_CSFB+",")){
						icons[iconi-1] = R.drawable.csfbanalysis_press;	
					} 
					iter.remove();
				}else if(type.equals(WalkStruct.ShowInfoType.VoLTEAnalyse)){
					icons[iconi++] = R.drawable.volteanalyse;
					if(sharePreferencesUtil.getString(AnalysisCommons.ANALYSIS_SELECT_SCENE).contains(AnalysisCommons.ANALYSIS_VOLTE+",")){
						icons[iconi-1] = R.drawable.volteanalyse_press;	
					} 
					iter.remove();
				}else if(type.equals(WalkStruct.ShowInfoType.LTEDataAnalysis)){
					icons[iconi++] = R.drawable.ltedataanalyse;
					if(sharePreferencesUtil.getString(AnalysisCommons.ANALYSIS_SELECT_SCENE).contains(AnalysisCommons.ANALYSIS_LTEDATA+",")){
						icons[iconi-1] = R.drawable.ltedataanalyse_press;	
					} 
					iter.remove();
				}
			} 
		}else{
			addCsfbBtn();
			addVoLTEBtn();
			addLTEBtn();
		}
	}
 
	/**
	 * 添加csfb按钮
	 */
	private void addCsfbBtn(){
		if (showTypes.contains(WalkStruct.ShowInfoType.CSFBAnalysis)) {
			icons[iconi++] = R.drawable.csfbanalysis;
			if(sharePreferencesUtil.getString(AnalysisCommons.ANALYSIS_SELECT_SCENE).contains(AnalysisCommons.ANALYSIS_CSFB+",")){
				icons[iconi-1] = R.drawable.csfbanalysis_press;	
			} 
			showTypes.remove(WalkStruct.ShowInfoType.CSFBAnalysis);
		}
	}
	/**
	 * 添加volte按钮
	 */
	private void addVoLTEBtn(){
		if (showTypes.contains(WalkStruct.ShowInfoType.VoLTEAnalyse)) {
			icons[iconi++] = R.drawable.volteanalyse;
			if(sharePreferencesUtil.getString(AnalysisCommons.ANALYSIS_SELECT_SCENE).contains(AnalysisCommons.ANALYSIS_VOLTE+",")){
				icons[iconi-1] = R.drawable.volteanalyse_press;	
			} 
			showTypes.remove(WalkStruct.ShowInfoType.VoLTEAnalyse);
		}
	}
	
	/**
	 * 添加ltedata按钮
	 */
	private void addLTEBtn(){
		if (showTypes.contains(WalkStruct.ShowInfoType.LTEDataAnalysis)) {
			icons[iconi++] = R.drawable.ltedataanalyse;
			if(sharePreferencesUtil.getString(AnalysisCommons.ANALYSIS_SELECT_SCENE).contains(AnalysisCommons.ANALYSIS_LTEDATA+",")){
				icons[iconi-1] = R.drawable.ltedataanalyse_press;	
			} 
			showTypes.remove(WalkStruct.ShowInfoType.LTEDataAnalysis);
		}
	}
	@Override
	public int getCount() {
		return icons.length;
	}

	@Override
	public Object getItem(int position) {
		return icons[position];
	}

	@Override
	public long getItemId(int position) {
		return icons[position];
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.analysis_main_gridview_item, null);
			holder = new ViewHolder(); 
			holder.iconImageView = (ImageView) convertView.findViewById(R.id.ItemImage);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
 
		holder.iconImageView.setImageResource(icons[position]);
		convertView.setId(icons[position]); 
		return convertView;

	}

	public class ViewHolder { 
		public ImageView iconImageView;
	}
}
