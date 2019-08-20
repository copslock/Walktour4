package com.walktour.gui.newmap.basestation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.walktour.base.util.LogUtil;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;
import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 基站详情弹出框显示
 *
 * @author jianchao.wang
 */
@SuppressLint("InflateParams")
public class BaseStationDetailPopWindow {
    /**
     * 日志标识
     */
    private final static String TAG = "BaseStationDetailPopWindow";
    /**
     * 共享参数文件名
     */
    private final static String PREFERENCE_NAME = "com.walktour.gui.map";
    /**
     * 弹出窗
     */
    private PopupWindow pop;
    /**
     * 显示的基站数据
     */
    private BaseStation baseStation;
    /**
     * 共享参数
     */
    private SharedPreferences mSharedPreferences;
    /**
     * 视图对象
     */
    private View view;
    /**
     * 父视图
     */
    private View parent;
    /**
     * 活动类
     */
    private Activity activity;
    /**
     * 删除按钮的监听
     */
    private OnDeleteListener listener;
    private TextView mTvLatLng;
    private LinearLayout mContainer;
    private ScrollView mScrollView;

    public BaseStationDetailPopWindow(View parent, Activity activity, BaseStation baseStation) {
        this.parent = parent;
        this.activity = activity;
        mSharedPreferences = activity.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        this.view = layoutInflater.inflate(R.layout.basedetail_activity, null);
        mTvLatLng = (TextView) this.view.findViewById(R.id.baselonlat);
        mContainer = (LinearLayout) this.view.findViewById(R.id.base);
        this.mScrollView = (ScrollView) this.view.findViewById(R.id.linearLayout1);
        this.baseStation = baseStation;
        this.fillBaseData();
        initView();
    }

