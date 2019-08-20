package com.walktour.gui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethod;
import com.walktour.framework.view.BasicParamView;
import com.walktour.gui.R;

public class HspaView extends BasicParamView {
    
    private int currentPage = 1;
    
    private int tableCols = 6; //列数
	
	public HspaView(Context context) {
		super(context);
	}
	
	public HspaView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	   public HspaView(Context context,int page) {
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
		float 	startx 		= 1;
		float 	starty 		= 0;
		float 	stopx 		= 0;
		float 	stopy 		= 0;
		float 	rowsHeight 	= this.getHeight()/tableRows;	//行高
		float rowUpBit = (rowsHeight - textSize)/2 ;		//指定行上升位数,为行高-字体高度 再除2
		
		if(currentPage ==1){
		    tableRows = 19;
		    rowsHeight = this.getHeight()/ tableRows;
		    rowUpBit = (rowsHeight - textSize)/2;
		    
	        //四周边框
            cv.drawLine(1, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(1,  rowsHeight * (tableRows-1), this.getWidth()-1,rowsHeight * (tableRows-1) , linePaint);
            cv.drawLine(1, 1, 1,  rowsHeight * (tableRows-1), linePaint);
            cv.drawLine(this.getWidth()-1, 1, this.getWidth()-1, rowsHeight * (tableRows-1), linePaint);
            
			//横线
			for(int i=0;i<tableRows - 1;i++){
				startx = 1;
				starty =  rowsHeight * (i+1);
				stopx = this.getWidth()-1;
				stopy = rowsHeight *(i+1);
				cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
			//Serving cell 分页竖线
            startx = this.getWidth() /2;
            starty =  rowsHeight * 1;
            stopx = startx;
            stopy = rowsHeight  * 14;
            cv.drawLine(startx, starty, stopx, stopy, linePaint);

			String paraname;
			//HSDPA System Info
			paraname = getContext().getString(R.string.hsdpa_hsdpaSystemInfo);
			cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
			
			int[] values = new int[]{R.string.hsdpa_session,R.string.hsdpa_mino_support,
			        R.string.hsdpa_cqi_min,R.string.hsdpa_class_category,
			        R.string.hsdpa_cqi_mean,R.string.hsdpa_h_rnti,
			        R.string.hsdpa_cqi_max,R.string.hsdpa_16qam_config,
			        R.string.hsdpa_qpsk_rate,R.string.hsdpa_scch_code_num,
			        R.string.hsdpa_16qam_rate,R.string.hsdpa_transbolck_size,
			        R.string.hsdpa_64qam_rate,R.string.hsdpa_dsch_error_rate,
			        R.string.hsdpa_scch_decodesuccrate,R.string.hsdpa_dsch_error_blocks,
			        R.string.hsdpa_dsch_ack_rate,R.string.hsdpa_dsch_total_code_num,
			        R.string.hsdpa_dsch_dtx_rate,R.string.hsdpa_dsch_avg_code_num,
			        R.string.hsdpa_harq_responserate,R.string.hsdpa_dsch_schedule_num,
			        R.string.hsdpa_harq_process_num,R.string.hsdpa_scch_schedule_num,
			        R.string.hsdpa_cqi_power_offset,R.string.hsdpa_scch_schedule_ratio
			        //,R.string.hsdpa_dsch_one_rtx_rate
			        };
			
            for (int i = 0,j = 2; i < values.length; i+=2,j++) {
                paraname = getContext().getString(values[i]);
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <values.length){
                    paraname = getContext().getString(values[i+1]);
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
            }
            
            paraname = getContext().getString(R.string.hsdpa_req_phys_layer_rate);
            cv.drawText(paraname, marginSize , rowsHeight * 15 - rowUpBit, fontPaint);
     
            paraname = getContext().getString(R.string.hsdpa_shd_phys_layer_rate);
            cv.drawText(paraname, marginSize , rowsHeight * 16 - rowUpBit, fontPaint);

            paraname = getContext().getString(R.string.hsdpa_srv_phys_layer_rate);
            cv.drawText(paraname, marginSize , rowsHeight * 17 - rowUpBit, fontPaint);
            
            paraname = getContext().getString(R.string.hsdpa_mac_layer_rate);
            cv.drawText(paraname, marginSize , rowsHeight * 18 - rowUpBit, fontPaint);            
		}else if(currentPage ==2){
		    rowsHeight = this.getHeight() /tableRows;
		    rowUpBit = (rowsHeight - textSize)/2;
            //四周边框
            cv.drawLine(1, 1, this.getWidth() - 1, 1, linePaint);
            cv.drawLine(1, rowsHeight * (tableRows -3 ), this.getWidth()-1, rowsHeight * (tableRows - 3), linePaint);
            cv.drawLine(1, 1, 1, rowsHeight * (tableRows - 3) , linePaint);
            cv.drawLine(this.getWidth()-1, 1, this.getWidth()-1, rowsHeight * (tableRows - 3) , linePaint);
            
			//横线
			for(int i=0;i<tableRows - 3;i++){
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
            stopy = rowsHeight * 4;
            cv.drawLine(startx, starty, stopx, stopy, linePaint);
			
            //HSUPA-2 竖线
            startx = this.getWidth()/2;
            starty =  rowsHeight * 5;
            stopx = startx;
            stopy = rowsHeight * 14;
            cv.drawLine(startx, starty, stopx, stopy, linePaint);

			String paraname;
			//HSPA Throughput Info
			paraname = getContext().getString(R.string.hsdpa_throughput_3);
			cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
			
			
			int[] values = new int[]{R.string.hsdpa_dl_rlc_pdu_thr,R.string.hsdpa_dl_sdu_thr,
			        R.string.hsdpa_ul_rlc_pdu_thr,R.string.hsdpa_ul_sdu_thr,
			        R.string.hsdpa_rlc_err_rate,R.string.hsdpa_rlc_rtx_rate};
			
			
            for (int i = 0,j = 2; i < values.length; i+=2,j++) {
                paraname = getContext().getString(values[i]);
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <values.length){
                    paraname = getContext().getString(values[i+1]);
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
            }
            paraname = getContext().getString(R.string.hsdpa_hsupa_2);
            cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 5 - rowUpBit, fontPaint);
            
            values = new int[]{R.string.hsdpa_session,R.string.hsdpa_e_rnti,
                    R.string.hsdpa_class_category,R.string.hsdpa_secondary_e_rnti,
                    R.string.hsdpa_happybit_rate,R.string.hsdpa_serving_tti,
                    R.string.hsdpa_etfci_ltdmp_rate,R.string.hsdpa_sgi_average,
                    R.string.hsdpa_etfci_ltdsg_rate,R.string.hsdpa_ack_rate,
                    R.string.hsdpa_etfci_ltdbo_rate,R.string.hsdpa_nack_rate,
                    R.string.hsdpa_a_set_count,R.string.hsdpa_dtx_rate,
                    R.string.hsdpa_ue_frame_usage,R.string.hsdpa_one_rtx_rate,
                    R.string.hsdpa_tb_size_max,R.string.hsdpa_two_rtx_rate};
            
            for (int i = 0,j = 6; i < values.length; i+=2,j++) {
                paraname = getContext().getString(values[i]);
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <values.length){
                    paraname = getContext().getString(values[i+1]);
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
            }
            
            
            paraname = getContext().getString(R.string.hsdpa_served_throughput);
            cv.drawText(paraname, marginSize , rowsHeight * 15 - rowUpBit, fontPaint);
  
            paraname = getContext().getString(R.string.hsdpa_mac_throughput);
            cv.drawText(paraname, marginSize , rowsHeight * 16 - rowUpBit, fontPaint);
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
		float rowUpBit = (rowsHeight - textSize)/2 ;		//指定行上升位数,为行高-字体高度 再除2
		
		if(currentPage ==1){
			String[] datas = new String[]{getParaValue(UnifyParaID.W_DPA1_Session),	getParaValue(UnifyParaID.W_DPA1_MIMO_Support),
					getParaValue(UnifyParaID.W_DPA1_CQI_Min),	getParaValue(UnifyParaID.W_DPA1_Class_Category),
					getParaValue(UnifyParaID.W_DPA1_CQI_Mean),	getParaValue(UnifyParaID.W_DPA1_H_RNTI),
					getParaValue(UnifyParaID.W_DPA1_CQI_Max),	getParaValue(UnifyParaID.W_DPA1_16QAM_Config),
					getParaValue(UnifyParaID.W_DPA1_QPSK_Rate),	getParaValue(UnifyParaID.W_DPA1_SCCH_Code_Num),
					getParaValue(UnifyParaID.W_DPA1_16QAM_Rate),getParaValue(UnifyParaID.W_DPA1_TransBolck_Size) ,
					getParaValue(UnifyParaID.W_DPA1_64QAM_Rate),getParaValue(UnifyParaID.W_DPA1_DSCH_Error_Rate),
					getParaValue(UnifyParaID.W_DPA1_SCCH_DecodeSuccRate),getParaValue(UnifyParaID.W_DPA1_DSCH_Error_Blocks),
					getParaValue(UnifyParaID.W_DPA1_DSCH_ACK_Rate),getParaValue(UnifyParaID.W_DPA1_DSCH_Total_Code_Num),
					getParaValue(UnifyParaID.W_DPA1_DSCH_DTX_Rate),getParaValue(UnifyParaID.W_DPA1_DSCH_Avg_Code_Num),
					getParaValue(UnifyParaID.W_DPA1_HARQ_ResponseRate),getParaValue(UnifyParaID.W_DPA1_DSCH_Schedule_Num),
					getParaValue(UnifyParaID.W_DPA1_HARQ_Process_Num),	getParaValue(UnifyParaID.W_DPA1_SCCH_Schedule_Num),
					getParaValue(UnifyParaID.W_DPA1_CQI_Power_Offset), getParaValue(UnifyParaID.W_DPA1_SCCH_Schedule_Ratio)
					//,througModels.gethS_DSCH_Retrans_Rate(),
			        }; 
			
            for (int i = 0,j = 2; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                if(i+1 <datas.length){
                    cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }
            }
            
            datas = new String[]{UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.W_DPA1_Phys_Request_Thr)),
					UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.W_DPA1_Phys_Schedule_Thr)),
					UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.W_DPA1_Phys_Service_Thr)),
					UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.W_DPA1_MAC_Thr))};
        	for (int i = 0,j = 15; i < datas.length; i++,j++) {
                cv.drawText(datas[i], this.getWidth() - paramPaint.measureText(datas[i]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
            }
		}else if(currentPage == 2){
			String[] datas = new String[]{
					UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.W_Thr_DL_RLC_Thr)),UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.W_Thr_DL_PDCP_Thr)),
					UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.W_Thr_UL_RLC_Thr)),UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.W_Thr_UL_PDCP_Thr)),
					getParaValue(UnifyParaID.W_Thr_RLC_Err_Rate),getParaValue(UnifyParaID.W_Thr_RLC_RTX_Rate)};
            for (int i = 0,j = 2; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                if(i+1 <datas.length){
                    cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }
            }
			
			String sessionStr = getParaValue(UnifyParaID.W_UPA1_Session);
			boolean isSessionNull = sessionStr.trim().equals("")
									|| sessionStr.equals("0");
			datas = new String[]{sessionStr,getParaValue(UnifyParaID.W_UPA1_E_RNTI),
					getParaValue(UnifyParaID.W_UPA1_Class_Category),getParaValue(UnifyParaID.W_UPA1_E_DPCCH_Power),
					getParaValue(UnifyParaID.W_UPA1_HappyBit_Rate),getParaValue(UnifyParaID.W_UPA1_Serving_TTI),
					getParaValue(UnifyParaID.W_UPA1_ETFCI_LTDMP_Rate),getParaValue(UnifyParaID.W_UPA1_SGI_Average),
					getParaValue(UnifyParaID.W_UPA1_ETFCI_LTDSG_Rate),getParaValue(UnifyParaID.W_UPA1_ACK_Rate),
					getParaValue(UnifyParaID.W_UPA1_ETFCI_LTDBo_Rate),getParaValue(UnifyParaID.W_UPA1_NACK_Rate),
					getParaValue(UnifyParaID.W_UPA1_A_Set_Count),	getParaValue(UnifyParaID.W_UPA1_DTX_Rate),
					getParaValue(UnifyParaID.W_UPA1_UE_Frame_Usage),getParaValue(UnifyParaID.W_UPA1_One_RTX_Rate),
					getParaValue(UnifyParaID.W_UPA1_TB_Size_Max),getParaValue(UnifyParaID.W_UPA1_Two_RTX_Rate)
			        };
			
            for (int i = 0,j = 6; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                if(i+1 <datas.length){
                    cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }
            }
            
            String phy=getParaValue(UnifyParaID.W_UPA1_Phys_Service_Thr);
            
            cv.drawText(UtilsMethod.bps2Kbps(phy), 
            		this.getWidth() -paramPaint.measureText(UtilsMethod.bps2Kbps(phy)) -marginSize, rowsHeight * 15 - rowUpBit, paramPaint);
            
            String mac_Thr=getParaValue(UnifyParaID.W_UPA1_MAC_Thr);
            
            cv.drawText(UtilsMethod.bps2Kbps(mac_Thr), 
            		this.getWidth() -paramPaint.measureText(UtilsMethod.bps2Kbps(mac_Thr)) -marginSize, rowsHeight * 16 - rowUpBit, paramPaint);

