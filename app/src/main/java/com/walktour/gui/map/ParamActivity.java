package com.walktour.gui.map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.dingli.wlan.apscan.WifiScanner;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.NetType;
import com.walktour.Utils.WalkStruct.ShowInfoType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.Deviceinfo;
import com.walktour.control.config.PageManager;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.framework.view.ScroollTabActivity;
import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 基本参数界面<BR>
 * 根据不同网络类型展示不同选项参数
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-6-25] 
 */
public class ParamActivity extends ScroollTabActivity implements
        OnTabChangeListener ,RefreshEventListener {
    
    private final String TAG = "ParamActivity";
    
    /**
     * 信息界面选项卡页面列表
     */
    private ArrayList<ShowInfoType> pageList;
    
    /**
     * 参数界面实际展示列表项
     */
    private List<ParamTab> paramTabList = new ArrayList<ParamTab>();
    
    private SharedPreferences mPreferences;
    
    /**
     * 参数界面数
     */
    private int pageCount = 0;
    
    private LinearLayout tab1_layout;
    
    private LinearLayout tab2_layout;
    
    private LinearLayout tab3_layout;
    
    private LinearLayout tab4_layout;
    
    private LinearLayout tab5_layout;
    
    private LinearLayout tab6_layout;
    
    private LinearLayout tab7_layout;
    
    private ImageView scrollTag_2;
    
    private ImageView scrollTag_3;
    
    private ImageView scrollTag_4;
    
    private ImageView scrollTag_5;
    
    private ImageView scrollTag_6;
    
    private ImageView scrollTag_7;
    
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param savedInstanceState
     * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        WifiScanner.instance(this);
        RefreshEventManager.addRefreshListener(this);
        byNetWorkTocurrentTab();
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @see com.walktour.framework.view.ScroollActivity#initView()
     */
    @Override
    public void initView() {
        setContentView(R.layout.info_param2);
        mTabHost = (TabHost) findViewById(R.id.child_tabhost);
        mTabHost.setup(this.getLocalActivityManager());
        /*      TabWidget tabWidget = mTabHost.getTabWidget(); 
        scrollLayout = (ScrollLayout) findViewById(R.id.scrolllayout);
        
        scrollLayout.addChangeListener(this);*/
        tab1 = initButton(R.id.tab1);
        tab2 = initButton(R.id.tab2);
        tab3 = initButton(R.id.tab3);
        tab4 = initButton(R.id.tab4);
        tab5 = initButton(R.id.tab5);
        tab6 = initButton(R.id.tab6);
        tab7 = initButton(R.id.tab7);
        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        tab3.setOnClickListener(this);
        tab4.setOnClickListener(this);
        tab5.setOnClickListener(this);
        tab6.setOnClickListener(this);
        tab7.setOnClickListener(this);
        scrollTag = initImageView(R.id.top_bar_select);
        scrollTag_2 = initImageView(R.id.top_bar_select_2);
        scrollTag_3 = initImageView(R.id.top_bar_select_3);
        scrollTag_4 = initImageView(R.id.top_bar_select_4);
        scrollTag_5 = initImageView(R.id.top_bar_select_5);
        scrollTag_6 = initImageView(R.id.top_bar_select_6);
        scrollTag_7 = initImageView(R.id.top_bar_select_7);
        tab1_layout = initLinearLayout(R.id.tab1_layout);
        tab2_layout = initLinearLayout(R.id.tab2_layout);
        tab3_layout = initLinearLayout(R.id.tab3_layout);
        tab4_layout = initLinearLayout(R.id.tab4_layout);
        tab5_layout = initLinearLayout(R.id.tab5_layout);
        tab6_layout = initLinearLayout(R.id.tab6_layout);
        tab7_layout = initLinearLayout(R.id.tab7_layout);
        
        pageList = new PageManager(getApplicationContext(), false).getShowInfoList();
        
        for (int i = 0; i < pageList.size(); i++) {
            WalkStruct.ShowInfoType page = pageList.get(i);
            if (page.equals(WalkStruct.ShowInfoType.Gsm)) {
                ParamTab paramTab = new ParamTab(getResources().getString(R.string.info_gsm),new Intent(ParamActivity.this, Gsm.class));
                paramTabList.add(paramTab);
            }else if(page.equals(WalkStruct.ShowInfoType.Edge)){
                ParamTab paramTab = new ParamTab(getResources().getString(R.string.info_edge),new Intent(ParamActivity.this, Edge.class));
                paramTabList.add(paramTab);
             }else if (page.equals(WalkStruct.ShowInfoType.TDSCDMA)) {
            	 tab3.setTextSize(10); 
                ParamTab paramTab = new ParamTab(getResources().getString(R.string.info_tdscdma),new Intent(ParamActivity.this, TdScdma.class));
                paramTabList.add(paramTab);
            } else if (page.equals(WalkStruct.ShowInfoType.Umts)) {
            	tab3.setTextSize(10); 
                ParamTab paramTab = new ParamTab(getResources().getString(R.string.info_umts),new Intent(ParamActivity.this, Umts.class));
                paramTabList.add(paramTab);
            }else if (page.equals(WalkStruct.ShowInfoType.TDHspaPlus)) {  //td_hspa
            	tab4.setTextSize(10); 
                ParamTab paramTab = new ParamTab(getResources().getString(R.string.info_tdhspa),new Intent(ParamActivity.this, TDHspa.class));
                paramTabList.add(paramTab);
            }else if(page.equals(WalkStruct.ShowInfoType.Hspa)){
                ParamTab paramTab = new ParamTab(getResources().getString(R.string.info_hspa),new Intent(ParamActivity.this, Hspa.class));
                paramTabList.add(paramTab);
             }else if(page.equals(WalkStruct.ShowInfoType.HspaPlus)){
                ParamTab paramTab = new ParamTab(getResources().getString(R.string.info_hspa_plus),new Intent(ParamActivity.this, HspaPlus.class));
                paramTabList.add(paramTab);
             }else if (page.equals(WalkStruct.ShowInfoType.LTE)) {
                ParamTab paramTab = new ParamTab(getResources().getString(R.string.info_lte),new Intent(ParamActivity.this, LTE.class));
                paramTabList.add(paramTab);
            } else if (page.equals(WalkStruct.ShowInfoType.Cdma)) {
                ParamTab paramTab = new ParamTab(getResources().getString(R.string.info_cdma),new Intent(ParamActivity.this,  Cdma.class));
                paramTabList.add(paramTab);
            } else if (page.equals(WalkStruct.ShowInfoType.EvDo)) {
                ParamTab paramTab = new ParamTab(getResources().getString(R.string.info_evdo),new Intent(ParamActivity.this,  EvDo.class));
                paramTabList.add(paramTab);
            } 
            else if (page.equals(WalkStruct.ShowInfoType.WLAN)) {
                ParamTab paramTab = new ParamTab(getResources().getString(R.string.info_wlan),new Intent(ParamActivity.this,  Wlan.class));
                paramTabList.add(paramTab);
            }
//            else if (page.equals(WalkStruct.ShowInfoType.LTECA)){
//            	ParamTab paramTab = new ParamTab(getResources().getString(R.string.info_ca),new Intent(ParamActivity.this,  LteCa.class));
//            	paramTabList.add(paramTab);
//            }
        }
        
//        ParamTab paramTab = new ParamTab("Parm",new Intent(ParamActivity.this,  DynamicParamInfo.class));
//        paramTabList.add(paramTab);
        setTabBtnText();
        switch (pageCount) {
        	case 1:
                tab2_layout.setVisibility(View.GONE);
        		scrollTag_2.setVisibility(View.GONE);
        	case 2:
                tab3_layout.setVisibility(View.GONE);
        		scrollTag_3.setVisibility(View.GONE);
        	case 3:
                tab4_layout.setVisibility(View.GONE);
        		scrollTag_4.setVisibility(View.GONE);
        	case 4:
                tab5_layout.setVisibility(View.GONE);
        		scrollTag_5.setVisibility(View.GONE);
        	case 5:
                tab6_layout.setVisibility(View.GONE);
        		scrollTag_6.setVisibility(View.GONE);
            case 6:
                tab7_layout.setVisibility(View.GONE);
                scrollTag_7.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }
    
    public void setTabBtnText() {
        Button[] tabs = { tab1, tab2, tab3, tab4, tab5, tab6, tab7 };
        Collections.sort(paramTabList, new ParamComparator());
        
        for (int i = 0; i < paramTabList.size() && i < 7; i++) {
            ParamTab paramTab = paramTabList.get(i);
            if(mPreferences.getBoolean("display_" + paramTab.paramBtnText, true)){
                LogUtil.w(TAG, paramTab.paramBtnText);
                tabs[pageCount].setText(paramTab.paramBtnText);
                Button btn = new Button(this);
                btn.setBackgroundResource(R.drawable.background_mybutton);
                btn.setText(paramTab.paramBtnText);
                btn.setTextColor(getResources().getColor(R.color.white));
                mTabHost.addTab(mTabHost.newTabSpec(paramTab.paramBtnText)
                        .setIndicator(btn)
                        .setContent(paramTab.intent));
                pageCount ++;
            }
        }
        mTabHost.setOnTabChangedListener(this);
    }
    
    private class ParamTab{
        /**
         * [构造简要说明]
         * @param paramBtnText
         * @param intent
         */
        public ParamTab(String paramBtnText, Intent intent) {
            this.paramBtnText = paramBtnText;
            this.intent = intent;
        }

        public String paramBtnText;
        
        public Intent intent; 
        
    }
    
    private class ParamComparator implements Comparator<ParamTab> {
        public final int compare(ParamTab param1, ParamTab param2) {
            if(mPreferences.getInt("SORT_" + param1.paramBtnText, 0) >=  mPreferences.getInt("SORT_" + param2.paramBtnText, 0)){
                return 1;
            }else {
                return -1;
            }
        }
      }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @see android.app.ActivityGroup#onResume()
     */
    
    @Override
    protected void onResume() {
        super.onResume();
        Message msg = new Message();
        msg.what = -1;
        initSelectTabHandle.sendMessageDelayed(msg, 500);
        if(mTabHost.getCurrentTabTag() != null 
        		&& mTabHost.getCurrentTabTag().equals(getResources().getString(R.string.info_wlan))){
        	if(!WifiScanner.isScannerWifi()){
        		WifiScanner.setScannerWifi(true);
        		WifiScanner.instance(this).startScan();
        	}
        }
    }
    
    /** 
     * 初始化选中Tab覆盖图片的Handler 
     */
    private Handler initSelectTabHandle = new Handler() {
        public void handleMessage(Message msg) {
            snapToScreenTab();
            super.handleMessage(msg);
        }
    };
    
    /**
     * 切换Tab页<BR>
     * [功能详细描述]
     */
    public void snapToScreenTab(){
        switch (TraceInfoInterface.currentShowChildTab) {
            case Gsm:
                mTabHost.setCurrentTabByTag(getResources().getString(R.string.info_gsm));
                snapToScreen(mTabHost.getCurrentTab());
                break;
            case Umts:
                mTabHost.setCurrentTabByTag(getResources().getString(R.string.info_umts));
                snapToScreen(mTabHost.getCurrentTab());
                break;
            case Hspa:
                mTabHost.setCurrentTabByTag(getResources().getString(R.string.info_hspa));
                snapToScreen(mTabHost.getCurrentTab());
                break;
            case HspaPlus:
                mTabHost.setCurrentTabByTag(getResources().getString(R.string.info_hspa_plus));
                snapToScreen(mTabHost.getCurrentTab());
                break;
            case LTE:
                mTabHost.setCurrentTabByTag(getResources().getString(R.string.info_lte));
                snapToScreen(mTabHost.getCurrentTab());
                break;
            case Cdma:
                mTabHost.setCurrentTabByTag(getResources().getString(R.string.info_cdma));
                snapToScreen(mTabHost.getCurrentTab());
                break;
            case EvDo:
                mTabHost.setCurrentTabByTag(getResources().getString(R.string.info_evdo));
                snapToScreen(mTabHost.getCurrentTab());
                break;
            case Edge:
                mTabHost.setCurrentTabByTag(getResources().getString(R.string.info_edge));
                snapToScreen(mTabHost.getCurrentTab());
                break;
            case TDSCDMA:
                mTabHost.setCurrentTabByTag(getResources().getString(R.string.info_tdscdma));
                snapToScreen(mTabHost.getCurrentTab());
                break;
            case TDHspaPlus:
            	mTabHost.setCurrentTabByTag(getResources().getString(R.string.info_tdhspa));
                snapToScreen(mTabHost.getCurrentTab());
                break;
            case TcpIpPcap:
            	mTabHost.setCurrentTabByTag(getResources().getString(R.string.info_tcpip));
                snapToScreen(mTabHost.getCurrentTab());
                break;
            case LTECA:
            	mTabHost.setCurrentTabByTag(getResources().getString(R.string.info_ca));
            	snapToScreen(mTabHost.getCurrentTab());
            	break;
            case LTE4T4R:
            	mTabHost.setCurrentTabByTag(getResources().getString(R.string.info_4t4r));
            	snapToScreen(mTabHost.getCurrentTab());
            	break;
            case WLAN:
            	mTabHost.setCurrentTabByTag(getResources().getString(R.string.info_wlan));
                	if(!WifiScanner.isScannerWifi()){
                		WifiScanner.setScannerWifi(true);
                		WifiScanner.instance(this).startScan();
                	}
            	snapToScreen(mTabHost.getCurrentTab());
            	break;
            default:
                break;
        }
    }
    
    /**
     * 当前显示页面所属网络组是否改变
     * @param showType
     * @return
     */
    private boolean currentGroupChange(ShowInfoType showType){
        return TraceInfoInterface.currentShowChildTab.getNetGroup() != showType.getNetGroup();
    }
    
    /**
     * 根据当前网络，判断Tab标签应该默认为哪一个<BR>
     * [功能详细描述]
     */
    private void byNetWorkTocurrentTab(){
    	//twq20130724 当前显示页不在WLAN下才执行网络切换时的
		if (TraceInfoInterface.currentShowChildTab != WalkStruct.ShowInfoType.WLAN) {
			int networkType = MyPhoneState.getInstance().getNetWorkType(
					ParamActivity.this);
			switch (networkType) {
			case TelephonyManager.NETWORK_TYPE_GPRS:
			case TelephonyManager.NETWORK_TYPE_EDGE:
				if (currentGroupChange(ShowInfoType.Gsm)) {
					TraceInfoInterface.currentShowChildTab = ShowInfoType.Gsm;
				}
				break;
			case TelephonyManager.NETWORK_TYPE_UMTS:
				if (currentGroupChange(ShowInfoType.Umts)) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Umts;
				}
				break;
			case TelephonyManager.NETWORK_TYPE_HSDPA:
			case TelephonyManager.NETWORK_TYPE_HSUPA:
			case TelephonyManager.NETWORK_TYPE_HSPA:
			case 15: // HSPA+
				if (Deviceinfo.getInstance().getNettype() == NetType.TDSCDMA
						.getNetType()
						|| Deviceinfo.getInstance().getNettype() == NetType.LTETDD
								.getNetType()) {
					if (currentGroupChange(ShowInfoType.TDSCDMA)) {
						TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.TDSCDMA;
					}
				} else {
					if (currentGroupChange(ShowInfoType.Umts)) {
						TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Umts;
					}
				}
				break;
			case TelephonyManager.NETWORK_TYPE_CDMA:
			case TelephonyManager.NETWORK_TYPE_1xRTT:
				if (currentGroupChange(ShowInfoType.Cdma)) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Cdma;
				}
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
			case 12: // TelephonyManager.NETWORK_TYPE_EVDO_B:
				if (currentGroupChange(ShowInfoType.EvDo)) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.EvDo;
				}
				break;
			case 13:
				if (currentGroupChange(ShowInfoType.LTE)) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.LTE;
				}
				break;
			case 14: // EHRPD
				break;
			case 16:
			case 17:
				if (currentGroupChange(ShowInfoType.TDSCDMA)) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.TDSCDMA;
				}
				break;
			}
		}
    }
    
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param tabId
     * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
     */
    @Override
    public void onTabChanged(String tabId) {
        
    	if (tabId.equals(getResources().getString(R.string.info_gsm))) {
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Gsm;
        } else if (tabId.equals(getResources().getString(R.string.info_umts))) {
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Umts;
        } else if (tabId.equals(getResources().getString(R.string.info_hspa))) {
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Hspa;
        } else if (tabId.equals(getResources().getString(R.string.info_hspa_plus))) {
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.HspaPlus;
        } else if (tabId.equals(getResources().getString(R.string.info_lte))) {
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.LTE;
        } else if (tabId.equals(getResources().getString(R.string.info_cdma))) {
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Cdma;
        } else if (tabId.equals(getResources().getString(R.string.info_evdo))) {
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.EvDo;
        } else if (tabId.equals(getResources().getString(R.string.info_edge))) {
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Edge;
        } else if (tabId.equals(getResources().getString(R.string.info_tdscdma))) {
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.TDSCDMA;
        } else if (tabId.equals(getResources().getString(R.string.info_tdhspa))) {
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.TDHspaPlus;
        } else if (tabId.equals(getResources().getString(R.string.info_tcpip))) {
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.TcpIpPcap;
        } else if (tabId.equals(getResources().getString(R.string.info_ca))){
        	TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.LTECA;
        }else if (tabId.equals(getResources().getString(R.string.info_4t4r))){
        	TraceInfoInterface.currentShowChildTab = ShowInfoType.LTE4T4R;
        }
        else if (tabId.equals(getResources().getString(R.string.info_wlan))) {
        	if(!WifiScanner.isScannerWifi()){
        		WifiScanner.instance(this).startScan();
        		WifiScanner.setScannerWifi(true);
        	}
        	TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.WLAN;
        }
        
        if( WifiScanner.isScannerWifi() && !tabId.equals(getResources().getString(R.string.info_wlan))){
        	WifiScanner.setScannerWifi(false);
        	WifiScanner.instance(this).stopScan();
        }
        
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param refreshType
     * @see com.walktour.framework.view.RefreshEventManager.RefreshEventListener#onRefreshed(com.walktour.framework.view.RefreshEventManager.RefreshType)
     */
    @Override
    public void onRefreshed(RefreshType refreshType,Object object) {
        switch (refreshType) {
            case REFRESH_PARAM_TAB:
                byNetWorkTocurrentTab();
                snapToScreenTab();
                
                break;
            
            default:
                break;
        }
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @see android.app.ActivityGroup#onDestroy()
     */
    @Override
    protected void onDestroy() {
    	LogUtil.i("ParamActivity", "onDestroy");
        RefreshEventManager.removeRefreshListener(this);
        super.onDestroy();
    }

	@Override
	public void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		LogUtil.i("ParamActivity", "onDetachedFromWindow");
		super.onDetachedFromWindow();
		if( WifiScanner.isScannerWifi()){
			WifiScanner.setScannerWifi(false);
        	WifiScanner.instance(this).stopScan();
        }
	}
    
	@Override
    protected void onSaveInstanceState(Bundle outState) {
		LogUtil.i("ParamActivity","onSaveInstanceState");
        //No call for super(). Bug on API Level > 11.
    }
    
    
}
