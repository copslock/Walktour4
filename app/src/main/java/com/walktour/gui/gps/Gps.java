package com.walktour.gui.gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.CompassView;
import com.walktour.gui.R;

import java.util.Locale;

public class Gps extends BasicActivity implements View.OnClickListener {

    private final float MAX_ROATE_DEGREE = 1.0f;
    private SensorManager mSensorManager;
    private Sensor mOrientationSensor;
    private float mDirection;
    private float mTargetDirection;
    private AccelerateInterpolator mInterpolator;
    protected final Handler mHandler = new Handler();
    private boolean mStopDrawing;
    private boolean mChinease;

    View mCompassView;
    CompassView mPointer;
    LinearLayout mDirectionLayout;
    LinearLayout mAngleLayout;
    
    private String tag=  "Gps";
    CheckBox gpsswitch;
    TextView v_longitude;
    TextView v_latitude;
    TextView v_altitude;
    TextView v_speed;
    TextView v_bearing;
    TextView v_accuracy;
    TextView v_state;
    TextView v_usedState;
    
    GpsInfo gpsInfo = null;
    ApplicationModel appModel = null;
    TableLayout sv_gps_valueLinearLayout;

    protected Runnable mCompassViewUpdater = new Runnable() {
        @Override
        public void run() {
            if (mPointer != null && !mStopDrawing) {
                if (mDirection != mTargetDirection) {

                    // calculate the short routine
                    float to = mTargetDirection;
                    if (to - mDirection > 180) {
                        to -= 360;
                    } else if (to - mDirection < -180) {
                        to += 360;
                    }

                    // limit the max speed to MAX_ROTATE_DEGREE
                    float distance = to - mDirection;
                    if (Math.abs(distance) > MAX_ROATE_DEGREE) {
                        distance = distance > 0 ? MAX_ROATE_DEGREE : (-1.0f * MAX_ROATE_DEGREE);
                    }

                    // need to slow down if the distance is short
                    mDirection = normalizeDegree(mDirection
                            + ((to - mDirection) * mInterpolator.getInterpolation(Math
                                    .abs(distance) > MAX_ROATE_DEGREE ? 0.4f : 0.3f)));
                    mPointer.updateDirection(mDirection);
                }

                updateDirection();

                mHandler.postDelayed(mCompassViewUpdater, 20);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.walktour_gps);
        gpsInfo = GpsInfo.getInstance();
        appModel = ApplicationModel.getInstance();
        IntentFilter  broadCaseIntent = new IntentFilter();
        broadCaseIntent.addAction(GpsInfo.gpsLocationChanged);
        //broadCaseIntent.addAction(GpsInfo.gpsProviderDisabled);
        this.registerReceiver(gpsLocationChangeReceiver, broadCaseIntent);
        initResources();
        initServices();
        findView();
    }
    
    private void findView() {
        
        (initTextView(R.id.title_txt)).setText(R.string.act_gps);
        findViewById(R.id.pointer).setOnClickListener(this);
        //绑定Layout里面的ListView
//      LayoutInflater factory = LayoutInflater.from(this);
//      final View textEntryView = factory.inflate(R.layout.walktour_gps, null); 
        final Spinner s1 = (Spinner)findViewById(R.id.gps_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.gps_type));
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        s1.setAdapter(adapter);        
        gpsswitch =(CheckBox)findViewById(R.id.gps_switch);
        
        (initTextView(R.id.txt_longitude)).setText(R.string.sys_gps_longitude);
        (initTextView(R.id.txt_latitude)).setText(R.string.sys_gps_latitude);
        (initTextView(R.id.txt_altitude)).setText(R.string.sys_gps_altitude);
        (initTextView(R.id.txt_speed)).setText(R.string.sys_gps_speed);
        (initTextView(R.id.txt_bearing)).setText(R.string.sys_gps_bearing);
        (initTextView(R.id.txt_accuracy)).setText(R.string.sys_gps_accuracy);
        (initTextView(R.id.txt_state)).setText(R.string.sys_gps_state);
        (initTextView(R.id.txt_usedState)).setText(R.string.sys_gps_usedState);
        v_longitude=initTextView(R.id.txt_longitude_value);
        v_latitude=initTextView(R.id.txt_latitude_value);
        v_altitude=initTextView(R.id.txt_altitude_value);
        v_speed=initTextView(R.id.txt_speed_value);
        v_bearing=initTextView(R.id.txt_bearing_value);
        v_accuracy=initTextView(R.id.txt_accuracy_value);
        v_state=initTextView(R.id.txt_state_value);
        v_usedState=initTextView(R.id.txt_usedState_value);
        sv_gps_valueLinearLayout=(TableLayout)findViewById(R.id.gps_valueLinearLayout);
        
        LogUtil.w(tag, "---gps is test:" + appModel.isGpsTest() + "--Open:" + gpsInfo.isJobTestGpsOpen());
        gpsswitch.setChecked(gpsInfo.isJobTestGpsOpen());
        showGpsInfo(gpsInfo.isJobTestGpsOpen());
        gpsswitch.setOnCheckedChangeListener(checkListener);
    }
    
