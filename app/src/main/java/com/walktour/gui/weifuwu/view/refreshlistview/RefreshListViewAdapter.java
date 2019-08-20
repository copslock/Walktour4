package com.walktour.gui.weifuwu.view.refreshlistview;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.gui.R;

import java.util.LinkedList;
import java.util.List;
public class RefreshListViewAdapter extends BaseAdapter {
	private ListView receiveListView;
	private SelectObjAdapter adapter;
	private List<ListViewModel> data = new LinkedList<ListViewModel>();
	private Context mContext;
	private LayoutInflater layoutInflater;
	private List<ListViewModel> listDevices;
	private LinearLayout backLayout;
	private LinearLayout backHeaderLayout;
	private TextView info;
	private Button objdownup;
	/** 是否展开页面 **/
	private boolean isExpand = false;
	private int lineHeight = 0;
	private int defaultBackHeight = 0;
	private int maxSize = 4;
	private String searchTxt;
	/***
	 * 
	 * @param context
	 * @param listView
	 * @param data
	 *            列表中的所有数据
	 * @param listDevices
	 *            选择了的数据
	 */
	public RefreshListViewAdapter(Context context, String searchTxt, final LinearLayout backLayout,
			final ListView receiveListView, List<ListViewModel> data, final List<ListViewModel> listDevices) {
		mContext = context;
		this.data = data;
		this.searchTxt = searchTxt;
		layoutInflater = LayoutInflater.from(mContext);
		this.backLayout = backLayout;
		backHeaderLayout = (LinearLayout) backLayout.findViewById(R.id.latop);
		if (defaultBackHeight == 0) {
			backHeaderLayout.measure(0, 0);
			defaultBackHeight = backHeaderLayout.getMeasuredHeight();
		}
		if (lineHeight == 0) {
			View convertView = layoutInflater.inflate(R.layout.share_select_obj, null, false);
			convertView.measure(0, 0);
			lineHeight = convertView.getMeasuredHeight();
		}
		this.listDevices = listDevices;
		this.receiveListView = receiveListView;
		adapter = new SelectObjAdapter();
		this.receiveListView.setAdapter(adapter);
		this.info = (TextView) backLayout.findViewById(R.id.showxp);
		this.objdownup = (Button) backLayout.findViewById(R.id.obj_downup);
		this.info.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isExpand = !isExpand;
				if (isExpand) {// 展开
					objdownup.setBackgroundResource(R.drawable.obj_down);
				} else {// 收缩
					objdownup.setBackgroundResource(R.drawable.obj_up);
				}
				adapter.notifyDataSetChanged();
				receiveListView.invalidate();
				showMax();
			}
		});
		objdownup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isExpand = !isExpand;
				if (isExpand) {// 展开
					objdownup.setBackgroundResource(R.drawable.obj_down);
				} else {// 收缩
					objdownup.setBackgroundResource(R.drawable.obj_up);
				}
				adapter.notifyDataSetChanged();
				receiveListView.invalidate();
				showMax();
			}
		});
	}
	private void showMax() {
		if (!isExpand) {// 收缩
			if (null != listDevices && listDevices.size() >= 0) {
				LayoutParams params = receiveListView.getLayoutParams();
				params.height = 0;
				receiveListView.setLayoutParams(params);
				LayoutParams params1 = backLayout.getLayoutParams();
				params1.height = defaultBackHeight + 0;
				backLayout.setLayoutParams(params1);
			}
		} else {// 展开
			if (null != listDevices && listDevices.size() > maxSize) {
				LayoutParams params = receiveListView.getLayoutParams();
				params.height = maxSize * lineHeight;
				receiveListView.setLayoutParams(params);
				LayoutParams params1 = backLayout.getLayoutParams();
				params1.height = defaultBackHeight + maxSize * lineHeight;
				backLayout.setLayoutParams(params1);
			} else {
				LayoutParams params = receiveListView.getLayoutParams();
				params.height = listDevices.size() * lineHeight;
				receiveListView.setLayoutParams(params);
				LayoutParams params1 = backLayout.getLayoutParams();
				params1.height = defaultBackHeight + listDevices.size() * lineHeight;
				backLayout.setLayoutParams(params1);
			}
		}
	}
	@Override
	public int getCount() {
		return data.size();
	}
	@Override
	public Object getItem(int pos) {
		return data.get(pos);
	}
	@Override
	public long getItemId(int arg0) {
		return 0;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.sharedevice_item, null);
		}
		ImageView img = (ImageView) convertView.findViewById(R.id.objimg);
		TextView name1 = (TextView) convertView.findViewById(R.id.name1);
		TextView name2 = (TextView) convertView.findViewById(R.id.name2);
		final CheckBox check = (CheckBox) convertView.findViewById(R.id.checkid);
		final ListViewModel model = data.get(position);
		String str1 = model.describe;
		String str2 = mContext.getString(R.string.share_project_device_code)+":" + model.code;
		if (null != searchTxt && searchTxt.length() > 0) {
			ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
			if (model.code.contains(searchTxt)) {
				SpannableString builder1 = new SpannableString(str1);
				builder1.setSpan(redSpan, str1.indexOf(searchTxt), searchTxt.length(),
						Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				name1.setText(builder1);
			}
			if (model.describe.contains(searchTxt)) {
				SpannableString builder2 = new SpannableString(str2);
				builder2.setSpan(redSpan, str2.indexOf(searchTxt), searchTxt.length(),
						Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				name2.setText(builder2);
			}
		} else {
			name1.setText(str1);
			name2.setText(str2);
		}
		img.setBackgroundResource(getDraw(model.osType));
		check.setChecked(listDevices.contains(model)); 
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (check.isChecked()) {
					check.setChecked(false);
					if (listDevices.contains(model))
						listDevices.remove(model);
				} else {
					check.setChecked(true);
					if (!listDevices.contains(model))
						listDevices.add(model);
				}
				adapter.notifyDataSetChanged();
				receiveListView.invalidate();
				info.setText(String.format(mContext.getString(R.string.share_project_devices_listx),
						listDevices.size() + ""));
				showView();
				showMax();
			}
		});
		check.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) { 
				int action =event.getAction();  
                if(action==MotionEvent.ACTION_UP){  
                	if (check.isChecked()) {
    					check.setChecked(false);
    					if (listDevices.contains(model))
    						listDevices.remove(model);
    				} else {
    					check.setChecked(true);
    					if (!listDevices.contains(model))
    						listDevices.add(model);
    				}
    				adapter.notifyDataSetChanged();
    				receiveListView.invalidate();
    				info.setText(String.format(mContext.getString(R.string.share_project_devices_listx),
    						listDevices.size() + ""));
    				showView();
    				showMax(); 
                }  
				return true;
			}
		});
		check.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				if (check.isChecked()) {
//					check.setChecked(false);
//					if (listDevices.contains(model))
//						listDevices.remove(model);
//				} else {
//					check.setChecked(true);
//					if (!listDevices.contains(model))
//						listDevices.add(model);
//				}
//				adapter.notifyDataSetChanged();
//				receiveListView.invalidate();
//				info.setText(String.format(mContext.getString(R.string.share_project_devices_listx),
//						listDevices.size() + ""));
//				showView();
//				showMax();
			}
		});
		//不能使用这个方法,会冲突
