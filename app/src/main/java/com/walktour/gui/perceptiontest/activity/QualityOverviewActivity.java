package com.walktour.gui.perceptiontest.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.walktour.Utils.ToastUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ServerManager;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.gui.R;
import com.walktour.gui.data.DateSelector;
import com.walktour.gui.data.FilterKey;
import com.walktour.gui.perceptiontest.fleet.ProvinceCityModel;
import com.walktour.gui.perceptiontest.fleet.QualityListModel;
import com.walktour.gui.perceptiontest.fleet.QualityTotalModel;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QualityOverviewActivity extends BasicActivity implements SwipeRefreshLayout
        .OnRefreshListener {

    private final String TAG = QualityOverviewActivity.class.getSimpleName();
    private static final int REFRESH_COMPLETE = 0X1290;
    private final String ALL_CITY = "全国";
    private final String ALL_NETWORK = "全网";
    private final String ALL_TIME = "全部";

    /**
     * 上下文
     **/
    private Context context = this;
    protected SharedPreferences sp;
    //选择的城市
    @BindView(R.id.city_namex)
    TextView cityx;
    //网络
    @BindView(R.id.net_workx)
    TextView networkx;
    //时间
    @BindView(R.id.select_timex)
    TextView timex;
    /**
     * 统计数据
     **/
    @BindView(R.id.showTotalLayout)
    LinearLayout showTotalLayout;

    /**
     * 小区列表
     **/
    @BindView(R.id.showList)
    LinearLayout showListLayout;


    /**
     * 窗口标题
     */
    @BindView(R.id.title_txt)
    TextView title_txt;
    /**
     * 列表标题
     **/
    @BindView(R.id.listtitle)
    TextView listTitle;
    @BindView(R.id.showlistview)
    ListView listView;

    @BindArray(R.array.network_select)
    String[] allNetworks;
    @BindString(R.string.str_waitting)
    String strWait;
    /**
     * 显示统计
     **/
    private boolean isShowTotal = true;

    /**
     * 选择的城市,为空时为全国
     */
    private String city = "";
    /**
     * 选择的网络,为空时为全网络
     */
    private String netWork = "";
    /**
     * 选择的时间,为空时为全部
     */
    private String time = "";
    /**
     * 日期选择器使用
     **/
    private TextView timeTV;

    /**
     * 所有城市
     **/
    private boolean isCityFetched = false;
    /**
     * 所有城市
     **/
    private List<String> citys = new LinkedList<>();
    /**
     * 城市适配器
     **/
    private ArrayAdapter<String> cityAdapter;
    //列表数据源
    private List<QualityListModel.DataBean> mDatas = new LinkedList<>();
    //列表适配器
    private MyAdapter mAdapter;
    //分页刷新
    private SwipeRefreshLayout mSwipeLayout;
    //起始记录
    private int startIndex = 0;
    //页大小
    private int pageSize = 10;
    /**
     * 资源概览统计
     */
    QualityTotalModel qualityTotalModel;
    /**
     * 进度提示
     */
    private ProgressDialog progressDialog;
    /**
     * 服务器管理类
     */
    private ServerManager mServer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_overview);
        ButterKnife.bind(this);
        sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        mServer = ServerManager.getInstance(this);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorScheme(android.R.color.holo_green_dark, android.R.color
                        .holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        citys.add(ALL_CITY);
        title_txt.setText(R.string.single_station_test_quality);
        //默认布局
        listTitle.setText(R.string.total_total);
        showTotalLayout.setVisibility(View.VISIBLE);
        showListLayout.setVisibility(View.GONE);
        doSearch();
        //为数据绑定适配器
        mAdapter = new MyAdapter(this, mDatas);
        listView.setAdapter(mAdapter);
    }


    @OnClick(R.id.pointer)
    public void doBackBtn()
    {
        this.finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            this.finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @OnClick(R.id.switchBtn)
    public void doSwichkBtn()
    {
        isShowTotal = !isShowTotal;
        if (!isShowTotal) {
            listTitle.setText(R.string.cell_list_name);
            showTotalLayout.setVisibility(View.GONE);
            showListLayout.setVisibility(View.VISIBLE);
        } else {
            listTitle.setText(R.string.total_total);
            showTotalLayout.setVisibility(View.VISIBLE);
            showListLayout.setVisibility(View.GONE);
        }
        doSearch();
    }


    @OnClick(R.id.searchBtnx2)
    public void searchBtn()
    {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_resource_overview_search,
                null);
        BasicSpinner spinner1 = ButterKnife.findById(view, R.id.city_name);
        cityAdapter = new ArrayAdapter<String>(this, R.layout
                .simple_list_item_1,
                citys);
        spinner1.setAdapter(cityAdapter);
        for (int i = 0; i < citys.size(); i++) {
            if (city.equals("") || city.equals(ALL_CITY)) {
                spinner1.setSelection(0);
                break;
            }
            if (citys.get(i).equals(city)) {
                spinner1.setSelection(i);
                break;
            }
        }

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (position == 0) {
                    city = "";
                } else {
                    city = citys.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        //
        BasicSpinner spinner2 = ButterKnife.findById(view, R.id.network_type);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout
                .simple_list_item_1,
                allNetworks);
        spinner2.setAdapter(adapter2);
        for (int i = 0; i < allNetworks.length; i++) {
            if (netWork.equals("") || netWork.equals(ALL_NETWORK)) {
                spinner2.setSelection(0);
                break;
            }
            if (allNetworks[i].equals(netWork)) {
                spinner2.setSelection(i);
                break;
            }
        }
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position) {
                    case 0:
                        netWork = "";
                        break;
                    case 1:
                        netWork = "CDMA";
                        break;
                    case 2:
                        netWork = "WCDMA";
                        break;
                    case 3:
                        netWork = "LTE";
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        timeTV = ButterKnife.findById(view, R.id.select_time);
        timeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intentDate = new Intent(context, DateSelector.class);
                intentDate.putExtra("flag", TAG);
                startActivityForResult(intentDate, 1);
            }
        });
        new BasicDialog.Builder(this).setTitle(R.string.fleet_eventsearch)
                .setIcon(R.drawable.icon_info)
                .setView(view)
                .setMessage(R.string.main_menu_exit_alert)
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {

                        cityx.setText(city.length() == 0 ? ALL_CITY : city + "");
                        networkx.setText(netWork.length() == 0 ? ALL_NETWORK : netWork + "");
                        timex.setText(timeTV.getText().toString() + "");
                        startIndex=0;
                        mDatas.clear();
                        doSearch();
                    }
                }).setNegativeButton(R.string.str_cancle).show();
        if (!isCityFetched) {
            new GetAllCitys().execute();
        }
    }

    /***
     * 时间选择框
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, android.content.Intent data)
    {
        String timeRang = sp.getString(FilterKey.KEY_TIME_RANGE + TAG, "");
        if (!timeRang.equals("")) {
            String[] tmps = timeRang.split("~");
            String str = tmps[0] + "\n" + tmps[1];
            timeTV.setText(str);
            time = tmps[0]+":00" + "," + tmps[1]+":59";
        } else {
            timeTV.setText(ALL_TIME);
            time = "";
        }

    }


    /***
     * 查询数据
     */
    private void doSearch()
    {
        //查询数据库
        if (isShowTotal) {
            new TotalResouce().execute();
        } else {
            new SearchList().execute();
        }
    }

    /**
     * 打开进度条
     *
     * @param txt
     */
    protected void openDialog(String txt)
    {
        progressDialog = new ProgressDialog(this.context);
        progressDialog.setMessage(txt);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    /**
     * 关闭进度条
     */
    protected void closeDialog()
    {
        progressDialog.dismiss();
    }

    /***
     * 获取所有的城市数据
     */
    private class GetAllCitys extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids)
        {
            try {
                StringBuilder http = new StringBuilder();
                http.append("http://");
                http.append(mServer.getDownloadFleetIp());
                http.append(":");
                http.append(mServer.getDownloadFleetPort());
                http.append("/services/AppService.svc/GetPerformanceCitys");
                LogUtil.w(TAG, "URL=" + http.toString());
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(3000, TimeUnit.MILLISECONDS)
                        .readTimeout(3000, TimeUnit.MILLISECONDS)
                        .build();

                Request request = new Request.Builder().url(http.toString()).build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    LogUtil.w(TAG, "json=" + result);
                    List<ProvinceCityModel> listModel = new ArrayList<>();
                    Gson gson = new Gson();
                    listModel = gson.fromJson(result, new TypeToken<List<ProvinceCityModel>>() {
                    }.getType());
                    for (ProvinceCityModel m : listModel) {
                        citys.add(m.getCity() + "");
                    }

                    isCityFetched = true;
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            openDialog(strWait);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);
            closeDialog();
            if (aBoolean) {
                cityAdapter.notifyDataSetChanged();
            } else {
                ToastUtil.showToastShort(context, R.string.network_request_failed);
            }
        }
    }


    /***
     * 获取全部统计数据
     */
    private class TotalResouce extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids)
        {
            try {
                StringBuilder http = new StringBuilder();
                http.append("http://");
                http.append(mServer.getDownloadFleetIp());
                http.append(":");
                http.append(mServer.getDownloadFleetPort());
                http.append("/services/AppService.svc/GetPerformanceSummary?");
                http.append("City=" + URLEncoder.encode(city, "UTF-8"));
                http.append("&Network=" + URLEncoder.encode(netWork, "UTF-8"));
                http.append("&Time=" + URLEncoder.encode(time, "UTF-8"));
                LogUtil.w(TAG, "URL=" + http.toString());
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(3000, TimeUnit.MILLISECONDS)
                        .readTimeout(3000, TimeUnit.MILLISECONDS)
                        .build();

                Request request = new Request.Builder().url(http.toString()).build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    LogUtil.w(TAG, "json=" + result);


                    Gson gson = new Gson();
                    qualityTotalModel = gson.fromJson(result, QualityTotalModel.class);

                    //显示统计数据
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            openDialog(strWait);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);
            closeDialog();
            LinearLayout gsmLayout = (LinearLayout) findViewById(R.id.id_total_layout);
            gsmLayout.setVisibility(View.GONE);
            if (!aBoolean) {
                ToastUtil.showToastShort(context, R.string.network_request_failed);
            } else {//成功

                gsmLayout.setVisibility(View.VISIBLE);
                TextView tv_1 = (TextView) gsmLayout.findViewById(R.id.tv_1);
                tv_1.setText(qualityTotalModel.getRadio_AccessRatio() + "");
                TextView tv_2 = (TextView) gsmLayout.findViewById(R.id.tv_2);
                tv_2.setText(qualityTotalModel.getLTE_Service_DropRatio() + "");
                TextView tv_3 = (TextView) gsmLayout.findViewById(R.id.tv_3);
                tv_3.setText(qualityTotalModel.getERAB_SetupSuccessRatio() + "");
                TextView tv_4 = (TextView) gsmLayout.findViewById(R.id.tv_4);
                tv_4.setText(qualityTotalModel.getRRC_SetupSuccessRatio() + "");

            }

        }
    }


    /**
     * 查询小区列表
     */
    private class SearchList extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids)
        {
            try {
                StringBuilder http = new StringBuilder();
                http.append("http://");
                http.append(mServer.getDownloadFleetIp());
                http.append(":");
                http.append(mServer.getDownloadFleetPort());
                http.append("/services/AppService.svc/GetPerformanceDetail?");
                http.append("City=" + URLEncoder.encode(city, "UTF-8"));
                http.append("&Network=" + URLEncoder.encode(netWork, "UTF-8"));
                http.append("&Time=" + URLEncoder.encode(time, "UTF-8"));
                http.append("&Start=" + startIndex);
                http.append("&PageSize=" + pageSize);
                LogUtil.w(TAG, "URL=" + http.toString());
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(3000, TimeUnit.MILLISECONDS)
                        .readTimeout(3000, TimeUnit.MILLISECONDS)
                        .build();

                Request request = new Request.Builder().url(http.toString()).build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    startIndex += pageSize;
                    String result = response.body().string();
                    LogUtil.w(TAG, "json=" + result);
                    Gson gson = new Gson();
                    QualityListModel listModel = gson.fromJson(result, QualityListModel.class);
                    if (null != listModel) {
                        for (QualityListModel.DataBean bean : listModel.getData()) {
                            mDatas.add(0, bean);
                        }

                    }
                    //显示统计数据
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
//            openDialog(strWait);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);

//            closeDialog();
            mSwipeLayout.setRefreshing(false);

            if (aBoolean) {
                mAdapter.notifyDataSetChanged();
                listView.invalidate();
            } else {
                ToastUtil.showToastShort(context, R.string.network_request_failed);

            }
        }
    }

    /***
     * 小区列表适配器
     */
    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<QualityListModel.DataBean> mDatas;

        //MyAdapter需要一个Context，通过Context获得Layout.inflater，然后通过inflater加载item的布局
        public MyAdapter(Context context, List<QualityListModel.DataBean> datas)
        {

            mInflater = LayoutInflater.from(context);
            mDatas = datas;
        }

        //返回数据集的长度
        @Override
        public int getCount()
        {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position)
        {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        //这个方法才是重点，我们要为它编写一个ViewHolder
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            MyAdapter.ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_resource_item, parent, false);
                //加载布局
                holder = new MyAdapter.ViewHolder();
                holder.titleTv = (TextView) convertView.findViewById(R.id.textViewTitle);
                convertView.setTag(holder);
            } else {   //else里面说明，convertView已经被复用了，说明convertView中已经设置过tag了，即holder
                holder = (MyAdapter.ViewHolder) convertView.getTag();
            }
            final QualityListModel.DataBean bean = mDatas.get(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("model", bean);
                    jumpActivity(QualityOverviewDetailActivity.class, bundle);
                }
            });
            holder.titleTv.setText(bean.getEquipment_Net_Manager_Name() == null ? "" : bean.getEquipment_Net_Manager_Name().trim());
            return convertView;
        }

        private class ViewHolder {
            TextView titleTv;
            TextView descTv;
        }

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    new SearchList().execute();
                    break;
            }
        }

        ;
    };

    public void onRefresh()
    {
        mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
    }
}

