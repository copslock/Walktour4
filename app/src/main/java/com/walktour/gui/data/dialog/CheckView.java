package com.walktour.gui.data.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.gui.R;
import com.walktour.gui.data.FilterKey;
import com.walktour.gui.data.model.ExceptionModel;

import java.util.ArrayList;
import java.util.List;

public class CheckView extends BaseView{

	private View mView;
	private ListView mListView;
	private ListAdapter adapter;
	public List<ExceptionModel> selectedList = new ArrayList<ExceptionModel>();
	private CheckBox cbMark;
	
	public CheckView(Context context, String type) {
		super(context, type);
		init();
	}
	
	public View getView() {
		return this.mView;
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_clear:
			clear();
			cbMark.setChecked(false);
			if (mCallBack != null) {
				mCallBack.onClear();
			}
			break;
		case R.id.cb_mark:
			mark(cbMark.isChecked());
			if (mCallBack != null) {
				mCallBack.onMark(cbMark.isChecked());
			}
			break;
		case R.id.btn_filter:
			filter();
			if (mCallBack != null) {
				mCallBack.onSummit();
			}
			break;
		default:
			break;
		}
		
	}
	
	private void init() {
		boolean isHighLightMark = mPreferences.getBoolean(FilterKey.KEY_IS_HIGH_LIGHT_MARK + type, false);
		mView = inflater.inflate(R.layout.check_view_root, null);
		mListView = (ListView)mView.findViewById(R.id.ListView02);
		this.mView.findViewById(R.id.btn_clear).setOnClickListener(this);
		this.mView.findViewById(R.id.cb_mark).setOnClickListener(this);
		this.mView.findViewById(R.id.btn_filter).setOnClickListener(this);
		cbMark = (CheckBox)this.mView.findViewById(R.id.cb_mark);
		cbMark.setChecked(isHighLightMark);
		
		initData();
	}
	private void initData() {
		ExceptionModel[] excetions = getExection();
		adapter = new ListAdapter(excetions);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if (adapter.getDatas()[position].checked) {
					adapter.getDatas()[position].checked = false;
					selectedList.remove(adapter.getDatas()[position]);
				} else {
					adapter.getDatas()[position].checked = true;
					selectedList.add(adapter.getDatas()[position]);
				}
				adapter.notifyDataSetChanged();
			}
		});
	}
	
	/**
	 * 清除数据、设置
	 */
	private void clear() {
		selectedList.clear();
		for (int i = 0; i < adapter.getDatas().length; i++) {
			adapter.getDatas()[i].checked = false;
		}
		adapter.notifyDataSetChanged();
		mPreferences.edit().putBoolean(FilterKey.KEY_IS_CHECK_SETTING + type, false).commit();
		mPreferences.edit().putString(FilterKey.KEY_EXCEPTION_SELECTED + type, "").commit();
		mPreferences.edit().putBoolean(FilterKey.KEY_IS_HIGH_LIGHT_MARK + type, false).commit();
	}
	
	/**
	 * 标记
	 * @param mark
	 */
	private void mark(boolean mark) {
		mPreferences.edit().putBoolean(FilterKey.KEY_IS_HIGH_LIGHT_MARK + type, mark).commit();
		save();
	}
	
	/**
	 * 过滤
	 */
	private void filter() {
		mPreferences.edit().putBoolean(FilterKey.KEY_IS_CHECK_SETTING + type, true).commit();
		save();
	}
	
	/**
	 * 保存数据、设置
	 */
	private void save() {
		String keys = "";
		for (int i = 0; i < selectedList.size(); i++) {
			keys += selectedList.get(i).typeKey + ",";
		}
		if (keys.contains(",")) {
			keys = keys.substring(0, keys.lastIndexOf(","));
		}
		mPreferences.edit().putString(FilterKey.KEY_EXCEPTION_SELECTED + type, keys).commit();
	}
	
	/**
	 * 获取实际发生采样的测试任务
	 * @return
	 */
	private ExceptionModel[] getExection() {
		
		String selectedKeys = mPreferences.getString(FilterKey.KEY_EXCEPTION_SELECTED + type, "");
		List<ExceptionModel> list = mDBManager.getAbnormalList();
		
		ExceptionModel[] result = new ExceptionModel[list.size()];
		for (ExceptionModel em : list) {
			em.checked = selectedKeys.contains(em.typeKey + "");
			if (em.checked) selectedList.add(em);
		}
		list.toArray(result);
		return result;
	}
	
	private class ListAdapter extends BaseAdapter{
		private ExceptionModel[] exception;
		public ListAdapter(ExceptionModel[] exception) {
			this.exception = exception;
		}
		
		public ExceptionModel[] getDatas() {
			return this.exception;
		}

		@Override
		public int getCount() {
			return exception.length;
		}

		@Override
		public Object getItem(int arg0) {
			return exception[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View view, ViewGroup arg2) {
	
			ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.business_fragment_listitem, null);
				holder.tv_name = (TextView)view.findViewById(R.id.txt_name);
				holder.img_choose = (ImageView)view.findViewById(R.id.checkBox1);
				view.setTag(holder);
			} else {
				holder = (ViewHolder)view.getTag();
			}

			holder.tv_name.setText(exception[position].name);
			holder.img_choose.setImageResource(exception[position].checked ? R.drawable.btn_check_on : R.drawable.btn_check_off);
			return view;
		}
		 
	}
	
	private static class ViewHolder{
		private TextView tv_name;
		private ImageView img_choose;
	}
	
	private ClickListenerCallBack mCallBack;
	public void setClickListenerCallBack(ClickListenerCallBack callback) {
		this.mCallBack = callback;
	}
	
}
