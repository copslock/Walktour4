package com.walktour.gui.setting.msgfilter;

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
import com.walktour.gui.setting.msgfilter.model.MsgFilterSetModel;

import java.util.List;

/**
 * 信令详细信息过滤设置
 * 
 * @author jianchao.wang
 * 
 */
public class MsgFilterDetailSettingActivity extends BasicActivity {
	/** 网络子类型 */
	private MsgFilterSetModel subFilter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_filter_detail_setting);
		Intent intent = getIntent();
		String filterCode = intent.getStringExtra("filterCode");
		this.subFilter = MsgFilterSettingFactory.getInstance().getModel(filterCode);
		findView();
	}

	/**
	 * 查找视图
	 */
	private void findView() {
		TextView title = (TextView) this.findViewById(R.id.title);
		title.setText(this.subFilter.getParent().getName() + "->" + this.subFilter.getName());
		List<MsgFilterSetModel> msgList = this.subFilter.getChildList();
		MsgDetailAdapter adapter = new MsgDetailAdapter(this, R.layout.message_filter_setting_row, msgList);
		ListView messageList = (ListView) this.findViewById(R.id.message_list);
		messageList.setAdapter(adapter);
	}

	/**
	 * 信令明细过滤适配器
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class MsgDetailAdapter extends ArrayAdapter<MsgFilterSetModel> implements View.OnClickListener {
		/** 行样式 */
		private int mResourceId;

		public MsgDetailAdapter(Context context, int textViewResourceId, List<MsgFilterSetModel> objects) {
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
			MsgFilterSetModel filter = this.getItem(position);
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
			MsgFilterSetModel filter = (MsgFilterSetModel) view.getTag();
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
		private void clickCheckedButton(MsgFilterSetModel filter) {
			switch (filter.getChecked()) {
			case MsgFilterSetModel.CHECKED_HALF:
				filter.setChecked(MsgFilterSetModel.CHECKED_YES);
				break;
			case MsgFilterSetModel.CHECKED_YES:
				filter.setChecked(MsgFilterSetModel.CHECKED_NO);
				break;
			default:
				filter.setChecked(MsgFilterSetModel.CHECKED_YES);
				break;
			}
			MsgFilterSettingFactory.getInstance().setFilter(filter);
			this.notifyDataSetChanged();
		}

		/**
		 * 点击设置按钮
		 * 
		 * @param filter
		 *          事件过滤对象
		 */
		private void clickSettingButton(final MsgFilterSetModel filter) {
			final MsgFilterDialog messageDialog = new MsgFilterDialog(this.getContext(), filter);
			messageDialog.getBuilder().setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					MsgFilterSetModel result = messageDialog.getResult();
					filter.setShowList(result.isShowList());
					filter.setShowMap(result.isShowMap());
					filter.setColor(result.getColor());
					saveEdit(filter);
				}
			});
			messageDialog.getBuilder().setNegativeButton(android.R.string.cancel);
			messageDialog.getBuilder().show();
		}

		/**
		 * 保存设置的值，同时更新当前界面的颜色显示
		 * 
		 * @param filter
		 *          对象
		 */
		public void saveEdit(MsgFilterSetModel filter) {
			MsgFilterSettingFactory.getInstance().setFilter(filter);
			this.notifyDataSetChanged();
		}

		/**
		 * 设置勾选的表现形式
		 * 
		 * @param check
		 *          勾选框
		 */
		private void setCheckValue(ImageButton check) {
			MsgFilterSetModel filter = (MsgFilterSetModel) check.getTag();
			switch (filter.getChecked()) {
			case MsgFilterSetModel.CHECKED_YES:
				check.setImageResource(R.drawable.checked_yes);
				break;
			case MsgFilterSetModel.CHECKED_NO:
				check.setImageResource(R.drawable.checked_no);
				break;
			case MsgFilterSetModel.CHECKED_HALF:
				check.setImageResource(R.drawable.checked_half);
				break;
			}
		}

	}

}
