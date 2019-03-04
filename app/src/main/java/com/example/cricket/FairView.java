package com.example.cricket;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;

import com.jiangdg.usbcamera.UVCCameraHelper;
import com.jiangdg.usbcamera.utils.FileUtils;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.Size;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;
import com.serenegiant.usb.encoder.RecordParams;
import com.serenegiant.usb.widget.CameraViewInterface;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;


public class FairView extends AppCompatActivity implements CameraDialog.CameraDialogParent, CameraViewInterface.Callback{

    private static final String TAG = "Debug";
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT = 1;
    private static final int TIME_DIFFERENCE = 3 * 60 * 1000;
    public View mTextureView;
    public Toolbar mToolbar;

    private UVCCameraHelper mCameraHelper;
    private CameraViewInterface mUVCCameraView;
    private AlertDialog mDialog;
    private ImageButton mRecordImageButton;
    private ImageButton mStopRecord;
    private Button mButton;

    private Chronometer mChronometer;
    private File mVideoFolder;

    private boolean isRequest;
    private boolean isPreview;
    private boolean isRecording = false;

    final Handler handler = new Handler();

    private UVCCameraHelper.OnMyDevConnectListener listener = new UVCCameraHelper.OnMyDevConnectListener() {

        @Override
        public void onAttachDev(UsbDevice device) {
            if (mCameraHelper == null || mCameraHelper.getUsbDeviceCount() == 0) {
                showShortMsg("check no usb camera");
                return;
            }
            // request open permission
            if (!isRequest) {
                isRequest = true;
                if (mCameraHelper != null) {
                    mCameraHelper.requestPermission(0);
                }
            }
        }

        @Override
        public void onDettachDev(UsbDevice device) {
            // close camera
            if (isRequest) {
                isRequest = false;
                mCameraHelper.closeCamera();
                showShortMsg(device.getDeviceName() + " is out");
            }
        }

        @Override
        public void onConnectDev(UsbDevice device, boolean isConnected) {
            if (!isConnected) {
                showShortMsg("fail to connect,please check resolution params");
                isPreview = false;
            } else {
                isPreview = true;
                showShortMsg("connecting");
                // initialize seekbar
                // need to wait UVCCamera initialize over
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Looper.prepare();
                        Looper.loop();
                    }
                }).start();
            }
        }

        @Override
        public void onDisConnectDev(UsbDevice device) {

            showShortMsg("disconnecting");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fair_view);
        ButterKnife.bind(this);
