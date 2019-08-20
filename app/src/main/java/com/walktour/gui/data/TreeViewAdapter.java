package com.walktour.gui.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dinglicom.data.model.RecordAbnormal;
import com.walktour.Utils.DateUtil;
import com.walktour.Utils.DensityUtil;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.framework.view.treeview.TreeNode;
import com.walktour.gui.R;
import com.walktour.gui.analysis.commons.AnalysisCommons;
import com.walktour.gui.data.dialog.PopButton;
import com.walktour.gui.data.dialog.PopDialog;
import com.walktour.gui.data.model.DBManager;
import com.walktour.gui.data.model.DataModel;
import com.walktour.gui.setting.KPIResulActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressLint("InflateParams")
public class TreeViewAdapter extends BaseAdapter {

	private TreeNode mRoot;
	private Context mContext;
	private LayoutInflater inflater;
	private List<TreeNode> list = new ArrayList<TreeNode>();
	private boolean isShareMode = false;// 是否分享模式
	private boolean isDeleteMode = false;// 是否删除模式
	private boolean isTotalMode = false;//是否是统计模式
	private boolean isReplayMode = false;//是否回放模式
	private boolean isMarkHighLight  = false;//是否开启标记高亮显示
	private boolean isAbnormalDisplayMode = true;//true:显示异常数量 false:显示goNogo
	public List<DataModel> selectedList = new ArrayList<DataModel>();
	public List<TreeNode> deleteList = new ArrayList<TreeNode>();
	public List<DataModel> totalList = new ArrayList<DataModel>();
	public List<DataModel> replayList = new ArrayList<DataModel>();
	private PopButton mPopButton;
	private Button btnTotal;

	public TreeViewAdapter(Context context, TreeNode root) {
		this.mContext = context;
		this.inflater = LayoutInflater.from(this.mContext);
		this.mRoot = root;
	}

	public List<TreeNode> getDatas() {
		return this.list;
	}
	
	public void setPopButton(PopButton popButton) {
		this.mPopButton = popButton;
	}
	
	public TreeNode getRoot() {
		return this.mRoot;
	}
	
	public void setRoot(TreeNode root) {
		this.mRoot = root;
	}
	
	public void expandLevel(int level) {
        for (TreeNode n : mRoot.getChildren()) {
            expandLevel(n, level);
        }
        notifyDataSetChanged();
	}
    private void expandLevel(TreeNode node, int level) {
        if (node.getLevel() <= level) {
            expandNode(node, false);
        }
        for (TreeNode n : node.getChildren()) {
            expandLevel(n, level);
        }
    }
    private void expandNode(final TreeNode node, boolean includeSubnodes) {
    	node.setExpanded(true);
    	list.add(node);
        for (final TreeNode n : node.getChildren()) {
        	list.add(n);
            if (n.isExpanded() || includeSubnodes) {
                expandNode(n, includeSubnodes);
            }

        }
    	
    }
    
    public void toggleNodeCustom(TreeNode node) {
        if (node.isExpanded()) {
            collapseNode(node, false);
        } else {
            expandNode(node, false);
        }

    }
    
    private void collapseNode(TreeNode node, final boolean includeSubnodes) {
    	 node.setExpanded(false);
    	 list.removeAll(node.getChildren());
    	 if (includeSubnodes) {
             for (TreeNode n : node.getChildren()) {
                 collapseNode(n, includeSubnodes);
             }
         }
    }
    
    public void expandNode(final TreeNode node, int position) {
    	if (node.isExpanded()) {
    		collapseNode(node, true);
    	} else {
    		node.setExpanded(true);
    		list.addAll(position + 1, node.getChildren());
    	}
    	notifyDataSetChanged();
    }

