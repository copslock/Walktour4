package com.walktour.gui.singlestation.setting.activity;

import com.walktour.base.gui.activity.SimpleTabHostActivity;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.setting.fragment.SettingInsideFragment;
import com.walktour.gui.singlestation.setting.fragment.SettingOutdoorFragment;

public class SettingActivity extends SimpleTabHostActivity {

    private static final String TAG = "SettingActivity";

    @Override
    protected void onCreate() {
        super.setToolbarTitle(R.string.sys_map_parameter_go_or_nogo_setting);
    }

    @Override
    protected void initFragments() {
        super.addFragment(new SettingInsideFragment());
        super.addFragment(new SettingOutdoorFragment());
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }
}
