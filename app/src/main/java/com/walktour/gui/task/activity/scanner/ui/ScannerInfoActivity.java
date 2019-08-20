
package com.walktour.gui.task.activity.scanner.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkStruct.ShowInfoType;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.LayoutChangeListener;
import com.walktour.framework.view.ScroollTabActivity;
import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 扫频仪信息界面
 *  zhihui.lian
 */
public class ScannerInfoActivity extends ScroollTabActivity implements
        LayoutChangeListener, OnTabChangeListener {
    private static final String tag = "InfoActivity";
    
    // 滑动动画执行时间  
    private final int DELAY_TIME = 500;
    
    //private LinearLayout tab5Layout;
    private ImageView img6;

	private ArrayList<ShowInfoType> pageList;

	private SharedPreferences mPreferences;

	private ImageView img4;
	
	private ImageView img5;
	
    /**
     * 参数界面实际展示列表项
     */
    private List<ParamTab> paramTabList = new ArrayList<ParamTab>();

	private int pageCount = 0;


    
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
        setContentView(R.layout.scanner_info);
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
        tab7 = initButton(R.id.tab7);
        
        img4 = initImageView(R.id.img4);
        img5 = initImageView(R.id.img5);
        img6 = initImageView(R.id.img6);
        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        tab3.setOnClickListener(this);
        tab4.setOnClickListener(this);
        tab5.setOnClickListener(this);
        tab6.setOnClickListener(this);
        tab7.setOnClickListener(this);
        scrollTag = initImageView(R.id.top_bar_select);
        
        ParamTab evenParm = new ParamTab("Event", new Intent(ScannerInfoActivity.this, ScanEvent.class));
        paramTabList.add(evenParm);
        ParamTab cwParm = new ParamTab("CW", new Intent(ScannerInfoActivity.this, ScanRssiInfoView.class));
        paramTabList.add(cwParm);
        ParamTab vsParm = new ParamTab("ColorCode", new Intent(ScannerInfoActivity.this, ScanColorCodeActivity.class));
        paramTabList.add(vsParm);
        ParamTab l3msgParm = new ParamTab("C-Pilot", new Intent(ScannerInfoActivity.this, ScanCdmaTopNActivity.class));
        paramTabList.add(l3msgParm);
        ParamTab chartParm = new ParamTab("W-Pilot", new Intent(ScannerInfoActivity.this, ScanWcdmaPilotActivity.class));
        paramTabList.add(chartParm);
        ParamTab dataParm = new ParamTab("T-Pilot", new Intent(ScannerInfoActivity.this, ScanTdPilotActivity.class));
        paramTabList.add(dataParm);
        ParamTab voLte = new ParamTab("L-Pilot", new Intent(ScannerInfoActivity.this, ScanLteTopNActivity.class));
        paramTabList.add(voLte);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setTabBtnText();
        
        
    }
    
    
    
    /**
     * zhihui.lian
     */
    public void setTabBtnText() {
        Button[] tabs = { tab1, tab2, tab3, tab4, tab5, tab6,tab7};
        
        for (int i = 0; i < paramTabList.size() && i < 7; i++) {
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
    
    
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        LogUtil.i(tag, "onSaveInstanceState");
        //No call for super(). Bug on API Level > 11.
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

	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		
	}
    
}