	@Override
	public int getCount() {
		return this.list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {

		ViewHolder viewHolder;
		TreeNode node = list.get(position);
		DataModel item = (DataModel) node.getValue();
		boolean isParent = item.isFirstLevel;
		if (view == null) {
			int viewId = R.layout.treeview_item;
			view = this.inflater.inflate(viewId, null);

			viewHolder = new ViewHolder();
			viewHolder.tvRoot = (TextView) view.findViewById(R.id.txt_date);
			viewHolder.tvName = (TextView) view.findViewById(R.id.txt_name);
			viewHolder.tvStartTime = (TextView) view.findViewById(R.id.txt_start_time);
			viewHolder.tvDuration = (TextView) view.findViewById(R.id.txt_time_duration);
			viewHolder.tvState = (TextView) view.findViewById(R.id.txt_state);
			viewHolder.tvExceptionCount = (TextView) view.findViewById(R.id.txt_exception);
			viewHolder.tvGoNogo = (TextView) view.findViewById(R.id.txt_go_nogo);
			viewHolder.layoutException = (LinearLayout)view.findViewById(R.id.layout_exception);
			viewHolder.layoutGoNogo = (LinearLayout)view.findViewById(R.id.layout_go_nogo);
			viewHolder.cb = (CheckBox) view.findViewById(R.id.checkBox1);
			viewHolder.cbDelete = (CheckBox) view.findViewById(R.id.cb_delete);
			viewHolder.deleteLayout = (LinearLayout) view.findViewById(R.id.right);
			viewHolder.view_choose = (View) view.findViewById(R.id.choose_view);
			viewHolder.layoutChild = (RelativeLayout) view.findViewById(R.id.layout_child);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		view.setPadding((list.get(position).getLevel() - 1) * DensityUtil.dip2px(mContext, 20), 0, 0, 0);

		if (isParent) {
			viewHolder.tvRoot.setVisibility(View.VISIBLE);
			viewHolder.layoutChild.setVisibility(View.GONE);
		} else {
			viewHolder.tvRoot.setVisibility(View.GONE);
			viewHolder.layoutChild.setVisibility(View.VISIBLE);
		}
		viewHolder.tvRoot.setText(item.firstLevelTitle);
		viewHolder.tvStartTime.setText(DateUtil.Y_M_D_H_M.format(new Date(item.getCreateTime())));
		viewHolder.tvDuration.setText(item.getDuration());
		viewHolder.tvState.setText(DBManager.getInstance(mContext).getUploadStateStr(item.getState()));
		viewHolder.cb.setChecked(item.isChecked);
		int state = item.getState();
		viewHolder.tvState.setTextColor(state == -2 ? mContext.getResources().getColor(R.color.red)
				: mContext.getResources().getColor(R.color.light_blue));
		int exceptionCount = item.getExceptionCount();
		String exceptionString = exceptionCount > 99 ? "N" : exceptionCount + "";
		
		if (isAbnormalDisplayMode) {
			viewHolder.tvExceptionCount.setVisibility(View.VISIBLE);
			viewHolder.tvGoNogo.setVisibility(View.GONE);
			viewHolder.layoutException.setVisibility(View.VISIBLE);
			viewHolder.layoutGoNogo.setVisibility(View.GONE);
		} else {
			viewHolder.tvExceptionCount.setVisibility(View.GONE);
			viewHolder.tvGoNogo.setVisibility(View.VISIBLE);
			viewHolder.layoutException.setVisibility(View.GONE);
			viewHolder.layoutGoNogo.setVisibility(View.VISIBLE);
		}
		boolean isFolder = item.isFolder;
		if (isFolder) {
			viewHolder.tvExceptionCount.setBackgroundResource(R.drawable.ic_menu_archive);
//			viewHolder.tvGoNogo.setBackgroundResource(R.drawable.ic_menu_archive);
			viewHolder.tvGoNogo.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.ic_menu_archive), null);
			viewHolder.tvExceptionCount.setText("");
			viewHolder.tvGoNogo.setText("");
			viewHolder.tvName.setText(item.getTaskName());
			viewHolder.tvState.setVisibility(View.INVISIBLE);
			viewHolder.layoutException.setClickable(false);
			viewHolder.layoutGoNogo.setClickable(false);
		} else {
			viewHolder.tvState.setVisibility(View.VISIBLE);
			String name = "";
			if (item.testRecord == null) {
				name = item.getTaskName();
			} else {
				if (item.testRecord.file_name.startsWith("Partition")) {
					name = item.testRecord.file_name;
				} else {
					name = item.getTaskName();
				}
			}
			viewHolder.tvName.setText(name);
			viewHolder.tvExceptionCount.setText(exceptionString);

			viewHolder. layoutException.setTag(item);
			viewHolder.layoutException.setOnClickListener(abnormalClickListener);
			 viewHolder.layoutGoNogo.setTag(item);
			 viewHolder. layoutGoNogo.setOnClickListener(goNogoClickListener);
			int count = item.getExceptionCount();
			viewHolder.tvExceptionCount.setTextColor(count == 0 ? mContext.getResources().getColor(R.color.gray)
					: mContext.getResources().getColor(R.color.red));
			viewHolder.tvExceptionCount
					.setBackgroundResource(count == 0 ? R.drawable.circle_shape_gray : R.drawable.circle_shape_red);
			boolean go = item.isGo();
			viewHolder.tvGoNogo.setTextColor(go ? mContext.getResources().getColor(R.color.light_blue)
					: mContext.getResources().getColor(R.color.red));
			//go no-go文字显示格式
			viewHolder.tvGoNogo.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
//			viewHolder.tvGoNogo.setBackgroundResource(R.drawable.empty);
//			viewHolder.tvGoNogo.setBackgroundResource(go ? R.drawable.circle_shape_blue : R.drawable.circle_shape_red);
			viewHolder.tvGoNogo.setText(go ? "Go" : "No-go");
			//go no-go图标显示格式
//			viewHolder.tvGoNogo.setBackgroundResource(go ? R.drawable.img_go : R.drawable.img_ng);
		}
		//选择操作监听
		viewHolder.view_choose.setOnClickListener(selectClickListener);
		viewHolder.view_choose.setTag(item);
		
		if (isDeleteMode) {//删除选择操作监听
			viewHolder.cbDelete.setChecked(item.isDeleteChecked);
			viewHolder.deleteLayout.setOnClickListener(deleteClickListener);
			viewHolder.deleteLayout.setTag(node);
		} else if (isTotalMode) {//统计选择操作监听
			if (item.isFolder) {
				viewHolder.cbDelete.setBackgroundResource(R.drawable.ic_menu_archive);
				viewHolder.deleteLayout.setTag(node);
			} else {
				viewHolder.cbDelete.setBackgroundResource(R.drawable.checkbox);
				viewHolder.deleteLayout.setTag(item);
			}
			viewHolder.cbDelete.setChecked(item.isTotalChecked);
			viewHolder.deleteLayout.setOnClickListener(totalClickListener);
		} else if (isReplayMode) {//回放选择操作监听
			if (item.isFolder) {
				viewHolder.cbDelete.setBackgroundResource(R.drawable.ic_menu_archive);
				viewHolder.deleteLayout.setTag(node);
			} else {
				viewHolder.cbDelete.setBackgroundResource(R.drawable.checkbox);
				viewHolder.deleteLayout.setTag(item);
			}
			viewHolder.cbDelete.setChecked(item.isReplayChecked);
			viewHolder.deleteLayout.setOnClickListener(replayClickListener);
		} 
		if (isDeleteMode || isTotalMode || isReplayMode) {
			viewHolder.tvExceptionCount.setVisibility(View.GONE);
			viewHolder.tvGoNogo.setVisibility(View.GONE);
			viewHolder.layoutException.setVisibility(View.GONE);
			viewHolder.layoutGoNogo.setVisibility(View.GONE);
			viewHolder.cb.setVisibility(View.GONE);
			viewHolder.cbDelete.setVisibility(View.VISIBLE);
		} else {
			viewHolder.cbDelete.setVisibility(View.GONE);
			viewHolder.cb.setVisibility(View.VISIBLE);
			viewHolder.cbDelete.setBackgroundResource(R.drawable.checkbox);
			viewHolder.deleteLayout.setClickable(false);//不是那几种特殊模式则取消点击事件
		}

		if (isMarkHighLight) {
			mark(item.isMark, viewHolder.layoutChild);
		} else {
			mark(false, viewHolder.layoutChild);
		}
		return view;
	}
	
