package com.walktour.gui.weifuwu.sharepush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.walktour.Utils.ToastUtil;
import com.walktour.control.config.ProjectManager;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.applet.MySimpleAdapter;
import com.walktour.gui.weifuwu.ShareDialogActivity;
import com.walktour.gui.weifuwu.business.model.ShareFileModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.gui.weifuwu.view.BadgeView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/***
 * 共享工程界面
 * 
 * @author weirong.fan
 *
 */
public class ShareProjectActivity extends BasicActivity implements OnClickListener {
	private Context context = ShareProjectActivity.this;
	private Button nb;
	//按钮上显示文字
	private BadgeView badge1;
	private ProjectManager proManager = null;
	private FileReceiver receiver = new FileReceiver();
	private MySimpleAdapter checkListItemAdapter;
	private ListView listView;
	private ArrayList<File> projectFileList;
	private ArrayList<HashMap<String, Object>> itemArrayList = new ArrayList<HashMap<String, Object>>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.shareprojectlayout);
		proManager = new ProjectManager(this);
		findViewById(R.id.pointer).setOnClickListener(this);
		initTextView(R.id.title_txt).setText(getString(R.string.share_project_list));
		Button shareBtn = this.initButton(R.id.share);
		shareBtn.setOnClickListener(this);
		nb = (Button) findViewById(R.id.push);
		nb.setOnClickListener(this);
		badge1 = new BadgeView(this, nb);
	    badge1.setText("1");
	    badge1.setTextColor(getResources().getColor(R.color.white));
	    badge1.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
	    badge1.hide();
		initButtonNum();
		initData();
		initViews();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ShareCommons.SHARE_ACTION_1);
		filter.addAction(ShareCommons.SHARE_ACTION_REFRESH_PROJECT);
		this.registerReceiver(receiver, filter);
	}
	private void initViews() {
		listView = (ListView) this.findViewById(R.id.shareobj);
		checkListItemAdapter = new MySimpleAdapter(this, itemArrayList, R.layout.listview_item_style13,
				new String[] { "ItemTitle", "ItemImage" }, new int[] { R.id.ItemTitle, R.id.ItemImage });
		listView.setAdapter(checkListItemAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// 先判断SD卡是否可用
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					final File fileProject = projectFileList.get(arg2);
					new BasicDialog.Builder(context).setTitle(R.string.main_project_load)
							.setMessage(getString(R.string.main_project_load_alert) + " "
									+ fileProject.getName().substring(0, fileProject.getName().lastIndexOf(".xml")))
							.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (fileProject.exists()) {
								int resultCode = proManager.loadProject(fileProject);
								if (resultCode == ProjectManager.LOAD_RESULT_VERSIONEROR) {
									ToastUtil.showToastShort(context, getString(R.string.import_file_format_str));
								} else {
									ToastUtil.showToastShort(context, String.format("%s %s",
											getString(R.string.main_project_load), getString(R.string.total_success)));
								}
							}
						}
					}).setNegativeButton(R.string.str_cancle).show();
				} else {
					ToastUtil.showToastShort(context, getString(R.string.sdcard_non));
				}
			}
		});
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(receiver);
	}
	/**
	 * 初始化数据
	 */
	private void initData() {
		itemArrayList.clear();
		projectFileList = proManager.getProjectList();
		if (null != projectFileList && projectFileList.size() > 0) {
			for (File f : projectFileList) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemTitle", f.getName().substring(0, f.getName().lastIndexOf(".xml")));// android:background="@drawable/background_btn_delete"
				map.put("ItemImage", R.drawable.background_btn_delete);
				map.put(MySimpleAdapter.KEY_DELETE, f);
				itemArrayList.add(map);
			}
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.share:
			Bundle bundle = new Bundle();
			bundle.putInt(ShareCommons.SHARE_FROM_KEY, ShareCommons.SHARE_FROM_PROJECT);
			this.jumpActivity(ShareNextActivity.class, bundle);
			break;
		case R.id.push:
			try {
				// 无新的共享信息,给出提示
				List<ShareFileModel> lists = ShareDataBase.getInstance(context).fetchAllFilesByFileStatusAndFileType(
						ShareFileModel.FILETYPE_PROJECT, new int[] { ShareFileModel.FILE_STATUS_INIT,
								ShareFileModel.FILE_STATUS_START, ShareFileModel.FILE_STATUS_ONGOING });
				if (lists.size() <= 0) {
					ToastUtil.showToastShort(ShareProjectActivity.this, R.string.share_project_share_info_obj_receive);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			badge1.hide();
			// 跳转到接收diaolog
			Bundle bundle2 = new Bundle();
			bundle2.putInt("fileType", ShareFileModel.FILETYPE_PROJECT);
			this.jumpActivity(ShareDialogActivity.class, bundle2);
			break;
		case R.id.pointer:
			finish();
			break;
		}
	}
	/***
	 * 广播接收器，更新接收按钮上的数组
	 * 
	 * @author weirong.fan
	 *
	 */
	private class FileReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ShareCommons.SHARE_ACTION_1)) {
				initButtonNum();
			} else if (intent.getAction().equals(ShareCommons.SHARE_ACTION_REFRESH_PROJECT)) {
				initData();
				checkListItemAdapter.notifyDataSetChanged();
				listView.invalidate();
			}
		}
	}
	/**
	 * 接收到广播后
	 */
	private void initButtonNum() {
		try {
			List<ShareFileModel> lists = ShareDataBase.getInstance(context).fetchAllFilesByFileStatusAndFileType(
					ShareFileModel.FILETYPE_PROJECT, new int[] { ShareFileModel.FILE_STATUS_INIT });
			if (lists.size() > 0){
				badge1.setText(lists.size()+"");
				badge1.show(true);
			}else{
				badge1.hide(true);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
