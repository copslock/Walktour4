package com.walktour.framework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.ShowInfo;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.model.YwDataModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.http.PUT;

public class MLineChartView extends LineChart  {
    private static final String TAG = "MLineChartView";
    Context mContext;
    private ArrayList<Entry> values=new ArrayList<>();//所有的数据,非5G都加载这个数据
    private ArrayList<Entry> nrValues=new ArrayList<>();//nr所有的数据
    private ArrayList<Entry> lteValues=new ArrayList<>();//lte所有的数据
    float maxYValue = -1;//最大值
    float minYValue = 30;//最小值

    /**
     * 并发时根据name 获取值
     */
    private String name = "";
    private LineDataSet set1;
    private LineDataSet setLte;//Lte曲线
    private LineDataSet setNR;//NR曲线
    private Float[] points;
    private LimitLine maxLine;
    private LimitLine minLine;

    public MLineChartView(Context context) {
        super(context);
        this.mContext = context;
        initChar();
    }

    public MLineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initChar();
    }

    public MLineChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initChar();
    }

    private void initChar() {
        values = new ArrayList<Entry>();
        setDrawGridBackground(false);
        // no description text
        getDescription().setEnabled(false);
        setTouchEnabled(true);
        setDragEnabled(true);
        setScaleEnabled(true);
        setPinchZoom(true);


        XAxis xAxis = getXAxis();
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.enableGridDashedLine(10f, 10f, 0f);

        YAxis leftAxis = getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisLineColor(Color.WHITE);
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaximum(30);
        leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(true);

        getAxisRight().setEnabled(false);

        // get the legend (only possible after setting data)
        Legend l = getLegend();
        l.setForm(Legend.LegendForm.NONE);
        animateX(2500);
        initEmptyData();
    }

    void initEmptyData() {
        initLineDataSet();
        initLTeLineDataSet();
        initNRLineDataSet();
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets
        dataSets.add(setLte); // add the datasets
        dataSets.add(setNR); // add the datasets
        // create a data object with the datasets
        LineData data = new LineData(dataSets);
        // set data
        setData(data);
    }

    private void initLineDataSet() {
        // create a dataset and give it a type
        set1 = new LineDataSet(values, "");
        set1.setDrawIcons(false);
        // set the line to be drawn like this "- - - - - -"
        set1.enableDashedLine(10f, 0f, 0f);
        set1.enableDashedHighlightLine(10f, 0f, 0f);
        set1.setColor(Color.WHITE);
        set1.setCircleColor(Color.WHITE);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawValues(false);
        set1.setDrawFilled(false);
        set1.setFormLineWidth(1f);
        set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set1.setFormSize(15.f);
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.fade_white);
            set1.setFillDrawable(drawable);
        } else {
            set1.setFillColor(Color.BLACK);
        }
    }
    private void initLTeLineDataSet() {
        // create a dataset and give it a type
        setLte = new LineDataSet(values, "");
        setLte.setDrawIcons(false);
        // set the line to be drawn like this "- - - - - -"
        setLte.enableDashedLine(10f, 0f, 0f);
        setLte.enableDashedHighlightLine(10f, 0f, 0f);
        setLte.setColor(Color.parseColor("#CD32CD"));
        setLte.setCircleColor(Color.parseColor("#CD32CD"));
        setLte.setLineWidth(1f);
        setLte.setCircleRadius(3f);
        setLte.setDrawCircleHole(false);
        setLte.setValueTextSize(9f);
        setLte.setDrawValues(false);
        setLte.setDrawFilled(false);
        setLte.setFormLineWidth(1f);
        setLte.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        setLte.setFormSize(15.f);
        setLte.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.fade_white);
            setLte.setFillDrawable(drawable);
        } else {
            setLte.setFillColor(Color.BLACK);
        }
    }
    private void initNRLineDataSet() {
        // create a dataset and give it a type
        setNR = new LineDataSet(values, "");
        setNR.setDrawIcons(false);
        // set the line to be drawn like this "- - - - - -"
        setNR.enableDashedLine(10f, 0f, 0f);
        setNR.enableDashedHighlightLine(10f, 0f, 0f);
        setNR.setColor(Color.parseColor("#FFC993"));
        setNR.setCircleColor(Color.parseColor("#FFC993"));
        setNR.setLineWidth(1f);
        setNR.setCircleRadius(3f);
        setNR.setDrawCircleHole(false);
        setNR.setValueTextSize(9f);
        setNR.setDrawValues(false);
        setNR.setDrawFilled(false);
        setNR.setFormLineWidth(1f);
        setNR.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        setNR.setFormSize(15.f);
        setNR.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.fade_white);
            setNR.setFillDrawable(drawable);
        } else {
            setNR.setFillColor(Color.BLACK);
        }
    }

    void drawMaxLine() {
        getAxisLeft().removeLimitLine(maxLine); //先清除原来的线，后面再加上，防止add方法重复绘制
        maxLine = new LimitLine(maxYValue, mContext.getString(R.string.maxValue) + maxYValue);
        maxLine.setLineWidth(4f);
        maxLine.enableDashedLine(10f, 10f, 0f);
        maxLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        maxLine.setTextSize(10f);
        getAxisLeft().setDrawLimitLinesBehindData(true);  //这个很神奇开始看源码注释我有点懵逼，啥意思？看下文解释吧
        getAxisLeft().addLimitLine(maxLine);
    }


    void drawMinLine() {
        getAxisLeft().removeLimitLine(minLine);//先清除原来的线，后面再加上，防止add方法重复绘制
        minLine = new LimitLine(minYValue, mContext.getString(R.string.minValue) + minYValue);
        minLine.setLineWidth(4f);
        minLine.enableDashedLine(10f, 10f, 0f);
        minLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        minLine.setTextSize(10f);
        getAxisLeft().setDrawLimitLinesBehindData(true);  //这个很神奇开始看源码注释我有点懵逼，啥意思？看下文解释吧
        getAxisLeft().addLimitLine(minLine);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.destroyDrawingCache();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*TODO 获取数据并显示*/
        WalkStruct.CurrentNetState netType = TraceInfoInterface.currentNetType;
        boolean is5G = (netType == WalkStruct.CurrentNetState.ENDC);//是否5G网络
        if (is5G) {
            lteValues = ShowInfo.getInstance().getLtePoint();
            nrValues = ShowInfo.getInstance().getNrPoint();
            draw5GData();
        } else {
            YwDataModel ywDataModel = ShowInfo.getInstance().getYwDataModel(name);
            points = ywDataModel.getBordPoints();
            drawData();
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void drawData() {
        if (points == null) {//如果数据没有发生改变，则不需要重新绘制
            return;
        }
        values.clear();
        for (int i = 0; i < points.length; i++) {
            values.add(new Entry(i, points[i]));
            if (points[i] > maxYValue) {//当值超过最大值，绘制最大值
                maxYValue = points[i];
                if (maxYValue > 30) {
                    getAxisLeft().setAxisMaximum(maxYValue);
                }
                drawMaxLine();
            }
            if (points[i] <= minYValue) {
                minYValue = points[i];
                drawMinLine();
            }
        }
        if (getData() != null &&
                getData().getDataSetCount() > 0) {
            lteValues.clear();
            nrValues.clear();
            set1.setValues(values);
            setLte.setValues(lteValues);
            setNR.setValues(nrValues);
            getData().notifyDataChanged();
            notifyDataSetChanged();
        } else {
            initEmptyData();
        }
    }

    public void draw5GData() {

        if (lteValues.size() == 0 && nrValues.size() == 0) {//如果数据 ，则不需要重新绘制
            return;
        }
        maxYValue = ShowInfo.getInstance().getMaxNR()/1024/1024;
        if (maxYValue > 30) {
            getAxisLeft().setAxisMaximum(maxYValue);
        }
        drawMaxLine();

        if (getData() != null &&
                getData().getDataSetCount() > 0) {
            values.clear();
            set1.setValues(values);
            setLte.setValues(lteValues);
            setNR.setValues(nrValues);
            getData().notifyDataChanged();
            notifyDataSetChanged();
        } else {
            initEmptyData();
        }
    }
}
