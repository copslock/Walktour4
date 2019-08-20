package com.walktour.gui.applet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;

import com.walktour.gui.R;

/**
 * 颜色拾取器
 * @author qihang.li
 * */
 public class ColorPicker implements SeekBar.OnSeekBarChangeListener{
		private AlertDialog dialog ;
		private Context context;
		
		//界面widgets
		private SeekBar seekBar01;
		private SeekBar seekBar02;
		private SeekBar seekBar03;
		private SeekBar seekBar04;
		private Button button01;
		private Button button02;
		private Button button03;
		private Button button04;
		
		//颜色和SeekBar的进度对应
		private int color1;int pro1;
		private int color2;int pro2;
		private int color3;int pro3;
		private int color4;int pro4;
		
		
		public ColorPicker(Context context){
			this.context = context;
			this.color1 = Color.RED;this.pro1=0;
			this.color2 = Color.BLUE;this.pro2=5;
			this.color3 = Color.YELLOW;this.pro3=2;
			this.color4 = Color.GREEN;this.pro4=3;
		}
		
		public ColorPicker(Context context,int color1,int color2,int color3,int color4){
			this.context = context;
			switch(color1){
				case Color.RED:this.color1=Color.RED;pro1=0;break;
				case Color.WHITE:this.color1=Color.WHITE;pro1=1;break;
				case Color.YELLOW:this.color1=Color.YELLOW;pro1=2;break;
				case Color.GREEN:this.color1=Color.GREEN;pro1=3;break;
				case Color.CYAN:this.color1=Color.CYAN;pro1=4;break;
				case Color.BLUE:this.color1=Color.BLUE;pro1=5;break;
				case Color.MAGENTA:this.color1=Color.MAGENTA;pro1=6;break;
				default:this.color1 = Color.RED;pro1=0;break;
			}
			switch(color2){
				case Color.RED:this.color2=Color.RED;pro2=0;break;
				case Color.WHITE:this.color2=Color.WHITE;pro2=1;break;
				case Color.YELLOW:this.color2=Color.YELLOW;pro2=2;break;
				case Color.GREEN:this.color2=Color.GREEN;pro2=3;break;
				case Color.CYAN:this.color2=Color.CYAN;pro2=4;break;
				case Color.BLUE:this.color2=Color.BLUE;pro2=5;break;
				case Color.MAGENTA:this.color2=Color.MAGENTA;pro2=6;break;
				default:this.color2 = Color.BLUE;pro2=5;break;
			}
			switch(color3){
				case Color.RED:this.color3=Color.RED;pro3=0;break;
				case Color.WHITE:this.color3=Color.WHITE;pro3=1;break;
				case Color.YELLOW:this.color3=Color.YELLOW;pro3=2;break;
				case Color.GREEN:this.color3=Color.GREEN;pro3=3;break;
				case Color.CYAN:this.color3=Color.CYAN;pro3=4;break;
				case Color.BLUE:this.color3=Color.BLUE;pro3=5;break;
				case Color.MAGENTA:this.color3=Color.MAGENTA;pro3=6;break;
				default:this.color3 = Color.YELLOW;pro3=2;break;
			}
			switch(color4){
				case Color.RED:this.color4=Color.RED;pro4=0;break;
				case Color.WHITE:this.color4=Color.WHITE;pro4=1;break;
				case Color.YELLOW:this.color4=Color.YELLOW;pro4=2;break;
				case Color.GREEN:this.color4=Color.GREEN;pro4=3;break;
				case Color.CYAN:this.color4=Color.CYAN;pro4=4;break;
				case Color.BLUE:this.color4=Color.BLUE;pro4=5;break;
				case Color.MAGENTA:this.color4=Color.MAGENTA;pro4=6;break;
				default:this.color4 = Color.GREEN;pro4=3;break;
			}
		}
		
		public int getColor1 (){
			return this.color1;
		}
		
		public int getColor2 (){
			return this.color2;
		}
		
		public int getColor3 (){
			return this.color3;
		}
		
		public int getColor4 (){
			return this.color4;
		}
				
		public AlertDialog getDialog(){
			//从XML获取AlertDialog对象的内容:EditText
	    	LayoutInflater factory = LayoutInflater.from(this.context);
	        final View choiceView = factory.inflate(R.layout.alert_dialog_colorpicker, null);
	        //绑定Layout里面的ListView  
	        ListView list = (ListView) choiceView.findViewById(R.id.ListView01); 
			 //拾色器相关的widgets
	        button01  = (Button) choiceView.findViewById(R.id.Button01);
	        button02  = (Button) choiceView.findViewById(R.id.Button02);
	        button03  = (Button) choiceView.findViewById(R.id.Button03);
	        button04  = (Button) choiceView.findViewById(R.id.Button04);
	        button01.setText("Color1");
	        button02.setText("Color2");
	        button03.setText("Color3");
	        button04.setText("Color4");
	        button01.setTextColor(color1);
	        button02.setTextColor(color2);
	        button03.setTextColor(color3);
	        button04.setTextColor(color4);
			seekBar01 = (SeekBar) choiceView.findViewById(R.id.SeekBar01);
			seekBar02 = (SeekBar) choiceView.findViewById(R.id.SeekBar02);
			seekBar03 = (SeekBar) choiceView.findViewById(R.id.SeekBar03);
			seekBar04 = (SeekBar) choiceView.findViewById(R.id.SeekBar04);
			//设置颜色为黑色到白色
			int max = 6 ;
			seekBar01.setMax( max );
			seekBar02.setMax( max );
			seekBar03.setMax( max );
			seekBar04.setMax( max );
			
			//根据初始化的颜色确定进度条位置
			seekBar01.setProgress( pro1 );
			seekBar02.setProgress( pro2 );
			seekBar03.setProgress( pro3 );
			seekBar04.setProgress( pro4 );
			
			//添加滑动事件
			seekBar01.setOnSeekBarChangeListener(this);
			seekBar02.setOnSeekBarChangeListener(this);
			seekBar03.setOnSeekBarChangeListener(this);
			seekBar04.setOnSeekBarChangeListener(this);
			
	        dialog = new AlertDialog.Builder(context)
	        .setView(choiceView)
	        .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
				
				
				public void onClick(DialogInterface dialog, int which) {
					//ChartProperty.getInstance().setColorOfLine(new int[]{color1,color2,color3,color4});
				}
			})
			.setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
				
				
				public void onClick(DialogInterface dialog, int which) {
					
				}
			})
	        .create();
	        return dialog;		
		}//end method getDialog

		
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			switch(seekBar.getId()){
			
			case R.id.SeekBar01:
				switch( progress ){
					case 0:color1=Color.RED;break; 
					case 1:color1=Color.WHITE;break; 
					case 2:color1=Color.YELLOW;break; 
					case 3:color1=Color.GREEN;break; 
					case 4:color1=Color.CYAN;break; 
					case 5:color1=Color.BLUE;break; 
					case 6:color1=Color.MAGENTA;break; 
				}
				button01.setTextColor( color1 );
				break;
			
			case R.id.SeekBar02:
				switch( progress ){
					case 0:color2=Color.RED;break; 
					case 1:color2=Color.WHITE;break; 
					case 2:color2=Color.YELLOW;break; 
					case 3:color2=Color.GREEN;break; 
					case 4:color2=Color.CYAN;break; 
					case 5:color2=Color.BLUE;break; 
					case 6:color2=Color.MAGENTA;break; 
				}
				button02.setTextColor( color2 );
				break;
				
			case R.id.SeekBar03:
				switch( progress ){
					case 0:color3=Color.RED;break; 
					case 1:color3=Color.WHITE;break; 
					case 2:color3=Color.YELLOW;break; 
					case 3:color3=Color.GREEN;break; 
					case 4:color3=Color.CYAN;break; 
					case 5:color3=Color.BLUE;break; 
					case 6:color3=Color.MAGENTA;break; 
				}
				button03.setTextColor( color3 );
				break;
				
			case R.id.SeekBar04:
				switch( progress ){
					case 0:color4=Color.RED;break; 
					case 1:color4=Color.WHITE;break; 
					case 2:color4=Color.YELLOW;break; 
					case 3:color4=Color.GREEN;break; 
					case 4:color4=Color.CYAN;break; 
					case 5:color4=Color.BLUE;break; 
					case 6:color4=Color.MAGENTA;break; 
				}
				button04.setTextColor( color4 );
				break;
			}
		}

		
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
    	  	  	
    }//end  inner class   