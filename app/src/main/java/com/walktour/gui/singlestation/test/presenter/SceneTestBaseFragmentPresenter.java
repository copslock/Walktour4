package com.walktour.gui.singlestation.test.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.dinglicom.data.model.RecordDetail;
import com.dinglicom.data.model.TestRecord;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.gui.model.SimpleCallBack;
import com.walktour.base.gui.model.TreeNode;
import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ServerManager;
import com.walktour.gui.InfoTabHost;
import com.walktour.gui.R;
import com.walktour.gui.StartDialog;
import com.walktour.gui.data.model.DBManager;
import com.walktour.gui.newmap2.NewInfoTabActivity;
import com.walktour.gui.singlestation.dao.SingleStationDaoManager;
import com.walktour.gui.singlestation.dao.model.SceneInfo;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.dao.model.SurveyPhoto;
import com.walktour.gui.singlestation.dao.model.TaskTestResult;
import com.walktour.gui.singlestation.survey.activity.SurveyActivity;
import com.walktour.gui.singlestation.survey.model.SurveyPhotoCallBack;
import com.walktour.gui.singlestation.test.fragment.AbnormalReportDlgFragment;
import com.walktour.gui.singlestation.test.fragment.SceneTestBaseFragment;
import com.walktour.gui.singlestation.test.model.TaskTestResultTreeCallBack;
import com.walktour.gui.singlestation.test.service.SceneTestMonitorStartService;
import com.walktour.gui.singlestation.test.service.SceneTestService;
import com.walktour.gui.task.TaskAttach;
import com.walktour.gui.task.TaskDNSLookup;
import com.walktour.gui.task.TaskEmailPop3;
import com.walktour.gui.task.TaskEmailSmtp;
import com.walktour.gui.task.TaskEmailSmtpAndPop3;
import com.walktour.gui.task.TaskEmpty;
import com.walktour.gui.task.TaskFacebookActivity;
import com.walktour.gui.task.TaskFtpDownload;
import com.walktour.gui.task.TaskFtpUpload;
import com.walktour.gui.task.TaskHttpDownload;
import com.walktour.gui.task.TaskHttpPage;
import com.walktour.gui.task.TaskHttpUpload;
import com.walktour.gui.task.TaskInitiativeVideoCall;
import com.walktour.gui.task.TaskIperfActivity;
import com.walktour.gui.task.TaskMOCCall;
import com.walktour.gui.task.TaskMTCCall;
import com.walktour.gui.task.TaskMmsReceive;
import com.walktour.gui.task.TaskMmsSend;
import com.walktour.gui.task.TaskMmsSendReceive;
import com.walktour.gui.task.TaskMultiFTPDownload;
import com.walktour.gui.task.TaskMultiFTPUpload;
import com.walktour.gui.task.TaskMultiRAB;
import com.walktour.gui.task.TaskPBMActivity;
import com.walktour.gui.task.TaskPDP;
import com.walktour.gui.task.TaskPassivityVideoCall;
import com.walktour.gui.task.TaskPing;
import com.walktour.gui.task.TaskSmsIncept;
import com.walktour.gui.task.TaskSmsSend;
import com.walktour.gui.task.TaskSmsSendReceive;
import com.walktour.gui.task.TaskSpeedTest;
import com.walktour.gui.task.TaskStreaming;
import com.walktour.gui.task.TaskTraceRouteActivity;
import com.walktour.gui.task.TaskUDPActivity;
import com.walktour.gui.task.TaskVideoPlay;
import com.walktour.gui.task.TaskVoIP;
import com.walktour.gui.task.TaskWapDownload;
import com.walktour.gui.task.TaskWapLogin;
import com.walktour.gui.task.TaskWapRefurbish;
import com.walktour.gui.task.TaskWeChatActivity;
import com.walktour.gui.task.TaskWeiBoActivity;
import com.walktour.gui.task.TaskWlanAp;
import com.walktour.gui.task.TaskWlanEteAuth;
import com.walktour.gui.task.TaskWlanLogin;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.TaskGroupConfig;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.service.app.DataTransService;
import com.walktour.service.app.datatrans.model.UploadFileModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 场景测试交互基础类
 * Created by wangk on 2017/6/15.
 */

