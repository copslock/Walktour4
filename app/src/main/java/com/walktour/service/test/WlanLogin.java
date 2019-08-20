package com.walktour.service.test;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.http.SslError;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.RcuEventCommand;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.FileReader;
import com.walktour.control.bean.MyFileWriter;
import com.walktour.control.instance.LoginManager;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;

import java.io.File;
import java.util.List;
import java.util.Locale;

/*
 * Wlan状态界面
 * time:2011.8.7 
 */
public class WlanLogin extends BasicActivity {

    private final String tag = "WifiStatus";
    private final int NOSIGNAL = 999;
    private final String TEMP_FILE = "wlan_success_url.txt";//wlan登录成功时的url
    //	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());//事件前时间格式
    //View
    private ImageView imgSignal;
    private ImageView imgStatus;
    private TextView txtTitle;
    private TextView txtRssi;
    private LinearLayout linearLayoutStatus;
    private LinearLayout linearLayoutTime;
    private TextView txtTime;
    private TextView txtInfo;
    private Button btnOffline;
    private Button btnConnect;
    //	private LinearLayout linerLayoutLinks;
//	private ArrayList<Button> buttonList;
    private LinearLayout linerLayoutProgress;
    private ImageView[] progressDots;
    private int[] progress;

    //WebView
    private WebView wv;
    private WebView wv1;
    private WebSettings ws;
    private WebSettings ws1;
    private TextView tip;
    //	private ProgressDialog progressDialog  ;
    //What
    private final int DIMISS_PROGRESS = -1;
    private final int MSG_TIME = 0;
    private final int MSG_LOGIN_START = 1;
    private final int MSG_LOGIN_SUCCESS = 2;
    private final int MSG_LOGIN_FAIL = 3;
    private final int OPEN_WLAN = 4;
    private final int SCAN_CMCC = 5;
    private final int SCAN_CMCC_FAIL = 6;
    private final int GET_IP = 7;
    private final int GET_IP_SUCCESS = 8;
    private final int GET_IP_FAIL = 9;
    private final int LOGOUT = 10;
    private final int LOGOUT_FAIL = 11;
    private final int LOGIN_CANCLE = 12;
    private final int LOGOUT_SUCCESS = 13;
    private final int REFRESH_WIFI = 14;
    private final int REFRESH_TIME = 15;
    private final int GET_APPS = 16;
    private final int START = 17;
    private final int PROGRESS = 18;

    private String user = "";
    private String pass = "";
//	private int timeOut = 120;

    private boolean isScanning = false;
    private boolean isLogging = false;
    private boolean hasCancleAlert = false;
    //	private boolean exitAfterOffLine = false;
    private boolean isTimeout = false;

    private LoginManager loginManager = LoginManager.getInstance();
    private WifiManager wifiManager;
    //private App app ;
//	private ArrayList<ApplicationInfo>  mInfoList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //app = App.getInstance( this );

        //获取登录帐号信息
        getExtras();

        //注册广播
        //regeditBroadcast();

        //生成界面
        findStatusView();
        findProgressView();
        findWebView();
        changeStatus();

        //启动wifi扫描
        new Thread(new WifiScanner()).start();
        new Thread(new Refresher()).start();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        //App.getInstance( this ).cancleStatusNotification();
    }

    @Override
    public void onPause() {
        super.onPause();
        //App.getInstance( this ).showStatusNotification();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //App.getInstance( this ).cancleStatusNotification();
        //反注册广播
        //this.unregisterReceiver(mBroadcastReceiver);

        isTimeout = true;
        //停止当次测试
        Intent intent = new Intent();
        intent.setAction(WlanTest.ACTION_TEST_STOP);
        sendBroadcast(intent);
    }

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void getExtras() {
        try {
            this.user = this.getIntent().getExtras().getString(WlanTest.KEY_USER);
            this.pass = this.getIntent().getExtras().getString(WlanTest.KEY_PASS);
//        	this.timeOut = this.getIntent().getExtras().getInt( WlanTest.KEY_TIMEOUT, 120);
        } catch (Exception e) {
            finish();
        }
    }

    /**
     * 生成title
     */
    private void findStatusView() {
        setContentView(R.layout.wlan_login);
        imgSignal = initImageView(R.id.ImageViewWifi);
        imgSignal.setImageResource(R.drawable.wifi_level);
        imgStatus = initImageView(R.id.ImageViewInfo);
        txtTitle = initTextView(R.id.TextViewTitle);
        txtRssi = initTextView(R.id.TextViewRssi);
        txtTime = initTextView(R.id.TextViewTime);
        txtInfo = initTextView(R.id.TextViewInfo);
        linearLayoutStatus = initLinearLayout(R.id.LinearLayoutStatus);
        linearLayoutTime = initLinearLayout(R.id.LinearLayoutTime);
        btnOffline = initButton(R.id.btn_offline);
        btnConnect = initButton(R.id.btn_connect);

        btnOffline.setOnClickListener(btn_listener);
        btnConnect.setOnClickListener(btn_listener);

        //new Thread( new ApplicaiontReader( false ) ).start();
    }

    private void findProgressView() {
        linerLayoutProgress = initLinearLayout(R.id.LinerLayoutProgress);
        linerLayoutProgress.setVisibility(View.INVISIBLE);
        ImageView dot1 = initImageView(R.id.ImageViewDot1);
        ImageView dot2 = initImageView(R.id.ImageViewDot2);
        ImageView dot3 = initImageView(R.id.ImageViewDot3);
        ImageView dot4 = initImageView(R.id.ImageViewDot4);
        ImageView dot5 = initImageView(R.id.ImageViewDot5);
        ImageView dot6 = initImageView(R.id.ImageViewDot6);
        ImageView dot7 = initImageView(R.id.ImageViewDot7);
        ImageView dot8 = initImageView(R.id.ImageViewDot8);

        progressDots = new ImageView[]{dot1, dot2, dot3, dot4, dot5, dot6, dot7, dot8,};
        progress = new int[]{1, 1, 1, 1, 1, 1, 1, 1};
        for (int i = 0; i < progressDots.length; i++) {
            progressDots[i].setImageResource(R.drawable.dot_level);
            progressDots[i].setImageLevel(progress[i]);
        }
    }
    
    
