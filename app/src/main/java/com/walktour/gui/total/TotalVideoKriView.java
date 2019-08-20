package com.walktour.gui.total;

import android.content.Context;
import android.util.AttributeSet;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalVideoPlay;
import com.walktour.Utils.UtilsMethod;
import com.walktour.framework.view.tableview.BaseTableView;
import com.walktour.framework.view.tableview.Table;
import com.walktour.gui.R;

import java.util.HashMap;

/**
 * 视频类KRI统计
 * 
 * @author jianchao.wang
 *
 */
public class TotalVideoKriView extends BaseTableView {
	/** 日志标识 */
	private static final String TAG = "TotalVideoKriView";
	/** 统计表标识 */
	private String mTableTag = "videoKri";
	/** 表格行数 */
	private static final int TABLE_ROWS = 5;
	/** 表格列数 */
	private static final int TABLE_COLS = 2;
	/** 图片保存名称 */
	private static final String SAVE_PIC_NAME = "-VSKri.jpg";

	public TotalVideoKriView(Context context) {
		super(context, TAG, SAVE_PIC_NAME);
	}

	public TotalVideoKriView(Context context, AttributeSet attrs) {
		super(context, attrs, TAG, SAVE_PIC_NAME);
	}

	@Override
	protected void setTablesDatas() {
		super.setTableCells(this.mTableTag, 1, 1, false, super.mValueColor, 1, false);
	}

	@Override
	protected void createTables() {
		if (super.hasTable(this.mTableTag))
			return;
		Table table = super.createTable(this.mTableTag, TABLE_ROWS, TABLE_COLS);
		table.setTitle(super.getString(R.string.total_videoplay_KRI), super.mTextColor);
		super.setTableCells(this.mTableTag, 1, 0, true, super.mTextColor, 1, true);
	}

	@Override
	protected String[][] getTableValues(String tag) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		if (!hMap.containsKey(TotalVideoPlay._videoTrys.name()))
			return new String[0][0];
		String[][] values = new String[TABLE_ROWS - 1][1];
		// 接收数据总大小
		values[0][0] = (hMap.get(TotalVideoPlay._vpTotalBytes.name()) == null ? ""
				: (String.format("%.2f",
						(hMap.get(TotalVideoPlay._vpTotalBytes.name()) / (UtilsMethod.kbyteRage * UtilsMethod.kbyteRage)))) + "M");
		// 平均收速率,
		values[1][0] = "" + TotalDataByGSM.getHashMapMultiple(hMap, TotalVideoPlay._vpTotalBytes.name(),
				TotalVideoPlay._vpTotalTime.name(), 8f, "kbps");
		// 平均A-V不同步率
		values[2][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalVideoPlay._videoAV_DeSync.name(),
				TotalVideoPlay._vpTotalSample.name(), 100, "%");
		// 平均VMOS
		String vals = TotalDataByGSM.getHashMapMultiple(hMap, TotalVideoPlay._videoAv_Vmos.name(),
				TotalVideoPlay._vpTotalSample.name(), 1, "");
		float val = vals.equals("") ? 0 : Float.parseFloat(vals);
		values[3][0] = TotalDataByGSM.getIntMultiple(val, hMap.get(TotalVideoPlay._videoReproductionStart.name()), 0.01f, "");
		return values;

	}

	@Override
	protected String[][] getTableTexts(String tag) {
		String[][] texts = new String[TABLE_ROWS - 1][1];
		texts[0][0] = super.getString(R.string.total_vs_kri_totalByteRecv);
		texts[1][0] = super.getString(R.string.total_vs_kri_avgSpeedRate);
		texts[2][0] = super.getString(R.string.total_vs_kri_avgDesyncRate);
		texts[3][0] = super.getString(R.string.total_vs_kri_avgVmos);
		return texts;
	}
}