public abstract class SceneTestBaseFragmentPresenter extends BaseFragmentPresenter {
    /**
     * 关联视图
     */
    private SceneTestBaseFragment mFragment;
    /**
     * 关联业务类
     */
    protected SceneTestService mService;
    /**
     * 要测试的业务类型结果ID列表
     */
    private List<Long> mTestTaskResultIdList = new ArrayList<>();
    /**
     * 应用对象类
     */
    private ApplicationModel mApplicationModel;

    /**
     * 异常上报对话框
     */
    private AbnormalReportDlgFragment mAbnormalReportDlg;


    /**
     * 列表数据层级常量</br>
     * </br>TREE_LEVEL_SCENE_NAME 最外面一级：场景名称
     * </br>TREE_LEVEL_TEST_TASK 第二级：测试任务
     * </br>TREE_LEVEL_KPI 第三级：测试指标
     */
    public static final int TREE_LEVEL_SCENE_NAME = 0, TREE_LEVEL_TEST_TASK = 1, TREE_LEVEL_KPI = 2;

    SceneTestBaseFragmentPresenter(SceneTestBaseFragment fragment, ApplicationModel applicationModel, SceneTestService service) {
        super(fragment);
        this.mFragment = fragment;
        this.mApplicationModel = applicationModel;
        this.mService = service;
    }

    /**
     * 显示开始测试对话框
     */
    private void showStartTestDialog(SceneInfo sceneInfo) {
        StationInfo stationInfo = super.getIntent().getParcelableExtra("station_info");
        Intent service = new Intent(this.getActivity(), SceneTestMonitorStartService.class);
        service.putExtra("scene_info", sceneInfo);
        StringBuilder resultIds = new StringBuilder();
        for (long id : this.mTestTaskResultIdList) {
            resultIds.append(id).append(",");
        }
        if (resultIds.length() > 0)
            resultIds.deleteCharAt(resultIds.length() - 1);
        service.putExtra("result_ids", resultIds.toString());
        service.putExtra("cell_id", sceneInfo.getCellId());
        super.startService(service);
        String sceneTypeName = "";
        switch (sceneInfo.getSceneType()) {
            case SingleStationDaoManager.SCENE_TYPE_COVERAGE:
                sceneTypeName = this.getActivity().getString(R.string.single_station_scene_coverage);
                break;
            case SingleStationDaoManager.SCENE_TYPE_HANDOVER:
                sceneTypeName = this.getActivity().getString(R.string.single_station_scene_handover);
                break;
            case SingleStationDaoManager.SCENE_TYPE_PERFORMANCE:
                sceneTypeName = this.getActivity().getString(R.string.single_station_scene_performance);
                break;
            case SingleStationDaoManager.SCENE_TYPE_SIGNAL_LEAKAGE:
                sceneTypeName = this.getActivity().getString(R.string.single_station_scene_signal_leakage);
                break;
            default:
        }
        StartDialog dialog = new StartDialog(this.getActivity(), null, false, sceneTypeName, TaskModel.FROM_TYPE_SELF, WalkStruct.SceneType.SingleSite, stationInfo.getName());
        dialog.checkDTOrCQT(sceneInfo.getSceneType() != SingleStationDaoManager.SCENE_TYPE_PERFORMANCE);
        dialog.show();
    }

    /**
     * 显示开始测试按钮图片
     */
    void showStartButton() {
        this.mFragment.showStartButton(this.mApplicationModel.isTestJobIsRun() || this.mApplicationModel.isTestStoping());
    }