/*    private void findLinks(ArrayList<ApplicationInfo>  infoList){
        mInfoList = infoList;
    	linerLayoutLinks = initLinearLayout( R.id.LinearLayoutLinks );
    	
    	buttonList = new ArrayList<Button>();
    	buttonList.clear();
    	
    	//已经添加的快捷方式
    	linerLayoutLinks.removeAllViews();
    	linerLayoutLinks.setGravity( Gravity.CENTER_VERTICAL );
    	
    	for( int i=0;i<infoList.size();i++){
    		ApplicationInfo info = infoList.get(i);
    		Button button = new Button(this);
    		button.setBackgroundResource( R.drawable.btn2 );
    		int w = button.getBackground().getIntrinsicWidth();
        	int h = button.getBackground().getIntrinsicHeight();
        	int paddingVer = h / 10;
        	int paddingHor = w / 10;
        	
    		button.setLayoutParams( new LayoutParams(w,h) );
    		Drawable drawable = info.getIcon();
    		int width = h*8/10;//取按钮的0.8
    		float scale =  drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight() ;
    		int height =  (int) (width * scale);
    		drawable = app.zoomDrawable( drawable,width,height );
    		button.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
    		button.setPadding(paddingHor, paddingVer, paddingHor, paddingVer);
    		button.setText( info.getTitle() );
    		button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
    		button.setTag( info.getPackageName() );
    		button.setOnClickListener( linkListener );
    		button.setOnLongClickListener( linkLongListener );
    		linerLayoutLinks.addView(button);
    		buttonList.add( button );
    	}
    	
    	//最少显示6个图标
    	do{
    		Button button = new Button(this);
    		button.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		button.setBackgroundResource( R.drawable.btn_add );
    		int w = button.getBackground().getIntrinsicWidth();
        	int h = button.getBackground().getIntrinsicHeight();
        	int paddingVer = h / 10;
        	int paddingHor = w / 10;
        	button.setPadding(paddingHor, paddingVer, paddingHor, paddingVer);
    		button.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
    		button.setText( R.string.status_link );
    		button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
    		button.setTag( "add" );
    		button.setOnClickListener( linkListener );
    		linerLayoutLinks.addView(button);
    		buttonList.add( button );
    	}while( linerLayoutLinks.getChildCount()<6 );
    	
    }*/
    
