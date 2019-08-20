/**
 * 
 */
package com.walktour.gui.total;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalPdp;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.BasicTotalView;
import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author qihang.li
 * PDP统计界面
 */
public class TotalPdpView  extends BasicTotalView {
	
	private static String tag = "TotalPdpView";
	private boolean isRegisterReceiver = false;
	
	
	int tableRows = 6; // 行数
	int tableCols = 3; // 列数
	private float oneTableheight = 0;

	public TotalPdpView(Context context) {
		super(context);
	}
	
	public TotalPdpView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@SuppressWarnings("unchecked")
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		ArrayList<TotalResult> resultList = new ArrayList<>();
		
		//HashMap
		Map<String, Map<String,Map<String,Long>>> specialTimesHM = TotalDataByGSM.getInstance().getSpecialTimes();
		Map<String, Map<String,Long>> tatalPdpHM = specialTimesHM.get("TotalPdp");
        
        if( tatalPdpHM == null ){
        	CreateOneTable(canvas,0);
        }else{
        	Iterator<Entry<String, Map<String,Long>>> iter = tatalPdpHM.entrySet().iterator();
            while( iter.hasNext() ) {
                Map.Entry<String, Map<String,Long>> entry = iter.next();
                String rate = entry.getKey();
                TotalResult result = new TotalResult();
                result.rate = rate;
    			HashMap<String, Long> valHM = (HashMap<String, Long>) entry.getValue();
                result.request = Integer.parseInt( valHM.get( TotalPdp._pdpRequest.name() )+"" );
                result.success = Integer.parseInt( valHM.get( TotalPdp._pdpSuccess.name() )+"" );
                result.delay   = Integer.parseInt( valHM.get( TotalPdp._pdpDelay.name() )+"" );
                resultList.add(result);
            }
        	for( int i =0;i<resultList.size();i++){
            	CreateOneTable(canvas,i);
            	CreateOneTableData(canvas,i,resultList.get(i));
            }
        }
	}
	
	/**
	 * 创建表格
	 * @param cv 要创建表格的位图
	 * @return 输出位图
	 */
	protected void CreateOneTable(Canvas cv,int index) {
		int width = this.getWidth();
		float startx = 1;
		float starty = 0;
		float stopx = 0;
		float stopy = 0;
		float tablewidth = width;
		float colsWidth = tablewidth / tableCols; // 列宽
		
		oneTableheight = rowHeight*(tableRows+1);
		//整体的Y轴位移
		float offsetY = index*oneTableheight ;

		/* 画四边 */
		cv.drawLine(startx, marginSize + offsetY, width - marginSize, marginSize+ offsetY, linePaint);
		cv.drawLine(startx, rowHeight * tableRows + offsetY, width - marginSize, rowHeight * tableRows+ offsetY, linePaint);
		cv.drawLine(startx, marginSize + offsetY, startx, rowHeight * tableRows+ offsetY, linePaint);
		cv.drawLine(width - marginSize , marginSize+ offsetY, width - marginSize, rowHeight * tableRows+ offsetY, linePaint);
		/* 画横线 */
		for (int i = 1; i < tableRows; i++) {
			startx = 1;
			starty = rowHeight * i ;
			stopx = width - marginSize;
			stopy = rowHeight * i;
			cv.drawLine(startx, starty+ offsetY, stopx, stopy+ offsetY, linePaint);
		}
		/* 画竖线 */
		startx = colsWidth * 2;
		starty = rowHeight;
		stopx = colsWidth * 2;
		stopy = rowHeight * 6;
		cv.drawLine(startx, starty+ offsetY, stopx, stopy+ offsetY, linePaint);
		
		float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2
		
		String paraname;
		
		//2/3G表格
		paraname = "PDP";
		cv.drawText(paraname,(colsWidth * 3 - fontPaint.measureText(paraname))/ 2, 
				rowHeight * 1 - rowUpBit + offsetY, fontPaint);
		
		paraname = getContext().getString(R.string.total_qos_type);
		cv.drawText(paraname,colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))/ 2, 
				rowHeight * 2 - rowUpBit+ offsetY, fontPaint);
		
		paraname = getContext().getString(R.string.total_attemptCount);
		cv.drawText(paraname,colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))/ 2, 
				rowHeight * 3 - rowUpBit+ offsetY, fontPaint);
		
		paraname = getContext().getString(R.string.total_SuccessCounts);
		cv.drawText(paraname,colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))/ 2, 
				rowHeight * 4 - rowUpBit+ offsetY, fontPaint);
		
		paraname = getContext().getString(R.string.total_SuccessRate);
		cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))/ 2, 
				rowHeight * 5 - rowUpBit+ offsetY, fontPaint);
		
		paraname = getContext().getString(R.string.total_AverageDelay);
		cv.drawText(paraname,colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))/ 2, 
				rowHeight * 6 - rowUpBit+ offsetY, fontPaint);
		
