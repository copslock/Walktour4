package com.walktour.gui.setting.sysroutine;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.walktour.Utils.APNOperate;
import com.walktour.control.config.ConfigAPN;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 接入点设置,修改当前类不要忘了同步修改dialog目录下的SysRoutineAPNDialog类文件
 * 
 * @author jianchao.wang
 * 
 */
public class SysRoutineAPNActivity extends BasicActivity {

	private ConfigAPN config;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mutily_listview);
		getConfig();

		findView();
	} // end onCreated

	@Override
	public void onStart() {
		getConfig();

		super.onStart();
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	@Override
	public void onResume() {
		getConfig();
		findView();
		super.onResume();
	}

	public void getConfig() {
		this.config = ConfigAPN.getInstance();

	}

	private void findView() {

		// 绑定Layout里面的ListView
		ListView list = (ListView) findViewById(R.id.ListView01);

		// 获取listview 的各个item标题
		String[] title = { getResources().getString(R.string.sys_setting_internt_apn),
				getResources().getString(R.string.sys_setting_internt_wapAPN) };

		String[] itemText;
		itemText = new String[] { config.getDataAPN(), config.getWapAPN() };

		// 生成动态数组，每个数组单元对应一个item
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < title.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemTitle", title[i]);
			map.put("ItemText", itemText[i]);
			listItem.add(map);
		}
		// 生成适配器的Item和动态数组对应的元素
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,// 数据源
				R.layout.listview_item_style8,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "ItemTitle", "ItemText" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.ItemTitle, R.id.ItemText });

		// 添加并且显示
		list.setAdapter(listItemAdapter);
		// 添加点击item事件
		list.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("deprecation")
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				showDialog(arg2);
			}
		});

	}

	/***************************************************
	 * 继承方法: 重写activity的onCreateDialog弹出对话框 *
	 * 
	 * @see com.walktour.framework.ui.BasicActivity#onCreateDialog(int)
	 ****************************************************/
	@Override
	@SuppressWarnings("deprecation")
	protected Dialog onCreateDialog(int id) {
		APNOperate apn = APNOperate.getInstance(SysRoutineAPNActivity.this);
		final String[] apn_names = apn.getAPNNameListByFirstEmpty(SysRoutineAPNActivity.this);

		switch (id) {

		case 1:
			return new BasicDialog.Builder(SysRoutineAPNActivity.this)
					.setTitle(R.string.sys_setting_internt_wapAPN)
					.setSingleChoiceItems(apn_names, apn.getPositonFirstEmpty(config.getWapAPN()), new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							config.setWapAPN(apn_names[which]);
							getConfig();
							findView();
							dialog.dismiss();
						}
					}).create();

		case 0:
			return new BasicDialog.Builder(SysRoutineAPNActivity.this).setTitle(R.string.sys_setting_internt_apn)
					.setSingleChoiceItems(apn_names, apn.getPositonFirstEmpty(config.getDataAPN()), new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							config.setDataAPN(apn_names[which]);
							getConfig();
							findView();
							dialog.dismiss();
						}
					}).create();
		}

		return null;
	}// end Dialog onCreateDialog

}