/*    private void showAppListDialog(){
    	
    	LayoutInflater inflater = LayoutInflater.from(Status.this);
    	View view = inflater.inflate(R.layout.listview, null);
    	ListView list = (ListView)view.findViewById(R.id.list);
    	
    	//生成弹出窗口
    	final AlertDialog dialog = new AlertDialog.Builder(Status.this)
		.setTitle( R.string.status_add_link) 
		.setView( view )
		.setNegativeButton( R.string.str_cancle, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		}).create();
		
		ArrayList<HashMap<String, Object>> listdata = new ArrayList<HashMap<String,Object>>();;
		final ArrayList<ApplicationInfo> appList = app.getAllApplicationInfo();
		
		//添加程序快捷方式
		for( int i =0;i<appList.size();i++){
			ApplicationInfo app = appList.get(i);
			Drawable drawable = app.getIcon();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemIcon", drawable );
			map.put("ItemText", app.getTitle());
			listdata.add( map );
		}
		
		MySimpleAdapter adapter = new MySimpleAdapter(
				Status.this, listdata, 
				R.layout.listview_item, 
				new String[]{"ItemIcon","ItemText"},
				new int[]{R.id.ItemIcon,R.id.ItemText}
		);
		list.setAdapter(adapter);
		list.setOnItemClickListener( 
				new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Database.getInstance( Status.this ).addLinkPackageName(
								appList.get(position).getPackageName() 
						);
						dialog.dismiss();
						new Thread( new ApplicaiontReader( false ) ).start();
					}
				}
		);
		dialog.show();
    }*/

    private void refreshTime() {
        long online = (System.currentTimeMillis() - loginManager.getLoginTime()) / 1000;
        int hour = (int) online / 3600;
        int minute = (int) (online / 60) % 60;
        int second = (int) online % 60;
        String time = (hour < 10 ? ("0" + hour) : hour) + ":"
                + (minute < 10 ? ("0" + minute) : minute) + ":"
                + (second < 10 ? ("0" + second) : second);
        txtTime.setText(time);
    }

    /**
     * 改变状态显示
     */
    private void changeStatus() {

        boolean hasLogin = loginManager.isHasLogin();
        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            if (loginManager.isHasCMCC()) {
                if (loginManager.isHasConnectToCMCC()) {
                    if (loginManager.isHasIP()) {
                        imgStatus.setBackgroundResource(hasLogin ? R.drawable.status_success : R.drawable.status_info);
                        txtTitle.setTextColor(hasLogin ? getResources().getColor(R.color.black)
                                : getResources().getColor(R.color.light_yellow)
                        );
                        txtTime.setVisibility(loginManager.isHasLogin() ? View.VISIBLE : View.GONE);
                        txtTitle.setText(hasLogin ? getString(R.string.status_title_on)
                                : getString(R.string.status_title_off));
                        txtInfo.setText(hasLogin ? getString(R.string.status_infomation)
                                : getString(R.string.status_cmcc_hasIP)
                        );
                        linearLayoutStatus.setVisibility(hasLogin ? View.GONE : View.VISIBLE);
                        linearLayoutTime.setVisibility(hasLogin ? View.VISIBLE : View.GONE);
                        btnOffline.setVisibility(hasLogin ? View.VISIBLE : View.GONE);
                        btnConnect.setVisibility(hasLogin ? View.GONE : View.VISIBLE);
                    } else {

                        linearLayoutTime.setVisibility(View.GONE);
                        linearLayoutStatus.setVisibility(View.VISIBLE);
                        btnOffline.setVisibility(View.GONE);
                        btnConnect.setVisibility(View.VISIBLE);
                        imgStatus.setBackgroundResource(R.drawable.status_info);
                        txtTitle.setTextColor(getResources().getColor(R.color.limpid_yellow));
                        txtTitle.setText(getString(R.string.status_cmcc_noIP));
                        txtInfo.setText(hasLogin ? getString(R.string.status_out_when_online)
                                : getString(R.string.status_cmcc_hasNoIP)
                        );
                    }
                } else {
                    linearLayoutTime.setVisibility(View.GONE);
                    linearLayoutStatus.setVisibility(View.VISIBLE);
                    btnOffline.setVisibility(View.GONE);
                    btnConnect.setVisibility(View.VISIBLE);
                    imgStatus.setBackgroundResource(R.drawable.status_info);
                    txtTitle.setTextColor(getResources().getColor(R.color.limpid_yellow));
                    txtTitle.setText(getString(R.string.status_cmcc_not_connected));
                    txtInfo.setText(hasLogin ? getString(R.string.status_out_when_online)
                            : getString(R.string.status_cmcc_not_connected_tip)

                    );
                }
            } else {
                linearLayoutTime.setVisibility(View.GONE);
                linearLayoutStatus.setVisibility(View.VISIBLE);
                btnOffline.setVisibility(View.GONE);
                btnConnect.setVisibility(View.VISIBLE);
                imgStatus.setBackgroundResource(R.drawable.status_alarm);
                txtTitle.setTextColor(getResources().getColor(R.color.limpid_yellow));
                txtTitle.setText(getString(R.string.status_wifi_cmcc_outof));
                txtInfo.setText(hasLogin ? getString(R.string.status_out_when_online)
                        : getString(R.string.status_outofservice)
                );
            }
        } else {
            linearLayoutTime.setVisibility(View.GONE);
            linearLayoutStatus.setVisibility(View.VISIBLE);
            btnOffline.setVisibility(View.GONE);
            btnConnect.setVisibility(View.VISIBLE);
            imgStatus.setBackgroundResource(R.drawable.status_alarm);
            txtTitle.setTextColor(getResources().getColor(R.color.limpid_red));
            txtTitle.setText(getString(R.string.status_wifi_off));
            txtInfo.setText(hasLogin ? getString(R.string.status_out_when_online)
                    : getString(R.string.status_wifi_off_tip)
            );
        }

        if (!(isScanning || isLogging)) {
            btnConnect.setText(R.string.login);
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("deprecation")
    private void findWebView() {
        wv = (WebView) this.findViewById(R.id.WebView);
        wv1 = (WebView) this.findViewById(R.id.WebView1);
        tip = (TextView) this.findViewById(R.id.TextViewTip);
        ws = wv.getSettings();
        ws.setAllowFileAccess(true);//设置允许访问文件数据
        ws.setJavaScriptEnabled(true);//设置支持javascript脚本
        ws.setBuiltInZoomControls(true);//设置支持缩放
        //ws.setCacheMode( WebSettings.LOAD_NO_CACHE );
        ws.setSavePassword(false);

        ws1 = wv1.getSettings();
        ws1.setAllowFileAccess(true);//设置允许访问文件数据
        ws1.setJavaScriptEnabled(true);//设置支持javascript脚本
        ws1.setBuiltInZoomControls(true);//设置支持缩放
        //ws1.setCacheMode( WebSettings.LOAD_NO_CACHE );
        ws1.setSavePassword(false);

        wv.setWebViewClient(webViewClient);
        wv.setWebChromeClient(webChromeClient);

        wv1.setWebViewClient(webViewClient1);
        wv1.setWebChromeClient(webChromeClient);
    }

    @SuppressWarnings("deprecation")
    private void loadPotralPage() {
        //wv.loadUrl("http://221.176.1.140/wlan/index.php?wlanacname=1102.0010.100.00&wlanuserip=117.130.225.87&ssid=CMCC&vlan=529");
        //启动一个常用网站,让页面自动跳转到CMCC登录界面
        loginManager.setHasLoadPage(false);
        wv.clearHistory();
        wv.stopLoading();
        wv.clearView();
        wv1.clearHistory();
        wv1.clearView();
        wv1.stopLoading();
        wv.loadUrl("http://www.baidu.com");
        //打开登录主页请求(加载任意网页)
        sendResultEvent(RcuEventCommand.WLAN_OPEN_PORTAL_PAGE_START);
    }

    WebChromeClient webChromeClient = new WebChromeClient() {
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            //LogUtil.w("Webview","---message:"+message);
            result.confirm();
     		/*final String alertMsg = message;
     		try{
     			
     			if( message.contains("下线成功" ) || message.contains("记住" ) ){
     				result.confirm();
     			}else if( message.contains("下线确认") && exitAfterOffLine ){
     				exitAfterOffLine = false;
     				result.confirm();
     				exitApp();
     			}
     			
     			else{
     				new AlertDialog.Builder( WlanLogin.this )
     				.setTitle("提示")
     				.setMessage( message )
     				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
     					@Override
     					public void onClick(DialogInterface dialog, int which) {
     						//如有不成功的提示,重置登录页面(打开任意网页)
     						result.confirm();
     						if( alertMsg.contains("认证失败") || alertMsg.contains("密码错误") || alertMsg.contains("认证失败") ){
     							 Intent i = new Intent(Status.this,Manual2.class);
     		     				 i.putExtra( Manual2.KEY_CHANGING_ACOUNT, true );
     		     				 i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
     		     				 myFinish( i );
     						}
     					}
     				})
     				.show();
     			}
     		}catch(Exception e){
     			
     		}*/
            return true;
        }

        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            hasCancleAlert = false;
            //Toast.makeText(Status.this,"onJsConfirm", Toast.LENGTH_SHORT).show();
            result.confirm();
     		/*try{
     			if( message.contains("记住" ) ){
     				result.confirm();
     			}else{
     				final AlertDialog.Builder builder = new AlertDialog.Builder(view
     						.getContext());
     				builder.setTitle("提示")
     				.setMessage(message).setPositiveButton("确定",
     						new OnClickListener() {
     					@Override
     					public void onClick(DialogInterface dialog,
     							int which) {
     						result.confirm();
     					}
     				}).setNeutralButton("取消", new OnClickListener() {
     					@Override
     					public void onClick(DialogInterface dialog, int which) {
     						hasCancleAlert = true;
     						result.cancel();
     					}
     				});
     				builder.setOnCancelListener(new OnCancelListener() {
     					@Override
     					public void onCancel(DialogInterface dialog) {
     						hasCancleAlert = true;
     						result.cancel();
     					}
     				});
     				builder.create();
     				builder.show();
     			}
     		}catch(Exception e){
     			
     		}*/
            return true;
        }

        @SuppressLint("InflateParams")
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
            LayoutInflater inflater = LayoutInflater.from(WlanLogin.this);
            final View v = inflater.inflate(R.layout.prom_dialog, null);
            //设置 TextView对应网页中的提示信息
            ((TextView) v.findViewById(R.id.TextView_PROM)).setText(message);
            //设置EditText对应网页中的输入框
            ((EditText) v.findViewById(R.id.EditText_PROM)).setText(defaultValue);
            Builder builder = new Builder(WlanLogin.this);
            builder.setTitle("带输入的对话框 ");
            builder.setView(v);
            builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String value = ((EditText) v.findViewById(R.id.EditText_PROM)).getText().toString();
                    result.confirm(value);
                }

            });
            builder.setNegativeButton(android.R.string.cancel, new AlertDialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }

            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    result.cancel();
                }

            });
            builder.create();
            builder.show();
            return true;
        }

        //设置网页加载的进度条
        public void onProgressChanged(WebView view, int newProgress) {
            WlanLogin.this.getWindow().setFeatureInt(Window.FEATURE_PROGRESS, newProgress * 100);
            super.onProgressChanged(view, newProgress);
        }

        //设置应用程序的标题
        public void onReceivedTitle(WebView view, String title) {
            //Login.this.setTitle(title);
            super.onReceivedTitle(view, title);
        }
    };

    WebViewClient webViewClient = new WebViewClient() {
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            //handler.cancel(); 默认的处理方式，WebView变成空白页
            //handler.process();//接受证书
            handler.proceed();
            //handleMessage(Message msg); 其他处理
        }

        public void onLoadResource(WebView view, String url) {
            tip.setText(tip.getText() + "\n" + "webview Load:" + url + "\n");
            if (url.contains("/input.php")) {
                //为了能通过加载javascript来提交表单,把子页面放到webview1中
                wv1.loadUrl(url);

                //暂时把此页面当成是登录页面第一个数据包到达
                sendResultEvent(RcuEventCommand.WLAN_OPEN_PORTAL_PAGE_FIRST_DATA);

                // 把登录页面添加到临时记录
                //Database.getInstance( Status.this ).addTempValue( TEMP_TAG, url );
                final String strUrl = url;
                Thread writer = new Thread() {
                    public void run() {
     						/*临时目录*/
                        File tempFileDir = new File(getFilesDir() + "/temp");
                        if (!tempFileDir.isDirectory()) {
                            tempFileDir.mkdir();
                        }
                        MyFileWriter.write(
                                WlanLogin.this.getFilesDir() + "/temp/" + TEMP_FILE,
                                strUrl);
                    }
                };
                writer.start();

            }

            //下线动作
            else if (url.contains("do_logout.php") || url.contains("logout_success.php")) {
                if (loginManager.isHasLogin()) {
                    loginManager.setHasLogin(false);
                    Toast.makeText(WlanLogin.this, "Wlan下线成功", Toast.LENGTH_LONG).show();
                    changeStatus();
                    //wv.loadUrl("http://www.baidu.com");
                }
            }

            //如果百度wap页面能自动加载,说明之前未成功下线
            if (url.contains("baidu.com")) {
                //String tempUrl = Database.getInstance(Status.this).getTempValue( TEMP_TAG);
                // 读取上次登录成功的url
                String tempUrl = new FileReader().getFileText(
                        getFilesDir() + "/temp/" + TEMP_FILE);
                if (tempUrl != null) {
                    wv.loadUrl(tempUrl);
                }
            }
        }

        /**页面加载完成时*/
        public void onPageFinished(WebView view, String url) {
            LogUtil.w("webview", "---onPageFinished:" + url);
            tip.setText(tip.getText() + "\n" + "webview Finish:" + url + "\n");
            //加载登录成功的状态页面成功后下线
            if (url.contains("/user_status.php")) {
                new Thread(new ThreadLogout()).start();
            }
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            //LogUtil.w("webview","---onReceivedError:"+failingUrl);
            tip.setText("Error:" + description + "\n" + failingUrl);
        }

    };

    WebViewClient webViewClient1 = new WebViewClient() {

        public void onLoadResource(WebView view, String url) {
            tip.setText(tip.getText() + "\n" + "webview1 Load:" + url + "\n");

            //登录成功
            if (url.contains("/user_status.php")) {
                mHandler.obtainMessage(DIMISS_PROGRESS).sendToTarget();
                //修改状态
                loginManager.setHasLogin(true);
                loginManager.setLoginTime(System.currentTimeMillis());
                changeStatus();
                //wv加载状态成功状态页面
                wv.loadUrl(url);
                //用户登录成功
                sendResultEvent(RcuEventCommand.WLAN_LOGIN_SUCCESS);
            }

        }

        /**页面加载完成时*/
        public void onPageFinished(WebView view, String url) {
            //LogUtil.w("webview1","---onPageFinished:"+url);
            tip.setText(tip.getText() + "\n" + "webview1 Finish:" + url + "\n");

            if (url.contains("/input.php")) {
                loginManager.setHasLoadPage(true);
                //登录页面加载成功
                sendResultEvent(RcuEventCommand.WLAN_OPEN_PORTAL_PAGE_SUCCESS);
            }

        }

        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            //handler.cancel(); 默认的处理方式，WebView变成空白页
            //handler.process();//接受证书
            handler.proceed();
            //handleMessage(Message msg); 其他处理
        }

    };
    
