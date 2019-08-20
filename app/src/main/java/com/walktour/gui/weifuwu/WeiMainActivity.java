package com.walktour.gui.weifuwu;

import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.TextView;

import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicTabActivity;
import com.walktour.gui.R;
import com.walktour.gui.share.logic.RegisterDeviceLogic;
import com.walktour.gui.share.logic.ShareHttpRequestUtil;
import com.walktour.gui.share.model.BaseResultInfoModel;
import com.walktour.gui.weifuwu.business.model.ShareDeviceModel;
import com.walktour.gui.weifuwu.business.model.ShareGroupModel;
import com.walktour.gui.weifuwu.business.model.ShareGroupRelationModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;
import com.walktour.gui.weifuwu.view.MyViewPager;

import java.util.ArrayList;
import java.util.List;
/***
 * 微服务主窗口
 * 
 * @author weirong.fan
 *
 */
@SuppressWarnings("deprecation")
public class WeiMainActivity extends BasicTabActivity implements OnClickListener {
	/** 进度提示 */
	private ProgressDialog progressDialog;
	public static TabHost mTabHost;
	public static LocalActivityManager manager = null;
	// 页卡内容
	private MyViewPager mPager;
	// 页卡头标
	private TextView t1, t2, t3;
	private LinearLayout tab1, tab2, tab3;
	private ImageView img1, img2, img3;
	// Tab页面列表
	private List<View> listViews;
	private int tabColor = -1;
	private Context context = WeiMainActivity.this;
	/** 更多菜单 */
	private PopupWindow popMoreMenu; 
	private String deviceName = "";
	private String groupName = "";
	private String groupCode = "";
	private Button shareBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.weifuwumainlayout);
		initTextView(R.id.title_txt).setText(R.string.share_project_weifuwu);
		deviceName = SharePreferencesUtil.getInstance(context).getString(RegisterDeviceLogic.SHARE_DEVICE_NAME, "");
		findViewById(R.id.pointer).setOnClickListener(this);
		shareBtn = (Button) findViewById(R.id.share);
		shareBtn.setVisibility(View.GONE);
		shareBtn.setBackgroundResource(R.drawable.obj_new);
		shareBtn.setOnClickListener(this);
		mTabHost = getTabHost();
		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);
		InitTextView();
		InitViewPager();
	}
	/**
	 * 
	 * 初始化头标
	 */
	private void InitTextView() {
		t1 = (TextView) findViewById(R.id.oneTV);
		t2 = (TextView) findViewById(R.id.twoTV);
		t3 = (TextView) findViewById(R.id.threeTV);
		tab1 = (LinearLayout) findViewById(R.id.tab1);
		tab2 = (LinearLayout) findViewById(R.id.tab2);
		tab3 = (LinearLayout) findViewById(R.id.tab3);
		img1 = (ImageView) findViewById(R.id.tabimg1);
		img2 = (ImageView) findViewById(R.id.tabimg2);
		img3 = (ImageView) findViewById(R.id.tabimg3);
		tabColor = getResources().getColor(R.color.tabcolor);
		t1.setTextColor(tabColor);
		tab1.setBackgroundResource(R.drawable.bg_item_list_8);
		img1.setBackgroundResource(R.drawable.obj_history);
		t2.setTextColor(Color.BLACK);
		img2.setBackgroundResource(R.drawable.obj_relation_hui);
		t3.setTextColor(Color.BLACK);
		img3.setBackgroundResource(R.drawable.obj_me_hui);
		tab1.setOnClickListener(new MyOnClickListener(0));
		tab2.setOnClickListener(new MyOnClickListener(1));
		tab3.setOnClickListener(new MyOnClickListener(2));
	}
	/**
	 * 
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		mPager = (MyViewPager) findViewById(R.id.vPager);
		mPager.setPagingEnabled(false);
		listViews = new ArrayList<View>();
		MyPagerAdapter mpAdapter = new MyPagerAdapter(listViews);
		Intent intent = new Intent(context, WeiMain1Activity.class);
		listViews.add(getView("one", intent));
		Intent intent2 = new Intent(context, WeiMain2Activity.class);
		listViews.add(getView("two", intent2));
		Intent intent3 = new Intent(context, WeiMain3Activity.class);
		listViews.add(getView("three", intent3));
		mPager.setAdapter(mpAdapter);
	}
	/**
	 * 
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;
		public MyOnClickListener(int i) {
			index = i;
		}
		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
			switch (index) {
			case 0:
				t1.setTextColor(tabColor);
				tab1.setBackgroundResource(R.drawable.bg_item_list_8);
				img1.setBackgroundResource(R.drawable.obj_history);
				t2.setTextColor(Color.BLACK);
				tab2.setBackgroundResource(R.drawable.white);
				img2.setBackgroundResource(R.drawable.obj_relation_hui);
				t3.setTextColor(Color.BLACK);
				tab3.setBackgroundResource(R.drawable.white);
				img3.setBackgroundResource(R.drawable.obj_me_hui);
				mTabHost.setCurrentTab(0);
				shareBtn.setVisibility(View.GONE);
				break;
			case 1:
				t1.setTextColor(Color.BLACK);
				tab1.setBackgroundResource(R.drawable.white);
				img1.setBackgroundResource(R.drawable.obj_history_hui);
				t2.setTextColor(tabColor);
				tab2.setBackgroundResource(R.drawable.bg_item_list_8);
				img2.setBackgroundResource(R.drawable.obj_relation);
				t3.setTextColor(Color.BLACK);
				tab3.setBackgroundResource(R.drawable.white);
				img3.setBackgroundResource(R.drawable.obj_me_hui);
				mTabHost.setCurrentTab(1);
				shareBtn.setVisibility(View.VISIBLE);
				break;
			case 2:
				t1.setTextColor(Color.BLACK);
				tab1.setBackgroundResource(R.drawable.white);
				img1.setBackgroundResource(R.drawable.obj_history_hui);
				t2.setTextColor(Color.BLACK);
				tab2.setBackgroundResource(R.drawable.white);
				img2.setBackgroundResource(R.drawable.obj_relation_hui);
				t3.setTextColor(tabColor);
				tab3.setBackgroundResource(R.drawable.bg_item_list_8);
				img3.setBackgroundResource(R.drawable.obj_me);
				mTabHost.setCurrentTab(2);
				shareBtn.setVisibility(View.GONE);
				break;
			}
		}
	};
	private View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}
	/**
	 * 关闭子页面
	 */
	private void finishActivity() {
		manager.destroyActivity("one", true);
		manager.destroyActivity("two", true);
		manager.destroyActivity("three", true);
	}
	/**
	 * 
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;
		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}
		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}
		@Override
		public void finishUpdate(View arg0) {
		}
		@Override
		public int getCount() {
			return mListViews.size();
		}
		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}
		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}
		@Override
		public Parcelable saveState() {
			return null;
		}
		@Override
		public void startUpdate(View arg0) {
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		finishActivity();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pointer:
			this.finish();
			break;
		case R.id.share:
			showMoreMenu();
			break;
		case R.id.txt_params_setting:
			Bundle bundle = new Bundle();
			jumpActivity(WeiMainJoinGroupActivity.class, bundle);
			colseMenu();
			break;
		case R.id.txt_tab:
			createGroup();
			colseMenu();
			break;
		}
	}
	private void createGroup() {
		final View textEntryView = LayoutInflater.from(context).inflate(R.layout.alert_dialog_edittext1, null);
		final EditText et = (EditText) textEntryView.findViewById(R.id.alert_textEditText);
		et.setHint(R.string.share_project_input_group_name);
		new BasicDialog.Builder(this).setView(textEntryView).setTitle(R.string.share_project_create_group)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						groupName = et.getText().toString() + "";
						new CreateGroup().execute();
					}
				}).setNegativeButton(R.string.str_cancle).show();
	}
	private void colseMenu() {
		if (popMoreMenu != null) {
			if (popMoreMenu.isShowing()) {
				popMoreMenu.dismiss();
			}
		}
	}
	private void showMoreMenu() {
		if (popMoreMenu == null) {
			View view = LayoutInflater.from(this).inflate(R.layout.weifuwu_group, null);
			TextView paramsSetting = (TextView) view.findViewById(R.id.txt_params_setting);
			paramsSetting.setText(getString(R.string.share_project_add_group));
			TextView tabSetting = (TextView) view.findViewById(R.id.txt_tab);
			tabSetting.setText(getString(R.string.share_project_create_group));
			paramsSetting.setOnClickListener(this);
			tabSetting.setOnClickListener(this);
			popMoreMenu = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);//
			popMoreMenu.setOutsideTouchable(true);
			popMoreMenu.setFocusable(true);
			popMoreMenu.setTouchable(true);
			popMoreMenu.setBackgroundDrawable(new BitmapDrawable());
			popMoreMenu.showAsDropDown(findViewById(R.id.title_layout), 0, 10);
		} else {
			if (popMoreMenu.isShowing()) {
				popMoreMenu.dismiss();
			} else {
				popMoreMenu.showAsDropDown(findViewById(R.id.title_layout), 0, 10);
			}
		}
	}
	/***
	 * 新建组
	 * 
	 * @author weirong.fan
	 *
	 */
	private class CreateGroup extends AsyncTask<Void, Void, BaseResultInfoModel> {
		public CreateGroup() {
			super();
		}
		@Override
		protected void onPostExecute(BaseResultInfoModel result) {
			super.onPostExecute(result);
			try {
				if (result.getReasonCode() == 1) {// 网络正常
					if (!result.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {// 新建组失败
						ToastUtil.showToastShort(context, getString(R.string.share_project_create_group_failure));
					} else {// 新建组成功
						ShareGroupModel model = new ShareGroupModel();
						model.setGroupCode(result.getGroup_code());
						model.setGroupName(groupName);
						model.setCreateDeviceCode(ShareCommons.device_code);
						// 将相应设备的详细信息填入设备表
						ShareDeviceModel deviceModel = new ShareDeviceModel();
						deviceModel.setDeviceCode(ShareCommons.device_code);
						deviceModel.setDeviceName(deviceName);
						deviceModel.setDeviceOS(ShareDeviceModel.OS_ANDROID);
						deviceModel.setDeviceType(android.os.Build.MODEL + "");
						ShareDataBase.getInstance(context).insertDevice(deviceModel);
						ShareDataBase.getInstance(context).saveOrUpdateGroup(model);
						ShareGroupRelationModel gm = new ShareGroupRelationModel();
						gm.setGroupCode(result.getGroup_code());
						gm.setDeviceCode(ShareCommons.device_code);
						ShareDataBase.getInstance(context).insertGroupRelation(gm);
						Bundle bundle = new Bundle();
						bundle.putString("groupCode", result.getGroup_code());
						jumpActivity(WeiMainJoinMemberActivity.class, bundle);
						Intent intent = new Intent(ShareCommons.SHARE_ACTION_MAIN_2);
						sendBroadcast(intent);
					}
				} else {// 网络错误
					ToastUtil.showToastShort(context, getString(R.string.sys_alarm_neterr));
				}
			} catch (Exception e) {
				e.printStackTrace();
				ToastUtil.showToastShort(context, getString(R.string.share_project_create_group_failure));
			}
			closeDialog();
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			openDialog(getString(R.string.share_project_server_doing));
		}
		@Override
		protected BaseResultInfoModel doInBackground(Void... params) {
			BaseResultInfoModel model = ShareHttpRequestUtil.getInstance().registerGroup(ShareCommons.device_code, groupName,ShareCommons.session_id);
			if(model.getReasonCode()==1&&model.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_FAILURE)){
				RegisterDeviceLogic.getInstance(context).shareRegister();
				model = ShareHttpRequestUtil.getInstance().registerGroup(ShareCommons.device_code, groupName,ShareCommons.session_id);
			}
			return model;
		}
	}
	/**
	 * 打开进度条
	 * 
	 * @param txt
	 */
	protected void openDialog(String txt) {
		progressDialog = new ProgressDialog(this.context);
		progressDialog.setMessage(txt);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	/**
	 * 关闭进度条
	 */
	protected void closeDialog() {
		progressDialog.dismiss();
	}
}
