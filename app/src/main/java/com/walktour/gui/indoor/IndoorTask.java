package com.walktour.gui.indoor;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigIndoor;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.CaptureImg;
import com.walktour.gui.R;
import com.walktour.gui.WalkTour;
import com.walktour.gui.setting.Sys;
import com.walktour.model.BuildingModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint({ "InflateParams", "HandlerLeak" })
public class IndoorTask extends BasicActivity {
	//指定拍照图片的存储目录
	public static final File IMG_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
	public static final String BUILD_POSITION = "buildPosition";
	private static final String tag = "IndoorTask";
	private static final String PREFS_NAME = "MyPrefsFile";
	private static final String INDOOR_TASK_TESTER = "Indoor_Task_tester";
	private final String defaultValue = "N/A";
	private static String longitude = "N/A";  //经度
	private static String latitude = "N/A";   //纬度
	private final int waitGpsTimeOut = 60;   //GPS超时时间
	private static final int gpsMsg = 1000; //停止GPS消息
	private static int item_postion_building;//选择的建筑位置
	private ConfigIndoor configIndoor;
	private Spinner spinnerBuilding;
	private Button btnConfig;
	private Button btnNext;
	private ImageView imageView;
	private CheckBox checkOpengps;
	private EditText editTestperson;
	private TextView editLongitude;
	private TextView editLatitude;
	private TextView buildTip;
	private Button btnCamera;
	private List<BuildingModel>  buildingList;
	private List<String > buildingNames;
	private GpsInfo gpsInfo;
	private Timer gpsTimer = null; //GPS计时器
	private TimerTask  gpsTask = null;
	private String buildName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!IMG_DIR.exists()){
   		 	IMG_DIR.mkdirs();
   	 	}
		gpsInfo = GpsInfo.getInstance();
		gpsTimer = new Timer();
		registerBroadcast();
		findView();//获取视图
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		initial();
		LogUtil.i(tag, "---onStart---");
	}
	
	/**
	 * 需要刷新数据的部分组件，在此执行
	 */
	private void initial(){
		configIndoor = ConfigIndoor.getInstance(this);
		boolean isAHworkorder = ApplicationModel.getInstance().isAnHuiTest();
		buildingList = configIndoor.getBuildings(this,isAHworkorder);
		setPositon(longitude, latitude);
		//若建筑物为空，则提示去配置
		if(buildingList.isEmpty()){
			setPositon(defaultValue, defaultValue);
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//				showDialog(1000);
			}
		}
		spinnerBuilding.setEnabled(!buildingList.isEmpty());//buildingList不为空时可用
