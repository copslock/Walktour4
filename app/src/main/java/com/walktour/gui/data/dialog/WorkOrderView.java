package com.walktour.gui.data.dialog;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dinglicom.data.model.RecordBuild;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.treeview.AndroidTreeView;
import com.walktour.framework.view.treeview.TreeNode;
import com.walktour.framework.view.treeview.TreeNode.TreeNodeClickListener;
import com.walktour.gui.R;
import com.walktour.gui.data.FilterKey;

import java.util.ArrayList;
import java.util.List;

public class WorkOrderView extends BaseView{

	private View mView;
	private LinearLayout mContentLayout;
	private ArrayList<RecordBuild> mRecordBuilds = new ArrayList<RecordBuild>();
	private AndroidTreeView treeView;
	
	public WorkOrderView(Context context, String type) {
		super(context, type);
		init();
	}
	
	public View getView() {
		return this.mView;
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_clear:
			clear();
			if (mCallBack != null) {
				mCallBack.onClear();
			}
			break;

		case R.id.btn_filter:
			save();
			if (mCallBack != null) {
				mCallBack.onSummit();
			}
			break;

		default:
			break;
		}
		
	}
	
	private void init() {
		initData();
		mView = inflater.inflate(R.layout.work_order_view_root, null);
		mContentLayout = (LinearLayout)mView.findViewById(R.id.content_list);
		this.mView.findViewById(R.id.btn_clear).setOnClickListener(this);
		this.mView.findViewById(R.id.btn_filter).setOnClickListener(this);
		try {
			mContentLayout.addView(getTreeView(type));
		}catch (Exception ex){
			LogUtil.w("WorkOrderView",ex.getMessage());
		}
	}

	private void initData() {
		mRecordBuilds.clear();
		mRecordBuilds.addAll(mDBManager.getRecordBuilds(type));
	}
	
	private void clear() {
		if (enable()) {
			for (int i = 0; i < treeView.selectedList.size(); i++) {
				View v = treeView.selectedList.get(i).getViewHolder().getView();
				((CheckBox)v.findViewById(R.id.check)).setChecked(false);
			}
		}
		mPreferences.edit().putBoolean(FilterKey.KEY_IS_WORK_ORDER_SETTING + type, false).commit();
		mPreferences.edit().putString(FilterKey.KEY_WORK_ORDER_SELECTED + type, "").commit();
	}
	
	private void save() {
		if (!enable()) return;
		String node_ids = "";
		List<TreeNode> selectedList = treeView.selectedList;
		for (int i = 0; i < selectedList.size(); i++) {
			WorkOrderHolder.TreeItem item = (WorkOrderHolder.TreeItem) selectedList.get(i).getValue();
			node_ids += item.item.node_id + ",";
		}
		if (node_ids.contains(",")) {
			node_ids = node_ids.substring(0, node_ids.lastIndexOf(","));
		}
		mPreferences.edit().putBoolean(FilterKey.KEY_IS_WORK_ORDER_SETTING + type, true).commit();
		mPreferences.edit().putString(FilterKey.KEY_WORK_ORDER_SELECTED + type, node_ids).commit();
	}
	
	public boolean enable() {
		return mRecordBuilds.size() > 0;
	}
	
	private View getTreeView(String type) {
		View view = null;
		if (!enable()) {
			view = inflater.inflate(R.layout.note_text, null);
			((TextView)view.findViewById(R.id.txt_note)).setText(mContext.getResources().getString(R.string.str_note_text_no_workorder_info));
			return view;
		}
		if (type.equals(SceneType.Anhui.name())) {
			view = getType2View();
		} else if (type.equals(SceneType.BTU.name())) {
			view = getType1View();
		}  else if (type.equals(SceneType.Huawei.name())) {
			view = getType2View();
		}  else if (type.equals(SceneType.SingleSite.name())) {
			view = getType2View();
		}  else if (type.equals(SceneType.Fujian.name())) {
			view = getType2View();
		} else if (type.equals(SceneType.ATU.name())) {
			view = getType2View();
		}
		
		return view;
	}

	
	/**
	 * 获取工单的筛选数据项(一级可选)
	 * @return
	 */
	private View getType1View() {
		String selected_node_ids = mPreferences.getString(FilterKey.KEY_WORK_ORDER_SELECTED + type, "");
		TreeNode root = TreeNode.root();
		root.setSelectable(true);
		for (RecordBuild recordBuild : mRecordBuilds) {
			if (recordBuild.parent_id.equals("0")) {
				TreeItemModel parentModel = new TreeItemModel();
				parentModel.node_id = recordBuild.node_id;
				parentModel.parent_id = recordBuild.parent_id;
				parentModel.name = recordBuild.node_name;
				parentModel.node_info = recordBuild.node_info;
				parentModel.isEnableChoose = true;
				parentModel.isSelected = selected_node_ids.contains(recordBuild.node_id);
				TreeNode parent = new TreeNode(new WorkOrderHolder.TreeItem(0, parentModel));
				root.addChild(parent);
			} 
//			else {
//				TreeItemModel childModel = new TreeItemModel();
//				childModel.node_id = recordBuild.node_id;
//				childModel.parent_id = recordBuild.parent_id;
//				childModel.name = recordBuild.node_name;
//				childModel.node_info = recordBuild.node_info;
//				childModel.isEnableChoose = true;
//				childModel.isSelected = false;
//				TreeNode child = new TreeNode(new WorkOrderHolder.TreeItem(0, childModel));
//				addChild(root, child);
//			}
		}
		treeView = new AndroidTreeView(mContext, root);
		treeView.setDefaultViewHolder(WorkOrderHolder.class);
		treeView.setDefaultContainerStyle(R.style.TreeNodeStyleDivided);
		treeView.setDefaultNodeClickListener(nodeClickListener);
		return treeView.getView();
	}
	
	
	/**
	 * 获取工单的筛选数据项(二级可选)
	 * @return
	 */
	private View getType2View() {
		String selected_node_ids = mPreferences.getString(FilterKey.KEY_WORK_ORDER_SELECTED + type, "");
		TreeNode root = TreeNode.root();
		root.setSelectable(true);
		for (RecordBuild recordBuild : mRecordBuilds) {
			if (recordBuild.parent_id.equals("0")) {
				TreeItemModel parentModel = new TreeItemModel();
				parentModel.node_id = recordBuild.node_id;
				parentModel.parent_id = recordBuild.parent_id;
				parentModel.name = recordBuild.node_name;
				parentModel.node_info = recordBuild.node_info;
				parentModel.isEnableChoose = false;
				parentModel.isSelected = false;
				TreeNode parent = new TreeNode(new WorkOrderHolder.TreeItem(0, parentModel));
				root.addChild(parent);
			} else {
				TreeItemModel childModel = new TreeItemModel();
				childModel.node_id = recordBuild.node_id;
				childModel.parent_id = recordBuild.parent_id;
				childModel.name = recordBuild.node_name;
				childModel.node_info = recordBuild.node_info;
				childModel.isEnableChoose = true;
				childModel.isSelected = selected_node_ids.contains(recordBuild.node_id);
				TreeNode child = new TreeNode(new WorkOrderHolder.TreeItem(0, childModel));
				addChild(root, child);
			}
		}
		treeView = new AndroidTreeView(mContext, root);
		treeView.setDefaultViewHolder(WorkOrderHolder.class);
		treeView.expandLevel(1);
		treeView.setDefaultContainerStyle(R.style.TreeNodeStyleDivided);
		
		treeView.setDefaultNodeClickListener(nodeClickListener);
		return treeView.getView();
	}
	
	private void addChild(TreeNode root, TreeNode node) {
		for (TreeNode n : root.getChildren()) {
			WorkOrderHolder.TreeItem parentModel = (WorkOrderHolder.TreeItem)n.getValue();
			WorkOrderHolder.TreeItem childModel = (WorkOrderHolder.TreeItem)node.getValue();
			if (parentModel.item.node_id.equals(childModel.item.parent_id)) {
				n.addChild(node);
				return;
			}
		}
	}

	private TreeNodeClickListener nodeClickListener = new TreeNodeClickListener() {
		
		@Override
		public void onClick(TreeNode node, Object value) {
			View v = node.getViewHolder().getView();
            if (node.getLevel() != 1 || ((WorkOrderHolder.TreeItem)value).item.isEnableChoose) {
            	return;
            }
            if (node.isExpanded()) {
            	v.findViewById(R.id.parent_divider).setVisibility(View.VISIBLE);
            } else {
            	v.findViewById(R.id.parent_divider).setVisibility(View.GONE);
            }
            treeView.toggleNodeCustom(node);
		}
	};

	private ClickListenerCallBack mCallBack;
	public void setClickListenerCallBack(ClickListenerCallBack callback) {
		this.mCallBack = callback;
	}
}
