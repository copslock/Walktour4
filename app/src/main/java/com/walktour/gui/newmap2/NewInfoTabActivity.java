package com.walktour.gui.newmap2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;

import com.dingli.wlan.apscan.WifiScanner;
import com.dinglicom.dataset.EventManager;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.ImageUtil;
import com.walktour.Utils.TabHostUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhone;
import com.walktour.control.config.ServerManager;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicTabActivity;
import com.walktour.gui.InfoTabHost;
import com.walktour.gui.R;
import com.walktour.gui.analysis.csfb.CsfbPieChartActivity;
import com.walktour.gui.eventbus.OnEventMenuSelectedEvent;
import com.walktour.gui.eventbus.OnL3MsgMenuSelectedEvent;
import com.walktour.gui.map.AlarmMsg;
import com.walktour.gui.map.InfoActivity2;
import com.walktour.gui.map.MapTabActivity;
import com.walktour.gui.map.ParamActivity2;
import com.walktour.gui.newmap2.activity.TagPhotoActivity;
import com.walktour.gui.newmap2.sdk.GaoDeNaviMapSdk;
import com.walktour.gui.newmap2.util.AudioUtil;
import com.walktour.gui.replayfloatview.FloatWindowManager;
import com.walktour.gui.replayfloatview.OnReplayWindowListener;
import com.walktour.gui.setting.ParamsSettingActivity;
import com.walktour.gui.task.activity.scanner.ui.ScannerInfoActivity;
import com.walktour.gui.task.activity.scannertsma.ui.ScannerTSMAInfoActivity;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;
import com.walktour.gui.weifuwu.sharepush.ShareSendActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zhicheng.chen
 * @Activity ????????????????????????(copy by InfoTabActivity)
 * @History 2013.9.9 qihang.li ?????????????????????????????????????????????
 */
@SuppressLint("InflateParams")
public class NewInfoTabActivity extends BasicTabActivity implements OnClickListener, OnTabChangeListener, OnReplayWindowListener {
    private static final String tag = "InfoTabActivity";
    /**
     * ??????????????????
     */
    public static final String INFO_TYPE_NAME = "infoType";
    public static final int REQUEST_PHOTO_URL = 10100;
    public static final int REQUEST_AUDIO_URL = 10101;
    public static final int IMAGE_REQUEST_CODE = 10102;
    public static final int TAKE_CAMERA_REQUEST_CODE = 10103;
    /**
     * tab??????
     */
    protected TabHost myTabhost;
    /**
     * ????????????
     */
    private boolean isReplay;
    /**
     * ??????????????????
     */
    private boolean isReplayNow = false;
    /**
     * ????????????
     */
    protected int currentNum = 0;
    /**
     * ?????????
     */
    private ApplicationModel appModel = null;
    /**
     * ????????????
     */
    private int infoType = InfoTabHost.INFO_TYPE_NULL;
    /**
     * ????????????
     */
    private PopupWindow popMoreMenu;

    private DisplayMetrics metric;
    /**
     * ?????????????????????????????????
     */
    private boolean isComeFromDataManager = true;


    /**
     * popMoreMenu??????????????????????????????
     **/
    View llEventContainer = null;
    /**
     * popMoreMenu??????????????????????????????
     **/
    View llL3MsgContainer = null;
    private ImageView mFreezeBtn;

