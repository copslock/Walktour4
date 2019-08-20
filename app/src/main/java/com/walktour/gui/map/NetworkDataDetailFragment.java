package com.walktour.gui.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.walktour.Utils.FileUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.R;
import com.walktour.model.Element;

import org.xwalk.core.XWalkView;

import java.io.File;
import java.util.ArrayList;

/**
 * 抓包详情显示
 *
 */
public class NetworkDataDetailFragment extends Fragment implements OnClickListener{

	private WebView mWebView1;
	private XWalkView mWebView2;
	private ArrayList<Element> elements = new ArrayList<Element>();

	private String tcpFileName="tcpipdetail.html";
	private String tcpFilePath="";

	public ArrayList<Element> getElements() {
		return elements;
	}

	public void setElements(ArrayList<Element> elements) {
		this.elements = elements;
		show();
		if(Deviceinfo.getInstance().isVivoX23()){
			mWebView2.reload(XWalkView.RELOAD_NORMAL);
		}else {
			mWebView1.reload();
		}
	}

	/**
	 * show detail
	 * */
	private void show() {
		StringBuilder builder = new StringBuilder();
		builder.append("<body bgcolor=\"#FBFAFA\">"); 
		for (int i = 0; i < this.elements.size(); i++) {
			Element element = this.elements.get(i);
			builder.append("<p style=\"white-space:nowrap;\">");
			String content = elements.get(i).getContentText();
			if (element.getLevel() == 0) {
				content = getSpace(element.getLevel()) + "<font color=\"#333333\">" + content + "</font>";
			} else {
				content = getSpace(element.getLevel()) + changeTextColor(content);
			}
			builder.append(content);
		}
		builder.append("</p>");
		builder.append("</body>");
		Log.d("DDD", "builder:" + builder.toString());
		tcpFilePath=AppFilePathUtil.getInstance().getSDCardBaseDirectory()+ File.separator+tcpFileName;
		FileUtil.writeToFile(new File(tcpFilePath),builder.toString());
		if (Deviceinfo.getInstance().isVivoX23()){
			mWebView2.loadUrl("file:///"+tcpFilePath );
		}else {
			mWebView1.loadUrl("file:///"+tcpFilePath );
		}
		builder.setLength(0);
		builder=null;
	}

	/**
	 * 处理缩进
	 * */
	private String getSpace(int level) {
		int  baseSpaceNum = 5;
		String space = "";
		int size  = level*baseSpaceNum;
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < size; i++) {
			String s = "&nbsp;";
			builder.append(s);
		}
		space = builder.toString();
		return space;
	}
	/**
	 * 处理字体颜色
	 * <font color="#0DAEF4">我是蓝色字体</font> 
	 * */
	private String changeTextColor(String original) {
		StringBuilder sb = new StringBuilder();
		if (original.contains(":")) {
			String[] tmps = original.split(":");
			sb.append("<font color=\"#333333\">");
			sb.append(tmps[0]);
			sb.append(":");
			sb.append("</font>");
			sb.append("<font color=\"#019CFF\">");
			sb.append(tmps[1]);
			sb.append("</font>");
		}
		return sb.toString();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.wireshark_detail_view, null);
		initView(rootView);
		show();
		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	 
	
	private void initView(View v) {
		mWebView1 = (WebView)v.findViewById(R.id.webview_detail1);
		mWebView1.getSettings().setMinimumFontSize(10);
		mWebView2 = (XWalkView)v.findViewById(R.id.webview_detail2);
		v.findViewById(R.id.btn_close).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_close) {
			closeFragment();
		}
	}
	
	private void closeFragment() {
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		ft.hide(this).commit();
	}
}
