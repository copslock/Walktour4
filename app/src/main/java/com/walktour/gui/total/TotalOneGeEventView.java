package com.walktour.gui.total;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalEvent;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.BasicTotalView;
import com.walktour.gui.R;

import java.util.HashMap;

/**
 * 增加异系统切换事件
 * @author zhihui.lian
 *
 */
public class TotalOneGeEventView extends BasicTotalView {
	private boolean isRegisterReceiver = false;

	public TotalOneGeEventView(Context context){
		super(context);
	}
	
	public TotalOneGeEventView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		CreateTable(canvas);
		CreateTableData(canvas, TotalDataByGSM.getInstance().getEvent());
	}
	

	/**
	 * 创建表格
	 * @param bm 要创建表格的位图
	 * @return   输出位图
	 */
	protected void CreateTable(Canvas cv){
		int width = this.getWidth();
		float startx =1;
		float starty =0;
		float stopx =0;
		float stopy =0;
		float tablewidth = width ;
		int tableRows = 40;	//行数
		int tableCols = 3;	//列数
		float colsWidth = tablewidth/tableCols;		//列宽
		cv.drawLine(startx, marginSize, width-marginSize, marginSize, linePaint);
		cv.drawLine(startx, rowHeight*tableRows, width-marginSize, rowHeight*tableRows , linePaint);
		cv.drawLine(startx, marginSize, startx, rowHeight*tableRows, linePaint);
		cv.drawLine(width-marginSize, marginSize, width-marginSize, rowHeight*tableRows, linePaint);
		
		for(int i=0;i<tableRows - 1;i++){
			startx = 1;
			starty =  rowHeight * (i+1);
			stopx = width - marginSize;
			stopy = rowHeight *(i+1);
			cv.drawLine(startx, starty, stopx, stopy, linePaint);
		}
		for(int i=0;i<tableCols -2;i++){
			startx = colsWidth * (i+2);
			starty =  marginSize;
			stopx = colsWidth * (i+2);
			stopy = rowHeight*tableRows;
			cv.drawLine(startx, starty, stopx, stopy, linePaint);
		}
		
		float rowUpBit = (rowHeight - textSize)/2 ;		//指定行上升位数,为行高-字体高度 再除2

		String paraname;
		paraname = getContext().getString(R.string.total_value);
		cv.drawText(paraname, colsWidth * 2 + (colsWidth - fontPaint.measureText(paraname)) / 2, rowHeight * 1- rowUpBit, fontPaint);
		
		paraname = getContext().getString(R.string.total_lteTotd_times);	
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 2- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_lteTotd_successrate);	
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 3- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_lteTotd_delay);	
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 4- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_lteTogsm_times);				
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 5- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_lteTogsm_successrate);			
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 6- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_lteTogsm_delay);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 7- rowUpBit, fontPaint);
		
		paraname = getContext().getString(R.string.total_lteTogsm_cellreselec_times);				
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 8- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_lteTogsm_cellreselec_successrate);				
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 9- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_lteTogsm_cellreselec_delay);				
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 10- rowUpBit, fontPaint);
		
		paraname = getContext().getString(R.string.total_gsmTolte_cellreselec_times);				
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 11- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_gsmTolte_cellreselec_successrate);				
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 12- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_gsmTolte_cellreselec_delay);				
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 13- rowUpBit, fontPaint);
		
		paraname = getContext().getString(R.string.total_lteTotd_cellreselec_times);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 14- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_lteTotd_cellreselec_successrate);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 15- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_lteTotd_cellreselec_delay);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 16- rowUpBit, fontPaint);
		
		paraname = getContext().getString(R.string.total_tdTolte_cellreselec_times);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 17- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_tdTolte_cellreselec_successrate);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 18- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_tdTolte_cellreselec_delay);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 19- rowUpBit, fontPaint);
		
		paraname = getContext().getString(R.string.total_gsmTotd_times);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 20- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_gsmTotd_scuccess_times);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 21- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_gsmTotd_successrate);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 22- rowUpBit, fontPaint);

		paraname = getContext().getString(R.string.total_tdTogsm_times);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 23- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_tdTogsm_scuccess_times);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 24- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_tdTogsm_successrate);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 25- rowUpBit, fontPaint);

		paraname = getContext().getString(R.string.total_gsmTotd_red_times);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 26- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_gsmTotd_red_scuccess_times);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 27- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_gsmTotd_red_successrate);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 28- rowUpBit, fontPaint);
		
		paraname = getContext().getString(R.string.total_tdTogsm_red_times);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 29- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_tdTogsm_red_scuccess_times);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 30- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_tdTogsm_red_successrate);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 31- rowUpBit, fontPaint);
		
		paraname = getContext().getString(R.string.total_lteTogsm_han_times);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 32- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_lteTogsm_han_scuccess_times);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 33- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_lteTogsm_han_successrate);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 34- rowUpBit, fontPaint);
		cv.restore();
		paraname = getContext().getString(R.string.total_tdTolte_times);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 35- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_tdTolte_scuccess_times);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 36- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_tdTolte_successrate);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 37- rowUpBit, fontPaint);

		paraname = getContext().getString(R.string.total_gsmTolte_times);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 38- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_gsmTolte_scuccess_times);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 39- rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_gsmTolte_successrate);		
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowHeight * 40- rowUpBit, fontPaint);
		cv.save();
		cv.restore();
	}
	/**
	 * 创建表格数据
	 * @param bm  要创建表格的位图
	 * @param data  表格数据
	 * @return    输出位图
	 */
	protected void CreateTableData(Canvas cv,HashMap<String, Long> hMap){
		int width = this.getWidth();
		float tablewidth = width ;
		int tableCols = 3;	//列数
		float colsWidth = tablewidth/tableCols;										//列宽
		float rowUpBit = (rowHeight - textSize)/2 ;								//指定行上升位数,为行高-字体高度 再除2
		
		String value;
		
		value = ""+TotalDataByGSM.getHashMapValue(hMap, TotalEvent._lteHandoverToTDRequest.name());
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 2- rowUpBit, paramPaint);
		
		value = ""+TotalDataByGSM.getHashMapMultiple(hMap, TotalEvent._lteHandoverToTDSuccess.name(), 
				TotalEvent._lteHandoverToTDRequest.name(), 100, "%");
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 3- rowUpBit, paramPaint);
		
		value = "";
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 4- rowUpBit, paramPaint);
		
		value = ""+TotalDataByGSM.getHashMapValue(hMap, TotalEvent._redirectionLTEToGSMRequest.name());
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 5- rowUpBit, paramPaint);
		
		value = ""+TotalDataByGSM.getHashMapMultiple(hMap, TotalEvent._redirectionLTEToGSMSuccess.name(), 
				TotalEvent._redirectionLTEToGSMRequest.name(), 100, "%");
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 6- rowUpBit, paramPaint);
		
		value = "";
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 7- rowUpBit, paramPaint);
		
		//LTE to GSM CellReselec次数
		value = ""+TotalDataByGSM.getHashMapValue(hMap, TotalEvent._lteToGsmCellRequest.name());
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 8- rowUpBit, paramPaint);
		value = ""+TotalDataByGSM.getHashMapMultiple(hMap, TotalEvent._lteToGsmCellComplete.name(), 
				TotalEvent._lteToGsmCellRequest.name(), 100, "%");
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 9- rowUpBit, paramPaint);
		value = "";
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 10- rowUpBit, paramPaint);
		
		//GSM to LTE CellReselec次数
		value = ""+TotalDataByGSM.getHashMapValue(hMap, TotalEvent._gsmToLteCellRequest.name());
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 11- rowUpBit, paramPaint);
		value = ""+TotalDataByGSM.getHashMapMultiple(hMap, TotalEvent._gsmToLteCellComplete.name(), 
				TotalEvent._gsmToLteCellRequest.name(), 100, "%");
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 12- rowUpBit, paramPaint);
		value = "";
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 13- rowUpBit, paramPaint);
		
		
		//LTE to TD CellReselec次数
		value = ""+TotalDataByGSM.getHashMapValue(hMap, TotalEvent._lteToTdCellRequest.name());
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 14- rowUpBit, paramPaint);
		value = ""+TotalDataByGSM.getHashMapMultiple(hMap, TotalEvent._lteToTdCellComplete.name(), 
				TotalEvent._lteToTdCellRequest.name(), 100, "%");
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 15- rowUpBit, paramPaint);
		value = "";
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 16- rowUpBit, paramPaint);
		
		
		//TD to LTE CellReselec次数
		value = ""+TotalDataByGSM.getHashMapValue(hMap, TotalEvent._tdscdmaToLteCellRequest.name());
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 17- rowUpBit, paramPaint);
		value = ""+TotalDataByGSM.getHashMapMultiple(hMap, TotalEvent._tdscdmaToLteCellComplete.name(), 
				TotalEvent._tdscdmaToLteCellRequest.name(), 100, "%");
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 18- rowUpBit, paramPaint);
		value = "";
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 19- rowUpBit, paramPaint);
		
		/***********************************************************************************************/
		//GSM to TD切换请求次数
		value = ""+TotalDataByGSM.getHashMapValue(hMap, TotalEvent._gsmTotdHandoverRequest.name());
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 20- rowUpBit, paramPaint);
		value = ""+TotalDataByGSM.getHashMapValue(hMap, TotalEvent._gsmTotdHandoverSuccess.name());
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 21- rowUpBit, paramPaint);
		value = ""+TotalDataByGSM.getHashMapMultiple(hMap, TotalEvent._gsmTotdHandoverSuccess.name(), 
				TotalEvent._gsmTotdHandoverRequest.name(), 100, "%");;
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 22- rowUpBit, paramPaint);
		
		//TD to GSM切换请求次数
		value = ""+TotalDataByGSM.getHashMapValue(hMap, TotalEvent._tdTogsmHandoverRequest.name());
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 23- rowUpBit, paramPaint);
		value = ""+TotalDataByGSM.getHashMapValue(hMap, TotalEvent._tdTogsmHandoverSuccess.name());
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 24- rowUpBit, paramPaint);
		value = ""+TotalDataByGSM.getHashMapMultiple(hMap, TotalEvent._tdTogsmHandoverSuccess.name(), 
				TotalEvent._tdTogsmHandoverRequest.name(), 100, "%");
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 25- rowUpBit, paramPaint);
		
		//GSM to TD重选请求次数
		value = ""+TotalDataByGSM.getHashMapValue(hMap, TotalEvent._gsmTotdCellReselectRequest.name());
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 26- rowUpBit, paramPaint);
		value = ""+TotalDataByGSM.getHashMapValue(hMap, TotalEvent._gsmTotdCellReselectComplete.name());
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 27- rowUpBit, paramPaint);
		value = ""+TotalDataByGSM.getHashMapMultiple(hMap, TotalEvent._gsmTotdCellReselectComplete.name(), 
				TotalEvent._gsmTotdCellReselectRequest.name(), 100, "%");
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 28- rowUpBit, paramPaint);
		
		//TD to GSM重选次数
		value = ""+TotalDataByGSM.getHashMapValue(hMap, TotalEvent._tdTogsmCellResRequest.name());
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 29- rowUpBit, paramPaint);
		value = ""+TotalDataByGSM.getHashMapValue(hMap, TotalEvent._tdTogsmCellResSuccess.name());
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 30- rowUpBit, paramPaint);
		value = ""+TotalDataByGSM.getHashMapMultiple(hMap, TotalEvent._tdTogsmCellResSuccess.name(), 
				TotalEvent._tdTogsmCellResRequest.name(), 100, "%");
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 31- rowUpBit, paramPaint);
		
		//LTE to GSM切换请求次数
		value = ""+TotalDataByGSM.getHashMapValue(hMap, TotalEvent._lteTogsmHandoverRequest.name());
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 32- rowUpBit, paramPaint);
		value = ""+TotalDataByGSM.getHashMapValue(hMap, TotalEvent._lteTogsmHandoverSuccess.name());
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 33- rowUpBit, paramPaint);
		value = ""+TotalDataByGSM.getHashMapMultiple(hMap, TotalEvent._lteTogsmHandoverSuccess.name(), 
				TotalEvent._lteTogsmHandoverRequest.name(), 100, "%");
		cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowHeight * 34- rowUpBit, paramPaint);
		
		/***********************************************************************************************/
		cv.save();
		cv.restore();
		
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		IntentFilter filter = new IntentFilter(); //注册一个消息过滤器
		filter.addAction(TotalDataByGSM.TotalTaskDataChanged); 
		filter.addAction(TotalDataByGSM.TotalParaDataChanged);
		getContext().registerReceiver(mIntentReceiver, filter, null, null);
		isRegisterReceiver = true;
	 }
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		try{
			if(isRegisterReceiver){
				getContext().unregisterReceiver(mIntentReceiver); //反注册消息过滤器
				isRegisterReceiver = false;
			}
		 }catch(java.lang.IllegalArgumentException e){
			LogUtil.w("IllegalArgumentException:",e.toString());
	 	}
	}
	
	/**
	 * 消息处理
	 */
	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
				invalidate();
		}
	};
	
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = (int) (rowHeight * 40) + (marginSize * 2);
        setMeasuredDimension(measureWidth(widthMeasureSpec), height);
    }
    
    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        
        int result = 500;
        if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        }
        else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        
        return result;
    }

}