/*      private class ApplicaiontReader implements Runnable{
    	  
    	  private boolean readFromSystem = false;
    	  public ApplicaiontReader(boolean readFromSys){
    		  readFromSystem = readFromSys;
    	  }
    	  
			@Override
			public void run() {
				ArrayList<ApplicationInfo>  infoList = 
						App.getInstance( Status.this ).getLinkApplications( readFromSystem );
				mHandler.obtainMessage( GET_APPS , infoList).sendToTarget();
			}
      }*/

    /**
     * 实现登录动画的线程
     */
    private class ProgressBar implements Runnable {

        @Override
        public void run() {
            int highLight = -1;
            while (linerLayoutProgress.getVisibility() == View.VISIBLE) {
                for (int i = 0; i < progress.length; i++) {
                    if (i == highLight) {
                        progress[i] = 3;
                    } else if (i == highLight + 1 || i == highLight - 1) {
                        progress[i] = 2;
                    } else {
                        progress[i] = 1;
                    }
                }

                mHandler.obtainMessage(PROGRESS).sendToTarget();
                highLight++;
                if (highLight == 9) {
                    highLight = -1;
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 刷新信号和时间
     */
    private class Refresher implements Runnable {
        @Override
        public void run() {

            while (!Thread.currentThread().isInterrupted()) {

                int level = 0;
                int rssi = 0;
                int rate = 0;

                List<ScanResult> scanResultList = wifiManager.getScanResults();
                if (scanResultList != null) {
                    for (int j = 0; j < scanResultList.size(); j++) {
                        ScanResult result = scanResultList.get(j);
                        if (result.SSID.toUpperCase(Locale.getDefault()).equals(LoginManager.WLAN_NAME)) {
                            level = result.level;
                            break;
                        }
                    }
                }

                WifiInfo currentWifi = wifiManager.getConnectionInfo();
                if (currentWifi != null) {
                    if (currentWifi.getSSID() != null) {
                        if (currentWifi.getSSID().toUpperCase(Locale.getDefault()).equals(LoginManager.WLAN_NAME)) {
                            rssi = currentWifi.getRssi();
                            rate = currentWifi.getLinkSpeed();
                        }
                    }
                }

                //刷新wifi图标
                mHandler.obtainMessage(REFRESH_WIFI,
                        level,
                        rssi,
                        String.valueOf(rate)
                ).sendToTarget();

                //刷新时间
                if (loginManager.isHasLogin()) {
                    if (loginManager.isHasIP()) {
                        mHandler.obtainMessage(REFRESH_TIME).sendToTarget();
                    }
                }

                try {
                    Thread.sleep(1 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 打开WLAN、扫描CMCC热点、关联热点、获取IP,更新Login
     */
    private class WifiScanner implements Runnable {
        @Override
        public void run() {

            if (isScanning) {
                return;
            }

            mHandler.obtainMessage(START).sendToTarget();
            isScanning = true;
            isTimeout = false;

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            //尝试打开wifi
            if (!wifiManager.isWifiEnabled() && !isTimeout) {
                wifiManager.setWifiEnabled(true);
                //提示用户正在打开CMCC
                mHandler.obtainMessage(OPEN_WLAN).sendToTarget();
            }
            while (!wifiManager.isWifiEnabled() && !isTimeout) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //提示用户正在搜索CMCC
            if (isTimeout) {
                mHandler.obtainMessage(LOGIN_CANCLE).sendToTarget();
            } else {
                mHandler.obtainMessage(SCAN_CMCC).sendToTarget();
                wifiManager.startScan();
            }

            //判断是否有CMCC热点
            boolean hasCMCC = false;
            for (int i = 0; i < 3 && !isTimeout; i++) {
                List<ScanResult> scanResultList = wifiManager.getScanResults();
                if (scanResultList != null) {
                    for (int j = 0; j < scanResultList.size(); j++) {
                        ScanResult result = scanResultList.get(j);
                        //LogUtil.w(tag, "---"+result.SSID );
                        if (result.SSID.toUpperCase(Locale.getDefault()).equals(LoginManager.WLAN_NAME)) {
                            //wifiManager.
                            hasCMCC = true;
                            break;
                        }
                    }
                }
                if (hasCMCC) {
                    break;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            LoginManager.getInstance().setHasCMCC(hasCMCC);


            //发送结果
            if (LoginManager.getInstance().isHasCMCC()) {//如果有CMCC热点
                try {
                    //断开不是CMCC的热点
                    WifiInfo current = wifiManager.getConnectionInfo();
                    if (!current.getSSID().toUpperCase(Locale.getDefault()).equals(LoginManager.WLAN_NAME)) {
                        wifiManager.disableNetwork(current.getNetworkId());
                        wifiManager.disconnect();
                        //关联到CMCC
                        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                        for (WifiConfiguration x : list) {
                            if (x.SSID.toUpperCase(Locale.getDefault()).equals(LoginManager.WLAN_NAME)) {
                                wifiManager.enableNetwork(x.networkId, true);
                                break;
                            }
                        }
                        wifiManager.reconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                //等待IP地址获取成功
                mHandler.obtainMessage(GET_IP).sendToTarget();
                boolean hasIP = false;
                for (int i = 0; i < 60 && !isTimeout; i++) {
                    WifiInfo info = wifiManager.getConnectionInfo();
                    int ip = info.getIpAddress();
                    LogUtil.w(tag, "---ip:" + ip);
                    if (ip != 0) {
                        hasIP = true;
                        LoginManager.getInstance().setHasConnectToCMCC(true);
                        LoginManager.getInstance().setHasIP(true);
                        LoginManager.getInstance().setIp(ip);
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (isTimeout) {
                    mHandler.obtainMessage(LOGIN_CANCLE).sendToTarget();
                } else {
                    if (hasIP) {
                        mHandler.obtainMessage(GET_IP_SUCCESS).sendToTarget();
                    } else {
                        mHandler.obtainMessage(GET_IP_FAIL).sendToTarget();
                    }
                }
            } else {
                if (isTimeout) {
                    mHandler.obtainMessage(LOGIN_CANCLE).sendToTarget();
                } else {
                    //发送结果：无CMCC
                    mHandler.obtainMessage(SCAN_CMCC_FAIL).sendToTarget();
                }
            }

            isScanning = false;
        }
    }

    private class ThreadLogin implements Runnable {

        @Override
        public void run() {

            if (isLogging) {
                return;
            }

            isLogging = true;

            mHandler.obtainMessage(MSG_LOGIN_START).sendToTarget();

            //获取Portal页面
            //for( int p=0;p<3 && !isTimeout;p++){
            //加载登录页面
            loadPotralPage();

            //等待登录页面加载完成
            for (int i = 0; i < 20 && !isTimeout; i++) {
                if (loginManager.isHasLoadPage()) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//				if( loginManager.isHasLoadPage() ){
//					break;
//				}
//			//}

            if (isTimeout) {
                mHandler.obtainMessage(LOGIN_CANCLE).sendToTarget();
            } else {
                //如果加载登录页面成功运行JS脚本
                if (loginManager.isHasLoadPage()) {
                    runSubmitScript();
                } else {
                    //打开登录主页失败
                    sendResultEvent(RcuEventCommand.WLAN_OPEN_PORTAL_PAGE_FAILURE);

                    mHandler.obtainMessage(MSG_LOGIN_FAIL).sendToTarget();
                    return;
                }

                //等待登录完成
                for (int i = 0; i < 30 && !loginManager.isHasLogin(); i++) {
                    if (loginManager.isHasLogin()) {
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (loginManager.isHasLogin()) {
                    mHandler.obtainMessage(MSG_LOGIN_SUCCESS).sendToTarget();
                } else {
                    //用户登录失败
                    sendResultEvent(RcuEventCommand.WLAN_LOGIN_FAILURE);
                    mHandler.obtainMessage(MSG_LOGIN_FAIL).sendToTarget();
                }
            }

            isLogging = false;
        }
    }


    private class ThreadLogout implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //注销开始
            sendResultEvent(RcuEventCommand.WLAN_LOGOUT_START);
            mHandler.obtainMessage(LOGOUT).sendToTarget();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < 10 && loginManager.isHasLogin() && !hasCancleAlert; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (loginManager.isHasLogin()) {
                if (!hasCancleAlert) {
                    mHandler.obtainMessage(LOGOUT_FAIL).sendToTarget();
                    sendResultEvent(RcuEventCommand.WLAN_LOGOUT_FAILURE);
                } else {
                    mHandler.obtainMessage(DIMISS_PROGRESS).sendToTarget();
                }
            } else {
                //注销成功
                sendResultEvent(RcuEventCommand.WLAN_LOGOUT_SUCCESS);
                mHandler.obtainMessage(LOGOUT_SUCCESS).sendToTarget();
                //删除成功登录的数据
                //Database.getInstance( Status.this ).deleteTempValue( TEMP_TAG );
                Thread cleaner = new Thread() {
                    public void run() {
                        File file = new File(getFilesDir() + "/temp/" + TEMP_FILE);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                };
                cleaner.start();
            }

            try {
                File file = new File(WlanLogin.this.getCacheDir() + "/webviewCache");
                LogUtil.w(tag, "---clear cache:" + file.getAbsolutePath());
                Runtime.getRuntime().exec("rm -r " + file.getAbsolutePath());
            } catch (Exception e) {

            }

        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

                case START:
                    btnConnect.setText(R.string.str_cancle);
                    sendResultEvent(RcuEventCommand.WLAN_WIFI_CONNECT_START);
                    break;

                case OPEN_WLAN:
                    txtTitle.setText(R.string.main_open_waln);
                    txtInfo.setText(R.string.status_wifi_off_tip);
                    showProgress(getString(R.string.main_open_waln), true);
                    break;

                case SCAN_CMCC:
                    txtTitle.setText(getString(R.string.main_scan_cmcc));
                    txtInfo.setText(R.string.status_scanning);
                    showProgress(getString(R.string.main_scan_cmcc), true);
                    break;

                //搜索CMCC成功
                case GET_IP_SUCCESS:
                    if (!loginManager.isHasLogin()) {
                        new Thread(new ThreadLogin()).start();
                    } else {
                        dismissProgress();
                        changeStatus();
                    }
                    sendResultEvent(RcuEventCommand.WLAN_WIFI_CONNECT_SUCCESS);
                    break;

                //搜索CMCC失败
                case SCAN_CMCC_FAIL:
                    dismissProgress();
                    Toast.makeText(WlanLogin.this,
                            getString(R.string.main_scan_cmcc_fail),
                            Toast.LENGTH_SHORT).show();
                    changeStatus();
                    sendResultEvent(RcuEventCommand.WLAN_WIFI_CONNECT_FAILURE);
                    finish();
                    break;

                case GET_IP:
                    txtTitle.setText(R.string.status_getip);
                    txtInfo.setText(R.string.status_getip_longtime);
                    showProgress(getString(R.string.status_getip), true);
                    break;

                //获取IP失败
                case GET_IP_FAIL:
                    dismissProgress();
                    Toast.makeText(WlanLogin.this,
                            getString(R.string.status_getip_fail),
                            Toast.LENGTH_SHORT).show();
                    changeStatus();
                    sendResultEvent(RcuEventCommand.WLAN_WIFI_CONNECT_FAILURE);
                    finish();
                    break;

                case DIMISS_PROGRESS:
                    dismissProgress();
                    break;

                case MSG_TIME:
                    long online = (System.currentTimeMillis() - loginManager.getLoginTime()) / 1000;
                    int hour = (int) online / 3600;
                    int minute = (int) (online / 60) % 60;
                    int second = (int) online % 60;
                    String time = getString(R.string.online_time) + "\t"
                            + (hour < 10 ? ("0" + hour) : hour) + ":"
                            + (minute < 10 ? ("0" + minute) : minute) + ":"
                            + (second < 10 ? ("0" + second) : second);
                    txtTime.setText(time);
                    break;

                case MSG_LOGIN_START:
                    showProgress(getString(R.string.status_title_logging), true);
                    txtTitle.setText(R.string.status_title_logging);
                    txtInfo.setText(R.string.status_login_longTime);
                    break;

                case MSG_LOGIN_SUCCESS:
                    dismissProgress();
                    break;

                case MSG_LOGIN_FAIL:
                    txtTitle.setText(getString(R.string.status_login_fail));
                    btnConnect.setText(R.string.login);
                    Toast.makeText(WlanLogin.this, getString(R.string.status_login_fail), Toast.LENGTH_SHORT).show();
                    dismissProgress();
                    finish();
                    break;

                case LOGIN_CANCLE:
                    btnConnect.setText(R.string.login);
                    Toast.makeText(WlanLogin.this,
                            getString(R.string.status_login_cancle),
                            Toast.LENGTH_SHORT).show();
                    changeStatus();
                    dismissProgress();
                    sendResultEvent(RcuEventCommand.WLAN_WIFI_CONNECT_FAILURE);
                    finish();
                    break;

                //下线
                case LOGOUT:
                    runOffLineScript();
                    showProgress(getString(R.string.status_offline_ing), true);
                    break;

                //下线成功
                case LOGOUT_SUCCESS:
                    dismissProgress();
                    finish();
                    break;

                //下线失败
                case LOGOUT_FAIL:
                    Toast.makeText(WlanLogin.this, getString(R.string.status_offline_fail), Toast.LENGTH_LONG).show();
                    dismissProgress();
                    finish();
                    break;

                //刷新wifi状态
                case REFRESH_WIFI:
                    int level = msg.arg1;
                    int rssi = msg.arg2;

                    //当rssi低于-90时，当成是信号最弱
                    if (rssi <= -90) {
                        imgSignal.setImageLevel(1);
                    } else {
                        if (level != 0 && rssi != 0) {
                            //以-100到level等分6个区间
                            int s = (level - (-100)) / 6;
                            int signal = 0;
                            if (s != 0) {
                                signal = ((rssi - (-100)) / s);
                            }

                            txtRssi.setText(rssi + "dBm");
                            imgSignal.setImageLevel(signal >= 6 ? 6 : signal);
                        } else {
                            txtRssi.setText("");
                            imgSignal.setImageLevel(NOSIGNAL);
                        }
                    }
                    break;

                //刷新时间
                case REFRESH_TIME:
                    refreshTime();
                    break;

                //进度动画
                case PROGRESS:
                    for (int i = 0; i < progressDots.length; i++) {
                        progressDots[i].setImageLevel(progress[i]);
                    }
                    break;

                case GET_APPS:
    			/*ArrayList<ApplicationInfo> appInfoList = (ArrayList<ApplicationInfo>) msg.obj;
    			findLinks( appInfoList );*/
                    break;
            }
        }
    };
    
    
/*    private ImageButton.OnLongClickListener linkLongListener = new ImageButton.OnLongClickListener(){
		@Override
		public boolean onLongClick(View arg0) {
			
			Drawable drawable = null;
			String title = null;
			String pkgName = null;
			for( int i=0;i<buttonList.size();i++){
				if( arg0.getTag().equals( buttonList.get(i).getTag() ) ){
					if( mInfoList != null ){
						drawable = mInfoList.get(i).getIcon();
						title = mInfoList.get(i).getTitle().toString();
						pkgName = mInfoList.get(i).getPackageName();
						break;
					}
				}
			}
			
			
			if( drawable!=null && title != null && pkgName!=null ){
				final String packgeName = pkgName;
				new AlertDialog.Builder(Status.this)
				.setTitle( title ) 
				.setIcon( drawable )
				.setMessage( R.string.status_remove_link_tip )
				.setPositiveButton( R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Database.getInstance( Status.this ).deleteLinkPackageName( packgeName );
						new Thread( new ApplicaiontReader(true) ).start();
					}
				})
				.setNegativeButton( R.string.str_cancle, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				}).show();
			}
			return false;
		}
    	
    };*/
    
/*    private Button.OnClickListener linkListener = new Button.OnClickListener(){
    	@Override
		public void onClick(View arg0) {
    		if( arg0.getTag().equals( "add") ){
    			showAppListDialog();
    		}else{
    			for( int i=0;i<buttonList.size();i++){
    				if( arg0.getTag().equals( buttonList.get(i).getTag() ) ){
    					app.startApplication( buttonList.get(i).getTag().toString() );
    					break;
    				}
    			}
    		}
    	}
    };*/


    /*Button Listener*/
    private Button.OnClickListener btn_listener = new Button.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            switch (arg0.getId()) {

                case R.id.btn_offline:
                    new Thread(new ThreadLogout()).start();
                    break;

                case R.id.btn_connect:
                    if (btnConnect.getText().equals(getString(R.string.login))) {
                        new Thread(new WifiScanner()).start();
                    } else if (btnConnect.getText().equals(getString(R.string.str_cancle))) {
                        isTimeout = true;
                        btnConnect.setText(R.string.login);
                        finish();
                    }
                    break;
            }
        }
    };
	
/*	*//**
     * 广播接收器:接收来广播更新界面
     * *//*
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			if( intent.getAction().equals( WlanService.ACTION_WIFI_REFRESH ) ){
				if( !isScanning && !isLogging ){
					changeStatus();
				}
			}
		}
	};
	
	*/

    /**
     * 注册广播接收器
     *//*
	private void regeditBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(  WlanService.ACTION_WIFI_REFRESH );
		this.registerReceiver( mBroadcastReceiver, filter );
	}
    */
    private void showProgress(String message, boolean cancleable) {
        LogUtil.d(tag, "message=" + message + "cancleable=" + cancleable);
        linerLayoutProgress.setVisibility(View.VISIBLE);
        new Thread(new ProgressBar()).start();
    }

    private void dismissProgress() {
        linerLayoutProgress.setVisibility(View.INVISIBLE);
    }


    private void runSubmitScript() {
        //用户开始登录
        sendResultEvent(RcuEventCommand.WLAN_LOGIN_START);

        //wv1.loadUrl("javascript:alert(document.body.innerHTML);");
        wv1.loadUrl("javascript:document.getElementById(\"staticusernameid\")" +
                ".value=\"" + user + "\";void(0);");
        wv1.loadUrl("javascript:document.getElementById(\"staticpasswordid\")" +
                ".value=\"" + pass + "\";void(0);");
        wv1.loadUrl("javascript:document.getElementsByName(\"staticlogin\")[0]" +
                ".submit();void(0);");
    }

    private void runOffLineScript() {
        wv.loadUrl("javascript:ButtonSubmit(document.getElementsByName(\"portal\")[0]);");
    }

//    private void myFinish( Intent intent ){
//    	finish();
//    	startActivity( intent );
//    }

//    private void exitApp(){
//    	finish();
//    }

    /***
     * 显示通知
     * @param tickerText 通知显示的内容
     * @param strBroadcast 点通知后要发的广播
     */
    @SuppressWarnings("deprecation")
    protected void showNotification(String tickerText, String strBroadcast) {
        //生成通知管理器
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // notification //通知图标, 状态栏显示的通知文本提示,通知产生的时间
        Notification.Builder notification = new Notification.Builder(this);
        notification.setTicker(tickerText);
        notification.setSmallIcon(R.mipmap.walktour);
        notification.setWhen(System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getBroadcast(WlanLogin.this,
                0, new Intent((strBroadcast == null) ? "" : strBroadcast), 0);
        // must set this for content view, or will throw a exception
        //如果想要更新一个通知，只需要在设置好notification之后，再次调用 setLatestEventInfo(),然后重新发送一次通知即可，即再次调用notify()。
        notification.setAutoCancel(true);
        notification.setContentIntent(contentIntent);
        notification.setContentTitle(getString(R.string.sys_alarm));
        notification.setContentText(tickerText);
        mNotificationManager.notify(R.string.service_started, notification.build());
    }

    /**
     * 发送广播到WlanTest服务
     *
     * @param rcuEvent 事件对应的RCU事件
     */
    private void sendResultEvent(int rcuEvent) {
        Intent intent = new Intent();
        intent.setAction(WlanTest.ACTION_WLAN_EVENT);
        intent.putExtra(WlanTest.KEY_EVENT, rcuEvent);
        sendBroadcast(intent);
    }
    
/*	*/

    /**
     * 按键事件
     *//*
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
    	 
         switch (keyCode) { 
         case KeyEvent.KEYCODE_BACK: 
    		 Intent intent=new Intent(Intent.ACTION_MAIN);
    		 intent.addCategory(Intent.CATEGORY_HOME);
    		 startActivity(intent);
        	 
        	 break;
        	 
         case KeyEvent.KEYCODE_MENU:
        	 return false;
        	 
         case KeyEvent.KEYCODE_HOME:
        	 return false;
        	 
         }
         
		return true;
    }*/
    
/*    @Override
	public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.clear();
		menu.add( getString(R.string.status_switch) ).setIcon(R.drawable.switch_default);
		menu.add( getString(R.string.exit) ).setIcon(R.drawable.exit_default);
		return super.onPrepareOptionsMenu(menu);
	}
    
    @Override//菜单点击事件
	public boolean onOptionsItemSelected(MenuItem item){    	
		super.onOptionsItemSelected(item);
		
		//切换用户
		if( item.getTitle().equals( getString(R.string.status_switch) ) ){
			if( LoginManager.getInstance().isHasLogin() ){
				if( LoginManager.getInstance().isHasIP() ){
					 new AlertDialog.Builder( this )
		        	 .setTitle( R.string.status_switch_alert )
		        	 .setPositiveButton( getString(R.string.str_ok) , new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							new Thread( new ThreadLogout() ).start();
						}
					})
					.setNegativeButton(getString(R.string.str_cancle), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					})
		        	 .show();
				}else{
					Intent i = new Intent( this,Manual2.class);
					myFinish( i );
				}
			}else{
				Intent i = new Intent( this,Manual2.class);
				myFinish( i );
			}
		}else if( item.getTitle().equals( getString(R.string.exit) ) ){
			if(  LoginManager.getInstance().isHasLogin() ){
				if( LoginManager.getInstance().isHasIP() ){
					new AlertDialog.Builder( this )
					.setTitle( R.string.status_switch_alert )
					.setPositiveButton( getString(R.string.str_ok) , new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							exitAfterOffLine = true;
							new Thread( new ThreadLogout() ).start();
						}
					})
					.setNegativeButton(getString(R.string.str_cancle), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					})
					.show();
				}else{
					exitApp();
				}
			}else{
				exitApp();
			}
		}
		return true;
    }
    */
    public static String getClassName() {
        String className = null;
        try {
            throw new Exception();
        } catch (Exception e) {
            StackTraceElement[] element = e.getStackTrace();
            className = element[0].getClassName();
        }
        return className;
    }
}