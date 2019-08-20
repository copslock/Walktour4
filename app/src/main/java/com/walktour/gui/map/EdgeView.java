package com.walktour.gui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UnifyStruct;
import com.walktour.Utils.UnifyStruct.PDPInfoDataV2;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.UtilsMethodPara;
import com.walktour.framework.view.BasicParamView;
import com.walktour.gui.R;

public class EdgeView extends BasicParamView {
    
    /**
     * 当前页
     */
    private int currentPage = 1;
    
    int tableCols = 6; //列数
    
    int tableColsSec = 4; //第二页行数
    
    float strokeWidth = 1;
    
    private boolean isRegisterReceiver = false;

	private float smInfoHeight;
	
	public EdgeView(Context context) {
		super(context);
	}
	public EdgeView(Context context, AttributeSet attrs) {
		super(context, attrs);
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
		CreateTableData(canvas,TraceInfoInterface.traceData);
	}
	
	/**
	 * 创建表格
	 * @param bm 要创建表格的位图
	 * @return   输出位图
	 */
	protected void CreateTable(Canvas cv){
		float startx =1;
		float starty =0;
		float stopx =0;
		float stopy =0;
		float rowsHeight = this.getHeight()/tableRows;	//行高
		float colsWidth = this.getWidth()/tableCols;		//列宽
		float rowUpBit = (rowsHeight - textSize)/2 ;      //指定行上升位数,为行高-字体高度 再除2
		int tsRows = 8;
		float tsClosWidth = (this.getWidth() - colsWidth ) / tsRows;
		String paraname;
		if(currentPage ==1){
    		//四周边框
    		cv.drawLine(1, 1, this.getWidth()-1, 1, linePaint);
    		cv.drawLine(1, this.getHeight() - 1 , this.getWidth()-1,  this.getHeight() - 1, linePaint);
    		cv.drawLine(1, 1, 1, rowsHeight * 19, linePaint);
    		cv.drawLine(this.getWidth()-1, 1, this.getWidth()-1, rowsHeight * 19, linePaint);
    		//横线
    		for(int i=0;i<12;i++){
    			startx = 1;
    			starty =  rowsHeight * (i+1);
    			stopx = this.getWidth() - 1;
    			stopy = rowsHeight *(i+1);
    			cv.drawLine(startx, starty, stopx, stopy, linePaint);
    		}
    		
    
    		//绘制EDGE INFO 表格中间线条
            startx = colsWidth * (3);
            starty =  rowsHeight * 1;
            stopx = colsWidth * (3);
            stopy = rowsHeight * 10 ;
            cv.drawLine(startx, starty, stopx, stopy, linePaint);
            
            
            
            //TODO 版本有变，暂时添加 TS UL DL
            for(int i=0;i<tsRows;i++){
                startx = colsWidth + (tsClosWidth * i);
                starty =  rowsHeight * 10;
                stopx = colsWidth + (tsClosWidth * i);;
                stopy = rowsHeight * 12 ;
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
            }

    		//DownLink Trch Bler Meas.Info
    		paraname = getContext().getString(R.string.gprsedge_info);
    		cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
    		
    		int[] values = new int[]{R.string.gprsedge_dLTBFIdentifier,R.string.gprsedge_uLTBFIdentifier,
    		        R.string.gprsedge_dLCS,R.string.gprsedge_uLCS,
    		        R.string.gprsedge_dLRLCThr,R.string.gprsedge_uLRLCThr,
    		        R.string.gprsedge_dLRLCRtx,R.string.gprsedge_uLRLCRtx,
    		       /* R.string.gprsedge_dLLLCThr,R.string.gprsedge_uLLLCThr,
    		        R.string.gprsedge_dLLLCRtx,R.string.gprsedge_uLLLCRtx,*/
    		        R.string.gprsedge_dL_TFI,R.string.gprsedge_uL_TFI,
    		        R.string.gprsedge_gmsk_cv_bep,R.string.gprsedge_gmsk_mean_bep,
    		        R.string.gprsedge_8psk_cv_bep,R.string.gprsedge_8psk_mean_bep,
    		        R.string.gprsedge_gPRSBLER,R.string.gprsedge_cValue,
    		        R.string.gprsedge_rxQual,R.string.gprsedge_signvar};
    		
            for (int i = 0,j = 2; i < values.length; i+=2,j++) {
                paraname = getContext().getString(values[i]);
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <values.length){
                    paraname = getContext().getString(values[i+1]);
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
            }
            
            
            paraname = getContext().getString(R.string.gprsedge_tSul);
            cv.drawText(paraname, (colsWidth - fontPaint.measureText(paraname)) / 2, rowsHeight * 11 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.gprsedge_tSdl);
            cv.drawText(paraname, (colsWidth - fontPaint.measureText(paraname)) / 2, rowsHeight * 12 - rowUpBit, fontPaint);
            for(int i=0; i < tsRows; i++){
                paraname = getContext().getString(R.string.gprsedge_tS) + i;
                cv.drawText(paraname, colsWidth + tsClosWidth * i + (tsClosWidth - fontPaint.measureText(paraname)) / 2, rowsHeight * 11 - rowUpBit, fontPaint);
                paraname = getContext().getString(R.string.gprsedge_tS) + i;
                cv.drawText(paraname, colsWidth + tsClosWidth * i + (tsClosWidth - fontPaint.measureText(paraname)) / 2, rowsHeight * 12 - rowUpBit, fontPaint);
            }
            
            smInfoHeight = rowsHeight * 12;										//这里做特殊处理，因为增加参数后表格显示不下。
            
            rowsHeight = (this.getHeight() - rowsHeight * 12) / 8;				
            
            rowUpBit = (rowsHeight - textSize)/2 ; 
            
//          //绘制SM INFO 表格中间线条
          startx = colsWidth * (3);
          starty =  smInfoHeight  + rowsHeight  * 1 ;
          stopx = colsWidth * (3);
          stopy = this.getHeight() - 1 ;
          cv.drawLine(startx, starty, stopx, stopy, linePaint);
          
        //横线
  		for(int i=1;i<8;i++){
  			startx = 1;
  			starty =  smInfoHeight + rowsHeight * i;
  			stopx = this.getWidth() - 1;
  			stopy = smInfoHeight + rowsHeight *i;
  			cv.drawLine(startx, starty, stopx, stopy, linePaint);
  		}
            
            
            paraname = "SM Info";
            cv.drawText(paraname, (this.getWidth() - fontPaint.measureText(paraname)) / 2, (smInfoHeight + rowsHeight * 1) - rowUpBit, fontPaint);
            
           String[]  valueArray = new String[]{"Peak throughput","Radio Priority",
        		   								"Mean throughput","Max PDU Size",
        		   								"UL Max Bit Rate","UL GUAR Bit Rate",
        		   								"DL Max Bit Rate","DL GUAR Bit Rate",
        		   								"IP Address","SM State",
        		   								"Delay Class","Reliability Class",
        		   								"Precedence Class","LLC SAPI"
           };
           
            for (int i = 0,j = 2; i < valueArray.length; i+=2,j++) {
                paraname = valueArray[i];
                cv.drawText(paraname, marginSize , (smInfoHeight + rowsHeight * j) - rowUpBit, fontPaint);
                if(i+1 < valueArray.length){
                    paraname = valueArray[i+1];
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , (smInfoHeight + rowsHeight * j) - rowUpBit, fontPaint);
                }
            }
            
            
            
//            //版本有变  暂时添加，与第二页内容一致  2012.8.14
//            paraname = getContext().getString(R.string.gprsedge_gprs_time_slot_1);
//            cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 11 - rowUpBit, fontPaint);
//            switchMarker(cv, 1);
		}else if(currentPage ==2){
			 tableRows =19;	
			 rowsHeight = this.getHeight()/tableRows;	//行高
			 rowUpBit = (rowsHeight - textSize)/2 ;      //指定行上升位数,为行高-字体高度 再除2
		    
	          //四周边框
            cv.drawLine(1, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(1, rowsHeight * 15, this.getWidth()-1,  rowsHeight * 15 , linePaint);
            cv.drawLine(1, 1, 1,  rowsHeight * 15, linePaint);
            cv.drawLine(this.getWidth()-1, 1, this.getWidth()-1,  rowsHeight * 15, linePaint);
            //横线
            for(int i=0;i<tableRows - 5;i++){
                startx = 1;
                starty =  rowsHeight * (i+1);
                stopx = this.getWidth() - 1;
                stopy = rowsHeight *(i+1);
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
            }
            
            
            //绘制EDGE INFO 表格中间线条
            startx = this.getWidth() /2;
            starty =  rowsHeight * 1;
            stopx = startx;
            stopy = rowsHeight * 12 ;
            cv.drawLine(startx, starty, stopx, stopy, linePaint);
            
            
            //TS UL DL
            for(int i=0;i<tsRows;i++){
                startx = colsWidth + (tsClosWidth * i);
                starty =  rowsHeight * 13;
                stopx = colsWidth + (tsClosWidth * i);;
                stopy = rowsHeight * 15 ;
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
            }
            
	        paraname = getContext().getString(R.string.gprsedge_gprs_edge_2);
	        cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
		    
            paraname = getContext().getString(R.string.gprsedge_rac);
            cv.drawText(paraname, marginSize , rowsHeight * 2 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.gprsedge_t3314);
            cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 2 - rowUpBit, fontPaint);
            
            paraname = getContext().getString(R.string.gprsedge_dl_egprs_winsize);
            cv.drawText(paraname, marginSize , rowsHeight * 3 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.gprsedge_ul_egprs_winsize);
            cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 3 - rowUpBit, fontPaint);

