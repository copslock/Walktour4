package com.walktour.gui.map;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.setting.SysChart;

/**
 * 全参数图表
 * @author tangwq
 */
public class Chart extends BasicActivity {
//	public static final String MyAction1 = "switch";
//	public static final String MyAction2 = "line1para";
//	public static final String MyAction3 = "line1color";
//	public static final String MyAction4 = "line2para";
//	public static final String MyAction5 = "line2color";
//	public static final String MyAction6 = "line3para";
//	public static final String MyAction7 = "line3color";
//	public static final String MyAction8 = "line4para";
//	public static final String MyAction9 = "line4color";
//	public static final String MyAction10 = "paraset";
	
	public static final int CHART_PAR_COUNT = 4;//显示4条曲线

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.map_chartview);
//		super.addChildView(new ChartView(this,1));
		setContentView(new ChartView(this,1));
//		Intent pdfIntent = new Intent(this, ParmPdfActivity.class);
//		View v1 = getLocalActivityManager().startActivity("", pdfIntent).getDecorView();
//		super.addChildView(v1);
//		super.addChildView(new ChartView(this,2));   屏蔽自定义参数
		//registerForContextMenu((ChartView) findViewById(R.id.chart_view));
//		super.refreshSwicthTag(0);
	}
	
		@Override//添加菜单
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		MenuInflater mInflater = getMenuInflater();
		mInflater.inflate(R.menu.info_chart, menu);		
		return true;
	}
    
    @Override//菜单点击事件
	public boolean onOptionsItemSelected(MenuItem item){    	
		super.onOptionsItemSelected(item);
		
		switch( item.getItemId() ){
			case R.id.chart_seeting:
				Intent intent = new Intent(Chart.this,SysChart.class);
				startActivity( intent );
				break;
		}
					
		return true;
	}
	
	/**
	 * 发送消息到ChartView
	 * *//*
	private void sendBroadcastToChartView(String myAction){
		Intent intent = new Intent();
		intent.setAction (myAction);
		sendBroadcast(intent);
	}
*/

	/**
	 * 菜单事件处理
	 * *//*
    @Override //弹出上下文菜单item点击事件（ 注：弹出上下文菜单已在ChartView中创建）
    public boolean onContextItemSelected(MenuItem item) {  
    	//获取当前测试任务
    	final WalkStruct.TaskType taskType = ApplicationModel.getInstance().getCurrentTask();
    	
    	switch (item.getItemId()) {
			case R.id.chart_switch://曲线/柱状图切换
				sendBroadcastToChartView(MyAction1);break;
			case R.id.chart_color://曲线/柱状图颜色
					ColorPicker colorPicker = new ColorPicker(this,
							ChartProperty.getInstance().getColorOfLine1(),
							ChartProperty.getInstance().getColorOfLine2(),
							ChartProperty.getInstance().getColorOfLine3(),
							ChartProperty.getInstance().getColorOfLine4()
					);
					colorPicker.getDialog().show();
					break;
			case R.id.chart_linecontent://跳转到按测试任务区分的设置页面
										//设置曲线/柱状图要显示内容:对应测试任务的参数
					
					if(taskType!=null){
						new MyDialog(taskType).getDialog().show();
					}else{
						Intent intent =  new Intent(Chart.this,SysParameter.class);
						startActivity(intent);
					}
					break;
			case R.id.chart_tablecontent://显示对话框
										//设置表格要显示的内容:对应测试任务的参数
				if(taskType==null){
					Intent intent =  new Intent(Chart.this,SysParameter.class);
					startActivity(intent);
				}else{
						//读取当前任务的参数列表
						ArrayList<PropertyChart>  properties = 
							ChartProperty.getInstance().getProperties(taskType);
						String [] propertyNames = new String[ properties.size() ];
						final boolean [] choiced = new boolean[ propertyNames.length ];
						for(int i=0;i<propertyNames.length;i++){
							propertyNames [i]  = properties.get(i).getName();
							choiced[i] = properties.get(i).isDisplayOnTable();
						}
						new AlertDialog.Builder(Chart.this)
				    	.setTitle( taskType.toString() )
				    	.setMultiChoiceItems(propertyNames, 
				    			choiced, new DialogInterface.OnMultiChoiceClickListener() {
							
							public void onClick(DialogInterface dialog, int which, boolean isChecked) {
								choiced[which] = isChecked;
							}
						})
						
						.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								//更新数据
								ChartProperty.getInstance().setDisplayInTable(taskType.toString(), choiced);
								//更新显示内容
								ShowInfo.getInstance().SetChartProperty();
							}
						})
						.setNeutralButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						})
						.show();
				}//end if and else
				break;
		}//end switch
	    return super.onContextItemSelected(item); 
    }
    
    
    *//**
     * @内部类:弹出窗口类
     * @功能 设置图表曲线显示内容
     * @author qihang.li
     * *//*
    private class MyDialog {
		private AlertDialog dialog ;
		private WalkStruct.TaskType taskType;
		
		public MyDialog(WalkStruct.TaskType taskType){
			this.taskType = taskType;
		}
		
		*//**
		 * @返回 AlertDialog对象  
		 * *//*
		public AlertDialog getDialog(){
			if(taskType==null){
				return null;
			}
			
			//从XML获取AlertDialog对象的内容:EditText
	    	LayoutInflater factory = LayoutInflater.from(Chart.this);
	        final View choiceView = factory.inflate(R.layout.listview_with_toolbar, null);
	        //绑定Layout里面的ListView  
	        ListView list = (ListView) choiceView.findViewById(R.id.ListView01); 
	        //添加底部按钮
			final Button btn_ok = (Button) choiceView.findViewById(R.id.Button01);
			final Button btn_cancle = (Button) choiceView.findViewById(R.id.Button02);
			btn_ok.setText( getString(R.string.str_ok) );
			btn_cancle.setText( getString(R.string.str_cancle) );
			
			//读取当前任务的参数列表
			ArrayList<PropertyChart>  properties = 
				ChartProperty.getInstance().getProperties(taskType);
			String [] propertyNames = new String[ properties.size() ];
			final boolean [] choiced = new boolean[ propertyNames.length ];
			for(int i=0;i<propertyNames.length;i++){
				propertyNames [i]  = properties.get(i).getName();
				choiced[i] = properties.get(i).isDisplayOnChart();
			}			
			list.setAdapter(new ArrayAdapter<String>(Chart.this,
	                android.R.layout.simple_list_item_multiple_choice,
	                propertyNames ));
			
			
			//设置list_view 的属性
	        list.setItemsCanFocus(false);
	        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	        
	        for(int i=0;i<choiced.length;i++){
				list.setItemChecked(i, choiced[i]);
			}
	        
	        //
	        list.setOnItemClickListener(new OnItemClickListener(){
				
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					choiced[arg2] = !choiced[arg2];
					int c =0;
					for(int i=0;i<choiced.length;i++){
						if(choiced[i]){c++;}
					}
					btn_ok.setEnabled(c<=CHART_PAR_COUNT);
					//btn_ok.setText(String.valueOf(c));
				}
	        });
	        
	        btn_ok.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					ChartProperty.getInstance().setDisplayInChart(taskType.toString(), choiced);
					dialog.dismiss();
					ShowInfo.getInstance().SetChartProperty();
				}
	        	
	        });
	        
	        btn_cancle.setOnClickListener(new OnClickListener(){
				
				public void onClick(View v) {
					dialog.dismiss();								
				}
	        	
	        });
	        
	        dialog = new AlertDialog.Builder(Chart.this)
	        .setView(choiceView)
	        .setTitle(ApplicationModel.getInstance().getCurrentTask().toString())
	        .create();
	        return dialog;		
		}//end method getDialog
    	  	  	
    }//end  inner class   
*/
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }
}
