package com.walktour.framework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.ShowInfo;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.gui.R;
import com.walktour.model.YwDataModel;

public class CustomDashboardDataView extends LinearLayout implements RefreshEventManager.RefreshEventListener {
    private static final String TAG = "CustomDashboardDataView";
    private LayoutInflater layoutInflater;
    private Context context;
    View rootView;
    /**
     * 并发时根据name 获取值
     */
    private String name = "";
    private String leftTitle;
    private String rightTitle;

    public CustomDashboardDataView(Context context) {
        this(context, null);

    }

    public CustomDashboardDataView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        rootView = layoutInflater.inflate(R.layout.layout_custom_dashboard_data_view, null);
        addView(rootView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        updateData();
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.destroyDrawingCache();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus){
            RefreshEventManager.addRefreshListener(this);
        }else {
            RefreshEventManager.removeRefreshListener(this);
        }
    }

    private void updateData() {
        YwDataModel ywDataModel = ShowInfo.getInstance().getYwDataModel(name);
//        LogUtil.d(TAG,"ywDataModel:"+ywDataModel);
        if (ywDataModel == null) {
            return;
        }
        leftTitle = ywDataModel.getBordLeftTitle();//平均速率@15007.63kbps
        rightTitle = ""+TraceInfoInterface.currentNetType.getNetTypeName();
        TextView tvSpped = (TextView) rootView.findViewById(R.id.speed);
        TextView tvNetword = (TextView) rootView.findViewById(R.id.tv_network);
        if (rightTitle == null || "".equals(rightTitle)) {
            tvNetword.setText("");
        } else {
            tvNetword.setText("" + rightTitle);
            tvSpped.setText("Mean Thr:  " + leftTitle);
        }
    }

    @Override
    public void onRefreshed(RefreshEventManager.RefreshType refreshType, Object object) {
//        if (ApplicationModel.getInstance().isTestJobIsRun() || ApplicationModel.getInstance().isTesting()) {
            switch (refreshType) {
                case ACTION_WALKTOUR_TIMER_CHANGED:
                    if (!ApplicationModel.getInstance().isFreezeScreen()) {
                        updateData();
                    }
                    break;
                default:
                    break;
            }
//        }
    }
}
