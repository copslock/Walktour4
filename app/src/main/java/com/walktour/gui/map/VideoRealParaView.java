package com.walktour.gui.map;

import android.content.Context;
import android.content.IntentFilter;
import android.util.AttributeSet;

import com.walktour.Utils.ConstItems;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.UtilsMethodPara;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.framework.view.tableview.BaseTableView;
import com.walktour.framework.view.tableview.Table;
import com.walktour.gui.R;

import java.util.Locale;
import java.util.Map;

/**
 * 视频实时参数显示界面
 * 
 * @author jianchao.wang
 *
 */
public class VideoRealParaView extends BaseTableView {
	/** 日志标识 */
	private static final String TAG = "VideoRealParaView";
	/** video统计表标识 */
	private String mVideoTableTag = "video";
	/** stream统计表标识 */
	private String mStreamTableTag = "stream";
	/** videoplay业务表格行数 */
	private static final int TABLE_VIDEO_ROWS = 16;
	/** stream业务表表格行数 */
	private static final int TABLE_STREAM_ROWS = 6;
	/** videoplay业务表格列数 */
	private static final int TABLE_VIDEO_COLS = 4;
	/** stream业务表表格列数 */
	private static final int TABLE_STREAM_COLS = 4;
	/** 图片保存名称 */
	private static final String SAVE_PIC_NAME = "-VSReal.jpg";

	public VideoRealParaView(Context context) {
		super(context, TAG, SAVE_PIC_NAME);
	}

	public VideoRealParaView(Context context, AttributeSet attrs) {
		super(context, attrs, TAG, SAVE_PIC_NAME);
	}

	/**
	 * 获得参数值
	 * 
	 * @param paramValues
	 *          参数值
	 * @param key
	 *          参数key
	 * @return
	 */
	private String getParamValue(Map<String, Object> paramValues, String key) {
		return UtilsMethodPara.getValueOnHashMap(paramValues, key);
	}

	/**
	 * 获取视频参数值
	 * 
	 * @return
	 */
	private String[][] getVideoTableValues() {
		String[][] values = new String[TABLE_VIDEO_ROWS][TABLE_VIDEO_COLS];
		Map<String, Object> paramValues = TraceInfoInterface.traceData.getVideoRealPrar();
		values[0][1] = paramValues.containsKey(ConstItems.TOTAL_RECV_SIZE) ? String.format(Locale.getDefault(), "%.2f",
				((Float) paramValues.get(ConstItems.TOTAL_RECV_SIZE)) / UtilsMethod.kbyteRage) : "";
		values[0][3] = getParamValue(paramValues, ConstItems.MEDIA_QUALITY);
		values[1][1] = getParamValue(paramValues, ConstItems.DOWN_PROCESS);
		values[1][3] = getParamValue(paramValues, ConstItems.TOTAL_BIT_RATE);
		values[2][1] = getParamValue(paramValues, ConstItems.CUR_RECV_SPEED);
		values[2][3] = getParamValue(paramValues, ConstItems.DURATION_TIME);
		values[3][1] = getParamValue(paramValues, ConstItems.REBUF_COUNTS);
		values[3][3] = getParamValue(paramValues, ConstItems.VIDEO_FPS);
		values[4][1] = getParamValue(paramValues, ConstItems.REBUF_TIMES);
		values[4][3] = getParamValue(paramValues, ConstItems.VIDEO_WITH);
		values[5][1] = getParamValue(paramValues, ConstItems.PLAY_DURATION);
		values[5][3] = getParamValue(paramValues, ConstItems.VIDEO_HEIGHT);
		values[6][1] = getParamValue(paramValues, ConstItems.STALLING_RATIO);
		values[6][3] = getParamValue(paramValues, ConstItems.VIDEO_CODEC);
		values[7][1] = getParamValue(paramValues, ConstItems.INIT_BUFFER_LATENCY);
		values[7][3] = getParamValue(paramValues, ConstItems.AUDIO_CODEC);
		values[8][1] = getParamValue(paramValues, ConstItems.VIDEO_SERVER_IP);
		values[8][3] = getParamValue(paramValues, ConstItems.WEBSITE_TYPE);
		values[9][2] = getParamValue(paramValues, ConstItems.VIDEO_SERVER_LOC);
		values[10][2] = getParamValue(paramValues, ConstItems.VIDEO_TITLE);
		values[11][1] = getParamValue(paramValues, ConstItems.VMOS);
		values[11][3] = getParamValue(paramValues, ConstItems.QUALITY_SCORE);
		values[12][1] = getParamValue(paramValues, ConstItems.LOADING_SCORE);
		values[12][3] = getParamValue(paramValues, ConstItems.STALLING_SCORE);
		values[13][2] = getParamValue(paramValues, ConstItems.WHOLE_PHASE_DURATION);
		values[14][2] = getParamValue(paramValues, ConstItems.WHOLE_PHASE_MAX_RATE);
		String curMediaQuality=getParamValue(paramValues, ConstItems.CUR_MEDIA_QUALITY);
		if(null==curMediaQuality||curMediaQuality.trim().length()==0){
			curMediaQuality="auto";
		}else if(curMediaQuality.equals("1")){
			curMediaQuality="normal";
		}else if(curMediaQuality.equals("2")){
			curMediaQuality="high";
		}else if(curMediaQuality.equals("3")){
			curMediaQuality="super";
		}else if(curMediaQuality.equals("4")){
			curMediaQuality="720P";
		}else if(curMediaQuality.equals("5")){
			curMediaQuality="1080P";
		}else if(curMediaQuality.equals("6")){
			curMediaQuality="2K";
		}else if(curMediaQuality.equals("7")){
			curMediaQuality="4K";
		}
		values[15][2] = curMediaQuality;
		return values;
	}

