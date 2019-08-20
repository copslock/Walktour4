/**
 * 
 */
package com.walktour.gui.total;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.walktour.model.XYModel;

import java.text.DecimalFormat;
/**
 * @author shining.wu
 *
 */
public class TotalDailStatisticsView extends View{
    
    /**
     * [构造简要说明]
     * @param context
     */
    public TotalDailStatisticsView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    private boolean initOk = false;
    
    float systemScale ;
    
    float scaleX=0;
    float scaleY=0;
    
    private int width;
    private int height;
    
    private Paint pgfont;
    private Paint pwfont;
    private Paint plinefont;
    private Paint pdailfont;
    
    Matrix scalzoom;
    
    private  final float originalWidth = 480.0f;
    //private  final float originalHeight= 624.0f;
    DecimalFormat format = new DecimalFormat("0.00");

    Context context=null;
    public TotalDailStatisticsView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.context=context;
        pgfont = new Paint();
        pgfont.setTextSize(18);
        pgfont.setFilterBitmap(true);
        pgfont.setAntiAlias(true);
        pgfont.setColor(Color.GREEN);
        
        pdailfont = new Paint();
        pdailfont.setTextSize(20);
        pdailfont.setFilterBitmap(true);
        pdailfont.setAntiAlias(true);
        pdailfont.setColor(Color.WHITE);
        
        pwfont = new Paint();
        pwfont.setTextSize(18);
        pwfont.setFilterBitmap(true);
        pwfont.setAntiAlias(true);
        pwfont.setColor(Color.WHITE);
        
        plinefont = new Paint();
        plinefont.setColor(0x66666600);
        
