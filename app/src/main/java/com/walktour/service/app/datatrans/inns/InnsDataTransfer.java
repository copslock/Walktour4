package com.walktour.service.app.datatrans.inns;


import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.ZipUtil;
import com.walktour.base.gui.model.SimpleCallBack;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ServerManager;
import com.walktour.gui.inns.dao.InnsDaoManager;
import com.walktour.gui.inns.dao.model.InnsFtpParams;
import com.walktour.gui.inns.dao.model.InnsVoLTEParams;
import com.walktour.service.app.DataTransService;
import com.walktour.service.app.datatrans.BaseDataTransfer;
import com.walktour.service.app.datatrans.inns.net.InnsmapRetrofitManager;
import com.walktour.service.app.datatrans.model.UploadFileModel;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by yi.lin on 2017/12/1.
 * <p>
 * 寅时服务器上传数据
 */

public class InnsDataTransfer extends BaseDataTransfer {


    private InnsmapRetrofitManager mRetrofitManager;
    private String userId;
    //testType Volte类型为3，ftp类型为4
    private static final int TEST_TYPE_VOLTE = 3;
    private static final int TEST_TYPE_FTP = 4;

    public InnsDataTransfer(DataTransService service) {
        super(ServerManager.SERVER_INNS, "InnsDataTransfer", service, "inns_upload.log");
        mRetrofitManager = new InnsmapRetrofitManager(mServerMgr.getInnsServerIp());
        userId = mServerMgr.getInnsServerUserId();
    }

    @Override
    public boolean init() {
        return true;
    }


    @Override
    public boolean uninit() {
        return true;
    }


    @Override
    public boolean downloadTestTask(boolean force) {
        return false;
    }

    @Override
    public boolean sendEvent() {
        return false;
    }

    @Override
    public boolean syncTime() {
        return false;
    }

    @Override
    protected void uploadCurrentFileType() {
        File file = super.mCurrentFile.getFile(super.mCurrentFileType);
        LogUtil.d(TAG, "file = " + file);
        zipFileAndUpload(file);
    }

    /**
     * 将txt文件压缩并上传到平台
     *
     * @param originFile 测试数据txt文件
     */
    private void zipFileAndUpload(final File originFile) {
        String filePath = AppFilePathUtil.getInstance().createSDCardBaseDirectory("inns", "log_txt_zips");
        File zipFile = new File(filePath + System.currentTimeMillis() + ".zip");
        if (!zipFile.exists()) {
            try {
                zipFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ZipUtil.zip(originFile, zipFile);
        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), zipFile);
        MultipartBody.Part formData = MultipartBody.Part.createFormData("file", zipFile.getName(), fileRequestBody);
        mRetrofitManager.uploadLogFile(mService, userId, formData, new SimpleCallBack() {
            @Override
            public void onSuccess() {
//                uploadVoLTEMeasInfo(originFile.toString());
                LogUtil.d(TAG, "--uploadLogFile onSuccess--");
                uploadMeasInfo(originFile.toString());
            }

            @Override
            public void onFailure(String message) {
                InnsDataTransfer.super.setFileTypeUploadState(UploadFileModel.UploadState.FAILURE);
                LogUtil.e(TAG, "--uploadLogFile onFailure--" + message);
            }
        });
    }

    /**
     * 上传volte、ftp测试数据
     *
     * @param fileNameDetail
     */
    private void uploadMeasInfo(final String fileNameDetail) {
        InnsVoLTEParams innsVoLTEParams = InnsDaoManager.getInstance(mService).getInnsVoLTEParamsByFileNameDetail(fileNameDetail);
        InnsFtpParams innsFtpParams = InnsDaoManager.getInstance(mService).getInnsFtpParamsByFileNameDetail(fileNameDetail);
        TestDataType type = checkTestDataType(innsVoLTEParams, innsFtpParams);
        if (type == TestDataType.ALL) {
            uploadVoLTEMeasInfo(fileNameDetail,true);
        }else if(type == TestDataType.VOLTE_ONLY){
            uploadVoLTEMeasInfo(fileNameDetail,false);
        }else if(type == TestDataType.FTP_ONLY){
            uploadFTPMeasInfo(fileNameDetail);
        }else{
            InnsDataTransfer.super.setFileTypeUploadState(UploadFileModel.UploadState.SUCCESS);
        }
    }

