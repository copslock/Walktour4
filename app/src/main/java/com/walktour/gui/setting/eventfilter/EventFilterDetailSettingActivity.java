package com.walktour.gui.setting.eventfilter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.setting.eventfilter.model.EventFilterSetModel;

import java.util.List;

/**
 * 事件过滤详细信息设置
 * 
 * @author jianchao.wang
 * 
 */
@SuppressLint("InflateParams")
public class EventFilterDetailSettingActivity extends BasicActivity {
	/** 二级分类 */
	private EventFilterSetModel secondFilter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_filter_detail_setting);
		Intent intent = getIntent();
		String filterCode = intent.getStringExtra("filterCode");
		this.secondFilter = EventFilterSettingFactory.getInstance().getModel(filterCode);
		findView();
	}

	/**
	 * 查找视图
	 */
	private void findView() {
		TextView title = (TextView) this.findViewById(R.id.title);
		title.setText(this.secondFilter.getParent().getName() + "->" + this.secondFilter.getName());
		List<EventFilterSetModel> msgList = this.secondFilter.getChildList();
		EventDetailAdapter adapter = new EventDetailAdapter(this, R.layout.event_filter_setting_row, msgList);
		ListView messageList = (ListView) this.findViewById(R.id.message_list);
		messageList.setAdapter(adapter);
	}

	/**
	 * 事件明细过滤适配器
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class EventDetailAdapter extends ArrayAdapter<EventFilterSetModel> implements View.OnClickListener {
		/** 行样式 */
		private int mResourceId;

		public EventDetailAdapter(Context context, int textViewResourceId, List<EventFilterSetModel> objects) {
			super(context, textViewResourceId, objects);
			this.mResourceId = textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(mResourceId, parent, false);
			} else {
				view = convertView;
			}
			EventFilterSetModel filter = this.getItem(position);
			TextView split = (TextView) view.findViewById(R.id.tab_split);
			split.setVisibility(View.VISIBLE);
			TextView text = (TextView) view.findViewById(R.id.text);
			text.setTag(filter);
			text.setText(filter.getName());
			text.setTextColor(filter.getColor());
			ImageButton check = (ImageButton) view.findViewById(R.id.checked);
			check.setTag(filter);
			check.setOnClickListener(this);
			this.setCheckValue(check);
			ImageButton button = (ImageButton) view.findViewById(R.id.setting);
			button.setTag(filter);
			button.setOnClickListener(this);
			return view;
		}

		@Override
		public void onClick(View view) {
			EventFilterSetModel filter = (EventFilterSetModel) view.getTag();
			switch (view.getId()) {
			case R.id.checked:
				this.clickCheckedButton(filter);
				break;
			case R.id.setting:
				this.clickSettingButton(filter);
				break;
			}
		}

		/**
		 * 点击勾选按钮
		 * 
		 * @param filter
		 *          事件过滤对象
		 */
		private void clickCheckedButton(EventFilterSetModel filter) {
			switch (filter.getChecked()) {
			case EventFilterSetModel.CHECKED_HALF:
				filter.setChecked(EventFilterSetModel.CHECKED_YES);
				break;
			case EventFilterSetModel.CHECKED_YES:
				filter.setChecked(EventFilterSetModel.CHECKED_NO);
				break;
			default:
				filter.setChecked(EventFilterSetModel.CHECKED_YES);
				break;
			}
			EventFilterSettingFactory.getInstance().setFilter(filter);
			this.notifyDataSetChanged();
		}

		/**
		 * 点击设置按钮
		 * 
		 * @param filter
		 *          事件过滤对象
		 */
		private void clickSettingButton(final EventFilterSetModel filter) {
			final EventFilterDialog eventDialog = new EventFilterDialog(this.getContext(), filter);
			eventDialog.getBuilder().setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					EventFilterSetModel result = eventDialog.getResult();
					filter.setShowList(result.isShowList());
					filter.setShowMap(result.isShowMap());
					filter.setShowChart(result.isShowChart());
					filter.setColor(result.getColor());
					saveEdit(filter);
				}
			});
			eventDialog.getBuilder().setNegativeButton(android.R.string.cancel);
			eventDialog.getBuilder().show();
		}

		/**
		 * 保存设置的值，同时更新当前界面的颜色显示
		 * 
		 * @param filter
		 *          对象
		 */
		public void saveEdit(EventFilterSetModel filter) {
			EventFilterSettingFactory.getInstance().setFilter(filter);
			this.notifyDataSetChanged();
		}

		/**
		 * 设置勾选的表现形式
		 * 
		 * @param check
		 *          勾选框
		 */
		private void setCheckValue(ImageButton check) {
			EventFilterSetModel filter = (EventFilterSetModel) check.getTag();
			switch (filter.getChecked()) {
			case EventFilterSetModel.CHECKED_YES:
				check.setImageResource(R.drawable.checked_yes);
				break;
			case EventFilterSetModel.CHECKED_NO:
				check.setImageResource(R.drawable.checked_no);
				break;
			case EventFilterSetModel.CHECKED_HALF:
				check.setImageResource(R.drawable.checked_half);
				break;
			}
		}

	}

}
