package com.walktour.gui.total;

import android.content.Context;
import android.util.AttributeSet;

import com.dingli.seegull.test.TestModels;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalDial;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.tableview.BaseTableView;
import com.walktour.framework.view.tableview.Table;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.TaskGroupConfig;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;

import java.util.HashMap;
import java.util.List;

/**
 * 语音业务实时统计
 *
 * @author jianchao.wang
 */
public class TotalDialView extends BaseTableView {
    /**
     * 日志标识
     */
    private static final String TAG = "TotalDialView";
    /**
     * 语音统计表标识
     */
    private String mVoiceTableTag = "voice";
    /**
     * csfb统计表标识
     */
    private String mCSFBTableTag = "csfb";
    /**
    /**
    /**
     * pesq统计表标识
     */
    private String mPESQTableTag = "pesq";
    /**
     * pesq统计表标识
     */
    private String mPOLQATableTag = "polqa";
    /**
     * 语音业务表格行数
     */
    private static final int TABLE_VOICE_ROWS = 8;
    /**
     * csfb业务表表格行数
     */
    private static final int TABLE_CSFB_ROWS = 20;
    /**
     * pesq业务表表格行数
     */
    private static final int TABLE_PESQ_ROWS = 3;
    /**
     * POLQA业务表表格行数
     */
    private static final int TABLE_POLQA_ROWS = 3;
    /**
     * 语音业务表格列数
     */
    private static final int TABLE_VOICE_COLS = 4;
    /**
     * csfb业务表表格列数
     */
    private static final int TABLE_CSFB_COLS = 4;
    /**
     * pesq业务表表格列数
     */
    private static final int TABLE_PESQ_COLS = 2;
    /**
     * POLQA业务表表格列数
     */
    private static final int TABLE_POLQA_COLS = 2;
    /**
     * 图片保存名称
     */
    private static final String SAVE_PIC_NAME = "-Voice.jpg";
    /**
     * 是否POLQA測試
     */
    private boolean isPOLQAMoc;

    public TotalDialView(Context context) {
        super(context, TAG, SAVE_PIC_NAME);
        checkIsPOLQA();
    }

    public TotalDialView(Context context, AttributeSet attrs) {
        super(context, attrs, TAG, SAVE_PIC_NAME);
        checkIsPOLQA();

    }

    @Override
    protected void createTables() {
        this.createVoiceTable();
        this.createCSFBTable();
        if (isPOLQAMoc) {
            this.createPOLQATable();
        } else {
            this.createPESQTable();
        }
    }

    void checkIsPOLQA() {
        /**
         * PESQ为PESQ算分,POLQA为POLQA算分
         * @return
         */
        TaskInitiativeCallModel callModel = getCurrentTaskMoel();
        LogUtil.d(TAG, "callMoedl:" + callModel);
        if (callModel != null && "POLQA".equals(callModel.getCallMosCountStr())) {
            isPOLQAMoc = true;
        } else {
            isPOLQAMoc = false;
        }
    }

    TaskInitiativeCallModel getCurrentTaskMoel() {
        // 1.获得当前勾选的任务组列表
        List<TaskGroupConfig> testGroups = TaskListDispose.getInstance().getAllSelectGroup();
        for (int i = 0; i < testGroups.size(); i++) {
            List<TaskModel> enableList = TaskListDispose.getInstance().getAllSelectedTask(i);
            for (int j = 0; j < enableList.size(); j++) {
                TaskModel currentTaskModel = (TaskModel) enableList.get(i);
                if (currentTaskModel instanceof TaskInitiativeCallModel) {
                    return (TaskInitiativeCallModel) currentTaskModel;
                }
            }
        }
        return null;
    }