    /**
     * 上传VoLTE测试数据
     * @param fileNameDetail 文件名称
     * @param uploadNextWhenFinish 上传成功后是否上传下一个测试数据
     */
    private void uploadVoLTEMeasInfo(final String fileNameDetail, final boolean uploadNextWhenFinish) {
        InnsVoLTEParams innsVoLTEParams = InnsDaoManager.getInstance(mService).getInnsVoLTEParamsByFileNameDetail(fileNameDetail);
        InnsFtpParams innsFtpParams = InnsDaoManager.getInstance(mService).getInnsFtpParamsByFileNameDetail(fileNameDetail);
        mRetrofitManager.uploadMeasInfo(TEST_TYPE_VOLTE, innsFtpParams, innsVoLTEParams, new SimpleCallBack() {
            @Override
            public void onSuccess() {
                if(uploadNextWhenFinish){
                    uploadFTPMeasInfo(fileNameDetail);
                }else{
                    //如果不需要上传下一个测试数据，则当上传第一个数据成功后就设置上传成功状态
                    InnsDataTransfer.super.setFileTypeUploadState(UploadFileModel.UploadState.SUCCESS);
                }
                LogUtil.d(TAG, "--uploadMeasInfo onSuccess--");
            }

            @Override
            public void onFailure(String message) {
                InnsDataTransfer.super.setFileTypeUploadState(UploadFileModel.UploadState.FAILURE);
                LogUtil.d(TAG, "--uploadMeasInfo onFailure--" + message);
            }
        });
    }

    enum TestDataType {
        VOLTE_ONLY, FTP_ONLY, ALL, NONE
    }

    /**
     * 判断上传什么类型的测量数据
     * @param volteParams
     * @param ftpParams
     * @return
     */
    private TestDataType checkTestDataType(InnsVoLTEParams volteParams, InnsFtpParams ftpParams) {
        if (volteParams == null && ftpParams == null) {
            return TestDataType.NONE;
        }
        boolean isVolteDataEmpty = volteParams.getConnRate() == 0 &&
                volteParams.getDropRate() == 0 &&
                volteParams.getCallDelay() == 0 &&
                volteParams.getMos3Rate() == 0 &&
                volteParams.getMos35Rate() == 0 &&
                volteParams.getImsSuccessRate() == 0 &&
                volteParams.getEsrvccSuccessRate() == 0 &&
                volteParams.getEsrvccDelay() == 0 &&
                volteParams.getRtpLostRate() == 0 &&
                volteParams.getRtpShakeRate() == 0;
        boolean isFtpEmpty = ftpParams.getFtpDownAve() == 0 &&
                ftpParams.getFtpUpAve() == 0 &&
                ftpParams.getFtpDownMax() == 0 &&
                ftpParams.getFtpDownMin() == 0;
        if (isFtpEmpty && isVolteDataEmpty) {
            return TestDataType.NONE;
        } else if (!isFtpEmpty && isVolteDataEmpty) {
            return TestDataType.FTP_ONLY;
        } else if (!isVolteDataEmpty && isFtpEmpty) {
            return TestDataType.VOLTE_ONLY;
        } else if (!isFtpEmpty && !isVolteDataEmpty) {
            return TestDataType.ALL;
        }
        return TestDataType.NONE;
    }

    /**
     * 上传FTP测试数据
     *
     * @param fileNameDetail
     */
    private void uploadFTPMeasInfo(String fileNameDetail) {
        InnsVoLTEParams innsVoLTEParams = InnsDaoManager.getInstance(mService).getInnsVoLTEParamsByFileNameDetail(fileNameDetail);
        InnsFtpParams innsFtpParams = InnsDaoManager.getInstance(mService).getInnsFtpParamsByFileNameDetail(fileNameDetail);
        mRetrofitManager.uploadMeasInfo(TEST_TYPE_FTP, innsFtpParams, innsVoLTEParams, new SimpleCallBack() {
            @Override
            public void onSuccess() {
                InnsDataTransfer.super.setFileTypeUploadState(UploadFileModel.UploadState.SUCCESS);
                LogUtil.d(TAG, "--uploadMeasInfo onSuccess--");
            }

            @Override
            public void onFailure(String message) {
                InnsDataTransfer.super.setFileTypeUploadState(UploadFileModel.UploadState.FAILURE);
                LogUtil.d(TAG, "--uploadMeasInfo onFailure--" + message);
            }
        });
    }

    @Override
    protected void initCurrentFileTypes() {
        // 目前Inns平台仅上传TXT
        if (super.mCurrentFile.getFileTypes().length == 0) {
            Set<WalkStruct.FileType> fileTypes = new HashSet<>();
            fileTypes.add(WalkStruct.FileType.TXT);
            this.mCurrentFile.setFileTypes(fileTypes);
        }
    }

    @Override
    protected void interruptUploading() {
    }

    @Override
    protected boolean uploadParamsReport(String msg) {
        // 无需实现
        return true;
    }


}
