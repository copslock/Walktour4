package com.walktour.gui.map;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.control.adapter.TcpIpListAdapter;
import com.walktour.control.bean.packet_dissect_info;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.gui.R;
import com.walktour.model.Element;
import com.walktour.netsniffer.NetSnifferServiceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 抓包界面列表显示
 *
 */
public class TcpIpListActivity extends FragmentActivity implements RefreshEventListener{

	private final String TAG=TcpIpListActivity.class.getSimpleName();
	private ListView lvSimpleList;
	
	/** 树中的元素集合 */
	private ArrayList<Element> elements;
	private int node_id = 0;
    private FrameLayout detailLayout;
    private FragmentManager manager;
    private FragmentTransaction ft;
    private NetworkDataDetailFragment fragment;
    private TcpIpListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wireshark_view);
		RefreshEventManager.addRefreshListener(this);
		initView();
	}
	
	private void initView(){
		lvSimpleList = (ListView)findViewById(R.id.lv_simple_list);	
		lvSimpleList.setOnItemClickListener(itemListener);
		adapter = new TcpIpListAdapter(getApplicationContext());
		lvSimpleList.setAdapter(adapter);
	}
	
	
	 
    OnItemClickListener itemListener = new OnItemClickListener() {  
        @Override  
        public void onItemClick(AdapterView<?> parent, View view, int position,  
                long id) {  
        	ListView listView = (ListView)parent;
        	packet_dissect_info show_data = (packet_dissect_info)listView.getItemAtPosition(position);
			String v_tree="";
//        	if(Deviceinfo.getInstance().isVivo()){
				v_tree = NetSnifferServiceUtil.getInstance().getStringInfo(show_data.packet_no - 1);
//			}else{
//				v_tree = NetSniffer.getInstance().buildTcpIpDetailInfo(show_data.packet_no - 1).proto_tree;
//			}
        	Log.i("wireshark", v_tree);
			node_id = 0;
			elements = new ArrayList<Element>();
			fetch_data(v_tree, Element.TOP_LEVEL, 0);
			showDetail();
        }  
    }; 
    
    private void fetch_data(String packet_data, int level, int parant_id)
    {
    	try {
			JSONArray jsonArray = new JSONArray(packet_data);
			for (int i = 0; i < jsonArray.length(); i++)
			{
				JSONObject jsonObject = jsonArray.getJSONObject(i);	
				String child_value = jsonObject.getString("child");
				boolean have_child = false;
				if (child_value != "null")
				{
					have_child = true;
				}
				
				String rep_value = jsonObject.getString("rep");
				
				if (level == 0)
				{
					parant_id = Element.NO_PARENT;
				}
				Element e_value = new Element(rep_value, level, node_id++, parant_id, have_child, false);
				elements.add(e_value);
				
				if (have_child)
				{
					fetch_data(child_value, level+1, e_value.getId());
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * 显示详细内容
     * */
    private void showDetail() {
    	detailLayout = (FrameLayout)findViewById(R.id.content2);
    	detailLayout.setVisibility(View.VISIBLE);
    	manager = getSupportFragmentManager();
    	fragment = (NetworkDataDetailFragment)manager.findFragmentById(R.id.detail);
    	ft = getSupportFragmentManager().beginTransaction();
    	ft.setCustomAnimations(R.anim.push_right_in,  R.anim.push_left_out);  
    	ft.show(fragment).commit();
    	fragment.setElements(elements);
    }
	
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	RefreshEventManager.removeRefreshListener(this);
    }

    @Override
    public void onBackPressed() {
    	
    	if (fragment !=null && !fragment.isHidden()) {
    		hideDetailFragment();
    	} else {
    		super.onBackPressed();
    	}
    	
    }
    
    

    /**
     * 关闭详情fragment
     * */
    private void hideDetailFragment() {
    	ft = manager.beginTransaction();
		ft.hide(fragment).commit();
    }
    

	@Override
	public void onRefreshed(RefreshType refreshType, Object object) {
		switch (refreshType) {
        case ACTION_WALKTOUR_TIMER_CHANGED:
            if(!ApplicationModel.getInstance().isFreezeScreen()){
				List<packet_dissect_info> datas=null;
//				if(Deviceinfo.getInstance().isVivo()){
					datas =NetSnifferServiceUtil.getInstance().getDatas();
//				}else{
//					datas= TraceInfoInterface.traceData.tcpipInfoList;   //全部
//				}


    			if (datas == null || datas.size() == 0) {
					return;
				}
    			adapter.getDataList().clear();
    			adapter.getDataList().addAll(datas);
    			adapter.notifyDataSetChanged();
            }
            break;
		default:
			break;
		}
	}
	
}
