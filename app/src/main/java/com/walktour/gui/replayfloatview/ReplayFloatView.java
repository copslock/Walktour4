package com.walktour.gui.replayfloatview;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dinglicom.DataSetUtil;
import com.dinglicom.data.model.RecordTestInfo;
import com.dinglicom.data.model.TestRecord;
import com.dinglicom.dataset.DatasetManager;
import com.dinglicom.dataset.EventManager;
import com.dinglicom.dataset.PlaybackManager;
import com.dinglicom.dataset.logic.ControlPanelLinstener;
import com.walktour.Utils.AlertWakeLock;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UtilsWalktour;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.instance.AlertManager;
import com.walktour.control.instance.DataManagerFileList;
import com.walktour.framework.view.L3MsgRefreshEventManager;
import com.walktour.framework.view.L3MsgRefreshEventManager.L3MsgRefreshEventListener;
import com.walktour.gui.R;
import com.walktour.gui.applet.FileExplorer;
import com.walktour.gui.data.FileManagerFragmentActivity;
import com.walktour.gui.data.model.DataModel;
import com.walktour.gui.highspeedrail.HighSpeedRailCommons;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.service.innsmap.InnsmapFactory;
import com.walktour.service.metro.MetroFactory;
import com.walktour.service.test.TestTaskService;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

/**
 * @author zhihui.lian
 * <p>
 * ?????????????????????View ??????????????????
 */
