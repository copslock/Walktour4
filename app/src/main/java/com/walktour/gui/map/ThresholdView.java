package com.walktour.gui.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.walktour.Utils.DensityUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.LegendColors;
import com.walktour.control.config.ParameterSetting;
import com.walktour.model.DynamicColors;
import com.walktour.model.Parameter;
import com.walktour.model.Threshold;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 地图下方阀值显示栏视图
 *
 * @author jianchao.wang
 *
 */
public class ThresholdView extends View {
//	private static final String TAG = "ThresholdView";
	/** 画笔 */
	private Paint mPaint;
	/** 字体大小 */
	private int textSize = 18;
	/** 系统缩放比例 */
	private float systemScale;
	/** 阀值显示名称数组 */
	private String thresholdTexts[] = new String[6];
	/** 阀值显示名称宽度数组 */
	private float thresholdTextWidths[] = new float[6];
	/** 阀值显示底色数组 */
	private int thresholdColors[] = new int[6];

	private DecimalFormat df = new DecimalFormat("#.##");

	/**
	 * [构造简要说明]
	 *
	 * @param context
	 * @param attrs
	 */
	public ThresholdView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		DisplayMetrics metric = new DisplayMetrics();
		((Activity) this.getContext()).getWindowManager().getDefaultDisplay().getMetrics(metric);
		systemScale = metric.densityDpi / 240.f;
		textSize *= systemScale;
		mPaint.setTextSize(textSize);
		mPaint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int net = MyPhoneState.getInstance().getCurrentNetForParam(this.getContext());
		List<Parameter> parameterList = ParameterSetting.getInstance().getCheckedParamertersByNet(net);
		Parameter mParameter = parameterList.size() > 0 ? parameterList.get(0) : null;
		if (mParameter == null) {
			return;
		}
		int length = mParameter.getThresholdList().size();
        String showName = mParameter.getShowName();

		if (length > this.thresholdTexts.length - 1) {
            length = this.thresholdTexts.length - 1;
        }
        float textWidth = mPaint.measureText(showName);
		if (length > 0) {
			thresholdTexts[0] = showName;
			thresholdColors[0] = Color.parseColor("#FFECECEC");
			thresholdTextWidths[0] = textWidth;
			for (int i = 0; i < length; i++) {
				Threshold threshold = mParameter.getThresholdList().get(i);
				String text = threshold.getValue2ShowForMap();
				thresholdTextWidths[i + 1] = mPaint.measureText(text);
				textWidth += thresholdTextWidths[i + 1];
				thresholdTexts[i + 1] = text;
				thresholdColors[i + 1] = threshold.getColor();
			}
			float marginWidth = (this.getWidth() - textWidth) / (length + 1) / 2;
			float top = 0;
			float bottom = this.getHeight();
			float left = 0;
			float right = 0;
			for (int i = 0; i < thresholdTexts.length; i++) {
				right += marginWidth + thresholdTextWidths[i] + marginWidth;
				mPaint.setColor(thresholdColors[i]);
				canvas.drawRect(left, top, right, bottom, mPaint);
				mPaint.setColor(Color.BLACK);
				canvas.drawText(thresholdTexts[i], left + marginWidth, getHeight()/ 2 + DensityUtil.dip2px(getContext(),3), mPaint);
				left = right;
			}
		} else {
            DynamicColors[] dynamicLis = LegendColors.getInstance().getDynamicLis();
            int colCount = 5;
            canvas.drawColor(Color.parseColor("#FFECECEC"));
            textWidth = mPaint.measureText(showName);
            float marginWidth = 8 * this.systemScale;
            Rect rect = new Rect();
            mPaint.getTextBounds(showName, 0, showName.length(), rect);
            canvas.drawText(showName, marginWidth, (getHeight() + rect.height()) / 2, mPaint);
            float colorLeft = marginWidth * 2 + textWidth;
            float colWidth = (this.getWidth() - colorLeft) / colCount;
            int rowCount = dynamicLis.length / colCount;
            if (dynamicLis.length % colCount > 0) {
                rowCount++;
            }
            float rowHeight = this.getHeight() / rowCount;
            int pos = 0;
            float textSize = rowHeight - 4 * systemScale;
            float rowUpBit = 2 * systemScale;
            mPaint.setTextSize(textSize);
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < colCount; col++) {
                    float left = colorLeft + col * colWidth;
                    float top = row * rowHeight;
                    float right = left + colWidth;
                    float bottom = top + rowHeight;
                    mPaint.setColor(getResources().getColor(dynamicLis[pos].color));
                    canvas.drawRect(left, top, right, bottom, mPaint);
                    mPaint.setColor(Color.BLACK);
                    String text = dynamicLis[pos].value == -9999 ? "" : df.format(dynamicLis[pos].value);
                    left += (colWidth - mPaint.measureText(text)) / 2;
                    canvas.drawText(text, left, bottom - rowUpBit, mPaint);
                    pos++;
                }
            }
		}
	}
}