    /**
     * ????????????
     */
    private AudioUtil mAudio;
    private BasicDialog.Builder dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.infoType = this.getIntent().getIntExtra(INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_OTHER);
        appModel = ApplicationModel.getInstance();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.info_toplist_new);
        FloatWindowManager.setOnReplayWindowListener(this);
        metric = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metric);
        currentNum++;
        // ?????????????????? ,???????????????????????????
        if (getIntent().getExtras() != null) {
            isReplay = getIntent().getBooleanExtra("isReplay", false);
            if (isReplay) {
                isReplayNow = getIntent().getBooleanExtra("isReplayNow", false);

                if (!FloatWindowManager.isWindowShowing()) {
                    FloatWindowManager.createFloatWindow(getApplicationContext());
                    if (isReplayNow) {
                        String filePath = getIntent().getStringExtra("filePath");
                        LogUtil.i(tag, "????????????filePath:" + filePath);
                        int startIndex = getIntent().getIntExtra("startIndex", -1);
                        int endIndex = getIntent().getIntExtra("endIndex", -1);
                        if (startIndex != -1 && endIndex != -1) {
                            FloatWindowManager.runReplay(filePath, startIndex, endIndex);
                        } else
                            FloatWindowManager.runReplay(filePath);
                    }
                }
            }
        }
        findView();
        registBroadcast();
    }

    /**
     * @date on
     * @describe ?????????????????????
     * @author jinfeng.xie
     */
    private void initAudio() {
        //??????Handler??????UI??????
        @SuppressLint("HandlerLeak")
        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case AudioUtil.RECORD_SUCCESS:
                        File file = mAudio.getAudioFile();
                        if (file != null && edittext != null) {
                            edittext.setText("save audio success\r\n" + file.getAbsolutePath());
                        }
                        break;
                    //????????????
                    case AudioUtil.RECORD_FAIL:
                        showToastMsg(getString(R.string.record_fail));
                        break;
                    //??????????????????
                    case AudioUtil.RECORD_TOO_SHORT:
                        showToastMsg(getString(R.string.time_too_short));
                        break;
                    case AudioUtil.PLAY_COMPLETION:
                        showToastMsg(getString(R.string.play_over));
                        break;
                    case AudioUtil.PLAY_ERROR:
                        showToastMsg(getString(R.string.play_error));
                        break;

                }
            }
        };
        mAudio = new AudioUtil(this);
        mAudio.binderButton(btAudio);
        mAudio.setHanlder(mHandler);
    }

    private void showToastMsg(String string) {
        ToastUtil.showToastShort(this, "" + string);
    }

    /**
     * ????????????
     */
    private void registBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WalkMessage.NOTIFY_TESTJOBDONE); // ????????????
        filter.addAction(WalkMessage.NOTIFY_INTERRUPTJOBDONE); // ??????????????????
        filter.addAction(WalkMessage.ACTION_FREEZE_CHANGED); // ??????????????????
        filter.addAction(WalkMessage.ACTION_INFO_MAP2EVENT); // ????????????????????????????????????
        registerReceiver(myReceiver, filter);
    }

    /**
     * ???????????????
     */
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (action.equals(WalkMessage.NOTIFY_TESTJOBDONE) || action.equals(WalkMessage.NOTIFY_INTERRUPTJOBDONE)) {
                final boolean isCsfb = ServerManager.getInstance(getApplicationContext()).getCsfbAnalysis();
                if (isCsfb) {
                    BasicDialog.Builder bdialog = new BasicDialog.Builder(NewInfoTabActivity.this)
                            .setTitle(R.string.str_tip)
                            .setMessage(
                                    getString(R.string.main_notify_testdone)
                                            + (isCsfb ? getString(R.string.csfb_testfinish_toanalysis) : ""))
                            .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // ???????????????????????????????????????????????????CSFB??????????????????
                                    if (isCsfb) {
                                        Intent csfbAnalysis = new Intent(getApplicationContext(), CsfbPieChartActivity.class);
                                        startActivity(csfbAnalysis);
                                        // 2015??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                                    } else if (!appModel.isFuJianTest() && appModel.isSaveMapLocas()) {
                                        TraceInfoInterface.isSaveFileLocus = true;
                                        TraceInfoInterface.saveFileLocusPath = intent.getStringExtra(WalkMessage.NOTIFY_TESTJOBDONE_PARANAME);
                                        TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Map;
                                        // TODO: 2018/6/21 czc
//                                        Intent mapIntent = new Intent(getApplicationContext(), NewInfoTabActivity.class);
//                                        mapIntent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_MAP);
//                                        startActivity(mapIntent);

                                        myTabhost.setCurrentTabByTag("map");
                                    }
                                }
                            });
                    if (isCsfb) {
                        bdialog.setNegativeButton(R.string.str_cancle);
                    }

                    bdialog.show();
                } else {
                    Toast.makeText(NewInfoTabActivity.this, getString(R.string.main_notify_testdone), Toast.LENGTH_LONG).show();
                }
            } else if (intent.getAction().equals(WalkMessage.ACTION_FREEZE_CHANGED)) {
                if (appModel.isFreezeScreen()) {
                    mFreezeBtn.setImageResource(R.drawable.navi_lock);
                } else {
                    mFreezeBtn.setImageResource(R.drawable.navi_unlock);
                }
            } else if (intent.getAction().equals(WalkMessage.ACTION_INFO_MAP2EVENT)) {
                myTabhost.setCurrentTabByTag("info");
            }
        }
    };

    @Override
    protected void onDestroy() {
        destroyTab();
        super.onDestroy();
        FloatWindowManager.setOnReplayWindowListener(null);
        LogUtil.w(tag, "---exit replay");
        try {
            if (mAudio != null) {
                mAudio.stop();
            }
            unregisterReceiver(myReceiver);
        } catch (Exception e) {
        }
    }

    private void destroyTab() {
        getLocalActivityManager().destroyActivity("map",true);
        getLocalActivityManager().destroyActivity("info",true);
        getLocalActivityManager().destroyActivity("param",true);
        getLocalActivityManager().destroyActivity("alarmmsg",true);
        getLocalActivityManager().destroyActivity("scanner",true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.w(tag, "----onStart for result");
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        if (appModel.isFreezeScreen()) {
            mFreezeBtn.setImageResource(R.drawable.navi_lock);
        } else {
            mFreezeBtn.setImageResource(R.drawable.navi_unlock);
        }
        // TODO: 2018/6/20 czc
        if (!TraceInfoInterface.saveEndShowChildTab.equals(WalkStruct.ShowInfoType.Default)) {
            TraceInfoInterface.currentShowChildTab = TraceInfoInterface.saveEndShowChildTab;
            TraceInfoInterface.saveEndShowChildTab = WalkStruct.ShowInfoType.Default;
        } else {
            //fix bug:# 211 cqt????????????????????????????????????
//            if (!SysFloorMap.isFromIndoor) {
//                TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Param;
//                TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Event;
//            }
        }

        if (TraceInfoInterface.currentShowTab.equals(WalkStruct.ShowInfoType.Map)) {
            myTabhost.setCurrentTabByTag("map");
        } else if (TraceInfoInterface.currentShowTab.equals(WalkStruct.ShowInfoType.Info)) {
            myTabhost.setCurrentTabByTag("info");
        } else if (TraceInfoInterface.currentShowTab.equals(WalkStruct.ShowInfoType.Param)) {
            myTabhost.setCurrentTabByTag("param");
        } else if (TraceInfoInterface.currentShowTab.equals(WalkStruct.ShowInfoType.AlarmMsg)) {
            myTabhost.setCurrentTabByTag("alarmmsg");
        } else if (TraceInfoInterface.currentShowTab.equals(WalkStruct.ShowInfoType.Scanner)) {
            myTabhost.setCurrentTabByTag("scanner");
        }
        updateTab(myTabhost);
    }

    /**
     * ????????????
     */
    private void findView() {
        findViewById(R.id.pointer).setOnClickListener(this);
        findViewById(R.id.freeze_btn).setOnClickListener(this);
        findViewById(R.id.capture_btn).setOnClickListener(this);
        findViewById(R.id.tag_btn).setOnClickListener(this);
        mFreezeBtn = initImageView(R.id.freeze_btn);
        myTabhost = (TabHost) findViewById(android.R.id.tabhost);
        genContentView();
        myTabhost.setOnTabChangedListener(this);

    }

    /**
     * ???????????????
     */
    private void genContentView() {
        // TODO: 2018/6/20 czc test
            this.createTab("map", R.string.info_map, new Intent(this, MapTabActivity.class));
            this.createTab("info", R.string.info_info, new Intent(this, InfoActivity2.class));
            this.createTab("param", R.string.info_parameter, new Intent(this, ParamActivity2.class));
            if (!appModel.isBeiJingTest())
                this.createTab("alarmmsg", R.string.info_alarmmsg, new Intent(this, AlarmMsg.class));
            if (appModel.isScannerTest()) {
                this.createTab("scanner", R.string.sc_main_main, new Intent(this, ScannerInfoActivity.class));
            }
            if (appModel.hasScannerTSMATest()){
                this.createTab("scanner_tsma", R.string.sc_main_main, new Intent(this, ScannerTSMAInfoActivity.class));
            }
        }

    /**
     * ??????tab??????
     *
     * @param tabTag  tab??????
     * @param textId  ??????ID
     * @param content ??????
     */
    protected View createTab(String tabTag, int textId, Intent content) {
        return TabHostUtil.createTab(this, myTabhost, tabTag, textId, content);
    }


    /**
     * ??????????????????
     */
    private void updateTab(TabHost tabHost) {
        TabHostUtil.updateTab(this, tabHost);
    }

    void tipNaviMapFinish(){
            if (dialog==null){
                dialog=new BasicDialog.Builder(this).setTitle(R.string.str_tip).setMessage(getString(R.string.exit_navi))
                        .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //??????????????????
                    }
                });
            }
            dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pointer:
                if (GaoDeNaviMapSdk.isNavi){
                    tipNaviMapFinish();
                }else {
                    this.finish();
                }
                break;
            case R.id.freeze_btn:
                if (!appModel.isFreezeScreen()) {
                    appModel.setFreezeScreen(true);
                    TraceInfoInterface.freezeBakupResult();
                    EventManager.getInstance().setFreezeEvent();
                    TraceInfoInterface.decodeFreezeNetType = TraceInfoInterface.currentNetType;
                } else {
                    appModel.setFreezeScreen(false);
                    TraceInfoInterface.unFreezeReductionResult();
                    EventManager.getInstance().setUnFreezeEvent();
                }
                Intent freezedIntent = new Intent(WalkMessage.ACTION_FREEZE_CHANGED);
                freezedIntent.putExtra(WalkMessage.FreezeState, appModel.isFreezeScreen());
                sendBroadcast(freezedIntent);
                break;
            case R.id.capture_btn:
                captureScreen();
                break;
            case R.id.tag_btn:
                showMoreMenu();
                break;
            case R.id.txt_params_setting:
                startActivity(new Intent(NewInfoTabActivity.this, ParamsSettingActivity.class));
                colseMenu();
                break;
            case R.id.txt_tab:
                showAddTagDialog();
                colseMenu();
                break;
            case R.id.txt_search:
                EventBus.getDefault().post(new OnEventMenuSelectedEvent(OnEventMenuSelectedEvent.TYPE_SEARCH));
                colseMenu();
                break;
            case R.id.txt_clear_text:
                EventBus.getDefault().post(new OnEventMenuSelectedEvent(OnEventMenuSelectedEvent.TYPE_CLEAR_TEXT));
                colseMenu();
                break;
            case R.id.txt_save:
                EventBus.getDefault().post(new OnEventMenuSelectedEvent(OnEventMenuSelectedEvent.TYPE_SAVE));
                colseMenu();
                break;
            case R.id.txt_add_label:
                EventBus.getDefault().post(new OnEventMenuSelectedEvent(OnEventMenuSelectedEvent.TYPE_ADD_LABEL));
                colseMenu();
                break;
            case R.id.txt_fleet_complain:
                EventBus.getDefault().post(new OnEventMenuSelectedEvent(OnEventMenuSelectedEvent.TYPE_FLEET_COMPLAIN));
                colseMenu();
                break;
            case R.id.txt_setting:
                EventBus.getDefault().post(new OnEventMenuSelectedEvent(OnEventMenuSelectedEvent.TYPE_SETTING));
                colseMenu();
                break;
            case R.id.tv_msg_search:
                EventBus.getDefault().post(new OnL3MsgMenuSelectedEvent(OnL3MsgMenuSelectedEvent.TYPE_SEARCH));
                colseMenu();
                break;
            case R.id.tv_msg_setting:
                EventBus.getDefault().post(new OnL3MsgMenuSelectedEvent(OnL3MsgMenuSelectedEvent.TYPE_SETTING));
                colseMenu();
                break;
            case R.id.tv_refresh_setting:
                EventBus.getDefault().post(new OnL3MsgMenuSelectedEvent(OnL3MsgMenuSelectedEvent.TYPE_REFRESH_SETTING));
                colseMenu();
                break;
            case R.id.tv_save_msg_list:
                EventBus.getDefault().post(new OnL3MsgMenuSelectedEvent(OnL3MsgMenuSelectedEvent.TYPE_SAVE_MSG_LIST));
                colseMenu();
                break;


            default:

                break;
        }

    }

    @SuppressWarnings("deprecation")
    private void showMoreMenu() {
        if (popMoreMenu == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.menu_info_more, null);
            view.findViewById(R.id.txt_params_setting).setOnClickListener(this);
            view.findViewById(R.id.txt_tab).setOnClickListener(this);
            llEventContainer = view.findViewById(R.id.ll_event_container);
            llL3MsgContainer = view.findViewById(R.id.ll_l3msg_container);
            //?????????PARAM?????????????????????????????????????????????????????????menu?????????
            llEventContainer.setVisibility(TraceInfoInterface.sIsOnEvent ? View.VISIBLE : View.GONE);
            //?????????PARAM?????????????????????????????????????????????????????????menu?????????
            llL3MsgContainer.setVisibility(TraceInfoInterface.sIsOnL3Msg ? View.VISIBLE : View.GONE);
            view.findViewById(R.id.txt_search).setOnClickListener(this);
            view.findViewById(R.id.txt_clear_text).setOnClickListener(this);
            view.findViewById(R.id.txt_save).setOnClickListener(this);
            view.findViewById(R.id.txt_add_label).setOnClickListener(this);
            view.findViewById(R.id.txt_fleet_complain).setOnClickListener(this);
            view.findViewById(R.id.txt_setting).setOnClickListener(this);
            view.findViewById(R.id.tv_msg_search).setOnClickListener(this);
            view.findViewById(R.id.tv_msg_setting).setOnClickListener(this);
            view.findViewById(R.id.tv_refresh_setting).setOnClickListener(this);
            view.findViewById(R.id.tv_save_msg_list).setOnClickListener(this);
            popMoreMenu = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);//
            popMoreMenu.setOutsideTouchable(true);
            popMoreMenu.setFocusable(true);
            popMoreMenu.setTouchable(true);
            popMoreMenu.setBackgroundDrawable(new BitmapDrawable());