    private void initView() {
        ImageButton ibDelete = (ImageButton) view.findViewById(R.id.ib_delete_dialog);
        ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.hasDelete();
                closePopWindow();
            }
        });
    }

    public void setOnDeleteListener(OnDeleteListener listener) {
        this.listener = listener;
    }

    /**
     * 显示弹出框
     */
    @SuppressLint("NewApi")
    public void showPopWindow() {
        LogUtil.d(TAG, "-------showPopWindow-----start----");
        float density = this.activity.getResources().getDisplayMetrics().density;
        int width = (int) (240 * density);
        int height = (int) (300 * density);
        pop = new PopupWindow(view, width, height);
        int[] location = new int[2];
        this.activity.getWindow().getDecorView().getLocationOnScreen(location);
        int x = location[0] + (this.parent.getMeasuredWidth() - width) / 2;
        int y = location[1] + (this.parent.getMeasuredHeight() - height) / 2;
        if (!activity.isDestroyed() && !pop.isShowing()) {//先判斷是否已經显示，否则会抛出WindowManager: android.view.WindowLeaked异常
            this.pop.showAtLocation(this.parent, Gravity.TOP | Gravity.LEFT, x, y);
        }
        LogUtil.d(TAG, "-------showPopWindow-----end----");
    }

    /**
     * 关闭弹出框
     */
    public void closePopWindow() {
        if (pop != null && pop.isShowing()) {//先判斷是否已經显示，否则会抛出WindowManager: android.view.WindowLeaked异常
            this.pop.dismiss();
        }

    }

    /**
     * 2018/6/8 czc : 不需要每次都new一个对象，复用同一个,后面有时间建议改成listview
     *
     * @param station
     */
    public void setBaseStation(BaseStation station) {
        mScrollView.scrollTo(0, 0);
        mContainer.removeAllViews();
        this.baseStation = station;
        fillBaseData();
        LogUtil.d("max","baseStation:"+baseStation);
    }

    /**
     * 填充基站数据
     */
    @SuppressWarnings("deprecation")
    private void fillBaseData() {

        mTvLatLng.setText(baseStation.longitude + " , " + baseStation.latitude);

        List<String> paramList = new ArrayList<>();
        for (int i = 0; i < baseStation.details.size(); i++) {
            BaseStationDetail detail = baseStation.details.get(i);
            paramList.clear();
            paramList.add("Resodential " + (i + 1));
//            paramList.add(StringUtil.gbkToUtf8(baseStation.name));
            paramList.add(baseStation.name);
            switch (baseStation.netType) {
                case BaseStation.NETTYPE_GSM:
                    getGsmParams(paramList, detail);
                    break;
                case BaseStation.NETTYPE_WCDMA:
                    getWcdmaParams(paramList, detail);
                    break;
                case BaseStation.NETTYPE_CDMA:
                    getCdmaParams(paramList, detail);
                    break;
                case BaseStation.NETTYPE_TDSCDMA:
                    getTdscdmaParams(paramList, detail);
                    break;
                case BaseStation.NETTYPE_LTE:
                    getLteParams(paramList, detail);
                    break;
				case BaseStation.NETTYPE_NBIOT:
					getNbParams(paramList, detail);
					break;
                default:
                    break;
            }
            View row;
            for (int j = 0; j < paramList.size(); j = j + 2) {
                row = LayoutInflater.from(activity).inflate(R.layout.basedetail_row, null);
                if (j == 0) {
                    row.setBackgroundColor(this.view.getContext().getResources().getColor(R.color.base_list_item_bg_select));
                }
                TextView param1 = (TextView) row.findViewById(R.id.param1);
                param1.setText(paramList.get(j));
                TextView param2 = (TextView) row.findViewById(R.id.param2);
                if (j + 1 < paramList.size()) {
                    param2.setText(paramList.get(j + 1));
                } else {
                    param2.setVisibility(View.GONE);
                }
                mContainer.addView(row, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            }
        }
    }

	/**
	 * 获取NB-Iot基站参数
	 * @param paramList
	 * @param detail
	 */
	private void getNbParams(List<String> paramList, BaseStationDetail detail) {
		if (mSharedPreferences.getBoolean("AZIMUTH", true)) {
			paramList.add(this.view.getContext().getString(R.string.map_base_azimuth) + ":" + detail.bearing);
		}

		if (mSharedPreferences.getBoolean("PCI", true)) {
			paramList.add("PCI:" + detail.pci);
		}

		if (mSharedPreferences.getBoolean("EARFCN", true)) {
			paramList.add("EARFCN:" + detail.earfcn);
		}

		if (mSharedPreferences.getBoolean("eNodeB ID", true)) {
			paramList.add("eNodeB ID:" + detail.main.enodebId);
		}

//		if (mSharedPreferences.getBoolean("enbid_lcellid", true)) {
//			paramList.add("enbid_lcellid:" + detail.enbid_lcellid);
//		}
	}

    /**
     * 获取LTE基站参数
     *
     * @param paramList 参数列表
     * @param detail    基站详情对象
     */
    private void getLteParams(List<String> paramList, BaseStationDetail detail) {
        if (mSharedPreferences.getBoolean("AZIMUTH", true)) {
            paramList.add(this.view.getContext().getString(R.string.map_base_azimuth) + ":" + detail.bearing);
        }

        if (mSharedPreferences.getBoolean("PCI", true)) {
            paramList.add("PCI:" + detail.pci);
        }

        if (mSharedPreferences.getBoolean("EARFCN", true)) {
            paramList.add("EARFCN:" + detail.earfcn);
        }

        if (mSharedPreferences.getBoolean("eNodeB ID", true)) {
            paramList.add("eNodeB ID:" + detail.main.enodebId);
        }

        if (mSharedPreferences.getBoolean("eNodeB IP", true)) {
            paramList.add("eNodeB IP:" + detail.enodebIp);
        }
    }

    /**
     * 获取TDSCDMA基站参数
     *
     * @param paramList 参数列表
     * @param detail    基站详情对象
     */
    private void getTdscdmaParams(List<String> paramList, BaseStationDetail detail) {
        if (mSharedPreferences.getBoolean("CELLID", true)) {
            paramList.add("Cell Id:" + detail.cellId);
        }

        if (mSharedPreferences.getBoolean("AZIMUTH", true)) {
            paramList.add(this.view.getContext().getString(R.string.map_base_azimuth) + ":" + detail.bearing);
        }

        if (mSharedPreferences.getBoolean("LAC", true)) {
            paramList.add("LAC:" + detail.lac);
        }

        if (mSharedPreferences.getBoolean("UARFCN", true)) {
            paramList.add("UARFCN:" + detail.uarfcn);
        }

        if (mSharedPreferences.getBoolean("CPI", true)) {
            paramList.add("CPI:" + detail.cpi);
        }
    }

    /**
     * 获取CDMA基站参数
     *
     * @param paramList 参数列表
     * @param detail    基站详情对象
     */
    private void getCdmaParams(List<String> paramList, BaseStationDetail detail) {
        if (mSharedPreferences.getBoolean("AZIMUTH", true)) {
            paramList.add(this.view.getContext().getString(R.string.map_base_azimuth) + ":" + detail.bearing);
        }
        if (mSharedPreferences.getBoolean("PN", true)) {
            paramList.add("PN:" + detail.pn);
        }

        if (mSharedPreferences.getBoolean("Frequency", true)) {
            paramList.add("Frequency:" + detail.frequency);
        }

        if (mSharedPreferences.getBoolean("BID", true)) {
            paramList.add("BID:" + detail.bid);
        }

        if (mSharedPreferences.getBoolean("SID", true)) {
            paramList.add("SID:" + detail.sid);
        }

        if (mSharedPreferences.getBoolean("NID", true)) {
            paramList.add("NID:" + detail.nid);
        }

        if (mSharedPreferences.getBoolean("EV PN", true)) {
            paramList.add("EV PN:" + detail.evPn);
        }
    }

    /**
     * 获取WCDMA基站参数
     *
     * @param paramList 参数列表
     * @param detail    基站详情对象
     */
    private void getWcdmaParams(List<String> paramList, BaseStationDetail detail) {
        if (mSharedPreferences.getBoolean("CELLID", true)) {
            paramList.add("Cell ID:" + detail.cellId);
        }

        if (mSharedPreferences.getBoolean("AZIMUTH", true)) {
            paramList.add(this.view.getContext().getString(R.string.map_base_azimuth) + ":" + detail.bearing);
        }

        if (mSharedPreferences.getBoolean("LAC", true)) {
            paramList.add("LAC:" + detail.lac);
        }

        if (mSharedPreferences.getBoolean("UARFCN", true)) {
            paramList.add("UARFCN:" + detail.uarfcn);
        }

        if (mSharedPreferences.getBoolean("PSC", true)) {
            paramList.add("PSC:" + detail.psc);
        }
    }

    /**
     * 获取GSM基站参数
     *
     * @param paramList 参数列表
     * @param detail    基站详情对象
     */
    private void getGsmParams(List<String> paramList, BaseStationDetail detail) {
        if (mSharedPreferences.getBoolean("CELLID", true)) {
            paramList.add("Cell ID:" + detail.cellId);
        }
        if (mSharedPreferences.getBoolean("AZIMUTH", true)) {
            paramList.add(this.view.getContext().getString(R.string.map_base_azimuth) + ":" + detail.bearing);
        }

        if (mSharedPreferences.getBoolean("BCCH", true)) {
            paramList.add("BCCH:" + detail.bcch);
        }

        if (mSharedPreferences.getBoolean("LAC", true)) {
            paramList.add("LAC:" + detail.lac);
        }

        if (mSharedPreferences.getBoolean("BSIC", true)) {
            paramList.add("BSIC:" + detail.bsic);
        }
    }

    public interface OnDeleteListener {
        void hasDelete();
    }

    public boolean isPopWindowShow() {
        if (pop != null) {
            return pop.isShowing();
        }
        return false;
    }

}
