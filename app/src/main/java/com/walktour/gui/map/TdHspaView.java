package com.walktour.gui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UnifyStruct;
import com.walktour.Utils.UnifyStruct.ChannelInfo;
import com.walktour.Utils.UnifyStruct.HSDPAPhysChannelInfoData;
import com.walktour.framework.view.BasicParamView;
import com.walktour.gui.R;




/**
 * 
 * TD HSDPA参数
 * @author zhihui.lian
 * 
 *  
 */
public class TdHspaView extends BasicParamView {
    
    private int currentPage = 1;
    
    private int tableCols = 6; //列数

	private Paint tlPaint;		//图例画笔
	
	public TdHspaView(Context context) {
		super(context);
	}
	
	public TdHspaView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	   public TdHspaView(Context context,int page) {
	        super(context);
	        this.currentPage = page;
	    }
	
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param canvas
     * @see com.walktour.framework.view.BasicParamView#initView(android.graphics.Canvas)
     */
    @Override
    public void initView(Canvas canvas) {
    	tlPaint = new Paint();
    	tlPaint.setAntiAlias(true);
    	tlPaint.setStyle(Paint.Style.FILL);   
    	tlPaint.setTypeface(null);
    	tlPaint.setTextSize(textSize);
        CreateTable(canvas);
    }
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		CreateTableData(canvas);
	}
	
	/**
	 * 创建表格
	 * @param bm 要创建表格的位图
	 * @return   输出位图
	 */
	protected void CreateTable(Canvas cv){
		float 	startx 		= 1;
		float 	starty 		= 0;
		float 	stopx 		= 0;
		float 	stopy 		= 0;
		
		tableRows = 19;
		float colsWidth = this.getWidth()/tableCols;		//列宽
		float 	rowsHeight 	= this.getHeight()/tableRows;	//行高
		float rowUpBit = (rowsHeight - textSize)/2 ;		//指定行上升位数,为行高-字体高度 再除2
		if(currentPage ==1){
			
			rowsHeight = this.getHeight() /tableRows;
			rowUpBit = (rowsHeight - textSize)/2;
			//四周边框
			cv.drawLine(1, 1, this.getWidth() - 1, 1, linePaint);
			cv.drawLine(1, rowsHeight * (tableRows -1 ), this.getWidth()-1, rowsHeight * (tableRows - 1), linePaint);
			cv.drawLine(1, 1, 1, rowsHeight * (tableRows - 1) , linePaint);
			cv.drawLine(this.getWidth()-1, 1, this.getWidth()-1, rowsHeight * (tableRows - 1) , linePaint);
			
			//横线
			for(int i=0;i<tableRows - 1;i++){
				startx = 1;
				starty =  rowsHeight * (i+1);
				stopx = this.getWidth()-1;
				stopy = rowsHeight *(i+1);
				cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
			
			//Throughput-3 竖线
			startx = this.getWidth()/2;
			starty =  rowsHeight * 1;
			stopx = startx;
			stopy = rowsHeight * 5;
			cv.drawLine(startx, starty, stopx, stopy, linePaint);
			
			//HSUPA-2 竖线
			startx = this.getWidth()/2;
			starty =  rowsHeight * 6;
			stopx = startx;
			stopy = rowsHeight * 18;
			cv.drawLine(startx, starty, stopx, stopy, linePaint);
			
			String paraname;
			//HSPA Throughput Info
			paraname = getContext().getString(R.string.tdhspa_title_radio);
			cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
			
			
			int[] values = new int[]{R.string.tdhspa_hsdpa_session,R.string.tdhspa_category,
					R.string.tdhspa_work_uarfcn,R.string.tdhspa_h_rnti,
					R.string.tdhspa_a_dpch_rscp,R.string.tdhspa_a_dpch_c,
					R.string.tdhspa_scch_sir,R.string.tdhspa_a_sich_txpower
			};
			
			
			for (int i = 0,j = 2; i < values.length; i+=2,j++) {
				paraname = getContext().getString(values[i]);
				cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
				if(i+1 <values.length){
					paraname = getContext().getString(values[i+1]);
					cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
				}
			}
			
			/*            paraname = getContext().getString(R.string.hsdpa_dl_rlc_pdu_thr);
            cv.drawText(paraname, marginSize , rowsHeight * 2 - rowUpBit, fontPaint);  
            
            paraname = getContext().getString(R.string.hsdpa_ul_rlc_pdu_thr);
            cv.drawText(paraname, marginSize , rowsHeight * 3 - rowUpBit, fontPaint);  
            
            paraname = getContext().getString(R.string.hsdpa_dl_sdu_thr);
            cv.drawText(paraname, marginSize , rowsHeight * 4 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.hsdpa_ul_sdu_thr);
            cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 4 - rowUpBit, fontPaint);     
            
            paraname = getContext().getString(R.string.hsdpa_rlc_err_rate);
            cv.drawText(paraname, marginSize , rowsHeight * 5 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.hsdpa_rlc_rtx_rate);
            cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 5 - rowUpBit, fontPaint); */
			
			
			
			paraname = getContext().getString(R.string.tdhspa_title_qos);
			cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 6 - rowUpBit, fontPaint);
			
			values = new int[]{R.string.tdhspa_16qam_rate,R.string.tdhspa_hs_transblock_size,
					R.string.tdhspa_qpsk_rate,R.string.tdhspa_max_hsdpa_cqi,
					R.string.tdhspa_scch_schedulecount,R.string.tdhspa_mean_hsdpa_cqi,
					R.string.tdhspa_scch_schedulerate,R.string.tdhspa_min_hsdpa_cqi,
					R.string.tdhspa_scch_bler,R.string.tdhspa_pdsch_codesusedrate,
					R.string.tdhspa_scch_decodesuccrate,R.string.tdhspa_pdsch_timeslotused,
					R.string.tdhspa_dsch_nack_rate,R.string.tdhspa_pdsch_averagesize,
					R.string.tdhspa_dsch_ack_rate,R.string.tdhspa_pdsch_initialbler,
					R.string.tdhspa_dsch_unused_rate,R.string.tdhspa_pdsch_totalbler,
					R.string.tdhspa_dsch_error_rate,R.string.tdhspa_phys_schedule_thr,
					R.string.tdhspa_dsch_errorblocks,R.string.tdhspa_phys_service_thr,
					R.string.tdhspa_onetime_trans_succrate,R.string.tdhspa_hs_dsch_thr
			};
			
			for (int i = 0,j = 7; i < values.length; i+=2,j++) {
				paraname = getContext().getString(values[i]);
				cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
				if(i+1 <values.length){
					paraname = getContext().getString(values[i+1]);
					cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
				}
			}
		}else if(currentPage ==2){
			String paraname  = "";
			fontPaint.setColor(Color.WHITE);
            tableRows = 20;
            int  tsRows = 6 ;
            rowsHeight = this.getHeight()/tableRows;	//行高
            float tsClosWidth = (this.getWidth() - colsWidth ) / tsRows;
            
            rowUpBit = (rowsHeight - textSize)/2 ;
             //四周边框
            cv.drawLine(startx, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(startx,  rowsHeight * (tableRows -2 ), this.getWidth()-1, rowsHeight * (tableRows -2) , linePaint);
            cv.drawLine(startx, 1, startx,  rowsHeight * (tableRows -2), linePaint);
            cv.drawLine(this.getWidth()-1, startx, this.getWidth()-1, rowsHeight * (tableRows -2), linePaint);
            
            
            
          //横线
            for(int i=0;i<tableRows - 2;i++){
                startx = 1;
                starty =  rowsHeight * (i+1);
                stopx = this.getWidth() - 1;
                stopy = rowsHeight *(i+1);
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
				if (i < 16){
					String paranameQ = String.valueOf(i + 1 );
					cv.drawText(paranameQ,  (colsWidth - paramPaint.measureText(paranameQ)) / 2, rowsHeight + (rowsHeight * (i + 2)) - rowUpBit, fontPaint);
                }
            }
            
            paramPaint.setColor(Color.YELLOW);
            //竖线
            for(int i=0;i<tsRows;i++){
                startx = colsWidth + (tsClosWidth * i);
                starty =  rowsHeight * 1;
                stopx = colsWidth + (tsClosWidth * i);;
                stopy = rowsHeight * 18 ;
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
                String paranameQ = "TS" + (i + 1);
                cv.drawText(paranameQ, colsWidth + tsClosWidth * i + (tsClosWidth - paramPaint.measureText(paranameQ)) / 2, rowsHeight * 2 - rowUpBit, paramPaint);
            }
            paramPaint.setColor(getResources().getColor(R.color.info_param_color));
            
            paraname = "Physical Channel";
            cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
		
          int colorTagCls = 3 ;
          int colorWidth  = this.getWidth() / colorTagCls ;	
          
          
          
          String[] tagName1 = { "UL DPCH", "DL DPCH", "PDSCH" , "SICH", "SCCH"};
          
          int [] paintColor = {Color.RED , Color.YELLOW ,Color.BLUE,Color.GRAY,getResources().getColor(R.color.info_param_color)};
			
			
			for (int i = 0,j = 19; i < tagName1.length; i+=3,j++) {
					cv.drawText(tagName1[i], marginSize + colorWidth * 0, rowsHeight * j - rowUpBit, fontPaint);
					tlPaint.setColor(paintColor[i]);
				cv.drawRect(
						colorWidth - (colorWidth - fontPaint.measureText(tagName1[0])) + 10,
						rowsHeight * (j - 1) + 5, 
						colorWidth - (colorWidth - fontPaint.measureText(tagName1[0])) + 10 + tsClosWidth, 
						rowsHeight * j - 5,
						tlPaint);
				if (i + 1 < tagName1.length && j < 20) {
					tlPaint.setColor(paintColor[i+1]);
					cv.drawText(tagName1[i+1], marginSize + colorWidth * 1,
							rowsHeight * j - rowUpBit, fontPaint);
					cv.drawRect( 
							colorWidth + fontPaint.measureText(tagName1[0]) + 10, 
							rowsHeight * (j - 1) + 5 , 
							colorWidth + fontPaint.measureText(tagName1[0]) + 10 + tsClosWidth,
							rowsHeight * j - 5, 
							tlPaint);
				}
				if ((j < 20 ? i + 2 : i + 1) < tagName1.length) {
					tlPaint.setColor(paintColor[j < 20 ? i + 2 : i +1]);
					cv.drawText(tagName1[j < 20 ? i + 2 : i +1], marginSize + colorWidth * 2,
							rowsHeight * j - rowUpBit, fontPaint);
					cv.drawRect(
							colorWidth  * 2+ fontPaint.measureText(tagName1[0]) + 10, 
							 rowsHeight * (j - 1) + 5, 
							 colorWidth  * 2+ fontPaint.measureText(tagName1[0]) + 10 + tsClosWidth,
							rowsHeight * j - 5, 
							tlPaint);
				}
            }
		}
		else if (currentPage == 3){
		    tableRows = 19;
		    int dpchCols = 5 ;  //DPCH显示参数列5
		    int dpchColsWidth = (int) (this.getWidth() - (colsWidth * 0.5)) / dpchCols ;
            rowsHeight = this.getHeight() / tableRows;
            rowUpBit = (rowsHeight - textSize)/2 ;
             //四周边框
            cv.drawLine(startx, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(startx,  rowsHeight * (tableRows -11 ), this.getWidth()-1, rowsHeight * (tableRows -11) , linePaint);
            cv.drawLine(startx, 1, startx,  rowsHeight * (tableRows -11), linePaint);
            cv.drawLine(this.getWidth()-1, startx, this.getWidth()-1, rowsHeight * (tableRows -11), linePaint);
	        
	        //throughput
            String  paraname = "Physical Channel";
	        cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
	      
	      //
	        //横线
            for(int i=0;i < tableRows - 11;i++){
                startx = 1;
                starty =  rowsHeight * (i+1);
                stopx = this.getWidth() - 1;
                stopy = rowsHeight *(i+1);
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
                if (i < 7 && i > 0){
					String paranameQ = String.valueOf((i - 1)+1);  //画数字序号
					cv.drawText(paranameQ,  ((float)(colsWidth * 0.5) - paramPaint.measureText(paranameQ)) / 2, rowsHeight + rowsHeight * (i+1)  - rowUpBit, fontPaint);
                }
            }
	        String [] paraNameArr = {"Channel","R-Length","MA Mode","MA Config","MA Shirt"};
	        paramPaint.setColor(Color.YELLOW);
	        
	      //画竖线
            for(int i=0;i < dpchCols;i++){
                startx = (float) ((colsWidth * 0.5) + (dpchColsWidth * i));
                starty =  rowsHeight * 1;
                stopx = (float) ((colsWidth * 0.5) + (dpchColsWidth * i));;
                stopy = rowsHeight * 8 ;
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
                cv.drawText(paraNameArr[i], (float)(colsWidth * 0.5) + dpchColsWidth * i + (dpchColsWidth - paramPaint.measureText(paraNameArr[i])) / 2, rowsHeight * 2 - rowUpBit, paramPaint);
            }
	        
            paramPaint.setColor(getResources().getColor(R.color.info_param_color));
			
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
	protected void CreateTableData(Canvas cv){

		float rowsHeight = this.getHeight()/tableRows;	//行高
		float rowUpBit = (rowsHeight - textSize)/2 ;		//指定行上升位数,为行高-字体高度 再除2
		
		if(currentPage ==1){
			
//			HspaThrougInfo througModels = traceData.getHspaThrougInfo();
			String[] datas = new String[]{
					getParaValue(UnifyParaID.TD_DPA_HSDPA_Session),getParaValue(UnifyParaID.TD_DPA1_Category),
					getParaValue(UnifyParaID.TD_DPA1_Work_UARFCN),getParaValue(UnifyParaID.TD_DPA1_H_RNTI),
					getParaValue(UnifyParaID.TD_DPA1_A_DPCH_RSCP),getParaValue(UnifyParaID.TD_DPA1_A_DPCH_C2I),
					getParaValue(UnifyParaID.TD_DPA_SCCH_SIR),getParaValue(UnifyParaID.TD_DPA1_A_SICH_TxPower)};
			
			for (int i = 0,j = 2; i < datas.length; i+=2,j++) {
				cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
				if(i+1 <datas.length){
					cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
				}
			}
			
			datas = new String[]{getParaValue(UnifyParaID.TD_DPA2_16QAM_Rate) ,getParaValue(UnifyParaID.TD_DPA2_HS_TransBlock_Size),                            
					getParaValue(UnifyParaID.TD_DPA2_QPSK_Rate), getParaValue(UnifyParaID.TD_DPA2_Max_HSDPA_CQI),      
					getParaValue(UnifyParaID.TD_DPA2_SCCH_ScheduleCount),getParaValue(UnifyParaID.TD_DPA2_Mean_HSDPA_CQI),          
					getParaValue(UnifyParaID.TD_DPA2_SCCH_ScheduleRate),getParaValue(UnifyParaID.TD_DPA2_Min_HSDPA_CQI),       
					getParaValue(UnifyParaID.TD_DPA2_SCCH_BLER),getParaValue(UnifyParaID.TD_DPA2_PDSCH_CodesUsedRate),          
					getParaValue(UnifyParaID.TD_DPA2_SCCH_DecodeSuccRate),getParaValue(UnifyParaID.TD_DPA2_PDSCH_TimeSlotUsed),         
					getParaValue(UnifyParaID.TD_DPA2_DSCH_NACK_Rate),	getParaValue(UnifyParaID.TD_DPA2_PDSCH_AverageSize),              
					getParaValue(UnifyParaID.TD_DPA2_DSCH_ACK_Rate), getParaValue(UnifyParaID.TD_DPA2_PDSCH_InitialBLER),       
					getParaValue(UnifyParaID.TD_DPA2_DSCH_UnUsed_Rate), getParaValue(UnifyParaID.TD_DPA2_PDSCH_TotalBLER),
					getParaValue(UnifyParaID.TD_DPA2_DSCH_Error_Rate),	getParaValue(UnifyParaID.TD_DPA2_Phys_Schedule_Thr),              
					getParaValue(UnifyParaID.TD_DPA2_DSCH_ErrorBlocks), getParaValue(UnifyParaID.TD_DPA2_Phys_Service_Thr),       
					getParaValue(UnifyParaID.TD_DPA2_OneTime_Trans_SuccRate), ""};  	//最后一个参数没有 	
					for (int i = 0,j = 7; i < datas.length; i+=2,j++) {
						cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
						if(i+1 <datas.length){
							cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
						}
					}
		}else if(currentPage ==2){
			float colsWidth = this.getWidth()/tableCols;		//列宽
			tableRows = 20;
            int  tsRows = 6 ;
            rowsHeight = this.getHeight()/tableRows;	//行高
            float tsClosWidth = (this.getWidth() - colsWidth ) / tsRows;
            
            int [] paintColor = {Color.RED , Color.YELLOW ,Color.BLUE,Color.GRAY,getResources().getColor(R.color.info_param_color)};
            rowUpBit = (rowsHeight - textSize)/2 ;
			
            HSDPAPhysChannelInfoData  tdPhysChannelInfoDataV2 = (HSDPAPhysChannelInfoData)TraceInfoInterface.getParaStruct(UnifyStruct.FLAG_TD_HSDPAPhysChannelInfoData);
			if(tdPhysChannelInfoDataV2 != null){
				fontPaint.setColor(Color.BLACK);
				for (int i = 0; i < tdPhysChannelInfoDataV2.chanInfo.size(); i++) {
					ChannelInfo info = tdPhysChannelInfoDataV2.chanInfo.get(i);
					
					if( info!= null && info.channelType!= ChannelInfo.INVALID_TYPE ){
						if(info.codeNO_count > 0){
    						for (int j = 0; j < info.codeNO.length; j++) {
    							Log.i("---codeNO", info.codeNO[j] + "");
    								if(info.channelType == ChannelInfo.TYPE_PDSCH ){
    									tlPaint.setColor(paintColor[2]);
    								}else if(info.channelType == ChannelInfo.TYPE_SCCH  ){
    									tlPaint.setColor(paintColor[4]);
    								}else if(info.channelType == ChannelInfo.TYPE_SICH){
    									tlPaint.setColor(paintColor[3]);
    								}	
    							float left = colsWidth + (info.timeSlot - 1) * tsClosWidth + 1;
    							float top  = rowsHeight * 2 + (info.codeNO[j] - 1) * rowsHeight + 1;
    							float right = colsWidth + info.timeSlot * tsClosWidth - 1 ;
    							float bottom = rowsHeight * 2 + info.codeNO[j] * rowsHeight -1 ;
    							cv.drawRect(left,top,right,bottom,tlPaint);
    							String paraValue = "SF="+info.sf;
    							cv.drawText(paraValue, 
    									colsWidth + (info.timeSlot - 1) * tsClosWidth + (tsClosWidth - fontPaint.measureText(paraValue)) / 2, 
    									rowsHeight * 2 + info.codeNO[j] * rowsHeight -  rowUpBit ,
    									fontPaint);
    						}
    					}
					}
				}
			}
		
		}
		else if (currentPage == 3){
			float colsWidth = this.getWidth()/tableCols;		//列宽
			  int dpchCols = 5 ;  //DPCH显示参数列5
  		    int dpchColsWidth = (int) (this.getWidth() - (colsWidth * 0.5)) / dpchCols ;
			
			String[]  datas = new String[]{
					"SCCH",
					"0",
					"0",
					"4",
					"0",
					
					"PDSCH",
					"0",
					"0",
					"4",
					"0",
					
					"SICH",
					"0",
					"0",
					"4",
					"0",
					
			};
			HSDPAPhysChannelInfoData  tdPhysChannelInfoDataV2 = (HSDPAPhysChannelInfoData)TraceInfoInterface.getParaStruct(UnifyStruct.FLAG_TD_HSDPAPhysChannelInfoData);
			if(tdPhysChannelInfoDataV2 != null){
				for (int i = 0,j = 3; i < datas.length; i+=5,j++) {
                cv.drawText(datas[i], 	(float)(colsWidth * 0.5) + dpchColsWidth * 0 + (dpchColsWidth - paramPaint.measureText(datas[i])) / 2, rowsHeight * j - rowUpBit, paramPaint);
                cv.drawText(datas[i+1], (float)(colsWidth * 0.5) + dpchColsWidth * 1 + (dpchColsWidth - paramPaint.measureText(datas[i+1])) / 2, rowsHeight * j - rowUpBit, paramPaint);
                cv.drawText(datas[i+2], (float)(colsWidth * 0.5) + dpchColsWidth * 2 + (dpchColsWidth - paramPaint.measureText(datas[i+2])) / 2, rowsHeight * j - rowUpBit, paramPaint);
                cv.drawText(datas[i+3], (float)(colsWidth * 0.5) + dpchColsWidth * 3 + (dpchColsWidth - paramPaint.measureText(datas[i+3])) / 2, rowsHeight * j - rowUpBit, paramPaint);
                cv.drawText(datas[i+4], (float)(colsWidth * 0.5) + dpchColsWidth * 4 + (dpchColsWidth - paramPaint.measureText(datas[i+4])) / 2, rowsHeight * j - rowUpBit, paramPaint);
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
                canvas.drawBitmap(markerBtm, (getWidth() - markerBtm.getWidth() * 2) /2, getHeight() - 10, paint);
                markerBtm = BitmapFactory.decodeResource(getResources(), R.drawable.darkdot);
                canvas.drawBitmap(markerBtm, (getWidth() - markerBtm.getWidth() * 2) /2 + markerBtm.getWidth() +3, getHeight() - 10, paint);
                break;
            case 2:
                markerBtm = BitmapFactory.decodeResource(getResources(), R.drawable.darkdot);
                canvas.drawBitmap(markerBtm, (getWidth() - markerBtm.getWidth() * 2) /2 , getHeight() - 10, paint);
                markerBtm = BitmapFactory.decodeResource(getResources(), R.drawable.lightdot);
                canvas.drawBitmap(markerBtm, (getWidth() - markerBtm.getWidth() * 2) /2  + markerBtm.getWidth() +3, getHeight() - 10, paint);
                break;
            default:
                break;
        }
	}
	
}
