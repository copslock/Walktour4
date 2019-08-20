package com.walktour.gui.total;

import android.content.Context;
import android.util.AttributeSet;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalFaceBook;
import com.walktour.framework.view.tableview.BaseTableView;
import com.walktour.framework.view.tableview.Table;
import com.walktour.gui.R;

import java.util.HashMap;

/**
 * facebook统计界面
 * 
 * @author jianchao.wang
 *
 */
public class TotalFaceBookView extends BaseTableView {
	/** 日志标识 */
	private static final String TAG = "TotalFaceBookView";
	/** 统计表标识 */
	private String mTableTag = "facebook";
	/** 表格行数 */
	private static final int TABLE_ROWS = 25;
	/** 表格列数 */
	private static final int TABLE_COLS = 3;
	/** 图片保存名称 */
	private static final String SAVE_PIC_NAME = "-Facebook.jpg";

	public TotalFaceBookView(Context context) {
		super(context, TAG, SAVE_PIC_NAME);
	}

	public TotalFaceBookView(Context context, AttributeSet attrs) {
		super(context, attrs, TAG, SAVE_PIC_NAME);
	}

	@Override
	protected void setTablesDatas() {
		super.setTableCells(this.mTableTag, 1, 2, false, super.mValueColor, 1, false);
	}

	@Override
	protected void createTables() {
		if (super.hasTable(this.mTableTag))
			return;
		Table table = super.createTable(this.mTableTag, TABLE_ROWS, TABLE_COLS);
		table.mergeCols(1, 0, 1);
		table.setTitle(super.getString(R.string.total_facebook), super.mTextColor);
		super.setTableCells(this.mTableTag, 1, 0, true, super.mTextColor, 1, true);
	}

	@Override
	protected String[][] getTableValues(String tag) {
		String[][] values = new String[TABLE_ROWS - 1][1];
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		values[0][0] = TotalDataByGSM.getHashMapValue(hMap, TotalFaceBook._faceBookAttempts.name());
		values[1][0] = TotalDataByGSM.getHashMapValue(hMap, TotalFaceBook._faceBookSuccesses.name());

		values[2][0] = TotalDataByGSM.getHashMapValue(hMap, TotalFaceBook._faceBookLoginAttempts.name());
		values[3][0] = TotalDataByGSM.getHashMapValue(hMap, TotalFaceBook._faceBookLoginSuccesses.name());
		values[4][0] = TotalDataByGSM.getHashMapValue(hMap, TotalFaceBook._faceBookLoginMeanDelay.name());
		if (!StringUtil.isNullOrEmpty(values[4][0]))
			values[4][0] += "ms";
		values[5][0] = TotalDataByGSM.getHashMapValue(hMap, TotalFaceBook._faceBookGetWallAttempts.name());
		values[6][0] = TotalDataByGSM.getHashMapValue(hMap, TotalFaceBook._faceBookGetWallSuccesses.name());
		values[7][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalFaceBook._faceBookGetWallDownBytes.name(),
				TotalFaceBook._faceBookGetWallMeanDelay.name(), 1, "kbps");
		values[8][0] = TotalDataByGSM.getHashMapValue(hMap, TotalFaceBook._faceBookGetFriendListAttempts.name());
		values[9][0] = TotalDataByGSM.getHashMapValue(hMap, TotalFaceBook._faceBookGetFriendListSuccesses.name());
		values[10][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalFaceBook._faceBookGetFriendListDownBytes.name(),
				TotalFaceBook._faceBookGetFriendListMeanDelay.name(), 1, "kbps");
		values[11][0] = TotalDataByGSM.getHashMapValue(hMap, TotalFaceBook._faceBookPostStatusAttempts.name());
		values[12][0] = TotalDataByGSM.getHashMapValue(hMap, TotalFaceBook._faceBookPostStatusSuccesses.name());
		values[13][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalFaceBook._faceBookPostStatusUpBytes.name(),
				TotalFaceBook._faceBookPostStatusMeanDelay.name(), 1, "kbps");
		values[14][0] = TotalDataByGSM.getHashMapValue(hMap, TotalFaceBook._faceBookPostPhotoAttempts.name());
		values[15][0] = TotalDataByGSM.getHashMapValue(hMap, TotalFaceBook._faceBookPostPhotoSuccesses.name());
		values[16][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalFaceBook._faceBookPostPhotoUpBytes.name(),
				TotalFaceBook._faceBookPostPhotoMeanDelay.name(), 1, "kbps");
		values[17][0] = TotalDataByGSM.getHashMapValue(hMap, TotalFaceBook._faceBookPostCommentAttempts.name());
		values[18][0] = TotalDataByGSM.getHashMapValue(hMap, TotalFaceBook._faceBookPostCommentSuccesses.name());
		values[19][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalFaceBook._faceBookPostCommentUpBytes.name(),
				TotalFaceBook._faceBookPostCommentMeanDelay.name(), 1, "kbps");
		values[20][0] = TotalDataByGSM.getHashMapValue(hMap, TotalFaceBook._faceBookLogoutAttempts.name());
		values[21][0] = TotalDataByGSM.getHashMapValue(hMap, TotalFaceBook._faceBookLogoutSuccesses.name());
		values[22][0] = TotalDataByGSM.getHashMapValue(hMap, TotalFaceBook._faceBookLogoutMeanDelay.name());
		if (!StringUtil.isNullOrEmpty(values[22][0]))
			values[22][0] += "ms";
		values[23][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalFaceBook._faceBookSuccesses.name(),
				TotalFaceBook._faceBookAttempts.name(), 100, "%");
		return values;
	}