//		check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if(isChecked) { 
//					if (!listDevices.contains(model))
//						listDevices.add(model);
//				}else{
//					if (listDevices.contains(model))
//						listDevices.remove(model);
//				}
//				adapter.notifyDataSetChanged();
//				receiveListView.invalidate();
//				info.setText(String.format(mContext.getString(R.string.share_project_devices_listx),
//						listDevices.size() + ""));
//				showView();
//				showMax();
//			}
//		});
		return convertView;
	}
	private int getDraw(int osType) {
		int id = R.drawable.obj_android;
		switch (osType) {
		case 0:
			id = R.drawable.obj_android;
			break;
		case 1:
			id = R.drawable.obj_iphone;
			break;
		case 2:
			id = R.drawable.obj_group;
			break;
		}
		return id;
	}
	private void showView() {
		if (null == listDevices || listDevices.size() <= 0) {
			objdownup.setBackgroundResource(R.drawable.obj_up);
			backLayout.setVisibility(View.GONE);
			backLayout.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return false;
				}
			});
		} else {
			backLayout.setVisibility(View.VISIBLE);
			backLayout.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			});
		}
	}
	/**
	 * 任务列表适配器
	 * 
	 * @author weirong.fan
	 * 
	 */
	private class SelectObjAdapter extends BaseAdapter {
		public SelectObjAdapter() {
			super();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.share_select_obj, parent, false);
			}
			final ListViewModel model = listDevices.get(position);
			final ImageView type = (ImageView) convertView.findViewById(R.id.objtype);
			type.setBackgroundResource(getDraw(model.osType));
			final TextView tv = (TextView) convertView.findViewById(R.id.receiveobj);
			final TextView tv2 = (TextView) convertView.findViewById(R.id.receiveobj2);
			String str1 = model.describe;
			String str2 = mContext.getString(R.string.share_project_device_code)+":" + model.code;
			if (null != searchTxt && searchTxt.length() > 0) {
				ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
				if (model.code.contains(searchTxt)) {
					SpannableString builder1 = new SpannableString(str1);
					builder1.setSpan(redSpan, str1.indexOf(searchTxt), searchTxt.length(),
							Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
					tv.setText(builder1);
				}
				if (model.describe.contains(searchTxt)) {
					SpannableString builder2 = new SpannableString(str2);
					builder2.setSpan(redSpan, str2.indexOf(searchTxt), searchTxt.length(),
							Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
					tv2.setText(builder2);
				}
			} else {
				tv.setText(str1);
				tv2.setText(str2);
			}
			final ImageView delBtn = (ImageView) convertView.findViewById(R.id.deleteobj);
			// 删除一行数据
			delBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					listDevices.remove(model);
					adapter.notifyDataSetChanged();
					receiveListView.invalidate();
					info.setText(String.format(mContext.getString(R.string.share_project_devices_listx),
							listDevices.size() + ""));
					showView();
					showMax();
				}
			});
			return convertView;
		}
		@Override
		public int getCount() {
			if (null == listDevices || listDevices.size() == 0)
				return 0;
			// if (!isExpand) {
			// return 0;
			// } else {
			return listDevices.size();
			// }
		}
		@Override
		public Object getItem(int position) {
			return null == listDevices ? null : listDevices.get(position);
		}
		@Override
		public long getItemId(int position) {
			return (long) position;
		}
	}
}
