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
import android.widget.ImageView;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.model.TotalTaskModel;

public class TotalTaskView extends ImageView {
	private static String tag="TotalTaskView";
	private Bitmap  mbmp=null;
	private int viewWidth;
	private int viewHeight;
	private float rate=1;
	float textSize = 10;	//字体大小
	private final Paint mPaint = new Paint();
	private boolean isRegisterReceiver = false;
	public TotalTaskView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public TotalTaskView(Context context, AttributeSet attrs) {
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
			 //mbmp = CreateTableData(CreateTable(CreateBlankBitmap()),TotalDataDispose.getInstance().getTotalTaskModel());
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
		int tableRows = 17;	//行数
		int tableCols = 5;	//列数
		float rowsHeight = tableheight/tableRows;	//行高
		float colsWidth = tablewidth/tableCols;		//列宽
		
		paint.setColor(getResources().getColor(R.color.param_view_line_color));
		paint.setStrokeWidth(1f);
		cv.drawLine(startx, 1*rate, width-1*rate, 1*rate, paint);
		cv.drawLine(startx, height-1*rate, width-1*rate, height-1*rate , paint);
		cv.drawLine(startx, 1*rate, startx, height-1*rate, paint);
		cv.drawLine(width-1*rate, startx, width-1*rate, height, paint);
		
		for(int i=0;i<tableRows - 1;i++){
			startx = 1;
			starty =  rowsHeight * (i+1);
			stopx = width;
			stopy = rowsHeight *(i+1);
			cv.drawLine(startx, starty, stopx, stopy, paint);
		}
		for(int i=0;i<tableCols - 1;i++){
			startx = colsWidth * (i+1);
			starty =  0;
			stopx = colsWidth * (i+1);
			stopy = height;
			cv.drawLine(startx, starty, stopx, stopy, paint);
		}
		
		float rowUpBit = (rowsHeight - textSize)/2 ;		//指定行上升位数,为行高-字体高度 再除2
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);	
		paint.setColor(Color.WHITE);
		paint.setTypeface(null);
		paint.setTextSize(textSize *rate);
		String paraname;
		paraname = getContext().getString(R.string.total_try)+getContext().getString(R.string.total_times);//"尝试次数";
		cv.drawText(paraname, colsWidth * 1 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 1- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_callSucc)+"/"+getContext().getString(R.string.total_rate);//"接通/率";
		cv.drawText(paraname, colsWidth * 2 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 1- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_drop)+getContext().getString(R.string.total_rate);///"掉话/率";
		cv.drawText(paraname, colsWidth * 3 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 1- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_delay);//"时延";
		cv.drawText(paraname, colsWidth * 4 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 1- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_dial);//"Dial";
		cv.drawText(paraname, colsWidth * 0 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 2- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_videoDial);//"Video T";
		cv.drawText(paraname, colsWidth * 0 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 3- rowUpBit, paint);
		
		paraname = getContext().getString(R.string.total_try)+getContext().getString(R.string.total_times);//"尝试次数";
		cv.drawText(paraname, colsWidth * 1 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 4- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_success)+getContext().getString(R.string.total_times);//"成功次数";
		cv.drawText(paraname, colsWidth * 2 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 4- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_success)+getContext().getString(R.string.total_rate);//"成功率";
		cv.drawText(paraname, colsWidth * 3 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 4- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_delay);//"时延";
		cv.drawText(paraname, colsWidth * 4 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 4- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_attach);//"Attach";
		cv.drawText(paraname, colsWidth * 0 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 5- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_pdp);//"PDP";
		cv.drawText(paraname, colsWidth * 0 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 6- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_ping);//"Ping";
		cv.drawText(paraname, colsWidth * 0 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 7- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_wap)+" "+getContext().getString(R.string.total_rf);//"Wap Ref";
		cv.drawText(paraname, colsWidth * 0 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 8- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_wap)+" "+getContext().getString(R.string.total_login);//"Wap Log";
		cv.drawText(paraname, colsWidth * 0 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 9- rowUpBit, paint);
		
