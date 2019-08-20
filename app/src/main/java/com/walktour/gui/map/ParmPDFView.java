package com.walktour.gui.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.dinglicom.dataset.DatasetManager;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.control.config.ParameterSetting;
import com.walktour.framework.view.BasicParamView;

import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * 实时参数PDF呈现
 * @author zhihui.lian
 */
public class ParmPDFView extends BasicParamView {
    
    private Context mContext = null;
    int tableCols = 4; 
    
    int tableColsSec = 4;
    
    float strokeWidth = 1;
    
    private float rowsHeight;
    
    private float rowUpBit;
    
    private int viewHeight;

	private Paint titlePaint;


	private ParameterSetting setting;
	
	public ParmPDFView(Context context) {
		super(context);
		mContext = context;
		setting = ParameterSetting.getInstance();
	}
	public ParmPDFView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	private String sp1ID = "";
	
	private String sp2ID = "";

	private int[] sp1;

	private int[] sp2;
	
	
	public void setDefsp1Andsp2(String sp1 , String sp2){
		setting.updateDistributionParams(0, setting.getParameterById(sp1));
		sp1ID = sp1;
		setting.updateDistributionParams(1, setting.getParameterById(sp2));
		sp2ID = sp2;
	}
	
	public void setSp1Id(String id){
		this.sp1ID = id;
		setting.updateDistributionParams(0, setting.getParameterById(sp1ID));
		//TraceInfoInterface.traceData.reSetDistributionData();
		DatasetManager.getInstance(mContext).rebuildDistributionParams(setting.getParameterById(sp1ID));
		invalidate();
	}
	