//		cv.save(Canvas.ALL_SAVE_FLAG);
//		cv.restore();
//		return null;
	}

	/**
	 * 创建表格数据
	 * 
	 * @param cv
	 *            要创建表格的位图
	 * @param result
	 *            表格数据
	 * @return 输出位图
	 */
	protected void CreateOneTableData(Canvas cv,int index, TotalResult result) {
		int width = this.getWidth();
		float tablewidth = width;
		int tableCols = 3; // 列数
		float colsWidth = tablewidth / tableCols; // 列宽
		float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2
		
		//整体的Y轴位移
		float offsetY = index*oneTableheight ;

		String value;
		
		value = ""+ result.rate;// QOS类型
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 2 - rowUpBit + offsetY, paramPaint);
		
		value = ""+ result.request;// 发送尝试次数
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 3 - rowUpBit + offsetY, paramPaint);
		
		value = "" + result.success;// 发送成功次数
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 4 - rowUpBit + offsetY, paramPaint);
		
		value = "" +String.format("%d", result.success*100/result.request );// 成功率
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 5 - rowUpBit + offsetY, paramPaint);
		
		value = "" + String.format("%d", result.delay/result.success) ;// 平均时延
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 6 - rowUpBit + offsetY, paramPaint);
		
//		cv.save(Canvas.ALL_SAVE_FLAG);
//		cv.restore();
//		return null;

	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		IntentFilter filter = new IntentFilter(); // 注册一个消息过滤器
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
			if (intent.getAction().equals(TotalDataByGSM.TotalResultToPicture)) {
				String path = intent
						.getStringExtra(TotalDataByGSM.TotalSaveFilePath)
						+ "-Email.jpg";
				LogUtil.w(tag, "--save current to file---" + path);
				TotalPdpView.this.buildDrawingCache();
				UtilsMethod.SaveBitmapToFile(
						TotalPdpView.this.getDrawingCache(), path);
			} else {
				invalidate();
				//请求重新Mesure
				requestLayout();
			}
		}
	};
	
	@SuppressWarnings("unchecked")
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		int tableCounts = 0;
		
		//HashMap
		Map<String, Map<String, Map<String,Long>>> specialTimesHM = TotalDataByGSM.getInstance().getSpecialTimes();
		Map<String, Map<String,Long>> tatalPdpHM = specialTimesHM.get("TotalPdp");
        if( tatalPdpHM == null ){
        	tableCounts =1 ;
        }else{
        	Iterator<Entry<String, Map<String,Long>>> iter = tatalPdpHM.entrySet().iterator();
            while( iter.hasNext() ) {
            	iter.next();
            	tableCounts++;
            }
        }
		
		oneTableheight = rowHeight*(tableRows+1);
        int height = (int) (oneTableheight * tableCounts) ;
        
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
	
	private class TotalResult {
		public String rate = "";
		public int request;
		public int success;
		public int delay;
	}
}
