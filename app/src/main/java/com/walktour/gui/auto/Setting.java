package com.walktour.gui.auto;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;

public class Setting extends BasicActivity{
//	private Button ButtonGetDir;
	private Spinner SpinnerServer;
//	private EditText editdevice;
//	private EditText editsavepath;
//	private EditText editfilesize;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.normalconfig);
		SpinnerServer = (Spinner)findViewById(R.id.SpinnerServer);
		String[] serverarr = getResources().getStringArray(R.array.serverSet);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,serverarr);
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		SpinnerServer.setAdapter(adapter);
		OnItemSelectedListener listener = new  OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
			}
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		};
		SpinnerServer.setOnItemSelectedListener(listener);
	}
}
