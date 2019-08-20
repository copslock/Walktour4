package com.walktour.gui.newmap.overlay.variable;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.baidu.mapapi.map.Marker;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.newmap.BaseMapActivity;
import com.walktour.gui.newmap.layer.BaseMapLayer;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap.overlay.BaseMapOverlay.OverlayType;
import com.walktour.model.AlarmModel;
import com.walktour.model.MapEvent;

/**
 * 告警事件显示图层
 *
 * @author jianchao.wang
 */
public class AlarmPointOverlay extends BaseVariableOverlay {
    /**
     * 事件点描述
     */
    private LinearLayout eventDesc;
    /**
     * 点击的告警事件
     */
    private AlarmModel clickItem;
    /**
     * 是否已绘制图层
     */
    private boolean isDraw = false;
    /**
     * 告警事件的显示对象
     */
    private Marker mMarker;
    /**
     * 告警事件图像
     */
    private Bitmap mBitMap;

    @SuppressLint("InflateParams")
    public AlarmPointOverlay(BaseMapActivity activity, View parent, BaseMapLayer mapLayer) {
        super(activity, parent, mapLayer, "AlarmPointOverlay", OverlayType.Alarm);
        this.eventDesc = (LinearLayout) LayoutInflater.from(this.mActivity).inflate(R.layout.even_descr, null);
        this.eventDesc.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void drawCanvas(Canvas canvas) {
        if (this.isDraw)
            return;
        this.clearMarker();
        this.createMarker();
        this.isDraw = true;
    }

    /**
     * 生成覆盖物对象
     *
     * @return 是否生成成功
     */
    private void createMarker() {
        if (this.mMarker != null) {
            return;
        }
        if (this.clickItem == null || this.clickItem.getMapEvent() == null)
            return;
        LogUtil.d(TAG, "-----createMarker-----");
        this.createAlarmBitmap();
        MapEvent event = this.clickItem.getMapEvent();
        this.mMarker = (Marker) super.mMapLayer.drawBitmapMarker(
                new MyLatLng(event.getAdjustLatitude(), event.getAdjustLongitude()), this.mBitMap, 0.5f, 1f, null);
    }

    /**
     * 生成告警描述的图片
     */
    private void createAlarmBitmap() {
        if (this.mBitMap != null)
            return;
        final TextView title = (TextView) this.eventDesc.findViewById(R.id.event_title);
        final TextView content = (TextView) this.eventDesc.findViewById(R.id.even_content);
        final TextView number = (TextView) this.eventDesc.findViewById(R.id.event_num);
        title.setText(this.clickItem.getDescription(this.mActivity));
        content.setText(this.clickItem.getMapPopInfo());
        number.setText(mActivity.getString(R.string.tag_alarm_number) + this.clickItem.getNumber());
        this.eventDesc.measure(0, 0);
        int width = this.eventDesc.getMeasuredWidth();
        int height = this.eventDesc.getMeasuredHeight();
        this.mBitMap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(this.mBitMap);
        this.eventDesc.measure(0, 0);
        this.eventDesc.layout(0, 0, width, height);
        this.eventDesc.draw(canvas);
    }

    @Override
    protected boolean onClick(Point click) {
        if (this.clickItem != null) {
            this.clickItem = null;
            this.isDraw = false;
        }
        return false;
    }

    /**
     * 清除覆盖物
     */
    private void clearMarker() {
        if (this.mMarker != null) {
            this.mMarker.remove();
            this.mMarker = null;
            if (this.mBitMap != null) {
                this.mBitMap.recycle();
                this.mBitMap = null;
            }
        }
    }

    @Override
    protected boolean onLongClick(Point click) {
        return false;
    }

    @Override
    public void closeShowPopWindow() {
        // 无须实现

    }

    @Override
    public void changeMapType() {
        // 无须实现

    }

    @Override
    public void onDestroy() {
        this.clearMarker();
    }

    @Override
    public void onResume() {
        this.isDraw = false;
    }

    @Override
    public boolean onMarkerClick(Bundle bundle) {
        int typeId = bundle.getInt("type", -1);
        if (typeId == -1 || !OverlayType.get(typeId).equals(super.mType)) {
            return false;
        }
        if (super.factory.getAlarmList() == null || super.factory.getAlarmList().isEmpty()) {
            this.clickItem = null;
        } else {
            this.clickItem = null;
            long id = bundle.getLong("id");
            for (int i = 0; i < super.factory.getAlarmList().size(); i++) {
                AlarmModel alarm = super.factory.getAlarmList().get(i);
                MapEvent event = alarm.getMapEvent();
                if (event != null) {
                    if (event.getId() == id) {
                        int alarmNumber = 0;
                        //遍历查到有多少个该类型的告警
                        for (int j = 0; j < super.factory.getAlarmList().size(); j++) {
                            AlarmModel alarmCompare = super.factory.getAlarmList().get(j);
                            if (alarm.getDescrtion().equals(alarmCompare.getDescrtion())) {
                                alarmNumber++;
                            }
                        }
                        alarm.setNumber(alarmNumber);
                        this.clickItem = alarm;
                        break;
                    }
                }
            }
        }
        if (this.clickItem != null) {
            this.isDraw = false;
            return true;
        }
        return false;
    }

}
