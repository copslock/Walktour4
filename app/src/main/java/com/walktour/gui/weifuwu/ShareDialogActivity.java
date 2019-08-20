package com.walktour.gui.weifuwu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.walktour.Utils.StringUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.share.UpDownService;
import com.walktour.gui.share.download.DownloadManager;
import com.walktour.gui.share.upload.UploadCallback;
import com.walktour.gui.weifuwu.business.model.ShareFileModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;

import java.util.LinkedList;
import java.util.List;
/***
 * 接收共享信息的dialog 各类型的接收dialog
 * 
 * @author weirong.fan
 *
 */
public class ShareDialogActivity extends BasicActivity implements OnClickListener {
	/** 上下文 */
	private Context context = ShareDialogActivity.this;
	/** 显示的文件类型 */
	private int fileType = -1;
	private ListView mListView;
	public List<ShareFileModel> list = new LinkedList<ShareFileModel>();
	private MyAdapter adapter = new MyAdapter();
	private MyReceiver receiver = new MyReceiver(); 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fileType = this.getIntent().getIntExtra("fileType", -1);
		initViews();
	}
	private void initViews() {
		this.setContentView(R.layout.sharedialoglayout);
		
		try {
			// 只显示初始状态的文件
			list = ShareDataBase.getInstance(context).fetchAllFilesByFileStatusAndFileType(fileType,
					new int[] { ShareFileModel.FILE_STATUS_INIT, ShareFileModel.FILE_STATUS_START,
							ShareFileModel.FILE_STATUS_ONGOING });
			// 将初始状态的文件修改为开始状态
			ShareDataBase.getInstance(context).updateFileStatusToStart(fileType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mListView = (ListView) findViewById(R.id.list);
		mListView.setAdapter(adapter);
		IntentFilter filter = new IntentFilter();
		filter.addAction(UploadCallback.ACTION);
		this.registerReceiver(receiver, filter);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(receiver);
	}
	@Override
	public void onClick(View v) {
		super.onClick(v);
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
				convertView = View.inflate(ShareDialogActivity.this, R.layout.weifuwumain1layout_item, null);
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
				tv2.setText(StringUtil.changeDateTime(context,model.getCreateTime()));
			} catch (Exception e) { 
				e.printStackTrace();
			}
			String txt = model.getFromDeviceCode();
			if (null == txt) {
				tv3.setText(getString(R.string.share_project_select_from) + "");
			} else {
				tv3.setText(getString(R.string.share_project_select_from) + (txt.equalsIgnoreCase(ShareCommons.device_code)
						? getString(R.string.share_project_select_local) : model.getFromDeviceCode()));
			}
			bar.setVisibility(View.GONE);
			switch (model.getFileStatus()) {
			case ShareFileModel.FILE_STATUS_FINISH:
				tv4.setVisibility(View.VISIBLE);
				btn5.setVisibility(View.GONE);
				tv4.setText(model.getSendOrReceive() == ShareFileModel.SEND_OR_RECEIVE_SEND ? getString(R.string.share_project_exist_send) : getString(R.string.share_project_exist_receive));
				break;
			case ShareFileModel.FILE_STATUS_ONGOING:
				tv4.setVisibility(View.VISIBLE);
				tv4.setText(model.getSendOrReceive() == ShareFileModel.SEND_OR_RECEIVE_SEND ? getString(R.string.share_project_devices_wait_sending) : getString(R.string.share_project_devices_wait_receiveing));
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
				tv4.setText(model.getSendOrReceive() == ShareFileModel.SEND_OR_RECEIVE_SEND ? getString(R.string.share_project_devices_wait_send) : getString(R.string.share_project_devices_wait_receive));
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
					DownloadManager dm = UpDownService.getDownloadManager();
					if (model.getFileStatus() == ShareFileModel.FILE_STATUS_ONGOING) {
						dm.stopDownload(model);
					} else {
						dm.startDownload(model);
					}
				}
			});
			return convertView;
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
				if (action.equals(UploadCallback.ACTION)) {
					List<Integer> IDS = new LinkedList<Integer>();
					for (ShareFileModel f : list) {
						IDS.add(f.getId());
					}
					list = ShareDataBase.getInstance(context).fetchAllFilesByIDS(IDS);
					adapter.notifyDataSetChanged();
					mListView.invalidate();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
