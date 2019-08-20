package com.walktour.gui.setting.customevent.msg;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.dinglicom.dataset.EventManager;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.gui.R;
import com.walktour.gui.setting.customevent.BaseCustomEventEditActivity;
import com.walktour.gui.setting.customevent.CustomIcomAdatper;
import com.walktour.gui.setting.customevent.model.CustomEventMsg;
import com.walktour.model.TdL3Model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义信令事件编辑界面
 *
 * @author jianchao.wang
 */
@SuppressLint("InflateParams")
public class CustomEventMsgEditActivity extends BaseCustomEventEditActivity<CustomEventMsg>
        implements OnItemSelectedListener {
    /**
     * 事件管理类
     */
    private EventManager mEventMgr;
    /**
     * 事件定义编辑对话框
     */
    private DialogView mDialog;
    /**
     * 事件定义编辑对话框(无比较)
     */
    private DialogView mDialogNoCompare;
    private String imagePath;//图标路径
    private BasicDialog imagePickerdialog;//图片拾取

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.mEventMgr = EventManager.getInstance();
        super.onCreate(savedInstanceState);
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
            super.mEvent = (CustomEventMsg) super.mFactory.getCustomDefine(eventName);
        } else {
            super.mEvent = new CustomEventMsg();
        }
    }

    @Override
    protected void setDefineDescrtion() {
        if (mTextViewDefine == null)
            return;
        if (mEvent == null) {
            return;
        }
        String msg = "";
        msg += mEventMgr.getCustomL3ById(mEvent.getL3MsgID1()).getL3Msg() + "\n";
        msg += mEventMgr.getCustomL3ById(mEvent.getL3MsgID2()).getL3Msg();
        mTextViewDefine.setText(msg);
    }

    @Override
    protected void saveEditEvent() {
        // 未选信令
        if (mEvent.getL3MsgID1() < 0 && mEvent.getL3MsgID2() < 0) {
            Toast.makeText(mContext, R.string.sys_alarm_toast_msg, Toast.LENGTH_LONG).show();
            showDialog();
            // 未写信令时延
        } else if (mEvent.getInterval() <= 0) {
            Toast.makeText(mContext, R.string.sys_alarm_toast_delay, Toast.LENGTH_LONG).show();
            showDialog();
            // 未输入事件名
        } else if (mEditName.getText().toString().trim().length() == 0) {
            Toast.makeText(mContext, R.string.sys_alarm_toast_name, Toast.LENGTH_LONG).show();
            // 重名
        } else if (!mEditName.getText().toString().trim().equals(mEvent.getName())
                && this.mFactory.hasCustomEvent(mEditName.getText().toString().trim())) {
            Toast.makeText(mContext, R.string.sys_alarm_toast_exist, Toast.LENGTH_LONG).show();
        } else {
            mEvent.setOldName(mEvent.getName());
            mEvent.setName(mEditName.getText().toString().trim());
            mEvent.setShowAlarm(mCheckAlarm.isChecked());
            mEvent.setShowChart(mCheckChart.isChecked());
            mEvent.setShowMap(mCheckMap.isChecked());
            mEvent.setShowTotal(mCheckTotal.isChecked());
            mEvent.setCompare(mCkeckCompare.isChecked());
            if (imagePath != null) {
                mEvent.setIconFilePath(imagePath);
            } else {
                mEvent.setIconFilePath(AppFilePathUtil.getInstance().getSDCardBaseFile("icons", "_custom_event1.png").getAbsolutePath());
            }
            if (isEdit) {
                this.mFactory.editCustomEvent(mEvent);
            } else {
                this.mFactory.addCustomEventMsg(mEvent);
            }
            finish();
        }
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
     * 设置信令事件定义对话框界面
     *
     * @return
     */
    private View genDialogView() {

        if (mDialog == null) {
            mDialog = new DialogView();

            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.alert_dialog_customevent, null);
            mDialog.view = view;
            mDialog.spinnerMsg1 = (BasicSpinner) view.findViewById(R.id.spinner_msg1);
            mDialog.spinnerMsg2 = (BasicSpinner) view.findViewById(R.id.spinner_msg2);
            mDialog.spinnerCom = (BasicSpinner) view.findViewById(R.id.spinner_com);
            mDialog.editDelay = (EditText) view.findViewById(R.id.edit_delay);

            List<String> l3StrList = new ArrayList<String>();
            List<TdL3Model> l3ModelList = mEventMgr.getCustomL3List();
            l3StrList.add("");
            for (TdL3Model model : l3ModelList) {
                l3StrList.add(model.getL3Msg());
            }

            ArrayAdapter<String> adatperMsg = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
                    l3StrList);
            adatperMsg.setDropDownViewResource(R.layout.spinner_dropdown_item);
            mDialog.spinnerMsg1.setAdapter(adatperMsg);
            mDialog.spinnerMsg2.setAdapter(adatperMsg);
            mDialog.spinnerMsg1.setOnItemSelectedListener(this);
            mDialog.spinnerMsg2.setOnItemSelectedListener(this);

            ArrayAdapter<String> adatperCom = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
                    new String[]{">", ">=", "<", "<="});
            adatperCom.setDropDownViewResource(R.layout.spinner_dropdown_item);
            mDialog.spinnerCom.setAdapter(adatperCom);
        }

        setDialogValue();

        return mDialog.view;
    }

    /**
     * 设置信令事件定义对话框界面（无比较）
     *
     * @return
     */
    private View genDialogNoCompareView() {

        if (mDialogNoCompare == null) {
            mDialogNoCompare = new DialogView();

            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.alert_dialog_customevent_no_compare, null);
            mDialogNoCompare.view = view;
            mDialogNoCompare.spinnerMsg1 = (BasicSpinner) view.findViewById(R.id.spinner_msg);

            List<String> l3StrList = new ArrayList<String>();
            List<TdL3Model> l3ModelList = mEventMgr.getCustomL3List();
            l3StrList.add("");
            for (TdL3Model model : l3ModelList) {
                l3StrList.add(model.getL3Msg());
            }
            ArrayAdapter<String> adatperMsg = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
                    l3StrList);
            adatperMsg.setDropDownViewResource(R.layout.spinner_dropdown_item);
            mDialogNoCompare.spinnerMsg1.setAdapter(adatperMsg);
            mDialogNoCompare.spinnerMsg1.setOnItemSelectedListener(this);

        }

        setDialogValueNoCompare();

        return mDialogNoCompare.view;
    }


    /**
     * 设置事件对话框的值
     */
    private void setDialogValue() {
        TdL3Model l3Model1 = mEventMgr.getCustomL3ById(mEvent.getL3MsgID1());
        TdL3Model l3Model2 = mEventMgr.getCustomL3ById(mEvent.getL3MsgID2());
        List<TdL3Model> l3ModelList = mEventMgr.getCustomL3List();
        mDialog.spinnerMsg1.setSelection(0);
        mDialog.spinnerMsg2.setSelection(0);
        for (int i = 0; i < l3ModelList.size(); i++) {
            if (l3ModelList.get(i).getId() == l3Model1.getId()) {
                mDialog.spinnerMsg1.setSelection(i + 1);
                break;
            }
        }
        for (int i = 0; i < l3ModelList.size(); i++) {
            if (l3ModelList.get(i).getId() == l3Model2.getId()) {
                mDialog.spinnerMsg2.setSelection(i + 1);
                break;
            }
        }
        mDialog.editDelay.setText(String.format("%.2f", mEvent.getInterval() / 1000f));
        mDialog.spinnerCom.setSelection(mEvent.getCompare());
    }

    /**
     * 设置事件对话框的值（无比较）
     */
    private void setDialogValueNoCompare() {
        TdL3Model l3Model1 = mEventMgr.getCustomL3ById(mEvent.getL3MsgID1());
        List<TdL3Model> l3ModelList = mEventMgr.getCustomL3List();
        mDialogNoCompare.spinnerMsg1.setSelection(0);
        for (int i = 0; i < l3ModelList.size(); i++) {
            if (l3ModelList.get(i).getId() == l3Model1.getId()) {
                mDialogNoCompare.spinnerMsg1.setSelection(i + 1);
                break;
            }
        }
    }

    @Override
    protected void showDialog() {
        if (mCkeckCompare.isChecked()) {
            View view = genDialogView();
            if (mDialog.dialog == null) {
                mDialog.dialog = new BasicDialog.Builder(mContext).setView(view)
                        .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int position1 = mDialog.spinnerMsg1.getSelectedItemPosition();
                                position1--;
                                int position2 = mDialog.spinnerMsg2.getSelectedItemPosition();
                                position2--;
                                int position3 = mDialog.spinnerCom.getSelectedItemPosition();
                                position3 = position3 == AdapterView.INVALID_POSITION ? 0 : position3;
                                mEvent.setL3MsgID1(0);
                                mEvent.setL3MsgID2(0);
                                if (position1 >= 0) {
                                    mEvent.setL3MsgID1(mEventMgr.getCustomL3List().get(position1).getId());
                                    if (position2 >= 0)
                                        mEvent.setL3MsgID2(mEventMgr.getCustomL3List().get(position2).getId());
                                } else if (position2 >= 0)
                                    mEvent.setL3MsgID1(mEventMgr.getCustomL3List().get(position2).getId());
                                mEvent.setCompare(position3);
                                int delay = 0;
                                try {
                                    delay = (int) (1000 * Float.parseFloat(mDialog.editDelay.getText().toString()));
                                } catch (Exception e) {
                                }
                                mEvent.setInterval(delay);
                                setDefineDescrtion();
                            }

                        }).setNegativeButton(R.string.str_cancle).create();
            }

            mDialog.dialog.setTitle(mEditName.getText().toString());
            mDialog.dialog.show();
        } else {
            View view = genDialogNoCompareView();
            if (mDialogNoCompare.dialog == null) {
                mDialogNoCompare.dialog = new BasicDialog.Builder(mContext).setView(view)
                        .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int position1 = mDialogNoCompare.spinnerMsg1.getSelectedItemPosition();
                                position1--;
                                mEvent.setL3MsgID1(0);
                                mEvent.setL3MsgID2(0);
                                if (position1 >= 0) {
                                    mEvent.setL3MsgID1(mEventMgr.getCustomL3List().get(position1).getId());
                                }
                                setDefineDescrtion();
                            }

                        }).setNegativeButton(R.string.str_cancle).create();
            }

            mDialogNoCompare.dialog.setTitle(mEditName.getText().toString());
            mDialogNoCompare.dialog.show();
        }

    }


    @Override
    protected void showImageViewDialog() {
        showIconDialog();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (view.getId()) {
            case R.id.spinner_msg:

                break;
            case R.id.spinner_msg1:
                if (mDialog.spinnerMsg1.getSelectedItemPosition() <= 0 || mDialog.spinnerMsg2.getSelectedItemPosition() <= 0) {
                    mDialog.spinnerCom.setEnabled(false);
                    mDialog.editDelay.setEnabled(false);
                } else {
                    mDialog.spinnerCom.setEnabled(true);
                    mDialog.editDelay.setEnabled(true);
                }
                break;
        }
    }

    /**
     * 信令事件定义对话框视图
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
         * 信令1
         */
        private BasicSpinner spinnerMsg1;
        /**
         * 信令2
         */
        private BasicSpinner spinnerMsg2;
        /**
         * 信令比较
         */
        private BasicSpinner spinnerCom;
        /**
         * 编辑时延
         */
        private EditText editDelay;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
