package com.walktour.gui.task.activity.scanner.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingli.seegull.model.ChannelModel;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 频点列表适配器
 *
 * @author zhihui.lian
 */
public class ChannelAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater = null;
    /**
     * item点击监听器
     */
//	private OnItemClickListener mOnItemClickListener;
    private List<ChannelModel> channelModels;

    List<ChannelModel> deleteModels = new ArrayList<ChannelModel>();

    /**
     * 函数构造
     *
     * @param mContext
      */
    public ChannelAdapter(Context mContext, List<ChannelModel> channelModels) {
        this.mContext = mContext;
        this.channelModels = channelModels;
        inflater = LayoutInflater.from(mContext);
    }


    @Override
    public int getCount() {
        return channelModels == null ? 0 : channelModels.size();
    }

    @Override
    public ChannelModel getItem(int position) {
        if (channelModels != null && channelModels.size() != 0) {
            return channelModels.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.channel_item, null);
            mHolder = new ViewHolder();
            mHolder.channel_txt = (TextView) view.findViewById(R.id.channel_txt);
            mHolder.delete_btn = (ImageView) view.findViewById(R.id.delete_channel);

            view.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) view.getTag();
        }

        final ChannelModel channelModel = getItem(position);

        boolean isEqual = (channelModel.getStartChannel() == channelModel.getEndChannel());

        mHolder.channel_txt.setText(channelModel.getStartChannel() + (isEqual ? "" : ("-" + channelModel.getEndChannel())));
        mHolder.delete_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showDeleteDialog(channelModel);
            }
        });


        return view;
    }


    /**
     * 显示删除键框
     *
      */
    private void showDeleteDialog(final ChannelModel channelModel) {
        new BasicDialog.Builder(mContext)
                .setTitle(R.string.delete)
                .setIcon(android.R.drawable.ic_menu_delete)
                .setMessage(R.string.str_delete_makesure)
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteModels.clear();
                        deleteModels.add(channelModel);
                        channelModels.removeAll(deleteModels);
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton(R.string.str_cancle).show();
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    // public void setOnItemClickListener(OnItemClickListener listener) {
    // mOnItemClickListener = listener;
    // }

    class ViewHolder {
        TextView channel_txt;
        ImageView delete_btn;
    }


}
