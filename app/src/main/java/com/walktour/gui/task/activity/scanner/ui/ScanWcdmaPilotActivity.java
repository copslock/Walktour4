package com.walktour.gui.task.activity.scanner.ui;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.dingli.seegull.SeeGullFlags.ScanIDShow;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UtilsMethod;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.LayoutChangeListener;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.framework.view.ScrollLayout;
import com.walktour.framework.view.ViewSizeLinstener;
import com.walktour.gui.R;
import com.walktour.gui.task.activity.scanner.model.WcdmaCpichPilotModel;
import com.walktour.gui.task.activity.scanner.model.WcdmaPschPilotModel;
import com.walktour.gui.task.activity.scanner.model.WcdmaSschPilotModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Scanner Wcdma pilot界面显示
 * @author zhihui.lian
 */
public class ScanWcdmaPilotActivity extends BasicActivity implements ViewSizeLinstener,RefreshEventListener {

	/**
	 * 滑动Layout对象
	 */
	private ScrollLayout scollLayout;
	private String[] titles = new String[]{"LTE Pilot List", "LTE PSCH List", "LTE SSCH List", "TD-SCDMA Pilot List", "WCDMA Pilot List", "WCDMA PSCH List", "WCDMA SSCH List", "GSM Color Code", "CDMA Pilot List"};
	private String[] showColumnNameLineOne;
	private CustomScrollView sv;
	private ArrayList<WcdmaPschPilotModel> pschModelResult;
	private ArrayList<WcdmaCpichPilotModel> cpichModelResult;
	private ArrayList<WcdmaSschPilotModel> sschModelResult;

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_gsmview);
		pschModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_WCDMA_PSCH);
		cpichModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_WCDMA_CPICH);
		sschModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_WCDMA_SSCH);
		scollLayout = (ScrollLayout) findViewById(R.id.srooll_layout);
		LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		scollLayout.addView(createPilotView(cpichModelResult == null ? 0 : cpichModelResult.size(),5), layoutParams);
		scollLayout.addView(createPilotView(pschModelResult == null ? 0 : pschModelResult.size(),6),layoutParams);
		scollLayout.addView(createPilotView(sschModelResult == null ? 0 : sschModelResult.size(),7),layoutParams);
		RefreshEventManager.addRefreshListener(this);
		scollLayout.addChangeListener(new LayoutChangeListener() {
			@Override
			public void doChange(int lastIndex, int currentIndex) {
				(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.darkdot);
				(initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.darkdot);
				(initImageView(R.id.switch_3)).setBackgroundResource(R.drawable.darkdot);
				switch (currentIndex) {
				case 0:
					(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.lightdot);
					break;
				case 1:
					(initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.lightdot);
					break;
				default:
					(initImageView(R.id.switch_3)).setBackgroundResource(R.drawable.lightdot);
					break;
				}
			}
		});
	}
	
	
	
	/**
	 * 创建Wcdma Pilot View
	 */
	private LinearLayout createPilotView(int dataCount, int type){
		LinearLayout layout = new LinearLayout(this);
		layout.setTag(Integer.valueOf("101" + type));
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    	layout.setOrientation(LinearLayout.VERTICAL);
    	//title
    	HorizontalBarChart title = new HorizontalBarChart(this, 1);
    	title.setChartTitle(getChartTitle(type));
    	showColumnNameLineOne = getColumnNameLineOne(type);
    	String[] showColumnNameLineTwo = getColumnNameLineTwo(type);
    	title.setShowColumnNameLineOne(showColumnNameLineOne);
    	title.setShowColumnNameLineTwo(showColumnNameLineTwo);
    	layout.addView(title);
    	
    	sv = new CustomScrollView(this);
    	sv.setTag(Integer.valueOf("102" + type));
    	sv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    	List<HashMap<String, Object>> itemValueLineOne = getDataLineOne(dataCount, showColumnNameLineOne, type);
    	List<HashMap<String, Object>> itemValueLineTwo = getDataLineTwo(dataCount, showColumnNameLineTwo, type);
    	HorizontalBarChart chart = new HorizontalBarChart(this, 2);
    	chart.setTag(type);
    	chart.setItemValueLineOne(itemValueLineOne);
    	chart.setItemValueLineTwo(itemValueLineTwo);
    	chart.setValueRange(-32, 0);
    	chart.setShowColumnNameLineOne(showColumnNameLineOne);
    	chart.setShowColumnNameLineTwo(showColumnNameLineTwo);
    	sv.setItemCount(itemValueLineOne.size());
    	sv.setItemHeight((int)chart.getItemHeight());
    	sv.addView(chart);
    	layout.addView(sv);
		
		return layout;
	}
	
	
	/**
	 * 下标从1开始
	 * @param type
	 * @return
	 */
	private String getChartTitle(int type) {
 		return titles[type - 1];
 	}
 	
 	private String[] getColumnNameLineOne(int type) {//
 		String[] showColumnNameLineOne = null;
 		if (type == 1 || type == 2 || type == 3)
 			showColumnNameLineOne = new String[]{"EARFCN", "PCI", "RSRP", "RSRQ", "CINR"};
 		else if (type == 4)
 			showColumnNameLineOne = new String[]{"UARFCN", "CPI", "RSCP", "ISCP", "C/I"};
 		else if (type == 5)
 			showColumnNameLineOne = new String[]{"UARFCN", "PSC", "RSCP", "Io", "Ec/Io"};
 		else if (type == 6)
 			showColumnNameLineOne = new String[]{"EARFCN", "PSC", "RSCP", "", "Ec/Io"};
 		else if (type == 7)
 			showColumnNameLineOne = new String[]{"EARFCN", "PSC", "", "RSCP", "Ec/Io"};
 		else if (type == 8)
 			showColumnNameLineOne = new String[]{"BCCH", "BSIC", "", "", "RxLev"};
 		else if (type == 9)
 			showColumnNameLineOne = new String[]{"Freq", "PN", "Ec", "Io", "Ec/Io"};
 		return showColumnNameLineOne;
 	}
 	private String[] getColumnNameLineTwo(int type) {
 		String[] showColumnNameLineTwo = null;
 		if (type == 1 || type == 2 || type == 3)
 			showColumnNameLineTwo = new String[]{"Band", "BW", "CO", "TO"};
 		else if (type == 4)
 			showColumnNameLineTwo = new String[]{"SyncID", "", "", "SIR"};
 		return showColumnNameLineTwo;
 	}

    private List<HashMap<String, Object>> getDataLineOne(int count, String[] showColumnNameLineOne, int type) {
    	List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    	if (type <= 4)
    		list.addAll(getItemValueLineOneTypeOne(count, showColumnNameLineOne,type));
    	else if (type == 5)
    		list.addAll(getItemValueLineOneTypeOne(count, showColumnNameLineOne,type));
    	else if (type == 6)
    		list.addAll(getItemValueLineOneTypeOne(count, showColumnNameLineOne,type));
       	else if (type == 7)
    		list.addAll(getItemValueLineOneTypeOne(count, showColumnNameLineOne,type));
       	else if (type == 8)
    		list.addAll(getItemValueLineOneTypeOne(count, showColumnNameLineOne,type));
       	else if (type == 9)
    		list.addAll(getItemValueLineOneTypeOne(count, showColumnNameLineOne,type));
    	return list;
    }
    
    private List<HashMap<String, Object>> getDataLineTwo(int count, String[] showColumnNameLineTwo, int type) {
    	List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    	if (type <= 3)
    		list.addAll(getItemValueLineTwoTypeOne(count, showColumnNameLineTwo));
    	else if (type == 4)
    		list.addAll(getItemValueLineTwoTypeTwo(count, showColumnNameLineTwo));
    	
    	return list;
    }
    
    //=============================BEGIN GET DEFERENCE DATA===================================================
    //======================================DATA LINE ONE=======================================
    /**
     * 组织数据源
     * @param count
     * @param showColumnNameLineOne
     * @param type
     * @return
     */
    private List<HashMap<String, Object>> getItemValueLineOneTypeOne(int count,String[] showColumnNameLineOne,int type) {
    	
    	List<HashMap<String, Object>> list;
    	switch (type) {
		case 6:
			list = new ArrayList<HashMap<String, Object>>();
	    	for (int i = 0; i < count; i++) {
	    		HashMap<String, Object> item = new HashMap<String, Object>();
				item.put(showColumnNameLineOne[0], UtilsMethod.convertValue(pschModelResult.get(i).getChannel()));
				item.put(showColumnNameLineOne[1], UtilsMethod.convertValue(pschModelResult.get(i).getPsc()));
				item.put(showColumnNameLineOne[2], UtilsMethod.convertValue(pschModelResult.get(i).getRscp()));
				item.put(showColumnNameLineOne[3], "");
				item.put(showColumnNameLineOne[4], UtilsMethod.convertValue(pschModelResult.get(i).getEcio()));
				item.put("color", 0);
				list.add(item);
	    	}
	    	return list;
		case 5:
			list = new ArrayList<HashMap<String, Object>>();
	    	for (int i = 0; i < count; i++) {
	    		HashMap<String, Object> item = new HashMap<String, Object>();
				item.put(showColumnNameLineOne[0], UtilsMethod.convertValue(cpichModelResult.get(i).getChannel()));
				item.put(showColumnNameLineOne[1], UtilsMethod.convertValue(cpichModelResult.get(i).getPsc()));
				item.put(showColumnNameLineOne[2], UtilsMethod.convertValue(cpichModelResult.get(i).getfRSCP()));
				item.put(showColumnNameLineOne[3], UtilsMethod.convertValue(cpichModelResult.get(i).getfRSSI()));
				item.put(showColumnNameLineOne[4], UtilsMethod.convertValue(cpichModelResult.get(i).getFaggEcIo()));
				item.put("color", 0);
				list.add(item);
	    	}
	    	return list;
		case 7:
			list = new ArrayList<HashMap<String, Object>>();
	    	for (int i = 0; i < count; i++) {
	    		HashMap<String, Object> item = new HashMap<String, Object>();
				item.put(showColumnNameLineOne[0], UtilsMethod.convertValue(sschModelResult.get(i).getChannel()));
				item.put(showColumnNameLineOne[1], UtilsMethod.convertValue(sschModelResult.get(i).getPsc()));
				item.put(showColumnNameLineOne[2], "");
				item.put(showColumnNameLineOne[3], UtilsMethod.convertValue(sschModelResult.get(i).getRscp()));
				item.put(showColumnNameLineOne[4], UtilsMethod.convertValue(sschModelResult.get(i).getEcio()));
				item.put("color", 0);
				list.add(item);
	    	}
	    	return list;
		default:
			break;
		}
		return new ArrayList<HashMap<String, Object>>();
    }
  //======================================DATA LINE TWO=======================================
    private List<HashMap<String, Object>> getItemValueLineTwoTypeOne(int count, String[] showColumnNameLineTwo) {
    	List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    	for (int i = 0; i < count; i++) {
    		HashMap<String, Object> item = new HashMap<String, Object>();
			item.put(showColumnNameLineTwo[0], 1800);
			item.put(showColumnNameLineTwo[1], 20);
			item.put(showColumnNameLineTwo[2], 2);
			item.put(showColumnNameLineTwo[3], 21222);
			list.add(item);
    	}
    	
    	return list;
    }
    
    private List<HashMap<String, Object>> getItemValueLineTwoTypeTwo(int count, String[] showColumnNameLineTwo) {
    	List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    	for (int i = 0; i < count; i++) {
    		HashMap<String, Object> item = new HashMap<String, Object>();
			item.put(showColumnNameLineTwo[0], 2);
			item.put(showColumnNameLineTwo[1], "");
			item.put(showColumnNameLineTwo[2], "");
			item.put(showColumnNameLineTwo[3], -4);
			list.add(item);
    	}
    	
    	return list;
    }


	@Override
	public void onViewSizeChange(int height, int weidth) {
		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// No call for super(). Bug on API Level > 11.
	}



	@Override
	public void onRefreshed(RefreshType refreshType, Object object) {
		switch (refreshType) {
        case ACTION_WALKTOUR_TIMER_CHANGED:
            if(!ApplicationModel.getInstance().isFreezeScreen()){
            	pschModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_WCDMA_PSCH);
            	HorizontalBarChart chart = (HorizontalBarChart)scollLayout.findViewWithTag(Integer.valueOf("101" + 6)).findViewWithTag(Integer.valueOf("102" + 6)).findViewWithTag(6);
            	chart.setItemValueLineOne(getDataLineOne(pschModelResult == null ? 0 : pschModelResult.size(), getColumnNameLineOne(6), 6));
            	cpichModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_WCDMA_CPICH);
            	HorizontalBarChart cpichChart = (HorizontalBarChart)scollLayout.findViewWithTag(Integer.valueOf("101" + 5)).findViewWithTag(Integer.valueOf("102" + 5)).findViewWithTag(5);
            	cpichChart.setItemValueLineOne(getDataLineOne(cpichModelResult == null ? 0 : cpichModelResult.size(), getColumnNameLineOne(5), 5));
            	sschModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_WCDMA_SSCH);
            	HorizontalBarChart sschChart = (HorizontalBarChart)scollLayout.findViewWithTag(Integer.valueOf("101" + 7)).findViewWithTag(Integer.valueOf("102" + 7)).findViewWithTag(7);
            	sschChart.setItemValueLineOne(getDataLineOne(sschModelResult == null ? 0 : sschModelResult.size(), getColumnNameLineOne(7), 7));
            }
            break;
		default:
			break;
		}
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		RefreshEventManager.removeRefreshListener(this);
	}
	
	

}