/*			value = models.getdLPDUThr();
			cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 2 - rowUpBit, paramPaint);
			value = models.getuLPDUThr();
			cv.drawText(value, colsWidth * 5 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 2 - rowUpBit, paramPaint);
			value = models.getpDUErrRate();
			cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 3 - rowUpBit, paramPaint);
			value = models.getpDURTXRate();
			cv.drawText(value, colsWidth * 5 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 3 - rowUpBit, paramPaint);
			value = models.getE_RNTI();
			cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 4 - rowUpBit, paramPaint);
			value = models.getrLCULReTxmtPDU();
			cv.drawText(value, colsWidth * 5 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 4 - rowUpBit, paramPaint);
			value = models.getrLCULNAKPDU();
			cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 5 - rowUpBit, paramPaint);
			value = models.gethS_DSCH_16QAM_Rate();
			cv.drawText(value, colsWidth * 5 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 5 - rowUpBit, paramPaint);
			value = models.gethS_DSCH_QPSK_Rate();
			cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 6 - rowUpBit, paramPaint);
			value = models.gethS_DSCH_Error_Block();
			cv.drawText(value, colsWidth * 5 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 6 - rowUpBit, paramPaint);
			value = models.gethS_DSCH_Error_Rate();
			cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 7 - rowUpBit, paramPaint);
			value = models.gethS_DSCH_ACK_Rate();
			cv.drawText(value, colsWidth * 5 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 7 - rowUpBit, paramPaint);
			value = models.gethS_DSCH_NACK_Rate();
			cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 8 - rowUpBit, paramPaint);
			value = models.gethS_DSCH_Throughput();
			cv.drawText(value, colsWidth * 5 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 8 - rowUpBit, paramPaint);
			value = models.gethS_DSCH_BLER_Average();
			cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 9 - rowUpBit, paramPaint);
			value = models.gethS_DSCH_DTX_Rate();
			cv.drawText(value, colsWidth * 5 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 9 - rowUpBit, paramPaint);
			value = models.gethS_DSCH_DSR();
			cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 10 - rowUpBit, paramPaint);
			value = models.gethS_DPCCH_CQI_MAX();
			cv.drawText(value, colsWidth * 5 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 10 - rowUpBit, paramPaint);
			value = models.gethS_DPCCH_CQI_MIN();
			cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 11 - rowUpBit, paramPaint);
			value = models.gethS_DPCCH_CQI_Median();
			cv.drawText(value, colsWidth * 5 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 11 - rowUpBit, paramPaint);
			value = models.gethS_Phyiscal_Requested_Throughput();
			cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 12 - rowUpBit, paramPaint);
			value = models.gethS_Phyiscal_Scheduled_Throughput();
			cv.drawText(value, colsWidth * 5 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 12 - rowUpBit, paramPaint);
			value = models.gethS_Phyiscal_Served_Throughput();
			cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 13 - rowUpBit, paramPaint);
			value = models.gethS_DSCH_Retrans_Rate();
			cv.drawText(value, colsWidth * 5 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 13 - rowUpBit, paramPaint);
			value = models.gethS_DSCH_RetransNum();
			cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 14 - rowUpBit, paramPaint);
			value = models.getcQIcount();
			cv.drawText(value, colsWidth * 5 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 14 - rowUpBit, paramPaint);
			value = models.getcQIBlockAccValue();
			cv.drawText(value, colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 15 - rowUpBit, paramPaint);
			value = models.getcQIBlockCurValue();
			cv.drawText(value, colsWidth * 5 + (colsWidth - paramPaint.measureText(value)) / 2, rowsHeight * 15 - rowUpBit, paramPaint);*/
		
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
