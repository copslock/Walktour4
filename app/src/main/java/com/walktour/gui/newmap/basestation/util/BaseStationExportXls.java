package com.walktour.gui.newmap.basestation.util;

import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * 基站数据导出xls格式文件
 *
 * @author jianchao.wang
 */
public class BaseStationExportXls extends BaseStationExportText {

    @Override
    protected void exportFile(int netType, String fileName) throws Exception {
        File file = new File(path + File.separator + fileName + ".xls");
        if (!file.exists())
            file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "GBK");
        try {
            osw.write(this.createTextHeader(netType));
            for (BaseStation bs : this.baseStationList) {
                for (BaseStationDetail detail : bs.details) {
                    osw.write(this.createTextRow(netType, detail));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            osw.close();
        }
    }
}