	@Override
	protected String[][] getTableTexts(String tag) {
		String[][] texts = new String[TABLE_ROWS - 1][1];
		texts[0][0] = super.getString(R.string.total_facebook_attempts);
		texts[1][0] = super.getString(R.string.total_facebook_successes);
		texts[2][0] = super.getString(R.string.total_facebook_login_attempts);
		texts[3][0] = super.getString(R.string.total_facebook_login_successes);
		texts[4][0] = super.getString(R.string.total_facebook_login_mean_delay);
		texts[5][0] = super.getString(R.string.total_facebook_get_wall_attempts);
		texts[6][0] = super.getString(R.string.total_facebook_get_wall_successes);
		texts[7][0] = super.getString(R.string.total_facebook_get_wall_mean_thr);
		texts[8][0] = super.getString(R.string.total_facebook_get_friend_list_attempts);
		texts[9][0] = super.getString(R.string.total_facebook_get_friend_list_successes);
		texts[10][0] = super.getString(R.string.total_facebook_get_friend_list_mean_thr);
		texts[11][0] = super.getString(R.string.total_facebook_post_status_attempts);
		texts[12][0] = super.getString(R.string.total_facebook_post_status_successes);
		texts[13][0] = super.getString(R.string.total_facebook_post_status_mean_thr);
		texts[14][0] = super.getString(R.string.total_facebook_post_photo_attempts);
		texts[15][0] = super.getString(R.string.total_facebook_post_photo_successes);
		texts[16][0] = super.getString(R.string.total_facebook_post_photo_mean_thr);
		texts[17][0] = super.getString(R.string.total_facebook_post_comment_attempts);
		texts[18][0] = super.getString(R.string.total_facebook_post_comment_successes);
		texts[19][0] = super.getString(R.string.total_facebook_post_comment_mean_thr);
		texts[20][0] = super.getString(R.string.total_facebook_logout_attempts);
		texts[21][0] = super.getString(R.string.total_facebook_logout_successes);
		texts[22][0] = super.getString(R.string.total_facebook_logout_mean_delay);
		texts[23][0] = super.getString(R.string.total_facebook_successes_rate);
		return texts;
	}

}
