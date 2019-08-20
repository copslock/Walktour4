package com.walktour.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.walktour.gui.R;

import java.util.HashMap;
import java.util.Map;

/**
 * [多选Adapter]<BR>
 * 
 * @author StreetLight
 * @version 2015-01-23
 */
public class MutilyChoiceAdapter extends BaseAdapter {
	private Context context;
	private String[] items = null;
	private int itemViewId = R.layout.list_alarm_item;
	/** 勾选状态映射 */
	private Map<String, Boolean> checkedMap = new HashMap<String, Boolean>();

	public MutilyChoiceAdapter(Context context, String[] items) {
		this.context = context;
		this.items = items;
		if (items != null) {
			for (String item : items) {
				checkedMap.put(item, false);
			}
		}
	}

	/**
	 * 设置勾选状态
	 * 
	 * @param position
	 *          序号
	 * @param isChecked
	 *          是否勾选
	 */
	public void setChecked(int position, boolean isChecked) {
		if (items == null || position >= items.length)
			return;
		this.checkedMap.put(items[position], isChecked);
	}

	/**
	 * 获取勾选状态
	 * 
	 * @param position
	 *          序号
	 * @return
	 */
	public boolean getChecked(int position) {
		if (items == null || position >= items.length)
			return false;
		return this.checkedMap.get(items[position]);
	}

	public void setItemViewId(int viewId) {
		itemViewId = viewId;
	}

	@Override
	public int getCount() {
		if (items == null) {
			return 0;
		}
		return items.length;
	}

	@Override
	public String getItem(int position) {
		return this.items[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(itemViewId, null);
			holder = new ViewHolder();
			holder.description = (TextView) convertView.findViewById(R.id.ItemText);
			holder.check = (ImageView) convertView.findViewById(R.id.ItemIcon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.description.setText(items[position]);
		boolean isOn = this.checkedMap.get(items[position]);
		holder.check.setImageResource(isOn ? R.drawable.btn_check_on : R.drawable.btn_check_off);
		return convertView;
	}

	private class ViewHolder {
		private TextView description;
		private ImageView check;
	}

}
