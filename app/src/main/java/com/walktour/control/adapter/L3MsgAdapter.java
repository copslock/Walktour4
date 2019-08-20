package com.walktour.control.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.setting.msgfilter.MsgFilterSettingFactory;
import com.walktour.gui.setting.msgfilter.model.MsgFilterSetModel;
import com.walktour.model.TdL3Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-9-4]
 */
@SuppressLint("InflateParams")
public class L3MsgAdapter extends BaseAdapter implements Filterable {
	private Context context;

	private List<TdL3Model> items = new ArrayList<TdL3Model>();
	/**
	 * 原始数据(存放没有过滤的数据)
	 */
	private List<TdL3Model> allItems = Collections.synchronizedList(new ArrayList<TdL3Model>());

	private int currentPointIndex = Integer.MAX_VALUE;
	private Filter mFilter;
	private boolean mIsFilterMode;
	private int mMarkColor = 0x88888888;

	public L3MsgAdapter(Context context, List<TdL3Model> items) {
		this.context = context;
		this.items.addAll(this.filterList(items));
	}

	public void clearAll() {
		this.items.clear();
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
	public TdL3Model getItem(int position) {
		if (items != null && items.size() > position && position >= 0) {
			return items.get(position);
		}
		return null;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		try{
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_layer3, null);
				holder = new ViewHolder();
				holder.description = (TextView) convertView.findViewById(R.id.ItemText);
				holder.image = (ImageView) convertView.findViewById(R.id.ItemIcon);
				holder.detail = (ImageView) convertView.findViewById(R.id.showL3Detail);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			TdL3Model item = items.get(position);
			int color = 0;
			if (item == null) {
				color = 0;
			} else {
				color = items.get(position).getColor();
			}
	
			if (currentPointIndex == Integer.MAX_VALUE) {
				// 正常测试　
				color = items.get(position).getColor();
			} else {
				// 回放
				/*
				 * if( model.getPointIndex()<= currentIndex ){ color = Color.WHITE;
				 * }else{ color = Color.GRAY; }
				 */
				color = getReplayColor(position);
			}
	
			if (getItem(position).getPointIndex() < currentPointIndex) {
				holder.description.setTextColor(Color.WHITE);
			} else {
				holder.description.setTextColor(Color.GRAY);
			}


			int icon = getDrawableIcon(getItem(position).getDirection());
			holder.image.setBackgroundResource(icon);
			holder.description.setText((getItem(position) == null) ? "" : items.get(position).getL3Msg());
			holder.description.setHint((getItem(position) == null) ? "-1" : String.valueOf(items.get(position).getPointIndex()));
			if(items.get(position).getL3Msg().contains("NR->")){
				color=Color.BLUE;
			}
			holder.description.setTextColor(color);

			final View finalConvertView = convertView;
			holder.detail.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mListener!=null){
						mListener.showDetail(finalConvertView, position);
					}
				}
			});

			if (getItem(position).isNeedMark()) {
				convertView.setBackgroundColor(mMarkColor);
			} else {
				convertView.setBackgroundColor(Color.WHITE);
			}
		} catch (Exception e) {
			LogUtil.w("L3MsgAdapter", "getView", e);
		}
		return convertView;
	}

	private int getDrawableIcon(String dirction){
		if(dirction.equals(TdL3Model.TdL3Model_UP)){
			return R.drawable.layer3_uplink;
		}else if(dirction.equals(TdL3Model.TdL3Model_DOWN)){
			return R.drawable.layer3_downlink;
		}
		return R.drawable.layer3_mobile;
	}
	/**
	 * ITEM颜色
	 * */
	@SuppressWarnings("deprecation")
	private int getReplayColor(int position) {
		TdL3Model item = getItem(position);
		MsgFilterSetModel filter = MsgFilterSettingFactory.getInstance().getModel(item.getId());
		int curItemIndex = item.getPointIndex();
		boolean curLess = curItemIndex <= currentPointIndex;
		// 如果当前事件采样点大于回放当前采样点，当前显示普通颜色
		if (!curLess) {
			return filter.getColor();
		}

		// 只向下判断仅取当前往下50个点进行比对
		int nextCount = (getCount() - position > 50) ? position + 50 : getCount();
		// 到此处表示当前采样点小于回放采样点，如果下一个彩样点大于回放采样点，表示当前需要标绿色，如果下一个采样点小于等于回放，则当前采样点标普通颜色
		for (int i = position + 1; i < nextCount; i++) {
			if (getItem(i).getPointIndex() != curItemIndex) {
				if (getItem(i).getPointIndex() > currentPointIndex) {
					return context.getResources().getColor(R.color.info_param_color);
				}
				return filter.getColor();
			}
		}
		return context.getResources().getColor(R.color.info_param_color);
	}

	/**
	 * 设置当前采样点<BR>
	 * [功能详细描述]
	 * 
	 * @param index
	 *            采样点
	 */
	public void setCurrentPointIndex(int index) {
		currentPointIndex = index;
	}

	/**
	 * 过滤掉信令列表
	 * 
	 * @param msgList
	 *            信令列表
	 * @return
	 */
	private List<TdL3Model> filterList(List<TdL3Model> msgList) {
		List<TdL3Model> list = new ArrayList<TdL3Model>(msgList);
		for (int i = 0; i < list.size(); i++) {
			TdL3Model model = list.get(i);
			if(model == null)
				continue;
			if(model.getL3Msg().toLowerCase(Locale.getDefault()).indexOf("lte ml1") >=0){
				System.out.println();
			}
			MsgFilterSetModel filter = MsgFilterSettingFactory.getInstance().getModel(model.getId());
			if (filter.getChecked() == MsgFilterSetModel.CHECKED_YES && filter.isShowList()) {
				model.setColor(filter.getColor());
			} else {
				list.remove(i);
				i--;
			}
		}
		return list;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @see android.widget.BaseAdapter#notifyDataSetChanged()
	 */

	@Override
	public void notifyDataSetChanged() {
		if (!isFilterMode()) {
			this.items.clear();
			this.items.addAll(this.filterList(TraceInfoInterface.traceData.l3MsgList));
		}
		super.notifyDataSetChanged();
	}

	@Override
	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new Filter() {

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					String regret = constraint.toString().toLowerCase(Locale.getDefault()).trim();
					if (StringUtil.isEmpty(regret)){
						return null;
					}
					List<TdL3Model> filter = new ArrayList<>();
					allItems.clear();
					allItems.addAll(filterList(TraceInfoInterface.traceData.l3MsgList));
					for (TdL3Model item : allItems) {
						String src = item.getL3Msg().toLowerCase(Locale.getDefault()).trim();
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
						items.clear();
						items.addAll((List<TdL3Model>) results.values);
						notifyDataSetChanged();
					}
				}
			};
		}
		return mFilter;
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

	static class ViewHolder {
		private ImageView image;
		private ImageView detail;
		private TextView description;
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


	private OnShowDetailListener mListener;
	public void setOnShowDetailListener(OnShowDetailListener listener){
		mListener = listener;
	}

	/**
	 * 展开详情callback
	 */
	public interface OnShowDetailListener{
		void showDetail(View containt,int position);
	}

	public void clearMark(){
		for (TdL3Model item : items) {
			item.setNeedMark(false);
		}
	}
}
