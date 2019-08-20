package com.walktour.gui.about;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.about.GlsOptionsDBHelper.UserAccount;
import com.walktour.service.app.License.Client;

import java.util.ArrayList;
import java.util.List;




/**
 * License 登录界面
 * @author zhihui.lian
 * 功能:支持记住多个用户密码、自动匹配用户、登陆服务器
 */
public class GlsLoginActivity extends BasicActivity implements OnClickListener{



	private Button about_login_btu;																	//登陆按钮
	private EditText glsUserName;																	//用户名
	private EditText glsPassword;																	//密码
	private static final String  SERVER = "http://ericssongpa.dingli.com:8011/apiservice.asmx";				//服务器地址
	private static final int TIMEOUT = 18;															//超时时间
	private static final String TAG = "LicenseClientTag" ;
	
	private static final int INIT_FAIL = 0x00; 
	private static final int LOGIN_FAIL = 0x01; 
	private static final int API_INVALID= 0x02; 
	private static final int USER_ERR = 0x03; 
	private static final int FORMAT_ERR = 0x04; 
	private static final int NO_BACK = 	0x05; 
	private static final int NO_LICENSE = 0x06; 
	private static final int STATUS_CODE_FAIL = 0x07; 
	private static final int LOGIN_SUCCESS = 0x08; 
	private String getImei;
	
	private LinearLayout loading;
	private CheckBox rememberPassword;
	private Animation shakeAnimY;
	private Button more_user;
	private PopupWindow selectPopupWindow;
	
	private GlsOptionsDBHelper mOptionsDBHelper;  				//数据库操作类
	private List<String> mUserNames=new ArrayList<String>(); 
	private int mCount=0;
	private View layout_option;
	private UserOptionsAdapter mOptionsAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about_login_page); 
		mOptionsDBHelper=new GlsOptionsDBHelper(GlsLoginActivity.this);
		glsUserName = initEditText(R.id.gls_license_username);
//		glsUserName.setText("walktour@dinglicom.com");
		glsPassword = initEditText(R.id.gls_license_password);
		rememberPassword = (CheckBox) findViewById(R.id.gls_remember_password);
		initData();
		more_user=initButton(R.id.more_user);
		if (mCount > 0) {
			more_user.setVisibility(Button.VISIBLE);
			more_user.setOnClickListener(this);
		} else {
			more_user.setVisibility(Button.INVISIBLE);
		}
