package com.walktour.gui.data;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.dinglicom.data.control.DataTableStruct;
import com.dinglicom.data.model.RecordDetail;
import com.dinglicom.data.model.RecordTestInfo;
import com.dinglicom.data.model.TestRecord;
import com.dinglicom.totalreport.DLMessageModel;
import com.dinglicom.totalreport.RequestSceneXMl;
import com.dinglicom.totalreport.SubDlMessageItem;
import com.dinglicom.totalreport.TotalDetailActivity;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.DateUtil;
import com.walktour.Utils.DateUtils;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.Utils.WalkStruct.ServerOperateType;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.control.VoLTEFaildAnalyse;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ServerManager;
import com.walktour.control.instance.DataManagerFileList;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.treeview.TreeNode;
import com.walktour.framework.view.viewpagerindicator.TabPageIndicator;
import com.walktour.gui.InfoTabHost;
import com.walktour.gui.R;
import com.walktour.gui.analysis.AnalysisDetailActivity;
import com.walktour.gui.analysis.commons.AnalysisCommons;
import com.walktour.gui.applet.ControlBar;
import com.walktour.gui.data.dialog.PopButton;
import com.walktour.gui.data.dialog.PopButton.ClickListener;
import com.walktour.gui.data.model.DBManager;
import com.walktour.gui.data.model.DataModel;
import com.walktour.gui.highspeedrail.HighSpeedRailCommons;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.newmap2.NewInfoTabActivity;
import com.walktour.gui.replayfloatview.FloatWindowManager;
import com.walktour.gui.setting.sysroutine.SysRoutineActivity;
import com.walktour.gui.weifuwu.ShareDialogActivity;
import com.walktour.gui.weifuwu.business.model.ShareFileModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;
import com.walktour.gui.weifuwu.sharepush.ShareSendActivity;
import com.walktour.gui.weifuwu.view.BadgeView;
import com.walktour.service.app.DataTransService;
import com.walktour.service.app.datatrans.model.UploadFileModel;
import com.walktour.service.innsmap.InnsmapFactory;
import com.walktour.service.metro.MetroFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/***
 * 数据管理
 * 
 * @author weirong.fan
 *
 */
@SuppressWarnings("deprecation")
public class FileManagerFragmentActivity extends FragmentActivity implements OnClickListener {

	public static final String KEY_RESULT = "isResultBack";
	public static final String KEY_FILELIST = "fileList";
	/** 统计 */
	public static final String KEY_TOTAL_MODE = "total_mode";
	public static final String KEY_ISREPORT = "key_isreport"; // 区分是统计还是报表选择
	public static final String KEY_TOTAL_RUN = "";
	public static final String KEY_FILEMANAGER_TO_TOTALDETAIL = "";//是否从数据管理跳到统计页面
	/** 刷新界面广播action 用于解决点击收缩数据时界面刷新问题 */
	public static final String ACTION_UPDATE_PAGER = "update_pager";
	private DBManager mDbManager;
	private List<Fragment> contentFragment = new ArrayList<Fragment>();
	private List<String> contentTabName = new ArrayList<String>();
	private ControlBar mControlBar;
	private Context mContext = FileManagerFragmentActivity.this;
	private FragmentPagerAdapter adapter;
	private ViewPager pager;
	private FileOperater mFileOperater = null;
	private PopButton popButton;
	/** 打开数据管理界面 当处于上传状态时 更新上传按钮状态 */
	private boolean isUpdateUploadButtonState = true;
	/** 是否从统计进入 */
	private boolean isTotalMode = false;
	/** 是否从报表进入 */
	private boolean isReportMode;
	/** 是否从回放选择文件进入 **/
	private boolean isFromReplay = false;
	/** 是否从智能分析进入 **/
	private boolean isInteligentAnalysis = false;
	private Button btnTotal;
	/** 是否是自定义报表统计 **/
	public final static String IS_CUSTOM_REPORT = "IS_CUSTOM_REPORT";
	public final static String IS_TOTAL = "IS_TOTAL";
	public final static String TOTAL_DDIB_PATH = "TOTAL_DDIB_PATH"; // ddib路径存储用作统计
	public final static String DDIBLIST = "ddibList";
	public int currentSelect = 0;
	/***
	 * 单实例静态变量:文件列表,可从目录或数据库读取 记录所有数据库记录的文件，文件的主要信息和状态
	 */
	private TreeViewAdapter treeAdapter;
	protected ServerOperateType uploadFileType; // 页面对应的Fleet操作类型，用于过滤不同的消息
	/** 服务器管理类 */
	private ServerManager mServer;
	private Button nb;
	// 按钮上显示文字
	private BadgeView badge1;
	// private FileReceiver receiver = new FileReceiver();
	private LinearLayout sharepushLayou;
	// private LocalBroadcastManager broadcastManager;
	private ApplicationModel appModel = ApplicationModel.getInstance();

