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
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.TypeConver;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.NetType;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.Deviceinfo;
import com.walktour.control.config.ParameterSetting;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.gui.R;
import com.walktour.model.AlarmModel;
import com.walktour.model.ChartPointModel;
import com.walktour.model.Parameter;

import java.util.Hashtable;
import java.util.Queue;

/**
 * 全参数图表视图类
 *
 * @author tangwq
 */
public class ChartView extends android.support.v7.widget.AppCompatImageView implements RefreshEventListener {

	private float rate = 1;

	int tableRows = 18; // 行数

	int tableCols = 4; // 列数

	float strokeWidth = 1;

	float textSize = 9; // 字体大小

	private int page = 1;

	private DisplayMetrics metric;
	/**
	 * 显示的曲线最大值
	 */
	private int lineMaxSize = 10;

	private Parameter[] chartLines = new Parameter[6];
	private boolean is5G=false;
	public ChartView(Context context, int page)
	{
		super(context);
		this.page = page;
		metric = this.getResources().getDisplayMetrics();
		initData();
		this.setBackgroundColor(getResources().getColor(R.color.app_main_bg_color));
	}

	public ChartView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		initData();
		if (page == 1) {
			CreateChart(canvas);
		} else {
			CreateTable(canvas);
			CreateTableData(canvas, TraceInfoInterface.traceData);
		}
	}

	private void initData(){
		is5G = TraceInfoInterface.currentNetType == WalkStruct.CurrentNetState.ENDC;
		Parameter[] parameters=ParameterSetting.getInstance().getChartLineParemeters();
		if(is5G){
			for(int i=2;i<parameters.length;i++){
				if(i>=8)//最多画6个参数
					break;
				if(i==4||i==5){
					if(!parameters[i].getShowName().contains("(L)")) {
						parameters[i].setShowName("(L)" + parameters[i].getShowName());
					}
				}else if(i==6||i==7){
					if(!parameters[i].getShowName().contains("(N)")) {
						parameters[i].setShowName("(N)" + parameters[i].getShowName());
					}
				}
				chartLines[i-2]=parameters[i];
			}
		}else{
			for(int i=0;i<parameters.length;i++){
				if(i>=6)//最多画6个参数
					break;
				if(i==4||i==5){
					if(!parameters[i].getShowName().contains("(L)")) {
						parameters[i].setShowName("(L)" + parameters[i].getShowName());
					}
				}
				chartLines[i]=parameters[i];
			}
		}
	}
	/**
	 * 创建表格
	 *
	 * @param bm 要创建表格的位图
	 * @return 输出位图
	 */
	protected void CreateTable(Canvas cv)
	{
		int width = this.getWidth();
		int height = this.getHeight();
		float marginSize = 1 * rate;
		float startx = 0;
		float starty = 0;
		float stopx = 0;
		float stopy = 0;
		float tablewidth = width;
		float tableheight = height;
		float rowsHeight = tableheight / tableRows; // 行高
		float colsWidth = tablewidth / tableCols; // 列宽
		float lableAddWidth = colsWidth / 4; // 标签列加宽
		Paint paint = new Paint();
		paint.setColor(getResources().getColor(R.color.legend));
		paint.setStrokeWidth(strokeWidth);

		// 四周边框
		cv.drawLine(0, marginSize, width - marginSize, marginSize, paint); // 上横线
		cv.drawLine(0, rowsHeight * (tableRows - 1) + marginSize, width - marginSize,
				rowsHeight * (tableRows - 1)
						+ marginSize, paint); // 下横线
		cv.drawLine(0, marginSize, 0, rowsHeight * (tableRows - 1) + marginSize, paint); // 左竖线
		cv.drawLine(width - marginSize, marginSize, width - marginSize,
				rowsHeight * (tableRows - 1) + marginSize, paint);// 右竖线
		// 横线
		for (int i = 0; i < (tableRows - 1) - 1; i++) {
			startx = 0;
			starty = rowsHeight * (i + 1);
			stopx = width - marginSize;
			stopy = rowsHeight * (i + 1);
			cv.drawLine(startx, starty, stopx, stopy, paint);
		}
		// Serving cell竖线
		for (int i = 0; i < tableCols - 1; i++) {
			// if(i%3 != 0){
			startx = colsWidth * (i + 1) + (i % 2 == 0 ? lableAddWidth : 0);
			starty = marginSize + rowsHeight;
			stopx = colsWidth * (i + 1) + (i % 2 == 0 ? lableAddWidth : 0);
			stopy = rowsHeight * (tableRows - 1) + marginSize;
			cv.drawLine(startx, starty, stopx, stopy, paint);
			// }
		}
		paint.setColor(getResources().getColor(R.color.app_main_text_color));
		paint.setTextSize(textSize * rate);
		String title = getResources().getString(R.string.sys_chart_custom_param);
		cv.drawText(title, (this.getWidth() - paint.measureText(title)) / 2, rowsHeight
				- (rowsHeight - paint.getTextSize()) / 2, paint);
		cv.save();
		cv.restore();
	}

	/**
	 * 创建表格数据
	 *
	 * @param bm   要创建表格的位图
	 * @param data 表格数据
	 * @return 输出位图
	 */
	protected void CreateTableData(Canvas cv, TraceInfoData traceData)
	{
		int width = this.getWidth();
		int height = this.getHeight();
		float tablewidth = width;
		float tableheight = height;
		float rowsHeight = tableheight / tableRows; // 行高
		float colsWidth = tablewidth / tableCols; // 列宽
		float rowUpBit = (rowsHeight - textSize) / 2.5f; // 指定行上升位数,为行高-字体高度 再除2
		float lableAddWidth = colsWidth / 4; // 标签列加宽
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(getResources().getColor(R.color.app_param_color));
		paint.setTypeface(null);
		paint.setTextSize(textSize * rate);
		String value;

		Parameter[] chartTables = ParameterSetting.getInstance().getTableParameters();
		int rows = 0;
		for (int i = 0; i < (chartTables.length > (tableRows - 2) * 2 ? (tableRows - 2) * 2 :
				chartTables.length); i++) {
			if (i % 2 == 0) {
				rows++;
			}

			paint.setColor(getResources().getColor(R.color.app_main_text_color));
			value = chartTables[i].getShowName();
			cv.drawText(value,
					colsWidth * (i % 2 * 2) + (colsWidth + lableAddWidth - paint.measureText(value)) / 2,
					rowsHeight * (1 + rows) - rowUpBit, paint);

			paint.setColor(getResources().getColor(R.color.app_param_color));
			value = getValueByIdForSpecial(Integer.parseInt(chartTables[i].getId(), 16));

			if (chartTables[i].getScale() > 1 && value.trim().length() > 0) {
				try {
					value =
							UtilsMethod.decFormat.format(Float.parseFloat(value.trim()) / chartTables[i].getScale());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			cv.drawText(value,
					colsWidth * (i % 2 * 2 + 1) + (colsWidth + lableAddWidth - paint.measureText(value)) / 2,
					rowsHeight * (1 + rows) - rowUpBit, paint);
		}
		cv.save();
		cv.restore();
	}

	protected void CreateChart(Canvas canvas)
	{
		createPublicData(canvas);
		createParamsData(canvas);
		if (ApplicationModel.getInstance().isGeneralMode())
			CreateColumChart(canvas);
		else if (TraceInfoInterface.chartCurrentPage == 1)
			if(is5G){
				CreateLineChart5G(canvas);
			}else {
				CreateLineChart(canvas);
			}
		else
			CreateColumChart(canvas);
	}

	private void createParamsData(Canvas cv) {
		int height = this.getHeight();
		float tableheight = height - 12;
		float rowsHeight = tableheight / tableRows; // 行高
		float yAxesWidth = /* colsWidth / 3 */8 * rate;
		float rowUpBit = (rowsHeight - textSize * rate) / 2; // 指定行上升位数,为行高-字体高度// 再除2

		int paramsRow = 0;
		if (chartLines.length % 2 == 0)
			paramsRow = chartLines.length / 2;
		else
			paramsRow = (chartLines.length + 1) / 2;
		float paramsRowHeight = (rowsHeight * 4 - rowsHeight / 2) / 5;
		float curveStartY = rowsHeight * 5 + paramsRowHeight * paramsRow + rowsHeight / 2;//// 折线图起始Y坐标


		Paint paint = new Paint();
		// paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		paint.setTypeface(null);
		paint.setTextSize(textSize * rate);

		paint.setColor(Color.GRAY);

		// 参数值显示外框
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1f);
		paint.setColor(getResources().getColor(R.color.black));
		PathEffect e = new DashPathEffect(new float[]{3, 5, 3, 5}, 1);
		paint.setPathEffect(e);
		float marginLR = (20 * metric.density);
		float publicParamRowHeight = rowsHeight * 2 / 3;
		cv.drawRoundRect(new RectF(yAxesWidth, rowsHeight * 5, this.getWidth() - yAxesWidth,
				rowsHeight * 5
						+ paramsRowHeight * paramsRow), 10, 10, paint);

//		Hashtable<String, Queue<ChartPointModel>> chartLineQ =
//				TraceInfoInterface.traceData.getChartLineQ();
		int rowNum = 0;
		paint=null;
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setTypeface(null);
		paint.setTextSize(textSize * rate);
		for (int i = 0; i < chartLines.length; i++) {
			// 当返回的数据队列中包含系统图表设置的关键值时,显示该数据队列信息
//			if (chartLineQ.containsKey(chartLines[i].getId())) {


				if (i % 2 == 0 && i != 0)
					rowNum++;
				paint.setColor(chartLines[i].getColor());

				// 参数值显示
				String valueStr = chartLines[i].getShowName()
						+ ":"
						+ TraceInfoInterface.getFloatParaValue(Integer.parseInt(chartLines[i].getId(), 16),
						chartLines[i].getScale());
				float x = marginLR + (this.getWidth() / 2) * (i % 2);
				float y =
						rowsHeight * 6 + publicParamRowHeight * (i / 2) - (publicParamRowHeight - textSize * rate) / 2;
				y -= rowUpBit;
				cv.drawText(valueStr, x, y, paint);
//			}
		}
	}

	/**
	 * 通过参数keyid获得相应的值 如果值在特殊列表中，需对值作进一步转换 注意：当前函数值不作文字转换
	 *
	 * @param keyId
	 * @return
	 */
	private String getValueByIdForSpecial(int keyId)
	{
		String value = TraceInfoInterface.getParaValue(keyId);
		switch (keyId) {
			case UnifyParaID.W_Ser_Cell_ID:
				value = UtilsMethod.getLongTosShortCellID(value);
				break;
		}

		return value;
	}

	private void createPublicData(Canvas canvas)
	{
		WalkStruct.CurrentNetState netType = !ApplicationModel.getInstance().isFreezeScreen() ? TraceInfoInterface.currentNetType
				: TraceInfoInterface.decodeFreezeNetType;
		float marginLR = 20 * metric.density;
		float marginTB = 10 * metric.density;
		float rowsHeight = ((this.getHeight() - 12) / tableRows * 5 - marginTB * 2) / 4; // 行高
		String networkName = "UNKNOWN";
		String[] pubicParamDatas = new String[]{"-", "-", "-", "-", "-", "-"};
		String[] publicParamNames = new String[]{"LAC", "Cell ID", "RxLevFul", "BCCH", "MCC/MNC",
				"BSIC"};
		switch (netType) {
			case GSM:
				networkName = "GSM";
				pubicParamDatas = new String[]{
						TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_LAC),
						TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_Cell_ID),
						TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_RxLevFull),
						TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_BCCH),
						TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_MCC).concat("/")
								.concat(TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_MNC)),
						TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_BSIC)};
				publicParamNames = new String[]{"LAC", "Cell ID", "RxLevFul", "BCCH", "MCC/MNC",
						"BSIC"};
				break;
			case CDMA:
				networkName = "CDMA";
				pubicParamDatas = new String[]{TraceInfoInterface.getParaValue(UnifyParaID.C_NID),
						TraceInfoInterface.getParaValue(UnifyParaID.C_BID)
						, TraceInfoInterface.getParaValue(UnifyParaID.C_TotalEcIo),
						TraceInfoInterface.getParaValue(UnifyParaID.C_Frequency), TraceInfoInterface.getParaValue(UnifyParaID.C_SID),
						TraceInfoInterface.getParaValue(UnifyParaID.C_ReferencePN)};
				publicParamNames = new String[]{"NID", "BID", "Total EcIo", "Freq", "SID", "PN"};
				break;
			case WCDMA:
				networkName = "WCDMA";
				pubicParamDatas = new String[]{
						TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_LAC),
						UtilsMethod.getLongTosShortCellID(TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_Cell_ID)),
						TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_Total_RSCP),
						TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_DL_UARFCN),
						TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_MCC).concat("/")
								.concat(TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_MNC)),
						TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_Max_PSC)};
				publicParamNames = new String[]{"LAC", "Cell ID", "RSCP", "UARFCN", "MCC/MNC",
						"PSC"};
				break;
			case TDSCDMA:
				networkName = "TDSCDMA";
				pubicParamDatas = new String[]{
						TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_LAC),
						TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_CellID),
						TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_PCCPCHRSCP),
						TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_UARFCN),
						TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_MCC).concat("/")
								.concat(TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_MNC)),
						TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_CPI)};
				publicParamNames = new String[]{"LAC", "Cell ID", "RSCP", "UARFCN", "MCC/MNC",
						"CPI"};
				break;
			case LTE:
				networkName = "LTE";
				if (ApplicationModel.getInstance().isNBTest()) {
					networkName = "NBIot";
				}
				String eci1 = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_ECIP1);
				String eci2 = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_ECIP2);
				String eci3 = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_ECIP3);
				String eci = eci1 + (eci2.length() == 0 ? "" : "," + eci2) + (eci3.length() == 0
						? "" : "," + eci3);
				pubicParamDatas = new String[]{
						TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_TAC),
						eci,
						TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_RSRP),
						TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_EARFCN),
						TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_MCC).concat("/")
								.concat(TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_MNC)),
						TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_PCI)};
				publicParamNames = new String[]{"TAC", "ECI", "RSRP", "EARFCN", "MCC/MNC", "PCI"};
				break;
			case ENDC:
				networkName = "ENDC-NR";
				eci1 = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_ECIP1);
				eci2 = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_ECIP2);
				eci3 = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_ECIP3);
				eci = eci1 + (eci2.length() == 0 ? "" : "," + eci2) + (eci3.length() == 0
						? "" : "," + eci3);
				pubicParamDatas = new String[]{
						TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_MCC).concat("/")
								.concat(TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_MNC)),

						TraceInfoInterface.getParaValue(UnifyParaID.NR_PCI),
						TraceInfoInterface.getParaValue(UnifyParaID.NR_SS_RSRP),
						TraceInfoInterface.getParaValue(UnifyParaID.NR_PointA_ARFCN),
						TraceInfoInterface.getParaValue(UnifyParaID.NR_SS_SINR),
						TraceInfoInterface.getParaValue(UnifyParaID.NR_SSB_ARFCN)};
				publicParamNames = new String[]{"MCC/MNC", "PCI", "SS-RSRP", "PointA EARFCN", "SS-SINR", "SSB ARFCN"};
				break;
			default:
				networkName = "UNKNOWN";
				break;
		}
		Paint paint = new Paint();
		paint.setTextSize(14 * metric.density);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(getResources().getColor(R.color.app_main_text_color));
		paint.setTypeface(null);
		float rightColStartX = this.getWidth() / 2;// + marginRT + marginLR;
		for (int i = 0, row = 0; i < publicParamNames.length && i < 6; i++) {
			if (i < 2 || i % 2 == 0) {
				row++;
			}
			float rowStartY = marginTB + rowsHeight * row - (rowsHeight - paint.getTextSize()) / 2;
			paint.setColor(getResources().getColor(R.color.app_main_text_color));
			if (i < 2 || i % 2 == 1) {
				canvas.drawText(publicParamNames[i], rightColStartX, rowStartY, paint);
				paint.setColor(getResources().getColor(R.color.app_param_color));
				canvas.drawText(pubicParamDatas[i],
						rightColStartX + paint.measureText(publicParamNames[i]) + 10, rowStartY,
						paint);
			} else {
				canvas.drawText(publicParamNames[i], marginLR, rowStartY, paint);
				paint.setColor(getResources().getColor(R.color.app_param_color));
				canvas.drawText(pubicParamDatas[i],
						marginLR + paint.measureText(publicParamNames[i]) + 10, rowStartY, paint);
			}
		}
		paint.setTextSize(20 * metric.density);
		Typeface font = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD);
		paint.setTypeface(font);
		canvas.drawText(networkName, marginLR,
				marginTB + rowsHeight * 2 - (rowsHeight * 2 - paint.getTextSize()) / 2 - 8
						* metric.density, paint);
	}

	/**
	 * 创建曲线图
	 *
	 * @return 输出位图
	 */
	protected void CreateLineChart(Canvas cv)
	{
		int width = this.getWidth();
		int height = this.getHeight();
		float tablewidth = width;
		float tableheight = height - 12;
		float rowsHeight = tableheight / tableRows; // 行高
		// float colsWidth = tablewidth / 6; // 列宽
		int lineColsNum = 40;
		int lineRowsNum = 13;
		float yAxesWidth = /* colsWidth / 3 */8 * rate;
		float lineColsWidth = (tablewidth - yAxesWidth * 2) / lineColsNum;
		float rowUpBit = (rowsHeight - textSize * rate) / 2; // 指定行上升位数,为行高-字体高度
		// 再除2

		int paramsRow = 0;
		if (chartLines.length % 2 == 0)
			paramsRow = chartLines.length / 2;
		else
			paramsRow = (chartLines.length + 1) / 2;
		float paramsRowHeight = (rowsHeight * 4 - rowsHeight / 2) / 5;
		float curveStartY = rowsHeight * 5 + paramsRowHeight * paramsRow + rowsHeight / 2;//
		// 折线图起始Y坐标
		float lineRowsHeight = (rowsHeight * 18 - curveStartY) / lineRowsNum;

		Paint paint = new Paint();
		// paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		paint.setTypeface(null);
		paint.setTextSize(textSize * rate);

		paint.setColor(Color.GRAY);
		cv.drawLine(yAxesWidth, curveStartY, yAxesWidth, rowsHeight * 18, paint);
		cv.drawLine(yAxesWidth, rowsHeight * 18, width - yAxesWidth, rowsHeight * 18, paint);
		cv.drawLine(width - yAxesWidth, curveStartY, width - yAxesWidth, rowsHeight * 18, paint);

		// 参数值显示外框
		float startx = 0;
		float starty = 0;
		float stopx = 0;
		float stopy = 0;
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1f);
		paint.setColor(getResources().getColor(R.color.black));
		PathEffect e = new DashPathEffect(new float[]{3, 5, 3, 5}, 1);
		paint.setPathEffect(e);
		Path p = new Path();
		for (int i = 1; i < lineColsNum; i++) // 竖虚线
		{
			if (i % 4 == 0) {
				startx = yAxesWidth + lineColsWidth * i;
				starty = curveStartY;
				stopx = yAxesWidth + lineColsWidth * i;
				stopy = rowsHeight * 18;

				p.moveTo(startx, starty);
				p.lineTo(stopx, stopy);
			}

		}
		for (int i = 0; i < lineRowsNum; i++) // 横虚线
		{
			startx = yAxesWidth;
			starty = curveStartY + lineRowsHeight * i;
			stopx = width - yAxesWidth;
			stopy = curveStartY + lineRowsHeight * i;
			p.moveTo(startx, starty);
			p.lineTo(stopx, stopy);

		}
		cv.drawPath(p, paint);

		Hashtable<String, Queue<ChartPointModel>> chartLineQ =
				TraceInfoInterface.traceData.getChartLineQ();

		// float labelWidth = (tablewidth - colsWidth * 1) / (chartLines.length != 0
		// ? chartLines.length : 1);
		int rowNum = 0;
		for (int i = 0; i < (chartLines.length > this.lineMaxSize ? this.lineMaxSize :
				chartLines.length); i++) {
			// 当返回的数据队列中包含系统图表设置的关键值时,显示该数据队列信息
			if (chartLineQ.containsKey(chartLines[i].getId())) {
				paint = new Paint();
				paint.setAntiAlias(true);
				paint.setStyle(Paint.Style.FILL);
				paint.setTypeface(null);
				paint.setTextSize(textSize * rate);

				if (i % 2 == 0 && i != 0)
					rowNum++;
				paint.setColor(chartLines[i].getColor());


				int yAxesMaxV = Integer.MIN_VALUE;
				int yAxesMinV = Integer.MAX_VALUE;

				Queue<ChartPointModel> chartQ = chartLineQ.get(chartLines[i].getId());
				for (ChartPointModel chart : chartQ) {
					if (!chart.getValue().equals("")) {
						int value = TypeConver.StringToInt(chart.getValue());
						if (chartLines[i].getScale() > 1) {
							value = value / chartLines[i].getScale();
						}
						if (value > yAxesMaxV)
							yAxesMaxV = value;
						if (value < yAxesMinV)
							yAxesMinV = value;
					}
				}
				if (yAxesMaxV == Integer.MIN_VALUE) {
					yAxesMaxV = (int) chartLines[i].getMaximum();
				}
				if (yAxesMaxV > 0) {
					yAxesMaxV = (int) (yAxesMaxV * 1.4);
				} else {
					yAxesMaxV = (int) (yAxesMaxV * 0.6);
				}

				if (yAxesMinV == Integer.MAX_VALUE) {
					yAxesMinV = chartLines[i].getMinimum();
				}
				if (yAxesMinV > 0) {
					yAxesMinV = (int) (yAxesMinV * 0.6);
				} else {
					yAxesMinV = (int) (yAxesMinV * 1.4);
				}

				float x = 0;
				float y =0;
				// 参数范围值显示
				x = (i % 2 == 0 ? yAxesWidth :
						(tablewidth - (yAxesWidth + (paint.measureText(String.valueOf(yAxesMaxV))))));
				y = curveStartY + (textSize * rate * (rowNum + 1));
				cv.drawText(String.valueOf(yAxesMaxV), x, y, paint);
				x = (i % 2 == 0 ? yAxesWidth :
						(tablewidth - (yAxesWidth + (paint.measureText(String.valueOf(yAxesMinV))))));
				y = rowsHeight * 17 - textSize * rate * (2 - rowNum);
				cv.drawText(String.valueOf(yAxesMinV), x, y, paint);

				// 图表折线

				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(3);
				paint.setColor(chartLines[i].getColor());
				paint.setPathEffect(null);
				Path path = new Path();
//				Queue<ChartPointModel> chartQ = chartLineQ.get(chartLines[i].getId());
				boolean pointStart = true;
				int index = 0;
				int chartValue = 0;
				for (ChartPointModel chart : chartQ) {
					if (!chart.getValue().equals("")) {
						chartValue = TypeConver.StringToInt(chart.getValue());
						if (chartLines[i].getScale() > 1) {
							chartValue = chartValue / chartLines[i].getScale();
						}
						if (chartValue > yAxesMaxV)
							chartValue = yAxesMaxV;
						if (chartValue < yAxesMinV)
							chartValue = yAxesMinV;
						startx = yAxesWidth + lineColsWidth / 2 * index;
						starty =
								rowsHeight * 18 - (1 - (float) (yAxesMaxV - chartValue) / (yAxesMaxV - yAxesMinV))
										* (rowsHeight * 18 - curveStartY);
						if (pointStart) {
							path.moveTo(startx, starty);
							pointStart = false;
						} else {
							path.lineTo(startx, starty);
						}
					} else if (!pointStart) {
						pointStart = true;
						cv.drawPath(path, paint);
					}
					// 绘制自定义事件
					if (i == 0 && chart.getAlarms() != null) {
						java.util.List<AlarmModel> alarms = chart.getAlarms();
						startx = yAxesWidth + lineColsWidth / 2 * index;
						starty =
								rowsHeight * 18 - (1 - (float) (yAxesMaxV - chartValue) / (yAxesMaxV - yAxesMinV)) * (curveStartY);
						for (int j = 0; j < alarms.size(); j++) {
							int drawableIds = alarms.get(j).getDrawableId();
							if (drawableIds != R.drawable.empty) {
								Drawable drawable = alarms.get(j).getIconDrawable(getContext());
								drawable.setBounds(
										(int) (startx - drawable.getIntrinsicWidth() / 2) + (j * drawable.getIntrinsicWidth()),
										(int) (tableheight - drawable.getIntrinsicHeight()),
										(int) (startx + drawable.getIntrinsicWidth()
												/ 2 + (j * drawable.getIntrinsicWidth())),
										(int) tableheight);
								drawable.draw(cv);
							}
						}
					}

					index++;
				}
				cv.drawPath(path, paint);
			}
		}
		cv.save();
		cv.restore();
	}



	/**
	 * 创建曲线图
	 *
	 * @return 输出位图
	 */
	protected void CreateLineChart5G(Canvas cv)
	{
		int width = this.getWidth();
		int height = this.getHeight();
		float tablewidth = width;
		float tableheight = height - 12;
		float rowsHeight = tableheight / tableRows; // 行高
		// float colsWidth = tablewidth / 6; // 列宽
		int lineColsNum = 40;
		int lineRowsNum = 13;
		float yAxesWidth = /* colsWidth / 3 */8 * rate;
		float lineColsWidth = (tablewidth - yAxesWidth * 2) / lineColsNum;
		float rowUpBit = (rowsHeight - textSize * rate) / 2; // 指定行上升位数,为行高-字体高度
		// 再除2

		int paramsRow = 0;
		if (chartLines.length % 2 == 0)
			paramsRow = chartLines.length / 2;
		else
			paramsRow = (chartLines.length + 1) / 2;
		float paramsRowHeight = (rowsHeight * 4 - rowsHeight / 2) / 5;
		float curveStartY = rowsHeight * 5 + paramsRowHeight * paramsRow + rowsHeight / 2;//
		// 折线图起始Y坐标
		float lineRowsHeight = (rowsHeight * 12 - curveStartY) / lineRowsNum;

		Paint paint = new Paint();
		// paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		paint.setTypeface(null);
		paint.setTextSize(textSize * rate);

		paint.setColor(Color.GRAY);
		cv.drawLine(yAxesWidth, curveStartY, yAxesWidth, rowsHeight * 12, paint);
		cv.drawLine(yAxesWidth, rowsHeight * 12, width - yAxesWidth, rowsHeight * 12, paint);
		cv.drawLine(width - yAxesWidth, curveStartY, width - yAxesWidth, rowsHeight * 12, paint);
		cv.drawText("NR",yAxesWidth + lineColsWidth-10,curveStartY,paint);
		// 参数值显示外框
		float startx = 0;
		float starty = 0;
		float stopx = 0;
		float stopy = 0;
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1f);
		paint.setColor(getResources().getColor(R.color.black));
		PathEffect e = new DashPathEffect(new float[]{3, 5, 3, 5}, 1);
		paint.setPathEffect(e);
		Path p = new Path();
		for (int i = 1; i < lineColsNum; i++) // 竖虚线
		{
			if (i % 4 == 0) {
				startx = yAxesWidth + lineColsWidth * i;
				starty = curveStartY;
				stopx = yAxesWidth + lineColsWidth * i;
				stopy = rowsHeight * 12;

				p.moveTo(startx, starty);
				p.lineTo(stopx, stopy);
			}

		}
		for (int i = 0; i < lineRowsNum; i++) // 横虚线
		{
			startx = yAxesWidth;
			starty = curveStartY + lineRowsHeight * i;
			stopx = width - yAxesWidth;
			stopy = curveStartY + lineRowsHeight * i;
			p.moveTo(startx, starty);
			p.lineTo(stopx, stopy);

		}
		cv.drawPath(p, paint);

		Hashtable<String, Queue<ChartPointModel>> chartLineQ =
				TraceInfoInterface.traceData.getChartLineQ();

		int rowNum = 0;
		/*NR 取4和5*/
		for (int i = 0; i < (chartLines.length > this.lineMaxSize ? this.lineMaxSize :
				chartLines.length); i++) {
			// 当返回的数据队列中包含系统图表设置的关键值时,显示该数据队列信息
			if (chartLineQ.containsKey(chartLines[i].getId())) {
				paint = new Paint();
				paint.setAntiAlias(true);
				paint.setStyle(Paint.Style.FILL);
				paint.setTypeface(null);
				paint.setTextSize(textSize * rate);

				if (i % 2 == 0 && i != 0)
					rowNum++;
				paint.setColor(chartLines[i].getColor());


				int yAxesMaxV = Integer.MIN_VALUE;
				int yAxesMinV = Integer.MAX_VALUE;

				Queue<ChartPointModel> chartQ = chartLineQ.get(chartLines[i].getId());
				for (ChartPointModel chart : chartQ) {
					if (!chart.getValue().equals("")) {
						int value = TypeConver.StringToInt(chart.getValue());
						if (chartLines[i].getScale() > 1) {
							value = value / chartLines[i].getScale();
						}
						if (value > yAxesMaxV)
							yAxesMaxV = value;
						if (value < yAxesMinV)
							yAxesMinV = value;
					}
				}
				if (yAxesMaxV == Integer.MIN_VALUE) {
					yAxesMaxV = (int) chartLines[i].getMaximum();
				}
				if (yAxesMaxV > 0) {
					yAxesMaxV = (int) (yAxesMaxV * 1.4);
				} else {
					yAxesMaxV = (int) (yAxesMaxV * 0.6);
				}

				if (yAxesMinV == Integer.MAX_VALUE) {
					yAxesMinV = chartLines[i].getMinimum();
				}
				if (yAxesMinV > 0) {
					yAxesMinV = (int) (yAxesMinV * 0.6);
				} else {
					yAxesMinV = (int) (yAxesMinV * 1.4);
				}

				float x = 0;
				float y =0;
				// 参数范围值显示
				x = (i % 2 == 0 ? yAxesWidth :
						(tablewidth - (yAxesWidth + (paint.measureText(String.valueOf(yAxesMaxV))))));
				y = curveStartY + (textSize * rate * (rowNum + 1));
				if (i==4||i==5){
					cv.drawText(String.valueOf(yAxesMaxV), x, y, paint);
				}
				x = (i % 2 == 0 ? yAxesWidth :
						(tablewidth - (yAxesWidth + (paint.measureText(String.valueOf(yAxesMinV))))));
				y = rowsHeight * 11 - textSize * rate * (2 - rowNum);
				if (i==4||i==5){
					cv.drawText(String.valueOf(yAxesMinV), x, y, paint);
				}

				// 图表折线

				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(3);
				paint.setColor(chartLines[i].getColor());
				paint.setPathEffect(null);
				Path path = new Path();
//				Queue<ChartPointModel> chartQ = chartLineQ.get(chartLines[i].getId());
				boolean pointStart = true;
				int index = 0;
				int chartValue = 0;
				for (ChartPointModel chart : chartQ) {
					if (!chart.getValue().equals("")) {
						chartValue = TypeConver.StringToInt(chart.getValue());
						if (chartLines[i].getScale() > 1) {
							chartValue = chartValue / chartLines[i].getScale();
						}
						if (chartValue > yAxesMaxV)
							chartValue = yAxesMaxV;
						if (chartValue < yAxesMinV)
							chartValue = yAxesMinV;
						startx = yAxesWidth + lineColsWidth / 2 * index;
						starty =
								rowsHeight * 12 - (1 - (float) (yAxesMaxV - chartValue) / (yAxesMaxV - yAxesMinV))
										* (rowsHeight * 12 - curveStartY);
						if (pointStart) {
							path.moveTo(startx, starty);
							pointStart = false;
						} else {
							path.lineTo(startx, starty);
						}
					} else if (!pointStart) {
						pointStart = true;
						if (i==4||i==5){
							cv.drawPath(path, paint);
						}
					}
					// 绘制自定义事件
					if (i == 0 && chart.getAlarms() != null) {
						java.util.List<AlarmModel> alarms = chart.getAlarms();
						startx = yAxesWidth + lineColsWidth / 2 * index;
						starty =
								rowsHeight * 12 - (1 - (float) (yAxesMaxV - chartValue) / (yAxesMaxV - yAxesMinV)) * (curveStartY);
						for (int j = 0; j < alarms.size(); j++) {
							int drawableIds = alarms.get(j).getDrawableId();
							if (drawableIds != R.drawable.empty) {
								Drawable drawable = alarms.get(j).getIconDrawable(getContext());
								drawable.setBounds(
										(int) (startx - drawable.getIntrinsicWidth() / 2) + (j * drawable.getIntrinsicWidth()),
										(int) (tableheight - drawable.getIntrinsicHeight()),
										(int) (startx + drawable.getIntrinsicWidth()
												/ 2 + (j * drawable.getIntrinsicWidth())),
										(int) tableheight);
								if (i==4||i==5){
									drawable.draw(cv);
								}
							}
						}
					}

					index++;
				}
				if (i==4||i==5){
					cv.drawPath(path, paint);
				}
			}
		}
		drawLteChart(cv);
		cv.save();
		cv.restore();
	}
	private void drawLteChart(Canvas cv) {
		int width = this.getWidth();
		int height = this.getHeight();
		float tablewidth = width;
		float tableheight = height - 12;
		float rowsHeight = tableheight / tableRows; // 行高
		// float colsWidth = tablewidth / 6; // 列宽
		int lineColsNum = 40;
		int lineRowsNum = 13;
		float yAxesWidth = /* colsWidth / 3 */8 * rate;
		float lineColsWidth = (tablewidth - yAxesWidth * 2) / lineColsNum;
		// 再除2

		float curveStartY = rowsHeight * 13+rowsHeight/2-10;// 折线图起始Y坐标
		float lineRowsHeight = (rowsHeight * 18 - curveStartY) / lineRowsNum;

		Paint paint = new Paint();
		// paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		paint.setTypeface(null);
		paint.setTextSize(textSize * rate);

		paint.setColor(Color.GRAY);
		cv.drawLine(yAxesWidth, curveStartY, yAxesWidth, rowsHeight * 18, paint);
		cv.drawLine(yAxesWidth, rowsHeight * 18, width - yAxesWidth, rowsHeight * 18, paint);
		cv.drawLine(width - yAxesWidth, curveStartY, width - yAxesWidth, rowsHeight * 18, paint);

		cv.drawText("LTE",yAxesWidth + lineColsWidth-10,curveStartY,paint);
		// 参数值显示外框
		float startx = 0;
		float starty = 0;
		float stopx = 0;
		float stopy = 0;
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1f);
		paint.setColor(getResources().getColor(R.color.black));
		PathEffect e = new DashPathEffect(new float[] { 3, 5, 3, 5 }, 1);
		paint.setPathEffect(e);
		Path p = new Path();
		for (int i = 1; i < lineColsNum; i++) // 竖虚线
		{
			if (i % 4 == 0) {
				startx = yAxesWidth + lineColsWidth * i;
				starty = curveStartY;
				stopx = yAxesWidth + lineColsWidth * i;
				stopy = rowsHeight * 18;

				p.moveTo(startx, starty);
				p.lineTo(stopx, stopy);
			}

		}
		for (int i = 0; i < lineRowsNum; i++) // 横虚线
		{
			startx = yAxesWidth;
			starty = curveStartY + lineRowsHeight * i;
			stopx = width - yAxesWidth;
			stopy = curveStartY + lineRowsHeight * i;
			p.moveTo(startx, starty);
			p.lineTo(stopx, stopy);

		}
		cv.drawPath(p, paint);

		Hashtable<String, Queue<ChartPointModel>> chartLineQ = TraceInfoInterface.traceData.getChartLineQ();

		// float labelWidth = (tablewidth - colsWidth * 1) / (chartLines.length != 0
		// ? chartLines.length : 1);

		int rowNum = 0;
		/*LTE 取2和3*/
		for (int i = 2; i < 4; i++) {
			// 当返回的数据队列中包含系统图表设置的关键值时,显示该数据队列信息
			if (chartLineQ.containsKey(chartLines[i].getId())) {
				paint = new Paint();
				paint.setAntiAlias(true);
				paint.setStyle(Paint.Style.FILL);
				paint.setTypeface(null);
				paint.setTextSize(textSize * rate);

				if (i % 2 == 0 && i != 0)
					rowNum++;
				paint.setColor(chartLines[i].getColor());
				// 图表参数信息
//				int yAxesMaxV = (int) chartLines[i].getMaximum();
//				int yAxesMinV = chartLines[i].getMinimum();

				int yAxesMaxV = Integer.MIN_VALUE;
				int yAxesMinV = Integer.MAX_VALUE;

				Queue<ChartPointModel> chartQ = chartLineQ.get(chartLines[i].getId());
				for (ChartPointModel chart : chartQ) {
					if (!chart.getValue().equals("")) {
						int value = TypeConver.StringToInt(chart.getValue());
						if (chartLines[i].getScale() > 1) {
							value = value / chartLines[i].getScale();
						}
						if (value > yAxesMaxV)
							yAxesMaxV = value;
						if (value < yAxesMinV)
							yAxesMinV = value;
					}
				}
				if (yAxesMaxV == Integer.MIN_VALUE) {
					yAxesMaxV = (int) chartLines[i].getMaximum();
				}
				if (yAxesMaxV > 0) {
					yAxesMaxV = (int) (yAxesMaxV * 1.4);
				} else {
					yAxesMaxV = (int) (yAxesMaxV * 0.6);
				}

				if (yAxesMinV == Integer.MAX_VALUE) {
					yAxesMinV = chartLines[i].getMinimum();
				}
				if (yAxesMinV > 0) {
					yAxesMinV = (int) (yAxesMinV * 0.6);
				} else {
					yAxesMinV = (int) (yAxesMinV * 1.4);
				}

				float x = 0;
				float y =0;
				// 参数范围值显示
				x = (i % 2 == 0 ? yAxesWidth : (tablewidth - (yAxesWidth + (paint.measureText(String.valueOf(yAxesMaxV))))));
				y = curveStartY + (textSize * rate * (rowNum + 1));
				cv.drawText(String.valueOf(yAxesMaxV), x, y, paint);
				x = (i % 2 == 0 ? yAxesWidth : (tablewidth - (yAxesWidth + (paint.measureText(String.valueOf(yAxesMinV))))));
				y = rowsHeight * 17 - textSize * rate * (2 - rowNum);
				cv.drawText(String.valueOf(yAxesMinV), x, y, paint);

				// 图表折线

				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(3);
				paint.setColor(chartLines[i].getColor());
				paint.setPathEffect(null);
				Path path = new Path();
//				Queue<ChartPointModel> chartQ = chartLineQ.get(chartLines[i].getId());
				boolean pointStart = true;
				int index = 0;
				int chartValue = 0;
				for (ChartPointModel chart : chartQ) {
					if (!chart.getValue().equals("")) {
						chartValue = TypeConver.StringToInt(chart.getValue());
						if (chartLines[i].getScale() > 1) {
							chartValue = chartValue / chartLines[i].getScale();
						}
						if (chartValue > yAxesMaxV)
							chartValue = yAxesMaxV;
						if (chartValue < yAxesMinV)
							chartValue = yAxesMinV;
						startx = yAxesWidth + lineColsWidth / 2 * index;
						starty = rowsHeight * 18 - (1 - (float) (yAxesMaxV - chartValue) / (yAxesMaxV - yAxesMinV))
								* (rowsHeight * 18 - curveStartY);
						if (pointStart) {
							path.moveTo(startx, starty);
							pointStart = false;
						} else {
							path.lineTo(startx, starty);
						}
					} else if (!pointStart) {
						pointStart = true;
						cv.drawPath(path, paint);
					}
					// 绘制自定义事件
					if (i == 0 && chart.getAlarms() != null) {
						java.util.List<AlarmModel> alarms = chart.getAlarms();
						startx = yAxesWidth + lineColsWidth / 2 * index;
						starty = rowsHeight * 18 - (1 - (float) (yAxesMaxV - chartValue) / (yAxesMaxV - yAxesMinV)) * (curveStartY);
						for (int j = 0; j < alarms.size(); j++) {
							int drawableIds = alarms.get(j).getDrawableId();
							if (drawableIds != R.drawable.empty) {
								Drawable drawable = alarms.get(j).getIconDrawable(getContext());
								drawable.setBounds(
										(int) (startx - drawable.getIntrinsicWidth() / 2) + (j * drawable.getIntrinsicWidth()),
										(int) (tableheight - drawable.getIntrinsicHeight()), (int) (startx + drawable.getIntrinsicWidth()
												/ 2 + (j * drawable.getIntrinsicWidth())), (int) tableheight);
								drawable.draw(cv);
							}
						}
					}

					index++;
				}
				cv.drawPath(path, paint);
			}
		}
	}

	/**
	 * 创建柱状图
	 *
	 * @param bm 要创建柱状图的位图
	 * @return 输出位图
	 */
	protected void CreateColumChart(Canvas cv)
	{
		int width = this.getWidth();
		int height = this.getHeight();
		float tablewidth = width;
		float tableheight = height - 12;
		float rowsHeight = tableheight / tableRows; // 行高
		float colsWidth = tablewidth / 8; // 列宽
		int rectNum = 5;
		float yAxesWidth = colsWidth / 3 * rate;
		float rectColsWidth = (tablewidth - yAxesWidth * 2) / rectNum;
		float rectWidth = 20 * rate; // 柱状宽度
		 float rowUpBit = (rowsHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2
		float marginLR = (20 * metric.density);
		float publicParamRowHeight = rowsHeight * 2 / 3;

		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(getResources().getColor(R.color.app_main_text_color));
		paint.setTypeface(null);
		paint.setTextSize(textSize * rate);

		cv.drawLine(yAxesWidth, rowsHeight * 8, yAxesWidth, rowsHeight * 18, paint);
		cv.drawLine(yAxesWidth, rowsHeight * 18, width - yAxesWidth, rowsHeight * 18, paint);

		float left, right, bottom, top;
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1f);
		PathEffect e = new DashPathEffect(new float[]{3, 5, 3, 5}, 1);
		paint.setPathEffect(e);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setPathEffect(null);

		for (int i =chartLines.length-1;i>=0;i--) {
			// 当返回的数据队列中包含系统图表设置的关键值时,显示该数据队列信息
			int yAxesMaxV = (int) chartLines[i].getMaximum();
			int yAxesMinV = chartLines[i].getMinimum();
			String chartV = TraceInfoInterface.getParaValue(Integer.parseInt(chartLines[i].getId(), 16));
			if (chartV == null) {
				chartV = "";
			}
			paint.setColor(chartLines[i].getColor());
			cv.drawText(String.valueOf(yAxesMaxV), (yAxesWidth - paint.measureText(String.valueOf(yAxesMaxV))) / 2,
					rowsHeight * 8 + (textSize * rate * (i + 1)), paint);
			cv.drawText(String.valueOf(yAxesMinV), (yAxesWidth - paint.measureText(String.valueOf(yAxesMinV))) / 2,
					rowsHeight * 18 - textSize * rate * (6 - i), paint);

			left = yAxesWidth + rectColsWidth * i + (rectColsWidth - rectWidth) / 2;
			right = yAxesWidth + rectColsWidth * i + (rectColsWidth - rectWidth) / 2 + rectWidth;
			bottom = rowsHeight * 18;
			int rectValue = chartV.equals("") ? yAxesMinV : TypeConver.StringToInt(chartV);
			if (chartLines[i].getScale() > 1 && !chartV.equals("")) {
				chartV = UtilsMethod.decFormat.format(rectValue * 1f / chartLines[i].getScale());
				rectValue = rectValue / chartLines[i].getScale();
			}
			if (rectValue > yAxesMaxV)
				rectValue = yAxesMaxV;
			if (rectValue < yAxesMinV)
				rectValue = yAxesMinV;

			top = rowsHeight * 18 - (1 - (float) (yAxesMaxV - rectValue) / (yAxesMaxV - yAxesMinV)) * (rowsHeight * 10);

			cv.drawRect(left, top, right, bottom, paint);
			cv.drawText(chartV, yAxesWidth + rectColsWidth * i + (rectColsWidth - paint.measureText(chartV)) / 2, top
					- textSize, paint);
		}

		cv.save();
		cv.restore();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int width = getWidth();
		int height = getHeight();
		float mTouchCurrX = event.getX();
		float mTouchCurrY = event.getY();

		switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				if (mTouchCurrX > 20 && mTouchCurrY > height / 18 * 6 && mTouchCurrX < width - 20 && mTouchCurrY < height * 18) {
					TraceInfoInterface.chartCurrentPage = TraceInfoInterface.chartCurrentPage == 1 ? 2 : 1;
					invalidate();
				}
				break;
		}
		return true;
	}

	@Override
	protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();
		RefreshEventManager.addRefreshListener(this);

	}

	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
		RefreshEventManager.removeRefreshListener(this);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		rate = (float) h / 366;
	}

	@Override
	public void onRefreshed(RefreshType refreshType, Object object)
	{
		switch (refreshType) {
			case ACTION_WALKTOUR_TIMER_CHANGED:
				if (!ApplicationModel.getInstance().isFreezeScreen()) {
					invalidate();
				}
				break;

			default:
				break;
		}
	}
}
