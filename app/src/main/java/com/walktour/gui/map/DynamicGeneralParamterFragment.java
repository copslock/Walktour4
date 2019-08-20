package com.walktour.gui.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.WalkStruct.NetType;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.gui.R;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 通用版动态参数显示
 * 
 * @author jianchao.wang
 *
 */
@SuppressLint({ "ValidFragment", "InflateParams" })
public class DynamicGeneralParamterFragment extends Fragment implements RefreshEventListener {

	/** 网络类型显示 */
	private TextView mNetTypeText;
	/** 参数名称显示数组 */
	private TextView mNameArray[] = new TextView[6];
	/** 参数值显示数组 */
	private TextView mValueArray[] = new TextView[6];
	/** 上下文 */
	private Context mContext;
	/** 折线图 */
	private ChartView mChartView;
	/** 折线图显示数值点数 */
	private final static int sChartValueSize = 10;
	/** 折线图的显示数值 */
	private Queue<Integer> mChartValues = new LinkedBlockingQueue<Integer>(sChartValueSize);
	/** 折线图的参数名 */
	private String mChartParamName;
	/** 最新的折线图值 */
	private int mCurrChartValue;
	/** 折线图的最大值 */
	private int mChartMaxValue;
	/** 折线图的最小值 */
	private int mChartMinValue;