	/**
	 * 获取流参数值
	 * 
	 * @return
	 */
	private String[][] getStreamTableValues() {
		Map<String, Object> paramValues = TraceInfoInterface.traceData.getVideoRealPrar();
		String[][] values = new String[TABLE_STREAM_ROWS][TABLE_STREAM_COLS];
		values[0][1] = getParamValue(paramValues, ConstItems.TOTAL_VIDEO_RECV_PACKETS);
		values[0][3] = getParamValue(paramValues, ConstItems.TOTAL_AUDIO_RECV_PACKETS);
		values[1][1] = getParamValue(paramValues, ConstItems.TOTAL_VIDEO_PACKETS_LOSS_RATE);
		values[1][3] = getParamValue(paramValues, ConstItems.TOTAL_AUDIO_PACKETS_LOSS_RATE);
		values[2][1] = getParamValue(paramValues, ConstItems.MEAN_VIDEO_PACKET_INTERVAL);
		values[2][3] = getParamValue(paramValues, ConstItems.MEAN_AUDIO_PACKET_INTERVAL);
		values[3][1] = getParamValue(paramValues, ConstItems.VIDEO_MAX_JITTER);
		values[3][3] = getParamValue(paramValues, ConstItems.AUDIO_MAX_JITTER);
		values[4][1] = getParamValue(paramValues, ConstItems.VIDEO_MEAN_JITTER);
		values[4][3] = getParamValue(paramValues, ConstItems.AUDIO_MEAN_JITTER);
		values[5][1] = getParamValue(paramValues, ConstItems.VIDEO_INST_JITTER);
		values[5][3] = getParamValue(paramValues, ConstItems.AUDIO_INST_JITTER);
		return values;
	}

	@Override
	protected void setTablesDatas() {
		super.setTableCells(this.mVideoTableTag, 0, 0, false, super.mValueColor, 2, true);
		super.setTableCells(this.mStreamTableTag, 0, 0, false, super.mValueColor, 2, true);
	}

	/**
	 * 获得视频表格的文本
	 * 
	 * @return
	 */
	private String[][] getVideoTableTexts() {
		String[][] texts = new String[TABLE_VIDEO_ROWS][TABLE_VIDEO_COLS - 1];
		texts[0][0] = super.getString(R.string.video_total_recv);
		texts[0][2] = super.getString(R.string.video_media_quality);
		texts[1][0] = super.getString(R.string.video_down_process);
		texts[1][2] = super.getString(R.string.video_total_bitrate);
		texts[2][0] = super.getString(R.string.video_curr_rcv_speed);
		texts[2][2] = super.getString(R.string.video_duration_time);
		texts[3][0] = super.getString(R.string.video_rebuf_counts);
		texts[3][2] = super.getString(R.string.video_fps);
		texts[4][0] = super.getString(R.string.video_rebuf_time);
		texts[4][2] = super.getString(R.string.video_width);
		texts[5][0] = super.getString(R.string.video_play_duration);
		texts[5][2] = super.getString(R.string.video_height);
		texts[6][0] = super.getString(R.string.video_stalling_ratio);
		texts[6][2] = super.getString(R.string.video_video_codec);
		texts[7][0] = super.getString(R.string.video_init_buffer_latency);
		texts[7][2] = super.getString(R.string.video_audio_codec);
		texts[8][0] = super.getString(R.string.video_server_ip);
		texts[8][2] = super.getString(R.string.video_website_type);
		texts[9][0] = super.getString(R.string.video_server_loc);
		texts[10][0] = super.getString(R.string.video_title);
		texts[11][0] = super.getString(R.string.video_vmos);
		texts[11][2] = super.getString(R.string.video_quality_score);
		texts[12][0] = super.getString(R.string.video_loading_score);
		texts[12][2] = super.getString(R.string.video_stalling_score);
		texts[13][0] = super.getString(R.string.video_whole_phase_duration);;
		texts[14][0] = super.getString(R.string.video_whole_phase_max_rate);;
		texts[15][0] = super.getString(R.string.video_cur_media_quality);;
		return texts;
	}

