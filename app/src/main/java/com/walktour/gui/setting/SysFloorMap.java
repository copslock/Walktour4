package com.walktour.gui.setting;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.dinglicom.data.model.RecordImg;
import com.walktour.Utils.BitmapUtils;
import com.walktour.Utils.DensityUtil;
import com.walktour.Utils.ImageUtil;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigIndoor;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.applet.ControlBar;
import com.walktour.gui.applet.FileExplorer;
import com.walktour.gui.applet.ImageExplorer;
import com.walktour.gui.applet.MySimpleAdapter;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.map.MapView;
import com.walktour.model.FloorModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("InflateParams")
public class SysFloorMap extends BasicActivity {
	private static final String tag = "SysFloorMap";
	private static final int DELETE      = 11;
	private static final int DELETE_END  = 12;
	private static final int BROWSER = 2;
	public final static String ACTION_LOADFILE = "Walktour.SysIndoorBuilding"; 
	private final static String KEY_EXTRA_NAME = "filePath";
	private int ITEM_MAP_POSITION;			//????????????????????????
	private ProgressDialog progressDialog;
	//ListView
	private ArrayList<HashMap<String, Object>> listItemMap;
	private MySimpleAdapter listItemAdapter;
	/**
	 * ????????????
	 */
	private List<FloorModel> floorList;
	private List<String> floorMaps;
	private ConfigIndoor config;
	private String floorDir;
	private String buildDir;
	private int floorPosition;
	public boolean isLoading=false;	
	private static List<Integer> list;//???????????????????????????????????????
	private Context mContext;
	
	private String floor_node_id = "";
	private ArrayList<RecordImg> recordImgList = new ArrayList<RecordImg>();
	private SysBuildingManager mSysBuildingManager;

	public static boolean isFromIndoor = false;//fix bug:# 211 qt????????????????????????????????????
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_with_controlbar );
		mContext = this;
		mSysBuildingManager = SysBuildingManager.getInstance(mContext);
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_LOADFILE);
		registerReceiver(myReceiver, filter);
		try {
	        this.getBundle();
	        this.getConfig();
	        buildRecordImgList();
	        this.findView();//????????????
	        this.genToolBar();//???????????????
        } catch (Exception e) {
            this.finish();
        }

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(myReceiver);
	}
	
	private String buildName = "";
	private String floorName = "";