        DisplayMetrics metric = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metric);
        systemScale = metric.densityDpi/240.f;
    }
    
    /*  @Override
    protected void onDraw(Canvas g) {
       super.onDraw(g);
       if(!initOk){
          init();
          initOk = true;
       }
       
       int drawwidth=getWidth();
       int startY=74*height/800;
       int tableguh=48*height/800;
       //X方向
       for(int i=0;i<8;i++){
           g.drawLine(10, startY+tableguh*i, drawwidth-10, startY+tableguh*i, pwfont);
       }
       int tableguw=(drawwidth-20)/3;
       //Y方向
       for(int m=0;m<4;m++){
           g.drawLine(10+tableguw*m, startY, 10+tableguw*m, startY+tableguh*7, pwfont);
       }
       
       filltabledata(g,10,startY,tableguh,tableguw);
       
       freshmoblenet();
       String netmark="";
       if(SysTempData.nettype==1){
           netmark="RxLev";
       }else if(SysTempData.nettype==2){
           netmark="RSCP";
       }
       //2G是RxLev 3G是RSCP
       
       Bitmap bit_fangraph = drawPieChart(new int[] { Color.RED, Color.rgb(255,187,2),
                Color.BLUE,Color.GREEN}, new long[] { SignalAssort.count95,SignalAssort.count85,SignalAssort.count75,
               SignalAssort.count65}, new String[] { "<=-95", "<=-85", "<=-75", "<=-65"
                },netmark+" ( dBm )");
//     Bitmap bit_fangraph = drawPieChart(new int[] { Color.RED, Color.rgb(255,187,2),
//              Color.BLUE,Color.GREEN}, new long[] { 0,0,17,82}, new String[] { "<=-95", "<=-85", "<=-75", "<=-65"
//              },netmark+" ( dBm )");
       g.drawBitmap(bit_fangraph,0,startY+tableguh*7+30,pwfont);
       
    }
    
    private void freshmoblenet(){
        TelephonyManager tManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        int nettype=tManager.getNetworkType();
        if(nettype==TelephonyManager.NETWORK_TYPE_CDMA||nettype==TelephonyManager.NETWORK_TYPE_1xRTT||nettype==TelephonyManager.NETWORK_TYPE_EDGE||nettype==TelephonyManager.NETWORK_TYPE_GPRS){
            SysTempData.nettype=1;
            if(SysTempData.mnc.equals("0")||SysTempData.mnc.equals("1")){
                SysTempData.nettypestr="GSM";
            }else if(SysTempData.mnc.equals("2")){
                SysTempData.nettypestr="CDMA";
            }                                                
        }else if(nettype==TelephonyManager.NETWORK_TYPE_EVDO_0||nettype==TelephonyManager.NETWORK_TYPE_HSDPA||nettype==TelephonyManager.NETWORK_TYPE_HSPA||nettype==TelephonyManager.NETWORK_TYPE_HSUPA||nettype==TelephonyManager.NETWORK_TYPE_UMTS){
            if(SysTempData.mnc.equals("0")){
                SysTempData.nettypestr="TD-SCDMA";
            }else if(SysTempData.mnc.equals("1")){
                SysTempData.nettypestr="WCDMA";
            }else if(SysTempData.mnc.equals("2")){
                SysTempData.nettypestr="EVDO";
            }  
            SysTempData.nettype=2;
        }
    }
    
    private void filltabledata(Canvas g,int tstartx,int tstarty,int tableh,int tablew){
        FontMetrics fm = pdailfont.getFontMetrics();// 得到系统默认字体属性  
        int mFontH = (int) (Math.ceil(fm.descent - fm.top) + 2);// 获得字体高 
        //table头
        String dialstr="Dial";
        float dialw=pdailfont.measureText(dialstr+"");
        g.drawText(dialstr, tstartx+tablew/2-dialw/2, tstarty+tableh/2+mFontH/4, pdailfont);
        
        String mydial="主叫";
        float mydialw=pdailfont.measureText(mydial+"");
        g.drawText(mydial, tstartx+tablew+tablew/2-mydialw/2, tstarty+tableh/2+mFontH/4, pdailfont);
        
        String callme="被叫";
        float callmew=pdailfont.measureText(callme+"");
        g.drawText(callme, tstartx+2*tablew+tablew/2-callmew/2, tstarty+tableh/2+mFontH/4, pdailfont);
        
        //table 左边
        String trydialstr="尝试次数";
        float trydialstrw=pdailfont.measureText(trydialstr+"");
        g.drawText(trydialstr, tstartx+tablew/2-trydialstrw/2, tstarty+tableh+tableh/2+mFontH/4, pdailfont);
        
        if(SysTempData.trydialcount!=0){
            float trydialcountw=pdailfont.measureText(SysTempData.trydialcount+"");
            g.drawText(SysTempData.trydialcount+"", tstartx+tablew+tablew/2-trydialcountw/2, tstarty+tableh+tableh/2+mFontH/4, pdailfont);
        }
        String revsucessstr="接通次数";
        float revsucessstrw=pdailfont.measureText(revsucessstr+"");
        g.drawText(revsucessstr, tstartx+tablew/2-revsucessstrw/2, tstarty+2*tableh+tableh/2+mFontH/4, pdailfont);
        
        if(SysTempData.trydialcount!=0){
            float revcountw=pdailfont.measureText(SysTempData.setupcall+"");
            g.drawText(SysTempData.setupcall+"", tstartx+tablew+tablew/2-revcountw/2, tstarty+2*tableh+tableh/2+mFontH/4, pdailfont);
        }
        
        String dropcallstr="掉话次数";
        float dropcallstrw=pdailfont.measureText(dropcallstr+"");
        g.drawText(dropcallstr, tstartx+tablew/2-dropcallstrw/2, tstarty+3*tableh+tableh/2+mFontH/4, pdailfont);
        
        if(SysTempData.trydialcount!=0){
            float dropcountw=pdailfont.measureText(SysTempData.dropcall+"");
            g.drawText(SysTempData.dropcall+"", tstartx+tablew+tablew/2-dropcountw/2, tstarty+3*tableh+tableh/2+mFontH/4, pdailfont);
        }
        
        String revratstr="接通率";
        float revratstrw=pdailfont.measureText(revratstr+"");
        g.drawText(revratstr, tstartx+tablew/2-revratstrw/2, tstarty+4*tableh+tableh/2+mFontH/4, pdailfont);
        
        if(SysTempData.trydialcount!=0){
            int seteprate=(int)(((float)SysTempData.setupcall/(float)SysTempData.trydialcount)*100);
            float setepratew=pdailfont.measureText(seteprate+"%");
            g.drawText(seteprate+"%", tstartx+tablew+tablew/2-setepratew/2, tstarty+4*tableh+tableh/2+mFontH/4, pdailfont);
        }
        
        String dropratstr="掉话率";
        float dropratstrw=pdailfont.measureText(dropratstr+"");
        g.drawText(dropratstr, tstartx+tablew/2-dropratstrw/2, tstarty+5*tableh+tableh/2+mFontH/4, pdailfont);
        
        if(SysTempData.trydialcount!=0){
            int dropcallrate=(int)(((float)SysTempData.dropcall/(float)SysTempData.trydialcount)*100);
            float dropcallratew=pdailfont.measureText(dropcallrate+"%");
            g.drawText(dropcallrate+"%", tstartx+tablew+tablew/2-dropcallratew/2, tstarty+5*tableh+tableh/2+mFontH/4, pdailfont);
        }
        
        String avgtimestr="平均时延";
        float avgtimestrw=pdailfont.measureText(avgtimestr+"");
        g.drawText(avgtimestr, tstartx+tablew/2-avgtimestrw/2, tstarty+6*tableh+tableh/2+mFontH/4, pdailfont);
        if(SysTempData.delaycount!=0){
            double avgdelaytime=(float)SysTempData.delaytimeSum/(float)SysTempData.delaycount/(float)1000;
            String avgdelaytime1=format.format(avgdelaytime);
            float testavgtimestr=pdailfont.measureText(avgdelaytime1+"s");
            g.drawText(avgdelaytime1+"s", tstartx+tablew+tablew/2-testavgtimestr/2, tstarty+6*tableh+tableh/2+mFontH/4, pdailfont);
        }
    }   
    
    private void init(){
        width = SysTempData.screenWidth;
        height = SysTempData.screenHeight;
        scaleX = width/originalWidth;
        scaleX /= systemScale;
        
        scaleY = height/800.0f;
        scaleY /= systemScale;
        
        scalzoom = new Matrix();//按长宽比例缩放
        scalzoom.postScale(scaleX,scaleY);  
    }
    
    *//**
     * 
     * @param colors
     *            颜色数组
     * @param radians
     *            点数数量
     * @param strs
     * @return 扇形图
     *//*
    public Bitmap drawPieChart(int[] colors, long[] number, String[] strs,
            String param) {
        Bitmap bit_pie = Bitmap.createBitmap(480, 300, Config.ARGB_8888);
        float radians[] = new float[number.length];
        long numSum = 0;
        for (int i = 0; i < number.length; i++) {
            numSum += number[i];
        }
        for (int i = 0; i < number.length; i++) {
            radians[i] = (number[i] * 360) / numSum;
        }
        Canvas c = new Canvas(bit_pie);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(20);
        paint.setShadowLayer(3f, 2, 2f, Color.BLACK);
        RectF oval = new RectF(50, 50, 250, 250);
        float start_radian = 0;
        for (int i = 0; i <radians.length; i++) {
            
            paint.setColor(colors[i]);
            c.drawArc(oval, start_radian, radians[i], true, paint);
            c.drawRect(300, 90 + i * 30, 340, 110 + i * 30, paint);
            c.drawText(strs[i], 360, 107 + i * 30, pdailfont);
            if(radians[i]==0){
                continue;
            }
            
            //double x = (start_radian+radians[i]/2)*Math.PI / 180;
            int percentv=(int)(100*((float)number[i]/(float)numSum));
            if(percentv!=0){
                float showwidth=pdailfont.measureText(percentv+"%");
                FontMetrics fm = pdailfont.getFontMetrics();// 得到系统默认字体属性  
                int mFontH = (int) (Math.ceil(fm.descent - fm.top) + 2);// 获得字体高 
                XYModel xymode=getXY(start_radian,start_radian+radians[i],100);
                double xx=(double)150+xymode.getX();
                double yy=(double)150-xymode.getY();
                c.drawText(percentv+"%",(int)(xx-(double)showwidth/2),(int)(yy+mFontH/4), pdailfont);
                //c.drawLine(150, 150, (int)xx,(int)yy,pdailfont);
                //c.drawText(percentv+"%",(float)(150+50*Math.cos(x))-showwidth/2, (float)(150+50*(Math.sin(x))), pdailfont);
            }
            start_radian += radians[i];
        }
        pwfont.setTextSize(24);
        c.drawText(param, 290,67,pdailfont);
        pwfont.setTextSize(18);
        c.drawText("Mean", 290,117 + (colors.length) * 30,pdailfont);
        //float strwidth=pdailfont.measureText("Mean");
        float meanvalue=0;
        String meanv="";
        if(SignalAssort.signalcount!=0){
           meanvalue=(float)SignalAssort.signalSum/(float)SignalAssort.signalcount;
           DecimalFormat format = new DecimalFormat("0.00");
           meanv=format.format(meanvalue);
        }
        c.drawText(meanv+"", 360,117 + (colors.length) * 30,pdailfont);
        return bit_pie;
    }
    
    //startdu开始角度，enddu结束角度，radian半径。
    private XYModel getXY(float startdu,float enddu,int radian){
        double sdu = (startdu)*Math.PI / 180;
        double edu = (enddu)*Math.PI / 180;
        
        double x1=0;
        double y1=0;
        double x2=0;
        double y2=0;
        
        if((0<=startdu&&startdu<=90)){//第一
            x1=(double)radian*Math.cos(sdu);
            y1=0-(double)radian*Math.sin(sdu);
        }
        
        if((90<startdu&&startdu<=180)){//第二
            x1=(double)radian*Math.cos(sdu);
            y1=0-(double)radian*Math.sin(sdu);
        }
        
        
        if((180<startdu&&startdu<=270)){//第三
            x1=(double)radian*Math.cos(sdu);
            y1=0-(double)radian*Math.sin(sdu);
        }
        
        if((270<startdu&&startdu<=360)){//第4
            x1=(double)radian*Math.cos(sdu);
            y1=0-(double)radian*Math.sin(sdu);
        }
        ///////////////////////////
        if((0<=enddu&&enddu<=90)){//第一
            x2=(double)radian*Math.cos(edu);
            y2=0-(double)radian*Math.sin(edu);
        }
        
        if((90<enddu&&enddu<=180)){//第二
            x2=(double)radian*Math.cos(edu);
            y2=0-(double)radian*Math.sin(edu);
        }
        
        
        if((180<enddu&&enddu<=270)){//第三
            x2=(double)radian*Math.cos(edu);
            y2=0-(double)radian*Math.sin(edu);
        }
        
        if((270<enddu&&enddu<=360)){//第三
            x2=(double)radian*Math.cos(edu);
            y2=0-(double)radian*Math.sin(edu);
        }
        
        double xx=(x1+x2)/2;
        double yy=(y1+y2)/2;
        if(enddu-startdu>180){
            xx=0-xx;
            yy=0-yy;
        }
        
        int sqrsum=(int)(xx*xx)+(int)(yy*yy);//求出半径一定大于圆半径一半以上。
        float factor=0.1f;
        while(sqrsum<((double)radian*(double)radian/3)){
            xx=xx*(1+factor);
            yy=yy*(1+factor);
            factor=factor+0.1f;
            sqrsum=(int)(xx*xx)+(int)(yy*yy);
        }
        
        factor=0.01f;
        while(sqrsum>((double)radian*(double)radian/3.01)){
            xx=xx*(1-factor);
            yy=yy*(1-factor);
            factor=factor+0.01f;
            sqrsum=(int)(xx*xx)+(int)(yy*yy);
        }
        
        XYModel xymodel=new XYModel();
        xymodel.setX(xx);
        xymodel.setY(yy);
        return xymodel;
    }*/
    
    
    
   /**
    * 
    * @param colors
    *            颜色数组
    * @param radians
    *            点数数量
    * @param strs
    * @return 扇形图
    */
   public Bitmap drawPieChart(int[] colors, long[] number, String[] strs,
           String param) {
       Bitmap bit_pie = Bitmap.createBitmap(480, 300, Config.ARGB_8888);
       float radians[] = new float[number.length];
       long numSum = 0;
       for (int i = 0; i < number.length; i++) {
           numSum += number[i];
       }
       for (int i = 0; i < number.length; i++) {
           radians[i] = (number[i] * 360) / numSum;
       }
       Canvas c = new Canvas(bit_pie);
       Paint paint = new Paint();
       paint.setAntiAlias(true);
       paint.setTextSize(20);
       paint.setShadowLayer(3f, 2, 2f, Color.BLACK);
       RectF oval = new RectF(50, 50, 250, 250);
       float start_radian = 0;
       for (int i = 0; i <radians.length; i++) {
           
           paint.setColor(colors[i]);
           c.drawArc(oval, start_radian, radians[i], true, paint);
           c.drawRect(300, 90 + i * 30, 340, 110 + i * 30, paint);
           c.drawText(strs[i], 360, 107 + i * 30, pdailfont);
           if(radians[i]==0){
               continue;
           }
           
           //double x = (start_radian+radians[i]/2)*Math.PI / 180;
           int percentv=(int)(100*((float)number[i]/(float)numSum));
           if(percentv!=0){
               float showwidth=pdailfont.measureText(percentv+"%");
               FontMetrics fm = pdailfont.getFontMetrics();// 得到系统默认字体属性  
               int mFontH = (int) (Math.ceil(fm.descent - fm.top) + 2);// 获得字体高 
               XYModel xymode=getXY(start_radian,start_radian+radians[i],100);
               double xx=(double)150+xymode.getX();
               double yy=(double)150-xymode.getY();
               c.drawText(percentv+"%",(int)(xx-(double)showwidth/2),(int)(yy+mFontH/4), pdailfont);
               //c.drawLine(150, 150, (int)xx,(int)yy,pdailfont);
               //c.drawText(percentv+"%",(float)(150+50*Math.cos(x))-showwidth/2, (float)(150+50*(Math.sin(x))), pdailfont);
           }
           start_radian += radians[i];
       }
       pwfont.setTextSize(24);
       c.drawText(param, 290,67,pdailfont);
       pwfont.setTextSize(18);
       c.drawText("Mean", 290,117 + (colors.length) * 30,pdailfont);
       //float strwidth=pdailfont.measureText("Mean");
       float meanvalue=0;
       String meanv="";
 /*      if(SignalAssort.signalcount!=0){
          meanvalue=(float)SignalAssort.signalSum/(float)SignalAssort.signalcount;
          DecimalFormat format = new DecimalFormat("0.00");
          meanv=format.format(meanvalue);
       }*/
       c.drawText(meanv+"", 360,117 + (colors.length) * 30,pdailfont);
       return bit_pie;
   }
   
   /**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @param canvas
 * @see android.view.View#onDraw(android.graphics.Canvas)
 */

