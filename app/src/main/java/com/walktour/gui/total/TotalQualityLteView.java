/*
 * 文件名: TotalCoverageQualityView.java
 * 版    权：  Copyright Dingli. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2013-10-29
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.gui.total;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.TotalStruct.TotalDial;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.BasicTotalView;
import com.walktour.gui.R;

import java.util.HashMap;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2013-10-29] 
 */
public class TotalQualityLteView extends BasicTotalView{
	private boolean isRegisterReceiver = false;
	
    /**
     * [构造简要说明]
     * @param context
     */
    public TotalQualityLteView(Context context) {
        super(context);
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param canvas
     * @see com.walktour.framework.view.BasicTotalView#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float startx = 1;
        float starty = 0;
        float stopx = 0;
        float stopy = 0;
        int tableRows = 9;
        float closwidth = this.getWidth() / 5;
        canvas.drawLine(startx, marginSize, this.getWidth() - marginSize, marginSize, linePaint);
        canvas.drawLine(startx, rowHeight * tableRows + marginSize, this.getWidth() - marginSize, rowHeight * tableRows + marginSize, linePaint);
        canvas.drawLine(startx, marginSize, startx, rowHeight * tableRows + marginSize, linePaint);
        canvas.drawLine(this.getWidth() - marginSize, marginSize, this.getWidth() - marginSize, rowHeight * tableRows + marginSize, linePaint);
        for (int i = 1; i < tableRows; i++) {
            startx = 1;
            starty = rowHeight * i + marginSize;
            stopx = this.getWidth() - marginSize;
            stopy = rowHeight * i + marginSize;
            canvas.drawLine(startx, starty, stopx, stopy, linePaint);
        }
        canvas.drawLine(closwidth * 3, marginSize + rowHeight, closwidth * 3, rowHeight * tableRows + marginSize, linePaint);
        
        canvas.drawText(getResources().getString(R.string.total_coverage_quality), 
                (this.getWidth() - fontPaint.measureText(getResources().getString(R.string.total_coverage_quality)))/2, marginSize + rowHeight - (rowHeight - fontPaint.getTextSize())/2, fontPaint);
        
        int[] params = new int[]{R.string.info_lte,
        		R.string.total_coverage_test_duration,
        		R.string.total_coverage_ratio,
                R.string.total_coverage_mileage,
                R.string.total_coverage_total_mileage,
                R.string.total_coverage_mileage_coverage_ratio,
                R.string.total_coverage_net_Research_delay,
                R.string.total_coverage_attach_delay};
        for (int i = 0; i < params.length; i++) {
            String param = getResources().getString(params[i]);
            canvas.drawText(param,  (closwidth * 3 - fontPaint.measureText(param))/2 ,
                    marginSize + rowHeight * (i+2) - (rowHeight - fontPaint.getTextSize())/2,fontPaint);
        }
        canvas.drawText(getResources().getString(R.string.total_value), closwidth * 3 + (closwidth * 2 - fontPaint.measureText(getResources().getString(R.string.total_value)))/2, 
                marginSize + rowHeight * 2 - (rowHeight - fontPaint.getTextSize())/2,fontPaint);
        HashMap<String, Long> paras = TotalDataByGSM.getInstance().getPara();
        HashMap<String, Long> attchHM = TotalDataByGSM.getInstance().getUnifyTimes();
		// 搜网时延
		String searchValue = TotalDataByGSM.getHashMapMultiple(attchHM,
				TotalStruct.TotalAttach._lteSearchDelay.name(),
				TotalStruct.TotalAttach._lteAttachRequest.name(), 1, "");
		;
		// Attach时延
		String attchValue = TotalDataByGSM.getHashMapMultiple(attchHM,
				TotalStruct.TotalAttach._lteAttachDelay.name(),
				TotalStruct.TotalAttach._lteAttachSuccess.name(), 1, "");
        
        String[] datas = new String[]{
        		 TotalDataByGSM.getHashMapValue(paras,TotalDial._TimeLongLTE.name(),1000f),
        		 TotalDataByGSM.getHashMapMultiple(paras,TotalDial._LTErsrp.name(),TotalDial._LTErsrpCount.name(),100, "%"),
        		 TotalDataByGSM.getHashMapValue(paras,TotalDial._LteCoverMileage.name(),10000),
        		 TotalDataByGSM.getHashMapValue(paras,TotalDial._LteTotalMileage.name(),10000),
        		 TotalDataByGSM.getHashMapMultiple(paras,TotalDial._LteCoverMileage.name(),TotalDial._LteTotalMileage.name(),100, "%"),
        		 searchValue,
        		 attchValue
        		};
        for (int i = 0; i < datas.length; i++) {
            canvas.drawText(datas[i], closwidth * 3 + (closwidth * 2 - paramPaint.measureText(datas[i]))/2, 
                    marginSize + rowHeight * (i+3) - (rowHeight - paramPaint.getTextSize())/2,paramPaint);
        }
    }
    
    
    @Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		IntentFilter filter = new IntentFilter(); // 注册一个消息过滤器
		filter.addAction(TotalDataByGSM.TotalTaskDataChanged);
		filter.addAction(TotalDataByGSM.TotalParaDataChanged);
		getContext().registerReceiver(mIntentReceiver, filter, null, null);
		isRegisterReceiver = true;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		try {
			if (isRegisterReceiver) {
				getContext().unregisterReceiver(mIntentReceiver); // 反注册消息过滤器
				isRegisterReceiver = false;
			}
		} catch (java.lang.IllegalArgumentException e) {
			LogUtil.w("IllegalArgumentException:", e.toString());
		}
	}

	/**
	 * 消息处理
	 */
	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			invalidate();
		}
	};
}
