package com.walktour.framework.view.treeview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.walktour.framework.view.treeview.TreeNode.BaseNodeViewHolder;
import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bogdan Melnychuk on 2/10/15.
 */
public class AndroidTreeView {

	private TreeNode mRoot;
	private Context mContext;
	private boolean applyForRoot;
	private int containerStyle = 0;
	private Class<? extends TreeNode.BaseNodeViewHolder<?>> defaultViewHolderClass = SimpleViewHolder.class;
	private TreeNode.TreeNodeClickListener nodeClickListener;
	private TreeNode.TreeNodeLongClickListener nodeLongClickListener;
	private boolean mSelectionModeEnabled;
	private boolean mUseDefaultAnimation = false;
	private boolean use2dScroll = false;
	public List<TreeNode> selectedList = new ArrayList<TreeNode>();// 选择的列表

	public AndroidTreeView(Context context, TreeNode root) {
		mRoot = root;
		mContext = context;
	}

	public void setDefaultContainerStyle(int style) {
		setDefaultContainerStyle(style, false);
	}

	private void setDefaultContainerStyle(int style, boolean applyForRoot) {
		containerStyle = style;
		this.applyForRoot = applyForRoot;
	}

	public void setDefaultViewHolder(Class<? extends TreeNode.BaseNodeViewHolder<?>> viewHolder) {
		defaultViewHolderClass = viewHolder;
	}

	public void setDefaultNodeClickListener(TreeNode.TreeNodeClickListener listener) {
		nodeClickListener = listener;
	}

	@SuppressLint("NewApi")
	private View getView(int style) {
		final ViewGroup view;
		if (style > 0) {
			ContextThemeWrapper newContext = new ContextThemeWrapper(mContext, style);
			view = use2dScroll ? new TwoDScrollView(newContext) : new ScrollView(newContext);
		} else {
			view = use2dScroll ? new TwoDScrollView(mContext) : new ScrollView(mContext);
		}

		Context containerContext = mContext;
		if (containerStyle != 0 && applyForRoot) {
			containerContext = new ContextThemeWrapper(mContext, containerStyle);
		}
		final LinearLayout viewTreeItems = new LinearLayout(containerContext, null, containerStyle);

		viewTreeItems.setId(R.id.tree_items);
		viewTreeItems.setOrientation(LinearLayout.VERTICAL);
		view.addView(viewTreeItems);

		mRoot.setViewHolder(new BaseNodeViewHolder<Object>(mContext) {
			@Override
			public View createNodeView(TreeNode node, Object value) {
				return null;
			}

			@Override
			public ViewGroup getNodeItemsView() {
				return viewTreeItems;
			}
		});

		expandNode(mRoot, false);
		return view;
	}

	public View getView() {
		return getView(-1);
	}

	public void expandLevel(int level) {
		for (TreeNode n : mRoot.getChildren()) {
			expandLevel(n, level);
		}
	}

	private void expandLevel(TreeNode node, int level) {
		if (node.getLevel() <= level) {
			expandNode(node, false);
		}
		for (TreeNode n : node.getChildren()) {
			expandLevel(n, level);
		}
	}

	public void toggleNodeCustom(TreeNode node) {
		if (node.isExpanded()) {
			collapseNode(node, false);
		} else {
			expandNode(node, false);
		}

	}

	private void toggleNode(TreeNode node) {
		if (node.isExpanded()) {
			collapseNode(node, false);
		} else {
			expandNode(node, false);
		}

	}

	private void collapseNode(TreeNode node, final boolean includeSubnodes) {
		node.setExpanded(false);
		BaseNodeViewHolder<?> nodeViewHolder = getViewHolderForNode(node);

		if (mUseDefaultAnimation) {
			collapse(nodeViewHolder.getNodeItemsView());
		} else {
			nodeViewHolder.getNodeItemsView().setVisibility(View.GONE);
		}
		if (includeSubnodes) {
			for (TreeNode n : node.getChildren()) {
				collapseNode(n, includeSubnodes);
			}
		}
	}

	private void expandNode(final TreeNode node, boolean includeSubnodes) {
		node.setExpanded(true);
		final BaseNodeViewHolder<?> parentViewHolder = getViewHolderForNode(node);
		parentViewHolder.getNodeItemsView().removeAllViews();

		for (final TreeNode n : node.getChildren()) {
			addNode(parentViewHolder.getNodeItemsView(), n);

			if (n.isExpanded() || includeSubnodes) {
				expandNode(n, includeSubnodes);
			}

		}
		if (mUseDefaultAnimation) {
			expand(parentViewHolder.getNodeItemsView());
		} else {
			parentViewHolder.getNodeItemsView().setVisibility(View.VISIBLE);
		}

	}

	private void addNode(ViewGroup container, final TreeNode n) {
		final BaseNodeViewHolder<?> viewHolder = getViewHolderForNode(n);
		final View nodeView = viewHolder.getView();
		container.addView(nodeView);

		nodeView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (n.getClickListener() != null) {
					n.getClickListener().onClick(n, n.getValue());
					toggleNode(n);
				} else if (nodeClickListener != null) {
					nodeClickListener.onClick(n, n.getValue());
				}
			}
		});

		nodeView.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				if (nodeLongClickListener != null) {
					nodeLongClickListener.onLongClick(n, n.getValue());
				}
				return true;
			}
		});
	}

	public boolean isSelectionModeEnabled() {
		return mSelectionModeEnabled;
	}

	private TreeNode.BaseNodeViewHolder<?> getViewHolderForNode(TreeNode node) {
		TreeNode.BaseNodeViewHolder<?> viewHolder = node.getViewHolder();
		if (viewHolder == null) {
			try {
				final Object object = defaultViewHolderClass.getConstructor(Context.class)
						.newInstance(new Object[] { mContext });
				viewHolder = (TreeNode.BaseNodeViewHolder<?>) object;
				node.setViewHolder(viewHolder);
			} catch (Exception e) {
				throw new RuntimeException("Could not instantiate class " + defaultViewHolderClass);
			}
		}
		if (viewHolder.getContainerStyle() <= 0) {
			viewHolder.setContainerStyle(containerStyle);
		}
		if (viewHolder.getTreeView() == null) {
			viewHolder.setTreeViev(this);
		}
		return viewHolder;
	}

	private static void expand(final View v) {
		v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		final int targetHeight = v.getMeasuredHeight();

		v.getLayoutParams().height = 0;
		v.setVisibility(View.VISIBLE);
		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				v.getLayoutParams().height = interpolatedTime == 1 ? LinearLayout.LayoutParams.WRAP_CONTENT
						: (int) (targetHeight * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);
	}

	private static void collapse(final View v) {
		final int initialHeight = v.getMeasuredHeight();

		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if (interpolatedTime == 1) {
					v.setVisibility(View.GONE);
				} else {
					v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);
	}

}