    public void startOrStopTest(final SceneInfo sceneInfo) {
        LogUtil.d(getLogTAG(), "startOrStopTest:SceneName = " + sceneInfo.getSceneName() + " , sceneId = " + sceneInfo.getId());
        if (!this.mApplicationModel.isTestStoping() && !this.mApplicationModel.isTestJobIsRun()) {
            sceneInfo.setUploaded(false);
            sceneInfo.setRecordId("");
            sceneInfo.setRecordTaskNames("");
            mService.updateSceneInfo(sceneInfo);
            List<TreeNode> list = this.mFragment.getDataList();
            this.mTestTaskResultIdList.clear();
            for (TreeNode node : list) {
                if (node.getLevel() == SceneTestBaseFragmentPresenter.TREE_LEVEL_TEST_TASK) {
                    TaskTestResult testTask = (TaskTestResult) node.getObject();
                    if (testTask.getSceneId().equals(sceneInfo.getId()) && testTask.isCheck()) {
                        this.mTestTaskResultIdList.add(testTask.getId());
                    }
                }
            }
            if (this.mTestTaskResultIdList.isEmpty()) {
                super.showToast(R.string.single_station_test_task_empty);
                return;
            }
        }
        this.mService.startTest(this.mFragment.getContext(), sceneInfo, this.mTestTaskResultIdList, new SimpleCallBack() {
            @Override
            public void onSuccess() {
                showStartTestDialog(sceneInfo);
            }

            @Override
            public void onFailure(String message) {
                showToast(message);
            }
        });
    }

