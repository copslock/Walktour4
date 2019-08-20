package com.walktour.gui.newmap2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.walktour.gui.R;
import com.walktour.gui.newmap2.ui.MovieRecorderView;
/**
 * @date on 2018/9/5
 * @describe 录制视频
 * @author jinfeng.xie
 * @version 1.0.0
 *
 */
public class TagPhotoActivity extends AppCompatActivity {

    private MovieRecorderView mRecorderView;//视频录制控件
    private Button mShootBtn;//视频开始录制按钮
    private boolean isFinish = true;
    private boolean success = false;//防止录制完成后出现多次跳转事件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_photo);
        mRecorderView = (MovieRecorderView) findViewById(R.id.movieRecorderView);
        mShootBtn = (Button) findViewById(R.id.shoot_button);

        //用户长按事件监听
        mShootBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {//用户按下拍摄按钮
                    mShootBtn.setBackgroundResource(R.drawable.bg_movie_add_shoot_select);
                    mRecorderView.record(new MovieRecorderView.OnRecordFinishListener() {

                        @Override
                        public void onRecordFinish() {
                            if(!success&&mRecorderView.getTimeCount()<10){//判断用户按下时间是否大于10秒
                                success = true;
                                handler.sendEmptyMessage(1);
                            }
                        }
                    });
                } else if (event.getAction() == MotionEvent.ACTION_UP) {//用户抬起拍摄按钮
                    mShootBtn.setBackgroundResource(R.drawable.bg_movie_add_shoot);
//                    if (mRecorderView.getTimeCount() > 3){//判断用户按下时间是否大于3秒
                        if(!success){
                            success = true;
                            handler.sendEmptyMessage(1);
                        }
//                    } else {
//                        success = false;
//                        if (mRecorderView.getmVecordFile() != null)
//                            mRecorderView.getmVecordFile().delete();//删除录制的过短视频
//                        mRecorderView.stop();//停止录制
//                        Toast.makeText(TagPhotoActivity.this, "视频录制时间太短", Toast.LENGTH_SHORT).show();
//                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        isFinish = true;
        if (mRecorderView.getmVecordFile() != null)
            mRecorderView.getmVecordFile().delete();//视频使用后删除
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isFinish = false;
        success = false;
        mRecorderView.stop();//停止录制
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(success){
                finishActivity();
            }
        }
    };

    //视频录制结束后，跳转的函数
    private void finishActivity() {
        if (isFinish) {
            mRecorderView.stop();
            Intent intent=new Intent();
            intent.putExtra("data","save radio success\n"+mRecorderView.getmVecordFile().getAbsolutePath());
            setResult(RESULT_OK,intent);
            finish();
        }
        success = false;
    }

    /**
     * 录制完成回调
     */
    public interface OnShootCompletionListener {
        public void OnShootSuccess(String path, int second);
        public void OnShootFailure();
    }
}
