package com.walktour.gui.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.dinglicom.dataset.DatasetManager;
import com.dinglicom.dataset.EventManager;
import com.dinglicom.dataset.PlaybackManager;
import com.dinglicom.dataset.logic.PointIndexChangeLinstener;
import com.dinglicom.dataset.model.EventModel;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.adapter.EventAdapter;
import com.walktour.control.adapter.EventIndexAdapter;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.colorpicker.ColorPickerDialog;
import com.walktour.gui.R;
import com.walktour.gui.eventbus.OnEventMenuSelectedEvent;
import com.walktour.gui.setting.eventfilter.EventFilterSettingActivity;
import com.walktour.gui.setting.eventfilter.EventFilterSettingFactory;
import com.walktour.gui.setting.eventfilter.model.EventFilterSetModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 事件页面
 * 
 * @author qihang.li
 */
@SuppressLint("InflateParams")
public class Event extends BasicActivity implements PointIndexChangeLinstener, EventIndexAdapter.OpenDetailI {

	private static final String TAG = "Event";

	private Context mContext;
	private EventManager mEventMgr = null;
	private EventAdapter adapter = null;
	private ListView listView = null;
	private boolean needToScroll = true; // 是否需要滚动到最底端
	private boolean hasResume = false;
	private int firstItem = 0;
	private int lastItem = 0;
	private int visibleItemCount = 0;

	private int preMsgIndex = 0;// 上次跳动的采样点
	private int prePosition = 0;// 上次跳动的采样点最近事件点index
	private ArrayList<EventModel> eventIndexs = new ArrayList<EventModel>(); // 固定事件索引列表
	private PopupWindow selectPopupWindow;
	private EditText keyword; // 搜索关键字输入
	private Button queryBtn; // 搜索按键

	private LinearLayout searchLny;

	private View layout_option;

	private EventIndexAdapter eventIndexAdapter;

	private EventIndexAdapter eventResultAdapter;

	private View myLayoutView;

	private boolean isEventIndex = true;
	private MyHandler refreshHandler = new MyHandler(this);
	private boolean isSetText = true;
	// private boolean isFristInit = true;

	/**
	 * 获取弹出键盘后的view高度
	 */
	private int viewHeght = 0;

	private Button eventClose;
	private Button mColorBtn;
	private ListView eventListView;

	private ImageView ivDeleteText;

