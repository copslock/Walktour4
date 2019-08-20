package com.walktour.gui.newmap2.overlay.gaode;

import android.content.Context;
import android.graphics.Color;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.walktour.Utils.DensityUtil;
import com.walktour.gui.R;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.newmap2.overlay.BaseOverlayManager;
import com.walktour.gui.newmap2.overlay.OverlayType;


/**
 * @author jinfeng.xie
 * @version 1.0.0
 * @date on 2018/6/11
 * @describe 测距图层
 */
public class GdMeasureOverlayManager extends BaseOverlayManager {

    private Marker startMark;//开始的点
    private Marker endMark;//最后的点
    private Marker flagMark;//标记点，用于删除
    private final BitmapDescriptor mIcon;
    private Text mDistanceText;
    private TextOptions mTextOptions;//距离文本
    private Polyline line;//线

    public GdMeasureOverlayManager(Context context) {
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
            if (startMark == null) {
                MarkerOptions mk = new MarkerOptions()
                        .icon(mIcon)
                        .position(latLng);
                startMark = getMapControllor().addMarker(mk);
            } else {
                if (flagMark != null) {
                    mDistanceText.remove();
                    line.remove();
                    flagMark.remove();
                }
                MarkerOptions mk = new MarkerOptions()
                        .icon(mIcon)
                        .position(latLng);
                endMark = getMapControllor().addMarker(mk);
                drawLineAndText();

                flagMark = startMark;
                startMark = endMark; //将最后一个坐标切换，为了下一组坐标的组合
            }
        }
    }

    /**
     * 绘制线和文本
     */
    private void drawLineAndText() {
        LatLng start = startMark.getPosition();
        LatLng end = endMark.getPosition();
        PolylineOptions mPolylineOptions = new PolylineOptions().add(start, end).color(Color.RED);
        mTextOptions = new TextOptions().position(getMidLatLng(start, end)).text("cal distance ...").
                fontSize(DensityUtil.dip2px(mContext, 10));
        line= getMapControllor().addPolyline(mPolylineOptions);
        mDistanceText = getMapControllor().addText(mTextOptions);
        mDistanceText.setText(getDistance(AMapUtils.calculateLineDistance(start, end)));
    }

    private AMap getMapControllor() {
        return (AMap) mMapSdk.getMapControllor();
    }


    /**
     * 求两个经纬度的中点
     *
     * @param l1
     * @param l2
     * @return
     */
    private LatLng getMidLatLng(LatLng l1, LatLng l2) {
        return new LatLng((l1.latitude + l2.latitude) / 2, (l1.longitude + l2.longitude) / 2);
    }

    /**
     * 一个float的长度，转化成String
     */
    private String getDistance(double distance) {
        String ret;
        if (distance > 1000) {
            ret = String.format("%.2fKM", (float) distance / 1000);
        } else {
            ret = String.format("%.2fM", distance);
        }
        return ret;
    }

    @Override
    public boolean addOverlay(Object... obj) {
        return false;
    }

    @Override
    public boolean clearOverlay() {
        if (startMark != null) {
            startMark.remove();
        }
        if (endMark != null) {
            endMark.remove();
        }
        if (line != null) {
            line.remove();
        }
        if (flagMark != null) {
            flagMark.remove();
        }
        if (mDistanceText != null) {
            mDistanceText.remove();
        }
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
