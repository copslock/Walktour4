package com.walktour.gui.newmap2.overlay.gaode;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap2.overlay.BaseOverlayManager;
import com.walktour.gui.newmap2.overlay.OverlayType;
import com.walktour.gui.newmap2.util.GaodeMapUtil;
import com.walktour.mapextention.mif.GraphicObj;
import com.walktour.mapextention.mif.MifCenter;
import com.walktour.mapextention.mif.MifConfig;
import com.walktour.mapextention.mif.MifParser;
import com.walktour.mapextention.mif.MifPoints;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jinfeng.xie
 * @version 1.0.0
 * @date on 2018/7/18
 * @describe mif图层
 */
public class GdMIFMapOverlayManager extends BaseOverlayManager {

    private static final String TAG = GdMIFMapOverlayManager.class.getSimpleName();
    private ExecutorService mifThreadPool = Executors.newFixedThreadPool(3);
    private boolean mIsDetroy;
    private boolean mIsRunnableExcete;//是否runnable执行标识
    private Object mLock = new Object();
    /**
     * MIF地图路径
     */
    private String mFilePath = "";
    /**
     * MIF文件解析器
     */
    MifParser mifParser;
    /**
     * 基站详情弹出框
     */
    private List<Polyline> mMifOverlay = new ArrayList<>();
    private List<Text> mTextOverlay = new ArrayList<>();

