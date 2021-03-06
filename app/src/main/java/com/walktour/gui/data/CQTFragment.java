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
import com.walktour.Utils.WalkStruct.TestType;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.PopDialogView;
import com.walktour.framework.view.PopDialogView.onDismissListener;
import com.walktour.framework.view.treeview.TreeNode;
import com.walktour.gui.R;
import com.walktour.gui.data.dialog.BaseView.ClickListenerCallBack;
import com.walktour.gui.data.dialog.CheckView;
import com.walktour.gui.data.dialog.DisplayView;
import com.walktour.gui.data.dialog.FilterView;
import com.walktour.gui.data.dialog.FloorView;
import com.walktour.gui.data.dialog.MoreOptionPopWin;
import com.walktour.gui.data.dialog.ScreeningView;
import com.walktour.gui.data.model.DataModel;

import java.util.ArrayList;
import java.util.List;

public class CQTFragment extends FragmentBase implements onDismissListener{

	private List<Button> operatorButtons = new ArrayList<Button>();
	private PopDialogView mScreeningViewDialog;
	private PopDialogView mCheckViewDialog;
	private PopDialogView mFilterViewDialog;
	private PopDialogView mDisplayViewDialog;
	private PopDialogView mFloorViewDialog;
	private FloorView mFloorView;
	private DisplayView dView;
	private ScreeningView mScreeningView;
	private boolean isFirstVisible = true;
	private LoadDataAsyncTask mLoadDataAsyncTask;
	private ArrayList<DataModel> localList = new ArrayList<DataModel>();
	
	public CQTFragment() {
		FLAG = TestType.CQT.name();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
		OnClickListener clickListener = new OnClickListener() {

			@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.Button01://??????dialog
				mScreeningViewDialog.showAsDropDown(view, 0, DensityUtil.dip2px(mActivity, 240));
				changeCheckBoxState(0);
				break;
			case R.id.Button02://????????????dialog
				mCheckViewDialog.showAsDropDown(view, 0, DensityUtil.dip2px(mActivity, 240));
				changeCheckBoxState(1);
				break;
			case R.id.Button03://????????????dialog
				mFilterViewDialog.showAsDropDown(view, 0, DensityUtil.dip2px(mActivity, 150));
				changeCheckBoxState(2);
				break;
			case R.id.Button_other://??????dialog
				if (mFloorView.enable()) {
					mFloorViewDialog.showAsDropDown(view, 0, DensityUtil.dip2px(mActivity, 300));//DensityUtil.dip2px(mActivity, 300)
				} else {
					mFloorViewDialog.showAsDropDown(view, 0, 0);
				}
				changeCheckBoxState(3);
				
				break;
				
			case R.id.Button04://??????dialog
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.file_list, null);
		initButtons(rootView);
		ListView lv = (ListView)rootView.findViewById(R.id.ListView01);
		mAdapter = new TreeViewAdapter(mActivity, root);
		lv.setAdapter(mAdapter);
		lv.setOnItemClickListener(this);
		if (isFirstVisible) {
			initAllPopWindows();
			isFirstVisible = false;
		}
		return rootView;
	}
	
	private void initButtons(final View root) {
		operatorButtons.clear();
		Button btn1 = (Button)root.findViewById(R.id.Button01);
		Button btn2 = (Button)root.findViewById(R.id.Button02);
		Button btn3 = (Button)root.findViewById(R.id.Button03);
		Button other = (Button)root.findViewById(R.id.Button_other);
		other.setVisibility(View.VISIBLE);
		root.findViewById(R.id.divider_other).setVisibility(View.VISIBLE);
		Button btn4 = (Button)root.findViewById(R.id.Button04);
		btn1.setText(getResources().getString(R.string.data_manager_screening));
		btn2.setText(getResources().getString(R.string.data_manager_check));
		btn3.setText(getResources().getString(R.string.data_manager_filter));
		btn4.setText(getResources().getString(R.string.data_manager_display));
		other.setText(getResources().getString(R.string.data_manager_floor));
		operatorButtons.add(btn1);
		operatorButtons.add(btn2);
		operatorButtons.add(btn3);
		operatorButtons.add(other);
		operatorButtons.add(btn4);
		for (int i = 0; i < operatorButtons.size(); i++) {
			operatorButtons.get(i).setOnClickListener(clickListener);;
		}
		reset();

	}
	
