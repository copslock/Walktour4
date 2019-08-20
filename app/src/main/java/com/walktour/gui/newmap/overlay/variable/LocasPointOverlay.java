package com.walktour.gui.newmap.overlay.variable;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.dinglicom.dataset.DatasetManager;
import com.dinglicom.dataset.PlaybackManager;
import com.dinglicom.dataset.logic.ControlPanelLinstener;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ParameterSetting;
import com.walktour.gui.R;
import com.walktour.gui.newmap.BaseMapActivity;
import com.walktour.gui.newmap.layer.BaseMapLayer;
import com.walktour.gui.newmap.overlay.BaseMapOverlay.OverlayType;
import com.walktour.model.LocusParamInfo;
import com.walktour.model.MapEvent;
import com.walktour.model.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * 轨迹点显示图层
 *
 * @author jianchao.wang
 *
 */
@SuppressLint("ViewConstructor")
public class LocasPointOverlay extends BaseVariableOverlay implements ControlPanelLinstener {

	/** 数据集管理对象 */
	private DatasetManager mDatasetMgr;
	/** 参数设置 */
	private ParameterSetting mParameterSet;
	/** 参数列表 */
	private List<Parameter> parameterms;
	/** 画笔 */
	private Paint mPaint;
	/** 选中点图标 */
	private Drawable marker;
	/** 选中点图标宽度 */
	private int mMarkerWidth;
	/** 选中点图标高度 */
	private int mMarkerHeight;
	/** 显示轨迹,且表示当前轨迹的Index */
	private int showIndex = 0;
	/** 点击的轨迹点 */
	private MapEvent clickItem;
	/** 采样点描述 */
	private LinearLayout locasDesc;

	private int currentIndex;
	private boolean mExecuteDraw;//是否在执行画图
	private Point mCurrentPoint;

	@SuppressLint("InflateParams")
	public LocasPointOverlay(BaseMapActivity activity, View parent, BaseMapLayer mapLayer) {
		super(activity, parent, mapLayer, "LocasPointOverlay", OverlayType.LocasPoint);
		mDatasetMgr = DatasetManager.getInstance(mActivity.getApplicationContext());
		mParameterSet = ParameterSetting.getInstance();
		mParameterSet.initMapLocusShape(mActivity.getApplicationContext());
		this.locasDesc = (LinearLayout) LayoutInflater.from(this.mActivity).inflate(R.layout.poi_descr, null);
		this.locasDesc.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setStrokeWidth(3);
		// 添加外圈
		mPaint.setAntiAlias(true);
		marker = this.mActivity.getResources().getDrawable(R.drawable.iconmarker);
		this.mMarkerWidth = marker.getIntrinsicWidth();
		this.mMarkerHeight = marker.getIntrinsicHeight();

		if (mDatasetMgr.getPlaybackManager() != null) {
			mDatasetMgr.getPlaybackManager().addControPanelListener(this);
			currentIndex = mDatasetMgr.getPlaybackManager().getCurrentIndex();
		}
	}

	/**
	 * 获得选中的点
	 * 
	 * @return
	 */
	private Point getMarkPoint() {
		List<MapEvent> list = new ArrayList<MapEvent>(super.factory.getLocasList());
		if (list.isEmpty())
			return null;
		if (DatasetManager.isPlayback) {
			for (int i = list.size() - 1; i >= 0; i--) {
				MapEvent mapEvent = list.get(i);
				if (mCurrentPoint == null) {
					Point point = new Point((int) mapEvent.getX(), (int) mapEvent.getY());
					mCurrentPoint = point;
				}
				int currentSpeed = mDatasetMgr.getPlaybackManager().getCurrentSpeed();
				int offset = currentSpeed * 100;
				if ((currentIndex >= (mapEvent.getBeginPointIndex()-offset)
						&& currentIndex <= (mapEvent.getEndPointIndex()+offset))
					/*|| currentIndex < mapEvent.getBeginPointIndex()*/) {
					Point point = new Point((int) mapEvent.getX(), (int) mapEvent.getY());
					mCurrentPoint = point;
					return point;
				}
			}
			return mCurrentPoint;
		} else {// 正常测试状态,也即是非回放状态下,将最后一个点置为当前点
			MapEvent mapEvent = list.get(list.size() - 1);
			return new Point((int) mapEvent.getX(), (int) mapEvent.getY());
		}
	}

	@Override
	public void drawCanvas(Canvas canvas) {
		mExecuteDraw = true;
		// 绘制点击事件Pop
		if (this.clickItem != null) {
			drawSelectItem(canvas);
		}
		// 绘制当前点
		Point markPoint = this.getMarkPoint();
		if (markPoint != null) {
			marker.setBounds(markPoint.x - mMarkerWidth / 2, markPoint.y - mMarkerHeight / 2, markPoint.x + mMarkerWidth / 2,
					markPoint.y + mMarkerHeight / 2);
			marker.draw(canvas);
		}
		mExecuteDraw = false;
	}

