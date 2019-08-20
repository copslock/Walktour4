package com.walktour.gui.total;

import android.content.Context;
import android.util.AttributeSet;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalVS;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.tableview.BaseTableView;
import com.walktour.framework.view.tableview.Table;
import com.walktour.gui.R;

import java.util.HashMap;

/**
 * 流媒体参数统计
 * 
 * @author jianchao.wang
 *
 */
public class TotalVSParaView extends BaseTableView {
	/** 日志标识 */
	private static final String TAG = "TotalVSParaView";
	/** 统计表标识 */
	private String mTableTag = "pbm";
	/** 表格行数 */
	private static final int TABLE_ROWS = 10;
	/** 表格列数 */
	private static final int TABLE_COLS = 4;
	/** 图片保存名称 */
	private static final String SAVE_PIC_NAME = "-VSParam.jpg";

	public TotalVSParaView(Context context) {
		super(context, TAG, SAVE_PIC_NAME);
	}

	public TotalVSParaView(Context context, AttributeSet attrs) {
		super(context, attrs, TAG, SAVE_PIC_NAME);
	}

	@Override
	protected void createTables() {
		if (super.hasTable(this.mTableTag))
			return;
		Table table = super.createTable(this.mTableTag, TABLE_ROWS, TABLE_COLS);
		table.setTitle(super.getString(R.string.strean_KRI), super.mTextColor);
		table.mergeCols(1, 0, 1);
		table.mergeCols(6, 9, 2, 3);
		super.setTableCells(this.mTableTag, 1, 0, true, super.mTextColor, 1, true);
	}

	@Override
	protected String[][] getTableTexts(String tag) {
		String[][] texts = new String[TABLE_ROWS - 1][TABLE_COLS];
		texts[0][2] = super.getString(R.string.total_vs_kri_video);
		texts[0][3] = super.getString(R.string.total_vs_kri_audio);
		texts[1][0] = super.getString(R.string.total_vs_kri_totalPackRecv);
		texts[2][0] = super.getString(R.string.total_vs_kri_lostPackRate);
		texts[3][0] = super.getString(R.string.total_vs_kri_avgPackGap);
		texts[4][0] = super.getString(R.string.total_vs_kri_avgPackJitter);
		texts[5][0] = super.getString(R.string.total_vs_kri_totalByteRecv);
		texts[6][0] = super.getString(R.string.total_vs_kri_avgSpeedRate);
		texts[7][0] = super.getString(R.string.total_vs_kri_avgDesyncRate);
		texts[8][0] = super.getString(R.string.total_vs_kri_avgVmos);
		return texts;
	}

	@Override
	protected void setTablesDatas() {
		super.setTableCells(this.mTableTag, 2, 2, false, super.mValueColor, 1, true);
	}

	@Override
	protected String[][] getTableValues(String tag) {
		HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
		String[][] values = new String[TABLE_ROWS - 2][2];
		values[0][0] = TotalDataByGSM.getHashMapValue(hMap, TotalVS._vsVideoTotalRecv.name());
		values[0][1] = TotalDataByGSM.getHashMapValue(hMap, TotalVS._vsAudio_Pkg_Recv.name());
		boolean emptyVal = !(hMap == null || hMap.containsKey(TotalVS._vsVideoTotalLost.name())
				|| hMap.containsKey(TotalVS._vsVideoTotalLost.name()));
		values[1][0] = emptyVal ? ""
				: "" + TotalDataByGSM.getIntMultiple(
						TotalDataByGSM.getHashMapVal(hMap, TotalVS._vsVideoTotalLost.name()),
						TotalDataByGSM.getHashMapVal(hMap, TotalVS._vsVideoTotalLost.name())
								+ TotalDataByGSM.getHashMapVal(hMap, TotalVS._vsVideoTotalRecv.name()),
						100, "%");
		emptyVal = !(hMap == null || hMap.containsKey(TotalVS._vsAudio_Pkg_Lost.name())
				|| hMap.containsKey(TotalVS._vsAudio_Pkg_Recv.name()));
		values[1][1] = emptyVal ? ""
				: "" + TotalDataByGSM.getIntMultiple(
						TotalDataByGSM.getHashMapVal(hMap, TotalVS._vsAudio_Pkg_Lost.name()),
						TotalDataByGSM.getHashMapVal(hMap, TotalVS._vsAudio_Pkg_Lost.name())
								+ TotalDataByGSM.getHashMapVal(hMap, TotalVS._vsAudio_Pkg_Recv.name()),
						100, "%");
		values[2][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalVS._vsVideo_Interval.name(),
				TotalVS._vsTotalQosTimes.name(), 1, "ms");
		values[2][1] = TotalDataByGSM.getHashMapMultiple(hMap, TotalVS._vsAudio_Interval.name(),
				TotalVS._vsTotalQosTimes.name(), 1, "ms");
		values[3][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalVS._vsVideo_Jitter.name(),
				TotalVS._vsTotalQosTimes.name(), 1, "");
		values[3][1] = TotalDataByGSM.getHashMapMultiple(hMap, TotalVS._vsAudio_Jitter.name(),
				TotalVS._vsTotalQosTimes.name(), 1, "");
		values[4][0] = ((hMap == null || hMap.get(TotalVS._vsTotalBytes.name()) == null) ? ""
				: (String.format("%.2f",
						(hMap.get(TotalVS._vsTotalBytes.name()) / (UtilsMethod.kbyteRage * UtilsMethod.kbyteRage))))
						+ "M");
		values[5][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalVS._vsTotalBytes.name(),
				TotalVS._vsTotalTime.name(), 8f, "kbps");
		values[6][0] = TotalDataByGSM.getHashMapMultiple(hMap, TotalVS._vsA_v_async.name(),
				TotalVS._vsTotalSample.name(), 100, "%");
		// 平均VMOS
		if (hMap != null && hMap.containsKey(TotalVS._vsTrys.name())) {
			long val = hMap.containsKey(TotalVS._vsAv_Vmos.name())
					? hMap.get(TotalVS._vsAv_Vmos.name()) : 0;
			long val2 = hMap.containsKey(TotalVS._vsReproductionStart.name())
					? hMap.get(TotalVS._vsReproductionStart.name()) : 0;
			if (val == 0 || val2 == 0) {
				values[7][0] = "0";
			} else {
				LogUtil.w(VIEW_LOG_TAG, "----->" + val + "," + hMap.get(TotalVS._vsTotalSample.name()) + ","
						+ (val * 0.01f / hMap.get(TotalVS._vsTotalSample.name())));
				values[7][0] = "" + TotalDataByGSM
						.getIntMultiple(val * 0.01f / hMap.get(TotalVS._vsTotalSample.name()), val2, 1, "");
			}
		}
		return values;
	}
}
