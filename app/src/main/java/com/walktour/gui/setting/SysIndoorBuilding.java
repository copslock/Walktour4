package com.walktour.gui.setting;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigIndoor;
import com.walktour.control.instance.DataManagerFileList;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.applet.ControlBar;
import com.walktour.gui.applet.MySimpleAdapter;
import com.walktour.model.FloorModel;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author qihang.li Activity ：建筑物页面 一、链接源： 1.由Sys_Indoor页面跳转过来，实现配置功能 二、配置功能
 *         1.显示当前建筑物的所有楼层 2.为当前建筑物添加楼层 3.配置指定楼层的图片 4.删除当前建筑物的所有楼层 三、加载功能
 *         把配置好的楼层地图加载到地图显示页面
 * */
public class SysIndoorBuilding extends BasicActivity {

	private static final String tag = "SysIndoorBuilding";
	public static final String FloorMaps = "floor_maps";
	public static final String BuildDir = "build_dir";
	public static final String FloorPosition = "floor_position";
//	private static final int DIALOG_IMAGE = 100;
	public static final String FloorNodeId = "floor_node_id";

	// ListView
	private ArrayList<HashMap<String, Object>> listItemMap;
	private MySimpleAdapter listItemAdapter;

	// 底部工具栏
	private ControlBar bar;
	private Button btnNew;
	private Button btnCheckAll;
	private Button btnCheckNon;
	private Button btnRemove;
	// 进度框
	private ProgressDialog progressDialog;
	private List<FloorModel> floorList;

	// 配置文件的变量
	private ConfigIndoor config; // 配置文件
	private String buildDir; // 当前建筑物的目录
	private String build_address; // 当前建筑物的目录
	private String buildName = "";//建筑物名称

	// 操作相关的变量
	private int ITEM_POSITION; // 记录点击事件
	private String new_floor_name; // 新楼层名
//	private ImageView imageView; // 弹出窗口的View
//	private String start_method; // 记录是新建楼层还是编辑楼层
	public boolean isLoading = false; // 是否由map页面跳转过来，是的在弹出预览框中显示"加载"按钮，否则不显示

	// 广播发送和接收相关
	public static final String IsLoadIndoorMap = "is_load_indoormap";
	private MyBroadcastReceiver mEventReceiver;
	private final static String ACTION_LOADFILE = "Walktour.SysIndoorBuilding";

	private static final int DELETE = 11;
	private static final int DELETE_END = 12;

	private final int SHOW_MUNE_DIALOG = 1001;
	
