package com.walktour.control.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dinglicom.dataset.EventManager;
import com.dinglicom.dataset.model.DataSetEvent;
import com.dinglicom.dataset.model.EventModel;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.setting.customevent.CustomIcomAdatper;
import com.walktour.gui.setting.eventfilter.EventFilterSettingFactory;
import com.walktour.gui.setting.eventfilter.model.EventFilterSetModel;
import com.walktour.gui.task.parsedata.TaskListDispose;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 控制事件页面
 *
 * @author qihang.li
 */
public class EventAdapter extends BaseAdapter implements Filterable {

	private Context mContext;
	private List<EventModel> eventList = new ArrayList<EventModel>();
	private int currentIndex = Integer.MAX_VALUE;
	/**
	 * 过滤前的原始当前索引
	 */
	private int originalCurrentIndex = Integer.MAX_VALUE;

	private Filter mFilter;
	private boolean mIsFilterMode;
	private int mMarkColor = 0x88888888;

	public EventAdapter(Context context) {
		this.mContext = context;
		this.eventList.addAll(EventManager.getInstance().getEventList());
		this.filterEvents();
	}

	@Override
	public int getCount() {
		if (eventList == null) {
			return 0;
		}
		return eventList.size();
	}

	@Override
	public EventModel getItem(int position) {
		if (eventList == null) {
			return null;
		}
		if (position >= 0 && position < eventList.size()) {
			return eventList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
		final EventModel model = getItem(position);
		ViewHolder holder = null;
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.listview_item_event, null);
			holder = new ViewHolder();
			holder.txtTime = (TextView) view.findViewById(R.id.ItemTime);
			holder.txtInfo = (TextView) view.findViewById(R.id.ItemInfo);
			holder.ivIcon = (ImageView) view.findViewById(R.id.iv_define_icon);
			holder.ivIcon.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
//                    showIconDialog(model);
				}
			});
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}


		if (model != null) {
			int color = mContext.getResources().getColor(R.color.csfb_delay_color);

			if (currentIndex == Integer.MAX_VALUE) {
				// 正常测试
				color = mContext.getResources().getColor(R.color.csfb_delay_color);
				EventFilterSetModel filter = EventFilterSettingFactory.getInstance().getModel(getItem(position).getRcuId());
				if (filter != null)
					color = filter.getColor();
			} else {
				// 回放
				color = getReplayColor(position);
			}

			if (model.getTime() != 0 && model.getEventStr().trim().length() > 0) {
				holder.txtTime.setText(UtilsMethod.getSimpleDateFormat1(model.getTime()));
				holder.txtInfo.setText(model.getEventStr());
				// 此处避免回放颜色与事件显示的颜色冲突
				if (color != mContext.getResources().getColor(R.color.info_param_color)) {
					if (model.isError()) {
						color = Color.RED;
					}

					if (model.getType() == EventModel.TYPE_TAG) {
						color = Color.BLACK;
					} else if (model.getType() == EventModel.TYPE_DEFINE) {
						color = Color.RED;
					}
				}
				if (!StringUtil.isEmpty(model.getIconDrawablePath())) {
//                    List<CustomEvent> customEventList = CustomEventFactory.getInstance().getCustomEventList();
//                    for (CustomEvent event : customEventList) {
//                        if (event.getName() == model.getCustomEventName()) {
					holder.ivIcon.setVisibility(View.VISIBLE);
					holder.ivIcon.setImageBitmap(BitmapFactory.decodeFile(model.getIconDrawablePath()));
//                            break;
//                        }
//                    }
				} else {
					holder.ivIcon.setVisibility(View.GONE);
				}
			} else {
				// 没时间的显示为空格
				holder.txtTime.setText("");
				holder.txtInfo.setText("");
			}

			holder.txtTime.setTextColor(color);
			holder.txtInfo.setTextColor(color);

			if (getItem(position).isNeedMark()) {
				view.setBackgroundColor(mMarkColor);
			} else {
				view.setBackgroundColor(Color.WHITE);
			}

		}
		return view;
	}

	/**
	 * 显示图标选择窗口 showIconDialog 函数功能：
	 *
	 * @param define 自定义对象
	 */
	private void showIconDialog(final EventModel define) {
		String iconDir = AppFilePathUtil.getInstance().getSDCardBaseDirectory("icons");
		GridView gridView = (GridView) LayoutInflater.from(mContext).inflate(R.layout.gridview_custom_event,
				null);

		final CustomIcomAdatper gridAdatper = new CustomIcomAdatper(mContext, new File(iconDir));
		gridView.setAdapter(gridAdatper);

		final BasicDialog dialog = new BasicDialog.Builder(mContext).setView(gridView).setTitle(define.getEventStr())
				.create();
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                define.setOldName(define.getName());
//                define.setIconFilePath(gridAdatper.getItem(position).getAbsolutePath());
//                mFactory.editCustomEvent(define);
//                dialog.dismiss();
//                notifyDataSetChanged();
				define.setIconDrawablePath(gridAdatper.getItem(position).getAbsolutePath());
				notifyDataSetChanged();
			}
		});

		dialog.show();
	}

	private class ViewHolder {
		private TextView txtTime;
		private TextView txtInfo;
		private ImageView ivIcon;
	}

	/**
	 * 设置游标到当前事件点
	 */
	public void setIndexChange(int index) {
		this.originalCurrentIndex = index;
		currentIndex = index;
	}

	@Override
	public void notifyDataSetChanged() {
		if (!isFilterMode()) {
			this.currentIndex = Integer.MAX_VALUE;
			this.filterEvents();
			if (this.currentIndex == Integer.MAX_VALUE)
				this.currentIndex = this.originalCurrentIndex;
		}
		super.notifyDataSetChanged();
	}

	/**
	 * 按照设置项过滤掉事件
	 */
	private void filterEvents() {
		this.eventList.clear();
		List<EventModel> list = new ArrayList<EventModel>(EventManager.getInstance().getEventList());
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) != null) {
				EventModel event = list.get(i);
				EventFilterSetModel filter = EventFilterSettingFactory.getInstance().getModel(event.getRcuId());
				// 如果是wifi测试,默认过滤其他所有的事件,只显示wifi的事件信息
				if (TaskListDispose.getInstance().isWlanTest() && ApplicationModel.getInstance().isTestJobIsRun()) {
					if (!this.isWifiEvent(event.getRcuId()))
						continue;
				}
				if (ApplicationModel.getInstance().isBeiJingTest()) {
					if (filter != null && filter.getChecked() != EventFilterSetModel.CHECKED_NO) {
						this.eventList.add(event);
						if (this.currentIndex == Integer.MAX_VALUE && this.originalCurrentIndex != Integer.MAX_VALUE
								&& this.originalCurrentIndex <= i) {
							this.currentIndex = this.eventList.size() - 1;
						}
					}
				} else if (filter == null || filter.getChecked() != EventFilterSetModel.CHECKED_NO) {
					this.eventList.add(event);
					if (this.currentIndex == Integer.MAX_VALUE && this.originalCurrentIndex != Integer.MAX_VALUE
							&& this.originalCurrentIndex <= i) {
						this.currentIndex = this.eventList.size() - 1;
					}
				}
			}
		}
	}

	/**
	 * 获得当前ITEM颜色
	 */
	private int getReplayColor(int position) {
		int curItemIndex = getItem(position).getPointIndex();
		boolean curLess = curItemIndex <= currentIndex;
		// 如果当前事件采样点大于回放当前采样点，当前显示普通颜色
		if (!curLess) {
			return mContext.getResources().getColor(R.color.csfb_delay_color);
		}

		// 只向下判断仅取当前往下50个点进行比对
		int nextCount = (getCount() - position > 50) ? position + 50 : getCount();
		// 到此处表示当前采样点小于回放采样点，如果下一个彩样点大于回放采样点，表示当前需要标绿色，如果下一个采样点小于等于回放，则当前采样点标普通颜色
		for (int i = position + 1; i < nextCount; i++) {
			if (getItem(i).getPointIndex() != curItemIndex) {
				if (getItem(i).getPointIndex() > currentIndex) {
					return mContext.getResources().getColor(R.color.info_param_color);
				}
				return mContext.getResources().getColor(R.color.csfb_delay_color);
			}
		}
		return mContext.getResources().getColor(R.color.info_param_color);
	}

	/**
	 * 判断是否wifi事件,以及http,ftp,ping事件
	 */
	private static int[][] sWifiEvents = new int[][]{ //
			{DataSetEvent.ET_WiFi_SearchStart, DataSetEvent.ET_WiFi_Roam}, //
			{DataSetEvent.ET_WiFi_DHCPStart, DataSetEvent.ET_WiFi_DHCPReleaseSuccess}, //
			{DataSetEvent.ET_WiFi_AuthMainPageStart, DataSetEvent.ET_WiFi_AuthMainPageFailed}, //
			{DataSetEvent.ET_WiFi_LoginStart, DataSetEvent.ET_WiFi_LogoutFailed}, //
			{DataSetEvent.ET_FTP_DL_SockConnect, DataSetEvent.ET_FTP_UP_Failure}, //
			{DataSetEvent.ET_HTTP_SockConnect, DataSetEvent.ET_HTTP_Start}, //
			{DataSetEvent.ET_HttpUpload_Start, DataSetEvent.ET_HttpUpload_Failure}, //
			{DataSetEvent.ET_HttpPageSocketConnecting, DataSetEvent.ET_HttpPageMainPageOK}, //
			{DataSetEvent.ET_Ping_Start, DataSetEvent.ET_DeviceIdentity}, //
			{DataSetEvent.ET_DNSLookup_Start, DataSetEvent.ET_DNSLookup_Failure}, //
			{DataSetEvent.ET_Stop_Logging, DataSetEvent.ET_Stop_Test},//
	};

	/***
	 * 判断是否是wifi事件,以及http,ftp,ping事件
	 *
	 * @return
	 */
	private boolean isWifiEvent(int rcuID) {
		for (int[] values : sWifiEvents) {
			if (rcuID >= values[0] && rcuID <= values[1])
				return true;
		}
		return false;
	}

	/**
	 * 设置标记颜色
	 *
	 * @param color
	 */
	public void setMarkColor(int color) {
		mMarkColor = color;
		notifyDataSetChanged();
	}

	public int getMarkColor() {
		return mMarkColor;
	}

	/**
	 * 当前是否是过滤模式
	 *
	 * @return
	 */
	public boolean isFilterMode() {
		return mIsFilterMode;
	}

	/**
	 * 设置是否过滤模式
	 *
	 * @param isFilterMode
	 */
	public void setIsFilterMode(boolean isFilterMode) {
		this.mIsFilterMode = isFilterMode;
	}

	public void clearMark() {
		for (EventModel item : eventList) {
			item.setNeedMark(false);
		}
	}


	@Override
	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new Filter() {

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					String regret = constraint.toString().toLowerCase(Locale.getDefault()).trim();
					if (StringUtil.isEmpty(regret)) {
						return null;
					}
					List<EventModel> filter = new ArrayList<>();
					filterEvents();
					List<EventModel> datas = new ArrayList<>();
					datas.addAll(eventList);
					for (EventModel item : datas) {
						String src = item.getEventStr().toLowerCase(Locale.getDefault()).trim();
						if (src.contains(regret)) {
							item.setNeedMark(true);
							filter.add(item);
						} else {
							item.setNeedMark(false);
						}
					}
					FilterResults results = new FilterResults();
					results.count = filter.size();
					results.values = filter;
					return results;
				}

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					if (results != null) {
						eventList.clear();
						eventList.addAll((List<EventModel>) results.values);
						notifyDataSetChanged();
					}
				}
			};
		}
		return mFilter;
	}
}
