package com.walktour.gui.total;

import android.content.Context;
import android.util.AttributeSet;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalWeiBo;
import com.walktour.framework.view.tableview.BaseTableView;
import com.walktour.framework.view.tableview.Table;
import com.walktour.gui.R;

import java.util.HashMap;

/**
 * 统计微博
 * 
 * @author jianchao.wang
 * 
 */
public class TotalWeiBoView extends BaseTableView {
	/** 日志标识 */
	private static final String TAG = "TotalWeiBoView";
	/** 统计表标识 */
	private String mTableTag = "weibo";
	/** 表格行数 */
	private static final int TABLE_ROWS = 21;
	/** 表格列数 */
	private static final int TABLE_COLS = 3;
	/** 图片保存名称 */
	private static final String SAVE_PIC_NAME = "-Weibo.jpg";

	public TotalWeiBoView(Context context) {
		super(context, TAG, SAVE_PIC_NAME);
	}

	public TotalWeiBoView(Context context, AttributeSet attrs) {
		super(context, attrs, TAG, SAVE_PIC_NAME);
	}

	@Override
	protected void createTables() {
		if (super.hasTable(this.mTableTag))
			return;
		Table table = super.createTable(this.mTableTag, TABLE_ROWS, TABLE_COLS);
		table.mergeCols(1, 0, 1);
		table.setTitle(super.getString(R.string.total_weibo), super.mTextColor);
		super.setTableCells(this.mTableTag, 1, 0, true, super.mTextColor, 1, true);
	}

	@Override
	protected String[][] getTableTexts(String tag) {
		String[][] texts = new String[TABLE_ROWS - 1][1];
		texts[0][0] = super.getString(R.string.total_weibo_login_times);
		texts[1][0] = super.getString(R.string.total_weibo_login_success_rate);
		texts[2][0] = super.getString(R.string.total_weibo_sent_text_times);
		texts[3][0] = super.getString(R.string.total_weibo_sent_text_success_rate);
		texts[4][0] = super.getString(R.string.total_weibo_sent_text_delay);
		texts[5][0] = super.getString(R.string.total_weibo_sent_pic_times);
		texts[6][0] = super.getString(R.string.total_weibo_sent_pic_success_rate);
		texts[7][0] = super.getString(R.string.total_weibo_sent_pic_delay);
		texts[8][0] = super.getString(R.string.total_weibo_fans_refresh_times);
		texts[9][0] = super.getString(R.string.total_weibo_fans_refresh_success_rate);
		texts[10][0] = super.getString(R.string.total_weibo_fans_refresh_delay);
		texts[11][0] = super.getString(R.string.total_weibo_fans_comment_times);
		texts[12][0] = super.getString(R.string.total_weibo_fans_comment_success_rate);
		texts[13][0] = super.getString(R.string.total_weibo_fans_comment_delay);
		texts[14][0] = super.getString(R.string.total_weibo_fans_relay_times);
		texts[15][0] = super.getString(R.string.total_weibo_fans_relay_success_rate);
		texts[16][0] = super.getString(R.string.total_weibo_fans_relay_delay);
		texts[17][0] = super.getString(R.string.total_weibo_fans_read_pic_times);
		texts[18][0] = super.getString(R.string.total_weibo_fans_read_pic_success_rate);
		texts[19][0] = super.getString(R.string.total_weibo_fans_read_pic_delay);
		return texts;
	}

	@Override
	protected void setTablesDatas() {
		super.setTableCells(this.mTableTag, 1, 2, false, super.mValueColor, 1, true);
	}

	@Override
	protected String[][] getTableValues(String tag) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		String[][] values = new String[TABLE_ROWS - 1][1];
		values[0][0] = TotalDataByGSM.getHashMapValue(hMap, TotalWeiBo._weiboLoginTimes.name());
		values[1][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeiBo._weiboLoginSuccessTimes.name(),
				TotalWeiBo._weiboLoginTimes.name(), 100, "");
		values[2][0] = TotalDataByGSM.getHashMapValue(hMap, TotalWeiBo._weiboSentTextTimes.name());
		values[3][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeiBo._weiboSentTextSuccessTimes.name(),
				TotalWeiBo._weiboSentTextTimes.name(), 100, "");
		values[4][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeiBo._weiboSentTextTotalDelay.name(),
				TotalWeiBo._weiboSentTextSuccessTimes.name(), 1, "");
		values[5][0] = TotalDataByGSM.getHashMapValue(hMap, TotalWeiBo._weiboSentPicTimes.name());
		values[6][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeiBo._weiboSentPicSuccessTimes.name(),
				TotalWeiBo._weiboSentPicTimes.name(), 100, "");
		values[7][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeiBo._weiboSentPicTotalDelay.name(),
				TotalWeiBo._weiboSentPicSuccessTimes.name(), 1, "");
		values[8][0] = TotalDataByGSM.getHashMapValue(hMap, TotalWeiBo._weiboRefreshTimes.name());
		values[9][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeiBo._weiboRefreshSuccessTimes.name(),
				TotalWeiBo._weiboRefreshTimes.name(), 100, "");
		values[10][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeiBo._weiboRefreshTotalDelay.name(),
				TotalWeiBo._weiboRefreshSuccessTimes.name(), 1, "");
		values[11][0] = TotalDataByGSM.getHashMapValue(hMap, TotalWeiBo._weiboCommentTimes.name());
		values[12][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeiBo._weiboCommentSuccessTimes.name(),
				TotalWeiBo._weiboCommentTimes.name(), 100, "");
		values[13][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeiBo._weiboCommentTotalDelay.name(),
				TotalWeiBo._weiboCommentSuccessTimes.name(), 1, "");
		values[14][0] = TotalDataByGSM.getHashMapValue(hMap, TotalWeiBo._weiboRelayTimes.name());
		values[15][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeiBo._weiboRelaySuccessTimes.name(),
				TotalWeiBo._weiboRelayTimes.name(), 100, "");
		values[16][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeiBo._weiboRelayTotalDelay.name(),
				TotalWeiBo._weiboRelaySuccessTimes.name(), 1, "");
		values[17][0] = TotalDataByGSM.getHashMapValue(hMap, TotalWeiBo._weiboReadPicTimes.name());
		values[18][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeiBo._weiboReadPicSuccessTimes.name(),
				TotalWeiBo._weiboReadPicTimes.name(), 100, "");
		values[19][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeiBo._weiboReadPicTotalDelay.name(),
				TotalWeiBo._weiboReadPicSuccessTimes.name(), 1, "");
		return values;
	}
}