	private SysBuildingManager mSysBuildingManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_with_controlbar);
		mSysBuildingManager = SysBuildingManager.getInstance(getApplicationContext());
		try {
	        this.getBundle();// 从Sys_Indoor页面获取参数
	        this.getConfig();// 读取记录楼层的配置文件
	        this.findView();// 生成界面
	        this.genToolBar();// 底部工具栏
        } catch (Exception e) {
            this.finish();
        }

		regeditMyBroadcastReceiver();// 注册广播接收器
	}

	@Override
	// 根据配置文件刷新页面
	public void onResume() {
		super.onResume();
		this.getConfig();
		this.findView();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mEventReceiver);// 反注册广播接收器
		super.onDestroy();
	}

	/**
	 * 广播接收器:接收来自Fleet.java的广播更新界面
	 * */
	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// sdcard状态改变
			if (intent.getAction().equals(WalkMessage.ACTION_SDCARD_STATUS)) {
				getConfig();// 读取记录楼层的配置文件
				findView();// 生成界面
			}
		}
	}

	/**
	 * 注册广播接收器
	 */
	private void regeditMyBroadcastReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_LOADFILE);
		filter.addAction(WalkMessage.ACTION_SDCARD_STATUS);
		mEventReceiver = new MyBroadcastReceiver();
		this.registerReceiver(mEventReceiver, filter);
	}

	private void getBundle() {
		try {
			Bundle bundle = getIntent().getExtras();
			this.isLoading = bundle.getBoolean(SysIndoor.KEY_LOADING, false);
			LogUtil.v(tag, "isLoadingMap:" + String.valueOf(isLoading));
			
			// 获取从SysIndoor传过来的参数
			this.buildDir = bundle.getString("building");// 读出数据
			this.build_address = bundle.getString("build_address");// 读出数据
			this.setTitle(buildDir);
			this.buildName = bundle.getString("name");

		} catch (Exception e) {
			LogUtil.v(tag, "isLoadingMap:" + String.valueOf(isLoading));
		}
	}

	/**
	 * 获取楼层信息
	 */
	private void getConfig() {
		this.config = ConfigIndoor.getInstance(this);
		if(buildDir == null){
		    this.finish();
		}
		File file = new File(buildDir);
		this.floorList = config.getFloorList(this,file);
	}

	private void findView() {
		// 绑定Layout里面的ListView
		ListView list = (ListView) findViewById(R.id.ListView01);
		// 生成动态数组，每个数组单元对应一个item
		listItemMap = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < floorList.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			if (floorList.get(i).getAllMapPaths().isEmpty()) {
				// 楼层是否有地图的图片标志
				map.put("ItemImage", R.drawable.safe_img);
				// 楼层是否有地图的文字标志
				map.put("ItemText", getString(R.string.sys_indoor_unload));
			} else {
				map.put("ItemImage", R.drawable.list_item_map);
				map.put("ItemText", getString(R.string.sys_indoor_imgnumber)
						+ floorList.get(i).getAllMapPaths().size());
			}
			// 标题
			map.put("ItemTitle", floorList.get(i).getName());
			// 勾选框
			map.put("ItemCheckble", false);
			listItemMap.add(map);
		}

		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new MySimpleAdapter(this, listItemMap,// ListItem的数据源
				R.layout.listview_item_style3,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "ItemImage", "ItemTitle", "ItemText",
						"ItemCheckble" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.ItemImage, R.id.ItemTitle, R.id.ItemText,
						R.id.ItemCheckble });

		// 添加并且显示
		list.setAdapter(listItemAdapter);

		// 添加item的点击事件
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// 跳转到楼层地图配置页面
				ITEM_POSITION = arg2;
				startFloormap();
			}
		});

		/*
		 * // 添加长按点击 弹出菜单 list.setOnCreateContextMenuListener(new
		 * OnCreateContextMenuListener() { public void
		 * onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo
		 * menuInfo) { if (floorList.size() > 0) {
		 * menu.setHeaderTitle(floorList.get(ITEM_POSITION).getName());
		 * menu.setHeaderIcon(R.drawable.list_item_floor); menu.add(0, 3, 0,
		 * getString(R.string.sys_indoor_floor_del)); menu.add(0, 4, 0,
		 * getString(R.string.sys_indoor_configMap)); // menu.add(0, 5, 0, //
		 * getString(R.string.sys_indoor_floor_rename) ); } } });
		 */

		// 添加长按item事件，获取长按的item序号
		list.setOnItemLongClickListener(new OnItemLongClickListener() {
			@SuppressWarnings("deprecation")
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				ITEM_POSITION = arg2;
				showDialog(SHOW_MUNE_DIALOG);
				return true;
			}
		});

	}// end method findView

	private void startFloormap() {
		Intent intent = new Intent(SysIndoorBuilding.this, SysFloorMap.class);
		Bundle bundle = new Bundle();
		// 建筑目录
		bundle.putString(BuildDir, buildDir);
		// 点击的楼层位置
		bundle.putInt(FloorPosition, ITEM_POSITION);
		bundle.putString("build_address", build_address);
		bundle.putBoolean(IsLoadIndoorMap, isLoading);
		
		String build_node_id = mSysBuildingManager.getNodeId(buildName, "0");
		String floor_node_id = mSysBuildingManager.getNodeId(floorList.get(ITEM_POSITION).getName(), build_node_id);
		bundle.putString(FloorNodeId, floor_node_id);
		intent.putExtras(bundle);
		startActivityForResult(intent, 10);
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
		btnNew.setText(R.string.sys_indoor_newFloor);
		btnCheckAll.setText(R.string.str_checkall);
		btnCheckNon.setText(R.string.str_checknon);
		btnRemove.setText(R.string.delete);

		// set icon
		btnNew.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
				.getDrawable(R.drawable.controlbar_new), null, null);
		btnCheckAll.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.controlbar_allcheck), null,
				null);
		btnCheckNon.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.controlbar_unallcheck),
				null, null);
		btnRemove.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
				.getDrawable(R.drawable.controlbar_clear), null, null);

		btnNew.setOnClickListener(btnListener);
		btnCheckAll.setOnClickListener(btnListener);
		btnCheckNon.setOnClickListener(btnListener);
		btnRemove.setOnClickListener(btnListener);
	}

	/**
	 * 显示进度框
	 * */
	private void showProgressDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getString(R.string.removing));
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	/** 工具栏点击事件 */
	private OnClickListener btnListener = new OnClickListener() {
		@SuppressWarnings("deprecation")
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.Button01: // 新建楼层
				showDialog(R.id.menu_newBuilding);
				break;
			case R.id.Button02: // 全选
				checkAll();
				break;
			case R.id.Button03: // 反选
				checkOthers();
				break;
			case R.id.Button04:// 删除
				if (listItemAdapter.hasChecked()) {
					new BasicDialog.Builder(SysIndoorBuilding.this)
							.setIcon(android.R.drawable.ic_menu_delete)
							.setTitle(R.string.delete)
							.setMessage(R.string.main_indoor_delete)
							.setPositiveButton(R.string.delete,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											showProgressDialog();
											ArrayList<String> removeList = new ArrayList<String>();
											ArrayList<String> removeListFormDB = new ArrayList<String>();
											for (int i = 0; i < listItemAdapter
													.getCount(); i++) {
												if (listItemAdapter
														.getChecked()[i]) {
													// listItemMap.remove(i);
													removeList.add(floorList
															.get(i)
															.getDirPath());
													removeListFormDB.add(floorList.get(i).getName());
												}
											}
											new DeleteThread(removeList).start();
											deleteFloor(removeListFormDB);
										}
									})
							.setNegativeButton(R.string.str_cancle).show();
				} else {
					Toast.makeText(getApplicationContext(),
							getString(R.string.str_check_non),
							Toast.LENGTH_SHORT).show();
				}

				break;
			}
		}
	};

	/**
	 * 全选
	 * */
	private void checkAll() {
		for (int i = 0; i < listItemMap.size(); i++) {
			listItemMap.get(i).put("ItemCheckble", true);
		}
		listItemAdapter.notifyDataSetChanged();
	}

	/**
	 * 反选
	 * */
	private void checkOthers() {
		for (int i = listItemAdapter.getCount() - 1; i >= 0; i--) {
			boolean check = !listItemAdapter.getChecked()[i];
			listItemMap.get(i).put("ItemCheckble", check);
		}
		listItemAdapter.notifyDataSetChanged();
	}

	/**
	 * 重新从数据库中获取所有建筑信息
	 */
	private void updateDb() {
		DataManagerFileList.getInstance(getApplicationContext()).refreshFiles();
	}

	/**
	 * 处理删除过程
	 */
	private MyHandler handler = new MyHandler(this);

	private static class MyHandler extends Handler {
		private WeakReference<SysIndoorBuilding> reference;
		public MyHandler(SysIndoorBuilding activity){
			this.reference = new WeakReference<SysIndoorBuilding>(activity);
		}
		public void handleMessage(android.os.Message msg) {
			SysIndoorBuilding activity = this.reference.get();
			switch (msg.what) {
			// 正在删除
			case DELETE:
				String filePath = msg.obj.toString();
				activity.progressDialog.setMessage(activity.getString(R.string.removing) + filePath);
				break;
			// 删除完成
			case DELETE_END:
				activity.updateDb();
				if (activity.progressDialog != null) {
					activity.progressDialog.dismiss();
				}
				activity.getConfig();
				activity.findView();
				break;
			}
		};
	}

	/**
	 * 起一个线程执行删除
	 * 
	 * @author Administrator
	 * 
	 */
	public class DeleteThread extends Thread {
		private ArrayList<String> removeList = new ArrayList<String>();

		public DeleteThread(ArrayList<String> removeList) {
			this.removeList = removeList;
		}

		@Override
		public void run() {
			Message msg;
			for (int i = removeList.size() - 1; i >= 0; i--) {
				config.delete(removeList.get(i));
				msg = handler.obtainMessage(DELETE, removeList.get(i));
				handler.sendMessage(msg);
			}
			msg = handler.obtainMessage(DELETE_END);
			handler.sendMessage(msg);
		}
	}

	// 重写长按弹出菜单item点击事件
	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (this.floorList.size() > 0) {
			switch (item.getItemId()) {
			case 3:// 删除
				new BasicDialog.Builder(SysIndoorBuilding.this)
						.setIcon(android.R.drawable.ic_menu_delete)
						.setTitle(R.string.delete)
						.setMessage(R.string.main_indoor_delete)
						.setPositiveButton(R.string.delete,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										showProgressDialog();
										ArrayList<String> removeList = new ArrayList<String>();
										ArrayList<String> removeListFormDB = new ArrayList<String>();
										removeList.add(floorList.get(
												ITEM_POSITION).getDirPath());
										removeListFormDB.add(floorList.get(ITEM_POSITION).getName());
										new DeleteThread(removeList).start();
										deleteFloor(removeListFormDB);
									}
								})
						.setNegativeButton(R.string.str_cancle).show();
				break;
			case 4:// 配置地图
				startFloormap();
				break;

			case 5:// 重命名
					// 从XML获取弹出窗口中的内容:EditText
				LayoutInflater factory = LayoutInflater.from(this);
				final View textEntryView = factory.inflate(
						R.layout.alert_dialog_edittext, null);

				final EditText alert_EditText = (EditText) textEntryView
						.findViewById(R.id.alert_textEditText);
				new BasicDialog.Builder(SysIndoorBuilding.this)
						.setIcon(android.R.drawable.ic_menu_edit)
						.setTitle(R.string.sys_indoor_floor_rename)
						.setView(textEntryView)
						.setPositiveButton(R.string.str_ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										String input = alert_EditText.getText()
												.toString().trim();
										boolean exisited = false;
										for (int i = 0; i < floorList.size(); i++) {
											if (floorList.get(i).getName()
													.equals(input)) {
												exisited = true;
												break;
											}
										}
										if (exisited) {
											Toast.makeText(
													getApplicationContext(),
													"\""
															+ input
															+ "\""
															+ getString(R.string.sys_indoor_alert_existName),
													Toast.LENGTH_LONG).show();
										} else if (input.length() < 1) {
											Toast.makeText(
													getApplicationContext(),
													getString(R.string.sys_indoor_alert_nullName),
													Toast.LENGTH_LONG).show();
										} else {
											config.setFloorName(floorList
													.get(ITEM_POSITION), input);
											getConfig();
											findView();
										}
									}
								})
						.setNegativeButton(R.string.str_cancle).show();

				break;
			}
			return super.onContextItemSelected(item);
		}
		showDialog(R.id.menu_newBuilding);
		return super.onContextItemSelected(item);
	}

	@Override
	// 添加菜单
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater mInflater = getMenuInflater();
		mInflater.inflate(R.menu.indoor_floor, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	// 菜单点击事件
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		showDialog(item.getItemId());
		return true;
	}

	/***************************************************
	 * 继承方法: 重写activity的onCreateDialog弹出对话框 *
	 * 
	 * @see com.walktour.framework.ui.BasicActivity#onCreateDialog(int)
	 ****************************************************/
	@SuppressLint("InflateParams")
	@Override
	@SuppressWarnings("deprecation")
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		// 返回"添加楼层"对话框
		case R.id.menu_newBuilding:
			// 从XML获取弹出窗口中的内容:EditText
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(
					R.layout.alert_dialog_edittext, null);

			return new BasicDialog.Builder(SysIndoorBuilding.this)
					.setIcon(android.R.drawable.ic_menu_edit)
					.setTitle(R.string.sys_indoor_newFloor)
					.setView(textEntryView)
					.setPositiveButton(R.string.str_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									EditText alert_EditText = (EditText) textEntryView
											.findViewById(R.id.alert_textEditText);
									String input = alert_EditText.getText()
											.toString().trim();
									boolean exisited = false;
									for (int i = 0; i < floorList.size(); i++) {
										if (floorList.get(i).getName()
												.equals(input)) {
											exisited = true;
											break;
										}
									}
									if (exisited) {
										Toast.makeText(
												getApplicationContext(),
												"\""
														+ input
														+ "\""
														+ getString(R.string.sys_indoor_alert_existName),
												Toast.LENGTH_LONG).show();
									} else if (input.length() < 1) {
										Toast.makeText(
												getApplicationContext(),
												getString(R.string.sys_indoor_alert_nullName),
												Toast.LENGTH_LONG).show();
									} else {
										if (newFloor(alert_EditText.getText()
												.toString().trim())) {
											// 重新获取配置文件
											getConfig();
											findView();
											createFloor(input);
										} else {
											Toast.makeText(
													getApplicationContext(),
													getString(R.string.sys_indoor_alert_errorName),
													Toast.LENGTH_LONG).show();
										}
									}
								}
							})
					.setNegativeButton(R.string.str_cancle,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.dismiss();
								}
							}).create();
		case R.id.menu_delall:
			showProgressDialog();
			ArrayList<String> removeList = new ArrayList<String>();
			for (FloorModel floorModel : floorList) {
				removeList.add(floorModel.getDirPath());
			}
			new DeleteThread(removeList).start();
			break;
		case SHOW_MUNE_DIALOG:
			new BasicDialog.Builder(SysIndoorBuilding.this)
					.setTitle(floorList.get(ITEM_POSITION).getName())
					.setItems(
							new String[] {
									getResources().getString(
											R.string.sys_indoor_floor_del),
									getResources().getString(
											R.string.sys_indoor_configMap) },
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									// 删除楼层
									case 0:
										new BasicDialog.Builder(
												SysIndoorBuilding.this)
												.setIcon(
														android.R.drawable.ic_menu_delete)
												.setTitle(R.string.delete)
												.setMessage(
														R.string.main_indoor_delete)
												.setPositiveButton(
														R.string.delete,
														new DialogInterface.OnClickListener() {
															@Override
															public void onClick(
																	DialogInterface dialog,
																	int which) {
																showProgressDialog();
																ArrayList<String> removeList = new ArrayList<String>();
																removeList
																		.add(floorList
																				.get(ITEM_POSITION)
																				.getDirPath());
																new DeleteThread(
																		removeList)
																		.start();
															}
														})
												.setNegativeButton(R.string.str_cancle).show();
										break;
									// 添加地图
									case 1:
										startFloormap();
										break;
									default:
										break;
									}
								}
							}).create().show();

			break;
		}

		return null;
	}

	private boolean newFloor(String floor_name) {
		this.new_floor_name = floor_name;
//		start_method = "newFloor";

		// xml文件中添加楼层信息
		return config.addFloor(new File(buildDir), this.new_floor_name);

	}
	
	/**
	 * 增加楼层
	 * @param floor_name
	 */
	private void createFloor(String floor_name) {
		mSysBuildingManager.addFloor(buildName, floor_name);
	}
	

	/**
	 * 删除楼层
	 * @param floors
	 */
	private void deleteFloor(ArrayList<String> floors) {
		for (String name : floors) {
			mSysBuildingManager.deleteFloor(buildName, name);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			LogUtil.w(tag, "---requestCode:" + requestCode);
			if (isLoading) {
				Bundle bundle = data.getExtras();
				String mapPath = bundle.getString(SysIndoor.KEY_RESULT);
				backForResult(mapPath);
			}

			break;
		}
	}

	// 关闭此Activity，并返回结果
	protected void backForResult(String path) {
		LogUtil.v(tag, "backForResult:" + path);
		Intent intent_back = this.getIntent();
		Bundle bundle = new Bundle();
		bundle.putString(SysIndoor.KEY_RESULT, path);// 返回结果
		intent_back.putExtras(bundle);
		this.setResult(RESULT_OK, intent_back);
		this.finish();
	}// end method backForResult
}