package com.walktour.gui.newmap.basestation;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.StringUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ServerManager;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.applet.ControlBar;
import com.walktour.gui.newmap.basestation.model.BaseStationCity;
import com.walktour.gui.newmap.basestation.service.BaseStationService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 基站下载主页面
 *
 * @author jianchao.wang 2014年8月12日
 */
public class BaseStationDownloadActivity extends BasicActivity implements OnCheckedChangeListener {

    private final static String TAG = "BaseStationDownloadActivity";
    /**
     * 下载按钮
     */
    private Button downloadButton;
    /**
     * 更新按钮
     */
    private Button refleshButton;
    /**
     * 确定按钮
     */
    private Button okButton;
    /**
     * 当前对象
     */
    private BaseStationDownloadActivity activity;
    /**
     * 可下载的城市列表
     */
    private List<BaseStationCity> cityList = new ArrayList<BaseStationCity>();
    /**
     * 城市列表适配
     */
    private CityAdapter cityAdapter;
    /**
     * 勾选的下载城市集合
     */
    private Set<BaseStationCity> citySet = new HashSet<BaseStationCity>();
    /**
     * 服务器管理类
     */
    private ServerManager mServer;
    /**
     * 下载进度条
     */
    private ProgressDialog progressDialog;
    /**
     * 下载城市结束标识
     */
    private static final int DOWNLOAD_CITY_END = 12;
    /**
     * 下载城市失败标识
     */
    private static final int DOWNLOAD_CITY_FAIL = 16;
    /**
     * 下载基站结束标识
     */
    private static final int DOWNLOAD_STATION_END = 22;
    /**
     * 下载基站失败标识
     */
    private static final int DOWNLOAD_STATION_FAIL = 26;
    /**
     * 基站数据服务类
     */
    private BaseStationService bsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mServer = ServerManager.getInstance(BaseStationDownloadActivity.this);
        setContentView(R.layout.base_station_download_activity);
        this.activity = this;
        this.findView();
        Intent service = new Intent(this, BaseStationService.class);
        bindService(service, conn, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bsService = ((BaseStationService.BaseStationBinder) service).getService();
        }
    };

    private void findView() {
        (initTextView(R.id.title_txt)).setText(R.string.base_station_download);
        findViewById(R.id.pointer).setOnClickListener(this);
        ControlBar bar = (ControlBar) this.findViewById(R.id.ControlBar);
        this.refleshButton = bar.getButton(0);
        this.refleshButton.setText(getResources().getString(R.string.update_order_str));
        this.refleshButton.setOnClickListener(this);
        this.downloadButton = bar.getButton(1);
        this.downloadButton.setText(getResources().getString(R.string.download));
        this.downloadButton.setOnClickListener(this);
        this.okButton = bar.getButton(2);
        this.okButton.setText(getResources().getString(R.string.str_ok));
        this.okButton.setOnClickListener(this);
        this.cityAdapter = new CityAdapter(this, R.layout.base_station_download_city_row, this.cityList);
        ListView cityView = (ListView) findViewById(R.id.city_view);
        cityView.setAdapter(cityAdapter);
    }

    /**
     * 城市列表适配类
     *
     * @author jianchao.wang
     */
    private class CityAdapter extends ArrayAdapter<BaseStationCity> {
        /**
         * 资源ID
         */
        private int resourceId;

        public CityAdapter(Context context, int textViewResourceId, List<BaseStationCity> objects) {
            super(context, textViewResourceId, objects);
            this.resourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                String inflater = Context.LAYOUT_INFLATER_SERVICE;
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
                view = vi.inflate(resourceId, null, true);
            } else {
                view = convertView;
            }
            BaseStationCity city = this.getItem(position);
            ((TextView) view.findViewById(R.id.province_name)).setText(city.provinceName);
            ((TextView) view.findViewById(R.id.city_name)).setText(city.cityName);
            ((TextView) view.findViewById(R.id.network_type)).setText(city.networkType);
            CheckBox check = (CheckBox) view.findViewById(R.id.check);
            check.setTag(city);
            check.setOnCheckedChangeListener(activity);
            view.setTag(city);
            return view;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked)
            this.citySet.add((BaseStationCity) buttonView.getTag());
        else
            this.citySet.remove(buttonView.getTag());
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Button01:
                refleshCitys();
                break;
            case R.id.Button02:
                downloadCitys();
                break;
            case R.id.Button03:
                finish();
                break;
            case R.id.pointer:
                finish();
                break;
        }
    }

    /**
     * 下载城市基站
     */
    private void downloadCitys() {
        if (this.citySet.isEmpty())
            return;
        String ip = this.mServer.getDownloadFleetIp();
        if (!this.mServer.getFleetServerType() || StringUtil.isNullOrEmpty(ip)) {
            Toast.makeText(getApplicationContext(), getString(R.string.base_station_fleet_ip_null), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (!MyPhoneState.getInstance().isNetworkAvirable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.sys_alarm_speech_neterr), Toast.LENGTH_SHORT).show();
            return;
        }
        this.showProgressDialog();
        new DownloadStationThread().start();
    }

    /**
     * 显示进度条
     */
    private void showProgressDialog() {
        // 创建ProgressDialog对象
        progressDialog = new ProgressDialog(this);
        // 设置进度条风格，风格为圆形，旋转的
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // 设置ProgressDialog 标题
        progressDialog.setTitle(R.string.base_station_download);
        // 设置ProgressDialog提示信息
        progressDialog.setMessage(getResources().getString(R.string.base_station_download_message));
        // 设置ProgressDialog标题图标
        // progressDialog.setIcon(R.drawable.a);
        // 设置ProgressDialog 的进度条是否不明确 false 就是不设置为不明确
        progressDialog.setIndeterminate(false);
        // 设置ProgressDialog 进度条进度
        progressDialog.setProgress(100);
        // 设置ProgressDialog 是否可以按退回键取消
        progressDialog.setCancelable(true);
        // 让ProgressDialog显示
        progressDialog.show();
    }

    /**
     * 下载城市线程
     *
     * @author jianchao.wang 2014年6月20日
     */
    private class DownloadCityThread extends Thread {
        @Override
        public void run() {
            String ip = mServer.getDownloadFleetIp();
            int port = mServer.getDownloadFleetPort();
            StringBuilder http = new StringBuilder();
            http.append("http://").append(ip).append(":").append(port);
            http.append("/Services/CQTCellInfoService.svc/GetCityNetworkTypeList");
            LogUtil.d(TAG, http.toString());
            OkHttpClient client = new OkHttpClient();
            try {
                Request request = new Request.Builder().url(http.toString()).build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    parseCityJson(result);
                    Message msg = handler.obtainMessage(DOWNLOAD_CITY_END);
                    handler.sendMessage(msg);
                } else {
                    Message msg = handler.obtainMessage(DOWNLOAD_CITY_FAIL);
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 解析基站城市的列表
     *
     * @param json 城市json
     * @return
     */
    private void parseCityJson(String json) {
        this.cityList.clear();
        try {
            JSONTokener jsonParser = new JSONTokener(json);
            JSONArray orders = (JSONArray) jsonParser.nextValue();
            for (int i = 0; i < orders.length(); i++) {
                JSONObject order = orders.getJSONObject(i);
                BaseStationCity city = new BaseStationCity();
                city.provinceName = this.getString(order, "Province");
                city.cityName = this.getString(order, "City");
                city.networkType = this.getString(order, "NetWorkType");
                cityList.add(city);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取json对象的指定属性值
     *
     * @param obj  json对象
     * @param name 属性名
     * @return
     * @throws JSONException
     */
    private String getString(JSONObject obj, String name) throws JSONException {
        String value = "";
        if (obj.has(name))
            value = obj.getString(name).trim();
        if ("null".equals(value))
            value = "";
        return value;
    }

    /**
     * 下载基站线程
     *
     * @author jianchao.wang 2014年6月20日
     */
    private class DownloadStationThread extends Thread {

        @Override
        public void run() {
            String ip = mServer.getDownloadFleetIp();
            int port = mServer.getDownloadFleetPort();
            OkHttpClient client = new OkHttpClient();
            for (BaseStationCity city : citySet) {
                StringBuilder http = new StringBuilder();
                http.append("http://").append(ip).append(":").append(port);
                http.append("/Services/CQTCellInfoService.svc/GetCQTCellListFile?");
                http.append("NetworkType=").append(city.networkType);
                http.append("&City=").append(city.cityName);
                LogUtil.d(TAG, http.toString());
                try {
                    Request request = new Request.Builder().url(http.toString()).build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        byte[] result = response.body().bytes();
                        Message msg = null;
                        if (bsService.parseStationBytes(result, handler)) {
                            msg = handler.obtainMessage(DOWNLOAD_STATION_END);
                        } else {
                            msg = handler.obtainMessage(DOWNLOAD_STATION_FAIL);
                        }
                        handler.sendMessage(msg);
                    } else {
                        Message msg = handler.obtainMessage(DOWNLOAD_STATION_FAIL);
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 处理删除过程
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case BaseStationService.SHOW_PROGRESS:
                    progressDialog.setProgress((Integer) msg.obj);
                    break;
                case DOWNLOAD_CITY_END:
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getApplicationContext(), getString(R.string.base_station_download_city_success),
                            Toast.LENGTH_SHORT).show();
                    cityAdapter.notifyDataSetChanged();
                    break;
                case DOWNLOAD_CITY_FAIL:
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getApplicationContext(), getString(R.string.base_station_download_city_fail), Toast.LENGTH_SHORT)
                            .show();
                    break;
                case DOWNLOAD_STATION_END:
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getApplicationContext(), getString(R.string.base_station_download_success), Toast.LENGTH_SHORT)
                            .show();
                    break;
                case DOWNLOAD_STATION_FAIL:
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getApplicationContext(), getString(R.string.base_station_download_fail), Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }

        ;
    };

    /**
     * 更新城市列表
     */
    private void refleshCitys() {
        String ip = this.mServer.getDownloadFleetIp();
        if (!this.mServer.getFleetServerType() || StringUtil.isNullOrEmpty(ip)) {
            Toast.makeText(getApplicationContext(), getString(R.string.base_station_fleet_ip_null), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (!MyPhoneState.getInstance().isNetworkAvirable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.sys_alarm_speech_neterr), Toast.LENGTH_SHORT).show();
            return;
        }
        this.progressDialog = ProgressDialog.show(BaseStationDownloadActivity.this,
                getString(R.string.base_station_download), getString(R.string.base_station_download_message), true);
        new DownloadCityThread().start();
    }

}
