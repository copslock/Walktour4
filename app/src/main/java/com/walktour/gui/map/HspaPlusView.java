package com.walktour.gui.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethod;
import com.walktour.framework.view.BasicParamView;
import com.walktour.gui.R;

/**
 * HSPA+视图类
 * @author li.bie
 *
 */
public class HspaPlusView extends BasicParamView {
    
    private int currentPage = 1;
    
    private int tableCols = 4; //列数
    
    private int tableColsSec = 3; //第二页列数
    
    private float rowsHeight;
    
    private float rowUpBit;
    
    private float colsWidth;
    
	public HspaPlusView(Context context) {
		super(context);
	}
	
	public HspaPlusView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
   public HspaPlusView(Context context,int page) {
        super(context);
        currentPage = page;
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
		rowsHeight 	= this.getHeight()/tableRows;	//行高
		colsWidth 	= this.getWidth()/tableCols;		//列宽
		rowUpBit = (rowsHeight - textSize)/2 ;		//指定行上升位数,为行高-字体高度 再除2
		
		if(currentPage ==1){
		    
            //四周边框
            cv.drawLine(1, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(1,  rowsHeight * (tableRows-1), this.getWidth()-1,rowsHeight * (tableRows-1) , linePaint);
            cv.drawLine(1, 1, 1,  rowsHeight * (tableRows-1), linePaint);
            cv.drawLine(this.getWidth()-1, 1, this.getWidth()-1, rowsHeight * (tableRows-1), linePaint);
            
			//横线
			for(int i=0;i<tableRows -1;i++){
				startx = 1;
				starty =  rowsHeight * (i+1);
				stopx = this.getWidth()-1;
				stopy = rowsHeight *(i+1);
				cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
			
			cv.drawLine(this.getWidth() / 2, 1 * rowsHeight, this.getWidth() / 2,  rowsHeight * 3, linePaint);
			
			cv.drawLine(this.getWidth() / 2, 4 * rowsHeight, this.getWidth() / 2,  rowsHeight * 7, linePaint);
			
			int midVar = 0;	//只适合三条线以下的.
			//Serving cell竖线
			for(int i=0;i<tableCols - 1;i++){
				midVar +=i;
				startx = colsWidth * (midVar + 2);
				starty =  rowsHeight * 7;
				stopx = colsWidth * (midVar + 2);
				stopy = rowsHeight * (tableRows - 1) ;
				cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
			
			String paraname;
			//HSPA+ MIMO Info
			paraname = getContext().getString(R.string.hs_mimo_state);
			cv.drawText(paraname,  (this.getWidth()  - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
			int[] values = new int[]{R.string.hs_mimo_session,R.string.hs_mimo_class_category,
			        R.string.hs_mimo_dual_carrier_session};
			
            for (int i = 0,j = 2; i < values.length; i+=2,j++) {
                paraname = getContext().getString(values[i]);
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <values.length){
                    paraname = getContext().getString(values[i+1]);
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
            }
            
            values = new int[]{R.string.hs_ratematchcode,R.string.hs_mimo_secondary_ernti,
                    R.string.hs_mimo_typea_cqi_rate,R.string.hs_mimo_typea_cqi_mean,
                    R.string.hs_mimo_typeb_cqi_rate,R.string.hs_mimo_typeb_cqi_mean
                    };
            for (int i = 0,j = 5; i < values.length; i+=2,j++) {
                paraname = getContext().getString(values[i]);
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <values.length){
                    paraname = getContext().getString(values[i+1]);
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
            }
            
            paraname = "Parameter";
            cv.drawText(paraname,  colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowsHeight * 8 - rowUpBit, fontPaint);
            paraname = "1st";
            cv.drawText(paraname, colsWidth * 2 + (colsWidth * 1 - fontPaint.measureText(paraname)) / 2, rowsHeight * 8 - rowUpBit, fontPaint);
            paraname = "2nd";
            cv.drawText(paraname,  colsWidth * 3 + (colsWidth * 1 - fontPaint.measureText(paraname)) / 2, rowsHeight * 8 - rowUpBit, fontPaint);
            
	        paraname = getContext().getString(R.string.hs_mimo_infos);
	        cv.drawText(paraname,  (this.getWidth()  - fontPaint.measureText(paraname)) / 2, rowsHeight * 4 - rowUpBit, fontPaint);
			values = new int[]{R.string.hs_mimo_pre_tb_rate,
			        R.string.hs_mimo_transblock_size_max,R.string.hs_mimo_dsch_bler_re_rate,R.string.hs_mimo_dsch_bler_fst_rate,
			        R.string.hs_mimo_dsch_ack_rate,/*R.string.hs_mimo_dsch_nack_rate,*/R.string.hs_mimo_16qam_rate,
			        R.string.hs_mimo_64qam_rate,R.string.hs_mimo_qpsk_rate,R.string.hs_mimo_phy_scheduled_th,
			        R.string.hs_mimo_phy_served_th};
			
			for (int i = 0,j = 9; i < values.length; i++,j++) {
			    paraname = getContext().getString(values[i]);
			    cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowsHeight * j - rowUpBit, fontPaint);
            }
			
		}else if(currentPage ==2){
			rowsHeight 	= this.getHeight()/tableRows;	//行高
			rowUpBit = (rowsHeight - textSize)/2 ;
            //四周边框
            cv.drawLine(1, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(1,  rowsHeight * (tableRows-1), this.getWidth()-1,rowsHeight * (tableRows-1) , linePaint);
            cv.drawLine(1, 1, 1,  rowsHeight * (tableRows-1), fontPaint);
            cv.drawLine(this.getWidth()-1, 1, this.getWidth()-1, rowsHeight * (tableRows-1), linePaint);
			//横线
			for(int i=0;i<tableRows - 1;i++){
				startx = 1;
				starty =  rowsHeight * (i+1);
				stopx = this.getWidth() -1;
				stopy = rowsHeight *(i+1);
				cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
			
			int midVar = 0;	//只适合三条线以下的.
			//Serving cell竖线
			for(int i=0;i<tableColsSec - 1;i++){
				midVar +=i;
				startx = colsWidth * (midVar + 2);
				starty =  rowsHeight * 1;
				stopx = colsWidth * (midVar + 2);
				stopy = rowsHeight * (tableRows - 1) ;
				cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
			String paraname;
			//HSDPA Dual Cell Info
			paraname = getContext().getString(R.string.hs_dual_cell_info);
			cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
			
            paraname = "Parameter";
            cv.drawText(paraname,  colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowsHeight * 2 - rowUpBit, fontPaint);
            paraname = "1st";
            cv.drawText(paraname, colsWidth * 2 + (colsWidth * 1 - fontPaint.measureText(paraname)) / 2, rowsHeight * 2 - rowUpBit, fontPaint);
            paraname = "2nd";
            cv.drawText(paraname,  colsWidth * 3 + (colsWidth * 1 - fontPaint.measureText(paraname)) / 2, rowsHeight * 2 - rowUpBit, fontPaint);
			
			int[] values = new int[]{R.string.hs_dual_serving_cell,R.string.hs_dual_serving_uarfcn,
			        R.string.hs_transblockcount,R.string.hs_transblocksize,R.string.hs_dual_cell_cqi_mean,
			        R.string.hs_dual_cell_dsch_bler_re_rate,R.string.hs_dual_cell_dsc_bler_fs_rate,
			        R.string.hs_dual_cell_dsch_ack_rate,R.string.hs_dual_cell_dsch_dtx_rate,
			        R.string.hs_dual_cell_dsch_retrans_rate,R.string.hs_dual_cell_64qam_rate,
			        R.string.hs_dual_cell_16qam_rate,/*R.string.hs_dual_cell_qpsk_rate,*/
			        R.string.hs_dual_cell_decode_success_rate,R.string.hs_dual_cell_phy_requested_throughput,
			        R.string.hs_dual_cell_phy_scheduled_th,R.string.hs_dual_cell_phy_served_th};
	         for (int i = 0,j = 3; i < values.length; i++,j++) {
	                paraname = getContext().getString(values[i]);
	                cv.drawText(paraname, colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2, rowsHeight * j - rowUpBit, fontPaint);
	         }
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
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);	
		paint.setColor(getResources().getColor(R.color.info_param_color));
		paint.setTypeface(null);
		paint.setTextSize(textSize);
		if(currentPage ==1){
			//HSPA+ MIMO Info
//			HspaPlusModel model = traceData.getHspaPlusModel();
			String session = getParaValue(UnifyParaID.W_PA1_HSPAPlus_Session);
			
			boolean isSessionNumm = session.trim().equals("")
									|| session.equals("0");
			String[] datas = new String[]{session, isSessionNumm ? "" : getParaValue(UnifyParaID.W_PA1_HSPAPlus_Class_Category),
					isSessionNumm ? "" : getParaValue(UnifyParaID.W_PA1_Dual_Carrier_Session_)};
            for (int i = 0,j = 2; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                if(i+1 <datas.length){
                    cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }
            }
            datas = new String[]{	isSessionNumm ? "" : getParaValue(UnifyParaID.W_MM_RateMatchCode),		isSessionNumm ? "" : getParaValue(UnifyParaID.W_MM_Secondary_E_RNTI),
            						isSessionNumm ? "" : getParaValue(UnifyParaID.W_MM_TypeA_CQI_Rate),	isSessionNumm ? "" : getParaValue(UnifyParaID.W_MM_TypeA_CQI_Mean),
    								isSessionNumm ? "" : getParaValue(UnifyParaID.W_MM_TypeB_CQI_Rate),	isSessionNumm ? "" : getParaValue(UnifyParaID.W_MM_TypeB_CQI_Mean)
                    };
            for (int i = 0,j = 5; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                if(i+1 <datas.length){
                    cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }
            }
			
			datas = new String[]{
					isSessionNumm ? "" : getParaValue(UnifyParaID.W_MM_Pre_TB_Rate_1),				isSessionNumm ? "" : getParaValue(UnifyParaID.W_MM_Pre_TB_Rate_2),
					isSessionNumm ? "" : getParaValue(UnifyParaID.W_MM_TransBlock_Size_Max_1),		isSessionNumm ? "" : getParaValue(UnifyParaID.W_MM_TransBlock_Size_Max_2),
					isSessionNumm ? "" : getParaValue(UnifyParaID.W_MM_DSCH_BLER_Re_Rate_1),	isSessionNumm ? "" : getParaValue(UnifyParaID.W_MM_DSCH_BLER_Re_Rate_2),
					isSessionNumm ? "" : getParaValue(UnifyParaID.W_MM_DSCH_BLER_Fst_Rate_1),	isSessionNumm ? "" : getParaValue(UnifyParaID.W_MM_DSCH_BLER_Fst_Rate_2),
					isSessionNumm ? "" : getParaValue(UnifyParaID.W_MM_DSCH_ACK_Rate_1),		isSessionNumm ? "" : getParaValue(UnifyParaID.W_MM_DSCH_ACK_Rate_2),
			        /*model.getHs_mimo_dsch_nack_rate(),model.getHs_mimo_dsch_nack_rate_2(),*/
					isSessionNumm ? "" : getParaValue(UnifyParaID.W_MM_16QAM_Rate_1),			isSessionNumm ? "" : getParaValue(UnifyParaID.W_MM_16QAM_Rate_2),
					isSessionNumm ? "" : getParaValue(UnifyParaID.W_MM_64QAM_Rate_1),			isSessionNumm ? "" : getParaValue(UnifyParaID.W_MM_64QAM_Rate_2),
					isSessionNumm ? "" : getParaValue(UnifyParaID.W_MM_QPSK_Rate_1),			isSessionNumm ? "" :getParaValue(UnifyParaID.W_MM_QPSK_Rate_2),
					isSessionNumm ? "" : UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.W_MM_Phys_Schedule_Thr_1)),	isSessionNumm ? "" : UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.W_MM_Phys_Schedule_Thr_2)),
					isSessionNumm ? "" : UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.W_MM_Phys_Service_Thr_1)),	isSessionNumm ? "" : UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.W_MM_Phys_Service_Thr_2))
			        };
			for (int i = 0,j=9; i < datas.length; i+=2,j++) {
			    cv.drawText(datas[i], colsWidth * 2 + (colsWidth * 1 - paint.measureText(datas[i])) / 2, rowsHeight * j - rowUpBit, paramPaint);
			    cv.drawText(datas[i+1], colsWidth * 3 + (colsWidth * 1 - paint.measureText(datas[i+1])) / 2, rowsHeight * j - rowUpBit, paramPaint);
            }
			
		}else if(currentPage == 2){
			//Serving Cell System Info
			//HSDPA Dual Cell Info
//			HspaPlusModel model = traceData.getHspaPlusModel();
			String session=getParaValue(UnifyParaID.W_PA1_HSPAPlus_Session);
			boolean isSessionNumm = session.trim().equals("")|| session.equals("0");
					
			String[] datas = new String[]{isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_Dual_Serving_Cell_1), isSessionNumm? "" : getParaValue(UnifyParaID.W_DC_Dual_Serving_Cell_2),
					isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_Dual_Serving_UARFCN_1),			isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_Dual_Serving_UARFCN_2),
					isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_TransBlockCount_1),				isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_TransBlockCount_2),
					isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_TransBlockSize_1),				isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_TransBlockSize_2),
					isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_CQI_Mean_1),					isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_CQI_Mean_2),
					isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_DSCH_BLER_Re_Rate_1),	isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_DSCH_BLER_Re_Rate_2),
					isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_DSCH_BLER_Fst_Rate_1),	isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_DSCH_BLER_Fst_Rate_2),
					isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_DSCH_ACK_Rate_1),		isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_DSCH_ACK_Rate_2),
					isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_DSCH_DTX_Rate_1),		isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_DSCH_DTX_Rate_2),
					isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_DSCH_Retrans_Rate_1),	isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_DSCH_Retrans_Rate_2),
					isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_64QAM_Rate_1),		isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_64QAM_Rate_2),
					isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_16QAM_Rate_1),		isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_16QAM_Rate_2),
			        /*model.getHs_dual_cell_qpsk_rate(),model.getHs_dual_cell_qpsk_rate_2(),*/
					isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_Decode_Success_Rate_1),			isSessionNumm ? "" : getParaValue(UnifyParaID.W_DC_Decode_Success_Rate_2),
					isSessionNumm ? "" : UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.W_DC_Phys_Request_Thr_1)),	isSessionNumm ? "" : UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.W_DC_Phys_Request_Thr_2)),
					isSessionNumm ? "" : UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.W_DC_Phys_Schedule_Thr_1)),isSessionNumm ? "" : UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.W_DC_Phys_Schedule_Thr_2)),
					isSessionNumm ? "" : UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.W_DC_Phys_Service_Thr_1)),	isSessionNumm ? "" : UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.W_DC_Phys_Service_Thr_2))
			        };
	        for (int i = 0,j=3; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], colsWidth * 2 + (colsWidth * 1 - paint.measureText(datas[i])) / 2, rowsHeight * j - rowUpBit, paramPaint);
                cv.drawText(datas[i+1], colsWidth * 3 + (colsWidth * 1 - paint.measureText(datas[i+1])) / 2, rowsHeight * j - rowUpBit, paramPaint);
            }
		}
		
		cv.save();
		cv.restore();
	}
	
	/**获得得参数队列中指定ID的值*/
    private String getParaValue(int paraId){
        return TraceInfoInterface.getParaValue(paraId);
    }

}