    /**
     * 当GPS状态为打开时,获取GPS消息
     */
    private final BroadcastReceiver gpsLocationChangeReceiver = new BroadcastReceiver(){
        public void onReceive(Context context,Intent intent){
            if(intent.getAction().equals(GpsInfo.gpsLocationChanged) && gpsInfo.getLocation() != null){
                Location location = gpsInfo.getLocation();
                v_longitude.setText(String.valueOf(location.getLongitude()));
                v_latitude.setText(String.valueOf(location.getLatitude()));
                v_altitude.setText(UtilsMethod.decFormat.format(location.getAltitude()));
                v_speed.setText(UtilsMethod.decFormat.format(location.getSpeed() * 3.6));  //1km/h=1000m/3600s=10/36 m/s
                v_bearing.setText(String.valueOf(location.getBearing()));
                v_accuracy.setText(UtilsMethod.decFormat.format(location.getAccuracy()));
                v_state.setText(""+gpsInfo.getNumSatelliteList().size());
                v_usedState.setText(""+gpsInfo.getUsedStatellite());
            }/*else if(intent.getAction().equals(GpsInfo.gpsProviderDisabled)){
                * 弹出对话框，提示用户当前GPS/A-GPS没有使能  
                AlertDialog dialog = new AlertDialog.Builder(Gps.this)   
                .setTitle(R.string.sys_gps_disabled)
                .setMessage(R.string.sys_gps_alertToset)   
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {   
                          
                        * 转到设置界面  
                          
                        Intent fireAlarm = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");   
                        fireAlarm.addCategory(Intent.CATEGORY_DEFAULT);   
                        startActivity(fireAlarm);   
                     }
                })
                .setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {   
                    public void onClick(DialogInterface dialog, int whichButton) {   
                        finish();   
                    }   
                })
                .create();   
                dialog.show();
            }*/
        }
    };
    