    /**
     * 生成POLQA统计表
     */
    private void createPOLQATable() {
        if (super.hasTable(this.mPOLQATableTag))
            return;
        Table table = super.createTable(this.mPOLQATableTag, TABLE_POLQA_ROWS, TABLE_POLQA_COLS);
        table.setTitle(super.getString(R.string.total_str_polqa), super.mTextColor);
        super.setTableCells(this.mPOLQATableTag, 1, 0, true, super.mTextColor, 1, true);
    }

    /**
     * 生成PESQ统计表
     */
    private void createPESQTable() {
        if (super.hasTable(this.mPESQTableTag))
            return;
        Table table = super.createTable(this.mPESQTableTag, TABLE_PESQ_ROWS, TABLE_PESQ_COLS);
        table.setTitle(super.getString(R.string.total_str_pesq), super.mTextColor);
        super.setTableCells(this.mPESQTableTag, 1, 0, true, super.mTextColor, 1, true);
    }

    /**
     * 生成CSFB统计表
     */
    private void createCSFBTable() {
        if (super.hasTable(this.mCSFBTableTag))
            return;
        Table table = super.createTable(this.mCSFBTableTag, TABLE_CSFB_ROWS, TABLE_CSFB_COLS);
        table.setTitle(super.getString(R.string.total_csfb), super.mTextColor);
        table.mergeCols(1, 0, 1);
        super.setTableCells(this.mCSFBTableTag, 1, 0, true, super.mTextColor, 1, true);
    }

    /**
     * 生成语音统计表
     */
    private void createVoiceTable() {
        if (super.hasTable(this.mVoiceTableTag))
            return;
        Table table = super.createTable(this.mVoiceTableTag, TABLE_VOICE_ROWS, TABLE_VOICE_COLS);
        table.setTitle(super.getString(R.string.total_str_voice), super.mTextColor);
        table.mergeCols(1, 0, 1);
        super.setTableCells(this.mVoiceTableTag, 1, 0, true, super.mTextColor, 1, true);
    }

    @Override
    protected String[][] getTableTexts(String tag) {
        if (this.mVoiceTableTag.equals(tag))
            return this.getVoiceTableTexts();
        else if (this.mCSFBTableTag.equals(tag))
            return this.getCSFBTableTexts();
        else if (this.mPESQTableTag.equals(tag))
            return this.getPESQTableTexts();
        else
            return this.getPOLQATableTexts();
    }

    /**
     * 获得PESQ统计表文本
     *
     * @return
     */
    private String[][] getPESQTableTexts() {
        String[][] texts = new String[TABLE_PESQ_ROWS - 1][TABLE_PESQ_COLS - 1];
        texts[0][0] = super.getString(R.string.total_PESQ_Score);
        texts[1][0] = super.getString(R.string.total_PESQ_MOS);
        return texts;
    }

    /**
     * 获得POLQA统计表文本
     *
     * @return
     */
    private String[][] getPOLQATableTexts() {
        String[][] texts = new String[TABLE_POLQA_ROWS - 1][TABLE_POLQA_COLS - 1];
        texts[0][0] = super.getString(R.string.total_POLQA_Score);
        texts[1][0] = super.getString(R.string.total_POLQA_MOS);
        return texts;
    }

