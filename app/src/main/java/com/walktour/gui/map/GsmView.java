package com.walktour.gui.map;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.UtilsMethodPara;
import com.walktour.Utils.WalktourConst;
import com.walktour.framework.view.BasicParamView;
import com.walktour.framework.view.CheckCellParamThread;
import com.walktour.framework.view.ViewSizeLinstener;
import com.walktour.gui.R;
import com.walktour.model.CellInfo;

public class GsmView extends BasicParamView {
    
//    private static String TAG = "GsmView";
    
    int tableCols = 6; //列数
    
    int tableColsSec = 4; //第二页行数
    
    float strokeWidth = 1;
    
    private float rowsHeight;
    
    private int currentPage = 1;
    
//    private ViewSizeLinstener viewSizeLinstener;
    
	private StringBuffer buffer = null; 
	
	private StringBuffer buffer2 = null;
    
    private int viewHeight;
	
	public GsmView(Context context) {
		super(context);
	}
	public GsmView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
    public GsmView(Context context,int page ,ViewSizeLinstener viewSizeLinstener) {
        super(context);
        this.currentPage = page;
//        this.viewSizeLinstener = viewSizeLinstener;
    }
	
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @see com.walktour.framework.view.BasicParamView#initView()
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
		float startx =0;
		float starty =0;
		float stopx =0;
		float stopy =0;
		float colsWidth = (this.getWidth() - 1)/tableCols;		//列宽
		float neiColsWith = (this.getWidth() - colsWidth - 1) / tableCols;//邻近单元C*宽度
		float rowUpBit = (rowsHeight - textSize)/2 ;		//指定行上升位数,为行高-字体高度 再除2
		
