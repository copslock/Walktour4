package com.walktour.gui.setting.eventfilter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.setting.eventfilter.model.EventFilterSetModel;

import java.util.List;

/**
 * 事件过滤设置
 * 
 * @author jianchao.wang
 * 
 */
public class EventFilterSettingActivity extends BasicActivity implements OnClickListener {
	/** 日志标识 */
	private static final String TAG = "EventFilterSettingActivity";
	/** 类型列表适配器 */
	private EventFirstTypeAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_filter_setting);
		findView();
	}

	/**
	 * 查找视图
	 */
	private void findView() {
		List<EventFilterSetModel> firstTypeList = EventFilterSettingFactory.getInstance().getFirstTypeList();
		mAdapter = new EventFirstTypeAdapter(this, R.layout.event_filter_setting_row, firstTypeList);
		ListView messageList = (ListView) this.findViewById(R.id.message_list);
		messageList.setAdapter(mAdapter);
	}

	@Override
	protected void onDestroy() {
		LogUtil.d(TAG, "----------onDestroy-------------");
		EventFilterSettingFactory.getInstance().writeToFile();
		// 修改完马上通知界面更新
		Intent intent = new Intent(WalkMessage.eventFilterChanged);
		this.getApplicationContext().sendBroadcast(intent);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		LogUtil.d(TAG, "----------onResume-------------");
		this.mAdapter.notifyDataSetChanged();
		super.onResume();
	}

	/**
	 * 事件一级类型过滤适配器
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class EventFirstTypeAdapter extends ArrayAdapter<EventFilterSetModel> implements View.OnClickListener {
		/** 行样式 */
		private int mResourceId;
		/** 上次点击的对象 */
		private EventFilterSetModel lastClick;

		public EventFirstTypeAdapter(Context context, int textViewResourceId, List<EventFilterSetModel> objects) {
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
			if (filter.getType() == EventFilterSetModel.TYPE_FIRST_TYPE)
				split.setVisibility(View.GONE);
			else
				split.setVisibility(View.VISIBLE);
			TextView text = (TextView) view.findViewById(R.id.text);
			text.setTag(filter);
			text.setText(filter.getName());
			text.setTextColor(filter.getColor());
			text.setOnClickListener(this);
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
			case R.id.text:
				this.clickText(filter);
				break;
			}
		}

		/**
		 * 显示网络子类型的下级信息
		 * 
		 * @param filter
		 *          网络子类型
		 */
		private void showSecondType(EventFilterSetModel filter) {
			Intent intent = new Intent(this.getContext(), EventFilterDetailSettingActivity.class);
			// 在Intent中传递数据
			intent.putExtra("filterCode", filter.getCode());
			// 启动Intent
			this.getContext().startActivity(intent);

		}

		/**
		 * 点击文本对象
		 * 
		 * @param filter
		 *          事件过滤对象
		 */
		private void clickText(EventFilterSetModel filter) {
			if (filter.getType() == EventFilterSetModel.TYPE_SECOND_TYPE) {
				this.lastClick = null;
				this.showSecondType(filter);
				return;
			}
			// 去除之前显示的二级类型
			this.clear();
			this.addAll(EventFilterSettingFactory.getInstance().getFirstTypeList());
			if (this.lastClick == null || !this.lastClick.equals(filter)) {
				this.lastClick = filter;
				for (int i = 0; i < this.getCount(); i++) {
					if (this.getItem(i).equals(filter)) {
						for (int j = 0; j < filter.getChildList().size(); j++) {
							this.insert(filter.getChildList().get(j), i + 1 + j);
						}
						break;
					}
				}
			} else {
				this.lastClick = null;
			}
			this.notifyDataSetChanged();
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
