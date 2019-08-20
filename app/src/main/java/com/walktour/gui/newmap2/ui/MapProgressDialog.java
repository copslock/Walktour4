package com.walktour.gui.newmap2.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.walktour.gui.R;

/**
 * 地图等待
 *
 * @author zhicheng.chen
 * @date 2018/6/25
 */
public class MapProgressDialog extends ProgressDialog {

    private TextView mMsg;

    public MapProgressDialog(Context context) {
        super(context, R.style.activity_dialog);
    }

    public MapProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());
    }

    private void init(Context context) {
        //设置不可取消，点击其他区域不能取消，实际中可以抽出去封装供外包设置
//        setCancelable(false);
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.map_progress_dialog);
        View layout = View.inflate(context, R.layout.map_progress_dialog, null);
        mMsg = (TextView) layout.findViewById(R.id.tv_msg);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }
}
