/*
 * 文件名: InfoActivity.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2012-6-25
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.gui.map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.ShowInfoType;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.LayoutChangeListener;
import com.walktour.framework.view.ScroollTabActivity;
import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-6-25] 
 */
public class InfoActivity extends ScroollTabActivity implements
        LayoutChangeListener, OnTabChangeListener {
    private static final String tag = "InfoActivity";
    
    // 滑动动画执行时间  
    private final int DELAY_TIME = 500;
    
    //private LinearLayout tab5Layout;
    private ImageView img6;

	private ArrayList<ShowInfoType> pageList;

	private SharedPreferences mPreferences;

	private ImageView img4;
	
	
    /**
     * 参数界面实际展示列表项
     */
    private List<ParamTab> paramTabList = new ArrayList<ParamTab>();

	private int pageCount = 0;

	private LinearLayout tab1_layout;

	private LinearLayout tab2_layout;

	private LinearLayout tab3_layout;

	private LinearLayout tab4_layout;

	private LinearLayout tab5_layout;

	private LinearLayout tab6_layout;

	private ImageView img5;

    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param savedInstanceState
     * @see com.walktour.framework.ui.BasicActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @see com.walktour.framework.view.ScroollTabActivity#initView()
     */
    @Override
    public void initView() {
        setContentView(R.layout.info_info);
        ApplicationModel appModel = ApplicationModel.getInstance();
        mTabHost = (TabHost) findViewById(R.id.child_tabhost);
        //tab5Layout = initLinearLayout(R.id.tab5Layout);
        mTabHost.setup(this.getLocalActivityManager());
        tab1 = initButton(R.id.tab1);
        tab2 = initButton(R.id.tab2);
        tab3 = initButton(R.id.tab3);
        tab4 = initButton(R.id.tab4);
        tab5 = initButton(R.id.tab5);
        tab6 = initButton(R.id.tab6);
        
        img4 = initImageView(R.id.img4);
        img5 = initImageView(R.id.img5);
        img6 = initImageView(R.id.img6);
        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        tab3.setOnClickListener(this);
        tab4.setOnClickListener(this);
        tab5.setOnClickListener(this);
        tab6.setOnClickListener(this);
        scrollTag = initImageView(R.id.top_bar_select);
        
        ParamTab evenParm = new ParamTab(getString(R.string.info_event), new Intent(InfoActivity.this, Event.class));
        paramTabList.add(evenParm);
        ParamTab l3msgParm = new ParamTab(getString(R.string.info_l3msg), new Intent(InfoActivity.this, L3Msg.class));
        paramTabList.add(l3msgParm);
        ParamTab chartParm = new ParamTab(getString(R.string.info_chart), new Intent(InfoActivity.this, Chart.class));
        paramTabList.add(chartParm);
        ParamTab dataParm = new ParamTab(getString(R.string.info_data), new Intent(InfoActivity.this, DataDashboardActivity.class));
        paramTabList.add(dataParm);
        //ParamTab voLte = new ParamTab(getString(R.string.info_volte), R.id.showLteView);
        if(appModel.getNetList().contains(WalkStruct.ShowInfoType.TCPIPCapture)){
        	ParamTab tcpip = new ParamTab(getString(R.string.info_tcpip), new Intent(InfoActivity.this, TcpIpListActivity.class));
        	paramTabList.add(tcpip);
        }
        
        if (appModel.getTaskList().contains(WalkStruct.TaskType.Stream)
                || appModel.getTaskList().contains(WalkStruct.TaskType.HTTPVS)) {
        	ParamTab vsParm = new ParamTab(getString(R.string.info_video), new Intent(InfoActivity.this, VideoRealPara.class));
        	paramTabList.add(vsParm);
        }
        
        tab4_layout = initLinearLayout(R.id.tab4Layout);
        tab5_layout = initLinearLayout(R.id.tab5Layout);
        tab6_layout = initLinearLayout(R.id.tab6Layout);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mCurScreen = 2;
        setTabBtnText();
        switch (pageCount) {
        case 3:
            tab4_layout.setVisibility(View.GONE);
            img4.setVisibility(View.GONE);
    	case 4:
            tab5_layout.setVisibility(View.GONE);
            img5.setVisibility(View.GONE);
    	case 5:
            tab6_layout.setVisibility(View.GONE);
            img6.setVisibility(View.GONE);
            break;
        default:
            break;
    }
        
        
    }
    
    
    
    /**
     * zhihui.lian
     */
    public void setTabBtnText() {
        Button[] tabs = { tab1, tab2, tab3, tab4, tab5, tab6};
        
        for (int i = 0; i < paramTabList.size() && i < 6; i++) {
            ParamTab paramTab = paramTabList.get(i);
            if(mPreferences.getBoolean("display_" + paramTab.paramBtnText, true)){
                tabs[pageCount].setText(paramTab.paramBtnText);
                Button btn = new Button(this);
                btn.setBackgroundResource(R.drawable.background_mybutton);
                btn.setPadding(3, 5, 3, 5);
                btn.setText(paramTab.paramBtnText);
                btn.setTextColor(getResources().getColor(R.color.white));
                
                if(paramTab.intent instanceof Intent){
                	mTabHost.addTab(mTabHost.newTabSpec(paramTab.paramBtnText)
                			.setIndicator(btn)
                			.setContent((Intent)paramTab.intent));
                }else{
                	mTabHost.addTab(mTabHost.newTabSpec(paramTab.paramBtnText)
                			.setIndicator(btn)
                			.setContent((Integer)paramTab.intent));
                }
                
                pageCount ++;
            }
        }
        mTabHost.setOnTabChangedListener(this);
    }
    
    
    
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @see android.app.ActivityGroup#onResume()
     */
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("--", "info");
        Message msg = new Message();
        msg.what = -1;
        initSelectTabHandle.sendMessageDelayed(msg, DELAY_TIME);
        
        if (TraceInfoInterface.currentShowChildTab.equals(WalkStruct.ShowInfoType.VideoPlay)) {
            VideoRealPara view = (VideoRealPara) getLocalActivityManager().getActivity("VS");
            //        	if(view != null){
            //        		view.startPlay();
            //        	}
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.v("VSRealTimePara", "--onPause--");
        VideoRealPara view = (VideoRealPara) getLocalActivityManager().getActivity("VS");
        if (view != null){
            view.stopPlay();
        }
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Log.i("VSRealTimePara", "--onDestroy--");
        super.onDestroy();
        VideoRealPara view = (VideoRealPara) getLocalActivityManager().getActivity("VS");
        if (view != null) {
            view.stopPlay();
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        LogUtil.i(tag, "onSaveInstanceState");
        //No call for super(). Bug on API Level > 11.
    }
    
    /** 
     * 初始化选中Tab覆盖图片的Handler 
     */
    private Handler initSelectTabHandle = new Handler() {
        public void handleMessage(Message msg) {
            switch (TraceInfoInterface.currentShowChildTab) {
                case Event:
                	Log.i(tag, "event");
//                    InfoActivity.this.onClick(tab1);
                	mTabHost.setCurrentTabByTag(getResources().getString(R.string.info_event));
                    snapToScreen(mTabHost.getCurrentTab());
                    break;
                case Chart:
                	Log.i(tag, "Chart");
//                    InfoActivity.this.onClick(tab3);
//                    TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Chart;
                	mTabHost.setCurrentTabByTag(getResources().getString(R.string.info_chart));
                    snapToScreen(mTabHost.getCurrentTab());
                    break;
                case Data:
                	Log.i(tag, "Data");
//                    InfoActivity.this.onClick(tab4);
//                    TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Data;
                	mTabHost.setCurrentTabByTag(getResources().getString(R.string.info_data));
                    snapToScreen(mTabHost.getCurrentTab());
                    break;
                case L3Msg:
                	Log.i(tag, "L3Msg");
//                    InfoActivity.this.onClick(tab2);
//                    TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.L3Msg;
                	mTabHost.setCurrentTabByTag(getResources().getString(R.string.info_l3msg));
                    snapToScreen(mTabHost.getCurrentTab());
                    break;
                case VideoPlay:
//                    InfoActivity.this.onClick(tab6);
//                    TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.VideoPlay;
                	//mTabHost.setCurrentTabByTag(getResources().getString(R.string.info_video));
                	mTabHost.setCurrentTabByTag(getResources().getString(R.string.info_tcpip));
                	snapToScreen(mTabHost.getCurrentTab());
                    break;
                case VoLTE:
//                	InfoActivity.this.onClick(tab5);
//                	TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.VoLTE;
                	mTabHost.setCurrentTabByTag(getResources().getString(R.string.info_volte));
                    snapToScreen(mTabHost.getCurrentTab());
                	break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param tabId
     * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
     */
    @Override
    public void onTabChanged(String tabId) {
        if (tabId.equals(getString(R.string.info_event))) {
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Event;
        } else if (tabId.equals(getString(R.string.info_chart))) {
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Chart;
        } else if (tabId.equals(getString(R.string.info_data))) {
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Data;
        } else if (tabId.equals(getString(R.string.info_l3msg))) {
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.L3Msg;
        } else if (tabId.equals(getString(R.string.info_video))) {
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.VideoPlay;
        } else if (tabId.equals(getString(R.string.info_volte))) {
            TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.VoLTE;
        }else if(tabId.equals(getString(R.string.info_tcpip))){
        	TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.TcpIpPcap;
        }
        VideoRealPara view = (VideoRealPara) getLocalActivityManager().getActivity("VS");
        LogUtil.i(tag,"onTabChanged:"+tabId );
		InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(InfoActivity.this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        if (view != null) {
            view.stopPlay();
        }
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
         	getLocalActivityManager().getCurrentActivity().onKeyUp(keyCode, event);
            return super.onKeyUp(keyCode, event);
    }
    
    private class ParamTab{
        /**
         * [构造简要说明]
         * @param paramBtnText
         * @param intent
         */
        public ParamTab(String paramBtnText, Object intent) {
            this.paramBtnText = paramBtnText;
            this.intent = intent;
        }

        public String paramBtnText;
        
        public Object intent; 
        
    }
    
}
