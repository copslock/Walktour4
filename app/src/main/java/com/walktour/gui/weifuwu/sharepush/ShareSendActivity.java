package com.walktour.gui.weifuwu.sharepush;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.ServerMessage;
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
import com.walktour.gui.weifuwu.WeiMainActivity;
import com.walktour.gui.weifuwu.business.model.ShareDeviceModel;
import com.walktour.gui.weifuwu.business.model.ShareFileModel;
import com.walktour.gui.weifuwu.business.model.ShareGroupModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.gui.weifuwu.view.refreshlistview.ListViewModel;
import com.walktour.gui.weifuwu.view.refreshlistview.RefreshListViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
/***
 * 分享发送界面,很多地方都会用到
 * 
 * @author weirong.fan
 *
 */
public class ShareSendActivity extends BasicActivity implements OnClickListener {
	private Context context = ShareSendActivity.this;
	/** 说明需要发送的数据来自于哪里 **/
	private int from = -1;
	private boolean isHistory = false;
	/** SD卡目录 **/
	private String sdcard_path = "";
	private RefreshListViewAdapter adapterS; 
	/** 选中的发送设备 **/
	private ArrayList<ListViewModel> listDevices = new ArrayList<ListViewModel>();
	/** 列表中的设备信息 **/
	private List<ListViewModel> listGroup = new LinkedList<ListViewModel>();
	private List<ListViewModel> listDevice = new LinkedList<ListViewModel>();
	/** 列表中的设备信息 **/
	private List<ListViewModel> listDevicesModel = new LinkedList<ListViewModel>();
	private EditText inputTxt;
	private String inputText = "";
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
		try {
			this.setContentView(R.layout.shareprojectsendlayout);
			
			sdcard_path = AppFilePathUtil.getInstance().getSDCardBaseDirectory();
			from = this.getIntent().getIntExtra(ShareCommons.SHARE_FROM_KEY, -1);
			isHistory = this.getIntent().getBooleanExtra("isHistory", false);
			findViewById(R.id.pointer).setOnClickListener(this);
			Button shareBtn = this.initButton(R.id.sharesend);
			this.initTextView(R.id.title_txt).setText(getString(R.string.share_project_share));
			shareBtn.setOnClickListener(this);
			initView();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	private void initView() {
		deviceTv = initTextView(R.id.typedevice);
		deviceTv.setOnClickListener(this);
		groupTv = initTextView(R.id.typegroup);
		groupTv.setOnClickListener(this);
		inputTxt = initEditText(R.id.inputxt);
		inputTxt.addTextChangedListener(textWatcher);
		LinearLayout backlayout = (LinearLayout) this.findViewById(R.id.backlayout);
		ListView receivelayout = (ListView) this.findViewById(R.id.receiveobjlayout);
		adapterS = new RefreshListViewAdapter(context, inputText, backlayout, receivelayout, listDevicesModel,
				listDevices);
		mListView = (ListView) findViewById(R.id.id_listview);
		 
		mListView.setAdapter(adapterS);
		if (from == ShareCommons.SHARE_FROM_PROJECT || from == ShareCommons.SHARE_FROM_CQT)
			isFilterType = true;
		if (from == ShareCommons.SHARE_FROM_SHARE){//历史文件再次分享
			int id = this.getIntent().getIntExtra("ID", -1);
			ShareFileModel  fileM = ShareDataBase.getInstance(context).fetchFile(id);
			if(fileM.getFileType()==ShareFileModel.FILETYPE_CQT||fileM.getFileType()==ShareFileModel.FILETYPE_PROJECT){
				isFilterType = true;
			}
		}
			
		// 刷新数据
		new FetchDevice().execute();
	}
	 
	@Override
	public void onClick(View v) {
		try {
			switch (v.getId()) {
			case R.id.sharesend:
				if (appModel.isTestJobIsRun()) {
					ToastUtil.showToastShort(context, R.string.exe_info);
					return;
				}
				if (listDevices.size() <= 0) {
					ToastUtil.showToastShort(context, R.string.share_project_share_info_obj);
					return;
				}
				switch (from) {
				case ShareCommons.SHARE_FROM_PROJECT:// 共享工程
					shareProject();
					break;
				case ShareCommons.SHARE_FROM_REPORT:// 共享报表
					shareReport();
					break;
				case ShareCommons.SHARE_FROM_SCREENSHOT_PIC:// 截屏分享
					shareSceenShotPic();
					break;
				case ShareCommons.SHARE_FROM_CQT_PIC:// 共享CQT室内地图
					shareCQTPic();
					break;
				case ShareCommons.SHARE_FROM_CQT:// 共享CQT
					shareCQT();
					break;
				case ShareCommons.SHARE_FROM_DATA:// 数据管理
					shareDATA();
					break;
				case ShareCommons.SHARE_FROM_STATION:// 共享基站
					shareStation();
					break;
				case ShareCommons.SHARE_FROM_SHARE:// 历史文件再次分享
					int id = this.getIntent().getIntExtra("ID", -1);
					new shareFile(id, listDevices).execute();
					break;
				}
				finish();
				break;
			case R.id.pointer:
				finish();
				break;
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
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			ToastUtil.showToastShort(context, "出现异常2");
		}
	}
	/***
	 * 共享基站
	 */
	private void shareStation() {
		try {
			ArrayList<String> fileS = this.getIntent().getStringArrayListExtra("projects");
			if (isHistory) {
				List<Integer> listIDS = new LinkedList<Integer>();
				for (String s : fileS) {
					listIDS.add(Integer.parseInt(s));
				}
				List<ShareFileModel> lx = ShareDataBase.getInstance(context).fetchAllFilesByIDS(listIDS);
				for (ShareFileModel sm : lx) {
					// 发送历史直接转发即可
					new shareFile(sm.getId(), listDevices).execute();
				}
			} else {
				// 需要压缩的文件,文件全路径名
				for (int i = 0; i < fileS.size(); i++) {
					File f = new File(fileS.get(i));
					String fileAbsolutePath = ShareCommons.SHARE_PATH_STATION + (System.currentTimeMillis() + i)
							+ ".zip";
					File zipF = new File(fileAbsolutePath);
					ZipUtil.zip(f, zipF);
					// 发送共享
					new SendProject(zipF, f.getName().substring(0, f.getName().lastIndexOf(".")), listDevices,
							ShareFileModel.FILETYPE_STATION).execute();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/***
	 * 数据管理 共享文件没有历史
	 */
	private void shareDATA() {
		try {
			// 启动服务执行操作,防止压缩时间过长
			ArrayList<String> recordIDS = this.getIntent().getStringArrayListExtra("projects");
			Intent i = new Intent();
			i.setAction(ServerMessage.ACTION_SHARE_SEND_CONTROL_DATA);
			i.putStringArrayListExtra("recordIDS", recordIDS);
			i.putParcelableArrayListExtra("listDevices", listDevices);
			this.sendBroadcast(i);
			Intent intentx = new Intent(this, WeiMainActivity.class);
			startActivity(intentx);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void shareCQT() {
		try {
			// 需要压缩的文件夹
			ArrayList<String> cqts = this.getIntent().getStringArrayListExtra("cqts");
			if (isHistory) {
				List<Integer> listIDS = new LinkedList<Integer>();
				for (String s : cqts) {
					listIDS.add(Integer.parseInt(s));
				}
				List<ShareFileModel> lx = ShareDataBase.getInstance(context).fetchAllFilesByIDS(listIDS);
				for (ShareFileModel sm : lx) {
					// 发送历史直接转发即可
					new shareFile(sm.getId(), listDevices).execute();
				}
			} else {
				// CQT楼层关系路径
				String basePath = AppFilePathUtil.getInstance().getSDCardBaseDirectory(getString(R.string.path_data),getString(R.string.path_indoortest));
				File dirF = new File(basePath);
				File[] files = dirF.listFiles();
				// 需要过滤的文件夹
				List<String> filterHolder = new LinkedList<String>();
				if (null != files && files.length > 0) {
					for (int i = 0; i < files.length; i++) {
						if (!cqts.contains(files[i].getName())) {
							filterHolder.add(files[i].getName());
						}
					}
				}
				// 需要过滤的文件类型
				List<String> filterFileType = new LinkedList<String>();
				filterFileType.add("ddib");
				filterFileType.add("rcu");
				filterFileType.add("lte.dgz");
				// 压缩的文件夹
				String fileAbsolutePath = ShareCommons.SHARE_PATH_CQT + System.currentTimeMillis() + ".zip";
				ZipUtil.zipShareFile(basePath, fileAbsolutePath, false, filterHolder, filterFileType, null);
				// 历史目录只保留5个文件
				// 发送共享
				StringBuffer sb = new StringBuffer();
				for (String fs : cqts) {
					sb.append(fs + "_");
				}
				new SendProject(new File(fileAbsolutePath), sb.toString().substring(0, sb.toString().length() - 1),
						listDevices, ShareFileModel.FILETYPE_CQT).execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void shareCQTPic() {
		if (ShareCommons.CURRENT_CQT_PIC_NAME.equals("")) {
			ToastUtil.showToastShort(context, R.string.share_project_share_info_info);
			return;
		}
		File filepic = new File(ShareCommons.CURRENT_CQT_PIC_NAME);
		try {
			new SendProject(filepic, filepic.getName(), listDevices, ShareFileModel.FILETYPE_CQI_PIC).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void shareSceenShotPic() {
		if (ShareCommons.CURRENT_SCREEN_SHOT_PIC_NAME.equals("")) {
			ToastUtil.showToastShort(context, R.string.share_project_share_info_info);
			return;
		}
		File filepic = new File(ShareCommons.CURRENT_SCREEN_SHOT_PIC_NAME);
		try {
			new SendProject(filepic, filepic.getName(), listDevices, ShareFileModel.FILETYPE_PIC_SCREENSHOT).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	/****
	 * 1-共享工程文件
	 * 
	 * @throws Exception
	 */
	private void shareProject() throws Exception {
		ArrayList<String> projects = this.getIntent().getStringArrayListExtra("projects");
		if (isHistory) {
			List<ShareFileModel> lx = ShareDataBase.getInstance(context)
					.fetchFilesHistory(ShareFileModel.FILETYPE_PROJECT);
			Iterator<ShareFileModel> iter = lx.iterator();
			while (iter.hasNext()) {
				ShareFileModel f = iter.next();
				if (!projects.contains(f.getFilePath() + f.getFileName())) {
					iter.remove();
				}
			}
			for (ShareFileModel sm : lx) {
				// 发送历史直接转发即可
				new shareFile(sm.getId(), listDevices).execute();
			}
		} else {
			if (null != projects && projects.size() > 0) {
				for (String s : projects) {
					File zipF = AppFilePathUtil.getInstance().createSDCardBaseFile(ShareCommons.SHARE_PATH_BASE, ShareCommons.SHARE_PATH_PROJECT, System.currentTimeMillis() + ".zip");
					File srcF = new File(s);
					ZipUtil.zip(srcF, zipF);
					new SendProject(zipF, srcF.getName().substring(0, srcF.getName().length() - 4), listDevices,
							ShareFileModel.FILETYPE_PROJECT).execute();
				}
			}
		}
	}
	/****
	 * 1-共享报表文件
	 */
	private void shareReport() throws Exception {
		ArrayList<String> projects = this.getIntent().getStringArrayListExtra("projects");
		if (isHistory) {
			List<ShareFileModel> lx = ShareDataBase.getInstance(context)
					.fetchFilesHistory(ShareFileModel.FILETYPE_REPORT);
			Iterator<ShareFileModel> iter = lx.iterator();
			while (iter.hasNext()) {
				ShareFileModel f = iter.next();
				if (!projects.contains(f.getFilePath() + f.getFileName())) {
					iter.remove();
				}
			}
			for (ShareFileModel sm : lx) {
				// 发送历史直接转发即可
				new shareFile(sm.getId(), listDevices).execute();
			}
		} else {
			if (null != projects && projects.size() > 0) {
				for (String s : projects) {
					File zipF = new File(ShareCommons.SHARE_PATH_REPORT + System.currentTimeMillis() + ".zip");
					File srcF = new File(s);
					ZipUtil.zip(srcF, zipF);
					new SendProject(zipF, srcF.getName().substring(0, srcF.getName().length() - 4), listDevices,
							ShareFileModel.FILETYPE_REPORT).execute();
				}
			}
		}
	}
	private class SendProject extends AsyncTask<Void, Void, BaseResultInfoModel> {
		private File file;
		private String fileDescribe;
		private List<ListViewModel> listDevices;
		private StringBuffer toDeviceCodes = new StringBuffer();
		private StringBuffer toGroupCodes = new StringBuffer();
		/** 共享的文件类型 **/
		private int fileType = -1;
		/** 发送给组 **/
		private String toG = "";
		private SendProject(File file, String fileDescribe, List<ListViewModel> listDevices, int fileType) {
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
						model.setFileStatus(ShareFileModel.FILE_STATUS_START);
						model.setFileRealSize(0);
						long fileRowID = ShareDataBase.getInstance(context).insertFile(model);
						model.setId((int) fileRowID);
						model = ShareDataBase.getInstance(context).fetchFile(fileRowID);
						UploadManager um = UpDownService.getUploadManager();
						um.startUpload(model);
						if (!isFilterType) {
							ToastUtil.showToastShort(context, getString(R.string.share_project_success));
						} else {
							if (toG.equals("")) {
								ToastUtil.showToastShort(context, getString(R.string.share_project_success));
							} else {
								boolean flag = false;
								// 检索组内是否有ios设备
								String[] arrs = toG.substring(0, toG.length() - 1).split(",");
								List<ShareDeviceModel> lists = ShareDataBase.getInstance(context)
										.fetDeviceByGroupCode(Arrays.asList(arrs));
								for (ShareDeviceModel sm : lists) {
									if (sm.getDeviceOS() == ShareDeviceModel.OS_IOS) {
										flag = true;
										break;
									}
								}
								if (flag) {
									ToastUtil.showToastShort(context,
											getString(R.string.share_project_devices_group_ifno));
								} else {
									ToastUtil.showToastShort(context, getString(R.string.share_project_success));
								}
							}
						}
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
			if (toGroupCodes.length() > 0)
				toG = toGroupCodes.toString();
			BaseResultInfoModel model = ShareHttpRequestUtil.getInstance().send(ShareCommons.device_code, toD, toG, fileType + "",
					file.getName(), file.length() + "", fileDescribe,ShareCommons.session_id);
			if(model.getReasonCode()==1&&model.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_FAILURE)){
				RegisterDeviceLogic.getInstance(context).shareRegister();
				model = ShareHttpRequestUtil.getInstance().send(ShareCommons.device_code, toD, toG, fileType + "",
						file.getName(), file.length() + "", fileDescribe,ShareCommons.session_id);
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
		private String toG = "";
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
					//转发存心的fileID更好
					model.setFileID(result.getFile_id().length()>0?result.getFile_id():fileM.getFileID());
					model.setFileTotalSize(fileM.getFileTotalSize());
					model.setFileDescribe(fileM.getFileDescribe());
					model.setSendOrReceive(ShareFileModel.SEND_OR_RECEIVE_SEND);
					model.setFromDeviceCode(ShareCommons.device_code);
					model.setTargetDeviceCodes(toDeviceCodes.toString());
					model.setTargetGroupCodes(toGroupCodes.toString());
					model.setFileStatus(ShareFileModel.FILE_STATUS_FINISH);
					model.setFileRealSize(0);
					long fileRowID = ShareDataBase.getInstance(context).insertFile(model);
					model.setId((int) fileRowID);
					if (from == ShareCommons.SHARE_FROM_SHARE) {
						// 返回到当前界面
						finish();
						if (!isFilterType) {
							ToastUtil.showToastShort(context, getString(R.string.share_project_success));
						} else {
							if (toG.equals("")) {
								ToastUtil.showToastShort(context, getString(R.string.share_project_success));
							} else {
								try {
									boolean flag = false;
									// 检索组内是否有ios设备
									String[] arrs = toG.substring(0, toG.length() - 1).split(",");
									List<ShareDeviceModel> lists;
									lists = ShareDataBase.getInstance(context)
											.fetDeviceByGroupCode(Arrays.asList(arrs));
									for (ShareDeviceModel sm : lists) {
										if (sm.getDeviceOS() == ShareDeviceModel.OS_IOS) {
											flag = true;
											break;
										}
									}
									if (flag) {
										ToastUtil.showToastShort(context,
												getString(R.string.share_project_devices_group_ifno));
									} else {
										ToastUtil.showToastShort(context, getString(R.string.share_project_success));
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					} else {
						jumpActivity(WeiMainActivity.class);
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
			if (toGroupCodes.length() > 0)
				toG = toGroupCodes.toString();
			BaseResultInfoModel model = ShareHttpRequestUtil.getInstance().share(ShareCommons.device_code, fileM.getFileID(), toD,
					toG,ShareCommons.session_id);
			if(model.getReasonCode()==1&&model.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_FAILURE)){
				RegisterDeviceLogic.getInstance(context).shareRegister();
				model = ShareHttpRequestUtil.getInstance().share(ShareCommons.device_code, fileM.getFileID(), toD,
						toG,ShareCommons.session_id);
			}
			return model;
		}
	}
}
