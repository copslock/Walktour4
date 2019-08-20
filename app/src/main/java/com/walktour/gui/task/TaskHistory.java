package com.walktour.gui.task;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.TaskGroupConfig;
import com.walktour.gui.task.parsedata.model.base.TaskConfig;
import com.walktour.gui.task.parsedata.model.base.TaskModel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/***
 * 历史任务
 * 
 * @author weirong.fan
 *
 */
public class TaskHistory extends BasicActivity {
	TaskListDispose taskHis = null;
	Vector<Integer> selectItem = null;
	private ListView list;
	private LinearLayout bt_back;
	private LinearLayout bt_add;
	private LinearLayout bt_del;
	private LinearLayout bt_all;
	/** 是否是组历史任务,为false即为测试任务历史任务 **/
	private boolean isGroup = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getIntent().getExtras();
		isGroup = bundle.getBoolean("isGroup", false);
		taskHis = TaskListDispose.getInstance();
		selectItem = new Vector<Integer>(); // 用于存放选中的ITEM序列号值
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listview_taskhistory);

		showView(false);
		bt_back = (LinearLayout) findViewById(R.id.back);
		bt_add = (LinearLayout) findViewById(R.id.addToTask);
		bt_del = (LinearLayout) findViewById(R.id.delHisTask);
		bt_all = (LinearLayout) findViewById(R.id.selAllTask);

		bt_back.setOnClickListener(btListener);
		bt_add.setOnClickListener(btListener);
		bt_del.setOnClickListener(btListener);
		bt_all.setOnClickListener(btListener);
		(initTextView(R.id.title_txt)).setText(R.string.act_task_history);
		findViewById(R.id.pointer).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TaskHistory.this.finish();
			}
		});

	}

	private OnClickListener btListener = new OnClickListener() {
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.addToTask: // 添加到可执行任务列表
				addHisToCur();
				break;
			case R.id.delHisTask: // 彻底删除任务列表
				delHis();
				break;
			case R.id.selAllTask: // 全部选中
				showView(true);
				break;
			}
		}
	};

	private void showView(boolean selected) {
		if (isGroup) {
			if (taskHis.getHistoryGroup().length == 0) {
				new BasicDialog.Builder(TaskHistory.this).setTitle(R.string.str_tip)
						.setMessage(R.string.task_alert_hisEmpty).setOnKeyListener(new OnKeyListener() {
							public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
								dialog.cancel();
								finish();
								return false;
							}
						}).setNegativeButton(R.string.str_return, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
								finish();
							}
						}).show();
			}
		} else {
			if (taskHis.getTaskNames(TaskConfig.TASKSTATUS_0).length == 0) {
				new BasicDialog.Builder(TaskHistory.this).setTitle(R.string.str_tip)
						.setMessage(R.string.task_alert_hisEmpty).setOnKeyListener(new OnKeyListener() {

							public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
								dialog.cancel();
								finish();
								return false;
							}
						}).setNegativeButton(R.string.str_return, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
								finish();
							}
						}).show();
			}
		}
		list = (ListView) findViewById(R.id.ListView01);
		if (isGroup) {
			list.setAdapter(new ArrayAdapter<String>(this, R.layout.simple_list_item_multiple_choice,
					taskHis.getHistoryGroup()));
		} else {
			list.setAdapter(new ArrayAdapter<String>(this, R.layout.simple_list_item_multiple_choice,
					taskHis.getTaskNames(TaskConfig.TASKSTATUS_0)));
		}
		list.setItemsCanFocus(false);
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// 如果当前单击项存在于选中列表中,则删除该选中,否则按照序号大小,插入到选中列表中
				if (selectItem.contains(arg2)) {
					selectItem.remove(arg2);
				} else {
					if (selectItem.size() <= 0)
						selectItem.add(arg2);
					else {
						boolean isInsert = false;
						for (int i = 0; i < selectItem.size(); i++) {
							if (arg2 < (Integer) selectItem.get(i)) {
								selectItem.add(i, arg2);
								isInsert = true;
								break;
							}
						}
						if (!isInsert)
							selectItem.add(arg2);
					}
				}
			}
		});
		// 如果是全选的话,清空选中列表,再每项单独加到选中列表中.
		if (selected) {
			selectItem = new Vector<Integer>();
			if (isGroup) {
				for (

				int i = 0; i < taskHis.getHistoryGroup().length; i++)

				{
					list.setItemChecked(i, true);
					selectItem.add(i);
				}
			} else {
				for (

                        int i = 0; i < taskHis.getTaskNames(TaskConfig.TASKSTATUS_0).length; i++)

				{
					list.setItemChecked(i, true);
					selectItem.add(i);
				}

			}
		}
	}

	/**
	 * 添加到当前可执行列表操作
	 */
	private void addHisToCur() {
		if (selectItem.size() == 0) {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.task_mulitirab_selete_delete_task), Toast.LENGTH_SHORT).show();
		} else {
			if (isGroup) {
				List<TaskGroupConfig> groupsH = new LinkedList<TaskGroupConfig>();
				List<TaskGroupConfig> groups = taskHis.getTestPlanConfig().getTestSchemas().getTestSchemaConfig()
						.getTaskGroups();
				for (TaskGroupConfig group : groups) {
					if (group.getGroupStatus() == TaskGroupConfig.GROUPSTATUS_0) {
						groupsH.add(group);
					}
				}

				for (int i = 0; i < selectItem.size(); i++) {
					TaskGroupConfig group = groupsH.get(i);
					for (TaskGroupConfig g : groups) {
						if (group.getGroupID().equals(g.getGroupID())) {
							g.setGroupStatus(TaskGroupConfig.GROUPSTATUS_1);
						}
					}
				}
			} else {
				List<TaskModel> list = taskHis.getTaskListArray();
				int maxCurTaskId = 0;
				for (int i = 0; i < list.size(); i++) {
					TaskModel model = (TaskModel) list.get(i);
					if (model.getEnable() == 0) {
						maxCurTaskId = i;
						break;
					}
				}

				for (int i = 0; i < selectItem.size(); i++) {

					TaskModel model = (TaskModel) list
							.get(Integer.parseInt(String.valueOf(selectItem.get(i))) + maxCurTaskId);
					model.setEnable(1);
					list.remove(Integer.parseInt(String.valueOf(selectItem.get(i))) + maxCurTaskId);
					list.add(maxCurTaskId + i, model);
				}
				taskHis.setTaskListArray(list);
			}
			finish();
		}
	}

	/**
	 * 删除当前选中的历史任务
	 */
	private void delHis() {
		if (selectItem.size() == 0) {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.task_mulitirab_selete_delete_task), Toast.LENGTH_SHORT).show();
		} else {
			if (isGroup) {
				new BasicDialog.Builder(TaskHistory.this).setIcon(android.R.drawable.ic_menu_delete)
						.setTitle(R.string.delete).setMessage(R.string.str_delete_makesure)
						.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								List<TaskGroupConfig> groupsH = new LinkedList<TaskGroupConfig>();
								List<TaskGroupConfig> groups = taskHis.getTestPlanConfig().getTestSchemas().getTestSchemaConfig()
										.getTaskGroups();
								for (TaskGroupConfig group : groups) {
									if (group.getGroupStatus() == TaskGroupConfig.GROUPSTATUS_0) {
										groupsH.add(group);
									}
								}

								for (int i = 0; i < selectItem.size(); i++) {
									TaskGroupConfig group = groupsH.get(i);
									Iterator<TaskGroupConfig> it=groups.iterator();
									while(it.hasNext()){
										TaskGroupConfig value=it.next();
										if(value.getGroupID().equals(group.getGroupID())){
											it.remove();
										}
									} 
								}
								taskHis.writeXml();
								selectItem.clear();
								
								list.invalidateViews();
								showView(false);
							}
						}).setNegativeButton(R.string.str_cancle).show();

			} else {
				new BasicDialog.Builder(TaskHistory.this).setIcon(android.R.drawable.ic_menu_delete)
						.setTitle(R.string.delete).setMessage(R.string.str_delete_makesure)
						.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								List<TaskModel> taskList = taskHis.getTaskListArray();
								int maxCurTaskId = 0;
								for (int i = 0; i < taskList.size(); i++) {
									TaskModel model = (TaskModel) taskList.get(i);
									if (model.getEnable() == 0) {
										maxCurTaskId = i;
										break;
									}
								}

								for (int i = selectItem.size() - 1; i >= 0; i--) {
									taskList.remove(Integer.parseInt(String.valueOf(selectItem.get(i))) + maxCurTaskId);
									selectItem.remove(i);
								}
								taskHis.setTaskListArray(taskList);
								list.invalidateViews();
								showView(false);
							}

						}).setNegativeButton(R.string.str_cancle).show();

			}
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
