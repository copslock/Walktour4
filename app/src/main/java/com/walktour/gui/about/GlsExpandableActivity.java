package com.walktour.gui.about;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.RotateAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.model.LicenseModel;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
  
public class GlsExpandableActivity extends BasicActivity {  
  
    private ExpandableListView elistview = null; // 定义树型组件  
    private ExpandableListAdapter adapter = null; // 定义适配器对象  
    CheckBox childBox;  
    TextView childTextView;  
    private HashMap<String, Boolean> statusHashMap;  
    View childItem = null;
	private FileInputStream fileInputStream;  
	/**
	 * 回调model
	 */
	private LicenseModel licenseModel = null;
	
	/**
	 * 列表取参数model
	 */
	private LicenseModel itemModel = null;
	
	private ArrayList<LicenseModel> lists = new ArrayList<LicenseModel>();
	private LinkedHashMap<String, ArrayList<LicenseModel>> licenseGruop = new LinkedHashMap<String, ArrayList<LicenseModel>>();
	
	private ArrayList<String> groupName	= new ArrayList<String>();
	
	private ArrayList<ArrayList<LicenseModel>> chileName = new ArrayList<ArrayList<LicenseModel>>();
	private JSONArray objArray;
	private JSONArray products;
	private JSONArray featureArray;
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.setContentView(R.layout.about_contract_list_page); // 默认布局管理器  
        TextView title =  initTextView(R.id.title_txt);
		title.setText("GLS Management");
		ImageView pointer = initImageView(R.id.pointer);
		pointer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GlsExpandableActivity.this.finish();
			}
		});
		File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Walktour/data/license_login.xml");
		if(file.exists()){
			try {
				fileInputStream = new FileInputStream(file);
				byte [] buffer = new byte[fileInputStream.available()] ;
				fileInputStream.read(buffer);
				String json = new String(buffer,"utf-8"); 
				objArray = new JSONArray(json);
				System.out.println("---------"+objArray.length());
				for (int i = 0; i < objArray.length(); i++) {
					LicenseModel licenseModel = new LicenseModel();
					licenseModel.setRegion(objArray.getJSONObject(i).getString("region"));
					licenseModel.setCountry(objArray.getJSONObject(i).getString("country"));
					licenseModel.setProject(objArray.getJSONObject(i).getString("project"));
					licenseModel.setContractname(objArray.getJSONObject(i).getString("contractname"));
					licenseModel.setExpiredate(objArray.getJSONObject(i).getString("expiredate"));
					licenseModel.setContractcode(objArray.getJSONObject(i).getString("contractcode"));
					String jsonArray = objArray.getJSONObject(i).getString("products");
					products = new JSONArray(jsonArray);
					for (int j = 0; j < products.length(); j++) {
						if(products.getJSONObject(j).getString("productcode").equals("W")){
							licenseModel.setLicenseQuantity(products.getJSONObject(j).getInt("licenseQuantity"));
							licenseModel.setLicenseused(products.getJSONObject(j).getInt("licenseused"));
							String features = products.getJSONObject(j).getString("feature");
							featureArray = new JSONArray(features);
							for (int k = 0; k < featureArray.length(); k++) {
								String feature = featureArray.getJSONObject(k).getString("featurename");
								licenseModel.getFeatureList().add(feature);
							}
							lists.add(licenseModel);
						}
					}
					
				}
				
				for (int i = 0; i < lists.size(); i++) {
					String keys = lists.get(i).getCountry() + ","+ lists.get(i).getRegion();
					Log.w("show","--" + keys);
					System.out.println("------总数量-----"+lists.get(i).getLicenseQuantity());
					System.out.println("------权限个数-----"+lists.get(i).getFeatureList().size());
					putModelToMap(keys,lists.get(i));
				}
				
				showListLicense();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
        
        this.elistview = (ExpandableListView) super.findViewById(R.id.elistview); // 取得组件  
        this.adapter = new MyExpandableListAdapter(this); // 实例化适配器  
        this.elistview.setAdapter(this.adapter); // 设置适配器  
        this.elistview.setOnChildClickListener(new OnChildClickListenerImpl()); // 设置子项单击事件  
        this.elistview.setOnGroupClickListener(new OnGroupClickListenerImpl());// 设置组项单击事件  
        this.elistview.setOnGroupCollapseListener(new OnGroupCollapseListenerImpl());// 关闭分组事件  
        this.elistview.setOnGroupExpandListener(new OnGroupExpandListenerImpl()); // 展开分组事件  
  
    }  
    
    
    /**
     * 分组
     * @param key
     * @param license
     */
    private void putModelToMap(String key,LicenseModel license){
		if(licenseGruop.get(key) != null){
			licenseGruop.get(key).add(license);
		}else{
			ArrayList<LicenseModel> licenses = new ArrayList<LicenseModel>();
			licenses.add(license);
			licenseGruop.put(key, licenses);
		}
	}

    @SuppressWarnings({"deprecation","unchecked"})
    private void showListLicense(){
		Iterator cc = licenseGruop.entrySet().iterator();
		while(cc.hasNext()){
			Map.Entry entry = (Map.Entry)cc.next();
			String keyString = entry.getKey().toString();
			Log.w("show","------------------"+ keyString +"--------------------");
			groupName.add(keyString);
			ArrayList<LicenseModel> models = (ArrayList<LicenseModel>) entry.getValue();
			for(int i=0 ;i<models.size(); i++){
				Log.w("show","--" + models.get(i).getCountry() + "--" + models.get(i).getRegion() + "--" );
			}
			chileName.add(models);
		}
		
	}
    
    
  
    private class OnChildClickListenerImpl implements OnChildClickListener {// 监听子项点击事件  
        @Override  
        public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {            
//            int gourpsSum = adapter.getGroupCount();//组的数量  
//            for(int i = 0; i < gourpsSum; i++) {  
//                int childSum = adapter.getChildrenCount(i);//组中子项的数量  
//                for(int k = 0; k < childSum;k++) {  
//                    boolean isLast = false;  
//                    if (k == (childSum - 1)){   
//                        isLast = true;  
//                    }  
//                      
//                    CheckBox cBox = (CheckBox) adapter.getChildView(i, k, isLast, null, null).findViewById(R.id.checkBox);  
//                    cBox.toggle();//切换CheckBox状态！！！！！！！！！！  
//                    boolean itemIsCheck=cBox.isChecked();  
//                    TextView tView=(TextView) adapter.getChildView(i, k, isLast, null, null).findViewById(R.id.textView);  
//                    String gameName=tView.getText().toString();  
//                    if (i == groupPosition && k == childPosition) {  
//                        statusHashMap.put(gameName, itemIsCheck);  
//                    } else {  
//                         statusHashMap.put(gameName, false);  
//                    }  
//                    ((BaseExpandableListAdapter) adapter).notifyDataSetChanged();//通知数据发生了变化  
//                }  
//                  
//            }                 
            return false;  
        }  
    }  
  
    private class OnGroupClickListenerImpl implements OnGroupClickListener {// 组被点击事件  
        @Override  
        public boolean onGroupClick(ExpandableListView parent, View v,int groupPosition, long id) {  
        	ImageView isExpandedImg = (ImageView)v.findViewById(R.id.isExpandedImg);
        	showAnimation(isExpandedImg);
            return false;  
        }  
    }  
    
    public void showAnimation(View mView) {
        final float centerX = mView.getWidth() / 2.0f;
        final float centerY = mView.getHeight() / 2.0f;
        RotateAnimation rotateAnimation = new RotateAnimation(0, 180, centerX,centerY);
        rotateAnimation.setDuration(300);
        rotateAnimation.setFillAfter(true);
        mView.startAnimation(rotateAnimation);
    }
  
    private class OnGroupCollapseListenerImpl implements OnGroupCollapseListener {// 组收缩事件  
        @Override  
        public void onGroupCollapse(int groupPosition) {  
        }  
    }  
  
    private class OnGroupExpandListenerImpl implements OnGroupExpandListener {// 打开组事件  
        @Override  
        public void onGroupExpand(int groupPosition) {  
        }  
    }  
  
    @Override  
    public void onCreateContextMenu(ContextMenu menu, View view,ContextMenuInfo menuInfo) {// 处理长按事件  
    }  
  


    
    
    /**
     * 自定义适配器分组适配器 ，功能显示圆角item
     * @author zhihui.lian
     */
    
    private class MyExpandableListAdapter extends BaseExpandableListAdapter {  
        private Context context = null;  
        
        public ArrayList<String> groups = groupName; // 组名称  
        
        public ArrayList<ArrayList<LicenseModel>> children = chileName; // 定义子选项  
		
        private ImageView isExpandedImg;
		
  
        public MyExpandableListAdapter(Context context) {  
            this.context = context;  
            statusHashMap = new HashMap<String, Boolean>();  
            for (int i = 0; i < children.size(); i++) {// 初始时,让所有的子选项均未被选中  
                for (int a = 0; a < children.get(i).size(); a++) {  
                    statusHashMap.put(children.get(i).get(a).getProject()+children.get(i).get(a).getRegion()+children.get(i).get(a).getCountry()+children.get(i).get(a).getContractname(), false);  
                }  
            }  
        }  
  
        @Override  
        public Object getChild(int groupPosition, int childPosition) { // 取得指定的子项  
            return this.children.get(groupPosition).get(childPosition);  
        }  
  
        @Override  
        public long getChildId(int groupPosition, int childPosition) { // 取得子项ID  
            return childPosition;  
        }  
  
        //点击事件发生后:先执行事件监听,然后调用此getChildView()  
        @Override  
        public View getChildView(final int groupPosition, final int childPosition,boolean isLastChild, View convertView, ViewGroup parent) {// 返回子项组件  
            if (convertView == null) {// 第一次的时候convertView是空,所以要生成convertView  
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
                convertView = inflater.inflate(R.layout.gls_group_child, null);  
            }
            childTextView = (TextView) convertView.findViewById(R.id.textView);  
            childTextView.setText(getChild(groupPosition, childPosition).toString());  
            childBox = (CheckBox) convertView.findViewById(R.id.checkBox);  
            childBox.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
			          int gourpsSum = getGroupCount();//组的数量  
			          
			          for(int i = 0; i < gourpsSum; i++) {  
			              int childSum = adapter.getChildrenCount(i);//组中子项的数量  
			              for(int k = 0; k < childSum;k++) {  
			                  boolean isLast = false;  
			                  if (k == (childSum - 1)){   
			                      isLast = true;  
			                  }  
			                  String keys = children.get(i).get(k).getProject()+children.get(i).get(k).getRegion()+children.get(i).get(k).getCountry()+children.get(i).get(k).getContractname();
			                  CheckBox cBox = (CheckBox) adapter.getChildView(i, k, isLast, null, null).findViewById(R.id.checkBox);  
			                  cBox.toggle();//切换CheckBox状态！！！！！！！！！！  
			                  boolean itemIsCheck=cBox.isChecked();  
			                  TextView tView=(TextView) adapter.getChildView(i, k, isLast, null, null).findViewById(R.id.textView);  
			                  if (i == groupPosition && k == childPosition) {  
			                	  System.out.println("------选择gameName-----"+keys+"--是否选中---"+itemIsCheck);
			                      statusHashMap.put(keys, itemIsCheck);  
			                      licenseModel = ((LicenseModel)getChild(i, k));
			                  } else {  
			                       statusHashMap.put(keys, false);  
			                  }  
			                  ((BaseExpandableListAdapter) adapter).notifyDataSetChanged();//通知数据发生了变化  
			              }  
			                
			          }
				}
			});
            
            itemModel = ((LicenseModel)getChild(groupPosition, childPosition));
            
            
            Boolean nowStatus = statusHashMap.get(children.get(groupPosition).get(childPosition).getProject()+children.get(groupPosition).get(childPosition).getRegion()+children.get(groupPosition).get(childPosition).getCountry()+children.get(groupPosition).get(childPosition).getContractname());//当前状态  
            System.out.println("--显示--"+nowStatus + "----" + children.get(groupPosition).get(childPosition).getProject()+children.get(groupPosition).get(childPosition).getRegion()+children.get(groupPosition).get(childPosition).getCountry()+children.get(groupPosition).get(childPosition).getContractname());
            
            childBox.setChecked(nowStatus);  
            
            RelativeLayout ly1 = (RelativeLayout)convertView.findViewById(R.id.ly1);
            ly1.setBackgroundResource(getBackground(childPosition,groupPosition));
            
            TextView gls_contract_name = (TextView)convertView.findViewById(R.id.gls_contract_name);
            gls_contract_name.setText(itemModel.getContractname());
            
            TextView gls_contract_expireDate = (TextView)convertView.findViewById(R.id.gls_contract_expireDate);
            gls_contract_expireDate.setText("Expired :"+UtilsMethod.stringToDateShort(itemModel.getExpiredate()));
            
            TextView gls_contract_available = (TextView)convertView.findViewById(R.id.gls_contract_available);
            gls_contract_available.setText("Available Number: " + (itemModel.getLicenseQuantity() - itemModel.getLicenseused()) + "/" + itemModel.getLicenseQuantity());
            
            TextView gls_contract_project = (TextView)convertView.findViewById(R.id.gls_contract_project);
            gls_contract_project.setText(itemModel.getProject());
            
            ImageView imv1 = (ImageView)convertView.findViewById(R.id.line);
            imv1.setVisibility(getline(childPosition,groupPosition));
            ly1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					Intent intent = new Intent(GlsExpandableActivity.this, GlsLicenseDetailPage.class);
					intent.putExtra("ItemDetail", (LicenseModel)getChild(groupPosition, childPosition));
					startActivity(intent);
					LogUtil.i("LisenseClent", "onCleck license detail item------"+((LicenseModel)getChild(groupPosition, childPosition)).getCountry());
				}
			});
            return convertView;  
        }  
        
        
        
    	/**
    	 * 背景圆角处理
    	 * @param position
    	 * @param groupPosition
    	 * @return
    	 */
        private int getBackground(int position ,int groupPosition) {  
            if (position == 0 && getChildrenCount(groupPosition) == 1) {  
                               //单个数据  
                return R.drawable.gls_item_only;  
            }  
            if (position == 0) {  
                               //头  
                return R.drawable.gls_item_top;  
            }  
            if (position == getChildrenCount(groupPosition) - 1) {  
                               //尾  
                return R.drawable.gls_item_bottom;  
            }  
                       //中间  
            return R.drawable.gls_item_mid;  
        }
        
        
        /**
         * item之间线条处理
         * @param position
         * @param groupPosition
         * @return
         */
        private int getline(int position ,int groupPosition) {  
            if (position == 0 && getChildrenCount(groupPosition) == 1) {  
                               //单个数据  
                return View.INVISIBLE;  
            }  
            if (position == 0) {  
                               //头  
                return View.VISIBLE;  
            }  
            if (position == getChildrenCount(groupPosition) - 1) {  
                               //尾  
                return View.INVISIBLE;  
            }  
                       //中间  
            return View.VISIBLE;  
        }
        
  
        @Override  
        public int getChildrenCount(int groupPosition) { // 取得子项个数  
            return this.children.get(groupPosition).size();  
        }  
  
        @Override  
        public Object getGroup(int groupPosition) { // 取得组对象  
            return this.groups.get(groupPosition);
        }
  
        @Override  
        public int getGroupCount() { // 取得组个数  
            return this.groups.size();
        }  
  
        @Override  
        public long getGroupId(int groupPosition) { // 取得组ID  
            return groupPosition;  
        }  
  
        @Override  
        public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {// 取得组显示组件  
//            TextView textView = buildTextView(); // 建立组件  
//            textView.setText(this.getGroup(groupPosition).toString()); // 设置文字  
//            return textView;  
        	// 返回子项组件  
            if (convertView == null) {// 第一次的时候convertView是空,所以要生成convertView  
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
                convertView = inflater.inflate(R.layout.gls_group_item, null);  
            }  
              
            childTextView = (TextView) convertView.findViewById(R.id.mainItem);  
            childTextView.setText(this.getGroup(groupPosition).toString());  
            isExpandedImg = (ImageView) convertView.findViewById(R.id.isExpandedImg);  
            if(isExpanded){
            	isExpandedImg.setBackgroundResource(R.drawable.expander_ic_maximized_black);
            }else{
            	isExpandedImg.setBackgroundResource(R.drawable.expander_ic_minimized_black);
            }
        	return convertView;
        }  
        
        

        
  
        @Override  
        public boolean hasStableIds() {  
            return true;  
        }  
  
        @Override  
        public void notifyDataSetChanged() {//通知数据发生变化  
            super.notifyDataSetChanged();  
        }  
  
        @Override  
        public boolean isChildSelectable(int groupPosition, int childPosition) {  
            return false;  
        }  
    }  
    @Override
    public void finish() {
    	if(licenseModel != null){
    		Intent intent = new Intent();
    		intent.putExtra("isCheckedModel", licenseModel);
    		this.setResult(RESULT_OK, intent);
    	}
    	super.finish();
    }
  
}  