package com.walktour.gui.newmap2.gis;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.walktour.Utils.DensityUtil;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.newmap2.ui.AutoScaleTextView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * gis 基站详情
 *
 * @author zhicheng.chen
 * @date 2018/11/20
 */
public class GisMapDetailActivity extends BasicActivity {

    public static final String EXTRA_STATION = "EXTRA_STATION";
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.ll_work_detail)
    LinearLayout mLlWorkDetail;
    @BindView(R.id.ll_performance_detail)
    LinearLayout mLlPerformanceDetail;
    @BindView(R.id.ll_neighbour_detail)
    LinearLayout mLlNeighbourDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gis_map_detail);
        ButterKnife.bind(this);
        getIntentExtra();
    }

    private void getIntentExtra() {
        BaseStation baseStation = (BaseStation) getIntent().getSerializableExtra(EXTRA_STATION);
        mTvTitle.setText(baseStation.name);
        Map<String, String> workMap = new HashMap<>();
        workMap.put("小区名称：", baseStation.name);
        workMap.put("设备厂家：", baseStation.name);
        workMap.put("经度：", baseStation.longitude + "");
        workMap.put("纬度：", baseStation.latitude + "");
        workMap.put("下倾角：", baseStation.latitude + "");
        workMap.put("方位角：", baseStation.latitude + "");
        workMap.put("天线挂高：", baseStation.latitude + "");
        generateLayout(mLlWorkDetail, workMap);

        Map<String, String> performanceMap = new HashMap<>();
        performanceMap.put("性能空口上下行流量：", baseStation.name);
        performanceMap.put("无限接通率：", baseStation.name);
        performanceMap.put("掉线率：", baseStation.longitude + "");
        generateLayout(mLlPerformanceDetail, performanceMap);

        Map<String, String> neighbourMap = new HashMap<>();
        neighbourMap.put("小区邻区详情：", baseStation.name);
        neighbourMap.put("小区列表：", baseStation.name);
        generateLayout(mLlNeighbourDetail, neighbourMap);
    }

    @OnClick(R.id.ib_back)
    void back() {
        finish();
    }

    private void generateLayout(ViewGroup root, Map<String, String> map) {

        int padding = DensityUtil.dip2px(this, 5);

        Set<String> set = map.keySet();
        Iterator<String> it = set.iterator();

        LinearLayout.LayoutParams lpItem = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(this, 42));
        LinearLayout.LayoutParams lpKey = new LinearLayout.LayoutParams(DensityUtil.dip2px(this, 120), LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams lpCd = new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams lpValue = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams lpDd = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);

        while (it.hasNext()) {
            String key = it.next();
            String value = map.get(key);

            LinearLayout llItem = new LinearLayout(this);
            llItem.setLayoutParams(lpItem);
            llItem.setOrientation(LinearLayout.HORIZONTAL);

            AutoScaleTextView tvKey = new AutoScaleTextView(this);
            tvKey.setLayoutParams(lpKey);
            tvKey.setPadding(padding, padding, padding, padding);
            tvKey.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            tvKey.setText(key);

            View centerDivider = new View(this);
            centerDivider.setLayoutParams(lpCd);
            centerDivider.setBackgroundColor(getResources().getColor(R.color.app_divier_line_color));

            AutoScaleTextView tvValue = new AutoScaleTextView(this);
            tvValue.setLayoutParams(lpValue);
            tvValue.setPadding(padding, padding, padding, padding);
            tvValue.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            tvValue.setText(value);

            llItem.addView(tvKey);
            llItem.addView(centerDivider);
            llItem.addView(tvValue);

            View divider = new View(this);
            divider.setLayoutParams(lpDd);
            divider.setBackgroundColor(getResources().getColor(R.color.app_divier_line_color));
            root.addView(llItem);
            root.addView(divider);
        }
    }
}
