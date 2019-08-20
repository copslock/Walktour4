package com.walktour.service.test;

import com.dingli.ott.event.MultipleEnvConfig;
import com.walktour.base.util.AppFilePathUtil;

/**
 * @author zhicheng.chen
 * @date 2019/1/24
 */
public class OttLibsManager {


    public static void loadLib(){
        System.loadLibrary("crystax_shared");
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("miniSDL");
        System.loadLibrary("ipc2");
        System.loadLibrary("ipc2tooljni");
        System.loadLibrary("ipc2tool");
        System.loadLibrary("mysock");
        System.loadLibrary("iconv");
        System.loadLibrary("myglib");
        System.loadLibrary("mypcap");
        System.loadLibrary("CustomWireshark");
        System.loadLibrary("MultipleAppAnalysisModule");

        String exe_path = AppFilePathUtil.getInstance().getAppLibDirectory();
        MultipleEnvConfig.getInstance().dll_path = exe_path;
        MultipleEnvConfig.getInstance().exe_path = exe_path;
    }
}
