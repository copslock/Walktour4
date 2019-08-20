package com.walktour.gui.newmap.basestation.util;

import android.content.Context;

/**
 * 基站导出工厂类
 *
 * @author jianchao.wang
 */
public class BaseStationExportFactory {
    /**
     * 导出文件类型
     */
    public enum FileType {
        TEXT, XLS, KML, MIF
    }

    /**
     * 唯一实例
     */
    private static BaseStationExportFactory sInstance;

    private BaseStationExportFactory() {

    }

    /**
     * 返回唯一实例
     *
     * @return 唯一实例
     */
    public static BaseStationExportFactory getInstance() {
        if (sInstance == null) {
            sInstance = new BaseStationExportFactory();
        }
        return sInstance;
    }

    /**
     * 导出生成基站数据文件
     *
     * @param context  上下文
     * @param path     文件路径
     * @param fileType 文件类型
     */
    public void exportFile(Context context, String path, FileType fileType) {
        BaseStationExportBase export;
        switch (fileType) {
            case XLS:
                export = new BaseStationExportXls();
                break;
            case KML:
                export = new BaseStationExportKml();
                break;
            case MIF:
                export = new BaseStationExportMif();
                break;
            default:
                export = new BaseStationExportText();
                break;
        }
        export.exportFile(context, path);
    }
}