		if(currentPage ==1){
		    tableRows = 19;
		    if(viewHeight == 0){
		        viewHeight = this.getViewHeight() - 1;
		    }
//		    viewSizeLinstener.onViewSizeChange(viewHeight, this.getWidth());
		    rowsHeight = viewHeight /tableRows;
		    rowUpBit = (rowsHeight - textSize)/2 ;       //指定行上升位数,为行高-字体高度 再除2
			//四周边框
		    cv.drawLine(1, 1, this.getWidth()-1, 1, linePaint);
            //cv.drawLine(1,  rowsHeight * tableRows - 8, this.getWidth()-1,rowsHeight * (tableRows - 8) , linePaint);
            cv.drawLine(1, 1, 1,  rowsHeight * (tableRows - 8), linePaint);
            cv.drawLine(this.getWidth()-1, 1, this.getWidth()-1, rowsHeight * (tableRows - 8), linePaint);
			//横线
			for(int i=0;i<tableRows - 8;i++){
				startx = 0;
				starty =  rowsHeight * (i+1);
				stopx = this.getWidth() - 1;
				stopy = rowsHeight *(i+1);
				cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
			
			
			//Serving cell 分页竖线
            startx = this.getWidth() /2;
            starty =  rowsHeight * 1;
            stopx = startx;
            stopy = rowsHeight * 10 ;
            cv.drawLine(startx, starty, stopx, stopy, linePaint);
            
            String paraname;
            
            paraname = getContext().getString(R.string.gsm_servingCell);//"Serving Cell Info";
            cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
            
            int[] values = new int[]{R.string.gsm_mcc_mnc,R.string.gsm_lac,
                    R.string.gsm_servingBCCHARFCN,R.string.gsm_cellid,
                    R.string.gsm_bsic,R.string.gsm_vcodec,
                    R.string.gsm_servingRxLev,R.string.gsm_timeslotNumber,
                    R.string.gsm_txPower,R.string.gsm_rlt,
                    R.string.gsm_ta,R.string.gsm_tchCtoI,
                    R.string.gsm_rxLevFull,R.string.gsm_rxQualFull,
                    R.string.gsm_rxLevSub,R.string.gsm_rxQualSub,
                    R.string.gsm_dtx,R.string.gsm_state
                    ,R.string.gsm_cellName
                    };
            
            for (int i = 0,j = 2; i < values.length; i+=2,j++) {
                paraname = getContext().getString(values[i]);
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <values.length){
                    paraname = getContext().getString(values[i+1]);
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
            }
			
		}else if(currentPage == 2){
		    tableRows = 8;
	        rowsHeight = this.getViewHeight() /tableRows;
            rowUpBit = (rowsHeight - textSize)/2 ;       //指定行上升位数,为行高-字体高度 再除2
            //四周边框
            cv.drawLine(1, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(1,  rowsHeight * (tableRows-1), this.getWidth()-1,rowsHeight * (tableRows-1) , linePaint);
            cv.drawLine(1, 1, 1,  rowsHeight * (tableRows-1), linePaint);
            cv.drawLine(this.getWidth()-1, 1, this.getWidth()-1, rowsHeight * (tableRows-1), linePaint);
		    
	          //横线
            for(int i=0;i<tableRows -1;i++){
                startx = 1;
                starty =  rowsHeight * (i+1);
                stopx = this.getWidth() - 1;
                stopy = rowsHeight *(i+1);
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
            }
		    
		    //Neighbor Cell竖线
            for(int i=0;i<tableCols ;i++){
                startx = colsWidth + neiColsWith * (i);
                starty =  rowsHeight * 1;
                stopx = colsWidth + neiColsWith* (i);
                stopy = rowsHeight * 7 ;
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
            }

            String paraname;
            //neighbor cell info
            paraname = getContext().getString(R.string.gsm_neighborCell);//"Serving Cell Info";
            cv.drawText(paraname,  (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.gsm_servingBCCHARFCN);
            cv.drawText(paraname, colsWidth * 0 + (colsWidth - fontPaint.measureText(paraname)) / 2, rowsHeight * 2 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.gsm_bsic);
            cv.drawText(paraname, colsWidth * 0 + (colsWidth - fontPaint.measureText(paraname)) / 2, rowsHeight * 3 - rowUpBit, fontPaint);
             paraname = getContext().getString(R.string.gsm_cellid);
                cv.drawText(paraname, colsWidth * 0 + (colsWidth - fontPaint.measureText(paraname)) / 2, rowsHeight * 4 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.gsm_rxLev);
            cv.drawText(paraname, colsWidth * 0 + (colsWidth - fontPaint.measureText(paraname)) / 2, rowsHeight * 5 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.gsm_servingC1);
            cv.drawText(paraname, colsWidth * 0 + (colsWidth - fontPaint.measureText(paraname)) / 2, rowsHeight * 6 - rowUpBit, fontPaint);
            paraname = getContext().getString(R.string.gsm_servingC2);
            cv.drawText(paraname, colsWidth * 0 + (colsWidth - fontPaint.measureText(paraname)) / 2, rowsHeight * 7 - rowUpBit, fontPaint);
        }else if (currentPage ==3) {
            
        }else if(currentPage ==4){
            tableRows = 19;
            rowsHeight = (this.getViewHeight()-1 ) /tableRows;
            rowUpBit = (rowsHeight - textSize)/2 ;       //指定行上升位数,为行高-字体高度 再除2
            //四周边框
            cv.drawLine(1, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(1, rowsHeight * tableRows, this.getWidth()-1, rowsHeight * tableRows , linePaint);
            cv.drawLine(1, 1, 1, rowsHeight * tableRows, linePaint);
            cv.drawLine(this.getWidth()-1, 1, this.getWidth()-1, rowsHeight * tableRows, linePaint);
			//横线
			for(int i=0;i<tableRows;i++){
				startx = 0;
				starty =  rowsHeight * (i+1);
				stopx = this.getWidth() - 1;
				stopy = rowsHeight *(i+1);
				cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
			//Serving cell 分页竖线
			
			startx = this.getWidth() /2;
			starty =  rowsHeight * 1;
			stopx = startx;
			stopy = rowsHeight * 10;
			cv.drawLine(startx, starty, stopx, stopy, linePaint);
			
			
			startx = this.getWidth() /2;
			starty =  rowsHeight * 11;
			stopx = startx;
			stopy = rowsHeight * 12;
			cv.drawLine(startx, starty, stopx, stopy, linePaint);
			
			startx = this.getWidth() /2;
			starty =  rowsHeight * 13;
			stopx = startx;
			stopy = rowsHeight * 16;
			cv.drawLine(startx, starty, stopx, stopy, linePaint);
			
			startx = this.getWidth() /2;
			starty =  rowsHeight * 18;
			stopx = startx;
			stopy = rowsHeight * 19;
			cv.drawLine(startx, starty, stopx, stopy, linePaint);
	
			String paraname;
			//DownLink Trch Bler Meas.Info
			paraname = getContext().getString(R.string.gsm_servingSystemCell);
			cv.drawText(paraname,  (this.getWidth()- fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
			
			paraname = "Voice Codec";
			cv.drawText(paraname,  (this.getWidth()- fontPaint.measureText(paraname)) / 2, rowsHeight * 11 - rowUpBit, fontPaint);
			
			paraname = "System State";
			cv.drawText(paraname,  (this.getWidth()- fontPaint.measureText(paraname)) / 2, rowsHeight * 13 - rowUpBit, fontPaint);
			
			int values[] = new int[]{R.string.gsm_hsn,R.string.gsm_maio,
			        R.string.gsm_cellReselectHysteresis,R.string.gsm_t3212,
			        R.string.gsm_cellReselectionOffset,R.string.gsm_max_retransmitted,
			        R.string.gsm_to,R.string.gsm_rx_level_access_min,
			        R.string.gsm_pt,R.string.gsm_ms_tx_power_max_cch};
            for (int i = 0,j = 2; i < values.length; i+=2,j++) {
                paraname = getContext().getString(values[i]);
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <values.length){
                    paraname = getContext().getString(values[i+1]);
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
            }
            String  [] valueArr = new String[]{getContext().getString(R.string.gsm_ferFull),getContext().getString(R.string.gsm_ferSub),
            									"Dedicated ARFCN","Channels Num",
            									"CCCH CONF","CCCH Combined","Channel Count","Config On BCCH PCH"};
            for (int i = 0,j = 7; i < valueArr.length; i+=2,j++) {
            	 paraname = valueArr[i];
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <valueArr.length){
                	paraname = valueArr[i+1];
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
            }
            
            
            valueArr = new String[]{"Channel type","Channel mode"};
            for (int i = 0,j = 12; i < valueArr.length; i+=2,j++) {
                paraname = valueArr[i];
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <valueArr.length){
                    paraname = valueArr[i+1];
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
            }
            
            valueArr  = new String[]{"GPRS Support","EDGE Support",
					            		"Attach State","Service State",
					            		"RLC Mode","NMO"
					            		};
            for (int i = 0,j = 14; i < valueArr.length; i+=2,j++) {
                paraname = valueArr[i];
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <valueArr.length){
                    paraname = valueArr[i+1];
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
            }
            
            paraname = "GRR State";
            cv.drawText(paraname, marginSize , rowsHeight * 17 - rowUpBit, fontPaint);
            
            paraname = "GMM State";
            cv.drawText(paraname, marginSize , rowsHeight * 18 - rowUpBit, fontPaint);
            
            valueArr = new String[]{"GPRS Class","EDGE MultiSlot Class"};
            for (int i = 0,j = 19; i < valueArr.length; i+=2,j++) {
                paraname = valueArr[i];
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <valueArr.length){
                    paraname = valueArr[i+1];
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
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
	    tableRows = 19;
		float rowsHeight = this.getViewHeight()/tableRows;	//行高
		float colsWidth = this.getWidth()/tableCols;		//列宽
		float neiColsWith = (this.getWidth() - colsWidth) / tableCols;//邻近单元C*宽度
		float rowUpBit = (rowsHeight - textSize)/2 ;		//指定行上升位数,为行高-字体高度 再除2
		
		if(currentPage ==1){
		    tableRows = 19;
		    rowsHeight = this.getViewHeight()/tableRows;   //行高
		    rowUpBit = (rowsHeight - textSize)/2;
			
			StringBuilder cellKey = new StringBuilder();
			cellKey.append(WalktourConst.NetWork.GSM);
      cellKey.append("_bcch_").append(getParaValue(UnifyParaID.G_Ser_BCCH));
      cellKey.append("_bsic_").append(getParaValue(UnifyParaID.G_Ser_BSIC));
		    
			String[] datas = new String[]{getParaValue(UnifyParaID.G_Ser_MCC).equals("") && getParaValue(UnifyParaID.G_Ser_MNC).equals("")?"":getParaValue(UnifyParaID.G_Ser_MCC)+"/"+getParaValue(UnifyParaID.G_Ser_MNC),getParaValue(UnifyParaID.G_Ser_LAC),
					getParaValue(UnifyParaID.G_Ser_BCCH),getParaValue(UnifyParaID.G_Ser_Cell_ID),
					getParaValue(UnifyParaID.G_Ser_BSIC),UtilsMethodPara.getGsmVCodec(getParaValue(UnifyParaID.G_Ser_V_Codec)),
					getParaValue(UnifyParaID.G_Ser_BCCHLev),(getParaValue(UnifyParaID.G_Ser_TS).equals("-9999") ? "" : getParaValue(UnifyParaID.G_Ser_TS)),
					getParaValue(UnifyParaID.G_Ser_TxPower),getParaValue(UnifyParaID.G_Ser_RLT),
					getParaValue(UnifyParaID.G_Ser_TA),getParaValue(UnifyParaID.G_Ser_TCH_C2I),
					getParaValue(UnifyParaID.G_Ser_RxLevFull),getParaValue(UnifyParaID.G_Ser_RxQualFull),
					getParaValue(UnifyParaID.G_Ser_RxLevSub),getParaValue(UnifyParaID.G_Ser_RxQualSub),
					getParaValue(UnifyParaID.G_Ser_DTX),UtilsMethodPara.getGsmRRStateStr(getParaValue(UnifyParaID.G_Ser_State))
			        };
			
            for (int i = 0,j = 2; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                if(i+1 <datas.length){
                    cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }
            }
            String cellnameString = traceData.getNetworkCellInfo(cellKey.toString()) == null ? "" : traceData.getNetworkCellInfo(cellKey.toString()).getCellName();
            cv.drawText(cellnameString, this.getWidth() -paramPaint.measureText(cellnameString) -marginSize, rowsHeight * 11 - rowUpBit, paramPaint);
			

		}else if(currentPage == 2){
            String[] datas = new String[]{
					getParaValue(UnifyParaID.G_NCell_N1_BCCH),
					getParaValue(UnifyParaID.G_NCell_N2_BCCH),
					getParaValue(UnifyParaID.G_NCell_N3_BCCH),
					getParaValue(UnifyParaID.G_NCell_N4_BCCH),
					getParaValue(UnifyParaID.G_NCell_N5_BCCH),
					getParaValue(UnifyParaID.G_NCell_N6_BCCH),
					
					getParaValue(UnifyParaID.G_NCell_N1_BSIC),
					getParaValue(UnifyParaID.G_NCell_N2_BSIC),
					getParaValue(UnifyParaID.G_NCell_N3_BSIC),
					getParaValue(UnifyParaID.G_NCell_N4_BSIC),
					getParaValue(UnifyParaID.G_NCell_N5_BSIC),
					getParaValue(UnifyParaID.G_NCell_N6_BSIC),
					
					getCellIDByPara(traceData,getParaValue(UnifyParaID.G_NCell_N1_BCCH),getParaValue(UnifyParaID.G_NCell_N1_BSIC)),
					getCellIDByPara(traceData,getParaValue(UnifyParaID.G_NCell_N2_BCCH),getParaValue(UnifyParaID.G_NCell_N2_BSIC)),
					getCellIDByPara(traceData,getParaValue(UnifyParaID.G_NCell_N3_BCCH),getParaValue(UnifyParaID.G_NCell_N3_BSIC)),
					getCellIDByPara(traceData,getParaValue(UnifyParaID.G_NCell_N4_BCCH),getParaValue(UnifyParaID.G_NCell_N4_BSIC)),
					getCellIDByPara(traceData,getParaValue(UnifyParaID.G_NCell_N5_BCCH),getParaValue(UnifyParaID.G_NCell_N5_BSIC)),
					getCellIDByPara(traceData,getParaValue(UnifyParaID.G_NCell_N6_BCCH),getParaValue(UnifyParaID.G_NCell_N6_BSIC)),
					
					getParaValue(UnifyParaID.G_NCell_N1_RxLevel),
					getParaValue(UnifyParaID.G_NCell_N2_RxLevel),
					getParaValue(UnifyParaID.G_NCell_N3_RxLevel),
					getParaValue(UnifyParaID.G_NCell_N4_RxLevel),
					getParaValue(UnifyParaID.G_NCell_N5_RxLevel),
					getParaValue(UnifyParaID.G_NCell_N6_RxLevel),
					
					getParaValue(UnifyParaID.G_NCell_N1_C1),
					getParaValue(UnifyParaID.G_NCell_N2_C1),
					getParaValue(UnifyParaID.G_NCell_N3_C1),
					getParaValue(UnifyParaID.G_NCell_N4_C1),
					getParaValue(UnifyParaID.G_NCell_N5_C1),
					getParaValue(UnifyParaID.G_NCell_N6_C1),
					
					getParaValue(UnifyParaID.G_NCell_N1_C2),
					getParaValue(UnifyParaID.G_NCell_N2_C2),
					getParaValue(UnifyParaID.G_NCell_N3_C2),
					getParaValue(UnifyParaID.G_NCell_N4_C2),
					getParaValue(UnifyParaID.G_NCell_N5_C2),
					getParaValue(UnifyParaID.G_NCell_N6_C2)
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
            	String bcch=getParaValue(UnifyParaID.G_Ser_BCCH);
            	String bsic=getParaValue(UnifyParaID.G_Ser_BSIC);
                String cellKey = new StringBuffer().append(WalktourConst.NetWork.GSM)
                        .append("_").append(bcch).append("_").append(bsic).toString();
                if(traceData.getNetworkCellInfo(cellKey) == null && !StringUtil.isNullOrEmpty(bcch)
                        && !StringUtil.isNullOrEmpty(bsic)){
                    buffer.append("," + bcch);
                    buffer2.append("," + bsic);
                }
                buffer.append(") and ").append(buffer2).append(")");
                new CheckCellParamThread(this.getContext(),new String[]{"bcch","bsic","cellName","cellId","longitude","latitude"},buffer.toString(), WalktourConst.NetWork.GSM).start();
            }
            buffer = null;
            buffer2 = null;
		}else if(currentPage == 3){
	        rowsHeight = this.getViewHeight() / 8;
	        float[] values = new float[]{
            		UtilsMethod.StringToFloat(getParaValue(UnifyParaID.G_NCell_N1_RxLevel),-9999),
            		UtilsMethod.StringToFloat(getParaValue(UnifyParaID.G_NCell_N2_RxLevel),-9999),
            		UtilsMethod.StringToFloat(getParaValue(UnifyParaID.G_NCell_N3_RxLevel),-9999),
            		UtilsMethod.StringToFloat(getParaValue(UnifyParaID.G_NCell_N4_RxLevel),-9999),
            		UtilsMethod.StringToFloat(getParaValue(UnifyParaID.G_NCell_N5_RxLevel),-9999),
            		UtilsMethod.StringToFloat(getParaValue(UnifyParaID.G_NCell_N6_RxLevel),-9999)
            };
            float[] percentages = new float[6];
                for(int i=0;i<values.length;i++){
                    percentages[i] = 100 - (values[i]/-120 * 100) + 30/120 * 100;
                }
                super.createCellHistogram("RxLev(dBm)", null, values, percentages, rowsHeight * 7, cv);
		}else if(currentPage == 4){
            tableRows = 19;
            rowsHeight = this.getViewHeight() /tableRows;
            rowUpBit = (rowsHeight - textSize)/2 ;       //指定行上升位数,为行高-字体高度 再除2
            
			String[] datas = new String[]{getParaValue(UnifyParaID.G_SYS_HSN),getParaValue(UnifyParaID.G_SYS_MAIO),
					getParaValue(UnifyParaID.G_SYS_CR_Hysteresis),getParaValue(UnifyParaID.G_SYS_T3212),
					getParaValue(UnifyParaID.G_SYS_CR_offset),getParaValue(UnifyParaID.G_SYS_Max_Retransmitted),
					getParaValue(UnifyParaID.G_SYS_TO),getParaValue(UnifyParaID.G_SYS_RX_Level_Access_Min),
					getParaValue(UnifyParaID.G_SYS_PT),getParaValue(UnifyParaID.G_SYS_MS_TX_Power_Max_CCH),/*,
			        "CellBar","Reestablish",
			        "MFRMS",models.getAttachAllowed()*/
					getParaValue(UnifyParaID.G_Ser_FerFull),getParaValue(UnifyParaID.G_Ser_FerSub),
					getParaValue(UnifyParaID.G_Dedicated_ARFCN),getParaValue(UnifyParaID.G_Channels_Num),
					getParaValue(UnifyParaID.G_CCCH_CONF),getParaValue(UnifyParaID.G_CCCH_Combined),
					getParaValue(UnifyParaID.G_Channel_Count),"BCCH"
					};
			
            for (int i = 0,j = 2; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                if(i+1 <datas.length){
                    cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }
            }
           
            datas = new String[]{  UtilsMethodPara.getGSMChannelType(getParaValue(UnifyParaID.G_Channel_Type)), UtilsMethodPara.getGSMChannelMode(getParaValue(UnifyParaID.G_Channel_Mode))};
			
            for (int i = 0,j = 12; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                if(i+1 <datas.length){
                    cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }
            }
            
            datas = new String[]{  UtilsMethodPara.getGSMGPRSSupport(getParaValue(UnifyParaID.G_GPRS_Support)), UtilsMethodPara.getGSMEGPRSSupport(getParaValue(UnifyParaID.G_EGPRS_Support)),
            			getParaValue(UnifyParaID.G_Attach_State), UtilsMethodPara.getGsmServiceState(getParaValue(UnifyParaID.G_Service_State)),
            						UtilsMethodPara.getRlcMode(getParaValue(UnifyParaID.G_RLC_Mode)), UtilsMethodPara.getNMO(getParaValue(UnifyParaID.G_NMO)) 
            					};
            for (int i = 0,j = 14; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                if(i+1 <datas.length){
                    cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }
            }
            
           datas = new String[]{UtilsMethodPara.getGSMGRRState(getParaValue(UnifyParaID.G_GRR_State)) ,UtilsMethodPara.getGSMGMMState(getParaValue(UnifyParaID.G_GMM_State))};
						
				for (int i = 0,j = 17; i < datas.length; i++,j++) {
							cv.drawText(datas[i], this.getWidth() - paramPaint.measureText(datas[i]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
					}		
				datas = new String[]{getParaValue(UnifyParaID.G_GPRS_Support).equals("") ? "" : "12",
									getParaValue(UnifyParaID.G_GPRS_Support).equals("") ? "":"12"};
            for (int i = 0,j = 19; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                if(i+1 <datas.length){
                    cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }
            }	
				

		}
		
		cv.save();
		cv.restore();
	}
	
	private String getCellIDByPara(TraceInfoData traceData,String bcch,String bsic){
		String value = "";
    	String cellidKey = WalktourConst.NetWork.GSM + "_bcch_" + bcch
                +"_bsic_" + bsic;
        if(traceData.containsCellIDHmKey(cellidKey)){
            value = traceData.getNetworkCellInfo(cellidKey).getCellId(); 
        }else {
            value = "";
            traceData.setNetworkCellInfo(cellidKey, new CellInfo("","",-1));
            if(buffer == null){
                if(!StringUtil.isNullOrEmpty(bcch)){
                    buffer = new StringBuffer();
                    buffer.append("bcch in(" + bcch);
                }
            }else {
                if(!StringUtil.isNullOrEmpty(bcch)){
                    buffer.append(","+ bcch);
                }
            }
            if(buffer2 == null){
                if(!StringUtil.isNullOrEmpty(bsic)){
                    buffer2 = new StringBuffer();
                    buffer2.append("bsic in(" + bsic);
                }
            }else {
                if(!StringUtil.isNullOrEmpty(bsic)){
                    buffer2.append(","+ bsic);
                }
                
            }
            
        }
        
        return value;
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
