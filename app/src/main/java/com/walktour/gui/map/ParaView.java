package com.walktour.gui.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.ShowInfo;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.model.YwDataModel;

/**
 * 应用层业务信息
 * Author:tangwq
 * Create Time:2010/9/16
 */
public class ParaView extends ImageView{
	private final Paint mPaint = new Paint();
	private Bitmap	mbmp=null;
	private int 	viewWidth;
	private int 	viewHeight;
	private float 	rate=1;
	private boolean isRegisterReceiver = false;
	private int 	tableRows = 14;		//行数
	private int 	tableCols = 5;		//列数
	private float 	textSize  = 11;	//字体大小
	private float strokeWidth = 1;
	
	public ParaView(Context context) {
		super(context);
		if( android.os.Build.MODEL.equals("HTC PG09410") ){
    		textSize = 8;
    		strokeWidth = 1f;
    	}
		// TODO Auto-generated constructor stub
	}
	public ParaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if( android.os.Build.MODEL.equals("HTC PG09410") ){
    		textSize = 8;
    		strokeWidth = 1f;
    	}
		// TODO Auto-generated constructor stub
		//mbmp = CreateTable(CreateBlankBitmap());
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		CreateTable(canvas);
		CreateTableData(canvas, ShowInfo.data);
	}
	/**
	 * 创建表格
	 * @param bm 要创建表格的位图
	 * @return   输出位图
	 */
	protected void CreateTable(Canvas cv){
		int 	width 		= this.getWidth();
		int 	height 		= this.getHeight();
		float 	marginSize 	= 1 * rate;
		float 	startx 		= 1;
		float 	starty 		= 0;
		float 	stopx 		= 0;
		float 	stopy 		= 0;
		float 	tablewidth 	= width ;
		float 	tableheight = height;
		float 	rowsHeight 	= tableheight/tableRows;	//行高
		float 	colsWidth 	= tablewidth/tableCols;		//列宽
		float 	rowUpBit 	= (rowsHeight - textSize)/2 ;//指定行上升位数,为行高-字体高度 再除2
		Paint 	paint 		= new Paint();

		paint.setColor(Color.YELLOW);
		paint.setStrokeWidth(strokeWidth);
		cv.drawLine(1, marginSize, width-marginSize, marginSize, paint);
		cv.drawLine(1, height-marginSize, width-marginSize, height-marginSize , paint);
		cv.drawLine(1, marginSize, 1, height-marginSize, paint);
		cv.drawLine(width-marginSize, 1, width-marginSize, height-marginSize, paint);
		//横线
		for(int i=0;i<tableRows - 1;i++){
			startx = 1;
			starty =  rowsHeight * (i+1);
			stopx = width-marginSize;
			stopy = rowsHeight *(i+1);
			cv.drawLine(startx, starty, stopx, stopy, paint);
		}
		
		startx = colsWidth * 3;
		starty =  rowsHeight * 1;
		stopx = colsWidth * 3;
		stopy = rowsHeight * tableRows ;
		cv.drawLine(startx, starty, stopx, stopy, paint);
		
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);	
		paint.setColor(Color.YELLOW);
		paint.setTypeface(null);
		paint.setTextSize(textSize *rate);
		String paraname;
		paraname = getContext().getString(R.string.data_applicationLayerInfo);
		cv.drawText(paraname,  (tablewidth - paint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, paint);
		paraname = getContext().getString(R.string.data_ftpDLThroughput);
		cv.drawText(paraname, (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 2 - rowUpBit, paint);
		paraname = getContext().getString(R.string.data_ftpULThroughput);
		cv.drawText(paraname, (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 3 - rowUpBit, paint);
		paraname = getContext().getString(R.string.data_ftpDLMeanRate);
		cv.drawText(paraname, (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 4 - rowUpBit, paint);
		paraname = getContext().getString(R.string.data_ftpULMeanRate);
		cv.drawText(paraname, (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 5 - rowUpBit, paint);
		paraname = getContext().getString(R.string.data_ftpDLProgress);
		cv.drawText(paraname, (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 6 - rowUpBit, paint);
		paraname = getContext().getString(R.string.data_ftpULProgress);
		cv.drawText(paraname, (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 7 - rowUpBit, paint);
		paraname = getContext().getString(R.string.data_pingDelay);
		cv.drawText(paraname, (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 8 - rowUpBit, paint);
		paraname = getContext().getString(R.string.data_httpDLThroughput);
		cv.drawText(paraname, (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 9 - rowUpBit, paint);
		paraname = getContext().getString(R.string.data_wapDLThroughput);
		cv.drawText(paraname, (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 10 - rowUpBit, paint);
		paraname = getContext().getString(R.string.data_pop3Throughput);	//data_Mileage
		cv.drawText(paraname, (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 11 - rowUpBit, paint);
		paraname = getContext().getString(R.string.data_smtpThroughput);	//data_TimeLength
		cv.drawText(paraname, (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 12 - rowUpBit, paint);
		paraname = getContext().getString(R.string.data_ftpPeakValue);
		cv.drawText(paraname, (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 13 - rowUpBit, paint);
		paraname = getContext().getString(R.string.data_useTime);
		cv.drawText(paraname, (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 14 - rowUpBit, paint);
		/*paraname = getContext().getString(R.string.data_FTPDLAverage);
		cv.drawText(paraname, (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 15 - rowUpBit, paint);
		paraname = getContext().getString(R.string.data_FTPULAverage);
		cv.drawText(paraname, (colsWidth * 3 - paint.measureText(paraname)) / 2, rowsHeight * 16 - rowUpBit, paint);*/
	}
	/**
	 * 创建表格数据
	 * @param bm  要创建表格的位图
	 * @param data  表格数据
	 * @return    输出位图
	 */
	protected void CreateTableData(Canvas cv,GenericData data){
		int width = this.getWidth();
		int height = this.getHeight();
		float 	tablewidth 	= width ;
		float 	tableheight = height;
		float 	rowsHeight 	= tableheight/tableRows;	//行高
		float 	colsWidth 	= tablewidth/tableCols;		//列宽
		float 	rowUpBit 	= (rowsHeight - textSize)/2 ;//指定行上升位数,为行高-字体高度 再除2
		
		Paint paint = new Paint();
		
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);	
		paint.setColor(Color.WHITE);
		paint.setTypeface(null);
		paint.setTextSize(textSize * rate);
		String value;
		YwDataModel ywData = ShowInfo.getInstance().getYwDataModel();
		value = (ywData.getFtpDlThrput().equals("") ? "": ywData.getFtpDlThrput()+" kbps");
		cv.drawText(value, colsWidth * 3 + (colsWidth * 2 - paint.measureText(value)) / 2, rowsHeight * 2 - rowUpBit, paint);
		value = (ywData.getFtpUlThrput().equals("")? "" : ywData.getFtpUlThrput()+" kbps");
		cv.drawText(value, colsWidth * 3 + (colsWidth * 2 - paint.measureText(value)) / 2, rowsHeight * 3 - rowUpBit, paint);
		value = (ywData.getFtpDlMeanRate().equals("") ? "": ywData.getFtpDlMeanRate()+" kbps");
		cv.drawText(value, colsWidth * 3 + (colsWidth * 2 - paint.measureText(value)) / 2, rowsHeight * 4 - rowUpBit, paint);
		value = (ywData.getFtpUlMeanRate().equals("") ? "": ywData.getFtpUlMeanRate()+" kbps");
		cv.drawText(value, colsWidth * 3 + (colsWidth * 2 - paint.measureText(value)) / 2, rowsHeight * 5 - rowUpBit, paint);
		value = (ywData.getFtpDlProgress().equals("") ? "": ywData.getFtpDlProgress()+" %");
		cv.drawText(value, colsWidth * 3 + (colsWidth * 2 - paint.measureText(value)) / 2, rowsHeight * 6 - rowUpBit, paint);
		value = (ywData.getFtpUlProgress().equals("") ? "": ywData.getFtpUlProgress()+" %");
		cv.drawText(value, colsWidth * 3 + (colsWidth * 2 - paint.measureText(value)) / 2, rowsHeight * 7 - rowUpBit, paint);
		value = ywData.getPingDelay();
		cv.drawText(value, colsWidth * 3 + (colsWidth * 2 - paint.measureText(value)) / 2, rowsHeight * 8 - rowUpBit, paint);
		value = ywData.getHttpDlThrput().equals("")?"" : ywData.getHttpDlThrput()+"kbps" ;
		cv.drawText(value, colsWidth * 3 + (colsWidth * 2 - paint.measureText(value)) / 2, rowsHeight * 9 - rowUpBit, paint);
		value = ( ywData.getWapDlThrput().equals("")? "" : ywData.getWapDlThrput()+"kbps" );
		cv.drawText(value, colsWidth * 3 + (colsWidth * 2 - paint.measureText(value)) / 2, rowsHeight * 10 - rowUpBit, paint);
		value = ywData.getPop3Thrput().equals("")?"":ywData.getPop3Thrput()+"kbps" ;	//TraceInfoInterface.traceData.getTestMileage();	// 
		cv.drawText(value, colsWidth * 3 + (colsWidth * 2 - paint.measureText(value)) / 2, rowsHeight * 11 - rowUpBit, paint);
		value = ywData.getSmtpThrput().equals("")? "":ywData.getSmtpThrput()+"kbps" ;	//TraceInfoInterface.traceData.getTestTimeLength();	// 
		cv.drawText(value, colsWidth * 3 + (colsWidth * 2 - paint.measureText(value)) / 2, rowsHeight * 12 - rowUpBit, paint);
		value = (ywData.getPeakValue().equals("") ? "": ywData.getPeakValue()+" kbps");
		cv.drawText(value, colsWidth * 3 + (colsWidth * 2 - paint.measureText(value)) / 2, rowsHeight * 13 - rowUpBit, paint);
		value = (ywData.getUseTimes().equals("") ? "": ywData.getUseTimes() + " s");
		cv.drawText(value, colsWidth * 3 + (colsWidth * 2 - paint.measureText(value)) / 2, rowsHeight * 14 - rowUpBit, paint);
		/*value = (ywData.getFtpDlAverage().equals("") ? "": ywData.getFtpDlAverage()+" kbps");
		cv.drawText(value, colsWidth * 3 + (colsWidth * 2 - paint.measureText(value)) / 2, rowsHeight * 15 - rowUpBit, paint);
		value = (ywData.getFtpUlAverage().equals("") ? "": ywData.getFtpUlAverage()+" kbps");
		cv.drawText(value, colsWidth * 3 + (colsWidth * 2 - paint.measureText(value)) / 2, rowsHeight * 16 - rowUpBit, paint);*/

		cv.save();
		cv.restore();
	}
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		IntentFilter filter = new IntentFilter(); //注册一个消息过滤器
		filter.addAction(WalkMessage.testDataUpdate); 
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
			 String action = intent.getAction(); 
			 if(TraceInfoInterface.currentShowTab.equals(WalkStruct.ShowInfoType.Data) 
			         && action.equalsIgnoreCase(WalkMessage.testDataUpdate)
			         && !ApplicationModel.getInstance().isFreezeScreen()){
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
		rate = (float)h /366;//(float)w /320;
		mbmp = null;
	}
}