//		btnNext.setEnabled(SaveIndoorData.IsGpsSearched&&!buildingList.isEmpty());//建筑物不为空并且GPS搜索过时可用
		//建筑不为空
		/*if(!buildingList.isEmpty()){
			//没有搜索过GPS
			if(!SaveIndoorData.IsGpsSearched){
				if(!SaveIndoorData.isGPSOpen){
					Toast.makeText(this, R.string.main_indoor_opengps, Toast.LENGTH_SHORT).show();
				}
			}
		}*/
		TextView editTesttime = initTextView(R.id.editTesttime);
		editTesttime.setText(sf.format(Calendar.getInstance().getTimeInMillis()));
		
		buildingNames = new ArrayList<String >();
		 for(int i=0;i<buildingList.size();i++){
			 buildingNames.add( buildingList.get(i).getName() );
		 }
		 if( buildingList.size()==0 ){
			 buildingNames.add( getString(R.string.none) );
		 }
		 final ArrayAdapter<String> adptBuild = new ArrayAdapter<String>(this,
		         R.layout.simple_spinner_custom_layout, buildingNames );
		 adptBuild.setDropDownViewResource(R.layout.spinner_dropdown_item);
		 spinnerBuilding.setAdapter( adptBuild );
		 
		 /**
		  * 若记录的位置小于建筑物数量，则仍然选中该建筑，
		  * 若不加此判断，在配置页面删除建筑物后返回时会发生异常
		  */
		 spinnerBuilding.setOnItemSelectedListener(itemSelectListener);
		 if(item_postion_building<buildingNames.size()){
			 spinnerBuilding.setSelection(item_postion_building,true);
			 LogUtil.i(tag, "---item_postion_building="+item_postion_building);
		 }
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		SaveIndoorData.selectBuildName = buildName;
		SaveIndoorData.isGPSOpen = checkOpengps.isChecked();
	}
	/**
	 * 注册接收广播
	 */
	private void registerBroadcast(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(GpsInfo.gpsLocationChanged);
		filter.addAction(WalkMessage.ACTION_SDCARD_STATUS);
		registerReceiver(myReceiver, filter);
	}
	
	/**
	 * 反注册接收广播
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(myReceiver);
		//退出时清除状态
		if(gpsInfo.isIndoorGpsOpen()){
			gpsInfo.releaseGps(IndoorTask.this, WalkCommonPara.OPEN_GPS_TYPE_INDOORTEST);
			spinnerBuilding.setEnabled(true);
			SaveIndoorData.isGPSOpen = false;
			clearGpstimer();
		}
	}
	/**
	 * 获取视图
	 */
	private void  findView() {
		setContentView(R.layout.indoor_task_view);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String tester = settings.getString(INDOOR_TASK_TESTER, "");
		
		btnConfig = initButton(R.id.btnConfig);
		btnNext = initButton(R.id.btnNext);
		btnCamera = initButton(R.id.btnCamera);
		imageView = initImageView(R.id.imageView);
		editTestperson = initEditText(R.id.editTestperson);
		editTestperson.setText(tester);
		btnConfig.setOnClickListener(btnListener);
		btnCamera.setOnClickListener(btnListener);
		btnNext.setOnClickListener(btnListener);
		editTestperson.setOnClickListener(btnListener);
		checkOpengps = (CheckBox)findViewById(R.id.checkOpengps);
		checkOpengps.setChecked(SaveIndoorData.isGPSOpen);
		checkOpengps.setOnCheckedChangeListener(checkListener);
		spinnerBuilding = (Spinner)findViewById(R.id.spinnerBuilding);
		editLongitude = initTextView(R.id.editLongitude);
		editLatitude = initTextView(R.id.editLatitude);
		buildTip = initTextView(R.id.buildTip);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case 1000:
			return new BasicDialog.Builder(this).setTitle(R.string.main_indoor_configtitle)
			.setMessage(R.string.main_indoor_configbuild)
			.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					configBuild();
				}
			})
			.setNegativeButton(R.string.str_cancle).create();
		case 1001:
			return new BasicDialog.Builder(this).setTitle(R.string.str_next)
			.setMessage(R.string.main_indoor_condions_empty)
			.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent it = new Intent(IndoorTask.this, WalkTour.class);
					Bundle bundle = new Bundle();
					bundle.putBoolean("indoor_special", true);
					bundle.putInt(BUILD_POSITION, item_postion_building);
					it.putExtras(bundle);
