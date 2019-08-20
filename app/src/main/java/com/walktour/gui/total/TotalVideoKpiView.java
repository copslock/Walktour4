package com.walktour.gui.total;

import android.content.Context;
import android.util.AttributeSet;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalVideoPlay;
import com.walktour.framework.view.tableview.BaseTableView;
import com.walktour.framework.view.tableview.Table;
import com.walktour.gui.R;

import java.util.HashMap;

/**
 * 视频类KPI参数统计
 * 
 * @author jianchao.wang
 *
 */
public class TotalVideoKpiView extends BaseTableView {
	/** 日志标识 */
	private static final String TAG = "TotalVideoKpiView";
	/** 统计表标识 */
	private String mTableTag = "videoKpi";
	/** 表格行数 */
	private static final int TABLE_ROWS = 16;
	/** 表格列数 */
	private static final int TABLE_COLS = 3;
	/** 图片保存名称 */
	private static final String SAVE_PIC_NAME = "-VSKpi.jpg";

	public TotalVideoKpiView(Context context) {
		super(context, TAG, SAVE_PIC_NAME);
	}

	public TotalVideoKpiView(Context context, AttributeSet attrs) {
		super(context, attrs, TAG, SAVE_PIC_NAME);
	}

	@Override
	protected void setTablesDatas() {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		if (hMap.containsKey(TotalVideoPlay._videoTrys.name())) {
			super.setTableCells(this.mTableTag, 1, 2, false, super.mValueColor, 1, false);
		}
	}

	@Override
	protected void createTables() {
		if (super.hasTable(this.mTableTag))
			return;
		Table table = super.createTable(this.mTableTag, TABLE_ROWS, TABLE_COLS);
		table.mergeCols(1, 0, 1);
		table.setTitle(super.getString(R.string.total_videoplay_KPI), super.mTextColor);
		super.setTableCells(this.mTableTag, 1, 0, true, super.mTextColor, 1, true);
	}

	@Override
	protected String[][] getTableValues(String tag) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		if (!hMap.containsKey(TotalVideoPlay._videoTrys.name()))
			return new String[0][0];
		String[][] values = new String[TABLE_ROWS - 1][1];
		// 发送尝试次数
		values[0][0] = TotalDataByGSM.getHashMapValue(hMap, TotalVideoPlay._videoTrys.name());
		// 接入成功次数
		values[1][0] = TotalDataByGSM.getHashMapValue(hMap, TotalVideoPlay._videoSuccs.name());
		// 接入成功率
		values[2][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalVideoPlay._videoSuccs.name(), TotalVideoPlay._videoTrys.name(),
				100, "%");
		// 平均接入时长
		values[3][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalVideoPlay._videoConnectTime.name(),
				TotalVideoPlay._videoSuccs.name(), 1, "ms");
		// 开始复制次数
		values[4][0] = TotalDataByGSM.getHashMapValue(hMap, TotalVideoPlay._videoReproductionStart.name());
		// 开始复制失败率
		long val = hMap.get(TotalVideoPlay._videoSuccs.name()) - hMap.get(TotalVideoPlay._videoReproductionStart.name());
		values[5][0] = TotalDataByGSM.getIntMultiple(val, hMap.get(TotalVideoPlay._videoSuccs.name()), 100, "%");
		// 平均复制开始时延
		values[6][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalVideoPlay._videoReproductionDaily.name(),
				TotalVideoPlay._videoReproductionStart.name(), 1, "ms");
		// 播放完成次数
		values[7][0] = TotalDataByGSM.getHashMapValue(hMap, TotalVideoPlay._videoPlayEnd.name());
		// 播放掉线次数
		values[8][0] = TotalDataByGSM.getHashMapValue(hMap, TotalVideoPlay._videoDrop.name());
		// 播放掉线率
		values[9][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalVideoPlay._videoDrop.name(),
				TotalVideoPlay._videoReproductionStart.name(), 100, "%");
		// 重缓冲次数
		values[10][0] = TotalDataByGSM.getHashMapValue(hMap, TotalVideoPlay._videoReBuffers.name());
		// 重缓冲失败率
		values[11][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalVideoPlay._videoReBufferFail.name(),
				TotalVideoPlay._videoReBuffers.name(), 100, "%");
		// 重缓冲平均时长
		values[12][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalVideoPlay._videoRebufferTime.name(),
				TotalVideoPlay._videoReBufferSuccess.name(), 1, "ms");
		// 播放掉线率
		values[13][0] = TotalDataByGSM.getIntMultiple(StringUtil.formatTOFloat(TotalDataByGSM.getHashMapValue(hMap, TotalVideoPlay._videoRebufferTime.name())),
				  StringUtil.formatTOFloat(TotalDataByGSM.getHashMapValue(hMap, TotalVideoPlay._videoRebufferTime.name())) 
				+ StringUtil.formatTOFloat(TotalDataByGSM.getHashMapValue(hMap, TotalVideoPlay._videoPlayDuration.name())) 
				, 100, "%");
		// 平均收速率,
		values[14][0] = "" + TotalDataByGSM.getHashMapMultiple(hMap, TotalVideoPlay._vpCurRecvSpeed.name(),
				TotalVideoPlay._vpCurRecvSpeedTimes.name(), 0.001f, "kbps");
		return values;
	}

	@Override
	protected String[][] getTableTexts(String tag) {
		String[][] texts = new String[TABLE_ROWS - 1][1];
		texts[0][0] = super.getString(R.string.total_vs_kpi_attempts);
		texts[1][0] = super.getString(R.string.total_vs_kpi_successAttempts);
		texts[2][0] = super.getString(R.string.total_vs_kpi_successAttemptRats);
		texts[3][0] = super.getString(R.string.total_vs_kpi_meanAccessTime);
		texts[4][0] = super.getString(R.string.total_vs_kpi_startReproductions);
		texts[5][0] = super.getString(R.string.total_vs_kpi_reproductionFailureRats);
		texts[6][0] = super.getString(R.string.total_vs_kpi_reproductionDelay);
		texts[7][0] = super.getString(R.string.total_vs_kpi_playEnds);
		texts[8][0] = super.getString(R.string.total_vs_kpi_playDrops);
		texts[9][0] = super.getString(R.string.total_vs_kpi_playDropRats);
		texts[10][0] = super.getString(R.string.total_vs_kpi_reBufferCount);
		texts[11][0] = super.getString(R.string.total_vs_kpi_reBufferFailureRats);
		texts[12][0] = super.getString(R.string.total_vs_kpi_meanReBufferTime);
		texts[13][0] = super.getString(R.string.total_vs_kpi_rebufferdurationRats);
		texts[14][0] = super.getString(R.string.total_vs_kri_avgSpeedRate);
		return texts;
	}

}
