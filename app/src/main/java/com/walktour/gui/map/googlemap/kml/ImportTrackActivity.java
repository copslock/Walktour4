package com.walktour.gui.map.googlemap.kml;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.map.googlemap.kml.XMLparser.GpxTrackParser;
import com.walktour.gui.map.googlemap.kml.XMLparser.KmlTrackParser;
import com.walktour.gui.map.googlemap.utils.SimpleThreadFactory;
import com.walktour.gui.map.googlemap.utils.Ut;

import org.openintents.filemanager.FileManagerActivity;
import org.openintents.filemanager.util.FileUtils;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ImportTrackActivity extends BasicActivity {
	EditText mFileName;
	private PoiManager mPoiManager;

	private ProgressDialog dlgWait;
	protected ExecutorService mThreadPool = Executors.newSingleThreadExecutor(new SimpleThreadFactory("ImportTrack"));

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences settings = getPreferences(Activity.MODE_PRIVATE);
		this.setContentView(R.layout.importtrack);

		if (mPoiManager == null)
			mPoiManager = new PoiManager(this);

		mFileName = initEditText(R.id.FileName);
		mFileName.setText(settings.getString("IMPORT_TRACK_FILENAME", Ut.getRMapsImportDir(this).getAbsolutePath()));

		(initButton(R.id.SelectFileBtn))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doSelectFile();
			}
		});
		(initButton(R.id.ImportBtn))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doImportTrack();
			}
		});
		(initButton(R.id.discardButton))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ImportTrackActivity.this.finish();
			}
		});
	}

	@Override
	@SuppressWarnings("deprecation")
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.id.dialog_wait: {
			dlgWait = new ProgressDialog(this);
			dlgWait.setMessage("Please wait while loading...");
			dlgWait.setIndeterminate(true);
			dlgWait.setCancelable(false);
			return dlgWait;
		}
		}
		return null;
	}

	protected void doSelectFile() {
		Intent intent = new Intent(this, FileManagerActivity.class);
		intent.setData(Uri.parse(mFileName.getText().toString()));
		startActivityForResult(intent, R.id.ImportBtn);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case R.id.ImportBtn:
			if (resultCode == RESULT_OK && data != null) {
				// obtain the filename
				String filename = Uri.decode(data.getDataString());
				if (filename != null) {
					// Get rid of URI prefix:
					if (filename.startsWith("file://")) {
						filename = filename.substring(7);
					}

					mFileName.setText(filename);
				}

			}
			break;
		}
	}

	@SuppressWarnings("deprecation")
	private void doImportTrack() {
		File file = new File(mFileName.getText().toString());

		if(!file.exists()){
			Toast.makeText(this, "No such file", Toast.LENGTH_LONG).show();
			return;
		}

		showDialog(R.id.dialog_wait);

		this.mThreadPool.execute(new Runnable() {
			public void run() {
				File file = new File(mFileName.getText().toString());

				SAXParserFactory fac = SAXParserFactory.newInstance();
				SAXParser parser = null;
				try {
					parser = fac.newSAXParser();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(parser != null){
					mPoiManager.beginTransaction();
					Ut.dd("Start parsing file " + file.getName());
					try {
						if(FileUtils.getExtension(file.getName()).equalsIgnoreCase(".kml"))
							parser.parse(file, new KmlTrackParser(mPoiManager));
						else if(FileUtils.getExtension(file.getName()).equalsIgnoreCase(".gpx"))
							parser.parse(file, new GpxTrackParser(mPoiManager));

						mPoiManager.commitTransaction();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						mPoiManager.rollbackTransaction();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						mPoiManager.rollbackTransaction();
					} catch (IllegalStateException e) {
					} catch (OutOfMemoryError e) {
						Ut.w("OutOfMemoryError");
						mPoiManager.rollbackTransaction();
					}
					Ut.dd("Pois commited");
				}

				dlgWait.dismiss();
				ImportTrackActivity.this.finish();
			};
		});

	}


	@Override
	protected void onDestroy() {
		mThreadPool.shutdown();
		super.onDestroy();
		mPoiManager.FreeDatabases();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("IMPORT_TRACK_FILENAME", mFileName.toString());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		SharedPreferences uiState = getPreferences(0);
		SharedPreferences.Editor editor = uiState.edit();
		editor.putString("IMPORT_TRACK_FILENAME", mFileName.getText().toString());
		editor.commit();
		super.onPause();
	}

}
