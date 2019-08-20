package com.walktour.base.gui.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.walktour.base.R;
import com.walktour.base.R2;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;


/***
 * 底部工具栏，包含7个Button,默认7个Button为View.GONE
 *
 */
public class BaseBottomControlBar extends FrameLayout {
    /**
     * 按钮图标显示位置
     */
    public static enum IconPos {
        Left, Right, Top, Bottom
    }

   /**
     * 按钮行
     */
    private LinearLayout controlBar;
    /**
     * 按钮数组
     */
    @BindViews({R2.id.control_bar_button01, R2.id.control_bar_button02, R2.id.control_bar_button03, R2.id.control_bar_button04, R2.id.control_bar_button05, R2.id.control_bar_button06, R2.id.control_bar_button07})
    List<Button> mButtons;
    /**
     * 按钮分割线数组
     */
    @BindViews({R2.id.control_bar_divider01, R2.id.control_bar_divider02, R2.id.control_bar_divider03, R2.id.control_bar_divider04, R2.id.control_bar_divider05, R2.id.control_bar_divider06, R2.id.control_bar_divider07})
    List<View> mDividers;

    public BaseBottomControlBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_control_bar, null);
        this.addView(view);
        ButterKnife.bind(this, view);
    }

    /**
     * 获取指定位置的按钮
     *
     * @param position 指定位置
     * @return 按钮
     */
    private Button getButton(int position) {
        if (position < 0 || position > this.mButtons.size() - 1) {
            return null;
        } else {
            return mButtons.get(position);
        }
    }

    /**
     * 获取指定位置的按钮
     *
     * @param position 指定位置
     * @return 按钮
     */
    private View getDivider(int position) {
        if (position < 0 || position > this.mDividers.size() - 1) {
            return null;
        } else {
            return mDividers.get(position);
        }
    }

    /**
     * 设置按钮图片、文本、点击监听类
     *
     * @param position 按钮位置
     * @param iconId   图片资源ID
     * @param iconPos  图片显示位置
     * @param textId   文本资源ID
     * @param listener 点击监听类
     */
    public void setButton(int position, int iconId, IconPos iconPos, int textId, OnClickListener listener) {
        this.setButtonIcon(position, iconId, iconPos);
        this.setButtonText(position, textId);
        this.setButtonListener(position, listener);
    }

    /**
     * 设置按钮文本、点击监听类
     *
     * @param position 按钮位置
     * @param textId   文本资源ID
     * @param listener 点击监听类
     */
    public void setButton(int position, int textId, OnClickListener listener) {
        this.setButtonText(position, textId);
        this.setButtonListener(position, listener);
    }

    /**
     * 设置按钮图片
     *
     * @param position 按钮位置
     * @param iconId   图片资源ID
     * @param iconPos  图片显示位置
     */
    public void setButtonIcon(int position, int iconId, IconPos iconPos) {
        Button button = this.getButton(position);
        this.showButton(position);
        if (button != null) {
            switch (iconPos) {
                case Left:
                    button.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(iconId), null, null, null);
                    break;
                case Right:
                    button.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(iconId), null);
                    break;
                case Top:
                    button.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(iconId), null, null);
                    break;
                case Bottom:
                    button.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(iconId));
                    break;
            }
        }
    }

    /**
     * 设置按钮文本
     *
     * @param position 按钮位置
     * @param textId   按钮文本资源ID
     */
    public void setButtonText(int position, int textId) {
        Button button = this.getButton(position);
        this.showButton(position);
        if (button != null) {
            button.setText(textId);
        }
    }

    public void setButtonText(int position, int textId, float size) {
        Button button = this.getButton(position);
        this.showButton(position);
        if (button != null) {
            button.setText(textId);
            button.setTextSize(size);
        }
    }

    /**
     * 设置按钮文本颜色
     *
     * @param position 按钮位置
     * @param colorId  按钮文本颜色资源ID
     */
    public void setButtonTextColor(int position, int colorId) {
        Button button = this.getButton(position);
        this.showButton(position);
        if (button != null) {
            button.setTextColor(colorId);
        }
    }

    /**
     * 显示指定按钮
     *
     * @param position 按钮位置
     */
    public void showButton(int position) {
        Button button = this.getButton(position);
        if (button != null) {
            button.setVisibility(VISIBLE);
            this.getDivider(position).setVisibility(VISIBLE);
        }
    }

    /**
     * 隐藏指定按钮
     *
     * @param position 按钮位置
     */
    public void hiddenButton(int position) {
        Button button = this.getButton(position);
        if (button != null) {
            button.setVisibility(GONE);
            this.getDivider(position).setVisibility(GONE);
        }
    }

    /**
     * 设置按钮监听类
     *
     * @param position 按钮位置
     * @param listener 按钮监听类
     */
    public void setButtonListener(int position, OnClickListener listener) {
        Button button = this.getButton(position);
        if (button != null) {
            button.setOnClickListener(listener);
        }
    }

}