package com.walktour.base.gui.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 树形结构节点
 * Created by wangk on 2017/6/20.
 */

public class TreeNode {
    /**
     * 节点等级，0是根节点
     */
    private int mLevel = 0;
    /**
     * 节点关联的对象
     */
    private Object mObject;
    /**
     * 是否显示当前节点的子节点
     */
    private boolean isExpanded = false;
    /**
     * 当前节点的父节点
     */
    private TreeNode mParent;
    /**
     * 节点的下级节点
     */
    private List<TreeNode> mChildren = new ArrayList<>();

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int level) {
        mLevel = level;
    }

    public Object getObject() {
        return mObject;
    }

    public void setObject(Object object) {
        mObject = object;
    }

    public List<TreeNode> getChildren() {
        return mChildren;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public TreeNode getParent() {
        return mParent;
    }

    public void setParent(TreeNode parent) {
        mParent = parent;
    }

    public void setChildren(List<TreeNode> children) {
        mChildren = children;
    }
}
