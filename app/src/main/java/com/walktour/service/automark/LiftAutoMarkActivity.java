
package com.walktour.service.automark;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.ImageUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhone;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.applet.ExplorerDirectory;
import com.walktour.gui.map.MapActivity;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.setting.SysMap;
import com.walktour.service.automark.constant.AutoMarkConstant;
import com.walktour.service.automark.constant.MarkScene;
import com.walktour.service.automark.glonavin.bean.GlonavinPoint;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

/**
 * @author jinfeng.xie
 * @version 1.0.0
 * @date on 2018/7/30
 * @describe ???????????????????????????????????????????????????
 */
public class LiftAutoMarkActivity extends Activity implements OnChartValueSelectedListener {
    private static final String TAG = "LiftAutoMarkActivity";
    private LineChart mChart;
    private ApplicationModel appModel;
    private TextView tvCurrentFloor;
    private TextView tvInitInfo;
    private float minY = 0f;//???????????????????????????????????????

    private HashMap<Integer, Float> floors;//???????????????
    private BasicDialog dialog;

    private final static String EXTRA_DIR = "dir";
    private final static String EXPORT_MAP_ACTION_DIR = "com.walktour.BaseMapActivity.exportMap";
    private ImageUtil.FileType picFileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lift_auto_mark);
        init();
        initChar();
        initReceiver();
    }
    private void initReceiver() {
        IntentFilter filter=new IntentFilter();
        filter.addAction(EXPORT_MAP_ACTION_DIR);
        filter.addAction( MapActivity.ACTION_MAP_COLOR_CHANGE);
        registerReceiver(mReceiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        unregisterReceiver(mReceiver);
    }

    private void init() {
        appModel = ApplicationModel.getInstance();
        tvCurrentFloor = (TextView) findViewById(R.id.tv_current_floor);
        tvInitInfo = (TextView) findViewById(R.id.tv_init_info);
        findViewById(R.id.btn_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LiftAutoMarkActivity.this, SysMap.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportMapImage();
            }
        });
        EventBus.getDefault().register(this);
    }




    private void initChar() {
        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        // ?????????????????????
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setAutoScaleMinMaxEnabled(false);

//        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
//        mv.setChartView(mChart); // For bounds control
//        mChart.setMarker(mv); // Set the marker to the chart


        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        mChart.getAxisRight().setEnabled(false);
        // ????????????????????????????????????????????????
        Legend l = mChart.getLegend();
        // modify the legend ...
        l.setForm(LegendForm.LINE);
        /**
         * ??????????????????
         */
        drawFloorLine();
        setData(MapFactory.getMapData().getGlonavinPointStack());
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param point
     */
    @SuppressLint("StringFormatInvalid")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void AddAutoMarkPoint(GlonavinPoint point) {
        if (appModel.isGlonavinTest()) {
            if (AutoMarkConstant.markScene != MarkScene.COMMON) {
                tvCurrentFloor.setVisibility(View.VISIBLE);
                tvCurrentFloor.setText(""+String.format(getString(R.string.glonavin_auto_mark_current_floor_show),point.getCurrentFloor()));
                if (!AutoMarkConstant.floors.containsKey(point.getCurrentFloor())) {
                    AutoMarkConstant.floors.put(point.getCurrentFloor(), point.getZ());
                }
                drawFloorLine();
                setData(MapFactory.getMapData().getGlonavinPointStack());
            }
        }
    }

    /**
     * ?????????????????????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateInfo(String msg) {
        if (appModel.isGlonavinTest()) {
            tvInitInfo.setText("" + msg);
        }
    }
    /**
     * ??????????????????
     */
    private void exportMapImage() {
        final String[] fileTypes = new String[]{"JPEG", "PNG"};
        new BasicDialog.Builder(LiftAutoMarkActivity.this.getParent()).setTitle(R.string.facebook_filetype)
                .setSingleChoiceItems(fileTypes, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                picFileType = ImageUtil.FileType.JPEG;
                                break;
                            case 1:
                                picFileType = ImageUtil.FileType.PNG;
                                break;
                        }
                        // ?????????????????????
                        new ExplorerDirectory(LiftAutoMarkActivity.this, fileTypes, EXPORT_MAP_ACTION_DIR, EXTRA_DIR).start();
                        dialog.dismiss();
                    }
                }).show();
    }
    /**
     * ????????????
     *
     * @param glonavinPoints
     */

    ArrayList<ArrayList<Entry>> datas = new ArrayList<>();//??????????????????
    boolean isXforward = true;//??????x???????????????
    ArrayList<Integer> colors = new ArrayList<>();//???????????????

    private void setData(Stack<GlonavinPoint> glonavinPoints) {
        if (glonavinPoints == null || glonavinPoints.size() == 0) {
            return;
        }
        datas.clear();
        colors.clear();
        isXforward = true;

        //????????????
        for (int i = 0; i < glonavinPoints.size(); i++) {
            GlonavinPoint point = glonavinPoints.get(i);//?????????

            if (point.getColor() == Color.GRAY) {
                continue;
            }

            float x = (float) Math.sqrt(Math.pow(point.getX(), 2) + Math.pow(point.getY(), 2));//X:????????????
            float y = point.getZ();//y????????????
            if (y < minY) {//???????????????
                minY = y;
            }
            ArrayList<Entry> lineDate;//??????????????????
            //???????????????????????????????????????????????????????????????x??????
            if (datas.size() == 0) {
                lineDate = new ArrayList<>();//??????
                lineDate.add(new Entry(x, y));
                colors.add(point.getColor());
                datas.add(lineDate);
            } else {
                /**
                 * 1?????????????????????????????????????????????????????????????????????
                 * 2.??????????????????2?????????
                 */
                lineDate = datas.get(datas.size() - 1);//??????????????????
                if (isXforward) {
                    if (x >= lineDate.get(lineDate.size() - 1).getX()) {//?????????????????????????????????????????????????????????
                        lineDate.add(new Entry(x, y));
                    } else if (x < lineDate.get(lineDate.size() - 1).getX()) {
                        Entry firstPoint = lineDate.get(lineDate.size() - 1);//?????????????????????
                        lineDate = new ArrayList<>();//??????
                        isXforward = false;//?????????
                        lineDate.add(0, firstPoint);
                        lineDate.add(0, new Entry(x, y));
                        colors.add(point.getColor());
                        datas.add(lineDate);
                    }
                } else {
                    if (x <= lineDate.get(0).getX()) {//?????????????????????????????????????????????????????????
                        lineDate.add(0, new Entry(x, y));
                    } else if (x > lineDate.get(0).getX()) {
                        Entry firstPoint = lineDate.get(0);//?????????????????????
                        lineDate = new ArrayList<>();//??????
                        isXforward = true;//???????????????
                        lineDate.add(firstPoint);
                        lineDate.add(new Entry(x, y));
                        colors.add(point.getColor());
                        datas.add(lineDate);
                    }
                }

            }

        }

        ArrayList<LineDataSet> sets = new ArrayList<>();

        LineDataSet set;
        LineData data = new LineData();//????????????setDate???
        for (int i = 0; i < datas.size(); i++) {
            set = new LineDataSet(datas.get(i), "");
            set.setDrawIcons(false);
            // ???????????????????????????-------
            set.enableDashedLine(10f, 5f, 0f);
            set.enableDashedHighlightLine(10f, 5f, 0f);
            set.setColor(Color.BLACK);
            if (colors.size() > i) { //????????????????????????
                set.setCircleColor(colors.get(i));
            } else {
                set.setCircleColor(Color.BLUE);
            }
            set.setLineWidth(1f);
            set.setCircleRadius(5f);
            set.setDrawCircleHole(false);
            set.setValueTextSize(0f);//???????????????????????????
            set.setDrawFilled(false);
            set.setFormLineWidth(1f);
            set.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set.setFormSize(15.f);
            data.addDataSet(set);
            sets.add(set);
        }
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);
        // set data
        mChart.setData(data);
        mChart.getAxisLeft().setAxisMinimum(minY);
        mChart.invalidate();
