package com.walktour.gui.task.activity.scannertsma.ui;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.dingli.seegull.ScanLteDataManage;
import com.dingli.seegull.SeeGullFlags.ScanIDShow;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.LayoutChangeListener;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.framework.view.ScrollLayout;
import com.walktour.framework.view.ViewSizeLinstener;
import com.walktour.gui.R;
import com.walktour.gui.task.activity.scanner.model.CellInfo;
import com.walktour.gui.task.activity.scanner.model.LteCellDataPilotModel;
import com.walktour.gui.task.activity.scanner.model.LtePssPilotModel;
import com.walktour.gui.task.activity.scanner.model.LteRsPilotModel;
import com.walktour.gui.task.activity.scanner.model.LteSssPilotModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Scanner LTE pilot界面显示
 * 
 * @author jinfeng.xie
 */
public class ScanLteTopNActivity extends BasicActivity implements ViewSizeLinstener, RefreshEventListener {

	/**
	 * 滑动Layout对象
	 */
	private ScrollLayout scollLayout;
	private String[] titles = new String[] { "LTE Pilot List", "LTE PSCH List", "LTE SSCH List", "TD-SCDMA Pilot List",
			"WCDMA Pilot List", "WCDMA PSCH List", "WCDMA SSCH List", "GSM Color Code", "CDMA Pilot List" };
	private String[] showColumnNameLineOne;
	private CustomScrollView sv;
	private String[] showColumnNameLineTwo;
	private List<LteCellDataPilotModel> cellInfoModelResult;
	private List<LtePssPilotModel> pssModelResult;
	private List<LteRsPilotModel> rsModelResult;
	private List<LteSssPilotModel> sssModelResult;
	private ScanLteDataManage scanLteDataManage = null;
	private byte[] lock = new byte[0];

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_gsmview);
		scanLteDataManage = ScanLteDataManage.getInstance();
		cellInfoModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_LTE_CellInfo);
		rsModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_LTE_RS);
		pssModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_LTE_PSS);
		sssModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_LTE_SSS);
		scollLayout = (ScrollLayout) findViewById(R.id.srooll_layout);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		scollLayout.addView(createPilotView(rsModelResult == null ? 0 : rsModelResult.size(), 1), layoutParams);
		scollLayout.addView(createPilotView(pssModelResult == null ? 0 : pssModelResult.size(), 2), layoutParams);
		scollLayout.addView(createPilotView(sssModelResult == null ? 0 : sssModelResult.size(), 3), layoutParams);

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
	private LinearLayout createPilotView(int dataCount, int type) {
		LinearLayout layout = new LinearLayout(this);
		layout.setId(Integer.valueOf("101" + type));
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		layout.setOrientation(LinearLayout.VERTICAL);
		// title
		HorizontalBarChart title = new HorizontalBarChart(this, 1);
		title.setChartTitle(getChartTitle(type));
		showColumnNameLineOne = getColumnNameLineOne(type); // 列表参数标题值显示
		showColumnNameLineTwo = getColumnNameLineTwo(type); // 列表参数副标题显示
		title.setShowColumnNameLineOne(showColumnNameLineOne);
		title.setShowColumnNameLineTwo(showColumnNameLineTwo);
		layout.addView(title);

		sv = new CustomScrollView(this);
		sv.setId(Integer.valueOf("102" + type));
		sv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		List<HashMap<String, Object>> itemValueLineOne = getDataLineOne(dataCount, showColumnNameLineOne, type);
		List<HashMap<String, Object>> itemValueLineTwo = getDataLineTwo(dataCount, showColumnNameLineTwo, type);
		HorizontalBarChart chart = new HorizontalBarChart(this, 2);
		chart.setId(type);
		chart.setItemValueLineOne(itemValueLineOne);
		chart.setItemValueLineTwo(itemValueLineTwo);
		chart.setValueRange(-20, 50);
		chart.setShowColumnNameLineOne(showColumnNameLineOne);
		chart.setShowColumnNameLineTwo(showColumnNameLineTwo);
		sv.setItemCount(itemValueLineOne.size());
		sv.setItemHeight((int) chart.getItemHeight());
		sv.addView(chart);
		layout.addView(sv);

		return layout;
	}

	/**
	 * 下标从1开始
	 * 
	 * @param type
	 * @return
	 */
	private String getChartTitle(int type) {
		return titles[type - 1];
	}

	private String[] getColumnNameLineOne(int type) {//
		String[] showColumnNameLineOne = null;
		if (type == 1 || type == 2 || type == 3)
			showColumnNameLineOne = new String[] { "EARFCN", "PCI", "RSRP", "RSRQ", "CINR" };
		else if (type == 4)
			showColumnNameLineOne = new String[] { "UARFCN", "CPI", "RSCP", "ISCP", "C/I" };
		else if (type == 5)
			showColumnNameLineOne = new String[] { "UARFCN", "PSC", "RSCP", "Io", "Ec/Io" };
		else if (type == 6)
			showColumnNameLineOne = new String[] { "EARFCN", "PSC", "RSCP", "", "Ec/Io" };
		else if (type == 7)
			showColumnNameLineOne = new String[] { "EARFCN", "PSC", "", "RSCP", "Ec/Io" };
		else if (type == 8)
			showColumnNameLineOne = new String[] { "BCCH", "BSIC", "", "", "RxLev" };
		else if (type == 9)
			showColumnNameLineOne = new String[] { "Freq", "PN", "Ec", "Io", "Ec/Io" };
		return showColumnNameLineOne;
	}

	private String[] getColumnNameLineTwo(int type) {
		String[] showColumnNameLineTwo = null;
		if (type == 1 || type == 2 || type == 3)
			showColumnNameLineTwo = new String[] { "Band", "BW", "RbN", "TO" };
		else if (type == 4)
			showColumnNameLineTwo = new String[] { "SyncID", "", "", "SIR" };
		return showColumnNameLineTwo;
	}

	private List<HashMap<String, Object>> getDataLineOne(int count, String[] showColumnNameLineOne, int type) {
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		if (type <= 4)
			list.addAll(getItemValueLineOneTypeOne(count, showColumnNameLineOne, type));
		else if (type == 5)
			list.addAll(getItemValueLineOneTypeOne(count, showColumnNameLineOne, type));
		else if (type == 6)
			list.addAll(getItemValueLineOneTypeOne(count, showColumnNameLineOne, type));
		else if (type == 7)
			list.addAll(getItemValueLineOneTypeOne(count, showColumnNameLineOne, type));
		else if (type == 8)
			list.addAll(getItemValueLineOneTypeOne(count, showColumnNameLineOne, type));
		else if (type == 9)
			list.addAll(getItemValueLineOneTypeOne(count, showColumnNameLineOne, type));
		return list;
	}

	private List<HashMap<String, Object>> getDataLineTwo(int count, String[] showColumnNameLineTwo, int type) {
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		if (type <= 3)
			list.addAll(getItemValueLineTwoTypeTwo(count, showColumnNameLineTwo, type));

		return list;
	}

	// =============================BEGIN GET DEFERENCE
	// DATA===================================================
	// ======================================DATA LINE
	// ONE=======================================
	/**
	 * 组织数据源
	 * 
	 * @param count
	 * @param showColumnNameLineOne
	 * @param type
	 * @return
	 */
	private List<HashMap<String, Object>> getItemValueLineOneTypeOne(int count, String[] showColumnNameLineOne, int type) {

		List<HashMap<String, Object>> list;

		try {
			switch (type) {
			case 1:
				list = new ArrayList<HashMap<String, Object>>();
				for (int i = 0; i < count; i++) {
					HashMap<String, Object> item = new HashMap<String, Object>();
					item.put(showColumnNameLineOne[0], convertValue(rsModelResult.get(i).getEarfcn()));
					item.put(showColumnNameLineOne[1], convertValue(rsModelResult.get(i).getPci()));
					item.put(showColumnNameLineOne[2], convertValue(rsModelResult.get(i).getRp()));
					item.put(showColumnNameLineOne[3], convertValue(rsModelResult.get(i).getRq()));
					item.put(showColumnNameLineOne[4], convertValue(rsModelResult.get(i).getCinr()));
					item.put("color", 0);
					list.add(item);
				}
				return list;
			case 2:
				list = new ArrayList<HashMap<String, Object>>();
				for (int i = 0; i < count; i++) {
					HashMap<String, Object> item = new HashMap<String, Object>();
					item.put(showColumnNameLineOne[0], convertValue(pssModelResult.get(i).getEarfcn()));
					item.put(showColumnNameLineOne[1], convertValue(pssModelResult.get(i).getPci()));
					item.put(showColumnNameLineOne[2], convertValue(pssModelResult.get(i).getRp()));
					item.put(showColumnNameLineOne[3], convertValue(pssModelResult.get(i).getRq()));
					item.put(showColumnNameLineOne[4], convertValue(pssModelResult.get(i).getCinr()));
					item.put("color", 0);
					list.add(item);
				}
				return list;
			case 3:
				list = new ArrayList<HashMap<String, Object>>();
				for (int i = 0; i < count; i++) {
					HashMap<String, Object> item = new HashMap<String, Object>();
					item.put(showColumnNameLineOne[0], convertValue(sssModelResult.get(i).getEarfcn()));
					item.put(showColumnNameLineOne[1], convertValue(sssModelResult.get(i).getPci()));
					item.put(showColumnNameLineOne[2], convertValue(sssModelResult.get(i).getRp()));
					item.put(showColumnNameLineOne[3], convertValue(sssModelResult.get(i).getRq()));
					item.put(showColumnNameLineOne[4], convertValue(sssModelResult.get(i).getCinr()));
					item.put("color", 0);
					list.add(item);
				}
				return list;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ArrayList<HashMap<String, Object>>();
	}

	private List<CellInfo> rsInfos = new ArrayList<CellInfo>();
	private List<CellInfo> pssInfos = new ArrayList<CellInfo>();
	private List<CellInfo> sssInfos = new ArrayList<CellInfo>();

	private List<HashMap<String, Object>> getItemValueLineTwoTypeTwo(int count, String[] showColumnNameLineTwo, int type) {
		List<HashMap<String, Object>> list;
		switch (type) {
		case 1:
			list = new ArrayList<HashMap<String, Object>>();
			rsInfos.clear();
			rsInfos = scanLteDataManage.findBandRelativeModels(rsModelResult, cellInfoModelResult);
			for (int i = 0; i < rsInfos.size(); i++) {
				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put(showColumnNameLineTwo[0], convertValue(rsInfos.get(i).getBand()));
				item.put(showColumnNameLineTwo[1], convertValue(rsInfos.get(i).getBandWidth()));
				item.put(showColumnNameLineTwo[2], convertValue(rsInfos.get(i).getRbNum()));
				item.put(showColumnNameLineTwo[3], convertValue(rsInfos.get(i).getTimeOffset()));
				list.add(item);
			}
			return list;
		case 2:
			list = new ArrayList<HashMap<String, Object>>();
			pssInfos.clear();
			pssInfos = scanLteDataManage.findBandRelativeModels(pssModelResult, cellInfoModelResult);
			for (int i = 0; i < pssInfos.size(); i++) {
				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put(showColumnNameLineTwo[0], convertValue(pssInfos.get(i).getBand()));
				item.put(showColumnNameLineTwo[1], convertValue(pssInfos.get(i).getBandWidth()));
				// item.put(showColumnNameLineTwo[1],
				// UtilsMethod.subZeroAndDot(String.valueOf(pssInfos.get(i).getBandWidth()))+"M");
				item.put(showColumnNameLineTwo[2], convertValue(pssInfos.get(i).getRbNum()));
				item.put(showColumnNameLineTwo[3], convertValue(pssInfos.get(i).getTimeOffset()));
				list.add(item);
			}

			return list;
		case 3:
			list = new ArrayList<HashMap<String, Object>>();
			sssInfos.clear();
			sssInfos = scanLteDataManage.findBandRelativeModels(sssModelResult, cellInfoModelResult);
			for (int i = 0; i < sssInfos.size(); i++) {
				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put(showColumnNameLineTwo[0], convertValue(sssInfos.get(i).getBand()));
				item.put(showColumnNameLineTwo[1], convertValue(sssInfos.get(i).getBandWidth()));
				// item.put(showColumnNameLineTwo[1],
				// UtilsMethod.subZeroAndDot(String.valueOf(sssInfos.get(i).getBandWidth()))+"M");
				item.put(showColumnNameLineTwo[2], convertValue(sssInfos.get(i).getRbNum()));
				item.put(showColumnNameLineTwo[3], convertValue(sssInfos.get(i).getTimeOffset()));
				list.add(item);
			}

			return list;

		default:
			break;
		}
		return new ArrayList<HashMap<String, Object>>();
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
			if (!ApplicationModel.getInstance().isFreezeScreen()) {
				synchronized (lock) {
					cellInfoModelResult = TraceInfoInterface.traceData
							.getScanResultList(ScanIDShow.SCANID_LTE_CellInfo); // 查小区信息
					rsModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_LTE_RS); // 1
					pssModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_LTE_PSS); // 2
					sssModelResult = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.SCANID_LTE_SSS); // 3
					refreshView();
				}
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 处理-9999
	 * @param value
	 * @return
	 */
	private Object convertValue(Object value) {
		Object mValue = "";
		mValue = value;
		if (value instanceof Float) {
			if ((Float) value == -9999) {
				mValue = "";
			}
		} else if (value instanceof Long) {
			if ((Long) value == -9999) {
				mValue = "";
			}
		} else if (value instanceof Integer) {
			if ((Integer) value == -9999) {
				mValue = "";
			}
		}
		return mValue;
	}

	/**
	 * 刷新view
	 */
	private void refreshView() {
		for (int i = 1; i < 4; i++) {
			CustomScrollView sschSv = (CustomScrollView) scollLayout.findViewById(Integer.valueOf("101" + i))
					.findViewById(Integer.valueOf("102" + i));
			HorizontalBarChart sschChart = (HorizontalBarChart) sschSv.findViewById(i);
			List<HashMap<String, Object>> sschList = getDataLineOne(sssModelResult == null ? 0 : sssModelResult.size(),
					getColumnNameLineOne(i), i);
			sschChart.setItemValueLineOne(sschList);
			sschChart.setItemValueLineTwo(getDataLineTwo(sssModelResult == null ? 0 : sssModelResult.size(),
					showColumnNameLineTwo, i));
			sschSv.setItemCount(sschList.size());
			sschSv.setItemHeight((int) sschChart.getItemHeight());
			sschSv.removeAllViews();
			sschSv.addView(sschChart);
			sschSv.invalidate();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RefreshEventManager.removeRefreshListener(this);
	}

}
