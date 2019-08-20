package com.walktour.gui.map.googlemap.kml;

import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.map.googlemap.utils.Ut;

import org.andnav.osm.util.GeoPoint;

public class PoiActivity extends BasicActivity {
	EditText mTitle, mLat, mLon, mDescr;
	Spinner mSpinner;
	CheckBox mHidden;
	private PoiPoint mPoiPoint;
	private PoiManager mPoiManager;
	
	
	@Override
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.poi);

		if(mPoiManager == null)
			mPoiManager = new PoiManager(this);

		mTitle = initEditText(R.id.Title);
		mLat = initEditText(R.id.Lat);
		mLon = initEditText(R.id.Lon);
		mDescr = initEditText(R.id.Descr);
		mHidden = (CheckBox) findViewById(R.id.Hidden);

		mSpinner = initSpinner(R.id.spinnerCategory);
		Cursor c = mPoiManager.getGeoDatabase().getPoiCategoryListCursor();
        startManagingCursor(c);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.poicategory_spinner, //android.R.layout.simple_spinner_item, 
                c, 
                        new String[] { "name", "iconid"}, 
                        new int[] { android.R.id.text1, R.id.pic });
        adapter.setDropDownViewResource(R.layout.poicategory_spinner_dropdown);
        mSpinner.setAdapter(adapter);
		
        Bundle extras = getIntent().getExtras();
        if(extras == null) extras = new Bundle();
        int id = extras.getInt("pointid", PoiPoint.EMPTY_ID());
        
        if(id < 0){
        	mPoiPoint = new PoiPoint();
			mTitle.setText(extras.getString("title"));
			mSpinner.setSelection(0);
			mLat.setText(Ut.formatGeoCoord(extras.getDouble("lat")));
			mLon.setText(Ut.formatGeoCoord(extras.getDouble("lon")));
			mDescr.setText(extras.getString("descr"));
			mHidden.setChecked(false);
        }else{
        	mPoiPoint = mPoiManager.getPoiPoint(id);
        	
        	if(mPoiPoint == null)
        		finish();
        	
        	mTitle.setText(mPoiPoint.Title);
        	for(int pos = 0; pos < mSpinner.getCount(); pos++){
        		if(mSpinner.getItemIdAtPosition(pos) == mPoiPoint.CategoryId){
        			mSpinner.setSelection(pos);
        			break;
        		}
        	}
         	mLat.setText(Ut.formatGeoCoord(mPoiPoint.GeoPoint.getLatitude()));
        	mLon.setText(Ut.formatGeoCoord(mPoiPoint.GeoPoint.getLongitude()));
        	mDescr.setText(mPoiPoint.Descr);
        	mHidden.setChecked(mPoiPoint.Hidden);
        }
		
		(initButton(R.id.saveButton))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doSaveAction();
			}
		});
		(initButton(R.id.discardButton))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				PoiActivity.this.finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPoiManager.FreeDatabases();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK: {
			doSaveAction();
			return true;
		}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void doSaveAction() {
		mPoiPoint.Title = mTitle.getText().toString();
		mPoiPoint.CategoryId = (int)mSpinner.getSelectedItemId();
		mPoiPoint.Descr = mDescr.getText().toString();
		mPoiPoint.GeoPoint = GeoPoint.from2DoubleString(mLat.getText().toString(), mLon.getText().toString());
		mPoiPoint.Hidden = mHidden.isChecked();
		
		mPoiManager.updatePoi(mPoiPoint);
		finish();
		
		Toast.makeText(PoiActivity.this, R.string.message_saved, Toast.LENGTH_SHORT).show();
	}

}
