
package com.walktour.gui.task.activity.scanner.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct.ShowInfoType;
import com.walktour.control.config.PageManager;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.gui.R;
import com.walktour.gui.task.CustomAutoCompleteTextView;
import com.walktour.gui.task.parsedata.model.base.TaskModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 扫频仪任务配置基类，抽象类
 * zhihui.lian
 */
public abstract class BaseScanTaskActivity extends BasicActivity {


    private ApplicationModel appModel = ApplicationModel.getInstance();

    private Boolean disEdit = false;

    private TextView saveTV; //保存按钮

    //private TextView cancleTV; //取消按钮

    protected TaskModel abstModel;
    /**
     * 权限控制页面
     */
    public ArrayList<ShowInfoType> showInfoList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        showInfoList = new PageManager(getApplicationContext(), false).getShowInfoList();
        regeditBroadcast();
    }

    /**
     * 注册广播接收器
     */
    protected void regeditBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WalkMessage.NOTIFY_TESTJOBDONE);
        filter.addAction(WalkMessage.NOTIFY_INTERRUPTJOBDONE);
        this.registerReceiver(mBroadcastReceiver, filter);
    }

    /**
     * 增加公共基类方法，供子类任务调用。主要操作正在时，任务模板保存按钮不可用
     */
    public void initBaseTask() {
        disEdit = appModel.isTestJobIsRun() || (abstModel != null && abstModel.cantEdit());

        saveTV = initTextView(R.id.btn_ok);
        //cancleTV = initTextView(R.id.btn_cencle);
        saveTV.setEnabled(disEdit ? false : true);
        saveTV.setTextColor(disEdit ? getResources().getColor(R.color.gray)
                : getResources().getColor(R.color.app_main_text_color));
        getAllChildViews();
    }

    /**
     * @获取页面的所有控件，判断控件的类型
     */
    public List<View> getAllChildViews() {
        View view = this.getWindow().getDecorView();
        return getAllChildViews(view);
    }

    private List<View> getAllChildViews(View view) {
        List<View> allchildren = new ArrayList<View>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                if (viewchild instanceof EditText) {
                    ((EditText) viewchild).setEnabled(disEdit ? false : true);
                    ((EditText) viewchild).setTextColor(!disEdit ? getResources().getColor(R.color.app_main_text_color)
                            : getResources().getColor(R.color.gray));
                }
                if (viewchild instanceof BasicSpinner) {
                    ((BasicSpinner) viewchild).setEnabled(disEdit ? false : true);
                }
                if (viewchild instanceof Button) {
                    if (((Button) viewchild).getId() == R.id.new_task
                            || ((Button) viewchild).getId() == R.id.reference_task
                            || ((Button) viewchild).getId() == R.id.btn_view
                            || ((Button) viewchild).getId() == R.id.upload_btn_view) {
                        ((Button) viewchild).setEnabled(disEdit ? false : true);
                        ((Button) viewchild).setTextColor(disEdit ? getResources().getColor(R.color.gray)
                                : getResources().getColor(R.color.app_main_text_color));
                    }
                }
                if (viewchild instanceof ListView) {
                    if (((ListView) viewchild).getId() == R.id.multirab_listview) {
                        ((ListView) viewchild).invalidateViews();
                    }
                }

                if (viewchild instanceof CheckBox) {
                    ((CheckBox) viewchild).setClickable(disEdit ? false : true);
                }
                if (viewchild instanceof CustomAutoCompleteTextView) {
                    ((CustomAutoCompleteTextView) viewchild).setEnabled(disEdit ? false : true);
                }

                allchildren.add(viewchild);
                allchildren.addAll(getAllChildViews(viewchild));
            }
        }
        return allchildren;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBaseTask();
    }

    /**
     * 抽象方法  <BR>
     * 所有子类必须实现该方法
     */
    public abstract void saveTestTask();

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     *
     * @param keyCode
     * @param event
     * @return
     * @see com.walktour.framework.ui.BasicActivity#onKeyDown(int, android.view.KeyEvent)
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (disEdit) {
                BaseScanTaskActivity.this.finish();
                return true;
            }
            BasicDialog.Builder builder = new BasicDialog.Builder(this);
            builder.setTitle(R.string.task_dialog_title);
            builder.setMessage(R.string.task_dialog_content);
            builder.setPositiveButton(R.string.yes, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveTestTask();
                }
            });
            builder.setNegativeButton(R.string.no, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    BaseScanTaskActivity.this.finish();
                }
            });
            builder.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     *
     * @param v
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                saveTestTask();
                break;
            case R.id.btn_cencle:
            case R.id.pointer:
                this.finish();
                break;
            case R.id.advanced_arrow_rel:
                LinearLayout advancedLayout = initLinearLayout(R.id.task_advanced_layout);
                ImageView iv = initImageView(R.id.advanced_arrow);
                if (advancedLayout.getVisibility() == View.GONE) {
                    advancedLayout.setVisibility(View.VISIBLE);
                    iv.setBackgroundResource(R.drawable.expander_ic_minimized_black);

                    showAnimation(iv);
                } else {
                    advancedLayout.setVisibility(View.GONE);
                    iv.setBackgroundResource(R.drawable.expander_ic_maximized_black);
                    showAnimation(iv);
                }

                break;
            default:
                break;
        }
    }

    public void showAnimation(View mView) {
        final float centerX = mView.getWidth() / 2.0f;
        final float centerY = mView.getHeight() / 2.0f;
        RotateAnimation rotateAnimation = new RotateAnimation(0, 180, centerX, centerY);
        rotateAnimation.setDuration(300);
        rotateAnimation.setFillAfter(true);
        mView.startAnimation(rotateAnimation);
    }


    public Boolean getIsJobRun() {
        return disEdit;
    }

    public void setIsJobRun(Boolean isJobRun) {
        this.disEdit = isJobRun;
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_in_down);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * 广播接收器:接收来广播更新界面
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WalkMessage.NOTIFY_TESTJOBDONE)
                    || intent.getAction()
                    .equals(WalkMessage.NOTIFY_INTERRUPTJOBDONE)) {
                initBaseTask();
            }
        }

    };

}
