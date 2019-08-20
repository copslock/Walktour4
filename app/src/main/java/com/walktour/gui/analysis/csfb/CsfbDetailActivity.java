package com.walktour.gui.analysis.csfb;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.control.VoiceAnalyse;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.InfoTabHost;
import com.walktour.gui.R;
import com.walktour.gui.analysis.model.AnalysisModel;
import com.walktour.gui.newmap2.NewInfoTabActivity;
import com.walktour.model.CsfbFaildEventModel;
import com.walktour.model.VoiceFaildModel;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/***
 * CSFB 数据分析详细界面
 * 
 * @author weirong.fan
 *
 */
public class CsfbDetailActivity extends BasicActivity implements OnClickListener, CsfbDetailAdapter.OpenDetailI {

	private ListView listView;
	private List<CsfbFaildEventModel> list;
	private TextView netTypeTxt;
	private TextView errTxt;
	private TextView timeTxt;
	private TextView title;
	private ImageView pointer;
	private VoiceFaildModel csfbFaildModel = null;
	private String para;
	private View txtView;
	private LinearLayout addPara_Ly;
	private HashMap<String, ArrayList<VoiceFaildModel>> csfbMap;
	private CsfbDetailAdapter csfbDetailAdapter;
	private RelativeLayout infoLayout;
	private Button close_btn;
	private WebView infoWeb;
	private int faildType;
	private VoiceAnalyse csfbFaild; 
	private String exceptionTime = "";
	private AnalysisModel model;

