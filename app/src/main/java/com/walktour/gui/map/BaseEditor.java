package com.walktour.gui.map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.base.util.LogUtil;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.applet.MySimpleAdapter;
import com.walktour.gui.newmap.basestation.BaseDataParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * 基站数据编辑器
 */
public class BaseEditor extends BasicActivity {
	private String tag = "BaseEditor";
	// 基站列表
	private Vector<BaseStationDetail> baseLoad = BaseDataParser.getDataLoad();
	private Vector<BaseStationDetail> baseDisplay = BaseDataParser.getDataDisplay();

	// ListView
	private ListView listView;// 显示所有文件
	private MySimpleAdapter checkListItemAdapter;// listView用到的Adapter
	private ArrayList<HashMap<String, Object>> itemArrayList;// checkListItemAdapter的参数

	// Views
	private Button btnSearch;
	private Button btnCheckAll;
	private Button btnUnCheck;
	private Button btnDelete;
	private Button btnLoad;
	private EditText editSearch;

	// 过滤条件

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_base);
	}

	@Override
	public void onStart() {
		super.onStart();
		findView();
	}

	private void findView() {
		// ListView
		listView = (ListView) findViewById(R.id.ListView);
		itemArrayList = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < baseDisplay.size(); i++) {
			BaseStationDetail baseData = baseDisplay.get(i);
			HashMap<String, Object> map = new HashMap<String, Object>();
			// 如果是CDMA类型的基站
			if (baseData.main.netType == BaseStation.NETTYPE_CDMA) {
				map.put("ItemSite", baseData.main.name);
				map.put("ItemCell", baseData.cellName);
				map.put("ItemPN", "PN:" + baseData.pn);
				map.put("ItemCheckble", baseData.checked);
				itemArrayList.add(map);
			}
		}

		checkListItemAdapter = new MySimpleAdapter(this, itemArrayList, R.layout.listview_item_style14, new String[] {
				"ItemSite", "ItemCell", "ItemPN", "ItemCheckble" }, new int[] { R.id.ItemTitle, R.id.ItemContent1,
				R.id.ItemContent2, R.id.ItemCheckble });
		listView.setAdapter(checkListItemAdapter);

		listView.setOnItemClickListener(itemClickListener);

		// EditText & Buttons
		editSearch = initEditText(R.id.EditTextSearch);
		btnSearch = initButton(R.id.ButtonSearch);
		btnCheckAll = initButton(R.id.ButtonCheckAll);
		btnUnCheck = initButton(R.id.ButtonUnCheckAll);
		btnDelete = initButton(R.id.ButtonDelete);
		btnLoad = initButton(R.id.ButtonLoad);
		btnSearch.setOnClickListener(btnListener);
		btnCheckAll.setOnClickListener(btnListener);
		btnUnCheck.setOnClickListener(btnListener);
		btnDelete.setOnClickListener(btnListener);
		btnLoad.setOnClickListener(btnListener);
	}

	/**
	 * 显示编辑框
	 * 
	 * @param cellName
	 *          小区名称
	 * */
	private void showEditDialog(final String cellName) {
		// 找到对应小区
		int index = 0;
		for (int i = 0; i < baseLoad.size(); i++) {
			BaseStationDetail detail = baseLoad.get(i);
			if (detail.cellName.equals(cellName)) {
				index = i;
				break;
			}
		}
		final BaseStationDetail detail = baseLoad.get(index);

		LayoutInflater factory = LayoutInflater.from(this);
		final View view = factory.inflate(R.layout.alert_dialog_base_edit, null);
		TextView txt1 = (TextView) view.findViewById(R.id.TextView01);
		TextView txt2 = (TextView) view.findViewById(R.id.TextView02);
		TextView txt3 = (TextView) view.findViewById(R.id.TextView03);
		TextView txt4 = (TextView) view.findViewById(R.id.TextView04);
		TextView txt5 = (TextView) view.findViewById(R.id.TextView05);
		TextView txt6 = (TextView) view.findViewById(R.id.TextView06);
		final EditText editSite = (EditText) view.findViewById(R.id.EditText01);
		final EditText editCell = (EditText) view.findViewById(R.id.EditText02);
		final EditText editPn = (EditText) view.findViewById(R.id.EditText03);
		final EditText editLon = (EditText) view.findViewById(R.id.EditText04);
		final EditText editLat = (EditText) view.findViewById(R.id.EditText05);
		final EditText editAzi = (EditText) view.findViewById(R.id.EditText06);
		txt1.setText(getString(R.string.map_base_name));
		txt2.setText(getString(R.string.map_base_cellname));
		txt3.setText(getString(R.string.map_base_pn));
		txt4.setText(getString(R.string.map_base_longitude));
		txt5.setText(getString(R.string.map_base_latitude));
		txt6.setText(getString(R.string.map_base_azimuth));
		// 如果不是CDMA基站隐藏PN
		if (detail.main.netType == BaseStation.NETTYPE_CDMA) {
			editPn.setText(detail.pn);
		} else {
			txt3.setVisibility(View.GONE);
			editPn.setVisibility(View.GONE);
		}
		editSite.setText(detail.main.name);
		editCell.setText(detail.cellName);
		editLon.setText(String.valueOf(detail.main.longitude));
		editLat.setText(String.valueOf(detail.main.latitude));
		editAzi.setText(String.valueOf(detail.bearing));

		new AlertDialog.Builder(BaseEditor.this).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						boolean isCdma = (detail.main.netType == BaseStation.NETTYPE_CDMA);

						// 验证填写的信息是否为空
						if (editSite.getText().toString().trim().length() == 0
								|| editCell.getText().toString().trim().length() == 0
								|| editLon.getText().toString().trim().length() == 0
								|| editLat.getText().toString().trim().length() == 0
								|| editAzi.getText().toString().trim().length() == 0
								|| (isCdma && editCell.getText().toString().trim().length() == 0)) {
							dialog.dismiss();
							showEditDialog(cellName);
							return;
						}

						try {
							int pn = 0;
							;
							if (isCdma) {
								pn = Integer.parseInt(editPn.getText().toString());
							}
							double longitude = Double.parseDouble(editLon.getText().toString());
							double latitude = Double.parseDouble(editLat.getText().toString());
							int azimuth = Integer.parseInt(editAzi.getText().toString());

							// 验证填写的信息是否有效值
							if (longitude > 180 || longitude < -180 || latitude > 180 || latitude < -180 || azimuth > 360) {
								dialog.dismiss();
								showEditDialog(cellName);
							} else {
								// 修改到基站信息
								if (isCdma) {
									detail.pn = String.valueOf(pn);
									for (int i = 0; i < itemArrayList.size(); i++) {
										HashMap<String, Object> map = itemArrayList.get(i);
										String itemCell = (String) map.get("ItemCell");
										if (itemCell.equals(cellName)) {
											itemArrayList.get(i).put("ItemPN", "PN:" + detail.pn);
											break;
										}
									}
								}
								detail.main.name = editSite.getText().toString();
								detail.cellName = editCell.getText().toString();
								detail.main.longitude = longitude;
								detail.main.latitude = latitude;
								detail.bearing = azimuth;
								// 刷新界面
								for (int i = 0; i < itemArrayList.size(); i++) {
									HashMap<String, Object> map = itemArrayList.get(i);
									String itemCell = (String) map.get("ItemCell");
									if (itemCell.equals(cellName)) {
										itemArrayList.get(i).put("ItemSite", detail.main.name);
										itemArrayList.get(i).put("ItemCell", detail.cellName);
										break;
									}
								}
								checkListItemAdapter.notifyDataSetChanged();
							}
						} catch (Exception e) {
							dialog.dismiss();
							showEditDialog(cellName);
						}
					}
				}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
	}

	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			String cellName = (String) itemArrayList.get(arg2).get("ItemCell");
			showEditDialog(cellName);
		}

	};

	private OnClickListener btnListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			// 查询
			case R.id.ButtonSearch:
				String[] keywords = editSearch.getText().toString().split(" ");// 关键字之间可用空格取或
				baseDisplay.clear();
				for (int i = 0; i < baseLoad.size(); i++) {
					BaseStationDetail detail = baseLoad.get(i);
					boolean isInKeyword = false;
					for (int j = 0; j < keywords.length; j++) {
						if (detail.main.name.contains(keywords[j])) {
							isInKeyword = true;
							break;
						}
					}
					if (isInKeyword) {
						baseDisplay.add(detail);
					}
				}
				findView();
				break;
			case R.id.ButtonCheckAll:
				for (int i = 0; i < itemArrayList.size(); i++) {
					HashMap<String, Object> map = itemArrayList.get(i);
					map.put("ItemCheckble", true);
				}
				checkListItemAdapter.notifyDataSetChanged();
				break;
			case R.id.ButtonUnCheckAll:
				for (int i = 0; i < itemArrayList.size(); i++) {
					HashMap<String, Object> map = itemArrayList.get(i);
					map.put("ItemCheckble", false);
				}
				checkListItemAdapter.notifyDataSetChanged();
				break;

			// 删除
			case R.id.ButtonDelete:
				for (int i = itemArrayList.size() - 1; i >= 0; i--) {
					HashMap<String, Object> map = itemArrayList.get(i);
					boolean isCheck = (Boolean) map.get("ItemCheckble");
					try {
						if (isCheck) {
							itemArrayList.remove(i);
							baseLoad.remove(baseDisplay.remove(i));
						}
					} catch (Exception e) {
						LogUtil.w(tag, e.toString());
					}
				}
				checkListItemAdapter.notifyDataSetChanged();
				break;

			// 加载
			case R.id.ButtonLoad:
				for (int i = itemArrayList.size() - 1; i >= 0; i--) {
					HashMap<String, Object> map = itemArrayList.get(i);
					boolean isCheck = (Boolean) map.get("ItemCheckble");
					try {
						if (!isCheck) {
							baseDisplay.remove(i);
						}
					} catch (Exception e) {
						LogUtil.w(tag, e.toString());
					}
				}
				sendBroadcast(new Intent(MapView.ACTION_BASE_REDRAW));
				finish();
				break;
			}
		}

	};

}
