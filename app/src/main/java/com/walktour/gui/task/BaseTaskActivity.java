/*
 * 文件名: BaseTaskActivity.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2012-6-15
 *
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.gui.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct.ShowInfoType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.PageManager;
import com.walktour.control.config.ServerManager;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.model.base.TaskModel;

import java.util.ArrayList;
import java.util.List;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 *
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-6-15]
 */
public abstract class BaseTaskActivity extends BasicActivity implements
        android.view.View.OnClickListener {

    /**
     * 把公共参数抽取到基础任务Activity
     */
    private String rabTag;

    private String multiRabName;

    private String rabTaskName;

    private String modifyBefRabName;

    private int rabDelayTimeType;

    private ApplicationModel appModel = ApplicationModel.getInstance();

    private Boolean disEdit = false;

    protected TaskModel abstModel;
    /**
     * 权限控制页面
     */
    public ArrayList<ShowInfoType> showInfoList;

    public final static String RABTAG = "RABTAG";            //并发标志

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        showInfoList = new PageManager(getApplicationContext(), false).getShowInfoList();
        regeditBroadcast();
        Intent intent = getIntent();
        rabTag = (intent == null ? "" : intent.getStringExtra("RAB")); // 传递的并发标记
        multiRabName = (intent == null ? "" : intent.getStringExtra("multiRabName")); // 传递当前并发的名字
        rabTaskName = (intent == null ? "" : intent.getStringExtra("RabTaskName"));// 并发列表单个任务名字
        modifyBefRabName = (intent == null ? "" : intent.getStringExtra("ModifyBefRabName"));// 修改前任务列表名字
        rabDelayTimeType = (intent == null ? 0 : intent.getIntExtra("RabDelayTimeType", 0)); // 并发传递启动方式类型
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
        disEdit = appModel.isTestJobIsRun() || (abstModel != null && abstModel.cantEdit()) || ServerManager.getInstance(this).getDTLogCVersion() != 0;
        getAllChildViews();
    }

    /**
     * @获取页面的所有控件，判断控件的类型 zhihui.lian
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
                    if (((EditText) viewchild).getId() == R.id.edit_ping_repeat) {
                        if (appModel.isTestJobIsRun()) {
                            ((EditText) viewchild).setEnabled(false);
                        }
                    } else {
                        ((EditText) viewchild).setEnabled(disEdit ? false : true);
                    }
                }
                if (viewchild instanceof BasicSpinner) {
                    BasicSpinner basicSpinner = ((BasicSpinner) viewchild);
                    basicSpinner.setEnabled(disEdit ? false : true);
                    if (!basicSpinner.isEnabled()) {
                        basicSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> arg0,
                                                       View arg1, int arg2, long arg3) {
                                ((TextView) arg1).setTextColor(disEdit ? getResources().getColor(R.color.app_click_disable_grey_color) :
                                        getResources().getColor(R.color.app_main_text_color));
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {
                            }
                        });
                    }
                }

                if (viewchild instanceof Button) {
                    if (((Button) viewchild).getId() == R.id.new_task
                            || ((Button) viewchild).getId() == R.id.reference_task
                            || ((Button) viewchild).getId() == R.id.btn_view
                            || ((Button) viewchild).getId() == R.id.upload_btn_view
                            || ((Button) viewchild).getId() == R.id.wifitestssid
                            || ((Button) viewchild).getId() == R.id.btn_ok) {
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
                if ((viewchild instanceof TextView) && !(viewchild instanceof Button)) {
                    ((TextView) viewchild).setClickable(disEdit ? false : true);
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
                BaseTaskActivity.this.finish();
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

                    BaseTaskActivity.this.finish();
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

    public String getMultiRabName() {
        return multiRabName;
    }

    public void setMultiRabName(String multiRabName) {
        this.multiRabName = multiRabName;
    }

    public String getRabTag() {
        return rabTag;
    }

    public void setRabTag(String rabTag) {
        this.rabTag = rabTag;
    }

    public String getRabTaskName() {
        return rabTaskName;
    }

    public void setRabTaskName(String rabTaskName) {
        this.rabTaskName = rabTaskName;
    }

    public String getModifyBefRabName() {
        return modifyBefRabName;
    }

    public void setModifyBefRabName(String modifyBefRabName) {
        this.modifyBefRabName = modifyBefRabName;
    }

    public Boolean getIsJobRun() {
        return disEdit;
    }

    public void setIsJobRun(Boolean isJobRun) {
        this.disEdit = isJobRun;
    }


    public int getRabDelayTimeType() {
        return rabDelayTimeType;
    }


    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     *
     * @see com.walktour.framework.ui.BasicActivity#finish()
     */

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
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

    public EditText rabRelTimeEdt;

    public EditText rabAblTimeEdt;

    /**
     * 设置并发相对时间或绝对时间 ,招标用
     *
     * @param dataConnectTypeSP
     */
    public void setRabTime(RelativeLayout rabTimeRel, RelativeLayout rabTimeRule) {
        rabRelTimeEdt = initEditText(R.id.rab_time_edt);
        rabAblTimeEdt = initEditText(R.id.rab_time_rel_edt);

        if (RABTAG.equals(getRabTag())) {
            if (getRabDelayTimeType() == 1) {
                rabTimeRel.setVisibility(View.VISIBLE);
                TextView rabTimeTxt = initTextView(R.id.rab_time_txt);
                rabTimeTxt.setText(getResources().getString(R.string.rab_time_ral_str));
            }
            if (getRabDelayTimeType() == 2) {
                rabTimeRule.setVisibility(View.VISIBLE);
                TextView rabRuleTimeTxt = initTextView(R.id.rab_time_rel_txt);
                rabRuleTimeTxt.setText(getResources().getString(R.string.rab_time_rule_str));
            }
        } else {
            rabTimeRel.setVisibility(View.GONE);
            rabTimeRule.setVisibility(View.GONE);
        }

    }

    /**
     * 业务模版中存储的为下拉列表的明文信息
     * 根所该字符串信息返回下拉列表数组序号
     *
     * @param arrayId  arrays数组列表ID
     * @param modelStr model中下拉明文字符串
     * @return
     */
    protected int getSpinnerIndexByModelStr(int arrayId, String modelStr) {
        int arrayIndex = 0;
        try {
            String[] arrays = getResources().getStringArray(arrayId);
            if (arrays != null && modelStr != null) {
                for (int i = 0; i < arrays.length; i++) {
                    if (arrays[i].equals(modelStr)) {
                        arrayIndex = i;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.w("BaseTaskActivity", "getSpinnerIndexByModelStr", e);
        }
        return arrayIndex;
    }


    private boolean mAutoHideKeyboard = true;

    /**
     * 设置是否启用点击关闭键盘
     *
     * @param autoHide
     */
    public void setAutoHideKeyboard(boolean autoHide) {
        mAutoHideKeyboard = autoHide;
    }

    /**
     * 点击EditText外的地方就收起软键盘
     */
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mAutoHideKeyboard) {
            return super.dispatchTouchEvent(ev);
        }
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    /**
     * 判断edittext的位置
     *
     * @param v
     * @param event
     * @return
     */
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            // 获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public String[] getArrary(int arrayId){
        return getResources().getStringArray(arrayId);
    }


    /**
     * 简化 Spinner 回调接口，省去 onNothingSelected(AdapterView<?> parent) 方法
     * @author zhicheng.chen
     * @date 2019/4/22
     */
    public abstract class SimpleItemSelectedListener implements OnItemSelectedListener{

        @Override
        public abstract void onItemSelected(AdapterView<?> parent, View view, int position, long id);

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // todo do nothing
        }
    }

}
