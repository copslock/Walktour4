package com.walktour.gui.data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dinglicom.DingliRcu;
import com.dinglicom.data.control.DataTableStruct.DataTableMap;
import com.dinglicom.data.model.RecordDetail;
import com.dinglicom.data.model.RecordDetailUpload;
import com.dinglicom.data.model.RecordNetType;
import com.dinglicom.data.model.RecordTaskType;
import com.dinglicom.data.model.TestRecord;
import com.dinglicom.dataset.DatasetManager;
import com.dinglicom.dataset.EventManager;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.DateUtil;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.Utils.WalkStruct.TestType;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.adapter.EventIndexAdapter;
import com.walktour.control.adapter.ExportMutilChoiceAdapter;
import com.walktour.control.config.ConfigParmExport;
import com.walktour.control.config.ServerManager;
import com.walktour.control.instance.AlertManager;
import com.walktour.control.instance.DataManagerFileList;
import com.walktour.control.instance.FileDB;
import com.walktour.customView.ListViewForScrollView;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.datepicker.WheelMain;
import com.walktour.framework.view.treeview.TreeNode;
import com.walktour.gui.R;
import com.walktour.gui.data.dialog.MoreOptionPopWin;
import com.walktour.gui.data.dialog.PopDialog;
import com.walktour.gui.data.dialog.PopDialog.ClickListener;
import com.walktour.gui.data.model.DBManager;
import com.walktour.gui.data.model.DataModel;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.setting.msgfilter.MsgFilterSettingFactory;
import com.walktour.model.ExportParmModel;
import com.walktour.service.app.DataTransService;
import com.walktour.service.app.datatrans.model.UploadFileModel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressLint({"InflateParams", "HandlerLeak"})
public abstract class FragmentBase extends Fragment implements OnItemClickListener {

    /**
     * 日志标识
     */
    private static final String TAG = "FragmentBase";
    protected String FLAG = SceneType.Anhui.name();
    public static final int MSG_UPDATE_UI = 1;
    private static final int MSG_CUSTOM_EXPORT = 10;
    // private final int MSG_DELETE = 20;
    // private final int MSG_COPY_OR_MOVE = 30;
    private final int MSG_Partition_SUCCESS = 40;
    private final int MSG_Partition_FAIL = 41;
    private final int MSG_RENAME_SUCCEED = 50;
    private final int MSG_MERGE_SUCCESS = 60;
    private final int MSG_MERGE_FAIL = 61;
    private final int MSG_REVIEW_EVENT_SUCCEED = 65;
    private final int MSG_REVIEW_EVENT_FAIL = 66;
    /**
     * 下载结束标识
     */
    private static final int DOWNLOAD_END = 72;
    /**
     * 下载进行标识
     */
    private static final int DOWNLOAD_DOING = 73;
    /**
     * 下载失败标识
     */
    private static final int DOWNLOAD_FAIL = 76;
    /**
     * 下载文件不存在标识
     */
    private static final int DOWNLOAD_NO_FOUND = 77;
    /**
     * 下载文件存在标识
     */
    private static final int DOWNLOAD_FOUND = 78;
    // private final int TEST_TYPE_DT = 5001;

    protected Activity mActivity;
    protected LayoutInflater inflater;
    protected SharedPreferences sp;
    protected DBManager mDbManager;
    public View filterView;
    protected View localTreeView = null;
    protected View deleteModeTreeView = null;
    /**
     * Fragment当前状态是否可见
     */
    protected boolean isVisible;
    protected boolean firstVisible = true;
    protected TreeNode root;
    protected ListView lv;
    protected TreeViewAdapter mAdapter;
    protected MoreOptionPopWin moreOptionPopWin;
    private FileOperater mFileOperater = null;
    /**
     * 是否显示平台查询对话框
     */
    private boolean isShowPlatformQueryDialog = false;

    /**
     * 数据集管理对象
     */
    private DatasetManager mDataset;
    protected String choutDate;

    private int fromIndex = 0;

    private int toIndex = 0;

    private TextView startTimeTxt;

    private TextView endTimeTxt;

    public boolean isChooseAll = false;
    /**
     * 服务器管理类
     */
    private ServerManager mServer;
    /**
     * 当前选择的报表类型
     */
    private String mChoiceReportType;
    /**
     * 报表存放路径
     */
    private String reportPath;
    /**
     * 报表类型适配器
     */
    private ArrayAdapter<String> mReportTypeAdapter;
    /**
     * 显示的报表类型数组
     */
    private String[] mShowReportNames;
    /**
     * 报表名称
     */
    private String mReportName;
    /**
     * 报表类型名称数组
     */
    private String[] mReportTypeNames;
    /**
     * 报表类型值数组
     */
    private String[] mReportTypeValues;
    /**
     * 报表状态
     */
    private Map<String, String> mReportStateMap = new HashMap<String, String>();

    private MyHandler mHandler = new MyHandler(this);
    /**
     * 旧日期格式
     */
    private SimpleDateFormat mOldDF = new SimpleDateFormat("yyyyMMdd-hhmmss", Locale.getDefault());
    /**
     * 新日期格式
     */
    private SimpleDateFormat mNewDF = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss", Locale.getDefault());
    /**
     * 文件名前缀，一般用来放设备名或工单测试的调用名
     */
    protected String mFileNamePrefix;

    private static class MyHandler extends Handler {
        private WeakReference<FragmentBase> reference;

        public MyHandler(FragmentBase base) {
            this.reference = new WeakReference<FragmentBase>(base);
        }

        public void handleMessage(Message msg) {
            FragmentBase base = this.reference.get();
            if (msg.obj != null) {
                Toast.makeText(base.mActivity, msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
            LogUtil.d(TAG, "----handleMessage:" + msg.what + "----");
            base.getAdapter().notifyDataSetChanged();
            switch (msg.what) {

                case MSG_CUSTOM_EXPORT:
                    base.dataDialog.cancel();
                    break;
                case DOWNLOAD_END:
                    String reportType = (String) msg.obj;
                    base.mReportStateMap.put(reportType, base.getResources().getString(R.string.download_file_finish));
                    base.showDownloadState();
                    break;
                case DOWNLOAD_DOING:
                    reportType = (String) msg.obj;
                    base.mReportStateMap.put(reportType, base.getResources().getString(R.string.downloading));
                    base.showDownloadState();
                    break;
                case DOWNLOAD_NO_FOUND:
                    reportType = (String) msg.obj;
                    base.mReportStateMap.put(reportType, base.getResources().getString(R.string.total_total_ing));
                    base.showDownloadState();
                    break;
                case DOWNLOAD_FOUND:
                    reportType = (String) msg.obj;
                    base.mReportStateMap.put(reportType, base.getResources().getString(R.string.total_total_finish));
                    base.showDownloadState();
                    break;
                case DOWNLOAD_FAIL:
                    Toast.makeText(base.mActivity, R.string.download_fail, Toast.LENGTH_SHORT).show();
                    base.showDownloadState();
                    break;
                default:
                    break;
            }
        }

        ;
    }

    ;

    private Handler uiHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REVIEW_EVENT_SUCCEED:
                    showPreviewEventDialog(true);
                    break;
                case MSG_REVIEW_EVENT_FAIL:
                    showPreviewEventDialog(false);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mActivity = getActivity();
        sp = mActivity.getSharedPreferences(mActivity.getPackageName(), Context.MODE_PRIVATE);
        inflater = LayoutInflater.from(mActivity);
        mDbManager = DBManager.getInstance(mActivity);
        mFileOperater = new FileOperater(mActivity);
        mDataset = DatasetManager.getInstance(mActivity);
        root = TreeNode.root();
        mServer = ServerManager.getInstance(this.getActivity());
        this.reportPath = AppFilePathUtil.getInstance().getSDCardBaseDirectory("report");
        if (FLAG.equals(SceneType.Auto.name()) || FLAG.equals(SceneType.MultiTest.name())) {//自动测试/多网测试时注册广播
            if (mActivity != null) {
                registReceiver();
            }
        }
        if (firstVisible) {
            onVisible();
            firstVisible = false;
        }
        super.onCreate(savedInstanceState);
    }

    /**
     * 可见
     */
    protected void onVisible() {
        if (mActivity != null) {
            registReceiver();
        }
        refreshData();
    }

    /**
     * 不可见
     */
//	protected void onInvisible(){
//		
//	}
    protected abstract void refreshData();

    protected abstract void getListDatas(boolean loacl);

