package com.walktour.gui.data;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.dinglicom.data.model.RecordAbnormal;
import com.walktour.Utils.DensityUtil;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.framework.view.PopDialogView;
import com.walktour.framework.view.PopDialogView.onDismissListener;
import com.walktour.framework.view.treeview.TreeNode;
import com.walktour.gui.R;
import com.walktour.gui.data.dialog.BaseView.ClickListenerCallBack;
import com.walktour.gui.data.dialog.CheckView;
import com.walktour.gui.data.dialog.DisplayView;
import com.walktour.gui.data.dialog.FilterView;
import com.walktour.gui.data.dialog.MetroView;
import com.walktour.gui.data.dialog.MoreOptionPopWin;
import com.walktour.gui.data.dialog.ScreeningView;
import com.walktour.gui.data.model.DataModel;

import java.util.ArrayList;
import java.util.List;

/****
 * 地铁数据管理
 * 
 * @author weirong.fan
 *
 */
public class MetroFragment extends FragmentBase implements onDismissListener {

	private List<Button> operatorButtons = new ArrayList<Button>();

	private PopDialogView mScreeningViewDialog;
	private PopDialogView mCheckViewDialog;
	private PopDialogView mFilterViewDialog;
	private PopDialogView mDisplayViewDialog;
	private PopDialogView mFloorViewDialog;

	private MetroView mMetroView;

	private DisplayView dView;
	private ScreeningView mScreeningView;

	private boolean isVisible;
	private boolean isStarted;
	private LoadDataAsyncTask mLoadDataAsyncTask;
	private ArrayList<DataModel> localList = new ArrayList<DataModel>();

	public MetroFragment() {
		FLAG = SceneType.Metro.name();
	}
	@Override
	public void onStart() {
		super.onStart();
		isStarted = true;
		if (isVisible){
			initAllPopWindows();
		}

	}

