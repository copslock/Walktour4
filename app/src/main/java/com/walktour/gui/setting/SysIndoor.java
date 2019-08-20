package com.walktour.gui.setting;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigIndoor;
import com.walktour.control.instance.DataManagerFileList;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.applet.ControlBar;
import com.walktour.gui.applet.FileExplorer;
import com.walktour.gui.applet.MySimpleAdapter;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;
import com.walktour.gui.weifuwu.sharepush.ShareNextActivity;
import com.walktour.model.BuildingModel;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * @author qihang.li
 * @Activity ：建筑物页面 一、链接源： 1.作为Activity Sys的Tab Content，实现配置功能
 *           2.由map页面跳转而来,实现配置功能和加载功能。 二、配置功能 1.显示所有建筑物 2.添加建筑物 3.配置指定楼层的图片
 *           4.删除所有建筑物 三、加载功能 跳转到Sys_Indoor_Building页面时传递参数isLoading,
 */
public class SysIndoor extends BasicActivity {
	private final String tag = "SysIndoor";
	// 参数的KEY，记录当前页面是否由map Activity启动
	public static final String KEY_LOADING = "loading";
	// public static final String KEY_SPARE = "spare";
	public static final String KEY_RESULT = "result";
	private int ITEM_POSITION;// 记录用户点击的item
	private boolean isLoading = false;// 记录当前页面是否加载地图
	// ListView
	private ArrayList<HashMap<String, Object>> listItemMap;
	private MySimpleAdapter listItemAdapter;
	private List<BuildingModel> buildingList;
	// 底部工具栏
	private ControlBar bar1;
	private ControlBar bar2;
	private Button btnNew;
	private Button btnImport;
	private Button btnShare;
	private Button btnCancel;
	private Button btnCheckAll;
	private Button btnCheckNon;
	private Button btnRemove1;
	private Button btnRemove2;
	private BasicDialog basicDialog;
	private ApplicationModel appModel = ApplicationModel.getInstance();
	private static final int DELETE = 11;
	private static final int DELETE_END = 12;
	// private static final String KEY_EXTRA_NAME = "filepath";
	// private static final String ACTION_IMPORT_ZIP = "Walktour.SysIndoor.zip";
	private final int MENU_SHOW_DIALOG = 1001;
	private SysBuildingManager mSysBuildingManager;
	private Context mContext;
	private boolean isDelete = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_with_controlbar);
		regeditMyBroadcastReceiver();
		mContext = this;
		mSysBuildingManager = SysBuildingManager.getInstance(mContext);
		this.getBundle();
		this.findView();
		this.genToolBar();
		LogUtil.i(tag, "--->onCreate");
	}// end method onCreate
	@Override
	public void onStart() {
		super.onStart();
		ApplicationModel appModel = ApplicationModel.getInstance();
		if (appModel.isTestJobIsRun() && appModel.isIndoorTest()) {
			showDialog();
		}
		LogUtil.i(tag, "--->onStart");
	}
	/**
	 * 如果正在进行室内测试，则建筑物不可操作
	 */
	private void showDialog() {
		basicDialog = new BasicDialog.Builder(SysIndoor.this).setMessage(R.string.main_indoor_cannotop)
				.setOnKeyListener(new OnKeyListener() {
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
						dialog.dismiss();
						finish();
						return false;
					}
				}).setNeutralButton(R.string.str_return, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				}).show();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mEventReceiver);
		if (basicDialog != null && basicDialog.isShowing()) {
			basicDialog.dismiss();
		}
		LogUtil.i(tag, "--->onDestroy");
	}
	@Override // 获取从SysIndoorBuilding返回的值
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			if (isLoading) {// 如果当前是加载地图页面
				Bundle b = data.getExtras();
				String map_file_path = b.getString(KEY_RESULT); // 地图文件路径
				LogUtil.v(this.getClass().toString(), "received the file's path:" + map_file_path);
				LogUtil.v(this.getClass().toString(), "go back to Activity: com.walktour.map/map");
				backForResult(map_file_path);// 返回map页面
			}
		}// end switch
	}
	private void getBundle() {
		try {
			Bundle bundle = getIntent().getExtras();
			this.isLoading = bundle.getBoolean(KEY_LOADING, false);
			LogUtil.v(this.getClass().toString(), "isLoadingMap:" + String.valueOf(isLoading));
		} catch (Exception e) {
			LogUtil.v(this.getClass().toString(), "isLoadingMap:" + String.valueOf(isLoading));
		}
	}// end method getBundle

	private void findView() {
		// 绑定Layout里面的ListView
		ListView list = (ListView) findViewById(R.id.ListView01);
		// 获取所有ftp服务器的名称
		ConfigIndoor config = ConfigIndoor.getInstance(this);
		boolean isAHworkorder = appModel.isAnHuiTest();
		buildingList = config.getBuildings(this,isAHworkorder);
		// 生成动态数组，每个数组单元对应一个item
		listItemMap = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < buildingList.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			StringBuilder sb = new StringBuilder();
			if (buildingList.get(i).getBuildAddress().indexOf("_") > -1) {
				String[] strs = buildingList.get(i).getBuildAddress().split("_");
				for (int j = 0; j < strs.length; j++) {
					sb.append(strs[j]);
				}
			}
			LogUtil.w(tag, "----address= " + sb);
			map.put("ItemImage", R.drawable.list_item_indoor);// 图像资源的ID
			map.put("ItemTitle", buildingList.get(i).getName());
			map.put("ItemAddress", sb.toString());
			map.put("ItemText", getString(R.string.sys_indoor_floors) + buildingList.get(i).getCounts());
			map.put("ItemCheckble", false);
			listItemMap.add(map);
		}
		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new MySimpleAdapter(this, listItemMap, // ListItem的数据源
				R.layout.listview_item_style12, // ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "ItemImage", "ItemTitle", "ItemText", "ItemCheckble", "ItemAddress" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.ItemImage, R.id.ItemTitle, R.id.ItemText, R.id.ItemCheckble, R.id.ItemAddress });
		// 添加并且显示
		list.setAdapter(listItemAdapter);
		// 添加item的点击事件
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ITEM_POSITION = arg2;
				editBuilding(buildingList.get(arg2).getDirPath());
			}
		});
		/*
		 * //添加长按点击 弹出菜单 list.setOnCreateContextMenuListener(new
		 * OnCreateContextMenuListener() { public void
		 * onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo
		 * menuInfo) { menu.setHeaderTitle(
		 * buildingList.get(ITEM_POSITION).getName() );
		 * menu.setHeaderIcon(R.drawable.list_item_indoor); menu.add(0, 0, 0,
		 * getString(R.string.sys_indoor_building_del) ); menu.add(0, 1, 0,
		 * getString(R.string.sys_indoor_building_edit) ); menu.add(0, 3, 0,
		 * getString(R.string.browser) ); } });
		 */
		// 添加长按item事件，获取长按的item序号
		list.setOnItemLongClickListener(new OnItemLongClickListener() {
			@SuppressWarnings("deprecation")
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ITEM_POSITION = arg2;
				showDialog(MENU_SHOW_DIALOG);
				return true;
			}
		});
	}// end method findView
	private void initButtonTxt() {
		if (isDelete) {
			bar1.setVisibility(View.GONE);
			bar2.setVisibility(View.VISIBLE);
			btnCancel.setText(R.string.str_cancle);
			btnCheckAll.setText(R.string.str_checkall);
			btnCheckNon.setText(R.string.str_checknon);
			btnRemove2.setText(R.string.delete);
			
			btnCancel.setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.controlbar_allcheck), null, null);
			btnCheckAll.setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.controlbar_allcheck), null, null);
			btnCheckNon.setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.controlbar_unallcheck), null, null);
			
			btnRemove2.setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.controlbar_clear), null, null);
			
		} else {
			bar2.setVisibility(View.GONE);
			bar1.setVisibility(View.VISIBLE);
			if (appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)) {
				btnNew.setText(R.string.sys_indoor_downloadBuilding);
			} else {
				btnNew.setText(R.string.sys_indoor_newBuilding);
			}
			btnImport.setText(R.string.sys_indoor_importbuild);
			btnShare.setText(R.string.share_project_share);
			btnRemove1.setText(R.string.delete);
			
			btnNew.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_new),
					null, null);
			btnImport.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_import),
					null, null);
			btnShare.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_share),
					null, null);
			btnRemove1.setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.controlbar_clear), null, null);
		}
	}
	/**
	 * 生成底部工具栏 由于4.0无法解压缩包，故屏蔽Import功能
	 **/
	private void genToolBar() {
		bar1 = (ControlBar) findViewById(R.id.ControlBar);
		bar2 = (ControlBar) findViewById(R.id.ControlBarDel);
		// get button from bar
		btnNew = bar1.getButton(0);
		btnImport = bar1.getButton(1);
		btnShare = bar1.getButton(2);
		btnRemove1 = bar1.getButton(3);
		btnCancel = bar2.getButton(0);
		btnCheckAll = bar2.getButton(1);
		btnCheckNon = bar2.getButton(2);
		btnRemove2 = bar2.getButton(3);
		initButtonTxt();
		// set icon
	
		
		btnNew.setOnClickListener(btnListener);
		btnImport.setOnClickListener(btnListener);
		btnShare.setOnClickListener(btnListener);
		btnRemove1.setOnClickListener(btnListener);
		btnCancel.setOnClickListener(btnListener);
		btnCheckAll.setOnClickListener(btnListener);
		btnCheckNon.setOnClickListener(btnListener);
		btnRemove2.setOnClickListener(btnListener);
	}
	/**
	 * 注册广播接收器
	 */
	private void regeditMyBroadcastReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(WalkMessage.ACTION_SDCARD_STATUS);
		filter.addAction(WalkMessage.ACTION_IMPORT_ZIP);
		this.registerReceiver(mEventReceiver, filter);
	}
	/**
	 * 重新从数据库中获取所有建筑信息
	 */
	private void updateDb() {
		DataManagerFileList.getInstance(getApplicationContext()).refreshFiles();
	}
	/**
	 * 广播接收器:接收来自Fleet.java的广播更新界面
	 */
	private BroadcastReceiver mEventReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 导入建筑的zip压缩包
			if (intent.getAction().equals(WalkMessage.ACTION_IMPORT_ZIP)) {
				findView();// 生成界面
				updateDb();
			}
			if (intent.getAction().equals(WalkMessage.ACTION_SDCARD_STATUS)) {
				findView();// 生成界面
			}
		}
	};
	/** 工具栏点击事件 */
	private OnClickListener btnListener = new OnClickListener() {
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.Button01: // 新建建筑物
				if (isDelete) {
					isDelete = false;
					bar1.setVisibility(View.VISIBLE);
					bar2.setVisibility(View.GONE);
					initButtonTxt();
				} else {
					if (appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)) {
						Toast.makeText(SysIndoor.this, R.string.sys_indoor_developing, Toast.LENGTH_SHORT).show();
					} else {
						newBuilding();
					}
				}
				break;
			case R.id.Button02: // 导入建筑物
				if (isDelete) {
					checkAll();
				} else {
					importBuild();
				}
				break;
			case R.id.Button03: // 分享
				if (isDelete) {
					checkOthers();
				} else {
					Bundle bundle = new Bundle();
					bundle.putInt(ShareCommons.SHARE_FROM_KEY, ShareCommons.SHARE_FROM_CQT);
					jumpActivity(ShareNextActivity.class, bundle);
				}
				break;
			case R.id.Button04:// 删除
				if (isDelete) {
					if (listItemAdapter.hasChecked()) {
						basicDialog = new BasicDialog.Builder(SysIndoor.this).setIcon(android.R.drawable.ic_menu_delete)
								.setTitle(R.string.delete).setMessage(R.string.main_indoor_delete)
								.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								showProgressDialog();
								ArrayList<String> removeList = new ArrayList<String>();
								ArrayList<String> removeListFromDB = new ArrayList<String>();
								for (int i = listItemAdapter.getCount() - 1; i >= 0; i--) {
									if (listItemAdapter.getChecked()[i]) {
										removeList.add(buildingList.get(i).getDirPath());
										removeListFromDB.add(buildingList.get(i).getName());
									}
								}
								new DeleteThread(removeList).start();
								// 删除数据库中的记录
								deleteBuild(removeListFromDB);
							}
						}).setNegativeButton(R.string.str_cancle).show();
					} else {
						Toast.makeText(getApplicationContext(), getString(R.string.str_check_non), Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					isDelete = true;
					bar1.setVisibility(View.GONE);
					bar2.setVisibility(View.VISIBLE);
					initButtonTxt();
				}
				break;
			}
		}
	};
	/**
	 * 导入建筑物的zip文件
	 */
	private void importBuild() {
		Intent it = new Intent(this, FileExplorer.class);
		Bundle b = new Bundle();
		b.putStringArray(FileExplorer.KEY_FILE_FILTER, getResources().getStringArray(R.array.ziptype_build));
		b.putBoolean(FileExplorer.KEY_IMPORT_BUILD, true);
		it.putExtras(b);
		startActivity(it);
	}
	/**
	 * 全选或者全不选
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
	private ProgressDialog progressDialog;
	private void showProgressDialog() {
		progressDialog = new ProgressDialog(SysIndoor.this);
		progressDialog.setMessage(getString(R.string.removing));
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	private MyHandler handler = new MyHandler(this);
	private static class MyHandler extends Handler {
		private WeakReference<SysIndoor> reference;
		public MyHandler(SysIndoor activity) {
			this.reference = new WeakReference<SysIndoor>(activity);
		}
		public void handleMessage(android.os.Message msg) {
			SysIndoor activity = this.reference.get();
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
				activity.findView();
				break;
			}
		};
	};
	/**
	 * 起一个线程执行删除
	 * 
	 * @author Administrator
	 *
	 */
	public class DeleteThread extends Thread {
		ArrayList<String> removeList = new ArrayList<String>();
		public DeleteThread(ArrayList<String> removeList) {
			this.removeList = removeList;
		}
		@Override
		public void run() {
			Message msg;
			ConfigIndoor config = ConfigIndoor.getInstance(mContext);
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
	@SuppressLint("InflateParams")
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:// 删除建筑物
			break;
		case 1:// 编辑
			editBuilding(buildingList.get(ITEM_POSITION).getDirPath());
			findView();
			break;
		case 2:// 重命名
				// 从XML获取弹出窗口中的内容:EditText
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.alert_dialog_edittext, null);
			final EditText alert_EditText = (EditText) textEntryView.findViewById(R.id.alert_textEditText);
			basicDialog = new BasicDialog.Builder(SysIndoor.this).setIcon(android.R.drawable.ic_menu_edit)
					.setTitle(R.string.sys_indoor_building_rename).setView(textEntryView)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String input = alert_EditText.getText().toString().trim();
							boolean exist = false;
							for (int i = 0; i < buildingList.size(); i++) {
								if (input.equals(buildingList.get(i).getName())) {
									exist = true;
									break;
								}
							}
							if (exist) {
								Toast.makeText(getApplicationContext(),
										"\"" + input + "\"" + getString(R.string.sys_indoor_alert_existName),
										Toast.LENGTH_LONG).show();
							} else if (input.length() < 1) {
								Toast.makeText(getApplicationContext(), getString(R.string.sys_indoor_alert_nullName),
										Toast.LENGTH_LONG).show();
							} else {
								ConfigIndoor config = ConfigIndoor.getInstance(mContext);
								// 修改xml文件中的建筑名
								config.setBuildingName(buildingList.get(ITEM_POSITION), input);
								findView();
							}
						}
					}).setNegativeButton(R.string.str_cancle).show();
			break;
		case 3:
			String filepath = buildingList.get(ITEM_POSITION).getBuildMapPath();
			if (filepath != null) {
				File imgFile = new File(filepath);
				if (imgFile.isFile()) {
					BitmapFactory.Options opts = new Options();
					opts.inSampleSize = 3;
					Bitmap bmp = BitmapFactory.decodeFile(filepath, opts);
					LayoutInflater lif = LayoutInflater.from(this);
					View view = lif.inflate(R.layout.alert_dialog_imageview, null);
					ImageView imageView = (ImageView) view.findViewById(R.id.ImageView01);
					imageView.setImageBitmap(bmp);
					basicDialog = new BasicDialog.Builder(this).setTitle(buildingList.get(ITEM_POSITION).getName())
							.setView(view).setNeutralButton(R.string.str_return, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							}).show();
				}
			} else {
				Toast.makeText(SysIndoor.this, R.string.main_indoor_buildNomap, Toast.LENGTH_SHORT).show();
			}
			break;
		}
		return super.onContextItemSelected(item);
	}
	/*
	 * @Override//添加菜单 public boolean onCreateOptionsMenu(Menu menu){
	 * super.onCreateOptionsMenu(menu); MenuInflater mInflater =
	 * getMenuInflater(); mInflater.inflate(R.menu.indoor, menu); return true; }
	 * 
	 * @Override//菜单点击事件 public boolean onOptionsItemSelected(MenuItem item){
	 * super.onOptionsItemSelected(item); showDialog( item.getItemId() ); return
	 * true; }
	 */
	@SuppressLint("InflateParams")
	/***************************************************
	 * 继承方法: 重写activity的onCreateDialog弹出对话框 *
	 * 
	 * @see com.walktour.framework.ui.BasicActivity#onCreateDialog(int)
	 ****************************************************/
	@Override
	@SuppressWarnings("deprecation")
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		// 返回"添加建筑物"对话框
		case R.id.menu_newBuilding:
			// 从XML获取弹出窗口中的内容:EditText
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.alert_dialog_edittext, null);
			final EditText alert_EditText = (EditText) textEntryView.findViewById(R.id.alert_textEditText);
			return new BasicDialog.Builder(SysIndoor.this).setIcon(android.R.drawable.ic_menu_edit)
					.setTitle(R.string.sys_indoor_newBuilding).setView(textEntryView)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String input = alert_EditText.getText().toString().trim();
							boolean exist = false;
							for (int i = 0; i < buildingList.size(); i++) {
								if (input.equals(buildingList.get(i).getName())) {
									exist = true;
									break;
								}
							}
							if (exist) {
								Toast.makeText(getApplicationContext(),
										"\"" + input + "\"" + getString(R.string.sys_indoor_alert_existName),
										Toast.LENGTH_LONG).show();
							} else if (input.length() < 1) {
								Toast.makeText(getApplicationContext(), getString(R.string.sys_indoor_alert_nullName),
										Toast.LENGTH_LONG).show();
							} else {
								ConfigIndoor config = ConfigIndoor.getInstance(mContext);
								// 修改记录楼层信息的xml文件,添加楼层的目录
								if (config.addBuilding(mContext,input)) {
									// 刷新界面
									findView();
									createBuild(input);
								} else {
									Toast.makeText(getApplicationContext(),
											getString(R.string.sys_indoor_alert_errorName), Toast.LENGTH_LONG).show();
								}
							}
						}
					}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							/* User clicked cancel so do some stuff */
						}
					}).create();
		case MENU_SHOW_DIALOG:
			return new BasicDialog.Builder(SysIndoor.this).setTitle(buildingList.get(ITEM_POSITION).getName())
					.setItems(
							new String[] { getResources().getString(R.string.sys_indoor_building_del),
									getResources().getString(R.string.sys_indoor_building_edit),
									getResources().getString(R.string.browser) },
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									switch (which) {
									case 0:
										new BasicDialog.Builder(SysIndoor.this)
												.setIcon(android.R.drawable.ic_menu_delete).setTitle(R.string.delete)
												.setMessage(R.string.main_indoor_delete)
												.setPositiveButton(R.string.delete,
														new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												showProgressDialog();
												ArrayList<String> removeList = new ArrayList<String>();
												ArrayList<String> removeListFromDB = new ArrayList<String>();
												removeList.add(buildingList.get(ITEM_POSITION).getDirPath());
												removeListFromDB.add(buildingList.get(ITEM_POSITION).getName());
												new DeleteThread(removeList).start();
												deleteBuild(removeListFromDB);
											}
										}).setNegativeButton(R.string.str_cancle).show();
										break;
									case 1:
										editBuilding(buildingList.get(ITEM_POSITION).getDirPath());
										findView();
										break;
									case 2:
										String filepath = buildingList.get(ITEM_POSITION).getBuildMapPath();
										if (filepath != null) {
											File imgFile = new File(filepath);
											if (imgFile.isFile()) {
												BitmapFactory.Options opts = new Options();
												opts.inSampleSize = 3;
												Bitmap bmp = BitmapFactory.decodeFile(filepath, opts);
												LayoutInflater lif = LayoutInflater.from(SysIndoor.this);
												View view = lif.inflate(R.layout.alert_dialog_imageview, null);
												ImageView imageView = (ImageView) view.findViewById(R.id.ImageView01);
												imageView.setImageBitmap(bmp);
												new BasicDialog.Builder(SysIndoor.this)
														.setTitle(buildingList.get(ITEM_POSITION).getName())
														.setView(view).setNeutralButton(R.string.str_return,
																new DialogInterface.OnClickListener() {
													@Override
													public void onClick(DialogInterface dialog, int which) {
														dialog.dismiss();
													}
												}).create().show();
											}
										} else {
											Toast.makeText(SysIndoor.this, R.string.main_indoor_buildNomap,
													Toast.LENGTH_SHORT).show();
										}
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
	// ----私有方法--------------------------------------------------------------------
	/**
	 * 创建建筑物RecordBuild并插入数据库
	 * 
	 * @param name
	 */
	private void createBuild(String name) {
		mSysBuildingManager.addBuilding(mContext,name);
	}
	/**
	 * 删除建筑物
	 * 
	 * @param buildings
	 */
	private void deleteBuild(ArrayList<String> buildings) {
		for (String name : buildings) {
			mSysBuildingManager.deleteBuilding(name);
		}
	}
	private void editBuilding(String building_name) {
		Intent intent;
		intent = new Intent(com.walktour.gui.setting.SysIndoor.this, SysIndoorBuilding.class);
		// 添加传递参数
		Bundle bundle = new Bundle();
		bundle.putString("name", buildingList.get(ITEM_POSITION).getName());
		bundle.putString("building", building_name);// 传递名称
		bundle.putString("build_address", buildingList.get(ITEM_POSITION).getBuildAddress());// 传递名称
		bundle.putBoolean(KEY_LOADING, this.isLoading);// 当前页面是否加载地图
		intent.putExtras(bundle);
		// 启动intent
		startActivityForResult(intent, 10);
	}
	@SuppressWarnings("deprecation")
	private void newBuilding() {
		this.showDialog(R.id.menu_newBuilding);
	}
	// 关闭此Activity，并返回结果
	protected void backForResult(String path) {
		Intent intent_back = this.getIntent();
		Bundle bundle = new Bundle();
		bundle.putString(SysIndoor.KEY_RESULT, path);// 返回结果
		intent_back.putExtras(bundle);
		this.setResult(RESULT_OK, intent_back);
		this.finish();
	}// end method backForResult
	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @see com.walktour.framework.ui.BasicActivity#onResume()
	 */
	@Override
	protected void onResume() {
		findView();
		super.onResume();
	}
}