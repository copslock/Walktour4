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

import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.setting.msgfilter.model.MsgFilterSetModel;

import java.util.List;

/**
 * 信令过滤设置
 * 
 * @author jianchao.wang
 * 
 */
public class MsgFilterSettingActivity extends BasicActivity {
	/** 日志标识 */
	private static final String TAG = "MsgFilterSettingActivity";
	/** 类型列表适配器 */
	private MsgNetTypeAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_filter_setting);
		findView();
	}

	/**
	 * 查找视图
	 */
	private void findView() {
		List<MsgFilterSetModel> netTypeList = MsgFilterSettingFactory.getInstance().getNetTypeList();
		mAdapter = new MsgNetTypeAdapter(this, R.layout.message_filter_setting_row, netTypeList);
		ListView messageList = (ListView) this.findViewById(R.id.message_list);
		messageList.setAdapter(mAdapter);
	}

	@Override
	protected void onDestroy() {
		LogUtil.d(TAG, "----------onDestroy-------------");
		MsgFilterSettingFactory.getInstance().writeToFile();
		// 修改完马上通知界面更新
		Intent intent = new Intent(WalkMessage.traceL3MsgChanged);
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
	private class MsgNetTypeAdapter extends ArrayAdapter<MsgFilterSetModel> implements View.OnClickListener {
		/** 行样式 */
		private int mResourceId;
		/** 上次点击的对象 */
		private MsgFilterSetModel lastClick;

		public MsgNetTypeAdapter(Context context, int textViewResourceId, List<MsgFilterSetModel> objects) {
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
			if (filter.getType() == MsgFilterSetModel.TYPE_NET_TYPE)
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
			MsgFilterSetModel filter = (MsgFilterSetModel) view.getTag();
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
		private void showSubType(MsgFilterSetModel filter) {
			Intent intent = new Intent(this.getContext(), MsgFilterDetailSettingActivity.class);
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
		private void clickText(MsgFilterSetModel filter) {
			if (filter.getType() == MsgFilterSetModel.TYPE_NET_SUB_TYPE) {
				this.lastClick = null;
				this.showSubType(filter);
				return;
			}
			// 去除之前显示的二级类型
			this.clear();
			this.addAll(MsgFilterSettingFactory.getInstance().getNetTypeList());
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
			final MsgFilterDialog eventDialog = new MsgFilterDialog(this.getContext(), filter);
			eventDialog.getBuilder().setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					MsgFilterSetModel result = eventDialog.getResult();
					filter.setShowList(result.isShowList());
					filter.setShowMap(result.isShowMap());
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
