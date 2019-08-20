package com.walktour.gui.total;

import android.content.Context;
import android.util.AttributeSet;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalAppreciation;
import com.walktour.Utils.TotalStruct.TotalHttpType;
import com.walktour.Utils.UtilsMethod;
import com.walktour.framework.view.tableview.BaseTableView;
import com.walktour.framework.view.tableview.Table;
import com.walktour.gui.R;

import java.util.HashMap;
import java.util.Map;

/**
 * http业务实时统计
 * 
 * @author jianchao.wang
 *
 */
public class TotalHttpView extends BaseTableView {
	/** 日志标识 */
	private static final String TAG = "TotalHttpView";
	/** logon统计表标识 */
	private String mLogonTableTag = "logon";
	/** refresh统计表标识 */
	private String mRefreshTableTag = "refresh";
	/** uploadDownload统计表标识 */
	private String mUploadDownloadTableTag = "upload";
	/** logon业务表格行数 */
	private static final int TABLE_LOGON_ROWS = 4;
	/** refresh业务表表格行数 */
	private static final int TABLE_REFRESH_ROWS = 4;
	/** uploadDownload业务表表格行数 */
	private static final int TABLE_UPLOAD_DOWNLOAD_ROWS = 8;
	/** logon业务表格列数 */
	private static final int TABLE_LOGON_COLS = 4;
	/** refresh业务表表格列数 */
	private static final int TABLE_REFRESH_COLS = 4;
	/** uploadDownload业务表表格列数 */
	private static final int TABLE_UPLOAD_DOWNLOAD_COLS = 3;
	/** 图片保存名称 */
	private static final String SAVE_PIC_NAME = "-Http.jpg";
	/** logon业务数据 */
	private Map<String, Object> mLogonMap = new HashMap<String, Object>();
	/** refresh业务数据 */
	private Map<String, Object> mRefreshMap = new HashMap<String, Object>();
	/** upload业务数据 */
	private Map<String, Object> mUploadMap = new HashMap<String, Object>();
	/** download业务数据 */
	private Map<String, Object> mDownloadMap = new HashMap<String, Object>();

	public TotalHttpView(Context context) {
		super(context, TAG, SAVE_PIC_NAME);
	}

