package com.walktour.gui.highspeedrail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.WalktourConst;
import com.walktour.Utils.ZipUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.DownloadUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.NetRequest;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.StartDialog;
import com.walktour.gui.highspeedrail.adapter.SelectHsNoAdapter;
import com.walktour.gui.highspeedrail.model.HighSpeedLineModel;
import com.walktour.gui.highspeedrail.model.HighSpeedNoModel;
import com.walktour.service.metro.utils.MetroUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

/**
 * @author jinfeng.xie
 * @version 1.0.0
 * @date on 2018/8/30
 * @describe 高铁选择班次
 */
public class HsSelectNoActivity extends BasicActivity {
    private static final String TAG = "HsSelectNoActivity";
    @BindView(R.id.lv_hs_select_no)
    RecyclerView rvHsSelectNo;
    @BindView(R.id.title_txt)
    TextView titleTxt;
    @BindView(R.id.pointer)
    ImageButton pointer;
    @BindView(R.id.search_content_edit)
    EditText etSearch;
    @BindView(R.id.downrailway)
    Button downBtn;
    private SelectHsNoAdapter mAdapter;
    private List<HighSpeedNoModel> datas;
    private HighSpeedNoModel mCurrentHS;
    /**
     * 进度提示
     */
    private ProgressDialog progressDialog;
    /**
     * 是否有新版本
     **/
    private boolean isNewVersion = false;
    /**
     * 版本号MD5
     */
    private String newVersionMD5 = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hs_select_no);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        titleTxt.setText(getString(R.string.gaotie_test_no));
        mAdapter = new SelectHsNoAdapter(this);

        rvHsSelectNo.setLayoutManager(new LinearLayoutManager(this));
        //添加Android自带的分割线
        rvHsSelectNo.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvHsSelectNo.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HighSpeedNoModel mCurrentNo = mAdapter.getList().get(i);
                readHSRTrainData(mCurrentNo);
                 SharePreferencesUtil.getInstance(HsSelectNoActivity.this).saveObjectToShare(WalktourConst.CURRENT_HS_NO, mCurrentNo, HighSpeedNoModel.class);
                startActivityForResult(new Intent(HsSelectNoActivity.this, HsSelectStationActivity.class), StartDialog.requestHsCode);
                LogUtil.e(TAG, "已经点击了班次：" + mCurrentNo);
            }
        });
        pointer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(etSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        checkNewVersion();
    }

    /**
     * 判断是否有新版本
     */
    private void checkNewVersion() {
        String url = "http://112.91.151.37:64061/Api/GetLatestMd5";
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "hs");
        openDialog(getString(R.string.metro_check_updateing));
        NetRequest.getFormRequest(url, params, new NetRequest.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                closeDialog();
                if ("-1".equals(result)) {
                    return;
                }
                newVersionMD5 = result;
                //获取高铁最新发布包MD5, 例如:7A0768E1065DDE7C4B50029159B76827
                String lastMD5 = SharePreferencesUtil.getInstance(HsSelectNoActivity.this).getString(WalktourConst.WALKTOUR_HST_VERSION_MD5);
                if (!lastMD5.equals(result)) {
                    downBtn.setText(R.string.metro_route_download_btn);
                    downBtn.setVisibility(View.VISIBLE);
                    isNewVersion = true;
                } else {
                    downBtn.setText(R.string.metro_check_update);
                    downBtn.setVisibility(View.VISIBLE);
                    isNewVersion = false;
                    Toast.makeText(HsSelectNoActivity.this, R.string.metro_no_update, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                closeDialog();
                ToastUtil.showToastShort(HsSelectNoActivity.this, R.string.init_server_failed);
            }
        });
    }

    /**
     * 下载数据文件
     */
    private void downloadFile() {
        String dowmurl = "http://112.91.151.37:64061/Api/GetFile?md5=" + newVersionMD5;
        final String parentPath = AppFilePathUtil.getInstance().getSDCardBaseDirectory(HighSpeedRailCommons.High_SPEED_RAIL_PATH);
        openDialog(getString(R.string.updating_now));
        DownloadUtil.getInstance().download(dowmurl, parentPath, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(final String path) {
                LogUtil.d(TAG,"onDownloadSuccess:"+path);
                downBtn.setText(R.string.metro_check_update);
//        服务端压缩文件为GBK格式，所以此处传入GBK解决解压缩出来乱码问题
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(200);
                            ZipUtil.unzip(path, parentPath, "GBK", false);
                            Thread.sleep(200);
                            SharePreferencesUtil.getInstance(HsSelectNoActivity.this).saveString(WalktourConst.WALKTOUR_HST_VERSION_MD5, newVersionMD5);
                            isNewVersion = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initData();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            closeDialog();
                        }
                    }
                }).start();


            }

            @Override
            public void onDownloading(int progress) {
                LogUtil.d(TAG,"onDownloading:"+progress);
            }

            @Override
            public void onDownloadFailed() {
                LogUtil.d(TAG,"onDownloadFailed");
                closeDialog();
                downBtn.setText(R.string.download_fail);
            }
        });
    }


    /**
     * 打开进度条
     *
     * @param txt
     */
    protected void openDialog(String txt) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(txt);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    /**
     * 关闭进度条
     */
    protected void closeDialog() {
        progressDialog.dismiss();
    }


    /**
     * 刷新列表
     */
    private void initData() {
        datas=new ArrayList<>();
        File fileDir = new File(AppFilePathUtil.getInstance().getSDCardBaseDirectory(HighSpeedRailCommons.High_SPEED_RAIL_PATH)
                + StringUtil.getLanguage() + "/");
        if (fileDir.exists()) {
            if (fileDir.isDirectory()) {
                File[] ffs = fileDir.listFiles();
                if (null != ffs && ffs.length > 0) {
                    for (File hsRailFile : ffs) {
                        if (hsRailFile.isDirectory()) {
                            List<HighSpeedNoModel> noModels = new ArrayList<>();
                            File[] hsNoFiles = hsRailFile.listFiles();
                            if (null != hsNoFiles && hsNoFiles.length > 0) {
                                for (File hsNoFile : hsNoFiles) {
                                    if (hsNoFile.getName().contains("xml")) {
                                        HighSpeedNoModel noModel = new HighSpeedNoModel();
                                        noModel.noName = hsNoFile.getName().replace(".xml", "");
                                        noModel.noPath = hsNoFile.getPath();
                                        noModel.parentPath=hsRailFile.getAbsolutePath() + File.separator + hsRailFile.getName() + ".kml";
                                        datas.add(noModel);
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        mAdapter.addListAtEnd(datas);
        mAdapter.notifyDataSetChanged();

    }
    private void readHSRTrainData(HighSpeedNoModel mCurrentNo) {
        MetroUtil.getInstance().initWithMode(MetroUtil.RTFillGPS_Mode_RailwayPro);
        MetroUtil.getInstance().readHSRTrainData(mCurrentNo);//获取路线数据
        MetroUtil.getInstance().uninit();
    }
    @OnClick(R.id.downrailway)
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.pointer:
                this.finish();
                break;
            case R.id.downrailway:
                if (!isNewVersion) {
                    checkNewVersion();
                } else {
//                    new DownloadWay().execute();
                    downloadFile();
                }
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == StartDialog.requestRailRouteCode || requestCode == StartDialog.requestHsCode) {
            if (resultCode == RESULT_OK) {
                this.setResult(RESULT_OK, data);
                this.finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
