/*
 * 文件名: OfflineMapAdapter.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2012-10-18
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.control.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.walktour.gui.R;
import com.walktour.gui.map.OfflineMapActivity;

import java.io.File;
import java.util.List;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-10-18] 
 */
public class OfflineMapAdapter extends BaseAdapter {
    
    private List<File> fileList;
    
    private Context context;
    
    private Handler mHandler;
    
    public OfflineMapAdapter(Context context, List<File> fileList, Handler handler) {
        this.context = context;
        this.fileList = fileList;
        this.mHandler = handler;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        if (fileList != null) {
            return fileList.size();
        }
        return 0;
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
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.listview_item_style5, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.ItemImage);
            holder.fileName = (TextView) convertView.findViewById(R.id.ItemTitle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final File file = fileList.get(position);
        if (file.isDirectory()) {
            holder.icon.setImageResource(R.drawable.folder);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHandler.obtainMessage(OfflineMapActivity.FIND_DIROCTORY_FILE,file.getPath()).sendToTarget();
                }
            });
        } else if (file.isFile()) {
            holder.icon.setImageResource(R.drawable.file);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHandler.obtainMessage(OfflineMapActivity.LOADING_OFFILEMAP,file.getPath()).sendToTarget();
                }
            });
        }
        holder.fileName.setText(file.getName());
        return convertView;
    }
    
    class ViewHolder {
        ImageView icon;
        
        TextView fileName;
    }
    
}
