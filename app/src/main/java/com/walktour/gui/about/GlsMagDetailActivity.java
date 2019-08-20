package com.walktour.gui.about;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.FileReader;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.datepicker.WheelMain;
import com.walktour.framework.view.datepicker.WheelView;
import com.walktour.gui.Main;
import com.walktour.gui.R;
import com.walktour.model.LicenseInfoModel;
import com.walktour.model.LicenseModel;
import com.walktour.service.app.License.Client;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;




/**
 * GLS Management 界面
 * @author zhihui.lian
 */
@SuppressLint("SdCardPath")
public class GlsMagDetailActivity extends BasicActivity implements OnClickListener{
//	private RelativeLayout mag_Contract;
	private TextView currentjon;
	private TextView user_txt ;
	private TextView status_txt;
	private TextView user_expiration_date;
	private TextView imei_txt;
	private TextView checkout_date_txt;
	private TextView check_expiration_txt;
	private Button check_out_btn;
	private Button check_in_btn;
	private FileInputStream fileInputStream;
	private JSONObject obj;
	private LicenseInfoModel licenseInfoModel;
	
	private LicenseModel licenseModel;
	private TextView query_contract;
	private RelativeLayout gls_mag_query_contract;
	private RelativeLayout check_expiration_date_layout;
	private String getImei;
	
	private String choutDate;
	
	private ApplicationModel appModel = ApplicationModel.getInstance();
	private String userName;
	private String passWord;
	private LinearLayout loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about_gls_management); 
		readStatusJson();
		getIntentData();
		findView();
		getImei = MyPhoneState.getInstance().getIMEI(GlsMagDetailActivity.this);
	}
	
	
	/**
	 * 接收intent传递的数据
	 */
	private void getIntentData(){
		userName = getIntent().getExtras().getString("username");
		passWord = getIntent().getExtras().getString("password");
		
	}
	
	
	
	
	private void findView() {
		TextView title =  initTextView(R.id.title_txt);
		title.setText("GLS Management");
		ImageView pointer = initImageView(R.id.pointer);
		pointer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GlsMagDetailActivity.this.finish();
			}
		});
		
//		mag_Contract = (RelativeLayout)findViewById(R.id.gls_mag_Contract);
		currentjon = initTextView(R.id.currentjon_txt);
		
		user_txt =initTextView(R.id.user_txt);					//user
		user_txt.setText(userName);
		status_txt =initTextView(R.id.status_txt);				//status

		user_expiration_date = initTextView(R.id.user_expiration_date_txt); 				//user + expiration_date

		checkout_date_txt = initTextView(R.id.checkout_date_txt);

		imei_txt = initTextView(R.id.imei_txt);

		check_expiration_txt = initTextView(R.id.check_expiration_date_txt);
		
		status_txt.setText(licenseInfoModel.getStatus());
		user_expiration_date.setText(licenseInfoModel.getExpirationDate());
		checkout_date_txt.setText(licenseInfoModel.getCheckoutDate());
		imei_txt.setText(MyPhoneState.getInstance().getIMEI(GlsMagDetailActivity.this));
		currentjon.setText(licenseInfoModel.getContract().equals("null") ? "N/A":licenseInfoModel.getContract());

		query_contract = initTextView(R.id.query_contract);

		check_out_btn = initButton(R.id.check_out_btn);
		check_out_btn.setOnClickListener(this);
		check_in_btn = initButton(R.id.check_in_btn);
		check_in_btn.setOnClickListener(this);
		gls_mag_query_contract = (RelativeLayout)findViewById(R.id.gls_mag_query_contract);
		gls_mag_query_contract.setOnClickListener(this);

		check_expiration_date_layout = (RelativeLayout)findViewById(R.id.check_expiration_date_layout);
		check_expiration_date_layout.setOnClickListener(this);
		
		loading = (LinearLayout)findViewById(R.id.progressBar1);
	}
	
	private static class MyHandler extends Handler{
		private WeakReference<GlsMagDetailActivity> reference;
		public MyHandler(GlsMagDetailActivity activity){
			this.reference = new WeakReference<GlsMagDetailActivity>(activity);
		}
		public void handleMessage(android.os.Message msg) {
			GlsMagDetailActivity activity = this.reference.get();
			switch (msg.what) {
			case 1:
				activity.status_txt.setText(activity.licenseInfoModel.getStatus());
				activity.user_expiration_date.setText(activity.licenseInfoModel.getExpirationDate());
				activity.checkout_date_txt.setText(activity.licenseInfoModel.getCheckoutDate());
				activity.imei_txt.setText(MyPhoneState.getInstance().getIMEI(activity));
				activity.currentjon.setText(activity.licenseInfoModel.getContract().equals("null") ? "N/A":activity.licenseInfoModel.getContract());
				activity.loading.setVisibility(View.GONE);
				break;
				
			case 0x22:
			case 0x21:
			case 0x23:
			case 0x24:
				Toast.makeText(activity, msg.obj.toString(), Toast.LENGTH_SHORT).show();
				activity.loading.setVisibility(View.GONE);
				break;		
			case 0x25:
				Toast.makeText(activity, msg.obj.toString(), Toast.LENGTH_SHORT).show();
				activity.loading.setVisibility(View.GONE);
				break;		
			default:
				break;
			}
			
		}
	}
	
	private MyHandler handler = new MyHandler(this);
	
	
	@Override
	protected void onResume() {
		super.onResume();
		query_contract.setText(licenseModel == null ? " No set" : licenseModel.getContractname());
	}



	/**
	 * 查询本机license状态
	 */