    /**
     * 显示文件属性
     */
    protected void showPropertyDialog(final DataModel dataModel) {
        View propertyView = inflater.inflate(R.layout.alert_dialog_data_manager_new_property, null);

        new PopDialog(mActivity, propertyView).setTitle(getResources().getString(R.string.attribute)).setButtonListener(R.id.btn_scan, new ClickListener() {

            @Override
            public void onClick() {
                showTestPlan(dataModel.testRecord);
            }
        }, false).show();
        ((TextView) propertyView.findViewById(R.id.txt_file_path)).setText(dataModel.testRecord.file_name);
        propertyView.findViewById(R.id.layout_tcp_ip).setVisibility(View.GONE);// 隐藏TCP/IP
        ((TextView) propertyView.findViewById(R.id.txt_file_size_1)).setText("");
        ((TextView) propertyView.findViewById(R.id.txt_file_size_2)).setText(DBManager.getInstance(getActivity().getApplicationContext()).getUploadStateStr(dataModel.getState()));
        ((TextView) propertyView.findViewById(R.id.txt_file_size_3)).setText(dataModel.testRecord.getTest_type_str());
        ((TextView) propertyView.findViewById(R.id.txt_file_size_4)).setText(dataModel.getOperator());// 运营商
        ((TextView) propertyView.findViewById(R.id.txt_file_size_5)).setText(DateUtil.Y_M_D_H_M.format(new Date(dataModel.testRecord.time_create)));
        ((TextView) propertyView.findViewById(R.id.txt_file_size_6)).setText(DateUtil.getTimeLengthString(mActivity, dataModel.testRecord.time_create, dataModel.testRecord.time_end));
        ((TextView) propertyView.findViewById(R.id.txt_file_size_7)).setText(dataModel.testRecord.getRecordTaskTypeName());
        // 文件类型 RCU 等
        LinearLayout content = (LinearLayout) propertyView.findViewById(R.id.layout_content);
        content.removeAllViews();
        ArrayList<RecordDetail> list = dataModel.testRecord.getRecordDetails();
        int test_type = dataModel.testRecord.test_type;// 测试类型:DT/CQT
        String node_id_str = dataModel.testRecord.getNode_id_str();
        if (test_type == TestType.CQT.getTestTypeId()) {// CQT 显示楼层结构信息
            propertyView.findViewById(R.id.layout_info_building_floor).setVisibility(View.VISIBLE);
            if (node_id_str.equals("0")) {
                ((TextView) propertyView.findViewById(R.id.txt_8)).setText("");
                ((TextView) propertyView.findViewById(R.id.txt_9)).setText("");
            } else {
                String nodeIdStr = node_id_str.split(",")[1];
                String buildStr = "";
                String floorStr = "";
                if (nodeIdStr.indexOf("/") > 0) {
                    buildStr = nodeIdStr.substring(0, nodeIdStr.indexOf("/"));
                    floorStr = nodeIdStr.substring(nodeIdStr.indexOf("/") + 1);
                }
                ((TextView) propertyView.findViewById(R.id.txt_8)).setText(buildStr);
                ((TextView) propertyView.findViewById(R.id.txt_9)).setText(floorStr);
            }
        } else {
            propertyView.findViewById(R.id.layout_info_building_floor).setVisibility(View.GONE);
        }

        if (list == null)
            return;
        for (int i = 0; i < list.size(); i++) {
            View v = inflater.inflate(R.layout.data_manager_new_property_file_type_item, null);
            RecordDetail recordDetail = list.get(i);
            ((TextView) v.findViewById(R.id.file_type_name)).setText(recordDetail.getFile_type_str());// 文件类型
            ((TextView) v.findViewById(R.id.txt_1)).setText(FileUtil.convertFileSize(recordDetail.file_size));// 文件大小
            ((TextView) v.findViewById(R.id.txt_2)).setText(recordDetail.file_path);
            ((TextView) v.findViewById(R.id.txt_5)).setText(
                    DBManager.getInstance(getActivity().getApplicationContext()).getUploadStateStr(recordDetail.getDetailUploads()));

            // if (test_type != TEST_TYPE_DT) {//CQT 增加楼层结构信息
            // //建筑物信息
            // v.findViewById(R.id.layout_building).setVisibility(View.VISIBLE);
            // if (node_id_str.equals("0")) {
            // ((TextView)v.findViewById(R.id.txt_3)).setText("");
            // ((TextView)v.findViewById(R.id.txt_4)).setText("");
            // } else {
            // String nodeIdStr = node_id_str.split(",")[1];
            // String buildStr = nodeIdStr.substring(0, nodeIdStr.indexOf("/"));
            // String floorStr = nodeIdStr.substring(nodeIdStr.indexOf("/") + 1);
            // ((TextView)v.findViewById(R.id.txt_3)).setText(buildStr);
            // ((TextView)v.findViewById(R.id.txt_4)).setText(floorStr);
            // }
            //
            // } else {
            // v.findViewById(R.id.layout_building).setVisibility(View.GONE);
            // }

            // 上传状态
            // ((TextView)v.findViewById(R.id.txt_5)).setText(DBManager.getInstance(getActivity().getApplicationContext()).getUploadStateStr(dataModel.getState()));
            // 测试类型（DT/CQT）
            // ((TextView)v.findViewById(R.id.txt_6)).setText(testRecord.getTest_type_str());
            content.addView(v);
        }
    }

    /**
     * 显示测试计划
     *
     * @param testRecord
     */
    protected void showTestPlan(TestRecord testRecord) {
        View testPlanView = inflater.inflate(R.layout.alert_dialog_data_manager_new_test_plan, null);
        new PopDialog(mActivity, testPlanView).setTitle(getResources().getString(R.string.str_test_plan)).show();
        ((TextView) testPlanView.findViewById(R.id.txt_file_path)).setText(testRecord.file_name);
        LinearLayout layoutContent = (LinearLayout) testPlanView.findViewById(R.id.layout_content);
        ArrayList<RecordTaskType> recordTaskTypes = testRecord.getRecordTaskTypes();
        layoutContent.removeAllViews();
        for (int i = 0; i < recordTaskTypes.size(); i++) {
            String testPlan = recordTaskTypes.get(i).test_plan;
            // 暂时使用
            // TextView tv = new TextView(mActivity);
            // tv.setText(testPlan);
            // layoutContent.addView(tv);
            // ---------------------------------------------------------------------------
            // String[] plans = testPlan.split("\\\\r\\\\n");
            String[] plans = testPlan.split("\\r\\n");
            View planV = inflater.inflate(R.layout.data_manager_new_test_plan_item, null);
            LinearLayout layoutPlan = (LinearLayout) planV.findViewById(R.id.layout_content_test_plan_item);
            if (plans.length > 0) {
                // String[] tmp = plans[0].split(":");
                String[] tmp = plans[0].split("=");
                String testTypeName = tmp.length > 1 ? tmp[1] : "";
                ((TextView) planV.findViewById(R.id.test_type_name)).setText(testTypeName);
            }
            layoutPlan.removeAllViews();
            for (int j = 0; j < plans.length; j++) {
                View planVChild = inflater.inflate(R.layout.data_manager_new_test_plan_item_child_item, null);
                String name = "";
                String value = "";
                // if (plans[j].contains(":")) {
                if (plans[j].contains("=")) {
                    // String[] planDetail = plans[j].split(":");
                    String[] planDetail = plans[j].split("=");
                    if (planDetail.length == 1) {
                        name = planDetail[0];
                    } else if (planDetail.length == 2) {
                        name = planDetail[0];
                        value = planDetail[1];
                    }

                } else {
                    name = plans[j];
                }
                ((TextView) planVChild.findViewById(R.id.txt_1)).setText(name);
                ((TextView) planVChild.findViewById(R.id.txt_2)).setText(value);
                if (j == plans.length - 1) {
                    planVChild.findViewById(R.id.divider).setVisibility(View.INVISIBLE);
                }
                if (!name.equals("") || !value.equals("")) {
                    layoutPlan.addView(planVChild);
                }
            }
            layoutContent.addView(planV);
        }
    }