		paraname = getContext().getString(R.string.total_try)+getContext().getString(R.string.total_times);//"尝试次数";
		cv.drawText(paraname, colsWidth * 1 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 10- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_success)+getContext().getString(R.string.total_times);//"成功次数";
		cv.drawText(paraname, colsWidth * 2 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 10- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_drop)+getContext().getString(R.string.total_rate);//"掉线率";
		cv.drawText(paraname, colsWidth * 3 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 10- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_speed)+getContext().getString(R.string.total_rate);//"速率";
		cv.drawText(paraname, colsWidth * 4 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 10- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_ftp)+" "+getContext().getString(R.string.total_upload);//"FTP UL";
		cv.drawText(paraname, colsWidth * 0 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 11- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_ftp)+" "+getContext().getString(R.string.total_download);//"FTP DL";
		cv.drawText(paraname, colsWidth * 0 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 12- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_http);//"Http";
		cv.drawText(paraname, colsWidth * 0 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 13- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_pop3);//"Pop3";
		cv.drawText(paraname, colsWidth * 0 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 14- rowUpBit, paint);
		paraname = getContext().getString(R.string.total_wap)+" "+getContext().getString(R.string.total_download);//"Wap DL";
		cv.drawText(paraname, colsWidth * 0 + (colsWidth - paint.measureText(paraname)) / 2, rowsHeight * 15- rowUpBit, paint);
		
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
	protected Bitmap CreateTableData(Bitmap bm,TotalTaskModel data){
		int width = bm.getWidth();
		int height = bm.getHeight();
		float tablewidth = width ;
		float tableheight = height;
		int tableRows = 17;	//行数
		int tableCols = 5;	//列数
		float rowsHeight = tableheight/tableRows;	//行高
		float colsWidth = tablewidth/tableCols;		//列宽
		float rowUpBit = (rowsHeight - textSize)/2 ;		//指定行上升位数,为行高-字体高度 再除2
		
		Canvas cv = new Canvas(bm);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);	
		paint.setColor(getResources().getColor(R.color.info_param_color));
		paint.setTypeface(null);
		paint.setTextSize(textSize * rate);
		String value;
		
