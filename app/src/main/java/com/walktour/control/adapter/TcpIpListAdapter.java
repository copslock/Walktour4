package com.walktour.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.walktour.Utils.DLArrayList;
import com.walktour.control.bean.packet_dissect_info;
import com.walktour.gui.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * 抓包列表适配器
 *
 */
public class TcpIpListAdapter  extends BaseAdapter{
	private Context mContext;
	private LayoutInflater inflater;
//	private List<packet_dissect_info> dataList = new ArrayList<packet_dissect_info>();
	private DLArrayList<packet_dissect_info> dataList = new DLArrayList<packet_dissect_info>();
	private SimpleDateFormat format = null;
	private SimpleDateFormat formatLocal = null;
	
	public TcpIpListAdapter(Context context) {
		this.mContext = context;
		inflater = LayoutInflater.from(mContext);
		format = new SimpleDateFormat("HH:mm:ss");
		formatLocal = new SimpleDateFormat("HH:mm:ss");
		formatLocal.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public void cleanDataList(){
		dataList.clear();
	}
	
	public DLArrayList<packet_dissect_info> getDataList() {
		return dataList;
	}

	public void setDataList(DLArrayList<packet_dissect_info> dataList) {
		this.dataList = dataList;
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
			convertView = inflater.inflate(R.layout.wireshark_item, null);
			holder.txt_time = (TextView)convertView.findViewById(R.id.time);
			holder.txt_protocol = (TextView)convertView.findViewById(R.id.protocol);
			holder.txt_description = (TextView)convertView.findViewById(R.id.description);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder2)convertView.getTag();
		}
		packet_dissect_info item = dataList.get(position);
		holder.txt_time.setText(getSpecialProcesTime(item.tv_sec) + "");
		holder.txt_protocol.setText(item.protocol+ "");
		holder.txt_description.setText(item.description + "");
		return convertView;
	}

	/**
	 * 获得时间输出
	 * @param time
	 * @return
	 */
	private String getSpecialProcesTime(long time){
    	String timeStr = "";
    	try {
    		Calendar c = Calendar.getInstance();
    		c.set(1970, Calendar.JANUARY, 1, 0, 0, 0);
    		c.setTimeInMillis(c.getTimeInMillis() + time * 1000);
    		timeStr = format.format(c.getTime());
    		timeStr = format.format(formatLocal.parse(timeStr));
		} catch (Exception e) {
			e.printStackTrace();
		}
        return timeStr;
	}
	
	static class ViewHolder2{
		public TextView txt_time;
		public TextView txt_protocol;
		public TextView txt_description;
	}
}
