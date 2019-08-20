package com.walktour.gui.setting;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.gui.R;
import com.walktour.model.UrlModel;

import java.util.ArrayList;
import java.util.List;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author lianzh
 * @version [WalkTour Client V100R001C03, 2012-11-28] 
 */
public class SysUrlAdapter extends BaseAdapter {
    
    private List<UrlModel> taskList;
    
    private Context context;
    
    private boolean isCheckModel;
    
    private ArrayList<UrlModel> rcfUrlModel;
    
    private Handler mHandler;  //handler携带数据对象
    
    private ArrayList<UrlModel> deleteUrlModel=SysURL.deteleUrlList;
 
    private ApplicationModel appModel = ApplicationModel.getInstance();
    
    public SysUrlAdapter(List<UrlModel> taskList,Context context,ArrayList<UrlModel> rcfUrlModel,Handler mHandler) {
        this.taskList = taskList;
        this.context = context;
        this.rcfUrlModel=rcfUrlModel;
        this.mHandler=mHandler;
    }
    
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        if (taskList != null) {
            return taskList.size();
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
        return taskList.get(position);
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
     * 重写按钮可点击与否
     */
    @Override
    public boolean isEnabled(int position) {
    	// TODO Auto-generated method stub
    	return appModel.isTestJobIsRun()?false:true;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_url, null);
            holder = new ViewHolder();
            holder.ItemTestable = (ImageView) convertView.findViewById(R.id.ItemTestable);
            holder.ItemTitle = (TextView) convertView.findViewById(R.id.ItemTitle);
            holder.ItemCheckble = (ImageView) convertView.findViewById(R.id.ItemCheckble);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (rcfUrlModel.contains(taskList.get(position))) {
            holder.ItemTestable.setImageResource(R.drawable.btn_check_on);
        } else {
            holder.ItemTestable.setImageResource(R.drawable.btn_check_off);
        }
        if (isCheckModel) {
            holder.ItemTestable.setVisibility(View.GONE);
        } else {
            holder.ItemTestable.setVisibility(View.VISIBLE);
        }
        
        if (isCheckModel) {
            holder.ItemCheckble.setVisibility(View.VISIBLE);
            if (deleteUrlModel.contains(taskList.get(position))) {
                holder.ItemCheckble.setImageResource(R.drawable.btn_check_on);
            } else {
                holder.ItemCheckble.setImageResource(R.drawable.btn_check_off);
            }
        } else {
            holder.ItemCheckble.setVisibility(View.GONE);
        }
        holder.ItemTestable.setEnabled(appModel.isTestJobIsRun()?false:true);
        holder.ItemTestable.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	Message msg=new Message();
                if (rcfUrlModel.contains(taskList.get(position))) {
                    holder.ItemTestable.setImageResource(R.drawable.btn_check_off);
                    rcfUrlModel.remove(taskList.get(position));
                } else {
                    holder.ItemTestable.setImageResource(R.drawable.btn_check_on);
                    rcfUrlModel.add(taskList.get(position));
                }
              
                msg.obj=rcfUrlModel;
                mHandler.sendMessage(msg);
            }
        });
        holder.ItemTitle.setText(taskList.get(position).getName());
        return convertView;
    }
    
    
    
    
    
    private class ViewHolder {
        
        ImageView ItemTestable;
        
        TextView ItemTitle;
        
        ImageView ItemCheckble;
        
    }
    
    public void notifyDataSetChanged(boolean isCheckMode){
        this.isCheckModel = isCheckMode;
        super.notifyDataSetChanged();
    }
    
}