	/**
	 * 高亮
	 */
	private void mark(boolean mark, RelativeLayout view) {
		if (mark) {
			((TextView)view.findViewById(R.id.txt_name)).setTextColor(mContext.getResources().getColor(R.color.txt_high_light));//txt_high_light
			((TextView)view.findViewById(R.id.txt_start_time)).setTextColor(mContext.getResources().getColor(R.color.txt_high_light));//app_click_disable_grey_color
			((TextView)view.findViewById(R.id.txt_time_duration)).setTextColor(mContext.getResources().getColor(R.color.txt_high_light));
		} else {
			((TextView)view.findViewById(R.id.txt_name)).setTextColor(mContext.getResources().getColor(R.color.app_main_text_color));//txt_high_light
			((TextView)view.findViewById(R.id.txt_start_time)).setTextColor(mContext.getResources().getColor(R.color.app_light_grey_color));//app_click_disable_grey_color
			((TextView)view.findViewById(R.id.txt_time_duration)).setTextColor(mContext.getResources().getColor(R.color.app_light_grey_color));
		}
	}
	
	private OnClickListener selectClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			DataModel item = (DataModel)view.getTag();
			if (item.isChecked) {
				item.isChecked = false;
				if (item.isFolder) {
					for (DataModel dataModel : item.getChild()) {
						dataModel.isChecked = false;
						selectedList.remove(dataModel);
					}
				} else {
					selectedList.remove(item);

				}
			} else {
				item.isChecked = true;
				if (item.isFolder) {
					for (DataModel dataModel : item.getChild()) {
						dataModel.isChecked = true;
						selectedList.add(dataModel);
					}
				} else {
					selectedList.add(item);
				}
			}
			addOrRemoveTotalItem(item);
			notifyDataSetChanged();
			
