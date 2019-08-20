package com.walktour.gui.applet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.walktour.framework.ui.BasicTabActivity;
import com.walktour.gui.R;

import java.util.ArrayList;

/**
 * 定制的TabActivity
 * 
 * */
public class MyTabActivity extends BasicTabActivity {
	//顶部选择栏目
	private TabHost myTabhost;
	private LinearLayout layout;
	private ArrayList<Button> buttonList;
	//记录当前打开第几个标签
	private static int indexPos ;
	
	private Intent[] activityClass;
	private String[] buttonTexts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * 初始化所有界面元素
	 * @param intents 所有子Activity
	 * @param buttonTexts 选择子页面的按钮
	 * */
	protected void initView(Intent[] intents ,String[] buttonTexts){
		this.activityClass = intents;
		this.buttonTexts = buttonTexts;
		findView();
	}
	
	private void findView(){
		setContentView(R.layout.total_toplist); 
		layout = (LinearLayout)findViewById(R.id.linelayout);
		
		myTabhost = (TabHost) findViewById(android.R.id.tabhost);
		
		for( int i=0;i<activityClass.length;i++){
			//添加Tab子页面
			myTabhost.addTab(myTabhost.newTabSpec( buttonTexts[i] )
					.setIndicator( buttonTexts[i] ).setContent( activityClass[i] ) );
			//添加顶部选择按钮
			Button btn = new Button(this);
			btn.setBackgroundResource(R.drawable.background_mybutton);
			btn.setLayoutParams(new LayoutParams(100, 64));
			btn.setPadding(6, 5, 6, 5);
			btn.setText( buttonTexts[i] );
			btn.setTextColor(getResources().getColor(R.color.white));
			layout.addView(btn);
		}
		
		//设置当前显示页
		myTabhost.setCurrentTab(indexPos); 
		   //添加tab改变事件
	 	myTabhost.setOnTabChangedListener(new OnTabChangeListener(){
				@Override
				public void onTabChanged(String tabId) {
					setButton();
				}
	 	});
		
    	//生成
    	genTopBar();
    	setButton();
	}
	
    private void genTopBar(){
    	
        buttonList = new ArrayList<Button>();
     	int length = layout.getChildCount();
     	//buttons = new Button[length];
     	for(int i = 0;i<length;i++)
     	{
     		Button button = (Button)layout.getChildAt(i);
     		buttonList.add(button);
     		button.setOnClickListener(btnListener);
     	}
     }//end method genTopBar
     
     /**
      * 设定选定的Button
      * */
     private void setButton(){
     	for(int i=0;i<buttonList.size();i++ ){
     		if( i==myTabhost.getCurrentTab() ){
     			buttonList.get(i).setSelected(true);
     			buttonList.get(i).requestFocus();
     			indexPos = i;
     		}
     		else{
     			buttonList.get(i).setSelected(false);
     		}
     	}
     }
     
     /**
      * tab按钮点击事件
      * */
     private OnClickListener btnListener = new  OnClickListener(){
 		@Override
 		public void onClick(View v) {
 			int tab =0;
 			
 			//判断点击第几个button
 			int count = buttonList.size();
 			for(int i = 0;i<count;i++)
 			{
 				
 				Button button  = buttonList.get(i);
 				if(((Button)v).getText().toString().equals(button.getText().toString()))
 				{
 					tab = i;
 				}
 			}
 			myTabhost.setCurrentTab( tab );
 		}
     	
     };
}