    /**
     * 编辑菜单
     */
    protected void showEditDialog(final DataModel dataModel) {
        new BasicDialog.Builder(mActivity).setIcon(android.R.drawable.ic_menu_edit).setTitle(R.string.edit).setItems(getResources().getStringArray(R.array.edit_new), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {

                    case 0:// 删除(完成)
                        mFileOperater.delete(dataModel);
                        break;

                    case 1:// 复制(完成)
                        mFileOperater.copyFile(dataModel);
                        break;

                    case 2:// 移动(完成)
                        mFileOperater.moveFile(dataModel);
                        break;

                    case 3:// 数据分割(完成)
                        /******************************************************/
                        // 按时间分割RCU调用流程:
                        // 1.initMergeOrDivideFile
                        // 2.addDivideRcuByTime
                        // 3.divideRcus
                        boolean hasRCUFile = false;
                        for (RecordDetail recordDetail : dataModel.testRecord.getRecordDetails()) {
                            if (recordDetail.getFile_type_str().equalsIgnoreCase("dcf")) {// 判断是否有rcu文件
                                hasRCUFile = true;
                                break;
                            }
                        }
                        if (hasRCUFile) {
                            showDataPartitionDialog(dataModel);
                        } else {
                            Toast.makeText(mActivity, "not support", Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case 4:// 数据合并
                        /************************************************************************/
                        // 合并RCU调用流程：
                        // 1.initMergeOrDivideFile
                        // 2.addMergeOrDivideFile
                        // 3. mergeRcus
                        /************* 测试分割合并 **************/

                        /**
                         * 按时间先后合并
                         */
                        Collections.sort(getAdapter().selectedList, new Comparator<DataModel>() {
                            @Override
                            public int compare(DataModel lhs, DataModel rhs) {
                                return new Date(lhs.getCreateTime()).compareTo(new Date(rhs.getCreateTime()));
                            }
                        });

                        if (getAdapter().selectedList.size() >= 2) {
                            mergeDataThr(getAdapter().selectedList); // 合并数据方法

                        } else {
                            Toast.makeText(mActivity, "You must select at least 2 files Merge", Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case 5:// 自定义导出(完成)
                        if (mAdapter.selectedList.size() >= 2) {
                            ToastUtil.showToastShort(getContext(), R.string.custom_export_multifile);
                            return;
                        }
                        new CustomExport().execute(new Object[]{dataModel});
                        break;
                    case 6:// 重命名(完成)
                        showRenameDialog(dataModel);
                        break;
                    case 7:// 预览轨迹
                        showPreviewLocusDialog(dataModel);
                        break;
                    case 8:// 预览事件(完成)
                        showPreviewEventDialog(dataModel);
                        break;
                    // case 9:// 查看属性(完成)
                    // showPropertyDialog(dataModel);
                    // break;
                    case 9:// 平台查询(电信招标用)
                        showPlatformQueryDialog(dataModel);
                        break;
                }
            }
        }).show();
    }

    /**
     * 显示下载的状态
     */
    private void showDownloadState() {
        if (!isShowPlatformQueryDialog)
            return;
        for (int i = 0; i < mShowReportNames.length; i++) {
            mShowReportNames[i] = mReportTypeNames[i] + "\t\t\t" + mReportStateMap.get(mReportTypeValues[i]);
        }
        this.mReportTypeAdapter.notifyDataSetChanged();
    }

    /**
     * 显示平台查询选择对话框
     *
     * @param dataModel 数据对象
     */
    private void showPlatformQueryDialog(DataModel dataModel) {
        this.isShowPlatformQueryDialog = true;
        View view = inflater.inflate(R.layout.alert_dialog_platform_query, null);
        TextView fileName = (TextView) view.findViewById(R.id.file_name_txt);
        this.mReportName = dataModel.testRecord.file_name;
        this.mReportName = formatFileName(this.mReportName);
        fileName.setText(this.mReportName);
        mReportTypeNames = this.getResources().getStringArray(R.array.report_type_name);
        mReportTypeValues = this.getResources().getStringArray(R.array.report_type_value);
        mReportStateMap.clear();
        for (int i = 0; i < mReportTypeValues.length; i++) {
            File file = new File(this.reportPath + dataModel.testRecord.file_name + "_" + mReportTypeValues[i] + ".xlsx");
            if (file.exists()) {
                mReportStateMap.put(mReportTypeValues[i], this.getResources().getString(R.string.download_file_finish));
            } else {
                mReportStateMap.put(mReportTypeValues[i], this.getResources().getString(R.string.total_total_ing));
            }
        }
        mShowReportNames = new String[mReportTypeValues.length];
        for (int i = 0; i < mShowReportNames.length; i++) {
            mShowReportNames[i] = mReportTypeNames[i] + "\t\t\t" + mReportStateMap.get(mReportTypeValues[i]);
        }
        mReportTypeAdapter = new ArrayAdapter<String>(this.mActivity, R.layout.list_item_report_type, mShowReportNames);
        ListView listView = (ListView) view.findViewById(R.id.report_type_list);
        listView.setAdapter(mReportTypeAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setItemChecked(0, true);
        this.mChoiceReportType = mReportTypeValues[0];
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mChoiceReportType = mReportTypeValues[position];
            }
        });
        new BasicDialog.Builder(mActivity).setTitle(R.string.platform_query).setIcon(R.drawable.pointer).setView(view).setNegativeButton(R.string.total_total, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                totalReport();
            }

        }).setPositiveButton(R.string.open, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String filePath = reportPath + mReportName + "_" + mChoiceReportType + ".xlsx";
                openFile(filePath);
            }
        }, false).setNeutralButton(R.string.download, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String saveFilePath = reportPath + mReportName + "_" + mChoiceReportType + ".xlsx";
                new DownloadThread(mChoiceReportType, mReportName, saveFilePath, true).start();
            }
        }).setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                isShowPlatformQueryDialog = false;
            }

        }).show();
    }

    /**
     * 格式化服务器端需要的文件名
     *
     * @return 不带后缀名的文件名称
     */
    protected String formatFileName(String fileName) {
        System.out.println(TAG + " fileName:" + fileName);
        String data = "";
        boolean isCQT = false;
        int start = 0;
        if (fileName.indexOf("IN") >= 0) {
            isCQT = true;
            start = fileName.indexOf("IN") + 2;
            data = fileName.substring(start, start + 15);
        } else if (fileName.indexOf("OUT") >= 0) {
            start = fileName.indexOf("OUT") + 3;
            data = fileName.substring(start, start + 15);
        } else {
            return fileName;
        }
        String taskType = "";
        if (fileName.indexOf("_Port") > 0) {
            taskType = fileName.substring(fileName.lastIndexOf("_Port") + 1);
        } else {
            if (isCQT) {
                taskType = fileName.substring(fileName.indexOf("IN") + 18);
            } else {
                taskType = fileName.substring(fileName.indexOf("OUT") + 19);
            }
        }
        this.mFileNamePrefix = "";
        if (fileName.indexOf("IN") > 0) {
            this.mFileNamePrefix = fileName.substring(0, fileName.indexOf("-IN"));
        } else if (fileName.indexOf("OUT") > 0) {
            this.mFileNamePrefix = fileName.substring(0, fileName.indexOf("-OUT"));
        }
        StringBuilder name = new StringBuilder();
        if (this.mFileNamePrefix.length() > 0)
            name.append(this.mFileNamePrefix).append("-");
        name.append(taskType).append("-").append(isCQT ? "IN" : "OUT").append("-");
        name.append(this.formatDateString(data));
        System.out.println(TAG + " formatFileName:" + name.toString());
        return name.toString();
    }

    /**
     * 转换日期格式
     *
     * @param dateStr 旧日期格式
     * @return 新日期格式
     */
    protected String formatDateString(String dateStr) {
        try {
            return this.mNewDF.format(this.mOldDF.parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr;
        }
    }

    /**
     * 打开选择的文件
     *
     * @param filePath
     */
    private void openFile(String filePath) {
        LogUtil.d(TAG, "-----openFile:" + filePath + "-----");
        File file = new File(filePath);
        if (!file.exists())
            return;
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        this.startActivity(intent);
    }

    /**
     * 统计当前的报表
     */
    private void totalReport() {
        for (int i = 0; i < mReportTypeValues.length; i++) {
            new DownloadThread(mReportTypeValues[i], this.mReportName, null, false).start();
        }
    }

    /**
     * 重命名dialog
     *
     * @param dataModel
     */
    private void showRenameDialog(final DataModel dataModel) {
        View view = inflater.inflate(R.layout.alert_dialog_edittext, null);
        final EditText editText = (EditText) view.findViewById(R.id.alert_textEditText);
        editText.setVisibility(View.VISIBLE);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setHint(R.string.rename);
        editText.setText(dataModel.testRecord.file_name);
        editText.requestFocus();

        new BasicDialog.Builder(mActivity).setTitle(R.string.rename).setView(view).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = editText.getText().toString().trim();
                // if (Verify.checkChar(newName)) {//暂时取消，因为有括号会不通过
                dataModel.rename(newName);
                // 更新数据库操作
                DataManagerFileList.getInstance(mActivity).refreshFileName(newName);
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_RENAME_SUCCEED;
                msg.obj = obtainString(R.string.total_success);
                mHandler.sendMessage(msg);
                // } else {
                // Toast.makeText(mActivity,
                // getResources().getString(R.string.monitor_inputPosition),
                // Toast.LENGTH_SHORT).show();
                // }
            }
        }).show();
    }

    /**
     * 弹出数据分割对话框
     */
    private void showDataPartitionDialog(final DataModel d) {
        LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
        View vDialog = layoutInflater.inflate(R.layout.alert_dialog_show_datapartition, null);
        startTimeTxt = (TextView) vDialog.findViewById(R.id.start_time_txt);
        endTimeTxt = (TextView) vDialog.findViewById(R.id.end_time_txt);
        RelativeLayout startTime = (RelativeLayout) vDialog.findViewById(R.id.start_time);
        final long dataStartTime = d.testRecord.time_create;
        final long dataEndTime = d.testRecord.time_end;
        final String dataStartTimeStr = UtilsMethod.sdFormatss.format(dataStartTime);
        final String dataEndTimeStr = UtilsMethod.sdFormatss.format(dataEndTime);
        startTimeTxt.setText(dataStartTimeStr);
        endTimeTxt.setText(dataEndTimeStr);
        startTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showDateTimePicker(v, dataStartTime, Html.fromHtml("<font color=white>Please set a date </font>" + "<font color=#28aae2>" + dataStartTimeStr + "</font>" + " to " + "<font color=#28aae2>" + dataEndTimeStr + "</font>"));
            }
        });

        RelativeLayout endTime = (RelativeLayout) vDialog.findViewById(R.id.end_time);
        endTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showDateTimePicker(v, dataEndTime, Html.fromHtml("<font color=white>Please set a date </font>" + "<font color=#28aae2>" + UtilsMethod.sdFormatss.format(dataStartTime) + "</font>" + " to " + "<font color=#28aae2>" + UtilsMethod.sdFormatss.format(dataEndTime) + "</font>"));
            }
        });
        new BasicDialog.Builder(mActivity).setTitle(R.string.sys_setting_data_partition).setIcon(R.drawable.pointer)
                .setView(vDialog).setNegativeButton(R.string.str_cancle)
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataPartitionThread(d, UtilsMethod.getSeconds(startTimeTxt.getText().toString()),
                                UtilsMethod.getSeconds(endTimeTxt.getText().toString())); // 分割线程
                    }
                }).show();
    }

    /**
     * 分割文件线程
     */
    private void dataPartitionThread(final DataModel dataModel, final long dataStartTime, final long dataEndTime) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DingliRcu.initMergeOrDivideFile();
                DingliRcu.addDivideRcuByTime(dataStartTime, dataEndTime);
                File file = new File(dataModel.getFilePath(FileType.DCF.getFileTypeName()));
                String newFileName = generatePartitionFileName(dataModel.testRecord.file_name);
                int isSuccess = DingliRcu.divideRcus(file.getPath(), file.getParent());