//        mStopRecord = findViewById(R.id.videoOffLineImageButton);
//        mStopRecord.setEnabled(false);
//        mStopRecord.setVisibility(View.INVISIBLE);
//        initView();
//        callAsynchronousTask();

        mTextureView = findViewById(R.id.camera_view);
        mToolbar = findViewById(R.id.toolbar);

        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideoPlayer();
            }
        });

        // step.1 initialize UVCCameraHelper
        mUVCCameraView = (CameraViewInterface) mTextureView;
        mUVCCameraView.setCallback(this);
        mCameraHelper = UVCCameraHelper.getInstance();
        mCameraHelper.setDefaultFrameFormat(UVCCameraHelper.FRAME_FORMAT_YUYV);
        mCameraHelper.initUSBMonitor(this, mUVCCameraView, listener);


        mCameraHelper.setOnPreviewFrameListener(new AbstractUVCCameraHandler.OnPreViewResultListener() {
            @Override
            public void onPreviewResult(byte[] nv21Yuv) {

            }
        });

        mChronometer = findViewById(R.id.chronometer);
        mTextureView = findViewById(R.id.textureView);
        mRecordImageButton = findViewById(R.id.videoOnlineImageButton);
        mRecordImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createVideoFolder();
                if (isRecording) {
                    stopRecording();
                } else {
                    startRecording();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isRecording = true;
                mRecordImageButton.setImageResource(R.mipmap.btn_video_busy);
                Toast.makeText(this, "Permissions succesfully granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "App needs to save video to run!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // step.2 register USB event broadcast
        if (mCameraHelper != null) {
            mCameraHelper.registerUSB();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // step.3 unregister USB event broadcast
        if (mCameraHelper != null) {
            mCameraHelper.unregisterUSB();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtils.releaseFile();
        // step.4 release uvc camera resources
        if (mCameraHelper != null) {
            mCameraHelper.release();
        }
    }

    private void showShortMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public USBMonitor getUSBMonitor() {
        return mCameraHelper.getUSBMonitor();
    }

    @Override
    public void onDialogResult(boolean canceled) {
        if (canceled) {
            showShortMsg("Canceled");
        }
    }

    public boolean isCameraOpened() {
        return mCameraHelper.isCameraOpened();
    }

    @Override
    public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
        if (!isPreview && mCameraHelper.isCameraOpened()) {
            mCameraHelper.startPreview(mUVCCameraView);
            isPreview = true;
        }
    }

    @Override
    public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {

    }

    @Override
    public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
        if (isPreview && mCameraHelper.isCameraOpened()) {
            mCameraHelper.stopPreview();
            isPreview = false;
        }
    }

    private void startRecording() {
        if (mCameraHelper == null || !mCameraHelper.isCameraOpened()) {
            showShortMsg("sorry,camera open failed");
        }
        else {
            handler.post(runnableCode);
        }
    }

    private void createVideoFolder() {
        File movieFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        mVideoFolder = new File(movieFile, getString(R.string.app_name));
        if (!mVideoFolder.exists()) {
            mVideoFolder.mkdirs();
        }
    }

    private void stopRecording() {
        FileUtils.releaseFile();
        mCameraHelper.stopPusher();
        mButton.setEnabled(true);
        mChronometer.stop();
        mChronometer.setVisibility(View.INVISIBLE);
        mRecordImageButton.setImageResource(R.mipmap.btn_video_online);
        showShortMsg("stop record...");
        isRecording = false;
        handler.removeCallbacks(runnableCode);
    }

    private void openVideoPlayer() {
        Intent intent = new Intent(this, VideoPlayerPlaylist.class);
        startActivity(intent);
    }

    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            if (!mCameraHelper.isPushing()) {
                String videoPath = UVCCameraHelper.ROOT_PATH + "Movies/Cricket/" + System.currentTimeMillis();
                mButton.setEnabled(false);
                isRecording = true;
                mRecordImageButton.setImageResource(R.mipmap.btn_video_busy);
                mChronometer.setBase(SystemClock.elapsedRealtime());
                mChronometer.setVisibility(View.VISIBLE);
                mChronometer.start();
                // if you want to record,please create RecordParams like this
                RecordParams params = new RecordParams();
                params.setRecordPath(videoPath);
                params.setRecordDuration(0);
                mCameraHelper.startPusher(params, new AbstractUVCCameraHandler.OnEncodeResultListener() {
                    @Override
                    public void onEncodeResult(byte[] data, int offset, int length, long timestamp, int type) {
                        // type = 1,h264 video stream
                        if (type == 1) {
                            FileUtils.putFileStream(data, offset, length);
                        }
                    }

                    @Override
                    public void onRecordResult(String videoPath) {
                        Log.i(TAG,"videoPath = "+videoPath);
                    }
                });
                // if you only want to push stream,please call like this
                // mCameraHelper.startPusher(listener);
                clean_up();
                showShortMsg("start recording...");

                // Tell MediaStore of the new file
                MediaScannerConnection.scanFile(FairView.this,
                        new String[] { videoPath+".mp4" }, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
            } else {
                FileUtils.releaseFile();
                mCameraHelper.stopPusher();
                mButton.setEnabled(true);
                mChronometer.stop();
                mChronometer.setVisibility(View.INVISIBLE);
                mRecordImageButton.setImageResource(R.mipmap.btn_video_online);
                showShortMsg("stop recording...");
                handler.removeCallbacks(runnableCode);
                handler.post(runnableCode);
            }
            handler.postDelayed(this, 60000);
        }
    };

    private void callAsynchronousTask() {
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if (!mCameraHelper.isPushing()) {
                            String videoPath = UVCCameraHelper.ROOT_PATH + "Movies/Cricket/" + System.currentTimeMillis();
                            mButton.setEnabled(false);
                            isRecording = true;
                            mRecordImageButton.setImageResource(R.mipmap.btn_video_busy);
                            mChronometer.setBase(SystemClock.elapsedRealtime());
                            mChronometer.setVisibility(View.VISIBLE);
                            mChronometer.start();
                            // if you want to record,please create RecordParams like this
                            RecordParams params = new RecordParams();
                            params.setRecordPath(videoPath);
                            params.setRecordDuration(0);
                            mCameraHelper.startPusher(params, new AbstractUVCCameraHandler.OnEncodeResultListener() {
                                @Override
                                public void onEncodeResult(byte[] data, int offset, int length, long timestamp, int type) {
                                    // type = 1,h264 video stream
                                    if (type == 1) {
                                        FileUtils.putFileStream(data, offset, length);
                                    }
                                }

                                @Override
                                public void onRecordResult(String videoPath) {
                                    Log.i(TAG,"videoPath = "+videoPath);
                                }
                            });
                            // if you only want to push stream,please call like this
                            // mCameraHelper.startPusher(listener);
//                            callAsynchronousTask();
                            showShortMsg("start recording...");
                        } else {
                            FileUtils.releaseFile();
                            mCameraHelper.stopPusher();
                            mButton.setEnabled(true);
                            mChronometer.stop();
                            mChronometer.setVisibility(View.INVISIBLE);
                            mRecordImageButton.setImageResource(R.mipmap.btn_video_online);
                            showShortMsg("stop recording...");
                            handler.removeCallbacks(null);
//                stopRecording();
//                callAsynchronousTask();
                        }
//                        handler.postDelayed(this, 10000);
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(doAsynchronousTask, 0, 10000); //execute in every 50000 ms
    }

    private void clean_up() {

        String path = Environment.getExternalStorageDirectory().toString()+"/Movies/" + getString(R.string.app_name);
        long longAgo = System.currentTimeMillis() - TIME_DIFFERENCE;
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            long file_time = files[i].lastModified();
            if (file_time < longAgo) {
                files[i].delete();
            }
        }
    }

}
