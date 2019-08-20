package com.walktour.gui.map;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
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

public class CdmaView extends BasicParamView {
    
    private int currentPage = 1;
    
    int tableCols = 6;      //列数
    
    int tableColsSec = 4;       //第二页行数
    
    private float rowUpBit;
    
    private float rowsHeight;
    
    private int viewHeight; 
    
    private ViewSizeLinstener viewSizeLinstener;
    
    public CdmaView(Context context) {
        super(context);
    }
    
    public CdmaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public CdmaView(Context context, int page) {
            super(context);
            this.currentPage = page;
     }
    
    public CdmaView(Context context, int page, ViewSizeLinstener viewSizeLinstener) {
           super(context);
           this.currentPage = page;
           this.viewSizeLinstener = viewSizeLinstener ;
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
        float startx =0;
        float starty =0;
        float stopx =0;
        float stopy =0;
        float colsWidth = this.getWidth()/tableCols;        //列宽
        rowUpBit = (rowsHeight - textSize)/2 ;      //指定行上升位数,为行高-字体高度 再除2
        
        
        String paraname;
        if(currentPage ==1){
            tableRows = 12;
            if(viewHeight == 0){
                viewHeight = this.getViewHeight() - 1;
            }
            rowsHeight = viewHeight /tableRows;  //行高
            rowUpBit = (rowsHeight - textSize)/2;
            viewSizeLinstener.onViewSizeChange(viewHeight, this.getWidth());
            
             //四周边框
            cv.drawLine(1, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(1,  rowsHeight * tableRows, this.getWidth()-1,rowsHeight * tableRows , linePaint);
            cv.drawLine(1, 1, 1,  rowsHeight * tableRows, linePaint);
            cv.drawLine(this.getWidth()-1, 1, this.getWidth()-1, rowsHeight * tableRows, linePaint);
            
            //横线
            for(int i=0;i<tableRows-1;i++){
                startx = 0;
                starty =  rowsHeight * (i+1);
                stopx = this.getWidth() - 1;
                stopy = rowsHeight *(i+1);
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
            }

            //CDMA Info 信息竖线
            cv.drawLine(this.getWidth()/2, rowsHeight*1, this.getWidth()/2, rowsHeight*10, linePaint);
            
            //TODO 2012.8.14 版本有变 暂时隐藏
/*            for(int i=1;i<=5;i++){
                cv.drawLine(colsWidth+i*fWidth , rowsHeight*18, colsWidth+i*fWidth, rowsHeight*24, paint);
            }*/
            
            
            paraname = getContext().getString(R.string.cdma_title);
            cv.drawText(paraname,  (this.getWidth() -marginSize - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
            
            int[] values = new int[]{R.string.cdma_frequancy,R.string.cdma_sid,
                    R.string.cdma_Referpn,R.string.cdma_nid,
                    R.string.cdma_refer_ecio,R.string.cdma_bid,
                    R.string.cdma_refer_ec,R.string.cdma_max_pn,
                    R.string.cdma_total_ecio,R.string.cdma_max_ecio,
                    R.string.cdma_total_ec,R.string.cdma_max_ec,
                    R.string.cdma_rxagc,R.string.cdma_ffer,
                    R.string.cdma_txagc,R.string.cdma_txgainadj,
                    R.string.cdma_txpower,R.string.cdma_a_set_num,
                    R.string.cdma_state
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
            cv.drawText(paraname, marginSize , rowsHeight * 12 - rowUpBit, fontPaint);
            
        }else if(currentPage ==2){
             
            tableRows = 6;
            rowsHeight = this.getViewHeight()/tableRows;  //行高
            rowUpBit = (rowsHeight - textSize)/2 ; 
            
            //四周边框
            cv.drawLine(1, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(1,  rowsHeight * (tableRows-1), this.getWidth()-1,rowsHeight * (tableRows-1) , linePaint);
            cv.drawLine(1, 1, 1,  rowsHeight * (tableRows-1), linePaint);
            cv.drawLine(this.getWidth()-1, 1, this.getWidth()-1, rowsHeight * (tableRows-1), linePaint);
            
            //横线
            for(int i=0;i<tableRows -1 ;i++){
                startx = 1;
                starty =  rowsHeight * (i+1);
                stopx = this.getWidth() - 1;
                stopy = rowsHeight *(i+1);
                cv.drawLine(startx, starty, stopx, stopy, linePaint);
            }

            //cv.drawLine(colsWidth*5, rowsHeight*1, colsWidth*5, rowsHeight*6, paint);
            cv.drawLine(colsWidth*2, rowsHeight*10, colsWidth*2, rowsHeight*11, linePaint);
            //邻小区信息竖线
            cv.drawLine(colsWidth*1,rowsHeight*12,colsWidth*1,rowsHeight*17,linePaint);
            //cv.drawLine(colsWidth*1,rowsHeight*18,colsWidth*1,rowsHeight*24,paint);
            float fWidth = ( this.getWidth() - colsWidth )/6;
            //set cell info
            for(int i=0;i<=5;i++){
                cv.drawLine(colsWidth+i*fWidth , rowsHeight*1, colsWidth+i*fWidth, rowsHeight*5, linePaint);
            }

            
            paraname = getContext().getString(R.string.cdma_set_cell_info);
            cv.drawText(paraname,  (this.getWidth() -marginSize - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit, fontPaint);
            
            int[] values = new int[]{R.string.cdma_frequancy,R.string.cdma_pn,R.string.cdma_ecio,R.string.cdma_ec};
            for (int i = 0,j = 2; i < values.length; i++,j++) {
                paraname = getResources().getString(values[i]);
                cv.drawText(paraname,  (fWidth - fontPaint.measureText(paraname)) / 2 + marginSize, rowsHeight * j - rowUpBit, fontPaint);
            }
            
        }else if(currentPage ==3){
            
        }else if(currentPage ==4){
            rowsHeight = this.getViewHeight()/tableRows;  //行高
            rowUpBit = (rowsHeight - textSize)/2 ; 
            
            //四周边框
            cv.drawLine(1, 1, this.getWidth()-1, 1, linePaint);
            cv.drawLine(1,  rowsHeight * (tableRows - 12), this.getWidth()-1,rowsHeight * (tableRows - 12) , linePaint);
            cv.drawLine(1, 1, 1,  rowsHeight * (tableRows - 12), linePaint);
            cv.drawLine(this.getWidth()-1, 1, this.getWidth()-1, rowsHeight * (tableRows - 12), linePaint);
           
           //横线
            for(int i=0;i<tableRows - 12;i++){
               startx = 1;
               starty =  rowsHeight * (i+1);
               stopx = this.getWidth() - 1;
               stopy = rowsHeight *(i+1);
               cv.drawLine(startx, starty, stopx, stopy, linePaint);
            }
            
            //System Parameter竖线
           startx = this.getWidth() /2 ;
           starty = rowsHeight * 1;
           stopx = startx;
           stopy = rowsHeight * 7;
           cv.drawLine(startx, starty, stopx, stopy, linePaint);
           

            paraname = getContext().getString(R.string.cdma_system_parameter);
            cv.drawText(paraname, (this.getWidth()-fontPaint.measureText(paraname))/2, rowsHeight * 1 - rowUpBit, fontPaint);
            
            int[] paranames = new int[]{R.string.cdma_srch_win_a,R.string.cdma_pilot_inc,
                        R.string.cdma_srch_win_n,R.string.cdma_t_add,
                        R.string.cdma_srch_win_r,R.string.cdma_t_comp,
                        R.string.cdma_soft_slope,R.string.cdma_t_drop,
                        R.string.cdma_ec_threshold,R.string.cdma_neighbor_max_age,
                        R.string.cdma_band ,R.string.cdma_esn
                        };
            
            for (int i = 0,j = 2; i < paranames.length; i+=2,j++) {
                paraname = getContext().getString(paranames[i]);
                cv.drawText(paraname, marginSize , rowsHeight * j - rowUpBit, fontPaint);
                if((i+1) < paranames.length){
                    paraname = getContext().getString(paranames[i+1]);
                    cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * j - rowUpBit, fontPaint);
                }
                
            }
        }else if(currentPage ==5) {
            
          rowsHeight = this.getViewHeight()/20;   //行高
            /**四周边框*/
          cv.drawLine(1, 1, this.getWidth()-1, 1, linePaint);
          //cv.drawLine(0, marginSize + rowsHeight * 15, this.getWidth()-marginSize, marginSize + rowsHeight * 15 , linePaint);
          cv.drawLine(1, 1, 0, rowsHeight * 19, linePaint);
          cv.drawLine(this.getWidth()-1, 0, this.getWidth()-1, rowsHeight * 19, linePaint);
          /**横线*/
          for(int i=0;i<19;i++){
              startx = 1;
              starty =  rowsHeight * (i+1);
              stopx = this.getWidth() - 1;
              stopy = rowsHeight *(i+1);
              cv.drawLine(startx, starty, stopx, stopy, linePaint);
          }
         //System Parameter竖线
          startx = this.getWidth() /2 ;
          starty = rowsHeight * 1;
          stopx = startx;
          stopy = rowsHeight * 8;
          cv.drawLine(startx, starty, stopx, stopy, linePaint);
          
          //Access Parameter竖线
          startx = this.getWidth() /2 ;
          starty = rowsHeight * 9;
          stopx = startx;
          stopy = rowsHeight * 15;
          cv.drawLine(startx, starty, stopx, stopy, linePaint);
          
          //Power Control竖线
          startx = this.getWidth() /2 ;
          starty = rowsHeight * 16;
          stopx = startx;
          stopy = rowsHeight * 19;
          cv.drawLine(startx, starty, stopx, stopy, linePaint);
          
          paraname = getContext().getString(R.string.cdma_system_parameter);
          cv.drawText(paraname, (this.getWidth()-fontPaint.measureText(paraname))/2, rowsHeight * 1 - rowUpBit, fontPaint);
          
          paraname = getContext().getString(R.string.cdma_srch_win_a);
          cv.drawText(paraname, marginSize , rowsHeight * 2 - rowUpBit, fontPaint);
          paraname = getContext().getString(R.string.cdma_pilot_inc);
          cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 2 - rowUpBit, fontPaint);
          
          paraname = getContext().getString(R.string.cdma_srch_win_n);
          cv.drawText(paraname, marginSize , rowsHeight * 3 - rowUpBit, fontPaint);
          paraname = getContext().getString(R.string.cdma_t_add);
          cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 3 - rowUpBit, fontPaint);
          
          paraname = getContext().getString(R.string.cdma_srch_win_r);
          cv.drawText(paraname, marginSize , rowsHeight * 4 - rowUpBit, fontPaint);
          paraname = getContext().getString(R.string.cdma_t_comp);
          cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 4 - rowUpBit, fontPaint);
          
          paraname = getContext().getString(R.string.cdma_soft_slope);
          cv.drawText(paraname, marginSize , rowsHeight * 5 - rowUpBit, fontPaint);
          paraname = getContext().getString(R.string.cdma_t_drop);
          cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 5 - rowUpBit, fontPaint);
          
          paraname = getContext().getString(R.string.cdma_ec_threshold);
          cv.drawText(paraname, marginSize , rowsHeight * 6 - rowUpBit, fontPaint);
          paraname = getContext().getString(R.string.cdma_t_tdrop);
          cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 6 - rowUpBit, fontPaint);
          
          paraname = getContext().getString(R.string.cdma_ecio_threshold);
          cv.drawText(paraname, marginSize , rowsHeight * 7 - rowUpBit, fontPaint);
          paraname = getContext().getString(R.string.cdma_neighbor_max_age);
          cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 7 - rowUpBit, fontPaint);
          
          paraname = getContext().getString(R.string.cdma_esn);
          cv.drawText(paraname, marginSize , rowsHeight * 8 - rowUpBit, fontPaint);
          paraname = getContext().getString(R.string.cdma_band);
          cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 8 - rowUpBit, fontPaint);
          