//	private String buildAddress = "";
	private void getBundle(){
		Bundle bundle = getIntent().getExtras();
		isLoading     = bundle.getBoolean(SysIndoorBuilding.IsLoadIndoorMap);
		//????????????
		buildDir  	  = bundle.getString(SysIndoorBuilding.BuildDir);
		//????????????
//		buildAddress  = bundle.getString("build_address");
		//????????????
		buildName 	  = buildDir.substring(buildDir.lastIndexOf("/")+1,buildDir.length());
		//?????????????????????
		floorPosition = bundle.getInt(SysIndoorBuilding.FloorPosition);
		
		floor_node_id = bundle.getString(SysIndoorBuilding.FloorNodeId);
	}
	
	/**
	 * ?????????????????????
	 * @param str
	 * @return
	 */
	public boolean isNumeric(String str){
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		return isNum.matches();
	} 
	
	private void getConfig(){
		config = ConfigIndoor.getInstance(this);
		floorList = config.getFloorList(this,new File(buildDir));
		// ???????????????????????????
		floorMaps = floorList.get(floorPosition).getAllMapPaths();
		list = new ArrayList<Integer>();
		if (floorMaps != null) {
			// ???????????????????????????????????????
			for (String s : floorMaps) {
				if (s.indexOf(".") > -1) {
					String index = s.substring(s.lastIndexOf("_") + 1, s.lastIndexOf("."));
					if (isNumeric(index)) {
						int a = Integer.parseInt(index);
						LogUtil.i(tag, "---map index:" + a);
						list.add(a);
					}
				}
			}
		}
		//????????????
		floorDir   = floorList.get(floorPosition).getDirPath();
		//????????????
		floorName  = floorDir.substring(floorDir.lastIndexOf("/")+1,floorDir.length());
		setTitle(floorDir);
	}
	
	/**
	 * ?????????????????????
	 */
	private void buildRecordImgList() {
		recordImgList.clear();
		recordImgList.addAll(mSysBuildingManager.buildRecordImgList(floor_node_id));
		
	}
	
	/**
	 * ???????????????
	 * @param list
	 */
	private void deleteMap(ArrayList<RecordImg> list) {
		for (RecordImg recordImg : list) {
			mSysBuildingManager.deleteMap(recordImg);
		}
	}
	
	/**
	 * ??????????????????
	 * @param filePath
	 */
	private void addMap(String filePath) {
		mSysBuildingManager.addMap(floor_node_id, filePath);
		buildRecordImgList();
	}
	
	private void findView() {
		//??????Layout?????????ListView  
        ListView list = (ListView) findViewById(R.id.ListView01); 
       //???????????????????????????????????????????????????item
        listItemMap = new ArrayList<HashMap<String, Object>>();
        for(int i = 0;i<floorMaps.size();i++){
        	File file = new File(floorMaps.get(i));
        	/*long time = file.lastModified();
        	long min = 0;
        	min = min<time?min:time;*/
        	if(file.isFile()){
        		HashMap<String, Object> map = new HashMap<String, Object>();
        		map.put("ItemTitle", file.getName().replaceAll(".jpg", ""));
        		map.put("ItemImage", R.drawable.list_item_map );
        		map.put("ItemCheckble", false);
        		listItemMap.add(map);
        	}
        }
        
        listItemAdapter = new MySimpleAdapter(this, listItemMap, 
        		R.layout.listview_item_style10, new String[]{"ItemTitle","ItemImage","ItemCheckble"},new int[]{R.id.ItemTitle,R.id.ItemImage,R.id.ItemCheckble});
        list.setAdapter(listItemAdapter);
        list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				ITEM_MAP_POSITION = position;
				return false;
			}
		});
        list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ITEM_MAP_POSITION = position;
				try {
					File f = new File(floorMaps.get(position));
					if(f.isFile()){
						LogUtil.i(tag, "---floorMaps.get(position)="+floorMaps.get(position));
						showMyDialog(floorMaps.get(position));
					}
				} catch (Exception e) {
				}
			showMenuDialog();
				
			}
		});
		list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				String mapName = floorMaps.get(ITEM_MAP_POSITION).substring(
						floorMaps.get(ITEM_MAP_POSITION).lastIndexOf("/")+1,
						floorMaps.get(ITEM_MAP_POSITION).length());				
				menu.setHeaderTitle(mapName.replaceAll(".jpg", ""));
				menu.add(0, DELETE, 0, R.string.sys_indoor_removeMap);
				menu.add(0, BROWSER, 0, R.string.browser);
			}
		});
	}  
	
	/**
	 * ?????????????????????<BR>
	 * [??????????????????]
	 */
	private void showMenuDialog(){
		String mapName = floorMaps.get(ITEM_MAP_POSITION).substring(
				floorMaps.get(ITEM_MAP_POSITION).lastIndexOf("/")+1,
				floorMaps.get(ITEM_MAP_POSITION).length());		
		new BasicDialog.Builder(this).setTitle(mapName)
		.setItems(new String[]{getResources().getString(R.string.sys_indoor_removeMap),getResources().getString(R.string.browser)},
				new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					new BasicDialog.Builder( SysFloorMap.this )
					.setIcon(android.R.drawable.ic_menu_delete)
					.setTitle(R.string.delete)
					.setMessage(R.string.str_delete_makesure)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							showProgressDialog();
							ArrayList<String> removeList = new ArrayList<String>();
							removeList.add(floorMaps.get(ITEM_MAP_POSITION));
							new DeleteThread(removeList).start();
						}
					})
					.setNegativeButton(R.string.str_cancle)
					.show();
					break;
				case 1:
					showMyDialog(floorMaps.get(ITEM_MAP_POSITION));
					break;

				default:
					break;
				}
				
			}
		} );
	}
	
	/**
	 * ???????????????
	 * */
	private void showMyDialog(final String mapPath){
	    //final File file = new File(mapPath);
		LayoutInflater factory = LayoutInflater.from(this);
    	View textEntryView = factory.inflate(R.layout.alert_dialog_imageview, null);
    
    	setImageView(textEntryView,mapPath);
    	if(isLoading){
    		new BasicDialog.Builder(this)  		             
    		.setView( textEntryView )
    		.setTitle(floorDir)
    		.setPositiveButton(R.string.str_load, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    			    String path = mapPath;
    			    if(!StringUtil.isNullOrEmpty(floorList.get(floorPosition).tabfilePath)){
    			        path = floorList.get(floorPosition).tabfilePath;

    			    }
    				File f = new File(path);
    				if(f.isFile()){
						long length = f.length();
						LogUtil.i(tag, "-----length:"+length/(1000*1000));
						if(length>FileExplorer.INDOOR_FILE_SIZE){
							Toast.makeText(SysFloorMap.this, R.string.main_indoor_largefile, Toast.LENGTH_SHORT).show();
							return;
						}
    				}
    				backForResult(path);
    			}
    		})
    		.setNegativeButton(R.string.str_return)
    		.show();
    	}else{
    		new BasicDialog.Builder(this)  		             
    		.setView( textEntryView )
    		.setTitle(floorDir)
    		.setNeutralButton(R.string.str_return, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				dialog.dismiss();
    			}
    		})
    		.show();
    	}
		
	}
	
	/**
	 * ???????????????view??????????????????????????????file
	 * */
	private void setImageView(View view,String mapPath){
		ImageView imageView = (ImageView) view.findViewById(R.id.ImageView01);
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.outHeight = 300;
		opts.outWidth = view.getWidth();
		try {
			Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromFd(mapPath, 0, DensityUtil.dip2px(mContext, 300));
			bitmap= ImageUtil.rotaingImageView(mapPath,bitmap);
        	imageView.setImageBitmap(bitmap);
		} catch (Exception e) {
			LogUtil.v(tag, e.toString() );
		}
	}

	/**
	 * ??????????????????????????????????????????setResult????????????finish()
	 * @param path  ???????????????
	 */
	protected void backForResult(String path) {
		LogUtil.v(tag, "backForResult:"+path);
		isFromIndoor = true;//fix bug:# 211 qt????????????????????????????????????
		//??????????????????????????????????????????SharedPreference
		SharePreferencesUtil sharePreferencesUtil = SharePreferencesUtil.getInstance(mContext);
		sharePreferencesUtil.saveString(MapView.SP_INDOOR_MAP_PATH,path);
		float plottingScale = sharePreferencesUtil.getFloat(path,1);
		MapFactory.getMapData().setPlottingScale(plottingScale);
		sharePreferencesUtil.saveBoolean(MapView.SP_IS_LOAD_INDOOR_MAP,true);
		Intent intent_back = this.getIntent();
		Bundle bundle = new Bundle(); 
        bundle.putString(SysIndoor.KEY_RESULT, path );//????????????
        intent_back.putExtras(bundle);
        this.setResult(RESULT_OK, intent_back);
		this.finish();
	}//end method backForResult
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()){
		//????????????
		case DELETE:
			new BasicDialog.Builder( SysFloorMap.this )
			.setIcon(android.R.drawable.ic_menu_delete)
			.setTitle(R.string.delete)
			.setMessage(R.string.str_delete_makesure)
			.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					showProgressDialog();
					ArrayList<String> removeList = new ArrayList<String>();
					removeList.add(floorMaps.get(ITEM_MAP_POSITION));
					new DeleteThread(removeList).start();
				}
			})
			.setNegativeButton(R.string.str_cancle)
			.show();
			break;
		case BROWSER:
			showMyDialog(floorMaps.get(ITEM_MAP_POSITION));
			break;
		}
		return super.onContextItemSelected(item);
	}
	    
	/**?????????????????????*/
	private void genToolBar() {
		ControlBar bar = (ControlBar) findViewById(R.id.ControlBar);
		//get button from bar
		Button btnNew = bar.getButton(0);
		Button btnCheckAll = bar.getButton(1);
		Button btnCheckNon = bar.getButton(2);
		Button btnRemove = bar.getButton(3);
		
		//set text
		btnNew.setText( R.string.sys_indoor_configMap);
		btnCheckAll.setText(R.string.str_checkall);
		btnCheckNon.setText(R.string.str_checknon);
		btnRemove.setText(R.string.delete);
		
		
		//set icon
		btnNew.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.controlbar_new), null, null);
		btnCheckAll.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.controlbar_allcheck), null, null);
		btnCheckNon.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.controlbar_unallcheck), null, null);
		btnRemove.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.controlbar_clear), null, null);
		
		btnNew.setOnClickListener(btnListener);
		btnCheckAll.setOnClickListener(btnListener);
		btnCheckNon.setOnClickListener(btnListener);
		btnRemove.setOnClickListener(btnListener);
	}
	
	//????????????????????????????????????
	private void editFloorMap(){
        ImageExplorer imageExplorer = new ImageExplorer(
        		this,
        		SysFloorMap.ACTION_LOADFILE,
        		SysFloorMap.KEY_EXTRA_NAME, 
        		getResources().getStringArray(R.array.maptype_picture)
        );
        imageExplorer.start();
	}
	
	
	/**
	 * ???????????????:????????????FileExplorer?????????????????????
	 * */
	private  BroadcastReceiver myReceiver =  new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			//???????????????????????????
			if(intent.getAction().equals(ACTION_LOADFILE ) ){
				String map_file_path = intent.getExtras().getString(KEY_EXTRA_NAME );
				LogUtil.i(tag, "-----map_file_path:"+map_file_path);
				//?????????????????????tab??????????????????JPEG??????
				if(map_file_path.endsWith(".tab")){
					if(floorMaps != null){
						File tabfile = new File(map_file_path);
						String  jpegPath = map_file_path.replace(".tab",".jpeg");
						File jepgfile = new File(jpegPath);
						if(tabfile.isFile()&&jepgfile.isFile()){
							String desFileTabPath = floorDir+"/"+tabfile.getName();
							config.setMap(map_file_path,desFileTabPath);
							String desFileJpegPath = floorDir+"/"+jepgfile.getName();
							config.setMap(jepgfile.getAbsolutePath(),desFileJpegPath);
						}
						getConfig();
						findView();
						
					}
				}else{
					if(floorMaps != null){
						int max = 0;
						//????????????????????????????????????
						for(int i = 0;i<list.size();i++){
							max = list.get(i)>max?list.get(i):max;
						}
						LogUtil.i(tag, "---map max index:"+max);
						//???????????????
						String prefix_name = getString(R.string.str_lcpm)+buildName+"_"+floorName+"_"+(max+1);
						//???????????????
						String extension_name = ".jpg";
						//???????????????
						String filename = prefix_name+extension_name;
						//??????????????????
						String desFileFullPath = floorDir+"/"+filename;
						//??????????????????????????????
						config.setMap(map_file_path,desFileFullPath);
						
						getConfig();
						findView();
						addMap(map_file_path);
					}
				}
			}
			//sdcard????????????
			if( intent.getAction().equals( WalkMessage.ACTION_SDCARD_STATUS ) ){
				getConfig();//?????????????????????????????????
		        findView();//????????????
			}
			
		}
	};//end inner class EventBroadcastReceiver
	
	/**
	 * ??????????????????????????????
	 */
	private void showProgressDialog(){
		progressDialog = new ProgressDialog(SysFloorMap.this);
		progressDialog.setMessage(getString(R.string.removing));
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	
	/**?????????????????????*/
	private OnClickListener btnListener = new  OnClickListener(){
		public void onClick(View view) {
			switch( view.getId() ){
			case R.id.Button01:	//????????????
				editFloorMap();
				break;
			case R.id.Button02:	//??????
				checkAll();
				break;
			case R.id.Button03:	//??????
				checkOthers();
				break;
			case R.id.Button04://??????
				if(listItemAdapter.hasChecked()){
					new BasicDialog.Builder( SysFloorMap.this )
					.setIcon(android.R.drawable.ic_menu_delete)
					.setTitle(R.string.delete)
					.setMessage(R.string.str_delete_makesure)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							showProgressDialog();
							ArrayList<String> removeList = new ArrayList<String>();
							ArrayList<RecordImg> removeListFromDB = new ArrayList<RecordImg>();
							for(int i = 0;i<listItemAdapter.getCount();i++){
								if(listItemAdapter.getChecked()[i]){
									removeList.add(floorMaps.get(i));
									if (i < recordImgList.size()) {
										removeListFromDB.add(recordImgList.get(i));
									}
								}
							}
							new DeleteThread(removeList).start();
							deleteMap(removeListFromDB);
						}
					})
					.setNegativeButton(R.string.str_cancle)
					.show();
				}else{
					Toast.makeText(getApplicationContext(), 
		    				getString(R.string.str_check_non), Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};
	
	/**
	 * ????????????
	 */
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			//????????????
			case DELETE:
				String filePath = msg.obj.toString();
				progressDialog.setMessage(getString(R.string.removing) + filePath);
				break;
			//????????????
			case DELETE_END:
				if(progressDialog != null){
					progressDialog.dismiss();
				}
				getConfig();
				findView();
				break;
			}
		};
	};
	
	
	 /**
     * ???????????????????????????
     * @author Administrator
     *
     */
	class DeleteThread extends Thread{
		ArrayList<String> removeList = new ArrayList<String>();
		public DeleteThread(ArrayList<String> removeList){
			this.removeList = removeList;
		}
		
		@Override
		public void run() {
			Message msg;
			for(int i = removeList.size()-1;i>=0;i--){
				System.out.println("????????????:" + removeList.get(i));
				config.delete(removeList.get(i));
				msg = handler.obtainMessage(DELETE,removeList.get(i));
				handler.sendMessage(msg);
			}
			msg = handler.obtainMessage(DELETE_END);
			handler.sendMessage(msg);
		}
	}
	
	
	  /**
     * ??????
     * */
    private void checkAll( ){
    	for(int i=0;i<listItemMap.size();i++){
    		listItemMap.get(i).put("ItemCheckble",true);
    	}
    	listItemAdapter.notifyDataSetChanged();
    }
    
    /**
     * ??????
     * */
    private void checkOthers(){
		for(int i=listItemAdapter.getCount()-1;i>=0;i--){
    		boolean check = ! listItemAdapter.getChecked()[i]  ;
    		listItemMap.get(i).put("ItemCheckble",check );
    	}
		listItemAdapter.notifyDataSetChanged();
    }
}
