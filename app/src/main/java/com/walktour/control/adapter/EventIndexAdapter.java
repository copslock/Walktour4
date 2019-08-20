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

import com.dinglicom.dataset.model.EventModel;
import com.walktour.Utils.UtilsMethod;
import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@SuppressLint("InflateParams")
public class EventIndexAdapter extends BaseAdapter implements Filterable {

	private Context context;

	private Filter filter;

	private String mSearchText = "";

	private List<EventModel> eventIndexs = new ArrayList<EventModel>();

	private List<EventModel> filterEventIndexs = new ArrayList<EventModel>();

	private boolean isEventIndex;
	/** 是否单纯显示，禁止点击操作 */
	private boolean isOnlyShow = false;

	public EventIndexAdapter(Context context, List<EventModel> eventIndexs, boolean isEventIndex) {
		this.context = context;
		this.eventIndexs = eventIndexs;
		this.isEventIndex = isEventIndex;
		this.filterEventIndexs.addAll(eventIndexs);
		this.isOnlyShow = false;
	}

	public EventIndexAdapter(Context context, List<EventModel> eventIndexs, boolean isEventIndex, boolean isOnlyShow) {
		this.context = context;
		this.eventIndexs = eventIndexs;
		this.isEventIndex = isEventIndex;
		this.filterEventIndexs.addAll(eventIndexs);
		this.sortList();
		this.isOnlyShow = isOnlyShow;
	}

	/**
	 * 设置改变的值
	 */
	public void setEventList(List<EventModel> eventIndexs, boolean isEventIndex) {
		this.filterEventIndexs.clear();
		this.filterEventIndexs.addAll(eventIndexs);
		this.sortList();
		this.isEventIndex = isEventIndex;
		this.notifyDataSetChanged();
	}

	/**
	 * 给事件列表做排序处理<BR>
	 * 测试和回放时事件窗口要改为按事件对应的采样点排序，若多个事件采样点序号一样的则再按事件Index排序。
	 */
	private void sortList() {
		Collections.sort(this.filterEventIndexs, new Comparator<EventModel>() {

			@Override
			public int compare(EventModel lhs, EventModel rhs) {
				if (lhs.getPointIndex() == rhs.getPointIndex())
					return rhs.getEventIndex() - lhs.getEventIndex();
				return rhs.getPointIndex() - lhs.getPointIndex();
			}

		});
	}

	@Override
	public int getCount() {
		return filterEventIndexs.size();
	}

	@Override
	public EventModel getItem(int position) {
		return filterEventIndexs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
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
		if (getItem(position) != null || getItem(position).getEventStr().trim().length() != 0) {
			SpannableStringBuilder style = new SpannableStringBuilder(getItem(position).getEventStr());
			int start = getItem(position).getEventStr().toLowerCase(Locale.getDefault()).indexOf(mSearchText);
			if (start != -1) {
				int end = start + mSearchText.length();
				style.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.info_param_color)), start, end,
						Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			}
			holder.lawyerText.setText(style);
		} else {
			holder.lawyerText.setText(getItem(position).getEventStr());
		}
		holder.eventTime.setVisibility(isEventIndex ? View.GONE : View.VISIBLE);
		holder.eventTime.setText(isEventIndex ? "" : UtilsMethod.getSimpleDateFormat1(getItem(position).getTime()));
		if (!isOnlyShow) {
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Object value;
					if (isEventIndex) {
						value = getItem(position).getEventStr();
					} else {
						value = getItem(position).getPointIndex();
					}
					openDetailI.onClickView(value);
				}
			});
		}
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
					filterEventIndexs.addAll((List<EventModel>) results.values);
					EventIndexAdapter.this.notifyDataSetChanged();
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();
					constraint = constraint.toString().toLowerCase(Locale.getDefault());
					mSearchText = constraint.toString().trim().toLowerCase(Locale.getDefault());
					if (constraint.toString().length() > 0) {
						List<EventModel> filt = new ArrayList<EventModel>();
						List<EventModel> Items = new ArrayList<EventModel>();
						synchronized (this) {
							Items = eventIndexs;
						}
						for (int i = 0; i < Items.size(); i++) {
							EventModel item = Items.get(i);
							if (item.getEventStr().toString().trim().toLowerCase(Locale.getDefault()).contains(constraint)) {
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
