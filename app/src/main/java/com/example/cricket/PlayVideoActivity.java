package com.example.cricket;

import androidx.appcompat.app.AppCompatActivity;
import ua.polohalo.zoomabletextureview.ZoomableTextureView;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class PlayVideoActivity extends AppCompatActivity  implements TextureView.SurfaceTextureListener, MediaController.MediaPlayerControl, View.OnTouchListener {

//    private VideoView videoView;
//    private TextureView videoView;
//    private MediaPlayer mediaPlayer;
//    private String str_video_url;
//    private MediaController mediaController;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_play_video);
//
//        Date modifiedDate = new Date();
//        String path = Environment.getExternalStorageDirectory().toString()+"/Movies/" + getString(R.string.app_name);
//        Log.d("Files", "Path: " + path);
//        File directory = new File(path);
//        File[] files = directory.listFiles();
//        Log.d("Files", "Size: "+ files.length);
//        for (int i = 0; i < files.length; i++)
//        {
//            long file_time = files[i].lastModified();
//            long current_time = System.currentTimeMillis();
//            Log.d("Files", "FileName:" + files[i].lastModified() + "current time is " + System.currentTimeMillis());
//        }
//        init();
//    }
//
//
//    private void init() {
//        videoView = findViewById(R.id.videoView);
//        str_video_url = getIntent().getStringExtra("video");
//        videoView.setVideoPath(str_video_url);
//        mediaController = new MediaController(this);
//        mediaController.setAnchorView(videoView);
//        videoView.setMediaController(mediaController);
//        mediaController.requestFocus();
//        videoView.start();
//    }

    private float downx = 0;
    private float downy = 0;
    private float upx = 0;
    private float upy = 0;

    private MediaPlayer mediaPlayer;

//    private TextureView textureView;

    private String str_video_url;

    private ZoomableTextureView textureView;

//    private MediaController videoController;
    private Handler handler = new Handler();

    private Button back_button;

    private Button clr_button;

    private Canvas canvas;

    private Paint paint;

    private ImageView imageView;


    @Override
    public void onCreate(Bundle SavedInstanceState) {

        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_play_video);

        // Draw lines setup
        Point point = new Point();
        Display curentDispaly = getWindowManager().getDefaultDisplay();
        curentDispaly.getSize(point);
        int weidth = point.x;
        int height = point.y;

        Bitmap bitmap = Bitmap.createBitmap(weidth , height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);

        imageView = (ImageView)findViewById(R.id.drawlines);
        imageView.setImageBitmap(bitmap);

        imageView.setOnTouchListener(this);

        textureView = findViewById(R.id.videoView);
        back_button = findViewById(R.id.back_btn);
        textureView.setSurfaceTextureListener(this);

        mediaPlayer = new MediaPlayer();
        str_video_url = getIntent().getStringExtra("video");
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideoPlayList();
            }
        });
        clr_button = findViewById(R.id.button4);
        clr_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap.eraseColor(Color.TRANSPARENT);
                canvas.drawBitmap(bitmap,0,0,paint);
            }
        });
//        setSpeedOptions();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Surface surface = new Surface(surfaceTexture);

        try {
            mediaPlayer.setDataSource(str_video_url);

            mediaPlayer.setSurface(surface);
            mediaPlayer.prepare();
//            MediaController videoController = new MediaController(this);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaplayer) {
                    mediaplayer.start();
                    mMediaControllerEnable();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    protected void onPause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return mediaPlayer.getAudioSessionId();
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(PlayVideoActivity.this,VideoPlayerPlaylist.class);
        startActivity(intent);
    }

    private void mMediaControllerEnable(){
        MediaController videoController = new MediaController(this) {
            @Override
            public void hide() {
            }
        };
        videoController.setMediaPlayer(this);//activity which implemented MediaPlayerControl
        videoController.setAnchorView(textureView);
        videoController.setEnabled(true);
        videoController.requestFocus();
        videoController.setEnabled(true);
        videoController.show();
        handler.post(new Runnable() {

            public void run() {
                videoController.setEnabled(true);
                videoController.show();
            }
        });
    }

    private void openVideoPlayList() {
        Intent intent = new Intent(this, VideoPlayerPlaylist.class);
        startActivity(intent);
    }

    // speed values displayed in the spinner
    private String[] getSpeedStrings() {
        return new String[]{"1.0", "1.2", "1.4", "1.6", "1.8", "2.0"};
    }

    private void setSpeedOptions() {
        final Spinner speedOptions = (Spinner)findViewById(R.id.speedOptions);
        String[] speeds = getSpeedStrings();

        ArrayAdapter<String> arrayAdapter =  new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, speeds);
        speedOptions.setAdapter(arrayAdapter);

        // change player playback speed if a speed is selected
        speedOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mediaPlayer != null) {
                    float selectedSpeed = Float.parseFloat(
                            speedOptions.getItemAtPosition(i).toString());

                    changeplayerSpeed(selectedSpeed);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void changeplayerSpeed(float speed) {
        // this checks on API 23 and up
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
            } else {
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
                mediaPlayer.pause();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                downx = event.getX();
                downy = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                upx = event.getX();
                upy = event.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                imageView.invalidate();
                downx = upx;
                downy = upy;
                break;
            case MotionEvent.ACTION_UP:
                upx = event.getX();
                upy = event.getY();
                //canvas.drawLine(downx, downy, upx, upy, paint);
                canvas.drawLine(downx, downy, upx, upy, paint);
                imageView.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;

        }
        return true;
    }
}
