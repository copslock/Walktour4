package com.walktour.gui.task.activity.scanner.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dingli.seegull.ScanTaskOperateFactory;
import com.dingli.seegull.SeeGullFlags.ProtocolCodes;
import com.dingli.seegull.model.ChannelModel;
import com.dingli.seegull.setupscan.SetupEnhanceTopNSignalScan;
import com.dingli.seegull.setupscan.SetupTopNPilotScan;
import com.walktour.Utils.StringUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 频点列表
 *
 * @author zhihui.lian
 */
public class ChannelListActivity extends BasicActivity
		implements OnItemClickListener, ChannelAdapter.OnItemClickListener {

	public ListView mListView;

	private ChannelAdapter channelAdapter;

	private ArrayList<ChannelModel> channelModels;

	private Button addChannel;

	private int protocolCode;
	private int channelStyle;
	private boolean isPilot = false;
	private boolean isLTE = false;
	private boolean isUpload = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.channel_listview);
		(initTextView(R.id.title_txt)).setText(R.string.scan_frequency_list);// 设置标题
		((ImageButton) findViewById(R.id.pointer)).setOnClickListener(new OnClickListener() { // 设置后退操作
			@Override
			public void onClick(View v) {
				ChannelListActivity.this.finish();
				ChannelListActivity.this.overridePendingTransition(0, R.anim.slide_in_down);
			}
		});
		mListView = (ListView) findViewById(R.id.ListView01);
		addChannel = initButton(R.id.add_channel_btn);
		addChannel.setOnClickListener(this);
		channelModels = ScanTaskOperateFactory.getInstance().getChannelList();
		channelAdapter = new ChannelAdapter(this, channelModels);
		mListView.setAdapter(channelAdapter);
		mListView.setOnItemClickListener(this);
		getIntentExtra();
	}

	/**
	 * 获取传递数据
	 */
	private void getIntentExtra() {
		protocolCode = getIntent().getExtras().getInt(ScanTaskOperateFactory.PROTOCOL_CODE);
		channelStyle = getIntent().getExtras().getInt(ScanTaskOperateFactory.CHANNEL_STYLE);
		isUpload = getIntent().getExtras().getBoolean(ScanTaskOperateFactory.IS_UPLOAD);
		isPilot = getIntent().getExtras().getBoolean(ScanTaskOperateFactory.IS_PILOT);
		isLTE = getIntent().getExtras().getBoolean(ScanTaskOperateFactory.IS_LTE);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
		ChannelModel channelModel = channelAdapter.getItem(index);
		showEditDialog(channelModel);
	};

	private int bandCodePostion = 0;

	/**
	 * 显示新建与编辑对话框
	 *
	 * @param channelModel
	 */
	@SuppressLint("InflateParams")
	private void showEditDialog(final ChannelModel channelModel) {

		LayoutInflater layoutInflater = LayoutInflater.from(ChannelListActivity.this);
		View layoutView = layoutInflater.inflate(R.layout.edit_channel_layout, null);
		Spinner bandCode = (Spinner) layoutView.findViewById(R.id.sp_bandCode);
		final TextView startChannelEdit = (TextView) layoutView.findViewById(R.id.txt_startChannel);
		final TextView endChannelEdit = (TextView) layoutView.findViewById(R.id.txt_endChannel);
		final TextView channelRangeEdit = (TextView) layoutView.findViewById(R.id.channel_range_txt);
		final RelativeLayout channelRangeLay = (RelativeLayout) layoutView.findViewById(R.id.channel_range);
		ArrayAdapter<String> bandArraySp = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				ScanTaskOperateFactory.ChannelRange.getBandStrArray(channelStyle,protocolCode, this.isUpload,
						this.getString(R.string.sys_setting_data_upload_log_select)));
		bandArraySp.setDropDownViewResource(R.layout.spinner_dropdown_item);
		bandCode.setAdapter(bandArraySp);

		if (channelModel != null) {
			bandCode.setSelection(
					bandCodeIndex(channelModel.getBandCode(), ScanTaskOperateFactory.ChannelRange.getBandCodeArray(channelStyle,protocolCode, this.isUpload)));
			startChannelEdit.setText(channelModel.getStartChannel() + "");
			endChannelEdit.setText(channelModel.getEndChannel() + "");
		}
		bandCode.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (channelModel != null) {
					if (bandCodeIndex(channelModel.getBandCode(),
							ScanTaskOperateFactory.ChannelRange.getBandCodeArray(channelStyle,protocolCode, isUpload)) == position) {
						startChannelEdit.setText(channelModel.getStartChannel() + "");
						endChannelEdit.setText(channelModel.getEndChannel() + "");
					} else {
						startChannelEdit.setText("");
						endChannelEdit.setText("");
					}
				}
				bandCodePostion = position;
				showChannelRange(channelRangeEdit, channelRangeLay);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

		new BasicDialog.Builder(this).setView(layoutView)
				.setIcon(channelModel == null ? R.drawable.controlbar_addto : R.drawable.controlbar_edit)
				.setTitle(channelModel == null ? R.string.sc_add_frequency : R.string.scan_edit_frequency)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String startChannelStr = startChannelEdit.getText().toString().trim();
						if (startChannelStr.length() == 0) {
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.task_alert_nullInut),
									Toast.LENGTH_SHORT).show();
							return;
						}
						String endChannelStr = endChannelEdit.getText().toString().trim();
						if (endChannelStr.length() == 0)
							endChannelStr = startChannelStr;
						int startChannel = Integer.valueOf(startChannelStr);
						int endChannel = Integer.valueOf(endChannelStr);
						String channelRange = getChannelRange();
						if (startChannel > endChannel || StringUtil.isNullOrEmpty(channelRange)) {
							Toast.makeText(getApplicationContext(), R.string.sc_frequency_out_of_range, Toast.LENGTH_SHORT).show();
							return;
						}
						String[] channels = channelRange.split(",");
						boolean isFind = false;
						for (String channel : channels) {
							if (!StringUtil.isNullOrEmpty(channel)) {
								if (channel.indexOf("-") > 0) {
									String[] ls = channel.split("-");
									if (startChannel >= Integer.parseInt(ls[0]) && endChannel <= Integer.parseInt(ls[1])) {
										isFind = true;
										break;
									}
								} else if (startChannel == endChannel && startChannel == Integer.parseInt(channel)) {
									isFind = true;
									break;
								}
							}
						}
						if (!isFind) {
							Toast.makeText(getApplicationContext(), R.string.sc_frequency_out_of_range, Toast.LENGTH_SHORT).show();
							return;
						}
						int bandCode = ScanTaskOperateFactory.ChannelRange.getBandCodeArray(channelStyle,protocolCode, isUpload)[bandCodePostion - 1];
						if (isPilot) {
							Set<Integer> set = new HashSet<Integer>();
							for (ChannelModel model : channelModels) {
								if(channelModel != null && model.getId() == channelModel.getId())
									continue;
								for (int channel = model.getStartChannel(); channel <= model.getEndChannel(); channel++) {
									set.add(channel);
								}
							}
							for (int channel = startChannel; channel <= endChannel; channel++) {
								set.add(channel);
							}
							int max = SetupEnhanceTopNSignalScan.MAX_CHANNEL_COUNT;
							if(protocolCode == ProtocolCodes.PROTOCOL_TDSCDMA)
								max = SetupTopNPilotScan.MAX_TD_CHANNEL_COUNT;
							else if(!isLTE)
								max = SetupTopNPilotScan.MAX_CHANNEL_COUNT;
							if (set.size() > max) {
								String message = String.format(getString(R.string.sc_frequency_number_exceeds_maximum_value),
										String.valueOf(max));
								Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
								return;
							}
						}
						ChannelModel channelModelNew = new ChannelModel();
						channelModelNew.setBandCode(bandCode);
						channelModelNew.setStartChannel(startChannel);
						channelModelNew.setEndChannel(endChannel);
						if (channelModel == null) {
							channelModels.add(channelModelNew);
						} else {
							int index = channelModels.indexOf(channelModel);
							channelModels.remove(channelModel);
							channelModels.add(index, channelModelNew);
						}
						channelAdapter.notifyDataSetChanged();
						dialog.dismiss();
					}

				}, false).setNegativeButton(R.string.str_cancle).show();
	}

	/**
	 * 显示频点范围信息
	 *
	 * @param channelRangeEdit
	 *          频点范围文本框
	 * @param channelRangeLay
	 *          频点范围行
	 */
	private void showChannelRange(TextView channelRangeEdit, RelativeLayout channelRangeLay) {
		String channelRange = getChannelRange();
		if (StringUtil.isNullOrEmpty(channelRange)) {
			channelRangeLay.setVisibility(View.GONE);
			channelRangeEdit.setText("");
		} else {
			channelRangeLay.setVisibility(View.VISIBLE);
			channelRangeEdit.setText(channelRange);
		}
	}

	/**
	 * 获取指定带宽和指定频段的频点范围
	 *
	 * @return
	 */
	private String getChannelRange() {
		List<String> channelRanges = ScanTaskOperateFactory.ChannelRange.getBandRangeList(protocolCode, channelStyle, isUpload);
		if (bandCodePostion == 0) {
			return "";
		}
		for (int i = 0; i < channelRanges.size(); i++) {
			if ((bandCodePostion - 1) == i) {
				return channelRanges.get(i);
			}
		}
		return "";
	}

	/**
	 * 查找BandCode Index
	 *
	 * @return
	 */
	private int bandCodeIndex(int bandCode, int[] bandCodeArr) {
		int index = 0;
		for (int i = 0; i < bandCodeArr.length; i++) {
			if (bandCode == bandCodeArr[i]) {
				index = i;
				break;
			}
		}
		return index + 1; // 由于前面还多了"选择"项，所以对应列表+1
	}

	@Override
	public void finish() {
		super.finish();
		ScanTaskOperateFactory.getInstance().setChannelList(channelModels);
		ChannelListActivity.this.overridePendingTransition(0, R.anim.slide_in_down);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.add_channel_btn:
				showEditDialog(null);
				break;
			default:
				break;
		}
	}

	/**
	 * 删除按钮监听
	 */
	@Override
	public void onItemClick(View view, final int position) {
		showDeleteDialog(position);
	}

	/**
	 * 显示删除键框
	 *
	 * @param position
	 */
	private void showDeleteDialog(final int position) {
		new BasicDialog.Builder(this).setTitle(R.string.delete).setIcon(android.R.drawable.ic_menu_delete)
				.setMessage(R.string.str_delete_makesure)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						channelModels.remove(position);
						channelAdapter.notifyDataSetChanged();
					}
				}).setNegativeButton(R.string.str_cancle).show();
	}

}
