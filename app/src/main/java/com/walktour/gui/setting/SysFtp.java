package com.walktour.gui.setting;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.WalkStruct;
import com.walktour.control.config.ConfigFtp;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.applet.ControlBar;
import com.walktour.gui.applet.MySimpleAdapter;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.ftp.TaskFtpModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SysFtp extends BasicActivity {
	private static String[] ftpNames;
	private ListView list;
	private ArrayList<HashMap<String, Object>> listItemMap;
	private TaskListDispose taskd; // 获取系统测试任务列表信息
	private MySimpleAdapter listItemAdapter;
	private int ITEM_POSITION;
	private int ftp_count;

	// 底部工具栏
	private ControlBar bar;
	private Button btnNew;
	private Button btnCheckAll;
	private Button btnCheckNon;
	private Button btnRemove;

	private TextView titleTextView;

	private BasicDialog basicDialog;

	// config
	private ConfigFtp config;

	private final int SHOW_MUNE_DIALOG = 1000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_with_controlbar);
		taskd = TaskListDispose.getInstance();
		titleTextView = initTextView(R.id.setting_title);
		titleTextView.setText(R.string.sys_tab_ftp);
		this.getConfig();
		this.genToolBar();
		this.genList();
	}

 

	@Override
	public void onResume() {
		super.onResume();
		getConfig();
		genList();
	}

	/**
	 * 编辑FTP
	 * 
	 * @param position
	 */
	private void editFtp(int position) {
		Bundle bundle = new Bundle();
		bundle.putBoolean("isEdit", true);
		bundle.putString("server", ftpNames[position]);// 传递ftp的名称
		bundle.putInt("position", position);
		this.jumpActivity(SysFtpNew.class, bundle);
	}

	/**
	 * 新建FTP
	 */
	private void newFtp() {
		this.jumpActivity(SysFtpNew.class);
	}

	private void getConfig() {
		this.config = new ConfigFtp();
		config = new ConfigFtp();
	}

	/** 生成底部工具栏 */
	private void genToolBar() {
		bar = (ControlBar) findViewById(R.id.ControlBar);
		// get button from bar
		btnNew = bar.getButton(0);
		btnCheckAll = bar.getButton(1);
		btnCheckNon = bar.getButton(2);
		btnRemove = bar.getButton(3);

		// set text
		btnNew.setText(R.string.sys_ftp_new);
		btnCheckAll.setText(R.string.str_checkall);
		btnCheckNon.setText(R.string.str_checknon);
		btnRemove.setText(R.string.delete);

		// set icon
		btnNew.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_new),
				null, null);
		btnCheckAll.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.controlbar_allcheck), null, null);
		btnCheckNon.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.controlbar_unallcheck), null, null);
		btnRemove.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_clear),
				null, null);

		btnNew.setOnClickListener(btnListener);
		btnCheckAll.setOnClickListener(btnListener);
		btnCheckNon.setOnClickListener(btnListener);
		btnRemove.setOnClickListener(btnListener);
	}

	private void genList() {

		// 绑定Layout里面的ListView
		list = (ListView) findViewById(R.id.ListView01);

		String[] title;
		String[] itemText_ip;
		String[] itemText_port;

		// 获取所有ftp服务器的名称
		title = config.getAllFtpNames();
		ftpNames = title;
		ftp_count = title.length;

		// 获取所有ftp服务器的IP地址
		itemText_ip = config.getAllFtpIps();

		// 获取所有ftp服务器的端口号
		itemText_port = config.getAllFtpPorts();

		// 生成动态数组，每个数组单元对应一个item
		listItemMap = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < title.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.list_item_ftp);// 图像资源的ID
			map.put("ItemTitle", title[i]);
			map.put("ItemText", itemText_ip[i] + ":" + itemText_port[i]);
			map.put("ItemCheckble", false);
			listItemMap.add(map);
		}

		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new MySimpleAdapter(this, listItemMap, // ListItem的数据源
				R.layout.listview_item_style3, // ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "ItemImage", "ItemTitle", "ItemText", "ItemCheckble" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.ItemImage, R.id.ItemTitle, R.id.ItemText, R.id.ItemCheckble });

		// 添加并且显示
		list.setAdapter(listItemAdapter);

		// 添加item的点击事件
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (ftp_count > 0) {
					editFtp(arg2);
				} else {
					newFtp();
				}
			}
		});

		// 添加长按item事件，获取长按的item序号
		list.setOnItemLongClickListener(new OnItemLongClickListener() {
			@SuppressWarnings("deprecation")
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ITEM_POSITION = arg2;
				showDialog(SHOW_MUNE_DIALOG);
				return true;
			}
		});

	}

	/** 工具栏点击事件 */
	private OnClickListener btnListener = new OnClickListener() {
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.Button01: // 新建任务
				newFtp();
				break;
			case R.id.Button02: // 全选
				checkAll();
				break;
			case R.id.Button03: // 反选
				checkOthers();
				break;
			case R.id.Button04:// 删除
				if (listItemAdapter.hasChecked()) {
					basicDialog = new BasicDialog.Builder(SysFtp.this).setIcon(android.R.drawable.ic_menu_delete)
							.setTitle(R.string.delete).setMessage(R.string.str_delete_makesure)
							.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							removeFtps();
						}
					}).setNegativeButton(R.string.str_cancle).show();
				} else {
					Toast.makeText(getApplicationContext(), getString(R.string.str_check_non), Toast.LENGTH_SHORT)
							.show();
				}

				break;
			}
		}
	};

	/**
	 * 全选
	 */
	private void checkAll() {
		for (int i = 0; i < listItemMap.size(); i++) {
			listItemMap.get(i).put("ItemCheckble", true);
		}
		listItemAdapter.notifyDataSetChanged();
	}

	/**
	 * 反选
	 */
	private void checkOthers() {
		for (int i = listItemAdapter.getCount() - 1; i >= 0; i--) {
			boolean check = !listItemAdapter.getChecked()[i];
			listItemMap.get(i).put("ItemCheckble", check);
		}
		listItemAdapter.notifyDataSetChanged();
	}

	/**
	 * 删除选中的任务
	 */
	private void removeFtps() {
		for (int i = listItemAdapter.getCount() - 1; i >= 0; i--) {
			if (listItemAdapter.getChecked()[i]) {
				if (isUseByFtpServer(ftpNames[i])) {
					// 如果当前的FTP名称存在于测试任务中，不让删除该FTP节点
					Toast.makeText(SysFtp.this, ftpNames[i] + ":" + getString(R.string.sys_setting_delete_isuse),
							Toast.LENGTH_LONG).show();
				} else {
					listItemMap.remove(i);
					config.removeFtp(ftpNames[i]);
				}
			}
		}
		listItemAdapter.notifyDataSetChanged();
		getConfig();
		genList();
	}

	/**
	 * 根据传进来的FTP服务器名称判断，当前服务器是否被使用
	 * 
	 * @param ftpServerName
	 * @return
	 */
	private boolean isUseByFtpServer(String ftpServerName) {
		boolean isUser = false;
		List<TaskModel> list = taskd.getTaskListArray();
		for (Object task : list) {
			if (((TaskModel) task).getTaskType().equals(WalkStruct.TaskType.FTPDownload.name())
					|| ((TaskModel) task).getTaskType().equals(WalkStruct.TaskType.FTPUpload.name())) {
				if (((TaskFtpModel) task).getFtpServerName().equals(ftpServerName)) {
					isUser = true;
					break;
				}
			}
		}
		return isUser;
	}

	/** 上下文菜单事件 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (this.ftp_count > 0) {
			switch (item.getItemId()) {
			case 0:// 删除
					// ftp.deleteFtp(ITEM_POSITION);
				config.removeFtp(ftpNames[ITEM_POSITION]);
				getConfig();
				genList();
				break;
			case 1:// 编辑
				editFtp(ITEM_POSITION);
				getConfig();
				genList();
				break;
			}
			return super.onContextItemSelected(item);
		} else {
			newFtp();
		}
		return super.onContextItemSelected(item);
	}

	/***************************************************
	 * 继承方法: 重写activity的onCreateDialog弹出对话框 *
	 * 
	 * @see com.walktour.framework.ui.BasicActivity#onCreateDialog(int)
	 ****************************************************/
	@Override
	@SuppressWarnings("deprecation")
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case R.id.menu_delall:// 返回"删除全部"对话框
			return new BasicDialog.Builder(SysFtp.this).setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.delete).setMessage(R.string.sys_ftp_alert)
					.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							config.removeFtps(ftpNames);
							getConfig();
							genList();
						}
					}).setNegativeButton(R.string.str_cancle).create();
		case SHOW_MUNE_DIALOG:
			return new BasicDialog.Builder(SysFtp.this).setIcon(R.drawable.list_item_ftp)
					.setTitle(ftpNames[ITEM_POSITION])
					.setItems(new String[] { getResources().getString(R.string.delete),
							getResources().getString(R.string.edit) }, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									switch (which) {
									case 0:
										config.removeFtp(ftpNames[ITEM_POSITION]);
										getConfig();
										genList();
										break;
									case 1:
										editFtp(ITEM_POSITION);
										getConfig();
										genList();
										break;
									default:
										break;
									}
								}
							})
					.create();
		}

		return null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (basicDialog != null && basicDialog.isShowing()) {
			basicDialog.dismiss();
		}
	}
}
