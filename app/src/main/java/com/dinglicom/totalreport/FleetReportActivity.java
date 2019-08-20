package com.dinglicom.totalreport;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.ToastUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.DownloadUtil;
import com.walktour.control.config.ServerManager;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 从fleet平台获取报告
 */
public class FleetReportActivity extends BasicActivity implements SwipeRefreshLayout.OnRefreshListener{
    private final String TAG="FleetReportActivity";
    private TextView title;
    private ImageView pointer;
    private ListView listView = null;
    private MyAdapter adapter = new MyAdapter();
    private ArrayList<ReportInfo> list=new ArrayList<ReportInfo>();
    /** 服务器管理类 */
    private ServerManager mServer;
    /** 进度提示 */
    private ProgressDialog progressDialog;
    private Context context=FleetReportActivity.this;
    private SwipeRefreshLayout mSwipeLayout;
    private static final int REFRESH_COMPLETE = 0X1213;
    private int index=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fleet_report);
        mServer = ServerManager.getInstance(this);
        title = initTextView(R.id.title_txt);
        title.setText(getString(R.string.total_reportlist_title_str));
        pointer = initImageView(R.id.pointer);
        pointer.setOnClickListener(this);
        listView = this.initListView(R.id.ListView01);
        listView.setAdapter(adapter);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorScheme(android.R.color.holo_green_dark, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        new FetchReport().execute();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    new FetchReport().execute();
                    break;
            }
        };
    };
    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
    }

    /**
     * 获取报告
     */
    private class FetchReport extends AsyncTask<Void, Void, ArrayList<ReportInfo>> {
        private FetchReport() {
            super();
        }

        @Override
        protected void onPostExecute(ArrayList<ReportInfo> reportListList) {
            super.onPostExecute(reportListList);
            closeDialog();
            mSwipeLayout.setRefreshing(false);
            if (null!=reportListList) {
                list.addAll(reportListList);
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            openDialog(getString(R.string.share_project_server_doing));
        }

        @Override
        protected ArrayList<ReportInfo> doInBackground(Void... params) {
            try {
                StringBuffer url = new StringBuffer();
                url.append("http://");
                url.append(mServer.getDownloadFleetIp());
                url.append(":");
                url.append(mServer.getDownloadFleetPort());
                url.append("/Services/PadCellInfoService.svc/GetReportDatas?");
                url.append("userName=" + mServer.getFleetAccount());
                url.append("&index="+index);
                url.append("&count=10");
//                LogUtil.w(TAG, "get url is=" + url.toString());
                OkHttpClient okHttpClient_get = new OkHttpClient();
                Request request = new Request.Builder()
                        .get()
                        .url(url.toString())
                        .build();
                Response response = okHttpClient_get.newCall(request).execute();
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    String str=response.body().string();
//                    LogUtil.w(TAG,"response is="+str);
                    if(null==str||str.trim().length()<=0){
                        return null;
                    }
                    if(str.startsWith("\"")&&str.endsWith("\"")) {
                        str = str.substring(1, str.length() - 1);
                    }
                    str=str.replace("\\","");
//                    LogUtil.w(TAG,"response is="+str);
                    FleetReport report=gson.fromJson(str,FleetReport.class);
                    if(index+10<=report.getTotalCount()){
                        index+=10;
                    }else{
                        if(index>report.getTotalCount()) {
                            return null;
                        }
                        index+=(report.getTotalCount()-index);
                    }

                    return report.getReportListList();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                ;
            }
            return null;
        }
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
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pointer://返回
                finish();
                break;
            default:
                break;
        }
    }

    private class MyAdapter extends BaseAdapter {
        private MyAdapter() {
            super();
        }
        @Override
        public int getCount() {
            return null == list ? 0 : list.size();
        }
        @Override
        public Object getItem(int position) {
            return null == list ? null : list.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView){
                LayoutInflater inflate = (LayoutInflater) FleetReportActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                convertView = inflate.inflate(R.layout.weifuwumain3layout_childs, null);
            }
            ImageView imageView = (ImageView) convertView.findViewById(R.id.objimg);
            TextView textView1 = (TextView) convertView.findViewById(R.id.name1);
            TextView textView2 = (TextView) convertView.findViewById(R.id.name2);
            TextView textView3 = (TextView) convertView.findViewById(R.id.joinnumbers);
            Button add = (Button) convertView.findViewById(R.id.addmem);
            Button refuse = (Button) convertView.findViewById(R.id.refusemem);
            final ReportInfo m = list.get(position);
            imageView.setBackgroundResource(R.drawable.obj_group);
            textView1.setText(m.getReportName()+"");
            textView2.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);
//            add.setVisibility(View.GONE);
            add.setText(R.string.download);
            add.setBackgroundResource(R.drawable.bg2);
            refuse.setVisibility(View.GONE);

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialog(getString(R.string.downloading));
                    DownloadUtil.getInstance().download(m.getDownloadUrl(), AppFilePathUtil.getInstance().getSDCardBaseDirectory(getString(R.string.fleet_report)), new DownloadUtil.OnDownloadListener() {
                        @Override
                        public void onDownloadSuccess(String path) {
                            closeDialog();
                            ToastUtil.showToastShort(context,R.string.download_file_finish);
                        }

                        @Override
                        public void onDownloading(int progress) {

                        }

                        @Override
                        public void onDownloadFailed() {
                            closeDialog();
                            ToastUtil.showToastShort(context,R.string.download_fail);
                        }
                    });
                }
            });
            return convertView;
        }
    }



    /**
     * Fleet返回的报表
     */
    private class FleetReport{

        @SerializedName("TotalCount")
        private int totalCount=0;
        @SerializedName("ReportList")
        private ArrayList<ReportInfo> reportListList=new ArrayList<ReportInfo>();

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public ArrayList<ReportInfo> getReportListList() {
            return reportListList;
        }

        public void setReportListList(ArrayList<ReportInfo> reportListList) {
            this.reportListList = reportListList;
        }
    }

    /**
     * 报表信息
     */
    private class ReportInfo{
        @SerializedName("ReportName")
        private String reportName;
        @SerializedName("CreateDateTime")
        private String createDateTime;
        @SerializedName("DownloadUrl")
        private String downloadUrl;

        public String getReportName() {
            return reportName;
        }

        public void setReportName(String reportName) {
            this.reportName = reportName;
        }

        public String getCreateDateTime() {
            return createDateTime;
        }

        public void setCreateDateTime(String createDateTime) {
            this.createDateTime = createDateTime;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }
    }
}
