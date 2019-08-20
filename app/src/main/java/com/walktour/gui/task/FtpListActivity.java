/**
 *
 */
package com.walktour.gui.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.FtpOperate;
import com.walktour.control.config.ConfigFtp;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.applet.ControlBar;
import com.walktour.model.FtpListUtilModel;
import com.walktour.model.FtpServerModel;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * ftp目录浏览器
 *
 * @author zhihui.lian
 */
public class FtpListActivity extends BasicActivity implements OnItemClickListener, OnClickListener {
    public ListView mListView;

    private CustomListAdapter customadapter = null;

    private List<FTPFile> files = new ArrayList<FTPFile>();


    private ProgressDialog ftpDialog;

    //private CutFtp cutFtp;
    private List<String> dirList = new ArrayList<String>();

    private FtpOperate cutFtp;

    private int ftppos = 1;

    private FtpServerModel ftpServerModel;

    private TextView title;

    /*底部工具栏*/
    private ControlBar toolBar;

    private Button btnQuite;

    private Button btnReturn;

    private Button btnRefresh;

    private ImageView pointer; //顶部返回按钮

    private boolean isConnect;

    private FtpListUtilModel ftpUtil;

    private AbstractMap<String, List<FTPFile>> dataCache;

    private FTPFile ftpFile;

    private StringBuffer filedir;

    public long startTime;

