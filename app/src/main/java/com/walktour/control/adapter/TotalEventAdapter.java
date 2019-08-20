package com.walktour.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.walktour.gui.R;
import com.walktour.gui.total.TotalEventCustomView;
import com.walktour.model.TotalCustomModel;

import java.util.List;

/**
 * [统计事件的Adapter]<BR>
 * @author qihang.li
 * @version [WalkTour Client V100R001C03, 2013-10-24] 
 */
public class TotalEventAdapter extends BaseAdapter{
    private Context context;
    private List<TotalCustomModel> items;
    
    /**
     * 
     * @param context
     * @param items
     */
    public TotalEventAdapter(Context context,List<TotalCustomModel> items){
        this.context = context;
        this.items = items;
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
    public TotalCustomModel getItem(int position) {
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
        TotalCustomModel itemModel = items.get(position);
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(
            		R.layout.listview_item_customevent_total, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.ItemIcon );
            holder.title = (TextView) convertView.findViewById( R.id.ItemTitle );
            holder.count = (TextView) convertView.findViewById( R.id.ItemCount);
            holder.delay = (TextView) convertView.findViewById( R.id.ItemDelay );
            holder.totalView = (TotalEventCustomView) convertView.findViewById( R.id.ItemTotal );
            holder.totalView.setName( itemModel.getName() );
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag(); 
        }
        
        holder.image.setVisibility( View.GONE );//暂时先隐藏图标
        holder.title.setText( itemModel.getName() );
        holder.count.setText( String.format("%s:%d", 
        		context.getString(R.string.str_count),itemModel.getCount() ) );
        holder.delay.setText( String.format("%s:%dms",
        		context.getString(R.string.str_delay_average),itemModel.getDelayAverage() ) );
        holder.totalView.setVisibility( itemModel.isVisible()? View.VISIBLE:View.GONE );
        
        return convertView;
    }
    
    static class ViewHolder{
    	private ImageView image;
    	private TextView title;
    	private TextView count;
    	private TextView delay;
    	private TotalEventCustomView totalView;
    }
}
