package com.walktour.gui.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dinglicom.dataset.DatasetManager;
import com.dinglicom.dataset.logic.PointIndexChangeLinstener;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.gui.customView.XMLTextView;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.adapter.L3MsgAdapter;
import com.walktour.control.adapter.L3MsgIndexAdapter;
import com.walktour.control.adapter.L3MsgIndexAdapter.OpenDetailI;
import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.L3MsgRefreshEventManager;
import com.walktour.framework.view.L3MsgRefreshEventManager.L3MsgRefreshEventListener;
import com.walktour.framework.view.colorpicker.ColorPickerDialog;
import com.walktour.gui.R;
import com.walktour.gui.eventbus.OnL3MsgMenuSelectedEvent;
import com.walktour.gui.setting.msgfilter.MsgFilterSettingActivity;
import com.walktour.gui.setting.msgfilter.MsgFilterSettingFactory;
import com.walktour.model.TdL3Model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xwalk.core.XWalkView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

@SuppressLint({"NewApi", "InflateParams"})
public class L3Msg extends BasicActivity implements OnClickListener, PointIndexChangeLinstener, OnItemClickListener, OnScrollListener, OpenDetailI, L3MsgRefreshEventListener {
	private static final String TAG = "L3Msg";

	private static final String tag = "L3Msg";

	private static final int Refresh = 1;

	/**
	 * 记录上一次刷新事件
	 */
//    private long reflashTime = 0;

	/**
	 * 层三信令ListView
	 */
	private ListView l3mgListView;

	/**
	 * 层三事件Adpater
	 */
	private L3MsgAdapter l3MsgAdapter;

	private boolean needToScroll = true; //是否需要滚动到最底端

	//BroadcastReceiver
	private MyBroadcastReceiver mEventReceiver;

	/**
	 * 层三信息详细显示Layout
	 */
	private RelativeLayout infoLayout;

	private Button closeBtn;
	private Button mColorBtn;
	private Button saveBtn;            //保存信令详细解码

	/**
	 * 显示详细信息的视图
	 */
	private WebView infoWeb1;
	private XWalkView infoWeb2;

	private List<TdL3Model> l3msgList;

	private int firstItem = 0;

	private int lastItem = 0;

	private int visibleItemCount = 0;
	private boolean hasResume = false;

	private int preMsgIndex = 0;//上次跳动的采样点
	private int prePosition = 0;//上次跳动的采样点最近事件点index

	private ArrayList<TdL3Model> l3Indexs = new ArrayList<TdL3Model>();


	private boolean isEventIndex = true;
	private boolean isSetText = true;

	private final static String ISAUTOREFRESH = "isAuto";            //shrp


