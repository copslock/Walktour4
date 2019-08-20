package com.walktour.gui.data.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.walktour.Utils.DensityUtil;
import com.walktour.framework.view.treeview.TreeNode;
import com.walktour.gui.R;

public class WorkOrderHolder extends TreeNode.BaseNodeViewHolder<WorkOrderHolder.TreeItem>{

	private Context mContext;
	public WorkOrderHolder(Context context) {
		super(context);
		mContext = context;
	}
	
	public static class TreeItem {
        public int icon;
        public TreeItemModel item;

        public TreeItem(int icon, TreeItemModel item) {
            this.icon = icon;
            this.item = item;
        }
    }
	


	@Override
	public View createNodeView(final TreeNode node, WorkOrderHolder.TreeItem value) {

		final View view = LayoutInflater.from(mContext).inflate(R.layout.work_order_view_treeview_parent_item, null);
		boolean isRoot = node.getLevel() == 1;//是否顶层
		TextView tvName = (TextView)view.findViewById(R.id.txt_name);
		CheckBox cb = (CheckBox)view.findViewById(R.id.check);
		tvName.setText(value.item.name);
		if (isRoot) {
			if (node.isExpanded()) {
            	view.findViewById(R.id.parent_divider).setVisibility(View.GONE);
            } else {
            	view.findViewById(R.id.parent_divider).setVisibility(View.VISIBLE);
            }
//			if (value.item.isEnableChoose) {
////				cb.setVisibility(View.VISIBLE);
//			} else {
//				view.setBackgroundColor(Color.parseColor("#eeeeee"));
//			}
		} else {
			view.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
			view.setPadding((node.getLevel() - 1) * DensityUtil.dip2px(mContext, 20), 0, 0, 0);
			cb.setVisibility(View.VISIBLE);
			view.findViewById(R.id.parent_divider).setVisibility(View.INVISIBLE);
		}
		cb.setVisibility(value.item.isEnableChoose ? View.VISIBLE : View.INVISIBLE);
		cb.setChecked(value.item.isSelected);
		if (value.item.isSelected) {
			tView.selectedList.add(node);
		}
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					tView.selectedList.add(node);
				} else {
					tView.selectedList.remove(node);
				}
			}
		});
		return view;
	}

    @Override
    public int getContainerStyle() {
        return R.style.TreeNodeStyleDivided;
    }
	
}

//public class TreeItemModel{
//	public String name;
//	public boolean isSelected;//是否已选择
//	public boolean isEnableChoose;//是否可以选择
//}
