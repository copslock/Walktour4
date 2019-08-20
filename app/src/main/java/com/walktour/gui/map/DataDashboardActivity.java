package com.walktour.gui.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.ShowInfo;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.CustomDLULView;
import com.walktour.framework.view.CustomDashboardDataView;
import com.walktour.framework.view.Dashboardfor5GView;
import com.walktour.framework.view.MLineChartView;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.gui.R;
import com.walktour.model.YwDataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 数据仪表盘<BR>
 * [功能详细描述]
 *
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-6-28]
 */
public class DataDashboardActivity extends BasicActivity implements RefreshEventManager.RefreshEventListener {
    private static final String TAG = "DataDashboardActivity";
    @BindView(R.id.item_5g_dashboardview)
    Dashboardfor5GView item5gDashboardview;
    @BindView(R.id.dlul_view)
    CustomDLULView dlulView;
    @BindView(R.id.item_Data)
    CustomDashboardDataView itemData;
    @BindView(R.id.line_chart)
    MLineChartView lineChart;
    private WalkStruct.CurrentNetState netType;//当前网络类型
    private boolean isDrawFinish = true; //是否更新完成
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_board_view_item);
        ButterKnife.bind(this);
        IntentFilter filter = new IntentFilter(); // 注册一个消息过滤器
        filter.addAction(WalkMessage.testDataUpdate);
        registerReceiver(mIntentReceiver, filter, null, null);

    }


    /**
     * 消息处理
     */
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(WalkMessage.testDataUpdate) && !ApplicationModel.getInstance().isFreezeScreen()) {// TraceInfoInterface.currentShowTab.equals(WalkStruct.ShowInfoType.Data)
                HashMap<String, YwDataModel> ywDataList = ShowInfo.getInstance().getYwDataModelList();
                LogUtil.d(TAG, "更新广播:currentSize" + "-----------数据大小:" + ywDataList.size());
                /*更新广播:currentSize0-----------数据大小:1    测试，httpPage的时候，数量在1和0之间变化  :netTypeLTE*/
                if (ywDataList.size() > 0) {
                    Iterator iter = ywDataList.keySet().iterator();
//                    while (iter.hasnext()) {
                    String key = iter.next().toString();
                    item5gDashboardview.setName(key);
                    lineChart.setName(key);
//                    }

                }
            }
        }
    };

    public void updateView() {
        if (!isDrawFinish) {/*如果还没更完，就不在重绘，防止ConcurrentModificationException*/
            return;
        }
        isDrawFinish = false;
        /*判断是否为5G*/
        netType = TraceInfoInterface.currentNetType;
        boolean is5G = (netType == WalkStruct.CurrentNetState.ENDC);
        dlulView.setVisibility(is5G ? View.VISIBLE : View.GONE);
        /*非测试状态下，不刷新chartview，dlulview*/
        if(ApplicationModel.getInstance().isTesting()||ApplicationModel.getInstance().isTestJobIsRun()){
            lineChart.invalidate();
            if(is5G) {
                dlulView.refreshView();
            }
        }
        item5gDashboardview.refreshView();
        isDrawFinish = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        RefreshEventManager.addRefreshListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mIntentReceiver); // 反注册消息过滤器
        RefreshEventManager.removeRefreshListener(this);
        super.onDestroy();
    }

    @Override
    public void onRefreshed(RefreshEventManager.RefreshType refreshType, Object object) {
        switch (refreshType) {
            case ACTION_WALKTOUR_TIMER_CHANGED:
                updateView();
                break;
            default:
                break;
        }
    }

}
