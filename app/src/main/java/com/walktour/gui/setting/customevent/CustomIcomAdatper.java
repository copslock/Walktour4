package com.walktour.gui.setting.customevent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.walktour.gui.R;

import java.io.File;
import java.util.ArrayList;

/**
 * CustomIcomAdatper 自定义事件图标选择Adatper 2013-11-20 上午10:10:46
 * 
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
@SuppressLint("InflateParams")
public class CustomIcomAdatper extends BaseAdapter {

	private Context mContext;
	private File[] icons;

	/**
	 * 
	 * 创建一个新的实例 CustomIcomAdatper.
	 *
	 * @param context
	 * @param fileDir
	 *          读取图标的目录
	 */
	public CustomIcomAdatper(Context context, File iconDir) {
		this.mContext = context;
		File[] dirFiles = iconDir.listFiles();
		if (dirFiles != null) {
			ArrayList<File> fileList = new ArrayList<File>();
			for (File f : dirFiles) {
				if (f.isFile()) {
					if (f.getAbsolutePath().contains("custom")) {
						fileList.add(0, f);
					} else {
						fileList.add(f);
					}
				}
			}
			icons = new File[fileList.size()];
			fileList.toArray(icons);
		}

	}

	@Override
	public int getCount() {
		if (icons != null) {
			return icons.length;
		} else {
			return 0;
		}
	}

	@Override
	public File getItem(int position) {
		if (icons != null) {
			return icons[position];
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_item_custom_event, null);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.ItemImage);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Drawable drawable = Drawable.createFromPath(icons[position].getAbsolutePath());
		holder.image.setImageDrawable(drawable);
		return convertView;
	}

	static class ViewHolder {
		ImageView image;
	}

}
