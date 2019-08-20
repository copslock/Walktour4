package com.walktour.gui.data.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.walktour.gui.R;

import java.lang.reflect.Field;

@SuppressLint("InflateParams")
public class PopDialog {

    private PopupWindow pop;
    private View mView = null;
    private Context mContext;
    private int statusBarHeight = 0;
    private int screenWidth = 0;
    private int screenHeight = 0;
    private View mScreenView;
    private TextView tvTitle;
    private ImageView ivBack;
    private View titleView;

    public PopDialog(Context context, View view) {
        this.mContext = context;
        this.mView = view;
        initView();
        getStatusBarHeight();
    }

    private void initView() {
        mScreenView = LayoutInflater.from(mContext).inflate(R.layout.pop_dialog, null);
        if (mView != null) {
            ((LinearLayout) mScreenView.findViewById(R.id.screening_view)).addView(this.mView);
        }
        titleView = mScreenView.findViewById(R.id.title_layout);
        tvTitle = (TextView) mScreenView.findViewById(R.id.title_txt);
        ivBack = (ImageView) mScreenView.findViewById(R.id.pointer);
        ivBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (pop != null && pop.isShowing()) {
                    pop.dismiss();
                }
            }
        });
    }

    public PopDialog setTitle(String title) {
        tvTitle.setText(title);
        return this;
    }

    private void getStatusBarHeight() {

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        Class<?> c = null;

        Object obj = null;

        Field field = null;

        int x = 0;

        try {

            c = Class.forName("com.android.internal.R$dimen");

            obj = c.newInstance();

            field = c.getField("status_bar_height");

            x = Integer.parseInt(field.get(obj).toString());

            statusBarHeight = mContext.getResources().getDimensionPixelSize(x);

        } catch (Exception e1) {


            e1.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public void show() {
        if (pop == null) {
            pop = new PopupWindow(mScreenView, screenWidth, screenHeight - statusBarHeight, true);
            pop.setOutsideTouchable(true);
            pop.setFocusable(true);
            pop.setTouchable(true);
            pop.setBackgroundDrawable(new BitmapDrawable());
            pop.setAnimationStyle(R.style.popwin_anim_style);
            pop.showAtLocation(mScreenView, Gravity.CENTER, 0, statusBarHeight);
        } else {
            if (pop.isShowing()) {
                pop.dismiss();
            } else {
                pop.showAtLocation(mScreenView, Gravity.CENTER, 0, statusBarHeight);
            }
        }
    }

    public PopDialog hideTitleBar() {
        titleView.setVisibility(View.GONE);
        return this;
    }

    @SuppressWarnings("deprecation")
    public void showAsDropDownView(View view) {
        if (pop == null) {
            pop = new PopupWindow(mScreenView, screenWidth, screenHeight - statusBarHeight, true);
            pop.setOutsideTouchable(true);
            pop.setFocusable(true);
            pop.setTouchable(true);
            pop.setBackgroundDrawable(new BitmapDrawable());
            pop.setAnimationStyle(R.style.popwin_anim_style);
            pop.showAsDropDown(view);
        } else {
            if (pop.isShowing()) {
                pop.dismiss();
            } else {
                pop.showAsDropDown(view);
            }
        }
    }

    public void close() {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
        }
    }

    public PopDialog setButtonListener(int buttonId, final ClickListener clickListener, final boolean close) {
        this.mView.findViewById(buttonId).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                clickListener.onClick();
                if (close) close();
            }
        });
        return this;
    }

    public interface ClickListener {
        void onClick();
    }
}
