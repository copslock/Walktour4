package com.walktour.gui.highspeedrail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.WalktourConst;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.highspeedrail.model.HighSpeedNoModel;
import com.walktour.service.metro.model.MetroRoute;
import com.walktour.service.metro.model.MetroStation;

import java.util.ArrayList;
import java.util.List;
/**
 * @date on 2018/8/30
 * @describe 高铁选择站点
 * @author jinfeng.xie
 * @version 1.0.0
 *
 */
public class HsSelectStationActivity extends BasicActivity implements View.OnClickListener,AdapterView.OnItemClickListener {


    /** 当前选择的路线 */
    private MetroRoute mRoute;
    /** 路线描述 */
    private TextView mRouteDesc;
    /** 站点列表 */
    private List<MetroStation> mStations = new ArrayList<MetroStation>();
    /** 起始站点 */
    private MetroStation mStartStation = null;
    /** 列表适配器 */
    private MetroStationAdapter mAdapter;
    /** 到达站点 */
    private MetroStation mEndStation = null;
    /** 确定按钮 */
    private Button mOkBtn;
    /*** 当前班次*/
    private HighSpeedNoModel mCurrentNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hs_select_route);
        mCurrentNo= SharePreferencesUtil.getInstance(this).getObjectFromShare(WalktourConst.CURRENT_HS_NO, HighSpeedNoModel.class);
        this.mRoute = mCurrentNo.routes;
        this.mRoute.initStationState();
        this.mStartStation = this.mRoute.getStartStation();
        this.mEndStation = this.mRoute.getEndStation();
        this.findView();
        this.setOkButtonState();
    }

    /**
     * 视图设置
     */
    private void findView() {
        TextView title = (TextView) findViewById(R.id.title_txt);
        title.setText(R.string.metro_select_station);
        TextView routeName = (TextView) findViewById(R.id.route_name);
        routeName.setText(this.mRoute.getName());
        mRouteDesc = (TextView) findViewById(R.id.route_desc);
        ImageButton pointer = (ImageButton) findViewById(R.id.pointer);
        pointer.setOnClickListener(this);
        Button clearBtn = (Button) findViewById(R.id.btn_clear);
        clearBtn.setOnClickListener(this);
        mOkBtn = (Button) findViewById(R.id.btn_ok);
        mOkBtn.setOnClickListener(this);
        TextView orderBtn = (TextView) findViewById(R.id.order_btn);
        orderBtn.setOnClickListener(this);
        ListView list = (ListView) findViewById(R.id.metro_station_list);
        this.setStations();
        this.mAdapter = new MetroStationAdapter(this, R.layout.activity_metro_select_station_row,
                this.mStations);
        list.setAdapter(this.mAdapter);
        list.setOnItemClickListener(this);
    }

    /**
     * 根据选择的方向显示当前线路的站点
     */
    private void setStations() {
        this.mStations.clear();
        this.mStations.addAll(this.mRoute.getStations());
        mRouteDesc.setText(this.mRoute.getRouteDesc());
    }

    /**
     * 路线列表适配器类
     *
     * @author jianchao.wang
     *
     */
    private class MetroStationAdapter extends ArrayAdapter<MetroStation> {
        private int mResourceId;

        public MetroStationAdapter(Context context, int textViewResourceId, List<MetroStation> objects) {
            super(context, textViewResourceId, objects);
            this.mResourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(this.getContext()).inflate(this.mResourceId, null);
            }
            TextView stationName = (TextView) view.findViewById(R.id.station_name);
            TextView stationState = (TextView) view.findViewById(R.id.station_state);
            MetroStation station = this.getItem(position);
            stationName.setText(station.getName());
            stationName.setTextColor(this.getContext().getResources().getColor(R.color.app_main_text_color));
            switch (station.getState()) {
                case MetroStation.STATE_CAN_SELECT:
                    stationState.setText("");
                    break;
                case MetroStation.STATE_START:
                    stationState.setText(R.string.metro_station_start);
                    stationState.setTextColor(this.getContext().getResources().getColor(R.color.light_blue));
                    stationName.setTextColor(this.getContext().getResources().getColor(R.color.light_blue));
                    break;
                case MetroStation.STATE_END:
                    stationState.setText(R.string.metro_station_end);
                    stationState.setTextColor(this.getContext().getResources().getColor(R.color.light_blue));
                    stationName.setTextColor(this.getContext().getResources().getColor(R.color.light_blue));
                    break;
                case MetroStation.STATE_CANT_SELECT:
                    stationName.setTextColor(this.getContext().getResources().getColor(R.color.app_grey_color));
                    stationState.setText("");
                    break;
            }
            return view;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.metro_station_list) {
            this.setStationState(position);
        }
    }

    /**
     * 设置站点状态
     *
     * @param position
     *          站点序号
     */
    private void setStationState(int position) {
        MetroStation station = this.mStations.get(position);
        if (station.getState() == MetroStation.STATE_CANT_SELECT)
            return;
        if (station.getState() == MetroStation.STATE_START) {
            for (int i = 0; i < this.mStations.size(); i++) {
                if (i <= position)
                    this.mStations.get(i).setState(MetroStation.STATE_CAN_SELECT);
            }
            this.mStartStation = null;
        } else if (station.getState() == MetroStation.STATE_END) {
            for (int i = 0; i < this.mStations.size(); i++) {
                if (i >= position)
                    this.mStations.get(i).setState(MetroStation.STATE_CAN_SELECT);
            }
            this.mEndStation = null;
        } else {
            int startIndex = -1;
            int endIndex = -1;
            for (int i = 0; i < this.mStations.size(); i++) {
                switch (this.mStations.get(i).getState()) {
                    case MetroStation.STATE_START:
                        startIndex = i;
                        break;
                    case MetroStation.STATE_END:
                        endIndex = i;
                        break;
                }
            }
            // 如果已选择了起始站点和结束站点，则不操作直接返回
            if (startIndex >= 0 && endIndex >= 0)
                return;
                // 如果当前线路未设置起始站点和结束站点，则把当前站点设置成起始站点，然后把之前的设置成不可点击，把之后的设置成可点击
            else if (startIndex < 0 && endIndex < 0) {
                this.mStartStation = station;
                station.setState(MetroStation.STATE_START);
                for (int i = 0; i < this.mStations.size(); i++) {
                    if (i < position)
                        this.mStations.get(i).setState(MetroStation.STATE_CANT_SELECT);
                    else if (i > position)
                        this.mStations.get(i).setState(MetroStation.STATE_CAN_SELECT);
                }
                // 如果当前线路已设置起始站点，则把当前站点设置成结束站点，然后把之后的设置成不可点击
            } else if (startIndex >= 0) {
                this.mEndStation = station;
                station.setState(MetroStation.STATE_END);
                for (int i = 0; i < this.mStations.size(); i++) {
                    if (i > position)
                        this.mStations.get(i).setState(MetroStation.STATE_CANT_SELECT);
                }
                // 如果当前线路已设置结束站点，则把当前站点设置成起始站点，然后把之前的设置成不可点击
            } else if (endIndex >= 0) {
                this.mStartStation = station;
                station.setState(MetroStation.STATE_START);
                for (int i = 0; i < this.mStations.size(); i++) {
                    if (i < position)
                        this.mStations.get(i).setState(MetroStation.STATE_CANT_SELECT);
                }
            }
        }
        this.mAdapter.notifyDataSetChanged();
        this.setOkButtonState();
    }

    /**
     * 设置确定按钮的状态
     */
    private void setOkButtonState() {
        if (this.mStartStation == null || this.mEndStation == null) {
            this.mOkBtn.setEnabled(false);
            this.mOkBtn.setTextColor(this.getResources().getColor(R.color.app_grey_color));
        } else {
            this.mOkBtn.setEnabled(true);
            this.mOkBtn.setTextColor(this.getResources().getColor(R.color.app_main_text_color));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pointer:
//                startActivity(new Intent(this,HsSelectNoActivity.class));
//                overridePendingTransition(R.anim.open_next, R.anim.close_main);
                finish();
                break;
            case R.id.btn_clear:
                this.clear();
                break;
            case R.id.btn_ok:
                if (this.mStartStation != null && this.mEndStation != null) {
                    this.saveStationSet();
                }
                break;
            case R.id.order_btn:
                this.setDirect();
                break;
        }
    }

    /**
     * 清除当前的站点选择
     */
    private void clear() {
        for (int i = 0; i < this.mStations.size(); i++) {
            this.mStations.get(i).setState(MetroStation.STATE_CAN_SELECT);
        }
        this.mAdapter.notifyDataSetChanged();
    }

    /**
     * 保存站点设置
     */
    private void saveStationSet() {
        this.mRoute.setStartStation(this.mStartStation);
        this.mRoute.setEndStation(this.mEndStation);
        this.mCurrentNo.setRoutes(mRoute);
        String json = new Gson().toJson(mCurrentNo);
        mCurrentNo = new Gson().fromJson(json, HighSpeedNoModel.class);
        SharePreferencesUtil.getInstance(this).saveObjectToShare(WalktourConst.CURRENT_HS_NO,mCurrentNo,HighSpeedNoModel.class);
        Intent data = new Intent();
        data.putExtra("result", this.mRoute.getName() + "," + this.mRoute.getRouteSelectDesc());
        this.setResult(RESULT_OK, data);
        this.finish();
    }

    /**
     * 设置路线方向
     */
    private void setDirect() {
        this.mRoute.setForward(!this.mRoute.isForward());
        this.setStations();
        for (MetroStation station : this.mStations) {
            station.setState(MetroStation.STATE_CAN_SELECT);
        }
        this.mAdapter.notifyDataSetChanged();
    }
}