//		glsPassword.setText("RLAIT53");
		findView();
		getImei = MyPhoneState.getInstance().getIMEI(GlsLoginActivity.this);
	}
	
	
	/**
	 * 加载控件
	 */
	private void findView() {
		TextView title =  initTextView(R.id.title_txt);
		title.setText("GLS Management");
		ImageView pointer = initImageView(R.id.pointer);
		pointer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GlsLoginActivity.this.finish();
			}
		});
		about_login_btu = initButton(R.id.about_login_btu);
		about_login_btu.setOnClickListener(this);
		loading = (LinearLayout)findViewById(R.id.progressBar1);
		shakeAnimY= AnimationUtils.loadAnimation(GlsLoginActivity.this,R.anim.shake_y);
	}
	
	public void initData() 
	{
		mCount=restoreAccounts();
		UserAccount userAccount = restoreLastAccount();
		if (mCount>0&&userAccount!=null) {
			
			rememberPassword.setChecked(userAccount.getRembPwd());
			
			glsUserName.setText(userAccount.getUserName());
			glsPassword.setText(userAccount.getPassWord());
		}
		
	   	//PopupWindow浮动下拉框布局
		layout_option= (View)this.getLayoutInflater().inflate(R.layout.layout_options, null); 
        ListView listView = (ListView) layout_option.findViewById(R.id.layout_options_list); 
        
        //设置自定义Adapter
        mOptionsAdapter=new UserOptionsAdapter(GlsLoginActivity.this, mUserNames);
        mOptionsAdapter.setOnClicked(onItemClicked);
        
        listView.setAdapter(mOptionsAdapter); 

	}
	
	
	/**
	 * 恢复账号
	 * @return
	 */
	public int restoreAccounts() {
		mOptionsDBHelper.open();
		int count = mOptionsDBHelper.queryDataCount();
		Log.d("restoreAccounts", "count:" + count);
		if (count > 0) {
			UserAccount[] userAccounts = new UserAccount[count];
			userAccounts = mOptionsDBHelper.queryAllData();

			mUserNames.clear();

			for (UserAccount userAccount : userAccounts) {
				mUserNames.add(userAccount.getUserName());
			}
			mOptionsDBHelper.close();
		} else {

		}
		return count;
	}
	
	/**
	 * 删除保存的账号、单条删除
	 * @param username
	 */
	public void removeAccount(String username) {
		mOptionsDBHelper.open();
		mOptionsDBHelper.deleteOne(username);
		mOptionsDBHelper.close();
	}
    
    
    
	public UserAccount restoreLastAccount() {
		UserAccount userAccount = mOptionsDBHelper.new UserAccount();
		mOptionsDBHelper.open();
		userAccount = mOptionsDBHelper.queryLastAcc();
		mOptionsDBHelper.close();
		return userAccount;
	}
	
	
	public void storeSelectedAccount(String name) {
		mOptionsDBHelper.open();
		mOptionsDBHelper.updateState(name);
		mOptionsDBHelper.close();
	}
    
    
	/**
	 * 更新状态
	 * @param name
	 */
    public void updateState(String name) {
        UserAccount userAccount =mOptionsDBHelper.new UserAccount();
        mOptionsDBHelper.open();
        userAccount=mOptionsDBHelper.queryOne(name);
        mOptionsDBHelper.close();
        
        glsUserName.setText(userAccount.getUserName());
        glsPassword.setText(userAccount.getPassWord());
        
        rememberPassword.setChecked(userAccount.getRembPwd());
	}
    
    /**
     * 保存登陆信息
     */
	public void storeAccount() {
		UserAccount userAccount = mOptionsDBHelper.new UserAccount();
		userAccount.setUserName(glsUserName.getText().toString());
		userAccount.setPassWord(glsPassword.getText().toString());
		userAccount.setRembPwd(rememberPassword.isChecked());
		userAccount.setLastAcc(true);
		Log.i("D", "store the account info");
		mOptionsDBHelper.open();
		mOptionsDBHelper.updateState(glsUserName.getText().toString());
		if (mOptionsDBHelper.isOneExist(glsUserName.getText().toString())) {
			mOptionsDBHelper.updateOne(userAccount.getUserName(), userAccount);
			Log.i("D", "update a exist account info");
		} else {
			mOptionsDBHelper.insert(userAccount);
			Log.i("D", "insert a new account info");
		}
		mOptionsDBHelper.close();
	}
    
    
    /**
     * 打开popWindow
     * @param show_flag
     */

	@SuppressWarnings("deprecation")
	private void uploadOptionPop(boolean show_flag) {
		if (show_flag) {
			if (selectPopupWindow != null) {
				if (selectPopupWindow.isShowing()) {
					selectPopupWindow.dismiss();
				}
				selectPopupWindow = null;
			}
			selectPopupWindow = new PopupWindow(layout_option,glsUserName.getWidth(), LayoutParams.WRAP_CONTENT, true);
					
			selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());// 设置允许在外点击消失
			selectPopupWindow.showAsDropDown(glsUserName, 0, 0);
			selectPopupWindow.setAnimationStyle(R.style.PopupAnimation);
			selectPopupWindow.setFocusable(true);
			selectPopupWindow.setOutsideTouchable(true);
			selectPopupWindow.update();
		} else {
			if (selectPopupWindow != null) {
				selectPopupWindow.dismiss();
				selectPopupWindow.setFocusable(false);
			}

		}
	}
    
    
    
    /**
     * 点击监听
     */
	private UserOptionsAdapter.OnClicked onItemClicked = new UserOptionsAdapter.OnClicked() {

		@Override
		public void onItemSelected(int index) {
			// TODO Auto-generated method stub
			updateState(mUserNames.get(index));
			uploadOptionPop(false);
		}

		@Override
		public void onItemDelete(int index) {
			// TODO Auto-generated method stub
			if (mUserNames.size() > 0) {
				removeAccount(mUserNames.get(index));
				mUserNames.remove(index);
				// 刷新下拉列表
				mOptionsAdapter.notifyDataSetChanged();
			}
			uploadOptionPop(false);
			if (mUserNames.size() == 0) {
				Button btn = initButton(R.id.more_user);
				btn.setVisibility(Button.INVISIBLE);
			} else {

			}
		}
	};
    
    
    
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	}

	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case INIT_FAIL:
			case LOGIN_FAIL:
			case API_INVALID:
			case USER_ERR:
			case FORMAT_ERR:
			case NO_BACK:
			case NO_LICENSE:
				loading.setVisibility(View.GONE);
				glsUserName.startAnimation(shakeAnimY);
				glsPassword.startAnimation(shakeAnimY);
				showTip(msg.obj.toString());	
				break;	
			case LOGIN_SUCCESS:
				if (rememberPassword.isChecked()) {
					mOptionsDBHelper.open();
					if (!mOptionsDBHelper.isOneExist(glsUserName.getText()
							.toString())) {
						mUserNames.add(glsUserName.getText().toString());
						mOptionsAdapter.notifyDataSetChanged();
					}
					mOptionsDBHelper.close();
					storeAccount();
					Button btn = initButton(R.id.more_user);
					btn.setVisibility(Button.VISIBLE);
				} else {
					Log.i("D",
							"the rembpass checkbox is not checked, and the username is "
									+ glsUserName.getText().toString());
					mOptionsDBHelper.open();
					if (mOptionsDBHelper.isOneExist(glsUserName.getText()
							.toString())) {
						removeAccount(glsUserName.getText().toString());
						mOptionsDBHelper.open();
						mOptionsDBHelper.updateState(null);
					} else {

					}
					mOptionsDBHelper.close();
				}
				loading.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		};
	};
	
	
	
	
	
	/**
	 * 查询状态license状态码
	 */
	private int getStatusCode(){
		int statuscode = Client.Status(getImei, Environment.getExternalStorageDirectory().getPath() + "/Walktour/data/license_status.xml");
		Log.i("licenseClentTag", "login status code");
		return statuscode;
	}
	
	
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.about_login_btu:
			final Message msgMessage = new Message();
			final String userName = glsUserName.getText().toString().trim();
			final String passWord = glsPassword.getText().toString().trim();
			loading.setVisibility(View.VISIBLE);
			loading.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
				}
			});
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					int statuInitCode = Client.Init(SERVER, "", TIMEOUT);
					if (statuInitCode == 0){
						LogUtil.i(TAG, "Init Success");
					 }else{
						 msgMessage.what = INIT_FAIL;
						 msgMessage.obj = "Init fail";
						 handler.sendMessage(msgMessage);
					}
					switch (isLoginSucc()) {
					case 1:
						switch (getStatusCode()) {
						case 1:
							 msgMessage.what = LOGIN_SUCCESS;
							 handler.sendMessage(msgMessage);
							 Intent intent = new Intent(GlsLoginActivity.this, GlsMagDetailActivity.class);
							 intent.putExtra("username", userName);
							 intent.putExtra("password", passWord);
							 startActivity(intent);
							 overridePendingTransition(R.anim.open_next, R.anim.close_main);
							break;
						default:
							msgMessage.what = STATUS_CODE_FAIL;
							msgMessage.obj = "get status fail";
							handler.sendMessage(msgMessage);
							break;
						}
						break;
					case -1:
						 msgMessage.what = LOGIN_FAIL;
						 msgMessage.obj = "Login fail, please check!";
						 handler.sendMessage(msgMessage);
						break;
					case -10:
						msgMessage.what = API_INVALID;
						 msgMessage.obj = "API invalid";
						 handler.sendMessage(msgMessage);
						break;
					case -2:
						msgMessage.what = USER_ERR;
						 msgMessage.obj = "Failed user authentication, please check the user permission or password.";
						 handler.sendMessage(msgMessage);
						break;
					case -3:
						msgMessage.what =FORMAT_ERR;
						 msgMessage.obj = "Format err";
						 handler.sendMessage(msgMessage);
						break;
					case -21:
						msgMessage.what =NO_BACK;
						 msgMessage.obj = "Please check in current license first.";
						 handler.sendMessage(msgMessage);
						break;
						
					case -22:
						msgMessage.what =NO_LICENSE;
						 msgMessage.obj = "All licenses have been checked out.";
						 handler.sendMessage(msgMessage);
						break;
					default:
						break;
					}
				}
			}).start();
			
			break;
			
		case R.id.more_user:
			if (mCount > 0) {
				uploadOptionPop(true);
			}
			break;

		default:
			break;
		}	
	}
	
	
	/**
	 * 消息提示
	 * @param tipStr
	 */
	public void showTip(String tipStr) {
		Toast.makeText(GlsLoginActivity.this, tipStr, Toast.LENGTH_SHORT).show();
		
	}
	
	
	/**
	 * 初始化登陆服务器
	 * @return 状态码
	 */
	public int isLoginSucc(){
		int statuLoginCode = -1 ;
		String userName = glsUserName.getText().toString().trim();
		String passWord = glsPassword.getText().toString().trim();
		statuLoginCode = Client.Login(userName, passWord, Environment.getExternalStorageDirectory().getPath() + "/Walktour/data/license_login.xml");
		return statuLoginCode;
	}
	
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.open_main, R.anim.close_next);
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

