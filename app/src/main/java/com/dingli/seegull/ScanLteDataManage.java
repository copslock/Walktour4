package com.dingli.seegull;

import android.util.Log;

import com.walktour.gui.task.activity.scanner.model.CellInfo;
import com.walktour.gui.task.activity.scanner.model.LteCellDataPilotModel;
import com.walktour.gui.task.activity.scanner.model.LtePssPilotModel;
import com.walktour.gui.task.activity.scanner.model.LteRsPilotModel;
import com.walktour.gui.task.activity.scanner.model.LteSssPilotModel;
import com.walktour.model.BaseStructParseModel;

import java.util.ArrayList;
import java.util.List;

public class ScanLteDataManage {
	
	
	private static ScanLteDataManage dataManage = null;
	
	
	/**
	 * @return
	 */
	public static ScanLteDataManage getInstance() {
		if(dataManage == null){
			dataManage = new ScanLteDataManage();
		}
		return dataManage;
	}
	
	
	/**
	 * 根据cellInfo查找相关pss、sss、ss的带宽、band
	 */

	public  <T extends BaseStructParseModel> List<CellInfo> findBandRelativeModels(List<T> models,List<LteCellDataPilotModel> baseCellInfos) {
		List<CellInfo> cellInfos = new ArrayList<CellInfo>();
		boolean flag = false;
		if(models == null || baseCellInfos == null){
			return cellInfos;
		}
		try {
			for (int i = 0; i < models.size(); i++) {
				for (int j = 0; j < baseCellInfos.size(); j++) {
					if (models.get(i) instanceof LtePssPilotModel) {
						LtePssPilotModel pssPilotModel = (LtePssPilotModel)models.get(i);
						if(pssPilotModel.getEarfcn() == baseCellInfos.get(j).getEarfcn() ){
							cellInfos.add(new CellInfo(baseCellInfos.get(j).getBandWidth(), baseCellInfos.get(j).getBand(),baseCellInfos.get(j).getNumOfRB(),pssPilotModel.getTimeOffset()));
							Log.i("LTEView", "come in pss!" + i);
							flag = true;
							break;
						}
					} else if (models.get(i) instanceof LteRsPilotModel) {
						LteRsPilotModel rsPilotModel = (LteRsPilotModel)models.get(i);
						if(rsPilotModel.getEarfcn() == baseCellInfos.get(j).getEarfcn() ){
							cellInfos.add(new CellInfo(baseCellInfos.get(j).getBandWidth(), baseCellInfos.get(j).getBand(),baseCellInfos.get(j).getNumOfRB(),rsPilotModel.getTimeOffset()));
							Log.i("LTEView", "come in rs!" + i);
							flag = true;
							break;
						}
						
					} else if (models.get(i) instanceof LteSssPilotModel){
						LteSssPilotModel sssPilotModel = (LteSssPilotModel)models.get(i);
						if(sssPilotModel.getEarfcn() == baseCellInfos.get(j).getEarfcn() ){
							Log.i("LTEView", "come in sss!" + i);
							cellInfos.add(new CellInfo(baseCellInfos.get(j).getBandWidth(), baseCellInfos.get(j).getBand(),baseCellInfos.get(j).getNumOfRB(), sssPilotModel.getTimeOffset()));
							flag = true;
							break;
						}
					}
					
				}
				if(!flag){
					Log.i("LTEView", "come in false!" + i);
					cellInfos.add(new CellInfo(-9999, -9999,-9999,-9999));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cellInfos;
	}
	
}
