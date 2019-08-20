package com.walktour.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.walktour.Utils.UtilsMethod;
import com.walktour.control.config.ConfigKpi;
import com.walktour.framework.view.HeadListView;
import com.walktour.framework.view.HeadListView.HeaderAdapter;
import com.walktour.gui.R;
import com.walktour.model.KpiSettingModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义kpi适配器
 * @author zhihui.lian
 *
 */
public class KPIAdapter extends BaseAdapter implements SectionIndexer,
		HeaderAdapter, OnScrollListener {
	private Context mContext;
	private ArrayList<KpiSettingModel> kpiModelList;
	private LayoutInflater inflater = null;
	private List<Integer> mPositions;
	private List<String> mSections;

	public KPIAdapter(Context mContext, ArrayList<KpiSettingModel> kpiModelList) {
		this.mContext = mContext;
		this.kpiModelList = kpiModelList;
		inflater = LayoutInflater.from(mContext);
		initDateHead();
	}

	private void initDateHead() {
		mSections = new ArrayList<String>();
		mPositions = new ArrayList<Integer>();
		for (int i = 0; i < kpiModelList.size(); i++) {
			if (i == 0) {
				mSections.add(String.valueOf(kpiModelList.get(i).getGroupby()));
				mPositions.add(i);
				continue;
			}
			if (i != kpiModelList.size()) {
				if (!kpiModelList.get(i).getGroupby().equals(kpiModelList.get(i - 1).getGroupby())) {
					mSections.add(kpiModelList.get(i).getGroupby());
					mPositions.add(i);
				}
			}
		}
	}

	@Override
	public int getCount() {
		return kpiModelList == null ? 0 : kpiModelList.size();
	}

	@Override
	public KpiSettingModel getItem(int position) {
		if (kpiModelList != null && kpiModelList.size() != 0) {
			return kpiModelList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.kpi_setting_item, null);
			mHolder = new ViewHolder();
			mHolder.showTextView = (TextView) view.findViewById(R.id.show_txt);
			//header
			mHolder.headLayout = (LinearLayout)view.findViewById(R.id.head_layout);
			mHolder.header = (TextView) view.findViewById(R.id.head_txt);
			mHolder.isEnble =(CheckBox)view.findViewById(R.id.enble_checkbox);
			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}
		KpiSettingModel kpiModel = getItem(position);
		mHolder.showTextView.setText(
				UtilsMethod.getStringsByFieldName(mContext, kpiModel.getKpiShowName()) +
				kpiModel.getOperator() + kpiModel.getValue() + kpiModel.getUnits()
		);
		
		mHolder.isEnble.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CheckBox buttonView = (CheckBox)v;
				ConfigKpi.getInstance().setKpiEnable(position, buttonView.isChecked() ? 1 : 0 );
			}
		});
		
		mHolder.isEnble.setChecked(kpiModel.getEnable() == 1 ? true :false);
		int section = getSectionForPosition(position);
		if (getPositionForSection(section) == position) {
			mHolder.headLayout.setVisibility(View.VISIBLE);
			mHolder.header.setText(mSections.get(section));
		} else {
			mHolder.headLayout.setVisibility(View.GONE);
		}
		return view;
	}

	class ViewHolder {
		TextView showTextView;
		//header
		CheckBox isEnble;
		TextView header;
		LinearLayout headLayout;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (view instanceof HeadListView) {
			((HeadListView) view).configureHeaderView(firstVisibleItem);
		}
	}

	@Override
	public int getHeaderState(int position) {
		// TODO Auto-generated method stub
		int realPosition = position;
		if (realPosition < 0 || position >= getCount()) {
			return HEADER_GONE;
		}
		int section = getSectionForPosition(realPosition);
		int nextSectionPosition = getPositionForSection(section + 1);
		if (nextSectionPosition != -1
				&& realPosition == nextSectionPosition - 1) {
			return HEADER_PUSHED_UP;
		}
		return HEADER_VISIBLE;
	}

	@Override
	public void configureHeader(View header, int position, int alpha) {
		int realPosition = position;
		int section = getSectionForPosition(realPosition);
		String title = (String) getSections()[section];
		((TextView) header.findViewById(R.id.head_txt)).setText(title);
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return mSections.toArray();
	}

	@Override
	public int getPositionForSection(int sectionIndex) {
		if (sectionIndex < 0 || sectionIndex >= mPositions.size()) {
			return -1;
		}
		return mPositions.get(sectionIndex);
	}

	@Override
	public int getSectionForPosition(int position) {
		if (position < 0 || position >= getCount()) {
			return -1;
		}
		int index = Arrays.binarySearch(mPositions.toArray(), position);
		return index >= 0 ? index : -index - 2;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}
}
