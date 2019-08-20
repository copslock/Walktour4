package com.walktour.gui.setting;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.Verify;
import com.walktour.control.config.ConfigUrl;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.applet.ControlBar;
import com.walktour.gui.applet.FileExplorer;
import com.walktour.gui.applet.LicenseExplorer;
import com.walktour.gui.task.CustomAutoCompleteTextView;
import com.walktour.gui.task.SaveHistoryShare;
import com.walktour.model.UrlModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * @ClassName: SysURL
 * @Description: 
 * @author lianzh
 * @date 2012-11-29 下午2:35:14
 *
 */

@SuppressLint("InflateParams")
public class SysURL extends BasicActivity implements OnItemClickListener{

	private String tag="SysURL";
	private ControlBar bar;
	private Button btnNew;
	private Button importTxt;
	private Button btnRemove;    
	private ListView listview;  //url列表控件
	private TextView txtAll;
	private CheckBox checkBox;
	private LinearLayout deleteBar;
	private Button btnDelete;
	private Button btnCancle;

	private SysUrlAdapter urlAdapter;     //url列表自定义适配器
	
	private  ArrayList<UrlModel> urlModelList;    //所有的url对象
	private   ArrayList<UrlModel> refUrlModelList;  //引用url对象
	public  static ArrayList<UrlModel> deteleUrlList;    //删除url对象集合
	private MyBroadcastReceiver mEventReceiver;
	private ConfigUrl config; //xml文件操作对象
	private Field field;    //反射过滤对象
	private boolean isCheckModel;
	
	private ApplicationModel appModel = ApplicationModel.getInstance();
	
	/**
	 * 保存历史记录
	 */
	private SaveHistoryShare historyShare;   
	
