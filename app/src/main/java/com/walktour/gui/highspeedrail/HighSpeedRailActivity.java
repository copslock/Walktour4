package com.walktour.gui.highspeedrail;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalktourConst;
import com.walktour.Utils.ZipUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.DownloadUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.NetRequest;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.highspeedrail.model.HighSpeedLineModel;
import com.walktour.gui.highspeedrail.model.HighSpeedNoModel;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.poi.ss.formula.functions.T;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Request;

/***
 * 高铁线路选择界面
 *
 * @author weirong.fan
 *
 */
@SuppressLint("SdCardPath")
public class HighSpeedRailActivity extends BasicActivity {
	private static final String TAG="HighSpeedRailActivity";
    /**
     * 接收对象列表
     **/
    private ListView listView;
    private MyAdapter adapter;
    /**
     * 所有的文件列表
     */
    private List<HighSpeedLineModel> highSpeedLineModel = new LinkedList<HighSpeedLineModel>();
    /**
     * 服务器上的zip文件
     ***/
    private Context context = HighSpeedRailActivity.this;
    /**
     * 进度提示
     */
    private ProgressDialog progressDialog;
    /**
     * 是否来自于开始测试窗口
     **/
    private boolean isFromStartDialog = false;
    /**
     * 下载按钮
     */
    private Button downBtn;
    /**
     * 是否有新版本
     **/
    private boolean isNewVersion = false;
    /**
     * 版本号MD5
     */
    private String newVersionMD5 = "";
    /**
     * 配置文件
     */
    private SharePreferencesUtil sharePreferencesUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gaotieselectlayout);
        sharePreferencesUtil = SharePreferencesUtil.getInstance(this);
        try {
//            copyFile();
            Bundle bundle = getIntent().getExtras();
            if (null != bundle) {
                isFromStartDialog = bundle.getBoolean(WalkMessage.KEY_IS_FROM_STARTDIALOG);
            }
            initViews();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    /***
//     * 拷贝本地文件作为默认线路
//     */
//    private void copyFile() {
//        if ("".equals(sharePreferencesUtil.getInteger(WalkMessage.KEY_HIGHSPEEDRAIL_VERSION))) {// 没有拷贝文件到默认SD卡
//            copyAssetDataToSD("files/" + localFileName, AppFilePathUtil.getInstance().getSDCardBaseDirectory(HighSpeedRailCommons.High_SPEED_RAIL_PATH) + localFileName);
//            try {
//                ZipUtil.unzip(AppFilePathUtil.getInstance().getSDCardBaseDirectory(HighSpeedRailCommons.High_SPEED_RAIL_PATH) + localFileName, AppFilePathUtil.getInstance().getSDCardBaseDirectory(HighSpeedRailCommons.High_SPEED_RAIL_PATH), "GBK", false);
//                String name = localFileName.substring(0, localFileName.lastIndexOf("."));
//                String[] names = name.split("_");
//                if (names.length >= 2) {
//                    sharePreferencesUtil.saveString(WalkMessage.KEY_HIGHSPEEDRAIL_VERSION,
//                            names[1]);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public void initViews() {
        initTextView(R.id.title_txt).setText(R.string.gaotie_project_test_1);
        listView = this.initListView(R.id.id_listview);
        refreshLocalData();
        adapter = new MyAdapter(highSpeedLineModel);
        listView.setAdapter(adapter);
        downBtn = initButton(R.id.downrailway);
        downBtn.setOnClickListener(this);
        initImageView(R.id.pointer).setOnClickListener(this);
//            new CheckVersion().execute();
        checkNewVersion();
    }
    void reFreshList() {
        refreshLocalData();
        adapter.notifyDataSetChanged();
    }
    /**
     * 刷新列表
     */
    private void refreshLocalData() {
        highSpeedLineModel.clear();
        File fileDir = new File(AppFilePathUtil.getInstance().getSDCardBaseDirectory(HighSpeedRailCommons.High_SPEED_RAIL_PATH)
                + StringUtil.getLanguage() + "/");
        if (fileDir.exists()) {
            if (fileDir.isDirectory()) {

                File[] ffs = fileDir.listFiles();
                if (null != ffs && ffs.length > 0) {
                    for (File hsRailFile : ffs) {
                        if (hsRailFile.isDirectory()) {
                            HighSpeedLineModel highNoModel = new HighSpeedLineModel();
                            highNoModel.hsname = hsRailFile.getName();
                            highNoModel.hsPath = hsRailFile.getAbsolutePath() + File.separator + hsRailFile.getName() + ".kml";
                            List<HighSpeedNoModel> noModels = new ArrayList<>();
                            File[] hsNoFiles = hsRailFile.listFiles();
                            if (null != hsNoFiles && hsNoFiles.length > 0) {
                                for (File hsNoFile : hsNoFiles) {
                                    if (hsNoFile.getName().contains("xml")) {
                                        HighSpeedNoModel noModel = new HighSpeedNoModel();
                                        noModel.noName = hsNoFile.getName().replace(".xml", "");
                                        noModel.noPath = hsNoFile.getPath();
                                        noModels.add(noModel);
                                    }
                                }
                                highNoModel.noModels = noModels;
                            }
                            highSpeedLineModel.add(highNoModel);
                        }
                    }
                }

            }
        }
    }


    private class MyAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private List<HighSpeedLineModel> localFiles;

        private MyAdapter(List<HighSpeedLineModel> localFiles) {
            super();
            layoutInflater = LayoutInflater.from(context);
            this.localFiles = localFiles;
        }

        @Override
        public int getCount() {
            return localFiles != null ? localFiles.size() : 0;
        }

        @Override
        public Object getItem(int arg0) {
            return localFiles != null ? localFiles.get(arg0) : null;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.gaotieselectlayout_item, parent, false);
            }
            TextView t1 = (TextView) convertView.findViewById(R.id.name1);
            TextView t2 = (TextView) convertView.findViewById(R.id.name2);
            ImageView tiv = (ImageView) convertView.findViewById(R.id.isnew);
            final HighSpeedLineModel highSpeedLineModel = localFiles.get(position);
            final String name = highSpeedLineModel.hsname;
            t1.setText(name);
            t2.setText("");
            tiv.setVisibility(View.GONE);
            if (isFromStartDialog) {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharePreferencesUtil.getInstance(HighSpeedRailActivity.this).saveObjectToShare(WalktourConst.CURRENT_HS, highSpeedLineModel, HighSpeedLineModel.class);
                        SharePreferencesUtil.getInstance(HighSpeedRailActivity.this).saveObjectToShare(WalktourConst.CURRENT_HS_NO, null, HighSpeedNoModel.class);
                        Intent intent = new Intent();
                        intent.putExtra("resultSelectRoute", name);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
            return convertView;
        }
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
                String lastMD5 = SharePreferencesUtil.getInstance(HighSpeedRailActivity.this).getString(WalktourConst.WALKTOUR_HST_VERSION_MD5);
                if (!lastMD5.equals(result)) {
                    downBtn.setText(R.string.metro_route_download_btn);
                    downBtn.setVisibility(View.VISIBLE);
                    isNewVersion = true;
                } else {
                    downBtn.setText(R.string.metro_check_update);
                    downBtn.setVisibility(View.VISIBLE);
                    isNewVersion = false;
                    Toast.makeText(HighSpeedRailActivity.this, R.string.metro_no_update, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                closeDialog();
                ToastUtil.showToastShort(context, R.string.init_server_failed);
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
                refreshLocalData();
//        服务端压缩文件为GBK格式，所以此处传入GBK解决解压缩出来乱码问题
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(200);
                                ZipUtil.unzip(path, parentPath, "GBK", false);
                                Thread.sleep(200);
                                SharePreferencesUtil.getInstance(HighSpeedRailActivity.this).saveString(WalktourConst.WALKTOUR_HST_VERSION_MD5, newVersionMD5);
                                isNewVersion = false;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        reFreshList();
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
        progressDialog = new ProgressDialog(this.context);
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

    @Override
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

    /***
     * 拷贝Asset目录下的文件到指定目录下
     *
     * @param fileName
     *            源文件名
     * @param strOutFileName
     *            目标文件，包含路径
     * @throws IOException
     */
//    private void copyAssetDataToSD(String fileName, String strOutFileName) {
//        InputStream myInput = null;
//        OutputStream myOutput = null;
//        try {
//            myOutput = new FileOutputStream(strOutFileName);
//
//            myInput = this.getAssets().open(fileName);
//            byte[] buffer = new byte[1024];
//            int length = myInput.read(buffer);
//            while (length > 0) {
//                myOutput.write(buffer, 0, length);
//                length = myInput.read(buffer);
//            }
//
//            myOutput.flush();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//            if (null != myInput) {
//                try {
//                    myInput.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                myInput = null;
//            }
//            if (null != myOutput) {
//                try {
//                    myOutput.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                myOutput = null;
//            }
//        }
//
//    }


}
