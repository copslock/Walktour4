package com.walktour.gui.analysis.csfb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dinglicom.listviewtest.ExpandCollapseAnimation;
import com.walktour.control.VoiceAnalyseInterface;
import com.walktour.gui.R;
import com.walktour.model.CsfbFaildEventModel;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * 伸展Listview适配器,实现单选伸展,以及动画
 * @author zhihui.lian
 *
 */
@SuppressLint("InflateParams")
public class CsfbDetailAdapter extends BaseAdapter {
	
	
	private List<CsfbFaildEventModel> list = new ArrayList<CsfbFaildEventModel>();
		
	 // 上下文
    private  Context context;
    
    
    private View lastOpen = null;
    
	//记住最后一次打开状态的位置
	private int lastOpenPosition = -1;
	
	//存储点击打开的items
	private BitSet openItems = new BitSet();
	
	//存储动画滑动的高度
	private final SparseIntArray viewHeights = new SparseIntArray(10);

	private View txtView;
	
	private CsfbFaildEventModel csfbFaildEventModel = null;
		

     // 构造器
     public CsfbDetailAdapter(Context context , List<CsfbFaildEventModel> list) {
         this.context = context;
         this.list = list;
         
     }

     @Override
     public int getCount() {
         return null==list?0:list.size();
     }

     @Override
     public Object getItem(int position) {
         return null==list?null:list.get(position);
     }

     @Override
     public long getItemId(int position) {
         return position;
     }
     
     
     

		@Override
		public boolean isEnabled(int position) {
				return false;
		}

		@Override
		public View getView(final int position, View convertView, final ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.expandable_list_item,null);
				// 设置item中indexText的文本
				viewHolder.ItemTitle = (TextView) convertView.findViewById(R.id.text);
				viewHolder.timeDelay = (TextView) convertView.findViewById(R.id.timeDelay);
				viewHolder.expandable = (LinearLayout)convertView.findViewById(R.id.expandable);
				viewHolder.testAdd = (LinearLayout)convertView.findViewById(R.id.testAdd);
				viewHolder.expandable_toggle_button = (ImageView)convertView.findViewById(R.id.expandable_toggle_button);
				viewHolder.event_txt = (TextView)convertView.findViewById(R.id.event_txt); 
				viewHolder.netWork_txt = (TextView)convertView.findViewById(R.id.newwork_txt);
				viewHolder.eventName = (TextView)convertView.findViewById(R.id.event_name_txt);
				viewHolder.reson_txt = (TextView)convertView.findViewById(R.id.reson_txt);
				viewHolder.csfb_open_l3detail = (ImageView)convertView.findViewById(R.id.csfb_open_l3detail);
				
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
				csfbFaildEventModel = list.get(position);
				
				LinearLayout  rl = (LinearLayout) convertView.findViewById(R.id.item);
				if(csfbFaildEventModel.getNodeShowType() == 1){
					viewHolder.event_txt.setVisibility(View.GONE);
					viewHolder.netWork_txt.setVisibility(View.GONE);
					viewHolder.ItemTitle.setTextColor(context.getResources().getColor(R.color.gray));
					viewHolder.expandable_toggle_button.setBackgroundResource(R.drawable.csfb_gray);
					viewHolder.eventName.setTextColor(context.getResources().getColor(R.color.gray));
					rl.setEnabled(false);
				}else{
					viewHolder.event_txt.setVisibility(View.VISIBLE);
					viewHolder.netWork_txt.setVisibility(View.VISIBLE);
					rl.setEnabled(true);
					viewHolder.ItemTitle.setTextColor(context.getResources().getColor(R.color.black));
					if(csfbFaildEventModel.getEventType() == VoiceAnalyseInterface.EVENT_TYPE_FAILD){
						viewHolder.expandable_toggle_button.setBackgroundResource(R.drawable.csfb_red);
						viewHolder.eventName.setTextColor(context.getResources().getColor(R.color.red));
					}else{
						viewHolder.expandable_toggle_button.setBackgroundResource(R.drawable.csfb_blue);
						viewHolder.eventName.setTextColor(context.getResources().getColor(R.color.csfb_txt_color));
					}
				}
				
