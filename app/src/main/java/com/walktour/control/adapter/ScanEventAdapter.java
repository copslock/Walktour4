package com.walktour.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.walktour.gui.task.activity.scannertsma.model.ScanEventModel;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 扫频仪事件列表适配器
 *
 */
public class ScanEventAdapter  extends BaseAdapter{
	private Context mContext;
	private LayoutInflater inflater;
	private List<ScanEventModel> dataList = new ArrayList<ScanEventModel>();
	
	public ScanEventAdapter(Context context) {
		this.mContext = context;
		inflater = LayoutInflater.from(mContext);
		dataList.addAll(TraceInfoInterface.traceData.scanEventList);
	}
	

	public List<ScanEventModel> getDataList() {
		return dataList;
	}


	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder2 holder = null;
		if (convertView == null) {
			holder = new ViewHolder2();
			convertView = inflater.inflate(R.layout.listview_item_event, null);
			holder.txt_time = (TextView)convertView.findViewById(R.id.ItemTime);
			holder.txt_info = (TextView)convertView.findViewById(R.id.ItemInfo);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder2)convertView.getTag();
		}
		holder.txt_time.setText(dataList.get(position).getEventTime());
		holder.txt_info.setText(dataList.get(position).getEventInfo());
		return convertView;
	}

	
	static class ViewHolder2{
		public TextView txt_time;
		public TextView txt_info;
	}
}