    private static Vector<GraphicObj> Graphic_Stack;
    private static List<String> roadnames;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (!mIsRunnableExcete) {
                    mifThreadPool.execute(mStationOverlayUpdateRun);
                }
            }
        }
    };
    private Runnable mStationOverlayUpdateRun = new Runnable() {
        @Override
        public void run() {
            synchronized (mLock) {
                if (!mIsDetroy) {
                    mIsRunnableExcete = true;
                    //如果跟filePath不相同，则加载MIF，否则不加载相同的MIF地图
                    if (!mFilePath.equals(SharePreferencesUtil.getInstance(mContext).getString(WalktourConst.MIF_MAP_DIR))) {
                        mFilePath = SharePreferencesUtil.getInstance(mContext).getString(WalktourConst.MIF_MAP_DIR);
                        clearOverlay();
                        loadMifMap(mContext);
                        draw();
                        switchMapByType(NewMapFactory.MAP_TYPE_NONE);
                    }
                    mIsRunnableExcete = false;
                }
            }
        }
    };
    public void switchMapByType(int type) {
        switch (type) {
            case NewMapFactory.MAP_TYPE_NORMAL_2D:
                getMapControllor().setMapType(AMap.MAP_TYPE_NORMAL);//矢量地图模式
                break;
            case NewMapFactory.MAP_TYPE_NORMAL_3D:
                getMapControllor().setMapType(AMap.MAP_TYPE_NORMAL);//矢量地图模式
                break;
            case NewMapFactory.MAP_TYPE_SATELLITE:
                getMapControllor().setMapType(AMap.MAP_TYPE_SATELLITE);//卫星地图模式
                break;
            case NewMapFactory.MAP_TYPE_NONE:
                getMapControllor().setMapType(AMap.MAP_TYPE_NAVI);//导航地图模式
                break;
        }


    }
    private AMap getMapControllor() {
        return (AMap) mMapSdk.getMapControllor();
    }

    public GdMIFMapOverlayManager(Context context) {
        super(context);
        mifParser = new MifParser();
    }


    @Override
    public boolean onMarkerClick(Object... obj) {
        return true;
    }

    @Override
    public boolean onMapClick(Object... obj) {
        return super.onMapClick(obj);
    }

    @Override
    public void onMapLoaded() {
        mHandler.sendEmptyMessage(1);
    }

    @Override
    public void onMapStatusChangeStart(Object... obj) {
    }

    @Override
    public void onMapStatusChange(Object... obj) {
    }

    @Override
    public void onMapStatusChangeFinish(Object... obj) {

    }

    @Override
    public OverlayType getOverlayType() {
        return OverlayType.MifMap;
    }

    @Override
    public boolean addOverlay(Object... obj) {
        mHandler.sendEmptyMessage(1);
        return false;
    }

    @Override
    public boolean clearOverlay() {
        for (Polyline overlay : mMifOverlay) {
            overlay.remove();
        }
        for (Text text : mTextOverlay) {
            text.remove();
        }
        mMifOverlay.clear();
        mTextOverlay.clear();
        SharePreferencesUtil.getInstance(mContext).saveString(WalktourConst.MIF_MAP_DIR,"");
        return false;
    }


    @Override
    public void onDestory() {
        super.onDestory();
        mIsDetroy = true;
        mHandler.removeMessages(1);
        if (!mifThreadPool.isShutdown()) {
            mifThreadPool.shutdown();
        }
    }


    /**
     * MIF文件的最大限制
     */
    private static final int MIF_MAX_SIZE = 4 * 1000 * 1000;

    /**
     * 加载MIF地图
     *
     * @param context 上下文
     */
    private void loadMifMap(Context context) {
        TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.AMap;
        com.walktour.base.util.LogUtil.w(TAG, "----------showmif--");
        if (mFilePath.equals("")){
            return;
        }
        File f = new File(mFilePath);
        // 如果导入的地图大于3.5M
        if (f.length() > MIF_MAX_SIZE) {
            Toast.makeText(context, R.string.map_toobig, Toast.LENGTH_SHORT).show();
            return;
        }
        initMifParser();
    }

    private void initMifParser() {
        mifParser.Parse(mFilePath);
        LogUtil.w(TAG, "---path:" + mFilePath);
        MapFactory.setLoadMIF(true);
    }

    private void draw() {
        Graphic_Stack = mifParser.getGraphic_Stack();
        roadnames = mifParser.getRoadnames();
        GraphicObj go = null;
        MifPoints points = null;
        MifCenter curpoint = null;
        Paint painttext = new Paint();
        painttext.setTextSize(18);
        // painttext.setStrokeWidth(1);
        painttext.setColor(Color.RED);
        painttext.setStyle(Paint.Style.FILL);
        // 第一次取得复杂区域或者复杂线条数的标志，如果是复杂区域或者线条，那么直到把这个复杂区域或者线条绘制完毕，然后才再重新取
        boolean isFirstGetRegion = true;
        // LogUtil.w(tag, "---Graphic_Stack.size="+Graphic_Stack.size());
        int count = 0, regionNum = 1, regionNp = 1;

        for (int i = 0; i < Graphic_Stack.size(); i++) {
            go = Graphic_Stack.elementAt(i);
            if ((go.type == MifConfig.region || go.type == MifConfig.multiplePline) && isFirstGetRegion) {
                regionNp = regionNum = go.regionNumb;
                isFirstGetRegion = false;
            }
            switch (go.type) {
                case MifConfig.multiplePline:
                    i++;
                    points = (MifPoints) Graphic_Stack.elementAt(i);
                    i++;
                    List<MyLatLng> myLatLngs = new ArrayList<>();
                    for (int j = 0; j < points.points.size(); j++) {
                        curpoint = points.points.elementAt(j);
                        myLatLngs.add(new MyLatLng(curpoint.y, curpoint.x));
                    }
                    drawLine(myLatLngs);
                    if (count < roadnames.size()) {
                        String s = roadnames.get(count);
                        if (myLatLngs.size() > 0) {
                            drawText(myLatLngs.get(points.points.size() / 2), s, (int) painttext.getTextSize(), painttext.getColor());
                        }
                    }
                    count++;
                    if (regionNp >= 1) {
                        regionNp--;
                        // LogUtil.w(tag, "---regionNp 222="+regionNp);
                    }
                    if (regionNp == 0) {
                        count = count - (regionNum - 1);
                        isFirstGetRegion = true;
                        // LogUtil.w(tag, "---isFirstGetRegion change");
                    }
                    break;
                case MifConfig.pline:
                    i++;
                    points = (MifPoints) Graphic_Stack.elementAt(i);
                    i++;
                    List<MyLatLng> plineLatLngs = new ArrayList<>();
                    if (curpoint == null) {
                        curpoint = points.points.elementAt(0);
                        plineLatLngs.add(new MyLatLng(curpoint.y, curpoint.x));
                    } else {
                        curpoint = points.points.elementAt(0);
                        plineLatLngs.add(new MyLatLng(curpoint.y, curpoint.x));
                    }
                    for (int j = 1; j < points.points.size(); j++) {
                        curpoint = points.points.elementAt(j);
                        plineLatLngs.add(new MyLatLng(curpoint.y, curpoint.x));
                    }
                    drawLine(plineLatLngs);
                    if (count < roadnames.size()) {
                        String s = roadnames.get(count);
                        if (plineLatLngs.size() > 0) {
                            drawText(plineLatLngs.get(points.points.size() / 2), s, (int) painttext.getTextSize(), painttext.getColor());
                        }
                    }
                    count++;
                    break;
                case MifConfig.region:
                    i++;
                    points = (MifPoints) Graphic_Stack.elementAt(i);
                    i++;
                    List<MyLatLng> regionLatLngs = new ArrayList<>();
                    for (int j = 0; j < points.points.size(); j++) {
                        curpoint = points.points.elementAt(j);
                        regionLatLngs.add(new MyLatLng(curpoint.y, curpoint.x));
                    }
                    drawLine(regionLatLngs);
                    if (regionNp >= 1) {
                        regionNp--;
                    }
                    count++;
                    if (regionNp == 0) {
                        count = count - (regionNum - 1);
                        isFirstGetRegion = true;
                    }
                    break;
                case MifConfig.line:
                    i++;
                    points = (MifPoints) Graphic_Stack.elementAt(i);
                    i++;
                    List<MyLatLng> lineLatlngs = new ArrayList<>();
                    for (int j = 0; j < points.points.size(); j++) {
                        curpoint = points.points.elementAt(j);
                        lineLatlngs.add(new MyLatLng(curpoint.y, curpoint.x));
                    }
                    drawLine(lineLatlngs);
                    if (count < roadnames.size()) {
                        String s = roadnames.get(count);
                        drawText(lineLatlngs.get(points.points.size() / 2), s, (int) painttext.getTextSize(), painttext.getColor());
                    }
                    count++;
                    break;
                case MifConfig.point:
                    count++;
                    break;
                case MifConfig.rect:
                    i++;
                    points = (MifPoints) Graphic_Stack.elementAt(i);
                    i++;
                    List<MyLatLng> rectLatlngs = new ArrayList<>();
                    for (int j = 0; j < points.points.size(); j++) {
                        curpoint = points.points.elementAt(j);
                        rectLatlngs.add(new MyLatLng(curpoint.y, curpoint.x));
                    }
                    count++;
                    break;
                case MifConfig.roundrect:
                    i++;
                    points = (MifPoints) Graphic_Stack.elementAt(i);
                    i++;
                    List<MyLatLng> roundrectLatlngs = new ArrayList<>();
                    for (int j = 0; j < points.points.size(); j++) {
                        curpoint = points.points.elementAt(j);
                        roundrectLatlngs.add(new MyLatLng(curpoint.y, curpoint.x));
                    }
                    count++;
                    break;
                case MifConfig.arc:
                    i++;
                    points = (MifPoints) Graphic_Stack.elementAt(i);
                    i++;
                    List<MyLatLng> arcLatlngs = new ArrayList<>();
                    //// LogUtil.w(tag, "----xvalue="+xvalue+" yvalue="+yvalue);
                    for (int j = 0; j < points.points.size(); j++) {
                        curpoint = points.points.elementAt(j);
                        arcLatlngs.add(new MyLatLng(curpoint.y, curpoint.x));
                    }
                    count++;
                    break;
                case MifConfig.ellipse:
                    i++;
                    points = (MifPoints) Graphic_Stack.elementAt(i);
                    i++;
                    List<MyLatLng> ellipseLatlngs = new ArrayList<>();
                    for (int j = 0; j < points.points.size(); j++) {
                        curpoint = points.points.elementAt(j);
                        ellipseLatlngs.add(new MyLatLng(curpoint.y, curpoint.x));
                    }
                    count++;
                    break;

            }
        }
        // LogUtil.w(tag, "---count 333="+count);
    }

    public Polyline drawLine(List<MyLatLng> points) {
        List<LatLng> list = new ArrayList<>();
        for (MyLatLng latlng : points) {
            list.add(GaodeMapUtil.convertToGaode(mContext, latlng, CoordinateConverter.CoordType.GPS));
        }
        PolylineOptions ooPolyline = new PolylineOptions().width(5)
                .color(0xAAFF0000);
        ooPolyline.setPoints(list);
        Polyline line = null;
        if (this.getMapControllor() != null) {
            line = this.getMapControllor().addPolyline(ooPolyline);
            mMifOverlay.add(line);
        }
        return line;
    }

    public Text drawText(MyLatLng latlng, String text, int textSize, int textColor) {
        TextOptions options = new TextOptions();
        options.position(GaodeMapUtil.convertToGaode(mContext, latlng, CoordinateConverter.CoordType.GPS));
        options.text(text);
        options.fontSize(textSize);
        options.fontColor(textColor);
        Text textOverlay = null;
        if (this.getMapControllor() != null) {
            textOverlay = this.getMapControllor().addText(options);
            mTextOverlay.add(textOverlay);
        }
        return textOverlay;
    }
}
