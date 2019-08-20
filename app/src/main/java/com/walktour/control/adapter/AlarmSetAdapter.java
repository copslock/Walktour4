package com.walktour.control.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.walktour.Utils.WalkStruct.Alarm;
import com.walktour.control.instance.AlertManager;
import com.walktour.gui.R;

import java.util.List;

/**
 * [告警选择项的Adapter]<BR>
 * 
 * @author qihang.li
 * @version [WalkTour Client V100R001C03, 2013-2-27]
 */
@SuppressLint("InflateParams")
public class AlarmSetAdapter extends BaseAdapter {
	private Context context;
	private List<Alarm> items;
	private AlertManager alarmManager;

	public AlarmSetAdapter(Context context, List<Alarm> items) {
		alarmManager = AlertManager.getInstance(context);
		this.context = context;
		this.items = items;
	}

	public List<Alarm> getAllItems() {
		return this.items;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @return
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		if (items == null) {
			return 0;
		}
		return items.size();
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param position
	 * @return
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return this.items.get(position);
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
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
	 * 
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	@Override
	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_alarm_item, null);
			holder = new ViewHolder();
			holder.description = (TextView) convertView.findViewById(R.id.ItemText);
			holder.check = (ImageView) convertView.findViewById(R.id.ItemIcon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.description.setText(items.get(position).getDescription(context));
		holder.description.setHint(items.get(position).name());
		boolean isOn = alarmManager.isAlarmOn(items.get(position));
		holder.check.setImageDrawable(isOn ? context.getResources().getDrawable(R.drawable.btn_check_on)
				: context.getResources().getDrawable(R.drawable.btn_check_off));
		return convertView;
	}

	static class ViewHolder {
		private TextView description;
		private ImageView check;
	}
}
