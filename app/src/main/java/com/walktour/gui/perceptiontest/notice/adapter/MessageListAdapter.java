package com.walktour.gui.perceptiontest.notice.adapter;

import android.content.Context;
import android.widget.TextView;

import com.walktour.base.gui.adapter.recyclerview.BaseViewHolder;
import com.walktour.base.gui.adapter.recyclerview.CommonRecyclerAdapter;
import com.walktour.gui.R;
import com.walktour.gui.perceptiontest.notice.bean.MessageBean;

/**
 * @author Max
 * @data 2018/11/18
 */
public class MessageListAdapter extends CommonRecyclerAdapter<MessageBean> {

    public MessageListAdapter(Context context) {
        super(context);
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.adapter_notic_message;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        MessageBean bean=mList.get(position);
        TextView title=holder.get(R.id.tv_title);
        TextView date=holder.get(R.id.tv_date);
        title.setText(""+bean.getTitle());
        date.setText(""+ bean.getCreateDT());
    }
}
