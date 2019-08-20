package com.walktour.gui.newmap2.ui;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;

/**
 * @author zhicheng.chen
 * @date 2018/9/3
 */
public class AutoScaleTextView extends AppCompatTextView {
	private static final String TAG = "AutoScaleTextview";
	private float preferredTextSize;
	private float minTextSize;
	private Paint textPaint;

	public AutoScaleTextView(Context context) {
		super(context, null);
		init();
	}

	public AutoScaleTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AutoScaleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();

	}

	private void init() {
		this.textPaint = new Paint();
		this.minTextSize = 10;
		this.preferredTextSize = this.getTextSize();
		Log.d(TAG, "this.preferredTextSize = " + this.preferredTextSize + ", this.minTextSize = " + this.minTextSize);
	}

	/**
	 * 设置最小的size
	 *
	 * @param minTextSize
	 */
	public void setMinTextSize(float minTextSize) {
		this.minTextSize = minTextSize;
	}

	/**
	 * 根据填充内容调整textview
	 *
	 * @param text
	 * @param textWidth
	 */
	private void refitText(String text, int textWidth) {
		if (textWidth <= 0 || text == null || text.length() == 0) {
			return;
		}

		int targetWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight();

		Log.d(TAG, "targetWidth = " + targetWidth);

		final float threshold = 0.5f;

		this.textPaint.set(this.getPaint());

		this.textPaint.setTextSize(this.preferredTextSize);
		if (this.textPaint.measureText(text) <= targetWidth) {
			this.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.preferredTextSize);
			return;
		}

		float tempMinTextSize = this.minTextSize;
		float tempPreferredTextSize = this.preferredTextSize;

		Log.d(TAG, "this.preferredTextSize = " + tempPreferredTextSize + ", this.minTextSize = " + tempMinTextSize);
		while ((tempPreferredTextSize - tempMinTextSize) > threshold) {
			float size = (tempPreferredTextSize + tempMinTextSize) / 2;
			this.textPaint.setTextSize(size);
			if (this.textPaint.measureText(text) >= targetWidth) {
				tempPreferredTextSize = size;
			} else {
				tempMinTextSize = size;
			}
		}
		Log.d(TAG, "this.minTextSize = " + tempMinTextSize);

		this.setTextSize(TypedValue.COMPLEX_UNIT_PX, tempMinTextSize);

	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
		this.refitText(text.toString(), this.getWidth());
	}

	@Override
	protected void onSizeChanged(int width, int h, int oldw, int oldh) {
		if (width != oldw) {
			this.refitText(this.getText().toString(), width);
		}
	}
}
