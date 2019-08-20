package com.walktour.gui.newmap2.overlay.baidu;

import android.content.Context;
import android.graphics.Color;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.walktour.Utils.DensityUtil;
import com.walktour.gui.R;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.newmap2.overlay.BaseOverlayManager;
import com.walktour.gui.newmap2.overlay.OverlayType;
import com.walktour.gui.newmap2.util.BaiduMapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 测距图层
 *
 * @author zhicheng.chen
 * @date 2018/6/9
 */
public class BdMeasureOverlayManager extends BaseOverlayManager {

    private Overlay mP1;
    private Overlay mP2;
    private Overlay mFirstP;//记录当前最后一个点
    private Overlay mLine;
    List<LatLng> points = new ArrayList<LatLng>();
    private final BitmapDescriptor mIcon;
    private Overlay mDistanceText;

    public BdMeasureOverlayManager(Context context) {
        super(context);
        mIcon = BitmapDescriptorFactory.fromResource(R.drawable.iconmarker2);
        setEnable(NewMapFactory.getInstance().isRanging());
    }

    @Override
    public OverlayType getOverlayType() {
        return OverlayType.RangingLink;
    }

    @Override
    public boolean onMapClick(Object... obj) {
        final LatLng latLng = (LatLng) obj[0];
        drawMarker(latLng);
        return isEnable();
    }

    private void drawMarker(LatLng latLng) {
        if (isEnable()) {
            if (mP1 == null) {
                OverlayOptions mk = new MarkerOptions()
                        .icon(mIcon)
                        .position(latLng);
                mP1 = getMapControllor().addOverlay(mk);
                mFirstP = mP1;
            } else if (mP2 == null) {
                OverlayOptions mk = new MarkerOptions()
                        .icon(mIcon)
                        .zIndex(9)
                        .position(latLng);
                mP2 = getMapControllor().addOverlay(mk);
                drawLineAndText();
                mFirstP = mP1;
            } else if (mP1 != null && mP2 != null) {
                if (mFirstP != null) {
                    mFirstP.remove();
                }
                if (mLine != null) {
                    mLine.remove();
                }
                if (mDistanceText != null) {
                    mDistanceText.remove();
                }
                mP1 = mP2;
                mFirstP = mP1;
                OverlayOptions mk = new MarkerOptions()
                        .icon(mIcon)
                        .zIndex(9)
                        .position(latLng);
                mP2 = getMapControllor().addOverlay(mk);

                points.clear();
                drawLineAndText();
            }
        }
    }

    private void drawLineAndText() {
        LatLng start = ((Marker) mP1).getPosition();
        LatLng end = ((Marker) mP2).getPosition();
        points.add(start);
        points.add(end);

        OverlayOptions ooPolyline = new PolylineOptions()
                .width(5)
                .color(0xAAFF0000)
                .zIndex(8)
                .points(points);
        mLine = getMapControllor().addOverlay(ooPolyline);

        LatLng center = new LatLng((start.latitude + end.latitude) / 2, (start.longitude + end.longitude) / 2);
        OverlayOptions textOption = new TextOptions()
                .text(BaiduMapUtil.getDistanceStr(start, end))
                .fontColor(0xFFFF0000)
                .zIndex(8)
                .bgColor(Color.WHITE)
                .fontSize(DensityUtil.dip2px(mContext, 10))
                .position(center);

        mDistanceText = getMapControllor().addOverlay(textOption);
    }

    private BaiduMap getMapControllor() {
        return (BaiduMap) mMapSdk.getMapControllor();
    }


    @Override
    public boolean addOverlay(Object... obj) {
        return false;
    }

    @Override
    public boolean clearOverlay() {
        if (mP1 != null) {
            mP1.remove();
            mP1 = null;
        }
        if (mP2 != null) {
            mP2.remove();
            mP2 = null;
        }
        if (mLine != null) {
            mLine.remove();
            mLine = null;
        }
        if (mFirstP != null) {
            mFirstP = null;
        }
        if (mDistanceText != null) {
            mDistanceText.remove();
            mDistanceText = null;
        }
        points.clear();
        return true;
    }

    @Override
    public void onDestory() {
        super.onDestory();
    }

    @Override
    public void setEnable(boolean isEnable) {
        super.setEnable(isEnable);
        if (!isEnable) {
            clearOverlay();
        }
    }
}
