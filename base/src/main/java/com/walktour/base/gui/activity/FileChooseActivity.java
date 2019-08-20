package com.walktour.base.gui.activity;

import com.walktour.base.R;
import com.walktour.base.gui.fragment.FileChooseFragment;

/**
 * 硬盘文件选择界面
 * Created by wangk on 2017/6/28.
 */

public class FileChooseActivity extends SimpleBaseActivity {
    /**
     * 日志标识
     */
    private static final String TAG = "FileChooseActivity";
    /**
     * 文件选择结果标识
     */
    public static final int FILE_CHOOSE_RESULT_CODE = 10001;

    @Override
    protected void onCreate() {
        super.setToolbarTitle(R.string.file_choose);
    }

    @Override
    protected void initFragments() {
        super.addFragment(new FileChooseFragment());
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

    @Override
    public void onBackPressed() {
        this.getCurrentFragment().getPresenter().onBackPressed();

    }

}
