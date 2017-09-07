package com.hcw2175.library.qrscan;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hcw2175.library.qrscan.camera.CameraManager;
import com.hcw2175.library.qrscan.decode.DecodeMatcher;
import com.hcw2175.library.qrscan.listener.ActivityLiveTaskTimer;

import java.io.IOException;

/**
 * 二维码扫码Activity
 *
 * @author hucw
 * @version 0.0.3
 */
public class QRScanActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {
    private static final String TAG = "QRScanActivity";

    /** 扫码成功提示音音量，值：{@value} **/
    private static final float BEEP_VOLUME = 0.50f;
    /** 震动提示时长，值：{@value} **/
    private static final long VIBRATE_DURATION = 200L;

    private QRScanActivityHandler mHandler;
    private ActivityLiveTaskTimer mInactivityTimer;
    private MediaPlayer mMediaPlayer;
    private DecodeMatcher mDecodeMatcher = null;

    // UI Fields
    private RelativeLayout mContainer = null;
    private RelativeLayout mCropLayout = null;
    private ImageView mBtnFlashLight = null;

    private boolean hasSurface = false;
    private boolean playBeep = true;
    private boolean vibrate = true;
    private boolean isFlashLightOpen = false;
    
    private int x = 0;
    private int y = 0;
    private int cropWidth = 0;
    private int cropHeight = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrscan_act);

        // 界面控件初始化
        mContainer = (RelativeLayout) findViewById(R.id.capture_containter);
        mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);
        mBtnFlashLight = (ImageView) findViewById(R.id.btn_flash_light);

        // 扫描动画初始化
        initScannerAnimation();

        // 初始化 CameraManager
        CameraManager.init(this);

        //
        mInactivityTimer = new ActivityLiveTaskTimer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 初始化相机取景器
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        // 初始化铃声播放服务
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        playBeep = audioService.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
        initBeepSound();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mHandler != null) {
            mHandler.quitSynchronously();
            mHandler = null;
        }
        CameraManager.getInstance().closeDriver();
    }

    @Override
    protected void onDestroy() {
        mInactivityTimer.shutdown();
        super.onDestroy();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId()) {
            case :

                break;
        }*/
    }

    /**
     * 扫码结果解析成功回调处理
     *
     * @param result 扫描结果
     */
    public void onDecodeSuccess(String result) {
        mInactivityTimer.init();

        if (null != result && result.length() == 12) {
            playBeepSoundAndVibrate();
            Log.d(TAG, "二维码/条形码 扫描结果: " + result);

            Intent intent = new Intent();
            intent.putExtra(QRCodeConstants.SCAR_RESULT, result);
            this.setResult(RESULT_OK, intent);
            finish();
        } else {
            // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
            Toast.makeText(this, "扫描结果不合法，请重新扫描", Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessage(R.id.restart_preview);
        }
    }


    //----------------------------------------------------------------------------------------------扫描成功之后的振动与声音提示 start

    //==============================================================================================扫描成功之后的振动与声音提示 end


    // ==========================================================================
    // private methods ==========================================================

    /**
     * 初始化相机
     *
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.getInstance().openDriver(surfaceHolder);

            Point point = CameraManager.getInstance().getCameraResolution();
            int width = point.y;
            int height = point.x;
            this.x = mCropLayout.getLeft() * width / mContainer.getWidth();
            this.y = mCropLayout.getTop() * height / mContainer.getHeight();
            this.cropWidth = mCropLayout.getWidth() * width / mContainer.getWidth();
            this.cropHeight = mCropLayout.getHeight() * height / mContainer.getHeight();

            if (mHandler == null) {
                mHandler = new QRScanActivityHandler(QRScanActivity.this);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化扫描框动画
     */
    private void initScannerAnimation() {
        ImageView scanView = (ImageView) findViewById(R.id.capture_scan_line);
        if (null != scanView) {
            ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f);
            animation.setRepeatCount(-1);
            animation.setRepeatMode(Animation.RESTART);
            animation.setInterpolator(new LinearInterpolator());
            animation.setDuration(1200);
            scanView.startAnimation(animation);
        }
    }

    /**
     * 播放提示音
     */
    private void initBeepSound() {
        if (playBeep && mMediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mMediaPlayer) {
                    mMediaPlayer.seekTo(0);
                }
            });

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mMediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mMediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mMediaPlayer.prepare();
            } catch (IOException e) {
                mMediaPlayer = null;
            }
        }
    }


    /**
     * 播放提示音并震动
     */
    private void playBeepSoundAndVibrate() {
        if (playBeep && mMediaPlayer != null) {
            mMediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    // ==================================================================
    // setter/getter ====================================================
    public Handler getQRScanActivityHandler() {
        return mHandler;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCropWidth() {
        return cropWidth;
    }

    public int getCropHeight() {
        return cropHeight;
    }

    public DecodeMatcher getDecodeMatcher() {
        return mDecodeMatcher;
    }
    public void setDecodeMatcher(DecodeMatcher mDecodeMatcher) {
        this.mDecodeMatcher = mDecodeMatcher;
    }
}
