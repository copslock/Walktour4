package com.walktour.gui.total;

import android.content.Context;
import android.util.AttributeSet;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalPBM;
import com.walktour.Utils.UtilsMethod;
import com.walktour.framework.view.tableview.BaseTableView;
import com.walktour.framework.view.tableview.Table;
import com.walktour.gui.R;

import java.util.HashMap;

/**
 * 统计PBM
 * 
 * @author jianchao.wang
 * 
 */
public class TotalPBMView extends BaseTableView {
	/** 日志标识 */
	private static final String TAG = "TotalPBMView";
	/** 统计表标识 */
	private String mTableTag = "pbm";
	/** 表格行数 */
	private static final int TABLE_ROWS = 5;
	/** 表格列数 */
	private static final int TABLE_COLS = 3;
	/** 图片保存名称 */
	private static final String SAVE_PIC_NAME = "-PBM.jpg";

	public TotalPBMView(Context context) {
		super(context, TAG, SAVE_PIC_NAME);
	}

	public TotalPBMView(Context context, AttributeSet attrs) {
		super(context, attrs, TAG, SAVE_PIC_NAME);
	}

	@Override
	protected void createTables() {
		if (super.hasTable(this.mTableTag))
			return;
		Table table = super.createTable(this.mTableTag, TABLE_ROWS, TABLE_COLS);
		table.setTitle(super.getString(R.string.total_pbm), super.mTextColor);
		super.setTableCells(this.mTableTag, 1, 0, true, super.mTextColor, 1, true);
	}

	@Override
	protected String[][] getTableTexts(String tag) {
		String[][] texts = new String[TABLE_ROWS - 1][TABLE_COLS];
		texts[0][1] = super.getString(R.string.total_upload);
		texts[0][2] = super.getString(R.string.total_download);
		texts[1][0] = super.getString(R.string.total_pbm_lost_fraction);
		texts[2][0] = super.getString(R.string.total_pbm_pkg_gap);
		texts[3][0] = super.getString(R.string.total_pbm_bandwidth);
		return texts;
	}

	@Override
	protected void setTablesDatas() {
		super.setTableCells(this.mTableTag, 2, 1, false, super.mValueColor, 1, true);
	}

	@Override
	protected String[][] getTableValues(String tag) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		String[][] values = new String[TABLE_ROWS - 2][TABLE_COLS - 1];
		values[0][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalPBM._pbmUpLostFraction.name(),
				TotalPBM._pbmCurrentTimes.name(), 1, "");// 上行丢包率
		values[0][1] = TotalDataByGSM.getHashMapMultiple(hMap, TotalPBM._pbmDownLostFraction.name(),
				TotalPBM._pbmCurrentTimes.name(), 1, "");// 下行丢包率
		values[1][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalPBM._pbmUpPkgGap.name(),
				TotalPBM._pbmCurrentTimes.name(), 1, "");// 上行包间隔
		values[1][1] = TotalDataByGSM.getHashMapMultiple(hMap, TotalPBM._pbmDownPkgGap.name(),
				TotalPBM._pbmCurrentTimes.name(), 1, "");// 下行包间隔
		values[2][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalPBM._pbmUpBandwidth.name(),
				TotalPBM._pbmCurrentTimes.name(), 1f / UtilsMethod.kbyteRage, "");// 上行带宽
		values[2][1] = TotalDataByGSM.getHashMapMultiple(hMap, TotalPBM._pbmDownBandwidth.name(),
				TotalPBM._pbmCurrentTimes.name(), 1f / UtilsMethod.kbyteRage, "");// 下行带宽
		return values;
	}

}