//			popMoreMenu.setAnimationStyle(R.style.popwin_anim_down_in_style);
            popMoreMenu.showAsDropDown(findViewById(R.id.title_layout), 0, 10);
        } else {
            if (popMoreMenu.isShowing()) {
                popMoreMenu.dismiss();
            } else {
                if (null != llEventContainer && null != llL3MsgContainer) {
                    //?????????PARAM?????????????????????????????????????????????????????????menu?????????
                    llEventContainer.setVisibility(TraceInfoInterface.sIsOnEvent ? View.VISIBLE : View.GONE);
                    //?????????PARAM?????????????????????????????????????????????????????????menu?????????
                    llL3MsgContainer.setVisibility(TraceInfoInterface.sIsOnL3Msg ? View.VISIBLE : View.GONE);
                }
                popMoreMenu.showAsDropDown(findViewById(R.id.title_layout), 0, 10);
            }
        }
    }

    private void colseMenu() {
        if (popMoreMenu != null) {
            if (popMoreMenu.isShowing()) {
                popMoreMenu.dismiss();
            }
        }
    }

    Spinner spTagType;//????????????tag??????
    EditText edittext;//
    Button btRadio;
    Button btAudio;
    Button btPhoto;
    Button btTakePhoto;
    private Uri mImageUri;                                  //?????????uri

    private String mImageName;                              //????????????????????????

    private File mImageFile;                                //????????????
    private String mImagePath;                   //??????????????????????????????????????? / ???????????????????????????????????????

