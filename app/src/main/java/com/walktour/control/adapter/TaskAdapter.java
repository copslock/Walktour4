package com.walktour.control.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkStruct.TaskType;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.ping.TaskPingModel;
import com.walktour.gui.task.parsedata.model.task.udp.TaskUDPModel;

import java.util.List;

@SuppressLint("InflateParams")
public class TaskAdapter extends BaseAdapter {
	private static final String TAG = "TaskAdapter";
	private List<TaskModel> taskList;
	private Context context;
	private List<Integer> taskDelList;
	private boolean isCheckModel;
	private boolean isDropMode = false;
	private ApplicationModel appModel = ApplicationModel.getInstance();
	/** 是否来自工单界面,来自工单界面隐藏复选框,只允许查看 */
	private boolean isFromWorkOrder = false;

	public TaskAdapter(List<TaskModel> taskList, List<Integer> taskDelList, boolean isCheckModel,
			boolean isFromWorkOrder, Context context) {
		this.taskList = taskList;
		this.context = context;
		this.taskDelList = taskDelList;
		this.isCheckModel = isCheckModel;
		this.isFromWorkOrder = isFromWorkOrder;
	}

	public void setDropMode(boolean dropMode) {
		this.isDropMode = dropMode;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (taskList != null) {
			return taskList.size();
		}
		return 0;
	}

	@Override
	public TaskModel getItem(int position) {
		return taskList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void remove(TaskModel item) {
		taskList.remove(item);
	}

	public void insert(TaskModel item, int position) {
		taskList.add(position, item);
	}

	public List<TaskModel> getTaskModelList() {
		return this.taskList;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		LogUtil.d(TAG,"taskList："+taskList.size());
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_checkbox, null);
			holder = new ViewHolder();
			holder.ItemTestable = (ImageButton) convertView.findViewById(R.id.ItemTestable);
			holder.ItemTitle = (TextView) convertView.findViewById(R.id.ItemTitle);
			holder.ItemCount = (TextView) convertView.findViewById(R.id.ItemCount);
			holder.ItemDescrition = (TextView) convertView.findViewById(R.id.ItemDescrition);
			holder.ItemCheckble = (ImageView) convertView.findViewById(R.id.ItemCheckble);
			holder.ItemDropable = (ImageView) convertView.findViewById(R.id.drag_handle);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (taskList.get(position).getEnable() == TaskModel.TASKSTATUS_1) {
			holder.ItemTestable
					.setImageResource(appModel.isTestJobIsRun() ? R.drawable.check_enable : R.drawable.btn_check_on);
		} else {
			holder.ItemTestable
					.setImageResource(appModel.isTestJobIsRun() ? R.drawable.btn_disenalble : R.drawable.btn_check_off);
		}
		// 滑动排序mode
		if (isDropMode) {
			holder.ItemDropable.setVisibility(View.VISIBLE);
		} else {
			holder.ItemDropable.setVisibility(View.GONE);
		}
		if (isCheckModel) {
			holder.ItemTestable.setVisibility(View.GONE);
			convertView.setPadding(20, 0, 0, 0);
		} else {
			holder.ItemTestable.setVisibility(View.VISIBLE);
			convertView.setPadding(0, 0, 0, 0);
		}

		holder.ItemTestable.setEnabled(!appModel.isTestJobIsRun());
		holder.ItemTestable.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (taskList.get(position).getEnable() == TaskModel.TASKSTATUS_1) {// 取消
					holder.ItemTestable.setImageResource(R.drawable.btn_check_off);
					taskList.get(position).setEnable(TaskModel.TASKSTATUS_2);
				} else {// 选中某个任务其所属组也应该被选中
					holder.ItemTestable.setImageResource(R.drawable.btn_check_on);
					taskList.get(position).setEnable(1);
				}
				TaskListDispose.getInstance().checkTask();
				TaskListDispose.getInstance().writeXml();
			}
		});
		holder.ItemTitle.setText(taskList.get(position).getTaskName());
		if (isCheckModel) {
			if (taskDelList.contains(position)) {
				holder.ItemCheckble.setImageResource(R.drawable.btn_check_on);
			} else {
				holder.ItemCheckble.setImageResource(R.drawable.btn_check_off);
			}
			holder.ItemCheckble.setVisibility(View.VISIBLE);
		} else {
			holder.ItemCheckble.setImageResource(R.drawable.empty);
			holder.ItemCheckble.setVisibility(View.GONE);
		}
		String repeat = context.getString(R.string.task_repeat) + ":" + getPingRepeatDec(taskList.get(position));
		holder.ItemCount.setText(repeat);
		holder.ItemDescrition.setText(TaskListDispose.getInstance().getDescrition(taskList.get(position)));
		if (isFromWorkOrder)
			holder.ItemTestable.setVisibility(View.GONE);
		return convertView;
	}

	/**
	 * 获取ping无限中文
	 */
	private String getPingRepeatDec(TaskModel taskModel) {
		try {
			if (TaskType.valueOf(taskModel.getTaskType()) == TaskType.Ping) {
				TaskPingModel pingModel = (TaskPingModel) taskModel;
				return pingModel.isInfinite() ? context.getString(R.string.task_ping_unlimited_str)
						: pingModel.getRepeat() + "";
			}else if(TaskType.valueOf(taskModel.getTaskType()) == TaskType.UDP){
				TaskUDPModel udpModel = (TaskUDPModel) taskModel;
				return udpModel.isInfinite() ? context.getString(R.string.task_udp_unlimited_str)
						: udpModel.getRepeat() + "";
			}
			return taskModel.getRepeat() + "";
		} catch (Exception e) {
			return taskModel.getRepeat() + "";
		}
	}

	private class ViewHolder {
		ImageButton ItemTestable;
		TextView ItemTitle;
		TextView ItemCount;
		TextView ItemDescrition;
		ImageView ItemCheckble;
		ImageView ItemDropable;
	}

	public void notifyDataSetChanged(boolean isCheckMode) {
		this.isCheckModel = isCheckMode;
		super.notifyDataSetChanged();
	}

}
