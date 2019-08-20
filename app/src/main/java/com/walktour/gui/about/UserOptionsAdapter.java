package com.walktour.gui.about;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义下拉框显示适配器
 * 
 * @author zhihui.lian 
 */
public class UserOptionsAdapter extends BaseAdapter {

	private List<String> list = new ArrayList<String>();
	private Context mContext;

	public UserOptionsAdapter(Context context, List<String> list) {
		this.mContext = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			// 下拉项布局
			convertView = LayoutInflater.from(mContext).inflate(R.layout.option_item, null);
			holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.option_item_layout);
			holder.textView = (TextView) convertView.findViewById(R.id.option_item_text);
			holder.delBtn = (ImageView) convertView.findViewById(R.id.option_item_del);
					

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.textView.setText(list.get(position));

		// 为下拉框选项文字部分设置事件，最终效果是点击将其文字填充到文本框
		holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnClicked.onItemSelected(position);
			}
		});

		// 为下拉框选项删除图标部分设置事件，最终效果是点击将该选项删除
		holder.delBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnClicked.onItemDelete(position);
			}
		});

		return convertView;
	}

	public OnClicked mOnClicked;

	public void setOnClicked(OnClicked onClicked) {
		mOnClicked = onClicked;
	}

	public interface OnClicked {
		void onItemDelete(int index);

		void onItemSelected(int index);
	}

}

class ViewHolder {
	RelativeLayout relativeLayout;
	TextView textView;
	ImageView delBtn;
}
