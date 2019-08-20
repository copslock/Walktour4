package com.walktour.gui.task.activity.scanner.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;

import com.dingli.seegull.SeeGullFlags.ScanIDShow;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.DensityUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.gui.task.activity.scanner.model.RssiParseModel;

import java.util.ArrayList;

/**
 * Rssi显示页面
 * @author zhihui.lian
 */
public class ScanRssiInfoView extends BasicActivity implements RefreshEventListener{

	private HistogramView histogramView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initView();
		RefreshEventManager.addRefreshListener(this);
	}


	private void initView(){
		histogramView = new HistogramView(this);

		histogramView.setValueMin(-125);//纵坐标最小值
		histogramView.setValueMax(0);//纵坐标最大值
		histogramView.setOrdinateName("RSSI(dBm)");//纵坐标名字

		histogramView.setAbscissaName("Channel");//横坐标名字
		ArrayList<RssiParseModel> resultModel = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.ScanID_RSSI);
		histogramView.setItems(resultModel);

		histogramView.setChartType(0);//设置图表样式  柱状图/折线图
		histogramView.setItemNameTextOrientation(HistogramView.ORIENTATION_VERTICAL);//设置横坐标文字方向
		histogramView.setDisplayHorizontalGridLine(false);//
		histogramView.setDisplayVerticalGridLine(false);//
		histogramView.setAxisColor(Color.parseColor("#49D7E2"));//轴颜色
		histogramView.setAbscissaScaleColor(Color.parseColor("#49D7E2"));//横坐标刻度颜色
		histogramView.setOrdinateScaleColor(Color.parseColor("#49D7E2"));//纵坐标刻度颜色
		histogramView.setOrdinateValueTextColor(Color.parseColor("#ffffff"));//纵坐标数据颜色
		histogramView.setItemValueTextColor(Color.parseColor("#ffffff"));//当前值的颜色
		histogramView.setItemNameTextColor(Color.parseColor("#ffffff"));//横坐标数据颜色
		histogramView.setAbscissaNameTextColor(Color.parseColor("#ffffff"));
		histogramView.setOrdinateNameTextColor(Color.parseColor("#ffffff"));
		histogramView.setOrdinateHeight(DensityUtil.dip2px(this,250));//纵坐标高度
		histogramView.setChartTitle("CW Measurement");//表名
		histogramView.setDisplayAbscissaScale(true);
		histogramView.setDisplayOrdinateScale(true);
		histogramView.setChartColor(Color.parseColor("#49D7E2"));//设置画图的颜色
		setContentView(histogramView);
	}


	@Override
	public void onRefreshed(RefreshType refreshType, Object object) {
		 switch (refreshType) {
         case ACTION_WALKTOUR_TIMER_CHANGED:
             if(!ApplicationModel.getInstance().isFreezeScreen()){
            	 ArrayList<RssiParseModel> resultModel = TraceInfoInterface.traceData.getScanResultList(ScanIDShow.ScanID_RSSI);
            	 histogramView.setItems(resultModel);
            	 histogramView.invalidate();
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
