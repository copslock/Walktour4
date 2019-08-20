package com.walktour.base.gui.presenter;

import android.content.Intent;

import com.walktour.base.gui.activity.FileChooseActivity;
import com.walktour.base.gui.fragment.FileChooseFragment;
import com.walktour.base.gui.model.FileChoose;
import com.walktour.base.gui.model.FileChooseCallBack;
import com.walktour.base.gui.service.FileChooseService;
import com.walktour.base.util.LogUtil;

import java.io.File;
import java.util.List;

/**
 * 文件选择交互类
 * Created by wangk on 2017/6/28.
 */

public class FileChooseFragmentPresenter extends BaseFragmentPresenter {
    /**
     * 日志标识
     */
    private static final String TAG = "FileChooseFragmentPresenter";
    /**
     * 关联视图
     */
    private FileChooseFragment mFragment;
    /**
     * 关联业务类
     */
    private FileChooseService mService;
    /**
     * 当前显示的子文件列表的父文件路径
     */
    private String mParentFilePath;
    /**
     * 当前显示的子文件列表的父文件等级
     */
    private int mParentFileLevel;
    /**
     * 文件类型过滤，以逗号分隔
     */
    private String mFilterTypes;

    public FileChooseFragmentPresenter(FileChooseFragment fragment, FileChooseService service) {
        super(fragment);
        this.mFragment = fragment;
        this.mService = service;
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

    /**
     * 处理选择的文件，如果是目录则进入下级列表，如果是文件则返回
     *
     * @param file 文件类型
     */
    public void selectFile(FileChoose file) {
        switch (file.getFileType()) {
            case FileChoose.FILE_TYPE_DIRECTORY:
            case FileChoose.FILE_TYPE_PARENT:
                this.loadFileChilds(file.getFilePath(), file.getLevel());
                break;
            case FileChoose.FILE_TYPE_FILE:
                this.backToActivity(file);
                break;
        }
    }

    /**
     * 把选择的文件返回给调用界面
     *
     * @param file 文件对象
     */
    private void backToActivity(FileChoose file) {
        LogUtil.d(TAG, "----backToActivity----");
        Intent intent = new Intent();
        intent.putExtra("file_path", file.getFilePath());
        this.getActivity().setResult(FileChooseActivity.FILE_CHOOSE_RESULT_CODE, intent);
        this.getActivity().finish();
    }

    @Override
    public void loadData() {
        String filePath = super.getIntent().getStringExtra("file_path");
        this.mFilterTypes = super.getIntent().getStringExtra("filter_types");
        this.loadFileChilds(filePath, 0);
    }

    /**
     * 加载指定文件的子目录和子文件
     *
     * @param filePath 指定文件路径
     * @param level    文件等级
     */
    private void loadFileChilds(String filePath, int level) {
        this.mFragment.showParentFilePath(filePath);
        this.mParentFilePath = filePath;
        this.mParentFileLevel = level;
        this.mService.loadFileChilds(this.mFragment.getContext(), filePath, level, this.mFilterTypes, new FileChooseCallBack() {
            @Override
            public void onFailure(String message) {

            }

            @Override
            public void onSuccess(List<FileChoose> fileList) {
                mFragment.showFragment(fileList);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (this.mParentFileLevel == 0)
            this.getActivity().finish();
        else {
            File file = new File(this.mParentFilePath);
            this.loadFileChilds(file.getParentFile().getAbsolutePath(), this.mParentFileLevel - 1);
        }
    }
}