	public DynamicGeneralParamterFragment(Context context) {
		mContext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dynamic_general_param_layout, null);
		initView(rootView);
		RefreshEventManager.addRefreshListener(this);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.createPublicData();
	}

	/**
	 * 初始化视图
	 * 
	 * @param view
	 */
	private void initView(View view) {
		LinearLayout chartViewlayout = (LinearLayout) view.findViewById(R.id.chart_view_layout);
		this.mNetTypeText = (TextView) view.findViewById(R.id.network_type);
		this.mNameArray[0] = (TextView) view.findViewById(R.id.param_left_name_1);
		this.mValueArray[0] = (TextView) view.findViewById(R.id.param_left_value_1);
		this.mNameArray[1] = (TextView) view.findViewById(R.id.param_left_name_2);
		this.mValueArray[1] = (TextView) view.findViewById(R.id.param_left_value_2);
		this.mNameArray[2] = (TextView) view.findViewById(R.id.param_right_name_1);
		this.mValueArray[2] = (TextView) view.findViewById(R.id.param_right_value_1);
		this.mNameArray[3] = (TextView) view.findViewById(R.id.param_right_name_2);
		this.mValueArray[3] = (TextView) view.findViewById(R.id.param_right_value_2);
		this.mNameArray[4] = (TextView) view.findViewById(R.id.param_right_name_3);
		this.mValueArray[4] = (TextView) view.findViewById(R.id.param_right_value_3);
		this.mNameArray[5] = (TextView) view.findViewById(R.id.param_right_name_4);
		this.mValueArray[5] = (TextView) view.findViewById(R.id.param_right_value_4);
		this.mChartView = new ChartView(mContext);
		chartViewlayout.addView(this.mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

	}

	/**
	 * 填固定公共值
	 * 
	 */

	private void createPublicData() {
		NetType netType = MyPhoneState.getInstance().getCurrentNetType(this.mContext);
		String networkName = "UNKNOWN";
		String[] pubicParamDatas = new String[] { "-", "-", "-", "-", "-", "-" };
		String[] publicParamNames = new String[] { "RSRP", "MCC/MNC", "TAC", "ECI", "SINR", "PCI" };
		switch (netType) {
		case GSM:
			networkName = "GSM";
			pubicParamDatas = new String[] { TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_Cell_ID),

					TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_MCC).concat("/")
							.concat(TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_MNC)),
					TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_LAC), "", "", "" };
			publicParamNames = new String[] { "Cell ID", "MCC/MNC", "LAC", "", "", "" };
			this.mChartParamName = "RxLev";
			this.setChartValue(UnifyParaID.G_Ser_RxLevFull);
			this.mChartMaxValue = -47;
			this.mChartMinValue = -110;
			break;
		case CDMA:
			networkName = "CDMA";
			pubicParamDatas = new String[] { TraceInfoInterface.getParaValue(UnifyParaID.C_NID),
					TraceInfoInterface.getParaValue(UnifyParaID.C_Frequency),
					TraceInfoInterface.getParaValue(UnifyParaID.C_TotalEcIo), TraceInfoInterface.getParaValue(UnifyParaID.C_SID),
					TraceInfoInterface.getParaValue(UnifyParaID.C_ReferencePN), "" };
			publicParamNames = new String[] { "NID", "Freq", "Total EcIo", "SID", "PN", "" };
			this.mChartParamName = "RxAGC";
			this.setChartValue(UnifyParaID.C_RxAGC);
			this.mChartMaxValue = -30;
			this.mChartMinValue = -130;
			break;
		case EVDO:
			networkName = "EVDO";
			pubicParamDatas = new String[] { TraceInfoInterface.getParaValue(UnifyParaID.C_NID),
					TraceInfoInterface.getParaValue(UnifyParaID.C_Frequency),
					TraceInfoInterface.getParaValue(UnifyParaID.C_TotalEcIo), TraceInfoInterface.getParaValue(UnifyParaID.C_SID),
					TraceInfoInterface.getParaValue(UnifyParaID.C_ReferencePN), "" };
			publicParamNames = new String[] { "NID", "Freq", "Total EcIo", "SID", "PN", "" };
			this.mChartParamName = "RxAGC";
			this.setChartValue(UnifyParaID.C_RxAGC);
			this.mChartMaxValue = -30;
			this.mChartMinValue = -130;
			break;
		case WCDMA:
			networkName = "WCDMA";
			pubicParamDatas = new String[] { TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_Cell_ID),
					TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_MCC).concat("/")
							.concat(TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_MNC)),
					TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_Max_EcIo),
					TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_LAC),
					TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_Max_PSC), "" };
			publicParamNames = new String[] { "Cell ID", "MCC/MNC", "EcIo", "LAC", "PSC", "" };
			this.mChartParamName = "RSCP";
			this.setChartValue(UnifyParaID.W_Ser_Total_RSCP);
			this.mChartMaxValue = -25;
			this.mChartMinValue = -124;
			break;
		case TDSCDMA:
			networkName = "TDSCDMA";
			pubicParamDatas = new String[] { TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_CellID),
					TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_MCC).concat("/")
							.concat(TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_MNC)),
					TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_LAC),
					TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_UARFCN),
					TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_CPI), "" };
			publicParamNames = new String[] { "Cell ID", "MCC/MNC", "LAC", "UARFCN", "CPI", "" };
			this.mChartParamName = "RSCP";
			this.setChartValue(UnifyParaID.TD_DPA1_A_DPCH_RSCP);
			this.mChartMaxValue = -25;
			this.mChartMinValue = -115;
			break;
		case LTE:
			networkName = "LTE";
			pubicParamDatas = new String[] { TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_ECGI),
					TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_MCC).concat("/")
							.concat(TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_MNC)),
					TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_TAC),
					TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_RSRP),
					TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_SINR),
					TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_PCI) };
			publicParamNames = new String[] { "ECI", "MCC/MNC", "TAC", "RSRP", "SINR", "PCI" };
			this.mChartParamName = "RSRP";
			this.setChartValue(UnifyParaID.L_SRV_RSRP);
			this.mChartMaxValue = -47;
			this.mChartMinValue = -110;
			break;
		default:
			networkName = "UNKNOWN";
			this.mChartMaxValue = -40;
			this.mChartMinValue = -141;
			break;
		}
		if (mNetTypeText != null) {
			mNetTypeText.setText(networkName);
			for (int i = 0; i < mNameArray.length; i++) {
				mNameArray[i].setText(publicParamNames[i]);
			}
			for (int i = 0; i < mValueArray.length; i++) {
				mValueArray[i].setText(pubicParamDatas[i]);
			}
		}
	}

	/**
	 * 设置折线图数组
	 * 
	 * @param paramId
	 *          参数ID
	 */
	private void setChartValue(int paramId) {
		String value = TraceInfoInterface.getParaValue(paramId);
		if (StringUtil.isInteger(value)) {
			if (this.mChartValues.size() == sChartValueSize)
				this.mChartValues.remove();
			this.mCurrChartValue = Integer.parseInt(value);
			this.mChartValues.add(this.mCurrChartValue);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		RefreshEventManager.removeRefreshListener(this);
	}

	@Override
	public void onRefreshed(RefreshType refreshType, Object object) {
		switch (refreshType) {
		case ACTION_WALKTOUR_TIMER_CHANGED:
			createPublicData();
			this.mChartView.invalidate();
			break;
		default:
			break;
		}
	}

	/**
	 * 折线图
	 * 
	 * @author jianchao.wang
	 *
	 */
	@SuppressLint("DrawAllocation")
	private class ChartView extends ImageView {
		/** 缩放比例 */
		private float mDensity = 1;
		/** 表格宽度 */
		private float tableWidth = 0;
		/** 表格高度 */
		private float tableHeight = 0;
		/** 行数 */
		private int tableRows = 15;
		/** 列数 */
		private int tableCols = 10;
		/** 字体大小 */
		private float textSize = 12;
		/** 显示工具 */
		private DisplayMetrics metric;
		/** 折线图虚线格行高 */
		private float rowHeight = 0;
		/** 折线图虚线格列宽 */
		private float colWidth = 0;

		public ChartView(Context context) {
			super(context);
			metric = this.getResources().getDisplayMetrics();
			mDensity = metric.density;
			this.textSize *= mDensity;
			this.setBackgroundColor(getResources().getColor(R.color.app_main_bg_color));
		}

		@Override
		protected void onDraw(Canvas canvas) {
			this.tableWidth = this.getWidth() - 12;
			this.tableHeight = this.getHeight() - 12;
			this.rowHeight = tableHeight / tableRows;
			this.colWidth = tableWidth / tableCols;
			this.drawParam(canvas);
			this.drawChart(canvas);
			canvas.save();
			canvas.restore();
		}

		private void drawChart(Canvas canvas) {

			// 绘制折线图三边实线框
			Paint paint = new Paint();
			paint.setStyle(Paint.Style.FILL);
			paint.setTypeface(null);
			paint.setColor(Color.GRAY);
			canvas.drawLine(colWidth, rowHeight * 2, colWidth, this.tableHeight, paint);
			canvas.drawLine(colWidth, this.tableHeight, this.tableWidth, this.tableHeight, paint);
			canvas.drawLine(this.tableWidth, rowHeight * 2, this.tableWidth, this.tableHeight, paint);
			// 绘制网格虚线
			paint = new Paint();
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.GRAY);
			paint.setTypeface(null);
			PathEffect e = new DashPathEffect(new float[] { 3, 5, 3, 5 }, 1);
			paint.setPathEffect(e);
			paint.setStrokeWidth(1f);
			Path p = new Path();
			// 竖虚线
			for (int i = 2; i < this.tableCols; i++) {
				p.moveTo(colWidth * i, rowHeight * 2);
				p.lineTo(colWidth * i, this.tableHeight);
			}
			// 横虚线
			for (int i = 2; i < this.tableRows; i++) {
				p.moveTo(colWidth, rowHeight * i);
				p.lineTo(this.tableWidth, rowHeight * i);
			}
			canvas.drawPath(p, paint);
			// 绘制坐标
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStyle(Paint.Style.FILL);
			paint.setTypeface(null);
			paint.setTextSize(textSize);
			paint.setColor(Color.GRAY);
			// 图表参数信息
			int step = (mChartMaxValue - mChartMinValue) / (this.tableRows - 3);
			int yAxesMaxV = mChartMaxValue;
			int yAxesMinV = -130;
			for (int i = 0; i < this.tableRows - 1; i++) {
				float x = 0;
				float y = rowHeight * (2 + i) + this.textSize / 2;
				if (i == this.tableRows - 2)
					yAxesMinV = yAxesMaxV - step * i;
				canvas.drawText(String.valueOf(yAxesMaxV - step * i), x, y, paint);
			}
			// 图表折线
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStyle(Paint.Style.STROKE);
			paint.setTypeface(null);
			paint.setStrokeWidth(3);
			paint.setColor(getResources().getColor(R.color.app_param_color));
			Path path = new Path();
			boolean isPointStart = true;
			Integer[] values = mChartValues.toArray(new Integer[mChartValues.size()]);
			for (int i = 0; i < values.length; i++) {
				int chartValue = values[i];
				if (chartValue < -1) {
					if (chartValue > yAxesMaxV)
						chartValue = yAxesMaxV;
					else if (chartValue < yAxesMinV)
						chartValue = yAxesMinV;
					float startx = colWidth * (i + 1);
					float starty = rowHeight * 2 + ((float) (yAxesMaxV - chartValue) / (float) (yAxesMaxV - yAxesMinV))
							* (rowHeight * (this.tableRows - 2));
					if (isPointStart) {
						path.moveTo(startx, starty);
						isPointStart = false;
					} else {
						path.lineTo(startx, starty);
					}
				} else if (!isPointStart) {
					isPointStart = true;
					canvas.drawPath(path, paint);
				}
			}
			canvas.drawPath(path, paint);
		}

		/**
		 * 绘制参数框
		 * 
		 * @param canvas
		 */
		private void drawParam(Canvas canvas) {
			// 参数值显示外框
			Paint paint = new Paint();
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(1f);
			paint.setColor(getResources().getColor(R.color.black));
			PathEffect e = new DashPathEffect(new float[] { 3, 5, 3, 5 }, 1);
			paint.setPathEffect(e);
			float height = this.rowHeight * (float) 1.5;
			canvas.drawRoundRect(new RectF(0, 0, this.tableWidth, height), 0, 0, paint);
			paint = new Paint();
			paint.setStyle(Paint.Style.FILL);
			paint.setTextSize(20 * mDensity);
			paint.setColor(getResources().getColor(R.color.app_param_color));
			String valueStr = mChartParamName + ":" + mCurrChartValue;
			canvas.drawText(valueStr, 10, height / 2 + 10 * mDensity, paint);
		}

	}

}
