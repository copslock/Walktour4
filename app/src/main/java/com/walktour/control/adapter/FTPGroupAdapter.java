/*
 * 文件名: FTPGroupAdapter.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2013-6-5
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.control.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkStruct.TaskType;
import com.walktour.control.config.ConfigFtp;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.applet.FileExplorer;
import com.walktour.gui.setting.Sys;
import com.walktour.gui.task.FtpListActivity;
import com.walktour.model.FTPGroupModel;
import com.walktour.model.FtpListUtilModel;

import java.util.List;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2013-6-5] 
 */
public class FTPGroupAdapter extends BaseAdapter{
    
    private Context mContext;
    
    private ConfigFtp configFtp;
    
    private List<FTPGroupModel> ftpGroupList;
    
    private EditText currentLocalFileET;
    
    private EditText currentDowloadFileET;
    
    private EditText currentUploadFileET;
    
    private TaskType taskType;
    
    public FTPGroupAdapter(Context context,List<FTPGroupModel> ftpGroupList,TaskType taskType){
        this.mContext = context;
        this.ftpGroupList = ftpGroupList;
        this.taskType = taskType;
    }
    

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        if(ftpGroupList == null){
            return 0;
        }
        return ftpGroupList.size();
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param position
     * @return
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        if(ftpGroupList != null){
            return ftpGroupList.get(position);
        }
        return null;
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param position
     * @return
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param position
     * @param convertView
     * @param parent
     * @return
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @SuppressLint("InflateParams")
		@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.sys_ftp_group_item, null);
            holder.ftpServerSP = (BasicSpinner) convertView.findViewById(R.id.edit_ftpServer);
            holder.fileSourceSP = (BasicSpinner) convertView.findViewById(R.id.edit_fileSource);
            holder.fileSizeET = (EditText) convertView.findViewById(R.id.edit_fileSize);
            holder.localFileET = (EditText) convertView.findViewById(R.id.edit_localFile);
            holder.uploadfilePathET = (EditText) convertView.findViewById(R.id.edit_upload_file_path);
            holder.serverTV = (TextView) convertView.findViewById(R.id.txt_ftpServer);
            holder.serverCK = (CheckBox) convertView.findViewById(R.id.server_ck);
            holder.filesizeRetlay = (RelativeLayout) convertView.findViewById(R.id.filesize_vorg_layout);
            holder.fileViewRetlat = (RelativeLayout) convertView.findViewById(R.id.file_view_layout);
            holder.viewBtn = (Button) convertView.findViewById(R.id.btn_default);
            holder.uploadFileRetlat = (RelativeLayout) convertView.findViewById(R.id.uploadfile_layout);
            holder.downloadFileRetlat = (RelativeLayout) convertView.findViewById(R.id.downloadfile_layout);
            holder.downloadViewBtn = (Button) convertView.findViewById(R.id.btn_download);
            holder.uploadViewBtn = (Button) convertView.findViewById(R.id.btn_upload);
            holder.saveFileRetlay = (RelativeLayout) convertView.findViewById(R.id.savefile_layout);
            holder.saveFileCK = (CheckBox) convertView.findViewById(R.id.savefile_ck);
            holder.downloadFileET = (EditText) convertView.findViewById(R.id.edit_download_file_path);
            holder.filesourceRetlay = (RelativeLayout) convertView.findViewById(R.id.filesource_layout);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        final FTPGroupModel ftpGroupModel = ftpGroupList.get(position);
        ArrayAdapter<String> ftpadapter = new ArrayAdapter<String>(mContext,
                R.layout.simple_spinner_custom_layout, configFtp.getAllFtpNamesFirstEmpty(mContext));
        ftpadapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        holder.ftpServerSP.setAdapter(ftpadapter);
        if(ftpGroupModel != null){
            holder.ftpServerSP.setSelection(configFtp.getPositonFirstEmpty(ftpGroupModel.getFtpServerName() == null ? "" :ftpGroupModel.getFtpServerName()));
        }else{ 
        	if(ftpadapter.getCount()>2)
            holder.ftpServerSP.setSelection(1);
          return convertView;
        }
        //添加监听事件，若选中项为配置，则跳转到ftp配置页面
        holder.ftpServerSP.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                if(position == parent.getCount()-1){
                    Intent it = new Intent(mContext, Sys.class);
                    it.putExtra(Sys.CURRENTTAB, 4);
                    mContext.startActivity(it);  
                }else {
                    ftpGroupModel.setFtpServers(configFtp.getNameFirstEmpty(position,mContext).equals(mContext.getString(R.string.none)) ? 
                            "" : configFtp.getNameFirstEmpty(position,mContext));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                
            }
        });
        
        holder.viewBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FileExplorer.class);
                //添加传递参数
                Bundle bundle = new Bundle();
                bundle.putBoolean(FileExplorer.KEY_NORMAL, true);
                bundle.putString(FileExplorer.KEY_ACTION,
                        FileExplorer.ACTION_LOAD_NORMAL_FILE);//文件浏览类型
                bundle.putString(FileExplorer.KEY_EXTRA, FileExplorer.KEY_FILE);
                bundle.putLong(FileExplorer.KEY_FILE_SIZE, 1000 * 1000);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
                currentLocalFileET = holder.localFileET;
            }
        });
        holder.downloadViewBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                int ftpPos=holder.ftpServerSP.getSelectedItemPosition();
                if(ftpPos!=0 ){
                    Intent intent=new Intent(mContext, FtpListActivity.class);
                    FtpListUtilModel.getInstance().setServerPosition(ftpPos);
                    FtpListUtilModel.getInstance().setDlOrUl(1);  //设置为1代表是ftpDownload
                    ((Activity) mContext).startActivityForResult(intent, 55);
                    currentDowloadFileET = holder.downloadFileET;
                }else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.task_ftp_select_non), Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        holder.uploadViewBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                int ftpPos=holder.ftpServerSP.getSelectedItemPosition();
                if(ftpPos!=0 ){
                    Intent intent=new Intent(mContext, FtpListActivity.class);
                    FtpListUtilModel.getInstance().setServerPosition(ftpPos);
                    FtpListUtilModel.getInstance().setDlOrUl(2);  //设置为1代表是ftpUpload
                    ((Activity) mContext).startActivityForResult(intent, 56);
                    currentUploadFileET = holder.uploadfilePathET;
                }else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.task_ftp_select_non), Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        ArrayAdapter<String> fileSourceAdapter = new ArrayAdapter<String>(mContext,
                R.layout.simple_spinner_custom_layout, mContext.getResources().getStringArray(R.array.array_mutil_ftpserver_filesource));
        fileSourceAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        holder.fileSourceSP.setAdapter(fileSourceAdapter);
        holder.fileSourceSP.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                ftpGroupModel.setFileSource(position);
                switch (position) {
                    case 0:
                        holder.filesizeRetlay.setVisibility(View.VISIBLE);
                        holder.fileViewRetlat.setVisibility(View.GONE);
                        break;
                    case 1:
                        holder.filesizeRetlay.setVisibility(View.GONE);
                        holder.fileViewRetlat.setVisibility(View.VISIBLE);
                        break;
                    
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                
            }
            
        });
        holder.fileSourceSP.setSelection(ftpGroupModel.getFileSource());
        holder.fileSizeET.setText(String.valueOf(ftpGroupModel.getFileSize()));
        holder.localFileET.setText(ftpGroupModel.getLocalFile() == null ? "" :ftpGroupModel.getLocalFile());
        holder.uploadfilePathET.setText(ftpGroupModel.getUploadFilePath() == null ? "/" :ftpGroupModel.getUploadFilePath());
        holder.serverTV.setText(mContext.getResources().getString(R.string.monitor_normal_server) + (position + 1));
        holder.downloadFileET.setText(ftpGroupModel.getDownloadFile() == null ? "" :ftpGroupModel.getDownloadFile());
        holder.fileSizeET.addTextChangedListener(new EditTextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int count,
                    int after) {
                super.onTextChanged(s, start, count, after);
                ftpGroupModel.setFileSize(StringUtil.isNullOrEmpty(s.toString()) ? 0 : Integer.valueOf(s.toString()));
            }
            
        });
        holder.localFileET.addTextChangedListener(new EditTextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int count,
                    int after) {
                super.onTextChanged(s, start, count, after);
                ftpGroupModel.setLocalFile(s.toString());
            }
            
        });
        
        holder.downloadFileET.addTextChangedListener(new EditTextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int count,
                    int after) {
                super.onTextChanged(s, start, count, after);
                ftpGroupModel.setDownloadFile(s.toString());
            }
            
        });
        
        holder.uploadfilePathET.addTextChangedListener(new EditTextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int count,
                    int after) {
                super.onTextChanged(s, start, count, after);
                ftpGroupModel.setUploadFilePath(s.toString());
            }
            
        });
        
        holder.serverCK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ftpGroupModel.setEnable(1);
                }else {
                    ftpGroupModel.setEnable(0);
                }
            }
        });
        holder.serverCK.setChecked(ftpGroupModel.getEnable() == 1 ? true : false);
        
        holder.saveFileCK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ftpGroupModel.setSavaFile(1);
                }else {
                    ftpGroupModel.setSavaFile(0);
                }
            }
        });
        holder.saveFileCK.setChecked(ftpGroupModel.getSavaFile() == 1 ? true : false);
        
        if(taskType != null){
            switch (taskType) {
                case MultiftpDownload:
                    holder.filesizeRetlay.setVisibility(View.GONE);
                    holder.fileViewRetlat.setVisibility(View.GONE);
                    holder.uploadFileRetlat.setVisibility(View.GONE);
                    holder.filesourceRetlay.setVisibility(View.GONE);
                    holder.saveFileRetlay.setVisibility(View.VISIBLE);
                    holder.downloadFileRetlat.setVisibility(View.VISIBLE);
                    break;
                case MultiftpUpload:
                    holder.filesizeRetlay.setVisibility(View.VISIBLE);
                    holder.fileViewRetlat.setVisibility(View.VISIBLE);
                    holder.uploadFileRetlat.setVisibility(View.VISIBLE);
                    holder.filesourceRetlay.setVisibility(View.VISIBLE);
                    holder.saveFileRetlay.setVisibility(View.GONE);
                    holder.downloadFileRetlat.setVisibility(View.GONE); 
                    break;
                
                default:
                    break;
            }
        }
        return convertView;
    }
    
    public void setConfigFtp(ConfigFtp configFtp) {
        this.configFtp = configFtp;
    }
    
    
    static class ViewHolder{
        BasicSpinner ftpServerSP;
        BasicSpinner fileSourceSP;
        EditText fileSizeET;
        EditText localFileET;
        EditText uploadfilePathET;
        TextView serverTV;
        CheckBox serverCK;
        Button viewBtn;
        CheckBox saveFileCK;
        EditText downloadFileET;
        Button downloadViewBtn;
        Button uploadViewBtn;
        
        RelativeLayout filesizeRetlay;
        RelativeLayout fileViewRetlat;
        RelativeLayout uploadFileRetlat;
        RelativeLayout downloadFileRetlat;
        RelativeLayout saveFileRetlay;
        RelativeLayout filesourceRetlay;
    }
    
    public void setLocalFile(String filepath){
        if(currentLocalFileET != null){
            currentLocalFileET.setText(filepath);
        }
        
    }
    
    public void setDownloadFile(String filepath){
        if(currentDowloadFileET != null){
            currentDowloadFileET.setText(filepath);
        }
    }
    
    public void setUploadFile(String filepath){
        if(currentUploadFileET != null){
            currentUploadFileET.setText(filepath);
        }
    }
    
    public void setFtpGroupList(List<FTPGroupModel> ftpGroupList){
        this.ftpGroupList = ftpGroupList;
    }
}
