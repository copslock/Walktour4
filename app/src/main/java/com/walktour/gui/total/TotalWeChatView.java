package com.walktour.gui.total;

import android.content.Context;
import android.util.AttributeSet;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalWeChat;
import com.walktour.framework.view.tableview.BaseTableView;
import com.walktour.framework.view.tableview.Table;
import com.walktour.gui.R;

import java.util.HashMap;

/**
 * 统计微信
 * 
 * @author jianchao.wang
 * 
 */
public class TotalWeChatView extends BaseTableView {
	/** 日志标识 */
	private static final String TAG = "TotalWeChatView";
	/** 统计表标识 */
	private String mTableTag = "wechat";
	/** 表格行数 */
	private static final int TABLE_ROWS = 16;
	/** 表格列数 */
	private static final int TABLE_COLS = 3;
	/** 图片保存名称 */
	private static final String SAVE_PIC_NAME = "-WeChat.jpg";

	public TotalWeChatView(Context context) {
		super(context, TAG, SAVE_PIC_NAME);
	}

	public TotalWeChatView(Context context, AttributeSet attrs) {
		super(context, attrs, TAG, SAVE_PIC_NAME);
	}

	@Override
	protected void createTables() {
		if (super.hasTable(this.mTableTag))
			return;
		Table table = super.createTable(this.mTableTag, TABLE_ROWS, TABLE_COLS);
		table.mergeCols(1, 0, 1);
		table.setTitle(super.getString(R.string.total_wechat), super.mTextColor);
		super.setTableCells(this.mTableTag, 1, 0, true, super.mTextColor, 1, true);
	}

	@Override
	protected String[][] getTableTexts(String tag) {
		String[][] texts = new String[TABLE_ROWS - 1][1];
		texts[0][0] = super.getString(R.string.total_wechat_msg_count);
		texts[1][0] = super.getString(R.string.total_wechat_msg_success_count);
		texts[2][0] = super.getString(R.string.total_wechat_msg_success_rate);
		texts[3][0] = super.getString(R.string.total_wechat_msg_avg_rate);
		texts[4][0] = super.getString(R.string.total_wechat_msg_avg_delay);
		texts[5][0] = super.getString(R.string.total_wechat_img_count);
		texts[6][0] = super.getString(R.string.total_wechat_img_success_count);
		texts[7][0] = super.getString(R.string.total_wechat_img_success_rate);
		texts[8][0] = super.getString(R.string.total_wechat_img_avg_rate);
		texts[9][0] = super.getString(R.string.total_wechat_img_avg_delay);
		texts[10][0] = super.getString(R.string.total_wechat_voice_count);
		texts[11][0] = super.getString(R.string.total_wechat_voice_success_count);
		texts[12][0] = super.getString(R.string.total_wechat_voice_success_rate);
		texts[13][0] = super.getString(R.string.total_wechat_voice_avg_rate);
		texts[14][0] = super.getString(R.string.total_wechat_voice_avg_delay);
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
		values[0][0] = TotalDataByGSM.getHashMapValue(hMap, TotalWeChat._sendMsgCount.name());// 发送消息次数
		values[1][0] = TotalDataByGSM.getHashMapValue(hMap, TotalWeChat._sendMsgSuccessCount.name());// 发送消息成功次数
		values[2][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeChat._sendMsgSuccessCount.name(),
				TotalWeChat._sendMsgCount.name(), 100, "");// 发送消息成功率
		values[3][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeChat._sendMsgTotalUpbytes.name(),
				TotalWeChat._sendMsgTotalDelay.name(), 1, "");// 发送消息平均速率
		values[4][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeChat._sendMsgTotalDelay.name(),
				TotalWeChat._sendMsgSuccessCount.name(), 1, "");// 发送消息平均时延
		values[5][0] = TotalDataByGSM.getHashMapValue(hMap, TotalWeChat._sendImgCount.name());// 发送图片次数
		values[6][0] = TotalDataByGSM.getHashMapValue(hMap, TotalWeChat._sendImgSuccessCount.name());// 发送图片成功次数
		values[7][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeChat._sendImgSuccessCount.name(),
				TotalWeChat._sendImgCount.name(), 100, "");// 发送图片成功率
		values[8][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeChat._sendImgTotalUpbytes.name(),
				TotalWeChat._sendImgTotalDelay.name(), 1, "");// 发送图片平均速率
		values[9][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeChat._sendImgTotalDelay.name(),
				TotalWeChat._sendImgSuccessCount.name(), 1, "");// 发送图片平均时延
		values[10][0] = TotalDataByGSM.getHashMapValue(hMap, TotalWeChat._sendVoiceCount.name());// 发送语音次数
		values[11][0] = TotalDataByGSM.getHashMapValue(hMap, TotalWeChat._sendVoiceSuccessCount.name());// 发送语音成功次数
		values[12][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeChat._sendVoiceSuccessCount.name(),
				TotalWeChat._sendVoiceCount.name(), 100, "");// 发送语音成功率
		values[13][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeChat._sendVoiceTotalUpbytes.name(),
				TotalWeChat._sendVoiceTotalDelay.name(), 1, "");// 发送语音平均速率
		values[14][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalWeChat._sendVoiceTotalDelay.name(),
				TotalWeChat._sendVoiceSuccessCount.name(), 1, "");// 发送语音平均时延
		return values;
	}

}