//				System.out.println("Partition--" + file.getPath() + "--" + file.getParent());
                Message msg = mHandler.obtainMessage();
                if (isSuccess >= 0) {
                    msg.what = MSG_Partition_SUCCESS;
                    msg.obj = "Partition Success";
                    mHandler.sendMessage(msg);
                } else {
                    msg.what = MSG_Partition_FAIL;
                    msg.obj = "Partition Fail";
                    mHandler.sendMessage(msg);
                }

                createNewTestRecord(dataModel.testRecord, dataStartTime, dataEndTime, newFileName);

            }
        }).start();
    }

    /**
     * 数据分割后生成新的testRecord
     *
     * @param oldTestRecord
     * @param dataStartTime
     * @param dataEndTime
     */
    @SuppressWarnings("unchecked")
    private void createNewTestRecord(TestRecord oldTestRecord, long dataStartTime, long dataEndTime, String newFileName) {
//		String newFileName = "Partition_" + oldTestRecord.file_name + "_" + DateUtil.Y_M_D_H_M_S.format(new Date());
        TestRecord testRecord = new TestRecord();
        testRecord.record_id = UtilsMethod.getUUID();
        testRecord.file_name = newFileName;
        testRecord.file_split_id = mDbManager.getMaxFileSplitId(oldTestRecord.task_no) + 1;
        testRecord.node_id = oldTestRecord.node_id;
        testRecord.port_id = oldTestRecord.port_id;
        // testRecord.task_no =
        // UtilsMethod.sdfhmsss.format(System.currentTimeMillis());
        testRecord.task_no = oldTestRecord.task_no;
        testRecord.test_index = oldTestRecord.test_index;
        testRecord.test_type = oldTestRecord.test_type;
        testRecord.time_create = dataStartTime / 1000;
        testRecord.time_end = dataEndTime / 1000;
        testRecord.type_scene = oldTestRecord.type_scene;
        testRecord.setRecordAbnormals(oldTestRecord.getRecordAbnormals());

        String filePath = oldTestRecord.getRecordDetails().get(0).file_path;
        String fileName = newFileName + FileType.DCF.getExtendName();
        RecordDetail recordDetail = new RecordDetail();

        recordDetail.file_name = fileName;
        recordDetail.file_guid = oldTestRecord.record_id + "_" + FileType.DCF.getFileTypeId();
        recordDetail.file_path = filePath;

        long fileLength = 0;
        File f = new File(filePath + fileName);
        if (f.exists()) {
            fileLength = f.length();
        }
        recordDetail.file_size = fileLength;
        recordDetail.file_type = FileType.DCF.getFileTypeId();
        recordDetail.record_id = testRecord.record_id;
        ArrayList<RecordDetail> rds = new ArrayList<RecordDetail>();
        rds.add(recordDetail);
        testRecord.setRecordDetails(rds);

        ArrayList<RecordNetType> newRecordNetTypes = (ArrayList<RecordNetType>) oldTestRecord.getRecordNetTypes().clone();
        for (RecordNetType recordNetType : newRecordNetTypes) {
            recordNetType.record_id = testRecord.record_id;
        }
        testRecord.setRecordNetTypes(newRecordNetTypes);

        ArrayList<RecordTaskType> newRecordTaskTypes = (ArrayList<RecordTaskType>) oldTestRecord.getRecordTaskTypes().clone();
        for (RecordTaskType recordTaskType : newRecordTaskTypes) {
            recordTaskType.task_type_id = 0;
            recordTaskType.record_id = testRecord.record_id;
        }
        testRecord.setRecordTaskTypes(newRecordTaskTypes);
        testRecord.setRecordTestInfo(oldTestRecord.getRecordTestInfo());
        DataManagerFileList.getInstance(mActivity).insertFile(testRecord);

    }

    /**
     * 数据合并后生成新的testRecord
     *
     * @param mergeList
     */
    @SuppressWarnings("unchecked")
    private void createNewTestRecord(List<DataModel> mergeList, String newFileName) {
        TestRecord oldTestRecord = mergeList.get(0).testRecord;
        long tmpTaskNum = Long.parseLong(oldTestRecord.task_no);
        long taskNumSum = 0;
        for (int i = 0; i < mergeList.size(); i++) {
            long task_no = Long.parseLong(mergeList.get(i).testRecord.task_no);
            taskNumSum += task_no;
        }
        int fileSplitId = 1;
        String task_no = "";
        if (tmpTaskNum * mergeList.size() - taskNumSum == 0) {//在同一个文件夹中
            fileSplitId = mDbManager.getMaxFileSplitId(oldTestRecord.task_no) + 1;
            task_no = oldTestRecord.task_no;
        } else {
            fileSplitId = 1;
            task_no = UtilsMethod.sdfhmsss.format(System.currentTimeMillis());
            ;
        }
//		String newFileName = generateMegerFileName();
//		long createTime = mergeList.get(0).testRecord.time_create;
//		long endTime = mergeList.get(mergeList.size() - 1).testRecord.time_end;
        long createTime = new Date().getTime();
        long endTime = createTime;
        TestRecord testRecord = new TestRecord();
        testRecord.record_id = UtilsMethod.getUUID();
        testRecord.file_name = newFileName;
        testRecord.file_split_id = fileSplitId;
        testRecord.node_id = oldTestRecord.node_id;
        testRecord.port_id = oldTestRecord.port_id;
        testRecord.task_no = task_no;

        testRecord.test_index = oldTestRecord.test_index;
        testRecord.test_type = oldTestRecord.test_type;
        testRecord.time_create = createTime;
        testRecord.time_end = endTime;
        testRecord.type_scene = oldTestRecord.type_scene;
        testRecord.setRecordAbnormals(oldTestRecord.getRecordAbnormals());

        String filePath = oldTestRecord.getRecordDetails().get(0).file_path;
        String fileName = newFileName + FileType.DCF.getExtendName();
        RecordDetail recordDetail = new RecordDetail();

        recordDetail.file_name = fileName;
        recordDetail.file_guid = oldTestRecord.record_id + "_" + FileType.DCF.getFileTypeId();
        recordDetail.file_path = filePath;

        long fileLength = 0;
        File f = new File(filePath + fileName);
        if (f.exists()) {
            fileLength = f.length();
        }
        recordDetail.file_size = fileLength;
        recordDetail.file_type = FileType.DCF.getFileTypeId();
        recordDetail.record_id = testRecord.record_id;
        ArrayList<RecordDetail> rds = new ArrayList<RecordDetail>();
        rds.add(recordDetail);
        testRecord.setRecordDetails(rds);
        // testRecord.setRecordNetTypes(oldTestRecord.getRecordNetTypes());
        // testRecord.setRecordTaskTypes(oldTestRecord.getRecordTaskTypes());

        ArrayList<RecordNetType> newRecordNetTypes = (ArrayList<RecordNetType>) oldTestRecord.getRecordNetTypes().clone();
        recordDetail.record_id = testRecord.record_id;
        testRecord.setRecordNetTypes(newRecordNetTypes);

        ArrayList<RecordTaskType> newRecordTaskTypes = (ArrayList<RecordTaskType>) oldTestRecord.getRecordTaskTypes().clone();
        for (RecordTaskType recordTaskType : newRecordTaskTypes) {
            recordTaskType.task_type_id = 0;
            recordTaskType.record_id = testRecord.record_id;
        }
        testRecord.setRecordTaskTypes(newRecordTaskTypes);
        testRecord.setRecordTestInfo(oldTestRecord.getRecordTestInfo());
        DataManagerFileList.getInstance(mActivity).insertFile(testRecord);
    }

    private String generateMegerFileName() {
        String result = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        result = "Merger_Android_" + df.format(new Date());
        return result;
    }

    private String generatePartitionFileName(String oldFileName) {
        return "Partition_" + oldFileName + "_" + DateUtil.Y_M_D_H_M_S.format(new Date());
    }

    /**
     * 合并数据操作线程
     *
     * @param mergeList
     */
    private void mergeDataThr(final List<DataModel> mergeList) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                DingliRcu.initMergeOrDivideFile();
                for (int i = 0; i < mergeList.size(); i++) {
                    DingliRcu.addMergeOrDivideFile(mergeList.get(i).getFilePath(FileType.DCF.getFileTypeName()));
//					System.out.println("MergeFile---" + mergeList.get(i).getFilePath(FileType.DCF.getFileTypeName()));
                }
                File file = new File(mergeList.get(0).getFilePath(FileType.DCF.getFileTypeName()));
                String newFileName = generateMegerFileName();
                //当选择的是非RCU时，file.getParent()为空，调用DingliRcu.mergeRcus(file.getParent())导致崩溃
                if (TextUtils.isEmpty(file.getParent())) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity, getString(R.string.only_rcu_data_can_be_merged), Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                int isSuccess = DingliRcu.mergeRcus(file.getParent());
                Message msg = mHandler.obtainMessage();
                if (isSuccess >= 0) {
                    msg.what = MSG_MERGE_SUCCESS;
                    msg.obj = "Merger Success";
                    mHandler.sendMessage(msg);
                } else {
                    msg.what = MSG_MERGE_FAIL;
                    msg.obj = "Merger Fail";
                    mHandler.sendMessage(msg);
                }

                createNewTestRecord(mergeList, newFileName);

            }
        }).start();
    }

    /**
     * 弹出自定义时间选择器
     */

    private void showDateTimePicker(final View v, long time, Spanned tipStr) {
        DisplayMetrics metric = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View view = inflater.inflate(R.layout.date_time_layout, null);
        TextView showDateTip = (TextView) view.findViewById(R.id.show_tip_massage);
        showDateTip.setText(tipStr);
        final WheelMain main = new WheelMain(view);
        main.setTime(time);
        new BasicDialog.Builder(mActivity).setTitle(R.string.data_choose_data_time).setIcon(R.drawable.pointer).setView(main.showDateTimePicker()).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                choutDate = main.getTime();
                switch (v.getId()) {
                    case R.id.start_time:
                        fromIndex = mDataset.getPointIndexFromTime(UtilsMethod.getSeconds(choutDate));
                        // Log.i(tag, "---start---" + choutDate + "----" + fromIndex);
                        startTimeTxt.setText(UtilsMethod.sdFormatss.format(UtilsMethod.getSeconds(choutDate) / 1000));
                        break;
                    case R.id.end_time:
                        toIndex = mDataset.getPointIndexFromTime(UtilsMethod.getSeconds(choutDate));
                        // Log.i(tag, "---end---" + choutDate + "----" + toIndex);
                        endTimeTxt.setText(UtilsMethod.sdFormatss.format(UtilsMethod.getSeconds(choutDate) / 1000));
                        break;

                    default:
                        break;
                }
                // 设置时间
            }
        }).setNegativeButton(R.string.str_cancle).show();

    }

    // *******************************************自定义导出
    // begin**************************************************
    private String savePathExport;
    private long dataExportStart = System.currentTimeMillis();

    private long dataExportEnd = System.currentTimeMillis();
    private ProgressDialog dataDialog;
    private BasicDialog showCustomDialog = null;

    private void showDialog(String strTip) {
        dataDialog = new ProgressDialog(mActivity);
        dataDialog.setMessage(strTip);
        dataDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        dataDialog.show();
    }

    /**
     * 自定义导出异步线程
     *
     * @author zhihui.lian
     */

    class CustomExport extends AsyncTask<Object, Object, Object> {

        @Override
        protected Object doInBackground(Object... params) {
            DataModel dataModel = (DataModel) params[0];
            dataCustomIndexToTime(dataModel);
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            dataDialog.cancel();
            LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
            View vDialog = layoutInflater.inflate(R.layout.alert_dialog_customexport, null);
            final RelativeLayout showPara = (RelativeLayout) vDialog.findViewById(R.id.show_para_ral);
            final RelativeLayout showPara2 = (RelativeLayout) vDialog.findViewById(R.id.show_para_show_ral);
            final Spinner netWorkSp = (Spinner) vDialog.findViewById(R.id.data_custom_notwork_type);
            ArrayAdapter<String> adpAP = new ArrayAdapter<String>(mActivity, R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_custom_export_network));
            adpAP.setDropDownViewResource(R.layout.spinner_dropdown_item);
            netWorkSp.setAdapter(adpAP);
            final Spinner formatSp = (Spinner) vDialog.findViewById(R.id.data_custom_format_type);
            ArrayAdapter<String> formatAPa = new ArrayAdapter<String>(mActivity, R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_custom_export_format));
            formatAPa.setDropDownViewResource(R.layout.spinner_dropdown_item);
            formatSp.setAdapter(formatAPa);
            netWorkSp.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    ConfigParmExport.getSingleInstance().setParmByNetWork(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
            RelativeLayout startTime = (RelativeLayout) vDialog.findViewById(R.id.start_time);
            RelativeLayout endTime = (RelativeLayout) vDialog.findViewById(R.id.end_time);
            final CheckBox event = (CheckBox) vDialog.findViewById(R.id.custom_check_event); // 事件
            final CheckBox l3msg = (CheckBox) vDialog.findViewById(R.id.custom_check_l3msg); // 信令
            final CheckBox parm = (CheckBox) vDialog.findViewById(R.id.custom_check_parm); // 参数
            final CheckBox gps = (CheckBox) vDialog.findViewById(R.id.custom_check_gps); // 参数
            parm.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (parm.isChecked()) {
                        showPara.setVisibility(View.VISIBLE);
                        showPara2.setVisibility(View.VISIBLE);
                    } else {
                        showPara.setVisibility(View.GONE);
                        showPara2.setVisibility(View.GONE);
                    }

                }
            });
            final Button parmSetting = (Button) vDialog.findViewById(R.id.parm_btn); // 参数设置
            parmSetting.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showExportParmDialog();
                }
            });

            startTimeTxt = (TextView) vDialog.findViewById(R.id.start_time_txt);
            endTimeTxt = (TextView) vDialog.findViewById(R.id.end_time_txt);
            startTimeTxt.setText(UtilsMethod.sdFormatss.format(dataExportStart / 1000));
            endTimeTxt.setText(UtilsMethod.sdFormatss.format(dataExportEnd / 1000));

            startTime.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showDateTimePicker(v, dataExportStart, Html.fromHtml("<font color=white>Please set a date </font>" + "<font color=#28aae2>" + UtilsMethod.sdFormatss.format(dataExportStart / 1000) + "</font>" + " to " + "<font color=#28aae2>" + UtilsMethod.sdFormatss.format(dataExportEnd / 1000) + "</font>"));
                }
            });
            endTime.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showDateTimePicker(v, dataExportEnd, Html.fromHtml("<font color=white>Please set a date </font>" + "<font color=#28aae2>" + UtilsMethod.sdFormatss.format(dataExportStart / 1000) + "</font>" + " to " + "<font color=#28aae2>" + UtilsMethod.sdFormatss.format(dataExportEnd / 1000) + "</font>"));
                }
            });

            showCustomDialog = new BasicDialog.Builder(mActivity).setIcon(R.drawable.pointer).setTitle(R.string.custom_export_titile).setView(vDialog).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dataCustomImport(Integer.parseInt(getExportConfig(gps.isChecked(), event.isChecked(), l3msg.isChecked(), parm.isChecked(), netWorkSp.getSelectedItemPosition()), 16), getResources().getStringArray(R.array.array_custom_export_format)[formatSp.getSelectedItemPosition()]);

                }
            }).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    mDataset.closePlayback(DatasetManager.PORT_4);
                }
            }).show();
            showCustomDialog.setOnKeyListener(new OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                        if (showCustomDialog != null && showCustomDialog.isShowing()) {
                            showCustomDialog.cancel();
                            mDataset.closePlayback(DatasetManager.PORT_4);
                        }
                    }

                    return false;
                }
            });

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(getString(R.string.file_import_data_ing));
        }

    }

    /**
     * 获取导出文件时间与采样点（互转）
     */

    private void dataCustomIndexToTime(DataModel dataModel) {

        savePathExport = "";
        savePathExport = dataModel.testRecord.file_name;
        mDataset.openPlaybackData(DatasetManager.PORT_4, dataModel.getFilePath(FileType.DDIB.getFileTypeName()));
        dataExportStart = 0;
        dataExportEnd = 0;
        dataExportStart = mDataset.getPointTime(0);
        dataExportEnd = mDataset.getPointTime(mDataset.getTotalPointCount(DatasetManager.PORT_4));
        fromIndex = mDataset.getPointIndexFromTime(dataExportStart);
        toIndex = mDataset.getPointIndexFromTime(dataExportEnd);
    }

    /**
     * 弹出导出参数选择对话框
     */
    private void showExportParmDialog() {
        BasicDialog.Builder builder = new BasicDialog.Builder(mActivity);
        ListView SingleChoice2g = new ListView(mActivity);
        SingleChoice2g.setDivider(this.getResources().getDrawable(R.drawable.list_divider));
        SingleChoice2g.setCacheColorHint(Color.TRANSPARENT);

        final ArrayList<ExportParmModel> parameters2g = ConfigParmExport.getSingleInstance().getParmModelList();
        ExportMutilChoiceAdapter adapter2g = new ExportMutilChoiceAdapter(mActivity, R.layout.simple_list_item_multiple_choice, parameters2g);
        SingleChoice2g.setAdapter(adapter2g);
        SingleChoice2g.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView = ((CheckedTextView) view);
                parameters2g.get(position).setEnable(checkedTextView.isChecked() ? 1 : 0);
                ConfigParmExport.getSingleInstance().setExporParmEnable(position, !checkedTextView.isChecked() ? 1 : 0);
                checkedTextView.setChecked(!checkedTextView.isChecked());
            }
        });
        builder.setView(SingleChoice2g);
        builder.show();
    }

    /**
     * 文件自定义导出
     */
    private ExportTask exportTask;

    private void dataCustomImport(final int exportConfig, final String fileSuffix) {
        final String paraDatePath = AppFilePathUtil.getInstance().getSDCardBaseDirectory(getString(R.string.path_data),getString(R.string.path_custom)) + savePathExport + fileSuffix;
        if (fileSuffix.endsWith("xml")) {
            exportTask = new ExportTask(paraDatePath, fromIndex, toIndex);
            exportTask.execute();
        } else {
            showDialog("Exporting File");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mDataset.parmCustomExportFile(paraDatePath, exportConfig, fromIndex, toIndex, Integer.MAX_VALUE, filterCodeArray(), fileteCodeArr.length, parmCodeArray());
                    mDataset.closePlayback(DatasetManager.PORT_4);
                    Message msg = mHandler.obtainMessage(MSG_CUSTOM_EXPORT);
                    msg.obj = "The exported " + savePathExport + fileSuffix + " to Walktour/Data/export";
                    msg.sendToTarget();
                }
            }
            ).start();
        }
    }

    private boolean isCanCancell = true;


    class ExportTask extends AsyncTask<Integer, Integer, String> {

        private String xmlFilePath;
        private int iFromPointIndex;
        private int iToPointIndex;
        private ProgressDialog progressDialog;


        private ExportTask(String xmlFilePath, int iFromPointIndex, int iToPointIndex) {
            this.xmlFilePath = xmlFilePath;
            this.iFromPointIndex = iFromPointIndex;
            this.iToPointIndex = iToPointIndex;
        }


        @Override
        protected String doInBackground(Integer... params) {
            isCanCancell = true;
            String returnCode = exportL3Detail(xmlFilePath, iFromPointIndex, iToPointIndex, FragmentBase.ExportTask.this);
            return returnCode;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("Exporting File");
            progressDialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (isCanCancell) {
                        exportTask.cancel(true);            //完美取消操作
                        isCanCancell = false;
                    }
                    mDataset.closePlayback(DatasetManager.PORT_4);
                }
            });
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            isCanCancell = false;
            progressDialog.dismiss();
            if (result.length() != 0) {
                Toast.makeText(mActivity, result, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }


    }


    /**
     * 导出信令详细解码XML 根据采样点获取信令详细解码
     */

    private String exportL3Detail(String xmlFilePath, int iFromPointIndex, int iToPointIndex, FragmentBase.ExportTask exportTask) {

        try {
            String signalStr = mDataset.getMsgCode(iFromPointIndex, iToPointIndex, true, false);
            String[] signalStrArray = signalStr.split("##");
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\r");
            stringBuffer.append("<DetailMessage>\n\r");
            StringBuffer stringBufferDetail = new StringBuffer();
            File file = new File(xmlFilePath);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(file, true);
            out.write(stringBuffer.toString().getBytes());
            for (int i = 0; i < signalStrArray.length; i++) {
                if (isCanCancell) {
                    String[] signalSplitArray = signalStrArray[i].split("@@");
                    if (signalSplitArray != null && signalSplitArray.length != 0) {
                        String pointIndexStr = signalSplitArray[0];
                        if (pointIndexStr != null && pointIndexStr.length() != 0) {
                            exportTask.progressDialog.setProgress((int) (((double) i / (double) signalStrArray.length) * 100));
                            String l3DetailStr = mDataset.queryL3Detail(Integer.parseInt(pointIndexStr));
                            if (l3DetailStr.trim().length() != 0) {
                                stringBufferDetail.append(l3DetailStr);
                                if (stringBufferDetail.toString().getBytes().length > 2.5 * 1024 * 1024) {        //判断是否大于2.5M
                                    out.write(stringBufferDetail.toString().getBytes());
                                    stringBufferDetail = new StringBuffer();
                                }

                            }
                        }
                    }
                }
            }
            out.write((stringBufferDetail + "</DetailMessage>\n\r").toString().getBytes());
            out.close();
            return "The exported " + file.getName() + " to Walktour/Data/export";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获得信令过滤code数组
     */

    private long[] fileteCodeArr = null;

    private long[] filterCodeArray() {
        try {
            List<String> filterCodeList = MsgFilterSettingFactory.getInstance().getFilterCodeList();
            fileteCodeArr = new long[filterCodeList.size()];
            for (int i = 0; i < filterCodeList.size(); i++) {
                fileteCodeArr[i] = Long.parseLong(filterCodeList.get(i).replace("0x", ""), 16);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileteCodeArr;
    }

    /**
     * 获得参数过滤数组code
     */

    private long[] parmCodeArr = null;

    private long[] parmCodeArray() {
        try {
            ArrayList<ExportParmModel> parmModelList = ConfigParmExport.getSingleInstance().getParmModelList();
            ArrayList<String> parmIdList = new ArrayList<String>();
            for (int i = 0; i < parmModelList.size(); i++) {
                if (parmModelList.get(i).getEnable() == 1) {
                    parmIdList.add(parmModelList.get(i).getId());
                }
            }
            parmCodeArr = new long[parmIdList.size()];
            for (int i = 0; i < parmIdList.size(); i++) {
                parmCodeArr[i] = Long.parseLong(parmIdList.get(i).replace("0x", ""), 16);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parmCodeArr;
    }

    /**
     * 导出配置config
     * <p>
     * //bit位标识：bool bGPS, bool bGsmPara, bool bWcdmaPara, bool bTDSDMAPara, bool bLTEPara, bool bEvent,bool bL3 // 6 5 4 3 2 1 0
     */

    private String getExportConfig(boolean isGps, boolean isEvent, boolean isL3, boolean isPara, int netType) {

        String config = "1100011"; // 默认配置
        StringBuffer conBuf = new StringBuffer(config);
        int length = conBuf.length();

        if (isGps) {
            conBuf.replace(0, 1, "1");
        } else {
            conBuf.replace(0, 1, "0");
        }
        if (isEvent) {
            conBuf.replace(length - 2, length - 1, "1");
        } else {
            conBuf.replace(length - 2, length - 1, "0");
        }
        if (isL3) {
            conBuf.replace(length - 1, length, "1");
        } else {
            conBuf.replace(length - 1, length, "0");
        }
        if (isPara) {
            for (int i = 0; i < conBuf.length(); i++) {
                if (i > 0 && i < length - 2) {
                    if (i == netType + 1) {
                        conBuf.replace(i, i + 1, "1");
                    } else {
                        conBuf.replace(i, i + 1, "0");
                    }
                }
            }
        } else {
            conBuf.replace(length - 6, length - 2, "0000");
        }
        return toHexString(conBuf.toString());

    }

    /**
     * 二进制转为16进制
     *
     * @param binaryString
     * @return
     */
    public String toHexString(String binaryString) {
        StringBuilder buf = new StringBuilder();
        for (int i = binaryString.length(); i >= 0; i -= 4) {
            buf.insert(0, String.format("%X", Integer.valueOf(binaryString.substring(Math.max(0, i - 4), i), 2)));
        }
        return buf.toString();
    }

    // *******************************************自定义导出
    // end*****************************************************
    // *******************************************预览轨迹
    // begin******************************************************

    /**
     * 显示预览轨迹对话框
     *
     * @param fileModel 文件对象
     */
    @SuppressWarnings("deprecation")
    private void showPreviewLocusDialog(DataModel fileModel) {
        String filePath = fileModel.getFilePath(FileType.LOCUSJPEG.getFileTypeName());
        File imgFile = new File(filePath);
        if (imgFile.isFile()) {
            BitmapFactory.Options opts = new Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, opts);
            opts.inSampleSize = UtilsMethod.computeSuitedSampleSize(opts, 480 * 800);
            opts.inJustDecodeBounds = false;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, opts);
            LayoutInflater lif = LayoutInflater.from(mActivity);
            View view = lif.inflate(R.layout.alert_dialog_imageview, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.ImageView01);
            imageView.setImageBitmap(bmp);
            DisplayMetrics metric = new DisplayMetrics();
            mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
            new BasicDialog.Builder(mActivity).setTitle(R.string.data_manage_preview_locus).setView(view, new LayoutParams(LayoutParams.FILL_PARENT, (int) (400 * metric.density))).setPositiveButton(R.string.str_return, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } else {
            Toast.makeText(mActivity, R.string.data_manage_preview_locus_fail, Toast.LENGTH_SHORT).show();
        }
    }

    // *******************************************预览轨迹
    // end********************************************************
    // *******************************************预览事件
    // begin******************************************************
    private ProgressDialog progressDialog;

    /**
     * 显示预览事件对话框
     *
     * @param fileModel 文件对象
     */
    private void showPreviewEventDialog(DataModel fileModel) {
        if (DatasetManager.isPlayback || DatasetManager.isTesting) {
            Toast.makeText(mActivity, R.string.data_manage_preview_event_fail, Toast.LENGTH_SHORT).show();
            return;
        }
        final String filePath = fileModel.getFilePath(FileType.DDIB.getFileTypeName());
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(mActivity, R.string.sys_indoor_notfound, Toast.LENGTH_SHORT).show();
            return;
        }
        this.showProgressDialog(getResources().getString(R.string.main_init_loading), false);
        new Thread() {
            public void run() {
                clearAllData();
                boolean flag = mDataset.openPlayback(DatasetManager.PORT_4, filePath);
                uiHandler.sendEmptyMessage(flag ? MSG_REVIEW_EVENT_SUCCEED : MSG_REVIEW_EVENT_FAIL);
            }

            ;
        }.start();

    }

    private void showPreviewEventDialog(boolean flag) {
        if (flag) {
            if (this.progressDialog != null)
                this.progressDialog.dismiss();
            LayoutInflater lif = LayoutInflater.from(mActivity);
            View view = lif.inflate(R.layout.alert_dialog_listview, null);
            ListViewForScrollView listView = (ListViewForScrollView) view.findViewById(R.id.ListView);
            EventIndexAdapter eventResultAdapter = new EventIndexAdapter(mActivity, EventManager.getInstance().getEventList(), false, true);
            listView.setAdapter(eventResultAdapter);
            mDataset.closePlayback(DatasetManager.PORT_4);
            new BasicDialog.Builder(mActivity).setTitle(R.string.data_manage_preview_event).setView(view).setPositiveButton(R.string.str_return, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } else {
            if (this.progressDialog != null)
                this.progressDialog.dismiss();
            Toast.makeText(mActivity, R.string.sys_indoor_notfound, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void showProgressDialog(String message, boolean cancleable) {
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage(message);
        // progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(cancleable);
        progressDialog.show();
    }

    /**
     * 清除预览时间
     */
    private void clearAllData() {
        TraceInfoInterface.traceData.getGpsLocas().clear();
        MapFactory.getMapData().getPointStatusStack().clear();
        MapFactory.getMapData().getEventQueue().clear();
        EventManager.getInstance().clearEvents();
        TraceInfoInterface.traceData.l3MsgList.clear();
        AlertManager.getInstance(mActivity).clearAlarms(true);

    }

    // *******************************************预览事件
    // end********************************************************

    /**
     * 刷新数据前先清理数据
     */
    protected void clear() {
        if (mAdapter != null) {
            mAdapter.getDatas().clear();
            mAdapter.notifyDataSetChanged();
            root = TreeNode.root();
            mAdapter.setRoot(root);
        }
    }

    /**
     * 过滤条件("%s.net_type in('6006','6001')")
     *
     * @param type
     * @return
     */
    protected HashMap<String, ArrayList<String>> getAllFilterCondiction(String type) {
        System.out.println("cuttent_type_flag:" + type);
        HashMap<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
        // test_scene
        ArrayList<String> testRecordSqlArray = new ArrayList<String>();
        String test_sence_value = "%s.type_scene =" + mDbManager.getTypeScene(type);
        testRecordSqlArray.add(test_sence_value);
        if (type.equals(TestType.DT.name())) {
            String testTypeSql = "%s.test_type = 5001 ";
            testRecordSqlArray.add(testTypeSql);
        } else if (type.equals(TestType.CQT.name())) {
            String testTypeSql = "%s.test_type = 5002 ";
            testRecordSqlArray.add(testTypeSql);
        }

        boolean isScreeningSetting = sp.getBoolean(FilterKey.KEY_IS_SCREEN_SETTING + type, false);
        boolean isCheckSetting = sp.getBoolean(FilterKey.KEY_IS_CHECK_SETTING + type, false);
        boolean isFilterSetting = sp.getBoolean(FilterKey.KEY_IS_FILTER_SETTING + type, false);
        boolean isFloorSetting = sp.getBoolean(FilterKey.KEY_IS_FLOOR_SETTING + type, false);
        boolean isWorkOrderSetting = sp.getBoolean(FilterKey.KEY_IS_WORK_ORDER_SETTING + type, false);
        boolean isRoute = sp.getBoolean(FilterKey.KEY_IS_ROUTE_SETTING + type, false);
        // 设置了筛选================================================================================================
        if (isScreeningSetting) {
            // 时间----------------------------------------------------------------------------------------------------------------------------------
            int checkPosition = sp.getInt(FilterKey.KEY_TIME_POSITION + type, -1);
            if (checkPosition != -1) {
                String[] timeRange = null;
                if (checkPosition == 3) {
                    String timeRangeStr = sp.getString(FilterKey.KEY_TIME_RANGE + type, "");
                    if (!timeRangeStr.equals("")) {
                        timeRange = timeRangeStr.split("~");
                    }
                }
                System.out.println("时间选项:" + checkPosition);
                long[] times = DateUtil.getTimeRange(checkPosition, timeRange);
                String timeSql = "%s.time_create between" + " '" + times[0] + "'" + " and" + " '" + times[1] + "'";
                testRecordSqlArray.add(timeSql);
            }

            // 上传状态--------------------------------------------------------------------------------------------------------------------------------
            ArrayList<String> recordDetailUploadArray = new ArrayList<String>();
            int uploadState = sp.getInt(FilterKey.KEY_UPLOADED_STATE + type, -100);
//			System.out.println("筛选upload state:" + uploadState);
            if (uploadState != -100) {
                if (uploadState == -1) {
                    String uploadStateSql = "%s.upload_type is null";
                    recordDetailUploadArray.add(uploadStateSql);
                } else {
                    String uploadStateSql = "%s.upload_type =" + uploadState + "";
                    recordDetailUploadArray.add(uploadStateSql);
                }
                result.put(DataTableMap.RecordDetailUpload.name(), recordDetailUploadArray);
            }
            // 业务------------------------------------------------------------------------------------------------------------------------------------------------
            ArrayList<String> recordTaskTypeArray = new ArrayList<String>();
            String businessSelectedKeys = sp.getString(FilterKey.KEY_BUSINESS_SELECTED + type, "");
            if (!businessSelectedKeys.equals("")) {
                String[] taskTypeArray = businessSelectedKeys.split(",");
                StringBuilder sb = new StringBuilder();
                for (String taskType : taskTypeArray) {
                    sb.append("'").append(taskType).append("'").append(",");
                }
                String tmp = sb.toString();
                String taskTypes = tmp.substring(0, tmp.lastIndexOf(","));
                String recordTaskTypeSql = "%s.task_type in(" + taskTypes + ")";
                recordTaskTypeArray.add(recordTaskTypeSql);
                result.put(DataTableMap.RecordTaskType.name(), recordTaskTypeArray);
            }

            // 网络---------------------------------------------------------------------------------------------------------------------------------------------------
            ArrayList<String> recordNetTypeArray = new ArrayList<String>();
            String selectedNetworkTypes = sp.getString(FilterKey.KEY_NETWORK_TYPE_SELECTED + type, "");
            if (!selectedNetworkTypes.equals("")) {
                String[] networkTypeArray = selectedNetworkTypes.split(",");
                StringBuilder sb = new StringBuilder();
                for (String networkType : networkTypeArray) {
                    sb.append("'").append(networkType).append("'").append(",");
                }
                String tmp = sb.toString();
                String networks = tmp.substring(0, tmp.lastIndexOf(","));
                String recordNetTypeSql = "%s.net_type in(" + networks + ")";
                recordNetTypeArray.add(recordNetTypeSql);
                result.put(DataTableMap.RecordNetType.name(), recordNetTypeArray);
            }
        }
        // 设置无效过滤===========================================================================================================
        if (isFilterSetting) {
            // 无效过滤 时长
            String timeLimit = sp.getString(FilterKey.KEY_TIME_LIMIT + type, "3");
            int timeLong = Integer.parseInt(timeLimit) * 60 * 1000;
            mDbManager.setInvalidTimeLong(timeLong);
        } else {
            mDbManager.setInvalidTimeLong(3 * 60 * 1000);
        }

        result.put(DataTableMap.TestRecord.name(), testRecordSqlArray);

        // 设置过智能检测===========================================================================================================
        if (isCheckSetting) {
            // 智能检测
            // 异常过滤------------------------------------------------------------------------------------------------------------------------------------
            ArrayList<String> recordAbnormalArray = new ArrayList<String>();
            String abnormalSelectedKeys = sp.getString(FilterKey.KEY_EXCEPTION_SELECTED + type, "");
            if (!abnormalSelectedKeys.equals("")) {
                String[] abnormalTypeArray = abnormalSelectedKeys.split(",");
                StringBuilder sb = new StringBuilder();
                for (String abnormalType : abnormalTypeArray) {
                    sb.append("'").append(abnormalType).append("'").append(",");
                }
                String tmp = sb.toString();
                String abnormalTypes = tmp.substring(0, tmp.lastIndexOf(","));
                String abnormalTypeSql = "%s.abnormal_type in(" + abnormalTypes + ")";
                recordAbnormalArray.add(abnormalTypeSql);
                result.put(DataTableMap.RecordAbnormal.name(), recordAbnormalArray);
            }
        }
        // 楼层
        if (isFloorSetting) {
            result.putAll(getBuildingCondiction(type));
        }
        // 工单
        if (isWorkOrderSetting) {
            result.putAll(getWorkOrderCondiction(type));
        }
        // 线路
        if (isRoute) {
            if (ApplicationModel.getInstance().getSelectScene() == WalkStruct.SceneType.Metro) {//地铁
                result.putAll(getMetroCondiction(type));
            } else if (ApplicationModel.getInstance().getSelectScene() == WalkStruct.SceneType.HighSpeedRail) {//高铁
                result.putAll(getHighSpeedRailCondiction(type));
            }
        }
        return result;
    }

    /**
     * 建筑物过滤条件("%s.net_type in('6006','6001')")
     *
     * @param type
     * @return
     */
    protected HashMap<String, ArrayList<String>> getBuildingCondiction(String type) {
        HashMap<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
        ArrayList<String> recordBuildArray = new ArrayList<String>();
        String selectedFloorNodeIds = sp.getString(FilterKey.KEY_FLOOR_SELECTED + type, "");
        if (!selectedFloorNodeIds.equals("")) {
            String[] node_id_array = selectedFloorNodeIds.split(",");
            StringBuilder sb = new StringBuilder();
            for (String node_id : node_id_array) {
                sb.append("'").append(node_id).append("'").append(",");
            }
            String tmp = sb.toString();
            String node_ids = tmp.substring(0, tmp.lastIndexOf(","));
            String recordBuildSql = "%s.node_id in(" + node_ids + ")";
            recordBuildArray.add(recordBuildSql);
            result.put(DataTableMap.RecordBuild.name(), recordBuildArray);
        }
        return result;
    }

    /**
     * 工单过滤条件("%s.net_type in('6006','6001')")
     *
     * @param type
     * @return
     */
    protected HashMap<String, ArrayList<String>> getWorkOrderCondiction(String type) {
        HashMap<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
        ArrayList<String> recordBuildArray = new ArrayList<String>();
        String selectedFloorNodeIds = sp.getString(FilterKey.KEY_WORK_ORDER_SELECTED + type, "");
        if (!selectedFloorNodeIds.equals("")) {
            String[] node_id_array = selectedFloorNodeIds.split(",");
            StringBuilder sb = new StringBuilder();
            for (String node_id : node_id_array) {
                sb.append("'").append(node_id).append("'").append(",");
            }
            String tmp = sb.toString();
            String node_ids = tmp.substring(0, tmp.lastIndexOf(","));
            String recordBuildSql = "%s.node_id in(" + node_ids + ")";
            recordBuildArray.add(recordBuildSql);
            result.put(DataTableMap.RecordBuild.name(), recordBuildArray);
        }
        return result;
    }

    /***
     * 地铁线路过滤条件
     * @param type
     * @return
     */
    protected HashMap<String, ArrayList<String>> getMetroCondiction(String type) {
        HashMap<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
        ArrayList<String> recordBuildArray = new ArrayList<String>();
        String city = sp.getString(FilterKey.KEY_CITY_SELECTED + type, "");//选择的城市
        String selectRout = sp.getString(FilterKey.KEY_ROUTE_SELECTED + type, "");//选择的线路
        List<String> taskNos = FileDB.getInstance(mActivity).getRecordTestInfoTaskNo(city, selectRout);
        if (taskNos.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String node_id : taskNos) {
                sb.append("'").append(node_id).append("'").append(",");
            }
            String tmp = sb.toString();
            String node_ids = tmp.substring(0, tmp.lastIndexOf(","));
            String recordBuildSql = "%s.task_no in(" + node_ids + ")";
            recordBuildArray.add(recordBuildSql);
            result.put(DataTableMap.TestRecord.name(), recordBuildArray);
        }
        return result;
    }

    /***
     * 高铁线路过滤条件
     * @param type
     * @return
     */
    protected HashMap<String, ArrayList<String>> getHighSpeedRailCondiction(String type) {
        HashMap<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
        ArrayList<String> recordBuildArray = new ArrayList<String>();
        String selectRout = sp.getString(FilterKey.KEY_ROUTE_SELECTED + type, "");//选择的线路
        List<String> taskNos = FileDB.getInstance(mActivity).getRecordTestInfoTaskNo(selectRout);
        if (taskNos.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String node_id : taskNos) {
                sb.append("'").append(node_id).append("'").append(",");
            }
            String tmp = sb.toString();
            String node_ids = tmp.substring(0, tmp.lastIndexOf(","));
            String recordBuildSql = "%s.task_no in(" + node_ids + ")";
            recordBuildArray.add(recordBuildSql);
            result.put(DataTableMap.TestRecord.name(), recordBuildArray);
        }
        return result;
    }

    public TreeViewAdapter getAdapter() {
        return this.mAdapter;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
        TreeNode node = mAdapter.getDatas().get(position);
        final DataModel item = (DataModel) mAdapter.getDatas().get(position).getValue();
        if (node.getChildren().size() > 0) {
            mAdapter.expandNode(node, position);
            sendUpdatePagerBroadCast();
        } else {
            if (mAdapter.isSpecialMode()) {// 回放、统计、删除模式
                if (mAdapter.isDeleteMode()) {
                    mAdapter.onCustomItemClickListener(node);
                } else {
                    mAdapter.onCustomItemClickListener(item);
                }
                mAdapter.notifyDataSetChanged();

            } else {
                DataManagerFileList.getInstance(mActivity).setOperation(item.testRecord);
                moreOptionPopWin.showMenu(v);
                moreOptionPopWin.mButtonTwo.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        showPropertyDialog(item);
                        moreOptionPopWin.closeMenu();//
                    }
                });
                moreOptionPopWin.mButtonThree.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        showEditDialog(item);
                        moreOptionPopWin.closeMenu();//
                    }
                });
            }
        }
    }

    /**
     * 收缩数据时发送广播通知界面更新
     */
    private void sendUpdatePagerBroadCast() {
        Intent intent = new Intent(FileManagerFragmentActivity.ACTION_UPDATE_PAGER);
        mActivity.sendBroadcast(intent);
    }

    public ArrayList<DataModel> getNewFileListInstance() {
        boolean isWorkorderDisplayType = sp.getBoolean(FilterKey.KEY_WORK_ORDER + FLAG, false);
        HashMap<String, ArrayList<String>> map = getAllFilterCondiction(FLAG);
        return DataManagerFileList.getInstance(mActivity).initFileList(isWorkorderDisplayType, map);
    }

    private String obtainString(int id) {
        return mActivity.getResources().getString(id);
    }

    private void registReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DataTransService.EXTRA_DATA_TRANS_FILE_PROGRESS);
        intentFilter.addAction(DataTransService.EXTRA_DATA_TRANS_FILE_END);
        intentFilter.addAction(DataTransService.EXTRA_DATA_TRANS_END);
        mActivity.registerReceiver(mUploadStateReceiver, intentFilter);
    }

    protected void unRegistReceiver() {
        try {
            mActivity.unregisterReceiver(mUploadStateReceiver);
        } catch (Exception e) {
        }
    }

    private BroadcastReceiver mUploadStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, android.content.Intent intent) {
            String action = intent.getAction();
            System.out.println("fragmentBase 接收到更新上传进度广播action:" + action);
            UploadFileModel item = intent.getParcelableExtra(DataTransService.EXTRA_DATA_TRANS_FILE_MODEL);
            if (action.equals(DataTransService.EXTRA_DATA_TRANS_FILE_PROGRESS)) {
                // 更新界面上传进度
                updateUI(item);
            } else if (action.equals(DataTransService.EXTRA_DATA_TRANS_FILE_END)) {
                // 上传完成
                updateUI(item);
            }
        }

        ;
    };

    private void updateUI(UploadFileModel uploadFileModel) {
        if (uploadFileModel == null) {
            return;
        }
//		System.out.println("rcu file progress:" + uploadFileModel.getProgress(FileType.RCU) + "----------------ddib file progress:" + uploadFileModel.getProgress(FileType.DDIB));
        for (TreeNode tn : mAdapter.getDatas()) {
            DataModel d = (DataModel) tn.getValue();
            if (d.isFirstLevel || d.isFolder) {
                continue;
            }
            if (d.testRecord.record_id.equals(uploadFileModel.getTestRecordId())) {
                d.setState(uploadFileModel.getProgress());
                String serverStr = mDbManager.getServerStr();
                for (RecordDetail detail : d.testRecord.getRecordDetails()) {
                    FileType fType = FileType.getFileType(detail.file_type);
                    if (uploadFileModel.getProgressMap().containsKey(fType)) {
                        for (RecordDetailUpload detailUpload : detail.getDetailUploads()) {
                            if (detailUpload.server_info.equals(serverStr)) {
                                detailUpload.upload_type = uploadFileModel.getProgress(fType);
                                break;
                            }
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegistReceiver();
    }

    /**
     * 下载线程
     *
     * @author jianchao.wang 2014年6月20日
     */
    private class DownloadThread extends Thread {
        /**
         * 报表类型
         */
        private String mReportType;
        /**
         * 文件名称
         */
        private String mFileName;
        /**
         * 存放报表的文件路径
         */
        private String mSaveFilePath;
        /**
         * 是否下载文件
         */
        private boolean isDownloadFile = false;

        public DownloadThread(String reportType, String fileName, String saveFilePath, boolean isDownloadFile) {
            this.mReportType = reportType;
            this.mFileName = fileName;
            this.mSaveFilePath = saveFilePath;
            this.isDownloadFile = isDownloadFile;
        }

        @Override
        public void run() {
            BufferedOutputStream output = null;
            HttpURLConnection connect = null;
            try {
                String ip = mServer.getDownloadFleetIp();
                int port = mServer.getDownloadFleetPort();
                StringBuilder http = new StringBuilder();
                http.append("http://").append(ip).append(":").append(port);
                http.append("/services/TestPlanService.svc/DownloadReportFile?reportType=");
                http.append(this.mReportType).append("&fileName=").append(URLEncoder.encode(mFileName, "UTF-8"));
                LogUtil.d(TAG, http.toString());
                URL url = new URL(http.toString());
                connect = (HttpURLConnection) url.openConnection();
                connect.setConnectTimeout(5 * 1000);
                int responseCode = connect.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    if (isDownloadFile) {
                        mHandler.obtainMessage(DOWNLOAD_DOING, this.mReportType).sendToTarget();
                        InputStream input = connect.getInputStream();
                        File file = new File(this.mSaveFilePath);
                        if (!file.getParentFile().exists())
                            file.getParentFile().mkdirs();
                        if (!file.exists())
                            file.createNewFile();
                        output = new BufferedOutputStream(new FileOutputStream(file));
                        byte[] buffer = new byte[1024];
                        int read = -1;
                        read = input.read(buffer);
                        while (read > 0) {
                            output.write(buffer, 0, read);
                            read = input.read(buffer);
                        }
                        output.flush();
                        output.close();
                        mHandler.obtainMessage(DOWNLOAD_END, this.mReportType).sendToTarget();
                    } else {
                        mHandler.obtainMessage(DOWNLOAD_FOUND, this.mReportType).sendToTarget();
                    }
                } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                    mHandler.obtainMessage(DOWNLOAD_NO_FOUND, this.mReportType).sendToTarget();
                } else {
                    mHandler.obtainMessage(DOWNLOAD_FAIL, this.mReportType).sendToTarget();
                }
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.obtainMessage(DOWNLOAD_FAIL, this.mReportType).sendToTarget();
            } finally {
                try {
                    if (output != null) {
                        output.close();
                        output = null;
                    }
                    if (connect != null) {
                        connect.disconnect();
                        connect = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