//	private void getStatus(){
//		
//		final Message message = new Message();
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				int statuscode = Client.Status(getImei, "/sdcard/walktour/data/license_status.xml");
//				if(	statuscode == 1 ){
//					readStatusJson();
//					message.what = 1;
//					message.obj = licenseInfoModel;
//					handler.sendMessage(message);
//				}else{
//					LogUtil.i("licenseClentTag", "get status l");
//				}
//			}
//		}).start();
//	}
	
	/**
	 * 读取Json数据
	 */
	private void readStatusJson(){
		licenseInfoModel = new LicenseInfoModel();
		File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Walktour/data/license_status.xml");
		if(file.exists()){
			try {
				fileInputStream = new FileInputStream(file);
				byte [] buffer = new byte[fileInputStream.available()] ;
				fileInputStream.read(buffer);
				String json = new String(buffer,"utf-8"); 
				obj = new JSONObject(json);
				String status = obj.getString("status");
				String contractName = obj.getString("contractname");
//				String productName = obj.getString("productname");
				String expireDate = obj.getString("expiredate");
				String checkoutDate = obj.getString("checkoutdate");
				licenseInfoModel.setStatus(status);
				licenseInfoModel.setContract(contractName);
				licenseInfoModel.setExpirationDate(status.equals("checkin") ? expireDate : UtilsMethod.utc2LocalTime(expireDate,"yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd"));
				licenseInfoModel.setCheckoutDate(status.equals("checkin") ? checkoutDate :UtilsMethod.utc2LocalTime(checkoutDate, "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 弹出自定义时间选择器
	 * 
	 */
	
	@SuppressLint("InflateParams")
	private void showDateTimePicker(View v) {
		DisplayMetrics metric = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay() .getMetrics(metric);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.license_date_time_layout, null);
		TextView showDateTip = (TextView)view.findViewById(R.id.show_tip_massage);
		WheelView wv_year = (WheelView) view.findViewById(R.id.year);
		wv_year.setLabel("Year");
		WheelView wv_month = (WheelView) view.findViewById(R.id.month);
		wv_month.setLabel("Month");
		WheelView wv_day = (WheelView) view.findViewById(R.id.day);
		wv_day.setLabel("Day");
		showDateTip.setText(Html.fromHtml("<font color=white>Please set a date before contract expiration date </font>"+"<font color=#28aae2>"+UtilsMethod.utc2LocalTime(licenseModel.getExpiredate(), "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd")+"</font>"));
		final WheelMain main = new WheelMain(view);
		main.setTime(System.currentTimeMillis());
		new BasicDialog.Builder(GlsMagDetailActivity.this).setTitle("Select DateTime")
		.setIcon( R.drawable.pointer )
		.setView(main.showDateTimePicker())
		.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				choutDate = main.getTime();
				check_expiration_txt.setText(UtilsMethod.stringToDateShort(choutDate));
//				Toast.makeText(getApplicationContext(), main.getTime(), Toast.LENGTH_SHORT).show();
			}
		})
		.setNegativeButton(R.string.str_cancle).show();
		
	}
	
	
	
	/**
	 * check out申请license
	 */
	private void checkOut(){
		loading.setVisibility(View.VISIBLE);
		loading.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		final Message message = new Message();
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				String contract_detail = "W" + "\r\n" + licenseModel.getContractcode() + "\r\n" + getImei + "\r\n" +  UtilsMethod.utc2LocalTime(choutDate, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss") ;
				int checkOutCode = Client.Checkout( contract_detail, Environment.getExternalStorageDirectory().getPath() + "/Walktour/license/license.bin");
				switch (checkOutCode) {
				case 1:
					message.what = 0x22;
					message.obj = "Check Out Success!";
					handler.sendMessage(message);
//					getStatus();
					/*FileOperater operater = new FileOperater();
					operater.overCopy(Environment.getExternalStorageDirectory().getPath() + "/Walktour/license/license.bin",getFilesDir()+"/license.bin" );*/
					FileReader fileRead = new FileReader();
					String autoStr = fileRead.getFileText(Environment.getExternalStorageDirectory().getPath() + "/Walktour/license/license.bin");
					ConfigRoutine.getInstance().setAutoValue(getApplicationContext(), autoStr);
					
					appModel.setEnvironmentInit(false);
					appModel.setCheckPowerSuccess( false );
					finish();
					startActivity(new Intent(GlsMagDetailActivity.this,Main.class));
					break;
				default:
					message.what = 0x24;
					message.obj = "Check Out Fail!";
					handler.sendMessage(message);
					break;
				}
			}
		}).start();
	}
	
	
	
	
	/**
	 * check in 归还license
	 */
	private void checkIn(){
		loading.setVisibility(View.VISIBLE);
		loading.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				int checkOutCode = Client.Checkin(getImei);
				Message message = handler.obtainMessage();;
				Boolean isExit =true;
				while (isExit) {
					switch (checkOutCode) {
					case 1:
						message.what = 0x21;
						message.obj = "Check In Success!";
						handler.sendMessage(message);
						appModel.setCheckPowerSuccess( false );
						LogUtil.w("GlsMmgDetial", "============activeTime 3:0============");
						ApplicationModel.getInstance().setActiveTime(0);
						File outFile = new File( getFilesDir()+"/license.bin" );
				        if( outFile.exists() ){
				            outFile.delete();
				        }
						finish();
						startActivity(new Intent(new Intent(GlsMagDetailActivity.this,AboutActivity.class)));
						break;
					default:
						message.what = 0x23;
						message.obj = "Check In Fail!";
						handler.sendMessage(message);
						break;
					}
					isExit = false;
				}
			}
		}).start();
	}
	
	
	/**
	 * 初始化登陆服务器
	 * @return 状态码
	 */
	public int isLoginSucc(){
		int statuLoginCode = -1 ;
		statuLoginCode = Client.Login(userName, passWord, Environment.getExternalStorageDirectory().getPath() + "/Walktour/data/license_login.xml");
		return statuLoginCode;
	}
	
	
	
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gls_mag_Contract:
			
			break;
			
		case R.id.check_out_btn:
			if(licenseInfoModel	!= null && licenseInfoModel.getStatus().equals("checkout")){
				Toast.makeText(getApplicationContext(), "Please check in current license first.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(licenseModel!=null){
				if(choutDate != null){
					if((compare_date(choutDate,licenseModel.getExpiredate()) != 1)){
						checkOut();
					}else{
						Toast.makeText(getApplicationContext(), "Please set A valid date.", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(getApplicationContext(), "Please set the expiration date.", Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(getApplicationContext(), "Please choose a contract.", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.check_in_btn:	
			if(licenseInfoModel	!= null && licenseInfoModel.getStatus().equals("checkin")){
				Toast.makeText(getApplicationContext(), "No available license to check in.", Toast.LENGTH_SHORT).show();
				return;
			}
			checkIn();
			break;
			
		case R.id.gls_mag_query_contract:	
			startActivityForResult(new Intent(GlsMagDetailActivity.this, GlsExpandableActivity.class), 0);
			break;
			
		case R.id.check_expiration_date_layout:
			if (licenseModel != null){
				showDateTimePicker(v);
			}else{
				Toast.makeText(getApplicationContext(), "Please choose a contract First.", Toast.LENGTH_SHORT).show();
			}
			break;
			
		default:
			break;
		}	
	}
	
	
	
	public int compare_date(String DATE1, String DATE2) {
        
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",Locale.getDefault());
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                System.out.println("dt1 在dt2前");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                System.out.println("dt1在dt2后");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode){
			case RESULT_OK:
			if (data != null){
				licenseModel = (LicenseModel)data.getSerializableExtra("isCheckedModel");
				}
				break;
			default:
				
				break;
		}
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) { 
			if(loading.getVisibility() == View.VISIBLE){
				loading.setVisibility(View.GONE);
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	
	}
	
}