//    TextView tvInfo;

    /**
     * ????????????????????????
     */
    private void showAddTagDialog() {
        if (!appModel.isTestJobIsRun()){
            ToastUtil.showToastShort(this,getString(R.string.toast_only_test_can_tag));
            return;
        }
        LayoutInflater fac = LayoutInflater.from(getApplicationContext());
        View view = fac.inflate(R.layout.alert_dialog_customevent_tag, null);
        spTagType = (Spinner) view.findViewById(R.id.sp_tagtype);
        edittext = (EditText) view.findViewById(R.id.alert_textEditText);
        btRadio = (Button) view.findViewById(R.id.bt_action_radio);
        btAudio = (Button) view.findViewById(R.id.bt_action_audio);
        btPhoto = (Button) view.findViewById(R.id.bt_action_photo);
        btTakePhoto = (Button) view.findViewById(R.id.bt_action_take_photo);
        initAudio();
//        tvInfo = (TextView) view.findViewById(R.id.tv_info);
        String[] tagTypes = getResources().getStringArray(R.array.arr_tag);
        ArrayAdapter<String> tagAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
                tagTypes);
        //???????????????????????????????????????Spinner??????????????????
        tagAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spTagType.setAdapter(tagAdapter);
        spTagType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                checkTagLayout(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                checkTagLayout(4);
            }
        });
        btPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //????????????????????????????????????????????????????????????
                if (ActivityCompat.checkSelfPermission(NewInfoTabActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //???????????????????????????
                    Log.d(tag, "???????????????????????????????????????");
                    ActivityCompat.requestPermissions(NewInfoTabActivity.this, new String[]
                            {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
                } else {
                    Log.d(tag, "???????????????????????????????????????");
                    //???????????????
                    startCamera();
                }
            }
        });
        btTakePhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, TAKE_CAMERA_REQUEST_CODE);
            }
        });
