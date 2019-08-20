package com.walktour.gui.map;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UnifyStruct;
import com.walktour.Utils.UnifyStruct.TDPhysChannelInfoDataV2;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.UtilsMethodPara;
import com.walktour.Utils.WalktourConst;
import com.walktour.framework.view.BasicParamView;
import com.walktour.framework.view.CheckCellParamThread;
import com.walktour.framework.view.ViewSizeLinstener;
import com.walktour.gui.R;
import com.walktour.model.CellInfo;

/**
 * TD-SCDMA根据当前解码实体类的信息以图形的形式呈现
 * 当收到信令改变消息后，当前重新获得TD实体信息重画界面并显示
 * @author tangwq
 * @version 1.0
 */
public class TdScdmaView extends BasicParamView {
    
    private int currentPage = 1;
    
    float rowsHeight;
    
    float rowUpBit;
    
	int tableCols = 6;		//列数
    
	int tableColsSec=4;		//第二页行数
	
	private Paint tlPaint;
	
    private ViewSizeLinstener viewSizeLinstener;
    
    private int viewHeight;
	
    private StringBuffer buffer = null; 
    
    private StringBuffer buffer2 = null;
    
	public TdScdmaView(Context context) {
		super(context);
	}
	public TdScdmaView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
    public TdScdmaView(Context context,int page) {
        super(context);
        this.currentPage =  page;
    }
    
