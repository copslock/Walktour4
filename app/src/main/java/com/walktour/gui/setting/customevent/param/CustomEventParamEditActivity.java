package com.walktour.gui.setting.customevent.param;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.walktour.base.util.AppFilePathUtil;
import com.walktour.control.config.ParameterSetting;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.gui.R;
import com.walktour.gui.setting.customevent.BaseCustomEventEditActivity;
import com.walktour.gui.setting.customevent.CustomIcomAdatper;
import com.walktour.gui.setting.customevent.model.CustomEventParam;
import com.walktour.gui.setting.customevent.model.Param;
import com.walktour.model.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

/**
 * 自定义参数事件编辑界面
 *
 * @author jianchao.wang
 */
@SuppressLint("InflateParams")
public class CustomEventParamEditActivity extends BaseCustomEventEditActivity<CustomEventParam> {
    /**
     * 参数列表
     */
    private ArrayList<Parameter> mParamList;
    /**
     * 事件定义编辑对话框
     */
    private DialogView mDialog;
    private String imagePath;//图标路径
    private BasicDialog imagePickerdialog;//图片拾取

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.rl_signaling_compare).setVisibility(View.GONE);
        mParamList = ParameterSetting.getInstance().getParametersFirstNull(mContext);
    }

    @Override
    protected void getBundleModel() {
        Bundle bundle = getIntent().getExtras();
        String eventName = null;
        if (bundle != null) {
            eventName = bundle.getString("name");
            isEdit = eventName != null;
        }

        if (isEdit) {
            super.mEvent = (CustomEventParam) this.mFactory.getCustomDefine(eventName);
        } else {
            super.mEvent = new CustomEventParam();
        }
    }

    @Override
    protected void setDefineDescrtion() {
        if (mTextViewDefine == null)
            return;
        String msg = "";
        if (mEvent.getParams() != null) {
            Param[] params = mEvent.getParams();
            for (Param param : params) {
                if (param != null) {
                    String name = ParameterSetting.getInstance().getParamShortName(param.id);
                    msg += String.format(Locale.getDefault(), "%s%s%.2f\n", name, param.getComapreStr(),
                            name.toLowerCase(Locale.getDefault()).endsWith("(k)") ? param.value / 1000 : param.value);
                }
            }

            // msg += eventModel.getDuration() +"(S)";
        }
        mTextViewDefine.setText(msg);
    }

    @Override
    protected void saveEditEvent() {
        // 未配置参数时延
        if (super.mEvent.getDuration() <= 0) {
            Toast.makeText(mContext, R.string.sys_alarm_toast_delay, Toast.LENGTH_LONG).show();
            showDialog();
            // 未配置参数
        } else if (super.mEvent.getParams()[0] == null && super.mEvent.getParams()[1] == null
                && super.mEvent.getParams()[2] == null) {
            Toast.makeText(mContext, R.string.sys_alarm_toast_oneParam, Toast.LENGTH_LONG).show();
            showDialog();
            // 未输入事件名
        } else if (mEditName.getText().toString().trim().length() == 0) {
            Toast.makeText(mContext, R.string.sys_alarm_toast_name, Toast.LENGTH_LONG).show();
            // 重名
        } else if (!mEditName.getText().toString().trim().equals(super.mEvent.getName())
                && this.mFactory.hasCustomEvent(mEditName.getText().toString().trim())) {
            Toast.makeText(mContext, R.string.sys_alarm_toast_exist, Toast.LENGTH_LONG).show();
        } else {
            super.mEvent.setOldName(super.mEvent.getName());
            super.mEvent.setName(mEditName.getText().toString().trim());
            super.mEvent.setShowAlarm(mCheckAlarm.isChecked());
            super.mEvent.setShowChart(mCheckChart.isChecked());
            super.mEvent.setShowMap(mCheckMap.isChecked());
            super.mEvent.setShowTotal(mCheckTotal.isChecked());
            super.mEvent.setCompare(mCkeckCompare.isChecked());
            if (imagePath != null) {
                super.mEvent.setIconFilePath(imagePath);
            } else {
                super.mEvent.setIconFilePath(AppFilePathUtil.getInstance().getSDCardBaseFile("icons", "_custom_event1.png").getAbsolutePath());
            }

            if (isEdit) {
                this.mFactory.editCustomEvent(super.mEvent);
            } else {
                this.mFactory.addCustomEventParams(super.mEvent);
            }
            finish();
        }
    }

    /**
     * 设置参数事件定义对话框界面
     *
     * @return
     */
    private View genDialogView() {
        if (mDialog == null) {
            mDialog = new DialogView();

            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.alert_dialog_customevent_param, null);
            mDialog.view = view;
            mDialog.paramSet[0] = (BasicSpinner) view.findViewById(R.id.spinner_param1);
            mDialog.paramSet[1] = (BasicSpinner) view.findViewById(R.id.spinner_param2);
            mDialog.paramSet[2] = (BasicSpinner) view.findViewById(R.id.spinner_param3);
            mDialog.conditionSet[0] = (BasicSpinner) view.findViewById(R.id.spinner_condition1);
            mDialog.conditionSet[1] = (BasicSpinner) view.findViewById(R.id.spinner_condition2);
            mDialog.conditionSet[2] = (BasicSpinner) view.findViewById(R.id.spinner_condition3);
            mDialog.valueSet[0] = (EditText) view.findViewById(R.id.edit_value1);
            mDialog.valueSet[1] = (EditText) view.findViewById(R.id.edit_value2);
            mDialog.valueSet[2] = (EditText) view.findViewById(R.id.edit_value3);
            mDialog.duration = (EditText) view.findViewById(R.id.edit_duration);

            // 参数
            ArrayAdapter<Parameter> adapterParam = new ArrayAdapter<Parameter>(this, R.layout.simple_spinner_custom_layout,
                    mParamList);
            adapterParam.setDropDownViewResource(R.layout.spinner_dropdown_item);
            for (int i = 0; i < mDialog.paramSet.length; i++) {
                mDialog.paramSet[i].setAdapter(adapterParam);
            }

            // 条件
            ArrayAdapter<String> adatperCom = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
                    new String[]{">", ">=", "<", "<="});
            adatperCom.setDropDownViewResource(R.layout.spinner_dropdown_item);
            for (int i = 0; i < mDialog.conditionSet.length; i++) {
                mDialog.conditionSet[i].setAdapter(adatperCom);
            }
        }

        setDialogValue();

        return mDialog.view;
    }

    /**
     * 设置参数事件编辑值
     */
    private void setDialogValue() {
        if (isEdit) {
            Param[] pArray = super.mEvent.getParams();
            if (pArray != null) {

                for (int j = 0; j < pArray.length; j++) {
                    Param param = pArray[j];
                    if (param != null) {

                        ParameterSetting setting = ParameterSetting.getInstance();
                        // 设置参数选择
                        for (int i = 0; i < mParamList.size(); i++) {
                            Parameter p = mParamList.get(i);

                            // 以(k)结尾的要除以1000
                            String name = setting.getParamShortName(param.id);
                            String value = String.format(Locale.getDefault(), "%.2f",
                                    name.toLowerCase(Locale.getDefault()).endsWith("(k)") ? param.value / 1000 : param.value);

                            if (j < mDialog.paramSet.length && p.getId().equals(param.id)) {
                                mDialog.paramSet[j].setSelection(i);
                                mDialog.conditionSet[j].setSelection(param.compare);
                                mDialog.valueSet[j].setText(value);
                            }
                        }

                    }
                }

                // 持续时间
                mDialog.duration.setText(String.valueOf(super.mEvent.getDuration()));
            }
        }
    }

    @Override
    protected void showDialog() {

        View view = genDialogView();

        if (mDialog.dialog == null) {
            mDialog.dialog = new BasicDialog.Builder(mContext).setView(view)
                    .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int[] paramIndexSet = new int[mDialog.paramSet.length];
                            int[] conditionSet = new int[paramIndexSet.length];
                            float[] valueSet = new float[paramIndexSet.length];
                            for (int i = 0; i < paramIndexSet.length; i++) {
                                paramIndexSet[i] = mDialog.paramSet[i].getSelectedItemPosition();
                                conditionSet[i] = mDialog.conditionSet[i].getSelectedItemPosition();
                                try {
                                    valueSet[i] = Float.parseFloat(mDialog.valueSet[i].getText().toString());
                                } catch (Exception e) {
                                    valueSet[i] = 0;
                                }
                            }

                            Param[] params = new Param[paramIndexSet.length];

                            ParameterSetting setting = ParameterSetting.getInstance();
                            mDialog.isDismiss = true;
                            for (int i = 0; i < paramIndexSet.length; i++) {
                                if (paramIndexSet[i] <= 0)
                                    continue;
                                if (mDialog.valueSet[i].getText().toString().length() == 0) {
                                    String msg = String.format(getString(R.string.sys_alarm_toast_param), 1);
                                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                                    mDialog.isDismiss = false;
                                    return;
                                } else {
                                    String paramId = mParamList.get(paramIndexSet[i]).getId();
                                    // 以(k)结尾的要乘以1000
                                    String name = setting.getParamShortName(paramId);
                                    valueSet[i] = name.toLowerCase(Locale.getDefault()).endsWith("(k)") ? valueSet[i] * 1000
                                            : valueSet[i];
                                    params[i] = new Param(paramId, conditionSet[i], valueSet[i]);
                                }
                            }
                            int count = 0;
                            for (int i = 0; i < params.length; i++) {
                                if (params[i] == null)
                                    count++;
                            }
                            if (params.length == count) {
                                Toast.makeText(mContext, R.string.sys_alarm_toast_oneParam, Toast.LENGTH_LONG).show();
                                mDialog.isDismiss = false;
                                return;
                            }

                            if (mDialog.duration.getText().toString().length() == 0) {
                                Toast.makeText(mContext, R.string.sys_alarm_toast_duration, Toast.LENGTH_LONG).show();
                                mDialog.isDismiss = false;
                                return;
                            }

                            mEvent.setParams(params);

                            mEvent.setDuration(Integer.parseInt(mDialog.duration.getText().toString()));

                            setDefineDescrtion();
                        }
                    }).setNegativeButton(R.string.str_cancle, null, mDialog.isDismiss).create();
        }

        mDialog.dialog.setTitle(mEditName.getText().toString());
        mDialog.dialog.show();
    }

    @Override
    protected void showImageViewDialog() {
        showIconDialog();
    }

    /**
     * 显示图标选择窗口 showIconDialog 函数功能：
     */
    private void showIconDialog() {
        if (imagePickerdialog == null) {
            String iconDir = AppFilePathUtil.getInstance().getSDCardBaseDirectory("icons");
            GridView gridView = (GridView) LayoutInflater.from(mContext).inflate(R.layout.gridview_custom_event, null);

            final CustomIcomAdatper gridAdatper = new CustomIcomAdatper(mContext, new File(iconDir));
            gridView.setAdapter(gridAdatper);

            imagePickerdialog = new BasicDialog.Builder(mContext).setView(gridView).setTitle(getString(R.string.choose_icon))
                    .create();
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    imagePath = gridAdatper.getItem(position).getAbsolutePath();
                    mIcon.setImageDrawable(Drawable.createFromPath(imagePath));
                    imagePickerdialog.dismiss();
                }
            });
        }
        imagePickerdialog.show();
    }

    /**
     * 参数事件定义对话框视图
     *
     * @author jianchao.wang
     */
    private class DialogView {
        /**
         * 对话框
         */
        private BasicDialog dialog;
        /**
         * 视图
         */
        private View view;
        /**
         * 参数选择框集合
         */
        private BasicSpinner[] paramSet = new BasicSpinner[3];
        /**
         * 参数条件选择框集合
         */
        private BasicSpinner[] conditionSet = new BasicSpinner[3];
        /**
         * 参数值编辑框集合
         */
        private EditText[] valueSet = new EditText[3];
        /**
         * 持续时间编辑框
         */
        private EditText duration;
        /**
         * 是否关闭对话框
         */
        private boolean isDismiss = true;
    }

}
