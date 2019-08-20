package com.walktour.gui.setting.sysroutine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.control.config.ConfigDataAcquisition;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.applet.MyKeyListener;
import com.walktour.model.DataAcquisitionModel;

import java.util.ArrayList;

/**
 * 数据过滤采集设置
 * 
 * @author jianchao.wang
 * 
 */
@SuppressLint("InflateParams")
public class SysRoutineDataFilterActivity extends BasicActivity implements OnItemClickListener, OnClickListener {
	/** 配置文件 */
	private ConfigDataAcquisition config;
	/** 配置对象 */
	private ArrayList<DataAcquisitionModel> dataLists = new ArrayList<DataAcquisitionModel>();
	/** 显示列表 */
	private ListView showList = null;
	/** 开关适配器 */
	private RadioAdapter radioAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sys_routine_setting_data_filter);
		config = ConfigDataAcquisition.getInstance();
		dataLists = config.getDataModelList();
		findView();
	}

	/**
	 * 加载控件
	 */
	private void findView() {
		CheckBox schCheck = (CheckBox) findViewById(R.id.sch_ckb);
		CheckBox rlcCheck = (CheckBox) findViewById(R.id.rlc_pdu_ckb);
		CheckBox macCheck = (CheckBox) findViewById(R.id.mac_pdu_ckb);
		CheckBox ml1Check = (CheckBox) findViewById(R.id.ml1_ckb);
		CheckBox pdcpCheck = (CheckBox) findViewById(R.id.pdcp_pdu_ckb);
		CheckBox grantCheck = (CheckBox) findViewById(R.id.grant_ckb);
		CheckBox mcsCheck = (CheckBox) findViewById(R.id.mcs_ckb);
		CheckBox l3Check = (CheckBox) findViewById(R.id.l3_ckb);
		CheckBox dciCheck = (CheckBox) findViewById(R.id.dci_ckb);
		CheckBox rbCheck = (CheckBox) findViewById(R.id.rb_ckb);
		CheckBox cusomCheck = (CheckBox) findViewById(R.id.cusom_ckb);
		schCheck.setChecked(config.getschInfo());
		rlcCheck.setChecked(config.getRLCPDUInfo());
		macCheck.setChecked(config.getMACPDUInfo());
		ml1Check.setChecked(config.getML1Info());
		pdcpCheck.setChecked(config.getPDCPPDUInfo());
		grantCheck.setChecked(config.getgrantInfo());
		mcsCheck.setChecked(config.getmcsStatis());
		l3Check.setChecked(config.getl3msg());
		dciCheck.setChecked(config.getdciInfo());
		rbCheck.setChecked(config.getrrbInfo());
		cusomCheck.setChecked(config.getisPara());
		schCheck.setOnClickListener(this);
		rlcCheck.setOnClickListener(this);
		macCheck.setOnClickListener(this);
		ml1Check.setOnClickListener(this);
		pdcpCheck.setOnClickListener(this);
		grantCheck.setOnClickListener(this);
		mcsCheck.setOnClickListener(this);
		l3Check.setOnClickListener(this);
		dciCheck.setOnClickListener(this);
		rbCheck.setOnClickListener(this);
		cusomCheck.setOnClickListener(this);
		showList = (ListView) findViewById(R.id.multirab_listview);
		showList.setOnItemClickListener(this);
		radioAdapter = new RadioAdapter(this.getParent());
		showList.setAdapter(radioAdapter);
		new Thread(new SetListViewHeightBasedOnChildren()).start();
	}

	/**
	 * 
	 * item事件列表监听
	 */

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		showTimeDialog(arg2);
	}

	/**
	 * 显示修改时间弹出框 [功能详细描述]
	 */
	public void showTimeDialog(final Integer itemPosition) {
		BasicDialog.Builder builder = new BasicDialog.Builder(this.getParent());
		View timeAlertLayout = LayoutInflater.from(this.getParent()).inflate(R.layout.alert_dialog_edittext, null);
		final EditText timeEditText = (EditText) timeAlertLayout.findViewById(R.id.alert_textEditText);
		timeEditText.setText(dataLists.get(itemPosition).getTimeInterval());
		timeEditText.setSelectAllOnFocus(true);
		timeEditText.setKeyListener(new MyKeyListener().getIntegerKeyListener());
		builder.setIcon(R.drawable.icon_info).setTitle("Input TimeInterval").setView(timeAlertLayout)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String size = timeEditText.getText().toString();
						if (size.length() >= 6) {
							Toast.makeText(getParent(), getParent().getString(R.string.alert_inputtoolong), Toast.LENGTH_SHORT).show();
							return;
						} else {
							if (Integer.valueOf(size.trim().length() == 0 ? "0" : size) <= 0) {
								Toast.makeText(getParent(), getParent().getString(R.string.alert_inputagain), Toast.LENGTH_SHORT).show();
								return;
							}
						}
						config.setTimeEdit(itemPosition, size);
						radioAdapter.notifyDataSetChanged();
					}
				}).setNegativeButton(R.string.str_cancle).show();
	}

	/**
	 * ListView处理<BR>
	 * 该方法解决ScroolView 中嵌套ListView的问题
	 * 设置完ListView的Adapter后，根据ListView的子项目重新计算ListView的高度，
	 * 然后把高度再作为LayoutParams设置给ListView 修改不能滑屏
	 */

	private class SetListViewHeightBasedOnChildren implements Runnable {

		@Override
		public void run() {
			ListAdapter hotlistAdapter = showList.getAdapter();
			if (hotlistAdapter == null) {
				return;
			}
			int hottotalHeight = 0;
			for (int i = 0; i < hotlistAdapter.getCount(); i++) {
				View listItem = hotlistAdapter.getView(i, null, showList);
				listItem.measure(0, 0);
				hottotalHeight += listItem.getMeasuredHeight();
			}
			final ViewGroup.LayoutParams hotparams = showList.getLayoutParams();
			hotparams.height = hottotalHeight + (showList.getDividerHeight() * (hotlistAdapter.getCount() - 1));
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					showList.setLayoutParams(hotparams);
				}
			});
		}
	}

	/**
	 * 
	 * 子项列表适配器 列表单选框实现
	 * 
	 */
	private class RadioAdapter extends BaseAdapter {

		private Context context;

		public RadioAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return dataLists.size();
		}

		@Override
		public Object getItem(int arg0) {
			return dataLists.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			RadioHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.show_data_acqui_item, null);
				holder = new RadioHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (RadioHolder) convertView.getTag();
			}
			holder.showName.setText(dataLists.get(position).getShowName());
			holder.timeValue.setText(dataLists.get(position).getTimeInterval());
			return convertView;
		}

	}

	/**
	 * 开关
	 * 
	 * @author jianchao.wang
	 * 
	 */
	private class RadioHolder {
		private TextView showName;
		private TextView timeValue;

		public RadioHolder(View view) {
			this.showName = (TextView) view.findViewById(R.id.ItemTitle);
			this.timeValue = (TextView) view.findViewById(R.id.time_value);
		}
	}

	@Override
	public void onClick(View v) {
		CheckBox buttonView = (CheckBox) v;
		switch (v.getId()) {
		case R.id.sch_ckb:
			config.setschInfo(buttonView.isChecked() ? "1" : "0");
			break;
		case R.id.rlc_pdu_ckb:
			config.setRLCPDUInfo(buttonView.isChecked() ? "1" : "0");
			break;
		case R.id.mac_pdu_ckb:
			config.setMACPDUInfo(buttonView.isChecked() ? "1" : "0");
			break;
		case R.id.ml1_ckb:
			config.setML1Info(buttonView.isChecked() ? "1" : "0");
			break;
		case R.id.pdcp_pdu_ckb:
			config.setPDCPPDUInfo(buttonView.isChecked() ? "1" : "0");
			break;
		case R.id.grant_ckb:
			config.setgrantList(buttonView.isChecked() ? "1" : "0");
			break;
		case R.id.mcs_ckb:
			config.setmcsStatis(buttonView.isChecked() ? "1" : "0");
			break;
		case R.id.l3_ckb:
			config.setl3msg(buttonView.isChecked() ? "1" : "0");
			break;
		case R.id.dci_ckb:
			config.setdciInfo(buttonView.isChecked() ? "1" : "0");
			break;
		case R.id.rb_ckb:
			config.setrrbInfo(buttonView.isChecked() ? "1" : "0");
			break;
		case R.id.cusom_ckb:
			config.setisPara(buttonView.isChecked() ? "1" : "0");
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(SysRoutineActivity.SHOW_DATA_TAB);
			this.sendBroadcast(intent);
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

}
