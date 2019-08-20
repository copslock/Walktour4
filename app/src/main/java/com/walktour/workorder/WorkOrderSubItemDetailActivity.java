package com.walktour.workorder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.workorder.model.WorkOrderDetail;
import com.walktour.workorder.model.WorkSubItem;
import com.walktour.workorder.model.WorkSubItem.CommandItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 工单子项详细描述ui
 * @author zhihui.lian
 *
 */
public class WorkOrderSubItemDetailActivity  extends BasicActivity implements OnClickListener{

//	private ListView listSubItemDesc; //显示工单子项的描述
	private TextView orderDetailDescView;
	private SimpleAdapter descAdapter;
	private View convertView;
	private WorkOrderDetail workOrderDetail;
	public static  WorkSubItem subItem;
	private static final String KEY_WORK_ID = "WorkId";
	public static final String KEY_WORK_NAME = "WorkName";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.work_order_sub_detail);
		
		findView();
		genToolBar();
		getIntentDate();
		fillOrderDetailDescView();
		fillListView();
	}
	/**
	 * 页面控件初始化
	 */
	public void findView() {
		(initTextView(R.id.title_txt)).setText(R.string.work_order_subitem);
		((ImageButton)findViewById(R.id.pointer)).setOnClickListener(this);
		
	//	RelativeLayout titleLayout=(RelativeLayout)findViewById(R.id.workordertitle);
	//	titleLayout.setOnClickListener(this);
//		map.put(0, 0);     //给单选列表默认勾选值
		
//		orderDetailDescView = initTextView(R.id.work_order_detail);
//		listSubItemDesc = (ListView) findViewById(R.id.list_subitem_desc);

//		listSubItemCommand = (ListView) findViewById(R.id.list_subitem_command_list);
		//listView.setOnItemClickListener(this);
		//TextView workIds=initTextView(R.id.ItemDescrition);
		//workIds.setText(String.valueOf(workId));
	}
	/**
     * 生成底部工具栏目
     * */
    private void genToolBar() {
    	Button  Button1= initButton(R.id.Button01);
    	Button1.setText(R.string.execute_test);
    	Button1.setOnClickListener(this);
    	//Button  Button2= initButton(R.id.Button02);
    	//Button2.setVisibility(View.VISIBLE);
    	//Button2.setText(R.string.execute_test);  
    	//Button2.setOnClickListener(this);
    }

	/**
	 * 获取workOrderDetail对象
	 */
	private void getIntentDate() {
		workOrderDetail = (WorkOrderDetail)getIntent().getSerializableExtra("WorkOrderDetail");
	}
	
	
	
	
	
	
	//
	private void fillOrderDetailDescView() {
		if (workOrderDetail == null)
			return;
		
//		<string name="worktype">工单类型</string>
//		<string name="projectid">项目编号</string>
//		<string name="project_name">项目名称</string>
//		<string name="plan_end_time">计划结束时间</string>
//		<string name="sender_account">发送人账号</string>
//		<string name="provinceid">省份</string>
//		<string name="cityid">城市</string>
//		<string name="areaid">区域</string>
//		<string name="test_site">测试基站</string>
//		<string name="test_building">测试建筑物</string>
//		<string name="address">地址</string>
//		<string name="site_num">基站数</string>
//		<string name="building_sum">建筑物数</string>
//		<string name="net_type">网络类型</string>
//		<string name="is_received">是否接收</string>
		
		List<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();
		final String[] title = {
				                getString(R.string.workname),
				                getString(R.string.workarea),
				                getString(R.string.worktype),
				                getString(R.string.projectid),
				                getString(R.string.project_name),
				                getString(R.string.plan_end_time),
				                getString(R.string.sender_account),
				                getString(R.string.provinceid),
				                getString(R.string.cityid),
				                getString(R.string.areaid),
				                getString(R.string.test_site),
				                getString(R.string.test_building),
				                getString(R.string.address),
				                getString(R.string.site_num),
				                getString(R.string.building_sum),
				                getString(R.string.net_type),
				                getString(R.string.is_received)
				                
				                };
		 
		final String[] content = {
				""+workOrderDetail.getWorkName(),
				""+workOrderDetail.getWorkArea(),
				""+workOrderDetail.getWorkType(),
				""+workOrderDetail.getProjectId(),
				""+workOrderDetail.getProjectName(),
				""+ UtilsMethod.getSpecialProcesTime( Long.valueOf(workOrderDetail.getPlanEndTime()) ),
				""+workOrderDetail.getSenderAccount(),
				""+workOrderDetail.getProvinceId(),
				""+workOrderDetail.getCityId(),
				""+workOrderDetail.getAreaId(),
				""+workOrderDetail.getTestSite(),
				""+workOrderDetail.getTestBuilding(),
				""+workOrderDetail.getAddress(),
				""+workOrderDetail.getSiteSum(),
				""+workOrderDetail.getBuildingSum(),
				""+(workOrderDetail.getNetType()==5?"WCDMA":"GSM"), //WCDMA:5 GSM=1
				""+workOrderDetail.getIsReceived()
				};
		
		for(int i=0;i<title.length;i++) {
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(KEY_WORK_ID, title[i]+"");
			map.put(KEY_WORK_NAME, content[i]);
			listData.add(map);
		}
		
		dynamicAddView(listData,R.id.work_detail);
	}
	
	
	
	
	
	
	
	
	
	private void fillListView() {
		List<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();
//		<string name="subitem_no">工单子项编号</string>
//		<string name="subitem_name">子项名称</string>
//		<string name="test_content_desc">测试内容描述</string>
//		<string name="test_floor">测试楼层</string>
//		<string name="test_scene">测试场景</string>
//		<string name="test_type">测试类型</string>
//		<string name="biz_type">业务类型</string>
//		<string name="test_repeat_times">测试任务执行次数</string>
//		<string name="test_interval">循环间隔</string>
//		<string name="test_task">测试任务</string>
		
		final String[] title = {getString(R.string.subitem_no),
				                getString(R.string.subitem_name),
				                getString(R.string.test_content_desc),
				                getString(R.string.test_floor),
				                getString(R.string.test_scene),
				                getString(R.string.test_type),
				                getString(R.string.biz_type),
				                getString(R.string.test_repeat_times),
				                getString(R.string.test_interval),
				                getString(R.string.subitems_count)
				            };
		 
//		<!-- 测试场景 -->
//		<string name="building_all">全部</string>
//		<string name="building_comm_floors">普通楼层</string>
//		<string name="building_elevator">电梯</string>
//		<string name="building_parking">地停</string>
//		
//		<!-- 业务类型 -->
//		
//		<string name="log_oper_cs">语音业务</string>
//		<string name="log_oper_ps">数据业务</string>
//		<string name="log_oper_all">全部</string>
//		
//		<!-- 测试方式 -->
//		
//		
//	    <string name="log_test_bl">遍历测试</string>
//		<string name="log_test_qh">切换测试</string>
//		<string name="log_test_st">渗透测试</string>
//		<string name="log_test_xl">泄漏测试</string>
//		
		final String[] testScene = {getString(R.string.building_all),getString(R.string.building_comm_floors),getString(R.string.building_elevator),getString(R.string.building_parking)};
		final String[] bizType = {getString(R.string.log_oper_cs),getString(R.string.log_oper_ps),getString(R.string.log_oper_all)};
		final String[] testMethod = {getString(R.string.log_test_bl),getString(R.string.log_test_qh),getString(R.string.log_test_st),getString(R.string.log_test_xl)};
		
		String sceneItem=""; //测试场景
		String typeItem=""; //测试类型
		String bizTypeItem="";//业务类型
		try{
			sceneItem = testScene[subItem.getTestScene()-1];
			typeItem =  testMethod[subItem.getTestType()-1];
			bizTypeItem=bizType[subItem.getServerType()-1];
		}catch(Exception e) {
			
		}
		
		final String[] content = { ""+subItem.getItemId(),
				 ""+subItem.getItemName(),
				 ""+subItem.getTestContent(),
				 ""+subItem.getTestFloors(),
				 ""+sceneItem,
				 ""+typeItem,
				 ""+bizTypeItem,
				 ""+subItem.getLoopSum(),
				 ""+subItem.getLoopInterval(),
				 ""+subItem.getItemsCount()
				 
				};
		
		for(int i=0;i<title.length;i++) {
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(KEY_WORK_ID, title[i]+"");
			map.put(KEY_WORK_NAME, content[i]);
			listData.add(map);
		}
		//每个子项的命令列表
		List<CommandItem> commandItem = subItem.getCommandItems();
		for (int i=0;i<commandItem.size();i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(KEY_WORK_ID, getString(R.string.test_task)+(i+1));
			map.put(KEY_WORK_NAME, commandItem.get(i).getCommandDesc());
			listData.add(map);
		}
		
//		descAdapter = new SimpleAdapter(this, listData, R.layout.listview_item_work_order_subitem_desc, new String[]{KEY_WORK_ID, KEY_WORK_NAME}, new int[]{R.id.txt_title, R.id.txt_content});
//		listSubItemDesc.setAdapter(descAdapter);
		
		dynamicAddView(listData ,R.id.addLayout);
	}
	
	
	/**
	 * 动态添加View
	 */
	private void dynamicAddView( List<HashMap<String, String>> listData , int id) {
		LinearLayout layout = (LinearLayout)findViewById(id);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        for (int i = 0; i < listData.size(); i++) {
        	convertView = inflater.inflate(R.layout.workorder_sub_detail_item, null);  
        	TextView titile = (TextView)convertView.findViewById(R.id.detail_titile);
        	TextView txt = (TextView)convertView.findViewById(R.id.detail_txt);
        	txt.setText(listData.get(i).get(KEY_WORK_NAME));
        	titile.setText(listData.get(i).get(KEY_WORK_ID));
        	layout.addView(convertView);
        	if(i!= listData.size() - 1){
        		View view = new View(this);
        		view.setBackgroundColor(getResources().getColor(R.color.about_item));
        		view.setLayoutParams(new LayoutParams( LayoutParams.MATCH_PARENT , 1));
        		layout.addView(view);
        	}
		}
	}
	
	
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.pointer:
				finish();
				break;
			case R.id.Button01:
			{
				Log.d("","send action execute task ");
				Intent intent = new Intent(WalkMessage.ACTION_EXECUTE_TASK);
				sendBroadcast(intent);
				finish();
			}
			break;
		}
	}
}
