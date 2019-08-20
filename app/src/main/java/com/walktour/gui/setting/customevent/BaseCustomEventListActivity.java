package com.walktour.gui.setting.customevent;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.ZipUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.applet.ControlBar;
import com.walktour.gui.setting.customevent.model.CustomEvent;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * 自定义事件管理类
 *
 * @author jianchao.wang
 */
public abstract class BaseCustomEventListActivity<T extends CustomEvent> extends BasicActivity
        implements OnItemClickListener, OnClickListener, OnCheckedChangeListener {
    protected static final int TYPE_MSG = 0;//信令
    protected static final int TYPE_PARAM = 1;//参数
    protected int eventType = TYPE_MSG;//自定义事件类型
    /**
     * 适配类
     */
    protected BaseCustomEventAdapter<T> mAdapter;
    /**
     * 底部工具栏
     */
    private ControlBar mBar;
    /**
     * 新增按钮
     */
    private Button mNewBtn;
    /**
     * 删除切换按钮
     */
    private Button mRemoveBtn;
    /**
     * 删除确定栏
     */
    private LinearLayout mDeleteLayout;
    /**
     * 删除按钮
     */
    private Button mDeleteBtn;
    /**
     * 取消按钮
     */
    private Button mCancleBtn;
    /**
     * 自定义事件工厂类
     */
    protected CustomEventFactory mFactory;
    /**
     * 上下文
     */
    protected Context mContext;
    /**
     * 进度条对话框
     */
    private BasicDialog mDialog = null;
    /**
     * 消息处理句柄
     */
    private MyHandler mHandler = new MyHandler(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = this;
        this.mFactory = CustomEventFactory.getInstance();
        this.mFactory.regeditHandler(mHandler);
        setContentView(R.layout.listview_custom_event_msg);
        findView();
        new IconReaderTask().execute("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mFactory.UnregeditHandler(mHandler);
    }

    private class IconReaderTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            if (mDialog == null) {
                mDialog = new BasicDialog.Builder(mContext).setProgressParam(false, 0, "", true).create();
                mDialog.show();
            }
        }

        @Override
        protected Integer doInBackground(String... params) {
            String iconDir = AppFilePathUtil.getInstance().createSDCardBaseDirectory();
            File fileDir = new File(iconDir);
            if (fileDir.list().length < 30) {
                // 解压文件
                File zipFile = new File(iconDir + File.separator + "icons.zip");
                UtilsMethod.writeRawResource(mContext, R.raw.icons, zipFile);
                try {
                    ZipUtil.unzip(zipFile.getAbsolutePath(), iconDir);
                    zipFile.delete();
                    return fileDir.listFiles().length;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return 0;
        }

        @Override
        public void onPostExecute(Integer result) {
            if (mDialog != null) {
                mDialog.cancel();
            }
        }

    }

    private void findView() {
        ((TextView) this.findViewById(R.id.title_txt)).setText(R.string.sys_alarm_define_list);
        this.findViewById(R.id.pointer).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ListView listView = (ListView) findViewById(R.id.ListView01);
        this.genAdapter();
        mAdapter.setNotifyOnChange(false);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        genToolBar();
    }

    protected abstract void genAdapter();

    /**
     * 生成底部工具栏
     */
    private void genToolBar() {
        mBar = (ControlBar) findViewById(R.id.ControlBar);
        // get button from bar
        mNewBtn = mBar.getButton(0);
        mBar.getButton(1).setVisibility(View.INVISIBLE);
        mBar.getButton(2).setVisibility(View.INVISIBLE);
        mRemoveBtn = mBar.getButton(3);

        // set text
        mNewBtn.setText(R.string.sys_alarm_new);
        mRemoveBtn.setText(R.string.delete);

        // set icon
        mNewBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_new), null,
                null);
        mRemoveBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_clear),
                null, null);

        mDeleteLayout = initLinearLayout(R.id.DeleteBar);
        mDeleteLayout.setVisibility(View.GONE);
        mDeleteBtn = initButton(R.id.ButtonDelete);
        mCancleBtn = initButton(R.id.ButtonCancle);

        mNewBtn.setOnClickListener(this);
        mRemoveBtn.setOnClickListener(this);
        mDeleteBtn.setOnClickListener(this);
        mCancleBtn.setOnClickListener(this);
    }

    /**
     * 消息处理句柄
     *
     * @author jianchao.wang
     */
    @SuppressWarnings("rawtypes")
    private static class MyHandler extends Handler {

        private WeakReference<BaseCustomEventListActivity> reference;

        public MyHandler(BaseCustomEventListActivity activity) {
            this.reference = new WeakReference<BaseCustomEventListActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseCustomEventListActivity activity = this.reference.get();
            switch (msg.what) {
                case CustomEventFactory.MSG_CUSTOM_EVENT_EDIT:
                case CustomEventFactory.MSG_CUSTOM_EVENT_ADD:
                case CustomEventFactory.MSG_CUSTOM_EVENT_DELETE:
                    activity.mAdapter.notifyDataSetChanged();
                    break;
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CustomEvent model = mAdapter.getItem(position);
        this.toEditEvent(model.getName());
    }

    /**
     * 跳转到编辑界面
     *
     * @param eventName
     */
    protected abstract void toEditEvent(String eventName);

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Button01: // 新建
                this.toEditEvent(null);
                break;
            case R.id.Button04:// 删除模式
                setCheckMode(true);
                break;

            case R.id.ButtonDelete:
                showDeleteDialog();
                break;

            case R.id.ButtonCancle:
                setCheckMode(false);
                break;

        }
    }// end onclicklis

    private void showDeleteDialog() {
        new BasicDialog.Builder(mContext).setIcon(android.R.drawable.ic_menu_delete).setTitle(R.string.delete)
                .setMessage(R.string.str_delete_makesure)
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (eventType==TYPE_MSG){
                            mFactory.removeCustomEventMsgs();
                        } else if(eventType==TYPE_PARAM){
                            mFactory.removeCustomEventParams();
                        }
                        setCheckMode(false);
                    }
                }).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setCheckMode(false);
            }
        }).show();
    }

    /**
     * 设置勾选模式
     *
     * @param isCheck
     */
    private void setCheckMode(boolean isCheck) {
        if (isCheck) {
            mDeleteLayout.setVisibility(View.VISIBLE);
            mBar.setVisibility(View.GONE);
            setBtnDelete();
        } else {
            mDeleteLayout.setVisibility(View.GONE);
            mBar.setVisibility(View.VISIBLE);
            CustomEventFactory.getInstance().getRemoveSet().clear();
        }
        mAdapter.setCheckMode(isCheck);
        mAdapter.notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        T itemModel = (T) buttonView.getTag();
        if (isChecked) {
            CustomEventFactory.getInstance().getRemoveSet().add(itemModel);
            Log.e("max", "要删除的itemModel：" + itemModel);
            Log.e("max", "要删除的RemoveSet的长度：" + CustomEventFactory.getInstance().getRemoveSet().size());
        } else {
            CustomEventFactory.getInstance().getRemoveSet().remove(itemModel);
        }
        this.setBtnDelete();
    }

    /**
     * 设置删除按钮
     */
    private void setBtnDelete() {
        mDeleteBtn.setEnabled(mFactory.hasChecked(CustomEvent.TYPE_PARAMS));
        // btnDelete.setTextColor(factory.hasChecked(type) ? Color.WHITE :
        // Color.DKGRAY);
        mDeleteBtn.setTextColor(mFactory.hasChecked(CustomEvent.TYPE_PARAMS)
                ? getResources().getColor(R.color.app_main_text_color) : Color.DKGRAY);
    }

}
