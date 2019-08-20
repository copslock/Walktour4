package com.walktour.gui.about;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walktour.Utils.AppVersionControl;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.UtilsMethod;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.framework.ui.ActivityManager;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.GetPowByNet;
import com.walktour.gui.R;
import com.walktour.gui.upgrade.UpgradeActivity;

import butterknife.ButterKnife;

/***
 * 关于界面
 */
public class AboutActivity extends BasicActivity implements OnClickListener {
    //    private RelativeLayout lisense_detail;
//    private RelativeLayout getLicense;
//    private RelativeLayout gls;
//    private RelativeLayout update;
//    private RelativeLayout help;
    private ApplicationModel appModel = ApplicationModel.getInstance();
    private boolean isSelectLocalLicense = false;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about_license_page);
        ButterKnife.bind(this);
        findView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!appModel.isCheckPowerSuccess() && isSelectLocalLicense) {
            finish();
        }
    }

    private void findView() {
        ImageView walktourImage = initImageView(R.id.about_walktour);
        ImageView logohwImage = initImageView(R.id.about_logo_hw);
        TextView webAddress = initTextView(R.id.web_address);
        webAddress.setText(Html.fromHtml("<font color='#1D7EC7'><a href=\"" + getString(R.string.aboutus_url) + "\">" + getString(R.string.aboutus_url) + "</a></font>"));
        boolean isHuawei = appModel.isHuaWeiTest();
        if (isHuawei) {
            walktourImage.setVisibility(View.GONE);
            logohwImage.setVisibility(View.VISIBLE);
            TextView copyrightTxt = initTextView(R.id.copyright);
            String copyright = "Copyright &#169; 2011-2019 Huawei Technologies Corp.,Ltd.";
            copyrightTxt.setText(Html.fromHtml(copyright));
            webAddress.setText(Html.fromHtml("<font color='#1D7EC7'><a href=\"http://www.huawei.com\">http://www.huawei.com</a></font>"));
            webAddress.setTextColor(Color.WHITE);
        }else if(AppVersionControl.getInstance().isPerceptionTest()){
            walktourImage.setImageResource(R.drawable.changjia);
            webAddress.setVisibility(View.GONE);
        }


        webAddress.setMovementMethod(LinkMovementMethod.getInstance());
        findViewById(R.id.about_license_item).setOnClickListener(this);
        findViewById(R.id.about_getlicense_item).setOnClickListener(this);
        findViewById(R.id.about_gls_item).setOnClickListener(this);
        findViewById(R.id.about_update_item).setOnClickListener(this);
        findViewById(R.id.about_help_item).setOnClickListener(this);
        if(isHuawei){
            findViewById(R.id.about_help_item).setVisibility(View.GONE);
            ButterKnife.findById(this, R.id.tv_namex).setVisibility(View.GONE);
        }
        findViewById(R.id.about_license_item_item).setOnClickListener(this);
        TextView title = initTextView(R.id.title_txt);
        title.setText(getResources().getText(R.string.main_menu_about));
        //居中显示
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) title.getLayoutParams();
        params.removeRule(RelativeLayout.RIGHT_OF);
        title.setLayoutParams(params);
        ImageView pointer = initImageView(R.id.pointer);
        pointer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.this.finish();
            }
        });
        TextView about_version = initTextView(R.id.about_version);
        String version = "Pilot Walktour  " + UtilsMethod.getCurrentVersionName(AboutActivity.this);
        if(AppVersionControl.getInstance().isPerceptionTest()){
            version = "" + UtilsMethod.getCurrentVersionName(AboutActivity.this);
        }
        about_version.setText(version);
        TextView licenseValid = initTextView(R.id.about_license_valid_txt);
        licenseValid.setText(String.format(getString(R.string.about_license_timelimit_activedays), String.valueOf(appModel.getActiveTime())));
        TextView deviceImei = (TextView) findViewById(R.id.about_device_imei_txt);
        String id = "*ID:" + MyPhoneState.getInstance().getMyDeviceId(getApplicationContext());
        deviceImei.setText(id);
        deviceImei.setOnClickListener(this);
        if(AppVersionControl.getInstance().isPerceptionTest()) {
            ButterKnife.findById(this, R.id.tv_namex).setVisibility(View.GONE);
            ButterKnife.findById(this, R.id.copyright).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about_license_item:
                break;
            case R.id.about_license_item_item:
                jumpActivity(LicenseViewActivity.class);
                break;
            case R.id.about_getlicense_item:
            /*new BasicDialog.Builder(AboutActivity.this)
            .setIcon( R.drawable.icon_info )
            .setTitle(R.string.menu_option)
            .setItems(getResources().getStringArray(R.array.select_license_array), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					appModel.setCheckPowerSuccess( false );
					isSelectLocalLicense = true;
					switch(which){
					case 0:
						startActivity(new Intent(AboutActivity.this,GetPowByNet.class).putExtra("fromMain", true));
						dialog.dismiss();
						break;
					case 1:
				    	new LicenseExplorer(AboutActivity.this,new String[]{"bin"} ).start();
						dialog.dismiss();
						//ActivityManager.finishAll();
						break;
					}
				}
			})
           .show();*/
                appModel.setCheckPowerSuccess(false);
                isSelectLocalLicense = true;
                startActivity(new Intent(AboutActivity.this, GetPowByNet.class).putExtra("fromMain", true));
                break;
            case R.id.about_gls_item:
                Intent glsIntent = new Intent(AboutActivity.this, GlsLoginActivity.class);
                startActivity(glsIntent);
                overridePendingTransition(R.anim.open_next, R.anim.close_main);
                break;
            case R.id.about_update_item:
                Intent upgradeIntent = new Intent(AboutActivity.this, UpgradeActivity.class);
                startActivity(upgradeIntent);
                break;
            case R.id.about_help_item:
                if(AppVersionControl.getInstance().isPerceptionTest()){
                    return;
                }
                new BasicDialog.Builder(context).setTitle(R.string.str_help).setItems(getResources().getStringArray(R.array.help_doc), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(AboutActivity.this, WebViewActivity.class);
                        if (which == 0) {
                            intent.putExtra("path", getFilesDir().getAbsolutePath() + "/quick_guide/content.html");
                        } else if (which == 1) {
                            intent.putExtra("path", getFilesDir().getAbsolutePath() + "/help_doc/menu.html");
                        }
                        startActivity(intent);
                    }
                }).show();
                break;
            case R.id.about_device_imei_txt:
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_about_cmd, null);
                final EditText command = (EditText) view.findViewById(R.id.cmd);
                new BasicDialog.Builder(context).setView(view).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread() {
                            @Override
                            public void run() {
                                UtilsMethod.runRootCMD(command.getText().toString());
                            }
                        }.start();
                    }
                }).show();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && !appModel.isCheckPowerSuccess()) {
            ActivityManager.finishAll();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
        if (ConfigRoutine.getInstance().getAutoValue(getApplicationContext()).length <= 0) {
            ActivityManager.finishAll();
        }
    }
}

