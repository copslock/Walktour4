package com.walktour.base.gui.adapter.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 * @date on 2018/6/19
 * @describe RecyclerView 适配器基类
 * @author jinfeng.xie
 * @version 1.0.0
 *
 */
public abstract class CommonRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {

    protected Context mContext;
    protected List<T> mList;

    protected ViewHolderClick<T> mHolderClick;
    protected AdapterView.OnItemClickListener onItemClickListener;
    protected AdapterView.OnItemLongClickListener onItemLongClickListener;

    public CommonRecyclerAdapter(Context context) {
        this.mContext = context;
        this.mList = new ArrayList<>();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(onCreateViewLayoutID(viewType), parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onViewRecycled(final RecyclerViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        onBindViewHolder(holder.getViewHolder(), position);
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(null, v, holder.getAdapterPosition(), holder.getItemId());
                }
            });
        }

        if (onItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemLongClickListener.onItemLongClick(null, v, holder.getAdapterPosition(), holder.getItemId());
                    return true;
                }
            });
        }
    }

    public abstract int onCreateViewLayoutID(int viewType);

    public abstract void onBindViewHolder(BaseViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    /**
     * 获取列表
     */
    public List<T> getList() {
        return mList;
    }

    /**
     * 替换某一个元素
     */
    public void replaceBean(int position, T t) {
        if (t != null) {
            this.mList.remove(position);
            this.mList.add(position, t);
            notifyItemChanged(position, t);
        }
    }

    /**
     * 添加数据列表到列表头部
     */
    public void addListAtStart(List<T> list) {
        if (list != null && !list.isEmpty()) {
            this.mList.addAll(0, list);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加数据列表到列表尾部
     */
    public void addListAtEnd(List<T> list) {
        if (list != null && !list.isEmpty()) {
            this.mList.addAll(list);
            notifyItemRangeInserted(mList.size() - 1, list.size());
        }

    }

    /**
     * 添加数据列表到列表尾部
     */
    public void addListAtEndAndNotify(List<T> list) {
        if (list != null && !list.isEmpty()) {
            this.mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加单个元素到列表头
     */
    public void addListBeanAtStart(T t) {
        if (t != null) {
            mList.add(0, t);
            notifyItemInserted(0);
        }
    }

    /**
     * 添加单个元素到列表尾
     */
    public void addListBeanAtEnd(T t) {
        if (t != null) {
            mList.add(t);
            notifyItemInserted(mList.size() - 1);
        }
    }

    /**
     * 添加单个元素到列表中
     */
    public void addListBeanAtList(T t, int position) {
        if (t != null) {
            mList.add(position, t);
            notifyDataSetChanged();
        }
    }

    /**
     * 替换RecyclerView数据
     */
    public void replaceList(List<T> list) {
        if (list != null) {
            this.mList = list;
        } else {
            mList.clear();
        }

        notifyDataSetChanged();
    }

    /**
     * 替换RecyclerView中的某一个数据
     */
    public void replaceItem(T t, int position) {
        if (position >= 0 && position <= mList.size() && t != null) {
            this.mList.set(position, t);
            notifyItemChanged(position);
        }
    }

    /**
     * 删除RecyclerView所有数据
     */
    public void removeAll() {
        if (mList != null) {
            notifyItemRangeRemoved(0, mList.size());
            this.mList.clear();
        }
    }

    /**
     * 删除RecyclerView指定位置的数据
     */
    public void remove(T t) {
        if (mList != null) {
            this.mList.remove(t);
            notifyDataSetChanged();
        }
    }

    /**
     * 删除RecyclerView指定位置的数据
     */
    public void remove(int position) {
        if (position >= 0 && position <= mList.size() && mList != null) {
            this.mList.remove(position);
            notifyItemRemoved(position);
            if (position != mList.size()) {
                notifyItemRangeChanged(position, mList.size() - position);
            }
        }
    }

    /**
     * item点击事件抽象方法
     */
    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * item长按事件抽象方法
     */
    public AdapterView.OnItemLongClickListener getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    /**
     * 点击事件抽象方法
     */
    public void setOnHolderClick(ViewHolderClick<T> holderClick) {
        this.mHolderClick = holderClick;
    }

    public interface ViewHolderClick<T> {
        void onViewClick(View view, T t, int position);
    }
}
