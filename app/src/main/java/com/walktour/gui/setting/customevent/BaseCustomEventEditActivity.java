package com.walktour.gui.setting.customevent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.setting.customevent.model.CustomEvent;

/**
 * 自定义事件编辑界面抽象类
 *
 * @author jianchao.wang
 */
@SuppressLint("InflateParams")
public abstract class BaseCustomEventEditActivity<T extends CustomEvent> extends BasicActivity
        implements OnClickListener {
    /**
     * 上下文
     */
    protected Context mContext = null;
    /**
     * 编辑事件对象
     */
    protected T mEvent;
    /**
     * 对象保存工厂类
     */
    protected CustomEventFactory mFactory;
    /**
     * 当前是否编辑
     */
    protected boolean isEdit = false;
    /**
     * 事件定义框架
     */
    private RelativeLayout mDefineLayout;
    /**
     * 事件描述
     */
    protected EditText mEditName;
    /**
     * 事件定义
     */
    protected TextView mTextViewDefine;
    /**
     * 是否告警
     */
    protected CheckBox mCheckAlarm;
    /**
     * 是否显示在地图
     */
    protected CheckBox mCheckMap;
    /**
     * 是否显示在报表
     */
    protected CheckBox mCheckChart;
    /**
     * 是否显示在统计
     */
    protected CheckBox mCheckTotal;
    /**
     * 是否2信令比较
     */
    protected CheckBox mCkeckCompare;
    protected ImageView mIcon;
    protected RelativeLayout mDefineIconLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = this;
        this.mFactory = CustomEventFactory.getInstance();
        getBundleModel();
        findView();
    }

    /**
     * 获取绑定的对象
     */
    protected abstract void getBundleModel();

    /**
     * 查找视图
     */
    private void findView() {
        setContentView(R.layout.sys_customevent);
        ((TextView) this.findViewById(R.id.title_txt)).setText(R.string.sys_alarm_define);
        this.findViewById(R.id.pointer).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button btn_ok = (Button) this.findViewById(R.id.btn_ok);
        Button btn_cencle = (Button) this.findViewById(R.id.btn_cencle);
        mDefineLayout = (RelativeLayout) this.findViewById(R.id.setting_alarm_define);
        mEditName = (EditText) this.findViewById(R.id.edit_name);
        mTextViewDefine = (TextView) this.findViewById(R.id.textView_define);
        mCheckAlarm = (CheckBox) this.findViewById(R.id.check_alarm);
        mCheckMap = (CheckBox) this.findViewById(R.id.check_alarm_map);
        mCheckChart = (CheckBox) this.findViewById(R.id.check_alarm_chart);
        mCheckTotal = (CheckBox) this.findViewById(R.id.check_alarm_total);
        mCkeckCompare = (CheckBox) this.findViewById(R.id.check_signaling_compare);
        mIcon = (ImageView) findViewById(R.id.iv_define_icon);
        mDefineIconLayout = (RelativeLayout) findViewById(R.id.setting_icon);

        // set listener
        btn_ok.setOnClickListener(this);
        btn_cencle.setOnClickListener(this);
        mDefineLayout.setOnClickListener(this);
        mDefineIconLayout.setOnClickListener(this);


//        mEditName.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                CharSequence temp = s.subSequence(start, start + count);
//                if (StringUtil.isChineseChar(temp.toString())) {
//                    mEditName.setText(s.subSequence(0, start).toString());
//                    mEditName.setSelection(start);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//
//        });
// set value
        if (mEvent!=null){
            setDefineDescrtion();
            mEditName.setText(mEvent.getName());
            mCheckAlarm.setChecked(mEvent.isShowAlarm());
            mCheckMap.setChecked(mEvent.isShowMap());
            mCheckChart.setChecked(mEvent.isShowChart());
            mCheckTotal.setChecked(mEvent.isShowTotal());
            mCkeckCompare.setChecked(mEvent.isCompare());
            mIcon.setImageDrawable(Drawable.createFromPath(mEvent.getIconFilePath()));
        }
    }

    /**
     * 设置事件的描述显示
     */
    protected abstract void setDefineDescrtion();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_alarm_define:
                showDialog();
                break;

            case R.id.btn_ok:
                this.saveEditEvent();
                break;

            case R.id.btn_cencle:
                finish();
                break;
            case R.id.setting_icon:
                showImageViewDialog();
                break;

        }
    }

    /**
     * 保存编辑的事件
     */
    protected abstract void saveEditEvent();

    /**
     * 显示事件定义编辑对话框
     */
    protected abstract void showDialog();
    /**
     * 选择图片
     */
    protected abstract void showImageViewDialog();

}
