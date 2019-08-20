package com.walktour.gui.weifuwu.sharepush;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.dinglicom.data.control.BuildWhere;
import com.dinglicom.data.control.DataTableStruct.DataTableMap;
import com.dinglicom.data.control.DataTableStruct.TestRecordEnum;
import com.dinglicom.data.model.TestRecord;
import com.walktour.Utils.ToastUtil;
import com.walktour.control.config.ConfigIndoor;
import com.walktour.control.config.ProjectManager;
import com.walktour.control.instance.FileDB;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.data.model.ContentModel;
import com.walktour.gui.data.model.DBManager;
import com.walktour.gui.weifuwu.business.model.ShareFileModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.model.BuildingModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/***
 * 分享下一步界面,很多地方都会用到
 * 
 * @author weirong.fan
 *
 */
public class ShareNextActivity extends BasicActivity {
	private Context context = ShareNextActivity.this;
	private ProjectManager proManager = null;
	private List<File> projectFileList = null;
	/** 来自于哪个界面 */
	private int from = -1;
	private LayoutInflater layoutInflater;
	private ConfigIndoor config;
	private List<Map<String, Object>> listItemMap;
	/** 选择的工程文件容器 **/
	private ArrayList<String> selectFiles = new ArrayList<String>();
	private ArrayList<String> fileDescribes = new ArrayList<String>();
	/** 分享记录表中的记录主键ID ***/
	public List<Integer> recordIDS = new LinkedList<Integer>();
	/** 基站信息 ***/
	private ArrayList<File> stations = new ArrayList<File>();
	/** 数据管理的数据记录 **/
	private ArrayList<TestRecord> dataRecords = new ArrayList<TestRecord>();
//	private String sdcard_path = "";
	// 是否是历史
	private boolean isHistory = false;
	private BaseAdapter adapter;
	private ListView listView;
	private Button historyBtn;
	private TextView infoTV;
	private String dataName = "";
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.shareprojectnextlayout);
//		sdcard_path = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.path_approot);
		selectFiles.clear();
		from = this.getIntent().getIntExtra(ShareCommons.SHARE_FROM_KEY, -1);
		findViewById(R.id.pointer).setOnClickListener(this);
		historyBtn = initButton(R.id.historybtn);
		historyBtn.setOnClickListener(this);
		Button shareBtn = this.initButton(R.id.sharesend);
		shareBtn.setText(getString(R.string.share_project_next));
		shareBtn.setBackgroundDrawable(null);
		LayoutParams pps = shareBtn.getLayoutParams();
		pps.width = LayoutParams.WRAP_CONTENT;
		shareBtn.setLayoutParams(pps);
		shareBtn.setOnClickListener(this);
		layoutInflater = LayoutInflater.from(context);
		this.config = ConfigIndoor.getInstance(this);
		initViews();
	}
	private void initViews() {
		initHeader();
	}
	private void initHeader() {
		this.initTextView(R.id.title_txt).setText(getString(R.string.share_project_share));
		infoTV = this.initTextView(R.id.infoid);
		listView = (ListView) this.findViewById(R.id.shareobj);
		switch (from) {
		case ShareCommons.SHARE_FROM_PROJECT:
			infoTV.setText(getString(R.string.share_project_select_file));
			proManager = new ProjectManager(this);
			projectFileList = proManager.getProjectList();
			adapter = new ProjectAdapter();
			listView.setAdapter(adapter);
			break;
		case ShareCommons.SHARE_FROM_CQT:
			infoTV.setText(getString(R.string.share_project_select_cqt));
			initCQT();
			adapter = new CQTAdapter();
			listView.setAdapter(adapter);
			break;
		case ShareCommons.SHARE_FROM_STATION:
			infoTV.setText(getString(R.string.share_project_select_station));
			initStation();
			adapter = new StationAdapter();
			listView.setAdapter(adapter);
			break;
		case ShareCommons.SHARE_FROM_DATA:
			initData();
			adapter = new DataAdapter();
			listView.setAdapter(adapter);
			break;
		case ShareCommons.SHARE_FROM_REPORT:
			infoTV.setText(getString(R.string.share_project_devices_release_relation_15));
			projectFileList = ShareCommons.getReportFiles(this);
			adapter = new ReportAdapter();
			listView.setAdapter(adapter);
			break;
		}
	}
	private void initData() {
		int select = this.getIntent().getIntExtra("data_tab_select", 0);
		ContentModel com = DBManager.getInstance(this).getContentModels(-1).get(select);
		dataName = com.getName();
		infoTV.setText(String.format(getString(R.string.share_project_data_title), dataName));
		BuildWhere wheres = new BuildWhere();
		wheres.addWhere(DataTableMap.TestRecord.name(),
				"%s." + TestRecordEnum.test_type.name() + " = " + com.getKey() + "");
		dataRecords = FileDB.getInstance(context).buildTestRecordList(wheres.getWhere());
	}
	private void initStation() {
		stations.clear();
		String sd_current_path = this.getIntent().getStringExtra("station_path");
//		ToastUtil.showToastShort(context, sd_current_path + "");
		File[] files = new File(sd_current_path).listFiles();
		if (null != files && files.length > 0) {
			for (File ff : files) {
				if (ff.isDirectory())
					continue;
				if (ff.getName().endsWith(".txt")) {
					stations.add(ff);
				}
			}
		}
	}
	private void initCQT() {
		List<BuildingModel> buildingList = config.getBuildings(this,false);
		if (null != listItemMap) {
			listItemMap.clear();
			listItemMap = null;
		}
		listItemMap = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < buildingList.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			StringBuilder sb = new StringBuilder();
			if (buildingList.get(i).getBuildAddress().indexOf("_") > -1) {
				String[] strs = buildingList.get(i).getBuildAddress().split("_");
				for (int j = 0; j < strs.length; j++) {
					sb.append(strs[j]);
				}
			}
			map.put("ItemImage", R.drawable.list_item_indoor);// 图像资源的ID
			map.put("ItemTitle", buildingList.get(i).getName());
			map.put("ItemAddress", sb.toString());
			map.put("ItemText", getString(R.string.sys_indoor_floors) + buildingList.get(i).getCounts());
			map.put("ItemCheckble", false);
			listItemMap.add(map);
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sharesend:
			if (selectFiles.size() <= 0) {
				ToastUtil.showToastShort(context, getString(R.string.share_project_share_info_info));
				return;
			}
			Bundle bundle = new Bundle();
			bundle.putInt(ShareCommons.SHARE_FROM_KEY, from);
			switch (from) {
			case ShareCommons.SHARE_FROM_PROJECT:
				bundle.putStringArrayList("projects", selectFiles);
				break;
			case ShareCommons.SHARE_FROM_REPORT:// 报表
				bundle.putStringArrayList("projects", selectFiles);
				break;
			case ShareCommons.SHARE_FROM_CQT:// 共享CQT信息
				bundle.putStringArrayList("cqts", selectFiles);
			case ShareCommons.SHARE_FROM_DATA:// 共享数据
				bundle.putStringArrayList("projects", selectFiles);
			case ShareCommons.SHARE_FROM_STATION:// 共享基站
				bundle.putStringArrayList("projects", selectFiles);
			}
			bundle.putBoolean("isHistory", isHistory);
			this.jumpActivity(ShareSendActivity.class, bundle);
			break;
		case R.id.historybtn:
			isHistory = !isHistory;
			selectFiles.clear();
			if (isHistory) {
				infoTV.setText(getString(R.string.share_project_select_history));
			}
			try {
				if (null != projectFileList)
					projectFileList.clear();
				if (null != fileDescribes)
					fileDescribes.clear();
				switch (from) {
				case ShareCommons.SHARE_FROM_PROJECT:// 工程
					if (isHistory) {// 历史数据
						List<ShareFileModel> lx = ShareDataBase.getInstance(context)
								.fetchFilesHistory(ShareFileModel.FILETYPE_PROJECT);
						for (ShareFileModel sm : lx) {
							projectFileList.add(new File(sm.getFilePath() + sm.getFileName()));
							fileDescribes.add(sm.getFileDescribe());
						}
					} else {
						infoTV.setText(getString(R.string.share_project_select_file));
						projectFileList = proManager.getProjectList();
					}
					break;
				case ShareCommons.SHARE_FROM_REPORT:// 报表
					recordIDS.clear();
					if (isHistory) {// 历史数据
						List<ShareFileModel> lx = ShareDataBase.getInstance(context)
								.fetchFilesHistory(ShareFileModel.FILETYPE_REPORT);
						for (ShareFileModel sm : lx) {
							stations.add(new File(sm.getFilePath() + sm.getFileName()));
							fileDescribes.add(sm.getFileDescribe());
							recordIDS.add(sm.getId());
						}
					} else {
						infoTV.setText(R.string.share_project_devices_release_relation_15);
						projectFileList = ShareCommons.getReportFiles(this);
					}
					break;
				case ShareCommons.SHARE_FROM_STATION:// 基站
					stations.clear();
					recordIDS.clear();
					if (isHistory) {// 历史数据
						List<ShareFileModel> lx = ShareDataBase.getInstance(context)
								.fetchFilesHistory(ShareFileModel.FILETYPE_STATION);
						for (ShareFileModel sm : lx) {
							stations.add(new File(sm.getFilePath() + sm.getFileName()));
							fileDescribes.add(sm.getFileDescribe());
							recordIDS.add(sm.getId());
						}
					} else {
						infoTV.setText(getString(R.string.share_project_select_station));
						initStation();
					}
					break;
				case ShareCommons.SHARE_FROM_CQT:// CQT
					listItemMap.clear();
					if (isHistory) {// 历史数据
						List<ShareFileModel> lx = ShareDataBase.getInstance(context)
								.fetchFilesHistory(ShareFileModel.FILETYPE_CQT);
						for (ShareFileModel sm : lx) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("ItemID", sm.getId());
							map.put("ItemTitle", sm.getFileName());
							map.put("ItemDescribe", sm.getFileDescribe());
							listItemMap.add(map);
						}
					} else {
						infoTV.setText(getString(R.string.share_project_select_cqt));
						initCQT();
					}
					break;
				case ShareCommons.SHARE_FROM_DATA:// 数据管理
					stations.clear();
					recordIDS.clear();
					if (isHistory) {// 历史数据
						if (null == dataRecords) {
							dataRecords = new ArrayList<TestRecord>();
						} else {
							dataRecords.clear();
						}
						List<ShareFileModel> lx = ShareDataBase.getInstance(context)
								.fetchFilesHistory(ShareFileModel.FILETYPE_DATA);
						for (ShareFileModel sm : lx) {
							TestRecord tr = new TestRecord();
							tr.file_name = sm.getFileName();
							dataRecords.add(tr);
							fileDescribes.add(sm.getFileDescribe());
							recordIDS.add(sm.getId());
						}
					} else {
						infoTV.setText(String.format(getString(R.string.share_project_data_title), dataName));
						initData();
					}
					break;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				ToastUtil.showToastShort(context, "出现错误");
			}
			adapter.notifyDataSetChanged();
			listView.invalidate();
			break;
		case R.id.pointer:
			finish();
			break;
		}
	}
	/**
	 * 工程项目列表适配器
	 * 
	 * @author weirong.fan
	 * 
	 */
	private class ProjectAdapter extends BaseAdapter {
		private String name = "";
		public ProjectAdapter() {
			super();
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.shareprojectlayout_item, parent, false);
			}
			TextView projectName = (TextView) convertView.findViewById(R.id.projectname);
			TextView describe = (TextView) convertView.findViewById(R.id.describe);
			final CheckBox projectID = (CheckBox) convertView.findViewById(R.id.projectid);
			projectID.setChecked(false);
			final File f = projectFileList.get(position);
			if (isHistory) {
				describe.setVisibility(View.VISIBLE);
				name = f.getName();
				describe.setText(fileDescribes.get(position) + "");
			} else {
				describe.setVisibility(View.GONE);
				if (f.getName().contains(".xml")) {
					name = f.getName().substring(0, f.getName().lastIndexOf(".xml"));
				} else {
					name = f.getName();
				}
			}
			projectName.setText(name + "");
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (projectID.isChecked()) {
						projectID.setChecked(false);
						if (selectFiles.contains(f.getAbsolutePath())) {
							selectFiles.remove(f.getAbsolutePath());
						}
					} else {
						projectID.setChecked(true);
						if (!selectFiles.contains(f.getAbsolutePath())) {
							selectFiles.add(f.getAbsolutePath());
						}
					}
				}
			});
			projectID.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						if (!selectFiles.contains(f.getAbsolutePath())) {
							selectFiles.add(f.getAbsolutePath());
						}
					} else {
						if (selectFiles.contains(f.getAbsolutePath())) {
							selectFiles.remove(f.getAbsolutePath());
						}
					}
				}
			});
			return convertView;
		}
		@Override
		public int getCount() {
			return projectFileList.size();
		}
		@Override
		public Object getItem(int position) {
			return projectFileList.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
	}
	/**
	 * CQT列表适配器
	 * 
	 * @author weirong.fan
	 * 
	 */
	private class CQTAdapter extends BaseAdapter {
		public CQTAdapter() {
			super();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.shareprojectlayout_item, parent, false);
			}
			TextView projectName = (TextView) convertView.findViewById(R.id.projectname);
			TextView describe = (TextView) convertView.findViewById(R.id.describe);
			final CheckBox projectID = (CheckBox) convertView.findViewById(R.id.projectid);
			projectID.setChecked(false);
			final Map<String, Object> map = listItemMap.get(position);
			projectName.setText(map.get("ItemTitle") + "");
			if (isHistory) {
				describe.setVisibility(View.VISIBLE);
				describe.setText(map.get("ItemDescribe") + "");
			} else {
				describe.setVisibility(View.GONE);
			}
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (projectID.isChecked()) {
						projectID.setChecked(false);
						if (isHistory) {
							if (selectFiles.contains(map.get("ItemID") + ""))
								selectFiles.remove(map.get("ItemID") + "");
						} else {
							if (selectFiles.contains(map.get("ItemTitle") + ""))
								selectFiles.remove(map.get("ItemTitle") + "");
						}
					} else {
						projectID.setChecked(true);
						if (isHistory) {
							if (!selectFiles.contains(map.get("ItemID") + ""))
								selectFiles.add(map.get("ItemID") + "");
						} else {
							if (!selectFiles.contains(map.get("ItemTitle") + ""))
								selectFiles.add(map.get("ItemTitle") + "");
						}
					}
				}
			});
			projectID.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						if (isHistory) {
							if (!selectFiles.contains(map.get("ItemID") + ""))
								selectFiles.add(map.get("ItemID") + "");
						} else {
							if (!selectFiles.contains(map.get("ItemTitle") + ""))
								selectFiles.add(map.get("ItemTitle") + "");
						}
					} else {
						if (isHistory) {
							if (selectFiles.contains(map.get("ItemID") + ""))
								selectFiles.remove(map.get("ItemID") + "");
						} else {
							if (selectFiles.contains(map.get("ItemTitle") + ""))
								selectFiles.remove(map.get("ItemTitle") + "");
						}
					}
				}
			});
			return convertView;
		}
		@Override
		public int getCount() {
			return listItemMap.size();
		}
		@Override
		public Object getItem(int position) {
			return listItemMap.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
	}
	/**
	 * 报表列表适配器
	 * 
	 * @author weirong.fan
	 * 
	 */
	private class ReportAdapter extends BaseAdapter {
		private String name = "";
		public ReportAdapter() {
			super();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.shareprojectlayout_item, parent, false);
			}
			TextView projectName = (TextView) convertView.findViewById(R.id.projectname);
			TextView describe = (TextView) convertView.findViewById(R.id.describe);
			final CheckBox projectID = (CheckBox) convertView.findViewById(R.id.projectid);
			final File f = projectFileList.get(position);
			if (isHistory) {
				describe.setVisibility(View.VISIBLE);
				name = f.getName();
				describe.setText(fileDescribes.get(position) + "");
			} else {
				describe.setVisibility(View.GONE);
				name = f.getName();
			}
			projectName.setText(name + "");
			projectID.setChecked(false);
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (projectID.isChecked()) {
						projectID.setChecked(false);
						if (selectFiles.contains(f.getAbsolutePath()))
							selectFiles.remove(f.getAbsolutePath());
					} else {
						projectID.setChecked(true);
						if (!selectFiles.contains(f.getAbsolutePath()))
							selectFiles.add(f.getAbsolutePath());
					}
				}
			});
			projectID.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// 如果已经选择
					if (isChecked) {
						if (!selectFiles.contains(f.getAbsolutePath()))
							selectFiles.add(f.getAbsolutePath());
					} else {
						if (selectFiles.contains(f.getAbsolutePath()))
							selectFiles.remove(f.getAbsolutePath());
					}
				}
			});
			return convertView;
		}
		@Override
		public int getCount() {
			return projectFileList.size();
		}
		@Override
		public Object getItem(int position) {
			return projectFileList.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
	}
	/**
	 * 报表列表适配器
	 * 
	 * @author weirong.fan
	 * 
	 */
	private class DataAdapter extends BaseAdapter {
		private String name = "";
		public DataAdapter() {
			super();
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.shareprojectlayout_item, parent, false);
			}
			TextView projectName = (TextView) convertView.findViewById(R.id.projectname);
			TextView describe = (TextView) convertView.findViewById(R.id.describe);
			final CheckBox projectID = (CheckBox) convertView.findViewById(R.id.projectid);
			projectID.setChecked(false);
			final TestRecord record = dataRecords.get(position);
			if (isHistory) {
				describe.setVisibility(View.VISIBLE);
				name = record.file_name;
				describe.setText(fileDescribes.get(position) + "");
			} else {
				describe.setVisibility(View.GONE);
				name = record.file_name;
			}
			projectName.setText(name + "");
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (projectID.isChecked()) {
						projectID.setChecked(false);
						if (isHistory) {
							if (selectFiles.contains(recordIDS.get(position) + ""))
								selectFiles.remove(recordIDS.get(position) + "");
						} else {
							if (selectFiles.contains(record.record_id + ""))
								selectFiles.remove(record.record_id + "");
						}
					} else {
						projectID.setChecked(true);
						if (isHistory) {
							if (!selectFiles.contains(recordIDS.get(position) + ""))
								selectFiles.add(recordIDS.get(position) + "");
						} else {
							if (!selectFiles.contains(record.record_id + ""))
								selectFiles.add(record.record_id + "");
						}
					}
				}
			});
			projectID.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// 如果已经选择
					if (isChecked) {
						if (isHistory) {
							if (!selectFiles.contains(recordIDS.get(position) + ""))
								selectFiles.add(recordIDS.get(position) + "");
						} else {
							if (!selectFiles.contains(record.record_id + ""))
								selectFiles.add(record.record_id + "");
						}
					} else {
						if (isHistory) {
							if (selectFiles.contains(recordIDS.get(position) + ""))
								selectFiles.remove(recordIDS.get(position) + "");
						} else {
							if (selectFiles.contains(record.record_id + ""))
								selectFiles.remove(record.record_id + "");
						}
					}
				}
			});
			return convertView;
		}
		@Override
		public int getCount() {
			return dataRecords.size();
		}
		@Override
		public Object getItem(int position) {
			return dataRecords.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
	}
	/**
	 * 报表列表适配器
	 * 
	 * @author weirong.fan
	 * 
	 */
	private class StationAdapter extends BaseAdapter {
		private String name = "";
		public StationAdapter() {
			super();
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.shareprojectlayout_item, parent, false);
			}
			TextView projectName = (TextView) convertView.findViewById(R.id.projectname);
			TextView describe = (TextView) convertView.findViewById(R.id.describe);
			final CheckBox projectID = (CheckBox) convertView.findViewById(R.id.projectid);
			projectID.setChecked(false);
			final File fx = stations.get(position);
			if (isHistory) {
				describe.setVisibility(View.VISIBLE);
				name = fx.getName();
				describe.setText(fileDescribes.get(position) + "");
			} else {
				describe.setVisibility(View.GONE);
				name = fx.getName();
			}
			projectName.setText(name + "");
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (projectID.isChecked()) {
						projectID.setChecked(false);
						if (isHistory) {
							if (selectFiles.contains(recordIDS.get(position) + ""))
								selectFiles.remove(recordIDS.get(position) + "");
						} else {
							if (selectFiles.contains(fx.getAbsolutePath() + ""))
								selectFiles.remove(fx.getAbsolutePath() + "");
						}
					} else {
						projectID.setChecked(true);
						if (isHistory) {
							if (!selectFiles.contains(recordIDS.get(position) + ""))
								selectFiles.add(recordIDS.get(position) + "");
						} else {
							if (!selectFiles.contains(fx.getAbsolutePath() + ""))
								selectFiles.add(fx.getAbsolutePath() + "");
						}
					}
				}
			});
			projectID.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// 如果已经选择
					if (isChecked) {
						if (isHistory) {
							if (!selectFiles.contains(recordIDS.get(position) + ""))
								selectFiles.add(recordIDS.get(position) + "");
						} else {
							if (!selectFiles.contains(fx.getAbsolutePath() + ""))
								selectFiles.add(fx.getAbsolutePath() + "");
						}
					} else {
						if (isHistory) {
							if (selectFiles.contains(recordIDS.get(position) + ""))
								selectFiles.remove(recordIDS.get(position) + "");
						} else {
							if (selectFiles.contains(fx.getAbsolutePath() + ""))
								selectFiles.remove(fx.getAbsolutePath() + "");
						}
					}
				}
			});
			return convertView;
		}
		@Override
		public int getCount() {
			return stations.size();
		}
		@Override
		public Object getItem(int position) {
			return stations.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
	}
}
