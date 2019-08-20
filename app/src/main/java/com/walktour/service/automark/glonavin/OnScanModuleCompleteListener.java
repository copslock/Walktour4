package com.walktour.service.automark.glonavin;

import java.util.ArrayList;

/**
 * Created by Yi.Lin on 2018/4/10.
 * 搜索定位蓝牙模块结束回调
 */

public interface OnScanModuleCompleteListener {
    void onComplete(ArrayList<String> addressList);
}