    @Override
    public void onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.single_station_scene_test_info) {
            jumpToTestInfoActivity();
        } else if (item.getItemId() == R.id.single_station_scene_to_survy) {
            jumpToSurveyActivity();
        } else if (item.getItemId() == R.id.single_station_scene_error_report) {
            showErrorReport();
        }
    }

    /**
     * 跳转到测试信息界面
     */
    private void jumpToTestInfoActivity() {
        LogUtil.d(this.getLogTAG(), "----jumpToTestInfoActivity----");
        Intent intent = new Intent(super.getActivity(), NewInfoTabActivity.class);
        if (TraceInfoInterface.currentShowTab == WalkStruct.ShowInfoType.Map || TraceInfoInterface.currentShowTab == WalkStruct.ShowInfoType.Default)
            intent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_MAP);
        else
            intent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_OTHER);
        intent.putExtra("isReplay", false);
        super.getActivity().startActivity(intent);
        super.getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_main);
    }

    /**
     * 跳转到基站勘察界面
     */
    private void jumpToSurveyActivity() {
        super.getActivity().startActivity(new Intent(super.getActivity(), SurveyActivity.class));
    }

    /**
     * 显示异常上报
     */
    private void showErrorReport() {
        StationInfo stationInfo = super.getIntent().getParcelableExtra("station_info");
        mAbnormalReportDlg = new AbnormalReportDlgFragment();
        mAbnormalReportDlg.putBundle("station_info", stationInfo);
        mService.getSurveyPhoto(stationInfo.getId(), SurveyPhoto.PHOTO_TYPE_ABNORMAL_REPORT, new SurveyPhotoCallBack() {
            @Override
            public void onSuccess(SurveyPhoto surveyPhoto) {
                if (surveyPhoto != null) {
                    mAbnormalReportDlg.putBundle("abnormal_photo_path", surveyPhoto.getPhotoPath());
                    mAbnormalReportDlg.putBundle("abnormal_photo_desc", surveyPhoto.getPhotoDesc());
                }
            }

            @Override
            public void onFailure(String message) {

            }
        });
        super.getActivity().showDialog(mAbnormalReportDlg);
    }

    @Override
    public void dealDialogCallBackValues(Bundle bundle) {
        super.dealDialogCallBackValues(bundle);
        String abnormalDesc = bundle.getString(AbnormalReportDlgFragment.EXTRA_ABNORMAL_REPORT_DESC, "");
        String abnormalPhotoPath = bundle.getString(AbnormalReportDlgFragment.EXTRA_ABNORMAL_REPORT_PHOTO, "");
        if (TextUtils.isEmpty(abnormalDesc)) {
            super.getActivity().showToast(super.getActivity().getString(R.string.single_station_toast_abnormal_desc_not_null));
            return;
        }
        StationInfo stationInfo = super.getIntent().getParcelableExtra("station_info");
        SurveyPhoto surveyPhoto = new SurveyPhoto();
        surveyPhoto.setPhotoDesc(abnormalDesc);
        surveyPhoto.setPhotoPath(abnormalPhotoPath);
        surveyPhoto.setPhotoType(SurveyPhoto.PHOTO_TYPE_ABNORMAL_REPORT);
        surveyPhoto.setSurveyStationId(stationInfo.getId());

        this.mService.saveSurveyPhoto(surveyPhoto, new SimpleCallBack() {
            @Override
            public void onSuccess() {
                showToast(R.string.single_station_survey_save_success);
            }

            @Override
            public void onFailure(String message) {
                showToast(R.string.single_station_survey_save_failure);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AbnormalReportDlgFragment.REQUEST_CAPTURE_IMAGE) {
            mAbnormalReportDlg.onActResult(requestCode, resultCode);
        }
    }

    /**
     * 加载场景测试数据
     */
    void loadSceneData() {
        this.mService.buildSceneTestResultList(this.mFragment.getContext(), this.mFragment.getSceneInfo(), new TaskTestResultTreeCallBack() {
            @Override
            public void onSuccess(List<TreeNode> resultList) {
                mFragment.showFragment(resultList);
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    /**
     * 上传测试业务数据
     *
     * @param sceneInfo 测试场景
     */
    public void uploadTestResult(SceneInfo sceneInfo) {
        StationInfo stationInfo = super.getIntent().getParcelableExtra("station_info");
        List<UploadFileModel> uploadFileList = new ArrayList<>();
        TestRecord record = DBManager.getInstance(getActivity()).getTestRecord(sceneInfo.getRecordId());
        if (record != null) {
            UploadFileModel file = new UploadFileModel(record.record_id, record.test_type);
            Set<WalkStruct.FileType> fileTypes = new HashSet<>();
            for (int i = 0; i < record.getRecordDetails().size(); i++) {
                RecordDetail detail = record.getRecordDetails().get(0);
                file.setParentPath(detail.file_path);
                fileTypes.add(WalkStruct.FileType.getFileType(detail.file_type));
            }
            file.setFileTypes(fileTypes);
            file.setName(record.file_name);
            //Tag {"Protocol":"SSV","FileType":"RCU","SourceDataType":"Indoor","SiteID":"0","SceneName":"coverage","TaskName":"FTP_Download,FTP_Upload,Attach","CellID":"12345","Info":"TestInfo"}
            file.addExtraParam("TestPointID", String.valueOf(stationInfo.getSiteId()));
            file.addExtraParam("SceneName", sceneInfo.getSceneName());
            file.addExtraParam("CellID", String.valueOf(sceneInfo.getCellId()));
            file.addExtraParam("TaskName", sceneInfo.getRecordTaskNames());
            uploadFileList.add(file);
            ServerManager.getInstance(this.mFragment.getContext()).uploadFile(this.mFragment.getContext(),
                    WalkStruct.ServerOperateType.uploadTestFile,
                    uploadFileList);
        } else {
            showToast(getActivity().getString(R.string.single_station_test_record_not_found));
        }
    }

    /**
     * 处理广播过来的上传测试记录action
     *
     * @param intent 广播过来的携带数据的intent
     */
    public void onReceiveUploadTestResultBroadcast(Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case DataTransService.EXTRA_DATA_TRANS_FILE_START:
                LogUtil.d(getLogTAG(), "----EXTRA_DATA_TRANS_FILE_START----");
                showProgressDialog("", getActivity().getString(R.string.server_start_upload), true, false);
                break;
            case DataTransService.EXTRA_DATA_TRANS_FILE_END:
                LogUtil.d(getLogTAG(), "----EXTRA_DATA_TRANS_FILE_END----");
                UploadFileModel fileModel = intent.getParcelableExtra(DataTransService.EXTRA_DATA_TRANS_FILE_MODEL);
                SceneInfo sceneInfo = SingleStationDaoManager.getInstance(getActivity()).getSceneInfo(fileModel.getTestRecordId());
                if (null != sceneInfo) {
                    sceneInfo.setUploaded(true);
                    mService.updateSceneInfo(sceneInfo);
                }
                dismissProgressDialog();
                showToast(R.string.server_upload_success);
                loadData();
                break;
            case DataTransService.EXTRA_DATA_TRANS_FILE_PROGRESS:
                LogUtil.d(getLogTAG(), "----EXTRA_DATA_TRANS_FILE_PROGRESS----");
                break;
            case DataTransService.EXTRA_DATA_TRANS_MESSAGE:
                LogUtil.d(getLogTAG(), "----EXTRA_DATA_TRANS_MESSAGE----");
                String extraStr = intent.getStringExtra(DataTransService.EXTRA_KEY_MESSAGE);
                if (!TextUtils.isEmpty(extraStr)) {
                    showToast(extraStr);
                }
                break;
            case WalkMessage.NOTIFY_TESTJOBDONE:
            case WalkMessage.NOTIFY_INTERRUPTJOBDONE:
                loadData();
                break;
            default:
        }
    }

    /**
     * 跳转到编辑任务界面
     * @param sceneInfo 场景对象
     * @param result
     * @param taskGroupConfig
     */
    public void jump2EditTask(SceneInfo sceneInfo,TaskTestResult result,TaskGroupConfig taskGroupConfig){
        String taskNameTag = "";
        switch (sceneInfo.getSceneType()) {
            case SingleStationDaoManager.SCENE_TYPE_COVERAGE:
                taskNameTag = getActivity().getString(R.string.single_station_scene_coverage);
                break;
            case SingleStationDaoManager.SCENE_TYPE_HANDOVER:
                taskNameTag = getActivity().getString(R.string.single_station_scene_handover) + sceneInfo.getSceneName().toUpperCase().replace("HANDOVER", "");
                break;
            case SingleStationDaoManager.SCENE_TYPE_PERFORMANCE:
                taskNameTag = getActivity().getString(R.string.single_station_scene_performance);
                break;
            case SingleStationDaoManager.SCENE_TYPE_SIGNAL_LEAKAGE:
                taskNameTag = getActivity().getString(R.string.single_station_scene_signal_leakage);
                break;
        }
        taskNameTag += "_";
        for (int i = 0; i < taskGroupConfig.getTasks().size(); i++) {
            TaskModel task = taskGroupConfig.getTasks().get(i);
            if (task.getTaskName().startsWith(taskNameTag)) {
                if (task.getTaskName().endsWith(result.getTaskType())) {
                    TaskListDispose.getInstance().setTaskListArray(taskGroupConfig.getTasks());
                    Intent intent = getIntentByTaskType(WalkStruct.TaskType.valueOf(task.getTaskType()));
                    Bundle bundle = new Bundle();
                    bundle.putInt("taskListId", i);
                    intent.putExtra("fromWorkOrder", false);
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                }
            }
        }
    }

    /**
     * 查询基站名称
     * @param stationId 基站id
     * @return
     */
    public String getStationName(Long stationId) {
        return mService.getStationName(stationId);
    }
    private Intent getIntentByTaskType(WalkStruct.TaskType taskType) {
        Intent intent = null;
        switch (taskType) {
            case EmptyTask:
                intent = new Intent(getActivity(), TaskEmpty.class);
                break;
            case InitiativeCall:
                intent = new Intent(getActivity(), TaskMOCCall.class);
                break;
            case PassivityCall:
                intent = new Intent(getActivity(), TaskMTCCall.class);
                break;
            case InitiativeVideoCall:
                intent = new Intent(getActivity(), TaskInitiativeVideoCall.class);
                break;
            case PassivityVideoCall:
                intent = new Intent(getActivity(), TaskPassivityVideoCall.class);
                break;
            case Ping:
                intent = new Intent(getActivity(), TaskPing.class);
                break;
            case Attach:
                intent = new Intent(getActivity(), TaskAttach.class);
                break;
            case PDP:
                intent = new Intent(getActivity(), TaskPDP.class);
                break;
            case FTPUpload:
                intent = new Intent(getActivity(), TaskFtpUpload.class);
                break;
            case FTPDownload:
                intent = new Intent(getActivity(), TaskFtpDownload.class);
                break;
            case Http:
                intent = new Intent(getActivity(), TaskHttpPage.class);
                break;
            case HttpRefurbish:
                intent = new Intent(getActivity(), TaskHttpPage.class);
                break;
            case HttpDownload:
                intent = new Intent(getActivity(), TaskHttpDownload.class);
                break;
            case EmailPop3:
                intent = new Intent(getActivity(), TaskEmailPop3.class);
                break;
            case EmailSmtp:
                intent = new Intent(getActivity(), TaskEmailSmtp.class);
                break;
            case SMSIncept:
                intent = new Intent(getActivity(), TaskSmsIncept.class);
                break;
            case SMSSend:
                intent = new Intent(getActivity(), TaskSmsSend.class);
                break;
            case SMSSendReceive:
                intent = new Intent(getActivity(), TaskSmsSendReceive.class);
                break;
            case MMSIncept:
                intent = new Intent(getActivity(), TaskMmsReceive.class);
                break;
            case MMSSend:
                intent = new Intent(getActivity(), TaskMmsSend.class);
                break;
            case MMSSendReceive:
                intent = new Intent(getActivity(), TaskMmsSendReceive.class);
                break;
            case WapLogin:
                intent = new Intent(getActivity(), TaskWapLogin.class);
                break;
            case WapRefurbish:
                intent = new Intent(getActivity(), TaskWapRefurbish.class);
                break;
            case WapDownload:
                intent = new Intent(getActivity(), TaskWapDownload.class);
                break;
            case EmailSmtpAndPOP:
                intent = new Intent(getActivity(), TaskEmailSmtpAndPop3.class);
                break;
            case Stream: // 流媒体处理 Jihong Xie 2012-07-19
                intent = new Intent(getActivity(), TaskStreaming.class);
                break;
            case VOIP: // VoIP处理
                intent = new Intent(getActivity(), TaskVoIP.class);
                break;
            case MultiRAB: // 并发业务
                intent = new Intent(getActivity(), TaskMultiRAB.class);
                break;
            case DNSLookUp:// DNS
                intent = new Intent(getActivity(), TaskDNSLookup.class);
                break;
            case SpeedTest:// Speed Test Jihong.Xie
                intent = new Intent(getActivity(), TaskSpeedTest.class);
                break;
            case HttpUpload:
                intent = new Intent(getActivity(), TaskHttpUpload.class);
                break;
            case HTTPVS:
                intent = new Intent(getActivity(), TaskVideoPlay.class);
                break;
            case MultiftpUpload:
                intent = new Intent(getActivity(), TaskMultiFTPUpload.class);
                break;
            case MultiftpDownload:
                intent = new Intent(getActivity(), TaskMultiFTPDownload.class);
                break;
            case Facebook:
                intent = new Intent(getActivity(), TaskFacebookActivity.class);
                break;
            case TraceRoute:
                intent = new Intent(getActivity(), TaskTraceRouteActivity.class);
                break;
            case Iperf:
                intent = new Intent(getActivity(), TaskIperfActivity.class);
                break;
            case PBM:
                intent = new Intent(getActivity(), TaskPBMActivity.class);
                break;
            case WeiBo:
                intent = new Intent(getActivity(), TaskWeiBoActivity.class);
                break;
            case WlanAP:
                intent = new Intent(getActivity(), TaskWlanAp.class);
                break;
            case WlanEteAuth:
                intent = new Intent(getActivity(), TaskWlanEteAuth.class);
                break;
            case WlanLogin:
                intent = new Intent(getActivity(), TaskWlanLogin.class);
                break;
            case WeChat:
                intent = new Intent(getActivity(), TaskWeChatActivity.class);
                break;
            case UDP:
                intent = new Intent(getActivity(), TaskUDPActivity.class);
                break;
            default:
                break;
        }
        return intent;
    }

    /**
     * 获取阈值显示文本对应的资源
     * @param thresholdKey 阈值key
     * @return
     */
    public int getThresholdKeyResId(String thresholdKey) {
        if (thresholdKey.equals(TotalStruct.TotalSingleStation._RSCoverMileage.name())) {
            return R.string.single_station_threshold_rs_coverage_ratio;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._FTPAttemptHandoverTimes.name())) {
            return R.string.single_station_threshold_attempt_handover_times;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._FTPHandoverSuccessRate.name())) {
            return R.string.single_station_threshold_handover_succ_ratio;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._PCISample.name())) {
            return R.string.single_station_threshold_pci_sample;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._RSRPAverage.name())) {
            return R.string.single_station_threshold_rsrp;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._tryTimes.name())) {
            return R.string.single_station_threshold_attempt_times;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._successRate.name())) {
            return R.string.single_station_threshold_succ_ratio;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._SpeedAverage.name())) {
            return R.string.single_station_threshold_data_average_rate;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_tryTimes.name())) {
            return R.string.single_station_threshold_attampt_call_times_csfb;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_establishedRate.name())) {
            return R.string.single_station_threshold_establised_succ_ratio_csfb;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_successRate.name())) {
            return R.string.single_station_threshold_call_succ_ratio_csfb;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._volte_successRate.name())) {
            return R.string.single_station_threshold_call_succ_ratio_volte;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._volte_eSRVCC.name())) {
            return R.string.single_station_threshold_handover_succ_times_esrvcc;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_returnDelay.name())) {
            return R.string.single_station_threshold_csfb_return_delay;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_connectDelay.name())) {
            return R.string.single_station_threshold_csfb_connect_delay;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_returnSuccessRate.name())) {
            return R.string.single_station_threshold_csfb_return_success_rate;
        }
        return R.string.single_station_validation;
    }

    /**
     * 获取场景测试显示的名称对应的资源
     * @param sceneInfo 场景对象
     * @return
     */
    public int getSceneDisplayNameResId(SceneInfo sceneInfo) {
        String sceneName = sceneInfo.getSceneName();
        if(TextUtils.isEmpty(sceneName)){
            switch (sceneInfo.getSceneType()){
                case SingleStationDaoManager.SCENE_TYPE_COVERAGE:
                    return R.string.single_station_scene_coverage_test;
                case SingleStationDaoManager.SCENE_TYPE_HANDOVER:
                    return R.string.single_station_scene_handover_test;
                case SingleStationDaoManager.SCENE_TYPE_SIGNAL_LEAKAGE:
                    return R.string.single_station_scene_signal_leakage_test;
                case SingleStationDaoManager.SCENE_TYPE_PERFORMANCE:
                    return R.string.single_station_scene_performance_test;
                default:
                    return R.string.single_station_scene_test;
            }
        }
        switch (sceneName) {
            case "coverage":
                return R.string.single_station_scene_coverage_test;
            case "handover_gate":
                return R.string.single_station_scene_handover_gate_test;
            case "handover_indoor":
                return R.string.single_station_scene_handover_indoor_test;
            case "handover_park":
                return R.string.single_station_scene_handover_park_test;
            case "leakage":
                return R.string.single_station_scene_signal_leakage_test;
            case "performance":
                return R.string.single_station_scene_performance_test;
            default:
                return R.string.single_station_scene_test;
        }
    }
}
