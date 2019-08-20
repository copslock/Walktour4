package com.walktour.gui.report;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.walktour.base.util.LogUtil;
import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;

import org.xwalk.core.XWalkView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 报表预览界面
 * 
 * @author jianchao.wang
 *
 */
public class ReportPreviewActivity extends BasicActivity {
	/** 传递参数名：报表文件的绝对路径 */
	public static final String EXTRA_FILE_PATH = "file_path";
	private static final String TAG = "ReportPreviewActivity";
	/** 报表工厂类 */
	private ReportFactory mFactory;
	/** web视图 */
	private WebView webView1;

	private XWalkView webView2;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_report_preview);
		this.mFactory = ReportFactory.getInstance(this);
		String filePath = this.getIntent().getStringExtra(EXTRA_FILE_PATH);
		TextView title = initTextView(R.id.title_txt);
		title.setText(getString(R.string.total_report_file_review_title));
		if (filePath == null)
			this.finish();
		this.mFactory.createHtmlFile(filePath);
		if (this.mFactory.getHtmlFile() == null)
			this.finish();
		findView();
	}

	/**
	 * 视图解析
	 */
	private void findView() {
		webView1 = (WebView) this.findViewById(R.id.report_preview1);
		webView2 = (XWalkView) this.findViewById(R.id.report_preview2);
		if(Deviceinfo.getInstance().isVivoX23()){
			webView1.setVisibility(View.GONE);
			webView2.setVisibility(View.VISIBLE);
		}else{
			webView1.setVisibility(View.VISIBLE);
			webView2.setVisibility(View.GONE);
		}
		this.setWebView();
		ImageView closeBtn = initImageView(R.id.pointer);
		closeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ReportPreviewActivity.this.finish();
			}
		});
	}

	/**
	 * 设置web视图显示的数据
	 */
	private void setWebView() {
		if (this.mFactory.getHtmlFile() == null)
			return;
		BufferedReader reader = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(this.mFactory.getHtmlFile());
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = "";
			StringBuffer buffer = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			if(Deviceinfo.getInstance().isVivoX23()){
				webView2.loadDataWithBaseURL(null, buffer.toString(), "text/html", "UTF-8", null);
			}else {
				webView1.loadDataWithBaseURL(null, buffer.toString(), "text/html", "UTF-8", null);
			}
		} catch (Exception e) {
			LogUtil.e(TAG, e.getMessage());
		} finally {
			try {
				if (reader != null)
					reader.close();
				if (in != null)
					in.close();
			} catch (IOException e) {
				LogUtil.e(TAG, e.getMessage());
			}
		}
	}
}
