package com.walktour.gui.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.walktour.control.config.GoOrNogoSetting;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog.Builder;
import com.walktour.gui.R;
import com.walktour.model.Business;
import com.walktour.model.GoOrNogoParameter;

import java.util.ArrayList;
import java.util.List;

/** 
 * @author msi
 *
 */
@SuppressLint("InflateParams")
public class GoOrNogoSettingActivity extends BasicActivity{
	
	private Context mContext;
	
	private GoOrNogoSetting goOrNogoSetting;
	
	private List<Business> businessList;
	
	private CustomExpandableListAdapter adapter;
	
	private String prefix = "";
	
	 private String[] businessNames = null;
	 
	 private ListAdapter listAdapter;
	 
	 private List<GoOrNogoParameter> selectedParameter = new ArrayList<GoOrNogoParameter>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.go_or_nogo_setting_activity);
		mContext = this;
		goOrNogoSetting = GoOrNogoSetting.getInstance(this);
		initView();
	}
	
	private void initTopbar() {
		ImageView iv = initImageView(R.id.pointer);
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	
	private void initView() {
		this.businessList = this.goOrNogoSetting.getBusinesses();
		this.businessNames = this.goOrNogoSetting.getBusinessNames();
		initTopbar();
		adapter = new CustomExpandableListAdapter(this, businessList);
		ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListView.setAdapter(adapter);
        expandableListView.setGroupIndicator(null); //默认ExpandableListView的Group最前面有一个Indicator  可以去掉 也可以自定义
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                return true;
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                return true;
            }
        });
        
       //展开所有组
        int groupCount = expandableListView.getCount();
        for (int i = 0; i < groupCount; i++) {
        	expandableListView.expandGroup(i);    
		}
	}
	
	/**
	 * 修改值dialog
	 * @param groupPosition
	 * @param childPosition
	 */
	private void showChangeValueDialog(final int groupPosition, final int childPosition) {
		Builder builder = new Builder(GoOrNogoSettingActivity.this);
		businessList = goOrNogoSetting.getBusinesses();
		View v = LayoutInflater.from(this).inflate(R.layout.dialog_go_or_nogo_change_layout, null);
		Spinner sp = (Spinner)v.findViewById(R.id.spinner_type);
		ArrayAdapter<String> ad= new ArrayAdapter<String>(this,R.layout.simple_spinner_custom_layout2,getResources().getStringArray(R.array.array_mathematical_operator));
		ad.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_in_center);  
		sp.setAdapter(ad);
		
		String value = businessList.get(groupPosition).getDefaultSettings().get(childPosition).getCondiction();
		int operatorIndex = getMathematicalOperatorIndex(value);
		prefix = getResources().getStringArray(R.array.array_mathematical_operator)[operatorIndex];
		sp.setSelection(operatorIndex, false);
		sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View v, int position, long arg3) {
				prefix = getResources().getStringArray(R.array.array_mathematical_operator)[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		final EditText contentEdit = (EditText)v.findViewById(R.id.value);
		contentEdit.setText(value.replace(prefix, ""));
		
		String title = getResources().getString(R.string.setting);
		
		builder.setIcon(android.R.drawable.ic_menu_edit).setTitle(title).setView(v)
		.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				String valueString = contentEdit.getText().toString().trim();
				if (!valueString.equals("")) {
					changeValue(groupPosition, childPosition,  prefix + valueString);
				}
			}
		}).setNegativeButton(R.string.str_cancle);
		builder.create().show();
	}
	
	/**
	 * 增加值dialog
	 * @param groupPosition
	 * @param childPosition
	 */
	private void showAddValueDialog(final int groupPosition) {
		Builder builder = new Builder(GoOrNogoSettingActivity.this);
		View v = LayoutInflater.from(this).inflate(R.layout.dialog_go_or_nogo_add_layout, null);
		ListView lv = (ListView)v.findViewById(R.id.business_list);
		listAdapter = new ListAdapter(groupPosition);
		lv.setAdapter(listAdapter);
		List<GoOrNogoParameter> avaliableList = this.businessList.get(groupPosition).getAvaliableSettings();
		List<GoOrNogoParameter> defaultList = this.businessList.get(groupPosition).getDefaultSettings();
		listAdapter.getDatas().clear();
		for (int i = 0; i < avaliableList.size(); i++) {
			GoOrNogoParameter item = avaliableList.get(i);
			if (!isExist(defaultList, item)) {
				listAdapter.getDatas().add(item);
			}
		}
		listAdapter.notifyDataSetChanged();
		String title = getResources().getString(R.string.work_order_fj_select_action);
		selectedParameter.clear();
		if (listAdapter.getDatas().size() == 0) {
			v = new TextView(mContext);
			((TextView)v).setTextColor(getResources().getColor(R.color.app_main_text_color));
			((TextView)v).setTextSize(16);
			((TextView)v).setText(getResources().getString(R.string.no_datas));
		}
		builder.setIcon(android.R.drawable.ic_menu_edit).setTitle(title).setView(v)
		.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				for (int i = 0; i < selectedParameter.size(); i++) {
					businessList.get(groupPosition).getDefaultSettings().add(selectedParameter.get(i));
				}
				adapter.notifyDataSetChanged();
				saveBusinesses(groupPosition);
			}
		}).setNegativeButton(R.string.str_cancle);
		builder.create().show();
	}
	
	private boolean isExist(List<GoOrNogoParameter> defaultList, GoOrNogoParameter item) {
		boolean result = false;
		for (int i = 0; i < defaultList.size(); i++) {
			if (defaultList.get(i).getName().equals(item.getName())) {
				result = true;
				return result;
			}
		}
		return result;
	}
	
	private int getMathematicalOperatorIndex(String key) {
		int result = 0;
		String[] strs = getResources().getStringArray(R.array.array_mathematical_operator);
		for (int i = 0; i < strs.length; i++) {
			if (key.contains(strs[i])) {
				return i;
			}
		}
		return result;
	}
	
	/**
	 * 改变值
	 */
	private void changeValue(int groupPosition, int childPosition, String value) {
		if (value == null || value.trim().equals("")) {
			return;
		}
		businessList = goOrNogoSetting.getBusinesses();
		businessList.get(groupPosition).getDefaultSettings().get(childPosition).setCondiction(value);
		adapter.notifyDataSetChanged();
		
		saveBusinesses(groupPosition);
	}
	
	/**
	 * 保存所有修改
	 */
	private void saveBusinesses(int position) {
		goOrNogoSetting.setBusiness(businessList.get(position));
		goOrNogoSetting.saveBusiness();
	}
	
	//==========================================================adapter=========================================================
	
	/**
	 * 获取相关项的名称
	 * @param key
	 * @return
	 */
	private String getName(String key) {
    	String name = "";
    	for (int i = 0; i < businessNames.length; i++) {
			String nameTmp = businessNames[i];
			if (nameTmp.contains(key)) {
				name = nameTmp.substring(nameTmp.indexOf(":") + 1);
				return name;
			}
		}
    	return name;
    }
	
	private class ListAdapter extends BaseAdapter{
		private List<GoOrNogoParameter> datas = new ArrayList<GoOrNogoParameter>();
		private int groupPosition;
		public ListAdapter(int groupPosition) {
			this.groupPosition = groupPosition;
		}
		
		public List<GoOrNogoParameter> getDatas() {
			return this.datas;
		}
		
		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public Object getItem(int position) {
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder itemViewHolder;

			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.go_or_nogo_add_listitem, parent, false);

				itemViewHolder = new ViewHolder();
				itemViewHolder.txt_name = (TextView) convertView.findViewById(R.id.textViewTitle);
				itemViewHolder.img_icon = (ImageView) convertView.findViewById(R.id.imageViewIcon);
				convertView.setTag(itemViewHolder);

			} else {

				itemViewHolder = (ViewHolder) convertView.getTag();
			}

			String key = businessList.get(this.groupPosition).getName() + "_" + datas.get(position).getName();
			if(!businessList.get(this.groupPosition).getName().equalsIgnoreCase("pbm")){    //PBM由于子Name相同需做特殊处理
				itemViewHolder.txt_name.setText(getName(key));
			}else{
				itemViewHolder.txt_name.setText(getName(datas.get(position).getAlias()));
			}
			if (itemViewHolder.isChecked)
				itemViewHolder.img_icon.setImageResource(R.drawable.img_checked);
			else
				itemViewHolder.img_icon.setImageResource(R.drawable.img_unchecked);
			
			final GoOrNogoParameter item = datas.get(position);
			itemViewHolder.img_icon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (itemViewHolder.isChecked) {
						itemViewHolder.isChecked = false;
						selectedParameter.add(item);
					} else {
						itemViewHolder.isChecked = true;
						selectedParameter.add(item);
					}
					listAdapter.notifyDataSetChanged();
				}
			});
			return convertView;
		}
	}
	
	public static class ViewHolder {
		public TextView txt_name;
		public ImageView img_icon;
		public boolean isChecked;
	}
	
	private class CustomExpandableListAdapter extends BaseExpandableListAdapter {
		Context context;
//	    List<String> groupList = new ArrayList<String>();
//	    List<List<String>> childList = new ArrayList<List<String>>();
//		private int startX = 0;
//		private int endX = 0;
	    
	    private List<Business> datas = new ArrayList<Business>();
	   

	    public CustomExpandableListAdapter(Context context, List<Business> datas) {
	        this.context = context;
	        this.datas = datas;
	    }
	    
//	    public List<Business> getDatas() {
//	    	return this.datas;
//	    }
	    
	    

	    @Override
	    public int getGroupCount() {
	        return datas.size();
	    }

	    @Override
	    public int getChildrenCount(int i) {
	    	return datas.get(i).getDefaultSettings().size(); 
	    }

	    @Override
	    public Object getGroup(int i) {
	        return datas.get(i);
	    }

	    @Override
	    public Object getChild(int i, int i2) {
	        return datas.get(i).getDefaultSettings().get(i2);
	    }

	    @Override
	    public long getGroupId(int i) {
	        return i;
	    }

	    @Override
	    public long getChildId(int i, int i2) {
	        return i2;
	    }

	    @Override
	    public boolean hasStableIds() {
	        return false;
	    }

	    //父listview视图
	    @Override
	    public View getGroupView(int groupPosition, boolean groupIsSelected, View view, ViewGroup viewGroup) {
	    	final ParentViewHolder viewHolder;
	    	if (view == null) {
	    		view = LayoutInflater.from(context).inflate(R.layout.go_or_nogo_setting_parent_item_view, null);
	    		viewHolder = new ParentViewHolder();
	    		viewHolder.txt_name = (TextView) view.findViewById(R.id.go_or_nogo_setting_parent_item_view_txt_name);
	    		viewHolder.img_add = (ImageView) view.findViewById(R.id.go_or_nogo_setting_parent_item_view_img_add);
	    		viewHolder.img_add.setTag(groupPosition);
	    		view.setTag(viewHolder);
	    	} else {
	    		viewHolder = (ParentViewHolder)view.getTag();
	    		viewHolder.img_add.setTag(groupPosition);
	    	}
	    	viewHolder.txt_name.setText(datas.get(groupPosition).getName());
	    	viewHolder.img_add.setOnClickListener(addClickListener);
	        return view;
	    }

	    //子listview视图
	    @Override
	    public View getChildView(int groupPosition, int childPosition, boolean childIsSelected, View view, ViewGroup viewGroup) {
	    	final ChildViewHolder viewHolder;
	    	if (view == null) {
	    		view = LayoutInflater.from(context).inflate(R.layout.go_or_nogo_setting_child_listitem, null);
	    		viewHolder = new ChildViewHolder();
	    		viewHolder.txt_name = (TextView) view.findViewById(R.id.go_or_nogo_setting_child_item_view_txt_name);
	    		viewHolder.txt_condiction = (TextView) view.findViewById(R.id.go_or_nogo_setting_child_item_view_txt_condition);
	    		viewHolder.img_more = (ImageView)view.findViewById(R.id.go_or_nogo_setting_child_item_view_img_load);
	    		viewHolder.img_delete = (ImageView)view.findViewById(R.id.trash);
	    		viewHolder.img_delete.setTag(groupPosition + ":" + childPosition);
	    		viewHolder.txt_condiction.setTag(groupPosition + ":" + childPosition);
	    		view.setTag(viewHolder);
	    	} else {
	    		viewHolder = (ChildViewHolder)view.getTag();
	    		viewHolder.img_delete.setTag(groupPosition + ":" + childPosition);
	    		viewHolder.txt_condiction.setTag(groupPosition + ":" + childPosition);
	    	}
	    	String key = datas.get(groupPosition).getName() + "_" + datas.get(groupPosition).getDefaultSettings().get(childPosition).getName();

	    	viewHolder.txt_name.setText(datas.get(groupPosition).getName().equalsIgnoreCase("pbm") ?  getName(datas.get(groupPosition).getDefaultSettings().get(childPosition).getAlias()): getName(key));
	    	
	    	viewHolder.txt_condiction.setText(datas.get(groupPosition).getDefaultSettings().get(childPosition).getCondiction());
	    	viewHolder.img_delete.setOnClickListener(deleteClickListener);
	    	viewHolder.txt_condiction.setOnClickListener(changeClickListener);
	        return view;
	    }
	    
	    private OnClickListener addClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int position = Integer.parseInt(v.getTag() + "");
				showAddValueDialog(position);
			}
		};
		
		private OnClickListener changeClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String tag = v.getTag() + "";
				String[] ids = tag.split(":");
				int groupPosition = Integer.parseInt(ids[0]);
				int childPosition = Integer.parseInt(ids[1]);
				showChangeValueDialog(groupPosition, childPosition);
				
			}
		};
		
		private OnClickListener deleteClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String tag = v.getTag() + "";
				String[] ids = tag.split(":");
				businessList.get(Integer.parseInt(ids[0])).getDefaultSettings().remove(Integer.parseInt(ids[1]));
				adapter.notifyDataSetChanged();
				saveBusinesses(Integer.parseInt(ids[0]));
			}
		};

	    //此方法是用来触发子listview 是否可以点击的  默认为false
	    @Override
	    public boolean isChildSelectable(int groupPosition, int childPosition) {
	        return true;
	    }
	}
	
	static class ParentViewHolder{
		public TextView txt_name;
		public ImageView img_add;
	}
	
	static class ChildViewHolder {
		public TextView txt_name;
		public TextView txt_condiction;
		public ImageView img_more;
		public ImageView img_delete;
	}
}
