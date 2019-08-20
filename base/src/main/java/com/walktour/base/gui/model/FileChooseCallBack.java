package com.walktour.base.gui.model;

import java.util.List;

/**
 * 文件选择回调函数
 * Created by wangk on 2017/8/30.
 */

public interface FileChooseCallBack extends BaseCallBack {
    /**
     * 执行成功
     *
     * @param fileList 选择的文件列表
     */
    void onSuccess(List<FileChoose> fileList);

}
