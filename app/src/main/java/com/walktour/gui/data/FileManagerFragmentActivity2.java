package com.walktour.gui.data;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.dinglicom.data.model.RecordDetail;
import com.dinglicom.data.model.RecordTestInfo;
import com.dinglicom.data.model.TestRecord;
import com.dinglicom.totalreport.DLMessageModel;
import com.dinglicom.totalreport.RequestSceneXMl;
import com.dinglicom.totalreport.SubDlMessageItem;
import com.dinglicom.totalreport.TotalDetailActivity;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.Utils.WalkStruct.ServerOperateType;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ServerManager;
import com.walktour.control.instance.DataManagerFileList;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.treeview.TreeNode;
import com.walktour.gui.InfoTabHost;
import com.walktour.gui.R;
import com.walktour.gui.applet.ControlBar;
import com.walktour.gui.data.dialog.PopButton;
import com.walktour.gui.data.dialog.PopButton.ClickListener;
import com.walktour.gui.data.model.DBManager;
import com.walktour.gui.data.model.DataModel;
import com.walktour.gui.highspeedrail.HighSpeedRailCommons;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.newmap2.NewInfoTabActivity;
import com.walktour.gui.setting.sysroutine.SysRoutineActivity;
import com.walktour.service.app.DataTransService;
import com.walktour.service.app.datatrans.model.UploadFileModel;
import com.walktour.service.innsmap.InnsmapFactory;
import com.walktour.service.metro.MetroFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 用于自动测试和多网测试模块
 * 
 * @author haohua
 *
 */
@SuppressLint({ "HandlerLeak", "InflateParams" })
@SuppressWarnings("deprecation")
public class FileManagerFragmentActivity2 extends FragmentActivity implements OnClickListener {

	public final static String IS_TOTAL = "IS_TOTAL";

