package com.walktour.gui.singlestation.test.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.walktour.Utils.SurveyPhotoUtil;
import com.walktour.base.gui.fragment.BaseDialogFragment;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.dao.model.SurveyPhoto;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yi.lin on 2017/8/29.
 * <p>
 * 异常上报对话框
 */

public class AbnormalReportDlgFragment extends BaseDialogFragment {

    private final static String TAG = "AbnormalReportDlgFragment";

    /**
     * 放进bundle的异常描述的key
     */
    public static final String EXTRA_ABNORMAL_REPORT_DESC = "abnormal_report_desc";
    /**
     * 放进bundle的拍照取证路径的key
     */
    public static final String EXTRA_ABNORMAL_REPORT_PHOTO = "abnormal_report_photo";

    /**
     * 拍照请求码
     */
    public static final int REQUEST_CAPTURE_IMAGE = 202;

    /**
     * 异常描述输入框
     */
    @BindView(R.id.et_abnormal_desc)
    EditText mEtAbnormalDesc;

    /**
     * 异常图片显示ImageView
     */
    @BindView(R.id.iv_abnormal_photo)
    ImageView mIvAbnormalPhoto;

    /**
     * 传递过来的StationInfo对象
     */
    private StationInfo mStationInfo;

    /**
     * 拍照图片最终路径
     */
    private Uri mUri;


    /**
     * 拍照图片临时存储路径
     */
    private String mTempFilePath;

    public AbnormalReportDlgFragment() {
        super(R.string.single_station_abnormal_report, R.layout.dlg_fragment_abnormal_report);
    }

    @Override
    public String getLogTAG() {
        return TAG;
    }

    @Override
    protected void setShowValues() {
        mStationInfo = (StationInfo) getParcelableBundle("station_info");
        if (this.getArguments().containsKey("abnormal_photo_path")) {
            String surveyPhotoPath = getStringBundle("abnormal_photo_path");
            if (!TextUtils.isEmpty(surveyPhotoPath)) {
                mIvAbnormalPhoto.setVisibility(View.VISIBLE);
                mIvAbnormalPhoto.setImageURI(Uri.parse(surveyPhotoPath));
            }
        }
        if (this.getArguments().containsKey("abnormal_photo_desc")) {
            String surveyPhotoDesc = getStringBundle("abnormal_photo_desc");
            if (!TextUtils.isEmpty(surveyPhotoDesc)) {
                mEtAbnormalDesc.setText(surveyPhotoDesc);
            }
        }
    }

    @Override
    protected Bundle setCallBackValues() {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_ABNORMAL_REPORT_DESC, mEtAbnormalDesc.getText().toString().trim());
        bundle.putString(EXTRA_ABNORMAL_REPORT_PHOTO, String.valueOf(mUri));
        return bundle;
    }

    /**
     * 拍照取证
     *
     * @param view
     */
    @OnClick(R.id.tv_photo_evidence)
    public void photoEvidence(View view) {
        String photoName = "temp" + System.currentTimeMillis();
        mTempFilePath = SurveyPhotoUtil.getPhotoFileName(mStationInfo.getName(), photoName);
        File file = new File(mTempFilePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        getActivity().startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
    }

    public void onActResult(int requestCode, int resultCode) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CAPTURE_IMAGE) {
            String photoName = "survey_" + SurveyPhoto.PHOTO_TYPE_ABNORMAL_REPORT + "_" + new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());//图片名称：survey_photoType_时间戳
            mUri = Uri.parse(SurveyPhotoUtil.fixRotateAndWatermarkDate2Photo(mStationInfo.getName(), photoName, mTempFilePath, SurveyPhotoUtil.WatermarkPosition.RIGHT_BOTTOM));
            SurveyPhotoUtil.deletePhoto(mTempFilePath);
            mIvAbnormalPhoto.setVisibility(View.VISIBLE);
            mIvAbnormalPhoto.setImageURI(mUri);
        }
    }
}
