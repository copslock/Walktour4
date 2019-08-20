package com.walktour.gui.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.walktour.Utils.FileUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.control.config.SpeedTestParamter;
import com.walktour.control.config.SpeedTestParamter.ServerInfo;
import com.walktour.control.config.SpeedTestSetting;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TaskSpeedTestServer extends BasicActivity implements OnItemSelectedListener {
    private static final String tag = "TaskSpeedTestServer";
    private Context mContext=TaskSpeedTestServer.this;
    private SpeedTestSetting spSetting;
    private Map<String, SpeedTestParamter> speedCountrylist=new HashMap<>();
    List<SpeedTestParamter.ServerInfo> countryInfoList=new LinkedList<>();
    private Spinner spCountry;
    private Spinner spCity;
    private ListView lv;
//    private EditText etURL;
    private Button btnUpdate;
    private Button btnOK;
    private Button btnCancel;
    String url;
    String initcountry;
    String initcity;
    int countryPosition;
    int cityPosition;
    String country = "";
    String city = "";
    private int selectPosition = -1;
    private List<String> cityList = new ArrayList<String>();
    //创建ArrayList对象 并添加数据
    ArrayList<HashMap<String, Object>> siteList = new ArrayList<HashMap<String, Object>>();
    Map<String, Object> selectMap=new HashMap<String, Object>();
    HashMap<String, Object> sites;
    /**
     * 进度框
     */
    private ProgressDialog dialog = null;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.speedtest_server_edit);
        spSetting = SpeedTestSetting.getInstance();
        countryInfoList.clear();
        speedCountrylist.clear();
        Intent intent = getIntent();
        url = intent.getStringExtra("URL");
        initcountry = intent.getStringExtra("Country");
        initcity = intent.getStringExtra("City");

        countryPosition = initcountry.split("@@").length > 1 ?
                Integer.parseInt(initcountry.split("@@")[1]) : 0;
        cityPosition = initcity.split("@@").length > 1 ?
                Integer.parseInt(initcity.split("@@")[1]) : 0;
        findView();
    }

    private void findView()
    {
        speedCountrylist=spSetting.getParaList();
        (initTextView(R.id.title_txt)).setText(R.string.task_speedtest_urlsetting); //设置标题
        initImageButton(R.id.pointer).setOnClickListener(clickListener);
        btnUpdate= initButton(R.id.update_speedtest_server);
        btnUpdate.setOnClickListener(clickListener);
        btnOK = initButton(R.id.btn_ok);
        btnOK.setOnClickListener(clickListener);
        btnCancel = initButton(R.id.btn_cencle);
        btnCancel.setOnClickListener(clickListener);

        spCountry = (Spinner) findViewById(R.id.edit_country);
        String[] countryArray = new String[speedCountrylist.size()];
        int i = 0;
        for (Iterator<Entry<String, SpeedTestParamter>> it =
			 speedCountrylist.entrySet().iterator(); it.hasNext(); ) {
            Entry<String, SpeedTestParamter> entry = it.next();
            countryArray[i] = entry.getValue().getCountry();
            i++;
        }

        ArrayAdapter<String> countryCodeAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout, countryArray);
        countryCodeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spCountry.setAdapter(countryCodeAdapter);
        spCountry.setSelection(countryPosition);
        spCountry.setOnItemSelectedListener(this);

        spCity = (Spinner) findViewById(R.id.edit_city);
        spCity.setOnItemSelectedListener(this);
        lv = (ListView) findViewById(R.id.urlList);

    }




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        siteList.clear();
        int selected = 0;
        int i = 0;
        if (parent.getId() == R.id.edit_country) {
            cityList.clear();
            for (Iterator<Entry<String, SpeedTestParamter>> it =
				 speedCountrylist.entrySet().iterator(); it.hasNext(); ) {
                Entry<String, SpeedTestParamter> entry = it.next();
                if (i == position) {
                    country = entry.getValue().getCountry() + "@@" + position;
                    countryInfoList = entry.getValue().getServerInfoList();
                    for (SpeedTestParamter.ServerInfo info : countryInfoList) {
                        if (!cityList.contains(info.getName())) {
                            cityList.add(info.getName());
                        }

                        if (info.getName() != null && info.getName().equals(city)) {
                            selected = i;
                        }
                    }

                    //退出for循环
                    break;
                }
                i++;
            }

            ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this,
                    R.layout.simple_spinner_custom_layout, cityList);
            cityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spCity.setAdapter(cityAdapter);

            if (countryPosition == position && cityList.size() > cityPosition) {
                spCity.setSelection(cityPosition);
            } else {
                countryPosition = position;
                spCity.setSelection(0);
            }
        } else if (parent.getId() == R.id.edit_city) {
            city = cityList.get(position);
            selected = -1;
            for (SpeedTestParamter.ServerInfo info : countryInfoList) {
                if (info.getName() != null && info.getName().equals(city) && !siteList.contains(info.getUrl())) {
                    sites = new HashMap<String, Object>();
                    sites.put("Key", info.getUrl());
                    sites.put("entity", info);
                    siteList.add(sites);
                    if (info.getUrl() != null && info.getUrl().equals(url)) {
                        selected = i;
                    }
                }
                i++;
            }

            final MyAdapter myAdapter = new MyAdapter(this,siteList);
            lv.setAdapter(myAdapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //获取选中的参数
                    selectPosition = position;
                    myAdapter.notifyDataSetChanged();
                    selectMap = siteList.get(position);
                    url=selectMap.get("Key").toString();
                }
            });
            if (selected != 0)
                lv.setSelection(selected);
            city = cityList.get(position) + "@@" + position;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
    }


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            if (v.getId() == R.id.btn_ok) {
                Intent intent = getIntent();
                intent.putExtra("Country", country);
                intent.putExtra("URL", url);
                intent.putExtra("City", city);
                TaskSpeedTestServer.this.setResult(RESULT_OK, intent);
                finish();
            }else if (v.getId() == R.id.btn_cencle||v.getId() ==R.id.pointer) {
                finish();
            }else{
                new DownLoadSpeedTestUrl().execute();
            }

        }
    };
    public class MyAdapter extends BaseAdapter {
        Context context;
        List<HashMap<String, Object>> brandsList;
        LayoutInflater mInflater;
        public MyAdapter(Context context,List<HashMap<String, Object>> mList){
            this.context = context;
            this.brandsList = mList;
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return brandsList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.listview_item_speedtest,parent,false);
                viewHolder = new ViewHolder();
                viewHolder.urlPath = (TextView)convertView.findViewById(R.id.ItemText);
                viewHolder.idSelect = (RadioButton)convertView.findViewById(R.id.id_select);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.urlPath.setText(brandsList.get(position).get("Key").toString());
            if(selectPosition == position){
                viewHolder.idSelect.setChecked(true);
            }
            else{
                viewHolder.idSelect.setChecked(false);
            }

            if(null!=url&&brandsList.get(position).get("Key").toString().equals(url)){
                viewHolder.idSelect.setChecked(true);
            }

            return convertView;
        }
    }
    /**
     * 列表数据
     */
    public class ViewHolder{
        TextView urlPath;
        RadioButton idSelect;
    }

    /***
     * 下载speed test url的地址
     *
     * @author weirong.fan
     *
     */
    private class DownLoadSpeedTestUrl extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            openDialog(getString(R.string.exe_info));
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            super.onPostExecute(result);
            countryInfoList.clear();
            speedCountrylist.clear();
            spSetting.reloadData();
            findView();
            closeDialog();
            if(result){
                ToastUtil.showToastShort(mContext,R.string.total_success);
            }else{
                ToastUtil.showToastShort(mContext,R.string.total_faild);
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            // url下载地址
            String speedTestUrl = "http://c.speedtest.net/speedtest-servers-static.php";
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                System.out.println("当前时间：" + sdf.format(System.currentTimeMillis()));
                String filePath = AppFilePathUtil.getInstance().getAppConfigDirectory() + "speedtest_server.xml";
                downloadFile(speedTestUrl, filePath);
//                System.out.println("当前时间：" + sdf.format(System.currentTimeMillis()));
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {

            }
            return false;
        }

        private boolean downloadFile(String httpUrl, String savePath) {
            int byteread = 0;
            HttpURLConnection conn = null;
            InputStream inStream = null;
            FileOutputStream fs = null;
            try {
                URL url = new URL(httpUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK) {
                    FileUtil.deleteFile(savePath);//先删除文件
                    inStream = conn.getInputStream();
                    fs = new FileOutputStream(savePath);
                    byte[] buffer = new byte[1024 * 5];
                    while ((byteread = inStream.read(buffer)) != -1) {
                        fs.write(buffer, 0, byteread);
                    }
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                if (null != inStream) {
                    try {
                        inStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    inStream = null;
                }

                if (null != fs) {
                    try {
                        fs.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    fs = null;
                }

                if (null != conn) {
                    try {
                        conn.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    conn = null;
                }
            }
            return false;
        }
    }

    /**
     * 打开进度条
     *
     * @param txt
     */
    protected void openDialog(String txt) {
        dialog = new ProgressDialog(this);
        dialog.setMessage(txt);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }
    /**
     * 关闭进度条
     */
    protected void closeDialog() {
        dialog.dismiss();
    }

}