	public TotalHttpView(Context context, AttributeSet attrs) {
		super(context, attrs, TAG, SAVE_PIC_NAME);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void createTables() {
		Map<String, Map<String, Map<String, Long>>> specialTimesMap = TotalDataByGSM.getInstance().getSpecialTimes();
		this.mLogonMap.clear();
		this.mRefreshMap.clear();
		this.mUploadMap.clear();
		this.mDownloadMap.clear();
		Map<String,Map<String,Long>> map = specialTimesMap.get(TotalHttpType.HTTPLogon.getHttpType());
		if (map != null)
			this.mLogonMap.putAll(map);
		map = specialTimesMap.get(TotalHttpType.HTTPRefresh.getHttpType());
		if (map != null)
			this.mRefreshMap.putAll(map);
		map = specialTimesMap.get(TotalHttpType.HTTPUpload.getHttpType());
		if (map != null)
			this.mUploadMap.putAll(map);
		map = specialTimesMap.get(TotalHttpType.HTTPDownload.getHttpType());
		if (map != null)
			this.mDownloadMap.putAll(map);
		boolean flag = false;
		if (!this.mLogonMap.isEmpty()) {
			this.createLogonTable(this.mLogonMap.size());
			flag = true;
		}
		if (!this.mRefreshMap.isEmpty()) {
			this.createRefreshTable(this.mRefreshMap.size());
			flag = true;
		}
		if (!this.mUploadMap.isEmpty() || !this.mDownloadMap.isEmpty()) {
			this.createUploadDownloadTable();
			flag = true;
		}
		if (!flag)
			this.createLogonTable(1);
	}

	/**
	 * 生成上传下载统计表格
	 */
	private void createUploadDownloadTable() {
		if (super.hasTable(this.mUploadDownloadTableTag))
			return;
		Table table = super.createTable(this.mUploadDownloadTableTag, TABLE_UPLOAD_DOWNLOAD_ROWS,
				TABLE_UPLOAD_DOWNLOAD_COLS);
		table.setTitle("HTTP", super.mTextColor);
		super.setTableCells(this.mUploadDownloadTableTag, 1, 0, true, super.mTextColor, 1, true);
	}

	/**
	 * 生成logon统计表格
	 * 
	 * @param urlCount
	 *          测试的URL数量
	 */
	private void createLogonTable(int urlCount) {
		if (super.hasTable(this.mLogonTableTag))
			return;
		int tableRows = TABLE_LOGON_ROWS + (TABLE_LOGON_ROWS - 1) * (urlCount - 1);
		Table table = super.createTable(this.mLogonTableTag, tableRows, TABLE_LOGON_COLS);
		for (int i = 1; i < tableRows; i = i + 3) {
			table.mergeCells(i, 1, 3);
		}
		table.setTitle(TotalHttpType.HTTPLogon.getHttpType(), super.mTextColor);
		super.setTableCells(this.mLogonTableTag, 1, 0, true, super.mTextColor, 1, true);
	}

	/**
	 * 生成refresh统计表格
	 * 
	 * @param urlCount
	 *          测试的URL数量
	 */
	private void createRefreshTable(int urlCount) {
		if (super.hasTable(this.mRefreshTableTag))
			return;
		int tableRows = TABLE_REFRESH_ROWS + (TABLE_REFRESH_ROWS - 1) * (urlCount - 1);
		Table table = super.createTable(this.mRefreshTableTag, tableRows, TABLE_REFRESH_COLS);
		for (int i = 1; i < tableRows; i = i + 3) {
			table.mergeCells(i, 1, 3);
		}
		table.setTitle(TotalHttpType.HTTPRefresh.getHttpType(), super.mTextColor);
		super.setTableCells(this.mRefreshTableTag, 1, 0, true, super.mTextColor, 1, true);
	}

	@Override
	protected String[][] getTableTexts(String tag) {
		if (this.mLogonTableTag.equals(tag))
			return this.getLogonTableTexts();
		else if (this.mRefreshTableTag.equals(tag))
			return this.getRefreshTableTexts();
		else
			return this.getUploadDownloadTableTexts();
	}

	/**
	 * 获取上传下载统计表的文本内容
	 * 
	 * @return
	 */
	private String[][] getUploadDownloadTableTexts() {
		String[][] texts = new String[TABLE_UPLOAD_DOWNLOAD_ROWS - 1][TABLE_UPLOAD_DOWNLOAD_COLS];
		texts[0][0] = "HTTP";
		texts[0][1] = this.getContext().getString(R.string.total_upload);
		texts[0][2] = this.getContext().getString(R.string.total_download);
		texts[1][0] = this.getContext().getString(R.string.total_attemptCount);
		texts[2][0] = this.getContext().getString(R.string.total_SuccessCounts);
		texts[3][0] = this.getContext().getString(R.string.total_SuccessRate);
		texts[4][0] = this.getContext().getString(R.string.total_AppAverageSpeed);
		texts[5][0] = this.getContext().getString(R.string.total_downTotalSize);
		texts[6][0] = this.getContext().getString(R.string.total_downTotalTimes);
		return texts;
	}

	/**
	 * 获取refresh统计表的文本内容
	 * 
	 * @return
	 */
	private String[][] getRefreshTableTexts() {
		Table table = super.getTable(this.mRefreshTableTag);
		String[][] texts = new String[table.getTableRows() - 1][TABLE_REFRESH_COLS];
		for (int i = 0; i < texts.length; i = i + 3) {
			texts[i][0] = super.getString(R.string.str_url);
			texts[i + 1][0] = super.getString(R.string.total_attemptCount);
			texts[i + 1][1] = super.getString(R.string.total_SuccessCounts);
			texts[i + 1][2] = super.getString(R.string.total_SuccessRate);
			texts[i + 1][3] = super.getString(R.string.total_AverageDelay);
		}
		return texts;
	}

	/**
	 * 获取logon统计表的文本内容
	 * 
	 * @return
	 */
	private String[][] getLogonTableTexts() {
		Table table = super.getTable(this.mLogonTableTag);
		String[][] texts = new String[table.getTableRows() - 1][TABLE_LOGON_COLS];
		for (int i = 0; i < texts.length; i = i + 3) {
			texts[i][0] = super.getString(R.string.str_url);
			texts[i + 1][0] = super.getString(R.string.total_attemptCount);
			texts[i + 1][1] = super.getString(R.string.total_SuccessCounts);
			texts[i + 1][2] = super.getString(R.string.total_SuccessRate);
			texts[i + 1][3] = super.getString(R.string.total_AverageDelay);
		}
		return texts;
	}

	@Override
	protected void setTablesDatas() {
		super.setTableCells(this.mLogonTableTag, 1, 0, false, super.mValueColor, 1, true);
		super.setTableCells(this.mRefreshTableTag, 1, 0, false, super.mValueColor, 1, true);
		super.setTableCells(this.mUploadDownloadTableTag, 1, 0, false, super.mValueColor, 1, true);
	}

	@Override
	protected String[][] getTableValues(String tag) {
		if (this.mLogonTableTag.equals(tag))
			return this.getLogonTableValues();
		else if (this.mRefreshTableTag.equals(tag))
			return this.getRefreshTableValues();
		else
			return this.getUploadDownloadTableValues();
	}

	/**
	 * 获取上传下载统计表数据
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String[][] getUploadDownloadTableValues() {
		String[][] values = new String[TABLE_UPLOAD_DOWNLOAD_ROWS - 1][TABLE_UPLOAD_DOWNLOAD_COLS];
		if (!this.mUploadMap.isEmpty()) {
			HashMap<String, Long> uploadData = (HashMap<String, Long>) this.mUploadMap.get("HttpUpload");
			if (uploadData != null) {
				values[1][1] = TotalDataByGSM.getHashMapValue(uploadData, TotalAppreciation._HttpUploadTry.name());
				values[2][1] = TotalDataByGSM.getHashMapValue(uploadData, TotalAppreciation._HttpUploadSuccess.name());
				values[3][1] = TotalDataByGSM.getHashMapMultiple(uploadData,
						TotalAppreciation._HttpUploadSuccess.name(),
						TotalAppreciation._HttpUploadTry.name(), 100, "%");
				// 界面显示为kbps<------byte/ms转换成 * 1000f/1024f
				values[4][1] = TotalDataByGSM.getHashMapMultiple(uploadData,
						TotalAppreciation._HttpUploadTotalBytes.name(),
						TotalAppreciation._HttpUploadTotalTime.name(), 8, "");
				values[5][1] = TotalDataByGSM.getHashMapValue(uploadData, TotalAppreciation._HttpUploadTotalBytes.name(),
						UtilsMethod.kbyteRage);
				values[6][1] = TotalDataByGSM.getHashMapValue(uploadData, TotalAppreciation._HttpUploadTotalTime.name(),
						UtilsMethod.kbyteRage);
			}
		}
		if (!this.mDownloadMap.isEmpty()) {
			HashMap<String, Long> downloadData = (HashMap<String, Long>) this.mDownloadMap.get("HttpDownload");
			if (downloadData != null) {
				values[1][2] = TotalDataByGSM.getHashMapValue(downloadData, TotalAppreciation._HttpDownloadTry.name());
				values[2][2] = TotalDataByGSM.getHashMapValue(downloadData, TotalAppreciation._HttpDownloadSuccess.name());
				values[3][2] = TotalDataByGSM.getHashMapMultiple(downloadData,
						TotalAppreciation._HttpDownloadSuccess.name(),
						TotalAppreciation._HttpDownloadTry.name(), 100, "%");
				// 界面显示为kbps<------byte/ms转换成 * 1000f/1024f
				values[4][2] = TotalDataByGSM.getHashMapMultiple(downloadData,
						TotalAppreciation._HttpDownloadTotalBytes.name(),
						TotalAppreciation._HttpDownloadTotalTime.name(), 8, "");
				values[5][2] = TotalDataByGSM.getHashMapValue(downloadData, TotalAppreciation._HttpDownloadTotalBytes.name(),
						UtilsMethod.kbyteRage);
				values[6][2] = TotalDataByGSM.getHashMapValue(downloadData, TotalAppreciation._HttpDownloadTotalTime.name(),
						UtilsMethod.kbyteRage);
			}
		}
		return values;
	}

	/**
	 * 获取refresh统计表数据
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String[][] getRefreshTableValues() {
		Table table = super.getTable(this.mRefreshTableTag);
		String[][] values = new String[table.getTableRows() - 1][TABLE_REFRESH_COLS];
		int rowNo = 0;
		for (String url : this.mRefreshMap.keySet()) {
			if (rowNo >= values.length)
				break;
			HashMap<String, Long> val = (HashMap<String, Long>) this.mRefreshMap.get(url);
			values[rowNo][1] = url;
			rowNo += 2;
			values[rowNo][0] = String.valueOf(val.get(TotalAppreciation._HttpRefreshTry.name()));
			values[rowNo][1] = String.valueOf(val.get(TotalAppreciation._HttpRefreshSuccess.name()));
			values[rowNo][2] = TotalDataByGSM.getHashMapMultiple(val,
					TotalAppreciation._HttpRefreshSuccess.name(),
					TotalAppreciation._HttpRefreshTry.name(), 100, "%");
			values[rowNo][3] = TotalDataByGSM.getHashMapMultiple(val, TotalAppreciation._HttpRefreshDelay.name(),
					TotalAppreciation._HttpRefreshSuccess.name(), 1, "");
			rowNo++;
		}
		return values;
	}

	/**
	 * 获取logon统计表数据
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String[][] getLogonTableValues() {
		Table table = super.getTable(this.mLogonTableTag);
		String[][] values = new String[table.getTableRows() - 1][TABLE_LOGON_COLS];
		int rowNo = 0;
		for (String url : this.mLogonMap.keySet()) {
			if (rowNo >= values.length)
				break;
			HashMap<String, Long> val = (HashMap<String, Long>) this.mLogonMap.get(url);
			values[rowNo][1] = url;
			rowNo += 2;
			values[rowNo][0] = String.valueOf(val.get(TotalAppreciation._HttpTry.name()));
			values[rowNo][1] = String.valueOf(val.get(TotalAppreciation._HttpSuccess.name()));
			values[rowNo][2] = TotalDataByGSM.getHashMapMultiple(val, TotalAppreciation._HttpSuccess.name(),
					TotalAppreciation._HttpTry.name(), 100, "%");
			values[rowNo][3] = TotalDataByGSM.getHashMapMultiple(val, TotalAppreciation._HttpDelay.name(),
					TotalAppreciation._HttpSuccess.name(), 1, "");
			rowNo++;
		}
		return values;
	}
}
