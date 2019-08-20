package com.walktour.gui.weifuwu;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.walktour.Utils.FileUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.share.UpDownService;
import com.walktour.gui.share.download.DownloadManager;
import com.walktour.gui.share.upload.UploadCallback;
import com.walktour.gui.share.upload.UploadManager;
import com.walktour.gui.weifuwu.business.model.ShareFileModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;
import com.walktour.gui.weifuwu.sharepush.ShareSendActivity;
import com.walktour.gui.weifuwu.view.swipemenulistview.SwipeMenu;
import com.walktour.gui.weifuwu.view.swipemenulistview.SwipeMenuCreator;
import com.walktour.gui.weifuwu.view.swipemenulistview.SwipeMenuItem;
import com.walktour.gui.weifuwu.view.swipemenulistview.SwipeMenuListView;
import com.walktour.gui.weifuwu.view.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
public class WeiMainHistoryActivity extends BasicActivity {
	private Context context = WeiMainHistoryActivity.this;
	private SwipeMenuListView mListView;
	public List<ShareFileModel> list = new LinkedList<ShareFileModel>();
	private MyAdapter adapter;
	private LinearLayout titleRight;
	private Button share1;
	private String isFrom = "";
	private String groupCode = "";
	private String currentDeviceCode = "";
	private MyReceiver receiver = new MyReceiver();
	private int requestCode1 = 0x1200;
	private int requestCode2 = 0x1200;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weifuwumainhistorylayout);
		isFrom = this.getIntent().getStringExtra("isFrom");
		findViewById(R.id.pointer).setOnClickListener(this);
		initTextView(R.id.title_txt).setText(getString(R.string.share_project_share_history_list));
		titleRight = initLinearLayout(R.id.title_right);
		titleRight.setVisibility(View.VISIBLE);
		share1 = (Button) titleRight.findViewById(R.id.share1);
		share1.setVisibility(View.VISIBLE);
		share1.setOnClickListener(this);
		adapter = new MyAdapter();
		if (isFrom.equals("relation-device")) {// 从设备信息过来的
			currentDeviceCode = this.getIntent().getStringExtra("deviceCode");
			list = ShareDataBase.getInstance(context).fetchAllFilesByCode(currentDeviceCode);
		} else if (isFrom.equals("relation-me")) {// 搜索我共享的历史
			list = ShareDataBase.getInstance(context).fetchAllFilesByDeviceCode(ShareCommons.device_code);
			share1.setVisibility(View.GONE);
		} else if (isFrom.equals("group")) {// 从指定的群组过来的
			groupCode = this.getIntent().getStringExtra("groupCode");
			list = ShareDataBase.getInstance(context).fetchAllFilesByGroupCode(groupCode);
		}
		mListView = (SwipeMenuListView) findViewById(R.id.list);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				final ShareFileModel m = list.get(arg2);
				if (m.getFileStatus() == ShareFileModel.FILE_STATUS_FINISH) {
					// 完成，如果是图片则显示图片信息
					if (m.getFileType() == ShareFileModel.FILETYPE_PIC_SCREENSHOT
							|| m.getFileType() == ShareFileModel.FILETYPE_CQI_PIC) {
						LayoutInflater inflater = getLayoutInflater();
						View layout = inflater.inflate(R.layout.weifuwumain_show_pic, null);
						ImageView img = (ImageView) layout.findViewById(R.id.imgshow);
						byte[] picByte = FileUtil.getBytesFromFile(new File(m.getFilePath() + m.getFileName()));
						final Bitmap bitmap = BitmapFactory.decodeByteArray(picByte, 0, picByte.length);
						img.setImageBitmap(bitmap);
						new AlertDialog.Builder(context).setTitle(R.string.browser).setView(layout)
								.setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
					}
				} else {
					if (m.getFileStatus() != ShareFileModel.FILE_STATUS_ZIP
							&& m.getFileStatus() != ShareFileModel.FILE_STATUS_UNZIP) {
						doSth(m);
					}
				}
			}
		});
		mListView.setAdapter(adapter);
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
				// set item width
				deleteItem.setWidth(dp2px(100));
				// set a icon
				// deleteItem.setIcon(R.drawable.ic_delete);
				deleteItem.setTitle(R.string.share_project_share);
				deleteItem.setTitleSize(14);
				deleteItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(deleteItem);
				// create "open" item
				SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
				// set item background
				openItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
				// set item width
				openItem.setWidth(dp2px(100));
				// set item title
				openItem.setTitle(R.string.delete);
				// set item title fontsize
				openItem.setTitleSize(14);
				// set item title font color
				openItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(openItem);
			}
		};
		mListView.setMenuCreator(creator);
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				ShareFileModel item = list.get(position);
				switch (index) {
				case 1:
					ShareDataBase.getInstance(context).deleteFile(item.getId());
					list.remove(position);
					adapter.notifyDataSetChanged();
					mListView.invalidate();
					break;
				case 0:
					Bundle bundle = new Bundle();
					bundle.putInt("ID", item.getId());
					bundle.putInt(ShareCommons.SHARE_FROM_KEY, ShareCommons.SHARE_FROM_SHARE);
					jumpActivity(ShareSendActivity.class, bundle);
					break;
				}
			}
		});
		IntentFilter filter = new IntentFilter();
		filter.addAction(UploadCallback.ACTION);
		filter.addAction(ShareCommons.SHARE_ACTION_MAIN_1);
		this.registerReceiver(receiver, filter);
	}
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(receiver);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pointer:
			this.finish();
			break;
		case R.id.share1:
			if (isFrom.equals("group")) {
				Bundle bundle = new Bundle();
				bundle.putString("groupCode", groupCode);
				jumpActivityForResult(WeiMainGroupInfoActivity.class, bundle, requestCode1);
			} else if (isFrom.equals("relation-device")) {
				Bundle bundle = new Bundle();
				bundle.putString("deviceCode", currentDeviceCode);
				jumpActivityForResult(WeiMainDeviceInfoActivity.class, bundle, requestCode2);
			}
			break;
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
	}
	private class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return null == list ? 0 : list.size();
		}
		@Override
		public Object getItem(int position) {
			return null == list ? null : list.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView) {
				convertView = View.inflate(WeiMainHistoryActivity.this, R.layout.weifuwumain1layout_item, null);
			}
			TextView typeTV = (TextView) convertView.findViewById(R.id.fileType);
			TextView tv1 = (TextView) convertView.findViewById(R.id.name1);
			TextView tv2 = (TextView) convertView.findViewById(R.id.name2);
			TextView tv3 = (TextView) convertView.findViewById(R.id.name333);
			TextView tv4 = (TextView) convertView.findViewById(R.id.name4);
			Button btn5 = (Button) convertView.findViewById(R.id.name5);
			ProgressBar bar = (ProgressBar) convertView.findViewById(R.id.probar);
			final ShareFileModel model = list.get(position);
			typeTV.setBackgroundResource(model.getFileTypeDrawable());
			tv1.setText(model.getFileDescribe());
			try {
				tv2.setText(StringUtil.changeDateTime(context, model.getCreateTime()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			String txt = model.getFromDeviceCode();
			if (null == txt) {
				tv3.setText(getString(R.string.share_project_select_from) + "");
			} else {
				tv3.setText(
						getString(R.string.share_project_select_from) + (txt.equalsIgnoreCase(ShareCommons.device_code)
								? getString(R.string.share_project_select_local) : model.getFromDeviceCode()));
			}
			bar.setVisibility(View.GONE);
			switch (model.getFileStatus()) {
			case ShareFileModel.FILE_STATUS_FINISH:
				tv4.setVisibility(View.VISIBLE);
				btn5.setVisibility(View.GONE);
				tv4.setText(model.getSendOrReceive() == ShareFileModel.SEND_OR_RECEIVE_SEND
						? getString(R.string.share_project_exist_send)
						: getString(R.string.share_project_exist_receive));
				break;
			case ShareFileModel.FILE_STATUS_ONGOING:
				tv4.setVisibility(View.VISIBLE);
				tv4.setText(model.getSendOrReceive() == ShareFileModel.SEND_OR_RECEIVE_SEND
						? getString(R.string.share_project_devices_wait_sending)
						: getString(R.string.share_project_devices_wait_receiveing));
				btn5.setVisibility(View.VISIBLE);
				btn5.setText(R.string.info_stop);
				bar.setVisibility(View.VISIBLE);
				bar.setProgress((int) ((float) model.getFileRealSize() / (float) model.getFileTotalSize() * 100));
				break;
			case ShareFileModel.FILE_STATUS_ZIP:
				if (model.getSendOrReceive() == ShareFileModel.SEND_OR_RECEIVE_SEND) {
					tv4.setVisibility(View.VISIBLE);
					tv4.setText(R.string.share_project_devices_wait_zipping);
					btn5.setVisibility(View.GONE);
					bar.setVisibility(View.GONE);
				}
				break;
			case ShareFileModel.FILE_STATUS_UNZIP:
				if (model.getSendOrReceive() == ShareFileModel.SEND_OR_RECEIVE_RECEIVE) {
					tv4.setVisibility(View.VISIBLE);
					tv4.setText(R.string.share_project_devices_wait_unzipping);
					btn5.setVisibility(View.GONE);
					bar.setVisibility(View.GONE);
				}
				break;
			case ShareFileModel.FILE_STATUS_WAITING:
				tv4.setVisibility(View.VISIBLE);
				tv4.setText(model.getSendOrReceive() == ShareFileModel.SEND_OR_RECEIVE_SEND
						? getString(R.string.share_project_devices_wait_send)
						: getString(R.string.share_project_devices_wait_receive));
				btn5.setVisibility(View.GONE);
				bar.setVisibility(View.VISIBLE);
				bar.setProgress((int) ((float) model.getFileRealSize() / (float) model.getFileTotalSize() * 100));
				break;
			case ShareFileModel.FILE_STATUS_ERROR:
				tv4.setVisibility(View.GONE);
				btn5.setVisibility(View.VISIBLE);
				bar.setVisibility(View.GONE);
				bar.setProgress((int) ((float) model.getFileRealSize() / (float) model.getFileTotalSize() * 100));
				btn5.setText(model.getSendOrReceive() == ShareFileModel.SEND_OR_RECEIVE_SEND
						? getString(R.string.share_project_send) : getString(R.string.share_project_receive));
				break;
			default:
				tv4.setVisibility(View.GONE);
				btn5.setVisibility(View.VISIBLE);
				btn5.setText(model.getSendOrReceive() == ShareFileModel.SEND_OR_RECEIVE_SEND
						? getString(R.string.share_project_send) : getString(R.string.share_project_receive));
				break;
			}
			btn5.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					doSth(model);
				}
			});
			return convertView;
		}
	}
	/**
	 * 触发上传或停止
	 * 
	 * @param model
	 */
	private void doSth(final ShareFileModel model) {
		if (model.getSendOrReceive() == ShareFileModel.SEND_OR_RECEIVE_SEND) {// 发送
			UploadManager um = UpDownService.getUploadManager();
			if (model.getFileStatus() == ShareFileModel.FILE_STATUS_ONGOING) {
				um.stopUpload(model);
			} else {
				um.startUpload(model);
			}
		} else {// 接收
			DownloadManager dm = UpDownService.getDownloadManager();
			if (model.getFileStatus() == ShareFileModel.FILE_STATUS_ONGOING) {
				dm.stopDownload(model);
			} else {
				dm.startDownload(model);
			}
		}
	}
	/**
	 * 广播接收器
	 * 
	 * @author weirong.fan
	 *
	 */
	private class MyReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			try {
				String action = intent.getAction();
				if (action.equals(UploadCallback.ACTION) || action.equals(ShareCommons.SHARE_ACTION_MAIN_1)) {
					list = ShareDataBase.getInstance(context).fetchAllFiles();
					adapter.notifyDataSetChanged();
					mListView.invalidate();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (requestCode == requestCode1) {
				// 刷新数据
				boolean flag;
				flag = ShareDataBase.getInstance(context).exitGroupRelation(groupCode, ShareCommons.device_code);
				if (!flag) {
					finish();
				} else {
					list.clear();
					list.addAll(ShareDataBase.getInstance(context).fetchAllFilesByGroupCode(groupCode));
					adapter.notifyDataSetChanged();
					mListView.invalidate();
				}
			} else if (requestCode == requestCode2) {
				// 刷新数据
				boolean flag;
				flag = ShareDataBase.getInstance(context).exitDeviceRelation(currentDeviceCode);
				if (!flag) {
					finish();
				} else {
					list.clear();
					list.addAll(ShareDataBase.getInstance(context).fetchAllFilesByDeviceCode(currentDeviceCode));
					adapter.notifyDataSetChanged();
					mListView.invalidate();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