//					it.putExtra(BUILD_POSITION,item_postion_building);
					startActivity(it);
				}
			})
			.setNegativeButton(R.string.str_cancle).create();
		}
		return super.onCreateDialog(id);
	}
	
	
	/**
	 * 跳转到配置建筑页面
	 */
	private void configBuild(){
		Intent i = new Intent(IndoorTask.this, Sys.class);
		i.putExtra(Sys.CURRENTTAB, 5);
		startActivity(i);
	}
	private OnClickListener btnListener = new OnClickListener() {
		
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			//配置
			case R.id.btnConfig:
				configBuild();
				break;
			//下一步
			case R.id.btnNext:
				//没有填写测试人员
				if(editTestperson.getText().toString().equals("")||
						buildTip.getVisibility() == View.VISIBLE||
						longitude.equals(defaultValue)){
					showDialog(1001);
//					Toast.makeText(IndoorTask.this, R.string.main_indoor_fillperson, Toast.LENGTH_SHORT).show();
//					return;
				}else{
					Intent it = new Intent(IndoorTask.this, WalkTour.class);
					Bundle bundle = new Bundle();
					bundle.putBoolean("indoor_special", true);
					bundle.putInt(BUILD_POSITION, item_postion_building);
					it.putExtras(bundle);
					startActivity(it);
				}
				//建筑没有外观图
				/*if(buildTip.getVisibility() == View.VISIBLE){
					Toast.makeText(IndoorTask.this, R.string.main_indoor_photograph, Toast.LENGTH_SHORT).show();
					LogUtil.i(tag,"---imageView is null");
					return;
				}*/
				break;
			//编辑测试人员
			case R.id.editTestperson:
				LayoutInflater lf = LayoutInflater.from(getApplicationContext());
				View view = lf.inflate(R.layout.indoor_text_entry_dialog, null);
				final EditText editName = (EditText)view.findViewById(R.id.EditTextParams);
				editName.setText(editTestperson.getText());
				editName.setSelectAllOnFocus(true);
				new BasicDialog.Builder(IndoorTask.this)
				 .setView(view)
				 .setTitle(R.string.main_indoor_testperson)
				 .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog, int which) {
		    				LogUtil.i(tag, "---->test person:"+editName.getText().toString());
		    				if(!editName.getText().toString().equals("")){
		    					editTestperson.setText(editName.getText().toString());
		    					SharedPreferences shp = getSharedPreferences(PREFS_NAME, 0);
		    					SharedPreferences.Editor editor = shp.edit();
		    					editor.putString(INDOOR_TASK_TESTER, editName.getText().toString());
		    					editor.commit();
		    				}else{
		    					Toast.makeText(IndoorTask.this, R.string.monitor_dialog_inputagain, Toast.LENGTH_SHORT).show();
		    				}
		    			}
		    		})
		    		.setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog, int whichButton) {
		    				
		    			}
		    		})
		    		.create()
		    		.show();
				break;
			//拍摄照片
			case R.id.btnCamera:
				//sd卡没有挂载
				if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					Toast.makeText(IndoorTask.this,R.string.sdcard_unmount,Toast.LENGTH_SHORT).show();
					return;
				}
				//建筑物不为空
				if( !buildingList.isEmpty()){
					doTakePhoto(); 
				}else{
					Toast.makeText(IndoorTask.this,R.string.main_indoor_nonebuild,Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};
	
	/**
	 * 启动拍照intent，调用系统自带的拍照程序
	 */
	protected void doTakePhoto() {  
        try { 
        	/* Intent intent = new  Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        	 //手机不是XT800,指定拍照图片的存储目录 和名称
        	 if(!android.os.Build.MODEL.toLowerCase().equals("xt800")){
	        	 File f = new File(IMG_DIR, "camera.jpg");
	        	 Uri uri = Uri.fromFile(f);
	        	 intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        	 }*/
        	 Intent intent = new  Intent(IndoorTask.this,CaptureImg.class);
        	 startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException e) {  
           
        }  
    }  
	
	/**
	 * 拍照返回时获取图片存储目录下的图片
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtil.i(tag, "---resultCode:"+resultCode);
		if(resultCode != RESULT_OK)return;
		if(requestCode == 0){
			File latestfile = new File(data.getStringExtra(CaptureImg.MAP_PATH));
			LogUtil.i(tag, "--latestfile path:"+latestfile.getAbsolutePath());
			if(latestfile.exists()){
				showImage(latestfile.getAbsolutePath());
				String buildDir = buildingList.get(item_postion_building).getDirPath();
				String buildImageName = getString(R.string.str_jzwg)+buildName+".jpg";
				String desMapPath = buildDir+"/"+buildImageName;
				
				//删除原有建筑外观图
				configIndoor.delete(desMapPath);
				LogUtil.i(tag, "---buildDir:"+buildDir);
				LogUtil.i(tag, "---lastfile:"+latestfile.getAbsolutePath());
				//拷贝外观图至对应的建筑目录下
				configIndoor.setMap(latestfile.getAbsolutePath(), desMapPath);
				
			}
		}
		
	}
	
	/**
	 * 显示建筑外观图
	 * @param imgPath
	 */
	private void showImage(String imgPath){
		LogUtil.i(tag, "---showImage");
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 3;
		Bitmap bmp = BitmapFactory.decodeFile(imgPath, opts);
		imageView.setImageBitmap(bmp);
	}
	
	/**
	 * 设置经度和纬度
	 * @param longitude
	 * @param latitude
	 */
	private void setPositon(String longitude,String latitude){
		editLongitude.setText(longitude);
		editLatitude.setText(latitude);
	}
	
	private OnCheckedChangeListener checkListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch(buttonView.getId()){
			case R.id.checkOpengps:
				if(buildingList.isEmpty()){
					Toast.makeText(IndoorTask.this, R.string.main_indoor_nonebuild, Toast.LENGTH_SHORT).show();
					checkOpengps.setChecked(false);
					return;
				}
				if(isChecked){
					//打开GPS时将重新定位，界面上清空经度和纬度
					setPositon(defaultValue, defaultValue);
//					btnNext.setEnabled(false);
					spinnerBuilding.setEnabled(false);//GPS正在搜索时spinnerBuilding不可用
					startGpstimer();
					gpsInfo.openGps(IndoorTask.this, WalkCommonPara.OPEN_GPS_TYPE_INDOORTEST);
				}else{
					gpsInfo.releaseGps(IndoorTask.this, WalkCommonPara.OPEN_GPS_TYPE_INDOORTEST);
					spinnerBuilding.setEnabled(true);//GPS停止时spinnerBuilding可用
					clearGpstimer();
					//如果已经搜索过GPS
					/*if(SaveIndoorData.IsGpsSearched){
						btnNext.setEnabled(true);
					}*/
				}
				break;
				
			}
		}
	};
	
	/**
	 * 启动GPS计时器
	 */
	private void startGpstimer(){
		LogUtil.i(tag, "---start timer");
		gpsTask = new TimerTask() {
			int time = 0;
			@Override
			public void run() {
				time++;
				LogUtil.i(tag, "---wait gps:"+time);
				//等待GPS超过60秒时停止GPS搜索
				if(time>=waitGpsTimeOut){
					Message msg = myHandler.obtainMessage(gpsMsg);
					myHandler.sendMessage(msg);
				}
			}
		};
		gpsTimer.schedule(gpsTask, 0, 1000);
	}
	
	/**
	 * 清除GPS计时器
	 */
	private void clearGpstimer(){
		LogUtil.i(tag, "---clear timer");
		if(gpsTask != null){
			gpsTask.cancel();
			gpsTask = null;
		}
	}
	
	/**
	 *处理GPS搜索超时消息
	 */
	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case gpsMsg:
				LogUtil.i(tag, "-----search GPS fail!");
				TestInfoValue.latitude = -9999.99;
				TestInfoValue.longtitude = -9999.99;
				Toast.makeText(IndoorTask.this, R.string.main_indoor_opengpsfail, Toast.LENGTH_SHORT).show();
				SaveIndoorData.IsGpsSearched = true;
				SaveIndoorData.isGPSOpen = false;
				clearGpstimer();
				gpsInfo.releaseGps(IndoorTask.this, WalkCommonPara.OPEN_GPS_TYPE_INDOORTEST);