	/** 智能分析选择的数据显示 */
	private Button analysisSelect;
	private int requestCode = 0x1892;
	/**
	 * 配置文件
	 */
	private SharePreferencesUtil sharePreferencesUtil;
	private static class MyHandler extends Handler {
		private WeakReference<FileManagerFragmentActivity> reference;

		public MyHandler(FileManagerFragmentActivity activity) {
			this.reference = new WeakReference<FileManagerFragmentActivity>(activity);
		}

		public void handleMessage(android.os.Message msg) {
			FileManagerFragmentActivity activity = this.reference.get();
			switch (msg.what) {
			case FragmentBase.MSG_UPDATE_UI:
				final FragmentBase frag = ((FragmentBase) activity.adapter.getItem(activity.pager.getCurrentItem()));
				frag.refreshData();
				break;
			}
		};
	}

	private MyHandler mHandler = new MyHandler(this);

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.file_manager_activity);
		sharePreferencesUtil=SharePreferencesUtil.getInstance(this);
		getIntentValue(this.getIntent());
		mDbManager = DBManager.getInstance(this.mContext);
		mDbManager.setHandler(mHandler);
		mFileOperater = new FileOperater(this.mContext);
		sharepushLayou = (LinearLayout) this.findViewById(R.id.title_right);
		sharepushLayou.setVisibility(View.VISIBLE);
		sharepushLayou.findViewById(R.id.shuxian).setVisibility(View.GONE);
		sharepushLayou.findViewById(R.id.share).setVisibility(View.GONE);
		sharepushLayou.findViewById(R.id.push).setOnClickListener(this);
		nb = (Button) sharepushLayou.findViewById(R.id.push);
		nb.setOnClickListener(this);
		badge1 = new BadgeView(this, nb);
		badge1.setText("1");
		badge1.setTextColor(getResources().getColor(R.color.white));
		badge1.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		badge1.hide();
		initButtonNum();
		initContentFragment(appModel.getSelectScene());
		initControlBar();
		initView();
		invalidateOptionsMenu();
		mServer = ServerManager.getInstance(this);
		registReceiver();
	}

	/***
	 * 获取从上一页传输过来的数据值
	 */
	private void getIntentValue(Intent intent) {
		isTotalMode = intent.getBooleanExtra(KEY_TOTAL_MODE, false);
		isReportMode = intent.getBooleanExtra(KEY_ISREPORT, false);
		isFromReplay = intent.getBooleanExtra(WalkMessage.KEY_IS_FROM_REPLAY, false);
		isInteligentAnalysis = intent.getBooleanExtra(WalkMessage.KEY_IS_FROM_Intelligent_Analysis, false);
	}

	@Override
	public void onBackPressed() {
		if (isFromReplay) {
			FloatWindowManager.showFloatWindow(mContext);
		}
		finish();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.pointer:// 返回
			if (isFromReplay) {
				FloatWindowManager.showFloatWindow(mContext);
			}
			finish();
			break;
		case R.id.push:
			try {
				// 无新的共享信息,给出提示
				List<ShareFileModel> lists = ShareDataBase.getInstance(mContext).fetchAllFilesByFileStatusAndFileType(
						ShareFileModel.FILETYPE_DATA, new int[] { ShareFileModel.FILE_STATUS_INIT, ShareFileModel.FILE_STATUS_START,
								ShareFileModel.FILE_STATUS_ONGOING });
				if (lists.size() <= 0) {
					ToastUtil.showToastShort(this, R.string.share_project_share_info_obj_receive);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			badge1.hide();
			// 跳转到接收diaolog
			Bundle bundle2 = new Bundle();
			bundle2.putInt("fileType", ShareFileModel.FILETYPE_DATA);
			this.jumpActivity(ShareDialogActivity.class, bundle2);
			break;
		case R.id.share:// 分享
			break;
		case R.id.Button01:// 全选
			chooseAll(view);
			break;
		case R.id.Button02:// 回放
			replay();
			break;
		case R.id.Button06:// 统计
			total();
			break;
		case R.id.Button04:// 删除
			deleteMode();
			break;
		case R.id.Button05:// 上传
			if (((Button) view).getText().equals(getResources().getString(R.string.upload))) {
				upload();
			} else {
				stopUpload();
			}
			break;
		case R.id.Button07:// 分享
			shareModel();
			break;
		default:
			break;
		}
	}

	private void jumpActivity(Class<?> cls, Bundle bundle) {
		Intent intentx = new Intent(this, cls);
		intentx.putExtras(bundle);
		this.startActivityForResult(intentx, requestCode);
		overridePendingTransition(R.anim.open_next, R.anim.close_main);
		intentx = null;
	}

	private void initContentFragment(SceneType sceneType) {
		contentFragment.clear();
		contentTabName.clear();
		contentFragment.addAll(mDbManager.getContentFragment(sceneType));
		contentTabName.addAll(mDbManager.getContentTabNames(sceneType));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (this.requestCode == requestCode) {
			if (resultCode == AnalysisCommons.ANALYSIS_RESULT_CODE) {
				this.finish();
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private void initView() {
		TextView tvTitle = (TextView) findViewById(R.id.title_txt);
		tvTitle.setText(getResources().getString(R.string.data_file));
		findViewById(R.id.pointer).setOnClickListener(this);
		adapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);
		pager.setOffscreenPageLimit(10);
		final TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		if (appModel.getSelectScene() == SceneType.HighSpeedRail) {
			indicator.setVisibility(View.GONE);
		} else if (appModel.getSelectScene() == SceneType.Metro) {
			indicator.setVisibility(View.GONE);
		}else if (appModel.getSelectScene() == SceneType.SingleSite) {
			indicator.setVisibility(View.GONE);
		}
		changeChooseAllButtonState();
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				indicator.setCurrentItem(arg0);
				if (isTotalMode) {
					getCurrentAdapter().setTotalMode(true);
					getCurrentAdapter().setTotalButton(btnTotal);
				} else {
					getCurrentAdapter().setNormalMode();
				}
				changeChooseAllButtonState();
				if (popButton != null) {
					popButton.close();
				}
				currentSelect = arg0;

				if (isInteligentAnalysis) {// 从智能分析按钮进入
					FragmentBase frag = ((FragmentBase) adapter.getItem(pager.getCurrentItem()));
					if (frag.getAdapter().selectedList.size() > 0) {
						analysisSelect.setText(
								String.format(getString(R.string.intelligent_analysis_select), frag.getAdapter().selectedList.size()));
					} else {
						analysisSelect.setText(R.string.intelligent_analysis_nothing);
					}

				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (isTotalMode) {
					getCurrentAdapter().setTotalMode(true);
					getCurrentAdapter().setTotalButton(btnTotal);
				} else {
					if (null != getCurrentAdapter()) {
						getCurrentAdapter().setNormalMode();
					}
				}
				changeChooseAllButtonState();

				if (isFromReplay) {
					FloatWindowManager.hiddenFloatWindow(mContext);
					replay();
				}
			}
		};
		handler.sendEmptyMessageDelayed(1, 100);
	}

	private void initControlBar() {
		mControlBar = (ControlBar) findViewById(R.id.ControlBar);
		Button btnChoose = mControlBar.getButton(0);
		Button btnReview = mControlBar.getButton(1);
		Button btnOther = mControlBar.getButton(2);
		Button btnDelete = mControlBar.getButton(3);
		Button btnUpload = mControlBar.getButton(4);
		btnTotal = mControlBar.getButton(5);
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
		if (SharePreferencesUtil.getInstance(mContext).getBoolean(ShareCommons.SHARE_DATA_KEY, false)) {
			Button btnShare = mControlBar.getButton(6);
			btnShare.setText(getResources().getString(R.string.share_project_share));
			btnShare.setOnClickListener(this);
			btnShare.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_edit),
					null, null);
		}
		if (isTotalMode) {
			btnReview.setVisibility(View.INVISIBLE);
			btnOther.setVisibility(View.INVISIBLE);
			btnDelete.setVisibility(View.INVISIBLE);
			btnUpload.setVisibility(View.INVISIBLE);
			btnTotal.setVisibility(View.VISIBLE);
		} else if (isInteligentAnalysis) {
			analysismode();
		} else {
			btnOther.setVisibility(View.GONE);
			btnTotal.setVisibility(View.VISIBLE);
		}
	}

	/***
	 * 智能分析
	 */
	private void analysismode() {
		Button[] buttons = mControlBar.getAllButtons();
		int length = buttons.length;
		for (int i = 0; i < length; i++) {
			buttons[i].setOnClickListener(null);
			if (i == length - 2) {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.MATCH_PARENT);
				params.weight = 2.0f;
				buttons[i].setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				buttons[i].setLayoutParams(params);
				buttons[i].setText(getString(R.string.intelligent_analysis_nothing) + "");
				buttons[i].setVisibility(View.VISIBLE);
				buttons[i].setEnabled(false);
				analysisSelect = buttons[i];
			} else if (i == length - 1) {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.MATCH_PARENT);
				params.weight = 1.0f;
				buttons[i].setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				buttons[i].setLayoutParams(params);
				buttons[i].setText(getString(R.string.intelligent_analysis_finish) + "");
				buttons[i].setVisibility(View.VISIBLE);
				buttons[i].setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						final FragmentBase frag = ((FragmentBase) adapter.getItem(pager.getCurrentItem()));
						if (frag.getAdapter().selectedList.size() <= 0) {
							ToastUtil.showToastShort(FileManagerFragmentActivity.this, R.string.csfb_faild_selectfile);
							return;
						}
						// 异步进行智能分析
						new ExecAnalysis((ArrayList<DataModel>) frag.getAdapter().selectedList).execute();

					}
				});
			} else {
				buttons[i].setVisibility(View.GONE);
			}
		}
	}

	/***
	 * 异步执行智能分析
	 * 
	 * @author weirong.fan
	 *
	 */
	private class ExecAnalysis extends AsyncTask<Void, Void, Boolean> {
		private ArrayList<DataModel> selectedList;
		/** 进度提示 */
		private ProgressDialog progressDialog;

		private String mergeUKPath = "";

		private ExecAnalysis(ArrayList<DataModel> selectedList) {
			super();
			this.selectedList = selectedList;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			this.openDialog(getString(R.string.intelligent_analysis_doing));
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			this.closeDialog();
			Bundle bundle = new Bundle();
			bundle.putString(AnalysisCommons.ANALYSIS_SELECT_FILE_MERGEUK_PATH, mergeUKPath);
			jumpActivity(AnalysisDetailActivity.class, bundle);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				List<String> ddibFiles = new LinkedList<String>();

				for (DataModel dm : selectedList) {
					ArrayList<RecordDetail> recordDetails = dm.testRecord.getRecordDetails();
					for (RecordDetail detail : recordDetails) {
						if (detail.file_name.endsWith(".ddib")) {
							ddibFiles.add(detail.file_path + detail.file_name);
						}
					}
				}
				VoLTEFaildAnalyse voiceAnalyse = VoLTEFaildAnalyse.getInstance(mContext);
				voiceAnalyse.getFaildAnalyseResult(ddibFiles);

				mergeUKPath = saveResultJson(selectedList);
				return true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return false;
		}

		/**
		 * 打开进度条
		 * 
		 * @param txt
		 */
		protected void openDialog(String txt) {
			progressDialog = new ProgressDialog(mContext);
			progressDialog.setMessage(txt);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		/**
		 * 关闭进度条
		 */
		protected void closeDialog() {
			progressDialog.dismiss();
		}

		/***
		 * 保存历史结果
		 * 
		 * @param listData
		 *          选择的结果数据文件
		 */
		@SuppressLint("SdCardPath")
		private String saveResultJson(List<DataModel> listData) throws Exception {
			// 先判断是否大于等于5个文件
			File fh = AppFilePathUtil.getInstance().getSDCardBaseFile(AnalysisCommons.ANALYSIS_PATH_ROOT,AnalysisCommons.ANALYSIS_PATH_ROOT_ANALYSIS,AnalysisCommons.ANALYSIS_PATH_ROOT_ANALYSIS_HISTORY);
			File[] files = fh.listFiles();
			if (null != files && files.length >= 5) {
				List<File> listFiles = new LinkedList<File>();
				for (File f : files) {
					listFiles.add(f);
				}
				Collections.sort(listFiles, new Comparator<File>() {
					@Override
					public int compare(File obj1, File obj2) {
						if (obj1.isDirectory() && obj2.isFile()) {
							return 1;
						} else if (obj1.isFile() && obj2.isDirectory()) {
							return -1;
						} else {
							return obj1.getName().compareTo(obj2.getName());
						}
					}
				});
				for (int i = 0; i < listFiles.size() - 4; i++) {
					FileUtil.deleteDir(listFiles.get(i));
				}
			}

			File filePath = new File(AppFilePathUtil.getInstance().createSDCardBaseDirectory(AnalysisCommons.ANALYSIS_PATH_ROOT,AnalysisCommons.ANALYSIS_PATH_ROOT_ANALYSIS,
					AnalysisCommons.ANALYSIS_PATH_ROOT_ANALYSIS_HISTORY, DateUtil.getDateTime()));

			// 保存json数据
			JSONObject allData = new JSONObject();
			JSONArray array = new JSONArray();
			String str_select = sharePreferencesUtil.getString(AnalysisCommons.ANALYSIS_SELECT_SCENE).trim();
			String[] selects = str_select.split(",");
			for (String s : selects) {
				array.put(s);
			}
			allData.put("Analysis", array);
			allData.put("Date", DateUtils.getCurrentDateTime());
			boolean isFlag = true;
			JSONArray array2 = new JSONArray();
			for (DataModel dm : listData) {
				ArrayList<RecordDetail> recordDetails = dm.testRecord.getRecordDetails();
				for (RecordDetail detail : recordDetails) {
					if (detail.file_name.endsWith(".ddib")) {
						array2.put(detail.file_path + detail.file_name);
					}
				}
				if (isFlag && dm.testRecord.test_type == WalkStruct.TestType.CQT.getTestTypeId()) {
					isFlag = false;
				}
			}
			allData.put("AllIsDT", isFlag ? 1 : 0);
			allData.put("Files", array2);
			File file = new File(filePath.getAbsolutePath() + File.separator + AnalysisCommons.HISTORY_JSON_FILE_NAME);
			PrintStream out = null;
			try {
				out = new PrintStream(new FileOutputStream(file));
				out.print(allData.toString());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (out != null) {
					out.close();
					out = null;
				}
			}

			// 拷贝uk到指定目录下
			String dbName = AnalysisCommons.ANALYSIS_MERGEUK_NAME;
			File srcF = AppFilePathUtil.getInstance().getSDCardBaseFile("TotalConfig", "ukdir", "Temp", dbName);
			// 拷贝到的目标uk
			File destF = new File(filePath.getAbsolutePath() + File.separator + dbName);
			FileUtil.copyFile(srcF, destF);
			return destF.getAbsolutePath();
		}

	}

	/**
	 * 删除模式
	 */
	@SuppressLint("InflateParams")
	private void deleteMode() {
		final FragmentBase frag = ((FragmentBase) adapter.getItem(pager.getCurrentItem()));
		final TreeViewAdapter adapter = frag.getAdapter();
		delete(adapter.selectedList);
		adapter.getDatas().removeAll(frag.getAdapter().selectedList);
		adapter.selectedList.clear();
		adapter.notifyDataSetChanged();
//		View view = LayoutInflater.from(mContext).inflate(R.layout.data_manager_delete_bottom_button, null);
//		popButton = new PopButton(mContext, view).setButtonListener(R.id.btn_cancel, new ClickListener() {
//			@Override
//			public void onClick() {
//				frag.getAdapter().setDeleteMode(false);
//				frag.getAdapter().notifyDataSetChanged();
//			}
//		}, true).setButtonListener(R.id.btn_choose_all, new ClickListener() {
//			@Override
//			public void onClick() {
//				adapter.deleteList.clear();
//				System.out.println("tree node size:" + adapter.getDatas().size());
//				for (TreeNode n : adapter.getDatas()) {
//					deleteChoose(adapter, n);
//				}
//				adapter.notifyDataSetChanged();
//				adapter.setButtonState(3, R.id.btn_delete);
//			}
//		}, false).setButtonListener(R.id.btn_choose_non, new ClickListener() {
//			@Override
//			public void onClick() {
//				adapter.deleteList.clear();
//				for (TreeNode n : adapter.getDatas()) {
//					deleteInverse(n);
//				}
//				adapter.notifyDataSetChanged();
//				adapter.setButtonState(3, R.id.btn_delete);
//			}
//		}, false).setButtonListener(R.id.btn_delete, new ClickListener() {
//			@Override
//			public void onClick() {
//				delete(frag.getAdapter().deleteList);
//				frag.getAdapter().getDatas().removeAll(frag.getAdapter().deleteList);
//				frag.getAdapter().deleteList.clear();
//				frag.getAdapter().notifyDataSetChanged();
//				frag.getAdapter().setDeleteMode(false);
//			}
//		}, true);
//		popButton.show();
//		adapter.setPopButton(popButton);
//		adapter.setButtonState(3, R.id.btn_delete);
//		frag.getAdapter().setDeleteMode(true);
//		frag.getAdapter().notifyDataSetChanged();
	}

	/**
	 * 分享模式
	 */
	@SuppressLint("InflateParams")
	private void shareModel() {
		final FragmentBase frag = ((FragmentBase) adapter.getItem(pager.getCurrentItem()));
		final TreeViewAdapter adapter = frag.getAdapter();
		View view = LayoutInflater.from(mContext).inflate(R.layout.data_manager_delete_bottom_button, null);
		popButton = new PopButton(mContext, view).setButtonListener(R.id.btn_cancel, new ClickListener() {
			@Override
			public void onClick() {
				frag.getAdapter().setShareMode(false);
				frag.getAdapter().setDeleteMode(false);
				frag.getAdapter().notifyDataSetChanged();
			}
		}, true).setButtonListener(R.id.btn_choose_all, new ClickListener() {
			@Override
			public void onClick() {
				adapter.deleteList.clear();
				System.out.println("tree node size:" + adapter.getDatas().size());
				for (TreeNode n : adapter.getDatas()) {
					deleteChoose(adapter, n);
				}
				adapter.notifyDataSetChanged();
				adapter.setButtonState(3, R.id.btn_delete);
			}
		}, false).setButtonListener(R.id.btn_choose_non, new ClickListener() {
			@Override
			public void onClick() {
				adapter.deleteList.clear();
				for (TreeNode n : adapter.getDatas()) {
					deleteInverse(n);
				}
				adapter.notifyDataSetChanged();
				adapter.setButtonState(3, R.id.btn_delete);
			}
		}, false).setButtonListener(R.id.btn_delete, new ClickListener() {
			@Override
			public void onClick() {
				Bundle bundle = new Bundle();
				bundle.putInt(ShareCommons.SHARE_FROM_KEY, ShareCommons.SHARE_FROM_DATA);
				List<TreeNode> deleteList = frag.getAdapter().deleteList;
				if (null == deleteList || deleteList.size() <= 0) {
					ToastUtil.showToastShort(mContext, getString(R.string.share_project_share_info_info));
					return;
				}
				frag.getAdapter().setShareMode(false);
				frag.getAdapter().setDeleteMode(false);
				ArrayList<String> selectFiles = new ArrayList<String>();
				for (TreeNode n : deleteList) {
					DataModel d = (DataModel) n.getValue();
					if (null != d.testRecord) {
						if (!selectFiles.contains(d.testRecord.record_id)) {
							selectFiles.add(d.testRecord.record_id);
						}
					}
				}
				bundle.putStringArrayList("projects", selectFiles);
				jumpActivity(ShareSendActivity.class, bundle);
			}
		}, true);
		popButton.show();
		adapter.setPopButton(popButton);
		frag.getAdapter().setShareMode(true);
		frag.getAdapter().setDeleteMode(true);
		adapter.setButtonState(3, R.id.btn_delete);
		frag.getAdapter().notifyDataSetChanged();
	}

	private void delete(List<DataModel> list) {

		ArrayList<DataModel> fileList = new ArrayList<DataModel>();
		for (DataModel d : list) {
			if (!d.isFolder) {
				fileList.add(d);
			}
		}
		// 删除SDCard文件
		mFileOperater.delete(fileList);
	}

	/**
	 * 统计
	 */
	private void total() {
		final FragmentBase frag = ((FragmentBase) adapter.getItem(pager.getCurrentItem()));
		final TreeViewAdapter adapter = frag.getAdapter();
		if (adapter.totalList.size() == 0) {
			Toast.makeText(mContext, getResources().getString(R.string.total_select_empty), Toast.LENGTH_SHORT).show();
		} else {
			totalReport(frag.FLAG, adapter.totalList);
		}
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
		ArrayList<String> ddibPaths = new ArrayList<String>();
		if (!isReportMode) {
			switch (flagValue) {
			case 0:
				DLMessageModel dlMessageModelCqt = new DLMessageModel();
				List<SubDlMessageItem> subDlMessageItemsCqt = new ArrayList<SubDlMessageItem>();
				dlMessageModelCqt.setSendTime(UtilsMethod.sdFormatss.format(System.currentTimeMillis()));
				dlMessageModelCqt.setBusiness("All");
				// List<String> allFilePathListCqt = new ArrayList<String>();
				SubDlMessageItem allSubDlMessageItemCqt = new SubDlMessageItem();
				for (int i = 0; i < lisDataModels.size(); i++) {
					List<String> filePathListCqt = new ArrayList<String>();
					SubDlMessageItem subDlMessageItemCqt = new SubDlMessageItem();
					filePathListCqt.add(lisDataModels.get(i).getFilePath(FileType.DDIB.getFileTypeName()));
					subDlMessageItemCqt.setSceneName(lisDataModels.get(i).testRecord.file_name);
					subDlMessageItemCqt.setDataFileName(filePathListCqt);
					subDlMessageItemsCqt.add(subDlMessageItemCqt);
					ddibPaths.add(lisDataModels.get(i).getFilePath(FileType.DDIB.getFileTypeName()));
				}
				allSubDlMessageItemCqt.setDataFileName(ddibPaths);
				allSubDlMessageItemCqt.setSceneName("DLSum");
				subDlMessageItemsCqt.add(0, allSubDlMessageItemCqt);
				dlMessageModelCqt.setSubDlMessageItems(subDlMessageItemsCqt);
				RequestSceneXMl.getInstance().setFilePathList(ddibPaths); // 存储选中数据管理
				RequestSceneXMl.getInstance().xmlFileCreator(dlMessageModelCqt);
				break;
			case 1:
				DLMessageModel dlMessageModel = new DLMessageModel();
				List<SubDlMessageItem> subDlMessageItems = new ArrayList<SubDlMessageItem>();
				SubDlMessageItem subDlMessageItem = new SubDlMessageItem();
				// List<String> filePathList = new ArrayList<String>();
				dlMessageModel.setSendTime(UtilsMethod.sdFormatss.format(System.currentTimeMillis()));
				dlMessageModel.setBusiness("All");
				for (int i = 0; i < lisDataModels.size(); i++) {
					ddibPaths.add(lisDataModels.get(i).getFilePath(FileType.DDIB.getFileTypeName()));
					subDlMessageItem.setSceneName("DT");
				}
				RequestSceneXMl.getInstance().setFilePathList(ddibPaths); // 存储选中数据管理
				subDlMessageItem.setDataFileName(ddibPaths);
				subDlMessageItems.add(subDlMessageItem);
				dlMessageModel.setSubDlMessageItems(subDlMessageItems);
				RequestSceneXMl.getInstance().xmlFileCreator(dlMessageModel);
				break;
			default:
				break;
			}
		} else {
			for (int i = 0; i < lisDataModels.size(); i++) {
				ddibPaths.add(lisDataModels.get(i).getFilePath(FileType.DDIB.getFileTypeName()));
			}
		}
		Intent intent = new Intent();
		if (isTotalMode){
			intent.putExtra(KEY_TOTAL_RUN, lisDataModels.size() > 0 ? true : false);
			intent.putExtra(FileManagerFragmentActivity.IS_TOTAL, flagValue);
			intent.putStringArrayListExtra(DDIBLIST, ddibPaths);
			FileManagerFragmentActivity.this.setResult(RESULT_OK, intent);
			FileManagerFragmentActivity.this.finish();
		}else {
			intent.setClass(this,TotalDetailActivity.class);
			intent.putExtra(KEY_TOTAL_RUN, lisDataModels.size() > 0 ? true : false);
			intent.putExtra(FileManagerFragmentActivity.IS_TOTAL, flagValue);
			intent.putExtra(KEY_FILEMANAGER_TO_TOTALDETAIL,true);
			intent.putStringArrayListExtra(DDIBLIST, ddibPaths);
			startActivity(intent);
			FileManagerFragmentActivity.this.finish();
		}
	}

	/**
	 * 回放
	 */
	private void replay() {
		final FragmentBase frag = ((FragmentBase) adapter.getItem(pager.getCurrentItem()));
		if (frag.getAdapter().selectedList.size() > 0) {
			if (isFromReplay) {
				FloatWindowManager.removeFloatWindow(mContext);
			}
			runReplay(frag.getAdapter().selectedList.get(0));
		} else {
			Toast.makeText(mContext, getResources().getString(R.string.str_selectfile), Toast.LENGTH_SHORT).show();
		}
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
			if (appModel.getSelectScene() == SceneType.Metro) {// 地铁场景,需要设置城市和线路
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
			} else if (appModel.getSelectScene() == SceneType.HighSpeedRail) {// 高铁场景,需要设置线路
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
		final FragmentBase frag = ((FragmentBase) adapter.getItem(pager.getCurrentItem()));
		TreeViewAdapter adapter = frag.getAdapter();
		if (((Button) v).getText().equals(getResources().getString(R.string.str_checkall))) {
			for (TreeNode n : adapter.getDatas()) {
				choose(adapter, n);
			}
			frag.isChooseAll = true;
			((Button) v).setText(getResources().getString(R.string.str_checknon));
			((Button) v).setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.controlbar_unallcheck), null, null);
			adapter.notifyDataSetChanged();
		} else {
			adapter.selectedList.clear();
			adapter.totalList.clear();
			for (TreeNode n : adapter.getDatas()) {
				inverse(n);
			}
			frag.isChooseAll = false;
			((Button) v).setText(getResources().getString(R.string.str_checkall));
			((Button) v).setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.controlbar_select), null, null);
			frag.getAdapter().notifyDataSetChanged();
		}
		adapter.setTotalButtonState();
	}

	private void changeChooseAllButtonState() {
		if (isInteligentAnalysis)
			return;
		FragmentBase frag = ((FragmentBase) adapter.getItem(pager.getCurrentItem()));
		if (frag.isChooseAll) {
			mControlBar.getButton(0).setText(getResources().getString(R.string.str_checknon));
			mControlBar.getButton(0).setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.controlbar_unallcheck), null, null);
		} else {
			mControlBar.getButton(0).setText(getResources().getString(R.string.str_checkall));
			mControlBar.getButton(0).setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.controlbar_select), null, null);
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
		if (!node.isExpanded() && node.getChildren().size() > 0) {
			for (TreeNode n : node.getChildren()) {
				deleteChoose(adapter, n);
			}
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
		d.isTotalChecked = true;
		if (!d.isFirstLevel && !d.isFolder) {
			adapter.selectedList.add(d);
			adapter.totalList.add(d);
		}
		if (!node.isExpanded() && node.getChildren().size() > 0) {
			for (TreeNode n : node.getChildren()) {
				choose(adapter, n);
			}
		}
	}

	/**
	 * 反选
	 * 
	 * @param node
	 */
	private void inverse(TreeNode node) {
		((DataModel) node.getValue()).isChecked = false;
		((DataModel) node.getValue()).isTotalChecked = false;
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

	private void stopUpload() {
		FragmentBase frag = ((FragmentBase) adapter.getItem(pager.getCurrentItem()));
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
			if (appModel.getNetList().contains(WalkStruct.ShowInfoType.TelecomSwitch))
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
		final FragmentBase frag = ((FragmentBase) adapter.getItem(pager.getCurrentItem()));
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
		treeAdapter = ((FragmentBase) adapter.getItem(pager.getCurrentItem())).getAdapter();
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
							changeUploadButtonState(true);//
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
			for (int j = 0; j < model.testRecord.getRecordDetails().size(); j++) {
				RecordDetail detail = model.testRecord.getRecordDetails().get(j);
				if (detail.file_type == FileType.FloorPlan.getFileTypeId()) {
					file.addExtraParam("BgPicID", detail.file_name);
				} else if (detail.file_type == FileType.MOSZIP.getFileTypeId()) {
					File file1 = new File(detail.file_path + File.separator + detail.file_name);
					if (file1.isDirectory()) {
						file.addExtraParam("MOSFilesPath", file1.getAbsolutePath());
					}
				} else {
					file.setParentPath(detail.file_path);
					for (int k = 0; k < model.testRecord.getRecordTestInfo().size(); k++) {
						RecordTestInfo info = model.testRecord.getRecordTestInfo().get(k);
						if (info.key_info.equals(DataTableStruct.RecordInfoKey.tester.name())) {
							file.addExtraParam("Tester", info.key_value);
							break;
						}
					}
				}
			}
			if (appModel.getNetList().contains(WalkStruct.ShowInfoType.TelecomSwitch))
				fileTypes.add(FileType.FloorPlan);
			file.setFileTypes(fileTypes);
			uploadFiles.add(file);
		}
		// 上传
		mServer.uploadFile(mContext, WalkStruct.ServerOperateType.uploadTestFile, uploadFiles);
	}

	private TreeViewAdapter getCurrentAdapter() {
		return ((FragmentBase) adapter.getItem(pager.getCurrentItem())).getAdapter();
	}

	/**
	 * 设置等待上传状态
	 */
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
		FragmentBase frag = ((FragmentBase) adapter.getItem(pager.getCurrentItem()));
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

	// =================================================================================
	private void registReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DataTransService.EXTRA_DATA_TRANS_FILE_PROGRESS);
		intentFilter.addAction(DataTransService.EXTRA_DATA_TRANS_END);
		intentFilter.addAction(ACTION_UPDATE_PAGER);
		intentFilter.addAction(ShareCommons.SHARE_ACTION_8);
		intentFilter.addAction(ShareCommons.SHARE_ACTION_REFRESH_DATA);
		intentFilter.addAction(AnalysisCommons.ANALYSIS_ACTION_SELECT_FILE);
		registerReceiver(mUploadStateReceiver, intentFilter);
	}

	private BroadcastReceiver mUploadStateReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, android.content.Intent intent) {
			String action = intent.getAction();
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
			} else if (action.equals(ACTION_UPDATE_PAGER)) {
				pager.invalidate();
			} else if (intent.getAction().equals(ShareCommons.SHARE_ACTION_REFRESH_DATA)) {
				int size = FileManagerFragmentActivity.this.adapter.getCount();
				for (int i = 0; i < size; i++) {
					final FragmentBase frag = ((FragmentBase) FileManagerFragmentActivity.this.adapter.getItem(i));
					frag.refreshData();
				}
			} else if (intent.getAction().equals(ShareCommons.SHARE_ACTION_8)) {
				initButtonNum();
			} else if (intent.getAction().equals(AnalysisCommons.ANALYSIS_ACTION_SELECT_FILE)) {
				if (isInteligentAnalysis) {
					FragmentBase frag = ((FragmentBase) adapter.getItem(pager.getCurrentItem()));
					if (frag.getAdapter().selectedList.size() > 0) {
						analysisSelect.setText(
								String.format(getString(R.string.intelligent_analysis_select), frag.getAdapter().selectedList.size()));
					} else {
						analysisSelect.setText(R.string.intelligent_analysis_nothing);
					}

				}
			}
		};
	};

	// ==========================================================================
	private class CustomFragmentPagerAdapter extends FragmentPagerAdapter {
		public CustomFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return contentFragment.get(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return contentTabName.get(position % contentTabName.size()).toUpperCase(Locale.getDefault());
		}

		@Override
		public int getCount() {
			return contentTabName.size();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			this.mDbManager.setHandler(null);
			unregisterReceiver(mUploadStateReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.open_main, R.anim.close_next);
	}

	/**
	 * 接收到广播后
	 */
	private void initButtonNum() {
		try {
			List<ShareFileModel> lists = ShareDataBase.getInstance(mContext).fetchAllFilesByFileStatusAndFileType(
					ShareFileModel.FILETYPE_DATA, new int[] { ShareFileModel.FILE_STATUS_INIT });
			if (lists.size() > 0) {
				badge1.setText(lists.size() + "");
				badge1.show(true);
			} else {
				badge1.hide(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