            paraname = getContext().getString(R.string.gprsedge_gmm_state);
            cv.drawText(paraname, marginSize , rowsHeight * 4 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.gprsedge_dtm_support);
            cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 4 - rowUpBit, fontPaint);
            
            paraname = getContext().getString(R.string.gprsedge_pc_meas_channel);
            cv.drawText(paraname, marginSize , rowsHeight * 5 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.gprsedge_ctr_ack_mode);
            cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 5 - rowUpBit, fontPaint);
            
            paraname = getContext().getString(R.string.gprsedge_network_control_order);
            cv.drawText(paraname, marginSize , rowsHeight * 6 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.gprsedge_mac_mode);
            cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 6 - rowUpBit, fontPaint);
            
            paraname = getContext().getString(R.string.gprsedge_nmo);
            cv.drawText(paraname, marginSize , rowsHeight * 7 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.gprsedge_access_burst);
            cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 7 - rowUpBit, fontPaint);
            
            paraname = getContext().getString(R.string.gprsedge_apn);
            cv.drawText(paraname, marginSize , rowsHeight * 8 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.gprsedge_ul_max_bit_rate);
            cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 8 - rowUpBit, fontPaint);
            
            paraname = getContext().getString(R.string.gprsedge_ip_address);
            cv.drawText(paraname, marginSize , rowsHeight * 9 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.gprsedge_dl_max_bit_rate);
            cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 9 - rowUpBit, fontPaint);
            
            paraname = getContext().getString(R.string.gprsedge_radio_priority);
            cv.drawText(paraname, marginSize , rowsHeight * 10 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.gprsedge_ul_guarante_bit_rate);
            cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 10 - rowUpBit, fontPaint);
            
            paraname = getContext().getString(R.string.gprsedge_peak_throughput);
            cv.drawText(paraname, marginSize , rowsHeight * 11 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.gprsedge_dl_guarante_bit_rate);
            cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 11 - rowUpBit, fontPaint);
            
            paraname = getContext().getString(R.string.gprsedge_traffic_class);
            cv.drawText(paraname, marginSize , rowsHeight * 12 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.gprsedge_max_sdu_size);
            cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 12 - rowUpBit, fontPaint);
            
            paraname = getContext().getString(R.string.gprsedge_gprs_time_slot_1);
            cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 13 - rowUpBit, fontPaint);
		    
		    paraname = getContext().getString(R.string.gprsedge_tSul);
	        cv.drawText(paraname, (colsWidth - fontPaint.measureText(paraname)) / 2, rowsHeight * 14 - rowUpBit, fontPaint);
	        paraname = getContext().getString(R.string.gprsedge_tSdl);
	        cv.drawText(paraname, (colsWidth - fontPaint.measureText(paraname)) / 2, rowsHeight * 15 - rowUpBit, fontPaint);
	        /*for(int i=0;i<tsRows;i++){
	            paraname = getContext().getString(R.string.gprsedge_tS) + i;
	            cv.drawText(paraname, colsWidth + tsClosWidth * i + (tsClosWidth - fontPaint.measureText(paraname)) / 2, rowsHeight * 14 - rowUpBit, fontPaint);
	            paraname = getContext().getString(R.string.gprsedge_tS) + i;
	            cv.drawText(paraname, colsWidth + tsClosWidth * i + (tsClosWidth - fontPaint.measureText(paraname)) / 2, rowsHeight * 15 - rowUpBit, fontPaint);
	        }*/