//        }
    }

    private void drawFloorLine() {
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
        floors = AutoMarkConstant.floors;
        if (floors == null || floors.size() == 0) {
            return;
        }
        //?????????????????????
        Iterator iterator = floors.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Integer floor = (Integer) entry.getKey();
            Float hegiht = (Float) entry.getValue();
            LimitLine ll = new LimitLine(hegiht, floor + getString(R.string.str_unit_floor));
            ll.setLineWidth(4f);
            ll.enableDashedLine(10f, 10f, 0f);
            ll.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll.setTextSize(10f);
            leftAxis.addLimitLine(ll);
        }
        mChart.getAxisRight().setEnabled(false);
        mChart.invalidate();
    }

    /**
     * ??????????????????
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MapActivity.ACTION_MAP_COLOR_CHANGE:
                    LogUtil.v("MapChange", "---Change---");
                    findViewById(R.id.threshold_view).invalidate();
                    break;
                case EXPORT_MAP_ACTION_DIR:
                    String path = intent.getExtras().getString(EXTRA_DIR);
                    saveViewToBMP(path);
                    break;
            }
        }
    };
    public void saveViewToBMP(final String path) {
        File snapDir = new File(path);
        if (!snapDir.isDirectory()) {
            snapDir.mkdirs();
        }
        MyPhone phone = new MyPhone(LiftAutoMarkActivity.this);
        phone.getScreen(LiftAutoMarkActivity.this, path,picFileType);
    }
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOWHIGH", "low: " + mChart.getLowestVisibleX() + ", high: " + mChart.getHighestVisibleX());
        Log.i("MIN MAX", "xmin: " + mChart.getXChartMin() + ", xmax: " + mChart.getXChartMax() + ", ymin: " + mChart.getYChartMin() + ", ymax: " + mChart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }
}