	@Override
	public void onStop() {
		super.onStop();
		isStarted = false;
	}


	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		isVisible = isVisibleToUser;
		if (isVisible && isStarted){
			initAllPopWindows();
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.Button01:// 筛选dialog
				mScreeningViewDialog.showAsDropDown(view, 0, DensityUtil.dip2px(mActivity, 240));
				changeCheckBoxState(0);
				break;
			case R.id.Button02:// 智能检测dialog
				mCheckViewDialog.showAsDropDown(view, 0, DensityUtil.dip2px(mActivity, 240));
				changeCheckBoxState(1);
				break;
			case R.id.Button03:// 无效过滤dialog
				mFilterViewDialog.showAsDropDown(view, 0, DensityUtil.dip2px(mActivity, 150));
				changeCheckBoxState(2);
				break;
			case R.id.Button_other:// 地铁线路dialog
				if (mMetroView.enable()) {
					mFloorViewDialog.showAsDropDown(view, 0, DensityUtil.dip2px(mActivity, 240));
				} else {
					mFloorViewDialog.showAsDropDown(view, 0, 0);
				}
				changeCheckBoxState(3);

				break;

			case R.id.Button04:// 显示dialog
				mDisplayViewDialog.showAsDropDown(view, 0, DensityUtil.dip2px(mActivity, 150));
				changeCheckBoxState(4);
				break;
			case R.id.radio3:
				Intent intentDate = new Intent(mActivity, DateSelector.class);
				intentDate.putExtra("flag", FLAG);
				startActivityForResult(intentDate, 1);
				break;

			default:
				break;
			}
		}
	};

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.file_list, null);
		initButtons(view);
		ListView lv = (ListView) view.findViewById(R.id.ListView01);
		mAdapter = new TreeViewAdapter(mActivity, root);
		lv.setAdapter(mAdapter);
		lv.setOnItemClickListener(this);
		return view;
	}

	private void initButtons(final View view) {
		operatorButtons.clear();
		Button btn1 = (Button) view.findViewById(R.id.Button01);
		Button btn2 = (Button) view.findViewById(R.id.Button02);
		Button btn3 = (Button) view.findViewById(R.id.Button03);
		Button other = (Button) view.findViewById(R.id.Button_other);
		other.setVisibility(View.VISIBLE);
		view.findViewById(R.id.divider_other).setVisibility(View.VISIBLE);
		Button btn4 = (Button) view.findViewById(R.id.Button04);
		btn1.setText(getResources().getString(R.string.data_manager_screening));
		btn2.setText(getResources().getString(R.string.data_manager_check));
		btn3.setText(getResources().getString(R.string.data_manager_filter));
		btn4.setText(getResources().getString(R.string.data_manager_display));
		other.setText(getResources().getString(R.string.metro_route));
		operatorButtons.add(btn1);
		operatorButtons.add(btn2);
		operatorButtons.add(btn3);
		operatorButtons.add(other);
		operatorButtons.add(btn4);
		for (int i = 0; i < operatorButtons.size(); i++) {
			operatorButtons.get(i).setOnClickListener(clickListener); 
		}
		reset();

	}


	/**
	 * 初始化所有过滤条件的pop框
	 */
	private void initAllPopWindows() {
		moreOptionPopWin = new MoreOptionPopWin(mActivity);
		mScreeningViewDialog = new PopDialogView(mActivity);
		mCheckViewDialog = new PopDialogView(mActivity);
		mFilterViewDialog = new PopDialogView(mActivity);
		mDisplayViewDialog = new PopDialogView(mActivity);
		mFloorViewDialog = new PopDialogView(mActivity);
		mScreeningViewDialog.setOnDismissListener(this);
		mCheckViewDialog.setOnDismissListener(this);
		mFilterViewDialog.setOnDismissListener(this);
		mDisplayViewDialog.setOnDismissListener(this);
		mFloorViewDialog.setOnDismissListener(this);

		initScreeningView();
		initCheckView();
		initFilterView();
		initDisplayView();
		initFloorView();

	}

	/**
	 * 刷新数据
	 */
	protected void refreshData() {
		System.out.println("地铁进入刷新数据");
		mLoadDataAsyncTask = new LoadDataAsyncTask();
		mLoadDataAsyncTask.execute(new Boolean[] { false });
	}

	/**
	 * 筛选dialog
	 */
	private void initScreeningView() {
		mScreeningView = new ScreeningView(mActivity, FLAG);
		View view = mScreeningView.getView();
		mScreeningViewDialog.setView(view);
		mScreeningView.mTimeView.getSelfRadioButton().setOnClickListener(clickListener);
		mScreeningView.setClickListenerCallBack(new ClickListenerCallBack() {

			@Override
			public void onSummit() {
				mScreeningViewDialog.closeView();
				refreshData();
			}

			@Override
			public void onClear() {
				mScreeningViewDialog.closeView();
				refreshData();
			}

			@Override
			public void onMark(boolean mark) {
			}
		});
	}

	/**
	 * 智能过滤dialog
	 */
	private void initCheckView() {
		CheckView checkView = new CheckView(mActivity, FLAG);
		View v = checkView.getView();
		mCheckViewDialog.setView(v);
		checkView.setClickListenerCallBack(new ClickListenerCallBack() {

			@Override
			public void onSummit() {
				mCheckViewDialog.closeView();
				refreshData();
			}

			@Override
			public void onMark(boolean mark) {
				mCheckViewDialog.closeView();
				mark(mark);
			}

			@Override
			public void onClear() {
				mCheckViewDialog.closeView();
				mAdapter.setMarkHighLight(false);
				refreshData();
			}
		});
	}

	/**
	 * 无效过滤dialog
	 */
	private void initFilterView() {
		FilterView fView = new FilterView(mActivity, FLAG);
		View v = fView.getView();
		mFilterViewDialog.setView(v);
		fView.setClickListenerCallBack(new ClickListenerCallBack() {

			@Override
			public void onSummit() {
				mFilterViewDialog.closeView();
				refreshData();
			}

			@Override
			public void onMark(boolean mark) {
			}

			@Override
			public void onClear() {
				mFilterViewDialog.closeView();
				refreshData();
			}
		});
	}

	/**
	 * 显示设置dialog
	 */
	private void initDisplayView() {
		dView = new DisplayView(mActivity, FLAG);
		View v = dView.getView();
		mDisplayViewDialog.setView(v);
		dView.setClickListenerCallBack(new ClickListenerCallBack() {

			@Override
			public void onSummit() {
				boolean showExection = sp.getBoolean(FilterKey.KEY_EXCEPTION + FLAG, true);
				mAdapter.setAbnormalDisplayMode(showExection);
				mAdapter.notifyDataSetChanged();
				mDisplayViewDialog.closeView();
			}

			@Override
			public void onMark(boolean mark) {
			}

			@Override
			public void onClear() {
				boolean showExection = sp.getBoolean(FilterKey.KEY_EXCEPTION + FLAG, true);
				mAdapter.setAbnormalDisplayMode(showExection);
				mAdapter.notifyDataSetChanged();
				mDisplayViewDialog.closeView();
			}
		});
	}

	/**
	 * 显示设置dialog
	 */
	private void initFloorView() {
		mMetroView = new MetroView(mActivity, FLAG);
		View v = mMetroView.getView();
		mFloorViewDialog.setView(v);
		mMetroView.setClickListenerCallBack(new ClickListenerCallBack() {

			@Override
			public void onSummit() {
				mFloorViewDialog.closeView();
				refreshData();
			}

			@Override
			public void onMark(boolean mark) {
			}

			@Override
			public void onClear() {
				mFloorViewDialog.closeView();
				refreshData();
			}
		});
	}

	/**
	 * 标记异常数据高亮
	 * 
	 * @param mark
	 */
	private void mark(boolean mark) {
		mAdapter.setMarkHighLight(mark);
		mAdapter.notifyDataSetChanged();
		if (!mark)
			return;
		String selectedKeys = sp.getString(FilterKey.KEY_EXCEPTION_SELECTED + FLAG, "");
		List<TreeNode> list = getAdapter().getDatas();
		for (TreeNode node : list) {
			if (node.getLevel() == 1) {
				continue;
			}
			DataModel d = (DataModel) node.getValue();
			if (d.isFolder) {
				for (TreeNode child : node.getChildren()) {
					DataModel dChild = (DataModel) child.getValue();
					ArrayList<RecordAbnormal> abnormals = dChild.testRecord.getRecordAbnormals();
					for (RecordAbnormal ra : abnormals) {
						if (selectedKeys.contains(ra.abnormal_type + "")) {
							dChild.isMark = true;
						}
					}
				}

			} else {
				ArrayList<RecordAbnormal> abnormals = d.testRecord.getRecordAbnormals();
				for (RecordAbnormal ra : abnormals) {
					if (selectedKeys.contains(ra.abnormal_type + "")) {
						d.isMark = true;
					}
				}
			}
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	protected void getListDatas(boolean local) {

		String selectedAbnormalTypes = sp.getString(FilterKey.KEY_EXCEPTION_SELECTED + FLAG, "");
		ArrayList<DataModel> list = new ArrayList<DataModel>();
		if (local) {
			list.addAll(localList);
		} else {
			localList.clear();
			// list = mDbManager.getFiles(false, FLAG,
			// getAllFilterCondiction(FLAG));
			list = getNewFileListInstance();
			localList.addAll(list);
		}

		for (int i = 0; i < list.size(); i++) {
			DataModel parentDataModel = list.get(i);
			// parentDataModel.titleTime = list.get(i).displayName;
			// parentDataModel.titleOrder = list.get(i).workOrderNum;
			parentDataModel.isFirstLevel = true;
			TreeNode parent = new TreeNode(parentDataModel);
			for (int j = 0; j < list.get(i).getChild().size(); j++) {
				DataModel dataLevel2 = list.get(i).getChild().get(j);
				dataLevel2.isDeleteChecked = false;
				dataLevel2.isChecked = false;
				dataLevel2.isMark(selectedAbnormalTypes);
				TreeNode child = new TreeNode(dataLevel2);
				for (int k = 0; k < dataLevel2.getChild().size(); k++) {
					DataModel dataLevel3 = dataLevel2.getChild().get(k);
					dataLevel3.isDeleteChecked = false;
					dataLevel3.isChecked = false;
					dataLevel3.isMark(selectedAbnormalTypes);
					TreeNode child_div = new TreeNode(dataLevel3);
					child.addChild(child_div);
				}
				parent.addChild(child);
			}
			root.addChild(parent);
		}

	}

	public void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
		String timeRang = sp.getString(FilterKey.KEY_TIME_RANGE + FLAG, "");
		if (!timeRang.equals("")) {
			String[] tmps = timeRang.split("~");
			String str = tmps[0] + "\n" + tmps[1];
			mScreeningView.mTimeView.getSelfRadioButton().setText(str);
		}

	};

	@Override
	public void onDismiss() {
		reset();
		if (dView != null) {
			dView.updateState();
		}
	}

	/**
	 * 改变除 ** 外的chekcbox 选中状态
	 * 
	 * @param withoutPosition
	 */
	private void changeCheckBoxState(int withoutPosition) {
		boolean[] buttonStates = getSettingState();
		for (int i = 0; i < operatorButtons.size(); i++) {
			if (i == withoutPosition) {
				operatorButtons.get(i).setTextColor(mActivity.getResources().getColor(R.color.txt_high_light));
				operatorButtons.get(i).setBackgroundResource(R.drawable.spinner_ab_pressed_holo_light);
			} else {
				if (buttonStates[i]) {
					operatorButtons.get(i).setTextColor(mActivity.getResources().getColor(R.color.txt_high_light));
				} else {
					operatorButtons.get(i).setTextColor(mActivity.getResources().getColor(R.color.app_main_text_color));
				}
				operatorButtons.get(i).setBackgroundResource(R.drawable.spinner_ab_holo_light);
			}
		}
	}

	/**
	 * 设置顶端所有筛选 过滤等操作按钮状态
	 */
	private void reset() {
		boolean[] buttonStates = getSettingState();
		for (int i = 0; i < operatorButtons.size(); i++) {
			if (buttonStates[i]) {
				operatorButtons.get(i).setTextColor(mActivity.getResources().getColor(R.color.txt_high_light));
			} else {
				operatorButtons.get(i).setTextColor(mActivity.getResources().getColor(R.color.app_main_text_color));
			}
			operatorButtons.get(i).setBackgroundResource(R.drawable.spinner_ab_holo_light);
		}
	}

	/**
	 * 获取所有筛选条件的设置状态
	 * 
	 * @return
	 */
	private boolean[] getSettingState() {
		boolean[] states = new boolean[5];
		states[0] = sp.getBoolean(FilterKey.KEY_IS_SCREEN_SETTING + FLAG, false);
		states[1] = sp.getBoolean(FilterKey.KEY_IS_CHECK_SETTING + FLAG, false);
		states[2] = sp.getBoolean(FilterKey.KEY_IS_FILTER_SETTING + FLAG, false);
		states[3] = sp.getBoolean(FilterKey.KEY_IS_ROUTE_SETTING + FLAG, false);
		states[4] = sp.getBoolean(FilterKey.KEY_IS_DISPLAY_SETTING + FLAG, false);
		return states;
	}

	private class LoadDataAsyncTask extends AsyncTask<Boolean, Integer, View> {

		boolean abnormalDisplayMode = true;
		boolean mark = false;

		@Override
		protected void onPreExecute() {
			abnormalDisplayMode = sp.getBoolean(FilterKey.KEY_EXCEPTION + FLAG, true);
			mark = sp.getBoolean(FilterKey.KEY_IS_HIGH_LIGHT_MARK + FLAG, false);
			clear();
		}

		@Override
		protected View doInBackground(Boolean... arg0) {
			boolean local = (boolean) arg0[0];
			getListDatas(local);
			return null;
		}

		@Override
		protected void onPostExecute(View result) {
			mAdapter.setAbnormalDisplayMode(abnormalDisplayMode);
			mAdapter.setMarkHighLight(mark);
			mAdapter.expandLevel(1);
			mAdapter.notifyDataSetChanged();
		}

	}

}