	protected Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case Refresh:
					if (!ApplicationModel.getInstance().isFreezeScreen()) {
						if (!DatasetManager.isPlayback) {
							if (needToScroll && isAutoRefresh) {
								l3MsgAdapter.notifyDataSetChanged();
								l3mgListView.setSelection(l3MsgAdapter.getCount() - 1);
							} else if (!isAutoRefresh) {
								if (needToScroll) {
									l3MsgAdapter.notifyDataSetChanged();
								} else {
									if (l3MsgAdapter.getCount() < 200) {
										l3MsgAdapter.notifyDataSetChanged();
									}
								}
							}
						} else if (DatasetManager.isPlayback && !DatasetManager.isPlaybackLoading) {
							l3MsgAdapter.notifyDataSetChanged();
						}
					}
					break;
				case 0x1001:
					if (selectPopupWindow != null) {
						if (selectPopupWindow.isShowing()) {
							selectPopupWindow.dismiss();
						}
						selectPopupWindow = null;
						if (!isDestroyed()||!isFinishing()) {
							InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
							View currentFocus = L3Msg.this.getCurrentFocus();
							if (currentFocus!=null) {
								manager.hideSoftInputFromWindow(currentFocus
										.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
							}
						}
					} else {
						uploadOptionPop(true);
					}
					break;
			}
		}
	};


	private EditText keyword;

	private LinearLayout searchLny;

	private View layout_option;

	private ListView eventListView;

	private Button eventClose;

	private L3MsgIndexAdapter l3MsgIndexAdapter;

	private ImageView ivDeleteText;

	private PopupWindow selectPopupWindow;

	private View myLayoutView;

	private boolean isFristCreate = true;

	private int viewHeght;

	private L3MsgIndexAdapter eventResultAdapter;

	private Button queryBtn;

	private SharedPreferences preferences;

	private boolean isAutoRefresh;
	private XMLTextView xmlTv;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater layoutInflater = LayoutInflater.from(L3Msg.this);
		myLayoutView = layoutInflater.inflate(R.layout.l3msg_activity, null);
		setContentView(myLayoutView);
		L3MsgRefreshEventManager.getInstance().registerL3MsgRefreshEventListener(this);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		infoLayout = initRelativeLayout(R.id.l3msg_info_layout);
		closeBtn = initButton(R.id.close_btn);
		closeBtn.setOnClickListener(this);
		saveBtn = initButton(R.id.save_btn);
		saveBtn.setOnClickListener(this);
		mColorBtn = initButton(R.id.btn_color);
		mColorBtn.setOnClickListener(this);
		getL3MsgStrList();                                        //获取信令索引列表
		infoWeb1 = (WebView) findViewById(R.id.info_web1);
		infoWeb2 = (XWalkView) findViewById(R.id.info_web2);
		WebSettings settings = infoWeb1.getSettings();
		settings.setDefaultTextEncodingName("UTF-8");
		settings.setSupportZoom(true);
		settings.setTextSize(WebSettings.TextSize.SMALLER);     //增加默认样式设置，确保展开时不会闪屏
		if (Deviceinfo.getInstance().isVivoX23()){//x23的WebView被修改过，不允许加载Webview
//			String l3mDetailFomart = formatXMLToHTML("");
//			infoWeb.loadDataWithBaseURL("", l3mDetailFomart, "text/html", "utf-8", "");
//			findViewById(R.id.xml_scrollview).setVisibility(View.GONE);
			infoWeb1.setVisibility(View.GONE);
			infoWeb2.setVisibility(View.VISIBLE);
		}else {
			infoWeb1.setVisibility(View.VISIBLE);
			infoWeb2.setVisibility(View.GONE);
		}

		//设置webView里字体大小
		l3mgListView = (ListView) findViewById(R.id.l3msg_listview);
//		l3mgListView.setOnItemClickListener(this);
		l3mgListView.setOnScrollListener(this);
		l3msgList = TraceInfoInterface.traceData.l3MsgList;
		initSearchView();
		l3MsgAdapter = new L3MsgAdapter(L3Msg.this, l3msgList);
		l3MsgAdapter.setOnShowDetailListener(new L3MsgAdapter.OnShowDetailListener() {
			@Override
			public void showDetail(View view, int position) {
				hidePopupWindow();
				if (infoLayout.getVisibility() == View.GONE) {
					actionAnimation(View.VISIBLE, R.anim.push_right_in);
				}
				new LoadL3msgDetail().execute(view);

				if (DatasetManager.isPlayback) {
					TdL3Model model = l3MsgAdapter.getItem(position);
					if (model != null) {
						DatasetManager.getInstance(L3Msg.this)
								.getPlaybackManager()
								.setSkipIndex(model.getPointIndex());
					}
				}
			}
		});
//        findView();
		regedit();
		DatasetManager.getInstance(this).addPointIndexChangeListener(this);
		isAutoRefresh = preferences.getBoolean(ISAUTOREFRESH, true);
		findView();
		hasResume = true;
	}
	void loadL3MsgDetail(String text){
		String l3mDetailFomart = formatXMLToHTML(text);
		if (Deviceinfo.getInstance().isVivoX23()){//x23的WebView被修改过，不允许加载Webview
			infoWeb2.loadDataWithBaseURL("", l3mDetailFomart, "text/html", "utf-8", "");
			infoWeb2.scrollTo(0, 0);
		}else{
			infoWeb1.loadDataWithBaseURL("", l3mDetailFomart, "text/html", "utf-8", "");
			infoWeb1.scrollTo(0, 0);
		}
	}

	/**
	 * 信令详细内容搜索，适配三星手机，招标用。后续重新开发完整功能
	 */
