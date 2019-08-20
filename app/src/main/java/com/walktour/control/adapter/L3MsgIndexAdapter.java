package com.walktour.control.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.walktour.Utils.UtilsMethod;
import com.walktour.gui.R;
import com.walktour.model.TdL3Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 索引适配器
 * 
 * @author zhihui.lian
 */
public class L3MsgIndexAdapter extends BaseAdapter implements Filterable {

	private Context context;

	private Filter filter;

	private String mSearchText = "";

	private List<TdL3Model> eventIndexs = new ArrayList<TdL3Model>();

	private ArrayList<TdL3Model> filterEventIndexs = new ArrayList<TdL3Model>();

	private boolean isEventIndex;

	public L3MsgIndexAdapter(Context context, List<TdL3Model> eventIndexs, boolean isEventIndex) {
		this.context = context;
		this.eventIndexs = eventIndexs;
		this.isEventIndex = isEventIndex;
		this.filterEventIndexs.addAll(eventIndexs);
	}

	/**
	 * 设置改变的值
	 */
	public void setEventList(List<TdL3Model> eventIndexs, boolean isEventIndex) {
		this.filterEventIndexs.clear();
		this.filterEventIndexs.addAll(eventIndexs);
		this.isEventIndex = isEventIndex;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return filterEventIndexs.size();
	}

	@Override
	public TdL3Model getItem(int position) {
		return filterEventIndexs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	@SuppressWarnings("deprecation")
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_event_index, null);
			holder.lawyerText = (TextView) convertView.findViewById(R.id.lawyer_text);
			holder.eventTime = (TextView) convertView.findViewById(R.id.lawyer_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (getItem(position) != null || getItem(position).getL3Msg().trim().length() != 0) {
			SpannableStringBuilder style = new SpannableStringBuilder(getItem(position).getL3Msg());
			int start = getItem(position).getL3Msg().toLowerCase(Locale.getDefault()).indexOf(mSearchText);
			if (start != -1) {
				int end = start + mSearchText.length();
				style.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.info_param_color)), start, end,
						Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			}
			holder.lawyerText.setText(style);
		} else {
			holder.lawyerText.setText(getItem(position).getL3Msg());
		}
		holder.eventTime.setVisibility(isEventIndex ? View.GONE : View.VISIBLE);
		holder.eventTime.setText(isEventIndex ? "" : UtilsMethod.getSimpleDateFormat1(getItem(position).getTime()));
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Object value;
				if (isEventIndex) {
					value = getItem(position).getL3Msg();
				} else {
					value = getItem(position).getPointIndex();
				}
				openDetailI.onClickView(value);
			}
		});
		return convertView;
	}

	public OpenDetailI openDetailI;

	public void setOnClickListener(OpenDetailI openDetailI) {
		this.openDetailI = openDetailI;
	}

	public interface OpenDetailI {
		void onClickView(Object v);
	}

	static class ViewHolder {
		TextView lawyerText;
		TextView eventTime;
	}

	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new Filter() {

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					filterEventIndexs.clear();
					filterEventIndexs.addAll((ArrayList<TdL3Model>) results.values);
					L3MsgIndexAdapter.this.notifyDataSetChanged();
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();
					constraint = constraint.toString().toLowerCase(Locale.getDefault());
					mSearchText = constraint.toString().trim().toLowerCase(Locale.getDefault());
					if (constraint.toString().length() > 0) {
						List<TdL3Model> filt = new ArrayList<TdL3Model>();
						List<TdL3Model> Items = new ArrayList<TdL3Model>();
						synchronized (this) {
							Items = eventIndexs;
						}
						for (int i = 0; i < Items.size(); i++) {
							TdL3Model item = Items.get(i);
							if (item.getL3Msg().toString().trim().toLowerCase(Locale.getDefault())
									.contains(constraint.toString().toLowerCase(Locale.getDefault()))) {
								filt.add(item);
							}
						}

						results.count = filt.size();
						results.values = filt;
					} else {
						synchronized (this) {
							results.count = eventIndexs.size();
							results.values = eventIndexs;
						}
					}
					return results;
				}
			};
		}
		return filter;
	}
}