	@Override
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.csfb_main);
		this.initImageButton(R.id.replaybtn).setOnClickListener(this);
		getIntentData();
		title = initTextView(R.id.title_txt);
		title.setText(String.format(getString(R.string.csfb_faild_detail_title), faildType == VoiceAnalyse.FAILD_TYPE_CSFB ? "CSFB" : "VoLTE"));
		pointer = initImageView(R.id.pointer);
		pointer.setOnClickListener(this);
		netTypeTxt = initTextView(R.id.netTxt);
		errTxt = initTextView(R.id.errTxt);
		timeTxt = initTextView(R.id.timeTxt);
		listView = (ListView) findViewById(R.id.list_id);
		infoLayout = initRelativeLayout(R.id.l3msg_info_layout);
		close_btn = initButton(R.id.close_btn);
		close_btn.setOnClickListener(this);
		addPara_Ly = (LinearLayout) findViewById(R.id.addPara_Ly);
		if (getBuildData()) {
			setDataValue();
			infoWeb = (WebView) findViewById(R.id.info_web);
			WebSettings settings = infoWeb.getSettings();
			settings.setDefaultTextEncodingName("UTF-8");
			settings.setSupportZoom(true);
			settings.setTextSize(WebSettings.TextSize.SMALLER);
			String l3mDetailFomart = formatXMLToHTML("");
			infoWeb.loadDataWithBaseURL("", l3mDetailFomart, "text/html", "utf-8", "");
			csfbDetailAdapter = new CsfbDetailAdapter(this, list);
			csfbDetailAdapter.setOnClickListener(this);
			listView.setAdapter(csfbDetailAdapter);
		} else {
			ToastUtil.showToastShort(this, "请检查数据...");
		}
	}

	/**
	 * 获取itent携带过来数据
	 */
	private void getIntentData() {
		Bundle bundle = getIntent().getExtras();
		model = (AnalysisModel) bundle.get("map"); 
		exceptionTime = bundle.getString("exceptionTime"); // 获取异常发生的时间
		faildType = bundle.getInt("FAILD_TYPE", 0);
	}

	/**
	 * 设置数据值
	 */
	@SuppressWarnings("deprecation")
	private void setDataValue() {
		if (null != csfbFaildModel) {
			netTypeTxt.setText(Html.fromHtml(getString(R.string.csfb_faild_network) + ": " + "<font color = #2A99C8>" + csfbFaildModel.getFaildNetType() + "</font>"));
			timeTxt.setText(exceptionTime.split(" ")[1]);
//			timeTxt.setText(csfbFaildModel.getFaildTime());
			errTxt.setText(Html.fromHtml("<font color = white>" + ": " + "</font>" + "<font color = #2A99C8>" + csfbFaildModel.getCsfbFaildStrDec() + "</font>"));
			para = csfbFaildModel.getFaildParamValues();
			if (para.trim().length() != 0) {
				String[] paraStr = para.split("@@");
				LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				for (int i = 0; i < paraStr.length; i += 2) {
					txtView = inflater.inflate(R.layout.csfb_para_item, null);
					TextView text1 = (TextView) txtView.findViewById(R.id.csfb_txt_1);
					TextView text2 = (TextView) txtView.findViewById(R.id.csfb_txt_2);
					text1.setTextColor(getResources().getColor(R.color.csfb_txt_color));
					text2.setTextColor(getResources().getColor(R.color.csfb_txt_color));
					text1.setText(getColorBuilder(paraStr[i]));
					if (i + 1 < paraStr.length) {
						text2.setText(getColorBuilder(paraStr[i + 1]));
					}
					addPara_Ly.addView(txtView);
				}
			}
		}
	}

	/**
	 * 设置自定义前景色
	 * 
	 * @param startIndex
	 * @param toIndex
	 * @param str
	 */
	private SpannableStringBuilder getColorBuilder(String str) {
		SpannableStringBuilder builder = null;
		try {
			builder = new SpannableStringBuilder(str);
			ForegroundColorSpan parmSpan = new ForegroundColorSpan(getResources().getColor(R.color.info_param_color));
			ForegroundColorSpan whiteSpan = new ForegroundColorSpan(getResources().getColor(R.color.app_main_text_color));
			builder.setSpan(whiteSpan, 0, str.indexOf(":") + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			builder.setSpan(parmSpan, str.indexOf(":") + 2, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		} catch (Exception e) {
			e.printStackTrace();
			return builder;
		}
		return builder;
	}

	/**
	 * 加载原始数据
	 */
	private boolean getBuildData() {
		csfbFaild = VoiceAnalyse.getInstance(getApplicationContext());
		csfbMap = csfbFaild.getTransmissionDataMap();
		if (csfbMap != null) {
			Iterator it = csfbMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry resultMap = (Map.Entry) it.next();
				if (resultMap.getKey().equals(model.getExceptionInfo())) {
					ArrayList<VoiceFaildModel> listModel = csfbMap.get(model.getExceptionInfo());
					for (VoiceFaildModel m : listModel) {
						csfbFaildModel = m;
						list = m.getCsfbFaildEventArray();
						if (exceptionTime.endsWith(m.getFaildTime())) {
//							csfbFaildModel = m;
							list = m.getCsfbFaildEventArray();
							return true;
						}
					}

				}

			}

		}
		return true;
	}

	class LoadL3msgDetail extends AsyncTask<View, Integer, String> {

		@Override
		protected void onPreExecute() {
			findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(View... view) {
			String p = "";
			try {
				ImageView tv = (ImageView) view[0].findViewById(R.id.csfb_open_l3detail);
				p = tv.getTag().toString();
				LogUtil.w("CSFB", "--p:" + p);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!p.equals("null")) {

				return p;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			findViewById(R.id.loading_progress).setVisibility(View.GONE);
			String l3msgDetail = result;
			String l3mDetailFomart = formatXMLToHTML(l3msgDetail);
			infoWeb.loadDataWithBaseURL("", l3mDetailFomart, "text/html", "utf-8", "");
			infoWeb.scrollTo(0, 0);
		}
	}

	/**
	 * 把xml文件格式化成html文件格式显示
	 * 
	 * @param str
	 *            xml文件字符串
	 * @return html文件字符串
	 */
	private String formatXMLToHTML(String str) {
		StringBuilder html = new StringBuilder();
		html.append("<html>").append("<body bgcolor=\"#E6E6E6\">").append("<table>");
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
				e.printStackTrace();
			}
		}
		html.append("</table>").append("</body>").append("</html>");
		return html.toString();
	}

	/**
	 * SAX类：DefaultHandler，它实现了ContentHandler接口。在实现的时候，只需要继承该类，重载相应的方法即可。
	 * 
	 * @author jianchao.wang
	 * 
	 */
	private class XMLContentHandler extends DefaultHandler {
		/** 生成的html */
		private StringBuilder html = new StringBuilder();
		/** 当前等级 */
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
				html.append("<font color=\"#15b6f9\">").append(text.trim()).append("<font>");
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

	/**
	 * 按钮监听
	 * 
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pointer:
			finish();
			break;
		case R.id.replaybtn:
			if (null != model) {// 回放事件
				Intent intent = new Intent(this, NewInfoTabActivity.class);
				if (TraceInfoInterface.currentShowTab == WalkStruct.ShowInfoType.Map || TraceInfoInterface.currentShowTab == WalkStruct.ShowInfoType.Default)
					intent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_MAP);
				else
					intent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_OTHER);
				intent.putExtra("isReplay", true);
				intent.putExtra("filePath", model.getDdibFile());
				intent.putExtra("isReplayNow", true);
				intent.putExtra("startIndex", model.getStartIndex());
				intent.putExtra("endIndex", model.getEndIndex());
				startActivity(intent);
				overridePendingTransition(R.anim.open_next, R.anim.close_main);
			}
			break;
		case R.id.csfb_open_l3detail:
			if (v.getTag().equals("")) {
				Toast.makeText(getApplicationContext(), "Not decoded", Toast.LENGTH_SHORT).show();
				return;
			}
			if (infoLayout.getVisibility() == View.GONE) {
				actionAnimation(View.VISIBLE, R.anim.push_right_in);
				new LoadL3msgDetail().execute(v);
			}

			break;
		case R.id.close_btn:
			actionAnimation(View.GONE, R.anim.push_left_out);
			break;

		default:
			break;
		}
	}

	/**
	 * 捕捉当层三详细解码显示的时候，先关闭详细解码界面
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (infoLayout.getVisibility() == View.VISIBLE) {
				actionAnimation(View.GONE, R.anim.push_left_out);
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 执行动画
	 */

	private void actionAnimation(int visible, int anim) {
		infoLayout.setVisibility(visible);
		if (!android.os.Build.MODEL.equalsIgnoreCase("m35t")) {
			Animation animation = AnimationUtils.loadAnimation(CsfbDetailActivity.this, anim);
			infoLayout.startAnimation(animation);
		}
	}
 
}
