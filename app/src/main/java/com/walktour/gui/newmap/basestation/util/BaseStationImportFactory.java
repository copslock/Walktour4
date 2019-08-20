package com.walktour.gui.newmap.basestation.util;

import com.walktour.framework.database.model.BaseStation;
import com.walktour.gui.WalktourApplication;

import org.apache.poi.poifs.filesystem.NotOLE2FileException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 基站数据导入工厂类
 *
 * @author jianchao.wang
 */
public class BaseStationImportFactory {
    /**
     * 导出文件类型
     */
    public enum FileType {
        TEXT, XLS, KML, MIF
    }

    /**
     * 唯一实例
     */
    private static BaseStationImportFactory sInstance;

    private BaseStationImportFactory() {

    }

    /**
     * 返回唯一实例
     *
     * @return 唯一实例
     */
    public static BaseStationImportFactory getInstance() {
        if (sInstance == null) {
            sInstance = new BaseStationImportFactory();
        }
        return sInstance;
    }

	/**
	 * 导入基站数据
	 *
	 * @param fileName 文件绝对路径
	 * @throws Exception 异常
	 */
	public List<BaseStation> importFile(String fileName) throws Exception {
		BaseStationImportBase importBase = null;
		try {
			File file = new File(fileName);
			if (!file.exists())
				return new ArrayList<>();
			if (file.getName().endsWith(".xls"))
				importBase = new BaseStationImportXls();
			else if (file.getName().endsWith(".kml"))
				importBase = new BaseStationImportKml();
			else if (file.getName().endsWith(".mif"))
				importBase = new BaseStationImportMif();
			else
				importBase = new BaseStationImportText();
			importBase.importFile(fileName);
		}  catch (NotOLE2FileException e) {
			importBase = new BaseStationImportText();
			importBase.importFile(fileName);
			e.printStackTrace();
		}catch (Exception e) {
			com.walktour.Utils.ToastUtil.showToastShort(WalktourApplication.getAppContext(), "导入失败");
			e.printStackTrace();
		}
		if(importBase == null){
			return Collections.emptyList();
		}
		return importBase.getBaseStationList();
	}
}
