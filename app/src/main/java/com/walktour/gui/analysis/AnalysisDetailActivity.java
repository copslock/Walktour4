package com.walktour.gui.analysis;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.ImageUtil;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.DateUtil;
import com.walktour.control.CsfbFaildAnalyse;
import com.walktour.control.VoLTEFaildAnalyse;
import com.walktour.control.VoiceAnalyse;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.analysis.commons.AnalysisCommons;
import com.walktour.gui.analysis.csfb.CsfbDetailActivity;
import com.walktour.gui.analysis.csfb.CsfbPieChartActivity;
import com.walktour.gui.analysis.db.AnalysisDBManage;
import com.walktour.gui.analysis.ltedata.LteDataActivity;
import com.walktour.gui.analysis.ltedata.LteDataDetaiActivity;
import com.walktour.gui.analysis.model.AnalysisModel;
import com.walktour.gui.analysis.model.ResultJsonModel;
import com.walktour.gui.analysis.view.pie.EntyExpenses;
import com.walktour.gui.analysis.view.pie.PieLayout;
import com.walktour.gui.analysis.view.pie.PieView;
import com.walktour.gui.analysis.volte.VoLTEPieChartActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 智能分析主界面
 * 
 * @author weirong.fan
 *
 */
@SuppressLint("InflateParams") 
public class AnalysisDetailActivity extends BasicActivity implements OnClickListener {
	private Context context = AnalysisDetailActivity.this;
	/**外面父控件**/
	private LinearLayout layoutBig; 
	private ScrollView datadetailinfoscrollview;
	private LinearLayout datapiesLayout;
	/**除标题栏外的布局容器*/
	private LinearLayout mLayoutContainer;
	/** 地图 **/
	private MapView mapView = null;
	/** 百度地图 */
	private BaiduMap mMap;
	/** 定位客户端 */
	private LocationClient mLocClient; 
	private TextView totalView; 
	/** 饼图集合*/
	private List<PieLayout> pieViews = new LinkedList<PieLayout>();
	/** 饼图布局集合*/
	private List<LinearLayout> pieLayout = new LinkedList<LinearLayout>();
	private List<LinearLayout> listLayout = new LinkedList<LinearLayout>();
	/** 集合*/
	private Map<String, LinearLayout> mapLayout = new HashMap<String, LinearLayout>(); 
	/** csfb异常 **/
	private Map<String, LinkedList<AnalysisModel>> csfbException = new HashMap<String, LinkedList<AnalysisModel>>();
	/** volte异常 **/
	private Map<String, LinkedList<AnalysisModel>> volteException = new HashMap<String, LinkedList<AnalysisModel>>();
	/** ltedata异常 **/
	private Map<String, LinkedList<AnalysisModel>> ltedataException = new HashMap<String, LinkedList<AnalysisModel>>();

	/** 当前选择的场景 **/
	private String currentScene = "";
	/** 当前选择的场景对应的异常 **/
	private Map<String, LinkedList<AnalysisModel>> currentException = new HashMap<String, LinkedList<AnalysisModel>>();
	/** 是否显示地图 **/
	private boolean isShowMap = false;
	/** 更多菜单 */
	private PopupWindow popMoreMenu1;
	/** 更多菜单 */
	private PopupWindow popMoreMenu2;

	/** 历史结果 **/
	private List<ResultJsonModel> history = new LinkedList<ResultJsonModel>();

	private List<String> pieAllColors = new LinkedList<String>(); 
	/*** LTEData 具体异常原因占比 **/
	private ArrayList<String> ltetotal = new ArrayList<String>();
	/** 选择的场景 */
	private String[] selectScenes = null;

	private MyLocationListenner locationListener = new MyLocationListenner();