    /**
     * 获取CSFB统计表文本
     *
     * @return
     */
    private String[][] getCSFBTableTexts() {
        String[][] texts = new String[TABLE_CSFB_ROWS - 1][TABLE_CSFB_COLS];
        texts[0][2] = super.getString(R.string.total_call_mo);
        texts[0][3] = super.getString(R.string.total_call_mt);
        texts[1][0] = super.getString(R.string.total_csfb_request);
        texts[2][0] = super.getString(R.string.total_csfb_rrc_release_delay);
        texts[3][0] = super.getString(R.string.total_csfb_fallback_to_2g_Count);
        texts[4][0] = super.getString(R.string.total_csfb_fallback_to_3g_Count);
        texts[5][0] = super.getString(R.string.total_csfb_fallback_to_2g_Rate);
        texts[6][0] = super.getString(R.string.total_csfb_fallback_to_3g_Rate);
        texts[7][0] = super.getString(R.string.total_csfb_fallback_to_2g_Delay);
        texts[8][0] = super.getString(R.string.total_csfb_fallback_to_3g_Delay);
        texts[9][0] = super.getString(R.string.total_csfb_successes_delay);
        texts[10][0] = super.getString(R.string.total_csfb_successes);
        texts[11][0] = super.getString(R.string.total_csfb_success_ratio);
        texts[12][0] = super.getString(R.string.total_csfb_dropped_calls);
        texts[13][0] = super.getString(R.string.total_csfb_dropped_call_rate);
        texts[14][0] = super.getString(R.string.total_csfb_Total_Call_Success_Rate);
        texts[15][0] = super.getString(R.string.total_csfb_2G_Return_to_LTE_Count);
        texts[16][0] = super.getString(R.string.total_csfb_2G_Return_to_LTE_Delay);
        texts[17][0] = super.getString(R.string.total_csfb_3G_Return_to_LTE_Count);
        texts[18][0] = super.getString(R.string.total_csfb_3G_Return_to_LTE_Delay);
        return texts;
    }

    /**
     * 获取语音业务统计表文本
     *
     * @return
     */
    private String[][] getVoiceTableTexts() {
        String[][] texts = new String[TABLE_VOICE_ROWS - 1][TABLE_VOICE_COLS];
        texts[0][2] = super.getString(R.string.total_call_mo);
        texts[0][3] = super.getString(R.string.total_call_mt);
        texts[1][0] = super.getString(R.string.total_attemptCount);
        texts[2][0] = super.getString(R.string.total_callconnected);
        texts[3][0] = super.getString(R.string.total_DropCall);
        texts[4][0] = super.getString(R.string.total_callSuccRate);
        texts[5][0] = super.getString(R.string.total_dropCallRate);
        texts[6][0] = super.getString(R.string.total_callDelay);
        return texts;
    }

    @Override
    protected void setTablesDatas() {
        super.setTableCells(this.mVoiceTableTag, 1, 0, false, super.mValueColor, 1, true);
        super.setTableCells(this.mCSFBTableTag, 1, 0, false, super.mValueColor, 1, true);
        super.setTableCells(this.mPESQTableTag, 1, 0, false, super.mValueColor, 1, true);
        super.setTableCells(this.mPOLQATableTag, 1, 0, false, super.mValueColor, 1, true);
    }

    @Override
    protected String[][] getTableValues(String tag) {
        if (this.mVoiceTableTag.equals(tag))
            return this.getVoiceTableValues();
        else if (this.mCSFBTableTag.equals(tag))
            return this.getCSFBTableValues();
        else if (this.mPESQTableTag.equals(tag))
            return this.getPESQTableValues();
        else return this.getPOLQAQTableValues();
    }

