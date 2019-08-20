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
import com.walktour.Utils.UtilsMethodPara;
import com.walktour.framework.view.BasicParamView;
import com.walktour.gui.R;

/**
 * LTE-Ca 参数界面view
 * @author zhihui.lian
 */

public class LteCaView extends BasicParamView {
    
    private int currentPage = 1;
    
    private int tableCols = 6; //列数
	
	public LteCaView(Context context) {
		super(context);
	}
	
	public LteCaView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	   public LteCaView(Context context,int page) {
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
		    tableRows = 17;
		    rowsHeight = this.getHeight()/ 19;
		    rowUpBit = (rowsHeight - textSize)/2;
		    
	        //四周边框
            cv.drawLine(1, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(1,  rowsHeight * (tableRows -1), this.getWidth()-1,rowsHeight * (tableRows-1) , linePaint);
            cv.drawLine(1, 1, 1,  rowsHeight * tableRows, linePaint);
            cv.drawLine(this.getWidth()-1, 1, this.getWidth()-1, rowsHeight * tableRows, linePaint);
            
			//横线
			for(int i=0;i<tableRows ;i++){
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
            stopy = rowsHeight  * 3;
            cv.drawLine(startx, starty, stopx, stopy, linePaint);

			String paraname;
			//LTE CA Capacity
			paraname = "LTE CA Capacity";
			cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
			
			String[] values = new String[]{"Packet Capability","LTE UE Category",
			        "CA Carrier Count","Network State",
			        "EMM Substate",""
			        };
			
            for (int i = 0,j = 2; i < values.length; i+=2,j++) {
                paraname = values[i];
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <values.length){
                    paraname = values[i+1];
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
            }

            for (int i = 1; i < 4; i++) {
            	//Serving cell 竖线
				if (i == 1){
            		startx = (this.getWidth() / 3 ) * i;
            	}else{
            		startx = (this.getWidth() / 3 ) + (this.getWidth() - this.getWidth() / 3  ) / 3 * (i-1) ;
            	}
            	starty =  rowsHeight * 5;
            	stopx = startx;
            	stopy = rowsHeight  * tableRows;
            	cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
            //CA Serving Cells
			paraname = "CA Serving Cells";
			cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 5 - rowUpBit, fontPaint);
			
			//竖线名称 
			values = new String[] { "Work Mode", "TAC", "Cell ID", "Band",
					"DL EARFCN", "UL EARFCN", "PCI", "DL/UL BandWidth", "TM",
					"DL/UL Freq(M)", "CodeWord Num" };
			
			for (int i = 0; i < values.length; i++) {
				cv.drawText(values[i],  (this.getWidth()/3 - fontPaint.measureText(values[i])) / 2, rowsHeight * (7+i) - rowUpBit, fontPaint);
			}
			
			values = new String[] { "PCell", "SCell1", "SCell2"};
			
			for (int i = 0; i < values.length; i++) {
				cv.drawText(values[i],  this.getWidth() / 3  +  (this.getWidth() * 2 ) / 9 * i +  ((this.getWidth() * 2 ) / 9 - fontPaint.measureText(values[i])) / 2, rowsHeight * 6 - rowUpBit, fontPaint);
			}
			
            
		}else if(currentPage ==2){
			tableRows = 18;
		    rowsHeight = this.getHeight()/ 19;
		    rowUpBit = (rowsHeight - textSize)/2;
		    
	        //四周边框
            cv.drawLine(1, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(1,  rowsHeight * (tableRows -1), this.getWidth()-1,rowsHeight * (tableRows-1) , linePaint);
            cv.drawLine(1, 1, 1,  rowsHeight * tableRows, linePaint);
            cv.drawLine(this.getWidth()-1, 1, this.getWidth()-1, rowsHeight * tableRows, linePaint);
            
			//横线
			for(int i=0;i<tableRows ;i++){
				startx = 1;
				starty =  rowsHeight * (i+1);
				stopx = this.getWidth()-1;
				stopy = rowsHeight *(i+1);
				cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
			String paraname;
			//CA Serving Cell Measurement
			paraname = "CA Serving Cell Measurement";
			cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);

            for (int i = 1; i < 4; i++) {
            	startx = (this.getWidth() / 3 ) + (this.getWidth() - this.getWidth() / 3  ) / 3 * (i-1) ;
            	starty =  rowsHeight;
            	stopx = startx;
            	stopy = rowsHeight  * tableRows;
            	cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
			//竖线名称 
			String[] values = new String[] { "RSSI", "RSSI Rx0", "RSSI Rx1", "RSRP",
					"RSRP Rx0", "RSRP Rx1", "CRS RP", "DRS RP", "SINR",
					"SINR Rx0", "SINR Rx1","CRS SINR", "DRS SINR","RSRQ","RSRQ Rx0","RSRQ Rx1" };
			
			for (int i = 0; i < values.length; i++) {
				cv.drawText(values[i],  (this.getWidth()/3 - fontPaint.measureText(values[i])) / 2, rowsHeight * (3+i) - rowUpBit, fontPaint);
			}
			
			values = new String[] {"PCell", "SCell1", "SCell2"};
			
			for (int i = 0; i < values.length; i++) {
				cv.drawText(values[i],  this.getWidth() / 3  +  (this.getWidth() * 2 ) / 9 * i +  ((this.getWidth() * 2 ) / 9 - fontPaint.measureText(values[i])) / 2, rowsHeight * 2  - rowUpBit, fontPaint);
			}
			
		}else if(currentPage ==3){
			tableRows = 17;
		    rowsHeight = this.getHeight()/ 19;
		    rowUpBit = (rowsHeight - textSize)/2;
		    
	        //四周边框
            cv.drawLine(1, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(1,  rowsHeight * (tableRows -1), this.getWidth()-1,rowsHeight * (tableRows-1) , linePaint);
            cv.drawLine(1, 1, 1,  rowsHeight * tableRows, linePaint);
            cv.drawLine(this.getWidth()-1, 1, this.getWidth()-1, rowsHeight * tableRows, linePaint);
            
			//横线
			for(int i=0;i<tableRows ;i++){
				startx = 1;
				starty =  rowsHeight * (i+1);
				stopx = this.getWidth()-1;
				stopy = rowsHeight *(i+1);
				cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
			String paraname;
			//CA Serving Cell Measurement
			paraname = "Throughput";
			cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
			paraname = "PDSCH RB Info";
			cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 5 - rowUpBit, fontPaint);
			paraname = "Grant Info";
			cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 14 - rowUpBit, fontPaint);

            for (int i = 1; i < 5; i++) {
            	startx = (this.getWidth() / 3 ) + (this.getWidth() - this.getWidth() / 3  ) / 4 * (i-1) ;
            	starty =  rowsHeight;
            	stopx = startx;
            	stopy = rowsHeight  * 4;
            	cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
            
            for (int i = 1; i < 5; i++) {
            	startx = (this.getWidth() / 3 ) + (this.getWidth() - this.getWidth() / 3  ) / 4 * (i-1) ;
            	starty =  rowsHeight * 5;
            	stopx = startx;
            	stopy = rowsHeight  * 13;
            	cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
            
            for (int i = 1; i < 5; i++) {
            	startx = (this.getWidth() / 3 ) + (this.getWidth() - this.getWidth() / 3  ) / 4 * (i-1) ;
            	starty =  rowsHeight * 14;
            	stopx = startx;
            	stopy = rowsHeight  * tableRows;
            	cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
			//竖线名称 
			String[] values = new String[] { "MAC Thr DL(kbps)", "Phy Thr DL(kbps)", "", "",
					"RB Count/s", "RB Count/slot", "Slot Count/s", "SubFN Count/s", "Schedule Rate(%)", "TB Size Code0","TB Size Code1",
					"", "","PDCCH DL Grant ", "PDCCH UL Grant"};
			
			for (int i = 0; i < values.length; i++) {
				cv.drawText(values[i],  (this.getWidth()/3 - fontPaint.measureText(values[i])) / 2, rowsHeight * (3+i) - rowUpBit, fontPaint);
			}
			
			values = new String[] {"Total", "PCell", "SCell1", "SCell2"};
			
			for (int i = 0; i < values.length; i++) {
				cv.drawText(values[i],  this.getWidth() / 3  +  (this.getWidth()) / 6 * i +  ((this.getWidth()) / 6 - fontPaint.measureText(values[i])) / 2, rowsHeight * 2  - rowUpBit, fontPaint);
			}
			for (int i = 0; i < values.length; i++) {
				cv.drawText(values[i],  this.getWidth() / 3  +  (this.getWidth()) / 6 * i +  ((this.getWidth()) / 6 - fontPaint.measureText(values[i])) / 2, rowsHeight * 6  - rowUpBit, fontPaint);
			}
			for (int i = 0; i < values.length; i++) {
				cv.drawText(values[i],  this.getWidth() / 3  +  (this.getWidth()) / 6 * i +  ((this.getWidth() ) / 6 - fontPaint.measureText(values[i])) / 2, rowsHeight * 15  - rowUpBit, fontPaint);
			}
			
		}
		else if(currentPage ==4){
			tableRows = 22;
		    rowsHeight = this.getHeight()/ tableRows;
		    rowUpBit = (rowsHeight - textSize)/2;
		    
	        //四周边框
            cv.drawLine(1, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(1,  this.getHeight() - 1, this.getWidth()-1,this.getHeight() - 1 , linePaint);
            cv.drawLine(1, 1, 1,  rowsHeight * tableRows, linePaint);
            cv.drawLine(this.getWidth()-1, 1, this.getWidth()-1, rowsHeight * tableRows, linePaint);
            
			//横线
			for(int i=0;i<tableRows ;i++){
				startx = 1;
				starty =  rowsHeight * (i+1);
				stopx = this.getWidth()-1;
				stopy = rowsHeight *(i+1);
				cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
			String paraname;
			//CA Serving Cell Measurement
			paraname = "PDSCH BLER & DL MCS Ratio";
			cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);

            for (int i = 1; i < 5; i++) {
            	startx = (this.getWidth() / 3 ) + (this.getWidth() - this.getWidth() / 3  ) / 4 * (i-1) ;
            	starty =  rowsHeight;
            	stopx = startx;
            	stopy = rowsHeight  * tableRows;
            	cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
            
			//竖线名称
			String[] values = new String[] { "PDSCH BLER (%)", "BLER", "BLER Code0", "BLER Code1",
					"Initial BLER", "Initial BLER Code0", "Initial BLER Code1", "Residual BLER", "Residual BLER Code0", "Residual BLER Code1",
					"DL MCS Ratio/s","QPSK(%)", "16QAM(%)","64QAM(%)", "QPSK Code0(%)","16QAM Code0(%)","64QAM Code0(%)","QPSK Code1(%)","16QAM Code1(%)","64QAM Code1(%)","MCS Avg."};
					
			
			for (int i = 0; i < values.length; i++) {
				cv.drawText(values[i],  (this.getWidth()/3 - fontPaint.measureText(values[i])) / 2, rowsHeight * (2+i) - rowUpBit, selectPaint(i));
			}
			
			values = new String[] {"Total", "PCell", "SCell1", "SCell2"};
			
			for (int i = 0; i < values.length; i++) {
				cv.drawText(values[i],  this.getWidth() / 3  +  (this.getWidth()) / 6 * i +  ((this.getWidth()) / 6 - fontPaint.measureText(values[i])) / 2, rowsHeight * 2  - rowUpBit, fontPaint);
			}
			for (int i = 0; i < values.length; i++) {
				cv.drawText(values[i],  this.getWidth() / 3  +  (this.getWidth()) / 6 * i +  ((this.getWidth()) / 6 - fontPaint.measureText(values[i])) / 2, rowsHeight * 12  - rowUpBit, fontPaint);
			}
			
		}
		cv.save();
		cv.restore();
	}
	
	private Paint selectPaint(int indexPaint){
		switch (indexPaint) {
		case 0:
		case 10:
			return paramPaint;
		default:
			return fontPaint;
		}
	}
	
	
	/**
	 * 创建表格数据
	 * @param bm  要创建表格的位图
	 * @param data  表格数据
	 * @return    输出位图
	 */
	protected void CreateTableData(Canvas cv,TraceInfoData traceData){

		
		if(currentPage ==1){
			float tableRows = 17;
			float rowsHeight = this.getHeight()/ 19;
			float  rowUpBit = (rowsHeight - textSize)/2;

			String[] datas = new String[]{UtilsMethodPara.netWorkCaType(getParaValue(UnifyParaID.LTECA_Capacity_Packet_Capability)),getParaValue(UnifyParaID.LTECA_Capacity_LTEUCategory),
											getParaValue(UnifyParaID.LTECA_Capacity_CACarrierCount),UtilsMethodPara.getLteRRCState(getParaValue(UnifyParaID.CURRENT_STATE_LTE)),
											"",UtilsMethodPara.getLteEMMSubState(getParaValue(UnifyParaID.L_SRV_EMM_Substate))}; 
												
            for (int i = 0,j = 2; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                if(i+1 <datas.length){
                    cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }
            }
            datas = new String[]{                               
            		getParaValue(UnifyParaID.LTECA_WorkMode_PCell),    
            		getParaValue(UnifyParaID.LTECA_WorkMode_SCell1),   
            		getParaValue(UnifyParaID.LTECA_WorkMode_SCell2),   
            		                                                   
            		getParaValue(UnifyParaID.LTECA_TAC_PCell),         
            		getParaValue(UnifyParaID.LTECA_TAC_SCell1),        
            		getParaValue(UnifyParaID.LTECA_TAC_SCell2),        
            		                                                   
            		getParaValue(UnifyParaID.LTECA_CellID_PCell),      
            		getParaValue(UnifyParaID.LTECA_CellID_SCell1),     
            		getParaValue(UnifyParaID.LTECA_CellID_SCell2),     
            		                                                   
            		getParaValue(UnifyParaID.LTECA_Band_PCell),        
            		getParaValue(UnifyParaID.LTECA_Band_SCell1),       
            		getParaValue(UnifyParaID.LTECA_Band_SCell2),       
            		                                                   
            		getParaValue(UnifyParaID.LTECA_DLEARFCN_PCell),    
            		getParaValue(UnifyParaID.LTECA_DLEARFCN_SCell1),   
            		getParaValue(UnifyParaID.LTECA_DLEARFCN_SCell2),   
            		                                                   
            		getParaValue(UnifyParaID.LTECA_ULEARFCN_PCell),    
            		getParaValue(UnifyParaID.LTECA_ULEARFCN_SCell1),   
            		getParaValue(UnifyParaID.LTECA_ULEARFCN_SCell2),   
            		                                                   
            		getParaValue(UnifyParaID.LTECA_PCI_PCell),         
            		getParaValue(UnifyParaID.LTECA_PCI_SCell1),        
            		getParaValue(UnifyParaID.LTECA_PCI_SCell2),        
            		                                                   
            		getParaValue(UnifyParaID.LTECA_BandWidth_PCell),   
            		getParaValue(UnifyParaID.LTECA_BandWidth_SCell1),  
            		getParaValue(UnifyParaID.LTECA_BandWidth_SCell2),  
            		                                                   
            		                                                   
            		getParaValue(UnifyParaID.LTECA_TM_PCell),          
            		getParaValue(UnifyParaID.LTECA_TM_SCell1),         
            		getParaValue(UnifyParaID.LTECA_TM_SCell2),         
            		                                                   
            		getParaValue(UnifyParaID.LTECA_Freq_PCell),        
            		getParaValue(UnifyParaID.LTECA_Freq_SCell1),       
            		getParaValue(UnifyParaID.LTECA_Freq_SCell2),       
            		                                                   
            		getParaValue(UnifyParaID.LTECA_CodeWordNum_PCell), 
            		getParaValue(UnifyParaID.LTECA_CodeWordNum_SCell1),
            		getParaValue(UnifyParaID.LTECA_CodeWordNum_SCell2)
            		};                                                 

            for (int i = 0,j = 7; i < datas.length; i+=3,j++) {
            	cv.drawText(datas[i],  this.getWidth() / 3  +  (this.getWidth() * 2 ) / 9 * 0 +  ((this.getWidth() * 2 ) / 9 - fontPaint.measureText(datas[i])) / 2, rowsHeight * j - rowUpBit, paramPaint);
            	 if(i+1 <datas.length){
            		 cv.drawText(datas[i+1],  this.getWidth() / 3  +  (this.getWidth() * 2 ) / 9 * 1 +  ((this.getWidth() * 2 ) / 9 - fontPaint.measureText(datas[i+1])) / 2, rowsHeight * j - rowUpBit, paramPaint);
            	 }
            	 if(i+2 <datas.length){
            		 cv.drawText(datas[i+2],  this.getWidth() / 3  +  (this.getWidth() * 2 ) / 9 * 2 +  ((this.getWidth() * 2 ) / 9 - fontPaint.measureText(datas[i+2])) / 2, rowsHeight * j - rowUpBit, paramPaint);
            	 }
            }
            
		}else if(currentPage == 2){
		    int rowsHeight = this.getHeight()/ 19;
		    int rowUpBit = (rowsHeight - textSize)/2;
		    
			String []datas = new String[]{
					getParaValue(UnifyParaID.LTECA_RSSI_PCell) ,     
					getParaValue(UnifyParaID.LTECA_RSSI_SCell1),     
					getParaValue(UnifyParaID.LTECA_RSSI_SCell2),     
					                                                 
					getParaValue(UnifyParaID.LTECA_RSSIRx0_PCell),   
					getParaValue(UnifyParaID.LTECA_RSSIRx0_SCell1),  
					getParaValue(UnifyParaID.LTECA_RSSIRx0_SCell2),  
					                                                 
					getParaValue(UnifyParaID.LTECA_RSSIRx1_PCell),   
					getParaValue(UnifyParaID.LTECA_RSSIRx1_SCell1),  
					getParaValue(UnifyParaID.LTECA_RSSIRx1_SCell2),  
					                                                 
					getParaValue(UnifyParaID.LTECA_RSRP_PCell),      
					getParaValue(UnifyParaID.LTECA_RSRP_SCell1),     
					getParaValue(UnifyParaID.LTECA_RSRP_SCell2),     
					                                                 
					getParaValue(UnifyParaID.LTECA_RSRPRx0_PCell),   
					getParaValue(UnifyParaID.LTECA_RSRPRx0_SCell1),  
					getParaValue(UnifyParaID.LTECA_RSRPRx0_SCell2),  
					                                                 
					getParaValue(UnifyParaID.LTECA_RSRPRx1_PCell),   
					getParaValue(UnifyParaID.LTECA_RSRPRx1_SCell1),  
					getParaValue(UnifyParaID.LTECA_RSRPRx1_SCell2),  
					                                                 
					getParaValue(UnifyParaID.LTECA_CRSRP_PCell),     
					getParaValue(UnifyParaID.LTECA_CRSRP_SCell1),    
					getParaValue(UnifyParaID.LTECA_CRSRP_SCell2),    
					                                                 
					getParaValue(UnifyParaID.LTECA_DRSRP_PCell),     
					getParaValue(UnifyParaID.LTECA_DRSRP_SCell1),    
					getParaValue(UnifyParaID.LTECA_DRSRP_SCell2),    
					                                                 
					getParaValue(UnifyParaID.LTECA_SINR_PCell),      
					getParaValue(UnifyParaID.LTECA_SINR_SCell1),     
					getParaValue(UnifyParaID.LTECA_SINR_SCell2),     
					                                                 
					getParaValue(UnifyParaID.LTECA_SINRRx0_PCell),   
					getParaValue(UnifyParaID.LTECA_SINRRx0_SCell1),  
					getParaValue(UnifyParaID.LTECA_SINRRx0_SCell2),  
					                                                 
					getParaValue(UnifyParaID.LTECA_SINRRx1_PCell),   
					getParaValue(UnifyParaID.LTECA_SINRRx1_SCell1),  
					getParaValue(UnifyParaID.LTECA_SINRRx1_SCell2),  
					                                                 
					getParaValue(UnifyParaID.LTECA_CRSSINR_PCell),   
					getParaValue(UnifyParaID.LTECA_CRSSINR_SCell1),  
					getParaValue(UnifyParaID.LTECA_CRSSINR_SCell2),  
					                                                 
					getParaValue(UnifyParaID.LTECA_DRSSINR_PCell),   
					getParaValue(UnifyParaID.LTECA_DRSSINR_SCell1),  
					getParaValue(UnifyParaID.LTECA_DRSSINR_SCell2),  
					                                                 
					getParaValue(UnifyParaID.LTECA_RSRQ_PCell),      
					getParaValue(UnifyParaID.LTECA_RSRQ_SCell1),     
					getParaValue(UnifyParaID.LTECA_RSRQ_SCell2),     
					                                                 
					getParaValue(UnifyParaID.LTECA_RSRQRx0_PCell),   
					getParaValue(UnifyParaID.LTECA_RSRQRx0_SCell1),  
					getParaValue(UnifyParaID.LTECA_RSRQRx0_SCell2),  
					                                                 
					getParaValue(UnifyParaID.LTECA_RSRQRx1_PCell),   
					getParaValue(UnifyParaID.LTECA_RSRQRx1_SCell1),  
					getParaValue(UnifyParaID.LTECA_RSRQRx1_SCell2)   
					};
			for (int i = 0,j = 3; i < datas.length; i+=3,j++) {
            	cv.drawText(datas[i],  this.getWidth() / 3  +  (this.getWidth() * 2 ) / 9 * 0 +  ((this.getWidth() * 2 ) / 9 - fontPaint.measureText(datas[i])) / 2, rowsHeight * j - rowUpBit, paramPaint);
            	 if(i+1 <datas.length){
            		 cv.drawText(datas[i+1],  this.getWidth() / 3  +  (this.getWidth() * 2 ) / 9 * 1 +  ((this.getWidth() * 2 ) / 9 - fontPaint.measureText(datas[i+1])) / 2, rowsHeight * j - rowUpBit, paramPaint);
            	 }
            	 if(i+2 <datas.length){
            		 cv.drawText(datas[i+2],  this.getWidth() / 3  +  (this.getWidth() * 2 ) / 9 * 2 +  ((this.getWidth() * 2 ) / 9 - fontPaint.measureText(datas[i+2])) / 2, rowsHeight * j - rowUpBit, paramPaint);
            	 }
            }
			
			
		}else if(currentPage == 3){
		    int rowsHeight = this.getHeight()/ 19;
		    int rowUpBit = (rowsHeight - textSize)/2;
		    
		    String [] datas = {
		    		UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.LTECA_MACThrDL_Total)), 
		    		UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.LTECA_MACThrDL_PCell)), 
		    		UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.LTECA_MACThrDL_SCell1)), 
		    		UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.LTECA_MACThrDL_SCell2)), 
		    	                                                      
		    		UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.LTECA_PhyThrDL_Total)), 
		    		UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.LTECA_PhyThrDL_PCell)), 
		    		UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.LTECA_PhyThrDL_SCell1)), 
		    		UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.LTECA_PhyThrDL_SCell2)) 
		    };
		    for (int i = 0,j = 3; i < datas.length; i+=4,j++) {
            	cv.drawText(datas[i],  this.getWidth() / 3  +  (this.getWidth()) / 6 * 0 +  ((this.getWidth()) / 6 - fontPaint.measureText(datas[i])) / 2, rowsHeight * j - rowUpBit, paramPaint);
            	 if(i+1 <datas.length){
            		 cv.drawText(datas[i+1],  this.getWidth() / 3  +  (this.getWidth()) / 6 * 1 +  ((this.getWidth()) / 6 - fontPaint.measureText(datas[i+1])) / 2, rowsHeight * j - rowUpBit, paramPaint);
            	 }
            	 if(i+2 <datas.length){
            		 cv.drawText(datas[i+2],  this.getWidth() / 3  +  (this.getWidth()) / 6 * 2 +  ((this.getWidth()) / 6 - fontPaint.measureText(datas[i+2])) / 2, rowsHeight * j - rowUpBit, paramPaint);
            	 }
            	 if(i+3 <datas.length){
            		 cv.drawText(datas[i+3],  this.getWidth() / 3  +  (this.getWidth()) / 6 * 3 +  ((this.getWidth()) / 6 - fontPaint.measureText(datas[i+3])) / 2, rowsHeight * j - rowUpBit, paramPaint);
            	 }
            }
		   datas = new String[]{
				     getParaValue(UnifyParaID.LTECA_RBCount_Total),
				      getParaValue(UnifyParaID.LTECA_RBCount_PCell),
				     getParaValue(UnifyParaID.LTECA_RBCount_SCell1),
				     getParaValue(UnifyParaID.LTECA_RBCount_SCell2),
				                                                    
				    getParaValue(UnifyParaID.LTECA_RBCountSl_Total),
				    getParaValue(UnifyParaID.LTECA_RBCountSl_PCell),
				   getParaValue(UnifyParaID.LTECA_RBCountSl_SCell1),
				   getParaValue(UnifyParaID.LTECA_RBCountSl_SCell2),
				                                                    
				    getParaValue(UnifyParaID.LTECA_SlotCount_Total),
				    getParaValue(UnifyParaID.LTECA_SlotCount_PCell),
				   getParaValue(UnifyParaID.LTECA_SlotCount_SCell1),
				   getParaValue(UnifyParaID.LTECA_SlotCount_SCell2),
				                                                    
				                                                    
				   getParaValue(UnifyParaID.LTECA_SubFNCount_Total),
				   getParaValue(UnifyParaID.LTECA_SubFNCount_PCell),
				  getParaValue(UnifyParaID.LTECA_SubFNCount_SCell1),
				  getParaValue(UnifyParaID.LTECA_SubFNCount_SCell2),
				                                                    
				                                                    
				 getParaValue(UnifyParaID.LTECA_ScheduleRate_Total),
				 getParaValue(UnifyParaID.LTECA_ScheduleRate_PCell),
				getParaValue(UnifyParaID.LTECA_ScheduleRate_SCell1),
				getParaValue(UnifyParaID.LTECA_ScheduleRate_SCell2),
				                                                    
				  getParaValue(UnifyParaID.LTECA_TBSizeCode0_Total),
				  getParaValue(UnifyParaID.LTECA_TBSizeCode0_PCell),
				 getParaValue(UnifyParaID.LTECA_TBSizeCode0_SCell1),
				 getParaValue(UnifyParaID.LTECA_TBSizeCode0_SCell2),
				                                                    
				  getParaValue(UnifyParaID.LTECA_TBSizeCode1_Total),
				  getParaValue(UnifyParaID.LTECA_TBSizeCode1_PCell),
				 getParaValue(UnifyParaID.LTECA_TBSizeCode1_SCell1),
				 getParaValue(UnifyParaID.LTECA_TBSizeCode1_SCell2)
		    };
		    
			   for (int i = 0,j = 7; i < datas.length; i+=4,j++) {
		           	cv.drawText(datas[i],  this.getWidth() / 3  +  (this.getWidth()) / 6 * 0 +  ((this.getWidth()) / 6 - fontPaint.measureText(datas[i])) / 2, rowsHeight * j - rowUpBit, paramPaint);
		       	 if(i+1 <datas.length){
		       		 cv.drawText(datas[i+1],  this.getWidth() / 3  +  (this.getWidth()) / 6 * 1 +  ((this.getWidth()) / 6 - fontPaint.measureText(datas[i+1])) / 2, rowsHeight * j - rowUpBit, paramPaint);
		       	 }
		       	 if(i+2 <datas.length){
		       		 cv.drawText(datas[i+2],  this.getWidth() / 3  +  (this.getWidth()) / 6 * 2 +  ((this.getWidth()) / 6 - fontPaint.measureText(datas[i+2])) / 2, rowsHeight * j - rowUpBit, paramPaint);
		       	 }
		       	 if(i+3 <datas.length){
		       		 cv.drawText(datas[i+3],  this.getWidth() / 3  +  (this.getWidth()) / 6 * 3 +  ((this.getWidth()) / 6 - fontPaint.measureText(datas[i+3])) / 2, rowsHeight * j - rowUpBit, paramPaint);
		       	 }
	       }
		   
		   datas = new String[]{ 
				   "-",  
				   getParaValue(UnifyParaID.LTECA_PDCCHDLGrant_PCell),  
				   getParaValue(UnifyParaID.LTECA_PDCCHDLGrant_SCell1),  
				   getParaValue(UnifyParaID.LTECA_PDCCHDLGrant_SCell2),  
				                                                         
				    "-",  
				    getParaValue(UnifyParaID.LTECA_PDCCHULGrant_PCell),  
				   getParaValue(UnifyParaID.LTECA_PDCCHULGrant_SCell1),  
				    getParaValue(UnifyParaID.LTECA_PDCCHULGrant_SCell2)  
		   			};
		    
		   for (int i = 0,j = 16; i < datas.length; i+=4,j++) {
	           	cv.drawText(datas[i],  this.getWidth() / 3  +  (this.getWidth()) / 6 * 0 +  ((this.getWidth()) / 6 - fontPaint.measureText(datas[i])) / 2, rowsHeight * j - rowUpBit, paramPaint);
	       	 if(i+1 <datas.length){
	       		 cv.drawText(datas[i+1],  this.getWidth() / 3  +  (this.getWidth()) / 6 * 1 +  ((this.getWidth()) / 6 - fontPaint.measureText(datas[i+1])) / 2, rowsHeight * j - rowUpBit, paramPaint);
	       	 }
	       	 if(i+2 <datas.length){
	       		 cv.drawText(datas[i+2],  this.getWidth() / 3  +  (this.getWidth()) / 6 * 2 +  ((this.getWidth()) / 6 - fontPaint.measureText(datas[i+2])) / 2, rowsHeight * j - rowUpBit, paramPaint);
	       	 }
	       	 if(i+3 <datas.length){
	       		 cv.drawText(datas[i+3],  this.getWidth() / 3  +  (this.getWidth()) / 6 * 3 +  ((this.getWidth()) / 6 - fontPaint.measureText(datas[i+3])) / 2, rowsHeight * j - rowUpBit, paramPaint);
	       	 }
       }
		    
		    
			
		}else if(currentPage == 4){
			int  rowsHeight = this.getHeight()/ 22;
			int  rowUpBit = (rowsHeight - textSize)/2;
			String [] datas = {
					getParaValue(UnifyParaID.LTECA_BLER_Total),              
					getParaValue(UnifyParaID.LTECA_BLER_PCell),              
					getParaValue(UnifyParaID.LTECA_BLER_SCell1),             
					getParaValue(UnifyParaID.LTECA_BLER_SCell2),             
					                                                         
					getParaValue(UnifyParaID.LTECA_BLERCode0_Total),         
					getParaValue(UnifyParaID.LTECA_BLERCode0__PCell),        
					getParaValue(UnifyParaID.LTECA_BLERCode0__SCell1),       
					getParaValue(UnifyParaID.LTECA_BLERCode0__SCell2),       
					                                                         
					getParaValue(UnifyParaID.LTECA_BLERCode1_Total),         
					getParaValue(UnifyParaID.LTECA_BLERCode1_PCell),         
					getParaValue(UnifyParaID.LTECA_BLERCode1_SCell1),        
					getParaValue(UnifyParaID.LTECA_BLERCode1_SCell2),        
					                                                         
					getParaValue(UnifyParaID.LTECA_InitialBLER_Total),       
					getParaValue(UnifyParaID.LTECA_InitialBLER_PCell),       
					getParaValue(UnifyParaID.LTECA_InitialBLER_SCell1),      
					getParaValue(UnifyParaID.LTECA_InitialBLER_SCell2),      
					                                                         
					getParaValue(UnifyParaID.LTECA_InitialBLERCode0_Total),  
					getParaValue(UnifyParaID.LTECA_InitialBLERCode0_PCell),  
					getParaValue(UnifyParaID.LTECA_InitialBLERCode0_SCell1), 
					getParaValue(UnifyParaID.LTECA_InitialBLERCode0_SCell2), 
					                                                         
					                                                         
					getParaValue(UnifyParaID.LTECA_InitialBLERCode1_Total),  
					getParaValue(UnifyParaID.LTECA_InitialBLERCode1_PCell),  
					getParaValue(UnifyParaID.LTECA_InitialBLERCode1_SCell1), 
					getParaValue(UnifyParaID.LTECA_InitialBLERCode1_SCell2), 
					                                                         
					getParaValue(UnifyParaID.LTECA_ResidualBLER_Total),      
					getParaValue(UnifyParaID.LTECA_ResidualBLER_PCell),      
					getParaValue(UnifyParaID.LTECA_ResidualBLER_SCell1),     
					getParaValue(UnifyParaID.LTECA_ResidualBLER_SCell2),     
					                                                         
					getParaValue(UnifyParaID.LTECA_ResidualBLERCode0_Total), 
					getParaValue(UnifyParaID.LTECA_ResidualBLERCode0_PCell), 
					getParaValue(UnifyParaID.LTECA_ResidualBLERCode0_SCell1),
					getParaValue(UnifyParaID.LTECA_ResidualBLERCode0_SCell2),
					                                                         
					                                                         
					getParaValue(UnifyParaID.LTECA_ResidualBLERCode1_Total), 
					getParaValue(UnifyParaID.LTECA_ResidualBLERCode1_PCell), 
					getParaValue(UnifyParaID.LTECA_ResidualBLERCode1_SCell1),
					getParaValue(UnifyParaID.LTECA_ResidualBLERCode1_SCell2)
				};
			 for (int i = 0,j = 3; i < datas.length; i+=4,j++) {
		           	cv.drawText(datas[i],  this.getWidth() / 3  +  (this.getWidth()) / 6 * 0 +  ((this.getWidth()) / 6 - fontPaint.measureText(datas[i])) / 2, rowsHeight * j - rowUpBit, paramPaint);
		       	 if(i+1 <datas.length){
		       		 cv.drawText(datas[i+1],  this.getWidth() / 3  +  (this.getWidth()) / 6 * 1 +  ((this.getWidth()) / 6 - fontPaint.measureText(datas[i+1])) / 2, rowsHeight * j - rowUpBit, paramPaint);
		       	 }
		       	 if(i+2 <datas.length){
		       		 cv.drawText(datas[i+2],  this.getWidth() / 3  +  (this.getWidth()) / 6 * 2 +  ((this.getWidth()) / 6 - fontPaint.measureText(datas[i+2])) / 2, rowsHeight * j - rowUpBit, paramPaint);
		       	 }
		       	 if(i+3 <datas.length){
		       		 cv.drawText(datas[i+3],  this.getWidth() / 3  +  (this.getWidth()) / 6 * 3 +  ((this.getWidth()) / 6 - fontPaint.measureText(datas[i+3])) / 2, rowsHeight * j - rowUpBit, paramPaint);
		       	 }
	       }
			 
			 datas = new String[]{
					 getParaValue(UnifyParaID.LTECA_QPSK_Total),       
					 getParaValue(UnifyParaID.LTECA_QPSK_PCell),       
					 getParaValue(UnifyParaID.LTECA_QPSK_SCell1),      
					 getParaValue(UnifyParaID.LTECA_QPSK_SCell2),      
					                                                   
					 getParaValue(UnifyParaID.LTECA_16QAM_Total),      
					 getParaValue(UnifyParaID.LTECA_16QAM_PCell),      
					 getParaValue(UnifyParaID.LTECA_16QAM_SCell1),     
					 getParaValue(UnifyParaID.LTECA_16QAM_SCell2),     
					                                                   
					 getParaValue(UnifyParaID.LTECA_64QAM_Total),      
					 getParaValue(UnifyParaID.LTECA_64QAM_PCell),      
					 getParaValue(UnifyParaID.LTECA_64QAM_SCell1),     
					 getParaValue(UnifyParaID.LTECA_64QAM_SCell2),     
					                                                   
					 getParaValue(UnifyParaID.LTECA_QPSKCode0_Total),  
					 getParaValue(UnifyParaID.LTECA_QPSKCode0_PCell),  
					 getParaValue(UnifyParaID.LTECA_QPSKCode0_SCell1), 
					 getParaValue(UnifyParaID.LTECA_QPSKCode0_SCell2), 
					                                                   
					 getParaValue(UnifyParaID.LTECA_16QAMCode0_Total), 
					 getParaValue(UnifyParaID.LTECA_16QAMCode0_PCell), 
					 getParaValue(UnifyParaID.LTECA_16QAMCode0_SCell1),
					 getParaValue(UnifyParaID.LTECA_16QAMCode0_SCell2),
					                                                   
					                                                   
					 getParaValue(UnifyParaID.LTECA_64QAMCode0_Total), 
					 getParaValue(UnifyParaID.LTECA_64QAMCode0_PCell), 
					 getParaValue(UnifyParaID.LTECA_64QAMCode0_SCell1),
					 getParaValue(UnifyParaID.LTECA_64QAMCode0_SCell2),
					                                                   
					 getParaValue(UnifyParaID.LTECA_QPSKCode1_Total),  
					 getParaValue(UnifyParaID.LTECA_QPSKCode1_PCell),  
					 getParaValue(UnifyParaID.LTECA_QPSKCode1_SCell1), 
					 getParaValue(UnifyParaID.LTECA_QPSKCode1_SCell2), 
					                                                   
					                                                   
					 getParaValue(UnifyParaID.LTECA_16QAMCode1_Total), 
					 getParaValue(UnifyParaID.LTECA_16QAMCode1_PCell), 
					 getParaValue(UnifyParaID.LTECA_16QAMCode1_SCell1),
					 getParaValue(UnifyParaID.LTECA_16QAMCode1_SCell2),
					                                                   
					                                                   
					 getParaValue(UnifyParaID.LTECA_64QAMCode1_Total), 
					 getParaValue(UnifyParaID.LTECA_64QAMCode1_PCell), 
					 getParaValue(UnifyParaID.LTECA_64QAMCode1_SCell1),
					 getParaValue(UnifyParaID.LTECA_64QAMCode1_SCell2),
					                                                   
					 getParaValue(UnifyParaID.LTECA_MCSAvg_Total),     
					 getParaValue(UnifyParaID.LTECA_MCSAvg_PCell),     
					 getParaValue(UnifyParaID.LTECA_MCSAvg_SCell1),    
					 getParaValue(UnifyParaID.LTECA_MCSAvg_SCell2),   

			 };
			 for (int i = 0,j = 13; i < datas.length; i+=4,j++) {
		           	cv.drawText(datas[i],  this.getWidth() / 3  +  (this.getWidth()) / 6 * 0 +  ((this.getWidth()) / 6 - fontPaint.measureText(datas[i])) / 2, rowsHeight * j - rowUpBit, paramPaint);
		       	 if(i+1 <datas.length){
		       		 cv.drawText(datas[i+1],  this.getWidth() / 3  +  (this.getWidth()) / 6 * 1 +  ((this.getWidth()) / 6 - fontPaint.measureText(datas[i+1])) / 2, rowsHeight * j - rowUpBit, paramPaint);
		       	 }
		       	 if(i+2 <datas.length){
		       		 cv.drawText(datas[i+2],  this.getWidth() / 3  +  (this.getWidth()) / 6 * 2 +  ((this.getWidth()) / 6 - fontPaint.measureText(datas[i+2])) / 2, rowsHeight * j - rowUpBit, paramPaint);
		       	 }
		       	 if(i+3 <datas.length){
		       		 cv.drawText(datas[i+3],  this.getWidth() / 3  +  (this.getWidth()) / 6 * 3 +  ((this.getWidth()) / 6 - fontPaint.measureText(datas[i+3])) / 2, rowsHeight * j - rowUpBit, paramPaint);
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
