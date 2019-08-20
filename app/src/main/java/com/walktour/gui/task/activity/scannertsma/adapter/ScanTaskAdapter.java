package com.walktour.gui.task.activity.scannertsma.adapter;

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

import com.walktour.gui.task.activity.scannertsma.ScanTask5GOperateFactory;
import com.walktour.gui.task.activity.scannertsma.model.ScanTaskModel;
import com.walktour.framework.view.HeadListView;
import com.walktour.framework.view.HeadListView.HeaderAdapter;
import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 任务列表适配器
 * @author zhihui.lian
 */
public class ScanTaskAdapter extends BaseAdapter implements SectionIndexer,HeaderAdapter, OnScrollListener {
	private Context mContext;
	private ArrayList<ScanTaskModel> taskModel;
	private LayoutInflater inflater = null;
	private List<Integer> mPositions;
	private List<String> mSections;
	/**
	 * item点击监听器
	 */
	private OnItemClickListener mOnItemClickListener;

	/**
	 * 函数构造
	 * @param mContext
	 * @param taskMolelList
	 */
	public ScanTaskAdapter(Context mContext, ArrayList<ScanTaskModel> taskMolelList) {
		this.mContext = mContext;
		this.taskModel = taskMolelList;
		inflater = LayoutInflater.from(mContext);
		initTitleHead();
	}
	

	/**
	 * 初始化标题头
	 */
	private void initTitleHead() {
		mSections = new ArrayList<String>();
		mPositions = new ArrayList<Integer>();
		for (int i = 0; i < taskModel.size(); i++) {
			if (i == 0) {
				mSections.add(String.valueOf(taskModel.get(i).getGroupName()));
				mPositions.add(i);
				continue;
			}
			if (i != taskModel.size()) {
				if (!taskModel.get(i).getGroupName().equals(taskModel.get(i - 1).getGroupName())) {
					mSections.add(taskModel.get(i).getGroupName());
					mPositions.add(i);
				}
			}
		}
	}

	@Override
	public int getCount() {
		return taskModel == null ? 0 : taskModel.size();
	}

	@Override
	public ScanTaskModel getItem(int position) {
		if (taskModel != null && taskModel.size() != 0) {
			return taskModel.get(position);
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
			view = inflater.inflate(R.layout.scan_setting_item, null);
			mHolder = new ViewHolder();
			mHolder.showTextView = (TextView) view.findViewById(R.id.show_txt);
			//header
			mHolder.headLayout = (LinearLayout)view.findViewById(R.id.head_layout);
			mHolder.header = (TextView) view.findViewById(R.id.head_txt);
			mHolder.isEnble =(CheckBox)view.findViewById(R.id.enble_checkbox);
			mHolder.itemLayout = (LinearLayout)view.findViewById(R.id.item_llyt);
			mHolder.scanListDivider = (View)view.findViewById(R.id.scan_listDivider);
			
			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}
		ScanTaskModel taskModel = getItem(position);
		mHolder.showTextView.setText(taskModel.getTaskName());
		
		mHolder.isEnble.setOnClickListener(new OnClickListener() {
			

			@Override
			public void onClick(View v) {
				CheckBox buttonView = (CheckBox)v;
				ScanTaskModel taskModel = getItem(position);
				taskModel.setEnable(buttonView.isChecked() ? 1: 0);
				ScanTask5GOperateFactory.getInstance().setEnable(taskModel,position);
			}
		});
		
		mHolder.isEnble.setChecked(taskModel.getEnable() == 1 ? true :false);
		int section = getSectionForPosition(position);
		if (getPositionForSection(section) == position) {
			mHolder.headLayout.setVisibility(View.VISIBLE);
			mHolder.scanListDivider.setVisibility(View.VISIBLE);
			mHolder.header.setText(mSections.get(section));
		} else {
			mHolder.headLayout.setVisibility(View.GONE);
			mHolder.scanListDivider.setVisibility(View.GONE);
		}
		mHolder.itemLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mOnItemClickListener.onItemClick(v, position);
			}
		});
		return view;
	}
	
	
	public interface OnItemClickListener{
		void onItemClick(View view, int position);
	}
	
	public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

	class ViewHolder {
		TextView showTextView;
		//header
		CheckBox isEnble;
		TextView header;
		LinearLayout headLayout;
		//item layout
		LinearLayout itemLayout;
		
		View scanListDivider;
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

	}
}
