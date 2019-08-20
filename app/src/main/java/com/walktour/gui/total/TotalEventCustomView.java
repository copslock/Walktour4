package com.walktour.gui.total;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.BasicTotalView;
import com.walktour.gui.R;
import com.walktour.gui.setting.customevent.CustomEventFactory;
import com.walktour.model.TotalCustomModel;
import com.walktour.model.TotalCustomModel.OneEvent;

import java.util.ArrayList;

public class TotalEventCustomView extends BasicTotalView {
    
    private static String tag = "TotalEventCustomView";
    
    private boolean isRegisterReceiver = false;
    
    /**
     * View宽度
     */
    private int width;
    
    private int MARGIN = 1;
    
    int colWidth ;
    float left;
    float top ;
    float right ;
    float bottom ;
    
    int tableRows = 1; //行数
    
    int tableCols = 8; //列数
    
    DisplayMetrics metric;
    
    CustomEventFactory factory;
    
    private String eventName = "";
    
    public TotalEventCustomView(Context context) {
        super(context);
        metric = new DisplayMetrics();
        ((Activity) this.getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(metric);
        factory = CustomEventFactory.getInstance();
    }
    
    public void setName(String eventName){
    	this.eventName = eventName;
    }
    
    public TotalEventCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        metric = new DisplayMetrics();
        ((Activity) this.getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(metric);
        factory = CustomEventFactory.getInstance();
    }
    
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        TotalCustomModel totalModel = this.factory.getCustomTotalByName(eventName);
        tableRows = totalModel.getEventList().size();
        
        width = this.getWidth()-2*MARGIN;
        colWidth = width/tableCols;
        left = MARGIN;
        top = MARGIN;
        right = width - MARGIN;
        bottom = (tableRows + 1) * rowHeight-MARGIN;
       
        createTable(canvas,tableRows);
        createTableData(canvas,totalModel);
       
    }
    
    /**
     * 绘制表格<BR>
     * [功能详细描述]
     * @param canvas
     * @param rows
     */
    private void createTable(Canvas canvas,int tableRows) {
        //绘制 两边
        canvas.drawLine(left, top, left, bottom, linePaint);
        canvas.drawLine(right, top, right, bottom, linePaint);
        //画横线
        for (int i = 0; i <= tableRows+1; i++) {
        	float y = top+rowHeight*i;
            canvas.drawLine(left,y,right,y, linePaint);
        }
        //画竖线
        canvas.drawLine(left+ 1*colWidth,top,left+ 1*colWidth,bottom,linePaint);
        canvas.drawLine(left+ 3*colWidth,top,left+ 3*colWidth,bottom,linePaint);
        canvas.drawLine(left+ 5*colWidth,top,left+ 5*colWidth,bottom,linePaint);
        
        //画表头
        String index = getResources().getString(R.string.str_index);
        String time = getResources().getString(R.string.str_time);
        String delay = getResources().getString(R.string.str_delay)+"(ms)";
        String lat = getResources().getString(R.string.str_latitude);
        
        drawRowText(canvas,0,new String[]{index,time,delay,lat});
    }
    
    /**
     * 画一行的数据
     * @param index 行号
     * @param data 长为4的数组
     */
    private void drawRowText(Canvas canvas,int index,String[] data){
    	 //文字上下位移
        final int OffsetVer = (int) ((rowHeight - fontPaint.getTextSize() ) / 2); 
        
        float y = (index+1)*rowHeight - OffsetVer;
        
    	canvas.drawText(data[0],
        		0*colWidth  + (1*colWidth - fontPaint.measureText(data[0]) ) / 2,
        		y,
                fontPaint);
        
        canvas.drawText(data[1],
        		1*colWidth  + (2*colWidth - fontPaint.measureText(data[1]) ) / 2,
        		y,
                fontPaint);
        
        canvas.drawText(data[2],
        		3*colWidth  + (2*colWidth - fontPaint.measureText(data[2]) ) / 2,
        		y,
                fontPaint);
        
        canvas.drawText(data[3],
        		5*colWidth  + (3*colWidth - fontPaint.measureText(data[3]) ) / 2,
        		y,
                fontPaint);
    }
    
    private void createTableData(Canvas canvas,TotalCustomModel totalModel) {
    	ArrayList<OneEvent> eventList = totalModel.getEventList();
    	for( int i=0;i<eventList.size();i++ ){
    		OneEvent one = eventList.get(i);
    		int index = i+1;
    		String [] data = new String[]{
    				String.valueOf( index ),
    				UtilsMethod.getSimpleDateFormat1( one.time ),
    				String.valueOf( one.delay>0 ? one.delay:"/" ),
    				String.format("%.6f/%.6f", one.logitude/1000000f,one.latitude/1000000f)
    		};
    		drawRowText(canvas,index,data);
    	}
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LogUtil.w(tag, "----onAttachedToWindow----");
        IntentFilter filter = new IntentFilter(); //注册一个消息过滤器
        filter.addAction(TotalDataByGSM.TotalTaskDataChanged);
        filter.addAction(TotalDataByGSM.TotalParaDataChanged);
        filter.addAction(TotalDataByGSM.TotalResultToPicture);
        getContext().registerReceiver(mIntentReceiver, filter, null, null);
        isRegisterReceiver = true;
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtil.w(tag, "----onDetachedFromWindow----");
        try {
            if (isRegisterReceiver) {
                getContext().unregisterReceiver(mIntentReceiver); //反注册消息过滤器
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
                String path = intent.getStringExtra(TotalDataByGSM.TotalSaveFilePath)
                        + "-Http.jpg";
                LogUtil.w(tag, "--save current to file---" + path);
                TotalEventCustomView.this.buildDrawingCache();
                UtilsMethod.SaveBitmapToFile(TotalEventCustomView.this.getDrawingCache(),
                        path);
            } else {
                /*				    TotalHttpView.this.onMeasure(TotalHttpView.this.getWidth(), 0);
                				    TotalHttpView.this.setBackgroundColor(getResources().getColor(R.color.param_view_line_color));*/
                //TotalHttpView.this.onTouchEvent(null);
                //TotalHttpView.this.invalidate();
            	
            	
            }
        }
    };
    
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int rows = factory.getCustomTotalByName(eventName).getCount();
        int height = (int) ((rows+1) * rowHeight + 5);
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
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param event
     * @return
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     */
    
/*        @Override
        public boolean onTouchEvent(MotionEvent event) {
            HashMap<String, Object> main2 = new HashMap<String, Object>();
            HashMap<String, Long> main3 = new HashMap<String, Long>();
            main3.put(TotalStruct.TotalAppreciation._HttpTry.name(), 1L);
            main3.put(TotalStruct.TotalAppreciation._HttpSuccess.name(), 2L);
            main3.put(TotalStruct.TotalAppreciation._HttpDelay.name(), 3L);
            main2.put("google.com", main3);
            main2.put("baidu.com", main3);
            main2.put("sina.com.com", main3);
            main2.put("sAAA.com.com", main3);
            main2.put("tt.com.com", main3);
            main2.put("aa.com.com", main3);
            specialTimesHM.put(TotalStruct.TotalHttpType.HTTPLogon.getHttpType(), main2);
            //onMeasure(this.getWidth(), 0);
            specialTimesHM = TotalDataByGSM.getInstance().getSpecialTimes();
            this.setBackgroundColor(getResources().getColor(R.color.param_view_line_color));
            invalidate();
            return true;
        }*/
    
}
