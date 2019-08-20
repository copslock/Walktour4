package com.walktour.gui.newmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ParameterSetting;
import com.walktour.framework.ui.ActivityManager;
import com.walktour.gui.R;
import com.walktour.gui.newmap.layer.baidu.BaiduMapLayer;
import com.walktour.gui.newmap.overlay.BaseMapOverlay;

import java.util.List;

/**
 * 百度地图控件
 * 
 * @author jianchao.wang
 * 
 */
public class BaiduMapMainActivity extends BaseMapActivity implements SnapshotReadyCallback {

	/** 导出地图存放路径 */
	private String path = null;
	/** 导出地图的文件名称 */
	private String picName = null;
	private static final String TAG = "BaiduMapMainActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.mapType = MAP_TYPE_BAIDU;
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(this.getApplicationContext());
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onTabActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtil.d(TAG, "----onTabActivityResult----");
		String mapName = "Baidu.Map";
		String[] dtmap = getResources().getStringArray(R.array.sys_dtmap_default);
		if (dtmap.length > 3 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[3]))
			mapName = "A.Map";
		switch (resultCode) {
			case OFFLINE_MAP_RESULT_CODE:
				super.factory.setTitle(mapName + " Offline");
				break;
			case ONLINE_MAP_RESULT_CODE:
				super.factory.setTitle(mapName + " Online");
				break;
		}
		super.setMapTitle();
	}

	@Override
	protected void doImportTrack(String mFileName) {
		// TODO 百度地图暂时不支持

	}

	@Override
	protected void setTileSource(String mapId) {
		// TODO 百度地图暂时不支持

	}

	@Override
	public void saveViewToBMP(String path, String picName) {
		this.path = path;
		if (StringUtil.isNullOrEmpty(picName))
			picName = this.getPicName();
		this.picName = picName;
		((BaiduMapLayer) this.mapLayer).snapshot(this);
	}

	@Override
	public void onSnapshotReady(Bitmap bitmap) {
		LinearLayout lineTool = initLinearLayout(R.id.LineraLayoutToolbar);
		lineTool.setVisibility(View.GONE);
		View view = this.getWindow().getDecorView();
		view.buildDrawingCache();
		Bitmap parentmap = view.getDrawingCache();
		lineTool.setVisibility(View.VISIBLE);
		if (parentmap == null) {
			return;
		}
		Bitmap basemap = Bitmap.createBitmap(parentmap.getWidth(), parentmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(basemap);
		canvas.drawBitmap(parentmap, 0, 0, null);
		View thresholdView = this.findViewById(R.id.threshold_view);
		Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		float top = thresholdView.getTop() - bitmap.getHeight();
		RectF dst = new RectF(0, top, basemap.getWidth(), thresholdView.getTop());
		canvas.drawBitmap(bitmap, src, dst, null);
		List<BaseMapOverlay> list = this.mapLayer.getOverlays();
		for (BaseMapOverlay overlay : list) {
			canvas.drawBitmap(overlay.getBitmap(), src, dst, null);
		}
		this.saveBitmapToFile(this.path, basemap, this.picName);
		Toast.makeText(getApplicationContext(), R.string.map_export_success, Toast.LENGTH_SHORT).show();
		if (TraceInfoInterface.isSaveFileLocus) {
			TraceInfoInterface.isSaveFileLocus = false;
			ActivityManager.removeLast();
		}
	}

}
