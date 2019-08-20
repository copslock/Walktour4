package com.walktour.report.utils.excel;

import com.walktour.base.util.LogUtil;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;


/**
 * Excel操作工具类
 */
public class ExcelUtils {
    /**
     * 标签名字
     */
    private static final String TAG = ExcelUtils.class.getSimpleName();

    /**
     * 替换Excel模板文件内容
     *
     * @param datas          文档数据,key-value对集合
     * @param sourceFilePath Excel模板文件路径
     * @param targetFilePath Excel生成文件路径
     */
    public static boolean replaceModel(Map<String, String> datas, String sourceFilePath, String targetFilePath) {
        boolean bool = true;
        try {
            if (null == datas || datas.isEmpty()) {
                return bool;
            }
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(sourceFilePath));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                HSSFSheet sheet = wb.getSheetAt(i);
                for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                    HSSFRow row = sheet.getRow(j);
                    if (row == null) continue;
                    for (int k = row.getFirstCellNum(); k <= row.getLastCellNum(); k++) {
                        HSSFCell cell = row.getCell(k);
                        if (cell == null) continue;
                        if (cell.getCellTypeEnum() == CellType.STRING) {
//						if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            String str = cell.getStringCellValue();
                            // 写入单元格内容
                            cell.setCellType(CellType.STRING);
//							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                            // cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                            for (Map.Entry<String, String> entry : datas.entrySet()) {
                                if (entry.getKey().equals(str)) {
                                    cell.setCellValue(entry.getValue() + "");
                                }
                            }
                        }
                    }
                }
            }
            // 输出文件
            FileOutputStream fileOut = new FileOutputStream(targetFilePath);
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            bool = false;
            e.printStackTrace();
            LogUtil.w(TAG, e.getMessage());
        }
        return bool;
    }
}