    private int count = 0;


    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.listview_ftp);
        ftpUtil = FtpListUtilModel.getInstance();
        mListView = (ListView) findViewById(R.id.ListView01);
        title = initTextView(R.id.title_txt);
        title.setEllipsize(TextUtils.TruncateAt.valueOf("START"));
        pointer = initImageView(R.id.pointer);
        pointer.setOnClickListener(this);
        dataCache = new ConcurrentHashMap<String, List<FTPFile>>();
        dirList.add("");   //加入默认路径
        customadapter = new CustomListAdapter(this);
        mListView.setAdapter(customadapter);
        mListView.setOnItemClickListener(this);
        /* 显示一个对话框 */
        getIntentExtra();     //获得携带的数据，已经加载服务器配置文件
        setTitle();
        this.genToolBar();
        startTime = System.currentTimeMillis();
        count++;
        new getFtpList().execute();
    }


    private void setTitle() {
        String filePath = (filedir == null ? "" : filedir.toString());
        title.setText("ftp://" + ftpServerModel.getIp() + filePath);
    }


    private void getIntentExtra() {
        ConfigFtp config_ftp = new ConfigFtp();
        ftppos = ftpUtil.getServerPosition();
        String ftpServer = config_ftp.getNameFirstEmpty(ftppos, getApplicationContext());
        ftpServerModel = config_ftp.getFtpServerModel(ftpServer);
    }


    private void showDialog() {
        ftpDialog = new ProgressDialog(FtpListActivity.this);
        ftpDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    if (ftpDialog.isShowing()) {
                        ftpDialog.dismiss();
                        return true;
                    }
                }
                return false;
            }
        });
        ftpDialog.setCancelable(false);
        ftpDialog.setMessage(getString(R.string.sys_ftp_searching) + "");
        ftpDialog.show();
    }


    Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mListView.setAdapter(customadapter);
            ftpDialog.cancel();
            Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    private Button btnOk;


    /**
     * 异步执行任务
     *
     * @author Administrator
     */

    class getFtpList extends AsyncTask {


        @Override
        protected Object doInBackground(Object... params) {
            // TODO Auto-generated method stub
            files.clear();
            if (cutFtp == null) {
                cutFtp = new FtpOperate(getApplicationContext());
            }
            try {
                if (!cutFtp.isConnect()) {
                    final ExecutorService exec = Executors.newFixedThreadPool(1);

                    Callable<String> call = new Callable<String>() {
                        public String call() throws Exception {
                            //开始执行耗时操作
                            isConnect = cutFtp.connect(ftpServerModel);
                            return "线程执行完成.";
                        }
                    };

                    try {
                        Future<String> future = exec.submit(call);
                        String obj = future.get(1000 * 30, TimeUnit.MILLISECONDS); //任务处理超时时间设为 1 秒
                        System.out.println("任务成功返回:" + obj);
                    } catch (TimeoutException ex) {
                        System.out.println("处理超时啦....");
                        ex.printStackTrace();
                        Message msg = updateHandler.obtainMessage();
                        msg.obj = getString(R.string.fleet_set_notset_notify);
                        updateHandler.sendMessage(msg);
                    } catch (Exception e) {
                        System.out.println("处理失败.");
                        e.printStackTrace();
                    } finally {
                        // 关闭线程池
                        exec.shutdown();
                    }
                }
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                Message msg = updateHandler.obtainMessage();
                msg.obj = getString(R.string.fleet_set_notset_notify);
                updateHandler.sendMessage(msg);
                return null;
            }

            if (isConnect) {
                try {
                    if (count == 1) {
                        dirList.remove(0);
                        try {
                            String ftpWorkDir = cutFtp.getFtpClient().printWorkingDirectory();
                            dirList.add(ftpWorkDir.endsWith(File.separator) ? ftpWorkDir + "" : ftpWorkDir + File.separator);   //加入初始路径
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        count++;
                    }
                    filedir = new StringBuffer();

                    TreeSet<FTPFile> fileTree = null;

                    fileTree = new TreeSet<FTPFile>(
                            new Comparator<Object>() {
                                //先按照文件的类型排序(倒排)，然后按文件名顺序排序
                                public int compare(Object objFile1, Object objFile2) {
                                    if (objFile1 == null)
                                        return -1;
                                    else if (objFile2 == null)
                                        return 1;
                                    else {
                                        FTPFile file1 = (FTPFile) objFile1;
                                        FTPFile file2 = (FTPFile) objFile2;
                                        if (file1.getType() != file2.getType())
                                            return file2.getType() - file1.getType();
                                        else
                                            return file1.getName().compareTo(file2.getName());
                                    }
                                }
                            }
                    );
                    for (int i = 0; i < dirList.size(); i++) {
                        if (dirList.get(i).trim().length() == 0) {
                            filedir.append("");
                        } else {
                            filedir.append(dirList.get(i).endsWith("/") ? dirList.get(i) : dirList.get(i) + "/");
                        }
                    }
                    System.out.println("dataCache>>>" + dataCache.size() + "    " + dataCache.get(filedir.toString()) + "    " + filedir.toString());
                    FTPFile[] listFile = cutFtp.getFTPLists(filedir.toString(), ftpServerModel);
                    Log.i("ftp list ", listFile.length + "");
                    if (listFile != null && listFile.length > 0) {
                        // 如果模板浏览类型为Upload
                        for (int i = 0; i < listFile.length; i++) {
                            ftpFile = listFile[i];
                            if (!ftpFile.getName().equals(".") && !ftpFile.getName().equals("..")) {
                                if (ftpUtil.getDlOrUl() == 2) {
                                    if (ftpFile.isDirectory()) {
                                        fileTree.add(ftpFile);
                                    }
                                } else {
                                    fileTree.add(ftpFile);
                                }
                            }
                        }
                        for (FTPFile file : fileTree) {
                            files.add(file);
                        }

                        System.out.println("目录路径" + filedir.toString());
                        if (!filedir.toString().equals("/")) {
                            ftpFile = new FTPFile();
                            ftpFile.setName(".." + getString(R.string.up_directory));
                            ftpFile.setType(FTPFile.DIRECTORY_TYPE);
                            files.add(0, ftpFile);
                        }
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    Message msg = updateHandler.obtainMessage();
                    msg.obj = getString(R.string.getftpTimeOUt);
                    updateHandler.sendMessage(msg);
                }

            } else {
                Message msg = updateHandler.obtainMessage();
                msg.obj = getString(R.string.fleet_error_login);
                updateHandler.sendMessage(msg);
            }

            return null;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            setTitle();
            mListView.setAdapter(customadapter);
            ftpDialog.cancel();
        }


        /**
         * 开始准备ui工作
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }


    }


    /**
     * 列表适配器
     */

    class CustomListAdapter extends BaseAdapter {

        // 上下文
        Context context;


        // 构造器
        public CustomListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return files.size();
        }

        @Override
        public Object getItem(int position) {
            return files.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {

                convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_style15, null);
                viewHolder = new ViewHolder();
                // 设置item中indexText的文本
                viewHolder.ItemTitle = (TextView) convertView.findViewById(R.id.ItemTitle);
                viewHolder.fileImg = (ImageView) convertView.findViewById(R.id.ItemImage);
                viewHolder.ItemSize = (TextView) convertView.findViewById(R.id.ItemSize);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            FTPFile ftp = files.get(position);

            long size = ftp.getSize() / 1000;
            String measure = size > 0 ? "###,###KB" : "###,###B";
            size = size > 0 ? size : ftp.getSize();


            if (ftp.isFile()) {
                if (ftp.getName().endsWith(".rar") || ftp.getName().endsWith(".zip")) {
                    viewHolder.fileImg.setImageResource(R.drawable.file_rar);
                } else {
                    viewHolder.fileImg.setImageResource(R.drawable.file);
                }
                viewHolder.ItemSize.setVisibility(View.VISIBLE);
            } else if (ftp.isDirectory()) {
                viewHolder.fileImg.setImageResource(R.drawable.folder);
                viewHolder.ItemSize.setVisibility(View.GONE);
            } else {
                viewHolder.fileImg.setImageResource(R.drawable.file2);

            }
            viewHolder.ItemSize.setText(new java.text.DecimalFormat(measure).format(size));
            viewHolder.ItemTitle.setText(ftp.getName());
            return convertView;
        }


        private class ViewHolder {

            TextView ItemTitle;

            ImageView fileImg;

            TextView ItemSize;
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
        if (index == 0 && !dirList.toString().equals("[/]")) {
            backUp();
        } else {
            FTPFile ftpf = files.get(index);
            if (ftpf.isFile()) {
                StringBuffer fdir = new StringBuffer();
                for (int i = 0; i < dirList.size(); i++) {
                    fdir.append(dirList.get(i));
                }

                Intent data = getIntent().putExtra("path", fdir.append(fdir.toString().endsWith("/") ? (files.get(index).getName().toString()) : "/" + files.get(index).getName()).toString());
                finish();
                FtpListActivity.this.setResult(RESULT_OK, data);
            } else if (ftpf.isDirectory()) {
                dirList.add(ftpf.getName() + "/");
                new getFtpList().execute();
            } else {
                Toast.makeText(this, getString(R.string.ftp_getfilelist_tip), Toast.LENGTH_SHORT).show();
            }
        }
    }

    ;

    /**
     * @Override public boolean dispatchKeyEvent(KeyEvent event) {
     * if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() != KeyEvent.ACTION_UP){
     * if(ftpDialog.isShowing()){
     * ftpDialog.dismiss();
     * }else{
     * backUp();
     * }
     * return true;
     * }
     * return super.dispatchKeyEvent(event);
     * }
     */


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() != KeyEvent.ACTION_UP) {
            Log.d("ftp list", "come in key");
            if (ftpDialog.isShowing()) {
                ftpDialog.dismiss();
            } else {
                backUp();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void finish() {
        Log.i("ftp", "finish");
        Intent data = getIntent().putExtra("POS", ftppos);
        FtpListActivity.this.setResult(RESULT_OK, data);
        super.finish();
    }


    /**
     * 返回上一级目录
     */

    @SuppressWarnings("unchecked")
    public void backUp() {
        String[] dirStr = dirList.get(0).toString().split("/");
        if (!dirList.get(0).equals("/")) {
            dirList.remove(0);
            for (int i = 0; i < dirStr.length; i++) {
                dirList.add(i, dirStr[i] + "/");
            }
        }
        if (dirList.size() > 1) {
            dirList.remove(dirList.size() - 1);
            new getFtpList().execute();
        } else if (dirList.get(0).equals("/")) {
            finish();
        }
    }


    /**
     * 生成底部工具栏目
     */
    private void genToolBar() {
        toolBar = (ControlBar) findViewById(R.id.ControlBar);

        btnQuite = toolBar.getButton(0);
        btnQuite.setText(R.string.str_cancle);

        btnQuite.setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.controlbar_undo,
                0,
                0);
        btnQuite.setOnClickListener(this);

        btnRefresh = toolBar.getButton(1);
        btnRefresh.setText(R.string.total_wap_refresh);

        btnRefresh.setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.controlbar_refresh,
                0,
                0);
        btnRefresh.setOnClickListener(this);


        btnReturn = toolBar.getButton(2);
        btnReturn.setText(getString(R.string.up_directory));
        btnReturn.setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.controlbar_up,
                0,
                0);
        btnReturn.setOnClickListener(this);


        if (ftpUtil.getDlOrUl() == 2) {
            btnOk = toolBar.getButton(3);
            btnOk.setText(R.string.str_ok);
            btnOk.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.controlbar_select,
                    0,
                    0);
            btnOk.setOnClickListener(this);

        }


    }


    @Override
    @SuppressWarnings("unchecked")
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int viewId = v.getId();
        switch (viewId) {
            //返回
            case R.id.Button01:
                finish();
                overridePendingTransition(0, R.anim.slide_in_down);
                break;

            //刷新
            case R.id.Button02:
                new getFtpList().execute();
                break;

            //上一级目录
            case R.id.Button03:
                backUp();
                break;

            case R.id.Button04:
                String str = "";
                if (filedir != null)
                    str = filedir.toString();
                Intent data = getIntent().putExtra("path", str);
                finish();
                FtpListActivity.this.setResult(RESULT_OK, data);
                break;


            case R.id.pointer:
                finish();
                overridePendingTransition(0, R.anim.slide_in_down);
            default:
                break;
        }
    }


}
