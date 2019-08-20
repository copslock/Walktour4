package com.walktour.gui.newmap2.overlay.baidu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.HeatMap;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.walktour.base.util.NetRequest;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.gui.newmap2.overlay.BaseOverlayManager;
import com.walktour.gui.newmap2.overlay.OverlayType;
import com.walktour.gui.newmap2.ui.MapProgressDialog;
import com.walktour.gui.newmap2.util.BaiduMapUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Request;

/**
 * 热力图
 *
 * @author zhicheng.chen
 * @date 2018/11/18
 */
public class BdHeatMapManager extends BaseOverlayManager implements Handler.Callback {

    private static final String IP = "http://172.16.23.253";
    private static final String URL = IP + "/Services/AppService.svc/GetBTS?";

    private int mCutTimes = 3; //分割次数
    private int mReqestTimes = mCutTimes * mCutTimes;//请求接口的次数
    private int mSquareMaxCount = 20000;//分块每次最大多少数据

    public static final int SHOW_LOADING = 1;//显示加载框
    public static final int HIDE_LOADING = 2;//隐藏加载框
    public static final int START_LOAD_HEAT = 3;//开始load热力图

    private static final int ADD_HEAT_MAP = 1;//添加热力图
    private static final int REMOVE_HEAT_MAP = 2;//移除热力图
    private static final int PARSE_JSON_DATA = 3;//解析数据

    private boolean mIsStillLoading;

    private AtomicInteger atomicInteger = new AtomicInteger(1);

    private List<LatLng> mDatas = new ArrayList<>();

    private MapProgressDialog mPgDialog;

    private Handler mH;
    private HeatMap mHeatMap;

    enum Scale {

        SMALL(6, 5, 8000),
        NORMAL(12, 4, 12500),
        LARGE(19, 3, 22000);

        int zoom; //地图缩放级别
        int cutTime;//切割次数
        int squreCount;//每个方块的总数

        Scale(int level, int cutTime, int squreCount) {
            this.zoom = level;
            this.cutTime = cutTime;
            this.squreCount = squreCount;
        }
    }

