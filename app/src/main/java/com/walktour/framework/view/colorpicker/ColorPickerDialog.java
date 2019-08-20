package com.walktour.framework.view.colorpicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.colorpicker.ColorPickerView.OnColorChangedListener;
import com.walktour.gui.R;

/**
 * 自定义颜色选择对话框<BR>
 * [功能详细描述]
 * 
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2013-10-5]
 */
public class ColorPickerDialog extends BasicDialog implements ColorPickerView.OnColorChangedListener {

	private ColorPickerView mColorPicker;

	private ColorPanelView mOldColor;
	private ColorPanelView mNewColor;

	private OnColorChangedListener mListener;

	public Builder builder;

	public ColorPickerDialog(Context context, int initialColor) {
		this(context, initialColor, null);
		builder = new BasicDialog.Builder(context);
		init(initialColor);

	}

	public ColorPickerDialog(Context context, int initialColor, OnColorChangedListener listener) {
		super(context);
		mListener = listener;
		builder = new BasicDialog.Builder(context);
		init(initialColor);
	}

	private void init(int color) {
		// To fight color branding.
		getWindow().setFormat(PixelFormat.RGBA_8888);
		setUp(color);
	}

	@SuppressLint("InflateParams")
	private void setUp(int color) {
		boolean isLandscapeLayout = false;

		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_color_picker, null);

		builder.setView(layout);

		builder.setTitle("Pick a Color");
		// setIcon(android.R.drawable.ic_dialog_info);

		LinearLayout landscapeLayout = (LinearLayout) layout.findViewById(R.id.dialog_color_picker_extra_layout_landscape);

		if (landscapeLayout != null) {
			isLandscapeLayout = true;
		}

		mColorPicker = (ColorPickerView) layout.findViewById(R.id.color_picker_view);
		mOldColor = (ColorPanelView) layout.findViewById(R.id.color_panel_old);
		mNewColor = (ColorPanelView) layout.findViewById(R.id.color_panel_new);

		if (!isLandscapeLayout) {
			((LinearLayout) mOldColor.getParent()).setPadding(Math.round(mColorPicker.getDrawingOffset()), 0,
					Math.round(mColorPicker.getDrawingOffset()), 0);

		} else if (landscapeLayout != null) {
			landscapeLayout.setPadding(0, 0, Math.round(mColorPicker.getDrawingOffset()), 0);
			setTitle(null);
		}

		mColorPicker.setOnColorChangedListener(this);

		mOldColor.setColor(color);
		mColorPicker.setColor(color, true);

	}

	@Override
	public void onColorChanged(int color) {
		mNewColor.setColor(color);

		if (mListener != null) {
			mListener.onColorChanged(color);

		}

	}

	public void setAlphaSliderVisible(boolean visible) {
		mColorPicker.setAlphaSliderVisible(visible);
	}

	public int getColor() {
		return mColorPicker.getColor();
	}

}