//				btnNext.setEnabled(true);
				spinnerBuilding.setEnabled(true);//GPS停止时spinnerBuilding可用
				checkOpengps.setChecked(false);
				setPositon(defaultValue,defaultValue);
				break;
			}
		};
	};
	
	/**
	 * 处理GPS信息
	 */
	private BroadcastReceiver myReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			//GPS定位成功
			if(action.equals(GpsInfo.gpsLocationChanged) && gpsInfo.getLocation() != null ){
//				LogUtil.i(tag, "---search gps successful!");
				SaveIndoorData.IsGpsSearched = true;
				SaveIndoorData.isGPSOpen = false;
				clearGpstimer();  //关闭计时器
//				btnNext.setEnabled(true);   //允许执行下一步
				checkOpengps.setChecked(false); //关闭打开GPS的勾选框
				longitude = StringUtil.formatStr(gpsInfo.getLocation().getLongitude()+"");
				latitude  = StringUtil.formatStr(gpsInfo.getLocation().getLatitude()+"");
				TestInfoValue.latitude   = Double.parseDouble(latitude);
				TestInfoValue.longtitude =  Double.parseDouble(longitude);
				setPositon(longitude, latitude);//设置位置
				gpsInfo.releaseGps(IndoorTask.this, WalkCommonPara.OPEN_GPS_TYPE_INDOORTEST);
			}
			//sdcard状态改变
			if( intent.getAction().equals( WalkMessage.ACTION_SDCARD_STATUS ) ){
		        findView();
		        initial();
			}
		}
	};
 
	
	private String buildAddress = "";
	/** 
	 * 
	 * 监听spinner选择事件
	 */
	private OnItemSelectedListener itemSelectListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			buildName = buildingNames.get(position);
			if(!buildingList.isEmpty()){
				String buildmap = buildingList.get(position).getBuildMapPath();
				buildAddress = buildingList.get(position).getBuildAddress();
				LogUtil.i(tag, "----buildAddress="+buildAddress);
				//建筑有外观图
				if(buildmap != null){
					showImage(buildmap);
					buildTip.setVisibility(View.GONE);
					buildTip.setText("");
				}else{
					//建筑没有外观图，给出提示
					imageView.setImageDrawable( 
							 getResources().getDrawable( 
									 android.R.drawable.stat_notify_error ) );
					buildTip.setVisibility(View.VISIBLE);
					buildTip.setText(R.string.main_indoor_buildNomap);
				}
				LogUtil.i(tag,"selectOlditem:"+buildName);
				//如果GPS没打开
//				if(!checkOpengps.isChecked()){
					//如果建筑位置不同，或者建筑物名字不同
					if(position != item_postion_building||!buildName.equals(SaveIndoorData.selectBuildName)){
						SaveIndoorData.IsGpsSearched = false;
						latitude = longitude = defaultValue;
						setPositon(defaultValue, defaultValue);//清除经纬度信息
						/*if(!buildingList.isEmpty()){
							Toast.makeText(IndoorTask.this, 
									R.string.main_indoor_opengps, Toast.LENGTH_SHORT).show();//提示打开GPS
						}
						btnNext.setEnabled(false);*/
					}
					item_postion_building = position;
					LogUtil.i(tag, "-----positon:"+position+"###item_postion_building:"+item_postion_building);
//				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			
		}
	};
	
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    };
	
}