			Intent mIntent = new Intent(AnalysisCommons.ANALYSIS_ACTION_SELECT_FILE);
			mContext.sendBroadcast(mIntent);  
		}
	};
	
	/**
	 * 删除模式监听
	 */
	private OnClickListener deleteClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			TreeNode node = (TreeNode)view.getTag();
			addOrRemoveDeleteItem(node);
			notifyDataSetChanged();
		}
	};
	
	private void addOrRemoveDeleteItem(TreeNode node) {
		DataModel item = (DataModel)node.getValue();
		if (item.isDeleteChecked) {
			item.isDeleteChecked = false;
			deleteList.remove(node);
			if (item.isFolder) {
				for (TreeNode n : node.getChildren()) {
					((DataModel)n.getValue()).isDeleteChecked = false;
					deleteList.remove(n);
				}
			} 
			((DataModel)node.getParent().getValue()).isDeleteChecked = false;
			deleteList.remove(node.getParent());

		} else {
			item.isDeleteChecked = true;
			deleteList.add(node);
			if (item.isFolder) {
				for (TreeNode n : node.getChildren()) {
					((DataModel)n.getValue()).isDeleteChecked = true;
					deleteList.add(n);
				}
			} 
			if (isAllChildrenSelected(node.getParent())) {
				((DataModel)node.getParent().getValue()).isDeleteChecked = true;
				deleteList.add(node.getParent());
			}			

		}
		setButtonState(3, R.id.btn_delete);
	}
	
	/**
	 * 统计模式监听
	 */
	private OnClickListener totalClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			if (view.getTag() instanceof DataModel) {
				DataModel item = (DataModel)view.getTag();
				addOrRemoveTotalItem(item);
			} else {
				TreeNode node = (TreeNode)view.getTag();
				expandNode(node, list.indexOf(node));
				
			}
			notifyDataSetChanged();
			
		}
	};
	
	private void addOrRemoveTotalItem(DataModel item) {
		if (item.isTotalChecked) {
			item.isTotalChecked = false;
			if (item.isFolder) {
				for (DataModel dataModel : item.getChild()) {
					dataModel.isTotalChecked = false;
					totalList.remove(dataModel);
				}
			} else {
				totalList.remove(item);
			}
		} else {
			item.isTotalChecked = true;
			if (item.isFolder) {
				for (DataModel dataModel : item.getChild()) {
					dataModel.isTotalChecked = true;
					totalList.add(dataModel);
				}
			} else {
				totalList.add(item);
			}
		}
		setTotalButtonState();
		setButtonState(2, R.id.btn_ok);
	}
	
	/**
	 * 回放模式监听
	 */
	private OnClickListener replayClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			if (view.getTag() instanceof DataModel) {
				DataModel item = (DataModel)view.getTag();
				addOrRemoveReplayItem(item);
			} else {
				TreeNode node = (TreeNode)view.getTag();
				expandNode(node, list.indexOf(node));
				
			}
			notifyDataSetChanged();
		}
	};
	
	public void addOrRemoveReplayItem(DataModel item) {
		if (item.isReplayChecked) {
			item.isReplayChecked = false;
			replayList.remove(item);
		} else {
			if (replayList.size() > 0) {
				replayList.get(0).isReplayChecked = false;
				replayList.clear();
			}
			item.isReplayChecked = true;
			replayList.add(item);
		}
		setButtonState(1, R.id.btn_ok);
	}
	
	/**
	 * 设置指定id按钮的状态（ 是否可点击、名字）
	 * @param witch 操作类型（1:回放/2:统计/3:删除）
	 * @param buttonId 按钮id
	 */
	public void setButtonState(int witch, int buttonId) {
		if (mPopButton == null) return;
		
		int size = 0;
		String name = "";
		if (witch == 1) {
			size = replayList.size();
			name = "";
		} else if (witch == 2) {
			size = totalList.size();
			name = mContext.getResources().getString(R.string.total_total) + "(" + size + ")";
		} else if (witch == 3) {
			size = deleteList.size();
			if(isShareMode){
				name = mContext.getResources().getString(R.string.str_next);
			}else{
				name = mContext.getResources().getString(R.string.delete) + "(" + size + ")";
			}
		}
		if (name != null && !name.trim().equals("")) {
			mPopButton.setButtonText(buttonId, name);
		}
		if (size > 0) {
			mPopButton.setButtonClickable(buttonId, true);
		} else {
			mPopButton.setButtonClickable(buttonId, false);
		}
	}
	
	public void setTotalButton(Button button) {
		this.btnTotal = button;
		setTotalButtonState();
	}
	
	public void setTotalButtonState() {
		if (this.btnTotal == null) return;
		int size = totalList.size();
		String name = mContext.getResources().getString(R.string.total_total) + "(" + size + ")";
		this.btnTotal.setText(name);
		
	}
	
	private OnClickListener abnormalClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			if (((TextView)((LinearLayout)view).getChildAt(0)).getText().equals("0")) {
				Toast.makeText(mContext, mContext.getResources().getString(R.string.data_manager_new_no_exception), Toast.LENGTH_SHORT).show();
			} else {
				DataModel item = (DataModel)view.getTag();
				showAbnormalDetail(item);
			}
		}
	};
	
	private OnClickListener goNogoClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			DataModel item = (DataModel)view.getTag();
			if (item.testRecord == null) return;
			String wheres = " where _mainId ='" + item.testRecord.getTotalId() + "'";
			TotalDataByGSM.getInstance().buildTotalDetailByHistory(mContext, wheres);
			Intent goOrNOIntent = new Intent(mContext,KPIResulActivity.class);
			goOrNOIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			goOrNOIntent.putExtra("TASK_NAME", item.testRecord.file_name);
			goOrNOIntent.putExtra("TEST_TIME", item.getStartTime());
			goOrNOIntent.putExtra("IS_GO", item.isGo());
			mContext.startActivity(goOrNOIntent);
		}
	};
	
	/**
	 * 显示异常详情
	 * @param item
	 */
	private void showAbnormalDetail(DataModel item) {
		View exceptionView = inflater.inflate(R.layout.alert_dialog_data_manager_new_exception, null);
		new PopDialog(mContext, exceptionView).setTitle(mContext.getResources().getString(R.string.data_manager_new_exception_detial)).show();
		LinearLayout content = (LinearLayout)exceptionView.findViewById(R.id.layout_content);
		content.removeAllViews();
		int count = item.getExceptionCount();
		for (int i = 0; i < count; i++) {
			View v = inflater.inflate(R.layout.data_manager_new_property_exception_item, null);
			RecordAbnormal abnormal = item.testRecord.getRecordAbnormals().get(i);
			((TextView)v.findViewById(R.id.txt_time)).setText(DateUtil.H_M_S.format(new Date(abnormal.abnormal_time)));
			((TextView)v.findViewById(R.id.txt_abnormal)).setText(abnormal.getAbnormal_type_str() + "");
			content.addView(v);
		}
	}
	
	public void onCustomItemClickListener(DataModel item) {
		if (isReplayMode) {
			addOrRemoveReplayItem(item);
		} else if (isTotalMode) {
			addOrRemoveTotalItem(item);
		} 
	}
	
	public void onCustomItemClickListener(TreeNode node) {
		addOrRemoveDeleteItem(node);
	}
	
	/**
	 * 用于删除操作
	 * 判断当前node的父Node是否所有子node都选择了
	 * @param node
	 * @return
	 */
	private boolean isAllChildrenSelected(TreeNode node) {
		for (TreeNode n : node.getParent().getChildren()) {
			DataModel d = (DataModel)n.getValue();
			if (!d.isDeleteChecked) {
				return false;
			}
		}
		return true;
	}

	static class ViewHolder {
		public TextView tvRoot;
		public TextView tvName;
		public TextView tvStartTime;
		public TextView tvDuration;
		public TextView tvState;
		public TextView tvExceptionCount;
		public TextView tvGoNogo;
		public CheckBox cb;
		public CheckBox cbDelete;
		public LinearLayout deleteLayout;
		public View view_choose;
		public RelativeLayout layoutChild;
		public LinearLayout layoutException;//用于方便点击
		public LinearLayout layoutGoNogo;//用于方便点击
	}
	
	public boolean isSpecialMode() {
		if (isReplayMode || isTotalMode || isDeleteMode) {
			return true;
		}
		return false;
	}
	
	public void setNormalMode() {
		this.setDeleteMode(false);
		this.setReplayMode(false);
		this.setTotalMode(false);
		notifyDataSetChanged();
	}
	
	public boolean isDeleteMode() {
		return this.isDeleteMode;
	}
	/**
	 * 设置分享模式
	 * @param ShareMode
	 */
	public void setShareMode(boolean ShareMode) {
		this.isShareMode = ShareMode;
		notifyDataSetChanged();
	}
	/**
	 * 设置删除模式
	 * @param deleteMode
	 */
	public void setDeleteMode(boolean deleteMode) {
		this.isDeleteMode = deleteMode;
		notifyDataSetChanged();
	}
	
	/**
	 * 设置显示模式
	 * @param isAbnormalDisplayMode
	 */
	public void setAbnormalDisplayMode(boolean isAbnormalDisplayMode) {
		this.isAbnormalDisplayMode = isAbnormalDisplayMode;
		notifyDataSetChanged();
	}
	
	/**
	 * 设置统计模式
	 * @param totalMode
	 */
	public void setTotalMode(boolean totalMode) {
		this.isTotalMode = totalMode;
		notifyDataSetChanged();
	}

	/**
	 * 设置回放模式
	 * @param replayMode
	 */
	public void setReplayMode(boolean replayMode) {
		this.isReplayMode = replayMode;
		notifyDataSetChanged();
	}
	
	/**
	 * 设置标记高亮
	 * @param highLight
	 */
	public void setMarkHighLight(boolean highLight) {
		this.isMarkHighLight = highLight;
		notifyDataSetChanged();
	}
}
