package com.walktour.framework.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walktour.Utils.DensityUtil;
import com.walktour.base.util.StringUtil;
import com.walktour.customView.ListViewForScrollView;
import com.walktour.customView.ListViewForStartDialog;
import com.walktour.gui.R;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义Dialog样式效果<BR>
 * 4.0风格Dialog控件，为了方便修改，提供与AlertDialog几乎一致的方法体
 * 
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-7-13]
 */
@SuppressLint("InflateParams")
public class BasicDialog extends Dialog {
	protected ProgressDialogParams mDialog;

	@Override
	public void onStart() {
		super.onStart();
		if (mDialog != null) {
			mDialog.mHasStarted = true;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mDialog != null) {
			mDialog.mHasStarted = false;
		}
	}

	public BasicDialog(Context context) {
		super(context);
	}

	public BasicDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			super.openOptionsMenu();
		} else {
			super.onKeyDown(keyCode, event);
		}
		return true;
	}

	/**
	 * Helper class for creating a custom dialog
	 */
	public static class Builder {
		private ProgressDialogParams progressDP;
		/** 是否居中显示信息 */
		private boolean isMessageShowCenter = false;
		/** 上下文 */
		private Context context;
		/** 标题 */
		private String title;
		/** 消息体 */
		private String message;
		/** 点击按钮是否会关闭对话框 */
		private Map<ButtonType, Boolean> mButtonDismissDialogMap = new HashMap<ButtonType, Boolean>();
		/** 按钮名称映射<按钮类型，按钮文字资源ID> */
		private Map<ButtonType, Integer> mButtonTextIdMap = new HashMap<ButtonType, Integer>();
		/** 按钮监听类映射<按钮类型，按钮监听类> */
		private Map<ButtonType, DialogInterface.OnClickListener> mButtonClickListenerMap = new HashMap<ButtonType, DialogInterface.OnClickListener>();
		/** 消息体布局 */
		private View contentView;
		/** 布局参数 */
		private RelativeLayout.LayoutParams contentParams;
		/** 提示图标 */
		private int icon;
		/** 列表数组 */
		private String[] strArray;
		/** 适配器 */
		private ListAdapter mAdapter;
		/** 列表数组默认选择 */
		private int defaultValue = -1;
		/** 多选选中项 */
		private boolean[] checkedItems;
		/** 列表按钮 */
		private DialogInterface.OnClickListener singleChoiceClickListener;
		/** 多选监听事件 */
		private DialogInterface.OnMultiChoiceClickListener multiChoiceClickListener;
		/** 列表按钮 */
		private DialogInterface.OnClickListener mChoiceClickListener;
		/** 物理键监听键事件 */
		private DialogInterface.OnKeyListener onKeyListener;
		/** 窗口关闭事件 */
		private DialogInterface.OnDismissListener onDismissListener;
		/** 窗口取消事件 */
		private DialogInterface.OnCancelListener onCancelListener;
		/** 当前窗口是否可以取消 */
		private boolean cancelable = true;

		/** 按键类型： 确定，中间，取消 */
		private enum ButtonType {
			positive, neutral, negative;
		}

		/**
		 * 构造函数
		 */
		public Builder(Context context) {
			this.context = context;
		}

		/**
		 * 设置消息体
		 * 
		 * @param messageId
		 *          消息ID
		 * @return
		 */
		public Builder setMessage(int messageId) {
			return this.setMessage(context.getString(messageId));
		}

		/**
		 * 设置消息体
		 * 
		 * @param message
		 *          消息
		 * @return
		 */
		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		/**
		 * 设置标题
		 * 
		 * @param titleId
		 *          标题Id
		 * @return
		 */
		public Builder setTitle(int titleId) {
			if (context!=null){
                String title = StringUtil.isEmpty(context.getString(titleId)) ? "" : context.getString(titleId);
                return this.setTitle(title);
			}else {
				return this.setTitle("提示");
			}

		}

		/**
		 * 设置标题
		 * 
		 * @param title
		 *          标题
		 * @return
		 */
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		/**
		 * 设置标题图标
		 * 
		 * @return
		 */
		public Builder setIcon(int iconId) {
			this.icon = iconId;
			return this;
		}

		/**
		 * 设置自定义布局 ，与setContentView 一致 AlertDialog原生拥有此方法，故增加
		 * 
		 * @param view
		 * @return
		 */
		public Builder setView(View view) {
			return this.setView(view, null);
		}

		/**
		 * 设置自定义布局 ，与setContentView 一致 AlertDialog原生拥有此方法，故增加
		 * 如果是ListView，则改成适配ScrollView的ListView；
		 * @param view
		 * @return
		 */
		public Builder setView(View view, RelativeLayout.LayoutParams contentParams) {
			if (view instanceof ListView){
				ListView lv = ((ListView) view);
				ListViewForScrollView listViewForScrollView=new ListViewForScrollView(context);
				listViewForScrollView.setAdapter(lv.getAdapter());
				listViewForScrollView.setDivider(lv.getDivider());
				// listView.setBackgroundColor(getResources().getColor(R.color.base_list_item_bg_nomal));
				listViewForScrollView.setBackgroundColor(lv.getDrawingCacheBackgroundColor());
				listViewForScrollView.setOnItemClickListener(lv.getOnItemClickListener());
				this.contentView= listViewForScrollView;
			}else {
				this.contentView = view;
			}
			this.contentParams = contentParams;
			return this;
		}

		/**
		 * 设置自定义列表布局
		 * 
		 * @param strArray
		 *          字符串列表
		 * @param listener
		 *          点击事件
		 * @return
		 */
		public Builder setItems(String[] strArray, DialogInterface.OnClickListener listener) {
			this.strArray = strArray;
			this.mChoiceClickListener = listener;
			return this;
		}

		/**
		 * 设置确定按钮
		 * 
		 * @param buttonTextId
		 *          按钮文字资源Id
		 * @param listener
		 *          监听类
		 * @return
		 */
		public Builder setPositiveButton(int buttonTextId, DialogInterface.OnClickListener listener) {
			return this.setPositiveButton(buttonTextId, listener, true);
		}

		/**
		 * 设置确定按钮
		 * 
		 * @param buttonTextId
		 *          按钮文字资源Id
		 * @param listener
		 *          监听类
		 * @param isDismissDialog
		 *          是否点击按钮后关闭对话框
		 * @return
		 */
		public Builder setPositiveButton(int buttonTextId, DialogInterface.OnClickListener listener,
				boolean isDismissDialog) {
			this.mButtonTextIdMap.put(ButtonType.positive, buttonTextId);
			if (listener != null)
				this.mButtonClickListenerMap.put(ButtonType.positive, listener);
			this.mButtonDismissDialogMap.put(ButtonType.positive, isDismissDialog);
			return this;
		}

		/**
		 * 设置取消按钮
		 * 
		 * @param buttonTextId
		 *          按钮文字资源Id
		 * @return
		 */
		public Builder setNegativeButton(int buttonTextId) {
			return this.setNegativeButton(buttonTextId, null, true);
		}

		/**
		 * 设置取消按钮
		 * 
		 * @param buttonTextId
		 *          按钮文字资源Id
		 * @param listener
		 *          监听类
		 * @return
		 */
		public Builder setNegativeButton(int buttonTextId, DialogInterface.OnClickListener listener) {
			return this.setNegativeButton(buttonTextId, listener, true);
		}

		/**
		 * 设置取消按钮
		 * 
		 * @param buttonTextId
		 *          按钮文字资源Id
		 * @param listener
		 *          监听类
		 * @param isDismissDialog
		 *          是否点击按钮后关闭对话框
		 * @return
		 */
		public Builder setNegativeButton(int buttonTextId, DialogInterface.OnClickListener listener,
				boolean isDismissDialog) {
			this.mButtonTextIdMap.put(ButtonType.negative, buttonTextId);
			if (listener != null)
				this.mButtonClickListenerMap.put(ButtonType.negative, listener);
			this.mButtonDismissDialogMap.put(ButtonType.negative, isDismissDialog);
			return this;
		}

		/**
		 * 设置NeutralButton 介于确定与取消中间
		 * 
		 * @param buttonTextId
		 *          按钮文字资源Id
		 * @param listener
		 *          监听类
		 * @return
		 */
		public Builder setNeutralButton(int buttonTextId, DialogInterface.OnClickListener listener) {
			this.mButtonTextIdMap.put(ButtonType.neutral, buttonTextId);
			return this.setNeutralButton(buttonTextId, listener, false);
		}

		/**
		 * 设置NeutralButton 介于确定与取消中间
		 * 
		 * @param buttonTextId
		 *          按钮文字资源Id
		 * @param listener
		 *          监听类
		 * @param isDismissDialog
		 *          是否点击按钮后关闭对话框
		 * @return
		 */
		public Builder setNeutralButton(int buttonTextId, DialogInterface.OnClickListener listener,
				boolean isDismissDialog) {
			this.mButtonTextIdMap.put(ButtonType.neutral, buttonTextId);
			if (listener != null)
				this.mButtonClickListenerMap.put(ButtonType.neutral, listener);
			this.mButtonDismissDialogMap.put(ButtonType.neutral, isDismissDialog);
			return this;
		}

		/**
		 * 设置物理键监听事件<BR>
		 * 物理键处理事件
		 * 
		 * @param onKeyListener
		 * @return
		 */
		public Builder setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
			this.onKeyListener = onKeyListener;
			return this;
		}

		/**
		 * 
		 * 设置单项选择列表
		 * 
		 * @return
		 */
		public Builder setSingleChoiceItems(String[] strArray, int defaultValue, DialogInterface.OnClickListener listener) {
			this.strArray = strArray;
			this.defaultValue = defaultValue;
			this.singleChoiceClickListener = listener;
			return this;
		}

		/**
		 * 直接设置适配器<BR>
		 * [功能详细描述]
		 * 
		 * @param adapter
		 * @param defaultValue
		 * @param listener
		 * @return
		 */
		public Builder setSingleChoiceItems(ListAdapter adapter, int defaultValue, final OnClickListener listener) {
			this.mAdapter = adapter;
			this.singleChoiceClickListener = listener;
			this.defaultValue = defaultValue;
			return this;
		}

		/**
		 * 
		 * 设置单项选择列表
		 * 
		 * @return
		 */
		public Builder setSingleChoiceItems(int arrayId, int defaultValue, DialogInterface.OnClickListener listener) {
			this.strArray = context.getResources().getStringArray(arrayId);
			this.defaultValue = defaultValue;
			this.singleChoiceClickListener = listener;
			return this;
		}

		/**
		 * 
		 * 设置多项选择列表
		 * 
		 * @return
		 */
		public Builder setMultiChoiceItems(String[] strArray, boolean[] checkedItems,
				DialogInterface.OnMultiChoiceClickListener listener) {
			this.strArray = strArray;
			this.checkedItems = checkedItems;
			this.multiChoiceClickListener = listener;
			return this;
		}

		/**
		 * 设置对话框关闭监听类
		 * 
		 * @param dismissListener
		 * @return
		 */
		public Builder setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
			this.onDismissListener = dismissListener;
			return this;
		}

		/**
		 * 设置取消监听类
		 * 
		 * @param cancelListener
		 * @return
		 */
		public Builder setOnCancelListener(DialogInterface.OnCancelListener cancelListener) {
			this.onCancelListener = cancelListener;
			return this;
		}

		public Builder setProgressParam(boolean cancelable, int progress, String message, boolean indeterminate) {

			progressDP = new ProgressDialogParams();

			LayoutInflater factory = LayoutInflater.from(context);
			final View view = factory.inflate(R.layout.alert_dialog_progress, null);

			progressDP.mProgress = (ProgressBar) view.findViewById(R.id.Progress);
			progressDP.mMessageView = (TextView) view.findViewById(R.id.TextView);
			progressDP.mMessageView.setText(message);
			progressDP.mProgress.setProgress(progress);
			progressDP.mProgress.setIndeterminate(indeterminate);
			contentView = view;
			this.cancelable = cancelable;

			return this;
		}

		/**
		 * Create the custom dialog
		 */
		@SuppressWarnings("deprecation")
		public BasicDialog create() {
			final BasicDialog dialog = new BasicDialog(context, R.style.Translucent_NoTitle);
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.dialog, null);
			if (title==null){
				title=context.getString(R.string.str_tip);
			};
			// 设置标题
			((TextView) layout.findViewById(R.id.title)).setText(title);
			// 设置提示图标
			((ImageView) layout.findViewById(R.id.icon)).setImageResource(icon);

			dialog.setCancelable(cancelable);
			if (!this.mButtonTextIdMap.isEmpty()) {
				// 按钮父类布局可见
				layout.findViewById(R.id.button_layout).setVisibility(View.VISIBLE);
				for (ButtonType type : this.mButtonTextIdMap.keySet()) {
					Button button = null;
					switch (type) {
					case positive:
						button = (Button) layout.findViewById(R.id.positiveButton);
						break;
					case negative:
						button = (Button) layout.findViewById(R.id.negativeButton);
						break;
					default:
						button = (Button) layout.findViewById(R.id.neutralButton);
						break;
					}
					button.setVisibility(View.VISIBLE);
					button.setText(this.mButtonTextIdMap.get(type));
					final DialogInterface.OnClickListener listener = this.mButtonClickListenerMap.get(type);
					final boolean isDismissDialog = this.mButtonDismissDialogMap.get(type);
					button.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							if (listener != null) {
								listener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
							}
							if (isDismissDialog)
								dialog.dismiss();

						}
					});
				}
			}
			// 如果标题为空 设置为不可见
			if (title == null) {
//				layout.findViewById(R.id.title_template).setVisibility(View.VISIBLE);
				layout.findViewById(R.id.title_split_line_view).setVisibility(View.GONE);
			}
			LinearLayout contentLayout = (LinearLayout) layout.findViewById(R.id.content);
			// 如果消息体没有 设置为不可见
			if (message != null) {
				contentLayout.setVisibility(View.VISIBLE);
				contentLayout.removeAllViews();
				TextView messageText = new TextView(context);
				messageText.setTextColor(context.getResources().getColor(R.color.app_main_text_color));
				messageText.setText(message);
				messageText.setTextSize(16);
				messageText.setMaxHeight(DensityUtil.dip2px(context,400));//设置最大高度
				messageText.setMinWidth(DensityUtil.dip2px(context,300));//设置最小宽度
				messageText.setMovementMethod(ScrollingMovementMethod.getInstance());
				if (isMessageShowCenter) {
					messageText.setGravity(Gravity.CENTER_HORIZONTAL);
				}
				contentLayout.addView(messageText, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			}

			// 设置物理键监听事件
			if (onKeyListener != null) {
				dialog.setOnKeyListener(onKeyListener);
			}

			if (onDismissListener != null) {
				dialog.setOnDismissListener(onDismissListener);
			}

			if (onCancelListener != null) {
				dialog.setOnCancelListener(onCancelListener);
			}

			// 如果自定义布局没有 设置为不可见
			if (contentView != null) {
				// 设置自定义的布局
				contentLayout.setVisibility(View.VISIBLE);
				contentLayout.removeAllViews();
				contentLayout.addView(contentView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				if (contentParams != null) {
					contentLayout.setLayoutParams(contentParams);
				}

			}

			// 如果自定义布局为ListView没有 设置为不可见
			if (null != strArray) {
				((LinearLayout) layout.findViewById(R.id.list_content)).setVisibility(View.VISIBLE);
                ListViewForStartDialog singleChoice = new ListViewForStartDialog(context);
				singleChoice.setDivider(context.getResources().getDrawable(R.drawable.list_divider));
				// singleChoice.setDividerHeight(1);
				singleChoice.setCacheColorHint(Color.TRANSPARENT);
				singleChoice.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
				ArrayAdapter<String> adapter;
				if (multiChoiceClickListener != null) {
					adapter = new ArrayAdapter<String>(context, R.layout.simple_list_item_multiple_choice, strArray);
					singleChoice.setAdapter(adapter);
					// 设置默认选择的单选框
					singleChoice.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					if (this.defaultValue >= 0)
						singleChoice.setItemChecked(defaultValue, true);
					else if (this.checkedItems != null) {
						for (int i = 0; i < this.checkedItems.length; i++) {
							singleChoice.setItemChecked(i, this.checkedItems[i]);
						}
					}
					singleChoice.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							multiChoiceClickListener.onClick(dialog, position,
									((CheckedTextView) view.findViewById(android.R.id.text1)).isChecked());
						}
					});

				} else if (defaultValue == -1) {
					adapter = new ArrayAdapter<String>(context, R.layout.simple_list_item_1, strArray);
					// adapter.setDropDownViewResource(R.layout.simple_list_item_1);
					singleChoice.setAdapter(adapter);
					singleChoice.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							if (null != mChoiceClickListener) {
								mChoiceClickListener.onClick(dialog, position);
								dialog.dismiss();
							}

						}
					});
					// 带有单选按钮的ListView
				} else {
					adapter = new ArrayAdapter<String>(context, R.layout.simple_list_item_single_choice, strArray);
					singleChoice.setAdapter(adapter);
					// 设置默认选择的单选框
					singleChoice.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					singleChoice.setItemChecked(defaultValue, true);

					singleChoice.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							if (null != singleChoiceClickListener) {
								singleChoiceClickListener.onClick(dialog, position);
								dialog.dismiss();
							}

						}
					});
				}
				((LinearLayout) layout.findViewById(R.id.list_content)).removeAllViews();
				LinearLayout contentView = ((LinearLayout) layout.findViewById(R.id.list_content));
				contentView.addView(singleChoice, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			} else if (mAdapter != null) {
				((LinearLayout) layout.findViewById(R.id.list_content)).setVisibility(View.VISIBLE);
				ListView singleChoice = new ListView(context);
				singleChoice.setDivider(context.getResources().getDrawable(R.drawable.list_divider));
				// singleChoice.setDividerHeight(1);
				singleChoice.setCacheColorHint(Color.TRANSPARENT);
				singleChoice.setAdapter(mAdapter);
				// 设置默认选择的单选框
				singleChoice.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				singleChoice.setItemChecked(defaultValue, true);
				singleChoice.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						if (null != singleChoiceClickListener) {
							singleChoiceClickListener.onClick(dialog, position);
							dialog.dismiss();
						}

					}
				});
				((LinearLayout) layout.findViewById(R.id.list_content)).removeAllViews();
				((LinearLayout) layout.findViewById(R.id.list_content)).addView(singleChoice,
						new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			}

			// 判断是否横屏
			if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				dialog.setContentView(layout, new LayoutParams(
						context.getResources().getDimensionPixelSize(R.dimen.dialogheight), LayoutParams.WRAP_CONTENT));
			} else {
				dialog.setContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			}

			if (this.progressDP != null) {
				dialog.mDialog = this.progressDP;
			}

			return dialog;
		}

		/**
		 * 弹出Dialog对话框<BR>
		 * 弹出对话框方法
		 * 
		 * @return
		 */
		public BasicDialog show() {
			BasicDialog basicDialog = this.create();
			basicDialog.show();
			return basicDialog;
		}

		public void setMessageShowCenter(boolean isMessageShowCenter) {
			this.isMessageShowCenter = isMessageShowCenter;
		}
	}

	public void setProgress(int value) {
		if (mDialog != null) {
			if (mDialog.mHasStarted) {
				mDialog.mProgress.setProgress(value);
			}
		}
	}

	public void setMessage(String str) {
		if (mDialog != null) {
			if (mDialog.mHasStarted) {
				mDialog.mMessageView.setText(str);
			}
		}
	}

	public static class ProgressDialogParams {
		private ProgressBar mProgress;
		private TextView mMessageView;
		private boolean mHasStarted;
	}

}