			viewHolder.ItemTitle.setText(csfbFaildEventModel.getEventTimes());
			viewHolder.timeDelay.setText(csfbFaildEventModel.getDelayByLastEvent().length() == 0 ? "" :  
					"(" + csfbFaildEventModel.getDelayByLastEvent() + "s)");
					
			viewHolder.eventName.setText(csfbFaildEventModel.getEventMsg());
			viewHolder.event_txt.setText((csfbFaildEventModel.getDirection().equals("1") ? "(UL)" : "(DL)") + csfbFaildEventModel.getSignalMsg());
			viewHolder.netWork_txt.setText(csfbFaildEventModel.getCurrentNet());
			if(csfbFaildEventModel.getDisconnectReasion().equals("")){
				viewHolder.reson_txt.setVisibility(View.GONE);
			}else{
				viewHolder.reson_txt.setVisibility(View.VISIBLE);
				viewHolder.reson_txt.setText(csfbFaildEventModel.getDisconnectReasion());
			}
			viewHolder.expandable.setVisibility(openItems.get(position) == true ? View.VISIBLE :View.GONE);
			
			enableFor(rl,viewHolder.expandable,position,parent,viewHolder.testAdd);
			
			viewHolder.csfb_open_l3detail.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					v.setTag(list.get(position).getL3DetailStr());
					openDetailI.onClick(v);
				}
			});
			
			return convertView;
		}
		
		
		public OpenDetailI openDetailI;
		
		public void setOnClickListener(OpenDetailI openDetailI){
			this.openDetailI = openDetailI;
		}
		
		public interface OpenDetailI{
			 void onClick(View v);
		}
		
		
		
		
		
		/**
		 * 伸缩控制
		 * @param button
		 * @param target
		 * @param position
		 * @param parent
		 * @param viewLinearLayout
		 */
		private void enableFor(final View button, final View target, final int position,final ViewGroup parent,final LinearLayout viewLinearLayout) {
			if(target == lastOpen && position!=lastOpenPosition) {
				// lastOpen is recycled, so its reference is false
				lastOpen = null;
			}
			if(position == lastOpenPosition) {
				// re reference to the last view
				// so when can animate it when collapsed
				lastOpen = target;
			}
			int height = viewHeights.get(position, -1);
			if(height == -1) {
				viewHeights.put(position, target.getMeasuredHeight());
				updateExpandable(target,position,viewLinearLayout);
			} else {
				updateExpandable(target, position,viewLinearLayout);
			}

			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View view) {
					
					Animation a = target.getAnimation();
					target.measure(target.getWidth(), target.getHeight());
					if (a != null && a.hasStarted() && !a.hasEnded()) {

						a.setAnimationListener(new Animation.AnimationListener() {
							@Override
							public void onAnimationStart(Animation animation) {
							}

							@Override
							public void onAnimationEnd(Animation animation) {
								view.performClick();
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
							}
						});

					} else {

						target.setAnimation(null);

						int type = target.getVisibility() == View.VISIBLE
								? ExpandCollapseAnimation.COLLAPSE
								: ExpandCollapseAnimation.EXPAND;

						// remember the state
						if (type == ExpandCollapseAnimation.EXPAND) {
							openItems.set(position, true);
							getViewText(position,viewLinearLayout);
						} else {
							openItems.set(position, false);
							getViewText(position,viewLinearLayout);
						}
						// check if we need to collapse a different view
						if (type == ExpandCollapseAnimation.EXPAND) {
							if (lastOpenPosition != -1 && lastOpenPosition != position) {
								if (lastOpen != null) {
									animateView(lastOpen, ExpandCollapseAnimation.COLLAPSE,parent);
									
								}
								openItems.set(lastOpenPosition, false);
							}
							lastOpen = target;
							lastOpenPosition = position;
						} else if (lastOpenPosition == position) {
							lastOpenPosition = -1;
						}
						animateView(target, type,parent);
					}
				}
			});
		}
		
		
		
		
		/**
		 * 更新伸缩
		 * @param target
		 * @param position
		 * @param viewLinearLayout
		 */
		private void updateExpandable(View target, int position ,LinearLayout viewLinearLayout) {
			final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)target.getLayoutParams();
			if(openItems.get(position)) {
				target.setVisibility(View.VISIBLE);
				params.bottomMargin = 0;
				getViewText(position,viewLinearLayout);
			} else {
				target.setVisibility(View.GONE);
				params.bottomMargin = 0-viewHeights.get(position);
				getViewText(position,viewLinearLayout);
			}
		}
		
		
		/**
		 * 动态生成View,显示参数
		 * @param position
		 * @param addView
		 */
		private void getViewText(int position,LinearLayout viewLinearLayout){
			
			viewLinearLayout.removeAllViews();
			csfbFaildEventModel = list.get(position);
    		String para = csfbFaildEventModel.getParamValues();
			if(para.trim().length()!=0){
				String[]  paraStr = para.split("@@");
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
				for (int i = 0; i < paraStr.length; i+=2) {
					txtView = inflater.inflate(R.layout.csfb_para_item, null); 
					TextView text1 =  (TextView)txtView.findViewById(R.id.csfb_txt_1);
					TextView text2 =  (TextView)txtView.findViewById(R.id.csfb_txt_2);
					text1.setText(getColorBuilder(paraStr[i]));
						if (i + 1 < paraStr.length){
							text2.setText(getColorBuilder(paraStr[i + 1]));
						}
						viewLinearLayout.addView(txtView);
				}
			}
    	
    	}
		
		/**
		 * 设置前景色
		 * @param startIndex
		 * @param toIndex
		 * @param str
		 */
		private SpannableStringBuilder getColorBuilder(String str){
			SpannableStringBuilder builder = new SpannableStringBuilder(str);
			ForegroundColorSpan parmSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.info_param_color));
			ForegroundColorSpan whiteSpan = new ForegroundColorSpan(Color.GRAY);
			builder.setSpan(whiteSpan, 0, str.indexOf(":"), Spannable.SPAN_INCLUSIVE_INCLUSIVE);  
			builder.setSpan(parmSpan,  str.indexOf(":") + 1, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
			return builder;
		}
		
     
     
     private class ViewHolder {
         
		TextView eventName;

		TextView reson_txt;

		ImageView csfb_open_l3detail;
		ImageView expandable_toggle_button;

		TextView event_txt;

		TextView netWork_txt;

		TextView ItemTitle;
		TextView timeDelay;
		LinearLayout expandable;
		LinearLayout testAdd;
     } 
     
     
     /**
 	 *调用伸展动画
 	 */
 	private void animateView(final View target, final int type ,final ViewGroup parent) {
 		Animation anim = new ExpandCollapseAnimation(
 				target,
 				type
 		);
		anim.setDuration(100);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (type == ExpandCollapseAnimation.EXPAND) {
					if (parent instanceof ListView) {
						ListView listView = (ListView) parent;
						int movement = target.getBottom();

						Rect r = new Rect();
						boolean visible = target.getGlobalVisibleRect(r);
						Rect r2 = new Rect();
						listView.getGlobalVisibleRect(r2);

						if (!visible) {
							listView.smoothScrollBy(movement, 100);
						} else {
							if (r2.bottom == r.bottom) {
								listView.smoothScrollBy(movement, 100);
							}
						}
					}
				}

			}
		});
 		target.startAnimation(anim);
 	}
	

}