//	        switchMarker(cv, 2);
		}
		

		cv.save();
		cv.restore();
	}
	/**
	 * 创建表格数据
	 * @param bm  要创建表格的位图
	 * @param data  表格数据
	 * @return    输出位图
	 */
	protected void CreateTableData(Canvas cv,TraceInfoData traceData){
		float rowsHeight = this.getHeight()/tableRows;	//行高
		float colsWidth = this.getWidth()/tableCols;		//列宽
		float rowUpBit = (rowsHeight - textSize)/2 ;		//指定行上升位数,为行高-字体高度 再除2
		int tsRows = 8;
		float tsClosWidth = (this.getWidth() - colsWidth ) / tsRows;
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);	
		paint.setColor(getResources().getColor(R.color.info_param_color));
		paint.setTypeface(null);
		paint.setTextSize(textSize);

		//Serving Cell System Info
		//GprsEdgeModel models = traceData.getGprsEdgeModel();
		
		String[] datas = new String[]{getParaValue(UnifyParaID.G_GPRS_DL_TBF_State).equals("0")?"Close":(getParaValue(UnifyParaID.G_GPRS_DL_TBF_State).equals("1")?"Open":""),getParaValue(UnifyParaID.G_GPRS_UL_TBF_State).equals("0")?"Close":(getParaValue(UnifyParaID.G_GPRS_UL_TBF_State).equals("1")?"Open":""),
		        getMcsCsShow(getParaValue(UnifyParaID.G_GPRS_DL_CS),getParaValue(UnifyParaID.G_GPRS_DL_MCS)),getMcsCsShow(getParaValue(UnifyParaID.G_GPRS_UL_CS),getParaValue(UnifyParaID.G_GPRS_UL_MCS)),
		        UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.G_GPRS_DL_RLC_Thr)),UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.G_GPRS_UL_RLC_Thr)),
		        /*getParaValue(UnifyParaID.G_GPRS_DL_RLC_Thr),getParaValue(UnifyParaID.G_GPRS_UL_RLC_Thr),
		        UtilsMethod.bps2Kbps(models.getdLLLCThr()),UtilsMethod.bps2Kbps(models.getuLLLCThr()),
		        models.getdLLLCRtx(),models.getuLLLCRtx(),*/
		        getParaValue(UnifyParaID.G_GPRS_DL_RLC_RTX),getParaValue(UnifyParaID.G_GPRS_UL_RLC_RTX),
		        getParaValue(UnifyParaID.G_GPRS_DL_TFI),getParaValue(UnifyParaID.G_GPRS_UL_TFI),
		        getParaValue(UnifyParaID.G_GPRS_GMSK_CV_BEP),getParaValue(UnifyParaID.G_GPRS_GMSK_MEAN_BEP),
		        getParaValue(UnifyParaID.G_GPRS_8PSK_CV_BEP),getParaValue(UnifyParaID.G_GPRS_8PSK_MEAN_BEP),
		        getParaValue(UnifyParaID.G_GPRS_GPRS_BLER),getParaValue(UnifyParaID.G_GPRS_CValue),
		        getParaValue(UnifyParaID.G_GPRS_CValue).equals("") ? "" : "0" ,getParaValue(UnifyParaID.G_GPRS_CValue).equals("") ? "" : "0" 
