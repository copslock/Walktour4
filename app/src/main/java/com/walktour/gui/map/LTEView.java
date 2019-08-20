package com.walktour.gui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UnifyStruct.LTEAPN;
import com.walktour.Utils.UnifyStruct.LTEEPSBearerContext02C1;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.UtilsMethodPara;
import com.walktour.Utils.WalktourConst;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.framework.view.BasicParamView;
import com.walktour.framework.view.CheckCellParamThread;
import com.walktour.framework.view.ViewSizeLinstener;
import com.walktour.gui.R;

import java.util.Locale;

/**
 * LTE视图类
 * @author li.bie
 *
 */
public class LTEView extends BasicParamView {
    
    private int currentPage = 1;
    
    int tableCols = 4; //列数
    
    int tableColsSec = 4; //第二页行数
    
    float strokeWidth = 1;
    
    private float rowsHeight;
    
    private float rowUpBit;
    
    private int viewHeight;
    
	private ViewSizeLinstener viewSizeLinstener;
    
	public LTEView(Context context) {
		super(context);
	}
	public LTEView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    public LTEView(Context context,int page) {
        super(context);
        currentPage = page;
	}
    
    public LTEView(Context context,int page,ViewSizeLinstener viewSizeLinstener) {
        super(context);
        currentPage = page;
        this.viewSizeLinstener = viewSizeLinstener;
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
		float colsWidth = this.getWidth()/tableCols;		//列宽
		float colsLqwidth = this.getWidth()/6;		//邻区第一列列宽
		float lableAddWidth = 0;	//标签列加宽
		float neiColsWith = (this.getWidth() - colsLqwidth) / 6;//邻区单元C*宽度
		rowUpBit = (rowsHeight - textSize)/2 ;		//指定行上升位数,为行高-字体高度 再除2
		String paraname;
		
		float secColsWidth = (this.getWidth() - marginSize)/3;		//列宽
		if(currentPage ==1){
		    tableRows = 19;
            if(viewHeight == 0){
                viewHeight = this.getViewHeight() - 1;
            }
		    rowsHeight = viewHeight / tableRows;
		    rowUpBit = (rowsHeight - textSize)/2 ;
		    
		    viewSizeLinstener.onViewSizeChange(viewHeight, this.getWidth());
		    
			//四周边框
			cv.drawLine(1, 1, this.getWidth()-1, 1, linePaint);
			//cv.drawLine(1, height-marginSize, width-marginSize, height-marginSize , paint);
			cv.drawLine(1, 1, 1, rowsHeight * (tableRows - 7), linePaint);
			cv.drawLine(this.getWidth()-1, 1, this.getWidth()-1, rowsHeight * (tableRows - 7), linePaint);
			//横线
			for(int i=0;i<tableRows - 7;i++){
				startx = 1;
				starty =  rowsHeight * (i+1);
				stopx = this.getWidth() - 1;
				stopy = rowsHeight *(i+1);
				cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
			
			//Serving cell竖线
            startx = this.getWidth() / 2;
            starty =  rowsHeight * 1;
            stopx = startx;
            stopy = rowsHeight * 9 ;
            cv.drawLine(startx, starty, stopx, stopy, linePaint);
            
			
			paraname = getContext().getString(R.string.lte_serving_cell_info_1);
            cv.drawText(paraname, (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
			
			paraname = getContext().getString(R.string.lte_mcc_mnc);
            cv.drawText(paraname, marginSize, rowsHeight * 2 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.lte_tmmode);
            cv.drawText(paraname, this.getWidth() /2 +marginSize, rowsHeight * 2 - rowUpBit, fontPaint);
			
            paraname = getContext().getString(R.string.lte_ulf);
			cv.drawText(paraname, marginSize, rowsHeight * 3 - rowUpBit, fontPaint);
			paraname = getContext().getString(R.string.lte_dlf);
			cv.drawText(paraname, this.getWidth() /2 +marginSize, rowsHeight * 3 - rowUpBit, fontPaint);
			
            paraname = getContext().getString(R.string.lte_workmode);
            cv.drawText(paraname, marginSize, rowsHeight * 4 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.lte_band);
            cv.drawText(paraname, this.getWidth() /2 +marginSize, rowsHeight * 4 - rowUpBit, fontPaint);
            
            paraname = getContext().getString(R.string.lte_earfcn);
            cv.drawText(paraname,marginSize, rowsHeight * 5 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.lte_pci);
			cv.drawText(paraname, this.getWidth() /2 +marginSize, rowsHeight * 5 - rowUpBit, fontPaint);
			
			
            paraname = getContext().getString(R.string.lte_rsrp);
            cv.drawText(paraname, marginSize, rowsHeight * 6 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.lte_rssi);
            cv.drawText(paraname, this.getWidth() /2 +marginSize, rowsHeight * 6 - rowUpBit, fontPaint);  
            
			
            paraname = getContext().getString(R.string.lte_rsrq);
            cv.drawText(paraname, marginSize, rowsHeight * 7 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.lte_sinr);
            cv.drawText(paraname, this.getWidth() /2 + marginSize, rowsHeight * 7 - rowUpBit, fontPaint);
            
            /*paraname = getContext().getString(R.string.lte_pathloss);
            cv.drawText(paraname, this.getWidth() /2 +marginSize, rowsHeight * 8 - rowUpBit, fontPaint);  
            paraname = getContext().getString(R.string.lte_srs_rb_num);
            cv.drawText(paraname, this.getWidth() /2 +marginSize, rowsHeight * 8 - rowUpBit, fontPaint);*/     			
			
            paraname = getContext().getString(R.string.lte_crs_sinr);
            cv.drawText(paraname, marginSize, rowsHeight * 8 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.lte_tac);
            cv.drawText(paraname, this.getWidth() /2 +marginSize, rowsHeight * 8 - rowUpBit, fontPaint);            
			
            /*paraname = getContext().getString(R.string.lte_drs_sinr);
            cv.drawText(paraname, marginSize, rowsHeight * 10 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.lte_srspower);
            cv.drawText(paraname, marginSize, rowsHeight * 10 - rowUpBit, fontPaint);*/
            
            paraname = getContext().getString(R.string.lte_ecgi);
            cv.drawText(paraname, marginSize, rowsHeight * 9 - rowUpBit, fontPaint);            
            paraname = getContext().getString(R.string.lte_network_state);
            cv.drawText(paraname, this.getWidth() /2 + marginSize, rowsHeight * 9 - rowUpBit, fontPaint);			

	        paraname = getContext().getString(R.string.lte_emm_state);
	        cv.drawText(paraname, marginSize, rowsHeight * 10 - rowUpBit, fontPaint);
	        
	        paraname = getContext().getString(R.string.lte_emm_substate);
	        cv.drawText(paraname, marginSize, rowsHeight * 11 - rowUpBit, fontPaint);
	        
            paraname = getContext().getString(R.string.wcdma_cell_name);
            cv.drawText(paraname, marginSize, rowsHeight * 12 - rowUpBit, fontPaint);

		}else if(currentPage == 2){
		    tableRows = 21;
			rowsHeight =( this.getHeight() - 1 )/tableRows;	//行高
			rowUpBit = (rowsHeight - textSize)/2 ;
            //四周边框
            cv.drawLine(startx, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(startx, (tableRows + 1)*rowsHeight , this.getWidth()-1, (tableRows + 1)*rowsHeight  , linePaint);
            cv.drawLine(startx, 1, startx, (tableRows + 1)*rowsHeight , linePaint);
            cv.drawLine(this.getWidth()-1, startx, this.getWidth()-1, (tableRows + 1)*rowsHeight , linePaint);
            
			//横线
			for(int i=0;i<tableRows + 1;i++){
				startx = 0;
				starty =  rowsHeight * (i+1);
				stopx = this.getWidth() - 1;
				stopy = rowsHeight *(i+1);
				cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
			

             for(int i=0;i<tableCols - 1;i++){
                    startx = colsWidth * (i+1) + (i%2==0 ? lableAddWidth:0);
                    starty = rowsHeight * 0;
                    stopx = colsWidth * (i+1) + (i%2==0 ? lableAddWidth:0);
                    stopy = rowsHeight * 5 ;
                    cv.drawLine(startx, starty, stopx, stopy, linePaint);
             }
             
			for(int i=0;i<2;i++){
				startx = secColsWidth+secColsWidth*i;
				starty =  rowsHeight * 6;
				stopx = startx;
				stopy = rowsHeight * 13;
				cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
			
			//
            startx = this.getWidth()/2;
            starty =  rowsHeight * 14;
            stopx = startx;
            stopy = rowsHeight * 22;
            cv.drawLine(startx, starty, stopx, stopy, linePaint);
			

			//DownLink Trch Bler Meas.Info
            //paraname = getContext().getString(R.string.lte_modulation_2);
            //cv.drawText(paraname, (this.getWidth()-fontPaint.measureText(paraname))/2, rowsHeight * 1 - rowUpBit, fontPaint);
            
			paraname = getContext().getString(R.string.lte_ul);
			cv.drawText(paraname, colsWidth * 1 + (colsWidth  - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
			paraname = getContext().getString(R.string.lte_dl0);
			cv.drawText(paraname, colsWidth * 2 + (colsWidth  - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
			paraname = getContext().getString(R.string.lte_dl1);
			cv.drawText(paraname, colsWidth * 3 + (colsWidth  - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
		 
			int values[] = new int[]{R.string.lte_qpsk,R.string.lte_16qam,
                    R.string.lte_64qam,R.string.lte_pdsch_bler};	//R.string.lte_mcs,
			for (int i = 0,j = 2; i < values.length; i++,j++) {
			    cv.drawText(getResources().getString(values[i]), colsWidth * 0 + (colsWidth  - fontPaint.measureText(getResources().getString(values[i]))) / 2, rowsHeight * j - rowUpBit, fontPaint);
            }
			
			paraname = getContext().getString(R.string.lte_throughput);
            cv.drawText(paraname, (this.getWidth()-fontPaint.measureText(paraname))/2, rowsHeight * 6 - rowUpBit, fontPaint);
            
			paraname = getContext().getString(R.string.lte_ul);
			cv.drawText(paraname, secColsWidth * 1 + (secColsWidth  - fontPaint.measureText(paraname)) / 2, rowsHeight * 7 - rowUpBit, fontPaint);
			paraname = getContext().getString(R.string.lte_dl);
			cv.drawText(paraname, secColsWidth * 2 + (secColsWidth  - fontPaint.measureText(paraname)) / 2, rowsHeight * 7 - rowUpBit, fontPaint);
			
			values = new int[]{ R.string.lte_pdcp,
			                    R.string.lte_rlc,
			                    R.string.lte_mac,
			                    R.string.lte_phy,
			                    R.string.lte_phydl0,
			                    R.string.lte_phydl1};
			for (int i = 0,j = 8; i < values.length; i++,j++) {
			    cv.drawText(getContext().getString(values[i]), secColsWidth * 0 + (secColsWidth - fontPaint.measureText(getContext().getString(values[i]))) / 2, rowsHeight * j - rowUpBit, fontPaint);
            }
			
			paraname = getContext().getString(R.string.lte_channelmeasurement);
            cv.drawText(paraname, (this.getWidth()-fontPaint.measureText(paraname))/2, rowsHeight * 14 - rowUpBit, fontPaint);
            
			values = new int[]{R.string.lte_pusch_initial_eler,R.string.lte_pdsch_bler,
			        R.string.lte_pusch_residual_bler,R.string.lte_pdcch_estimated_bler,
			        R.string.lte_puschtxpower,R.string.lte_pucchtxpower,
			        R.string.lte_wideband_cqi_for_cw0,R.string.lte_wideband_cqi_for_cw1,
			        R.string.lte_cqi_report_mode,R.string.lte_frame_number,
			        R.string.lte_pdcchdlgrant,R.string.lte_pdschrbcount,
			        R.string.lte_pdcchulgrant,R.string.lte_puschrbcount};
            for (int i = 0,j = 15; i < values.length; i+=2,j++) {
                paraname = getContext().getString(values[i]);
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <values.length){
                    paraname = getContext().getString(values[i+1]);
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
            }

		}else if(currentPage ==3){
            tableRows = 7;
            rowsHeight = this.getViewHeight() /tableRows;   //行高
            rowUpBit = (rowsHeight - textSize)/2 ;
            //四周边框
            cv.drawLine(startx, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(startx, (tableRows-1)*rowsHeight , this.getWidth()-1, (tableRows-1)*rowsHeight  , linePaint);
            cv.drawLine(startx, 1, startx, (tableRows-1)*rowsHeight , linePaint);
            cv.drawLine(this.getWidth()-1, startx, this.getWidth()-1, (tableRows-1)*rowsHeight , linePaint);
            
            //横线
            for(int i=0;i<(tableRows-1);i++){
                startx = 1;
                starty =  rowsHeight * (i+1);
                stopx = this.getWidth() - 1;
                stopy = rowsHeight *(i+1);
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
            }
		    
            //Neighbor Cell 第一竖线
            {
                startx = colsLqwidth ;
                starty =  rowsHeight*1;
                stopx = colsLqwidth ;
                stopy = rowsHeight * (tableRows-1) ;
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
            }
            
            //Neighbor Cell竖线
            for(int i=0;i<5;i++){
                startx = colsLqwidth + neiColsWith * (i+1);
                starty =  rowsHeight*1;
                stopx = startx;
                stopy = rowsHeight * (tableRows-1) ;
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
            }
            
            //neighbor cell info
            paraname = getContext().getString(R.string.gsm_neighborCell);//"Serving Cell Info";
            cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
            
  
            
            int values[] = new int[]{R.string.lte_earfcn,R.string.lte_pci,R.string.lte_rsrp,
                    R.string.lte_rsrq,R.string.lte_rssi};
            for (int i = 0,j = 2; i < values.length; i++,j++) {
                cv.drawText(getResources().getString(values[i]), colsLqwidth * 0 + (colsLqwidth - fontPaint.measureText(getResources().getString(values[i]))) / 2, rowsHeight * j - rowUpBit, fontPaint);
            }
		    
		}else if(currentPage ==4) {
		    
            tableRows = 19;
            rowsHeight = (this.getViewHeight() - 1) / tableRows; //行高
            rowUpBit = (rowsHeight - textSize) / 2 ;
            float thirdColmWidth = (this.getWidth() - 1) * 3 / 8;       //列宽
            
            //四周边框
            cv.drawLine(startx, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(startx,  rowsHeight * (tableRows - 3 ), this.getWidth()-1, rowsHeight * (tableRows - 3) , linePaint);
            cv.drawLine(startx, 1, startx,  rowsHeight * (tableRows - 3), linePaint);
            cv.drawLine(this.getWidth()-1, startx, this.getWidth()-1, rowsHeight * (tableRows - 3), linePaint);
 
            //画竖线
            startx = this.getWidth() / 2;
            starty =  rowsHeight * 1;
            stopx = startx;
            stopy = rowsHeight * 14 ;
            cv.drawLine(startx, starty, stopx, stopy, linePaint);

            //画横线
            for(int i = 0;i<tableRows - 3; i++){
                startx = 0;
                starty =  rowsHeight * (i+1);
                stopx = this.getWidth() - 1;
                stopy = rowsHeight *(i+1);
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
            }

            paraname = getContext().getString(R.string.lte_system_parameter_4);
            cv.drawText(paraname, thirdColmWidth * 0 + (this.getWidth()  - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
            
            int values[] = new int[]{R.string.lte_ue_category,R.string.lte_maxUe_Power,
                    R.string.lte_qoffset_cell,R.string.lte_drx_state,
                    R.string.lte_qoffset_freq,R.string.lte_nas_state,
                    R.string.lte_qrxlevmin,R.string.lte_power_headroom,
                    R.string.lte_qrxlevminoffset,R.string.lte_uu_ta,
                    R.string.lte_intrafreq_reselection,R.string.lte_mmec,
                    R.string.lte_mmegi,										//R.string.lte_eutra_carrier_freq,
                    R.string.lte_thresholdx_high,R.string.lte_imsi,
                    R.string.lte_thresholdx_low,R.string.lte_mtmsi,
                    R.string.lte_threshold_serving_low,R.string.lte_crnti,
                    R.string.lte_treselection_eutra,R.string.lte_tcrnti,
                    R.string.lte_cell_reselect_priority,R.string.lte_pdn_address,
                    R.string.lte_qhyst,R.string.lte_QCI,
                    //R.string.lte_allowed_meas_bandwidth,R.string.lte_apn
                    };
            for (int i = 0,j = 2; i < values.length; i+=2,j++) {
                paraname = getContext().getString(values[i]);
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <values.length){
                    paraname = getContext().getString(values[i+1]);
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
            }
            cv.drawText(getResources().getString(R.string.lte_allowed_meas_bandwidth), marginSize , rowsHeight * 15 - rowUpBit, fontPaint);
            cv.drawText(getResources().getString(R.string.lte_apn), marginSize , rowsHeight * 16 - rowUpBit, fontPaint);
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
		float colsWidth = this.getWidth()/tableCols;		//列宽
		float colsLqwidth = (this.getWidth() - marginSize)/6;		//邻区第一列列宽
		
		float neiColsWith = (this.getWidth() - colsLqwidth - marginSize) / 6;//邻近单元C*宽度
		if(currentPage ==1){
			if (!StringUtil.isNullOrEmpty(getParaValue(UnifyParaID.L_SRV_EARFCN))
					&& !StringUtil.isNullOrEmpty(getParaValue(UnifyParaID.L_SRV_PCI))) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("eafrcn = '").append(getParaValue(UnifyParaID.L_SRV_EARFCN)).append("'");
				buffer.append(" and pci = '").append(getParaValue(UnifyParaID.L_SRV_PCI)).append("'");
				new CheckCellParamThread(this.getContext(),new String[] { "eafrcn", "pci", "cellName", "cellId", "longitude", "latitude" },
						buffer.toString(), WalktourConst.NetWork.LTE).start();
			}
			StringBuilder cellKey = new StringBuilder();
			cellKey.append(WalktourConst.NetWork.LTE);
      cellKey.append("_eafrcn_").append(getParaValue(UnifyParaID.L_SRV_EARFCN));
      cellKey.append("_pci_").append(getParaValue(UnifyParaID.L_SRV_PCI));
			String cellnameString = traceData.getNetworkCellInfo(cellKey.toString()) == null ? "" : traceData.getNetworkCellInfo(cellKey.toString()).getCellName();
	          cv.drawText(cellnameString, this.getWidth() -paramPaint.measureText(cellnameString) - marginSize, rowsHeight * 12 - rowUpBit, paramPaint);
            
            String eci = 
            		getParaValue(UnifyParaID.L_SRV_ECIP1) + "(" +
            		getParaValue(UnifyParaID.L_SRV_ECIP2) + "-" +
            		getParaValue(UnifyParaID.L_SRV_ECIP3) + ")";
		    
		    String[] datas = new String[]{getParaValue(UnifyParaID.L_SRV_MCC).equals("") && getParaValue(UnifyParaID.L_SRV_MNC).equals("")?"":getParaValue(UnifyParaID.L_SRV_MCC) + "/" + getParaValue(UnifyParaID.L_SRV_MNC),UtilsMethodPara.getLteTM(getParaValue(UnifyParaID.L_SRV_TM)),
		    		getParaValue(UnifyParaID.L_SRV_UL_Freq).equals("") ? "" :getParaValue(UnifyParaID.L_SRV_UL_Freq)+"/"+ getParaValue(UnifyParaID.L_SRV_DL_Freq),getParaValue(UnifyParaID.LTECA_UL_BandWidth).equals("")? "" : getParaValue(UnifyParaID.LTECA_UL_BandWidth) + "/" + getParaValue(UnifyParaID.L_SRV_DL_BandWidth),
		            UtilsMethodPara.getLteWorkModel(getParaValue(UnifyParaID.L_SRV_Work_Mode)),getParaValue(UnifyParaID.L_SRV_Band),
		            getParaValue(UnifyParaID.L_SRV_EARFCN),getParaValue(UnifyParaID.L_SRV_PCI),
		            getParaValue(UnifyParaID.L_SRV_RSRP),getParaValue(UnifyParaID.L_SRV_RSSI),
		            getParaValue(UnifyParaID.L_SRV_RSRQ),getParaValue(UnifyParaID.L_SRV_SINR),
		            //model.getPathloss(),model.getSrsRbNum(),
		            getParaValue(UnifyParaID.L_SRV_CRS_SINR),getParaValue(UnifyParaID.L_SRV_TAC),
		            //model.getDrsSinr(),model.getSrsPower(),   //RRC State   0: Idle  1: Connected 或者叫Dedicated 
		            eci,UtilsMethodPara.getLteRRCState(getParaValue(UnifyParaID.CURRENT_STATE_LTE)),
		            "",UtilsMethodPara.getLteEMMState(getParaValue(UnifyParaID.L_SRV_EMM_State)),		//此处 “”用于占位，让值靠后
		            "",UtilsMethodPara.getLteEMMSubState(getParaValue(UnifyParaID.L_SRV_EMM_Substate))	//此处 “”用于占位，让值靠后
		            };
		    
            for (int i = 0,j = 2; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                if(i+1 <datas.length){
                    cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }
            }
		}else if(currentPage == 2){
			boolean puschTxpNull = getParaValue(UnifyParaID.L_CH1_PUSCH_TxPower).equals("");
			float secColsWidth = (this.getWidth() - marginSize)/3;		//列宽
			String[] datas = new String[]{
					UtilsMethod.addUnit(puschTxpNull ? "" : getParaValue(UnifyParaID.LTE_QPSK_Ratio_UL),"%"),
					UtilsMethod.addUnit(puschTxpNull ? "" : getParaValue(UnifyParaID.LTE_QPSK_Ratio_code0_DL),"%"),
					UtilsMethod.addUnit(puschTxpNull ? "" : getParaValue(UnifyParaID.LTE_QPSK_Ratio_code1_DL),"%"),
					UtilsMethod.addUnit(puschTxpNull ? "" : getParaValue(UnifyParaID.LTE_16QAM_Ratio_UL),"%"),
					UtilsMethod.addUnit(puschTxpNull ? "" : getParaValue(UnifyParaID.LTE_16QAM_Ratio_code0_DL),"%"),
					UtilsMethod.addUnit(puschTxpNull ? "" : getParaValue(UnifyParaID.LTE_16QAM_Ratio_code1_DL),"%"),
					UtilsMethod.addUnit(puschTxpNull ? "" : getParaValue(UnifyParaID.LTE_64QAM_Ratio_UL),"%"),
					UtilsMethod.addUnit(puschTxpNull ? "" : getParaValue(UnifyParaID.LTE_64QAM_Ratio_code0_DL),"%"),
					UtilsMethod.addUnit(puschTxpNull ? "" : getParaValue(UnifyParaID.LTE_64QAM_Ratio_code1_DL),"%"),
			        "-",
			        UtilsMethod.addUnit(puschTxpNull ? "" : getParaValue(UnifyParaID.LTE_DL_PDSCH_BLER_Code0),"%"),
			        UtilsMethod.addUnit(puschTxpNull ? "" : getParaValue(UnifyParaID.LTE_DL_PDSCH_BLER_Code1),"%")};
            for (int i = 0,j = 2; i < datas.length; i+=3,j++) {
                cv.drawText(datas[i], colsWidth * 1 + (colsWidth  - paramPaint.measureText(datas[i])) / 2, rowsHeight * j - rowUpBit, paramPaint);
                cv.drawText(datas[i+1], colsWidth * 2 + (colsWidth  - paramPaint.measureText(datas[i+1])) / 2, rowsHeight * j - rowUpBit, paramPaint);
                cv.drawText(datas[i+2], colsWidth * 3 + (colsWidth  - paramPaint.measureText(datas[i+2])) / 2, rowsHeight * j - rowUpBit, paramPaint);
            }
            
            datas = new String[]{UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.L_Thr_UL_PDCP_Thr)),UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.L_Thr_DL_PDCP_Thr)),
                    UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.L_Thr_UL_RLC_Thr)),UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.L_Thr_DL_RLC_Thr)),
                    UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.L_Thr_UL_MAC_Thr)),UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.L_Thr_DL_MAC_Thr)),
                    UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.L_Thr_UL_Phy_Thr)),UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.L_Thr_DL_Phy_Thr)),
                    "-",UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.L_Thr_DL_PhyThrCode0)),
                    "-",UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.L_Thr_DL_PhyThrCode1))};
            for (int i = 0,j = 8; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], secColsWidth * 1 + (secColsWidth  - paramPaint.measureText(datas[i])) / 2, rowsHeight * j - rowUpBit, paramPaint);
                cv.drawText(datas[i+1], secColsWidth * 2 + (secColsWidth  - paramPaint.measureText(datas[i+1])) / 2, rowsHeight * j - rowUpBit, paramPaint);
            }
            
            
            datas = new String[]{(puschTxpNull ? "" : getParaValue(UnifyParaID.L_CH2_PUSCH_Initial_BLER)),(puschTxpNull ? "" : getParaValue(UnifyParaID.L_CH2_PDSCH_BLER)),
                    (puschTxpNull ? "" : getParaValue(UnifyParaID.L_CH2_PUSCH_Residual_BLER)),(puschTxpNull ? "" : getParaValue(UnifyParaID.L_CH2_PDCCH_Estimated_BLER)),
            		getParaValue(UnifyParaID.L_CH1_PUSCH_TxPower),getParaValue(UnifyParaID.L_CH1_PUCCH_TxPower),
            		getParaValue(UnifyParaID.L_CH2_Wideband_CQI_for_CW0),getParaValue(UnifyParaID.L_CH2_Wideband_CQI_for_CW1),
                    (puschTxpNull ? "" : UtilsMethodPara.getLteCQIReportMode(getParaValue(UnifyParaID.L_CH2_CQI_Report_Mode))),(puschTxpNull ? "" : getParaValue(UnifyParaID.L_CH2_Frame_Number)),
                    (puschTxpNull ? "" : getParaValue(UnifyParaID.L_CH1_PDCCH_DL_Grant_Count)),(puschTxpNull ? "" : getParaValue(UnifyParaID.L_DL_PDSCH_RB_Count)),
                    (puschTxpNull ? "" : getParaValue(UnifyParaID.L_CH1_PDCCH_UL_Grant_Count)),(puschTxpNull ? "" : getParaValue(UnifyParaID.L_UL_PUSCH_RB_Count))
                    };
                    
            for (int i = 0,j = 15; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                if(i+1 <datas.length){
                    cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }
            }         

		}else if(currentPage == 3){
			String[] servingNeighbor = getParaValue(UnifyParaID.LTE_CELL_LIST).split(";");
			int subAct = 0;
			String value = "";
			for (int i = 0; servingNeighbor != null && i < servingNeighbor.length - 1; i++) {
				/**
				 * valueStr的值：0,	-10688,			103,		 -68.000000,		  -5.267518,		  -73.267517
				 * 0: Earfcn;	1:Pci;	2:Rsrp(/100);	3:Rsrq(/100);	4:Rssi(/100)
				 */
                String[] neighbor = servingNeighbor[i+1].split(",");
                //当邻区列表中的EARFCN,PCI与主服务小区的值相等时，此处不显示该值
                if(neighbor[0].equals(getParaValue(UnifyParaID.L_SRV_EARFCN)) 
                		&& neighbor[1].equals(getParaValue(UnifyParaID.L_SRV_PCI))){
                	subAct ++;
                	continue;
                }
                
                //去掉缩放比例
                value = neighbor[0];	//Earfcn
                cv.drawText(value, colsLqwidth + neiColsWith * (i - subAct) + (neiColsWith - paramPaint.measureText(value) ) / 2  , rowsHeight*2 - rowUpBit, paramPaint);
                value = neighbor[1];	//PCI
                cv.drawText(value, colsLqwidth + neiColsWith * (i - subAct) + (neiColsWith - paramPaint.measureText(value) ) / 2  , rowsHeight*3 - rowUpBit, paramPaint);
                value = neighbor[2]; 	//getRsrpByRsrqAndRssi(neighbor);
                cv.drawText(value, colsLqwidth + neiColsWith * (i - subAct) + (neiColsWith - paramPaint.measureText(value) ) / 2  , rowsHeight*4 - rowUpBit, paramPaint);
                value = neighbor[3];	//RSRQ
                cv.drawText(value, colsLqwidth + neiColsWith * (i - subAct) + (neiColsWith - paramPaint.measureText(value) ) / 2  , rowsHeight*5 - rowUpBit, paramPaint);
                value = neighbor[4]; 	//RSSI
                cv.drawText(value, colsLqwidth + neiColsWith * (i - subAct) + (neiColsWith - paramPaint.measureText(value) ) / 2  , rowsHeight*6 - rowUpBit, paramPaint);
            }
		}else if(currentPage == 4){
			LTEEPSBearerContext02C1 pdnAddress = ((LTEEPSBearerContext02C1)TraceInfoInterface.getParaStruct(UnifyParaID.LTE_EPS_BearerContext_02C1));
			//String[] lteApn	= getParaValue(UnifyParaID.LTE_APN).split(","); APN结构只有一个参数，直接取即可
		    String[] datas = new String[]{getParaValue(UnifyParaID.L_SYS_UE_Category),getParaValue(UnifyParaID.L_SYS_Max_UE_TxPower),
		    		getParaValue(UnifyParaID.L_SYS_Q_Offset_Cell),getParaValue(UnifyParaID.L_SYS_DRX_State),
		    		getParaValue(UnifyParaID.L_SYS_Q_Offset_Freq),UtilsMethodPara.getLteNasState(getParaValue(UnifyParaID.L_SYS_NAS_State)),
		    		getParaValue(UnifyParaID.L_SYS_Q_Rxlevmin),getParaValue(UnifyParaID.L_SYS_Power_Headroom),
		    		getParaValue(UnifyParaID.L_SYS_Q_RxlevminOffset),getParaValue(UnifyParaID.L_SYS_Uu_TA),
		              UtilsMethodPara.getLteIntraFreqReselection(getParaValue(UnifyParaID.L_SYS_IntraFreq_Reselection)),getParaValue(UnifyParaID.L_SYS_MMEC),
		              getParaValue(UnifyParaID.L_SYS_MMEGI),	//getParaValue(UnifyParaID.L_SYS_E_UTRA_Carrier_Freq),
		              getParaValue(UnifyParaID.L_SYS_ThresholdX_High),(MyPhoneState.getInstance().getIMSI(getContext()) == null ? "" : MyPhoneState.getInstance().getIMSI(getContext())),//traceData.getPhoneState().getCurrentNetType() != InfoPhoneState.PhoneStateLte ? "" :
		              getParaValue(UnifyParaID.L_SYS_ThresholdX_Low),mTmsiToHex(getParaValue(UnifyParaID.L_SYS_M_TMSI)),
		              getParaValue(UnifyParaID.L_SYS_Threshold_Serving_Low),getParaValue(UnifyParaID.L_SYS_C_RNTI),
		              getParaValue(UnifyParaID.L_SYS_Treselection_EUTRA),getParaValue(UnifyParaID.L_SYS_T_CRNTI),
		              getParaValue(UnifyParaID.L_SYS_Cell_Reselect_Priority),(pdnAddress != null ? pdnAddress.pndAddress : ""),
		              getParaValue(UnifyParaID.L_SYS_Q_Hyst),UtilsMethodPara.getLteQCI(getParaValue(UnifyParaID.L_SYS_QCI)),
		              //getParaValue(UnifyParaID.L_SYS_Allowed_Meas_Bandwidth),getParaValue(UnifyParaID.LTE_APN)
		              };
		              
		    for (int i = 0,j = 2; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                if(i+1 <datas.length){
                    cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }
            }
		    String valuestr = getParaValue(UnifyParaID.L_SYS_Allowed_Meas_Bandwidth);
		    cv.drawText(valuestr, this.getWidth() -paramPaint.measureText(valuestr) -marginSize, rowsHeight * 15 - rowUpBit, paramPaint);
		    
		    LTEAPN lteapn = ((LTEAPN)TraceInfoInterface.getParaStruct(UnifyParaID.LTE_APN));
		    if(lteapn != null){
		    	valuestr = lteapn.lteApn;
		    }else{
		    	valuestr = "";
		    }
		    cv.drawText(valuestr, this.getWidth() -paramPaint.measureText(valuestr) -marginSize, rowsHeight * 16 - rowUpBit, paramPaint);
		}
		else if(currentPage == 5){
		    rowsHeight = this.getViewHeight() / 7;
		    
		    String[] servingNeighbor = getParaValue(UnifyParaID.LTE_CELL_LIST).split(";");
		    float[] values = new float[servingNeighbor.length - 1];
            String[] params = new String[servingNeighbor.length - 1];
            float[] percentages = new float[servingNeighbor.length - 1];
            
            for(int i = 0; servingNeighbor != null && i < servingNeighbor.length - 1; i++){
            	String[] neighbor = servingNeighbor[i+1].split(",");
            	values[i] = Float.valueOf(UtilsMethod.transferByScale(neighbor[3],100));
            	params[i] = (i == 0 && getParaValue(UnifyParaID.L_SRV_RSRQ).equals(UtilsMethod.transferByScale(neighbor[3],100)) 
            					? "Srv" : "N" + (i+1));
            	percentages[i] = 100 - ((values[i]/-40) * 100) + 6/40 * 100;
            }
            
            super.createCellHistogram("RSRQ(dB)", params, values, percentages, (int)rowsHeight * 6, cv);
		}
		
		cv.save();
		cv.restore();
	}
	

	
	private String mTmsiToHex(String mTmsi){
	    String mTmsiHex = "";
	    try{
	        if(!mTmsi.trim().equals("")){
	            mTmsiHex = "0x"+Integer.toHexString(Integer.parseInt(mTmsi.trim())).toUpperCase(Locale.getDefault());
	        }
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    return mTmsiHex;
	}
	
		
	
	/**
	 * 创建柱状图
	 * @param bm 要创建柱状图的位图
	 * @return   输出位图
	 */
	protected Bitmap CreateColumChart(Bitmap bm,TraceInfoData traceData){
		return bm;
	}

	/**获得得参数队列中指定ID的值*/
    private String getParaValue(int paraId){
        return TraceInfoInterface.getParaValue(paraId);
    }
    
    /**
     * @param viewHeight the viewHeight to set
     */
    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
        invalidate();
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
