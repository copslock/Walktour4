package com.walktour.gui.newmap2.filter;

import android.content.Context;

import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.StringUtil;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.newmap2.overlay.MapCache;

import java.util.ArrayList;
import java.util.List;

/**
 * lte 过滤
 * @author zhicheng.chen
 * @date 2019/3/15
 */
public class LteParamFilter implements IParamFilter {

    private final int MIN_VALUE = 0;
    private final int MAX_VALUE = 99999;
    private final String MIN = "_Min";
    private final String MAX = "_Max";
    private final FilterMap<String, Integer> map = new FilterMap<>();

    @Override
    public int getNetType() {
        return WalktourConst.NetWork.LTE;
    }

    @Override
    public String getFirstParamName() {
        return "PCI";
    }

    @Override
    public String getSecondParamName() {
        return "EARFCN";
    }

    @Override
    public void saveFirstParamRange(int min, int max) {
        MapCache.saveParams(getFirstParamName() + MIN, min);
        MapCache.saveParams(getFirstParamName() + MAX, max);
    }

    @Override
    public void saveSecondParamRange(int min, int max) {
        MapCache.saveParams(getSecondParamName() + MIN, min);
        MapCache.saveParams(getSecondParamName() + MAX, max);
    }

    @Override
    public int getFirstParamMin() {
        return MapCache.getParams(getFirstParamName() + MIN, MIN_VALUE);
    }

    @Override
    public int getFirstParamMax() {
        return MapCache.getParams(getFirstParamName() + MAX, MAX_VALUE);
    }

    @Override
    public int getSecondParamMin() {
        return MapCache.getParams(getSecondParamName() + MIN, MIN_VALUE);
    }

    @Override
    public int getSecondParamMax() {
        return MapCache.getParams(getSecondParamName() + MAX, MAX_VALUE);
    }

    @Override
    public void resetFirstParam() {
        MapCache.saveParams(getFirstParamName() + MIN, MIN_VALUE);
        MapCache.saveParams(getFirstParamName() + MAX, MAX_VALUE);
    }

    @Override
    public void resetSecondParam() {
        MapCache.saveParams(getSecondParamName() + MIN, MIN_VALUE);
        MapCache.saveParams(getSecondParamName() + MAX, MAX_VALUE);
    }

    @Override
    public boolean filter(BaseStation station) {
        if (station.netType == getNetType()) {
            List<BaseStationDetail> tempDetailList = new ArrayList<>();
            List<BaseStationDetail> details = station.details;
            for (BaseStationDetail detail : details) {
                int param1 = 0;
                try {

                    param1 = Integer.parseInt(StringUtil.isEmpty(detail.pci) ? "" : detail.pci);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                int param2 = 0;
                try {
                    param2 = Integer.parseInt(StringUtil.isEmpty(detail.earfcn) ? "" : detail.earfcn);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (getFirstParamMin() < param1 && param1 < getFirstParamMax()
                        && getSecondParamMin() < param2 && param2 < getSecondParamMax()) {
                    tempDetailList.add(detail);
                }
            }
            station.details.clear();
            station.details.addAll(tempDetailList);
            return true;
        }
        return false;
    }
}
