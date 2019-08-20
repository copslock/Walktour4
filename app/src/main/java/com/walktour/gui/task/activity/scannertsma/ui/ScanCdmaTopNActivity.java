package com.walktour.gui.task.activity.scannertsma.ui;

import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.dingli.seegull.SeeGullFlags.ScanIDShow;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UtilsMethod;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.gui.task.activity.scanner.model.CdmaCpichPilotModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * * @author jinfeng.xie
 * Scanner CDMA TopN界面
 */
public class ScanCdmaTopNActivity extends BasicActivity  implements RefreshEventListener{
	
	private float density;
	private String[] titles = new String[]{"LTE Pilot List", "LTE PSCH List", "LTE SSCH List", "TD-SCDMA Pilot List", "WCDMA Pilot List", "WCDMA PSCH List", "WCDMA SSCH List", "GSM Color Code", "CDMA Pilot List"};
	private HorizontalBarChart chart;
	private String[] showColumnNameLineOne;
	private ArrayList<CdmaCpichPilotModel> topNModelResult;
	private CustomScrollView sv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		density = getResources().getDisplayMetrics().density;
		topNModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_CDMA_CPICH);
		initView();
		RefreshEventManager.addRefreshListener(this);
	}

	private void initView() {
		setContentView(createChart(topNModelResult == null ? 0 : topNModelResult.size(), 9));
	}
	
	
	 private LinearLayout createChart(int dataCount, int type) {
	    	LinearLayout layout = new LinearLayout(this);
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
	    	sv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	    	chart = new HorizontalBarChart(this, 2);
	    	List<HashMap<String, Object>> itemValueLineOne = getDataLineOne(dataCount, showColumnNameLineOne, type);
	    	List<HashMap<String, Object>> itemValueLineTwo = getDataLineTwo(dataCount, showColumnNameLineTwo, type);
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
	 			showColumnNameLineOne = new String[]{"EARFCN", "PCI", "RSCP", "", "Ec/Io"};
	 		else if (type == 7)
	 			showColumnNameLineOne = new String[]{"EARFCN", "PCI", "", "Io", "Ec/Io"};
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
	    		list.addAll(getItemValueLineOneTypeOne(count, showColumnNameLineOne));
	    	else if (type == 5)
	    		list.addAll(getItemValueLineOneTypeOne(count, showColumnNameLineOne));
	    	else if (type == 6)
	    		list.addAll(getItemValueLineOneTypeOne(count, showColumnNameLineOne));
	       	else if (type == 7)
	    		list.addAll(getItemValueLineOneTypeOne(count, showColumnNameLineOne));
	       	else if (type == 8)
	    		list.addAll(getItemValueLineOneTypeOne(count, showColumnNameLineOne));
	       	else if (type == 9)
	    		list.addAll(getItemValueLineOneTypeOne(count, showColumnNameLineOne));
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
	    private List<HashMap<String, Object>> getItemValueLineOneTypeOne(int count,String[] showColumnNameLineOne) {
	    	List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	    	Random r = new Random();
	    	for (int i = 0; i < count; i++) {
	    		HashMap<String, Object> item = new HashMap<String, Object>();
				item.put(showColumnNameLineOne[0], UtilsMethod.convertValue(topNModelResult.get(i).getChannel()));
				item.put(showColumnNameLineOne[1], UtilsMethod.convertValue(topNModelResult.get(i).getPn()));
				item.put(showColumnNameLineOne[2], UtilsMethod.convertValue(topNModelResult.get(i).getFaggEc()));
				item.put(showColumnNameLineOne[3], UtilsMethod.convertValue(topNModelResult.get(i).getfRSSI()));
				item.put(showColumnNameLineOne[4], UtilsMethod.convertValue(topNModelResult.get(i).getFaggEcIo()));
				item.put("color", 0);
				//				float ecio = Float.parseFloat(item.get(showColumnNameLineOne[4]).toString());
//				if (ecio < -65)
//					item.put("color", getResources().getColor(R.color.green));
//				else if (ecio >= -65 && ecio < -50)
//					item.put("color", getResources().getColor(R.color.red));
//				else if (ecio >= -50 && ecio < -30)
//					item.put("color", getResources().getColor(R.color.blue));
//				else 
//					item.put("color", Color.RED);
				list.add(item);
	    	}
	    	return list;
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
		public void onRefreshed(RefreshType refreshType, Object object) {
			switch (refreshType) {
	         case ACTION_WALKTOUR_TIMER_CHANGED:
	             if(!ApplicationModel.getInstance().isFreezeScreen()){
	            	 topNModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_CDMA_CPICH);
	            	 chart.setItemValueLineOne(getDataLineOne(topNModelResult == null ? 0 : topNModelResult.size(), showColumnNameLineOne, 9));
	            	 chart.invalidate();
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