	private DBManager mDbManager;
	private ControlBar mControlBar;
	private Context mContext;
	private FileOperater mFileOperater = null;
	private ServerManager mServer = null;
	/** 打开数据管理界面 当处于上传状态时 更新上传按钮状态 */
	private boolean isUpdateUploadButtonState = true;
	/***
	 * 单实例静态变量:文件列表,可从目录或数据库读取 记录所有数据库记录的文件，文件的主要信息和状态
	 */
	private TreeViewAdapter treeAdapter;
	private int from = 0;// 0:自动测试 1:多网测试
	private FragmentBase fragment;
	private AutoFragment mAutoFragmet;
	private MTFragment mMTFragment;
	private PopButton popButton;
	private ApplicationModel appModel = ApplicationModel.getInstance();
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case FragmentBase.MSG_UPDATE_UI:
				fragment.refreshData();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.file_manager_activity2);
		mContext = this;
		from = getIntent().getExtras().getInt("from");
		mDbManager = DBManager.getInstance(this);
		mFileOperater = new FileOperater(this.mContext);
		mDbManager.setHandler(mHandler);
		initView();
		initControlBar();
		invalidateOptionsMenu();
		mServer = ServerManager.getInstance(this);
		registReceiver();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.pointer:// 返回
			finish();
			break;
		case R.id.Button01:// 全选
			chooseAll(view);
			break;
		case R.id.Button02:// 回放
			if (appModel.isTestJobIsRun()) {
				ToastUtil.showToastShort(this, R.string.str_testing);
				return;
			}
			replay();
			break;
		case R.id.Button03:// 统计
			if (appModel.isTestJobIsRun()) {
				ToastUtil.showToastShort(this, R.string.str_testing);
				return;
			}
			total();
			break;
		case R.id.Button04:// 删除
			if (appModel.isTestJobIsRun()) {
				ToastUtil.showToastShort(this, R.string.str_testing);
				return;
			}
			deleteMode();
			break;
		case R.id.Button05:// 上传
			if (appModel.isTestJobIsRun()) {
				ToastUtil.showToastShort(this, R.string.str_testing);
				return;
			}
			if (((Button) view).getText().equals(getResources().getString(R.string.upload))) {
				upload();
			} else {
				stopUpload();
			}
			break;

		default:
			break;
		}
	}

	private void initView() {

		mAutoFragmet = new AutoFragment();
		mMTFragment = new MTFragment();
		fragment = from == 0 ? mAutoFragmet : mMTFragment;
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).commit();
	}

	private void initControlBar() {
		mControlBar = (ControlBar) findViewById(R.id.ControlBar);
		Button btnChoose = mControlBar.getButton(0);
		Button btnReview = mControlBar.getButton(1);
		Button btnTotal = mControlBar.getButton(2);
		Button btnDelete = mControlBar.getButton(3);
		Button btnUpload = mControlBar.getButton(4);
		btnChoose.setText(getResources().getString(R.string.str_checkall));
		btnReview.setText(getResources().getString(R.string.data_replay));
		btnTotal.setText(getResources().getString(R.string.total_total));
		btnDelete.setText(getResources().getString(R.string.delete));
		btnUpload.setText(getResources().getString(R.string.upload));
		btnChoose.setOnClickListener(this);
		btnReview.setOnClickListener(this);
		btnTotal.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
		btnUpload.setOnClickListener(this);

		btnChoose.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_select),
				null, null);
		btnReview.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_undo),
				null, null);
		btnTotal.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_edit), null,
				null);
		btnDelete.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_clear),
				null, null);
		btnUpload.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_upload),
				null, null);
	}

	// ==========================================================================
	/**
	 * 删除模式
	 */
	private void deleteMode() {
		final FragmentBase frag = from == 0 ? mAutoFragmet : mMTFragment;
		final TreeViewAdapter adapter = frag.getAdapter();
		View view = LayoutInflater.from(mContext).inflate(R.layout.data_manager_delete_bottom_button, null);
		popButton = new PopButton(mContext, view).setButtonListener(R.id.btn_cancel, new ClickListener() {

			@Override
			public void onClick() {
				frag.getAdapter().setDeleteMode(false);
				frag.getAdapter().notifyDataSetChanged();
			}
		}, true).setButtonListener(R.id.btn_choose_all, new ClickListener() {

			@Override
			public void onClick() {
				for (TreeNode n : adapter.getDatas()) {
					deleteChoose(adapter, n);
				}
				adapter.notifyDataSetChanged();
			}
		}, false).setButtonListener(R.id.btn_choose_non, new ClickListener() {

			@Override
			public void onClick() {
				adapter.deleteList.clear();
				for (TreeNode n : adapter.getDatas()) {
					deleteInverse(n);
				}
				adapter.notifyDataSetChanged();
			}
		}, false).setButtonListener(R.id.btn_delete, new ClickListener() {

			@Override
			public void onClick() {
				delete(frag.getAdapter().deleteList);
				frag.getAdapter().getDatas().removeAll(frag.getAdapter().deleteList);
				frag.getAdapter().deleteList.clear();
				frag.getAdapter().notifyDataSetChanged();
				frag.getAdapter().setDeleteMode(false);
			}
		}, true);
		popButton.show();
		adapter.setPopButton(popButton);
		adapter.setButtonState(3, R.id.btn_delete);
		frag.getAdapter().setDeleteMode(true);
		frag.getAdapter().notifyDataSetChanged();

	}

	private void delete(List<TreeNode> list) {
		ArrayList<DataModel> fileList = new ArrayList<DataModel>();
		for (TreeNode n : list) {
			DataModel d = (DataModel) n.getValue();
			if (!d.isFolder) {
				fileList.add(d);
			}
			n.getParent().deleteChild(n);
		}
		// 删除SDCard文件
		mFileOperater.delete(fileList);
	}

	// private void delete(TreeNode node) {
	//
	// }

	/**
	 * 统计
	 */
	private void total() {
		final FragmentBase frag = from == 0 ? mAutoFragmet : mMTFragment;
		popButton = new PopButton(mContext, null).setOKButtonText(getResources().getString(R.string.total_total))
				.setOKButtonListener(new ClickListener() {

					@Override
					public void onClick() {
						totalReport(frag.FLAG, frag.getAdapter().totalList);
						frag.getAdapter().setTotalMode(false);
						frag.getAdapter().notifyDataSetChanged();
					}
				}).setCancelButtonListener(new ClickListener() {

					@Override
					public void onClick() {
						frag.getAdapter().setTotalMode(false);
						frag.getAdapter().notifyDataSetChanged();
					}
				});
		popButton.show();
		frag.getAdapter().setPopButton(popButton);
		frag.getAdapter().setButtonState(2, R.id.btn_ok);
		frag.getAdapter().setTotalMode(true);
		frag.getAdapter().notifyDataSetChanged();
	}

	/**
	 * 统计流程
	 */
	private void totalReport(String flag, List<DataModel> lisDataModels) {
		int flagValue = -1;
		if (flag.equalsIgnoreCase("CQT")) {
			flagValue = TotalDetailActivity.TEST_TYPE_CQT;
		} else {
			flagValue = TotalDetailActivity.TEST_TYPE_DT;
		}
		switch (flagValue) {
		case 0:
			DLMessageModel dlMessageModelCqt = new DLMessageModel();
			List<SubDlMessageItem> subDlMessageItemsCqt = new ArrayList<SubDlMessageItem>();
			dlMessageModelCqt.setSendTime(UtilsMethod.sdFormatss.format(System.currentTimeMillis()));
			List<String> allFilePathListCqt = new ArrayList<String>();
			SubDlMessageItem allSubDlMessageItemCqt = new SubDlMessageItem();
			for (int i = 0; i < lisDataModels.size(); i++) {
				List<String> filePathListCqt = new ArrayList<String>();
				SubDlMessageItem subDlMessageItemCqt = new SubDlMessageItem();
				filePathListCqt.add(lisDataModels.get(i).getFilePath(FileType.DDIB.getFileTypeName()));
				subDlMessageItemCqt.setSceneName(lisDataModels.get(i).testRecord.file_name);
				subDlMessageItemCqt.setDataFileName(filePathListCqt);
				subDlMessageItemsCqt.add(subDlMessageItemCqt);
				allFilePathListCqt.add(lisDataModels.get(i).getFilePath(FileType.DDIB.getFileTypeName()));
			}
			allSubDlMessageItemCqt.setDataFileName(allFilePathListCqt);
			allSubDlMessageItemCqt.setSceneName("DLSum");
			subDlMessageItemsCqt.add(0, allSubDlMessageItemCqt);
			dlMessageModelCqt.setSubDlMessageItems(subDlMessageItemsCqt);
			RequestSceneXMl.getInstance().setFilePathList(allFilePathListCqt); // 存储选中数据管理,供报表导出使用
			RequestSceneXMl.getInstance().xmlFileCreator(dlMessageModelCqt);
			break;
		case 1:
			DLMessageModel dlMessageModel = new DLMessageModel();
			List<SubDlMessageItem> subDlMessageItems = new ArrayList<SubDlMessageItem>();
			SubDlMessageItem subDlMessageItem = new SubDlMessageItem();
			List<String> filePathList = new ArrayList<String>();
			dlMessageModel.setSendTime(UtilsMethod.sdFormatss.format(System.currentTimeMillis()));
			dlMessageModel.setBusiness("All");
			for (int i = 0; i < lisDataModels.size(); i++) {
				filePathList.add(lisDataModels.get(i).getFilePath(FileType.DDIB.getFileTypeName()));
				subDlMessageItem.setSceneName("DT");
			}
			RequestSceneXMl.getInstance().setFilePathList(filePathList); // 存储选中数据管理,供报表导出使用
			subDlMessageItem.setDataFileName(filePathList);
			subDlMessageItems.add(subDlMessageItem);
			dlMessageModel.setSubDlMessageItems(subDlMessageItems);
			RequestSceneXMl.getInstance().xmlFileCreator(dlMessageModel);
			break;

		default:
			break;
		}
		// Intent intent = new Intent();
		// intent.putExtra(FileManagerFragmentActivity.KEY_TOTAL_RUN,
		// lisDataModels.size() > 0 ? true : false);
		// intent.putExtra(FileManagerFragmentActivity.IS_TOTAL, flagValue);
		// FileManagerFragmentActivity2.this.setResult(RESULT_OK, intent);
		// FileManagerFragmentActivity2.this.finish();

		SharePreferencesUtil.getInstance(mContext).saveInteger(FileManagerFragmentActivity.IS_TOTAL, flagValue);
		Intent intent2 = new Intent(this, TotalDetailActivity.class);
		startActivity(intent2);
		intent2 = null;
		overridePendingTransition(R.anim.open_next, R.anim.close_main);
	}

	/**
	 * 回放
	 */
	private void replay() {
		final FragmentBase frag = from == 0 ? mAutoFragmet : mMTFragment;
		popButton = new PopButton(mContext, null).setOKButtonText(getResources().getString(R.string.data_replay))
				.setOKButtonListener(new ClickListener() {

					@Override
					public void onClick() {
						if (frag.getAdapter().replayList.size() > 0) {
							runReplay(frag.getAdapter().replayList.get(0));
						} else {
							Toast.makeText(mContext, getResources().getString(R.string.str_selectfile), Toast.LENGTH_SHORT).show();
						}
						frag.getAdapter().setReplayMode(false);
						frag.getAdapter().notifyDataSetChanged();
					}
				}).setCancelButtonListener(new ClickListener() {

					@Override
					public void onClick() {
						frag.getAdapter().setReplayMode(false);
						frag.getAdapter().notifyDataSetChanged();
					}
				});
		popButton.show();
		frag.getAdapter().setPopButton(popButton);
		frag.getAdapter().setButtonState(1, R.id.btn_ok);
		frag.getAdapter().setReplayMode(true);
		frag.getAdapter().notifyDataSetChanged();
	}

	/**
	 * 执行回放
	 * 
	 * @param dataModel
	 */
	private void runReplay(DataModel dataModel) {
		String filePath = dataModel.getFilePath(FileType.DDIB.name());
		if (!StringUtil.isNullOrEmpty(filePath)) {
			TestRecord testRecord = dataModel.testRecord;
			ArrayList<RecordTestInfo> testInfos = testRecord.getRecordTestInfo();
			if (ApplicationModel.getInstance().getSelectScene() == SceneType.Metro) {// 地铁场景,需要设置城市和线路
				String city = "";
				String route = "";
				for (RecordTestInfo testInfo : testInfos) {
					if (testInfo.key_info.equals("city")) {
						city = testInfo.key_value;
					}
					if (testInfo.key_info.equals("metro_line")) {
						route = testInfo.key_value;
					}
				}
				if (!StringUtil.isNullOrEmpty(city) && !StringUtil.isNullOrEmpty(route)) {
					MetroFactory factory = MetroFactory.getInstance(mContext);
					factory.setReplayCityName(city);
					factory.setReplayRouteName(route);
				}
				NewMapFactory.getInstance().setMapType(NewMapFactory.MAP_TYPE_NONE);
			} else if (ApplicationModel.getInstance().getSelectScene() == SceneType.HighSpeedRail) {// 高铁场景,需要设置线路
				String route = "";
				for (RecordTestInfo testInfo : testInfos) {
					if (testInfo.key_info.equals("high_speed_rail")) {
						route = testInfo.key_value;
					}
				}
				if (!StringUtil.isNullOrEmpty(route)) {
					HighSpeedRailCommons.setRunningRoute(route);
				}
			} else if (testRecord.test_type == WalkStruct.TestType.CQT.getTestTypeId()) {
				File file = new File(filePath);
				if (file.exists()) {
					String floorName = file.getParentFile().getName();
					if (floorName.indexOf("_") > 0) {
						InnsmapFactory.getInstance(mContext).setReplayFloorId(floorName.substring(floorName.lastIndexOf("_") + 1));
					}
				}
			}
			Intent intent = new Intent(this, NewInfoTabActivity.class);
			intent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_OTHER);
			intent.putExtra("isReplay", true);
			intent.putExtra("isReplayNow", true);
			intent.putExtra("filePath", filePath);
			startActivity(intent);
			overridePendingTransition(R.anim.open_next, R.anim.close_main);
		} else {
			Toast.makeText(mContext, getResources().getString(R.string.str_no_replay_file), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 全选/反选（上传操作）
	 */
	private void chooseAll(View v) {
		final FragmentBase frag = from == 0 ? mAutoFragmet : mMTFragment;
		TreeViewAdapter adapter = frag.getAdapter();
		if (((Button) v).getText().equals(getResources().getString(R.string.str_checkall))) {
			for (TreeNode n : adapter.getDatas()) {
				choose(adapter, n);
			}
			((Button) v).setText(getResources().getString(R.string.str_checknon));
			((Button) v).setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.controlbar_unallcheck), null, null);
			adapter.notifyDataSetChanged();
		} else {
			adapter.selectedList.clear();
			for (TreeNode n : adapter.getDatas()) {
				inverse(n);
			}
			((Button) v).setText(getResources().getString(R.string.str_checkall));
			((Button) v).setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.controlbar_select), null, null);
			frag.getAdapter().notifyDataSetChanged();
		}

	}

	/**
	 * 删除选择
	 * 
	 * @param adapter
	 * @param node
	 */
	private void deleteChoose(TreeViewAdapter adapter, TreeNode node) {
		DataModel d = (DataModel) node.getValue();
		d.isDeleteChecked = true;
		adapter.deleteList.add(node);
		for (TreeNode n : node.getChildren()) {
			deleteChoose(adapter, n);
		}
	}

	/**
	 * 删除反选
	 * 
	 * @param node
	 */
	private void deleteInverse(TreeNode node) {
		((DataModel) node.getValue()).isDeleteChecked = false;
		for (TreeNode n : node.getChildren()) {
			deleteInverse(n);
		}
	}

	/**
	 * 选择
	 * 
	 * @param adapter
	 * @param node
	 */
	private void choose(TreeViewAdapter adapter, TreeNode node) {
		DataModel d = (DataModel) node.getValue();
		d.isChecked = true;
		adapter.selectedList.add(d);
		for (TreeNode n : node.getChildren()) {
			choose(adapter, n);
		}
	}

	/**
	 * 反选
	 * 
	 * @param node
	 */
	private void inverse(TreeNode node) {
		((DataModel) node.getValue()).isChecked = false;
		for (TreeNode n : node.getChildren()) {
			inverse(n);
		}
	}

	private void changeUploadButtonState(boolean isUploading) {
		Button btnUpload = mControlBar.getButton(4);
		if (isUploading) {
			btnUpload.setText(getResources().getString(R.string.stop));
			btnUpload.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_stop),
					null, null);
		} else {
			btnUpload.setText(getResources().getString(R.string.upload));
			btnUpload.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_upload),
					null, null);
		}
	}

	/**
	 * 停止上传
	 */
	private void stopUpload() {
		final FragmentBase frag = from == 0 ? mAutoFragmet : mMTFragment;
		List<DataModel> modelList = new ArrayList<DataModel>();
		for (TreeNode treeNode : frag.getAdapter().getDatas()) {
			DataModel dataModel = (DataModel) treeNode.getValue();
			if (!dataModel.isFirstLevel && !dataModel.isFolder) {
				if (dataModel.getState() == 0 || (dataModel.getState() != -1 && dataModel.getState() != 100)) {
					modelList.add(dataModel);
				}
			}
		}

		// List<DataModel> modelList = treeAdapter.selectedList;
		System.out.println("需要停止上传的文件列表大小:" + modelList.size());
		List<UploadFileModel> uploadFiles = new ArrayList<UploadFileModel>();

		Set<Entry<String, Boolean>> set = mServer.getUploadFileTypes(mContext).entrySet();
		Iterator<?> iterator = set.iterator();

		final String[] strArray = new String[set.size()];
		final boolean[] checkedItems = new boolean[set.size()];
		final HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();

		int count = 0;
		while (iterator.hasNext()) {
			@SuppressWarnings("unchecked")
			Entry<String, Boolean> entry = (Entry<String, Boolean>) iterator.next();
			strArray[count] = entry.getKey();
			checkedItems[count] = entry.getValue();
			hashMap.put(strArray[count], checkedItems[count]);
			count++;
		}

		if (count == 0)
			return;
		Set<FileType> fileTypes = new HashSet<FileType>();
		if (count > 0) {
			for (String key : hashMap.keySet()) {
				if (hashMap.get(key)) {
					if (FileType.getFileTypeByName(key) != null)
						fileTypes.add(FileType.getFileTypeByName(key));
				}
			}
		}

		for (int i = 0; i < modelList.size(); i++) {
			DataModel model = modelList.get(i);
			UploadFileModel file = new UploadFileModel(model.testRecord.record_id, model.testRecord.test_type);
			file.setName(model.testRecord.file_name);
			for (int j = 0; j < model.testRecord.getRecordDetails().size(); j++) {
				RecordDetail detail = model.testRecord.getRecordDetails().get(j);
				if (detail.file_type == FileType.ORGRCU.getFileTypeId()) {
					file.setParentPath(detail.file_path);
				}
			}
			if (ApplicationModel.getInstance().getNetList().contains(WalkStruct.ShowInfoType.TelecomSwitch))
				fileTypes.add(FileType.FloorPlan);
			file.setFileTypes(fileTypes);
			uploadFiles.add(file);
		}

		// 停止上传
		mServer.uploadFile(mContext, WalkStruct.ServerOperateType.stopUpload, uploadFiles);

		changeUploadButtonState(false);
		setUnuploadState();
	}

	/**
	 * 上传
	 */
	private void upload() {
		final FragmentBase frag = from == 0 ? mAutoFragmet : mMTFragment;
		TreeViewAdapter adapter = frag.getAdapter();
		if (adapter.selectedList.size() == 0) {
			Toast.makeText(mContext, getResources().getString(R.string.str_select_upload_file), Toast.LENGTH_SHORT).show();
			return;
		}

		// 先检查服务器设置
		ServerManager sm = ServerManager.getInstance(mContext);
		if (!sm.hasUploadServerSet()) {
			new BasicDialog.Builder(mContext).setTitle(R.string.str_tip).setMessage(R.string.fleet_set_notset_notify)
					.setNeutralButton(R.string.setting, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							Intent intent = new Intent(mContext, SysRoutineActivity.class);
							intent.setAction(SysRoutineActivity.SHOW_DATA_UPLOAD_TAB);
							startActivity(intent);
						}
					}).show();
		}

		// 先判断网络是否可用
		else if (MyPhoneState.getInstance().isNetworkAvirable(getApplicationContext())) {
			int state = MyPhoneState.getInstance().getDataConnectState(getApplicationContext());
			// Wifi模式
			if (mServer.getUploadNetWork() == ServerManager.UPLOAD_NETWORK_ONLY_WIFI) {
				if (state != MyPhoneState.DATA_STATE_WIFI) {
					// 提示网络不可用
					Toast.makeText(getApplicationContext(), getString(R.string.fleet_network_wifi), Toast.LENGTH_SHORT).show();
					return;
				}

			}
			// data模式
			else if (mServer.getUploadNetWork() == ServerManager.UPLOAD_NETWORK_ONLY_MOBILE) {
				if (state != MyPhoneState.DATA_STATE_MOBILE) {
					// 提示网络不可用
					Toast.makeText(getApplicationContext(), getString(R.string.fleet_network_data), Toast.LENGTH_SHORT).show();
					return;
				}

			}

			// 添加选中的文件
			ArrayList<String> uploadList = new ArrayList<String>();
			for (int i = 0; i < adapter.selectedList.size(); i++) {
				// 如果文件被选中则添加到上传列表
				DataModel d = adapter.selectedList.get(i);
				DataManagerFileList.getInstance(mContext).getUploadList().add(d);
				uploadList.addAll(adapter.selectedList.get(i).getAllFilePath());
				// 修改数据库
				d.setState(DataModel.STATUS_UPLOAD_WAITING);
				DataManagerFileList.getInstance(mContext).updateFileUploadState(d);

			}

			// 如果添加列表不为空
			if (uploadList.size() > 0) {
				showFileTypeDialog();
			} else {
				// Toast:所选文件已上传
				Toast.makeText(getApplicationContext(), getString(R.string.str_fileuled), Toast.LENGTH_SHORT).show();
			}

		} else {
			// 提示网络不可用
			Toast.makeText(getApplicationContext(), R.string.fleet_netIsOff, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 创建上传文件类型对话框
	 */
	private void showFileTypeDialog() {
		// final Context context = this;
		// final FragmentBase frag =
		// ((FragmentBase)adapter.getItem(pager.getCurrentItem()));
		treeAdapter = ((FragmentBase) (from == 0 ? mAutoFragmet : mMTFragment)).getAdapter();
		Set<Entry<String, Boolean>> set = mServer.getUploadFileTypes(mContext).entrySet();
		Iterator<?> iterator = set.iterator();

		final String[] strArray = new String[set.size()];
		final boolean[] checkedItems = new boolean[set.size()];
		final HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();

		int count = 0;
		while (iterator.hasNext()) {
			@SuppressWarnings("unchecked")
			Entry<String, Boolean> entry = (Entry<String, Boolean>) iterator.next();
			strArray[count] = entry.getKey();
			checkedItems[count] = entry.getValue();
			hashMap.put(strArray[count], checkedItems[count]);
			count++;
		}

		if (count > 1) {
			new BasicDialog.Builder(mContext).setTitle(R.string.sys_setting_data_upload_file_type)
					.setMultiChoiceItems(strArray, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which, boolean isChecked) {
							hashMap.put(strArray[which], isChecked);
						}
					}).setPositiveButton(R.string.upload, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							uploadFile(hashMap);
							setUploadWaitingState();
							changeUploadButtonState(true);
							// 在ListView中显示要上传的文件的进度条
							if (treeAdapter != null) {
								// 通知adatpter更新界面
								treeAdapter.notifyDataSetChanged();
							}
						}
					}).show();
		} else {
			uploadFile(hashMap);
			setUploadWaitingState();
			changeUploadButtonState(true);
			// 在ListView中显示要上传的文件的进度条
			if (treeAdapter != null) {
				// 通知adatpter更新界面
				treeAdapter.notifyDataSetChanged();
			}

		}
	}

	/**
	 * 上传文件
	 * 
	 * @param hashMap
	 *          要上传的文件类型映射
	 */
	private void uploadFile(Map<String, Boolean> hashMap) {
		int count = 0;
		for (String key : hashMap.keySet()) {
			if (hashMap.get(key))
				count++;
		}
		if (count == 0)
			return;
		Set<FileType> fileTypes = new HashSet<FileType>();
		if (count > 0) {
			for (String key : hashMap.keySet()) {
				if (hashMap.get(key)) {
					if (FileType.getFileTypeByName(key) != null)
						fileTypes.add(FileType.getFileTypeByName(key));
				}
			}
		}
		List<DataModel> modelList = treeAdapter.selectedList;
		List<UploadFileModel> uploadFiles = new ArrayList<UploadFileModel>();
		for (int i = 0; i < modelList.size(); i++) {
			DataModel model = modelList.get(i);
			UploadFileModel file = new UploadFileModel(model.testRecord.record_id, model.testRecord.test_type);
			file.setName(model.testRecord.file_name);
			file.setParentPath(model.testRecord.getRecordDetails().get(0).file_path);
			file.setFileTypes(fileTypes);
			uploadFiles.add(file);
		}

		// 上传
		mServer.uploadFile(mContext, WalkStruct.ServerOperateType.uploadTestFile, uploadFiles);
	}

	private void setUploadWaitingState() {
		List<DataModel> modelList = treeAdapter.selectedList;
		for (DataModel dataModel : modelList) {
			dataModel.setState(0);
		}
		treeAdapter.notifyDataSetChanged();
	}

	/**
	 * 设置未上传状态
	 */
	private void setUnuploadState() {
		final FragmentBase frag = from == 0 ? mAutoFragmet : mMTFragment;
		List<DataModel> modelList = new ArrayList<DataModel>();
		for (TreeNode treeNode : frag.getAdapter().getDatas()) {
			DataModel dataModel = (DataModel) treeNode.getValue();
			if (!dataModel.isFirstLevel && !dataModel.isFolder) {
				if (dataModel.getState() == 0 || (dataModel.getState() != -1 && dataModel.getState() != 100)) {
					modelList.add(dataModel);
				}
			}
		}

		for (DataModel dataModel : modelList) {
			// 修改数据库
			dataModel.setState(DataModel.STATUS_UPLOAD_FAIL);
			DataManagerFileList.getInstance(mContext).updateFileUploadState(dataModel);
		}
		treeAdapter.notifyDataSetChanged();
	}

	// ==========================================================================
	private void registReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DataTransService.EXTRA_DATA_TRANS_FILE_PROGRESS);
		intentFilter.addAction(DataTransService.EXTRA_DATA_TRANS_END);
		registerReceiver(mUploadStateReceiver, intentFilter);
	}

	private void unRegistReceiver() {
		try {
			unregisterReceiver(mUploadStateReceiver);
		} catch (Exception e) {
		}
	}

	private BroadcastReceiver mUploadStateReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, android.content.Intent intent) {
			String action = intent.getAction();
			// System.out.println("自动测试上传:" + action);
			if (action.equals(DataTransService.EXTRA_DATA_TRANS_FILE_PROGRESS)) {
				// 上传中
				if (isUpdateUploadButtonState) {
					changeUploadButtonState(true);
					isUpdateUploadButtonState = false;// 改变上传按钮状态后 置false
				}
			} else if (action.equals(DataTransService.EXTRA_DATA_TRANS_END)) {
				// 上传完成
				changeUploadButtonState(false);
				isUpdateUploadButtonState = true;
			}
		};
	};
	// ==========================================================================

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.mDbManager.setHandler(null);
		unRegistReceiver();
	}

}