//	public class TestFindListener implements android.webkit.WebView.FindListener {
//			
//		private FindListener mFindListener;
//
//		public TestFindListener(FindListener findListener) {
//			mFindListener = findListener;
//		}
//
//		@Override
//		public void onFindResultReceived(int activeMatchOrdinal,
//				int numberOfMatches, boolean isDoneCounting) {
//			mFindListener.onFindResultReceived(activeMatchOrdinal,numberOfMatches, isDoneCounting);
//		}
//	}


	/**
	 * 加载搜索相关操作
	 */
	private void initSearchView() {
		keyword = initEditText(R.id.keyword_edit);
		searchLny = (LinearLayout) findViewById(R.id.LinearLayoutMapSearch);
		//PopupWindow浮动下拉框布局
		layout_option = (View) this.getLayoutInflater().inflate(R.layout.gps_dialog, null);
		eventListView = (ListView) layout_option.findViewById(R.id.mListView);
		eventClose = (Button) layout_option.findViewById(R.id.event_shrink);
		eventClose.setOnClickListener(this);
		l3MsgIndexAdapter = new L3MsgIndexAdapter(getApplicationContext(), l3Indexs, isEventIndex);
		eventListView.setAdapter(l3MsgIndexAdapter);
		l3MsgIndexAdapter.setOnClickListener(this);
		keyword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		keyword.setOnClickListener(this);
		ivDeleteText = (initImageView(R.id.ivDeleteText));
		ivDeleteText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(Event.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);  
				keyword.setText("");
//				 ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(keyword, 0);
				hidePopupWindow();
				l3MsgAdapter.setIsFilterMode(false);
				l3MsgAdapter.notifyDataSetChanged();
			}
		});
		keyword.addTextChangedListener(new TextWatcher() {


			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 0 || TextUtils.isEmpty(s.toString())) {
					hidePopupWindow();
					l3MsgAdapter.setIsFilterMode(false);
					l3MsgAdapter.notifyDataSetChanged();
				} else {
					if (selectPopupWindow != null && !selectPopupWindow.isShowing()) {
						uploadOptionPop(true);
					} else if (selectPopupWindow == null) {
						uploadOptionPop(true);
					}
				}
				if (selectPopupWindow == null) {
					if (isFristCreate == true) {
						viewHeght = myLayoutView.getHeight();
						isFristCreate = false;
					}
//					uploadOptionPop(true);
				}
				if (!isEventIndex) {
					if (!isSetText) {
						isEventIndex = false;
						if (eventResultAdapter == null) {
							eventResultAdapter = new L3MsgIndexAdapter(getApplicationContext(), TraceInfoInterface.traceData.l3MsgList, isEventIndex);
							eventListView.setAdapter(eventResultAdapter);
						} else {
							eventResultAdapter.setEventList(TraceInfoInterface.traceData.l3MsgList, isEventIndex);
							eventListView.setAdapter(eventResultAdapter);
						}
						isSetText = true;
						eventResultAdapter.getFilter().filter(s.toString());

					} else {
						isEventIndex = true;
						if (l3MsgIndexAdapter == null) {
							l3MsgIndexAdapter = new L3MsgIndexAdapter(getApplicationContext(), l3Indexs, isEventIndex);
							l3MsgIndexAdapter.setOnClickListener(L3Msg.this);
							eventListView.setAdapter(l3MsgIndexAdapter);
						} else {
							l3MsgIndexAdapter.setEventList(l3Indexs, isEventIndex);
							eventListView.setAdapter(l3MsgIndexAdapter);
						}
//						eventIndexAdapter.getFilter().filter(s.toString());
					}
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (isEventIndex && isSetText) {
					l3MsgIndexAdapter.getFilter().filter(s.toString());
				}
				if (s.length() == 0) {
					ivDeleteText.setVisibility(View.GONE);
				} else {
					ivDeleteText.setVisibility(View.VISIBLE);
				}

			}
		});
		queryBtn = initButton(R.id.query_btn);
		queryBtn.setOnClickListener(this);
	}


	/**
	 * 获取信令索引集合
	 */
	private void getL3MsgStrList() {
		List<String> l3Str = MsgFilterSettingFactory.getInstance().getl3ModelStrAll();
		for (int i = 0; i < l3Str.size(); i++) {
			TdL3Model tdL3Model = new TdL3Model();
			tdL3Model.setL3Msg(l3Str.get(i));
			l3Indexs.add(tdL3Model);
		}
	}


	/**
	 * 打开下拉框
	 */
	@SuppressWarnings("deprecation")
	private void uploadOptionPop(boolean show_flag) {
		if (show_flag) {
			if (selectPopupWindow != null) {
				if (selectPopupWindow.isShowing()) {
					selectPopupWindow.dismiss();
				}
				selectPopupWindow = null;
			}
			LogUtil.i(tag, "Size: " + isScreenV2G + viewHeght);
			selectPopupWindow = new PopupWindow(layout_option, LayoutParams.FILL_PARENT, ((int) this.getResources().getDimension(R.dimen.panelheight)), true);
			selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());// 设置允许在外点击消失
			selectPopupWindow.showAsDropDown(keyword, 0, 0);
//			selectPopupWindow.setAnimationStyle(R.style.PopupAnimation);
			selectPopupWindow.setFocusable(false);
			selectPopupWindow.update();
		} else {
			if (selectPopupWindow != null) {
				selectPopupWindow.dismiss();
				selectPopupWindow.setFocusable(false);
			}
		}
	}

	class CountScreenHeightThr extends Thread {
		@Override
		public void run() {
			screenHeight(L3Msg.this);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			mHandler.sendEmptyMessage(0x1001);
		}
	}

	private boolean isScreenV2G = false;

	private int screenHeightE = 0;


	/**
	 * 计算键盘高度
	 *
	 * @param activity
	 */
	private void screenHeight(Activity activity) {
		final View screenView = activity.getWindow().getDecorView();
		activity.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				Rect r = new Rect();
				screenView.getWindowVisibleDisplayFrame(r);
				int screenHeight = screenView.getRootView().getHeight();
				int heightDifference = screenHeight - (r.bottom - r.top);
				if (heightDifference > 100) {
					isScreenV2G = true;
					screenHeightE = heightDifference;
				} else {
					isScreenV2G = false;
				}
				LogUtil.d("Keyboard Size", "Size: " + heightDifference);
			}
		});
	}

	/**
	 * 注册广播接收器
	 */
	private void regedit() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(WalkMessage.traceL3MsgChanged);
		filter.addAction(WalkMessage.ACTION_TRACE_RESOLVE_L3MSG_DETAIL_CALLBACK);
		mEventReceiver = new MyBroadcastReceiver();
		this.registerReceiver(mEventReceiver, filter);
	}

	/**
	 * 初始化控件<BR>
	 * [功能详细描述]
	 */
	private void findView() {
		l3mgListView.setAdapter(l3MsgAdapter);
		//l3mgListView.setSelection(TraceInfoDispose.traceData.l3MsgList.size());
		if (!DatasetManager.isPlayback) {
			l3mgListView.setSelection(l3MsgAdapter.getCount() - 1);
		}
	}

	/**
	 * 广播接收器:接收来自Fleet.java的广播更新界面
	 */
	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			//fleet事件
			//LogUtil.w(tag,"--action:"+intent.getAction());
			if (intent.getAction().equals(WalkMessage.traceL3MsgChanged) && !ApplicationModel.getInstance().isFreezeScreen()) {
//                reflashTime = System.currentTimeMillis();
				Message msg = mHandler.obtainMessage(Refresh);
				msg.sendToTarget();
			} else if (WalkMessage.ACTION_TRACE_RESOLVE_L3MSG_DETAIL_CALLBACK.equals(intent.getAction())) {
				String l3msgDetail = intent.getStringExtra("L3MSG");
				LogUtil.d(TAG,l3msgDetail);
				loadL3MsgDetail(l3msgDetail);
			}
		}

	}//end inner class EventBroadcastReceiver


	/**
	 * 把xml文件格式化成html文件格式显示
	 *
	 * @param str xml文件字符串
	 * @return html文件字符串
	 */
	private String formatXMLToHTML(String str) {
		StringBuilder html = new StringBuilder();
		html.append("<html>").append("<body bgcolor=\"#FBFAFA\">").append("<table>");
		if (StringUtil.isNullOrEmpty(str)) {
			html.append("<tr><td>").append("The message can't  be decoded").append("</td></tr>");
		} else {
			try {
				// 创建解析器
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser saxParser = spf.newSAXParser();

				// 设置解析器的相关特性，true表示开启命名空间特性
				XMLContentHandler handler = new XMLContentHandler();
				saxParser.parse(new ByteArrayInputStream(str.getBytes()), handler);
				html.append(handler.getHtml());
			} catch (Exception e) {
				LogUtil.e(tag, e.getMessage());
			}
		}
		html.append("</table>").append("</body>").append("</html>");
		return html.toString();
	}

	/**
	 * SAX类：DefaultHandler，它实现了ContentHandler接口。在实现的时候，只需要继承该类，重载相应的方法即可。
	 *
	 * @author jianchao.wang
	 */
	private class XMLContentHandler extends DefaultHandler {
		/**
		 * 生成的html
		 */
		private StringBuilder html = new StringBuilder();
		/**
		 * 当前等级
		 */
		private int level = 0;

		// 接收元素开始的通知。当读到一个开始标签的时候，会触发这个方法。其中namespaceURI表示元素的命名空间；
		// localName表示元素的本地名称（不带前缀）；qName表示元素的限定名（带前缀）；atts 表示元素的属性集合
		@Override
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
			this.level++;
			if (this.level <= 1)
				return;
			html.append("<tr><td style=\"white-space: nowrap\">");
			for (int i = 0; i < this.level; i++) {
				html.append("&emsp;");
			}
			html.append("<font color=\"#333333\">").append(localName);
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (this.level <= 1)
				return;
			String text = new String(ch, start, length);
			if (!StringUtil.isNullOrEmpty(text)) {
				html.append(":<font>");
				html.append("<font color=\"#019CFF\">").append(text.trim()).append("<font>");
			} else {
				html.append("<font>");
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			this.level--;
			if (this.level <= 1)
				return;
			html.append("</td></tr>");
		}

		public StringBuilder getHtml() {
			return html;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.d(tag, "--->onResume");
		findView();
		hasResume = true;
	}


	@Override
	public void onStart() {
		super.onStart();
		LogUtil.i(tag, "--->onStart");
//        reflashTime = System.currentTimeMillis();
	}

	@Override
	public void onDestroy() {
		if (l3MsgAdapter!=null){
			l3MsgAdapter.clearMark();
		}
		if (mHandler!=null) {
			mHandler.removeMessages(0x1001);
		}
		if(Deviceinfo.getInstance().isVivoX23()) {
			infoWeb2.removeAllViews();
			infoWeb2.onDestroy();
		}
		L3MsgRefreshEventManager.getInstance().removeL3MsgRefreshEventListener(this);

		unregisterReceiver(mEventReceiver);//反注册事件监听
		DatasetManager.getInstance(this).removePointIndexChangeListener(this);
		super.onDestroy();
	}

	private String l3msgDetail = "";

	class LoadL3msgDetail extends AsyncTask<View, Integer, String> {


		@Override
		protected void onPreExecute() {
			findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(View... view) {
			int p = -1;
			try {
				TextView tv = (TextView) view[0].findViewById(R.id.ItemText);
				p = Integer.parseInt(tv.getHint().toString());
				LogUtil.w(tag,
						"----text:" + tv.getText() + "--hint:" + tv.getHint()
								+ "--p:" + p);
			} catch (Exception e) {
				e.printStackTrace();
				p = -1;
			}
			if (p != -1) {

				return DatasetManager.getInstance(getApplicationContext())
						.queryL3Detail(p);
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			findViewById(R.id.loading_progress).setVisibility(View.GONE);
			l3msgDetail = result;
			loadL3MsgDetail(l3msgDetail);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.close_btn:
				actionAnimation(View.GONE, R.anim.push_left_out);
//                infoWeb.setVisibility(View.GONE);
				break;
			case R.id.save_btn:
				saveL3DetailXml(l3msgDetail);
				break;
			case R.id.query_btn:
				l3MsgIndexAdapter.getFilter().filter(keyword.getText().toString());
				l3MsgAdapter.setIsFilterMode(true);
				l3MsgAdapter.getFilter().filter(keyword.getText().toString());
				hidePopupWindow();
				break;

			case R.id.keyword_edit:
				viewHeght = myLayoutView.getHeight();
				new CountScreenHeightThr().start();

				break;
			case R.id.btn_color:
				final ColorPickerDialog colorDialog = new ColorPickerDialog(getParent().getParent(), l3MsgAdapter.getMarkColor());
				colorDialog.setAlphaSliderVisible(false);
				colorDialog.builder.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mColorBtn.setBackgroundColor(colorDialog.getColor());
								l3MsgAdapter.setMarkColor(colorDialog.getColor());
							}
						});
				colorDialog.builder.setNegativeButton(android.R.string.cancel);
				colorDialog.builder.show();
				break;
			default:
				break;
		}
	}


	/**
	 * 让搜索框重新响应输入变化
	 */
	public void enableKeywordChanged() {
		if (searchLny.getVisibility() == View.VISIBLE) {
			viewHeght = myLayoutView.getHeight();
			new CountScreenHeightThr().start();
		}
	}

	/**
	 * 保存信令详细解码
	 */

	private void saveL3DetailXml(String xmlStr) {
		try {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\r");
			stringBuffer.append(xmlStr);
			if (xmlStr == null || xmlStr.trim().length() == 0) {
				Toast.makeText(getApplicationContext(), "Saved L3MsgDetail Fail!", Toast.LENGTH_SHORT).show();
				return;
			}
			File file = AppFilePathUtil.getInstance().createSDCardBaseFile(getString(R.string.path_data), getString(R.string.path_custom)
					, "L3MsgDetail-" + UtilsMethod.sdfhmsss.format(new Date()) + ".xml");
			FileOutputStream out = new FileOutputStream(file);
			out.write(stringBuffer.toString().getBytes());
			out.close();
			Toast.makeText(getApplicationContext(), "The saved " + file.getName() + " to Walktour/Data/export", Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//No call for super(). Bug on API Level > 11.
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 *
	 * @param pointIndex
	 * @param isProgressSkip
	 * @see com.dinglicom.dataset.logic.PointIndexChangeLinstener#onPointIndexChange(int, boolean)
	 */
	@Override
	public void onPointIndexChange(final int pointIndex,
								   final boolean isProgressSkip) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (DatasetManager.isPlayback) {
					if (!DatasetManager.isPlaybackLoading) {
						if (l3MsgAdapter.getCount() > 0) {
							int currentMsgIndex = pointIndex;
							//变色
							l3MsgAdapter.setCurrentPointIndex(currentMsgIndex);
							l3MsgAdapter.notifyDataSetChanged();
							if (hasResume || isProgressSkip) {
								hasResume = false;
								scroolMsgIndex(0, l3MsgAdapter.getCount() - 1, currentMsgIndex, false);
							} else {
								if (pointIndex >= 0 && pointIndex <= 300) {
									l3mgListView.setSelection(0);
								}

								if (currentMsgIndex - preMsgIndex > 0) {
									scrollForward(preMsgIndex, currentMsgIndex);
								} else {
									scrollBackward(preMsgIndex, currentMsgIndex);
								}
							}
							preMsgIndex = currentMsgIndex;
						}
					}
				}
			}
		});
	}


	/**
	 * 检查当前屏幕中可见的item是否在跳动范围内，是就向前滚
	 */
	private void scrollForward(int preMsgIndx, int currentMsgIndex) {
		TdL3Model firstEvent = l3MsgAdapter.getItem(firstItem);
		TdL3Model lastEvent = l3MsgAdapter.getItem(lastItem);
		if (firstEvent != null && lastEvent != null) {
			if (preMsgIndex < lastEvent.getPointIndex()
					&& currentMsgIndex > firstEvent.getPointIndex()) {
				int position = l3MsgAdapter.getCount();
				for (int i = firstItem; i <= l3MsgAdapter.getCount(); i++) {
					TdL3Model event = (TdL3Model) l3MsgAdapter.getItem(i);
					if (event != null && event.getPointIndex() >= currentMsgIndex) {
						position = i;
						break;
					}

				}
				scrollByPostion(position, false, false);
			}

		}
	}

	/**
	 * 检查当前屏幕中可见的item是否在跳动范围内,是就往后滚
	 */
	private void scrollBackward(int preMsgIndx, int currentMsgIndex) {
		TdL3Model firstEvent = l3MsgAdapter.getItem(firstItem);
		TdL3Model lastEvent = l3MsgAdapter.getItem(lastItem);
		if (firstEvent != null && lastEvent != null) {
			if (preMsgIndex > firstEvent.getPointIndex()
					&& currentMsgIndex < lastEvent.getPointIndex()) {
				int position = 0;
				for (int i = prePosition; i >= 0; i--) {
					TdL3Model event = (TdL3Model) l3MsgAdapter.getItem(i);
					if (event != null && event.getPointIndex() <= currentMsgIndex) {
						position = i;
						prePosition = position;
						break;
					}
				}
				scrollByPostion(position, false, true);
			}
		}
	}

	/**
	 * 从from到to遍历当前listView的item,满足  from < msgIndex < to时，跳到msgIndex最近的事件
	 *
	 * @param from
	 * @param to
	 * @param msgIndex
	 */
	private void scroolMsgIndex(int from, int to, int msgIndex, boolean smooth) {
		if (from >= 0 && from < l3MsgAdapter.getCount()
				&& to >= 0 && to < l3MsgAdapter.getCount()) {
			TdL3Model fromEvent = (TdL3Model) l3MsgAdapter.getItem(from);
			TdL3Model toEvent = (TdL3Model) l3MsgAdapter.getItem(to);
			if (fromEvent != null && toEvent != null
					&& fromEvent.getPointIndex() < msgIndex
					&& msgIndex < toEvent.getPointIndex()) {
				int position = 0;
				for (int i = from; i < to; i++) {
					TdL3Model event = (TdL3Model) l3MsgAdapter.getItem(i);
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
		if (position > 0 && position < l3MsgAdapter.getCount()) {
			prePosition = position;
			if (smooth) {
				int scroll = backward ? (position - visibleItemCount / 2) : (position + visibleItemCount / 2);
				scroll = scroll <= l3MsgAdapter.getCount() ? scroll : l3MsgAdapter.getCount();
				scroll = scroll > 0 ? scroll : 0;
				l3mgListView.smoothScrollToPosition(scroll);
			} else {
				int select = position - visibleItemCount / 2;
				select = select > 0 ? select : 0;
				l3mgListView.setSelection(select);
			}
		}
	}

	@Override
	// 添加菜单
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater mInflater = getMenuInflater();
		mInflater.inflate(R.menu.info_l3msg, menu);
		return true;
	}


	@Override
	protected void onPause() {
		LogUtil.d(tag, "----onPause----");
		super.onPause();
		hidePopupWindow();
	}

	/**
	 * 关闭搜索框
	 */
	public void closePopWindow() {
		//关闭PopupWindow
		hidePopupWindow();
		//关闭键盘
		InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(keyword
				.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
	}

	@Override
	// 菜单点击事件
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
			case R.id.msg_seeting:
				Intent intent = new Intent(L3Msg.this, MsgFilterSettingActivity.class);
				startActivity(intent);
				break;
			case R.id.msg_search:
				if (searchLny.getVisibility() == View.GONE) {
					searchLny.setVisibility(View.VISIBLE);
				} else {
					searchLny.setVisibility(View.GONE);
				}
				break;
			case R.id.refresh_seeting:
				showAutoRefreshDialog();
				break;
		}

		return true;
	}

	/**
	 * 接收到查看信息—》更多下拉的menu点击事件（这里接收的事件是由#{{@link SignalActivity}}分发过来的）
	 *
	 * @param event
	 */
	public void onReceiveL2MsgMenuSelectedEvent(OnL3MsgMenuSelectedEvent event) {
		if (null != event) {
			switch (event.getType()) {
				case OnL3MsgMenuSelectedEvent.TYPE_SEARCH:

					if (searchLny.getVisibility() == View.GONE) {
						searchLny.setVisibility(View.VISIBLE);
					} else {
						searchLny.setVisibility(View.GONE);
						uploadOptionPop(false);
						if (!keyword.getText().toString().equals("")) {
							keyword.setText("");
							l3MsgAdapter.setIsFilterMode(false);
							l3MsgAdapter.notifyDataSetChanged();
						}
					}
					break;
				case OnL3MsgMenuSelectedEvent.TYPE_SETTING:
					Intent intent = new Intent(L3Msg.this, MsgFilterSettingActivity.class);
					startActivity(intent);
					break;
				case OnL3MsgMenuSelectedEvent.TYPE_REFRESH_SETTING:
					showAutoRefreshDialog();
					break;
				case OnL3MsgMenuSelectedEvent.TYPE_SAVE_MSG_LIST:
					saveL3MsgListTxt();
					break;
			}
		}
	}

	/**
	 * 保存信令列表
	 */
	private void saveL3MsgListTxt() {
		String fileDir;
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			fileDir = AppFilePathUtil.getInstance().createAppFilesDirectory(getString(R.string.path_l3msg), getString(R.string.path_event_test));
		} else {
			fileDir = AppFilePathUtil.getInstance().createSDCardBaseDirectory(getString(R.string.path_l3msg), getString(R.string.path_event_test));
		}
		// 当前时间
		SimpleDateFormat dateFm = new SimpleDateFormat("yyyyMMdd.HHmmss", Locale.getDefault()); // 格式化当前系统日期
		String time = dateFm.format(new Date(System.currentTimeMillis()));
		StringBuffer sber = new StringBuffer();
		for (int i = 0; i < l3msgList.size(); i++) {
			sber.append(l3msgList.get(i).getL3Msg() + "\n");
		}
		UtilsMethod.WriteFile(fileDir, "test_log" + time + ".txt", sber.toString());
		Toast.makeText(getApplicationContext(), getString(R.string.fleet_saveEvent) + fileDir, Toast.LENGTH_LONG).show();
	}

	/**
	 * 设置信令刷新方式
	 */
	private void showAutoRefreshDialog() {
		LayoutInflater fac = LayoutInflater.from(getApplicationContext());
		View view = fac.inflate(R.layout.sys_routine_setting_l3msg_auto, null);
		final CheckBox autoChx = (CheckBox) view.findViewById(R.id.l3msg_auto);
		autoChx.setChecked(preferences.getBoolean(ISAUTOREFRESH, true));
		autoChx.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				preferences.edit().putBoolean(ISAUTOREFRESH, isChecked).commit();
				isAutoRefresh = isChecked;
				needToScroll = isChecked;
			}
		});
		BasicDialog alert = new BasicDialog.Builder(L3Msg.this.getParent().getParent())
				.setIcon(R.drawable.icon_info)
				.setTitle(R.string.l3msg_refresh)
				.setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.setNegativeButton(R.string.str_cancle).create();
		alert.show();
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		hidePopupWindow();
		if (infoLayout.getVisibility() == View.GONE) {
			actionAnimation(View.VISIBLE, R.anim.push_right_in);
		}
		new LoadL3msgDetail().execute(view);

		if (DatasetManager.isPlayback) {
			TdL3Model model = l3MsgAdapter.getItem(position);
			if (model != null) {
				DatasetManager.getInstance(L3Msg.this)
						.getPlaybackManager()
						.setSkipIndex(model.getPointIndex());
			}
		}

	}

	/**
	 * 消失搜索结果框
	 */
	private void hidePopupWindow() {
		if (selectPopupWindow != null && selectPopupWindow.isShowing()) {
			selectPopupWindow.dismiss();
		}
	}


	/**
	 * 执行动画
	 */

	private void actionAnimation(int visible, int anim) {
		if (visible == View.GONE) {
			if(Deviceinfo.getInstance().isVivoX23()){
				infoWeb2.reload(XWalkView.RELOAD_NORMAL);
				infoWeb2.setVisibility(View.GONE);
			}else {
				infoWeb1.reload();
			}
		}else{
			if(Deviceinfo.getInstance().isVivoX23()){
				infoWeb2.setVisibility(View.VISIBLE);
			}else {
				infoWeb1.setVisibility(View.VISIBLE);
			}
		}
		infoLayout.setVisibility(visible);
		if (!android.os.Build.MODEL.equalsIgnoreCase("m35t")) {
			Animation animation = AnimationUtils.loadAnimation(L3Msg.this, anim);
			infoLayout.startAnimation(animation);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
						 int visibleItemCount, int totalItemCount) {
		firstItem = firstVisibleItem;
		lastItem = firstVisibleItem + visibleItemCount - 1;
		L3Msg.this.visibleItemCount = visibleItemCount;

		if (firstVisibleItem < (totalItemCount - visibleItemCount - 10)) {
			needToScroll = false;
		} else {
			needToScroll = true;
		}

	}

	@Override
	public void onClickView(Object v) {
		if (isEventIndex) {
			isEventIndex = false;
			isSetText = false;
			keyword.setText(v.toString());
			keyword.setSelection(v.toString().length());
			eventResultAdapter.getFilter().filter(v.toString());
			eventResultAdapter.setOnClickListener(this);
//			InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//			manager.hideSoftInputFromWindow(L3Msg.this.getCurrentFocus()
//					.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
//			uploadOptionPop(false);
			hidePopupWindow();
			l3MsgAdapter.setIsFilterMode(true);
			l3MsgAdapter.getFilter().filter(v.toString());
		} else {
			if (DatasetManager.isPlayback) {
				DatasetManager.getInstance(L3Msg.this)
						.getPlaybackManager()
						.setSkipIndex(Integer.valueOf(v.toString()));
			}
		}
	}


	@Override
	public void onL3MsgRefreshed(String actionType, String content) {
		if (actionType.equals(WalkMessage.traceL3MsgChanged) && !ApplicationModel.getInstance().isFreezeScreen()) {
//            reflashTime = System.currentTimeMillis();
			Message msg = mHandler.obtainMessage(Refresh);
			msg.sendToTarget();
		} else if (WalkMessage.ACTION_TRACE_RESOLVE_L3MSG_DETAIL_CALLBACK.equals(actionType)) {
			String l3msgDetail = content;
			loadL3MsgDetail(l3msgDetail);
		}
	}



}