//        btAudio.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(NewInfoTabActivity.this, TagAudioActivity.class);
//                startActivityForResult(intent, REQUEST_PHOTO_URL);
//            }
//        });
        btRadio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewInfoTabActivity.this, TagPhotoActivity.class);
                startActivityForResult(intent, REQUEST_PHOTO_URL);
            }
        });
        final long lableTime = System.currentTimeMillis();
        BasicDialog alert = new BasicDialog.Builder(NewInfoTabActivity.this).setIcon(android.R.drawable.ic_menu_edit)
                .setTitle(R.string.info_add_label).setView(view)
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String eventStr = edittext.getText().toString().trim();
                        if (eventStr == null || eventStr.equals("")) {

                        } else {
                            EventManager.getInstance().addTagEvent(NewInfoTabActivity.this, lableTime, eventStr);
                        }
                    }
                }).setNegativeButton(R.string.str_cancle).create();
        alert.show();
    }
    /**
     * ?????????????????????????????????????????????uri
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void startCamera() {
        Intent intent = new Intent();
        //???????????????????????????
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        //????????????
        createImageFile();
        //????????????
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //??????uri
        mImageUri = FileProvider.getUriForFile(this, "com.walktour.gui.provider", mImageFile);
        //???uri?????????????????????
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        //?????????????????????????????????
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }
    /**
     * ??????????????????
     */
    private void createImageFile(){
        //???????????????????????????????????????????????????????????????????????????
        //??????Media File
        mImageName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
        //??????????????????
        mImageFile = new File(WalktourConst.SAVE_ATTACH_DIR + "/", mImageName);
        //?????????????????????????????????mImagePath??????????????????
        mImagePath = mImageFile.getAbsolutePath();
        //?????????????????????????????????
        mImageFile.getParentFile().mkdirs();
        //??????????????????Read-only????????????????????????SD
        mImageFile.setWritable(true);
    }


    private void checkTagLayout(int i) {
        switch (i) {
            case 0:
                edittext.setEnabled(true);
                edittext.setText("");
                btRadio.setVisibility(View.GONE);
                btAudio.setVisibility(View.GONE);
                btPhoto.setVisibility(View.GONE);
                btTakePhoto.setVisibility(View.GONE);
                break;
            case 1:
                edittext.setEnabled(false);
                edittext.setText("");
                btRadio.setVisibility(View.VISIBLE);
                btAudio.setVisibility(View.GONE);
                btPhoto.setVisibility(View.VISIBLE);
                btTakePhoto.setVisibility(View.VISIBLE);
                break;
            case 2:
                edittext.setEnabled(false);
                edittext.setText("");
                btRadio.setVisibility(View.GONE);
                btAudio.setVisibility(View.VISIBLE);
                btPhoto.setVisibility(View.GONE);
                btTakePhoto.setVisibility(View.GONE);
                break;
            default:
                edittext.setEnabled(false);
                btRadio.setVisibility(View.GONE);
                btAudio.setVisibility(View.GONE);
                btPhoto.setVisibility(View.GONE);
                btTakePhoto.setVisibility(View.GONE);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PHOTO_URL) {
                edittext.setText(data.getStringExtra("data"));
            } else if (requestCode == REQUEST_AUDIO_URL) {
                edittext.setText(data.getStringExtra("data"));
            } else if (requestCode == IMAGE_REQUEST_CODE) {
                Bitmap bitmap = null;
                try {
                    //??????uri??????bitmap
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //??????????????????SD???????????????
                savePhotoToSD(bitmap);
                //??????????????????
                updateSystemGallery();
                edittext.setText("save photo success\r\n"+mImageFile.getAbsolutePath());
            } else if (requestCode == TAKE_CAMERA_REQUEST_CODE) {
                Uri selectImageUri = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectImageUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");//??????????????????
                File tagFile=new File(WalktourConst.SAVE_ATTACH_DIR +File.separator+ df.format(new Date()) + ".jpg");
                try {
                    FileUtil.copyFile(new File(picturePath),tagFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                edittext.setText("save photo success\r\n" + tagFile.getAbsolutePath());
            }

        }
    }

    /**
     * ??????????????????
     */
    private void updateSystemGallery() {
        //??????????????????????????????
        try {
            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                    mImageFile.getAbsolutePath(), mImageName, null);
            Log.d(tag, "????????????????????????????????????");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // ????????????????????????
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + mImagePath)));
        Log.d(tag, "???????????????????????????");
    }

    /**
     * ???????????????SD??????????????????
     */
    private void savePhotoToSD(Bitmap bitmap) {
        Log.d(tag, "?????????????????????????????????");
        //????????????????????????
        BufferedOutputStream os = null;
        try {
            //???????????????
            os = new BufferedOutputStream(new FileOutputStream(mImageFile));
            Log.d(tag, "??????????????????");
            //???????????????100???????????????
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            Log.d(tag, "?????????????????????");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    //??????????????????????????????????????????
                    os.flush();
                    os.close();
                    Log.d(tag, "??????????????????");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * ????????????
     */
    private void captureScreen() {
        // ??????Sdcard???????????????????????????
        String desFileDir = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                AppFilePathUtil.getInstance().getSDCardBaseDirectory(getString(R.string.path_snapshot))
                : AppFilePathUtil.getInstance().getAppFilesDirectory(getString(R.string.path_snapshot));
        File snapDir = new File(desFileDir);
        if (!snapDir.isDirectory()) {
            snapDir.mkdirs();
        }

        new BasicDialog.Builder(NewInfoTabActivity.this).setIcon(android.R.drawable.ic_menu_camera)
                .setTitle(R.string.str_snapshot).setMessage(getString(R.string.str_snapshot_save) + desFileDir)
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String desFileDir = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                                AppFilePathUtil.getInstance().getSDCardBaseDirectory(getString(R.string.path_snapshot))
                                : AppFilePathUtil.getInstance().getAppFilesDirectory(getString(R.string.path_snapshot));
                        File snapDir = new File(desFileDir);
                        if (!snapDir.isDirectory()) {
                            snapDir.mkdirs();
                        }
                        MyPhone phone = new MyPhone(NewInfoTabActivity.this);
                        phone.getScreen(NewInfoTabActivity.this, desFileDir, ImageUtil.FileType.PNG);
                    }
                }).setNeutralButton(R.string.share_project_share, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //?????????
                String desFileDir = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                        AppFilePathUtil.getInstance().getSDCardBaseDirectory(getString(R.string.path_snapshot))
                        : AppFilePathUtil.getInstance().getAppFilesDirectory(getString(R.string.path_snapshot));
                File snapDir = new File(desFileDir);
                if (!snapDir.isDirectory()) {
                    snapDir.mkdirs();
                }
                MyPhone phone = new MyPhone(NewInfoTabActivity.this);
                phone.getScreen(NewInfoTabActivity.this, desFileDir, ImageUtil.FileType.PNG);
                //?????????
                Bundle bundle = new Bundle();
                bundle.putInt(ShareCommons.SHARE_FROM_KEY, ShareCommons.SHARE_FROM_SCREENSHOT_PIC);
                jumpActivity(ShareSendActivity.class, bundle);
            }
        }).setNegativeButton(R.string.str_cancle).show();
    }

    @Override
    public void onTabChanged(String tabId) {
        currentNum--;
        updateTab(myTabhost);
        if (tabId.equals("map")) {
            TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Map;
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Default;
        } else if (tabId.equals("info")) {
            TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Info;
        } else if (tabId.equals("param")) {
            TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Param;
        } else if (tabId.equals("alarmmsg")) {
            TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.AlarmMsg;
        } else if (tabId.equals("scanner")) {
            TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Scanner;
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Default;
        } else if (tabId.equals("param2")) {
            TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Param;
        }
        /**
         * ???????????????????????????????????????????????????View???Tag
         */
        TabHost tabHost = (TabHost) myTabhost.getCurrentView().findViewById(R.id.child_tabhost);
        if (tabHost != null && currentNum != 0) {
            String tabTag = tabHost.getCurrentTabTag();
            if (tabTag == null) {
                tabTag = "";
            }
            if (tabTag.equals("Event")) {
                TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Event;
            } else if (tabTag.equals("Chart")) {
                TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Chart;
            } else if (tabTag.equals("Data")) {
                TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Data;
            } else if (tabTag.equals("L3Msg")) {
                TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.L3Msg;
            } else if (tabTag.equals("VideoPlay")) {
                TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.VideoPlay;
            } else if (tabTag.equals(getResources().getString(R.string.info_gsm))) {
                TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Gsm;
            } else if (tabTag.equals(getResources().getString(R.string.info_umts))) {
                TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Umts;
            } else if (tabTag.equals(getResources().getString(R.string.info_hspa))) {
                TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Hspa;
            } else if (tabTag.equals(getResources().getString(R.string.info_hspa_plus))) {
                TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.HspaPlus;
            } else if (tabTag.equals(getResources().getString(R.string.info_lte))) {
                TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.LTE;
            } else if (tabTag.equals(getResources().getString(R.string.info_cdma))) {
                TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Cdma;
            } else if (tabTag.equals(getResources().getString(R.string.info_evdo))) {
                TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.EvDo;
            } else if (tabTag.equals(getResources().getString(R.string.info_edge))) {
                TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Edge;
            } else if (tabTag.equals(getResources().getString(R.string.info_tdscdma))) {
                TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.TDSCDMA;
            } else if (tabTag.equals(getResources().getString(R.string.info_tdhspa))) {
                TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.TDHspaPlus;
            } else if (tabTag.equals(getResources().getString(R.string.info_tcpip))) {
                TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.TcpIpPcap;
            } else if (tabTag.equals(getResources().getString(R.string.info_ca))) {
                TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.LTECA;
            } else if (tabTag.equals(getResources().getString(R.string.info_4t4r))) {
                TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.LTE4T4R;
            }
        }

        Log.i(tag, "currentShowTab:" + TraceInfoInterface.currentShowChildTab);
        // ?????????static????????????WifiScanner???????????????Info?????????
        if (WifiScanner.isScannerWifi() && !tabId.equals("param")) {
            WifiScanner.setScannerWifi(false);
            WifiScanner.instance(this).stopScan();
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (TraceInfoInterface.currentShowChildTab.equals(WalkStruct.ShowInfoType.Default)) {
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Default;
        } else {
            TraceInfoInterface.saveEndShowChildTab = TraceInfoInterface.currentShowChildTab;
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Default;
        }
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        try {
            return super.dispatchKeyEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onWindowClose() {
        if (isComeFromDataManager) {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == AudioUtil.PERMISSIONS_REQUEST_FOR_AUDIO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mAudio != null) {
                    mAudio.startRecord();
                } else {
                    startCamera();
                }
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
