package com.walktour.base.gui.service;

import android.content.Context;

import com.walktour.base.R;
import com.walktour.base.gui.model.FileChoose;
import com.walktour.base.gui.model.FileChooseCallBack;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.StringUtil;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Singleton;

/**
 * 文件选择服务类
 * Created by wangk on 2017/6/28.
 */
@Singleton
public class FileChooseService {
    /**
     * 日志标识
     */
    private static final String TAG = "FileChooseService";
    /**
     * 日期格式化
     */
    private SimpleDateFormat mDateFormat;

    public FileChooseService() {
        LogUtil.d(TAG, "---new()---");
        this.mDateFormat = new SimpleDateFormat("dd MM yy HH:mm:ss", Locale.getDefault());
    }

    /**
     * 获得指定文件的子文件和子目录
     *
     * @param context     上下文
     * @param filePath    查询参数 请求子目录和子文件的文件路径
     * @param level       当前显示的文件列表的等级，以最开始查询目录的子目录为1级
     * @param filterTypes 要显示的文件类型,以","分割
     * @param callBack    回调结果类
     */
    public void loadFileChilds(Context context, String filePath, int level, String filterTypes, final FileChooseCallBack callBack) {
        LogUtil.d(TAG, "----loadFileChilds----");
        List<FileChoose> fileList = new ArrayList<>();
        if (StringUtil.isEmpty(filePath)) {
            callBack.onFailure(context.getString(R.string.file_not_exist));
            return;
        }
        File parent = new File(filePath);
        //生成上层目录行
        if (level > 1) {
            FileChoose fileChoose = new FileChoose();
            File parentFile = new File(parent.getParentFile().getAbsolutePath());
            fileChoose.setFileType(FileChoose.FILE_TYPE_PARENT);
            fileChoose.setFileName("..");
            fileChoose.setFileDescription(context.getString(R.string.file_choose_parent_file));
            fileChoose.setFilePath(parentFile.getAbsolutePath());
            fileChoose.setLevel(level - 2);
            fileList.add(fileChoose);
        }
        if (parent.exists() && parent.isDirectory()) {
            for (File file : parent.listFiles()) {
                if (file.isFile() && !StringUtil.isEmpty(filterTypes)) {
                    String[] fileTypes = filterTypes.split(",");
                    boolean isFind = false;
                    for (String fileType : fileTypes) {
                        if (file.getName().endsWith(fileType)) {
                            isFind = true;
                            break;
                        }
                    }
                    if (!isFind)
                        continue;
                }
                if (file.isDirectory() && (file.listFiles() == null || file.listFiles().length == 0))
                    continue;
                FileChoose fileChoose = new FileChoose();
                if (file.isDirectory())
                    fileChoose.setFileType(FileChoose.FILE_TYPE_DIRECTORY);
                else
                    fileChoose.setFileType(FileChoose.FILE_TYPE_FILE);
                fileChoose.setFileName(file.getName());
                fileChoose.setFilePath(file.getAbsolutePath());
                StringBuilder description = new StringBuilder();
                description.append(this.mDateFormat.format(new Date(file.lastModified())));
                if (file.isFile()) {
                    description.append(" ").append(this.getFileSize(file.length()));
                }
                fileChoose.setFileDescription(description.toString());
                fileChoose.setLevel(level + 1);
                fileList.add(fileChoose);
            }
        }
        callBack.onSuccess(fileList);
    }

    /**
     * 获取文件大小
     *
     * @param fileLength 文件实际大小
     * @return 带单位的文件大小
     */
    private String getFileSize(long fileLength) {
        String size;
        DecimalFormat df = new DecimalFormat("#.00");
        long cal1 = 1024;
        long cal2 = 1024 * 1024;
        long cal3 = 1024 * 1024 * 1024;
        if (fileLength == 0) {
            size = "0B";
        } else if (fileLength < cal1) {
            size = fileLength + "B";
        } else if (fileLength < cal2) {
            size = df.format((double) fileLength / cal1) + "KB";
        } else if (fileLength < cal3) {
            size = df.format((double) fileLength / cal2) + "MB";
        } else {
            size = df.format((double) fileLength / cal3) + "GB";
        }
        return size;
    }

}