    /**
     * 显示GPS信息
     * @param isChecked 是否显示GPS信息，true显示，false隐藏
     */
    private void showGpsInfo(boolean isChecked){
        if(isChecked){
            try {
                LogUtil.w(tag, "---come here");
                if(!gpsInfo.isJobTestGpsOpen()){
                    gpsInfo.openGps(Gps.this, WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
                }
                sv_gps_valueLinearLayout.setVisibility(View.VISIBLE);
                v_longitude.setText("N/A");
                v_latitude.setText("N/A");
                v_altitude.setText("N/A");
                v_speed.setText("N/A");
                v_bearing.setText("N/A");
                v_accuracy.setText("N/A");
                v_usedState.setText("0");
                v_state.setText("0");
            } catch (SecurityException ex1) {// 请求使用GPS，应用需要处理抛出的以下异常   
                ex1.printStackTrace();
            } catch (IllegalArgumentException ex2) {
                ex2.printStackTrace();
            } catch (RuntimeException ex3) {
                ex3.printStackTrace();
            }   
        }else{
            try {   
                //使用完毕需要remove
                sv_gps_valueLinearLayout.setVisibility(View.GONE);
                /*if(!gpsInfo.isMonitorGpsOpen())
                    stopService(new Intent(Gps.this,GService.class));*/
                gpsInfo.releaseGps(Gps.this, WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
            } catch (IllegalArgumentException ex) {   
                ex.printStackTrace();
            } 
        }
    }
    private OnCheckedChangeListener checkListener = new OnCheckedChangeListener() {
        
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // TODO Auto-generated method stub
            switch(buttonView.getId()){
            case R.id.gps_switch:
                LogUtil.w(tag,"--isGpsTest:"+appModel.isGpsTest()+"--GpsOpen:"+gpsInfo.isJobTestGpsOpen()
                        +"--GerenalTest:"+appModel.isGerenalTest()+"--indoorTest:"+appModel.isIndoorTest());
                //如果是一般测试，那么转化为GPS测试
                if(appModel.isGerenalTest()){
                    appModel.setGerenalTest(false);
                    appModel.setGpsTest(true);
                }
                //如果正在测试并且不是GPS测试，那么点击GPS开关无效，退出该方法
                if((appModel.isTestJobIsRun() || appModel.isTestStoping()) && appModel.isIndoorTest()){
                    LogUtil.w(tag, "---appModel.isGpsTest="+appModel.isGpsTest() + "--isIndoor:" + appModel.isIndoorTest());
                    gpsswitch.setChecked(false);
                    Toast.makeText(Gps.this,R.string.toast_gerenal_test,Toast.LENGTH_SHORT).show();
                    return;
                }
                showGpsInfo(isChecked);
                Intent stateIntent = new Intent();
                stateIntent.setAction(GpsInfo.gpsOpenStateChanged);
                sendBroadcast(stateIntent);
                break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (mOrientationSensor != null) {
            mSensorManager.registerListener(mOrientationSensorEventListener, mOrientationSensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }
        mStopDrawing = false;
        mHandler.postDelayed(mCompassViewUpdater, 20);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mStopDrawing = true;
        if (mOrientationSensor != null) {
            mSensorManager.unregisterListener(mOrientationSensorEventListener);
        }
    }
    
    @Override 
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(gpsLocationChangeReceiver);
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param v
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pointer:
                Gps.this.finish();
                break;
            
            default:
                break;
        }
        
    }
    
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    private void initResources() {
        mDirection = 0.0f;
        mTargetDirection = 0.0f;
        mInterpolator = new AccelerateInterpolator();
        mStopDrawing = true;
        mChinease = TextUtils.equals(Locale.getDefault().getLanguage(), "zh");

        mCompassView = findViewById(R.id.view_compass);
        mPointer = (CompassView) findViewById(R.id.compass_pointer);
        mDirectionLayout = initLinearLayout(R.id.layout_direction);
        mAngleLayout = initLinearLayout(R.id.layout_angle);

        mPointer.setImageResource(mChinease ? R.drawable.compass_cn : R.drawable.compass);
    }

    @SuppressWarnings("deprecation")
    private void initServices() {
        // sensor manager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }

    private void updateDirection() {
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        mDirectionLayout.removeAllViews();
        mAngleLayout.removeAllViews();

        TextView east = null;
        TextView west = null;
        TextView south = null;
        TextView north = null;
        float direction = normalizeDegree(mTargetDirection * -1.0f);
        if (direction > 22.5f && direction < 157.5f) {
            // east
            east = new TextView(this);
            east.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
            east.setTextColor(Color.WHITE);
            //east.setImageResource(mChinease ? R.drawable.e_cn : R.drawable.e);
            east.setText(getResources().getStringArray(R.array.gps_info_array)[0]);
            east.setLayoutParams(lp);
        } else if (direction > 202.5f && direction < 337.5f) {
            // west
            west = new TextView(this);
            west.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
            west.setTextColor(Color.WHITE);
            //west.setImageResource(mChinease ? R.drawable.w_cn : R.drawable.w);
            west.setText(getResources().getStringArray(R.array.gps_info_array)[1]);
            west.setLayoutParams(lp);
        }

        if (direction > 112.5f && direction < 247.5f) {
            // south
            south = new TextView(this);
            south.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
            south.setTextColor(Color.WHITE);
            //south.setImageResource(mChinease ? R.drawable.s_cn : R.drawable.s);
            south.setText(getResources().getStringArray(R.array.gps_info_array)[2]);
            south.setLayoutParams(lp);
        } else if (direction < 67.5 || direction > 292.5f) {
            // north
            north = new TextView(this);
            north.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
            north.setTextColor(Color.WHITE);
            //north.setImageResource(mChinease ? R.drawable.n_cn : R.drawable.n);
            north.setText(getResources().getStringArray(R.array.gps_info_array)[3]);
            north.setLayoutParams(lp);
        }

        if (mChinease) {
            // east/west should be before north/south
            if (east != null) {
                mDirectionLayout.addView(east);
            }
            if (west != null) {
                mDirectionLayout.addView(west);
            }
            if (south != null) {
                mDirectionLayout.addView(south);
            }
            if (north != null) {
                mDirectionLayout.addView(north);
            }
        } else {
            // north/south should be before east/west
            if (south != null) {
                mDirectionLayout.addView(south);
            }
            if (north != null) {
                mDirectionLayout.addView(north);
            }
            if (east != null) {
                mDirectionLayout.addView(east);
            }
            if (west != null) {
                mDirectionLayout.addView(west);
            }
        }

        int direction2 = (int) direction;
        boolean show = false;
        if (direction2 >= 100) {
            mAngleLayout.addView(getNumberImage(direction2 / 100));
            direction2 %= 100;
            show = true;
        }
        if (direction2 >= 10 || show) {
            mAngleLayout.addView(getNumberImage(direction2 / 10));
            direction2 %= 10;
        }
        mAngleLayout.addView(getNumberImage(direction2));

        TextView degreeImageView = new TextView(this);
        //degreeImageView.setImageResource(R.drawable.degree);
        degreeImageView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
        degreeImageView.setTextColor(Color.WHITE);
        degreeImageView.setText(R.string.str_degree);
        degreeImageView.setLayoutParams(lp);
        mAngleLayout.addView(degreeImageView);
    }

    private TextView getNumberImage(int number) {
        TextView image = new TextView(this);
        image.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
        image.setTextColor(Color.WHITE);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        switch (number) {
            case 0:
                //image.setImageResource(R.drawable.number_0);
                image.setText("0");
                break;
            case 1:
                image.setText("1");
                //image.setImageResource(R.drawable.number_1);
                break;
            case 2:
                image.setText("2");
                //image.setImageResource(R.drawable.number_2);
                break;
            case 3:
                image.setText("3");
                //image.setImageResource(R.drawable.number_3);
                break;
            case 4:
                image.setText("4");
                //image.setImageResource(R.drawable.number_4);
                break;
            case 5:
                image.setText("5");
                //image.setImageResource(R.drawable.number_5);
                break;
            case 6:
                image.setText("6");
                //image.setImageResource(R.drawable.number_6);
                break;
            case 7:
                image.setText("7");
                //image.setImageResource(R.drawable.number_7);
                break;
            case 8:
                image.setText("8");
                //image.setImageResource(R.drawable.number_8);
                break;
            case 9:
                image.setText("9");
                //image.setImageResource(R.drawable.number_9);
                break;
        }
        image.setLayoutParams(lp);
        return image;
    }


    private SensorEventListener mOrientationSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            float direction = event.values[0] * -1.0f;
            mTargetDirection = normalizeDegree(direction);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private float normalizeDegree(float degree) {
        return (degree + 720) % 360;
    }

}
