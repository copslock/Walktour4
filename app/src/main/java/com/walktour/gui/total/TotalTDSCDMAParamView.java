package com.walktour.gui.total;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.BasicTotalView;
import com.walktour.gui.R;
import com.walktour.model.TotalMeasureModel;

import java.util.HashMap;

public class TotalTDSCDMAParamView extends BasicTotalView {
	private static String tag = "TotalParaView";
	private boolean isRegisterReceiver = false;
	DisplayMetrics metric = new DisplayMetrics();

	public TotalTDSCDMAParamView(Context context) {
		super(context);

		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(metric);
	}

	public TotalTDSCDMAParamView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setDrawingCacheEnabled(false);
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(metric);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		CreateTable(canvas);
		CreateTableData(canvas, TotalDataByGSM.getInstance().getMeasuePara());
	}

	/**
	 * 创建表格
	 * 
	 * @param bm
	 *            要创建表格的位图
	 * @return 输出位图
	 */
	protected Bitmap CreateTable(Canvas cv) {
		int width = this.getWidth();
		float startx = 1;
		float starty = 1;
		float stopx = 0;
		float stopy = 0;
		float tablewidth = width;
		int tableRows = 11; // 行数
		int tableCols = 5; // 列数
		float colsWidth = tablewidth / tableCols; // 列宽

		cv.drawLine(startx, marginSize, width - marginSize, marginSize, linePaint);
		cv.drawLine(startx, rowHeight * tableRows + marginSize, width - marginSize,
				rowHeight * tableRows + marginSize, linePaint);
		cv.drawLine(startx, marginSize, startx, rowHeight * tableRows + marginSize, linePaint);
		cv.drawLine(width - marginSize, marginSize, width - marginSize, rowHeight
				* tableRows + marginSize, linePaint);

		for (int i = 0; i < tableRows - 1; i++) {
			startx = 1;
			starty = rowHeight * (i + 1);
			stopx = width - marginSize;
			stopy = rowHeight * (i + 1);
			cv.drawLine(startx, starty+marginSize, stopx, stopy + marginSize, linePaint);
		}
		for (int i = 1; i < tableCols - 1; i++) {
			startx = colsWidth * (i + 1);
			starty = 1 + rowHeight * 1;
			stopx = colsWidth * (i + 1);
			stopy = rowHeight * tableRows;
			cv.drawLine(startx, starty+ marginSize, stopx, stopy + marginSize, linePaint);
		}
		
		float rowUpBit = (rowHeight - textSize) / 2;// 指定行上升位数,为行高-字体高度 再除2

		String paraname;
		paraname = "TDSCDMA Cell Info";// 表头
		cv.drawText(paraname,
				(tablewidth - fontPaint.measureText(paraname))
						/ 2, rowHeight * 1 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_para);// "参数名称";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 2 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_max);// "最大";
		cv.drawText(paraname,
				colsWidth * 2 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 2 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_min);// "最小";
		cv.drawText(paraname,
				colsWidth * 3 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 2 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_average);// "平均";
		cv.drawText(paraname,
				colsWidth * 4 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 2 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_td_pccpchrscp);	//
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 3 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_td_pccpchc2i);	//"PCCPCH C/I(dB)";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 4 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_td_uetxpower);	//"UE TxPower";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 5 - rowUpBit + marginSize, fontPaint);
		paraname = "DPCH BLER(%)";	//"BLER";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 6 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_td_dlrlcthr);	//"BLER";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 7 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_td_dlrlcbler);	//"BLER";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 8 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_td_ulrlcthr);
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 9 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_td_ulrlcrtxrate);
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 10 - rowUpBit + marginSize, fontPaint);
		paraname = "PDSCH Total BLER(%)";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
				/ 2, rowHeight * 11 - rowUpBit + marginSize, fontPaint);
		cv.save();
		cv.restore();
		return null;
	}

	/**
	 * 创建表格数据
	 * 
	 * @param bm
	 *            要创建表格的位图
	 * @param data
	 *            表格数据
	 * @return 输出位图
	 */
	protected Bitmap CreateTableData(Canvas cv,
			HashMap<String, TotalMeasureModel> hMap) {
		int width = this.getWidth();
		float tablewidth = width;
		int tableCols = 5; // 列数
		float colsWidth = tablewidth / tableCols; // 列宽
		float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2
		
		TotalMeasureModel pccpchRSCP = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._tPCCPCH_RSCP.name());

		TotalMeasureModel pccpchCI = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._tPCCPCH_C_I.name());

		TotalMeasureModel ueTxpower = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._tUE_TxPower.name());

		TotalMeasureModel bler = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._tBLER.name());
		TotalMeasureModel dlRlcThr = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._tdlRLCThr.name());
		TotalMeasureModel dlRlcBler = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._tdlRLCBLER.name());
		TotalMeasureModel ulRlcThr = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._tulRLCThr.name());
		TotalMeasureModel ulRlcRtxRate = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._tulRLCRTXRate.name());
		TotalMeasureModel pdschTotalLER = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._pdschTotalLER.name());
            
        String datas[] = new String[]{
                handlesInvalid(pccpchRSCP.getMaxValue()),handlesInvalid(pccpchRSCP.getMinValue()),processAverage(pccpchRSCP.getKeySum(), pccpchRSCP.getKeyCounts()),
                handlesInvalid(pccpchCI.getMaxValue()),handlesInvalid(pccpchCI.getMinValue()),processAverage(pccpchCI.getKeySum(), pccpchCI.getKeyCounts()),
                handlesInvalid(ueTxpower.getMaxValue()),handlesInvalid(ueTxpower.getMinValue()),processAverage(ueTxpower.getKeySum(), ueTxpower.getKeyCounts()),
                TotalDataByGSM.getValueByMultiple(bler.getMaxValue(),UtilsMethod.kbyteRage),
                TotalDataByGSM.getValueByMultiple(bler.getMinValue(),UtilsMethod.kbyteRage),
                processAverage(bler.getKeySum(), bler.getKeyCounts() * UtilsMethod.kbyteRage),
                TotalDataByGSM.getValueByMultiple(dlRlcThr.getMaxValue(), UtilsMethod.kbyteRage),
                TotalDataByGSM.getValueByMultiple(dlRlcThr.getMinValue(),UtilsMethod.kbyteRage),
                processAverage(dlRlcThr.getKeySum(), dlRlcThr.getKeyCounts() * UtilsMethod.kbyteRage),
                TotalDataByGSM.getValueByMultiple(dlRlcBler.getMaxValue(),UtilsMethod.kbyteRage),
                TotalDataByGSM.getValueByMultiple(dlRlcBler.getMinValue(),UtilsMethod.kbyteRage),
                processAverage(dlRlcBler.getKeySum(), dlRlcBler.getKeyCounts() * UtilsMethod.kbyteRage),
                TotalDataByGSM.getValueByMultiple(ulRlcThr.getMaxValue(),UtilsMethod.kbyteRage),
                TotalDataByGSM.getValueByMultiple(ulRlcThr.getMinValue(),UtilsMethod.kbyteRage),
                processAverage(ulRlcThr.getKeySum(), ulRlcThr.getKeyCounts() * UtilsMethod.kbyteRage),
                TotalDataByGSM.getValueByMultiple(ulRlcRtxRate.getMaxValue(),UtilsMethod.kbyteRage),
                TotalDataByGSM.getValueByMultiple(ulRlcRtxRate.getMinValue(),UtilsMethod.kbyteRage),
                processAverage(ulRlcRtxRate.getKeySum(), ulRlcRtxRate.getKeyCounts() * UtilsMethod.kbyteRage),
                TotalDataByGSM.getValueByMultiple(pdschTotalLER.getMaxValue(),UtilsMethod.kbyteRage),
                TotalDataByGSM.getValueByMultiple(pdschTotalLER.getMinValue(),UtilsMethod.kbyteRage),
                processAverage(pdschTotalLER.getKeySum(), pdschTotalLER.getKeyCounts() * UtilsMethod.kbyteRage)
                };
        
        for (int i = 0,j=2,k=3; i < datas.length; i++,j++) {

            cv.drawText(datas[i],
                    colsWidth * j + (colsWidth - paramPaint.measureText(datas[i])) / 2,
                    rowHeight * k - rowUpBit + marginSize, paramPaint);
               if((i+1)%3 == 0){
                    k++;
                    j=1;
                }
        }
		
		cv.save();
		cv.restore();
		return null;

	}
	
	   /**
     * 处理无效值情况<BR>
     * [功能详细描述]
     * @param value
     * @return
     */
    private String handlesInvalid(long value){
        if(value != -9999){
            return String.valueOf(value);
        }
        return "";
    } 
    
    
    /**
     * 处理平均值<BR>
     * [功能详细描述]
     * @param sum
     * @param count
     * @return
     */
    private String processAverage(long sum, long count){
        return sum == -9999 ? "" : UtilsMethod.decFormat
                .format(sum
                        * 1f
                        / (count != 0 ? count : 1));
    }
    
    /**
     * 处理平均值<BR>
     * [功能详细描述]
     * @param sum
     * @param count
     * @return
     */
    private String processAverage(long sum, float count){
        return sum == -9999 ? "" : UtilsMethod.decFormat
                .format(sum
                        * 1f
                        / (count != 0 ? count : 1));
    }

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		IntentFilter filter = new IntentFilter(); // 注册一个消息过滤器
		filter.addAction(WalkMessage.TotalParaSelect);
		filter.addAction(TotalDataByGSM.TotalTaskDataChanged);
		filter.addAction(TotalDataByGSM.TotalParaDataChanged);
		filter.addAction(TotalDataByGSM.TotalResultToPicture);
		getContext().registerReceiver(mIntentReceiver, filter, null, null);
		isRegisterReceiver = true;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		try {
			if (isRegisterReceiver) {
				getContext().unregisterReceiver(mIntentReceiver); // 反注册消息过滤器
				isRegisterReceiver = false;
			}
		} catch (java.lang.IllegalArgumentException e) {
			LogUtil.w("IllegalArgumentException:", e.toString());
		}
	}

	/**
	 * 消息处理
	 */
	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			 if (intent.getAction().equals(
					TotalDataByGSM.TotalResultToPicture)) {
				String path = intent
						.getStringExtra(TotalDataByGSM.TotalSaveFilePath)
						+ "-Para.jpg";
				LogUtil.w(tag, "--save current to file---" + path);
				TotalTDSCDMAParamView.this.buildDrawingCache();
				UtilsMethod.SaveBitmapToFile(
						TotalTDSCDMAParamView.this.getDrawingCache(), path);
			} else {
				invalidate();
			}
		}
	};

}
