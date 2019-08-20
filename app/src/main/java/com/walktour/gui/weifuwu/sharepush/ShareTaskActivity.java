package com.walktour.gui.weifuwu.sharepush;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TimeUtils;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.DateUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.ZipUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.share.UpDownService;
import com.walktour.gui.share.logic.RegisterDeviceLogic;
import com.walktour.gui.share.logic.ShareHttpRequestUtil;
import com.walktour.gui.share.model.BaseResultInfoModel;
import com.walktour.gui.share.upload.UploadManager;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.TaskGroupConfig;
import com.walktour.gui.task.parsedata.model.TestPlanConfig;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.weifuwu.WeiMainActivity;
import com.walktour.gui.weifuwu.business.model.ShareDeviceModel;
import com.walktour.gui.weifuwu.business.model.ShareFileModel;
import com.walktour.gui.weifuwu.business.model.ShareGroupModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.gui.weifuwu.view.refreshlistview.ListViewModel;
import com.walktour.gui.weifuwu.view.refreshlistview.RefreshListViewAdapter;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/***
 * 共享任务组界面
 * 
 * @author weirong.fan
 *
 */
public class ShareTaskActivity extends BasicActivity implements OnClickListener {
	private Context context = ShareTaskActivity.this;
	/** from=1 为从组界面跳转过来的,from=2为从任务界面跳转过来的 **/
	private int from = 1;
	private LayoutInflater layoutInflater;
	private ArrayList<String> groupIDS = new ArrayList<String>();
	private String nameS = "";
	private ArrayList<String> taskIDS = new ArrayList<String>();
	private List<Integer> recordIDS = new LinkedList<Integer>();
	private List<TaskGroupConfig> taskGroups;
	private List<TaskModel> tasks;
	private List<ShareFileModel> listShareFile = new LinkedList<ShareFileModel>();
	private RefreshListViewAdapter adapterS;
	private BaseAdapter adapterX;
	private ListView listViewX;
	/** 是否点击了历史按钮 **/
	private boolean isHistory = false;
	private TestPlanConfig testPlanConfig = new TestPlanConfig();
	/** 选中的发送设备 **/
	private List<ListViewModel> listDevices = new LinkedList<ListViewModel>();
	/** 列表中的设备信息 **/
	private List<ListViewModel> listDevicesModel = new LinkedList<ListViewModel>();
	/** 列表中的设备信息 **/
	private List<ListViewModel> listGroup = new LinkedList<ListViewModel>();
	private List<ListViewModel> listDevice = new LinkedList<ListViewModel>();
	private EditText inputTxt;
	private String inputText = "";
	private TextView titleNameTV; 
	private ListView mListView;
	/** 是否选择群组 **/
	private boolean isSelectGroup = false;
	private TextView deviceTv;
	private TextView groupTv;
	private boolean isFilterType = false;  
	private ApplicationModel appModel = ApplicationModel.getInstance();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layoutInflater = LayoutInflater.from(this);
		from = this.getIntent().getIntExtra("from", 1);
		initViews();
	}
	private void initViews() {
		this.setContentView(R.layout.sharetaskgrouplayout);
		deviceTv = initTextView(R.id.typedevice);
		deviceTv.setOnClickListener(this);
		groupTv = initTextView(R.id.typegroup);
		groupTv.setOnClickListener(this);
		inputTxt = initEditText(R.id.inputxt);
		titleNameTV = initTextView(R.id.titlename);
		inputTxt.addTextChangedListener(textWatcher);
		LinearLayout backlayout = (LinearLayout) this.findViewById(R.id.backlayout);
		ListView receivelayout = (ListView) this.findViewById(R.id.receiveobjlayout);
		adapterS = new RefreshListViewAdapter(context, inputText, backlayout, receivelayout, listDevicesModel,
				listDevices);
		mListView = (ListView) findViewById(R.id.id_listview);
		 
		mListView.setAdapter(adapterS);
		// 刷新数据
		new FetchDevice().execute();
		this.initTextView(R.id.title_txt).setText(getString(R.string.share_project_share));
		listViewX = this.initListView(R.id.shareresource);
		nameS = "";
		switch (from) {
		case 1:
			taskGroups = TaskListDispose.getInstance().getTestPlanConfig().getTestSchemas().getTestSchemaConfig()
					.getTaskGroups();
			titleNameTV.setText(getString(R.string.share_project_select_group));
			// 默认进来全部选中所有任务组
			for (TaskGroupConfig tg : taskGroups) {
				groupIDS.add(tg.getGroupID());
				if (!nameS.toString().contains(tg.getGroupName() + "_")) {
					nameS += tg.getGroupName() + "_";
				}
			}
			adapterX = new TaskGroupAdapter();
			listViewX.setAdapter(adapterX);
			break;
		case 2:
			tasks = TaskListDispose.getInstance().getCurrentTaskList();
			titleNameTV.setText(getString(R.string.share_project_select_task));
			// 默认进来全部选中所有任务
			for (TaskModel tm : tasks) {
				taskIDS.add(tm.getTaskID());
				if (!nameS.toString().contains(tm.getTaskName() + "_")) {
					nameS += tm.getTaskName() + "_";
				}
			}
			adapterX = new TaskAdapter();
			listViewX.setAdapter(adapterX);
			break;
		}
		findViewById(R.id.historybtn).setOnClickListener(this);
		findViewById(R.id.pointer).setOnClickListener(this);
		findViewById(R.id.sharesend).setOnClickListener(this);
	}
	 
	/***
	 * 获取设备信息
	 * 
	 * @author weirong.fan
	 *
	 */
	private class FetchDevice extends AsyncTask<Void, Void, Integer> {
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result); 
			adapterS.notifyDataSetChanged();
			mListView.invalidate();
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Integer doInBackground(Void... arg0) {
			try {
				List<ShareGroupModel> listGroups;
				List<ShareDeviceModel> listds = null;
				listDevicesModel.clear();
				// 从服务器取
				if (isSelectGroup) {// 选择群组
					listGroups = ShareDataBase.getInstance(context).fetchAllGroup(inputText);
					
					listGroup.clear();
					for (ShareGroupModel m : listGroups) {
						ListViewModel info = new ListViewModel();
						info.type = ListViewModel.INFO_GROUP;
						info.osType = ListViewModel.OS_GROUP;
						info.code = m.getGroupCode();
						info.describe = m.getGroupName();
						listGroup.add(info);
					}
					listDevicesModel.addAll(listGroup);
				} else {// 选择设备
					listDevice.clear();
					List<Integer> status = new LinkedList<Integer>();
					status.add(ShareDeviceModel.STATUS_ADDED);
					listds = ShareDataBase.getInstance(context).fetAllDevice(inputText, status);
					for (ShareDeviceModel sdm : listds) {
						ListViewModel info = new ListViewModel();
						info.type = ListViewModel.INFO_DEVIE;
						if (sdm.getDeviceOS() == ListViewModel.OS_ANDROID) {
							info.osType = ListViewModel.OS_ANDROID;
						} else {
							info.osType = ListViewModel.OS_IPHONE;
						}
						info.code = sdm.getDeviceCode();
						info.describe = sdm.getDeviceName();
						listDevice.add(info);
					}
					listDevicesModel.addAll(listDevice);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}
	}
	/**
	 * 文本更新监听
	 */
	private TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			inputText = inputTxt.getText().toString();
			listGroup.clear();
			listDevice.clear();
			listDevicesModel.clear();
			new FetchDevice().execute();
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}
		@Override
		public void afterTextChanged(Editable s) {
		}
	};
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.typedevice:
			deviceTv.setBackgroundResource(R.color.single_lanse);
			groupTv.setBackgroundResource(R.color.single_huise);
			isSelectGroup = false;
			new FetchDevice().execute();
			break;
		case R.id.typegroup:
			deviceTv.setBackgroundResource(R.color.single_huise);
			groupTv.setBackgroundResource(R.color.single_lanse);
			isSelectGroup = true;
			new FetchDevice().execute();
			break;
		case R.id.sharesend:
			if (appModel.isTestJobIsRun()) {
				ToastUtil.showToastShort(context, R.string.exe_info);
				return;
			}
			if (listDevices.size() <= 0) {
				ToastUtil.showToastShort(context, R.string.share_project_share_info_obj);
				return;
			}
			try {
				String fileXmlPath = "";
				String fileZipPath = "";
//				if (nameS.toString().length() > 120) {
//					nameS = nameS.substring(0, 120);
//				}
//				String curT = nameS.toString() + "_" + System.currentTimeMillis();
				String curT =""+System.currentTimeMillis();
				String fileName = curT + ".xml";
				String fileZip = curT + ".zip";
				switch (from) {
				case 1:// 组
					if (!isHistory) {
						if (groupIDS.size() <= 0) {
							ToastUtil.showToastShort(context, getString(R.string.share_project_share_info_info));
							return;
						}
						fileXmlPath = AppFilePathUtil.getInstance().getSDCardBaseDirectory()+ShareCommons.SHARE_PATH_BASE+File.separator+
								ShareCommons.SHARE_PATH_TASKGROUP + File.separator+fileName;
						fileZipPath =AppFilePathUtil.getInstance().getSDCardBaseDirectory()+ShareCommons.SHARE_PATH_BASE+File.separator+
								ShareCommons.SHARE_PATH_TASKGROUP + File.separator+ fileZip;
						saveTaskGroup(fileXmlPath);
						File zipF = new File(fileZipPath);
						File srcF = new File(fileXmlPath);
						ZipUtil.zip(srcF, zipF);
						srcF.delete();
						new SendTask(zipF, nameS.toString().substring(0, nameS.toString().length() - 1), listDevices,
								ShareFileModel.FILETYPE_GROUP).execute();
					} else {
						List<ShareFileModel> files = ShareDataBase.getInstance(context).fetchAllFilesByIDS(recordIDS);
						if (null != files && files.size() > 0) {
							for (ShareFileModel sm : files) {
								// 发送历史直接转发即可
								new shareFile(sm.getId(), listDevices).execute();
							}
						}
					}
					break;
				case 2:// 任务
					if (!isHistory) {
						if (taskIDS.size() <= 0) {
							ToastUtil.showToastShort(context, getString(R.string.share_project_share_info_info));
							return;
						}
						fileXmlPath = AppFilePathUtil.getInstance().getSDCardBaseDirectory()+ShareCommons.SHARE_PATH_BASE+File.separator+
								ShareCommons.SHARE_PATH_TASK + File.separator+fileName;
						fileZipPath =AppFilePathUtil.getInstance().getSDCardBaseDirectory()+ShareCommons.SHARE_PATH_BASE+File.separator+
								ShareCommons.SHARE_PATH_TASK + File.separator+ fileZip;
						saveTask(fileXmlPath);
						File zipF = new File(fileZipPath);
						File srcF = new File(fileXmlPath);
						ZipUtil.zip(srcF, zipF);
						srcF.delete();
						new SendTask(zipF, nameS.toString().substring(0, nameS.toString().length() - 1), listDevices,
								ShareFileModel.FILETYPE_TASK).execute();
					} else {
						List<ShareFileModel> files = ShareDataBase.getInstance(context).fetchAllFilesByIDS(recordIDS);
						if (null != files && files.size() > 0) {
							for (ShareFileModel sm : files) {
								// 发送历史直接转发即可
								new shareFile(sm.getId(), listDevices).execute();
							}
						}
					}
					break;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			this.finish();
			break;
		case R.id.historybtn:
			try {
				isHistory = !isHistory;
				nameS = "";
				switch (from) {
				case 1:// 组
					if (isHistory) {
						titleNameTV.setText(getString(R.string.share_project_select_history));
						recordIDS.clear();
						listShareFile = ShareDataBase.getInstance(context)
								.fetchFilesHistory(ShareFileModel.FILETYPE_GROUP);
						adapterX = null;
						adapterX = new TaskXmlAdapter();
						listViewX.setAdapter(adapterX);
					} else {
						titleNameTV.setText(getString(R.string.share_project_select_group));
						taskGroups = TaskListDispose.getInstance().getTestPlanConfig().getTestSchemas()
								.getTestSchemaConfig().getTaskGroups();
						adapterX = null;
						adapterX = new TaskGroupAdapter();
						listViewX.setAdapter(adapterX);
					}
					break;
				case 2:// 任务
					if (isHistory) {
						titleNameTV.setText(getString(R.string.share_project_select_history));
						recordIDS.clear();
						listShareFile = ShareDataBase.getInstance(context)
								.fetchFilesHistory(ShareFileModel.FILETYPE_TASK);
						adapterX = null;
						adapterX = new TaskXmlAdapter();
						listViewX.setAdapter(adapterX);
					} else {
						titleNameTV.setText(getString(R.string.share_project_select_task));
						tasks = TaskListDispose.getInstance().getCurrentTaskList();
						adapterX = null;
						adapterX = new TaskAdapter();
						listViewX.setAdapter(adapterX);
					}
					break;
				}
				adapterX.notifyDataSetChanged();
				listViewX.invalidate();
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		case R.id.pointer:
			finish();
			break;
		}
	}
	private void saveTaskGroup(String filePath) {
		TestPlanConfig config = null;
		if (isHistory) {
			config = testPlanConfig;
		} else {
			config = TaskListDispose.getInstance().getTestPlanConfig();
		}
		try {
			XmlSerializer serializer = Xml.newSerializer();
			File file = new File(filePath);
			OutputStream out = new FileOutputStream(file);
			serializer.setOutput(out, "UTF-8");
			serializer.startDocument("UTF-8", true);
			serializer.startTag(null, "TestPlanConfig");
			if (null != config) {
				Iterator<TaskGroupConfig> it = config.getTestSchemas().getTestSchemaConfig().getTaskGroups().iterator();
				boolean isflag = false;
				while (it.hasNext()) {
					isflag = false;
					TaskGroupConfig value = it.next();
					if (null != groupIDS && groupIDS.size() > 0) {
						for (String group : groupIDS) {
							if (group.equals(value.getGroupID())) {
								isflag = true;
								break;
							}
						}
						if (!isflag)
							it.remove();
					}
				}
				config.writeXml(serializer);
			}
			serializer.endTag(null, "TestPlanConfig");
			serializer.endDocument();
			out.flush();
			out.close();
			TaskListDispose.getInstance().reloadFromXML();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * 保存任务，default组
	 * 
	 * @param filePath
	 */
	private void saveTask(String filePath) {
		TestPlanConfig config = null;
		if (isHistory) {
			config = testPlanConfig;
		} else {
			config = TaskListDispose.getInstance().getTestPlanConfig();
		}
		// 只取默认组数据
		// String groupID = TaskListDispose.getInstance().getCurrentSchemasID()
		// + "_" + TaskListDispose.sDefaultGroupID;
		try {
			XmlSerializer serializer = Xml.newSerializer();
			File file = new File(filePath);
			OutputStream out = new FileOutputStream(file);
			serializer.setOutput(out, "UTF-8");
			serializer.startDocument("UTF-8", true);
			serializer.startTag(null, "TestPlanConfig");
			if (null != config) {
				Iterator<TaskGroupConfig> it = config.getTestSchemas().getTestSchemaConfig().getTaskGroups().iterator();
				boolean isflag = false;
				while (it.hasNext()) {
					isflag = false;
					TaskGroupConfig value = it.next();
					if (!value.getGroupID().equals(TaskListDispose.getInstance().getGroupID()))
						it.remove();
					else {
						if (null != taskIDS && taskIDS.size() > 0) {
							Iterator<TaskModel> tasks = value.getTasks().iterator();
							while (tasks.hasNext()) {
								TaskModel tm = tasks.next();
								isflag = false;
								for (String s : taskIDS) {
									if (s.equals(tm.getTaskID())) {
										isflag = true;
										break;
									}
								}
								if (!isflag) {
									tasks.remove();
								}
							}
						}
					}
				}
				config.writeXml(serializer);
			}
			serializer.endTag(null, "TestPlanConfig");
			serializer.endDocument();
			out.flush();
			out.close();
			TaskListDispose.getInstance().reloadFromXML();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * 任务组列表适配器
	 * 
	 * @author weirong.fan
	 * 
	 */
	private class TaskGroupAdapter extends BaseAdapter {
		public TaskGroupAdapter() {
			super();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.shareprojectlayout_item, parent, false);
			}
			TextView projectName = (TextView) convertView.findViewById(R.id.projectname);
			final CheckBox projectID = (CheckBox) convertView.findViewById(R.id.projectid);
			final TaskGroupConfig tg = taskGroups.get(position);
			projectName.setText(tg.getGroupName() + "");
			final String groupID = tg.getGroupID();
			projectID.setChecked(groupIDS.contains(groupID));
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (projectID.isChecked()) {
						projectID.setChecked(false);
						groupIDS.remove(groupID);
						if (nameS.toString().contains(tg.getGroupName() + "_")) {
							nameS = nameS.replace(tg.getGroupName() + "_", "");
						}
					} else {
						projectID.setChecked(true);
						groupIDS.add(groupID);
						if (!nameS.toString().contains(tg.getGroupName() + "_")) {
							nameS += (tg.getGroupName() + "_");
						}
					}
				}
			});
			projectID.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// 如果已经选择
					if (isChecked) {
						if (!groupIDS.contains(groupID)) {
							groupIDS.add(groupID);
							if (!nameS.toString().contains(tg.getGroupName() + "_")) {
								nameS += (tg.getGroupName() + "_");
							}
						}
					} else {
						if (groupIDS.contains(groupID)) {
							groupIDS.remove(groupID);
							if (nameS.toString().contains(tg.getGroupName() + "_")) {
								nameS = nameS.replace(tg.getGroupName() + "_", "");
							}
						}
					}
				}
			});
			return convertView;
		}
		@Override
		public int getCount() {
			return null == taskGroups ? 0 : taskGroups.size();
		}
		@Override
		public Object getItem(int position) {
			return null == taskGroups ? null : taskGroups.get(position);
		}
		@Override
		public long getItemId(int position) {
			return (long) position;
		}
	}
	/**
	 * 任务列表适配器
	 * 
	 * @author weirong.fan
	 * 
	 */
	private class TaskAdapter extends BaseAdapter {
		public TaskAdapter() {
			super();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.shareprojectlayout_item, parent, false);
			}
			TextView projectName = (TextView) convertView.findViewById(R.id.projectname);
			final CheckBox projectID = (CheckBox) convertView.findViewById(R.id.projectid);
			final TaskModel tm = tasks.get(position);
			projectName.setText(tm.getTaskName() + "");
			projectID.setChecked(taskIDS.contains(tm.getTaskID()));
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (projectID.isChecked()) {
						projectID.setChecked(false);
						taskIDS.remove(tm.getTaskID());
						if (nameS.toString().contains(tm.getTaskName() + "_")) {
							nameS = nameS.replace(tm.getTaskName() + "_", "");
						}
					} else {
						projectID.setChecked(true);
						taskIDS.add(tm.getTaskID());
						if (!nameS.toString().contains(tm.getTaskName() + "_")) {
							nameS += tm.getTaskName() + "_";
						}
					}
				}
			});
			projectID.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// 如果已经选择
					if (isChecked) {
						if (!taskIDS.contains(tm.getTaskID())) {
							taskIDS.add(tm.getTaskID());
							if (!nameS.toString().contains(tm.getTaskName() + "_")) {
								nameS += tm.getTaskName() + "_";
							}
						}
					} else {
						if (taskIDS.contains(tm.getTaskID())) {
							taskIDS.remove(tm.getId());
							if (nameS.toString().contains(tm.getTaskName() + "_")) {
								nameS = nameS.replace(tm.getTaskName() + "_", "");
							}
						}
					}
				}
			});
			return convertView;
		}
		@Override
		public int getCount() {
			return null == tasks ? 0 : tasks.size();
		}
		@Override
		public Object getItem(int position) {
			return null == tasks ? null : tasks.get(position);
		}
		@Override
		public long getItemId(int position) {
			return (long) position;
		}
	}
	/**
	 * 任务列表适配器
	 * 
	 * @author weirong.fan
	 * 
	 */
	private class TaskXmlAdapter extends BaseAdapter {
		public TaskXmlAdapter() {
			super();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.shareprojectlayout_item, parent, false);
			}
			TextView projectName = (TextView) convertView.findViewById(R.id.projectname);
			TextView describe = (TextView) convertView.findViewById(R.id.describe);
			describe.setVisibility(View.VISIBLE);
			final CheckBox projectID = (CheckBox) convertView.findViewById(R.id.projectid);
			final ShareFileModel tm = listShareFile.get(position);
			projectName.setText(tm.getFileName() + "");
			describe.setText(tm.getFileDescribe() + "");
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (projectID.isChecked()) {
						projectID.setChecked(false);
						if (recordIDS.contains(tm.getId()))
							recordIDS.remove(tm.getId());
					} else {
						projectID.setChecked(true);
						if (!recordIDS.contains(tm.getId()))
							recordIDS.add(tm.getId());
					}
				}
			});
			projectID.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// 如果已经选择
					if (isChecked) {
						if (!recordIDS.contains(tm.getId()))
							recordIDS.add(tm.getId());
					} else {
						if (recordIDS.contains(tm.getId()))
							recordIDS.remove(tm.getId());
					}
				}
			});
			return convertView;
		}
		@Override
		public int getCount() {
			return null == listShareFile ? 0 : listShareFile.size();
		}
		@Override
		public Object getItem(int position) {
			return null == listShareFile ? null : listShareFile.get(position);
		}
		@Override
		public long getItemId(int position) {
			return (long) position;
		}
	}
	private class SendTask extends AsyncTask<Void, Void, BaseResultInfoModel> {
		private File file;
		private String fileDescribe;
		private List<ListViewModel> listDevices;
		private StringBuffer toDeviceCodes = new StringBuffer();
		private StringBuffer toGroupCodes = new StringBuffer();
		private int fileType = -1;
		private SendTask(File file, String fileDescribe, List<ListViewModel> listDevices, int fileType) {
			super();
			this.file = file;
			this.fileDescribe = fileDescribe;
			this.listDevices = listDevices;
			this.fileType = fileType;
		}
		@Override
		protected void onPostExecute(BaseResultInfoModel result) {
			super.onPostExecute(result);
			if (result.getReasonCode() == 1) {// 判断网络
				if (result.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {// 分享成功
					try {
						ShareFileModel model = new ShareFileModel();
						model.setFileType(fileType);
						model.setFilePath(
								file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("/") + 1));
						model.setFileName(file.getName());
						model.setFileID(result.getFile_id());
						model.setFileTotalSize(file.length());
						model.setFileDescribe(fileDescribe);
						model.setSendOrReceive(ShareFileModel.SEND_OR_RECEIVE_SEND);
						model.setFromDeviceCode(ShareCommons.device_code);
						model.setTargetDeviceCodes(toDeviceCodes.toString());
						model.setTargetGroupCodes(toGroupCodes.toString());
						model.setFileStatus(ShareFileModel.FILE_STATUS_INIT);
						model.setFileRealSize(0);
						long fileRowID = ShareDataBase.getInstance(context).insertFile(model);
						model.setId((int) fileRowID);
						model = ShareDataBase.getInstance(context).fetchFile(fileRowID);
						UploadManager um = UpDownService.getUploadManager();
						um.startUpload(model);
						ToastUtil.showToastShort(context, getString(R.string.share_project_success));
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					ToastUtil.showToastShort(context, getString(R.string.share_project_failure));
				}
			} else {
				ToastUtil.showToastShort(context, getString(R.string.sys_alarm_neterr));
			}
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected BaseResultInfoModel doInBackground(Void... arg0) {
			for (int i = 0; i < listDevices.size(); i++) {
				ListViewModel m = listDevices.get(i);
				if (m.type == ListViewModel.INFO_DEVIE) {
					toDeviceCodes.append(listDevices.get(i).code + ",");
				} else {
					toGroupCodes.append(listDevices.get(i).code + ",");
				}
			}
			String toD = "";
			if (toDeviceCodes.length() > 0)
				toD = toDeviceCodes.toString();
			String toG = "";
			if (toGroupCodes.length() > 0)
				toG = toGroupCodes.toString();
			BaseResultInfoModel model = ShareHttpRequestUtil.getInstance().send(ShareCommons.device_code, toD, toG,
					fileType + "", file.getName(), file.length() + "", fileDescribe, ShareCommons.session_id);
			if(model.getReasonCode()==1&&model.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_FAILURE)){
				RegisterDeviceLogic.getInstance(context).shareRegister();
				model = ShareHttpRequestUtil.getInstance().send(ShareCommons.device_code, toD, toG,
						fileType + "", file.getName(), file.length() + "", fileDescribe, ShareCommons.session_id);
			}
			return model;
		}
	}
	/***
	 * 历史文件再次分享
	 */
	private class shareFile extends AsyncTask<Void, Void, BaseResultInfoModel> {
		/** 文件记录表主键 **/
		private int id;
		private List<ListViewModel> listDevices;
		private ShareFileModel fileM;
		private StringBuffer toDeviceCodes = new StringBuffer();
		private StringBuffer toGroupCodes = new StringBuffer();
		private shareFile(int id, List<ListViewModel> listDevices) {
			super();
			this.id = id;
			this.listDevices = listDevices;
		}
		@Override
		protected void onPostExecute(BaseResultInfoModel result) {
			super.onPostExecute(result);
			if (result.getReasonCode() == 1) {// 判断网络
				if (result.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {// 分享成功
					ShareFileModel model = new ShareFileModel();
					model.setFileType(fileM.getFileType());
					model.setFilePath(fileM.getFilePath());
					model.setFileName(fileM.getFileName());
					model.setFileID(fileM.getFileID());
					model.setFileTotalSize(fileM.getFileTotalSize());
					model.setFileDescribe(fileM.getFileDescribe());
					model.setSendOrReceive(ShareFileModel.SEND_OR_RECEIVE_SEND);
					model.setFromDeviceCode(ShareCommons.device_code);
					model.setTargetDeviceCodes(toDeviceCodes.toString());
					model.setTargetGroupCodes(toGroupCodes.toString());
					model.setFileStatus(ShareFileModel.FILE_STATUS_FINISH);
					model.setFileRealSize(0);
					long fileRowID = ShareDataBase.getInstance(context).insertFile(model);
					// ToastUtil.showToastShort(context,
					// getString(R.string.share_project_success));
					model.setId((int) fileRowID);
					jumpActivity(WeiMainActivity.class);
				} else {
					ToastUtil.showToastShort(context, getString(R.string.share_project_failure));
				}
			} else {
				ToastUtil.showToastShort(context, getString(R.string.sys_alarm_neterr));
			}
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected BaseResultInfoModel doInBackground(Void... arg0) {
			fileM = ShareDataBase.getInstance(context).fetchFile(id);
			for (int i = 0; i < listDevices.size(); i++) {
				ListViewModel m = listDevices.get(i);
				if (m.type == ListViewModel.INFO_DEVIE) {
					toDeviceCodes.append(listDevices.get(i).code + ",");
				} else {
					toGroupCodes.append(listDevices.get(i).code + ",");
				}
			}
			String toD = "";
			if (toDeviceCodes.length() > 0)
				toD = toDeviceCodes.toString();
			String toG = "";
			if (toGroupCodes.length() > 0)
				toG = toGroupCodes.toString();
			BaseResultInfoModel model = ShareHttpRequestUtil.getInstance().share(ShareCommons.device_code,
					fileM.getFileID(), toD, toG, ShareCommons.session_id);
			if(model.getReasonCode()==1&&model.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_FAILURE)){
				RegisterDeviceLogic.getInstance(context).shareRegister();
				model = ShareHttpRequestUtil.getInstance().share(ShareCommons.device_code,
						fileM.getFileID(), toD, toG, ShareCommons.session_id);
			}
			return model;
		}
	}
}
