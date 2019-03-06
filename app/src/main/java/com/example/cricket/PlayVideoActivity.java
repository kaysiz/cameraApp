package com.example.cricket;

import androidx.appcompat.app.AppCompatActivity;
import ua.polohalo.zoomabletextureview.ZoomableTextureView;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class PlayVideoActivity extends AppCompatActivity  implements TextureView.SurfaceTextureListener, MediaController.MediaPlayerControl {

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

    private MediaPlayer mediaPlayer;

//    private TextureView textureView;

    private String str_video_url;

    private ZoomableTextureView textureView;

//    private MediaController videoController;
    private Handler handler = new Handler();

    private Button back_button;


    @Override
    public void onCreate(Bundle SavedInstanceState) {

        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_play_video);
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
}
