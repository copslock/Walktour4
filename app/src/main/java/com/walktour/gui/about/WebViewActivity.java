package com.walktour.gui.about;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.walktour.Utils.WalkMessage;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;

import java.io.File;

public class WebViewActivity extends BasicActivity {
	
	private WebView webView ;
	
	@Override
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.webview_help); 
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(WalkMessage.KEY_HTTP_SHOWWEB_QUID);
		this.registerReceiver(testEventReceiver, filter);
		
		webView = (WebView) findViewById(R.id.WebViewHelp);
	    WebSettings webSettings = webView.getSettings();
	    webSettings.setJavaScriptEnabled(true);
	    webSettings.setSupportZoom(false);
	    webSettings.setAllowFileAccess(true);
	    webSettings.setLoadsImagesAutomatically(true);
	    webSettings.setSavePassword(false);
	    webSettings.setSaveFormData(false);
	    webSettings.setJavaScriptEnabled(true);  
	    
	    String url = getIntent().getExtras().getString("path");
		
	    if( url.startsWith("http") ){
	    	webView.loadUrl( url );
	    }else{
	    	
	    	File file = new File( url );
	    	if( file.isFile() ){
	    		webView.loadUrl("file://"+file.getAbsolutePath() );
	    	}
	    }
		
	}

	private final BroadcastReceiver testEventReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(WalkMessage.KEY_HTTP_SHOWWEB_QUID)){
				finish();
			}
		}
	};

	/* (non-Javadoc)
	 * @see com.walktour.framework.ui.BasicActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.unregisterReceiver(testEventReceiver);
	}
}
