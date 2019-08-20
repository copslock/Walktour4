package com.walktour.gui.newmap2.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.walktour.Utils.DensityUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.ScreenUtils;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;
import com.walktour.gui.R;
import com.walktour.gui.newmap.NewMapFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 基站详情弹出框显示
 *
 * @author jianchao.wang
 */
@SuppressLint("InflateParams")
public class BaseStationDetailDialog extends Dialog {
    /**
     * 日志标识
     */
    private final static String TAG = "BaseStationDetailPopWindow";
    /**
     * 共享参数文件名
     */
    private final static String PREFERENCE_NAME = "com.walktour.gui.map";

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
     * 活动类
     */
    private Context mContext;
    /**
     * 删除按钮的监听
     */
    private OnCheckListener listener;
    private TextView mTvLatLng;
    private LinearLayout mContainer;
    private ScrollView mScrollView;
    private ImageButton mIbDelete;
    private CheckBox mCheckBox;

    public BaseStationDetailDialog(@NonNull Context context) {
        this(context, R.style.activity_dialog);
        mContext = context;
        init();
    }

    public BaseStationDetailDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        init();
    }

    private void init() {
        mSharedPreferences = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        view = LayoutInflater.from(mContext).inflate(R.layout.basedetail_activity, null);
        mTvLatLng = (TextView) this.view.findViewById(R.id.baselonlat);
        mContainer = (LinearLayout) this.view.findViewById(R.id.base);
        mScrollView = (ScrollView) this.view.findViewById(R.id.linearLayout1);
        mIbDelete = (ImageButton) view.findViewById(R.id.ib_delete_dialog);
        mCheckBox = (CheckBox) view.findViewById(R.id.cb_select);
        mIbDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        setContentView(view);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        lp.width = ScreenUtils.getScreenWidth(mContext) * 3 / 4;
        lp.height = DensityUtil.dip2px(mContext, 320); // 高度
        dialogWindow.setAttributes(lp);
    }

//    public void setStation(BaseStation baseStation) {
//        this.baseStation = baseStation;
//        fillBaseData();
//    }


    public void setOnCheckListener(OnCheckListener listener) {
        this.listener = listener;
    }


    /**
     * 2018/6/8 czc : 不需要每次都new一个对象，复用同一个,后面有时间建议改成listview
     *
     * @param station
     */
    public void setBaseStation(BaseStation station) {
        if (station == null) {
            return;
        }
        mScrollView.scrollTo(0, 0);
        mContainer.removeAllViews();
        this.baseStation = station;
        initCheckBox();
        fillBaseData();
        LogUtil.d("max", "baseStation:" + baseStation);
    }

    private void initCheckBox() {
        BaseStation selectBaseStation = NewMapFactory.getInstance().getSelectBaseStation();
        mCheckBox.setOnCheckedChangeListener(null);
        if (selectBaseStation != null) {
            mCheckBox.setChecked(baseStation.id == selectBaseStation.id);
        } else {
            mCheckBox.setChecked(false);
        }
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (listener != null) {
                    listener.onCheckStation(isChecked);
                }
                dismiss();
            }
        });
    }

    /**
     * 填充基站数据
     */
    @SuppressLint("ResourceAsColor")
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
                row = LayoutInflater.from(mContext).inflate(R.layout.basedetail_row, null);
                row.setBackgroundColor(Color.WHITE);
                LayoutParams layoutParams=  new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                TextView param1 = (TextView) row.findViewById(R.id.param1);
                param1.setText(paramList.get(j));
                TextView param2 = (TextView) row.findViewById(R.id.param2);
                if (j == 0) {//第一行
//                    row.setBackgroundColor(this.view.getContext().getResources().getColor(R.color.base_list_item_bg_select));
                    param1.setTextColor(mContext.getResources().getColor(R.color.black));
                    param2.setTextColor(mContext.getResources().getColor(R.color.black));
                    param1.getPaint().setFakeBoldText(true);//中文英文都可以粗体
                    param2.getPaint().setFakeBoldText(true);//中文英文都可以粗体
                }else {
                    param1.setTextColor(mContext.getResources().getColor(R.color.app_main_text_color));
                    param2.setTextColor(mContext.getResources().getColor(R.color.app_main_text_color));
                    param1.setTextSize(14);
                    param2.setTextSize(14);
                }
                if (j==paramList.size()-1){//最后一行
                    row.setPadding(0,0,0,DensityUtil.px2dip(mContext,200));
//                    layoutParams.setMargins(0,0,0,DensityUtil.px2dip(mContext,100));
                }
                if (j + 1 < paramList.size()) {
                    param2.setText(paramList.get(j + 1));
                } else {
                    param2.setVisibility(View.GONE);
                }
                mContainer.addView(row, layoutParams);
            }
        }
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
     * 获取NB-Iot基站参数
     *
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

    public interface OnCheckListener {
        void onCheckStation(boolean isCheck);
    }

}
