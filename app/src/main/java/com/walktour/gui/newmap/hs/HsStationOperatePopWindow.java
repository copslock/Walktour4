package com.walktour.gui.newmap.hs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.WalktourConst;
import com.walktour.gui.R;
import com.walktour.gui.highspeedrail.model.HighSpeedNoModel;
import com.walktour.gui.map.MapFactory;
import com.walktour.service.metro.HsFactory;
import com.walktour.service.metro.MetroFactory;
import com.walktour.service.metro.model.MetroStation;

import java.util.Locale;

/**
 * 地铁站点设置状态弹出框显示
 *
 * @author jinfeng.xie
 */
@SuppressLint("InflateParams")
public class HsStationOperatePopWindow implements OnCheckedChangeListener {
    /**
     * 日志标识
     */
    private final static String TAG = "MetroStationOperatePopWindow";
    /**
     * 弹出窗
     */
    private PopupWindow mPop;
    /**
     * 视图对象
     */
    private View mView;
    /**
     * 父视图
     */
    private View mParent;
    /**
     * 活动类
     */
    private Context mContext;
    /**
     * 地铁工厂类
     */
    private HsFactory mFactory;
    /**
     * 地铁站点
     */
    private MetroStation mStation;
//    /**
//     * 当前窗口是否显示
//     */
//    private boolean isShow = false;
    /**
     * 当前是否最后一个站点
     */
    private boolean isLastStation = false;
    /**
     * 是否自动测试
     */
    private boolean isAutoMark;
    private final TextView mStationDesc;
    private CheckBox mStartCheck;
    private CheckBox mEndCheck;

    public HsStationOperatePopWindow(View parent, Context context, MetroStation station) {
        this.mParent = parent;
        this.mContext = context;
        this.mFactory = HsFactory.getInstance(context);
//        this.mStation = station;
//        this.isLastStation = this.mFactory.isLastStation(station);
        this.isAutoMark = MapFactory.getMapData().isAutoMark();
//        LayoutInflater layoutInflater = LayoutInflater.from(context);
        this.mView = LayoutInflater.from(context).inflate(R.layout.window_metro_station_operate, null);
        mStationDesc = (TextView) this.mView.findViewById(R.id.station_desc);
        mStartCheck = (CheckBox) this.mView.findViewById(R.id.check_station_start);
        mEndCheck = (CheckBox) this.mView.findViewById(R.id.check_station_reach);

//        String desc = context.getResources().getString(R.string.metro_station_operate_desc);
//        desc = String.format(Locale.getDefault(), desc, station.getName());
//        mStationDesc.setText(desc);
//        this.setCheckState(this.mStation.isReach());

        float density = this.mContext.getResources().getDisplayMetrics().density;
        int width = this.mParent.getMeasuredWidth();
        int height = (int) (80 * density);
        this.mPop = new PopupWindow(this.mView, width, height);

        HighSpeedNoModel train = SharePreferencesUtil.getInstance(mContext).getObjectFromShare(WalktourConst.CURRENT_HS_NO, HighSpeedNoModel.class);
        setStation(station);
    }

    public void setStation(MetroStation station) {
        this.mStation = station;
        this.isLastStation = this.mFactory.isLastStation(station);
        String desc = mContext.getResources().getString(R.string.metro_station_operate_desc);
        desc = String.format(Locale.getDefault(), desc, station.getName());
        mStationDesc.setText(desc);
        mStartCheck.setOnCheckedChangeListener(null);
        mEndCheck.setOnCheckedChangeListener(null);
        mStartCheck.setChecked(false);
        mEndCheck.setChecked(false);
        this.setCheckState(this.mStation.isReach());
    }


    /**
     * 设置勾选框的状态
     *
     * @param isReach 当前站点是否已经到站
     */
    private void setCheckState(boolean isReach) {
        if (!this.isAutoMark) {
            mStartCheck.setOnCheckedChangeListener(this);
        } else {
            mStartCheck.setVisibility(View.GONE);
        }
        mEndCheck.setOnCheckedChangeListener(this);
        if (isReach) {
            mStartCheck.setEnabled(true);
            mStartCheck.setTextColor(this.mContext.getResources().getColor(R.color.light_blue));
            mEndCheck.setChecked(true);
            mEndCheck.setEnabled(false);
            mEndCheck.setTextColor(this.mContext.getResources().getColor(R.color.app_grey_color));
        } else {
            mStartCheck.setEnabled(false);
            mStartCheck.setTextColor(this.mContext.getResources().getColor(R.color.app_grey_color));
            mEndCheck.setEnabled(true);
            mEndCheck.setTextColor(this.mContext.getResources().getColor(R.color.light_blue));
        }
    }

    /**
     * 显示弹出框
     */
    @SuppressLint("NewApi")
    public void showPopWindow() {
        float density = this.mContext.getResources().getDisplayMetrics().density;
        int height = (int) (80 * density);
        int[] location = new int[2];
        this.mParent.getLocationOnScreen(location);
        int x = 0;
        int y = location[1] + this.mParent.getMeasuredHeight() - height;
        this.mPop.showAtLocation(this.mParent, Gravity.TOP | Gravity.LEFT, x, y);
    }

    /**
     * 关闭弹出框
     */
    public void closePopWindow() {
//        if (!this.isShow)
//            return;
        if (this.mPop != null) {
            this.mPop.dismiss();
        }
//        this.isShow = false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.check_station_start:
                this.mFactory.startStation(mStartCheck.isChecked());
                this.closePopWindow();
                break;
            case R.id.check_station_reach:
                this.mFactory.reachStation(mEndCheck.isChecked());
                if (this.isAutoMark || this.isLastStation) {
                    this.closePopWindow();
                }else
                    this.setCheckState(true);
                break;
        }

    }

    public boolean isShow() {
//        return isShow;
        if (mPop == null) {
            return false;
        }
        return mPop.isShowing();
    }


    public MetroStation getStation() {
        return mStation;
    }
}