          paraname = getContext().getString(R.string.cdma_access_parameter);
          cv.drawText(paraname, (this.getWidth()-fontPaint.measureText(paraname))/2, rowsHeight * 9 - rowUpBit, fontPaint);
          
          paraname = getContext().getString(R.string.cdma_num_step);
          cv.drawText(paraname, marginSize , rowsHeight * 10 - rowUpBit, fontPaint);
          paraname = getContext().getString(R.string.cdma_max_req_seq);
          cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 10 - rowUpBit, fontPaint);
          
          paraname = getContext().getString(R.string.cdma_pwr_step);
          cv.drawText(paraname, marginSize , rowsHeight * 11 - rowUpBit, fontPaint);
          paraname = getContext().getString(R.string.cdma_max_rsp_seq);
          cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 11 - rowUpBit, fontPaint);
          
          paraname = getContext().getString(R.string.cdma_init_pwr);
          cv.drawText(paraname, marginSize , rowsHeight * 12 - rowUpBit, fontPaint);
          paraname = getContext().getString(R.string.cdma_max_cap_sz);
          cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 12 - rowUpBit, fontPaint);
          
          paraname = getContext().getString(R.string.cdma_nom_pwr);
          cv.drawText(paraname, marginSize , rowsHeight * 13 - rowUpBit, fontPaint);
          paraname = getContext().getString(R.string.cdma_acc_tmo);
          cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 13 - rowUpBit, fontPaint);
          
          paraname = getContext().getString(R.string.cdma_bkoff);
          cv.drawText(paraname, marginSize , rowsHeight * 14 - rowUpBit, fontPaint);
          paraname = getContext().getString(R.string.cdma_probe_bkoff);
          cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 14 - rowUpBit, fontPaint);
          
          paraname = getContext().getString(R.string.cdma_pam_se);
          cv.drawText(paraname, marginSize , rowsHeight * 15 - rowUpBit, fontPaint);
          paraname = getContext().getString(R.string.cdma_probe_pn_ran);
          cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 15 - rowUpBit, fontPaint);
          
          paraname = getContext().getString(R.string.cdma_power_control);
          cv.drawText(paraname, (this.getWidth()-fontPaint.measureText(paraname))/2, rowsHeight * 16 - rowUpBit, fontPaint);
          
          paraname = getContext().getString(R.string.cdma_pwr_rep_threshold);
          cv.drawText(paraname, marginSize , rowsHeight * 17 - rowUpBit, fontPaint);
          paraname = getContext().getString(R.string.cdma_pwr_cntl_step);
          cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 17 - rowUpBit, fontPaint);
          
          paraname = getContext().getString(R.string.cdma_pwr_rep_frames);
          cv.drawText(paraname, marginSize , rowsHeight * 18 - rowUpBit, fontPaint);
          paraname = getContext().getString(R.string.cdma_pwr_thresh_enable);
          cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 18 - rowUpBit, fontPaint);
          
          paraname = getContext().getString(R.string.cdma_20);
          cv.drawText(paraname, marginSize , rowsHeight * 19 - rowUpBit, fontPaint);
          paraname = getContext().getString(R.string.cdma_pwr_period_enable);
          cv.drawText(paraname, this.getWidth() /2 + marginSize , rowsHeight * 19 - rowUpBit, fontPaint);
          switchMarker(cv, 2);
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
        
        float colsWidth = this.getWidth()/tableCols;        //列宽

        String text;
        
        if(currentPage ==1 ){

            /**实时数据文字*/
            text = "";
            
            //查询CellName逻辑
            if(!StringUtil.isNullOrEmpty(getParaValue(UnifyParaID.C_Frequency)) && !StringUtil.isNullOrEmpty(getParaValue(UnifyParaID.C_ReferencePN))){
                StringBuilder cellKey = new StringBuilder();
                cellKey.append(WalktourConst.NetWork.CDMA);
                cellKey.append("_frequency_").append(getParaValue(UnifyParaID.C_Frequency));
                cellKey.append("_pn_").append(getParaValue(UnifyParaID.C_ReferencePN));
                if(traceData.getNetworkCellInfo(cellKey.toString()) != null){
                    text = traceData.getNetworkCellInfo(cellKey.toString()).getCellName();
                    cv.drawText(text, this.getWidth() -paramPaint.measureText(text) -marginSize, rowsHeight * 12 - rowUpBit, paramPaint);
                }else {
                    traceData.setNetworkCellInfo(cellKey.toString(),  new CellInfo("","",-1));
                    StringBuilder sb = new StringBuilder();
                    sb.append("frequency = '").append(getParaValue(UnifyParaID.C_Frequency)).append("' ");
                    sb.append(" and pn = '").append(getParaValue(UnifyParaID.C_ReferencePN)).append("' ");
                    new CheckCellParamThread(this.getContext(),new String[]{"frequency","pn","cellName","cellId","longitude","latitude"},sb.toString(), WalktourConst.NetWork.CDMA).start();
                }
            }
            
            String[] datas = new String[]{getParaValue(UnifyParaID.C_Frequency),getParaValue(UnifyParaID.C_SID),
                    getParaValue(UnifyParaID.C_ReferencePN),getParaValue(UnifyParaID.C_NID),
                    getParaValue(UnifyParaID.C_ReferenceEcIo),getParaValue(UnifyParaID.C_BID),
                    getParaValue(UnifyParaID.C_ReferenceEc),getParaValue(UnifyParaID.C_MaxEcIoPN),
                    getParaValue(UnifyParaID.C_TotalEcIo),getParaValue(UnifyParaID.C_MaxEcIo),
                    getParaValue(UnifyParaID.C_TotalEc),getParaValue(UnifyParaID.C_MaxEc),
                    getParaValue(UnifyParaID.C_RxAGC),getParaValue(UnifyParaID.C_FFER),
                    getParaValue(UnifyParaID.C_TxAGC),getParaValue(UnifyParaID.C_TxGainAdj),
                    getParaValue(UnifyParaID.C_TxPower),getParaValue(UnifyParaID.C_ActiveSetNum),
                    UtilsMethodPara.getCdmaState(getParaValue(UnifyParaID.C_State).trim())
                    };
            for (int i = 0,j = 2; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                if(i+1 <datas.length){
                    cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }
            }
        
        } else if(currentPage == 2){
            float fWidth = ( this.getWidth() - colsWidth )/6;
            
            String[] servingNeighbor = getParaValue(UnifyParaID.C_cdmaServingNeighbor).split(";");
            for (int i = 0; servingNeighbor != null && i < servingNeighbor.length - 1; i++) {
            	//CdmaPilot cdmaPilot = cdmaPilotList.get(i);
                /**neighbor:ActiveSetType,CdmaFreq,CdmaPn,CdmaRssi,CdmaRSCP,CdmaEcIo,CdmaDummy*/
                String[] neighbor = servingNeighbor[i+1].split(",");
                text = neighbor[1]; //cdmaPilot.getFrequency();
                cv.drawText(text, colsWidth + fWidth*i + (fWidth-paramPaint.measureText(text) ) / 2  , rowsHeight*2 - rowUpBit, paramPaint);
                text = neighbor[2];  //cdmaPilot.getPn();
                cv.drawText(text, colsWidth + fWidth*i + (fWidth-paramPaint.measureText(text) ) / 2  , rowsHeight*3 - rowUpBit, paramPaint);
                text = UtilsMethod.numToShowDecimal2(neighbor[5]); //cdmaPilot.getEcio();
                cv.drawText(text, colsWidth + fWidth*i + (fWidth-paramPaint.measureText(text) ) / 2  , rowsHeight*4 - rowUpBit, paramPaint);
                text = UtilsMethod.numToShowDecimal2(neighbor[4]); //cdmaPilot.getRssi();
                cv.drawText(text, colsWidth + fWidth*i + (fWidth-paramPaint.measureText(text) ) / 2  , rowsHeight*5 - rowUpBit, paramPaint);
            }
        }else if(currentPage == 3){
            rowsHeight = this.getViewHeight() / 6;
            //ArrayList<CdmaPilot> cdmaPilotList = traceData.getCdmaModel().getPilotList();
            /*if(cdmaPilotList.size() <= 0){
                cdmaPilotList = traceData.getCdmaModel().getNighborCellList();
            }*/
            String[] servingNeighbor = getParaValue(UnifyParaID.C_cdmaServingNeighbor).split(";");
            
            float[] values = new float[servingNeighbor.length - 1];
            float[] percentages = new float[servingNeighbor.length - 1]; 
            for (int i = 0;i < servingNeighbor.length - 1 ;i++) {
                try{
                    //if(!StringUtil.isNullOrEmpty(cdmaPilotList.get(i).getEcio())){
                    //values[i] = Float.parseFloat(cdmaPilotList.get(i).getEcio());
                    String[] neighbor = servingNeighbor[i+1].split(",");
                    values[i] = Float.parseFloat(UtilsMethod.numToShowDecimal2(neighbor[5]));
                    //}
                    percentages[i] = 100 - values[i]/-32 * 100; //ecio 0~-32
                    //percentages[i] = values[i]/30 * 100;
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            super.createCellHistogram("Ec/Io(dB)", null, values, percentages, rowsHeight * 5, cv);
        }else if(currentPage == 4){
            
            //CdmaModel model = traceData.getCdmaModel();
            
            text = "";
            
            String[] datas = new String[]{getParaValue(UnifyParaID.C_Win_A),getParaValue(UnifyParaID.C_Pilot_Inc),
                    getParaValue(UnifyParaID.C_Win_N),getParaValue(UnifyParaID.C_T_Add),
                    getParaValue(UnifyParaID.C_Win_R),getParaValue(UnifyParaID.C_T_Comp),
                    //"0"/*20121225李靖建议先填0*/,getParaValue(UnifyParaID.C_T_Drop),
                    getParaValue(UnifyParaID.C_Soft_Slope),getParaValue(UnifyParaID.C_T_Drop),
                    getParaValue(UnifyParaID.C_Ec_Threshold),getParaValue(UnifyParaID.C_NeighborMaxAge),
                    getParaValue(UnifyParaID.E_Band),UtilsMethod.numToShowHexStr(getParaValue(UnifyParaID.C_ESN))
                    //model.getESN(),赵英彬建议解码无法获得时，从API获取
                    //(model.getESN().equals("") ? MyPhoneState.getInstance().getDeviceId(getContext()) : model.getESN())
                    };
            
            for (int i = 0,j = 2; i < datas.length; i+=2,j++) {
                cv.drawText(datas[i], this.getWidth()/2 -paramPaint.measureText(datas[i]) - marginSize, rowsHeight * j - rowUpBit, paramPaint);
                if(i+1 <datas.length){
                    cv.drawText(datas[i+1], this.getWidth() -paramPaint.measureText(datas[i+1]) -marginSize, rowsHeight * j - rowUpBit, paramPaint);
                }
            }
        }
        cv.save();
        cv.restore();
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