	public void setSp2Id(String id){
		this.sp2ID = id;
		setting.updateDistributionParams(1, setting.getParameterById(sp2ID));
		//TraceInfoInterface.traceData.reSetDistributionData();
		DatasetManager.getInstance(mContext).rebuildDistributionParams(setting.getParameterById(sp2ID));
		invalidate();
	}
	
	
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param canvas
     * @see com.walktour.framework.view.BasicParamView#initView(android.graphics.Canvas)
     */
    @Override
    public void initView(Canvas canvas) {
        CreateTable(canvas);
    }
    
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		CreateTableData(canvas);
	}

	protected void CreateTable(Canvas cv) {
		sp1 = TraceInfoInterface.traceData.getDistributionData(sp1ID);
		sp2 = TraceInfoInterface.traceData.getDistributionData(sp2ID);
		
		float colsTextWidthTotal = this.getWidth() / 2 ;// 文字列宽

		if (viewHeight == 0) {
			viewHeight = this.getViewHeight()  - 1;
		}
		tableRows = 21;
		rowsHeight = viewHeight / tableRows;
		cv.drawLine(1,1 , this.getWidth() - 1, 1, linePaint);
		cv.drawLine(colsTextWidthTotal, 1, colsTextWidthTotal, this.getHeight() - 5, linePaint);
		rowUpBit = (rowsHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2
		initTitlePaint(); // 初始化标题字体
		cv.save();
		cv.restore();
	}
	
	
	
	/**
	 * 标题头字体
	 */
	
	private void initTitlePaint(){
		titlePaint = new Paint();
		titlePaint.setAntiAlias(true);
		titlePaint.setStyle(Paint.Style.FILL);   
		titlePaint.setColor(Color.WHITE);
		titlePaint.setTypeface(null);
        titlePaint.setTextSize(rowsHeight);
		
	}
	
	
	/**
	 * 创建表格数据
	 * @param bm  要创建表格的位图
	 * @param data  表格数据
	 * @return    输出位图
	 */
	protected void CreateTableData(Canvas cv){
		
		float colsTextWidthTotal = this.getWidth() / 2;	
		String str = "";
		/*if(sp1 != null){
			for (int i = 0; i < sp1.length; i++) {
				str += i + ":" + sp1[i] + ";";
			}
			LogUtil.w("parmPdf", "--distr:" + sp1ID + "-" + str);
		}*/
		//Old
//		for (int i = 0; i < 5; i++) {
//			//画第一个参数
//			cv.drawText(setting.getParameterById(sp1ID).getThresholdList().get(i).getValue2Show(), 10, rowsHeight/2 + (rowsHeight * (1 + (i * 4))) - rowUpBit, getDrawPaint(0));
//			//画第二个参数
//			cv.drawText(setting.getParameterById(sp2ID).getThresholdList().get(i).getValue2Show(), 10, rowsHeight/2 + (rowsHeight * (2 + (i * 4))) - rowUpBit, getDrawPaint(1));
//			
//			NumberFormat nf = NumberFormat.getPercentInstance();
//			nf.setMaximumFractionDigits(2);
//			/*
//		     * setMinimumFractionDigits设置成2
//		     * 如果不这么做，那么当value的值是100.00的时候返回100
//		     * 而不是100.00
//		     */
//		    //nf.setMinimumFractionDigits(2);
//		    nf.setRoundingMode(RoundingMode.HALF_UP);
//		    
//			if(sp1!= null && sp1.length != 0){
//				cv.drawText((nf.format(sp1[i+1] * 1.0 /sp1[0])), colsTextWidthTotal / 2, rowsHeight/2 + (rowsHeight * (1 + (i * 4))) - rowUpBit, getDrawPaint(0));
//				//cv.drawText((String.valueOf(sp1[i+1] )), colsTextWidthTotal / 3 * 2, rowsHeight/2 + (rowsHeight * (1 + (i * 4))) - rowUpBit, getDrawPaint(0));
//				cv.drawRect(colsTextWidthTotal, rowsHeight/2 + (rowsHeight * (4 * i)) , colsTextWidthTotal + (colsTextWidthTotal - (5 * systemScale)) / sp1[0] * sp1[i+1], rowsHeight/2 + (rowsHeight * (1 + (i * 4))) , getDrawPaint(0));
//			}
//			if(sp2!= null && sp2.length != 0){
//				cv.drawText((nf.format(sp2[i+1] * 1.0 /sp2[0])), colsTextWidthTotal / 2, rowsHeight/2 + (rowsHeight * (2 + (i * 4))) - rowUpBit, getDrawPaint(1));
//				//cv.drawText(String.valueOf(sp2[i+1]), colsTextWidthTotal / 3 * 2, rowsHeight/2 + (rowsHeight * (2 + (i * 4))) - rowUpBit, getDrawPaint(1));
//				cv.drawRect(colsTextWidthTotal, rowsHeight/2 + (rowsHeight * (1 + (i * 4))) , colsTextWidthTotal + (colsTextWidthTotal - (5 * systemScale)) / sp2[0] * sp2[i+1], rowsHeight/2 + (rowsHeight * (2 + (i * 4))) , getDrawPaint(1));
//			}
//		}
		//画第一个参数
		int parameterOneSize = setting.getParameterById(sp1ID).getThresholdList().size();
		for (int i = 0; i < parameterOneSize; i++) {
		//画第一个参数
		cv.drawText(setting.getParameterById(sp1ID).getThresholdList().get(i).getValue2Show(), 10, rowsHeight/2 + (rowsHeight * (1 + (i * 4))) - rowUpBit, getDrawPaint(0));
//		//画第二个参数
//		cv.drawText(setting.getParameterById(sp2ID).getThresholdList().get(i).getValue2Show(), 10, rowsHeight/2 + (rowsHeight * (2 + (i * 4))) - rowUpBit, getDrawPaint(1));
		
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMaximumFractionDigits(2);
		/*
	     * setMinimumFractionDigits设置成2
	     * 如果不这么做，那么当value的值是100.00的时候返回100
	     * 而不是100.00
	     */
	    //nf.setMinimumFractionDigits(2);
	    nf.setRoundingMode(RoundingMode.HALF_UP);
	    
		if(sp1!= null && sp1.length != 0){
			cv.drawText((nf.format(sp1[i+1] * 1.0 /sp1[0])), colsTextWidthTotal / 2, rowsHeight/2 + (rowsHeight * (1 + (i * 4))) - rowUpBit, getDrawPaint(0));
			//cv.drawText((String.valueOf(sp1[i+1] )), colsTextWidthTotal / 3 * 2, rowsHeight/2 + (rowsHeight * (1 + (i * 4))) - rowUpBit, getDrawPaint(0));
			cv.drawRect(colsTextWidthTotal, rowsHeight/2 + (rowsHeight * (4 * i)) , colsTextWidthTotal + (colsTextWidthTotal - (5 * systemScale)) / sp1[0] * sp1[i+1], rowsHeight/2 + (rowsHeight * (1 + (i * 4))) , getDrawPaint(0));
		}
//		if(sp2!= null && sp2.length != 0){
//			cv.drawText((nf.format(sp2[i+1] * 1.0 /sp2[0])), colsTextWidthTotal / 2, rowsHeight/2 + (rowsHeight * (2 + (i * 4))) - rowUpBit, getDrawPaint(1));
//			//cv.drawText(String.valueOf(sp2[i+1]), colsTextWidthTotal / 3 * 2, rowsHeight/2 + (rowsHeight * (2 + (i * 4))) - rowUpBit, getDrawPaint(1));
//			cv.drawRect(colsTextWidthTotal, rowsHeight/2 + (rowsHeight * (1 + (i * 4))) , colsTextWidthTotal + (colsTextWidthTotal - (5 * systemScale)) / sp2[0] * sp2[i+1], rowsHeight/2 + (rowsHeight * (2 + (i * 4))) , getDrawPaint(1));
//		}
		
	}
	//画第二个参数
	int parameterTwoSize = setting.getParameterById(sp2ID).getThresholdList().size();
	for (int i = 0; i < parameterTwoSize; i++) {
		//画第二个参数
		cv.drawText(setting.getParameterById(sp2ID).getThresholdList().get(i).getValue2Show(), 10, rowsHeight/2 + (rowsHeight * (2 + (i * 4))) - rowUpBit, getDrawPaint(1));
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMaximumFractionDigits(2);
		 nf.setRoundingMode(RoundingMode.HALF_UP);
		if(sp2!= null && sp2.length != 0){
			cv.drawText((nf.format(sp2[i+1] * 1.0 /sp2[0])), colsTextWidthTotal / 2, rowsHeight/2 + (rowsHeight * (2 + (i * 4))) - rowUpBit, getDrawPaint(1));
			//cv.drawText(String.valueOf(sp2[i+1]), colsTextWidthTotal / 3 * 2, rowsHeight/2 + (rowsHeight * (2 + (i * 4))) - rowUpBit, getDrawPaint(1));
			cv.drawRect(colsTextWidthTotal, rowsHeight/2 + (rowsHeight * (1 + (i * 4))) , colsTextWidthTotal + (colsTextWidthTotal - (5 * systemScale)) / sp2[0] * sp2[i+1], rowsHeight/2 + (rowsHeight * (2 + (i * 4))) , getDrawPaint(1));
		}
	}
		
		cv.save();
		cv.restore();
	}
	
	
	
	
	
	
	/**
	 * 颜色变化
	 * @param setType
	 * @return
	 */
	private Paint getDrawPaint(int setType) {

		switch (setType) {
		case 0:
			paramPaint.setColor(Color.parseColor("#0071BC"));
			break;
		case 1:
			paramPaint.setColor(Color.parseColor("#29ABE2"));
			break;
		default:
			break;
		}

		return paramPaint;
	}
	
    
    /**
     * @param viewHeight the viewHeight to set
     */
    public int getViewHeight() {
        if(viewHeight == 0){
            viewHeight = this.getHeight();
        }
        return viewHeight;
    }

}
