package com.walktour.base.gui.adapter.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @date on 2018/6/19
 * @describe
 * @author jinfeng.xie
 * @version 1.0.0
 *
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private BaseViewHolder baseViewHolder;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        baseViewHolder = BaseViewHolder.getViewHolder(itemView);
    }

    public BaseViewHolder getViewHolder() {
        return baseViewHolder;
    }
}