	/**
	 * ??????????????????????????????pop???
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
	 * ????????????
	 */
	protected void refreshData() {
		LogUtil.i("CQTFragment","--------refreshData----------");
		mLoadDataAsyncTask = new LoadDataAsyncTask();
		mLoadDataAsyncTask.execute(new Boolean[]{false});
	}
	
	/**
	 * ??????dialog
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
			public void onMark(boolean mark) {}
		});
	}
	/**
	 * ????????????dialog
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
	 * ????????????dialog
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
			public void onMark(boolean mark) {}
			
			@Override
			public void onClear() {
				mFilterViewDialog.closeView();
				refreshData();
			}
		});
	}
	/**
	 * ????????????dialog
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
	 * ????????????dialog
	 */
	private void initFloorView() {
		mFloorView = new FloorView(mActivity, FLAG);
		View v = mFloorView.getView();
		mFloorViewDialog.setView(v);
		mFloorView.setClickListenerCallBack(new ClickListenerCallBack() {
			
			@Override
			public void onSummit() {
				mFloorViewDialog.closeView();
				refreshData();
			}
			
			@Override
			public void onMark(boolean mark) {}
			
			@Override
			public void onClear() {
				mFloorViewDialog.closeView();
				refreshData();
			}
		});
	}
	
	/**
	 * ????????????????????????
	 * @param mark
	 */
	private void mark(boolean mark) {
		mAdapter.setMarkHighLight(mark);
		mAdapter.notifyDataSetChanged();
		if (!mark) return;
		String selectedKeys = sp.getString(FilterKey.KEY_EXCEPTION_SELECTED + FLAG, "");
		List<TreeNode> list = getAdapter().getDatas();
		for (TreeNode node : list) {
				if (node.getLevel() == 1) {
					continue;
				}
				DataModel d = (DataModel)node.getValue();
				if (d.isFolder) {
					for (TreeNode child : node.getChildren()) {
						DataModel dChild = (DataModel)child.getValue();
						ArrayList<RecordAbnormal> abnormals = dChild.testRecord.getRecordAbnormals();
						for(RecordAbnormal ra : abnormals) {
							if (selectedKeys.contains(ra.abnormal_type + "")) {
								dChild.isMark = true;
							}
						}
					}
					
				} else {
					ArrayList<RecordAbnormal> abnormals = d.testRecord.getRecordAbnormals();
					for(RecordAbnormal ra : abnormals) {
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
//			list = mDbManager.getFiles(false, FLAG, getAllFilterCondiction(FLAG));
			list = getNewFileListInstance();
			localList.addAll(list);
		}
		
		for (int i = 0; i < list.size(); i++) {
			DataModel parentDataModel = list.get(i);
//			parentDataModel.titleTime = list.get(i).displayName;
//			parentDataModel.titleOrder = list.get(i).workOrderNum;
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
	 * ????????? ** ??????chekcbox ????????????
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
	 * ???????????????????????? ???????????????????????????
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
	 * ???????????????????????????????????????
	 * @return
	 */
	private boolean[] getSettingState() {
		boolean[] states = new boolean[5];
		states[0] = sp.getBoolean(FilterKey.KEY_IS_SCREEN_SETTING + FLAG, false);
		states[1] = sp.getBoolean(FilterKey.KEY_IS_CHECK_SETTING + FLAG, false);
		states[2] = sp.getBoolean(FilterKey.KEY_IS_FILTER_SETTING + FLAG, false);
		states[3] = sp.getBoolean(FilterKey.KEY_IS_FLOOR_SETTING + FLAG, false);
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
			boolean local = (boolean)arg0[0];
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