	/**
	 * 历史记录集合
	 */
	private ArrayList<String> mOriginalValues = new ArrayList<String>();
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		regedit();
		getConfig();
		deteleUrlList=new ArrayList<UrlModel>();
		urlModelList=config.getAllUrl();
		if(getIntent().getExtras()!=null){
			refUrlModelList=(ArrayList<UrlModel>)getIntent().getExtras().get("urlModel");
		}
		showView();
		historyShare=new SaveHistoryShare(getApplicationContext());
        historyShare.getHistoryDataFromSP(this.getPackageName(), "HistoryURL", mOriginalValues);
	}
	
	@SuppressWarnings("unchecked")
	private Handler mhHandler=new Handler(new Handler.Callback() {//接收adapter发送过来的消息
		
		@Override
		public boolean handleMessage(Message msg) {
            refUrlModelList=(ArrayList<UrlModel>)msg.obj;
			return true;
		}
	});
	
	
	/**
	 * 加载控件
	 */
	private void showView() {
	    	//绑定Layout里面的ListView
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.task_httppage_url_list, null); 
			((TextView)textEntryView.findViewById(R.id.title_txt)).setText(R.string.task_http_url_list);//设置标题
			((ImageView)textEntryView.findViewById(R.id.pointer)).setOnClickListener(new OnClickListener() { //设置后退操作
				@Override
				public void onClick(View v) {  //点击后退并把引用的对象携带回去
					if(isCheckModel){
						dimissCheck();
					}else{
						Intent intent=new Intent();
						urlModelList=SysURL.this.getConfig().getAllUrl();
						intent.putExtra("backUrlModelList", getNewestUrl(urlModelList, refUrlModelList));
						SysURL.this.setResult(RESULT_OK, intent);
						SysURL.this.finish();
						SysURL.this.overridePendingTransition(0, R.anim.slide_in_down);
					}
				}
			});
	        textEntryView.setVerticalScrollBarEnabled(true);
	        listview=(ListView)textEntryView.findViewById(R.id.ListView01);
	        if(urlAdapter==null){
	        	urlAdapter=new SysUrlAdapter(urlModelList, SysURL.this,refUrlModelList,mhHandler);
	        }
	        listview.setAdapter(urlAdapter); //此处注入list数据
	        if(appModel.isTestJobIsRun()){
	        	listview.invalidateViews();
	        }else{
	        	listview.invalidateViews();
	        	listview.setOnItemClickListener(this);
	        }
	        urlAdapter.notifyDataSetChanged();
	        setContentView(textEntryView);
	        genToolBar();
	}
	
	
    @Override
    public void onDestroy() {
        super.onDestroy();
		this.unregisterReceiver(mEventReceiver);
    }
    
	/**
	 * 注册广播接收器
	 */
	private void regedit() {
		IntentFilter filter = new IntentFilter();
		filter.addAction( FileExplorer.ACTION_LOAD_NORMAL_FILE);
		filter.addAction(WalkMessage.NOTIFY_TESTJOBDONE);
		filter.addAction(WalkMessage.NOTIFY_INTERRUPTJOBDONE);
		mEventReceiver = new MyBroadcastReceiver();
		this.registerReceiver(mEventReceiver, filter);
	}
	
	
	/**生成配置保存对象
	 */
	private ConfigUrl getConfig() {		
		this.config = new ConfigUrl();
		return config= new ConfigUrl();
	}
	

	/**
	  *生成底部工具栏
	  * 
	  */

	private void genToolBar() {
		bar = (ControlBar) findViewById(R.id.ControlBar);
		bar.setVisibility(appModel.isTestJobIsRun()?View.GONE:View.VISIBLE);
        txtAll = initTextView(R.id.TextViewAll);
        checkBox = (CheckBox) findViewById(R.id.CheckBoxAll);
        deleteBar = initLinearLayout(R.id.DeleteBar);
        btnDelete = initButton(R.id.ButtonDelete);
        btnDelete.setOnClickListener(new OnClickListener() {
			//最终删除操作 ,弹出告警框
			@Override
			public void onClick(View v) {
	    		new BasicDialog.Builder( SysURL.this)
	    		.setIcon(android.R.drawable.ic_menu_delete)
	    		.setTitle(R.string.delete)
	    		.setMessage( R.string.str_delete_makesure  )
	    		.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
	    			@Override
	    			public void onClick(DialogInterface dialog, int which) {
	    				if(urlModelList.size()==0){
	    					Toast.makeText(getApplicationContext(), "List is null!", Toast.LENGTH_SHORT).show();
	    					return;
	    				}
	    				urlModelList.removeAll(deteleUrlList);
	    				LogUtil.i(tag, deteleUrlList.size()+">>>>>>216");
	    				SysURL.this.getConfig().removeUrls(getUrlNames(deteleUrlList));
	    				LogUtil.i(tag, deteleUrlList.size()+">>>>>>219");
	    				urlAdapter.notifyDataSetChanged(isCheckModel);
	    				dimissCheck();
	    			}
	    		})
	    		.setNegativeButton(R.string.str_cancle).show();
			}
		});
        btnCancle = initButton(R.id.ButtonCancle);
        btnCancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dimissCheck();   //取消操作
			}
		});
		//get button from bar
		btnNew = bar.getButton(0);
		importTxt = bar.getButton(1);
		btnRemove = bar.getButton(2);
		//set icon
				btnNew.setCompoundDrawablesWithIntrinsicBounds(null,
						getResources().getDrawable(R.drawable.controlbar_new), null, null);
				importTxt.setCompoundDrawablesWithIntrinsicBounds(null,
						getResources().getDrawable(R.drawable.controlbar_load), null, null);
				btnRemove.setCompoundDrawablesWithIntrinsicBounds(null,
						getResources().getDrawable(R.drawable.controlbar_delete), null, null);
		//set text
		btnNew.setText( R.string.task_http_url_new);
		btnNew.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
		importTxt.setText(R.string.task_http_url_import);
		importTxt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new LicenseExplorer(SysURL.this,
						new String[] { "txt" }, LicenseExplorer.LOADING_URL ,FileExplorer.ACTION_LOAD_NORMAL_FILE,FileExplorer.KEY_FILE)
						.start();
			}
		});
		btnRemove.setText(R.string.task_http_url_delete);
		btnRemove.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displayUrlDetele();  //点击删除时显示可选模式
			}

		});
	}
	 
	/**
	 * 
	 * @param 返回所有的url名
	 * @return
	 */
	public String[] getUrlNames(ArrayList<UrlModel>  deteleUrlList ){
		ArrayList<String> emp=new ArrayList<String>();
		for(int i=0;i<deteleUrlList.size();i++){
			emp.add(deteleUrlList.get(i).getName());
		}
		return emp.toArray(new String[emp.size()]);
	}
	
	
	
	
	/**
	 * 
	  * 新增url
	 */
	
	private void showDialog(){
		LayoutInflater factory = LayoutInflater.from(SysURL.this);
		View alert_view=factory.inflate(R.layout.alert_dialog_edittext_url, null);
		final CustomAutoCompleteTextView textView=(CustomAutoCompleteTextView)alert_view.findViewById(R.id.alert_textEditText);
		textView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (arg2 == arg0.getCount() - 1){
					historyShare.clearData();
					textView.setText("");
				}
			}
		});
		historyShare.initAutoComplete(textView);
		textView.setText("http://");
		textView.setSelection(textView.getText().length());
		new BasicDialog.Builder(this)
		.setView(alert_view)
		.setIcon(R.drawable.controlbar_addto)
		.setTitle(R.string.task_http_url_input)
		.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					//通过反射让不符合条件的弹出框不消失
					field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");   
					field.setAccessible(true);
					field.set(dialog,false);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(textView.getText().toString().trim().length()==0||textView.getText().toString().equals("")){
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.task_http_url_null), Toast.LENGTH_SHORT).show();
					return;
				}else if(!Verify.isUrl(textView.getText().toString().trim())){
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.task_alert_nullUrl), Toast.LENGTH_SHORT).show();
					return;
				}else if(SysURL.this.getConfig().contains(textView.getText().toString().trim())){
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.task_http_url_iscf), Toast.LENGTH_SHORT).show();
					return;
				}
				 
				UrlModel urlModel=new UrlModel();
				urlModel.setEnable("1");
				urlModel.setName(textView.getText().toString());
				SysURL.this.getConfig().addUrl(urlModel);
				historyShare.saveHistory(textView);
				urlModelList.add(urlModel);
				urlAdapter.notifyDataSetChanged();
				try {
					field.set(dialog, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		})
		.setNegativeButton(R.string.str_cancle,new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				try {
					field.set(dialog, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		})
		.show();
	}


	/*
	  * 如果为正常item点击事件,编辑url   
	  * 如果为删除模式，删除url
	  */
	
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
		if(isCheckModel){
			ImageView ItemCheckble = (ImageView) arg1.findViewById(R.id.ItemCheckble);
			UrlModel urlModel=new UrlModel();
			urlModel.setEnable(((UrlModel)urlModelList.get(arg2)).getEnable());
			urlModel.setName(((UrlModel)urlModelList.get(arg2)).getName());
			if (deteleUrlList.size()==0?true:!deteleUrlList.contains((urlModelList.get(arg2)))) {
				ItemCheckble.setImageResource(R.drawable.btn_check_on);
				deteleUrlList.add(urlModel);
			} else {
				ItemCheckble.setImageResource(R.drawable.btn_check_off);
				deteleUrlList.remove(urlModel);
			}
			LogUtil.i(tag, deteleUrlList.size()+">>>>>>392");
			if(deteleUrlList.size()!=0){
				btnDelete.setEnabled(true);
			}
		}else{
			BasicDialog.Builder builder = new BasicDialog.Builder(this);
			LayoutInflater factory = LayoutInflater.from(SysURL.this);
			View alert_view=factory.inflate(R.layout.alert_dialog_edittext_url, null);
			final CustomAutoCompleteTextView textView=(CustomAutoCompleteTextView)alert_view.findViewById(R.id.alert_textEditText);
			textView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if (arg2 == arg0.getCount() - 1){
						historyShare.clearData();
						textView.setText("");
					}
				}
			});
			historyShare.initAutoComplete(textView);
			textView.setText(((UrlModel)urlModelList.get(arg2)).getName());
			builder.setView(alert_view);
			builder.setIcon(R.drawable.controlbar_edit);
			builder.setTitle(R.string.task_http_url_input);
			builder.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
				
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						//通过反射让不符合条件的弹出框不消失
						field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");   
						field.setAccessible(true);
						field.set(dialog,false);
					} catch (Exception e) {
						e.printStackTrace();
					}
					String editUrl=textView.getText().toString().trim();
					String getUrl=((UrlModel)urlModelList.get(arg2)).getName();
					if(editUrl.length()==0||editUrl.equals("")){
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.task_http_url_null), Toast.LENGTH_SHORT).show();
						return;
					}else if(!Verify.isUrl(editUrl)){
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.task_alert_nullUrl), Toast.LENGTH_SHORT).show();
						return;
					}else if(!editUrl.equals(getUrl)&&SysURL.this.getConfig().contains(editUrl)){
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.task_http_url_iscf), Toast.LENGTH_SHORT).show();
						return;
					}
					historyShare.saveHistory(textView);
					config.setEnble(getUrl, "0");
					config.setUrlName(getUrl, editUrl);
					ConfigUrl configEdit=new ConfigUrl();
					urlModelList=configEdit.getAllUrl();
					urlAdapter=new SysUrlAdapter(urlModelList, SysURL.this,refUrlModelList,mhHandler);
					listview.setAdapter(urlAdapter);
					urlAdapter.notifyDataSetChanged();
					try {
						field.set(dialog, true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			builder.setNegativeButton(R.string.str_cancle,new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					try {
						field.set(dialog, true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			builder.show();
		}
	}
	
	
	/**
	 * 显示可选模式
	 */
	private void displayUrlDetele() {
		isCheckModel = true;
        btnDelete.setText(getString(R.string.delete));
        checkBox.setVisibility(View.INVISIBLE);
        checkBox.setChecked(false);
        txtAll.setVisibility(View.INVISIBLE);
        urlAdapter.notifyDataSetChanged(isCheckModel);
        findViewById(R.id.control_btn_layout).setVisibility(View.VISIBLE);
        deleteBar.setVisibility(View.VISIBLE);
	}
	
    /**取消可选模式*/
    private void dimissCheck() {
        isCheckModel = false;
        /*    	for( int i=0;i<taskListItemMap.size();i++ ){
            		taskListItemMap.get( i ).put("ItemCheckble", R.drawable.empty );
            	}*/
        deteleUrlList.clear();
        deleteBar.setVisibility(View.INVISIBLE);
        checkBox.setVisibility(View.INVISIBLE);
        txtAll.setVisibility(View.INVISIBLE);
        urlAdapter.notifyDataSetChanged(isCheckModel);
        findViewById(R.id.control_btn_layout).setVisibility(View.GONE);
    }
	
    
	/**
	 * 
	 * @param 取最终引用有效的url
	 * @param refList
	 * @return
	 */
	private ArrayList<UrlModel> getNewestUrl(ArrayList<UrlModel> allList,ArrayList<UrlModel> refList){
		ArrayList<UrlModel> newList=new ArrayList<UrlModel>();
		ArrayList<UrlModel> goodUrlModel=new ArrayList<UrlModel>();
		for(int i=refList.size()-1;i>=0;i--){
			for(int j=0;j<allList.size();j++){
				if(refList.get(i).getName().equals(allList.get(j).getName())){
					newList.add(refList.get(i));
					break;
				}
			}
		}
		/**
		 * 将url列表顺序按照原始列表排列
		 */
		for(int i=0;i<allList.size();i++){
        	for(int j=0;j<newList.size();j++){
        		if(allList.get(i).getName().equals(newList.get(j).getName())){
        			goodUrlModel.add(newList.get(j));
        			break;
        		}
        	}
        }
		return goodUrlModel;
		
	}


	/*
	  * 捕捉返回事件，将引用对象携带回上一个界面
	  */
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
			if(isCheckModel){
				dimissCheck();
			}else{
				Intent intent=new Intent();
				urlModelList=SysURL.this.getConfig().getAllUrl();
				intent.putExtra("backUrlModelList", getNewestUrl(urlModelList,refUrlModelList));
				SysURL.this.setResult(RESULT_OK, intent);
				SysURL.this.finish();
				SysURL.this.overridePendingTransition(0, R.anim.slide_in_down);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 广播接收器:接收来自Fleet.java的广播更新界面
	 * */
	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("~~~~~~~~~~~~~~~~~文件浏览"+intent.getAction());
			if (intent.getAction().equals(FileExplorer.ACTION_LOAD_NORMAL_FILE)) {
				String filePath = "";
				try {
					filePath = intent.getStringExtra(FileExplorer.KEY_FILE);
				} catch (Exception e) {
					e.printStackTrace();
				}
				BufferedReader bfReader;
				File file = new File(filePath);
				if (file.exists()) {
					LogUtil.i(tag, "文件路径>>>>>>>>>>>>>>" + filePath);
					FileInputStream inStream = null;
					try {
//						Boolean showTip=false;
						inStream = new FileInputStream(file);
						bfReader = new BufferedReader(new InputStreamReader(
								inStream));
						try {
							String line;
							// 文件从头到尾每行只读一次
							int count=0;
							while ((line = bfReader.readLine()) != null) {
								if(!Verify.isUrl(line)){  //判断http是否合法
									continue;
								}else if(SysURL.this.getConfig().contains(line)){   //判断url地址是否重复
									continue;
								}
								else{
									UrlModel urlModel=new UrlModel();
									LogUtil.i(tag, "导入>>>>>>>>>>>>>>" + line);
									urlModel.setName(line);
									urlModel.setEnable("1");
									SysURL.this.getConfig().addUrl(urlModel);
									urlModelList.add(urlModel);
									count++;
								}
							}
							Toast.makeText(getApplicationContext(), "Import success "+count+" URL!", Toast.LENGTH_SHORT).show();
							urlAdapter.notifyDataSetChanged();
						} catch (IOException e) {
							e.printStackTrace();
							Toast.makeText(getApplicationContext(), "Import fail!", Toast.LENGTH_SHORT).show();
						}
					} catch (FileNotFoundException fe) {
						fe.printStackTrace();
					} finally {
						try {
							inStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			if(intent.getAction().equals(WalkMessage.NOTIFY_TESTJOBDONE) 
					|| intent.getAction().equals(WalkMessage.NOTIFY_INTERRUPTJOBDONE)){
				showView();
				LogUtil.i(tag, "~~~~url测试完成~~~~"+appModel.isTestJobIsRun());
			}
		}
	}
}