    private Handler mUiHd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == SHOW_LOADING) {
                if (mPgDialog != null) {
                    mPgDialog.show();
                }
            } else if (what == HIDE_LOADING) {
                if (mPgDialog != null) {
                    mPgDialog.hide();
                }
            } else if (what == START_LOAD_HEAT) {
                if (!mIsStillLoading) {
                    mUiHd.sendEmptyMessage(SHOW_LOADING);
                    mIsStillLoading = true;
                    atomicInteger.set(1);
                    mDatas.clear();
                    List<LatLng[]> latLngs = dividerScreen();
                    for (int i = 0; i < latLngs.size(); i++) {
                        getDataFromNet(latLngs.get(i));
                    }
                }
            }
        }
    };

    private List<LatLng[]> dividerScreen() {
        List<LatLng[]> llList = new ArrayList<>(mReqestTimes);
        BaiduMap baiduMap = getMapControllor();
        if (baiduMap != null) {
            Projection projection = baiduMap.getProjection();
            if (projection != null) {
                View mapView = mMapSdk.getMapView();
                int width = mapView.getWidth();
                int height = mapView.getHeight();

                int sW = width / mCutTimes;
                int sH = height / mCutTimes;

                for (int i = 0; i < mCutTimes; i++) {
                    for (int j = 0; j < mCutTimes; j++) {
                        Point lt = new Point(j * sW, i * sH);
                        Point rb = new Point((j + 1) * sW, (i + 1) * sH);

                        LatLng ltLL = projection.fromScreenLocation(lt);
                        LatLng rbLL = projection.fromScreenLocation(rb);

                        llList.add(new LatLng[]{ltLL, rbLL});
                    }
                }
            }
        }
        return llList;
    }

    private void getDataFromNet(LatLng[] ll) {
        double[] gpsLt = BaiduMapUtil.baidu2Gaode(ll[0]);
        double[] gpsrb = BaiduMapUtil.baidu2Gaode(ll[1]);

        Map<String, String> params = new HashMap<>();
        params.put("Network", "4G");
        params.put("Latitude1", gpsLt[0] + "");
        params.put("Longitude1", gpsLt[1] + "");
        params.put("Latitude2", gpsrb[0] + "");
        params.put("Longitude2", gpsrb[1] + "");
        NetRequest.getFormRequest(URL, params, new NetRequest.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                mH.obtainMessage(PARSE_JSON_DATA, result).sendToTarget();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                int value = atomicInteger.getAndIncrement();
                if (value == mReqestTimes) {
                    mH.sendEmptyMessage(ADD_HEAT_MAP);
                }
                Log.w("@@@", "times= " + value);
            }
        });
    }

    public BdHeatMapManager(Context context) {
        super(context);

        mPgDialog = new MapProgressDialog(((Activity) mContext).getParent());
        mPgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // set disable
        mIsOverlayEnable = false;

        HandlerThread thread = new HandlerThread("heat_map_thread");
        thread.start();

        mH = new Handler(thread.getLooper(), this);
    }

    @Override
    public void onMapStatusChangeFinish(Object... obj) {
        BaiduMap map = getMapControllor();
        if (map != null) {
            if (map.getMapStatus() != null) {
                float zoom = map.getMapStatus().zoom;
                if (zoom <= Scale.SMALL.zoom) {
                    mCutTimes = Scale.SMALL.cutTime;
                    mSquareMaxCount = Scale.SMALL.squreCount;
                } else if (zoom <= Scale.NORMAL.zoom) {
                    mCutTimes = Scale.NORMAL.cutTime;
                    mSquareMaxCount = Scale.NORMAL.squreCount;
                } else if (zoom <= Scale.LARGE.zoom) {
                    mCutTimes = Scale.LARGE.cutTime;
                    mSquareMaxCount = Scale.LARGE.squreCount;
                }
                mReqestTimes = mCutTimes * mCutTimes;
            }
        }

        if (isEnable() && !mIsStillLoading) {
            mUiHd.sendEmptyMessageDelayed(START_LOAD_HEAT, 2000);
        }
    }

    @Override
    public OverlayType getOverlayType() {
        return OverlayType.HeatMap;
    }

    // if isEnable add mHeatmap,otherwise hide mHeatmap ~
    @Override
    public void setEnable(boolean isEnable) {
        if (isEnable) {
            mUiHd.sendEmptyMessage(START_LOAD_HEAT);
        } else {
            mH.sendEmptyMessage(REMOVE_HEAT_MAP);
        }
        super.setEnable(isEnable);
    }

    @Override
    public boolean addOverlay(Object... obj) {
        return false;
    }


    @Override
    public boolean clearOverlay() {
        if (mHeatMap != null) {
            mHeatMap.removeHeatMap();
        }
        return false;
    }

    private BaiduMap getMapControllor() {
        return (BaiduMap) mMapSdk.getMapControllor();
    }

    @Override
    public boolean handleMessage(Message msg) {
        int what = msg.what;
        if (what == ADD_HEAT_MAP) {
            if (mDatas.size() > 0) {
                mHeatMap = new HeatMap.Builder().data(mDatas).build();
                getMapControllor().addHeatMap(mHeatMap);
            }
            mIsStillLoading = false;
            mUiHd.sendEmptyMessage(HIDE_LOADING);
        } else if (what == REMOVE_HEAT_MAP) {
            clearOverlay();
        } else if (what == PARSE_JSON_DATA) {
            String result = (String) msg.obj;
            try {
                JSONArray ja = new JSONArray(result);
                int offset = -1;
                if (ja.length() > mSquareMaxCount) {
                    // 过滤部分数据
                    offset = ja.length() / mSquareMaxCount;
                }
                List<LatLng> datas = new ArrayList<>();
                for (int i = 0; i < ja.length(); i++) {
                    if (offset != -1 && i % offset == 0) {
                        BaseStation bs = new BaseStation();
                        JSONObject jo = ja.optJSONObject(i);
                        LatLng ll = BaiduMapUtil.convert(jo.optDouble("Latitude"), jo.optDouble("Longitude"));
                        datas.add(ll);
                    } else if (offset == -1) {
                        BaseStation bs = new BaseStation();
                        JSONObject jo = ja.optJSONObject(i);
                        LatLng ll = BaiduMapUtil.convert(jo.optDouble("Latitude"), jo.optDouble("Longitude"));
                        datas.add(ll);
                    }
                }

                int value = atomicInteger.getAndIncrement();
                Log.w("@@@", "times= " + value + "; value=" + datas.size());
                if (datas.size() > 0) {
                    mDatas.addAll(datas);
                }
                Log.i("@@@", "总数:" + mDatas.size());
                if (value == mReqestTimes) {
                    mH.sendEmptyMessage(ADD_HEAT_MAP);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