    /**
     * 获取POLQA统计表数据
     *
     * @return
     */
    private String[][] getPOLQAQTableValues() {
        String[][] values = new String[TABLE_POLQA_ROWS - 1][TABLE_POLQA_COLS];
        HashMap<String, Long> unifyTimes = TotalDataByGSM.getInstance().getUnifyTimes();
        String value = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._pesq_score.name(),
                TotalDial._pesq_scoreCount.name(), 1, "");
        if (value.trim().length() > 0) {
            try {
                values[0][1] = String.format("%.2f", Float.valueOf(value) / 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // PESQ MOS
        value = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._pesq_mos.name(), TotalDial._pesq_mosCount.name(),
                1, "");
        if (value.trim().length() > 0) {
            try {
                values[1][1] = String.format("%.2f", Float.valueOf(value) / 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return values;
    }

    /**
     * 获取PESQ统计表数据
     *
     * @return
     */
    private String[][] getPESQTableValues() {
        String[][] values = new String[TABLE_PESQ_ROWS - 1][TABLE_PESQ_COLS];
        HashMap<String, Long> unifyTimes = TotalDataByGSM.getInstance().getUnifyTimes();
        String value = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._pesq_score.name(),
                TotalDial._pesq_scoreCount.name(), 1, "");
        if (value.trim().length() > 0) {
            try {
                values[0][1] = String.format("%.2f", Float.valueOf(value) / 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // PESQ MOS
        value = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._pesq_mos.name(), TotalDial._pesq_mosCount.name(),
                1, "");
        if (value.trim().length() > 0) {
            try {
                values[1][1] = String.format("%.2f", Float.valueOf(value) / 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return values;
    }

    /**
     * 获取CSFB统计表数据
     *
     * @return
     */
    private String[][] getCSFBTableValues() {
        String[][] values = new String[TABLE_CSFB_ROWS - 1][TABLE_CSFB_COLS];
        HashMap<String, Long> unifyTimes = TotalDataByGSM.getInstance().getUnifyTimes();
        values[1][2] = TotalDataByGSM.getHashMapValue(unifyTimes, TotalDial._csfb_mo_request.name());
        values[1][3] = TotalDataByGSM.getHashMapValue(unifyTimes, TotalDial._csfb_mt_request.name());
        values[2][2] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mo_RrcReleaseDelay.name(),
                TotalDial._csfb_mo_RrcRelease.name(), 0.001f, "");
        values[2][3] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mt_RrcReleaseDelay.name(),
                TotalDial._csfb_mt_RrcRelease.name(), 0.001f, "");
        values[3][2] = TotalDataByGSM.getHashMapValue(unifyTimes, TotalDial._csfb_mo_proceeding_2G.name());
        values[3][3] = TotalDataByGSM.getHashMapValue(unifyTimes, TotalDial._csfb_mt_proceeding_2G.name());
        values[4][2] = TotalDataByGSM.getHashMapValue(unifyTimes, TotalDial._csfb_mo_proceeding_3G.name());
        values[4][3] = TotalDataByGSM.getHashMapValue(unifyTimes, TotalDial._csfb_mt_proceeding_3G.name());
        values[5][2] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mo_proceeding_2G.name(),
                TotalDial._csfb_mo_request.name(), 100, "%");
        values[5][3] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mt_proceeding_2G.name(),
                TotalDial._csfb_mt_request.name(), 100, "%");
        values[6][2] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mo_proceeding_3G.name(),
                TotalDial._csfb_mo_request.name(), 100, "%");
        values[6][3] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mt_proceeding_3G.name(),
                TotalDial._csfb_mt_request.name(), 100, "%");
        values[7][2] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mo_proceedingDelay_2G.name(),
                TotalDial._csfb_mo_proceeding_2G.name(), 0.001f, "");
        values[7][3] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mt_proceedingDelay_2G.name(),
                TotalDial._csfb_mt_proceeding_2G.name(), 0.001f, "");
        values[8][2] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mo_proceedingDelay_3G.name(),
                TotalDial._csfb_mo_proceeding_3G.name(), 0.001f, "");
        values[8][3] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mt_proceedingDelay_3G.name(),
                TotalDial._csfb_mt_proceeding_3G.name(), 0.001f, "");
        values[9][2] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mo_SuccessDelay.name(),
                TotalDial._csfb_mo_Established.name(), 0.001f, "");
        values[9][3] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mt_SuccessDelay.name(),
                TotalDial._csfb_mt_Established.name(), 0.001f, "");
        values[10][2] = TotalDataByGSM.getHashMapValue(unifyTimes, TotalDial._csfb_mo_Established.name());
        values[10][3] = TotalDataByGSM.getHashMapValue(unifyTimes, TotalDial._csfb_mt_Established.name());
        values[11][2] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mo_Established.name(),
                TotalDial._csfb_mo_request.name(), 100, "%");
        values[11][3] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mt_Established.name(),
                TotalDial._csfb_mt_request.name(), 100, "%");
        values[12][2] = TotalDataByGSM.getHashMapValue(unifyTimes, TotalDial._csfb_mo_drop.name());
        values[12][3] = TotalDataByGSM.getHashMapValue(unifyTimes, TotalDial._csfb_mt_drop.name());
        values[13][2] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mo_drop.name(),
                TotalDial._csfb_mo_Established.name(), 100, "%");
        values[13][3] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mt_drop.name(),
                TotalDial._csfb_mt_Established.name(), 100, "%");
        values[14][2] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mo_CallEnd.name(),
                TotalDial._csfb_mo_request.name(), 100, "%");
        values[14][3] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mt_CallEnd.name(),
                TotalDial._csfb_mt_request.name(), 100, "%");
        values[15][2] = TotalDataByGSM.getHashMapValue(unifyTimes, TotalDial._csfb_mo_2G_ReturnLTE.name());
        values[15][3] = TotalDataByGSM.getHashMapValue(unifyTimes, TotalDial._csfb_mt_2G_ReturnLTE.name());
        values[16][2] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mo_2G_ReturnLTE_Delay.name(),
                TotalDial._csfb_mo_2G_ReturnLTE.name(), 0.001f, "");
        values[16][3] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mt_2G_ReturnLTE_Delay.name(),
                TotalDial._csfb_mt_2G_ReturnLTE.name(), 0.001f, "");
        values[17][2] = TotalDataByGSM.getHashMapValue(unifyTimes, TotalDial._csfb_mo_3G_ReturnLTE.name());
        values[17][3] = TotalDataByGSM.getHashMapValue(unifyTimes, TotalDial._csfb_mt_3G_ReturnLTE.name());
        values[18][2] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mo_3G_ReturnLTE_Delay.name(),
                TotalDial._csfb_mo_3G_ReturnLTE.name(), 0.001f, "");
        values[18][3] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._csfb_mt_3G_ReturnLTE_Delay.name(),
                TotalDial._csfb_mt_3G_ReturnLTE.name(), 0.001f, "");
        return values;
    }

    /**
     * 获取语音统计数据
     *
     * @return
     */
    private String[][] getVoiceTableValues() {
        String[][] values = new String[TABLE_VOICE_ROWS - 1][TABLE_VOICE_COLS];
        HashMap<String, Long> unifyTimes = TotalDataByGSM.getInstance().getUnifyTimes();
        values[1][2] = TotalDataByGSM.getHashMapValue(unifyTimes, TotalDial._moTrys.name());
        values[1][3] = TotalDataByGSM.getHashMapValue(unifyTimes, TotalDial._mtTrys.name());
        values[2][2] = TotalDataByGSM.getHashMapValue(unifyTimes, TotalDial._moConnects.name());
        values[2][3] = TotalDataByGSM.getHashMapValue(unifyTimes, TotalDial._mtConnects.name());
        values[3][2] = TotalDataByGSM.getHashMapValue(unifyTimes, TotalDial._moDropcalls.name());
        values[3][3] = TotalDataByGSM.getHashMapValue(unifyTimes, TotalDial._mtDropcalls.name());
        values[4][2] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._moConnects.name(), TotalDial._moTrys.name(),
                100, "%");
        values[4][3] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._mtConnects.name(), TotalDial._mtTrys.name(),
                100, "%");
        values[5][2] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._moDropcalls.name(),
                TotalDial._moConnects.name(), 100, "%");
        values[5][3] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._mtDropcalls.name(),
                TotalDial._mtConnects.name(), 100, "%");
        values[6][2] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._moCalldelay.name(),
                TotalDial._moDelaytimes.name(), 0.001f, "");
        values[6][3] = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalDial._mtCalldelay.name(),
                TotalDial._mtDelaytimes.name(), 0.001f, "");
        return values;
    }
}