    public TdScdmaView(Context context,int page,ViewSizeLinstener viewSizeLinstener) {
        super(context);
        this.currentPage =  page;
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
    	tlPaint = new Paint();
    	tlPaint.setAntiAlias(true);
    	tlPaint.setStyle(Paint.Style.FILL);   
    	tlPaint.setTypeface(null);
    	tlPaint.setTextSize(textSize);

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
		float neiColsWith = (this.getWidth() - colsWidth - marginSize) / tableCols;//邻近单元C*宽度
		
		rowUpBit = (rowsHeight - textSize)/2 ;		//指定行上升位数,为行高-字体高度 再除2
		String paraname;
		
		if(currentPage == 1){
    		
            if(viewHeight == 0){
                viewHeight = this.getViewHeight() - 1;
            }
            viewSizeLinstener.onViewSizeChange(viewHeight, this.getWidth());
            tableRows = 19;
    		rowsHeight = viewHeight /tableRows;
    		rowUpBit = (rowsHeight - textSize)/2 ;
    		tableRows = 13;
    		
    		//四周边框
    		cv.drawLine(startx, 1, this.getWidth()-1, 1, linePaint);
    		cv.drawLine(startx,  rowsHeight * tableRows, this.getWidth()-1, rowsHeight * tableRows , linePaint);
    		cv.drawLine(startx, 1, startx,  rowsHeight * tableRows, linePaint);
    		cv.drawLine(this.getWidth()-1, startx, this.getWidth()-1, rowsHeight * tableRows, linePaint);
    		
    		//横线
    		for(int i=0;i<tableRows ;i++){
    			startx = 1;
    			starty =  rowsHeight * (i+1);
    			stopx = this.getWidth() - 1;
    			stopy = rowsHeight *(i+1);
    			cv.drawLine(startx, starty, stopx, stopy, linePaint);
    		}
    		
    		//TD-SCDMA Radio 中间线
    		cv.drawLine(this.getWidth() /2 , rowsHeight*1, this.getWidth() /2, rowsHeight*12, linePaint);
    		
    		//DownLink Trch Bler Meas.Info
    		paraname = getContext().getString(R.string.gsm_servingCell);
    		cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
    		
    		
    		int[] values = new int[]{R.string.tdscdma_mcc_mnc,R.string.tdscdma_lac,
    		        R.string.tdscdma_uarfcn,R.string.tdscdma_cellids,
    		        R.string.tdscdma_dchuarfcn,R.string.tdscdma_rncid,
    		        R.string.tdscdma_cpi,R.string.tdscdma_ura_id,
    		        R.string.tdscdma_carrierRSSI,R.string.tdscdma_uppch_txpower,
    		        R.string.tdscdma_pccpchrscp,R.string.tdscdma_dpchrscp,
    		        R.string.tdscdma_pccpchiscp,R.string.tdscdma_dpchiscp,
    		        R.string.tdscdma_pccpchci,R.string.tdscdma_dpchci,
    		        R.string.tdscdma_pccpch_sir,R.string.tdscdma_ueTxPower,
    		        R.string.tdscdma_pccpch_pathloss,R.string.tdscdma_ta,
    		        R.string.tdscdma_bler,R.string.tdscdma_rac
    		        };
    		
            for (int i = 0,j = 2; i < values.length; i+=2,j++) {
                paraname = getContext().getString(values[i]);
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <values.length){
                    paraname = getContext().getString(values[i+1]);
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
            }
            
            paraname = getContext().getString(R.string.wcdma_cell_name);
            cv.drawText(paraname, marginSize , rowsHeight * 13 - rowUpBit, fontPaint);
		
		}else if(currentPage  == 2){
		    tableRows = 9;
		    rowsHeight = this.getViewHeight() / tableRows;
		    rowUpBit = (rowsHeight - textSize)/2 ;
		     //四周边框
	        cv.drawLine(startx, 1, this.getWidth()-1, 1, linePaint);
	        cv.drawLine(startx,  rowsHeight * (tableRows -1 ), this.getWidth()-1, rowsHeight * (tableRows -1) , linePaint);
	        cv.drawLine(startx, 1, startx,  rowsHeight * (tableRows -1), linePaint);
	        cv.drawLine(this.getWidth()-1, startx, this.getWidth()-1, rowsHeight * (tableRows -1), linePaint);
	        
	        //横线
	        for(int i=0;i<tableRows - 1;i++){
	            startx = 1;
	            starty =  rowsHeight * (i+1);
	            stopx = this.getWidth() - 1;
	            stopy = rowsHeight *(i+1);
	            cv.drawLine(startx, starty, stopx, stopy, linePaint);
	        }
		      //Neighbor Cell 第一竖线
	        {
	            startx = colsWidth ;
	            starty =  rowsHeight*1;
	            stopx = colsWidth ;
	            stopy = rowsHeight * (tableRows -1) ;
	            cv.drawLine(startx, starty, stopx, stopy, linePaint);
	        }
	        //Neighbor Cell竖线
	        for(int i=0;i<tableCols - 1;i++){
	            startx = colsWidth + neiColsWith * (i+1);
	            starty =  rowsHeight * 1;
	            stopx = colsWidth + neiColsWith* (i+1);
	            stopy = rowsHeight * (tableRows-1 );
	            cv.drawLine(startx, starty, stopx, stopy, linePaint);
	        }
		    
		      //neighbor cell info
	        paraname = getContext().getString(R.string.gsm_neighborCell);//"Serving Cell Info";
	        cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
	        
	        int values[] = new int[]{R.string.tdscdma_uarfcn,R.string.tdscdma_cpi,
	                R.string.tdscdma_rscp, R.string.wcdma_cell_id,R.string.tdscdma_ncarrierRSSI,
	                R.string.tdscdma_pathloss,R.string.tdscdma_rn};
	        for (int i = 0,j = 2; i < values.length; i++,j++) {
	            cv.drawText(getResources().getString(values[i]), colsWidth * 0 + (colsWidth - fontPaint.measureText(getResources().getString(values[i]))) / 2, rowsHeight * j - rowUpBit, fontPaint); 
            }
	        
		}else if(currentPage == 3){
			
		    tableRows = 19;
		    int dpchCols = 5 ;  //DPCH显示参数列5
		    int dpchColsWidth = (int) (this.getWidth() - (colsWidth * 0.5)) / dpchCols ;
            rowsHeight = this.getViewHeight() / tableRows;
            rowUpBit = (rowsHeight - textSize)/2 ;
             //四周边框
            cv.drawLine(startx, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(startx,  rowsHeight * (tableRows -7 ), this.getWidth()-1, rowsHeight * (tableRows -7) , linePaint);
            cv.drawLine(startx, 1, startx,  rowsHeight * (tableRows -7), linePaint);
            cv.drawLine(this.getWidth()-1, startx, this.getWidth()-1, rowsHeight * (tableRows -7), linePaint);
            
            //横线
            for(int i=0;i<tableRows - 7;i++){
                startx = 1;
                starty =  rowsHeight * (i+1);
                stopx = this.getWidth() - 1;
                stopy = rowsHeight *(i+1);
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
                if (i < 12 && i > 5){
					String paranameQ = String.valueOf((i - 6)+1);  //画数字序号
					cv.drawText(paranameQ,  ((float)(colsWidth * 0.5) - paramPaint.measureText(paranameQ)) / 2, rowsHeight * (i+1)  - rowUpBit, fontPaint);
                }
            }
            
	        //Throughput 中间线
	        cv.drawLine(this.getWidth() /2 , rowsHeight*1, this.getWidth() /2, rowsHeight*4, linePaint);
	        
	        //throughput
	        paraname = getContext().getString(R.string.tdscdma_throughput);
	        cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
	        
	        int values[] = new int[]{R.string.tdscdma_dl_rlc_thr,R.string.tdscdma_ul_rlc_thr,
	                R.string.tdscdma_dl_pdcp_thr,R.string.tdscdma_ul_pdcp_thr,
	                R.string.tdscdma_dl_rlc_error_rate,R.string.tdscdma_ul_rlc_rtx_rate};
	        
            for (int i = 0,j = 2; i < values.length; i+=2,j++) {
                paraname = getContext().getString(values[i]);
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <values.length){
                    paraname = getContext().getString(values[i+1]);
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
            }
	        
	        //throughput
	        paraname = "DPCH Timeslot Info";
	        cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 5 - rowUpBit, fontPaint);
	      
	      //dpch画竖线
	        
	        String [] paraNameArr = {"ISCP","Used","RSCP","TxPower","SIR"};
	        paramPaint.setColor(getResources().getColor(R.color.info_param_color));
	        
	      //Dpch 竖线
            for(int i=0;i < dpchCols;i++){
                startx = (float) ((colsWidth * 0.5) + (dpchColsWidth * i));
                starty =  rowsHeight * 5;
                stopx = (float) ((colsWidth * 0.5) + (dpchColsWidth * i));;
                stopy = rowsHeight * 12 ;
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
                cv.drawText(paraNameArr[i], (float)(colsWidth * 0.5) + dpchColsWidth * i + (dpchColsWidth - paramPaint.measureText(paraNameArr[i])) / 2, rowsHeight * 6 - rowUpBit, paramPaint);
            }
	        
            paramPaint.setColor(getResources().getColor(R.color.info_param_color));
            
		}else if(currentPage == 5){
            tableRows = 18;
            rowsHeight = this.getViewHeight() / tableRows;
            rowUpBit = (rowsHeight - textSize)/2 ;
             //四周边框
            cv.drawLine(startx, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(startx,  rowsHeight * (tableRows -6 ), this.getWidth()-1, rowsHeight * (tableRows -6) , linePaint);
            cv.drawLine(startx, 1, startx,  rowsHeight * (tableRows -6), linePaint);
            cv.drawLine(this.getWidth()-1, startx, this.getWidth()-1, rowsHeight * (tableRows -6), linePaint);
            
            //横线
            for(int i=0;i<tableRows - 6;i++){
                startx = 1;
                starty =  rowsHeight * (i+1);
                stopx = this.getWidth() - 1;
                stopy = rowsHeight *(i+1);
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
                
            }
            paraname = getContext().getString(R.string.cdma_state);
            cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
            
            int[] values = new int[]{
                    R.string.tdscdma_main_state,
                    R.string.tdscdma_connected_state,
                    };
            
            for (int i = 0,j = 2; i < values.length; i++,j++) {
                paraname = getContext().getString(values[i]);
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
            }
            
	        paraname = "System State";
            cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 4 - rowUpBit, fontPaint);
	        
            String valueArr[]  = new String[]{"Main Current State","Main Previous State",
	        								"Connected Current State","Connected Previous State"};
	        for (int i = 0,j = 5; i < valueArr.length; i++,j++) {
                paraname = valueArr[i];
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
            }
	        
	        paraname = "System State";
            cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 9 - rowUpBit, fontPaint);
	        
	      //System State 中间线
	        cv.drawLine(this.getWidth() /2 , rowsHeight*9, this.getWidth() /2, rowsHeight*12, linePaint);
	        valueArr  = new String[]{"Attach Allowed","QRxLevMin",
					"Cell Barred","ServRs",
					"Max Allowed TxPower",""
	        		};
	        
	        for (int i = 0,j = 10; i < valueArr.length; i+=2,j++) {
                paraname = valueArr[i];
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <valueArr.length){
                    paraname =valueArr[i+1];
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
            }
	        
            
		}else if(currentPage == 6){
			
            tableRows = 20;
            int  tsRows = 6 ;
            float rowsHeight = this.getHeight()/tableRows;	//行高
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
            
            paramPaint.setColor(getResources().getColor(R.color.app_main_text_color));
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
          
          
          
          String[] tagName1 = { "UL DPCH", "DL DPCH"};
          
          int [] paintColor = {Color.YELLOW ,getResources().getColor(R.color.info_param_color)};
			
			
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
		float neiColsWith = (this.getWidth() - colsWidth) / tableCols;//邻近单元C*宽度
		try{
    		String value;
    		
    		if(currentPage == 1){
    		    //TdScdmaModel models = traceData.getTdScdmaInfo();
    		    
                //查询CellName逻辑
                if(!StringUtil.isNullOrEmpty(getParaValue(UnifyParaID.TD_Ser_UARFCN)) && !StringUtil.isNullOrEmpty(getParaValue(UnifyParaID.TD_Ser_CPI))){
                    StringBuilder cellKey = new StringBuilder();
                    cellKey.append(WalktourConst.NetWork.TDSDCDMA);
                    cellKey.append("_uarfcn_").append(getParaValue(UnifyParaID.TD_Ser_UARFCN));
                    cellKey.append("_cpi_").append(getParaValue(UnifyParaID.TD_Ser_CPI));
                    if(traceData.getNetworkCellInfo(cellKey.toString()) != null){
                        value = traceData.getNetworkCellInfo(cellKey.toString()).getCellName();
                        cv.drawText(value, this.getWidth() -paramPaint.measureText(value) -marginSize, rowsHeight * 13 - rowUpBit, paramPaint);
                    }else {
                        traceData.setNetworkCellInfo(cellKey.toString(),  new CellInfo("","",-1));
                        StringBuilder sb = new StringBuilder();
                        sb.append("uarfcn  = '").append(getParaValue(UnifyParaID.TD_Ser_UARFCN)).append("'");
                        sb.append(" and cpi = '").append(getParaValue(UnifyParaID.TD_Ser_CPI)).append("'");
                        new CheckCellParamThread(this.getContext(),new String[]{"uarfcn","cpi","cellName","cellId","longitude","latitude"},sb.toString(), WalktourConst.NetWork.TDSDCDMA).start();
                    }
                }
                
    		     //Serving Cell System Info
    		    String[] datas = new String[]{getParaValue(UnifyParaID.TD_Ser_MCC).equals("") &&  getParaValue(UnifyParaID.TD_Ser_MNC).equals("") 
    		            ? "" : getParaValue(UnifyParaID.TD_Ser_MCC) + "/" + getParaValue(UnifyParaID.TD_Ser_MNC),getParaValue(UnifyParaID.TD_Ser_LAC),
    		            getParaValue(UnifyParaID.TD_Ser_UARFCN),getParaValue(UnifyParaID.TD_Ser_CellID),
    		            getParaValue(UnifyParaID.TD_Ser_DCHURAFCN),getParaValue(UnifyParaID.TD_Ser_RNCID),
    		            getParaValue(UnifyParaID.TD_Ser_CPI),getParaValue(UnifyParaID.TD_Ser_URAID),
    		            getParaValue(UnifyParaID.TD_Ser_CarrierRSSI),getParaValue(UnifyParaID.TD_Ser_UpPCHTxPower),
    		            getParaValue(UnifyParaID.TD_Ser_PCCPCHRSCP),getParaValue(UnifyParaID.TD_Ser_DPCHRSCP),
    		            getParaValue(UnifyParaID.TD_Ser_PCCPCHISCP),getParaValue(UnifyParaID.TD_Ser_DPCHISCP),
    		            getParaValue(UnifyParaID.TD_Ser_PCCPCHC2I),getParaValue(UnifyParaID.TD_Ser_DPCHC2I),
    		            getParaValue(UnifyParaID.TD_Ser_PCCPCHSIR),getParaValue(UnifyParaID.TD_Ser_UETxPower),
    		            getParaValue(UnifyParaID.TD_Ser_PCCPCHPathloss),getParaValue(UnifyParaID.TD_Ser_TA),
    		            getParaValue(UnifyParaID.TD_Ser_BLER),getParaValue(UnifyParaID.TD_Ser_RAC)
    		            };
    		    
                for (int i = 0,j = 2; i < datas.length; i+=2,j++) {
                    cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                    if(i+1 <datas.length){
                        cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                    }
                }
    		}else if(currentPage == 2){
    	        //neighbor cell info
    			String[] datas = new String[]{
    					getParaValue(UnifyParaID.T_NCell_N1_UARFCN),
    					getParaValue(UnifyParaID.T_NCell_N2_UARFCN),
    					getParaValue(UnifyParaID.T_NCell_N3_UARFCN),
    					getParaValue(UnifyParaID.T_NCell_N4_UARFCN),
    					getParaValue(UnifyParaID.T_NCell_N5_UARFCN),
    					getParaValue(UnifyParaID.T_NCell_N6_UARFCN),
    					
    					getParaValue(UnifyParaID.T_NCell_N1_CPI),
    					getParaValue(UnifyParaID.T_NCell_N2_CPI),
    					getParaValue(UnifyParaID.T_NCell_N3_CPI),
    					getParaValue(UnifyParaID.T_NCell_N4_CPI),
    					getParaValue(UnifyParaID.T_NCell_N5_CPI),
    					getParaValue(UnifyParaID.T_NCell_N6_CPI),
    					
    					getParaValue(UnifyParaID.T_NCell_N1_RSCP),
    					getParaValue(UnifyParaID.T_NCell_N2_RSCP),
    					getParaValue(UnifyParaID.T_NCell_N3_RSCP),
    					getParaValue(UnifyParaID.T_NCell_N4_RSCP),
    					getParaValue(UnifyParaID.T_NCell_N5_RSCP),
    					getParaValue(UnifyParaID.T_NCell_N6_RSCP),
    					
    					
              getCellIDByPara(traceData,getParaValue(UnifyParaID.T_NCell_N1_UARFCN),getParaValue(UnifyParaID.T_NCell_N1_CPI)),
              getCellIDByPara(traceData,getParaValue(UnifyParaID.T_NCell_N2_UARFCN),getParaValue(UnifyParaID.T_NCell_N2_CPI)),
              getCellIDByPara(traceData,getParaValue(UnifyParaID.T_NCell_N3_UARFCN),getParaValue(UnifyParaID.T_NCell_N3_CPI)),
              getCellIDByPara(traceData,getParaValue(UnifyParaID.T_NCell_N4_UARFCN),getParaValue(UnifyParaID.T_NCell_N4_CPI)),
              getCellIDByPara(traceData,getParaValue(UnifyParaID.T_NCell_N5_UARFCN),getParaValue(UnifyParaID.T_NCell_N5_CPI)),
              getCellIDByPara(traceData,getParaValue(UnifyParaID.T_NCell_N6_UARFCN),getParaValue(UnifyParaID.T_NCell_N6_CPI)),
    					
    					getParaValue(UnifyParaID.T_NCell_N1_CarrierRSSI),
    					getParaValue(UnifyParaID.T_NCell_N2_CarrierRSSI),
    					getParaValue(UnifyParaID.T_NCell_N3_CarrierRSSI),
    					getParaValue(UnifyParaID.T_NCell_N4_CarrierRSSI),
    					getParaValue(UnifyParaID.T_NCell_N5_CarrierRSSI),
    					getParaValue(UnifyParaID.T_NCell_N6_CarrierRSSI),
    					
    					getParaValue(UnifyParaID.T_NCell_N1_PathLoss),
    					getParaValue(UnifyParaID.T_NCell_N2_PathLoss),
    					getParaValue(UnifyParaID.T_NCell_N3_PathLoss),
    					getParaValue(UnifyParaID.T_NCell_N4_PathLoss),
    					getParaValue(UnifyParaID.T_NCell_N5_PathLoss),
    					getParaValue(UnifyParaID.T_NCell_N6_PathLoss),
    					
    					getParaValue(UnifyParaID.T_NCell_N1_Rn),
    					getParaValue(UnifyParaID.T_NCell_N2_Rn),
    					getParaValue(UnifyParaID.T_NCell_N3_Rn),
    					getParaValue(UnifyParaID.T_NCell_N4_Rn),
    					getParaValue(UnifyParaID.T_NCell_N5_Rn),
    					getParaValue(UnifyParaID.T_NCell_N6_Rn)
    			};
    			for (int i = 0,j = 2; i < datas.length; i++,j++) {
                    cv.drawText(datas[i], 	colsWidth + neiColsWith * (i % 6) + (neiColsWith - paramPaint.measureText(datas[i])) / 2, rowsHeight * j - rowUpBit, paramPaint);
                    cv.drawText(datas[++i], colsWidth + neiColsWith * (i % 6) + (neiColsWith - paramPaint.measureText(datas[i])) / 2, rowsHeight * j - rowUpBit, paramPaint);
                    cv.drawText(datas[++i], colsWidth + neiColsWith * (i % 6) + (neiColsWith - paramPaint.measureText(datas[i])) / 2, rowsHeight * j - rowUpBit, paramPaint);
                    cv.drawText(datas[++i], colsWidth + neiColsWith * (i % 6) + (neiColsWith - paramPaint.measureText(datas[i])) / 2, rowsHeight * j - rowUpBit, paramPaint);
                    cv.drawText(datas[++i], colsWidth + neiColsWith * (i % 6) + (neiColsWith - paramPaint.measureText(datas[i])) / 2, rowsHeight * j - rowUpBit, paramPaint);
                    cv.drawText(datas[++i], colsWidth + neiColsWith * (i % 6) + (neiColsWith - paramPaint.measureText(datas[i])) / 2, rowsHeight * j - rowUpBit, paramPaint);
    			}  
    			
                if(buffer != null  && buffer2 != null){
                    buffer.append(") and ").append(buffer2).append(")");
                    new CheckCellParamThread(this.getContext(),new String[]{"uarfcn","cpi","cellName","cellId","longitude","latitude"},buffer.toString(), WalktourConst.NetWork.TDSDCDMA).start();
                }
                buffer = null;
                buffer2 = null;
                
    		}else if(currentPage == 3){
    		    String[] datas = new String[]{
    		    		UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.TD_Thr_DL_RLC_Thr)),UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.TD_Thr_UL_RLC_Thr)),
    		    		UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.TD_Thr_DL_PDCP_Thr)),UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.TD_Thr_UL_PDCP_Thr)),
	    				getParaValue(UnifyParaID.TD_Thr_DL_RLC_Error_Rate),getParaValue(UnifyParaID.TD_Thr_UL_RLC_RTX_Rate)};
                for (int i = 0,j = 2; i < datas.length; i+=2,j++) {
                    cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                    if(i+1 <datas.length){
                        cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                    }
                }
    		    int dpchCols = 5 ;  //DPCH显示参数列5
    		    int dpchColsWidth = (int) (this.getWidth() - (colsWidth * 0.5)) / dpchCols ;
                
                
               datas = new String[]{
    					"",
    					"",
    					"",
    					getParaValue(UnifyParaID.TD_TS1_TxPower),
    					"",
    					
    					
    					getParaValue(UnifyParaID.TD_TS2_DPCH_ISCP),
    					getParaValue(UnifyParaID.TD_TS2_DPCH_RSCP).equals("")?"":"Used",
    					getParaValue(UnifyParaID.TD_TS2_DPCH_RSCP),
    					getParaValue(UnifyParaID.TD_TS2_TxPower),
    					getParaValue(UnifyParaID.TD_TS2_DPCH_SIR).equals("") ? "" : 
    						(UtilsMethod.decFormat.format(Float.parseFloat(getParaValue(UnifyParaID.TD_TS2_DPCH_SIR)) + 12.04)),
    					
    					getParaValue(UnifyParaID.TD_TS3_DPCH_ISCP),
    					getParaValue(UnifyParaID.TD_TS3_DPCH_RSCP).equals("")?"":"Used",
    					getParaValue(UnifyParaID.TD_TS3_DPCH_RSCP),
    					getParaValue(UnifyParaID.TD_TS3_TxPower),
    					getParaValue(UnifyParaID.TD_TS3_DPCH_SIR).equals("") ? "" : 
    						(UtilsMethod.decFormat.format(Float.parseFloat(getParaValue(UnifyParaID.TD_TS3_DPCH_SIR)) + 12.04)),
    					
    					
    					getParaValue(UnifyParaID.TD_TS4_DPCH_ISCP),
    					getParaValue(UnifyParaID.TD_TS4_DPCH_RSCP).equals("")?"":"Used",
    					getParaValue(UnifyParaID.TD_TS4_DPCH_RSCP),
    					"",
    					getParaValue(UnifyParaID.TD_TS4_DPCH_SIR).equals("") ? "" : 
    						(UtilsMethod.decFormat.format(Float.parseFloat(getParaValue(UnifyParaID.TD_TS4_DPCH_SIR)) + 12.04)),
    					
    					getParaValue(UnifyParaID.TD_TS5_DPCH_ISCP),
    					getParaValue(UnifyParaID.TD_TS5_DPCH_RSCP).equals("")?"":"Used",
    					getParaValue(UnifyParaID.TD_TS5_DPCH_RSCP),
    					"",
    					getParaValue(UnifyParaID.TD_TS5_DPCH_SIR).equals("") ? "" : 
    						(UtilsMethod.decFormat.format(Float.parseFloat(getParaValue(UnifyParaID.TD_TS5_DPCH_SIR)) + 12.04)),
    					
    					
    					getParaValue(UnifyParaID.TD_TS6_DPCH_ISCP),
    					getParaValue(UnifyParaID.TD_TS6_DPCH_RSCP).equals("")?"":"Used",
    					getParaValue(UnifyParaID.TD_TS6_DPCH_RSCP),
    					"",
    					getParaValue(UnifyParaID.TD_TS6_DPCH_SIR).equals("") ? "" : 
    						(UtilsMethod.decFormat.format(Float.parseFloat(getParaValue(UnifyParaID.TD_TS6_DPCH_SIR)) + 12.04))
    					
    			};
               for (int i = 0,j = 7; i < datas.length; i+=5,j++) {
                   cv.drawText(datas[i], 	(float)(colsWidth * 0.5) + dpchColsWidth * 0 +(dpchColsWidth  - paramPaint.measureText(datas[i])) / 2, rowsHeight * j - rowUpBit, paramPaint);
                   cv.drawText(datas[i+1], (float)(colsWidth * 0.5) + 	dpchColsWidth * 1 +(dpchColsWidth  - paramPaint.measureText(datas[i+1])) / 2, rowsHeight * j - rowUpBit, paramPaint);
                   cv.drawText(datas[i+2], (float)(colsWidth * 0.5) + dpchColsWidth * 2 + (dpchColsWidth  - paramPaint.measureText(datas[i+2])) / 2, rowsHeight * j - rowUpBit, paramPaint);
                   cv.drawText(datas[i+3], (float)(colsWidth * 0.5) + dpchColsWidth * 3 +(dpchColsWidth  - paramPaint.measureText(datas[i+3])) / 2, rowsHeight * j - rowUpBit, paramPaint);
                   cv.drawText(datas[i+4], (float)(colsWidth * 0.5) + dpchColsWidth * 4 +(dpchColsWidth  - paramPaint.measureText(datas[i+4])) / 2, rowsHeight * j - rowUpBit, paramPaint);
               }
    		}else if(currentPage == 4){
                rowsHeight = this.getViewHeight() / 8;
                float[] values = new float[]{
                		UtilsMethod.StringToFloat(getParaValue(UnifyParaID.T_NCell_N1_RSCP),-9999),
                		UtilsMethod.StringToFloat(getParaValue(UnifyParaID.T_NCell_N2_RSCP),-9999),
                		UtilsMethod.StringToFloat(getParaValue(UnifyParaID.T_NCell_N3_RSCP),-9999),
                		UtilsMethod.StringToFloat(getParaValue(UnifyParaID.T_NCell_N4_RSCP),-9999),
                		UtilsMethod.StringToFloat(getParaValue(UnifyParaID.T_NCell_N5_RSCP),-9999),
                		UtilsMethod.StringToFloat(getParaValue(UnifyParaID.T_NCell_N6_RSCP),-9999)
                };
                float[] percentages = new float[6];
                for(int i=0;i<values.length;i++){
                    percentages[i] = 100 - (values[i]/-115 * 100) + 25/115 * 100;
                }
                super.createCellHistogram("RSCP(dBm)", null, values, percentages, (int)rowsHeight * 7, cv);
    		}else if(currentPage == 5){
                /*TdScdmaModel models = traceData.getTdScdmaInfo();
                String[] datas = new String[]{models.getMain_state(),
                        models.getConnection_state()};
                for (int i = 0,j = 2; i < datas.length; i+=1,j++) {
                    cv.drawText(datas[i], this.getWidth() -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }*/
    			
    			
    			
    			String[] datas = new String[]{UtilsMethodPara.getTDMainState(getParaValue(UnifyParaID.TD_Main_Current_State)) ,
    										 UtilsMethodPara.getTDMainState(getParaValue(UnifyParaID.TD_Main_Previous_State)),
    										 UtilsMethodPara.getTDConnectedState(getParaValue(UnifyParaID.TD_Connected_Current_State)),
    										 UtilsMethodPara.getTDConnectedState(UtilsMethodPara.getPreviousState(getParaValue(UnifyParaID.TD_Connected_Current_State)))};
    											
            	for (int i = 0,j = 5; i < datas.length; i++,j++) {
                    cv.drawText(datas[i], this.getWidth() - paramPaint.measureText(datas[i]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }
            	
            	
    			datas = new String[]{UtilsMethodPara.TDScdmaAttachState(getParaValue(UnifyParaID.TD_Attach_Allowed)) ,getParaValue(UnifyParaID.TD_Q_Rxlevmin),
    								UtilsMethodPara.TDScdmaCellState(getParaValue(UnifyParaID.TD_Cell_Barred)),getParaValue(UnifyParaID.TD_Served_RS),
    										getParaValue(UnifyParaID.TD_Max_Allowed_TxPower)};
    			
                for (int i = 0,j = 10; i < datas.length; i+=2,j++) {
                    cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                    if(i+1 <datas.length){
                        cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                    }
                }
    			
    			
    		}else if(currentPage == 6){
    			tableRows = 20;
                int  tsRows = 6 ;
                float rowsHeight = this.getHeight()/tableRows;	//行高
                float tsClosWidth = (this.getWidth() - colsWidth ) / tsRows;
                
                int [] paintColor = {Color.YELLOW ,getResources().getColor(R.color.info_param_color)};
                rowUpBit = (rowsHeight - textSize)/2 ;
    			
    			TDPhysChannelInfoDataV2  tdPhysChannelInfoDataV2 = (TDPhysChannelInfoDataV2)TraceInfoInterface.getParaStruct(UnifyStruct.TDPhysChannelInfoDataV2.FLAG);
    			if(tdPhysChannelInfoDataV2 != null){
    				int length = tdPhysChannelInfoDataV2.channelNum;
    				fontPaint.setColor(Color.BLACK);
    				for (int i = 0; i < length; i++) {
    					if(tdPhysChannelInfoDataV2.chanInfo[i]!= null){
    						if(tdPhysChannelInfoDataV2.chanInfo[i].codeNO_count > 0){
        						for (int j = 0; j < tdPhysChannelInfoDataV2.chanInfo[i].codeNO.length; j++) {
        							Log.i("---codeNO", tdPhysChannelInfoDataV2.chanInfo[i].codeNO[j] + "");
        							if(tdPhysChannelInfoDataV2.chanInfo[i].direction == 0){
        								tlPaint.setColor(paintColor[1]);
        							}else{
        								tlPaint.setColor(paintColor[0]);
        							}
        							float left = colsWidth + (tdPhysChannelInfoDataV2.chanInfo[i].timeSlot - 1) * tsClosWidth + 1;
        							float top  = rowsHeight * 2 + (tdPhysChannelInfoDataV2.chanInfo[i].codeNO[j] - 1) * rowsHeight + 1;
        							float right = colsWidth + tdPhysChannelInfoDataV2.chanInfo[i].timeSlot * tsClosWidth - 1 ;
        							float bottom = rowsHeight * 2 + tdPhysChannelInfoDataV2.chanInfo[i].codeNO[j] * rowsHeight -1 ;
        							cv.drawRect(left,top,right,bottom,tlPaint);
        							String paraValue = "SF="+tdPhysChannelInfoDataV2.chanInfo[i].sf;
        							cv.drawText(paraValue, 
        									colsWidth + (tdPhysChannelInfoDataV2.chanInfo[i].timeSlot - 1) * tsClosWidth + (tsClosWidth - fontPaint.measureText(paraValue)) / 2, 
        									rowsHeight * 2 + tdPhysChannelInfoDataV2.chanInfo[i].codeNO[j] * rowsHeight -  rowUpBit ,
        									fontPaint);
        						}
        					}
    					}
    				}
    			}
    		}
    
    		cv.save();
    		cv.restore();
		}catch(Exception e){
		    e.printStackTrace();
		}
	}
	
	/**获得得参数队列中指定ID的值*/
    private String getParaValue(int paraId){
        return TraceInfoInterface.getParaValue(paraId);
    }
    
    /**
     * 获取CELLID<BR>
     * [功能详细描述]
     * @param traceData
     * @param uarfcn
     * @param cpi
     * @return
     */
    private String getCellIDByPara(TraceInfoData traceData,String uarfcn,String cpi){
        String value = "";
        String cellidKey = WalktourConst.NetWork.TDSDCDMA + "_uarfcn_" + uarfcn
                +"_cpi_" + cpi;
        if(traceData.containsCellIDHmKey(cellidKey)){
            value = traceData.getNetworkCellInfo(cellidKey).getCellId(); 
        }else {
            value = "";
            traceData.setNetworkCellInfo(cellidKey, new CellInfo("","",-1));
            if(buffer == null){
                if(!StringUtil.isNullOrEmpty(uarfcn)){
                    buffer = new StringBuffer();
                    buffer.append("uarfcn in(" + uarfcn);
                }
            }else {
                if(!StringUtil.isNullOrEmpty(uarfcn)){
                    buffer.append(","+ uarfcn);
                }
            }
            if(buffer2 == null){
                if(!StringUtil.isNullOrEmpty(cpi)){
                    buffer2 = new StringBuffer();
                    buffer2.append("cpi in(" + cpi);
                }
            }else {
                if(!StringUtil.isNullOrEmpty(cpi)){
                    buffer2.append(","+ cpi);
                }
                
            }
            
        }
        return value;
    }
    
    /**
     * @param viewHeight the viewHeight to set
     */
    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
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