	/**饼图放大缩小大小*/
	private int maxWidth=0;
	private int maxHeight=0;
	private int minWidth=0;
	private int minHeight=0;
	/**默认颜色*/
	private String defaultColor="#ffffff";
	/**选中颜色*/
	private String blueColor="#8ecaff";
	/**字体颜色**/
	private String textBlueColor="#418ce3";
	private String textWhiteColor="#ffffff";
	/**地图切换按钮**/
	private ImageView mapBtn;
	/**最新的分析文件**/
	private ResultJsonModel latestModel;
	/**每一个异常的高度，也就是下面的异常包含上面异常的高度，方便自动拖动**/
	private Map<String,Integer> exceptionHeight=new HashMap<String,Integer>();
	/**
	 * 配置文件
	 */
	private SharePreferencesUtil sharePreferencesUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(this.getApplicationContext());
		this.setContentView(R.layout.analysis_main_detail);
		sharePreferencesUtil=SharePreferencesUtil.getInstance(this);
		try {
			maxWidth=dip2px(context,150f);
			maxHeight=dip2px(context,125f);
			minWidth=dip2px(context,100f);
			minHeight=dip2px(context,80f);
			selectScenes = sharePreferencesUtil.getString(AnalysisCommons.ANALYSIS_SELECT_SCENE).trim()
					.split(",");
			// 历史数据
			history.clear();
			history.addAll(parseResultJson());

			latestModel=(history.get(history.size()-1));
			findViewById(R.id.backBtn).setOnClickListener(this);
			findViewById(R.id.datadetailtotal).setOnClickListener(this);
			mapBtn=this.initImageView(R.id.datadetailmap);
			mapBtn.setOnClickListener(this);
			mLayoutContainer = (LinearLayout) findViewById(R.id.ll_layout_container);
			if(latestModel.allIsDT==0){
				mapBtn.setVisibility(View.GONE);	
			}else{
				mapBtn.setVisibility(View.VISIBLE);
			}
			findViewById(R.id.historydetail).setOnClickListener(this);
			findViewById(R.id.maindetail).setOnClickListener(this);
			findViewById(R.id.mainsave).setOnClickListener(this);
			totalView = initTextView(R.id.totalcount);
			datapiesLayout = this.initLinearLayout(R.id.datapieslayout);
			initPieColors();
			String dbFilePath = this.getIntent().getStringExtra(AnalysisCommons.ANALYSIS_SELECT_FILE_MERGEUK_PATH);
			initView(selectScenes, dbFilePath);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/***
	 * 饼图默认的10种颜色
	 */
	private void initPieColors() {
		pieAllColors.clear();
		pieAllColors.add("#60d1d9");
		pieAllColors.add("#35b7e4");
		pieAllColors.add("#fe9c29");
		pieAllColors.add("#70ad47");
		pieAllColors.add("#b8df72");
		pieAllColors.add("#e14956");
		pieAllColors.add("#f1ee83");
		pieAllColors.add("#37c4bc");
		pieAllColors.add("#3a6286");
		pieAllColors.add("#90214a");
	}

	private void changeCurrentScene(String scene) {
		this.currentScene = scene;
		if (scene.equals(AnalysisCommons.ANALYSIS_CSFB)) {
			currentException = csfbException;
		} else if (scene.equals(AnalysisCommons.ANALYSIS_VOLTE)) {
			currentException = volteException;
		} else if (scene.equals(AnalysisCommons.ANALYSIS_LTEDATA)) {
			currentException = ltedataException;
		}
		int totalSum = 0;
		for (Map.Entry<String, LinkedList<AnalysisModel>> entry : currentException.entrySet()) {
			LinkedList<AnalysisModel> value = entry.getValue();
			totalSum += value.size();
		}
		totalView.setText(totalSum + "");
	}

	/***
	 * 初始化界面
	 * 
	 * @param selectScenes
	 *            选择的场景，按场景初始化界面
	 */
	private void initView(String[] selectScenes, String dbFilePath) {
		initData(dbFilePath);
		pieViews.clear();
		pieLayout.clear();
		datapiesLayout.removeAllViews();
		for (int i = 0; i < selectScenes.length; i++) {
			changeCurrentScene(selectScenes[i]);
			initPieChart(selectScenes[i]); 
		}
		changeCurrentScene(selectScenes[0]);
		for (int i = 0; i < pieViews.size(); i++) {
			PieLayout pieChart = pieViews.get(i);
			if (i==0) {
				pieChart.setWidthAndHeight(maxWidth,maxHeight,true);
			} else {
				pieChart.setWidthAndHeight(minWidth,minHeight,false);
			}
			pieChart.invalidate();
			pieChart.refreshDrawableState();
		}
		layoutBig = this.initLinearLayout(R.id.datadetailall);
		isShowMap = false;
		showInfo();
	}

	@SuppressWarnings("unchecked")
	private void initPieChart(final String title) {
		final LinearLayout picItemLayout = (LinearLayout) LayoutInflater.from(this)
				.inflate(R.layout.analysis_main_detail_picitem, null);
		
		LinearLayout pie = (LinearLayout) picItemLayout.findViewById(R.id.pielayout);
		TextView tv = (TextView) picItemLayout.findViewById(R.id.pictitle);
		tv.setText(title + "");  
		final List<EntyExpenses> list = new ArrayList<EntyExpenses>();
		for (Map.Entry<String, LinkedList<AnalysisModel>> entry : currentException.entrySet()) { 
			EntyExpenses ee=new EntyExpenses(entry.getKey(),Float.valueOf(entry.getValue().size()));
			list.add(ee);
		}
		
		final PieLayout pieChart = new PieLayout(this, null,maxWidth,maxHeight);
		pieChart.initi(list);
		pie.addView(pieChart);
		datapiesLayout.addView(picItemLayout);
		pieChart.getPie().setOnItemChangedListener(new PieView.OnItemChangedListener() {
            
			@Override
            public void onItemChanged(List<EntyExpenses> listNew ) { 
				if (currentException != null) {
					Iterator<Entry<String, LinkedList<AnalysisModel>>> it = currentException.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, LinkedList<AnalysisModel>> resultMap = (Map.Entry) it.next();
						if (listNew.get(0).getExpensesMainType().equals(resultMap.getKey())) {
							for (Map.Entry<String, LinearLayout> entry : mapLayout.entrySet()) {
								if (resultMap.getKey().toString().equals(entry.getKey())) {
									entry.getValue().setBackgroundColor(Color.parseColor(blueColor));
									TextView tv1 = (TextView) entry.getValue().findViewById(R.id.keyvalue1);
									TextView tv2 = (TextView) entry.getValue().findViewById(R.id.keyvalue2);
									tv1.setTextColor(Color.parseColor(textWhiteColor));
									tv2.setTextColor(Color.parseColor(textWhiteColor));
									//滚动到指定位置 
									datadetailinfoscrollview.scrollTo(0,exceptionHeight.get(entry.getKey()));
								} else {
									entry.getValue().setBackgroundColor(Color.parseColor(defaultColor));
									TextView tv1 = (TextView) entry.getValue().findViewById(R.id.keyvalue1);
									TextView tv2 = (TextView) entry.getValue().findViewById(R.id.keyvalue2);
									tv1.setTextColor(Color.parseColor(textBlueColor));
									tv2.setTextColor(Color.parseColor(textBlueColor));
								}
							}

							break;
						} 
					}
				}
			}});
		pieChart.getPie().setOnCircleClickListener(new PieView.OnCircleClickListener() {
			
			@Override
			public void onCircleOnClick(PieView view) {
				changeCurrentScene(title);
				for (int i = 0; i < pieViews.size(); i++) {
					PieLayout pieChart = pieViews.get(i);
					if (view.equals(pieChart.getPie())) {
						pieChart.setWidthAndHeight(maxWidth,maxHeight,true);
					} else {
						pieChart.setWidthAndHeight(minWidth,minHeight,false);
					}
					pieChart.invalidate();
					pieChart.refreshDrawableState();
				}

				for (int i = 0; i < pieLayout.size(); i++) {
					pieLayout.get(i).removeAllViews();
				}
				for (int i = 0; i < pieLayout.size(); i++) {
					pieLayout.get(i).addView(pieViews.get(i));
					pieLayout.get(i).invalidate();
				}
				if (!isShowMap) {
					showInfo();
				} else {
					showMap();
				}
			} 
		}); 

		pieViews.add(pieChart);
		pieLayout.add(pie);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mapView != null) {
			mapView.onResume();
		}
	}

	@Override
	protected void onPause() {

		super.onPause();
		if (mapView != null) {
			mapView.onPause();
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.backBtn:// 返回
			this.finish();
			break;
		case R.id.maindetail:// 返回到场景选择界面
			this.setResult(AnalysisCommons.ANALYSIS_RESULT_CODE);
			this.finish();
			break;
		case R.id.mainsave:// 保存
			saveScreenShot();
			break;
		case R.id.datadetailtotal:// 统计 
			Bundle bundle = new Bundle();
			if (currentScene.equals(AnalysisCommons.ANALYSIS_CSFB)) {
				bundle.putInt("FAILD_TYPE", VoiceAnalyse.FAILD_TYPE_CSFB);
				bundle.putString("TotalCount", totalView.getText().toString()+"");
				jumpActivity(CsfbPieChartActivity.class, bundle);
			} else if (currentScene.equals(AnalysisCommons.ANALYSIS_VOLTE)) {
				bundle.putInt("FAILD_TYPE", VoiceAnalyse.FAILD_TYPE_VOLTE);
				bundle.putString("TotalCount", totalView.getText().toString()+"");
				jumpActivity(VoLTEPieChartActivity.class, bundle);
			} else if (currentScene.equals(AnalysisCommons.ANALYSIS_LTEDATA)) {
				bundle.putInt("FAILD_TYPE", AnalysisCommons.FAILD_TYPE_LTEDATA);
				bundle.putStringArrayList("ltetotal", ltetotal);
				jumpActivity(LteDataActivity.class, bundle);
			}

			break;
		case R.id.datadetailmap:// 地图
			if (!isShowMap) {
				findViewById(R.id.datadetailmap).setBackgroundResource(R.drawable.analysis_7);
				showMap();
			} else {
				findViewById(R.id.datadetailmap).setBackgroundResource(R.drawable.analysis_4);

				showInfo();
			}
			isShowMap = !isShowMap;
			break;
		case R.id.historydetail:// 历史结果
			showMoreMenu();
			break;
		}
	}

	/**
	 * 保存屏幕截图到文件
	 */
	private void saveScreenShot() {
		if(mLayoutContainer.isDrawingCacheEnabled()){
			mLayoutContainer.setDrawingCacheEnabled(false);
		}
		mLayoutContainer.setDrawingCacheEnabled(true);
		Bitmap bmScreenShot = mLayoutContainer.getDrawingCache();
		ImageUtil.saveBitmapToFile(AppFilePathUtil.getInstance().getSDCardBaseFile("data","report").toString(),bmScreenShot,("analysis_"+ DateUtil.formatDate(DateUtil.FORMAT_DATE_TIME2,new Date())), ImageUtil.FileType.JPEG);
		ToastUtil.showToastShort(this,R.string.intelligent_analysis_save_file_success);
	}


	@SuppressWarnings("deprecation")
	private void showMoreMenu() {
		if (popMoreMenu1 == null) {
			LinearLayout view = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.analysis_popview, null);
			for (int index=0;index<history.size();index++) {
				final ResultJsonModel m = history.get(index);
				LinearLayout layoutinfo2 = (LinearLayout) getLayoutInflater().inflate(R.layout.analysis_main_detail_3,
						null);
				TextView tv11 = (TextView) layoutinfo2.findViewById(R.id.keyvalue11);
//				TextView tv22 = (TextView) layoutinfo2.findViewById(R.id.keyvalue22);
				TextView tv33 = (TextView) layoutinfo2.findViewById(R.id.keyvalue33);
				View tv44 = (View) layoutinfo2.findViewById(R.id.viewidx);
				if(index==history.size()-1){
					tv44.setVisibility(View.GONE);
				}else{
					tv44.setVisibility(View.VISIBLE);
				}
				tv11.setText(m.getDataTime().split(" ")[0] + " "+m.getDataTime().split(" ")[1] + "");
				tv33.setText(m.getAnalysis().toString());
				view.addView(layoutinfo2);
				layoutinfo2.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						popMoreMenu1.dismiss();
						File file = AppFilePathUtil.getInstance().getSDCardBaseFile(AnalysisCommons.ANALYSIS_PATH_ROOT,
								AnalysisCommons.ANALYSIS_PATH_ROOT_ANALYSIS,AnalysisCommons.ANALYSIS_PATH_ROOT_ANALYSIS_HISTORY,
								m.getParentName(),AnalysisCommons.ANALYSIS_MERGEUK_NAME	);
						selectScenes = m.getAnalysis().toArray(new String[m.getAnalysis().size()]);
						initView(selectScenes, file.getAbsolutePath());
					}
				});
			}
			popMoreMenu1 = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);//
			popMoreMenu1.setOutsideTouchable(true);
			popMoreMenu1.setFocusable(true);
			popMoreMenu1.setTouchable(true);
			popMoreMenu1.setBackgroundDrawable(new BitmapDrawable());
			popMoreMenu1.showAsDropDown(findViewById(R.id.historydetail), 0, 10);
		} else {
			if (popMoreMenu1.isShowing()) {
				popMoreMenu1.dismiss();
			} else {
				popMoreMenu1.showAsDropDown(findViewById(R.id.historydetail), 0, 10);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void showExceptionType(ImageView imgView) {
		if (popMoreMenu2 == null) {
			final LinearLayout view = (LinearLayout) LayoutInflater.from(this)
					.inflate(R.layout.analysis_popview_exceptiontype, null);
			final CheckBox ckAll=(CheckBox)view.findViewById(R.id.selectallck);
			final LinearLayout selectalllayout = (LinearLayout) view.findViewById(R.id.selectalllayout);
			selectalllayout.removeAllViews();
			for (Map.Entry<String, LinkedList<AnalysisModel>> entry : currentException.entrySet()) {
				String name = entry.getKey();
				CheckBox chb = new CheckBox(this);
				chb.setText(name + "");
				chb.setChecked(true);
				selectalllayout.addView(chb);
				chb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked) {//选择
							int counts=selectalllayout.getChildCount();
//							boolean isAllCheck=true;
							for(int i=0;i<counts;i++){
								CheckBox view=(CheckBox)selectalllayout.getChildAt(i);
								if(!view.isChecked()){
//									isAllCheck=false;
								}
							}
//							ckAll.setChecked(isAllCheck?true:false);
						} else { //未选择
//							ckAll.setChecked(false);
						} 
						refreshMap(selectalllayout);
					}
				});
			}
			ckAll.setChecked(true);
			ckAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {//全选
						int counts=selectalllayout.getChildCount(); 
						for(int i=0;i<counts;i++){
							CheckBox view=(CheckBox)selectalllayout.getChildAt(i);
							view.setChecked(true);
						}
						refreshMap(selectalllayout);
					} else {//全不选
						int counts=selectalllayout.getChildCount(); 
						for(int i=0;i<counts;i++){
							CheckBox view=(CheckBox)selectalllayout.getChildAt(i);
							view.setChecked(false);
						}
						mMap.clear();
					} 
				}
			});
			popMoreMenu2 = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);//
			popMoreMenu2.setOutsideTouchable(true);
			popMoreMenu2.setFocusable(true);
			popMoreMenu2.setTouchable(true);
			popMoreMenu2.setBackgroundDrawable(new BitmapDrawable());
			popMoreMenu2.showAsDropDown(imgView, 0, 20);
		} else {
			if (popMoreMenu2.isShowing()) {
				popMoreMenu2.dismiss();
			} else {
				popMoreMenu2.showAsDropDown(imgView, 0, 20);
			}
		}
	}

	private void refreshMap(LinearLayout selectalllayout){
		this.mMap.clear();
		int counts=selectalllayout.getChildCount(); 
		List<CheckBox> cbs=new LinkedList<CheckBox>();
		for(int i=0;i<counts;i++){
			CheckBox view=(CheckBox)selectalllayout.getChildAt(i);
				cbs.add(view);
		}
		int indexy=0;
		for (Map.Entry<String, LinkedList<AnalysisModel>> entry : currentException.entrySet()) { 
			String key=entry.getKey();
			boolean isShow=false;
			for(CheckBox cb:cbs){
				if(cb.isChecked()&&cb.getText().equals(key)){
					isShow=true;
					break;
				}
			}
			if(isShow){
				LinkedList<AnalysisModel> value = entry.getValue();
				
				for (AnalysisModel m : value) {
					// 定义Maker坐标点
					LatLng point = new LatLng(m.getLat(), m.getLon());
					// 构建Marker图标
					BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(this.getMapBmp(indexy));
					// 构建MarkerOption，用于在地图上添加Marker
					OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
					// 在地图上添加Marker，并显示
					this.mMap.addOverlay(option);
					
				} 
			}
			indexy+=1;
		}
	}
	/***
	 * 显示异常分类信息
	 */
	private void showInfo() {
		if (mapView != null) {
			mapView.onDestroy();
			mapView = null;
		}
		exceptionHeight.clear();
		layoutBig.removeAllViews();
		LinearLayout layoutInfo = (LinearLayout) getLayoutInflater().inflate(R.layout.analysis_main_detail_main, null);
		datadetailinfoscrollview=(ScrollView) layoutInfo.findViewById(R.id.datadetailinfoscrollview);
		final LinearLayout layout = (LinearLayout) layoutInfo.findViewById(R.id.datadetailinfo);
 
		
		layout.removeAllViews();
		mapLayout.clear();
		listLayout.clear(); 
		LinkedList<AnalysisModel> listAll = new LinkedList<AnalysisModel>();
		for (Map.Entry<String, LinkedList<AnalysisModel>> entry : currentException.entrySet()) {
			listAll.addAll(entry.getValue());
		}
		int heightx=0;
		for (final Map.Entry<String, LinkedList<AnalysisModel>> entry : currentException.entrySet()) {
			final LinearLayout layoutinfo1 = (LinearLayout) getLayoutInflater().inflate(R.layout.analysis_main_detail_1,
					null);
			layoutinfo1.measure(0,0);
			layoutinfo1.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(MotionEventCompat.getActionMasked(event)==MotionEvent.ACTION_DOWN){
						layoutinfo1.setBackgroundColor(Color.parseColor(blueColor));
					}else if(MotionEventCompat.getActionMasked(event)==MotionEvent.ACTION_UP){
						layoutinfo1.setBackgroundColor(Color.parseColor(defaultColor));
					}
//					else if(MotionEventCompat.getActionMasked(event)==MotionEvent.ACTION_MOVE){
//						layoutinfo1.setBackgroundColor(Color.parseColor(defaultColor));
//					}
					return true;
				}
			});
			TextView tv1 = (TextView) layoutinfo1.findViewById(R.id.keyvalue1);
			TextView tv2 = (TextView) layoutinfo1.findViewById(R.id.keyvalue2);
			tv1.setText(entry.getKey());
			tv2.setText(StringUtil.formatPercent(entry.getValue().size() / (float) listAll.size()+"")+"%");
			mapLayout.put(entry.getKey(), layoutinfo1);
			listLayout.add(layoutinfo1);
			layout.addView(layoutinfo1);
			exceptionHeight.put(entry.getKey(), heightx); 
			heightx+=layoutinfo1.getMeasuredHeight();
			for (int index=0;index<entry.getValue().size();index++) {
				final AnalysisModel m = entry.getValue().get(index);
				LinearLayout layoutinfo2 = (LinearLayout) getLayoutInflater().inflate(R.layout.analysis_main_detail_2,
						null);
				layoutinfo2.measure(0, 0);
				TextView tv11 = (TextView) layoutinfo2.findViewById(R.id.keyvalue11);
				TextView tv22 = (TextView) layoutinfo2.findViewById(R.id.keyvalue22);
				TextView tv33 = (TextView) layoutinfo2.findViewById(R.id.keyvalue33); 
				View tv44 = (View) layoutinfo2.findViewById(R.id.viewidx);
				if(index==entry.getValue().size()-1){
					tv44.setVisibility(View.GONE);
				}else{
					tv44.setVisibility(View.VISIBLE);
				}
				
				tv11.setText(m.getExceptionTime().split(" ")[1] + "");

				tv22.setText(getValue2(m) + "");

				tv33.setText(getValue3(m) + "");

				layout.addView(layoutinfo2);
				heightx+=layoutinfo2.getMeasuredHeight();
				layoutinfo2.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (currentScene.equals(AnalysisCommons.ANALYSIS_CSFB)
								|| currentScene.equals(AnalysisCommons.ANALYSIS_VOLTE)) {
							int faildType = -1;
							if (currentScene.equals(AnalysisCommons.ANALYSIS_CSFB)) {
								faildType = VoiceAnalyse.FAILD_TYPE_CSFB;
							} else if (currentScene.equals(AnalysisCommons.ANALYSIS_VOLTE)) {
								faildType = VoiceAnalyse.FAILD_TYPE_VOLTE;
							}
							m.setExceptionInfo(getValue3(m));
							new ExecAnalysis(faildType, m).execute();
						} else if (currentScene.equals(AnalysisCommons.ANALYSIS_LTEDATA)) {
							Bundle bundle = new Bundle();
							bundle.putSerializable("map", m);
							jumpActivity(LteDataDetaiActivity.class, bundle);
						}

					}
				});
			} 
		}
		layoutBig.addView(layoutInfo);
	}

	private String getValue2(AnalysisModel m) {
		if (m.getExceptionType() == AnalysisModel.TYPE_MO) {
			return (getString(R.string.csfb_faild_type_mo));
		} else if (m.getExceptionType() == AnalysisModel.TYPE_MT) {
			return (getString(R.string.csfb_faild_type_mt));
		} else if (m.getExceptionType() == AnalysisModel.TYPE_FTPDOWNLOAD) {
			return (getString(R.string.act_task_ftpdownload));
		} else {
			return (m.getExceptionType() + "");
		}
	}

	private String getValue3(AnalysisModel m) {
		if (currentScene.equals(AnalysisCommons.ANALYSIS_CSFB)) {//CSFB
			if (m.getExceptionCode() == 161) {
				return (getString(R.string.csfb_faild_161_0));
			} else if (m.getExceptionCode() == 194) {
				return (getString(R.string.csfb_faild_194_0A));
			} else if (m.getExceptionCode() == 195) {
				return getString(R.string.csfb_faild_195_0A);
			} else if (m.getExceptionCode() == 173) {
				return (getString(
						CsfbFaildAnalyse.CSFBException.getCSFBExceptionByReasion((int) m.getExceptionSubCode()).getShowNameId()));
			} else if (m.getExceptionCode() == 174) {
				return (getString(
						CsfbFaildAnalyse.CSFBReturnFaild.getCSFBReturnFaildByReasion((int) m.getExceptionSubCode()).getShowNameId()));
			} else if (m.getExceptionCode() == 175) {
				return getString(R.string.csfb_faild_175_0A);
			} else {
				return (m.getExceptionCode() + "");
			}
		} else if (currentScene.equals(AnalysisCommons.ANALYSIS_VOLTE)) {//VOLTE
			return (getString(VoLTEFaildAnalyse.VolteException.getVolteExceptionByReasion((int) m.getExceptionCode()).getShowNameId()));
		} else if (currentScene.equals(AnalysisCommons.ANALYSIS_LTEDATA)) {//LTEDATA
			return (m.getExceptionInfo());
		}
		return "";
	}

	/***
	 * 回放出所有的数据
	 * 
	 * @author weirong.fan
	 *
	 */
	private class ExecAnalysis extends AsyncTask<Void, Void, Boolean> {
		/** 进度提示 */
		private ProgressDialog progressDialog;
		/** 异常类型：csfb还是volte **/
		private int faildType = -1; 
		private AnalysisModel model;

		private ExecAnalysis(int faildType, AnalysisModel model) {
			super();
			this.faildType = faildType; 
			this.model = model;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute(); 
			this.openDialog(getString(R.string.intelligent_analysis_doing));

		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			this.closeDialog();
			if (result) {
				Bundle bundle = new Bundle();
				bundle.putInt("FAILD_TYPE", faildType); 
				bundle.putSerializable("map", model);
				bundle.putString("exceptionTime", model.getExceptionTime());
				jumpActivity(CsfbDetailActivity.class, bundle); 
			} else {
				ToastUtil.showToastShort(getApplicationContext(), "分析数据出现问题,请核查.");
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {

				VoiceAnalyse csfbFaild = VoiceAnalyse.getInstance(getApplicationContext());
				csfbFaild.setTransmissionDataMap(csfbFaild.getCsfbFaildResult(faildType, model.getDdibFile(), false));

				return true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return false;
		}

		/**
		 * 打开进度条
		 * 
		 * @param txt
		 */
		protected void openDialog(String txt) {
			progressDialog = new ProgressDialog(AnalysisDetailActivity.this);
			progressDialog.setMessage(txt);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		/**
		 * 关闭进度条
		 */
		protected void closeDialog() {
			progressDialog.dismiss();
		}

	}

	/**
	 * 显示百度地图
	 */
	private void showMap() {
		layoutBig.removeAllViews();
		RelativeLayout mapViewLayout = (RelativeLayout) getLayoutInflater()
				.inflate(R.layout.analysis_main_detail_main_map, null);
		layoutBig.addView(mapViewLayout);
		// 获取地图控件引用
		mapView = (MapView) mapViewLayout.findViewById(R.id.bmapView);
		if(null!=popMoreMenu2){
			popMoreMenu2.update();
			popMoreMenu2.dismiss();
			popMoreMenu2=null;
		}
		final ImageView imgView = (ImageView) mapViewLayout.findViewById(R.id.showexceptiontype);
		imgView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showExceptionType(imgView);
			}
		});

		LinearLayout exceptionTypeLayout = (LinearLayout) mapViewLayout.findViewById(R.id.showexceptiontypelayout);
		int index = 0;
		for (Map.Entry<String, LinkedList<AnalysisModel>> entry : currentException.entrySet()) {
			LinearLayout exceptionItem = (LinearLayout) getLayoutInflater()
					.inflate(R.layout.analysis_popview_exceptiontype_item, null);
			TextView tv1 = (TextView) exceptionItem.findViewById(R.id.exceptioncolor);
			TextView tv2 = (TextView) exceptionItem.findViewById(R.id.exceptioninfo);
			tv1.setBackgroundColor(Color.parseColor(pieAllColors.get(index++)));
			tv2.setText(entry.getKey());
			exceptionTypeLayout.addView(exceptionItem);
		}

		this.mMap = mapView.getMap();
		this.mMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		this.mMap.setMaxAndMinZoomLevel(19, 3);
		this.mMap.setMapStatus(MapStatusUpdateFactory.zoomTo(16));
		mapView.showZoomControls(false);
		this.setLocation();

		int indexy=0;
		for (Map.Entry<String, LinkedList<AnalysisModel>> entry : currentException.entrySet()) {
			LinkedList<AnalysisModel> value = entry.getValue();
			for (AnalysisModel m : value) {
				// 定义Maker坐标点
				LatLng point = new LatLng(m.getLat(), m.getLon());
				// 构建Marker图标
				BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(getMapBmp(indexy));
				// 构建MarkerOption，用于在地图上添加Marker
				OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
				// 在地图上添加Marker，并显示
				this.mMap.addOverlay(option);
			}
			indexy+=1;
		}

		// 定义Maker坐标点
		this.mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(final Marker marker) {
				mMap.hideInfoWindow();
				LatLng latlng = marker.getPosition();
				for (final Map.Entry<String, LinkedList<AnalysisModel>> entry : currentException.entrySet()) {
					LinkedList<AnalysisModel> value = entry.getValue();
					for (final AnalysisModel m : value) {
						if (latlng.latitude == m.getLat() && latlng.longitude == m.getLon()) {
							TextView tv = new TextView(context);
							StringBuffer sb = new StringBuffer();
							sb.append(getValue2(m) + "").append("\n");
//							sb.append("Lat:" + m.getLat() + "").append("\n");
//							sb.append("Lon:" + m.getLon() + "").append("\n");
							sb.append(getValue3(m) + "");
							tv.setText(sb.toString() + "");
							tv.setBackgroundResource(R.drawable.popup_middle);
							// 定义用于显示该InfoWindow的坐标点
							// 创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
							InfoWindow mInfoWindow = new InfoWindow(tv, marker.getPosition(), -47);
							// 显示InfoWindow
							mMap.showInfoWindow(mInfoWindow);
							
							tv.setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View v) {
									if (currentScene.equals(AnalysisCommons.ANALYSIS_CSFB)
											|| currentScene.equals(AnalysisCommons.ANALYSIS_VOLTE)) {
										int faildType = -1;
										if (currentScene.equals(AnalysisCommons.ANALYSIS_CSFB)) {
											faildType = VoiceAnalyse.FAILD_TYPE_CSFB;
										} else if (currentScene.equals(AnalysisCommons.ANALYSIS_VOLTE)) {
											faildType = VoiceAnalyse.FAILD_TYPE_VOLTE;
										}
										new ExecAnalysis(faildType, m).execute();
									} else if (currentScene.equals(AnalysisCommons.ANALYSIS_LTEDATA)) {
										Bundle bundle = new Bundle();
										bundle.putSerializable("map", m);
										jumpActivity(LteDataDetaiActivity.class, bundle);
									}
									
								}
							});
							break;
						}
					}
				}

				return false;
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mapView) {
			mapView.onDestroy();
			mapView = null;
		}
	}

	/**
	 * 定位当前位置
	 */
	private void setLocation() {
		// 开启定位图层
		mMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this.getApplicationContext());
		mLocClient.registerLocationListener(locationListener);
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		// 发起POI查询请求。请求过程是异步的，定位结果在上面的监听函数onReceivePoi中获取。
		if (mLocClient != null && mLocClient.isStarted()) {
			mLocClient.requestLocation();
		}
	}

	/**
	 * 定位SDK监听函数
	 */
	private class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || mMap == null)
				return;
			for (String key : currentException.keySet()) {
				LinkedList<AnalysisModel> listModel = currentException.get(key);
				if (null != listModel && listModel.size() > 0) {
					double lat = listModel.getFirst().getLat();
					double lon = listModel.getFirst().getLon();
					location.setLatitude(lat);
					location.setLongitude(lon);
					// 此处设置开发者获取到的方向信息，顺时针0-360
					MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(100)
							.latitude(lat).longitude(lon).build(); 
					mMap.setMyLocationData(locData);
					LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
					mMap.animateMapStatus(u);
					mLocClient.unRegisterLocationListener(locationListener);
					break;
				}
			}
		}

	}

	/***
	 * 解析结果数据文件
	 * 
	 * @return
	 * @throws JSONException
	 */
	private List<ResultJsonModel> parseResultJson() throws JSONException {
		List<ResultJsonModel> list = new LinkedList<ResultJsonModel>();
		File filePaths = new File(AppFilePathUtil.getInstance().getSDCardBaseDirectory(AnalysisCommons.ANALYSIS_PATH_ROOT,
				AnalysisCommons.ANALYSIS_PATH_ROOT_ANALYSIS,AnalysisCommons.ANALYSIS_PATH_ROOT_ANALYSIS_HISTORY));
		File[] files = filePaths.listFiles();
		List<File> listFiles = new LinkedList<File>();
		for (File f : files) {
			listFiles.add(f);
		}
		Collections.sort(listFiles, new Comparator<File>() {
			@Override
			public int compare(File obj1, File obj2) {
				if (obj1.isDirectory() && obj2.isFile()) {
					return 1;
				} else if (obj1.isFile() && obj2.isDirectory()) {
					return -1;
				} else {
					return obj1.getName().compareTo(obj2.getName());
				}
			}
		});
		for (File f : listFiles) {
			ResultJsonModel model = new ResultJsonModel();
			JSONObject reader = new JSONObject(
					FileUtil.getStringFromFile(AppFilePathUtil.getInstance().getSDCardBaseFile(AnalysisCommons.ANALYSIS_PATH_ROOT,
							AnalysisCommons.ANALYSIS_PATH_ROOT_ANALYSIS,AnalysisCommons.ANALYSIS_PATH_ROOT_ANALYSIS_HISTORY,
					f.getName(),AnalysisCommons.HISTORY_JSON_FILE_NAME)));
			model.setParentName(f.getName());
			model.setDataTime(reader.getString("Date"));
			model.setAllIsDT(reader.getInt("AllIsDT"));
			JSONArray array1 = reader.getJSONArray("Analysis");
			for (int i = 0; i < array1.length(); i++) {
				model.getAnalysis().add(array1.getString(i));
			}
			JSONArray array2 = reader.getJSONArray("Files");
			for (int i = 0; i < array2.length(); i++) {
				model.getFiles().add(array2.getString(i));
			}
			list.add(model);
		}
		return list;
	}

	private void initData(String dbFilePath) {
		AnalysisDBManage db = AnalysisDBManage.getInstance();
		try {
			db.openDatabase(dbFilePath);

			for (String scene : selectScenes) {
				if (scene.equalsIgnoreCase(AnalysisCommons.ANALYSIS_CSFB)) {
					doCSFBException(db);
				}
				if (scene.equalsIgnoreCase(AnalysisCommons.ANALYSIS_VOLTE)) {
					doVolteException(db);
				}
				if (scene.equalsIgnoreCase(AnalysisCommons.ANALYSIS_LTEDATA)) {
					doLTEDataException(db);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void doCSFBException(AnalysisDBManage db) {
		csfbException.clear();
		// 161--非csfb呼叫
		List<AnalysisModel> lists = db.getCSFBDetail("AVERAGE_161", "AVERAGE_161<1");
		String key = getString(R.string.csfb_faild_161_0);
		if (null != lists) {
			for (AnalysisModel m : lists) {
				m.setExceptionCode(161);
				if (csfbException.keySet().contains(key)) {
					csfbException.get(key).add(m);
				} else {
					LinkedList<AnalysisModel> list = new LinkedList<AnalysisModel>();
					list.add(m);
					csfbException.put(key, list);
				}
			}
		}
		// 173--CSFB配置无效
		lists = db.getCSFBDetail("AVERAGE_173", "(AVERAGE_173 in(0.0,5.0,6.0))");
		key = getString(R.string.intelligent_analysis_csfb_type_2);
		if (null != lists) {
			for (AnalysisModel m : lists) {
				m.setExceptionSubCode(m.getExceptionCode());
				m.setExceptionCode(173);
				if (csfbException.keySet().contains(key)) {
					csfbException.get(key).add(m);
				} else {
					LinkedList<AnalysisModel> list = new LinkedList<AnalysisModel>();
					list.add(m);
					csfbException.put(key, list);
				}
			}
		}
		// 173--等待Connect超时
		lists = db.getCSFBDetail("AVERAGE_173", "(AVERAGE_173 in(1.0))");
		key = getString(R.string.csfb_faild_173_1);
		if (null != lists) {
			for (AnalysisModel m : lists) {
				m.setExceptionSubCode(m.getExceptionCode());
				m.setExceptionCode(173);
				if (csfbException.keySet().contains(key)) {
					csfbException.get(key).add(m);
				} else {
					LinkedList<AnalysisModel> list = new LinkedList<AnalysisModel>();
					list.add(m);
					csfbException.put(key, list);
				}
			}
		}
		// 173--CSFB配置不合理
		lists = db.getCSFBDetail("AVERAGE_173", "(AVERAGE_173 in(2.0,3.0,4.0,7.0,8.0,9.0,10.0))");
		key = getString(R.string.csfb_faild_173_9);
		if (null != lists) {
			for (AnalysisModel m : lists) {
				m.setExceptionSubCode(m.getExceptionCode());
				m.setExceptionCode(173);
				if (csfbException.keySet().contains(key)) {
					csfbException.get(key).add(m);
				} else {
					LinkedList<AnalysisModel> list = new LinkedList<AnalysisModel>();
					list.add(m);
					csfbException.put(key, list);
				}
			}
		}

		// 174--未发起TAU
		lists = db.getCSFBDetail("AVERAGE_174", "(AVERAGE_174 in(2.0,3.0,4.0))");
		key = getString(R.string.csfb_faild_174_2);
		if (null != lists) {
			for (AnalysisModel m : lists) {
				m.setExceptionSubCode(m.getExceptionCode());
				m.setExceptionCode(174);
				if (csfbException.keySet().contains(key)) {
					csfbException.get(key).add(m);
				} else {
					LinkedList<AnalysisModel> list = new LinkedList<AnalysisModel>();
					list.add(m);
					csfbException.put(key, list);
				}
			}
		}
		// 174--TAU更新失败
		lists = db.getCSFBDetail("AVERAGE_174", "(AVERAGE_174 in(0.0,1.0,5.0,6.0))");
		key = getString(R.string.intelligent_analysis_csfb_type_3);
		if (null != lists) {
			for (AnalysisModel m : lists) {
				m.setExceptionSubCode(m.getExceptionCode());
				m.setExceptionCode(174);
				if (csfbException.keySet().contains(key)) {
					csfbException.get(key).add(m);
				} else {
					LinkedList<AnalysisModel> list = new LinkedList<AnalysisModel>();
					list.add(m);
					csfbException.put(key, list);
				}
			}
		}

		// 175--TAC和LAC不一致
		lists = db.getCSFBDetail("AVERAGE_175", "(AVERAGE_175>=0)");
		key = getString(R.string.csfb_faild_175_0A);
		if (null != lists) {
			for (AnalysisModel m : lists) {
				m.setExceptionCode(175);
				if (csfbException.keySet().contains(key)) {
					csfbException.get(key).add(m);
				} else {
					LinkedList<AnalysisModel> list = new LinkedList<AnalysisModel>();
					list.add(m);
					csfbException.put(key, list);
				}
			}
		}

		// 193--寻呼配置问题
		lists = db.getCSFBDetail("AVERAGE_193", "(AVERAGE_193>0)");
		key = getString(R.string.csfb_faild_193_1);
		if (null != lists) {
			for (AnalysisModel m : lists) {
				m.setExceptionCode(194);
				if (csfbException.keySet().contains(key)) {
					csfbException.get(key).add(m);
				} else {
					LinkedList<AnalysisModel> list = new LinkedList<AnalysisModel>();
					list.add(m);
					csfbException.put(key, list);
				}
			}
		} 
	}

	private void doVolteException(AnalysisDBManage db) {
		volteException.clear();
		List<AnalysisModel> lists = db.getVoLTEDetail();
		String key = "";
		for (AnalysisModel m : lists) {
			if (m.getExceptionCode() == 0x00030000) {// 非volte呼叫
				key = getString(R.string.volte_faild_30_00);
			} else if (m.getExceptionCode() >= 0x00021001 && m.getExceptionCode() <= 0x00021047) {// SIP信令丢失
				key = getString(R.string.volte_faild_21_22);
			} else if (m.getExceptionCode() >= 0x00022002 && m.getExceptionCode() <= 0x00022014) {// 掉话
				key = getString(R.string.volte_faild_22_14);
			}
			if (volteException.keySet().contains(key)) {
				volteException.get(key).add(m);
			} else {
				LinkedList<AnalysisModel> list = new LinkedList<AnalysisModel>();
				list.add(m);
				volteException.put(key, list);
			}
			key = null;
		}
	}

	private void doLTEDataException(AnalysisDBManage db) {
		ltedataException.clear();
		ltetotal.clear();
		List<AnalysisModel> lists = new LinkedList<AnalysisModel>();
		lists.addAll(db.getLTEDATAFtpDownload("PoorCover>0", getString(R.string.monitor_singalVerdict_PoorCov)));
		lists.addAll(
				db.getLTEDATAFtpDownload("IsLTEOverlap>0", getString(R.string.intelligent_analysis_ltedata_type_1)));
		lists.addAll(db.getLTEDATAFtpDownload("IsLTEMod3>0", getString(R.string.intelligent_analysis_ltedata_type_2)));
		lists.addAll(
				db.getLTEDATAFtpDownload("IsNoMainCell>0", getString(R.string.intelligent_analysis_ltedata_type_3)));
		lists.addAll(db.getLTEDATAFtpDownload("IsULIntf>0", getString(R.string.intelligent_analysis_ltedata_type_4)));
		lists.addAll(db.getLTEDATAFtpDownload("IsOutInterference>0",
				getString(R.string.intelligent_analysis_ltedata_type_5)));
		lists.addAll(
				db.getLTEDATAFtpDownload("IsNeighborIntf>0", getString(R.string.intelligent_analysis_ltedata_type_6)));
		lists.addAll(db.getLTEDATAFtpDownload("IsAntennaPortUnBlance>0",
				getString(R.string.intelligent_analysis_ltedata_type_7)));
		lists.addAll(db.getLTEDATAFtpDownload("IsResourceNotEnough>0",
				getString(R.string.intelligent_analysis_ltedata_type_8)));
		lists.addAll(db.getLTEDATAFtpDownload("LTENoCover>0", getString(R.string.intelligent_analysis_ltedata_type_9)));
		String key = getString(R.string.intelligent_analysis_ltedata_type);
		for (AnalysisModel m : lists) {
			ltetotal.add(m.getExceptionInfo());
			if (ltedataException.keySet().contains(key)) {
				ltedataException.get(key).add(m);
			} else {
				LinkedList<AnalysisModel> list = new LinkedList<AnalysisModel>();
				list.add(m);
				ltedataException.put(key, list);
			}
		}
		key = null;
	}
	
	private int getMapBmp(int indexy) {
		int resourceID = R.drawable.pin0;
		switch (indexy) {
		case 0:
			resourceID = R.drawable.pin0;
			break;
		case 1:
			resourceID = R.drawable.pin1;
			break;
		case 2:
			resourceID = R.drawable.pin2;
			break;
		case 3:
			resourceID = R.drawable.pin3;
			break;
		case 4:
			resourceID = R.drawable.pin4;
			break;
		case 5:
			resourceID = R.drawable.pin5;
			break;
		case 6:
			resourceID = R.drawable.pin6;
			break;
		case 7:
			resourceID = R.drawable.pin7;
			break;
		case 8:
			resourceID = R.drawable.pin8;
			break;
		case 9:
			resourceID = R.drawable.pin9;
			break;
		}
		return resourceID;
	}

	/**
	 * dp2px
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}
