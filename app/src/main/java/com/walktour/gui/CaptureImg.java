package com.walktour.gui;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.BasicActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 实现拍照功能
 * 
 * @author Administrator
 *
 */
@SuppressWarnings("deprecation")
public class CaptureImg extends BasicActivity implements Callback {
	/** Called when the activity is first created. */
	public static final String MAP_PATH = "map_path";
	private static final String tag = "CaptureImg";
	private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Camera mCamera = null;
	private boolean mPreviewRunning;
	private Button btnSave;
	private Button btnTake;
	private Button btnCancel;
	private byte[] bitmpdata = null;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.indoor_task_captureimg);

		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		btnSave = initButton(R.id.btnSave);
		btnTake = initButton(R.id.btnTake);
		btnCancel = initButton(R.id.btnCancel);
		btnSave.setOnClickListener(btnListener);
		btnTake.setOnClickListener(btnListener);
		btnCancel.setOnClickListener(btnListener);

		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	private OnClickListener btnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnTake:
				setButtonEnable(false);
				mCamera.takePicture(shutter, null, mPictureCallback);
				setButtonEnable(true);
				break;
			case R.id.btnSave:
				savePic();
				break;
			case R.id.btnCancel: // 增加取消按钮退出
				if (bitmpdata != null) {
					bitmpdata = null;
					mCamera.startPreview();
				} else if (mCamera != null) {
					LogUtil.i("capture photo", " cat " + "exit");
					mCamera.stopPreview();
					mPreviewRunning = false;
					mCamera.release();
					mCamera = null;
					CaptureImg.this.finish();
				} else {
					CaptureImg.this.finish();
				}

				break;
			}
		}
	};

	private void setButtonEnable(boolean enablevalue) {
		btnTake.setEnabled(enablevalue);
		btnSave.setEnabled(enablevalue);
		btnCancel.setEnabled(enablevalue);
	}

	private static class MyHandler extends Handler {
		private WeakReference<CaptureImg> refence;

		public MyHandler(CaptureImg context) {
			this.refence = new WeakReference<CaptureImg>(context);
		}

		public void handleMessage(android.os.Message msg) {
			CaptureImg obj = this.refence.get();
			switch (msg.what) {
			case SAVE:
				obj.setButtonEnable(false);
				break;
			case SAVE_FINISH:
				obj.setButtonEnable(true);
				if (obj.progressDialog != null) {
					obj.progressDialog.dismiss();
				}
				break;
			}
		};

	}

	private Handler handler = new MyHandler(CaptureImg.this);

	private void showProgressDialog() {
		progressDialog = new ProgressDialog(CaptureImg.this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getString(R.string.str_saving));
		progressDialog.show();
	}

	private static final int SAVE = 1000;
	private static final int SAVE_FINISH = 1001;
	private ProgressDialog progressDialog = null;

	class TakeThread implements Runnable {
		@Override
		public void run() {
			LogUtil.i(tag, "bitmpdata is not null");
			Message msg = handler.obtainMessage(SAVE);
			handler.sendMessage(msg);
			File filedir = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
			if (!filedir.exists()) {
				filedir.mkdirs();
			}
			String filename = sf.format(new Date()) + ".jpg";
			String filepath = filedir.getAbsolutePath() + "/" + filename;
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				Bitmap bitmap = BitmapFactory.decodeByteArray(bitmpdata, 0, bitmpdata.length, options);
				File file = new File(filepath);
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
				bos.flush();
				bos.close();
				if (bitmap != null && !bitmap.isRecycled()) {
					bitmap.recycle();
					bitmap = null;
					System.gc();
				}
				mCamera.stopPreview();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				bitmpdata = null;
				msg = handler.obtainMessage(SAVE_FINISH);
				handler.sendMessage(msg);
			}
			backForResult(filepath);
		}
	}

	private void backForResult(String path) {
		LogUtil.w(tag, "--path=" + path);
		Intent intent = new Intent();
		intent.putExtra(MAP_PATH, path);
		this.setResult(RESULT_OK, intent);
		this.finish();
	}

	@Override
	@SuppressWarnings("deprecation")
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i(tag, "suffaceChanged");
		if (mPreviewRunning) {
			mCamera.stopPreview();
		}
		Parameters params = mCamera.getParameters();
		params.setPictureFormat(ImageFormat.JPEG);// 设置图片格式
		params.set("rotation", 90);
		params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//设置连续自动对焦
		// params.setPreviewSize(width, height);
		// params.setPictureSize(1280, 960);
		mCamera.setParameters(params);
		mPreviewRunning = true;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(tag, "surfaceCreated");
		if (mCamera == null) {
			mCamera = Camera.open();
			try {
				mCamera.setPreviewDisplay(holder);
			} catch (IOException e) {
				mCamera.release();
			}
			mCamera.startPreview();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(tag, "surfaceDestroyed");
		if (mCamera != null) {
			mCamera.stopPreview();
			mPreviewRunning = false;
			mCamera.release();
			mCamera = null;
		}
	}

	// private AutoFocusCallback mAutoFocusCallBack = new AutoFocusCallback() {
	// @Override
	// public void onAutoFocus(boolean success, Camera camera) {
	// Camera.Parameters Parameters = mCamera.getParameters();
	// Parameters.setPictureFormat(ImageFormat.JPEG);// 设置图片格式
	// mCamera.setParameters(Parameters);
	// mCamera.takePicture(null, null, mPictureCallback);
	// }
	// };
	//

	/**
	 * 添加拍照声音功能，
	 */
	@SuppressWarnings("deprecation")
	Camera.ShutterCallback shutter = new ShutterCallback() {

		private ToneGenerator tone;

		@Override
		public void onShutter() {
			if (tone == null) {
				// 发出提示用户的声音
				tone = new ToneGenerator(AudioManager.STREAM_SYSTEM, ToneGenerator.MAX_VOLUME);
			}
			tone.startTone(ToneGenerator.TONE_PROP_BEEP);
			Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
			vibrator.vibrate(100);

		}
	};

	/**
	 * 拍照的回调接口
	 */
	@SuppressWarnings("deprecation")
	private PictureCallback mPictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			bitmpdata = data;
			mCamera.stopPreview();
		}
	};

	private void savePic() {
		if (bitmpdata != null) {
			LogUtil.w(tag, "bitmpdata is not null");
			showProgressDialog();
			new Thread(new TakeThread()).start();
		} else {
			LogUtil.w(tag, "bitmpdata is null");
		}
	}
}