	/**
	 * 生成视频表格
	 */
	private void createVideoTable() {
		if (super.hasTable(this.mVideoTableTag))
			return;
		Table table = super.createTable(this.mVideoTableTag, TABLE_VIDEO_ROWS, TABLE_VIDEO_COLS);
		table.mergeCells(9, 0, 1);
		table.mergeCells(9, 2, 3);
		table.mergeCells(10, 0, 1);
		table.mergeCells(10, 2, 3);
		table.mergeCells(13, 0, 1);
		table.mergeCells(13, 2, 3);
		table.mergeCells(14, 0, 1);
		table.mergeCells(14, 2, 3);
		table.mergeCells(15, 0, 1);
		table.mergeCells(15, 2, 3);
		super.setTableCells(this.mVideoTableTag, 0, 0, true, super.mTextColor, 0, false);
	}

	/**
	 * 获得流表格的文本
	 * 
	 * @return
	 */
	private String[][] getStreamTableTexts() {
		String[][] texts = new String[TABLE_STREAM_ROWS][TABLE_STREAM_COLS - 1];
		texts[0][0] = super.getString(R.string.stream_video_total_recv_pack);
		texts[0][2] = super.getString(R.string.stream_audio_total_recv_pack);
		texts[1][0] = super.getString(R.string.stream_video_total_lossrate);
		texts[1][2] = super.getString(R.string.stream_audio_total_lossrate);
		texts[2][0] = super.getString(R.string.stream_video_mean_pack_interval);
		texts[2][2] = super.getString(R.string.stream_audio_mean_pack_interval);
		texts[3][0] = super.getString(R.string.stream_video_max_jitter);
		texts[3][2] = super.getString(R.string.stream_audio_max_jitter);
		texts[4][0] = super.getString(R.string.stream_video_mean_jitter);
		texts[4][2] = super.getString(R.string.stream_audio_mean_jitter);
		texts[5][0] = super.getString(R.string.stream_video_inst_jitter);
		texts[5][2] = super.getString(R.string.stream_audio_inst_jitter);
		return texts;
	}

	/**
	 * 生成流表格
	 */
	private void createStreamTable() {
		if (super.hasTable(this.mStreamTableTag))
			return;
		Map<String, Object> paramValues = TraceInfoInterface.traceData.getVideoRealPrar();
		if (paramValues.containsKey(ConstItems.VIDEO_TYPE)
				&& (Integer) paramValues.get(ConstItems.VIDEO_TYPE) == WalkCommonPara.CALL_BACK_VIDEO_STREAM_REAL_PARA) {
			super.createTable(this.mStreamTableTag, TABLE_STREAM_ROWS, TABLE_STREAM_COLS);
			super.setTableCells(this.mStreamTableTag, 0, 0, true, super.mTextColor, 0, false);
		}
	}

	@Override
	protected void createTables() {
		this.createVideoTable();
		this.createStreamTable();
	}

	@Override
	protected IntentFilter createIntentFilter() {
		IntentFilter filter = new IntentFilter(); // 注册一个消息过滤器
		filter.addAction(WalkCommonPara.VIDEO_REAL_PARA_CHANGE);
		return filter;
	}

	@Override
	protected String[][] getTableValues(String tag) {
		if (this.mVideoTableTag.equals(tag))
			return this.getVideoTableValues();
		return this.getStreamTableValues();
	}

	@Override
	protected String[][] getTableTexts(String tag) {
		if (this.mVideoTableTag.equals(tag))
			return this.getVideoTableTexts();
		return this.getStreamTableTexts();
	}

}
