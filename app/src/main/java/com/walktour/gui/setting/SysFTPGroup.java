/*
 * 文件名: SysFTPGroup.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2013-6-4
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.gui.setting;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkStruct.TaskType;
import com.walktour.control.adapter.FTPGroupAdapter;
import com.walktour.control.config.ConfigFtp;
import com.walktour.framework.ui.GroupBasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.applet.FileExplorer;
import com.walktour.gui.task.TaskMultiFTPDownload;
import com.walktour.model.FTPGroupModel;

import java.util.ArrayList;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2013-6-4] 
 */
public class SysFTPGroup extends GroupBasicActivity implements OnClickListener{
    
    public static final String TASKTYPE_KEY ="tasktype_key";
    
    public TaskType taskType; 
    
    /**
     * FTP 服务器组
     */
    //private ListView ftpServerListView;
    
    private ArrayList<FTPGroupModel> ftpGroupList;
    
    private FTPGroupAdapter ftpGroupAdapter;
    
    private Button newgroupBtn;
    
    private Button deleteBtn;

    LinearLayout serverGroupLay;
    
    View convertView = null;
    
    private ConfigFtp mConfigFtp;
    
    private boolean reinit = true;
    
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param savedInstanceState
     * @see com.walktour.framework.ui.BasicActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sys_ftp_group);
        ftpGroupList = getIntent().getParcelableArrayListExtra(TaskMultiFTPDownload.SERVER_GROUP_KEY);
        taskType = (TaskType) getIntent().getSerializableExtra(SysFTPGroup.TASKTYPE_KEY);
        findView();
        IntentFilter filter = new IntentFilter();
        filter.addAction(FileExplorer.ACTION_LOAD_NORMAL_FILE);
        this.registerReceiver(mReceiver, filter);
    }
    
    /**
     * 初始化View控件<BR>
     * [功能详细描述]
     */
    private void findView(){
        (initTextView(R.id.title_txt)).setText(R.string.task_ftp_servergroup);
        (initImageView(R.id.pointer)).setOnClickListener(this);
        newgroupBtn = (initButton(R.id.btn_cencle));
        deleteBtn = (initButton(R.id.btn_ok));
        newgroupBtn.setText(R.string.act_task_new);
        deleteBtn.setText(R.string.delete);
        newgroupBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        
        ftpGroupAdapter = new FTPGroupAdapter(this,ftpGroupList,taskType);
        //ftpServerListView = (ListView) findViewById(R.id.ftpgroup_list);
        serverGroupLay = initLinearLayout(R.id.server_group_lay);
        //ftpServerListView.setAdapter(ftpGroupAdapter);
        notifyDataChange();
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param v
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pointer:
                saveFTPInfo();
                break;
            //新建
            case R.id.btn_cencle:
                synchronized (v) {
                    
                }
                if(ftpGroupList == null){
                    ftpGroupList = new ArrayList<FTPGroupModel>();
                }else {
                    if(ftpGroupList.size() == 5){
                        return;
                    }
                }
                FTPGroupModel ftpModel = new FTPGroupModel();
                ftpModel.setEnable(0);
                ftpModel.setFileSize(1000);
                ftpModel.setFileSource(1);
                ftpGroupList.add(ftpModel);
                reinit = true;
                notifyDataChange();
                break;
            //删除
            case R.id.btn_ok:
                boolean selectFTP = false;
                if(ftpGroupList != null){
                    for (int i = ftpGroupList.size() - 1; i >= 0; i--) {
                        if(ftpGroupList.get(i).getEnable() == 1){
                            selectFTP = true;
                            ftpGroupList.remove(i);
                        }
                    }
                }
                if(!selectFTP){
                    Toast.makeText(SysFTPGroup.this, R.string.str_check_non, Toast.LENGTH_LONG).show();
                }
                serverGroupLay.removeAllViews();
                reinit = true;
                notifyDataChange();
                break;
            default:
                break;
        }
        
    }
    
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(FileExplorer.ACTION_LOAD_NORMAL_FILE)) {
                String filePath = "";
                try {
                    filePath = intent.getStringExtra(FileExplorer.KEY_FILE);
                } catch (Exception e) {
                    
                }
                ftpGroupAdapter.setLocalFile(filePath);
            }
            
        }
    };
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 55 && resultCode == RESULT_OK){
                String remoteStr = data.getExtras().getString("path");
                if( remoteStr != null){
                    ftpGroupAdapter.setDownloadFile(remoteStr);
                }
        }
        if(requestCode == 56 && resultCode == RESULT_OK){
            String remoteStr = data.getExtras().getString("path");
            if( remoteStr != null){
                ftpGroupAdapter.setUploadFile(remoteStr);
            }
        } 
        
            
    }
    
    /**
     * 刷新FTP组信息
     * [一句话功能简述]<BR>
     * [功能详细描述]
     */
    private synchronized void notifyDataChange(){
        if(reinit){
            mConfigFtp = new ConfigFtp();
            ftpGroupAdapter.setConfigFtp(mConfigFtp);
            ftpGroupAdapter.setFtpGroupList(ftpGroupList);
            if(ftpGroupList != null  && ftpGroupList.size() > 0){
                serverGroupLay.removeAllViews();
                for (int i = 0; i < ftpGroupList.size(); i++) {
                    serverGroupLay.addView(ftpGroupAdapter.getView(i, convertView, null),new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                }   
            } 
            reinit = false;
        }
    }
    
    
    /**
     * 保存FTP信息<BR>
     * [功能详细描述]
     * @return
     */
    private boolean saveFTPInfo(){
        if(taskType == TaskType.MultiftpDownload){
            for (FTPGroupModel ftpGroupModel : ftpGroupList) {
                if(ftpGroupModel.getEnable() == 1){
                    if(StringUtil.isNullOrEmpty(ftpGroupModel.getFtpServerName()) || StringUtil.isNullOrEmpty(ftpGroupModel.getDownloadFile())){
                        Toast.makeText(this, R.string.alert_input_error, Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
            }
        }else {
            for (FTPGroupModel ftpGroupModel : ftpGroupList) {
                if(ftpGroupModel.getEnable() == 1 ){
                    if(ftpGroupModel.getFileSource() ==  1){
                        if(StringUtil.isNullOrEmpty(ftpGroupModel.getFtpServerName()) || StringUtil.isNullOrEmpty(ftpGroupModel.getLocalFile()) 
                                || StringUtil.isNullOrEmpty(ftpGroupModel.getUploadFilePath())){
                            Toast.makeText(this, R.string.alert_input_error, Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }else {
                        if(StringUtil.isNullOrEmpty(ftpGroupModel.getFtpServerName()) || StringUtil.isNullOrEmpty(String.valueOf(ftpGroupModel.getFileSize())) 
                                || StringUtil.isNullOrEmpty(ftpGroupModel.getUploadFilePath())){
                            Toast.makeText(this, R.string.alert_input_error, Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }

                }
            }
        }
        Intent dataIntent = new Intent();
        dataIntent.putParcelableArrayListExtra(TaskMultiFTPDownload.SERVER_GROUP_KEY, ftpGroupList);
        setResult(Activity.RESULT_OK, dataIntent);  
        SysFTPGroup.this.finish(); 
        return true;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @see com.walktour.framework.ui.BasicActivity#onResume()
     */
    
    @Override
    protected void onResume() {
        notifyDataChange();
        super.onResume();
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @see com.walktour.framework.ui.BasicActivity#onPause()
     */
    @Override
    protected void onPause() {
        reinit = true;
        super.onPause();
    }
    
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mReceiver);
    };
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param keyCode
     * @param event
     * @return
     * @see com.walktour.framework.ui.BasicActivity#onKeyUp(int, android.view.KeyEvent)
     */
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
            return saveFTPInfo();
        }
        return super.onKeyUp(keyCode, event);
    }
}
