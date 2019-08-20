/*
 * 文件名: MultiRABTaskAdapter.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建时间:2012-8-7
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.gui.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * [并行业务列表适配]<BR>
 * [功能详细描述]
 * @author zhihui.lian@dinglicom.com
 * @version [WalkTour, 2012-8-7] 
 */
@SuppressLint({ "InflateParams", "UseSparseArrays" })
public class MultiRABTaskAdapter extends BaseAdapter {
	
	private Context context;
	Map<Integer, TaskModel> taskCheckList1=new HashMap<Integer, TaskModel>();
	Map<Integer, Boolean> state = new HashMap<Integer, Boolean>();  //记录checkbox的位置
	private  ArrayList<TaskModel> taskCheckList=new ArrayList<TaskModel>();
	private TaskListDispose taskListDispose;
	private Handler mHandler;
	private ApplicationModel appModel = ApplicationModel.getInstance();
	/**
	 * [构造简要说明]
	 */
	public MultiRABTaskAdapter(Context context,ArrayList<TaskModel> taskCheckList,TaskListDispose taskListDispose,Handler mHandler) {
		this.context=context;
		this.taskCheckList=taskCheckList;
		this.taskListDispose=taskListDispose;
		this.mHandler=mHandler;
	}

	/**
	 * [获取列表个数]<BR>
	 * [功能详细描述]
	 * @return
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		if(taskCheckList!=null){
			return taskCheckList.size();
		}
		return 0;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * @param position
	 * @return
	 * @see android.widget.Adapter#getItem(int)
	 */

	@Override
	public Object getItem(int position) {
		return null;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * @param position
	 * @return
	 * @see android.widget.Adapter#getItemId(int)
	 */

	@Override
	public long getItemId(int position) {
		return 0;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater factory = LayoutInflater.from(context);
		ViewHolder holder;
		if(convertView==null){
			convertView=factory.inflate(R.layout.multi_rab_listview, null);
			holder=new ViewHolder();
			holder.itemTitle=(TextView)convertView.findViewById(R.id.ItemTitle);
			holder.ckTask=(CheckBox)convertView.findViewById(R.id.ItemTestable);
			holder.times=(TextView)convertView.findViewById(R.id.ItemCount_times);
			holder.ItemDescrition=(TextView)convertView.findViewById(R.id.ItemDescrition);
			holder.imgDelete=(ImageView)convertView.findViewById(R.id.ItemCheckble);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
			holder.ckTask.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Message msg=new Message();
					if(isChecked){
						state.put(position, isChecked);
					}else{
						state.remove(position);
					}
					msg.obj=state;
					mHandler.sendMessage(msg);
				}
			});
			
			
			//记录checkbox的位置，防止列表滚动时候选中状态错乱
			holder.ckTask.setChecked(state.get(position) == null ? false: true);
			String rabTaskName=((TaskModel)(taskCheckList.get(position))).getTaskName();
			holder.itemTitle.setText(rabTaskName.substring(rabTaskName.indexOf("%")+1, rabTaskName.length()));
			holder.times.setText(context.getString(R.string.task_repeat)+":"+((TaskModel)(taskCheckList.get(position))).getRepeat());
			holder.ItemDescrition.setText(taskListDispose.getDescrition((TaskModel)(taskCheckList.get(position)))); 
			holder.imgDelete.setEnabled(appModel.isTestJobIsRun()?false:true);
			holder.imgDelete.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					new BasicDialog.Builder(context)
					.setTitle(R.string.delete)
					.setIcon(android.R.drawable.ic_menu_delete)
					.setMessage(R.string.str_delete_makesure)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							synchronized (MultiRABTaskAdapter.class) {
								taskCheckList.remove(position);
								MultiRABTaskAdapter.this.notifyDataSetChanged();
								Message msg=new Message();
								msg.what=1;
								msg.obj = taskCheckList;
								mHandler.sendMessage(msg);  //发消息通知界面刷新Listview
							}
						}
					})
					.setNegativeButton(R.string.str_cancle).show();
				}
			});
		return convertView;
	}
	
	/**
	 * 
	 * [view结构]<BR>
	 * [功能详细描述]
	 * @version [WalkTour , 2012-8-7]
	 */
	public class ViewHolder{
		TextView itemTitle;
		CheckBox ckTask;
		TextView ItemDescrition;
		TextView times;
		ImageView imgDelete;
	}

}