@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    
  Bitmap bit_fangraph = drawPieChart(new int[] { Color.RED, Color.rgb(255,187,2),
  Color.BLUE,Color.GREEN}, new long[] { 0,0,17,82}, new String[] { "<=-95", "<=-85", "<=-75", "<=-65"
  },1+" ( dBm )");
  canvas.drawBitmap(bit_fangraph, 50, 200, pwfont);
}

//startdu开始角度，enddu结束角度，radian半径。
   private XYModel getXY(float startdu,float enddu,int radian){
       double sdu = (startdu)*Math.PI / 180;
       double edu = (enddu)*Math.PI / 180;
       
       double x1=0;
       double y1=0;
       double x2=0;
       double y2=0;
       
       if((0<=startdu&&startdu<=90)){//第一
           x1=(double)radian*Math.cos(sdu);
           y1=0-(double)radian*Math.sin(sdu);
       }
       
       if((90<startdu&&startdu<=180)){//第二
           x1=(double)radian*Math.cos(sdu);
           y1=0-(double)radian*Math.sin(sdu);
       }
       
       
       if((180<startdu&&startdu<=270)){//第三
           x1=(double)radian*Math.cos(sdu);
           y1=0-(double)radian*Math.sin(sdu);
       }
       
       if((270<startdu&&startdu<=360)){//第4
           x1=(double)radian*Math.cos(sdu);
           y1=0-(double)radian*Math.sin(sdu);
       }
       ///////////////////////////
       if((0<=enddu&&enddu<=90)){//第一
           x2=(double)radian*Math.cos(edu);
           y2=0-(double)radian*Math.sin(edu);
       }
       
       if((90<enddu&&enddu<=180)){//第二
           x2=(double)radian*Math.cos(edu);
           y2=0-(double)radian*Math.sin(edu);
       }
       
       
       if((180<enddu&&enddu<=270)){//第三
           x2=(double)radian*Math.cos(edu);
           y2=0-(double)radian*Math.sin(edu);
       }
       
       if((270<enddu&&enddu<=360)){//第三
           x2=(double)radian*Math.cos(edu);
           y2=0-(double)radian*Math.sin(edu);
       }
       
       double xx=(x1+x2)/2;
       double yy=(y1+y2)/2;
       if(enddu-startdu>180){
           xx=0-xx;
           yy=0-yy;
       }
       
       int sqrsum=(int)(xx*xx)+(int)(yy*yy);//求出半径一定大于圆半径一半以上。
       float factor=0.1f;
       while(sqrsum<((double)radian*(double)radian/3)){
           xx=xx*(1+factor);
           yy=yy*(1+factor);
           factor=factor+0.1f;
           sqrsum=(int)(xx*xx)+(int)(yy*yy);
       }
       
       factor=0.01f;
       while(sqrsum>((double)radian*(double)radian/3.01)){
           xx=xx*(1-factor);
           yy=yy*(1-factor);
           factor=factor+0.01f;
           sqrsum=(int)(xx*xx)+(int)(yy*yy);
       }
       
       XYModel xymodel=new XYModel();
       xymodel.setX(xx);
       xymodel.setY(yy);
       return xymodel;
   }
}