//		        	getParaValue(UnifyParaID.G_GPRS_RxQual),getParaValue(UnifyParaID.G_GPRS_SignVar)
		        };
        
        for (int i = 0,j = 2; i < datas.length; i+=2,j++) {
            cv.drawText(datas[i], this.getWidth()/2 -paint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paint);
            if(i+1 <datas.length){
                cv.drawText(datas[i+1], this.getWidth() -paint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paint);
            }
        }

        char[] tsUL = getTSBinaryChar(getParaValue(UnifyParaID.G_TS_UL_TS));
		char[] tsDL = getTSBinaryChar(getParaValue(UnifyParaID.G_TS_DL_TS));
		
		float left,right,bottom,top;
		String paraname = "";
		float strokeWidth =  1;
		for(int i=0; i < 8; i++){
			if(tsUL[i]=='1'){
				left = colsWidth + (tsClosWidth * i) + strokeWidth;
				right = colsWidth + (tsClosWidth * i) + tsClosWidth - strokeWidth;
				top = rowsHeight * 10 + strokeWidth;
				bottom = rowsHeight * 11 - strokeWidth;
				paint.setColor(getResources().getColor(R.color.green9));
				cv.drawRect(left, top, right, bottom, paint);
			
				paint.setColor(Color.WHITE);
				paraname = getContext().getString(R.string.gprsedge_tS) + i;
				cv.drawText(paraname, colsWidth + tsClosWidth * i + (tsClosWidth - paint.measureText(paraname)) / 2, rowsHeight * 11 - rowUpBit, paint);
			}
			//if(tsDlValue.get(i).value.equals("1")){
			if(tsDL[i]=='1'){
				left = colsWidth + (tsClosWidth * i) + strokeWidth;
				right = colsWidth + (tsClosWidth * i) + tsClosWidth - strokeWidth;
				top = rowsHeight * 11 + strokeWidth;
				bottom = rowsHeight * 12 + strokeWidth ;
				paint.setColor(getResources().getColor(R.color.green9));
				cv.drawRect(left, top, right, bottom, paint);

				paint.setColor(Color.WHITE);
				paraname = getContext().getString(R.string.gprsedge_tS) + i;
				cv.drawText(paraname, colsWidth + tsClosWidth * i + (tsClosWidth - paint.measureText(paraname)) / 2, rowsHeight * 12 - rowUpBit, paint);
			}
		}
		
		rowsHeight = (this.getHeight() - rowsHeight * 12) / 8;				
        
        rowUpBit = (rowsHeight - textSize)/2 ;
		
		
		PDPInfoDataV2 pdpInfo = (PDPInfoDataV2)TraceInfoInterface.getParaStruct(UnifyStruct.FLAG_TD_Activate_PDP_Context_Accept_Win_Data);
		if(pdpInfo != null){
			String[] smInfo = new String[]{String.valueOf(pdpInfo.Peak_Throughput),String.valueOf(pdpInfo.Radio_Priority),
					String.valueOf(pdpInfo.Mean_Throughput),String.valueOf(pdpInfo.Max_SDU_size),
					String.valueOf(pdpInfo.UL_Max_bit_Rate),String.valueOf(pdpInfo.UL_Guarante_bit_Rate),
					String.valueOf(pdpInfo.DL_Max_bit_Rate),String.valueOf(pdpInfo.DL_Guarante_bit_Rate),
					UtilsMethodPara.ToIP(String.valueOf(pdpInfo.IP)) ,getParaValue(UnifyParaID.G_GPRS_SM_STATE),
					String.valueOf(pdpInfo.Delay_Class),String.valueOf(pdpInfo.Reliability_Class),
					String.valueOf(pdpInfo.Precedence_Class),String.valueOf(pdpInfo.LLC_SAPI)
			        };
	        
	        for (int i = 0,j = 2; i < smInfo.length; i+=2,j++) {
	            cv.drawText(smInfo[i], this.getWidth()/2 -paramPaint.measureText(smInfo[i]) - marginSize, (smInfoHeight + rowsHeight * j) - rowUpBit, paramPaint);
	            if(i+1 <smInfo.length){
	                cv.drawText(smInfo[i+1], this.getWidth() -paramPaint.measureText(smInfo[i+1]) -marginSize, (smInfoHeight + rowsHeight * j) - rowUpBit, paramPaint);
	            }
	        }
		}else{
			String[] smInfo = new String[]{"","",
					"","",
					"","",
					"","",
					"",getParaValue(UnifyParaID.G_GPRS_SM_STATE),
					"","",
					"",""
			        };
	        
	        for (int i = 0,j = 14; i < smInfo.length; i+=2,j++) {
	            cv.drawText(smInfo[i], this.getWidth()/2 -paramPaint.measureText(smInfo[i]) - marginSize, (smInfoHeight + rowsHeight * j) - rowUpBit, paramPaint);
	            if(i+1 <smInfo.length){
	                cv.drawText(smInfo[i+1], this.getWidth() -paramPaint.measureText(smInfo[i+1]) -marginSize, (smInfoHeight + rowsHeight * j) - rowUpBit, paramPaint);
	            }
	        }
		}
		cv.save();
		cv.restore();
	}
	
	/**获得得参数队列中指定ID的值*/
    private String getParaValue(int paraId){
        return TraceInfoInterface.getParaValue(paraId);
    }
	
	/**
	 * 切换当前页标志图片<BR>
	 * [功能详细描述]
	 * @param canvas
	 * @param page 页码
	 */
	public void switchMarker(Canvas canvas,int page){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);   
        Bitmap markerBtm = null;
	    switch (page) {
            case 1:
                markerBtm = BitmapFactory.decodeResource(getResources(), R.drawable.lightdot);
                canvas.drawBitmap(markerBtm, (getWidth() - markerBtm.getWidth()) /2, getHeight() - 10, paint);
                break;
            case 2:
                markerBtm = BitmapFactory.decodeResource(getResources(), R.drawable.darkdot);
                canvas.drawBitmap(markerBtm, (getWidth() - markerBtm.getWidth() * 2) /2 , getHeight() - 10, paint);
                markerBtm = BitmapFactory.decodeResource(getResources(), R.drawable.lightdot);
                canvas.drawBitmap(markerBtm, (getWidth() - markerBtm.getWidth() * 2) /2  + markerBtm.getWidth() + 3, getHeight() - 10, paint);
                break;
            default:
                break;
        }
	}
	
	
	/**
	 * 将上下行时间隙值转换成char[8]数组返回
	 * @param timeSlot
	 * @return
	 */
	private char[] getTSBinaryChar(String timeSlot){
		char[] ts = new char[8];
		try{
			//当timeSlot不为空时才需进行处理
			if(!timeSlot.equals("")){
				int tsi = Integer.parseInt(timeSlot);
				//不在时隙值允许范围内
				if(tsi <0 || tsi > 255){
					return ts;
				}
				String tss = Integer.toBinaryString(tsi);
				char[] ts2 = tss.toCharArray();
				int num = 0;
				for(int i=ts2.length - 1; i>=0; i--){
					ts[7-num] = ts2[i];
					num ++;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ts;
	}
	
	/**
	 * 根据McsCS值进行显示
	 * McsCs 取值0-7，当小于等3时显示 CS+(i+1)，当大于3时显示MCS+(i+1)
	 * @param mcsCs
	 * @return
	 */
	private String getMcsCsShow(String cs,String mcs){
		try{
			if(!cs.equals("")){
				return "CS"+(Integer.parseInt(cs)+1);
			}else if(!mcs.equals("")){
				return "MCS"+(Integer.parseInt(mcs)+1);
			}else{
				return "";
			}
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}

}