	/** 广播接听类 */
	private MyBroadcastReceiver mEventReceiver;
	private boolean isFristCreate = true;
	/** 一键投诉勾选框集合 */
	private Set<CheckBox> mComplainCheckes = new HashSet<CheckBox>();
	/** 上一次勾选的投诉内容 */
	private String mLastComplainName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		LayoutInflater layoutInflater = LayoutInflater.from(Event.this);
		myLayoutView = layoutInflater.inflate(R.layout.mutily_listview, null);
		setContentView(myLayoutView);
		listView = (ListView) findViewById(R.id.ListView01);
		keyword = initEditText(R.id.keyword_edit);
		searchLny = (LinearLayout) findViewById(R.id.LinearLayoutMapSearch);
		mColorBtn = initButton(R.id.btn_color);
		mColorBtn.setOnClickListener(this);
		// PopupWindow浮动下拉框布局
		layout_option = this.getLayoutInflater().inflate(R.layout.gps_dialog, null);
		getEventStrList();
		eventListView = (ListView) layout_option.findViewById(R.id.mListView);
		eventClose = (Button) layout_option.findViewById(R.id.event_shrink);
		eventClose.setOnClickListener(this);
		eventIndexAdapter = new EventIndexAdapter(mContext, eventIndexs, isEventIndex);
		eventListView.setAdapter(eventIndexAdapter);
		eventIndexAdapter.setOnClickListener(this);
		keyword.setInputType(
				InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		keyword.setOnClickListener(this);
		ivDeleteText = (initImageView(R.id.ivDeleteText));
		ivDeleteText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(Event.this.getCurrentFocus().getWindowToken(),
				//				// InputMethodManager.HIDE_NOT_ALWAYS);
				keyword.setText("");
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(keyword, 0);
			}
		});
		keyword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (selectPopupWindow == null) {
					if (isFristCreate == true) {
						viewHeght = myLayoutView.getHeight();
						isFristCreate = false;
					}
					uploadOptionPop(true);
				}
				if (!isEventIndex) {
					if (!isSetText) {
						isEventIndex = false;
						if (eventResultAdapter == null) {
							eventResultAdapter = new EventIndexAdapter(mContext, EventManager.getInstance().getEventList(),
									isEventIndex);
							eventListView.setAdapter(eventResultAdapter);
						} else {
							eventResultAdapter.setEventList(EventManager.getInstance().getEventList(), isEventIndex);
							eventListView.setAdapter(eventResultAdapter);
						}
						isSetText = true;
						eventResultAdapter.getFilter().filter(s.toString());

					} else {
						isEventIndex = true;
						if (eventIndexAdapter == null) {
							eventIndexAdapter = new EventIndexAdapter(mContext, eventIndexs, isEventIndex);
							eventIndexAdapter.setOnClickListener(Event.this);
							eventListView.setAdapter(eventIndexAdapter);
						} else {
							eventIndexAdapter.setEventList(eventIndexs, isEventIndex);
							eventListView.setAdapter(eventIndexAdapter);
						}
						// eventIndexAdapter.getFilter().filter(s.toString());
					}
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (isEventIndex && isSetText) {
					eventIndexAdapter.getFilter().filter(s.toString());
				}
				if (s.length() == 0) {
					ivDeleteText.setVisibility(View.GONE);
					adapter.setIsFilterMode(false);
					adapter.notifyDataSetChanged();
				} else {
					ivDeleteText.setVisibility(View.VISIBLE);
				}

			}
		});
		queryBtn = initButton(R.id.query_btn);
		queryBtn.setOnClickListener(this);
		mEventMgr = EventManager.getInstance();
		adapter = new EventAdapter(mContext);
		LogUtil.e(TAG, "---onCreate---");
		EventManager.getInstance().regeditEventChangeHandler(refreshHandler);
		this.regedit();
		DatasetManager.getInstance(this).addPointIndexChangeListener(this);
	}

	/**
	 * 获取事件索引集合
	 */
	private void getEventStrList() {
		List<EventFilterSetModel> eventStr = EventFilterSettingFactory.getInstance().getEventList();
		for (EventFilterSetModel eventFilterSetModel : eventStr) {
			EventModel eventModel = new EventModel(0, eventFilterSetModel.getName(), 0);
			eventIndexs.add(eventModel);
		}
	}

	/**
	 * 打开下拉框
	 */
	@SuppressWarnings("deprecation")
	private void uploadOptionPop(boolean show_flag) {
		try {
			if (show_flag) {
				if (selectPopupWindow != null) {
					if (selectPopupWindow.isShowing()) {
						selectPopupWindow.dismiss();
					}
					selectPopupWindow = null;
				}
				Log.i(TAG, "Size: " + isScreenV2G + viewHeght);
				selectPopupWindow = new PopupWindow(layout_option, LayoutParams.FILL_PARENT, ((int) this.getResources().getDimension(R.dimen.panelheight)), true);
				selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());// 设置允许在外点击消失
				selectPopupWindow.showAsDropDown(keyword, 0, 0);
				selectPopupWindow.setAnimationStyle(R.style.PopupAnimation);
				selectPopupWindow.setFocusable(false);
				selectPopupWindow.update();
			} else {
				if (selectPopupWindow != null) {
					selectPopupWindow.dismiss();
					selectPopupWindow.setFocusable(false);
				}
			}
		}catch (Exception ex){
			LogUtil.w(TAG,ex.getMessage());
		}
	}

	/**
	 * 注册广播接收器
	 */
	private void regedit() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(WalkMessage.eventFilterChanged);
		filter.addAction(WalkMessage.ACTION_ADD_TAG);
		mEventReceiver = new MyBroadcastReceiver();
		this.registerReceiver(mEventReceiver, filter);
	}

	/**
	 * 广播接收器:接收来自Fleet.java的广播更新界面
	 */
	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(WalkMessage.eventFilterChanged)
					&& !ApplicationModel.getInstance().isFreezeScreen()) {
				Message msg = null;
				if (DatasetManager.isPlayback)
					msg = refreshHandler.obtainMessage(EventManager.MSG_INDEX_CHANGE);
				else
					msg = refreshHandler.obtainMessage(EventManager.MSG_EVENT_ADD);
				msg.sendToTarget();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);
		TraceInfoInterface.sIsOnEvent = true;
		hasResume = true;
		setView();
		LogUtil.d(TAG, "---onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtil.d(TAG, "---onPause");
		TraceInfoInterface.sIsOnEvent = false;
		if(EventBus.getDefault().isRegistered(this)){
			EventBus.getDefault().unregister(this);
		}

		searchLny.setVisibility(View.GONE);
		if (selectPopupWindow != null) {
			if (selectPopupWindow.isShowing()) {
				selectPopupWindow.dismiss();
			}
			selectPopupWindow = null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.e(TAG, "---onDestroy---");
		EventManager.getInstance().unRegeditEventChangeHandler();
		DatasetManager.getInstance(this).removePointIndexChangeListener(this);
		unregisterReceiver(mEventReceiver);// 反注册事件监听
		InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(keyword
				.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
		uploadOptionPop(false);
	}

	@SuppressLint("ResourceAsColor")
	private void setView() {
		listView.setDivider(new ColorDrawable(Color.GRAY));
		listView.setDividerHeight(1);
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				firstItem = firstVisibleItem;
				lastItem = firstVisibleItem + visibleItemCount - 1;
				// LogUtil.d(tag,"first:"+ firstItem +
				// "select:"+listView.getSelectedItemPosition()+
				// ",last:"+lastItem +"count:"+adapter.getCount() );
				Event.this.visibleItemCount = visibleItemCount;

				if (firstVisibleItem < (totalItemCount - visibleItemCount - 10)) {
					needToScroll = false;
				} else {
					needToScroll = true;
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!ApplicationModel.getInstance().isTesting()) {
					// 2013.10.25下面这个状态可能不准确，上面加个条件限制
					if (DatasetManager.isPlayback) {
						EventModel model = adapter.getItem(position);
						if (model != null) {
							PlaybackManager play = DatasetManager.getInstance(mContext).getPlaybackManager();
							if (play != null) {
								play.setSkipIndex(model.getPointIndex());
							}
						}
					}
				}
			}

		});

		if (!DatasetManager.isPlayback) {
			listView.setSelection(adapter.getCount());
		}

	}

	private static class MyHandler extends Handler {
		private WeakReference<Event> reference;

		public MyHandler(Event event) {
			this.reference = new WeakReference<Event>(event);
		}

		@Override
		public void handleMessage(android.os.Message msg) {
			Event event = this.reference.get();
			switch (msg.what) {
			case EventManager.MSG_EVENT_ADD:
				if (!ApplicationModel.getInstance().isFreezeScreen()) {
					event.adapter.notifyDataSetChanged();
					if (event.needToScroll) {
						event.listView.setSelection(event.adapter.getCount());
					}
				}
				break;

			case EventManager.MSG_EVENT_ADD_ALL:
				event.adapter.notifyDataSetChanged();
				event.listView.setSelection(0);
				break;

			case EventManager.MSG_EVENT_CLEAR:
				event.adapter.notifyDataSetChanged();
				break;

			case EventManager.MSG_INDEX_CHANGE:
				if (!ApplicationModel.getInstance().isFreezeScreen()) {
					if (event.adapter.getCount() > 0) {
						int currentMsgIndex = msg.arg1;
						boolean isProgressSkip = (msg.arg2 == 1);
						// 变色
						event.adapter.setIndexChange(currentMsgIndex);
						event.adapter.notifyDataSetChanged();

						if (event.hasResume || isProgressSkip) {
							event.hasResume = false;
							event.scroolMsgIndex(0, event.adapter.getCount() - 1, currentMsgIndex, false);
						} else {
							if (currentMsgIndex - event.preMsgIndex > 0) {
								event.scrollForward(currentMsgIndex);
							} else {
								event.scrollBackward(currentMsgIndex);
							}
						}
						event.preMsgIndex = currentMsgIndex;
					}
				}
				break;
			case 0x1001:
				if (event.selectPopupWindow != null) {
					if (event.selectPopupWindow.isShowing()) {
						event.selectPopupWindow.dismiss();
					}
					event.selectPopupWindow = null;
					InputMethodManager manager = (InputMethodManager) event.getSystemService(INPUT_METHOD_SERVICE);
					manager.hideSoftInputFromWindow(event.getCurrentFocus().getWindowToken(),
							InputMethodManager.RESULT_UNCHANGED_SHOWN);
				} else {
					event.uploadOptionPop(true);
				}
				break;
			case 0x10001:
				// showAddTagDialog();
				break;

			}
		}
	};

	/** 请求编码 */
	private int requestCode = 10082;
	/** 返回的结果编码 */
	private int resultCode = 10180;

	/**
	 * 检查当前屏幕中可见的item是否在跳动范围内，是就向前滚
	 */
	private void scrollForward(int currentMsgIndex) {
		EventModel firstEvent = adapter.getItem(firstItem);
		EventModel lastEvent = adapter.getItem(lastItem);
		if (firstEvent != null && lastEvent != null) {
			if (preMsgIndex < lastEvent.getPointIndex() && currentMsgIndex > firstEvent.getPointIndex()) {
				int position = adapter.getCount();
				for (int i = firstItem; i <= adapter.getCount(); i++) {
					EventModel event = adapter.getItem(i);
					if (event != null && event.getPointIndex() >= currentMsgIndex) {
						position = i;
						break;
					}

				}
				scrollByPostion(position, true, false);
			}

		}
	}

	/**
	 * 检查当前屏幕中可见的item是否在跳动范围内,是就往后滚
	 */
	private void scrollBackward(int currentMsgIndex) {
		EventModel firstEvent = adapter.getItem(firstItem);
		EventModel lastEvent = adapter.getItem(lastItem);
		if (firstEvent != null && lastEvent != null) {
			if (preMsgIndex > firstEvent.getPointIndex() && currentMsgIndex < lastEvent.getPointIndex()) {
				int position = 0;
				for (int i = prePosition; i >= 0; i--) {
					EventModel event = adapter.getItem(i);
					if (event != null && event.getPointIndex() <= currentMsgIndex) {
						position = i;
						prePosition = position;
						break;
					}
				}
				scrollByPostion(position, true, true);
			}
		}
	}

	/**
	 * 从from到to遍历当前listView的item,满足 from < msgIndex < to时，跳到msgIndex最近的事件
	 * 
	 * @param from
	 * @param to
	 * @param msgIndex
	 */
	private void scroolMsgIndex(int from, int to, int msgIndex, boolean smooth) {
		if (from >= 0 && from < adapter.getCount() && to >= 0 && to < adapter.getCount()) {
			EventModel fromEvent = adapter.getItem(from);
			EventModel toEvent = adapter.getItem(to);
			if (fromEvent != null && toEvent != null && fromEvent.getPointIndex() < msgIndex
					&& msgIndex < toEvent.getPointIndex()) {
				int position = 0;
				for (int i = from; i < to; i++) {
					EventModel event = adapter.getItem(i);
					if (event != null && event.getPointIndex() > msgIndex) {
						position = i;
						break;
					}
				}
				scrollByPostion(position, smooth, false);
			}
		}
	}

	/**
	 * 函数功能：跳到指定的item
	 * 
	 * @param position
	 */
	private void scrollByPostion(int position, boolean smooth, boolean backward) {
		if (position > 0 && position < adapter.getCount()) {
			prePosition = position;
			if (smooth) {
				int scroll = backward ? (position - visibleItemCount / 2) : (position + visibleItemCount / 2);
				scroll = scroll <= adapter.getCount() ? scroll : adapter.getCount();
				scroll = scroll > 0 ? scroll : 0;
				listView.smoothScrollToPosition(scroll);
			} else {
				int select = position - visibleItemCount / 2;
				select = select > 0 ? select : 0;
				listView.setSelection(select);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// No call for super(). Bug on API Level > 11.
		LogUtil.e(TAG, "---onSaveInstanceState");
	}

	/**
	 * 回放时采样点变化
	 * 
	 * @param pointIndex
	 *          当前采样点
	 * @param isProgressSkip
	 *          是否从进度条触发的采样点跳转
	 */
	@Override
	public void onPointIndexChange(int pointIndex, boolean isProgressSkip) {
		if (DatasetManager.isPlayback) {
			LogUtil.e(TAG, "---onPointIndexChange:" + pointIndex);
			refreshHandler.obtainMessage(EventManager.MSG_INDEX_CHANGE, pointIndex, isProgressSkip ? 1 : 0).sendToTarget();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater mInflater = getMenuInflater();
		mInflater.inflate(R.menu.fleet_event, menu);
		return true;
	}

	/**
	 * 接收查看信息-》更多下拉的menu点击事件
	 * @param event
	 */
	@Subscribe
	public void onReceiveEventMenuSelectedEvent(OnEventMenuSelectedEvent event){
		if(null != event){
			switch (event.getType()){
				case OnEventMenuSelectedEvent.TYPE_SEARCH:
					if (searchLny.getVisibility() == View.GONE) {
						searchLny.setVisibility(View.VISIBLE);
					} else {
						searchLny.setVisibility(View.GONE);
						uploadOptionPop(false);
					}
					break;
				case OnEventMenuSelectedEvent.TYPE_CLEAR_TEXT:
					mEventMgr.clearEvents();
					break;
				case OnEventMenuSelectedEvent.TYPE_SAVE:
					saveEvent();
					break;
				case OnEventMenuSelectedEvent.TYPE_ADD_LABEL:
					showAddTagDialog();
					break;
				case OnEventMenuSelectedEvent.TYPE_FLEET_COMPLAIN:
					showComplainDialog();
					break;
				case OnEventMenuSelectedEvent.TYPE_SETTING:
					Intent intent = new Intent(Event.this, EventFilterSettingActivity.class);
					startActivityForResult(intent, requestCode);
					break;
			}
		}
	}

	@Override // 菜单点击事件
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		// 清除事件
		case R.id.menu_fleet_clearEvent:
			mEventMgr.clearEvents();
			break;

		// 保存事件
		case R.id.menu_fleet_save:
			saveEvent();
			break;

		case R.id.menu_fleet_addlabel:
			showAddTagDialog();
			break;
		case R.id.menu_fleet_complain:
			showComplainDialog();
			break;
		case R.id.menu_fleet_eventSearch:
			if (searchLny.getVisibility() == View.GONE) {
				searchLny.setVisibility(View.VISIBLE);
			} else {
				searchLny.setVisibility(View.GONE);
			}
			break;

		case R.id.menu_event_seeting:
			Intent intent = new Intent(Event.this, EventFilterSettingActivity.class);
			startActivityForResult(intent, requestCode);
			break;

		}

		return true;
	}

	/**
	 * 初始化一键投诉的勾选框
	 * 
	 * @param view
	 *          控件
	 * @param checkBoxId
	 *          勾选框ID
	 * @param stringId
	 *          文本ID
	 * @param listener
	 *          勾选监听类
	 */
	private void initComplainCheckBox(View view, int checkBoxId, int stringId, OnCheckedChangeListener listener) {
		CheckBox checkBox = (CheckBox) view.findViewById(checkBoxId);
		checkBox.setTag(stringId);
		checkBox.setOnCheckedChangeListener(listener);
		this.mComplainCheckes.add(checkBox);
	}

	/**
	 * 显示一键投诉的对话框
	 */
	private void showComplainDialog() {
		LayoutInflater fac = LayoutInflater.from(getApplicationContext());
		View view = fac.inflate(R.layout.alert_dialog_complain, null);
		OnCheckedChangeListener listener = new ComplainOnCheckedChangeListener();
		this.initComplainCheckBox(view, R.id.complain_singlePass_check, R.string.complain_singlePass, listener);
		this.initComplainCheckBox(view, R.id.complain_crossTalk_check, R.string.complain_crossTalk, listener);
		this.initComplainCheckBox(view, R.id.complain_echo_check, R.string.complain_echo, listener);
		this.initComplainCheckBox(view, R.id.complain_mute_check, R.string.complain_mute, listener);
		this.initComplainCheckBox(view, R.id.complain_noise_check, R.string.complain_noise, listener);
		this.initComplainCheckBox(view, R.id.complain_voiceInterruption_check, R.string.complain_voiceInterruption,
				listener);
		this.initComplainCheckBox(view, R.id.complain_voiceVague_check, R.string.complain_voiceVague, listener);
		this.initComplainCheckBox(view, R.id.complain_volumeSmall_check, R.string.complain_volumeSmall, listener);
		final long lableTime = System.currentTimeMillis();
		BasicDialog alert = new BasicDialog.Builder(Event.this.getParent()).setIcon(android.R.drawable.ic_menu_edit)
				.setTitle(R.string.complain_title).setView(view)
				.setPositiveButton(R.string.str_save, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (!StringUtil.isNullOrEmpty(mLastComplainName)) {
							mEventMgr.addTagEvent(mContext, lableTime, mLastComplainName);
						}
						mComplainCheckes.clear();
					}
				}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mComplainCheckes.clear();
					}
				}).create();
		alert.show();
	}

	/**
	 * 一键投诉类型勾选监听类
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class ComplainOnCheckedChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			for (CheckBox check : mComplainCheckes) {
				check.setChecked(false);
			}
			if (isChecked) {
				int complainId = (Integer) buttonView.getTag();
				Locale locale = Locale.getDefault();
				String language = locale.getLanguage() + "_" + locale.getCountry();
				LogUtil.d(TAG, "----locale:" + language + "----");
				setLocaleLanguage(Locale.ENGLISH);
				mLastComplainName = getString(complainId);
				setLocaleLanguage(locale);
				((CheckBox) buttonView).setChecked(true);
			} else {
				mLastComplainName = "";
			}
			LogUtil.d(TAG, "----mLastComplainName:" + mLastComplainName + "----");
		}

	}

	/**
	 * 设置当前的语言
	 * 
	 * @param locale
	 *          当前语言
	 */
	private void setLocaleLanguage(Locale locale) {
		Configuration config = getResources().getConfiguration();
		config.locale = locale;
		getResources().updateConfiguration(config, null);
	}

	/**
	 * 显示增加事件标注
	 */
	private void showAddTagDialog() {
		LayoutInflater fac = LayoutInflater.from(getApplicationContext());
		View view = fac.inflate(R.layout.alert_dialog_edittext12, null);
		final EditText edittext = (EditText) view.findViewById(R.id.alert_textEditText);
		final long lableTime = System.currentTimeMillis();
		BasicDialog alert = new BasicDialog.Builder(Event.this.getParent()).setIcon(android.R.drawable.ic_menu_edit)
				.setTitle(R.string.info_add_label).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String eventStr = edittext.getText().toString().trim();
						if (eventStr == null || eventStr.equals("")) {

						} else {
							mEventMgr.addTagEvent(mContext, lableTime, eventStr);
						}
					}
				}).setNegativeButton(R.string.str_cancle).create();
		alert.show();
	}

	/**
	 * 保存事件文件
	 */
	private void saveEvent(){
		String fileDir;
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			fileDir = AppFilePathUtil.getInstance().createAppFilesDirectory(getString(R.string.path_event),getString(R.string.path_event_test));
		} else {
			fileDir = AppFilePathUtil.getInstance().createSDCardBaseDirectory(getString(R.string.path_event),getString(R.string.path_event_test));
		}
		// 当前时间
		SimpleDateFormat dateFm = new SimpleDateFormat("yyyyMMdd.HHmmss", Locale.getDefault()); // 格式化当前系统日期
		String time = dateFm.format(new Date(System.currentTimeMillis()));
		StringBuffer sber = new StringBuffer();
		for (int i = 0; i < mEventMgr.getEventList().size(); i++) {
			sber.append(mEventMgr.getEventList().get(i).getEventStr() + "\n");
		}
		UtilsMethod.WriteFile(fileDir, "test_log" + time + ".txt", sber.toString());
		Toast.makeText(getApplicationContext(), getString(R.string.fleet_saveEvent) + fileDir, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.query_btn:
			eventIndexAdapter.getFilter().filter(keyword.getText().toString());
			adapter.setIsFilterMode(true);
			adapter.getFilter().filter(keyword.getText().toString());
			if (selectPopupWindow != null) {
				if (selectPopupWindow.isShowing()) {
					selectPopupWindow.dismiss();
				}
			}
			hideSoft();
			break;

		case R.id.keyword_edit:
			viewHeght = myLayoutView.getHeight();
			new CountScreenHeightThr().start();

			break;

		case R.id.event_shrink:
			if (selectPopupWindow != null) {
				if (selectPopupWindow.isShowing()) {
					selectPopupWindow.dismiss();
				}
				selectPopupWindow = null;
			}
			hideSoft();
			break;
		case R.id.btn_color:
			showColorDialog();
				break;
		default:
			break;
		}
	}

	private void showColorDialog() {
		final ColorPickerDialog colorDialog = new ColorPickerDialog(getParent().getParent(), adapter.getMarkColor());
		colorDialog.setAlphaSliderVisible(false);
		colorDialog.builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mColorBtn.setBackgroundColor(colorDialog.getColor());
						adapter.setMarkColor(colorDialog.getColor());
					}
				});
		colorDialog.builder.setNegativeButton(android.R.string.cancel);
		colorDialog.builder.show();
	}

	private void hideSoft() {
		InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(Event.this.getCurrentFocus().getWindowToken(),
				InputMethodManager.RESULT_UNCHANGED_SHOWN);
	}

	int[] s = new int[0];

	class CountScreenHeightThr extends Thread {
		@Override
		public void run() {
			synchronized (s) {
				try {
					screenHeight(Event.this);
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				refreshHandler.sendEmptyMessage(0x1001);
			}
		}
	}

	@Override
	public void onClickView(Object v) {
		if (isEventIndex) {
			try {
				isEventIndex = false;
				isSetText = false;
				keyword.setText(v != null ? v.toString() : "");
				keyword.setSelection(v != null ? v.toString().length() :0);
				eventResultAdapter.getFilter().filter(v != null ? v.toString() : "");
				eventResultAdapter.setOnClickListener(this);
				adapter.setIsFilterMode(true);
				adapter.getFilter().filter(keyword.getText().toString());
				hideSoft();
				uploadOptionPop(false);
			}catch (Exception ex){
				LogUtil.w(TAG,ex.getMessage());
			}
		} else {
			if (DatasetManager.isPlayback) {
				DatasetManager.getInstance(Event.this).getPlaybackManager().setSkipIndex(Integer.valueOf(v.toString()));
			}
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			super.closeOptionsMenu();
			super.openOptionsMenu();
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == this.requestCode && resultCode == this.resultCode) {
			this.adapter.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private boolean isScreenV2G = false;

	// private int screenHeightE = 0;

	/**
	 * 计算键盘高度
	 * 
	 * @param activity
	 */
	private void screenHeight(Activity activity) {
		final View screenView = activity.getWindow().getDecorView();
		activity.getWindow().getDecorView().getViewTreeObserver()
				.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						Rect r = new Rect();
						screenView.getWindowVisibleDisplayFrame(r);
						int screenHeight = screenView.getRootView().getHeight();
						int heightDifference = screenHeight - (r.bottom - r.top);
						if (heightDifference > 100) {
							isScreenV2G = true;
							// screenHeightE = heightDifference;
						} else {
							isScreenV2G = false;
						}
						Log.d("Keyboard Size", "Size: " + heightDifference);
					}
				});
	}
}