	/**
	 * 绘制选中的轨迹点
	 *
	 * @param canvas
	 *          画布
	 */
	private void drawSelectItem(Canvas canvas) {
		int pointX = (int) clickItem.getX();
		int pointY = (int) clickItem.getY();
		if (pointX < 0 || pointY <= 0)
			return;
		float radius = this.getLocusRadius();
		mPaint.setColor(this.clickItem.getColor());
		mPaint.setStyle(Paint.Style.FILL);
		canvas.drawCircle(pointX + showIndex * radius * 3, pointY, radius, mPaint);
		final TextView title = (TextView) this.locasDesc.findViewById(R.id.poi_title);
		final TextView descr = (TextView) this.locasDesc.findViewById(R.id.descr);
		final TextView coord = (TextView) this.locasDesc.findViewById(R.id.coord);

		title.setText("Location");
		descr.setText(this.clickItem.getMapPopInfo());
		if (parameterms == null || parameterms.isEmpty()) {
			coord.setVisibility(View.GONE);
		} else {
			LocusParamInfo locusParamInfo = null;
			locusParamInfo = this.clickItem.getParamInfoMap().get(parameterms.get(showIndex).getShowName());
			if (locusParamInfo == null || locusParamInfo.paramName == null || locusParamInfo.value == -9999) {
				coord.setVisibility(View.GONE);
			} else {
				coord.setVisibility(View.VISIBLE);
				coord.setText(locusParamInfo.paramName + ":" + locusParamInfo.value);
			}
		}
		this.locasDesc.measure(0, 0);
		this.locasDesc.layout(0, 0, this.locasDesc.getMeasuredWidth(), this.locasDesc.getMeasuredHeight());
		canvas.save();
		canvas.translate(pointX - this.locasDesc.getMeasuredWidth() / 2 + showIndex * radius * 3,
				pointY - 4 * mDensity - this.locasDesc.getMeasuredHeight());
		this.locasDesc.draw(canvas);
		canvas.restore();
	}

	/**
	 * 获取轨迹的半径大小<BR>
	 * [功能详细描述]
	 */
	private int getLocusRadius() {
		float radius = 8 * mDensity;
		switch (mParameterSet.getLocusSize()) {
		case 0:
			radius = 12 * mDensity;
			break;
		case 1:
			radius = 8 * mDensity;
			break;
		case 2:
			radius = 4 * mDensity;
			break;
		default:
			break;
		}
		return (int) radius;
	}

	/**
	 * 判断当前参数是否有更改
	 *
	 * @return
	 */
	private void checkParamerChange() {
		List<Parameter> params = mParameterSet
				.getCheckedParamertersByNet(MyPhoneState.getInstance().getCurrentNetForParam(this.mActivity));
		if (params == null || params.size() == 0 || this.parameterms == null || this.parameterms.size() != params.size()) {
			this.clickItem = null;
			this.showIndex = 0;
			this.parameterms = params;
			return;
		}
		int count = 0;
		for (Parameter param : params) {
			for (Parameter param1 : this.parameterms) {
				if (param.getId().equals(param1.getId())) {
					count++;
				}
			}
		}
		if (params.size() != count) {
			this.clickItem = null;
			this.showIndex = 0;
			this.parameterms = params;
			return;
		}
	}

	/**
	 * 获取点中的轨迹点
	 *
	 * @param click
	 *          点击坐标
	 * @return
	 */
	private MapEvent getClickPoint(Point click) {
		float radius = this.getLocusRadius();
		if (this.parameterms == null || this.parameterms.isEmpty())
			return null;
		final Rect rect = new Rect();
		int left, right, top, bottom;
		int pointX, pointY;
		List<MapEvent> list = new ArrayList<MapEvent>(super.factory.getLocasList());
		if (list.isEmpty())
			return null;
		for (int i = list.size() - 1; i >= 0; i--) {
			MapEvent event = list.get(i);
			pointX = (int) event.getX();
			pointY = (int) event.getY();
			for (int j = 0; j < parameterms.size(); j++) {
				left = (int) (pointX - radius + j * radius * 3);
				right = (int) (pointX + radius + j * radius * 3);
				top = (int) (pointY - radius);
				bottom = (int) (pointY + radius);
				rect.set(left, top, right, bottom);
				if (rect.contains(click.x, click.y)) {
					showIndex = j;
					if (DatasetManager.isPlayback) {
						PlaybackManager playbackManager = mDatasetMgr.getPlaybackManager();
						playbackManager.setSkipIndex(event.getBeginPointIndex());
					}
					return event;
				}
			}
		}
		return null;
	}

	@Override
	protected boolean onClick(Point click) {
		if (super.factory.getLocasList().isEmpty()) {
			this.showIndex = 0;
			this.clickItem = null;
		} else {
			this.checkParamerChange();
			this.clickItem = this.getClickPoint(click);
		}
		if (this.clickItem != null)
			return true;
		return false;
	}

	@Override
	protected boolean onLongClick(Point point) {
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
		// 无须实现

	}

	@Override
	public void onResume() {
		// 无须实现

	}

	@Override
	public boolean onMarkerClick(Bundle bundle) {
		return false;
	}

	@Override
	public boolean onPlay(int pointIndexTotal) {
		return false;
	}

	@Override
	public boolean onStop() {
		return false;
	}

	@Override
	public boolean onPasue() {
		return false;
	}

	@Override
	public boolean onFastForward(int speed) {
		return false;
	}

	@Override
	public boolean onRewind(int speed) {
		return false;
	}

	@Override
	public boolean onNext() {
		return false;
	}

	@Override
	public boolean onUp() {
		return false;
	}

	@Override
	public void onSeekBar(int progress) {
		updateOverlay(progress);
	}

	@Override
	public void onSeekBar(long progressTime, int progress) {
		updateOverlay(progress);
	}


	private synchronized void updateOverlay(final int progress) {
		if (!mExecuteDraw) {
			currentIndex = progress;
		}
	}
}
