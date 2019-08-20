package com.walktour.gui.data.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.walktour.gui.data.model.DBManager;

import java.util.Locale;

public class BaseView implements OnClickListener{
	
	public Context mContext;
	public String type = "";
	public SharedPreferences mPreferences;
	public LayoutInflater inflater;
	public String language = "";//语音环境
	protected DBManager mDBManager;
	
	public BaseView(Context context, String type) {
		this.mContext = context;
		this.type = type;
		mPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE );
		inflater = LayoutInflater.from(context);
		init();
	}

	private void init() {
		language = Locale.getDefault().getLanguage();
		mDBManager = DBManager.getInstance(this.mContext);
	}
	@Override
	public void onClick(View view) {
		
	}
	
	public interface ClickListenerCallBack {
		void onSummit();
		void onMark(boolean mark);
		void onClear();
	}
}
