package com.dinglicom.totalreport;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.UtilsMethod;
import com.walktour.control.config.ServerManager;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicDialog.Builder;
import com.walktour.gui.R;
import com.walktour.gui.applet.ControlBar;
import com.walktour.gui.report.ReportFactory;
import com.walktour.gui.report.ReportPreviewActivity;
import com.walktour.gui.setting.sysroutine.SysRoutineActivity;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;
import com.walktour.gui.weifuwu.sharepush.ShareNextActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 统计报表管理界面
 * 
 * @author zhihui.lian
 */
@SuppressLint("InflateParams")
public class TotalReportXlsActivity extends BasicActivity {

	private TextView title;
	private ImageView pointer;
	private ListView listView;
	private List<File> fileList = new ArrayList<File>();
	private ControlBar bar;
	private Button allBtn;
	private Button noBtn;
	private Button refreshBtn;
	private Button delBtn;
	private ReportXlsAdapter reportXlsAdapter;
	private Button shareBtn;
	private Builder dialog;
	private Set<Integer> mapExist = new HashSet<Integer>();

	/** 更多弹出选项框 */
	private PopupWindow morePopupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_path_listview);
		title = initTextView(R.id.title_txt);
		title.setText(getString(R.string.total_reportfile_title_str));
		fileList.addAll(getFiles());
		bar = (ControlBar) findViewById(R.id.ControlBar);
		pointer = initImageView(R.id.pointer);
		pointer.setOnClickListener(this);
		initView();
	}

	/**
	 * 句柄接收消息
	 */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x01:
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.report_email_send_ing),
						Toast.LENGTH_LONG).show();
				break;
			case 0x02:
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.report_email_send_success),
						Toast.LENGTH_SHORT).show();
				break;
			case 0x03:
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.report_email_send_faild),
						Toast.LENGTH_SHORT).show();
				showDialog(true);
				break;

			default:
				break;
			}
		};
	};

	/**
	 * 检查设置
	 */
	private void showDialog(boolean isTip) {
		ServerManager sm = ServerManager.getInstance(getApplicationContext());
		if (sm.getEmailReciverAddress().trim().length() == 0 || isTip) {
			if (dialog != null && dialog.show().isShowing()) {
				return;
			}
			dialog = new BasicDialog.Builder(TotalReportXlsActivity.this).setTitle(R.string.str_tip)
					.setMessage(R.string.fleet_set_notset_notify)
					.setNeutralButton(R.string.setting, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							Intent intent = new Intent(getApplicationContext(), SysRoutineActivity.class);
							intent.setAction(SysRoutineActivity.SHOW_DATA_UPLOAD_TAB);
							startActivity(intent);
						}
					});
			dialog.show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Button01: // 全选
			for (int i = 0; i < fileList.size(); i++) {
				mapExist.add(i);
			}
			reportXlsAdapter.notifyDataSetChanged();
			break;
		case R.id.Button02: // 反选
			for (int i = 0; i < fileList.size(); i++) {
				if (mapExist.contains(i)) {
					mapExist.remove(i);
				} else {
					mapExist.add(i);
				}
			}
			reportXlsAdapter.notifyDataSetChanged();
			break;
		case R.id.Button03: // 删除
			List<File> deleteFile = new ArrayList<File>();
			for (int index : mapExist) {
				new File(fileList.get(index).toString()).delete();
				deleteFile.add(fileList.get(index));
			}
			if (mapExist.size() != 0) {
				fileList.removeAll(deleteFile);
				mapExist.clear();
				reportXlsAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.Button04: // 刷新
			fileList.clear();
			fileList.addAll(getFiles());
			reportXlsAdapter.notifyDataSetChanged();
			break;
		case R.id.Button05: // 分享

			showMorePopView();

			break;

		case R.id.pointer:
			finish();
			break;
		case R.id.map_clean_all:
			showDialog(false);
			List<String> deletePaths = new ArrayList<String>();
			for (int index : mapExist) {
				deletePaths.add(fileList.get(index).getAbsolutePath());
			}
			ReportSendMail reportSendMail = new ReportSendMail();

			String[] share = new String[deletePaths.size()];
			for (int i = 0; i < share.length; i++) {
				share[i] = deletePaths.get(i);
			}
			reportSendMail.sendReportMail(share, TotalReportXlsActivity.this, mHandler);
			morePopupWindow.dismiss();
			break;
		case R.id.setting_btn:
			Bundle bundle = new Bundle();
			bundle.putInt(ShareCommons.SHARE_FROM_KEY, ShareCommons.SHARE_FROM_REPORT);
			jumpActivity(ShareNextActivity.class, bundle);
			morePopupWindow.dismiss();
			break;
		default:
			break;
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("deprecation")
	private void initView() {
		listView = (ListView) findViewById(R.id.report_path_listview_id);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ReportFactory.getInstance(TotalReportXlsActivity.this).createHtmlFile(fileList.get(arg2).getAbsolutePath());
				Intent intent = new Intent(TotalReportXlsActivity.this, ReportPreviewActivity.class);
				intent.putExtra(ReportPreviewActivity.EXTRA_FILE_PATH, fileList.get(arg2).getAbsolutePath());
				TotalReportXlsActivity.this.startActivity(intent);
			}
		});

		allBtn = bar.getButton(0);
		allBtn.setText(R.string.str_checkall);
		allBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_select), null,
				null);
		allBtn.setOnClickListener(this);

		noBtn = bar.getButton(1);
		noBtn.setText(R.string.str_checknon);
		noBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_unallcheck), null,
				null);
		noBtn.setOnClickListener(this);

		delBtn = bar.getButton(2);
		delBtn.setText(R.string.delete);
		delBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_delete), null,
				null);
		delBtn.setOnClickListener(this);

		refreshBtn = bar.getButton(3);
		refreshBtn.setText(R.string.total_refurbish);
		refreshBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_refresh),
				null, null);
		refreshBtn.setOnClickListener(this);

		shareBtn = bar.getButton(4);
		shareBtn.setText(R.string.report_share);
		shareBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_share), null,
				null);
		shareBtn.setOnClickListener(this);
		reportXlsAdapter = new ReportXlsAdapter();
		listView.setAdapter(reportXlsAdapter);
		reportXlsAdapter.notifyDataSetChanged();

		initMorePopView();
	}

	@SuppressWarnings("deprecation")
	private void initMorePopView() {
		if (morePopupWindow == null) {
			View morePopView = LayoutInflater.from(this).inflate(R.layout.report_share_more_pop, null);
			morePopView.findViewById(R.id.map_clean_all).setOnClickListener(this);
			morePopView.findViewById(R.id.setting_btn).setOnClickListener(this);
			float density = this.getResources().getDisplayMetrics().density;
			morePopupWindow = new PopupWindow(morePopView, (int) (150 * density), (int) (210 * density), true);
			morePopupWindow.setFocusable(true);
			morePopupWindow.setTouchable(true);
			morePopupWindow.setBackgroundDrawable(new BitmapDrawable());
		}
	}

	private void showMorePopView() {
		int[] location = new int[2];
		this.shareBtn.getLocationOnScreen(location);
		this.morePopupWindow.showAsDropDown(this.shareBtn, 10, 10);
	}

	/**
	 * 遍历sdcard下的报表目录,返回文件列表
	 */
	private List<File> getFiles() {
		File root = new File(Environment.getExternalStorageDirectory() + "/Walktour/data/report/");
		File[] files = root.listFiles();
		List<File> filePathList = new ArrayList<File>();
		for (File file : files) {
			if (file.isDirectory()) {
				continue;
			}
			filePathList.add(file);
		}

		Collections.sort(filePathList, new Comparator<File>() {
			@Override
			public int compare(File lhs, File rhs) {
				return new Date(rhs.lastModified()).compareTo(new Date(lhs.lastModified()));
			}
		});

		return filePathList;
	}

	@Override
	public void finish() {
		super.finish();
	}

	/**
	 * 适配器
	 */

	class ReportXlsAdapter extends BaseAdapter {

		private ViewHolder holder;

		@Override
		public int getCount() {
			return fileList.size();
		}

		@Override
		public Object getItem(int position) {
			return fileList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.report_xls_listview_checkbox, null);
				holder = new ViewHolder();
				holder.ItemTitle = (TextView) convertView.findViewById(R.id.ItemTitle);
				holder.itemTime = (TextView) convertView.findViewById(R.id.ItemCount);
				holder.itemCheckBox = (CheckBox) convertView.findViewById(R.id.ItemTestable);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.ItemTitle.setText(fileList.get(position).getName());
			holder.itemTime.setText(UtilsMethod.sdFormatss.format(fileList.get(position).lastModified()));

			holder.itemCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						mapExist.add(position);
					} else {
						mapExist.remove(position);
					}
				}
			});
			holder.itemCheckBox.setChecked(mapExist.contains(position));
			return convertView;
		}

		class ViewHolder {
			CheckBox itemCheckBox;
			TextView ItemTitle;
			TextView itemTime;
		}

	}

}
