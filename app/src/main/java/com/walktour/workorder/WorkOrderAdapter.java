
package com.walktour.workorder;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.walktour.Utils.StringUtil;
import com.walktour.gui.R;
import com.walktour.workorder.model.WorkOrderList.WorkOrderInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 工单自动检索适配器
 * @author zhihui.lian
 */
public class WorkOrderAdapter extends BaseAdapter implements Filterable {
    
    private Context mContext;
    
    private List<WorkOrderInfo> workOrderInfos;
    
    private Filter filter;
    
    private List<WorkOrderInfo> filteredWorkOrders;
    
    private String mSearchText = "";
    
    /**
     * [构造简要说明]
     */
    public WorkOrderAdapter(Context context, List<WorkOrderInfo> workOrderInfos) {
        super();
        this.mContext = context;
        this.workOrderInfos = workOrderInfos;
        this.filteredWorkOrders = workOrderInfos;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return
     * @see android.widget.Adapter#getCount()
     */
    
    @Override
    public int getCount() {
        if (filteredWorkOrders != null) {
            return filteredWorkOrders.size();
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
    public WorkOrderInfo getItem(int position) {
        return filteredWorkOrders.get(position);
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
    
    public List<WorkOrderInfo> getFilteredBaseDatas(){
        return filteredWorkOrders;
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
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.listview_item_work_order_list, null);
            holder.wordOrderID = (TextView) convertView.findViewById(R.id.txt_title);
            holder.wordOrderDes = (TextView) convertView.findViewById(R.id.txt_content);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        WorkOrderInfo baseData = getItem(position);
        
        if(baseData.getWorkId() >= 0){
        	String workID =  String.valueOf(baseData.getWorkId());
            SpannableStringBuilder style = new SpannableStringBuilder(workID);
            int start = workID.indexOf(mSearchText);
            if(start != -1){
                int end = start + mSearchText.length();
                style.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.info_param_color)),
                        start,
                        end,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
            holder.wordOrderID.setText(style);
        }else {
        	holder.wordOrderID.setText("-");
        }
        
        if(!StringUtil.isNullOrEmpty(baseData.getWorkName())){
            SpannableStringBuilder style = new SpannableStringBuilder(baseData.getWorkName());
            int start = baseData.getWorkName().toLowerCase().indexOf(mSearchText);
            if(start != -1){
                int end = start + mSearchText.length();
                style.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.info_param_color)),
                        start,
                        end,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
            holder.wordOrderDes.setText(style);
        }else {
        	holder.wordOrderDes.setText("-");
        }

        
        return convertView;
    }
    
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new Filter() {
                
                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint,
                        FilterResults results) {
                    //filteredBaseDatas.clear();
                	filteredWorkOrders = (List<WorkOrderInfo>)results.values;
                    WorkOrderAdapter.this.notifyDataSetChanged();
                }
                
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    constraint = constraint.toString().toLowerCase();
                    if (constraint != null
                            && constraint.toString().length() >= 0) {
                        mSearchText = constraint.toString().trim().toLowerCase();
                        List<WorkOrderInfo> filt = new ArrayList<WorkOrderInfo>();
                        List<WorkOrderInfo> Items = new ArrayList<WorkOrderInfo>();
                        synchronized (this) {
                            Items = workOrderInfos;
                        }
                        for (int i = 0; i < Items.size(); i++) {
                        	WorkOrderInfo item = Items.get(i);
                            if (item.toString().trim().toLowerCase()
                                    .contains(constraint.toString()
                                            .toLowerCase())) {
                                filt.add(item);
                            }
                        }
                        
                        results.count = filt.size();
                        results.values = filt;
                    }else {
                        synchronized (this) {
                            results.count = workOrderInfos.size();
                            results.values = workOrderInfos;
                        }
                    }
                    return results;
                }
            };
        }
        return filter;
    }
    
    class ViewHolder {
        
        /**
         * 工单ID
         */
        TextView wordOrderID;
        
        /**
         * 工单描述
         */
        TextView wordOrderDes;
        
    }
    
}
