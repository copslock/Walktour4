package com.dinglicom.totalreport;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.walktour.Utils.ToastUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.data.FileManagerFragmentActivity;

import java.io.File;

/**
 * 报表选模板管理列表
 * 
 * @author zhihui.lian
 * 
 */
@SuppressLint("InflateParams")
public class SelectTemplateListActivity extends BasicActivity {
	private Context context=SelectTemplateListActivity.this; 
	public final static int REQUESTCODE_2 = 20123;

	private LinearLayout layout1;
	/**标识是否是新建的自定义统计报表**/
	private boolean isNewCreateReport=false;
	/**自定义报表sheet 格式字符串**/
	private String jsonPath="";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listview_report_manger2);

		initTextView(R.id.title_txt).setText(R.string.total_reportlist_title_str);
		initImageView(R.id.pointer).setOnClickListener(this);

		initTextView(R.id.exportcurrentreport).setOnClickListener(this);
		initTextView(R.id.createreport).setOnClickListener(this);
		layout1 = initLinearLayout(R.id.layout11);

		refreshData();
	}

	private void refreshData() {
		layout1.removeAllViews();
		File ff = new File(ReportCommons.CUSTOMREPORT_PATH);
		if (ff.exists()) {
			File[] files = ff.listFiles();
			if (null != files && files.length > 0) {
				for (final File file : files) {
					LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.report_manger_item4, null);
					ImageView delBtn = (ImageView) layout.findViewById(R.id.Itemdelete);
					TextView tv = (TextView) layout.findViewById(R.id.ItemTitle);
					ImageView editBtn = (ImageView) layout.findViewById(R.id.Itemedit);

					delBtn.setVisibility(View.GONE);

					editBtn.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							Bundle bundle = new Bundle();
							bundle.putBoolean("isEdit", true);
							bundle.putString("reportPath", ReportCommons.CUSTOMREPORT_PATH + file.getName() + "/");
							jumpActivity(CreateReportTemplateActivity.class, bundle);
						}
					});
					tv.setText(file.getName());
					tv.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							isNewCreateReport = true;
							jsonPath = file.getAbsolutePath();
							File jsf = new File(jsonPath);
							if (jsf.exists()) {
								if (jsf.listFiles().length >= 3) {//模板文件都生成好
									Bundle bundle = new Bundle();
									bundle.putBoolean(FileManagerFragmentActivity.KEY_TOTAL_MODE, true);
									jumpActivityForResult(FileManagerFragmentActivity.class, bundle, TotalDetailActivity.REQUESTCODE);
								} else {
									ToastUtil.showToastShort(context, R.string.server_file_notfound);
								}
							} 
						}
					});
					layout1.addView(layout);
				}

			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() != KeyEvent.ACTION_UP) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		isNewCreateReport=false;
		switch (viewId) {
		// 导出当前报表
		case R.id.exportcurrentreport:
			Bundle bundle=new Bundle();
			bundle.putBoolean(FileManagerFragmentActivity.KEY_TOTAL_MODE, true);
			jumpActivityForResult(FileManagerFragmentActivity.class, bundle, TotalDetailActivity.REQUESTCODE); 
			break;
		// 创建自定义报表模板
		case R.id.createreport:
			createReport();
			break; 
		// 关闭
		case R.id.pointer:
			finish();
		default:
			break;
		}
	}
	private void createReport() {
		final EditText et = new EditText(this);
		et.setHint(R.string.total_reportlist_export_str6);
		new AlertDialog.Builder(this).setTitle(R.string.total_reportlist_export_str5).setView(et).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which==DialogInterface.BUTTON_POSITIVE){//确定
					if(null==et.getText()||et.getText().toString().length()<=0){
						ToastUtil.showToastShort(context, R.string.total_reportlist_export_str9);
						return;
					}
					String reportPath=ReportCommons.CUSTOMREPORT_PATH+et.getText()+"/";
					File fid=new File(reportPath);
					if(fid.exists()){
						ToastUtil.showToastShort(context, R.string.total_reportlist_export_str8);
						return;
					}
					fid.mkdir();
					Bundle bundle=new Bundle();
					bundle.putBoolean("isEdit", false);
					bundle.putString("reportPath", reportPath);
					jumpActivityForResult(CreateReportTemplateActivity.class, bundle,REQUESTCODE_2);
				}
			}

		}).setNegativeButton(R.string.str_cancle, null).show();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case TotalDetailActivity.REQUESTCODE:
				if(!isNewCreateReport){
					Intent intent = new Intent();
					intent.putExtra(FileManagerFragmentActivity.KEY_TOTAL_RUN,data.getBooleanExtra(FileManagerFragmentActivity.KEY_TOTAL_RUN, false));
					intent.putExtra(FileManagerFragmentActivity.IS_TOTAL, data.getIntExtra(FileManagerFragmentActivity.IS_TOTAL, -1)); 
					intent.putExtra(FileManagerFragmentActivity.IS_CUSTOM_REPORT,isNewCreateReport);
					this.setResult(RESULT_OK, intent);
					this.finish();
				}else{ 
					Bundle bundle = new Bundle();
					bundle.putString("jsonPath",jsonPath);
					bundle.putStringArrayList("selectDDIBFiles",data.getStringArrayListExtra(FileManagerFragmentActivity.DDIBLIST));
					jumpActivity(CustomReportActivity.class,bundle);
				}
				break;
			case REQUESTCODE_2:
				this.refreshData();
				break;
			}
		}
	}
}
