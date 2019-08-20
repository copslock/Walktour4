package com.walktour.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.walktour.Utils.UtilsMethod;
import com.walktour.gui.R;
import com.walktour.model.AlarmModel;

import java.util.List;

/**
 * [显示告警列表的Adapter]<BR>
 * @author qihang.li
 * @version [WalkTour Client V100R001C03, 2013-3-4] 
 */
public class AlarmAdapter extends BaseAdapter{
    private Context context;
    private List<AlarmModel> items;
    
    public AlarmAdapter(Context context,List<AlarmModel> items){
        this.context = context;
        this.items = items;
    }
    
    public void setAlarmList(List<AlarmModel> itemList ){
    	this.items = itemList;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if(items == null){
            return 0;
        }
        return items.size();
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
        // TODO Auto-generated method stub
        return this.items.get(position);
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
        // TODO Auto-generated method stub
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
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_style7, null);
            holder = new ViewHolder();
            holder.description = (TextView)convertView.findViewById(R.id.ItemText);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag(); 
        }
        holder.description.setText(
        		UtilsMethod.getSimpleDateFormat1( items.get(position).getTime() ) 
        		+ " "+items.get( position ).getDescription(context) 
        );
        return convertView;
    }
    
    static class ViewHolder{
        private TextView description;
    }
}