		//拨打数据
		value = ""+data.getDialTimes();
		cv.drawText(value, colsWidth * 1 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 2- rowUpBit, paint);
		value = ""+data.getDialSuccTimes()+"/"+UtilsMethod.decFormat.format(data.getDialSuccTimes()*1.0f/(data.getDialTimes()==0?1:data.getDialTimes()) * 100)+"%";
		cv.drawText(value, colsWidth * 2 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 2- rowUpBit, paint);
		value = ""+data.getDialDropTimes()+"/"+UtilsMethod.decFormat.format(data.getDialDropTimes()*1.0f/(data.getDialTimes()==0?1:data.getDialTimes()) * 100)+"%";
		cv.drawText(value, colsWidth * 3 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 2- rowUpBit, paint);
		value = ""+data.getDialDelay();
		cv.drawText(value, colsWidth * 4 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 2- rowUpBit, paint);
		//视频拨打数据
		value = ""+data.getVideoDTimes();
		cv.drawText(value, colsWidth * 1 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 3- rowUpBit, paint);
		value = ""+data.getVideoDSuccTimes()+"/"+UtilsMethod.decFormat.format(data.getVideoDSuccTimes()*1.0f/(data.getVideoDTimes()==0?1:data.getVideoDTimes()) * 100)+"%";
		cv.drawText(value, colsWidth * 2 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 3- rowUpBit, paint);
		value = ""+data.getVideoDDropTimes()+"/"+UtilsMethod.decFormat.format(data.getVideoDDropTimes()*1.0f/(data.getVideoDTimes()==0?1:data.getVideoDTimes()) * 100)+"%";
		cv.drawText(value, colsWidth * 3 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 3- rowUpBit, paint);
		value = ""+data.getVideoDDelay();
		cv.drawText(value, colsWidth * 4 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 3- rowUpBit, paint);
		//Attach
		value = ""+data.getAttachTimes();
		cv.drawText(value, colsWidth * 1 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 5- rowUpBit, paint);
		value = ""+data.getAttachSuccTimes();
		cv.drawText(value, colsWidth * 2 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 5- rowUpBit, paint);
		value = ""+UtilsMethod.decFormat.format(data.getAttachSuccTimes()*1.0f/(data.getAttachTimes()==0?1:data.getAttachTimes()) * 100)+"%";
		cv.drawText(value, colsWidth * 3 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 5- rowUpBit, paint);
		value = ""+data.getAttachDelay();
		cv.drawText(value, colsWidth * 4 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 5- rowUpBit, paint);
		//PDP
		value = ""+data.getPdpTimes();
		cv.drawText(value, colsWidth * 1 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 6- rowUpBit, paint);
		value = ""+data.getPdpSuccTimes();
		cv.drawText(value, colsWidth * 2 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 6- rowUpBit, paint);
		value = ""+UtilsMethod.decFormat.format(data.getPdpSuccTimes()*1.0f/(data.getPdpTimes()==0?1:data.getPdpTimes()) * 100)+"%";
		cv.drawText(value, colsWidth * 3 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 6- rowUpBit, paint);
		value = ""+data.getPdpDelay();
		cv.drawText(value, colsWidth * 4 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 6- rowUpBit, paint);
		//Ping
		value = ""+data.getPingTimes();
		cv.drawText(value, colsWidth * 1 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 7- rowUpBit, paint);
		value = ""+data.getPingSuccTimes();
		cv.drawText(value, colsWidth * 2 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 7- rowUpBit, paint);
		value = ""+UtilsMethod.decFormat.format(data.getPingSuccTimes()*1.0f/(data.getPingTimes()==0?1:data.getPingTimes()) * 100)+"%";
		cv.drawText(value, colsWidth * 3 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 7- rowUpBit, paint);
		value = ""+data.getPingDelay();
		cv.drawText(value, colsWidth * 4 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 7- rowUpBit, paint);
		//WapRef
		value = ""+data.getWapRefTimes();
		cv.drawText(value, colsWidth * 1 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 8- rowUpBit, paint);
		value = ""+data.getWapRefSuccTimes();
		cv.drawText(value, colsWidth * 2 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 8- rowUpBit, paint);
		value = ""+UtilsMethod.decFormat.format(data.getWapRefSuccTimes()*1.0f/(data.getWapRefTimes()==0?1:data.getWapRefTimes()) * 100)+"%";
		cv.drawText(value, colsWidth * 3 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 8- rowUpBit, paint);
		value = ""+data.getWapRefDelay();
		cv.drawText(value, colsWidth * 4 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 8- rowUpBit, paint);
		//WapLogin
		value = ""+data.getWapLoginTimes();
		cv.drawText(value, colsWidth * 1 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 9- rowUpBit, paint);
		value = ""+data.getWapLoginSuccTimes();
		cv.drawText(value, colsWidth * 2 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 9- rowUpBit, paint);
		value = ""+UtilsMethod.decFormat.format(data.getWapLoginSuccTimes()*1.0f/(data.getWapLoginTimes()==0?1:data.getWapLoginTimes()) * 100)+"%";
		cv.drawText(value, colsWidth * 3 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 9- rowUpBit, paint);
		value = ""+data.getWapLoginDelay();
		cv.drawText(value, colsWidth * 4 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 9- rowUpBit, paint);
		//FtpUL
		value = ""+data.getFtpULTimes();
		cv.drawText(value, colsWidth * 1 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 11- rowUpBit, paint);
		value = ""+data.getFtpULSuccTimes();
		cv.drawText(value, colsWidth * 2 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 11- rowUpBit, paint);
		value = ""+UtilsMethod.decFormat.format((data.getFtpULTimes() - data.getFtpULSuccTimes())*1.0f/(data.getFtpULTimes()==0?1:data.getFtpULTimes()) * 100)+"%";
		cv.drawText(value, colsWidth * 3 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 11- rowUpBit, paint);
		value = ""+UtilsMethod.decFormat.format(data.getFtpULRate()/UtilsMethod.kbyteRage * 8)+"kbps";
		cv.drawText(value, colsWidth * 4 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 11- rowUpBit, paint);
		//FtpDL
		value = ""+data.getFtpDLTimes();
		cv.drawText(value, colsWidth * 1 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 12- rowUpBit, paint);
		value = ""+data.getFtpDLSuccTimes();
		cv.drawText(value, colsWidth * 2 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 12- rowUpBit, paint);
		value = ""+UtilsMethod.decFormat.format((data.getFtpDLTimes() - data.getFtpDLSuccTimes())*1.0f/(data.getFtpDLTimes()==0?1:data.getFtpDLTimes()) * 100)+"%";
		cv.drawText(value, colsWidth * 3 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 12- rowUpBit, paint);
		value = ""+UtilsMethod.decFormat.format(data.getFtpDLRate()/UtilsMethod.kbyteRage * 8)+"kbps";
		cv.drawText(value, colsWidth * 4 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 12- rowUpBit, paint);
		//Http
		value = ""+data.getHttpTimes();
		cv.drawText(value, colsWidth * 1 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 13- rowUpBit, paint);
		value = ""+data.getHttpSuccTimes();
		cv.drawText(value, colsWidth * 2 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 13- rowUpBit, paint);
		value = ""+UtilsMethod.decFormat.format((data.getHttpTimes() - data.getHttpSuccTimes())*1.0f/(data.getHttpTimes()==0?1:data.getHttpTimes()) * 100)+"%";
		cv.drawText(value, colsWidth * 3 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 13- rowUpBit, paint);
		value = ""+UtilsMethod.decFormat.format(data.getHttpRate()/UtilsMethod.kbyteRage * 8)+"kbps";
		cv.drawText(value, colsWidth * 4 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 13- rowUpBit, paint);
		//Pop3
		value = ""+data.getPop3Times();
		cv.drawText(value, colsWidth * 1 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 14- rowUpBit, paint);
		value = ""+data.getPop3SuccTimes();
		cv.drawText(value, colsWidth * 2 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 14- rowUpBit, paint);
		value = ""+UtilsMethod.decFormat.format((data.getPop3Times() - data.getPop3SuccTimes())*1.0f/(data.getPop3Times()==0?1:data.getPop3Times()) * 100)+"%";
		cv.drawText(value, colsWidth * 3 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 14- rowUpBit, paint);
		value = ""+UtilsMethod.decFormat.format(data.getPop3Rate()/UtilsMethod.kbyteRage * 8)+"kbps";
		cv.drawText(value, colsWidth * 4 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 14- rowUpBit, paint);
		//WapDL
		value = ""+data.getWapDLTimes();
		cv.drawText(value, colsWidth * 1 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 15- rowUpBit, paint);
		value = ""+data.getWapDLSuccTimes();
		cv.drawText(value, colsWidth * 2 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 15- rowUpBit, paint);
		value = ""+UtilsMethod.decFormat.format((data.getWapDLTimes() - data.getWapDLSuccTimes())*1.0f/(data.getWapDLTimes()==0?1:data.getWapDLTimes()) * 100)+"%";
		cv.drawText(value, colsWidth * 3 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 15- rowUpBit, paint);
		value = ""+UtilsMethod.decFormat.format(data.getWapDLRate()/UtilsMethod.kbyteRage * 8)+"kbps";
		cv.drawText(value, colsWidth * 4 + (colsWidth - paint.measureText(value)) / 2, rowsHeight * 15- rowUpBit, paint);
		
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
			LogUtil.w(tag,"---TotalTask---");
			//mbmp = CreateTableData(CreateTable(CreateBlankBitmap()),TotalDataDispose.getInstance().getTotalTaskModel());
			invalidate();
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
		//mbmp = CreateTableData(CreateTable(CreateBlankBitmap()),TotalDataDispose.getInstance().getTotalTaskModel());
	}
}