@SuppressLint("MissingSuperCall")
public class ReplayFloatView extends LinearLayout implements ViewLinstenser, View.OnClickListener,
		ControlPanelLinstener, OnSeekBarChangeListener, ConverterDdib.ConverterDdibI {
	private ApplicationModel appModel = ApplicationModel.getInstance();
	private final String TAG = "ReplayFloatView";
	/**
	 * ????????????????????????????????????
	 */
	public static int viewWidth;

	/**
	 * ??????????????????????????????
	 */
	public static int viewHeight;

	/**
	 * ??????????????????????????????
	 */
	private static int statusBarHeight;

	/**
	 * ????????????????????????????????????
	 */
	private WindowManager windowManager;

	/**
	 * ????????????????????????
	 */
	private WindowManager.LayoutParams mParams;

	/**
	 * ???????????????????????????????????????????????????
	 */
	private float xInScreen;

	/**
	 * ???????????????????????????????????????????????????
	 */
	private float yInScreen;

	/**
	 * ???????????????????????????????????????????????????
	 */
	// private float xDownInScreen;

	/**
	 * ???????????????????????????????????????????????????
	 */
	// private float yDownInScreen;

	/**
	 * ???????????????????????????????????????View?????????????????????
	 */
	private float xInView;

	/**
	 * ???????????????????????????????????????View?????????????????????
	 */
	private float yInView;

	private PlaybackManager playbackManager;

	private PopupWindow showReplayFile = null; // ????????????????????????

	private Context mContext;

	private LinearLayout view; // ??????View

	private ImageView open;

	private ImageView start2Stop;

	private TextView pointStartTime;

	private TextView pointEndTime;

	private TextView currentSpeed; // ????????????Text

	private SeekBar seekBarA;

	private final static int FILEPATH = 0x00; // ????????????TAG

	private final static int CURRENTTIME = 0x01; // ??????????????????TAG

	private int screenHeight;

	private ArrayList<DataModel> fileList = new ArrayList<DataModel>();

	private String reFilePath = "";

	private boolean isPlayIng = false; // ???????????????

	private boolean isPause = false; // ???????????????

	private ListView list01; // ??????????????????

	private int currentFilePosition = 0; // ?????????????????????

	private String fileName = ""; // ???????????????

	// private ArrayList<DataModel> indoorList = new ArrayList<DataModel>(); //
	// ??????????????????

	private TextView inordorTxt; // ?????? ??????/??????

	// private ApplicationModel appModel = null;

	private Context context;

	// private ConverterDdib converterDdib;

	private TextView showPath;

	private ImageView mSwitchBtn;

	/**
	 * ??????View?????????????????????????????????
	 * 
	 * @param --
	 */
	public ReplayFloatView(Context context) {
		super(context);
		this.context = context;
		// appModel = ApplicationModel.getInstance();
		mContext = context;
		windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		LayoutInflater.from(context).inflate(R.layout.float_window_play, this);
		view = (LinearLayout) findViewById(R.id.big_window_layout);

		/**
		 * ?????????ImageButton??????????????????????????????????????????
		 */
		open = (ImageView) findViewById(R.id.open); // ??????
		open.setOnClickListener(this);

		ImageView hidden = (ImageView) findViewById(R.id.hidden); // ??????
		hidden.setOnClickListener(this);

		ImageView close = (ImageView) findViewById(R.id.close); // ??????
		close.setOnClickListener(this);

		ImageView speedup = (ImageView) findViewById(R.id.speedup); // ??????
		speedup.setOnClickListener(this);

		ImageView speeddown = (ImageView) findViewById(R.id.speeddown); // ??????
		speeddown.setOnClickListener(this);

		ImageView next = (ImageView) findViewById(R.id.next); // ?????????
		next.setOnClickListener(this);

		ImageView pre = (ImageView) findViewById(R.id.pre); // ?????????
		pre.setOnClickListener(this);

		ImageView inordor = (ImageView) findViewById(R.id.inordor); // ??????/??????
		inordor.setOnClickListener(this);

		start2Stop = (ImageView) findViewById(R.id.start_stop); // ??????/??????
		start2Stop.setOnClickListener(this);

		pointStartTime = (TextView) findViewById(R.id.pointStartTime); // ????????????Text

		pointEndTime = (TextView) findViewById(R.id.pointEndTime); // ????????????Text

		currentSpeed = (TextView) findViewById(R.id.currentSpeed); // ????????????Text

		inordorTxt = (TextView) findViewById(R.id.inordorTxt); // ?????????????????? ??????/??????

		showPath = (TextView) findViewById(R.id.showFilePath); // ????????????????????????
		showPath.setSelected(true);

		mSwitchBtn = (ImageView) findViewById(R.id.tv_switch);
		mSwitchBtn.setOnClickListener(this);
		/** ????????? */
		seekBarA = (SeekBar) findViewById(R.id.seekBar);
		seekBarA.setOnSeekBarChangeListener(this);

		viewWidth = view.getLayoutParams().width; // ????????????View??????
		viewHeight = view.getLayoutParams().height; // ????????????View??????
		fileList.clear();
		if (appModel.getSelectScene() == SceneType.HighSpeedRail || appModel.getSelectScene() == SceneType.Metro) {
			fileList.addAll(DataManagerFileList.getInstance(getContext()).getAllFileList(appModel.getSelectScene()));
		} else {
			fileList.addAll(DataManagerFileList.getInstance(context).getAllFileList(appModel.getBusinessTestScenes()));
			// fileList.addAll(DataManagerFileList.getInstance(getContext()).getAllFileList(TestType.DT));
			// fileList.addAll(DataManagerFileList.getInstance(getContext()).getAllFileList(TestType.CQT));
		}
		playbackManager = new PlaybackManager(getContext(), this); // ????????????????????????

		ConverterDdib.getInstance(mContext).registerObserver(this);

		// twq20140214????????????????????????
		// regeditBroadcast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(FileExplorer.ACTION_LOAD_NORMAL_FILE);
		filter.addAction(WalkMessage.ACTION_UNIT_NORMAL_RECEIVE);
		context.registerReceiver(mReceiver, filter);
	}

	/**
	 * ?????????????????????????????????????????????????????????????????????????????????
	 */
	private int pauseTimes = 0;

	private MyHandler handler = new MyHandler(this);

	private static class MyHandler extends Handler {
		private WeakReference<ReplayFloatView> reference;

		public MyHandler(ReplayFloatView view) {
			this.reference = new WeakReference<ReplayFloatView>(view);
		}

		public void handleMessage(Message msg) {
			ReplayFloatView view = this.reference.get();
			switch (msg.what) {
				case FILEPATH:
					view.currentFilePosition = msg.arg1;
					view.initPlayFileInfo(false);
					break;
				case CURRENTTIME:
					if (view.isPause) { // ?????????????????????????????????????????????,??????????????????
						switch (view.pauseTimes % 2) {
							case 0:
								setTvStartTime(msg, view);
								if (view.pauseTimes > 99) {
									view.pauseTimes = 0;
								}
								// ?????????
								Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
								// ??????????????????
								alphaAnimation.setDuration(1000);
								view.pointStartTime.startAnimation(alphaAnimation);
								break;

							default:
								setTvStartTime(msg, view);
								// ?????????
								break;
						}
						view.pauseTimes++;
					} else {
						setTvStartTime(msg, view);
					}
					break;
				default:
					break;
			}
		}

		private void setTvStartTime(Message msg, ReplayFloatView view) {
			if (msg == null || view == null){
				return;
			}
			if (msg.arg1 == PlaybackManager.UINT_FILE) {
				view.pointStartTime.setText(msg.obj.toString());
				view.seekBarA.setMax(view.playbackManager.getTotalCount());
				view.seekBarA.setProgress((int)msg.obj); // ?????????????????????
				view.pointEndTime.setText(view.playbackManager.getTotalCount() + "");
			} else if (msg.arg1 == PlaybackManager.UINT_TIME) {
				long between = (long) msg.obj;
				int ratio = (int) ((float) between / view.playbackManager.getTotalTime() * 100);
				view.seekBarA.setMax(100);
				view.seekBarA.setProgress(ratio); // ?????????????????????
				view.pointEndTime.setText(DataSetUtil.parseTimeStr(view.playbackManager.getTotalTime()));
				view.pointStartTime.setText(DataSetUtil.parseTimeStr(between));
			}
		}

		;
	}

	/**
	 * ??????????????????????????????????????????????????????
	 */
	private void initPlayFileInfo(boolean isSync) {
		reFilePath = fileList.get(currentFilePosition).getFilePath(FileType.DDIB.getFileTypeName());
		fileName = fileList.get(currentFilePosition).testRecord.file_name;
		showPath.setText(fileName);
		reSelectFile(isSync);
	}

	private void reSelectFile(boolean isSync) {
		if (isBlueSync(isSync)) {
			UtilsWalktour.sendNormalMsgToBluetooth(mContext, TestTaskService.MSG_PLAYBACK_RESELECT_FILE);
		}

		if (DatasetManager.isPlayback) {
			// ????????????????????????,????????????
			playbackManager.onStop();
			seekBarA.setProgress(0);
			pointStartTime.setText(String.valueOf(0));
			pointEndTime.setText("--");
		}
	}

	private SimpleAdapter adapter;

	private WindowManager wm;

	private View mView;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// ?????????????????????????????????,?????????????????????????????????????????????
			xInView = event.getX();
			yInView = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			xInScreen = event.getRawX();
			yInScreen = event.getRawY();
			// ???????????????????????????????????????????????????
			updateViewPosition();
			break;
		case MotionEvent.ACTION_UP:

			break;
		default:
			break;

		}
		return true;
	}

	/**
	 * ????????????????????????????????????????????????????????????????????????
	 * 
	 * @param params
	 *          ??????????????????
	 */
	public void setParams(WindowManager.LayoutParams params) {
		mParams = params;
	}

	/**
	 * ?????????????????????????????????????????????
	 */
	private void updateViewPosition() {
		try {
			mParams.x = (int) (xInScreen - xInView);
			mParams.y = (int) (yInScreen - yInView - getStatusBarHeight());
			windowManager.updateViewLayout(ReplayFloatView.this, mParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ??????????????????????????????????????????
	 */
	private void hiddenPlayWindow() {
		FloatWindowManager.createDragWindow(context);
		FloatWindowManager.hiddenFloatWindow(context);
	}

	/**
	 * ?????????????????????????????????
	 * 
	 * @return ????????????????????????????????????
	 */
	private int getStatusBarHeight() {
		if (statusBarHeight == 0) {
			try {
				Class<?> c = Class.forName("com.android.internal.R$dimen");
				Object o = c.newInstance();
				Field field = c.getField("status_bar_height");
				int x = (Integer) field.get(o);
				statusBarHeight = getResources().getDimensionPixelSize(x);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return statusBarHeight;
	}

	@Override
	public void onSroll(MotionEvent e1, MotionEvent e2) {
		// yInScreen += (e2.getY() - e1.getY());
		yInView = e1.getY();
		yInScreen = e2.getRawY();
		// ???????????????????????????????????????????????????
		updateViewPosition();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.open:
			LogUtil.w(TAG, "--Replay Open--");
			AlertWakeLock.acquire(mContext);

			screenHeight = windowManager.getDefaultDisplay().getHeight();
			mParams.y = screenHeight;
			mParams.height = this.getHeight() + 400; // ?????????????????????????????????????????????
			windowManager.updateViewLayout(this, mParams);
			createPopView();
			// Toast.makeText(getContext(),
			// "test onClick open",Toast.LENGTH_SHORT).show();
			break;
		case R.id.close:
			LogUtil.w(TAG, "--Replay Close--");
			AlertWakeLock.release();

			closeReplay(false);
			break;
		case R.id.start_stop: // ???????????????
			runOpenPlay(reFilePath);
			// Toast.makeText(getContext(),
			// "test onClick start",Toast.LENGTH_SHORT).show();
			break;
		case R.id.inordor:
			// Toast.makeText(getContext(),
			// "test onClick inordor",Toast.LENGTH_SHORT).show();
			setInOrdor(false);

			break;
		case R.id.pre:
			// Toast.makeText(getContext(),
			// "test onClick pre",Toast.LENGTH_SHORT).show();
			// selectFilePosition(0);
			playbackManager.onUp(isBlueSync(false));
			break;

			case R.id.next:
				// Toast.makeText(getContext(),
				// "test onClick next",Toast.LENGTH_SHORT).show();
				// selectFilePosition(1);
				playbackManager.onNext(isBlueSync(false));
				break;
			case R.id.speeddown:
				setSpeed(false, true, playbackManager.getCurrentSpeed() - 1);
				break;
			case R.id.speedup:
				setSpeed(false, false, playbackManager.getCurrentSpeed() + 1);
				break;
			case R.id.hidden:
				hiddenPlayWindow();
				break;
			case R.id.tv_switch:
				if (playbackManager.getTotalCount()==-1){
					return;
				}
				if (playbackManager != null) {
					playbackManager.switchUint();
				}
				break;
			default:
				break;
		}
	}

	/**
	 * ??????????????????
	 * 
	 * @param isSync
	 *          ????????????????????????
	 * @param isRewind
	 *          ????????????
	 * @param speed
	 *          ????????????
	 */
	private void setSpeed(boolean isSync, boolean isRewind, int speed) {
		if (speed < PlaybackManager.PLAYBACK_SPEED_1X) {
			speed = PlaybackManager.PLAYBACK_SPEED_1X;
		} else if (speed > PlaybackManager.PLAYBACK_SPEED_3X) {
			speed = PlaybackManager.PLAYBACK_SPEED_3X;
		}

		if (isBlueSync(isSync)) {
			UtilsWalktour.sendNormalMsgToBluetooth(mContext, TestTaskService.MSG_PLAYBACK_SPEED + speed + "&" + isRewind);
		}

		if (isRewind) {
			playbackManager.onRewind(speed);
		} else {
			playbackManager.onFastForward(speed);
		}
	}

	/**
	 * ??????????????????
	 * 
	 * @param isSync
	 */
	private void setInOrdor(boolean isSync) {
		if (isBlueSync(isSync)) {
			UtilsWalktour.sendNormalMsgToBluetooth(mContext,
					TestTaskService.MSG_PLAYBACK_INORDOR + playbackManager.getPlaybackDirection());
		}

		switch (playbackManager.getPlaybackDirection()) {
		case PlaybackManager.PLAYBACK_DIRECTION_PREV:
			playbackManager.setPlaybackDirection(1);
			inordorTxt.setText("-->");
			break;
		case PlaybackManager.PLAYBACK_DIRECTION_NEXT:
			playbackManager.setPlaybackDirection(0);
			inordorTxt.setText("<--");
			break;
		default:
			break;
		}
	}

	/**
	 * ????????????
	 * 
	 * @param isSync
	 *          ????????????????????????
	 */
	private void closeReplay(boolean isSync) {
		if (isBlueSync(isSync)) {
			UtilsWalktour.sendNormalMsgToBluetooth(mContext, TestTaskService.MSG_PLAYBACK_CLOSE);
		}

		if (DatasetManager.isPlayback) {
			playbackManager.onStop();
			clearAllData();
		}
		FloatWindowManager.removeFloatWindow(getContext());
	}

	/**
	 * ?????????????????? zhihui.lian
	 */
	@SuppressLint("InflateParams")
	private void createDialog() {
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams para = new WindowManager.LayoutParams();
		para.height = -1;
		para.width = -1;
		para.format = 1;
		para.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
				| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		para.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		mView = LayoutInflater.from(context).inflate(R.layout.replay_progress_page, null);
		wm.addView(mView, para);
		mView.findViewById(R.id.close_dialog).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				wm.removeView(mView);
				mView = null;
			}
		});
		mView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					wm.removeView(mView);
					mView = null;
				}
				return false;
			}
		});
	}

	/**
	 * ??????????????????<BR>
	 * ??????????????????????????????????????????????????????????????????
	 */
	private void clearAllData() {
		TraceInfoInterface.traceData.getGpsLocas().clear();
		MapFactory.getMapData().getPointStatusStack().clear();
		MapFactory.getMapData().getEventQueue().clear();
		EventManager.getInstance().clearEvents();
		TraceInfoInterface.traceData.l3MsgList.clear();
		AlertManager.getInstance(mContext).clearAlarms(true);
        L3MsgRefreshEventManager.getInstance().notifyL3MsgRefreshed(WalkMessage.traceL3MsgChanged, "");
		// mContext.sendBroadcast(new Intent(WalkMessage.traceL3MsgChanged));
		mContext.sendBroadcast(new Intent(WalkMessage.REPLAY_CLEAR_ALL_DATA));
	}

	/**
	 * ??????????????????????????????????????????
	 */
	public void dismissAndHidden() {
		int mparamsHeight = mParams.height - 400;
		if (mparamsHeight < 0 || mparamsHeight >= 100) {
			mParams.height = mparamsHeight;
		} else {
			mParams.height = -130;
		}
		mParams.y = screenHeight;
		windowManager.updateViewLayout(this, mParams);
	}

	ArrayList<HashMap<String, Object>> listItem = null;

	/**
	 * ???popWindow??????????????????
	 */
	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	public void createPopView() {
		if (showReplayFile == null) {
			View treePopView = LayoutInflater.from(getContext()).inflate(R.layout.show_refile_list, null);
			// ??????Layout?????????ListView
			list01 = (ListView) treePopView.findViewById(R.id.showRefile);
			list01.setFocusable(true);
			list01.requestFocus();
			list01.setFocusableInTouchMode(true);

			showReplayFile = new PopupWindow(treePopView, LayoutParams.FILL_PARENT, 400, true);

			listItem = new ArrayList<HashMap<String, Object>>();

			/**
			 * ????????????????????????????????????????????????????????????????????????????????????
			 */
			Collections.sort(fileList, new Comparator<DataModel>() {
				@Override
				public int compare(DataModel lhs, DataModel rhs) {
					return new Date(rhs.getCreateTime()).compareTo(new Date(lhs.getCreateTime()));
				}
			});

			for (int i = 0; i < fileList.size(); i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				DataModel fileModel = fileList.get(i);
				map.put("ItemTitle", fileModel.testRecord.file_name);
				map.put("ItemKey", fileModel.getFilePath(FileType.DDIB.getFileTypeName()));
				listItem.add(map);
			}

			adapter = new SimpleAdapter(getContext(), listItem, R.layout.refile_item, new String[] { "ItemTitle" },
					new int[] { R.id.ItemTitle });

			View viewFile = LayoutInflater.from(getContext()).inflate(R.layout.refile_item, null);

			TextView viewTxt = (TextView) viewFile.findViewById(R.id.ItemTitle);
			viewTxt.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.filelist),
					null, null, null);
			viewTxt.setText(" " + getContext().getResources().getString(R.string.act_file_explorer));

			viewTxt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					showReplayFile.dismiss();
					Intent intent;
					intent = new Intent(context, FileExplorer.class);
					// ??????????????????
					Bundle bundle = new Bundle();
					bundle.putBoolean(FileExplorer.KEY_NORMAL, true);
					bundle.putString(FileExplorer.KEY_ACTION, FileExplorer.ACTION_LOAD_NORMAL_FILE);// ??????????????????
					bundle.putString(FileExplorer.KEY_EXTRA, FileExplorer.KEY_FILE);
					bundle.putStringArray(FileExplorer.KEY_FILE_FILTER,
							new String[] { "FMT", "fmt", "ddib", "Indexbak", "rcu", "cu", "dcf" });
					intent.putExtras(bundle);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
			});

			TextView datamanage = (TextView) viewFile.findViewById(R.id.Itemright);
			datamanage.setText(R.string.main_file);
			datamanage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					showReplayFile.dismiss();
					Intent intentx = new Intent(context, FileManagerFragmentActivity.class);
					Bundle bundle = new Bundle();
					bundle.putBoolean(WalkMessage.KEY_IS_FROM_REPLAY, true);
					intentx.putExtras(bundle);
					intentx.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intentx);
					intentx = null;

				}
			});

			if (appModel.getSelectScene() == SceneType.Metro || appModel.getSelectScene() == SceneType.HighSpeedRail) {
				datamanage.setVisibility(View.VISIBLE);
			}

			list01.addHeaderView(viewFile);

			list01.setAdapter(adapter);
			list01.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					showReplayFile.dismiss();
					Message msg = Message.obtain();

					msg.arg1 = arg2 - list01.getHeaderViewsCount();
					msg.what = FILEPATH;

					DataModel dataModel = fileList.get(msg.arg1);
					TestRecord testRecord = dataModel.testRecord;
					ArrayList<RecordTestInfo> testInfos = testRecord.getRecordTestInfo();
					if (appModel.getSelectScene() == SceneType.Metro) {// ????????????,???????????????????????????
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
					} else if (appModel.getSelectScene() == SceneType.HighSpeedRail) {// ????????????,??????????????????
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
						String filePath = dataModel.getFilePath(FileType.DDIB.name());
						File file = new File(filePath);
						if (file.exists()) {
							String floorName = file.getParentFile().getName();
							if (floorName.indexOf("_") > 0) {
								InnsmapFactory.getInstance(mContext)
										.setReplayFloorId(floorName.substring(floorName.lastIndexOf("_") + 1));
							}
						}
					}
					handler.sendMessage(msg);
				}
			});

			showReplayFile.setOutsideTouchable(true);
			showReplayFile.setFocusable(true);
			showReplayFile.setTouchable(true);
			showReplayFile.setBackgroundDrawable(new BitmapDrawable());
			showReplayFile.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					dismissAndHidden();
				}
			});
			// showReplayFile.showAtLocation(super.findFocus(),
			// Gravity.BOTTOM|Gravity.LEFT, 0,
			// ReplayFloatView.this.getHeight());
			showReplayFile.showAtLocation(findViewById(R.id.start_stop), Gravity.BOTTOM, 0, -1);
		} else {
			// showReplayFile.showAtLocation(super.findFocus(),
			// Gravity.BOTTOM|Gravity.LEFT, 0,
			// ReplayFloatView.this.getHeight());
			listItem.clear();
			for (int i = 0; i < fileList.size(); i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				DataModel fileModel = fileList.get(i);
				map.put("ItemTitle", fileModel.testRecord.file_name);
				map.put("ItemKey", fileModel.getFilePath(FileType.DDIB.getFileTypeName()));
				listItem.add(map);
			}
			adapter.notifyDataSetChanged();
			showReplayFile.showAtLocation(findViewById(R.id.start_stop), Gravity.BOTTOM, 0, -1);
		}
	}

	/**
	 * ????????????
	 */
	public boolean removeView() {
		if (wm != null) {
			if (mView != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onPlay(final int pointIndexTotal) {
		// Toast.makeText(getContext(), "play replay",
		// Toast.LENGTH_SHORT).show();
		this.post(new Runnable() {
			@Override
			public void run() {
				DatasetManager.isPlaybackLoading = false;
                L3MsgRefreshEventManager.getInstance().notifyL3MsgRefreshed(WalkMessage.traceL3MsgChanged, "");
				if (wm != null) {
					if (mView != null) {
						wm.removeView(mView);
					}
				}
				findViewById(R.id.playProgress).setVisibility(View.GONE);
				if (pointIndexTotal > 0) {
					isPlayIng = true;
					start2Stop.setImageResource(R.drawable.btn_replay_stop);
					// TODO: 2018/6/27 czc
					if (playbackManager.getPlaybackUintType() == PlaybackManager.UINT_FILE) {
						pointEndTime.setText(String.valueOf(pointIndexTotal));
						seekBarA.setMax(pointIndexTotal);
					} else if (playbackManager.getPlaybackUintType() == PlaybackManager.UINT_TIME) {
						pointEndTime.setText(DataSetUtil.parseTimeStr(playbackManager.getTotalTime()));
						seekBarA.setMax(100);
					}

					Intent intent = new Intent(WalkMessage.ACTION_REPLAY_FINISH);
					mContext.sendBroadcast(intent);
					// freezeChange(false);
				} else if (pointIndexTotal == PlaybackManager.NO_FILE_FAIL) {
					Toast.makeText(getContext(), "Replay File No exists,Please check!", Toast.LENGTH_SHORT).show();
				} else {
					playbackManager.onStop();
					Toast.makeText(getContext(), "Open File fail,Please check!", Toast.LENGTH_SHORT).show();
				}
			}
		});
		return false;
	}

	@Override
	public boolean onStop() {
		this.post(new Runnable() {
			@Override
			public void run() {
				LogUtil.d(TAG, "-------onStop-------");
				isPlayIng = false;
				isPause = false;
				start2Stop.setImageResource(R.drawable.btn_repaly_paly);
				// freezeChange(isPause);
			}
		});
		return false;
	}

	@Override
	public boolean onPasue() {
		this.post(new Runnable() {
			@Override
			public void run() {
				start2Stop.setImageResource(R.drawable.btn_repaly_paly);
			}
		});
		return false;
	}

	@Override
	public boolean onFastForward(int speed) {
		currentSpeed.setText(speed + "X");
		return false;
	}

	@Override
	public boolean onRewind(int speed) {
		currentSpeed.setText(speed + "X");
		return false;
	}

	@Override
	public boolean onNext() {
		return false;
	}

	@Override
	public boolean onUp() {
		return false;
	}

	@Override
	public void onSeekBar(int progress) {
		Log.d(TAG, "progress>>>" + progress);
		Message msg = Message.obtain();
		msg.obj = progress;
		msg.arg1 = playbackManager.getPlaybackUintType();
		msg.what = CURRENTTIME;
		handler.sendMessage(msg); // ???????????????
	}

	@Override
	public void onSeekBar(long progressTime,int progress) {
		Log.d(TAG, "progress>>>" + progressTime);
		Message msg = Message.obtain();
		msg.obj = progressTime;
		msg.arg1 = playbackManager.getPlaybackUintType();
		msg.what = CURRENTTIME;
		handler.sendMessage(msg); // ???????????????

	}

	/**
	 * ?????????????????????
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// Toast.makeText(getContext(), "onProgressChanged",
		// Toast.LENGTH_SHORT).show();
		if (fromUser) {
			playbackManager.setSkipIndex(progress,false);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// Toast.makeText(getContext(), "onStartTrackingTouch" +
		// seekBar.getProgress(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// Toast.makeText(getContext(), "onStopTrackingTouch",
		// Toast.LENGTH_SHORT).show();
	}

	/**
	 * ????????????????????????
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(FileExplorer.ACTION_LOAD_NORMAL_FILE)) {
				String filePath = "";
				try {
					filePath = intent.getStringExtra(FileExplorer.KEY_FILE);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (filePath.lastIndexOf(".FMT") != -1 || filePath.lastIndexOf(".rcu") != -1|| filePath.lastIndexOf(".cu") != -1) {
					Intent intentI = new Intent(mContext, ShowDialogTip.class);
					intentI.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(intentI);
				}
				ConverterDdib.getInstance(mContext).doConverterDdib(filePath);
			} else if (intent.getAction().equals(WalkMessage.ACTION_UNIT_NORMAL_RECEIVE)) {
				try {
					// twq0825??????????????????????????????,????????????????????????????????????????????????????????????,?????????,????????????????????????????????????
					String msg = intent.getExtras().getString(WalkMessage.KEY_UNIT_MSG);
					if (msg.startsWith(TestTaskService.MSG_PLAYBACK_INDEXTIMES_SYNC)) {
						String strTime = msg.substring(TestTaskService.MSG_PLAYBACK_INDEXTIMES_SYNC.length());
						if (!strTime.equals("")) {
							long syncTime = Long.parseLong(strTime);
							int currentIndex = DatasetManager.getInstance(mContext).getPointIndexFromTime(syncTime);

							if (currentIndex > 0) {
								playbackManager.setSkipIndex(currentIndex, true);
							}
						}
					} else if (msg.startsWith(TestTaskService.MSG_PLAYBACK_FILE_START)) {
						String fileCreTime = msg.substring(TestTaskService.MSG_PLAYBACK_FILE_START.length());
						if (reFilePath.equals("") && fileCreTime != null && fileCreTime.length() > 0) {
							searchTheNearbyCreateFile(Long.parseLong(fileCreTime));
						}

						runOpenPlay(reFilePath, true);
					} else if (msg.equals(TestTaskService.MSG_PLAYBACK_RESELECT_FILE)) {
						reSelectFile(true);
					} else if (msg.startsWith(TestTaskService.MSG_PLAYBACK_INORDOR)) {
						String order = msg.substring(TestTaskService.MSG_PLAYBACK_INORDOR.length());
						playbackManager.setPlaybackDirection(Integer.parseInt(order));
						setInOrdor(true);
					} else if (msg.startsWith(TestTaskService.MSG_PLAYBACK_SPEED)) {
						String[] speedStr = msg.substring(TestTaskService.MSG_PLAYBACK_SPEED.length()).split("&");
						if (speedStr != null && speedStr.length == 2) {
							int speed = Integer.parseInt(speedStr[0]);
							boolean isRewind = Boolean.parseBoolean(speedStr[1]);

							setSpeed(true, isRewind, speed);
						}
					} else if (msg.equals(TestTaskService.MSG_PLAYBACK_CLOSE)) {
						closeReplay(true);
					}
				} catch (Exception e) {
					LogUtil.e(TAG, "PLACKBACK SYNC INDEXTIME", e);
				}
			}

		}
	};

	/**
	 * ????????????????????????????????????????????????????????????????????????????????????
	 * 
	 * @param creTime
	 */
	private void searchTheNearbyCreateFile(long creTime) {
		currentFilePosition = 0;
		long subTime = Math.abs(creTime - (fileList.get(0).getCreateTime()));
		for (int i = 1; i < fileList.size(); i++) {
			long midTime = Math.abs(creTime - (fileList.get(i).getCreateTime()));
			if (midTime < subTime) {
				subTime = midTime;
				currentFilePosition = i;
			}
		}

		initPlayFileInfo(true);
	}

	/**
	 * view???????????????
	 */
	protected void onDetachedFromWindow() {
		context.unregisterReceiver(mReceiver);
		ConverterDdib.getInstance(mContext).removeObserver(this);
	}

	@Override
	public void doFinish(final int isSuccess) {
		LogUtil.d(TAG, "----doFinish-----");
		this.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getContext(), getContext().getResources().getString(R.string.replay_fmt_load_success),
						Toast.LENGTH_LONG).show();
			}
		});
	}

	/**
	 * ????????????
	 */
	public void desDdibPath(final String path) {
		LogUtil.d(TAG, "-------desDdibPath-------path:" + path);
		this.post(new Runnable() {

			@Override
			public void run() {
				try {
					showPath.setText(path);
					if (DatasetManager.isPlayback) {
						// ????????????????????????,????????????
						playbackManager.onStop();
						seekBarA.setProgress(0);
						pointStartTime.setText(String.valueOf(0));
						pointEndTime.setText("--");
						isPlayIng = false;
						isPause = false;
					}
					runOpenPlay(path);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * ????????????
	 */
	public void desDdibPath(final String path, final int startIndex, final int endIndex) {
		LogUtil.d(TAG, "-------desDdibPath-------path:" + path);
		this.post(new Runnable() {

			@Override
			public void run() {
				try {
					showPath.setText(path);
					if (DatasetManager.isPlayback) {
						// ????????????????????????,????????????
						playbackManager.onStop();
						seekBarA.setProgress(0);
						pointStartTime.setText(String.valueOf(0));
						pointEndTime.setText("--");
						isPlayIng = false;
						isPause = false;
					}
					runOpenPlay(path, startIndex, endIndex);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * ?????????????????? ??????????????????????????????
	 * 
	 * @param ddibPath
	 */
	private void runOpenPlay(final String ddibPath) {
		runOpenPlay(ddibPath, false);
	}

	/**
	 * ?????????????????? ??????????????????????????????
	 * 
	 * @param ddibPath
	 */
	private void runOpenPlay(final String ddibPath, int startIndex, int endIndex) {
		runOpenPlay(ddibPath, false, startIndex, endIndex);
	}

	/**
	 * ??????????????????
	 * 
	 * @param ddibPath
	 */
	private void runOpenPlay(final String ddibPath, boolean isSync) {
		LogUtil.d(TAG, "--runOpenPlay isSync:" + isSync);
		if (isBlueSync(isSync)) {
			UtilsWalktour.sendNormalMsgToBluetooth(mContext,
					TestTaskService.MSG_PLAYBACK_FILE_START + fileList.get(currentFilePosition).getCreateTime());
		}

		if (isPlayIng && !isPause) {
			playbackManager.setIsPasue(true);
			isPause = true;
			// freezeChange(isPause);
		} else if (isPause) {
			playbackManager.setIsPasue(false);
			isPause = false;
			start2Stop.setImageResource(R.drawable.btn_replay_stop);
			// freezeChange(isPause);
		} else {
			playbackManager.setIsPasue(false);
			DatasetManager.isPlaybackLoading = true;
			createDialog();
			findViewById(R.id.playProgress).setVisibility(View.VISIBLE); // ???????????????
			clearAllData();
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (ApplicationModel.getInstance().getSelectScene() == SceneType.Metro)
						NewMapFactory.getInstance().setMapType(NewMapFactory.MAP_TYPE_NONE);
					if (TraceInfoInterface.currentShowTab.equals(WalkStruct.ShowInfoType.Map)
							|| TraceInfoInterface.currentShowTab.equals(WalkStruct.ShowInfoType.Default)) {
						TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Event;
						Intent intent = new Intent(WalkMessage.ACTION_INFO_MAP2EVENT);
						mContext.sendBroadcast(intent);
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					playbackManager.onPlay(ddibPath, 1, 1);
				}
			}).start();
			currentSpeed.setText("1X");
			inordorTxt.setText("-->");
		}
	}

	/**
	 * ??????????????????
	 * 
	 * @param ddibPath
	 */
	private void runOpenPlay(final String ddibPath, boolean isSync, final int startIndex, final int endIndex) {
		LogUtil.d(TAG, "--runOpenPlay isSync:" + isSync);
		if (isBlueSync(isSync)) {
			UtilsWalktour.sendNormalMsgToBluetooth(mContext,
					TestTaskService.MSG_PLAYBACK_FILE_START + fileList.get(currentFilePosition).getCreateTime());
		}

		if (isPlayIng && !isPause) {
			playbackManager.setIsPasue(true);
			isPause = true;
			// freezeChange(isPause);
		} else if (isPause) {
			playbackManager.setIsPasue(false);
			isPause = false;
			start2Stop.setImageResource(R.drawable.btn_replay_stop);
			// freezeChange(isPause);
		} else {
			playbackManager.setIsPasue(false);
			DatasetManager.isPlaybackLoading = true;
			createDialog();
			findViewById(R.id.playProgress).setVisibility(View.VISIBLE); // ???????????????
			clearAllData();
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (ApplicationModel.getInstance().getSelectScene() == SceneType.Metro)
						NewMapFactory.getInstance().setMapType(NewMapFactory.MAP_TYPE_NONE);
					if (TraceInfoInterface.currentShowTab.equals(WalkStruct.ShowInfoType.Map)
							|| TraceInfoInterface.currentShowTab.equals(WalkStruct.ShowInfoType.Default)) {
						TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Event;
						Intent intent = new Intent(WalkMessage.ACTION_INFO_MAP2EVENT);
						mContext.sendBroadcast(intent);
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					playbackManager.onPlay(ddibPath, 1, 1, startIndex, endIndex);
				}
			}).start();
			currentSpeed.setText("1X");
			inordorTxt.setText("-->");
		}
	}

	/**
	 * ?????????????????????????????????
	 * 
	 * @param isSync
	 *          ????????????????????????????????????????????????????????????????????????
	 * @return
	 */
	private boolean isBlueSync(boolean isSync) {
		return (!isSync && ConfigRoutine.getInstance().isBluetoothSync(mContext));
	}

	/**
	 * ??????????????????
	 */
	/*
	 * private void freezeChange(boolean freezeScreen){
	 * appModel.setFreezeScreen(freezeScreen);
	 * 
	 * Intent freezedIntent = new Intent(WalkMessage.ACTION_FREEZE_CHANGED);
	 * freezedIntent.putExtra(WalkMessage.FreezeState, freezeScreen);
	 * getContext().sendBroadcast(freezedIntent); }
	 */

	/**
	 * ??????????????????????????????
	 */
	/*
	 * protected void regeditBroadcast() { IntentFilter filter = new
	 * IntentFilter(); filter.addAction(WalkMessage.ACTION_FREEZE_CHANGED);
	 * 
	 * getContext().registerReceiver(mBroadcastReceiver, filter); }
	 */

	/*
	 * private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
	 * 
	 * @Override public void onReceive(Context context, Intent intent) {
	 * if(intent.getAction().equals(WalkMessage.ACTION_FREEZE_CHANGED) &&
	 * isPlayIng){ if(appModel.isFreezeScreen()){
	 * start2Stop.setImageResource(R.drawable.play_replay); }else{
	 * start2Stop.setImageResource(R.drawable.pause_replay); } } } };
	 */
}
