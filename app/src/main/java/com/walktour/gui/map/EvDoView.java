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

public class EvDoView extends BasicParamView {
    
//    private static final String tag = "EvDoView";

	private int currentPage = 1;
    
    private float rowsHeight;
    
    private float rowUpBit;
    
    private int viewHeight; 
    
    private ViewSizeLinstener viewSizeLinstener;
    
    public EvDoView(Context context) {
        super(context);
    }
    
    public EvDoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public EvDoView(Context context, int page) {
        super(context);
        this.currentPage = page;
    }
    
    public EvDoView(Context context, int page,ViewSizeLinstener viewSizeLinstener) {
        super(context);
        this.currentPage = page;
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
        CreateTableData(canvas, TraceInfoInterface.traceData);
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
        float tWidth = this.getWidth()/2;       // table 1  列宽(9等分),
        float fWidth = this.getWidth()/8;       // table F 列宽(10等分),
        
        rowUpBit = (rowsHeight - textSize)/2 ;      //指定行上升位数,为行高-字体高度 再除2
        
        String paramname;
        
        if(currentPage ==1){
            tableRows = 10;
            if(viewHeight == 0){
                viewHeight = this.getViewHeight() - 1;
            }
            rowsHeight = viewHeight / tableRows;   //行高
            rowUpBit = (rowsHeight - textSize)/2 ;
            viewSizeLinstener.onViewSizeChange(viewHeight, this.getWidth());
            
            /**表１*/
            for(int i=0;i<tableRows;i++){
                starty =  rowsHeight * (i+1);
                stopx = this.getWidth() - 1;
                stopy = rowsHeight *(i+1);
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
            }
            
            //EVDO Radio 中间竖线
            cv.drawLine(tWidth, rowsHeight*1, tWidth, rowsHeight*9, linePaint);
            

            //四周边框
            cv.drawLine(startx, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(startx, tableRows*rowsHeight , this.getWidth()-1, tableRows*rowsHeight  , linePaint);
            cv.drawLine(startx, 1, startx, tableRows*rowsHeight , linePaint);
            cv.drawLine(this.getWidth()-1, startx, this.getWidth()-1, tableRows*rowsHeight , linePaint);
            
    
            paramname = getContext().getString(R.string.evdo_title);
            cv.drawText(paramname, ( this.getWidth()-fontPaint.measureText(paramname) ) / 2 , rowsHeight-rowUpBit, fontPaint);
            
            int values[] = new int[]{R.string.evdo_frequency,R.string.evdo_band,
                    R.string.evdo_pn,R.string.evdo_uati,
                    R.string.evdo_sectorID24,R.string.evdo_servUserNum,
                    R.string.evdo_rx_agc0,R.string.evdo_rxAGC1,
                    R.string.evdo_total_sinr,R.string.evdo_txAgc,
                    R.string.evdo_drcRate,R.string.evdo_txpilotpower,
                    R.string.evdo_drc_cover,R.string.evdo_txopenlooppower,
                    R.string.evdo_a_set_count,R.string.evdo_txcloseloopadjust};
            
 
            for (int i = 0,j = 2; i < values.length; i+=2,j++) {
                String paraname;
                paraname = getContext().getString(values[i]);
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <values.length){
                    paraname = getContext().getString(values[i+1]);
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
            }
            
            cv.drawText(getResources().getString(R.string.wcdma_cell_name), marginSize , rowsHeight * 10 - rowUpBit, fontPaint);
            

        }else if(currentPage ==2){
            rowsHeight = this.getViewHeight()/tableRows;   //行高
            rowUpBit = (rowsHeight - textSize)/2 ;
            
            //EVDO System Parameter横线
            for(int i=0;i<tableRows - 6;i++){
                starty =  rowsHeight * (i+1);
                stopx = this.getWidth() - 1;
                stopy = rowsHeight *(i+1);
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
            }

            
            
            //Rev.A Reverse Info. 中间竖线
            cv.drawLine(tWidth, rowsHeight*1, tWidth, rowsHeight*6, linePaint);
            
            //EVDO State 竖线
            startx = this.getWidth() /2;
            starty =  rowsHeight * 7;
            stopx = startx;
            stopy = rowsHeight * 12;
            cv.drawLine(startx, starty, stopx, stopy, linePaint);

            
            //四周边框
            cv.drawLine(1, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(1,  rowsHeight * (tableRows -6), this.getWidth()-1,rowsHeight * (tableRows -6) , linePaint);
            cv.drawLine(1, 1, 1,  rowsHeight * (tableRows -6), linePaint);
            cv.drawLine(this.getWidth()-1, 1, this.getWidth()-1, rowsHeight * (tableRows -6), linePaint);
       
            paramname = getContext().getString(R.string.evdo_rev_reverse_info);
            cv.drawText(paramname, ( this.getWidth()-fontPaint.measureText(paramname) ) / 2 , rowsHeight * 1-rowUpBit, fontPaint);
            
            //evdo_rev_reverse_info 参数
            int values[] = new int[]{R.string.evdo_drc2pilot,R.string.evdo_txmode,
                    R.string.evdo_rri2pilot,R.string.evdo_maxT2P,
                    R.string.evdo_dsc2pilot,R.string.evdo_dsc,
                    R.string.evdo_data2pilot,R.string.evdo_txpacketsize,
                    R.string.evdo_aux2pilot,R.string.evdo_frab,
                    };
            
            for (int i = 0,j = 2; i < values.length; i+=2,j++) {
                String paraname;
                paraname = getContext().getString(values[i]);
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <values.length){
                    paraname = getContext().getString(values[i+1]);
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
            }
            
 
            paramname = getContext().getString(R.string.evdo_evdo_state);//"Serving Cell Info";
            cv.drawText(paramname,  (this.getWidth() - fontPaint.measureText(paramname)) / 2, rowsHeight * 7 - rowUpBit, fontPaint);
            
            //evdo_evdo_state-3 参数
            values = new int[]{R.string.evdo_session,R.string.evdo_at,
                    R.string.evdo_almp,R.string.evdo_init,
                    R.string.evdo_idle,R.string.evdo_connected,
                    R.string.evdo_overheadmsg,R.string.evdo_routeupdate,
                    R.string.evdo_hbridmode,R.string.evdo_session_release,
                    R.string.evdo_connection_release
                    };
            
            for (int i = 0,j = 8; i < values.length; i+=2,j++) {
                String paraname;
                paraname = getContext().getString(values[i]);
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <values.length){
                    paraname = getContext().getString(values[i+1]);
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
            }
        }else if(currentPage ==3 ){
            tableRows = 9;
            rowsHeight = this.getViewHeight()/tableRows;   //行高
            rowUpBit = (rowsHeight - textSize)/2 ;
            /**表C*/
            float fStart = rowsHeight*1;
            cv.drawLine(0, fStart, this.getWidth()-1, fStart, linePaint);
            
            for(int i=0;i<5;i++){
                starty =  fStart + rowsHeight * (i+1);
                stopx = this.getWidth() - 1;
                stopy = fStart + rowsHeight * (i+1);
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
            }
            
 
            //四周边框
            cv.drawLine(1, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(1,  rowsHeight * (tableRows-2), this.getWidth()-1,rowsHeight * (tableRows-2) , linePaint);
            cv.drawLine(1, 1, 1,  rowsHeight * (tableRows-2), linePaint);
            cv.drawLine(this.getWidth()-1, 1, this.getWidth()-1, rowsHeight * (tableRows-2), linePaint);
            
            cv.drawLine(fWidth*2, fStart, fWidth*2, fStart+rowsHeight*6 + fWidth*0, linePaint);
            cv.drawLine(fWidth*3, fStart, fWidth*3, fStart+rowsHeight*6 + fWidth*0, linePaint);
            cv.drawLine(fWidth*4, fStart, fWidth*4, fStart+rowsHeight*6 + fWidth*0, linePaint);
            cv.drawLine(fWidth*5, fStart, fWidth*5, fStart+rowsHeight*6 + fWidth*0, linePaint);
            cv.drawLine(fWidth*6, fStart, fWidth*6, fStart+rowsHeight*6 + fWidth*0, linePaint);
            cv.drawLine(fWidth*7, fStart, fWidth*7, fStart+rowsHeight*6 + fWidth*0, linePaint);
            cv.drawLine(fWidth*8, fStart, fWidth*8, fStart+rowsHeight*6 + fWidth*0, linePaint);
            cv.drawLine(fWidth*9, fStart, fWidth*9, fStart+rowsHeight*6 + fWidth*0, linePaint);
            cv.drawLine(fWidth*10, fStart, fWidth*10, fStart+rowsHeight*6 + fWidth*0, linePaint);
            
            /**表N*/
            float nStart = fStart + rowsHeight * 1 ;
            cv.drawLine(0, nStart, this.getWidth()-1, nStart, linePaint);
            for(int i=0;i<2;i++){
                starty =  nStart + rowsHeight * (i+1);
                stopx = this.getWidth() - 1;
                stopy = nStart + rowsHeight * (i+1);
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
            }
   
            
            paramname = getContext().getString(R.string.evdo_set_cell_info);
            cv.drawText(paramname, ( this.getWidth()-fontPaint.measureText(paramname) ) / 2 , rowsHeight * 1-rowUpBit, fontPaint);
            
            int[] values = new int[]{
            		R.string.evdo_freq,R.string.evdo_pn,
            		R.string.evdo_ec_io,R.string.evdo_set,R.string.evdo_drc_cover};
            for (int i = 0,j = 3; i < values.length; i++,j++) {
                cv.drawText(getResources().getString(values[i]),( fWidth*2 - fontPaint.measureText(getResources().getString(values[i])) )/2 , rowsHeight*j-rowUpBit,fontPaint );
            }
    
            values = new int[]{R.string.evdo_c1,R.string.evdo_c2,R.string.evdo_c3,
                    R.string.evdo_c4,R.string.evdo_c5,R.string.evdo_c6};
            
            for (int i = 0,j = 2; i < values.length; i++,j++) {
                cv.drawText(getResources().getString(values[i]),( fWidth - fontPaint.measureText(getResources().getString(values[i])) )/2 + fWidth*j, rowsHeight*2-rowUpBit,fontPaint );
            }
            
        }else if(currentPage == 4){
            rowsHeight = this.getViewHeight()/tableRows;   //行高
            rowUpBit = (rowsHeight - textSize)/2 ;
            //四周边框
            cv.drawLine(1, 1, this.getWidth()-1, 1, linePaint);//上边框
            cv.drawLine(1,  rowsHeight * (tableRows - 7), this.getWidth()-1,rowsHeight * (tableRows - 7) , linePaint);
            cv.drawLine(1, 1, 1,  rowsHeight * (tableRows - 7), linePaint);
            cv.drawLine(this.getWidth()-1, 1, this.getWidth()-1, rowsHeight * (tableRows - 7), linePaint);
            

            
            //EVDO Throughput 竖线
            startx = this.getWidth() /2;
            starty =  rowsHeight * 1;
            stopx = startx;
            stopy = rowsHeight * 8;
            cv.drawLine(startx, starty, stopx, stopy, linePaint);
            
            startx = this.getWidth() /2;
            starty =  rowsHeight * 9;
            stopx = startx;
            stopy = rowsHeight * 11;
            cv.drawLine(startx, starty, stopx, stopy, linePaint);
            
            //横线
            for(int i=0;i<tableRows - 6;i++){
                starty =  rowsHeight * i;
                stopx = this.getWidth() - 1;
                stopy = rowsHeight * i;
                cv.drawLine(1, starty, stopx, stopy, linePaint);
            }
            
      
            paramname = getContext().getString(R.string.evdo_evdo_throughput);//"Serving Cell Info";
            cv.drawText(paramname,  (this.getWidth() - fontPaint.measureText(paramname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
            

            
            int values[]  = new int[]{R.string.evdo_rxrlp_thr,R.string.evdo_txrlp_thr,
                    R.string.evdo_rlp_error_rate,R.string.evdo_rlp_rtx_rate,
                    R.string.evdo_rxpacket_thr,R.string.evdo_txpacket_thr,
                    R.string.evdo_rxper,R.string.evdo_txper,
                    R.string.evdo_rxsupacket_thr_ist,R.string.evdo_rxmupacket_thr_ist,
                    R.string.evdo_rxsupacket_thr,R.string.evdo_rxmupacket_thr,
                    R.string.evdo_rxsuper,R.string.evdo_rxmuper};
            
            for (int i = 0,j = 2; i < values.length; i+=2,j++) {
                String paraname;
                paraname = getContext().getString(values[i]);
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <values.length){
                    paraname = getContext().getString(values[i+1]);
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
            }
            
          //EVDO System Parameter-3参数
            paramname = getContext().getString(R.string.evdo_evdo_system_parameter);//"Serving Cell Info";
            cv.drawText(paramname,  (this.getWidth() - fontPaint.measureText(paramname)) / 2, rowsHeight * 9 - rowUpBit, fontPaint);
            values = new int[]{R.string.evdo_a_set_window,R.string.evdo_ev_revision,
            		R.string.evdo_c_set_window,R.string.evdo_pilot_inc,
            		R.string.evdo_r_set_window};
            
            for (int i = 0,j = 10; i < values.length; i+=2,j++) {
                String paraname;
                paraname = getContext().getString(values[i]);
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if(i+1 <values.length){
                    paraname = getContext().getString(values[i+1]);
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

        /**实时数据文字*/
        String text = "";
        if(currentPage == 1){
            
            //查询CellName逻辑
            if(!StringUtil.isNullOrEmpty(getParaValue(UnifyParaID.E_EV_Frequenc)) && !StringUtil.isNullOrEmpty(getParaValue(UnifyParaID.E_ServingSectorPN))){
                StringBuilder cellKey = new StringBuilder();
                cellKey.append(WalktourConst.NetWork.CDMA);
                cellKey.append("_ev_freq_").append(getParaValue(UnifyParaID.E_EV_Frequenc));
                cellKey.append("_ev_pn_").append(getParaValue(UnifyParaID.E_ServingSectorPN));
                if(traceData.getNetworkCellInfo(cellKey.toString()) != null){
                    text = traceData.getNetworkCellInfo(cellKey.toString()).getCellName();
                    cv.drawText(text, this.getWidth() -paramPaint.measureText(text) -marginSize, rowsHeight * 10 - rowUpBit, paramPaint);
                }else {
                    traceData.setNetworkCellInfo(cellKey.toString(),  new CellInfo("","",-1));
                    StringBuilder sb = new StringBuilder();
                    sb.append("ev_freq = '").append(getParaValue(UnifyParaID.E_EV_Frequenc)).append("'");
                    sb.append(" and ev_pn = '").append(getParaValue(UnifyParaID.E_ServingSectorPN)).append("'");
                    new CheckCellParamThread(this.getContext(),new String[]{"ev_freq","ev_pn","cellName","cellId","longitude","latitude"},sb.toString(), WalktourConst.NetWork.CDMA).start();
                }
            }
            text = getParaValue(UnifyParaID.E_EVsectorInfo);
            String[] values = text.split(",");
        	String[] datas = new String[]{getParaValue(UnifyParaID.E_EV_Frequenc),UtilsMethodPara.getEvdoBand(getParaValue(UnifyParaID.E_Band)),
                    getParaValue(UnifyParaID.E_ServingSectorPN),getParaValue(UnifyParaID.E_UATI),
                    values[0],getParaValue(UnifyParaID.E_DedicateUserCount),
                    getParaValue(UnifyParaID.E_Carrier1_EV_RxAGC0),getParaValue(UnifyParaID.E_Carrier1_EV_RxAGC1),
                    getParaValue(UnifyParaID.E_Carrier1_TotalSINR),getParaValue(UnifyParaID.E_Carrier1_EV_TxAGC),
                    UtilsMethod.enlargeMultiple(getParaValue(UnifyParaID.E_Carrier1_DRC_Value),0.001f),getParaValue(UnifyParaID.E_Carrier1_TxPilotPower),
                    getParaValue(UnifyParaID.E_Carrier1_DRC_Cover),getParaValue(UnifyParaID.E_Carrier1_TxOpenLoopPower),
                    getParaValue(UnifyParaID.E_ActiveCount),getParaValue(UnifyParaID.E_Carrier1_TxClosedLoopAdjust),
                    };
            for (int i = 0,j = 2; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                if(i+1 <datas.length){
                    cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }
            }
        }else if(currentPage == 2){
            String[] datas = new String[]{
            		getParaValue(UnifyParaID.E_DRC2Pilot),getParaValue(UnifyParaID.E_TxMode),
            		getParaValue(UnifyParaID.E_RRI2Pilot),getParaValue(UnifyParaID.E_MaxT2P),
            		getParaValue(UnifyParaID.E_DSC2Pilot),getParaValue(UnifyParaID.E_DSC),
            		getParaValue(UnifyParaID.E_Data2Pilot),getParaValue(UnifyParaID.E_TxPacketSize),
            		getParaValue(UnifyParaID.E_Aux2Pilot),getParaValue(UnifyParaID.E_FRAB)};
            
            for (int i = 0,j = 2; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                if(i+1 <datas.length){
                    cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }
            }
            
            //EVDO State-3数据部分
            datas = new String[]{
            		UtilsMethodPara.getEvdoSessionState(getParaValue(UnifyParaID.E_Session)), UtilsMethodPara.getEvdoATState(getParaValue(UnifyParaID.E_AT)),
            		UtilsMethodPara.getEvdoALMPState(getParaValue(UnifyParaID.E_ALMP)), UtilsMethodPara.getEvdoInitState(getParaValue(UnifyParaID.E_Init)),
            		UtilsMethodPara.getEvdoIdleState(getParaValue(UnifyParaID.E_Idle)), UtilsMethodPara.getEvdoConnectedState(getParaValue(UnifyParaID.E_Connected)),
            		UtilsMethodPara.getEvdoOverheadState(getParaValue(UnifyParaID.E_OverHeadMsg)), UtilsMethodPara.getEvdoRouteUpdateState(getParaValue(UnifyParaID.E_RouteUpdate)),
            		UtilsMethodPara.getEvdoHDRHybridModeState(getParaValue(UnifyParaID.E_HbridMode)), UtilsMethodPara.getEvdoSessionCloseCause(getParaValue(UnifyParaID.E_Session_Release)),
            		"",UtilsMethodPara.getEvodConnectionCloseCause(getParaValue(UnifyParaID.E_Connection_Release))
                    };
            
            for (int i = 0,j = 8; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                if(i+1 <datas.length){
                    cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }
            }
            
        }else if(currentPage == 3){
            float nWidth = this.getWidth()/8;       // table N 列宽(8等分),
            String[] servingNeighbor = getParaValue(UnifyParaID.E_EVServingNeighbor).split(";");
            for(int i=0; i < servingNeighbor.length - 1; i++){
                /*ActiveSetType,EvdoFreq,EvdoPn,EvdoRssi,EvdoEcIo,EvdoTotalC2I,EvdoDRCCover,LinkID,PilotGroupID,SchedTag,EvdoDummy*/
                String[] neighbor = servingNeighbor[i+1].split(",");
                text = neighbor[1];	//EvdoFreq
                cv.drawText(text, (i+2)*nWidth + ( nWidth-paramPaint.measureText(text) ) / 2 ,  rowsHeight*3 - rowUpBit  , paramPaint);
                text = neighbor[2];	//pn
                cv.drawText(text, (i+2)*nWidth + ( nWidth-paramPaint.measureText(text) ) / 2 ,  rowsHeight*4 - rowUpBit  , paramPaint);
                text = UtilsMethod.numToShowDecimal2(neighbor[4]);	//ecio
                cv.drawText(text, (i+2)*nWidth + ( nWidth-paramPaint.measureText(text) ) / 2 ,  rowsHeight*5 - rowUpBit  , paramPaint);
                text = UtilsMethodPara.getEvdoSetType(neighbor[0]);
                cv.drawText(text, (i+2)*nWidth + ( nWidth-paramPaint.measureText(text) ) / 2, rowsHeight*6 - rowUpBit  , paramPaint);
                
                text = neighbor[6];	//DRCCover
                cv.drawText(text, (i+2)*nWidth + ( nWidth-paramPaint.measureText(text) ) / 2, rowsHeight*7 - rowUpBit  , paramPaint);
            }
        }else if(currentPage == 4){//第三页的数据内容
            String[] datas = new String[]{
            		UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.E_RxRLP_Thr)), UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.E_TxRLP_Thr)),
            		getParaValue(UnifyParaID.E_RLP_Error_Rate), getParaValue(UnifyParaID.E_RLP_RTX_Rate),
            		UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.E_RxPacket_Thr)), UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.E_TxPacket_Thr)),
            		getParaValue(UnifyParaID.E_RxPER), getParaValue(UnifyParaID.E_TxPER),
            		UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.E_RxSuPacket_Thr_Ist)), UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.E_RxMuPacket_Thr_Ist)),
    				UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.E_RxSuPacket_Thr)), UtilsMethod.bps2Kbps(getParaValue(UnifyParaID.E_RxMuPacket_Thr)),
            		getParaValue(UnifyParaID.E_RxSuPER), getParaValue(UnifyParaID.E_RxMuPER)
                    };
            for (int i = 0,j = 2; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                if(i+1 <datas.length){
                    cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }
            }
            
            text = getParaValue(UnifyParaID.E_EVPilotActiveSet);
            //LogUtil.w(tag,"--aSet:" + text);
            String[] values = text.split(";")[0].split(",");
            if(values.length >= 3){
            	cv.drawText(values[2], this.getWidth()/2 -paramPaint.measureText(values[2]) - marginSize, rowsHeight * 10 - rowUpBit, paramPaint);
            	cv.drawText(values[0], this.getWidth() -paramPaint.measureText(values[0]) - marginSize, rowsHeight * 11 - rowUpBit, paramPaint);
            }
            
            text = UtilsMethodPara.getEvdoVersion(getParaValue(UnifyParaID.E_EV_Revision));
            cv.drawText(text, this.getWidth() -paramPaint.measureText(text) - marginSize, rowsHeight * 10 - rowUpBit, paramPaint);
           
            text = getParaValue(UnifyParaID.E_EVPilotCadidateSet);
            //LogUtil.w(tag,"--CSet:" + text);
            values = text.split(";")[0].split(",");
            if(values.length >= 3){
            	cv.drawText(values[2], this.getWidth()/2 -paramPaint.measureText(values[2]) - marginSize, rowsHeight * 11 - rowUpBit, paramPaint);
            }
            
            text = getParaValue(UnifyParaID.E_EVPilotNeighborSet);
            //LogUtil.w(tag,"--nSet:" + text);
            values = text.split(";")[0].split(",");
            if(values.length >= 3){
            	cv.drawText(values[2], this.getWidth()/2 -paramPaint.measureText(values[2]) - marginSize, rowsHeight * 12 - rowUpBit, paramPaint);
            }
        } else if(currentPage == 5){
            rowsHeight = this.getViewHeight() / 9;
            try {
                String[] servingNeighbor = getParaValue(UnifyParaID.E_EVServingNeighbor).split(";");
                if(servingNeighbor.length > 1){
                    float[] values = new float[servingNeighbor.length - 1];
                    float[] percentages = new float[servingNeighbor.length - 1];
                    String[] params = new String[servingNeighbor.length - 1];
                    int setType;
                    for(int i=0; i < servingNeighbor.length - 1; i++){
                        try{
                            String[] neighbor = servingNeighbor[i+1].split(",");
    	                    values[i] = Float.parseFloat(UtilsMethod.numToShowDecimal2(neighbor[4]));
    	                    percentages[i] = 100 - values[i]/-32 * 100; //ecio 0~-32
    	                    setType = Integer.parseInt(neighbor[0]);
    	                    params[i] = setType == 0 ? "A" : (setType == 1 ? "C" : "N");
    	                    //percentages[i] = values[i]/30 * 100;
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    super.createCellHistogram("Ec/Io(dBm)", params, values, percentages, rowsHeight * 8, cv);
                }
			} catch (Exception e) {
			}

        }

        
        cv.save();
        cv.restore();
    }
    
    /**获得得参数队列中指定ID的值*/
    private String getParaValue(int paraId){
    	String result = TraceInfoInterface.getParaValue(paraId);
    	//LogUtil.d(tag, Integer.toHexString(paraId).toUpperCase() + "-->" + result);
        return result;
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
