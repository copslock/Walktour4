package com.walktour.gui.metro.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.FileUtil;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.WalktourConst;
import com.walktour.Utils.ZipUtil;
import com.walktour.base.util.DownloadUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.NetRequest;
import com.walktour.gui.R;
import com.walktour.gui.StartDialog;
import com.walktour.gui.metro.MetroSettingCityActivity;
import com.walktour.service.metro.MetroFactory;
import com.walktour.service.metro.model.MetroCity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * 地铁线路城市列表界面
 *
 * @author jianchao.wang
 */
public class MetroSettingCityFragment extends Fragment implements OnItemClickListener, OnClickListener {
    private static final String TAG = "MetroSettingCityFragmen";
    /**
     * 地铁线路工厂类
     */
    private MetroFactory mFactory;
    /**
     * 城市列表
     */
    private List<MetroCity> mCities = new ArrayList<MetroCity>();
    /**
     * 列表适配器
     */
    private MetroCityAdapter mAdapter;
    /**
     * 是否是选择城市的模式
     */
    private boolean isSelect = false;
    /**
     * 下载更新按钮
     */
    private Button mDownloadButton;
    /**
     * 是否有新版本
     */
    private boolean hasNewVersion = false;
    private ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mFactory = MetroFactory.getInstance(this.getActivity());
        View view = inflater.inflate(R.layout.activity_metro_setting_city, container, false);
        this.isSelect = this.getArguments().getBoolean("is_select");
        this.findView(view);
        if (!this.isSelect)
            this.checkNewVersion();
        return view;
    }

    /**
     * 视图设置
     */
    private void findView(View view) {
        TextView title = (TextView) view.findViewById(R.id.title_txt);
        if (isSelect)
            title.setText(R.string.metro_select_city);
        else
            title.setText(R.string.metro_route_download);
        ImageButton pointer = (ImageButton) view.findViewById(R.id.pointer);
        pointer.setOnClickListener(this);
        LinearLayout toolBar = (LinearLayout) view.findViewById(R.id.tool_bar);
        if (isSelect)
            toolBar.setVisibility(View.GONE);
        else
            toolBar.setVisibility(View.VISIBLE);
        mDownloadButton = (Button) view.findViewById(R.id.btn_download_file);
        mDownloadButton.setOnClickListener(this);
        list = (ListView) view.findViewById(R.id.metro_city_list);
        this.mCities.addAll(this.mFactory.getCities());
        this.mAdapter = new MetroCityAdapter(this.getActivity(), R.layout.activity_metro_setting_city_row, this.mCities);
        list.setAdapter(this.mAdapter);
        if (this.isSelect)
            list.setOnItemClickListener(this);
    }


    /**
     * 下载完成后刷新列表
     */
    private void reflashList() {
        this.mCities.clear();
        this.mCities.addAll(this.mFactory.getCities());
        this.mAdapter.notifyDataSetChanged();
    }

    /**
     * 城市列表适配器类
     *
     * @author jianchao.wang
     */
    private class MetroCityAdapter extends ArrayAdapter<MetroCity> {
        private int mResourceId;

        public MetroCityAdapter(Context context, int textViewResourceId, List<MetroCity> objects) {
            super(context, textViewResourceId, objects);
            this.mResourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(this.getContext()).inflate(this.mResourceId, null);
            }
            TextView cityName = (TextView) view.findViewById(R.id.city_name);
            MetroCity city = this.getItem(position);
            cityName.setText(city.getName());
            return view;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.metro_city_list) {
            MetroCity city = this.mCities.get(position);
            this.mFactory.setCurrentCity(this.getActivity(), city, true);
            Intent data = new Intent();
            data.putExtra("result", city.getName());
            this.getActivity().setResult(StartDialog.requestCityCode, data);
            this.getActivity().finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_download_file:
                if (this.hasNewVersion)
                    this.downloadFile();
                else
                    this.checkNewVersion();
                break;
            case R.id.pointer:
                this.getActivity().finish();
                break;
        }
    }

    String newVersionMD5 = "";//最新的版本号MD5

    /**
     * 判断是否有新版本
     */
    private void checkNewVersion() {
//		new CheckVersionTask().execute();
        String url = "http://112.91.151.37:64061/Api/GetLatestMd5";
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "metro");
        NetRequest.getFormRequest(url, params, new NetRequest.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                if ("-1".equals(result)) {
                    return;
                }
                newVersionMD5 = result;
                //获取地铁最新发布包MD5, 例如:7A0768E1065DDE7C4B50029159B76827
                String lastMD5 = SharePreferencesUtil.getInstance(getContext()).getString(WalktourConst.WALKTOUR_METRO_VERSION_MD5);
                if (!lastMD5.equals(result)) {
                    mDownloadButton.setText(R.string.metro_route_download_btn);
                    hasNewVersion = true;
                } else {
                    mDownloadButton.setText(R.string.metro_check_update);
                    hasNewVersion = false;
                    Toast.makeText(getActivity(), R.string.metro_no_update, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                Log.d(TAG, "获取失败:" + request);
            }
        });
    }

    /**
     * 下载数据文件
     */
    private void downloadFile() {
//        new DownloadTask().execute();
        final MetroSettingCityActivity activity = (MetroSettingCityActivity) getActivity();
        String dowmurl = "http://112.91.151.37:64061/Api/GetFile?md5=" + newVersionMD5;
        final String parentPath = mFactory.mBaseFile.getAbsolutePath() + File.separator + "Metro" + File.separator;
        activity.showProgressDialog(getString(R.string.downloading));
        DownloadUtil.getInstance().download(dowmurl, parentPath, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(final String path) {
                mDownloadButton.setText(R.string.metro_check_update);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.d(TAG,"下载成功");
                        //        服务端压缩文件为GBK格式，所以此处传入GBK解决解压缩出来乱码问题
                        try {
                            FileUtil.deleteDirectory(parentPath+File.separator+"cn");
                            FileUtil.createFileDir(parentPath+File.separator+"cn");
                            LogUtil.d(TAG,"删除成功");
                            Thread.sleep(200);
                            ZipUtil.unzip(path, parentPath, "GBK", false);
                            Thread.sleep(200);
                            LogUtil.d(TAG,"解压成功");
                            SharePreferencesUtil.getInstance(getContext()).saveString(WalktourConst.WALKTOUR_METRO_VERSION_MD5, newVersionMD5);
                            hasNewVersion = false;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            activity.dismissProgress();
                        }
                        mFactory.init(activity);//初始化地铁信息
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reflashList();//刷新列表
                            }
                        });
                    }
                }).start();


            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
                activity.dismissProgress();
                mDownloadButton.setText(R.string.metro_route_download_btn);
            }
        });
    }

    /**
     * 判断ftp服务器数据是否有更新
     */
    private class CheckVersionTask extends AsyncTask<Void, Void, Boolean> {
        private MetroSettingCityActivity activity = (MetroSettingCityActivity) getActivity();

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            hasNewVersion = result;
            if (result) {
                mDownloadButton.setText(R.string.metro_route_download_btn);

            } else {
                mDownloadButton.setText(R.string.metro_check_update);
                Toast.makeText(activity, R.string.metro_no_update, Toast.LENGTH_SHORT).show();
            }
            activity.dismissProgress();
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            return mFactory.checkFileHasNewVersion();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activity.showProgressDialog(getString(R.string.metro_check_updateing));
        }
    }

    /**
     * 判断ftp服务器数据是否有更新
     */
    private class DownloadTask extends AsyncTask<Void, Void, Boolean> {

        private MetroSettingCityActivity activity = (MetroSettingCityActivity) getActivity();

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                mDownloadButton.setText(R.string.metro_check_update);
                reflashList();
            } else
                mDownloadButton.setText(R.string.metro_route_download_btn);
            activity.dismissProgress();
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            boolean flag = mFactory.downloadFile();
            if (flag)
                mFactory.init(activity);
            return flag;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activity.showProgressDialog(getString(R.string.updating_now));
        }
    }

}
