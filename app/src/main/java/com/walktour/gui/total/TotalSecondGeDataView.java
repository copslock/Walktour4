package com.walktour.gui.total;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.model.TotalMeasureModel;

import java.util.HashMap;

public class TotalSecondGeDataView extends View {
	private static String tag="TotalSecondGeDataView";
	private Bitmap  mbmp=null;
	private int viewWidth;
	private int viewHeight;
	private float rate=1;
	float textSize = 10;	//字体大小
	private final Paint mPaint = new Paint();
	private boolean isRegisterReceiver = false;
	public TotalSecondGeDataView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public TotalSecondGeDataView(Context context, AttributeSet attrs) {
		super(context, attrs);
        this.setDrawingCacheEnabled(true);
        this.setDrawingCacheBackgroundColor(getResources().getColor(R.color.param_view_bg_color));
		// TODO Auto-generated constructor stub
		//mbmp = CreateTable(CreateBlankBitmap());
	}
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if(mbmp!=null){
			canvas.drawBitmap(mbmp, 0, 0, mPaint);
		 }else{
			 mbmp = CreateTableData(CreateTable(CreateBlankBitmap()),TotalDataByGSM.getInstance().getMeasuePara());
			 canvas.drawBitmap(mbmp, 0, 0, mPaint);
		 }
	}
	
	/**
	 * 创建空白位图
	 * @return 输出位图
	 */
	protected Bitmap CreateBlankBitmap(){
		Bitmap bm = Bitmap.createBitmap(viewWidth, viewHeight, Config.ARGB_8888);
		return bm;
	}
	/**
	 * 创建表格
	 * @param bm 要创建表格的位图
	 * @return   输出位图
	 */
	protected Bitmap CreateTable(Bitmap bm){
		int width = bm.getWidth();
		int height = bm.getHeight();
		Canvas cv = new Canvas(bm);
		Paint paint = new Paint();
				
		float startx =1;
		float starty =0;
		float stopx =0;
		float stopy =0;
		float tablewidth = width ;
		float tableheight = height;
		int tableRows = 6;	//行数
		int tableCols = 6;	//列数
		float rowsHeight = (tableheight/tableRows > 50 ? 50:tableheight/tableRows);	//如果算出来的行高大于20直接取20行高
		float colsWidth = tablewidth/tableCols;		//列宽
		
		paint.setColor(getResources().getColor(R.color.param_view_line_color));
		paint.setStrokeWidth(1f);
		cv.drawLine(startx, 1*rate, width-1*rate, 1*rate, paint);
		cv.drawLine(startx, rowsHeight*tableRows, width-1*rate, rowsHeight*tableRows , paint);
		cv.drawLine(startx, 1*rate, startx, rowsHeight*tableRows, paint);
		cv.drawLine(width-1*rate, startx, width-1*rate, rowsHeight*tableRows, paint);
		
		for(int i=0;i<tableRows - 1;i++){
			startx = 1;
			starty =  rowsHeight * (i+1);
			stopx = width - 1 * rate;
			stopy = rowsHeight *(i+1);
			cv.drawLine(startx, starty, stopx, stopy, paint);
		}
		for(int i=0;i<tableCols - 3;i++){
			startx = colsWidth * (i+3);
			starty =  0;
			stopx = colsWidth * (i+3);
			stopy = rowsHeight*tableRows;
			cv.drawLine(startx, starty, stopx, stopy, paint);
		}
		
		float rowUpBit = (rowsHeight - textSize)/2 ;		//指定行上升位数,为行高-字体高度 再除2
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);	
		paint.setColor(Color.WHITE);
		paint.setTypeface(null);
		paint.setTextSize(textSize *rate);
		String paraname;
		paraname = getContext().getString(R.string.total_max);
		cv.drawText(paraname, colsWidth * 3 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 1- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_min);
		cv.drawText(paraname, colsWidth * 4 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 1- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_average);
		cv.drawText(paraname, colsWidth * 5 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 1- rowUpBit, paint);
		
		/*paraname = getContext().getString(R.string.total_ul_rlc);
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 2- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_dl_rlc);
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 3- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_gprs_bler);
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 4- rowUpBit, paint);*/
		paraname = getContext().getString(R.string.total_bep_gmsk);
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 2- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_bep_8psk);
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 3- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_cvbep_gmsk);
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 4- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_cvbep_8psk);
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 5- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_c_value);
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 6- rowUpBit, paint);
		
		cv.save();
		cv.restore();
		return bm;
	}
	/**
	 * 创建表格数据
	 * @param bm  要创建表格的位图
	 * @param data  表格数据
	 * @return    输出位图
	 */
	protected Bitmap CreateTableData(Bitmap bm,HashMap<String, TotalMeasureModel> hMap){
		int width = bm.getWidth();
		int height = bm.getHeight();
		float tablewidth = width ;
		float tableheight = height;
		int tableRows = 6;	//行数
		int tableCols = 6;	//列数
		float rowsHeight = (tableheight/tableRows > 50 ? 50:tableheight/tableRows);	//如果算出来的行高大于20直接取20行高
		float colsWidth = tablewidth/tableCols;										//列宽
		float rowUpBit = (rowsHeight - textSize)/2 ;								//指定行上升位数,为行高-字体高度 再除2
		
		Canvas cv = new Canvas(bm);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);	
		paint.setColor(getResources().getColor(R.color.info_param_color));
		paint.setTypeface(null);
		paint.setTextSize(textSize * rate);
		String value;
		
		TotalMeasureModel data= null;
		
		/*data = TotalDataByGSM.getHashMapMeasure(hMap, TotalStruct.TotalMeasurePara._ulRLCThr.name());
		value = ""+(data.getMaxValue() != -9999 ? data.getMaxValue() : "");
		cv.drawText(value, colsWidth * 3 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 2- rowUpBit, paint);
		value = ""+(data.getMinValue() != -9999 ? data.getMinValue() : "");
		cv.drawText(value, colsWidth * 4 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 2- rowUpBit, paint);
		value =""+ (data.getKeySum() == -9999 ? "" :
			UtilsMethod.decFormat.format(data.getKeySum() * 1f / (data.getKeyCounts() != 0 ? data.getKeyCounts() : 1)));
		cv.drawText(value, colsWidth * 5 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 2- rowUpBit, paint);
		
		data = TotalDataByGSM.getHashMapMeasure(hMap, TotalStruct.TotalMeasurePara._dlRLCThr.name());
		value = ""+(data.getMaxValue() != -9999 ? data.getMaxValue() : "");
		cv.drawText(value, colsWidth * 3 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 3- rowUpBit, paint);
		value = ""+(data.getMinValue() != -9999 ? data.getMinValue() : "");
		cv.drawText(value, colsWidth * 4 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 3- rowUpBit, paint);
		value =""+ (data.getKeySum() == -9999 ? "" :
			UtilsMethod.decFormat.format(data.getKeySum() * 1f / (data.getKeyCounts() != 0 ? data.getKeyCounts() : 1)));
		cv.drawText(value, colsWidth * 5 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 3- rowUpBit, paint);
		
		data = TotalDataByGSM.getHashMapMeasure(hMap, TotalStruct.TotalMeasurePara._gprsBLER.name());
		value = ""+(data.getMaxValue() != -9999 ? data.getMaxValue() : "");
		cv.drawText(value, colsWidth * 3 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 4- rowUpBit, paint);
		value = ""+(data.getMinValue() != -9999 ? data.getMinValue() : "");
		cv.drawText(value, colsWidth * 4 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 4- rowUpBit, paint);
		value =""+ (data.getKeySum() == -9999 ? "" :
			UtilsMethod.decFormat.format(data.getKeySum() * 1f / (data.getKeyCounts() != 0 ? data.getKeyCounts() : 1)));
		cv.drawText(value, colsWidth * 5 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 4- rowUpBit, paint);*/
		
		data = TotalDataByGSM.getHashMapMeasure(hMap, TotalStruct.TotalMeasurePara._bepGMSK.name());
		value = ""+(data.getMaxValue() != -9999 ? data.getMaxValue() : "");
		cv.drawText(value, colsWidth * 3 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 2- rowUpBit, paint);
		value = ""+(data.getMinValue() != -9999 ? data.getMinValue() : "");
		cv.drawText(value, colsWidth * 4 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 2- rowUpBit, paint);
		value =""+ (data.getKeySum() == -9999 ? "" :
			UtilsMethod.decFormat.format(data.getKeySum() * 1f / (data.getKeyCounts() != 0 ? data.getKeyCounts() : 1)));
		cv.drawText(value, colsWidth * 5 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 2- rowUpBit, paint);
		
		data = TotalDataByGSM.getHashMapMeasure(hMap, TotalStruct.TotalMeasurePara._bep8PSK.name());
		value = ""+(data.getMaxValue() != -9999 ? data.getMaxValue() : "");
		cv.drawText(value, colsWidth * 3 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 3- rowUpBit, paint);
		value = ""+(data.getMinValue() != -9999 ? data.getMinValue() : "");
		cv.drawText(value, colsWidth * 4 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 3- rowUpBit, paint);
		value =""+ (data.getKeySum() == -9999 ? "" :
			UtilsMethod.decFormat.format(data.getKeySum() * 1f / (data.getKeyCounts() != 0 ? data.getKeyCounts() : 1)));
		cv.drawText(value, colsWidth * 5 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 3- rowUpBit, paint);
		
		data = TotalDataByGSM.getHashMapMeasure(hMap, TotalStruct.TotalMeasurePara._cvBepGMSK.name());
		value = ""+(data.getMaxValue() != -9999 ? data.getMaxValue() : "");
		cv.drawText(value, colsWidth * 3 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 4- rowUpBit, paint);
		value = ""+(data.getMinValue() != -9999 ? data.getMinValue() : "");
		cv.drawText(value, colsWidth * 4 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 4- rowUpBit, paint);
		value =""+ (data.getKeySum() == -9999 ? "" :
			UtilsMethod.decFormat.format(data.getKeySum() * 1f / (data.getKeyCounts() != 0 ? data.getKeyCounts() : 1)));
		cv.drawText(value, colsWidth * 5 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 4- rowUpBit, paint);
		
		data = TotalDataByGSM.getHashMapMeasure(hMap, TotalStruct.TotalMeasurePara._cvBep8PSK.name());
		value = ""+(data.getMaxValue() != -9999 ? data.getMaxValue() : "");
		cv.drawText(value, colsWidth * 3 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 5- rowUpBit, paint);
		value = ""+(data.getMinValue() != -9999 ? data.getMinValue() : "");
		cv.drawText(value, colsWidth * 4 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 5- rowUpBit, paint);
		value =""+ (data.getKeySum() == -9999 ? "" :
			UtilsMethod.decFormat.format(data.getKeySum() * 1f / (data.getKeyCounts() != 0 ? data.getKeyCounts() : 1)));
		cv.drawText(value, colsWidth * 5 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 5- rowUpBit, paint);
		
		data = TotalDataByGSM.getHashMapMeasure(hMap, TotalStruct.TotalMeasurePara._cvalue.name());
		value = ""+(data.getMaxValue() != -9999 ? data.getMaxValue() : "");
		cv.drawText(value, colsWidth * 3 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 6- rowUpBit, paint);
		value = ""+(data.getMinValue() != -9999 ? data.getMinValue() : "");
		cv.drawText(value, colsWidth * 4 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 6- rowUpBit, paint);
		value =""+ (data.getKeySum() == -9999 ? "" :
			UtilsMethod.decFormat.format(data.getKeySum() * 1f / (data.getKeyCounts() != 0 ? data.getKeyCounts() : 1)));
		cv.drawText(value, colsWidth * 5 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 6- rowUpBit, paint);
		
		cv.save();
		cv.restore();
		return bm;
		
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
	@Override
	protected Parcelable onSaveInstanceState() {
		// TODO Auto-generated method stub
		if(mbmp !=null && !mbmp.isRecycled()){
			mbmp.recycle();   
		}
		mbmp =null;
		return super.onSaveInstanceState();
	}
	/**
	 * 消息处理
	 */
	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(TotalDataByGSM.currentTotal == WalkStruct.ShowTotalType.SecondGeData){
				mbmp = CreateTableData(CreateTable(CreateBlankBitmap()),TotalDataByGSM.getInstance().getMeasuePara());
				invalidate();
			}
		}
	};
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		viewWidth = w;
		viewHeight = h;
		rate = (float)h /366;
		mbmp = null;
		mbmp = CreateTableData(CreateTable(CreateBlankBitmap()),TotalDataByGSM.getInstance().getMeasuePara());
	}
}
